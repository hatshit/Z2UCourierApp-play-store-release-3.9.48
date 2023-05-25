package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class InvitationsFragment extends Fragment {

    RecyclerView recievedrecyclerview,sentrecyclerview;
    LinearLayout recivedinvitationheader,sentinvitationheader,noinvitation;
    TextView seeall,sentnodata,recivenodata,seeallsentinvitation;
    private ProgressDialog progressDialogSRView;
    String responseCommunityrecivedinvitation;
    String responseCommunitysentinvitation;
    String responseCommunityacceptmember;
    SwipeRefreshLayout refreshsentinvitation,refreshrecivedinvitation;
    String responseCommunitydeletemember;
    String responseCommunityMemberReject;
    String responseCommunitysentinvitationdeletemember;
    int userid;
    String deletuserid="";

    String sentinvitationcourierid= "";

    InvitationAdapter invitationAdapter;
    sentAdapter sentAdapter;
  List<ReceivedInvitationslist.Result> receivedinvitationlist = new ArrayList<>();

  List<SentInvitationList.Result> sentinvitationlist = new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_invitations, container, false);

        receivedinvitationlist.clear();
        sentinvitationlist.clear();
        recievedrecyclerview=view.findViewById(R.id.recievedrecyclerview);
        sentrecyclerview=view.findViewById(R.id.sentrecyclerview);
        seeall=view.findViewById(R.id.seeall);
        recivenodata=view.findViewById(R.id.recivenodata);
        sentnodata=view.findViewById(R.id.sentnodata);
        seeallsentinvitation=view.findViewById(R.id.seeallsentinvitation);
        sentinvitationheader=view.findViewById(R.id.sentinvitationheader);
        recivedinvitationheader=view.findViewById(R.id.recivedinvitationheader);
        noinvitation=view.findViewById(R.id.noinvitation);
        refreshrecivedinvitation=view.findViewById(R.id.refreshrecivedinvitation);

        if (receivedinvitationlist.size()<=0 || sentinvitationlist.size()<=0){
            noinvitation.setVisibility(View.VISIBLE);
        }
        refreshrecivedinvitation.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiCallToGetcommunityrecivedinvitation();
               //  apiCallToGetcommunitysentinvitation();
                refreshrecivedinvitation.setRefreshing(false);
            }
        });


        //recived invitation
        apiCallToGetcommunityrecivedinvitation();

        return view;
    }

    //=========================================================================================
    //recived invitation
    private void apiCallToGetcommunityrecivedinvitation() {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunityrecivedAsyncTask();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void GetcommunityrecivedAsyncTask() {

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(getContext());
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityrecivedinvitation = webServiceHandler.getCommunityReceivedInvitations();
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onPostExecute() {
                //sent invitation
                apiCallToGetcommunitysentinvitation();
               // Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                try {

                    JSONObject jsonObject = new JSONObject(responseCommunityrecivedinvitation);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");

                    if (jsonArray.length()>0) {
                        receivedinvitationlist.clear();
                        noinvitation.setVisibility(View.GONE);
                        recivedinvitationheader.setVisibility(View.VISIBLE);
                        recievedrecyclerview.setVisibility(View.VISIBLE);
                        for (int i = 0; i<jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            ReceivedInvitationslist.Result result = new ReceivedInvitationslist.Result();
                            result.set$id(jsonObject1.getString("Id"));
                            result.setId(jsonObject1.getInt("Id"));
                            result.setCourierId(jsonObject1.getString("CourierId"));
                            result.setUserId(jsonObject1.getString("UserId"));
                            result.setNickName(jsonObject1.getString("NickName"));
                            result.setFirstName(jsonObject1.getString("FirstName"));
                            result.setLastName(jsonObject1.getString("LastName"));
                            result.setMobile(jsonObject1.getString("Mobile"));
                            result.setEmail(jsonObject1.getString("Email"));
                            result.setPhoto(jsonObject1.getString("Photo"));

                            receivedinvitationlist.add(result);
                        }

                         if(receivedinvitationlist!=null) {
                             setAdapterReceivedInvitation(receivedinvitationlist);
                         }

                    }else {
                        recivedinvitationheader.setVisibility(View.GONE);
                        recievedrecyclerview.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                        e.printStackTrace();
                }
            }
        }.execute();
    }

    private void setAdapterReceivedInvitation(List<ReceivedInvitationslist.Result> receivedinvitationlist) {

        recievedrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        invitationAdapter=new InvitationAdapter(getContext(), receivedinvitationlist);
        recievedrecyclerview.setAdapter(invitationAdapter);
        invitationAdapter.notifyDataSetChanged();
        showHide();

        seeall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                invitationAdapter.setSeeall(!invitationAdapter.seeall);
                invitationAdapter.notifyDataSetChanged();
                boolean seeAll = invitationAdapter.seeall;
                if (seeAll)
                    seeall.setText("See less");
                else seeall.setText("See more");

            }
        });


        invitationAdapter.setOnItemClickListener(new InvitationAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                try {
                    userid= receivedinvitationlist.get(position).getId();
                    callapicommunityacceptmember(position);
                }catch (Exception e){

                }
            }

        });

        invitationAdapter.setOnItemClickListenerdelete(new InvitationAdapter.OnItemClickListenerdelete() {
            @Override
            public void onItemClickdelte(int position) {
                try {
                    deletuserid = receivedinvitationlist.get(position).getCourierId();
                    callapicommunitydeletemember(position);
                }catch (Exception e){

                }
            }
        });

    }

    private void showHide() {
        if (invitationAdapter.list.size()<5){
            seeall.setVisibility(View.GONE);
        }else {
            seeall.setVisibility(View.VISIBLE);
        }

    }

    //==========================================================================================
    //sent invitation

    private void apiCallToGetcommunitysentinvitation() {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunitysentAsyncTask();
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void GetcommunitysentAsyncTask() {

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(getContext());
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunitysentinvitation = webServiceHandler.getCommunitySentInvitations();
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                try {
                    JSONObject jsonObject = new JSONObject(responseCommunitysentinvitation);
                    JSONArray jsonArray = jsonObject.getJSONArray("list");
                    if (jsonArray.length()>0) {
                        sentinvitationlist.clear();
                        noinvitation.setVisibility(View.GONE);
                        sentrecyclerview.setVisibility(View.VISIBLE);
                        sentinvitationheader.setVisibility(View.VISIBLE);
                        for (int i = 0; i<jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            SentInvitationList.Result result = new SentInvitationList.Result();
                            result.set$id(jsonObject1.getString("Id"));
                            result.setCourierId(jsonObject1.getString("CourierId"));
                            result.setUserId(jsonObject1.getString("UserId"));
                            result.setNickName(jsonObject1.getString("NickName"));
                            result.setFirstName(jsonObject1.getString("FirstName"));
                            result.setLastName(jsonObject1.getString("LastName"));
                            result.setMobile(jsonObject1.getString("Mobile"));
                            result.setEmail(jsonObject1.getString("Email"));
                            result.setPhoto(jsonObject1.getString("Photo"));

                            sentinvitationlist.add(result);
                        }
                        setAdapterSentInvitation(sentinvitationlist);
                    }else {
                        sentinvitationheader.setVisibility(View.GONE);
                        if(receivedinvitationlist.size()>0) {
                            noinvitation.setVisibility(View.GONE);
                        }else {
                            noinvitation.setVisibility(View.VISIBLE);
                        }
                        sentrecyclerview.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                       e.printStackTrace();
                }

            }
        }.execute();
    }

    private void setAdapterSentInvitation(List<SentInvitationList.Result> sentinvitationlist) {
        sentrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        sentAdapter=new sentAdapter(getContext(), this.sentinvitationlist);
        sentrecyclerview.setAdapter(sentAdapter);
        sentAdapter.notifyDataSetChanged();
        sentAdapter.setListener(new sentAdapter.OnItemClickListenersentinvitationdelete() {
            @Override
            public void onItemClicksentinvitationdelete(int position) {
                try {
                    sentinvitationcourierid = InvitationsFragment.this.sentinvitationlist.get(position).getCourierId();
                    callapicommunitysentinvitationdeletemember(position);

                }catch (Exception e){
                  e.printStackTrace();
                }
            }
        });

    }

    //===============================================================================================
    //accept member
    private void callapicommunityacceptmember(int position) {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunityacceptmemberAsyncTask(position);
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void GetcommunityacceptmemberAsyncTask(int position) {

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(getContext());
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityacceptmember = webServiceHandler.getCommunityacceptmember(userid);
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {

                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    Toast.makeText(getContext(), "Invitation accepted succesfully", Toast.LENGTH_SHORT).show();
                    invitationAdapter.removeitem(position);
                    showHide();
                    if(receivedinvitationlist.size()>0) {
                        recivedinvitationheader.setVisibility(View.VISIBLE);
                        recievedrecyclerview.setVisibility(View.VISIBLE);
                        noinvitation.setVisibility(View.GONE);
                    }else {
                        recivedinvitationheader.setVisibility(View.GONE);
                        recievedrecyclerview.setVisibility(View.GONE);
                        noinvitation.setVisibility(View.VISIBLE);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.execute();
    }

    ///=================================================================================================
    //delete member

    private void callapicommunitydeletemember(int position) {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunitydeletememberAsyncTask(position);
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void GetcommunitydeletememberAsyncTask(int position) {

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(getContext());
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunityMemberReject = webServiceHandler.getCommunityrejectmember(deletuserid);
                    webServiceHandler = null;

                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                try {
                    JSONObject newBookingResponseJObj = new JSONObject(responseCommunityMemberReject);
                    if (newBookingResponseJObj.getBoolean("success")) {
                        Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                        Toast.makeText(getContext(), "Invitation deleted", Toast.LENGTH_SHORT).show();
                        invitationAdapter.removeitem(position);
                        showHide();
                        if(receivedinvitationlist.size()>0) {
                            recivedinvitationheader.setVisibility(View.VISIBLE);
                            recievedrecyclerview.setVisibility(View.VISIBLE);
                            noinvitation.setVisibility(View.GONE);
                        }else {
                            recivedinvitationheader.setVisibility(View.GONE);
                            recievedrecyclerview.setVisibility(View.GONE);
                            if(sentinvitationlist.size()>0) {
                                noinvitation.setVisibility(View.GONE);
                            }else {
                                noinvitation.setVisibility(View.VISIBLE);
                            }
                        }
                    }else {
                        if(newBookingResponseJObj.getString("Message")!=null)
                            Toast.makeText(getActivity(),newBookingResponseJObj.getString("Message").toString(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                     e.printStackTrace();
                }

            }
        }.execute();
    }


    ///=================================================================================================
    //sent invitation delete member

    private void callapicommunitysentinvitationdeletemember(int position) {

        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            GetcommunitysentinvitationdeletememberAsyncTask(position);
            /// new GetSummaryReportAsyncTask().execute();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void GetcommunitysentinvitationdeletememberAsyncTask(int position) {

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogSRView == null)
                        progressDialogSRView = new ProgressDialog(getContext());
                    Custom_ProgressDialogBar.inItProgressBar(progressDialogSRView);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    responseCommunitysentinvitationdeletemember = webServiceHandler.getCommunitysentinvitationdeletemember(sentinvitationcourierid);
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    JSONObject newBookingResponseJObj = new JSONObject(responseCommunitysentinvitationdeletemember);
                    if (newBookingResponseJObj.getBoolean("success")) {
                        Toast.makeText(getContext(), "Invitation deleted", Toast.LENGTH_SHORT).show();
                        sentAdapter.removeitem(position);
                        if(sentinvitationlist.size()>0) {
                            sentinvitationheader.setVisibility(View.VISIBLE);
                            sentrecyclerview.setVisibility(View.VISIBLE);
                            noinvitation.setVisibility(View.GONE);
                        }else {
                            sentinvitationheader.setVisibility(View.GONE);
                            sentrecyclerview.setVisibility(View.GONE);

                            if(receivedinvitationlist.size()>0) {
                                noinvitation.setVisibility(View.GONE);
                            }else {
                                noinvitation.setVisibility(View.VISIBLE);
                            }
                        }
                    }else {
                        if(newBookingResponseJObj.getString("Message")!=null)
                            Toast.makeText(getActivity(),newBookingResponseJObj.getString("Message").toString(), Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                   e.printStackTrace();
                }
            }
        }.execute();
    }

}