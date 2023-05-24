package com.zoom2u.utility;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.services.ServiceForSendLatLong;

public class MyLocation implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final String TAG = "LocationService";
    LocationResult locationResult;
    Context con;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;

    @Override
    public void onConnected(Bundle bundle) {
       // Log.d(TAG, "onConnected");
        locationRequest = LocationRequest.create();
        // milliseconds at which our app can handle location updates
        locationRequest.setInterval(60000); // milliseconds
        locationRequest.setFastestInterval(60000); // the fastest rate in
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(0);
        try { LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }catch (SecurityException e){
            e.printStackTrace();
            // Throws SecurityException when location permission denied by user
        }
    }

    private void stopLocationUpdates() {
       // Log.d(TAG, "  Call to disconnected...");
        if (googleApiClient != null && googleApiClient.isConnected()) {
       //     Log.d(TAG, "  Connection disconnected...");
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
       // Log.e(TAG, "onConnectionFailed");
        stopLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
     //   Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            locationResult.gotLocation(location); // Call background service for location update
            stopLocationUpdates();                // stop location update after updating
        }
    }

    public boolean getLocation(Context context, LocationResult result) {
        con = context;
        locationResult = result;
        startTracking();
        return true;
    }

    private void startTracking() {
       // Log.d(TAG, "startTracking");
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(con) == ConnectionResult.SUCCESS) {
            googleApiClient = new GoogleApiClient.Builder(con)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
//        else
//            Toast.makeText(con, "Unable to connect to google play services", Toast.LENGTH_LONG).show();
    }

	public static abstract class LocationResult {
		public abstract void gotLocation(Location location);
	}
}
