package com.zoom2u.slidemenu.unallocated_runs;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.webservice.WebserviceHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class UnallocatedRunsFragments extends Fragment {
    private View rootView = null;
    RecyclerView list_recyclerview;
    List<UnAllocatedRuns> unAllocatedRuns;
    ProgressDialog progressDialog;
    String GetRunBatchDetailsresponse;
    TextView no_runs;
    SwipeRefreshLayout swipe_refresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.unallocated_runs, container, false);
        progressDialog = new ProgressDialog(getActivity());
        Custom_ProgressDialogBar.inItProgressBar(progressDialog);
        Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
        swipe_refresh= rootView.findViewById(R.id.swipe_refresh);
        list_recyclerview = rootView.findViewById(R.id.list_recyclerview);
        no_runs = rootView.findViewById(R.id.no_runs);
        list_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        GetUnAllocatedRuns();

        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_refresh.setRefreshing(false);
                GetUnAllocatedRuns();
            }
        });
        return rootView;



    }


    private void GetUnAllocatedRuns() {

        new MyAsyncTasks() {
            @Override
            public void onPreExecute() {
                if (progressDialog != null) progressDialog.show();
            }

            @Override
            public void doInBackground() {
                GetRunBatchDetailsresponse = new WebserviceHandler().getUnAllocateRuns();
            }

            @Override
            public void onPostExecute() {
                try {

                    if (GetRunBatchDetailsresponse != null) {
                        no_runs.setVisibility(View.GONE);
                        Type listType = new TypeToken<List<UnAllocatedRuns>>() {
                        }.getType();
                        unAllocatedRuns = new Gson().fromJson(GetRunBatchDetailsresponse, listType);
                       if (unAllocatedRuns != null && unAllocatedRuns.size()!=0) {
                           List<UnAllocatedRuns> unAllocatedRuns1= new ArrayList<>();
                           for(UnAllocatedRuns un:unAllocatedRuns){
                               if(un.getNumberOfStops()>0){
                                   unAllocatedRuns1.add(un);
                               }
                           }
                         if(unAllocatedRuns1.size()>0)
                           list_recyclerview.setAdapter(new UnallocatedRunBatchListAdapter(getActivity(), unAllocatedRuns1));
                         else
                             no_runs.setVisibility(View.VISIBLE);
                        }else{
                           no_runs.setVisibility(View.VISIBLE);
                       }
                    }else{
                        no_runs.setVisibility(View.VISIBLE);
                    }

                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                } catch (Exception e) {
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialog);
                }
            }
        }.execute();
    }

}
