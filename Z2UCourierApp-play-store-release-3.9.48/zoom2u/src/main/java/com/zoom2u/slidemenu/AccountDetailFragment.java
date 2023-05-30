package com.zoom2u.slidemenu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;
import com.suggestprice_team.courier_team.TeamMemberList_Activity;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.roundedimage.RoundedImageView;
import com.zoom2u.slidemenu.accountdetail_section.CourierIdentification_View;
import com.zoom2u.slidemenu.accountdetail_section.MyProfile_Information;
import com.zoom2u.slidemenu.accountdetail_section.PoliceCheckView;
import com.zoom2u.slidemenu.accountdetail_section.SettingView;
import com.zoom2u.slidemenu.accountdetail_section.SummaryReportView;
import com.zoom2u.slidemenu.accountdetail_section.alcohol_delivery_training.AlcoholTrainingVideo_View;
import com.zoom2u.slidemenu.accountdetail_section.sellcourierbusiness.ActivitySellYourCourierBusiness;
import com.zoom2u.slidemenu.invoice_adjustment.InvoiceAdjustments;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import me.pushy.sdk.lib.jackson.core.type.TypeReference;
import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class AccountDetailFragment extends Fragment implements View.OnClickListener {
	
	public static final int PICK_FROM_CAMERA = 1;
	public static final int PICK_FROM_GALLERY = 2;
	public static int OPEN_SETTING_VIEW = 3;
	public static int OPEN_YOUR_INFO_PAGE = 604;

    private static final String URI_VICTORIA_OWNER_DRIVER_INFO_BOOKLET = "https://business.vic.gov.au/__data/assets/pdf_file/0005/1322339/Victorian-Owner-Driver-Information-Booklet.pdf";
	private static final String URI_VICTORIA_OWNER_DRIVER_RATE_SCHEDULE = "https://business.vic.gov.au/__data/assets/pdf_file/0005/1289903/1-Tonne-Van-Courier-Messenger-2020-21.pdf";

	TextView courierNameTxtProfile, moneyEarned,txtToMoneyEarnedDate;
	
	ProgressDialog progressForGetCourierProfile;

	JSONObject  courierProfileMainResponseJOBJ;

	private View rootView = null;

	TextView ratingInPercent;

	ImageView alcoholeSignatureUploaded, policeCheckUploaded;

	RoundedImageView profileImgAccountDetail;

	TextView slideMenuTxt, courierFirstLastName, thumbsUpTxt, thumbsDownTxt, customerRatingTxt;
	HashMap<String, Object> hashMapCourierProfileMainOBJ;

	private boolean allowNotificationToTeamMembers = true;
	private TextView summaryBtnProfileView, courierIdentificationButton, yourInformationBtn, settingBtn, sellCourierBusinessBtnSettings,
			victoriaOwnerDriverInfoBookletBtnSettings, victoriaOwnerDriverRateScheduleBtnSettings,invoice_adjustments;

	private TextView yourTeamMyProfile, alcoholDeliveryMyProfile, policeCheckMyProfile;

	public AccountDetailFragment(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
		StrictMode.setThreadPolicy(policy);
	}

//	public AccountDetailFragment(TextView slideMenuTxt){
//		try {
//			this.slideMenuTxt = slideMenuTxt;
//			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
//			StrictMode.setThreadPolicy(policy);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	public void setSlideMenuChatCounterTxt(TextView slideMenuTxt){
		this.slideMenuTxt = slideMenuTxt;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
			rootView = inflater.inflate(R.layout.myprofile_screen, container, false);
			if(savedInstanceState != null){
				try {
					ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItem");
					if(LoginZoomToU.NOVA_BOLD == null)
						LoginZoomToU.staticFieldInit(getActivity());
					inItAccountDetailView();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				inItAccountDetailView();
			}
        } catch (Exception e) {
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
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		try{
//			if(isSwitchToUpdatePass == false)
//				SlideMenuZoom2u.setCourierToOfflineFromChat();
			outState.putInt("SlideMenuItem", ConfirmPickUpForUserName.isDropOffSuccessfull);
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onSaveInstanceState(outState);
	}

	//************* Call get profile detail async task ***************
	private void callProfileDetailTask(){
		if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
			GetCourierProfileAsyncTask();
			//new GetCourierProfileAsyncTask().execute();
		else
			DialogActivity.alertDialogView(getActivity(), "No Network!", "No Network connection, Please try again later.");
	}

	/***************  In-it account detail view  ****************/
	void inItAccountDetailView(){
		try {
			TextView txtToMoneyEarned = (TextView) rootView.findViewById(R.id.txtToMoneyEarned);

			if (moneyEarned == null)
                moneyEarned = (TextView) rootView.findViewById(R.id.moneyEarned);
			txtToMoneyEarnedDate =(TextView) rootView.findViewById(R.id.txtToMoneyEarnedDate);
			if(courierNameTxtProfile == null)
				courierNameTxtProfile = (TextView) rootView.findViewById(R.id.courierNameTxtProfile);


			profileImgAccountDetail = (RoundedImageView) rootView.findViewById(R.id.profileImgAccountDetail);
			courierFirstLastName = (TextView) rootView.findViewById(R.id.courierFirstLastName);

			thumbsUpTxt = (TextView) rootView.findViewById(R.id.thumbsUpTxt);

			thumbsDownTxt = (TextView) rootView.findViewById(R.id.thumbsDownTxt);

			customerRatingTxt = (TextView) rootView.findViewById(R.id.customerRatingTxt);


			alcoholeSignatureUploaded = (ImageView) rootView.findViewById(R.id.alcoholeSignatureUploaded);
			policeCheckUploaded = (ImageView) rootView.findViewById(R.id.policeCheckUploaded);

			alcoholeSignatureUploaded.setVisibility(View.GONE);
			policeCheckUploaded.setVisibility(View.GONE);

			if (ratingInPercent == null)
				ratingInPercent = (TextView) rootView.findViewById(R.id.ratingInPercent);


			yourTeamMyProfile = (TextView) rootView.findViewById(R.id.yourTeamMyProfile);

			alcoholDeliveryMyProfile = (TextView) rootView.findViewById(R.id.alcoholDeliveryMyProfile);

			alcoholDeliveryMyProfile.setOnClickListener(this);

			policeCheckMyProfile = (TextView) rootView.findViewById(R.id.policeCheckMyProfile);

			policeCheckMyProfile.setOnClickListener(this);

			summaryBtnProfileView = (TextView) rootView.findViewById(R.id.summaryBtnProfileView);

			summaryBtnProfileView.setOnClickListener(this);

			courierIdentificationButton = (TextView) rootView.findViewById(R.id.courierIdentificationButton);

			courierIdentificationButton.setOnClickListener(this);

			invoice_adjustments = (TextView) rootView.findViewById(R.id.invoice_adjustments);

			invoice_adjustments.setOnClickListener(this);

			sellCourierBusinessBtnSettings = (TextView) rootView.findViewById(R.id.sellCourierBusinessBtnSettings);

			sellCourierBusinessBtnSettings.setOnClickListener(this);

			yourInformationBtn = (TextView) rootView.findViewById(R.id.yourInformationBtn);

			yourInformationBtn.setOnClickListener(this);

			settingBtn = (TextView) rootView.findViewById(R.id.settingBtn);

			if (LoginZoomToU.IS_TEAM_LEADER) {
				settingBtn.setVisibility(View.VISIBLE);
				settingBtn.setOnClickListener(this);
			} else {
				settingBtn.setVisibility(View.GONE);
			}

            victoriaOwnerDriverInfoBookletBtnSettings = (TextView) rootView.findViewById(R.id.victoriaOwnerDriverInfoBookletBtnSettings);

            victoriaOwnerDriverInfoBookletBtnSettings.setOnClickListener(this);

			victoriaOwnerDriverRateScheduleBtnSettings = (TextView) rootView.findViewById(R.id.victoriaOwnerDriverRateScheduleBtnSettings);

			victoriaOwnerDriverRateScheduleBtnSettings.setOnClickListener(this);

			callProfileDetailTask();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//  **** convert Map to jsonObject ***
//	 @SuppressWarnings("rawtypes")
//	public JSONObject convertMapToJson(Map map)throws JSONException{
//	  
//	  JSONObject obj = new JSONObject();
//	  JSONObject main = new JSONObject();
//	  Set set = map.keySet();
//	  
//	  Iterator iter = set.iterator();
//	  while (iter.hasNext()) {
//		     String key = (String) iter.next();
//		     obj.accumulate(key, map.get(key));
//	   }
//	   main.accumulate("data",obj);
//	   obj = null;
//	   iter = null;
//	   set = null;
//	   return main;
//	 }

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	//*********  Getting camera/gallery images		********//
	@SuppressWarnings({ "static-access", "deprecation" })
	public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		try {
			PushReceiver.isCameraOpen = false;
			if (requestCode == OPEN_SETTING_VIEW) {
				callProfileDetailTask();
			} else if (requestCode == OPEN_YOUR_INFO_PAGE) {
				if(hashMapCourierProfileMainOBJ != null)
					hashMapCourierProfileMainOBJ = null;
				hashMapCourierProfileMainOBJ = (HashMap<String, Object>) data.getSerializableExtra("ProfileDictionary");

				if (!hashMapCourierProfileMainOBJ.get("Photo").equals(""))
					Picasso.with(getActivity()).load((String) hashMapCourierProfileMainOBJ.get("Photo")).into(profileImgAccountDetail);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()){
			case R.id.invoice_adjustments:
				intent = new Intent(getActivity(), InvoiceAdjustments.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;
			case R.id.courierIdentificationButton:
				intent = new Intent(getActivity(), CourierIdentification_View.class);
				intent.putExtra("DetailDictionary", hashMapCourierProfileMainOBJ);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;
			case R.id.settingBtn:
				OPEN_SETTING_VIEW = 3;
				intent = new Intent(getActivity(), SettingView.class);
				try {
					try {
						intent.putExtra("Margin", courierProfileMainResponseJOBJ.getJSONObject("carrier").getDouble("Margin"));
					} catch (JSONException e) {
						e.printStackTrace();
						intent.putExtra("Margin", 0.0);
					}
					intent.putExtra("AllowMemberNotify", allowNotificationToTeamMembers);
				} catch (Exception e) {
					e.printStackTrace();
					intent.putExtra("Margin", 0.0);
					intent.putExtra("AllowMemberNotify", false);
				}
				startActivityForResult(intent, OPEN_SETTING_VIEW);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;
			case R.id.summaryBtnProfileView:
				intent = new Intent(getActivity(), SummaryReportView.class);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;

			case R.id.yourTeamMyProfile:
				if (LoginZoomToU.CARRIER_ID == 0) {
					DialogBecomeTeamLeadTermsCondition dialogBecomeTeamLeadTermsCondition = new DialogBecomeTeamLeadTermsCondition(getActivity(),R.style.Theme_Dialog);
			    	dialogBecomeTeamLeadTermsCondition.setOnDialogTermsConditionResponse(new DialogBecomeTeamLeadTermsCondition.OnDialogTermsConditionResponse() {
					@Override
					public void onSuccess() {
						allowNotificationToTeamMembers = true;
						settingBtn.setVisibility(View.VISIBLE);
						settingBtn.setOnClickListener(AccountDetailFragment.this);
					}
				});
				dialogBecomeTeamLeadTermsCondition.show();
				} else {
					intent = new Intent(getActivity(), TeamMemberList_Activity.class);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
					intent = null;
					break;
				}
				break;
            case R.id.sellCourierBusinessBtnSettings:
                Intent sellCourierBusinessItent = new Intent(getActivity(), ActivitySellYourCourierBusiness.class);
				startActivity(sellCourierBusinessItent);
                break;
            case R.id.victoriaOwnerDriverInfoBookletBtnSettings:
                Intent intentToVictoriaOwnerDriver = new Intent(Intent.ACTION_VIEW);
                intentToVictoriaOwnerDriver.setData(Uri.parse(URI_VICTORIA_OWNER_DRIVER_INFO_BOOKLET));
                startActivity(intentToVictoriaOwnerDriver);
                break;
			case R.id.victoriaOwnerDriverRateScheduleBtnSettings:
				Intent intentToVictoriaOwnerDriverRateSchedule = new Intent(Intent.ACTION_VIEW);
				intentToVictoriaOwnerDriverRateSchedule.setData(Uri.parse(URI_VICTORIA_OWNER_DRIVER_RATE_SCHEDULE));
				startActivity(intentToVictoriaOwnerDriverRateSchedule);
				break;
			case R.id.alcoholDeliveryMyProfile:
                OPEN_SETTING_VIEW = 3;
				intent = new Intent(getActivity(), AlcoholTrainingVideo_View.class);
				intent.putExtra("flag_does_alcohol_delivery", (Boolean) hashMapCourierProfileMainOBJ.get("DoesAlcoholDeliveries"));
				try {
					intent.putExtra("signatureImageURL", courierProfileMainResponseJOBJ.getString("alcoholUploadedImageUrl"));
					intent.putExtra("lastImgUploadDateTime", courierProfileMainResponseJOBJ.getString("alcoholUploadedImageDateTime"));
				} catch (JSONException e) {
					e.printStackTrace();
					intent.putExtra("signatureImageURL", "");
					intent.putExtra("lastImgUploadDateTime", "");
				}
				startActivityForResult(intent, OPEN_SETTING_VIEW);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;
			case R.id.policeCheckMyProfile:
                OPEN_SETTING_VIEW = 3;
				intent = new Intent(getActivity(), PoliceCheckView.class);
                startActivityForResult(intent, OPEN_SETTING_VIEW);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;

			case R.id.yourInformationBtn:
				OPEN_YOUR_INFO_PAGE = 604;
				intent = new Intent(getActivity(), MyProfile_Information.class);
				intent.putExtra("ProfileDictionary", hashMapCourierProfileMainOBJ);
				startActivityForResult(intent, OPEN_YOUR_INFO_PAGE);
				getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
				intent = null;
				break;
		}
	}


	private void GetCourierProfileAsyncTask(){

		final String[] courierProfileResponseStr = {"0"};
		final JSONArray[] responseArrayOfcourierProfileData = {null};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(progressForGetCourierProfile == null)
					progressForGetCourierProfile = new ProgressDialog(getActivity());
				Custom_ProgressDialogBar.inItProgressBar(progressForGetCourierProfile);
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					courierProfileResponseStr[0] = webServiceHandler.getCourierProfileDetails();

					webServiceHandler = null;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					try {
						courierProfileMainResponseJOBJ = new JSONObject(courierProfileResponseStr[0]);
						if(hashMapCourierProfileMainOBJ != null)
							hashMapCourierProfileMainOBJ = null;
						hashMapCourierProfileMainOBJ = new HashMap<String, Object>();

						ObjectMapper mapper = new ObjectMapper();
						String jObjStr = courierProfileMainResponseJOBJ.toString();
						HashMap<String, Object> tempHashMapCourierProfileMainOBJ = mapper.readValue(jObjStr, new TypeReference<HashMap<String, Object>>(){});
						hashMapCourierProfileMainOBJ = ((HashMap<String, Object>) tempHashMapCourierProfileMainOBJ.get("courier"));
						tempHashMapCourierProfileMainOBJ = null;

						mapper = null;
					} catch (JSONException e) {
						e.printStackTrace();
					}

					if(hashMapCourierProfileMainOBJ.size() > 0){

						courierNameTxtProfile.setText(hashMapCourierProfileMainOBJ.get("FirstName")+" "+
								hashMapCourierProfileMainOBJ.get("LastName"));

						try {
							if (hashMapCourierProfileMainOBJ.get("Rating") != null && !hashMapCourierProfileMainOBJ.get("Rating").equals("null")
									&& !hashMapCourierProfileMainOBJ.get("Rating").equals(""))
								try {
									ratingInPercent.setText(Functional_Utility.round((Double) hashMapCourierProfileMainOBJ.get("Rating"))+"%");
								} catch (Exception e) {
									e.printStackTrace();
									ratingInPercent.setText(hashMapCourierProfileMainOBJ.get("Rating")+"%");
								}
							else
								ratingInPercent.setText("0%");
						} catch (Exception e) {
							e.printStackTrace();
							ratingInPercent.setText("0%");
						}

						try {
							courierFirstLastName.setText(((String)((((String)hashMapCourierProfileMainOBJ.get("FirstName")).charAt(0)+""+((String) hashMapCourierProfileMainOBJ.get("LastName")).charAt(0)))).toUpperCase());
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if(!hashMapCourierProfileMainOBJ.get("Photo").equals("")) {
								Picasso.with(getActivity()).load((String) hashMapCourierProfileMainOBJ.get("Photo")).fit().centerInside().into(profileImgAccountDetail);
								courierFirstLastName.setVisibility(View.GONE);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						thumbsUpTxt.setText(""+hashMapCourierProfileMainOBJ.get("TotalThumbsUp"));
						thumbsDownTxt.setText(""+hashMapCourierProfileMainOBJ.get("TotalThumbsDown"));

						responseArrayOfcourierProfileData[0] = null;
						courierProfileResponseStr[0] = null;

						if ((Boolean) hashMapCourierProfileMainOBJ.get("DoesAlcoholDeliveries"))
							alcoholeSignatureUploaded.setVisibility(View.GONE);
						try {
							if (courierProfileMainResponseJOBJ.getBoolean("isPoliceCheckDocUploaded"))
								policeCheckUploaded.setVisibility(View.VISIBLE);
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							if (LoginZoomToU.IS_TEAM_LEADER || courierProfileMainResponseJOBJ.getJSONObject("carrier") == null) {
								yourTeamMyProfile.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
								yourTeamMyProfile.setOnClickListener(AccountDetailFragment.this);
								yourTeamMyProfile.setVisibility(View.VISIBLE);
								yourTeamMyProfile.setAlpha(1.0f);
								yourTeamMyProfile.setEnabled(true);
							} else {
								yourTeamMyProfile.setAlpha(0.3f);
								yourTeamMyProfile.setEnabled(false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
							yourTeamMyProfile.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
							yourTeamMyProfile.setOnClickListener(AccountDetailFragment.this);
							yourTeamMyProfile.setVisibility(View.VISIBLE);
							yourTeamMyProfile.setAlpha(1.0f);
							yourTeamMyProfile.setEnabled(true);
						}

						try {
							if (LoginZoomToU.CARRIER_ID==0|| LoginZoomToU.IS_TEAM_LEADER)
								invoice_adjustments.setVisibility(View.VISIBLE);
							else invoice_adjustments.setVisibility(View.GONE);
						}catch (Exception ex){

						}
					}

					if(progressForGetCourierProfile != null)
						if(progressForGetCourierProfile.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForGetCourierProfile);

					if(WebserviceHandler.jObjOfCurrentCourierLevel != null){
						try {
							moneyEarned.setText("$"+WebserviceHandler.jObjOfCurrentCourierLevel.getDouble("MoneyEarned")); //  Set text to money earned
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
                    if(WebserviceHandler.jObjOfCurrentCourierLevel != null){
						try {
							String startDate = LoginZoomToU.checkInternetwithfunctionality.getDateServer1(WebserviceHandler.jObjOfCurrentCourierLevel.getString("StartDate"));
							String toDate = LoginZoomToU.checkInternetwithfunctionality.getDateServer1(WebserviceHandler.jObjOfCurrentCourierLevel.getString("ToDate"));
							txtToMoneyEarnedDate.setText("Revenue from "+startDate+" to "+toDate);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					try {
						allowNotificationToTeamMembers = courierProfileMainResponseJOBJ.getJSONObject("carrier").getBoolean("AllowTeamMembersToReceiveOffers");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
					e.printStackTrace();
					if(progressForGetCourierProfile != null)
						if(progressForGetCourierProfile.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForGetCourierProfile);

					DialogActivity.alertDialogView(getActivity(), "Server Error!", "Please try later.");
					courierProfileResponseStr[0] = null;
					responseArrayOfcourierProfileData[0] = null;
				}

				setVictoriaOwnerDriverInfo();
			}
		}.execute();
	}

    private void setVictoriaOwnerDriverInfo() {
        try {

			JSONObject address =null;
			if(courierProfileMainResponseJOBJ!=null) {
				address = courierProfileMainResponseJOBJ.getJSONObject("courier").getJSONObject("Address");
			}

            if (address != null) {
                if (address.has("Province")) {
                    if (!address.getString("Province").equals("")) {
                        if (address.getString("Province").equalsIgnoreCase("VIC")
                                || address.getString("Province").equalsIgnoreCase("Victoria")) {
                            victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.VISIBLE);
							victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.VISIBLE);
                        }
                    } else if (address.has("Street1") && !address.getString("Street1").equals("")) {
                        if (address.getString("Street1").contains("VIC")
                                || address.getString("Street1").contains("Victoria")) {
                            victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.VISIBLE);
							victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.VISIBLE);
                        }
                    } else {
                        victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.GONE);
						victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.GONE);
                    }
                } else if (address.has("Street1") && !address.getString("Street1").equals("")) {
                    if (address.getString("Street1").contains("VIC")
                            || address.getString("Street1").contains("Victoria")) {
                        victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.VISIBLE);
						victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.VISIBLE);
                    }
                } else {
                    victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.GONE);
					victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.GONE);
                }
            } else {
                victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.GONE);
				victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            victoriaOwnerDriverInfoBookletBtnSettings.setVisibility(View.GONE);
			victoriaOwnerDriverRateScheduleBtnSettings.setVisibility(View.GONE);
        }
	}
}
