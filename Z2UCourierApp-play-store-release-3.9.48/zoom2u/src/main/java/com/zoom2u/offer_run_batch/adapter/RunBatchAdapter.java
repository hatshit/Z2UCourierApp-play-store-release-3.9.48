package com.zoom2u.offer_run_batch.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.zoom2u.R;
import com.zoom2u.offer_run_batch.SingleRunActivity;
import com.zoom2u.offer_run_batch.model.RunBatchResponse;

import java.util.List;

public class RunBatchAdapter extends RecyclerView.Adapter<RunBatchAdapter.MyViewHolder> {
    List<RunBatchResponse> runBatchResponsens ;
    Context context;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String runId);
    }
    public RunBatchAdapter(Context context,List<RunBatchResponse> runBatchResponses,OnItemClickListener listener) {
        this.runBatchResponsens = runBatchResponses;
        this.context=context;
        this.listener=listener;

    }



    @NonNull
    @Override
    public RunBatchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accept_run_batch, parent, false);
        return new RunBatchAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunBatchAdapter.MyViewHolder myViewHolder, int i) {

       try {
           myViewHolder.run_value.setText( "$"+runBatchResponsens.get(i).getPossibleEarnings());
           myViewHolder.no_of_stops.setText(runBatchResponsens.get(i).getNumberOfStops().toString());
           myViewHolder.tv_end_suburb.setText(runBatchResponsens.get(i).getSuburbArea());
       }catch (Exception e) {
           e.printStackTrace();
       }

       try {
           List<LatLng> startEnd = com.google.maps.android.PolyUtil.decode(runBatchResponsens.get(i).getRoutePolyline());

           double dis = 0.0;
           for (int pos = 0; pos < startEnd.size() - 1; pos++) {
               dis = dis + SphericalUtil.computeDistanceBetween(startEnd.get(pos), startEnd.get(pos + 1));
           }
           myViewHolder.tv_distance.setText(Math.round(dis / 1000) + "km");
       }catch (Exception e){

       }

       myViewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, SingleRunActivity.class);
                intent.putExtra("runId",runBatchResponsens.get(i).getRunId());
                context.startActivity(intent);
            }
        });
        myViewHolder.accept_run.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(runBatchResponsens.get(i).getRunId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return runBatchResponsens == null ? 0 : runBatchResponsens.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_end_suburb,run_value,no_of_stops,tv_distance;
        Button accept_run;
        RelativeLayout root;
        public MyViewHolder(View itemView) {
            super(itemView);
            no_of_stops=itemView.findViewById(R.id.no_of_stops1);
            run_value=itemView.findViewById(R.id.run_value1);
            accept_run=itemView.findViewById(R.id.accept_run);
            tv_end_suburb = itemView.findViewById(R.id.tv_end_suburb1);
            root=itemView.findViewById(R.id.root);
            tv_distance=itemView.findViewById(R.id.tv_distance_km);

        }
    }

}
