package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.z2u.chatview.ChatViewBookingScreen;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

public class EditTeamMembersActivity extends Activity implements View.OnClickListener {
    ImageView backImg;
    EditText nickname, mobileno, firstname, lastname;
    Button updateBtn, cancelBtn;
    private RadioGroup rgInviteTeamMember;
    ImageView chatIconAddTeam;
    private RadioButton rbForBike, rbForCar, rbForVan;
    RelativeLayout headerDLayoutAddTeam;

    private ProgressDialog progressDialogSRView;

    String responseCommunityupdatememberlist;
    String vehicleType = "Car";
    String fnme = "";
    String lnme = "";
    String phone = "";
    String nikname = "";
    String userid="";
    private TextView bikeTxtAddMember, carTxtAddMember, vanTxtAddMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addmemberactivity);

        Window window = EditTeamMembersActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        setUp();

        if (MainActivity.isIsBackGroundGray()) {
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerDLayoutAddTeam.setBackgroundResource(R.color.base_color_gray);
        } else {
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerDLayoutAddTeam.setBackgroundResource(R.color.base_color1);
        }

    }

    private void setUp() {
        backImg = findViewById(R.id.backImg);
        cancelBtn = findViewById(R.id.cancelBtn);
        chatIconAddTeam = findViewById(R.id.chatIconAddTeam);
        headerDLayoutAddTeam = findViewById(R.id.headerDLayoutAddTeam);
        nickname = findViewById(R.id.nickname);
        mobileno = findViewById(R.id.mobileno);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        updateBtn = findViewById(R.id.updateBtn);
        bikeTxtAddMember = findViewById(R.id.bikeTxtAddMember);
        carTxtAddMember = findViewById(R.id.carTxtAddMember);
        vanTxtAddMember = findViewById(R.id.vanTxtAddMember);
        rgInviteTeamMember = (RadioGroup) findViewById(R.id.rgInviteTeamMember);
        rbForBike = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForBike);
        rbForCar = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForCar);
        rbForVan = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForVan);

        cancelBtn.setOnClickListener(this);
        chatIconAddTeam.setOnClickListener(this);
        backImg.setOnClickListener(this);
        updateBtn.setOnClickListener(this);

        rbForBike.setEnabled(false);
        rbForCar.setEnabled(false);
        rbForVan.setEnabled(false);

        if (getIntent() != null) {
            fnme = getIntent().getStringExtra("firstName");
            lnme = getIntent().getStringExtra("lastName");
            phone = getIntent().getStringExtra("mobile");
            nikname = getIntent().getStringExtra("nikName");
            userid = getIntent().getStringExtra("userId");
            vehicleType = getIntent().getStringExtra("vehicleType");

            firstname.setText(firstLetterCapital(fnme));
            lastname.setText(firstLetterCapital(lnme));
            mobileno.setText(phone);
            nickname.setText(firstLetterCapital(nikname));



            setVehicle(vehicleType);
        }

    }

    private void setVehicle(String type) {
        if(type.equals("Van")) {
            rbForBike.setChecked(false);
            rbForVan.setChecked(true);
            rbForCar.setChecked(false);
        }else if(type.equals("Car")) {
            rbForBike.setChecked(false);
            rbForVan.setChecked(false);
            rbForCar.setChecked(true);
        }else {
            rbForBike.setChecked(true);
            rbForVan.setChecked(false);
            rbForCar.setChecked(false);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cancelBtn:
               finish();
                break;
            case R.id.chatIconAddTeam:
                Intent intent = new Intent(EditTeamMembersActivity.this, ChatViewBookingScreen.class);
                startActivity(intent);
                break;
            case R.id.backImg:
              onBackPressed();
                break;
            case R.id.updateBtn:
                if (TextUtils.isEmpty(nickname.getText().toString())) {
                    DialogActivity.alertDialogViewNew(EditTeamMembersActivity.this, "Alert!", "Please insert nickname.");
                  //  Toast.makeText(EditTeamMembersActivity.this, "Please insert nick name", Toast.LENGTH_SHORT).show();
                } else {
                    apiCallToGetCommunityUpdateMember();
                }
                break;
        }
    }

    private void apiCallToGetCommunityUpdateMember() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            getCommunityUpdateMembersAsyncTask();
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(EditTeamMembersActivity.this, "No Network!", "No network connection, Please try again later.");
    }

    private void getCommunityUpdateMembersAsyncTask() {

        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(EditTeamMembersActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityupdatememberlist = webServiceHandler.getCommunityupdatemember(userid, nickname.getText().toString());
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {

                Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                try {
                   // DialogActivity.alertDialogViewNew(EditTeamMembersActivity.this, "Message", "Member's details updated successfully.");
                    Toast.makeText(EditTeamMembersActivity.this, "Member's details updated successfully.", Toast.LENGTH_SHORT).show();
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("result", "result");
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
                } catch (Exception e) {
                  e.printStackTrace();
                }

            }
        }.execute();
    }
    public String firstLetterCapital(String name) {
        if(name.length()>0)
        {
            StringBuilder sb = new StringBuilder(name);
            sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
            return sb.toString();
        }
        return name;
    }

}
