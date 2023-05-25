package com.zoom2u;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.animations.ExpandCollapseAnimation;
import com.customnotify_event.CustomNotification_View;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newnotfication.view.NewBooking_Notification;
import com.newnotfication.view.bookings_view_offer.CounterOffers;
import com.newnotfication.view.bookings_view_offer.ShowOfferForBookings;
import com.suggestprice_team.courier_team.community.CommunityBookingActivity;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.booking.vc.CompletedView;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dimension.Dimension_class;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class BookingDetail_New extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener{

    int PERMISSION_ID=22;
    FusedLocationProviderClient mFusedLocationClient;
    public static WorkaroundMapFragment mapFragmentBookingDetail;
    public static GoogleMap gMapBookingDetail;

    /** This integer will uniquely define the dialog to be used for displaying time picker.*/
    static final int TIME_DIALOG_BD = 0;
    private ScrollView scrollViewNewBD;
    private TextView countChatBookingDetail, secondHeaderTxtBD, vehicelValueTxtBD, distanceValueTxtBD, pickUpTimeValueTxtBD, deliveryTimeValueTxtBD,
            pickUpContactNameTxtBD, pickUpSuburbTxtBD, dropOffContactNameTxtBD, dropOffSuburbTxtBD, priceValueTxtBD;

    private TextView suggestPriceTxt, priceTxtForGSTInfo;

    private RelativeLayout documentLayoutBD;
    private TextView documentTxtBD, dimentionDetailsTxtBD, viewDimensionTxtBD;

    private boolean isRejectBooking_NBD;
     boolean isshow;

    private ProgressDialog progressDialogForNBD;

    private double dropoffLatitude, dropoffLongitude, pickUpLatitude, pickUpLongitude;

    private All_Bookings_DataModels newBookingDetailModel;
    private Dialog dialogInNOotificationUI;

    //************ ETA dialog fields *********
    private TextView etaTitleTextBD, etaMsgTextBD;
    private ImageView etaCancelBtnBD, etaDoneBtnBD, selectPickUpEtaBD;
    private RelativeLayout bookingETADialogBD, etaTimeLayoutBD,headerLayoutAllTitleBar;

    LinearLayout txtWithSuggestPriceBtnLayout;

    //*********** Suggest price view ********
    LinearLayout priceSubmitView;
    TextView suggestPriceHeaderTxtBD;
    EditText edtSuggestPriceTxt;
    Button cancelSuggestPriceBtnBD;
    Button submitSuggestPriceBtnBD;

    String courierSuggestedPrice;

    private String uploadtimeToServer;
    private int pHour;
    private int pMinute;
    Window window;
    ImageView backBtnHeader;
    Spinner bidActivePeriodSpinnerToPlaceBid;
    private int bidActivePeriodInterval = 15;

    private LinearLayout ll_booking_due_day;

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
    protected void onResume() {
        super.onResume();
        PushReceiver.IsOtherScreenOpen =true;
        //************ Show unread chat icon if available **************
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatBookingDetail);
        SlideMenuZoom2u.countChatBookingView = countChatBookingDetail;
    }

    /*************  Save activity state when process killed in background*************/
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        try {
            if(!isshow)
            {
                outState.putBoolean("SetRoutific", WebserviceHandler.isRoutific);
                outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
                outState.putParcelable("NewBookingDetailModel", newBookingDetailModel);
                outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
            }

        //    outState.putParcelableArrayList("NewBookingArray", BookingView.bookingListArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*************  Restore activity state when resuming app after process killed in background*************/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view content
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getConfiguration().screenWidthDp<=360)
        {
            setContentView(R.layout.new_booking_detail_view_small);
        }
        else
        {
            setContentView(R.layout.new_booking_detail_view);
        }
        window = BookingDetail_New.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }


        isshow = getIntent().getExtras().getBoolean("my_boolean_key");
        Log.d("hsdsuild", "onCreate: "+isshow);

        PushReceiver.IsOtherScreenOpen =true;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view contents
    }

    //******** Initialize new booking detail view contents ***************
    @SuppressLint("SuspiciousIndentation")
    private void inItNewBookingDetailViewContents(Bundle savedInstanceState) {
        try {
            if (savedInstanceState == null) {
                if (isshow)
                {
                    int itemPositionNewBooking = getIntent().getBundleExtra("NewBookingBundle").getInt("position", 0);
                    newBookingDetailModel = CommunityBookingActivity.list.get(itemPositionNewBooking);
                }else {
                    int itemPositionNewBooking = getIntent().getBundleExtra("NewBookingBundle").getInt("position", 0);
                    newBookingDetailModel = (All_Bookings_DataModels) BookingView.bookingListArray.get(itemPositionNewBooking);
                }

            } else {
                try {
                    WebserviceHandler.isRoutific = savedInstanceState.getBoolean("SetRoutific");
                    ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                    newBookingDetailModel = savedInstanceState.getParcelable("NewBookingDetailModel");
                    BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
            //      BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("NewBookingArray");
                    Functional_Utility.removeLocationTimer();
                    Intent serviceIntent = new Intent(this, ServiceForSendLatLong.class);
                    startService(serviceIntent);
                    serviceIntent = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            savedInstanceState.clear();
            getIntent().getBundleExtra("NewBookingBundle").clear();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            suggestPriceTxt = (TextView) findViewById(R.id.suggestPriceTxt);
            suggestPriceTxt.setOnClickListener(this);
            if (isshow) {
                suggestPriceTxt.setVisibility(View.GONE);
            }else {
                suggestPriceTxt.setVisibility(View.VISIBLE);
            }

            headerLayoutAllTitleBar=findViewById(R.id.headerLayoutAllTitleBar);
            backBtnHeader=findViewById(R.id.backBtnHeader);
            backBtnHeader.setOnClickListener(this);

           ((TextView) findViewById(R.id.titleBookingDetail)).setText("#" + newBookingDetailModel.getBookingRefNo());

            if(MainActivity.isIsBackGroundGray()){
                window.setStatusBarColor(Color.parseColor("#374350"));
                headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
            }else{
                window.setStatusBarColor(Color.parseColor("#00A6E2"));
                headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
            }

            //********** To Show Test booking alert *************
            String firstCharOfReferenceNo = newBookingDetailModel.getBookingRefNo().substring(0, 1);
            if (firstCharOfReferenceNo.equalsIgnoreCase("T"))
                NewBooking_Notification.showTestBookingAlert(BookingDetail_New.this);

            countChatBookingDetail = (TextView) findViewById(R.id.countChatBookingDetail);

            countChatBookingDetail.setVisibility(View.GONE);
            SlideMenuZoom2u.countChatBookingView = countChatBookingDetail;



            priceTxtForGSTInfo = (TextView) findViewById(R.id.priceTxtForGSTInfo);

            priceTxtForGSTInfo.setOnClickListener(this);

            findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);

            if (scrollViewNewBD == null)
                scrollViewNewBD = (ScrollView) findViewById(R.id.scrollViewNewBD);

            askForLocationPermission(BookingDetail_New.this);

            inItNewBookingDetailMapFragment();          //******** Initialize new booking detail view map

            if (secondHeaderTxtBD == null)
                secondHeaderTxtBD = (TextView) findViewById(R.id.secondHeaderTxtBD);
            if (ll_booking_due_day==null)
            ll_booking_due_day=findViewById(R.id.ll_booking_due_day);

            if (priceValueTxtBD == null)
                priceValueTxtBD = (TextView) findViewById(R.id.priceValueTxtBD);

            //   ***********  Change color for next day booking  **************
            try {
                 if (Functional_Utility.checkDateIsToday(newBookingDetailModel.getPickupDateTime())==false) {
                ll_booking_due_day.setBackgroundResource(R.drawable.new_green_round);
                priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.green_bg));
                secondHeaderTxtBD.setText("ⓘ This delivery is for future date");
            } else
            {
                ll_booking_due_day.setBackgroundResource(R.drawable.new_white_back_blue);
                priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.base_color1));
                secondHeaderTxtBD.setText("ⓘ This delivery is due today");
            }

            }catch (Exception ex){
            }


            //   **************** ----------------- *****************

            findViewById(R.id.newCustomerTxtInBD);
            if (newBookingDetailModel.isNewCustomer())
                findViewById(R.id.newCustomerTxtInBD).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.newCustomerTxtInBD).setVisibility(View.GONE);

            View newBDDistanceView = findViewById(R.id.newBDDistanceView);
            newBDDistanceView.findViewById(R.id.verticleTxt1BD);
            ((TextView) newBDDistanceView.findViewById(R.id.verticleTxt1BD)).setText("VEHICLE");
            if (vehicelValueTxtBD == null)
                vehicelValueTxtBD = (TextView) newBDDistanceView.findViewById(R.id.verticleTxt2BD);
            vehicelValueTxtBD.setText(newBookingDetailModel.getVehicle());

            View newBDValueView = findViewById(R.id.newBDValueView);
            newBDValueView.findViewById(R.id.verticleTxt1BD);
            ((TextView) newBDValueView.findViewById(R.id.verticleTxt1BD)).setText("DISTANCE");
            if (distanceValueTxtBD == null)
                distanceValueTxtBD = (TextView) newBDValueView.findViewById(R.id.verticleTxt2BD);
            distanceValueTxtBD.setText(newBookingDetailModel.getDistance() + "");

            View newBDPickupTimeView = findViewById(R.id.newBDPickupView);
            newBDPickupTimeView.findViewById(R.id.verticleTxt1BD);
            ((TextView) newBDPickupTimeView.findViewById(R.id.verticleTxt1BD)).setText("PICKUP");
            if (pickUpTimeValueTxtBD == null)
                pickUpTimeValueTxtBD = (TextView) newBDPickupTimeView.findViewById(R.id.verticleTxt2BD);
            Functional_Utility.setDateTimeFromServerToPerticularField(pickUpTimeValueTxtBD, newBookingDetailModel.getPickupDateTime(), false);

            View newBDDeliveryTimeView = findViewById(R.id.newBDDeliveryView);
            newBDDeliveryTimeView.findViewById(R.id.verticleTxt1BD);
            ((TextView) newBDDeliveryTimeView.findViewById(R.id.verticleTxt1BD)).setText("DELIVER BY");
            if (deliveryTimeValueTxtBD == null)
                deliveryTimeValueTxtBD = (TextView) newBDDeliveryTimeView.findViewById(R.id.verticleTxt2BD);
            Functional_Utility.setDateTimeFromServerToPerticularField(deliveryTimeValueTxtBD, newBookingDetailModel.getDropDateTime(), false);

            View pickAddressViewBD = findViewById(R.id.pickAddressViewBD);
            pickAddressViewBD.findViewById(R.id.addressHeaderTxtBD);
            ((TextView) pickAddressViewBD.findViewById(R.id.addressHeaderTxtBD)).setText("Pickup");
            if (pickUpContactNameTxtBD == null)
                pickUpContactNameTxtBD = (TextView) pickAddressViewBD.findViewById(R.id.contactNameTxtBD);
            pickUpContactNameTxtBD.setText("" + newBookingDetailModel.getPick_ContactName());

            if (pickUpSuburbTxtBD == null)
                pickUpSuburbTxtBD = (TextView) pickAddressViewBD.findViewById(R.id.suburbTxtBD);
            pickUpSuburbTxtBD.setText("" + newBookingDetailModel.getPick_Suburb());

            View dropAddressViewBD = findViewById(R.id.dropAddressViewBD);
            dropAddressViewBD.findViewById(R.id.addressHeaderTxtBD);
            ((TextView) dropAddressViewBD.findViewById(R.id.addressHeaderTxtBD)).setText("Drop");
            if (dropOffContactNameTxtBD == null)
                dropOffContactNameTxtBD = (TextView) dropAddressViewBD.findViewById(R.id.contactNameTxtBD);
            dropOffContactNameTxtBD.setText("" + newBookingDetailModel.getDrop_ContactName());

            if (dropOffSuburbTxtBD == null)
                dropOffSuburbTxtBD = (TextView) dropAddressViewBD.findViewById(R.id.suburbTxtBD);
            dropOffSuburbTxtBD.setText("" + newBookingDetailModel.getDrop_Suburb());

            LinearLayout rootView = findViewById(R.id.dimension_view);

            try {
                if (newBookingDetailModel.getShipmentsArray()!=null&&newBookingDetailModel.getShipmentsArray().size() > 0) {
                    ArrayList<HashMap<String, Object>> shipmentArray = newBookingDetailModel.getShipmentsArray();
                     new Dimension_class().setTheViewsForDimensions(rootView,this,shipmentArray);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }


            String priceInt = Functional_Utility.returnCourierPrice((Double) newBookingDetailModel.getPrice());
            priceValueTxtBD.setText("$" + priceInt);

            findViewById(R.id.rejectBtnBD).setOnClickListener(this);
            findViewById(R.id.acceptBtnBD).setOnClickListener(this);

            txtWithSuggestPriceBtnLayout = (LinearLayout) findViewById(R.id.txtWithSuggestPriceBtnLayout);

            //*********** ************* Courier suggest price work 12-july-2018 *************** ***********
            priceSubmitView = (LinearLayout) findViewById(R.id.priceSubmitView);
            suggestPriceHeaderTxtBD = (TextView) findViewById(R.id.suggestPriceHeaderTxtBD);
            edtSuggestPriceTxt = (EditText) findViewById(R.id.edtSuggestPriceTxt);
            cancelSuggestPriceBtnBD = (Button) findViewById(R.id.cancelSuggestPriceBtnBD);
            cancelSuggestPriceBtnBD.setOnClickListener(this);
            submitSuggestPriceBtnBD = (Button) findViewById(R.id.submitSuggestPriceBtnBD);
            submitSuggestPriceBtnBD.setOnClickListener(this);

            this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            try {
                if (!newBookingDetailModel.getCourierSuggestedPrice().equals("") && !newBookingDetailModel.getCourierSuggestedPrice().equals("null")
                        && newBookingDetailModel.getCourierSuggestedPrice() != null) {
                    ExpandCollapseAnimation.expand(priceSubmitView);
                    ExpandCollapseAnimation.collapse(txtWithSuggestPriceBtnLayout);
                    edtSuggestPriceTxt.setText(newBookingDetailModel.getCourierSuggestedPrice());
                    edtSuggestPriceTxt.setTextSize(24f);
                    edtSuggestPriceTxt.requestFocus();
                } else {
                    ExpandCollapseAnimation.expand(txtWithSuggestPriceBtnLayout);
                    ExpandCollapseAnimation.collapse(priceSubmitView);
                    edtSuggestPriceTxt.setTextSize(14f);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ExpandCollapseAnimation.expand(txtWithSuggestPriceBtnLayout);
                ExpandCollapseAnimation.collapse(priceSubmitView);
                edtSuggestPriceTxt.setTextSize(14f);
            }

            findViewById(R.id.view_offer).setOnClickListener(this);
            bidActivePeriodSpinnerToPlaceBid= (Spinner)findViewById(R.id.bidActivePeriodSpinnerToPlaceBid);
            String[] pkgPosition = {"15 mins", "30 mins", "45 mins", "1 hour", "1 hour 15 mins", "1 hour 30 mins", "1 hour 45 mins", "2 hours", "4 hours", "8 hours", "12 hours", "24 hours"};
            ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(this, R.layout.spinneritemxml1, pkgPosition) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTextColor(Color.parseColor("#45515b"));
                    return v;
                }
                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                    View v =super.getDropDownView(position, convertView, parent);

                    ((TextView) v).setTextColor(Color.parseColor("#45515b"));
                    return v;
                }
            };


            bidActivePeriodSpinnerToPlaceBid.setAdapter(adapter_Position);
            bidActivePeriodSpinnerToPlaceBid.setSelection(1);
            bidActivePeriodSpinnerToPlaceBid.setOnItemSelectedListener(this);



            edtSuggestPriceTxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (start < 0)
                        edtSuggestPriceTxt.setTextSize(14f);
                    else
                        edtSuggestPriceTxt.setTextSize(24f);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0)
                        edtSuggestPriceTxt.setTextSize(14f);
                }
            });

            edtSuggestPriceTxt.setOnTouchListener(new View.OnTouchListener() {
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
            });
            //*********** Courier suggest price work 12-july-2018 ***********

            //*********** ETA dialog view init ***********
            if (bookingETADialogBD == null)
                bookingETADialogBD = (RelativeLayout) findViewById(R.id.bookingETADialogBD);
            bookingETADialogBD.setVisibility(View.GONE);
            if (etaTimeLayoutBD == null)
                etaTimeLayoutBD = (RelativeLayout) findViewById(R.id.etaTimeLayoutBD);
            if (selectPickUpEtaBD == null)
                selectPickUpEtaBD = (ImageView) findViewById(R.id.selectPickUpEtaBD);
            if (etaTitleTextBD == null)
                etaTitleTextBD = (TextView) findViewById(R.id.etaTitleTextBD);

            if (etaMsgTextBD != null)
                etaMsgTextBD = null;
            etaMsgTextBD = (TextView) findViewById(R.id.etaMsgTextBD);
            if (etaCancelBtnBD == null)
                etaCancelBtnBD = (ImageView) findViewById(R.id.etaCancelBtnBD);
            if (etaDoneBtnBD == null)
                etaDoneBtnBD = (ImageView) findViewById(R.id.etaDoneBtnBD);

            etaTimeLayoutBD.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onClick(View v) {
                    selectPickUpEtaBD.setImageResource(R.drawable.icon_up);
                    /** Get the current time */
                    final Calendar cal = Calendar.getInstance();
                    pHour = cal.get(Calendar.HOUR_OF_DAY);
                    pMinute = cal.get(Calendar.MINUTE);

                    showDialog(TIME_DIALOG_BD);
                }
            });

            etaCancelBtnBD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bookingETADialogBD.setVisibility(View.GONE);
                }
            });

            etaDoneBtnBD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        bookingETADialogBD.setVisibility(View.GONE);
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            //new AcceptBookingForPickupInBDAsyncTask().execute();
                            AcceptBookingForPickupInBDAsyncTask();
                        else
                            DialogActivity.alertDialogView(BookingDetail_New.this, "No Network !", "No network connection, Please try again later.");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            //*********** **************** ************
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************ Ask for location permission ***************//
    public static void askForLocationPermission(final Context locationPermissionContext) {
        if ((int) Build.VERSION.SDK_INT >= 23) {
            String[] permission = {android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION};
            if (ContextCompat.checkSelfPermission(locationPermissionContext, permission[0]) == PackageManager.PERMISSION_DENIED ||
                    ContextCompat.checkSelfPermission(locationPermissionContext, permission[1]) == PackageManager.PERMISSION_DENIED) {

                Dialog enterFieldDialog  = new Dialog(locationPermissionContext);

                enterFieldDialog.setCancelable(true);
                enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                enterFieldDialog.setContentView(R.layout.permission_dailog);

                Window window = enterFieldDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);
                TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

                enterFieldDialogHEader.setText("Permission required!");

                TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

                enterFieldDialogMsg.setText("Z2U for carriers is a location based application" +
                        " based on location services where we need to access your location");

                Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                enterFieldDialogDoneBtn.setText("Got it!");

                enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions((Activity) locationPermissionContext,
                                new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        enterFieldDialog.dismiss();
                    }
                });
                enterFieldDialog.show();

            }
        }
        //************ Ask for location permission ***************//
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int intervalMul = position + 1;
        switch (position) {
            case 0: case 1: case 2: case 3: case 4: case 5: case 6:case 7:
                bidActivePeriodInterval = 15 * intervalMul;
                break;
            case 8:
                bidActivePeriodInterval = 4 * 60;
                break;
            case 9:
                bidActivePeriodInterval = 8 * 60;
                break;
            case 10:
                bidActivePeriodInterval = 12 * 60;
                break;
            case 11:
                bidActivePeriodInterval = 24 * 60;
                break;
            default:
                bidActivePeriodInterval = 30;
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");
                            BookingDetail_New.gMapBookingDetail.addMarker(markerOptions);
                        }
                    }
                });
            }
        }
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }



    //******** Initialize new booking detail view map ***************
    private void inItNewBookingDetailMapFragment() {
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
                getLastLocation();
                try {
                    pickUpLatitude = Double.parseDouble(newBookingDetailModel.getPick_GPSX());
                    pickUpLongitude = Double.parseDouble(newBookingDetailModel.getPick_GPSY());

                    gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(pickUpLatitude, pickUpLongitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin_new)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    dropoffLatitude = Double.parseDouble(newBookingDetailModel.getDrop_GPSX());
                    dropoffLongitude = Double.parseDouble(newBookingDetailModel.getDrop_GPSY());

                    gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(dropoffLatitude, dropoffLongitude))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin_new)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    gMapBookingDetail.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            try {
                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(new LatLng(pickUpLatitude, pickUpLongitude))
                                        .include(new LatLng(dropoffLatitude, dropoffLongitude))
                                        .build();
                                gMapBookingDetail.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                                bounds = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.274398, 133.775136), 8));
                            }
                        }
                    });

                    // Stop scrolling of layout View when map scroll
                    mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
                        @Override
                        public void onTouch() {
                            scrollViewNewBD.requestDisallowInterceptTouchEvent(true);
                        }
                    });

