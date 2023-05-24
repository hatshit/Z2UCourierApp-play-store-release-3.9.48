package com.zoom2u.slidemenu.driversupport;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.offerrequesthandlr.DateTimePickerView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

/**
 * Created by Akansha jain on 06/04/17.
 */

public class DriverSupport extends Fragment implements View.OnClickListener {

    View rootView = null;

    TextView dateTxtdetail;
    EditText howOftenTxtdetail, describeTxtdetail;
    TextView headerChatIcon,howOftenTxt;

    String appVersion, deviceModel;
    DateTimePickerView dateTimePickerView;
    ProgressDialog progressDialogDriverSPrtView;
    public void setSlideMenuChatCounterTxt(TextView slideMenuTxt){
        this.headerChatIcon = slideMenuTxt;
    }

    @Override
    public void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        SlideMenuZoom2u.countChatBookingView = headerChatIcon;
        Model_DeliveriesToChat.showExclamationForUnreadChat(headerChatIcon);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         rootView = inflater.inflate(R.layout.driversupport, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        ActiveBookingView.getCurrentLocation(getActivity());

        //to get app version
        PackageManager manager = getActivity().getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        appVersion = info.versionName;
        deviceModel = Build.MODEL +" "+ Build.MANUFACTURER+" "+Build.VERSION.RELEASE;

        inItDriverSupportView();
        return rootView;
    }

