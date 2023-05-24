package com.zoom2u;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;

public class LoginZoomToU extends Activity {

	EditText loginEmail, loginPass;
	TextView courierAppTxt, labelEmail, labelPassword, showTxtLoginScreen, rememberMe, forgotPassword, textTermInLogin, privacyPolicyTxt, versionNameTxt;
	Button loginBtn;
	CheckBox checkBoxRememberMe;

	RelativeLayout loginPage, tempMainLayoutLoginView;
	
	public static SharedPreferences prefrenceForLogin;
	public static Editor loginEditor;
	
	public static String getCurrentLocatnlatitude = "", getCurrentLocatnLongitude = "";

	public static String pushyRegId = "";
	ProgressDialog progressForLogin;
	
	public static Typeface NOVA_BLACK;
	public static Typeface NOVA_BOLD;
	public static Typeface NOVA_EXTRABOLD;
	public static Typeface NOVA_LIGHT;
	public static Typeface NOVA_REGULAR;
	public static Typeface NOVA_SEMIBOLD;
	public static Typeface NOVA_THIN;
	
	public static String courierID = "";
	public static String courierImage = "";
	public static String courierCompany = "";
	public static String courierName = "";

	public static int CARRIER_ID = 0;
	public static boolean IS_TEAM_LEADER = false;

	public static Functional_Utility checkInternetwithfunctionality;
	public static String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
				+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	public static String PHONE_REGIX = "^[\\s0-9\\()\\-\\+]+$";
	public static int isLoginSuccess = 0;
	public static Dialog enterFieldDialog;
	public static int height, width;

	public static float device_Density;

	public static InputMethodManager imm;
	
	public static SharedPreferences prefrenceForLogout;
	public static Editor editorForLogout;
	public static int notificUINewBookingVisibleCount = 0;
	public static int notificUIRequestAvailCount = 0;
	
	public static int activeBookingTab = 0;
	public static String filterDayActiveListStr = "today";
	public static String getDHLBookingStr = "Active";
	
	public static String savedImgDirOfFialedImg;
	boolean isRemembermeChecked = false;

	Courier_AutoLoginFeature courier_autoLogin;
	public static boolean isImgFromInternalStorage = false;

	public static FirebaseAuth mAuth_Firebase;
	public static FirebaseUser firebase_CurrentUser;

