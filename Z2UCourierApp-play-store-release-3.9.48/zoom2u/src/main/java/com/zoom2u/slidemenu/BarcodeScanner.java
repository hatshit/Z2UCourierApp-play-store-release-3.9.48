package com.zoom2u.slidemenu;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatDetailActivity;
import com.zoom2u.MainActivity;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.dialogactivity.DropOffDoneDialog;
import com.zoom2u.slidemenu.barcode_reader.BarcodeController;

import java.util.ArrayList;

public class BarcodeScanner extends Activity {

    BarcodeController barcodeController;
    public static int ScanAWBForPick = 1;

    public String runType;

    ImageView flashIconBarcodeScanner;
    boolean isFlashOn = false;

    public static boolean isScannedSuccessFully = false;
    public static final int TAKE_PHOTO_INTENT = 151;
    Window window;
    Button btnManualScan;
    RelativeLayout headerBarCodeScanner;
    TextView msgTitleHeaderTxt;
    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsOtherScreenOpen =false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsOtherScreenOpen =true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_scanner);
        PushReceiver.IsOtherScreenOpen =true;
        if (SlideMenuZoom2u.isDropOffCompleted) {
            DropOffDoneDialog.alertDialogToFinishView(this, "Drop off Completed");
            SlideMenuZoom2u.isDropOffCompleted = false;
        }
        headerBarCodeScanner = (RelativeLayout) findViewById(R.id.headerBarCodeScanner);
        btnManualScan = (Button) findViewById(R.id.btnManualScan);
        msgTitleHeaderTxt = (TextView) findViewById(R.id.msgTitleHeaderTxt);
        window = BarcodeScanner.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if(MainActivity.isIsBackGroundGray()){
            msgTitleHeaderTxt.setBackgroundResource(R.color.base_color_gray);
            btnManualScan.setBackgroundResource(R.drawable.new_base_round);
            headerBarCodeScanner.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        }else{
            msgTitleHeaderTxt.setBackgroundResource(R.color.base_color1);
            btnManualScan.setBackgroundResource(R.drawable.new_base_round_gray);
            headerBarCodeScanner.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(BarcodeScanner.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(BarcodeScanner.this, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(BarcodeScanner.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(BarcodeScanner.this);

                    enterFieldDialog.setCancelable(true);
                    enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    enterFieldDialog.setContentView(R.layout.permission_dailog);

                    Window window = enterFieldDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

                    enterFieldDialogHEader.setText("Permission required!");

                    TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

                    enterFieldDialogMsg.setText("Z2U for couriers app need to access your images for picture post.");

                    Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                    enterFieldDialogDoneBtn.setText("Got it!");

                    enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions(BarcodeScanner.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();

                }else
                    barcodeView();
            }else
                barcodeView();

            ImageView backFrom= (ImageView) findViewById(R.id.backFrom);
            backFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        barcodeController.releaseCamera();
                        barcodeController.backToBooking();
                    } catch (Exception e) {
                        e.printStackTrace();
                        setResult(Activity.RESULT_OK);
                        finish();
                        overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        barcodeView ();
        try {
            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("selfieUpload"));
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    //************ In it barcode view ************
    private void barcodeView (){
        isScannedSuccessFully = false;
        ScanAWBForPick = getIntent().getIntExtra("ScanAWBForPick", 1);

        try{
            FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
            ImageView cornerBorder = (ImageView) findViewById(R.id.cornerBorder);






            CheckBox toggleForReturnInBarCodeView =  (CheckBox) findViewById(R.id.toggleForReturnInBarCodeView);

            flashIconBarcodeScanner = (ImageView) findViewById(R.id.flashIconBarcodeScanner);
            flashIconBarcodeScanner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFlashIconOnOff();
                }
            });

            switch (ScanAWBForPick){
                case 1:
                    toggleForReturnInBarCodeView.setVisibility(View.VISIBLE);
                    msgTitleHeaderTxt.setText("Scan barcode \nYou can move the targeting box around if needed.");
                    barcodeController = new BarcodeController(BarcodeScanner.this, preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
                case 2:
                    msgTitleHeaderTxt.setText("Scan barcode \nYou can move the targeting box around if needed.");
                    barcodeControllerForTriedToDeliverOrDrop(preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
                case 3:
                    msgTitleHeaderTxt.setText("Scan barcode item for return to depot.\nYou can move the targeting box around if needed.");
                    barcodeControllerForTriedToDeliverOrDrop(preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
                case 4:
                    msgTitleHeaderTxt.setText("Scan barcode item for dropoff.\nYou can move the targeting box around if needed.");
                    barcodeControllerForTriedToDeliverOrDrop(preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
                case 5:
                case 7:
                    msgTitleHeaderTxt.setText("Scan barcode \nYou can move the targeting box around if needed.");
                    barcodeControllerForTriedToDeliverOrDrop(preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
                default:
                    msgTitleHeaderTxt.setText("Scan barcode \nYou can move the targeting box around if needed.");
                    barcodeController = new BarcodeController(BarcodeScanner.this, preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, toggleForReturnInBarCodeView);
                    break;
            }

            //************** Hide return button ************
            RelativeLayout bottomViewBarCodeScanner =  (RelativeLayout) findViewById(R.id.bottomViewBarCodeScanner);
            try {
                if (runType.equals("SMARTSORT")) {
                    bottomViewBarCodeScanner.setVisibility(View.GONE);
                    toggleForReturnInBarCodeView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            btnManualScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        barcodeController.manualScanDialog(true, "Manual scan AWB number");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setFlashIconOnOff() {
        if (barcodeController.mCamera != null) {
            if (isFlashOn) {
                isFlashOn = !isFlashOn;
                flashIconBarcodeScanner.setImageResource(R.drawable.flash_icon_off);
                Camera.Parameters p = barcodeController.mCamera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                barcodeController.mCamera.setParameters(p);
                barcodeController.mCamera.startPreview();
                p = null;
            } else {
                isFlashOn = !isFlashOn;
                flashIconBarcodeScanner.setImageResource(R.drawable.flash_icon_on);
                Camera.Parameters p = barcodeController.mCamera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                barcodeController.mCamera.setParameters(p);
                barcodeController.mCamera.startPreview();
                p = null;
            }
        }
    }

    void barcodeControllerForTriedToDeliverOrDrop(FrameLayout preview, TextView msgTitleHeaderTxt, ImageView cornerBorder, RelativeLayout headerBarCodeScanner, CheckBox toggleForReturnInBarCodeView){
        toggleForReturnInBarCodeView.setVisibility(View.GONE);
        String awbNumber = getIntent().getStringExtra("AWB_NUMBER");
        ArrayList<String> pieceArray = getIntent().getStringArrayListExtra("PIECE_ARRAY");
        barcodeController = new BarcodeController(BarcodeScanner.this, preview, msgTitleHeaderTxt, cornerBorder, headerBarCodeScanner, awbNumber, pieceArray);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //******** Back from Barcode scanner view ***********//
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (barcodeController != null)
                barcodeController.releaseCamera();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (barcodeController != null) {
            barcodeController.releaseCamera();
            barcodeController.backToBooking();
        }
    }


    //local brodcast recevier for action neededto performed
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            if (intent.hasExtra("showDialogMsg"))
            {
                barcodeController.showAgreeDisagreeDialog();
            }/*else
            if (intent.hasExtra("imgcount")) {
                int count = intent.getIntExtra("imgcount", 0);
                Log.d("receiver", "Got message: " + count);
                barcodeController.dismissDialog();
                startActivity(new Intent(BarcodeScanner.this,DialogRunPopup.class));
            }*/
        }
    };

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onDestroy() {
        try {
            // Unregister since the activity is about to be closed.
            if (mMessageReceiver!=null)
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        }catch (Exception ex){

        }
        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        barcodeController.inItBarcodeScanner((FrameLayout) findViewById(R.id.cameraPreview), (TextView) findViewById(R.id.msgTitleHeaderTxt),
                (RelativeLayout) findViewById(R.id.headerBarCodeScanner));
        try {
            PushReceiver.isCameraOpen = false;
       /*     if (requestCode==1&&resultCode==RESULT_OK)
            {
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;

                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;

                ActiveBookingView.photo = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                barcodeController.showDialogForImageUpload();
                startService(new Intent(this, BG_ImageUploadToServer.class)
                        .putExtra("bookingIdStrForUploadImg",barcodeController.getBookingID()).putExtra("selfie",true));

            }else*/
            if (requestCode == TAKE_PHOTO_INTENT && resultCode == RESULT_OK) {
                barcodeController.getImageFromCamera();
            }
//            else if (requestCode == BarcodeController.CAMERA_PIC_REQUEST1) {
//                if (resultCode == RESULT_OK) {
//                    try {
//                        barcodeController.callAsyncTaskForRequest();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }else {
//                    resetSelfiePref();
//                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
//                }
//            }
            else if (requestCode == BarcodeController.CAMERA_PIC_REQUEST_OVERLAY) {
                if (resultCode == RESULT_OK) {
                    try {
                        if (ActiveBookingView.photo != null)
                            barcodeController.sendBitmapToServerForDropOrReturn();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }else if (resultCode == RESULT_CANCELED) {
                resetSelfiePref();
                Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
            }
            barcodeController.resetBarcodeScanner();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //** Reset Selfie if selfie not taken
    private void resetSelfiePref(){
        SharePreference_FailedImg prefForSelfieDate = new SharePreference_FailedImg();
        prefForSelfieDate.resetSelfiePref(BarcodeScanner.this);
        prefForSelfieDate = null;
    }
}

