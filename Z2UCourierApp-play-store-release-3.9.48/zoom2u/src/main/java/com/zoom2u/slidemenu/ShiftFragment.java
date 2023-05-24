package com.zoom2u.slidemenu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.z2u.booking.vc.NewBookingView.ViewHolderPattern;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionItemsModel;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.ShiftViewDetail;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.ShiftModel;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemodel.SwipeListView;
import com.zoom2u.slidemodel.SwipeListView.SwipeListViewCallback;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;

public class ShiftFragment extends Fragment{

    ArrayList<DHL_SectionInterface> arrayOfShiftModel;
    ListView shiftListView;
    TextView txtNoShift;
    ShiftItemAdapter adapterShiftItem;
    SwipeListViewCallback swipeCallBackShiftView;
    ProgressDialog progressToLoadShiftItems;
    Dialog dialogOnShiftAccept;

    RelativeLayout refreshBtnShiftList;

    int selectedItemPosition;

	TextView countChatShift;

	public ShiftFragment() {
		arrayOfShiftModel = new ArrayList<DHL_SectionInterface>();
	}

	@Override
	public void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		Model_DeliveriesToChat.showExclamationForUnreadChat(countChatShift);
		SlideMenuZoom2u.countChatBookingView = countChatShift;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
			outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	public void setSlideMenuChatCounterTxt(TextView slideMenuTxt){
		this.countChatShift = slideMenuTxt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View shiftView = null;
		shiftView = inflater.inflate(R.layout.shiftlist_fragment, container, false);
		try {
			if(savedInstanceState != null){
				try {
					ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
					if(LoginZoomToU.NOVA_BOLD == null)
						LoginZoomToU.staticFieldInit(getActivity());
					initializeShiftView(shiftView);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else
				initializeShiftView(shiftView);
		}catch(Exception e){
			e.printStackTrace();
		}
		return shiftView;
	}

	/*********** Call Async task for getting shifts  ***************/
	void callAsyncTaskForGettingShift(){
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
			/*AsyncTaskForGetShiftItems asyncTaskGetShiftItems = new AsyncTaskForGetShiftItems();
			asyncTaskGetShiftItems.execute();
			asyncTaskGetShiftItems = null;*/
			AsyncTaskForGetShiftItems();
		} else
			DialogActivity.alertDialogView(getActivity(), "No network!", "No network connection, Please try later.");
	}

	/*************** In-it shift view  **************/
	void initializeShiftView(View shiftView){
		try {
			if(shiftListView == null)
				shiftListView = (ListView) shiftView.findViewById(R.id.shiftListView);
			shiftListView.setVisibility(View.VISIBLE);

			if(txtNoShift == null)
				txtNoShift = (TextView) shiftView.findViewById(R.id.txtNoShift);
			txtNoShift.setVisibility(View.GONE);

			if (refreshBtnShiftList == null)
				refreshBtnShiftList = (RelativeLayout) shiftView.findViewById(R.id.refreshBtnShiftList);
			if (MainActivity.isIsBackGroundGray()) {
				refreshBtnShiftList.setBackgroundResource(R.drawable.chip_background_gray);
			} else {
				refreshBtnShiftList.setBackgroundResource(R.drawable.chip_background);

			}

			refreshBtnShiftList.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					callAsyncTaskForGettingShift();
				}
			});

