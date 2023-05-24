package com.zoom2u.slidemenu.accountdetail_section;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.roundedimage.RoundedImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by Arun on 7/2/17.
 */

public class CourierIdentification_View extends Activity implements View.OnClickListener{

    TextView countChatCourierIdentification, courierFirstLastName;
    RoundedImageView courierImg;
    ImageView defaultPdfImg;
    ProgressBar progressBarCourierId;

    HashMap<String, Object> detailDictionaryObj;

    public static final int PDF_SELECTION = 1101;
      Window window;
    @Override
    protected void onResume() {
        super.onResume();
            super.onResume();
            SlideMenuZoom2u.setCourierToOnlineForChat();
            Model_DeliveriesToChat.showExclamationForUnreadChat(countChatCourierIdentification);
            SlideMenuZoom2u.countChatBookingView = countChatCourierIdentification;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_identification_view);

       RelativeLayout headerSummaryReportLayout=findViewById(R.id.courierIdPageTitleBar);
        window = CourierIdentification_View.this.getWindow();
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

        inItCourierIdentificationContents();        //***** init current view contents
    }

    //********** Initiaize this view contents ************
    private void inItCourierIdentificationContents() {

        detailDictionaryObj = (HashMap<String, Object>) getIntent().getSerializableExtra("DetailDictionary");


        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);
        findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);

        countChatCourierIdentification = (TextView) findViewById(R.id.courierIdPageTitleBar).findViewById(R.id.countChatBookingDetail);

        countChatCourierIdentification.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatCourierIdentification;

        ((ImageView) findViewById(R.id.logoImgZ2U)).setImageResource(R.drawable.logo_z2u_courier);
        findViewById(R.id.logoImgZ2U).setOnClickListener(this);

        progressBarCourierId = (ProgressBar) findViewById(R.id.progressBarCourierId);
        progressBarCourierId.setVisibility(View.GONE);

        courierImg = (RoundedImageView) findViewById(R.id.courierImg);
        //new BackgroundTaskToSetProfileImage().execute();
        BackgroundTaskToSetProfileImage();

        findViewById(R.id.courierName);
        ((TextView) findViewById(R.id.courierName)).setText(detailDictionaryObj.get("FirstName")+" "+
                detailDictionaryObj.get("LastName"));

        courierFirstLastName = (TextView) findViewById(R.id.courierFirstLastName);

        try {
            courierFirstLastName.setText(((String)((((String)detailDictionaryObj.get("FirstName")).charAt(0)+""+((String) detailDictionaryObj.get("LastName")).charAt(0)))).toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }


        ((TextView) findViewById(R.id.car)).setText(""+detailDictionaryObj.get("Vehicle"));




        ((TextView) findViewById(R.id.courierIDZ)).setText(""+detailDictionaryObj.get("CourierRef"));




        if (detailDictionaryObj.get("CentralWorkSuburb").equals(""))
            ((TextView) findViewById(R.id.city)).setText("-NA-");
        else
            ((TextView) findViewById(R.id.city)).setText(""+detailDictionaryObj.get("CentralWorkSuburb"));




        if (detailDictionaryObj.get("ABN").equals(""))
            ((TextView) findViewById(R.id.abnNumber)).setText("-NA-");
        else
            ((TextView) findViewById(R.id.abnNumber)).setText(""+detailDictionaryObj.get("ABN"));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backFromBookingDetail:
                finish();
                break;
            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(CourierIdentification_View.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.logoImgZ2U:
                //Toast.makeText(CourierIdentification_View.this, "Image tapped", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void BackgroundTaskToSetProfileImage(){
        final Bitmap[] uploadProfileImageBitmap = new Bitmap[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    progressBarCourierId.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    if (!detailDictionaryObj.get("Photo").equals(""))
                        uploadProfileImageBitmap[0] = BitmapFactory.decodeStream((InputStream) new URL("" + detailDictionaryObj.get("Photo")).getContent());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (uploadProfileImageBitmap[0] != null) {
                        courierImg.setImageBitmap(uploadProfileImageBitmap[0]);
                        courierFirstLastName.setVisibility(View.GONE);
                        uploadProfileImageBitmap[0] = null;
                    } else
                        courierFirstLastName.setVisibility(View.VISIBLE);

                    progressBarCourierId.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBarCourierId.setVisibility(View.GONE);
                }
            }
        }.execute();

    }


}
