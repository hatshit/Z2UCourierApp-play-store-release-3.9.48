package com.zoom2u.slidemenu.accountdetail_section;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.z2u.booking.vc.NewBookingView;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;
import org.json.JSONObject;

/**
 * Created by Arun on 23-Nov-2017.
 */

public class SummaryReportView extends Activity implements View.OnClickListener {

    private TextView countChatSummaryReport, deliveriesTodayTxtSummaryReport, deliveriesLastWeekTxtSummaryReport, moneyTodayTxtSummaryReport, moneyLastWeekTxtSummaryRepor,
            thumbsUpTodayTxtSummaryReport, thumbsUpLastWeekTxtSummaryReport, pointsTodayTxtSummaryReport, pointsLastWeekTxtSummaryReport;
    private TextView thumbsUpTxt, thumbsDownTxt, ratingTxtSR;
    private ListView recentItemListSR;
    private TextView recentBookingNotAvailTxt;
    RelativeLayout headerSummaryReportLayout;
    Window window;
    private SummaryReportDetail_POJO summaryReportDetailPojo;
    private ProgressDialog progressDialogSRView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_report_view);
        headerSummaryReportLayout=findViewById(R.id.headerSummaryReportLayout);
        window = SummaryReportView.this.getWindow();
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

        //headerWithGreenStatusBar();
        inItSummaryReportView();    //************** Initialize summary report views
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatSummaryReport);
        SlideMenuZoom2u.countChatBookingView = countChatSummaryReport;
    }

    @SuppressLint("NewApi")
    void headerWithGreenStatusBar(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            Window window = SummaryReportView.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#7BCE20"));
        }
    }

    //************** Initialize summary report view *********
    private void inItSummaryReportView() {
        //********** Summary report header content ************
        findViewById(R.id.backFromSummaryReport).setOnClickListener(this);

        findViewById(R.id.summaryReportChatIcon).setOnClickListener(this);
        countChatSummaryReport = (TextView) findViewById(R.id.countChatSummaryReport);

        countChatSummaryReport.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatSummaryReport;




        View deliveriesViewSR = findViewById(R.id.deliveriesViewSR);

        ((TextView) deliveriesViewSR.findViewById(R.id.categeoryItemTxtSummaryReport)).setText("Delivery");
        deliveriesTodayTxtSummaryReport = (TextView) deliveriesViewSR.findViewById(R.id.categeoryItemTodayTxtSummaryReport);

        deliveriesLastWeekTxtSummaryReport = (TextView) deliveriesViewSR.findViewById(R.id.categeoryItemLastWeekTxtSummaryReport);

        deliveriesTodayTxtSummaryReport.requestFocus();
        deliveriesTodayTxtSummaryReport.setFocusable(true);
        deliveriesTodayTxtSummaryReport.setFocusableInTouchMode(true);

        View moneyViewSR = findViewById(R.id.moneyViewSR);

        ((TextView) moneyViewSR.findViewById(R.id.categeoryItemTxtSummaryReport)).setText("Revenue");
        moneyTodayTxtSummaryReport = (TextView) moneyViewSR.findViewById(R.id.categeoryItemTodayTxtSummaryReport);

        moneyTodayTxtSummaryReport.setTextColor(getResources().getColor(R.color.colorPrimary));
        moneyLastWeekTxtSummaryRepor = (TextView) moneyViewSR.findViewById(R.id.categeoryItemLastWeekTxtSummaryReport);

        moneyLastWeekTxtSummaryRepor.setTextColor(getResources().getColor(R.color.colorPrimary));

        View thumbsUpViewSR = findViewById(R.id.thumbsUpViewSR);

        ((TextView) thumbsUpViewSR.findViewById(R.id.categeoryItemTxtSummaryReport)).setText("Thumbs Up");
        thumbsUpTodayTxtSummaryReport = (TextView) thumbsUpViewSR.findViewById(R.id.categeoryItemTodayTxtSummaryReport);

        thumbsUpTodayTxtSummaryReport.setTextColor(getResources().getColor(R.color.gold_light));
        thumbsUpLastWeekTxtSummaryReport = (TextView) thumbsUpViewSR.findViewById(R.id.categeoryItemLastWeekTxtSummaryReport);

        thumbsUpLastWeekTxtSummaryReport.setTextColor(getResources().getColor(R.color.gold_light));

        View pointsViewSR = findViewById(R.id.pointsViewSR);

        ((TextView) pointsViewSR.findViewById(R.id.categeoryItemTxtSummaryReport)).setText("Thumbs Down");
        pointsTodayTxtSummaryReport = (TextView) pointsViewSR.findViewById(R.id.categeoryItemTodayTxtSummaryReport);

        pointsTodayTxtSummaryReport.setTextColor(getResources().getColor(R.color.colorAccent));
        pointsLastWeekTxtSummaryReport = (TextView) pointsViewSR.findViewById(R.id.categeoryItemLastWeekTxtSummaryReport);

        pointsLastWeekTxtSummaryReport.setTextColor(getResources().getColor(R.color.colorAccent));
        pointsViewSR.setVisibility(View.GONE);


        thumbsUpTxt = (TextView) findViewById(R.id.thumbsUpTxt);

        thumbsDownTxt = (TextView) findViewById(R.id.thumbsDownTxt);

        ratingTxtSR = (TextView) findViewById(R.id.ratingTxtSR);




        recentItemListSR = (NonScrollListView) findViewById(R.id.recentItemListSR);

        recentBookingNotAvailTxt = (TextView) findViewById(R.id.recentBookingNotAvailTxt);

        recentBookingNotAvailTxt.setVisibility(View.GONE);

        apiCallToGetSummaryReport();        //********** Get summary report details
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backFromSummaryReport:
                finish();
                break;
            case R.id.summaryReportChatIcon:
                Intent chatViewItent = new Intent(SummaryReportView.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
//            case R.id.summaryReportRefreshBtn:
//                apiCallToGetSummaryReport();        //********** Refresh summary report view
//                break;
        }
    }

    //************* API call to get summary report ************
    private void apiCallToGetSummaryReport() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetSummaryReportAsyncTask();
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(SummaryReportView.this, "No Network!", "No network connection, Please try again later.");
    }

    private void GetSummaryReportAsyncTask(){
        final String[] responseSummaryReport = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(SummaryReportView.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseSummaryReport[0] = webServiceHandler.getCourierSummaryReport();
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    responseSummaryReport[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            if (!responseSummaryReport[0].equals("")) {
                                if (new JSONObject(responseSummaryReport[0]).getBoolean("success")) {
                                    summaryReportDetailPojo = new SummaryReportDetail_POJO(new JSONObject(responseSummaryReport[0]));
                                    setSummaryReportDataToUIContents();
                                } else
                                    DialogActivity.alertDialogView(SummaryReportView.this, "Alert!", new JSONObject(responseSummaryReport[0]).getString("message"));
                            } else
                                DialogActivity.alertDialogView(SummaryReportView.this, "Sorry!", "Something went wrong, Please try again later.");
                            break;
                        case 1:
                            DialogActivity.alertDialogView(SummaryReportView.this, "No Network!", "No network connection, Please try again later.");
                            break;
                        case 2:
                            break;
                        case 3:
                            DialogActivity.alertDialogView(SummaryReportView.this, "Sorry!", "Something went wrong, Please try again later.");
                            break;
                        default:
                            break;
                    }
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(SummaryReportView.this, "Error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();
    }



    //************ Set summary report data to UI content after getting response ***********
    private void setSummaryReportDataToUIContents() {
        if (summaryReportDetailPojo != null) {
            deliveriesTodayTxtSummaryReport.setText("" + summaryReportDetailPojo.getDeliveriesOfToday());
            deliveriesLastWeekTxtSummaryReport.setText("" + summaryReportDetailPojo.getDeliveriesOfLastWeek());
            moneyTodayTxtSummaryReport.setText("" + summaryReportDetailPojo.getMoneyOfToday());
            moneyLastWeekTxtSummaryRepor.setText("" + summaryReportDetailPojo.getMoneyOfLastWeek());
            thumbsUpTodayTxtSummaryReport.setText("" + summaryReportDetailPojo.getThumbsUpOfToday());
            thumbsUpLastWeekTxtSummaryReport.setText("" + summaryReportDetailPojo.getThumbsUpOfLastWeek());
            pointsTodayTxtSummaryReport.setText("" + summaryReportDetailPojo.getThumbsDownOfToday());
            pointsLastWeekTxtSummaryReport.setText("" + summaryReportDetailPojo.getThumbsDownOfLastWeek());
            thumbsUpTxt.setText("" + summaryReportDetailPojo.getTotalThumbsUp());
            thumbsDownTxt.setText("" + summaryReportDetailPojo.getTotalThumbsDown());
            ratingTxtSR.setText(summaryReportDetailPojo.getTotalBookingsPercentage() + "%");
            if (summaryReportDetailPojo.getArrayOfRecentBookings().size() > 0) {
                recentBookingNotAvailTxt.setVisibility(View.GONE);
                recentItemListSR.setVisibility(View.VISIBLE);
                RecentBookingsListAdapter recentBookingsListAdapter = new RecentBookingsListAdapter(SummaryReportView.this, R.layout.recentdata_items_sr);
                recentItemListSR.setAdapter(recentBookingsListAdapter);
                recentBookingsListAdapter = null;
            } else {
                recentItemListSR.setVisibility(View.VISIBLE);
                recentItemListSR.setVisibility(View.GONE);
            }
        } else
            DialogActivity.alertDialogView(SummaryReportView.this, "Sorry!", "Something went wrong, Please try again later.");
    }

    public class RecentBookingsListAdapter extends ArrayAdapter<RecentBookingItems_SR_POJO> {

        private int[] colors;

        public RecentBookingsListAdapter(Context recentBookingListContext, int resourceId) {
            super(recentBookingListContext, resourceId);
            if (summaryReportDetailPojo.getArrayOfRecentBookings().size() % 2 != 0)
                colors = new int[]{0xF7FAFCFC, 0xFFFFFFFF};
            else
                colors = new int[]{0xFFFFFFFF, 0xF7FAFCFC};
        }

        @Override
        public int getCount() {
            return summaryReportDetailPojo.getArrayOfRecentBookings().size();
        }

        @Override
        public RecentBookingItems_SR_POJO getItem(int position) {
            return summaryReportDetailPojo.getArrayOfRecentBookings().get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            try {
                if (convertView == null)
                    convertView = LayoutInflater.from(SummaryReportView.this).inflate(R.layout.recentdata_items_sr, null);

                LinearLayout recentItemLayout = NewBookingView.ViewHolderPattern.get(convertView, R.id.recentItemLayout);

                //   ***********  Change alternate color for present day booking  **************
                int colorPos = position % colors.length;
                colorPos = position % colors.length;
                recentItemLayout.setBackgroundColor(colors[colorPos]);
                //

                RecentBookingItems_SR_POJO recentBookingItemsSrPojo = summaryReportDetailPojo.getArrayOfRecentBookings().get(position);

                TextView recentDateTxtSummaryReport = NewBookingView.ViewHolderPattern.get(convertView, R.id.recentDateTxtSummaryReport);

                try {
                    String[] filterDateArray = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(recentBookingItemsSrPojo.getRecentBookingDate()).split("-");
                    recentDateTxtSummaryReport.setText(filterDateArray[0]+"-"+filterDateArray[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                    recentDateTxtSummaryReport.setText("-NA-");
                }
                TextView recentRatingTxtSummaryReport = NewBookingView.ViewHolderPattern.get(convertView, R.id.recentRatingTxtSummaryReport);

                try {
                    if (!recentBookingItemsSrPojo.getRecentBookingRating().equals("") && !recentBookingItemsSrPojo.getRecentBookingRating().equals("null")
                            && !recentBookingItemsSrPojo.getRecentBookingRating().equals(null)) {
                        recentRatingTxtSummaryReport.setText("" +recentBookingItemsSrPojo.getRecentBookingRating());
                    } else
                        recentRatingTxtSummaryReport.setText("0");
                } catch (Exception e) {
                    e.printStackTrace();
                    recentRatingTxtSummaryReport.setText("0");
                }
                TextView recentPickSuburbTxtSummaryReport = NewBookingView.ViewHolderPattern.get(convertView, R.id.recentPickSuburbTxtSummaryReport);

                recentPickSuburbTxtSummaryReport.setText(recentBookingItemsSrPojo.getRecentBookingPickUpSuburb());

                TextView recentDropSuburbTxtSummaryReport = NewBookingView.ViewHolderPattern.get(convertView, R.id.recentDropSuburbTxtSummaryReport);

                recentDropSuburbTxtSummaryReport.setText(recentBookingItemsSrPojo.getRecentBookingDropoffSuburb());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
