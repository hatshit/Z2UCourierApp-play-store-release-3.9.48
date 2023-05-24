package com.z2u.chatview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.slidemenu.offerrequesthandlr.BidRequest_ChatModel;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.utility.Functional_Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoadChatBookingArray {

    public static ArrayList<Model_DeliveriesToChat> arrayOfChatDelivery = null;
    BroadcastReceiver receiverToGetChatBookingListData;
    Context context;
    public static int NOTIFICATIONID_FOR_CHAT = 0;

    public LoadChatBookingArray(Context context, final int isFromLoginScreen){

        //******* isFromLoginScreen = 0  //****  Refresh Booking Chat list array but not bid chat array called after drop, Accept, admin allocated, returned.
        //******* isFromLoginScreen = 1  //****  Load booking and bid chat list array to initiate chat after login
        //******* isFromLoginScreen = 2  //****  Do not Load chat list array but only refresh bid chat list array

        this.context = context;
        if (ChatViewBookingScreen.mFirebaseRef == null)
            ChatViewBookingScreen.mFirebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatViewBookingScreen.FIREBASE_URL);
        if (arrayOfChatDelivery == null)
            arrayOfChatDelivery = new ArrayList<Model_DeliveriesToChat>();
        if (RequestView.requestView_pojoList == null)
            RequestView.requestView_pojoList = new ArrayList<RequestView_DetailPojo>();

        Intent getChatBookingListSevice = new Intent(context, ServiceToLoadChatBookings.class);
        getChatBookingListSevice.putExtra("IsListLoadAfterLogin", isFromLoginScreen);
        context.startService(getChatBookingListSevice);
        getChatBookingListSevice = null;

        //**********  Call local broadcast on silent notification for calculated ETA  ********//
        receiverToGetChatBookingListData =
                new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (intent.getStringExtra("responseChatBookings") != null)
                         setDeliveriesToChat(intent.getStringExtra("responseChatBookings"));
                    if (intent.getStringExtra("responseBidChatList") != null)
                         setBidRequestItems(context, intent.getStringExtra("responseBidChatList"));
                        //********* Don't load bid chat id Bid chat notification is available *******
                        //*********** Open bid chat list on notification click when launch after kill ***********
                    if (isFromLoginScreen == 1) {
                        if (NOTIFICATIONID_FOR_CHAT == 3 && PushReceiver.prefrenceForPushy.getString("isChatForBidRequest", "").equals("1")) {
                            PushReceiver.loginEditorForPushy.putString("isChatForBidRequest", "");
                            PushReceiver.loginEditorForPushy.commit();
                            Intent intentToOpenChatList = new Intent(context, ChatViewBookingScreen.class);
                            context.startActivity(intentToOpenChatList);
                            intentToOpenChatList = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NOTIFICATIONID_FOR_CHAT = 0;
            }
        };

        //*************  Register local broadcast receiver   ****************//
        if (receiverToGetChatBookingListData != null)
            LocalBroadcastManager.getInstance(context).registerReceiver((receiverToGetChatBookingListData), new IntentFilter(ServiceToLoadChatBookings.LOAD_CHAT));
    }

    //********** Get Deliveries to chat response to array  ************//
    void setDeliveriesToChat(String setDeliveriesToChatStr){
        try {
            if (arrayOfChatDelivery.size() > 0){
                for (Model_DeliveriesToChat model_deliveriesToChat : arrayOfChatDelivery){
                    if (model_deliveriesToChat.getCustomer().equals("Zoom2u-Admin")) {
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS+model_deliveriesToChat.getCourierId()+"/status/courier/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCourierChat());
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_UNREADS + model_deliveriesToChat.getCourierId() + "/status/admin/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCustomerChat());
                    }else{
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT+model_deliveriesToChat.getBookingId()+"_"+model_deliveriesToChat.getCourierId()+"/status/courier/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCourierChat());
                        ChatViewBookingScreen.mFirebaseRef.child(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT + model_deliveriesToChat.getBookingId()+"_"+model_deliveriesToChat.getCourierId() + "/status/customer/unread").removeEventListener(model_deliveriesToChat.getValueEventListnerOfCustomerChat());
                    }
                }
                arrayOfChatDelivery.clear();
            }

            if (ChatViewBookingScreen.mFirebaseRef != null)
                ChatViewBookingScreen.mFirebaseRef = null;
            ChatViewBookingScreen.mFirebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatViewBookingScreen.FIREBASE_URL);
            Model_DeliveriesToChat model_deliveriesToChat = new Model_DeliveriesToChat(context, LoginZoomToU.courierID, "Zoom2u-Admin");
            arrayOfChatDelivery.add(model_deliveriesToChat);
            if (!setDeliveriesToChatStr.equals("")){
                JSONObject mainJObjOfChatDelivery = new JSONObject(setDeliveriesToChatStr);
                if (mainJObjOfChatDelivery.getBoolean("Success")){
                    JSONArray jsonArrayOfChatDelivery = new JSONArray(mainJObjOfChatDelivery.getString("data"));
                    if (jsonArrayOfChatDelivery.length() > 0){
                        for (int i = 0; i < jsonArrayOfChatDelivery.length(); i++){
                            Model_DeliveriesToChat model_deliveriesToChatCustomer = new Model_DeliveriesToChat(context, jsonArrayOfChatDelivery.getJSONObject(i));
                            arrayOfChatDelivery.add(model_deliveriesToChatCustomer);
                        }
                    }
                }
            }

            //*************  Register local broadcast receiver   ****************//
            if (receiverToGetChatBookingListData != null)
                LocalBroadcastManager.getInstance(context).unregisterReceiver(receiverToGetChatBookingListData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        SlideMenuZoom2u.setCourierToOfflineFromChat();
    }

    //********** Get bid list to init bid chat response to array  ************//
    public static void setBidRequestItems(Context context, String bidRequestItemResponseStr) {
        try {
            RequestView.inIt_OR_removeBidChatValueeventListners();
            List<RequestView_DetailPojo> newBidList = new ArrayList<RequestView_DetailPojo>();

            if (!bidRequestItemResponseStr.equals("")) {
                JSONObject jsonObjectOflIstDelivery=new JSONObject(bidRequestItemResponseStr);
                JSONArray getDeliverArray = jsonObjectOflIstDelivery.getJSONArray("data");
                for (int i = 0; i < getDeliverArray.length(); i++) {
                    JSONObject objDelivery = getDeliverArray.getJSONObject(i);

                    RequestView_DetailPojo requestView_pojo=new RequestView_DetailPojo();
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

                    BidRequest_ChatModel bidRequest_chatModel = new BidRequest_ChatModel(context, objDelivery.getInt("RequestId"), objDelivery.getString("Customer"), objDelivery.getString("CustomerId"), objDelivery.getBoolean("IsCancel"), objDelivery.getString("Vehicle"), objDelivery.getString("CustomerPhoto"));
                    requestView_pojo.setBidRequest_chatModel(bidRequest_chatModel);
                    bidRequest_chatModel = null;

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


                    String primary_image = objDelivery.getString("PrimaryPackageImage");
//                    String total_images = "";
//                    if (images_array.length() > 0) {
//                        for (int j = 0; j < images_array.length(); j++) {
//                            if (total_images != "")
//                                total_images = total_images + "," + images_array.getString(j);
//                            else
//                                total_images = images_array.getString(j);
//                        }
//                    }
//                    requestView_pojo.setPackageImages(total_images);
                    requestView_pojo.setPackageImages(primary_image);
                    String offerPrice = "";
                    try {
                        offerPrice = objDelivery.getJSONObject("OfferDetails").getString("Price");
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

                    if (!requestView_pojo.getOfferPrice().equals("")  && !requestView_pojo.getOfferPrice().equals("null"))
                        RequestView.requestView_pojoList.add(requestView_pojo);
                    else
                        newBidList.add(requestView_pojo);
                }
                SlideMenuZoom2u.notBidrequestCount(newBidList); //******** Refresh Home view Slide menu adapter
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
   ******** Update Firebase "Recipient-Admin-Chat" as CLOSED for DHL booking after drop off and Return to DHL **********
   */
    public static void updateRecipientAdminChatAsCloseForDHL(final int bookingId){
        try {
            if (ChatViewBookingScreen.mFirebaseRef != null)
                ChatViewBookingScreen.mFirebaseRef.child("recipient-admin-chat/booking-details/"+bookingId+"/close/").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            ChatViewBookingScreen.mFirebaseRef.child("recipient-admin-chat/booking-details/"+bookingId+"/close/").setValue(1);
                            ChatViewBookingScreen.mFirebaseRef.child("recipient-admin-chat/unread-chats/"+bookingId).setValue(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}