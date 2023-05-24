package com.zoom2u.offer_run_batch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.offer_run_batch.adapter.RunBatchAdapter;
import com.zoom2u.offer_run_batch.model.RunBatchResponse;
import com.zoom2u.offer_run_batch.model.RunDetailsModel;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RunBatchActivity extends Activity implements View.OnClickListener {
    Button accept_random_run;
    RecyclerView list_recyclerview;
    TextView tv_deliver_by, tv_pickup_time, pickup_address;
    NestedScrollView scrollViewNewBD;
    ImageView backFromBookingDetail;
    TextView tv_pickup_address, secondHeaderTxtBD;
    String runBatchId = "0";

    public WorkaroundMapFragment mapFragmentBookingDetail;
    public GoogleMap gMapBookingDetail;
    ProgressDialog progressDialog;
    List<RunBatchResponse> runDetailsModel;
    RunDetailsModel runDetailsModel1;
    Window window;
    List<String> dropLocations;
    ApiResponseModel AcceptRunresponse;
    String GetRunBatchDetailsresponse;
    ApiResponseModel GetRandomRunBatchDetailsresponse;
    RelativeLayout headerLayoutAllTitleBar;
    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsOtherScreenOpen =false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsOtherScreenOpen =true;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_batch_accept);
        PushReceiver.IsOtherScreenOpen =true;
        headerLayoutAllTitleBar=findViewById(R.id.headerLayoutAllTitleBar);
        window = RunBatchActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        window.setStatusBarColor(Color.parseColor("#374350"));
        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }
        progressDialog = new ProgressDialog(RunBatchActivity.this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialog);
        Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
        backFromBookingDetail = findViewById(R.id.backFromBookingDetail);
        backFromBookingDetail.setOnClickListener(this);
        scrollViewNewBD =  findViewById(R.id.scrollViewNewBD);
        secondHeaderTxtBD = findViewById(R.id.secondHeaderTxtBD);


        tv_pickup_time = findViewById(R.id.tv_pickup_time);
        tv_deliver_by = findViewById(R.id.tv_deliver_by);
        pickup_address = findViewById(R.id.pickup_address);
        tv_pickup_address = findViewById(R.id.tv_pickup_address);

        accept_random_run = findViewById(R.id.accept_random_run);
        accept_random_run.setOnClickListener(this);

        list_recyclerview = findViewById(R.id.list_recyclerview);
        list_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        list_recyclerview.setAdapter(new RunBatchAdapter(RunBatchActivity.this, runDetailsModel, new RunBatchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String runId) {
                AcceptRunAsync(runId);
                // new AcceptRunAsync().execute(runId);
            }
        }));

        try {
            if (getIntent().hasExtra("runBatchId"))
                runBatchId = getIntent().getStringExtra("runBatchId");
            else if (!PushReceiver.prefrenceForPushy.getString("runBatchId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("runBatchId", "").equals("")) {
                runBatchId = PushReceiver.prefrenceForPushy.getString("runBatchId", "0");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
       // new GetRunBatchDetails().execute();
       GetRunBatchDetails();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backFromBookingDetail:
                finish();
                break;

            case R.id.accept_random_run:
                GetRunRandomBatchDetails();
                //new GetRunRandomBatchDetails().execute();
                break;
        }
    }

    private void AcceptRunAsync(String runId){

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                AcceptRunresponse = new WebserviceHandler().acceptRunByRunId(runId);
            }

            @Override
            public void onPostExecute() {
                try {
                    if (AcceptRunresponse != null) {
                        if (AcceptRunresponse.code == 200) {
                            Intent intent = new Intent(RunBatchActivity.this, SlideMenuZoom2u.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("FromRunBatchNotification", 2);
                            startActivity(intent);
                            intent = null;
                            finish();
                        } else if (AcceptRunresponse.code == 400) {
                            try {
                                //{"message":"Validation Errors","errors":[{"message":"This run has already been accepted by another courier"}]}
                                JSONObject object = new JSONObject(AcceptRunresponse.response);
                                JSONArray errorsArray = object.getJSONArray("errors");
                                if (errorsArray != null && errorsArray.length() > 0) {
                                    String message = errorsArray.getJSONObject(0).getString("message");
                                    Toast.makeText(RunBatchActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else
                            Toast.makeText(RunBatchActivity.this, AcceptRunresponse.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }catch (Exception e){
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }




    private void GetRunBatchDetails(){

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                GetRunBatchDetailsresponse = new WebserviceHandler().getRunBatchDetails(runBatchId);
            }

            @Override
            public void onPostExecute() {
                try {

                    if (GetRunBatchDetailsresponse != null) {

                        Type listType = new TypeToken<List<RunBatchResponse>>() {
                        }.getType();
                        runDetailsModel = new Gson().fromJson(GetRunBatchDetailsresponse, listType);
                        if (runDetailsModel != null) {
                            tv_pickup_time.setText(runDetailsModel.get(0).getStartTime());
                            tv_deliver_by.setText(runDetailsModel.get(0).getEndTime());
                            pickup_address.setText(runDetailsModel.get(0).getStartLocation().getAddress());
                            list_recyclerview.setAdapter(new RunBatchAdapter(RunBatchActivity.this, runDetailsModel, new RunBatchAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(String runId) {
                                    AcceptRunAsync(runId);
                                    //new AcceptRunAsync().execute(runId);
                                }
                            }));
                            dropLocations = new ArrayList<String>();
                            for (int i = 1; i < runDetailsModel.toArray().length - 1; i++) {
                                dropLocations.add(runDetailsModel.get(i).getSuburbArea());
                            }


                            getAddressFromLocation(runDetailsModel.get(runDetailsModel.size() - 1).getSuburbArea() + " Australia",
                                    getApplicationContext());


                            Calendar cal = Calendar.getInstance();
                            if (LoginZoomToU.checkInternetwithfunctionality.dateFromString(runDetailsModel.get(0).getRunDate()).after(cal.getTime())) {
                                secondHeaderTxtBD.setText("ⓘ This run is for future date");
                            } else
                                secondHeaderTxtBD.setText("ⓘ This run is for today");

                        }

                    }

                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }catch (Exception e){
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
        }
        }.execute();
    }



    public void getAddressFromLocation(final String locationAddress,
                                              final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                try {
                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = (Address) addressList.get(0);
                        inItNewBookingDetailMapFragment(runDetailsModel, address.getLatitude(), address.getLongitude());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

    }

    //******** Initialize new booking detail view map ***************
    private void inItNewBookingDetailMapFragment(List<RunBatchResponse> runBatchResponse, Double dropgpx,Double dropgpy) {
        try {
            mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewBookingDetail));
            mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
                gMapBookingDetail = mapFragmentBookingDetail.getMap();
                gMapBookingDetail.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                gMapBookingDetail.getUiSettings().setZoomControlsEnabled(false);
                gMapBookingDetail.getUiSettings().setMyLocationButtonEnabled(true);
                gMapBookingDetail.getUiSettings().setCompassEnabled(false);
                gMapBookingDetail.getUiSettings().setRotateGesturesEnabled(true);
                gMapBookingDetail.getUiSettings().setZoomGesturesEnabled(true);


                try {
                    LatLng latLng = new LatLng(runBatchResponse.get(0).getStartLocation().getLatitude(), runBatchResponse.get(0).getStartLocation().getLongitude());
                    gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin)));
                    gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 9.5F));

                } catch (Exception e) {
                    e.printStackTrace();
                }



                try {

                    gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(dropgpx,dropgpy))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.a_drop)));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollViewNewBD.requestDisallowInterceptTouchEvent(true);
                    }
                });
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void GetRunRandomBatchDetails(){

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                GetRandomRunBatchDetailsresponse = new WebserviceHandler().getRandomRunBatchDetails(runBatchId);
            }

            @Override
            public void onPostExecute() {
                try {
                    if (GetRandomRunBatchDetailsresponse != null) {
                        if (GetRandomRunBatchDetailsresponse.code == 200) {
                            Intent intent = new Intent(RunBatchActivity.this, SlideMenuZoom2u.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("FromRunBatchNotification", 2);
                            startActivity(intent);
                            intent = null;
                            finish();
                        } else if (GetRandomRunBatchDetailsresponse.code == 400) {
                            try {
                                JSONObject object = new JSONObject(GetRandomRunBatchDetailsresponse.response);
                                JSONArray errorsArray = object.getJSONArray("errors");
                                if (errorsArray != null && errorsArray.length() > 0) {
                                    String message = errorsArray.getJSONObject(0).getString("message");
                                    Toast.makeText(RunBatchActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else
                            Toast.makeText(RunBatchActivity.this, GetRandomRunBatchDetailsresponse.getResponse(), Toast.LENGTH_SHORT).show();
                    }

                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }catch (Exception e){
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }



}