package com.zoom2u;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

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
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.suggestprice_team.AssignToOtherCourier_Functionality;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.chat.Chat;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.alcohol_delivery.dropdelivery_alert.DropAlcoholDelivery_Alerts;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogReasonForLateDelivery;
import com.zoom2u.dimension.Dimension_class;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.barcode_reader.BarcodeController;
import com.zoom2u.slidemenu.barcode_reader.CameraOverlay_Activity;
import com.zoom2u.slidemenu.offerrequesthandlr.Bid_RequestView_Detail;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class ActiveBookingDetail_New extends Activity implements View.OnClickListener, Animation.AnimationListener {
    int PERMISSION_ID = 22;
    FusedLocationProviderClient mFusedLocationClient;
    /*   ***** Courier detail view contents *****  */
    RelativeLayout.LayoutParams paramForCollapseExpandImgParam;

    RelativeLayout customerInfoHiddenLayout, customerDetailLayoutABD;
    TextView bookingRefNoTxtABD, createdTimeTxtCustomerDetailABD, customerNameTxtCoureirDetailABD, customerCompanyAndMobNoTxtCouDetalABD,
            deliveryNotesTxtcourierDetailABD;
    ImageView arrowUpDownImgCustmerLayout, iconCallCustomerBtnInDeailABD;

    /*   ***** ATL detail view contents *****  */
    RelativeLayout atlInfoHiddenLayout, atlDetailLayoutABD;

    AssignToOtherCourier_Functionality assignToOtherCourier_functionality;
    TextView txtHeaderATLBtnInATLDetail, atlInfoWhereTxtDetailABD, doorCodeTxtATLDetailABD, customerTxtATLDetailABD,
            notesTxtATLDetailABD, tv_eta_msg;
    ImageView arrowUpDownImgATLImg, img_info_eta;

    private int courierDetailViewExpanded = 0;

    TextView countChatABookingDetail, secondHeaderTxtABD, pickHeaderLayoutPickTimeTxtABD, pickDropNotesABD, pickDropDate, pickDropReadMoreTxtABD;

    Button pickDropBtnABD, triedToDeliverBtnABD, returnToDHLBtnABD, openBarcodeBtnABD;

    RelativeLayout documentLayoutABD;
    TextView documentTxtABD, item_type, dimentionDetailsTxtABD, viewDimensionTxtABD;

    private ScrollView activeDetailScrollView;
    private boolean isReadMoreTxt = false;
    //*********** ETA dialog view contents ***********
    RelativeLayout activeBookABD, etaUpdateLayout;
    TextView etaTitleText, etaMsgText,item_type1;
    ImageView selectEtaImage;

    ProgressDialog progressInABDView;
    Dialog alertDialog;

    String positionForTriedToDeliverReason = "";
    int returnToPickupReasonFromDropDown;
    public int isReturnedToDHL = 0;

    boolean isPickBtnPress = false;

    public static boolean isPickBtn = false;
    public static final int TAKE_PHOTO = 1;
    private int itemPositionInActiveDetail;
    public static String bookingIdActiveBooking;
    /**
     * This integer will uniquely define the dialog to be used for displaying time picker.
     */
    public static final int TIME_DIALOG_ID = 0;

    String dateStr, dateTimeStr, strHrsToShow, dateValueIs, uploadDateStr;
    private int pHour, pMinute;

    Dialog attemptDeliveryDialog;
    String attemptDeliveryStr = "";

    String pickOrDropPersonContactNoToCall, pickOrDropPersonAddressForMap;
    public int routeViewCallingIntent;
    int bookingIdToLoadFromRouteView;       // Route view params

    private boolean isDropOffFromATL = false;
    ProgressDialog progressInActiveDetailView;

    private All_Bookings_DataModels activeBookingModelForSelectedItem;

    //***************** Returned to pickup **********
    int isForReturnToPickup = 0;        //********** This field value is 1 when attempt for Returned to pickup otherwise 0
    Button returnToPickUpBtnABD;    //********** Added button for Returned to pickup
    //***************** Returned to pickup **********

    public static HashMap<String, Boolean> scannedPieceMap;
    Window window;
    public static int IS_BARCODE_FROM_ACTIVE_DETAIL = 1227;
    private boolean isPickShowed = true;
    RelativeLayout headerLayoutAllTitleBar;

    @Override
    protected void onResume() {
        super.onResume();

        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatABookingDetail);
        SlideMenuZoom2u.countChatBookingView = countChatABookingDetail;
        try {
            if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 2) {
                BarcodeScanner.isScannedSuccessFully = false;
                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
                    attemptDeliveryWindow(0, 1);
                else
                    attemptDeliveryWindow(0, 1);
            } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 3) {
                BarcodeScanner.isScannedSuccessFully = false;
                isReturnedToDHL = 1;
                takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
            } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 4) {
                BarcodeScanner.isScannedSuccessFully = false;
                isReturnedToDHL = 0;
                takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
            } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 5) {
                BarcodeScanner.isScannedSuccessFully = false;
                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                atlDialogAlert();
            } else if (BarcodeScanner.ScanAWBForPick == 6 && ActiveBookingView.photo != null) {
                BarcodeScanner.ScanAWBForPick = 1;
                pickAndDropBookingAfterTakePkgPhoto();   //************* Pick and Drop booking after take photo
            } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 7) {
                BarcodeScanner.isScannedSuccessFully = false;
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                   // new PackageUploadWithETA_DetailViewAsyncTask().execute("SMARTSORT");
                    PackageUploadWithETA_DetailViewAsyncTask("SMARTSORT");
                else
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************* Pick and Drop booking after take photo ************
    private void pickAndDropBookingAfterTakePkgPhoto() {
        if (isDropOffFromATL) {
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                if (activeBookingModelForSelectedItem.getSource().equals("DHL"))
                    //new DropOffFromATLAsyncTask().execute();
                    DropOffFromATLAsyncTask();
                else
                    //new RequestToDropOffTemendo_AND_MenulogAsyncTask().execute();
                    RequestToDropOffTemendo_AND_MenulogAsyncTask();
            } else
                DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
        } else if (activeBookingModelForSelectedItem.getSource().equals("DHL")
                && ActiveBookingView.photo != null) {
            callConfirmPIckDropBookingView();
        } else if (!activeBookingModelForSelectedItem.getSource().equals("DHL"))
            callConfirmPIckDropBookingView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.new_active_detail_view);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        window = ActiveBookingDetail_New.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        headerLayoutAllTitleBar = findViewById(R.id.headerLayoutAllTitleBar);

        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }

        try {
            //************ Ask for location permission ***************//
            BookingDetail_New.askForLocationPermission(ActiveBookingDetail_New.this);

            if (savedInstanceState == null) {
                if (getIntent().getIntExtra("RouteViewCalling", 0) == 181) {
                    routeViewCallingIntent = getIntent().getIntExtra("RouteViewCalling", 0);
                    bookingIdToLoadFromRouteView = getIntent().getIntExtra("bookingIDToLoad", 0);

                    bookingIdActiveBooking = String.valueOf(bookingIdToLoadFromRouteView);
                    findViewById(R.id.titleBookingDetail);
                    ((TextView) findViewById(R.id.titleBookingDetail)).setText("Active Booking Detail");
                    activeDetailByBookingIdFromRouteView();
                }
                else {
                    Intent i = getIntent();
                    if (i.getBooleanExtra("fromOfferRun", false)) {
                        activeBookingModelForSelectedItem = i.getParcelableExtra("model");
                        bookingIdActiveBooking = activeBookingModelForSelectedItem.getBookingId() + "";
                        i = null;
                        ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
                        inItABDUIContents();
                    }
                    else {
                        try {
                            itemPositionInActiveDetail = i.getIntExtra("positionActiveBooking", 0);
                            activeBookingModelForSelectedItem = (All_Bookings_DataModels) BookingView.bookingListArray.get(itemPositionInActiveDetail);
                            bookingIdActiveBooking = String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId());
                            i = null;
                            ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        inItABDUIContents();  // Initialize active booking detail view
                    }
                }
            }
            else {
                try {
                    if (savedInstanceState != null)
                        reStoreActivityItems(savedInstanceState);
                    if (LoginZoomToU.NOVA_BOLD == null)
                        LoginZoomToU.staticFieldInit(ActiveBookingDetail_New.this);
                    if (routeViewCallingIntent == 181 && bookingIdToLoadFromRouteView != 0)
                        activeDetailByBookingIdFromRouteView();
                    else
                        inItABDUIContents();                                // Initialize active booking detail view
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setBGColorOrTxtOfBottomButtonInABD(Button bottomBtnId, int bgColor, String buttonTxt, int txtColor) {
        bottomBtnId.setBackgroundResource(bgColor);
        bottomBtnId.setText(buttonTxt);
        bottomBtnId.setTextColor(txtColor);
    }

    private void inItABDUIContents() {
        ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
        setBookingTimeToField((TextView) findViewById(R.id.pickupEnd), activeBookingModelForSelectedItem.getPickupDateTime(), "", false, false);
        setBookingTimeToField( (TextView) findViewById(R.id.dropEnd), activeBookingModelForSelectedItem.getDropDateTime(), "", false, false);
        activeDetailScrollView = (ScrollView) findViewById(R.id.activeDetailScrollView);
        //********** In-it Title bar contents ***********//
        findViewById(R.id.whatsApp).setVisibility(View.VISIBLE);
        findViewById(R.id.whatsApp).setOnClickListener(this);
        findViewById(R.id.titleBookingDetail);

        ((TextView) findViewById(R.id.titleBookingDetail)).setText("#" + activeBookingModelForSelectedItem.getBookingRefNo());

        findViewById(R.id.backFromBookingDetail);
        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);
        findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);
        countChatABookingDetail = (TextView) findViewById(R.id.countChatBookingDetail);
        countChatABookingDetail.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatABookingDetail;

        pickDropBtnABD = (Button) findViewById(R.id.pickDropBtnABD);

        pickDropBtnABD.setOnClickListener(this);
        triedToDeliverBtnABD = (Button) findViewById(R.id.triedToDeliverBtnABD);

        triedToDeliverBtnABD.setOnClickListener(this);
        returnToDHLBtnABD = (Button) findViewById(R.id.returnToDHLBtnABD);

        item_type1=findViewById(R.id.item_type1);

        returnToDHLBtnABD.setOnClickListener(this);
        returnToPickUpBtnABD = (Button) findViewById(R.id.returnToPickUpBtnABD);

        returnToPickUpBtnABD.setOnClickListener(this);
        openBarcodeBtnABD = (Button) findViewById(R.id.openBarcodeBtnABD);

        openBarcodeBtnABD.setOnClickListener(this);

        //******* Help Button to show admin chat
        findViewById(R.id.helpBtnDetailPage).setOnClickListener(this);

        try {
            if (getIntent().getBooleanExtra("IsReturnToDHL", false) == true) {
                returnToDHLBtnABD.setVisibility(View.VISIBLE);
                isReturnedToDHL = 2;
            }
            if (activeBookingModelForSelectedItem.getSource().equals("DHL")
                    && activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                returnToDHLBtnABD.setVisibility(View.VISIBLE);
                isReturnedToDHL = 2;
            } else {
                if ((activeBookingModelForSelectedItem.getStatus().equals("Accepted") && LoginZoomToU.IS_TEAM_LEADER == true)||(activeBookingModelForSelectedItem.getStatus().equals("Accepted") && !LoginZoomToU.IS_TEAM_LEADER)){

                    if (!LoginZoomToU.IS_TEAM_LEADER){
                        returnToDHLBtnABD.setVisibility(View.VISIBLE);
                        setBGColorOrTxtOfBottomButtonInABD(returnToDHLBtnABD, R.drawable.rounded_worrier_level, "Assign to other team member", Color.WHITE);
                    }else {
                        if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")) {
                            returnToDHLBtnABD.setVisibility(View.GONE);
                        } else {
                            returnToDHLBtnABD.setVisibility(View.VISIBLE);
                            setBGColorOrTxtOfBottomButtonInABD(returnToDHLBtnABD, R.drawable.rounded_worrier_level, "Assign to other team member", Color.WHITE);
                        }
                    }
                } else
                    returnToDHLBtnABD.setVisibility(View.GONE);


                isReturnedToDHL = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            returnToDHLBtnABD.setVisibility(View.GONE);
        }

        if (activeBookingModelForSelectedItem.getSource().equals("Temando")) {
            if (activeBookingModelForSelectedItem.getStatus().equals("Accepted")) {
                triedToDeliverBtnABD.setVisibility(View.GONE);
                setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_worrier_level, "Mark as picked up", Color.WHITE);
                isPickBtn = false;
            } else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up")) {
                triedToDeliverBtnABD.setVisibility(View.GONE);
                setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_worrier_level, "Mark as dropped off", Color.WHITE);
                isPickBtn = true;
            }
        } else {
            if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup") || activeBookingModelForSelectedItem.getStatus().equals("Accepted")) {
                String firstCharOfReferenceNo = activeBookingModelForSelectedItem.getBookingRefNo().substring(0, 1);
                if (firstCharOfReferenceNo.equalsIgnoreCase("T")
                        || activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")
                        || activeBookingModelForSelectedItem.getPackage().equals("XL")
                        || activeBookingModelForSelectedItem.getRunId() != 0) {
                    triedToDeliverBtnABD.setVisibility(View.GONE);
                } else {
                    triedToDeliverBtnABD.setVisibility(View.VISIBLE);
                    setBGColorOrTxtOfBottomButtonInABD(triedToDeliverBtnABD, R.drawable.rounded_elite_level, "Request new driver", Color.WHITE);
                }

                if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                    pickDropBtnABD.setTextSize(14f);
                    pickDropBtnABD.setClickable(false);
                    pickDropBtnABD.setEnabled(false);
                    setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.color.white, "Please use the barcode scanner in the menu to pick up items", Color.parseColor("#45515b"));
                } else if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")) {
                    openBarcodeBtnABD.setVisibility(View.VISIBLE);
                    pickDropBtnABD.setTextSize(14f);
                    pickDropBtnABD.setClickable(true);
                    pickDropBtnABD.setEnabled(true);
                    setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_worrier_level, "Mark as picked up", Color.WHITE);
                } else {
                    pickDropBtnABD.setTextSize(14f);
                    pickDropBtnABD.setClickable(true);
                    pickDropBtnABD.setEnabled(true);
                    if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                        setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_worrier_level, "Mark as picked up", Color.WHITE);
                    else
                        setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_dynamo_level, "On route to pick up", Color.WHITE);
                }
                isPickBtn = false;
                returnToPickUpBtnABD.setVisibility(View.GONE);
            } else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up")) {
                returnToPickUpBtnABD.setVisibility(View.VISIBLE);
                triedToDeliverBtnABD.setVisibility(View.GONE);
                setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_dynamo_level, "On route to drop off", Color.WHITE);
                isPickBtn = false;
            } else if (activeBookingModelForSelectedItem.getStatus().equals("Returning")
                    || activeBookingModelForSelectedItem.isTTDReasonForAlcoholDelivery()) {
                triedToDeliverBtnABD.setVisibility(View.GONE);
                pickDropBtnABD.setVisibility(View.GONE);
                returnToPickUpBtnABD.setVisibility(View.VISIBLE);
            } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                    || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT"))
                    openBarcodeBtnABD.setVisibility(View.VISIBLE);
                returnToPickUpBtnABD.setVisibility(View.VISIBLE);
                triedToDeliverBtnABD.setVisibility(View.VISIBLE);
                setBGColorOrTxtOfBottomButtonInABD(triedToDeliverBtnABD, R.drawable.roundedskybluebg, "Tried to deliver", Color.WHITE);
                setBGColorOrTxtOfBottomButtonInABD(pickDropBtnABD, R.drawable.rounded_worrier_level, "Mark as dropped off", Color.WHITE);
                isPickBtn = true;
            } else {
                triedToDeliverBtnABD.setVisibility(View.GONE);
                pickDropBtnABD.setVisibility(View.GONE);
                returnToPickUpBtnABD.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.secondHeaderABDLayout).setOnClickListener(this);
        img_info_eta = findViewById(R.id.img_info_eta);
        tv_eta_msg = findViewById(R.id.tv_eta_msg);
        tv_eta_msg.setVisibility(View.GONE);
        tv_eta_msg.setOnClickListener(v -> tv_eta_msg.setVisibility(View.GONE));
        img_info_eta.setOnClickListener(v -> {
            if (tv_eta_msg.getVisibility() == View.VISIBLE)
                tv_eta_msg.setVisibility(View.GONE);
            else tv_eta_msg.setVisibility(View.VISIBLE);
        });
        secondHeaderTxtABD = (TextView) findViewById(R.id.secondHeaderTxtABD);

        TextView missingPieceTxtABD = (TextView) findViewById(R.id.missingPieceTxtABD);


        findViewById(R.id.newCustomerTxtInABD);
        showHideNewCustomerText();

        //************* Document layout over map view *************
        documentLayoutABD = (RelativeLayout) findViewById(R.id.documentLayoutABD);
        dimentionDetailsTxtABD = (TextView) findViewById(R.id.dimentionDetailsTxtABD);
        viewDimensionTxtABD = (TextView) findViewById(R.id.viewDimensionTxtABD);
        ((TextView) findViewById(R.id.priceTxtABD1)).setText("$" + Functional_Utility.returnCourierPrice((Double) activeBookingModelForSelectedItem.getPrice()));
        TextView weight = findViewById(R.id.weight);
        ((TextView) findViewById(R.id.vehicleTxtABD)).setText(activeBookingModelForSelectedItem.getVehicle());

        try {
            double totalWeight = 0.0;
            if (activeBookingModelForSelectedItem.getShipmentsArray().size() > 0) {
                for (int i = 0; i < activeBookingModelForSelectedItem.getShipmentsArray().size(); i++) {
                    totalWeight = totalWeight + (double) activeBookingModelForSelectedItem.getShipmentsArray().get(i).get("TotalWeightKg");

                }
                if (totalWeight > 0.0) {
                    weight.setVisibility(View.VISIBLE);
                    weight.setText("Total weight : " + totalWeight + "kg");
                } else
                    weight.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        item_type = findViewById(R.id.item_type);
        item_type.setText("");
        try {
            if (activeBookingModelForSelectedItem.getShipmentsArray().size() > 0) {
                for (int i = 0; i < activeBookingModelForSelectedItem.getShipmentsArray().size(); i++) {
                    if (i < 3) {
                        if (i == 0)
                            item_type.append(activeBookingModelForSelectedItem.getShipmentsArray().get(i).get("Category") + " (" + activeBookingModelForSelectedItem.getShipmentsArray().get(i).get("Quantity") + ")");
                        else
                            item_type.append(", " + activeBookingModelForSelectedItem.getShipmentsArray().get(i).get("Category") + " (" + activeBookingModelForSelectedItem.getShipmentsArray().get(i).get("Quantity") + ")");
                    } else {
                        item_type.append("...");
                        break;
                    }
                }
            } else
                item_type.append("" + activeBookingModelForSelectedItem.getPackage());
        } catch (Exception e) {
            e.printStackTrace();
            item_type.append("" + activeBookingModelForSelectedItem.getPackage());
        }


        documentTxtABD = (TextView) findViewById(R.id.documentTxtABD);

        documentTxtABD.setText("");
        dimentionDetailsTxtABD.setText("");
        if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
            if (activeBookingModelForSelectedItem.getStatus().equals("Accepted") || activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")) {
                missingPieceTxtABD.setVisibility(View.GONE);
                secondHeaderTxtABD.setVisibility(View.VISIBLE);
                img_info_eta.setVisibility(View.VISIBLE);
                documentTxtABD.setText("Pieces: " + activeBookingModelForSelectedItem.getTotalPieceCount());
                item_type1.setText("Pieces: " + activeBookingModelForSelectedItem.getTotalPieceCount());
               // dimentionDetailsTxtABD.setText("Custom");
            } else {
                int pickedUpPieceCount = activeBookingModelForSelectedItem.getPickedUpPieceCount();
                int totalPieceCount = activeBookingModelForSelectedItem.getTotalPieceCount();
                if (pickedUpPieceCount < totalPieceCount) {
                    missingPieceTxtABD.setVisibility(View.VISIBLE);
                    secondHeaderTxtABD.setVisibility(View.GONE);
                    img_info_eta.setVisibility(View.GONE);
                } else {
                    missingPieceTxtABD.setVisibility(View.GONE);
                    secondHeaderTxtABD.setVisibility(View.VISIBLE);
                    img_info_eta.setVisibility(View.VISIBLE);
                }
                documentTxtABD.setText("Pieces: " + pickedUpPieceCount + "/" + totalPieceCount);
                item_type1.setText("Pieces: " + pickedUpPieceCount + "/" + totalPieceCount);

            }

        } else {
            boolean hasLengthWidthAvailable = false;
            missingPieceTxtABD.setVisibility(View.GONE);
            secondHeaderTxtABD.setVisibility(View.VISIBLE);
            img_info_eta.setVisibility(View.VISIBLE);
            try {
                ArrayList<HashMap<String, Object>> shipmentArray = activeBookingModelForSelectedItem.getShipmentsArray();
                if (shipmentArray.size() > 0) {
                    for (int i = 0; i < shipmentArray.size(); i++) {
                        String category = "" + shipmentArray.get(i).get("Category");
                        String quantity = "" + shipmentArray.get(i).get("Quantity");
                        int lengthCm = (Integer) shipmentArray.get(i).get("LengthCm");
                        int widthCm = (Integer) shipmentArray.get(i).get("WidthCm");
                        int heightCm = (Integer) shipmentArray.get(i).get("HeightCm");

                        if (!hasLengthWidthAvailable && (lengthCm > 0 || widthCm > 0 || heightCm > 0))
                            hasLengthWidthAvailable = true;

                        if (i == 0) {
                            documentTxtABD.append(category + " (" + quantity + ")");
                           item_type1.append(category + " (" + quantity + ")");

                           //dimentionDetailsTxtABD.append(category + ":- " + "Length(cm) " + lengthCm + ", Width(cm) " + widthCm + ", Height(cm) " + heightCm + ", Item weight(kg) " + shipmentArray.get(i).get("ItemWeightKg") + ", Total weight(kg) " + shipmentArray.get(i).get("TotalWeightKg"));
                        } else {
                            documentTxtABD.append(", " + category + " (" + quantity + ")");
                           item_type1.append(", " + category + " (" + quantity + ")");

                           // dimentionDetailsTxtABD.append("\n\n" + category + ":- " + "Length(cm) " + lengthCm + ", Width(cm) " + widthCm + ", Height(cm) " + heightCm + ", Item weight(kg) " + shipmentArray.get(i).get("ItemWeightKg") + ", Total weight(kg) " + shipmentArray.get(i).get("TotalWeightKg"));
                        }
                    }
                } else
                    documentTxtABD.append("" + activeBookingModelForSelectedItem.getPackage());
                    item_type1.append("" + activeBookingModelForSelectedItem.getPackage());

            } catch (Exception e) {
                e.printStackTrace();
                documentTxtABD.append("" + activeBookingModelForSelectedItem.getPackage());
                item_type1.append("" + activeBookingModelForSelectedItem.getPackage());

            }


        }

        documentLayoutABD.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showDimensionPopup();

            }
        });



        inItCustomerInfoDetailView();        /*   ***** In-it courier detail view contents *****  */
        intItATLInfoDetailView();           /*   ***** In-it ATL detail view contents *****  */

        findViewById(R.id.pickUpHeaderLayoutABD).setOnClickListener(this);
        findViewById(R.id.dropOffHeaderLayoutABD).setOnClickListener(this);


        pickHeaderLayoutPickTimeTxtABD = (TextView) findViewById(R.id.pickHeaderLayoutPickTimeTxtABD);

        pickDropNotesABD = (TextView) findViewById(R.id.pickDropNotesABD);
        pickDropDate = findViewById(R.id.pickDropDate);

        if (activeBookingModelForSelectedItem.getStatus().equals("Picked up") ||
                activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff") ||
                activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
            showDropInformation();
        } else
            showPickInformation();
        pickDropReadMoreTxtABD = (TextView) findViewById(R.id.pickDropReadMoreTxtABD);
        pickDropReadMoreTxtABD.setOnClickListener(this);

        findViewById(R.id.iconCallABD).setOnClickListener(this);
        findViewById(R.id.iconMapABD).setOnClickListener(this);

        inItETADialogViewOnETAUpdate();                  //******** Init ETA dialog **********
        inItActiveBookingDetailMapFragment();            //******** Initialize active booking detail view map
    }
    public void showPickInformation() {
        isPickShowed = true;
        findViewById(R.id.pickUpHeaderLayoutABD).setBackgroundResource(R.drawable.new_gray_back);
        findViewById(R.id.dropOffHeaderLayoutABD).setBackgroundResource(R.drawable.new_gary_backgroun);
        ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD)).setTextColor(Color.parseColor("#374350"));
        ((TextView) findViewById(R.id.dropEnd)).setTextColor(Color.parseColor("#374350"));
        ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD)).setTextColor(Color.parseColor("#FFFFFF"));
        ((TextView) findViewById(R.id.pickupEnd)).setTextColor(Color.parseColor("#FFFFFF"));
        pickOrDropPersonContactNoToCall = activeBookingModelForSelectedItem.getPick_Phone();
        pickOrDropPersonAddressForMap = activeBookingModelForSelectedItem.getPick_Address();
        ((TextView) findViewById(R.id.pickDropCustomerNameABD)).setText(activeBookingModelForSelectedItem.getPick_ContactName());
        ((TextView) findViewById(R.id.pickDropMolibeABD)).setText(activeBookingModelForSelectedItem.getPick_Phone());
        setPickDropCompanyName(activeBookingModelForSelectedItem.getPickupCompanyName());
        ((TextView) findViewById(R.id.pickDropAddressABD)).setText(activeBookingModelForSelectedItem.getPick_Address());
        pickDropNotesABD.setText("Notes : " +activeBookingModelForSelectedItem.getPick_Notes());
        setBookingTimeToField(pickHeaderLayoutPickTimeTxtABD, activeBookingModelForSelectedItem.getPickupDateTime(), "", false, false);
        setBookingTimeToField((TextView) findViewById(R.id.pickupEnd), activeBookingModelForSelectedItem.getPickupDateTime(), "", false, false);
        String bookingCreateTimeActiveBooking;
        bookingCreateTimeActiveBooking = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + activeBookingModelForSelectedItem.getPickupDateTime());
        pickDropDate.setText(bookingCreateTimeActiveBooking);
        showOrHideReadMoreText();
    }

    public void showDropInformation() {
        isPickShowed = false;
        findViewById(R.id.pickUpHeaderLayoutABD).setBackgroundResource(R.drawable.new_gary_backgroun);
        findViewById(R.id.dropOffHeaderLayoutABD).setBackgroundResource(R.drawable.new_gray_back);
        ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD)).setTextColor(Color.parseColor("#374350"));
        ((TextView) findViewById(R.id.pickupEnd)).setTextColor(Color.parseColor("#374350"));
        ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD)).setTextColor(Color.parseColor("#FFFFFF"));
        ((TextView) findViewById(R.id.dropEnd)).setTextColor(Color.parseColor("#FFFFFF"));
        pickOrDropPersonContactNoToCall = activeBookingModelForSelectedItem.getDrop_Phone();
        pickOrDropPersonAddressForMap = activeBookingModelForSelectedItem.getDrop_Address();
        ((TextView) findViewById(R.id.pickDropCustomerNameABD)).setText(activeBookingModelForSelectedItem.getDrop_ContactName());
        ((TextView) findViewById(R.id.pickDropMolibeABD)).setText(activeBookingModelForSelectedItem.getDrop_Phone());
        setPickDropCompanyName(activeBookingModelForSelectedItem.getDrop_Company());
        ((TextView) findViewById(R.id.pickDropAddressABD)).setText(activeBookingModelForSelectedItem.getDrop_Address());
        pickDropNotesABD.setText("Notes : " +activeBookingModelForSelectedItem.getDrop_Notes());
        String bookingCreateTimeActiveBooking = null;
        bookingCreateTimeActiveBooking = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + activeBookingModelForSelectedItem.getDropDateTime());
        pickDropDate.setText(bookingCreateTimeActiveBooking);
        setBookingTimeToField(pickHeaderLayoutPickTimeTxtABD, activeBookingModelForSelectedItem.getDropDateTime(), "", false, false);
        setBookingTimeToField( (TextView) findViewById(R.id.dropEnd), activeBookingModelForSelectedItem.getDropDateTime(), "", false, false);
        showOrHideReadMoreText();
    }
    private void showHideNewCustomerText() {
        if (activeBookingModelForSelectedItem.isNewCustomer())
            findViewById(R.id.newCustomerTxtInABD).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.newCustomerTxtInABD).setVisibility(View.INVISIBLE);
    }

    public void showDimensionPopup(){
        Dialog enterFieldDialog;


        try {
            enterFieldDialog = new Dialog(this, android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(true);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.show_popup_dimesion);


            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);
            View dimension = enterFieldDialog.findViewById(R.id.dimension_view);
            RecyclerView recycler_dimension = dimension.findViewById(R.id.dimension_recyclerview);


            ((TextView) enterFieldDialog.findViewById(R.id.priceTxtABD)).setText("$" + Functional_Utility.returnCourierPrice((Double) activeBookingModelForSelectedItem.getPrice()));
            ((TextView) enterFieldDialog.findViewById(R.id.vehicleTxtABD)).setText((String) activeBookingModelForSelectedItem.getVehicle());
            TextView documentTxtABD1=((TextView) enterFieldDialog.findViewById(R.id.documentTxtABD));
            TextView dimentionDetailsTxtABD = enterFieldDialog.findViewById(R.id.dimentionDetailsTxtABD);

            enterFieldDialog.findViewById(R.id.viewDimensionTxtABD).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.cancel();
                }
            });

            if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                if (activeBookingModelForSelectedItem.getStatus().equals("Accepted") || activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")) {
                    documentTxtABD1.setText("Pieces: " + activeBookingModelForSelectedItem.getTotalPieceCount());
                    dimentionDetailsTxtABD.setVisibility(View.VISIBLE);
                    dimentionDetailsTxtABD.setText("Custom");
                    enterFieldDialog.findViewById(R.id.ll_di).setVisibility(View.GONE);
                } else {
                    int pickedUpPieceCount = activeBookingModelForSelectedItem.getPickedUpPieceCount();
                    int totalPieceCount = activeBookingModelForSelectedItem.getTotalPieceCount();
                    documentTxtABD1.setText("Pieces: " + pickedUpPieceCount + "/" + totalPieceCount);
                    dimentionDetailsTxtABD.setVisibility(View.VISIBLE);
                    dimentionDetailsTxtABD.setText("Custom");
                    enterFieldDialog.findViewById(R.id.ll_di).setVisibility(View.GONE);

                }


            } else {

                try {
                    ArrayList<HashMap<String, Object>> shipmentArray = activeBookingModelForSelectedItem.getShipmentsArray();
                    if (shipmentArray.size() > 0) {

                        for (int i = 0; i < shipmentArray.size(); i++) {
                            String category = "" + shipmentArray.get(i).get("Category");
                            String quantity = "" + shipmentArray.get(i).get("Quantity");
                            if (i == 0) {
                                documentTxtABD1.append(category + " (" + quantity + ")");

                            } else {
                                documentTxtABD1.append(", " + category + " (" + quantity + ")");
                            }
                        }

                        enterFieldDialog.findViewById(R.id.ll_di).setVisibility(View.VISIBLE);
                        Dimension_class.setDimension(recycler_dimension, ActiveBookingDetail_New.this,shipmentArray);
                    } else {
                        documentTxtABD1.append("" + activeBookingModelForSelectedItem.getPackage());
                        dimentionDetailsTxtABD.setVisibility(View.VISIBLE);
                        dimentionDetailsTxtABD.append("No dimension");
                        enterFieldDialog.findViewById(R.id.ll_di).setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    documentTxtABD1.append("" + activeBookingModelForSelectedItem.getPackage());


                }


            }

            enterFieldDialog.findViewById(R.id.mainDefaultDialogLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                enterFieldDialog.cancel();
                }
            });
                enterFieldDialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    //*********** If pick or Drop notes is greater than 3 line then show read more text otherwise its hide *******
    private void showOrHideReadMoreText() {
        pickDropNotesABD.post(new Runnable() {
            @Override
            public void run() {
                int lineCount = pickDropNotesABD.getLineCount();
                if (lineCount > 3) {
                    if (!isReadMoreTxt) {
                        pickDropNotesABD.setMaxLines(1);
                        pickDropReadMoreTxtABD.setVisibility(View.VISIBLE);
                        findViewById(R.id.bottomBtnLayoutABD).setVisibility(View.VISIBLE);
                    } else {
                        pickDropNotesABD.setMaxLines(500);
                        pickDropReadMoreTxtABD.setVisibility(View.VISIBLE);
                        pickDropReadMoreTxtABD.setText("Show less");
                        findViewById(R.id.bottomBtnLayoutABD).setVisibility(View.VISIBLE);
                    }
                } else {
                    pickDropNotesABD.setMaxLines(3);
                    pickDropReadMoreTxtABD.setVisibility(View.GONE);
                    findViewById(R.id.bottomBtnLayoutABD).setVisibility(View.VISIBLE);
                }
            }
        });
    }

    /*
     ********* Set time in HH:mm format to particular field **********
     */
    private void setBookingTimeToField(TextView timeTxtInABD, String dropDateTime, String addedTxtToField, boolean isBothDateTimeInvolved, boolean isAlreadyConvertedDate) {
        try {
            String[] bookingTimeToFieldArray;
            if (!dropDateTime.equals("") && !dropDateTime.equals("null")) {
                if (isAlreadyConvertedDate) {
                    bookingTimeToFieldArray = dropDateTime.split(" ");
                    if (!addedTxtToField.equals(""))
                        timeTxtInABD.setText(addedTxtToField + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                    else
                        timeTxtInABD.setText(bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                } else {
                    String bookingTimeToField = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(dropDateTime);
                    bookingTimeToFieldArray = bookingTimeToField.split(" ");
                    if (isBothDateTimeInvolved)
                        timeTxtInABD.setText("Created " + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2] + " on " + bookingTimeToFieldArray[0]);
                    else {
                        if (!addedTxtToField.equals(""))
                            timeTxtInABD.setText(addedTxtToField + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                        else
                            timeTxtInABD.setText(bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                    }
                    bookingTimeToFieldArray = null;
                }
            } else
                timeTxtInABD.setText("-NA-");
        } catch (Exception e) {
            e.printStackTrace();
            timeTxtInABD.setText("-NA-");
        }
    }

    /*   ***** In-it courier detail view contents *****  */
    private void inItCustomerInfoDetailView() {
        customerDetailLayoutABD = (RelativeLayout) findViewById(R.id.customerDetailLayoutABD);
        customerDetailLayoutABD.setOnClickListener(this);


        bookingRefNoTxtABD = (TextView) findViewById(R.id.bookingRefNoTxtABD);

        if (activeBookingModelForSelectedItem.getRunNumber() != 0) {
            setrefNoOrRunNumber(String.valueOf(activeBookingModelForSelectedItem.getRunNumber()), true);
        } else
            setrefNoOrRunNumber(activeBookingModelForSelectedItem.getBookingRefNo(), false);

        arrowUpDownImgCustmerLayout = (ImageView) findViewById(R.id.arrowUpDownImgCustmerLayout);


    }

    private void setrefNoOrRunNumber(String bookingRefNoOrRunNo, boolean isRunNumber) {
        String runNumberTxt;
        if (isRunNumber)
            runNumberTxt = "Run no.- " + bookingRefNoOrRunNo;
        else
            runNumberTxt = "#" + bookingRefNoOrRunNo;
        try {
            if (!activeBookingModelForSelectedItem.getOrderNumber().equals(""))
                bookingRefNoTxtABD.setText("AWB: " + activeBookingModelForSelectedItem.getOrderNumber());
                //bookingRefNoTxtABD.setText(Html.fromHtml("<body><font color=#76D750 face=Proxima Nova Semibold>"+runNumberTxt+"</font><font size=12 color=#00A6E3 face=Proxima Nova Semibold><br/>AWB: "+activeBookingModelForSelectedItem.getOrderNumber()+"</font>"));
            else
                bookingRefNoTxtABD.setText("More Info..");
            // bookingRefNoTxtABD.setText(runNumberTxt);
        } catch (Exception e) {
            e.printStackTrace();
            bookingRefNoTxtABD.setText(runNumberTxt);
        }
    }

    private int setHghtWdthInPixelFromDP(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

    private void inItarrowForCollapseExpaImgParam(ImageView arrowUpDownImg, int supportLayoutRefId, int imgResource) {
        arrowUpDownImg.setImageResource(imgResource);
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramForCollapseExpandImgParam.setMargins(10, 0, 0, 10);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_BOTTOM, supportLayoutRefId);
        arrowUpDownImg.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
    }

    /*
     * ****** Collapse Courier detail view  *********
     */
    private void collapseCourierDetailABD() {
       // customerInfoHiddenLayout.setVisibility(View.GONE);
        inItarrowForCollapseExpaImgParam(arrowUpDownImgCustmerLayout, R.id.bookingRefNoTxtABD, R.drawable.jobopen_arrow);
        if (bookingRefNoTxtABD.getText() == ("  Less Info.."))
            bookingRefNoTxtABD.setText("More Info..");
        atlViewUI_Visibility();
    }

    /*
     * ****** Expand Courier detail view  *********
     */
    private void expandCourierDetailABD() {
        inItarrowForCollapseExpaImgParam(arrowUpDownImgCustmerLayout, R.id.customerInfoHiddenLayout, R.drawable.joboclose_arrow);
        if (bookingRefNoTxtABD.getText() == ("More Info.."))
            bookingRefNoTxtABD.setText("  Less Info..");
        atlDetailLayoutABD.setVisibility(View.GONE);
    }

    /*
     * ***** In-it ATL detail view contents *****
     */
    private void intItATLInfoDetailView() {
        atlDetailLayoutABD = (RelativeLayout) findViewById(R.id.atlDetailLayoutABD);
        atlViewUI_Visibility();   // ******** To hide and show ATL view ***********

        atlInfoHiddenLayout = (RelativeLayout) findViewById(R.id.atlInfoHiddenLayout);
        atlInfoHiddenLayout.setVisibility(View.GONE);

        txtHeaderATLBtnInATLDetail = (TextView) findViewById(R.id.txtHeaderATLBtnInATLDetail);

        atlInfoWhereTxtDetailABD = (TextView) findViewById(R.id.atlInfoWhereTxtDetailABD);

        doorCodeTxtATLDetailABD = (TextView) findViewById(R.id.doorCodeTxtATLDetailABD);

        customerTxtATLDetailABD = (TextView) findViewById(R.id.customerTxtATLDetailABD);

        notesTxtATLDetailABD = (TextView) findViewById(R.id.notesTxtATLDetailABD);


        arrowUpDownImgATLImg = (ImageView) findViewById(R.id.arrowUpDownImgATLImg);
        collapseATLDetailABD();
    }


    public void showPopup() {
        Dialog enterFieldDialog;


        try {
            enterFieldDialog = new Dialog(this, android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(true);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.details_popup_active);
            enterFieldDialog.setCancelable(true);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            ((TextView) enterFieldDialog.findViewById(R.id.priceTxtABD)).setText("$" + Functional_Utility.returnCourierPrice((Double) activeBookingModelForSelectedItem.getPrice()));
            TextView Awb_no= (TextView) enterFieldDialog.findViewById(R.id.Awb_no);
            try {
                if (!activeBookingModelForSelectedItem.getOrderNumber().equals(""))
                    Awb_no.setText("AWB: " + activeBookingModelForSelectedItem.getOrderNumber());
                else
                    Awb_no.setText("AWB: -");

            } catch (Exception e) {
                e.printStackTrace();
                Awb_no.setText("AWB: -");
            }
            ((TextView) enterFieldDialog.findViewById(R.id.vehicleTxtABD)).setText(activeBookingModelForSelectedItem.getVehicle());
            ((TextView) enterFieldDialog.findViewById(R.id.bookingId)).setText("#" + activeBookingModelForSelectedItem.getBookingRefNo());
            setBookingTimeToField((TextView) enterFieldDialog.findViewById(R.id.createdTimeTxtCustomerDetailABD), activeBookingModelForSelectedItem.getCreatedDateTime(), "", true, false);
            ((TextView) enterFieldDialog.findViewById(R.id.customerNameTxtCoureirDetailABD)).setText(activeBookingModelForSelectedItem.getCustomerName());

            String cmpanyNameMobileInfoStr = "";
            if (!activeBookingModelForSelectedItem.getCustomerCompany().equals("")
                    && !activeBookingModelForSelectedItem.getCustomerCompany().equals("null"))
                cmpanyNameMobileInfoStr = activeBookingModelForSelectedItem.getCustomerCompany() + "\n";
            if (!activeBookingModelForSelectedItem.getCustomerContact().equals("")
                    && !activeBookingModelForSelectedItem.getCustomerContact().equals("null"))
                cmpanyNameMobileInfoStr = cmpanyNameMobileInfoStr + activeBookingModelForSelectedItem.getCustomerContact();
            ((TextView) enterFieldDialog.findViewById(R.id.customerCompanyAndMobNoTxtCouDetalABD)).setText(cmpanyNameMobileInfoStr);


            (enterFieldDialog.findViewById(R.id.iconCallCustomerBtnInDeailABD)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callingIntent = new Intent(Intent.ACTION_DIAL);
                    callingIntent.setData(Uri.parse("tel:"+activeBookingModelForSelectedItem.getCustomerContact()));
                    startActivity(callingIntent);
                    callingIntent = null;
                }
            });

            (enterFieldDialog.findViewById(R.id.mainDefaultDialogLayout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.cancel();
                }
            });

            if (!activeBookingModelForSelectedItem.getNotes().equals("")
                    && !activeBookingModelForSelectedItem.getNotes().equals("null"))
                ((TextView) enterFieldDialog.findViewById(R.id.deliveryNotesTxtcourierDetailABD)).setText(activeBookingModelForSelectedItem.getNotes());
            else
                ((TextView) enterFieldDialog.findViewById(R.id.deliveryNotesTxtcourierDetailABD)).setText("Delivery instructions here");

            enterFieldDialog.setCanceledOnTouchOutside(true);
            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /*
     ****** Hide and Show ATL view ***********
     * */
    private void atlViewUI_Visibility() {
        if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
            if (activeBookingModelForSelectedItem.isATL() == false && activeBookingModelForSelectedItem.isAuthorityToLeavePermitted() == false)
                showHideATLButton(this, View.VISIBLE);
            else if ((activeBookingModelForSelectedItem.getNoContactDelivery() || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) && activeBookingModelForSelectedItem.isATL())
                showHideATLButton(this, View.VISIBLE);
            else
                showHideATLButton(null, View.GONE);
        } else if (activeBookingModelForSelectedItem.isATL() == true) {
            if (activeBookingModelForSelectedItem.getNoContactDelivery() || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver"))
                showHideATLButton(this, View.VISIBLE);
            else
                showHideATLButton(null, View.GONE);
        } else
            showHideATLButton(null, View.GONE);
    }

    //************ When to show ATL button ************
    private void showHideATLButton(ActiveBookingDetail_New activeBookingDetail_new, int visibility) {
        atlDetailLayoutABD.setOnClickListener(activeBookingDetail_new);
        atlDetailLayoutABD.setVisibility(visibility);
    }

    /*
     * ****** Collapse ATL detail view  *********
     */
    private void collapseATLDetailABD() {
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(setHghtWdthInPixelFromDP(140), setHghtWdthInPixelFromDP(37));
        txtHeaderATLBtnInATLDetail.setGravity(Gravity.CENTER);

        txtHeaderATLBtnInATLDetail.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
        txtHeaderATLBtnInATLDetail.setAlpha(1.0f);
        txtHeaderATLBtnInATLDetail.setEnabled(false);
        txtHeaderATLBtnInATLDetail.setClickable(false);
        txtHeaderATLBtnInATLDetail.setTextColor(getResources().getColor(R.color.white));
        txtHeaderATLBtnInATLDetail.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramForCollapseExpandImgParam.setMargins(0, setHghtWdthInPixelFromDP(72), setHghtWdthInPixelFromDP(10), 0);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        atlDetailLayoutABD.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
        atlInfoHiddenLayout.setVisibility(View.GONE);
        inItarrowForCollapseExpaImgParam(arrowUpDownImgATLImg, R.id.txtHeaderATLBtnInATLDetail, R.drawable.jobopen_arrow);
        customerDetailLayoutABD.setVisibility(View.VISIBLE);

        if (activeBookingModelForSelectedItem.getSource().equals("DHL") && activeBookingModelForSelectedItem.isAuthorityToLeavePermitted() == false && activeBookingModelForSelectedItem.isATL() == false) {
            txtHeaderATLBtnInATLDetail.setText("Signature Req.");
            atlInfoWhereTxtDetailABD.setText("No ATL has been provided for this parcel.");
            doorCodeTxtATLDetailABD.setText("");
            customerTxtATLDetailABD.setText("A signature must be collected from the recipient, or the parcel returned.");
            notesTxtATLDetailABD.setText("Contact Zoom2u on 1300 318 675 if you have any questions.");
            atlDetailLayoutABD.setBackgroundResource(R.drawable.rounded_elite_level);
        }
        //************* ATL information for DHL deliveries *************
        else if (activeBookingModelForSelectedItem.getSource().equals("DHL") && (activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver") || activeBookingModelForSelectedItem.getNoContactDelivery()) && activeBookingModelForSelectedItem.isATL()) {
            txtHeaderATLBtnInATLDetail.setText("Safe Drop Allowed");
            setATLDetailTxt(atlInfoWhereTxtDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Where </font>" + activeBookingModelForSelectedItem.getATLLeaveAt() + "</body"));
            setATLDetailTxt(doorCodeTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Door code </font>" + activeBookingModelForSelectedItem.getATLDoorCode() + "</body"));
            setATLDetailTxt(customerTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Name </font>" + activeBookingModelForSelectedItem.getATLReceiverName() + "</body"));
            setATLDetailTxt(notesTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Delivery notes </font>" + activeBookingModelForSelectedItem.getATLInstructions() + "</body"));
            atlDetailLayoutABD.setBackgroundResource(R.drawable.rounded_worrier_level);
        }
        //************* ATL information for Telstra/Normal deliveries *************
        else if (activeBookingModelForSelectedItem.isATL()) {
            txtHeaderATLBtnInATLDetail.setText("Safe Drop Allowed");
            setATLDetailTxt(atlInfoWhereTxtDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Where </font>" + activeBookingModelForSelectedItem.getATLLeaveAt() + "</body"));
            setATLDetailTxt(doorCodeTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Door code </font>" + activeBookingModelForSelectedItem.getATLDoorCode() + "</body"));
            setATLDetailTxt(customerTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Name </font>" + activeBookingModelForSelectedItem.getATLReceiverName() + "</body"));
            setATLDetailTxt(notesTxtATLDetailABD, Html.fromHtml("<body><font color=\"black\" face=\'Proxima Nova Semibold\'>Delivery notes </font>" + activeBookingModelForSelectedItem.getATLInstructions() + "</body"));
            atlDetailLayoutABD.setBackgroundResource(R.drawable.rounded_worrier_level);
        }
    }

    /*
     * ****** Set text to ATL detail view  *********
     */
    private void setATLDetailTxt(TextView txtViewATLDetailABD, Spanned atlTxtFromServer) {
        if (!atlTxtFromServer.equals("") && !atlTxtFromServer.equals("null"))
            txtViewATLDetailABD.setText(atlTxtFromServer);
        else
            txtViewATLDetailABD.setText("-NA-");
    }

    /*
     * ****** Expand ATL detail view  *********
     */
    private void expandATLDetailABD() {
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, setHghtWdthInPixelFromDP(37));
        paramForCollapseExpandImgParam.setMargins(setHghtWdthInPixelFromDP(13), setHghtWdthInPixelFromDP(10), 0, 0);
        txtHeaderATLBtnInATLDetail.setGravity(Gravity.CENTER);

        txtHeaderATLBtnInATLDetail.setPadding(15, 0, 15, 0);
        txtHeaderATLBtnInATLDetail.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
        txtHeaderATLBtnInATLDetail.setTextColor(getResources().getColor(R.color.white));
        atlDetailLayoutABD.setBackgroundResource(R.drawable.roundedwhiteback);
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramForCollapseExpandImgParam.setMargins(setHghtWdthInPixelFromDP(10), setHghtWdthInPixelFromDP(10), setHghtWdthInPixelFromDP(10), 0);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        atlDetailLayoutABD.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
        atlInfoHiddenLayout.setVisibility(View.VISIBLE);
        inItarrowForCollapseExpaImgParam(arrowUpDownImgATLImg, R.id.atlInfoHiddenLayout, R.drawable.joboclose_arrow);
        customerDetailLayoutABD.setVisibility(View.GONE);

        if (activeBookingModelForSelectedItem.isATL()) {
            if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                    || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                txtHeaderATLBtnInATLDetail.setAlpha(1.0f);
                txtHeaderATLBtnInATLDetail.setEnabled(true);
                txtHeaderATLBtnInATLDetail.setClickable(true);
                txtHeaderATLBtnInATLDetail.setOnClickListener(this);
            } else {
                txtHeaderATLBtnInATLDetail.setAlpha(0.5f);
                txtHeaderATLBtnInATLDetail.setEnabled(false);
                txtHeaderATLBtnInATLDetail.setClickable(false);
                txtHeaderATLBtnInATLDetail.setOnClickListener(null);
            }
            txtHeaderATLBtnInATLDetail.setText("Authority To Leave");
            txtHeaderATLBtnInATLDetail.setBackgroundResource(R.drawable.rounded_worrier_level);
        } else {
            txtHeaderATLBtnInATLDetail.setEnabled(true);
            txtHeaderATLBtnInATLDetail.setClickable(false);
            txtHeaderATLBtnInATLDetail.setText("Signature Required");
            txtHeaderATLBtnInATLDetail.setBackgroundResource(R.drawable.rounded_elite_level);
        }
    }

    //********************* Customer ID check alert before Drop off the booking
    Dialog alertDialogIDCheckAlert;

    private void deliveryIdCheckAlert() {
        try {
            try {
                if (alertDialogIDCheckAlert != null)
                    if (alertDialogIDCheckAlert.isShowing())
                        alertDialogIDCheckAlert.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            alertDialogIDCheckAlert = new Dialog(ActiveBookingDetail_New.this);
            alertDialogIDCheckAlert.setCancelable(true);
            alertDialogIDCheckAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ACACB0")));
            alertDialogIDCheckAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialogIDCheckAlert.setContentView(R.layout.delivery_id_check_alert);

            Window window = alertDialogIDCheckAlert.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            ImageView deliveryCheckDialog_Image = (ImageView) alertDialogIDCheckAlert.findViewById(R.id.deliveryCheckDialog_Image);
            if (activeBookingModelForSelectedItem.getDropIdentityType().equals("DriversLicence"))
                deliveryCheckDialog_Image.setImageResource(R.drawable.licence);
            else if (activeBookingModelForSelectedItem.getDropIdentityType().equals("Passport"))
                deliveryCheckDialog_Image.setImageResource(R.drawable.customer_passport);
            else
                deliveryCheckDialog_Image.setImageResource(R.drawable.customer_citizencard);

            TextView headingTxtDeliveryIdCheck = (TextView) alertDialogIDCheckAlert.findViewById(R.id.headingTxtDeliveryIdCheck);

            TextView msgTxtDeliveryIdCheck = (TextView) alertDialogIDCheckAlert.findViewById(R.id.msgTxtDeliveryIdCheck);

            try {
                msgTxtDeliveryIdCheck.setText("Please enter the last 4 digits of the recipient's " + activeBookingModelForSelectedItem.getDropIdentityType() + "\n\n" +
                        "NOTE:\n" +
                        "- Photocopies are not valid ID\n" +
                        "- Name of the recipient must also match name on the ID\n" +
                        "- DO NOT take a photo of the ID.");
            } catch (Exception e) {
                e.printStackTrace();
                msgTxtDeliveryIdCheck.setText("Please enter the last 4 digits of the recipient's ID number\n\n" +
                        "NOTE:\n" +
                        "- Photocopies are not valid ID\n" +
                        "- Name of the recipient must also match name on the ID\n" +
                        "- DO NOT take a photo of the ID.");
            }

            final EditText edtEnterPin = (EditText) alertDialogIDCheckAlert.findViewById(R.id.edtEnterPin);


            TextView closeBtnIdAlert = (TextView) alertDialogIDCheckAlert.findViewById(R.id.closeBtnIdAlert);

            Paint paint = new Paint();
            paint.setARGB(255, 255, 71, 106);
            paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
            closeBtnIdAlert.setPaintFlags(paint.getFlags());

            closeBtnIdAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogIDCheckAlert.dismiss();
                    alertDialogIDCheckAlert = null;
                }
            });

            Button confirmBtnDeliveryIdCheck = (Button) alertDialogIDCheckAlert.findViewById(R.id.confirmBtnDeliveryIdCheck);

            confirmBtnDeliveryIdCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!edtEnterPin.getText().toString().equals("")) {
                        if (edtEnterPin.getText().toString().equalsIgnoreCase(activeBookingModelForSelectedItem.getDropIdentityNumber())) {
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
                            alertDialogIDCheckAlert.dismiss();
                            alertDialogIDCheckAlert = null;
                        } else
                            dialogInActiveDetailToShow_IDCkeck_Error_Msg(ActiveBookingDetail_New.this, "", Html.fromHtml("Please try again, or contact<br><font color=\"#45515b\"><big><b>" + activeBookingModelForSelectedItem.getCustomerName() + "</b></big></font><br>on " +
                                    "<font color=\"#00A6E3\"><big><b>" + activeBookingModelForSelectedItem.getCustomerContact() + "</b></big></font><br>to check the ID they received."));
                    } else
                        Toast.makeText(ActiveBookingDetail_New.this, "Please enter the ID number against customer provided number to drop this delivery", Toast.LENGTH_LONG).show();
                }
            });
            alertDialogIDCheckAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
        }
    }

    //********************* Customer ID check error alert
    Dialog idCheckErrorDialog;

    private void dialogInActiveDetailToShow_IDCkeck_Error_Msg(ActiveBookingDetail_New activeBookingDetail_new, String headerMsg, Spanned msgStr) {
        try {
            if (idCheckErrorDialog != null)
                if (idCheckErrorDialog.isShowing())
                    idCheckErrorDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        idCheckErrorDialog = null;
        idCheckErrorDialog = new Dialog(activeBookingDetail_new);
        idCheckErrorDialog.setCancelable(true);
        idCheckErrorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
        idCheckErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        idCheckErrorDialog.setContentView(R.layout.id_check_error_alert);

        Window window = idCheckErrorDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView errorMsgTxt_IDCheck = (TextView) idCheckErrorDialog.findViewById(R.id.errorMsgTxt_IDCheck);


        TextView errorMsgHint_IDCheck = (TextView) idCheckErrorDialog.findViewById(R.id.errorMsgHint_IDCheck);

        errorMsgHint_IDCheck.setText(msgStr);

        Button okBtn__IDCheckError = (Button) idCheckErrorDialog.findViewById(R.id.okBtn__IDCheckError);

        okBtn__IDCheckError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheckErrorDialog.dismiss();
            }
        });

        idCheckErrorDialog.show();
    }

    public static int convertDpToPixelInAlert(Context context, int dpValue) {
        Resources r = context.getResources();
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        return pixel;
    }

    //************* Alert dialog to show special instruction before Pickup or Dropoff ***********
    private void alertToShowSpecialInsBeforePickOrDrop(String msgStr) {
        try {
            if (!msgStr.equals("") && !msgStr.equals("null")) {
                try {
                    if (alertDialog != null)
                        if (alertDialog.isShowing())
                            alertDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDialog = new Dialog(ActiveBookingDetail_New.this);
                alertDialog.setCancelable(true);
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialog.setContentView(R.layout.new_dialogview);

                Window window = alertDialog.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                TextView titleDialogNew = (TextView) alertDialog.findViewById(R.id.titleDialogNew);

                titleDialogNew.setText("Special Instruction!");

                TextView dialogMessageTextNew = (TextView) alertDialog.findViewById(R.id.dialogMessageTextNew);

                dialogMessageTextNew.setText(msgStr);

                Button okBtnNewDialog = (Button) alertDialog.findViewById(R.id.okBtnNewDialog);

                okBtnNewDialog.setText("OK");
                okBtnNewDialog.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        alertDialog = null;
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            //new AcceptNotesTimeStampAsyncTask().execute();
                            AcceptNotesTimeStampAsyncTask();
                        else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
                    }
                });

                alertDialog.show();
            } else
                pickAndDropBtnFunctionality();
        } catch (Exception e) {
            e.printStackTrace();
            pickAndDropBtnFunctionality();
        }
    }

    private void AcceptNotesTimeStampAsyncTask(){
        final boolean[] isAcceptNotesTimeStampSuccess = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressInActiveDetailView != null)
                    progressInActiveDetailView = null;
                progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    String responseAcceptNotesTimeStamp = "";
                    if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                        responseAcceptNotesTimeStamp = webServiceHandler.addAcceptNotesTimeStamp((Integer) activeBookingModelForSelectedItem.getBookingId(), false);
                    else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver"))
                        responseAcceptNotesTimeStamp = webServiceHandler.addAcceptNotesTimeStamp((Integer) activeBookingModelForSelectedItem.getBookingId(), true);

                    JSONObject jOBJForRequestPick = new JSONObject(responseAcceptNotesTimeStamp);
                    isAcceptNotesTimeStampSuccess[0] = jOBJForRequestPick.getBoolean("success");
                    jOBJForRequestPick = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (isAcceptNotesTimeStampSuccess[0])
                        pickAndDropBtnFunctionality();
                    else
                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Sorry!", "Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    //******** Call async task request for On route to pick and Drop ************
    private void callOnRouteTask() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new OnRouteDetailAsyncTask().execute();
            OnRouteDetailAsyncTask();
        else
            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
    }

    //******** Show driving direction on Google map ************
    private void showDrivingDirection() {
        Intent mapIntent;

        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0")) {
            String currLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
            String desLocation = "";
            if (activeBookingModelForSelectedItem.getStatus().equals("Accepted"))
                desLocation = activeBookingModelForSelectedItem.getPick_GPSX() + "," + activeBookingModelForSelectedItem.getPick_GPSY();
            else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up"))
                desLocation = activeBookingModelForSelectedItem.getDrop_GPSX() + "," + activeBookingModelForSelectedItem.getDrop_GPSY();

            // "d" means driving car, "w" means walking "r" means by bus
            mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?"
                    + "saddr=" + currLocation + "&daddr=" + desLocation + "&dirflg=d"));
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        } else {
            Uri gmmIntentUri;
            if (activeBookingModelForSelectedItem.getStatus().equals("Accepted"))
                gmmIntentUri = Uri.parse("geo:0,0?q=" + activeBookingModelForSelectedItem.getPick_Address());
            else
                gmmIntentUri = Uri.parse("geo:0,0?q=" + activeBookingModelForSelectedItem.getDrop_Address());
            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }

        if (mapIntent.resolveActivity(getPackageManager()) != null)
            startActivity(mapIntent);
        mapIntent = null;
    }

