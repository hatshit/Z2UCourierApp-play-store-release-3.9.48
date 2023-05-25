package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zoom2u.R;
import com.zoom2u.roundedimage.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class sentAdapter extends RecyclerView.Adapter<sentAdapter.ViewHolder>{
    Context context;
  List<SentInvitationList.Result> list;

    private OnItemClickListenersentinvitationdelete listener;

    public sentAdapter(Context context, List<SentInvitationList.Result> list) {
        this.context = context;
        this.list = list;
    }

    public void removeitem(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    public void setListener(OnItemClickListenersentinvitationdelete listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sentinvit, parent, false);
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


        holder.deleteinvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClicksentinvitationdelete(position);
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,userfname,Fname,Lname,userlname;
        RoundedImageView teamMemberImg;
        ImageView deleteinvitation;
        LinearLayout linear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            teamMemberImg=itemView.findViewById(R.id.teamMemberImg);
            linear=itemView.findViewById(R.id.linear);
            Fname=itemView.findViewById(R.id.Fname);
            Lname=itemView.findViewById(R.id.Lname);
            deleteinvitation=itemView.findViewById(R.id.deleteinvitation);
            userfname=itemView.findViewById(R.id.userfname);
        }
    }

    public interface OnItemClickListenersentinvitationdelete {
        void onItemClicksentinvitationdelete(int position);
    }
}
