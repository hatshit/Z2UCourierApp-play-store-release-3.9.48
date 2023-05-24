package com.zoom2u.slidemenu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

/**
 * Created by Mahendra Dabi on 28-02-2023.
 */
public class DialogBecomeTeamLeadTermsCondition extends Dialog {
    private final Context mContext;
    private ProgressDialog progressCourierTeamTC;

    private OnDialogTermsConditionResponse onDialogTermsConditionResponse;

    public void setOnDialogTermsConditionResponse(OnDialogTermsConditionResponse onDialogTermsConditionResponse) {
        this.onDialogTermsConditionResponse = onDialogTermsConditionResponse;
    }

    interface OnDialogTermsConditionResponse{
        void onSuccess();
    }

    public DialogBecomeTeamLeadTermsCondition(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext=context;
        setCancelable(false);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Window window = getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_to_become_a_team_member);

        TextView courierTeamTermHeaderTxt = (TextView) findViewById(R.id.courierTeamTermHeaderTxt);

        courierTeamTermHeaderTxt.setText("Become a Team");

        TextView courierTeamMessageTxt7 = (TextView) findViewById(R.id.courierTeamMessageTxt7);

        RelativeLayout headerSummaryReportLayout =  findViewById(R.id.headerMyProfileInfoLayout);
        if (MainActivity.isIsBackGroundGray()) {
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);

        }

        findViewById(R.id.backFromMyProfileInfo).setOnClickListener(v -> dismiss());


        courierTeamMessageTxt7.setOnClickListener(v -> {
            try {
                Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse(WebserviceHandler.TERMS_CONDITIONS));
                mContext.startActivity(browserIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        Button courierTeamTermBtnDecline = (Button)findViewById(R.id.courierTeamTermBtnDecline);

        courierTeamTermBtnDecline.setOnClickListener(v -> dismiss());

        final Button courierTeamTermBtnAccept = (Button) findViewById(R.id.courierTeamTermBtnAccept);

        courierTeamTermBtnAccept.setOnClickListener(v -> {
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                AcceptCourierTeamTCAsyncTask();
                //new AcceptCourierTeamTCAsyncTask().execute();
            else
                DialogActivity.alertDialogView(mContext, "No Network!", "No Network connection, Please try again later.");
        });

    }


    private void AcceptCourierTeamTCAsyncTask(){

        final String[] webServiceResponseForAcceptTC = {"0"};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressCourierTeamTC == null)
                        progressCourierTeamTC = new ProgressDialog(mContext);
                    Custom_ProgressDialogBar.inItProgressBar(progressCourierTeamTC);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    webServiceResponseForAcceptTC[0] = webServiceHandler.createATeamMember();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (LoginZoomToU.isLoginSuccess == 0) {
                        dismiss();
                        try {
                            LoginZoomToU.CARRIER_ID = new JSONObject(webServiceResponseForAcceptTC[0]).getInt("teamId");
                            LoginZoomToU.IS_TEAM_LEADER = true;
                            LoginZoomToU.loginEditor.putInt("CarrierId", LoginZoomToU.CARRIER_ID);
                            LoginZoomToU.loginEditor.commit();

                            if (onDialogTermsConditionResponse!=null)
                                onDialogTermsConditionResponse.onSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(mContext, TeamMemberList_Activity.class);
                        mContext.startActivity(intent);
                        //mContext.overridePendingTransition(R.anim.right_in, R.anim.left_out);
                    } else if (LoginZoomToU.isLoginSuccess == 1)
                        DialogActivity.alertDialogView(mContext, "No Network!", "No Network connection, Please try again later.");
                    else
                        Functional_Utility.validationErrorMsg(mContext, webServiceResponseForAcceptTC[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(mContext, "Error!", "Something went wrong, Please try again");
                }finally {
                    if(progressCourierTeamTC != null)
                        if(progressCourierTeamTC.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressCourierTeamTC);
                }
            }
        }.execute();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
