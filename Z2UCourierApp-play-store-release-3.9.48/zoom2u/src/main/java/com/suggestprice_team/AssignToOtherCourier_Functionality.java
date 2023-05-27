package com.suggestprice_team;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.suggestprice_team.courier_team.MyTeamList_Model;
import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.suggestprice_team.courier_team.community.Communitymemberlist;
import com.suggestprice_team.courier_team.community.MemberAdapter;
import com.suggestprice_team.courier_team.community.NewCommunitymemberlist;
import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.AdapterAssignToCommunitymember;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.utility.DividerItemDecoration;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arun on 23-July-2018.
 */

public class AssignToOtherCourier_Functionality {

    Context context;

    int bookingId;
    String courierId = "", courierName;

    String communitycourierid="",communitycouriername;


    private Dialog dialogDriverAllocate;

    private ProgressDialog progressDialogSRView;

    String responseCommunitymemberlist;
    String responseSendBookingOfferToCommunityMember;
    private AdapterAssignToOtherCourier adapterAssignToOtherCourier;
    ProgressDialog progressTeamMemberList;
    boolean isFromActiveBookingList;
    ActiveBookingView activeBookingView;
    ActiveBookingDetail_New activeDetailPage;
    ArrayList<NewCommunitymemberlist.Result> list = new ArrayList<>();

    String isDialog ="";
    String isTabDialog ="team_member";

    public AssignToOtherCourier_Functionality (Context context, ActiveBookingView activeBookingViewList, ActiveBookingDetail_New activeDetailPage, boolean isFromActiveBookingList, int bookingId) {
        this.context = context;
        this.bookingId = bookingId;
        this.activeBookingView = activeBookingViewList;
        this.isFromActiveBookingList = isFromActiveBookingList;
        this.activeDetailPage = activeDetailPage;

        if (TeamMemberList_Activity.arrayOfMyTeamList == null) {
            TeamMemberList_Activity.inItTeamMemberListArray();
            //new GetTeamMemberListAPICall().execute();
            GetTeamMemberListAPICall();
        } else if (TeamMemberList_Activity.arrayOfMyTeamList.size() == 0) {
           // new GetTeamMemberListAPICall().execute();
            GetTeamMemberListAPICall();
        } else {
            //************ Reset Array of My team list *************
            ArrayList<MyTeamList_Model> tempArrayMyTeamList = new ArrayList<MyTeamList_Model>();
            for (MyTeamList_Model myTeamListModel : TeamMemberList_Activity.arrayOfMyTeamList) {
                myTeamListModel.setSetFlagToSelectItem(false);
                tempArrayMyTeamList.add(myTeamListModel);
            }

            TeamMemberList_Activity.inItTeamMemberListArray();
            for (MyTeamList_Model myTeamListModel : tempArrayMyTeamList)
                TeamMemberList_Activity.arrayOfMyTeamList.add(myTeamListModel);

            tempArrayMyTeamList = null;     //************ Reset Array of My team list

            dialogToShowDriversListToAllocateBooking();
        }
    }

    public AssignToOtherCourier_Functionality(Context context,int bookingId) {
        this.context = context;
        this.bookingId=bookingId;

    }

