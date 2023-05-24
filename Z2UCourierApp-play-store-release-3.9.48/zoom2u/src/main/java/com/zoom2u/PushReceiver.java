package com.zoom2u;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.newnotfication.view.NewBidRequest_NotificationView;
import com.newnotfication.view.NewBooking_Notification;
import com.newnotfication.view.Notification_BookingStatusRequest;
import com.suggestprice_team.courier_team.community.CommunityBookingActivity;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.dialogactivity.AchievementNotifyView;
import com.zoom2u.dialogactivity.DefaultWindowForChat;
import com.zoom2u.dialogactivity.DefaultWindowForToast;
import com.zoom2u.dialogactivity.DepotAlert_For_DHLDrivers;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogActivityForBooking;
import com.zoom2u.dialogactivity.DialogOutstandingBidNotification;
import com.zoom2u.dialogactivity.Notification_Courier_Level;
import com.zoom2u.offer_run_batch.SingleRunActivity;
import com.zoom2u.offer_run_batch.RunBatchActivity;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceToUpdateNewBookingList;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.webservice.WebserviceHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PushReceiver extends BroadcastReceiver {

	public static boolean isPushySilentNotification = false;
	public static SharedPreferences prefrenceForPushy;
	public static Editor loginEditorForPushy;
	public static String bookingID = "0";
	public static String runId = "0";
	public static String runBatchId = "0";
	public static String requestID = "0";
	public static String Bid_REQUEST_ID = "0";
	public static boolean isAppPresentInBGPushy = true;
	Intent notificationIntent;
	PendingIntent pendingIntent;
	public static Context contextOfPushReceiver;
	boolean isCourierLogin = false;

	public static String pushyCourierLevelTxt = "";

	public static boolean isPushyCourierLevelAchived = false;

	public static String THUMBS_UP_MSG = "";

	public static boolean isCameraOpen = false;

	private Intent intentForBidChat;

	public static  Boolean IsOtherScreenOpen =false;
	public static  Boolean IsSignatureScreenOpen =false;

    @Override
    public void onReceive(Context context, Intent intent){
    	if(contextOfPushReceiver != null)
    		contextOfPushReceiver = null;
    	contextOfPushReceiver = context;

		Log.e("Pushy", " Message ----- "+intent.getStringExtra("message"));

		if (intent.getStringExtra("message") != null){
			try{
				if(!IsOtherScreenOpen && !IsSignatureScreenOpen) {
					isPushySilentNotification = intent.getBooleanExtra("isSilentNotification", false);
					if (prefrenceForPushy == null)
						prefrenceForPushy = context.getSharedPreferences("bookingId", 0);
					if (loginEditorForPushy == null)
						loginEditorForPushy = prefrenceForPushy.edit();

					validateTimeStamp(context, intent);            // check time stamp for duplicate notification
				}
				else if(IsSignatureScreenOpen){
					saveNotificationItemToSystemPreference(intent);
					if (intent.getStringExtra("bookingId") != null)
						showNotificationForCameraOpen(NewBooking_Notification.class, null, context, intent.getStringExtra("message"), 0, 0);
					else if(intent.getStringExtra("runId") != null)
						showNotificationForCameraOpen(SingleRunActivity.class, null, context,  intent.getStringExtra("message"), 0, 8);
					else if(intent.getStringExtra("runBatchId") != null)
						showNotificationForCameraOpen(RunBatchActivity.class, null, context,  intent.getStringExtra("message"), 0, 9);
					else if (intent.getStringExtra("Request_Id") != null) {
						if (!intent.getStringExtra("Request_Id").equals("0") && !intent.getStringExtra("Request_Id").equals(""))
							showNotificationForCameraOpen(NewBidRequest_NotificationView.class, null, context, intent.getStringExtra("message"), 0, 1);
					}else if(intent.getStringExtra("chat") != null){
						if(isAppPresentInBGPushy == false) {
							if(notificationIntent != null)
								notificationIntent = null;
							notificationIntent = new Intent(context, MainActivity.class);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.setAction(Intent.ACTION_MAIN);
							notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.putExtra("NOTIFICATION_ID", 3);
							passingNoificationIntent(notificationIntent, context, intent.getStringExtra("message"), 3);
						} else {
							if(notificationIntent != null)
								notificationIntent = null;
							String titleStr = "Zoom2u";
							if (intent.getStringExtra("isBidRequest") != null && intent.getStringExtra("isBidRequest").equals("1"))
								titleStr = titleStr+" - Bid Request";
							notificationIntent = new Intent(context, DefaultWindowForChat.class);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.setAction(Intent.ACTION_MAIN);
							notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							pendingIntent =  PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

							Uri soundUri = Uri.parse("android.resource://com.zoom2u/"+ R.raw.chatnotification);
							//************** Notification change for Android O (8.0) target and lower ************
							notificationWithChannelFor_8_N_Lower(context, titleStr, intent.getStringExtra("message"), soundUri, 3);

							pendingIntent = null;
						}
					}
					else if (intent.getStringExtra("LateDeliveryId") != null) {

					}
					else if (intent.getStringExtra("DhlDriver") != null) {

					}
					else if (intent.getStringExtra("Outstanding_RequestId") != null) {

					}
					else if(intent.getStringExtra("level") != null){

					}
					else if(intent.getStringExtra("achievementId") != null){

					}
					else if(intent.getStringExtra("notifyAboutThumbsUp") != null){

					}
					else {
						PushReceiver.loginEditorForPushy.putString("NotificationMessage", intent.getStringExtra("message"));
						PushReceiver.loginEditorForPushy.commit();
						showNotificationForCameraOpen(DialogActivity.class, null, context, intent.getStringExtra("message"), 0, 0);
					}
				}else if(IsOtherScreenOpen){
					saveNotificationItemToSystemPreference(intent);
					if (intent.getStringExtra("bookingId") != null){
						isPushySilentNotification = intent.getBooleanExtra("isSilentNotification", false);
						if (prefrenceForPushy == null)
							prefrenceForPushy = context.getSharedPreferences("bookingId", 0);
						if (loginEditorForPushy == null)
							loginEditorForPushy = prefrenceForPushy.edit();
						validateTimeStamp(context, intent);            //
					}
					else if(intent.getStringExtra("runId") != null)
						showNotificationForCameraOpen(SingleRunActivity.class, null, context,  intent.getStringExtra("message"), 0, 8);
					else if(intent.getStringExtra("runBatchId") != null)
						showNotificationForCameraOpen(RunBatchActivity.class, null, context,  intent.getStringExtra("message"), 0, 9);
					else if (intent.getStringExtra("Request_Id") != null) {
						if (!intent.getStringExtra("Request_Id").equals("0") && !intent.getStringExtra("Request_Id").equals(""))
							showNotificationForCameraOpen(NewBidRequest_NotificationView.class, null, context, intent.getStringExtra("message"), 0, 1);
					}else if(intent.getStringExtra("chat") != null){
						if(isAppPresentInBGPushy == false) {
							if(notificationIntent != null)
								notificationIntent = null;
							notificationIntent = new Intent(context, MainActivity.class);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.setAction(Intent.ACTION_MAIN);
							notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.putExtra("NOTIFICATION_ID", 3);
							passingNoificationIntent(notificationIntent, context, intent.getStringExtra("message"), 3);
						} else {
							if(notificationIntent != null)
								notificationIntent = null;
							String titleStr = "Zoom2u";
							if (intent.getStringExtra("isBidRequest") != null && intent.getStringExtra("isBidRequest").equals("1"))
								titleStr = titleStr+" - Bid Request";
							notificationIntent = new Intent(context, DefaultWindowForChat.class);
							notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							notificationIntent.setAction(Intent.ACTION_MAIN);
							notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
							pendingIntent =  PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

							Uri soundUri = Uri.parse("android.resource://com.zoom2u/"+ R.raw.chatnotification);
							//************** Notification change for Android O (8.0) target and lower ************
							notificationWithChannelFor_8_N_Lower(context, titleStr, intent.getStringExtra("message"), soundUri, 3);

							pendingIntent = null;
						}
					}
					else if (intent.getStringExtra("LateDeliveryId") != null) {

					}
					else if (intent.getStringExtra("DhlDriver") != null) {

					}
					else if (intent.getStringExtra("Outstanding_RequestId") != null) {

					}
					else if(intent.getStringExtra("level") != null){

					}
					else if(intent.getStringExtra("achievementId") != null){

					}
					else if(intent.getStringExtra("notifyAboutThumbsUp") != null){

					}
					else {
						if (intent.hasExtra("Request_Rejected_Id"))
						{
							PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id",intent.getStringExtra("Request_Rejected_Id"));
						}

						PushReceiver.loginEditorForPushy.putString("NotificationMessage", intent.getStringExtra("message"));
						PushReceiver.loginEditorForPushy.commit();
						showNotificationForCameraOpen(DialogActivity.class, null, context, intent.getStringExtra("message"), 0, 0);
					}
				}
			}catch (Exception e) {
				//e.printStackTrace();
			}
		}
    }

    /************  Check for New and Active booking count  ************/
    void checkForNewANDActiveBookingCount(){
    	if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11){
    		if(BookingView.bookingViewSelection == 1 || BookingView.bookingViewSelection == 2){
                if(BookingView.bookingViewSelection == 1){
                    Intent intent1 = new Intent(contextOfPushReceiver, ServiceToUpdateNewBookingList.class);
                    contextOfPushReceiver.startService(intent1);
                    intent1 = null;
                }
				if(BookingView.bookingViewSelection == 2){
					Intent intent1 = new Intent(contextOfPushReceiver, ServiceToUpdate_ActiveBookingList.class);
					contextOfPushReceiver.startService(intent1);
					intent1 = null;
				}
    		}
    	}

    	WebserviceHandler.NEWBOOKING_COUNT++;
		Intent bookingCountService = new Intent(contextOfPushReceiver, ServiceForCourierBookingCount.class);
		bookingCountService.putExtra("Is_API_Call_Require", 4);
		contextOfPushReceiver.startService(bookingCountService);
		bookingCountService = null;

		new LoadChatBookingArray(contextOfPushReceiver, 0);
    }

    /**********  Check for food ready in for refresh active jobs ************/
