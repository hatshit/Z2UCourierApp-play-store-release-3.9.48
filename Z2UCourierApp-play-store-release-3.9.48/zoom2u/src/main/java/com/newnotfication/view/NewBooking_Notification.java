package com.newnotfication.view;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.animations.ExpandCollapseAnimation;
import com.customnotify_event.CustomNotification_View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.newnotfication.view.bookings_view_offer.CounterOffers;
import com.newnotfication.view.bookings_view_offer.ShowOfferForBookings;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dimension.Dimension_class;
import com.zoom2u.polyUtils.PolyUtil;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceForSendLatLong;
import com.zoom2u.services.ServiceToUpdateNewBookingList;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.WorkaroundMapFragment;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewBooking_Notification extends Activity implements View.OnClickListener , AdapterView.OnItemSelectedListener{

	private ScrollView scrollViewNewBD;

	private TextView secondHeaderTxtBD, vehicelValueTxtBD, distanceValueTxtBD, priceValueTxtBD, pickUpTimeValueTxtBD, deliveryTimeValueTxtBD,
			pickUpContactNameTxtBD, pickUpSuburbTxtBD, dropOffContactNameTxtBD, dropOffSuburbTxtBD, documentTxtBD;
	private TextView  dimentionDetailsTxtBD;
	private boolean isBookingRejected;

	TextView txtBanner;
	private ProgressDialog progressForBookingAction;

	private double dropoffLatitude, dropoffLongitude, pickUpLatitude, pickUpLongitude;

	private All_Bookings_DataModels newBookingNotificationModel;

	private Dialog dialogInNOotificationUI;

	LinearLayout txtWithSuggestPriceBtnLayout;

	private TextView suggestPriceTxt, priceTxtForGSTInfo;
	//*********** Suggest price view ********
	LinearLayout priceSubmitView;
	TextView suggestPriceHeaderTxtBD;
	EditText edtSuggestPriceTxt;
	Button cancelSuggestPriceBtnBD;
	Button submitSuggestPriceBtnBD;

	String courierSuggestedPrice;
    Spinner bidActivePeriodSpinnerToPlaceBid;
	private int bidActivePeriodInterval = 15;
	/*************  Save activity state when process killed in background*************/
	private LinearLayout ll_booking_due_day;
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		try {
			outState.putBoolean("SetRoutific", WebserviceHandler.isRoutific);
			outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
			outState.putParcelable("NewBookingNotificationModel", newBookingNotificationModel);
			outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
			//		outState.putParcelableArrayList("NewBookingArray", BookingView.bookingListArray);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	/*************  Restore activity state when resuming app after process killed in background*************/
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view contents
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	synchronized protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		finish();
		/*Intent intent1=new Intent(this,NewBooking_Notification.class);
		startActivity(intent1);*/
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
		if (getResources().getConfiguration().screenWidthDp<=360){
			setContentView(R.layout.new_booking_detail_view_notifi_small);
		}else {
			setContentView(R.layout.new_booking_detail_view_notifi);
		}
		RelativeLayout headerSummaryReportLayout=findViewById(R.id.headerLayoutAllTitleBar);
		Window window = NewBooking_Notification.this.getWindow();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}


		if (MainActivity.isIsBackGroundGray()) {
			window.setStatusBarColor(Color.parseColor("#374350"));
			headerSummaryReportLayout.setBackgroundResource(R.color.base_color_gray);
		} else {
			window.setStatusBarColor(Color.parseColor("#00A6E2"));
			headerSummaryReportLayout.setBackgroundResource(R.color.base_color1);
		}

		PushReceiver.IsOtherScreenOpen =true;
		inItNewBookingDetailViewContents(savedInstanceState);        //***** Initialize new booking detail view contents
	}

	//******** Initialize new booking notification view contents ***************
	private void inItNewBookingDetailViewContents(Bundle savedInstanceState) {
		try {
			if (LoginZoomToU.NOVA_BOLD == null)
				LoginZoomToU.staticFieldInit(NewBooking_Notification.this);
			LoginZoomToU.notificUINewBookingVisibleCount = 1;
			if (savedInstanceState != null) {
				try {
					WebserviceHandler.isRoutific = savedInstanceState.getBoolean("SetRoutific");
					ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
					newBookingNotificationModel = savedInstanceState.getParcelable("NewBookingNotificationModel");
					BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
					BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("NewBookingArray");
					Intent serviceIntent = new Intent(this, ServiceForSendLatLong.class);
					startService(serviceIntent);
					serviceIntent = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}


		findViewById(R.id.backFromBookingDetail).setOnClickListener(this);



		if (scrollViewNewBD == null)
			scrollViewNewBD = (ScrollView) findViewById(R.id.scrollViewNewBD);

		if (secondHeaderTxtBD == null)
			secondHeaderTxtBD = (TextView) findViewById(R.id.secondHeaderTxtBD);

		if (txtBanner == null)
			txtBanner = (TextView) findViewById(R.id.txtBanner);

		if (ll_booking_due_day==null)
			ll_booking_due_day=findViewById(R.id.ll_booking_due_day);

		if (priceValueTxtBD == null)
			priceValueTxtBD = (TextView) findViewById(R.id.priceValueTxtBD);

		findViewById(R.id.newCustomerTxtInBD);

		findViewById(R.id.newBDDistanceView).findViewById(R.id.verticleTxt1BD);
		((TextView) findViewById(R.id.newBDDistanceView).findViewById(R.id.verticleTxt1BD)).setText("VEHICLE");
		if (vehicelValueTxtBD == null)
			vehicelValueTxtBD = (TextView) findViewById(R.id.newBDDistanceView).findViewById(R.id.verticleTxt2BD);


		View newBDValueView = findViewById(R.id.newBDValueView);
		newBDValueView.findViewById(R.id.verticleTxt1BD);
		((TextView) newBDValueView.findViewById(R.id.verticleTxt1BD)).setText("DISTANCE");
		if (distanceValueTxtBD == null)
			distanceValueTxtBD = (TextView) newBDValueView.findViewById(R.id.verticleTxt2BD);

		View newBDPickupView = findViewById(R.id.newBDPickupView);
		newBDPickupView.findViewById(R.id.verticleTxt1BD);
		((TextView) newBDPickupView.findViewById(R.id.verticleTxt1BD)).setText("PICKUP");
		if (pickUpTimeValueTxtBD == null)
			pickUpTimeValueTxtBD = (TextView) findViewById(R.id.newBDPickupView).findViewById(R.id.verticleTxt2BD);


		findViewById(R.id.newBDDeliveryView).findViewById(R.id.verticleTxt1BD);
		((TextView) findViewById(R.id.newBDDeliveryView).findViewById(R.id.verticleTxt1BD)).setText("DELIVER BY");
		if (deliveryTimeValueTxtBD == null)
			deliveryTimeValueTxtBD = (TextView) findViewById(R.id.newBDDeliveryView).findViewById(R.id.verticleTxt2BD);


		findViewById(R.id.pickAddressViewBD).findViewById(R.id.addressHeaderTxtBD);
		((TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.addressHeaderTxtBD)).setText("Pickup");
		if (pickUpContactNameTxtBD == null)
			pickUpContactNameTxtBD = (TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.contactNameTxtBD);


		if (pickUpSuburbTxtBD == null)
			pickUpSuburbTxtBD = (TextView) findViewById(R.id.pickAddressViewBD).findViewById(R.id.suburbTxtBD);


		findViewById(R.id.dropAddressViewBD).findViewById(R.id.addressHeaderTxtBD);
		((TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.addressHeaderTxtBD)).setText("Drop");
		if (dropOffContactNameTxtBD == null)
			dropOffContactNameTxtBD = (TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.contactNameTxtBD);


		if (dropOffSuburbTxtBD == null)
			dropOffSuburbTxtBD = (TextView) findViewById(R.id.dropAddressViewBD).findViewById(R.id.suburbTxtBD);


		priceTxtForGSTInfo = (TextView) findViewById(R.id.priceTxtForGSTInfo);

		priceTxtForGSTInfo.setOnClickListener(this);

		findViewById(R.id.rejectBtnBD).setOnClickListener(this);
		findViewById(R.id.acceptBtnBD).setOnClickListener(this);

		suggestPriceTxt = (TextView) findViewById(R.id.suggestPriceTxt);

		suggestPriceTxt.setOnClickListener(this);

		txtWithSuggestPriceBtnLayout = (LinearLayout) findViewById(R.id.txtWithSuggestPriceBtnLayout);

		//*********** ************* Courier suggest price work 12-july-2018 *************** ***********
		priceSubmitView = (LinearLayout) findViewById(R.id.priceSubmitView);
		suggestPriceHeaderTxtBD = (TextView) findViewById(R.id.suggestPriceHeaderTxtBD);
		edtSuggestPriceTxt = (EditText) findViewById(R.id.edtSuggestPriceTxt);

		cancelSuggestPriceBtnBD = (Button) findViewById(R.id.cancelSuggestPriceBtnBD);

		cancelSuggestPriceBtnBD.setOnClickListener(this);
		submitSuggestPriceBtnBD = (Button) findViewById(R.id.submitSuggestPriceBtnBD);

		submitSuggestPriceBtnBD.setOnClickListener(this);
		findViewById(R.id.view_offer).setOnClickListener(this);
		bidActivePeriodSpinnerToPlaceBid= (Spinner)findViewById(R.id.bidActivePeriodSpinnerToPlaceBid);
		String[] pkgPosition = {"15 mins", "30 mins", "45 mins", "1 hour", "1 hour 15 mins", "1 hour 30 mins", "1 hour 45 mins", "2 hours", "4 hours", "8 hours", "12 hours", "24 hours"};
		ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(this, R.layout.spinneritemxml1, pkgPosition) {
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTextColor(Color.parseColor("#45515b"));
				return v;
			}
			public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
				View v =super.getDropDownView(position, convertView, parent);

				((TextView) v).setTextColor(Color.parseColor("#45515b"));
				return v;
			}
		};


		bidActivePeriodSpinnerToPlaceBid.setAdapter(adapter_Position);
		bidActivePeriodSpinnerToPlaceBid.setSelection(1);
		bidActivePeriodSpinnerToPlaceBid.setOnItemSelectedListener(this);




		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		edtSuggestPriceTxt.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (start < 0)
					edtSuggestPriceTxt.setTextSize(14f);
				else
					edtSuggestPriceTxt.setTextSize(24f);
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0)
					edtSuggestPriceTxt.setTextSize(14f);
			}
		});

		edtSuggestPriceTxt.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				scrollViewNewBD.postDelayed(new Runnable() {
					@Override
					public void run() {
						scrollViewNewBD.fullScroll(ScrollView.FOCUS_DOWN);
					}
				}, 500);
				return false;
			}
		});
		//*********** Courier suggest price work 12-july-2018 ***********

		initView();
	}

	//******** Initialize new notification view map ***************
	private void inItNewNotificationViewMapFragment() {
		try {
			if (BookingDetail_New.mapFragmentBookingDetail != null)
				BookingDetail_New.mapFragmentBookingDetail = null;
			BookingDetail_New.mapFragmentBookingDetail = ((WorkaroundMapFragment) getFragmentManager().findFragmentById(R.id.mapViewBookingDetail));
			BookingDetail_New.mapFragmentBookingDetail.setMapReadyCallback(googleMap -> {
				BookingDetail_New.gMapBookingDetail = BookingDetail_New.mapFragmentBookingDetail.getMap();
				BookingDetail_New.gMapBookingDetail.setMapType(GoogleMap.MAP_TYPE_NORMAL);
				//BookingDetail_New.gMapBookingDetail.setMyLocationEnabled(true);
				BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomControlsEnabled(true);
				BookingDetail_New.gMapBookingDetail.getUiSettings().setMyLocationButtonEnabled(true);
				BookingDetail_New.gMapBookingDetail.getUiSettings().setCompassEnabled(false);
				BookingDetail_New.gMapBookingDetail.getUiSettings().setRotateGesturesEnabled(true);
				BookingDetail_New.gMapBookingDetail.getUiSettings().setZoomGesturesEnabled(true);

				// Stop scrolling of layout View when map scroll
				BookingDetail_New.mapFragmentBookingDetail.setListener(new WorkaroundMapFragment.OnTouchListener() {
					@Override
					public void onTouch() {
						scrollViewNewBD.requestDisallowInterceptTouchEvent(true);
					}
				});
				try{
					BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
							.position(new LatLng(pickUpLatitude, pickUpLongitude))
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup_pin_new)));
				}catch(Exception e){
					e.printStackTrace();
				}

				try{
					BookingDetail_New.gMapBookingDetail.addMarker(new MarkerOptions()
							.position(new LatLng(dropoffLatitude, dropoffLongitude))
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.dropoff_pin_new)));
				}catch(Exception e){
					e.printStackTrace();
				}

				try {
					BookingDetail_New.gMapBookingDetail.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
						@Override
						public void onMapLoaded() {
							try {
								LatLngBounds bounds = new LatLngBounds.Builder()
										.include(new LatLng(pickUpLatitude, pickUpLongitude))
										.include(new LatLng(dropoffLatitude, dropoffLongitude))
										.build();
								BookingDetail_New.gMapBookingDetail.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
								bounds = null;
							} catch (Exception e) {
								e.printStackTrace();
								BookingDetail_New.gMapBookingDetail.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-25.274398, 133.775136), 8));
							}
						}
					});

