package com.zoom2u.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.LoginZoomToU;

public class ServiceUpdateDeviceToken extends IntentService{

	WebserviceHandler serviceHandlUpdateToken;
	
	public ServiceUpdateDeviceToken() {
		super("Start Service update token");
		try {
			serviceHandlUpdateToken =  new WebserviceHandler();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			//serviceHandlUpdateToken.getQuoteRequestsForOfflineCouriers();

            serviceHandlUpdateToken.getCurrentCourierLevel();
			serviceHandlUpdateToken.sendDeviceTokenID(LoginZoomToU.pushyRegId, getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
			Log.e("", "------------------    "+LoginZoomToU.pushyRegId);
			ServiceForSendLatLong.courierAvailabilityResponseStr = serviceHandlUpdateToken.getCourierAvailability();
			serviceHandlUpdateToken = null;
		}catch(Exception e){
			e.printStackTrace();
			ServiceForSendLatLong.courierAvailabilityResponseStr = "0";
		}
	}
	
}
