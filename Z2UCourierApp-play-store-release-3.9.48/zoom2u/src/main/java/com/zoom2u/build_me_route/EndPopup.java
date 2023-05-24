package com.zoom2u.build_me_route;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.BookingView;

public class EndPopup extends Activity{
    public static Dialog enterFieldDialog;

    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsSignatureScreenOpen =false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        PushReceiver.IsSignatureScreenOpen =true;
    }
    public static void alertDialogToFinishView(SecondBuildRouteActivity secondBuildRouteActivity){
        try{
            if(enterFieldDialog != null)
                if(enterFieldDialog.isShowing())
                    enterFieldDialog.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        PushReceiver.IsSignatureScreenOpen =true;

        try {
            if(enterFieldDialog != null)
                enterFieldDialog = null;
            enterFieldDialog = new Dialog(secondBuildRouteActivity,android.R.style.Theme_Light);
            enterFieldDialog.setCancelable(false);
            enterFieldDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1A000000")));
            enterFieldDialog.setContentView(R.layout.end_build_me_route);

            Window window = enterFieldDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);


            Button home = (Button) enterFieldDialog.findViewById(R.id.home);
            LinearLayout build_me = (LinearLayout) enterFieldDialog.findViewById(R.id.build_me);

            build_me.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                    Intent intent=new Intent();
                    secondBuildRouteActivity.setResult(12,intent);
                    secondBuildRouteActivity.finish();
                }
            });
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterFieldDialog.dismiss();
                    BookingView.bookingViewSelection=1;
                    ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
                    Intent intent=new Intent(secondBuildRouteActivity, SlideMenuZoom2u.class);
                    secondBuildRouteActivity.startActivity(intent);
                    secondBuildRouteActivity.finish();
                }
            });
            enterFieldDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
