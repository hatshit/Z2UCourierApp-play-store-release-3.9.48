package com.zoom2u.dialogactivity;

import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.webservice.WebserviceHandler;

public class Dialog_RequestAvailability extends Activity implements OnClickListener{

	ProgressDialog progressForRequestAvailability;
	TextView headerTxtRequestAvailability, countChatAvailabilityDetail;
	TextView pickLocationTitleTxt, pickLocationValueTxt, dropLocationTitleTxt, dropLocationValueTxt, deliveryTimeTitleTxt,
		deliveryTimeValueTxt, courierMsgTxt;
	Button acceptRequestAvailability, rejectRequestAvailability;
	ImageView cancelRequestAvailWindowBtn, availabilityDetailChatIcon;
	boolean isRequestAccepted = false;
	JSONObject jObjOfGetRequestAvailabilityDetail;
	
	Dialog dialogInRequestAvailabilityUI;
	int requestId = 0;
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		LoginZoomToU.notificUIRequestAvailCount = 1;
		initView();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_requestavailability);
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(Dialog_RequestAvailability.this);
		LoginZoomToU.notificUIRequestAvailCount = 1;
		if(headerTxtRequestAvailability == null)
			headerTxtRequestAvailability = (TextView) findViewById(R.id.headerTxtRequestAvailability);
		headerTxtRequestAvailability.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
			
		if(pickLocationTitleTxt == null)
			pickLocationTitleTxt = (TextView) findViewById(R.id.pickLocationTitleTxt);
		pickLocationTitleTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		if(pickLocationValueTxt == null)
			pickLocationValueTxt = (TextView) findViewById(R.id.pickLocationValueTxt);
		pickLocationValueTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		if(dropLocationTitleTxt == null)
			dropLocationTitleTxt = (TextView) findViewById(R.id.dropLocationTitleTxt);
		dropLocationTitleTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		if(dropLocationValueTxt == null)
			dropLocationValueTxt = (TextView) findViewById(R.id.dropLocationValueTxt);
		dropLocationValueTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		if(deliveryTimeTitleTxt == null)
			deliveryTimeTitleTxt = (TextView) findViewById(R.id.requestNotesTitleTxt);
		deliveryTimeTitleTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		if(deliveryTimeValueTxt == null)
			deliveryTimeValueTxt = (TextView) findViewById(R.id.requestNotesMsgTxt);
		deliveryTimeValueTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		if(courierMsgTxt == null)
			courierMsgTxt = (TextView) findViewById(R.id.courierMsgTxt);
		courierMsgTxt.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		
		if(acceptRequestAvailability == null)
			acceptRequestAvailability = (Button) findViewById(R.id.acceptRequestAvailability);
		acceptRequestAvailability.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		acceptRequestAvailability.setOnClickListener(this);
		if(rejectRequestAvailability == null)
			rejectRequestAvailability = (Button) findViewById(R.id.rejectRequestAvailability);
		rejectRequestAvailability.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
		rejectRequestAvailability.setOnClickListener(this);
		if(cancelRequestAvailWindowBtn == null)
			cancelRequestAvailWindowBtn = (ImageView) findViewById(R.id.cancelRequestAvailWindowBtn);
		cancelRequestAvailWindowBtn.setVisibility(View.VISIBLE);
		cancelRequestAvailWindowBtn.setOnClickListener(this);

		availabilityDetailChatIcon = (ImageView) findViewById(R.id.availabilityDetailChatIcon);
		availabilityDetailChatIcon.setVisibility(View.GONE);

		countChatAvailabilityDetail = (TextView) findViewById(R.id.countChatAvailabilityDetail);
		countChatAvailabilityDetail.setVisibility(View.GONE);

		initView();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	synchronized void initView(){
		try{	
			if(progressForRequestAvailability != null)
				if(progressForRequestAvailability.isShowing())
					progressForRequestAvailability.dismiss();
			
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				//new GetRequestAvailabilityDetail().execute();
				GetRequestAvailabilityDetail();
			else
				DialogActivity.alertDialogView(Dialog_RequestAvailability.this, "Network error!", "No network connection, Please try again later.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void GetRequestAvailabilityDetail(){

		final String[] responseStrRequestAvailabilityById = {"0"};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressForRequestAvailability != null)
						if(progressForRequestAvailability.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForRequestAvailability);
				}catch(Exception e){
					e.printStackTrace();
				}

				if(progressForRequestAvailability != null)
					progressForRequestAvailability = null;
				progressForRequestAvailability = new ProgressDialog(Dialog_RequestAvailability.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForRequestAvailability);
			}

			@Override
			public void doInBackground() {
				try{
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if(!PushReceiver.prefrenceForPushy.getString("requestId", "0").equals("0") && !PushReceiver.prefrenceForPushy.getString("requestId", "").equals("")){
						requestId = Integer.parseInt(PushReceiver.prefrenceForPushy.getString("requestId", "0"));
						responseStrRequestAvailabilityById[0] = webServiceHandler.getRequestAvailById(requestId);
						try {
							jObjOfGetRequestAvailabilityDetail =  new JSONObject(responseStrRequestAvailabilityById[0]);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
					requestId = 0;
					responseStrRequestAvailabilityById[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				if(progressForRequestAvailability != null)
					if(progressForRequestAvailability.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressForRequestAvailability);

				try {
					if (jObjOfGetRequestAvailabilityDetail != null) {
						if (jObjOfGetRequestAvailabilityDetail.getBoolean("success")) {
							try {
								if (!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("DropSuburb").equals("") &&
										!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("DropSuburb").equals(null)
										&& jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("DropSuburb") != null)
									dropLocationValueTxt.setText(jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("DropSuburb"));
								else
									dropLocationValueTxt.setText("Dropoff suburb");
							} catch (Exception e1) {
								e1.printStackTrace();
								dropLocationValueTxt.setText("Dropoff suburb");
							}

							try {
								if (!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("PickupSuburb").equals("") &&
										!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("PickupSuburb").equals(null)
										&& jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("PickupSuburb") != null)
									pickLocationValueTxt.setText(jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("PickupSuburb"));
								else
									pickLocationValueTxt.setText("Pickup suburb");
							} catch (Exception e1) {
								e1.printStackTrace();
								pickLocationValueTxt.setText("Pickup suburb");
							}

							try {
								if (!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("Notes").equals("") &&
										!jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("Notes").equals(null)
										&& jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("Notes") != null)
									deliveryTimeValueTxt.setText(jObjOfGetRequestAvailabilityDetail.getJSONArray("data").getJSONObject(0).getString("Notes"));
								else
									deliveryTimeValueTxt.setText("Not available");
							} catch (Exception e) {
								e.printStackTrace();
								deliveryTimeValueTxt.setText("Not available");
							}
						}else
							dialogViewForNewNotification("Sorry!", "Something went wrong at this movement, Please try again");
					}else
						dialogViewForNewNotification("Sorry!", "Something went wrong at this movement, Please try again");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				jObjOfGetRequestAvailabilityDetail = null;
			}
		}.execute();

	}



	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.acceptRequestAvailability:
			isRequestAccepted = false;
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				//new AcceptBookingForPickupAsyncTask().execute();
				AcceptBookingForPickupAsyncTask();
			else
				DialogActivity.alertDialogView(Dialog_RequestAvailability.this, "Network error", "No network connection, Please try again later.");
			break;
		case R.id.rejectRequestAvailability:
			isRequestAccepted = true;
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
				//new AcceptBookingForPickupAsyncTask().execute();
				AcceptBookingForPickupAsyncTask();
			else
				DialogActivity.alertDialogView(Dialog_RequestAvailability.this, "Network error", "No network connection, Please try again later.");
		break;
		case R.id.cancelRequestAvailWindowBtn:
			LoginZoomToU.notificUIRequestAvailCount= 0;
			finish();
			break;	
		default:
			break;
		}
	}

	private void AcceptBookingForPickupAsyncTask(){

		final JSONObject[] jObjAcceptRejectRequestAvailResponse = new JSONObject[1];

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressForRequestAvailability != null)
						if(progressForRequestAvailability.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForRequestAvailability);
				}catch(Exception e){
					e.printStackTrace();
				}
				if(progressForRequestAvailability != null)
					progressForRequestAvailability = null;
				progressForRequestAvailability = new ProgressDialog(Dialog_RequestAvailability.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForRequestAvailability);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					String responseForAcceptRejectRequestAvail = "";
					if(isRequestAccepted == false){
						responseForAcceptRejectRequestAvail = webServiceHandler.acceptAvailabilityRequest(requestId);
					}else{
						responseForAcceptRejectRequestAvail = webServiceHandler.rejectAvailabilityRequest(requestId);
					}
					jObjAcceptRejectRequestAvailResponse[0] = new JSONObject(responseForAcceptRejectRequestAvail);
				}catch(Exception e){
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(isRequestAccepted == false){
						if(jObjAcceptRejectRequestAvailResponse[0].getBoolean("success") == true){
							dialogViewForNewNotification("Availability request!", "You replied yes");
						}else
							dialogViewForNewNotification("Sorry!", "Something went wrong at this movement, Please try again");
					}else{
						if(jObjAcceptRejectRequestAvailResponse[0].getBoolean("success") == true){
							dialogViewForNewNotification("Availability request!", "You replied no");
						}else
							dialogViewForNewNotification("Sorry!", "Something went wrong at this movement, Please try again");
					}
				} catch (Exception e) {
					e.printStackTrace();
					dialogViewForNewNotification("Server error !", "Please try later !");
				}finally{
					jObjAcceptRejectRequestAvailResponse[0] = null;
					try {
						if(progressForRequestAvailability != null)
							if(progressForRequestAvailability.isShowing())
								Custom_ProgressDialogBar.dismissProgressBar(progressForRequestAvailability);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}.execute();
	}


	
