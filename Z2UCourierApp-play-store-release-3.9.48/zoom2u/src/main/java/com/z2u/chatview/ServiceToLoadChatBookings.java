package com.z2u.chatview;

import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.zoom2u.webservice.WebserviceHandler;

public class ServiceToLoadChatBookings extends IntentService {

    static final public String LOAD_CHAT = "com.zoom2u.loadchat";

    public ServiceToLoadChatBookings() {
        super("Service Load Booking Chat");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        WebserviceHandler webSErviceHandler = new WebserviceHandler();
        try {

            //******* IsListLoadAfterLogin = 0  //****  Refresh Booking Chat list array but not bid chat array called after drop, Accept, admin allocated, returned.
            //******* IsListLoadAfterLogin = 1  //****  Load booking and bid chat list array to initiate chat after login
            //******* IsListLoadAfterLogin = 2  //****  Do not Load chat list array only refresh bid chat list array

            Intent intent1 = new Intent(LOAD_CHAT);
            if (intent.getIntExtra("IsListLoadAfterLogin", 0) != 2) {
                String responseStrToLoadChat = webSErviceHandler.getBookingForChatList();
                intent1.putExtra("responseChatBookings", responseStrToLoadChat);
            }
            if (intent.getIntExtra("IsListLoadAfterLogin", 0) != 0) {
                String responseStrToLoadBid = webSErviceHandler.getQuoteRequestOfCourier();
                intent1.putExtra("responseBidChatList", responseStrToLoadBid);
            }

            LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(ServiceToLoadChatBookings.this);
            broadcaster.sendBroadcast(intent1);
            broadcaster = null;
            intent1 = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        webSErviceHandler = null;
    }
}