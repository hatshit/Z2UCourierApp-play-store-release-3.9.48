package com.zoom2u.services;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.utility.MyLocation;
import com.zoom2u.utility.MyLocation.LocationResult;
import com.zoom2u.webservice.WebserviceHandler;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class ServiceForSendLatLong extends IntentService {

	WebserviceHandler serviceHandl;
    private Timer locationTimer;
    public static TimerTask locationTimerTask;
    public static String courierAvailabilityResponseStr = "0";
    static final public String SILENT_NOTIFICATION = "com.zoom2u.service";

	public ServiceForSendLatLong() {
		super("Start Service");
		serviceHandl =  new WebserviceHandler();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
            if(locationTimer != null)
                locationTimer.cancel();
            else
                locationTimer = new Timer();

        //    Log.e("Location updates","  ======= Location service started ========= ");

			if(!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")){
                 String currentTime = getCurrentTime();
                 serviceHandl.sendUserLatLong(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude, currentTime);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        locationTrace();
	}

    void locationTrace(){
        try {
            if (locationTimerTask != null)
                locationTimerTask = null;
            locationTimerTask = new TimerTask() {
                @Override
                public void run() {
                    updateLocationInBG();
                }
            };

            locationTimer.scheduleAtFixedRate(locationTimerTask, 60000, 60000);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

   private void updateLocationInBG(){
            LocationResult locationResult = new LocationResult() {
					@Override
					public void gotLocation(Location location) {
			try {
				String newLat = String.valueOf(location.getLatitude());
				String newLong = String.valueOf(location.getLongitude());
            //    Toast.makeText(ServiceForSendLatLong.this, "  ======= Latitude, Longitude  "+newLat+", "+newLong, Toast.LENGTH_LONG).show();
				/****************  New with check distance for 100 meter ****************/
                Location previousLocation = null;
                try {
                    previousLocation = new Location("");
                    previousLocation.setLatitude(Double.parseDouble(LoginZoomToU.getCurrentLocatnlatitude));
                    previousLocation.setLongitude(Double.parseDouble(LoginZoomToU.getCurrentLocatnLongitude));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                Location currentLocation = new Location("");
				currentLocation.setLatitude(Double.parseDouble(newLat));
				currentLocation.setLongitude(Double.parseDouble(newLong));
							
				double distanceFromLastLocation = previousLocation.distanceTo(currentLocation);
            //    Log.e("Location updates","  ======= Latitude, Longitude and Distance "+newLat+", "+newLong+", "+distanceFromLastLocation);
				if(distanceFromLastLocation  >= 20) {
                        LoginZoomToU.getCurrentLocatnlatitude = String.valueOf(location.getLatitude());
                        LoginZoomToU.getCurrentLocatnLongitude = String.valueOf(location.getLongitude());

                        String currentTime = getCurrentTime();
                        String savedLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude + "," + currentTime;

                        SharePreference_FailedImg prefForLocation = new SharePreference_FailedImg();
                        prefForLocation.saveRecentLocation(ServiceForSendLatLong.this, savedLocation);

                        String savedLocationFromPref = prefForLocation.getLocationFromPref(ServiceForSendLatLong.this);
                        prefForLocation = null;
                        // Log.e("", "  ******  Saved location  **********  " + savedLocationFromPref);
                        //  String serverResponse = "Location test";
                        if (!savedLocationFromPref.equals("")) {
                           try {
                                String[] savedLocArray = savedLocationFromPref.split(",");
                                serviceHandl.sendUserLatLong(savedLocArray[0], savedLocArray[1], savedLocArray[2]);
                                savedLocArray = null;
                           } catch (Exception e) {
                                e.printStackTrace();
                               serviceHandl.sendUserLatLong(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude, currentTime);
                           }
                        } else
                            serviceHandl.sendUserLatLong(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude, currentTime);
                }
                previousLocation = null;
                currentLocation = null;
		    } catch (Exception e) {
		      e.printStackTrace();
		    }
         }};

	     MyLocation myLocation = new MyLocation();
	     myLocation.getLocation(getApplicationContext(), locationResult);
         myLocation = null;
	   }
//		};

    public static String getCurrentTime(){
        Calendar now = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        format.setTimeZone(TimeZone.getTimeZone("IST"));
        String currentTime = format.format(now.getTime());
        now = null;
        format = null;

        return currentTime;
    }
}
