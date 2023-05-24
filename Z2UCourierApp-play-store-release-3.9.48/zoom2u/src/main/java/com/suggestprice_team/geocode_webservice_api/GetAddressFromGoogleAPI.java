package com.suggestprice_team.geocode_webservice_api;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.StrictMode;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GetAddressFromGoogleAPI {
	
	private static final String LOG_TAG = "Z2U Courier App";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
	private static final String DIRECTION_API_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
	private static final String GEOCODER_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?";

	//------------ make your specific key ------------
//	private static final String GEOCODER_API_KEY = "AIzaSyCjbvnC6D8wzGiEANETEkJNYlp6z5hbNoo";	// zoom2u account GoogleMapForWork project
	private static final String API_KEY = "AIzaSyCVkhrXx0Jqp1SMQ74yWjIHMI3UASXT_Bs";   // zoom2u account Place API project

	LatLng p1 = null;

	public GetAddressFromGoogleAPI() {
		super();
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//**********  For Auto complete text
	public static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			
			StringBuilder sb = new StringBuilder(PLACES_API_BASE);
			sb.append("?key=" + API_KEY);
			sb.append("&components=country:au");
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			
			System.out.println("URL: "+url);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			//String[] splittedStrings = new String[predsJsonArray.length()];
			for (int i = 0; i < predsJsonArray.length(); i++) {
				//splittedStrings[i] = predsJsonArray.split(",");
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
				
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}
		
		return resultList;
	}

	  //************ For getting Latitude and longitude from Geocoder
	  public static LatLng getLocationFromAddress(Context context, String strAddress){

		Geocoder coder = new Geocoder(context);
		List<Address> address;
		  LatLng p1 = null;
		try {
		    address = coder.getFromLocationName(strAddress,5);
		    if (address == null) {
		        return null;
		    }
		    Address location = address.get(0);
		    location.getLatitude();
		    location.getLongitude();

		    p1 = new LatLng((int) (location.getLatitude() * 1E6),
		                      (int) (location.getLongitude() * 1E6));

		}catch(Exception e){
			e.printStackTrace();
		}
		return p1;
	}
	  
	  //************  For Calculating distance between two address from Google API
	  public static String getDistance(String sourceAddress, String destinationAddress) {
			String resultStr = "0";
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
			try {
				if(sourceAddress.equals(""))
					sourceAddress = " ";
			} catch (Exception e1) {
				e1.printStackTrace();
				sourceAddress = " ";
			}
			try {
				if(destinationAddress.equals(""))
					destinationAddress = " ";
			} catch (Exception e1) {
				e1.printStackTrace();
				destinationAddress = " ";
			}
			
			HttpURLConnection conn = null;
			StringBuilder jsonResults = new StringBuilder();
			try {
				
				StringBuilder sb = new StringBuilder(DIRECTION_API_BASE);
				sb.append("newForwardGeocoder=true&origin=" + URLEncoder.encode(sourceAddress, "utf8"));
				sb.append("&destination=" + URLEncoder.encode(destinationAddress, "utf8"));
				sb.append("&sensor=false");

				URL url = new URL(sb.toString());
				
				System.out.println("URL: "+url);
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
				Log.e(LOG_TAG, "Error processing Places API URL", e);
				resultStr = "0";
				return resultStr;
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error connecting to Places API", e);
				resultStr = "0";
				return resultStr;
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				resultStr = "0";
			}

			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				JSONArray routesJsonArray = jsonObj.getJSONArray("routes");
				for(int i = 0; i < routesJsonArray.length(); i++){
					JSONObject jOBJofRoutes = routesJsonArray.getJSONObject(i);
					JSONArray legsJsonArray = jOBJofRoutes.getJSONArray("legs");
					for(int j = 0; j < legsJsonArray.length(); j++){
							JSONObject jOBJofLegs = legsJsonArray.getJSONObject(i);
							JSONObject distanceJsonOBJ = jOBJofLegs.getJSONObject("distance");
							resultStr = distanceJsonOBJ.getString("text");
					}
				}
				
			} catch (JSONException e) {
				Log.e(LOG_TAG, "Cannot process JSON results", e);
				resultStr = "0";
			}
			Log.d(LOG_TAG, " results  ========   "+resultStr);
			return resultStr;
		}
	  
	  
	//************  For Getting Address detail from address text using Google GeoCoder API
	  public static HashMap<String, Object> getAddressDetailGeoCoder(String sourceAddress) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
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
				sb.append("newForwardGeocoder=true&address=" + URLEncoder.encode(sourceAddress, "utf8"));
			//	sb.append("&key="+ShowPolyline_DirectionAPI.DIRECTION_N_GEOCODE_API_KEY);