			((Button) shiftView.findViewById(R.id.refreshBtn)).setClickable(false);
			shiftListView.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				    case MotionEvent.ACTION_UP: shiftListView.smoothScrollBy(0, 0);
				    case MotionEvent.ACTION_DOWN: shiftListView.smoothScrollBy(0, 0);
				    break;
			    }
				return true;
			}
			});

			swipeCallBackShiftView = new SwipeListViewCallback() {
						@Override
						public void onSwipeItemRight(boolean isleft, int position) {
						//	adapterShiftItem.onSwipeItemRight(isleft, position);
						}

						@Override
						public void onSwipeItemLeft(boolean isRight, int position) {
						//	adapterShiftItem.onSwipeItemLeft(isRight, position);
						}

						@Override
						public void onItemClickListener(ListAdapter adapter, int position) {
							try{
								Intent intentShiftDetail = new Intent(getActivity(), ShiftViewDetail.class);
								intentShiftDetail.putExtra("ShiftModel", arrayOfShiftModel.get(position));
								startActivity(intentShiftDetail);
								intentShiftDetail = null;
								getActivity().finish();
								getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
							}catch(Exception e){
								e.printStackTrace();
							}
						}

						@Override
						public ListView getListView() {
							return shiftListView;
						}

						@Override
						public void onItemLongClickListener(ListAdapter adapter, int position) {

						}
					};

					SwipeListView l = new SwipeListView(getActivity(), swipeCallBackShiftView);
					l.exec();

					clearArrayOfShift();
					callAsyncTaskForGettingShift();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/************* clear shift main array **********/
	void clearArrayOfShift(){
		if(arrayOfShiftModel != null) {
			if (arrayOfShiftModel.size() > 0)
				arrayOfShiftModel.clear();
		} else
			arrayOfShiftModel = new ArrayList<DHL_SectionInterface>();
	}

	private void AsyncTaskForGetShiftItems() {
		final String[] responseForGetShiftItems = {"0"};

		new MyAsyncTasks() {
			@Override
			public void onPreExecute() {
				try {
					if (progressToLoadShiftItems != null)
						if (progressToLoadShiftItems.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressToLoadShiftItems);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (progressToLoadShiftItems != null)
					progressToLoadShiftItems = null;
				progressToLoadShiftItems = new ProgressDialog(getActivity());
				Custom_ProgressDialogBar.inItProgressBar(progressToLoadShiftItems);
			}

			@Override
			public void doInBackground() {
				WebserviceHandler webServiceHandler = new WebserviceHandler();
				try {
					responseForGetShiftItems[0] = webServiceHandler.deliveryRunsForCourier();
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					responseForGetShiftItems[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if (progressToLoadShiftItems != null)
						if (progressToLoadShiftItems.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressToLoadShiftItems);

					if (!responseForGetShiftItems[0].equals("0") && !responseForGetShiftItems[0].equals("")) {
						JSONObject jObjOfShiftResponse = new JSONObject(responseForGetShiftItems[0]);
						if (jObjOfShiftResponse.getBoolean("success")) {

							setShiftItemToArray(jObjOfShiftResponse.getJSONArray("AvailableShifts"), jObjOfShiftResponse.getJSONArray("AcceptedShifts"), true);

							if (arrayOfShiftModel.size() > 0) {
								adapterShiftItem = new ShiftItemAdapter(getActivity(), R.layout.shiftlist_item);
								shiftListView.setAdapter(adapterShiftItem);
								shiftListView.setVisibility(View.VISIBLE);
								txtNoShift.setVisibility(View.GONE);
							} else
								setNoDeliveryRunTxt();
						} else
							setNoDeliveryRunTxt();
					} else
						setNoDeliveryRunTxt();
				} catch (Exception e) {
					e.printStackTrace();
					setNoDeliveryRunTxt();
				}
			}
		}.execute();
	}

	/*
	 ********* Set No deliveries runs available text ********
	 */
	private void setNoDeliveryRunTxt() {
		txtNoShift.setVisibility(View.VISIBLE);
		shiftListView.setVisibility(View.GONE);
	}

	private void setShiftItemToArray(JSONArray jArryOfGetShiftItem, JSONArray jArryOfAcceptedShift, boolean isAcceptedShift) {
		try {
			HashMap<Date, List<ShiftModel>> map = new HashMap<Date, List<ShiftModel>>();
			addDataToHashMap(jArryOfGetShiftItem, map);
			addDataToHashMap(jArryOfAcceptedShift, map);

			clearArrayOfShift();

			// ******* Default sorting provided by tree map in ascending order *****
			Map<Date, List<ShiftModel>> treeMap = new TreeMap<Date, List<ShiftModel>>(map);

			for (Map.Entry<Date, List<ShiftModel>> entry : treeMap.entrySet()) {

				Calendar cal = Calendar.getInstance();
				SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
				Date currentDate = cal.getTime();
				//******  For Today text
				String formattedCurrentDate = df.format(currentDate);
				String formattedCurrentDateStr = getFormatedDate(formattedCurrentDate);

				cal.add(Calendar.DATE, 1);
				Date tomorrowDate = cal.getTime();
				//******  For tomorrow text
				String formattedTomorrowDate = df.format(tomorrowDate);
				String formattedTomorrowDateStr = getFormatedDate(formattedTomorrowDate);

				String dateString = df.format(entry.getKey());

				if (formattedCurrentDate.equals(dateString))
					arrayOfShiftModel.add(new DHL_SectionItemsModel("Today (" + formattedCurrentDateStr + ")"));
				else if (formattedTomorrowDate.equals(dateString))
					arrayOfShiftModel.add(new DHL_SectionItemsModel("Tomorrow (" + formattedTomorrowDateStr + ")"));
				else {
					SimpleDateFormat converter = new SimpleDateFormat("dd-MMM-yyyy");
					SimpleDateFormat dateFormat = new SimpleDateFormat("EEE (dd-MMM)");
					Date date = null;
					try {
						date = converter.parse(converter.format(entry.getKey()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String dateString1 = dateFormat.format(date);
					String dateStringForFutureDate = getFormatedDate(dateString1);

					arrayOfShiftModel.add(new DHL_SectionItemsModel(dateStringForFutureDate));
				}

				for (ShiftModel shiftModel : entry.getValue()) {
					arrayOfShiftModel.add(shiftModel);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addDataToHashMap(JSONArray jArryOfGetShiftItem, HashMap<Date,List<ShiftModel>> map) {
		try {
			for(int i = 0; i < jArryOfGetShiftItem.length(); i++) {
				ShiftModel shiftModel = new ShiftModel();
				JSONObject jObjOfShifts = jArryOfGetShiftItem.getJSONObject(i);
				shiftModel.setShiftID(jObjOfShifts.getInt("ShiftId"));
				shiftModel.setPayPerDelivery(jObjOfShifts.getInt("PayPerDelivery"));
				shiftModel.setTitleShiftItem(jObjOfShifts.getString("Title"));
				shiftModel.setStartDateTime(jObjOfShifts.getString("StartDateTime"));
				shiftModel.setEndDateTime(jObjOfShifts.getString("EndDateTime"));
				shiftModel.setStatus(jObjOfShifts.getString("Status"));

				shiftModel.setArea(jObjOfShifts.getString("Area"));
				shiftModel.setCity(jObjOfShifts.getString("City"));
				shiftModel.setState(jObjOfShifts.getString("State"));

				SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				converter.setTimeZone(TimeZone.getTimeZone("IST"));
				SimpleDateFormat df = new SimpleDateFormat("dd-MMM");
				Date date = null;
				try {
					date = converter.parse(jObjOfShifts.getString("StartDateTime"));
				} catch (ParseException e) {
					e.printStackTrace();
				}
				//	String dateString = df.format(date);

				addToMap(map, date, shiftModel);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private String getFormatedDate(String dateTime) {
		try {
			String[] formattedCurrentDate = dateTime.split("-");
			return formattedCurrentDate[0]+"-"+formattedCurrentDate[1];
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "-NA-";
	}

	public void addToMap(HashMap<Date, List<ShiftModel>> map, Date key, ShiftModel value){
		if(!map.containsKey(key)){
			map.put(key, new ArrayList<ShiftModel>());
		}
		map.get(key).add(value);
	}

	public static boolean checkDateBeforeAccept(String dateStr){
		boolean isBeforeDate = false;
		try{
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		    converter.setTimeZone(TimeZone.getTimeZone("IST"));
			Date checkDate = new Date();
			checkDate = converter.parse(dateStr);
			Log.e("", checkDate+"=============   "+cal.getTime().after(checkDate));
			if(cal.getTime().after(checkDate))
				isBeforeDate = false;
			else
				isBeforeDate = true;
			cal = null;
			checkDate = null;
			converter = null;
		}catch(Exception e){
			e.printStackTrace();
		}
		return isBeforeDate;
	}

	/******************  Set shift item adapter  *****************/
	public class ShiftItemAdapter  extends ArrayAdapter<DHL_SectionInterface>{

		private int[] colors = new int[] {0xFFFFFFFF, 0xF0F0F0F3};
		Context con;
		LayoutInflater inflater;

//		private boolean isSwipeLeft = false;
//		private boolean isSwipeRight = false;
//
//		private final int INVALID = -1;
//		private int DELETE_POS = -1;
//		private int ADD_POS = -1;

//		public void onSwipeItemRight(boolean isLeft, int position) {
//			if(isSwipeRight){
//				isSwipeRight = false;
//				ADD_POS = INVALID;
//				DELETE_POS = INVALID;
//			}else if(isSwipeLeft){
//				isSwipeLeft = false;
//				if (isLeft == false) {
//					ADD_POS = position;
//					DELETE_POS = INVALID;
//				}else if (ADD_POS == position)
//					ADD_POS = INVALID;
//			}else{
//				isSwipeRight = false;
//				isSwipeLeft = true;
//				ADD_POS = position;
//				DELETE_POS = INVALID;
//			}
//			notifyDataSetChanged();
//		}
//
//		public void onSwipeItemLeft(boolean isRight, int position) {
//			if(isSwipeLeft){
//				isSwipeLeft = false;
//				ADD_POS = INVALID;
//				DELETE_POS = INVALID;
//			}else if(isSwipeRight){
//				isSwipeRight = false;
//				if (isRight == false) {
//					DELETE_POS = position;
//					ADD_POS = INVALID;
//				}else if (DELETE_POS == position)
//					DELETE_POS = INVALID;
//			}else{
//				isSwipeLeft = false;
//				isSwipeRight = true;
//				ADD_POS = INVALID;
//				DELETE_POS = position;
//			}
//			notifyDataSetChanged();
//		}

		public ShiftItemAdapter(Context con, int resourceId) {
			super(con, resourceId);
			this.con = con;
		}

		@Override
		public int getCount() {
			return arrayOfShiftModel.size();
		}

		@Override
		public DHL_SectionInterface getItem(int arg0) {
			return arrayOfShiftModel.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View shiftView, ViewGroup arg2) {
			try {
				final DHL_SectionInterface i = arrayOfShiftModel.get(position);
				if (i.isSection()) {
					DHL_SectionItemsModel si = (DHL_SectionItemsModel) i;
					inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					shiftView = inflater.inflate(R.layout.dhllist_item_section, null);
					shiftView.setOnClickListener(null);
					((TextView) shiftView.findViewById(R.id.list_item_section_text)).setGravity(Gravity.LEFT | Gravity.CENTER);
					((TextView) shiftView.findViewById(R.id.list_item_section_text)).setPadding(22, 0, 0, 0);
					((TextView) shiftView.findViewById(R.id.list_item_section_text)).setText(si.getTitle());
				} else {
					inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					shiftView = inflater.inflate(R.layout.shiftlist_item, null);
					setDataItemView(shiftView, position);   	// ************ Set shifts list item content's ************
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			return shiftView;
		}

		/* *
		* ************ Set shifts list item content's ************
		* */
		private void setDataItemView(View shiftView, final int position) {

			ShiftModel deliveryRunData = (ShiftModel) arrayOfShiftModel.get(position);

			RelativeLayout shiftViewSubLayout = ViewHolderPattern.get(shiftView, R.id.shiftViewSubLayout);
			RelativeLayout shiftViewSubLayout2 = ViewHolderPattern.get(shiftView, R.id.shiftViewSubLayout2);

			final Button acceptShiftBtn = ViewHolderPattern.get(shiftView, R.id.acceptShift);

			acceptShiftBtn.setVisibility(View.GONE);

			final Button leavingShift = ViewHolderPattern.get(shiftView, R.id.leavingShift);

			leavingShift.setVisibility(View.GONE);

			TextView shiftTitleTxt = ViewHolderPattern.get(shiftView, R.id.shiftTitleTxt);

			shiftTitleTxt.setText(""+deliveryRunData.getTitleShiftItem());

			TextView shiftPayPerDeliveryTxt = ViewHolderPattern.get(shiftView, R.id.shiftPayPerDeliverySimpleTxt);

			shiftPayPerDeliveryTxt.setText("Estimated pay\nper delivery");

			TextView runStatusDeliveryRunTxt = ViewHolderPattern.get(shiftView, R.id.runStatusDeliveryRunTxt);

			if (deliveryRunData.getStatus().equals("Confirmed")) {
				runStatusDeliveryRunTxt.setText(deliveryRunData.getStatus());
				runStatusDeliveryRunTxt.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
			} else if (deliveryRunData.getStatus().equals("Accepted")) {
				runStatusDeliveryRunTxt.setText(deliveryRunData.getStatus());
				runStatusDeliveryRunTxt.setTextColor(getActivity().getResources().getColor(R.color.gold_light));
			} else {
				runStatusDeliveryRunTxt.setText("Available");
				runStatusDeliveryRunTxt.setTextColor(getActivity().getResources().getColor(R.color.colorAccent));
			}

			TextView suburbDeliveryRunTxt = ViewHolderPattern.get(shiftView, R.id.suburbDeliveryRunTxt);

			suburbDeliveryRunTxt.setText(deliveryRunData.getCity());

			TextView shiftPayPerDeliveryValue = ViewHolderPattern.get(shiftView, R.id.shiftPayPerDeliveryValue);

			shiftPayPerDeliveryValue.setText("$"+deliveryRunData.getPayPerDelivery());

			TextView shiftStartTimeTxt = ViewHolderPattern.get(shiftView, R.id.shiftStartTimeTxt);

			try{
				if(!deliveryRunData.getStartDateTime().equals("")){
					String startTimeStr[] = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(deliveryRunData.getStartDateTime()).split(" ");
					shiftStartTimeTxt.setText(startTimeStr[1]+" "+startTimeStr[2]);
				}else
					shiftStartTimeTxt.setText("-NA-");
			}catch(Exception e){
				e.printStackTrace();
				shiftStartTimeTxt.setText("-NA-");
			}

			TextView shiftEndTimeTxt = ViewHolderPattern.get(shiftView, R.id.shiftEndTimeTxt);

			try{
				if(!deliveryRunData.getEndDateTime().equals("")){
					String endTimeStr[] = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(deliveryRunData.getEndDateTime()).split(" ");
					shiftEndTimeTxt.setText(endTimeStr[1]+" "+endTimeStr[2]);
				}else
					shiftEndTimeTxt.setText("-NA-");
			}catch(Exception e){
				e.printStackTrace();
				shiftEndTimeTxt.setText("-NA-");
			}

		}
	}

}
