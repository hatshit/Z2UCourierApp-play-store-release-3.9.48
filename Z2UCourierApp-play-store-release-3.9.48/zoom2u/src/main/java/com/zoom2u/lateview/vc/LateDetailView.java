package com.zoom2u.lateview.vc;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class LateDetailView extends Activity{
	
	Button dropDownBtn;
	ImageView dropdownImg, backFromLateView;
	ListView dropDownList;
	
	TextView headerTitleTxt, customerNameHeaderTxt, customerEmailHeaderTxt, customerContactNoHeaderTxt, lateInformationHeader,
	  saveButtonLate;
	
	String updatedNotesStr = "";
	
	EditText customerNameTxt, customerEmailTxt, customerContactNoTxt;
	ProgressDialog progressForLateInfo;
	ArrayList<String> list = new ArrayList<String>();
    boolean isClicked = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_late_view);
		
		list.add("Customer Contacted, all ok will deliver after hours");
        list.add("Customer Contacted, all ok will deliver tomorrow");
        list.add("Customer Contacted, all ok will deliver to new destination");
        list.add("Unable to contact customer");
        list.add("Other (allow courier to enter note)");
		
        updatedNotesStr = list.get(0);
        
		if(headerTitleTxt == null)
			headerTitleTxt = (TextView) findViewById(R.id.headerTitleTxt);
			headerTitleTxt.setTypeface(LoginZoomToU.NOVA_BOLD);
		if(customerNameHeaderTxt == null)
			customerNameHeaderTxt = (TextView) findViewById(R.id.customerNameHeaderTxt);
			customerNameHeaderTxt.setTypeface(LoginZoomToU.NOVA_BOLD);
		if(customerEmailHeaderTxt == null)
			customerEmailHeaderTxt = (TextView) findViewById(R.id.customerEmailHeaderLateDetailTxt);
			customerEmailHeaderTxt.setTypeface(LoginZoomToU.NOVA_BOLD);
		if(customerContactNoHeaderTxt == null)
			customerContactNoHeaderTxt = (TextView) findViewById(R.id.customerContactNoHeaderTxt);
			customerContactNoHeaderTxt.setTypeface(LoginZoomToU.NOVA_BOLD);
		if(lateInformationHeader == null)
			lateInformationHeader = (TextView) findViewById(R.id.lateInformationHeader);
			lateInformationHeader.setTypeface(LoginZoomToU.NOVA_BOLD);
		if(saveButtonLate == null)
			saveButtonLate = (TextView) findViewById(R.id.saveButtonLate);
			saveButtonLate.setTypeface(LoginZoomToU.NOVA_REGULAR);
		
		if(customerNameTxt == null)
			customerNameTxt = (EditText) findViewById(R.id.customerNameTxt);
			customerNameTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		if(customerEmailTxt == null)
			customerEmailTxt = (EditText) findViewById(R.id.customerEmailTxt);
			customerEmailTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		if(customerContactNoTxt == null)
			customerContactNoTxt = (EditText) findViewById(R.id.customerContactNoTxt);
			customerContactNoTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
		
		if(dropDownBtn == null)
			dropDownBtn = (Button) findViewById(R.id.dropDownBtn);
			dropDownBtn.setTypeface(LoginZoomToU.NOVA_REGULAR);
			dropDownBtn.setText(list.get(0));
		if(dropdownImg == null)
			dropdownImg = (ImageView) findViewById(R.id.dropdownImg);
		if(dropDownList == null)
			dropDownList = (ListView) findViewById(R.id.dropDownList);
		dropDownList.setCacheColorHint(Color.parseColor("#318CE7"));
	    
		dropDownList.setScrollingCacheEnabled(false);
		dropDownList.setAnimationCacheEnabled(false);
		
    //     CustomListForLateDetail adapter = new CustomListForLateDetail(this, R.layout.list_item,  list);
    //     dropDownList.setAdapter(adapter);
         
         saveButtonLate.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View arg0) {
 				if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
 					UpdateLateInfoAsyncTask();
 					//new UpdateLateInfoAsyncTask().execute();
 				else
 					DialogActivity.alertDialogView(LateDetailView.this, "No network!", "No internet connection, Please try later");
 			}
 		});
         
         dropDownBtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                      if (isClicked) {

                    	  dropDownList.setVisibility(View.VISIBLE);
//                             TranslateAnimation slide = new TranslateAnimation(0, 0,
//                                           -1500, 0);
//                             slide.setDuration(2300);
//                             slide.setFillAfter(false);
//                             dropDownList.startAnimation(slide);
                             isClicked = false;
                      } else {

//                             TranslateAnimation slide = new TranslateAnimation(0, 0, 0,
//                                           -4500);
                             isClicked = true;
//                             slide.setDuration(4500);
//                             slide.setFillAfter(false);
//                             dropDownList.startAnimation(slide);
                             dropDownList.setVisibility(View.GONE);
                      }
                }
         });

         dropDownList.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                	updatedNotesStr = list.get(position);
                	isClicked = true;
                	dropDownBtn.setText(list.get(position));
                    dropDownList.setVisibility(View.GONE);
                    
                    if(position == 4){
                    	dialogForEnterNote();
                    }
                    
                }
         });
  }

	void dialogForEnterNote(){
		try {
			if(DialogActivity.enterFieldDialog != null)
				DialogActivity.enterFieldDialog = null;
				DialogActivity.enterFieldDialog = new Dialog(LateDetailView.this);
				DialogActivity.enterFieldDialog.setCancelable(false);
				DialogActivity.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				DialogActivity.enterFieldDialog.setContentView(R.layout.dialog_enter_note);

				Window window = DialogActivity.enterFieldDialog.getWindow();
				window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				android.view.WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.CENTER;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);

				final EditText edtFieldDialogTxt = (EditText) DialogActivity.enterFieldDialog.findViewById(R.id.dialogEdtNoteTxt);
				edtFieldDialogTxt.setTypeface(LoginZoomToU.NOVA_REGULAR);
				
				TextView saveDialogBtnNote = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.saveDialogBtnNote);
				saveDialogBtnNote.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

				TextView cancelDialogBtnNote = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.cancelDialogBtnNote);
				cancelDialogBtnNote.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

				saveDialogBtnNote.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						DialogActivity.enterFieldDialog.dismiss();
						updatedNotesStr = edtFieldDialogTxt.getText().toString();
						if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
							UpdateLateInfoAsyncTask();
							//new UpdateLateInfoAsyncTask().execute();
						else
							DialogActivity.alertDialogView(LateDetailView.this, "No network!", "No internet connection, Please try later");
						}
					});
				
				cancelDialogBtnNote.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogActivity.enterFieldDialog.dismiss();
					}
				});
				DialogActivity.enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void successDialog(String alertTitleStr, String msgStr){
		try {
			if(DialogActivity.enterFieldDialog != null)
				DialogActivity.enterFieldDialog = null;
			DialogActivity.enterFieldDialog = new Dialog(LateDetailView.this);
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
				enterFieldDialogHEader.setTypeface(LoginZoomToU.NOVA_BOLD);
				enterFieldDialogHEader.setText(alertTitleStr);

				TextView enterFieldDialogMsg = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.dialogMessageText);
				enterFieldDialogMsg.setTypeface(LoginZoomToU.NOVA_REGULAR);
				enterFieldDialogMsg.setText(msgStr);

				ImageView enterFieldDialogDoneBtn = (ImageView) DialogActivity.enterFieldDialog.findViewById(R.id.dialogDoneBtn);
				enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogActivity.enterFieldDialog.dismiss();
					finish();
					}
				});
				DialogActivity.enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void UpdateLateInfoAsyncTask(){
		final String[] lateInfoResponseStr = {"0"};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try {
					if(progressForLateInfo == null)
						progressForLateInfo = new ProgressDialog(LateDetailView.this);
					progressForLateInfo.setMessage("Wait while updating...");
					progressForLateInfo.setCancelable(false);
					progressForLateInfo.show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					lateInfoResponseStr[0] = webServiceHandler.updateDeliveryNotes(String.valueOf(getIntent().getIntExtra("bookingIdForLateInfo", 0)), updatedNotesStr);
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					lateInfoResponseStr[0] = "0";
				}

			}

			@Override
			public void onPostExecute() {
				if(progressForLateInfo != null)
					if(progressForLateInfo.isShowing())
						progressForLateInfo.dismiss();

				try {
					JSONObject jOBJForlateInfo = new JSONObject(lateInfoResponseStr[0]);
					if(jOBJForlateInfo.getBoolean("success")) {
						successDialog("Updated!", "Your notes updated successfully");
					} else
						DialogActivity.alertDialogView(LateDetailView.this, "Sorry!", "Not updated, Please try later");

				} catch (JSONException e) {
					e.printStackTrace();
					lateInfoResponseStr[0] = "0";
				}
			}
		}.execute();

	}


}
