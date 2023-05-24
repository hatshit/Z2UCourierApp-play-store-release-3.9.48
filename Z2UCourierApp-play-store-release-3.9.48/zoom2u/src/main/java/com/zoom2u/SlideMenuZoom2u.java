package com.zoom2u;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.suggestprice_team.courier_team.DrcActivity;
import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.suggestprice_team.courier_team.community.AddmemberActivity;
import com.suggestprice_team.courier_team.community.CommunityBookingActivity;
import com.suggestprice_team.courier_team.community.CommunityListActivity;
import com.z2u.booking.vc.CompletedView;
import com.z2u.chat.Firebase_Auth_Provider;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.build_me_route.FirstBuildMeActivity;
import com.zoom2u.datamodels.PreferenceImgUpload;
import com.zoom2u.datamodels.SharePreference_FailedImg;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogReasonForLateDelivery;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.services.ServiceToGetCourierLevel;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.services.Service_ResendFailedImgToServer;
import com.zoom2u.slidemenu.AccountDetailFragment;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.CourierRouteDetail;
import com.zoom2u.slidemenu.DialogBecomeTeamLeadTermsCondition;
import com.zoom2u.slidemenu.Invite_Customer;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.slidemenu.ShiftFragment;
import com.zoom2u.slidemenu.driversupport.DriverSupport;
import com.zoom2u.slidemenu.offerrequesthandlr.ChatListForBid;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.slidemenu.unallocated_runs.UnallocatedRunsFragments;
import com.zoom2u.slidemodel.MenuSection_Interface;
import com.zoom2u.slidemodel.Menu_SectionModel;
import com.zoom2u.slidemodel.NavDrawerItem;
import com.zoom2u.userlatlong.GPSTracker;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SlideMenuZoom2u extends FragmentActivity {

	RelativeLayout slideMenuTopBarLayout;
	BookingView fragment_booking;
	 MainLayout mLayout;
	 private ListView lvMenu;
	 ImageView btMenu, logoImg, refreshBtn, chatBtnBookingView;
	 TextView tvTitle;
	 Dialog pushNotifyDialog;

	 public static TextView countChatBookingView;
	 TextView countChatSlideView;
//	 public static boolean isDragEnabled = false;    // To Drag Active list item

	 int selectedItemOnSlideList;

	 private String[] navMenuTitles;

	private ArrayList<MenuSection_Interface> navDrawerItems;

	public static NavDrawerListAdapter HOME_SLIDE_MENU_ADAPTER;

	public static boolean isDropOffCompleted=false;
	public static boolean isPickupOffCompleted=false;

	Window window;

	public static Context contxtForSharedPrefUploadImg;

	public static RoundedImageView courierProfileImg_Menu;
	TextView courierNameTxt_Menu;
	public static TextView courierCompanyNameTxt_Menu, courierFirstLastName;
	Button signOutBtn;
	LinearLayout build_me;
	public static int NEW_LOCATION_PERNISSION_DIALOG = 1011;

	  @Override
	 protected void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  try {
		 if(!isPickupOffCompleted && !isDropOffCompleted)
		 LoginZoomToU.checkForLocationAndOtherPermissionEnabled(SlideMenuZoom2u.this);        // Check location or capture image permission for Marshmallow

		  new Firebase_Auth_Provider(SlideMenuZoom2u.this, true);

		  contxtForSharedPrefUploadImg = SlideMenuZoom2u.this;
		  setCourierToOnlineForChat();
		  if(savedInstanceState != null) {            // If process kill by android OS
              restoreInstanceAfterKillOnBG(savedInstanceState);
              callLocationService();
          } else {
			  try {
				  if (getIntent().hasExtra("intentFromLoginUI")){
					  new LoadChatBookingArray(SlideMenuZoom2u.this, 1);  //********* LoadBooking Chat and Bid chat*******

					  callLocationService();
					  SharePreference_FailedImg sharedPrefForFailImg = new SharePreference_FailedImg();
					  List<PreferenceImgUpload> arrayOfPrefOfFailedImg = sharedPrefForFailImg.getImgToUploadToServerFromPref(SlideMenuZoom2u.this);
					  sharedPrefForFailImg = null;
					  if(arrayOfPrefOfFailedImg != null){
						  if(arrayOfPrefOfFailedImg.size() > 0){
							  Intent callResendService = new Intent(SlideMenuZoom2u.this, Service_ResendFailedImgToServer.class);
							  startService(callResendService);
							  callResendService = null;
						  }
                          arrayOfPrefOfFailedImg = null;
                      }else{
						  /* ******** If external storage is mounted then remove image from External storage
						  // otherwise it remove image from Internal storage  */
						  if (BG_ImageUploadToServer.prepareDirectory() && BG_ImageUploadToServer.isExternalStorageWritable()) {
							  try {
								  File tempdir = new File(LoginZoomToU.savedImgDirOfFialedImg);
								  removeAllImageFromDirectory(tempdir);
							  } catch (Exception e) {
								  e.printStackTrace();
							//	  Toast.makeText(SlideMenuZoom2u.this, "Error while removing..", Toast.LENGTH_LONG).show();
							  }
						  } else {
							  try {
								  ContextWrapper wrapper = new ContextWrapper(SlideMenuZoom2u.this);
								  File tempdir = wrapper.getDir("Z2U_FailedSignatureImg", MODE_PRIVATE);
								  removeAllImageFromDirectory(tempdir);
							  } catch (Exception e) {
								  e.printStackTrace();
							//	  Toast.makeText(SlideMenuZoom2u.this, "Error while removing..", Toast.LENGTH_LONG).show();
							  }
						  }

                      }
					  if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null) {
						  new Service_CheckBatteryLevel(SlideMenuZoom2u.this);
						  ChatViewBookingScreen.mFirebaseRef.child("/couriers/" + LoginZoomToU.courierID + "/status/online").onDisconnect().setValue(0);
					  }
                  }
				  else if(getIntent().hasExtra("FromRunBatchNotification")){
					  LoginZoomToU.activeBookingTab=1;
					  ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
				  	  BookingView.bookingViewSelection=2;
					  FragmentManager fm = SlideMenuZoom2u.this.getSupportFragmentManager();
					  FragmentTransaction ft = fm.beginTransaction();
					  fm = null;
					  refreshSlideMenuList(ft);
					  ft.commit();
					  ft = null;
				  }
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			  if(LoginZoomToU.NOVA_BOLD == null)
				  LoginZoomToU.staticFieldInit(SlideMenuZoom2u.this);
		  }
		  slideMenuView();          // initialize slide menu view

		} catch (Exception e) {
			e.printStackTrace();
		}

	    LoginZoomToU.showAlertForIgnoreBatteryOptimize(SlideMenuZoom2u.this);   //**************** Fire Intent for battery ignore optimaze *******

	  }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == NEW_LOCATION_PERNISSION_DIALOG) {
			getLocationAndLevelOfCourier();
		}
	}



	private boolean checkNotificationPermissions() {
		return ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED ;
	}

	private void methodForNotification(){
		if(!checkNotificationPermissions()) {

			try {

				Dialog enterFieldDialog = new Dialog(this);

				enterFieldDialog.setCancelable(true);
				enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				enterFieldDialog.setContentView(R.layout.permission_dailog_gps);

				Window window = enterFieldDialog.getWindow();
				window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
				WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.CENTER;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);
				TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

				enterFieldDialogHEader.setText("Push Notification!");

				TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

				enterFieldDialogMsg.setText("Receive Booking offers and updates by allowing Zoom2u to send notifications. Please update your notification settings here. ");

				Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);
				Button cancel = (Button) enterFieldDialog.findViewById(R.id.cancel);

				cancel.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						enterFieldDialog.cancel();
					}
				});

				enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						enterFieldDialog.cancel();

						Intent intent = new Intent();
						intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.putExtra("android.provider.extra.APP_PACKAGE", getPackageName());
						startActivity(intent);
					}
				});
				enterFieldDialog.show();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}


	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == 1000) {
			// If request is cancelled, the result arrays are empty.
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
						&& ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
					if (android.os.Build.VERSION.SDK_INT >= 29) {
						boolean backgroundLocationPermissionApproved = ActivityCompat
								.checkSelfPermission((Activity) this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
								PackageManager.PERMISSION_GRANTED;

						if (backgroundLocationPermissionApproved) {
							// App can access location both in the foreground and in the background.
							// Start your service that doesn't have a foreground service type
							// defined.
							getLocationAndLevelOfCourier();
						} else {
							// App can only access location in the foreground. Display a dialog
							// warning the user that your app must have all-the-time access to
							// location in order to function properly. Then, request background
							// location.
							accessBackgrpundLocationPermissionFor_android10();
						}
					} else {
						getLocationAndLevelOfCourier();
					}
				} else {
					getLocationAndLevelOfCourier();
				}
			} else {
				//Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
				getLocationAndLevelOfCourier();
				if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.TIRAMISU)
				methodForNotification();
			}
		}
		else if(requestCode == 99){
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				Toast.makeText(this, "Location Permission Granted.", Toast.LENGTH_SHORT).show();
			}
			else {
				DialogActivity.alertDialogView(this, "Alert!", "This option is unavailable while location services are turned off or restricted.\n" +
						"Please go to your phone setting to turn on your location services and ensure that the Zoom2u app has permissions enabled.");

			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void accessBackgrpundLocationPermissionFor_android10() {
		Intent intent = new Intent(SlideMenuZoom2u.this, LocationPermissionDialog.class);
		startActivityForResult(intent, SlideMenuZoom2u.NEW_LOCATION_PERNISSION_DIALOG);
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setCancelable(false);
//		builder.setTitle("Update location permission!");
//		builder.setMessage("Z2U for carriers doesn't work reliably when Location permission is not granted as \"all-the-time\" Tap here to go to Settings and grant permission.");
//
//		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//			}
//		});
//
//		builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				LoginZoomToU.checkForOtherPermissionEnabled(SlideMenuZoom2u.this);        // Check capture image permission for Marshmallow
//			}
//		});
//
//		builder.show();
	}

	//************** Remove all image from directory *************
	private void removeAllImageFromDirectory (File tempdir){
		if (tempdir.isDirectory()) {
			for (File child : tempdir.listFiles()) {
				child.delete();
			}
		}
	}

    private void callLocationService() {
        Functional_Utility.removeLocationTimer();
        Intent serviceIntentForLocation = new Intent(SlideMenuZoom2u.this, ServiceForSendLatLong.class);
		startService(serviceIntentForLocation);
        serviceIntentForLocation = null;

	//	refreshNewAndActiveBookingCount();	//************ Refresh New and Active booking count
    }

    /********************  Call for courier location and courier level *************/
	 void getLocationAndLevelOfCourier(){
			GPSTracker gpsLocation = new GPSTracker(SlideMenuZoom2u.this);
			  try {
				  LoginZoomToU.getCurrentLocatnlatitude = String.valueOf(gpsLocation.getLatitude());
				  LoginZoomToU.getCurrentLocatnLongitude = String.valueOf(gpsLocation.getLongitude());
				  gpsLocation = null;
			}catch (Exception e2) {
				e2.printStackTrace();
				LoginZoomToU.getCurrentLocatnlatitude = "0.0";
				LoginZoomToU.getCurrentLocatnLongitude = "0.0";
				gpsLocation = null;
			}

		 try{
			  if(WebserviceHandler.jObjOfCurrentCourierLevel == null || LoginZoomToU.courierID.equals(""))
				  serviceToGetCourierLevel();
			}catch(Exception e){
				e.printStackTrace();
				LoginZoomToU.courierID = "";
				serviceToGetCourierLevel();
			}
		}

	 //  Call Intent service to get courier level
	 void serviceToGetCourierLevel(){
		 Intent serviceIntent = new Intent(SlideMenuZoom2u.this, ServiceToGetCourierLevel.class);
		 startService(serviceIntent);
		 serviceIntent = null;
	 }

	 /*************** In-it Slide menu view  ***************/
	 @SuppressLint("SuspiciousIndentation")
	 @SuppressWarnings("ResourceType")
	 void slideMenuView(){
		 try {
			runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mLayout = (MainLayout) SlideMenuZoom2u.this.getLayoutInflater().inflate(R.layout.slidemenuzoom2u, null);
						setContentView(mLayout);
					}
				});

				 if(slideMenuTopBarLayout == null)
					slideMenuTopBarLayout = (RelativeLayout) findViewById(R.id.slideMenuTopBarLayout);
				 slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A7E2"));

				  if(lvMenu == null)
				  	  lvMenu = (ListView) findViewById(R.id.menu_listview);
				  if(logoImg == null)
					  logoImg = (ImageView) findViewById(R.id.logoImg);
				  if(refreshBtn == null)
					refreshBtn = (ImageView) findViewById(R.id.refreshBtn);
				 refreshBtn.setImageResource(R.drawable.refresh_list);
				 refreshBtn.setVisibility(View.VISIBLE);

			 courierProfileImg_Menu = (RoundedImageView) findViewById(R.id.courierProfileImg_Menu);
			 courierNameTxt_Menu = (TextView) findViewById(R.id.courierNameTxt_Menu);
			 courierNameTxt_Menu.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
			 courierFirstLastName = (TextView) findViewById(R.id.courierFirstLastName);
			 courierFirstLastName.setTypeface(LoginZoomToU.NOVA_BOLD);
			 courierCompanyNameTxt_Menu = (TextView) findViewById(R.id.courierCompanyNameTxt_Menu);
			 courierCompanyNameTxt_Menu.setTypeface(LoginZoomToU.NOVA_REGULAR);
			 signOutBtn = (Button) findViewById(R.id.signOutBtn);
			 signOutBtn.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
			 signOutBtn.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
						 LogoutCourier();
					 	//new LogoutCourier().execute();
					 else
						 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "No Network!", "No network connection, Please try again later");
				 }
			 });
			 build_me=findViewById(R.id.build_me);
			 build_me.setOnClickListener(new OnClickListener() {
				 @Override
				 public void onClick(View v) {
					 Intent intent=new Intent(SlideMenuZoom2u.this, FirstBuildMeActivity.class);
					 startActivity(intent);
				 }
			 });

			 TypedArray navMenuIcons;
			 // nav drawer icons from resources
			 navDrawerItems = new ArrayList<MenuSection_Interface>();
			 if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0)
			 {
				 navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items_team);
				 navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons_team);
				 // adding nav drawer items to array
				 navDrawerItems.add(new Menu_SectionModel("My Deliveries"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
				 navDrawerItems.add(new Menu_SectionModel("New Customers"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
				 navDrawerItems.add(new Menu_SectionModel("My Details"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[10], navMenuIcons.getResourceId(10, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[11], navMenuIcons.getResourceId(11, -1)));

			 }
			 else
			 {
				 navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
				 navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

				 // adding nav drawer items to array
				 navDrawerItems.add(new Menu_SectionModel("My Deliveries"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
				 navDrawerItems.add(new Menu_SectionModel("New Customers"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
				 navDrawerItems.add(new Menu_SectionModel("My Details"));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[8], navMenuIcons.getResourceId(8, -1)));
				 navDrawerItems.add(new NavDrawerItem(navMenuTitles[9], navMenuIcons.getResourceId(9, -1)));

			 }

			// Recycle the typed array
			navMenuIcons.recycle();
			navMenuIcons = null;

			lvMenu.setOnItemClickListener(new SlideMenuClickListener());
			// setting the nav drawer list adapter
			HOME_SLIDE_MENU_ADAPTER = new NavDrawerListAdapter(SlideMenuZoom2u.this, navDrawerItems);
			lvMenu.setAdapter(HOME_SLIDE_MENU_ADAPTER);

			btMenu = (ImageView) findViewById(R.id.button_menu);
			btMenu.setImageResource(R.drawable.nav_menu_white);
			btMenu.setOnClickListener(new OnClickListener() {
				   @Override
				   public void onClick(View v) {
			// Show/hide the menu
			try {
				LoginZoomToU.imm.hideSoftInputFromWindow(tvTitle.getWindowToken(), 0);
				selectedMenuItems();
				HOME_SLIDE_MENU_ADAPTER.notifyDataSetChanged();
				toggleMenu(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
		 }
			});

				refreshBtn.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (ConfirmPickUpForUserName.isDropOffSuccessfull == 17){
							Intent intentHeroLevel = new Intent(SlideMenuZoom2u.this, HeroLevel_View.class);
							startActivity(intentHeroLevel);
							intentHeroLevel = null;
						}else {
							FragmentManager fm = SlideMenuZoom2u.this.getSupportFragmentManager();
							FragmentTransaction ft = fm.beginTransaction();
							fm = null;
							refreshSlideMenuList(ft);
							ft.commit();
							ft = null;
						}
					}
				});

			if (chatBtnBookingView == null)
				chatBtnBookingView = (ImageView) findViewById(R.id.chatBtnBookingView);
			chatBtnBookingView.setOnClickListener(new View.OnClickListener(){
				 @Override
				 public void onClick(View v) {
					 if (ConfirmPickUpForUserName.isDropOffSuccessfull != 13) {
						 openBookingChatScreen();
					 } else {
						 if (RequestView.requestView_pojoList != null) {
							 if (RequestView.requestView_pojoList.size() > 0) {
								 Intent chatViewIntent = new Intent(SlideMenuZoom2u.this, ChatListForBid.class);
								 startActivity(chatViewIntent);
								 overridePendingTransition(R.anim.right_in, R.anim.left_out);
								 chatViewIntent = null;
							 } else
								 openBookingChatScreen();
						 } else
							 openBookingChatScreen();
					 }
				 }
			 });

			if (countChatSlideView == null)
				 countChatSlideView = (TextView) findViewById(R.id.countChatBookingView);
			countChatSlideView.setTypeface(LoginZoomToU.NOVA_EXTRABOLD);
			countChatSlideView.setVisibility(View.GONE);
			countChatBookingView = countChatSlideView;

			 tvTitle = (TextView) findViewById(R.id.activity_main_content_title);



			 if(CourierRouteDetail.courierRouteView != null)
				 CourierRouteDetail.courierRouteView = null;

			 if(window == null)
			     window = SlideMenuZoom2u.this.getWindow();
			 setHeaderAndTitleBackground();
			 FragmentManager fm = SlideMenuZoom2u.this.getSupportFragmentManager();
			 FragmentTransaction ft = fm.beginTransaction();
			 fm = null;
			 Model_DeliveriesToChat.showExclamationForUnreadChat(countChatSlideView);
			if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 || ConfirmPickUpForUserName.isDropOffSuccessfull == 12
					|| ConfirmPickUpForUserName.isDropOffSuccessfull == 15){
				  refreshSlideMenuList(ft);
			}else if(ConfirmPickUpForUserName.isDropOffSuccessfull == 13){
				RequestView fragment = new RequestView();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Bid Requests", 13, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				//headerWithNormalStatusBar();
				setHeaderAndTitleBackground();
				fragment = null;
			}else if(ConfirmPickUpForUserName.isDropOffSuccessfull == 19){
				UnallocatedRunsFragments fragment = new UnallocatedRunsFragments();
				//fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Available Runs", 19, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#00A7E2"), R.drawable.iconchat);
				setHeaderAndTitleBackground();
				fragment = null;
			}
			else if(ConfirmPickUpForUserName.isDropOffSuccessfull == 16){
				Invite_Customer fragment = new Invite_Customer();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Invite Customer", 16, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				logoImg.setVisibility(View.INVISIBLE);
				//headerWithNormalStatusBar();
				setHeaderAndTitleBackground();
				fragment = null;
			}else if (ConfirmPickUpForUserName.isDropOffSuccessfull == 17) {
				AccountDetailFragment fragment = new AccountDetailFragment();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Profile", 17, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
			//	refreshBtn.setImageResource(R.drawable.icon_favorite);
				//headerWithGreenStatusBar();
				setHeaderAndTitleBackground();
				fragment = null;
			}else if (ConfirmPickUpForUserName.isDropOffSuccessfull == 18) {
				DriverSupport fragment = new DriverSupport();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Support", 18, View.VISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				refreshBtn.setVisibility(View.GONE);
				//headerWithGunMetalStatusBar();
				setHeaderAndTitleBackground();
				fragment = null;
			}
			ft.commit();
			ft = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
		 	courierNameTxt_Menu.setText(LoginZoomToU.courierName);
		 	courierCompanyNameTxt_Menu.setText(LoginZoomToU.courierCompany);
		 	if (!LoginZoomToU.courierImage.equals("")) {
				Picasso.with(SlideMenuZoom2u.this).load(LoginZoomToU.courierImage).into(courierProfileImg_Menu);
				courierFirstLastName.setVisibility(View.GONE);
			}
		} catch (Exception e) {
		 	e.printStackTrace();
		}

		 try {
		 	String[] courierNameArray = LoginZoomToU.courierName.split(" ");
		 	courierFirstLastName.setText(((String)((((String)courierNameArray[0]).charAt(0)+""+((String) courierNameArray[courierNameArray.length-1]).charAt(0)))).toUpperCase());
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 }

	private void openBookingChatScreen() {
		Intent chatViewIntent;
		chatViewIntent = new Intent(SlideMenuZoom2u.this, ChatViewBookingScreen.class);
		startActivity(chatViewIntent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
		chatViewIntent = null;
	 }

	/*********  Selected menu item  **************/
	 protected void selectedMenuItems() {
		 switch (ConfirmPickUpForUserName.isDropOffSuccessfull) {
			 case 11:
				selectedItemOnSlideList = 1;
				break;
			 case 12:
				selectedItemOnSlideList = 2;
				break;
			 case 13:
				selectedItemOnSlideList = 3;
				break;
			 case 15:
				selectedItemOnSlideList = 5;
				break;
			 case 16:
				 if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0)
					 selectedItemOnSlideList = 10;
				 else selectedItemOnSlideList=8;
				 break;
			 case 17:
				 if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0)
					 selectedItemOnSlideList = 12;
				 else selectedItemOnSlideList=11;
				 break;
			 case 18:
				 if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0)
				selectedItemOnSlideList = 13;
				 else selectedItemOnSlideList=10;
			   break;
			 case 19:
				selectedItemOnSlideList = 6;
				break;
			default:
				break;
		}
	}

	/****************  Refresh slide menu list items ****************/
	 protected void refreshSlideMenuList(FragmentTransaction ft){
			if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11){
				fragment_booking = new BookingView();
				fragment_booking.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment_booking);
				setSlideMenuFragmentItems("Bookings", 11, View.GONE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				refreshBtn.setImageResource(R.drawable.refresh_list);
				if(WebserviceHandler.jObjOfCurrentCourierLevel != null)
					changeStatusBarColor();
				else
					changeSlideMenuTopBaColorWithDelay();
				//fragment = null;
				refreshNewAndActiveBookingCount();
			}else if(ConfirmPickUpForUserName.isDropOffSuccessfull == 12){
				CourierRouteDetail fragment = new CourierRouteDetail();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.add(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Routes", 12, View.VISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				refreshBtn.setImageResource(R.drawable.refresh_list);
				setHeaderAndTitleBackground();
				fragment = null;
			}else if(ConfirmPickUpForUserName.isDropOffSuccessfull == 15){
				ShiftFragment fragment = new ShiftFragment();
				fragment.setSlideMenuChatCounterTxt(countChatSlideView);
				ft.replace(R.id.activity_main_content_fragment, fragment);
				setSlideMenuFragmentItems("Delivery Runs", 15, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
				refreshBtn.setImageResource(R.drawable.refresh_list);
				setHeaderAndTitleBackground();
				fragment = null;
			}
	}

	//************ Refresh New and Active booking count ************
	private void refreshNewAndActiveBookingCount() {
		Intent deliveryCountIntent = new Intent(SlideMenuZoom2u.this, ServiceForCourierBookingCount.class);
		deliveryCountIntent.putExtra("Is_API_Call_Require", 1);
		startService(deliveryCountIntent);
		deliveryCountIntent = null;
	}

	private void changeSlideMenuTopBaColorWithDelay() {
		try{
			Timer t = new Timer();
			t.schedule(new TimerTask() {
				@Override
				public void run() {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							changeStatusBarColor();
						}
					});
				}
			}, 2000);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	 @SuppressLint("NewApi")
	private void changeStatusBarColor(){
			try {
				if(WebserviceHandler.jObjOfCurrentCourierLevel != null){
					try {
						if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11){
							btMenu.setImageResource(R.drawable.nav_menu_white);
							refreshBtn.setImageResource(R.drawable.refresh_list);

							if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
								window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
								window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
							}
							switch (WebserviceHandler.jObjOfCurrentCourierLevel.getString("Level")) {
							case "Dynamo":
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								break;
							case "Recruit":
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								break;
							case "Warrior":
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								break;
							case "Elite":
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								break;
							case "Legend":
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								break;
							default:
								if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
									window.setStatusBarColor(Color.parseColor("#00A6E2"));
								   slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
								fragment_booking.setTabBackgroundblue();
								   break;
							}
						}else{
							window.setStatusBarColor(Color.BLACK);
							slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	 }

	@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	  getMenuInflater().inflate(R.menu.main, menu);
	  return true;
	 }

	 public void toggleMenu(View v) {
	  mLayout.toggleMenu();
	 }

	 /**************** Set values on slide menu item item selection  *******************/
	 public void setSlideMenuFragmentItems(String fragTxt, int viewingItem, int refreshBtnVisibility,
			 int btnMenuImg, int tvTitleColor, int chatBtnImage) {
		 tvTitle.setText(fragTxt);
		 tvTitle.setVisibility(View.VISIBLE);
		 logoImg.setVisibility(View.GONE);
		 refreshBtn.setVisibility(refreshBtnVisibility);
		 ConfirmPickUpForUserName.isDropOffSuccessfull = viewingItem;
		 btMenu.setImageResource(btnMenuImg);
		 tvTitle.setTextColor(tvTitleColor);
		 chatBtnBookingView.setImageResource(chatBtnImage);
	}

	//************* Refresh Home Slide menu adapter *************
	public static void refreshHomeSlideMenuAdapter() {
		try {
			if (SlideMenuZoom2u.HOME_SLIDE_MENU_ADAPTER != null)
				SlideMenuZoom2u.HOME_SLIDE_MENU_ADAPTER.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//************ Refresh Home view slide menu *********
	public static void notBidrequestCount(List<RequestView_DetailPojo> newBidList) {
		RequestView.COUNT_FOR_NOTBIDYET = 0;
		if (newBidList.size() > 0) {
			for (RequestView_DetailPojo requestViewDetailPojo : newBidList) {
				String utcDateTime = LoginZoomToU.checkInternetwithfunctionality.returnDateToShowBidActiveFor(requestViewDetailPojo.getDropDateTime());
				String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiff(utcDateTime, true);
				if (!minInStr.equals("Expired"))
					RequestView.COUNT_FOR_NOTBIDYET++;
			}
		}
		try {
			if (SlideMenuZoom2u.HOME_SLIDE_MENU_ADAPTER != null)
                SlideMenuZoom2u.HOME_SLIDE_MENU_ADAPTER.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class SlideMenuClickListener implements ListView.OnItemClickListener {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			/****************  Call service for get courier updated point  **********/
				try{
					Intent serviceIntent = new Intent(SlideMenuZoom2u.this, ServiceToGetCourierLevel.class);
					startService(serviceIntent);
					serviceIntent = null;
				}catch(Exception e){
					e.printStackTrace();
				}
			/************************************************************************/

				selectedItemOnSlideList = position;

				String selectedItem = "";
				if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0) {
					if (position != 0 && position != 9 && position != 11) {
						view.setBackgroundColor(Color.parseColor("#D1D0CE"));
						selectedItem = ((NavDrawerItem) navDrawerItems.get(position)).getTitle();
					}
				}else{
					if (position != 0 && position != 7 && position != 9) {
						view.setBackgroundColor(Color.parseColor("#D1D0CE"));
						selectedItem = ((NavDrawerItem) navDrawerItems.get(position)).getTitle();
					}

				}
				String currentItem = tvTitle.getText().toString();

				if (selectedItem.compareTo(currentItem) == 0) {
				  mLayout.toggleMenu();
				  return;
				}

				FragmentManager fm = SlideMenuZoom2u.this.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				fm = null;
				Fragment fragment = null;

				if(selectedItem.compareTo("Bookings") == 0){
					LoginZoomToU.filterDayActiveListStr = "today";
					LoginZoomToU.activeBookingTab = 0;
					fragment = new BookingView();
					((BookingView) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 1;
					setSlideMenuFragmentItems("Bookings", 11, View.GONE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					if(WebserviceHandler.jObjOfCurrentCourierLevel != null)
						changeStatusBarColor();
					else
						changeSlideMenuTopBaColorWithDelay();
					refreshBtn.setImageResource(R.drawable.refresh_list);

				//	refreshNewAndActiveBookingCount();	//************ Refresh New and Active booking count
				}else if(selectedItem.compareTo("Routes") == 0){
					fragment = new CourierRouteDetail();
					((CourierRouteDetail) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 0;
					setSlideMenuFragmentItems("Routes", 12, View.VISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					refreshBtn.setImageResource(R.drawable.refresh_list);
					setHeaderAndTitleBackground();
					//headerWithNormalStatusBar();
					setHeaderAndTitleBackground();
				} else if(selectedItem.compareTo("Bid Request") == 0){
					fragment = new RequestView();
					((RequestView) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 0;
					setSlideMenuFragmentItems("Bid Request", 13, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					setHeaderAndTitleBackground();
					//headerWithNormalStatusBar();
					setHeaderAndTitleBackground();
				}else if (selectedItem.compareTo("Leads") == 0) {
					  fragment = new Invite_Customer();
					  ((Invite_Customer) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					  BookingView.bookingViewSelection = 0;
					  setSlideMenuFragmentItems("Invite Customer", 16, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					  setHeaderAndTitleBackground();
					  //headerWithGreenStatusBar();
					  //headerWithNormalStatusBar();
				}else if (selectedItem.compareTo("Delivery Runs") == 0) {
					fragment = new ShiftFragment();
					((ShiftFragment) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 0;
					setSlideMenuFragmentItems("Delivery Runs", 15, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					refreshBtn.setImageResource(R.drawable.refresh_list);
					setHeaderAndTitleBackground();
					//headerWithGreenStatusBar();
					//headerWithNormalStatusBar();
				}else if (selectedItem.compareTo("Unallocated Runs") == 0) {
					fragment = new UnallocatedRunsFragments();
					//((Unallocated_Runs) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 0;
					setSlideMenuFragmentItems("Available Runs", 19, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					refreshBtn.setImageResource(R.drawable.refresh_list_blue);
					//headerWithNormalStatusBar();
					setHeaderAndTitleBackground();
				}
				else if (selectedItem.compareTo("Account Details") == 0) {
					  fragment = new AccountDetailFragment();
					  ((AccountDetailFragment) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					  BookingView.bookingViewSelection = 0;
					  setSlideMenuFragmentItems("Profile", 17, View.INVISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					  setHeaderAndTitleBackgroundForProfile();
					  //headerWithGreenStatusBar();
				//	 refreshBtn.setImageResource(R.drawable.icon_favorite);
				}else if(selectedItem.compareTo("Barcode Scanner") == 0){
					Intent intent=new Intent(SlideMenuZoom2u.this, BarcodeScanner.class);
					intent.putExtra("ScanAWBForPick", 1);
					startActivity(intent);
					intent = null;
				}else if(selectedItem.compareTo("Support") == 0){
					fragment = new DriverSupport();
					((DriverSupport) fragment).setSlideMenuChatCounterTxt(countChatSlideView);
					BookingView.bookingViewSelection = 0;
					setSlideMenuFragmentItems("Support", 18, View.VISIBLE, R.drawable.nav_menu_white, Color.parseColor("#FFFFFF"), R.drawable.new_iconchat);
					refreshBtn.setVisibility(View.GONE);
					CompletedView.endlessCount = 1;
					//headerWithGunMetalStatusBar();
					setHeaderAndTitleBackground();
				}else if (selectedItem.compareTo("Your Team")==0){
						if (LoginZoomToU.IS_TEAM_LEADER||LoginZoomToU.CARRIER_ID==0)
					if (LoginZoomToU.CARRIER_ID == 0) {
						DialogBecomeTeamLeadTermsCondition dialogBecomeTeamLeadTermsCondition
								= new DialogBecomeTeamLeadTermsCondition(SlideMenuZoom2u.this,R.style.Theme_Dialog);
						dialogBecomeTeamLeadTermsCondition.show();
					} else {
						Intent intent = new Intent(SlideMenuZoom2u.this, TeamMemberList_Activity.class);
						startActivity(intent);
					}
				}
				else if (selectedItem.compareTo("Community")==0){
					Intent intent = new Intent(SlideMenuZoom2u.this, CommunityListActivity.class);
					startActivity(intent);
						}
				else if (selectedItem.compareTo("Driver Resource Centre")==0){
					Intent intent = new Intent(SlideMenuZoom2u.this, DrcActivity.class);
					startActivity(intent);
						}
				
				Model_DeliveriesToChat.showExclamationForUnreadChat(countChatSlideView);

				if (fragment != null) {
					   ft.replace(R.id.activity_main_content_fragment, fragment);
					   ft.commit();
					   fragment = null;
					   ft = null;
				}
				mLayout.toggleMenu();
			}
		}

	//***************  Connect with firebase ************
	public static void connectWithFireBaseChat(){
		if (ChatViewBookingScreen.mFirebaseRef == null)
			ChatViewBookingScreen.mFirebaseRef = FirebaseDatabase.getInstance().getReferenceFromUrl(ChatViewBookingScreen.FIREBASE_URL);
		setCourierToOfflineFromChat();
	 }

	//***************  Set courier status to offline for chat ***********
	public static void setCourierToOfflineFromChat(){
		if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
			ChatViewBookingScreen.mFirebaseRef.child("/couriers/" + LoginZoomToU.courierID + "/status/online").onDisconnect().setValue(0);
	 }

	//***************  Set courier status to online for chat ***********
	 public static void setCourierToOnlineForChat() {
		 if(ChatViewBookingScreen.mFirebaseRef != null){
			 if(!LoginZoomToU.courierID.equals("")){
				 ChatViewBookingScreen.mFirebaseRef.child("/couriers/"+LoginZoomToU.courierID+"/status/online").setValue(1);
			 }
		 }
	}


	private void setHeaderAndTitleBackground(){
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
	 	if(MainActivity.isIsBackGroundGray()){
			window.setStatusBarColor(Color.parseColor("#374350"));
	 		slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#374350"));
		}
		else{
			window.setStatusBarColor(Color.parseColor("#00A6E2"));
			slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A6E2"));
		}
	}

	private void setHeaderAndTitleBackgroundForProfile()  {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
		try {
			switch (WebserviceHandler.jObjOfCurrentCourierLevel.getString("Level")) {
				case "Dynamo"://gray
					window.setStatusBarColor(Color.parseColor("#374350"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#374350"));
					break;
				case "Recruit"://blue
					window.setStatusBarColor(Color.parseColor("#00A7E2"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A7E2"));
					break;
				case "Warrior"://green
					window.setStatusBarColor(Color.parseColor("#7BCE20"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#7BCE20"));
					break;
				case "Elite"://red
					window.setStatusBarColor(Color.parseColor("#FF476A"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#FF476A"));
					break;
				case "Legend"://gold
					window.setStatusBarColor(Color.parseColor("#FFCB2A"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#FFCB2A"));
					break;
				default://blue
					window.setStatusBarColor(Color.parseColor("#00A7E2"));
					slideMenuTopBarLayout.setBackgroundColor(Color.parseColor("#00A7E2"));
					break;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	@Override
	 public void onBackPressed() {
		  if (mLayout.isMenuShown())
			  	mLayout.toggleMenu();
		  else {
			 // super.onBackPressed();
			 // logoutWindow();
			  this.moveTaskToBack(true);
		  }
	}

	//********** Minimize app on Back button press from slide menu **************
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (mLayout.isMenuShown()) {
				mLayout.toggleMenu();
				return false;
			} else {
				this.moveTaskToBack(true);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private ProgressDialog progressDialogForLogout;

	 private void LogoutCourier(){
		 final String[] webServiceResponseForLogout = {"0"};
		 new MyAsyncTasks(){
			 @Override
			 public void onPreExecute() {
				 try {
					 if(progressDialogForLogout == null)
						 progressDialogForLogout = new ProgressDialog(SlideMenuZoom2u.this);
					 Custom_ProgressDialogBar.inItProgressBar(progressDialogForLogout);
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
			 }

			 @Override
			 public void doInBackground() {
				 try {
					 WebserviceHandler webServiceHandler = new WebserviceHandler();
					 webServiceResponseForLogout[0] = webServiceHandler.courierLogout();
					 webServiceHandler.SetIsCourierOnline(false);
				 }catch (Exception e){
					 e.printStackTrace();
				 }
			 }

			 @Override
			 public void onPostExecute() {
				 try {
					 try{
						 if(progressDialogForLogout != null)
							 if(progressDialogForLogout.isShowing())
								 Custom_ProgressDialogBar.dismissProgressBar(progressDialogForLogout);
					 }catch(Exception e){
						 e.printStackTrace();
					 }
					 if (LoginZoomToU.isLoginSuccess == 1)
						 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "No network!", "No network connection, Please check your connection and try again");
					 else if (LoginZoomToU.isLoginSuccess == 2)
						 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "Error!", new JSONObject(webServiceResponseForLogout[0]).getString("error_description"));
					 else if (LoginZoomToU.isLoginSuccess == 3)
						 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "Server Error!", "Something went wrong, Please try later.");
					 else if (!webServiceResponseForLogout[0].equals("")) {
						 if (new JSONObject(webServiceResponseForLogout[0]).getBoolean("success")) {
							 //************ Re-store updated password to preference *************//
							 new Courier_AutoLoginFeature().storeCredentialAfterLogin(LoginZoomToU.prefrenceForLogin.getBoolean("islogin", false),
									 LoginZoomToU.prefrenceForLogin.getString("username", null), LoginZoomToU.prefrenceForLogin.getString("password", null), null, 0, false);
							 TeamMemberList_Activity.arrayOfMyTeamList = null;
							 LoginZoomToU.CARRIER_ID = 0;
							 LoginZoomToU.IS_TEAM_LEADER = false;

							 try{
								 ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
								 BookingView.bookingViewSelection = 0;
								 LoginZoomToU.editorForLogout.putBoolean("isLogout", true);
								 LoginZoomToU.editorForLogout.commit();
							 } catch (Exception e) {
								 e.printStackTrace();
							 }

							 if (DialogReasonForLateDelivery.reasonlist != null)
								 DialogReasonForLateDelivery.reasonlist = null;

							 if(CourierRouteDetail.courierRouteView != null)
								 CourierRouteDetail.courierRouteView = null;

							 if(ServiceForSendLatLong.locationTimerTask != null) {
								 ServiceForSendLatLong.locationTimerTask.cancel();
								 ServiceForSendLatLong.locationTimerTask = null;
							 }

							 FirebaseAuth.getInstance().signOut();
							 LoginZoomToU.mAuth_Firebase = null;
							 LoginZoomToU.firebase_CurrentUser = null;

							 Toast.makeText(SlideMenuZoom2u.this, "You logged out successfully!", Toast.LENGTH_LONG).show();

							 Intent i = new Intent(getApplicationContext(), LoginZoomToU.class);
							 startActivity(i);
							 i = null;
							 finish();
							 overridePendingTransition(R.anim.left_in, R.anim.right_out);
						 } else
							 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "Error!", new JSONObject(webServiceResponseForLogout[0]).getString("error_description"));
					 }
				 } catch (Exception e) {
					 e.printStackTrace();
					 DialogActivity.alertDialogView(SlideMenuZoom2u.this, "Sorry!", "Can't logged you out at this moment, Please try again later.");
				 }
			 }
		 }.execute();

	 }



	@Override
		public void onSaveInstanceState(Bundle outState) {
		 //	setCourierToOfflineFromChat();
			outState.putInt("GetSlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putString("CourierId", LoginZoomToU.courierID);
			outState.putString("CourierName", LoginZoomToU.courierName);
            outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
			outState.putBoolean("Routific", WebserviceHandler.isRoutific);
			outState.putString("imageFilePath", Functional_Utility.mCurrentPhotoPath);		// *** for Camera crash on samsung galaxy S4 25-Jan-2017
		//	outState.putParcelableArrayList("BookingArryList", BookingView.bookingListArray);
			super.onSaveInstanceState(outState);
		}

	 @Override
	protected void onDestroy() {
		super.onDestroy();
	 }

	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			 if(savedInstanceState != null){
				 restoreInstanceAfterKillOnBG(savedInstanceState);
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onRestoreInstanceState(savedInstanceState);
	 }

	 void restoreInstanceAfterKillOnBG(Bundle savedInstanceState){
		 try {
			 WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
			 Functional_Utility.mCurrentPhotoPath = savedInstanceState.getString("imageFilePath");   // *** for Camera crash on samsung galaxy S4 25-Jan-2017
			 LoginZoomToU.courierID = savedInstanceState.getString("CourierId");
			 LoginZoomToU.courierName = savedInstanceState.getString("CourierName");
             BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
			 ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("GetSlideMenuItem");
			 if (savedInstanceState.getParcelableArrayList("BookingArryList") != null)
				 BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("BookingArryList");
			 LoginZoomToU.staticFieldInit(SlideMenuZoom2u.this);
			 finish();
			 Intent serviceIntent = new Intent(SlideMenuZoom2u.this, SlideMenuZoom2u.class);
			 startService(serviceIntent);
			 serviceIntent = null;
             connectWithFireBaseChat();
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }

	 @Override
		public void onResume() {
			super.onResume();
			setCourierToOnlineForChat();
		 	countChatBookingView = countChatSlideView;
		 	Model_DeliveriesToChat.showExclamationForUnreadChat(countChatBookingView);
		 }

//	/************** Show exclamation icon to unread chat for courier
//	 Called in OnResume or Booking view selection  *************/
//	public static void showExclamatiomIconForUnreadChat(TextView exclamationTxt) {
//		int totalUnreadCount = 0;
//		if (exclamationTxt != null) {
//			try {
//				for(Model_DeliveriesToChat modelDeliveryChat : LoadChatBookingArray.arrayOfChatDelivery)
//                    totalUnreadCount = totalUnreadCount + modelDeliveryChat.getUnreadMsgCountOfCustomer();
//
//				if (totalUnreadCount > 0)
//                    exclamationTxt.setVisibility(View.VISIBLE);
//                else
//                    exclamationTxt.setVisibility(View.GONE);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//}

	public class NavDrawerListAdapter extends BaseAdapter {

			private Context context;
			private ArrayList<MenuSection_Interface> navDrawerItems;

		public NavDrawerListAdapter(Context context, ArrayList<MenuSection_Interface> navDrawerItems){
			this.context = context;
			this.navDrawerItems = navDrawerItems;
		}

		@Override
		public int getCount() {
			return navDrawerItems.size();
		}

		@Override
		public Object getItem(int position) {
			return navDrawerItems.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//	if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (navDrawerItems.get(position).isSection()) {
				convertView = mInflater.inflate(R.layout.menu_header_txt, null);
				TextView headerMenuIcon = (TextView) convertView.findViewById(R.id.headerMenuIcon);
				if (position != 0)
					headerMenuIcon.setPadding(ActiveBookingDetail_New.convertDpToPixelInAlert(SlideMenuZoom2u.this, 25),
							ActiveBookingDetail_New.convertDpToPixelInAlert(SlideMenuZoom2u.this, 30), 0,
							ActiveBookingDetail_New.convertDpToPixelInAlert(SlideMenuZoom2u.this, 10));
				else
					headerMenuIcon.setPadding(ActiveBookingDetail_New.convertDpToPixelInAlert(SlideMenuZoom2u.this, 25),
							0, 0, ActiveBookingDetail_New.convertDpToPixelInAlert(SlideMenuZoom2u.this, 10));
				headerMenuIcon.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
				headerMenuIcon.setText(((Menu_SectionModel) navDrawerItems.get(position)).getTitle());
			} else {

				if (((NavDrawerItem) navDrawerItems.get(position)).getTitle().equals("Community")) {
					convertView = mInflater.inflate(R.layout.itemcommunity, null);

					RelativeLayout rrArrowLeft= (RelativeLayout) convertView.findViewById(R.id.rrArrowLeft);
					RelativeLayout rrArrowUp= (RelativeLayout) convertView.findViewById(R.id.rrArrowUp);
					CardView commbooking =(CardView) convertView.findViewById(R.id.commbooking);
					rrArrowLeft.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							rrArrowUp.setVisibility(View.VISIBLE);
							rrArrowLeft.setVisibility(View.GONE);
							commbooking.setVisibility(View.VISIBLE);
						}
					});

					rrArrowUp.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							rrArrowUp.setVisibility(View.GONE);
							rrArrowLeft.setVisibility(View.VISIBLE);
							commbooking.setVisibility(View.GONE);
						}
					});

					commbooking.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							rrArrowUp.setVisibility(View.GONE);
							rrArrowLeft.setVisibility(View.VISIBLE);
							commbooking.setVisibility(View.GONE);
							startActivity(new Intent(SlideMenuZoom2u.this, CommunityBookingActivity.class));

							if (mLayout.isMenuShown())
								mLayout.toggleMenu();

						}
					});

				} else {

					convertView = mInflater.inflate(R.layout.drawer_list_item, null);
					ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
					TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
					txtTitle.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

					if (selectedItemOnSlideList == position)
						convertView.setBackgroundColor(Color.parseColor("#4b5561"));
					else
						convertView.setBackgroundColor(getResources().getColor(R.color.gunmetal_new));

					imgIcon.setImageResource(((NavDrawerItem) navDrawerItems.get(position)).getIcon());
					txtTitle.setText(((NavDrawerItem) navDrawerItems.get(position)).getTitle());

					TextView unreadMsgCount = (TextView) convertView.findViewById(R.id.unreadMsgCount);
					unreadMsgCount.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
					if (position == 3) {
						if (RequestView.COUNT_FOR_NOTBIDYET > 0) {
							unreadMsgCount.setVisibility(View.VISIBLE);
							unreadMsgCount.setText("" + RequestView.COUNT_FOR_NOTBIDYET);
						} else
							unreadMsgCount.setVisibility(View.GONE);
					} else
						unreadMsgCount.setVisibility(View.GONE);
				}
			}
			//    }

			return convertView;
		}
	}
}
