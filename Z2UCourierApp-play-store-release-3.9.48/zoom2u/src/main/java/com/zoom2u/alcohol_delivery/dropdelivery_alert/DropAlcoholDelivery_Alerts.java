package com.zoom2u.alcohol_delivery.dropdelivery_alert;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.utility.CustomTypefaceSpan;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class DropAlcoholDelivery_Alerts {

    Context context;
    boolean isFromActiveDetailPage = false;

    All_Bookings_DataModels activeBookingModel;
    ActiveBookingView activeBookingList_obj;
    int position;

    public DropAlcoholDelivery_Alerts(Context context, All_Bookings_DataModels activeBookingModelForSelectedItem) {
        this.context = context;
        activeBookingModel = activeBookingModelForSelectedItem;
        isFromActiveDetailPage = true;

        dialogDropAlcoholDelivery_Alert1_Dialog();
    }

    public DropAlcoholDelivery_Alerts (Context context, ActiveBookingView activeBookingList_obj,
             All_Bookings_DataModels activeBookingModelForSelectedItem, int position) {
        this.context = context;
        isFromActiveDetailPage = false;
        this.activeBookingList_obj = activeBookingList_obj;
        this.activeBookingModel = activeBookingModelForSelectedItem;
        this.position = position;

        dialogDropAlcoholDelivery_Alert1_Dialog();
    }

    Dialog dialogDropAlcoholDelivery_Alert1;

    //************* Drop delivery alert for RSA compliance **********
    private void dialogDropAlcoholDelivery_Alert1_Dialog() {
        try{
            if(dialogDropAlcoholDelivery_Alert1 != null)
                if(dialogDropAlcoholDelivery_Alert1.isShowing())
                    dialogDropAlcoholDelivery_Alert1.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(dialogDropAlcoholDelivery_Alert1 != null)
                dialogDropAlcoholDelivery_Alert1 = null;
            dialogDropAlcoholDelivery_Alert1 = new Dialog(context);
            dialogDropAlcoholDelivery_Alert1.setCancelable(false);
            dialogDropAlcoholDelivery_Alert1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
            dialogDropAlcoholDelivery_Alert1.setContentView(R.layout.drop_alcohole_delivery_alert_1);

            Window window = dialogDropAlcoholDelivery_Alert1.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            DateFormat newDate = new SimpleDateFormat("dd-MMM-yyyy");
            Calendar cal = Calendar.getInstance();
            Date today = cal.getTime();
            cal.add(Calendar.YEAR, -18); // to get previous year add -1
            Date priorYear = cal.getTime();
            String priorBirthDateStr = newDate.format(priorYear);

            String firstTxtStr = "Does the person you are delivering to look under 25 years?\n\nIf yes please ask for a valid ID. Only Driver License, Proof of age or Valid passport is acceptable.\n\nDate of Birth must be prior to:\n";

            // Create a new spannable with the two strings
            Spannable spannable = new SpannableString(firstTxtStr+priorBirthDateStr);

            // Set the custom typeface to span over a section of the spannable object
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), 0, firstTxtStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_SEMIBOLD), firstTxtStr.length(), firstTxtStr.length() + priorBirthDateStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView alcohol_drop_delivery_alrt_titletxt = (TextView) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_titletxt);


            TextView alcohol_drop_delivery_alrt_msg1txt = (TextView) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_msg1txt);

            alcohol_drop_delivery_alrt_msg1txt.setText(spannable);

            TextView alcohol_drop_delivery_alrt_noteTitletxt = (TextView) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_noteTitletxt);

            TextView alcohol_drop_delivery_alrt_notemsgtxt = (TextView) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_notemsgtxt);

            Button alcohol_drop_delivery_alrt_18plus = (Button) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_18plus);

            alcohol_drop_delivery_alrt_18plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_Alert1.dismiss();
                    dialogDropAlcoholDelivery_Alert2_Dialog();
                }
            });

            Button alcohol_drop_delivery_alrt_under18 = (Button) dialogDropAlcoholDelivery_Alert1.findViewById(R.id.alcohol_drop_delivery_alrt_under18);

            alcohol_drop_delivery_alrt_under18.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_Alert1.dismiss();
                    int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(activeBookingModel.getDrop_GPSX(),
                            activeBookingModel.getDrop_GPSY());
                    if (distanceFromCurrentToDrop > 1000)
                        DialogActivity.alertDialogView(context, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                    else {
                        if (isFromActiveDetailPage)
                            ((ActiveBookingDetail_New) context).attemptDeliveryWindow(0, 2);
                        else
                            activeBookingList_obj.attemptDeliveryWindow(2);
                    }
                }
            });
            dialogDropAlcoholDelivery_Alert1.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Dialog dialogDropAlcoholDelivery_Alert2;

    //************* Drop delivery alert to ask Drivers for intoxicated or not **********
    private void dialogDropAlcoholDelivery_Alert2_Dialog() {
        try{
            if(dialogDropAlcoholDelivery_Alert2 != null)
                if(dialogDropAlcoholDelivery_Alert2.isShowing())
                    dialogDropAlcoholDelivery_Alert2.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(dialogDropAlcoholDelivery_Alert2 != null)
                dialogDropAlcoholDelivery_Alert2 = null;
            dialogDropAlcoholDelivery_Alert2 = new Dialog(context);
            dialogDropAlcoholDelivery_Alert2.setCancelable(false);
            dialogDropAlcoholDelivery_Alert2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
            dialogDropAlcoholDelivery_Alert2.setContentView(R.layout.drop_alcohol_delivery_alert2);

            Window window = dialogDropAlcoholDelivery_Alert2.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView alcohol_drop_delivery_alrt2_titletxt = (TextView) dialogDropAlcoholDelivery_Alert2.findViewById(R.id.alcohol_drop_delivery_alrt2_titletxt);

            TextView alcohol_drop_delivery_alrt2_msg1txt = (TextView) dialogDropAlcoholDelivery_Alert2.findViewById(R.id.alcohol_drop_delivery_alrt2_msg1txt);

            ImageView alcohol_drop_delivery_alrt2_info_icon = (ImageView) dialogDropAlcoholDelivery_Alert2.findViewById(R.id.alcohol_drop_delivery_alrt2_info_icon);
            alcohol_drop_delivery_alrt2_info_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_InfoAlert ();
                }
            });

            Button alcohol_drop_delivery_alrt2_18plus = (Button) dialogDropAlcoholDelivery_Alert2.findViewById(R.id.alcohol_drop_delivery_alrt2_18plus);

            alcohol_drop_delivery_alrt2_18plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_Alert2.dismiss();
                    int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(activeBookingModel.getDrop_GPSX(),
                            activeBookingModel.getDrop_GPSY());
                    if (distanceFromCurrentToDrop > 1000)
                        DialogActivity.alertDialogView(context, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                    else {
                        if (isFromActiveDetailPage)
                            ((ActiveBookingDetail_New) context).attemptDeliveryWindow(0, 3);
                        else
                            activeBookingList_obj.attemptDeliveryWindow(3);
                    }
                }
            });

            Button alcohol_drop_delivery_alrt2_under18 = (Button) dialogDropAlcoholDelivery_Alert2.findViewById(R.id.alcohol_drop_delivery_alrt2_under18);

            alcohol_drop_delivery_alrt2_under18.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_Alert2.dismiss();
                    if (isFromActiveDetailPage) {
//                        if (activeBookingModel.getSource().equals("DHL"))
//                            ((ActiveBookingDetail_New) context).openBarCodeScannerView(4);
//                        else
                        ((ActiveBookingDetail_New) context).processToDropNonDHLAndNormalDelivery();
                    } else {
//                        if (activeBookingModel.getSource().equals("DHL"))
//                            activeBookingList_obj.openBarCodeScannerView(4);
//                        else
                        activeBookingList_obj.processToDropNonDHLAndNormalDelivery(position);
                    }
                }
            });
            dialogDropAlcoholDelivery_Alert2.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    Dialog dialogDropAlcoholDelivery_InfoAlert;

    //************* Drop delivery information alert for Signs for Intoxication **********
    private void dialogDropAlcoholDelivery_InfoAlert() {
        try{
            if(dialogDropAlcoholDelivery_InfoAlert != null)
                if(dialogDropAlcoholDelivery_InfoAlert.isShowing())
                    dialogDropAlcoholDelivery_InfoAlert.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }

        try {
            if(dialogDropAlcoholDelivery_InfoAlert != null)
                dialogDropAlcoholDelivery_InfoAlert = null;
            dialogDropAlcoholDelivery_InfoAlert = new Dialog(context);
            dialogDropAlcoholDelivery_InfoAlert.setCancelable(false);
            dialogDropAlcoholDelivery_InfoAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogDropAlcoholDelivery_InfoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
            dialogDropAlcoholDelivery_InfoAlert.setContentView(R.layout.driversafely_information_dialog);

            Window window = dialogDropAlcoholDelivery_InfoAlert.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            android.view.WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView titleTxtForDriverSafe_Info = (TextView) dialogDropAlcoholDelivery_InfoAlert.findViewById(R.id.titleTxtForDriverSafe_Info);

            titleTxtForDriverSafe_Info.setText("Signs of Intoxication");

            TextView msgTxtDriverSafe_Info = (TextView) dialogDropAlcoholDelivery_InfoAlert.findViewById(R.id.msgTxtDriverSafe_Info);

            String str_1_info_drop_delivery = "Speech: ";
            String str_11_info_drop_delivery = "Slurring, Rambling, Incoherent\n";
            String str_2_info_drop_delivery = "Balance: ";
            String str_21_info_drop_delivery = "Unsteady, Swaying, Stumbling\n";
            String str_3_info_drop_delivery = "Coordination: ";
            String str_31_info_drop_delivery = "Fumbling, difficulty in simple actions\n";
            String str_4_info_drop_delivery = "Behavior: ";
            String str_41_info_drop_delivery = "Rude, not following instructions, Distressed\n";
            String str_5_info_drop_delivery = "Smell: ";
            String str_51_info_drop_delivery = "Alcoholic Aroma\n\n\n\n";
            String str_6_info_drop_delivery = "Signs of drug impairment:\n";
            String str_61_info_drop_delivery = "Jerky or rapid movement\n" +
                    "Incoherence\n" +
                    "Dilated pupils\n" +
                    "Rapid breathing\n" +
                    "Odd behaviour";

            // Create a new spannable with the two strings
            Spannable spannable = new SpannableString(str_1_info_drop_delivery+str_11_info_drop_delivery+str_2_info_drop_delivery+
                    str_21_info_drop_delivery+str_3_info_drop_delivery+str_31_info_drop_delivery+str_4_info_drop_delivery+
                    str_41_info_drop_delivery+str_5_info_drop_delivery+str_51_info_drop_delivery+str_6_info_drop_delivery+str_61_info_drop_delivery);

            int secEndThirdFirstLength = str_1_info_drop_delivery.length() + str_11_info_drop_delivery.length();
            int thirdEndFourthFirstLength = secEndThirdFirstLength + str_2_info_drop_delivery.length();
            int fourtEndFifthFirstLength = thirdEndFourthFirstLength + str_21_info_drop_delivery.length();
            int fifthEndSixthFirstLength = fourtEndFifthFirstLength + str_3_info_drop_delivery.length();
            int sixthEndSeventhFirstLength = fifthEndSixthFirstLength + str_31_info_drop_delivery.length();
            int seventhEndEightFirstLength = sixthEndSeventhFirstLength + str_4_info_drop_delivery.length();
            int eightEndNinethFirstLength = seventhEndEightFirstLength + str_41_info_drop_delivery.length();
            int nineEndTenthFirstLength = eightEndNinethFirstLength + str_5_info_drop_delivery.length();
            int tenEndElevenFirstLength = nineEndTenthFirstLength + str_51_info_drop_delivery.length();
            int elevenEndTwelveFirstLength = tenEndElevenFirstLength + str_6_info_drop_delivery.length();
            int twelveEndThirteenFirstLength = elevenEndTwelveFirstLength + str_61_info_drop_delivery.length();

            // Set the custom typeface to span over a section of the spannable object
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), 0, str_1_info_drop_delivery.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), str_1_info_drop_delivery.length(), secEndThirdFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), secEndThirdFirstLength, thirdEndFourthFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), thirdEndFourthFirstLength, fourtEndFifthFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), fourtEndFifthFirstLength, fifthEndSixthFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), fifthEndSixthFirstLength, sixthEndSeventhFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), sixthEndSeventhFirstLength, seventhEndEightFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), seventhEndEightFirstLength, eightEndNinethFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), eightEndNinethFirstLength, nineEndTenthFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), nineEndTenthFirstLength, tenEndElevenFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_BOLD), tenEndElevenFirstLength, elevenEndTwelveFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannable.setSpan( new CustomTypefaceSpan("sans-serif",LoginZoomToU.NOVA_REGULAR), elevenEndTwelveFirstLength, twelveEndThirteenFirstLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            msgTxtDriverSafe_Info.setText(spannable);

            Button cancelBtnDriverSafe_Info = (Button) dialogDropAlcoholDelivery_InfoAlert.findViewById(R.id.cancelBtnDriverSafe_Info);

            cancelBtnDriverSafe_Info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_InfoAlert.dismiss();
                }
            });

            ImageView closeDialogBtnDriverSafe_Info = (ImageView) dialogDropAlcoholDelivery_InfoAlert.findViewById(R.id.closeDialogBtnDriverSafe_Info);
            closeDialogBtnDriverSafe_Info.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDropAlcoholDelivery_InfoAlert.dismiss();
                }
            });

            dialogDropAlcoholDelivery_InfoAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
