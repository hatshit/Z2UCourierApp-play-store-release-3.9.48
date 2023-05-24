package com.zoom2u.build_me_route;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.newnotfication.view.DirectionJsonParser;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar_build;
import com.zoom2u.offer_run_batch.ApiResponseModel;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.GetAddressFromGoogleAPI;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.lib.jackson.core.JsonProcessingException;
import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class SecondBuildRouteActivity extends Activity implements View.OnClickListener {
    Window window;
    CountDownTimer countDownTimer;
    TextView timer;
    Button accept_route;
    //ImageView back;
    BuildMeRouteModel buildMeRouteModel;
    Double CurrentLat, CurrentLang;
    ProgressDialog progressDialog;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    SharedPreferences sh;
    String bookingsForReturnStr;
    public static HashSet<Integer> arrayOfBookingId ;
    RelativeLayout headerLayoutAllTitleBar;

    @Override
    protected void onPause() {
        super.onPause();
       // PushReceiver.IsSignatureScreenOpen =false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsSignatureScreenOpen =true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_build_route);
        PushReceiver.IsSignatureScreenOpen =true;
        sh = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        CurrentLat=Double.parseDouble(sh.getString("latitude","0.0"));
        CurrentLang=Double.parseDouble(sh.getString("Longitude","0.0"));
        headerLayoutAllTitleBar = findViewById(R.id.headerLayoutAllTitleBar);
        window = SecondBuildRouteActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        window.setStatusBarColor(Color.parseColor("#374350"));
        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }
        timer = findViewById(R.id.time);
        //back = findViewById(R.id.back);
        //back.setOnClickListener(this);
        buildMeRouteModel = (BuildMeRouteModel) getIntent().getSerializableExtra("BuildMeRoute");
        arrayOfBookingId=new HashSet<>();
        for(int i=0;i<buildMeRouteModel.getDeliveries().size();i++){
            arrayOfBookingId.add(buildMeRouteModel.getDeliveries().get(i).getBookingId());
        }

        try {
            Map<String, Object> mapObject = new HashMap<>();
            mapObject.put("bookingIds",arrayOfBookingId);
            ObjectMapper objectMapper = new ObjectMapper();
            bookingsForReturnStr = objectMapper.writeValueAsString(mapObject);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        progressDialog = new ProgressDialog(SecondBuildRouteActivity.this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialog);
        Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
        accept_route = findViewById(R.id.accept_route);
        accept_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                AcceptRoute(bookingsForReturnStr);
            }
        });
        findViewById(R.id.reject_route).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new RejectRoute().execute();
                RejectRoute();
            }
        });
        startTimer();
        setDataInView();

    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {
            if (CurrentLat!=0.0 &&CurrentLang!=0.0) {
                LatLng pick_latLng = new LatLng(CurrentLat,CurrentLang);
                BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(pick_latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_drop)));
            }
        } else {
            requestPermissions();
        }
    }


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (CurrentLat!=0.0 &&CurrentLang!=0.0) {
                    LatLng pick_latLng = new LatLng(CurrentLat,CurrentLang);
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(pick_latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_drop)));
                }
            } else {
                Toast.makeText(SecondBuildRouteActivity.this, "This option is unavailable while location services are turned off or restricted.\n" +
                        "Please go to your phone setting to turn on your location services and ensure that the Zoom2u app has permissions enabled.", Toast.LENGTH_SHORT).show();
                if (CurrentLat!=0.0 &&CurrentLang!=0.0) {
                    LatLng pick_latLng = new LatLng(CurrentLat,CurrentLang);
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(pick_latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_drop)));
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void setDataInView() {
        try {
            ((TextView) findViewById(R.id.distance)).setText((int) (buildMeRouteModel.getTotalDistanceMetres() / 1000) + " Km");
            ((TextView) findViewById(R.id.total_bookings)).setText(buildMeRouteModel.getDeliveries().size() + "");
            ((TextView) findViewById(R.id.tv_pickup_address)).setText(buildMeRouteModel.getPickupLocation().getFullAddress());
            ((TextView) findViewById(R.id.tv_pickup_time)).setText("$" + buildMeRouteModel.getTotalCourierPrice());
            ((TextView) findViewById(R.id.tv_deliver_by)).setText(buildMeRouteModel.getEstimatedEndTime());
            ((TextView) findViewById(R.id.tv_drop_address)).setText(buildMeRouteModel.getDeliveries().get(buildMeRouteModel.getDeliveries().size() - 1).getFullAddress());
            inItNewBookingDetailMapFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void AcceptRoute(String bookingsForReturnStr){
        final ApiResponseModel[] response = new ApiResponseModel[1];
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialog != null)
                    progressDialog.show();
                Custom_ProgressDialogBar_build.inItProgressBar(progressDialog,"Accepting run...");
            }

            @Override
            public void doInBackground() {


                 response[0] = new WebserviceHandler().AcceptRoute1(bookingsForReturnStr);
            }

            @Override
            public void onPostExecute() {
                if (response[0] != null) {
                    if (response[0].getCode() == 200) {
                        Toast.makeText(SecondBuildRouteActivity.this, "Run accepted.", Toast.LENGTH_LONG).show();
                        BookingView.bookingViewSelection = 2;
                        LoginZoomToU.activeBookingTab = 0;
                        ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
                        Intent intent = new Intent(SecondBuildRouteActivity.this, SlideMenuZoom2u.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            JSONObject object = new JSONObject(response[0].getResponse());
                            JSONArray errorsArray = object.getJSONArray("errors");
                            if (errorsArray != null && errorsArray.length() > 0) {
                                String message = errorsArray.getJSONObject(0).getString("message");
                                Toast.makeText(SecondBuildRouteActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception ex) {
                            Toast.makeText(SecondBuildRouteActivity.this, "This route is being cleared and is unavailable right now. Please try again later.", Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                        countDownTimer.start();
                    }
                }
                Custom_ProgressDialogBar_build.dismissProgressBar(progressDialog);
            }
        }.execute();
    }


    private void RejectRoute(){
        final ApiResponseModel[] response = new ApiResponseModel[1];
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialog != null)
                    progressDialog.show();
                Custom_ProgressDialogBar_build.inItProgressBar(progressDialog,"Rejecting run...");
            }

            @Override
            public void doInBackground() {


                response[0] = new WebserviceHandler().RejectRoute();
            }

            @Override
            public void onPostExecute() {
                if (response[0]  != null) {
                    if (response[0] .getCode() == 200) {
                        Toast.makeText(SecondBuildRouteActivity.this, "Run rejected.", Toast.LENGTH_LONG).show();
                        BookingView.bookingViewSelection = 1;
                        ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
                        Intent intent = new Intent(SecondBuildRouteActivity.this, SlideMenuZoom2u.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SecondBuildRouteActivity.this, response[0] .getResponse(), Toast.LENGTH_LONG).show();
                    }


                }

                Custom_ProgressDialogBar_build.dismissProgressBar(progressDialog);
            }
        }.execute();
    }



    //******** Initialize new booking detail view map ***************
    private void inItNewBookingDetailMapFragment() {
        try {
            if (BookingDetail_New.mapFragmentBookingDetail != null)
                BookingDetail_New.mapFragmentBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewABD));
            if (BookingDetail_New.gMapBookingDetail != null)
                BookingDetail_New.gMapBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
                        BookingDetail_New.gMapBookingDetail = BookingDetail_New.mapFragmentBookingDetail.getMap();
                        BookingDetail_New.gMapBookingDetail.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomControlsEnabled(false);
                        BookingDetail_New.gMapBookingDetail.getUiSettings().setMyLocationButtonEnabled(true);
                        BookingDetail_New.gMapBookingDetail.getUiSettings().setCompassEnabled(false);
                        BookingDetail_New.gMapBookingDetail.getUiSettings().setRotateGesturesEnabled(true);
                        BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomGesturesEnabled(true);

                        getLastLocation();
                        LatLng latLng1 = new LatLng(buildMeRouteModel.getPickupLocation().getLatitude(), buildMeRouteModel.getPickupLocation().getLongitude());
                        BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(latLng1)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin)));
                        BookingDetail_New.gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 12F));

                        try {
                            for (int i = 0; i < buildMeRouteModel.getDeliveries().size(); i++) {
                                /** not show any pin on pick*/
                                if (buildMeRouteModel.getDeliveries().size() == 1) {
                                    LatLng latLng = new LatLng(buildMeRouteModel.getDeliveries().get(i).getLatitude(), buildMeRouteModel.getDeliveries().get(i).getLongitude());
                                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.a_drop)));
                                } else {
                                    if (i == buildMeRouteModel.getDeliveries().size() - 1) {
                                        if (!buildMeRouteModel.getPickupLocation().getLatitude().equals(buildMeRouteModel.getDeliveries().get(i).getLatitude())) {
                                            LatLng latLng = new LatLng(buildMeRouteModel.getDeliveries().get(i).getLatitude(), buildMeRouteModel.getDeliveries().get(i).getLongitude());
                                            BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.a_drop)));
                                        }
                                    } else {
                                        if (!buildMeRouteModel.getPickupLocation().getLatitude().equals(buildMeRouteModel.getDeliveries().get(i).getLatitude())) {
                                            if (!buildMeRouteModel.getDeliveries().get(buildMeRouteModel.getDeliveries().size() - 1).getLatitude().equals(buildMeRouteModel.getDeliveries().get(i).getLatitude())){
                                                LatLng latLng = new LatLng(buildMeRouteModel.getDeliveries().get(i).getLatitude(), buildMeRouteModel.getDeliveries().get(i).getLongitude());
                                               BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin)));
                                        }
                                        }

                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        List<LatLng> list = null;
                        try {
                            list = PolyUtil.decode(buildMeRouteModel.getRoutePolyline());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (list != null)
                            BookingDetail_New.drawRoute(list);
                    }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false&key=" + GetAddressFromGoogleAPI.API_KEY_GEOCODER_DIRECTION;

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }



    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            iStream.close();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(16000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                float sec = millisUntilFinished / 1000 % 60;
                timer.setText(f.format(sec));
            }

            @Override
            public void onFinish() {
                timer.setText("00");
               EndPopup.alertDialogToFinishView(SecondBuildRouteActivity.this);
            }

        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           /* case R.id.back:
                finish();
                break;*/

        }
    }
}