private void shareTheBookingDetailsViaWhatsApp(){

    boolean isInstalled = isAppInstalled(this, "com.whatsapp");
    boolean isInstalledWhatsappBussines = isAppInstalled(this, "com.whatsapp.w4b");
    String helpMsgToAdminStr = "I need help with booking " + activeBookingModelForSelectedItem.getBookingRefNo();

    if (isInstalled || isInstalledWhatsappBussines) {
        try {
            String url = "https://wa.me/61456375000?text=" + URLEncoder.encode(helpMsgToAdminStr, "UTF-8");
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    } else
        Toast.makeText(this, "Please install WhatsApp from play store to start chatting with customer support.", Toast.LENGTH_LONG)
                .show();

}
    @SuppressLint("SuspiciousIndentation")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.whatsApp:
                shareTheBookingDetailsViaWhatsApp();
                break;
            case R.id.pickDropBtnABD:
                isForReturnToPickup = 0;
                if (activeBookingModelForSelectedItem.getSource().equals("Temando")
                        || activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                    if (activeBookingModelForSelectedItem.getSource().equals("Temando")) {
                        if (activeBookingModelForSelectedItem.getStatus().equals("Accepted"))
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getPick_Notes());
                        else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up"))
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
                    } else {
                        if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getPick_Notes());
                        else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                            if (activeBookingModelForSelectedItem.getSource().equals("DHL") && activeBookingModelForSelectedItem.isATL() == false && activeBookingModelForSelectedItem.isAuthorityToLeavePermitted() == false)
                                dialogNonATLBooking(1);
                            else {
                                alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
                            }
                        } else
                            pickAndDropBtnFunctionality();
                    }
                } else {
                    if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")) {
                        if (activeBookingModelForSelectedItem.getStatus().equals("Picked up"))
                            callOnRouteTask();
                        else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver"))
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
                        else
                            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getPick_Notes());
                    } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                        alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getPick_Notes());
                    else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                        checkDeliveryDropIdAlert();                 //********* Show Alert if any ID exist against customer provided id
                    } else
                        callOnRouteTask();
                }
                break;
            case R.id.txtHeaderATLBtnInATLDetail:
                if (activeBookingModelForSelectedItem.getSource().equals("DHL") || activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT"))
                    openBarCodeScannerView(5);
                else {
                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                    atlDialogAlert();
                }
                break;
            case R.id.triedToDeliverBtnABD:
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                isForReturnToPickup = 0;
                if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                        || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                        || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")) {
                    if (checkPermissions()) {
                        if (statusOfGPS) {
                            int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(activeBookingModelForSelectedItem.getDrop_GPSX(),
                                    activeBookingModelForSelectedItem.getDrop_GPSY());

                            if (!activeBookingModelForSelectedItem.getSource().equals("DHL") && activeBookingModelForSelectedItem.isATL()) {
                                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                                atlDialogAlert();
                            } else if (activeBookingModelForSelectedItem.getPick_ContactName().equalsIgnoreCase("Telstra")) {
                                callForAtteptDeliveryWindow(activeBookingModelForSelectedItem.isDoesAlcoholDeliveries());
                            } else {
                                if (activeBookingModelForSelectedItem.getSource().equals("DHL") && activeBookingModelForSelectedItem.isATL() == false && activeBookingModelForSelectedItem.isAuthorityToLeavePermitted() == false) {
                                    if (distanceFromCurrentToDrop > 1000)
                                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                    else
                                        dialogNonATLBooking(2);
                                } else if ((activeBookingModelForSelectedItem.getSource().equals("DHL") || activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT"))
                                        && activeBookingModelForSelectedItem.isATL())
                                    openBarCodeScannerView(5);
                                else if (activeBookingModelForSelectedItem.getSource().equals("DHL") || activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")) {
                                    if (distanceFromCurrentToDrop > 1000)
                                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                    else
                                        openBarCodeScannerView(2);
                                } else {
                                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                                    if (distanceFromCurrentToDrop > 1000)
                                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                    else {
                                        if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
                                            attemptDeliveryWindow(0, 1);
                                        else
                                            attemptDeliveryWindow(0, 0);
                                   }
                                }
                            }
                        } else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Please enable GPS location to complete this delivery. If you’re having trouble, please contact support via the help button in the top right corner.");
                    } else
                        requestPermissions();
                        //DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Please enable GPS location to complete this delivery. If you’re having trouble, please contact support via the help button in the top right corner.");

                } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")
                        || activeBookingModelForSelectedItem.getStatus().equals("Accepted")) {
                    dispatchWindow();
                }
                break;
            case R.id.returnToDHLBtnABD:
                if ((activeBookingModelForSelectedItem.getStatus().equals("Accepted") && LoginZoomToU.IS_TEAM_LEADER == true)||(activeBookingModelForSelectedItem.getStatus().equals("Accepted") && !LoginZoomToU.IS_TEAM_LEADER)) {
                    if (!LoginZoomToU.IS_TEAM_LEADER) {
                        assignToOtherCourier_functionality=new AssignToOtherCourier_Functionality(this, activeBookingModelForSelectedItem.getBookingId());
                        assignToOtherCourier_functionality.dialogToShowDriversListToAllocateBooking();
                    }else
                    new AssignToOtherCourier_Functionality(ActiveBookingDetail_New.this, null, ActiveBookingDetail_New.this, false, activeBookingModelForSelectedItem.getBookingId());
                } else {
                    isForReturnToPickup = 0;
                    openBarCodeScannerView(3);
                }
                break;
            case R.id.returnToPickUpBtnABD:
                isReturnedToDHL = 0;
                if (activeBookingModelForSelectedItem.isTTDReasonForAlcoholDelivery())
                    attemptDeliveryWindow(1, 1);
                else
                    attemptDeliveryWindow(1, 0);
                break;
            case R.id.backFromBookingDetail:
                finish();
                break;
            case R.id.secondHeaderABDLayout:
                try {
                    tv_eta_msg.setVisibility(View.GONE);
                    if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                            || activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")) {
                        activeBookABD.setVisibility(View.VISIBLE);
                        if (dateValueIs != null)
                            dateValueIs = null;
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss.SSS");
                        if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                                || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted"))
                            dateValueIs = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModelForSelectedItem.getDropDateTime());
                        else
                            dateValueIs = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModelForSelectedItem.getPickupDateTime());

                        dateValueIs = dateValueIs + "T" + sdf1.format(c.getTime());
                        dateValueIs = LoginZoomToU.checkInternetwithfunctionality.getPickOrDropDateTimeFromServer(dateValueIs);
                        etaMsgText.setText(dateValueIs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.etaTimeLayoutDetail:
                selectEtaImage.setImageResource(R.drawable.icon_up);
                /** Get the current time */
                final Calendar cal = Calendar.getInstance();
                pHour = cal.get(Calendar.HOUR_OF_DAY);
                pMinute = cal.get(Calendar.MINUTE);
                showDialog(TIME_DIALOG_ID);
                break;
            case R.id.etaCancelBtn:
                isPickBtnPress = false;
                activeBookABD.setVisibility(View.GONE);
                break;
            case R.id.etaDoneBtn:
                try {
                    if ((activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Picked up")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                            || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")))
                        setBookingTimeToField(secondHeaderTxtABD, etaMsgText.getText().toString(), "Your ETA for Drop off is ", false, true);
                    else
                        setBookingTimeToField(secondHeaderTxtABD, etaMsgText.getText().toString(), "Your ETA for Pick up is ", false, true);

                    if (isPickBtnPress == false) {
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            //new UpdatePickUpETAAsyncTask().execute();
                            UpdatePickUpETAAsyncTask();
                        else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
                    } else
                        takePicture("Do you want to take photo of item to pick.");

                    activeBookABD.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(ActiveBookingDetail_New.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.pickUpHeaderLayoutABD:
                if (!isPickShowed) {
                 showPickInformation();
                } //*********** If pick or Drop notes is greater than 3 line then show read more text otherwise its hide *******
                break;
            case R.id.dropOffHeaderLayoutABD:
                if (isPickShowed) {
                  showDropInformation();
                }  //*********** If pick or Drop notes is greater than 3 line then show read more text otherwise its hide *******
                break;
            case R.id.pickDropReadMoreTxtABD:
                if (!isReadMoreTxt) {
                    pickDropReadMoreTxtABD.setText("Show less");
                    isReadMoreTxt = !isReadMoreTxt;
                    pickDropNotesABD.setMaxLines(500);
                    findViewById(R.id.bottomBtnLayoutABD).setVisibility(View.VISIBLE);
                } else {
                    pickDropNotesABD.setMaxLines(1);
                    pickDropReadMoreTxtABD.setText("Read more");
                    isReadMoreTxt = !isReadMoreTxt;
                    findViewById(R.id.bottomBtnLayoutABD).setVisibility(View.VISIBLE);
                }
                break;
            case R.id.iconCallABD:
                Intent inten = new Intent(Intent.ACTION_DIAL);
                inten.setData(Uri.parse("tel:" + pickOrDropPersonContactNoToCall));
                startActivity(inten);
                inten = null;
                break;
            case R.id.iconMapABD:
                try {
                    String address = pickOrDropPersonAddressForMap; // Get address
                    address = address.replace(' ', '+');
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
                    startActivity(geoIntent);    // Initiate lookup
                    geoIntent = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.customerDetailLayoutABD:
                showPopup();
                break;
            case R.id.atlDetailLayoutABD:
                if (courierDetailViewExpanded == 0) {
                    courierDetailViewExpanded = 2;
                    expandATLDetailABD();
                } else {
                    courierDetailViewExpanded = 0;
                    collapseATLDetailABD();
                }
                break;
            case R.id.helpBtnDetailPage:
                shareTheBookingDetailsViaWhatsApp();
                /*Bundle bModelDeliveryChat = new Bundle();
                if (LoadChatBookingArray.arrayOfChatDelivery != null) {
                    if (LoadChatBookingArray.arrayOfChatDelivery.size() > 0) {
                        bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", LoadChatBookingArray.arrayOfChatDelivery.get(0));
                        sendHelpMsgToChat(LoadChatBookingArray.arrayOfChatDelivery.get(0), bModelDeliveryChat);
                    } else {
                        bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", new Model_DeliveriesToChat(ActiveBookingDetail_New.this, LoginZoomToU.courierID, "Zoom2u-Admin"));
                        sendHelpMsgToChat(new Model_DeliveriesToChat(ActiveBookingDetail_New.this, LoginZoomToU.courierID, "Zoom2u-Admin"), bModelDeliveryChat);
                    }
                } else {
                    bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", new Model_DeliveriesToChat(ActiveBookingDetail_New.this, LoginZoomToU.courierID, "Zoom2u-Admin"));
                    sendHelpMsgToChat(new Model_DeliveriesToChat(ActiveBookingDetail_New.this, LoginZoomToU.courierID, "Zoom2u-Admin"), bModelDeliveryChat);
                }*/
                break;
            case R.id.openBarcodeBtnABD:
                Intent intent = new Intent(ActiveBookingDetail_New.this, BarcodeScanner.class);
                intent.putExtra("ScanAWBForPick", 1);
                intent.putExtra("RunType", activeBookingModelForSelectedItem.getRunType());
                startActivityForResult(intent, IS_BARCODE_FROM_ACTIVE_DETAIL);
                intent = null;
                break;
        }
    }

    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void setPickDropCompanyName(String pickDrop_company) {
        try {
            if (pickDrop_company.isEmpty() || pickDrop_company == null || pickDrop_company.equals("null"))
                ((TextView) findViewById(R.id.pickDropCompanyABD)).setText("Not available");
            else
                ((TextView) findViewById(R.id.pickDropCompanyABD)).setText(pickDrop_company);
        } catch (Exception e) {
            e.printStackTrace();
            ((TextView) findViewById(R.id.pickDropCompanyABD)).setText("Not available");
        }
    }

    private void callForAtteptDeliveryWindow(boolean doesAlcoholDeliveries) {
        if (doesAlcoholDeliveries)
            attemptDeliveryWindow(0, 1);
        else
            attemptDeliveryWindow(0, 0);
    }

    //********* Show Alert if any ID exist against customer provided idF
    private void checkDeliveryDropIdAlert() {
        try {
            if (!activeBookingModelForSelectedItem.getDropIdentityNumber().equals("") && !activeBookingModelForSelectedItem.getDropIdentityNumber().equals("null")
                    && activeBookingModelForSelectedItem.getDropIdentityNumber() != null) {
                deliveryIdCheckAlert();
            } else
                alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
        } catch (Exception e) {
            e.printStackTrace();
            alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
        }
    }

    // *********** Send Help msg to customer support and Open Admin chat **************
    private void sendHelpMsgToChat(Model_DeliveriesToChat model_deliveriesToChat, final Bundle bModelDeliveryChat) {

        int unreadMessageCountForCustomer = model_deliveriesToChat.getUnreadMsgCountOfCourier();
        unreadMessageCountForCustomer++;
        String helpMsgToAdminStr = "Driver needs your help with the Booking<br />" +
                "<a target='_blank' href='" + ChatViewBookingScreen.BOOKING_DETAIL_LINK + activeBookingModelForSelectedItem.getBookingId() + "'>#" + activeBookingModelForSelectedItem.getBookingRefNo() + "</a>";
        String currentDateTime = ChatDetailActivity.sendTimeToServer();
        Chat chat = new Chat(helpMsgToAdminStr, "courier", 0, currentDateTime, "");
        mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_MESSAGE_CHAT + LoginZoomToU.courierID + "/message").push().setValue(chat);
        mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS + LoginZoomToU.courierID + "/status/courier/unread").setValue(unreadMessageCountForCustomer);
        chat = null;

        try {
            if (progressInABDView == null)
                progressInABDView = new ProgressDialog(ActiveBookingDetail_New.this);
            Custom_ProgressDialogBar.inItProgressBar(progressInABDView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (progressInABDView != null)
                    if (progressInABDView.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressInABDView);
                Intent chatDetailViewIntent = new Intent(ActiveBookingDetail_New.this, ChatDetailActivity.class);
                chatDetailViewIntent.putExtras(bModelDeliveryChat);
                startActivity(chatDetailViewIntent);
                chatDetailViewIntent = null;
            }
        }, 1000);
    }

    /*
     ************ Pickup, On route and Dropoff button functionality ***********
     */
    public void pickAndDropBtnFunctionality() {
        try {
            if (uploadDateStr != null)
                uploadDateStr = null;
            if (activeBookingModelForSelectedItem.getSource().equals("Temando")
                    || activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                if (activeBookingModelForSelectedItem.getSource().equals("Temando")) {
                    if (activeBookingModelForSelectedItem.getStatus().equals("Accepted")) {
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            //new PackageUploadWithETA_DetailViewAsyncTask().execute("NormalDeliveries");
                            PackageUploadWithETA_DetailViewAsyncTask("NormalDeliveries");
                        else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
                    } else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up")) {
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                            //new RequestToDropOffTemendo_AND_MenulogAsyncTask().execute();
                            RequestToDropOffTemendo_AND_MenulogAsyncTask();
                        else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
                    }
                } else {
                    if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff") || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                            || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")) {
//                       if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
//                           new DropAlcoholDelivery_Alerts(ActiveBookingDetail_New.this, activeBookingModelForSelectedItem);
//                       else
//                           openBarCodeScannerView(4);
                        openBarCodeScannerView(4);
                    } else
                        callOnRouteTask();
                }
            } else if (activeBookingModelForSelectedItem.getStatus().equals("Accepted")
                    || activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")
                    || activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                    || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                    || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")) {

                if (WebserviceHandler.isRoutific == true) {
                    if (activeBookingModelForSelectedItem.getStatus().equals("Accepted") ||
                            activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")) {
                        if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT")) {
                            BarcodeController.scanned_piece_to_pickup = "";
                            openBarCodeScannerView(7);
                        } else
                            takePicture("Do you want to take photo of item to pick.");
                    } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                            || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")) {
                        //                        if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
//                            new DropAlcoholDelivery_Alerts(ActiveBookingDetail_New.this, activeBookingModelForSelectedItem);
//                        else
//                            processToDropNonDHLAndNormalDelivery ();

                        if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT"))
                            openBarCodeScannerView(4);
                        else if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
                            new DropAlcoholDelivery_Alerts(ActiveBookingDetail_New.this, activeBookingModelForSelectedItem);
                        else
                            processToDropNonDHLAndNormalDelivery();
                    }
                } else {
                    if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")) {
                        isPickBtnPress = true;
                        etaTitleText.setText("Update your ETA, and notify the drop off.");
                        activeBookABD.setVisibility(View.VISIBLE);
                    } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                            || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")) {

