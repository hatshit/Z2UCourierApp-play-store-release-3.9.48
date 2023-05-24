package com.zoom2u.slidemenu.unallocated_runs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.offer_run_batch.SingleRunActivity;
import com.zoom2u.offer_run_batch.RunBatchActivity;
import com.google.maps.android.SphericalUtil;

import java.util.List;

public class UnallocatedRunBatchListAdapter extends RecyclerView.Adapter<UnallocatedRunBatchListAdapter.MyViewHolder> {

    List<UnAllocatedRuns> unAllocatedRuns;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(String runId);
    }

    public UnallocatedRunBatchListAdapter(Context context, List<UnAllocatedRuns> unAllocatedRuns) {
        this.unAllocatedRuns = unAllocatedRuns;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_unallocated_run, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UnallocatedRunBatchListAdapter.MyViewHolder myViewHolder, int i) {
        if (!unAllocatedRuns.get(i).getCustomer().getCompany().equals(""))
            myViewHolder.tv_company_name.setText(unAllocatedRuns.get(i).getCustomer().getCompany());
        else
            myViewHolder.tv_company_name.setText(unAllocatedRuns.get(i).getCustomer().getContact());
        String runDate = LoginZoomToU.checkInternetwithfunctionality.getDateServer("" + (unAllocatedRuns.get(i).getRunDate()));
        myViewHolder.tv_run_date.setText(runDate);

        myViewHolder.tv_customer_name.setText(" "+unAllocatedRuns.get(i).getCustomer().getContact());

        myViewHolder.tv_delivery_time.setText(" "+unAllocatedRuns.get(i).getStartTime() + "-" + unAllocatedRuns.get(i).getEndTime());
        myViewHolder.tv_price.setText("$"+unAllocatedRuns.get(i).getTotalCourierPayment());

        if (unAllocatedRuns.get(i).getNumberOfRuns() == 1) {
            myViewHolder.RunOrRunBatch.setText("Run");

            try {
                List<LatLng> startEnd = com.google.maps.android.PolyUtil.decode(unAllocatedRuns.get(i).getRoutePolyline());
                double dis = 0.0;
                for (int pos = 0; pos < startEnd.size() - 1; pos++) {
                    dis = dis + SphericalUtil.computeDistanceBetween(startEnd.get(pos), startEnd.get(pos + 1));
                }
                myViewHolder.tv_km_away.setText(Math.round(dis / 1000) + "km");
            }catch (Exception e){

            }
            myViewHolder.RunOrRunBatch.setBackgroundResource(R.drawable.green_background1);
            myViewHolder.textView_run_stop.setText("Number of Stops :");
            myViewHolder.tv_no_stops.setText(" "+unAllocatedRuns.get(i).getNumberOfStops());
        } else {
            myViewHolder.RunOrRunBatch.setText(" RunBatch ");
            myViewHolder.RunOrRunBatch.setBackgroundResource(R.drawable.red_background);
            myViewHolder.textView_run_stop.setText("Number of Runs :");
            myViewHolder.dist.setVisibility(View.GONE);
            myViewHolder.tv_no_stops.setText(" "+unAllocatedRuns.get(i).getNumberOfRuns());
        }

        myViewHolder.root.setOnClickListener(v -> {

            Intent intentNewBooking;
            if (unAllocatedRuns.get(i).getNumberOfRuns() == 1) {
                intentNewBooking = new Intent(context, SingleRunActivity.class);
                intentNewBooking.putExtra("runId", unAllocatedRuns.get(i).getRunId());
            } else {
                intentNewBooking = new Intent(context, RunBatchActivity.class);
                intentNewBooking.putExtra("runBatchId", unAllocatedRuns.get(i).getRunBatchId());
            }
            context.startActivity(intentNewBooking);

        });
    }

    @Override
    public int getItemCount() {
        return unAllocatedRuns == null ? 0 : unAllocatedRuns.size();
    }
    @Override
    public int getItemViewType(int position)
    {
        return position;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_company_name, tv_run_date, tv_customer_name, tv_delivery_time, tv_no_stops,tv_km_away,tv_price,textView_run_stop;
        ConstraintLayout root;
        LinearLayout dist;
        TextView RunOrRunBatch;

        public MyViewHolder(View itemView) {
            super(itemView);
            RunOrRunBatch = itemView.findViewById(R.id.RunOrRunBatch);
            root = itemView.findViewById(R.id.root);
            tv_company_name = itemView.findViewById(R.id.tv_company_name);
            tv_run_date = itemView.findViewById(R.id.tv_run_date);
            tv_customer_name = itemView.findViewById(R.id.tv_customer_name);
            tv_delivery_time = itemView.findViewById(R.id.tv_delivery_time);
            tv_no_stops = itemView.findViewById(R.id.tv_no_stops);
            tv_km_away = itemView.findViewById(R.id.tv_km_away);
            tv_price = itemView.findViewById(R.id.tv_price);
            dist = itemView.findViewById(R.id.dist);
            textView_run_stop = itemView.findViewById(R.id.textView18);
        }
    }
}

