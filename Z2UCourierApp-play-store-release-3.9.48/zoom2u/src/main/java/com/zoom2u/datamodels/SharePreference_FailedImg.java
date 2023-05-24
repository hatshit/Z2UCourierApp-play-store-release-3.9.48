package com.zoom2u.datamodels;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.zoom2u.LoginZoomToU;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class SharePreference_FailedImg {

	public static final String PREFS_NAME_FAILIMG = "Preference_OF_FailedImg";
    public static final String FAILEDIMG_PREF = "FailedImg_Pref";

    public static final String PREFS_NAME_LOCATION = "Preference_OF_RecentLocation";
    public static final String LOCATION_PREF = "Location_Pref";

    public static final String PREFS_NAME_SELFIE = "Preference_For_Selfie";
    public static final String SELFIE_PREF = "Selfie_Pref";

    public SharePreference_FailedImg() {
        super();
    }
 
    // This four methods are used for maintaining and store package and signature images and booking 
    // id to local preference if its failed to upload to server.

    /* Saved failed package or signature image to prefrence */
    public void saveFailedImg(Context context, List<PreferenceImgUpload> preferenceImgUpload){
        try{
			SharedPreferences settings;
			Editor editor;
 
			settings = context.getSharedPreferences(PREFS_NAME_FAILIMG, Context.MODE_PRIVATE);
			editor = settings.edit();
 
			OutputStream out = new ByteArrayOutputStream();
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(out, preferenceImgUpload);
			final byte[] data = ((ByteArrayOutputStream) out).toByteArray();
			String jsonPreferenceImgUpload = new String(data);
			
			editor.putString(FAILEDIMG_PREF, jsonPreferenceImgUpload);
			editor.commit();
			out = null;
			mapper = null;
		}catch (Exception e) {
			e.printStackTrace();
		}
    }

    /* Add failed package or signature image to preference */
    public void addToUploadImgPref(Context context, PreferenceImgUpload preferenceImgUpload){
        List<PreferenceImgUpload> arrayOfPrefImgUpload = getImgToUploadToServerFromPref(context);
        if (arrayOfPrefImgUpload == null)
        	arrayOfPrefImgUpload = new ArrayList<PreferenceImgUpload>();
        arrayOfPrefImgUpload.add(preferenceImgUpload);
        saveFailedImg(context, arrayOfPrefImgUpload);
    }
 
    public void removeFromUploadImgPref(Context context, PreferenceImgUpload preferenceImgUpload) {
        try {
			deleteImgFromStorage(context, preferenceImgUpload);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
 
    //********************* Delete image from storage after successfully resend it to server
    void deleteImgFromStorage(Context context, PreferenceImgUpload preferenceImgUpload){
        try {
          File tempdir = new File(LoginZoomToU.savedImgDirOfFialedImg);
	      if (tempdir.isDirectory()){
	    	  deletePkgOrSignImg(preferenceImgUpload.getPickDropPkgImgPath());   // if package image exist then deleted
	    	  deletePkgOrSignImg(preferenceImgUpload.getSignatureImgPath());	 // if signature image exist then deleted
	      }
	      ArrayList<PreferenceImgUpload> arrayOfPrefImgUpload = getImgToUploadToServerFromPref(context);
	      System.out.println("++++++++  Before   ===     "+arrayOfPrefImgUpload.size());
	      if (arrayOfPrefImgUpload != null){
	    	  if(arrayOfPrefImgUpload.size() > 0){
	        	arrayOfPrefImgUpload.remove(0);
	        	saveFailedImg(context, arrayOfPrefImgUpload);
	    	  }
	      }
	      System.out.println("++++++++  After   ===     "+arrayOfPrefImgUpload.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void deletePkgOrSignImg(String fileName){
    	try {
			File pkgImg = new File(fileName);
			if(pkgImg.isFile())
			  pkgImg.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /* Get failed uploaded image from shared prefrence */
    public ArrayList<PreferenceImgUpload> getImgToUploadToServerFromPref(Context context) {
        try {
			SharedPreferences settings;
			List<PreferenceImgUpload> arrayOfPrefImgUpload;
 
			settings = context.getSharedPreferences(PREFS_NAME_FAILIMG, Context.MODE_PRIVATE);
 
			if (settings.contains(FAILEDIMG_PREF)){
			    String jsonPrefStr = settings.getString(FAILEDIMG_PREF, null);
			    ObjectMapper mapper = new ObjectMapper();
			    PreferenceImgUpload[] prefImgUploadItems =  mapper.readValue(jsonPrefStr, PreferenceImgUpload[].class);
			    arrayOfPrefImgUpload = Arrays.asList(prefImgUploadItems);
			    arrayOfPrefImgUpload = new ArrayList<PreferenceImgUpload>(arrayOfPrefImgUpload);
			    mapper = null;
			    prefImgUploadItems = null;
			} else
			    return null;
 
			return (ArrayList<PreferenceImgUpload>) arrayOfPrefImgUpload;
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
    }

    /* Get recent location from shared prefrence */
    public String getLocationFromPref(Context context){
        String recentLocationStr = "";
        SharedPreferences locationPref;
        locationPref = context.getSharedPreferences(PREFS_NAME_LOCATION, Context.MODE_PRIVATE);

        if (locationPref.contains(LOCATION_PREF)) {
            recentLocationStr = locationPref.getString(LOCATION_PREF, null);
        }
        return recentLocationStr;
    }

    /* Save recent location from shared prefrence */
    public void saveRecentLocation(Context context, String recentLocation){
        try{
            SharedPreferences locationPref;
            Editor editor;

            locationPref = context.getSharedPreferences(PREFS_NAME_LOCATION, Context.MODE_PRIVATE);
            editor = locationPref.edit();
            String saveLocationWithTimeStamp = new String(recentLocation);
            editor.putString(LOCATION_PREF, saveLocationWithTimeStamp);
            editor.commit();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Reset Selfie preference to blank if hit back from Selfie camera */
    public void resetSelfiePref(Context context){
        SharedPreferences datePref;
        Editor editor;

        datePref = context.getSharedPreferences(PREFS_NAME_SELFIE, Context.MODE_PRIVATE);
        editor = datePref.edit();
        editor.putString(SELFIE_PREF, "");
        editor.commit();
    }

    /* Get recent date for DHL run to take selfie from shared prefrence */
    public String getRecentDateFromPref(Context context){
        String recentDateStr = "";
        SharedPreferences datePref;
        datePref = context.getSharedPreferences(PREFS_NAME_SELFIE, Context.MODE_PRIVATE);

        if (datePref.contains(SELFIE_PREF)) {
            recentDateStr = datePref.getString(SELFIE_PREF, "");
        }
        return recentDateStr;
    }

    /* Save recent Date to shared prefrence */
    public void saveRecentDateToPref(Context context){
        try{
            SharedPreferences datePref;
            Editor editor;

            datePref = context.getSharedPreferences(PREFS_NAME_SELFIE, Context.MODE_PRIVATE);
            editor = datePref.edit();

            String saveDateWithTimeStamp = new String(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
            editor.putString(SELFIE_PREF, saveDateWithTimeStamp);
            editor.commit();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkRecentDateToPref(String prefDateStr){

        Calendar now = Calendar.getInstance();

        Date prefDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            prefDate = sdf.parse(prefDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        Date currentDate = null;
        try {
            String currentDateStr = sdf.format(now.getTime());
            currentDate = sdf.parse(currentDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        if (currentDate.after(prefDate))
            return true;
        else
            return false;
    }

}
