package com.zoom2u.webservice;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class GetAddressFromGoogleAPI {
	
	private static final String LOG_TAG = "ExampleApp";
	private static final String GEOCODER_API_BASE = "https://maps.googleapis.com/maps/api/geocode/json?";
	public static final String API_KEY_GEOCODER_DIRECTION = "AIzaSyDsqlqVQsCmsNdqjp3guok-DfH52YsrRc8";    // Purchased account zoom.2ua@gmail.com





	  
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
				sb.append("&address=" + URLEncoder.encode(sourceAddress, "utf8"));
				sb.append("&key="+API_KEY_GEOCODER_DIRECTION);
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
	  

}