	private boolean isShowBtnClicked = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginzoom2u);
		try {
			staticFieldInit(LoginZoomToU.this);
			courier_autoLogin = new Courier_AutoLoginFeature(LoginZoomToU.this);
			try {
				if (MainActivity.CURRENT_DEVICE_APP_VERSION == 0)
					MainActivity.CURRENT_DEVICE_APP_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
			} catch (PackageManager.NameNotFoundException e) {
				e.printStackTrace();
			}

			if(checkBoxRememberMe == null)
				checkBoxRememberMe = (CheckBox) findViewById(R.id.checkForRememberMe);
			if(loginPage == null)
				loginPage = (RelativeLayout) findViewById(R.id.loginPage);
			if (tempMainLayoutLoginView == null)
				tempMainLayoutLoginView = (RelativeLayout) findViewById(R.id.tempMainLayoutLoginView);
			tempMainLayoutLoginView.setVisibility(View.GONE);

			labelEmail = (TextView) findViewById(R.id.labelEmail);

			labelEmail.setVisibility(View.GONE);
			labelPassword = (TextView) findViewById(R.id.labelPassword);

			labelPassword.setVisibility(View.GONE);

			if(loginEmail == null)
				loginEmail = (EditText) findViewById(R.id.loginId);

			if(loginPass == null)
				loginPass = (EditText) findViewById(R.id.loginPass);

			loginPass.setTransformationMethod(new PasswordTransformationMethod());

			if(courierAppTxt == null)
				courierAppTxt = (TextView) findViewById(R.id.courierAppTxt);


			showTxtLoginScreen = (TextView) findViewById(R.id.showTxtLoginScreen);

			showTxtLoginScreen.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isShowBtnClicked) {
						showTxtLoginScreen.setText("Hide");
						loginPass.setTransformationMethod(null);
					} else {
						showTxtLoginScreen.setText("Show");
						loginPass.setTransformationMethod(new PasswordTransformationMethod());
					}
					isShowBtnClicked = !isShowBtnClicked;
				}
			});

			if(rememberMe == null)
				rememberMe = (TextView) findViewById(R.id.textRememberMe);

			if(forgotPassword == null)
				forgotPassword = (TextView) findViewById(R.id.forgotPassword);

			if(loginBtn == null)
				loginBtn = (Button) findViewById(R.id.loginBtn);

			loginBtn.setTransformationMethod(null);
			if(textTermInLogin == null)
				textTermInLogin = (TextView) findViewById(R.id.textTermInLogin);

			textTermInLogin.setText("Terms & Conditions");
			if(privacyPolicyTxt == null)
				privacyPolicyTxt = (TextView) findViewById(R.id.privacyPolicyTxt);

			if(versionNameTxt == null)
				versionNameTxt = (TextView) findViewById(R.id.versionNameTxt);

				try {
					versionNameTxt.setText("Version "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

			textTermInLogin.setVisibility(View.VISIBLE);
			textTermInLogin.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				try {
						Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.zoom2u.com/courier-terms/"));
						startActivity(browserIntent);
						browserIntent = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			 }
		  });

			privacyPolicyTxt.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						Intent browserIntent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.zoom2u.com.au/privacy/"));
						startActivity(browserIntent);
						browserIntent = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			loginPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
					imm.hideSoftInputFromWindow(loginEmail.getWindowToken(), 0);
					if (arg1 == EditorInfo.IME_ACTION_DONE){
						loginPass.setFocusable(true);
						checkValidationBeforeLogin();
						return true;
					}
					return false;
				}
			});

			loginEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
					if (arg1 == EditorInfo.IME_ACTION_DONE){
						loginPass.setFocusable(true);
						return true;
					}
					return false;
				}
			});

			loginEmail.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() == 0) {
						loginEmail.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transtarent_bottom_whiteborder));
						labelEmail.setVisibility(View.GONE);
					} else {
						loginEmail.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transparent_bottom_blue));
						labelEmail.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

			loginPass.addTextChangedListener(new TextWatcher() {
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {

				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if (s.length() == 0) {
						labelPassword.setVisibility(View.GONE);
						loginPass.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transtarent_bottom_whiteborder));
					} else {
						labelPassword.setVisibility(View.VISIBLE);
						loginPass.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transparent_bottom_blue));
					}
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});

//			loginEmail.setOnFocusChangeListener(new OnFocusChangeListener() {
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//					if (hasFocus) {
//
//					}
//				}
//			});

			if(pushyRegId.equals(""))
				courier_autoLogin.getPushyTokenId();
		     
	    if(courier_autoLogin.isLogin){
			loginEmail.setText(courier_autoLogin.userName);
			loginEmail.setFocusable(true);
			loginEmail.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transparent_bottom_blue));
			loginPass.setBackgroundDrawable(ContextCompat.getDrawable(LoginZoomToU.this, R.drawable.bg_transparent_bottom_blue));
			loginPass.setText(courier_autoLogin.password);
			loginPass.setTransformationMethod(new PasswordTransformationMethod());
			checkBoxRememberMe.setChecked(true);
            labelEmail.setVisibility(View.VISIBLE);
            labelPassword.setVisibility(View.VISIBLE);
		}
		
		forgotPassword.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
					Intent forgotPage = new Intent(LoginZoomToU.this, ForgotPasswordActivity.class);
					startActivity(forgotPage);
					 if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB)
						 overridePendingTransition(R.anim.right_in, R.anim.left_out);
				}
		});
		
		loginBtn.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
				try {
					checkValidationBeforeLogin();
				} catch (Exception e) {
					e.printStackTrace();
				}
			  }
			});

