package com.zoom2u;

import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ForgotPasswordActivity extends Activity {

	ImageView backForgetPass;
	EditText retriveEmail;
	Button retrivePassword;
	ProgressDialog progressToGetPassword;
	Dialog enterFieldDialog;
	
	String retriveEmailID;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_activity);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			Window window = ForgotPasswordActivity.this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor("#3a424f"));
		}

	try {
		
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(ForgotPasswordActivity.this);

		if(backForgetPass == null)
			backForgetPass = (ImageView) findViewById(R.id.backRetrivePass);
		if(retriveEmail == null)
			retriveEmail = (EditText) findViewById(R.id.retriveEmail);

		if(retrivePassword == null)
			retrivePassword = (Button) findViewById(R.id.retriveBtn);




		// back from current view
		backForgetPass.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
					finish();
					 if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
						 overridePendingTransition(R.anim.left_in, R.anim.right_out);
			}
		});

		retrivePassword.setOnClickListener(new OnClickListener() {

		@Override
		public void onClick(View v) {
				try {
					LoginZoomToU.imm.hideSoftInputFromWindow(retriveEmail.getWindowToken(), 0);
					retriveEmailID = retriveEmail.getText().toString();

					if(retriveEmailID.equals("")){
						if(LoginZoomToU.enterFieldDialog != null)
							LoginZoomToU.enterFieldDialog = null;
							LoginZoomToU.enterFieldDialog = new Dialog(ForgotPasswordActivity.this);
							LoginZoomToU.enterFieldDialog.setCancelable(false);
							LoginZoomToU.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							LoginZoomToU.enterFieldDialog.setContentView(R.layout.dialogview);

							Window window = LoginZoomToU.enterFieldDialog.getWindow();
							window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							WindowManager.LayoutParams wlp = window.getAttributes();

							wlp.gravity = Gravity.TOP;
							wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
							window.setAttributes(wlp);

							TextView enterFieldDialogHEader = (TextView) LoginZoomToU.enterFieldDialog.findViewById(R.id.titleDialog);

							TextView enterFieldDialogMsg = (TextView) LoginZoomToU.enterFieldDialog.findViewById(R.id.dialogMessageText);

							Button enterFieldDialogDoneBtn = (Button) LoginZoomToU.enterFieldDialog.findViewById(R.id.dialogDoneBtn);
							enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
									LoginZoomToU.enterFieldDialog.dismiss();
								}
							});
							LoginZoomToU.enterFieldDialog.show();
					}else if(!retriveEmailID.matches(LoginZoomToU.EMAIL_PATTERN)){
							DialogActivity.alertDialogView(ForgotPasswordActivity.this, "Information !", "Please enter valid e-mail.");
					}else{
							if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
								GetPasswordAsyncTask();
								//new GetPasswordAsyncTask().execute();
							else
								DialogActivity.alertDialogView(ForgotPasswordActivity.this, "No Network !", "No network connection, Please try again later.");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			  }
		  });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed()
	{
	     // code here to show dialog
	     super.onBackPressed();
	     finish();
	     if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

	    	 overridePendingTransition(R.anim.left_in, R.anim.right_out);
	     }

	   }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forgot, menu);
		return true;
	}

	private void GetPasswordAsyncTask(){
		final String[] webServiceResponse = {"0"};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try {
					if(progressToGetPassword == null)
						progressToGetPassword = new ProgressDialog(ForgotPasswordActivity.this);
					Custom_ProgressDialogBar.inItProgressBar(progressToGetPassword);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					webServiceResponse[0] = webServiceHandler.getForgetPasswordEmail(retriveEmailID);
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					webServiceResponse[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(progressToGetPassword.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressToGetPassword);

					if(!webServiceResponse[0].equals("0"))
					{
						if(enterFieldDialog != null)
							enterFieldDialog = null;
						enterFieldDialog = new Dialog(ForgotPasswordActivity.this);
						enterFieldDialog.setCancelable(false);
						enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
						enterFieldDialog.setContentView(R.layout.dialogview);

						Window window = enterFieldDialog.getWindow();
						window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						WindowManager.LayoutParams wlp = window.getAttributes();

						wlp.gravity = Gravity.CENTER;
						wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
						window.setAttributes(wlp);

						TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

						enterFieldDialogHEader.setText("Successfull !");

						TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

						enterFieldDialogMsg.setText("Please check your email to get password.");

						Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);
						enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								enterFieldDialog.dismiss();
								finish();
								webServiceResponse[0] = "0";
							}
						});
						enterFieldDialog.show();

					}else if(!webServiceResponse[0].equals(""))
					{
						DialogActivity.alertDialogView(ForgotPasswordActivity.this, "Error !", "Please enter correct e-mail.");
						webServiceResponse[0] = "0";
					}
				} catch (Exception e) {
					e.printStackTrace();

					if(progressToGetPassword.isShowing())
						progressToGetPassword.dismiss();

					DialogActivity.alertDialogView(ForgotPasswordActivity.this, "Server Error !", "Please try later.");
					webServiceResponse[0] = "0";
				}
			}
		}.execute();
	}



}
