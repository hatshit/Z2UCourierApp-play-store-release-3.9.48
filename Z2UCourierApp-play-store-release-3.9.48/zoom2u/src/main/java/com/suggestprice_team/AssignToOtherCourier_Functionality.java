package com.suggestprice_team;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.suggestprice_team.courier_team.MyTeamList_Model;
import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.z2u.booking.vc.ActiveBookingView;
import com.zoom2u.ActiveBookingDetail_New;
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

/**
 * Created by Arun on 23-July-2018.
 */

public class AssignToOtherCourier_Functionality {

    Context context;

    int bookingId;
    String courierId = "", courierName;

    private Dialog dialogDriverAllocate;
    private AdapterAssignToOtherCourier adapterAssignToOtherCourier;

    ProgressDialog progressTeamMemberList;
    boolean isFromActiveBookingList;
    ActiveBookingView activeBookingView;
    ActiveBookingDetail_New activeDetailPage;

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

    private void dialogToShowDriversListToAllocateBooking(){
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

        TextView titleAllocateDriverAlert = (TextView) dialogDriverAllocate.findViewById(R.id.titleAllocateDriverAlert);
        titleAllocateDriverAlert.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

        CustomRecyclerViewToShowDriver listAllocateDriverAlert = (CustomRecyclerViewToShowDriver) dialogDriverAllocate.findViewById(R.id.listAllocateDriverAlert);

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

        Button allocateBtnDriverAllocate = (Button) dialogDriverAllocate.findViewById(R.id.allocateBtnDriverAllocate);
        allocateBtnDriverAllocate.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        allocateBtnDriverAllocate.setTransformationMethod(null);
        allocateBtnDriverAllocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courierId.equals(""))
                    Toast.makeText(context, "Please select a team member", Toast.LENGTH_LONG).show();
                else {
                    dialogDriverAllocate.dismiss();
                    apiCallToAllocateBooking();
                }
            }
        });

        Button cancelBtnDriverAllocate = (Button) dialogDriverAllocate.findViewById(R.id.cancelBtnDriverAllocate);
        cancelBtnDriverAllocate.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        cancelBtnDriverAllocate.setTransformationMethod(null);
        cancelBtnDriverAllocate.setOnClickListener(new View.OnClickListener() {
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

                                if (TeamMemberList_Activity.arrayOfMyTeamList.size() > 0)
                                    dialogToShowDriversListToAllocateBooking();
                                else
                                    DialogActivity.alertDialogView(context, "Alert!", "No team members available to assign booking. Please add team members and try again.");
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

}
