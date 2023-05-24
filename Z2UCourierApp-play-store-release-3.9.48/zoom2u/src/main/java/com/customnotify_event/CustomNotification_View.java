package com.customnotify_event;

import android.app.Activity;
import android.app.ProgressDialog;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONException;
import org.json.JSONObject;

public class CustomNotification_View extends Activity implements View.OnClickListener {

    private ImageView customNotificationImg;
    private TextView customNotifyTxt;
    private ProgressDialog progressForCustomView;
    private boolean isRepliedYes = false;

    JSONObject jObjForCustomNoticationData;
    int eventIdCustomNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_notification_view);

        try {
            jObjForCustomNoticationData = new JSONObject(getIntent().getStringExtra("CustomNotifiedData"));
            eventIdCustomNotification = getIntent().getIntExtra("BookingID", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        LoginZoomToU.loginEditor.putBoolean("isRepliedCustomNotification", false);
//        LoginZoomToU.loginEditor.putString("CustomNotificationData", jObjForCustomNoticationData.toString());
//        LoginZoomToU.loginEditor.putInt("EventIdCustomNotification", eventIdCustomNotification);
//        LoginZoomToU.loginEditor.commit();



        customNotificationImg = (ImageView) findViewById(R.id.customNotificationImg);
        customNotifyTxt = (TextView) findViewById(R.id.customNotifyTxt);

        // changes by geet
        if (jObjForCustomNoticationData != null) {
            try {
                Picasso.with(this).load(jObjForCustomNoticationData.getString("ImageUrl")).into(customNotificationImg);
                customNotifyTxt.setText(jObjForCustomNoticationData.getString("Question"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // changes by geet

        findViewById(R.id.noBtnCustomNotification).setOnClickListener(this);

        findViewById(R.id.yesBtnCustomNotification).setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.noBtnCustomNotification:
                isRepliedYes = false;
                repliedForCustomEvent();
                break;
            case R.id.yesBtnCustomNotification:
                isRepliedYes = true;
                repliedForCustomEvent();
                break;
        }
    }

    private void repliedForCustomEvent() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            RepliedYesOrNoFor_CustomEvent();
        else
            DialogActivity.alertDialogView(CustomNotification_View.this, "No Network!", "No network connection, Please try again later.");
    }

    private void RepliedYesOrNoFor_CustomEvent(){
        final String[] eventRepliedResponseStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForCustomView == null)
                        progressForCustomView = new ProgressDialog(CustomNotification_View.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressForCustomView);
                    ActiveBookingView.getCurrentLocation(CustomNotification_View.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webserviceHandler = new WebserviceHandler();
                    eventRepliedResponseStr[0] = webserviceHandler.repliedCustomNotification(jObjForCustomNoticationData.getInt("NotificationId"), eventIdCustomNotification, isRepliedYes);
                    webserviceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    eventRepliedResponseStr[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressForCustomView);
                try {
                    if (!eventRepliedResponseStr[0].equals("")) {
                        JSONObject jObjOfEveentRepliedResponse = new JSONObject(eventRepliedResponseStr[0]);
                        if (jObjOfEveentRepliedResponse.getBoolean("success"))
                            finish();
                        else
                            DialogActivity.alertDialogToFinishView(CustomNotification_View.this, "Failed!", jObjOfEveentRepliedResponse.getString("message"));
                    } else
                        DialogActivity.alertDialogToFinishView(CustomNotification_View.this, "Error!", "Something went wrong here.");
                } catch (Exception e) {
                    DialogActivity.alertDialogToFinishView(CustomNotification_View.this, "Error!", "Something went wrong here.");
                    e.printStackTrace();
                }
            }
        }.execute();
    }



}
