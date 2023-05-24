package com.zoom2u.slidemenu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.slidemenu.offerrequesthandlr.BidRequest_ChatModel;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.slidemenu.offerrequesthandlr.TimerForBid_Active;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.offerrequesthandlr.EndlessListView;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_Adapter;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestView extends Fragment implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    View rootView;
    RequestView_DetailPojo requestView_pojo;
    EndlessListView listMyRequest;
    RelativeLayout requestListlayout;
    ProgressDialog progressForDeliveryHistory;
    private SwipeRefreshLayout swipeRefreshLayout;
    TextView slideMenuTxt, noQoutesAvailTxt, newBidTab, activeBidTab;
    LinearLayout tabsForBidList;
    public static List<RequestView_DetailPojo> requestView_pojoList;
    List<RequestView_DetailPojo> newRequestView_pojoList;

    TimerForBid_Active timerForBidActive;

    public static int COUNT_FOR_NOTBIDYET = 0;

    public static int newBidSelection = 1;

    public RequestView(){}

    public void setSlideMenuChatCounterTxt(TextView slideMenuTxt){
        slideMenuTxt.setVisibility(View.GONE);
        this.slideMenuTxt = slideMenuTxt;
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        try{
         //   SlideMenuZoom2u.setCourierToOfflineFromChat();
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        }catch(Exception e){
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(savedInstanceState != null){
            try{
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                if(LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(getActivity());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        rootView = inflater.inflate(R.layout.tabview_requests, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(Color.parseColor("#215400"), Color.parseColor("#4f5151"), Color.parseColor("#a1c93a"));
        swipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);// LARGE also can be used

        requestListlayout = (RelativeLayout) rootView.findViewById(R.id.requestListlayout);

        noQoutesAvailTxt = (TextView) rootView.findViewById(R.id.noQoutesAvailTxt);

        noQoutesAvailTxt.setVisibility(View.GONE);
        newBidTab = (TextView) rootView.findViewById(R.id.newBidTab);
        newBidTab.setOnClickListener(this);
        activeBidTab = (TextView) rootView.findViewById(R.id.activeBidTab);
        activeBidTab.setOnClickListener(this);
        newBidSelection=1;
        if (newBidSelection == 1)
            newBidListSelection();
        else
            activeBidListSelection();

        inItRequestViewList();
        inIt_OR_removeBidChatValueeventListners();     //********** Init and remove bid request array and listner ********
        newRequestView_pojoList = new ArrayList<RequestView_DetailPojo>();
        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
              GetMyRequestList();
        else
             DialogActivity.alertDialogView(getActivity(), "No Network!", "No network connection, Please try again later.");

        listMyRequest.setOnLoadMoreListener(loadMoreListener);

        tabsForBidList=rootView.findViewById(R.id.tabsForBidList);
        if(MainActivity.isIsBackGroundGray()) {
            tabsForBidList.setBackgroundResource(R.color.base_color_gray);
            activeBidTab.setBackgroundResource(R.color.base_color_gray);
            newBidTab.setBackgroundResource(R.drawable.selected_background_gray);
        }else{
            tabsForBidList.setBackgroundResource(R.color.base_color1);
            activeBidTab.setBackgroundResource(R.color.base_color1);
            newBidTab.setBackgroundResource(R.drawable.selected_background);
        }



        return rootView;
    }

    void inItRequestViewList(){
        listMyRequest = new EndlessListView(getActivity(), 0 , 0, swipeRefreshLayout);
        listMyRequest.setDivider(null);
        listMyRequest.setBackgroundColor(Color.parseColor("#EDF1FC"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        listMyRequest.setLayoutParams(params);
        params = null;

        requestListlayout.addView(listMyRequest);
    }

    public void setAdapter() {
        try {
            if (newBidSelection == 1) {
                if (newRequestView_pojoList.size() > 0) {
                    timerForBidActive = new TimerForBid_Active(getActivity());
                    TimerForBid_Active.requestView_adapter = new RequestView_Adapter(getActivity(), RequestView.this, newRequestView_pojoList);
                    listMyRequest.setAdapter(timerForBidActive.requestView_adapter);
                    TimerForBid_Active.requestView_adapter.notifyDataSetChanged();
                    listMyRequest.setVisibility(View.VISIBLE);
                    noQoutesAvailTxt.setVisibility(View.GONE);
                } else {
                    noQoutesAvailTxt.setVisibility(View.VISIBLE);
                    listMyRequest.setVisibility(View.GONE);
                }
            } else {
                if (requestView_pojoList.size() > 0) {
                    timerForBidActive = new TimerForBid_Active(getActivity());
                    TimerForBid_Active.requestView_adapter = new RequestView_Adapter(getActivity(), RequestView.this, requestView_pojoList);
                    listMyRequest.setAdapter(timerForBidActive.requestView_adapter);
                    TimerForBid_Active.requestView_adapter.notifyDataSetChanged();
                    listMyRequest.setVisibility(View.VISIBLE);
                    noQoutesAvailTxt.setVisibility(View.GONE);
                } else {
                    noQoutesAvailTxt.setVisibility(View.VISIBLE);
                    listMyRequest.setVisibility(View.GONE);
                }
            }

            if (newRequestView_pojoList.size() > 0)
                newBidTab.setText("New ("+newRequestView_pojoList.size()+")");
            else
                newBidTab.setText("New");

            if (requestView_pojoList.size() > 0)
                activeBidTab.setText("Active ("+requestView_pojoList.size()+")");
            else
                activeBidTab.setText("Active");

            SlideMenuZoom2u.notBidrequestCount(newRequestView_pojoList);           //********** Refresh main Home Slide view adapter ********
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);

        setVisibilityOfChatIcon();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SlideMenuZoom2u.setCourierToOnlineForChat();
        SlideMenuZoom2u.countChatBookingView = slideMenuTxt;
        setVisibilityOfChatIcon();

      /*  if (timerForBidActive != null)
            timerForBidActive.callTimerActiveBookingList();*/
    }

    private void setVisibilityOfChatIcon() {
        if (requestView_pojoList != null) {
            if (requestView_pojoList.size() > 0)
                BidRequest_ChatModel.showTotalUnreadCountChat(slideMenuTxt);
            else
                Model_DeliveriesToChat.showExclamationForUnreadChat(slideMenuTxt);
        } else
            Model_DeliveriesToChat.showExclamationForUnreadChat(slideMenuTxt);
    }


    public static void inIt_OR_removeBidChatValueeventListners(){
        if (requestView_pojoList == null)
            requestView_pojoList = new ArrayList<RequestView_DetailPojo>();
        else if (requestView_pojoList.size() > 0){
            for (RequestView_DetailPojo bidRequest_chatModel : requestView_pojoList)
                ChatViewBookingScreen.mFirebaseRef.child("quote-request-comments/"+bidRequest_chatModel.getId()+"_"+LoginZoomToU.courierID+"/status/customer/unread").removeEventListener(bidRequest_chatModel.getBidRequest_chatModel().getValueEventListnerOfCustomerBidChat());
            requestView_pojoList.clear();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
       /* if (timerForBidActive != null)
            timerForBidActive.removeMenulogHandlerInActiveList();*/
    }

    @Override
    public void onRefresh() {
        inIt_OR_removeBidChatValueeventListners();
        if (newRequestView_pojoList != null)
            newRequestView_pojoList.clear();
        else
            newRequestView_pojoList = new ArrayList<RequestView_DetailPojo>();
        GetMyRequestList();
        //new GetMyRequestList().execute();
        swipeRefreshLayout.setRefreshing(false);
    }

    Dialog cancelBidRequestDialog;
    public void cancelBidRequestConfirmationDialog (final int offerId, final boolean isRejectBid) {

        if (cancelBidRequestDialog != null) {
            if (cancelBidRequestDialog.isShowing())
                cancelBidRequestDialog.dismiss();
            cancelBidRequestDialog = null;
        }

        cancelBidRequestDialog = new Dialog(getActivity());
        cancelBidRequestDialog.setCancelable(false);
        cancelBidRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        cancelBidRequestDialog.setContentView(R.layout.logoutwindow);

        Window window = cancelBidRequestDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView cancelBidRequestMsgTxt = (TextView) cancelBidRequestDialog.findViewById(R.id.logoutWindowMessageText);

        if (isRejectBid)
            cancelBidRequestMsgTxt.setText("Are you sure, you want to Reject this bid?");
        else
            cancelBidRequestMsgTxt.setText("Are you sure, you want to Cancel this bid?");

        Button cancelBidRequestNoBtn = (Button) cancelBidRequestDialog.findViewById(R.id.logoutWindowCancelBtn);

        cancelBidRequestNoBtn.setText("No");
        cancelBidRequestNoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBidRequestDialog.dismiss();
            }
        });

        Button cancelBidRequestYesBtn = (Button) cancelBidRequestDialog.findViewById(R.id.logoutWindowLogoutBtn);

        cancelBidRequestYesBtn.setText("Yes");
        cancelBidRequestYesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelBidRequestDialog.dismiss();
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    if (isRejectBid)
                        CancelBidRequest(offerId, true);
                        //new CancelBidRequest(offerId, true).execute();
                    else
                         CancelBidRequest(offerId, false);
                        //new CancelBidRequest(offerId, false).execute();
                else
                    DialogActivity.alertDialogView(getActivity(), "No Network!", "No network connection, Please try again later.");
            }
        });
        cancelBidRequestDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newBidTab:
                newBidListSelection();
                break;
            case R.id.activeBidTab:
                activeBidListSelection();
                break;
        }
    }

    /************ New bid list selection **********/
    void newBidListSelection(){
        newBidSelection = 1;
        if(MainActivity.isIsBackGroundGray()) {
            newBidTab.setBackgroundResource(R.drawable.selected_background_gray);
            activeBidTab.setBackgroundResource(R.color.base_color_gray);
        }else{
            newBidTab.setBackgroundResource(R.drawable.selected_background);
            activeBidTab.setBackgroundResource(R.color.base_color1);
        }
        //focusOnBookingBtn(newBidTab);
        //clearFocusFromBookingBtn(activeBidTab);
        setAdapter();
    }

    /************ Active bid list selection **********/
    void activeBidListSelection(){
        newBidSelection = 2;
        if(MainActivity.isIsBackGroundGray()) {
            activeBidTab.setBackgroundResource(R.drawable.selected_background_gray);
            newBidTab.setBackgroundResource(R.color.base_color_gray);
        }else{
            activeBidTab.setBackgroundResource(R.drawable.selected_background);
            newBidTab.setBackgroundResource(R.color.base_color1);
        }
        // focusOnBookingBtn(activeBidTab);
       // clearFocusFromBookingBtn(newBidTab);
        setAdapter();
    }

    // Focus on bid tab
    void focusOnBookingBtn(TextView bookingBtnTxtView){
        bookingBtnTxtView.setBackgroundColor(Color.parseColor("#374350"));
        bookingBtnTxtView.setTextColor(Color.WHITE);
    }

    // Clear focus from tab
    void clearFocusFromBookingBtn(TextView bookingBtnTxtView){
        bookingBtnTxtView.setBackgroundColor(getResources().getColor(R.color.lightgray2));
        bookingBtnTxtView.setTextColor(Color.parseColor("#374350"));
    }

    private void CancelBidRequest(int offerId,boolean isRejectBid){
        final String[] responseStrCancelBidRequest = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(getActivity());
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                List<RequestView_DetailPojo> requestPojoList = new ArrayList<RequestView_DetailPojo>();
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    if (isRejectBid)
                        responseStrCancelBidRequest[0] = handler.rejectBidOfCourier(offerId);
                    else
                        responseStrCancelBidRequest[0] = handler.cancelBidRequest(offerId);
                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (!responseStrCancelBidRequest[0].equals("")) {
                        if(new JSONObject(responseStrCancelBidRequest[0]).getBoolean("success")){
                            if (isRejectBid) {
                                RequestView.COUNT_FOR_NOTBIDYET--;
                                SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
                                new LoadChatBookingArray(getActivity(), 2);  //********* Refresh Bid chat only *******
                                Toast.makeText(getActivity(), "Bid request rejected successfully", Toast.LENGTH_LONG).show();
                            } else {
                                inIt_OR_removeBidChatValueeventListners();
                                Toast.makeText(getActivity(), "Bid request cancelled successfully", Toast.LENGTH_LONG).show();
                            }
                            GetMyRequestList();
                            //new GetMyRequestList().execute();
                        } else
                            DialogActivity.alertDialogView(getActivity(), "Sorry!", "Something went wrong, Please try again later.");
                    } else
                        DialogActivity.alertDialogView(getActivity(), "Server error!", "Something went wrong, Please try again later.");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(getActivity(), "Server error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();

    }



    private void GetMyRequestList(){

        final String[] webserviceMyRequestList = {"success"};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(getActivity());
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                List<RequestView_DetailPojo> requestPojoList = new ArrayList<RequestView_DetailPojo>();
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    webserviceMyRequestList[0] = handler.getQuoteRequestOfCourier();
                    JSONObject jsonObjectOflIstDelivery=new JSONObject(webserviceMyRequestList[0]);
                    JSONArray getDeliverArray = jsonObjectOflIstDelivery.getJSONArray("data");
                    requestPojoList = getAllDelHistory(getDeliverArray);
                    getDeliverArray = null;
                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                    setAdapter();
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(getActivity(), "Sorry!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();

    }


    /**** Endless Listview for Loading More data in the List ****/
    private EndlessListView.OnLoadMoreListener loadMoreListener = new EndlessListView.OnLoadMoreListener() {

        @Override
        public boolean onLoadMore() {
            return false;
        }
    };

    private void loadMoreData() {
 //       new GetMyRequestList().execute((Void) null);
    }

    public List<RequestView_DetailPojo> getAllDelHistory(JSONArray getDeliverArray) {
        List<RequestView_DetailPojo> tempArrayPojo = new ArrayList<RequestView_DetailPojo>();
        try {
            if (newRequestView_pojoList == null)
                newRequestView_pojoList = new ArrayList<RequestView_DetailPojo>();
            else
                newRequestView_pojoList.clear();

            requestView_pojoList.clear();

            for (int i = 0; i < getDeliverArray.length(); i++) {
                JSONObject objDelivery = getDeliverArray.getJSONObject(i);
                requestView_pojo=new RequestView_DetailPojo();
                requestView_pojo.setOfferId(objDelivery.getInt("OfferId"));
                requestView_pojo.setCustomer(objDelivery.getString("Customer"));
                requestView_pojo.setCustomerID(objDelivery.getString("CustomerId"));
                requestView_pojo.setPickupSuburb(objDelivery.getString("PickupSuburb"));
                requestView_pojo.setDropSuburb(objDelivery.getString("DropSuburb"));
                requestView_pojo.setCancel(objDelivery.getBoolean("IsCancel"));
                requestView_pojo.setAverageBid(objDelivery.getString("AverageBid"));
                requestView_pojo.setVehicle(objDelivery.getString("Vehicle"));
                requestView_pojo.setCustomerPhoto(objDelivery.getString("CustomerPhoto"));

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
                    requestView_pojo.setCourierPrice(objDelivery.getDouble("CourierPrice"));
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

//                JSONArray images_array = objDelivery.getJSONArray("PackageImages");
//                String total_images = "";
//                if (images_array.length() > 0) {
//                    for (int j = 0; j < images_array.length(); j++) {
//                        if (total_images != "")
//                            total_images = total_images + "," + images_array.getString(j);
//                        else
//                            total_images = images_array.getString(j);
//                    }
//                }
//                requestView_pojo.setPackageImages(total_images);

                String offerPrice = null;
                try {
                    offerPrice = "";
                    offerPrice = objDelivery.getJSONObject("OfferDetails").getString("Price");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                requestView_pojo.setOfferPrice(offerPrice);

                ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                for (int k = 0; k < objDelivery.getJSONArray("Shipments").length(); k++) {
                    HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                    JSONObject jObjOfShipmentItem = objDelivery.getJSONArray("Shipments").getJSONObject(k);
                    objOFShipments.put("Category", jObjOfShipmentItem.getString("Category"));
                    objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("Quantity"));
                    try {
                        objOFShipments.put("LengthCm", jObjOfShipmentItem.getInt("LengthCm"));
                        objOFShipments.put("WidthCm", jObjOfShipmentItem.getInt("WidthCm"));
                        objOFShipments.put("HeightCm", jObjOfShipmentItem.getInt("HeightCm"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        objOFShipments.put("LengthCm", 0);
                        objOFShipments.put("WidthCm", 0);
                        objOFShipments.put("HeightCm", 0);
                    }

                    try {
                        objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("ItemWeightKg"));
                        objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("TotalWeightKg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        objOFShipments.put("ItemWeightKg", 0);
                        objOFShipments.put("TotalWeightKg", 0);
                    }

                    arrayOfShipments.add(objOFShipments);
                    objOFShipments = null;
                }
                requestView_pojo.setShipmentsArray(arrayOfShipments);
                requestView_pojo.setPrimaryPackageImage(objDelivery.getString("PrimaryPackageImage"));

                tempArrayPojo.add(requestView_pojo);
            }

            for (RequestView_DetailPojo requestView_pojo : tempArrayPojo) {
                if (requestView_pojo.getOfferPrice().equals("")  || requestView_pojo.getOfferPrice().equals("null"))
                    newRequestView_pojoList.add(requestView_pojo);
                else {
                    BidRequest_ChatModel bidRequest_chatModel = new BidRequest_ChatModel(getActivity(), requestView_pojo.getId(), requestView_pojo.getCustomer(), requestView_pojo.getCustomerID(), requestView_pojo.isCancel(),requestView_pojo.getVehicle(),requestView_pojo.getCustomerPhoto());
                    requestView_pojo.setBidRequest_chatModel(bidRequest_chatModel);
                    requestView_pojoList.add(requestView_pojo);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tempArrayPojo;
    }
}


