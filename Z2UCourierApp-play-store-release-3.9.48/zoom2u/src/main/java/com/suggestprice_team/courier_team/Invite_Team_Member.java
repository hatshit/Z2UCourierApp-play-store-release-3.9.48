package com.suggestprice_team.courier_team;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Arun on 13-July-2018.
 */

public class Invite_Team_Member extends Activity implements View.OnClickListener {

    public static int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1019;

//    JSONObject addressObj;

    private TextView countInviteTeamMember;
    private TextView firstNameAddMember, lastNameAddMember, emailAddMember, phoneNoAddMember,
            addressAddMember;
    private ScrollView scrollViewInviteTeam;
    private RoundedImageView memberIconAddMemberView;

    private EditText edtFirstNameAddMember, edtLastNameAddMember, edtEmailAddMember, edtPhoneNoAddMember;
    private TextView edtAddressAddMember, bikeTxtAddMember, carTxtAddMember, vanTxtAddMember;
    private Button cancelBtnAddMember, acceptBtnAddMember;

    private RadioGroup rgInviteTeamMember;
    private RadioButton bikeVehicleButton, carVehicleButton, vanVehicleButton;
    private String vehicleType = "Car";

    String firstNameStr, lastNameStr, emailAddressStr, phoneNoStr, address;
    ProgressDialog progressInviteTeamMember;

    private boolean isTeamMemberAdded = false;

    private boolean isEditTeamMember;
    private MyTeamList_Model teamMembersModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_member_view);

        RelativeLayout headerLayoutAllTitleBar = findViewById(R.id.headerDLayoutAddTeam);

        Window window = Invite_Team_Member.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerLayoutAllTitleBar.setBackgroundResource(R.color.base_color1);
        }
        String apiKey = "AIzaSyDsqlqVQsCmsNdqjp3guok-DfH52YsrRc8";

        // Setup Places Client
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        initInviteTeamUIContent();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                edtAddressAddMember.setText(place.getAddress());
                edtAddressAddMember.setTextColor(getResources().getColor(R.color.gun_metal));
                edtAddressAddMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
                addressAddMember.setVisibility(View.VISIBLE);
            //    getAddressData(place.getAddress().toString());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                edtAddressAddMember.setText("");
                edtAddressAddMember.setTextColor(getResources().getColor(R.color.add_member_hint_color));
                edtAddressAddMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);
                addressAddMember.setVisibility(View.INVISIBLE);
                Log.i("Place API Failure", "  -------------- Error -------------"+status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i("Place API Failure", "  -------------- User Cancelled -------------");
            }
        }
    }

//    private void getAddressData(String addressStr) {
//        if (addressObj != null)
//            addressObj = null;
//        addressObj = new JSONObject();
//        //************ Add geocoded address to address object **************
//        HashMap<String, Object> mapForInstantquotesAddressDetail = null;
//        try {
//            try {
//                mapForInstantquotesAddressDetail = GetAddressFromGoogleAPI.getAddressDetailGeoCoder(addressStr);
//                if (mapForInstantquotesAddressDetail != null) {
//                    addressObj.put("street1", (String) mapForInstantquotesAddressDetail.get("address"));
//                    addressObj.put("street2", "");
//                    addressObj.put("suburb", (String) mapForInstantquotesAddressDetail.get("suburb"));
//                    addressObj.put("province", (String) mapForInstantquotesAddressDetail.get("state"));
//                    addressObj.put("zipcode", (String) mapForInstantquotesAddressDetail.get("postcode"));
//                    addressObj.put("location", mapForInstantquotesAddressDetail.get("latitude")+","+mapForInstantquotesAddressDetail.get("longitude"));
//                } else
//                    addressObj.put("street1", "");
//            } catch (Exception e) {
//                e.printStackTrace();
//                addressObj.put("street1", "");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onBackPressed() {
        if (isTeamMemberAdded) {
            setResult(Activity.RESULT_OK);
            finish();
        } else
            finish();
        super.onBackPressed();
    }

     @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(countInviteTeamMember);
    }

    private void initInviteTeamUIContent() {

        isTeamMemberAdded = false;

        isEditTeamMember = getIntent().getBooleanExtra("isEditTeamMember", false);

        findViewById(R.id.backFromAddTeam).setOnClickListener(this);
        findViewById(R.id.titleAddTeam);
        findViewById(R.id.chatIconAddTeam).setOnClickListener(this);
        countInviteTeamMember = (TextView)  findViewById(R.id.countChatAddTeam);

        countInviteTeamMember.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = countInviteTeamMember;

        scrollViewInviteTeam = (ScrollView) findViewById(R.id.scrollViewInviteTeam);

        firstNameAddMember = (TextView) findViewById(R.id.firstNameAddMember);

        lastNameAddMember = (TextView) findViewById(R.id.lastNameAddMember);

        emailAddMember = (TextView) findViewById(R.id.emailAddMember);

        phoneNoAddMember = (TextView) findViewById(R.id.phoneNoAddMember);

        addressAddMember = (TextView) findViewById(R.id.addressAddMember);

        findViewById(R.id.vehicleTypeAddMember);

        edtFirstNameAddMember = (EditText) findViewById(R.id.edtFirstNameAddMember);
        edtLastNameAddMember = (EditText) findViewById(R.id.edtLastNameAddMember);
        edtEmailAddMember = (EditText) findViewById(R.id.edtEmailAddMember);
        edtPhoneNoAddMember = (EditText) findViewById(R.id.edtPhoneNoAddMember);
        edtAddressAddMember = (TextView) findViewById(R.id.edtAddressAddMember);


        setEdtFieldBGtoGray(edtFirstNameAddMember, firstNameAddMember);
        setEdtFieldBGtoGray(edtLastNameAddMember, lastNameAddMember);
        setEdtFieldBGtoGray(edtEmailAddMember, emailAddMember);
        setEdtFieldBGtoGray(edtPhoneNoAddMember, phoneNoAddMember);

        edtAddressAddMember.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (hasFocus) {
                        placeAPICallForAddress();
                    }
                }
            }
        });

        edtAddressAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeAPICallForAddress();
            }
        });

