package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.jinatonic.confetti.Utils;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddmemberActivity extends Activity implements View.OnClickListener{

    RelativeLayout headerDLayout;
    ImageView backtBtn,searchbtn,cleartext;
    EditText editEmail;
    LinearLayout addmember,backbtn;
    TextView invalidemail,foundemail;
    ImageView chatIconTeamList;
    String setemail="";
    String courierid ="";
    private ProgressDialog progressDialogSRView;
    String responseCommunitysearchcourier;
    String responseCommunityaddmember;
    RecyclerView recycle;
    RelativeLayout relativeinvalidemail;

    Dialog alertDialog;

    SearchcourierAdapter adapter;

    List<SearchCourierslist.Result> list = new ArrayList<>();

    boolean searchEnable=true;
    TextView chatCountInviteTeamMamber;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newaddmemberactivity);

        headerDLayout=findViewById(R.id.headerDLayout);
        backtBtn=findViewById(R.id.backtBtn);
        editEmail=findViewById(R.id.editEmail);
        addmember=findViewById(R.id.addmember);
        invalidemail=findViewById(R.id.invalidemail);
        searchbtn=findViewById(R.id.searchbtn);
        backbtn=findViewById(R.id.backbtn);
        recycle=findViewById(R.id.recycle);
        relativeinvalidemail=findViewById(R.id.relativeinvalidemail);
        cleartext=findViewById(R.id.cleartext);
        foundemail=findViewById(R.id.foundemail);
        chatIconTeamList=findViewById(R.id.chatIconTeamList);

        chatCountInviteTeamMamber = (TextView)  findViewById(R.id.countChatTeamList);
        chatCountInviteTeamMamber.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;


        Window window = AddmemberActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if(MainActivity.isIsBackGroundGray()){
            window.setStatusBarColor(Color.parseColor("#374350"));
            headerDLayout.setBackgroundResource(R.color.base_color_gray);
        }else{
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
            headerDLayout.setBackgroundResource(R.color.base_color1);
        }

        cleartext.setOnClickListener(this);
        searchbtn.setOnClickListener(this);
        addmember.setOnClickListener(this);
        backbtn.setOnClickListener(this);
        backtBtn.setOnClickListener(this);
        chatIconTeamList.setOnClickListener(this);

        editEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch();
                    return true;
                }
                return false;
            }
        });

        editEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               if (TextUtils.isEmpty(charSequence))
                {
                    relativeinvalidemail.setVisibility(View.GONE);
                    cleartext.setVisibility(View.GONE);
                    return;
                }
              String  emailInput = editEmail.getText().toString().trim();
               // if (Patterns.EMAIL_ADDRESS.matcher(charSequence).matches()) {
                if(emailValidation(editEmail.getText().toString())) {
                        performSearch();
                        relativeinvalidemail.setVisibility(View.GONE);
                    }
                    else {
                        invalidemail.setText("Email address not valid.");
                        invalidemail.setTextColor(Color.RED);
                        cleartext.setVisibility(View.VISIBLE);
                        recycle.setVisibility(View.GONE);
                        foundemail.setVisibility(View.GONE);
                        relativeinvalidemail.setVisibility(View.VISIBLE);
                    }
            }
            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(chatCountInviteTeamMamber);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;
    }

    private void performSearch() {
        if (TextUtils.isEmpty(editEmail.getText().toString())){
            Toast.makeText(AddmemberActivity.this,"Please input a valid email to search.", Toast.LENGTH_SHORT).show();
        }
        else {
            apiCallToGetcommunityaddmember();
        }
    }

    private void apiCallToGetcommunityaddmember() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            getCommunityAddMembersAsyncTask();
        else
            DialogActivity.alertDialogView(this, "No Network!", "No network connection, Please try again later.");
    }

    private void getCommunityAddMembersAsyncTask() {
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(AddmemberActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunitysearchcourier = webServiceHandler.getCommunitySearchCouriers(editEmail.getText().toString());
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                try {
                    JSONObject jsonObject = new JSONObject(responseCommunitysearchcourier);
                    if (jsonObject.has("Message") && !jsonObject.isNull("Message")) {
                        courierid ="";
                        cleartext.setVisibility(View.VISIBLE);
                        relativeinvalidemail.setVisibility(View.VISIBLE);
                        recycle.setVisibility(View.GONE);
                        foundemail.setVisibility(View.GONE);
                        invalidemail.setText("Email address not found.");
                        Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                       // DialogActivity.alertDialogView(AddmemberActivity.this, "Message", jsonObject.getString("Message"));
                    }else {
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        if (jsonArray.length()>0) {
                            list.clear();
                            hideKeyboard(AddmemberActivity.this);
                            recycle.setVisibility(View.VISIBLE);
                            foundemail.setVisibility(View.VISIBLE);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                SearchCourierslist.Result result = new SearchCourierslist.Result();
                                result.setEmail(jsonObject1.getString("Email"));
                                result.setCourierId(jsonObject1.getString("CourierId"));
                                list.add(result);
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddmemberActivity.this, RecyclerView.VERTICAL, false);
                            recycle.setLayoutManager(linearLayoutManager);

                            adapter = new SearchcourierAdapter(AddmemberActivity.this, list);
                            recycle.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                            if (list!=null && list.size()>0){
                                emailclick(list.get(0));
                            }

                            adapter.setOnItemClickListener(new SearchcourierAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    emailclick(list.get(position));
                                }
                            });
                        }else {
                            courierid ="";
                            cleartext.setVisibility(View.VISIBLE);
                            relativeinvalidemail.setVisibility(View.VISIBLE);
                            recycle.setVisibility(View.GONE);
                            foundemail.setVisibility(View.GONE);
                            invalidemail.setText("Email address not found.");
                        }
                    }
                }catch (Exception e){
                    courierid ="";
                    cleartext.setVisibility(View.VISIBLE);
                    relativeinvalidemail.setVisibility(View.VISIBLE);
                    recycle.setVisibility(View.GONE);
                    foundemail.setVisibility(View.GONE);
                    invalidemail.setText("Email address not found.");
                  //  DialogActivity.alertDialogView(AddmemberActivity.this, "Server Error!", "Something went wrong, Please try again later.");
                }

            }
        }.execute();
    }

    private void apiCallToGetcommunityaddnewmember() {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            communityAddNewMembersAsyncTask();
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(this, "No Network!", "No network connection, Please try again later.");
    }

    private void communityAddNewMembersAsyncTask() {
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(AddmemberActivity.this);
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityaddmember = webServiceHandler.getCommunityaddmember(courierid);
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }
            @Override
            public void onPostExecute() {
                addmember.setEnabled(true);
                Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                try {
                    JSONObject jsonObject = new JSONObject(responseCommunityaddmember);
                    if (jsonObject.has("Message") && !jsonObject.isNull("Message")){
                        invalidemail.setVisibility(View.GONE);
                        cleartext.setVisibility(View.VISIBLE);
                        relativeinvalidemail.setVisibility(View.VISIBLE);
                        DialogActivity.alertDialogViewNew(AddmemberActivity.this, "Message",jsonObject.getString("Message").toString());
                    }else {
                        hideKeyboard(AddmemberActivity.this);
                        editEmail.setText("");
                        cleartext.setVisibility(View.VISIBLE);
                        recycle.setVisibility(View.GONE);
                        foundemail.setVisibility(View.GONE);
                        cleartext.setVisibility(View.GONE);
                        DialogActivity.alertDialogViewNew(AddmemberActivity.this, "Message","Invitation sent successfully.".toString());
                    }
                }catch (Exception e) {
                    courierid ="";
                    invalidemail.setVisibility(View.GONE);
                    cleartext.setVisibility(View.VISIBLE);
                    relativeinvalidemail.setVisibility(View.VISIBLE);
                    DialogActivity.alertDialogView(AddmemberActivity.this, "Server Error!", "Something went wrong, Please try again later.");
                }
            }
        }.execute();
    }

    public void emailclick(SearchCourierslist.Result result){
        setemail=result.getEmail();
        cleartext.setVisibility(View.GONE);
        recycle.setVisibility(View.VISIBLE);
        foundemail.setVisibility(View.VISIBLE);
        courierid= result.getCourierId();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cleartext:
                relativeinvalidemail.setVisibility(View.GONE);
                foundemail.setVisibility(View.GONE);
                recycle.setVisibility(View.GONE);
                cleartext.setVisibility(View.GONE);
                editEmail.setText("");
                break;
            case R.id.searchbtn:
               performSearch();
                break;
            case R.id.addmember:
                if (TextUtils.isEmpty(editEmail.getText().toString())){
                    Toast.makeText(AddmemberActivity.this,"Please input a valid email to search.", Toast.LENGTH_SHORT).show();
                }else if(emailValidation(editEmail.getText().toString())) {
                    if(courierid.equals("") && courierid!=null)
                    {
                        invalidemail.setText("Email address not valid.");
                        invalidemail.setTextColor(Color.RED);
                        cleartext.setVisibility(View.VISIBLE);
                        recycle.setVisibility(View.GONE);
                        foundemail.setVisibility(View.GONE);
                        relativeinvalidemail.setVisibility(View.VISIBLE);

                    }else {
                        cleartext.setVisibility(View.GONE);
                        relativeinvalidemail.setVisibility(View.GONE);
                        addmember.setEnabled(false);
                        apiCallToGetcommunityaddnewmember();
                    }
                }else {
                    invalidemail.setText("Email address not valid.");
                    invalidemail.setTextColor(Color.RED);
                    cleartext.setVisibility(View.VISIBLE);
                    recycle.setVisibility(View.GONE);
                    foundemail.setVisibility(View.GONE);
                    relativeinvalidemail.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.backbtn:
            case R.id.backtBtn:
                finish();
                break;
            case R.id.chatIconTeamList:
                Intent intent = new Intent(AddmemberActivity.this, ChatViewBookingScreen.class);
                startActivity(intent);
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Boolean emailValidation(String email) {
    String emailPattern= "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if (email.toString().matches(emailPattern)) {
            return true;
        }
        return false;
    }
}
