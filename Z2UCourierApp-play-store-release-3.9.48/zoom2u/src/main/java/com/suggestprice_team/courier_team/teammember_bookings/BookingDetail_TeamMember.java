package com.suggestprice_team.courier_team.teammember_bookings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.slidemenu.offerrequesthandlr.Bid_RequestView_Detail;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;

import java.util.List;

/**
 * Created by Arun on 28-July-2018.
 */

public class BookingDetail_TeamMember extends Activity implements View.OnClickListener {
    private boolean isPickShowed = true;
    TextView countChatBookingDetail;
    TextView item_type,distance;
    TextView secondHeaderTxtABD;

    RelativeLayout customerDetailLayoutABD_Team;
    TextView bookingRefNoTxtABD_Team;
    ImageView arrowUpDownImgCustmerLayoutABD_Team;




    LinearLayout pickUpHeaderLayoutABD_Team;
    TextView pickHeaderLayoutPickTxtABD_Team;
    TextView pickHeaderLayoutPickTimeTxtABD_Team;
    LinearLayout dropOffHeaderLayoutABD_Team;
    TextView dropHeaderLayoutDropTxtABD_Team;

    RelativeLayout itemsOfABD_TeamLayout;
    TextView pickDropCustomerNameABD_Team;
    TextView pickDropMolibeABD_Team;
    TextView pickDropAddressABD_Team;
    TextView pickDropNotesABD_Team,pickDropDate;

    All_Bookings_DataModels bookingModel;

    String pickOrDropPersonContactNoToCall, pickOrDropPersonAddressForMap;
    private int customerDetailViewExpanded = 0;

