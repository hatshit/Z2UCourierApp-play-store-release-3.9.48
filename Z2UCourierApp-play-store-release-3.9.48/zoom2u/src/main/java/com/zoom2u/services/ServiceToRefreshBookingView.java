package com.zoom2u.services;

import java.util.Timer;
import java.util.TimerTask;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;
import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

public class ServiceToRefreshBookingView  extends IntentService {

	 LocalBroadcastManager broadcasterToRefreshBookingView;
	 String recieveDataToRefreshBVStr;
	 public static TimerTask timerTask;
	 Timer timerToRefreshBV;
	 
	 public ServiceToRefreshBookingView() {
		super("Refresh booking view");
	}

	 public void sendResult(String message) {
		    try {
				Intent intent = new Intent(ServiceToUpdateNewBookingList.SILENT_NOTIFICATION_NEW_JOB);
				if(!message.equals(""))
				    intent.putExtra("result", message);
				broadcasterToRefreshBookingView.sendBroadcast(intent);
				intent = null;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{	
			broadcasterToRefreshBookingView = LocalBroadcastManager.getInstance(ServiceToRefreshBookingView.this);
			if(timerToRefreshBV != null)
				timerToRefreshBV.cancel();
			else
				timerToRefreshBV = new Timer();
		 	
			if(timerTask != null)
		 		timerTask = null;
		 	timerTask = new TimerTask() {
				@Override
				public void run() {
					try {
						WebserviceHandler webServiceHandler = new WebserviceHandler();
						if(BookingView.bookingViewSelection == 1)
							recieveDataToRefreshBVStr = webServiceHandler.getNewBookingList(0);
						else
							timerToRefreshBV.cancel();
						Log.i("", "**********  New booking service called **********");
						webServiceHandler = null;
						if(!recieveDataToRefreshBVStr.equals(""))
							sendResult(recieveDataToRefreshBVStr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			timerToRefreshBV.schedule(timerTask, 20000, 20000);
			timerToRefreshBV = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}