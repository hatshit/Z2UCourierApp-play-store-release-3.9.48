package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.z2u.chatview.ChatView_BidConfirmation;
import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.BidImageDialog;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dimension.Dimension_class;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Created by Arun on 5/5/17.
 */

public class Bid_RequestView_Detail extends Activity implements View.OnClickListener {

    BidDetailsModel bidDetailsModel;
    ScrollView bidDetailScrollView;
    RelativeLayout bidDetailMainLayout;
    private boolean isPickShowed = true;
    int images_array_length;

    TextView shipmentItemBidDetail, shipmentItemBidDetail1,packagedItemBidDetail, distanceTxtPlaceBidDetail;
    TextView  suggestedPriceValueTxtPlaceBidDetail;
    ReadMoreTextView notesValueTxtPlaceBidDetail;
    TextView bidActiveForTxtInDetail, totalBidValueTxtPlaceBidDetail;

    TextView txtShipmentDetailsBidDetail, viewHideBtnShipmentDetailsBidDetail;
    private boolean hasExpanded = false;
    Button placeBidBtnList, chatBtnPlaceBidDetail, cancelBtnPlaceBidDetail;
    int requestIdFromBidListView, offerId;
    String price, cretedDateTime;
    RelativeLayout documentLayoutABD;
    RequestView_DetailPojo requestView_detailPojo;
    ProgressDialog progressForDeliveryHistory;
    RelativeLayout subLayoutBiddetail;
    private boolean isBidIsCanceled;
    public static boolean isBidDetailPageOpen = false;
    public static Button btnNotifyUnreadCountDetail;
    public static int requestDetailBidId;
    String webserviceMyRequestList = "";
    TextView pickDropDate, pickHeaderLayoutPickTimeTxtABD;
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
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt("bidRequestIdForDetail", requestIdFromBidListView);
            outState.putBoolean("IsRequestIdIsCancel", isBidIsCanceled);
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quote_detailview);
        PushReceiver.IsOtherScreenOpen =true;
        isBidDetailPageOpen = true;     //************** For updating chat counter  **********
        RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
        Window window = Bid_RequestView_Detail.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if(MainActivity.isIsBackGroundGray()){
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        }else{
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }

         bidDetailMainLayout=findViewById(R.id.activeBookingDetailWindow);

        if (savedInstanceState != null) {
            try {
                requestIdFromBidListView = savedInstanceState.getInt("bidRequestIdForDetail");
                isBidIsCanceled = savedInstanceState.getBoolean("IsRequestIdIsCancel");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                if (LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(Bid_RequestView_Detail.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            requestIdFromBidListView = getIntent().getIntExtra("RequestIdForDetail", 0);

        requestDetailBidId = requestIdFromBidListView;

        inItBidDetailView();
    }

    //*********** In-it bid detail view ***********
    private void inItBidDetailView() {
        pickDropDate = findViewById(R.id.pickDropDate);
        findViewById(R.id.pickUpHeaderLayoutABD).setOnClickListener(this);
        findViewById(R.id.pickHeaderLayoutPickTxtABD);
        pickHeaderLayoutPickTimeTxtABD = (TextView) findViewById(R.id.pickHeaderLayoutPickTimeTxtABD);
        documentLayoutABD = (RelativeLayout) findViewById(R.id.documentLayoutABD);

        findViewById(R.id.dropOffHeaderLayoutABD).setOnClickListener(this);
        findViewById(R.id.dropHeaderLayoutDropTxtABD);
        findViewById(R.id.pickDropCustomerNameABD);
        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);
        isBidIsCanceled = getIntent().getBooleanExtra("IsRequestIdIsCancel", false);

        if (btnNotifyUnreadCountDetail != null)
            btnNotifyUnreadCountDetail = null;

        btnNotifyUnreadCountDetail = (Button) findViewById(R.id.btnNotifyUnreadCountDetail);



        // bidDetailScrollView = (ScrollView) findViewById(R.id.bidDetailScrollView);


        txtShipmentDetailsBidDetail = (TextView) findViewById(R.id.txtShipmentDetailsBidDetail);
        txtShipmentDetailsBidDetail.setVisibility(View.GONE);
        viewHideBtnShipmentDetailsBidDetail = (TextView) findViewById(R.id.viewHideBtnShipmentDetailsBidDetail);

        viewHideBtnShipmentDetailsBidDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDimensionPopup();
             /*   if (hasExpanded) {
                    txtShipmentDetailsBidDetail.setMaxLines(3);
                    txtShipmentDetailsBidDetail.setVisibility(View.GONE);
                    documentLayoutABD.getLayoutParams().width = Functional_Utility.dip2px(Bid_RequestView_Detail.this, 124.0f);
                    viewHideBtnShipmentDetailsBidDetail.setText("Show Dimension");
                } else {
                    txtShipmentDetailsBidDetail.setMaxLines(100);
                    txtShipmentDetailsBidDetail.setVisibility(View.VISIBLE);
                    viewHideBtnShipmentDetailsBidDetail.setText("Hide Dimension");
                    documentLayoutABD.getLayoutParams().width = RelativeLayout.LayoutParams.MATCH_PARENT;

                }
                hasExpanded = !hasExpanded;*/
            }
        });




        shipmentItemBidDetail = (TextView) findViewById(R.id.shipmentItemBidDetail);
        shipmentItemBidDetail1 = (TextView) findViewById(R.id.shipmentItemBidDetail1);
        packagedItemBidDetail = (TextView) findViewById(R.id.packagedItemBidDetail);
        distanceTxtPlaceBidDetail = (TextView) findViewById(R.id.distanceTxtPlaceBidDetail);
        bidActiveForTxtInDetail = (TextView) findViewById(R.id.bidActiveForTxtInDetail);
        notesValueTxtPlaceBidDetail = (ReadMoreTextView) findViewById(R.id.notesValueTxtPlaceBidDetail);
        suggestedPriceValueTxtPlaceBidDetail = (TextView) findViewById(R.id.suggestedPriceValueTxtPlaceBidDetail);
        totalBidValueTxtPlaceBidDetail = (TextView) findViewById(R.id.totalBidValueTxtPlaceBidDetail);
        placeBidBtnList = (Button) findViewById(R.id.placeBidBtnList);
        placeBidBtnList.setOnClickListener(this);
        chatBtnPlaceBidDetail = (Button) findViewById(R.id.chatBtnPlaceBidDetail);
        chatBtnPlaceBidDetail.setOnClickListener(this);
        cancelBtnPlaceBidDetail = (Button) findViewById(R.id.cancelBtnPlaceBidDetail);
        cancelBtnPlaceBidDetail.setOnClickListener(this);

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetMyRequestOfCustomerDetail();
            //new GetMyRequestOfCustomerDetail().execute();
        else
            DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "No network!", "No network connection, Please try again later.");


    }


    // ************  Start timer for Bid request list *********
    public void callTimerBidActiveFor() {
        if (BookingView.handlerForTimeCounter != null)
            BookingView.handlerForTimeCounter = null;
        BookingView.handlerForTimeCounter = new Handler();
        BookingView.handlerForTimeCounter.postDelayed(TimerForBidActiveFor, 60 * 1000);
    }

    Runnable TimerForBidActiveFor = new Runnable() {
        @Override
        public void run() {
            setActiveTimeForBid();
            BookingView.handlerForTimeCounter.postDelayed(this, 60 * 1000);
        }
    };


    public void showDimensionPopup(){
        Dialog enterFieldDialog;


        try {
            enterFieldDialog = new Dialog(this, android.R.style.Theme_Light);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.show_popup_dimesion_bid);
            enterFieldDialog.setCanceledOnTouchOutside(true);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            View dimension = enterFieldDialog.findViewById(R.id.dimension_view);
            RecyclerView recycler_dimension = dimension.findViewById(R.id.dimension_recyclerview);

            enterFieldDialog.findViewById(R.id.hideDimensionTxtABD).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.cancel();
                }
            });


            ((TextView) enterFieldDialog.findViewById(R.id.vehicleTxtABD)).setText((String) requestView_detailPojo.getVehicle());
            TextView documentTxtABD1=((TextView) enterFieldDialog.findViewById(R.id.documentTxtABD));
            TextView dimentionDetailsTxtABD = enterFieldDialog.findViewById(R.id.dimentionDetailsTxtABD);

          try {
              if (requestView_detailPojo.getShipmentsArray().size() > 0) {
                  for (int i = 0; i < requestView_detailPojo.getShipmentsArray().size(); i++) {
                      ArrayList<HashMap<String, Object>> shipmentArray = requestView_detailPojo.getShipmentsArray();
                      String category = "" + shipmentArray.get(i).get("Category");
                      String quantity = "" + shipmentArray.get(i).get("Quantity");
                      if (i == 0) {
                          documentTxtABD1.append(category + " (" + quantity + ")");
                      } else {
                          documentTxtABD1.append(", " + category + " (" + quantity + ")");
                      }

                  }
              } else documentTxtABD1.append("No package");
          }catch (Exception e){

          }
            try {
                if (requestView_detailPojo.getShipmentsArray().size() > 0) {
                    if(Objects.equals(requestView_detailPojo.getShipmentsArray().get(0).get("LengthCm"), 0)){
                        dimentionDetailsTxtABD.setVisibility(View.VISIBLE);
                        dimentionDetailsTxtABD.append("No dimension");
                    }
                    else {
                        enterFieldDialog.findViewById(R.id.ll_di).setVisibility(View.VISIBLE);
                        ArrayList<HashMap<String, Object>> shipmentArray = requestView_detailPojo.getShipmentsArray();
                        Dimension_class.setDimension(recycler_dimension, Bid_RequestView_Detail.this, shipmentArray);
                    }
                }else{
                    dimentionDetailsTxtABD.setVisibility(View.VISIBLE);
                    dimentionDetailsTxtABD.append("No dimension");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }




            enterFieldDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setActiveTimeForBid() {
        try {
            if (!requestView_detailPojo.getDropDateTime().equals("")) {
                String dropOffTime = requestView_detailPojo.getDropDateTime().replace("\n", " ");
                String utcDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromDeviceForQuoteRequestETA(dropOffTime);
                String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiff(utcDateTime, true);
                if (minInStr.equals("Expired")) {
                    placeBidBtnList.setClickable(false);
                    placeBidBtnList.setEnabled(false);
                    placeBidBtnList.setAlpha(0.5f);
                    bidActiveForTxtInDetail.setText(minInStr);
                } else
                    bidActiveForTxtInDetail.setText("Bid Active For - " + minInStr);
            } else {
                bidActiveForTxtInDetail.setText("");
            }
        } catch (Exception e) {
            bidActiveForTxtInDetail.setText("");
        }
    }

    public void removeTimerForBidActiveFor() {
        if (BookingView.handlerForTimeCounter != null && TimerForBidActiveFor != null)
            BookingView.handlerForTimeCounter.removeCallbacks(TimerForBidActiveFor);
    }

    //******** Initialize bid detail map fragment ***************
    private void inItBidDetailMapFragment() {
        try {
            if (BookingDetail_New.mapFragmentBookingDetail != null)
                BookingDetail_New.mapFragmentBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewQuoteDetail));
            if (BookingDetail_New.gMapBookingDetail != null)
                BookingDetail_New.gMapBookingDetail = null;
            BookingDetail_New.mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
                BookingDetail_New.gMapBookingDetail = BookingDetail_New.mapFragmentBookingDetail.getMap();
                BookingDetail_New.gMapBookingDetail.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                //BookingDetail_New.gMapBookingDetail.setMyLocationEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomControlsEnabled(false);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setMyLocationButtonEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setCompassEnabled(false);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setRotateGesturesEnabled(true);
                BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomGesturesEnabled(true);

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

                // Stop scrolling of layout View when map scroll
                BookingDetail_New.mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        bidDetailScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBidDetailPageOpen = false;
        removeTimerForBidActiveFor();
        finish();
        requestDetailBidId = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBidDetailPageOpen = true;
        PushReceiver.IsOtherScreenOpen =true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBidDetailPageOpen = false;
    }

    private void setPickUpData(){
        findViewById(R.id.pickUpHeaderLayoutABD).setBackgroundResource(R.drawable.new_gray_back);
        findViewById(R.id.dropOffHeaderLayoutABD).setBackgroundResource(R.drawable.new_gary_backgroun);
        ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD)).setTextColor(Color.parseColor("#374350"));
        ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD)).setTextColor(Color.parseColor("#FFFFFF"));

        ((TextView) findViewById(R.id.pickDropCustomerNameABD)).setText(bidDetailsModel.getPickupContactName());
        ((TextView) findViewById(R.id.pickDropAddressABD)).setText(requestView_detailPojo.getPickUpAddress());
        String bookingPickDate = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + bidDetailsModel.getPickupDateTime());
        pickDropDate.setText(bookingPickDate);

        String bookingPickTime = LoginZoomToU.checkInternetwithfunctionality.getOnlyTimeFromServer("" +bidDetailsModel.getPickupDateTime());
        pickHeaderLayoutPickTimeTxtABD.setText(bookingPickTime);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pickUpHeaderLayoutABD:
                if (!isPickShowed) {
                   try {
                       isPickShowed = true;
                       findViewById(R.id.pickUpHeaderLayoutABD).setBackgroundResource(R.drawable.new_gray_back);
                       findViewById(R.id.dropOffHeaderLayoutABD).setBackgroundResource(R.drawable.new_gary_backgroun);
                       ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD)).setTextColor(Color.parseColor("#374350"));
                       ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD)).setTextColor(Color.parseColor("#FFFFFF"));
                       ((TextView) findViewById(R.id.pickupEnd)).setTextColor(Color.parseColor("#FFFFFF"));
                       ((TextView) findViewById(R.id.dropEnd)).setTextColor(Color.parseColor("#374350"));
                       ((TextView) findViewById(R.id.pickDropCustomerNameABD)).setText(bidDetailsModel.getPickupContactName());
                       ((TextView) findViewById(R.id.pickDropAddressABD)).setText(requestView_detailPojo.getPickUpAddress());
                       String bookingPickDate = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + bidDetailsModel.getPickupDateTime());
                       pickDropDate.setText(bookingPickDate);

                       String bookingPickTime = LoginZoomToU.checkInternetwithfunctionality.getOnlyTimeFromServer("" + bidDetailsModel.getPickupDateTime());
                       pickHeaderLayoutPickTimeTxtABD.setText(bookingPickTime);
                   }catch (Exception e){

                   }

                }
                break;
            case R.id.dropOffHeaderLayoutABD:
                if (isPickShowed) {
                    try {
                        isPickShowed = false;
                        findViewById(R.id.pickUpHeaderLayoutABD).setBackgroundResource(R.drawable.new_gary_backgroun);
                        findViewById(R.id.dropOffHeaderLayoutABD).setBackgroundResource(R.drawable.new_gray_back);
                        ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD)).setTextColor(Color.parseColor("#374350"));
                        ((TextView) findViewById(R.id.pickupEnd)).setTextColor(Color.parseColor("#374350"));
                        ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD)).setTextColor(Color.parseColor("#FFFFFF"));
                        ((TextView) findViewById(R.id.dropEnd)).setTextColor(Color.parseColor("#FFFFFF"));

                        ((TextView) findViewById(R.id.pickDropCustomerNameABD)).setText(bidDetailsModel.getDropContactName().toString());
                        ((TextView) findViewById(R.id.pickDropAddressABD)).setText(requestView_detailPojo.getDropOffAddress());
                        String bookingDropDate = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + bidDetailsModel.getDropDateTime());
                        pickDropDate.setText(bookingDropDate);

                        String bookingDropTime = LoginZoomToU.checkInternetwithfunctionality.getOnlyTimeFromServer("" + bidDetailsModel.getDropDateTime());
                        pickHeaderLayoutPickTimeTxtABD.setText(bookingDropTime);
                    }catch (Exception e){

                    }
                }
                break;



                case R.id.backFromBookingDetail:
                removeTimerForBidActiveFor();
                finish();
                requestDetailBidId = 0;
                break;

            case R.id.placeBidBtnList:
                Intent placeBidView = new Intent(Bid_RequestView_Detail.this, Alert_To_PlaceBid.class);
                placeBidView.putExtra("isFromBidDetail", 1);
                placeBidView.putExtra("requestView_detailPojo", requestView_detailPojo);
                placeBidView.putExtra("shipmentItems", shipmentItemBidDetail.getText().toString());
                startActivity(placeBidView);
                placeBidView = null;
                break;
            case R.id.chatBtnPlaceBidDetail:
                if (price.equals("null") || price.equals("")) {
                    cancelBidRequestConfirmationDialog(requestView_detailPojo.getOfferId(), true);
                } else if (isBidIsCanceled) {
                    cancelBidRequestConfirmationDialog(requestView_detailPojo.getOfferId(), true);
                } else {
                    Intent bidChatWithCustomer = new Intent(Bid_RequestView_Detail.this, ChatView_BidConfirmation.class);
                    bidChatWithCustomer.putExtra("BidIDForChat", requestView_detailPojo.getId());
                    bidChatWithCustomer.putExtra("CustomerNameForChat", requestView_detailPojo.getCustomer());
                    bidChatWithCustomer.putExtra("CustomerIDForChat", requestView_detailPojo.getCustomerID());
                    startActivity(bidChatWithCustomer);
                    bidChatWithCustomer = null;
                }
                break;
            case R.id.cancelBtnPlaceBidDetail:
                cancelBidRequestConfirmationDialog(requestView_detailPojo.getOfferId(), false);
                break;

        }
    }

    Dialog cancelBidRequestDialog;
    public void cancelBidRequestConfirmationDialog (final int offerId, final boolean isRejectBid) {

        if (cancelBidRequestDialog != null) {
            if (cancelBidRequestDialog.isShowing())
                cancelBidRequestDialog.dismiss();
            cancelBidRequestDialog = null;
        }

        cancelBidRequestDialog = new Dialog(Bid_RequestView_Detail.this);
        cancelBidRequestDialog.setCancelable(false);
        cancelBidRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cancelBidRequestDialog.setContentView(R.layout.logoutwindow);

        Window window = cancelBidRequestDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView cancelBidRequestMsgTxt = (TextView) cancelBidRequestDialog.findViewById(R.id.logoutWindowMessageText);

        if (isRejectBid)
            cancelBidRequestMsgTxt.setText("Are you sure, you want to Reject this bid request?");
        else
            cancelBidRequestMsgTxt.setText("Are you sure, you want to Cancel this bid request?");

        Button cancelBidRequestNoBtn = (Button) cancelBidRequestDialog.findViewById(R.id.logoutWindowCancelBtn);

        cancelBidRequestNoBtn.setText("No");
        cancelBidRequestNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBidRequestDialog.dismiss();
            }
        });

        Button cancelBidRequestYesBtn = (Button) cancelBidRequestDialog.findViewById(R.id.logoutWindowLogoutBtn);

        cancelBidRequestYesBtn.setText("Yes");
        cancelBidRequestYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBidRequestDialog.dismiss();
                if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    if (isRejectBid)
                        CancelBidRequest(offerId, true);
                        //new CancelBidRequest(offerId, true).execute();
                    else
                        CancelBidRequest(offerId, false);
                        //new CancelBidRequest(offerId, false).execute();
                else
                    DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "No Network!", "No network connection, Please try again later.");
            }
        });
        cancelBidRequestDialog.show();
    }

    private void CancelBidRequest(int offerId,boolean isRejectBid){
        final String[] responseStrCancelBidRequest = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(Bid_RequestView_Detail.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                List<RequestView_DetailPojo> requestPojoList = new ArrayList<RequestView_DetailPojo>();
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    if (isRejectBid)
                        responseStrCancelBidRequest[0] = handler.rejectBidOfCourier(offerId);
                    else
                        responseStrCancelBidRequest[0] = handler.cancelBidRequest(offerId);
                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (!responseStrCancelBidRequest[0].equals("")) {
                        if(new JSONObject(responseStrCancelBidRequest[0]).getBoolean("success")){
                            if (isRejectBid) {
                                RequestView.COUNT_FOR_NOTBIDYET--;
                                SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
                                new LoadChatBookingArray(Bid_RequestView_Detail.this, 2);  //********* Refresh Bid chat only *******
                                redirectToBidRequestList();
                                Toast.makeText(Bid_RequestView_Detail.this, "Bid request rejected successfully", Toast.LENGTH_LONG).show();
                            } else {
                                redirectToBidRequestList();
                                Toast.makeText(Bid_RequestView_Detail.this, "Bid request cancelled successfully", Toast.LENGTH_LONG).show();
                            }
                        } else
                            DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "Sorry!", "Something went wrong, Please try again later.");
                    } else
                        DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "Server error!", "Something went wrong, Please try again later.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "Server error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();


    }



    //************** Redirect to bid request list ***********
    private void redirectToBidRequestList() {
        ConfirmPickUpForUserName.isDropOffSuccessfull = 13;
        Intent callCompleteBookingfragment = new Intent(Bid_RequestView_Detail.this, SlideMenuZoom2u.class);
        callCompleteBookingfragment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(callCompleteBookingfragment);
        finish();
        callCompleteBookingfragment = null;
    }

    //************** Enable package image arrow button *************
    private void enableImageArrowBtn(ImageView imageArrowBtn){
        imageArrowBtn.setAlpha(1.0f);
        imageArrowBtn.setEnabled(true);
        imageArrowBtn.setClickable(true);
    }

    //************** Disable package image arrow button *************
    private void disableImageArrowBtn(ImageView imageArrowBtn){
        imageArrowBtn.setAlpha(0.5f);
        imageArrowBtn.setEnabled(false);
        imageArrowBtn.setClickable(false);
    }

    private void GetMyRequestOfCustomerDetail(){
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(Bid_RequestView_Detail.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                WebserviceHandler handler = new WebserviceHandler();
                webserviceMyRequestList = handler.getQuoteRequestOfCustomerDetails(requestIdFromBidListView);
            }

            @Override
            public void onPostExecute() {
                try {
                    JSONObject jsonObjectOflIstDelivery = new JSONObject(webserviceMyRequestList);
                    JSONArray jsonArrayOfListDelivery = jsonObjectOflIstDelivery.getJSONArray("data");
                    jsonObjectOflIstDelivery = null;
                    JSONObject objDeliveryDetail = jsonArrayOfListDelivery.getJSONObject(0);
                    jsonArrayOfListDelivery = null;
                    requestView_detailPojo = new RequestView_DetailPojo();

                    Gson gson = new Gson();
                    bidDetailsModel = gson.fromJson(String.valueOf(objDeliveryDetail), BidDetailsModel.class);



                    requestView_detailPojo.set$id(objDeliveryDetail.getString("$id"));
                    requestView_detailPojo.setId(objDeliveryDetail.getInt("RequestId"));
                    if (!objDeliveryDetail.getString("Notes").equals("") || !objDeliveryDetail.getString("Notes").equals(null))
                        requestView_detailPojo.setNotes(" "+objDeliveryDetail.getString("Notes"));
                    else
                        requestView_detailPojo.setNotes(" Delivery notes");

                    String PickupDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDeliveryDetail.getString("PickupDateTime"));
                    String[] PickupDateTimeSplit = PickupDateTime.split(" ");
                    requestView_detailPojo.setPickUpDateTime(PickupDateTimeSplit[0] + "\n" + PickupDateTimeSplit[1] +" "+ PickupDateTimeSplit[2]);
                    PickupDateTimeSplit = null;

                    String dropOffDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDeliveryDetail.getString("DropDateTime"));
                    String[] dropOffDateTimeSplit = dropOffDateTime.split(" ");
                    requestView_detailPojo.setDropDateTime(dropOffDateTimeSplit[0]+ "\n" + dropOffDateTimeSplit[1] +" "+ dropOffDateTimeSplit[2] );
                    dropOffDateTimeSplit = null;

                    requestView_detailPojo.setVehicle(objDeliveryDetail.getString("Vehicle"));
                    requestView_detailPojo.setCustomer(objDeliveryDetail.getString("Customer"));
                    requestView_detailPojo.setExtraLargeQuoteRef(objDeliveryDetail.getString("ExtraLargeQuoteRef"));
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
                    requestView_detailPojo.setCustomerID(objDeliveryDetail.getString("CustomerId"));
                    requestView_detailPojo.setAverageBid(objDeliveryDetail.getString("AverageBid"));
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

                    JSONArray images_array = objDeliveryDetail.getJSONArray("PackageImages");
                    images_array_length = images_array.length();
                    String total_images = "";
                    if (images_array_length > 0) {
                        for (int j = 0; j < images_array_length; j++) {
                            if (total_images != "")
                                total_images = total_images + "," + images_array.getString(j);
                            else
                                total_images = images_array.getString(j);
                        }
                    }
                    requestView_detailPojo.setPackageImages(total_images);
                    offerId = objDeliveryDetail.getJSONObject("OfferDetails").getInt("OfferId");
                    price = objDeliveryDetail.getJSONObject("OfferDetails").getString("Price");
                    cretedDateTime = objDeliveryDetail.getJSONObject("OfferDetails").getString("QuoteDateTime");

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
                        objOFShipments = null;
                    }

                    requestView_detailPojo.setShipmentsArray(arrayOfShipments);

                    requestView_detailPojo.setPackagingNotes(objDeliveryDetail.getString("PackagingNotes"));

                    TextView weight = findViewById(R.id.weight);


                    try {
                        double totalWeight = 0.0;
                        if (objDeliveryDetail.getJSONArray("Shipments").length() > 0) {
                            for (int i = 0; i < objDeliveryDetail.getJSONArray("Shipments").length(); i++) {
                                totalWeight = totalWeight +objDeliveryDetail.getJSONArray("Shipments").getJSONObject(i).getDouble("TotalWeightKg");

                            }
                            if (totalWeight > 0.0) {
                                weight.setVisibility(View.VISIBLE);
                                weight.setText(" "+totalWeight + "kg");
                            } else
                                weight.setVisibility(View.GONE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        weight.setText(" 0kg");
                    }




                    //******** Initialize bid detail map fragment ********
                    setUIContentvaluesOfBidDetail();    //******* Set UI content for Bid detail
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(Bid_RequestView_Detail.this, "Error!", "Something went wrong, Please try again.");
                }
                Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
            }
        }.execute();

    }


    //******* Set UI content for Bid detail *************
    private void setUIContentvaluesOfBidDetail() {
        if (price.equals("null") || price.equals("")) {
            cancelBtnPlaceBidDetail.setVisibility(View.GONE);
            placeBidBtnList.setText("Place bid");
            placeBidBtnList.setClickable(true);
            placeBidBtnList.setEnabled(true);
            placeBidBtnList.setAlpha(1.0f);
            chatBtnPlaceBidDetail.setVisibility(View.VISIBLE);
            chatBtnPlaceBidDetail.setText("Reject Bid");
            chatBtnPlaceBidDetail.setTextColor(getResources().getColor(R.color.gold_dark));
            chatBtnPlaceBidDetail.setBackgroundResource(R.drawable.new_boder);
            btnNotifyUnreadCountDetail.setVisibility(View.GONE);
            setActiveTimeForBid();
            callTimerBidActiveFor();
        } else {
            chatBtnPlaceBidDetail.setVisibility(View.VISIBLE);
            if (isBidIsCanceled) {
                setActiveTimeForBid();
                cancelBtnPlaceBidDetail.setVisibility(View.VISIBLE);
                cancelBtnPlaceBidDetail.setClickable(false);
                cancelBtnPlaceBidDetail.setEnabled(false);
                cancelBtnPlaceBidDetail.setAlpha(0.5f);
                cancelBtnPlaceBidDetail.setText("Cancelled");
                placeBidBtnList.setText("Place Bid");
                placeBidBtnList.setClickable(true);
                placeBidBtnList.setEnabled(true);
                placeBidBtnList.setAlpha(1.0f);
                chatBtnPlaceBidDetail.setText("Reject Bid");
                chatBtnPlaceBidDetail.setTextColor(getResources().getColor(R.color.gold_dark));
                chatBtnPlaceBidDetail.setBackgroundResource(R.drawable.new_boder);
                btnNotifyUnreadCountDetail.setVisibility(View.GONE);
            } else {
                cancelBtnPlaceBidDetail.setVisibility(View.VISIBLE);
                cancelBtnPlaceBidDetail.setClickable(true);
                cancelBtnPlaceBidDetail.setEnabled(true);
                cancelBtnPlaceBidDetail.setAlpha(1.0f);
                cancelBtnPlaceBidDetail.setText("Cancel bid");
                placeBidBtnList.setText("Bid placed");
                placeBidBtnList.setClickable(false);
                placeBidBtnList.setEnabled(false);
                placeBidBtnList.setAlpha(0.5f);
                chatBtnPlaceBidDetail.setBackgroundResource(R.drawable.green_bg_withborder);
                chatBtnPlaceBidDetail.setTextColor(getResources().getColor(R.color.white));
                chatBtnPlaceBidDetail.setText("Chat Customer");
                int unreadChatCount = getIntent().getIntExtra("UnReadChatCount", 0);
                if (unreadChatCount > 0) {
                    btnNotifyUnreadCountDetail.setVisibility(View.VISIBLE);
                    btnNotifyUnreadCountDetail.setText("" + unreadChatCount);
                } else
                    btnNotifyUnreadCountDetail.setVisibility(View.GONE);
            }
        }

        String bookingPickTime = LoginZoomToU.checkInternetwithfunctionality.getOnlyTimeFromServer("" + bidDetailsModel.getPickupDateTime());
        String bookingDropTime = LoginZoomToU.checkInternetwithfunctionality.getOnlyTimeFromServer("" + bidDetailsModel.getDropDateTime());
        ((TextView) findViewById(R.id.pickupEnd)).setText(bookingPickTime);
        ((TextView) findViewById(R.id.dropEnd)).setText(bookingDropTime);

        distanceTxtPlaceBidDetail.setText(" "+requestView_detailPojo.getDistance());
        totalBidValueTxtPlaceBidDetail.setText(" " + requestView_detailPojo.getTotalBids());

        ((TextView) findViewById(R.id.pickDropAddressABD)).setText(requestView_detailPojo.getPickUpAddress());

        if (!requestView_detailPojo.getPackagingNotes().equals(""))
            packagedItemBidDetail.setVisibility(View.VISIBLE);
        else
            packagedItemBidDetail.setVisibility(View.GONE);

        if (requestView_detailPojo.getNotes().equals(""))
            notesValueTxtPlaceBidDetail.setText("No delivery notes");
        else
            notesValueTxtPlaceBidDetail.setText(requestView_detailPojo.getNotes());

        if (requestView_detailPojo.isSuggestedPrice()) {
            findViewById(R.id.suggest).setVisibility(View.VISIBLE);
            findViewById(R.id.suggestedPriceTxtPlaceBidDetail).setVisibility(View.VISIBLE);
            suggestedPriceValueTxtPlaceBidDetail.setVisibility(View.VISIBLE);
            suggestedPriceValueTxtPlaceBidDetail.setText("$" + (requestView_detailPojo.getCourierPrice()));
        } else {
            findViewById(R.id.suggest).setVisibility(View.GONE);
            findViewById(R.id.suggestedPriceTxtPlaceBidDetail).setVisibility(View.GONE);
            suggestedPriceValueTxtPlaceBidDetail.setVisibility(View.GONE);
        }

        ((TextView)findViewById(R.id.vehicleTxtABD)).setText(requestView_detailPojo.getVehicle());
        shipmentItemBidDetail.setText("");
        if (requestView_detailPojo.getShipmentsArray().size() > 0) {
            for (int i = 0; i < requestView_detailPojo.getShipmentsArray().size(); i++) {
                ArrayList<HashMap<String, Object>> shipmentArray = requestView_detailPojo.getShipmentsArray();
                String category = "" + shipmentArray.get(i).get("Category");
                String quantity = "" + shipmentArray.get(i).get("Quantity");
                int lengthCm = (Integer) shipmentArray.get(i).get("LengthCm");
                int widthCm = (Integer) shipmentArray.get(i).get("WidthCm");
                int heightCm = (Integer) shipmentArray.get(i).get("HeightCm");
                if (i == 0) {
                     if(lengthCm==0 && widthCm == 0 && heightCm == 0){
                         shipmentItemBidDetail.append(category + " (" + quantity + ")");
                         shipmentItemBidDetail1.append(category + " (" + quantity + ")");
                         txtShipmentDetailsBidDetail.append("No dimension");
                     }else {
                         shipmentItemBidDetail.append(category + " (" + quantity + ")");
                         shipmentItemBidDetail1.append(category + " (" + quantity + ")");
                         txtShipmentDetailsBidDetail.append(category + ":- " + "Length(cm) " + lengthCm + ", Width(cm) " + widthCm + ", Height(cm) " + heightCm + ", Item weight(kg) " + shipmentArray.get(i).get("ItemWeightKg") + ", Total weight(kg) " + shipmentArray.get(i).get("TotalWeightKg"));
                     }
                     } else {
                    shipmentItemBidDetail.append(", " + category + " (" + quantity + ")");
                    shipmentItemBidDetail1.append(", " + category + " (" + quantity + ")");
                    txtShipmentDetailsBidDetail.append("\n\n" + category + ":- " + "Length(cm) " + lengthCm + ", Width(cm) " + widthCm + ", Height(cm) " + heightCm + ", Item weight(kg) " + shipmentArray.get(i).get("ItemWeightKg") + ", Total weight(kg) " + shipmentArray.get(i).get("TotalWeightKg"));
                }

            }
        }else    shipmentItemBidDetail1.append("No package");
       /* findViewById(R.id.show_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (requestView_detailPojo.getPackageImages() != "") {
                    BidImageDialog.alertDialogToFinishView(requestView_detailPojo.getPackageImages(),Bid_RequestView_Detail.this,images_array_length,bidDetailMainLayout);
                }
            }
        });*/

        inItBidDetailMapFragment();
        setPickUpData();
    }


}
