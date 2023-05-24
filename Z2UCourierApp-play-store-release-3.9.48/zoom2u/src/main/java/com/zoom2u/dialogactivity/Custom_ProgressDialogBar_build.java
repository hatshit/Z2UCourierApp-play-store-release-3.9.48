package com.zoom2u.dialogactivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;

import com.zoom2u.R;

public class Custom_ProgressDialogBar_build {

    public static void inItProgressBar(ProgressDialog progressDialog,String text){
    if(progressDialog != null){
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.show();
        progressDialog.setContentView(R.layout.view_loader_build);
        TextView textView=progressDialog.findViewById(R.id.txtlog);
        textView.setText(text);
    }
}

    public static void dismissProgressBar(ProgressDialog progressDialog){
        if(progressDialog != null){
            try {
                progressDialog.dismiss();
                progressDialog = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
