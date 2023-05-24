package com.zoom2u.slidemenu.accountdetail_section;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.AccountDetailFragment;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;
import org.json.JSONObject;

/**
 * Created by Arun on 19-Oct-2018.
 */

public class SettingView extends Activity implements View.OnClickListener {

    TextView countChatSettingView;

    TextView checkedTextView, addMarginTxt, dollerTxtSettingView;
    CheckBox checkForNotifyMembers;
    EditText marginValueTxtToSettingVew;
    Button updateBtnSettings;

    String marginvalue;
    boolean isNotifyChecked = true;
    ProgressDialog progressInSettingView;
    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatSettingView);
        SlideMenuZoom2u.countChatBookingView = countChatSettingView;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingview_myprofile);

        RelativeLayout headerSummaryReportLayout=findViewById(R.id.profileSettingTitleBarLayout);
        Window window = SettingView.this.getWindow();
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


        //********** In-it Title bar contents ***********//


        findViewById(R.id.backBtnHeader).setOnClickListener(this);
        findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);
        countChatSettingView = findViewById(R.id.countChatBookingDetail);
        countChatSettingView.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatSettingView;

        checkedTextView = (TextView) findViewById(R.id.checkedTextView);

        addMarginTxt = (TextView) findViewById(R.id.addMarginTxt);
        dollerTxtSettingView = (TextView) findViewById(R.id.dollerTxtSettingView);
        checkForNotifyMembers = (CheckBox) findViewById(R.id.checkForNotifyMembers);
        marginValueTxtToSettingVew = (EditText) findViewById(R.id.marginValueTxtToSettingVew);
        updateBtnSettings = (Button) findViewById(R.id.updateBtnSettings);
        updateBtnSettings.setOnClickListener(this);

        if (getIntent().getDoubleExtra("Margin", 0.0) >= 100)
            marginvalue = String.valueOf((int) getIntent().getDoubleExtra("Margin", 0.0));
        else
            marginvalue = String.valueOf((int) getIntent().getDoubleExtra("Margin", 0.0));
        isNotifyChecked = getIntent().getBooleanExtra("AllowMemberNotify", false);

        checkForNotifyMembers.setChecked(isNotifyChecked);
        if (!marginvalue.equals("0"))
            marginValueTxtToSettingVew.setText(marginvalue);

        if (marginValueTxtToSettingVew.getText().toString().equals(""))
            marginValueTxtToSettingVew.setText("0");
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        LoginZoomToU.imm.hideSoftInputFromWindow(marginValueTxtToSettingVew.getWindowToken(), 0);
        AccountDetailFragment.OPEN_SETTING_VIEW = 4;
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backBtnHeader:
                AccountDetailFragment.OPEN_SETTING_VIEW = 4;
                finish();
                break;

            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(SettingView.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;

            case R.id.updateBtnSettings:
                LoginZoomToU.imm.hideSoftInputFromWindow(marginValueTxtToSettingVew.getWindowToken(), 0);
                marginvalue = marginValueTxtToSettingVew.getText().toString();
                if (marginvalue.equals(""))
                    marginvalue = "0";
                isNotifyChecked = checkForNotifyMembers.isChecked();
                if (Double.parseDouble(marginvalue) < 100) {
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        UpdateSettingsAsyncTask();
                        // new UpdateSettingsAsyncTask().execute();
                    else
                        DialogActivity.alertDialogView(SettingView.this, "No Network!", "No Network connection, Please try again later.");
                } else {
                    Toast.makeText(SettingView.this, "Margin should be less than 100", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void UpdateSettingsAsyncTask(){

        final JSONObject[] jOBJForRequestPick = new JSONObject[1];
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try{
                    if(progressInSettingView != null)
                        if(progressInSettingView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressInSettingView);
                }catch(Exception e){
                    e.printStackTrace();
                }
                try {
                    if(progressInSettingView != null)
                        progressInSettingView = null;
                    progressInSettingView = new ProgressDialog(SettingView.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressInSettingView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    try {
                        String responseDataForAttemptOrDispatchStr = "";
                        responseDataForAttemptOrDispatchStr = webServiceHandler.courierSettingUpdates(Double.parseDouble(marginvalue), isNotifyChecked);
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
                if(progressInSettingView != null)
                    if(progressInSettingView.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressInSettingView);

                try {
                    if(LoginZoomToU.isLoginSuccess == 0){
                        Toast.makeText(SettingView.this, "Updated successfully", Toast.LENGTH_LONG).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else
                        DialogActivity.alertDialogView(SettingView.this, "Sorry!", "Something went wrong, Please try again later");
                }catch(Exception e){
                    e.printStackTrace();
                    DialogActivity.alertDialogView(SettingView.this, "Sorry!", "Something went wrong, Please try again later");
                }
            }
        }.execute();
    }


}
