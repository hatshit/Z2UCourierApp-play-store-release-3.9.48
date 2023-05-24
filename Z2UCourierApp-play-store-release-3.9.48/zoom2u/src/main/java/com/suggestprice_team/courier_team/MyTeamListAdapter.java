package com.suggestprice_team.courier_team;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.R;

import java.util.List;

/**
 * Created by Arun on 17-July-2018.
 */

public class MyTeamListAdapter extends RecyclerView.Adapter<MyTeamListAdapter.MyViewHolder>{

    List<MyTeamList_Model> arrayOfMyTeamList;
    private final OnItemClickListener listener;
    Context context;

    public interface OnItemClickListener {
        void onItemClick(MyTeamList_Model item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView courierFirstLastNameTeamList, courierName, courierMobile, courierEmail, activeJobCount, activeJobTxt;
        RoundedImageView teamMemberImg;

        public MyViewHolder(View itemView) {
            super(itemView);
            teamMemberImg = (RoundedImageView) itemView.findViewById(R.id.teamMemberImg);
            courierName = (TextView) itemView.findViewById(R.id.teamMemberName);

            courierMobile = (TextView) itemView.findViewById(R.id.teamMemberPhoneNo);

            courierEmail = (TextView) itemView.findViewById(R.id.teamMemberEmail);

            activeJobCount = (TextView) itemView.findViewById(R.id.activeJobCount);

            activeJobTxt = (TextView) itemView.findViewById(R.id.activeJobTxt);

            courierFirstLastNameTeamList = (TextView) itemView.findViewById(R.id.courierFirstLastNameTeamList);

        }

        public void bind(final MyTeamList_Model item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public MyTeamListAdapter(List<MyTeamList_Model> arrayOfMyTeamList, OnItemClickListener listener, Context context){
        this.arrayOfMyTeamList = arrayOfMyTeamList;
        this.listener = listener;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.row_teamlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        try {
            MyTeamList_Model myTeamList_Model = arrayOfMyTeamList.get(position);
            holder.bind(arrayOfMyTeamList.get(position), listener);
            holder.courierName.setText(myTeamList_Model.getName());
            holder.courierMobile.setText(myTeamList_Model.getMobile());
            holder.courierEmail.setText(myTeamList_Model.getEmail());
            holder.activeJobCount.setText(""+myTeamList_Model.getActiveJobCount());
            try {
                holder.courierFirstLastNameTeamList.setText(((String)((myTeamList_Model.getFirstName().charAt(0)+""+myTeamList_Model.getLastName().charAt(0)))).toUpperCase());
            } catch (Exception e) {
                e.printStackTrace();
                holder.teamMemberImg.setImageResource(R.drawable.user_icon_myteam);
            }
            try {
                Picasso.with(context).load(myTeamList_Model.getPhoto()).fit().centerInside().into(holder.teamMemberImg);
                holder.courierFirstLastNameTeamList.setVisibility(View.GONE);
            } catch (Exception e) {
            //    holder.teamMemberImg.setImageResource(R.drawable.user_icon_myteam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
            return arrayOfMyTeamList.size();
    }
}