//        edtAddressAddMember.setAdapter(new GooglePlacesAutocompleteAdapter(Invite_Team_Member.this, R.layout.list_item_autocomplete));
//        edtAddressAddMember.setThreshold(0);

        bikeTxtAddMember = (TextView)  findViewById(R.id.bikeTxtAddMember);

        carTxtAddMember = (TextView)  findViewById(R.id.carTxtAddMember);

        vanTxtAddMember = (TextView)  findViewById(R.id.vanTxtAddMember);


        cancelBtnAddMember = (Button) findViewById(R.id.cancelBtnAddMember);

        cancelBtnAddMember.setOnClickListener(this);
        acceptBtnAddMember = (Button) findViewById(R.id.acceptBtnAddMember);

        acceptBtnAddMember.setOnClickListener(this);

        rgInviteTeamMember = (RadioGroup) findViewById(R.id.rgInviteTeamMember);
        bikeVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForBike);
        carVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForCar);
        vanVehicleButton = (RadioButton) rgInviteTeamMember.findViewById(R.id.rbForVan);
        bikeVehicleButton.setOnClickListener(this);
        carVehicleButton.setOnClickListener(this);
        vanVehicleButton.setOnClickListener(this);

        if (isEditTeamMember) {
            teamMembersModel = getIntent().getParcelableExtra("TeamMemberInfo");

            if (teamMembersModel != null) {
                edtFirstNameAddMember.setText(teamMembersModel.getFirstName());
                setEdtFieldBGtoBlue(edtFirstNameAddMember, firstNameAddMember);
                edtLastNameAddMember.setText(teamMembersModel.getLastName());
                setEdtFieldBGtoBlue(edtLastNameAddMember, lastNameAddMember);
                edtEmailAddMember.setText(teamMembersModel.getEmail());
                setEdtFieldBGtoBlue(edtEmailAddMember, emailAddMember);
                edtPhoneNoAddMember.setText(teamMembersModel.getMobile());
                setEdtFieldBGtoBlue(edtPhoneNoAddMember, phoneNoAddMember);

                String addressStr = teamMembersModel.getAddressModel().getStreet1();

                edtAddressAddMember.setText(addressStr);
                if (!edtAddressAddMember.getText().toString().equals("")) {
                    edtAddressAddMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
                    edtAddressAddMember.setTextColor(getResources().getColor(R.color.gun_metal));
                    addressAddMember.setVisibility(View.VISIBLE);
                }

//                if (addressObj != null)
//                    addressObj = null;
//                addressObj = new JSONObject();
//                try {
//                    addressObj.put("street1", teamMembersModel.getAddressModel().getStreet1());
//                    addressObj.put("street2", teamMembersModel.getAddressModel().getStreet2());
//                    addressObj.put("suburb", teamMembersModel.getAddressModel().getSuburb());
//                    addressObj.put("province", teamMembersModel.getAddressModel().getProvince());
//                    addressObj.put("zipcode", teamMembersModel.getAddressModel().getZipcode());
//                    addressObj.put("location", teamMembersModel.getAddressModel().getLocation());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                setVehicle();
            }
            ((TextView) findViewById(R.id.titleAddTeam)).setText("Edit Member");
            acceptBtnAddMember.setText("Update");
        } else {
            ((TextView) findViewById(R.id.titleAddTeam)).setText("Add Member");
            acceptBtnAddMember.setText("Add");
            firstNameAddMember.setVisibility(View.INVISIBLE);
            lastNameAddMember.setVisibility(View.INVISIBLE);
            emailAddMember.setVisibility(View.INVISIBLE);
            phoneNoAddMember.setVisibility(View.INVISIBLE);
            addressAddMember.setVisibility(View.INVISIBLE);
        }
    }

    private void setVehicle() {
        switch (teamMembersModel.getVehicle()) {
            case "Car":
                carSelection();
                carVehicleButton.setChecked(true);
                break;
            case "Bike":
                bikeSelection();
                bikeVehicleButton.setChecked(true);
                break;
            case "Van":
                vanSelection();
                vanVehicleButton.setChecked(true);
                break;
            default:
                carSelection();
                carVehicleButton.setChecked(true);
                break;
        }
    }

    private void setEdtFieldBGtoBlue(EditText edtFieldAddEditMember, TextView textFieldAddEditMember) {
        if (!edtFieldAddEditMember.getText().toString().equals("")) {
            edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
            edtFieldAddEditMember.setTextSize(17f);
            textFieldAddEditMember.setVisibility(View.VISIBLE);
        }
    }

    private void setEdtFieldBGtoGrayWhileBlank(EditText edtFieldAddEditMember) {
        if (edtFieldAddEditMember.getText().toString().equals("")) {
            edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

        }
    }

    private void setEdtFieldBGtoGray(final EditText edtFieldAddEditMember, final TextView txtFieldAddEditMember) {
        edtFieldAddEditMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

                    txtFieldAddEditMember.setVisibility(View.INVISIBLE);
                } else {
                    edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
                    txtFieldAddEditMember.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void placeAPICallForAddress() {
        try {
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS);
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .setCountry("AU")
                    .setTypeFilter(TypeFilter.ADDRESS)
                    .build(this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backFromAddTeam:
                if (isTeamMemberAdded) {
                    setResult(Activity.RESULT_OK);
                    finish();
                } else
                    finish();
                break;
            case R.id.chatIconAddTeam:
                LoginZoomToU.imm.hideSoftInputFromWindow(edtAddressAddMember.getWindowToken(), 0);
                Intent chatViewItent = new Intent(Invite_Team_Member.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.cancelBtnAddMember:
                finish();
                break;
            case R.id.rbForBike:
                bikeSelection();
                break;
            case R.id.rbForCar:
                carSelection();
                break;
            case R.id.rbForVan:
                vanSelection();
                break;
            case R.id.acceptBtnAddMember:
                firstNameStr = edtFirstNameAddMember.getText().toString();
                lastNameStr = edtLastNameAddMember.getText().toString();
                emailAddressStr = edtEmailAddMember.getText().toString();
                phoneNoStr = edtPhoneNoAddMember.getText().toString();
                address = edtAddressAddMember.getText().toString();

                if (firstNameStr.equals("") || lastNameStr.equals("") || emailAddressStr.equals("") ||
                        edtAddressAddMember.getText().toString().equals("") || phoneNoStr.equals(""))
                    DialogActivity.alertDialogView(Invite_Team_Member.this, "Alert", "Please fill all the fields");
                else if(!emailAddressStr.matches(LoginZoomToU.EMAIL_PATTERN))
                    DialogActivity.alertDialogView(Invite_Team_Member.this, "Alert!", "Please enter valid e-mail id");
		        else if(!phoneNoStr.matches(LoginZoomToU.PHONE_REGIX))
                    DialogActivity.alertDialogView(Invite_Team_Member.this, "Alert!", "Please enter valid phone number");
                else {
                    apiCallToInviteTeamMember();
                }
                break;
        }
    }

    private void vanSelection() {
        vehicleType = "Van";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
    }

    private void carSelection() {
        vehicleType = "Car";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
    }

    private void bikeSelection() {
        vehicleType = "Bike";
        bikeTxtAddMember.setTextColor(getResources().getColor(R.color.loginbtn_blue));
        carTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
        vanTxtAddMember.setTextColor(getResources().getColor(R.color.gray_dark));
    }

    //*************** API call to Invite team member *************
    void apiCallToInviteTeamMember () {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            InviteNewTeamMemberAPICall();
            //new InviteNewTeamMemberAPICall().execute();
        else
            DialogActivity.alertDialogView(Invite_Team_Member.this, "No Network!", "No Network connection, Please try again later.");
    }

    private void InviteNewTeamMemberAPICall(){
        final String[] responseMsgStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressInviteTeamMember == null)
                    progressInviteTeamMember = new ProgressDialog(Invite_Team_Member.this);
                Custom_ProgressDialogBar.inItProgressBar(progressInviteTeamMember);
            }

            @Override
            public void doInBackground() {
                //********** Create Json body to invite team member **********
                JSONObject jObjOfInviteTeamMember = null;
                try {
                    jObjOfInviteTeamMember = new JSONObject();
                    jObjOfInviteTeamMember.put("firstName", firstNameStr);
                    jObjOfInviteTeamMember.put("lastName", lastNameStr);
                    jObjOfInviteTeamMember.put("email", emailAddressStr);
                    jObjOfInviteTeamMember.put("mobile", phoneNoStr);
                    jObjOfInviteTeamMember.put("vehicle", vehicleType);
                    jObjOfInviteTeamMember.put("photo", null);
                    jObjOfInviteTeamMember.put("address", address);

                    if (isEditTeamMember)
                        jObjOfInviteTeamMember.put("CourierId", teamMembersModel.getCourierId());

                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseMsgStr[0] = webServiceHandler.inviteTeamMember(jObjOfInviteTeamMember.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    responseMsgStr[0] = "0";
                }
            }

            @Override
            public void onPostExecute() {
                if (progressInviteTeamMember != null)
                    if (progressInviteTeamMember.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressInviteTeamMember);

                if (responseMsgStr[0].equalsIgnoreCase("Invalid address")) {
                    DialogActivity.alertDialogView(Invite_Team_Member.this, "Alert!", "You have entered an invalid address, please enter correct address");
                } else if (responseMsgStr[0].equals("0")) {
                    DialogActivity.alertDialogView(Invite_Team_Member.this, "Alert!", "Please check the information you have entered.");
                } else {
                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            if (isEditTeamMember) {
                                Toast.makeText(Invite_Team_Member.this, "Team member details updated successfully.", Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            } else
                                showConfirmationDialog();
                            break;
                        case 1:
                            DialogActivity.alertDialogView(Invite_Team_Member.this, "No Network!", "No network connection, Please try again later.");
                            break;
                        case 2:
                        case 3:
                            Functional_Utility.validationErrorMsg(Invite_Team_Member.this, responseMsgStr[0]);
                            break;
                    }
                }
            }
        }.execute();
    }



