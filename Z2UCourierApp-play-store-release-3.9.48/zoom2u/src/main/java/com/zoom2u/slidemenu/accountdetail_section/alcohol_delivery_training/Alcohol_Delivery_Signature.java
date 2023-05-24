package com.zoom2u.slidemenu.accountdetail_section.alcohol_delivery_training;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.slidemenu.AccountDetailFragment;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Calendar;

public class Alcohol_Delivery_Signature extends Activity implements View.OnClickListener {

    TextView alcoholSignatureViewTitleTxt, termTxtAlcohol_SignatureView, clearTxtSignatureAlcohol, signatureHereTxtAlcohol_SignatureView;

    CheckBox checkBoxAlcohol_SignatureView;

    LinearLayout signatureLayoutAlcohol_SignatureView;

    Button acceptBtnAlcohol_SignatureView, rejectBtnAlcohol_SignatureView;

    RelativeLayout clearBtnAlcohol_SignatureView;

    Signature mSignature;
    public static String tempDir;
    public String current = null;
    public static Bitmap mBitmap_AlcoholSignature;
    View mView;
    File mypath;
    String uniqueId;

    boolean isSignatureViewTouched = false;
    ProgressDialog progressToUploadSignature;
    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        AccountDetailFragment.OPEN_SETTING_VIEW = 4;
        finish();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signature_view_alcohole_delivery);

        alcoholSignatureViewTitleTxt = (TextView) findViewById(R.id.alcoholSignatureViewTitleTxt);
        alcoholSignatureViewTitleTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        termTxtAlcohol_SignatureView = (TextView) findViewById(R.id.termTxtAlcohol_SignatureView);
        termTxtAlcohol_SignatureView.setTypeface(LoginZoomToU.NOVA_REGULAR);
        clearTxtSignatureAlcohol = (TextView) findViewById(R.id.clearTxtSignatureAlcohol);
        clearTxtSignatureAlcohol.setTypeface(LoginZoomToU.NOVA_BOLD);
        signatureHereTxtAlcohol_SignatureView = (TextView) findViewById(R.id.signatureHereTxtAlcohol_SignatureView);
        signatureHereTxtAlcohol_SignatureView.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

        checkBoxAlcohol_SignatureView = (CheckBox) findViewById(R.id.checkBoxAlcohol_SignatureView);

        signatureLayoutAlcohol_SignatureView = (LinearLayout) findViewById(R.id.signatureLayoutAlcohol_SignatureView);

        acceptBtnAlcohol_SignatureView = (Button) findViewById(R.id.acceptBtnAlcohol_SignatureView);
        acceptBtnAlcohol_SignatureView.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        acceptBtnAlcohol_SignatureView.setOnClickListener(this);
        rejectBtnAlcohol_SignatureView = (Button) findViewById(R.id.rejectBtnAlcohol_SignatureView);
        rejectBtnAlcohol_SignatureView.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        rejectBtnAlcohol_SignatureView.setOnClickListener(this);

        clearBtnAlcohol_SignatureView = (RelativeLayout) findViewById(R.id.clearBtnAlcohol_SignatureView);

        //********* Create temp file to save signature image *************
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            // make temporary directory for save image to sdcard
            File directory;
            if (LoginZoomToU.isImgFromInternalStorage) {
                directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                File mypath=new File(directory,"alcoholSignatureImg.png");
                tempDir = mypath.getAbsolutePath();
            }else {
                tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
                if (!prepareDirectory()) {                // create directory
                    directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                    File mypath=new File(directory,"alcoholSignatureImg.png");
                    tempDir = mypath.getAbsolutePath();
                    prepareDirectory();
                }else
                    directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);
            }

            uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();		// Set image name with uniqueID
            current = uniqueId + ".png";
            mypath= new File(directory,current);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //********* Create temp file to save signature image *************

        mSignature = new Signature(Alcohol_Delivery_Signature.this, null);
        signatureLayoutAlcohol_SignatureView.addView(mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mView = signatureLayoutAlcohol_SignatureView;

        // erase signature
        clearBtnAlcohol_SignatureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mSignature.clear();
                isSignatureViewTouched = false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clearBtnAlcohol_SignatureView:
                break;
            case R.id.acceptBtnAlcohol_SignatureView:
                captureSignatureToSentItToServer();
                break;
            case R.id.rejectBtnAlcohol_SignatureView:
                AccountDetailFragment.OPEN_SETTING_VIEW = 4;
                finish();
                break;
        }
    }

    //*********** Capture signature image and sent it to server ***************//
    private void captureSignatureToSentItToServer() {
        boolean error = captureSignature();
        if(!error){
            mView.setBackgroundColor(Color.WHITE);
            mView.setDrawingCacheEnabled(true);
            mSignature.save(mView);
        }
    }

    private boolean captureSignature() {
        boolean error = false;
        try {
            String errorMessage = "";
            if(!checkBoxAlcohol_SignatureView.isChecked() || !isSignatureViewTouched){
                if (!checkBoxAlcohol_SignatureView.isChecked() && !isSignatureViewTouched)
                    errorMessage = errorMessage + "Please sign your name to acknowledge and agree to terms";
                else if (!checkBoxAlcohol_SignatureView.isChecked())
                    errorMessage = errorMessage + "Please agree to terms";
                else
                    errorMessage = errorMessage + "Please sign your name to acknowledge";

                error = true;
                DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "Error!", errorMessage);
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

    private boolean makedirs() {
        File tempdir = new File(tempDir);
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
    public class Signature extends View  {
        private static final float STROKE_WIDTH = 5.0f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();

        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        private void saveSignatureBitmap (View v){
            Canvas canvas = new Canvas(mBitmap_AlcoholSignature);
            v.draw(canvas);

            mBitmap_AlcoholSignature = BG_ImageUploadToServer.getPkgAndSignImgToAspectRatio(mBitmap_AlcoholSignature, 600.0f, 600.0f);

            if (mBitmap_AlcoholSignature == null) {
                mSignature.clear();
                isSignatureViewTouched = false;
                ConfirmPickUpForUserName.showValidationDialog("Something went wrong when we try to get signature, Please try again.", Alcohol_Delivery_Signature.this);
            } else {
                if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                    UploadAlcoholSignatureImg();
                    //new UploadAlcoholSignatureImg().execute();
                } else
                    DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "No Network!", "No Network connection, Please try again later.");
            }
        }

        public void save(View v) {
            try {
                if (mBitmap_AlcoholSignature != null)
                    mBitmap_AlcoholSignature = null;

                mBitmap_AlcoholSignature = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);

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
                float eventX = event.getX();
                float eventY = event.getY();

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                        path.moveTo(eventX, eventY);
                        lastTouchX = eventX;
                        lastTouchY = eventY;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                    case MotionEvent.ACTION_UP:

                        isSignatureViewTouched = true;

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

        private void UploadAlcoholSignatureImg(){
            final String[] uploadSignatureImageResponseStr = {"0"};
            new MyAsyncTasks(){
                @Override
                public void onPreExecute() {
                    if(progressToUploadSignature == null)
                        progressToUploadSignature = new ProgressDialog(Alcohol_Delivery_Signature.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressToUploadSignature);
                }

                @Override
                public void doInBackground() {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    uploadSignatureImageResponseStr[0] = webServiceHandler.uploadAlcoholSignatureImage(mBitmap_AlcoholSignature, "AlcoholTraining_Signature.png");
                }

                @Override
                public void onPostExecute() {
                    if (!uploadSignatureImageResponseStr[0].equals("0")) {
                        JSONObject jObjOfResponse;
                        try {
                            jObjOfResponse = new JSONObject(uploadSignatureImageResponseStr[0]);
                            if (jObjOfResponse.has("Message"))
                                DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "Error!", jObjOfResponse.getString("Message"));
                            else if (jObjOfResponse.getBoolean("success")) {
                                setResult(Activity.RESULT_OK);
                                finish();
                                Toast.makeText(Alcohol_Delivery_Signature.this, "Signature uploaded", Toast.LENGTH_LONG).show();
                            } else
                                DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "Error!", "Failed to upload image, Please try again");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "Sorry!", "Something went wrong here, Please try again");
                        }
                    } else
                        DialogActivity.alertDialogView(Alcohol_Delivery_Signature.this, "Server error!", "Failed to upload image, Please try again");

                    if(progressToUploadSignature != null)
                        if(progressToUploadSignature.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressToUploadSignature);
                }
            }.execute();

        }


    }

}
