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

import com.squareup.picasso.Picasso;
import com.zoom2u.R;
import com.zoom2u.roundedimage.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder>{
    Context context;
    List<ReceivedInvitationslist.Result> list;

     boolean seeall = false;
    private OnItemClickListener listener;
    private OnItemClickListenerdelete listenerdelete;

    public void setSeeall(boolean seeall) {
        this.seeall = seeall;
    }

    public InvitationAdapter(Context context, List<ReceivedInvitationslist.Result> list) {
        this.context = context;
        this.list = list;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemClickListenerdelete(OnItemClickListenerdelete listenerdelete) {
        this.listenerdelete = listenerdelete;
    }

    public void removeitem(int position){
        list.remove(position);
       notifyDataSetChanged();
    }





    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivedinvitations, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        String fnamelname=list.get(position).getFirstName()+" "+list.get(position).getLastName();
        holder.userfname.setText(fnamelname);

        if (list.get(position).getPhoto()==null||list.get(position).getPhoto().isEmpty()){
            holder.linear.setVisibility(View.VISIBLE);
            holder.Fname.setText(list.get(position).getFirstName().substring(0,1));
            holder.Lname.setText(list.get(position).getLastName().substring(0,1));

        }
        else {
            Picasso.with(context).load(list.get(position).getPhoto()).into(holder.teamMemberImg);
            holder.linear.setVisibility(View.GONE);

        }

        if (list.get(position).getNickName()==null||list.get(position).getNickName().isEmpty()){
            holder.name.setText(list.get(position).getFirstName());
        }
        else {
            holder.name.setText(list.get(position).getNickName());
        }

        holder.linearaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            }
        });

        holder.lineardelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerdelete != null) {
                    listenerdelete.onItemClickdelte(position);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        if (list.size() > 4&& !seeall) {
            return 4;
        } else {
            return list.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,Fname,Lname,userfname;
        RoundedImageView teamMemberImg;
        LinearLayout linear,swipeaccept,swipereject,linearaccept,lineardelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            teamMemberImg=itemView.findViewById(R.id.teamMemberImg);
            linear=itemView.findViewById(R.id.linear);
            Lname=itemView.findViewById(R.id.Lname);
            Fname=itemView.findViewById(R.id.Fname);
            userfname=itemView.findViewById(R.id.userfname);
            swipereject=itemView.findViewById(R.id.swipereject);
            swipeaccept=itemView.findViewById(R.id.swipeaccept);
            linearaccept=itemView.findViewById(R.id.linearaccept);
            lineardelete=itemView.findViewById(R.id.lineardelete);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemClickListenerdelete {
        void onItemClickdelte(int position);
    }

}
