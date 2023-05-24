package com.zoom2u.slidemenu.barcode_reader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogReasonForLateDelivery;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.utility.signaturefail.DBHelperForSignatureFailure;
import com.zoom2u.utility.signaturefail.SignatureImg_DB_Model;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class DropOff_From_BarcodeScannerView extends Activity implements AdapterView.OnItemSelectedListener {

    TextView titleConfirmPickName, countChatConfirmPicUp, userNameConfirmPickTxt, lastNameTxtSignature, contactTxt, confirmPickContactName, confirmPickContactAddress;
    EditText edtConfirmPickUserName, confirmPickUserLastNameTxt;
    ImageView backFromConfirmPick, confirmPicUpChatIcon;
    Spinner spinnerForPkgPosition;

    TextView pickupnameConfirmPickSignature;
    ImageView clearSignature;
    Button doneBtnConfirmPickSignature;

    RelativeLayout headerViewToShowContactInfo;

    //*********** Header view for Return to DHL bookings only
    RelativeLayout returnDHLBookingHeader;
    //*********** Header view for Return to DHL bookings only

    LinearLayout mContent;
    RelativeLayout belowSignature;
    Signature mSignature;
    public String current = null;

    View mView;
    File mypath;

    ProgressDialog progressDialogForBarcodeDrop;

    int bookingIDFromScannerView, lateReasonId;

    int signeePosition;

    boolean isReturnedToDHL = false;
    private String uniqueId;
    private String userName, dropPersonName, dropDateTime, firstDropAttemptWasLate;
    private Dialog alertDialog;
    Window window;
    TextView totalDeliveredCountTxt, pickedUpCountTxt, toReturnCountTxt;
    RelativeLayout linearLayout1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmpickup_for_username);

            Intent userNameIntent = getIntent();
            ConfirmPickUpForUserName.isSignatureImageFromDB = false;
            linearLayout1=findViewById(R.id.linearLayout1);
            window = DropOff_From_BarcodeScannerView.this.getWindow();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
            if(MainActivity.isIsBackGroundGray()){
                linearLayout1.setBackgroundResource(R.color.base_color_gray);
                window.setStatusBarColor(Color.parseColor("#374350"));
            }else{
                linearLayout1.setBackgroundResource(R.color.base_color1);
                window.setStatusBarColor(Color.parseColor("#00A6E2"));
            }
        try {
            dropDateTime = userNameIntent.getStringExtra("dropDateTime");
            bookingIDFromScannerView = userNameIntent.getIntExtra("bookingID", 0);
            dropPersonName = userNameIntent.getStringExtra("dropPersonName");
            firstDropAttemptWasLate = userNameIntent.getStringExtra("firstDropAttemptWasLate");
            lateReasonId = userNameIntent.getIntExtra("lateReasonId", 0);
            ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf(bookingIDFromScannerView);
            isReturnedToDHL = userNameIntent.getBooleanExtra("IsReturnedToDHL", false);

        } catch (Exception e) {
            e.printStackTrace();
        }

            if(titleConfirmPickName == null)
                titleConfirmPickName = (TextView) findViewById(R.id.titleConfirmPicUpUserName);

            if(countChatConfirmPicUp == null)
                countChatConfirmPicUp = (TextView) findViewById(R.id.countChatConfirmPicUp);

            countChatConfirmPicUp.setVisibility(View.GONE);
            SlideMenuZoom2u.countChatBookingView = countChatConfirmPicUp;

            if(confirmPicUpChatIcon == null)
                confirmPicUpChatIcon = (ImageView) findViewById(R.id.confirmPicUpChatIcon);
            confirmPicUpChatIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent chatViewIntent = new Intent(DropOff_From_BarcodeScannerView.this, ChatViewBookingScreen.class);
                    startActivity(chatViewIntent);
                    chatViewIntent = null;
                }
            });

            if(contactTxt == null)
                contactTxt = (TextView) findViewById(R.id.contactTxt);

            if(confirmPickContactName == null)
                confirmPickContactName = (TextView) findViewById(R.id.confirmPickContactName);

            if(confirmPickContactAddress == null)
                confirmPickContactAddress = (TextView) findViewById(R.id.confirmPickContactAddress);

            if(lastNameTxtSignature == null)
                lastNameTxtSignature = (TextView) findViewById(R.id.lastNameTxtSignature);

            if(confirmPickUserLastNameTxt == null)
                confirmPickUserLastNameTxt = (EditText) findViewById(R.id.confirmPickUserLastNameTxt);

            if(edtConfirmPickUserName == null)
                edtConfirmPickUserName = (EditText) findViewById(R.id.edtConfirmPickUserName);

            if(userNameConfirmPickTxt == null)
                userNameConfirmPickTxt = (TextView) findViewById(R.id.confirmPickUserNameTxt);


            if(backFromConfirmPick == null)
                backFromConfirmPick = (ImageView) findViewById(R.id.backFromConfirmPicUpUserName);
            if(belowSignature == null)
                belowSignature = (RelativeLayout) findViewById(R.id.belowSignature);

            if(mContent == null)
                mContent = (LinearLayout) findViewById(R.id.signatureLayout);

            if(pickupnameConfirmPickSignature == null)
                pickupnameConfirmPickSignature = (TextView) findViewById(R.id.confirmPickUserName);

            headerViewToShowContactInfo = (RelativeLayout) findViewById(R.id.linearLayout2);

            returnDHLBookingHeader = (RelativeLayout) findViewById(R.id.returnDHLBookingHeader);

            if (isReturnedToDHL) {
                titleConfirmPickName.setText("Return to depot");
                returnDHLBookingHeader.setVisibility(View.VISIBLE);
                headerViewToShowContactInfo.setVisibility(View.GONE);

                TextView returnBookingHeaderTxt = (TextView) findViewById(R.id.returnBookingHeaderTxt);

                TextView totalDeliveredTxtHeader = (TextView) findViewById(R.id.totalDeliveredTxtHeader);

                totalDeliveredCountTxt = (TextView) findViewById(R.id.totalDeliveredCountTxt);

                TextView pickedUpDeliveryHeaderTxt = (TextView) findViewById(R.id.pickedUpDeliveryHeaderTxt);

                pickedUpCountTxt = (TextView) findViewById(R.id.pickedUpCountTxt);


                TextView toReturnHeaderTxt = (TextView) findViewById(R.id.toReturnHeaderTxt);

                toReturnCountTxt = (TextView) findViewById(R.id.toReturnCountTxt);


                TextView defaultTxtForLineConfirmDrp = (TextView) findViewById(R.id.defaultTxtForLineConfirmDrp);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
                param.topMargin = 7;
                param.addRule(RelativeLayout.BELOW, R.id.returnDHLBookingHeader);
                defaultTxtForLineConfirmDrp.setLayoutParams(param);

                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    GetTotalDropAndToReturnCount();
                    //new GetTotalDropAndToReturnCount().execute();
                else
                    DialogActivity.alertDialogView(DropOff_From_BarcodeScannerView.this, "No Network!", "No Network connection, Please check your internet connection.");
            } else
                titleConfirmPickName.setText("Confirm delivery");

            confirmPickContactName.setText(dropPersonName);
            confirmPickContactAddress.setText(userNameIntent.getStringExtra("dropAddress"));

            if(clearSignature == null)
                clearSignature = (ImageView) findViewById(R.id.clearSignature);
            clearSignature.setVisibility(View.GONE);
            if(doneBtnConfirmPickSignature == null)
                doneBtnConfirmPickSignature = findViewById(R.id.doneConfirmPickBtn);

            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // make temporary directory for save image to sdcard
            File directory;
            if (LoginZoomToU.isImgFromInternalStorage) {
                directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                File mypath=new File(directory,"signatureImg.jpg");
                ConfirmPickUpForUserName.tempDir = mypath.getAbsolutePath();
            }else {
                ConfirmPickUpForUserName.tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
                if (!prepareDirectory()) {                // create directory
                    directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                    File mypath=new File(directory,"signatureImg.jpg");
                    ConfirmPickUpForUserName.tempDir = mypath.getAbsolutePath();
                    prepareDirectory();
                }else
                    directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);
            }

            uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();		// Set image name with uniqueID
            current = uniqueId + ".png";
            mypath= new File(directory,current);

            mSignature = new Signature(DropOff_From_BarcodeScannerView.this, null);
            mContent.addView(mSignature, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            mView = mContent;

            backFromConfirmPick.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    PushReceiver.isCameraOpen = false;
                    Intent i;
                    i = new Intent(DropOff_From_BarcodeScannerView.this, BarcodeScanner.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.left_in, R.anim.right_out);
                    i = null;
                }
            });

            // erase signature
            clearSignature.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mSignature.clear();
                    clearSignature.setVisibility(View.GONE);
                }
            });

            doneBtnConfirmPickSignature.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    try {
                        userName = edtConfirmPickUserName.getText().toString()+" "+confirmPickUserLastNameTxt.getText().toString();
                        if (isReturnedToDHL){
                            if (edtConfirmPickUserName.getText().toString().equals(""))
                                showValidationDialog("Please fill first name");
                            else
                                checkSignatureValidation();
                        }else {
                            if (edtConfirmPickUserName.getText().toString().equals("") || confirmPickUserLastNameTxt.getText().toString().equals(""))
                                showValidationDialog("Please fill first and last name");
                            else
                                checkSignatureValidation();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                //********** Check signature validation before upload ***********
                private void checkSignatureValidation() {
                    int usernameLength = edtConfirmPickUserName.getText().toString().length()+confirmPickUserLastNameTxt.getText().toString().length();
                    if (usernameLength <= 2)
                        showValidationDialog("The signature needs to contain at least 1 initial and 1 full name.\n" +
                                "eg. 'John Smith', 'J Smith', or 'John S'");
                    else if (userName.equalsIgnoreCase(LoginZoomToU.courierName))
                        showValidationDialog("Please collect signature from the customer.\n" +
                                "If the customer is not available, contact Zoom2u");
                    else if (getIntent().getBooleanExtra("isATLBooking", false) == false) {
                        if (edtConfirmPickUserName.getText().toString().equalsIgnoreCase("atl") || confirmPickUserLastNameTxt.getText().toString().equalsIgnoreCase("atl"))
                            showValidationDialog("ATL has not been provided for the booking");
                        else
                            captureSignatureToSentItToServer();
                    }else
                        captureSignatureToSentItToServer();
                }
            });

            spinnerForPkgPosition = (Spinner) findViewById(R.id.spinnerForPkgPosition);
            ArrayList<String> pkgPositionArray = new ArrayList<String>();
            pkgPositionArray.add("Recipient");
            pkgPositionArray.add("Family / Friend");
            pkgPositionArray.add("BM / Concierge");
            pkgPositionArray.add("Reception");
            pkgPositionArray.add("Colleague");
            pkgPositionArray.add("Mailroom");
            pkgPositionArray.add("Other");
            if (!isReturnedToDHL)
                pkgPositionArray.add("ATL on item");

            ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(this, R.layout.spinneritemxml_new, pkgPositionArray) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTextColor(Color.parseColor("#808080"));
                    return v;
                }
                public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
                    View v =super.getDropDownView(position, convertView, parent);

                    ((TextView) v).setTextColor(Color.parseColor("#808080"));
                    return v;
                }
            };

            spinnerForPkgPosition.setAdapter(adapter_Position);
            pkgPositionArray = null;
            adapter_Position = null;
            spinnerForPkgPosition.setOnItemSelectedListener(this);



        ActiveBookingView.getCurrentLocation(DropOff_From_BarcodeScannerView.this);
    }

    //*********** Capture signature image and sent it to server ***************//
    private void captureSignatureToSentItToServer() {
        boolean error = captureSignature();
        if(!error){
            LoginZoomToU.imm.hideSoftInputFromWindow(edtConfirmPickUserName.getWindowToken(), 0);
            //belowSignature.setBackgroundColor(Color.WHITE);
            mView.setBackgroundColor(Color.WHITE);
            mView.setDrawingCacheEnabled(true);
            mSignature.save(mView);

            if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                ActiveBookingDetail_New.IS_BARCODE_FROM_ACTIVE_DETAIL = 1228;
                RequestForDropOrReturnAsyncTask();
                //new RequestForDropOrReturnAsyncTask().execute();
            }else
                DialogActivity.alertDialogView(DropOff_From_BarcodeScannerView.this, "No Network !", "No Network connection, Please try again later.");
        }
    }

    void showValidationDialog(String validateStr){
        AlertDialog.Builder dialogBlankField = new AlertDialog.Builder(DropOff_From_BarcodeScannerView.this);
        TextView myMsg = new TextView(DropOff_From_BarcodeScannerView.this);
        myMsg.setText(validateStr);
        myMsg.setTextColor(Color.parseColor("#00A7E2"));

        myMsg.setPadding(0, 20, 0, 10);
        myMsg.setTextSize(15.0f);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogBlankField.setView(myMsg);
        dialogBlankField.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog d = dialogBlankField.create();
        d.show();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
        signeePosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatConfirmPicUp);
        SlideMenuZoom2u.countChatBookingView = countChatConfirmPicUp;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        PushReceiver.isCameraOpen = false;
        switchToBarcodeScanerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.confirm_pick_up_for_user_name, menu);
        return true;
    }

    private boolean captureSignature() {
        boolean error = false;
        try {
            String errorMessage = "";
            if(edtConfirmPickUserName.getText().toString().equalsIgnoreCase("")){
                errorMessage = errorMessage + "Please enter your Name\n";
                error = true;
            }
            if(error){
                Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 105, 50);
                toast.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return error;
    }

    private String getTodaysDate() {
        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
                ((c.get(Calendar.MONTH) + 1) * 100) +
                (c.get(Calendar.DAY_OF_MONTH));
        return(String.valueOf(todaysDate));
    }

    private String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
                (c.get(Calendar.MINUTE) * 100) +
                (c.get(Calendar.SECOND));
        return(String.valueOf(currentTime));
    }

    private boolean prepareDirectory(){
        try {
            if (makedirs())
                return true;
            else
                return false;
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean makedirs()
    {
        File tempdir = new File(ConfirmPickUpForUserName.tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();
        if (tempdir.isDirectory()){
            File[] files = tempdir.listFiles();
            for (File file : files){
                if (!file.delete())
                    System.out.println("Failed to delete " + file);
            }
        }
        return (tempdir.isDirectory());
    }

    //************* Insert signature image to DB ***********
    private void insertSignatureImageDetailToDB() {
        DBHelperForSignatureFailure dbHelperForSignatureFailure = new DBHelperForSignatureFailure(this);
        String bookingIdStr = "";
        if (isReturnedToDHL) {
            try {
                ActiveBookingView.photo = null;
                if (ActiveBookingView.arrayOfBookingId.size() > 1) {
                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                    Iterator<Integer> itr = ActiveBookingView.arrayOfBookingId.iterator();
                    while (itr.hasNext()) {
                        ActiveBookingDetail_New.bookingIdActiveBooking = ActiveBookingDetail_New.bookingIdActiveBooking + itr.next() + ",";
                    }
                    if (null != ActiveBookingDetail_New.bookingIdActiveBooking && ActiveBookingDetail_New.bookingIdActiveBooking.length() > 0 ) {
                        int endIndex = ActiveBookingDetail_New.bookingIdActiveBooking.lastIndexOf(",");
                        if (endIndex != -1)
                            bookingIdStr = ActiveBookingDetail_New.bookingIdActiveBooking.substring(0, endIndex) + "_DS";; // not forgot to put check if(endIndex != -1)
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (ActiveBookingDetail_New.isPickBtn)
            bookingIdStr = bookingIDFromScannerView + "_DS";
        else
            bookingIdStr = bookingIDFromScannerView + "_PS";

        ConfirmPickUpForUserName.mBitmap = BG_ImageUploadToServer.getPkgAndSignImgToAspectRatio(ConfirmPickUpForUserName.mBitmap, 600.0f, 600.0f);

        SignatureImg_DB_Model signatureImg_db_model = new SignatureImg_DB_Model(bookingIdStr, ConfirmPickUpForUserName.mBitmap);
        dbHelperForSignatureFailure.open();
        dbHelperForSignatureFailure.insertSignatureDetails(signatureImg_db_model);

        ConfirmPickUpForUserName.isSignatureImageFromDB = true;
        dbHelperForSignatureFailure.close();
        signatureImg_db_model = null;
    }

    public class Signature extends View
    {
        private static final float STROKE_WIDTH = 3.0f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        private void saveSignatureBitmap (View v){
            Canvas canvas = new Canvas(ConfirmPickUpForUserName.mBitmap);
            v.draw(canvas);

//			String FtoSave = tempDir + current;
//			File file = new File(FtoSave);
            try {
//				if (file.isFile() && file.exists()) {
//					FileOutputStream mFileOutStream = new FileOutputStream(file);
//					mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
//					mFileOutStream.flush();
//					mFileOutStream.close();
//				} else
                insertSignatureImageDetailToDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void save(View v) {
            try {
                if (ConfirmPickUpForUserName.mBitmap == null)
                    ConfirmPickUpForUserName.mBitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);

                saveSignatureBitmap(v);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        public void clear() {
            path.reset();
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawPath(path, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event){
            try {
                LoginZoomToU.imm.hideSoftInputFromWindow(edtConfirmPickUserName.getWindowToken(), 0);
                float eventX = event.getX();
                float eventY = event.getY();
                clearSignature.setVisibility(View.VISIBLE);

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        path.moveTo(eventX, eventY);
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                    case MotionEvent.ACTION_UP:

                        resetDirtyRect(eventX, eventY);
                        int historySize = event.getHistorySize();
                        for (int i = 0; i < historySize; i++)
                        {
                            float historicalX = event.getHistoricalX(i);
                            float historicalY = event.getHistoricalY(i);
                            expandDirtyRect(historicalX, historicalY);
                            path.lineTo(historicalX, historicalY);
                        }
                        path.lineTo(eventX, eventY);
                        break;

                    default:
                        return false;
                }

                invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

                lastTouchX = eventX;
                lastTouchY = eventY;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

        private void expandDirtyRect(float historicalX, float historicalY)
        {
            try {
                if (historicalX < dirtyRect.left)
                    dirtyRect.left = historicalX;
                else if (historicalX > dirtyRect.right)
                    dirtyRect.right = historicalX;

                if (historicalY < dirtyRect.top)
                    dirtyRect.top = historicalY;
                else if (historicalY > dirtyRect.bottom)
                    dirtyRect.bottom = historicalY;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void resetDirtyRect(float eventX, float eventY)
        {
            try {
                dirtyRect.left = Math.min(lastTouchX, eventX);
                dirtyRect.right = Math.max(lastTouchX, eventX);
                dirtyRect.top = Math.min(lastTouchY, eventY);
                dirtyRect.bottom = Math.max(lastTouchY, eventY);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void RequestForDropOrReturnAsyncTask(){

        final String[] requestDropResponseStr = {null};
        boolean responseDataRequestForDropOff = false;

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if(progressDialogForBarcodeDrop != null)
                        progressDialogForBarcodeDrop = null;
                    progressDialogForBarcodeDrop = new ProgressDialog(DropOff_From_BarcodeScannerView.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogForBarcodeDrop);
                    ActiveBookingView.getCurrentLocation(DropOff_From_BarcodeScannerView.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try{
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (isReturnedToDHL) {
                        Map<String, Object> mapObject = new HashMap<String, Object>();
                        mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
                        mapObject.put("recipientName", userName);
                        mapObject.put("pieceBarcodes", BarcodeController.scannedPieceIDArray);
                        mapObject.put("signeesPosition", signeePosition);
                        mapObject.put("notes", "Write some note here");
                        mapObject.put("forceIncompleteDropOff", false);
                        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                            mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
                            mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        String bookingsForReturnStr = objectMapper.writeValueAsString(mapObject);
                        requestDropResponseStr[0] = webServiceHandler.multipleReturnDeliveries(0, bookingsForReturnStr);
                    } else {
                        Map<String, Object> mapObject = new HashMap<String, Object>();
                        mapObject.put("bookingId", bookingIDFromScannerView);
                        mapObject.put("recipientName", userName);
                        mapObject.put("pieceBarcodes", BarcodeController.scannedPieceIDArray);
                        mapObject.put("notes", "Write some note here");
                        mapObject.put("signeesPosition", signeePosition);
                        mapObject.put("forceIncompleteDropOff", false);
                        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                            mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
                            mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        String requestDropoffParams = objectMapper.writeValueAsString(mapObject);;
                        requestDropResponseStr[0] = webServiceHandler.dropDeliveryUsingBarcode(requestDropoffParams);
                        if (LoginZoomToU.isLoginSuccess == 0) {
                            if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                                Intent bgUploadImage = new Intent(DropOff_From_BarcodeScannerView.this, BG_ImageUploadToServer.class);
                                bgUploadImage.putExtra("isActionBarPickup", false);
                                bgUploadImage.putExtra("isDropOffFromATL", false);
                                bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
                                bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                startService(bgUploadImage);
                                bgUploadImage = null;
                            }
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (isReturnedToDHL) {
                        new LoadChatBookingArray(DropOff_From_BarcodeScannerView.this, 0);
                        if (LoginZoomToU.isLoginSuccess == 0) {
                            try {
                                JSONObject jObjOfMultipleReturnDelivery = new JSONObject(requestDropResponseStr[0]);
                                if (jObjOfMultipleReturnDelivery.getJSONArray("validationErrors").length() <= 0) {
                                    Intent bgUploadImage = new Intent(DropOff_From_BarcodeScannerView.this, BG_ImageUploadToServer.class);
                                    bgUploadImage.putExtra("isActionBarPickup", false);
                                    bgUploadImage.putExtra("isDropOffFromATL", false);
                                    bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
                                    try {
                                        ActiveBookingView.photo = null;
                                        if (ActiveBookingView.arrayOfBookingId.size() > 1) {
                                            ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                            Iterator<Integer> itr = ActiveBookingView.arrayOfBookingId.iterator();
                                            while (itr.hasNext())
                                                ActiveBookingDetail_New.bookingIdActiveBooking = ActiveBookingDetail_New.bookingIdActiveBooking + itr.next() + ",";

                                            if (null != ActiveBookingDetail_New.bookingIdActiveBooking && ActiveBookingDetail_New.bookingIdActiveBooking.length() > 0 )
                                                ActiveBookingDetail_New.bookingIdActiveBooking = ActiveBookingDetail_New.bookingIdActiveBooking.substring(0, (ActiveBookingDetail_New.bookingIdActiveBooking.length()-1));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                    try {
                                        if (ActiveBookingDetail_New.bookingIdActiveBooking.contains(",")) {
                                            String[] bookingIDStrArray = ActiveBookingDetail_New.bookingIdActiveBooking.split(",");
                                            for (String bookingId : bookingIDStrArray)
                                                LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(Integer.parseInt(bookingId));
                                            bookingIDStrArray = null;
                                        }
                                    } catch (NumberFormatException e) {
                                        e.printStackTrace();
                                    }
                                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                    startService(bgUploadImage);
                                    bgUploadImage = null;

                                    Toast.makeText(DropOff_From_BarcodeScannerView.this, "Returned successfully", Toast.LENGTH_LONG).show();
                                    switchToBarcodeScanerView();
                                } else {
                                    String msgStr = "";
                                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                    Intent bgUploadImage = new Intent(DropOff_From_BarcodeScannerView.this, BG_ImageUploadToServer.class);
                                    bgUploadImage.putExtra("isActionBarPickup", false);
                                    bgUploadImage.putExtra("isDropOffFromATL", false);
                                    bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
                                    for (int i = 0; i < new JSONObject(requestDropResponseStr[0]).getJSONArray("validationErrors").length(); i++) {
                                        ActiveBookingDetail_New.bookingIdActiveBooking = ActiveBookingDetail_New.bookingIdActiveBooking + String.valueOf(new JSONObject(requestDropResponseStr[0]).getJSONArray("validationErrors").getJSONObject(i).getInt("bookingId"));
                                        msgStr = msgStr + "\nBooking ID"+new JSONObject(requestDropResponseStr[0]).getJSONArray("validationErrors").getJSONObject(i).getInt("bookingId")+
                                                " "+new JSONObject(requestDropResponseStr[0]).getJSONArray("validationErrors").getJSONObject(i).getJSONArray("validatonErrors").get(0);
                                        if ((i+1) < new JSONObject(requestDropResponseStr[0]).getJSONArray("validationErrors").length())
                                            ActiveBookingDetail_New.bookingIdActiveBooking = ActiveBookingDetail_New.bookingIdActiveBooking+", ";
                                    }
                                    bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                    startService(bgUploadImage);
                                    bgUploadImage = null;
                                    dialogForView("Validation Error!", msgStr);
                                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                }
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }
                    } else {
                        switch (LoginZoomToU.isLoginSuccess) {
                            case 0:
                                try {
                                    if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                                        new LoadChatBookingArray(DropOff_From_BarcodeScannerView.this, 0);
                                        LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(bookingIDFromScannerView);
                                        //*********** Sent battery info to admin on firebase ***************
                                        if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                                            new Service_CheckBatteryLevel(DropOff_From_BarcodeScannerView.this);
                                        //*********** Sent battery info to admin on firebase ***************
                                        switchToBarcodeScanerView();
                                        try {
                                            if (!firstDropAttemptWasLate.equals("") && !firstDropAttemptWasLate.equals("null")
                                                    && !firstDropAttemptWasLate.equals(null)) {
                                                if (LoginZoomToU.checkInternetwithfunctionality.checkIFirstAttemptWasLateFromBarcodeScanner(dropDateTime,
                                                        firstDropAttemptWasLate)
                                                        && lateReasonId == 0) {
                                                    openLateReasonPrompt();
                                                    SlideMenuZoom2u.isDropOffCompleted=true;
                                                }
                                            } else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLateFromBarcodeScanner(dropDateTime)
                                                    && lateReasonId == 0) {
                                                openLateReasonPrompt();
                                                SlideMenuZoom2u.isDropOffCompleted=true;
                                            }else
                                                SlideMenuZoom2u.isDropOffCompleted=true;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLateFromBarcodeScanner(dropDateTime)
                                                    && lateReasonId == 0) {
                                                openLateReasonPrompt();
                                            }
                                        }
                                    } else
                                        dialogForView("Error!", "Status not updated. Please try again.");
                                }catch (Exception e) {
                                    dialogForView("Error!", "Status not updated., Please try again.");
                                }
                                break;

                            case 1:
                                DialogActivity.alertDialogView(DropOff_From_BarcodeScannerView.this, "No network!", "No network connection, Please check your connection and try again");
                                break;
                            default:
                                dialogForView("Server Error!", "Something went wrong. Please try again.");
                                break;
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    dialogForView("Server Error!", "Something went wrong. Please try again.");
                    requestDropResponseStr[0] = null;
                }finally {
                    LoginZoomToU.isLoginSuccess = 0;
                    try {
                        if (progressDialogForBarcodeDrop != null)
                            if (progressDialogForBarcodeDrop.isShowing())
                                Custom_ProgressDialogBar.dismissProgressBar(progressDialogForBarcodeDrop);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();

    }



    private void openLateReasonPrompt() {
        switchToBarcodeScanerView();
        Intent callDialogReasonLate = new Intent(DropOff_From_BarcodeScannerView.this, DialogReasonForLateDelivery.class);
        callDialogReasonLate.putExtra("ReasonLateBookingID", bookingIDFromScannerView);
        callDialogReasonLate.putExtra("BookingTypeSource", "DHL");
        startActivity(callDialogReasonLate);
        callDialogReasonLate = null;
    }

    //************* Switch to Barcode scanner view ************
    void switchToBarcodeScanerView(){
        if (ActiveBookingView.arrayOfBookingId != null)
            ActiveBookingView.arrayOfBookingId.clear();

        PushReceiver.isCameraOpen = false;

        Intent bookingCountService = new Intent(DropOff_From_BarcodeScannerView.this, ServiceForCourierBookingCount.class);
        bookingCountService.putExtra("Is_API_Call_Require", 1);
        startService(bookingCountService);
        bookingCountService = null;

        Intent callCompleteBookingfragment = new Intent(DropOff_From_BarcodeScannerView.this, BarcodeScanner.class);
        startActivity(callCompleteBookingfragment);
        finish();
        callCompleteBookingfragment = null;
    }

    private void dialogForView(String titleStr, String msgStr){
        if(alertDialog != null)
            alertDialog = null;
        alertDialog = new Dialog(DropOff_From_BarcodeScannerView.this);
        alertDialog.setCancelable(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.setContentView(R.layout.dialogview);

        Window window = alertDialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView enterFieldDialogHEader = (TextView) alertDialog.findViewById(R.id.titleDialog);

        enterFieldDialogHEader.setText(titleStr);

        TextView enterFieldDialogMsg = (TextView) alertDialog.findViewById(R.id.dialogMessageText);

        enterFieldDialogMsg.setText(msgStr);

        Button enterFieldDialogDoneBtn = (Button) alertDialog.findViewById(R.id.dialogDoneBtn);
        enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                switchToBarcodeScanerView();
            }
        });

        alertDialog.show();
    }

    private void GetTotalDropAndToReturnCount(){

        final String[] getDHLTotalDropAndToReturnData = {""};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressDialogForBarcodeDrop != null)
                    progressDialogForBarcodeDrop = null;
                progressDialogForBarcodeDrop = new ProgressDialog(DropOff_From_BarcodeScannerView.this);
                Custom_ProgressDialogBar.inItProgressBar(progressDialogForBarcodeDrop);
                ActiveBookingView.getCurrentLocation(DropOff_From_BarcodeScannerView.this);
            }

            @Override
            public void doInBackground() {
                WebserviceHandler webServiceHandler = new WebserviceHandler();
                getDHLTotalDropAndToReturnData[0] = webServiceHandler.getToReturnOrTotalDropCount();
            }

            @Override
            public void onPostExecute() {
                try {
                    JSONObject jObjOfTotalDropOrToReturn = new JSONObject(getDHLTotalDropAndToReturnData[0]);
                    ConfirmPickUpForUserName.totalDroppedOff =  jObjOfTotalDropOrToReturn.getInt("totalDroppedOff");
                    ConfirmPickUpForUserName.totalPickedUp =  jObjOfTotalDropOrToReturn.getInt("totalPickedUp");
                    ConfirmPickUpForUserName.totalToReturn =  jObjOfTotalDropOrToReturn.getInt("totalToReturn");

                    if (ConfirmPickUpForUserName.totalDroppedOff < 0)
                        totalDeliveredCountTxt.setText("-");
                    else
                        totalDeliveredCountTxt.setText("" + ConfirmPickUpForUserName.totalDroppedOff);
                    if (ConfirmPickUpForUserName.totalPickedUp < 0)
                        pickedUpCountTxt.setText("-");
                    else
                        pickedUpCountTxt.setText("" + ConfirmPickUpForUserName.totalPickedUp);
                    if (ConfirmPickUpForUserName.totalToReturn < 0)
                        toReturnCountTxt.setText("-");
                    else
                        toReturnCountTxt.setText("" + ConfirmPickUpForUserName.totalToReturn);
                } catch (Exception e) {
                    e.printStackTrace();
                    ConfirmPickUpForUserName.totalDroppedOff = -1;
                    totalDeliveredCountTxt.setText("-");
                    ConfirmPickUpForUserName.totalPickedUp = -1;
                    pickedUpCountTxt.setText("-");
                    ConfirmPickUpForUserName.totalToReturn = -1;
                    toReturnCountTxt.setText("-");
                }

                try{
                    if(progressDialogForBarcodeDrop != null)
                        if(progressDialogForBarcodeDrop.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogForBarcodeDrop);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }


}
