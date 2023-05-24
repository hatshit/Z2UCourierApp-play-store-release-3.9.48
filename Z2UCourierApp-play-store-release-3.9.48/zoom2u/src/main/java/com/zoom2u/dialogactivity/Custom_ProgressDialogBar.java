package com.zoom2u.dialogactivity;

import com.zoom2u.R;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class Custom_ProgressDialogBar{

	public static void inItProgressBar(ProgressDialog progressDialog){
		if(progressDialog != null){
			progressDialog.setCancelable(false);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			progressDialog.show();
			progressDialog.setContentView(R.layout.view_loader);
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
