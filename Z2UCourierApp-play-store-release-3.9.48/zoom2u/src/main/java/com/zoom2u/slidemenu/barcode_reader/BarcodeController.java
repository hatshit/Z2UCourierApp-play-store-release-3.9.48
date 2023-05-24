package com.zoom2u.slidemenu.barcode_reader;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.net.Uri;

import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatDetailActivity;
import com.z2u.chatview.ChatViewBookingScreen;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.MyContentProviderAtLocal;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.run_popup.DialogRunPopup;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class BarcodeController implements View.OnTouchListener{

    public Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;
    private ImageScanner scanner;
    public boolean barcodeScanned = false;
    public boolean previewing = true;
    String scanResult;
    ProgressDialog progressForDeliveryHistory;
    String message;
    Dialog enterFieldDialog;
    Context currentBarcodeContext;

    String awbNumber;

    FrameLayout preview;
    float croppedViewX, croppedViewY;
    float croppedViewHeight, croppedViewWidth;
    int runID = 0;
    private int _xDelta;
    private int _yDelta;
    ViewGroup.MarginLayoutParams headerBarCodeScannerViewHeightParam;
    ViewGroup.MarginLayoutParams msgTitleHeaderTxtHeight;
    ViewGroup.MarginLayoutParams scannerViewParam;
    ImageView scannerView;

    int isPickingUpByAWB = 0;       /* 1 For pickup, 2 for drop off, 3 for return to DHL */

    CheckBox toggleForReturnInBarCodeView;

    private boolean isAlreadyScanned = false;

    private boolean isHaveingMultilePieceForDropOrReturn = false;

    ArrayList<String> pieceArrayFromListOrDetail;

    public static HashSet<String> scannedPieceIDArray;            //******* Scanned piece id array
    HashSet<String> tempScannedPieceIDArray;            //******* temp scanned piece id array
    ArrayList<String> pieceIdWithScannedDelivery;  //******* Get all piece id from server and save into this array
    ArrayList<String> pieceIdWithPickedUpStatus;  //******* Get piece id with status (Which is picked up, TTD etc) from server and save into this array

    private int bookingID;
    int lateReasonId;
    private boolean isReturnedToDHL = false;
    private boolean isATLProvided = false;
    private String firstDropAttemptWasLate;
    private String bookingStatus = "";
    private String orderNumberGetBarcodeResponse;
    private String dropContactNameBarcodeResponse;
    private String dropAddressBarcodeResponse;
    private String pickupContactNameBarcodeResponse;
    private String pickupAddressBarcodeResponse;
    private String dropDateTimeBarcodeResponse = "";

    /* ********* Create another instance for Camera to take selfie ************ */
    public final static int CAMERA_PIC_REQUEST1 = 0;
    public final static int CAMERA_PIC_REQUEST_OVERLAY = 100;

    //******** Scanned piece from Active detail page for Non-DHL pickup *******
    public static String scanned_piece_to_pickup = "";

    String runType;
    String saveVehicleAgreementRes;
    //******** Back from Barcode scanner view ***********//
    public void backToBooking(){
        if (BarcodeScanner.ScanAWBForPick == 1) {
            if (ActiveBookingView.arrayOfBookingId != null)
                ActiveBookingView.arrayOfBookingId.clear();
            ActiveBookingDetail_New.isPickBtn = false;
            if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 2) {
                Intent intent1 = new Intent(currentBarcodeContext, ServiceToUpdate_ActiveBookingList.class);
                currentBarcodeContext.startService(intent1);
                intent1 = null;
            }
        }

        Intent bookingCountService = new Intent(currentBarcodeContext, ServiceForCourierBookingCount.class);
        bookingCountService.putExtra("Is_API_Call_Require", 1);
        currentBarcodeContext.startService(bookingCountService);
        bookingCountService = null;

        ((Activity) currentBarcodeContext).setResult(Activity.RESULT_OK);
        ((Activity) currentBarcodeContext).finish();
        ((Activity) currentBarcodeContext).overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    static {
        try {
            System.loadLibrary("iconv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BarcodeController(Context currentBarcodeContext, FrameLayout preview, TextView msgTitleHeaderTxt, ImageView cornerBorder, RelativeLayout headerBarCodeScanner, CheckBox toggleForReturnInBarCodeView){
        if(ActiveBookingView.arrayOfBookingId == null)
            ActiveBookingView.arrayOfBookingId = new HashSet<Integer>();
        else
            ActiveBookingView.arrayOfBookingId.clear();

        this.runType = ((BarcodeScanner) currentBarcodeContext).runType;
        this.preview = preview;
        this.currentBarcodeContext = currentBarcodeContext;
        this.scannerView = cornerBorder;
        this.toggleForReturnInBarCodeView = toggleForReturnInBarCodeView;
        inItBarcodeScanner(preview, msgTitleHeaderTxt, headerBarCodeScanner);
    }

    public BarcodeController(Context currentBarcodeContext, FrameLayout preview, TextView msgTitleHeaderTxt, ImageView cornerBorder, RelativeLayout headerBarCodeScanner, String awbNumber, ArrayList<String> pieceArrayFromListOrDetail){
        this.preview = preview;
        this.currentBarcodeContext = currentBarcodeContext;
        this.awbNumber = awbNumber;
        this.scannerView = cornerBorder;
        this.pieceArrayFromListOrDetail = pieceArrayFromListOrDetail;
        this.runType = ((BarcodeScanner) currentBarcodeContext).runType;

        if (pieceArrayFromListOrDetail.size() > 1) {
            if (pieceIdWithPickedUpStatus == null)
                pieceIdWithPickedUpStatus = new ArrayList<String>();
            else
                pieceIdWithPickedUpStatus.clear();

            if (ActiveBookingDetail_New.scannedPieceMap != null) {
                for (Map.Entry<String, Boolean> entry : ActiveBookingDetail_New.scannedPieceMap.entrySet()) {
                    if (entry.getValue())
                        pieceIdWithPickedUpStatus.add(entry.getKey());
                }
            }
        }

        inItBarcodeScanner(preview, msgTitleHeaderTxt, headerBarCodeScanner);
    }

    public void inItBarcodeScanner(FrameLayout preview, TextView msgTitleHeaderTxt, RelativeLayout headerBarCodeScanner) {

        isHaveingMultilePieceForDropOrReturn = false;            //******* Disable multiple pieces for Drop or return
        pieceIdWithScannedDelivery = new ArrayList<String>();

        autoFocusHandler = new Handler();
        mCamera = getCameraInstance();

        // Instance barcode scanner
        scanner = new ImageScanner();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);

        scannerViewParam = (ViewGroup.MarginLayoutParams) scannerView.getLayoutParams();
        headerBarCodeScannerViewHeightParam = (ViewGroup.MarginLayoutParams) headerBarCodeScanner.getLayoutParams();
        msgTitleHeaderTxtHeight = (ViewGroup.MarginLayoutParams) msgTitleHeaderTxt.getLayoutParams();

        croppedViewX = (LoginZoomToU.width - scannerViewParam.width)/2;

        int scannerViewTopMarginFromOrigin = ((scannerViewParam.height/2) + headerBarCodeScannerViewHeightParam.height + msgTitleHeaderTxtHeight.height + getStatusBarHeight());
        croppedViewY = (LoginZoomToU.height/2 - scannerViewTopMarginFromOrigin);
        scannerViewParam.setMargins((int) croppedViewX, (int) croppedViewY, 0, 0);
        scannerView.setLayoutParams(scannerViewParam);

        croppedViewHeight = scannerViewParam.height;
        croppedViewWidth = scannerViewParam.width;

        scannerView.setOnTouchListener(this);

        mPreview = new CameraPreview(currentBarcodeContext, mCamera, previewCb, autoFocusCB);
        preview.addView(mPreview);
        preview.removeView(scannerView);
        preview.addView(scannerView);

        previewing = true;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = currentBarcodeContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = currentBarcodeContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
        }
        return c;
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

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (previewing) {
                try {
                    Camera.Parameters parameters = camera.getParameters();
                    Camera.Size zBarCameraPreviewSize = parameters.getPreviewSize();

                    float zBarViewWidth = LoginZoomToU.width;
                    float zBarViewHeight = LoginZoomToU.height - (msgTitleHeaderTxtHeight.height + headerBarCodeScannerViewHeightParam.height + getStatusBarHeight());

                    float factorWidth = zBarCameraPreviewSize.width / zBarViewHeight;
                    float factorHeight = zBarCameraPreviewSize.height / zBarViewWidth;

                    Image barcode = new Image(zBarCameraPreviewSize.width, zBarCameraPreviewSize.height, "Y800");
                    barcode.setData(data);

                    barcode.setCrop((int) (croppedViewY * factorWidth), (int) (croppedViewX * factorHeight), (int) (croppedViewHeight * factorWidth), (int) (croppedViewWidth * factorHeight));

                    int result = scanner.scanImage(barcode);

                    if (result != 0) {
                        previewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();

                        SymbolSet syms = scanner.getResults();
                        for (Symbol sym : syms) {
                            scanResult = sym.getData().trim();
                            if (!scanResult.equals(null) || !scanResult.equals("")) {
                                if (BarcodeScanner.ScanAWBForPick == 1) {
                                    if (isHaveingMultilePieceForDropOrReturn) {
                                        if (toggleForReturnInBarCodeView.isChecked() && !isReturnedToDHL)
                                            multiplePiecesForDropOrReturnDelivery("Returned");
                                        else if (toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL)
                                            multiplePiecesForDropOrReturnDelivery("Returned");
                                        else if (!toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL)
                                            manualScanDialog(false, "From here you can only return the parcels and no other actions allowed");
                                        else
                                            multiplePiecesForDropOrReturnDelivery("Dropped off");
                                    } else
                                        GetBookingInfoByAwb();
                                        //new GetBookingInfoByAwb().execute();
                                } else {
                                    if (pieceArrayFromListOrDetail.size() == 0) {
                                        if (awbNumber.toLowerCase().equals(scanResult.toLowerCase())) {
                                            BarcodeScanner.isScannedSuccessFully = true;
                                            message = "AWB / PieceID number matched successfully";
                                            dialogForBarcode("Matched!", 0, 0);
                                            setPickedUpPiece_ForNonDHL_ActiveDetail(scanResult);
                                            scanResult = null;
                                        } else {
                                            if (BarcodeScanner.ScanAWBForPick == 2)
                                                message = "The booking you tried to deliver does not match with scanned AWB / PieceID number.";
                                            else if (BarcodeScanner.ScanAWBForPick == 3)
                                                message = "The booking you are trying to return does not match with scanned AWB / PieceID number.";
                                            else if (BarcodeScanner.ScanAWBForPick == 7)
                                                message = "The booking you are trying to Pick up does not match with scanned AWB / PieceID number.";
                                            else
                                                message = " The booking you are trying to drop off does not match with scanned AWB / PieceID number.";
                                            //                               message = "AWB "+scanResult+" does not exist. Please see DHL staff or contact Zoom2u Support";
                                            dialogForBarcode("Alert!", 0, 0);
                                            scanResult = null;
                                        }
                                    } else if (pieceArrayFromListOrDetail.size() == 1) {
                                        if (awbNumber.equals(scanResult) || pieceArrayFromListOrDetail.get(0).equals(scanResult)) {
                                            BarcodeScanner.isScannedSuccessFully = true;
                                            message = "AWB / PieceID number matched successfully";
                                            dialogForBarcode("Matched!", 0, 0);
                                            setPickedUpPiece_ForNonDHL_ActiveDetail(pieceArrayFromListOrDetail.get(0));
                                            scanResult = null;
                                        } else {
                                            if (BarcodeScanner.ScanAWBForPick == 2)
                                                message = "The booking you tried to deliver does not match with scanned AWB/ PieceID number.";
                                            else if (BarcodeScanner.ScanAWBForPick == 3)
                                                message = " The booking you are trying to return does not match with scanned AWB/ PieceID number.";
                                            else if (BarcodeScanner.ScanAWBForPick == 7)
                                                message = "The booking you are trying to Pick up does not match with scanned AWB / PieceID number.";
                                            else
                                                message = " The booking you are trying to drop off does not match with scanned AWB/ PieceID number.";
                                            //                               message = "AWB "+scanResult+" does not exist. Please see DHL staff or contact Zoom2u Support";
                                            dialogForBarcode("Alert!", 0, 0);
                                            scanResult = null;
                                        }
                                    } else {
                                        if (BarcodeScanner.ScanAWBForPick == 2 || BarcodeScanner.ScanAWBForPick == 7)
                                            scanMultiplePiecesFromListAndDetailForTriedToDeliver(scanResult, false);
                                        else {
                                            if (isHaveingMultilePieceForDropOrReturn)
                                                scanMultiplePiecesFromListAndDetailForDropOffAndreturn(scanResult, false);
                                            else {
                                                tempScannedPieceIDArray = new HashSet<String>();
                                                scanMultiplePiecesFromListAndDetailForDropOffAndreturn(scanResult, false);
                                            }
                                        }
                                    }
                                }
                            }
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

    // temprary coment 1.38 time / 28/04/22
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1500);
        }
    };

    Dialog dialogManualScan;
    // *************  Alert for search DHL bookings **************
    public void manualScanDialog(final boolean isDialogForManualScan, String titleMsgStr){
        if(dialogManualScan != null)
            if (dialogManualScan.isShowing())
                dialogManualScan.dismiss();

        if(dialogManualScan != null)
            dialogManualScan = null;
        dialogManualScan = new Dialog(currentBarcodeContext);
        dialogManualScan.setCancelable(false);
        dialogManualScan.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogManualScan.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogManualScan.setContentView(R.layout.dhlsearchdialog);

        Window window = dialogManualScan.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView manualScanHeaderMsgTxt = (TextView) dialogManualScan.findViewById(R.id.dialogHeaderDHLSearchBookingText);


        final EditText edtAWBNumber = (EditText) dialogManualScan.findViewById(R.id.edtSearchDHLBooking);
        edtAWBNumber.setInputType(InputType.TYPE_CLASS_TEXT);


        final TextView manualScanValidationTxt = (TextView) dialogManualScan.findViewById(R.id.dhlSearchValidationTxt);


        Button cancelBtnManualScanDialog = (Button) dialogManualScan.findViewById(R.id.cancelDhlSearchBtn);

        cancelBtnManualScanDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogManualScan.dismiss();
                dialogManualScan = null;
                LoginZoomToU.imm.hideSoftInputFromWindow(edtAWBNumber.getWindowToken(), 0);
                if (!isDialogForManualScan){
                    callConfirmPIckDropBookingView();
                }
            }
        });
        Button searchDhlBtn = (Button) dialogManualScan.findViewById(R.id.searchDhlBtn);


        if (isDialogForManualScan){
            manualScanHeaderMsgTxt.setText(titleMsgStr);
            edtAWBNumber.setVisibility(View.VISIBLE);
            manualScanValidationTxt.setVisibility(View.GONE);
            cancelBtnManualScanDialog.setText("Cancel");
            searchDhlBtn.setText("Done");
        } else {
            manualScanHeaderMsgTxt.setText(titleMsgStr);
            edtAWBNumber.setVisibility(View.GONE);
            manualScanValidationTxt.setVisibility(View.GONE);
            cancelBtnManualScanDialog.setText("No");
            searchDhlBtn.setText("Yes");
        }

        searchDhlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isDialogForManualScan){
                    resetBarcodeScanner();
                    dialogManualScan.dismiss();
                    dialogManualScan = null;
                }else {
                    if (edtAWBNumber.getText().toString().equals("")) {
                        manualScanValidationTxt.setVisibility(View.VISIBLE);
                    } else {
                        LoginZoomToU.imm.hideSoftInputFromWindow(edtAWBNumber.getWindowToken(), 0);
                        manualScanValidationTxt.setVisibility(View.INVISIBLE);
                        if (BarcodeScanner.ScanAWBForPick == 1) {
                            scanResult = edtAWBNumber.getText().toString();
                            if (isHaveingMultilePieceForDropOrReturn) {
                                dialogManualScan.dismiss();
                                dialogManualScan = null;
                                if (toggleForReturnInBarCodeView.isChecked() && !isReturnedToDHL)
                                    multiplePiecesForDropOrReturnDelivery("Returned");
                                else if (toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL)
                                    multiplePiecesForDropOrReturnDelivery("Returned");
                                else if (!toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL)
                                    manualScanDialog(false, "From here you can only return the parcels and no other actions allowed");
                                else
                                    multiplePiecesForDropOrReturnDelivery("Dropped off");
                            } else
                                GetBookingInfoByAwb();
                                //new GetBookingInfoByAwb().execute();
                        } else {
                            if (pieceArrayFromListOrDetail.size() == 0) {
                                if (awbNumber.toLowerCase().equals(edtAWBNumber.getText().toString().toLowerCase())) {
                                    setPickedUpPiece_ForNonDHL_ActiveDetail(edtAWBNumber.getText().toString());
                                    scanResult = null;
                                    dialogManualScan.dismiss();
                                    dialogManualScan = null;
                                    BarcodeScanner.isScannedSuccessFully = true;
                                    releaseCamera();
                                    backToBooking();
                                } else {
                                    if (BarcodeScanner.ScanAWBForPick == 2)
                                        message = "The booking you tried to deliver does not match with scanned AWB / PieceID number.";
                                    else if (BarcodeScanner.ScanAWBForPick == 3)
                                        message = " The booking you are trying to return does not match with scanned AWB / PieceID number.";
                                    else if (BarcodeScanner.ScanAWBForPick == 7)
                                        message = "The booking you are trying to Pick up does not match with scanned AWB / PieceID number.";
                                    else
                                        message = " The booking you are trying to drop off does not match with scanned AWB / PieceID number.";
                                    //                               message = "AWB "+scanResult+" does not exist. Please see DHL staff or contact Zoom2u Support";
                                    dialogForBarcode("Alert!", 0, 0);
                                    scanResult = null;
                                }
                            } else if (pieceArrayFromListOrDetail.size() == 1) {
                                if (awbNumber.equals(edtAWBNumber.getText().toString()) || pieceArrayFromListOrDetail.get(0).equals(edtAWBNumber.getText().toString())) {
                                    setPickedUpPiece_ForNonDHL_ActiveDetail(pieceArrayFromListOrDetail.get(0));
                                    scanResult = null;
                                    dialogManualScan.dismiss();
                                    dialogManualScan = null;
                                    BarcodeScanner.isScannedSuccessFully = true;
                                    releaseCamera();
                                    backToBooking();
                                } else {
                                    if (BarcodeScanner.ScanAWBForPick == 2)
                                        message = "The booking you tried to deliver does not match with scanned AWB/ PieceID number.";
                                    else if (BarcodeScanner.ScanAWBForPick == 3)
                                        message = " The booking you are trying to return does not match with scanned AWB/ PieceID number.";
                                    else if (BarcodeScanner.ScanAWBForPick == 7)
                                        message = "The booking you are trying to Pick up does not match with scanned AWB / PieceID number.";
                                    else
                                        message = " The booking you are trying to drop off does not match with scanned AWB/ PieceID number.";
                                    //                               message = "AWB "+scanResult+" does not exist. Please see DHL staff or contact Zoom2u Support";
                                    dialogForBarcode("Alert!", 0, 0);
                                    scanResult = null;
                                }
                            } else {
                                if (BarcodeScanner.ScanAWBForPick == 2 || BarcodeScanner.ScanAWBForPick == 7)
                                    scanMultiplePiecesFromListAndDetailForTriedToDeliver(edtAWBNumber.getText().toString(), true);
                                else {
                                    if (isHaveingMultilePieceForDropOrReturn)
                                        scanMultiplePiecesFromListAndDetailForDropOffAndreturn(edtAWBNumber.getText().toString(), true);
                                    else {
                                        tempScannedPieceIDArray = new HashSet<String>();
                                        scanMultiplePiecesFromListAndDetailForDropOffAndreturn(edtAWBNumber.getText().toString(), true);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        dialogManualScan.show();
    }

    //************* Scan multiple pieces from list and detail for Drop off**************
    private void scanMultiplePiecesFromListAndDetailForDropOffAndreturn(String scannedItemStr, boolean isManualScan) {
        String toDropOrReturn = "";
        if (BarcodeScanner.ScanAWBForPick == 3)
            toDropOrReturn = "Returned";
        else
            toDropOrReturn = "Dropped Off";

        if (scannedItemStr.equals(awbNumber)) {
            if (isManualScan){
                dialogManualScan.dismiss();
                dialogManualScan = null;
            }
            message = "AWB: " + awbNumber + " \n\nThis booking has "
                    + pieceArrayFromListOrDetail.size() + " pieces.\n\nPlease scan each of the piece IDs to mark them as "+toDropOrReturn+
                    "\n\nA PieceID will usually start with (J) and be approx 20 characters.";
            scanResult = null;
            dialogForBarcode("Note!", 1, 0);
        } else {
            if (pieceArrayFromListOrDetail.contains(scannedItemStr)) {
                if (tempScannedPieceIDArray.contains(scannedItemStr)) {
                    isHaveingMultilePieceForDropOrReturn = true;
                    if (isManualScan){
                        dialogManualScan.dismiss();
                        dialogManualScan = null;
                    }
                    message = "You have already scanned this PieceId " + scannedItemStr + " to "+toDropOrReturn+" this delivery.";
                    scanResult = null;
                    if (pieceIdWithPickedUpStatus.size() == 0)
                        showAlertForScanNext(toDropOrReturn+"!", 3, 1);  //************** Show alert for scan next and skip
                    else
                        showAlertForScanNext(toDropOrReturn+"!", 2, 1);  //************** Show alert for scan next
                } else {
                    ActiveBookingDetail_New.scannedPieceMap.put(scannedItemStr, true);
                    tempScannedPieceIDArray.add(scannedItemStr);
                    if (pieceArrayFromListOrDetail.size() == tempScannedPieceIDArray.size()) {
                        isHaveingMultilePieceForDropOrReturn = false;
                        if (isManualScan){
                            dialogManualScan.dismiss();
                            dialogManualScan = null;
                        }
                        BarcodeScanner.isScannedSuccessFully = true;
                        releaseCamera();
                        backToBooking();
                    } else {

                        message = "AWB: " + awbNumber + "\nPiece: " + scannedItemStr + "\nThis booking has "
                                + pieceArrayFromListOrDetail.size() + " pieces.\nPieces scanned: " + tempScannedPieceIDArray.size() + "/" + pieceArrayFromListOrDetail.size() + "\n\n";

                        isHaveingMultilePieceForDropOrReturn = true;
                        if (isManualScan){
                            dialogManualScan.dismiss();
                            dialogManualScan = null;
                        }

                        if (pieceIdWithPickedUpStatus != null) {
                            if (pieceIdWithPickedUpStatus.contains(scannedItemStr)) {
                                pieceIdWithPickedUpStatus.remove(scannedItemStr);
                                if (pieceIdWithPickedUpStatus.size() == 0)
                                    showAlertForScanNext(toDropOrReturn+"!", 3, 1);  //************** Show alert for scan next and skip
                                else
                                    showAlertForScanNext(toDropOrReturn+"!", 2, 1);  //************** Show alert for scan next
                            } else {
                                if (pieceIdWithPickedUpStatus.size() == 0)
                                    showAlertForScanNext(toDropOrReturn + "!", 3, 1);  //************** Show alert for scan next and skip
                                else
                                    showAlertForScanNext(toDropOrReturn + "!", 2, 1);  //************** Show alert for scan next
                            }
                        } else
                            showAlertForScanNext(toDropOrReturn+"!", 2, 1);   //************** Show alert for scan next
                    }
                }
            } else
                setErrorMsgAlert("Alert!", "Piece ID " + scannedItemStr + " does not exist with this delivery. Please contact Zoom2u Support.");
        }
    }

    //************* Scan multiple pieces from list and detail for Tried to deliver**************
    private void scanMultiplePiecesFromListAndDetailForTriedToDeliver (String scannedItemStr, boolean isManualScan) {
            if (scannedItemStr.equals(awbNumber) || pieceArrayFromListOrDetail.contains(scannedItemStr)) {
                if (isManualScan){
                    dialogManualScan.dismiss();
                    dialogManualScan = null;
                }
                if (BarcodeScanner.ScanAWBForPick == 7 && scannedItemStr.equals(awbNumber)) {
                    message = "AWB: " + awbNumber + " \n\nThis booking has "
                            + pieceArrayFromListOrDetail.size() + " pieces.\n\nPlease scan atleast one piece IDs to mark them as picked up"+
                            "\n\nA PieceID will usually start with (J) and be approx 20 characters.";
                    dialogForBarcode("Note!", 1, 0);
                } else {
                    setPickedUpPiece_ForNonDHL_ActiveDetail(scannedItemStr);
                    BarcodeScanner.isScannedSuccessFully = true;
                    releaseCamera();
                    backToBooking();
                }
                scanResult = null;
            } else {
                if (BarcodeScanner.ScanAWBForPick == 7)
                    setErrorMsgAlert("Alert!", "The booking you trying to Pick up does not match with scanned AWB / Piece ID number.");
                else
                    setErrorMsgAlert("Alert!", "The booking you tried to deliver does not match with scanned AWB / Piece ID number.");
            }
    }

    private void setPickedUpPiece_ForNonDHL_ActiveDetail(String scannedItemStr) {
        if (BarcodeScanner.ScanAWBForPick == 7)
            scanned_piece_to_pickup = scannedItemStr;
    }

    private int getFrontCameraId() {
        Camera.CameraInfo ci = new Camera.CameraInfo();
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
                return i;
        }
        return -1; // No front-facing camera found
    }

    public void showAgreeDisagreeDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(currentBarcodeContext);
        View dialogView=LayoutInflater.from(currentBarcodeContext).inflate(R.layout.vehicle_agree_for_dhl,null,false);
        builder.setView(dialogView);

        TextView btnAgree=dialogView.findViewById(R.id.agreeBtnVehicleAgree);
        TextView btnDisagree=dialogView.findViewById(R.id.disagreeBtnVehicleAgree);

        AlertDialog alertDialog = builder.create();

        btnAgree.setOnClickListener(v->{
            SaveVehicleAgreementAsync(new CallBackAfterAgree() {
                @Override
                public void onSuccess() {
                    alertDialog.dismiss();
                    callBookingInfoAfterSelfie();
                }

                @Override
                public void onFailure() {
                    setErrorMsgAlert("Fail","Please try again.");
                }
            });
        });

        btnDisagree.setOnClickListener(v -> {
            DialogActivity.alertDialogView(currentBarcodeContext, "Zoom2U", "Unfortunately if you are unable to secure your vehicle you will not be able to do further deliveries. Please contact "+R.string.zoom2u_support_no);
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void dialogForBarcode(String titleStr, int changeTitleTxtStyle, final int fromDetailPage){
        try{
            if(enterFieldDialog != null)
                if(enterFieldDialog.isShowing())
                    enterFieldDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(enterFieldDialog != null)
                enterFieldDialog = null;
            enterFieldDialog = new Dialog(currentBarcodeContext);
            enterFieldDialog.setCancelable(false);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            enterFieldDialog.setContentView(R.layout.dialogview);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);
            if (changeTitleTxtStyle == 0)
                enterFieldDialogHEader.setTypeface(LoginZoomToU.NOVA_BOLD);
            else if (changeTitleTxtStyle == 1){
                enterFieldDialogHEader.setTextColor(Color.parseColor("#FF476A"));
                enterFieldDialogHEader.setTypeface(LoginZoomToU.NOVA_REGULAR);
            } else {
                enterFieldDialogHEader.setTextColor(Color.parseColor("#5EAC40"));
                enterFieldDialogHEader.setTypeface(LoginZoomToU.NOVA_REGULAR);
            }
            enterFieldDialogHEader.setText(titleStr);

            TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);
            enterFieldDialogMsg.setTypeface(LoginZoomToU.NOVA_REGULAR);
            enterFieldDialogMsg.setText(message);

            Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);
            enterFieldDialogDoneBtn.setTypeface(LoginZoomToU.NOVA_BOLD);
            enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                    if (BarcodeScanner.isScannedSuccessFully) {
                        try {
                            if(dialogManualScan != null)
                                if (dialogManualScan.isShowing())
                                    dialogManualScan.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        releaseCamera();
                        scanResult=null;
                        backToBooking();
                    }else {
                        BarcodeScanner.isScannedSuccessFully = false;
                        resetBarcodeScanner();
                    }
                }
            });

            LinearLayout twoBtnLayoutForSkipAndNext = (LinearLayout) enterFieldDialog.findViewById(R.id.twoBtnLayoutForSkipAndNext);
            twoBtnLayoutForSkipAndNext.setVisibility(View.GONE);
            Button dialogScanNextBtn = (Button) enterFieldDialog.findViewById(R.id.dialogScanNextBtn);
            dialogScanNextBtn.setTypeface(LoginZoomToU.NOVA_BOLD);
            Button dialogSkipBtn = (Button) enterFieldDialog.findViewById(R.id.dialogSkipBtn);
            dialogSkipBtn.setTypeface(LoginZoomToU.NOVA_BOLD);
            if (changeTitleTxtStyle == 2) {
                enterFieldDialogDoneBtn.setVisibility(View.VISIBLE);
                enterFieldDialogDoneBtn.setText("Scan Next");
            } else if (changeTitleTxtStyle == 3) {
                enterFieldDialogDoneBtn.setVisibility(View.GONE);
                twoBtnLayoutForSkipAndNext.setVisibility(View.VISIBLE);
            }

            dialogScanNextBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                    if (BarcodeScanner.isScannedSuccessFully) {
                        try {
                            if(dialogManualScan != null)
                                if (dialogManualScan.isShowing())
                                    dialogManualScan.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        releaseCamera();
                        scanResult=null;
                        backToBooking();
                    }else {
                        BarcodeScanner.isScannedSuccessFully = false;
                        resetBarcodeScanner();
                    }
                }
            });

            dialogSkipBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                    if (fromDetailPage == 1) {
                        try {
                            if(dialogManualScan != null)
                                if (dialogManualScan.isShowing())
                                    dialogManualScan.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        releaseCamera();
                        scanResult=null;
                        backToBooking();
                        BarcodeScanner.isScannedSuccessFully = true;
                    } else {
                        resetBarcodeScanner();
                        BarcodeScanner.isScannedSuccessFully = false;
                        takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                    }

                    isHaveingMultilePieceForDropOrReturn = false;
                }
            });

            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetBarcodeScanner() {
        if (barcodeScanned) {
            barcodeScanned = false;
            previewing = true;
            mCamera.setPreviewCallback(previewCb);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCB);
        }
    }

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
        preview.invalidate();
        return true;
    }

    //check the first DHL booking of the day
    String dateText=null;
   private boolean checkFirstScanDialogDHLForSelfie(){
       SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
       Date dateToday = Calendar.getInstance().getTime();
       Date dateNow = null,savedDate = null;

       SharedPreferences sharedPref = currentBarcodeContext.getSharedPreferences(currentBarcodeContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
       String lastSavedDate = sharedPref.getString(currentBarcodeContext.getString(R.string.preference_selfie_key), null);

       try {
            dateText = sdf.format(dateToday);
            dateNow=sdf.parse(dateText);
            if (lastSavedDate!=null)
            savedDate = sdf.parse(lastSavedDate);
       } catch (Exception e) {
           e.printStackTrace();
       }

       if (lastSavedDate==null)
          return  true;
       else {
           if (dateNow.after(savedDate))
              return true;
           else  return false;
       }
   }

   //show the dialog for selfie
    private void showSelfieDialogMsg(){
        AlertDialog alertDialog;
    AlertDialog.Builder builder=new AlertDialog.Builder(currentBarcodeContext);
    builder.setTitle("");
    builder.setMessage("Please take a photo of yourself before starting the run.");
    builder.setPositiveButton("Take Photo", (dialog, which) ->{
        openCameraForSelfie();
        dialog.dismiss();

    });
         alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    public void showDialogForImageUpload()
    {
        try {
            if (progressForDeliveryHistory == null)
                progressForDeliveryHistory = new ProgressDialog(currentBarcodeContext);
            Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void dismissDialog(){
        Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
    }

    public int getBookingID() {
        return bookingID;
    }

    public void callBookingInfoAfterSelfie()
    {
        SharedPreferences sharedPref = currentBarcodeContext.getSharedPreferences(currentBarcodeContext.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(currentBarcodeContext.getString(R.string.preference_selfie_key),dateText);
        edit.apply();
        GetBookingInfoByAwb();
        //new GetBookingInfoByAwb().execute();
    }

    private void GetBookingInfoByAwb(){
        final String[] webPostBarcode = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(currentBarcodeContext);
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webserviceHandler = new WebserviceHandler();
                    webPostBarcode[0] = webserviceHandler.getDeliveryInfoByBarcode(scanResult,runID);
                    webserviceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    webPostBarcode[0] = "";
                }

            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                try {
                    try {
                        if (dialogManualScan != null)
                            if (dialogManualScan.isShowing())
                                dialogManualScan.dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            boolean b = checkFirstScanDialogDHLForSelfie();
                            if (b)
                            {
                                try {
                                    JSONObject jObjOfBarcodeScanner = new JSONObject(webPostBarcode[0]);
                                    bookingID = jObjOfBarcodeScanner.getInt("bookingId");
                                    currentBarcodeContext.startActivity(new Intent(currentBarcodeContext, DialogRunPopup.class));
                                    webPostBarcode[0] ="";
                                }catch (Exception ex)
                                {
                                    ex.printStackTrace();
                                }
                            }else {
                                isAlreadyScanned = false;
                                try {
                                    JSONObject jObjOfBarcodeScanner = new JSONObject(webPostBarcode[0]);
                                    bookingID = jObjOfBarcodeScanner.getInt("bookingId");
                                    bookingStatus = jObjOfBarcodeScanner.getString("status");
                                    try {
                                        if (!jObjOfBarcodeScanner.getString("orderNumber").equals("null") || jObjOfBarcodeScanner.getString("orderNumber") != null)
                                            orderNumberGetBarcodeResponse = jObjOfBarcodeScanner.getString("orderNumber");
                                        else
                                            orderNumberGetBarcodeResponse = "";
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        orderNumberGetBarcodeResponse = "";
                                    }
                                    dropDateTimeBarcodeResponse = jObjOfBarcodeScanner.getString("dropDateTime");
                                    isATLProvided = jObjOfBarcodeScanner.getBoolean("isATL");
                                    dropContactNameBarcodeResponse = jObjOfBarcodeScanner.getString("dropContactName");
                                    dropAddressBarcodeResponse = jObjOfBarcodeScanner.getString("dropAddress");
                                    pickupContactNameBarcodeResponse = jObjOfBarcodeScanner.getString("pickupContactName");
                                    pickupAddressBarcodeResponse = jObjOfBarcodeScanner.getString("pickupAddress");
                                    firstDropAttemptWasLate = jObjOfBarcodeScanner.getString("firstDropAttemptWasLate");
                                    lateReasonId = jObjOfBarcodeScanner.getInt("lateReasonId");

                                    if (pieceIdWithScannedDelivery != null)
                                        if (pieceIdWithScannedDelivery.size() > 0)
                                            pieceIdWithScannedDelivery.clear();

                                    JSONArray jArrayOfPieces = jObjOfBarcodeScanner.getJSONArray("pieces");
                                    int pieceArraySize = jArrayOfPieces.length();
                                    if (pieceArraySize > 0) {
                                        if (!bookingStatus.equals("Accepted") && !bookingStatus.equals("On Route to Pickup")) {
                                            if (pieceArraySize > 1) {
                                                if (pieceIdWithPickedUpStatus == null)
                                                    pieceIdWithPickedUpStatus = new ArrayList<String>();
                                                else
                                                    pieceIdWithPickedUpStatus.clear();
                                            }
                                        }
                                        for (int i = 0; i < jArrayOfPieces.length(); i++) {
                                            pieceIdWithScannedDelivery.add(jArrayOfPieces.getJSONObject(i).getString("barcode"));
                                            if (!toggleForReturnInBarCodeView.isChecked() && !isReturnedToDHL) {
                                                if (bookingStatus.equals("Picked up")) {
                                                    if (jArrayOfPieces.getJSONObject(i).getString("barcode").equals(scanResult)) {
                                                        if (jArrayOfPieces.getJSONObject(i).getString("status").equals("Picked up"))
                                                            isAlreadyScanned = true;
                                                        break;
                                                    }
                                                }
                                            }

                                            if (pieceIdWithPickedUpStatus != null) {
                                                try {
                                                    if (jArrayOfPieces.getJSONObject(i).getString("status").equals("Picked up")
                                                            || jArrayOfPieces.getJSONObject(i).getString("status").equals("Tried to deliver")
                                                            || jArrayOfPieces.getJSONObject(i).getString("status").equals("Returning"))
                                                        pieceIdWithPickedUpStatus.add(jArrayOfPieces.getJSONObject(i).getString("barcode"));
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }



                                    if (toggleForReturnInBarCodeView.isChecked() && !isReturnedToDHL) {
                                        isReturnedToDHL = !isReturnedToDHL;
                                        if (bookingStatus.equals("Accepted") || bookingStatus.equals("On Route to Pickup"))
                                            setErrorMsgAlert("Validation Error!", "This delivery currently has a status of Accepted, so can't be marked as returned");
                                        else
                                            markBookingAsReturned();
                                    } else if (toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL) {
                                        if (ActiveBookingView.arrayOfBookingId != null && ActiveBookingView.arrayOfBookingId.size() > 0
                                                && ActiveBookingView.arrayOfBookingId.contains(bookingID))
                                            if (bookingStatus.equals("Accepted") || bookingStatus.equals("On Route to Pickup"))
                                                setErrorMsgAlert("Validation Error!", "This delivery currently has a status of Accepted, so can't be marked as returned");
                                            else
                                                manualScanDialog(false, "This parcel has already been returned to DHL, do you have more parcels to return?");
                                        else {
                                            if (bookingStatus.equals("Accepted") || bookingStatus.equals("On Route to Pickup"))
                                                setErrorMsgAlert("Validation Error!", "This delivery currently has a status of Accepted, so can't be marked as returned");
                                            else
                                                markBookingAsReturned();
                                        }
                                    } else if (!toggleForReturnInBarCodeView.isChecked() && isReturnedToDHL) {
                                        if (ActiveBookingView.arrayOfBookingId != null && ActiveBookingView.arrayOfBookingId.size() == 0) {
                                            isReturnedToDHL = !isReturnedToDHL;
                                            requestBookingAccordingToStatus(bookingStatus);
                                        } else {
                                            if (bookingStatus.equals("Accepted") || bookingStatus.equals("On Route to Pickup"))
                                                setErrorMsgAlert("Validation Error!", "This delivery currently has a status of Accepted, so can't be marked as returned");
                                            else
                                                manualScanDialog(false, "From here you can only return the parcels and no other actions allowed");
                                        }
                                    } else
                                        requestBookingAccordingToStatus(bookingStatus);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                    setErrorMsgAlert("Error!", "Something went wrong, please try again");
                                }
                            }
                            break;
                        case 1:
                            setErrorMsgAlert("No network!", "No network connection, Please check your connection and try again");
                            break;
                        case 2:
                            setErrorMsgAlert("Alert!", "AWB / piece ID "+scanResult+" does not exist. Please contact Zoom2u Support.");
                            break;
                        case 3:
                            try {
                                String strMsg = new JSONObject(webPostBarcode[0]).getJSONArray("errors").getJSONObject(0).getString("message");
                                //    setErrorMsgAlert("Alert!", new JSONObject(webPostBarcode).getString("message"));
                                setErrorMsgAlert("Alert!", strMsg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                setErrorMsgAlert("Alert!", "The booking "+scanResult+" is allocated to another courier.");
                            }
                            break;
                        case 4:
                            setErrorMsgAlert("Server Error!", "Something went wrong, Please try later.");
                            break;
                    }
                    LoginZoomToU.isLoginSuccess = 0;
                }catch (Exception e) {
                    e.printStackTrace();
                    setErrorMsgAlert("Server Error!", "Something went wrong, Please try later.");
                }
            }
        }.execute();

    }

    //*********** Process delivery for Return after scan
    private void markBookingAsReturned() {
        isPickingUpByAWB = 3;    // Request for return to DHL
        dropContactNameBarcodeResponse = pickupContactNameBarcodeResponse;
        dropAddressBarcodeResponse = pickupAddressBarcodeResponse;
        tempScannedPieceIDArray = new HashSet<String>();   //******* Temp scanned piece id array
        if (pieceIdWithScannedDelivery.size() == 0) {
            isHaveingMultilePieceForDropOrReturn = false;
            takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
        } else if (pieceIdWithScannedDelivery.size() == 1) {
            isHaveingMultilePieceForDropOrReturn = false;
            tempScannedPieceIDArray.add(pieceIdWithScannedDelivery.get(0));
            takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
        } else
            multiplePiecesForDropOrReturnDelivery("Returned");
    }



    //************** Process booking according to its status ***********//
    private void requestBookingAccordingToStatus(String status) {
            switch (status) {
                case "Accepted":
                case "On Route to Pickup":
                    isReturnedToDHL = false;
                    isPickingUpByAWB = 1;    // Request for pick up
                    callAsyncTaskForRequest();
//                    SharePreference_FailedImg prefForSelfieDate = new SharePreference_FailedImg();
//                    //************* Check for selfie before pickup first DHL run  **************//
//                    String getSelfieDatePrefStr = prefForSelfieDate.getRecentDateFromPref(currentBarcodeContext);
//                    if (getSelfieDatePrefStr.equals(""))
//                        showValidationDialog("Please take a selfie for driver verification.");
//                    else {
//                        if (prefForSelfieDate.checkRecentDateToPref(getSelfieDatePrefStr))
//                            showValidationDialog("Please take a selfie for driver verification.");
//                        else
//                            callAsyncTaskForRequest();
//                    }
//                    prefForSelfieDate = null;
                    break;

                case "On Route to Dropoff":
                case "Tried to deliver":
                    proceedBookingForDropOff(status);
                    break;

                case "Picked up":
                    isReturnedToDHL = false;
                    if (isAlreadyScanned) {
                        isPickingUpByAWB = 0;
                        setErrorMsgAlert("Alert!", "Booking with this AWB / piece number "+scanResult+" is already Picked up by you.");
                    } else
                        callAsyncTaskForRequest();
                    break;
                case "Returning":
                    isReturnedToDHL = false;
                    message = "This AWB/piece Id can only be return.";
                    dialogForBarcode("Alert!", 0, 0);
                    break;
                default:
                    isReturnedToDHL = false;
                    message = "Something went wrong, please try again.";
                    dialogForBarcode("Error!", 0, 0);
                    break;
            }
    }

    //************ Process DHL booking for Dropoff
    private void proceedBookingForDropOff(String status) {
        tempScannedPieceIDArray = new HashSet<String>();   //******* Scanned piece id array
        isReturnedToDHL = false;
        isPickingUpByAWB = 2;    // Request for drop off
        if (pieceIdWithScannedDelivery.size() == 0) {
            isHaveingMultilePieceForDropOrReturn = false;
            takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
        } else if (pieceIdWithScannedDelivery.size() == 1) {
            isHaveingMultilePieceForDropOrReturn = false;
            tempScannedPieceIDArray.add(pieceIdWithScannedDelivery.get(0));
            takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
        } else
            multiplePiecesForDropOrReturnDelivery("Dropped off.");
    }

    //************ Scan multiple pieces to Drop and Return delivery ***********
    private void multiplePiecesForDropOrReturnDelivery(String dropReturnStr) {
        if (scanResult.equals(orderNumberGetBarcodeResponse)) {
            message = "AWB: " + scanResult + " \n\nThis booking has "
                    + pieceIdWithScannedDelivery.size() + " pieces.\n\nPlease scan each of the piece IDs to mark them as "+dropReturnStr+
                    "\n\nA PieceID will usually start with (J) and be approx 20 characters.";
            scanResult = null;
            dialogForBarcode("Note!", 1, 0);
        } else {
            isHaveingMultilePieceForDropOrReturn = true;
            if (pieceIdWithScannedDelivery.contains(scanResult)) {
                if (tempScannedPieceIDArray.contains(scanResult)) {
                    message = "You have already scanned this PieceId "+scanResult+" to "+dropReturnStr+" this delivery.";
                    if (pieceIdWithPickedUpStatus.size() == 0)
                        showAlertForScanNext(dropReturnStr, 3, 0);  //************** Show alert for scan next and skip
                    else
                        showAlertForScanNext(dropReturnStr, 2, 0);  //************** Show alert for scan next
                } else {
                    tempScannedPieceIDArray.add(scanResult);
                    if (pieceIdWithScannedDelivery.size() == tempScannedPieceIDArray.size()) {
                        isHaveingMultilePieceForDropOrReturn = false;
                        takePicture("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                    } else {

                        message = "AWB: "+orderNumberGetBarcodeResponse+"\nPiece: "+scanResult+"\nThis booking has "
                                + pieceIdWithScannedDelivery.size() + " pieces.\nPieces scanned: "+tempScannedPieceIDArray.size()+"/"+pieceIdWithScannedDelivery.size()+"\n\n";

                        if (pieceIdWithPickedUpStatus != null) {
                            if (pieceIdWithPickedUpStatus.contains(scanResult)) {
                                pieceIdWithPickedUpStatus.remove(scanResult);
                                if (pieceIdWithPickedUpStatus.size() == 0)
                                    showAlertForScanNext(dropReturnStr, 3, 0);  //************** Show alert for scan next and skip
                                else
                                    showAlertForScanNext(dropReturnStr, 2, 0);  //************** Show alert for scan next
                            } else {
                                if (pieceIdWithPickedUpStatus.size() == 0)
                                    showAlertForScanNext(dropReturnStr, 3, 0);  //************** Show alert for scan next and skip
                                else
                                    showAlertForScanNext(dropReturnStr, 2, 0);  //************** Show alert for scan next
                            }
                        } else
                            showAlertForScanNext(dropReturnStr, 2, 0);   //************** Show alert for scan next
                    }
                }
            } else
                setErrorMsgAlert("Alert!", "Piece ID "+scanResult+" does not exist with this delivery. Please contact Zoom2u Support.");
        }
    }

    //************** Show alert for scan next ***********
    private void showAlertForScanNext(String dropReturnStr, int aletCount, int fromDetailPage) {
        scanResult = null;
        dialogForBarcode(dropReturnStr+"!", aletCount, fromDetailPage);
    }

    void showValidationDialog(String validateStr){
        AlertDialog.Builder dialogBlankField = new AlertDialog.Builder(currentBarcodeContext);
        dialogBlankField.setCancelable(false);
        TextView myMsg = new TextView(currentBarcodeContext);
        myMsg.setText(validateStr);
        myMsg.setTextColor(Color.parseColor("#FFFFFF"));
        myMsg.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        myMsg.setPadding(20, 25, 20, 25);
        myMsg.setTextSize(16.0f);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogBlankField.setView(myMsg);
        dialogBlankField.setNeutralButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getFrontCameraId() == -1) {
                    Toast.makeText(currentBarcodeContext, "Front Camera Not Detected", Toast.LENGTH_SHORT).show();
                } else {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setClass(currentBarcodeContext, FrontCamera_Activity.class);
                    cameraIntent.putExtra("BookingID", bookingID);
                    ((Activity)currentBarcodeContext).startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST1);
                    releaseCamera();
                }
                dialog.dismiss();
            }
        });

        Dialog d = dialogBlankField.create();
        d.show();
    }

    //************** Take picture for drop off or return to DHL ***********//
    private void takePicture(String alertMsg) {
        Dialog enterFieldDialog = new Dialog(currentBarcodeContext);
        enterFieldDialog.setCancelable(true);
        enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        enterFieldDialog.setContentView(R.layout.dialogview_take_photo);

        Window window = enterFieldDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        TextView dialogMessageText = enterFieldDialog.findViewById(R.id.dialogMessageText);
        Button take_photoBtn = enterFieldDialog.findViewById(R.id.take_photoBtn);

        dialogMessageText.setText(alertMsg);

        take_photoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPickingUpByAWB == 2){
                    Intent i = new Intent(currentBarcodeContext, CameraOverlay_Activity.class);
                    if (isATLProvided && bookingStatus.equals("Tried to deliver"))
                        i.putExtra("ScreenCount", 3);
                    else if (!isATLProvided && bookingStatus.equals("Tried to deliver"))
                        i.putExtra("ScreenCount", 2);
                    else
                        i.putExtra("ScreenCount", 1);
                    ((Activity)currentBarcodeContext).startActivityForResult(i, CAMERA_PIC_REQUEST_OVERLAY);
                    releaseCamera();
                    i = null;
                }else
                    selectImage();

                enterFieldDialog.cancel();
            }
        });


        enterFieldDialog.show();
    }

    private void selectImage() {
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(currentBarcodeContext, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(currentBarcodeContext, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(currentBarcodeContext, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(currentBarcodeContext);

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
                            ActivityCompat.requestPermissions((Activity) currentBarcodeContext,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();
                }else
                    openCamera();
            }else
                openCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openCamera(){
        try {
            releaseCamera();
            PushReceiver.isCameraOpen = true;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(currentBarcodeContext.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                    LoginZoomToU.isImgFromInternalStorage = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LoginZoomToU.isImgFromInternalStorage = true;
                    Toast.makeText(currentBarcodeContext, "Image file at internal",Toast.LENGTH_LONG).show();
                }
                if (LoginZoomToU.isImgFromInternalStorage) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyContentProviderAtLocal.CONTENT_URI);
                    ((Activity) currentBarcodeContext).startActivityForResult(takePictureIntent, BarcodeScanner.TAKE_PHOTO_INTENT);
                }else{
                    //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    Uri photoURI = FileProvider.getUriForFile(currentBarcodeContext,
                            currentBarcodeContext.getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    ((Activity) currentBarcodeContext).startActivityForResult(takePictureIntent, BarcodeScanner.TAKE_PHOTO_INTENT);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(currentBarcodeContext, "Error while opening camera",Toast.LENGTH_LONG).show();
        }
    }

    //************** Get bitmap after taking picture from camera ***********//
    public void getImageFromCamera(){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        if (LoginZoomToU.isImgFromInternalStorage){
            File out = new File(currentBarcodeContext.getFilesDir(), "packageImage.png");
            if(!out.exists())
                Toast.makeText(currentBarcodeContext, "Error while capturing image", Toast.LENGTH_LONG) .show();
            else {
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;
                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;
                bmOptions.inPurgeable = true;
                Functional_Utility.mCurrentPhotoPath = out.getAbsolutePath();
                ActiveBookingView.photo = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
            //    ActiveBookingView.photo = Functional_Utility.getRotatedCameraImg(Functional_Utility.mCurrentPhotoPath, ActiveBookingView.photo);
                sendBitmapToServerForDropOrReturn();
            }
        }else {
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            ActiveBookingView.photo = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
        //    ActiveBookingView.photo = Functional_Utility.getRotatedCameraImg(Functional_Utility.mCurrentPhotoPath, ActiveBookingView.photo);
            sendBitmapToServerForDropOrReturn();
        }
    }

    public void sendBitmapToServerForDropOrReturn() {
        if (isPickingUpByAWB == 2) {
            scannedPieceIDArray = tempScannedPieceIDArray;
            ActiveBookingDetail_New.isPickBtn = true;
            callConfirmPIckDropBookingView();
        }else if (isPickingUpByAWB == 3) {
            if (scannedPieceIDArray == null)
                scannedPieceIDArray = new HashSet<String>();
            for (String tempScannedPieceItem : tempScannedPieceIDArray)
                    scannedPieceIDArray.add(tempScannedPieceItem);
            tempScannedPieceIDArray.clear();
            try {
                ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf(bookingID);
                ActiveBookingDetail_New.isPickBtn = true;
                Intent bgUploadImage = new Intent(currentBarcodeContext, BG_ImageUploadToServer.class);
                bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                bgUploadImage.putExtra("isActionBarPickup", false);
                bgUploadImage.putExtra("isDropOffFromATL", false);
                bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
                currentBarcodeContext.startService(bgUploadImage);
                ActiveBookingDetail_New.bookingIdActiveBooking = "";
                bgUploadImage = null;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (progressForDeliveryHistory == null)
                progressForDeliveryHistory = new ProgressDialog(currentBarcodeContext);
            Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
            ActiveBookingView.arrayOfBookingId.add(bookingID);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    ((Activity) currentBarcodeContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                            manualScanDialog(false, "Do you have more parcels to scan and return?");
                        }
                    });
                }
            }, 3000);
        }
    }

    private void callConfirmPIckDropBookingView() {
        PushReceiver.isCameraOpen = true;
        Intent i = new Intent(currentBarcodeContext, DropOff_From_BarcodeScannerView.class);
        i.putExtra("dropPersonName", dropContactNameBarcodeResponse);
        i.putExtra("bookingID", bookingID);
        i.putExtra("dropAddress", dropAddressBarcodeResponse);
        i.putExtra("IsReturnedToDHL", isReturnedToDHL);
        i.putExtra("dropDateTime", dropDateTimeBarcodeResponse);
        i.putExtra("isATLBooking", isATLProvided);
        i.putExtra("bookingStatus", bookingStatus);
        i.putExtra("firstDropAttemptWasLate", firstDropAttemptWasLate);
        i.putExtra("lateReasonId", lateReasonId);
        ((Activity)currentBarcodeContext).startActivity(i);
        i = null;
        pieceIdWithPickedUpStatus = null;
        ((Activity)currentBarcodeContext).finish();
        ((Activity)currentBarcodeContext).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    void updateIntentForCameraFacing( Intent cameraIntent,  Boolean frontFacing){
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            if(frontFacing)
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK);
            else
                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);

        }
        else if(frontFacing){
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_BACK);
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);

            //samsung
            cameraIntent.putExtra("camerafacing", "front");
            cameraIntent.putExtra("previous_mode", "front");
        }
        else{
            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);
            cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", false);

            //samsung
            cameraIntent.putExtra("camerafacing", "rear");
            cameraIntent.putExtra("previous_mode", "rear");
        }
    }

    /*click selfie*/
    private void openCameraForSelfie()
    {
        try{
            PushReceiver.isCameraOpen = true;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (Functional_Utility.checkCameraFront(currentBarcodeContext))
            {
                updateIntentForCameraFacing(takePictureIntent,true);
            }
            if (takePictureIntent.resolveActivity(currentBarcodeContext.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(currentBarcodeContext,
                            currentBarcodeContext.getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    ((BarcodeScanner)currentBarcodeContext).startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //*********** Server call for booking process ************//
    public void callAsyncTaskForRequest(){
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            PostBarcodeForPick_Drop_OR_Return();
            //new PostBarcodeForPick_Drop_OR_Return().execute();
        else {
            message = "No network connection, Please try again later.";
            dialogForBarcode("No network!", 0, 0);
        }
    }

    private void PostBarcodeForPick_Drop_OR_Return(){

        final String[] webPostBarcode = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressForDeliveryHistory == null)
                        progressForDeliveryHistory = new ProgressDialog(currentBarcodeContext);
                    Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
                    ActiveBookingView.getCurrentLocation(currentBarcodeContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webserviceHandler = new WebserviceHandler();
                    webPostBarcode[0] = webserviceHandler.pickUpPackageByBarcode(scanResult);
                    webserviceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    webPostBarcode[0] = "";
                }
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
                try {
                    if(dialogManualScan != null)
                        if (dialogManualScan.isShowing())
                            dialogManualScan.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            ActiveBookingDetail_New.IS_BARCODE_FROM_ACTIVE_DETAIL = 1228;
                            JSONObject jObjResponseOfPickUpByBarcode = new JSONObject(webPostBarcode[0]);
                            if (jObjResponseOfPickUpByBarcode.getBoolean("isPickupDeliveryComplete")) {
                                dialogForBarcode("Picked up!", 0, 0);
                                int runPickedUpProcessCount = jObjResponseOfPickUpByBarcode.getInt("runPickupProgress");
                                if (runPickedUpProcessCount < 1)
                                    runPickedUpProcessCount = 1;
                                int runTotalDeliveryCount = jObjResponseOfPickUpByBarcode.getInt("runTotalDeliveryCount");
                                if (runTotalDeliveryCount < 1)
                                    runTotalDeliveryCount = 1;

                                message = "\nITEM : "+jObjResponseOfPickUpByBarcode.getString("orderNumber")+
                                        "\nAddress: "+jObjResponseOfPickUpByBarcode.getString("address")+
                                        "\nRoute: "+runPickedUpProcessCount+ " of "+runTotalDeliveryCount+
                                        " parcels\nRemaining parcels to be picked up: "+jObjResponseOfPickUpByBarcode.getInt("runRemainingPickupCount");
                            } else {
                                int remainingPieceCount = 0;
                                if (pieceIdWithScannedDelivery != null)
                                    if (pieceIdWithScannedDelivery.size() > 0)
                                        pieceIdWithScannedDelivery.clear();
                                if (jObjResponseOfPickUpByBarcode.getJSONArray("pieces").length() > 0) {
                                    for (int i = 0; i < jObjResponseOfPickUpByBarcode.getJSONArray("pieces").length(); i++) {
                                        if (jObjResponseOfPickUpByBarcode.getJSONArray("pieces").getJSONObject(i).getString("status").equals("Picked up"))
                                            remainingPieceCount++;
                                        pieceIdWithScannedDelivery.add(jObjResponseOfPickUpByBarcode.getJSONArray("pieces").getJSONObject(i).getString("barcode"));
                                    }
                                }

                                int runPickedUpProcessCount = jObjResponseOfPickUpByBarcode.getInt("runPickupProgress");
                                if (runPickedUpProcessCount <= 1)
                                    runPickedUpProcessCount = 1;
                                int runTotalDeliveryCount = jObjResponseOfPickUpByBarcode.getInt("runTotalDeliveryCount");
                                if (runTotalDeliveryCount <= 1)
                                    runTotalDeliveryCount = 1;

                                message = "AWB : "+jObjResponseOfPickUpByBarcode.getString("orderNumber")+
                                        "\nPiece: "+scanResult+"\n This booking has "+pieceIdWithScannedDelivery.size()+" pieces"+
                                        "\nPiece scanned: "+remainingPieceCount+"/"+pieceIdWithScannedDelivery.size()+
                                        "\n\nAddress: "+jObjResponseOfPickUpByBarcode.getString("address")+
                                        "\nRoute: "+runPickedUpProcessCount+" of "+runTotalDeliveryCount+
                                        " parcels\nRemaining parcels to be picked up: "+jObjResponseOfPickUpByBarcode.getInt("runRemainingPickupCount");
                            }

                            dialogForBarcode("Picked up!", 0, 0);
                            //*********** Sent battery info to admin on firebase ***************
                            if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                                new Service_CheckBatteryLevel(currentBarcodeContext);
                            break;
                        case 1:
                            setErrorMsgAlert("No network!", "No network connection, Please check your connection and try again");
                            break;
                        case 2:
                            setErrorMsgAlert("Alert!", "AWB / piece ID "+scanResult+" does not exist. Please contact Zoom2u Support.");
                            break;
                        case 3:
                            if (pieceIdWithScannedDelivery.size() == 1) {
                                message = new JSONObject(webPostBarcode[0]).getJSONArray("errors").getJSONObject(0).getString("message");
                                scanResult = null;
                                dialogForBarcode("Alert!", 0, 0);
                            } else {
                                setErrorMsgFromResponseAlert(LoginZoomToU.isLoginSuccess, null, "AWB: " + scanResult + " \n\nThis booking has "
                                        + pieceIdWithScannedDelivery.size() + " pieces.\n\nPlease scan each of the piece IDs to mark them as Picked Up." +
                                        "\n\nA piece ID will usually start with (J) and be approx 20 characters.");
                            }
                            break;
                        case 4:
                            setErrorMsgAlert("Server Error!", "Something went wrong, Please try later.");
                            break;
                        default:
                            setErrorMsgFromResponseAlert(LoginZoomToU.isLoginSuccess, new JSONObject(webPostBarcode[0]), "");
                            break;
                    }
                    LoginZoomToU.isLoginSuccess = 0;
                    scanResult=null;
                }catch (Exception e) {
                    e.printStackTrace();
                    setErrorMsgAlert("Server Error!", "Something went wrong, Please try later.");
                }
            }
        }.execute();

    }

    //*********** Set error message from response ************
    private void setErrorMsgFromResponseAlert(int isLoginSuccess, JSONObject jsonObject, String msgStr) {
        try {
            if (isLoginSuccess == 3) {
                message = msgStr;
                dialogForBarcode("Not Picked up!", 1, 0);
            } else {
                message = jsonObject.getString("message");
                dialogForBarcode("Alert!", 0, 0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            message = "Something went wrong, Please try later.";
            dialogForBarcode("Server Error!", 0, 0);
        }
        scanResult = null;
    }


    //*********** Set error message from response ************
    private void setErrorMsgAlert(String titleStr, String msgStr) {
        message = msgStr;
        dialogForBarcode(titleStr, 0, 0);
        scanResult = null;
    }

    public interface CallBackAfterAgree{
        void onSuccess();
        void onFailure();
    }

    private void SaveVehicleAgreementAsync(CallBackAfterAgree callBackAfterAgree){
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                showDialogForImageUpload();
            }

            @Override
            public void doInBackground() {
                WebserviceHandler webServiceHandler = new WebserviceHandler();
                saveVehicleAgreementRes= webServiceHandler.saveVehicleAgreementForDhl(bookingID);
            }

            @Override
            public void onPostExecute() {
                dismissDialog();
                try {
                    JsonObject jsonObject=new JsonParser().parse(saveVehicleAgreementRes).getAsJsonObject();
                    boolean success = jsonObject.get("success").getAsBoolean();
                    if (success)callBackAfterAgree.onSuccess();
                } catch (Exception e) {
                    e.printStackTrace();
                    callBackAfterAgree.onFailure();
                }
            }
        }.execute();

    }


}
