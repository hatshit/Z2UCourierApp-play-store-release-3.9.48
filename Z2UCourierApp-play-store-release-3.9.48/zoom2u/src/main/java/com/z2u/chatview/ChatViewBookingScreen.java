package com.z2u.chatview;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.z2u.chat.Firebase_Auth_Provider;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.slidemenu.offerrequesthandlr.Bid_ChatListAdapter;
import com.zoom2u.slidemenu.offerrequesthandlr.ChatListForBid;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatViewBookingScreen extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener{

    RecyclerView chatViewList, bidChatViewList;
    SwipeRefreshLayout swipeRefreshChatList;
    Button chat_now;
    public static ChatBookingListAdapter chatListAdapter;
    public static DatabaseReference mFirebaseRef;
    ProgressDialog progressDialogToLoadBookingsToChat;
    Window window;
    RelativeLayout heading;
    //****************  Firebase chat Base Url for Production   *******//
    //         Account - iosaplitetesting@gmail.com
   public static final String FIREBASE_URL = "https://zoom2u.firebaseio.com/";

    //****************  Firebase chat Base Url for Test/Staging   *******//
   //public static final String FIREBASE_URL = "https://customer-app-f86ec.firebaseio.com";

//**************** Booking Link for HELP *********************
    public static final String BOOKING_DETAIL_LINK = "https://inside.zoom2u.com/#/bookings/detail/";

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        if (chatListAdapter != null)
            chatListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
      //  SlideMenuZoom2u.setCourierToOfflineFromChat();
        outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
        outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            if(savedInstanceState != null){
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
            }

            if(LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(ChatViewBookingScreen.this);

            inItChatBookingView();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        heading=findViewById(R.id.heading);
        window = ChatViewBookingScreen.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if(MainActivity.isIsBackGroundGray()){
            heading.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        }else{
            heading.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }
        new Firebase_Auth_Provider(ChatViewBookingScreen.this, false);            //******** In it firebase current user ********

        if(savedInstanceState != null){
            WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
            ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
            BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
            if(LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(ChatViewBookingScreen.this);
        }

        inItChatBookingView();
    }

    private void inItChatBookingView() {
        try{
            chat_now = findViewById(R.id.chat_now);
            chat_now.setOnClickListener(this);

            TextView chatHeaderTxt = (TextView) findViewById(R.id.chatHeaderTxt);

            ImageView chatCloseBtn = (ImageView) findViewById(R.id.chatCloseBtn);
            chatCloseBtn.setOnClickListener(this);


            int getPixelForItem = convertDpToPixel(154);
            int adminHiddenTxtHeight = convertDpToPixel(50);

            if (swipeRefreshChatList == null)
                swipeRefreshChatList = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshChatList);
            swipeRefreshChatList.setOnRefreshListener(this);
            swipeRefreshChatList.setColorSchemeColors(Color.parseColor("#215400"), Color.parseColor("#4f5151"), Color.parseColor("#a1c93a"));
            if (chatViewList == null)
                chatViewList = (RecyclerView) findViewById(R.id.chatViewList);
            if (bidChatViewList == null)
                bidChatViewList = (RecyclerView) findViewById(R.id.bidChatViewList);

            if (chatListAdapter != null)
                chatListAdapter = null;
            if (LoadChatBookingArray.arrayOfChatDelivery == null)
                LoadChatBookingArray.arrayOfChatDelivery = new ArrayList<Model_DeliveriesToChat>();
            if (RequestView.requestView_pojoList == null)
                RequestView.requestView_pojoList = new ArrayList<RequestView_DetailPojo>();

            chatListAdapter = new ChatBookingListAdapter(this,LoadChatBookingArray.arrayOfChatDelivery, new ChatBookingListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(Model_DeliveriesToChat item) {
                    Bundle bModelDeliveryChat = new Bundle();
                    bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", item);
                    Intent chatDetailViewIntent = new Intent(ChatViewBookingScreen.this, ChatDetailActivity.class);
                    chatDetailViewIntent.putExtras(bModelDeliveryChat);
                    startActivity(chatDetailViewIntent);
                    chatDetailViewIntent = null;
                    bModelDeliveryChat = null;
                }
            });

            if (ChatListForBid.bidChatListAdapter != null)
                ChatListForBid.bidChatListAdapter = null;
            ChatListForBid.bidChatListAdapter = new Bid_ChatListAdapter(this,new Bid_ChatListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RequestView_DetailPojo item) {
                    if (!item.getOfferPrice().equals("null") && !item.getOfferPrice().equals("")) {
                        if (item.isCancel())
                             DialogActivity.alertDialogView(ChatViewBookingScreen.this, "Alert!", "This bid has been cancelled by you. Please place the bid again to chat.");
                        else {
                            Intent bidChatWithCustomer = new Intent(ChatViewBookingScreen.this, ChatView_BidConfirmation.class);
                            bidChatWithCustomer.putExtra("BidIDForChat", item.getId());
                            bidChatWithCustomer.putExtra("CustomerNameForChat", item.getCustomer());
                            bidChatWithCustomer.putExtra("CustomerIDForChat", item.getCustomerID());
                            startActivity(bidChatWithCustomer);
                            bidChatWithCustomer = null;
                        }
                    } else
                        DialogActivity.alertDialogView(ChatViewBookingScreen.this, "Alert!", "First place the bid to chat.");
                }
            });

            int bookingChatListHeight = ((getPixelForItem * LoadChatBookingArray.arrayOfChatDelivery.size()) - adminHiddenTxtHeight);
            RelativeLayout.LayoutParams paramForRecyclerView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, bookingChatListHeight);
            chatViewList.setLayoutParams(paramForRecyclerView);
            chatViewList.setNestedScrollingEnabled(false);
            paramForRecyclerView = null;
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            chatViewList.setLayoutManager(mLayoutManager);
            chatViewList.setItemAnimator(new DefaultItemAnimator());
            chatViewList.setAdapter(chatListAdapter);

            int bidChatListHeight = getPixelForItem * RequestView.requestView_pojoList.size();
            paramForRecyclerView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, bidChatListHeight);
            paramForRecyclerView.addRule(RelativeLayout.BELOW, R.id.chatViewList);
            bidChatViewList.setLayoutParams(paramForRecyclerView);
            bidChatViewList.setNestedScrollingEnabled(false);
            paramForRecyclerView = null;
            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
            bidChatViewList.setLayoutManager(mLayoutManager1);
            bidChatViewList.setItemAnimator(new DefaultItemAnimator());
            bidChatViewList.setAdapter(ChatListForBid.bidChatListAdapter);

            if (LoadChatBookingArray.arrayOfChatDelivery != null || RequestView.requestView_pojoList != null) {
                if (LoadChatBookingArray.arrayOfChatDelivery.size() == 0)
                    //new GetBookingsForChatList().execute();
                    GetBookingsForChatList();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatCloseBtn:
                exitFormChatListView();
                break;
            case R.id.chat_now:
                boolean isInstalled = isAppInstalled(this,"com.whatsapp");
                boolean isInstalledWhatsappBussines = isAppInstalled(this,"com.whatsapp.w4b");
                if(isInstalled||isInstalledWhatsappBussines) {
                    try {
                        String url = "https://wa.me/61456375000";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(this, "Please install WhatsApp from play store to start chatting with customer support.", Toast.LENGTH_LONG)
                            .show();

                break;
        }
    }


    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }catch (Exception e){
            return false;
        }
    }

    void exitFormChatListView(){
        chatListAdapter = null;
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        exitFormChatListView();
    }

    @Override
    public void onRefresh() {
        swipeRefreshChatList.setRefreshing(true);
        //new GetBookingsForChatList().execute();
        GetBookingsForChatList();
    }


    private void GetBookingsForChatList(){
        final String[] getBookingForChatListStr = {""};
        final String[] responseStrToLoadBidList = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                swipeRefreshChatList.setRefreshing(false);
                if (progressDialogToLoadBookingsToChat != null)
                    progressDialogToLoadBookingsToChat = null;
                progressDialogToLoadBookingsToChat = new ProgressDialog(ChatViewBookingScreen.this);
                Custom_ProgressDialogBar.inItProgressBar(progressDialogToLoadBookingsToChat);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    getBookingForChatListStr[0] = webServiceHandler.getBookingForChatList();
                    responseStrToLoadBidList[0] = webServiceHandler.getQuoteRequestOfCourier();
                    Log.e("Pushy", webServiceHandler.getBookingForChatList());
                    Log.e("Pushy", webServiceHandler.getQuoteRequestOfCourier());
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    getBookingForChatListStr[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressDialogToLoadBookingsToChat != null)
                        if (progressDialogToLoadBookingsToChat.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadBookingsToChat);
                    setDeliveriesToChat(getBookingForChatListStr[0]);
                    LoadChatBookingArray.setBidRequestItems(ChatViewBookingScreen.this, responseStrToLoadBidList[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }



    //********** Get Deliveries to chat response to array  ************//
    void setDeliveriesToChat(String setDeliveriesToChatStr){
        try {
            SlideMenuZoom2u.connectWithFireBaseChat();
            if (LoadChatBookingArray.arrayOfChatDelivery.size() > 0){
                for (Model_DeliveriesToChat model_deliveriesToChat : LoadChatBookingArray.arrayOfChatDelivery){
                    if (model_deliveriesToChat.getCustomer().equals("Zoom2u-Admin")) {
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS+model_deliveriesToChat.getCourierId()+"/status/courier/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCourierChat());
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS + model_deliveriesToChat.getCourierId() + "/status/admin/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCustomerChat());
                    }else{
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT+model_deliveriesToChat.getBookingId()+"_"+model_deliveriesToChat.getCourierId()+"/status/courier/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCourierChat());
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT + model_deliveriesToChat.getBookingId()+"_"+model_deliveriesToChat.getCourierId() + "/status/customer/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCustomerChat());
                    }
                }
                LoadChatBookingArray.arrayOfChatDelivery.clear();
            }

            Model_DeliveriesToChat model_deliveriesToChat = new Model_DeliveriesToChat(ChatViewBookingScreen.this, LoginZoomToU.courierID, "Zoom2u-Admin");
            LoadChatBookingArray.arrayOfChatDelivery.add(model_deliveriesToChat);
            if (!setDeliveriesToChatStr.equals("")){
                JSONObject mainJObjOfChatDelivery = new JSONObject(setDeliveriesToChatStr);
                if (mainJObjOfChatDelivery.getBoolean("Success")){
                    JSONArray jsonArrayOfChatDelivery = new JSONArray(mainJObjOfChatDelivery.getString("data"));
                    if (jsonArrayOfChatDelivery.length() > 0){
                        for (int i = 0; i < jsonArrayOfChatDelivery.length(); i++){
                            Model_DeliveriesToChat model_deliveriesToChatCustomer = new Model_DeliveriesToChat(ChatViewBookingScreen.this, jsonArrayOfChatDelivery.getJSONObject(i));
                            LoadChatBookingArray.arrayOfChatDelivery.add(model_deliveriesToChatCustomer);
                        }
                    }
                    jsonArrayOfChatDelivery = null;
                    mainJObjOfChatDelivery = null;
                }
            }
            setRecyclerAdapter();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //************ Refresh recycler view adapter ********//
    void setRecyclerAdapter(){
        if (chatListAdapter != null)
            chatListAdapter.notifyDataSetChanged();
    }

    private int convertDpToPixel(int dpValue){
        Resources r = getResources();
        int pixel = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, r.getDisplayMetrics());
        return pixel;
    }
}
