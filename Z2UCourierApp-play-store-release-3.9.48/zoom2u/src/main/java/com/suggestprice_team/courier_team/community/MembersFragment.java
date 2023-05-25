package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class MembersFragment extends Fragment implements View.OnClickListener {

    MemberAdapter memberAdapter;
    RecyclerView recycle;
    TextView addMemberBtn;
    private ProgressDialog progressDialogSRView;
    String responseCommunitymemberlist;
    String responseCommunitymemberdelete;
    SwipeRefreshLayout swipeRefreshLayoutNew;
    List<NewCommunitymemberlist.Result> list = new ArrayList<>();
    TextView txtematyMessage;
    int LAUNCH_SECOND_ACTIVITY = 1;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_members, container, false);

        recycle=view.findViewById(R.id.recycle);
        txtematyMessage=view.findViewById(R.id.txtematyMessage);
        addMemberBtn=view.findViewById(R.id.addMemberBtn);
        swipeRefreshLayoutNew=view.findViewById(R.id.swipeRefreshLayoutNew);

        addMemberBtn.setOnClickListener(this);
        swipeRefreshLayoutNew.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                apiCallToGetcommunitymember();
                swipeRefreshLayoutNew.setRefreshing(false);
            }
        });

        apiCallToGetcommunitymember();

        return view;
    }



    private void apiCallToGetcommunitymember() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            getCommunityMembersAsyncTask();
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void callapicommunitydeletemember(int position,String courId) {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            getCommunityDeleteMemberAsyncTask(position,courId);
        else
            DialogActivity.alertDialogView(getContext(), "No Network!", "No network connection, Please try again later.");
    }

    private void getCommunityMembersAsyncTask() {
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

                    if (jsonArray.length()>0) {
                        recycle.setVisibility(View.VISIBLE);
                        txtematyMessage.setVisibility(View.GONE);
                        list.clear();
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
                            result.setVehicle(jsonObject1.getString("Vehicle"));
                            result.setId(jsonObject1.getInt("Id"));

                            list.add(result);
                        }

                        if(list!=null) {
                            setAdapter(list);
                        }else{
                            recycle.setVisibility(View.GONE);
                            txtematyMessage.setVisibility(View.VISIBLE);
                        }

                    }else {
                        recycle.setVisibility(View.GONE);
                        txtematyMessage.setVisibility(View.VISIBLE);
                        Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.execute();
    }

    private void getCommunityDeleteMemberAsyncTask(int position,String courId) {

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
                    responseCommunitymemberdelete = webServiceHandler.getCommunitydeletemembers(courId);
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onPostExecute() {
                try {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogSRView);
                    JSONObject newBookingResponseJObj = new JSONObject(responseCommunitymemberdelete);
                    if (newBookingResponseJObj.getBoolean("success")) {
                        Toast.makeText(getContext(), "Member deleted.", Toast.LENGTH_SHORT).show();
                        memberAdapter.removeitem(position);
                        if(list.size()>0) {
                            recycle.setVisibility(View.VISIBLE);
                            txtematyMessage.setVisibility(View.GONE);
                        }else {
                            recycle.setVisibility(View.GONE);
                            txtematyMessage.setVisibility(View.VISIBLE);
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


    private void setAdapter(List<NewCommunitymemberlist.Result> list) {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recycle.setLayoutManager(linearLayoutManager);
        memberAdapter = new MemberAdapter(getContext(), this.list);
        recycle.setAdapter(memberAdapter);
        memberAdapter.notifyDataSetChanged();

        memberAdapter.setOnItemClickListenerdelete(new MemberAdapter.OnItemClickListenerdelete() {
            @Override
            public void onItemClickdelte(int position,String type) {
                if(type.equals("delete")){
                    if(list.get(position).getCourierId()!=null) {
                        String courId= list.get(position).getCourierId();
                        callapicommunitydeletemember(position,courId);
                    }
                }else {
                    try {
                        Intent intent = new Intent(getActivity(), EditTeamMembersActivity.class);
                        intent.putExtra("firstName",list.get(position).getFirstName());
                        intent.putExtra("lastName",list.get(position).getLastName());
                        intent.putExtra("mobile",list.get(position).getMobile());
                        intent.putExtra("nikName",list.get(position).getNickName());
                        intent.putExtra("userId",list.get(position).getId().toString());
                        intent.putExtra("vehicleType",list.get(position).getVehicle());
                        startActivityForResult(intent, LAUNCH_SECOND_ACTIVITY);
                    }catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                apiCallToGetcommunitymember();
            }
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addMemberBtn:
                Intent intent = new Intent(getContext(),AddmemberActivity.class);
                startActivity(intent);
                break;
        }
    }
}