    public void dialogToShowDriversListToAllocateBooking(){
        if (dialogDriverAllocate != null) {
            if (dialogDriverAllocate.isShowing())
                dialogDriverAllocate.dismiss();
            dialogDriverAllocate = null;
        }

        dialogDriverAllocate = new Dialog(context);
        dialogDriverAllocate.setCancelable(false);
        dialogDriverAllocate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
        dialogDriverAllocate.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDriverAllocate.setContentView(R.layout.reassign_courier_dialog);

        Window window = dialogDriverAllocate.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER ;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        CustomRecyclerViewToShowDriver listAllocateDriverAlert = (CustomRecyclerViewToShowDriver) dialogDriverAllocate.findViewById(R.id.listAllocateDriverAlert);
        LinearLayout teammemberlinerar=(LinearLayout)dialogDriverAllocate.findViewById(R.id.teammemberlinerar);
        LinearLayout communitymemberlinear=(LinearLayout)dialogDriverAllocate.findViewById(R.id.communitymemberlinear);
        LinearLayout teammcommunitumlinear=(LinearLayout)dialogDriverAllocate.findViewById(R.id.teammcommunitumlinear);
        TextView titleAllocateDriverAlert = (TextView) dialogDriverAllocate.findViewById(R.id.titleAllocateDriverAlert);
        TextView communitymember = (TextView) dialogDriverAllocate.findViewById(R.id.communitymember);
        TextView teammember = (TextView) dialogDriverAllocate.findViewById(R.id.teammember);
        titleAllocateDriverAlert.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        RecyclerView recycler = (RecyclerView) dialogDriverAllocate.findViewById(R.id.recycler);
        LinearLayout communitynodata=(LinearLayout)dialogDriverAllocate.findViewById(R.id.communitynodata);
        LinearLayout teamnodata=(LinearLayout)dialogDriverAllocate.findViewById(R.id.teamnodata);
        LinearLayout llSend=(LinearLayout)dialogDriverAllocate.findViewById(R.id.llSend);
        LinearLayout dialoglinear=(LinearLayout)dialogDriverAllocate.findViewById(R.id.dialoglinear);

        if (LoginZoomToU.IS_TEAM_LEADER) {
            llSend.setVisibility(View.VISIBLE);
            dialoglinear.setVisibility(View.VISIBLE);
            teammcommunitumlinear.setVisibility(View.VISIBLE);
            titleAllocateDriverAlert.setVisibility(View.VISIBLE);
            titleAllocateDriverAlert.setText("Which courier you want to \nre-assign this booking?");

            isDialog = "double";
            teammcommunitumlinear.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.VISIBLE);
            listAllocateDriverAlert.setVisibility(View.VISIBLE);

            teammemberlinerar.setBackgroundResource(R.drawable.member);
            teammember.setTextColor(ContextCompat.getColor(context, R.color.white));
            communitymember.setTextColor(ContextCompat.getColor(context, R.color.black));


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
            listAllocateDriverAlert.setLayoutManager(mLayoutManager);
            listAllocateDriverAlert.setItemAnimator(new DefaultItemAnimator());
            listAllocateDriverAlert.setNestedScrollingEnabled(false);
            listAllocateDriverAlert.addItemDecoration(new DividerItemDecoration(context, null));

            if (adapterAssignToOtherCourier != null)
                adapterAssignToOtherCourier = null;

            if(TeamMemberList_Activity.arrayOfMyTeamList.size()<=0)
                teamnodata.setVisibility(View.VISIBLE);
            adapterAssignToOtherCourier = new AdapterAssignToOtherCourier(context, TeamMemberList_Activity.arrayOfMyTeamList, new AdapterAssignToOtherCourier.OnItemClickListener() {
                @Override
                public void onItemClick(MyTeamList_Model item, AdapterAssignToOtherCourier.MyViewHolder holder, int position) {

                    courierId = item.getCourierId();
                    courierName = item.getName();
                    communitycourierid="";

                    for (int i = 0; i < TeamMemberList_Activity.arrayOfMyTeamList.size(); i++) {
                        if (TeamMemberList_Activity.arrayOfMyTeamList.get(i).getCourierId().equals(courierId)) {
                            TeamMemberList_Activity.arrayOfMyTeamList.get(i).setSetFlagToSelectItem(true);
                        } else {
                            TeamMemberList_Activity.arrayOfMyTeamList.get(i).setSetFlagToSelectItem(false);
                        }
                    }
                    adapterAssignToOtherCourier.notifyDataSetChanged();
                }
            });

            listAllocateDriverAlert.setAdapter(adapterAssignToOtherCourier);

            teammemberlinerar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    titleAllocateDriverAlert.setText("Which courier you want to \nre-assign this booking?");

                    if(TeamMemberList_Activity.arrayOfMyTeamList.size()<=0)
                        teamnodata.setVisibility(View.VISIBLE);
                    isTabDialog = "team_member";
                    communitynodata.setVisibility(View.GONE);
                    recycler.setVisibility(View.GONE);
                    listAllocateDriverAlert.setVisibility(View.VISIBLE);
                    communitymemberlinear.setBackgroundResource(R.drawable.roundedskybluebg);
                    teammemberlinerar.setBackgroundResource(R.drawable.member);
                    teammember.setTextColor(ContextCompat.getColor(context, R.color.white));
                    communitymember.setTextColor(ContextCompat.getColor(context, R.color.black));

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                    listAllocateDriverAlert.setLayoutManager(mLayoutManager);
                    listAllocateDriverAlert.setItemAnimator(new DefaultItemAnimator());
                    listAllocateDriverAlert.setNestedScrollingEnabled(false);
                    listAllocateDriverAlert.addItemDecoration(new DividerItemDecoration(context, null));

                    if (adapterAssignToOtherCourier != null)
                        adapterAssignToOtherCourier = null;

                    adapterAssignToOtherCourier = new AdapterAssignToOtherCourier(context, TeamMemberList_Activity.arrayOfMyTeamList, new AdapterAssignToOtherCourier.OnItemClickListener() {
                        @Override
                        public void onItemClick(MyTeamList_Model item, AdapterAssignToOtherCourier.MyViewHolder holder, int position) {

                            courierId = item.getCourierId();
                            courierName = item.getName();
                            communitycourierid="";

                            for (int i = 0; i < TeamMemberList_Activity.arrayOfMyTeamList.size(); i++) {
                                if (TeamMemberList_Activity.arrayOfMyTeamList.get(i).getCourierId().equals(courierId)) {
                                    TeamMemberList_Activity.arrayOfMyTeamList.get(i).setSetFlagToSelectItem(true);
                                } else {
                                    TeamMemberList_Activity.arrayOfMyTeamList.get(i).setSetFlagToSelectItem(false);
                                }
                            }
                            adapterAssignToOtherCourier.notifyDataSetChanged();
                        }
                    });

                    listAllocateDriverAlert.setAdapter(adapterAssignToOtherCourier);

                }
            });


            communitymemberlinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    titleAllocateDriverAlert.setText("Which courier you want to offer this booking?");

                    teamnodata.setVisibility(View.GONE);
                    isTabDialog = "community_member";
                    listAllocateDriverAlert.setVisibility(View.GONE);
                    recycler.setVisibility(View.VISIBLE);
                    communitymemberlinear.setBackgroundResource(R.drawable.member);
                    teammemberlinerar.setBackgroundResource(R.drawable.roundedskybluebg);
                    teammember.setTextColor(ContextCompat.getColor(context, R.color.black));
                    communitymember.setTextColor(ContextCompat.getColor(context, R.color.white));

                    CallApiCommunitymember();
                }
            });


        }
        else {
            titleAllocateDriverAlert.setText("Which courier you want to offer this booking?");
            isDialog = "single";
            teammcommunitumlinear.setVisibility(View.GONE);
            recycler.setVisibility(View.VISIBLE);
            listAllocateDriverAlert.setVisibility(View.GONE);

            CallApiCommunitymember();
        }

        Button allocateBtnDriverAllocate = (Button) dialogDriverAllocate.findViewById(R.id.allocateBtnDriverAllocate);
        allocateBtnDriverAllocate.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        allocateBtnDriverAllocate.setTransformationMethod(null);
        allocateBtnDriverAllocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDialog.equalsIgnoreCase("single")){
                    if (communitycourierid.equals("")) {

                        Toast.makeText(context, "Please select a member to assign the booking.", Toast.LENGTH_SHORT).show();
                    } else {
                        dialogDriverAllocate.dismiss();
                        SendBookingOfferToCommunityMember();
                    }
                }else {
                    if (isTabDialog.equalsIgnoreCase("team_member")){
                        if (courierId.equals("")) {
                            DialogActivity.alertDialogViewNew(context, "Alert!", "Please select a member to assign the booking..");
                           // Toast.makeText(context, "Please select a member to assign the booking.", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogDriverAllocate.dismiss();
                            apiCallToAllocateBooking();
                        }
                    }else {
                        if (communitycourierid.equals("")) {
                             DialogActivity.alertDialogViewNew(context, "Alert!", "Please select a member to assign the booking..");
                          //  Toast.makeText(context, "Please select a member to assign the booking.", Toast.LENGTH_SHORT).show();
                        } else {
                            dialogDriverAllocate.dismiss();
                            SendBookingOfferToCommunityMember();
                        }
                    }

                }
            }
        });

        Button cancelBtnDriverAllocate = (Button) dialogDriverAllocate.findViewById(R.id.cancelBtnDriverAllocate);
        Button doneBtn = (Button) dialogDriverAllocate.findViewById(R.id.doneBtn);
        cancelBtnDriverAllocate.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        cancelBtnDriverAllocate.setTransformationMethod(null);
        cancelBtnDriverAllocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDriverAllocate.dismiss();

            }
        });

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDriverAllocate.dismiss();

            }
        });


        dialogDriverAllocate.show();
    }

    //************  Allocate booking to courier ********
    private void apiCallToAllocateBooking(){
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new AssignBookingToOtherTeamMamber().execute();
            AssignBookingToOtherTeamMamber();
        else
            DialogActivity.alertDialogView(context, "No network!", "No network connection, Please try again later.");
    }

    private void AssignBookingToOtherTeamMamber(){
        final String[] responseAssignBookingToOtherCouriers = {""};


        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressTeamMemberList == null)
                    progressTeamMemberList = new ProgressDialog(context);
                Custom_ProgressDialogBar.inItProgressBar(progressTeamMemberList);
            }

            @Override
            public void doInBackground() {
                WebserviceHandler webServiceHandler = new WebserviceHandler();
                responseAssignBookingToOtherCouriers[0] = webServiceHandler.assignBookingToOtherTeamMember(bookingId, courierId);
            }

            @Override
            public void onPostExecute() {
                if (progressTeamMemberList != null)
                    if (progressTeamMemberList.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressTeamMemberList);

                switch (LoginZoomToU.isLoginSuccess) {
                    case 0:
                        dialogDriverAllocate.dismiss();
                        if (isFromActiveBookingList) {
                            WebserviceHandler.reCountActiveBookings(1, 2);
                            activeBookingView.bookingView.showActiveBookingCount();
                            if (activeBookingView != null)
                                activeBookingView.refreshActiveBookingList();
                        } else {
                            if (activeDetailPage != null)
                                activeDetailPage.switchToBookDelivery();

                            Intent bookingCountService = new Intent(context, ServiceForCourierBookingCount.class);
                            bookingCountService.putExtra("Is_API_Call_Require", 2);
                            context.startService(bookingCountService);
                            bookingCountService = null;
                        }

                        Toast.makeText(context, "Your booking has been assigned to " + courierName + " successfully.", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        DialogActivity.alertDialogView(context, "No Network!", "No network connection, Please try again later.");
                        break;
                    case 2:
                        Functional_Utility.validationErrorMsg(context, responseAssignBookingToOtherCouriers[0]);
                        break;
                }
            }
        }.execute();
    }



    private void GetTeamMemberListAPICall(){
        final String[] responseMsgStr = {""};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if(progressTeamMemberList == null)
                    progressTeamMemberList = new ProgressDialog(context);
                Custom_ProgressDialogBar.inItProgressBar(progressTeamMemberList);
            }

            @Override
            public void doInBackground() {
                WebserviceHandler webServiceHandler = new WebserviceHandler();
                responseMsgStr[0] = webServiceHandler.getMyTeamList();
            }

            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onPostExecute() {
                if(progressTeamMemberList != null)
                    if(progressTeamMemberList.isShowing())
                        Custom_ProgressDialogBar.dismissProgressBar(progressTeamMemberList);
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
                                    TeamMemberList_Activity.arrayOfMyTeamList.add(myTeamListModel);
                                }

                                if (TeamMemberList_Activity.arrayOfMyTeamList.size() > 0) {
                                    dialogToShowDriversListToAllocateBooking();
                                }
                                else {
                                    dialogToShowDriversListToAllocateBooking();
                                    //DialogActivity.alertDialogView(context, "Alert!", "No team members available to assign booking. Please add team members and try again.");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                DialogActivity.alertDialogView(context, "Sorry!", "Something went wrong, Please try again later.");
                            }
                        }
                        break;
                    case 1:
                        DialogActivity.alertDialogView(context, "No Network!", "No network connection, Please try again later.");
                        break;
                    case 2:case 3:
                        Functional_Utility.validationErrorMsg(context, responseMsgStr[0]);
                        break;
                }
            }
        }.execute();
    }

    private void CallApiCommunitymember(){

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
            new MyAsyncTasks(){
                @Override
                public void onPreExecute() {
                    try {
                        if (progressDialogSRView == null)
                            progressDialogSRView = new ProgressDialog(context);
                        Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void doInBackground() {
                    try {
                        WebserviceHandler webServiceHandler = new WebserviceHandler();
                        responseCommunitymemberlist = webServiceHandler.getCommunitymemberlist();
                        webServiceHandler = null;

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }

                @Override
                public void onPostExecute() {

                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    try {
                        JSONObject jsonObject = new JSONObject(responseCommunitymemberlist);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");
                        TextView titleAllocateDriverAlert=(TextView)dialogDriverAllocate.findViewById(R.id.titleAllocateDriverAlert);
                        LinearLayout llSend=(LinearLayout)dialogDriverAllocate.findViewById(R.id.llSend);
                        LinearLayout dialoglinear=(LinearLayout)dialogDriverAllocate.findViewById(R.id.dialoglinear);
                        if (jsonArray.length()>0) {
                            llSend.setVisibility(View.VISIBLE);
                            dialoglinear.setVisibility(View.VISIBLE);
                            titleAllocateDriverAlert.setVisibility(View.VISIBLE);
                            list.clear();
                            LinearLayout communitynodata=(LinearLayout)dialogDriverAllocate.findViewById(R.id.communitynodata);
                            communitynodata.setVisibility(View.GONE);
                            for (int i=0; i<jsonArray.length(); i++){

                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                                NewCommunitymemberlist.Result result = new NewCommunitymemberlist.Result();
                                result.setMobile(jsonObject1.getString("Mobile"));
                                result.setEmail(jsonObject1.getString("Email"));
                                result.setPhoto(jsonObject1.getString("Photo"));
                                result.setFirstName(jsonObject1.getString("FirstName"));
                                result.setLastName(jsonObject1.getString("LastName"));
                                result.setNickName(jsonObject1.getString("NickName"));
                                result.setCourierId(jsonObject1.getString("CourierId"));
                                result.setId(jsonObject1.getInt("Id"));

                                list.add(result);

                            }

                            RecyclerView recycler = (RecyclerView) dialogDriverAllocate.findViewById(R.id.recycler);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,RecyclerView.VERTICAL,false);
                            recycler.setLayoutManager(linearLayoutManager);

                            AdapterAssignToCommunitymember adapterAssignToCommunitymember = new AdapterAssignToCommunitymember(context,list);
                            recycler.setAdapter(adapterAssignToCommunitymember);
                            adapterAssignToCommunitymember.notifyDataSetChanged();

                            adapterAssignToCommunitymember.setOnItemClickListener(new AdapterAssignToCommunitymember.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {

                                    communitycourierid= String.valueOf(list.get(position).getId());
                                    communitycouriername=list.get(position).getFirstName();
                                }
                            });

                        }else {
                            Button doneBtn=(Button)dialogDriverAllocate.findViewById(R.id.doneBtn);
                            llSend.setVisibility(View.GONE);
                            titleAllocateDriverAlert.setVisibility(View.VISIBLE);
                            doneBtn.setVisibility(View.VISIBLE);
                            dialoglinear.setVisibility(View.VISIBLE);
                            titleAllocateDriverAlert.setText("No Community members available to assing booking. Please add community members and try agin.");
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                        }

                    }catch (Exception e){

                    }
                }
            }.execute();
        }
        /// new GetSummaryReportAsyncTask().execute();
        else {
            DialogActivity.alertDialogView(context, "No Network!", "No network connection, Please try again later.");
        }
    }

    //===================================================================================================
    //call api SendBookingOfferToCommunityMember

    private void SendBookingOfferToCommunityMember(){
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
            new MyAsyncTasks(){
                @Override
                public void onPreExecute() {
                    try {
                        if (progressDialogSRView == null)
                            progressDialogSRView = new ProgressDialog(context);
                        Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void doInBackground() {
                    try {
                        WebserviceHandler webServiceHandler = new WebserviceHandler();
                        responseSendBookingOfferToCommunityMember = webServiceHandler.SendBookingOfferToCommunityMember(bookingId,communitycourierid);
                        Log.d("jddfjiuj", "doInBackground: "+responseSendBookingOfferToCommunityMember);
                        webServiceHandler = null;

                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
                @SuppressLint("SuspiciousIndentation")
                @Override
                public void onPostExecute() {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    try {
                        JSONObject jsonObject = new JSONObject(responseSendBookingOfferToCommunityMember);
                        if (jsonObject.has("success")){
                            DialogActivity.alertDialogViewNew(context, "Message","Your offer for this booking has been sent to your selected community member." +
                                    "We will notify you when they have accepted or rejected your offer until then the booking will stay assigned to you.");
                        }else {
                            if(jsonObject.getString("Message").toString()!=null)
                                DialogActivity.alertDialogViewNew(context, "Message",jsonObject.getString("Message").toString());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        DialogActivity.alertDialogView(context, "Sorry!", "Something went wrong, Please try again later.");
                    }
                }
            }.execute();
        }
        else {
            DialogActivity.alertDialogView(context, "No Network!", "No network connection, Please try again later.");
        }
    }
}
