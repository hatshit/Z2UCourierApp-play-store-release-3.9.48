package com.zoom2u.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.app.IntentService;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.datamodels.PreferenceImgUpload;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.signaturefail.DBHelperForSignatureFailure;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.SlideMenuZoom2u;

public class BG_ImageUploadToServer extends IntentService{

	SharePreference_FailedImg sharedPreference_FailedImg;
	File cwFile;
	boolean isSelfieImage=false;
	public BG_ImageUploadToServer() {
		super("Upload Image");
	}

	public static Bitmap getPkgAndSignImgToAspectRatio (Bitmap imgBitmap, float maxWidth, float maxHeight){
		Bitmap originalBitmap = imgBitmap;
		if(originalBitmap != null){
			int bitmapImageHeight = originalBitmap.getHeight();
			int bitmapImageWidth = originalBitmap.getWidth();

			if(bitmapImageWidth >= maxWidth || bitmapImageHeight >= maxHeight)
				originalBitmap = Functional_Utility.scaleToActualAspectRatio(originalBitmap, maxWidth, maxHeight);
		}
		return originalBitmap;
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			if (intent.hasExtra("selfie"))
			{
				isSelfieImage=true;
				ActiveBookingView.photo = getPkgAndSignImgToAspectRatio(ActiveBookingView.photo, 800.0f, 800.0f);
			//	callToUploadSelfieImage(intent);
			}else {
				ActiveBookingView.photo = getPkgAndSignImgToAspectRatio(ActiveBookingView.photo, 800.0f, 800.0f);
				//********** Make directory for DHL capture image reason to take long time **********
				if (LoginZoomToU.filterDayActiveListStr.equals("DeliveryRuns"))
					Functional_Utility.mCurrentPhotoPath = LoginZoomToU.checkInternetwithfunctionality.makeDirToSaveOverlayImage(BG_ImageUploadToServer.this, ActiveBookingView.photo);
				callToUploadSignImgToServer(intent);  //************* Call http request to upload signature image
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/*Upload selfie image to server*/
	void callToUploadSelfieImage(Intent intent){
		WebserviceHandler webServiceHandler = new WebserviceHandler();
		int uploadImgCount=0;
		Bitmap selfieBitmap=ActiveBookingView.photo;
		if (ActiveBookingView.photo!=null)
			ActiveBookingView.photo=null;

	   String	bookingIdStrForUploadImg = String.valueOf(intent.getIntExtra("bookingIdStrForUploadImg",0));
		//uploadImgCount = webServiceHandler.uploadSelfieImage(selfieBitmap, bookingIdStrForUploadImg+"_CS");
		uploadImgCount = webServiceHandler.uploadPickUpandDropOffPackageImage(selfieBitmap,bookingIdStrForUploadImg+"_CS",null,null);
		Intent sendintent = new Intent("selfieUpload");
		sendintent.putExtra("imgcount", uploadImgCount);
		LocalBroadcastManager.getInstance(this).sendBroadcast(sendintent);

	}
	
	//************* Call http request to upload signature image
	void callToUploadSignImgToServer(Intent intent){
		WebserviceHandler webServiceHandler = new WebserviceHandler();
		int uploadImgCount = 0;
		String addBookingIdStr = "";
		String pkgImgUpload = "";
		String signImgUpload = "";

		Bitmap packageImageBitmap = ActiveBookingView.photo;
		Bitmap signatureImgBitmap = ConfirmPickUpForUserName.mBitmap;

		if(ActiveBookingView.photo != null)
			ActiveBookingView.photo = null;
		if(ConfirmPickUpForUserName.mBitmap != null)
			ConfirmPickUpForUserName.mBitmap = null;

		int isSignatureImageFromDB = 0;
		if (ConfirmPickUpForUserName.isSignatureImageFromDB)
			isSignatureImageFromDB = 1;

		if (intent.getBooleanExtra("isDropOffFromATL", false)) {
			addBookingIdStr = String.valueOf(intent.getStringExtra("bookingIdStrForUploadImg"));
			pkgImgUpload = addBookingIdStr+"_DP";
			uploadImgCount = webServiceHandler.uploadPickUpandDropOffPackageImage(packageImageBitmap, pkgImgUpload,
					null, signImgUpload);
		}else if(intent.getBooleanExtra("isActionBarPickup", false) == true){
			addBookingIdStr = intent.getStringExtra("bookingIdStrForUploadImg");
			pkgImgUpload = addBookingIdStr+"_PP";
			signImgUpload = addBookingIdStr+"_PS";
			uploadImgCount = webServiceHandler.uploadPickUpandDropOffPackageImage(packageImageBitmap, pkgImgUpload,
					signatureImgBitmap, signImgUpload);
		}else{
			addBookingIdStr = String.valueOf(intent.getStringExtra("bookingIdStrForUploadImg"));
			if(intent.getBooleanExtra("isDropoffBooking", false) == true) {
				pkgImgUpload = addBookingIdStr+"_DP";
				signImgUpload = addBookingIdStr+"_DS";
				uploadImgCount = webServiceHandler.uploadPickUpandDropOffPackageImage(packageImageBitmap, pkgImgUpload,
						signatureImgBitmap, signImgUpload);
			}else{
				pkgImgUpload = addBookingIdStr+"_PP";
				signImgUpload = addBookingIdStr+"_PS";
				uploadImgCount = webServiceHandler.uploadPickUpandDropOffPackageImage(packageImageBitmap, pkgImgUpload,
						signatureImgBitmap, signImgUpload);
			}
		}
		Log.e("", "--------   Upload image count  =====    "+uploadImgCount);
		saveFailedUploadImgToPref(uploadImgCount, packageImageBitmap, signatureImgBitmap, pkgImgUpload, signImgUpload, isSignatureImageFromDB);
		packageImageBitmap = null;
		signatureImgBitmap = null;
		webServiceHandler = null;
		try{
			LoginZoomToU.checkInternetwithfunctionality.deleteLatest();
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//************  Saved failed uploaded image to preference
	void saveFailedUploadImgToPref(int uploadImgCount, Bitmap pkgImg, Bitmap signImg, String pkgImgUpload, String signImgUpload, int isSignatureImageFromDB){
		if(uploadImgCount <= 0){
			String uploadedPackageImg = "";
			String uploadedSignatureImg = "";
			if(pkgImg != null)
				uploadedPackageImg = makeDirectoryToSaveFailedImg(pkgImgUpload, pkgImg);
//			if(signImg != null)
//				uploadedSignatureImg = makeDirectoryToSaveFailedImg(signImgUpload, signImg);

			// Add items to Upload image SharedPreferences.
			sharedPreference_FailedImg = new SharePreference_FailedImg();
			PreferenceImgUpload modelPrefUploadImg = new PreferenceImgUpload();
			modelPrefUploadImg.setBookingIdPP_DP(pkgImgUpload);
			modelPrefUploadImg.setBookingIdPS_DS(signImgUpload);
			modelPrefUploadImg.setPickDropPkgImgPath(uploadedPackageImg);
			modelPrefUploadImg.setSignatureImgPath(uploadedSignatureImg);
			modelPrefUploadImg.setIsSignatureImageFromDB(isSignatureImageFromDB);
			sharedPreference_FailedImg.addToUploadImgPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg, modelPrefUploadImg);
			modelPrefUploadImg = null;
			sharedPreference_FailedImg = null;
			Intent callResendService = new Intent(SlideMenuZoom2u.contxtForSharedPrefUploadImg, Service_ResendFailedImgToServer.class);
			startService(callResendService);
			callResendService = null;
		} else {
			if (isSignatureImageFromDB == 1) {
				DBHelperForSignatureFailure dbHelperForSignatureFailure = new DBHelperForSignatureFailure(this);
				dbHelperForSignatureFailure.open();
				dbHelperForSignatureFailure.deletePerticularItem(signImgUpload);
				dbHelperForSignatureFailure.close();
			}
		}
	}

	// ************ Make directory to saved failed images
	String makeDirectoryToSaveFailedImg(String bookingId, Bitmap bitmapImage){
		String filePath = "";
		File mypath = null;

		String uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();		// Set image name with uniqueID
		String current = uniqueId + ".png";

		boolean isMediaMounted = prepareDirectory();				// create directory

		if (isMediaMounted && isExternalStorageWritable()){
			mypath= new File(LoginZoomToU.savedImgDirOfFialedImg, current);
			filePath = mypath.toString();
		} else {
			ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
			mypath = wrapper.getDir("Z2U_FailedSignatureImg", MODE_PRIVATE);
			mypath = new File(mypath, current);
			filePath = mypath.toString();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(mypath);
	        bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePath;
	}

	public static String getTodaysDate(){
		final Calendar c = Calendar.getInstance();
		int todaysDate = (c.get(Calendar.YEAR) * 10000) +
				((c.get(Calendar.MONTH) + 1) * 100) +
				(c.get(Calendar.DAY_OF_MONTH));
		return(String.valueOf(todaysDate));
	}

	public static String getCurrentTime() {
		final Calendar c = Calendar.getInstance();
		int currentTime = (c.get(Calendar.HOUR_OF_DAY) * 10000)+
				(c.get(Calendar.MINUTE) * 100) +
				(c.get(Calendar.SECOND));
		return(String.valueOf(currentTime));
	}

	/* Checks if external storage is available for read and write */
	public static boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

    public static boolean prepareDirectory(){
        try{
            if (makedirs())
                return true;
            else
                return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }
 
    private static boolean makedirs(){
        File tempdir = new File(LoginZoomToU.savedImgDirOfFialedImg);
        if (!tempdir.exists())
            tempdir.mkdirs();
        return (tempdir.isDirectory());
    }
	
}
