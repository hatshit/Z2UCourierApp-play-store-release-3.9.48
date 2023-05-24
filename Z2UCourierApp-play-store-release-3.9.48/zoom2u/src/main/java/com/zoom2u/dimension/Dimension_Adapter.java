package com.zoom2u.dimension;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.zoom2u.R;
import com.zoom2u.offer_run_batch.SingleRunActivity;
import com.zoom2u.offer_run_batch.model.RunBatchResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Dimension_Adapter extends RecyclerView.Adapter<Dimension_Adapter.MyViewHolder> {
    ArrayList<HashMap<String, Object>> shipmentArray;
    Context context;


    public interface OnItemClickListener {
        void onItemClick(String runId);
    }
    public Dimension_Adapter(Context context, ArrayList<HashMap<String, Object>> shipmentArray) {
        this.shipmentArray = shipmentArray;
        this.context=context;
    }



    @NonNull
    @Override
    public Dimension_Adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.a_dimiesion_item, parent, false);
        return new Dimension_Adapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(Dimension_Adapter.MyViewHolder myViewHolder, int i) {

        myViewHolder.category.setText(shipmentArray.get(i).get("Category")+" ("+shipmentArray.get(i).get("Quantity")+"):-");
        if(Objects.equals(shipmentArray.get(i).get("LengthCm"), null)|| Objects.equals(shipmentArray.get(i).get("LengthCm"), 0)){
           myViewHolder.dimiesion.setVisibility(View.GONE);
        }
        else
        {
            myViewHolder.dimiesion.setVisibility(View.VISIBLE);
            myViewHolder.length.setText(" Length:- " + shipmentArray.get(i).get("LengthCm") + "(cm)");
            myViewHolder.width.setText("Width:- " + shipmentArray.get(i).get("WidthCm") + "(cm)");
            myViewHolder.height.setText(" Height:- " + shipmentArray.get(i).get("HeightCm") + "(cm)");
            myViewHolder.item_weight.setText("Item weight:- " + shipmentArray.get(i).get("ItemWeightKg") + "(kg)");
            myViewHolder.total_weight.setText("Total weight:- " + shipmentArray.get(i).get("TotalWeightKg") + "(kg)");
        }
        setPackageImgNText(shipmentArray.get(i).get("Category").toString(),myViewHolder.primaryImageView);

    }
    private void setPackageImgNText(String categeory, ImageView placeBidItemImgList) {
        switch (categeory) {
            case "Documents":
                placeBidItemImgList.setImageResource(R.drawable.bid_documents);
               // bid_imageTypeTxt.setText("Documents");
                break;
            case "Bag":
                placeBidItemImgList.setImageResource(R.drawable.bid_satchelandlaptops);
                //bid_imageTypeTxt.setText("Satchel, laptops");
                break;
            case "Box":
                placeBidItemImgList.setImageResource(R.drawable.bid_smallbox);
               // bid_imageTypeTxt.setText("Small box");
                break;
            case "Flowers":
                placeBidItemImgList.setImageResource(R.drawable.bid_cakesflowersdelicates);
               // bid_imageTypeTxt.setText("Cakes, flowers & delicates");
                break;
            case "Large":
                placeBidItemImgList.setImageResource(R.drawable.bid_largebox);
               // bid_imageTypeTxt.setText("Large box");
                break;
            default:
                placeBidItemImgList.setImageResource(R.drawable.bid_largeitems);
               // bid_imageTypeTxt.setText("General Truck Shipments");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return shipmentArray == null ? 0 : shipmentArray.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView category,length,width,height,item_weight,total_weight;
        ImageView primaryImageView;
        LinearLayout dimiesion;

        public MyViewHolder(View itemView) {
            super(itemView);
            category=itemView.findViewById(R.id.category);
            length=itemView.findViewById(R.id.length);
            width=itemView.findViewById(R.id.width);
            height = itemView.findViewById(R.id.height);
            item_weight=itemView.findViewById(R.id.item_weight);
            total_weight=itemView.findViewById(R.id.total_weight);
            primaryImageView = itemView.findViewById(R.id.primaryImageView);
            dimiesion = itemView.findViewById(R.id.dimiesion);

        }
    }

}
