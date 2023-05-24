package com.zoom2u.utility;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.TextView;

import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.userlatlong.GPSTracker;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Functional_Utility {
	
	 private Context _context;
	 String[] monthName = {"0","Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
	 public static String mCurrentPhotoPath;
	 
	 private static final String GEOCODER_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?";
	 
	    public Functional_Utility(Context context){
	        this._context = context;
	    }
	   
	    /**
	     * Checking for all possible internet providers
	     * **/
	    public boolean isConnectingToInternet(){
	        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
	          if (connectivity != null)
	          {
	              NetworkInfo[] info = connectivity.getAllNetworkInfo();
	              if (info != null)
	                  for (int i = 0; i < info.length; i++)
	                      if (info[i].getState() == NetworkInfo.State.CONNECTED)
	                      {
	                          return true;
	                      }
	  
	          }
	          return false;
	    }
	    
	    public static int round(double a) {
	        if (a != 0x1.fffffffffffffp-2) // greatest double value less than 0.5
	            return (int)Math.floor(a + 0.5d);
	        else
	            return 0;
	    }

	//************** Set price up to 2 decimal points for booking list and detail ***********
	public static String returnCourierPrice(double courierPriceFromServer) {
		String listCourierPriceToShowOn;
		String text = Double.toString(Math.abs(courierPriceFromServer));
		int integerPlaces = text.indexOf('.');
		int decimalPlaces = text.length() - integerPlaces - 1;

		String decimalValue;
		if (decimalPlaces >= 2) {
			decimalValue = getValueOfAfterDecimalPoint(courierPriceFromServer, 2);
			if (decimalValue.equals("00")) {
				listCourierPriceToShowOn = new Double(courierPriceFromServer).toString();
				listCourierPriceToShowOn = listCourierPriceToShowOn.substring(0, listCourierPriceToShowOn.indexOf('.'));
			} else if (decimalValue.charAt(1) == '0') {
				decimalValue = getValueOfAfterDecimalPoint(courierPriceFromServer, 1);
				listCourierPriceToShowOn = new Double(courierPriceFromServer).toString();
				listCourierPriceToShowOn = listCourierPriceToShowOn.substring(0, listCourierPriceToShowOn.indexOf('.'));
				listCourierPriceToShowOn = listCourierPriceToShowOn +"."+decimalValue;
			} else {
				listCourierPriceToShowOn = new Double(courierPriceFromServer).toString();
				listCourierPriceToShowOn = listCourierPriceToShowOn.substring(0, listCourierPriceToShowOn.indexOf('.'));
				listCourierPriceToShowOn = listCourierPriceToShowOn +"."+decimalValue;
			}
		} else {
			decimalValue = getValueOfAfterDecimalPoint(courierPriceFromServer, 1);
			if (decimalValue.equals("0")) {
				listCourierPriceToShowOn = new Double(courierPriceFromServer).toString();
				listCourierPriceToShowOn = listCourierPriceToShowOn.substring(0, listCourierPriceToShowOn.indexOf('.'));
			} else {
				listCourierPriceToShowOn = new Double(courierPriceFromServer).toString();
				listCourierPriceToShowOn = listCourierPriceToShowOn.substring(0, listCourierPriceToShowOn.indexOf('.'));
				listCourierPriceToShowOn = listCourierPriceToShowOn +"."+decimalValue;
			}
		}

		return listCourierPriceToShowOn;
	}

	public static String getValueOfAfterDecimalPoint(double courierPriceFromServer, int afterDecimalPointValue) {
		String decimalValue = String.format("%."+afterDecimalPointValue+"f", courierPriceFromServer);
		String[] decimalValueArray = decimalValue.split("\\.");
		decimalValue = decimalValueArray[1];
		return decimalValue;
	}

	/************ Remove location timer before recall ************/
    public static void removeLocationTimer(){
        if(ServiceForSendLatLong.locationTimerTask != null){
            ServiceForSendLatLong.locationTimerTask.cancel();
            ServiceForSendLatLong.locationTimerTask = null;
        }
    }

 //**************  Scale image to actual ratio ***************//
	    public static Bitmap scaleToActualAspectRatio(Bitmap bitmap, float maxHeight, float maxWidth) {
			 try {
				if (bitmap != null) {
					 	try {
							float actualHeight = bitmap.getHeight();
							float actualWidth = bitmap.getWidth();
//							float maxHeight = 1024.0f;
//							float maxWidth = 1024.0f;
							float imgRatio = actualWidth/actualHeight;
							float maxRatio = maxWidth/maxHeight;
							if (actualHeight > maxHeight || actualWidth > maxWidth){
							    if(imgRatio < maxRatio){
							        //adjust width according to maxHeight
							        imgRatio = maxHeight / actualHeight;
							        actualWidth = imgRatio * actualWidth;
							        actualHeight = maxHeight;
							    }else if(imgRatio > maxRatio){
							        //adjust height according to maxWidth
							        imgRatio = maxWidth / actualWidth;
							        actualHeight = imgRatio * actualHeight;
							        actualWidth = maxWidth;
							    }else{
							        actualHeight = maxHeight;
							        actualWidth = maxWidth;
							    }
							}

							bitmap = Bitmap.createScaledBitmap(bitmap, (int) actualWidth, (int) actualHeight, true);

					 	} catch (Exception e) {
							e.printStackTrace();
						}
				 }
			} catch (Exception e) {
				e.printStackTrace();
			}
			 return bitmap;
		 }

	    //************ For getting Latitude and longitude from Google Geocoder API
	    public static String getAddressDetailGeoCoder(String sourceAddress) {
	    	String latLongStr = "";
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
			policy = null;
			try {
				if(sourceAddress.equals(""))
					sourceAddress = " ";
			} catch (Exception e1) {
				e1.printStackTrace();
				sourceAddress = " ";
			}

			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {

				StringBuilder sb = new StringBuilder(GEOCODER_API_BASE);
				sb.append("&address=" + URLEncoder.encode(sourceAddress, "utf8"));
				sb.append("&components=country:au");
				sb.append("&sensor=false");

				URL url = new URL(sb.toString());
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				InputStreamReader in = new InputStreamReader(conn.getInputStream());

				// Load the results into a StringBuilder
				int read;
				char[] buff = new char[1024];
				while ((read = in.read(buff)) != -1) {
					jsonResults.append(buff, 0, read);
				}
				sb = null;
				in = null;
				buff = null;
			} catch (MalformedURLException e) {
				Log.e("", "Error processing Places API URL", e);
			} catch (IOException e) {
				Log.e("", "Error connecting to Places API", e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				if(jsonObj.getString("status").equals("OK")){
					JSONArray resultsJsonArray = jsonObj.getJSONArray("results");
			        double latStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
					double longStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

					latLongStr = latStr+","+longStr;
					resultsJsonArray = null;
				}
				jsonObj = null;
			} catch (Exception e) {
				Log.e("", "Cannot process JSON results", e);
				latLongStr = "";
			}
			Log.d("", " results  ========   "+latLongStr);
			return latLongStr;
		}

	//************  For Getting Address detail from Lat-long using Google GeoCoder API
	public static String getAddressFromDriverLatLong(Context appContext,String latitudeStr, String longitudeStr) {
		String formatted_address = "";
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
		StrictMode.setThreadPolicy(policy);
		try {
			if (latitudeStr.equals(""))
				latitudeStr = " ";
		} catch (Exception e1) {
			e1.printStackTrace();
			latitudeStr = " ";
			longitudeStr = " ";
		}

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {

			StringBuilder sb = new StringBuilder(GEOCODER_API_BASE);
			sb.append("&latlng=" + URLEncoder.encode(latitudeStr, "utf8") + "," + URLEncoder.encode(longitudeStr, "utf8"));
			sb.append("&key	=" + URLEncoder.encode(appContext.getString(R.string.places_api_key), "utf8"));

			URL url = new URL(sb.toString());
			System.out.println("URL: " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e("Error getting Address", "Error processing Places API URL", e);
		} catch (IOException e) {
			Log.e("Error getting Address", "Error connecting to Places API", e);
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			if (jsonObj.getString("status").equals("OK")) {
				JSONArray resultsJsonArray = jsonObj.getJSONArray("results");

				// Extracting formatted address, if available
				if (!resultsJsonArray.getJSONObject(0).isNull("formatted_address")) {
					formatted_address = resultsJsonArray.getJSONObject(0).getString("formatted_address");
				}
			}else formatted_address="Latitude:"+latitudeStr+",Longitude:"+longitudeStr;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formatted_address;
	}


				/**************  Convert date time for delivery speed ETA	************/
	@SuppressLint("SimpleDateFormat")
	public String getDateTimeFromDeviceForDeliveryETA(String serverDateTimeValue) {
		String dateTimeReturn = null;
		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm a", Locale.ENGLISH);
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	/**************  Convert date time for delivery speed ETA	************/
	@SuppressLint("SimpleDateFormat")
	public String getDateTimeFromDeviceForQuoteRequestETA(String serverDateTimeValue) {
		String dateTimeReturn = null;
		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH);
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	/**************  Convert date time for delivery speed ETA	************/
	@SuppressLint("SimpleDateFormat")
	public synchronized String returnDateToShowBidActiveFor(String serverDateTimeValue) {
		String dateTimeReturn = null;
		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("dd-MMM-yyyy | hh:mm a", Locale.ENGLISH);
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	/**************  Convert date time for delivery speed ETA	************/
	@SuppressLint("SimpleDateFormat")
	public synchronized String returnDateToShowBookingStatusRequest(String serverDateTimeValue) {
		String dateTimeReturn = "";
		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.ENGLISH);
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
					dateFormatter.setTimeZone(TimeZone.getTimeZone("IST"));
					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	    /******************  check for Late delivery ***************/
		public boolean checkIsDeliveryDropLate(String dropDateTime){
			boolean ischeckDeliveryDropLate = false;
			try{
				if(!dropDateTime.equals("")){
		            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		            utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
		            Calendar now = Calendar.getInstance();
		            String currentTime = utcFormat.format(now.getTime());
		            Date dropDateTimeFromServer = new Date();
		            dropDateTimeFromServer = utcFormat.parse(dropDateTime);
		            Date currentDateTime = utcFormat.parse(currentTime);
		            if(currentDateTime.after(dropDateTimeFromServer))
		            	ischeckDeliveryDropLate = true;
		            else
		              	ischeckDeliveryDropLate = false;

		            utcFormat = null;
		            now = null;
		            dropDateTimeFromServer = null;
		            currentDateTime = null;
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return ischeckDeliveryDropLate;
		}

	/******************  check for Late delivery for tried to deliver ***************/
	public boolean checkIsFirstDropAttemptWasLate(String dropDateTime, String firstDropAttemptDate){
		boolean ischeckDeliveryDropLate = false;
		try{
			if(!dropDateTime.equals("") && !firstDropAttemptDate.equals("")){
				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
				Date dropDateTimeFromServer = new Date();
				dropDateTimeFromServer = utcFormat.parse(dropDateTime);
				Date firstDropAttemptDateFromServer = new Date();
				firstDropAttemptDateFromServer = utcFormat.parse(firstDropAttemptDate);
				if(firstDropAttemptDateFromServer.after(dropDateTimeFromServer))
					ischeckDeliveryDropLate = true;
				else
					ischeckDeliveryDropLate = false;

				utcFormat = null;
				dropDateTimeFromServer = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ischeckDeliveryDropLate;
	}

	/******************  check for Late delivery ***************/
	public boolean checkIsDeliveryDropLateFromBarcodeScanner(String dropDateTime){
		boolean ischeckDeliveryDropLate = false;
		try{
			if(!dropDateTime.equals("")){
				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
				utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
				Calendar now = Calendar.getInstance();
				String currentTime = utcFormat.format(now.getTime());
				Date dropDateTimeFromServer = new Date();
				dropDateTime = dropDateTime.replace("Z", "GMT+00:00");
				dropDateTimeFromServer = utcFormat.parse(dropDateTime);
				Date currentDateTime = utcFormat.parse(currentTime);
				if(currentDateTime.after(dropDateTimeFromServer))
					ischeckDeliveryDropLate = true;
				else
					ischeckDeliveryDropLate = false;

				utcFormat = null;
				now = null;
				dropDateTimeFromServer = null;
				currentDateTime = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ischeckDeliveryDropLate;
	}

	/******************  check for Late delivery ***************/
	public boolean checkIFirstAttemptWasLateFromBarcodeScanner(String dropDateTime, String firstDropAttemptDate){
		boolean ischeckDeliveryDropLate = false;
		try{
			if(!dropDateTime.equals("")){
				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
				utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
				Date dropDateTimeFromServer = new Date();
				dropDateTime = dropDateTime.replace("Z", "GMT+00:00");
				dropDateTimeFromServer = utcFormat.parse(dropDateTime);
				Date firstDropAttemptDateTime = new Date();
				firstDropAttemptDate = firstDropAttemptDate.replace("Z", "GMT+00:00");
				firstDropAttemptDateTime = utcFormat.parse(firstDropAttemptDate);
				if(firstDropAttemptDateTime.after(dropDateTimeFromServer))
					ischeckDeliveryDropLate = true;
				else
					ischeckDeliveryDropLate = false;

				utcFormat = null;
				dropDateTimeFromServer = null;
				firstDropAttemptDateTime = null;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return ischeckDeliveryDropLate;
	}

	/******************  check for Late delivery for tried to deliver from barcode ***************/
//	public boolean checkIsFirstDropAttemptWasLateFromBarcodeView(String dropDateTime, String firstDropAttemptDate){
//		boolean ischeckDeliveryDropLate = false;
//		try{
//			if(!dropDateTime.equals("") && !firstDropAttemptDate.equals("")){
//				SimpleDateFormat utcFormatForFirstAttempt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
//				utcFormatForFirstAttempt.setTimeZone(TimeZone.getTimeZone("IST"));
//				Date firstDropAttemptDateFromServer = new Date();
//				firstDropAttemptDateFromServer = utcFormatForFirstAttempt.parse(firstDropAttemptDate);
//				SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
//				utcFormat.setTimeZone(TimeZone.getTimeZone("IST"));
//				Date dropDateTimeFromServer = new Date();
//				dropDateTime = dropDateTime.replace("Z", "GMT+00:00");
//				dropDateTimeFromServer = utcFormat.parse(dropDateTime);
//				if(firstDropAttemptDateFromServer.after(dropDateTimeFromServer))
//					ischeckDeliveryDropLate = true;
//				else
//					ischeckDeliveryDropLate = false;
//
//				utcFormatForFirstAttempt = null;
//				utcFormat = null;
//				dropDateTimeFromServer = null;
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return ischeckDeliveryDropLate;
//	}

	    // Find distance between two locations
	    public String distanceBetweenTwoPosition(String srcLocationLat, String srcLocationLong, String destLocationLat, String destLocationLong) {
			String distance = "";

			try {
				Location mylocation = new Location("");
				Location dest_location = new Location("");

				if (!destLocationLat.equals("")	|| destLocationLong.equals(""))
				{
					dest_location.setLatitude(Double.parseDouble(destLocationLat));
					dest_location.setLongitude(Double.parseDouble(destLocationLong));

				} else {
					// set default latitude and longitude
					dest_location.setLatitude(0.0);
					dest_location.setLongitude(0.0);
				}

				if (!srcLocationLat.equals("0.0")	|| srcLocationLong.equals("0.0"))
				{
					mylocation.setLatitude(Double.parseDouble(srcLocationLat));
					mylocation.setLongitude(Double.parseDouble(srcLocationLong));

				} else {
					// set default latitude and longitude
					mylocation.setLatitude(0.0);
					mylocation.setLongitude(0.0);
				}
					Double getdistanceValue = (double) mylocation.distanceTo(dest_location);
					mylocation = null;
					dest_location = null;
					getdistanceValue = getdistanceValue / 1000;

					distance = new DecimalFormat("##.#").format(getdistanceValue);
			} catch (NumberFormatException e) {

				e.printStackTrace();
				
			}
	    	
	    	return distance;
		}
	    
	    @SuppressLint("SimpleDateFormat")
		public String getscheduleDateFromServer(String serverDateStr) {
		    
	    	try {
				if(!serverDateStr.equals("")){
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				    
				    Date convertedDate = new Date();
				    try {
				    	convertedDate = converter.parse(serverDateStr);
				    	Calendar cal = Calendar.getInstance();
				    	TimeZone tz = cal.getTimeZone();

					    converter.setTimeZone(tz);
					    
						return converter.format(convertedDate);

				    } catch (Exception e) {
						e.printStackTrace();
					}
				
				    return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return null;
		}
	    
	    @SuppressLint("SimpleDateFormat")
		public String getPickerDateTimeFromDevice(Date serverDateTimeValue) {
		    
	    	try {
				if(serverDateTimeValue != null){
					//creating DateFormat for converting time from local timezone to GMT
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				    //getting GMT timezone, you can get any timezone e.g. UTC
				    converter.setTimeZone(TimeZone.getTimeZone("IST"));
				
				return converter.format(serverDateTimeValue);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return null;
		}

	    @SuppressLint("SimpleDateFormat")
		public String getDateTimeFromServerone(String serverDateTimeValue) {
			String dateTimeReturn = null;

			try {
				if(!serverDateTimeValue.equals("")){
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				    //getting GMT timezone, you can get any timezone e.g. UTC
				    converter.setTimeZone(TimeZone.getTimeZone("IST"));
				    Date convertedDate = new Date();
				    try {
				    	convertedDate = converter.parse(serverDateTimeValue);
					    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

						return dateFormatter.format(convertedDate);
				    } catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			    return dateTimeReturn;
		}
		public String getDateTimeFromServer(String serverDateTimeValue) {
			String dateTimeReturn = null;

			try {
				if(!serverDateTimeValue.equals("")){
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				    //getting GMT timezone, you can get any timezone e.g. UTC
				    converter.setTimeZone(TimeZone.getTimeZone("IST"));
				    Date convertedDate = new Date();
				    try {
				    	convertedDate = converter.parse(serverDateTimeValue);
					    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

						return dateFormatter.format(convertedDate);
				    } catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			    return dateTimeReturn;
		}

	@SuppressLint("SimpleDateFormat")
	public Date getDateTimeFromServer1(String serverDateTimeValue) {
		String dateTimeReturn = null;
		Date convertedDate = new Date();
		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));

				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

					return  convertedDate;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateFromServer(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm a");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}
	@SuppressLint("SimpleDateFormat")
	public String getDateServer(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public String getTimeFromServer(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}
	@SuppressLint("SimpleDateFormat")
	public String getDateServer1(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public String getOnlyTimeFromServer(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mm a");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateTimeFromServerTo_BookingStatusRequestUI(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("h:mm a");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public boolean greaterThan24Hour_ForDeliveryRun(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);

					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.HOUR, 24);

					converter.setTimeZone(TimeZone.getTimeZone("UTC"));
					String currentDateTimeStr = converter.format(cal.getTime());

					SimpleDateFormat converter1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					Date currentDateTime = converter1.parse(currentDateTimeStr);

					if (convertedDate.after(currentDateTime))
						return true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDayOfWeekFromServer(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE | dd-MMM-yyyy");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public String serverDateTimeToDevice_TeamDetails(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy h:mm a");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}

	@SuppressLint("SimpleDateFormat")
	public String getDateTimeFromServerForCourierRouteForDHL(String serverDateTimeValue) {
		String dateTimeReturn = null;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				//getting GMT timezone, you can get any timezone e.g. UTC
				converter.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date convertedDate = new Date();
				try {
					convertedDate = converter.parse(serverDateTimeValue);
					SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");

					return dateFormatter.format(convertedDate);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateTimeReturn;
	}
	    
	    @SuppressLint("SimpleDateFormat")
		public String getDateTimeFromServerInDayFormat(String serverDateTimeValue) {
			String dateTimeReturn = null;
	    	
			try {
				if(!serverDateTimeValue.equals("")){
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				    //getting GMT timezone, you can get any timezone e.g. UTC
				    converter.setTimeZone(TimeZone.getTimeZone("IST"));
				    Date convertedDate = new Date();
				    try {
				    	convertedDate = converter.parse(serverDateTimeValue);
					    SimpleDateFormat dateFormatter = new SimpleDateFormat("EEE, dd-MMM-yyyy hh:mm a");
						
						return dateFormatter.format(convertedDate);
				    } catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			    return dateTimeReturn;
		}
	    
	    @SuppressLint("SimpleDateFormat")
		public String getPickOrDropDateTimeFromServer(String serverDateTimeValue) {
	    	
			try {
				if(!serverDateTimeValue.equals("")){
					SimpleDateFormat converter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
				    Date convertedDate = new Date();
				    try {
				    	convertedDate = converter.parse(serverDateTimeValue);
					    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
						
						return dateFormatter.format(convertedDate);
				    } catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			    return null;
		}
	    
	    
	    public int indexOfString(String searchString, String[] domain)
		{
		     for(int i = 0; i < domain.length; i++)
		     {
		        if(searchString.equals(domain[i]))
		           return i;
		     }
		     return -1;
		}
	    
	    
	    // **********   Delete Captured Images from device local storage
	    public void deleteLatest() {
			if (!LoginZoomToU.isImgFromInternalStorage) {
				File f = new File(mCurrentPhotoPath);
				f.delete();
			}
	    }
//	    public void deleteLatest() {
//	        File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera" );
//	        //Log.i("Log", "file name in delete folder :  "+f.toString());
//	        File [] files = f.listFiles();
//
//	        //Log.i("Log", "List of files is: " +files.toString());
//	        Arrays.sort( files, new Comparator<Object>()
//	                {
//	            public int compare(Object o1, Object o2) {
//
//	                if (((File)o1).lastModified() > ((File)o2).lastModified()) {
//	                    //         Log.i("Log", "Going -1");
//	                    return -1;
//	                } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
//	                    //     Log.i("Log", "Going +1");
//	                    return 1;
//	                } else {
//	                    //     Log.i("Log", "Going 0");
//	                    return 0;
//	                }
//	            }
//
//	                });
//	        files[0].delete();
//	        try {
//				deleteCaptureANDSignature();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//	    }
//	    
//	 // ********** Also Delete Captured Images and signature from thumbnails (where a copy of captured images also created) 
//	    public void deleteCaptureANDSignature() {
//	        try {
//				File f = new File(Environment.getExternalStorageDirectory() + "/DCIM/.thumbnails" );
//				//Log.i("Log", "file name in delete folder :  "+f.toString());
//				File [] files = f.listFiles();
//
//				//Log.i("Log", "List of files is: " +files.toString());
//				Arrays.sort( files, new Comparator<Object>()
//				        {
//				    public int compare(Object o1, Object o2) {
//
//				        if (((File)o1).lastModified() > ((File)o2).lastModified()) {
//				            //         Log.i("Log", "Going -1");
//				            return -1;
//				        } else if (((File)o1).lastModified() < ((File)o2).lastModified()) {
//				            //     Log.i("Log", "Going +1");
//				            return 1;
//				        } else {
//				            //     Log.i("Log", "Going 0");
//				            return 0;
//				        }
//				    }
//
//				        });
//				
//				files[0].delete();
//				files[1].delete();
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//	    }
	    
	    public String returnDateFromServer(String serverDateTimeValue) {
			String dateFromServer = null;
			try {
				//   2014-09-04T12:49:35.477
				String[] splitDate = serverDateTimeValue.split("T");
				String dateSeparater = splitDate[0];
				
				String[] formatedDate = dateSeparater.split("-");
				dateFromServer = formatedDate[1]+"/"+formatedDate[2]+"/"+formatedDate[0];
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	return dateFromServer;
		}
	    
	    public String returnDateFromDeviceToPick(String serverDateTimeValue) {
	    	String dateFromServer = null;
			try {
				//   2014-09-04T12:49:35.477
				String[] splitDate = serverDateTimeValue.split("T");
				String dateSeparater = splitDate[0];
				
				String[] formatedDate = dateSeparater.split("-");
				dateFromServer = formatedDate[2]+"-"+formatedDate[1]+"-"+formatedDate[0];
				
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	
	    	return dateFromServer;
			}
	    
	    @SuppressLint("SimpleDateFormat")
		public String dateConvert(String D){

	        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
	        SimpleDateFormat format2 = new SimpleDateFormat("dd-MMM");
	        Date date = null;
	        try {
	            date = format1.parse(D);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        String dateString = format2.format(date);
	        dateString = dateString.replace("-", " "); 
	        System.out.println(dateString);
	        return ((dateString));
	    }
	    
	    @SuppressLint("SimpleDateFormat")
		public Date dateFromString(String dateStr){
	    	Date convertedDate = null;
	    	try {
	    		String[] dateTimeStrArray = dateStr.split("T");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				convertedDate = sdf.parse(dateTimeStrArray[0]);
				dateTimeStrArray = null;
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    	return convertedDate;
	    }
	    
	    @SuppressLint("SimpleDateFormat")
		public Date dateFromStringToSort(String dateStr){
	    	Date convertedDate = null;
	    	try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
				convertedDate = sdf.parse(dateStr);
	    	}catch (Exception e) {
				e.printStackTrace();
			}
	    	return convertedDate;
	    }
	    
	    @SuppressLint("SimpleDateFormat")
		public Date returnDateTimeForConversion(String dateTimeStr){
		    	SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
			    Date convertedDate = new Date();
			    try {
			        convertedDate = dateFormat.parse(dateTimeStr);
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
	    	return convertedDate;
	    }

	@SuppressLint("SimpleDateFormat")
	public Date returnDateTimeFor_BookingStatusRequest(String dateTimeStr){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateTimeStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertedDate;
	}

		//************ Create temp file in external storage *************//
	    public File createImageFile() throws IOException {
	        // Create an image file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	        String imageFileName = "PNG" + timeStamp + "_";
	        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	        File image = File.createTempFile(
	            imageFileName,  /* prefix */
	            ".png",         /* suffix */
	            storageDir      /* directory */
	        );

	        // Save a file: path for use with ACTION_VIEW intents
	       mCurrentPhotoPath = image.getAbsolutePath();
	        return image;
	    }

	    //************* Path for overlay image **************
	    public String makeDirToSaveOverlayImage(Context context, Bitmap bitmapImage){
			String filePath = "";
			File mypath = null;

			String uniqueId = BG_ImageUploadToServer.getTodaysDate() + "_" + BG_ImageUploadToServer.getCurrentTime() + "_" + Math.random();		// Set image name with uniqueID
			String current = uniqueId + ".png";

			boolean isMediaMounted = BG_ImageUploadToServer.prepareDirectory();				// create directory

			if (isMediaMounted && BG_ImageUploadToServer.isExternalStorageWritable()){
				mypath= new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), current);
				filePath = mypath.toString();
			} else {
				ContextWrapper wrapper = new ContextWrapper(context);
				mypath = wrapper.getDir("Z2U_PkgImg", context.MODE_PRIVATE);
				mypath = new File(mypath, current);
				filePath = mypath.toString();
			}

			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(mypath);
				bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return filePath;
		}


	//************ Create temp file in internal storage *************//
//	public File createImageFileAtInternalStorage(Context con) throws IOException {
//		ContextWrapper cw = new ContextWrapper(con.getApplicationContext());
//		// path to /data/data/yourapp/app_data/imageDir
//		File dir = new File("/data/data/com.zoom2u/app_Z2U_imageDir");
//		if (!dir.exists())
//			dir.mkdirs();
//		else
//			dir.delete();
//
//		File directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
//		// Create imageDir
//		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//		String imageFileName = "PNG" + timeStamp + "_";
//
//		File mypath=new File(directory, imageFileName+".png");
//
//		FileOutputStream fos = null;
//		try {
//			fos = new FileOutputStream(mypath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				fos.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//		mCurrentPhotoPath = mypath.getAbsolutePath();
//		return mypath;
//	}

//	public void deleteAllTempFileFromInternalStorage(Context con){
//		try {
//			File dir = con.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
//			if (dir.isDirectory())
//                dir.delete();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	    public String getMenulogTimeDiff(String createdTime, boolean isForCreatedTime){
	    	String returnMinutesInStr = "";
	    	try{
	    		if(!createdTime.equals("")){
	    			String defaultTime = "";
	    			if(isForCreatedTime == true)
	    				defaultTime = getDefaultSchedulePickUp(createdTime);
	    			else
	    				defaultTime = createdTime;
	                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	                format.setTimeZone(TimeZone.getTimeZone("UTC"));
	                Calendar now = Calendar.getInstance();
	                String currentTime = format.format(now.getTime());
	                Date defaultDate = format.parse(defaultTime);
	                Date currentDate = format.parse(currentTime);
	                long mills = defaultDate.getTime() - currentDate.getTime();
	                int mins = (int) (mills/(1000*60));
	                mins = mins + 1;
                	returnMinutesInStr = String.valueOf(mins)+" mins";
                	format = null;
                	now = null;
                	defaultDate = null;
                	currentDate = null;
	    		}
            }catch (Exception e) {
                e.printStackTrace();
            }
	    	return returnMinutesInStr;
	    }
	    
	    @SuppressLint("SuspiciousIndentation")
		public synchronized String getNormalTimeDiff(String deliverTime, boolean isFromBidView){
	    	String returnMinutesInStr = "";
	    	try{
	    		if(!deliverTime.equals("")){
	    			Calendar now = Calendar.getInstance();
	    			Date checkForNextDay = dateFromString(deliverTime);
					if (!isFromBidView) {
						if (checkForNextDay.after(now.getTime()))
							returnMinutesInStr = "Tomorrow";
						else
							return returnRemainingTime(deliverTime, isFromBidView, now);
					} else
		                return returnRemainingTime(deliverTime, isFromBidView, now);

	    			checkForNextDay = null;
	    			now = null;
	    		}
            }catch (Exception e) {
                e.printStackTrace();
            }
	    	return returnMinutesInStr;
	    }
/**
 * @param dropDate it's future date and here we calculating the
 *                 date, day, today and tomorrow
 * @param now
 */
		public  String getBookingDate(Date dropDate, Calendar now)
		{
			String bookingDateText="";
				try {

					Calendar calDropDate=Calendar.getInstance();
					calDropDate.setTime(dropDate);

					//add next day in current date to check the drop date is future or not
					now.add(Calendar.DATE,1);
					if (now.get(Calendar.DATE)==calDropDate.get(Calendar.DATE)&&
					now.get(Calendar.YEAR)==calDropDate.get(Calendar.YEAR)){
						bookingDateText="Tomorrow";
					}else {
						bookingDateText=new SimpleDateFormat("dd-MMM-yyyy").format(dropDate);
					}

				}catch (Exception ex){
					ex.printStackTrace();
				}

			return bookingDateText;
		}

	public synchronized String getNormalTimeDiffActive(String deliverTime, boolean isFromBidView,TextView view){
		String returnMinutesInStr = "";
		try{
			if(!deliverTime.equals("")){
				Calendar now = Calendar.getInstance();
				Date checkForNextDay = dateFromString(deliverTime);
				if (!isFromBidView) {
					if (checkForNextDay.after(now.getTime())) {
						returnMinutesInStr=getBookingDate(checkForNextDay,now);

                        view.setBackgroundResource(R.drawable.rounded_recruite_level_green);
					}
					else
						return returnRemainingTimeActive(deliverTime, isFromBidView, now,view);
				} else
					return returnRemainingTimeActive(deliverTime, isFromBidView, now,view);

				checkForNextDay = null;
				now = null;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return returnMinutesInStr;
	}

	private String returnRemainingTimeActive(String deliverTime, boolean isFromBidView, Calendar now,TextView textView) {
		String returnMinutesInStr = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			String currentTime = format.format(now.getTime());
			Date deliverDateTime = format.parse(deliverTime);
			Date currentDate = format.parse(currentTime);
			long mills = deliverDateTime.getTime() - currentDate.getTime();
			if (!isFromBidView) {
				int mins = (int) (mills / (1000 * 60));
                 if(Integer.signum(mins)==1)
                 	textView.setBackgroundResource(R.drawable.rounded_recruite_level);
                 else
					 textView.setBackgroundResource(R.drawable.rounded_recruite_level_red);
				returnMinutesInStr = String.valueOf(mins) + " mins";
			} else {
				int hours = (int) (mills / (1000 * 60 * 60));
				if (hours > 0)
					returnMinutesInStr = String.valueOf(hours) + " hrs";
				else {
					int mins = (int) (mills / (1000 * 60));
					if (mins > 0)
						returnMinutesInStr = String.valueOf(mins) + " mins";
					else
						returnMinutesInStr = "Expired";
				}
			}
			format = null;
			deliverDateTime = null;
			currentDate = null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnMinutesInStr;
	}




	private String returnRemainingTime(String deliverTime, boolean isFromBidView, Calendar now) {
		String returnMinutesInStr = "";
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			String currentTime = format.format(now.getTime());
			Date deliverDateTime = format.parse(deliverTime);
			Date currentDate = format.parse(currentTime);
			long mills = deliverDateTime.getTime() - currentDate.getTime();
			if (!isFromBidView) {
                int mins = (int) (mills / (1000 * 60));
                returnMinutesInStr = String.valueOf(mins) + " mins";
            } else {
                int hours = (int) (mills / (1000 * 60 * 60));
                if (hours > 0)
                    returnMinutesInStr = String.valueOf(hours) + " hrs";
                else {
                    int mins = (int) (mills / (1000 * 60));
                    if (mins > 0)
                        returnMinutesInStr = String.valueOf(mins) + " mins";
                    else
                        returnMinutesInStr = "Expired";
                }
            }
			format = null;
			deliverDateTime = null;
			currentDate = null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnMinutesInStr;
	}

	public String returnTimeInHourAndMinute(String deliverTime) {
		String returnMinutesInStr = "";
		try {
			Calendar now = Calendar.getInstance();
			SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String currentTime = utcFormat.format(now.getTime());
			Date dropDateTimeFromServer = new Date();
			deliverTime = deliverTime.replace("Z", "GMT+00:00");
			Date deliverDateTime = utcFormat.parse(deliverTime);
			Date currentDate = utcFormat.parse(currentTime);
			long mills = deliverDateTime.getTime() - currentDate.getTime();
			int hours = (int) (mills / (1000 * 60 * 60));
			int reminder = (int) (mills % (1000 * 60 * 60)) ;
			if (hours != 0) {
				returnMinutesInStr = String.valueOf(hours) + " hr";
				int minute = reminder / (1000 * 60);
				if (minute > 0)
					returnMinutesInStr = returnMinutesInStr+" "+minute+" min";
			} else {
				int mins = (int) (mills / (1000 * 60));
				returnMinutesInStr = String.valueOf(mins) + " mins";
			}
			utcFormat = null;
			deliverDateTime = null;
			currentDate = null;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return returnMinutesInStr;
	}

	/************** Add schedule pickup time **********/
		@SuppressLint("SimpleDateFormat")
		public String getDefaultSchedulePickUp(String serverDateTimeValue) {
			String dateTimeReturn = null;
			try {
				if (!serverDateTimeValue.equals("")) {
					SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
					//converter.setTimeZone(TimeZone.getTimeZone("UTC"));
					// getting GMT timezone, you can get any timezone e.g. UTC
					//converter.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date convertedDate = new Date();
					try {
						convertedDate = converter.parse(serverDateTimeValue);
						long t=convertedDate.getTime();
						Date afterAddingTenMins=new Date(t + (20 * 60000));
						return converter.format(afterAddingTenMins);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return dateTimeReturn;
		}	
		
		/***************  Getting present location for shift request ****************/
		public String getPresentLocation(){
			String presentLocationStr = "";
				 GPSTracker gpsForPresentLocation = new GPSTracker(_context);
				  try {
					  if(gpsForPresentLocation.canGetLocation()){
						  LoginZoomToU.getCurrentLocatnlatitude = String.valueOf(gpsForPresentLocation.getLatitude());
						  LoginZoomToU.getCurrentLocatnLongitude = String.valueOf(gpsForPresentLocation.getLongitude());
					  }else{
						  LoginZoomToU.getCurrentLocatnlatitude = "0.0";
						  LoginZoomToU.getCurrentLocatnLongitude = "0.0";
					  }
					  gpsForPresentLocation = null;
				  }catch (Exception e2) {
					  e2.printStackTrace();
					  LoginZoomToU.getCurrentLocatnlatitude = "0.0";
					  LoginZoomToU.getCurrentLocatnLongitude = "0.0";
				  }
				  
				  if(!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
					  presentLocationStr = LoginZoomToU.getCurrentLocatnlatitude+","+LoginZoomToU.getCurrentLocatnLongitude;
					  return presentLocationStr;
				  }else
					  return presentLocationStr;
		}

    public static void sendLocationToServer(){
        try {
            WebserviceHandler webServiceHandler = new WebserviceHandler();
            String currentTime = ServiceForSendLatLong.getCurrentTime();
            if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals(""))
                webServiceHandler.sendUserLatLong(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude, currentTime);
            webServiceHandler = null;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

	public static boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}
		return isInBackground;
	}

	//**************** Set pick and drop time from server to required text field *****************
	public static void setDateTimeFromServerToPerticularField(TextView arriveBeforeTxtValueRouteAlrt, String dateTime, boolean isForDetailScreen) {
		if (!dateTime.equals("") && !dateTime.equals("null")){
			try{
				String [] dropDateTimeFromServer = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(dateTime).split(" ");
				if (isForDetailScreen)
					arriveBeforeTxtValueRouteAlrt.setText(dropDateTimeFromServer[1]+" "+dropDateTimeFromServer[2]);
				else {
					String dateStr = dropDateTimeFromServer[0];
					dateStr = dateStr.substring(0, 6);
					arriveBeforeTxtValueRouteAlrt.setText(dateStr+"\n"+dropDateTimeFromServer[1]+" "+dropDateTimeFromServer[2]);
				}
				dropDateTimeFromServer = null;
			}catch (Exception e){
				e.printStackTrace();
				arriveBeforeTxtValueRouteAlrt.setText("-");
			}
		}else
			arriveBeforeTxtValueRouteAlrt.setText("-");
	}

	//********** Get distance from current location to drop location ***********
	public float getDistanceFromCurrentToDropLocation(String destLatitude, String destLongitude){
		float distance = 0;			//in meters
		try {
			Location mylocation = new Location("");
			Location dest_location = new Location("");
			dest_location.setLatitude(Double.parseDouble(destLatitude));
			dest_location.setLongitude(Double.parseDouble(destLongitude));
			mylocation.setLatitude(Double.parseDouble(LoginZoomToU.getCurrentLocatnlatitude));
			mylocation.setLongitude(Double.parseDouble(LoginZoomToU.getCurrentLocatnLongitude));
			distance = mylocation.distanceTo(dest_location);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}

		return distance;
	}

	public static int dip2px(Context context, float dp){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}

	//*************** Error message ************
	public static void validationErrorMsg (Context context, String apiResponseObj){
		try {
			JSONObject jObjOfServerResponse = new JSONObject(apiResponseObj);
			if (apiResponseObj.length() > 0) {
                String titleMsgStr = "";
                String msgStr = "";
                if (jObjOfServerResponse.has("errors")) {
                    try {
                        JSONArray jArrayOfErrorObj = jObjOfServerResponse.getJSONArray("errors");
                        titleMsgStr = jObjOfServerResponse.getString("message");
                        for (int i = 0; i < jArrayOfErrorObj.length(); i++) {
                            JSONObject errorObj = jArrayOfErrorObj.getJSONObject(i);
                            if (msgStr.equals(""))
                                msgStr = errorObj.getString("message");
                            else
                                msgStr = msgStr +"\n"+errorObj.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        titleMsgStr = "Error!";
                        msgStr = "Something went wrong, Please try again.";
                    }
                } else {
                    if (jObjOfServerResponse.has("message")) {
                        titleMsgStr = "Error!";
                        try {
                            msgStr = jObjOfServerResponse.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            titleMsgStr = "Error!";
                            msgStr = "Something went wrong, Please try again.";
                        }
                    } else {
                        titleMsgStr = "Error!";
                        msgStr = "Something went wrong, Please try again.";
                    }
                }
                DialogActivity.alertDialogView(context, titleMsgStr, msgStr);
            } else
                DialogActivity.alertDialogView(context, "Error!", "Something went wrong, Please try again.");
		} catch (Exception e) {
			e.printStackTrace();
			DialogActivity.alertDialogView(context, "Error!", "Something went wrong, Please try again.");
		}
	}


	// ********** Get file path from URI ************
	public String getFileNameByUri(Context context, Uri uri)
	{
		String filepath = "";//default fileName
		//Uri filePathUri = uri;
		File file;
		if (uri.getScheme().toString().compareTo("content") == 0)
		{
			Cursor cursor = context.getContentResolver().query(uri, new String[] { android.provider.MediaStore.Images.ImageColumns.DATA, MediaStore.Images.Media.ORIENTATION }, null, null, null);
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);

			cursor.moveToFirst();

			String mImagePath = cursor.getString(column_index);
			cursor.close();
			filepath = mImagePath;

		}
		else
		if (uri.getScheme().compareTo("file") == 0)
		{
			try
			{
				file = new File(new URI(uri.toString()));
				if (file.exists())
					filepath = file.getAbsolutePath();

			}
			catch (URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			filepath = uri.getPath();
		}
		return filepath;
	}

	//***************  Get rotation of camera image **************
	public static int getImageOrientation(String imagePath){
		int rotate = 0;
		try {
			File imageFile = new File(imagePath);
			ExifInterface exif = new ExifInterface(
					imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotate;
	}

	//************** Rotate camera image ************
	public static Bitmap getRotatedCameraImg (String imageUrl, Bitmap originalBitmap) {
		Bitmap rotatedBitmap = originalBitmap;
		try {
			Matrix matrix = new Matrix();
			matrix.postRotate(getImageOrientation(imageUrl));
			rotatedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotatedBitmap;
	}

	public static ArrayList<String> getScannedPieceArray (ArrayList<String> pieceArray) {
		ArrayList<String> scannedPieceArray = null;
		if (pieceArray.size() > 1) {
			if (ActiveBookingDetail_New.scannedPieceMap != null) {
				scannedPieceArray = new ArrayList<String>();
				for (Map.Entry<String, Boolean> entry : ActiveBookingDetail_New.scannedPieceMap.entrySet()) {
					if (entry.getValue())
						scannedPieceArray.add(entry.getKey());
				}
			} else
				return pieceArray;
		} else
			return pieceArray;

		return scannedPieceArray;
	}

	public static boolean checkCameraFront(Context context) {
		if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean checkDateIsToday(String serverDateTimeValue){
		boolean isToday = true;

		try {
			if(!serverDateTimeValue.equals("")){
				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				converter.setTimeZone(TimeZone.getTimeZone("UTC"));

				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				//getting GMT timezone, you can get any timezone e.g. UTC
				Calendar now = Calendar.getInstance();

				Date sreverDate = converter.parse(serverDateTimeValue);

				String bookingDate= sdf.format(sreverDate);
				String todayDate = sdf.format(now.getTime());

				Date parseBookingDate = sdf.parse(bookingDate);
				Date parseTodayDate = sdf.parse(todayDate);

				if (parseBookingDate.after(parseTodayDate))
					isToday=false;
				else isToday=true;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isToday;
	}

}
