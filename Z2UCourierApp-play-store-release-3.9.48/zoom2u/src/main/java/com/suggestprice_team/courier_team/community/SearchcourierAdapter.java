package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;

import java.util.List;

public class SearchcourierAdapter extends RecyclerView.Adapter<SearchcourierAdapter.ViewHolder>{
    Context context;
  List<SearchCourierslist.Result> list;

    private int selectedPosition = -1;

    private OnItemClickListener listener;


    public SearchcourierAdapter(Context context, List<SearchCourierslist.Result> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.searchcourier, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.emailtext.setText(list.get(position).getEmail());

        if (selectedPosition == position) {
            //holder.linear.setBackgroundResource(R.color.light_gar);
            holder.linear.setSelected(true);
        } else {
            //holder.linear.setBackgroundResource(R.color.light_gar);
            holder.linear.setSelected(false);
        }

        holder.linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position);

                    if (selectedPosition >= 0)
                        notifyItemChanged(selectedPosition);
                    selectedPosition = holder.getAdapterPosition();
                    notifyItemChanged(selectedPosition);
                    }
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void clearAllData() {
        list.clear();
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView emailtext;
        LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            emailtext=itemView.findViewById(R.id.emailtext);
            linear=itemView.findViewById(R.id.linear);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
