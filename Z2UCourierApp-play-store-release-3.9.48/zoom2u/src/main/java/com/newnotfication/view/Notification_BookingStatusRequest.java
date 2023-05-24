package com.newnotfication.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Calendar;

public class Notification_BookingStatusRequest extends Activity implements View.OnClickListener {

    private ConstraintLayout bookingStatusRequestView;
    private TextView customerNameValueStr_BSR;
    private TextView pickupSuburbValueTxt_BSR;
    private TextView dropOffTimeValueTxt_BSR;
    private TextView dropOffSuburbValueTxt_BSR;
    private Button lateBtn_BSR;
    private Button onTimeBtn_BSR;
    private ConstraintLayout updateETAView;
    private TextView selectTimeValue_BSR_For_ETA;
    private EditText edtNotesValueTxt_BSR_For_ETA;
    private Button backBtn_BSR_For_ETA;
    private Button updateETABtn_BSR_For_ETA;

    private int pHour, pMinute;
    private String strHrsToShow, uploadedDateTimeETAStr;

    ProgressDialog currentProgressDialog;
    private String courierNotes;

    private int lateDeliveryId = 0;
    private JSONObject jObjOfLateDelivery_Details;

    @Override
    synchronized protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        inItViewContents(null, intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_booking_status_request);

