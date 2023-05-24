package com.suggestprice_team.courier_team;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.suggestprice_team.courier_team.teammember_bookings.List_CourierTeamMembersBooking;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arun on 16-july-2018.
 */

public class TeamMemberList_Activity extends Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ProgressDialog progressTeamMemberList;
    TextView chatCountInviteTeamMamber;
    TextView addMemberBtn;
    SwipeRefreshLayout swipeRefreshMyTeamList;
    RecyclerView myTeamList;
    TextView teamsNotAvailTxtMyTeamList;
    public static int BACK_TO_ACTIVITY = 121;

    public static List<MyTeamList_Model> arrayOfMyTeamList;

    @Override
    protected void onResume() {
        super.onResume();
        SlideMenuZoom2u.setCourierToOnlineForChat();
        Model_DeliveriesToChat.showExclamationForUnreadChat(chatCountInviteTeamMamber);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myteam_list);

        findViewById(R.id.backFromTeamList).setOnClickListener(this);

        ((TextView) findViewById(R.id.titleTeamList)).setText("My Team");
        findViewById(R.id.chatIconTeamList).setOnClickListener(this);
        chatCountInviteTeamMamber = (TextView)  findViewById(R.id.countChatTeamList);

        chatCountInviteTeamMamber.setVisibility(View.GONE);
        SlideMenuZoom2u.countChatBookingView = chatCountInviteTeamMamber;


        findViewById(R.id.btnOpenTeamMembersBooking).setOnClickListener(this);

        RelativeLayout headerLayoutAllTitleBar = findViewById(R.id.headerDLayout);

        Window window = TeamMemberList_Activity.this.getWindow();
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


        addMemberBtn = (TextView)  findViewById(R.id.addMemberBtn);

        addMemberBtn.setOnClickListener(this);
        if (LoginZoomToU.IS_TEAM_LEADER) {
            addMemberBtn.setVisibility(View.VISIBLE);

        } else {
            addMemberBtn.setVisibility(View.GONE);

        }
        teamsNotAvailTxtMyTeamList = (TextView)  findViewById(R.id.teamsNotAvailTxtMyTeamList);

        teamsNotAvailTxtMyTeamList.setVisibility(View.GONE);
        swipeRefreshMyTeamList = (SwipeRefreshLayout)  findViewById(R.id.swipeRefreshMyTeamList);
        swipeRefreshMyTeamList.setOnRefreshListener(this);
        swipeRefreshMyTeamList.setColorSchemeColors(Color.parseColor("#215400"), Color.parseColor("#4f5151"), Color.parseColor("#a1c93a"));

        myTeamList = (RecyclerView)  findViewById(R.id.myTeamList);
        myTeamList.setNestedScrollingEnabled(false);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        myTeamList.setLayoutManager(mLayoutManager);
        myTeamList.setItemAnimator(new DefaultItemAnimator());

        myTeamList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                                           @Override
                                           public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                               super.onScrollStateChanged(recyclerView, newState);
                                           }

                                           @Override
                                           public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                               super.onScrolled(recyclerView, dx, dy);
                                               swipeRefreshMyTeamList.setEnabled(mLayoutManager.findFirstCompletelyVisibleItemPosition() == 0);
                                           }
                                       });

        inItTeamMemberListArray();

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            apiCallToMyTeamList();
        else
            DialogActivity.alertDialogView(TeamMemberList_Activity.this, "No Network!", "No Network connection, Please try again later.");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backFromTeamList:
                finish();
                break;
            case R.id.chatIconTeamList:
                Intent chatViewItent = new Intent(TeamMemberList_Activity.this, ChatViewBookingScreen.class);
                startActivity(chatViewItent);
                chatViewItent = null;
                break;
            case R.id.addMemberBtn:
                Intent intent = new Intent(TeamMemberList_Activity.this, Invite_Team_Member.class);
                intent.putExtra("isEditTeamMember", false);
                startActivityForResult(intent, BACK_TO_ACTIVITY);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                intent = null;
                break;
            case R.id.btnOpenTeamMembersBooking:
                Intent teamMemberBookingListIntent = new Intent(TeamMemberList_Activity.this, List_CourierTeamMembersBooking.class);
                startActivity(teamMemberBookingListIntent);
                chatViewItent = null;
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == BACK_TO_ACTIVITY) {
                refreshTeamMemberList ();
            }
        }
    }


    //*********** Init Team member array ************
    public static void inItTeamMemberListArray() {
        if (arrayOfMyTeamList != null) {
            if (arrayOfMyTeamList.size() > 0)
                arrayOfMyTeamList.clear();
        } else
            arrayOfMyTeamList = new ArrayList<MyTeamList_Model>();
    }

    //*********** Refresh Team member list data **********
    void refreshTeamMemberList () {
        inItTeamMemberListArray();
        swipeRefreshMyTeamList.setRefreshing(false);
        GetTeamMemberListAPICall();
        //new GetTeamMemberListAPICall().execute();
    }

    @Override
    public void onRefresh() {
        refreshTeamMemberList ();
    }
    //*************** API call to Invite team member *************
    void apiCallToMyTeamList () {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetTeamMemberListAPICall();
            //new GetTeamMemberListAPICall().execute();
        else
            DialogActivity.alertDialogView(TeamMemberList_Activity.this, "No Network!", "No Network connection, Please try again later.");
    }

    private void GetTeamMemberListAPICall(){
        final String[] responseMsgStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressTeamMemberList == null)
                    progressTeamMemberList = new ProgressDialog(TeamMemberList_Activity.this);
                Custom_ProgressDialogBar.inItProgressBar(progressTeamMemberList);
            }

            @Override
            public void doInBackground() {
                WebserviceHandler webServiceHandler = new WebserviceHandler();
                responseMsgStr[0] = webServiceHandler.getMyTeamList();
            }

            @Override
            public void onPostExecute() {
                if(progressTeamMemberList != null)
                    if(progressTeamMemberList.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressTeamMemberList);
                swipeRefreshMyTeamList.setRefreshing(false);
                switch (LoginZoomToU.isLoginSuccess) {
                    case 0:
                        if (!responseMsgStr[0].equals("")) {
                            try {
                                JSONArray jArrayOfMyTeam = new JSONArray(responseMsgStr[0]);
                                for (int i = 0; i < jArrayOfMyTeam.length(); i++) {
                                    JSONObject jObjOfMyTeam = jArrayOfMyTeam.getJSONObject(i);
                                    MyTeamList_Model myTeamListModel = new MyTeamList_Model();
                                    myTeamListModel.setCourierId(jObjOfMyTeam.getString("courierId"));
                                    myTeamListModel.setName(jObjOfMyTeam.getString("name"));
                                    myTeamListModel.setMobile(jObjOfMyTeam.getString("mobile"));
                                    myTeamListModel.setEmail(jObjOfMyTeam.getString("email"));
                                    myTeamListModel.setPhoto(jObjOfMyTeam.getString("photo"));
                                    myTeamListModel.setActiveJobCount(jObjOfMyTeam.getInt("activeJobCount"));
                                    myTeamListModel.setAddressModel(jObjOfMyTeam.getJSONObject("address"));
                                    myTeamListModel.setFirstName(jObjOfMyTeam.getString("firstName"));
                                    myTeamListModel.setLastName(jObjOfMyTeam.getString("lastName"));
                                    myTeamListModel.setVehicle(jObjOfMyTeam.getString("vehicle"));

                                    arrayOfMyTeamList.add(myTeamListModel);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                DialogActivity.alertDialogView(TeamMemberList_Activity.this, "Sorry!", "Something went wrong, Please try again later.");
                            }

                            if (arrayOfMyTeamList.size() > 0) {
                                MyTeamListAdapter chatListAdapter = new MyTeamListAdapter(arrayOfMyTeamList, new MyTeamListAdapter.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(MyTeamList_Model item) {
                                        Intent intent = new Intent(TeamMemberList_Activity.this, Invite_Team_Member.class);
                                        intent.putExtra("isEditTeamMember", true);
                                        intent.putExtra("TeamMemberInfo", item);
                                        startActivityForResult(intent, BACK_TO_ACTIVITY);
                                        overridePendingTransition(R.anim.right_in, R.anim.left_out);
                                        intent = null;
                                    }
                                }, TeamMemberList_Activity.this);
                                myTeamList.setAdapter(chatListAdapter);
                                teamsNotAvailTxtMyTeamList.setVisibility(View.GONE);
                            } else {
                                findViewById(R.id.btnOpenTeamMembersBooking).setVisibility(View.GONE);
                                teamsNotAvailTxtMyTeamList.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                    case 1:
                        DialogActivity.alertDialogView(TeamMemberList_Activity.this, "No Network!", "No network connection, Please try again later.");
                        break;
                    case 2:case 3:
                        Functional_Utility.validationErrorMsg(TeamMemberList_Activity.this, responseMsgStr[0]);
                        break;
                }
            }
        }.execute();
    }



}