//*************   Confirmation window to confirm and back to My Team member list  **************//
    Dialog selectProfileImgDialog;
    private void showConfirmationDialog() {

        if(selectProfileImgDialog != null)
            selectProfileImgDialog = null;
        selectProfileImgDialog = new Dialog(Invite_Team_Member.this, R.style.OpenDialogSlideAnim);
        selectProfileImgDialog.setCancelable(false);
        selectProfileImgDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.parseColor("#29000000")));
        selectProfileImgDialog.setContentView(R.layout.logoutwindow);

        Window window = selectProfileImgDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        try {
            TextView titleTxtOutstandingNotiDialog = (TextView) selectProfileImgDialog.findViewById(R.id.logoutWindowDialog);

            titleTxtOutstandingNotiDialog.setVisibility(View.GONE);

            TextView msgTxtOutstandingNotiDialog = (TextView) selectProfileImgDialog.findViewById(R.id.logoutWindowMessageText);

            msgTxtOutstandingNotiDialog.setText("Invitation sent successfully.\n\nDo you want to add more team members?");

            Button hideBtnOutstandingNotiDialog = (Button) selectProfileImgDialog.findViewById(R.id.logoutWindowCancelBtn);

            hideBtnOutstandingNotiDialog.setText("No, Thanks");
            hideBtnOutstandingNotiDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectProfileImgDialog.dismiss();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            });

            Button viewDetailBtnOutstandingNotiDialog = (Button) selectProfileImgDialog.findViewById(R.id.logoutWindowLogoutBtn);

            viewDetailBtnOutstandingNotiDialog.setText("Add");
            viewDetailBtnOutstandingNotiDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTeamMemberAdded = true;
                    edtFirstNameAddMember.setText("");
                    setEdtFieldBGtoGrayWhileBlank(edtFirstNameAddMember);
                    edtLastNameAddMember.setText("");
                    setEdtFieldBGtoGrayWhileBlank(edtLastNameAddMember);
                    edtEmailAddMember.setText("");
                    setEdtFieldBGtoGrayWhileBlank(edtEmailAddMember);
                    edtPhoneNoAddMember.setText("");
                    setEdtFieldBGtoGrayWhileBlank(edtPhoneNoAddMember);
                    edtAddressAddMember.setText("");
                    carVehicleButton.setChecked(true);
                    edtFirstNameAddMember.requestFocus();

                    edtAddressAddMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);
                    edtAddressAddMember.setTextColor(getResources().getColor(R.color.add_member_hint_color));


                    firstNameAddMember.setVisibility(View.INVISIBLE);
                    lastNameAddMember.setVisibility(View.INVISIBLE);
                    emailAddMember.setVisibility(View.INVISIBLE);
                    phoneNoAddMember.setVisibility(View.INVISIBLE);
                    addressAddMember.setVisibility(View.INVISIBLE);

                    selectProfileImgDialog.dismiss();
                }
            });
            selectProfileImgDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
