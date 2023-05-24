package com.zoom2u.slidemenu;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.json.JSONObject;

import android.app.ProgressDialog;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.webservice.WebserviceHandler;

public class Invite_Customer extends Fragment {
	

	Button inviteCustomerBtn;
	View rootView = null;
	ProgressDialog progressForInviteCustomer;

	
	String customerFirstNameStr, customerLastNameStr, customerEmailStr, customerContactNoStr, customersCompanyNameStr;
	TextView slideMenuTxt;
	private EditText edtFirstNameAddMember, edtLastNameAddMember, edtEmailAddMember, edtPhoneNoAddMember,edtCompanyAddMember;
	private TextView firstNameAddMember, lastNameAddMember, emailAddMember, phoneNoAddMember,companyAddMember;
	public Invite_Customer(){}



	public void setSlideMenuChatCounterTxt(TextView slideMenuTxt){
		this.slideMenuTxt = slideMenuTxt;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try{
			rootView = inflater.inflate(R.layout.invite_customer_view, container, false);
			if(savedInstanceState != null){
				ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
				if(LoginZoomToU.NOVA_BOLD == null)
					LoginZoomToU.staticFieldInit(getActivity());
			}
			inItInviteCustomerView();
		}catch(Exception e){
			e.printStackTrace();
		}
		return rootView;
  }
	
