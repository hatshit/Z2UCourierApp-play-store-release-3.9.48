package com.newnotfication.view.bookings_view_offer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zoom2u.R;
import com.zoom2u.offer_run_batch.model.RunBatchResponse;

import java.util.ArrayList;
import java.util.List;

public class ShowOfferForBookings extends Activity {
    public static Dialog enterFieldDialog;


    public static void alertDialogToFinishView(Context newBooking_notification,List<CounterOffers> counterOffers){
        try{
            if(enterFieldDialog != null)
                if(enterFieldDialog.isShowing())
                    enterFieldDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }


        try {
            if(enterFieldDialog != null)
                enterFieldDialog = null;
            enterFieldDialog = new Dialog(newBooking_notification,android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(false);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.show_bid_offer);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            Button home = (Button) enterFieldDialog.findViewById(R.id.home);
            RecyclerView recyclerView=enterFieldDialog.findViewById(R.id.list_recyclerview);


            recyclerView.setLayoutManager(new LinearLayoutManager(newBooking_notification));
            recyclerView.setAdapter(new OfferAdapter(newBooking_notification, counterOffers));


            enterFieldDialog.findViewById(R.id.mainDefaultDialogLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                }
            });
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();

                }
            });
            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
