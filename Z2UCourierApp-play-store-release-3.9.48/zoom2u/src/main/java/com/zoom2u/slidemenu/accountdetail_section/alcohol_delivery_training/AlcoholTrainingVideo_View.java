package com.zoom2u.slidemenu.accountdetail_section.alcohol_delivery_training;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.androidquery.AQuery;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.Vimeo.Exceptions.VimeoException;
import com.zoom2u.Vimeo.VimeoCallback;
import com.zoom2u.Vimeo.VimeoExtractor;
import com.zoom2u.Vimeo.VimeoQuality.VimeoQuality;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.AccountDetailFragment;

public class AlcoholTrainingVideo_View extends Activity implements View.OnClickListener, VimeoCallback {

    VideoView videoViewToPlayVideo;
    TextView titleTxtalcoholeTrainingVideo, msgTxtalcoholeTrainingVideo;
    ImageView backBtnalcoholeTrainingVideo;

    RelativeLayout signatureImgLayout;
    Button nextPageButton;
    ImageView uploadedSignatureImg;

    ProgressBar progressBar2AlcoholeTraining;

    boolean flag_Does_Alcohol_Delivery;
    String signatureImageURL, lastImgUploadDateTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alcohol_training_video_view);

        flag_Does_Alcohol_Delivery = getIntent().getBooleanExtra("flag_does_alcohol_delivery", false);
        signatureImageURL = getIntent().getStringExtra("signatureImageURL");
        try {
            lastImgUploadDateTime = getIntent().getStringExtra("lastImgUploadDateTime");
            if (lastImgUploadDateTime == null)
                lastImgUploadDateTime = "";
        } catch (Exception e) {
            e.printStackTrace();
            lastImgUploadDateTime = "";
        }

        signatureImgLayout = (RelativeLayout) findViewById(R.id.signatureImgLayout);

        titleTxtalcoholeTrainingVideo = (TextView) findViewById(R.id.titleTxtalcoholeTrainingVideo);
        titleTxtalcoholeTrainingVideo.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        backBtnalcoholeTrainingVideo = (ImageView) findViewById(R.id.backBtnalcoholeTrainingVideo);
        backBtnalcoholeTrainingVideo.setOnClickListener(this);

        progressBar2AlcoholeTraining = (ProgressBar) findViewById(R.id.progressBar2AlcoholeTraining);
        progressBar2AlcoholeTraining.setVisibility(View.VISIBLE);

        msgTxtalcoholeTrainingVideo = (TextView) findViewById(R.id.msgTxtalcoholeTrainingVideo);
        msgTxtalcoholeTrainingVideo.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

        uploadedSignatureImg = (ImageView) findViewById(R.id.uploadedSignatureImg);

        nextPageButton = (Button) findViewById(R.id.nextPageButton);
        nextPageButton.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        nextPageButton.setOnClickListener(this);

        videoViewToPlayVideo = (VideoView) findViewById(R.id.videoViewToPlayVideo);

        if (flag_Does_Alcohol_Delivery) {
            if (!lastImgUploadDateTime.equals("") && !lastImgUploadDateTime.equals("null")) {
                try{
                    String startTimeStr[] = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(lastImgUploadDateTime).split(" ");
                    msgTxtalcoholeTrainingVideo.setText("You are enabled for alcohol deliveries!\n\nLast updated - " +startTimeStr[0]);
                }catch(Exception e){
                    e.printStackTrace();
                    msgTxtalcoholeTrainingVideo.setText("You are enabled for alcohol deliveries!");
                }
            } else
                msgTxtalcoholeTrainingVideo.setText("You are enabled for alcohol deliveries!");
            msgTxtalcoholeTrainingVideo.setPadding(0, 10, 0, 0);
            msgTxtalcoholeTrainingVideo.setTextSize(16.0f);
            nextPageButton.setText("Rewatch Video");
            signatureImgLayout.setVisibility(View.VISIBLE);
            videoViewToPlayVideo.setVisibility(View.INVISIBLE);
            progressBar2AlcoholeTraining.setVisibility(View.GONE);
            AQuery aQuery = new AQuery(AlcoholTrainingVideo_View.this);
            aQuery.id(uploadedSignatureImg).image(signatureImageURL, true, true, uploadedSignatureImg.getWidth(), R.drawable.signature1, null, 0, 0);
        } else {
            playVideo();
        }
    }

    private void playVideo() {
        msgTxtalcoholeTrainingVideo.setText(R.string.training_video_msg_txt);
        msgTxtalcoholeTrainingVideo.setPadding(0, 20, 0, 0);
        msgTxtalcoholeTrainingVideo.setTextSize(14.0f);
        nextPageButton.setEnabled(false);
        nextPageButton.setClickable(false);
        nextPageButton.setAlpha(0.5f);
        nextPageButton.setText("Next");
        signatureImgLayout.setVisibility(View.GONE);
        videoViewToPlayVideo.setVisibility(View.VISIBLE);
        progressBar2AlcoholeTraining.setVisibility(View.VISIBLE);
        final MediaController mediacontroller = new MediaController(this);
        mediacontroller.setAnchorView(videoViewToPlayVideo);
        videoViewToPlayVideo.setMediaController(mediacontroller);

        VimeoExtractor extr = new VimeoExtractor("304552230", getApplicationContext(), VimeoQuality.mobile, this);
        extr.downloadVideoFile();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtnalcoholeTrainingVideo:
                progressBar2AlcoholeTraining.setVisibility(View.GONE);
                AccountDetailFragment.OPEN_SETTING_VIEW = 4;
                finish();
                break;
            case R.id.nextPageButton:
                if (flag_Does_Alcohol_Delivery) {
                    playVideo();
                } else {
                    Intent intent = new Intent(AlcoholTrainingVideo_View.this, Alcohol_Delivery_Signature.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    intent = null;
                    finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        progressBar2AlcoholeTraining.setVisibility(View.GONE);
        AccountDetailFragment.OPEN_SETTING_VIEW = 4;
        finish();
    }

    @Override
    public void vimeoURLCallback(String callback) {
        Uri uri = Uri.parse(callback);
        videoViewToPlayVideo.setVideoURI(uri);
        videoViewToPlayVideo.requestFocus();
        progressBar2AlcoholeTraining.setVisibility(View.GONE);

        videoViewToPlayVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                flag_Does_Alcohol_Delivery = false;
                nextPageButton.setEnabled(true);
                nextPageButton.setClickable(true);
                nextPageButton.setAlpha(1.0f);
            }
        });

        videoViewToPlayVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(AlcoholTrainingVideo_View.this, "Error, While playing video", Toast.LENGTH_LONG).show();
                return false;
            }
        });

        videoViewToPlayVideo.start();
    }

    @Override
    public void videoExceptionCallback(VimeoException exceptionCallback) {
        DialogActivity.alertDialogView(AlcoholTrainingVideo_View.this, "Error", exceptionCallback.getMessage());
    }
}
