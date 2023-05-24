package com.zoom2u.offer_run_batch.adapter;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.datamodels.All_Bookings_DataModels;

import java.util.List;

/**
 * Created by Mahendra Dabi on 29-07-2021.
 */
public class RunBatchBookingAdapter extends RecyclerView.Adapter<RunBatchBookingAdapter.MyViewHolder> {
    List<All_Bookings_DataModels> bookingList;

    public RunBatchBookingAdapter(List<All_Bookings_DataModels> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_run_batch_offer, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RunBatchBookingAdapter.MyViewHolder myViewHolder, int i) {

        All_Bookings_DataModels all_bookings_dataModels = bookingList.get(i);

        //setting booking created time
        String bookingCreateTimeActiveBooking = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer("" + bookingList.get(i).getPickupDateTime());
        myViewHolder.bookingCreatedTime.setText("" + bookingCreateTimeActiveBooking);

        //setting contact name
        try {
            if (!all_bookings_dataModels.getPick_ContactName().equals(""))
                myViewHolder.userNameBList.setText("" + all_bookings_dataModels.getPick_ContactName());
            else
                myViewHolder.userNameBList.setText("Pickup contact  -  NA");
        } catch (Exception e) {
            e.printStackTrace();
            myViewHolder.userNameBList.setText("Pickup contact  -  NA");
        }

        //set delivery notes
        try {
            if (!all_bookings_dataModels.getNotes().equals(""))
                myViewHolder.delivryNotesBookingList.setText("" + all_bookings_dataModels.getNotes());
            else
                myViewHolder.delivryNotesBookingList.setText("No Delivery Notes");
        } catch (Exception e) {
            e.printStackTrace();
            myViewHolder.delivryNotesBookingList.setText("No Delivery Notes");
        }
        try {
            myViewHolder.suburbTextnewBooking.setText(all_bookings_dataModels.getPackage());
            myViewHolder.textDropoffBList.setText("Dropoff:- " + all_bookings_dataModels.getDrop_Address());
            myViewHolder.timeToArriveInBookingList.setText(all_bookings_dataModels.getDeliverySpeed());
            myViewHolder.chargesBList.setText("$" + all_bookings_dataModels.getPrice());
            myViewHolder.orderNumberNBL.setText(String.valueOf(all_bookings_dataModels.getOrderNumber()));
            myViewHolder.tv_booking_status.setText(String.valueOf(all_bookings_dataModels.getStatus()));
            myViewHolder.locationMarkBList.setText(all_bookings_dataModels.getDistanceFromCurrentLocation() + " km away");
        }catch (Exception e) {
           e.printStackTrace();
        }
        try {
            if (!all_bookings_dataModels.getDropDateTime().equals("")) {
                String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiff(all_bookings_dataModels.getDropDateTime(), false);
                myViewHolder.menuLogTimerTxtForNBList.setText(minInStr);
            } else {
                myViewHolder.menuLogTimerTxtForNBList.setText("NA");
            }
        } catch (Exception e) {
            myViewHolder.menuLogTimerTxtForNBList.setText("NA");
        }

        myViewHolder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(myViewHolder.itemView.getContext(), ActiveBookingDetail_New.class);
            intent.putExtra("RouteViewCalling", 0);
            intent.putExtra("model",bookingList.get(i));
            intent.putExtra("fromOfferRun",true);
            intent.putExtra("positionActiveBooking", -1);
            intent.putExtra("IsReturnToDHL", false);
            myViewHolder.itemView.getContext().startActivity(intent);

        });

    }

    @Override
    public int getItemCount() {
        return bookingList == null ? 0 : bookingList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userNameBList, bookingCreatedTime, delivryNotesBookingList, suburbTextnewBooking;
        TextView textDropoffBList, chargesBList, timeToArriveInBookingList, menuLogTimerTxtForNBList;
        TextView orderNumberNBL, tv_booking_status;
        TextView locationMarkBList;
        public MyViewHolder(View itemView) {
            super(itemView);
            userNameBList = itemView.findViewById(R.id.userNameBList);
            bookingCreatedTime = itemView.findViewById(R.id.bookingCreatedTime);
            delivryNotesBookingList = itemView.findViewById(R.id.delivryNotesBookingList);
            suburbTextnewBooking = itemView.findViewById(R.id.suburbTextnewBooking);

            textDropoffBList = itemView.findViewById(R.id.textDropoffBList);
            chargesBList = itemView.findViewById(R.id.chargesBList);
            timeToArriveInBookingList = itemView.findViewById(R.id.timeToArriveInBookingList);
            menuLogTimerTxtForNBList = itemView.findViewById(R.id.menuLogTimerTxtForNBList);

            orderNumberNBL = itemView.findViewById(R.id.orderNumberNBL);
            tv_booking_status = itemView.findViewById(R.id.tv_booking_status);
            locationMarkBList = itemView.findViewById(R.id.locationMarkBList);
        }
    }
}
