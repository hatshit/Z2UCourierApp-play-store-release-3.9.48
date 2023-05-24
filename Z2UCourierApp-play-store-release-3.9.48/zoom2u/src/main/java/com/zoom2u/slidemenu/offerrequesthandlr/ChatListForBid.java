package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.chat.Firebase_Auth_Provider;
import com.z2u.chatview.ChatView_BidConfirmation;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatListForBid extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    Button chat_now;
    RecyclerView bidChatViewList;
    SwipeRefreshLayout swipeRefreshChatList;
    RelativeLayout headerLayoutAllTitleBar;
    public static Bid_ChatListAdapter bidChatListAdapter;
    ProgressDialog progressDialogToLoadBookingsToChat;
    @Override
    protected void onResume() {
        super.onResume();
        if (bidChatListAdapter != null)
            bidChatListAdapter.notifyDataSetChanged();
    }
    Window window;
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
        outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try {
            if (savedInstanceState != null) {
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
            }

            if (LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(ChatListForBid.this);

            inItChatBookingView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_list);
        headerLayoutAllTitleBar = findViewById(R.id.heading);
        new Firebase_Auth_Provider(ChatListForBid.this, false);            //******** In it firebase current user ********
        window = ChatListForBid.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }

        if (savedInstanceState != null) {
            WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
            ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
            BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
            if (LoginZoomToU.NOVA_BOLD == null)
                LoginZoomToU.staticFieldInit(ChatListForBid.this);
        }
        inItChatBookingView();
    }

    private void inItChatBookingView() {
        try {
            chat_now = findViewById(R.id.chat_now);
            chat_now.setOnClickListener(this);

            TextView chatHeaderTxt = (TextView) findViewById(R.id.chatHeaderTxt);
            chatHeaderTxt.setText("Bid Chats");
            ImageView chatCloseBtn = (ImageView) findViewById(R.id.chatCloseBtn);
            chatCloseBtn.setOnClickListener(this);

            if (swipeRefreshChatList == null)
                swipeRefreshChatList = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshChatList);
            swipeRefreshChatList.setOnRefreshListener(this);
            swipeRefreshChatList.setColorSchemeColors(Color.parseColor("#215400"), Color.parseColor("#4f5151"), Color.parseColor("#a1c93a"));
            if (bidChatViewList == null)
                bidChatViewList = (RecyclerView) findViewById(R.id.chatViewList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            bidChatViewList.setLayoutManager(mLayoutManager);
            bidChatViewList.setItemAnimator(new DefaultItemAnimator());
            bidChatViewList.setNestedScrollingEnabled(false);

            if (bidChatListAdapter != null)
                bidChatListAdapter = null;
            bidChatListAdapter = new Bid_ChatListAdapter(this,new Bid_ChatListAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(RequestView_DetailPojo item) {
                    if (!item.getOfferPrice().equals("null") && !item.getOfferPrice().equals("")) {
                        if (item.isCancel())
                            DialogActivity.alertDialogView(ChatListForBid.this, "Alert!", "This bid has been cancelled by you. Please place the bid again to chat.");
                        else {
                            Intent bidChatWithCustomer = new Intent(ChatListForBid.this, ChatView_BidConfirmation.class);
                            bidChatWithCustomer.putExtra("BidIDForChat", item.getId());
                            bidChatWithCustomer.putExtra("CustomerNameForChat", item.getCustomer());
                            bidChatWithCustomer.putExtra("CustomerIDForChat", item.getCustomerID());
                            startActivity(bidChatWithCustomer);
                            bidChatWithCustomer = null;
                        }
                    } else
                        DialogActivity.alertDialogView(ChatListForBid.this, "Alert!", "First place the bid to chat.");
                }
            });

            bidChatViewList.setAdapter(bidChatListAdapter);

            if (RequestView.requestView_pojoList != null) {
                if (RequestView.requestView_pojoList.size() == 0)
                    GetBidChatListTask();
                    //new GetBidChatListTask().execute();
            }

        } catch (Exception e) {
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

    void exitFormChatListView() {
        bidChatListAdapter = null;
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
        GetBidChatListTask();
        //new GetBidChatListTask().execute();
    }

    private void GetBidChatListTask(){
        final String[] webserviceMyRequestList = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                swipeRefreshChatList.setRefreshing(false);
                if (progressDialogToLoadBookingsToChat != null)
                    progressDialogToLoadBookingsToChat = null;
                progressDialogToLoadBookingsToChat = new ProgressDialog(ChatListForBid.this);
                Custom_ProgressDialogBar.inItProgressBar(progressDialogToLoadBookingsToChat);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    webserviceMyRequestList[0] = webServiceHandler.getQuoteRequestOfCourier();
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    webserviceMyRequestList[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressDialogToLoadBookingsToChat != null)
                        if (progressDialogToLoadBookingsToChat.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadBookingsToChat);

                    JSONObject jsonObjectOflIstDelivery = new JSONObject(webserviceMyRequestList[0]);
                    JSONArray getDeliverArray = jsonObjectOflIstDelivery.getJSONArray("data");
                    setBidRequestToChat(getDeliverArray);
                    jsonObjectOflIstDelivery = null;
                    getDeliverArray = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
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

    //********** Get bid chat response to array  ************//
    void setBidRequestToChat(JSONArray setBidChatArray) {
        try {
                RequestView.requestView_pojoList.clear();
                List<RequestView_DetailPojo> newBidList = new ArrayList<RequestView_DetailPojo>();
                for (int i = 0; i < setBidChatArray.length(); i++) {
                    JSONObject objDelivery = setBidChatArray.getJSONObject(i);
                    RequestView_DetailPojo requestView_pojo=new RequestView_DetailPojo();
                    requestView_pojo.setOfferId(objDelivery.getInt("OfferId"));
                    requestView_pojo.setCustomer(objDelivery.getString("Customer"));
                    requestView_pojo.setCustomerID(objDelivery.getString("CustomerId"));
                    requestView_pojo.setPickupSuburb(objDelivery.getString("PickupSuburb"));
                    requestView_pojo.setDropSuburb(objDelivery.getString("DropSuburb"));
                    requestView_pojo.setCancel(objDelivery.getBoolean("IsCancel"));

                    String PickupDateTime=LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDelivery.getString("PickupDateTime"));
                    String[] PickupDateTimeSplit=PickupDateTime.split(" ");
                    requestView_pojo.setPickUpDateTime(PickupDateTimeSplit[0]+" | "+ PickupDateTimeSplit[1]+" "+PickupDateTimeSplit[2]);
                    PickupDateTimeSplit = null;

                    String dropOffDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDelivery.getString("DropDateTime"));
                    String[] dropOffDateTimeSplit = dropOffDateTime.split(" ");
                    requestView_pojo.setDropDateTime(dropOffDateTimeSplit[0]+ " | " + dropOffDateTimeSplit[1]+" "+dropOffDateTimeSplit[2]);
                    dropOffDateTimeSplit = null;

                    requestView_pojo.setNotes(objDelivery.getString("Notes"));
                    requestView_pojo.setId(objDelivery.getInt("RequestId"));
                    requestView_pojo.setTotalBids(objDelivery.getInt("TotalBids"));
                    requestView_pojo.setDistance(objDelivery.getString("Distance"));
                    requestView_pojo.setMinPrice(Functional_Utility.round(objDelivery.getDouble("MinPrice")));
                    requestView_pojo.setMaxPrice(Functional_Utility.round(objDelivery.getDouble("MaxPrice")));

                    try {
                        requestView_pojo.setCourierPrice(Functional_Utility.round(objDelivery.getDouble("CourierPrice")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestView_pojo.setCourierPrice(0.0);
                    }
                    try {
                        requestView_pojo.setSuggestedPrice(objDelivery.getBoolean("IsSuggestedPrice"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        requestView_pojo.setSuggestedPrice(false);
                    }

                    JSONArray images_array = objDelivery.getJSONArray("PackageImages");
                    String total_images = "";
                    if (images_array.length() > 0) {
                        for (int j = 0; j < images_array.length(); j++) {
                            if (total_images != "")
                                total_images = total_images + "," + images_array.getString(j);
                            else
                                total_images = images_array.getString(j);
                        }
                    }
                    requestView_pojo.setPackageImages(total_images);

                    String offerPrice = "";
                    try {
                        offerPrice = objDelivery.getJSONArray("OfferDetails").getJSONObject(0).getString("Price");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    requestView_pojo.setOfferPrice(offerPrice);

                    ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                    for (int k = 0; k < objDelivery.getJSONArray("Shipments").length(); k++) {
                        HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                        objOFShipments.put("Category", objDelivery.getJSONArray("Shipments").getJSONObject(k).getString("Category"));
                        objOFShipments.put("Quantity", objDelivery.getJSONArray("Shipments").getJSONObject(k).getInt("Quantity"));
                        arrayOfShipments.add(objOFShipments);
                        objOFShipments = null;
                    }
                    requestView_pojo.setShipmentsArray(arrayOfShipments);

                    BidRequest_ChatModel bidRequest_chatModel = new BidRequest_ChatModel(ChatListForBid.this, objDelivery.getInt("RequestId"), objDelivery.getString("Customer"), objDelivery.getString("CustomerId"), objDelivery.getBoolean("IsCancel"),objDelivery.getString("Vehicle"), objDelivery.getString("CustomerPhoto"));
                    requestView_pojo.setBidRequest_chatModel(bidRequest_chatModel);

                    if (!requestView_pojo.getOfferPrice().equals("")  && !requestView_pojo.getOfferPrice().equals("null"))
                        RequestView.requestView_pojoList.add(requestView_pojo);
                    else
                        newBidList.add(requestView_pojo);
                    requestView_pojo = null;
                }
            setRecyclerAdapter(newBidList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //************ Refresh recycler view adapter ********//
    void setRecyclerAdapter(List<RequestView_DetailPojo> newBidList) {
        if (bidChatListAdapter != null)
            bidChatListAdapter.notifyDataSetChanged();
        SlideMenuZoom2u.notBidrequestCount(newBidList); //******** Refresh Home view Slide menu adapter
    }
}