//			if (!MainActivity.CURRENT_APP_VERSION.equals("")) {
//				if (!MainActivity.CURRENT_APP_VERSION.equals(MainActivity.CURRENT_DEVICE_APP_VERSION))
//					versionUpdateDialogView();
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
			Window window = LoginZoomToU.this.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor("#3a424f"));
		}
	}

	void checkValidationBeforeLogin(){
		imm.hideSoftInputFromWindow(loginEmail.getWindowToken(), 0);
		courier_autoLogin.userName = loginEmail.getText().toString();
		courier_autoLogin.password = loginPass.getText().toString();
		if(courier_autoLogin.userName.equals("") || courier_autoLogin.password.equals("")) {
			if(courier_autoLogin.userName.equals(""))
				DialogActivity.alertDialogView(LoginZoomToU.this, "Information!", "Please enter email");
			else
				DialogActivity.alertDialogView(LoginZoomToU.this, "Information!", "Please enter password");
		} else if(!courier_autoLogin.userName.matches(EMAIL_PATTERN))
			DialogActivity.alertDialogView(LoginZoomToU.this, "Information!", "Please enter valid e-mail id");
		else {
			isRemembermeChecked = checkBoxRememberMe.isChecked();
			getLogin();
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		loginEmail.setFocusable(true);
		loginPass.clearFocus();
	}
	
	@Override
	public void onBackPressed(){
	     super.onBackPressed();
	     imm.hideSoftInputFromWindow(loginEmail.getWindowToken(), 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		try {
			loginEmail.setFocusable(true);
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}

	private void LoginAsyncTask(){

		final String[] webServiceResponseForLogin = {"0"};
		final String[] webServiceResponseForRoutific = {"0"};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try {
					if(progressForLogin == null)
						progressForLogin = new ProgressDialog(LoginZoomToU.this);
					Custom_ProgressDialogBar.inItProgressBar(progressForLogin);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if (MainActivity.CURRENT_APP_VERSION_FROM_SERVER == 0)
						webServiceHandler.CheckForCourierAppVerSionUpdate();

					webServiceResponseForLogin[0] = webServiceHandler.getUserLogin(courier_autoLogin.userName, courier_autoLogin.password);
					JSONObject responseData = null;
					LoginZoomToU.CARRIER_ID = 0;
					try {
						responseData = new JSONObject(webServiceResponseForLogin[0]);
						String accessToken = responseData.getString("access_token");
						try {
							IS_TEAM_LEADER = responseData.getBoolean("isTeamLeader");
							CARRIER_ID = responseData.getInt("CarrierId");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if(pushyRegId.equals(""))
							courier_autoLogin.getPushyTokenId();
						// set username and password to preference when remember me is checked
						if(isRemembermeChecked)
							courier_autoLogin.storeCredentialAfterLogin(true, courier_autoLogin.userName, courier_autoLogin.password, accessToken, CARRIER_ID, IS_TEAM_LEADER);
						else
							courier_autoLogin.storeCredentialAfterLogin(false, courier_autoLogin.userName, courier_autoLogin.password, accessToken, CARRIER_ID, IS_TEAM_LEADER);

						webServiceResponseForRoutific[0] = webServiceHandler.CheckRoutific();
						responseData = null;
					}catch(Exception e) {
						e.printStackTrace();
					}
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					/*get home location*/
					String dropLatLong=new JSONObject(webServiceResponseForRoutific[0]).getJSONObject("address").getString("Location");
					if(!dropLatLong.equals("0.0,0.0")) {
						String[] arrOfStr = dropLatLong.split(",");
						MainActivity.HomeLat = Double.parseDouble(arrOfStr[0]);
						MainActivity.HomeLang = Double.parseDouble(arrOfStr[1]);
						Geocoder geocoder = new Geocoder(LoginZoomToU.this, Locale.getDefault());
						List<Address> addresses = geocoder.getFromLocation(MainActivity.HomeLat, MainActivity.HomeLang, 1);
						MainActivity.HomeAddress = addresses.get(0).getAddressLine(0);

					}
				}catch (Exception e){ }



				try{
					if(progressForLogin != null)
						if(progressForLogin.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForLogin);
				}catch(Exception e){
					e.printStackTrace();
				}
				try {
					if (!webServiceResponseForLogin[0].equals("0") && !webServiceResponseForLogin[0].equals("")) {
						if (MainActivity.CURRENT_APP_VERSION_FROM_SERVER != 0 && MainActivity.CURRENT_DEVICE_APP_VERSION >= MainActivity.CURRENT_APP_VERSION_FROM_SERVER) {
							if (isLoginSuccess == 1)
								DialogActivity.alertDialogView(LoginZoomToU.this, "No network!", "No network connection, Please check your connection and try again");
							else if (isLoginSuccess == 2)
								DialogActivity.alertDialogView(LoginZoomToU.this, "Error!", new JSONObject(webServiceResponseForLogin[0]).getString("error_description"));
							else if (isLoginSuccess == 3)
								DialogActivity.alertDialogView(LoginZoomToU.this, "Server Error!", "Something went wrong, Please try later.");
							else if (!webServiceResponseForRoutific[0].equals("")) {
								if (webServiceResponseForRoutific[0].equals("401"))
									DialogActivity.alertDialogView(LoginZoomToU.this, "Alert!", "It looks like this login belongs to a customer account, not a courier account.\n" +
											"Please check the app store for the correct app, or contact support for more help.");
								else if (new JSONObject(webServiceResponseForRoutific[0]).getBoolean("isAccepted")) {

									TeamMemberList_Activity.arrayOfMyTeamList = null;
									loginPass.clearFocus();
									courier_autoLogin.loggedInSuccessfully(true,new JSONObject(webServiceResponseForRoutific[0]).getBoolean("isFirstLogin"));
									courier_autoLogin = null;
								} else
									courier_autoLogin.alertTermsAndConditionForCourier(true,new JSONObject(webServiceResponseForRoutific[0]).getBoolean("isFirstLogin"),LoginZoomToU.this);
							} else
								DialogActivity.alertDialogView(LoginZoomToU.this, "Server Error!", "Something went wrong, Please try later.");
							isLoginSuccess = 0;
						} else
							versionUpdateDialogView();
					} else
						DialogActivity.alertDialogView(LoginZoomToU.this, "Server Error!", "Something went wrong, Please try later.");
				} catch (Exception e){
					e.printStackTrace();
					isLoginSuccess = 0;
					DialogActivity.alertDialogView(LoginZoomToU.this, "Server Error!", "Something went wrong, Please try later.");
				}
			}
		}.execute();
	}


	
	/********* Check device is tablet **********/
	public static boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	void getLogin(){
		try {
			filterDayActiveListStr = "today";
			activeBookingTab = 0;
			if(checkInternetwithfunctionality.isConnectingToInternet())
				//new LoginAsyncTask().execute();
				LoginAsyncTask();
			else
				DialogActivity.alertDialogView(LoginZoomToU.this, "No Network!", "No network connection, Please try again later");
		} catch (Exception e) {
			e.printStackTrace();
			DialogActivity.alertDialogView(LoginZoomToU.this, "Sorry!", "Something went wrong. Please try again later");
		}
	}
	
	/***************  static field initialization in login view  *****************/
	public static void staticFieldInit(Context currentActivityContext){
		try {
			if(prefrenceForLogout == null)
				prefrenceForLogout = currentActivityContext.getApplicationContext().getSharedPreferences("isLogout", 0);
			 if(editorForLogout == null)
				 editorForLogout = prefrenceForLogout.edit();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		device_Density = currentActivityContext.getResources().getDisplayMetrics().density;

		imm = (InputMethodManager)currentActivityContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		checkInternetwithfunctionality = new Functional_Utility(currentActivityContext);
		// Share preference for remember username and password
		if(prefrenceForLogin == null)
		 	prefrenceForLogin = currentActivityContext.getApplicationContext().getSharedPreferences("userLoginDetail", 0);
		 if(loginEditor == null)
			loginEditor = prefrenceForLogin.edit();
		 
		 if(PushReceiver.prefrenceForPushy == null)
			 PushReceiver.prefrenceForPushy = currentActivityContext.getSharedPreferences("bookingId", 0);
		 if(PushReceiver.loginEditorForPushy == null)
			 PushReceiver.loginEditorForPushy = PushReceiver.prefrenceForPushy.edit();
		 
		 NOVA_BLACK = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Book.otf");
		 NOVA_BOLD = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Bold.otf");
		 NOVA_EXTRABOLD = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Bold.otf");
		 NOVA_LIGHT = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Light.otf");
		 NOVA_REGULAR = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Book.otf");
		 NOVA_SEMIBOLD = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Medium.otf");
		 NOVA_THIN = Typeface.createFromAsset(currentActivityContext.getAssets(), "GothamRnd-Light.otf");
		 
		// Get device height and width
		DisplayMetrics displayMetrics = currentActivityContext.getResources().getDisplayMetrics();
		width = displayMetrics.widthPixels;
		height = displayMetrics.heightPixels;
		
		savedImgDirOfFialedImg = Environment.getExternalStorageDirectory() + "/" + currentActivityContext.getResources().getString(R.string.dir_failedsignatureimg) + "/";
	}

	//    Method to check for other permission is enabled or not for the application (Marshmallow)
	//    If not enabled show popup to enable it.
	public static void checkForLocationAndOtherPermissionEnabled(final Context con) {
		try {
			if((int) Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.TIRAMISU){
				String[] permission = {Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE,
						Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS};
				if (ContextCompat.checkSelfPermission(con, permission[0]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[1]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[2]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[3]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[4]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[5]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[6]) == PackageManager.PERMISSION_DENIED
						) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
					alertDialog.setCancelable(false);
					alertDialog.setTitle("Permission required!");
					alertDialog.setMessage("Z2U for carriers is a location based application based on location services where we need to access your location and access to your images for profile picture and proof of delivery.\n\n" +"After this please do whitelist app from battery optimizations to receive job notifications.");
					alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) con,
									new String[]{Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
											Manifest.permission.READ_MEDIA_IMAGES,
											Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE,
											Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, 1000);
							dialog.dismiss();
						}
					});
					alertDialog.show();
				}
			}
			else  {
				String[] permission = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
						Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
						Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE,
						Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS};
				if (ContextCompat.checkSelfPermission(con, permission[0]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[1]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[2]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[3]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[4]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[5]) == PackageManager.PERMISSION_DENIED ||
						ContextCompat.checkSelfPermission(con, permission[6]) == PackageManager.PERMISSION_DENIED) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(con);
					alertDialog.setCancelable(false);
					alertDialog.setTitle("Permission required!");
					alertDialog.setMessage("Z2U for carriers is a location based application based on location services where we need to access your location and access to your images for profile picture and proof of delivery.\n\n" +"After this please do whitelist app from battery optimizations to receive job notifications.");
					alertDialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ActivityCompat.requestPermissions((Activity) con,
									new String[]{Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
											Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
											Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE,
											Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS}, 1000);
							dialog.dismiss();
						}
					});
					alertDialog.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//************ Show alert to Ignore battery optimization ***************
	public static void showAlertForIgnoreBatteryOptimize(Context activityContext) {
        try {
            if (PushReceiver.prefrenceForPushy.getInt("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS_", 0) != 1) {
                if ((int) Build.VERSION.SDK_INT >= 23) {
                    Intent intent = new Intent();
                    String packageName = activityContext.getPackageName();
                    PowerManager pm = (PowerManager) activityContext.getSystemService(Context.POWER_SERVICE);
                    if (pm.isIgnoringBatteryOptimizations(packageName)) {
                        intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                    } else {
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                    }
                    PushReceiver.loginEditorForPushy.putInt("REQUEST_IGNORE_BATTERY_OPTIMIZATIONS_", 1);
                    PushReceiver.loginEditorForPushy.commit();
                    activityContext.startActivity(intent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	/**************  Dialog for version update  **************/
	Dialog versionUpdateDialog;
	void versionUpdateDialogView(){
		try{
			tempMainLayoutLoginView.setVisibility(View.VISIBLE);
			if(versionUpdateDialog != null)
				versionUpdateDialog = null;
			versionUpdateDialog = new Dialog(LoginZoomToU.this);
			versionUpdateDialog.setCancelable(false);
			versionUpdateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			versionUpdateDialog.setContentView(R.layout.update_version);

			Window window = versionUpdateDialog.getWindow();
			window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) versionUpdateDialog.findViewById(R.id.versionNameHeaderTxt);

			TextView enterFieldDialogMsg = (TextView) versionUpdateDialog.findViewById(R.id.versionNameMsgTxt);

			Button enterFieldDialogDoneBtn = (Button) versionUpdateDialog.findViewById(R.id.updateBtnVersionTxt);


			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					versionUpdateDialog.dismiss();
					tempMainLayoutLoginView.setVisibility(View.GONE);
					//String api = "https://play.google.com/store/apps/details?id=com.zoom2u";
					try {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zoom2u")));
					} catch (android.content.ActivityNotFoundException anfe) {
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.zoom2u")));
					}
				}
			});

			versionUpdateDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
