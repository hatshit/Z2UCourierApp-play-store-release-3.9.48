package com.zoom2u;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.newnotfication.view.NewBidRequest_NotificationView;
import com.newnotfication.view.NewBooking_Notification;
import com.newnotfication.view.Notification_BookingStatusRequest;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.weeklycourierstats.CallWeeklyCourierStats;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.dialogactivity.AchievementNotifyView;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DepotAlert_For_DHLDrivers;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogOutstandingBidNotification;
import com.zoom2u.dialogactivity.Notification_Courier_Level;
import com.zoom2u.offer_run_batch.SingleRunActivity;
import com.zoom2u.offer_run_batch.RunBatchActivity;
import com.zoom2u.onboardpopup.OnBoardActivity;
import com.zoom2u.services.GetKillEvent_ToDestryNotication;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceUpdateDeviceToken;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

import me.pushy.sdk.Pushy;

public class Courier_AutoLoginFeature {

    Context currentActivityContext;
    boolean isLogin = false;
    String userName, password;
    ProgressBar progressBar;
    Button termBtnAccept;
    ProgressDialog progressForTC;
    public Courier_AutoLoginFeature(){}

    public Courier_AutoLoginFeature(Context currentActivityContext){
        this.currentActivityContext = currentActivityContext;
        try {
            // get username or password from preference
            isLogin = LoginZoomToU.prefrenceForLogin.getBoolean("islogin", false);
            userName = LoginZoomToU.prefrenceForLogin.getString("username", null);
            password = LoginZoomToU.prefrenceForLogin.getString("password", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********** Store login credential to preference after login ************//
    public void storeCredentialAfterLogin(boolean isLoggedInBefore, String userName, String password, String loginToken, int carrierId, boolean isTeamLeader){
        isLogin = isLoggedInBefore;
        LoginZoomToU.loginEditor.putBoolean("islogin", isLoggedInBefore);
        LoginZoomToU.loginEditor.putString("username", userName);
        LoginZoomToU.loginEditor.putString("password", password);
        LoginZoomToU.loginEditor.putString("accessToken", loginToken);
        LoginZoomToU.loginEditor.putInt("CarrierId", carrierId);
        LoginZoomToU.loginEditor.putBoolean("isTeamLeader", isTeamLeader);
        LoginZoomToU.loginEditor.commit();
    }

    //*********** Switch to Booking view after login ************//
    public void loggedInSuccessfully(boolean isFromLoginScreen,boolean isFirstLogin){
        LoginZoomToU.isLoginSuccess = 0;
        BookingView.bookingViewSelection = 1;
        LoginZoomToU.editorForLogout.putBoolean("isLogout", false);
        LoginZoomToU.editorForLogout.commit();
        Intent serviceIntent = new Intent(currentActivityContext, ServiceUpdateDeviceToken.class);
        currentActivityContext.startService(serviceIntent);
        serviceIntent = null;
        Intent bookingCountService = new Intent(currentActivityContext, ServiceForCourierBookingCount.class);
        bookingCountService.putExtra("Is_API_Call_Require", 1);
        currentActivityContext.startService(bookingCountService);
        bookingCountService = null;

        /**show onboard screen when user login for first time*/
        if(isFirstLogin){
            Intent onboard = new Intent(currentActivityContext, OnBoardActivity.class);
            currentActivityContext.startActivity(onboard);
            onboard=null;
        }else{
            Intent loginPage = new Intent(currentActivityContext, SlideMenuZoom2u.class);
            loginPage.putExtra("intentFromLoginUI", "intentFromLoginUI");
            currentActivityContext.startActivity(loginPage);
            loginPage = null;
        }



        try {
            if(LoginZoomToU.notificUINewBookingVisibleCount == 0 || LoginZoomToU.notificUIRequestAvailCount == 0){
                //********** Open specific notification view on notification click when app is killed ***********
                if (MainActivity.NOTIFICATION_ID == 0) {
                    openOtherNotificationViews();
                    doNotOpenNewBookingAndBidNotiAtLogin(isFromLoginScreen, 0);
                } else if (MainActivity.NOTIFICATION_ID == 1) {
                    openOtherNotificationViews();
                    doNotOpenNewBookingAndBidNotiAtLogin(isFromLoginScreen, 1);
                } else if (MainActivity.NOTIFICATION_ID == 2) {
                    doNotOpenNewBookingAndBidNotiAtLogin(isFromLoginScreen, 0);
                    openOtherNotificationViews();
                }else if(MainActivity.NOTIFICATION_ID == 8){
                    if(!PushReceiver.prefrenceForPushy.getString("runId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("runId", "").equals("")){
                        Intent intentNewBooking = new Intent(currentActivityContext, SingleRunActivity.class);
                        intentNewBooking.putExtra("runId",PushReceiver.prefrenceForPushy.getString("runId", "0"));
                        currentActivityContext.startActivity(intentNewBooking);
                        intentNewBooking = null;
                    }
                }else if(MainActivity.NOTIFICATION_ID == 9){
                    if(!PushReceiver.prefrenceForPushy.getString("runBatchId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("runBatchId", "").equals("")){
                        Intent intentNewBooking = new Intent(currentActivityContext, RunBatchActivity.class);
                        intentNewBooking.putExtra("runBatchId",PushReceiver.prefrenceForPushy.getString("runBatchId", "0"));
                        currentActivityContext.startActivity(intentNewBooking);
                        intentNewBooking = null;
                    }

                } else {
                    openOtherNotificationViews();
                    doNotOpenNewBookingAndBidNotiAtLogin(isFromLoginScreen, 0);
                }

                NotificationManager notificationManager = (NotificationManager)currentActivityContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
                notificationManager = null;

                //******* Redirect to chat booking screen if chat notification comes at kill state **********
                LoadChatBookingArray.NOTIFICATIONID_FOR_CHAT = MainActivity.NOTIFICATION_ID;
                MainActivity.NOTIFICATION_ID = -1;
            }

            currentActivityContext.startService(new Intent(currentActivityContext, GetKillEvent_ToDestryNotication.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

        ((Activity)currentActivityContext).finish();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
            ((Activity)currentActivityContext).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    //************** Don not open New booking or Quote request notification at the time of login ************
    private void doNotOpenNewBookingAndBidNotiAtLogin(boolean isFromLoginScreen, int notificationId) {
        if (isFromLoginScreen == false) {
            if (notificationId == 1) {
                openNewBookingNotificationView();
                openNewBidNotificationView();
            } else {
                openNewBidNotificationView();
                openNewBookingNotificationView();
            }
        }
    }

    //************ Open New booking notification view if contains **************
    private void openNewBookingNotificationView() {
        try {
            if(!PushReceiver.prefrenceForPushy.getString("bookingId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("bookingId", "").equals("")){
                Intent intentNewBooking = new Intent(currentActivityContext, NewBooking_Notification.class);
                currentActivityContext.startActivity(intentNewBooking);
                intentNewBooking = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //************ Open New bid notification view if contains **************
    private void openNewBidNotificationView () {
        try {
            if(!PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "").equals("")){
                PushReceiver.Bid_REQUEST_ID = PushReceiver.prefrenceForPushy.getString("Bid_REQUEST_ID", "0");
                Intent intentNewBooking = new Intent(currentActivityContext, NewBidRequest_NotificationView.class);
                currentActivityContext.startActivity(intentNewBooking);
                intentNewBooking = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************ Open other notification view if contains **************
    private void openOtherNotificationViews() {
        try {

            if(!PushReceiver.pushyCourierLevelTxt.equals("") && !PushReceiver.pushyCourierLevelTxt.equals("0")){
                Intent intentCourierLevel = new Intent(currentActivityContext, Notification_Courier_Level.class);
                currentActivityContext.startActivity(intentCourierLevel);
                intentCourierLevel = null;
            }
            if(!PushReceiver.prefrenceForPushy.getString("WeeklyCourierStatus", "0").equals("0")){
                CallWeeklyCourierStats callWeeklyCourierStats = new CallWeeklyCourierStats(currentActivityContext);
                callWeeklyCourierStats.weeklyCourierStatusNotify();
                callWeeklyCourierStats = null;
                PushReceiver.loginEditorForPushy.putString("WeeklyCourierStatus", "0");
                PushReceiver.loginEditorForPushy.commit();
            }
            if(!PushReceiver.prefrenceForPushy.getString("NotificationMessage", "").equals("")){
                if(!PushReceiver.prefrenceForPushy.getString("Outstanding_RequestId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("Outstanding_RequestId", "").equals("")){
                    Intent intentOutstandingBidNotification = new Intent(currentActivityContext, DialogOutstandingBidNotification.class);
                    currentActivityContext.startActivity(intentOutstandingBidNotification);
                    intentOutstandingBidNotification = null;
                } else {
                    Intent intentNormalNotification = new Intent(currentActivityContext, DialogActivity.class);
                    currentActivityContext.startActivity(intentNormalNotification);
                    intentNormalNotification = null;
                }
            }
            if(!PushReceiver.prefrenceForPushy.getString("achievementId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("achievementId", "").equals("")){
                Intent intentCourierLevel = new Intent(currentActivityContext, AchievementNotifyView.class);
                currentActivityContext.startActivity(intentCourierLevel);
                intentCourierLevel = null;
            }

            Log.e("******** ", "@@@@@@@@@@   Depot Message Str   "+PushReceiver.prefrenceForPushy.getString("DepotReach_Msg_for_DHLDriver", ""));

            if (PushReceiver.prefrenceForPushy.getString("DepotReach_Msg_for_DHLDriver", "") != null) {
                if (!PushReceiver.prefrenceForPushy.getString("DepotReach_Msg_for_DHLDriver", "").equals("")) {
                    Intent intentNormalNotification = new Intent(currentActivityContext, DepotAlert_For_DHLDrivers.class);
                    intentNormalNotification.putExtra("RouteCode", PushReceiver.prefrenceForPushy.getString("DepotReach_RouteCode_DHLDriver", ""));
                    intentNormalNotification.putExtra("NotificationMessageStr", PushReceiver.prefrenceForPushy.getString("DepotReach_Msg_for_DHLDriver", ""));
                    currentActivityContext.startActivity(intentNormalNotification);
                    intentNormalNotification = null;
                    PushReceiver.loginEditorForPushy.putString("DepotReach_RouteCode_DHLDriver", "");
                    PushReceiver.loginEditorForPushy.putString("DepotReach_Msg_for_DHLDriver", "");
                    PushReceiver.loginEditorForPushy.remove("DepotReach_Msg_for_DHLDriver");
                    PushReceiver.loginEditorForPushy.remove("DepotReach_RouteCode_DHLDriver");
                    PushReceiver.loginEditorForPushy.commit();
                }
            } else if(!PushReceiver.prefrenceForPushy.getString("NotificationMessage", "").equals("")){
                Intent intentNormalNotification = new Intent(currentActivityContext, DialogActivity.class);
                currentActivityContext.startActivity(intentNormalNotification);
                intentNormalNotification = null;
            }

            if(PushReceiver.prefrenceForPushy.getString("LateDeliveryId", "0") != null) {
                if(!PushReceiver.prefrenceForPushy.getString("LateDeliveryId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("LateDeliveryId", "").equals("")) {
                    Intent intentLateDeliveryView = new Intent(currentActivityContext, Notification_BookingStatusRequest.class);
                    currentActivityContext.startActivity(intentLateDeliveryView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********** Get token from pushy ************//
    public void getPushyTokenId() {
        /********************* Pushy Implementation  *******************/
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
            StrictMode.setThreadPolicy(policy);
            // For device register to pushy
            RegisterForPushNotificationsAsync();
            //new RegisterForPushNotificationsAsync().execute();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        /******************  End Pushy Implementation  *******************/
    }

    private void RegisterForPushNotificationsAsync(){

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                // before execution
            }

            @Override
            public void doInBackground() {
                try {
                    // Assign a unique token to this device
                    LoginZoomToU.pushyRegId = Pushy.register(currentActivityContext);
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                // Ui task here
            }
        }.execute();
    }


    Dialog dialogTermsCondition;
    void alertTermsAndConditionForCourier(final boolean isFromLoginScreen, boolean isFirstLogin, LoginZoomToU loginZoomToU){

        if (dialogTermsCondition != null)
            if (dialogTermsCondition.isShowing())
                dialogTermsCondition.dismiss();

        dialogTermsCondition = new Dialog(currentActivityContext, android.R.style.Theme_Translucent_NoTitleBar);
        dialogTermsCondition.setCancelable(false);
        dialogTermsCondition.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogTermsCondition.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTermsCondition.setContentView(R.layout.terms_z2u_couriers);

        Window window = dialogTermsCondition.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView termHeaderTxt = (TextView) dialogTermsCondition.findViewById(R.id.termHeaderTxt);

        ScrollView scrollView=dialogTermsCondition.findViewById(R.id.scroll_view);
        String url = WebserviceHandler.TERMS_CONDITIONS;
        WebView webViewTermsConditions = (WebView) dialogTermsCondition.findViewById(R.id.webViewTermsConditions);
        webViewTermsConditions.setWebViewClient(new MyBrowser());
        progressBar = (ProgressBar)  dialogTermsCondition.findViewById(R.id.progressBar);

        webViewTermsConditions.getSettings().setJavaScriptEnabled(true);
        webViewTermsConditions.getSettings().setLoadsImagesAutomatically(true);
        webViewTermsConditions.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewTermsConditions.loadUrl(url);

        Button termBtnDecline = (Button) dialogTermsCondition.findViewById(R.id.termBtnDecline);

        termBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTermsCondition.dismiss();
            }
        });

        termBtnAccept = (Button) dialogTermsCondition.findViewById(R.id.termBtnAccept);

        termBtnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptTCAsyncTask(isFromLoginScreen,isFirstLogin);
                //new AcceptTCAsyncTask(isFromLoginScreen,isFirstLogin).execute();
            }
        });

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
                int topDetector = scrollView.getScrollY();
                int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
                if(bottomDetector == 0 ){
                    // Toast.makeText(loginZoomToU,"Scroll View bottom reached",Toast.LENGTH_SHORT).show();
                    termBtnAccept.setBackgroundResource(R.drawable.rounded_worrier_level);
                    termBtnAccept.setEnabled(true);
                }
                if(topDetector <= 0){
                    //Toast.makeText(loginZoomToU,"Scroll View top reached",Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialogTermsCondition.show();
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            progressBar.setProgress(100);
            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            super.onPageStarted(view, url, favicon);
        }
    }

    private void AcceptTCAsyncTask(boolean isFromLoginScreen,boolean isFirstLogin){
        final String[] webServiceResponseForAcceptTC = {"0"};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForTC == null)
                        progressForTC = new ProgressDialog(currentActivityContext);
                    Custom_ProgressDialogBar.inItProgressBar(progressForTC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    webServiceResponseForAcceptTC[0] = webServiceHandler.addCourierLoginAcceptance();
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (new JSONObject(webServiceResponseForAcceptTC[0]).getBoolean("success")) {
                        // dialogTermsCondition.dismiss();
                        loggedInSuccessfully(isFromLoginScreen,isFirstLogin);
                    }else
                        DialogActivity.alertDialogView(currentActivityContext, "Error!", "Something went wrong, Please try again");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(currentActivityContext, "Error!", "Something went wrong, Please try again");
                }finally {
                    if(progressForTC != null)
                        if(progressForTC.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressForTC);
                }
            }
        }.execute();

    }



}
