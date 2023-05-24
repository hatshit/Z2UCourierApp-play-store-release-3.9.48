package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Arun on 8/5/17.
 */

public class Alert_To_PlaceBid extends Activity implements View.OnClickListener,  AdapterView.OnItemSelectedListener{

    TextView dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick, dateValueTxtToPlaceBidToDrop, timeValueTxtToPlaceBidToDrop;
    EditText edtPricrTxt, edtNotesTxtToPlaceBid;
    Spinner bidActivePeriodSpinnerToPlaceBid;

    RequestView_DetailPojo requestView_detailPojo;
    DateTimePickerView datePickerToSelectItemForDateField;

    private String courierPriceStr, notesStr;
    private String etaTimeForPickup, etaTimeForDropoff;

    private int bidActivePeriodInterval = 15;
    ProgressDialog progressToPlaceBid;
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
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_bid_view);
        PushReceiver.IsOtherScreenOpen =true;
        RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
        Window window = Alert_To_PlaceBid.this.getWindow();
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

        requestView_detailPojo = getIntent().getExtras().getParcelable("requestView_detailPojo");


        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);




        if (getIntent().getStringExtra("shipmentItems") != null)
            ((TextView) findViewById(R.id.bidItemTxtToPlaceBid)).setText(getIntent().getStringExtra("shipmentItems"));
        else
            ((TextView) findViewById(R.id.bidItemTxtToPlaceBid)).setText("-NA-");


        ((TextView) findViewById(R.id.pickAddress)).setText(requestView_detailPojo.getPickupSuburb());

        ((TextView) findViewById(R.id.dropAddress)).setText(requestView_detailPojo.getDropSuburb());


        ((TextView) findViewById(R.id.distanceTxtToPlaceBid)).setText(requestView_detailPojo.getDistance());

        String pickUpTime = "-NA-";
        if (!requestView_detailPojo.getPickUpDateTime().equals("") && !requestView_detailPojo.getPickUpDateTime().equals("null"))
            if (requestView_detailPojo.getPickUpDateTime().contains("\n"))
                pickUpTime = requestView_detailPojo.getPickUpDateTime().replace("\n", " ");
            else
                pickUpTime = requestView_detailPojo.getPickUpDateTime().replace("|", "");

        ((TextView) findViewById(R.id.pickTimePlaceBidList)).setText(pickUpTime);


        String dropOffTime = "-NA-";
        if (!requestView_detailPojo.getDropDateTime().equals("") && !requestView_detailPojo.getDropDateTime().equals("null"))
            if (requestView_detailPojo.getDropDateTime().contains("\n"))
                dropOffTime = requestView_detailPojo.getDropDateTime().replace("\n", " ");
            else
                dropOffTime = requestView_detailPojo.getDropDateTime().replace("|", "");

        ((TextView) findViewById(R.id.dropTimePlaceBidList)).setText(dropOffTime);



        if (requestView_detailPojo.isSuggestedPrice()) {
            ((TextView) findViewById(R.id.suggestPriceTxtToPlaceBid)).setText("Suggested price $" + requestView_detailPojo.getCourierPrice());
            findViewById(R.id.suggestPriceTxtToPlaceBid).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.suggestPriceTxtToPlaceBid).setVisibility(View.GONE);

        }

        View pickTimeVieToPlaceBid = findViewById(R.id.pickTimeVieToPlaceBid);

        ((TextView) pickTimeVieToPlaceBid.findViewById(R.id.pickUpTxtToPlaceBid)).setText("ETA for Pickup");

        dateValueTxtToPlaceBidToPick = (TextView) pickTimeVieToPlaceBid.findViewById(R.id.dateValueTxtToPlaceBid);


        timeValueTxtToPlaceBidToPick = (TextView) pickTimeVieToPlaceBid.findViewById(R.id.timeValueTxtToPlaceBid);


        View dropTimeVieToPlaceBid = findViewById(R.id.dropTimeVieToPlaceBid);

        ((TextView) dropTimeVieToPlaceBid.findViewById(R.id.pickUpTxtToPlaceBid)).setText("ETA for Dropoff");

        dateValueTxtToPlaceBidToDrop = (TextView) dropTimeVieToPlaceBid.findViewById(R.id.dateValueTxtToPlaceBid);


        timeValueTxtToPlaceBidToDrop = (TextView) dropTimeVieToPlaceBid.findViewById(R.id.timeValueTxtToPlaceBid);


    //    setDefaultPickTimeTo2PlusHour();

        setDefaultPickDropTime(pickUpTime, dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick);
        setDefaultPickDropTime(dropOffTime, dateValueTxtToPlaceBidToDrop, timeValueTxtToPlaceBidToDrop);


        bidActivePeriodSpinnerToPlaceBid = (Spinner) findViewById(R.id.bidActivePeriodSpinnerToPlaceBid);
        String[] pkgPosition = {"15 mins", "30 mins", "45 mins", "1 hour", "1 hour 15 mins", "1 hour 30 mins", "1 hour 45 mins", "2 hours", "4 hours", "8 hours", "12 hours", "24 hours"};
        ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(this, R.layout.spinneritemxml_white, pkgPosition) {
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
        pkgPosition = null;
        adapter_Position = null;
        bidActivePeriodSpinnerToPlaceBid.setOnItemSelectedListener(this);

        datePickerToSelectItemForDateField = new DateTimePickerView(Alert_To_PlaceBid.this);
        datePickerToSelectItemForDateField.setDateTimeTxtField(dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick, dateValueTxtToPlaceBidToDrop, timeValueTxtToPlaceBidToDrop);

        dateValueTxtToPlaceBidToPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerToSelectItemForDateField.datePickerDialog(dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick, 1);
            }
        });

        timeValueTxtToPlaceBidToPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerToSelectItemForDateField.timePickerDialog(timeValueTxtToPlaceBidToPick, dateValueTxtToPlaceBidToPick, 1);
            }
        });

        dateValueTxtToPlaceBidToDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerToSelectItemForDateField.datePickerDialog(dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick, 0);
            }
        });

        timeValueTxtToPlaceBidToDrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerToSelectItemForDateField.timePickerDialog(timeValueTxtToPlaceBidToPick, dateValueTxtToPlaceBidToPick, 0);
            }
        });

        edtPricrTxt = (EditText) findViewById(R.id.edtPricrTxt);
        edtPricrTxt.requestFocus();

        InputMethodManager imm = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        edtNotesTxtToPlaceBid = (EditText) findViewById(R.id.edtNotesTxtToPlaceBid);



        findViewById(R.id.placeBidBtnToPlaceBid).setOnClickListener(this);
    }

    private void setDefaultPickDropTime(String givenPickDropTimeFromCustomer, TextView dateValueTxtToPlaceBidToPickDrop, TextView timeValueTxtToPlaceBidToPickDrop) {
        try {
            String[] setDateTime = givenPickDropTimeFromCustomer.split(" ");
            if (setDateTime.length > 3)
                timeValueTxtToPlaceBidToPickDrop.setText(setDateTime[2] + " " + setDateTime[3]);
            else
                timeValueTxtToPlaceBidToPickDrop.setText(setDateTime[1] + " " + setDateTime[2]);

            String defaultDateTime = "";
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
                Date convertedTime = new Date();
                convertedTime = sdf.parse(setDateTime[0]);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
                defaultDateTime = dateFormatter.format(convertedTime);
                sdf = null;
                convertedTime = null;
                dateFormatter = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            dateValueTxtToPlaceBidToPickDrop.setText(defaultDateTime);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaultPickTimeTo2PlusHour(){
       try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy hh:mm a");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR, 2);
            String defaultPickupTime = sdf.format(calendar.getTime());

           calendar = null;
           setDefaultPickDropTime(defaultPickupTime, dateValueTxtToPlaceBidToPick, timeValueTxtToPlaceBidToPick);

           Date date = sdf.parse(defaultPickupTime);
           calendar = Calendar.getInstance();
           calendar.setTime(date);
           calendar.add(Calendar.HOUR, 1);
           String defaultDropoffTime = sdf.format(calendar.getTime());

           setDefaultPickDropTime(defaultDropoffTime, dateValueTxtToPlaceBidToDrop, timeValueTxtToPlaceBidToDrop);
            calendar = null;
            sdf = null;
       } catch (Exception e) {
            e.printStackTrace();
       }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.placeBidBtnToPlaceBid:
                courierPriceStr = edtPricrTxt.getText().toString();
                notesStr = edtNotesTxtToPlaceBid.getText().toString();

                String selectPickUpEtaDateStr, selectDropOffEtaDateStr, selectPickUpETATimeSTr, selectDropOffETATimeSTr;

                selectPickUpEtaDateStr = dateValueTxtToPlaceBidToPick.getText().toString();
                selectPickUpETATimeSTr = returnSelectedPickOrDropTime(timeValueTxtToPlaceBidToPick.getText().toString());

                selectDropOffEtaDateStr = dateValueTxtToPlaceBidToDrop.getText().toString();
                selectDropOffETATimeSTr = returnSelectedPickOrDropTime(timeValueTxtToPlaceBidToDrop.getText().toString());

                //************** Convert selected pick or drop date and time ***********
                String pickupETADateTime = selectPickUpEtaDateStr + "T" + selectPickUpETATimeSTr;
                String dropOffETADateTime = selectDropOffEtaDateStr + "T" + selectDropOffETATimeSTr;
                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
                Date pickUpConvertedDate = new Date();
                Date dropOffConvertedDate = new Date();
                try {
                    pickUpConvertedDate = sdf.parse(pickupETADateTime);
                    dropOffConvertedDate = sdf.parse(dropOffETADateTime);
                    sdf = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                etaTimeForPickup = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(pickUpConvertedDate);
                pickUpConvertedDate = null;
                etaTimeForDropoff = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(dropOffConvertedDate);
                pickUpConvertedDate = null;
                //*****************************************************************

                //**************** Old Bid date time conversion ********************
//                etaTimeForPickup = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromDeviceForQuoteRequestETA(dateValueTxtToPlaceBidToPick.getText().toString()+" "+timeValueTxtToPlaceBidToPick.getText().toString());
//                etaTimeForDropoff = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromDeviceForQuoteRequestETA(dateValueTxtToPlaceBidToDrop.getText().toString()+" "+timeValueTxtToPlaceBidToDrop.getText().toString());
                //**************** Old Bid date time work ********************

                if (courierPriceStr.equals(""))
                    Toast.makeText(Alert_To_PlaceBid.this, "Please enter price first", Toast.LENGTH_LONG).show();
                else
                    apiCallToPlaceBidRequest();
                LoginZoomToU.imm.hideSoftInputFromWindow(edtPricrTxt.getWindowToken(), 0);
                break;
            case R.id.backFromBookingDetail:
                finish();
                break;
        }
    }

    //************** Return selected pickup and dropoff time ***********
    private String returnSelectedPickOrDropTime(String pickOrDropTimeStr) {
        String selectPickUpETATimeSTr = "";
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
            Date convertedTime = new Date();
            convertedTime = sdf1.parse(pickOrDropTimeStr);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss.SSS");
            selectPickUpETATimeSTr = dateFormatter.format(convertedTime);
            sdf1 = null;
            convertedTime = null;
            dateFormatter = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectPickUpETATimeSTr;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //************  API call to place bid request ********
    private void apiCallToPlaceBidRequest(){
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            PlaceBidAsyncTask();
            //new PlaceBidAsyncTask().execute();
        else
            DialogActivity.alertDialogView(Alert_To_PlaceBid.this, "No network!", "No network connection, Please try again later.");
    }

    private void PlaceBidAsyncTask(){

        final String[] webAcceptOffers = {""};
        new MyAsyncTasks(){
        @Override
        public void onPreExecute() {
            try {
                if (progressToPlaceBid == null)
                    progressToPlaceBid = new ProgressDialog(Alert_To_PlaceBid.this);
                Custom_ProgressDialogBar.inItProgressBar(progressToPlaceBid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void doInBackground() {
            try {
                WebserviceHandler webserviceHandler = new WebserviceHandler();
                webAcceptOffers[0] = webserviceHandler.getCourierMyAcceptOffer(requestView_detailPojo.getOfferId(), courierPriceStr,
                        etaTimeForPickup, etaTimeForDropoff, notesStr, bidActivePeriodInterval);

                webserviceHandler = null;
            } catch (Exception e) {
                e.printStackTrace();
                webAcceptOffers[0] = "";
            }
        }

        @Override
        public void onPostExecute() {
            Custom_ProgressDialogBar.dismissProgressBar(progressToPlaceBid);
            try {
                if(!webAcceptOffers[0].equals("")){
                    JSONObject jObjOfOfferBid = new JSONObject(webAcceptOffers[0]);
                    if (jObjOfOfferBid.getBoolean("success")) {
                        RequestView.COUNT_FOR_NOTBIDYET--;
                        SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
                        if (getIntent().getIntExtra("isFromBidDetail", 0) == 2) {
                            new LoadChatBookingArray(Alert_To_PlaceBid.this, 2);  //********* Refresh Bid chat only *******
                            finish();
                        } else
                            backToRequestListView();
                        Toast.makeText(Alert_To_PlaceBid.this, "Bid placed successfully", Toast.LENGTH_LONG).show();
                    }else
                        DialogActivity.alertDialogView(Alert_To_PlaceBid.this, "Alert!", jObjOfOfferBid.getString("message"));
                }else
                    DialogActivity.alertDialogView(Alert_To_PlaceBid.this, "Error!", "Bid not placed, try again");
            } catch (Exception e) {
                e.printStackTrace();
                DialogActivity.alertDialogView(Alert_To_PlaceBid.this, "Server error!", "Something went wrong, please try again");
            }
        }
    }.execute();
}


    //********** Redirect to Request list *********
    private void backToRequestListView() {
        RequestView.newBidSelection = 1;
        ConfirmPickUpForUserName.isDropOffSuccessfull = 13;
        Intent callCompleteBookingfragment = new Intent(Alert_To_PlaceBid.this, SlideMenuZoom2u.class);
        callCompleteBookingfragment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(callCompleteBookingfragment);
        finish();
        callCompleteBookingfragment = null;
    }

}
