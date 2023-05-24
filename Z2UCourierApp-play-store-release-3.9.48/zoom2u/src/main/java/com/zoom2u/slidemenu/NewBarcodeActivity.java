package com.zoom2u.slidemenu;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.z2u.chatview.ChatDetailActivity;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.barcode_reader.CameraPreview;
import com.zoom2u.webservice.WebserviceHandler;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONArray;
import org.json.JSONObject;

import static me.dm7.barcodescanner.core.CameraUtils.getCameraInstance;

public class NewBarcodeActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    ViewGroup.MarginLayoutParams scannerViewParam;
    ViewGroup.MarginLayoutParams headerBarCodeScannerViewHeightParam;
    ViewGroup.MarginLayoutParams msgTitleHeaderTxtHeight;
    public Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    public boolean barcodeScanned = false;
    public boolean previewing = true;
    String scanResult;
    private int _xDelta;
    private int _yDelta;
    public SubmitNoteForReamingDhl submitNoteForReamingDhl;
    float croppedViewX, croppedViewY;
    float croppedViewHeight, croppedViewWidth;
    RelativeLayout headerBarCodeScanner;
    TextView msgTitleHeaderTxt;
    public static int ScanAWBForPick = 1;
    ProgressDialog progressToGetPassword;
    public String runType;
    private EditText note;
    private Button submitBtn;
    private boolean cameraPermissionGranted=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_barcode);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        if (getIntent().hasExtra("SubmitNoteForReamingDhl")) {
            submitNoteForReamingDhl =  getIntent().getParcelableExtra("SubmitNoteForReamingDhl");
        }

        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(NewBarcodeActivity.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(NewBarcodeActivity.this, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(NewBarcodeActivity.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(NewBarcodeActivity.this);

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
                            ActivityCompat.requestPermissions(NewBarcodeActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();

                }else
                    barcodeView();
            }else
                barcodeView();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    FrameLayout preivew;  ImageView scannerView;
    TextView header;
    private void barcodeView() {
        msgTitleHeaderTxt = findViewById(R.id.msgTitleHeaderTxt);
        headerBarCodeScanner =findViewById(R.id.headerBarCodeScanner);
        preivew = findViewById(R.id.cameraPreview);
        scannerView = findViewById(R.id.cornerBorder);
        header = findViewById(R.id.header);
        note = findViewById(R.id.note);
        submitBtn = findViewById(R.id.next_btn);
        submitBtn.setOnClickListener(this);
        cameraPermissionGranted=true;
       // inItBarcodeScanner();

    }

    private void inItBarcodeScanner() {
        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        scannerViewParam = (ViewGroup.MarginLayoutParams) scannerView.getLayoutParams();
        headerBarCodeScannerViewHeightParam = (ViewGroup.MarginLayoutParams) headerBarCodeScanner.getLayoutParams();
        msgTitleHeaderTxtHeight = (ViewGroup.MarginLayoutParams) msgTitleHeaderTxt.getLayoutParams();

        croppedViewX = (LoginZoomToU.width -scannerViewParam.width)/2;

        int scannerViewTopMarginFromOrigin = ((scannerViewParam.height/2) + headerBarCodeScannerViewHeightParam.height + msgTitleHeaderTxtHeight.height + getStatusBarHeight());
        croppedViewY = (LoginZoomToU.height/2 - scannerViewTopMarginFromOrigin);
        scannerViewParam.setMargins((int) croppedViewX, (int) croppedViewY, 0, 0);
        scannerView.setLayoutParams(scannerViewParam);

        croppedViewHeight = scannerViewParam.height;
        croppedViewWidth = scannerViewParam.width;

        scannerView.setOnTouchListener(this);

        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
        preivew.addView(mPreview);
        preivew.removeView(scannerView);
        preivew.addView(scannerView);
        previewing = true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.next_btn:{
              if(checkValidation(scanResult,note.getText().toString()))
                  GetActiveBookingDetailByBookingID();
                  // new GetActiveBookingDetailByBookingID().execute();
            }

        }
    }

    private boolean checkValidation(String scanResult, String note) {
        if(TextUtils.isEmpty(scanResult) && TextUtils.isEmpty(note)){
            DialogActivity.alertDialogView(this,"Alert!",getString(R.string.dhl_warning));
          return false;
        }
        else if(TextUtils.isEmpty(scanResult)) {
            DialogActivity.alertDialogView(this,"Alert!",getString(R.string.dhl_warning));
            return false;
        } else if(TextUtils.isEmpty(note)) {
            DialogActivity.alertDialogView(this,"Alert!",getString(R.string.dhl_warning));
            return false;
        } return true;
    }

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (previewing) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    Camera.Size zBarCameraPreviewSize = parameters.getPreviewSize();

                    float zBarViewWidth = LoginZoomToU.width;
                    float zBarViewHeight = LoginZoomToU.height;;

                    float factorWidth = zBarCameraPreviewSize.width / zBarViewHeight;
                    float factorHeight = zBarCameraPreviewSize.height / zBarViewWidth;

                    Image barcode = new Image(zBarCameraPreviewSize.width, zBarCameraPreviewSize.height, "Y800");
                    barcode.setData(data);

                    barcode.setCrop((int) (croppedViewY * factorWidth), (int) (croppedViewX * factorHeight), (int) (croppedViewHeight * factorWidth), (int) (croppedViewWidth * factorHeight));

                    int result = scanner.scanImage(barcode);

                    if (result != 0) {
                      //  previewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        SymbolSet syms = scanner.getResults();
                        for (Symbol sym : syms) {
                            scanResult = sym.getData().trim();
                            barcodeScanned = true;
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1500);
        }
    };


    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            try {
                if (previewing)
                    mCamera.autoFocus(autoFocusCB);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ViewGroup.MarginLayoutParams lParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                _xDelta = X - lParams.leftMargin;
                _yDelta = Y - lParams.topMargin;
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = X - _xDelta;
                layoutParams.topMargin = Y - _yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;

                view.setLayoutParams(layoutParams);
                croppedViewX = layoutParams.leftMargin;
                croppedViewY = layoutParams.topMargin;
                break;
        }
        preivew.invalidate();
        return true;
    }

    private void GetActiveBookingDetailByBookingID(){

        final String[] responseActiveBookingDetailStr = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressToGetPassword == null)
                        progressToGetPassword = new ProgressDialog(NewBarcodeActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressToGetPassword);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                JSONObject jObjOfInviteTeamMember = null;
                try {
                    WebserviceHandler handler = new WebserviceHandler();
                    jObjOfInviteTeamMember = new JSONObject();
                    jObjOfInviteTeamMember.put("RunId", submitNoteForReamingDhl.getRunId());
                    jObjOfInviteTeamMember.put("SupervisorIdBarcode", scanResult);
                    jObjOfInviteTeamMember.put("Notes", note.getText().toString());
                    jObjOfInviteTeamMember.put("DepotLocation", submitNoteForReamingDhl.getDepotLocation());
                    JSONArray leftBookingArrays = new JSONArray();
                    String[] split = submitNoteForReamingDhl.getRemainingPickup().split(",");
                    if (split!=null)
                    {
                        for (String bookingId:split)
                        {
                            int parseBookingId = Integer.parseInt(bookingId.trim());
                            leftBookingArrays.put(parseBookingId);
                        }
                        jObjOfInviteTeamMember.put("NotPickedUpBookingIds",leftBookingArrays);
                    }
                    responseActiveBookingDetailStr[0] = handler.getSubmitBarcodeNote(jObjOfInviteTeamMember.toString());
                    handler = null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if(progressToGetPassword.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressToGetPassword);


                    if(LoginZoomToU.isLoginSuccess==0){
                        Toast.makeText(NewBarcodeActivity.this,"Thank you for confirmation. We have captured the submitted details.",Toast.LENGTH_SHORT).show();
                        finish();

                    }else if(LoginZoomToU.isLoginSuccess==2){
                        Toast.makeText(NewBarcodeActivity.this, responseActiveBookingDetailStr[0],Toast.LENGTH_SHORT).show();
                        //finish();
                    }else
                    {
                        DialogActivity.alertDialogView(NewBarcodeActivity.this, "Server Error !", "Please try later.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    if(progressToGetPassword.isShowing())
                        progressToGetPassword.dismiss();

                    DialogActivity.alertDialogView(NewBarcodeActivity.this, "Server Error !", "Please try later.");

                }
            }
        }.execute();

    }



    @Override
    public void onBackPressed() {
        DialogActivity.alertDialogView(this,"Alert!",getString(R.string.dhl_warning));
    }

    @Override
    protected void onDestroy() {
            releaseCamera();
          super.onDestroy();

    }

    public void releaseCamera() {
        try {
            if (mCamera != null) {
                previewing = false;
                mCamera.setPreviewCallback(null);
                mCamera.release();
                mCamera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        if (cameraPermissionGranted)
        releaseCamera();
        super.onStop();

    }

    @Override
    protected void onResume() {
        if (cameraPermissionGranted)
        inItBarcodeScanner();
        super.onResume();
    }


}