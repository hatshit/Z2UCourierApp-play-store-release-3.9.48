package com.suggestprice_team.courier_team.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.slidemenu.BookingView;

import java.util.ArrayList;
import java.util.List;

public class CommunityBookingOfferAdapter extends RecyclerView.Adapter<CommunityBookingOfferAdapter.ViewHolder> {

    Context context;

    private OnItemClickListener listener;

    //ArrayList<OfferBookingList.List> list = new ArrayList<>();
    ArrayList<All_Bookings_DataModels> list;

    public CommunityBookingOfferAdapter(Context context, ArrayList<All_Bookings_DataModels> list) {
        this.context = context;
        this.list = list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void removeitem(int position){
        list.remove(position);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.communitybookingoffer, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        try {
            double totalWeight=0.0;
            if ((list.get(position)).getShipmentsArray().size() > 0) {
                for (int i = 0; i < (list.get(position)).getShipmentsArray().size(); i++) {
                    totalWeight = totalWeight + (double)(list.get(position)).getShipmentsArray().get(i).get("TotalWeightKg");
                }
                holder.weight.setText("Total weight : "+totalWeight+"kg");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try
        {
            String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiffActive((list.get(position)).getDropDateTime(), false,holder.menuLogTimerTxtForNBList);
            String bookingCreateTimeNewBookingDetail = null,bookingCreateDateNewBookingDetail = null;
            bookingCreateTimeNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer(""+(list.get(position).getPickupDateTime()));
            bookingCreateDateNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getDateFromServer(""+(list.get(position).getPickupDateTime()));

            holder.userNameBList.setText(list.get(position).getPick_ContactName());
            holder.menuLogTimerTxtForNBList.setText(minInStr);
            holder.bookingCreatedTime.setText(""+bookingCreateTimeNewBookingDetail +" | "+bookingCreateDateNewBookingDetail);
            holder.orderNumberNBL.setText(list.get(position).getOrderNumber());
            holder.textPickupBList.setText(list.get(position).getPick_Suburb());
            holder.textDropoffBList.setText(list.get(position).getDrop_Suburb());
            holder.locationMarkBList.setText(list.get(position).getDistance());
            holder.timeToArriveInBookingList.setText(list.get(position).getDeliverySpeed());
            holder.chargesBList.setText("$"+list.get(position).getPrice()+"");
            holder.delivryNotesBookingList.setText(list.get(position).getNotes());


        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            holder.suburbTextnewBooking.setText("");
            if (list.get(position).getShipmentsArray().size() > 0) {
                for (int i = 0; i < list.get(position).getShipmentsArray().size(); i++) {
                    if (i < 3) {
                        if (i == 0)
                            holder.suburbTextnewBooking.append(list.get(position).getShipmentsArray().get(i).get("Category") + " (" + list.get(position).getShipmentsArray().get(i).get("Quantity") + ")");
                        else
                            holder.suburbTextnewBooking.append(", " + list.get(position).getShipmentsArray().get(i).get("Category") + " (" + list.get(position).getShipmentsArray().get(i).get("Quantity") + ")");
                    } else {
                        holder.suburbTextnewBooking.append("...");
                        break;
                    }
                }
            } else
                holder.suburbTextnewBooking.append("" + list.get(position).getPackage());
        } catch (Exception e) {
            e.printStackTrace();
            holder.suburbTextnewBooking.append("" + list.get(position).getPackage());
        }


        if((list.get(position)).getVehicle().equals("Car"))
            holder.dot_car.setImageResource(R.drawable.ic_from_to_car_icon);
        else if((list.get(position)).getVehicle().equals("Bike"))
            holder.dot_car.setImageResource(R.drawable.ic_bike_normal_new);
        else	if((list.get(position)).getVehicle().equals("Van"))
            holder.dot_car.setImageResource(R.drawable.ic_van_normal_new);

        holder.swipeaccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position,"swipeAccept");
                }
            }
        });

        holder.swipereject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position,"swipeReject");
                }

            }
        });

        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(position,"details");
                }
            }
        });

        holder.imglocationMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String address=((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address();
                    address = address.replace(' ', '+');
                    Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
                    context.startActivity(geoIntent);    // Initiate lookup
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout swipeaccept,swipereject;
        RelativeLayout mainLayout;
        TextView userNameBList,bookingCreatedTime,suburbTextnewBooking,weight,
                orderNumberNBL,textPickupBList,textDropoffBList,chargesBList,
                timeToArriveInBookingList,menuLogTimerTxtForNBList,locationMarkBList;
        ReadMoreTextView delivryNotesBookingList;
        ImageView dot_car,imglocationMark;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            swipeaccept=itemView.findViewById(R.id.swipeaccept);
            swipereject=itemView.findViewById(R.id.swipereject);
            mainLayout=itemView.findViewById(R.id.mainLayout);
            userNameBList=itemView.findViewById(R.id.userNameBList);
            bookingCreatedTime=itemView.findViewById(R.id.bookingCreatedTime);
            suburbTextnewBooking=itemView.findViewById(R.id.suburbTextnewBooking);
            weight=itemView.findViewById(R.id.weight);
            orderNumberNBL=itemView.findViewById(R.id.orderNumberNBL);
            textPickupBList=itemView.findViewById(R.id.textPickupBList);
            textDropoffBList=itemView.findViewById(R.id.textDropoffBList);
            chargesBList=itemView.findViewById(R.id.chargesBList);
            timeToArriveInBookingList=itemView.findViewById(R.id.timeToArriveInBookingList);
            menuLogTimerTxtForNBList=itemView.findViewById(R.id.menuLogTimerTxtForNBList);
            delivryNotesBookingList=itemView.findViewById(R.id.delivryNotesBookingList);
            dot_car=itemView.findViewById(R.id.dot_car);
            imglocationMark=itemView.findViewById(R.id.imglocationMark);
            locationMarkBList=itemView.findViewById(R.id.locationMarkBList);

        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position,String type);
    }


}
