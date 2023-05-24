package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.zoom2u.HeroLevel_View;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.services.ServiceToGetCourierLevel;

public class Notification_Courier_Level extends Activity{

//	public static int notificationCourierLevelDialogCount = 0;
	RelativeLayout courierLevelNotifySubLayout, courierLevelImgLayout;
	TextView courierLevelNotifyMsgTxt, courierLevelNotifyOtherTxt;
	ImageView courierLevelNotifyStarImg1, courierLevelNotifyStarImg2, courierLevelNotifyStarImg3, courierLevelNotifyStarImg4,
		courierLevelNotifyStarImg5, courierLevelNotifyCancelDialog;
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
//		notificationCourierLevelDialogCount++;
		showNotificationDialog_CourierLevel();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_view_courier_level);
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(Notification_Courier_Level.this);
//		if(notificationCourierLevelDialogCount == 0){
		showNotificationDialog_CourierLevel();
//		}else{
//			finish();
//			Log.e("", "@@@@@@@@@@@*************");
//		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	//	notificationCourierLevelDialogCount = 0;
		PushReceiver.pushyCourierLevelTxt = "";
		PushReceiver.isPushyCourierLevelAchived = false;
		PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
		PushReceiver.loginEditorForPushy.putString("CourierLevelTxt", PushReceiver.pushyCourierLevelTxt);
		PushReceiver.loginEditorForPushy.putBoolean("CourierLevelAchived", PushReceiver.isPushyCourierLevelAchived);
		PushReceiver.loginEditorForPushy.commit();
		finish();
	}
	
	private void showNotificationDialog_CourierLevel() {
		try {
			Intent serviceIntent = new Intent(Notification_Courier_Level.this, ServiceToGetCourierLevel.class);
			startService(serviceIntent);
			serviceIntent = null;
			
	//		notificationCourierLevelDialogCount++;
			if(courierLevelNotifySubLayout == null)
				courierLevelNotifySubLayout = (RelativeLayout) findViewById(R.id.courierLevelNotifySubLayout);
			if(courierLevelImgLayout == null)	
				courierLevelImgLayout = (RelativeLayout) findViewById(R.id.courierLevelImgLayout);
				
			if(courierLevelNotifyMsgTxt == null)
				courierLevelNotifyMsgTxt = (TextView) findViewById(R.id.courierLevelNotifyTitleTxt);
				courierLevelNotifyMsgTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
			if(courierLevelNotifyOtherTxt == null)	
				courierLevelNotifyOtherTxt = (TextView) findViewById(R.id.courierLevelNotifyOtherTxt);
				courierLevelNotifyOtherTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
				courierLevelNotifyOtherTxt.setPaintFlags(courierLevelNotifyOtherTxt.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
			if(courierLevelNotifyStarImg1 == null)		
				courierLevelNotifyStarImg1 = (ImageView) findViewById(R.id.courierLevelNotifyStarImg1);
			if(courierLevelNotifyStarImg2 == null)
				courierLevelNotifyStarImg2 = (ImageView) findViewById(R.id.courierLevelNotifyStarImg2);
			if(courierLevelNotifyStarImg3 == null)
				courierLevelNotifyStarImg3 = (ImageView) findViewById(R.id.courierLevelNotifyStarImg3);
			if(courierLevelNotifyStarImg4 == null)
				courierLevelNotifyStarImg4 = (ImageView) findViewById(R.id.courierLevelNotifyStarImg4);
			if(courierLevelNotifyStarImg5 == null)
				courierLevelNotifyStarImg5 = (ImageView) findViewById(R.id.courierLevelNotifyStarImg5);
			
			//if(PushReceiver.prefrenceForPushy.getBoolean("CourierLevelAchived", PushReceiver.isPushyCourierLevelAchived) == true)
			courierLevelImgLayout.setVisibility(View.VISIBLE);
//			else
//				courierLevelImgLayout.setVisibility(View.GONE);
				
			setCourierLevelDetail();
			
			if(courierLevelNotifyCancelDialog == null)
				courierLevelNotifyCancelDialog = (ImageView) findViewById(R.id.courierLevelNotifyCancelDialog);
			courierLevelNotifyCancelDialog.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
		//			notificationCourierLevelDialogCount = 0;
					PushReceiver.pushyCourierLevelTxt = "";
					PushReceiver.isPushyCourierLevelAchived = false;
					PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
					PushReceiver.loginEditorForPushy.putString("CourierLevelTxt", PushReceiver.pushyCourierLevelTxt);
					PushReceiver.loginEditorForPushy.putBoolean("CourierLevelAchived", PushReceiver.isPushyCourierLevelAchived);
					PushReceiver.loginEditorForPushy.commit();
					finish();
				}
			});
			
			courierLevelNotifyOtherTxt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
		//			notificationCourierLevelDialogCount = 0;
					PushReceiver.pushyCourierLevelTxt = "";
					PushReceiver.isPushyCourierLevelAchived = false;
					PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
					PushReceiver.loginEditorForPushy.putString("CourierLevelTxt", PushReceiver.pushyCourierLevelTxt);
					PushReceiver.loginEditorForPushy.putBoolean("CourierLevelAchived", PushReceiver.isPushyCourierLevelAchived);
					PushReceiver.loginEditorForPushy.commit();
					if(HeroLevel_View.isHeroLevelViewVisible == false){
						finish();
						Intent heroLevelIntent = new Intent(Notification_Courier_Level.this, HeroLevel_View.class);
						startActivity(heroLevelIntent);
						heroLevelIntent = null;
						overridePendingTransition(R.anim.right_in, R.anim.left_out);
					}else
						finish();
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
			PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
			PushReceiver.loginEditorForPushy.commit();
		}
	}
	
	/************  Show notification  courier level star and message according to notification response *********/
	private void setCourierLevelDetail() {
		try {
			if(!PushReceiver.prefrenceForPushy.getString("CourierLevelTxt","").equals("")){
				String levelTxt = "";
				try{
					String[] levelTxtArray = PushReceiver.prefrenceForPushy.getString("CourierLevelTxt","").split(",");
					levelTxt = levelTxtArray[0];
				}catch (Exception e){
					e.printStackTrace();
					levelTxt = PushReceiver.prefrenceForPushy.getString("CourierLevelTxt","");
				}
					try {
						switch (levelTxt) {
						case "1":
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_recruite_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_star);
							break;
						case "2":
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_dynamo_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_star);
							break;
						case "3":
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_worrier_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_star);
							break;
						case "4":
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_elite_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_star);
							break;
						case "5":
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_legend_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_starselect);
							break;
						default:
							courierLevelNotifySubLayout.setBackgroundResource(R.drawable.rounded_recruite_level);
							courierLevelNotifyStarImg1.setImageResource(R.drawable.icon_starselect);
							courierLevelNotifyStarImg2.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg3.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg4.setImageResource(R.drawable.icon_star);
							courierLevelNotifyStarImg5.setImageResource(R.drawable.icon_star);
							break;
						}
						
						if(!PushReceiver.prefrenceForPushy.getString("NotificationMessage","").equals("")){
							try {
								String[] notifyMsgArray = PushReceiver.prefrenceForPushy.getString("NotificationMessage","").split("    ");
								courierLevelNotifyMsgTxt.setText(notifyMsgArray[0]);
								courierLevelNotifyOtherTxt.setText(notifyMsgArray[1]);
								notifyMsgArray = null;
							}catch (Exception e) {
								e.printStackTrace();
								courierLevelNotifyMsgTxt.setText(PushReceiver.prefrenceForPushy.getString("NotificationMessage",""));
								courierLevelNotifyOtherTxt.setText("Find out exactly what\nthat means for you");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
