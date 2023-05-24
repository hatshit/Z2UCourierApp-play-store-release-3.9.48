package com.zoom2u.slidemenu.accountdetail_section.sellcourierbusiness;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.accountdetail_section.PoliceCheckView;

public class ActivitySellYourCourierBusiness extends Activity implements View.OnClickListener {

    private static String URI_REGISTER_FOR_NEW_BUSINESS = "https://zoom2u.typeform.com/to/jQze795M";
    private static String URI_TRANSFER_BUSINESS_TO_NEW_OWNER = "https://zoom2u.typeform.com/to/lRWUvRJZ";

    TextView countChatSellYourCourierBusiness;
    Button registerBusinessForSale, transferBusinessToNewOwner;



    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatSellYourCourierBusiness);
        SlideMenuZoom2u.countChatBookingView = countChatSellYourCourierBusiness;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_sell_your_courier_business);

        RelativeLayout headerSummaryReportLayout=findViewById(R.id.SellCourierBusinessSettingTitleBarLayout);
        Window window = ActivitySellYourCourierBusiness.this.getWindow();
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
        countChatSellYourCourierBusiness = (TextView) findViewById(R.id.countChatBookingDetail);
        countChatSellYourCourierBusiness.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatSellYourCourierBusiness;

        registerBusinessForSale = (Button) findViewById(R.id.registerBusinessForSale);

        registerBusinessForSale.setOnClickListener(this);
        transferBusinessToNewOwner = (Button) findViewById(R.id.transferBusinessToNewOwner);

        transferBusinessToNewOwner.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
    //    AccountDetailFragment.OPEN_SETTING_VIEW = 4;
        finish();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.backBtnHeader:
            //    AccountDetailFragment.OPEN_SETTING_VIEW = 4;
                finish();
                break;

            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(ActivitySellYourCourierBusiness.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;


            case R.id.registerBusinessForSale:
                Intent intentToRegisterForNewBusiness = new Intent(Intent.ACTION_VIEW);
                intentToRegisterForNewBusiness.setData(Uri.parse(URI_REGISTER_FOR_NEW_BUSINESS));
                startActivity(intentToRegisterForNewBusiness);
//                sellCourierBusinessTypeFormInAppBrowser(URI_REGISTER_FOR_NEW_BUSINESS);
                break;

            case R.id.transferBusinessToNewOwner:
                Intent intentToTransferBusiness = new Intent(Intent.ACTION_VIEW);
                intentToTransferBusiness.setData(Uri.parse(URI_TRANSFER_BUSINESS_TO_NEW_OWNER));
                startActivity(intentToTransferBusiness);
//                sellCourierBusinessTypeFormInAppBrowser(URI_TRANSFER_BUSINESS_TO_NEW_OWNER);
                break;
        }
    }

    Dialog dialogTypeFormSellCourierBusiness;
    ProgressBar progressBar;
    void sellCourierBusinessTypeFormInAppBrowser(String url){

        if (dialogTypeFormSellCourierBusiness != null)
            if (dialogTypeFormSellCourierBusiness.isShowing())
                dialogTypeFormSellCourierBusiness.dismiss();

        dialogTypeFormSellCourierBusiness = new Dialog(ActivitySellYourCourierBusiness.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialogTypeFormSellCourierBusiness.setCancelable(false);
        dialogTypeFormSellCourierBusiness.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogTypeFormSellCourierBusiness.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTypeFormSellCourierBusiness.setContentView(R.layout.dialog_sell_courier_business_typeform);

        Window window = dialogTypeFormSellCourierBusiness.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        WebView webViewSellCourierBusinessTypeForm = (WebView) dialogTypeFormSellCourierBusiness.findViewById(R.id.webViewSellCourierBusinessTypeForm);
        webViewSellCourierBusinessTypeForm.setWebViewClient(new MyBrowser());
        progressBar = (ProgressBar)  dialogTypeFormSellCourierBusiness.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        webViewSellCourierBusinessTypeForm.getSettings().setJavaScriptEnabled(true);
        webViewSellCourierBusinessTypeForm.getSettings().setLoadsImagesAutomatically(true);
        webViewSellCourierBusinessTypeForm.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webViewSellCourierBusinessTypeForm.loadUrl(url);

        TextView cancelBtnSellCourierBusinessTypeForm = (TextView) dialogTypeFormSellCourierBusiness.findViewById(R.id.cancelBtnSellCourierBusinessTypeForm);

        cancelBtnSellCourierBusinessTypeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTypeFormSellCourierBusiness.dismiss();
            }
        });

        dialogTypeFormSellCourierBusiness.show();
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
}
