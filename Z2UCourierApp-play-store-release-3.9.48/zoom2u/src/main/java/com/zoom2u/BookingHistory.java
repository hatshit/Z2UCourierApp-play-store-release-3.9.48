package com.zoom2u;

import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.Functional_Utility;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class BookingHistory extends Activity{
	
	ImageView backFromBookingHistory, bookingHistoryChatIcon;
	TextView titleBookingHistory, countChatBookingHistory, pickUpDate, userNamePick, diliverItemName, diliverItemPrize, dropedDate, pickUpLocationTitle, pickUpLocationText,
		pickUpActualDate, dropedLocationTitle, dropedLocationText;
	Window window;
	public static int itemPositionBookingHistory;
    RelativeLayout header;
	@Override
	protected void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		Model_DeliveriesToChat.showExclamationForUnreadChat(countChatBookingHistory);
		SlideMenuZoom2u.countChatBookingView = countChatBookingHistory;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookinghistory);
		header=findViewById(R.id.header);
		window = BookingHistory.this.getWindow();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
		if(MainActivity.isIsBackGroundGray()){
			header.setBackgroundResource(R.color.base_color_gray);
			window.setStatusBarColor(Color.parseColor("#374350"));
		}else{
			header.setBackgroundResource(R.color.base_color1);
			window.setStatusBarColor(Color.parseColor("#00A6E2"));
		}


		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
			StrictMode.setThreadPolicy(policy);
			
			if(savedInstanceState == null){
				Intent i = getIntent();
				itemPositionBookingHistory = i.getIntExtra("positionCompleteBooking", 0);
				i = null;
				inItBookingHistoryUI();			// in-it booking history view
			}else{
				try{
					if(savedInstanceState != null){
						ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
						itemPositionBookingHistory = savedInstanceState.getInt("ItemPositionBookingHistory");
						if(BookingView.bookingListArray != null)
							BookingView.bookingListArray = null;
						BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("CompleteBookingList");
					}

					if(LoginZoomToU.NOVA_BOLD == null)
						LoginZoomToU.staticFieldInit(BookingHistory.this);

					inItBookingHistoryUI();		// in-it booking history view
                    Functional_Utility.removeLocationTimer();
                    Intent serviceIntent = new Intent(BookingHistory.this, ServiceForSendLatLong.class);
                    startService(serviceIntent);
                    serviceIntent = null;
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
		//	SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putInt("ItemPositionBookingHistory", itemPositionBookingHistory);
			outState.putParcelableArrayList("CompleteBookingList", BookingView.bookingListArray);
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
				itemPositionBookingHistory = savedInstanceState.getInt("ItemPositionBookingHistory");
				if(BookingView.bookingListArray != null)
					BookingView.bookingListArray = null;
				BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("CompleteBookingList");
			}

			if(LoginZoomToU.NOVA_BOLD == null)
				LoginZoomToU.staticFieldInit(BookingHistory.this);

			inItBookingHistoryUI();
		}catch (Exception e) {
			e.printStackTrace();
		}
		super.onRestoreInstanceState(savedInstanceState);
	}

	 /******************  Initialize booking history view  **************/
	void inItBookingHistoryUI(){
		try{
			BookingView.bookingViewSelection = 0;

			if(bookingHistoryChatIcon == null)
				bookingHistoryChatIcon = (ImageView) findViewById(R.id.bookingHistoryChatIcon);
			bookingHistoryChatIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent chatViewItent = new Intent(BookingHistory.this, ChatViewBookingScreen.class);
					startActivity(chatViewItent);
					chatViewItent = null;
				}
			});
			if (countChatBookingHistory == null)
				countChatBookingHistory =  (TextView) findViewById(R.id.countChatBookingHistory);

			countChatBookingHistory.setVisibility(View.GONE);
			SlideMenuZoom2u.countChatBookingView = countChatBookingHistory;

			if(backFromBookingHistory == null)
				backFromBookingHistory = (ImageView) findViewById(R.id.backFromBookingHistory);
			/*if(signatureFirst == null)
				signatureFirst = (ImageView) findViewById(R.id.signatureFirst);
			if(signatureSecond == null)
				signatureSecond = (ImageView) findViewById(R.id.signatureSecond);*/
			if(titleBookingHistory == null)
				titleBookingHistory = (TextView) findViewById(R.id.titleBookingHistory);

			if(pickUpDate == null)
				pickUpDate = (TextView) findViewById(R.id.pickUpDate);


				String pickUPDateStr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPickupDateTime());
				pickUpDate.setText("Scheduled pickup date : "+pickUPDateStr);
				pickUPDateStr = null;

			if(pickUpActualDate == null)
				pickUpActualDate = (TextView) findViewById(R.id.pickUpActualDate);

				String pickUpActualDateStr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPickupActual());
				pickUpActualDate.setText("Actual pickup date : "+pickUpActualDateStr);
				pickUpActualDateStr = null;

			if(userNamePick == null)
				userNamePick = (TextView) findViewById(R.id.userNamePick);

				userNamePick.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPickupPerson()+"");
			if(diliverItemName == null)
				diliverItemName = (TextView) findViewById(R.id.diliverItemName);

			diliverItemName.setText("");
			try{
				if (((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().size() > 0) {
					for (int i = 0; i < ((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().size(); i++){
						if (i == 0)
							diliverItemName.append(((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().get(i).get("Quantity")+")");
						else
							diliverItemName.append(", "+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getShipmentsArray().get(i).get("Quantity")+")");
					}
				}else
					diliverItemName.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPackage());
			}catch (Exception e){
				e.printStackTrace();
				diliverItemName.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPackage());
			}

			if(diliverItemPrize == null)
				diliverItemPrize = (TextView) findViewById(R.id.diliverItemPrize);

			String priceInt = Functional_Utility.returnCourierPrice((Double)((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPrice());
			diliverItemPrize.setText("Price : $"+priceInt);
			if(dropedDate == null)
				dropedDate = (TextView) findViewById(R.id.dropedDate);

			String dropOffDate = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getDropActual());
			dropedDate.setText("Drop off on : "+dropOffDate);
			dropOffDate = null;
			if(pickUpLocationTitle == null)
				pickUpLocationTitle = (TextView) findViewById(R.id.pickUpLocationTitle);

			if(pickUpLocationText == null)
				pickUpLocationText = (TextView) findViewById(R.id.pickUpLocationText);

			pickUpLocationText.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getPick_Suburb());
			if(dropedLocationTitle == null)
				dropedLocationTitle = (TextView) findViewById(R.id.dropedLocationTitle);

			if(dropedLocationText == null)
				dropedLocationText = (TextView) findViewById(R.id.dropedLocationText);

			dropedLocationText.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(itemPositionBookingHistory)).getDrop_Suburb());

			backFromBookingHistory.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					BookingView.bookingViewSelection = 3;
					ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
				    finish();
					overridePendingTransition(R.anim.left_in, R.anim.right_out);
				}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	 
	@Override
	public void onBackPressed(){
	     super.onBackPressed();
	     BookingView.bookingViewSelection = 3;
	     ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
	     finish();
		 overridePendingTransition(R.anim.left_in, R.anim.right_out);
	}
}
