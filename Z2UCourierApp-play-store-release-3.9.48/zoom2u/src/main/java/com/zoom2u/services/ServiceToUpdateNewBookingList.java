package com.zoom2u.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;
import com.zoom2u.PushReceiver;
import com.zoom2u.webservice.WebserviceHandler;

public class ServiceToUpdateNewBookingList extends IntentService{

	static final public String SILENT_NOTIFICATION_NEW_JOB = "com.zoom2u.newbookingservice";
	
	public ServiceToUpdateNewBookingList(){
		super("Service to update new delivery list");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WebserviceHandler webSErviceHandler = new WebserviceHandler();
		try {
			String responseStrToUpdate = webSErviceHandler.getNewBookingList(0);
			Log.e("", "------------------------- Call ServiceToUpdate_New booking list ");
			Intent intent1 = new Intent(SILENT_NOTIFICATION_NEW_JOB);
			intent1.putExtra("result", responseStrToUpdate);
			LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(PushReceiver.contextOfPushReceiver);
			broadcaster.sendBroadcast(intent1);
			broadcaster = null;
			intent1 = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		webSErviceHandler = null;
	}

}
