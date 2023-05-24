package com.zoom2u;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

import java.util.Timer;
import java.util.TimerTask;

public class HeroLevel_View extends Activity{
	
	TextView titleTxtHeroLevel, headerTxtHeroLevel, pointTxtHeroLevel;
	ImageView cancelBtnHeroLevel;
	LinearLayout scrollViewMainLayout;
	int yourLevel = 5;
	String courierLevelTxt = "";
	ProgressDialog progressForHeroLevel;
	public static boolean isHeroLevelViewVisible = false;
	boolean isViewCreated = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.herolevel_view);
	
		isHeroLevelViewVisible = true;
		isViewCreated = true;
		
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(HeroLevel_View.this);
		
		inItHeroLevelView();
	}
	
	/******** Initialize Hero level view **********/
	void inItHeroLevelView(){
		try{
			scrollViewMainLayout = (LinearLayout) findViewById(R.id.scrollViewSecondMainLayout);
			titleTxtHeroLevel = (TextView) findViewById(R.id.titleTxtHeroLevel);

			headerTxtHeroLevel = (TextView) findViewById(R.id.headerTxtHeroLevel);

			
			cancelBtnHeroLevel = (ImageView) findViewById(R.id.cancelBtnHeroLevel);
			cancelBtnHeroLevel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					finish();
					isHeroLevelViewVisible = false;
				}
			});
			
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
				/*GetHeroLevelAsyncTask getHeroLevelAsyncTask = new GetHeroLevelAsyncTask();
				getHeroLevelAsyncTask.execute();
				getHeroLevelAsyncTask = null;*/
				GetHeroLevelAsyncTask();
			}else{
				DialogActivity.alertDialogView(HeroLevel_View.this, "No Network!", "No network connection, Please try again later");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		if(isViewCreated == false){
			if(scrollViewMainLayout != null)
				scrollViewMainLayout.removeAllViews();
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
				/*GetHeroLevelAsyncTask getHeroLevelAsyncTask = new GetHeroLevelAsyncTask();
				getHeroLevelAsyncTask.execute();
				getHeroLevelAsyncTask = null;*/
				GetHeroLevelAsyncTask();
			}else
				DialogActivity.alertDialogView(HeroLevel_View.this, "No Network!", "No network connection, Please try again later");
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	//	SlideMenuZoom2u.setCourierToOfflineFromChat();
	}

	void showHeroLevelDetailViews(){
		LinearLayout eachStarLayout[] = new LinearLayout[5];
		LinearLayout eachStarLayoutForTxt[] = new LinearLayout[5];
		LinearLayout eachLayoutForPointTxt[] = new LinearLayout[5];
		for(int i = 0; i < 5; i++){
			eachStarLayout[i] = new LinearLayout(HeroLevel_View.this);
			LinearLayout.LayoutParams paramForEachStarLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			if(LoginZoomToU.width <= 320){
				paramForEachStarLayout.leftMargin = 15;
				paramForEachStarLayout.rightMargin = 15;
				paramForEachStarLayout.topMargin = 15;
			}else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540){
				paramForEachStarLayout.leftMargin = 22;
				paramForEachStarLayout.rightMargin = 22;
				paramForEachStarLayout.topMargin = 22;
			}else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720){
				paramForEachStarLayout.leftMargin = 30;
				paramForEachStarLayout.rightMargin = 30;
				paramForEachStarLayout.topMargin = 30;
			}else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080){
				paramForEachStarLayout.leftMargin = 45;
				paramForEachStarLayout.rightMargin = 45;
				paramForEachStarLayout.topMargin = 45;
			}else{
				paramForEachStarLayout.leftMargin = 60;
				paramForEachStarLayout.rightMargin = 60;
				paramForEachStarLayout.topMargin = 60;
			}
			eachStarLayout[i].setBackgroundResource(R.drawable.bottom_border);
			eachStarLayout[i].setLayoutParams(paramForEachStarLayout);
			eachStarLayout[i].setOrientation(LinearLayout.VERTICAL);
			setStarsForHeroLevels(i+1, eachStarLayout[i]);

			eachStarLayoutForTxt[i] = new LinearLayout(HeroLevel_View.this);
			LinearLayout.LayoutParams paramForEachStarLayoutForTxt = null;
			paramForEachStarLayoutForTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			eachStarLayoutForTxt[i].setBackgroundColor(Color.WHITE);
			paramForEachStarLayoutForTxt.bottomMargin = 2;
			eachStarLayoutForTxt[i].setPadding(0, 0, 0, 5);
			eachStarLayoutForTxt[i].setLayoutParams(paramForEachStarLayoutForTxt);
			eachStarLayoutForTxt[i].setOrientation(LinearLayout.VERTICAL);

			eachLayoutForPointTxt[i] = new LinearLayout(HeroLevel_View.this);
			LinearLayout.LayoutParams paramForEachLayoutForPointTxt = null;
			paramForEachLayoutForPointTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			eachLayoutForPointTxt[i].setBackgroundColor(Color.WHITE);
			paramForEachLayoutForPointTxt.bottomMargin = 2;
			eachLayoutForPointTxt[i].setPadding(0, 0, 0, 5);
			eachLayoutForPointTxt[i].setLayoutParams(paramForEachLayoutForPointTxt);
			eachLayoutForPointTxt[i].setOrientation(LinearLayout.HORIZONTAL);

			TextView pointTxt = new TextView(HeroLevel_View.this);
			LinearLayout.LayoutParams paramForPointTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
			pointTxt.setGravity(Gravity.CENTER);
			paramForPointTxt.leftMargin = 10;
			paramForPointTxt.topMargin = 10;
			pointTxt.setLayoutParams(paramForPointTxt);
			pointTxt.setTextColor(Color.parseColor("#374350"));

			pointTxt.setTextSize(14f);
			try {
				if(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels") != null){
					if(i == 0)
						pointTxt.setText(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels").getString("Recruit")+" points");
					else if(i == 1)
						pointTxt.setText(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels").getString("Dynamo")+" points");
					else if(i == 2)
						pointTxt.setText(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels").getString("Warrior")+" points");
					else if(i == 3)
						pointTxt.setText(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels").getString("Elite")+" points");
					else if(i == 4)
						pointTxt.setText(WebserviceHandler.jObjOfCurrentCourierLevel.getJSONObject("CourierLevels").getString("Legend")+" points");
				}else{
					if(i == 0)
						pointTxt.setText("0 - 250 points");
					else if(i == 1)
						pointTxt.setText("250 - 500 points");
					else if(i == 2)
						pointTxt.setText("500 - 1000 points");
					else if(i == 3)
						pointTxt.setText("1000 - 2000 points");
					else if(i == 4)
						pointTxt.setText("2000 points");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			eachLayoutForPointTxt[i].addView(pointTxt);
			eachStarLayoutForTxt[i].addView(eachLayoutForPointTxt[i]);

			if(i == yourLevel){
				LinearLayout yourLevelViewMain = new LinearLayout(HeroLevel_View.this);
				LinearLayout.LayoutParams yourLevelViewParamMain = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				yourLevelViewMain.setBackgroundColor(Color.TRANSPARENT);
				yourLevelViewMain.setGravity(Gravity.RIGHT);
				yourLevelViewMain.setLayoutParams(yourLevelViewParamMain);
				LinearLayout yourLevelView = new LinearLayout(HeroLevel_View.this);
				LinearLayout.LayoutParams yourLevelViewParam = null;
				LinearLayout.LayoutParams paramYourLevelImg = null;
				if(LoginZoomToU.width <= 320){
					paramYourLevelImg = new LinearLayout.LayoutParams(20, 20);
					yourLevelViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				//	yourLevelViewParam.leftMargin = 70;
					yourLevelViewParam.topMargin = 10;
				}else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540){
					paramYourLevelImg = new LinearLayout.LayoutParams(30, 30);
					yourLevelViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				//	yourLevelViewParam.leftMargin = 120;
					yourLevelViewParam.topMargin = 15;
				}else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720){
					paramYourLevelImg = new LinearLayout.LayoutParams(40, 40);
					yourLevelViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				//	yourLevelViewParam.leftMargin = 210;
					yourLevelViewParam.topMargin = 20;
				}else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080){
					paramYourLevelImg = new LinearLayout.LayoutParams(60, 60);
					yourLevelViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				//	yourLevelViewParam.leftMargin = 280;
					yourLevelViewParam.topMargin = 25;
				}else{
					paramYourLevelImg = new LinearLayout.LayoutParams(80, 80);
					yourLevelViewParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				//	yourLevelViewParam.leftMargin = 350;
					yourLevelViewParam.topMargin = 30;
				}

				yourLevelView.setPadding(5, 3, 5, 3);
				yourLevelView.setBackgroundResource(R.drawable.rounded_dynamo_level);
				yourLevelView.setGravity(Gravity.CENTER | Gravity.RIGHT);
				yourLevelView.setLayoutParams(yourLevelViewParam);
				yourLevelView.setOrientation(LinearLayout.HORIZONTAL);

				ImageView yourLevelImg = new ImageView(HeroLevel_View.this);
				paramYourLevelImg.gravity = Gravity.CENTER | Gravity.RIGHT;
				yourLevelImg.setLayoutParams(paramYourLevelImg);
				yourLevelImg.setImageResource(R.drawable.icon_favorite);
				yourLevelView.addView(yourLevelImg);

				TextView yourLevelTxt = new TextView(HeroLevel_View.this);
				LinearLayout.LayoutParams paramyourLevelTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				yourLevelTxt.setGravity(Gravity.CENTER | Gravity.RIGHT);
				paramyourLevelTxt.leftMargin = 5;
				yourLevelTxt.setLayoutParams(paramyourLevelTxt);
				yourLevelTxt.setTextColor(Color.WHITE);

				yourLevelTxt.setTextSize(11f);
				yourLevelTxt.setText("Your level");
				yourLevelView.addView(yourLevelTxt);
				yourLevelViewMain.addView(yourLevelView);
				eachLayoutForPointTxt[i].addView(yourLevelViewMain);
			}
//
//			TextView rewardTxt = new TextView(HeroLevel_View.this);
//			LinearLayout.LayoutParams paramForRewardTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//			rewardTxt.setGravity(Gravity.CENTER | Gravity.LEFT);
//			paramForRewardTxt.leftMargin = 10;
//			paramForRewardTxt.topMargin = 10;
//			rewardTxt.setLayoutParams(paramForRewardTxt);
//			rewardTxt.setTextColor(Color.parseColor("#374350"));
//			rewardTxt.setText("Rewards");
//
//			rewardTxt.setTextSize(12f);
//			eachStarLayoutForTxt[i].addView(rewardTxt);
//
//			TextView rewardMsgTxt = new TextView(HeroLevel_View.this);
//			LinearLayout.LayoutParams paramForRewardMsgTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//			rewardMsgTxt.setGravity(Gravity.CENTER | Gravity.LEFT);
//			paramForRewardMsgTxt.leftMargin = 10;
//			paramForRewardMsgTxt.topMargin = 5;
//			rewardMsgTxt.setLayoutParams(paramForRewardMsgTxt);
//			rewardMsgTxt.setTextColor(Color.parseColor("#374350"));
//			rewardMsgTxt.setLineSpacing(1.0f, 1.0f);
//
//			rewardMsgTxt.setTextSize(12f);
//			rewardMsgTxt.setText("-  Lorem ipsum dolor sit amet\n-  Laritas est etiam processus dynamicus\n-  Clari, fiant sollemnes infutrum");
//			eachStarLayoutForTxt[i].addView(rewardMsgTxt);

			eachStarLayout[i].addView(eachStarLayoutForTxt[i]);
			scrollViewMainLayout.addView(eachStarLayout[i]);
		}
	}

	void setStarsForHeroLevels(int level, LinearLayout eachStarLayout){
		LinearLayout setStarsForHeroLevels = new LinearLayout(HeroLevel_View.this);
		LinearLayout.LayoutParams paramForHeroLevels = null;
		if(LoginZoomToU.width <= 320){
			paramForHeroLevels = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 35);
		}else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540){
			paramForHeroLevels = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 53);
		}else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720){
			paramForHeroLevels = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 70);
		}else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080){
			paramForHeroLevels = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 105);
		}else{
			paramForHeroLevels = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 140);
		}

		setStarsForHeroLevels.setLayoutParams(paramForHeroLevels);

		switch (level) {
		case 1:
			setStarsForHeroLevels.setBackgroundResource(R.drawable.rounded_recruite_level);
			addStars(1, setStarsForHeroLevels);
			break;

		case 2:
			setStarsForHeroLevels.setBackgroundResource(R.drawable.rounded_dynamo_level);
			addStars(2, setStarsForHeroLevels);
			break;

		case 3:
			setStarsForHeroLevels.setBackgroundResource(R.drawable.rounded_worrier_level);
			addStars(3, setStarsForHeroLevels);
			break;

		case 4:
			setStarsForHeroLevels.setBackgroundResource(R.drawable.rounded_elite_level);
			addStars(4, setStarsForHeroLevels);
			break;

		case 5:
			setStarsForHeroLevels.setBackgroundResource(R.drawable.rounded_legend_level);
			addStars(5, setStarsForHeroLevels);
			break;
		}
		eachStarLayout.addView(setStarsForHeroLevels);
		isViewCreated = false;
	}


	void addStars(int level, LinearLayout starLayout) {
		ImageView starImgArray[] = new ImageView[level];
		for(int i = 0; i < level; i++){
			starImgArray[i] = new ImageView(HeroLevel_View.this);
			LinearLayout.LayoutParams paramForHeroLevels = null;
			if(LoginZoomToU.width <= 320){
				paramForHeroLevels = new LinearLayout.LayoutParams(25, 25);
			}else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540){
				paramForHeroLevels = new LinearLayout.LayoutParams(37, 37);
			}else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720){
				paramForHeroLevels = new LinearLayout.LayoutParams(50, 50);
			}else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080){
				paramForHeroLevels = new LinearLayout.LayoutParams(75, 75);
			}else{
				paramForHeroLevels = new LinearLayout.LayoutParams(100, 100);
			}
			paramForHeroLevels.gravity = Gravity.CENTER;
			paramForHeroLevels.setMargins(10, 0, 0, 0);
			starImgArray[i].setLayoutParams(paramForHeroLevels);
			starImgArray[i].setImageResource(R.drawable.icon_starselect);
			starImgArray[i].setId(i);
			starLayout.addView(starImgArray[i]);
			if(i == (level-1)){
				TextView starLevelTxt = new TextView(HeroLevel_View.this);
				LinearLayout.LayoutParams paramForstarLevelTxt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
				starLevelTxt.setGravity(Gravity.CENTER);
				paramForstarLevelTxt.leftMargin = 10;
				starLevelTxt.setLayoutParams(paramForstarLevelTxt);
				starLevelTxt.setTextColor(Color.WHITE);

				starLevelTxt.setTextSize(15f);
				setTextToStarLayout(i, starLevelTxt);
				starLayout.addView(starLevelTxt);
			}
		}
	}

	private void setTextToStarLayout(int i, TextView starLevelTxt) {
		switch (i) {
		case 0:
			starLevelTxt.setText("ROOKIE");
			break;
		case 1:
			starLevelTxt.setText("DYNAMO");
			break;
		case 2:
			starLevelTxt.setText("WARRIOR");
			break;
		case 3:
			starLevelTxt.setText("ELITE");
			break;
		case 4:
			starLevelTxt.setText("LEGENDARY");
			break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		isHeroLevelViewVisible = false;
	}

	private void GetHeroLevelAsyncTask(){
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressForHeroLevel != null)
						if(progressForHeroLevel.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForHeroLevel);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(progressForHeroLevel != null)
					progressForHeroLevel = null;
				progressForHeroLevel = new ProgressDialog(HeroLevel_View.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForHeroLevel);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					webServiceHandler.getCurrentCourierLevel();
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(progressForHeroLevel != null)
						if(progressForHeroLevel.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForHeroLevel);
				}catch(Exception e){
					e.printStackTrace();
				}

				try{
					if(WebserviceHandler.jObjOfCurrentCourierLevel != null){
						try {
							switch (WebserviceHandler.jObjOfCurrentCourierLevel.getString("Level")) {
								case "Recruit":
									yourLevel = 0;
									courierLevelTxt = "ROOKIE";
									break;
								case "Dynamo":
									yourLevel = 1;
									courierLevelTxt = "DYNAMO";
									break;
								case "Warrior":
									yourLevel = 2;
									courierLevelTxt = "WARRIOR";
									break;
								case "Elite":
									yourLevel = 3;
									courierLevelTxt = "ELITE";
									break;
								case "Legend":
									yourLevel = 4;
									courierLevelTxt = "LEGENDARY";
									break;
								default :
									yourLevel = 5;
									courierLevelTxt = "";
							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					String headerTxtHeroLevelMsg = "";
					try {
						headerTxtHeroLevelMsg = "So far you've earned "+WebserviceHandler.jObjOfCurrentCourierLevel.getInt("Points")+" points, that makes you a "+courierLevelTxt+" driver!";
					} catch (Exception e1) {
						e1.printStackTrace();
						headerTxtHeroLevelMsg = "";
					}
					headerTxtHeroLevel.setText(headerTxtHeroLevelMsg);

					showHeroLevelDetailViews();
					Timer timerCourierOnline = new Timer();
					timerCourierOnline.schedule(new TimerTask() {
						@Override
						public void run() {
							SlideMenuZoom2u.setCourierToOnlineForChat();
						}
					}, 1000);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();
	}



	
}
