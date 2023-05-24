package com.z2u.weeklycourierstats;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;
import com.androidquery.AQuery;
import com.zoom2u.datamodels.Hero_LeaderBoardModel;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeeklyCourierUpdateView extends Activity{

	ImageView closeViewBtn;
	RoundedImageView weeklyCourierCustomerImg; 
	TextView headerTxtWeeklyUpdates, dateTxtWeeklyCourier, deliveriesPOintTxt, dollerPOintTxt, thumbsUpPointTxt,
		pointsPointTxt, bottomTxtCourierUpdate;
	Button deliveriesUpBtn, deliveriesGraphBtn, dollerUpBtn, dollerGraphBtn, thumbsUpBtn, thumbsGraphBtn, 
		pointsUpBtn, pointsGraphBtn;
	ListView leaderListWeeklyCourier;
	
	RelativeLayout layoutLeaderBoard;
	
	ProgressDialog progressDialogForWeeklyCourierUpdate;
	WeeklyCourierAdapterView weeklyCourierAdapterView;
	
	ArrayList<Hero_LeaderBoardModel> arrayOfWeeklyCourier;
	int LEADERBOARD_LAYOUT_HEIGHT = 0;
	AQuery aq;
	
	@Override
	synchronized protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		loadWeeklyCourierUpdateView();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weekly_courier_update);
		
		loadWeeklyCourierUpdateView();
	}
	
	private void loadWeeklyCourierUpdateView() {
		if(aq != null)
			aq = null;
		aq = new AQuery(WeeklyCourierUpdateView.this);
		LoginZoomToU.notificUINewBookingVisibleCount = 1;
		inItWeeklyCourierUpdateView();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
	// ********* In-it weekly courier update view
	void inItWeeklyCourierUpdateView(){
		try {
			PushReceiver.loginEditorForPushy.putString("WeeklyCourierStatus", "0");
			PushReceiver.loginEditorForPushy.commit();
			
			if(arrayOfWeeklyCourier != null)
				arrayOfWeeklyCourier.clear();
			else
				arrayOfWeeklyCourier = new ArrayList<Hero_LeaderBoardModel>();
			
			JSONObject jObjOfWeeklyUpdateMain = new JSONObject(getIntent().getStringExtra("WeeklyCourierUpdateStr"));	
			JSONArray jArrayOfTopLeader = jObjOfWeeklyUpdateMain.getJSONArray("topLeaders");	
			for(int i = 0; i < jArrayOfTopLeader.length(); i++){
				Hero_LeaderBoardModel modelTopLeaderBoard = new Hero_LeaderBoardModel();
				modelTopLeaderBoard.setFirstName(jArrayOfTopLeader.getJSONObject(i).getString("FirstName"));
				modelTopLeaderBoard.setLastName(jArrayOfTopLeader.getJSONObject(i).getString("LastName"));
				modelTopLeaderBoard.setPoints(jArrayOfTopLeader.getJSONObject(i).getInt("Points"));
				modelTopLeaderBoard.setPhoto(jArrayOfTopLeader.getJSONObject(i).getString("Photo"));
				arrayOfWeeklyCourier.add(modelTopLeaderBoard);
				modelTopLeaderBoard = null;
			}
			jArrayOfTopLeader = null;
			
			if(layoutLeaderBoard != null)
				layoutLeaderBoard = null;
			layoutLeaderBoard = (RelativeLayout) findViewById(R.id.layoutLeaderBoard);
			setLayoutLeaderBoard();
			if(weeklyCourierCustomerImg != null)
				weeklyCourierCustomerImg = null;
			weeklyCourierCustomerImg = (RoundedImageView) findViewById(R.id.weeklyCourierCustomerImg);
			String loadedImage = jObjOfWeeklyUpdateMain.getJSONObject("data").getString("Photo");  
		    aq.id(weeklyCourierCustomerImg).image(loadedImage, true, true, 80, R.drawable.icon_account, null, 0, 0);
		    
		    if(bottomTxtCourierUpdate != null)
		    	bottomTxtCourierUpdate = null;
		    bottomTxtCourierUpdate = (TextView) findViewById(R.id.bottomTxtCourierUpdate);

			if(headerTxtWeeklyUpdates != null)
				headerTxtWeeklyUpdates = null;
			headerTxtWeeklyUpdates = (TextView) findViewById(R.id.headerTxtWeeklyUpdates);

			String headrTxtWeeklyUpdateStr = "Hi "+jObjOfWeeklyUpdateMain.getJSONObject("data").getString("FirstName")+" "+
					"\nHere are your weekly stats";
			headerTxtWeeklyUpdates.setText(headrTxtWeeklyUpdateStr);

			if(dateTxtWeeklyCourier != null)
				dateTxtWeeklyCourier = null;
			dateTxtWeeklyCourier = (TextView) findViewById(R.id.dateTxtWeeklyCourier);

			if(!jObjOfWeeklyUpdateMain.getJSONObject("data").getString("StartDate").equals("") ||
					!jObjOfWeeklyUpdateMain.getJSONObject("data").getString("EndDate").equals("")){
				String[] startDateSTr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(jObjOfWeeklyUpdateMain.getJSONObject("data").getString("StartDate")).split(" ");
				String[] endDateSTr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(jObjOfWeeklyUpdateMain.getJSONObject("data").getString("EndDate")).split(" ");
				String dateTxtWeeklyCourierStr = startDateSTr[0]+" to "+endDateSTr[0];
				dateTxtWeeklyCourier.setText(dateTxtWeeklyCourierStr);
				startDateSTr = null;
				endDateSTr = null;
			}else
				dateTxtWeeklyCourier.setText("NA");
				
			if(deliveriesPOintTxt != null)
				deliveriesPOintTxt = null;
			deliveriesPOintTxt = (TextView) findViewById(R.id.deliveriesPOintTxt);

			deliveriesPOintTxt.setText(""+jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("DeliveriesOfWeek"));
			if(dollerPOintTxt != null)
				dollerPOintTxt = null;
			dollerPOintTxt = (TextView) findViewById(R.id.dollerPOintTxt);

			dollerPOintTxt.setText(""+jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("MoneyOfWeek"));
			if(thumbsUpPointTxt != null)
				thumbsUpPointTxt = null;
			thumbsUpPointTxt = (TextView) findViewById(R.id.thumbsUpPointTxt);

			thumbsUpPointTxt.setText(""+jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("ThumbsUpOfWeek"));
			if(pointsPointTxt != null)
				pointsPointTxt = null;
			pointsPointTxt = (TextView) findViewById(R.id.pointsPointTxt);

			pointsPointTxt.setText(""+jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("PointsOfWeek"));
			if(deliveriesUpBtn != null)
				deliveriesUpBtn = null;
			deliveriesUpBtn = (Button) findViewById(R.id.deliveriesUpBtn);

			getWeeklyPercent(deliveriesUpBtn, jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("DeliveriesOfWeek"), 
					jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("DeliveriesOfLastWeek"));
			
			if(deliveriesGraphBtn != null)
				deliveriesGraphBtn = null;
			deliveriesGraphBtn = (Button) findViewById(R.id.deliveriesGraphBtn);

			deliveriesGraphBtn.setText(jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("TotalDeliveries")+" total");
			if(dollerUpBtn != null)
				dollerUpBtn = null;
			dollerUpBtn = (Button) findViewById(R.id.dollerUpBtn);

			getWeeklyPercent(dollerUpBtn, jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("MoneyOfWeek"), 
					jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("MoneyOfLastWeek"));
			
			if(dollerGraphBtn != null)
				dollerGraphBtn = null;
			dollerGraphBtn = (Button) findViewById(R.id.dollerGraphBtn);

			dollerGraphBtn.setText(jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("TotalMoney")+" total");
			if(thumbsUpBtn != null)
				thumbsUpBtn = null;
			thumbsUpBtn = (Button) findViewById(R.id.thumbsUpBtn);

			getWeeklyPercent(thumbsUpBtn, jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("ThumbsUpOfWeek"), 
					jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("ThumbsUpOfLastWeek"));
			if(thumbsGraphBtn != null)
				thumbsGraphBtn = null;
			thumbsGraphBtn = (Button) findViewById(R.id.thumbsGraphBtn);

			thumbsGraphBtn.setText(jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("TotalThumbsUp")+" total");
			if(pointsUpBtn != null)
				pointsUpBtn = null;
			pointsUpBtn = (Button) findViewById(R.id.pointsUpBtn);

			getWeeklyPercent(pointsUpBtn, jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("PointsOfWeek"), 
					jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("PointsOfLastWeek"));
			if(pointsGraphBtn != null)
				pointsGraphBtn = null;
			pointsGraphBtn = (Button) findViewById(R.id.pointsGraphBtn);

			pointsGraphBtn.setText(jObjOfWeeklyUpdateMain.getJSONObject("data").getInt("TotalPoints")+" total");
			if(leaderListWeeklyCourier != null)
				leaderListWeeklyCourier = null;
			leaderListWeeklyCourier = (ListView) findViewById(R.id.leaderListWeeklyCourier);
			leaderListWeeklyCourier.setCacheColorHint(Color.parseColor("#ffffff"));
			if(closeViewBtn != null)
				closeViewBtn = null;
			closeViewBtn = (ImageView) findViewById(R.id.closeViewBtn);
			closeViewBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
				}
			});
			
			weeklyCourierAdapterView = new WeeklyCourierAdapterView(WeeklyCourierUpdateView.this, R.layout.weekly_courier_update);
			leaderListWeeklyCourier.setAdapter(weeklyCourierAdapterView);
			weeklyCourierAdapterView = null;
		} catch (Exception e) {
			e.printStackTrace();
			finish();
		}
	}
	
	/********************  Weekly Percent to Up or Down *********************/
	private void getWeeklyPercent(Button deliveriesUpBtn2, int int1, int int2){
		if(int1 != 0 && int2 != 0 ){
			percentageCount(deliveriesUpBtn2, int1, int2);
		}else if(int1 != 0 && int2 == 0){
			deliveriesUpBtn2.setText(int1+"% last week");
			deliveriesUpBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up, 0, 0);
		}else if(int1 == 0 && int2 != 0){
			deliveriesUpBtn2.setText(int2+"% last week");
			deliveriesUpBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.down, 0, 0);
		}else{
			deliveriesUpBtn2.setText("0% last week");
			deliveriesUpBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.down, 0, 0);
		}
	}

	private void percentageCount(Button deliveriesUpBtn2, int int1, int int2){
		float currentWeekStatus = int1;
		float lastWeekStatus = int2;
		float percentValue = (currentWeekStatus - lastWeekStatus)/lastWeekStatus;
		int weeklyPercantage = (int) (percentValue * 100);
		weeklyPercantage = Math.abs(weeklyPercantage);
		deliveriesUpBtn2.setText(weeklyPercantage+"% last week");
		if(percentValue > 0.0)
			deliveriesUpBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.up, 0, 0);
		else
			deliveriesUpBtn2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.down, 0, 0);
	}
	/**************************    Weekly Percent to Up or Down ********************/
	
	private void setLayoutLeaderBoard() {
		
		DisplayMetrics displayMetrics = WeeklyCourierUpdateView.this.getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams paramLayoutLeaderBoard = null;
		if(LoginZoomToU.width <= 320){
			LEADERBOARD_LAYOUT_HEIGHT = displayMetrics.heightPixels - 372;
			paramLayoutLeaderBoard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LEADERBOARD_LAYOUT_HEIGHT);
			paramLayoutLeaderBoard.leftMargin = 7;
			paramLayoutLeaderBoard.rightMargin = 5;
			paramLayoutLeaderBoard.topMargin = 7;
			paramLayoutLeaderBoard.bottomMargin = 7;
		}else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540){
			LEADERBOARD_LAYOUT_HEIGHT = displayMetrics.heightPixels - 558;
			paramLayoutLeaderBoard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LEADERBOARD_LAYOUT_HEIGHT);
			paramLayoutLeaderBoard.leftMargin = 10;
			paramLayoutLeaderBoard.rightMargin = 8;
			paramLayoutLeaderBoard.topMargin = 10;
			paramLayoutLeaderBoard.bottomMargin = 8;
		}else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720){
			LEADERBOARD_LAYOUT_HEIGHT = displayMetrics.heightPixels - 744;
			paramLayoutLeaderBoard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LEADERBOARD_LAYOUT_HEIGHT);
			paramLayoutLeaderBoard.leftMargin = 12;
			paramLayoutLeaderBoard.rightMargin = 10;
			paramLayoutLeaderBoard.topMargin = 12;
			paramLayoutLeaderBoard.bottomMargin = 10;
		}else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080){
			LEADERBOARD_LAYOUT_HEIGHT = displayMetrics.heightPixels - 1116;
			paramLayoutLeaderBoard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LEADERBOARD_LAYOUT_HEIGHT);
			paramLayoutLeaderBoard.leftMargin = 17;
			paramLayoutLeaderBoard.rightMargin = 15;
			paramLayoutLeaderBoard.topMargin = 17;
			paramLayoutLeaderBoard.bottomMargin = 15;
		}else{
			LEADERBOARD_LAYOUT_HEIGHT = displayMetrics.heightPixels - 1488;
			paramLayoutLeaderBoard = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, LEADERBOARD_LAYOUT_HEIGHT);
			paramLayoutLeaderBoard.leftMargin = 22;
			paramLayoutLeaderBoard.rightMargin = 20;
			paramLayoutLeaderBoard.topMargin = 22;
			paramLayoutLeaderBoard.bottomMargin = 20;
		}
		
		layoutLeaderBoard.setLayoutParams(paramLayoutLeaderBoard);
		layoutLeaderBoard.setBackgroundColor(Color.WHITE);
		displayMetrics = null;
		paramLayoutLeaderBoard = null;
	}


	
	class WeeklyCourierAdapterView extends ArrayAdapter<Hero_LeaderBoardModel>{

		private int[] colors = new int[] {0xF0F0F0F3, 0xFFFFFFFF};
		Context con;
		
		public WeeklyCourierAdapterView(Context con, int resourceId) {
			super(con, resourceId);
			this.con = con;
		}

		@Override
		public int getCount() {
			return arrayOfWeeklyCourier.size();
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
		
		@Override
		public View getView(int position, View listItemView, ViewGroup arg2) {
			try {
				if (listItemView == null)
					listItemView = LayoutInflater.from(con).inflate(R.layout.leaderboard_listitem, null);
				
				RelativeLayout listItemLayout = (RelativeLayout) listItemView.findViewById(R.id.listItemLayoutLeaderView);
				
				TextView leaderBoardItemCount = (TextView) listItemView.findViewById(R.id.leaderBoardItemCount);

				leaderBoardItemCount.setText(""+(position+1));
				
				TextView leaderBoardCourierName = (TextView) listItemView.findViewById(R.id.leaderBoardCourierName);

				leaderBoardCourierName.setText(arrayOfWeeklyCourier.get(position).getFirstName()+" "+arrayOfWeeklyCourier.get(position).getLastName());
				
				TextView leaderBoardCourierPoint = (TextView) listItemView.findViewById(R.id.leaderBoardCourierPoint);

				leaderBoardCourierPoint.setText(arrayOfWeeklyCourier.get(position).getPoints()+" points");
				
				RoundedImageView leaderBoardCourierImg = (RoundedImageView) listItemView.findViewById(R.id.leaderBoardCourierImg);
				
				String loadedImage = arrayOfWeeklyCourier.get(position).getPhoto();   
			    aq.id(leaderBoardCourierImg).image(loadedImage, true, true, 80, R.drawable.icon_account, null, 0, 0);
				
				    //   ***********  Change alternate color for present day booking  **************		
				   int colorPos = position % colors.length;
				   colorPos = position % colors.length;
				   listItemLayout.setBackgroundColor(colors[colorPos]);
				   //   **************** ----------------- ***************** 
			} catch (Exception e) {
				e.printStackTrace();
			}
			return listItemView;
		}
	}
	
	
}
