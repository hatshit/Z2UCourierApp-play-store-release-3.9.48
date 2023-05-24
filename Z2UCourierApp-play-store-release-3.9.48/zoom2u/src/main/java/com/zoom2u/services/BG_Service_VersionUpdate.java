package com.zoom2u.services;

import android.app.IntentService;
import android.content.Intent;
import com.zoom2u.webservice.WebserviceHandler;

public class BG_Service_VersionUpdate extends IntentService{

	public BG_Service_VersionUpdate() {
		super("Version update");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		WebserviceHandler webSErviceHandler = new WebserviceHandler();
		webSErviceHandler.CheckForCourierAppVerSionUpdate();
		webSErviceHandler = null;
	}
}