//			new ShowPolyline_DirectionAPI(NewBooking_Notification.this, BookingDetail_New.gMapBookingDetail,
//                    new LatLng(pickUpLatitude, pickUpLongitude), new LatLng(dropoffLatitude, dropoffLongitude));

					List<LatLng> list = null;
					try {
						list = PolyUtil.decode(newBookingNotificationModel.getRoutePolyline());
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (list != null)
						BookingDetail_New.drawRoute(list);

				} catch (Exception e) {
					e.printStackTrace();
				}

			});


		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	/*
	************** Set new notification view contents to fields **********
	 */
	private void setNewNotificationViewContents(){
		try {


			suggestPriceTxt.setVisibility(View.VISIBLE);

			if (!newBookingNotificationModel.getBookingRefNo().equals("") && !newBookingNotificationModel.getBookingRefNo().equals("null")) {
				((TextView) findViewById(R.id.titleBookingDetail1)).setText("#" + newBookingNotificationModel.getBookingRefNo());

				//********** To Show Test booking alert *************
				String firstCharOfReferenceNo = newBookingNotificationModel.getBookingRefNo().substring(0, 1);
				if (firstCharOfReferenceNo.equalsIgnoreCase("T"))
					showTestBookingAlert(NewBooking_Notification.this);
			} else
				((TextView) findViewById(R.id.titleBookingDetail)).setText("New booking");
				//((TextView) findViewById(R.id.titleBookingDetail1)).setText("New booking");

			inItNewNotificationViewMapFragment();          //******** Initialize new booking detail view map

			//   ***********  Change color for next day booking  **************

			try {
				if (Functional_Utility.checkDateIsToday(newBookingNotificationModel.getPickupDateTime())==false) {
					ll_booking_due_day.setBackgroundResource(R.drawable.new_green_round);
					priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.green_bg));
					secondHeaderTxtBD.setText("ⓘ This delivery is for future date");
				} else
				{
					ll_booking_due_day.setBackgroundResource(R.drawable.new_white_back_blue);
					priceValueTxtBD.setTextColor(ContextCompat.getColor(this,R.color.base_color1));
					secondHeaderTxtBD.setText("ⓘ This delivery is due today");
				}

			}catch (Exception ex){
			}


			//   **************** ----------------- *****************

			vehicelValueTxtBD.setText(newBookingNotificationModel.getVehicle().toUpperCase());

			if(newBookingNotificationModel.isNewCustomer())
                findViewById(R.id.newCustomerTxtInBD).setVisibility(View.VISIBLE);
            else
                findViewById(R.id.newCustomerTxtInBD).setVisibility(View.GONE);

			try
			{
				if(newBookingNotificationModel.IsAutoReturn()) {
					txtBanner =findViewById(R.id.txtBanner);
					txtBanner.setVisibility(View.VISIBLE);
				} else {
					txtBanner =findViewById(R.id.txtBanner);
					txtBanner.setVisibility(View.GONE);
				}

			}catch (Exception e)
			{
				e.printStackTrace();
			}
			distanceValueTxtBD.setText(newBookingNotificationModel.getDistance()+"");

		//	int priceInt = LoginZoomToU.checkInternetwithfunctionality.round((Double)newBookingNotificationModel.getPrice());
			priceValueTxtBD.setText("$"+newBookingNotificationModel.getPrice());

			Functional_Utility.setDateTimeFromServerToPerticularField(pickUpTimeValueTxtBD, newBookingNotificationModel.getPickupDateTime(), false);

			Functional_Utility.setDateTimeFromServerToPerticularField(deliveryTimeValueTxtBD, newBookingNotificationModel.getDropDateTime(), false);

			pickUpContactNameTxtBD.setText(""+newBookingNotificationModel.getPick_ContactName());

			pickUpSuburbTxtBD.setText(""+newBookingNotificationModel.getPick_Suburb());

			dropOffContactNameTxtBD.setText(""+newBookingNotificationModel.getDrop_ContactName());

			dropOffSuburbTxtBD.setText(""+newBookingNotificationModel.getDrop_Suburb());

			LinearLayout rootView = findViewById(R.id.dimension_view);
			try {
				if (newBookingNotificationModel.getShipmentsArray()!=null&&newBookingNotificationModel.getShipmentsArray().size() > 0) {
					ArrayList<HashMap<String, Object>> shipmentArray = newBookingNotificationModel.getShipmentsArray();
					new Dimension_class().setTheViewsForDimensions(rootView,this,shipmentArray);

				}
			} catch (Exception e) {
				e.printStackTrace();

			}
		} catch (Exception e) {
			e.printStackTrace();
			finish();
			//dialogViewForNewNotification("Error!", "Something went wrong here."+e.getMessage(), false);
		}
	}


	@Override
	public void onBackPressed(){
//		super.onBackPressed();
//		finish();
	}

	// ******* Dismiss new notification view ***********
	private void backFromBookingDetailView(){
		refreshNewBookingJob();
		finish();
		LoginZoomToU.notificUINewBookingVisibleCount = 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.view_offer:
				ViewBookingOffer();
				break;

			case R.id.backFromBookingDetail:
				LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
				backFromBookingDetailView();
				break;
			case R.id.rejectBtnBD:
				rejectNewBooking();
				break;
			case R.id.acceptBtnBD:
				acceptNewBooking();
				break;
			case R.id.suggestPriceTxt:
				findViewById(R.id.button).setVisibility(View.GONE);
				ExpandCollapseAnimation.expand(priceSubmitView);
                ExpandCollapseAnimation.collapse(txtWithSuggestPriceBtnLayout);
                edtSuggestPriceTxt.requestFocus();
               // LoginZoomToU.imm.showSoftInput(edtSuggestPriceTxt, InputMethodManager.SHOW_IMPLICIT);
                scrollViewNewBD.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollViewNewBD.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                }, 500);
                break;
            case R.id.cancelSuggestPriceBtnBD:
				findViewById(R.id.button).setVisibility(View.VISIBLE);
                edtSuggestPriceTxt.setText("");
                courierSuggestedPrice = "";
                ExpandCollapseAnimation.expand(txtWithSuggestPriceBtnLayout);
                ExpandCollapseAnimation.collapse(priceSubmitView);
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
                break;
            case R.id.submitSuggestPriceBtnBD:
                courierSuggestedPrice = edtSuggestPriceTxt.getText().toString();
                if (!courierSuggestedPrice.equals("")) {
                    LoginZoomToU.imm.hideSoftInputFromWindow(edtSuggestPriceTxt.getWindowToken(), 0);
					submitCouriersSuggestPrice();
                } else
                    Toast.makeText(NewBooking_Notification.this, "Please enter your price", Toast.LENGTH_LONG).show();
                break;
			case R.id.priceTxtForGSTInfo:
				dialogViewForGSTInfo();
				break;
		}
	}

	private void acceptNewBooking() {
		try {
			if(LoginZoomToU.prefrenceForLogout.getBoolean("isLogout", false) == false){
				isBookingRejected = false;
				callServiceForAcceptBooking();
			}else
				dialogViewForNewNotification("Login first!", "Please login first for accept booking.", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ViewBookingOffer(){
		final String[] responseForAcceptBook = {"0"};
		final JSONObject[] jObjAcceptResponse = new JSONObject[1];

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				inItProgressInNewNotificationView();

			}

			@Override
			public void doInBackground() {
				try {

					WebserviceHandler webServiceHandler = new WebserviceHandler();
					responseForAcceptBook[0] =webServiceHandler.getViewOffer(newBookingNotificationModel.getBookingId());
					jObjAcceptResponse[0] = new JSONObject(responseForAcceptBook[0]);
				} catch (Exception e) {
					e.printStackTrace();

				}
			}

			@Override
			public void onPostExecute() {
				try {
					if (!responseForAcceptBook[0].equals("0")) {
						if (jObjAcceptResponse[0].getBoolean("success")) {
							if(jObjAcceptResponse[0].getString("counterOffers")!=null){
								Type listType = new TypeToken<List<CounterOffers>>() {}.getType();
								List<CounterOffers> data = new Gson().fromJson(jObjAcceptResponse[0].getString("counterOffers"), listType);
								if(data.size()>0)
								ShowOfferForBookings.alertDialogToFinishView(NewBooking_Notification.this,data);
							    else
							    Toast.makeText(NewBooking_Notification.this,"No offers available",Toast.LENGTH_LONG).show();
							}
						} else {
							DialogActivity.alertDialogView(NewBooking_Notification.this, "Information!", jObjAcceptResponse[0].getString("message"));
						}
					} else
						DialogActivity.alertDialogView(NewBooking_Notification.this, "Error!", "Something went wrong, Please try again.");
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(NewBooking_Notification.this, "Server Error!", "Something went wrong, Please try again later.");
				} finally {
					dismissNewBookingProgressDialog();

				}
			}
		}.execute();
	}


	private void rejectNewBooking () {
		try {
			if(LoginZoomToU.prefrenceForLogout.getBoolean("isLogout", false) == false){
				isBookingRejected = true;
				callServiceForAcceptBooking();
			}else
				dialogViewForNewNotification("Login first!", "Please login first for reject booking.", false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
   **********  Call service for accept booking ************
    */
    private void submitCouriersSuggestPrice() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
             SubmitCouriersSuggestPrice();
        else
            DialogActivity.alertDialogView(NewBooking_Notification.this, "No Network!", "No network connection, Please try again later.");
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
		 int intervalMul = position + 1;
		switch (position) {
			case 0: case 1: case 2: case 3: case 4: case 5: case 6:case 7:
				bidActivePeriodInterval = 15 * intervalMul;
				break;
			case 8:
				bidActivePeriodInterval = 4 * 60;
				break;
			case 9:
				bidActivePeriodInterval = 8 * 60;
				break;
			case 10:
				bidActivePeriodInterval = 12 * 60;
				break;
			case 11:
				bidActivePeriodInterval = 24 * 60;
				break;
			default:
				bidActivePeriodInterval = 30;
				break;
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	private void SubmitCouriersSuggestPrice(){

		final String[] responseSubmitCourierSuggestPrice = {"0"};
    	new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				inItProgressInNewNotificationView();
				ActiveBookingView.getCurrentLocation(NewBooking_Notification.this);
			}

			@Override
			public void doInBackground() {
				try {
					//    Functional_Utility.sendLocationToServer();
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					responseSubmitCourierSuggestPrice[0] = webServiceHandler.submitCouriersSuggestPrice(newBookingNotificationModel.getBookingId(), courierSuggestedPrice,bidActivePeriodInterval);
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(LoginZoomToU.isLoginSuccess == 0) {
						edtSuggestPriceTxt.setText("");
						courierSuggestedPrice = "";
						Toast.makeText(NewBooking_Notification.this, "Your suggested price submitted successfully", Toast.LENGTH_LONG).show();
						backFromBookingDetailView();
					} else if(LoginZoomToU.isLoginSuccess == 1) {
						DialogActivity.alertDialogView(NewBooking_Notification.this, "No Network!", "No network connection, Please try again later.");
					} else
						DialogActivity.alertDialogView(NewBooking_Notification.this, "Error!", "Something went wrong, Please try again.");
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(NewBooking_Notification.this, "Server Error!", "Something went wrong, Please try again later."+e.getMessage());
				}finally{
					dismissNewBookingProgressDialog();
				}
			}
		}.execute();
	}



	/*
    **********  Call service for accept or reject booking ************
     */
	private void callServiceForAcceptBooking() {
		if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
			 AcceptBookingForPickupInBDAsyncTask();
		else
			DialogActivity.alertDialogView(NewBooking_Notification.this, "No Network!", "No network connection, Please try again later.");
	}

	private void AcceptBookingForPickupInBDAsyncTask(){
		final String[] responseForAcceptBook = {"0"};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				inItProgressInNewNotificationView();
				ActiveBookingView.getCurrentLocation(NewBooking_Notification.this);
			}

			@Override
			public void doInBackground() {
				try {
					//    Functional_Utility.sendLocationToServer();
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if(isBookingRejected == false)
						responseForAcceptBook[0] = webServiceHandler.acceptBookingForPickup(newBookingNotificationModel.getBookingId(), "");
					else
						responseForAcceptBook[0] = webServiceHandler.rejectBookingForPickup(newBookingNotificationModel.getBookingId());
				}catch(Exception e){
					e.printStackTrace();
					responseForAcceptBook[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(!responseForAcceptBook.equals("0")){
						JSONObject jObjAcceptResponse  = new JSONObject(responseForAcceptBook[0]);
						if(jObjAcceptResponse.getBoolean("success") == true) {
							if(isBookingRejected == false) {
								WebserviceHandler.NEWBOOKING_COUNT--;
								callDeliveryCount(3);
								new LoadChatBookingArray(NewBooking_Notification.this, 0);
								Toast.makeText(NewBooking_Notification.this, newBookingNotificationModel.getPick_Suburb()+" to "+newBookingNotificationModel.getDrop_Suburb()+" accepted", Toast.LENGTH_LONG).show();
								finishNewBookingNotificationView(true);
								try {
									if (jObjAcceptResponse.getJSONObject("notification") != null){
										Intent customNotificationIntent = new Intent(NewBooking_Notification.this, CustomNotification_View.class);
										customNotificationIntent.putExtra("CustomNotifiedData", jObjAcceptResponse.getJSONObject("notification").toString());
										customNotificationIntent.putExtra("BookingID", jObjAcceptResponse.getInt("bookingId"));
										startActivity(customNotificationIntent);
										customNotificationIntent = null;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else {
								WebserviceHandler.NEWBOOKING_COUNT--;
								callDeliveryCount(4);
								isBookingRejected = false;
								Toast.makeText(NewBooking_Notification.this, "Booking rejected", Toast.LENGTH_LONG).show();
								finishNewBookingNotificationView(false);
							}
						}else
							dialogViewForNewNotification("Information!", jObjAcceptResponse.getString("message"), false);
					}else
						dialogViewForNewNotification("Error!", "Something went wrong, Please try again.", false);
				} catch (Exception e) {
					e.printStackTrace();
					dialogViewForNewNotification("Server Error!", "Something went wrong, Please try again later."+e.getMessage(), false);
				}finally{
					dismissNewBookingProgressDialog();
				}
			}
		}.execute();
	}



	private void dialogViewForNewNotification(String titleStr, String msgStr, final boolean isBookingAccepted){

		try{
			if(dialogInNOotificationUI != null)
				if (dialogInNOotificationUI.isShowing())
					dialogInNOotificationUI.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(dialogInNOotificationUI != null)
			dialogInNOotificationUI = null;
			dialogInNOotificationUI = new Dialog(NewBooking_Notification.this);
			dialogInNOotificationUI.setCancelable(true);
			dialogInNOotificationUI.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialogInNOotificationUI.setContentView(R.layout.dialogview);

			Window window = dialogInNOotificationUI.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) dialogInNOotificationUI.findViewById(R.id.titleDialog);

			enterFieldDialogHEader.setText(titleStr);

			TextView enterFieldDialogMsg = (TextView) dialogInNOotificationUI.findViewById(R.id.dialogMessageText);

			enterFieldDialogMsg.setText(msgStr);

			Button enterFieldDialogDoneBtn = (Button) dialogInNOotificationUI.findViewById(R.id.dialogDoneBtn);
			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					dialogInNOotificationUI.dismiss();
					finishNewBookingNotificationView(isBookingAccepted);

				}
			});
			dialogInNOotificationUI.show();
	}

	//********** Dialog for GST info ***************
	private void dialogViewForGSTInfo(){

		try{
			if(dialogInNOotificationUI != null)
				if (dialogInNOotificationUI.isShowing())
					dialogInNOotificationUI.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(dialogInNOotificationUI != null)
			dialogInNOotificationUI = null;
		dialogInNOotificationUI = new Dialog(NewBooking_Notification.this);
		dialogInNOotificationUI.setCancelable(true);
        dialogInNOotificationUI.getWindow().setBackgroundDrawableResource(R.color.transparent_black_bg);
		dialogInNOotificationUI.setContentView(R.layout.offerscreen_charge_n_fees);

		Window window = dialogInNOotificationUI.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		android.view.WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		TextView chargeHeaderTxtOffer = (TextView) dialogInNOotificationUI.findViewById(R.id.chargeHeaderTxtOffer);

		TextView customerPriceTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.customerPriceTxt);

		TextView priceTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.priceTxt);

		TextView incGSTTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.incGSTTxt);

		TextView gstTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.gstTxt);

		TextView gstValueTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.gstValueTxt);

		TextView zoom2uFeeTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.zoom2uFeeTxt);

		TextView zoom2uFeeValueTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.zoom2uFeeValueTxt);

		TextView bookingFeeTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.bookingFeeTxt);

		TextView bookingFeeValueTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.bookingFeeValueTxt);

		TextView carrierChargeTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.carrierChargeTxt);

		TextView carrierChargeValueTxt = (TextView) dialogInNOotificationUI.findViewById(R.id.carrierChargeValueTxt);



		Button rejectBtn_GST_InfoDialog = (Button) dialogInNOotificationUI.findViewById(R.id.rejectBtn_GST_InfoDialog);

		rejectBtn_GST_InfoDialog.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogInNOotificationUI.dismiss();
			}
		});

		dialogInNOotificationUI.show();
	}

	//************ Finish Notification view after accept or reject booking
	private void finishNewBookingNotificationView(boolean isBookingAccepted) {
		backFromBookingDetailView();
		if (isBookingAccepted && ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 2) {
			Intent intent1 = new Intent(NewBooking_Notification.this, ServiceToUpdate_ActiveBookingList.class);
			startService(intent1);
			intent1 = null;
		}
	}

	synchronized void initView(){
		try{
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				 GetBookingDetailData();
			else
				DialogActivity.alertDialogView(NewBooking_Notification.this, "No network!", "No network connection, Please try again later.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
    **********  Service call for delivery count on accept and reject booking ***************
     */
	private void callDeliveryCount(int addOrDeduct) {
		Intent bookingCountService = new Intent(NewBooking_Notification.this, ServiceForCourierBookingCount.class);
		bookingCountService.putExtra("Is_API_Call_Require", addOrDeduct);
		startService(bookingCountService);
		bookingCountService = null;

	}

	/*
    ***************  For New booking notification ****************
     */
	private void refreshNewBookingJob() {
		if(ConfirmPickUpForUserName.isDropOffSuccessfull == 11 && BookingView.bookingViewSelection == 1) {
			Intent intent1 = new Intent(NewBooking_Notification.this, ServiceToUpdateNewBookingList.class);
			startService(intent1);
			intent1 = null;
		}
	}

	private void dismissNewBookingProgressDialog() {
		try {
			if(progressForBookingAction != null)
				if(progressForBookingAction.isShowing())
					Custom_ProgressDialogBar.dismissProgressBar(progressForBookingAction);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void inItProgressInNewNotificationView() {
		if(progressForBookingAction != null)
			progressForBookingAction = null;
		progressForBookingAction = new ProgressDialog(NewBooking_Notification.this);
		Custom_ProgressDialogBar.inItProgressBar(progressForBookingAction);
	}

	private void GetBookingDetailData(){
		final String[] bookingDetailStr = {"0"};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				dismissNewBookingProgressDialog();
				inItProgressInNewNotificationView();
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();// 57489
					JSONObject jObjOfGetBookingDetail = null;
					if (!PushReceiver.prefrenceForPushy.getString("bookingId", "0").equals("0")) {
						bookingDetailStr[0] = webServiceHandler.getBookingDetailByID(Integer.parseInt(PushReceiver.prefrenceForPushy.getString("bookingId", "0")));
						try {
							jObjOfGetBookingDetail = new JSONArray(bookingDetailStr[0]).getJSONObject(0);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							pickUpLatitude = Double.parseDouble(jObjOfGetBookingDetail.getString("PickupGPSX"));
							pickUpLongitude = Double.parseDouble(jObjOfGetBookingDetail.getString("PickupGPSY"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							dropoffLatitude = Double.parseDouble(jObjOfGetBookingDetail.getString("DropGPSX"));
							dropoffLongitude = Double.parseDouble(jObjOfGetBookingDetail.getString("DropGPSY"));
						} catch (Exception e) {
							e.printStackTrace();
						}

						String distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
								(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
										String.valueOf(pickUpLatitude),
										String.valueOf(pickUpLongitude));

						String orderNumberForMenuLog = "";
						try {
							if (!jObjOfGetBookingDetail.getString("OrderNo").equals("null") || jObjOfGetBookingDetail.getString("OrderNo") != null)
								orderNumberForMenuLog = jObjOfGetBookingDetail.getString("OrderNo");
							else
								orderNumberForMenuLog = "";
						} catch (Exception e) {
							orderNumberForMenuLog = "";
						}

						newBookingNotificationModel = new All_Bookings_DataModels();
						newBookingNotificationModel.setBookingId(jObjOfGetBookingDetail.getInt("BookingId"));
						try {
							newBookingNotificationModel.setBookingRefNo(jObjOfGetBookingDetail.getString("BookingRefNo"));
						}catch (Exception e){

						}
						newBookingNotificationModel.setCustomerId(jObjOfGetBookingDetail.getString("CustomerId"));

						ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
						for (int k = 0; k < jObjOfGetBookingDetail.getJSONArray("Shipments").length(); k++) {
							HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
							JSONObject jObjOfShipmentItem = jObjOfGetBookingDetail.getJSONArray("Shipments").getJSONObject(k);
							objOFShipments.put("Category", jObjOfShipmentItem.getString("Category"));
							objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("Quantity"));
							try {
								objOFShipments.put("LengthCm", jObjOfShipmentItem.getInt("LengthCm"));
								objOFShipments.put("WidthCm", jObjOfShipmentItem.getInt("WidthCm"));
								objOFShipments.put("HeightCm", jObjOfShipmentItem.getInt("HeightCm"));
							} catch (JSONException e) {
								e.printStackTrace();
								objOFShipments.put("LengthCm", 0);
								objOFShipments.put("WidthCm", 0);
								objOFShipments.put("HeightCm", 0);
							}
							try {
								objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("ItemWeightKg"));
								objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("TotalWeightKg"));
							} catch (JSONException e) {
								e.printStackTrace();
								objOFShipments.put("ItemWeightKg", 0);
								objOFShipments.put("TotalWeightKg", 0);
							}
							arrayOfShipments.add(objOFShipments);
							objOFShipments = null;
						}
						newBookingNotificationModel.setShipmentsArray(arrayOfShipments);
						newBookingNotificationModel.setNewCustomer(jObjOfGetBookingDetail.getBoolean("isNewCustomer"));
						newBookingNotificationModel.setPickupDateTime(jObjOfGetBookingDetail.getString("PickupDateTime"));
						newBookingNotificationModel.setPick_Address(jObjOfGetBookingDetail.getString("PickupAddress"));
						newBookingNotificationModel.setPick_ContactName(jObjOfGetBookingDetail.getString("PickupContactName"));
						newBookingNotificationModel.setPick_Notes(jObjOfGetBookingDetail.getString("PickupNotes"));
						newBookingNotificationModel.setPick_Phone(jObjOfGetBookingDetail.getString("PickupPhone"));
						newBookingNotificationModel.setPick_Suburb(jObjOfGetBookingDetail.getString("PickupSuburb"));
						newBookingNotificationModel.setDropDateTime(jObjOfGetBookingDetail.getString("DropDateTime"));
						newBookingNotificationModel.setDrop_Address(jObjOfGetBookingDetail.getString("DropAddress"));
						newBookingNotificationModel.setDrop_ContactName(jObjOfGetBookingDetail.getString("DropContactName"));
						newBookingNotificationModel.setDrop_Notes(jObjOfGetBookingDetail.getString("DropNotes"));
						newBookingNotificationModel.setDrop_Phone(jObjOfGetBookingDetail.getString("DropPhone"));
						newBookingNotificationModel.setDrop_Suburb(jObjOfGetBookingDetail.getString("DropSuburb"));
						newBookingNotificationModel.setCreatedDateTime(jObjOfGetBookingDetail.getString("CreatedDateTime"));
						newBookingNotificationModel.setDeliverySpeed(jObjOfGetBookingDetail.getString("DeliverySpeed"));
						newBookingNotificationModel.setDistance(jObjOfGetBookingDetail.getString("Distance"));
						newBookingNotificationModel.setNotes(jObjOfGetBookingDetail.getString("Notes"));
						newBookingNotificationModel.setVehicle(jObjOfGetBookingDetail.getString("Vehicle"));
						newBookingNotificationModel.setSource(jObjOfGetBookingDetail.getString("Source"));
						newBookingNotificationModel.setPackage(jObjOfGetBookingDetail.getString("PackageType"));

						try {
							newBookingNotificationModel.setRoutePolyline(jObjOfGetBookingDetail.getString("RoutePolyline"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						//autoreturn case for banner
						try {
							if(jObjOfGetBookingDetail.has("IsAutoReturn")) {
								Boolean isAutoreturn= jObjOfGetBookingDetail.getBoolean("IsAutoReturn");
								if(isAutoreturn!=null) {
									newBookingNotificationModel.setAutoReturn(jObjOfGetBookingDetail.getBoolean("IsAutoReturn"));
								}else {
									newBookingNotificationModel.setAutoReturn(false);
								}
							}
						}catch (Exception e) {
							e.printStackTrace();
						}

						/*ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
						for (int k = 0; k < jObjOfGetBookingDetail.getJSONArray("Shipments").length(); k++) {
							HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
							objOFShipments.put("Category", jObjOfGetBookingDetail.getJSONArray("Shipments").getJSONObject(k).getString("Category"));
							objOFShipments.put("Quantity", jObjOfGetBookingDetail.getJSONArray("Shipments").getJSONObject(k).getInt("Quantity"));
							arrayOfShipments.add(objOFShipments);
							objOFShipments = null;
						}
						newBookingNotificationModel.setShipmentsArray(arrayOfShipments);
						arrayOfShipments = null;*/
						newBookingNotificationModel.setPrice(jObjOfGetBookingDetail.getDouble("CourierPrice"));
						newBookingNotificationModel.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
						newBookingNotificationModel.setOrderNumber(orderNumberForMenuLog);

						//	newBookingNotificationModel.setPricingBreakdown_model(jObjOfGetBookingDetail.getJSONObject("PricingBreakdowns"));

						jObjOfGetBookingDetail = null;
					}
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					bookingDetailStr[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				dismissNewBookingProgressDialog();
				setNewNotificationViewContents();
			}
		}.execute();
	}


	//**************** Alert for test booking *************
	public static Dialog testBookingDialog;
	public static void showTestBookingAlert(Context currentViewContext){
		try{
			if (testBookingDialog != null)
				if (testBookingDialog.isShowing())
					testBookingDialog.dismiss();
		} catch (Exception e){
			e.printStackTrace();
		}

		if(testBookingDialog != null)
			testBookingDialog = null;
		testBookingDialog = new Dialog(currentViewContext);
		testBookingDialog.setCancelable(true);
		testBookingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		testBookingDialog.setContentView(R.layout.dialogview);

		Window window = testBookingDialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		android.view.WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		TextView enterFieldDialogHEader = (TextView) testBookingDialog.findViewById(R.id.titleDialog);

		SpannableString content = new SpannableString("Test Booking");
		content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		enterFieldDialogHEader.setText(content);

		TextView enterFieldDialogMsg = (TextView) testBookingDialog.findViewById(R.id.dialogMessageText);

		enterFieldDialogMsg.setText("This is a test booking to show you how the app works.\nPlease Accept and complete the booking to be eligible for real work.\nNote: You do not need to visit the location in the test booking.");

		Button enterFieldDialogDoneBtn = (Button) testBookingDialog.findViewById(R.id.dialogDoneBtn);
		enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				testBookingDialog.dismiss();
			}
		});

		testBookingDialog.show();
	}
}
