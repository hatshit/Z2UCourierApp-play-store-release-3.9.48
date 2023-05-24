package com.zoom2u.build_me_route;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zoom2u.MainActivity;

import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar_build;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.offer_run_batch.ApiResponseModel;
import com.zoom2u.webservice.WebserviceHandler;
import android.Manifest;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FirstBuildMeActivity extends Activity implements View.OnClickListener, TimePicker.MyCallback {
    private final int dropAutocompleteRequest = 1018;
    private final int pickAutocompleteRequest = 1019;
    int PERMISSION_ID = 44;
    private TextView drop_add, pick_add;
    TimePicker timePicker;
    ConstraintLayout pick_date_cl, drop_date_cl;
    DateFormat timeFormat;
    TextView drop_date, pick_date;
    Window window;
    ImageView back;
    Date date;
    List<BuildMeRouteModel> buildMeRouteModel;
    String endLocation;
    String startLocation;
    String lat1, lang1;
    ProgressDialog progressDialog1;
    FusedLocationProviderClient mFusedLocationClient;
    RelativeLayout headerLayoutAllTitleBar;
    @Override
    protected void onPause() {
        super.onPause();
        // PushReceiver.IsSignatureScreenOpen =false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsSignatureScreenOpen = true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_build_me);
        PushReceiver.IsSignatureScreenOpen = true;
        window = FirstBuildMeActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        headerLayoutAllTitleBar= findViewById(R.id.headerLayoutAllTitleBar);
        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }

        drop_add = findViewById(R.id.drop_add);
        pick_add = findViewById(R.id.pick_add);
        findViewById(R.id.ending_location).setOnClickListener(this);
        findViewById(R.id.start_location).setOnClickListener(this);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        timePicker = new TimePicker();
        findViewById(R.id.next).setOnClickListener(this);
        pick_date_cl = findViewById(R.id.pick_date_cl);
        drop_date_cl = findViewById(R.id.drop_date_cl);
        pick_date_cl.setOnClickListener(this);
        drop_date_cl.setOnClickListener(this);
        String first = "<font color='#00A6E3'>*Note:</font>";
        String next = "<font color='#3a424f'> if this is empty, we will try and finish your bookings near the 'Home' location in your account profile.</font>";
        TextView note = findViewById(R.id.note);
        note.setText(Html.fromHtml(first + next));

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyDsqlqVQsCmsNdqjp3guok-DfH52YsrRc8");
        }



        drop_date = findViewById(R.id.drop_date);
        pick_date = findViewById(R.id.pick_date);

        setDataInPickDropTime();

        progressDialog1 = new ProgressDialog(FirstBuildMeActivity.this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialog1);
        Custom_ProgressDialogBar.dismissProgressBar(progressDialog1);
        //((TextView) findViewById(R.id.drop_add)).setText(MainActivity.HomeAddress);
        endLocation = MainActivity.HomeAddress;
        lat1 = String.valueOf(MainActivity.HomeLat);
        lang1 = String.valueOf(MainActivity.HomeLang);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void setDataInPickDropTime(){
        date = new Date();
        timeFormat = new SimpleDateFormat("hh:mm aaa");


        drop_date.setText("06:00 PM");
        pick_date.setText(timeFormat.format(date).toUpperCase());
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (progressDialog1 != null)
                    progressDialog1.show();
                Custom_ProgressDialogBar_build.inItProgressBar(progressDialog1,"Getting current location...");
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            getAddFromLatLong(location.getLatitude(), location.getLongitude());

                        }
                    }
                });
            } else {
                DialogActivity.alertDialogView(this, "Alert!", "GPS is turned off, please turn it on to use this feature.");
            }
        } else {
            requestPermissions();
        }
    }

    private void getAddFromLatLong(double lat, double Long) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, Long, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                startLocation = address.getAddressLine(0);
                Custom_ProgressDialogBar_build.dismissProgressBar(progressDialog1);
                pick_add.setText(startLocation);
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                DialogActivity.alertDialogView(this, "Alert!", "Please enable the Location Service to use 'Build a run' function in the App. To enable, please go to Settings>Privacy>Location Services");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            getAddFromLatLong(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    };


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.ending_location:
                dropSearchLocation(dropAutocompleteRequest);
                break;
            case R.id.start_location:
                dropSearchLocation(pickAutocompleteRequest);
                break;
            case R.id.pick_date_cl:
                timePicker.timePickerDialog(this, this, pick_date.getText().toString(), true);
                break;
            case R.id.drop_date_cl:
                timePicker.timePickerDialog(this, this, drop_date.getText().toString(), false);
                break;
            case R.id.next:
                if (checkDropTimeIsGreaterThenPick(pick_date.getText().toString(),drop_date.getText().toString()) && checkStartLocationisblank() )
                DrawRoute();
        }
    }
    @SuppressLint("MissingPermission")
   private boolean checkStartLocationisblank() {
        if (pick_add.getText().equals("")) {
            if (checkPermissions()) {
                if (isLocationEnabled()) {
                    getLastLocation();
                }  else
                    DialogActivity.alertDialogView(this, "Alert!", "GPS is turned off, please turn it on to use this feature.");
               }
            else
                    requestPermissions();
            return false;
            }
        return true;
        }

    private void DrawRoute() {
        //new GetRoute().execute();
        GetRoute();

    }


    private void GetRoute() {
        final ApiResponseModel[] response = {null};
        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                if (progressDialog1 != null)
                    progressDialog1.show();
                Custom_ProgressDialogBar_build.inItProgressBar(progressDialog1,"Building a route...");
            }

            @Override
            public void doInBackground() {
                // String response1= new WebserviceHandler().getRoutes(drop_date.getText().toString(),endLocation,lat1,lang1);
                try {
                    JSONObject request = new JSONObject();

                    request.put("Address", endLocation);
                    request.put("Latitude", lat1);
                    request.put("Longitude", lang1);
                    request.put("EndTime", drop_date.getText().toString());
                    request.put("StartAddress", startLocation);
                    request.put("StartTime", pick_date.getText().toString());
                    response[0] = new WebserviceHandler().getRoute1(request.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPostExecute() {
                try {
                    if (response[0] != null) {
                        if (response[0].getCode() != 200) {
                            Toast.makeText(FirstBuildMeActivity.this, "There are no bookings available for the specified time and route.", Toast.LENGTH_LONG).show();
                        } else {
                            Type listType = new TypeToken<List<BuildMeRouteModel>>() {
                            }.getType();
                            buildMeRouteModel = new Gson().fromJson(response[0].getResponse(), listType);
                            if (buildMeRouteModel != null && buildMeRouteModel.size() != 0) {
                                Intent intent = new Intent(FirstBuildMeActivity.this, SecondBuildRouteActivity.class);
                                intent.putExtra("BuildMeRoute", buildMeRouteModel.get(0));
                                startActivityForResult(intent, 12);
                            } else
                                Toast.makeText(FirstBuildMeActivity.this, "There are no bookings available for the specified time and route.", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception ignored) {
                    Toast.makeText(FirstBuildMeActivity.this, "There are no bookings available for the specified time and route.", Toast.LENGTH_LONG).show();
                }
                Custom_ProgressDialogBar_build.dismissProgressBar(progressDialog1);
            }
        }.execute();
    }

    @Override
    public void onTimeClick(String time, Boolean isPickup) {
        if (isPickup) {
            if (checkPickFutureTime(time))
                pick_date.setText(time.toUpperCase());
        } else {
            if (checkDropTimeIsGreaterThenPick(pick_date.getText().toString(),time))
                drop_date.setText(time.toUpperCase());
        }
    }

    public Boolean checkPickFutureTime(String time){
        DateFormat dateFormat=new SimpleDateFormat("EEE dd MMM yyyy");
        Date date =new Date();
        String serverDateTimeValue=dateFormat.format(date)+" "+time;
        SimpleDateFormat converter=new SimpleDateFormat("EEE dd MMM yyyy hh:mm a");
        Date convertedDate;
        try
        {
            convertedDate=converter.parse(serverDateTimeValue);
            if(System.currentTimeMillis()>convertedDate.getTime()){
                DialogActivity.alertDialogView(this, "Alert!", "Start time can not be set in past, please choose current or a future start time.");
                return false;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    private boolean checkDropTimeIsGreaterThenPick(String startTime,String dropTime) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aaa");
            Date date1 = formatter.parse(startTime);
            Date date2 = formatter.parse(dropTime);

            if (date2.compareTo(date1) < 0) {
                DialogActivity.alertDialogView(this, "Alert!", "Please make sure end time is after the start time for the requested run.");
                return false;
            }

        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        return true;
    }


    private void dropSearchLocation(int requestCode) {
        try {
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("AU")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(this);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            ((Activity) this).startActivityForResult(intent, requestCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getAddressFromLocation(final String locationAddress,
                                       final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List addressList = geocoder.getFromLocationName(locationAddress, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = (Address) addressList.get(0);
                endLocation=locationAddress;
                lat1=String.valueOf(address.getLatitude());
                lang1=String.valueOf(address.getLongitude());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == dropAutocompleteRequest) {
            if (resultCode == RESULT_OK) {
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                Place place = Autocomplete.getPlaceFromIntent(data);
                drop_add.setText(place.getAddress());
                getAddressFromLocation(place.getAddress(),this);
                Log.i("Place API Success", "  -------------- Place -------------" + place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                //drop_add.setText(MainActivity.HomeAddress);
                lat1=String.valueOf(MainActivity.HomeLat);
                lang1=String.valueOf(MainActivity.HomeLang);
                endLocation=MainActivity.HomeAddress;
                Log.i("Place API Failure", "  -------------- Error -------------" + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Place API Failure", "  -------------- User Cancelled -------------");
               //drop_add.setText(MainActivity.HomeAddress);
                lat1=String.valueOf(MainActivity.HomeLat);
                lang1=String.valueOf(MainActivity.HomeLang);
                endLocation=MainActivity.HomeAddress;
            }
        }
        else if (requestCode == pickAutocompleteRequest) {
            if (resultCode == RESULT_OK) {
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                Place place = Autocomplete.getPlaceFromIntent(data);
                pick_add.setText(place.getAddress());
                startLocation=place.getAddress();
                Log.i("Place API Success", "  -------------- Place -------------" + place.getName());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("Place API Failure", "  -------------- Error -------------" + status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                Log.i("Place API Failure", "  -------------- User Cancelled -------------");

            }
        }
        else if(requestCode==12){
            drop_add.setText("");
            getLastLocation();
            endLocation=MainActivity.HomeAddress;
            lat1=String.valueOf(MainActivity.HomeLat);
            lang1=String.valueOf(MainActivity.HomeLang);
            setDataInPickDropTime();
        }
    }




}