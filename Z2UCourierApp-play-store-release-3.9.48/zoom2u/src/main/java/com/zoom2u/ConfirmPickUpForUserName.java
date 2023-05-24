package com.zoom2u;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;

import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogReasonForLateDelivery;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.signaturefail.DBHelperForSignatureFailure;
import com.zoom2u.utility.signaturefail.SignatureImg_DB_Model;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class ConfirmPickUpForUserName extends Activity implements AdapterView.OnItemSelectedListener {

	TextView titleConfirmPickName, countChatConfirmPicUp, userNameConfirmPickTxt, lastNameTxtSignature, contactTxt, confirmPickContactName, confirmPickContactAddress;
	EditText edtConfirmPickUserName, confirmPickUserLastNameTxt;
	ImageView backFromConfirmPick, confirmPicUpChatIcon;
	Spinner spinnerForPkgPosition;
	
	TextView titleConfirmPickSignature, pickupnameConfirmPickSignature;
	ImageView clearSignature;
	Button doneBtnConfirmPickSignature;

	RelativeLayout headerViewToShowContactInfo;

	//*********** Header view for Return to DHL bookings only
	RelativeLayout returnDHLBookingHeader;
	//*********** Header view for Return to DHL bookings only

	LinearLayout mContent;
	RelativeLayout belowSignature;

	Signature mSignature;
    public static String tempDir;
    public String current = null;
    public static Bitmap mBitmap;
    
    View mView;
    File mypath;

	String uniqueId;

    public static int isDropOffSuccessfull = 11;
    
    ProgressDialog progressForRequestPickETA;
    
    int takePhotoId;
    int dataFromActiveList;
    LinearLayout sign_here;
	String userName, etaForDropOffStr;
	Dialog alertDialog;
	boolean isActionBarPickup = false;

    int signeePosition;

	boolean isReturnedToDHL = false;
	int returnToDHL = 0;

	private All_Bookings_DataModels activeJobModelToPickOrDrop;
	private int positionActiveFragment;
	private ArrayList<String> pieceArrayFromListOrDetail;
	public static boolean isSignatureImageFromDB = false;

	//*********** To show in Return Delivery ***************
	public static int totalDroppedOff;
	public static int totalPickedUp;
	public static int totalToReturn;

	//**************** When attempt for Returned to pickup
	private int isForReturnToPickup;
	private String reasonFor_ReturnedToPickup;
	private int dropDownItem_ReturnedToPickup;
    RelativeLayout linearLayout1;
	TextView totalDeliveredCountTxt, pickedUpCountTxt, toReturnCountTxt;
	Window window;

	@Override
	protected void onPause() {
		super.onPause();
		PushReceiver.IsSignatureScreenOpen =false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		PushReceiver.IsSignatureScreenOpen =true;
	}

	@SuppressWarnings("deprecation")



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirmpickup_for_username);
		PushReceiver.IsSignatureScreenOpen =true;
		linearLayout1=findViewById(R.id.linearLayout1);
		window = ConfirmPickUpForUserName.this.getWindow();
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
		if(MainActivity.isIsBackGroundGray()){
			linearLayout1.setBackgroundResource(R.color.base_color_gray);
			window.setStatusBarColor(Color.parseColor("#374350"));
		}else{
			linearLayout1.setBackgroundResource(R.color.base_color1);
			window.setStatusBarColor(Color.parseColor("#00A6E2"));
		}

		try {
			mBitmap = null;
			isSignatureImageFromDB = false;
			Intent userNameIntent = getIntent();
			dataFromActiveList = userNameIntent.getExtras().getInt("dataFromActiveList", 0);
			etaForDropOffStr = userNameIntent.getStringExtra("dropOffEta");

			headerViewToShowContactInfo = (RelativeLayout) findViewById(R.id.linearLayout2);
			returnDHLBookingHeader = (RelativeLayout) findViewById(R.id.returnDHLBookingHeader);

			if(dataFromActiveList == 11){
				positionActiveFragment = userNameIntent.getIntExtra("positionActiveFragment", 0);
				activeJobModelToPickOrDrop = (All_Bookings_DataModels) BookingView.bookingListArray.get(positionActiveFragment);
				ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf((Integer) activeJobModelToPickOrDrop.getBookingId());
				takePhotoId = userNameIntent.getIntExtra("cameraPic", 0);
				isActionBarPickup = userNameIntent.getBooleanExtra("isActionBarButtonTaped", false);
						
				if(activeJobModelToPickOrDrop.getStatus().equals("On Route to Pickup"))
					ActiveBookingDetail_New.isPickBtn = false;
				else if(activeJobModelToPickOrDrop.getStatus().equals("On Route to Dropoff")
						|| activeJobModelToPickOrDrop.getStatus().equals("Tried to deliver")
						|| activeJobModelToPickOrDrop.getStatus().equals("Delivery Attempted"))
					ActiveBookingDetail_New.isPickBtn = true;
			} else {
				activeJobModelToPickOrDrop = userNameIntent.getParcelableExtra("ActiveBookingModel");
				takePhotoId = userNameIntent.getIntExtra("cameraPic", 0);
				returnToDHL = userNameIntent.getIntExtra("IsReturnedToDHL", 0);
				if (returnToDHL == 0)
					isReturnedToDHL = false;
				else {
					isReturnedToDHL = true;
					returnDHLBookingHeader.setVisibility(View.VISIBLE);
					headerViewToShowContactInfo.setVisibility(View.GONE);

					TextView returnBookingHeaderTxt = (TextView) findViewById(R.id.returnBookingHeaderTxt);

					TextView totalDeliveredTxtHeader = (TextView) findViewById(R.id.totalDeliveredTxtHeader);

					totalDeliveredCountTxt = (TextView) findViewById(R.id.totalDeliveredCountTxt);

					TextView pickedUpDeliveryHeaderTxt = (TextView) findViewById(R.id.pickedUpDeliveryHeaderTxt);

					pickedUpCountTxt = (TextView) findViewById(R.id.pickedUpCountTxt);


					TextView toReturnHeaderTxt = (TextView) findViewById(R.id.toReturnHeaderTxt);

					toReturnCountTxt = (TextView) findViewById(R.id.toReturnCountTxt);


					TextView defaultTxtForLineConfirmDrp = (TextView) findViewById(R.id.defaultTxtForLineConfirmDrp);
					RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 1);
					param.topMargin = 7;
					param.addRule(RelativeLayout.BELOW, R.id.returnDHLBookingHeader);
					defaultTxtForLineConfirmDrp.setLayoutParams(param);

					//************** Get total drop off and to return counts for DHL deliveries ********
					if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
						GetTotalDropAndToReturnCount();
						//new GetTotalDropAndToReturnCount().execute();
					else
						DialogActivity.alertDialogView(ConfirmPickUpForUserName.this, "No network!", "No network connection, Please check your connection");
				}
			}

			isForReturnToPickup = userNameIntent.getIntExtra("IsForReturnToPickup", 0);
			if (isForReturnToPickup == 1) {
				ActiveBookingDetail_New.isPickBtn = true;
				reasonFor_ReturnedToPickup = userNameIntent.getStringExtra("ReasonFor_ReturnedToPickup");
				dropDownItem_ReturnedToPickup = userNameIntent.getIntExtra("DropDownItem_ReturnedToPickup", 0);
			}

			try {
				pieceArrayFromListOrDetail = userNameIntent.getStringArrayListExtra("PieceArray");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if(titleConfirmPickName == null)
			titleConfirmPickName = (TextView) findViewById(R.id.titleConfirmPicUpUserName);

		if(countChatConfirmPicUp == null)
			countChatConfirmPicUp = (TextView) findViewById(R.id.countChatConfirmPicUp);

		countChatConfirmPicUp.setVisibility(View.GONE);
		SlideMenuZoom2u.countChatBookingView = countChatConfirmPicUp;

		if(confirmPicUpChatIcon == null)
			confirmPicUpChatIcon = (ImageView) findViewById(R.id.confirmPicUpChatIcon);
		confirmPicUpChatIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent chatViewIntent = new Intent(ConfirmPickUpForUserName.this, ChatViewBookingScreen.class);
				startActivity(chatViewIntent);
				chatViewIntent = null;
			}
		});

        if(contactTxt == null)
            contactTxt = (TextView) findViewById(R.id.contactTxt);

        if(confirmPickContactName == null)
            confirmPickContactName = (TextView) findViewById(R.id.confirmPickContactName);

        if(confirmPickContactAddress == null)
            confirmPickContactAddress = (TextView) findViewById(R.id.confirmPickContactAddress);


        if(lastNameTxtSignature == null)
            lastNameTxtSignature = (TextView) findViewById(R.id.lastNameTxtSignature);

        if(confirmPickUserLastNameTxt == null)
            confirmPickUserLastNameTxt = (EditText) findViewById(R.id.confirmPickUserLastNameTxt);

		if(edtConfirmPickUserName == null)
			edtConfirmPickUserName = (EditText) findViewById(R.id.edtConfirmPickUserName);

		if(userNameConfirmPickTxt == null)
			userNameConfirmPickTxt = (TextView) findViewById(R.id.confirmPickUserNameTxt);

			
		if(backFromConfirmPick == null)
			backFromConfirmPick = (ImageView) findViewById(R.id.backFromConfirmPicUpUserName);
		if(belowSignature == null)
			belowSignature = (RelativeLayout) findViewById(R.id.belowSignature);
			//sign_here=findViewById(R.id.sign_here);
		if(mContent == null)
			mContent = (LinearLayout) findViewById(R.id.signatureLayout);

			
		if(pickupnameConfirmPickSignature == null)
			pickupnameConfirmPickSignature = (TextView) findViewById(R.id.confirmPickUserName);


		if (isForReturnToPickup == 1) {
			titleConfirmPickName.setText("Return to Pickup");
			confirmPickContactName.setText(""+activeJobModelToPickOrDrop.getPick_ContactName());
			confirmPickContactAddress.setText(""+activeJobModelToPickOrDrop.getPick_Address());
		} else if(isActionBarPickup == false){
			if (returnToDHL == 1) {
				titleConfirmPickName.setText("Return to DHL");
				confirmPickContactName.setText(""+activeJobModelToPickOrDrop.getPick_ContactName());
				confirmPickContactAddress.setText(""+activeJobModelToPickOrDrop.getPick_Address());
			}else if(activeJobModelToPickOrDrop.getStatus().equals("On Route to Pickup")){
					titleConfirmPickName.setText("Confirm pickup");
					confirmPickContactName.setText(""+activeJobModelToPickOrDrop.getPick_ContactName());
					confirmPickContactAddress.setText(""+activeJobModelToPickOrDrop.getPick_Address());
			}else if(activeJobModelToPickOrDrop.getStatus().equals("On Route to Dropoff")
					|| activeJobModelToPickOrDrop.getStatus().equals("Tried to deliver")
					|| activeJobModelToPickOrDrop.getStatus().equals("Delivery Attempted")){
					titleConfirmPickName.setText("Confirm delivery");
					confirmPickContactName.setText(""+activeJobModelToPickOrDrop.getDrop_ContactName());
					confirmPickContactAddress.setText(""+activeJobModelToPickOrDrop.getDrop_Address());
			}
		} else {
            titleConfirmPickName.setText("Confirm pickup");
            confirmPickContactName.setText(""+activeJobModelToPickOrDrop.getPick_ContactName());
            confirmPickContactAddress.setText(""+activeJobModelToPickOrDrop.getPick_Address());
        }

		if(clearSignature == null)
			clearSignature = (ImageView) findViewById(R.id.clearSignature);
			clearSignature.setVisibility(View.GONE);
		if(doneBtnConfirmPickSignature == null)
			doneBtnConfirmPickSignature = (Button) findViewById(R.id.doneConfirmPickBtn);

		//********* Create temp file to save signature image *************
			try {
				ContextWrapper cw = new ContextWrapper(getApplicationContext());
				// make temporary directory for save image to sdcard
				File directory;
				if (LoginZoomToU.isImgFromInternalStorage) {
                    directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                    File mypath=new File(directory,"signatureImg.jpg");
                    tempDir = mypath.getAbsolutePath();
                }else {
                    tempDir = Environment.getExternalStorageDirectory() + "/" + getResources().getString(R.string.external_dir) + "/";
                    if (!prepareDirectory()) {                // create directory
                        directory = cw.getDir("Z2U_imageDir", Context.MODE_PRIVATE);
                        File mypath=new File(directory,"signatureImg.jpg");
                        tempDir = mypath.getAbsolutePath();
                        prepareDirectory();
                    }else
                        directory = cw.getDir(getResources().getString(R.string.external_dir), Context.MODE_PRIVATE);
                }

				uniqueId = getTodaysDate() + "_" + getCurrentTime() + "_" + Math.random();		// Set image name with uniqueID
				current = uniqueId + ".png";
				mypath= new File(directory,current);
			} catch (Exception e) {
				e.printStackTrace();
			}
			//********* Create temp file to save signature image *************

		mSignature = new Signature(ConfirmPickUpForUserName.this, null);
	    mContent.addView(mSignature, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	    mView = mContent;
		
		backFromConfirmPick.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			PushReceiver.isCameraOpen = false;
			finish();
			}
		});
		
		// erase signature
		clearSignature.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
						mSignature.clear();
						clearSignature.setVisibility(View.GONE);
						//sign_here.setVisibility(View.VISIBLE);
				}
			});
		
		doneBtnConfirmPickSignature.setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View arg0) {
				try {
					userName = edtConfirmPickUserName.getText().toString().trim()+" "+confirmPickUserLastNameTxt.getText().toString().trim();
					if (isActionBarPickup || !ActiveBookingDetail_New.isPickBtn || returnToDHL == 1) {
						if (edtConfirmPickUserName.getText().toString().equals(""))
							showValidationDialog("Please fill first name", ConfirmPickUpForUserName.this);
						else
							checkSignatureValidation();

					}else{
						if (edtConfirmPickUserName.getText().toString().equals("") || confirmPickUserLastNameTxt.getText().toString().equals(""))
							showValidationDialog("Please fill first and last name", ConfirmPickUpForUserName.this);
						else
							checkSignatureValidation();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
   			  }

			//********** Check signature validation before upload ***********
			private void checkSignatureValidation() {
				int usernameLength = edtConfirmPickUserName.getText().toString().length()+confirmPickUserLastNameTxt.getText().toString().length();
				if (usernameLength <= 2)
					alertMessage("Error message!", "The signature needs to contain at least 1 initial and 1 full name.\n" +
							"eg. 'John Smith', 'J Smith', or 'John S'", false);
				else if (activeJobModelToPickOrDrop.getPick_ContactName().equalsIgnoreCase("Telstra")
						&& activeJobModelToPickOrDrop.getDropIdentityNumber().length() > 0
						&& !userName.equalsIgnoreCase(confirmPickContactName.getText().toString())
						&& (activeJobModelToPickOrDrop.getStatus().equals("On Route to Dropoff")
						|| activeJobModelToPickOrDrop.getStatus().equals("Tried to deliver"))) {
					if (isForReturnToPickup == 1)
						captureSignatureToSentItToServer();
					else
						alertMessage("Error message!", "Oops! That name seems to be incorrect. This Status cannot be received by anyone other than the consignee. If they’re not available, please contact Zoom2u", false);
				} else if (userName.equalsIgnoreCase(LoginZoomToU.courierName))
					alertMessage("Error message!", "Please collect signature from the customer.\n" +
							"If the customer is not available, contact Zoom2u", false);
				else if (!activeJobModelToPickOrDrop.isATL()) {
					if (edtConfirmPickUserName.getText().toString().equalsIgnoreCase("atl") || confirmPickUserLastNameTxt.getText().toString().equalsIgnoreCase("atl")
							|| edtConfirmPickUserName.getText().toString().equalsIgnoreCase("nsr") || confirmPickUserLastNameTxt.getText().toString().equalsIgnoreCase("nsr")
							|| edtConfirmPickUserName.getText().toString().equalsIgnoreCase("front door") || confirmPickUserLastNameTxt.getText().toString().equalsIgnoreCase("front door"))
						alertMessage("Error message!", "ATL has not been provided for the booking", false);
					else
						captureSignatureToSentItToServer();
				}else
					captureSignatureToSentItToServer();
			}
		});
		
			spinnerForPkgPosition = (Spinner) findViewById(R.id.spinnerForPkgPosition);
			String[] pkgPosition = {"Recipient", "Family / Friend", "BM / Concierge", "Reception", "Colleague", "Mailroom", "Other"};
			ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(this, R.layout.spinneritemxml_new, pkgPosition) {
			     public View getView(int position, View convertView, ViewGroup parent) {
			             View v = super.getView(position, convertView, parent);
			             ((TextView) v).setTextColor(Color.parseColor("#808080"));
			             return v;
			     }
			     public View getDropDownView(int position,  View convertView,  ViewGroup parent) {
			              View v =super.getDropDownView(position, convertView, parent);
			             ((TextView) v).setTextColor(Color.parseColor("#808080"));
			             return v;
			     }
			};
			
			spinnerForPkgPosition.setAdapter(adapter_Position);
			pkgPosition = null;
			adapter_Position = null;
			spinnerForPkgPosition.setOnItemSelectedListener(this);
		
		} catch (Exception e) {
			e.printStackTrace();
		}

		ActiveBookingView.getCurrentLocation(ConfirmPickUpForUserName.this);
	}

	//*********** Capture signature image and sent it to server ***************//
	private void captureSignatureToSentItToServer() {
		boolean error = captureSignature();
		if(!error){
			LoginZoomToU.imm.hideSoftInputFromWindow(edtConfirmPickUserName.getWindowToken(), 0);
			//belowSignature.setBackgroundColor(Color.WHITE);
			mView.setBackgroundColor(Color.WHITE);
			mView.setDrawingCacheEnabled(true);
			mSignature.save(mView);
		}
	}

	public static void showValidationDialog(String validateStr, Context context){
        AlertDialog.Builder dialogBlankField = new AlertDialog.Builder(context);
        TextView myMsg = new TextView(context);
        myMsg.setText(validateStr);
        myMsg.setTextColor(Color.parseColor("#00A7E2"));
        myMsg.setPadding(20, 20, 20, 20);
        myMsg.setTextSize(15.0f);
        myMsg.setGravity(Gravity.CENTER_HORIZONTAL);
        dialogBlankField.setView(myMsg);
        dialogBlankField.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog d = dialogBlankField.create();
        d.show();
    }


	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,long id) {
        signeePosition = position;
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	//	SlideMenuZoom2u.setCourierToOfflineFromChat();
        outState.putInt("dataFromActiveList", dataFromActiveList);
        outState.putString("etaForDropOffStr", etaForDropOffStr);
        outState.putInt("cameraPic", takePhotoId);
        outState.putString("userName", getIntent().getStringExtra("userName"));
        outState.putInt("positionActiveFragment", positionActiveFragment);
        outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
        outState.putBoolean("Routific", WebserviceHandler.isRoutific);
    //    outState.putParcelableArrayList("ACTIVEBOOKINGARRAy", BookingView.bookingListArray);
		outState.putParcelable("ActiveBookingModel", activeJobModelToPickOrDrop);
        if(dataFromActiveList == 11)
            outState.putBoolean("isActionBarPickup", isActionBarPickup);
    }
 
	@Override
	protected void onResume() {
		super.onResume();
		SlideMenuZoom2u.setCourierToOnlineForChat();
		Model_DeliveriesToChat.showExclamationForUnreadChat(countChatConfirmPicUp);
		SlideMenuZoom2u.countChatBookingView = countChatConfirmPicUp;
	}
	
	 @Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onBackPressed(){
	     super.onBackPressed();
		PushReceiver.isCameraOpen = false;
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.confirm_pick_up_for_user_name, menu);
		return true;
	}

	private boolean captureSignature() {
        boolean error = false;
		try {
			String errorMessage = "";
			if(edtConfirmPickUserName.getText().toString().equalsIgnoreCase("")){
			    errorMessage = errorMessage + "Please enter your Name\n";
			    error = true;
			}  
			if(error){
			    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			    toast.setGravity(Gravity.CENTER, 105, 50);
			    toast.show();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return error;
    }
 
    private String getTodaysDate() {
        final Calendar c = Calendar.getInstance();
        int todaysDate =     (c.get(Calendar.YEAR) * 10000) +
        ((c.get(Calendar.MONTH) + 1) * 100) +
        (c.get(Calendar.DAY_OF_MONTH));
        return(String.valueOf(todaysDate));
    }
 
    private String getCurrentTime() {
        final Calendar c = Calendar.getInstance();
        int currentTime =     (c.get(Calendar.HOUR_OF_DAY) * 10000) +
        (c.get(Calendar.MINUTE) * 100) +
        (c.get(Calendar.SECOND));
        return(String.valueOf(currentTime));
    }
 
    private boolean prepareDirectory(){
        try {
            if (makedirs())
                return true;
            else
                return false;
        } catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "Could not initiate File System?", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
 
    private boolean makedirs() {
        File tempdir = new File(tempDir);
        if (!tempdir.exists())
            tempdir.mkdirs();
        if (tempdir.isDirectory()){
            File[] files = tempdir.listFiles();
            for (File file : files){
                if (!file.delete())
                    System.out.println("Failed to delete " + file);
            }
        }
        return (tempdir.isDirectory());
    }

    //************* Insert signature image to DB ***********
	private void insertSignatureImageDetailToDB() {
		DBHelperForSignatureFailure dbHelperForSignatureFailure = new DBHelperForSignatureFailure(this);
		String bookingIdStr;
		if(isActionBarPickup == true){
			bookingIdStr = "";
			Iterator<Integer> itr = ActiveBookingView.arrayOfBookingId.iterator();
			while(itr.hasNext()){
				bookingIdStr = bookingIdStr +itr.next() +",";
			}
			bookingIdStr = bookingIdStr.substring(0, (bookingIdStr.length()-1)) + "_PS";
		} else {
			if (ActiveBookingDetail_New.isPickBtn)
				bookingIdStr = String.valueOf((Integer) activeJobModelToPickOrDrop.getBookingId()) + "_DS";
			else
				bookingIdStr = String.valueOf((Integer) activeJobModelToPickOrDrop.getBookingId()) + "_PS";
		}

		mBitmap = BG_ImageUploadToServer.getPkgAndSignImgToAspectRatio(mBitmap, 600.0f, 600.0f);

		SignatureImg_DB_Model signatureImg_db_model = new SignatureImg_DB_Model(bookingIdStr, mBitmap);
		dbHelperForSignatureFailure.open();
		dbHelperForSignatureFailure.insertSignatureDetails(signatureImg_db_model);

		isSignatureImageFromDB = true;
		dbHelperForSignatureFailure.close();
		signatureImg_db_model = null;

		if (mBitmap == null) {
			mSignature.clear();
			showValidationDialog("Something went wrong when we try to get signature, Please try again.", ConfirmPickUpForUserName.this);
		} else {
			if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
				if (isForReturnToPickup == 1)
					RequestForReturnToPickUp();
					//new RequestForReturnToPickUp().execute();
				else if (activeJobModelToPickOrDrop.getSource().equals("DHL") || activeJobModelToPickOrDrop.getRunType().equals("SMARTSORT"))
					RequestForDropOrReturnOfDHLDeliveryAsyncTask();
					//new RequestForDropOrReturnOfDHLDeliveryAsyncTask().execute();
				else
					RequestForPickUpETAAsyncTask();
					//new RequestForPickUpETAAsyncTask().execute();
			} else
				DialogActivity.alertDialogView(ConfirmPickUpForUserName.this, "No Network !", "No Network connection, Please try again later.");
		}
	}

    public class Signature extends View  {
        private static final float STROKE_WIDTH = 3.0f;
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;
        private Paint paint = new Paint();
        private Path path = new Path();
 
        private float lastTouchX;
        private float lastTouchY;
        private final RectF dirtyRect = new RectF();
 
        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

		private void saveSignatureBitmap (View v){
			Canvas canvas = new Canvas(mBitmap);
			v.draw(canvas);

//			String FtoSave = tempDir + current;
//			File file = new File(FtoSave);
			try {
//				if (file.isFile() && file.exists()) {
//					FileOutputStream mFileOutStream = new FileOutputStream(file);
//					mBitmap.compress(Bitmap.CompressFormat.PNG, 90, mFileOutStream);
//					mFileOutStream.flush();
//					mFileOutStream.close();
//				} else
				insertSignatureImageDetailToDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void save(View v) {
        	try {
				if (mBitmap == null)
					mBitmap = Bitmap.createBitmap(mView.getWidth(), mView.getHeight(), Bitmap.Config.RGB_565);

				saveSignatureBitmap(v);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
 
        public void clear() {
            path.reset();
            invalidate();
        }
 
        @Override
        protected void onDraw(Canvas canvas)
        {
            canvas.drawPath(path, paint);
        }
 
        @Override
        public boolean onTouchEvent(MotionEvent event){
            try {
				LoginZoomToU.imm.hideSoftInputFromWindow(edtConfirmPickUserName.getWindowToken(), 0);
				float eventX = event.getX();
				float eventY = event.getY();
				clearSignature.setVisibility(View.VISIBLE);
 
				switch (event.getAction())
				{
				case MotionEvent.ACTION_DOWN:
				    path.moveTo(eventX, eventY);
				    lastTouchX = eventX;
				    lastTouchY = eventY;
				    return true;
 
				case MotionEvent.ACTION_MOVE:
 
				case MotionEvent.ACTION_UP:
 
				    resetDirtyRect(eventX, eventY);
				    int historySize = event.getHistorySize();
				    for (int i = 0; i < historySize; i++)
				    {
				        float historicalX = event.getHistoricalX(i);
				        float historicalY = event.getHistoricalY(i);
				        expandDirtyRect(historicalX, historicalY);
				        path.lineTo(historicalX, historicalY);
				    }
				    path.lineTo(eventX, eventY);
				    break;
 
				default:
				    return false;
				}
 
				invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
				        (int) (dirtyRect.top - HALF_STROKE_WIDTH),
				        (int) (dirtyRect.right + HALF_STROKE_WIDTH),
				        (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));
 
				lastTouchX = eventX;
				lastTouchY = eventY;
			} catch (Exception e) {
				e.printStackTrace();
			}
            return true;
        }
 
        private void expandDirtyRect(float historicalX, float historicalY)
        {
            try {
				if (historicalX < dirtyRect.left)
				    dirtyRect.left = historicalX;
				else if (historicalX > dirtyRect.right)
				    dirtyRect.right = historicalX;
 
				if (historicalY < dirtyRect.top)
				    dirtyRect.top = historicalY;
				else if (historicalY > dirtyRect.bottom)
				    dirtyRect.bottom = historicalY;
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
 
        private void resetDirtyRect(float eventX, float eventY)
        {
            try {
				dirtyRect.left = Math.min(lastTouchX, eventX);
				dirtyRect.right = Math.max(lastTouchX, eventX);
				dirtyRect.top = Math.min(lastTouchY, eventY);
				dirtyRect.bottom = Math.max(lastTouchY, eventY);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
    }


    private void RequestForPickUpETAAsyncTask(){

		final String[] requestPickResponseStr = {null};
		final boolean[] responseDataRequestPick = {false};
		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				try {
					if(progressForRequestPickETA != null)
						progressForRequestPickETA = null;
					progressForRequestPickETA = new ProgressDialog(ConfirmPickUpForUserName.this);
					Custom_ProgressDialogBar.inItProgressBar(progressForRequestPickETA);
					ActiveBookingView.getCurrentLocation(ConfirmPickUpForUserName.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if(isActionBarPickup == true){
						Map<String, Object> mapObject = new HashMap<String, Object>();
						mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
						mapObject.put("pickupPerson", userName);
						mapObject.put("signeesPosition", signeePosition);
						mapObject.put("notes", "");
						if(!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
								!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
							String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
							mapObject.put("location", currentLocation);
						}
						ObjectMapper objectMapper = new ObjectMapper();
						String bookingIdObj = objectMapper.writeValueAsString(mapObject);
						requestPickResponseStr[0] = webServiceHandler.pickUpMultipleBookings(bookingIdObj);
						try {
							String bookingStrForUploadImg = "";
							Iterator<Integer> itr = ActiveBookingView.arrayOfBookingId.iterator();
							while(itr.hasNext()){
								bookingStrForUploadImg = bookingStrForUploadImg +itr.next() +",";
							}
							Log.e("", "----------------  "+bookingStrForUploadImg);
							bookingStrForUploadImg = bookingStrForUploadImg.substring(0, (bookingStrForUploadImg.length()-1));
							Intent bgUploadImage = new Intent(ConfirmPickUpForUserName.this, BG_ImageUploadToServer.class);
							bgUploadImage.putExtra("isActionBarPickup", true);
							bgUploadImage.putExtra("isDropOffFromATL", false);
							bgUploadImage.putExtra("isDropoffBooking", false);
							bgUploadImage.putExtra("bookingIdStrForUploadImg", bookingStrForUploadImg);
							startService(bgUploadImage);
							ActiveBookingDetail_New.bookingIdActiveBooking = "";
							bgUploadImage = null;
						} catch (Exception e1) {
							// e1.printStackTrace();
						}
						JSONObject jOBJForRequestPick = new JSONObject(requestPickResponseStr[0]);
						responseDataRequestPick[0] = jOBJForRequestPick.getBoolean("success");
						jOBJForRequestPick = null;
						webServiceHandler = null;
						mapObject = null;
						objectMapper = null;
						ActiveBookingView.arrayOfBookingId.clear();
					}else{
						if(ActiveBookingDetail_New.isPickBtn) {
							if (returnToDHL == 1) {
								requestPickResponseStr[0] = webServiceHandler.returnedToDHL(userName, "", signeePosition,
										String.valueOf((Integer) activeJobModelToPickOrDrop.getBookingId()));
								WebserviceHandler.reCountActiveBookings(1, 2);
							} else {
								requestPickResponseStr[0] = webServiceHandler.requestForDropOffPersonName(userName, "", signeePosition,
										String.valueOf((Integer) activeJobModelToPickOrDrop.getBookingId()));
								WebserviceHandler.reCountActiveBookings(1, 2);
							}
						}else
							requestPickResponseStr[0] = webServiceHandler.requestForPickPersonName(userName, "", signeePosition
									, String.valueOf(activeJobModelToPickOrDrop.getBookingId()),
									etaForDropOffStr);

						try {
							JSONObject jOBJForRequestPick = new JSONObject(requestPickResponseStr[0]);
							Log.e("", "---------------- ***********  "+jOBJForRequestPick);
							responseDataRequestPick[0] = jOBJForRequestPick.getBoolean("success");
							jOBJForRequestPick = null;
							webServiceHandler = null;
						}catch (Exception e) {
							//  e.printStackTrace();
						}

						try {
							Intent bgUploadImage = new Intent(ConfirmPickUpForUserName.this, BG_ImageUploadToServer.class);
							bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
							bgUploadImage.putExtra("isActionBarPickup", false);
							bgUploadImage.putExtra("isDropOffFromATL", false);
							bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
							startService(bgUploadImage);
							ActiveBookingDetail_New.bookingIdActiveBooking = "";
							bgUploadImage = null;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if(ActiveBookingDetail_New.isPickBtn) {
						if(responseDataRequestPick[0]){
							new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
							if (activeJobModelToPickOrDrop.getSource().equals("DHL"))
								LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
							if (returnToDHL == 1) {
								switchToBookingView(2);
							} else {
								//*********** Sent battery info to admin on firebase ***************
								if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
									new Service_CheckBatteryLevel(ConfirmPickUpForUserName.this);
								//*********** Sent battery info to admin on firebase ***************
								switchToBookingView(2);
								//************** Check to show late delivery alert after drop off of Normal delivery ************
								try {
									if (!activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals("") && !activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals("null")
											&& !activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals(null)) {
										if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(activeJobModelToPickOrDrop.getDropDateTime(), activeJobModelToPickOrDrop.getFirstDropAttemptWasLate())
												&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
											callForLateReason();
											SlideMenuZoom2u.isDropOffCompleted=true;
										}
									} else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeJobModelToPickOrDrop.getDropDateTime())
											&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
										callForLateReason();
										SlideMenuZoom2u.isDropOffCompleted=true;
									}else{
										SlideMenuZoom2u.isDropOffCompleted=true;
										//Toast.makeText(ConfirmPickUpForUserName.this,"Well done!\nDropoff completed",Toast.LENGTH_LONG).show();
									}
								}
								catch (Exception e) {
									e.printStackTrace();
									if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeJobModelToPickOrDrop.getDropDateTime())
											&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
										callForLateReason();
									}
								}
							}
						}else
							alertMessage("Error!", "Status not uploaded, Please try again", true);
					}else{
						if(responseDataRequestPick[0]) {
							//*********** Sent battery info to admin on firebase ***************
							if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
								new Service_CheckBatteryLevel(ConfirmPickUpForUserName.this);
							switchToBookingView(2);
							SlideMenuZoom2u.isPickupOffCompleted=true;
							//Toast.makeText(ConfirmPickUpForUserName.this,"Well done!\nPickup completed",Toast.LENGTH_LONG).show();;
						}else
							alertMessage("Error!", "Status not uploaded, Please try again", true);
					}
				} catch (Exception e) {
					//e.printStackTrace();
					alertMessage("Server Error!", "Something went wrong. Please try again", true);
				}finally{
					try{
						if(progressForRequestPickETA != null)
							if(progressForRequestPickETA.isShowing())
								Custom_ProgressDialogBar.dismissProgressBar(progressForRequestPickETA);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.execute();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode==200&&resultCode==RESULT_OK){
			switchToBookingView(2);
		}
	}

	private void callForLateReason() {
		Intent callDialogReasonLate = new Intent(ConfirmPickUpForUserName.this, DialogReasonForLateDelivery.class);
		callDialogReasonLate.putExtra("fromReason",true);
		callDialogReasonLate.putExtra("ReasonLateBookingID", activeJobModelToPickOrDrop.getBookingId());
		callDialogReasonLate.putExtra("BookingTypeSource", activeJobModelToPickOrDrop.getSource());
		startActivityForResult(callDialogReasonLate,200);
		callDialogReasonLate = null;
	}

	private void RequestForDropOrReturnOfDHLDeliveryAsyncTask(){

		final String[] requestDropResponseStr = {null};
		boolean responseDataRequestForDropOff = false;

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(progressForRequestPickETA != null)
					progressForRequestPickETA = null;
				progressForRequestPickETA = new ProgressDialog(ConfirmPickUpForUserName.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForRequestPickETA);
				ActiveBookingView.getCurrentLocation(ConfirmPickUpForUserName.this);
			}

			@Override
			public void doInBackground() {
				try{
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					if (returnToDHL == 1) {
						ActiveBookingView.arrayOfBookingId = new HashSet<Integer>();
						ActiveBookingView.arrayOfBookingId.add(activeJobModelToPickOrDrop.getBookingId());
						Map<String, Object> mapObject = new HashMap<String, Object>();
						mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
						mapObject.put("recipientName", userName);
						mapObject.put("pieceBarcodes", pieceArrayFromListOrDetail);
						mapObject.put("signeesPosition", signeePosition);
						mapObject.put("notes", "Write some note here");
						mapObject.put("forceIncompleteDropOff", false);
						if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
								!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
							String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
							mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
							mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
						}
						ObjectMapper objectMapper = new ObjectMapper();
						String bookingsForReturnStr = objectMapper.writeValueAsString(mapObject);
						requestDropResponseStr[0] = webServiceHandler.multipleReturnDeliveries(0, bookingsForReturnStr);
						WebserviceHandler.reCountActiveBookings(ActiveBookingView.arrayOfBookingId.size(), 2);
					} else {
						Map<String, Object> mapObject = new HashMap<String, Object>();
						mapObject.put("bookingId", activeJobModelToPickOrDrop.getBookingId());
						mapObject.put("recipientName", userName);
						mapObject.put("pieceBarcodes", pieceArrayFromListOrDetail);
						mapObject.put("notes", "Write some note here");
						mapObject.put("signeesPosition", signeePosition);
						mapObject.put("forceIncompleteDropOff", false);
						if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
								!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
							String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
							mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
							mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
						}
						ObjectMapper objectMapper = new ObjectMapper();
						String requestDropoffParams = objectMapper.writeValueAsString(mapObject);;
						requestDropResponseStr[0] = webServiceHandler.dropDeliveryUsingBarcode(requestDropoffParams);
						if (LoginZoomToU.isLoginSuccess == 0) {
							if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
								WebserviceHandler.reCountActiveBookings(1, 2);
								Intent bgUploadImage = new Intent(ConfirmPickUpForUserName.this, BG_ImageUploadToServer.class);
								bgUploadImage.putExtra("isActionBarPickup", false);
								bgUploadImage.putExtra("isDropOffFromATL", false);
								bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
								bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
								ActiveBookingDetail_New.bookingIdActiveBooking = "";
								startService(bgUploadImage);
								bgUploadImage = null;
							}
						}
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					if (returnToDHL == 1) {
						new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
						LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
						if (LoginZoomToU.isLoginSuccess == 0) {
							try {
								JSONObject jObjOfReturnDeliveryResponse = new JSONObject(requestDropResponseStr[0]);
								if (jObjOfReturnDeliveryResponse.getJSONArray("validationErrors").length() <= 0) {
									try {
										Intent bgUploadImage = new Intent(ConfirmPickUpForUserName.this, BG_ImageUploadToServer.class);
										bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
										bgUploadImage.putExtra("isActionBarPickup", false);
										bgUploadImage.putExtra("isDropOffFromATL", false);
										bgUploadImage.putExtra("isDropoffBooking", ActiveBookingDetail_New.isPickBtn);
										startService(bgUploadImage);
										ActiveBookingDetail_New.bookingIdActiveBooking = "";
										bgUploadImage = null;
									} catch (Exception e1) {
										e1.printStackTrace();
									}

									Toast.makeText(ConfirmPickUpForUserName.this, "Returned successfully", Toast.LENGTH_LONG).show();
									switchToBookingView(2);
								} else
									alertMessage("Sorry!", "Error while returning, please try again", true);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						} else
							alertMessage("Sorry!", "Error while returning, please try again", true);
					} else {
						switch (LoginZoomToU.isLoginSuccess) {
							case 0:
								try {
									if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
										new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
										LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
										//*********** Sent battery info to admin on firebase ***************
										if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
											new Service_CheckBatteryLevel(ConfirmPickUpForUserName.this);
										//*********** Sent battery info to admin on firebase ***************
										switchToBookingView(2);
										//************** Check to show late delivery alert after dropoff of DHL delivery ************
										try {
											if (!activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals("") && !activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals("null")
													&& !activeJobModelToPickOrDrop.getFirstDropAttemptWasLate().equals(null)) {
												if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(activeJobModelToPickOrDrop.getDropDateTime(), activeJobModelToPickOrDrop.getFirstDropAttemptWasLate())
														&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
													SlideMenuZoom2u.isDropOffCompleted=true;
													callForLateReason();
												}
											} else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeJobModelToPickOrDrop.getDropDateTime())
													&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
												SlideMenuZoom2u.isDropOffCompleted=true;
												callForLateReason();
											}else
												SlideMenuZoom2u.isDropOffCompleted=true;
										} catch (Exception e) {
											e.printStackTrace();
											if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(activeJobModelToPickOrDrop.getDropDateTime())
													&& activeJobModelToPickOrDrop.getLateReasonId() == 0) {
												callForLateReason();
											}
										}
									} else
										alertMessage("Error!", "Status not uploaded, Please try again.", true);
								}catch (Exception e) {
									alertMessage("Error!", "Status not uploaded, Please try again.", true);
								}
								break;

							case 1:
								DialogActivity.alertDialogView(ConfirmPickUpForUserName.this, "No network!", "No network connection, Please check your connection and try again");
								break;
							default:
								alertMessage("Server Error!", "Something went wrong. Please try again.", true);
								break;
						}
					}
				} catch (Exception e) {
					//e.printStackTrace();
					alertMessage("Server Error!", "Something went wrong. Please try again.", true);
					requestDropResponseStr[0] = null;
				}finally{
					LoginZoomToU.isLoginSuccess = 0;
					try{
						if(progressForRequestPickETA != null)
							if(progressForRequestPickETA.isShowing())
								Custom_ProgressDialogBar.dismissProgressBar(progressForRequestPickETA);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.execute();

	}


	private void RequestForReturnToPickUp(){
		final String[] requestDropResponseStr = {null};
		boolean responseDataRequestForDropOff = false;

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(progressForRequestPickETA != null)
					progressForRequestPickETA = null;
				progressForRequestPickETA = new ProgressDialog(ConfirmPickUpForUserName.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForRequestPickETA);
				ActiveBookingView.getCurrentLocation(ConfirmPickUpForUserName.this);
			}

			@Override
			public void doInBackground() {
				try{
					ActiveBookingView.arrayOfBookingId = new HashSet<Integer>();
					ActiveBookingView.arrayOfBookingId.add(activeJobModelToPickOrDrop.getBookingId());
					Map<String, Object> mapObject = new HashMap<String, Object>();

					mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
					mapObject.put("notes", "Write some note here");
					mapObject.put("signeesPosition", signeePosition);
					mapObject.put("returnToPickupReason", dropDownItem_ReturnedToPickup);
					mapObject.put("returnToPickupNotes", reasonFor_ReturnedToPickup);

					//*************** Return to Pickup for Normal bookings
					if (activeJobModelToPickOrDrop.getSource().equals("DHL")
							|| activeJobModelToPickOrDrop.getRunType().equals("SMARTSORT")) {
						mapObject.put("recipientName", userName);
						mapObject.put("pieceBarcodes", pieceArrayFromListOrDetail);
						mapObject.put("forceIncompleteDropOff", false);
						if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
								!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
							String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
							mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
							mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
						}
						requestDropResponseStr[0]=callReturnToPickupAPI(1, mapObject);
					} else {
						mapObject.put("recipient", userName);
						if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
								!LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
							String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
							mapObject.put("location", String.valueOf(LoginZoomToU.getCurrentLocatnlatitude) + "," + String.valueOf(LoginZoomToU.getCurrentLocatnLongitude));
						}
						requestDropResponseStr[0]=callReturnToPickupAPI(2, mapObject);
					}
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try {
					new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
					LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
					if (LoginZoomToU.isLoginSuccess == 0) {
						try {
							if (activeJobModelToPickOrDrop.getSource().equals("DHL") || activeJobModelToPickOrDrop.getRunType().equals("SMARTSORT")) {
								JSONObject jObjOfReturnDeliveryResponse = new JSONObject(requestDropResponseStr[0]);
								if (jObjOfReturnDeliveryResponse.getJSONArray("validationErrors").length() <= 0) {
									uploadImageAndSignature();
									new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
									LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
									Toast.makeText(ConfirmPickUpForUserName.this, "Returned successfully", Toast.LENGTH_LONG).show();
									switchToBookingView(2);
								} else
									alertMessage("Sorry!", "Error while returning, please try again", true);
							} else {
								if (new JSONObject(requestDropResponseStr[0]).getBoolean("success")) {
									uploadImageAndSignature();
									new LoadChatBookingArray(ConfirmPickUpForUserName.this, 0);
									LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(activeJobModelToPickOrDrop.getBookingId());
									Toast.makeText(ConfirmPickUpForUserName.this, "Returned to pickup successfully", Toast.LENGTH_LONG).show();
									switchToBookingView(2);
								} else
									alertMessage("Server Error!", "Something went wrong. Please try again.", true);
							}
						} catch (Exception e1) {
							e1.printStackTrace();
							alertMessage("Server Error!", "Something went wrong. Please try again.", true);
						}
					} else
						alertMessage("Sorry!", "Error while returning, please try again", true);
				} catch (Exception e) {
					//e.printStackTrace();
					alertMessage("Server Error!", "Something went wrong. Please try again.", true);
					requestDropResponseStr[0] = null;
				}finally{
					LoginZoomToU.isLoginSuccess = 0;
					try{
						if(progressForRequestPickETA != null)
							if(progressForRequestPickETA.isShowing())
								Custom_ProgressDialogBar.dismissProgressBar(progressForRequestPickETA);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.execute();

	}

	private void uploadImageAndSignature() {
		try {
			Intent bgUploadImage = new Intent(ConfirmPickUpForUserName.this, BG_ImageUploadToServer.class);
			bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
			bgUploadImage.putExtra("isActionBarPickup", false);
			bgUploadImage.putExtra("isDropOffFromATL", false);
			bgUploadImage.putExtra("isDropoffBooking", true);
			startService(bgUploadImage);
			ActiveBookingDetail_New.bookingIdActiveBooking = "";
			bgUploadImage = null;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private String callReturnToPickupAPI(int isReturnToPickup, Map<String, Object> mapObject) {
		String requestDropResponseStr=null;
		try {
			WebserviceHandler webServiceHandler = new WebserviceHandler();
			ObjectMapper objectMapper = new ObjectMapper();
			String bookingsForReturnStr = objectMapper.writeValueAsString(mapObject);
			requestDropResponseStr = webServiceHandler.multipleReturnDeliveries(isReturnToPickup, bookingsForReturnStr);
			WebserviceHandler.reCountActiveBookings(ActiveBookingView.arrayOfBookingId.size(), 2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return requestDropResponseStr;
	}



	private void alertMessage(String titleStr, String msgStr, final boolean isSwitchToBookingView) {

		try {
			if (alertDialog != null)
                if (alertDialog.isShowing())
                    alertDialog.dismiss();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if(alertDialog != null)
			alertDialog = null;
		alertDialog = new Dialog(ConfirmPickUpForUserName.this);
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		alertDialog.setContentView(R.layout.dialogview);
		Window window = alertDialog.getWindow();
		window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		WindowManager.LayoutParams wlp = window.getAttributes();

		wlp.gravity = Gravity.CENTER;
		wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.setAttributes(wlp);

		TextView enterFieldDialogHEader = (TextView) alertDialog.findViewById(R.id.titleDialog);

		enterFieldDialogHEader.setText(titleStr);

		TextView enterFieldDialogMsg = (TextView) alertDialog.findViewById(R.id.dialogMessageText);

		enterFieldDialogMsg.setText(msgStr);

		Button enterFieldDialogDoneBtn = (Button) alertDialog.findViewById(R.id.dialogDoneBtn);
		enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
				if (isSwitchToBookingView)
					switchToBookingView(2);
			}
		});
		alertDialog.show();
	}


	void switchToBookingView(int bookingSelection){
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (getIntent().getIntExtra("RouteViewCalling", 0) == 181) {
			BookingView.bookingViewSelection = 1;
			ConfirmPickUpForUserName.isDropOffSuccessfull = 12;
		}else {
			BookingView.bookingViewSelection = bookingSelection;
			isDropOffSuccessfull = 11;
		}
		Intent callCompleteBookingfragment = new Intent(ConfirmPickUpForUserName.this, SlideMenuZoom2u.class);
		callCompleteBookingfragment.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		startActivity(callCompleteBookingfragment);
		finish();
		PushReceiver.isCameraOpen = false;
		callCompleteBookingfragment = null;
	}

	private void GetTotalDropAndToReturnCount(){

		final String[] getDHLTotalDropAndToReturnData = {""};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				if(progressForRequestPickETA != null)
					progressForRequestPickETA = null;
				progressForRequestPickETA = new ProgressDialog(ConfirmPickUpForUserName.this);
				Custom_ProgressDialogBar.inItProgressBar(progressForRequestPickETA);
				ActiveBookingView.getCurrentLocation(ConfirmPickUpForUserName.this);
			}

			@Override
			public void doInBackground() {
				WebserviceHandler webServiceHandler = new WebserviceHandler();
				getDHLTotalDropAndToReturnData[0] = webServiceHandler.getToReturnOrTotalDropCount();
			}

			@Override
			public void onPostExecute() {
				try {
					JSONObject jObjOfTotalDropOrToReturn = new JSONObject(getDHLTotalDropAndToReturnData[0]);
					totalDroppedOff =  jObjOfTotalDropOrToReturn.getInt("totalDroppedOff");
					totalPickedUp =  jObjOfTotalDropOrToReturn.getInt("totalPickedUp");
					totalToReturn =  jObjOfTotalDropOrToReturn.getInt("totalToReturn");

					if (totalDroppedOff < 0)
						totalDeliveredCountTxt.setText("-");
					else
						totalDeliveredCountTxt.setText("" + totalDroppedOff);
					if (totalPickedUp < 0)
						pickedUpCountTxt.setText("-");
					else
						pickedUpCountTxt.setText("" + totalPickedUp);
					if (totalToReturn < 0)
						toReturnCountTxt.setText("-");
					else
						toReturnCountTxt.setText("" + totalToReturn);
				} catch (Exception e) {
					e.printStackTrace();
					totalDroppedOff = -1;
					totalDeliveredCountTxt.setText("-");
					totalPickedUp = -1;
					pickedUpCountTxt.setText("-");
					totalToReturn = -1;
					toReturnCountTxt.setText("-");
				}
				try{
					if(progressForRequestPickETA != null)
						if(progressForRequestPickETA.isShowing())
							Custom_ProgressDialogBar.dismissProgressBar(progressForRequestPickETA);
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}.execute();
	}


}
