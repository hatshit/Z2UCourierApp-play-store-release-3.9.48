package com.zoom2u.slidemenu.courier_routes;

import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.widget.Toast;

import com.newnotfication.view.DirectionJsonParser;
import com.zoom2u.R;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowPolyline_DirectionAPI {

//    public static final String DIRECTION_N_GEOCODE_API_KEY = "AIzaSyCRR7TuIViOwRgFjnxSeNJ3bh9wUSXicaA";
//
//    Context currentViewContext;
//    GoogleMap currentMapView;
//
//    public ShowPolyline_DirectionAPI(Context currentViewContext, GoogleMap currentMapView, LatLng sourceLatLong, LatLng destLatLong) {
//        this.currentViewContext = currentViewContext;
//        this.currentMapView = currentMapView;
//
//        // Getting URL to the Google Directions API
//        String url = getDirectionsUrl(sourceLatLong, destLatLong);
//        DownloadTask downloadTask = new DownloadTask();
//        downloadTask.execute(url);
//        downloadTask = null;
//    }
//
//    private String getDirectionsUrl(LatLng origin, LatLng dest) {
//
//        String url = "";
//        try {
//            // Origin of route
//            String str_origin = "origin=" + origin.latitude + ","+ origin.longitude;
//
//            // Destination of route
//            String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
//
//            // Sensor enabled
//            String sensor = "sensor=false&key="+DIRECTION_N_GEOCODE_API_KEY;
//
//            // Building the parameters to the web service
//            String parameters = str_origin + "&" + str_dest + "&" + sensor;
//
//            // Output format
//            String output = "json";
//
//            // Building the url to the web service
//            url = "https://maps.googleapis.com/maps/api/directions/"+ output + "?" + parameters;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return url;
//    }
//
//    /** A method to download json data from url */
//    private String downloadUrl(String strUrl) throws IOException {
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strUrl);
//
//            // Creating an http connection to communicate with url
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            // Connecting to url
//            urlConnection.connect();
//
//            // Reading data from url
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//
//            br.close();
//
//        } catch (Exception e) {
//            Log.d("Exception url", e.toString());
//        } finally {
//            iStream.close();
//            urlConnection.disconnect();
//        }
//        return data;
//    }
//
//    // Fetches data from url passed
//    private class DownloadTask extends AsyncTask<String, Void, String> {
//        // Downloading data in non-ui thread
//        @Override
//        protected String doInBackground(String... url) {
//
//            // For storing data from web service
//            String data = "";
//
//            try {
//                // Fetching the data from web service
//                data = downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        // Executes in UI thread, after the execution of
//        // doInBackground()
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            try {
//                ParserTask parserTask = new ParserTask();
//                // Invokes the thread for parsing the JSON data
//                parserTask.execute(result);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
//
//    /** A class to parse the Google Places in JSON format */
//    private class ParserTask extends
//            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(
//                String... jsonData) {
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                DirectionJsonParser parser = new DirectionJsonParser();
//
//                // Starts parsing data
//                routes = parser.parse(jObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        // Executes in UI thread, after the parsing process
//        @SuppressWarnings("unused")
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            ArrayList<LatLng> points = null;
//            PolylineOptions lineOptions = null;
//            String distance = "";
//            String duration = "";
//
//            try {
//                if (result.size() < 1) {
//                    return;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            try {
//                // Traversing through all the routes
//                for (int i = 0; i < result.size(); i++) {
//                    points = new ArrayList<LatLng>();
//
//                    if(lineOptions != null)
//                        lineOptions = null;
//                    lineOptions = new PolylineOptions();
//
//                    // Fetching i-th route
//                    List<HashMap<String, String>> path = result.get(i);
//                    // Fetching all the points in i-th route
//                    for (int j = 0; j < path.size(); j++) {
//                        HashMap<String, String> point = path.get(j);
//
//                        if (j == 0) { // Get distance from the list
//                            distance = point.get("distance");
//                            continue;
//                        } else if (j == 1) { // Get duration from the list
//                            duration = point.get("duration");
//                            continue;
//                        }
//                        double lat = Double.parseDouble(point.get("lat"));
//                        double lng = Double.parseDouble(point.get("lng"));
//                        LatLng position = new LatLng(lat, lng);
//                        points.add(position);
//                    }
//
//                    // Adding all the points in the route to LineOptions
//                    lineOptions.addAll(points);
//                    lineOptions.width(6);
//                    lineOptions.color(currentViewContext.getResources().getColor(R.color.gun_metal));
//                }
//                currentMapView.addPolyline(lineOptions);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
}
