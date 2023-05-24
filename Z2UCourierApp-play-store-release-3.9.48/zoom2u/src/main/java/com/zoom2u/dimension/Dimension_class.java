package com.zoom2u.dimension;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Dimension_class {


    public   static void setDimension(RecyclerView dimension_recyclerview, Context context, ArrayList<HashMap<String, Object>> shipmentArray){
        dimension_recyclerview.setLayoutManager(new LinearLayoutManager(context));
        dimension_recyclerview.setAdapter(new Dimension_Adapter(context, shipmentArray));

    }

    public void setTheViewsForDimensions(LinearLayout rootLayout,Context context,ArrayList<HashMap<String, Object>> shipmentArray){
        if (shipmentArray==null||shipmentArray.isEmpty())
            return;

        //clearing any view attached with root element
        rootLayout.removeAllViews();

        shipmentArray.forEach(item -> {
            View itemView = LayoutInflater.from(context).inflate(R.layout.a_dimiesion_item, null);

            TextView category=itemView.findViewById(R.id.category);
            TextView length=itemView.findViewById(R.id.length);
            TextView width=itemView.findViewById(R.id.width);
            TextView  height = itemView.findViewById(R.id.height);
            TextView item_weight=itemView.findViewById(R.id.item_weight);
            TextView  total_weight=itemView.findViewById(R.id.total_weight);
            ImageView primaryImageView = itemView.findViewById(R.id.primaryImageView);
            LinearLayout dimiesion = itemView.findViewById(R.id.dimiesion);

            category.setText(item.get("Category")+" ("+item.get("Quantity")+"):-");
            if(Objects.equals(item.get("LengthCm"), null)|| Objects.equals(item.get("LengthCm"), 0)){
               dimiesion.setVisibility(View.GONE);
            }
            else
            {
                dimiesion.setVisibility(View.VISIBLE);
                length.setText(" Length:- " + item.get("LengthCm") + "(cm)");
                width.setText("Width:- " + item.get("WidthCm") + "(cm)");
                height.setText(" Height:- " + item.get("HeightCm") + "(cm)");
                item_weight.setText("Item weight:- " + item.get("ItemWeightKg") + "(kg)");
                total_weight.setText("Total weight:- " + item.get("TotalWeightKg") + "(kg)");
            }

            setPackageIcon(item.get("Category").toString(),primaryImageView);

            rootLayout.addView(itemView);

        });
    }

    private void setPackageIcon(String category, ImageView placeBidItemImgList) {
        switch (category) {
            case "Documents":
                placeBidItemImgList.setImageResource(R.drawable.bid_documents);
                break;
            case "Bag":
                placeBidItemImgList.setImageResource(R.drawable.bid_satchelandlaptops);
                break;
            case "Box":
                placeBidItemImgList.setImageResource(R.drawable.bid_smallbox);
                break;
            case "Flowers":
                placeBidItemImgList.setImageResource(R.drawable.bid_cakesflowersdelicates);
                break;
            case "Large":
                placeBidItemImgList.setImageResource(R.drawable.bid_largebox);
                break;
            default:
                placeBidItemImgList.setImageResource(R.drawable.bid_largeitems);
                break;
        }
    }

}