//            new ShowPolyline_DirectionAPI(BookingDetail_New.this, BookingDetail_New.gMapBookingDetail,
//                    new LatLng(pickUpLatitude, pickUpLongitude), new LatLng(dropoffLatitude, dropoffLongitude));

                    List<LatLng> list = null;
                    try {
                        list = PolyUtil.decode(newBookingDetailModel.getRoutePolyline());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (list != null)
                        drawRoute(list);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void drawRoute(List<LatLng> pointList) {

        PolylineOptions options = new PolylineOptions().width(10.0f).color(Color.parseColor("#1C3E61")).geodesic(true);
        int loopEnd = pointList.size() - 1;
        for (int i = 0; i<= loopEnd; i++ ) {
            LatLng point = pointList.get(i);
            options.add(point);
        }

        gMapBookingDetail.addPolyline(options);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
        finish();
    }

    // ******* Back from booking detail view ***********
    private void backFromBookingDetailView() {
        //LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnHeader:
                finish();
                break;
            case R.id.view_offer:
                ViewBookingOffer();
                break;
                case R.id.backFromBookingDetail:
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                finish();
                break;
            case R.id.bookingDetailChatIcon:
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                Intent chatViewItent = new Intent(BookingDetail_New.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.rejectBtnBD:
                rejectNewBooking ();
                break;
            case R.id.acceptBtnBD:
                acceptNewBooking();
                break;
            case R.id.suggestPriceTxt:
                findViewById(R.id.button).setVisibility(View.GONE);
                ExpandCollapseAnimation.expand(priceSubmitView);
                ExpandCollapseAnimation.collapse(txtWithSuggestPriceBtnLayout);
                edtSuggestPriceTxt.requestFocus();
              //  LoginZoomToU.imm.showSoftInput(edtSuggestPriceTxt, InputMethodManager.SHOW_IMPLICIT);
                scrollViewNewBD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewNewBD.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 500);
                break;
            case R.id.cancelSuggestPriceBtnBD:
                findViewById(R.id.button).setVisibility(View.VISIBLE);
                edtSuggestPriceTxt.setText("");
                courierSuggestedPrice = "";
                ExpandCollapseAnimation.expand(txtWithSuggestPriceBtnLayout);
                ExpandCollapseAnimation.collapse(priceSubmitView);
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                break;
            case R.id.submitSuggestPriceBtnBD:
                courierSuggestedPrice = edtSuggestPriceTxt.getText().toString();
                if (!courierSuggestedPrice.equals("")) {
                    LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                    submitCouriersSuggestPrice();
                } else
                    Toast.makeText(BookingDetail_New.this, "Please enter your price", Toast.LENGTH_LONG).show();
                break;
            case R.id.priceTxtForGSTInfo:
                dialogViewForGSTInfo();
                break;
        }
    }

    private void ViewBookingOffer(){
        final String[] responseForAcceptBook = {"0"};
        final JSONObject[] jObjAcceptResponse = new JSONObject[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                inItProgressInNewDetailView();
            }

            @Override
            public void doInBackground() {
                try {

                   WebserviceHandler webServiceHandler = new WebserviceHandler();
                   responseForAcceptBook[0] =webServiceHandler.getViewOffer(newBookingDetailModel.getBookingId());
                   jObjAcceptResponse[0] = new JSONObject(responseForAcceptBook[0]);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (!responseForAcceptBook[0].equals("0")) {
                        if (jObjAcceptResponse[0].getBoolean("success")) {
                           if(jObjAcceptResponse[0].getString("counterOffers")!=null){
                               Type listType = new TypeToken<List<CounterOffers>>() {}.getType();
                               List<CounterOffers> data = new Gson().fromJson(jObjAcceptResponse[0].getString("counterOffers"), listType);
                               if(data.size()>0)
                               ShowOfferForBookings.alertDialogToFinishView(BookingDetail_New.this,data);
                               else
                                   Toast.makeText(BookingDetail_New.this,"No offers available",Toast.LENGTH_LONG).show();
                           }


                        } else {
                            DialogActivity.alertDialogView(BookingDetail_New.this, "Information!", jObjAcceptResponse[0].getString("message"));
                        }
                    } else
                        DialogActivity.alertDialogView(BookingDetail_New.this, "Error!", "Something went wrong, Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(BookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                } finally {
                    dismissNewDetailProgressDialog();

                }
            }
        }.execute();
    }



    private void acceptNewBooking() {
        if (isshow){
            try{
                String listPosition = String.valueOf(getIntent().getBundleExtra("NewBookingBundle").getInt("listPosition"));
                int offerId =getIntent().getExtras().getInt("offerId");
                int bookingId =getIntent().getExtras().getInt("bookingId");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Accept");
                returnIntent.putExtra("listPosition", listPosition);
                returnIntent.putExtra("offerId",offerId);
                returnIntent.putExtra("bookingId",bookingId);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }catch(Exception e) {
                e.printStackTrace();
            }

        }else {
          if (WebserviceHandler.isRoutific)
                callServiceForAcceptBooking();
            else {
                bookingETADialogBD.setVisibility(View.VISIBLE);

                String etaForCurrentTime = null;
                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss.SSS");
                etaForCurrentTime = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + newBookingDetailModel.getPickupDateTime());
                etaForCurrentTime = etaForCurrentTime + "T" + sdf1.format(c.getTime());
                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");

                Date convertedDate = new Date();
                try {
                    convertedDate = sdf.parse(etaForCurrentTime);
                    sdf = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (uploadtimeToServer != null)
                    uploadtimeToServer = null;
                // Date uploaded to server
                uploadtimeToServer = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
                convertedDate = null;
                etaForCurrentTime = LoginZoomToU.checkInternetwithfunctionality.getPickOrDropDateTimeFromServer(etaForCurrentTime);
                etaMsgTextBD.setText(etaForCurrentTime);
                etaForCurrentTime = null;
            }
        }
    }

    private void rejectNewBooking () {
        if(isshow) {
            try {
                String listPosition = String.valueOf(getIntent().getBundleExtra("NewBookingBundle").getInt("listPosition"));
                int offerId =getIntent().getExtras().getInt("offerId");
                int bookingId =getIntent().getExtras().getInt("bookingId");
                Intent returnIntent = new Intent();
                returnIntent.putExtra("result", "Reject");
                returnIntent.putExtra("listPosition", listPosition);
                returnIntent.putExtra("offerId",offerId);
                returnIntent.putExtra("bookingId",bookingId);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }else {
            isRejectBooking_NBD = true;
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                //new AcceptBookingForPickupInBDAsyncTask().execute();
                AcceptBookingForPickupInBDAsyncTask();
            else
                DialogActivity.alertDialogView(BookingDetail_New.this, "No Network!", "No network connection, Please try again later.");

        }
      }

    //********** Dialog for GST info ***************
    private void dialogViewForGSTInfo(){

        try{
            if(dialogInNOotificationUI != null)
                if (dialogInNOotificationUI.isShowing())
                    dialogInNOotificationUI.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(dialogInNOotificationUI != null)
            dialogInNOotificationUI = null;
        dialogInNOotificationUI = new Dialog(BookingDetail_New.this);
        dialogInNOotificationUI.setCancelable(true);
        dialogInNOotificationUI.getWindow().setBackgroundDrawableResource(R.color.transparent_black_bg);
        dialogInNOotificationUI.setContentView(R.layout.offerscreen_charge_n_fees);

        Window window = dialogInNOotificationUI.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);



        Button rejectBtn_GST_InfoDialog = (Button) dialogInNOotificationUI.findViewById(R.id.rejectBtn_GST_InfoDialog);
        rejectBtn_GST_InfoDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInNOotificationUI.dismiss();
            }
        });

        dialogInNOotificationUI.show();
    }
    /*
   **********  Call service for accept booking ************
    */
    private void submitCouriersSuggestPrice() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
           // new SubmitCouriersSuggestPrice().execute();
            SubmitCouriersSuggestPrice();
        else
            DialogActivity.alertDialogView(BookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
    }

    /*
    **********  Call service for accept booking ************
     */
    private void callServiceForAcceptBooking() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new AcceptBookingForPickupInBDAsyncTask().execute();
            AcceptBookingForPickupInBDAsyncTask();
        else
            DialogActivity.alertDialogView(BookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
    }

    private void AcceptBookingForPickupInBDAsyncTask(){
        final String[] responseForAcceptBook = {"0"};
        final JSONObject[] jObjAcceptResponse = new JSONObject[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                inItProgressInNewDetailView();
                ActiveBookingView.getCurrentLocation(BookingDetail_New.this);
            }

            @Override
            public void doInBackground() {
                try {
                    //    Functional_Utility.sendLocationToServer();
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (isRejectBooking_NBD == false) {
                        if (WebserviceHandler.isRoutific == true)
                            responseForAcceptBook[0] = webServiceHandler.acceptBookingForPickup(newBookingDetailModel.getBookingId(), "");
                        else
                            responseForAcceptBook[0] = webServiceHandler.acceptBookingForPickup(newBookingDetailModel.getBookingId(),
                                    uploadtimeToServer);
                    } else
                        responseForAcceptBook[0] = webServiceHandler.rejectBookingForPickup(newBookingDetailModel.getBookingId());

                    jObjAcceptResponse[0] = new JSONObject(responseForAcceptBook[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    responseForAcceptBook[0] = "0";
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (!responseForAcceptBook[0].equals("0")) {
                        if (jObjAcceptResponse[0].getBoolean("success") == true) {
                            if (isRejectBooking_NBD == false) {
                                callDeliveryCount(3);
                                new LoadChatBookingArray(BookingDetail_New.this, 0);
                                redirectViewToActiveList();
                                Toast.makeText(BookingDetail_New.this, newBookingDetailModel.getPick_Suburb() + " to " + newBookingDetailModel.getDrop_Suburb() + " accepted", Toast.LENGTH_LONG).show();
                                try {
                                    if (jObjAcceptResponse[0].getJSONObject("notification") != null) {
                                        Intent customNotificationIntent = new Intent(BookingDetail_New.this, CustomNotification_View.class);
                                        customNotificationIntent.putExtra("CustomNotifiedData", jObjAcceptResponse[0].getJSONObject("notification").toString());
                                        customNotificationIntent.putExtra("BookingID", jObjAcceptResponse[0].getInt("bookingId"));
                                        startActivity(customNotificationIntent);
                                        customNotificationIntent = null;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                callDeliveryCount(4);
                                isRejectBooking_NBD = false;
                                CompletedView.endlessCount = 0;
                                Toast.makeText(BookingDetail_New.this, "Booking rejected", Toast.LENGTH_LONG).show();
                                backFromBookingDetailView();
                            }
                        } else {
                            DialogActivity.alertDialogView(BookingDetail_New.this, "Information!", jObjAcceptResponse[0].getString("message"));
                        }
                    } else
                        DialogActivity.alertDialogView(BookingDetail_New.this, "Error!", "Something went wrong, Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(BookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                } finally {
                    dismissNewDetailProgressDialog();

                }
            }
        }.execute();
    }




    private void SubmitCouriersSuggestPrice(){
        final String[] responseSubmitCourierSuggestPrice = {"0"};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                inItProgressInNewDetailView();
                ActiveBookingView.getCurrentLocation(BookingDetail_New.this);
            }

            @Override
            public void doInBackground() {
                try {
                    //    Functional_Utility.sendLocationToServer();
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseSubmitCourierSuggestPrice[0] = webServiceHandler.submitCouriersSuggestPrice(newBookingDetailModel.getBookingId(), courierSuggestedPrice,bidActivePeriodInterval);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (LoginZoomToU.isLoginSuccess == 0) {
                        edtSuggestPriceTxt.setText("");
                        courierSuggestedPrice = "";
                        Toast.makeText(BookingDetail_New.this, "Your suggested price has been sent to the customer.", Toast.LENGTH_LONG).show();
                        backFromBookingDetailView();
                    } else if (LoginZoomToU.isLoginSuccess == 1) {
                        DialogActivity.alertDialogView(BookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
                    } else if (LoginZoomToU.isLoginSuccess == 2) {
                        LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                        validationErrorMsg(responseSubmitCourierSuggestPrice[0]);
                    } else
                        DialogActivity.alertDialogView(BookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(BookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                } finally {
                    dismissNewDetailProgressDialog();
                }
            }
        }.execute();
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_BD:
                return new TimePickerDialog(this, mTimeSetListener, pHour, pMinute, false);
        }
        return null;
    }

    /**
     * Callback received when the user "picks" a time in the dialog
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SimpleDateFormat")
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    pHour = hourOfDay;
                    pMinute = minute;

                    String am_pm = "";

                    Calendar datetime = Calendar.getInstance();
                    datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    datetime.set(Calendar.MINUTE, minute);

                    if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                        am_pm = "AM";
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "PM";

                    String strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                    strHrsToShow = strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;

                    selectPickUpEtaBD.setImageResource(R.drawable.icon_down);

                    String dateStr = LoginZoomToU.checkInternetwithfunctionality.returnDateFromServer("" + newBookingDetailModel.getPickupDateTime());
                    dateStr = dateStr + " " + strHrsToShow;

                    Date convertedDate = LoginZoomToU.checkInternetwithfunctionality.returnDateTimeForConversion(dateStr);
                    if (uploadtimeToServer != null)
                        uploadtimeToServer = null;
                    uploadtimeToServer = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
                    String dateValueIs = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(uploadtimeToServer);

                    etaMsgTextBD.setText(dateValueIs);
                    datetime = null;
                    convertedDate = null;
                }
            };

    /*
    **********  Service call for delivery count on accept and reject booking ***************
     */
    private void callDeliveryCount(int addDeductFromNewBookingCount) {
        if (ConfirmPickUpForUserName.isDropOffSuccessfull == 11) {
            if (BookingView.bookingViewSelection == 1 || BookingView.bookingViewSelection == 2 || BookingView.bookingViewSelection == 3) {
                Intent bookingCountService = new Intent(BookingDetail_New.this, ServiceForCourierBookingCount.class);
                bookingCountService.putExtra("Is_API_Call_Require", addDeductFromNewBookingCount);
                startService(bookingCountService);
                bookingCountService = null;
            }
        }
    }

    /*
    ***************  For New booking notification ****************
     */
//    private void refreshNewBookingJob() {
//        if (ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 1) {
//            Intent intent1 = new Intent(BookingDetail_New.this, ServiceToUpdateNewBookingList.class);
//            startService(intent1);
//            intent1 = null;
//        }
//    }

    private void dismissNewDetailProgressDialog() {
        try {
            if (progressDialogForNBD != null)
                if (progressDialogForNBD.isShowing())
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogForNBD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItProgressInNewDetailView() {
        if (progressDialogForNBD != null)
            progressDialogForNBD = null;
        progressDialogForNBD = new ProgressDialog(BookingDetail_New.this);
        Custom_ProgressDialogBar.inItProgressBar(progressDialogForNBD);
    }

    private void dialogViewForNewNotification(String titleStr, String msgStr) {
        if (dialogInNOotificationUI != null)
            dialogInNOotificationUI = null;
        dialogInNOotificationUI = new Dialog(BookingDetail_New.this);
        dialogInNOotificationUI.setCancelable(true);
        dialogInNOotificationUI.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#29000000")));
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
            }
        });
        dialogInNOotificationUI.show();
    }

    private void redirectViewToActiveList() {
        BookingView.bookingViewSelection = 2;
        ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
        Intent callCompleteBookingfragment = new Intent(BookingDetail_New.this, SlideMenuZoom2u.class);
        callCompleteBookingfragment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(callCompleteBookingfragment);
        callCompleteBookingfragment = null;
        finish();
    }

    //*************** Error message from server ************
    public void validationErrorMsg(String apiResponseObj) {
        try {
            JSONObject jObjOfServerResponse = new JSONObject(apiResponseObj);
            if (apiResponseObj.length() > 0) {
                String titleMsgStr = "";
                String msgStr = "";
                if (jObjOfServerResponse.has("errors")) {
                    try {
                        JSONArray jArrayOfErrorObj = jObjOfServerResponse.getJSONArray("errors");
                        titleMsgStr = jObjOfServerResponse.getString("message");
                        for (int i = 0; i < jArrayOfErrorObj.length(); i++) {
                            JSONObject errorObj = jArrayOfErrorObj.getJSONObject(i);
                            if (msgStr.equals(""))
                                msgStr = errorObj.getString("message");
                            else
                                msgStr = msgStr + "\n" + errorObj.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        titleMsgStr = "Error!";
                        msgStr = "Something went wrong, Please try again.";
                    }
                } else {
                    if (jObjOfServerResponse.has("message")) {
                        titleMsgStr = "Error!";
                        try {
                            msgStr = jObjOfServerResponse.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            titleMsgStr = "Error!";
                            msgStr = "Something went wrong, Please try again.";
                        }
                    } else {
                        titleMsgStr = "Error!";
                        msgStr = "Something went wrong, Please try again.";
                    }
                }
                dialogViewForNewNotification(titleMsgStr, msgStr);
            } else
                dialogViewForNewNotification("Error!", "Something went wrong, Please try again.");
        } catch (Exception e) {
            e.printStackTrace();
            dialogViewForNewNotification("Error!", "Something went wrong, Please try again.");
        }
    }


}