    RelativeLayout.LayoutParams paramForCollapseExpandImgParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookingdetail_teammember);
        RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
        Window window = BookingDetail_TeamMember.this.getWindow();
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
        //************ Ask for location permission ***************//
        BookingDetail_New.askForLocationPermission(BookingDetail_TeamMember.this);
        try {
            bookingModel = (All_Bookings_DataModels) getIntent().getParcelableExtra("BookingModel");

            inItUIContents();
            inItActiveBookingDetailMapFragment();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if ((bookingModel.getStatus().equals("On Route to Dropoff")
                    || bookingModel.getStatus().equals("Picked up")
                    || bookingModel.getStatus().equals("Tried to deliver")
                    || bookingModel.getStatus().equals("Delivery Attempted"))) {
                if (!bookingModel.getDropETA().equals("") && !bookingModel.getDropETA().equals("null"))
                    setDefaultETATOField(bookingModel.getDropETA(), " ETA for Drop off is ");
                else
                    setDefaultETATOField(bookingModel.getDropDateTime(), " ETA for Drop off is ");
            } else {
                if (!bookingModel.getPickupETA().equals("") && !bookingModel.getPickupETA().equals("null"))
                    setDefaultETATOField(bookingModel.getPickupETA(), " ETA for Pick up is ");
                else
                    setDefaultETATOField(bookingModel.getPickupDateTime(), " ETA for Pick up is ");
            }
        } catch (Exception e) {
        }

    }
    private void setDefaultETATOField(String dropDateTime, String msgStr) {

       String dateValueIs = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServerone(dropDateTime);
        setBookingTimeToField(secondHeaderTxtABD, dateValueIs, msgStr, false, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatBookingDetail);
        SlideMenuZoom2u.countChatBookingView = countChatBookingDetail;
    }

    //********* Initialize Booking details team member UI content **********
    private void inItUIContents() {
        secondHeaderTxtABD=findViewById(R.id.secondHeaderTxtABD);
        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);
        findViewById(R.id.titleBookingDetail);
        ((TextView) findViewById(R.id.titleBookingDetail)).setText("#" + bookingModel.getBookingRefNo());
        findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);
        countChatBookingDetail = (TextView) findViewById(R.id.countChatBookingDetail);

        countChatBookingDetail.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatBookingDetail;
        pickDropDate=findViewById(R.id.pickDropDate);
        distance=findViewById(R.id.distance);
        item_type=findViewById(R.id.item_type);
        distance.setText("Distance :"+bookingModel.getDistance());
        customerDetailLayoutABD_Team = (RelativeLayout) findViewById(R.id.customerDetailLayoutABD_Team);
        bookingRefNoTxtABD_Team = (TextView) findViewById(R.id.bookingRefNoTxtABD_Team);
        arrowUpDownImgCustmerLayoutABD_Team = (ImageView) findViewById(R.id.arrowUpDownImgCustmerLayoutABD_Team);
        pickUpHeaderLayoutABD_Team = (LinearLayout) findViewById(R.id.pickUpHeaderLayoutABD_Team);
        pickHeaderLayoutPickTxtABD_Team = (TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD_Team);
        pickHeaderLayoutPickTimeTxtABD_Team = (TextView) findViewById(R.id.pickdropHeaderLayoutPickTimeTxtABD);
        dropOffHeaderLayoutABD_Team = (LinearLayout) findViewById(R.id.dropOffHeaderLayoutABD_Team);
        dropHeaderLayoutDropTxtABD_Team = (TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD_Team);
        itemsOfABD_TeamLayout = (RelativeLayout) findViewById(R.id.itemsOfABD_TeamLayout);
        pickDropCustomerNameABD_Team = (TextView) findViewById(R.id.pickDropCustomerNameABD_Team);
        pickDropMolibeABD_Team = (TextView) findViewById(R.id.pickDropMolibeABD_Team);
        pickDropAddressABD_Team = (TextView) findViewById(R.id.pickDropAddressABD_Team);
        pickDropNotesABD_Team = (TextView) findViewById(R.id.pickDropNotesABD_Team);
        setDataToUI();      //*********** Set data to UI contents
    }

    //*********** Set data to UI contents ***********
    private void setDataToUI() {
        bookingRefNoTxtABD_Team.setText("#" + bookingModel.getBookingRefNo());

        customerDetailLayoutABD_Team.setOnClickListener(this);


        String cmpanyNameMobileInfoStr = "";
        if (!bookingModel.getCustomerCompany().equals("") && !bookingModel.getCustomerCompany().equals("null"))
            cmpanyNameMobileInfoStr = bookingModel.getCustomerCompany() + "\n";
        if (!bookingModel.getCustomerContact().equals("") && !bookingModel.getCustomerContact().equals("null"))
            cmpanyNameMobileInfoStr = cmpanyNameMobileInfoStr + bookingModel.getCustomerContact();

        pickUpHeaderLayoutABD_Team.setOnClickListener(this);
        setBookingTimeToField(pickHeaderLayoutPickTimeTxtABD_Team, bookingModel.getPickupDateTime(), "", false, false);
        dropOffHeaderLayoutABD_Team.setOnClickListener(this);

        pickOrDropPersonContactNoToCall = bookingModel.getPick_Phone();
        pickOrDropPersonAddressForMap = bookingModel.getPick_Address();
        pickDropCustomerNameABD_Team.setText(bookingModel.getPick_ContactName());
        pickDropMolibeABD_Team.setText(pickOrDropPersonContactNoToCall);
        pickDropAddressABD_Team.setText(pickOrDropPersonAddressForMap);
        pickDropNotesABD_Team.setText(bookingModel.getPick_Notes());


        item_type.setText("");
        try {
            if (bookingModel.getShipmentsArray().size() > 0) {
                for (int i = 0; i < bookingModel.getShipmentsArray().size(); i++) {
                    if (i == 0) {
                        item_type.append(bookingModel.getShipmentsArray().get(i).get("Category") + " (" + bookingModel.getShipmentsArray().get(i).get("Quantity") + ")");

                    }else{

                        item_type.append(", " + bookingModel.getShipmentsArray().get(i).get("Category") + " (" + bookingModel.getShipmentsArray().get(i).get("Quantity") + ")");
                }
                }
            } else{
                item_type.append("" + bookingModel.getPackage());
        }
        } catch (Exception e) {
            e.printStackTrace();

            item_type.append("" + bookingModel.getPackage());
        }
    }

    //******** Initialize active booking detail view map ***************
    private void inItActiveBookingDetailMapFragment() {
        try {
            try {
                if (BookingDetail_New.mapFragmentBookingDetail != null)
                    BookingDetail_New.mapFragmentBookingDetail = null;
                BookingDetail_New.mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewABD_Team));
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
                                .position(new LatLng(Double.parseDouble((String) bookingModel.getPick_GPSX()),
                                        Double.parseDouble((String) bookingModel.getPick_GPSY())))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin)));
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    try{
                        BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
                                .position(new LatLng(Double.parseDouble((String) bookingModel.getDrop_GPSX()),
                                        Double.parseDouble((String) bookingModel.getDrop_GPSY())))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin)));
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    BookingDetail_New.gMapBookingDetail.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            try {
                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(new LatLng(Double.parseDouble((String) bookingModel.getPick_GPSX()),
                                                Double.parseDouble((String) bookingModel.getPick_GPSY())))
                                        .include(new LatLng(Double.parseDouble((String) bookingModel.getDrop_GPSX()),
                                                Double.parseDouble((String) bookingModel.getDrop_GPSY())))
                                        .build();
                                BookingDetail_New.gMapBookingDetail.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                                bounds = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                                BookingDetail_New.gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.274398, 133.775136), 8));
                            }
                        }
                    });


                    List<LatLng> list = null;
                    try {
                        list = PolyUtil.decode(bookingModel.getRoutePolyline());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (list != null)
                        BookingDetail_New.drawRoute(list);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backFromBookingDetail:
                finish();
                break;
            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(BookingDetail_TeamMember.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.pickUpHeaderLayoutABD_Team:
                if (!isPickShowed) {
                    isPickShowed = true;
                    ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD_Team)).setTextColor(Color.parseColor("#FFFFFF"));
                    ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD_Team)).setTextColor(Color.parseColor("#374350"));
                    pickUpHeaderLayoutABD_Team.setBackgroundResource(R.drawable.new_gray_back);
                    dropOffHeaderLayoutABD_Team.setBackgroundResource(R.drawable.new_gary_backgroun);
                pickOrDropPersonContactNoToCall = bookingModel.getPick_Phone();
                pickOrDropPersonAddressForMap = bookingModel.getPick_Address();
                pickDropCustomerNameABD_Team.setText(bookingModel.getPick_ContactName());
                pickDropMolibeABD_Team.setText(pickOrDropPersonContactNoToCall);
                pickDropAddressABD_Team.setText(pickOrDropPersonAddressForMap);
                pickDropNotesABD_Team.setText(bookingModel.getPick_Notes());

                    setBookingTimeToField(pickHeaderLayoutPickTimeTxtABD_Team, bookingModel.getPickupDateTime(), "", false, false);
                }
                break;
            case R.id.dropOffHeaderLayoutABD_Team:
                if (isPickShowed) {
                    isPickShowed = false;
                    pickUpHeaderLayoutABD_Team.setBackgroundResource(R.drawable.new_gary_backgroun);
                    ((TextView) findViewById(R.id.pickHeaderLayoutPickTxtABD_Team)).setTextColor(Color.parseColor("#374350"));
                    ((TextView) findViewById(R.id.dropHeaderLayoutDropTxtABD_Team)).setTextColor(Color.parseColor("#FFFFFF"));
                    dropOffHeaderLayoutABD_Team.setBackgroundResource(R.drawable.new_gray_back);
                pickOrDropPersonContactNoToCall = bookingModel.getDrop_Phone();
                pickOrDropPersonAddressForMap = bookingModel.getDrop_Address();
                pickDropCustomerNameABD_Team.setText(bookingModel.getDrop_ContactName());
                pickDropMolibeABD_Team.setText(pickOrDropPersonContactNoToCall);
                pickDropAddressABD_Team.setText(pickOrDropPersonAddressForMap);
                pickDropNotesABD_Team.setText(bookingModel.getDrop_Notes());

                    setBookingTimeToField(pickHeaderLayoutPickTimeTxtABD_Team, bookingModel.getDropDateTime(), "", false, false);
                }
                break;

            case R.id.customerDetailLayoutABD_Team:
                showPopup();
                break;
        }
    }


    public void showPopup() {
        Dialog enterFieldDialog;


        try {
            enterFieldDialog = new Dialog(this, android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(false);
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

            ((TextView) enterFieldDialog.findViewById(R.id.priceTxtABD)).setText("$" + Functional_Utility.returnCourierPrice((Double) bookingModel.getPrice()));
            ((TextView) enterFieldDialog.findViewById(R.id.vehicleTxtABD)).setText(bookingModel.getVehicle());
            ((TextView) enterFieldDialog.findViewById(R.id.bookingId)).setText("#" + bookingModel.getBookingRefNo());
            setBookingTimeToField((TextView) enterFieldDialog.findViewById(R.id.createdTimeTxtCustomerDetailABD), bookingModel.getCreatedDateTime(), "", true, false);
            ((TextView) enterFieldDialog.findViewById(R.id.customerNameTxtCoureirDetailABD)).setText(bookingModel.getCustomerName());

            String cmpanyNameMobileInfoStr = "";
            if (!bookingModel.getCustomerCompany().equals("")
                    && !bookingModel.getCustomerCompany().equals("null"))
                cmpanyNameMobileInfoStr = bookingModel.getCustomerCompany() + "\n";
            if (!bookingModel.getCustomerContact().equals("")
                    && !bookingModel.getCustomerContact().equals("null"))
                cmpanyNameMobileInfoStr = cmpanyNameMobileInfoStr + bookingModel.getCustomerContact();
            ((TextView) enterFieldDialog.findViewById(R.id.customerCompanyAndMobNoTxtCouDetalABD)).setText(cmpanyNameMobileInfoStr);


            (enterFieldDialog.findViewById(R.id.iconCallCustomerBtnInDeailABD)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callingIntent = new Intent(Intent.ACTION_DIAL);
                    callingIntent.setData(Uri.parse("tel:"+bookingModel.getCustomerContact()));
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

            if (!bookingModel.getNotes().equals("")
                    && !bookingModel.getNotes().equals("null"))
                ((TextView) enterFieldDialog.findViewById(R.id.deliveryNotesTxtcourierDetailABD)).setText(bookingModel.getNotes());
            else
                ((TextView) enterFieldDialog.findViewById(R.id.deliveryNotesTxtcourierDetailABD)).setText("Delivery instructions here");

            enterFieldDialog.setCanceledOnTouchOutside(true);
            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }




    /*
   ********* Set time in HH:mm format to particular field **********
    */
    private void setBookingTimeToField(TextView timeTxtInABD, String dropDateTime, String addedTxtToField, boolean isBothDateTimeInvolved, boolean isAlreadyConvertedDate) {
        try {
            String[] bookingTimeToFieldArray;
            if (!dropDateTime.equals("") && !dropDateTime.equals("null")) {
                if (isAlreadyConvertedDate){
                    bookingTimeToFieldArray = dropDateTime.split(" ");
                    if (!addedTxtToField.equals("")) {
                        timeTxtInABD.setText(addedTxtToField + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                         pickDropDate.setText(bookingTimeToFieldArray[0]);
                    } else{
                        timeTxtInABD.setText(bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                        pickDropDate.setText(bookingTimeToFieldArray[0]);}
                }else {
                    String bookingTimeToField = LoginZoomToU.checkInternetwithfunctionality.serverDateTimeToDevice_TeamDetails(dropDateTime);
                    bookingTimeToFieldArray = bookingTimeToField.split(" ");
                    if (isBothDateTimeInvolved) {
                        timeTxtInABD.setText("Created " + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2] + " on " + bookingTimeToFieldArray[0]);
                        pickDropDate.setText(bookingTimeToFieldArray[0]);
                    } else {
                        if (!addedTxtToField.equals("")) {
                            timeTxtInABD.setText(addedTxtToField + " " + bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                            pickDropDate.setText(bookingTimeToFieldArray[0]);
                        }  else{
                            timeTxtInABD.setText(bookingTimeToFieldArray[1] + " " + bookingTimeToFieldArray[2]);
                            pickDropDate.setText(bookingTimeToFieldArray[0]);
                        }
                    }
                    bookingTimeToFieldArray = null;
                }
            }else
                timeTxtInABD.setText("-NA-");
        } catch (Exception e) {
            e.printStackTrace();
            timeTxtInABD.setText("-NA-");
        }
    }

    private void inItarrowForCollapseExpaImgParam(ImageView arrowUpDownImg, int supportLayoutRefId, int imgResource){
        arrowUpDownImg.setImageResource(imgResource);
        paramForCollapseExpandImgParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        paramForCollapseExpandImgParam.setMargins(10, 0, 0, 10);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramForCollapseExpandImgParam.addRule(RelativeLayout.ALIGN_BOTTOM, supportLayoutRefId);
        arrowUpDownImg.setLayoutParams(paramForCollapseExpandImgParam);
        paramForCollapseExpandImgParam = null;
    }





}