//    void checkForFoodReadyIn(Context context, Intent intent){
//    	try{
//			if(intent.getStringExtra("foodReadyIn") != null){
//				if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 1){
//					try {
//						refreshNewBookingJobs(context);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}else
//					refreshActiveJobs(context);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//    }

	private void refreshNewBookingJobs(Context context) {
		Intent intent1 = new Intent(context, ServiceToUpdateNewBookingList.class);
		context.startService(intent1);
		intent1 = null;
	}

	/*****************  Validate time stamp for repeat notification **************/
    void validateTimeStamp(Context context, Intent intent){
    	try{
			 if(intent.getStringExtra("timestamp") != null){
				 Date newTimeStamp = new Date();
				 SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				 newTimeStamp = converter.parse(intent.getStringExtra("timestamp"));

				 if(!prefrenceForPushy.getString("timestamp", "0").equals("0")){
					 try {
						Date oldTimeStamp = new Date();
						oldTimeStamp = converter.parse(prefrenceForPushy.getString("timestamp", "0"));
						if(newTimeStamp.after(oldTimeStamp)){
							 saveNotificationItemToSystemPreference(intent);		// Save notification item to system preference
						     isAppPresentInBG(context);
						     generateNotification(context, intent.getStringExtra("message"), intent);
						} else if (prefrenceForPushy.getInt("CATCH_SKIPPED_NOTIFICATION", 0) != 1){
							//********** Capture very first skipped notification when reinstall app after delete *********
							PushReceiver.loginEditorForPushy.putInt("CATCH_SKIPPED_NOTIFICATION", 1);
							PushReceiver.loginEditorForPushy.commit();
							saveNotificationItemToSystemPreference(intent);		// Save notification item to system preference
							isAppPresentInBG(context);
							generateNotification(context, intent.getStringExtra("message"), intent);
						}

						 loginEditorForPushy.putString("timestamp", intent.getStringExtra("timestamp"));
						 loginEditorForPushy.commit();
						 newTimeStamp = null;
						 oldTimeStamp = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				 }else{
					 loginEditorForPushy.putString("timestamp", intent.getStringExtra("timestamp"));
					 loginEditorForPushy.commit();
				 }
				 converter = null;
			 }
		 }catch(Exception e){
			 e.printStackTrace();
		 }
    }

	/***********  Save notification item to preference  **************/
	void saveNotificationItemToSystemPreference(Intent intent){
		try {
			/**********  Set booking id in preference when new job offer has received *********/
			try {
				if(intent.getStringExtra("bookingId") != null){
					bookingID = intent.getStringExtra("bookingId");
					loginEditorForPushy.putString("bookingId", bookingID);
					checkForNewANDActiveBookingCount();
				}else
					bookingID = "0";
			}catch (Exception e){
				e.printStackTrace();
			}

			/**set run and run batch notification*/

			try {
				if(intent.getStringExtra("runId") != null){
					runId = intent.getStringExtra("runId");
					loginEditorForPushy.putString("runId", runId);
				}else
					runId = "0";
			}catch (Exception e){
				e.printStackTrace();
			}


			try {
				if(intent.getStringExtra("runBatchId") != null){
					runBatchId = intent.getStringExtra("runBatchId");
					loginEditorForPushy.putString("runBatchId", runBatchId);
				}else
					runBatchId = "0";
			}catch (Exception e){
				e.printStackTrace();
			}



			/**********  Set Request id into preference when new bid notification has received *********/
			try {
				if(intent.getStringExtra("Request_Id") != null){
					if (!intent.getStringExtra("Request_Id").equals("0") && !intent.getStringExtra("Request_Id").equals("")) {
						Bid_REQUEST_ID = intent.getStringExtra("Request_Id");
						loginEditorForPushy.putString("Bid_REQUEST_ID", Bid_REQUEST_ID);
					}
				}else
					Bid_REQUEST_ID = "0";
			}catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if(intent.getStringExtra("LateDeliveryId") != null){
					loginEditorForPushy.putString("LateDeliveryId", intent.getStringExtra("LateDeliveryId"));
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			try {
				if(intent.getStringExtra("Outstanding_RequestId") != null){
					loginEditorForPushy.putString("Outstanding_RequestId", intent.getStringExtra("Outstanding_RequestId"));
				}
			}catch (Exception e){
				e.printStackTrace();
			}

			/**********  Set requestId into preference for new request availability has received in notification *********/
			try {
				if(intent.getStringExtra("requestId") != null){
					requestID = intent.getStringExtra("requestId");
					loginEditorForPushy.putString("requestId", requestID);
				}else
					requestID = "0";
			}catch (Exception e) {
				e.printStackTrace();
			}

			/**********  Get courier level from Notification response *********/
			try {
				if(intent.getStringExtra("level") != null){
					pushyCourierLevelTxt = intent.getStringExtra("level");
					loginEditorForPushy.putString("CourierLevelTxt", pushyCourierLevelTxt);
				}else
					pushyCourierLevelTxt = "";
			}catch(Exception e){
				e.printStackTrace();
				pushyCourierLevelTxt = "";
			}

			/**********  Save content into preference to show courier level achievement dialog as notification *********/
			try {
				if(intent.getStringExtra("isAchieved") != null){
					isPushyCourierLevelAchived = true;
					loginEditorForPushy.putBoolean("CourierLevelAchived", isPushyCourierLevelAchived);
				}else
					isPushyCourierLevelAchived = false;
			}catch(Exception e) {
				isPushyCourierLevelAchived = false;
				e.printStackTrace();
			}

			/**********  Save content into preference to show courier weekly status as notification *********/
//			try {
//				if(intent.getStringExtra("WeeklyCourierStatus") != null)
//					loginEditorForPushy.putString("WeeklyCourierStatus", "WeeklyCourierStatus");
//			}catch(Exception e) {
//				e.printStackTrace();
//			}

			/**********  Refresh New and Active booking list when courier is in booking section page
			 * and allocate booking to courier *********/
			try {
				if(intent.getStringExtra("AdminAllocated") != null) {
					Intent bookingCountService = new Intent(contextOfPushReceiver, ServiceForCourierBookingCount.class);
					bookingCountService.putExtra("Is_API_Call_Require", 1);
					contextOfPushReceiver.startService(bookingCountService);
					bookingCountService = null;
				}
			}catch(Exception e) {
				e.printStackTrace();
			}

			//************* Alert to notify courier achievements ***********
			try {
				if(intent.getStringExtra("achievementId") != null)
					loginEditorForPushy.putString("achievementId", intent.getStringExtra("achievementId"));
			}catch (Exception e) {
				e.printStackTrace();
				loginEditorForPushy.putString("achievementId", "0");
			}
			//************* Alert to notify courier achievements ***********

			/*********** ******************** *********/
			loginEditorForPushy.commit();
		} catch (Exception e) {
			e.printStackTrace();
			loginEditorForPushy.commit();
		}
	}

    @SuppressLint("NewApi")
	private void generateNotification(Context context, String message, Intent intent){
		try {
			intentForBidChat = intent;
			try {
				// ******* Check for Is Courier login *********
				isCourierLogin = LoginZoomToU.prefrenceForLogin.getBoolean("islogin", false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(isPushySilentNotification == false){
				if (intent.getStringExtra("bookingId") != null)
					showNewBookingNotification(context, message, true);
				else if (intent.getStringExtra("Request_Id") != null) {
					if (!intent.getStringExtra("Request_Id").equals("0") && !intent.getStringExtra("Request_Id").equals(""))
						showNewBookingNotification(context, message, false);
					else
						showOtherNotifications(context, message, intent);
				} else
					showOtherNotifications(context, message, intent);
			}else{
				try {
					//********* Refresh active job list if app present in Foreground ***************
					if(isAppIsInBackground(context) == false)
						refreshActiveJobs(context);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//************ Show other notifications apart from Booking offer and New Bid request *********
	private void showOtherNotifications(Context context, String message, Intent intent) {
		if (intent.getStringExtra("LateDeliveryId") != null) {
			showLateDeliveryNotification(context, message);
		}else if(intent.getStringExtra("runId") != null){
			showRunNotification(context, intent, message, true);
		}
		else if(intent.getStringExtra("runBatchId") != null){
			showRunNotification(context, intent,message, false);
		}
		else if (intent.getStringExtra("DhlDriver") != null) {
			openNotification_InBG_Forground_OrInKillState(context, intent, message, DepotAlert_For_DHLDrivers.class, 6);
		} else if (intent.getStringExtra("Outstanding_RequestId") != null) {
			loginEditorForPushy.putString("NotificationMessage", message);
			loginEditorForPushy.commit();
			showOutStandingBidNotification(context, message);
		} else if(intent.getStringExtra("level") != null){
			loginEditorForPushy.putString("NotificationMessage", message);
			loginEditorForPushy.commit();
			showCourierLevelNotification(context, message);
		} else if(intent.getStringExtra("achievementId") != null)
			showAchievementAlertToCourier(context, message);
		else if(intent.getStringExtra("notifyAboutThumbsUp") != null)
			showToastOnThumbsUp(context, message);
		else {
			try {
				if(intent.getStringExtra("chat") != null){
					if(isAppPresentInBGPushy == false) {
						if(notificationIntent != null)
							notificationIntent = null;
						notificationIntent = new Intent(context, MainActivity.class);
						notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						notificationIntent.setAction(Intent.ACTION_MAIN);
						notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
						notificationIntent.putExtra("NOTIFICATION_ID", 3);
						passingNoificationIntent(notificationIntent, context, message, 3);
					} else {
						if(notificationIntent != null)
							notificationIntent = null;
						String titleStr = "Zoom2u";
						if (intent.getStringExtra("isBidRequest") != null && intent.getStringExtra("isBidRequest").equals("1"))
							titleStr = titleStr+" - Bid Request";
						notificationIntent = new Intent(context, DefaultWindowForChat.class);
						notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						notificationIntent.setAction(Intent.ACTION_MAIN);
						notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
						pendingIntent =  PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

						Uri soundUri = Uri.parse("android.resource://com.zoom2u/"+ R.raw.chatnotification);
						//************** Notification change for Android O (8.0) target and lower ************
						notificationWithChannelFor_8_N_Lower(context, titleStr, intent.getStringExtra("message"), soundUri, 3);

						pendingIntent = null;
					}
				}else{
					defaultAndRequestNotification(context, message, intent);
				}
			}catch(Exception e) {
				e.printStackTrace();
				defaultAndRequestNotification(context, message, intent);
			}
		}
	}


	private void openNotification_InBG_Forground_OrInKillState(Context context, Intent intent, String message, Class clsWhichToOpen, int notificationCount) {
		try {
			int runIDToShowAsNotificationCount = notificationCount;
			try {
				if (intent.getStringExtra("RunId") != null)
					runIDToShowAsNotificationCount = Integer.parseInt(intent.getStringExtra("RunId"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(isAppPresentInBGPushy == false) {
				String routeCode = "";
				try {
					if (intent.getStringExtra("RouteCode") != null)
						routeCode = intent.getStringExtra("RouteCode");
				} catch (Exception e) {
					e.printStackTrace();
				}

				loginEditorForPushy.putString("DepotReach_RouteCode_DHLDriver", routeCode);
				loginEditorForPushy.putString("DepotReach_Msg_for_DHLDriver", message);
				loginEditorForPushy.commit();

				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.putExtra("NOTIFICATION_ID", notificationCount);
				passingNoificationIntent(notificationIntent, context, message, notificationCount);
			}else if(isCameraOpen == true){
				showNotificationForCameraOpen(clsWhichToOpen, intent, context, message, runIDToShowAsNotificationCount, notificationCount);
			}else{
				if(isCourierLogin == true) {
					if(isAppIsInBackground(context)) {
						showNotificationForCameraOpen(clsWhichToOpen, intent, context, message, runIDToShowAsNotificationCount, notificationCount);
					} else {
						Intent i = new Intent("android.intent.action.MAIN");
						i.putExtra("NotificationMessageStr", message);
						try {
							if (intent.getStringExtra("RouteCode") != null)
                                i.putExtra("RouteCode", intent.getStringExtra("RouteCode"));
                            else
                                i.putExtra("RouteCode", "");
						} catch (Exception e) {
							e.printStackTrace();
							i.putExtra("RouteCode", "");
						}
						i.setClass(context, clsWhichToOpen);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(i);
						i = null;
						popupDefaultNotification(context, message, notificationCount);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	//*************** Show outstanding bid notification in every 5 min *************
	private void showOutStandingBidNotification(Context context, String message) {
		try {
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.putExtra("NOTIFICATION_ID", 5);
				passingNoificationIntent(notificationIntent, context, message, 5);
			}else if(isCameraOpen == true){
				showNotificationForCameraOpen(DialogOutstandingBidNotification.class, null, context, message, 0, 5);
			}else{
				if(isCourierLogin == true) {
					if(isAppIsInBackground(context)) {
						showNotificationForCameraOpen(DialogOutstandingBidNotification.class, null, context, message, 0, 5);
					} else {
						Intent i = new Intent("android.intent.action.MAIN");
						i.setClass(context, DialogOutstandingBidNotification.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(i);
						i = null;
						popupDefaultNotification(context, message, 5);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/***************** Refresh active jobs in case of silent notification Or Food ready ETA  **********/
    void refreshActiveJobs(Context context){
    	try{
    		if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 2){
				try {
					Intent intent1 = new Intent(context, ServiceToUpdate_ActiveBookingList.class);
					context.startService(intent1);
					intent1 = null;
				} catch (Exception e){
					e.printStackTrace();
				}
			}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    /****************  Default and Request Id Notification ******************/
    void defaultAndRequestNotification(Context context, String message, Intent intent){
    	try {
			if (isAppPresentInBGPushy == false) {
				if (notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.putExtra("NOTIFICATION_ID", 2);
				switchToPerticularActivity(notificationIntent, context, message);
				if(intent.getStringExtra("requestId") == null) {
					if (intent.hasExtra("Request_Rejected_Id"))
					{
						PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id",intent.getStringExtra("Request_Rejected_Id"));
					}

					PushReceiver.loginEditorForPushy.putString("NotificationMessage", message);
					PushReceiver.loginEditorForPushy.commit();
				}
			} else if(isCameraOpen == true){
				if(intent.getStringExtra("requestId") != null) {
//					if (!requestID.equals("0") && !requestID.equals(""))
//						showNotificationForCameraOpen(Dialog_RequestAvailability.class, context, message, 2);
				} else {
					if (notificationIntent != null)
						notificationIntent = null;
					notificationIntent = new Intent(context, DialogActivity.class);
					switchToPerticularActivity(notificationIntent, context, message);
					PushReceiver.loginEditorForPushy.putString("NotificationMessage", message);
					PushReceiver.loginEditorForPushy.commit();
				}
			} else{
				if(intent.getStringExtra("requestId") != null) {
//					if (!requestID.equals("0") && !requestID.equals(""))
//						switchToClass(Dialog_RequestAvailability.class, context, message);
				} else {
					if (intent.hasExtra("Request_Rejected_Id"))
					{
						PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id",intent.getStringExtra("Request_Rejected_Id"));
					}

					PushReceiver.loginEditorForPushy.putString("NotificationMessage", message);
					PushReceiver.loginEditorForPushy.commit();
					switchToClass(DialogActivity.class, contextOfPushReceiver, message);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /***********  Set Notification class when camera is appear  **********/
    @SuppressWarnings("rawtypes")
	void showNotificationForCameraOpen(Class cls, Intent intent, Context context, String message, int runID, int notificationCount){
    	if(notificationIntent != null)
			notificationIntent = null;
		notificationIntent = new Intent(context, cls);
		//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (notificationCount == 6) {
			notificationIntent.putExtra("NotificationMessageStr", message);
			try {
				if (intent != null)
                    notificationIntent.putExtra("RouteCode", intent.getStringExtra("RouteCode"));
                else
                    notificationIntent.putExtra("RouteCode", "");
			} catch (Exception e) {
				e.printStackTrace();
				notificationIntent.putExtra("RouteCode", "");
			}
			passingNoificationIntent(notificationIntent, context, message, runID);
		} else
			passingNoificationIntent(notificationIntent, context, message, notificationCount);
    }

    /******* Show Courier level alert view **************/
    void showCourierLevelNotification(Context context, String message){
    	try {
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.putExtra("NOTIFICATION_ID", 2);
				switchToPerticularActivity(notificationIntent, context, message);
			}else if(isCameraOpen == true){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, Notification_Courier_Level.class);
				switchToPerticularActivity(notificationIntent, context, message);
			}else
				switchToClass(Notification_Courier_Level.class, context, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	//*********** Courier Late Delivery notification  *******************//
	private void showLateDeliveryNotification(Context context, String message) {
		try {
			if (isAppPresentInBGPushy == false) {
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.putExtra("NOTIFICATION_ID", 7);
				passingNoificationIntent(notificationIntent, context, message, 6);
			} else if(isCameraOpen == true) {
				showNotificationForCameraOpen(Notification_BookingStatusRequest.class, null, context, message, 0, 7);
			} else {
				if(isCourierLogin == true) {
					if(isAppIsInBackground(context)) {
						showNotificationForCameraOpen(Notification_BookingStatusRequest.class, null, context, message, 0, 7);
					} else {
						Intent i = new Intent("android.intent.action.MAIN");
						i.setClass(context, Notification_BookingStatusRequest.class);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(i);
						i = null;
						popupDefaultNotification(context, message, 7);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//*********** Courier achievement alert for courier *******************//
	private void showAchievementAlertToCourier(Context context, String message) {
		try {
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.putExtra("NOTIFICATION_ID", 2);
				switchToPerticularActivity(notificationIntent, context, message);
			}else if(isCameraOpen == true){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, AchievementNotifyView.class);
				switchToPerticularActivity(notificationIntent, context, message);
			}else
				switchToClass(AchievementNotifyView.class, context, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//*************** Pass intent to switch to particular activity **********//
	private void switchToPerticularActivity (Intent notificationIntent, Context context, String messageStr){
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		notificationIntent.setAction(Intent.ACTION_MAIN);
		notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
		passingNoificationIntent(notificationIntent, context, messageStr, 2);
	}

    /******* Show Courier weekly status alert view **************/
//    void showCourierWeeklyStatusNotification(Context context, String message){
//    	try {
//			if(isAppPresentInBGPushy == false){
//				if(notificationIntent != null)
//					notificationIntent = null;
//				notificationIntent = new Intent(context, MainActivity.class);
//				notificationIntent.putExtra("NOTIFICATION_ID", 2);
//				switchToPerticularActivity(notificationIntent, context, message);
//			}else{
//				CallWeeklyCourierStats callWeeklyCourierStats = new CallWeeklyCourierStats(context);
//				callWeeklyCourierStats.weeklyCourierStatusNotify();
//				callWeeklyCourierStats = null;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    }

/******* Show Toast when getting point **************/
    void showToastOnThumbsUp(Context context, String message){
    	try {
			THUMBS_UP_MSG = message;
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.putExtra("thumbsUpMsg", message);
				notificationIntent.putExtra("NOTIFICATION_ID", 4);
				switchToPerticularActivity(notificationIntent, context, message);
				//Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			} else {
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, DefaultWindowForToast.class);
				notificationIntent.putExtra("thumbsUpMsg", message);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				passingNoificationIntent(notificationIntent, context, message, 4);
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


	/******* Show New booking notification view when booking id exist in notification response  *******/
	@SuppressLint("NewApi")
	void showRunNotification(Context context, Intent intent, String message, boolean isRunNotication) {

		try {
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isRunNotication) {
					notificationIntent.putExtra("NOTIFICATION_ID", 8);
					passingNoificationIntent(notificationIntent, context, message, 8);
				} else {
					notificationIntent.putExtra("NOTIFICATION_ID", 9);
					passingNoificationIntent(notificationIntent, context, message, 9);
				}
			}else if(isCameraOpen == true){
				if (isRunNotication)
					showNotificationForCameraOpen(SingleRunActivity.class, null, context, message, 0, 8);
				else {
					showNotificationForCameraOpen(RunBatchActivity.class, null, context, message, 0, 9);
				}
			}else{
				if(isCourierLogin == true) {
					if(isAppIsInBackground(context)) {
						if (isRunNotication)
							showNotificationForCameraOpen(SingleRunActivity.class, null, context, message, 0, 8);
						else {
							showNotificationForCameraOpen(RunBatchActivity.class, null, context, message, 0, 9);
						}
					}
					else {
						if (isRunNotication) {
							Intent i = new Intent("android.intent.action.MAIN");
							i.setClass(context, SingleRunActivity.class);
							i.putExtra("runId",intent.getStringExtra("runId"));
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
							i = null;
							popupDefaultNotification(context, message, 8);
						} else {
							Intent i = new Intent("android.intent.action.MAIN");
							i.setClass(context, RunBatchActivity.class);
							i.putExtra("runBatchId",intent.getStringExtra("runBatchId"));
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
							i = null;
							popupDefaultNotification(context, message, 9);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/******* Show New booking notification view when booking id exist in notification response  *******/
    @SuppressLint("NewApi")
	void showNewBookingNotification(Context context, String message, boolean isNewBookingNotication) {

    	//*********** Stack trace of courier - Dean taylor issue **********
//    	if (!isNewBookingNotication) {
//			ChatApplication.getInstance().writeLogs();
//    		Log.e("BidNotification", "Received Bid notification **************** ");
//		}
		//*********** Stack trace of courier - Dean taylor issue **********

		try {
			if(isAppPresentInBGPushy == false){
				if(notificationIntent != null)
					notificationIntent = null;
				notificationIntent = new Intent(context, MainActivity.class);
				//notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				notificationIntent.setAction(Intent.ACTION_MAIN);
				notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				if (isNewBookingNotication) {
					notificationIntent.putExtra("NOTIFICATION_ID", 0);
					passingNoificationIntent(notificationIntent, context, message, 0);
				} else {
					notificationIntent.putExtra("NOTIFICATION_ID", 1);
					passingNoificationIntent(notificationIntent, context, message, 1);
				}
			}else if(isCameraOpen == true){
				if (isNewBookingNotication) {
					if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 1)
						refreshActiveJobs(context);
					showNotificationForCameraOpen(NewBooking_Notification.class, null, context, message, 0, 0);
				} else {
					RequestView.COUNT_FOR_NOTBIDYET++;
			//		SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
					showNotificationForCameraOpen(NewBidRequest_NotificationView.class, null, context, message, 0, 1);
				}
			}else{
				if(isCourierLogin == true) {
					if(isAppIsInBackground(context)) {
						if (isNewBookingNotication)
							showNotificationForCameraOpen(NewBooking_Notification.class, null, context, message, 0, 0);
						else {
							RequestView.COUNT_FOR_NOTBIDYET++;
			//				SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
							showNotificationForCameraOpen(NewBidRequest_NotificationView.class, null, context, message, 0, 1);
						}
					} else {
						if (isNewBookingNotication) {
							Boolean isCommunityNotification=communityNotification(message);
							Intent i = new Intent("android.intent.action.MAIN");
							if(isCommunityNotification) {
								i.putExtra("NotificationMessageStr",message);
								i.setClass(context, DialogActivityForBooking.class);
							}else {
								i.setClass(context, NewBooking_Notification.class);
							}
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
							i = null;
							popupDefaultNotification(context, message, 0);
						} else {
							RequestView.COUNT_FOR_NOTBIDYET++;
			//				SlideMenuZoom2u.refreshHomeSlideMenuAdapter();
							Intent i = new Intent("android.intent.action.MAIN");
							i.setClass(context, NewBidRequest_NotificationView.class);
							i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(i);
							i = null;
							popupDefaultNotification(context, message, 1);
						}
					}
			    }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	private Boolean communityNotification(String message) {
		try {
			String myMessage="You have a new booking offer from your community member";
			String messageRange =message.substring(0, 55);
            if (myMessage.equals(messageRange))
				return true;

		}catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}


	/**********  Switch to respective view when tap on status bar notification *********/
    @SuppressLint("NewApi")
	@SuppressWarnings("rawtypes")
	void switchToClass(Class cls, Context context, String message){
			try {
				Intent i = new Intent("android.intent.action.MAIN");
				i.setClass(context, cls);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(i);
				i = null;
				popupDefaultNotification(context, message, 2);
			} catch (Exception e) {
				e.printStackTrace();
			}
    }

	/**********  Check is app killed *********/
	@SuppressWarnings("deprecation")
	public static boolean isAppPresentInBG(Context context) {
		try {
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			 // get the info from the currently running task
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			{
				if (am.getAppTasks().size()==0)
					isAppPresentInBGPushy=false;
				else isAppPresentInBGPushy=true;

			}else{
				List < ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
			for(int i = 0; i < taskInfo.size(); i++){
				if(taskInfo.get(i).baseActivity.getPackageName().equals(("com.zoom2u"))){
					isAppPresentInBGPushy = true;
					break;
				}else
					isAppPresentInBGPushy = false;
			}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return isAppPresentInBGPushy;
	}

	//************** Check app is running in background or not *************
	private boolean isAppIsInBackground(Context context) {
		boolean isInBackground = true;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
			List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
			for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
				if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
					for (String activeProcess : processInfo.pkgList) {
						if (activeProcess.equals(context.getPackageName())) {
							isInBackground = false;
						}
					}
				}
			}
		} else {
			List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
			ComponentName componentInfo = taskInfo.get(0).topActivity;
			if (componentInfo.getPackageName().equals(context.getPackageName())) {
				isInBackground = false;
			}
		}

		return isInBackground;
	}

	/**********  Pop-up notification in status bar  *********/
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	void passingNoificationIntent(Intent newIntent, Context context, String message, int isChatNotification){
		try {
			String title = "Zoom2u";
			if(intentForBidChat.getStringExtra("chat") != null) {
				loginEditorForPushy.putString("isChatForBidRequest", "1");
				loginEditorForPushy.commit();
				if (intentForBidChat.getStringExtra("isBidRequest") != null) {
					if(intentForBidChat.getStringExtra("isBidRequest").equals("1"))
							title = title + " - Bid Request";
				}
			}

			if(pendingIntent != null)
				pendingIntent = null;
			int requestIDForPendingIntent = (int) System.currentTimeMillis();
			pendingIntent = PendingIntent.getActivity(context, requestIDForPendingIntent, newIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

			//************** Notification change for Android O (8.0) target and lower ************
			Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			notificationWithChannelFor_8_N_Lower(context, title, message, soundUri, isChatNotification);

			pendingIntent = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//************* Default notification ********
	private void popupDefaultNotification (final Context context, String message, final int notificationCount){
		final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		if(pendingIntent != null)
			pendingIntent = null;
		Intent newIntent = new Intent();
		pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

		//************** Notification change for Android O (8.0) target and lower ************
		Uri soundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		notificationWithChannelFor_8_N_Lower(context, "Zoom2u", message, soundUri, notificationCount);

		try {
			Handler handlerPostDelay = new Handler(Looper.getMainLooper());
			handlerPostDelay.postDelayed(() -> {
				try {
					notificationManager.cancel(notificationCount);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, 2000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//***************************** Notification test work *************************


	//************** Notification change for Android O (8.0) target and lower ************
	private void notificationWithChannelFor_8_N_Lower(Context context, String titleStr, String message, Uri soundURI, int notificationId) {

		NotificationManager mNotificationManager =
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		NotificationCompat.Builder builder;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			String id = "Z2U Channel";
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(id, context.getString(R.string.app_name), importance);
			mChannel.enableVibration(true);
			mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
			mNotificationManager.createNotificationChannel(mChannel);

			builder = new NotificationCompat.Builder(context, id)
					.setContentTitle(titleStr)                            // required
					.setSmallIcon(Model_DeliveriesToChat.getNotificationIcon())   // required
					.setContentText(message) // required
					.setDefaults(Notification.DEFAULT_ALL)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setSound(soundURI)
					.setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
					.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
		} else {
			builder = new NotificationCompat.Builder(context)
					.setSound(soundURI)
					.setContentTitle(titleStr)
					.setContentText(message)
					.setContentIntent(pendingIntent)
					.setSmallIcon(Model_DeliveriesToChat.getNotificationIcon())
					.setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
					.setAutoCancel(true)
					.setOngoing(true)
					.setChannelId("Z2U Channel")
					.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

			if (android.os.Build.VERSION.SDK_INT >= 21) {
				builder.setColor(context.getResources().getColor(R.color.colorAccent))
						.setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
			}
		}

		Notification notification = builder.build();
		mNotificationManager.notify(notificationId, notification);

		mNotificationManager = null;
		notification = null;
	}
}