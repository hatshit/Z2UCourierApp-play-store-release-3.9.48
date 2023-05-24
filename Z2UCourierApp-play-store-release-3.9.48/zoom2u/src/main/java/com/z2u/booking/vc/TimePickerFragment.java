package com.z2u.booking.vc;

import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

	TextView etaMsgTxt; 
	ImageView selectPickImg; 
	public String uploadTimeStr = "";
	String pickUpEta;
	
	public TimePickerFragment(){
	}
	
	public void setTxtOnETADialogView(TextView etaMsgTxt, ImageView selectPickImg, String pickUpEta){
		this.etaMsgTxt = etaMsgTxt;
		this.selectPickImg = selectPickImg;
		this.pickUpEta = pickUpEta;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
	
		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}
	
	@SuppressLint("SimpleDateFormat")
	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
	
			try {
				String am_pm = "";
                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                datetime.set(Calendar.MINUTE, minute);

                if (datetime.get(Calendar.AM_PM) == Calendar.AM)
                    am_pm = "AM";
                else if (datetime.get(Calendar.AM_PM) == Calendar.PM)
                    am_pm = "PM";

                String timeBookingStr = (datetime.get(Calendar.HOUR) == 0) ?"12":datetime.get(Calendar.HOUR)+""; 
                timeBookingStr = timeBookingStr+":"+datetime.get(Calendar.MINUTE)+" "+am_pm;
                datetime = null;
                
				String dateBookingStr = LoginZoomToU.checkInternetwithfunctionality.returnDateFromServer(pickUpEta);
				String finalDateTimeStr = dateBookingStr+" "+timeBookingStr;
				
				Date convertedDate = LoginZoomToU.checkInternetwithfunctionality.returnDateTimeForConversion(finalDateTimeStr);
			    uploadTimeStr = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
			    convertedDate = null;
			    
			    String dateValueBookingStr = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer(uploadTimeStr);
		      
		       if(etaMsgTxt != null)
		    	   if(hourOfDay >= 12)
		    		   etaMsgTxt.setText(dateValueBookingStr);
		    	   else
		    		   etaMsgTxt.setText(dateValueBookingStr);
			
		       selectPickImg.setImageResource(R.drawable.icon_down);
		       
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}