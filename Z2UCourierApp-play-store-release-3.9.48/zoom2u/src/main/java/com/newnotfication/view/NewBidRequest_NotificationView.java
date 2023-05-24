package com.newnotfication.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dimension.Dimension_class;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.offerrequesthandlr.Alert_To_PlaceBid;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Arun on 28/06/17.
 */

public class NewBidRequest_NotificationView extends Activity implements View.OnClickListener {

    private ScrollView scrollViewNewBD;

    private TextView secondHeaderTxtBD, vehicelValueTxtBD, distanceValueTxtBD, priceValueTxtBD, pickUpTimeValueTxtBD, deliveryTimeValueTxtBD,
            pickUpContactNameTxtBD, pickUpSuburbTxtBD, dropOffContactNameTxtBD, dropOffSuburbTxtBD, documentTxtBD;

    private int offerId;
    private String pickUpTime, dropOffTime, pickUpDateTime;

    private ProgressDialog progressForBookingAction;

    private RequestView_DetailPojo requestView_detailPojo;

    private Dialog dialogInNOotificationUI;

    private LinearLayout ll_booking_due_day;

    /*************  Save activity state when process killed in background*************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        try {
            outState.putBoolean("SetRoutific", WebserviceHandler.isRoutific);
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putParcelable("RequestView_detailPojo", requestView_detailPojo);
            outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
            //        outState.putParcelableArrayList("NewBookingArray", BookingView.bookingListArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    /*************  Restore activity state when resuming app after process killed in background*************/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view contents
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    synchronized protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("--------- ", "++++++++  On New intent is called ");
        inItNewBookingDetailViewContents(null);
    }

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PushReceiver.IsOtherScreenOpen =true;
        setContentView(R.layout.new_bid_request_notification_view);
        inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view contents
        RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
        Window window = NewBidRequest_NotificationView.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
        }
    }

    //******** Initialize new booking notification view contents ***************
    private void inItNewBookingDetailViewContents(Bundle savedInstanceState) {
        try {
            if (LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(NewBidRequest_NotificationView.this);
            LoginZoomToU.notificUINewBookingVisibleCount = 1;
            if (savedInstanceState != null) {
                try {
                    WebserviceHandler.isRoutific = savedInstanceState.getBoolean("SetRoutific");
                    ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                    requestView_detailPojo = savedInstanceState.getParcelable("RequestView_detailPojo");
                    BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
                    BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("NewBookingArray");
                    Intent serviceIntent = new Intent(this, ServiceForSendLatLong.class);
                    startService(serviceIntent);
                    serviceIntent = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);

        if (scrollViewNewBD == null)
            scrollViewNewBD = (ScrollView) findViewById(R.id.scrollViewNewBD);

        if (secondHeaderTxtBD == null)
            secondHeaderTxtBD = (TextView) findViewById(R.id.secondHeaderTxtBD);

         if (ll_booking_due_day == null)
             ll_booking_due_day =  findViewById(R.id.ll_booking_due_day);



        ((TextView) findViewById(R.id.newBDDistanceView).findViewById(R.id.verticleTxt1BD)).setText("VEHICLE");
        if (vehicelValueTxtBD == null)
            vehicelValueTxtBD = (TextView) findViewById(R.id.newBDDistanceView).findViewById(R.id.verticleTxt2BD);


        View newBDValueView = findViewById(R.id.newBDValueView);
        ((TextView) newBDValueView.findViewById(R.id.verticleTxt1BD)).setText("DISTANCE");
        if (distanceValueTxtBD == null)
            distanceValueTxtBD = (TextView) newBDValueView.findViewById(R.id.verticleTxt2BD);



        ((TextView) findViewById(R.id.newBDPickupView).findViewById(R.id.verticleTxt1BD)).setText("PICKUP");
        if (pickUpTimeValueTxtBD == null)
            pickUpTimeValueTxtBD = (TextView) findViewById(R.id.newBDPickupView).findViewById(R.id.verticleTxt2BD);



        ((TextView) findViewById(R.id.newBDDeliveryView).findViewById(R.id.verticleTxt1BD)).setText("DELIVER BY");
        if (deliveryTimeValueTxtBD == null)
            deliveryTimeValueTxtBD = (TextView) findViewById(R.id.newBDDeliveryView).findViewById(R.id.verticleTxt2BD);



        ((TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.addressHeaderTxtBD)).setText("Pickup");
        if (pickUpContactNameTxtBD == null)
            pickUpContactNameTxtBD = (TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.contactNameTxtBD);
        pickUpContactNameTxtBD.setVisibility(View.GONE);

        if (pickUpSuburbTxtBD == null)
            pickUpSuburbTxtBD = (TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.suburbTxtBD);


        ((TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.addressHeaderTxtBD)).setText("Dropoff");
        if (dropOffContactNameTxtBD == null)
            dropOffContactNameTxtBD = (TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.contactNameTxtBD);
        dropOffContactNameTxtBD.setVisibility(View.GONE);


        if (dropOffSuburbTxtBD == null)
            dropOffSuburbTxtBD = (TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.suburbTxtBD);


        if (documentTxtBD == null)
            documentTxtBD = (TextView) findViewById(R.id.documentTxtBD);




        if (priceValueTxtBD == null)
            priceValueTxtBD = (TextView) findViewById(R.id.priceValueTxtBD);



        findViewById(R.id.rejectBtnBD).setOnClickListener(this);
        ((Button) findViewById(R.id.rejectBtnBD)).setText("Cancel");
        findViewById(R.id.acceptBtnBD).setOnClickListener(this);
        ((Button) findViewById(R.id.acceptBtnBD)).setText("Place bid");

        initView();
    }

    //******** Initialize new notification view map ***************
    private void inItNewNotificationViewMapFragment() {
        try {
            if (BookingDetail_New.mapFragmentBookingDetail != null)
                BookingDetail_New.mapFragmentBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewBookingDetail));
            BookingDetail_New.mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
                BookingDetail_New.gMapBookingDetail = BookingDetail_New.mapFragmentBookingDetail.getMap();
                BookingDetail_New.gMapBookingDetail.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //BookingDetail_New.gMapBookingDetail.setMyLocationEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomControlsEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setMyLocationButtonEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setCompassEnabled(false);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setRotateGesturesEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomGesturesEnabled(true);

                // Stop scrolling of layout View when map scroll
                BookingDetail_New.mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollViewNewBD.requestDisallowInterceptTouchEvent(true);
                    }
                });

                try{
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble((String) requestView_detailPojo.getPickupLatitude()),
                                    Double.parseDouble((String) requestView_detailPojo.getPickupLongitude())))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin_new)));
                }catch(Exception e){
                    e.printStackTrace();
                }

                try{
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble((String) requestView_detailPojo.getDropLatitude()),
                                    Double.parseDouble((String) requestView_detailPojo.getDropLongitude())))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin_new)));
                }catch(Exception e){
                    e.printStackTrace();
                }

                BookingDetail_New.gMapBookingDetail.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        try {
                            LatLngBounds bounds = new LatLngBounds.Builder()
                                    .include(new LatLng(Double.parseDouble((String) requestView_detailPojo.getPickupLatitude()),
                                            Double.parseDouble((String) requestView_detailPojo.getPickupLongitude())))
                                    .include(new LatLng(Double.parseDouble((String) requestView_detailPojo.getDropLatitude()),
                                            Double.parseDouble((String) requestView_detailPojo.getDropLongitude())))
                                    .build();
                            BookingDetail_New.gMapBookingDetail.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                            bounds = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                            BookingDetail_New.gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.274398, 133.775136), 8));
                        }
                    }
                });

