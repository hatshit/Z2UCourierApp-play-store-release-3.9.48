package com.z2u.booking.vc;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.customnotify_event.CustomNotification_View;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.booking.vc.endlesslistview.NewBooking_EndlessListView;
import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceToUpdateNewBookingList;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.userlatlong.GPSTracker;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewBookingView implements NewBooking_EndlessListView.EndLessListener{

	Context newBookingContext;
	ProgressDialog progressDialogToLoadNewBookings;
	private TextView noBookingAvailable, etaMsgTextBG, onLineOffLineBtnForNewBooking;
	private ImageView selectPickUpEtaNewBookingList;
	NewBooking_EndlessListView newBookingListView;
	Dialog etaDialogInNewBookingList;

	static int countGPSAlert = 0;

	public BroadcastReceiver receiverForRefreshNewBookingList;

	public NewBookingListAdapter newBookingListAdapter;

	boolean isSwipeLeft_NewBookingList = false;
	boolean isSwipeRight_NewBookingList = false;
	boolean isRejectBooking_NewBookingList = false;

	private final int INVALID_POS_NEWBOOKINGLIST = -1;
	protected int DELETE_POS_NEWBOOKINGLIST = -1;
	protected int ADD_POS_NEWBOOKINGLIST = -1;

//	public Intent refreshNewBookingServiceIntent;

	int selectedItemInNewBookingList, currentScrollIndex;
	String uploadDateBookingStr;
	TimePickerFragment timeFragment;
	TextView online_offline_color;
	LinearLayout onLineOffLineBtnForNewBooking_ll;
	SwipeRefreshLayout swipeRefreshLayoutNew;
	RelativeLayout count_rl;
	TextView count;
//	ArrayList<DHL_SectionInterface> newBookingArray;

	public NewBookingView(Context newBookingContext, TextView noBookingAvailable,
						  NewBooking_EndlessListView newBookingListView, TextView onLineOffLineBtnForNewBooking,
						  LinearLayout onLineOffLineBtnForNewBooking_ll, SwipeRefreshLayout swipeRefreshLayoutNew,TextView online_offline_color,
						  RelativeLayout count_rl,TextView count) {
        this.noBookingAvailable = noBookingAvailable;
		this.newBookingListView = newBookingListView;
		this.newBookingListView.setListener(this);
		this.onLineOffLineBtnForNewBooking_ll=onLineOffLineBtnForNewBooking_ll;
		this.onLineOffLineBtnForNewBooking = onLineOffLineBtnForNewBooking;
		this.newBookingContext = newBookingContext;
		this.swipeRefreshLayoutNew=swipeRefreshLayoutNew;
        this.online_offline_color=online_offline_color;
        this.count_rl=count_rl;
        this.count=count;
        swipeRefreshLayoutNew.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				callNewBookingApi();
				swipeRefreshLayoutNew.setRefreshing(false);
			}
		});
	    callNewBookingApi();
	    //**********  Call local broadcast on silent notification for calculated ETA  ********//
		receiverForRefreshNewBookingList = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		       try{
		    	   if(intent.getStringExtra("result") != null){
					   if (BookingView.bookingViewSelection == 1) {
						   inItNewBookingArray();
						   updateNewBookingListItem(intent.getStringExtra("result"));
					   }
		    	   }
		        }catch(Exception e){
		        	e.printStackTrace();
		        }
		    }
		};

		//*************  Register local broadcast receiver   ****************//
		if(receiverForRefreshNewBookingList != null)
			LocalBroadcastManager.getInstance(newBookingContext).registerReceiver((receiverForRefreshNewBookingList), new IntentFilter(ServiceToUpdateNewBookingList.SILENT_NOTIFICATION_NEW_JOB));

		callTimerNewBookingList();
	}

	private boolean checkPermissions() {
		return ActivityCompat.checkSelfPermission(newBookingContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(newBookingContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
	}

	public void callNewBookingApi(){
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
			GPSTracker getGpsLocationLatLong = new GPSTracker(newBookingContext);
			LocationManager manager = (LocationManager) newBookingContext.getSystemService(Context.LOCATION_SERVICE);
			boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			if (!statusOfGPS && checkPermissions())
				getGpsLocationLatLong.showSettingsAlert();


			getGpsLocationLatLong = null;
			CompletedView.endlessCount = 0;
			inItNewBookingArray();          // init New booking array according to load more
			callNewBookingAsyncTask();
		}else
			DialogActivity.alertDialogView(newBookingContext, "No Network !", "No Network connection, Please try again later.");

	 }

	//***********  Init New booking array according to load more  *************
	public void inItNewBookingArray() {
		if (BookingView.bookingListArray != null){
			if (BookingView.bookingListArray.size() > 0)
				BookingView.bookingListArray.clear();
		}else
			BookingView.bookingListArray = new ArrayList<DHL_SectionInterface>();

//		if (newBookingArray != null){
//			if (newBookingArray.size() > 0)
//				newBookingArray.clear();
//		}else
//			newBookingArray = new ArrayList<DHL_SectionInterface>();
	}

	// ************  Start timer for New booking list *********
	void callTimerNewBookingList(){
		if(BookingView.handlerForTimeCounter != null)
			BookingView.handlerForTimeCounter = null;
		BookingView.handlerForTimeCounter = new Handler();
		BookingView.handlerForTimeCounter.postDelayed(MenuLogTimerForNewList, 60*1000 );
	}

	Runnable MenuLogTimerForNewList = new Runnable() {
		@Override
		public void run() {
			if(newBookingListAdapter != null)
				newBookingListAdapter.notifyDataSetChanged();
			BookingView.handlerForTimeCounter.postDelayed(this, 60*1000 );
		}
	};

	public void removeMenulogHandlerForNewBookingList(){
		if(BookingView.handlerForTimeCounter != null && MenuLogTimerForNewList != null)
			BookingView.handlerForTimeCounter.removeCallbacks(MenuLogTimerForNewList);
	}

	// In-it New booking progress dialog to show
	public void inItNewBookingProgressDialog() {
		if(progressDialogToLoadNewBookings != null)
			progressDialogToLoadNewBookings = null;
		progressDialogToLoadNewBookings = new ProgressDialog(newBookingContext);
		Custom_ProgressDialogBar.inItProgressBar(progressDialogToLoadNewBookings);
	}

	// Dismiss New booking progress dialog
	public void dismissNewBookingProgressDialog() {
		if(progressDialogToLoadNewBookings != null)
			if(progressDialogToLoadNewBookings.isShowing())
				Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadNewBookings);
	}

	@Override
	public void loadData() {
//		try {
//			CompletedView.endlessCount++;
//			callNewBookingAsyncTask();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	private void GetItemsNewBookinList(){
		final String[] responseForGetCourierOnline = {"0"};
		final String[] webServiceResponseOfNewBookingItems = {"0"};
		new MyAsyncTasks() {
			@Override
			public void onPreExecute() {
				inItNewBookingProgressDialog();
			}
			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					try {
						webServiceResponseOfNewBookingItems[0] = webServiceHandler.getNewBookingList(CompletedView.endlessCount);
					} catch (Exception e) {
						e.printStackTrace();
						webServiceResponseOfNewBookingItems[0] = "0";
					}
					responseForGetCourierOnline[0] = webServiceHandler.serviceForGetCourierOnlineOffline();
					JSONObject responseJOBJ = new JSONObject(responseForGetCourierOnline[0]);
					if(responseJOBJ.getBoolean("success")){
						BookingView.isCourierOnline = responseJOBJ.getBoolean("isOnline");
					}
					responseJOBJ = null;
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					responseForGetCourierOnline[0] = "0";
				}
			}
			@Override
			public void onPostExecute() {
				try {
					if(BookingView.isCourierOnline == true){
						onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg);
						onLineOffLineBtnForNewBooking.setText("Online");
						online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton_green);
					}else{
						onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg_gray);
						onLineOffLineBtnForNewBooking.setText("Offline");
						online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton);
					}

					updateNewBookingListItem(webServiceResponseOfNewBookingItems[0]);
					webServiceResponseOfNewBookingItems[0] = "0";
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(newBookingContext, "Error !", "Please try later !");
				}finally{
					dismissNewBookingProgressDialog();
				}
			}
		}.execute();
	}


	/***************** Update new booking list item *************/
	synchronized void updateNewBookingListItem(String newBookingItemResponse){
		try {
			JSONObject newBookingResponseJObj = new JSONObject(newBookingItemResponse);
			if (newBookingResponseJObj.getBoolean("success")) {
				JSONArray newBookingResponseArray = newBookingResponseJObj.getJSONArray("data");
				List<DHL_SectionInterface> tempNewBookingList = new ArrayList<DHL_SectionInterface>();
				for (int i = 0; i < newBookingResponseArray.length(); i++) {
					JSONObject responseDataObject = newBookingResponseArray.getJSONObject(i);

					String distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
							(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
									responseDataObject.getString("PickupGPSX"),
									responseDataObject.getString("PickupGPSY"));

					String orderNumberForMenuLog = "";
					try {
						if (!responseDataObject.getString("OrderNo").equals("null") || responseDataObject.getString("OrderNo") != null)
							orderNumberForMenuLog = responseDataObject.getString("OrderNo");
						else
							orderNumberForMenuLog = "";
					} catch (Exception e) {
						e.printStackTrace();
						orderNumberForMenuLog = "";
					}

					All_Bookings_DataModels dataModel_AllBookingList = new All_Bookings_DataModels();
					dataModel_AllBookingList.setBookingId(responseDataObject.getInt("BookingId"));
					dataModel_AllBookingList.setBookingRefNo(responseDataObject.getString("BookingRefNo"));
					dataModel_AllBookingList.setCustomerId(responseDataObject.getString("CustomerId"));
					dataModel_AllBookingList.setNewCustomer(responseDataObject.getBoolean("isNewCustomer"));
					dataModel_AllBookingList.setPickupDateTime(responseDataObject.getString("PickupDateTime"));
					dataModel_AllBookingList.setPick_Address(responseDataObject.getString("PickupAddress"));
					dataModel_AllBookingList.setPick_ContactName(responseDataObject.getString("PickupContactName"));
					dataModel_AllBookingList.setPick_GPSX(responseDataObject.getString("PickupGPSX"));
					dataModel_AllBookingList.setPick_GPSY(responseDataObject.getString("PickupGPSY"));
					dataModel_AllBookingList.setPick_Notes(responseDataObject.getString("PickupNotes"));
					dataModel_AllBookingList.setPick_Phone(responseDataObject.getString("PickupPhone"));
					dataModel_AllBookingList.setPick_Suburb(responseDataObject.getString("PickupSuburb"));
					dataModel_AllBookingList.setDropDateTime(responseDataObject.getString("DropDateTime"));
					dataModel_AllBookingList.setDrop_Address(responseDataObject.getString("DropAddress"));
					dataModel_AllBookingList.setDrop_ContactName(responseDataObject.getString("DropContactName"));
					dataModel_AllBookingList.setDrop_GPSX(responseDataObject.getString("DropGPSX"));
					dataModel_AllBookingList.setDrop_GPSY(responseDataObject.getString("DropGPSY"));
					dataModel_AllBookingList.setDrop_Notes(responseDataObject.getString("DropNotes"));
					dataModel_AllBookingList.setDrop_Phone(responseDataObject.getString("DropPhone"));
					dataModel_AllBookingList.setDrop_Suburb(responseDataObject.getString("DropSuburb"));
					dataModel_AllBookingList.setCreatedDateTime(responseDataObject.getString("CreatedDateTime"));
					dataModel_AllBookingList.setDeliverySpeed(responseDataObject.getString("DeliverySpeed"));
					dataModel_AllBookingList.setDistance(responseDataObject.getString("Distance"));
					dataModel_AllBookingList.setNotes(responseDataObject.getString("Notes"));
					dataModel_AllBookingList.setVehicle(responseDataObject.getString("Vehicle"));
					dataModel_AllBookingList.setSource(responseDataObject.getString("Source"));
					dataModel_AllBookingList.setPackage(responseDataObject.getString("PackageType"));

					try {
						dataModel_AllBookingList.setRoutePolyline(responseDataObject.getString("RoutePolyline"));
					} catch (JSONException e) {
                        e.printStackTrace();
                    }


					ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
					for (int k = 0; k < responseDataObject.getJSONArray("Shipments").length(); k++) {
						HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
						JSONObject jObjOfShipmentItem = responseDataObject.getJSONArray("Shipments").getJSONObject(k);
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
					dataModel_AllBookingList.setShipmentsArray(arrayOfShipments);
					arrayOfShipments = null;
					dataModel_AllBookingList.setPrice(responseDataObject.getDouble("CourierPrice"));
					dataModel_AllBookingList.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
					dataModel_AllBookingList.setOrderNumber(orderNumberForMenuLog);

				//	dataModel_AllBookingList.setPricingBreakdown_model(responseDataObject.getJSONObject("PricingBreakdowns"));

					tempNewBookingList.add(dataModel_AllBookingList);
					dataModel_AllBookingList = null;
					distanceFromCurrentLoc = null;
					responseDataObject = null;
				}

//				for (DHL_SectionInterface dataModelArray : tempNewBookingList){
//					newBookingArray.add(dataModelArray);
//				}



				Log.e("", "****************  New booking array size   == " + tempNewBookingList.size());
				List<DHL_SectionInterface> sortNewBookingArrayByCreatedDateTime = new ArrayList<DHL_SectionInterface>();
				List<DHL_SectionInterface> sortNewBookingArrayByVIP;
				List<DHL_SectionInterface> sortNewBookingArrayBy3Hr;
				List<DHL_SectionInterface> sortNewBookingArrayBySameDay;
				List<DHL_SectionInterface> sortNewBookingArrayRemain = new ArrayList<DHL_SectionInterface>();

				for (int j = 0; j < tempNewBookingList.size(); j++) {
					try {
						Collections.sort(tempNewBookingList, new Comparator<DHL_SectionInterface>() {
							@Override
							public int compare(DHL_SectionInterface lhs, DHL_SectionInterface rhs) {
								return ((All_Bookings_DataModels)lhs).getCreatedDateTime().compareTo(((All_Bookings_DataModels)rhs).getCreatedDateTime());
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
					sortNewBookingArrayByCreatedDateTime.add(tempNewBookingList.get(j));
				}

				sortNewBookingArrayByVIP = sortedArray("VIP", sortNewBookingArrayByCreatedDateTime);
				sortNewBookingArrayBy3Hr = sortedArray("3 hour", sortNewBookingArrayByCreatedDateTime);
				sortNewBookingArrayBySameDay = sortedArray("Same day", sortNewBookingArrayByCreatedDateTime);

				for (DHL_SectionInterface newBookingArray : sortNewBookingArrayByCreatedDateTime) {
					if (!((All_Bookings_DataModels)newBookingArray).getDeliverySpeed().equals("VIP") && !((All_Bookings_DataModels)newBookingArray).getDeliverySpeed().equals("3 hour")
							&& !((All_Bookings_DataModels)newBookingArray).getDeliverySpeed().equals("Same day"))
						sortNewBookingArrayRemain.add(newBookingArray);
				}

				if (BookingView.bookingListArray != null){
					if (BookingView.bookingListArray.size() > 0)
						BookingView.bookingListArray.clear();
				}else
					BookingView.bookingListArray = new ArrayList<DHL_SectionInterface>();

				sortNewBookingArrayByCreatedDateTime = null;
				finalSortedArray(sortNewBookingArrayByVIP);
				sortNewBookingArrayByVIP = null;
				finalSortedArray(sortNewBookingArrayBy3Hr);
				sortNewBookingArrayBy3Hr = null;
				finalSortedArray(sortNewBookingArrayBySameDay);
				sortNewBookingArrayBySameDay = null;
				finalSortedArray(sortNewBookingArrayRemain);
				sortNewBookingArrayRemain = null;
				Log.e("", "****************  Main array size == " + BookingView.bookingListArray.size());
				count_rl.setVisibility(View.VISIBLE);
				count.setText(newBookingResponseJObj.getJSONArray("data").length()+"");
				newBookingResponseArray = null;

				if (CompletedView.endlessCount == 0) {
					if (BookingView.bookingListArray.size() > 0) {
						noBookingAvailable.setVisibility(View.GONE);
						newBookingListView.setVisibility(View.VISIBLE);
						newBookingListAdapter = new NewBookingListAdapter(newBookingContext, R.layout.bookinglist_item1);
						newBookingListView.setNewBookingAdapter(newBookingListAdapter);
					} else {
						noBookingAvailable.setText("New bookings not available");
						noBookingAvailable.setVisibility(View.VISIBLE);
						newBookingListView.setVisibility(View.GONE);
					}
				}else
					newBookingListView.addNewData(tempNewBookingList);

				tempNewBookingList = null;
			}
	       }catch (Exception e) {
		        e.printStackTrace();
		   }catch (OutOfMemoryError e){
	            e.printStackTrace();
	       }
	}

    /********* Final Sorted array by VIP, 3 hour  or by Same day ***********/
    void finalSortedArray(List<DHL_SectionInterface> sortedArrayByCreatedDateTime){
        if(sortedArrayByCreatedDateTime.size() > 0) {
            for (DHL_SectionInterface newBookingArray : sortedArrayByCreatedDateTime)
                BookingView.bookingListArray.add(newBookingArray);
        }
    }

    /********* Sort array by VIP, 3 hour  or by Same day ***********/
    ArrayList<DHL_SectionInterface> sortedArray(String filteredString, List<DHL_SectionInterface> sortedArrayByCreatedDateTime){
        ArrayList<DHL_SectionInterface> sortArrayOfNewBooking = new ArrayList<DHL_SectionInterface>();
        for (DHL_SectionInterface newBookingArray : sortedArrayByCreatedDateTime) {
            if(((All_Bookings_DataModels)newBookingArray).getDeliverySpeed().equals(filteredString))
                sortArrayOfNewBooking.add(newBookingArray);
        }
        return  sortArrayOfNewBooking;
    }

    private void AcceptBookingForPickupAsyncTask(){
		final String[] responseForAcceptBook = {"0"};
		final JSONObject[] jObjAcceptResponse = new JSONObject[1];

    	new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				inItNewBookingProgressDialog();
				ActiveBookingView.getCurrentLocation(newBookingContext);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					// Functional_Utility.sendLocationToServer();
					if(isRejectBooking_NewBookingList == false){
						if(WebserviceHandler.isRoutific == true)
							responseForAcceptBook[0] = webServiceHandler.acceptBookingForPickup(((All_Bookings_DataModels)BookingView.bookingListArray.get(selectedItemInNewBookingList)).getBookingId(), "");
						else
							responseForAcceptBook[0] = webServiceHandler.acceptBookingForPickup(((All_Bookings_DataModels)BookingView.bookingListArray.get(selectedItemInNewBookingList)).getBookingId(),
									uploadDateBookingStr);
					}else{
						responseForAcceptBook[0] = webServiceHandler.rejectBookingForPickup(((All_Bookings_DataModels)BookingView.bookingListArray.get(selectedItemInNewBookingList)).getBookingId());
					}
					jObjAcceptResponse[0] = new JSONObject(responseForAcceptBook[0]);
				}catch(Exception e){
					e.printStackTrace();
					responseForAcceptBook[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				DELETE_POS_NEWBOOKINGLIST = -1;
				ADD_POS_NEWBOOKINGLIST = -1;
				try{
					dismissNewBookingProgressDialog();
				}catch(Exception e){
					e.printStackTrace();
				}
				try {
					if(!responseForAcceptBook[0].equals("0")){
						if (isRejectBooking_NewBookingList == false) {
							if (jObjAcceptResponse[0].getBoolean("success") == true) {
								callDeliveryCount(3);
								new LoadChatBookingArray(newBookingContext, 0);
								BookingView.bookingViewSelection = 2;
								ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
								Toast.makeText(newBookingContext, ((All_Bookings_DataModels)BookingView.bookingListArray.get(selectedItemInNewBookingList)).getPick_Suburb()+" to "+((All_Bookings_DataModels)BookingView.bookingListArray.get(selectedItemInNewBookingList)).getDrop_Suburb()+" accepted", Toast.LENGTH_LONG).show();
								Intent callCompleteBookingfragment = new Intent(newBookingContext, SlideMenuZoom2u.class);
								newBookingContext.startActivity(callCompleteBookingfragment);
								try {
									if (jObjAcceptResponse[0].getJSONObject("notification") != null){
										Intent customNotificationIntent = new Intent(newBookingContext, CustomNotification_View.class);
										customNotificationIntent.putExtra("CustomNotifiedData", jObjAcceptResponse[0].getJSONObject("notification").toString());
										customNotificationIntent.putExtra("BookingID", jObjAcceptResponse[0].getInt("bookingId"));
										newBookingContext.startActivity(customNotificationIntent);
										customNotificationIntent = null;
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

								((Activity) newBookingContext).finish();
								callCompleteBookingfragment = null;
							} else {
								responseAlert("Information!", jObjAcceptResponse[0].getString("message"));
							}
						} else {
							isRejectBooking_NewBookingList = false;
							if(jObjAcceptResponse[0].getBoolean("success") == true) {
								CompletedView.endlessCount = 0;
								clearNewBookingSubArray();
								callDeliveryCount(4);
								callNewBookingAsyncTask();

								Toast.makeText(newBookingContext, "Booking rejected", Toast.LENGTH_LONG).show();
							} else
								responseAlert("Information!", jObjAcceptResponse[0].getString("message"));
						}
					}else
						DialogActivity.alertDialogView(newBookingContext, "Error!", "Something went wront, please try again.");

					jObjAcceptResponse[0] = null;
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(newBookingContext, "Server error!", "Something went wront, please try later !");
				}
			}
		}.execute();
	}

	private void clearNewBookingSubArray() {
//		if (newBookingArray != null)
//			if (newBookingArray.size() > 0)
//				newBookingArray.clear();
	}

	public void callDeliveryCount(int addOrDeductToActiveCount) {
        Intent bookingCountService = new Intent(newBookingContext, ServiceForCourierBookingCount.class);
		bookingCountService.putExtra("Is_API_Call_Require", addOrDeductToActiveCount);
        newBookingContext.startService(bookingCountService);
        bookingCountService = null;
    }

	public void callNewBookingAsyncTask(){
	/*	GetItemsNewBookinList getItemsNewBookinList = new GetItemsNewBookinList();
		getItemsNewBookinList.execute();
		getItemsNewBookinList = null;*/
		GetItemsNewBookinList();
    }

	void responseAlert(String alrtTitle, String alrtMsg){
		if(DialogActivity.enterFieldDialog != null)
			DialogActivity.enterFieldDialog = null;
			DialogActivity.enterFieldDialog = new Dialog(newBookingContext);
			DialogActivity.enterFieldDialog.setCancelable(false);
			DialogActivity.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			DialogActivity.enterFieldDialog.setContentView(R.layout.dialogview);

			Window window = DialogActivity.enterFieldDialog.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.titleDialog);
			enterFieldDialogHEader.setText(alrtTitle);

			TextView enterFieldDialogMsg = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.dialogMessageText);
			enterFieldDialogMsg.setText(alrtMsg);

			Button enterFieldDialogDoneBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.dialogDoneBtn);
			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					DialogActivity.enterFieldDialog.dismiss();
					CompletedView.endlessCount = 0;
					clearNewBookingSubArray();
					callNewBookingAsyncTask();
				}
			});
			DialogActivity.enterFieldDialog.show();
	}

	void callAcceptOrRejectBookingAsyncTask(){
		/*AcceptBookingForPickupAsyncTask acceptBookingForPickupAsyncTask = new AcceptBookingForPickupAsyncTask();
		acceptBookingForPickupAsyncTask.execute();
		acceptBookingForPickupAsyncTask = null;*/
		AcceptBookingForPickupAsyncTask();
	}

	// New booking list adapter
		public class NewBookingListAdapter extends ArrayAdapter<DHL_SectionInterface> {

		int resourceId;

		private int[] colors;
		private int[] colorsNextDay = new int[] { Color.parseColor("#80deff"), Color.parseColor("#31c9ff") };

		public NewBookingListAdapter(Context newBookingContext, int resourceId) {
			super(newBookingContext, resourceId);
			this.resourceId = resourceId;
			if(BookingView.bookingListArray.size()%2 != 0)
				colors = new int[] {0xF0F0F0F3, 0xFFFFFFFF};
			else
				colors = new int[] {0xFFFFFFFF, 0xF0F0F0F3};
		}

		public void onSwipeItemRight(boolean isLeft, int position) {

			if(isSwipeRight_NewBookingList){
				isSwipeRight_NewBookingList = false;
				ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
				DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			}else if(isSwipeLeft_NewBookingList){
				isSwipeLeft_NewBookingList = false;
				if (isLeft == false) {
					ADD_POS_NEWBOOKINGLIST = position;
					DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
				}else if (ADD_POS_NEWBOOKINGLIST == position)
					ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			}else{
				isSwipeRight_NewBookingList = false;
				isSwipeLeft_NewBookingList = true;
				ADD_POS_NEWBOOKINGLIST = position;
				DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			}
			notifyDataSetChanged();
		}


		public void onSwipeItemLeft(boolean isRight, int position) {
			if(isSwipeLeft_NewBookingList){
				isSwipeLeft_NewBookingList = false;
				ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
				DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			}else if(isSwipeRight_NewBookingList){
				isSwipeRight_NewBookingList = false;
				if (isRight == false) {
					DELETE_POS_NEWBOOKINGLIST = position;
					ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
				}else if (DELETE_POS_NEWBOOKINGLIST == position)
					DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			}else{
				isSwipeLeft_NewBookingList = false;
				isSwipeRight_NewBookingList = true;
				ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
				DELETE_POS_NEWBOOKINGLIST = position;
			}
				notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if(BookingView.bookingListArray!=null)
				return BookingView.bookingListArray.size();
			else
				return 0;
		}

		@Override
		public DHL_SectionInterface getItem(int position) {
			return BookingView.bookingListArray.get(position);
		}

		@Override
		public int getViewTypeCount() {
			return getCount();
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint({ "NewApi", "RtlHardcoded" })
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			try {
				if (convertView == null)
					convertView = LayoutInflater.from(newBookingContext).inflate(R.layout.demo_newbookinglist, null);

				RelativeLayout rLayout = ViewHolderPattern.get(convertView, R.id.rrLayout);

				View line_blank=ViewHolderPattern.get(convertView, R.id.line_blank);
				//RelativeLayout frontBackNewBooking = ViewHolderPattern.get(convertView, R.id.frontBackNewBooking);

				if(position==getCount()-1)
					line_blank.setVisibility(View.VISIBLE);
				else
					line_blank.setVisibility(View.GONE);

				ImageView imglocationMark=ViewHolderPattern.get(convertView,R.id.imglocationMark);
				imglocationMark.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							String address=((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address();
							address = address.replace(' ', '+');
							Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
							newBookingContext.startActivity(geoIntent);    // Initiate lookup
						} catch (Exception e) {
							e.printStackTrace();
							/*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
									Uri.parse("google.navigation:q="));*/
						}
					}
				});



				TextView newCustomerTxtInNBL = ViewHolderPattern.get(convertView, R.id.newCustomerTxtInNBL);
				if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).isNewCustomer())
					newCustomerTxtInNBL.setVisibility(View.VISIBLE);
				else
					newCustomerTxtInNBL.setVisibility(View.GONE);

				TextView userName = ViewHolderPattern.get(convertView, R.id.userNameBList);
				TextView bookingChrges = ViewHolderPattern.get(convertView, R.id.chargesBList);
				TextView weight = ViewHolderPattern.get(convertView, R.id.weight);
				TextView textPickupBList = ViewHolderPattern.get(convertView, R.id.textPickupBList);
				TextView textDropoffBList = ViewHolderPattern.get(convertView, R.id.textDropoffBList);


				TextView orderNumberNBL = ViewHolderPattern.get(convertView, R.id.orderNumberNBL);

				if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getSource().equals("DHL")){
                    orderNumberNBL.setVisibility(View.VISIBLE);
                    try {
                        if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getOrderNumber().equals("")){
                            orderNumberNBL.setText("AWB - "+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getOrderNumber());
                        }else
                            orderNumberNBL.setText("AWB - NA");
                    } catch (Exception e) {
                        e.printStackTrace();
                        orderNumberNBL.setText("AWB - NA");
                    }
                }else
					   orderNumberNBL.setVisibility(View.GONE);


				ReadMoreTextView textDeliveryNotes = ViewHolderPattern.get(convertView, R.id.delivryNotesBookingList);
				textDeliveryNotes.setVisibility(View.VISIBLE);


				try {
					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getNotes().equals(""))
					   textDeliveryNotes.setText(" "+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getNotes());
					else
					   textDeliveryNotes.setText("No Delivery Notes");
				} catch (Exception e) {
					e.printStackTrace();
				}

			    TextView textArrivalTime = ViewHolderPattern.get(convertView, R.id.timeToArriveInBookingList);

			   textArrivalTime.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDeliverySpeed());

			  				ImageView vehical=ViewHolderPattern.get(convertView,R.id.dot_car);
				if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Car"))
					vehical.setImageResource(R.drawable.ic_from_to_car_icon);
				else if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Bike"))
					vehical.setImageResource(R.drawable.ic_bike_normal_new);
				else	if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getVehicle().equals("Van"))
					vehical.setImageResource(R.drawable.ic_van_normal_new);

			   TextView locationMarkBList = ViewHolderPattern.get(convertView, R.id.locationMarkBList);


			   TextView menuLogTimerTxtForNBList = ViewHolderPattern.get(convertView, R.id.menuLogTimerTxtForNBList);

			   menuLogTimerTxtForNBList.setVisibility(View.VISIBLE);
			   try {
				   if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDropDateTime().equals("")){
						String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiffActive(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDropDateTime(), false,menuLogTimerTxtForNBList);
						menuLogTimerTxtForNBList.setText(minInStr);
					}else{
						menuLogTimerTxtForNBList.setText("-NA-");
					}
				} catch (Exception e) {
					menuLogTimerTxtForNBList.setText("-NA-");
				}

			   TextView textBookingTime = ViewHolderPattern.get(convertView, R.id.bookingCreatedTime);


			   String bookingCreateTimeNewBookingDetail = null,bookingCreateDateNewBookingDetail = null;
			    bookingCreateTimeNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPickupDateTime());
				bookingCreateDateNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getDateFromServer(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPickupDateTime());


			   textBookingTime.setText(""+bookingCreateTimeNewBookingDetail +" | "+bookingCreateDateNewBookingDetail);

			   TextView textPackageName = ViewHolderPattern.get(convertView, R.id.suburbTextnewBooking);
			   textPackageName.setText("");
				try{
					if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().size() > 0) {
						for (int i = 0; i < ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().size(); i++){
							if (i < 3){
								if (i == 0)
									textPackageName.append(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Quantity")+")");
								else
									textPackageName.append(", "+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Category")+" ("+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("Quantity")+")");
							}else {
								textPackageName.append("...");
								break;
							}
						}
					}else
						textPackageName.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPackage());
				}catch (Exception e){
					e.printStackTrace();
					textPackageName.append(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPackage());
				}


				if(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getSource().equals("Temando")){

			   	if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address().equals(""))

			   		textPickupBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address());
					else
					   textPickupBList.setText("No Address");


				  if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address().equals(""))
					textDropoffBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address());
				  else
					textDropoffBList.setText("No Address");


				}else{

					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Suburb().equals(""))
					   textPickupBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Suburb());
					else
					   textPickupBList.setText("No suburb");

					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Suburb().equals(""))
						textDropoffBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Suburb());
					else
					   textDropoffBList.setText("No suburb");

					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance().equals(""))
						locationMarkBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistance());
					else
						locationMarkBList.setText("0 km");

				}

			   final ImageView rejectBookingImg = ViewHolderPattern.get(convertView, R.id.rejectImageBList);
				final ImageView acceptBookingImg = ViewHolderPattern.get(convertView, R.id.addImageBList);



				try {
					if(!((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_ContactName().equals("")){
						userName.setText(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_ContactName());
					}else
						userName.setText("Pickup Contact  -  NA");
			   } catch (Exception e) {
					e.printStackTrace();
					userName.setText("Pickup Contact  -  NA");
			   }

				if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_ContactName().equalsIgnoreCase("Telstra"))
					userName.setText("" + ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_ContactName());

//			String priceInt = Functional_Utility.returnCourierPrice((Double)((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPrice());
			bookingChrges.setText("$"+(Double)((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPrice());

				try {
					double totalWeight=0.0;
					if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getShipmentsArray().size() > 0) {
						for (int i = 0; i < ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getShipmentsArray().size(); i++) {
							totalWeight = totalWeight + (double)((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getShipmentsArray().get(i).get("TotalWeightKg");

						}
						weight.setText("Total weight : "+totalWeight+"kg");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				//locationMarkBList.setText(((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDistanceFromCurrentLocation()+" km away");


			//   ***********  Slide view to right or left
			if (DELETE_POS_NEWBOOKINGLIST == position) {
					newBookingListView.setScrollContainer(false);
					acceptBookingImg.setVisibility(View.GONE);
					rejectBookingImg.setVisibility(View.VISIBLE);

					if(LoginZoomToU.isTablet(newBookingContext))
						rLayout.animate().translationX(110).withLayer();
					else if(LoginZoomToU.width <= 320)
						rLayout.animate().translationX(110).withLayer();
					else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540)
						rLayout.animate().translationX(165).withLayer();
					else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720)
						rLayout.animate().translationX(220).withLayer();
					else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080)
						rLayout.animate().translationX(330).withLayer();
					else
						rLayout.animate().translationX(440).withLayer();
						rejectBookingImg.setId(position);

				} else if(ADD_POS_NEWBOOKINGLIST == position){
						newBookingListView.setScrollContainer(false);
						acceptBookingImg.setVisibility(View.VISIBLE);
						rejectBookingImg.setVisibility(View.GONE);

						if(LoginZoomToU.isTablet(newBookingContext))
							rLayout.animate().translationX(-110).withLayer();
						else if(LoginZoomToU.width <= 320)
							rLayout.animate().translationX(-110).withLayer();
						else if(LoginZoomToU.width > 320 && LoginZoomToU.width <= 540)
							rLayout.animate().translationX(-165).withLayer();
						else if(LoginZoomToU.width > 540 && LoginZoomToU.width <= 720)
							rLayout.animate().translationX(-220).withLayer();
						else if(LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080)
							rLayout.animate().translationX(-330).withLayer();
						else
							rLayout.animate().translationX(-440).withLayer();

						acceptBookingImg.setId(position);
				}else{
					newBookingListView.setScrollContainer(true);
					rLayout.animate().translationX(0).withLayer();
					Handler bookingHandler = new Handler();
					bookingHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							acceptBookingImg.setVisibility(View.GONE);
							rejectBookingImg.setVisibility(View.GONE);
						}
					}, 500);
				}




				//   ***********  Tap event for reject booking
			rejectBookingImg.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedItemInNewBookingList = position;
						isRejectBooking_NewBookingList = true;
						deleteItem(position);
					}
				});

			//   ***********  Tap event for accept booking
			acceptBookingImg.setOnClickListener(new View.OnClickListener() {
						@SuppressLint("SimpleDateFormat")
						@Override
						public void onClick(View v) {

							selectedItemInNewBookingList = position;
							if(WebserviceHandler.isRoutific == true){
								if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
									callAcceptOrRejectBookingAsyncTask();
								else
									DialogActivity.alertDialogView(newBookingContext, "No Network !", "No Network connection, Please try again later.");
							}else{
								if(etaDialogInNewBookingList != null)
										etaDialogInNewBookingList = null;
										etaDialogInNewBookingList = new Dialog(newBookingContext);
										etaDialogInNewBookingList.setCancelable(false);
										etaDialogInNewBookingList.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
										etaDialogInNewBookingList.setContentView(R.layout.etadialog);

										Window window = etaDialogInNewBookingList.getWindow();
										window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
										android.view.WindowManager.LayoutParams wlp = window.getAttributes();

										wlp.gravity = Gravity.TOP;
										wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
										window.setAttributes(wlp);
										RelativeLayout etaTimeLayout = (RelativeLayout)etaDialogInNewBookingList.findViewById(R.id.etaTimeLayout);
										TextView etaTitleTextBG = (TextView)etaDialogInNewBookingList.findViewById(R.id.etaTitleTextBG);
										etaMsgTextBG = (TextView)etaDialogInNewBookingList.findViewById(R.id.etaMsgTextBG);


										if(selectPickUpEtaNewBookingList == null)
											selectPickUpEtaNewBookingList = (ImageView)etaDialogInNewBookingList.findViewById(R.id.selectPickUpEta);

										String dateValueBookingStr = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick(""+((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPickupDateTime());

										Calendar c = Calendar.getInstance();
										SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss.SSS");
										dateValueBookingStr = dateValueBookingStr+"T"+sdf1.format(c.getTime());

										SimpleDateFormat sdf;
									    sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");

										 Date convertedDate = new Date();
										    try {
										    	convertedDate = sdf.parse(dateValueBookingStr);
										    	sdf = null;
										    }catch(Exception e){
										    	e.printStackTrace();
										    }

										// Date uploaded to server
										uploadDateBookingStr = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
										convertedDate = null;
										// Date show on device
										dateValueBookingStr = LoginZoomToU.checkInternetwithfunctionality.getPickOrDropDateTimeFromServer(dateValueBookingStr);
										etaMsgTextBG.setText(dateValueBookingStr);

										ImageView  etaCancelBtnBG = (ImageView)etaDialogInNewBookingList.findViewById(R.id.etaCancelBtnBG);
										ImageView etaDoneBtnBG = (ImageView)etaDialogInNewBookingList.findViewById(R.id.etaDoneBtnBG);

										etaTimeLayout.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											selectPickUpEtaNewBookingList.setImageResource(R.drawable.icon_up);
											if(timeFragment != null)
												timeFragment = null;
											timeFragment = new com.z2u.booking.vc.TimePickerFragment();
											((com.z2u.booking.vc.TimePickerFragment) timeFragment).setTxtOnETADialogView(etaMsgTextBG, selectPickUpEtaNewBookingList,
													((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPickupDateTime());
								            timeFragment.show(((FragmentActivity) newBookingContext).getSupportFragmentManager(), "timePicker");
										}
									});

									etaCancelBtnBG.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
											if(timeFragment != null)
												timeFragment = null;
											etaDialogInNewBookingList.dismiss();
											etaDialogInNewBookingList = null;
										}
									});

									etaDoneBtnBG.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										if(timeFragment != null){
											uploadDateBookingStr = timeFragment.uploadTimeStr;
											timeFragment = null;
										}
										deleteItem(selectedItemInNewBookingList);
										etaDialogInNewBookingList.dismiss();
										etaDialogInNewBookingList = null;
										}
									});
									etaDialogInNewBookingList.show();
							}
						}
					});
			} catch (Exception e) {
				e.printStackTrace();
			}
		   return convertView;
		}


		public void deleteItem(int pos){
			DELETE_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			ADD_POS_NEWBOOKINGLIST = INVALID_POS_NEWBOOKINGLIST;
			selectedItemInNewBookingList = pos;
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				callAcceptOrRejectBookingAsyncTask();
			else
				DialogActivity.alertDialogView(newBookingContext, "No Network !", "No Network connection, Please try again later.");
		}
	}

	// Call request for accept or reject booking
	private void callRequestForAcceptOrRejectBooking() {
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
			callAcceptOrRejectBookingAsyncTask();
		else
			DialogActivity.alertDialogView(newBookingContext, "No Network !", "No Network connection, Please try again later.");
	}

	public static class ViewHolderPattern {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
}
