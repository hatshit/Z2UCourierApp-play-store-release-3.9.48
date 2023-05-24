package com.zoom2u.slidemenu.offerrequesthandlr;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.R;

/**
 * Created by Arun on 8/6/17.
 */

public class Bid_ChatListAdapter extends RecyclerView.Adapter<Bid_ChatListAdapter.MyViewHolder>{

    private final OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(RequestView_DetailPojo item);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtBookingRef, txtDueByTime, txtDropEta, textPickupCompleteListB,textDropoffCompleteListB,txtDueByEta;
        public Button btnNotify;
        ImageView dp,dot_car;
        public MyViewHolder(View itemView) {
            super(itemView);
            txtBookingRef = (TextView) itemView.findViewById(R.id.txtBookingRef);
            dp = itemView.findViewById(R.id.dp);
            dot_car = itemView.findViewById(R.id.dot_car);
            txtDueByTime = (TextView) itemView.findViewById(R.id.txtDueByTime);
            txtDropEta = (TextView) itemView.findViewById(R.id.txtDropEta);
            textPickupCompleteListB = (TextView) itemView.findViewById(R.id.textPickupCompleteListB);
            textDropoffCompleteListB = (TextView) itemView.findViewById(R.id.textDropoffCompleteListB);
            txtDueByEta=(TextView) itemView.findViewById(R.id.txtDueByEta);
            btnNotify = (Button) itemView.findViewById(R.id.btnNotify);

        }

        public void bind(final RequestView_DetailPojo item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public Bid_ChatListAdapter(Context context,OnItemClickListener listener){
        this.listener = listener;
        this.context=context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()) .inflate(R.layout.row_chat, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RequestView_DetailPojo requestView_DetailPojo = RequestView.requestView_pojoList.get(position);
        holder.bind(RequestView.requestView_pojoList.get(position), listener);
        holder.txtDueByEta.setVisibility(View.GONE);
        holder.txtBookingRef.setText(requestView_DetailPojo.getCustomer());
        holder.txtDropEta.setTextColor(Color.parseColor("#00A6E2"));
        if (requestView_DetailPojo.isCancel())
            holder.txtDropEta.setText("Bid chat - Cancelled");
        else
            holder.txtDropEta.setText("Bid chat");

        try {
            if (requestView_DetailPojo.getDropDateTime().equals(""))
                holder.txtDueByTime.setText("Due by - NA");
            else {
                String dropOffTime = requestView_DetailPojo.getDropDateTime();
                String[] dropOffTimeArray = dropOffTime.split(" ");
                holder.txtDueByTime.setText("Due by - " + dropOffTimeArray[2]+" "+ dropOffTimeArray[3]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            holder.txtDueByTime.setText("Due by - NA");
        }
        try{
            if(requestView_DetailPojo.getVehicle().equals("Car"))
                holder.dot_car.setImageResource(R.drawable.ic_from_to_car_icon);
            else if(requestView_DetailPojo.getVehicle().equals("Bike"))
                holder.dot_car.setImageResource(R.drawable.ic_bike_normal_new);
            else	if(requestView_DetailPojo.getVehicle().equals("Van"))
                holder.dot_car.setImageResource(R.drawable.ic_van_normal_new);
        }catch (Exception e){

        }

        try {
            Picasso.with(context).load(requestView_DetailPojo.getCustomerPhoto()).placeholder(R.drawable.profile) // optional
                    .into(holder.dp);
            //Picasso.with(context).load(requestView_DetailPojo.getCustomerPhoto()).into(holder.dp);
            } catch (Exception e) {
                e.printStackTrace();
                holder.dp.setImageResource(R.drawable.profile);
            }

        holder.textPickupCompleteListB.setText(requestView_DetailPojo.getPickupSuburb());
        holder.textDropoffCompleteListB.setText(requestView_DetailPojo.getDropSuburb());



        if (requestView_DetailPojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer() > 0) {
            holder.btnNotify.setVisibility(View.VISIBLE);
            holder.btnNotify.setText("" + requestView_DetailPojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer());
        } else
            holder.btnNotify.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return RequestView.requestView_pojoList.size();
    }
}
