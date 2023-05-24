package com.zoom2u.dialogactivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.zoom2u.R;

import java.util.Timer;
import java.util.TimerTask;

public class DropOffDoneDialog {
    public static Dialog enterFieldDialog;

    public static void alertDialogToFinishView(Context context,String dropOrpick){
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
            enterFieldDialog = new Dialog(context,android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(true);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.drop_off_dilog);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            LottieAnimationView view=enterFieldDialog.findViewById(R.id.view);
            view.playAnimation();

            enterFieldDialog.findViewById(R.id.mainDefaultDialogLayout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.cancel();
                }
            });


            enterFieldDialog.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.cancel();
                }
            });

            ((TextView)enterFieldDialog.findViewById(R.id.titleDialog)).setText(dropOrpick);

            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                    public void run() {
                    enterFieldDialog.cancel();
                    }
                }, 5000);



            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
