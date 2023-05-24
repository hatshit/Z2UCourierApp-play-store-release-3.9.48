package com.zoom2u;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

public class UpdatePassword extends Activity {

	ImageView backSignUp;
	TextView textCourierAppUpdatePass,confirm_new_pass,new_pass,old_pass;
	EditText oldPassword, newPassword, confirmNewPass;
	Button updatePasswordBtn;

	ProgressDialog progressForUpdatePassword;
	
	String oldPasswordStr, newPasswordStr, confirmNewPassStr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_activity);
		try {
			if(savedInstanceState != null){
				ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
				if(LoginZoomToU.NOVA_BOLD == null)
					LoginZoomToU.staticFieldInit(UpdatePassword.this);
			}
			inItUpdatePasswordView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	protected void onResume() {
		SlideMenuZoom2u.setCourierToOnlineForChat();
		super.onResume();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
			Log.e("", "********************    Save instant state in update pass activity");
		//	SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}
	
	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		 if(savedInstanceState != null){
				ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
				if(LoginZoomToU.NOVA_BOLD == null)
					LoginZoomToU.staticFieldInit(UpdatePassword.this);
			}
			inItUpdatePasswordView();
		super.onRestoreInstanceState(savedInstanceState);
	}

	 void inItUpdatePasswordView(){
		 try{
			confirm_new_pass=findViewById(R.id.confirm_new_pass);
			old_pass=findViewById(R.id.old_pass);
			new_pass=findViewById(R.id.new_pass);
			 Window window = UpdatePassword.this.getWindow();
			 if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
				 window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				 window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			 }
			 window.setStatusBarColor(Color.parseColor("#374350"));


		 	if(oldPassword == null)
					oldPassword = (EditText) findViewById(R.id.signUpName);

				if(textCourierAppUpdatePass == null)
					textCourierAppUpdatePass = (TextView) findViewById(R.id.textCourierAppUpdatePass);

				if(newPassword == null)
					newPassword = (EditText) findViewById(R.id.signUpPassword);

				if(confirmNewPass == null)
					confirmNewPass = (EditText) findViewById(R.id.signUpConfirmPassword);

				if(updatePasswordBtn == null)
					updatePasswordBtn = (Button) findViewById(R.id.updatePasswordBtn);

				if(backSignUp == null)	
					backSignUp = (ImageView) findViewById(R.id.backSignUp);
				



			 setEdtFieldBGtoGray(oldPassword, old_pass);
			 setEdtFieldBGtoGray(newPassword, new_pass);
			 setEdtFieldBGtoGray(confirmNewPass, confirm_new_pass);
			 old_pass.setVisibility(View.INVISIBLE);
			 new_pass.setVisibility(View.INVISIBLE);
			 confirm_new_pass.setVisibility(View.INVISIBLE);


					
				backSignUp.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							
							finish();
							overridePendingTransition(R.anim.left_in, R.anim.right_out);
						}
					});
					
				updatePasswordBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							LoginZoomToU.imm.hideSoftInputFromWindow(oldPassword.getWindowToken(), 0);
							oldPasswordStr = oldPassword.getText().toString();
							newPasswordStr = newPassword.getText().toString();
							confirmNewPassStr = confirmNewPass.getText().toString();
							
							if(oldPasswordStr.equals("")  || newPasswordStr.equals("") || confirmNewPassStr.equals(""))
								DialogActivity.alertDialogView(UpdatePassword.this, "Information !", "Please enter field first.");
							else if(oldPasswordStr.length() < 6 || newPasswordStr.length() < 6 || confirmNewPassStr.length() < 6)
								DialogActivity.alertDialogView(UpdatePassword.this, "Information !", "Password must be atleast 6 characters.");
							else if(!newPasswordStr.equals(confirmNewPassStr))
								DialogActivity.alertDialogView(UpdatePassword.this, "Information !", "New password and confirm password doesn't match.");
							else {
								if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
									UpdatePasswordAsyncTask();
									//new UpdatePasswordAsyncTask().execute();
								else
									DialogActivity.alertDialogView(UpdatePassword.this, "No Network !", "No network connection, Please try again later.");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
		 }catch(Exception e){
			 e.printStackTrace();
		 }
	 }


	private void setEdtFieldBGtoGray(final EditText edtFieldAddEditMember, final TextView txtFieldAddEditMember) {
		edtFieldAddEditMember.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

					txtFieldAddEditMember.setVisibility(View.INVISIBLE);
				} else {
					edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
					txtFieldAddEditMember.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	public void onBackPressed()
	{
	     // code here to show dialog
	     super.onBackPressed();
	     LoginZoomToU.imm.hideSoftInputFromWindow(oldPassword.getWindowToken(), 0);
	     finish();
		 overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
		return true;
	}

	private void UpdatePasswordAsyncTask(){
		final String[] updatePassResponseStr = {"0"};
		final boolean[] responseData = {false};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try {
					if(progressForUpdatePassword == null)
						progressForUpdatePassword = new ProgressDialog(UpdatePassword.this);
					Custom_ProgressDialogBar.inItProgressBar(progressForUpdatePassword);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					updatePassResponseStr[0] = webServiceHandler.getUpdatePasswordforOldPass(oldPasswordStr, newPasswordStr);
					webServiceHandler = null;
					try {
						JSONObject jOBJForUpdatePass = new JSONObject(updatePassResponseStr[0]);
						responseData[0] = jOBJForUpdatePass.getBoolean("success");
					} catch (JSONException e) {
						e.printStackTrace();
						updatePassResponseStr[0] = "0";
					}
				} catch (Exception e) {
					e.printStackTrace();
					updatePassResponseStr[0] = "0";

				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(progressForUpdatePassword != null)
						if(progressForUpdatePassword.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForUpdatePassword);

					if(!updatePassResponseStr[0].equals("0"))
					{
						if(responseData[0])
						{
							//************ Re-store updated password to preference *************//
							new Courier_AutoLoginFeature().storeCredentialAfterLogin(LoginZoomToU.prefrenceForLogin.getBoolean("islogin", false),
									LoginZoomToU.prefrenceForLogin.getString("username", null), newPasswordStr, LoginZoomToU.prefrenceForLogin.getString("accessToken", null),
									LoginZoomToU.prefrenceForLogin.getInt("CarrierId", 0), LoginZoomToU.prefrenceForLogin.getBoolean("isTeamLeader", false));

							if(LoginZoomToU.enterFieldDialog != null)
								LoginZoomToU.enterFieldDialog = null;
							LoginZoomToU.enterFieldDialog = new Dialog(UpdatePassword.this);
							LoginZoomToU.enterFieldDialog.setCancelable(false);
							LoginZoomToU.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
							LoginZoomToU.enterFieldDialog.setContentView(R.layout.dialogview);

							Window window = LoginZoomToU.enterFieldDialog.getWindow();
							window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
							android.view.WindowManager.LayoutParams wlp = window.getAttributes();

							wlp.gravity = Gravity.CENTER;
							wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
							window.setAttributes(wlp);

							TextView enterFieldDialogHEader = (TextView) LoginZoomToU.enterFieldDialog.findViewById(R.id.titleDialog);

							enterFieldDialogHEader.setText("Updated successfully !");

							TextView enterFieldDialogMsg = (TextView) LoginZoomToU.enterFieldDialog.findViewById(R.id.dialogMessageText);

							enterFieldDialogMsg.setText("Password updated successfully.");

							Button enterFieldDialogDoneBtn = (Button) LoginZoomToU.enterFieldDialog.findViewById(R.id.dialogDoneBtn);
							enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {

								@Override
								public void onClick(View v) {

									LoginZoomToU.enterFieldDialog.dismiss();
									finish();
									overridePendingTransition(R.anim.left_in, R.anim.right_out);
								}
							});

							LoginZoomToU.enterFieldDialog.show();
						}else{
							DialogActivity.alertDialogView(UpdatePassword.this, "Fail to update !", "Please enter correct password.");
						}
					}else
						DialogActivity.alertDialogView(UpdatePassword.this, "Failed !", "Something went wrong, Please try again later.");

				} catch (Exception e) {
					e.printStackTrace();

					if(progressForUpdatePassword != null)
						if(progressForUpdatePassword.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForUpdatePassword);

					DialogActivity.alertDialogView(UpdatePassword.this, "Failed !", "Something went wrong, Please try again later.");
				}
			}
		}.execute();
	}



	
}
