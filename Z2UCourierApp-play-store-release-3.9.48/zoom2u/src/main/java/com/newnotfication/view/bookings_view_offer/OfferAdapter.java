package com.newnotfication.view.bookings_view_offer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;

import java.util.List;

public class OfferAdapter extends RecyclerView.Adapter<OfferAdapter.MyViewHolder> {
    List<CounterOffers> counterOffers ;
    Context context;

    public OfferAdapter(Context newBooking_notification, List<CounterOffers> counterOffers) {
        this.counterOffers= counterOffers;
        this.context=newBooking_notification;
    }

    public interface OnItemClickListener {
        void onItemClick(String runId);
    }




    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.offer_adapter, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int i) {

        myViewHolder.count.setText(i+1+".");
        myViewHolder.price.setText("$"+counterOffers.get(i).getPrice()+"");
    }

    @Override
    public int getItemCount() {
        return context == null ? 0 : counterOffers.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView count,price;
        public MyViewHolder(View itemView) {
            super(itemView);
            count=itemView.findViewById(R.id.count);
            price=itemView.findViewById(R.id.price);
        }
    }

}

