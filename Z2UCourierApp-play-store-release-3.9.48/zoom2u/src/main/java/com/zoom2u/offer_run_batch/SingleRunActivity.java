package com.zoom2u.offer_run_batch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.animations.ExpandCollapseAnimation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.offer_run_batch.adapter.RunBatchStopsAdapter;
import com.zoom2u.offer_run_batch.model.RunDetailsModel;
import com.zoom2u.offer_run_batch.model.Stop;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Mahendra Dabi on 06-07-2021.
 */
public class SingleRunActivity extends Activity implements View.OnClickListener {
    Button accept_run, reject_run;
    RecyclerView list_recyclerview;
    TextView tv_vehicle, tv_deliver_by, tv_pickup_time, tv_price, tv_no_stops;
    TextView tv_pickup_address, secondHeaderTxtBD, suggestPriceTxt;
    String runId = "0";
    ScrollView scrollViewNewBD;
    ImageView backFromBookingDetail;
    RunDetailsModel runDetailsModel;
    public WorkaroundMapFragment mapFragmentBookingDetail;
    public GoogleMap gMapBookingDetail;
    ProgressDialog progressDialog;
    Window window;
    String GetRunDetailsresponse;
    ApiResponseModel AcceptRunresponse;
    LinearLayout priceSubmitView,txtWithSuggestPriceBtnLayout;
    EditText edtSuggestPriceTxt;
    Button cancelSuggestPriceBtnBD;
    Button submitSuggestPriceBtnBD;
    String runSuggestedPrice;
    TextView first_text,second_text;
    RelativeLayout headerLayoutAllTitleBar;
    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsOtherScreenOpen = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsOtherScreenOpen = true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_batch_offer_run);
        PushReceiver.IsOtherScreenOpen = true;
        window = SingleRunActivity.this.getWindow();
        headerLayoutAllTitleBar= findViewById(R.id.headerLayoutAllTitleBar);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }

        progressDialog = new ProgressDialog(SingleRunActivity.this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialog);

        backFromBookingDetail = findViewById(R.id.backFromBookingDetail);
        backFromBookingDetail.setOnClickListener(this);

        secondHeaderTxtBD = findViewById(R.id.secondHeaderTxtBD);
        suggestPriceTxt = findViewById(R.id.suggestPriceTxt);
        suggestPriceTxt.setOnClickListener(this);
        scrollViewNewBD = (ScrollView) findViewById(R.id.scrollViewNewBD);
        tv_vehicle = findViewById(R.id.tv_vehicle);
        tv_pickup_time = findViewById(R.id.tv_pickup_time);
        tv_deliver_by = findViewById(R.id.tv_deliver_by);
        tv_price = findViewById(R.id.tv_price);
        tv_no_stops = findViewById(R.id.tv_no_stops);

        tv_pickup_address = findViewById(R.id.tv_pickup_address);
        first_text = findViewById(R.id.first_text);
        second_text = findViewById(R.id.second_text);
        accept_run = findViewById(R.id.accept_run);
        accept_run.setOnClickListener(this);
        reject_run = findViewById(R.id.reject_run);
        reject_run.setOnClickListener(this);
        list_recyclerview = findViewById(R.id.list_recyclerview);
        list_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        priceSubmitView = (LinearLayout) findViewById(R.id.priceSubmitView);
        txtWithSuggestPriceBtnLayout = (LinearLayout) findViewById(R.id.txtWithSuggestPriceBtnLayout);
        edtSuggestPriceTxt=findViewById(R.id.edtSuggestPriceTxt);
        cancelSuggestPriceBtnBD = (Button) findViewById(R.id.cancelSuggestPriceBtnBD);
        cancelSuggestPriceBtnBD.setOnClickListener(this);
        submitSuggestPriceBtnBD = (Button) findViewById(R.id.submitSuggestPriceBtnBD);
        submitSuggestPriceBtnBD.setOnClickListener(this);
       /* edtSuggestPriceTxt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scrollViewNewBD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewNewBD.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 500);
                return false;
            }
        });*/
        try {
            if (getIntent().hasExtra("runId"))
                runId = getIntent().getStringExtra("runId");
            else if (!PushReceiver.prefrenceForPushy.getString("runId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("runId", "").equals("")) {
                runId = PushReceiver.prefrenceForPushy.getString("runId", "0");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //new GetRunDetails().execute();
        GetRunDetails(runId);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backFromBookingDetail:
                finish();
                break;

            case R.id.accept_run:
                AcceptRunAsync(runId);
                //new AcceptRunAsync().execute(runId);
                break;
            case R.id.reject_run:
                finish();
                break;
            case R.id.suggestPriceTxt:
                findViewById(R.id.txtWithSuggestPriceBtnLayout).setVisibility(View.GONE);
                ExpandCollapseAnimation.expand(priceSubmitView);
                ExpandCollapseAnimation.collapse(txtWithSuggestPriceBtnLayout);
                edtSuggestPriceTxt.requestFocus();
               // LoginZoomToU.imm.showSoftInput(edtSuggestPriceTxt, InputMethodManager.SHOW_IMPLICIT);
                scrollViewNewBD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewNewBD.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 500);
                break;
            case R.id.cancelSuggestPriceBtnBD:
                findViewById(R.id.txtWithSuggestPriceBtnLayout).setVisibility(View.VISIBLE);
                edtSuggestPriceTxt.setText("");
                ExpandCollapseAnimation.expand(txtWithSuggestPriceBtnLayout);
                ExpandCollapseAnimation.collapse(priceSubmitView);
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                break;
            case R.id.submitSuggestPriceBtnBD:
                runSuggestedPrice = edtSuggestPriceTxt.getText().toString();
                if (!runSuggestedPrice.equals("")) {
                    LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                    submitCouriersSuggestPrice(runSuggestedPrice);
                } else
                    Toast.makeText(this, "Please enter your price", Toast.LENGTH_LONG).show();
                break;
        }
    }
    private void submitCouriersSuggestPrice(String edtSuggestPriceTxt) {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            SuggestAPriceAsync(runSuggestedPrice);
        else
            DialogActivity.alertDialogView(SingleRunActivity.this, "No Network!", "No network connection, Please try again later.");
    }

    private List<String> sortDropPoints(List<Stop> stopList) {
        List<String> newArray = new ArrayList<>();
        HashSet<String> stops = new HashSet();

        for (Stop stop : stopList)
            stops.add(stop.getSuburb());
        for (String s : stops) {
            int count = 0;
            for (Stop compareWith : stopList) {
                if (s.equals(compareWith.getSuburb())) {
                    count++;
                }
            }
            if (count > 1) {
                s = s + " (" + count + ")";
                newArray.add(s);
            } else newArray.add(s);
        }
        return newArray;
    }

    private void GetRunDetails(String runId) {

        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                GetRunDetailsresponse = new WebserviceHandler().getRunDetails(runId);
            }

            @Override
            public void onPostExecute() {
                try {
                    if (GetRunDetailsresponse != null) {
                        Gson gson = new Gson();
                        runDetailsModel = gson.fromJson(GetRunDetailsresponse, RunDetailsModel.class);
                        if (runDetailsModel != null) {
                            tv_vehicle.setText(runDetailsModel.getVehicle());
                            tv_pickup_time.setText(runDetailsModel.getStartTime());
                            tv_deliver_by.setText(runDetailsModel.getEndTime());
                            tv_price.setText("$" + runDetailsModel.getPossibleEarnings());


                            if(runDetailsModel.getIsReverseRun()){
                                first_text.setText("Drop Locations");
                                second_text.setText("Pickup suburbs");
                            }else{
                                first_text.setText("First pickup");
                                second_text.setText("Drop suburbs");
                            }


                            if (runDetailsModel.getStops() != null) {
                                tv_no_stops.setText("" + runDetailsModel.getStops().size());

                                List<String> stops = sortDropPoints(runDetailsModel.getStops());
                                list_recyclerview.setAdapter(new RunBatchStopsAdapter(stops));

                                inItNewBookingDetailMapFragment(
                                        runDetailsModel.getStartLocation().getLatitude(),
                                        runDetailsModel.getStartLocation().getLongitude(),
                                        runDetailsModel.getStops()
                                );
                            } else tv_no_stops.setText("0");
                            tv_pickup_address.setText(runDetailsModel.getStartLocation().getAddress());

                            Calendar cal = Calendar.getInstance();
                            if (LoginZoomToU.checkInternetwithfunctionality.dateFromString(runDetailsModel.getRunDate()).after(cal.getTime())) {
                                secondHeaderTxtBD.setText("ⓘ This run is for future date");
                            } else
                                secondHeaderTxtBD.setText("ⓘ This run is for today");

                        }

                    }else
                        finish();
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                } catch (Exception e) {
                    finish();
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }


    private void SuggestAPriceAsync(String edtSuggestPriceTxt) {
        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                AcceptRunresponse = new WebserviceHandler().suggestAPriceRun(edtSuggestPriceTxt,runDetailsModel.getRunId(),runDetailsModel.getStartLocation().getSuburb(),runDetailsModel.getPossibleEarnings(),runDetailsModel.getStops().size());
            }

            @Override
            public void onPostExecute() {
                try {
                    if (AcceptRunresponse != null) {
                        if (AcceptRunresponse.code == 200) {
                            Intent intent = new Intent(SingleRunActivity.this, SlideMenuZoom2u.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Toast.makeText(SingleRunActivity.this, "Your suggested price has been sent.", Toast.LENGTH_LONG).show();
                            finish();
                        } else if (AcceptRunresponse.code == 400) {
                            try {
                                JSONObject object = new JSONObject(AcceptRunresponse.response);
                                JSONArray errorsArray = object.getJSONArray("errors");
                                if (errorsArray != null && errorsArray.length() > 0) {
                                    String message = errorsArray.getJSONObject(0).getString("message");
                                    Toast.makeText(SingleRunActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else
                            Toast.makeText(SingleRunActivity.this, AcceptRunresponse.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                } catch (Exception e) {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }


    private void AcceptRunAsync(String runId) {
        new MyAsyncTasks() {
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
                            Intent intent = new Intent(SingleRunActivity.this, SlideMenuZoom2u.class);
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
                                    Toast.makeText(SingleRunActivity.this, message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else
                            Toast.makeText(SingleRunActivity.this, AcceptRunresponse.getResponse(), Toast.LENGTH_SHORT).show();
                    }
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                } catch (Exception e) {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }



    //******** Initialize new booking detail view map ***************
    private void inItNewBookingDetailMapFragment(Double pickUpLatitude, Double pickUpLongitude, List<Stop> dropStops) {
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
                    LatLng latLng = new LatLng(pickUpLatitude, pickUpLongitude);
                    gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin)));
                    gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10F));
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    gMapBookingDetail.setOnMapLoadedCallback(() -> {
                        try {
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            if (dropStops != null)
                                /* for (Stop s : dropStops) {*/
                                for (int i = 0; i < dropStops.size(); i++) {
                                    if (i == dropStops.size() - 1) {
                                        LatLng latLng = new LatLng(dropStops.get(i).getLatitude(), dropStops.get(i).getLongitude());
                                        builder.include(latLng);
                                        gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                                                .title(dropStops.get(i).getSuburb())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.a_drop)));
                                    } else {
                                        LatLng latLng = new LatLng(dropStops.get(i).getLatitude(), dropStops.get(i).getLongitude());
                                        builder.include(latLng);
                                        gMapBookingDetail.addMarker(new MarkerOptions().position(latLng)
                                                .title(dropStops.get(i).getSuburb())
                                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin)));
                                    }
                                }
                        } catch (Exception e) {
                            e.printStackTrace();
                            gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.274398, 133.775136), 8));
                        }
                    });


                } catch (Exception e) {
                    e.printStackTrace();
                }


                // Stop scrolling of layout View when map scroll
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

}
