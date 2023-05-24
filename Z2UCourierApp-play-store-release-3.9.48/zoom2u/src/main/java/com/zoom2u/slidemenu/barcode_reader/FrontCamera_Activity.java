package com.zoom2u.slidemenu.barcode_reader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;

import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.z2u.chatview.ChatDetailActivity;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

/**
 * Created by arun on 30/1/17.
 */

public class FrontCamera_Activity extends Activity implements View.OnClickListener{
    private Camera mCamera;
    private Front_CameraPreview mPreview;
    ImageView captureButton;
    static Context con;
    Bitmap mainbitmap;
    private int bookingID;

    RelativeLayout subViewFrontCamera;
    ProgressDialog progressForSelfieUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bookingID = getIntent().getIntExtra("BookingID", 0);
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(FrontCamera_Activity.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(FrontCamera_Activity.this, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(FrontCamera_Activity.this, permission[2]) == PackageManager.PERMISSION_DENIED) {
                    Dialog enterFieldDialog  = new Dialog(FrontCamera_Activity.this);

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
                            ActivityCompat.requestPermissions(FrontCamera_Activity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();


                }else
                    inItCameraPreview();
            }else
                inItCameraPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //****** Initialize camera view ************
    private void inItCameraPreview() {
        setContentView(R.layout.activity_front_camera);

        try {
            subViewFrontCamera = (RelativeLayout) findViewById(R.id.subViewFrontCamera);
            subViewFrontCamera.setVisibility(View.GONE);

            captureButton = (ImageView) findViewById(R.id.button_capture);
            FrameLayout preview = (FrameLayout) findViewById(R.id.front_camera_preview);

            // Create an instance of Camera
            con = getApplicationContext();
            try {
                mCamera = getCameraInstance();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // Create our Preview view and set it as the content of our activity.
            mPreview = new Front_CameraPreview(FrontCamera_Activity.this, mCamera);
            preview.addView(mPreview);
            captureButton.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Bitmap bitmap;
    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            System.gc();
            bitmap = null;
           /* BitmapWorkerTask task = new BitmapWorkerTask(data);
            task.execute(0);*/
            BitmapWorkerTask(data);
        }
    };

    @Override
    public void onClick(View v) {
        // get an image from the camera
        captureButton.setOnClickListener(null);
        mCamera.takePicture(null, null, mPicture);
    }

    private void BitmapWorkerTask(byte[] imgdata){
        final int[] data = {0};

        final WeakReference<byte[]> dataf;
        dataf = new WeakReference<byte[]>(imgdata);

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                // before execution
            }

            @Override
            public void doInBackground() {
                data[0] = imgdata[0];
                ResultActivity(dataf.get());
            }

            @Override
            public void onPostExecute() {
                if (mainbitmap != null) {
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        GetCourierProfileAsyncTask();
                        //new GetCourierProfileAsyncTask().execute();
                    else
                        DialogActivity.alertDialogView(FrontCamera_Activity.this, "No Network!", "No Network connection, Please try again later.");
                }
            }
        }.execute();

    }



    private void GetCourierProfileAsyncTask(){
        final String[] uploadImageResponseStr = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressForSelfieUpload == null)
                    progressForSelfieUpload = new ProgressDialog(FrontCamera_Activity.this);
                Custom_ProgressDialogBar.inItProgressBar(progressForSelfieUpload);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (mainbitmap != null)
                        uploadImageResponseStr[0] = webServiceHandler.uploadProfileImage(mainbitmap, bookingID + "_CS.png", false);
                    else
                        uploadImageResponseStr[0] = webServiceHandler.saveVehicleAgreementForDhl(bookingID);
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                if(progressForSelfieUpload != null)
                    if(progressForSelfieUpload.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressForSelfieUpload);

