package com.zoom2u.slidemenu;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.androidquery.AQuery;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.Hero_LeaderBoardModel;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.webservice.WebserviceHandler;

public class Hero_LeaderBoard_InSlideView extends Activity implements View.OnClickListener{

	TextView txtHeroLeaderBoardHeading, countChatLeaderBoard, courierNameTxtLeaderView, pointTxtLeaderBoardView;
	ListView listOfLeaderBoardView;
	ImageView chatBtnLeaderBoard, backBtnLeaderBoard;
	LeaderBoardAdapterView leaderBoardAdapterView;
	ProgressDialog progressDialogForLeaderBoard;
	public ArrayList<Hero_LeaderBoardModel> arrayOfLeaderBoard;
	
	public Hero_LeaderBoard_InSlideView() {}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_heroleaderboard);
		try {
			arrayOfLeaderBoard = new ArrayList<Hero_LeaderBoardModel>();
			if(savedInstanceState != null){
				try {
					ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
					if(arrayOfLeaderBoard != null)
						arrayOfLeaderBoard = null;
					arrayOfLeaderBoard = savedInstanceState.getParcelableArrayList("ArrayOfLeaderBorrd");

					if(LoginZoomToU.NOVA_BOLD == null)
						LoginZoomToU.staticFieldInit(Hero_LeaderBoard_InSlideView.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			inItHeroLeaderBoardView();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		Model_DeliveriesToChat.showExclamationForUnreadChat(countChatLeaderBoard);
		SlideMenuZoom2u.countChatBookingView = countChatLeaderBoard;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		try {
		//	SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putParcelableArrayList("ArrayOfLeaderBorrd", arrayOfLeaderBoard);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.chatBtnLeaderBoard:
				Intent chatViewIntent = new Intent(Hero_LeaderBoard_InSlideView.this, ChatViewBookingScreen.class);
				startActivity(chatViewIntent);
				overridePendingTransition(R.anim.right_in, R.anim.left_out);
				break;
			case R.id.backBtnLeaderBoard:
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.right_out);
				break;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}

	/********** Initialize Hero leaderboard View **********/
	void inItHeroLeaderBoardView(){
		try{
			txtHeroLeaderBoardHeading = (TextView) findViewById(R.id.txtHeroLeaderBoardHeading);

			countChatLeaderBoard = (TextView) findViewById(R.id.countChatLeaderBoard);

			countChatLeaderBoard.setVisibility(View.GONE);
			SlideMenuZoom2u.countChatBookingView = countChatLeaderBoard;

			chatBtnLeaderBoard = (ImageView) findViewById(R.id.chatBtnLeaderBoard);
			chatBtnLeaderBoard.setOnClickListener(this);
			backBtnLeaderBoard = (ImageView) findViewById(R.id.backBtnLeaderBoard);
			backBtnLeaderBoard.setOnClickListener(this);

			courierNameTxtLeaderView = (TextView) findViewById(R.id.courierNameTxtLeaderView);

			pointTxtLeaderBoardView = (TextView) findViewById(R.id.pointTxtLeaderBoardView);

			
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
				/*GetLeaderboardListAsyncTask getLeaderboardListAsyncTask = new GetLeaderboardListAsyncTask();
				getLeaderboardListAsyncTask.execute();
				getLeaderboardListAsyncTask = null;*/
				GetLeaderboardListAsyncTask();
			}else
				DialogActivity.alertDialogView(Hero_LeaderBoard_InSlideView.this, "No network!", "No network connection, Please try again later");
			
			listOfLeaderBoardView = (ListView) findViewById(R.id.listOfLeaderBoardView);
			listOfLeaderBoardView.setCacheColorHint(Color.parseColor("#ffffff"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void GetLeaderboardListAsyncTask(){
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressDialogForLeaderBoard != null)
						if(progressDialogForLeaderBoard.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressDialogForLeaderBoard);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(progressDialogForLeaderBoard != null)
					progressDialogForLeaderBoard = null;
				progressDialogForLeaderBoard = new ProgressDialog(Hero_LeaderBoard_InSlideView.this);
				Custom_ProgressDialogBar.inItProgressBar(progressDialogForLeaderBoard);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					String responseForLeaderboardList = webServiceHandler.getHeroLeaderboardList();
					JSONArray jAarryContentOFLeaderBoard = new JSONArray(responseForLeaderboardList);
					for(int i = 0; i < jAarryContentOFLeaderBoard.length(); i++){
						Hero_LeaderBoardModel modelHeroLeaderBoard = new Hero_LeaderBoardModel();
						JSONObject jObjOfHeroLeaderBoardItems = jAarryContentOFLeaderBoard.getJSONObject(i);
						modelHeroLeaderBoard.setFirstName(jObjOfHeroLeaderBoardItems.getString("FirstName"));
						modelHeroLeaderBoard.setLastName(jObjOfHeroLeaderBoardItems.getString("LastName"));
						modelHeroLeaderBoard.setPoints(jObjOfHeroLeaderBoardItems.getInt("Points"));
						modelHeroLeaderBoard.setPhoto(jObjOfHeroLeaderBoardItems.getString("Photo"));
						arrayOfLeaderBoard.add(modelHeroLeaderBoard);
						modelHeroLeaderBoard = null;
						jObjOfHeroLeaderBoardItems = null;
					}
					jAarryContentOFLeaderBoard = null;
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try{
					if(progressDialogForLeaderBoard != null)
						if(progressDialogForLeaderBoard.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressDialogForLeaderBoard);

					if(arrayOfLeaderBoard.size() > 0){
						leaderBoardAdapterView = new LeaderBoardAdapterView(Hero_LeaderBoard_InSlideView.this, R.layout.leaderboard_listitem);
						listOfLeaderBoardView.setAdapter(leaderBoardAdapterView);
						leaderBoardAdapterView = null;
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();

	}


	
	class LeaderBoardAdapterView extends ArrayAdapter<Hero_LeaderBoardModel>{

		private int[] colors = new int[] {0xF0F0F0F3, 0xFFFFFFFF};
		Context con;
		AQuery aq;
		
		public LeaderBoardAdapterView(Context con, int resourceId) {
			super(con, resourceId);
			this.con = con;
			aq = new AQuery(con);
		}

		@Override
		public int getCount() {
			return arrayOfLeaderBoard.size();
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

				leaderBoardCourierName.setText(arrayOfLeaderBoard.get(position).getFirstName()+" "+arrayOfLeaderBoard.get(position).getLastName());
				
				TextView leaderBoardCourierPoint = (TextView) listItemView.findViewById(R.id.leaderBoardCourierPoint);

				leaderBoardCourierPoint.setText(""+arrayOfLeaderBoard.get(position).getPoints());
				
				RoundedImageView leaderBoardCourierImg = (RoundedImageView) listItemView.findViewById(R.id.leaderBoardCourierImg);
				
				String loadedImage = arrayOfLeaderBoard.get(position).getPhoto();   
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