//                        if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
//                            new DropAlcoholDelivery_Alerts(ActiveBookingDetail_New.this, activeBookingModelForSelectedItem);
//                        else
//                            processToDropNonDHLAndNormalDelivery ();

                        if (activeBookingModelForSelectedItem.getRunType().equals("SMARTSORT"))
                            openBarCodeScannerView(4);
                        else if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
                            new DropAlcoholDelivery_Alerts(ActiveBookingDetail_New.this, activeBookingModelForSelectedItem);
                        else
                            processToDropNonDHLAndNormalDelivery();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************* Process to drop after Drop btn click **********
    public void processToDropNonDHLAndNormalDelivery() {
        if (!activeBookingModelForSelectedItem.getDropIdentityNumber().equals("")
                && activeBookingModelForSelectedItem.getPick_ContactName().equalsIgnoreCase("Telstra"))
            takePicture("Please take a photo of the item to be delivered.\nNote:\nDO NOT photograph the recipient, or the ID");
        else
            takePicture("Please take a photo of the parcel you are delivering. Please note this should be a picture of the parcel only.");
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
                            if(BookingDetail_New.gMapBookingDetail!=null)
                            BookingDetail_New.gMapBookingDetail.addMarker(markerOptions);

                        }
                    }
                });
            }
        }
    }


    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }


    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    //******** Initialize active booking detail view map ***************
    private void inItActiveBookingDetailMapFragment() {
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
                //   BookingDetail_New.gMapBookingDetail.setMyLocationEnabled(true);

                getLastLocation();

                try {
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSX()),
                                    Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSY())))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin_new)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
                            .position(new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSX()),
                                    Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSY())))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin_new)));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                BookingDetail_New.gMapBookingDetail.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        try {
                            LatLngBounds bounds = new LatLngBounds.Builder()
                                    .include(new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSX()),
                                            Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSY())))
                                    .include(new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSX()),
                                            Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSY())))
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
                        activeDetailScrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });

