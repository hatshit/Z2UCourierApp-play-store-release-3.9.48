package com.zoom2u.utility;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;

public class DialogFor_ActiveListOrDetail {

    public static Dialog alertForActiveListOrDetail;

    public static void dialogOnRouteToPickForActiveListOrDetail(final Context context, String titleStr, String message, int changeTitleTxtColor){
        try{
            if(alertForActiveListOrDetail != null)
                if(alertForActiveListOrDetail.isShowing())
                    alertForActiveListOrDetail.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(alertForActiveListOrDetail != null)
                alertForActiveListOrDetail = null;
            alertForActiveListOrDetail = new Dialog(context);
            alertForActiveListOrDetail.setCancelable(false);
            alertForActiveListOrDetail.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            alertForActiveListOrDetail.setContentView(R.layout.dialogview);

            Window window = alertForActiveListOrDetail.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView alertForActiveListOrDetailHEader = (TextView) alertForActiveListOrDetail.findViewById(R.id.titleDialog);

            alertForActiveListOrDetailHEader.setTextColor(context.getResources().getColor(changeTitleTxtColor));
            alertForActiveListOrDetailHEader.setText(titleStr);

            TextView alertForActiveListOrDetailMsg = (TextView) alertForActiveListOrDetail.findViewById(R.id.dialogMessageText);

            alertForActiveListOrDetailMsg.setText(message);

            Button alertForActiveListOrDetailDoneBtn = (Button) alertForActiveListOrDetail.findViewById(R.id.dialogDoneBtn);
            alertForActiveListOrDetailDoneBtn.setText("I understand");
            alertForActiveListOrDetailDoneBtn.setBackgroundResource(R.drawable.rounded_worrier_level);

            alertForActiveListOrDetailDoneBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertForActiveListOrDetail.dismiss();
                }
            });

            alertForActiveListOrDetail.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