        inItViewContents(savedInstanceState, getIntent());
    }

    private void inItViewContents(Bundle savedInstanceState, Intent intent) {

        bookingStatusRequestView = (ConstraintLayout) findViewById(R.id.bookingStatusRequestView);
        customerNameValueStr_BSR = (TextView) findViewById(R.id.customerNameValueStr_BSR);
        pickupSuburbValueTxt_BSR = (TextView) findViewById(R.id.pickupSuburbValueTxt_BSR);
        dropOffTimeValueTxt_BSR = (TextView) findViewById(R.id.dropOffTimeValueTxt_BSR);
        dropOffSuburbValueTxt_BSR = (TextView) findViewById(R.id.dropOffSuburbValueTxt_BSR);
        lateBtn_BSR = (Button) findViewById(R.id.lateBtn_BSR);
        onTimeBtn_BSR = (Button) findViewById(R.id.onTimeBtn_BSR);

        updateETAView = (ConstraintLayout) findViewById(R.id.updateETAView);
        selectTimeValue_BSR_For_ETA = (TextView) findViewById(R.id.selectTimeValue_BSR_For_ETA);
        edtNotesValueTxt_BSR_For_ETA = (EditText) findViewById(R.id.edtNotesValueTxt_BSR_For_ETA);
        backBtn_BSR_For_ETA = (Button) findViewById(R.id.backBtn_BSR_For_ETA);
        updateETABtn_BSR_For_ETA = (Button) findViewById(R.id.updateETABtn_BSR_For_ETA);

        bookingStatusRequestView.setVisibility(View.VISIBLE);
        updateETAView.setVisibility(View.GONE);

        lateBtn_BSR.setOnClickListener(this);
        onTimeBtn_BSR.setOnClickListener(this);
        selectTimeValue_BSR_For_ETA.setOnClickListener(this);
        backBtn_BSR_For_ETA.setOnClickListener(this);
        updateETABtn_BSR_For_ETA.setOnClickListener(this);

        try {
            lateDeliveryId = Integer.parseInt(PushReceiver.prefrenceForPushy.getString("LateDeliveryId", ""));
            if (lateDeliveryId != 0) {
                Get_LateDeliveryDetail();
            } else
                finish();
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        removeLateDeliveryDataFrom_Preference();
    }

    private void removeLateDeliveryDataFrom_Preference() {
        PushReceiver.loginEditorForPushy.remove("LateDeliveryId");
        PushReceiver.loginEditorForPushy.apply();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.lateBtn_BSR:  {
                bookingStatusRequestView.setVisibility(View.GONE);
                updateETAView.setVisibility(View.VISIBLE);
                break;
            }

            case R.id.onTimeBtn_BSR:   {
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                   OnTimeOrLateDelivery_CourierResponse("1");
                else
                    Toast.makeText(Notification_BookingStatusRequest.this, "No network connection, Please try again later.", Toast.LENGTH_LONG).show();
                break;
            }

            case R.id.selectTimeValue_BSR_For_ETA:    {
                /**Get the current time */
                final Calendar cal = Calendar.getInstance();
                pHour = cal.get(Calendar.HOUR_OF_DAY);
                pMinute = cal.get(Calendar.MINUTE);
                showDialog(ActiveBookingDetail_New.TIME_DIALOG_ID);
                break;
            }

            case R.id.backBtn_BSR_For_ETA:    {
                bookingStatusRequestView.setVisibility(View.VISIBLE);
                updateETAView.setVisibility(View.GONE);
                break;
            }

            case R.id.updateETABtn_BSR_For_ETA:    {
                courierNotes = edtNotesValueTxt_BSR_For_ETA.getText().toString();
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    OnTimeOrLateDelivery_CourierResponse("0");
                else
                    Toast.makeText(Notification_BookingStatusRequest.this, "No network connection, Please try again later.", Toast.LENGTH_LONG).show();
                break;
            }
        }

    }

    private void dismissProgressDialog() {
        try {
            if (currentProgressDialog != null)
                if (currentProgressDialog.isShowing())
                    Custom_ProgressDialogBar.dismissProgressBar(currentProgressDialog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItProgressView() {
        Custom_ProgressDialogBar.inItProgressBar(currentProgressDialog);
    }

    private void Get_LateDeliveryDetail(){
        final String[] getLateDeliveryDetail_ResponseStr = {"0"};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (currentProgressDialog != null)
                        if (currentProgressDialog.isShowing())
                            currentProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                currentProgressDialog = null;

                currentProgressDialog = new ProgressDialog(Notification_BookingStatusRequest.this);
                inItProgressView();
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    getLateDeliveryDetail_ResponseStr[0] = webServiceHandler.getLateDeliveryBookingDetail(lateDeliveryId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (LoginZoomToU.isLoginSuccess == 0) {

                        try {
                            jObjOfLateDelivery_Details = new JSONObject(getLateDeliveryDetail_ResponseStr[0]);

                            try {
                                if (!jObjOfLateDelivery_Details.getString("customerName").equals(""))
                                    customerNameValueStr_BSR.setText(jObjOfLateDelivery_Details.getString("customerName"));
                                else
                                    customerNameValueStr_BSR.setText("-NA-");
                            } catch (JSONException e) {
                                e.printStackTrace();
                                customerNameValueStr_BSR.setText("-NA-");
                            }

                            pickupSuburbValueTxt_BSR.setText(jObjOfLateDelivery_Details.getString("pickupSuburb"));
                            dropOffSuburbValueTxt_BSR.setText(jObjOfLateDelivery_Details.getString("dropSuburb"));

                            edtNotesValueTxt_BSR_For_ETA.setText("Delivery from "+jObjOfLateDelivery_Details.getString("pickupSuburb")+" to "+jObjOfLateDelivery_Details.getString("dropSuburb"));

                            try {
                                String[] dropTime = LoginZoomToU.checkInternetwithfunctionality.serverDateTimeToDevice_TeamDetails(jObjOfLateDelivery_Details.getString("dropDateTime")).split(" ");
                                dropOffTimeValueTxt_BSR.setText(dropTime[1]+" "+dropTime[2].toLowerCase());
                                selectTimeValue_BSR_For_ETA.setText(dropTime[1]+" "+dropTime[2].toLowerCase());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                dropOffTimeValueTxt_BSR.setText("--");
                                selectTimeValue_BSR_For_ETA.setText("--");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DialogActivity.alertDialogToFinishView(Notification_BookingStatusRequest.this, "Error!", "Something went wrong here.");
                        }
                    } else
                        DialogActivity.alertDialogToFinishView(Notification_BookingStatusRequest.this, "Error!", "Something went wrong here.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogToFinishView(Notification_BookingStatusRequest.this, "Server Error!", "Something went wrong here.");
                } finally {
                    dismissProgressDialog();
                }
            }
        }.execute();
    }


    private void OnTimeOrLateDelivery_CourierResponse(String arg){
        final String[] onTimeOrLateDelivery_ResponseStr = {"0"};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (currentProgressDialog != null)
                        if (currentProgressDialog.isShowing())
                            currentProgressDialog.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                currentProgressDialog = null;

                currentProgressDialog = new ProgressDialog(Notification_BookingStatusRequest.this);
                inItProgressView();
            }

            @Override
            public void doInBackground() {
                try {
                    JSONObject jObjOfLateDeliveryCourierResponse = new JSONObject();
                    //    int isOntime = Integer.parseInt(arg[0]);
                    if (arg.equals("1")) {
                        jObjOfLateDeliveryCourierResponse.put("BookingId", lateDeliveryId);
                        jObjOfLateDeliveryCourierResponse.put("OnTime", arg);
                    } else {
                        jObjOfLateDeliveryCourierResponse.put("BookingId", lateDeliveryId);
                        jObjOfLateDeliveryCourierResponse.put("OnTime", "0");
                        jObjOfLateDeliveryCourierResponse.put("ETA", uploadedDateTimeETAStr);
                        jObjOfLateDeliveryCourierResponse.put("Notes", courierNotes);
                    }
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    onTimeOrLateDelivery_ResponseStr[0] = webServiceHandler.ontimeOrLate_BSR_CourierRespone(jObjOfLateDeliveryCourierResponse.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (onTimeOrLateDelivery_ResponseStr[0].equals("success")) {
                        finish();
                        Toast.makeText(Notification_BookingStatusRequest.this, "Request saved successfully.", Toast.LENGTH_LONG).show();
                    } else
                        DialogActivity.alertDialogToFinishView(Notification_BookingStatusRequest.this, "Server Error!", "Something went wrong, Please try again later.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogToFinishView(Notification_BookingStatusRequest.this, "Server Error!", "Something went wrong, Please try again later.");
                } finally {
                    dismissProgressDialog();
                }
            }
        }.execute();
    }



    /** Create a new dialog for time picker */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case ActiveBookingDetail_New.TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, pHour, pMinute, false);
        }
        return null;
    }

    /** Callback received when the user "picks" a time in the dialog */
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
                        am_pm = "am";
                    else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                        am_pm = "pm";

                    strHrsToShow = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+"";
                    strHrsToShow = strHrsToShow+":"+datetime.get(Calendar.MINUTE)+" "+am_pm;
                    updateDisplay();
                }
            };

    private void updateDisplay() {
        String dateTimeStr = null;
        try {
            String[] dateStr = jObjOfLateDelivery_Details.getString("dropDateTime").split("T");
            dateTimeStr = dateStr[0]+" "+strHrsToShow;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            if(uploadedDateTimeETAStr != null)
                uploadedDateTimeETAStr = null;
            uploadedDateTimeETAStr = LoginZoomToU.checkInternetwithfunctionality.returnDateToShowBookingStatusRequest(dateTimeStr);

            String updateTimeUIField = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServerTo_BookingStatusRequestUI(uploadedDateTimeETAStr);
            selectTimeValue_BSR_For_ETA.setText(updateTimeUIField.toLowerCase());
        } catch (Exception e) {
            e.printStackTrace();
            selectTimeValue_BSR_For_ETA.setText("--");
        }
    }
}
