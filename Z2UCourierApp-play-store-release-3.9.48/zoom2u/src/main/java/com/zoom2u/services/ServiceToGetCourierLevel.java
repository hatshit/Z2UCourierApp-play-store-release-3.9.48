package com.zoom2u.services;

import android.app.IntentService;
import android.content.Intent;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.webservice.WebserviceHandler;

public class ServiceToGetCourierLevel extends IntentService{

	public ServiceToGetCourierLevel() {
		super("Service get courier level");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			WebserviceHandler serviceHandlUpdateToken =  new WebserviceHandler();

			String currentTime = ServiceForSendLatLong.getCurrentTime();
			serviceHandlUpdateToken.sendUserLatLong(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude, currentTime);

			serviceHandlUpdateToken.getCurrentCourierLevel();
			if(LoginZoomToU.courierID.equals(""))
				serviceHandlUpdateToken.CheckRoutific();
			serviceHandlUpdateToken = null;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
}