    void inItDriverSupportView(){

        rootView.findViewById(R.id.linearDate).requestFocus();

        if (MainActivity.isIsBackGroundGray()) {
            rootView.findViewById(R.id.sendButton).setBackgroundResource(R.drawable.chip_background_gray);
        } else {
            rootView.findViewById(R.id.sendButton).setBackgroundResource(R.drawable.chip_background);

        }

        rootView.findViewById(R.id.contactUsTxt);
        rootView.findViewById(R.id.liveChatTxt);

        rootView.findViewById(R.id.liveChatTxtClick);
        rootView.findViewById(R.id.liveChatTxtClick).setOnClickListener(this);

        rootView.findViewById(R.id.phoneTxt);
        rootView.findViewById(R.id.phoneTxtdetail);
        ((TextView) rootView.findViewById(R.id.phoneTxtdetail)).setOnClickListener(this);
        rootView.findViewById(R.id.emailTxt);
        rootView.findViewById(R.id.emailTxtdetail);
        rootView.findViewById(R.id.reportAnIssueTxt);
        rootView.findViewById(R.id.DAteTxt);
        howOftenTxt=rootView.findViewById(R.id.howOftenTxt);


        if(dateTxtdetail == null)
            dateTxtdetail = (TextView) rootView.findViewById(R.id.dateTxtdetail);

        dateTxtdetail.setOnClickListener(this);

        if(howOftenTxtdetail == null)
            howOftenTxtdetail = (EditText) rootView.findViewById(R.id.howOftenTxtdetail);


        if(describeTxtdetail == null)
            describeTxtdetail = (EditText) rootView.findViewById(R.id.describeTxtdetail);


        rootView.findViewById(R.id.sendButton);
        ((TextView) rootView.findViewById(R.id.sendButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConetntToServer();
            }
        });
        howOftenTxt.setVisibility(View.INVISIBLE);
        setEdtFieldBGtoGray(howOftenTxtdetail, howOftenTxt);
        describeTxtdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.describeTxtdetail) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });
    }
    private void setEdtFieldBGtoGray(final EditText edtFieldAddEditMember, final TextView txtFieldAddEditMember) {
        edtFieldAddEditMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

                    txtFieldAddEditMember.setVisibility(View.INVISIBLE);
                } else {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
                    txtFieldAddEditMember.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //************** Report issue on Send button *************//
    private void sendConetntToServer() {
        if (!dateTxtdetail.getText().toString().equals("") && !howOftenTxtdetail.getText().toString().equals("")
                && !describeTxtdetail.getText().toString().equals("")) {
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                ReportedIssueAsyncTask();
                // new ReportedIssueAsyncTask().execute();
            else
                DialogActivity.alertDialogView(getActivity(), "No Network!", "No network connection, Please try again later.");
        } else
            DialogActivity.alertDialogView(getActivity(), "Alert!", "Please fill all the fields.");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dateTxtdetail:
                dateTimePickerView = new DateTimePickerView(getActivity(), 1);
                dateTimePickerView.datePickerDialog(dateTxtdetail, null, -1);
                break;
            case R.id.liveChatTxtClick:
                callAdminChatDetail();
                break;
            case R.id.phoneTxtdetail:
                Intent inten = new Intent(Intent.ACTION_DIAL);
                inten.setData(Uri.parse("tel:"+"1300 318 675"));
                startActivity(inten);
                inten = null;
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
    //************ Open admin chat detail page ***************
    //************ Open admin chat detail page ***************
    private void callAdminChatDetail() {
        Bundle bModelDeliveryChat = new Bundle();
        if (LoadChatBookingArray.arrayOfChatDelivery != null) {
            if (LoadChatBookingArray.arrayOfChatDelivery.size() > 0)
                bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", LoadChatBookingArray.arrayOfChatDelivery.get(0));
            else
                bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", new Model_DeliveriesToChat(getActivity(), LoginZoomToU.courierID, "Zoom2u-Admin"));
        } else
            bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", new Model_DeliveriesToChat(getActivity(), LoginZoomToU.courierID, "Zoom2u-Admin"));

        Intent chatDetailViewIntent = new Intent(getActivity(), ChatDetailActivity.class);
        chatDetailViewIntent.putExtras(bModelDeliveryChat);
        startActivity(chatDetailViewIntent);
        chatDetailViewIntent = null;
        bModelDeliveryChat = null;
    }
    private void ReportedIssueAsyncTask(){
        final boolean[] isSuccessToReportIssue = {false};
        final JSONObject[] jObjOfDrivrSprtView = new JSONObject[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialogDriverSPrtView != null)
                    progressDialogDriverSPrtView = null;
                progressDialogDriverSPrtView = new ProgressDialog(getActivity());
                Custom_ProgressDialogBar.inItProgressBar(progressDialogDriverSPrtView);

                jObjOfDrivrSprtView[0] = new JSONObject();
                try {
                    dateTimePickerView.selectedDateTimeForDriverSupport = dateTimePickerView.selectedDateTimeForDriverSupport.replace(".", "");

                    jObjOfDrivrSprtView[0].put("HowOftenItHappens", howOftenTxtdetail.getText().toString());
                    jObjOfDrivrSprtView[0].put("IssueReportedDateTime", LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromDeviceForDeliveryETA(dateTimePickerView.selectedDateTimeForDriverSupport));
                    jObjOfDrivrSprtView[0].put("Description", describeTxtdetail.getText().toString());
                    jObjOfDrivrSprtView[0].put("AppVersion", appVersion);
                    jObjOfDrivrSprtView[0].put("DeviceType", "Android");
                    jObjOfDrivrSprtView[0].put("DeviceModel", deviceModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("")
                            && !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0"))
                        jObjOfDrivrSprtView[0].put("CurrentLocation", Functional_Utility.getAddressFromDriverLatLong(getActivity().getApplicationContext(), LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude));
                    else
                        jObjOfDrivrSprtView[0].put("CurrentLocation", "");

                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    String responseReportIssueStr = "";
                    responseReportIssueStr = webServiceHandler.sendDriverReportedIssue(jObjOfDrivrSprtView[0].toString());

                    JSONObject jOBJResForeportIssue = new JSONObject(responseReportIssueStr);
                    isSuccessToReportIssue[0] = jOBJResForeportIssue.getBoolean("success");
                    jOBJResForeportIssue = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressDialogDriverSPrtView != null)
                        if (progressDialogDriverSPrtView.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogDriverSPrtView);

                    if (isSuccessToReportIssue[0]) {
                        dateTxtdetail.setText("");
                        howOftenTxtdetail.setText("");
                        describeTxtdetail.setText("");
                        dateTimePickerView = null;
                        //DialogActivity.alertDialogView(getActivity(), "Send successfully!", "Issue reported successfully.");
                        Toast.makeText(getActivity(), "Issue reported to Driver Support Team.", Toast.LENGTH_LONG).show();
                    } else
                        DialogActivity.alertDialogView(getActivity(), "Sorry!", "Failed to report issue, Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();

    }



}
