package com.zoom2u;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.services.BG_Service_VersionUpdate;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.pushy.sdk.Pushy;

public class MainActivity extends Activity {

    public static int CURRENT_APP_VERSION_FROM_SERVER;
	public static int CURRENT_DEVICE_APP_VERSION;

    Courier_AutoLoginFeature courier_autoLogin;
    public static Double HomeLat,HomeLang;
    public static String HomeAddress;
	public static int NOTIFICATION_ID = -1;
	SharedPreferences sharedPref;
	SharedPreferences.Editor edit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPref = getSharedPreferences("Level", Context.MODE_PRIVATE);
		edit=sharedPref.edit();
		LoginZoomToU.mAuth_Firebase = FirebaseAuth.getInstance();

		Window window = MainActivity.this.getWindow();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor("#4b5054"));
		}


		Pushy.listen(this);
		setContentView(R.layout.activity_main);

		try {
			CURRENT_DEVICE_APP_VERSION = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		try {
			NOTIFICATION_ID = getIntent().getIntExtra("NOTIFICATION_ID", -1);
		} catch (Exception e){
			NOTIFICATION_ID = -1;
			e.printStackTrace();
		}

		Intent versionUpdateService = new Intent(getApplicationContext(), BG_Service_VersionUpdate.class);
		startService(versionUpdateService);
		versionUpdateService = null;

		LoginZoomToU.staticFieldInit(MainActivity.this);
		courier_autoLogin = new Courier_AutoLoginFeature(MainActivity.this);
		courier_autoLogin.getPushyTokenId();

		if (courier_autoLogin.isLogin) {
			if (courier_autoLogin.isLogin && LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				LoginAsyncTask();
				//new LoginAsyncTask().execute();
			else
				timerToGetLogin();
		} else
			timerToGetLogin();
	}


	public void storeLevel(String level){
		edit.putString("level",level);
		edit.commit();
	}

	public String getStoredLevel(){
		String level = sharedPref.getString("level", null);
		return  level;
	}

	public static boolean isIsBackGroundGray() {
		/*try {
			if (WebserviceHandler.jObjOfCurrentCourierLevel.getString("Level").equals("Dynamo"))
			   return false;
		   else
			   return false;

		}catch (Exception e) {
			e.printStackTrace();
		}*/
		return false;
	}





	@Override
	public void onStart() {
		super.onStart();
		// Check if user is signed in (non-null) and update UI accordingly.
		LoginZoomToU.firebase_CurrentUser = LoginZoomToU.mAuth_Firebase.getCurrentUser();
//		Log.i("", "---  Current firebase user --- "+LoginZoomToU.firebase_CurrentUser);
	}

	private void timerToGetLogin() {
		// set timer to show splash screen for 3 second
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				switchToLoginView();
			}
		}, 3000);
	}

	private void switchToLoginView() {
		try {
			Intent i = new Intent(getApplicationContext(), LoginZoomToU.class);
			startActivity(i);
			i = null;
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void LoginAsyncTask (){
		final String[] webServiceResponseForLogin = {"0"};
		final String[] webServiceResponseForRoutific = {""};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				// before execution
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if (CURRENT_APP_VERSION_FROM_SERVER == 0)
						webServiceHandler.CheckForCourierAppVerSionUpdate();

					webServiceResponseForLogin[0] = webServiceHandler.getUserLogin(courier_autoLogin.userName, courier_autoLogin.password);
					JSONObject responseData = null;
					try {
						responseData = new JSONObject(webServiceResponseForLogin[0]);
						String accessToken = responseData.getString("access_token");
						try {
							LoginZoomToU.IS_TEAM_LEADER = responseData.getBoolean("isTeamLeader");
							LoginZoomToU.CARRIER_ID = responseData.getInt("CarrierId");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						courier_autoLogin.storeCredentialAfterLogin(true, courier_autoLogin.userName, courier_autoLogin.password, accessToken, LoginZoomToU.CARRIER_ID, LoginZoomToU.IS_TEAM_LEADER);
						responseData = null;
						webServiceResponseForRoutific[0] = webServiceHandler.CheckRoutific();
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
						Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
						List<Address> addresses = geocoder.getFromLocation(MainActivity.HomeLat, MainActivity.HomeLang, 1);
						MainActivity.HomeAddress = addresses.get(0).getAddressLine(0);

					}
				}catch (Exception e){ }

				try {
					if (CURRENT_APP_VERSION_FROM_SERVER != 0 && CURRENT_DEVICE_APP_VERSION >= CURRENT_APP_VERSION_FROM_SERVER) {
						if (LoginZoomToU.isLoginSuccess == 1 || LoginZoomToU.isLoginSuccess == 2) {
							LoginZoomToU.isLoginSuccess = 0;
							timerToGetLogin();
						} else if (LoginZoomToU.isLoginSuccess == 3) {
							LoginZoomToU.isLoginSuccess = 0;
							switchToLoginView();
						} else if (!webServiceResponseForLogin[0].equals("") && !webServiceResponseForRoutific[0].equals("")) {
							if (new JSONObject(webServiceResponseForRoutific[0]).getBoolean("isAccepted")) {
								if (LoginZoomToU.pushyRegId.equals(""))
									courier_autoLogin.getPushyTokenId();
								courier_autoLogin.loggedInSuccessfully(false,false);
								courier_autoLogin = null;
							} else
								switchToLoginView();
						} else
							switchToLoginView();
					} else
						switchToLoginView();
				} catch (Exception e){
					e.printStackTrace();
					LoginZoomToU.isLoginSuccess = 0;
					switchToLoginView();
				}
			}
		}.execute();
	}





//	//************ Get app version in Integer *******
//	public static int getDeviceAppVersion (String versionName){
//		if (versionName.equals(""))
//			return 0;
//		else {
//			try {
//				StringBuilder strNew = new StringBuilder(versionName.length());
//				String versionNameTxt = null;
//				char charRead;
//				for (int i = 0; i < versionName.length(); i++) {
//					charRead = versionName.charAt(i);
//					if(charRead != '.')
//						strNew.append(charRead);
//				}
//
//				versionNameTxt = strNew.toString();
//
//				return Integer.parseInt(versionNameTxt);
//			} catch (NumberFormatException e) {
//				e.printStackTrace();
//				return 0;
//			}
//		}
//	}

}
