package com.zoom2u.dialogactivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.lateview.vc.LateDetailView;
import com.zoom2u.services.ServiceToGetCourierLevel;
import com.zoom2u.slidemenu.offerrequesthandlr.Alert_To_PlaceBid;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DialogActivity extends Activity{
	
	public static Dialog enterFieldDialog;
//	public static int notificationDialogCount = 0;
	Intent intent;

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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	//	notificationDialogCount++;
		this.intent = intent;
		showNotificationDialog();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialogview);
		PushReceiver.IsOtherScreenOpen =true;
		this.intent = getIntent();
		showNotificationDialog();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	private void showNotificationDialog() {
		
		if(LoginZoomToU.NOVA_BOLD == null)
			LoginZoomToU.staticFieldInit(DialogActivity.this);
		try{
			Intent serviceIntent = new Intent(DialogActivity.this, ServiceToGetCourierLevel.class);
			startService(serviceIntent);
			serviceIntent = null;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		try {
			TextView titleDialogDefaultNoti = (TextView) findViewById(R.id.titleDialog);
			titleDialogDefaultNoti.setText("Notification!");

				
			TextView dialogMessageTextDefaultNoti = (TextView) findViewById(R.id.dialogMessageText);

			Button dialogDoneBtn = (Button) findViewById(R.id.dialogDoneBtn);

			if (intent.getStringExtra("NotificationMessageStr") != null)
				dialogMessageTextDefaultNoti.setText(intent.getStringExtra("NotificationMessageStr"));
			else if(!PushReceiver.prefrenceForPushy.getString("NotificationMessage", "").equals(""))
			{
				if (!PushReceiver.prefrenceForPushy.getString("Request_Rejected_Id","").equals("")){

					try {
						rejectBidId=Integer.parseInt(PushReceiver.prefrenceForPushy.getString("Request_Rejected_Id","0"));
						getBidDetails(false,rejectBidId);
					}catch (Exception exception){
						exception.printStackTrace();
					}

					Button dialogYesButton = (Button) findViewById(R.id.dialogYesBtn);
					dialogYesButton.setVisibility(View.VISIBLE);

					//change the color UI for reject button
					dialogDoneBtn.setText("NO");

					dialogDoneBtn.setTextColor(getColor(R.color.red));
					dialogDoneBtn.setBackground(getDrawable(R.drawable.redical_cornerborder));

					//adding listener to redirect to place new bid request for rejected Bid
					dialogYesButton.setOnClickListener(v -> {
								if(quoteRequestOfCustomerDetails==null)
								{
									if(!LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
									{
										DialogActivity.alertDialogView(DialogActivity.this, "No network!", "No internet connection, Please try later");
									}
									else
									{
										getBidDetails(true,rejectBidId);
									}
								}
								else {
									startBidPlaceActivity();
									dialogDoneBtn.performClick();
								}


					});
				}

				dialogMessageTextDefaultNoti.setText(PushReceiver.prefrenceForPushy.getString("NotificationMessage", ""));
			}
//			else
//				dialogMessageTextDefaultNoti.setText("100 points added to your account");
			

			dialogDoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//	notificationDialogCount = 0;
					PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
			     	PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id", "");
					PushReceiver.loginEditorForPushy.commit();
					finish();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
			PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id", "");
			PushReceiver.loginEditorForPushy.commit();
		}
	}

	public static void alertDialogView(Context con, String alertTitle, String alertMsg){
		try{
			if(enterFieldDialog != null)
				if(enterFieldDialog.isShowing())
					enterFieldDialog.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			if(enterFieldDialog != null)
				enterFieldDialog = null;
				enterFieldDialog = new Dialog(con);
				enterFieldDialog.setCancelable(false);
				enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				enterFieldDialog.setContentView(R.layout.dialogview);

				Window window = enterFieldDialog.getWindow();
				window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				android.view.WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.CENTER;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);

				TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

				enterFieldDialogHEader.setText(alertTitle);

				TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

				enterFieldDialogMsg.setText(alertMsg);

				Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

				if(alertMsg.equals("Already a Member")) {
					enterFieldDialogDoneBtn.setText("Ok");
				}
				enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						enterFieldDialog.dismiss();
					}
				});
				enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    public static void alertDialogViewNew(Context con, String alertTitle, String alertMsg){
		try{
			if(enterFieldDialog != null)
				if(enterFieldDialog.isShowing())
					enterFieldDialog.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}

		try {
			if(enterFieldDialog != null)
				enterFieldDialog = null;
				enterFieldDialog = new Dialog(con);
				enterFieldDialog.setCancelable(false);
				enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
				enterFieldDialog.setContentView(R.layout.dialogviewboldmessage);

				Window window = enterFieldDialog.getWindow();
				window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				android.view.WindowManager.LayoutParams wlp = window.getAttributes();

				wlp.gravity = Gravity.CENTER;
				wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
				window.setAttributes(wlp);

				TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

				enterFieldDialogHEader.setText(alertTitle);

				TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

				enterFieldDialogMsg.setText(alertMsg);

				Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

				enterFieldDialogDoneBtn.setText("Ok");
				enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
						enterFieldDialog.dismiss();
					}
				});
				enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void alertDialogToFinishView(final Context con, String alertTitle, String alertMsg){
		try{
			if(enterFieldDialog != null)
				if(enterFieldDialog.isShowing())
					enterFieldDialog.dismiss();
		}catch(Exception e){
			e.printStackTrace();
		}

		try {
			if(enterFieldDialog != null)
				enterFieldDialog = null;
			enterFieldDialog = new Dialog(con);
			enterFieldDialog.setCancelable(false);
			enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			enterFieldDialog.setContentView(R.layout.dialogview);

			Window window = enterFieldDialog.getWindow();
			window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			android.view.WindowManager.LayoutParams wlp = window.getAttributes();

			wlp.gravity = Gravity.CENTER;
			wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
			window.setAttributes(wlp);

			TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

			enterFieldDialogHEader.setText(alertTitle);

			TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

			enterFieldDialogMsg.setText(alertMsg);

			Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

			enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					enterFieldDialog.dismiss();
					((Activity) con).finish();
				}
			});
			enterFieldDialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//call api to get the bid details
	RequestView_DetailPojo quoteRequestOfCustomerDetails;
	String shipmentItemDetails;
	ProgressDialog progressForDeliveryHistory;

	Boolean requestInProgress=false;

	int rejectBidId=0;
	private void getBidDetails(Boolean showProgress,int requestId){
		if (requestInProgress)
			return;
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				requestInProgress=true;
				try {
					if(showProgress&&progressForDeliveryHistory == null)
						progressForDeliveryHistory = new ProgressDialog(DialogActivity.this);
					Custom_ProgressDialogBar.inItProgressBar(progressForDeliveryHistory);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
			  	WebserviceHandler webserviceHandler=new WebserviceHandler();
				String response  = webserviceHandler.getQuoteRequestOfCustomerDetails(requestId);
				 if (response!=null){
					 try {
						 JSONObject jsonObjectOflIstDelivery=new JSONObject(response);
						 JSONArray getDeliverArray = jsonObjectOflIstDelivery.getJSONArray("data");
						 JSONObject objDelivery = getDeliverArray.getJSONObject(0);

						RequestView_DetailPojo requestView_pojo=new RequestView_DetailPojo();
						 requestView_pojo.setOfferId(objDelivery.getInt("OfferId"));
						 requestView_pojo.setCustomer(objDelivery.getString("Customer"));
						 requestView_pojo.setCustomerID(objDelivery.getString("CustomerId"));
						 requestView_pojo.setPickupSuburb(objDelivery.getString("PickupSuburb"));
						 requestView_pojo.setDropSuburb(objDelivery.getString("DropSuburb"));
						 requestView_pojo.setCancel(objDelivery.getBoolean("IsCancel"));
						 requestView_pojo.setAverageBid(objDelivery.getString("AverageBid"));
						 requestView_pojo.setVehicle(objDelivery.getString("Vehicle"));
						 requestView_pojo.setCustomerPhoto(objDelivery.getString("CustomerPhoto"));

						 String PickupDateTime=LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDelivery.getString("PickupDateTime"));
						 String[] PickupDateTimeSplit=PickupDateTime.split(" ");
						 requestView_pojo.setPickUpDateTime(PickupDateTimeSplit[0]+" | "+ PickupDateTimeSplit[1]+" "+PickupDateTimeSplit[2]);
						 PickupDateTimeSplit = null;

						 String dropOffDateTime = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(objDelivery.getString("DropDateTime"));
						 String[] dropOffDateTimeSplit = dropOffDateTime.split(" ");
						 requestView_pojo.setDropDateTime(dropOffDateTimeSplit[0]+ " | " + dropOffDateTimeSplit[1]+" "+dropOffDateTimeSplit[2]);
						 dropOffDateTimeSplit = null;

						 requestView_pojo.setNotes(objDelivery.getString("Notes"));
						 requestView_pojo.setId(objDelivery.getInt("RequestId"));
						 requestView_pojo.setTotalBids(objDelivery.getInt("TotalBids"));
						 requestView_pojo.setDistance(objDelivery.getString("Distance"));
						 requestView_pojo.setMinPrice(Functional_Utility.round(objDelivery.getDouble("MinPrice")));
						 requestView_pojo.setMaxPrice(Functional_Utility.round(objDelivery.getDouble("MaxPrice")));

						 try {
							 requestView_pojo.setCourierPrice(objDelivery.getDouble("CourierPrice"));
						 } catch (JSONException e) {
							 e.printStackTrace();
							 requestView_pojo.setCourierPrice(0.0);
						 }
						 try {
							 requestView_pojo.setSuggestedPrice(objDelivery.getBoolean("IsSuggestedPrice"));
						 } catch (JSONException e) {
							 e.printStackTrace();
							 requestView_pojo.setSuggestedPrice(false);
						 }

						 String offerPrice = null;
						 try {
							 offerPrice = "";
							 offerPrice = objDelivery.getJSONObject("OfferDetails").getString("Price");
							 int offerId = objDelivery.getJSONObject("OfferDetails").getInt("OfferId");
							 requestView_pojo.setOfferId(offerId);
						 } catch (JSONException e) {
							 e.printStackTrace();
						 }

						 requestView_pojo.setOfferPrice(offerPrice);

						 ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
						 for (int k = 0; k < objDelivery.getJSONArray("Shipments").length(); k++) {
							 HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
							 JSONObject jObjOfShipmentItem = objDelivery.getJSONArray("Shipments").getJSONObject(k);
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
						 requestView_pojo.setShipmentsArray(arrayOfShipments);
						 requestView_pojo.setPrimaryPackageImage(objDelivery.getString("PrimaryPackageImage"));

						quoteRequestOfCustomerDetails=requestView_pojo;
					 } catch (JSONException e) {
						 e.printStackTrace();
					 }
				 }
			}

			@Override
			public void onPostExecute() {
				requestInProgress=false;

				if (quoteRequestOfCustomerDetails!=null){
					try {
						try {
							StringBuilder stringBuilder=new StringBuilder();
							if (quoteRequestOfCustomerDetails.getShipmentsArray().size() > 0) {
								for (int i = 0; i < quoteRequestOfCustomerDetails.getShipmentsArray().size(); i++) {
									if (i == 0)
										stringBuilder.append("" + quoteRequestOfCustomerDetails.getShipmentsArray().get(i).get("Category")+" ("+quoteRequestOfCustomerDetails.getShipmentsArray().get(i).get("Quantity")+")");
									else
										stringBuilder.append(", " + quoteRequestOfCustomerDetails.getShipmentsArray().get(i).get("Category")+" ("+quoteRequestOfCustomerDetails.getShipmentsArray().get(i).get("Quantity")+")");
								}

								shipmentItemDetails=stringBuilder.toString();
							}
						}catch (Exception ex){
							ex.printStackTrace();
						}

						if (showProgress&&progressForDeliveryHistory!=null)
						{
							Custom_ProgressDialogBar.dismissProgressBar(progressForDeliveryHistory);
							startBidPlaceActivity();
						}
					}catch (Exception ex){
						ex.printStackTrace();
					}
				}
			}
		}.execute();
	}

	private void startBidPlaceActivity(){
		try {
			Intent placeBidView = new Intent(DialogActivity.this, Alert_To_PlaceBid.class);
			placeBidView.putExtra("isFromBidDetail", 1);

			if (shipmentItemDetails!=null)
			{
				placeBidView.putExtra("shipmentItems", shipmentItemDetails);
			}
			placeBidView.putExtra("requestView_detailPojo", quoteRequestOfCustomerDetails);
			startActivity(placeBidView);
			PushReceiver.loginEditorForPushy.putString("NotificationMessage", "");
			PushReceiver.loginEditorForPushy.putString("Request_Rejected_Id", "");
			PushReceiver.loginEditorForPushy.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
