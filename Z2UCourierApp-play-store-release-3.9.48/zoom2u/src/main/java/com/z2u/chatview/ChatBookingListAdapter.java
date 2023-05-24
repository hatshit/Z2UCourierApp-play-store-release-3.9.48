package com.z2u.chatview;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.slidemenu.BookingView;

import java.util.ArrayList;

public class ChatBookingListAdapter extends RecyclerView.Adapter<ChatBookingListAdapter.MyViewHolder>{

    ArrayList<Model_DeliveriesToChat> arrayModelChatList;
    private final OnItemClickListener listener;
    private Context context;
    public interface OnItemClickListener {
        void onItemClick(Model_DeliveriesToChat item);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtBookingRef, txtDueByTime, txtDropEta,textDropoffCompleteListB,textPickupCompleteListB,txtDueByEta;
        RelativeLayout txtPickUpDropOffSuburb;
        public Button btnNotify;
        ImageView dp,dot_car;


        public MyViewHolder(View itemView) {
            super(itemView);
            dp = itemView.findViewById(R.id.dp);
            dot_car = itemView.findViewById(R.id.dot_car);
            txtBookingRef = (TextView) itemView.findViewById(R.id.txtBookingRef);
            txtDueByTime = (TextView) itemView.findViewById(R.id.txtDueByTime);
            txtDropEta = (TextView) itemView.findViewById(R.id.txtDropEta);
            txtPickUpDropOffSuburb = (RelativeLayout) itemView.findViewById(R.id.txtPickUpDropOffSuburb);
            btnNotify = (Button) itemView.findViewById(R.id.btnNotify);
            textPickupCompleteListB=(TextView) itemView.findViewById(R.id.textPickupCompleteListB);
            textDropoffCompleteListB=(TextView) itemView.findViewById(R.id.textDropoffCompleteListB);
            txtDueByEta=(TextView) itemView.findViewById(R.id.txtDueByEta);
        }

        public void bind(final Model_DeliveriesToChat item, final OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public ChatBookingListAdapter(Context context,ArrayList<Model_DeliveriesToChat> arrayModelChatList, OnItemClickListener listener){
        this.arrayModelChatList = arrayModelChatList;
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
        try {
            Model_DeliveriesToChat model_deliveriesToChat = arrayModelChatList.get(position);
            holder.bind(arrayModelChatList.get(position), listener);
            if (position == 0) {
                holder.txtBookingRef.setText("Zoom2u Admin");
                holder.txtDueByTime.setText("Chat with Zoom2u Customer Service");
                holder.txtDropEta.setVisibility(View.GONE);
                holder.txtPickUpDropOffSuburb.setVisibility(View.GONE);
                holder.dp.setVisibility(View.GONE);
                holder.txtDueByEta.setVisibility(View.GONE);
            }else {
                holder.txtBookingRef.setText(model_deliveriesToChat.getCustomer());
                if (model_deliveriesToChat.getDropDateTime() != null) {
                    try {
                        String[] deliverTimeStr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(model_deliveriesToChat.getDropDateTime()).split(" ");
                        holder.txtDueByTime.setText("Due by "+deliverTimeStr[1]+" "+deliverTimeStr[2]);
                        deliverTimeStr = null;
                    } catch (Exception e) {
                        holder.txtDueByTime.setText("Due by - NA");
                        e.printStackTrace();
                    }
                }else
                    holder.txtDueByTime.setText("Due by - NA");


                if (model_deliveriesToChat.getPickupETA() != null) {
                    try {
                        String[] deliverTimeStr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(model_deliveriesToChat.getPickupETA()).split(" ");
                        holder.txtDueByEta.setText("Eta for pickup "+deliverTimeStr[1]+" "+deliverTimeStr[2]);
                        deliverTimeStr = null;
                    } catch (Exception e) {
                        holder.txtDueByEta.setText("Eta for pickup - NA");
                        e.printStackTrace();
                    }
                }else
                    holder.txtDueByEta.setText("Eta for pickup - NA");


                holder.txtDropEta.setText("Booking chat");

                try{
                    if(model_deliveriesToChat.getVehicle().equals("Car"))
                        holder.dot_car.setImageResource(R.drawable.ic_from_to_car_icon);
                    else if(model_deliveriesToChat.getVehicle().equals("Bike"))
                        holder.dot_car.setImageResource(R.drawable.ic_bike_normal_new);
                    else if(model_deliveriesToChat.getVehicle().equals("Van"))
                        holder.dot_car.setImageResource(R.drawable.ic_van_normal_new);
                }catch (Exception e){

                }

                if(!model_deliveriesToChat.getCustomerPhoto().equals("") &&model_deliveriesToChat.getCustomerPhoto()!=null) {
                    try {
                        Picasso.with(context).load(model_deliveriesToChat.getCustomerPhoto()).placeholder(R.drawable.profile) // optional
                                .into(holder.dp);
                        //Picasso.with(context).load(model_deliveriesToChat.getCustomerPhoto()).into(holder.dp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                holder.textPickupCompleteListB.setText(model_deliveriesToChat.getPickupSuburb());
                holder.textDropoffCompleteListB.setText(model_deliveriesToChat.getDropSuburb());
                holder.txtPickUpDropOffSuburb.setVisibility(View.VISIBLE);
            }

            if (model_deliveriesToChat.getUnreadMsgCountOfCustomer() > 0) {
                holder.btnNotify.setVisibility(View.VISIBLE);
                holder.btnNotify.setText("" + model_deliveriesToChat.getUnreadMsgCountOfCustomer());
            }else
                holder.btnNotify.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (arrayModelChatList != null)
            return arrayModelChatList.size();
        else
            return 0;
    }
}
