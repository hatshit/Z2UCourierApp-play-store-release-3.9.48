package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.suggestprice_team.courier_team.community.CommunityBookingActivity;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.services.ServiceToGetCourierLevel;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;

public class DialogActivityForBooking extends Activity{
	
	public static Dialog enterFieldDialog;
	Intent intent;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.intent = intent;
		showNotificationDialog();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogview_booking);
		PushReceiver.IsOtherScreenOpen =true;
		this.intent = getIntent();
		showNotificationDialog();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}


	/*************  Save activity state when process killed in background*************/
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putBoolean("SetRoutific", WebserviceHandler.isRoutific);
			outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
			//        outState.putParcelableArrayList("NewBookingArray", BookingView.bookingListArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	private void showNotificationDialog() {
		
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(DialogActivityForBooking.this);
		try{
			Intent serviceIntent = new Intent(DialogActivityForBooking.this, ServiceToGetCourierLevel.class);
			startService(serviceIntent);
			serviceIntent = null;
			
		}catch(Exception e){
			e.printStackTrace();
		}

		try {
			TextView titleDialogDefaultNoti = (TextView) findViewById(R.id.titleDialog);
			titleDialogDefaultNoti.setText("Notification!");

				
			TextView dialogMessageTextDefaultNoti = (TextView) findViewById(R.id.dialogMessageText);

			Button dialogDoneBtn = (Button) findViewById(R.id.dialogDoneBtn);
			Button dialogNoBtn = (Button) findViewById(R.id.dialogNoBtn);

			if (intent.getStringExtra("NotificationMessageStr") != null)
				dialogMessageTextDefaultNoti.setText(intent.getStringExtra("NotificationMessageStr"));


			dialogDoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				intent=new Intent(DialogActivityForBooking.this, CommunityBookingActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				finish();
				}
			});

			dialogNoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(DialogActivityForBooking.this, SlideMenuZoom2u.class);
				startActivity(intent);
				finish();
				}
			});

		} catch (Exception e) {
               e.printStackTrace();
		}
	}

}
