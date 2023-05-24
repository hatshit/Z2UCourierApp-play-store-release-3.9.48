package com.zoom2u.services;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.webservice.WebserviceHandler;
import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServiceToUpdate_ActiveBookingList  extends IntentService{

	static final public String SILENT_NOTIFICATION = "com.zoom2u.service";
	
	public ServiceToUpdate_ActiveBookingList() {
		super("Service to update menulog delivery list");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WebserviceHandler webSErviceHandler = new WebserviceHandler();
		try {
			String responseStrToUpdate;
			if (LoginZoomToU.filterDayActiveListStr.equals("DeliveryRuns"))
				responseStrToUpdate = webSErviceHandler.getActiveBookingList(LoginZoomToU.filterDayActiveListStr, 0, "", LoginZoomToU.getDHLBookingStr);
			else
				responseStrToUpdate = webSErviceHandler.getActiveBookingList(LoginZoomToU.filterDayActiveListStr, 0, "", "");

			Intent intent1 = new Intent(SILENT_NOTIFICATION);
			intent1.putExtra("result", responseStrToUpdate);
			LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(PushReceiver.contextOfPushReceiver);
			broadcaster.sendBroadcast(intent1);
			intent1 = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		webSErviceHandler = null;
	}

}

