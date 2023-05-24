package com.zoom2u.services;

import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;
import android.app.IntentService;
import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ServiceForCourierBookingCount extends IntentService{

	static final public String COURIER_BOOKING_COUNT = "com.zoom2u.courierbookingcount";
	
	public ServiceForCourierBookingCount() {
		super("Courier booking count");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			int isAPI_CallRequire = intent.getIntExtra("Is_API_Call_Require", 0);
			if (isAPI_CallRequire == 1) {
				WebserviceHandler webhandler = new WebserviceHandler();
				webhandler.getCourierDeliveriesCount();
				webhandler = null;
			} else {
				WebserviceHandler.reCountActiveBookings(1, isAPI_CallRequire);
			}
			if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11){
    			if(BookingView.bookingViewSelection == 1 || BookingView.bookingViewSelection == 2 || BookingView.bookingViewSelection == 3){
					LocalBroadcastManager broadcasterForDeliveryCount = LocalBroadcastManager.getInstance(ServiceForCourierBookingCount.this);
					Intent intentForRefreshBookingView = new Intent(COURIER_BOOKING_COUNT);
					intentForRefreshBookingView.putExtra("CalledBookingCount", "CalledBookingCount");
					broadcasterForDeliveryCount.sendBroadcast(intentForRefreshBookingView);

					broadcasterForDeliveryCount = null;
					intentForRefreshBookingView = null;
    			}
    		}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