private void dialogViewForNewNotification(String titleStr, String msgStr){
		if(dialogInRequestAvailabilityUI != null)
			dialogInRequestAvailabilityUI = null;
			dialogInRequestAvailabilityUI = new Dialog(Dialog_RequestAvailability.this);
			dialogInRequestAvailabilityUI.setCancelable(true);
			dialogInRequestAvailabilityUI.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialogInRequestAvailabilityUI.setContentView(R.layout.dialogview);

			Window window = dialogInRequestAvailabilityUI.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) dialogInRequestAvailabilityUI.findViewById(R.id.titleDialog);
			enterFieldDialogHEader.setTypeface(LoginZoomToU.NOVA_BOLD);
			enterFieldDialogHEader.setText(titleStr);

			TextView enterFieldDialogMsg = (TextView) dialogInRequestAvailabilityUI.findViewById(R.id.dialogMessageText);
			enterFieldDialogMsg.setTypeface(LoginZoomToU.NOVA_REGULAR);
			enterFieldDialogMsg.setText(msgStr);

			Button enterFieldDialogDoneBtn = (Button) dialogInRequestAvailabilityUI.findViewById(R.id.dialogDoneBtn);
			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
					dialogInRequestAvailabilityUI.dismiss();
					finish();
					LoginZoomToU.notificUIRequestAvailCount = 0;
				}
			});
			dialogInRequestAvailabilityUI.show();
	}
	
}