//        new ShowPolyline_DirectionAPI(NewBidRequest_NotificationView.this, BookingDetail_New.gMapBookingDetail,
//                new LatLng(Double.parseDouble((String) requestView_detailPojo.getPickupLatitude()),
//                        Double.parseDouble((String) requestView_detailPojo.getPickupLongitude())),
//                new LatLng(Double.parseDouble((String) requestView_detailPojo.getDropLatitude()),
//                        Double.parseDouble((String) requestView_detailPojo.getDropLongitude())));

                List<LatLng> list = null;
                try {
                    list = PolyUtil.decode(requestView_detailPojo.getRoutePolyline());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (list != null)
                    BookingDetail_New.drawRoute(list);

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*
    ************** Set new notification view contents to fields **********
     */
    private void setNewNotificationViewContents(){
        try {
            inItNewNotificationViewMapFragment();          //******** Initialize new booking detail view map

            //   ***********  Change color for next day booking  **************


            try {
                if (Functional_Utility.checkDateIsToday(pickUpDateTime)==false) {
                    ll_booking_due_day.setBackgroundResource(R.drawable.new_green_round);
                    priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.green_bg));
                    secondHeaderTxtBD.setText("ⓘ This request is for future date");
                } else
                {
                    ll_booking_due_day.setBackgroundResource(R.drawable.new_white_back_blue);
                    priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.base_color1));
                    secondHeaderTxtBD.setText("ⓘ This request is due today");
                }

            }catch (Exception ex){
            }

            //   **************** ----------------- *****************

            vehicelValueTxtBD.setText(requestView_detailPojo.getVehicle().toUpperCase());
            findViewById(R.id.newCustomerTxtInBD).setVisibility(View.GONE);

            distanceValueTxtBD.setText(requestView_detailPojo.getDistance()+"");

            double priceInt = requestView_detailPojo.getCourierPrice();
            if (priceInt != 0)
                priceValueTxtBD.setText("$"+priceInt);
            else {
                findViewById(R.id.priceHeaderTxtBD).setVisibility(View.GONE);
                priceValueTxtBD.setVisibility(View.GONE);
            }

            pickUpTimeValueTxtBD.setText(pickUpTime);

            deliveryTimeValueTxtBD.setText(dropOffTime);

            pickUpContactNameTxtBD.setText(""+requestView_detailPojo.getPickupContactName());

            pickUpSuburbTxtBD.setText(""+requestView_detailPojo.getPickupSuburb());

            dropOffContactNameTxtBD.setText(""+requestView_detailPojo.getDropContactName());

            dropOffSuburbTxtBD.setText(""+requestView_detailPojo.getDropSuburb());

            documentTxtBD.setText("");
            LinearLayout rootView = findViewById(R.id.dimension_view);

            if (requestView_detailPojo.isSuggestedPrice()) {
                ((TextView)findViewById(R.id.priceHeaderTxtBD)).setText("Suggested price :");
            }else
                ((TextView)findViewById(R.id.priceHeaderTxtBD)).setText("Price :");

            try{
                if (requestView_detailPojo.getShipmentsArray().size() > 0) {
                    for (int i = 0; i < requestView_detailPojo.getShipmentsArray().size(); i++){
                        rootView.setVisibility(View.VISIBLE);
                        documentTxtBD.setVisibility(View.GONE);
                        ArrayList<HashMap<String, Object>> shipmentArray = requestView_detailPojo.getShipmentsArray();
                        new Dimension_class().setTheViewsForDimensions(rootView,this,shipmentArray);
                    }
                }else {
                    documentTxtBD.setVisibility(View.VISIBLE);
                    documentTxtBD.append("Shipments not available");
                }}catch (Exception e){
                e.printStackTrace();
                documentTxtBD.setVisibility(View.VISIBLE);
                documentTxtBD.append("Shipments not available");
            }
        } catch (Exception e) {
            e.printStackTrace();
            dialogViewForNewNotification("Error!", "Something went wrong here.", false);
        }
    }


    @Override
    public void onBackPressed(){
//		super.onBackPressed();
//		finish();
    }

    // ******* Dismiss new bid  request notification view ***********
    private void backFromBookingDetailView(){
        finish();
        LoginZoomToU.notificUINewBookingVisibleCount = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backFromBookingDetail:
                backFromBookingDetailView();
                break;
            case R.id.rejectBtnBD:
                backFromBookingDetailView();
                break;
            case R.id.acceptBtnBD:
                Intent placeBidView = new Intent(NewBidRequest_NotificationView.this, Alert_To_PlaceBid.class);
                placeBidView.putExtra("isFromBidDetail", 2);
                placeBidView.putExtra("requestView_detailPojo", requestView_detailPojo);
                placeBidView.putExtra("shipmentItems", documentTxtBD.getText().toString());
                startActivity(placeBidView);
                placeBidView = null;
                backFromBookingDetailView();
                break;
        }
    }

    private void dialogViewForNewNotification(String titleStr, String msgStr, final boolean isBookingAccepted){

        try{
            if(dialogInNOotificationUI != null)
                if (dialogInNOotificationUI.isShowing())
                    dialogInNOotificationUI.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dialogInNOotificationUI != null)
            dialogInNOotificationUI = null;
        dialogInNOotificationUI = new Dialog(NewBidRequest_NotificationView.this);
        dialogInNOotificationUI.setCancelable(false);
        dialogInNOotificationUI.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogInNOotificationUI.setContentView(R.layout.dialogview);

        Window window = dialogInNOotificationUI.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView enterFieldDialogHEader = (TextView) dialogInNOotificationUI.findViewById(R.id.titleDialog);

        enterFieldDialogHEader.setText(titleStr);

        TextView enterFieldDialogMsg = (TextView) dialogInNOotificationUI.findViewById(R.id.dialogMessageText);
        enterFieldDialogMsg.setText(msgStr);

        Button enterFieldDialogDoneBtn = (Button) dialogInNOotificationUI.findViewById(R.id.dialogDoneBtn);
        enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInNOotificationUI.dismiss();
                backFromBookingDetailView();
                if (isBookingAccepted && ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 2) {
                    Intent intent1 = new Intent(NewBidRequest_NotificationView.this, ServiceToUpdate_ActiveBookingList.class);
                    startService(intent1);
                    intent1 = null;
                }

            }
        });
        dialogInNOotificationUI.show();
    }

    synchronized void initView(){
        try{
            if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                if (!PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "").equals(""))
                    GetMyRequestOfCustomerDetail();
                else {
                    finish();
                    LoginZoomToU.notificUINewBookingVisibleCount = 0;
                }
            } else
                DialogActivity.alertDialogView(NewBidRequest_NotificationView.this, "No network!", "No network connection, Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dismissNewBookingProgressDialog() {
        try {
            if(progressForBookingAction != null)
                if(progressForBookingAction.isShowing())
                    Custom_ProgressDialogBar.dismissProgressBar(progressForBookingAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItProgressInNewNotificationView() {
        if(progressForBookingAction != null)
            progressForBookingAction = null;
        progressForBookingAction = new ProgressDialog(NewBidRequest_NotificationView.this);
        Custom_ProgressDialogBar.inItProgressBar(progressForBookingAction);
    }

    private void GetMyRequestOfCustomerDetail(){
        final String[] webserviceMyRequestList = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    dismissNewBookingProgressDialog();
                    inItProgressInNewNotificationView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    //    Functional_Utility.sendLocationToServer();
                    WebserviceHandler handler = new WebserviceHandler();
                    Log.e("********************** ", "Bid Request ID == "+PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "0"));
                    webserviceMyRequestList[0] = handler.getQuoteRequestOfCustomerDetails(Integer.parseInt(PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "0")));
                    Log.e("Pushy", webserviceMyRequestList[0]);
                    handler.resetBidRequestId();
                    handler = null;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                try {
                    Log.e("--------- ", "Bid Response == "+webserviceMyRequestList);
                    JSONObject jsonObjectOflIstDelivery = new JSONObject(webserviceMyRequestList[0]);
                    JSONArray jsonArrayOfListDelivery = jsonObjectOflIstDelivery.getJSONArray("data");
                    if (jsonArrayOfListDelivery.length() > 0) {
                        JSONObject objDeliveryDetail = jsonArrayOfListDelivery.getJSONObject(0);
                        jsonArrayOfListDelivery = null;
                        requestView_detailPojo = new RequestView_DetailPojo();
                        requestView_detailPojo.set$id(objDeliveryDetail.getString("$id"));
                        requestView_detailPojo.setId(objDeliveryDetail.getInt("RequestId"));
                        if (!objDeliveryDetail.getString("Notes").equals("") || !objDeliveryDetail.getString("Notes").equals(null))
                            requestView_detailPojo.setNotes(objDeliveryDetail.getString("Notes"));
                        else
                            requestView_detailPojo.setNotes("Delivery notes");

                        pickUpDateTime = objDeliveryDetail.getString("PickupDateTime");

                        String PickupDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDeliveryDetail.getString("PickupDateTime"));
                        String[] PickupDateTimeSplit = PickupDateTime.split(" ");
                        requestView_detailPojo.setPickUpDateTime(PickupDateTimeSplit[0] + "\n" + PickupDateTimeSplit[1] + " " + PickupDateTimeSplit[2]);
                        pickUpTime = getDateForBidNotification(PickupDateTimeSplit[0])+"\n"+PickupDateTimeSplit[1] + " " + PickupDateTimeSplit[2];
                        PickupDateTimeSplit = null;

                        String dropOffDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDeliveryDetail.getString("DropDateTime"));
                        String[] dropOffDateTimeSplit = dropOffDateTime.split(" ");
                        requestView_detailPojo.setDropDateTime(dropOffDateTimeSplit[0] + "\n" + dropOffDateTimeSplit[1] + " " + dropOffDateTimeSplit[2]);
                        dropOffTime = getDateForBidNotification(dropOffDateTimeSplit[0])+"\n"+dropOffDateTimeSplit[1] + " " + dropOffDateTimeSplit[2];
                        dropOffDateTimeSplit = null;

                        requestView_detailPojo.setPickupContactName(objDeliveryDetail.getString("PickupContactName"));
                        requestView_detailPojo.setDropContactName(objDeliveryDetail.getString("DropContactName"));
                        requestView_detailPojo.setPickupLatitude(objDeliveryDetail.getString("PickupLatitude"));
                        requestView_detailPojo.setPickupLongitude(objDeliveryDetail.getString("PickupLongitude"));
                        requestView_detailPojo.setDropLatitude(objDeliveryDetail.getString("DropLatitude"));
                        requestView_detailPojo.setDropLongitude(objDeliveryDetail.getString("DropLongitude"));
                        requestView_detailPojo.setPickupSuburb(objDeliveryDetail.getString("PickupSuburb"));
                        requestView_detailPojo.setDropSuburb(objDeliveryDetail.getString("DropSuburb"));
                        requestView_detailPojo.setPickupState(objDeliveryDetail.getString("PickupState"));
                        requestView_detailPojo.setDropState(objDeliveryDetail.getString("DropState"));
                        requestView_detailPojo.setPickUpAddress(objDeliveryDetail.getString("PickupAddress"));
                        requestView_detailPojo.setDropOffAddress(objDeliveryDetail.getString("DropAddress"));
                        requestView_detailPojo.setMinPrice(Functional_Utility.round(objDeliveryDetail.getDouble("MinPrice")));
                        requestView_detailPojo.setMaxPrice(Functional_Utility.round(objDeliveryDetail.getDouble("MaxPrice")));
                        requestView_detailPojo.setDistance(objDeliveryDetail.getString("Distance"));
                        requestView_detailPojo.setTotalBids(objDeliveryDetail.getInt("TotalBids"));
                        requestView_detailPojo.setCustomer(objDeliveryDetail.getString("Customer"));
                        requestView_detailPojo.setCustomerID(objDeliveryDetail.getString("CustomerId"));
                        requestView_detailPojo.setVehicle(objDeliveryDetail.getString("Vehicle"));

                        try {
                            requestView_detailPojo.setRoutePolyline(objDeliveryDetail.getString("RoutePolyline"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            requestView_detailPojo.setCourierPrice(objDeliveryDetail.getDouble("CourierPrice"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestView_detailPojo.setCourierPrice(0.0);
                        }
                        try {
                            requestView_detailPojo.setSuggestedPrice(objDeliveryDetail.getBoolean("IsSuggestedPrice"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            requestView_detailPojo.setSuggestedPrice(false);
                        }

                        String vehicle = "";
                         offerId = objDeliveryDetail.getJSONObject("OfferDetails").getInt("OfferId");

                        requestView_detailPojo.setOfferId(offerId);

                        ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                        for (int k = 0; k < objDeliveryDetail.getJSONArray("Shipments").length(); k++) {
                            HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                            JSONObject jObjOfShipmentItem = objDeliveryDetail.getJSONArray("Shipments").getJSONObject(k);
                            objOFShipments.put("Category", jObjOfShipmentItem.getString("Category"));
                            objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("Quantity"));
                            try {
                                objOFShipments.put("LengthCm", jObjOfShipmentItem.getInt("LengthCm"));
                                objOFShipments.put("WidthCm", jObjOfShipmentItem.getInt("WidthCm"));
                                objOFShipments.put("HeightCm", jObjOfShipmentItem.getInt("HeightCm"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                objOFShipments.put("LengthCm", 0);
                                objOFShipments.put("WidthCm", 0);
                                objOFShipments.put("HeightCm", 0);
                            }
                            try {
                                objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("ItemWeightKg"));
                                objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("TotalWeightKg"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                objOFShipments.put("ItemWeightKg", 0);
                                objOFShipments.put("TotalWeightKg", 0);
                            }
                            arrayOfShipments.add(objOFShipments);
                        }

                        requestView_detailPojo.setShipmentsArray(arrayOfShipments);
                        setNewNotificationViewContents();   //******* Set UI content for Bid detail
                        objDeliveryDetail = null;
                        jsonObjectOflIstDelivery = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogToFinishView(NewBidRequest_NotificationView.this, "Error!", "Something went wrong, Please try again.");
                }
                dismissNewBookingProgressDialog();
            }
        }.execute();
    }

    private String getDateForBidNotification(String s) {
        return s.substring(0, 6);
    }

}