//        new ShowPolyline_DirectionAPI(ActiveBookingDetail_New.this, BookingDetail_New.gMapBookingDetail,
//                new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSX()),
//                        Double.parseDouble((String) activeBookingModelForSelectedItem.getPick_GPSY())),
//                new LatLng(Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSX()),
//                        Double.parseDouble((String) activeBookingModelForSelectedItem.getDrop_GPSY())));

                List<LatLng> list = null;
                try {
                    list = PolyUtil.decode(activeBookingModelForSelectedItem.getRoutePolyline());
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

    //********* In-it ETA dialog view on manually updating ETA **********
    void inItETADialogViewOnETAUpdate() {
        if (activeBookABD == null)
            activeBookABD = (RelativeLayout) findViewById(R.id.activeBookABD);
        activeBookABD.setVisibility(View.GONE);
        if (etaUpdateLayout == null)
            etaUpdateLayout = (RelativeLayout) findViewById(R.id.etaTimeLayoutDetail);
        etaUpdateLayout.setOnClickListener(this);
        if (selectEtaImage == null)
            selectEtaImage = (ImageView) findViewById(R.id.selectPickUpEta);
        if (etaTitleText == null)
            etaTitleText = (TextView) findViewById(R.id.etaTitleTextBG);

        if (isPickBtn)
            etaTitleText.setText("Update your ETA, and notify the drop off");
        else
            etaTitleText.setText("Please enter in your arrival time to pickup the goods.");

        if (etaMsgText != null)
            etaMsgText = null;
        etaMsgText = (TextView) findViewById(R.id.etaMsgTextBGActiveBooking);


        findViewById(R.id.etaCancelBtn).setOnClickListener(this);
        findViewById(R.id.etaDoneBtn).setOnClickListener(this);

        //   Update on 03-11-2014
        if ((activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                || activeBookingModelForSelectedItem.getStatus().equals("Picked up")
                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted"))) {
            if (!activeBookingModelForSelectedItem.getDropETA().equals("") && !activeBookingModelForSelectedItem.getDropETA().equals("null"))
                setDefaultETATOField(activeBookingModelForSelectedItem.getDropETA(), "Your ETA for Drop off is ");
            else
                setDefaultETATOField(activeBookingModelForSelectedItem.getDropDateTime(), "Your ETA for Drop off is ");
        } else {
            if (!activeBookingModelForSelectedItem.getPickupETA().equals("") && !activeBookingModelForSelectedItem.getPickupETA().equals("null"))
                setDefaultETATOField(activeBookingModelForSelectedItem.getPickupETA(), "Your ETA for Pick up is ");
            else
                setDefaultETATOField(activeBookingModelForSelectedItem.getPickupDateTime(), "Your ETA for Pick up is ");
        }
    }

    /*
     ********* Set default ETA to field *********
     */
    private void setDefaultETATOField(String dropDateTime, String msgStr) {
        uploadDateStr = dropDateTime;
        dateValueIs = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(uploadDateStr);
        setBookingTimeToField(secondHeaderTxtABD, dateValueIs, msgStr, false, true);
    }

    //************ Show progress dialog for active booking detail
    private void showProgressInABDView() {
        try {
            if (progressInABDView != null)
                if (progressInABDView.isShowing())
                    progressInABDView.dismiss();
            progressInABDView = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (progressInABDView == null)
                progressInABDView = new ProgressDialog(ActiveBookingDetail_New.this);
            Custom_ProgressDialogBar.inItProgressBar(progressInABDView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //**************** Load active booking detail view content from Route view **************
    private void activeDetailByBookingIdFromRouteView() {
        if (getIntent().getIntExtra("RouteViewCalling", 0) == 181)
            ConfirmPickUpForUserName.isDropOffSuccessfull = 12;

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new GetActiveBookingDetailByBookingID().execute();
            GetActiveBookingDetailByBookingID();
        else
            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            Intent i = getIntent();
            itemPositionInActiveDetail = i.getIntExtra("positionActiveBooking", 0);
            bookingIdActiveBooking = String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId());
            i = null;
            //	SlideMenuZoom2u.setCourierToOfflineFromChat();
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putBoolean("Routific", WebserviceHandler.isRoutific);
            outState.putInt("ITEMPOSITION", itemPositionInActiveDetail);
            outState.putString("BOOKINGIDACTIVEDETAIL", bookingIdActiveBooking);
            outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
            outState.putParcelableArrayList("ACTIVEBOOKINGARRAy", BookingView.bookingListArray);
            outState.putParcelable("ActiveBookingDetailModel", activeBookingModelForSelectedItem);

            if (routeViewCallingIntent == 181 && bookingIdToLoadFromRouteView != 0) {
                outState.putInt("RouteViewCalling", routeViewCallingIntent);
                outState.putInt("bookingIDToLoad", bookingIdToLoadFromRouteView);
            } else {
                outState.putInt("RouteViewCalling", 0);
                outState.putInt("bookingIDToLoad", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            if (savedInstanceState != null)
                reStoreActivityItems(savedInstanceState);
            if (LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(ActiveBookingDetail_New.this);

            inItABDUIContents();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************ Restore content of this activity when app killed in background ***************//
    private void reStoreActivityItems(Bundle savedInstanceState) {
        routeViewCallingIntent = savedInstanceState.getInt("RouteViewCalling");
        bookingIdToLoadFromRouteView = savedInstanceState.getInt("bookingIDToLoad");
        WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
        ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
        itemPositionInActiveDetail = savedInstanceState.getInt("ITEMPOSITION");
        bookingIdActiveBooking = savedInstanceState.getString("BOOKINGIDACTIVEDETAIL");
        BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
        if (activeBookingModelForSelectedItem != null)
            activeBookingModelForSelectedItem = null;
        activeBookingModelForSelectedItem = savedInstanceState.getParcelable("ActiveBookingDetailModel");
        if (BookingView.bookingListArray != null)
            BookingView.bookingListArray = null;
        BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("ACTIVEBOOKINGARRAy");
        Functional_Utility.removeLocationTimer();
        Intent serviceIntentForLocation = new Intent(ActiveBookingDetail_New.this, ServiceForSendLatLong.class);
        startService(serviceIntentForLocation);
        serviceIntentForLocation = null;
    }

    //************** open barcode scanner view to read AWB number *************/
    public void openBarCodeScannerView(int ScannedAWB) {
        Intent intent = new Intent(ActiveBookingDetail_New.this, BarcodeScanner.class);
        intent.putExtra("ScanAWBForPick", ScannedAWB);
        intent.putExtra("AWB_NUMBER", activeBookingModelForSelectedItem.getOrderNumber());
        intent.putExtra("RunType", activeBookingModelForSelectedItem.getRunType());
        intent.putStringArrayListExtra("PIECE_ARRAY", activeBookingModelForSelectedItem.getPiecesArray());

        if (activeBookingModelForSelectedItem.getPiecesArray().size() > 1) {
            if (!activeBookingModelForSelectedItem.getStatus().equals("Accepted") && !activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup")
                    && !activeBookingModelForSelectedItem.getStatus().equals("Picked up")) {
                inItScannedPieceMap();
                scannedPieceMap.putAll(activeBookingModelForSelectedItem.getPiecesScannedMap());
            }
        }

        startActivity(intent);
        intent = null;
    }

    Dialog atlDialog;
    private View bgBlurDefaultView;

    // *************  Alert for search DHL bookings **************
    public void atlDialogAlert() {
        dismissATLDialog();
        isDropOffFromATL = false;
        if (atlDialog != null)
            atlDialog = null;
        atlDialog = new Dialog(ActiveBookingDetail_New.this);
        atlDialog.setCancelable(true);
        atlDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        atlDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        atlDialog.setContentView(R.layout.dialog_safe_delivery_avail_atl);

        Window window = atlDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        bgBlurDefaultView = atlDialog.findViewById(R.id.bgBlurDefaultView);
        bgBlurDefaultView.setVisibility(View.GONE);

        TextView safeDelDialogTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogTitleTxt);


        TextView safeDelDialogMsgTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogMsgTxt);


        TextView safeDelDialogLocationTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogLocationTitleTxt);

        TextView safeDelDialogLocationValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogLocationValueTxt);

        if (!activeBookingModelForSelectedItem.getATLLeaveAt().equals("")
                && !activeBookingModelForSelectedItem.getATLLeaveAt().equals("null")
                && activeBookingModelForSelectedItem.getATLLeaveAt() != null)
            safeDelDialogLocationValueTxt.setText(activeBookingModelForSelectedItem.getATLLeaveAt());
        else
            safeDelDialogLocationValueTxt.setText("-NA-");

        TextView safeDelDialogDoorCodeTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogDoorCodeTitleTxt);

        TextView safeDelDialogDoorCodeValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogDoorCodeValueTxt);

        if (!activeBookingModelForSelectedItem.getATLDoorCode().equals("")
                && !activeBookingModelForSelectedItem.getATLDoorCode().equals("null")
                && activeBookingModelForSelectedItem.getATLDoorCode() != null)
            safeDelDialogDoorCodeValueTxt.setText(activeBookingModelForSelectedItem.getATLDoorCode());
        else
            safeDelDialogDoorCodeValueTxt.setText("-NA-");

        TextView safeDelDialogRecipientNameTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogRecipientNameTitleTxt);

        TextView safeDelDialogRecipientValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogRecipientValueTxt);

        if (!activeBookingModelForSelectedItem.getATLReceiverName().equals("")
                && !activeBookingModelForSelectedItem.getATLReceiverName().equals("null")
                && activeBookingModelForSelectedItem.getATLReceiverName() != null)
            safeDelDialogRecipientValueTxt.setText(activeBookingModelForSelectedItem.getATLReceiverName());
        else
            safeDelDialogRecipientValueTxt.setText("-NA-");

        TextView safeDelDialogInstructionTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogInstructionTitleTxt);

        TextView safeDelDialogInstructionValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogInstructionValueTxt);

        if (!activeBookingModelForSelectedItem.getATLInstructions().equals("")
                && !activeBookingModelForSelectedItem.getATLInstructions().equals("null")
                && activeBookingModelForSelectedItem.getATLInstructions() != null)
            safeDelDialogInstructionValueTxt.setText(activeBookingModelForSelectedItem.getATLInstructions());
        else
            safeDelDialogInstructionValueTxt.setText("-NA-");

        Button safeDelDialogSafeLocationBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogSafeLocationBtn);

        safeDelDialogSafeLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWhatIsSafe();
            }
        });

        Button safeDelDialogDeliveryAttemptBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogDeliveryAttemptBtn);

        safeDelDialogDeliveryAttemptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissATLDialog();
                isDropOffFromATL = false;
                if (activeBookingModelForSelectedItem.getPick_ContactName().equalsIgnoreCase("Telstra"))
                    attemptDeliveryWindow(0, 0);
                else {
                    int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(activeBookingModelForSelectedItem.getDrop_GPSX(),
                            activeBookingModelForSelectedItem.getDrop_GPSY());
                    if (distanceFromCurrentToDrop <= 1000) {
                        if (activeBookingModelForSelectedItem.isDoesAlcoholDeliveries())
                            attemptDeliveryWindow(0, 1);
                        else
                            attemptDeliveryWindow(0, 0);
                    } else
                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                }
            }
        });

        Button safeDelDialogCancelBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogCancelBtn);

        safeDelDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissATLDialog();
                isDropOffFromATL = false;
            }
        });

        atlDialog.show();
    }

    private void dismissATLDialog() {
        try {
            if (atlDialog != null)
                if (atlDialog.isShowing())
                    atlDialog.dismiss();
            atlDialog = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialogWhatIsSafe() {
        if (bgBlurDefaultView != null)
            bgBlurDefaultView.setVisibility(View.VISIBLE);
        try {
            if (alertDialog != null)
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialog != null)
            alertDialog = null;
        alertDialog = new Dialog(ActiveBookingDetail_New.this);
        alertDialog.setCancelable(true);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(R.layout.dialog_what_safe_for_atl);

        Window window = alertDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView titleTxtForWhatIsSafe = (TextView) alertDialog.findViewById(R.id.titleTxtForWhatIsSafe);

        final CheckBox checkBoxForWhatIsSafe = (CheckBox) alertDialog.findViewById(R.id.checkBoxForWhatIsSafe);


        alertDialog.findViewById(R.id.firstPointForWhatIsSafe);
        alertDialog.findViewById(R.id.secondPointForWhatIsSafe);
        alertDialog.findViewById(R.id.finalPointForWhatIsSafe);

        Button atlDropOffBtnWhatIsSafe = (Button) alertDialog.findViewById(R.id.atlDropOffBtnWhatIsSafe);

        atlDropOffBtnWhatIsSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxForWhatIsSafe.isChecked()) {
                    if (bgBlurDefaultView != null)
                        bgBlurDefaultView.setVisibility(View.GONE);
                    isDropOffFromATL = true;
                    alertDialog.dismiss();
                    dismissATLDialog();
                    takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                } else
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Alert!", "Can you make sure Safe location option is ticked?");
            }
        });

        Button cancelBtnWhatIsSafe = (Button) alertDialog.findViewById(R.id.cancelBtnWhatIsSafe);

        cancelBtnWhatIsSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (bgBlurDefaultView != null)
                    bgBlurDefaultView.setVisibility(View.GONE);
                isDropOffFromATL = false;
            }
        });

        alertDialog.show();
    }

    /******************  Dispatched to other courier window  ******/
    public void attemptDeliveryWindow(final int isForReturnToPickup, int ttdForAlcoholeDelivery) {
        /*
        isForReturnToPickup = 1      //****** When Returned to picked up button is clicked
        isForReturnToPickup = 0      //****** When Tried to deliver button is clicked
         */

        try {
            if (attemptDeliveryDialog != null)
                if (attemptDeliveryDialog.isShowing())
                    attemptDeliveryDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        attemptDeliveryStr = "";
        if (attemptDeliveryDialog != null)
            attemptDeliveryDialog = null;
        attemptDeliveryDialog = new Dialog(ActiveBookingDetail_New.this);
        attemptDeliveryDialog.setCancelable(true);
        attemptDeliveryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
        attemptDeliveryDialog.setContentView(R.layout.attemptdelivery_window);

        Window window = attemptDeliveryDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER | Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView attemptDelievryTitleTxt = (TextView) attemptDeliveryDialog.findViewById(R.id.attemptDelievryTitleTxt);
        TextView textReasonForLate = (TextView) attemptDeliveryDialog.findViewById(R.id.textReasonForLate);
        textReasonForLate.setVisibility(View.GONE);
        Spinner spinnerForTriedToDeliver = (Spinner) attemptDeliveryDialog.findViewById(R.id.spinnerForTriedToDeliver);
        final EditText attempDeliveryMsgText = (EditText) attemptDeliveryDialog.findViewById(R.id.attempDeliveryMsgText);


        ArrayList<String> triedToDeliverReason = new ArrayList<String>();

        if (isForReturnToPickup == 1) {
            attemptDelievryTitleTxt.setText("Returned to pickup!");

            attemptDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt).setVisibility(View.GONE);

            triedToDeliverReason.add("AD - Incorrect Address");
            triedToDeliverReason.add("OR - Order Refused");
            triedToDeliverReason.add("UA - Unable to Access");
            triedToDeliverReason.add("RM - Reciever non match");
            triedToDeliverReason.add("RW - Recieved job W implausible time window");
            if (ttdForAlcoholeDelivery > 0) {
                triedToDeliverReason.add("PA - Person is under the age of 18");
                triedToDeliverReason.add("PI - Person is intoxicated");
            }

            attempDeliveryMsgText.setHint("Please enter notes for your return");
        } else {
            attemptDelievryTitleTxt.setText("Attempt delivery!");

            if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                attemptDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt);
                attemptDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt).setVisibility(View.VISIBLE);
            } else
                attemptDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt).setVisibility(View.GONE);

            triedToDeliverReason.add("NH – Not Home");
            triedToDeliverReason.add("RD – Customer Refused Delivery");
            triedToDeliverReason.add("CA – Closed on arrival (Business)");
            triedToDeliverReason.add("ND – Parcel Not Delivered / Other");
            if (ttdForAlcoholeDelivery > 0) {
                triedToDeliverReason.add("PA - Person is under the age of 18");
                triedToDeliverReason.add("PI - Person is intoxicated");
            }

            attempDeliveryMsgText.setHint("Please enter any additional notes around why the delivery failed.");
        }

        ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(ActiveBookingDetail_New.this, R.layout.spinneritemxml_white1, triedToDeliverReason) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#808080"));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(Color.parseColor("#808080"));
                return v;
            }
        };

        spinnerForTriedToDeliver.setAdapter(adapter_Position);
        adapter_Position = null;

        spinnerForTriedToDeliver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                returnToPickupReasonFromDropDown = position;        //************ For return to pickup delivery
                positionForTriedToDeliverReason = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int setAdapterPOsition;
        if (ttdForAlcoholeDelivery == 2) {
            setAdapterPOsition = triedToDeliverReason.size() - 2;
            returnToPickupReasonFromDropDown = setAdapterPOsition;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason.get(setAdapterPOsition);
            spinnerForTriedToDeliver.setSelection(setAdapterPOsition);
        } else if (ttdForAlcoholeDelivery == 3) {
            setAdapterPOsition = triedToDeliverReason.size() - 1;
            returnToPickupReasonFromDropDown = setAdapterPOsition;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason.get(setAdapterPOsition);
            spinnerForTriedToDeliver.setSelection(setAdapterPOsition);
        } else {
            setAdapterPOsition = 0;
            returnToPickupReasonFromDropDown = setAdapterPOsition;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason.get(setAdapterPOsition);
        }

        triedToDeliverReason = null;
        Button attemptDeliveryCancelBtn = (Button) attemptDeliveryDialog.findViewById(R.id.attemptDeliveryCancelBtn);

        attemptDeliveryCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptDeliveryDialog.dismiss();
                LoginZoomToU.imm.hideSoftInputFromWindow(attempDeliveryMsgText.getWindowToken(), 0);
            }
        });
        Button attemptDeliverySaveBtn = (Button) attemptDeliveryDialog.findViewById(R.id.attemptDeliverySaveBtn);

        attemptDeliverySaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginZoomToU.imm.hideSoftInputFromWindow(attempDeliveryMsgText.getWindowToken(), 0);
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                    if (isForReturnToPickup == 1) {
                        attemptDeliveryStr = attempDeliveryMsgText.getText().toString();
                        if (ttdForAlcoholeDelivery == 1)
                            //new RequestForReturnToPickUp().execute();
                            RequestForReturnToPickUp();
                        else {
                            ActiveBookingDetail_New.this.isForReturnToPickup = isForReturnToPickup;
                            takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                        }
                        attemptDeliveryDialog.dismiss();
                    } else {
                        attemptDeliveryStr = attempDeliveryMsgText.getText().toString();
                        if (returnToPickupReasonFromDropDown != 3) {
                            //new AttemptOrDispatchToCourierAsyncTask().execute();
                            AttemptOrDispatchToCourierAsyncTask();
                            attemptDeliveryDialog.dismiss();
                        } else {
                            if (!attempDeliveryMsgText.getText().toString().equals("")) {
                                //new AttemptOrDispatchToCourierAsyncTask().execute();
                                AttemptOrDispatchToCourierAsyncTask();
                                attemptDeliveryDialog.dismiss();
                            } else
                                DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Alert!", "Please enter notes");
                        }
                    }
                } else
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
            }
        });

        attemptDeliveryDialog.show();
    }

    void dispatchWindow() {
        if (DialogActivity.enterFieldDialog != null)
            DialogActivity.enterFieldDialog = null;
        DialogActivity.enterFieldDialog = new Dialog(ActiveBookingDetail_New.this);
        DialogActivity.enterFieldDialog.setCancelable(true);
        DialogActivity.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogActivity.enterFieldDialog.setContentView(R.layout.logoutwindow);

        Window window = DialogActivity.enterFieldDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView dispatchDialogFieldMsg = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowMessageText);

        dispatchDialogFieldMsg.setText("Are you sure, you want to request for a new driver for this booking?");

        Button dispatchDialogFieldCancelBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowCancelBtn);

        dispatchDialogFieldCancelBtn.setText("No");
        dispatchDialogFieldCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity.enterFieldDialog.dismiss();

            }
        });
        Button dispatchDialogFieldLogoutBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowLogoutBtn);

        dispatchDialogFieldLogoutBtn.setText("Yes");
        dispatchDialogFieldLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity.enterFieldDialog.dismiss();
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    //new DispatchToOtherCourierAsyncTask().execute();
                    DispatchToOtherCourierAsyncTask();
                else
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network!", "No network connection, Please try again later.");
            }
        });

        DialogActivity.enterFieldDialog.show();
    }

    private void DispatchToOtherCourierAsyncTask(){
        final String[] requestDispatchToOtherCourierMsgStr = {""};
        final boolean[] responseDataRequestDrop = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (progressInActiveDetailView != null)
                        progressInActiveDetailView = null;
                    progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    try {
                        String requestDropResponseStr = webServiceHandler.dispatchToOtherCourier((Integer) activeBookingModelForSelectedItem.getBookingId());
                        JSONObject jOBJForRequestPick = new JSONObject(requestDropResponseStr);
                        responseDataRequestDrop[0] = jOBJForRequestPick.getBoolean("success");
                        try {
                            requestDispatchToOtherCourierMsgStr[0] = jOBJForRequestPick.getString("message");
                        } catch (Exception e) {
                            requestDispatchToOtherCourierMsgStr[0] = "";
                        }
                        jOBJForRequestPick = null;
                        webServiceHandler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (requestDispatchToOtherCourierMsgStr[0].equals("")) {
                        if (responseDataRequestDrop[0] == true) {
                            Intent bookingCountService = new Intent(ActiveBookingDetail_New.this, ServiceForCourierBookingCount.class);
                            bookingCountService.putExtra("Is_API_Call_Require", 2);
                            startService(bookingCountService);
                            bookingCountService = null;

                            new LoadChatBookingArray(ActiveBookingDetail_New.this, 0);
                            dialogInActiveDetail(ActiveBookingDetail_New.this, "Sent!", "Booking sent to the other drivers successfully.");
                        } else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Sorry!", "Booking can't be sent to the other drivers, Please try again");
                    } else
                        dialogInActiveDetail(ActiveBookingDetail_New.this, "Alert!", requestDispatchToOtherCourierMsgStr[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Sorry!", "Something went wrong, Please try again later");
                }
            }
        }.execute();
    }


    /**
     * Callback received when the user "picks" a time in the dialog
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
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

                    strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ? "12" : datetime.get(Calendar.HOUR) + "";
                    strHrsToShow = strHrsToShow + ":" + datetime.get(Calendar.MINUTE) + " " + am_pm;
                    updateDisplay();
                }
            };

    public void takePicture(String alertMsg) {

        Dialog enterFieldDialog = new Dialog(this);
        enterFieldDialog.setCancelable(true);
        enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        enterFieldDialog.setContentView(R.layout.dialogview_take_photo);

        Window window = enterFieldDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        TextView dialogMessageText = enterFieldDialog.findViewById(R.id.dialogMessageText);
        Button take_photoBtn = enterFieldDialog.findViewById(R.id.take_photoBtn);

        dialogMessageText.setText(alertMsg);


        // On pressing Settings button
        take_photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeBookingModelForSelectedItem.getSource().equals("DHL")) {
                    if (isReturnedToDHL == 1 || isForReturnToPickup == 1)
                        selectImage();
                    else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                            || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
                        int screenCount = 1;
                        if (activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver") && activeBookingModelForSelectedItem.isATL())
                            screenCount = 3;
                        else if (activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver") && !activeBookingModelForSelectedItem.isATL())
                            screenCount = 2;
                        else
                            screenCount = 1;

                        Intent i = new Intent(ActiveBookingDetail_New.this, CameraOverlay_Activity.class);
                        i.putExtra("ScreenCount", screenCount);
                        startActivity(i);
                        i = null;
                    } else
                        selectImage();
                } else
                    selectImage();
                enterFieldDialog.cancel();
            }
        });

        if (activeBookingModelForSelectedItem.getPick_ContactName().equalsIgnoreCase("Telstra"))
            showSkipBtnOnPackageImage(enterFieldDialog);
        else if (!activeBookingModelForSelectedItem.getSource().equals("DHL")
                && activeBookingModelForSelectedItem.getIsCakeAndFlower() == 0)
            showSkipBtnOnPackageImage(enterFieldDialog);

        enterFieldDialog.show();
    }

    //************ Show Skip button while taking package image **************
    private void showSkipBtnOnPackageImage(Dialog alertDialog) {
        if (!activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                && !activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")) {
            if (isForReturnToPickup != 1) {         //******** Hide skip button when attempt for Returned to pickup
                alertDialog.findViewById(R.id.skipBtn).setVisibility(View.VISIBLE);
                alertDialog.findViewById(R.id.skipBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ActiveBookingView.photo != null)
                            ActiveBookingView.photo.recycle();
                        isPickBtnPress = false;
                        alertDialog.cancel();
                        PushReceiver.isCameraOpen = true;
                        Intent i = new Intent(ActiveBookingDetail_New.this, ConfirmPickUpForUserName.class);
                        if (isDropOffFromATL) {
                            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                                DropOffFromATLAsyncTask();
                            else
                                DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
                        } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                                || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted"))
                            i.putExtra("userName", activeBookingModelForSelectedItem.getDrop_ContactName());
                        else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                            i.putExtra("userName", activeBookingModelForSelectedItem.getPick_ContactName());

                        i.putExtra("RouteViewCalling", routeViewCallingIntent);
                        i.putExtra("dropOffEta", uploadDateStr);
                        i.putExtra("IsReturnedToDHL", isReturnedToDHL);
                        i.putExtra("ActiveBookingModel", activeBookingModelForSelectedItem);
                        isReturnedToDHL = 0;
                        startActivity(i);
                        i = null;
                    }
                });

               /* alertDialog.setNegativeButton("Skip", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (ActiveBookingView.photo != null)
                            ActiveBookingView.photo.recycle();
                        isPickBtnPress = false;
                        dialog.cancel();
                        PushReceiver.isCameraOpen = true;
                        Intent i = new Intent(ActiveBookingDetail_New.this, ConfirmPickUpForUserName.class);
                        if (isDropOffFromATL) {
                            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                               // new DropOffFromATLAsyncTask().execute();
                                DropOffFromATLAsyncTask();
                            else
                                DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No Network !", "No Network connection, Please try again later.");
                        } else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                                || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted"))
                            i.putExtra("userName", activeBookingModelForSelectedItem.getDrop_ContactName());
                        else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
                            i.putExtra("userName", activeBookingModelForSelectedItem.getPick_ContactName());

                        i.putExtra("RouteViewCalling", routeViewCallingIntent);
                        i.putExtra("dropOffEta", uploadDateStr);
                        i.putExtra("IsReturnedToDHL", isReturnedToDHL);
                        i.putExtra("ActiveBookingModel", activeBookingModelForSelectedItem);
                        isReturnedToDHL = 0;
                        startActivity(i);
                        i = null;
                    }
                });*/
            } else alertDialog.findViewById(R.id.skipBtn).setVisibility(View.GONE);
        }
    }

    /**
     * Updates the time in the TextView
     */
    private void updateDisplay() {
        selectEtaImage.setImageResource(R.drawable.icon_down);

        if (isPickBtn)
            dateStr = LoginZoomToU.checkInternetwithfunctionality.returnDateFromServer("" + activeBookingModelForSelectedItem.getPickupDateTime());
        else
            dateStr = LoginZoomToU.checkInternetwithfunctionality.returnDateFromServer("" + activeBookingModelForSelectedItem.getDropDateTime());

        dateTimeStr = dateStr + " " + strHrsToShow;
        Date convertedDate = LoginZoomToU.checkInternetwithfunctionality.returnDateTimeForConversion(dateTimeStr);

        if (uploadDateStr != null)
            uploadDateStr = null;
        uploadDateStr = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
        convertedDate = null;
        dateValueIs = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(uploadDateStr);
        etaMsgText.setText(dateValueIs);
    }

    /**
     * Create a new dialog for time picker
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, pHour, pMinute, false);
        }
        return null;
    }

    @Override
    public void onBackPressed() {
//        if (routeViewCallingIntent == 181) {
//            BookingView.bookingViewSelection = 1;
//            ConfirmPickUpForUserName.isDropOffSuccessfull = 12;
//        }else {
//            BookingView.bookingViewSelection = 2;
//            ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
//        }
//        Intent i = new Intent(ActiveBookingDetail_New.this, SlideMenuZoom2u.class);
//        startActivity(i);
//        i = null;
        finish();
//        overridePendingTransition(R.anim.left_in, R.anim.right_out);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.booking_detail, menu);
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        openCamera();
                    } else
                        Toast.makeText(ActiveBookingDetail_New.this, "Permission Denied", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    //Log.d("ijhrjhri", "onRequestPermissionsResult: ");
                   // Toast.makeText(ActiveBookingDetail_New.this, "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLastLocation();
                } else {

                    DialogActivity.alertDialogView(this, "Alert!", "This option is unavailable while location services are turned off or restricted.\n" +
                            "Please go to your phone setting to turn on your location services and ensure that the Zoom2u app has permissions enabled.");
                }
                break;

        }

    }

    private void selectImage() {
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(ActiveBookingDetail_New.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(ActiveBookingDetail_New.this, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(ActiveBookingDetail_New.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(ActiveBookingDetail_New.this);

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

                    enterFieldDialogMsg.setText("Z2U for couriers app need to access your images for picture post.");

                    Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                    enterFieldDialogDoneBtn.setText("Got it!");

                    enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(ActiveBookingDetail_New.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();

                } else
                    openCamera();
            } else
                openCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        try {
            PushReceiver.isCameraOpen = true;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                    LoginZoomToU.isImgFromInternalStorage = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LoginZoomToU.isImgFromInternalStorage = true;
                    Toast.makeText(ActiveBookingDetail_New.this, "Image file at internal", Toast.LENGTH_LONG).show();
                }
                if (LoginZoomToU.isImgFromInternalStorage) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyContentProviderAtLocal.CONTENT_URI);
                    startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                } else {
                    //    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    Uri photoURI = FileProvider.getUriForFile(ActiveBookingDetail_New.this,
                            getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ActiveBookingDetail_New.this, "Error while opening camera", Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            PushReceiver.isCameraOpen = false;
            isPickBtnPress = false;
            if (requestCode == TAKE_PHOTO && resultCode == RESULT_OK) {
                /*************************  2nd from android developer ******************/
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                if (LoginZoomToU.isImgFromInternalStorage) {
                    File out = new File(getFilesDir(), "packageImage.png");
                    if (!out.exists())
                        Toast.makeText(ActiveBookingDetail_New.this, "Error while capturing image", Toast.LENGTH_LONG).show();
                    else {
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;
                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;
                        Functional_Utility.mCurrentPhotoPath = out.getAbsolutePath();
                        ActiveBookingView.photo = BitmapFactory.decodeFile(out.getAbsolutePath(), bmOptions);
                    }
                } else {
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;
                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    ActiveBookingView.photo = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                }

                //    ActiveBookingView.photo = Functional_Utility.getRotatedCameraImg(Functional_Utility.mCurrentPhotoPath, ActiveBookingView.photo);
                pickAndDropBookingAfterTakePkgPhoto();  //************* Pick and Drop booking after take photo
            } else if (requestCode == 1227 && IS_BARCODE_FROM_ACTIVE_DETAIL == 1228 && resultCode == RESULT_OK) {
                IS_BARCODE_FROM_ACTIVE_DETAIL = 1227;
                setResult(Activity.RESULT_OK);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callConfirmPIckDropBookingView() {
        PushReceiver.isCameraOpen = true;
        Intent i = new Intent(ActiveBookingDetail_New.this, ConfirmPickUpForUserName.class);
        if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted"))
            i.putExtra("userName", activeBookingModelForSelectedItem.getDrop_ContactName());
        else if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Pickup"))
            i.putExtra("userName", activeBookingModelForSelectedItem.getPick_ContactName());
        i.putExtra("RouteViewCalling", routeViewCallingIntent);
        //      i.putExtra("notes", activeBookingModelForSelectedItem.getNotes());
        i.putExtra("dropOffEta", uploadDateStr);
        i.putExtra("cameraPic", TAKE_PHOTO);
        i.putExtra("IsReturnedToDHL", isReturnedToDHL);
        i.putStringArrayListExtra("PieceArray", Functional_Utility.getScannedPieceArray(activeBookingModelForSelectedItem.getPiecesArray()));
        i.putExtra("ActiveBookingModel", activeBookingModelForSelectedItem);

        i.putExtra("IsForReturnToPickup", isForReturnToPickup);
        if (isForReturnToPickup == 1) {
            //************* Add data string for Returned to pickup
            i.putExtra("ReasonFor_ReturnedToPickup", attemptDeliveryStr);
            i.putExtra("DropDownItem_ReturnedToPickup", returnToPickupReasonFromDropDown);
        }

        isReturnedToDHL = 0;
        isForReturnToPickup = 0;
        startActivity(i);
        i = null;
//        finish();
//        overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    private void UpdatePickUpETAAsyncTask(){
        final String[] requestPickResponseStr = {null};
        final boolean[] responseDataUpdateTime = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressInActiveDetailView != null)
                    progressInActiveDetailView = null;
                progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (isPickBtn) {
                        requestPickResponseStr[0] = webServiceHandler.updateDropOffETAForBookingID(uploadDateStr, String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId()));
                        JSONObject jOBJForRequestPick = new JSONObject(requestPickResponseStr[0]);
                        responseDataUpdateTime[0] = jOBJForRequestPick.getBoolean("success");
                        jOBJForRequestPick = null;
                        webServiceHandler = null;
                    } else {
                        requestPickResponseStr[0] = webServiceHandler.updatePickUpETAForBookingID(uploadDateStr, String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId()));
                        JSONObject jOBJForRequestPick = new JSONObject(requestPickResponseStr[0]);
                        responseDataUpdateTime[0] = jOBJForRequestPick.getBoolean("success");
                        jOBJForRequestPick = null;
                        webServiceHandler = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (isPickBtn) {
                        if (!requestPickResponseStr[0].equals("")) {
                            if (responseDataUpdateTime[0]) {
                                etaMsgText.setText(dateValueIs);
                                setBookingTimeToField(secondHeaderTxtABD, dateValueIs, "Your ETA for Drop off is ", false, true);
                            }
                        } else {
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Dropoff time not updated! try again.");
                            requestPickResponseStr[0] = null;
                        }
                    } else {
                        if (!requestPickResponseStr[0].equals("")) {
                            if (responseDataUpdateTime[0]) {
                                etaMsgText.setText(dateValueIs);
                                if ((activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                        || activeBookingModelForSelectedItem.getStatus().equals("Picked up")
                                        || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver")
                                        || activeBookingModelForSelectedItem.getStatus().equals("Delivery Attempted")))
                                    setBookingTimeToField(secondHeaderTxtABD, dateValueIs, "Your ETA for dropoff is ", false, true);
                                else
                                    setBookingTimeToField(secondHeaderTxtABD, dateValueIs, "Your ETA for Pick up is ", false, true);
                            }
                        } else {
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Pickup time not updated! try again.");
                            requestPickResponseStr[0] = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                    requestPickResponseStr[0] = null;
                }
            }
        }.execute();
    }

    private String pickUpPieceForNonDHL(WebserviceHandler webServiceHandler) {
        String requestPickResponseStr;
        if (activeBookingModelForSelectedItem.getPiecesArray().size() > 0)
            requestPickResponseStr = webServiceHandler.pickUpPackageByBarcode(activeBookingModelForSelectedItem.getPiecesArray().get(0));
        else
            requestPickResponseStr = webServiceHandler.pickUpPackageByBarcode(activeBookingModelForSelectedItem.getOrderNumber());

        return requestPickResponseStr;
    }

    private void PackageUploadWithETA_DetailViewAsyncTask(String checkForSmartSortPickup ){
        final String[] requestPickResponseStr = {null};
        final JSONObject[] jOBJForRequestPick = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        progressInActiveDetailView = null;
                    progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (checkForSmartSortPickup.equals("SMARTSORT")) {
                        try {
                            if (!BarcodeController.scanned_piece_to_pickup.equals(""))
                                requestPickResponseStr[0] = webServiceHandler.pickUpPackageByBarcode(BarcodeController.scanned_piece_to_pickup);
                            else
                                requestPickResponseStr[0]=  pickUpPieceForNonDHL(webServiceHandler);
                        } catch (Exception e) {
                            e.printStackTrace();
                            requestPickResponseStr[0]= pickUpPieceForNonDHL(webServiceHandler);
                        }
                        BarcodeController.scanned_piece_to_pickup = null;
                    } else {
                        requestPickResponseStr[0] = webServiceHandler.requestForPickPersonName((String) activeBookingModelForSelectedItem.getPick_ContactName(),
                                "", 5, String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId()),
                                uploadDateStr);
                        jOBJForRequestPick[0] = new JSONObject(requestPickResponseStr[0]);
                    }
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (checkForSmartSortPickup.equals("SMARTSORT")) {
                        try {
                            switch (LoginZoomToU.isLoginSuccess) {
                                case 0:
                                    JSONObject jObjResponseOfPickUpByBarcode = new JSONObject(requestPickResponseStr[0]);
                                    int runPickedUpProcessCount = jObjResponseOfPickUpByBarcode.getInt("runPickupProgress");
                                    if (runPickedUpProcessCount < 1)
                                        runPickedUpProcessCount = 1;
                                    int runTotalDeliveryCount = jObjResponseOfPickUpByBarcode.getInt("runTotalDeliveryCount");
                                    if (runTotalDeliveryCount < 1)
                                        runTotalDeliveryCount = 1;

                                    String message = "\nITEM : " + jObjResponseOfPickUpByBarcode.getString("orderNumber") +
                                            "\nAddress: " + jObjResponseOfPickUpByBarcode.getString("address") +
                                            "\nRoute: " + runPickedUpProcessCount + " of " + runTotalDeliveryCount +
                                            " parcels\nRemaining parcels to be picked up: " + jObjResponseOfPickUpByBarcode.getInt("runRemainingPickupCount");

                                    Toast.makeText(ActiveBookingDetail_New.this, "Picked up successfully", Toast.LENGTH_LONG).show();
                                    //*********** Sent battery info to admin on firebase ***************
                                    if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                                        new Service_CheckBatteryLevel(ActiveBookingDetail_New.this);

                                    setResult(Activity.RESULT_OK);
                                    finish();
                                    break;
                                case 1:
                                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No network!", "No network connection, Please check your connection and try again");
                                    break;
                                case 2:
                                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Alert!", "AWB / piece ID " + activeBookingModelForSelectedItem.getOrderNumber() + " does not exist. Please contact Zoom2u Support.");
                                    break;
                                case 3 | 4:
                                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try later.");
                                    break;
                                default:
                                    try {
                                        String messageStr = new JSONObject(requestPickResponseStr[0]).getString("message");
                                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", messageStr);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try later.");
                                    }
                                    break;
                            }
                            LoginZoomToU.isLoginSuccess = 0;
                        } catch (Exception e) {
                            e.printStackTrace();
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try later.");
                        }
                    } else {
                        if (jOBJForRequestPick[0].getBoolean("success") == true) {
                            dialogInActiveDetail(ActiveBookingDetail_New.this, "Congrats!", "Picked up successfully");
                            //*********** Sent battery info to admin on firebase ***************
                            if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                                new Service_CheckBatteryLevel(ActiveBookingDetail_New.this);
                        } else
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Alert!", jOBJForRequestPick[0].getString("Message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    dialogInActiveDetail(ActiveBookingDetail_New.this, "Server error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();
    }

    void dialogInActiveDetail(Context con, String titleTxt, String msgTxt) {
        if (alertDialog != null)
            alertDialog = null;
        alertDialog = new Dialog(con);
        alertDialog.setCancelable(true);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(R.layout.dialogview);

        Window window = alertDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView enterFieldDialogHEader = (TextView) alertDialog.findViewById(R.id.titleDialog);

        enterFieldDialogHEader.setText(titleTxt);

        TextView enterFieldDialogMsg = (TextView) alertDialog.findViewById(R.id.dialogMessageText);

        enterFieldDialogMsg.setText(msgTxt);

        Button enterFieldDialogDoneBtn = (Button) alertDialog.findViewById(R.id.dialogDoneBtn);

        enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switchToBookDelivery();
            }
        });

        alertDialog.show();
    }

    private void OnRouteDetailAsyncTask(){
        final String[] requestOnRouteResponseStr = {null};
        final boolean[] responseDataOnRoute = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressInActiveDetailView != null)
                    progressInActiveDetailView = null;
                progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    //    Functional_Utility.sendLocationToServer();
                    if (activeBookingModelForSelectedItem.getStatus().equals("Accepted"))
                        requestOnRouteResponseStr[0] = webServiceHandler.onRoutePickUp((Integer) activeBookingModelForSelectedItem.getBookingId());
                    else if (activeBookingModelForSelectedItem.getStatus().equals("Picked up"))
                        requestOnRouteResponseStr[0] = webServiceHandler.onRouteDropOff((Integer) activeBookingModelForSelectedItem.getBookingId());

                    JSONObject jOBJForRequestPick = new JSONObject(requestOnRouteResponseStr[0]);
                    responseDataOnRoute[0] = jOBJForRequestPick.getBoolean("success");
                    jOBJForRequestPick = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (responseDataOnRoute[0] == true) {
                        switchToBookDelivery();
                        requestOnRouteResponseStr[0] = null;
                    } else {
                        requestOnRouteResponseStr[0] = null;
                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    requestOnRouteResponseStr[0] = null;
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();
    }


    private void RequestToDropOffTemendo_AND_MenulogAsyncTask(){
        final String[] requestDropResponseStr = {null};
        final boolean[] responseDataRequestDrop = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        progressInActiveDetailView = null;
                    progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    try {
                        String userNameInActiveBookingDetail = "";
                        if (isDropOffFromATL)
                            userNameInActiveBookingDetail = "ATL - " + activeBookingModelForSelectedItem.getATLReceiverName();
                        else
                            userNameInActiveBookingDetail = activeBookingModelForSelectedItem.getDrop_ContactName();

                        requestDropResponseStr[0] = webServiceHandler.requestForDropOffPersonName(userNameInActiveBookingDetail,
                                "", 5, String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId()));


                        JSONObject jOBJForRequestPick = new JSONObject(requestDropResponseStr[0]);
                        responseDataRequestDrop[0] = jOBJForRequestPick.getBoolean("success");

                        if (responseDataRequestDrop[0] && isDropOffFromATL && ActiveBookingView.photo != null) {
                            try {
                                ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId());
                                Intent bgUploadImage = new Intent(ActiveBookingDetail_New.this, BG_ImageUploadToServer.class);
                                bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                bgUploadImage.putExtra("isActionBarPickup", false);
                                bgUploadImage.putExtra("isDropOffFromATL", true);
                                bgUploadImage.putExtra("isDropoffBooking", false);
                                startService(bgUploadImage);
                                isDropOffFromATL = false;
                                ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                bgUploadImage = null;
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                        jOBJForRequestPick = null;
                        webServiceHandler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (responseDataRequestDrop[0] == true) {
                        dismissATLDialog();
                        WebserviceHandler.reCountActiveBookings(1, 2);
                        new LoadChatBookingArray(ActiveBookingDetail_New.this, 0);
                        if (activeBookingModelForSelectedItem.getSource().equals("DHL"))
                            LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeBookingModelForSelectedItem.getBookingId());
                        switchToBookDelivery();
                    } else
                        dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Package not uploaded, Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    dialogInActiveDetail(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong. Please try again.");
                }
            }
        }.execute();
    }


    private void DropOffFromATLAsyncTask(){
        final String[] requestDropResponseStr = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressInABDView != null)
                    progressInABDView = null;
                progressInABDView = new ProgressDialog(ActiveBookingDetail_New.this);
                Custom_ProgressDialogBar.inItProgressBar(progressInABDView);
                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    Map<String, Object> mapObject = new HashMap<String, Object>();
                    mapObject.put("bookingId", activeBookingModelForSelectedItem.getBookingId());
                    mapObject.put("recipientName", "ATL - " + activeBookingModelForSelectedItem.getATLReceiverName());
                    mapObject.put("pieceBarcodes", Functional_Utility.getScannedPieceArray(activeBookingModelForSelectedItem.getPiecesArray()));
                    mapObject.put("notes", "Write some note here");
                    mapObject.put("signeesPosition", 5);
                    mapObject.put("forceIncompleteDropOff", false);
                    if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                            !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                        String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                        mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
                        mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    String requestDropoffParams = objectMapper.writeValueAsString(mapObject);
                    ;
                    requestDropResponseStr[0] = webServiceHandler.dropDeliveryUsingBarcode(requestDropoffParams);
                    if (LoginZoomToU.isLoginSuccess == 0) {
                        if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                            if (ActiveBookingView.photo != null) {
                                try {
                                    ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf((Integer) activeBookingModelForSelectedItem.getBookingId());
                                    Intent bgUploadImage = new Intent(ActiveBookingDetail_New.this, BG_ImageUploadToServer.class);
                                    bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                    bgUploadImage.putExtra("isActionBarPickup", false);
                                    bgUploadImage.putExtra("isDropOffFromATL", true);
                                    bgUploadImage.putExtra("isDropoffBooking", false);
                                    startService(bgUploadImage);
                                    isDropOffFromATL = false;
                                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                    bgUploadImage = null;
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            try {
                                if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                                    dismissATLDialog();
                                    WebserviceHandler.reCountActiveBookings(1, 2);
                                    new LoadChatBookingArray(ActiveBookingDetail_New.this, 0);
                                    LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeBookingModelForSelectedItem.getBookingId());
                                    try {
                                        if (!activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals("")
                                                && !activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals("null")
                                                && !activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals(null)) {
                                            if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(activeBookingModelForSelectedItem.getDropDateTime(),
                                                    activeBookingModelForSelectedItem.getFirstDropAttemptWasLate())
                                                    && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                                lateDeliveryReasonView(false);
                                            }
                                        } else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeBookingModelForSelectedItem.getDropDateTime())
                                                && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                            lateDeliveryReasonView(false);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeBookingModelForSelectedItem.getDropDateTime())
                                                && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                            lateDeliveryReasonView(false);
                                        }
                                    }
                                    switchToBookDelivery();
                                } else
                                    dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Package not uploaded, Please try again.");
                            } catch (Exception e) {
                                dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Package not uploaded, Please try again.");
                            }
                            break;

                        case 1:
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "No network!", "No network connection, Please check your connection and try again");
                            break;
                        default:
                            dialogInActiveDetail(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong. Please try again.");
                            break;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    dialogInActiveDetail(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong. Please try again.");
                    requestDropResponseStr[0] = null;
                } finally {
                    LoginZoomToU.isLoginSuccess = 0;
                    try {
                        if (progressInABDView != null)
                            if (progressInABDView.isShowing())
                                Custom_ProgressDialogBar.dismissProgressBar(progressInABDView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    private void lateDeliveryReasonView(boolean Isttd) {
        Intent callDialogReasonLate = new Intent(ActiveBookingDetail_New.this, DialogReasonForLateDelivery.class);
        callDialogReasonLate.putExtra("ReasonLateBookingID", activeBookingModelForSelectedItem.getBookingId());
        callDialogReasonLate.putExtra("BookingTypeSource", activeBookingModelForSelectedItem.getSource());
        callDialogReasonLate.putExtra("IsFromTtd", Isttd);
        startActivity(callDialogReasonLate);
        callDialogReasonLate = null;
    }

    //********** Redirect to previous view ************
    public void switchToBookDelivery() {
        if (routeViewCallingIntent == 181) {
            BookingView.bookingViewSelection = 1;
            ConfirmPickUpForUserName.isDropOffSuccessfull = 12;
            Intent callCompleteBookingfragment = new Intent(ActiveBookingDetail_New.this, SlideMenuZoom2u.class);
            callCompleteBookingfragment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(callCompleteBookingfragment);
            callCompleteBookingfragment = null;
        } else
            setResult(Activity.RESULT_OK);

        finish();
//        Intent intent1 = new Intent(ActiveBookingDetail_New.this, ServiceToUpdate_ActiveBookingList.class);
//        startService(intent1);
//        intent1 = null;
    }

    private void AttemptOrDispatchToCourierAsyncTask(){
        final JSONObject[] jOBJForRequestPick = new JSONObject[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (progressInActiveDetailView != null)
                        progressInActiveDetailView = null;
                    progressInActiveDetailView = new ProgressDialog(ActiveBookingDetail_New.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressInActiveDetailView);
                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    Functional_Utility.sendLocationToServer();
                    try {
                        String responseDataForAttemptOrDispatchStr = "";
                        if (activeBookingModelForSelectedItem.getStatus().equals("On Route to Dropoff")
                                || activeBookingModelForSelectedItem.getStatus().equals("Tried to deliver"))
                            if (LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && LoginZoomToU.getCurrentLocatnLongitude.equals("0.0"))
                                responseDataForAttemptOrDispatchStr = webServiceHandler.attemptDelivery(activeBookingModelForSelectedItem.getBookingId(), attemptDeliveryStr, positionForTriedToDeliverReason, "");
                            else
                                responseDataForAttemptOrDispatchStr = webServiceHandler.attemptDelivery(activeBookingModelForSelectedItem.getBookingId(), attemptDeliveryStr, positionForTriedToDeliverReason, LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude);
                        jOBJForRequestPick[0] = new JSONObject(responseDataForAttemptOrDispatchStr);
                        webServiceHandler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);

                    if (jOBJForRequestPick[0].getBoolean("success") == true) {
                        //*********** Sent battery info to admin on firebase ***************
                        if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                            new Service_CheckBatteryLevel(ActiveBookingDetail_New.this);
                        //*********** Sent battery info to admin on firebase ***************
                        Toast.makeText(ActiveBookingDetail_New.this, "Delivery attempted successfully", Toast.LENGTH_LONG).show();
                        switchToBookDelivery();
                        try {
                            if (!activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals("")
                                    && !activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals("null")
                                    && !activeBookingModelForSelectedItem.getFirstDropAttemptWasLate().equals(null)) {
                                if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(activeBookingModelForSelectedItem.getDropDateTime(),
                                        activeBookingModelForSelectedItem.getFirstDropAttemptWasLate())
                                        && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                    lateDeliveryReasonView(true);
                                }
                            } else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeBookingModelForSelectedItem.getDropDateTime())
                                    && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                lateDeliveryReasonView(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeBookingModelForSelectedItem.getDropDateTime())
                                    && activeBookingModelForSelectedItem.getLateReasonId() == 0) {
                                lateDeliveryReasonView(true);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (progressInActiveDetailView != null)
                        if (progressInActiveDetailView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInActiveDetailView);
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Sorry!", "Something went wrong, Please try again later");
                }
            }
        }.execute();


    }


    private void GetActiveBookingDetailByBookingID(){
        final String[] responseActiveBookingDetailStr = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                showProgressInABDView();
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    responseActiveBookingDetailStr[0] = handler.getActiveBookingDetailByID(bookingIdToLoadFromRouteView);
                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    try {
                        if (BookingView.bookingListArray != null) {
                            if (BookingView.bookingListArray.size() > 0)
                                BookingView.bookingListArray.clear();
                        } else
                            BookingView.bookingListArray = new ArrayList<DHL_SectionInterface>();

                        JSONObject jObjOfActiveBookingDetails = new JSONObject(responseActiveBookingDetailStr[0]);

                        JSONArray jsonArrayOfActiveBookingDetailByID = jObjOfActiveBookingDetails.getJSONArray("data");

                        if (jsonArrayOfActiveBookingDetailByID.length() > 0) {

                            JSONObject jObjOfBookingResponse = jsonArrayOfActiveBookingDetailByID.getJSONObject(0);

                            String distanceFromCurrentLoc = "";
                            try {
                                distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
                                        (LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
                                                jObjOfBookingResponse.getString("pickupGPSX"), jObjOfBookingResponse.getString("pickupGPSY"));
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }

                            String orderNumberForMenuLog = "";
                            try {
                                orderNumberForMenuLog = jObjOfBookingResponse.getString("orderNo");
                            } catch (Exception e) {
                                //e.printStackTrace();
                                orderNumberForMenuLog = "";
                            }

                            activeBookingModelForSelectedItem = new All_Bookings_DataModels();
                            activeBookingModelForSelectedItem.setBookingId(jObjOfBookingResponse.getInt("bookingId"));
                            activeBookingModelForSelectedItem.setCustomerId(jObjOfBookingResponse.getString("customerId"));
                            activeBookingModelForSelectedItem.setNewCustomer(jObjOfBookingResponse.getBoolean("isNewCustomer"));
                            activeBookingModelForSelectedItem.setCustomerName(jObjOfBookingResponse.getString("customerName"));
                            activeBookingModelForSelectedItem.setCustomerCompany(jObjOfBookingResponse.getString("customerCompany"));
                            activeBookingModelForSelectedItem.setCustomerContact(jObjOfBookingResponse.getString("customerContact"));
                            activeBookingModelForSelectedItem.setBookingRefNo(jObjOfBookingResponse.getString("bookingRefNo"));
                            activeBookingModelForSelectedItem.setPickupDateTime(jObjOfBookingResponse.getString("pickupDateTime"));
                            activeBookingModelForSelectedItem.setPick_Address(jObjOfBookingResponse.getString("pickupAddress"));
                            activeBookingModelForSelectedItem.setPick_ContactName(jObjOfBookingResponse.getString("pickupContactName"));
                            activeBookingModelForSelectedItem.setPick_GPSX(jObjOfBookingResponse.getString("pickupGPSX"));
                            activeBookingModelForSelectedItem.setPick_GPSY(jObjOfBookingResponse.getString("pickupGPSY"));
                            activeBookingModelForSelectedItem.setPick_Notes(jObjOfBookingResponse.getString("pickupNotes"));
                            activeBookingModelForSelectedItem.setPick_Phone(jObjOfBookingResponse.getString("pickupPhone"));
                            activeBookingModelForSelectedItem.setPick_StreetNo(jObjOfBookingResponse.getString("pickupStreetNumber"));
                            activeBookingModelForSelectedItem.setPick_StreetName(jObjOfBookingResponse.getString("pickupStreet"));
                            activeBookingModelForSelectedItem.setPick_Suburb(jObjOfBookingResponse.getString("pickupSuburb"));
                            activeBookingModelForSelectedItem.setDropDateTime(jObjOfBookingResponse.getString("dropDateTime"));
                            activeBookingModelForSelectedItem.setDrop_Address(jObjOfBookingResponse.getString("dropAddress"));
                            activeBookingModelForSelectedItem.setDrop_ContactName(jObjOfBookingResponse.getString("dropContactName"));
                            activeBookingModelForSelectedItem.setDrop_GPSX(jObjOfBookingResponse.getString("dropGPSX"));
                            activeBookingModelForSelectedItem.setDrop_GPSY(jObjOfBookingResponse.getString("dropGPSY"));
                            activeBookingModelForSelectedItem.setDrop_Notes(jObjOfBookingResponse.getString("dropNotes"));
                            activeBookingModelForSelectedItem.setDrop_Phone(jObjOfBookingResponse.getString("dropPhone"));
                            activeBookingModelForSelectedItem.setDrop_StreetNo(jObjOfBookingResponse.getString("dropStreetNumber"));
                            activeBookingModelForSelectedItem.setDrop_StreetName(jObjOfBookingResponse.getString("dropStreet"));
                            activeBookingModelForSelectedItem.setDrop_Suburb(jObjOfBookingResponse.getString("dropSuburb"));
                            activeBookingModelForSelectedItem.setCreatedDateTime(jObjOfBookingResponse.getString("createdDateTime"));
                            activeBookingModelForSelectedItem.setDeliverySpeed(jObjOfBookingResponse.getString("deliverySpeed"));
                            activeBookingModelForSelectedItem.setDistance(jObjOfBookingResponse.getString("distance"));
                            activeBookingModelForSelectedItem.setNotes(jObjOfBookingResponse.getString("notes"));
                            activeBookingModelForSelectedItem.setVehicle(jObjOfBookingResponse.getString("vehicle"));
                            activeBookingModelForSelectedItem.setSource(jObjOfBookingResponse.getString("source"));
                            activeBookingModelForSelectedItem.setPackage(jObjOfBookingResponse.getString("packageType"));
                            activeBookingModelForSelectedItem.setPrice(jObjOfBookingResponse.getDouble("courierPrice"));
                            activeBookingModelForSelectedItem.setStatus(jObjOfBookingResponse.getString("status"));
                            activeBookingModelForSelectedItem.setPickupETA(jObjOfBookingResponse.getString("pickupETA"));
                            activeBookingModelForSelectedItem.setDropETA(jObjOfBookingResponse.getString("dropETA"));
                            activeBookingModelForSelectedItem.setPickupActual(jObjOfBookingResponse.getString("pickupActual"));
                            activeBookingModelForSelectedItem.setDropActual(jObjOfBookingResponse.getString("dropActual"));
                            activeBookingModelForSelectedItem.setPickupPerson(jObjOfBookingResponse.getString("pickupPerson"));
                            activeBookingModelForSelectedItem.setDropPerson(jObjOfBookingResponse.getString("dropPerson"));
                            activeBookingModelForSelectedItem.setPickupSignature(jObjOfBookingResponse.getString("pickupSignature"));
                            activeBookingModelForSelectedItem.setDropSignature(jObjOfBookingResponse.getString("dropSignature"));
                            activeBookingModelForSelectedItem.setPickupCompanyName(jObjOfBookingResponse.getString("pickupCompany"));
                            activeBookingModelForSelectedItem.setATL(jObjOfBookingResponse.getBoolean("isATL"));
                            activeBookingModelForSelectedItem.setATLLeaveAt(jObjOfBookingResponse.getString("atlLeaveAt"));
                            activeBookingModelForSelectedItem.setATLReceiverName(jObjOfBookingResponse.getString("atlReceiverName"));
                            activeBookingModelForSelectedItem.setATLDoorCode(jObjOfBookingResponse.getString("atlDoorCode"));
                            activeBookingModelForSelectedItem.setATLInstructions(jObjOfBookingResponse.getString("atlInstructions"));

                            try {
                                activeBookingModelForSelectedItem.setDrop_Company(jObjOfBookingResponse.getString("dropCompany"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                activeBookingModelForSelectedItem.setDrop_Company("Not available");
                            }

                            try {
                                activeBookingModelForSelectedItem.setNoContactDelivery(jObjOfBookingResponse.getBoolean("atlNoContact"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                activeBookingModelForSelectedItem.setRoutePolyline(jObjOfBookingResponse.getString("routePolyline"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                activeBookingModelForSelectedItem.setFirstDropAttemptWasLate(jObjOfBookingResponse.getString("firstDropAttemptWasLate"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                activeBookingModelForSelectedItem.setAuthorityToLeavePermitted(jObjOfBookingResponse.getBoolean("authorityToLeavePermitted"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            activeBookingModelForSelectedItem.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
                            activeBookingModelForSelectedItem.setOrderNumber(orderNumberForMenuLog);

                            try {
                                activeBookingModelForSelectedItem.setRunId(jObjOfBookingResponse.getInt("runId"));
                                activeBookingModelForSelectedItem.setRunNumber(jObjOfBookingResponse.getInt("runNumber"));
                                activeBookingModelForSelectedItem.setDropSuburbCount(jObjOfBookingResponse.getInt("dropSuburbCount"));
                                activeBookingModelForSelectedItem.setRunTotalDeliveryCount(jObjOfBookingResponse.getInt("runTotalDeliveryCount"));
                                activeBookingModelForSelectedItem.setRunCompletedDeliveryCount(jObjOfBookingResponse.getInt("runCompletedDeliveryCount"));
                                activeBookingModelForSelectedItem.setRunType(jObjOfBookingResponse.getString("runType"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            activeBookingModelForSelectedItem.setDoesAlcoholDeliveries(jObjOfBookingResponse.getBoolean("doesAlcoholDeliveries"));

                            try {
                                activeBookingModelForSelectedItem.setDropIdentityNumber(jObjOfBookingResponse.getString("dropIdentityNumber"));
                                activeBookingModelForSelectedItem.setDropIdentityType(jObjOfBookingResponse.getString("dropIdentityType"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                activeBookingModelForSelectedItem.setDropIdentityNumber("");
                                activeBookingModelForSelectedItem.setDropIdentityType("");
                            }

                            activeBookingModelForSelectedItem.setLateReasonId(jObjOfBookingResponse.getInt("lateReasonId"));
                            try {
                                if (jObjOfBookingResponse.has("triedToDeliverReason")
                                        && jObjOfBookingResponse.getString("status").equals("Tried to deliver")
                                        && (jObjOfBookingResponse.getString("triedToDeliverReason").equals("PA - Person is under the age of 18")
                                        || jObjOfBookingResponse.getString("triedToDeliverReason").equals("PI - Person is intoxicated"))) {
                                    activeBookingModelForSelectedItem.setTTDReasonForAlcoholDelivery(true);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ArrayList<String> piecesArrayActiveList = new ArrayList<String>();
                            HashMap<String, Boolean> pieceScannedMap = null;
                            JSONArray jArrayOfPieces = jObjOfBookingResponse.getJSONArray("pieces");
                            int pieceArrayLength = jArrayOfPieces.length();
                            if (pieceArrayLength > 0) {
                                if (!jObjOfBookingResponse.getString("status").equals("Accepted") && !jObjOfBookingResponse.getString("status").equals("On Route to Pickup")
                                        && !jObjOfBookingResponse.getString("status").equals("Picked up")) {
                                    if (pieceArrayLength > 1)
                                        pieceScannedMap = new HashMap<String, Boolean>();
                                }

                                for (int p = 0; p < pieceArrayLength; p++) {
                                    piecesArrayActiveList.add(jArrayOfPieces.getJSONObject(p).getString("barcode"));

                                    if (pieceScannedMap != null) {
                                        if (pieceArrayLength > 1) {
                                            try {
                                                if (jArrayOfPieces.getJSONObject(p).getString("status").equals("Picked up")
                                                        || jArrayOfPieces.getJSONObject(p).getString("status").equals("Tried to deliver"))
                                                    pieceScannedMap.put(jArrayOfPieces.getJSONObject(p).getString("barcode"), true);
                                                else
                                                    pieceScannedMap.put(jArrayOfPieces.getJSONObject(p).getString("barcode"), false);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                                pieceScannedMap.put(jArrayOfPieces.getJSONObject(p).getString("barcode"), false);
                                            }
                                        }
                                    }
                                }
                            }

                            activeBookingModelForSelectedItem.setPiecesScannedMap(pieceScannedMap);
                            activeBookingModelForSelectedItem.setPiecesArray(piecesArrayActiveList);
                            piecesArrayActiveList = null;

                            activeBookingModelForSelectedItem.setPickedUpPieceCount(jObjOfBookingResponse.getInt("pickedUpPieceCount"));
                            activeBookingModelForSelectedItem.setTotalPieceCount(jObjOfBookingResponse.getInt("totalPieceCount"));

                            ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                            for (int k = 0; k < jObjOfBookingResponse.getJSONArray("shipments").length(); k++) {
                                HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                                JSONObject jObjOfShipmentItem = jObjOfBookingResponse.getJSONArray("shipments").getJSONObject(k);
                                objOFShipments.put("Category", jObjOfShipmentItem.getString("category"));
                                objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("quantity"));
                                try {
                                    objOFShipments.put("LengthCm", jObjOfShipmentItem.getInt("lengthCm"));
                                    objOFShipments.put("WidthCm", jObjOfShipmentItem.getInt("widthCm"));
                                    objOFShipments.put("HeightCm", jObjOfShipmentItem.getInt("heightCm"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    objOFShipments.put("LengthCm", 0);
                                    objOFShipments.put("WidthCm", 0);
                                    objOFShipments.put("HeightCm", 0);
                                }

                                try {
                                    objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("itemWeightKg"));
                                    objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("totalWeightKg"));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    objOFShipments.put("ItemWeightKg", 0);
                                    objOFShipments.put("TotalWeightKg", 0);
                                }
                                arrayOfShipments.add(objOFShipments);
                                objOFShipments = null;
                                if (jObjOfShipmentItem.getString("category").equals("Flowers"))
                                    activeBookingModelForSelectedItem.setIsCakeAndFlower(1);
                            }
                            activeBookingModelForSelectedItem.setShipmentsArray(arrayOfShipments);
                            arrayOfShipments = null;
                            BookingView.bookingListArray.add(activeBookingModelForSelectedItem);
                        } else
                            dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Something went wrong, Please try again later.");
                    } catch (Exception e) {
                        e.printStackTrace();
                        dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Something went wrong, Please try again later.");
                    }
                    itemPositionInActiveDetail = 0;
                    inItABDUIContents();                                        // Initialize active booking detail view
                    ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
                    Custom_ProgressDialogBar.dismissProgressBar(progressInABDView);
                } catch (Exception e) {
                    e.printStackTrace();
                    dialogInActiveDetail(ActiveBookingDetail_New.this, "Error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();

    }


    //********** Init scanned piece map for multiple pieces **********
    public static void inItScannedPieceMap() {
        if (scannedPieceMap != null)
            scannedPieceMap.clear();
        else
            scannedPieceMap = new HashMap<String, Boolean>();
    }

    //*************** Show Non-ATL dialog when ATL = 0 & AuthorityToLeavePermitted = 0 ************

    //*****  isProccedForDropOff = 1 When trying to drop off the booking
    //*****  isProccedForDropOff = 2 When trying to Tried to deliver the booking
    private void dialogNonATLBooking(final int isProccedForDropOff) {
        try {
            if (alertDialog != null)
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialog != null)
            alertDialog = null;
        alertDialog = new Dialog(ActiveBookingDetail_New.this);
        alertDialog.setCancelable(true);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(R.layout.dialog_non_atl_booking);

        Window window = alertDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView titleTxtNonATLDialog = (TextView) alertDialog.findViewById(R.id.titleTxtNonATLDialog);


        TextView msgTxtNonATLDialog = (TextView) alertDialog.findViewById(R.id.msgTxtNonATLDialog);


        Button doneBtnNonATLDialog = (Button) alertDialog.findViewById(R.id.doneBtnNonATLDialog);

        doneBtnNonATLDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (isProccedForDropOff == 2)
                    openBarCodeScannerView(2);
                else
                    alertToShowSpecialInsBeforePickOrDrop(activeBookingModelForSelectedItem.getDrop_Notes());
            }
        });

        Button backBtnNonATLDialog = (Button) alertDialog.findViewById(R.id.backBtnNonATLDialog);

        backBtnNonATLDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void RequestForReturnToPickUp(){
        final String[] returnToPickupResponseStr = {null};
        final ProgressDialog[] progressForReturnToPickup = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressForReturnToPickup[0] != null)
                    progressForReturnToPickup[0] = null;
                progressForReturnToPickup[0] = new ProgressDialog(ActiveBookingDetail_New.this);
                Custom_ProgressDialogBar.inItProgressBar(progressForReturnToPickup[0]);
                ActiveBookingView.getCurrentLocation(ActiveBookingDetail_New.this);
            }

            @Override
            public void doInBackground() {
                try {
                    if (ActiveBookingView.arrayOfBookingId != null)
                        ActiveBookingView.arrayOfBookingId.clear();
                    else
                        ActiveBookingView.arrayOfBookingId = new HashSet<Integer>();

                    ActiveBookingView.arrayOfBookingId.add(activeBookingModelForSelectedItem.getBookingId());
                    Map<String, Object> mapObject = new HashMap<String, Object>();

                    mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
                    mapObject.put("notes", "Write some note here");
                    mapObject.put("signeesPosition", 0);
                    mapObject.put("returnToPickupReason", returnToPickupReasonFromDropDown);
                    mapObject.put("returnToPickupNotes", attemptDeliveryStr);

                    //*************** Return to Pickup for Normal bookings
                    mapObject.put("recipient", activeBookingModelForSelectedItem.getPick_ContactName());
                    if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                            !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                        String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                        mapObject.put("location", String.valueOf(LoginZoomToU.getCurrentLocatnlatitude) + "," + String.valueOf(LoginZoomToU.getCurrentLocatnLongitude));
                    }

                    returnToPickupResponseStr[0] =  callReturnToPickupAPI(2, mapObject);
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (LoginZoomToU.isLoginSuccess == 0) {
                        try {
                            if (new JSONObject(returnToPickupResponseStr[0]).getBoolean("success")) {
                                WebserviceHandler.reCountActiveBookings(ActiveBookingView.arrayOfBookingId.size(), 2);
                                new LoadChatBookingArray(ActiveBookingDetail_New.this, 0);
                                LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeBookingModelForSelectedItem.getBookingId());
                                Toast.makeText(ActiveBookingDetail_New.this, "Returned successfully", Toast.LENGTH_LONG).show();
                                switchToBookDelivery();
                            } else
                                DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Error!", "Something went wrong. Please try again.");
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong. Please try again.");
                        }
                    } else
                        DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Sorry!", "Error while returning, please try again");
                } catch (Exception e) {
                    //e.printStackTrace();
                    DialogActivity.alertDialogView(ActiveBookingDetail_New.this, "Server Error!", "Something went wrong. Please try again.");
                } finally {
                    LoginZoomToU.isLoginSuccess = 0;
                    returnToPickupReasonFromDropDown = 0;
                    attemptDeliveryStr = "";
                    try {
                        if (progressForReturnToPickup[0] != null)
                            if (progressForReturnToPickup[0].isShowing())
                                Custom_ProgressDialogBar.dismissProgressBar(progressForReturnToPickup[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private String callReturnToPickupAPI(int isReturnToPickup, Map<String, Object> mapObject) {
        String returnToPickupResponseStr=null;
        try {
            WebserviceHandler webServiceHandler = new WebserviceHandler();
            ObjectMapper objectMapper = new ObjectMapper();
            String bookingsForReturnStr = objectMapper.writeValueAsString(mapObject);
            returnToPickupResponseStr = webServiceHandler.multipleReturnDeliveries(isReturnToPickup, bookingsForReturnStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnToPickupResponseStr;
    }


}