	@Override
	public void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		SlideMenuZoom2u.countChatBookingView = slideMenuTxt;
		Model_DeliveriesToChat.showExclamationForUnreadChat(slideMenuTxt);
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
		//	SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	/******** Initialize Invite customer view ********/
	void inItInviteCustomerView(){
		try{

			firstNameAddMember = (TextView) rootView.findViewById(R.id.firstNameAddMember);
			lastNameAddMember = (TextView) rootView.findViewById(R.id.lastNameAddMember);
			emailAddMember = (TextView) rootView.findViewById(R.id.emailAddMember);
			phoneNoAddMember = (TextView) rootView.findViewById(R.id.phoneNoAddMember);
            companyAddMember=(TextView) rootView.findViewById(R.id.companyAddMember);



			edtFirstNameAddMember = (EditText) rootView.findViewById(R.id.edtFirstNameAddMember);
			edtLastNameAddMember = (EditText) rootView.findViewById(R.id.edtLastNameAddMember);
			edtEmailAddMember = (EditText) rootView.findViewById(R.id.edtEmailAddMember);
			edtPhoneNoAddMember = (EditText) rootView.findViewById(R.id.edtPhoneNoAddMember);
			edtCompanyAddMember = (EditText) rootView.findViewById(R.id.edtCompanyAddMember);


			 String text="<font color=#a4abb0>Email Address</font><font color=#FF1C00>*</font>";
			 emailAddMember.setText(Html.fromHtml(text));
			 edtEmailAddMember.setHint(Html.fromHtml(text));

			setEdtFieldBGtoGray(edtFirstNameAddMember, firstNameAddMember);
			setEdtFieldBGtoGray(edtLastNameAddMember, lastNameAddMember);
			setEdtFieldBGtoGray(edtEmailAddMember, emailAddMember);
			setEdtFieldBGtoGray(edtPhoneNoAddMember, phoneNoAddMember);
			setEdtFieldBGtoGray(edtCompanyAddMember, companyAddMember);

			firstNameAddMember.setVisibility(View.INVISIBLE);
			lastNameAddMember.setVisibility(View.INVISIBLE);
			emailAddMember.setVisibility(View.INVISIBLE);
			phoneNoAddMember.setVisibility(View.INVISIBLE);
			companyAddMember.setVisibility(View.INVISIBLE);
			if(inviteCustomerBtn == null)
				inviteCustomerBtn = (Button) rootView.findViewById(R.id.inviteCustomerBtn);

			if (MainActivity.isIsBackGroundGray()) {
				inviteCustomerBtn.setBackgroundResource(R.drawable.chip_background_gray);
			} else {
				inviteCustomerBtn.setBackgroundResource(R.drawable.chip_background);

			}
			inviteCustomerBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
					try {
						customerFirstNameStr = edtFirstNameAddMember.getText().toString();
						customerLastNameStr = edtLastNameAddMember.getText().toString();
						customerEmailStr = edtEmailAddMember.getText().toString();
						customerContactNoStr = edtPhoneNoAddMember.getText().toString();
						customersCompanyNameStr = edtCompanyAddMember.getText().toString();
						
						if(customerEmailStr.equals("")){
								DialogActivity.alertDialogView(getActivity(), "Information !", "Please enter the mandatary fields.");
						}else if(!customerEmailStr.matches(LoginZoomToU.EMAIL_PATTERN)){
								DialogActivity.alertDialogView(getActivity(), "Information !", "Please enter valid e-mail id.");
						}else if(!customerContactNoStr.equals("")){
							if(!customerContactNoStr.matches(LoginZoomToU.PHONE_REGIX))
								DialogActivity.alertDialogView(getActivity(), "Information !", "Please enter valid contact number.");
							else
								inviteCustomerCall();
						}else{
							inviteCustomerCall();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

			}
			});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	private void setEdtFieldBGtoGray(final EditText edtFieldAddEditMember, final TextView txtFieldAddEditMember) {
		edtFieldAddEditMember.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (s.length() == 0) {
					edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_graydark);

					txtFieldAddEditMember.setVisibility(View.INVISIBLE);
				} else {
					edtFieldAddEditMember.setBackgroundResource(R.drawable.bg_transparent_bottom_blue);
					txtFieldAddEditMember.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}
	private void inviteCustomerCall(){
		customerEmailStr = "Email="+customerEmailStr;
		
		if(!customerFirstNameStr.equals("")){
			try {
				customerFirstNameStr = URLEncoder.encode(customerFirstNameStr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			customerEmailStr = customerEmailStr+"&FirstName="+customerFirstNameStr;
		}
		if(!customerLastNameStr.equals("")){
			try {
				customerLastNameStr = URLEncoder.encode(customerLastNameStr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			customerEmailStr = customerEmailStr+"&LastName="+customerLastNameStr;
		}
		if(!customerContactNoStr.equals("")){
			try {
				customerContactNoStr = URLEncoder.encode(customerContactNoStr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			customerEmailStr = customerEmailStr+"&ContactNo="+customerContactNoStr+"&MobileNo="+customerContactNoStr;
		}
		if(!customersCompanyNameStr.equals("")){
			try {
				customersCompanyNameStr = URLEncoder.encode(customersCompanyNameStr, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			customerEmailStr = customerEmailStr+"&CompanyName="+customersCompanyNameStr+"&CompanyName="+customersCompanyNameStr;
		}
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()){
			InviteCustomerAsyncTask();
			// new InviteCustomerAsyncTask().execute();
		}else{
			DialogActivity.alertDialogView(getActivity(), "No Network !", "No network connection, Please try again later.");
		}
	}
	
	private void InviteCustomerAsyncTask(){

		final String[] invitationResponse = {"0"};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(progressForInviteCustomer == null)
					progressForInviteCustomer = new ProgressDialog(getActivity());
				Custom_ProgressDialogBar.inItProgressBar(progressForInviteCustomer);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					invitationResponse[0] = webServiceHandler.inviteCustomer(customerEmailStr);
					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
					invitationResponse[0] = "0";
				}
			}

			@Override
			public void onPostExecute() {
				if(progressForInviteCustomer != null)
					if(progressForInviteCustomer.isShowing())
						Custom_ProgressDialogBar.dismissProgressBar(progressForInviteCustomer);

				try {
					if(!invitationResponse[0].equals("0")){
						try {
							JSONObject jObjOfInvitation = new JSONObject(invitationResponse[0]);
							if(jObjOfInvitation.getBoolean("success")==true)
								//DialogActivity.alertDialogView(getActivity(), "Successful !", "Invitation send successfuly to customers email.");
								Toast.makeText(getActivity(), "Email to customer sent!", Toast.LENGTH_LONG).show();
							else
								DialogActivity.alertDialogView(getActivity(), "Error!", "Something went wrong. Please try again");
							jObjOfInvitation = null;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else
						DialogActivity.alertDialogView(getActivity(), "Error!", "Something went wrong. Please try again");
				} catch (Exception e) {
					e.printStackTrace();
					DialogActivity.alertDialogView(getActivity(), "Error!", "Something went wrong. Please try again later.");
				}
			}
		}.execute();
	}



}
