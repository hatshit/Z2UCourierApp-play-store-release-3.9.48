package com.zoom2u.slidemenu.accountdetail_section;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;
import android.provider.OpenableColumns;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.AccountDetailFragment;
import com.zoom2u.utility.FileUtils;
import com.zoom2u.webservice.WebserviceHandler;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

public class PoliceCheckView extends Activity implements View.OnClickListener {

    TextView countChatPoliceCheck;
    //TextView selectPdFBtnPoliceCheck;
   // ImageView defaultPdfImgPoloceCheck;
   // TextView fileNameTxt;
    //Button uploadBtnPoliceCheck;
    TextView link;
    private boolean isPDFUPloaded = false;

    @Override
    protected void onResume() {
        super.onResume();
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countChatPoliceCheck);
        SlideMenuZoom2u.countChatBookingView = countChatPoliceCheck;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.policecheck_view);

        RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
        Window window = PoliceCheckView.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if(MainActivity.isIsBackGroundGray()){
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        }else{
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }
        findViewById(R.id.backFromBookingDetail).setOnClickListener(this);
        findViewById(R.id.bookingDetailChatIcon).setOnClickListener(this);
        countChatPoliceCheck = findViewById(R.id.countChatBookingDetail);
        countChatPoliceCheck.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countChatPoliceCheck;
        link = findViewById(R.id.link);
        link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.link:
                try
                {
                    String url = "http://courier.zoom2u.com/#/login";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                } catch (Exception e){
                e.printStackTrace();
             }
                break;
            case R.id.backFromBookingDetail:
                backFromPDFUpload();
                break;
            case R.id.bookingDetailChatIcon:
                Intent chatViewItent = new Intent(PoliceCheckView.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
           /* case R.id.selectPdFBtnPoliceCheck:
//                Intent target = FileUtils.createGetContentIntent();
//                Intent intent = Intent.createChooser(target, getString(R.string.gcm_message));
//                startActivityForResult(intent, CourierIdentification_View.PDF_SELECTION);

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(
                        Intent.createChooser(intent, "Select a File to Upload"),
                        CourierIdentification_View.PDF_SELECTION);


                break;*/
           /* case R.id.uploadBtnPoliceCheck:
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    new Upload_PoliceCheck_PDF_AsyncTask().execute();
                else
                    DialogActivity.alertDialogView(PoliceCheckView.this, "No Network", "No network connection, Please check your internet connection.");
                break;*/
        }
    }

    private void backFromPDFUpload() {
        if (isPDFUPloaded) {
            setResult(Activity.RESULT_OK);
            finish();
        } else {
            AccountDetailFragment.OPEN_SETTING_VIEW = 4;
            finish();
        }
    }

    @Override
    public void onBackPressed() {
    //    super.onBackPressed();
        backFromPDFUpload();
    }

    File myFile;
    Uri uri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CourierIdentification_View.PDF_SELECTION:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    uri = data.getData();
                    String displayName = "document.pdf";
                    try {
                        String uriFilePath = FileUtils.getPath(this, uri);
                        myFile = new File(uriFilePath);

                        String uriString = uri.toString();
                        File getNameFromFile = new File(uriString);

                        if (uriString.startsWith("content://")) {
                            Cursor cursor = null;
                            try {
                                cursor = getContentResolver().query(uri, null, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                }
                            } finally {
                                cursor.close();
                            }
                        } else if (uriString.startsWith("file://")) {
                            displayName = getNameFromFile.getName();
                        }

                        if ((!displayName.equals("") && displayName.contains(".pdf")) || displayName.contains(".PDF")) {
                            showPdfFile(displayName);
                        } else {
                            Toast.makeText(PoliceCheckView.this, "Please select pdf file only", Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (uri != null)
                            showPdfFile(displayName);
                        else
                            Toast.makeText(PoliceCheckView.this, "Error, While selecting file", Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showPdfFile(String displayName) {
       /* fileNameTxt.setText(displayName);
        fileNameTxt.setVisibility(View.VISIBLE);
        defaultPdfImgPoloceCheck.setImageResource(R.drawable.default_pdf);
        defaultPdfImgPoloceCheck.setVisibility(View.VISIBLE);
        findViewById(R.id.uploadBtnPoliceCheck).setEnabled(true);
        findViewById(R.id.uploadBtnPoliceCheck).setAlpha(1.0f);
        findViewById(R.id.uploadBtnPoliceCheck).setOnClickListener(this);*/
    }


}
