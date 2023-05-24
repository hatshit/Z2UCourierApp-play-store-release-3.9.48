package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.models.Model_DialogReasonLateDelivey;
import com.zoom2u.lateview.vc.CustomListForLateDetail;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class DialogReasonForLateDelivery extends Activity{
	
	ProgressDialog progressForReasonForLate;
	EditText edtTxtReasonForLateDelivery;
	RelativeLayout mainLayoutReasonForLateDelivery;
	Button dropDownBtnReasonLateDelivery;
	
	int itemPosition = 0;
	String updatedNotesStr = "";

	boolean isClicked = true;
	
	int bookingIdForLateDelivery;
	String bookingTypeSource;
    boolean IsFromTtd;
	public static ArrayList<Model_DialogReasonLateDelivey> reasonlist;

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("ReasonLateBookingID", bookingIdForLateDelivery);
		outState.putString("BookingTypeSource", bookingTypeSource);
		outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
	}
	
	// ********* Get items from bundle when app killed in background
	void getItemsFromBuldle(Bundle outState){
		bookingIdForLateDelivery = outState.getInt("ReasonLateBookingID");
		bookingTypeSource = outState.getString("BookingTypeSource");
		ConfirmPickUpForUserName.isDropOffSuccessfull = outState.getInt("SlideMenuItem");
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null)
			getItemsFromBuldle(savedInstanceState);
		super.onRestoreInstanceState(savedInstanceState);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.reason_for_lateview);

		if(savedInstanceState != null)
			getItemsFromBuldle(savedInstanceState);
		else{
			bookingIdForLateDelivery = getIntent().getIntExtra("ReasonLateBookingID", 0);
			bookingTypeSource = getIntent().getStringExtra("BookingTypeSource");
			IsFromTtd = getIntent().getBooleanExtra("IsFromTtd",false);
		}
		showReasonForLateDelivery();
	}
	
	@Override
	public void onBackPressed(){
		//super.onBackPressed();
		//finish();
		setResult(RESULT_OK);
		LoginZoomToU.imm.hideSoftInputFromWindow(edtTxtReasonForLateDelivery.getWindowToken(), 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		setResult(RESULT_OK);
	}
	
	// ************** In-it reason for late delivery **********
	private void showReasonForLateDelivery() {
		
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(DialogReasonForLateDelivery.this);

		if (reasonlist == null) {
			reasonlist = new ArrayList<Model_DialogReasonLateDelivey>();
			apiCallLateReason();
		} else if (reasonlist.size() == 0) {
			apiCallLateReason();
		} else
			setReasonsToUIContents ();
	}

	private void apiCallLateReason() {
		/*GetLateReasons reasonForLateDeliveryAsyncTask = new GetLateReasons();
		reasonForLateDeliveryAsyncTask.execute();
		reasonForLateDeliveryAsyncTask = null;*/
		GetLateReasons();
	}

	/******** Change button items *********/
	void updateBtnItems(Button buttonId, String btnColor, String btnTxt, Typeface tf){
		buttonId.setBackgroundColor(Color.parseColor(btnColor));
		buttonId.setText(btnTxt);

	}

	private void GetLateReasons(){
		final String[] getLateReasonResponseStr = {"0"};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressForReasonForLate != null)
						if(progressForReasonForLate.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForReasonForLate);
				}catch(Exception e){
					e.printStackTrace();
				}

				if(progressForReasonForLate != null)
					progressForReasonForLate = null;
				progressForReasonForLate = new ProgressDialog(DialogReasonForLateDelivery.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForReasonForLate);
			}

			@Override
			public void doInBackground() {
				try{
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					getLateReasonResponseStr[0] = webServiceHandler.getLateDeliveryReason();
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
					getLateReasonResponseStr[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				if(progressForReasonForLate != null)
					if(progressForReasonForLate.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressForReasonForLate);

				try {
					if(!getLateReasonResponseStr[0].equals("0") && !getLateReasonResponseStr[0].equals("")){
						JSONArray jObjOfLateDelivery = new JSONArray(getLateReasonResponseStr[0]);
						if(jObjOfLateDelivery.length() > 0) {
							for (int i = 0; i < jObjOfLateDelivery.length(); i++) {
								JSONObject jObjLateReasonData = jObjOfLateDelivery.getJSONObject(i);

								Model_DialogReasonLateDelivey model_ReasonLate = new Model_DialogReasonLateDelivey();
								model_ReasonLate.setId(jObjLateReasonData.getString("Id"));
								model_ReasonLate.setReasonForLate(jObjLateReasonData.getString("Reason"));
								reasonlist.add(model_ReasonLate);
							}
							setReasonsToUIContents ();
						} else
							successDialogForLateDeliveryReason("Sorry!", "Something went wront, please try again later");

						jObjOfLateDelivery = null;
					}else
						successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");

				} catch (Exception e) {
					e.printStackTrace();
					//successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");
				}
			}
		}.execute();

	}


	private void setReasonsToUIContents (){
		try {
			TextView titleReasonForLateDelivery = (TextView) findViewById(R.id.reasonLateDelievryTitleTxt);
			titleReasonForLateDelivery.setText("Late delivery");


			TextView textReasonForLate = (TextView) findViewById(R.id.textReasonForLate);
			textReasonForLate.setText("Reason for late delivery");


			edtTxtReasonForLateDelivery = (EditText) findViewById(R.id.reasonLateDeliveryMsgText);

			edtTxtReasonForLateDelivery.setHint("Please enter any extra info here!");

			final ListView dropDownListReasonLateView = (ListView) findViewById(R.id.dropDownListReasonLateView);
			dropDownListReasonLateView.setCacheColorHint(Color.parseColor("#318CE7"));
			dropDownListReasonLateView.setScrollingCacheEnabled(false);
			dropDownListReasonLateView.setAnimationCacheEnabled(false);

			CustomListForLateDetail adapter = new CustomListForLateDetail(DialogReasonForLateDelivery.this, R.layout.list_item,  reasonlist);
			dropDownListReasonLateView.setAdapter(adapter);
			adapter = null;

			dropDownBtnReasonLateDelivery = (Button) findViewById(R.id.dropDownBtnReasonLateDelivery);
			dropDownBtnReasonLateDelivery.setText("Select options");

			dropDownBtnReasonLateDelivery.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isClicked) {
						dropDownListReasonLateView.setVisibility(View.VISIBLE);
						isClicked = false;
					}else{
						isClicked = true;
						dropDownListReasonLateView.setVisibility(View.GONE);
					}
				}
			});

			dropDownListReasonLateView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					itemPosition = position;
					isClicked = true;
					dropDownBtnReasonLateDelivery.setText(reasonlist.get(position).getReasonForLate());
					dropDownListReasonLateView.setVisibility(View.GONE);
				}
			});

			Button reasonForLateDeliveryCancelBtn = (Button) findViewById(R.id.reasonLateDeliveryCancelBtn);
			reasonForLateDeliveryCancelBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
					LoginZoomToU.imm.hideSoftInputFromWindow(edtTxtReasonForLateDelivery.getWindowToken(), 0);
				}
			});

			Button menulogLateDeliverySubmitBtn = (Button) findViewById(R.id.menulogLateDeliverySubmitBtn);

			menulogLateDeliverySubmitBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v){
					try {
						if (dropDownBtnReasonLateDelivery.getText().toString().equals("Select options"))
							Toast.makeText(DialogReasonForLateDelivery.this, "Please select reason first", Toast.LENGTH_LONG).show();
						else {
							updatedNotesStr = edtTxtReasonForLateDelivery.getText().toString();
							callServiceForLateDelivery();
						}
					} catch (Exception e) {
						e.printStackTrace();
						successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");
					}
				}
			});

			reasonForLateDeliveryCancelBtn.setVisibility(View.GONE);

		} catch (Exception e) {
			e.printStackTrace();
			successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");
		}
	}

	
	//******* Service calling for late delivery *******
	void callServiceForLateDelivery(){
		LoginZoomToU.imm.hideSoftInputFromWindow(edtTxtReasonForLateDelivery.getWindowToken(), 0);
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
			/*ReasonForLateDeliveryAsyncTask reasonForLateDeliveryAsyncTask = new ReasonForLateDeliveryAsyncTask();
			reasonForLateDeliveryAsyncTask.execute();
			reasonForLateDeliveryAsyncTask = null;*/
			ReasonForLateDeliveryAsyncTask();
		} else
			DialogActivity.alertDialogView(DialogReasonForLateDelivery.this, "No network!", "No network connection, Please try again later");
	}

	private void ReasonForLateDeliveryAsyncTask(){
		final String[] responseReasonForLateDeliveryStr = {"0"};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try{
					if(progressForReasonForLate != null)
						if(progressForReasonForLate.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForReasonForLate);
				}catch(Exception e){
					e.printStackTrace();
				}

				if(progressForReasonForLate != null)
					progressForReasonForLate = null;
				progressForReasonForLate = new ProgressDialog(DialogReasonForLateDelivery.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForReasonForLate);
			}

			@Override
			public void doInBackground() {
				try{
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					responseReasonForLateDeliveryStr[0] = webServiceHandler.reasonForLateDelivery(bookingIdForLateDelivery, updatedNotesStr,
							Integer.parseInt(reasonlist.get(itemPosition).getId()));
					webServiceHandler = null;
				}catch(Exception e){
					e.printStackTrace();
					responseReasonForLateDeliveryStr[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				if(progressForReasonForLate != null)
					if(progressForReasonForLate.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressForReasonForLate);

				try {
					if(!responseReasonForLateDeliveryStr[0].equals("0")){
						JSONObject jObjOfLateDelivery = new JSONObject(responseReasonForLateDeliveryStr[0]);
						if(jObjOfLateDelivery.getBoolean("success")) {
							if(BookingView.bookingViewSelection == 2){
								Intent intent1 = new Intent(DialogReasonForLateDelivery.this, ServiceToUpdate_ActiveBookingList.class);
								startService(intent1);
								intent1 = null;
							}
							if(!IsFromTtd)
							SlideMenuZoom2u.isDropOffCompleted=true;
							//Toast.makeText(DialogReasonForLateDelivery.this, "Late reason submitted.\n" +"Thanks!", Toast.LENGTH_LONG).show();
							finish();
							//successDialogForLateDeliveryReason("Late delivery!", "Reason submitted");
						} else
							successDialogForLateDeliveryReason("Sorry!", "Your request can't process at this moment, please try again");

						jObjOfLateDelivery = null;
					}else
						successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");

				} catch (Exception e) {
					e.printStackTrace();
					successDialogForLateDeliveryReason("Error!", "Something went wront, please try again later");
				}
			}
		}.execute();
	}


	void successDialogForLateDeliveryReason(String titleTxt, String msgTxt){
		try{
			if(DialogActivity.enterFieldDialog != null)
				if(DialogActivity.enterFieldDialog.isShowing())
					DialogActivity.enterFieldDialog.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			if(DialogActivity.enterFieldDialog != null)
				DialogActivity.enterFieldDialog = null;
				DialogActivity.enterFieldDialog = new Dialog(DialogReasonForLateDelivery.this);
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

				enterFieldDialogHEader.setText(titleTxt);

				TextView enterFieldDialogMsg = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.dialogMessageText);

				enterFieldDialogMsg.setText(msgTxt);

				Button enterFieldDialogDoneBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.dialogDoneBtn);

				enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						finish();
						DialogActivity.enterFieldDialog.dismiss();
					}
				});
				DialogActivity.enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}