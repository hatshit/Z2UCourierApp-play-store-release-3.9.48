package com.zoom2u;

import org.json.JSONObject;

import com.customnotify_event.CustomNotification_View;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.datamodels.ShiftModel;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.ShiftFragment;
import com.zoom2u.slidemenu.accountdetail_section.SummaryReportView;
import com.zoom2u.webservice.WebserviceHandler;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class ShiftViewDetail extends Activity implements OnClickListener{

	TextView headerTxtShiftDetail, shiftTitleTxt, countChatShiftDetail, dateTxtDeliveryRunDetail, startTimeTxtDeliveryRunDetail,
			startTimeValueDeliveryRunDetail, endTimeTxtDeliveryRunDetail, endTimeValueDeliveryRunDetail, priceTxtDeliveryRunDetail,
			priceTxt1DeliveryRunDetail, addressHeaderTxtDeliveryRunDetail, addressTxtTxtDeliveryRunDetail,
			statusTxtDeliveryRunDetail, statusValueDeliveryRunDetail, msgTxtDeliveryRunDetail;

	ImageView backFromShiftDetail, shiftDetailChatIcon;
	Button acceptBtnShiftDetail, rejectBtnShiftDetail;
	Dialog dialogInShiftDetailView;

	RelativeLayout statusLayoutDeliveryRunDetail;

	boolean isLeavingShiftInDetail;
	ShiftModel deliveryRunModel;
	ProgressDialog progressToLoadShiftItems;
	@Override
	protected void onResume() {
		super.onResume();
		PushReceiver.IsOtherScreenOpen =true;
		SlideMenuZoom2u.setCourierToOnlineForChat();
		Model_DeliveriesToChat.showExclamationForUnreadChat(countChatShiftDetail);
		SlideMenuZoom2u.countChatBookingView = countChatShiftDetail;
	}

	@Override
	protected void onPause() {
		super.onPause();
		PushReceiver.IsOtherScreenOpen =false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		PushReceiver.IsOtherScreenOpen =true;
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		PushReceiver.IsOtherScreenOpen =true;
		try{
			setContentView(R.layout.shiftdetailview);
			PushReceiver.IsOtherScreenOpen =true;
			RelativeLayout headerSummaryReportLayout=findViewById(R.id.demoViewShiftDetail);
			Window window = ShiftViewDetail.this.getWindow();
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			}
			if(MainActivity.isIsBackGroundGray()){
				headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
				window.setStatusBarColor(Color.parseColor("#374350"));
			}else{
				headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
				window.setStatusBarColor(Color.parseColor("#00A6E2"));
			}
				if(savedInstanceState == null)
				deliveryRunModel = getIntent().getParcelableExtra("ShiftModel");
			else {
				ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
				if(LoginZoomToU.NOVA_BOLD == null)
					LoginZoomToU.staticFieldInit(ShiftViewDetail.this);
				deliveryRunModel = savedInstanceState.getParcelable("ShiftModel");
			}
			inItShiftDetail();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
		//	SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putParcelable("ShiftModel", deliveryRunModel);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}
 
	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		 try{
			if(savedInstanceState != null){
				ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
				deliveryRunModel = savedInstanceState.getParcelable("ShiftModel");
				
				if(LoginZoomToU.NOVA_BOLD == null)
					LoginZoomToU.staticFieldInit(ShiftViewDetail.this);
				
				inItShiftDetail();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	/***********  Initialize shift detail view *************/
	void inItShiftDetail(){
		try {
			if(headerTxtShiftDetail == null)
				headerTxtShiftDetail = (TextView) findViewById(R.id.headerTxtShiftDetail);

			if(countChatShiftDetail == null)
				countChatShiftDetail = (TextView) findViewById(R.id.countChatShiftDetail);

			countChatShiftDetail.setVisibility(View.GONE);
			SlideMenuZoom2u.countChatBookingView = countChatShiftDetail;

			if(shiftDetailChatIcon == null)
				shiftDetailChatIcon = (ImageView) findViewById(R.id.shiftDetailChatIcon);
			shiftDetailChatIcon.setOnClickListener(this);

			statusLayoutDeliveryRunDetail = (RelativeLayout) findViewById(R.id.statusLayoutDeliveryRunDetail);

			dateTxtDeliveryRunDetail = (TextView) findViewById(R.id.dateTxtDeliveryRunDetail);

			try{
				if(!deliveryRunModel.getStartDateTime().equals("")){
					dateTxtDeliveryRunDetail.setText(LoginZoomToU.checkInternetwithfunctionality.getDayOfWeekFromServer(deliveryRunModel.getStartDateTime()));
				}else
					dateTxtDeliveryRunDetail.setText("-NA-");
			}catch(Exception e){
				e.printStackTrace();
				dateTxtDeliveryRunDetail.setText("-NA-");
			}

			startTimeTxtDeliveryRunDetail = (TextView) findViewById(R.id.startTimeTxtDeliveryRunDetail);

			startTimeValueDeliveryRunDetail = (TextView) findViewById(R.id.startTimeValueDeliveryRunDetail);

			try{
				if(!deliveryRunModel.getStartDateTime().equals("")){
					String startTimeStr[] = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(deliveryRunModel.getStartDateTime()).split(" ");
					startTimeValueDeliveryRunDetail.setText(startTimeStr[1]+" "+startTimeStr[2]);
				}else
					startTimeValueDeliveryRunDetail.setText("-NA-");
			}catch(Exception e){
				e.printStackTrace();
				startTimeValueDeliveryRunDetail.setText("-NA-");
			}

			endTimeTxtDeliveryRunDetail = (TextView) findViewById(R.id.endTimeTxtDeliveryRunDetail);

			endTimeValueDeliveryRunDetail = (TextView) findViewById(R.id.endTimeValueDeliveryRunDetail);

			try{
				if(!deliveryRunModel.getEndDateTime().equals("")){
					String endTimeStr[] = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(deliveryRunModel.getEndDateTime()).split(" ");
					endTimeValueDeliveryRunDetail.setText(endTimeStr[1]+" "+endTimeStr[2]);
				}else
					endTimeValueDeliveryRunDetail.setText("-NA-");
			}catch(Exception e){
				e.printStackTrace();
				endTimeValueDeliveryRunDetail.setText("-NA-");
			}

			priceTxtDeliveryRunDetail = (TextView) findViewById(R.id.priceTxtDeliveryRunDetail);

			priceTxtDeliveryRunDetail.setText("$"+deliveryRunModel.getPayPerDelivery());
			priceTxt1DeliveryRunDetail = (TextView) findViewById(R.id.priceTxt1DeliveryRunDetail);

			priceTxt1DeliveryRunDetail.setText("Estimated pay\nper delivery");
			addressHeaderTxtDeliveryRunDetail = (TextView) findViewById(R.id.addressHeaderTxtDeliveryRunDetail);

			addressHeaderTxtDeliveryRunDetail.setText(deliveryRunModel.getTitleShiftItem());
			addressTxtTxtDeliveryRunDetail = (TextView) findViewById(R.id.addressTxtTxtDeliveryRunDetail);

			statusTxtDeliveryRunDetail = (TextView) findViewById(R.id.statusTxtDeliveryRunDetail);

			statusValueDeliveryRunDetail = (TextView) findViewById(R.id.statusValueDeliveryRunDetail);

			msgTxtDeliveryRunDetail = (TextView) findViewById(R.id.msgTxtDeliveryRunDetail);

		//	LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(startDateTime)

			if(backFromShiftDetail == null)
				backFromShiftDetail = (ImageView) findViewById(R.id.backFromShiftDetail);
			backFromShiftDetail.setOnClickListener(this);
			if(acceptBtnShiftDetail == null)
				acceptBtnShiftDetail = (Button) findViewById(R.id.acceptBtnShiftDetail);

			acceptBtnShiftDetail.setOnClickListener(this);
			if(rejectBtnShiftDetail == null)
				rejectBtnShiftDetail = (Button) findViewById(R.id.rejectBtnShiftDetail);

			rejectBtnShiftDetail.setOnClickListener(this);

			try {
				if (deliveryRunModel.getStatus().equals("Confirmed")) {
					statusLayoutDeliveryRunDetail.setBackgroundResource(R.color.colorPrimary);
					statusTxtDeliveryRunDetail.setVisibility(View.VISIBLE);
					statusValueDeliveryRunDetail.setVisibility(View.VISIBLE);
					statusValueDeliveryRunDetail.setText("Confirmed");
					msgTxtDeliveryRunDetail.setVisibility(View.VISIBLE);
					msgTxtDeliveryRunDetail.setText(R.string.msg_confirm_txt_deliveryrun_detail);
					acceptBtnShiftDetail.setVisibility(View.GONE);
					rejectBtnShiftDetail.setVisibility(View.GONE);
					addressTxtTxtDeliveryRunDetail.setText(deliveryRunModel.getCity()+", "+deliveryRunModel.getState());
				} else if (deliveryRunModel.getStatus().equals("Accepted")) {
					statusTxtDeliveryRunDetail.setVisibility(View.VISIBLE);
					statusValueDeliveryRunDetail.setVisibility(View.VISIBLE);
					msgTxtDeliveryRunDetail.setVisibility(View.VISIBLE);
					msgTxtDeliveryRunDetail.setText(R.string.msg_txt_deliveryrun_detail);
					rejectBtnShiftDetail.setText("I am no longer available");
					rejectBtnShiftDetail.setVisibility(View.VISIBLE);
					if (LoginZoomToU.checkInternetwithfunctionality.greaterThan24Hour_ForDeliveryRun(deliveryRunModel.getStartDateTime())) {
						acceptBtnShiftDetail.setVisibility(View.GONE);
						addressTxtTxtDeliveryRunDetail.setText(deliveryRunModel.getCity()+", "+deliveryRunModel.getState());
					} else {
						acceptBtnShiftDetail.setVisibility(View.VISIBLE);
						acceptBtnShiftDetail.setText("Confirm- I will be there");
						addressTxtTxtDeliveryRunDetail.setText(deliveryRunModel.getCity()+", "+deliveryRunModel.getState());
					}
				} else {
					statusLayoutDeliveryRunDetail.setVisibility(View.GONE);
					msgTxtDeliveryRunDetail.setVisibility(View.GONE);
					acceptBtnShiftDetail.setVisibility(View.VISIBLE);
					acceptBtnShiftDetail.setText("Accept");
					rejectBtnShiftDetail.setVisibility(View.VISIBLE);
					rejectBtnShiftDetail.setText("Decline");
					addressTxtTxtDeliveryRunDetail.setText(deliveryRunModel.getCity()+", "+deliveryRunModel.getState());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.backFromShiftDetail:
				back2ShiftList();
				break;
			case R.id.acceptBtnShiftDetail:
				if (deliveryRunModel.getStatus().equals("Offered")
						&& ShiftFragment.checkDateBeforeAccept(deliveryRunModel.getEndDateTime()) == true)
					callAsyncTaskForAcceptRejectShift();
				else if (deliveryRunModel.getStatus().equals("Accepted"))
					callAsyncTaskForAcceptRejectShift();
				else
					DialogActivity.alertDialogView(ShiftViewDetail.this, "Alert!", "Time is over, you can't accept this delivery run");
				break;
			case R.id.rejectBtnShiftDetail:
				isLeavingShiftInDetail = true;
				callAsyncTaskForAcceptRejectShift();
				break;
			case  R.id.shiftDetailChatIcon:
				Intent chatViewIntent = new Intent(ShiftViewDetail.this, ChatViewBookingScreen.class);
				startActivity(chatViewIntent);
				chatViewIntent = null;
			default:
				break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		back2ShiftList();
	}
	
	void back2ShiftList(){
		ConfirmPickUpForUserName.isDropOffSuccessfull = 15;
		Intent i = new Intent(ShiftViewDetail.this, SlideMenuZoom2u.class);
		startActivity(i);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
	
	void callAsyncTaskForAcceptRejectShift(){
		try{
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
				/*AsyncForAcceptRejectDetailShift acceptRejectShiftAsyncTask = new AsyncForAcceptRejectDetailShift();
				acceptRejectShiftAsyncTask.execute();
				acceptRejectShiftAsyncTask = null;*/
				AsyncForAcceptRejectDetailShift();
			}else
				DialogActivity.alertDialogView(ShiftViewDetail.this, "No network!", "No network connection! please try again later");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void AsyncForAcceptRejectDetailShift(){
		final String[] respStrAcept_Arrive_LeavingShift = {"0"};
		final String[] presentLocationStr = {""};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(deliveryRunModel.getStatus().equals("Accepted") && !isLeavingShiftInDetail)
					presentLocationStr[0] = LoginZoomToU.checkInternetwithfunctionality.getPresentLocation();

				progressToLoadShiftItems = new ProgressDialog(ShiftViewDetail.this);

				try{
					if(progressToLoadShiftItems.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressToLoadShiftItems);
				}catch(Exception e){
					e.printStackTrace();
				}
				Custom_ProgressDialogBar.inItProgressBar(progressToLoadShiftItems);
			}

			@Override
			public void doInBackground() {
				try{
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if(isLeavingShiftInDetail == true)
						respStrAcept_Arrive_LeavingShift[0] = webServiceHandler.leavingShift(deliveryRunModel.getShiftID());
					else if (deliveryRunModel.getStatus().equals("Offered")) {
						respStrAcept_Arrive_LeavingShift[0] = webServiceHandler.acceptShift(deliveryRunModel.getShiftID());
					} else {
						respStrAcept_Arrive_LeavingShift[0] = webServiceHandler.arrivedShift(deliveryRunModel.getShiftID(), presentLocationStr[0]);
					}
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
					respStrAcept_Arrive_LeavingShift[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				try{
					if(progressToLoadShiftItems != null)
						if(progressToLoadShiftItems.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressToLoadShiftItems);

					if(!respStrAcept_Arrive_LeavingShift[0].equals("0")){
						JSONObject jObjOFAcept_Arrive_LeavingShift = new JSONObject(respStrAcept_Arrive_LeavingShift[0]);
						if(deliveryRunModel.getStatus().equals("Offered")){
							if(jObjOFAcept_Arrive_LeavingShift.getBoolean("success")) {
								if (isLeavingShiftInDetail)
									dialogViewOfShiftDetail("Declined!", "Delivery run declined.");
								else
									dialogDeliveryRunConfirmed("Delivery Run\nConfirmed");
							} else
								dialogViewOfShiftDetail("Delivery run request!", "Sorry, your request can't process at this moment");
						}else{
							if(jObjOFAcept_Arrive_LeavingShift.getBoolean("success")){
								if(isLeavingShiftInDetail)
									dialogViewOfShiftDetail("Success!", "Delivery run is no longer available for you.");
								else {
									dialogViewOfShiftDetail("Confirmed!", "Delivery run confirmed");
									try {
										if (jObjOFAcept_Arrive_LeavingShift.getJSONObject("notification") != null){
											Intent customNotificationIntent = new Intent(ShiftViewDetail.this, CustomNotification_View.class);
											customNotificationIntent.putExtra("CustomNotifiedData", jObjOFAcept_Arrive_LeavingShift.getJSONObject("notification").toString());
											customNotificationIntent.putExtra("BookingID", jObjOFAcept_Arrive_LeavingShift.getInt("shiftId"));
											startActivity(customNotificationIntent);
											customNotificationIntent = null;
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}else
								dialogViewOfShiftDetail("Delivery run request!", "Sorry, your request can't process at this moment");
						}
						jObjOFAcept_Arrive_LeavingShift = null;
					}else
						DialogActivity.alertDialogView(ShiftViewDetail.this, "Sorry!", "Something went wrong, Please try later");

				}catch(Exception e){
					e.printStackTrace();
					DialogActivity.alertDialogView(ShiftViewDetail.this, "Sorry!", "Something went wrong, Please try later");
				}
				isLeavingShiftInDetail = false;
			}
		}.execute();
	}

	

	
	private void dialogViewOfShiftDetail(String titleStr, String msgStr){
		if(dialogInShiftDetailView != null)
			dialogInShiftDetailView = null;
			dialogInShiftDetailView = new Dialog(ShiftViewDetail.this);
			dialogInShiftDetailView.setCancelable(true);
			dialogInShiftDetailView.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialogInShiftDetailView.setContentView(R.layout.dialogview);

			Window window = dialogInShiftDetailView.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) dialogInShiftDetailView.findViewById(R.id.titleDialog);

			enterFieldDialogHEader.setText(titleStr);

			TextView enterFieldDialogMsg = (TextView) dialogInShiftDetailView.findViewById(R.id.dialogMessageText);

			enterFieldDialogMsg.setText(msgStr);

			Button enterFieldDialogDoneBtn = (Button) dialogInShiftDetailView.findViewById(R.id.dialogDoneBtn);
			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					dialogInShiftDetailView.dismiss();
					back2ShiftList();
				}
			});
			dialogInShiftDetailView.show();
	}

	private void dialogDeliveryRunConfirmed(String titleStr){
		if(dialogInShiftDetailView != null)
			dialogInShiftDetailView = null;
		dialogInShiftDetailView = new Dialog(ShiftViewDetail.this);
		dialogInShiftDetailView.setCancelable(true);
		dialogInShiftDetailView.getWindow().setBackgroundDrawableResource(R.color.transparent_black_bg);
		dialogInShiftDetailView.setContentView(R.layout.delivery_run_confirm_dialog);

		Window window = dialogInShiftDetailView.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		android.view.WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		TextView titleDeliveryRunConfirmDialog = (TextView) dialogInShiftDetailView.findViewById(R.id.titleDeliveryRunConfirmDialog);

		titleDeliveryRunConfirmDialog.setText(titleStr);

		TextView txtDeliveryRunConfirmDialog = (TextView) dialogInShiftDetailView.findViewById(R.id.txtDeliveryRunConfirmDialog);


		TextView okBtnDeliveryRunConfirmAlrt = (TextView) dialogInShiftDetailView.findViewById(R.id.okBtnDeliveryRunConfirmAlrt);

		okBtnDeliveryRunConfirmAlrt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogInShiftDetailView.dismiss();
				back2ShiftList();
			}
		});
		dialogInShiftDetailView.show();
	}
}
