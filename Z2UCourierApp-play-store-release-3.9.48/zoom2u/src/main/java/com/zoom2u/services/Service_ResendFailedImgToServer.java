package com.zoom2u.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.sendmail_in_bg.imagefailure.GMailSender;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.datamodels.PreferenceImgUpload;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.signaturefail.DBHelperForSignatureFailure;
import com.zoom2u.utility.signaturefail.SignatureImg_DB_Model;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.SlideMenuZoom2u;
import android.app.IntentService;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

public class Service_ResendFailedImgToServer extends IntentService{

	SharePreference_FailedImg sharedPreference_FailedImg;

    public static TimerTask failedImgTimerTask;

	public Service_ResendFailedImgToServer() {
		super("Resend Image");
	}

	@Override
	protected void onHandleIntent(Intent intent){
		getUploadImgPrefItemForResendToServer();
	}
	
	//*************** Get failed image from preference and send again to server
	void getUploadImgPrefItemForResendToServer(){
		WebserviceHandler webServiceHandler = new WebserviceHandler();
		sharedPreference_FailedImg = new SharePreference_FailedImg();
		List<PreferenceImgUpload> arrayOfPrefImgUpload = sharedPreference_FailedImg.getImgToUploadToServerFromPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg);
        if(arrayOfPrefImgUpload != null){
			if(arrayOfPrefImgUpload.size() > 0){
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                    try {
                        Bitmap bitmapPkgImg = null;
                        try {
                            if (!arrayOfPrefImgUpload.get(0).getPickDropPkgImgPath().equals("")) {
                                File filePkgImg = new File(arrayOfPrefImgUpload.get(0).getPickDropPkgImgPath());
                                bitmapPkgImg = BitmapFactory.decodeStream(new FileInputStream(filePkgImg));
                                filePkgImg = null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Bitmap bitmapSignatureImg = null;
                        try {
                            //************ Get failer image from DB ***********
                            String bookingIdOfSignatureImg = arrayOfPrefImgUpload.get(0).getBookingIdPS_DS();

                            DBHelperForSignatureFailure dbHelperForSignatureFailure = new DBHelperForSignatureFailure(this);
                            dbHelperForSignatureFailure.open();
                            SignatureImg_DB_Model signatureImg_db_model = dbHelperForSignatureFailure.retriveSignatureImgDetails(bookingIdOfSignatureImg);
                            bitmapSignatureImg = signatureImg_db_model.getSignatureImgBitmap();
                            signatureImg_db_model = null;
                            dbHelperForSignatureFailure.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            sendEmail(arrayOfPrefImgUpload.get(0).getBookingIdPS_DS()+" - "+getException(e));
                        }

                        int uploadImgCountResend = webServiceHandler.uploadPickUpandDropOffPackageImage(bitmapPkgImg, arrayOfPrefImgUpload.get(0).getBookingIdPP_DP(),
                                bitmapSignatureImg, arrayOfPrefImgUpload.get(0).getBookingIdPS_DS());
                        if (uploadImgCountResend > 0) {
                            removeSignatureImgFromBD(arrayOfPrefImgUpload.get(0).getIsSignatureImageFromDB(), arrayOfPrefImgUpload.get(0).getBookingIdPS_DS());
                            sharedPreference_FailedImg.removeFromUploadImgPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg, arrayOfPrefImgUpload.get(0));
                        }
                        webServiceHandler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        sharedPreference_FailedImg.addToUploadImgPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg, arrayOfPrefImgUpload.get(0));
                        sharedPreference_FailedImg.removeFromUploadImgPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg, arrayOfPrefImgUpload.get(0));
                        sendEmail(arrayOfPrefImgUpload.get(0).getBookingIdPS_DS()+" - "+getException(e));
                    }
                }
                checkImgExistToUploadInPref();
                arrayOfPrefImgUpload = null;
			}
		}
	}

	public static String getException (Exception e) {
        StringWriter errors = new StringWriter();
        e.printStackTrace(new PrintWriter(errors));
        return errors.toString();
    }

    //****************** Remove image from DB *******************
    private void removeSignatureImgFromBD(int isSignatureImageFromDB, String signImgUpload){
        if (isSignatureImageFromDB == 1) {
            DBHelperForSignatureFailure dbHelperForSignatureFailure = new DBHelperForSignatureFailure(this);
            dbHelperForSignatureFailure.open();
            dbHelperForSignatureFailure.deletePerticularItem(signImgUpload);
            dbHelperForSignatureFailure.close();
        }
    }

	/*********** Again check for existing image in internal storage and if exist
	      then sent it back to server after 1 minute *************/
	void checkImgExistToUploadInPref(){
        try {
            List<PreferenceImgUpload> arrayOfPrefImgUpload = sharedPreference_FailedImg.getImgToUploadToServerFromPref(SlideMenuZoom2u.contxtForSharedPrefUploadImg);
            Log.e("  Preference Array ", " After image upload ==========     "+arrayOfPrefImgUpload.size());
            sharedPreference_FailedImg = null;
            if(arrayOfPrefImgUpload != null){
                if(arrayOfPrefImgUpload.size() > 0){
                    arrayOfPrefImgUpload = null;

                    Timer timerForFailedImg = new Timer();
                    if (failedImgTimerTask != null) {
                        failedImgTimerTask.cancel();
                        failedImgTimerTask = null;
                    }

                    failedImgTimerTask = new TimerTask() {
                        @Override
                        public void run() {
                            getUploadImgPrefItemForResendToServer();
                        }
                    };

                    timerForFailedImg.schedule(failedImgTimerTask, 60000);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendEmail(final String errorMsg) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    GMailSender sender = new GMailSender("arun.dey@aplitetech.com", "123Arundey");
                    sender.sendMail("Zoom2u image failure", errorMsg, "arun.dey@aplitetech.com", "arun.dey@aplitetech.com,milind.kale@aplitetech.com");
                    Log.v("MainActivity", "Your mail has been sent…");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
