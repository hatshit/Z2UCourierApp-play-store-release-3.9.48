package com.zoom2u.webservice;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.PushReceiver;
import com.zoom2u.offer_run_batch.ApiResponseModel;
import com.zoom2u.utility.Functional_Utility;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class WebserviceHandler {

	//************ Must change the Firebase DB and config file
	// google-service.json to live before release

	static String serverURL = "https://api.zoom2u.com";          				// Pointing to Live DB
	public static String TERMS_CONDITIONS="https://courier.zoom2u.com/courier-terms-conditions.html"; //live

//	public static String serverURL = "https://zoom2uapi-test-2.azurewebsites.net";
	//public static String serverURL = "https://api-test.zoom2u.com";						// Pointing to Test DB
	//public static String serverURL = "https://zoom2uapi-staging.azurewebsites.net";		// Staging slot of api-staging (Staging API)

// 	public static String TERMS_CONDITIONS="https://courier-staging.zoom2u.com/courier-terms-conditions.html";//staging
//	public static String serverURL = "https://zoom2uapi-staginge23f.azurewebsites.net"; // Staging slot of production api
//	public static String serverURL = "http://112278f82eca4131a753fe2d4e7e8b74.cloudapp.net";
//	public static String serverURL = "http://192.168.88.81:25251";
//	public static String serverURL = "http://zoom2uapi-test.cloudapp.net";

	public static boolean isRoutific = false;
	String serverReponse = "";
	public static JSONObject jObjOfCurrentCourierLevel = null;
	public static String vehicleTypeForUpdateLocation = "";

    public static int NEWBOOKING_COUNT;
    public static int ACTIVEBOOKING_COUNT;

	static String lineEnd = "\r\n";
	static String twoHyphens = "--";
	static String boundary = "AaB03x87yxdkjnxvi7";

	public WebserviceHandler() {
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//************* Get Courier app current version *************
	public String CheckForCourierAppVerSionUpdate() {
		HttpURLConnection conn = null;
		try {
			URL url = new URL(serverURL+"/breeze/courier/GetCourierAppCurrentVersion?deviceType=Android");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			try{
				JSONObject jObjOfVersionUpdate = new JSONObject(serverReponse);
				Log.e("", "   ------ Version API response --------     "+serverReponse);
				if(jObjOfVersionUpdate.getBoolean("success") == true)
					MainActivity.CURRENT_APP_VERSION_FROM_SERVER = jObjOfVersionUpdate.getInt("versionCode");
				else
					MainActivity.CURRENT_APP_VERSION_FROM_SERVER = 0;
				jObjOfVersionUpdate = null;
			}catch(Exception e){
				e.printStackTrace();
				MainActivity.CURRENT_APP_VERSION_FROM_SERVER = 0;
			}
			url = null;
			sb = null;
			br = null;
		} catch (UnsupportedEncodingException e) {
			MainActivity.CURRENT_APP_VERSION_FROM_SERVER = 0;
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			MainActivity.CURRENT_APP_VERSION_FROM_SERVER = 0;
			e.printStackTrace();
		} catch (IOException e) {
			MainActivity.CURRENT_APP_VERSION_FROM_SERVER = 0;
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}

		return serverReponse;
	}

	/********************* Http Get request to server  ****************/
	String httpGetRequest(URL url){
		String getResponse = "";
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			getResponse = sb.toString();
			url = null;
			sb = null;
			br = null;
		}catch(Exception e){
			e.printStackTrace();
			getResponse = "";
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return getResponse;
	}

	//  Call for http post method
	private String httpRequestForPost (URL urlHttpPost) {
		String responseStrForHttpPost = "";
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) urlHttpPost.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

	    	while ((line = br.readLine()) != null) {
	    		// Append server response in string
	    		sb.append(line);
	    	}
	    	responseStrForHttpPost = sb.toString();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}

	//  Call for http post method with response status
	private String httpPostRequestWithResponseStatus(URL urlHttpPost){
		LoginZoomToU.isLoginSuccess = 0;
		String responseStrForHttpPost = "";
		HttpURLConnection conn = null;
		try{
			conn = (HttpURLConnection) urlHttpPost.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				LoginZoomToU.isLoginSuccess = 2;
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			responseStrForHttpPost = sb.toString();
		}catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 3;
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}

	private String httpPostWithRequestBodyWithResponseCode (URL url, String requestBody){
		LoginZoomToU.isLoginSuccess = 0;
		String responseStrForHttpPost = "";
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(requestBody);
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				LoginZoomToU.isLoginSuccess = 2;
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			responseStrForHttpPost = sb.toString();
		}catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 3;
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}


	//************* Call service to get detail of Active booking ************
	public String getSubmitBarcodeNote(String inviteMemberRequestBody){
		serverReponse = "";
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/runs/incomplete-run-pickup-approval");
			serverReponse = httpPostWithRequestBodyWithResponseCode(url, inviteMemberRequestBody);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}



	// WebService call for User Login
	public String getUserLogin(String userEmail, String userPass) {
		HttpURLConnection conn = null;
		try {
			LoginZoomToU.isLoginSuccess = 0;
			String data = URLEncoder.encode("grant_type", "UTF-8") + "=" + URLEncoder.encode("password", "UTF-8");
        	data += "&" + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userEmail, "UTF-8");
        	data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(userPass, "UTF-8");

            URL url = new URL(serverURL+"/token");
            conn = (HttpURLConnection) url.openConnection();

            conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();
            // Get the server response 
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				LoginZoomToU.isLoginSuccess = 2;
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line = null;

			 while ((line = br.readLine()) != null) {
			 // Append server response in string
				 sb.append(line);
			 }

			 serverReponse = sb.toString();
			Log.e("", "  Login response  ==   "+serverReponse);
			 url = null;
			 sb = null;
			 br = null;

		}catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch (Exception e) {
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 3;
		}finally {
			if (conn != null)
				conn.disconnect();
		}

		return serverReponse;
	}

	// WebService call for Routific check for ETA
		public String CheckRoutific() {
			HttpURLConnection conn = null;
			try {
	            URL url = new URL(serverURL+"/breeze/courier/CheckRoutific");
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "application/json");
				conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
				conn.setConnectTimeout(15000);
				conn.setReadTimeout(20000);
				// Get the server response
				BufferedReader br = null;
				if (conn.getResponseCode() == 200)
					br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				else if (conn.getResponseCode() == 401) {
					serverReponse = "401";
					return serverReponse;
				}
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = br.readLine()) != null) {
					// Append server response in string
					sb.append(line);
				}
				serverReponse = sb.toString();
				url = null;
				sb = null;
				br = null;
				conn.disconnect();

		    	JSONObject jObjOfCheckRoutific = new JSONObject(serverReponse);
		    	if(jObjOfCheckRoutific.getString("enabled").equals("false"))
		    		isRoutific = false;
		    	else
		    		isRoutific = true;

		    	try {
					LoginZoomToU.courierID = jObjOfCheckRoutific.getString("courierId");
					LoginZoomToU.courierName = jObjOfCheckRoutific.getString("name");
					LoginZoomToU.courierImage = jObjOfCheckRoutific.getString("image");
					LoginZoomToU.courierCompany = jObjOfCheckRoutific.getString("company");
				} catch (Exception e){
					e.printStackTrace();
					LoginZoomToU.courierID = "";
					LoginZoomToU.courierName = "";
				}
		    	jObjOfCheckRoutific = null;
		    }catch (Exception e) {
		         e.printStackTrace();
				serverReponse = "";
		    }finally {
				if (conn != null)
					conn.disconnect();
			}
			return serverReponse;
		}


	//get run batch details by runId
	public ApiResponseModel getRoute1(String request){
		ApiResponseModel responseStrForHttpPost = new ApiResponseModel();
		HttpURLConnection conn = null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/bookings/build-route");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(request);
			wr.flush();

			// Get the server response
			responseStrForHttpPost.setCode(conn.getResponseCode());
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null){
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			responseStrForHttpPost.setResponse(serverReponse);
			url = null;
			sb = null;
			br = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}


	public String getRoutes(String endTime,String endLocation,String lat,String lang)
	{
		//ApiResponseModel serverReponse=null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/bookings/build-route?endTime="+endTime+"&endLocation="+endLocation+"&latitude="+lat+"&longitude="+lang);
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//get run batch details by runId
	public ApiResponseModel AcceptRoute(String routeModelId)
	{
		ApiResponseModel serverReponse=null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/bookings/accept-route?routeModelId="+routeModelId);
			serverReponse = httpRequestForPostWithErrorBodyMsg(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public ApiResponseModel AcceptRoute1(String multipleBookingIdStr)
	{
		ApiResponseModel serverReponse=null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/bookings/accept-route?routeModelId");
			serverReponse = httpRequestForPostWithErrorBodyMsgWithBody(url,multipleBookingIdStr);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}



	//get run batch details by runId
	public ApiResponseModel RejectRoute()
	{
		ApiResponseModel serverReponse=null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/bookings/reject-route");
			serverReponse = httpRequestForPostWithErrorBodyMsg(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}



		// WebService call for getting Current Courier Level
		public void getCurrentCourierLevel() {
					try {
			            URL url = new URL(serverURL+"/breeze/courier/CourierLevel");
			            serverReponse = httpGetRequest(url);
				    	url = null;
				    	try {
							JSONArray jArrayOfCurrentCourierLevel = new JSONArray(serverReponse);
							jObjOfCurrentCourierLevel = jArrayOfCurrentCourierLevel.getJSONObject(0);
							jArrayOfCurrentCourierLevel = null;
							serverReponse = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
				    }catch (Exception e) {
				         e.printStackTrace();
				    }
				}

	// Sending User's Latitude and Longitude
	public void sendUserLatLong(String latitude, String longitude, String currentTime) {
			try {
                URL url = new URL(serverURL+"/breeze/courier/PresentLocation?latitude="+latitude+
                            "&longitude="+longitude+"&timestamp="+currentTime);

	          	serverReponse = httpRequestForPost(url);
				Log.e("Location updates",url+" \n ======= UserLocation send to server ========= "+serverReponse);
              	url = null;
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
		}

	// Sending Device TokenId after user login
	public void sendDeviceTokenID(String deviceId, String appVersionName) {
				try {
					URL url = new URL(serverURL+"/breeze/courier/UpdateDeviceId?DeviceId="+deviceId+"&deviceType=Android&appVersion="+appVersionName);
		            serverReponse = httpRequestForPost(url);
                    try {
						JSONObject jOBJOfTokenUpdate = new JSONObject(serverReponse);
						vehicleTypeForUpdateLocation = jOBJOfTokenUpdate.getString("vehicle");

						jOBJOfTokenUpdate = null;
					} catch (Exception e) {
						e.printStackTrace();
						vehicleTypeForUpdateLocation = "";
					}
					url = null;
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			}

		// User Forget password
		public String getForgetPasswordEmail(String emailId) {
				try {
		            URL url = new URL(serverURL+"/api/account/ForgotPassword?username="+emailId);
		            serverReponse = httpRequestForPost(url);
			    	url = null;
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
				return serverReponse;
			}

	// Get Courier is Online/Offline
	public String serviceForGetCourierOnlineOffline() {
		try {
			URL url = new URL(serverURL+"/breeze/courier/CheckIsOnline");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	// User Forget password
	public String SetIsCourierOnline(boolean isOnline) {
		try {
			URL url = new URL(serverURL+"/breeze/courier/SetIsOnline?isOnline="+isOnline);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//********** Get new booking data ***************//
		public String getNewBookingList(int pageCount) {
			try {
				URL url = new URL(serverURL+"/breeze/courier/DeliveryRequestsForCourier?page="+pageCount);
	            serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

		//   GetActive Booking List From Server
		public String getActiveBookingList(String filterDayStr, int pageCount, String awbNumber, String status) {
			try {
				URL url = null;
				//url = new URL(serverURL+"/breeze/courier/ActiveDeliveries?filterDay="+filterDayStr);

				filterDayStr = java.net.URLEncoder.encode(filterDayStr, "UTF-8");
				if (!awbNumber.equals(""))
					awbNumber = java.net.URLEncoder.encode(awbNumber, "UTF-8");
				if (!status.equals(""))
					status = java.net.URLEncoder.encode(status, "UTF-8");

//  **Old API**	url = new URL(serverURL+"/breeze/courier/ActiveDeliveriesForCourier?filterType="+filterDayStr+"&page="+pageCount+"&awb="+awbNumber+"&status="+status);
				url = new URL(serverURL+"/api/mobile/courier/GetActiveDeliveriesForCourier?filterType="+filterDayStr+"&page="+pageCount+"&awb="+awbNumber+"&status="+status);
				serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		   return serverReponse;
		}

	//   Get Drop off Booking List From Server
	public String getDropOffBookingListFromServer(int page) {
			try {
		        URL url = new URL(serverURL+"/breeze/courier/DroppedOffDeliveriesForCourier?page="+page);
		        serverReponse = httpGetRequest(url);
			  	url = null;
			} catch (Exception e) {
			    e.printStackTrace();
			}
		return serverReponse;
	}

	// accept booking from New booking list
	@SuppressLint("SimpleDateFormat")
	public String acceptBookingForPickup(int bookingID, String etaTime) {
				try {
					URL url = null;
		            if(isRoutific == false){
		            	if(etaTime.equals("")){
							Date date = new Date();
						    SimpleDateFormat sdf;
						    sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
						    etaTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(sdf.format(date));
						    date = null;
						    sdf = null;
						}

                        if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude+","+LoginZoomToU.getCurrentLocatnLongitude;
                            url = new URL(serverURL + "/breeze/courier/AcceptDelivery?BookingId=" + bookingID + "&PickupETA=" + etaTime + "&location=" +currentLocation);
                        }else
                            url = new URL(serverURL+"/breeze/courier/AcceptDelivery?BookingId="+bookingID+"&PickupETA="+etaTime);
		            }else {
                        if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                            url = new URL(serverURL + "/breeze/courier/AcceptDelivery?BookingId=" + bookingID+"&location=" +currentLocation);
                        }else
                            url = new URL(serverURL + "/breeze/courier/AcceptDelivery?BookingId=" + bookingID);
                    }
		            serverReponse = httpRequestForPost(url);
			    	url = null;
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
				return serverReponse;
			}

	// Reject booking from New booking list
	public String rejectBookingForPickup(int bookingIDForReject) {
					try {
                        URL url = new URL(serverURL+"/breeze/courier/RejectDelivery?BookingId="+bookingIDForReject);
			            serverReponse = httpRequestForPost(url);
				    	url = null;
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					return serverReponse;
				}

	/******************* Booking dispatch to courier ***********************/
	public String dispatchToOtherCourier(int bookingIDForDoispatchCourier) {
				try {
				       URL url = new URL(serverURL+"/breeze/courier/SendBackToCouriers?bookingId="+bookingIDForDoispatchCourier);
				       serverReponse = httpRequestForPost(url);
					   url = null;
					} catch (Exception e) {
					    e.printStackTrace();
					}
				return serverReponse;
			}

	//  Update password from server
	public String getUpdatePasswordforOldPass(String oldPassword, String newPassword){
		try {
            URL url = new URL(serverURL+"/api/account/changePassword?OldPassword="+URLEncoder.encode(oldPassword, "utf-8")+"&NewPassword="+URLEncoder.encode(newPassword, "utf-8"));
            serverReponse = httpRequestForPost(url);
	    	url = null;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return serverReponse;
	}

	//  Request Pickup person name and notes to server
	public String requestForPickPersonName(String pickPersonName, String noteString, int signeePosition, String bookingIdforPickUpPerson, String etaTimeForPickup){
		try {
			URL url = null;
			try {
				if(!pickPersonName.equals(""))
					pickPersonName = java.net.URLEncoder.encode(pickPersonName, "UTF-8");
				else
					pickPersonName = "NA";
			} catch (Exception e) {
				e.printStackTrace();
				pickPersonName = "NA";
			}

		if(noteString.equals("")) {
            if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                    !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
                String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                url = new URL(serverURL + "/breeze/courier/PickupDelivery?BookingId=" + bookingIdforPickUpPerson + "&pickupPerson=" + pickPersonName + "&signeesPosition=" + signeePosition+"&location="+currentLocation);
            }else
             url = new URL(serverURL + "/breeze/courier/PickupDelivery?BookingId=" + bookingIdforPickUpPerson + "&pickupPerson=" + pickPersonName + "&signeesPosition=" + signeePosition);
        }else{
			noteString = java.net.URLEncoder.encode(noteString, "UTF-8");
            if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                    !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                url = new URL(serverURL+"/breeze/courier/PickupDelivery?BookingId="+bookingIdforPickUpPerson+"&pickupPerson="+pickPersonName+"&notes="+noteString+"&signeesPosition="+signeePosition+"&location="+currentLocation);
            }else
			    url = new URL(serverURL+"/breeze/courier/PickupDelivery?BookingId="+bookingIdforPickUpPerson+"&pickupPerson="+pickPersonName+"&notes="+noteString+"&signeesPosition="+signeePosition);
		}
           serverReponse = httpRequestForPost(url);
	    	url = null;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return serverReponse;
	}

	//  Request Drop off person name and notes to server
	public String requestForDropOffPersonName(String dropPersonName, String noteDropOffString, int signeePosition, String bookingIdforDropOffPerson){
		HttpURLConnection conn = null;
		try {
			dropPersonName = java.net.URLEncoder.encode(dropPersonName, "UTF-8").replace(" ", "%20");
			URL url = null;
            if(noteDropOffString.equals("")){
                if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                        !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
                    String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                    url = new URL(serverURL + "/breeze/courier/DropDelivery?BookingId=" + bookingIdforDropOffPerson + "&pickupPerson=" + dropPersonName + "&signeesPosition=" + signeePosition+"&location="+currentLocation);
                }else
                    url = new URL(serverURL + "/breeze/courier/DropDelivery?BookingId=" + bookingIdforDropOffPerson + "&pickupPerson=" + dropPersonName + "&signeesPosition=" + signeePosition);
            }else{
                noteDropOffString = java.net.URLEncoder.encode(noteDropOffString, "UTF-8").replace(" ", "%20");
                if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                        !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
                    String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                    url = new URL(serverURL+"/breeze/courier/DropDelivery?BookingId="+bookingIdforDropOffPerson+"&pickupPerson="+dropPersonName+"&notes="+noteDropOffString+"&signeesPosition="+signeePosition+"&location="+currentLocation);
                }else
                    url = new URL(serverURL+"/breeze/courier/DropDelivery?BookingId="+bookingIdforDropOffPerson+"&pickupPerson="+dropPersonName+"&notes="+noteDropOffString+"&signeesPosition="+signeePosition);
            }
            Log.e("", "--------- Request Drop URL ============================   "+url);
			conn = (HttpURLConnection) url.openConnection();
	        conn.setDoOutput(true);
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
            conn.setDoOutput(true);
            ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
            PushReceiver.isCameraOpen = true;
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

	    	while ((line = br.readLine()) != null) {
	    		// Append server response in string
	    		sb.append(line);
	    	}

	    	serverReponse = sb.toString();
            Log.e("", "--------- Request Drop Server response ====   "+serverReponse);
	    	url = null;
	    	sb = null;
	    	br = null;

	    } catch (UnsupportedEncodingException e) {
	              e.printStackTrace();
	    } catch (ClientProtocolException e) {
	              e.printStackTrace();
	    } catch (IOException e) {
	              e.printStackTrace();
	    }finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}

	//  Request Return to DHL booking to server
	public String returnedToDHL(String recipentName, String noteDropOffString, int signeePosition, String bookingId){
		HttpURLConnection conn = null;
		try {
			recipentName = java.net.URLEncoder.encode(recipentName, "UTF-8").replace(" ", "%20");
			URL url = null;
			if(noteDropOffString.equals("")){
				if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
						!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
					String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
					url = new URL(serverURL + "/breeze/courier/ReturnDelivery?bookingId=" + bookingId + "&recipient=" + recipentName + "&signeesPosition=" + signeePosition+"&location="+currentLocation);
				}else
					url = new URL(serverURL + "/breeze/courier/ReturnDelivery?bookingId=" + bookingId + "&recipient=" + recipentName + "&signeesPosition=" + signeePosition);
			}else{
				noteDropOffString = java.net.URLEncoder.encode(noteDropOffString, "UTF-8").replace(" ", "%20");
				if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
						!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
					String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
					url = new URL(serverURL+"/breeze/courier/ReturnDelivery?bookingId="+bookingId+"&recipient="+recipentName+"&notes="+noteDropOffString+"&signeesPosition="+signeePosition+"&location="+currentLocation);
				}else
					url = new URL(serverURL+"/breeze/courier/ReturnDelivery?bookingId="+bookingId+"&recipient="+recipentName+"&notes="+noteDropOffString+"&signeesPosition="+signeePosition);
			}
			Log.e("", "--------- Request Return to DHL  ============================   "+url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
			PushReceiver.isCameraOpen = true;
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			Log.e("", "--------- Request Drop Server response ====   "+serverReponse);
			url = null;
			sb = null;
			br = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}

	// upload PickUp and DropOff PackageImage to server
	public int uploadPickUpandDropOffPackageImage(Bitmap imageCapture,String packageName, Bitmap signatureImage, String signatureName){
		int uploadImgCount = 0;
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 30000);
			 DefaultHttpClient client = new DefaultHttpClient(httpParams);
			httpParams = null;
	//*********** Staging Server - To resolve SSL exception when upload image	**********
			 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			 SchemeRegistry registry = new SchemeRegistry();
			 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			 registry.register(new Scheme("https", socketFactory, 443));
			 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
			 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
			 client = null;
	//*****************************************************************************
			 // Set verifier
			 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	         HttpPost postRequest = new HttpPost(serverURL+"/api/upload");

	         postRequest.setHeader("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));

            Log.e("", "--------- Request Image upload ================== ====   "+packageName+"------------"+signatureName);
			 MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			 if(imageCapture != null){
				 try {
					 ByteArrayOutputStream bos1 = new ByteArrayOutputStream();
					 imageCapture.compress(CompressFormat.JPEG, 75, bos1);
					 byte[] data = bos1.toByteArray();
					 ByteArrayBody bab = new ByteArrayBody(data, packageName+".png");
					 reqEntity.addPart("userfile1", bab);
					 reqEntity.addPart("photoSignature", new StringBody("signature"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
			if(signatureImage != null) {
				try {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					signatureImage.compress(CompressFormat.JPEG, 75, bos);
					byte[] data1 = bos.toByteArray();
					ByteArrayBody bab1 = new ByteArrayBody(data1, signatureName + ".png");
					reqEntity.addPart("userfile2", bab1);
					reqEntity.addPart("photoCaption", new StringBody("sfsdfsdf"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
	         postRequest.setEntity(reqEntity);
	         HttpResponse response = httpClient.execute(postRequest);
	         BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	         String sResponse;
	         StringBuilder s = new StringBuilder();

	         while ((sResponse = reader.readLine()) != null) {
	               s = s.append(sResponse);
	         }
	         serverReponse = s.toString();
	         try{
                 JSONObject jObjOfPkgImgResponse = new JSONObject(serverReponse);
                 if(jObjOfPkgImgResponse.getBoolean("success") == true)
                     uploadImgCount = 1;
                 else{
                     boolean status = true;
                     try{
                         status = jObjOfPkgImgResponse.getBoolean("status");
                     }catch (Exception e){
                        // e.printStackTrace();
                     }
					 /*
					 In the below two cases we don't store failed image to our local:-
					 1) If booking status hasn't changed.
					 2) If booking has not found in DB.
					 */
                     if(status == false)   // **** If booking status hasn't change **********
                         uploadImgCount = 1;
                     else{
                         try {
							 // **** If booking has been not found **********
                             if(jObjOfPkgImgResponse.getString("message").equalsIgnoreCase("Booking not found"))
                                 uploadImgCount = 1;
                         }catch (Exception e){
                           //  e.printStackTrace();
                         }
                     }
                 }
                 jObjOfPkgImgResponse = null;
	         }catch(Exception e){
	        	// e.printStackTrace();
	         }
            Log.e("","********** Upload image response  ==   "+serverReponse+"\n **********  Upload image count  "+uploadImgCount);
		 }catch(Exception e){
			// e.printStackTrace();
		 }
		return uploadImgCount;
	}

	//  Update eta time for pickup booking
	public String updatePickUpETAForBookingID(String pickUpEtaTime, String bookingIdforPickUpETA){
		try {
			URL url = new URL(serverURL+"/breeze/courier/UpdatePickupETA?BookingId="+bookingIdforPickUpETA+"&PickupETA="+pickUpEtaTime);
			serverReponse = httpRequestForPost(url);
	    	url = null;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return serverReponse;
	}


//  Update eta time for Drop off booking
	public String updateDropOffETAForBookingID(String dropOffEtaTime, String bookingIdforDropOffETA){
		try {
			URL url = new URL(serverURL+"/breeze/courier/UpdateDropETA?BookingId="+bookingIdforDropOffETA+"&DropETA="+dropOffEtaTime);
			serverReponse = httpRequestForPost(url);
	    	url = null;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
		return serverReponse;
	}

	//  Get courier profile detail From Server
		public String getCourierProfileDetails() {
			try {
	            //URL url = new URL(serverURL+"/breeze/courier/Couriers");   //******** Old profile detail ******
				URL url = new URL(serverURL+"/breeze/courier/GetCourierProfileDetails");
	            serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

		//  Update Courier Profile Detail
		public String updateCourierProfileDetailsWithDataArray(String dataForUpdateCourierdetail){
			HttpURLConnection conn = null;
			try {
				URL url = null;
				url = new URL(serverURL+"/breeze/courier/UpdateCourier");
				conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(20000);
	            conn.setDoOutput(true);
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            wr.write(dataForUpdateCourierdetail);
	            wr.flush();

				// Get the server response
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

		    	while ((line = br.readLine()) != null){
		    		// Append server response in string
		    		sb.append(line);
		    	}

		    	serverReponse = sb.toString();

		    	url = null;
		    	sb = null;
		    	br = null;

		    } catch (UnsupportedEncodingException e) {
		              e.printStackTrace();
		    } catch (ClientProtocolException e) {
		              e.printStackTrace();
		    } catch (IOException e) {
		              e.printStackTrace();
		    }finally {
				if (conn != null)
					conn.disconnect();
			}
			return serverReponse;
		}

		public String uploadProfileImage(Bitmap profileImage, String imageName, boolean isChatImage) {
			 try {
				 if(profileImage != null){
						int bitmapImageHeight = profileImage.getHeight();
						int bitmapImageWidth = profileImage.getWidth();
						if(bitmapImageWidth > 400 || bitmapImageHeight > 400)
							profileImage = Functional_Utility.scaleToActualAspectRatio(profileImage, 400.0f, 400.0f);
					}
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params, 20000);
			HttpConnectionParams.setSoTimeout(params, 25000);
				 DefaultHttpClient client = new DefaultHttpClient(params);
		//*********** Staging Server - To resolve SSL exception when upload image	**********
				 HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				 SchemeRegistry registry = new SchemeRegistry();
				 SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
				 socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				 registry.register(new Scheme("https", socketFactory, 443));
				 SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
				 DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
				 client = null;
		//*****************************************************************************
				 HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
				 ByteArrayOutputStream bos = new ByteArrayOutputStream();
				 profileImage.compress(CompressFormat.PNG, 100, bos);
		         byte[] data = bos.toByteArray();
		         HttpPost postRequest;
			if (isChatImage)
				postRequest = new HttpPost(serverURL + "/api/mobile/courier/chat/photo");
			else
				postRequest = new HttpPost(serverURL + "/api/upload");
		         postRequest.setHeader("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));

		         ByteArrayBody bab = new ByteArrayBody(data, imageName);
		         MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		         reqEntity.addPart("userfile1", bab);
		         reqEntity.addPart("photoCaption", new StringBody("---------------------\\\\sfsdfsdf"));

		         postRequest.setEntity(reqEntity);
		         HttpResponse response = httpClient.execute(postRequest);
		         BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		         String sResponse;
		         StringBuilder s = new StringBuilder();

		         while ((sResponse = reader.readLine()) != null) {
		               s = s.append(sResponse);
		         }

		         serverReponse = s.toString();
			 }catch (Exception e) {
		            Log.e(e.getClass().getName(), e.getMessage());
		        }
			return serverReponse;
		}

		public String getCourierAvailability() {
			try {
	            URL url = new URL(serverURL+"/breeze/courier/CourierAvailability");
	            serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

	//Update Courier Profile Detail
	public String updateCourierAvailablility(JSONArray dataArrayForUpdateCourierAvailablility){
		HttpURLConnection conn = null;
		try {
			URL url = null;
			url = new URL(serverURL+"/breeze/courier/UpdateCourierAvailability");
			conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(20000);
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(dataArrayForUpdateCourierAvailablility.toString());
            wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

	    	while ((line = br.readLine()) != null) {
	    		// Append server response in string
	    		sb.append(line);
	    	}

	    	serverReponse = sb.toString();
	    	url = null;
	    	sb = null;
	    	br = null;

	    } catch (UnsupportedEncodingException e) {
	              e.printStackTrace();
	    } catch (ClientProtocolException e) {
	              e.printStackTrace();
	    } catch (IOException e) {
	              e.printStackTrace();
	    }finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}


		//Update Courier Date Schedule
		public String UpdateCourierDateSchedule(JSONArray courierSelectDate){
			HttpURLConnection conn = null;
			try {
				URL url = null;
				url = new URL(serverURL+"/breeze/courier/UpdateCourierDateSchedule?scheduledDate=");
				conn = (HttpURLConnection) url.openConnection();
	            conn.setRequestMethod("POST");
	            conn.setRequestProperty("Content-Type", "application/json");
	            conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
	            conn.setDoOutput(true);
	            conn.setConnectTimeout(15000);
	            conn.setReadTimeout(20000);
	            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	            wr.write(courierSelectDate.toString());
	            wr.flush();

				// Get the server response
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

		    	while ((line = br.readLine()) != null) {
		    		// Append server response in string
		    		sb.append(line);
		    	}

		    	serverReponse = sb.toString();
		    	url = null;
		    	sb = null;
		    	br = null;

		    } catch (Exception e) {
		              e.printStackTrace();
		              return null;
		    } finally {
				if (conn != null)
					conn.disconnect();
			}
			return serverReponse;
		}

		public String getCourierDateSchedule() {
			try {
	            URL url = new URL(serverURL+"/breeze/courier/CourierDateSchedule");
	            serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

		//on Route PickUp
		public String onRoutePickUp(int bookingIdOnRoutePick){
					try {
						URL url = null;
                        if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                            url = new URL(serverURL+"/breeze/courier/OnRoutePickup?bookingId="+bookingIdOnRoutePick+"&location="+currentLocation);
                        }else
						   url = new URL(serverURL+"/breeze/courier/OnRoutePickup?bookingId="+bookingIdOnRoutePick);

						serverReponse = httpRequestForPost(url);

				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					return serverReponse;
				}

		/********  On route to pick multiple bookings  ***********/
		public String OnRoutePickupMultiple(String multipleBookingIdStr){
			HttpURLConnection conn = null;
			try {
				URL url = null;
                url = new URL(serverURL+"/breeze/courier/OnRoutePickupMultiple");
				conn = (HttpURLConnection) url.openConnection();
		        conn.setRequestMethod("POST");
		        conn.setRequestProperty("Content-Type", "application/json");
		        conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
		        conn.setDoOutput(true);
		        conn.setConnectTimeout(15000);
		        conn.setReadTimeout(20000);
		        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		        wr.write(multipleBookingIdStr);
		        wr.flush();

		        // Get the server response
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = br.readLine()) != null) {
				// Append server response in string
					sb.append(line);
				}

				serverReponse = sb.toString();
				url = null;
				sb = null;
				br = null;

			} catch (Exception e) {
			     e.printStackTrace();
			     return null;
			} finally {
				if (conn != null)
					conn.disconnect();
			}
			return serverReponse;
		}

		/********  Pick up multiple bookings  ***********/
		public String pickUpMultipleBookings(String multipleBookingIdObjStr){
			HttpURLConnection conn = null;
			try {
				URL url = null;
				url = new URL(serverURL+"/breeze/courier/PickupMultipleDeliveries");
				conn = (HttpURLConnection) url.openConnection();
		        conn.setRequestMethod("POST");
		        conn.setRequestProperty("Content-Type", "application/json");
		        conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
		        conn.setDoOutput(true);
		        conn.setConnectTimeout(15000);
		        conn.setReadTimeout(20000);
		        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		        wr.write(multipleBookingIdObjStr);
		        wr.flush();

		        // Get the server response
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = br.readLine()) != null) {
				// Append server response in string
					sb.append(line);
				}

				serverReponse = sb.toString();
				url = null;
				sb = null;
				br = null;

			} catch (Exception e) {
			     e.printStackTrace();
			     return null;
			} finally {
				if (conn != null)
					conn.disconnect();
			}
		return serverReponse;
	}

		//on Route Drop Off
		public String onRouteDropOff(int bookingIdOnRouteDrop){
				try {
					URL url = null;
                    if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                            !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                        String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                        url = new URL(serverURL+"/breeze/courier/OnRouteDropoff?bookingId="+bookingIdOnRouteDrop+"&location="+currentLocation);
                    }else
					    url = new URL(serverURL+"/breeze/courier/OnRouteDropoff?bookingId="+bookingIdOnRouteDrop);

					serverReponse = httpRequestForPost(url);
			    	url = null;
			    } catch (Exception e) {
			        e.printStackTrace();
			    }
			return serverReponse;
		}

		//on Route Drop Off
		public String inviteCustomer(String invitationData){
				try {
					URL url = null;
					url = new URL(serverURL+"/breeze/courier/InviteCustomer?"+invitationData);
					serverReponse = httpRequestForPost(url);
					url = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			return serverReponse;
		}

		/******************* Service for attempt delivery ******************/
		public String attemptDelivery(int bookingID, String notes, String triedToDeliverReason, String location){
				try {
					if(!notes.equals(""))
						notes = java.net.URLEncoder.encode(notes, "UTF-8").replace(" ", "%20");
					triedToDeliverReason = java.net.URLEncoder.encode(triedToDeliverReason, "UTF-8").replace(" ", "%20");

					URL url = new URL(serverURL+"/breeze/courier/AttemptDelivery?bookingId="+bookingID+"&notes="+notes+"&triedToDeliverReason="+triedToDeliverReason+"&presentLocation="+location);
					serverReponse = httpRequestForPost(url);
					url = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			return serverReponse;
		}

//		/***************  Old Get Request Availability detail on notification******************/
//		public String getRequestBookingDetail(int requestId) {
//			try {
//	            URL url = new URL(serverURL+"/breeze/courier/AvailabilityRequestById?requestId="+requestId);
//	            serverReponse = httpGetRequest(url);
//		    	resetRequestId();
//		    	url = null;
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		        resetRequestId();
//		    }
//			return serverReponse;
//		}

		/***************  Get List of request availability ******************/
		public String getRequestAvailList() {
			try {
	            URL url = new URL(serverURL+"/breeze/courier/GetAvailabilityRequests");
	            serverReponse = httpGetRequest(url);
		    	url = null;
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

	/***************  Get List of request availability ******************/
	public String getRequestAvailById(int requestId) {
		try {
			URL url = new URL(serverURL+"/breeze/courier/GetAvailabilityRequestById?requestId="+requestId);
			serverReponse = httpGetRequest(url);
			resetRequestId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
			resetRequestId();
		}
		return serverReponse;
	}

		/***************  Accept Availability Request  ********************/
		public String acceptAvailabilityRequest(int requestID) {
					try {
			            URL url = new URL(serverURL+"/breeze/courier/AcceptAvailabilityRequest?requestId="+requestID);
			            serverReponse = httpRequestForPost(url);
				    	url = null;
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					return serverReponse;
				}

		/********** Reject Availability Request ***************/
		public String rejectAvailabilityRequest(int requestId) {
				try {
			          URL url = new URL(serverURL+"/breeze/courier/RejectAvailabilityRequest?requestId="+requestId);
			          serverReponse = httpRequestForPost(url);
					  url = null;
				}catch(Exception e) {
					 e.printStackTrace();
				}
			return serverReponse;
		}

	//   Get Drop off Booking List From Server
		public String getHeroLeaderboardList() {
					try {
			            URL url = new URL(serverURL+"/breeze/courier/ActiveLeaders");
			            serverReponse = httpGetRequest(url);
				    	url = null;
				    } catch (Exception e) {
				        e.printStackTrace();
				    }
					return serverReponse;
				}

	//  Get Drop off Booking List From Server
		public String getTrainingVideoList(int page) {
						try {
				            URL url = new URL(serverURL+"/breeze/courier/TutorialVideos?page="+page);
					    	serverReponse = httpGetRequest(url);
					    	url = null;
					    } catch (Exception e) {
					              e.printStackTrace();
					    }
						return serverReponse;
					}

		/***************   Update Delivery Notes  ******************/
		public String updateDeliveryNotes(String bookingId,String notesStr) {
			try {
				try{
					notesStr = java.net.URLEncoder.encode(notesStr, "UTF-8").replace(" ", "%20");
				}catch(Exception e){
					e.printStackTrace();
				}

	            URL url = new URL(serverURL+"/breeze/courier/UpdateDeliveryNotes?bookingId="+bookingId+"&notes="+notesStr);
		    	serverReponse = httpRequestForPost(url);
		    	url = null;
		    } catch (Exception e) {
		              e.printStackTrace();
		              resetBookingId();
		    }
			return serverReponse;
		}

		void resetBookingId(){
			PushReceiver.bookingID = "0";
			PushReceiver.loginEditorForPushy.putString("bookingId", PushReceiver.bookingID);
			PushReceiver.loginEditorForPushy.commit();
		}

		public void resetBidRequestId() {
			PushReceiver.Bid_REQUEST_ID = "0";
			PushReceiver.loginEditorForPushy.putString("Bid_REQUEST_ID", PushReceiver.Bid_REQUEST_ID);
			PushReceiver.loginEditorForPushy.commit();
		}

	public void resetRunId() {
		PushReceiver.runId = "0";
		PushReceiver.loginEditorForPushy.putString("runId", PushReceiver.runId);
		PushReceiver.loginEditorForPushy.commit();
	}


	public void resetRunBatchId() {
		PushReceiver.runBatchId = "0";
		PushReceiver.loginEditorForPushy.putString("runBatchId", PushReceiver.runBatchId);
		PushReceiver.loginEditorForPushy.commit();
	}

		void resetRequestId(){
			PushReceiver.requestID = "0";
			PushReceiver.loginEditorForPushy.putString("requestId", PushReceiver.requestID);
			PushReceiver.loginEditorForPushy.commit();
		}

	public String deliveryRunsForCourier() {
		try {
			URL url = new URL(serverURL+"/breeze/courier/DeliveriesRuns");
			serverReponse = httpGetRequest(url);
			url = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

		/***************   Available Shifts For Courier  ******************/
//		public String availableShiftsForCourier(int pageCount) {
//			try {
//	            URL url = new URL(serverURL+"/breeze/courier/AvailableShiftsForCourier?page="+pageCount);
//		    	serverReponse = httpGetRequest(url);
//		    	url = null;
//		    }catch(Exception e) {
//		        e.printStackTrace();
//		    }
//			return serverReponse;
//		}
//
//		/***************   Available Shifts For Courier  ******************/
//		public String acceptedShiftsForCourier(int pageCount) {
//			try {
//	            URL url = new URL(serverURL+"/breeze/courier/AcceptedShiftsForCourier?page="+pageCount);
//		    	serverReponse = httpGetRequest(url);
//		    	url = null;
//		    }catch(Exception e) {
//		        e.printStackTrace();
//		    }
//			return serverReponse;
//		}

		/***************   Accept Shift  ******************/
		public String acceptShift(int shiftID) {
			try {
	            URL url = new URL(serverURL+"/breeze/courier/AcceptShift?shiftId="+shiftID);
	            serverReponse = httpRequestForPost(url);
		    	url = null;
		    } catch (Exception e) {
		          e.printStackTrace();
		    }
			return serverReponse;
		}

		/***************   Accept Shift  ******************/
		public String arrivedShift(int shiftID, String presentLocationStr) {
			try {
				URL url = null;
				if(presentLocationStr.equals(""))
					url = new URL(serverURL+"/breeze/courier/ArrivedForShift?shiftId="+shiftID);
				else
					url = new URL(serverURL+"/breeze/courier/ArrivedForShift?shiftId="+shiftID+"&presentLocation="+presentLocationStr);
				serverReponse = httpRequestForPost(url);
		    	url = null;
		    } catch (Exception e) {
		          e.printStackTrace();
		    }
			return serverReponse;
		}

		/***************   Accept Shift  ******************/
		public String leavingShift(int shiftID) {
			try {
				URL url = new URL(serverURL+"/breeze/courier/LeavingForShift?shiftId="+shiftID);
	            serverReponse = httpRequestForPost(url);
		    	url = null;
		    } catch (Exception e) {
		          e.printStackTrace();
		    }
			return serverReponse;
		}

	/***************   Get reasons for late delivery  ******************/
	public String getLateDeliveryReason() {
		try {
			URL url = null;
			url = new URL(serverURL+"/breeze/courier/GetLateReasons");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

		/***************   Reason for late delivery  ******************/
		public String reasonForLateDelivery(int bookingId, String reason, int reasonId) {
			try {
				reason = java.net.URLEncoder.encode(reason, "UTF-8");
				URL url = null;
				url = new URL(serverURL+"/breeze/courier/AddReasonForLateDelivery?bookingId="+bookingId+"&reason="+reason+
						"&lateReasonId="+reasonId);
	            serverReponse = httpRequestForPost(url);
		    	url = null;
		    } catch (Exception e) {
		          e.printStackTrace();
		    }
			return serverReponse;
		}

		/***************   Weekly update courier  ******************/
		public String weeklyUpdateCourier() {
			try {
	            URL url = new URL(serverURL+"/breeze/courier/GetWeeklyStatusForCourier?");
		    	serverReponse = httpGetRequest(url);
		    	url = null;
		    }catch(Exception e) {
		        e.printStackTrace();
		    }
			return serverReponse;
		}

    /********** Get Courier Deliveries Count ************/
    public String getCourierDeliveriesCount() {
        try {
            URL url = new URL(serverURL+"/breeze/courier/GetCourierDeliveriesCount?");
            serverReponse = httpGetRequest(url);
            try {
                JSONObject jObjOfCourierDeliverCount = new JSONObject(serverReponse);
				ACTIVEBOOKING_COUNT = jObjOfCourierDeliverCount.getInt("activeDeliveries");
     //           NEWBOOKING_COUNT = jObjOfCourierDeliverCount.getInt("newDeliveries");
                jObjOfCourierDeliverCount = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("", "------------    Courier delivery count ====   "+serverReponse);
            url = null;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return serverReponse;
    }

    //******** Deduct Active deliveries count ***********
    public static void reCountActiveBookings (int deductActiveDeliveryCount, int needToAddOrDeduct){
		if (needToAddOrDeduct == 2)
    		ACTIVEBOOKING_COUNT = ACTIVEBOOKING_COUNT - deductActiveDeliveryCount;
		else if (needToAddOrDeduct == 3) {
			ACTIVEBOOKING_COUNT = ACTIVEBOOKING_COUNT + deductActiveDeliveryCount;
		}
	}

	/***************   Get booking for chat list ******************/
	public String getBookingForChatList() {
		try {
			URL url = new URL(serverURL+"/breeze/courier/DeliveriesToChatAbout");
			serverReponse = httpGetRequest(url);
			url = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling getQuoteRequestForCustomer
	public String getQuoteRequestOfCourier() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/GetQuoteRequestsForCourier");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling getQuoteRequestForCustomerDetails
	public String getCourierMyAcceptOffer(int OfferId, String priceCourier, String etaForPickup, String etaForDropOff, String notesStr, int bidActivePeriod) {
		try {
			URL url;
			try{
				if (!notesStr.equals("")) {
					notesStr = java.net.URLEncoder.encode(notesStr, "UTF-8").replace(" ", "%20");
					url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/SaveCourierQuotation?offerId=" + OfferId + "&price=" + priceCourier + "&pickupEta=" + etaForPickup+ "&dropEta=" + etaForDropOff+"&bidActivePeriod="+bidActivePeriod+"&notes=" + notesStr);
				} else
					url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/SaveCourierQuotation?offerId=" + OfferId + "&price=" + priceCourier+"&pickupEta="+etaForPickup+ "&dropEta=" + etaForDropOff+"&bidActivePeriod="+bidActivePeriod);
			}catch(Exception e){
				e.printStackTrace();
				url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/SaveCourierQuotation?offerId=" + OfferId + "&price=" + priceCourier+"&pickupEta="+etaForPickup+ "&dropEta=" + etaForDropOff+"&bidActivePeriod="+bidActivePeriod);
			}
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling getQuoteRequestForCustomerDetails
	public String getQuoteRequestOfCustomerDetails(int requestId) {
		try {
			URL url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/GetQuoteRequestsDetailForCourier?requestId=" + requestId);
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//************** Cancel bid request ***************
	public String cancelBidRequest(int offerID) {
		try {
			URL url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/CancelBid?offerId=" + offerID);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling getQuoteRequestForCustomerDetails
	public String notifyCustomerAboutChatMessage(String customerId, String msgTxt, int isBidChat) {
		try {
			URL url = new URL(serverURL + "/breeze/courier/NotifyCustomerAboutChatMessage?customerId=" + customerId + "&text=" + msgTxt+"&isBidRequest="+isBidChat);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling Get DHL booking suburb
	public String getDHLBookingSuburb() {
		try {
			URL url = new URL(serverURL + "/breeze/courier/BookingCountsBySuburbForDHL");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling Courier Login Acceptance
	public String addCourierLoginAcceptance() {
		try {
			URL url = new URL(serverURL + "/breeze/courier/AddCourierLoginAcceptance");
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling Available run offer from batch id
	public String getAvailableDeliveryRunOffers(int batchID) {
		try {
			URL url = new URL(serverURL + "/breeze/courier/GetAvailableDeliveryRunOffers?runBatchId="+batchID);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//**************** Start ********** Old Barcode API for barcode  (Made by Sarita)******************

	//webService for calling barcode
	public String postBarcode(String barcode) {
		try {
			URL url = null;
			if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
					!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
				String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
				url	= new URL(serverURL + "/breeze/courier/PickupDeliveryByAWB?awb="+barcode+"&location="+currentLocation);
			}else
				url	= new URL(serverURL + "/breeze/courier/PickupDeliveryByAWB?awb="+barcode);

			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling GetCourierRouteDetails
	public String getCourierRouteDetails() {
		try {
			//	URL url = new URL(serverURL + "/breeze/courier/GetCourierBookingRouteData");       //********* Old made by sarita
			URL url = new URL(serverURL + "/api/mobile/courier/GetRunDeliveriesListForCourier"); //********* New made by Chris
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//webService for calling Get Courier Deliveries Route Data
	public String getCourierDeliveriesRouteData() {
		try {
			URL url = new URL(serverURL + "/breeze/courier/GetCourierDeliveriesRouteData");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//************* Call service to get detail of Active booking ************
	public String getActiveBookingDetailByID(int bookingID){
		try {
			// **Old API**		URL url = new URL(serverURL + "/breeze/courier/GetDeliveryByBookingId?bookingId="+bookingID);
			URL url = new URL(serverURL + "/api/mobile/courier/GetDeliveryByBookingId?bookingId="+bookingID);
			serverReponse = httpGetRequest(url);
			resetBookingId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//************* Call service to get detail of new booking notification ************
	public String getBookingDetailByID(int bookingID){
		try {
			URL url = new URL(serverURL + "/breeze/courier/GetActiveBookingDetailsById?bookingId="+bookingID);
			serverReponse = httpGetRequest(url);
			resetBookingId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//************ WebService for calling get booking info by AWB
	public String getBasicBookingInfoByAwb(String scannedAWBNumber) {
		try {
			URL url = new URL(serverURL + "/breeze/courier/GetBasicBookingInfoByAwb?awb="+scannedAWBNumber);
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	/********  Return multiple DHL bookings from Slide menu scanner (Changed by Chris) ***********/
	public String multipleReturnDeliveries(int isForReturnToPickup, String multipleBookingIdStrToReturn){

        /* *******  isForReturnToPickup = 0    for Return multiple deliveries for DHL booking
        **********  isForReturnToPickup = 1    for Return to pickup for DHL booking
        **********  isForReturnToPickup = 2    for Return to pickup for Normal bookings
        */
		serverReponse = "";
		LoginZoomToU.isLoginSuccess = 0;
		try {
			URL url = null;
			if (isForReturnToPickup == 1)
				url = new URL(serverURL+"/api/mobile/courier/ReturnToPickup");
            else if (isForReturnToPickup == 2)
                url = new URL(serverURL+"/breeze/courier/ReturnToPickup");
			else
				url = new URL(serverURL+"/api/mobile/courier/ReturnDeliveries");

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(multipleBookingIdStrToReturn);
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				if (conn.getResponseCode() == 500)
					LoginZoomToU.isLoginSuccess = 2;			//******** Set for Invalid barcode or not exist
				else
					LoginZoomToU.isLoginSuccess = 3;			//******** When scan parcel of other courier
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			url = null;
			sb = null;
			br = null;
			conn.disconnect();

		} catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;         //******** Set for Network failure
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;		 //******** Set for Network failure
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 4;		//******** Set for request failure
		}
		return serverReponse;
	}

//****************Start ************* New API for Barcode scanning (Made by Chris)**********************//

	//********* WebService for pickup package by Piece/AWB using barcode scan ***********
	public String pickUpPackageByBarcode(String barcode) {
		serverReponse = "";
		LoginZoomToU.isLoginSuccess = 0;
		try {
			URL url = null;
			if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
					!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
				String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
				url	= new URL(serverURL + "/api/mobile/courier/PickupPackageByBarcode?barcode="+barcode+"&location="+currentLocation);
			}else
				url	= new URL(serverURL + "/api/mobile/courier/PickupPackageByBarcode?barcode="+barcode);

			HttpURLConnection conn = null;
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				if (conn.getResponseCode() == 500)
					LoginZoomToU.isLoginSuccess = 2;			//******** Set for Invalid barcode or not exist
				else if (conn.getResponseCode() == 400)
					LoginZoomToU.isLoginSuccess = 3;
				else
					LoginZoomToU.isLoginSuccess = 5;
				//******** When scan parcel of other courier
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			serverReponse = sb.toString();
			url = null;
		} catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;         //******** Set for Network failure
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;		 //******** Set for Network failure
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 4;		//******** Set for request failure
		}
		return serverReponse;
	}

	//********* WebService to get booking info by Piece/AWB using barcode scan ***********
	public String getDeliveryInfoByBarcode(String scannedAWBNumber, int runID) {
		serverReponse = "";
		URL url;
		LoginZoomToU.isLoginSuccess = 0;
		try {
			if(runID==0)
			 url = new URL(serverURL + "/api/mobile/courier/GetDeliveryByBarcode?barcode="+scannedAWBNumber);
			else
				url = new URL(serverURL + "/api/mobile/courier/GetDeliveryByBarcode?barcode="+scannedAWBNumber+"&runId="+runID);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				if (conn.getResponseCode() == 500)
					LoginZoomToU.isLoginSuccess = 2;			//******** Set for Invalid barcode or not exist
				else
					LoginZoomToU.isLoginSuccess = 3;			//******** When scan parcel of other courier
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			serverReponse = sb.toString();

			Log.e("","------------- Response code =  "+conn.getResponseCode()+"------ Response ------ "+serverReponse);

			url = null;
			sb = null;
			br = null;
			conn.disconnect();
			url = null;
		} catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;         //******** Set for Network failure
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;		 //******** Set for Network failure
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 4;		//******** Set for request failure
		}
		return serverReponse;
	}

	/********  DropDelivery by barcode (Changed by Chris) ***********/
	public String dropDeliveryUsingBarcode(String contentStrToBeSend){
		serverReponse = "";
		LoginZoomToU.isLoginSuccess = 0;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/DropDelivery");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(contentStrToBeSend);
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				if (conn.getResponseCode() == 500)
					LoginZoomToU.isLoginSuccess = 2;			//******** Set for Invalid barcode or not exist
				else
					LoginZoomToU.isLoginSuccess = 3;			//******** When scan parcel of other courier
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			url = null;
			sb = null;
			br = null;
			conn.disconnect();

		} catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;         //******** Set for Network failure
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;		 //******** Set for Network failure
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 4;		//******** Set for request failure
		}
		return serverReponse;
	}

	//**************** Finish *********** ******** New API for Barcode scanning (Made by Chris)**********************//


	//*************** webService for call custom notification reply
	public String repliedCustomNotification(int notificationId, int eventID, boolean isRepliedYes) {
		try {
			URL url	= new URL(serverURL + "/breeze/courier/AuditCustomNotificationResponse?notificationId="+notificationId+"&eventId="+eventID+"&action="+isRepliedYes);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//*************** webService for call achievement notification ***********
	public String getAchievementById(int achievementId) {
		try {
			URL url	= new URL(serverURL + "/breeze/courier/GetAchievementById?achievementId="+achievementId);
			serverReponse = httpGetRequest(url);
			resetAchievementId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//********** Reset achievement id from preference after getting detail ************//
	void resetAchievementId(){
		PushReceiver.loginEditorForPushy.putString("achievementId", "0");
		PushReceiver.loginEditorForPushy.commit();
	}

	//************* Webservice to get achievement list **************
    public String getAchivements() {
        String getResponse = "";
        try {
            URL url = null;
            url = new URL(serverURL + "/breeze/courier/GetAchievements");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
    }


	//*************** webService for Add Bio to server
	public String updateBioToServer(String bioTxtStr) {
		HttpURLConnection conn = null;
		try {
			URL url = null;
			url = new URL(serverURL+"/breeze/courier/UpdateCourierBio");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setDoOutput(true);
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(bioTxtStr);
			wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();
			url = null;
			sb = null;
			br = null;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}

	//*********** Send Accept notes time stamp to server for Pick and Drop ***********
	public String addAcceptNotesTimeStamp (int bookingID, boolean isDropNotes) {
		try {
			URL url = new URL(serverURL + "/breeze/courier/AddAcceptNotes?bookingId="+bookingID+"&isAcceptDropNotes="+isDropNotes);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//*********** Save flag for vehicle agreement ***********
	public String saveVehicleAgreementForDhl (int bookingID) {
		try {
			URL url = new URL(serverURL + "/breeze/courier/SaveVehicleAgreementForDhl?bookingId="+bookingID);
			serverReponse = httpRequestForPost(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//  Update Courier Profile Detail
	public String sendDriverReportedIssue(String sendDriverReportedIssueStr){
		HttpURLConnection conn = null;
		try {
			URL url = null;
			url = new URL(serverURL+"/breeze/courier/ReportIssue");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(sendDriverReportedIssueStr);
			wr.flush();

			// Get the server response
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null){
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();

			url = null;
			sb = null;
			br = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}

//************ Http get request with response code ***********
	public String httpGetRequestWithResponseCode (URL url) {
		LoginZoomToU.isLoginSuccess = 0;
		String getResponse = "";
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			} else {
				LoginZoomToU.isLoginSuccess = 2;
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}
			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			getResponse = sb.toString();
		}catch (UnknownHostException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch (SocketTimeoutException e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 1;
		}catch(Exception e){
			e.printStackTrace();
			LoginZoomToU.isLoginSuccess = 3;
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return getResponse;
	}

	//********* WebService for call data for summary report ***********
	public String getCourierSummaryReport() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/courier/GetCourierSummaryReport");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

    public String rejectBidOfCourier(int offerIdToRejectBid) {
        serverReponse = "";
        try {
            URL url = new URL(serverURL + "/breeze/ExtraLargeQuoteRequest/RejectBid?offerId="+offerIdToRejectBid);
            serverReponse = httpRequestForPost(url);
            url = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverReponse;
    }

	// Courier logout
	public String courierLogout() {
		try {
			serverReponse = "";
			URL url = new URL(serverURL+"/breeze/courier/LogoutCourier");
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

public String getToReturnOrTotalDropCount (){
		serverReponse = "";
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/GetDhlDeliveriesInfo");
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

    //********* Courier counter offer (Courier suggest price) API*********
	public String submitCouriersSuggestPrice(int bookingIDForReject, String suggestPrice,int bidActivePeriodInterval) {
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/bookings/"+bookingIDForReject+"/counteroffer?amount="+suggestPrice+"&validForMinutes="+bidActivePeriodInterval);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//********* Create team member API if courier is not a part of a team *********
	public String createATeamMember(){
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/team/create");
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
			serverReponse = "";
		}
		return serverReponse;
	}

	//********* Create team member API if courier is not a part of a team *********
	public String inviteTeamMember(String inviteMemberRequestBody){
		serverReponse = "";
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/team/members/invite");
			serverReponse = httpPostWithRequestBodyWithResponseCode(url, inviteMemberRequestBody);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
			serverReponse = "";
		}
		return serverReponse;
	}

	//********* My team list API which returns all the team member *********
	public String getMyTeamList() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/api/mobile/courier/team/members");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//********* Assign booking to other team member *********
	public String assignBookingToOtherTeamMember(int bookingIDToAssign, String courierId) {
		try {
			URL url = new URL(serverURL+"/api/mobile/courier/bookings/"+bookingIDToAssign+"/reassign?toCourierId="+courierId);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//********* Get Booking list of all team member *********
	public String getBookingList() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/api/mobile/courier/bookings/bookinglist");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

    //********* Courier setting updates add leader margin and Notification enabling *********
    public String courierSettingUpdates (Double margin, boolean isNotify) {
        try {
            URL url = new URL(serverURL+"/breeze/courier/SaveCarrierCourierSetting?margin="+margin+"&AllowTeamMembersToReceiveOffers="+isNotify);
            serverReponse = httpPostRequestWithResponseStatus(url);
            url = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverReponse;
    }

	//********* Get Booking list of all team member *********
	public String getLateDeliveryBookingDetail(int bookingId) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/api/mobile/courier/GetDeliverySummaryByBookingId?bookingId="+bookingId);
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//  Booking Status Request Courier response about ontime or late
	public String ontimeOrLate_BSR_CourierRespone(String bsr_courierresponse){
		HttpURLConnection conn = null;
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/LateDelivery");
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(15000);
			conn.setReadTimeout(20000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(bsr_courierresponse);
			wr.flush();

			// Get the server response
			BufferedReader br;
			if (conn.getResponseCode() == 200) {
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				serverReponse = "success";
				return serverReponse;
			} else {
				LoginZoomToU.isLoginSuccess = 2;
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null){
				// Append server response in string
				sb.append(line);
			}

			serverReponse = sb.toString();

			url = null;
			sb = null;
			br = null;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return serverReponse;
	}

	public String uploadAlcoholSignatureImage(Bitmap signatureImage, String imageName) {
		String serverReponse = "0";
		try {
			DefaultHttpClient client = new DefaultHttpClient();
			//*********** Staging Server - To resolve SSL exception when upload image	**********
			HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
			SchemeRegistry registry = new SchemeRegistry();
			SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
			socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
			registry.register(new Scheme("https", socketFactory, 443));
			SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
			DefaultHttpClient httpClient = new DefaultHttpClient(mgr, client.getParams());
			client = null;
			//*****************************************************************************
			HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			signatureImage.compress(CompressFormat.PNG, 100, bos);
			byte[] data = bos.toByteArray();
			HttpPost postRequest = new HttpPost(serverURL+"/api/mobile/courier/bookings/SaveAlcoholDeliveriesRelatedData");
			postRequest.setHeader("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));

			ByteArrayBody bab = new ByteArrayBody(data, imageName);
			MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			reqEntity.addPart("userfile1", bab);
			reqEntity.addPart("photoCaption", new StringBody("---------------------\\\\sfsdfsdf"));

			postRequest.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(postRequest);
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String sResponse;
			StringBuilder s = new StringBuilder();

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}

			serverReponse = s.toString();
		}catch (Exception e) {
			serverReponse = "0";
			Log.e(e.getClass().getName(), e.getMessage());
		}
		return serverReponse;
	}

public String uploadChat_VoiceMessage(File file) {

		HttpURLConnection connection;
		DataOutputStream dataOutputStream;
		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "*****";

		int bytesRead,bytesAvailable,bufferSize;
		byte[] buffer;
		int maxBufferSize = 1 * 1024 * 1024;
			try{
				FileInputStream fileInputStream = new FileInputStream(file);
				URL url = new URL(serverURL + "/api/mobile/courier/chat/voice-message");
				connection = (HttpURLConnection) url.openConnection();
				connection.setDoInput(true);//Allow Inputs
				connection.setDoOutput(true);//Allow Outputs
				connection.setUseCaches(false);//Don't use a cached Copy
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Authorization", "Bearer " + LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
				connection.setRequestProperty("Connection", "Keep-Alive");
				connection.setRequestProperty("ENCTYPE", "multipart/form-data");
				connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("userfile", "recording.mp3");

				//creating new dataoutputstream
				dataOutputStream = new DataOutputStream(connection.getOutputStream());

				//writing bytes to data outputstream
				dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"userfile\";filename=\""
                        + "recording.mp3" + "\"" + lineEnd);

				dataOutputStream.writeBytes(lineEnd);

				//returns no. of bytes present in fileInputStream
				bytesAvailable = fileInputStream.available();
				//selecting the buffer size as minimum of available bytes or 1 MB
				bufferSize = Math.min(bytesAvailable,maxBufferSize);
				//setting the buffer as byte array of size of bufferSize
				buffer = new byte[bufferSize];

				//reads bytes from FileInputStream(from 0th index of buffer to buffersize)
				bytesRead = fileInputStream.read(buffer,0,bufferSize);

				//loop repeats till bytesRead = -1, i.e., no bytes are left to read
				while (bytesRead > 0){
					//write the bytes read from inputstream
					dataOutputStream.write(buffer,0,bufferSize);
					bytesAvailable = fileInputStream.available();
					bufferSize = Math.min(bytesAvailable,maxBufferSize);
					bytesRead = fileInputStream.read(buffer,0,bufferSize);
				}

				dataOutputStream.writeBytes(lineEnd);
				dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

				int serverResponseCode = connection.getResponseCode();

				BufferedReader br;
				if (serverResponseCode == 200)
					br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				else
					br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				StringBuilder sb = new StringBuilder();
				String line = null;

				while ((line = br.readLine()) != null) {
					// Append server response in string
					sb.append(line);
				}
				serverReponse = sb.toString();

				Log.i(">>>>>>>> ", "Server Response is: " + serverReponse + ": " + serverResponseCode);

				//closing the input and output streams
				fileInputStream.close();
				dataOutputStream.flush();
				dataOutputStream.close();

			} catch (Exception e) {
				e.printStackTrace();
			}
			return serverReponse;
	}

	//get run details by runId
	public String getRunDetails(String runId)
	{
		try {
			URL url = null;
				runId = java.net.URLEncoder.encode(runId, "UTF-8");
				url = new URL(serverURL+"/api/mobile/courier/runs/"+runId);
			serverReponse = httpGetRequest(url);
			resetRunId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
			resetRunId();
		}
		return serverReponse;
	}

	public String getUnAllocateRuns() {

		try {
			URL url;
			url = new URL(serverURL+"/api/mobile/courier/run-batches/unallocated-runs");
			serverReponse = httpGetRequest(url);

		} catch (Exception e) {
			e.printStackTrace();

		}
		return serverReponse;
	}


	//get run batch details by runId
	public String getRunBatchDetails(String runBatchId)
	{
		try {
			URL url = null;
			runBatchId = java.net.URLEncoder.encode(runBatchId, "UTF-8");
			url = new URL(serverURL+"/api/mobile/courier/run-batches/"+runBatchId+"/available-runs");
			serverReponse = httpGetRequest(url);
			resetRunBatchId();
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
			resetRunBatchId();
		}
		return serverReponse;
	}

	//get run batch details by runId
	public ApiResponseModel getRandomRunBatchDetails(String runBatchId)
	{
		ApiResponseModel serverReponse=null;
		try {
			URL url = null;
			runBatchId = java.net.URLEncoder.encode(runBatchId, "UTF-8");
			url = new URL(serverURL+"/api/mobile/courier/run-batches/"+runBatchId+"/accept-best-run");
			serverReponse = httpRequestForPostWithErrorBodyMsg(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public ApiResponseModel suggestAPriceRun(String price,int deliveryRunId,String startSuburb,double possibleEarnings,int stops)
	{
		ApiResponseModel serverReponse=null;
		try {

			URL url = null;
			url = new URL(serverURL+"/breeze/courier/DeliveryRunCounterOffer?price="+price+"&deliveryRunId="+deliveryRunId+"&startSuburb="+startSuburb+"&originalValue="+possibleEarnings+"&Stops="+stops);
			serverReponse = httpRequestForPostWithErrorBodyMsg(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}


	public ApiResponseModel acceptRunByRunId(String runId)
	{
		ApiResponseModel serverReponse=null;
		try {

			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/runs/"+runId+"/accept");
			 serverReponse = httpRequestForPostWithErrorBodyMsg(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getBookingListByRunId(String runId)
	{
		try {
			URL url = null;
			runId = java.net.URLEncoder.encode(runId, "UTF-8");
			url = new URL(serverURL+"/api/mobile/courier/runs/getDeliveriesForRun?RunId="+runId);
			serverReponse = httpGetRequest(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//  Call for http post method
	private ApiResponseModel httpRequestForPostWithErrorBodyMsg (URL urlHttpPost) {
		ApiResponseModel responseStrForHttpPost = new ApiResponseModel();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) urlHttpPost.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.flush();

			// Get the server response
			responseStrForHttpPost.setCode(conn.getResponseCode());
			BufferedReader br;
			if (conn.getResponseCode()==200)
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			else
			if (conn.getResponseCode()==400)
				 br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			else if (conn.getResponseCode()==500)
			{
				responseStrForHttpPost.setResponse("Internal Server Error");
				return responseStrForHttpPost;
			}else
			{
				responseStrForHttpPost.setResponse("something went wrong.");
				return  responseStrForHttpPost;
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			responseStrForHttpPost.setResponse(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}
	//  Call for http post method
	private ApiResponseModel httpRequestForPostWithErrorBodyMsgWithBody (URL urlHttpPost,String multipleBookingIdStr) {
		ApiResponseModel responseStrForHttpPost = new ApiResponseModel();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) urlHttpPost.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "Bearer "+LoginZoomToU.prefrenceForLogin.getString("accessToken", null));
			conn.setConnectTimeout(20000);
			conn.setReadTimeout(30000);
			conn.setDoOutput(true);
			OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
			wr.write(multipleBookingIdStr);
			wr.flush();

			// Get the server response
			responseStrForHttpPost.setCode(conn.getResponseCode());
			BufferedReader br;
			if (conn.getResponseCode()==200)
				br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			else
			if (conn.getResponseCode()==400)
				br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
			else if (conn.getResponseCode()==500)
			{
				responseStrForHttpPost.setResponse("Internal Server Error");
				return responseStrForHttpPost;
			}else
			{
				responseStrForHttpPost.setResponse("something went wrong.");
				return  responseStrForHttpPost;
			}

			StringBuilder sb = new StringBuilder();
			String line = null;

			while ((line = br.readLine()) != null) {
				// Append server response in string
				sb.append(line);
			}
			responseStrForHttpPost.setResponse(sb.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if (conn != null)
				conn.disconnect();
		}
		return responseStrForHttpPost;
	}


    public String getViewOffer(int bookingId) {
		try {
			URL url = new URL(serverURL + "/api/mobile/courier/bookings/"+bookingId+"/getcounteroffers");
			serverReponse = httpGetRequest(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
    }

	public String getInvoiceDetails(String date,int pageNumber) {
		try {
			URL url = null;
			url = new URL(serverURL+"/api/mobile/courier/GetCourierAdjustments?courierId="+LoginZoomToU.courierID+"&approvedDateUtc="+date+"&pageNumber="+pageNumber);
			Log.d("invoiceResponse","Requesting: "+url.toString());
			///api/mobile/courier/GetCourierAdjustments?courierId=e6ac2296-0d49-48c5-a61f-e212021354fe&approvedDateUtc=2023-02-28&pageNumber=1
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	//Community Section
	//==============================================================================================
	public String getCommunitymemberlist() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/MemberList");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunityReceivedInvitations() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/ReceivedInvitationList");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitySentInvitations() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/SentInvitationList");
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitySearchCouriers(String email) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/SearchCouriers?input="+email);
			serverReponse = httpGetRequestWithResponseCode(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunityaddmember(String courierid) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/AddMember?courierId="+courierid);
			serverReponse = httpPostRequestWithResponseStatus(url);
			Log.e("AddMembers","AddMembers Responce : "+serverReponse);

			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}


	public String getCommunityupdatemember(String id,String nickname) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/UpdateMember?id="+id+"&nickName="+nickname);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunityacceptmember(int id) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/AcceptInvitation?id="+id);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunityrejectmember(String courierid) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/DeleteMember?courierId="+courierid);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitysentinvitationdeletemember(String courierid) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/DeleteMember?courierId="+courierid);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitydeletemembers(String courierid) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/DeleteMember?courierId="+courierid);
			serverReponse = httpPostRequestWithResponseStatus(url);
			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunityofferbookinglist() {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/OfferBookingList");
			serverReponse = httpGetRequestWithResponseCode(url);

			Log.e("CommunityOfferBooking", "GetList response  ==   "+serverReponse);

			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitybookingofferAcceptBooking(int offerid,int bookingid) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/AcceptBooking?OfferId="+offerid+"&BookingId="+bookingid);
			serverReponse = httpPostRequestWithResponseStatus(url);

			Log.e("CommunityOfferBooking", "Accepted response  ==   "+serverReponse);

			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String getCommunitybookingofferRejectBooking(int offerid,int bookingid ) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/RejectBooking?OfferId="+offerid+"&BookingId="+bookingid);
			serverReponse = httpPostRequestWithResponseStatus(url);
			Log.e("CommunityOfferBooking", "Rejected response  ==   "+serverReponse);

			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}

	public String SendBookingOfferToCommunityMember(int bookingIDToAssign, String courierId) {
		serverReponse = "";
		try {
			URL url = new URL(serverURL + "/breeze/Community/SendBookingOfferToCommunityMember?bookingId="+bookingIDToAssign+"&memberId="+courierId);
			serverReponse = httpPostRequestWithResponseStatus(url);
			Log.e("AssignBooking", "AssigningBooking response  ==   "+serverReponse);

			url = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serverReponse;
	}
}