//				sb.append("&components=country:au");
//				sb.append("&sensor=false");

				URL url = new URL(sb.toString());
				
				System.out.println("URL: "+url);
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
				Log.e(LOG_TAG, "Error processing Places API URL", e);
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error connecting to Places API", e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			
			HashMap<String, Object> mapForAllAddressInformation = new HashMap<String, Object>();
			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				if(jsonObj.getString("status").equals("OK")){
					JSONArray resultsJsonArray = jsonObj.getJSONArray("results");
					
					String formatted_address = "-NA-";
					
					// Extracting formatted address, if available
			        if(!resultsJsonArray.getJSONObject(0).isNull("formatted_address")){
			           // formatted_address = resultsJsonArray.getJSONObject(0).getString("formatted_address");
			        	formatted_address = sourceAddress;
			        }
					
			        double latStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
					double longStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
						
					mapForAllAddressInformation.put("address", formatted_address);
					mapForAllAddressInformation.put("latitude", latStr);
					mapForAllAddressInformation.put("longitude", longStr);
						
					JSONArray jArrayOfAddress_Component = resultsJsonArray.getJSONObject(0).getJSONArray("address_components");
					if(jArrayOfAddress_Component.length() > 0){
						for(int j = 0; j < jArrayOfAddress_Component.length(); j++){
							JSONArray jsonArr= jArrayOfAddress_Component.getJSONObject(j).getJSONArray("types");
							
							try {
								if(j == 0){
									if (jsonArr.length() == 0){
										mapForAllAddressInformation.put("defaultName", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("defaultName", "");
							}
							
							try {
								if (jsonArr.getString(0).equals("street_number")){
									mapForAllAddressInformation.put("streetNumber", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("streetNumber", " ");
							}
							try {
								if (jsonArr.getString(0).equals("sublocality")){
									mapForAllAddressInformation.put("sublocality", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("sublocality", " ");
							}
							
							try {
								if (jsonArr.getString(0).equals("route")){
									mapForAllAddressInformation.put("route", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("route", " ");
							}
							try {
								if (jsonArr.getString(0).equals("colloquial_area")){
									mapForAllAddressInformation.put("suburb", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
						
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("suburb", " ");
								
							}
							
							try {
								if (jsonArr.getString(0).equals("locality")){
									mapForAllAddressInformation.put("suburb", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("suburb", " ");
								
							}

							try {
								if (jsonArr.getString(0).equals("administrative_area_level_1")){
									mapForAllAddressInformation.put("state", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("state", " ");
							}
							
							try {
								if (jsonArr.getString(0).equals("administrative_area_level_1")){
									mapForAllAddressInformation.put("stateLong", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("stateLong", " ");
							}
							
							try {
								if (jsonArr.getString(0).equals("country")){
									mapForAllAddressInformation.put("country", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("country", " ");
							}

							try {
								if (jsonArr.getString(0).equals("postal_code")){
									mapForAllAddressInformation.put("postcode", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("postcode", " ");
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Cannot process JSON results", e);
			}
			Log.d(LOG_TAG, " results  ========   "+mapForAllAddressInformation);
			return mapForAllAddressInformation;
		}
	  
	//************  For Getting Address detail from Lat-long using Google GeoCoder API
	  public static HashMap<String, Object> getAddressDetailGeoCoder(String latitudeStr, String longitudeStr) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
			try {
				if(latitudeStr.equals(""))
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
				sb.append("&latlng=" + URLEncoder.encode(latitudeStr, "utf8")+","+ URLEncoder.encode(longitudeStr, "utf8"));

				URL url = new URL(sb.toString());
				System.out.println("URL: "+url);
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
				Log.e(LOG_TAG, "Error processing Places API URL", e);
			} catch (IOException e) {
				Log.e(LOG_TAG, "Error connecting to Places API", e);
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
			}
			
			HashMap<String, Object> mapForAllAddressInformation = new HashMap<String, Object>();
			try {
				// Create a JSON object hierarchy from the results
				JSONObject jsonObj = new JSONObject(jsonResults.toString());
				if(jsonObj.getString("status").equals("OK")){
					JSONArray resultsJsonArray = jsonObj.getJSONArray("results");
					
					String formatted_address = "-NA-";
					
					// Extracting formatted address, if available
			        if(!resultsJsonArray.getJSONObject(0).isNull("formatted_address")){
			            formatted_address = resultsJsonArray.getJSONObject(0).getString("formatted_address");
			        }
					
			        double latStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
					double longStr = resultsJsonArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
						
					mapForAllAddressInformation.put("address", formatted_address);
					mapForAllAddressInformation.put("latitude", latStr);
					mapForAllAddressInformation.put("longitude", longStr);
						
					JSONArray jArrayOfAddress_Component = resultsJsonArray.getJSONObject(0).getJSONArray("address_components");
					if(jArrayOfAddress_Component.length() > 0){
						for(int j = 0; j < jArrayOfAddress_Component.length(); j++){
							JSONArray jsonArr= jArrayOfAddress_Component.getJSONObject(j).getJSONArray("types");
							
							try {
								if (jsonArr.getString(0).equals("street_number")){
									mapForAllAddressInformation.put("streetNumber", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("streetNumber", " ");
							}
							try {
								if (jsonArr.getString(0).equals("route")){
									mapForAllAddressInformation.put("route", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("route", " ");
							}
							try {
								if (jsonArr.getString(0).equals("colloquial_area")){
									mapForAllAddressInformation.put("suburb", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
						
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("suburb", " ");
								
							}
							
							try {
								if (jsonArr.getString(0).equals("locality")){
									mapForAllAddressInformation.put("suburb", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("suburb", " ");
								
							}

							try {
								if (jsonArr.getString(0).equals("administrative_area_level_1")){
									mapForAllAddressInformation.put("state", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("state", " ");
							}
							
							try {
								if (jsonArr.getString(0).equals("administrative_area_level_1")){
									mapForAllAddressInformation.put("stateLong", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("stateLong", " ");
							}
							try {
								if (jsonArr.getString(0).equals("country")){
									mapForAllAddressInformation.put("country", jArrayOfAddress_Component.getJSONObject(j).getString("long_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("country", " ");
							}

							try {
								if (jsonArr.getString(0).equals("postal_code")){
									mapForAllAddressInformation.put("postcode", jArrayOfAddress_Component.getJSONObject(j).getString("short_name"));
								}
							} catch (Exception e) {
								e.printStackTrace();
								mapForAllAddressInformation.put("postcode", " ");
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(LOG_TAG, "Cannot process JSON results", e);
			}
			Log.d(LOG_TAG, " results  ========   "+mapForAllAddressInformation);
			return mapForAllAddressInformation;
		}
	  
	  //************ Get address from geo-coder *************//
	  public static List<Address> addressFromGeoCoder(Activity activity, double lat, double longitude){
		  List<Address> addreelist = null;
		  try{
			  Geocoder geocoder = new Geocoder(activity);
			  addreelist = geocoder.getFromLocation(lat, longitude, 1);
			  geocoder = null;
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return addreelist;
	  }

}
