package com.zoom2u;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.suggestprice_team.courier_team.community.NewCommunitymemberlist;

import java.util.ArrayList;

public class AdapterAssignToCommunitymember extends RecyclerView.Adapter<AdapterAssignToCommunitymember.ViewHolder>{
    Context context;
    ArrayList<NewCommunitymemberlist.Result> list;
    private int selectedPosition = -1;

    private OnItemClickListener listener;

    public AdapterAssignToCommunitymember(Context context, ArrayList<NewCommunitymemberlist.Result> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.assigncommunitymember, parent, false);
        return new ViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.driverNameTxt.setText(list.get(position).getFirstName());

        if (selectedPosition == position) {
            holder.checkBoxForDriverSelection.setImageResource(R.drawable.assign_courier_selected);
            holder.itemView.setSelected(true);
        } else {
            holder.checkBoxForDriverSelection.setImageResource(R.drawable.assign_courier_unselect);
            holder.itemView.setSelected(false);
        }


        holder.selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedPosition >= 0)
                    notifyItemChanged(selectedPosition);
                selectedPosition = holder.getAdapterPosition();
                notifyItemChanged(selectedPosition);


                if (listener != null) {
                    listener.onItemClick(position);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView checkBoxForDriverSelection;
        TextView driverNameTxt;
        RelativeLayout selected;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBoxForDriverSelection=itemView.findViewById(R.id.checkBoxForDriverSelection);
            driverNameTxt=itemView.findViewById(R.id.driverNameTxt);
            selected=itemView.findViewById(R.id.selected);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
