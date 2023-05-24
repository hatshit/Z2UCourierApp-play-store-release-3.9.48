package com.z2u.booking.vc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.endlessadapter.EndlessAdapter_DropoffBookings;
import com.zoom2u.endlessadapter.EndlessListView;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;

public class CompletedView implements EndlessListView.EndLessListener{
	
	Context completeBookingContext;
	EndlessListView listPickedUp;
	TextView noCompleteBookingText;
	EndlessAdapter_DropoffBookings adapterPickedUp;
	ProgressDialog progressToLoadDropOffBookings;
	String dropOffResponseStr = "0";
	public static int endlessCount = 0;
	ArrayList<DHL_SectionInterface> tempArrayOfDropOffBookingListArray;

	SwipeRefreshLayout swipeRefreshLayoutCompleted;
	public CompletedView(Context completeBookingContext, EndlessListView listPickedUp, TextView noCompleteBookingText, SwipeRefreshLayout swipeRefreshLayoutCompleted, RelativeLayout count_rl) {

		this.listPickedUp = listPickedUp;
		listPickedUp.setCacheColorHint(Color.parseColor("#ffffff"));
		listPickedUp.setListener(this);
		count_rl.setVisibility(View.GONE);
		this.completeBookingContext = completeBookingContext;
		this.noCompleteBookingText = noCompleteBookingText;
        this.swipeRefreshLayoutCompleted=swipeRefreshLayoutCompleted;
		PushReceiver.isCameraOpen = false;
		if(BookingView.bookingListArray != null)
			BookingView.bookingListArray.clear();
		endlessCount = 0;
		callCompletedBookingListItems();
		swipeRefreshLayoutCompleted.setVisibility(View.VISIBLE);
		swipeRefreshLayoutCompleted.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				BookingView.bookingListArray.clear();
				callCompletedBookingListItems();
				swipeRefreshLayoutCompleted.setRefreshing(false);
			}
		});
	}

	//******************  Call AsyncTask to load Completed booking list item **********
	void callCompletedBookingListItems(){
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){		
			/*GetDropOffBookinListData getDropOffBookinListData = new GetDropOffBookinListData();
			getDropOffBookinListData.execute();
			getDropOffBookinListData = null;*/
			GetDropOffBookinListData();
		}else
			DialogActivity.alertDialogView(completeBookingContext, "No Network !", "No Network connection, Please try again later.");
	}
	private void GetDropOffBookinListData(){
		final JSONArray[] responseArrayOfDropOffData = new JSONArray[1];

		new MyAsyncTasks() {
			@Override
			public void onPreExecute() {
				try{
					if(progressToLoadDropOffBookings != null)
						if(progressToLoadDropOffBookings.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressToLoadDropOffBookings);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(progressToLoadDropOffBookings != null)
					progressToLoadDropOffBookings = null;
				progressToLoadDropOffBookings = new ProgressDialog(completeBookingContext);
				Custom_ProgressDialogBar.inItProgressBar(progressToLoadDropOffBookings);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					dropOffResponseStr = webServiceHandler.getDropOffBookingListFromServer(endlessCount);
					webServiceHandler = null;
					try {
						tempArrayOfDropOffBookingListArray = new ArrayList<DHL_SectionInterface>();
						JSONObject completeBookingResponseObj = new JSONObject(dropOffResponseStr);
						if (completeBookingResponseObj.getBoolean("success")){
							responseArrayOfDropOffData[0] = completeBookingResponseObj.getJSONArray("data");
							JSONObject mainResponseDropOffJOBJ = null;

							for (int i = 0; i < responseArrayOfDropOffData[0].length(); i++) {
								mainResponseDropOffJOBJ = responseArrayOfDropOffData[0].getJSONObject(i);

								String distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
										(LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
												mainResponseDropOffJOBJ.getString("PickupGPSX"), mainResponseDropOffJOBJ.getString("PickupGPSY"));

								String orderNumberForMenuLog = "";
								try {
									orderNumberForMenuLog = mainResponseDropOffJOBJ.getString("OrderNo");
								} catch (Exception e) {
									orderNumberForMenuLog = "";
								}

								All_Bookings_DataModels dataModel_AllBookingList = new All_Bookings_DataModels();
								dataModel_AllBookingList.setBookingId(mainResponseDropOffJOBJ.getInt("BookingId"));
								dataModel_AllBookingList.setCustomerId(mainResponseDropOffJOBJ.getString("CustomerId"));
								dataModel_AllBookingList.setNewCustomer(mainResponseDropOffJOBJ.getBoolean("isNewCustomer"));
								dataModel_AllBookingList.setPickupDateTime(mainResponseDropOffJOBJ.getString("PickupDateTime"));
								dataModel_AllBookingList.setPick_Address(mainResponseDropOffJOBJ.getString("PickupAddress"));
								dataModel_AllBookingList.setPick_ContactName(mainResponseDropOffJOBJ.getString("PickupContactName"));
								dataModel_AllBookingList.setPick_GPSX(mainResponseDropOffJOBJ.getString("PickupGPSX"));
								dataModel_AllBookingList.setPick_GPSY(mainResponseDropOffJOBJ.getString("PickupGPSY"));
								dataModel_AllBookingList.setPick_Notes(mainResponseDropOffJOBJ.getString("PickupNotes"));
								dataModel_AllBookingList.setPick_Phone(mainResponseDropOffJOBJ.getString("PickupPhone"));
								dataModel_AllBookingList.setPick_Suburb(mainResponseDropOffJOBJ.getString("PickupSuburb"));
								dataModel_AllBookingList.setDropDateTime(mainResponseDropOffJOBJ.getString("DropDateTime"));
								dataModel_AllBookingList.setDrop_Address(mainResponseDropOffJOBJ.getString("DropAddress"));
								dataModel_AllBookingList.setDrop_ContactName(mainResponseDropOffJOBJ.getString("DropContactName"));
								dataModel_AllBookingList.setDrop_GPSX(mainResponseDropOffJOBJ.getString("DropGPSX"));
								dataModel_AllBookingList.setDrop_GPSY(mainResponseDropOffJOBJ.getString("DropGPSY"));
								dataModel_AllBookingList.setDrop_Notes(mainResponseDropOffJOBJ.getString("DropNotes"));
								dataModel_AllBookingList.setDrop_Phone(mainResponseDropOffJOBJ.getString("DropPhone"));
								dataModel_AllBookingList.setDrop_Suburb(mainResponseDropOffJOBJ.getString("DropSuburb"));
								dataModel_AllBookingList.setCreatedDateTime(mainResponseDropOffJOBJ.getString("CreatedDateTime"));
								dataModel_AllBookingList.setDeliverySpeed(mainResponseDropOffJOBJ.getString("DeliverySpeed"));
								dataModel_AllBookingList.setDistance(mainResponseDropOffJOBJ.getString("Distance"));
								dataModel_AllBookingList.setNotes(mainResponseDropOffJOBJ.getString("Notes"));
								dataModel_AllBookingList.setVehicle(mainResponseDropOffJOBJ.getString("Vehicle"));
								dataModel_AllBookingList.setSource(mainResponseDropOffJOBJ.getString("Source"));
								dataModel_AllBookingList.setPackage(mainResponseDropOffJOBJ.getString("PackageType"));
								dataModel_AllBookingList.setStatus(mainResponseDropOffJOBJ.getString("Status"));
								dataModel_AllBookingList.setPickupETA(mainResponseDropOffJOBJ.getString("PickupETA"));
								dataModel_AllBookingList.setDropETA(mainResponseDropOffJOBJ.getString("DropETA"));
								dataModel_AllBookingList.setPickupActual(mainResponseDropOffJOBJ.getString("PickupActual"));
								dataModel_AllBookingList.setDropActual(mainResponseDropOffJOBJ.getString("DropActual"));
								dataModel_AllBookingList.setPickupPerson(mainResponseDropOffJOBJ.getString("PickupPerson"));
								dataModel_AllBookingList.setDropPerson(mainResponseDropOffJOBJ.getString("DropPerson"));
								dataModel_AllBookingList.setDropSignature(mainResponseDropOffJOBJ.getString("DropSignature"));
								dataModel_AllBookingList.setPickupSignature(mainResponseDropOffJOBJ.getString("PickupSignature"));
								dataModel_AllBookingList.setPrice(mainResponseDropOffJOBJ.getDouble("CourierPrice"));
								try {
									dataModel_AllBookingList.setRunId(mainResponseDropOffJOBJ.getInt("runId"));
								}catch (JSONException ex){

								}

								dataModel_AllBookingList.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
								dataModel_AllBookingList.setOrderNumber(orderNumberForMenuLog);

								ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
								for (int k = 0; k < mainResponseDropOffJOBJ.getJSONArray("Shipments").length(); k++) {
									HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
									objOFShipments.put("Category", mainResponseDropOffJOBJ.getJSONArray("Shipments").getJSONObject(k).getString("Category"));
									objOFShipments.put("Quantity", mainResponseDropOffJOBJ.getJSONArray("Shipments").getJSONObject(k).getInt("Quantity"));
									arrayOfShipments.add(objOFShipments);
									objOFShipments = null;
								}
								dataModel_AllBookingList.setShipmentsArray(arrayOfShipments);
								arrayOfShipments = null;
								tempArrayOfDropOffBookingListArray.add(dataModel_AllBookingList);
								dataModel_AllBookingList = null;
								mainResponseDropOffJOBJ = null;
							}
						}
						dropOffResponseStr = null;
						completeBookingResponseObj = null;
					} catch (JSONException e) {
						e.printStackTrace();
						dropOffResponseStr = null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(responseArrayOfDropOffData[0].length() > 0){
						if(BookingView.bookingListArray != null) {
							if (BookingView.bookingListArray.size() > 0){
								for (DHL_SectionInterface completeBookingArray : BookingView.bookingListArray) {
									tempArrayOfDropOffBookingListArray.add(completeBookingArray);
								}
								BookingView.bookingListArray.clear();
							}
						}

						for (int j = 0; j < tempArrayOfDropOffBookingListArray.size(); j++) {
							try {
								Collections.sort(tempArrayOfDropOffBookingListArray,new Comparator<DHL_SectionInterface>() {
									@Override
									public int compare(DHL_SectionInterface lhs,DHL_SectionInterface rhs) {
										return ((All_Bookings_DataModels)rhs).getDropActual().compareTo(((All_Bookings_DataModels)lhs).getDropActual());
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}

							BookingView.bookingListArray.add(tempArrayOfDropOffBookingListArray.get(j));
						}
						//tempArrayOfDropOffBookingListArray = null;
						if(endlessCount == 0){
							if(BookingView.bookingListArray.size() > 0){
								adapterPickedUp = new EndlessAdapter_DropoffBookings(completeBookingContext, R.layout.completebookinglist_item);
								listPickedUp.setForDropOffBookingAdapter(adapterPickedUp);
								listPickedUp.setVisibility(View.VISIBLE);
								noCompleteBookingText.setVisibility(View.GONE);
							}else{
								listPickedUp.setVisibility(View.GONE);
								noCompleteBookingText.setVisibility(View.VISIBLE);
								noCompleteBookingText.setText("Dropoff bookings not available");
							}
						}else
							listPickedUp.addNewData(tempArrayOfDropOffBookingListArray);
					}

					if(BookingView.bookingListArray.size() == 0){
						listPickedUp.setVisibility(View.GONE);
						noCompleteBookingText.setVisibility(View.VISIBLE);
						noCompleteBookingText.setText("Dropoff bookings not available");
					}
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(completeBookingContext, "Error!", "Please try again later");
				}finally{
					try{
						if(progressToLoadDropOffBookings != null)
							if(progressToLoadDropOffBookings.isShowing())
								Custom_ProgressDialogBar.dismissProgressBar(progressToLoadDropOffBookings);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.execute();
	}


	
	@Override
	public void loadData() {
		try {
			endlessCount++;
			callCompletedBookingListItems();
			Log.e("", "---------------------- Endless adapter Complete booking-----   " + endlessCount);
		} catch (Exception e) {
			e.printStackTrace();
		}				
	}
}
