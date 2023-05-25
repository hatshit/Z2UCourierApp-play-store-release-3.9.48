package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.zoom2u.R;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.slidemenu.unallocated_runs.UnAllocatedRuns;

import java.util.ArrayList;
import java.util.List;

public class MemberAdapter extends RecyclerView.Adapter<MemberAdapter.ViewHolder>{
    Context context;
  List<NewCommunitymemberlist.Result> list;

    private OnItemClickListenerdelete listenerdelete;

    public MemberAdapter(Context context, List<NewCommunitymemberlist.Result> list) {
        this.context = context;
        this.list = list;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.number.setText(list.get(position).getMobile());
        holder.email.setText(list.get(position).getEmail());


        if (list.get(position).getPhoto()==null||list.get(position).getPhoto().isEmpty()){
            holder.linear.setVisibility(View.VISIBLE);
            holder.fName.setText(list.get(position).getFirstName().substring(0,1).toUpperCase());
            holder.lName.setText(list.get(position).getLastName().substring(0,1).toLowerCase());
        }
        else {
            Picasso.with(context).load(list.get(position).getPhoto()).into(holder.teamMemberImg);
            holder.linear.setVisibility(View.GONE);
        }

        if (list.get(position).getNickName()==null||list.get(position).getNickName().isEmpty()){
           String name =firstLetterCapital(list.get(position).getFirstName());
            holder.name.setText(name);
        }
        else {
            String nikName =firstLetterCapital(list.get(position).getNickName());
            holder.name.setText(nikName);
        }



        holder.relative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerdelete != null) {
                    listenerdelete.onItemClickdelte(position,"addMembers");
                }
            }
        });

        holder.swipeaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listenerdelete != null) {
                    listenerdelete.onItemClickdelte(position,"delete");
                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name,number,email,fName,lName;
        LinearLayout relative,linear,swipeaccept;
        RoundedImageView teamMemberImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.name);
            number=itemView.findViewById(R.id.number);
            email=itemView.findViewById(R.id.email);
            relative=itemView.findViewById(R.id.relative);
            teamMemberImg=itemView.findViewById(R.id.teamMemberImg);
            linear=itemView.findViewById(R.id.linear);
            lName=itemView.findViewById(R.id.lName);
            fName=itemView.findViewById(R.id.fName);
            swipeaccept=itemView.findViewById(R.id.swipeaccept);

        }
    }

    public interface OnItemClickListenerdelete {
        void onItemClickdelte(int position,String type);
    }


    public String firstLetterCapital(String name)
    {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}
