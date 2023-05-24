package com.zoom2u.services;

import com.zoom2u.PushReceiver;
import com.zoom2u.webservice.WebserviceHandler;
import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

public class ServiceWeeklyCourierUpdate extends IntentService{

	static final public String WEEKLY_COURIER_UPDATE_STR = "com.zoom2u.weeklyupdatecourier";
	
	public ServiceWeeklyCourierUpdate() {
		super("Service Weekly update courier");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WebserviceHandler webSErviceHandler = new WebserviceHandler();
		try {
			String responseStrToUpdate = webSErviceHandler.weeklyUpdateCourier();
			Log.e("", "------------------------- Call ServiceToUpdate_New booking list ");
			Intent intent1 = new Intent(WEEKLY_COURIER_UPDATE_STR);
			intent1.putExtra("responseWeeklyupdate", responseStrToUpdate);
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
