package com.z2u.chatview;

import android.app.IntentService;
import android.content.Intent;
import com.zoom2u.webservice.WebserviceHandler;

public class ServiceNotifyCustomerAboutChat  extends IntentService {

    public ServiceNotifyCustomerAboutChat() {
        super("Service notify customer about chat");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            WebserviceHandler serviceHandlUpdateToken =  new WebserviceHandler();
            String customerId = intent.getStringExtra("CustomerId");
            String msgStr = intent.getStringExtra("Message");
            int isBidChat = intent.getIntExtra("isBidChat", 0);
            serviceHandlUpdateToken.notifyCustomerAboutChatMessage(customerId, msgStr, isBidChat);
            serviceHandlUpdateToken = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}