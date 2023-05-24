
package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.jinatonic.confetti.ConfettiManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class AbstractActivity extends Activity implements View.OnClickListener {
    protected ViewGroup container;

    protected int goldDark, goldMed, gold, goldLight;
    protected int[] colors;

    private final List<ConfettiManager> activeConfettiManagers = new ArrayList<>();

    RoundedImageView imgAchievementNotifyView;
    TextView titleTxtAchievementView, msgTxtAchievementView, okBtnAchievementView;
    ProgressBar progressBarAcheivementNotify;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initAchievementNotifyView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_milestones_dialog);

        container = (ViewGroup) findViewById(R.id.container);

        final Resources res = getResources();
        goldDark = res.getColor(R.color.gold_dark);
        goldMed = res.getColor(R.color.gold_med);
        gold = res.getColor(R.color.gold);
        goldLight = res.getColor(R.color.gold_light);
        colors = new int[] { goldDark, goldMed, gold, goldLight };

        timerToGetLogin();
        initAchievementNotifyView();
    }

    private void timerToGetLogin() {
        // set timer to show splash screen for 3 second
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activeConfettiManagers.add(generateInfinite());
                    }
                });
            }
        }, 1000);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.okBtnAchievementView:
                stopConfettiRendering();
                finish();
                break;
        }
    }

    void stopConfettiRendering(){
        for (ConfettiManager confettiManager : activeConfettiManagers) {
            confettiManager.terminate();
        }
        activeConfettiManagers.clear();
    }

    protected abstract ConfettiManager generateOnce();
    protected abstract ConfettiManager generateStream();
    protected abstract ConfettiManager generateInfinite();

    private void initAchievementNotifyView() {
        if (imgAchievementNotifyView == null)
            imgAchievementNotifyView = (RoundedImageView) findViewById(R.id.imgAchievementNotifyView);

        if (progressBarAcheivementNotify == null)
            progressBarAcheivementNotify = (ProgressBar) findViewById(R.id.progressBarAcheivementNotify);
        progressBarAcheivementNotify.setVisibility(View.VISIBLE);

        if (titleTxtAchievementView == null)
            titleTxtAchievementView = (TextView) findViewById(R.id.titleTxtAchievementView);

        if (msgTxtAchievementView == null)
            msgTxtAchievementView = (TextView) findViewById(R.id.msgTxtAchievementView);
       ;
        if (okBtnAchievementView == null)
            okBtnAchievementView = (TextView) findViewById(R.id.okBtnAchievementView);

        okBtnAchievementView.setOnClickListener(this);

        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new GetDataForNotifyAchievement().execute();
            GetDataForNotifyAchievement();
        else
            DialogActivity.alertDialogView(this, "Network error!", "No network connection, Please try again later.");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopConfettiRendering();
        finish();
    }

    private void GetDataForNotifyAchievement(){
        final JSONObject[] jObjOfNotifyAchievement = new JSONObject[1];

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                // before execution
            }

            @Override
            public void doInBackground() {
                try{
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if(!PushReceiver.prefrenceForPushy.getString("achievementId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("achievementId", "").equals("")) {
                        int achievementId = Integer.parseInt(PushReceiver.prefrenceForPushy.getString("achievementId", "0"));
                        String responseStrOfNotifyAchievement = webServiceHandler.getAchievementById(achievementId);
                        try {
                            jObjOfNotifyAchievement[0] = new JSONArray(responseStrOfNotifyAchievement).getJSONObject(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    webServiceHandler = null;
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (jObjOfNotifyAchievement[0] != null && jObjOfNotifyAchievement[0].length() > 0) {
                        titleTxtAchievementView.setText(jObjOfNotifyAchievement[0].getString("Title"));
                        msgTxtAchievementView.setText(jObjOfNotifyAchievement[0].getString("Description"));
                        Picasso.with(AbstractActivity.this)
                                .load(jObjOfNotifyAchievement[0].getString("ImageUrl"))
                                .fit()
                                .placeholder(R.drawable.customer_profile_photo)
                                .into(imgAchievementNotifyView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBarAcheivementNotify.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        progressBarAcheivementNotify.setVisibility(View.GONE);
                                    }
                                });
                    }else
                        setErrorMsg();
                } catch (Exception e) {
                    e.printStackTrace();
                    setErrorMsg();
                }
            }
        }.execute();

    }


    private void setErrorMsg(){
        titleTxtAchievementView.setText("Alert!");
        msgTxtAchievementView.setText("Data couldn't load at this moment.");
        okBtnAchievementView.setText("Ok");
        progressBarAcheivementNotify.setVisibility(View.GONE);
    }

}