                if (mainbitmap != null) {
                    try {
                        JSONObject jObjOfProfileImg = new JSONObject(uploadImageResponseStr[0]);
                        if (jObjOfProfileImg.getBoolean("success")) {
                            mainbitmap = null;
                            vehicleAgreeDialog();
                        } else {
                            releaseCamera();
                            alertForResetCamera("Error!", "Pictuer not uploaded, Please try again.");
                        }
                    } catch(JSONException e){
                        e.printStackTrace();
                        releaseCamera();
                        alertForResetCamera("Error!", "Something went wrong, Please try again.");
                    }
                    mainbitmap = null;
                } else {
                    try {
                        JSONObject jObjOfProfileImg = new JSONObject(uploadImageResponseStr[0]);
                        if (jObjOfProfileImg.getBoolean("success")) {
                            new SharePreference_FailedImg().saveRecentDateToPref(FrontCamera_Activity.this);
                            subViewFrontCamera.setVisibility(View.GONE);
                            dissmissVehicleAgreementDialog();
                            Intent i = new Intent();
                            setResult(-1, i);
                            finish();
                        } else
                            alertForResetCamera("Error!", "Please try again.");
                    } catch(JSONException e){
                        e.printStackTrace();
                        alertForResetCamera("Error!", "Something went wrong, Please try again.");
                    }
                }
                uploadImageResponseStr[0] = "";

            }
        }.execute();

    }



    Dialog errorDialogResetCamera;
    //*********** Alert for Reset camera ************
    private void alertForResetCamera(String titleStr, String msgstr){
            try{
                if(errorDialogResetCamera != null)
                    if(errorDialogResetCamera.isShowing())
                        errorDialogResetCamera.dismiss();
            }catch(Exception e){
                e.printStackTrace();
            }

            try {
                if(errorDialogResetCamera != null)
                    errorDialogResetCamera = null;
                errorDialogResetCamera = new Dialog(FrontCamera_Activity.this);
                errorDialogResetCamera.setCancelable(false);
                errorDialogResetCamera.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                errorDialogResetCamera.setContentView(R.layout.dialogview);

                Window window = errorDialogResetCamera.getWindow();
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                TextView errorDialogResetCameraHEader = (TextView) errorDialogResetCamera.findViewById(R.id.titleDialog);
                errorDialogResetCameraHEader.setTypeface(LoginZoomToU.NOVA_BOLD);
                errorDialogResetCameraHEader.setText(titleStr);

                TextView errorDialogResetCameraMsg = (TextView) errorDialogResetCamera.findViewById(R.id.dialogMessageText);
                errorDialogResetCameraMsg.setTypeface(LoginZoomToU.NOVA_REGULAR);
                errorDialogResetCameraMsg.setText(msgstr);

                Button errorDialogResetCameraDoneBtn = (Button) errorDialogResetCamera.findViewById(R.id.dialogDoneBtn);
                errorDialogResetCameraDoneBtn.setTypeface(LoginZoomToU.NOVA_BOLD);
                errorDialogResetCameraDoneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        errorDialogResetCamera.dismiss();
                        inItCameraPreview();
                    }
                });
                errorDialogResetCamera.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    Dialog dialogVehicleAgree;

    //*********** Dismiss Vehicle agreement dialog ************
    private void dissmissVehicleAgreementDialog() {
        try{
            if(dialogVehicleAgree != null)
                if(dialogVehicleAgree.isShowing())
                    dialogVehicleAgree.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //*********** Alert for Vehicle agreement ************
    private void vehicleAgreeDialog(){
        dissmissVehicleAgreementDialog();
        subViewFrontCamera.setVisibility(View.VISIBLE);
        try {
            if(dialogVehicleAgree != null)
                dialogVehicleAgree = null;
            dialogVehicleAgree = new Dialog(FrontCamera_Activity.this);
            dialogVehicleAgree.setCancelable(false);
            dialogVehicleAgree.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialogVehicleAgree.setContentView(R.layout.vehicle_agree_for_dhl);

            Window window = dialogVehicleAgree.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView dialogVehicleAgreeMsg = (TextView) dialogVehicleAgree.findViewById(R.id.msgTxtVehicleAgree);
            dialogVehicleAgreeMsg.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

            TextView dialogVehicleAgreeDoneBtn = (TextView) dialogVehicleAgree.findViewById(R.id.agreeBtnVehicleAgree);
            dialogVehicleAgreeDoneBtn.setTypeface(LoginZoomToU.NOVA_BOLD);
            dialogVehicleAgreeDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        GetCourierProfileAsyncTask();
                        //new GetCourierProfileAsyncTask().execute();
                    else
                        DialogActivity.alertDialogView(FrontCamera_Activity.this, "No Network!", "No Network connection, Please try again later.");
                }
            });

            TextView disagreeBtnVehicleAgree = (TextView) dialogVehicleAgree.findViewById(R.id.disagreeBtnVehicleAgree);
            disagreeBtnVehicleAgree.setTypeface(LoginZoomToU.NOVA_BOLD);
            disagreeBtnVehicleAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogActivity.alertDialogView(FrontCamera_Activity.this, "Alert!", "Unfortunately if you are unable to secure your vehicle you will not be able to do further deliveries. Please contact "+R.string.zoom2u_support_no);
                }
            });

            dialogVehicleAgree.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ResultActivity(byte[] data) {
        mainbitmap = null;
        mainbitmap = decodeSampledBitmapFromResource(data, 200, 200);
        mainbitmap=RotateBitmap(mainbitmap,270);
        mainbitmap=flip(mainbitmap);
    }

    public static Bitmap decodeSampledBitmapFromResource(byte[] data, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // BitmapFactory.decodeResource(res, resId, options);
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance() {
        Camera c = null;
        Log.d("No of cameras", Camera.getNumberOfCameras() + "");
        for (int camNo = 0; camNo < Camera.getNumberOfCameras(); camNo++) {
            Camera.CameraInfo camInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(camNo, camInfo);

            if (camInfo.facing == (Camera.CameraInfo.CAMERA_FACING_FRONT)) {
                c = Camera.open(camNo);
                c.setDisplayOrientation(90);
            }
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera(); // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null)
            inItCameraPreview();
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release(); // release the camera for other applications
            mCamera = null;
        }
    }

    // rotate the bitmap to portrait
    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    //the front camera displays the mirror image, we should flip it to its original
    Bitmap flip(Bitmap d)
    {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d;
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return dst;
    }


}
