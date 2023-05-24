package com.zoom2u.slidemenu.barcode_reader;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatDetailActivity;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.slidemenu.barcode_reader.camera_utils.CameraConstants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Arun on 27/3/17.
 */

public class CameraOverlay_Activity  extends Activity implements SurfaceHolder.Callback {

    SurfaceView cameraView, transparentView;
    SurfaceHolder holder, holderTransparent;

    Camera camera;
    Camera.Parameters param;

    int  imageBitmapWdthHegt;
    boolean previewing = false;

    Button btnCameraCapture;
    ImageView flashIcon;
    RelativeLayout titleBarLayoutOverlay, msgLayoutOverlay;

    boolean isFlashOn = false;

    @Override
    protected void onResume() {
        super.onResume();
        inItCameraPreview();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overlay_over_camera);

        BarcodeScanner.ScanAWBForPick = 6;

        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(CameraOverlay_Activity.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(CameraOverlay_Activity.this, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(CameraOverlay_Activity.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(CameraOverlay_Activity.this);

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
                            ActivityCompat.requestPermissions(CameraOverlay_Activity.this,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();

                }else
                    inItCamearOverlayActivity();
            }else
                inItCamearOverlayActivity();

            findViewById(R.id.backBtnOverlay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    releaseCamera();
                    BarcodeScanner.ScanAWBForPick = 1;
                    finish();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItCamearOverlayActivity() {
        try {
            boolean isFlashAvailable = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

            cameraView = (SurfaceView)findViewById(R.id.CameraView);
            holder = cameraView.getHolder();

            holder.addCallback((SurfaceHolder.Callback) this);

            ((TextView) findViewById(R.id.titleTxtOverlayView)).setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
            ((TextView) findViewById(R.id.atlTxtOverlayView)).setTypeface(LoginZoomToU.NOVA_REGULAR);
            ((TextView) findViewById(R.id.msgTxtOverlayView)).setTypeface(LoginZoomToU.NOVA_REGULAR);

            transparentView = (SurfaceView)findViewById(R.id.transparentView);
            holderTransparent = transparentView.getHolder();

            int getScreenCount = getIntent().getIntExtra("ScreenCount", 1);
            if (getScreenCount == 1){
                imageBitmapWdthHegt = getintToPX(150);
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.msgTxtOverlayView)).setText("Please take a photo of the parcel before collecting the customer's signature.\n\nInclude the whole parcel,\npreferably with the label readable. ");
            } else if (getScreenCount == 2) {
                imageBitmapWdthHegt = getintToPX(150);
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setText("Customer Signature Mandatory");
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                ((TextView) findViewById(R.id.msgTxtOverlayView)).setText("This booking must be signed off by the recipient. Under no circumstances can it be left alone.");
            } else {
                imageBitmapWdthHegt = getintToPX(270);
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setText("Safe Parcel Delivery Available.");
                ((TextView) findViewById(R.id.atlTxtOverlayView)).setTextColor(getResources().getColor(R.color.colorPrimary));
                ((TextView) findViewById(R.id.msgTxtOverlayView)).setText("The customer has allowed you to leave the parcel, but only if it is in a safe location.\nPlease include surrounding features in your photo.\neg. Door, walls, plants, etc.");
            }
            transparentView.getHolder().setFixedSize(imageBitmapWdthHegt, imageBitmapWdthHegt);

            holderTransparent.addCallback((SurfaceHolder.Callback) this);
            holderTransparent.setFormat(PixelFormat.TRANSLUCENT);
            transparentView.setZOrderMediaOverlay(true);

            btnCameraCapture= (Button) findViewById(R.id.btnCameraCapture);
            btnCameraCapture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btnCameraCapture.setEnabled(false);
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                            btnCameraCapture.setOnClickListener(null);
                            camera.takePicture(null, null, cameraPictureCallbackJpeg);
                        }
                    });
                }
            });

            transparentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    camera.autoFocus(new Camera.AutoFocusCallback() {
                        @Override
                        public void onAutoFocus(boolean b, Camera camera) {
                        }
                    });
                    return false;
                }
            });

            titleBarLayoutOverlay = (RelativeLayout) findViewById(R.id.titleBarLayoutOverlay);
            msgLayoutOverlay = (RelativeLayout) findViewById(R.id.msgLayoutOverlay);

            flashIcon = (ImageView) findViewById(R.id.flashIcon);
            if (isFlashAvailable)
                flashIcon.setVisibility(View.VISIBLE);
            else
                flashIcon.setVisibility(View.GONE);

            flashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFlashIconOnOff();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFlashIconOnOff() {
        if (camera != null) {
            if (isFlashOn) {
                isFlashOn = !isFlashOn;
                flashIcon.setImageResource(R.drawable.flash_icon_off);
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.startPreview();
                p = null;
            } else {
                isFlashOn = !isFlashOn;
                flashIcon.setImageResource(R.drawable.flash_icon_on);
                Camera.Parameters p = camera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                camera.setParameters(p);
                camera.startPreview();
                p = null;
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        inItCameraPreview();
    }

    //******** Initialize overlay camera preview *********
    private void inItCameraPreview() {
        try {
            synchronized (holder) {
                //Draw();
            }
            camera = Camera.open();     //open a camera
        } catch (Exception e) {
            Log.i("Exception", e.toString());
            return;
        }

        try {
            camera.setPreviewDisplay(holder);
        } catch (Exception e) {
            return;
        }

        param = camera.getParameters();
        param.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0)
            camera.setDisplayOrientation(90);

        List<Camera.Size> supportedSizes = param.getSupportedPictureSizes();
        Camera.Size size = supportedSizes.get(0);
        if (size != null)
            param.setPictureSize(size.width, size.height);

        camera.setParameters(param);
        camera.startPreview();
        param = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();    //call method for refress camera
    }

    public void refreshCamera() {
        if (holder.getSurface() == null)
            return;

        try {
            if(previewing){
                camera.stopPreview();
                previewing = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (camera != null){
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (camera != null) {
            isFlashOn = !isFlashOn;
            flashIcon.setImageResource(R.drawable.flash_icon_off);
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.startPreview();
            p = null;
            camera.release(); //********* For release a camera
            camera = null;
        }
    }

    //************ Release camera ***********
    private void releaseCamera(){
        if (camera != null) {
            camera.release(); //********* For release a camera
            camera = null;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
        previewing = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releaseCamera();
        BarcodeScanner.ScanAWBForPick = 1;
        finishView();
    }

    private void finishView(){
        Intent i = new Intent();
        setResult(-1, i);
        finish();
    }

    Camera.PictureCallback cameraPictureCallbackJpeg = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            byte[] bm = createPicture(data);
            ActiveBookingView.photo = BitmapFactory.decodeByteArray(bm, 0, bm.length);
            btnCameraCapture.setEnabled(true);

            stopPreviewAndFreeCamera();

            finishView();
        }
    };

    private byte[] createPicture(byte[] jdata) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(jdata, 0, jdata.length);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, CameraConstants.PHOTO_QUALITY, os);

        return os.toByteArray();
    }

    private int getintToPX(int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, i, getResources().getDisplayMetrics());
    }

    /**
     * When this function returns, mCamera will be null.
     */
    private void stopPreviewAndFreeCamera() {

        if (camera != null) {
            Camera.Parameters p = camera.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(p);
            camera.stopPreview();
            camera.release();

            camera = null;
        }
    }
}