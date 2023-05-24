package com.zoom2u.offer_run_batch.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zoom2u.R;

import java.util.List;

/**
 * Created by Mahendra Dabi on 29-07-2021.
 */
public class RunBatchStopsAdapter extends RecyclerView.Adapter<RunBatchStopsAdapter.MyViewHolder> {
    List<String> stopList;

    public RunBatchStopsAdapter(List<String> stopList) {
        this.stopList = stopList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offer_drop_subrubs, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunBatchStopsAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.tv_drop_suburb.setText(stopList.get(i));
    }

    @Override
    public int getItemCount() {
        return stopList == null ? 0 : stopList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_drop_suburb;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_drop_suburb = itemView.findViewById(R.id.tv_drop_suburb);
        }
    }
}
