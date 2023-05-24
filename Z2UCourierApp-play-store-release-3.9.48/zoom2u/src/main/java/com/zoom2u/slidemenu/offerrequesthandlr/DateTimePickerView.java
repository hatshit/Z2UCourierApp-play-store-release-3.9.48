package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import kankan.wheel.widget.OnWheelScrollListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

public class DateTimePickerView {

    public interface OnDateSelection {
        void onDateSelected(String selectedDate);
        void onDismiss();
    }

    private OnDateSelection onDateSelection;

    public void setOnDateSelection(OnDateSelection onDateSelection) {
        this.onDateSelection = onDateSelection;
    }

    Context context;
    ArrayList<String> arrayOFDate;
    String[] ampmArray = new String[] {"AM", "PM"};
    String[] arrayOfHour = new String[] {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

    int indexValueOfHour;

    private int isFromDriverSupportView = 0;
    public String selectedDateTimeForDriverSupport = "";

    TextView pickUpDateField, pickupTimeField, dropDateField, dropTimeField;

    //************* Used for Place bid ***************
    public DateTimePickerView(Context con){
        context = con;
        arrayOFDate = new ArrayList<String>();
        SimpleDateFormat formatFrom = new SimpleDateFormat("dd-MM-yyyy");
        for(int i = 0; i < 30; i++){
            Date date = new Date(); // wherever you get this from
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, i); // add 10 days
            date = cal.getTime();
            String dateStr = formatFrom.format(date);
            arrayOFDate.add(dateStr);
        }
    }

    //************ Set pick and Drop date time fields **************
    public void setDateTimeTxtField(TextView pickUpDateField, TextView pickupTimeField, TextView dropDateField, TextView dropTimeField){
        this.pickUpDateField = pickUpDateField;
        this.pickupTimeField = pickupTimeField;
        this.dropDateField = dropDateField;
        this.dropTimeField = dropTimeField;
    }

    //************* Used in Suggested price accept bid and In Driver support ***************
    public DateTimePickerView(Context con, int isFromDriverSupportView){
        context = con;
        this.isFromDriverSupportView = isFromDriverSupportView;     // ***** Pass 1 for Driver support feature and 2 for Suggested price feature
        if (isFromDriverSupportView == 1) {
            arrayOFDate = new ArrayList<String>();
            SimpleDateFormat formatFrom = new SimpleDateFormat("EEE, dd MMM yyyy");
            for (int i = -30; i <= 0; i++) {
                Date date = new Date(); // wherever you get this from
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, i); // add 10 days
                date = cal.getTime();
                String dateStr = formatFrom.format(date);
                arrayOFDate.add(dateStr);
            }
        }
    }

    Dialog dialogDateTimePicker;

    public Dialog getDialogDateTimePicker() {
        return dialogDateTimePicker;
    }

    /*****************  Date picker dialog for date selection  **************/
    public void datePickerDialog(final TextView selectDateTxt, final TextView selectTimeTxt, final int isPickUpDate){
        //Create a custom dialog with the dialog_date.xml file
        if(dialogDateTimePicker != null)
            dialogDateTimePicker = null;
        dialogDateTimePicker = new Dialog(context, R.style.DialogSlideAnim);
        dialogDateTimePicker.setContentView(R.layout.datepicker_view);
        dialogDateTimePicker.setCancelable(true);
        dialogDateTimePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialogDateTimePicker.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity =  Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        Button setDateBtn = (Button) dialogDateTimePicker.findViewById(R.id.setDateBtn);
        setDateBtn.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        final Button cancelDateBtn = (Button) dialogDateTimePicker.findViewById(R.id.cancelDateBtn);
        cancelDateBtn.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        //Configure Days Column
        final WheelView dateWheel = (WheelView) dialogDateTimePicker.findViewById(R.id.dateWheelPicker);

        if (isFromDriverSupportView == 1) {
            dateWheel.setViewAdapter(new DateWheelAdapter(context, arrayOFDate, arrayOFDate.size() - 1, true));
            dateWheel.setCurrentItem(arrayOFDate.size() - 1);
        }else
            dateWheel.setViewAdapter(new DateWheelAdapter(context, arrayOFDate, 0));

//        else if (isFromDriverSupportView == 2)
//            dateWheel.setViewAdapter(new DateWheelAdapter(context, arrayOfTimeInterval, 0, false, isFromDriverSupportView));

        setDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDateTimePicker.dismiss();
                dialogDateTimePicker = null;
                if (isFromDriverSupportView == 1) {
                    selectedDateTimeForDriverSupport = arrayOFDate.get(dateWheel.getCurrentItem())+" 00:00 "+ampmArray[0];;
                    selectDateTxt.setText(arrayOFDate.get(dateWheel.getCurrentItem()));
                    if (onDateSelection!=null)onDateSelection.onDateSelected(arrayOFDate.get(dateWheel.getCurrentItem()));
                } else {
                    if (isPickUpDate == 1) {
                        selectDateTxt.setText(arrayOFDate.get(dateWheel.getCurrentItem()));
                        if (dropDateField.getText().toString().equals("Date")) {
                            dropDateField.setText(arrayOFDate.get(dateWheel.getCurrentItem()));
                        } else
                            setDefaultDateForPick(arrayOFDate.get(dateWheel.getCurrentItem()));

                        if (!selectTimeTxt.getText().toString().equals("Time")) {
                            if (selectDateTxt.getText().toString().equals(arrayOFDate.get(0)))
                                selectTimeTxt.setText(getCurrentTime());
                            if (dropTimeField.getText().toString().equals("Time"))
                                setDefaultDropTimeTOOneHour();
                            else if (checkForDropTimeSelection(dropTimeField.getText().toString(), selectTimeTxt.getText().toString()))
                                setTimeforDrop(dropTimeField.getText().toString());
                        } else {
                            selectTimeTxt.setText(getCurrentTime());
                            setDefaultDropTimeTOOneHour();
                        }
                    } else {
                        if (pickUpDateField.getText().toString().equals("Date"))
                            dropDateField.setText(arrayOFDate.get(dateWheel.getCurrentItem()));
                        else
                            setDefaultDateForDrop(arrayOFDate.get(dateWheel.getCurrentItem()));

                        if (!selectTimeTxt.getText().toString().equals("Time")) {
                            if (selectDateTxt.getText().toString().equals(arrayOFDate.get(0)))
                                selectTimeTxt.setText(getCurrentTime());
                            if (dropTimeField.getText().toString().equals("Time"))
                                setDefaultDropTimeTOOneHour();
                            else if (checkForDropTimeSelection(dropTimeField.getText().toString(), selectTimeTxt.getText().toString()))
                                setTimeforDrop(dropTimeField.getText().toString());
                        } else {
                            selectTimeTxt.setText(getCurrentTime());
                            setDefaultDropTimeTOOneHour();
                        }
                    }
                }
//                else if (isFromDriverSupportView == 2) {
//                    selectedMinutesForSuggestedPrice = intArrayOfTimeIntervalinMinutes[dateWheel.getCurrentItem()];
//                    selectDateTxt.setText(arrayOfTimeInterval[dateWheel.getCurrentItem()]);
//                }
            }
        });

        cancelDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDateTimePicker.dismiss();
                if (onDateSelection!=null)onDateSelection.onDismiss();
                dialogDateTimePicker = null;
            }
        });
        dialogDateTimePicker.show();
    }

    private void setDefaultDateForDrop(String dropDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date pickDate = sdf.parse(pickUpDateField.getText().toString());
            Date dropDate = sdf.parse(dropDateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dropDate);

            if(calendar.getTime().compareTo(pickDate) < 0){
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(pickDate);
                dropDateField.setText(sdf.format(calendar1.getTime()));
                calendar1 = null;
            }else{
                dropDateField.setText(dropDateStr);
            }
            calendar = null;
            sdf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDefaultDateForPick(String pickDateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date dropDate = sdf.parse(dropDateField.getText().toString());
            Date pickDate = sdf.parse(pickDateStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(pickDate);

            if(calendar.getTime().compareTo(dropDate) > 0){
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(dropDate);
                //pickUpDateField.setText(sdf.format(calendar1.getTime()));
                pickUpDateField.setText(pickDateStr);
                dropDateField.setText(pickDateStr);
                calendar1 = null;
            }else{
                pickUpDateField.setText(pickDateStr);
            }
            calendar = null;
            sdf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    WheelView hourWheelPicker, minuteWheelPicker, ampmWheelPicker;
    Calendar calendar1;
    /**********************  Time picker Dialog *******************/
    void timePickerDialog(final TextView selectTimeTxt, final TextView selectedDatetxt, final int isPickUpDate){
        if(dialogDateTimePicker != null)
            dialogDateTimePicker = null;

        dialogDateTimePicker = new Dialog(context, R.style.DialogSlideAnim);
        dialogDateTimePicker.setContentView(R.layout.timepicker_dialog);
        dialogDateTimePicker.setCancelable(false);
        dialogDateTimePicker.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialogDateTimePicker.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER | Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        dialogDateTimePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        Button setTimeBtn = (Button) dialogDateTimePicker.findViewById(R.id.setTimeBtn);
        setTimeBtn.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);
        Button cancelTimeBtn = (Button) dialogDateTimePicker.findViewById(R.id.cancelTimeBtn);
        cancelTimeBtn.setTypeface(LoginZoomToU.NOVA_SEMIBOLD);

        WheelView defaultPicker1 = null, defaultPicker2 = null;
        String[] defaultArray = {"",""};

        defaultPicker1 = (WheelView) dialogDateTimePicker.findViewById(R.id.defaultWheelPicker1);
        defaultPicker2 = (WheelView) dialogDateTimePicker.findViewById(R.id.defaultWheelPicker2);

        ArrayWheelAdapter<String> defaultAdapter = new ArrayWheelAdapter<String>(context, defaultArray);
        defaultPicker1.setViewAdapter(defaultAdapter);
        defaultPicker2.setViewAdapter(defaultAdapter);
        defaultAdapter = null;
        defaultArray = null;

        //Configure Days Column
        if(hourWheelPicker != null)
            hourWheelPicker = null;
        hourWheelPicker = (WheelView) dialogDateTimePicker.findViewById(R.id.hourWheelPicker);
        //	NumericWheelAdapter hourAdapter = new NumericWheelAdapter(getBaseContext, 0, 12, "%02d");
        ArrayWheelAdapter<String> hourAdapter = new ArrayWheelAdapter<String>(context, arrayOfHour);
        hourWheelPicker.setViewAdapter(hourAdapter);
        hourWheelPicker.setCyclic(true);
        hourAdapter.setItemResource(R.layout.wheelpicker_item);
        hourAdapter.setItemTextResource(R.id.time_item);

        if(minuteWheelPicker != null)
            minuteWheelPicker = null;
        minuteWheelPicker = (WheelView) dialogDateTimePicker.findViewById(R.id.minuteWheelPicker);
        NumericWheelAdapter minuteAdapter = new NumericWheelAdapter(context, 0, 59, "%02d");
        minuteWheelPicker.setViewAdapter(minuteAdapter);
        minuteWheelPicker.setCyclic(true);
        minuteAdapter.setItemResource(R.layout.wheelpicker_item);
        minuteAdapter.setItemTextResource(R.id.time_item);

        if(ampmWheelPicker != null)
            ampmWheelPicker = null;
        ampmWheelPicker = (WheelView) dialogDateTimePicker.findViewById(R.id.ampmWheelPicker);
        ArrayWheelAdapter<String> ampmAdapter = new ArrayWheelAdapter<String>(context, ampmArray);
        ampmWheelPicker.setViewAdapter(ampmAdapter);
        ampmAdapter.setItemResource(R.layout.wheelpicker_item);
        ampmAdapter.setItemTextResource(R.id.time_item);

        // set current time
        calendar1 = Calendar.getInstance(Locale.US);
        hourWheelPicker.setCurrentItem(calendar1.get(Calendar.HOUR)-1);
        minuteWheelPicker.setCurrentItem(calendar1.get(Calendar.MINUTE));
        ampmWheelPicker.setCurrentItem(calendar1.get(Calendar.AM_PM));

        indexValueOfHour = Integer.parseInt(arrayOfHour[hourWheelPicker.getCurrentItem()]);
        hourWheelPicker.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                setDefaultTime(selectedDatetxt, isPickUpDate);
            }
        });

        minuteWheelPicker.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                setDefaultTime(selectedDatetxt, isPickUpDate);
            }
        });

        ampmWheelPicker.addScrollingListener(new OnWheelScrollListener() {
            @Override
            public void onScrollingStarted(WheelView wheel) {

            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                setDefaultTime(selectedDatetxt, isPickUpDate);
            }
        });

        setTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String hourStr = arrayOfHour[hourWheelPicker.getCurrentItem()];
                    String minStr = returnMinuteInStr(minuteWheelPicker.getCurrentItem());
                    String pickUpTime = hourStr + ":" + minStr + " " + ampmArray[ampmWheelPicker.getCurrentItem()];
                    dialogDateTimePicker.dismiss();
                    dialogDateTimePicker = null;

                    if (isPickUpDate == 1) {
                        if (!dropTimeField.getText().toString().equals("Time")) {
                            if (checkForDropTimeSelection(dropTimeField.getText().toString(), pickUpTime)) {
                                if (checkForDropDateSelection(dropDateField.getText().toString(), pickUpDateField.getText().toString()) == 0) {
                                    selectTimeTxt.setText(pickUpTime);
                                    if (checkForPickUpTimeIs_11PM_Or_After(pickUpTime)) {
                                        dropDateField.setText(arrayOFDate.get(setFutureDate_In_DropDateField(dropDateField.getText().toString())));
                                        setDefaultDropTimeTOOneHour();
                                    } else
                                        setDefaultDropTimeTOOneHour();
                                } else if (checkForDropDateSelection(dropDateField.getText().toString(), pickUpDateField.getText().toString()) > 0) {
                                    selectTimeTxt.setText(pickUpTime);
                                    setDefaultDropTimeTOOneHour();
                                } else
                                    selectTimeTxt.setText(pickUpTime);
                            } else
                                selectTimeTxt.setText(pickUpTime);
                        } else {
                            selectTimeTxt.setText(pickUpTime);
                            setDefaultDropTimeTOOneHour();
                        }
                        if (pickUpDateField.getText().toString().equals("Date"))
                            pickUpDateField.setText(arrayOFDate.get(0));
                        if (dropDateField.getText().toString().equals("Date"))
                            dropDateField.setText(arrayOFDate.get(0));
                    } else {
                        if (!pickupTimeField.getText().toString().equals("Time")) {
                            if (checkForDropTimeSelection(pickUpTime, pickupTimeField.getText().toString())) {
                                if (checkForDropDateSelection(dropDateField.getText().toString(), pickUpDateField.getText().toString()) != 1)
                                    setDefaultDropTimeTOOneHour();
                                else
                                    dropTimeField.setText(pickUpTime);
                            } else
                                dropTimeField.setText(pickUpTime);
                        } else
                            dropTimeField.setText(pickUpTime);

                        if (pickUpDateField.getText().toString().equals("Date"))
                            pickUpDateField.setText(arrayOFDate.get(0));
                        if (dropDateField.getText().toString().equals("Date"))
                            dropDateField.setText(arrayOFDate.get(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        cancelTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDateTimePicker.dismiss();
                dialogDateTimePicker = null;
            }
        });

        dialogDateTimePicker.show();
    }

    //***********  Return Hour in string
    public String returnHourInStr(int item){
        String timeInStr = null;
        if(item == 0)
            timeInStr = "12";
        else if(item < 10)
            timeInStr = "0"+item;
        else
            timeInStr = ""+item;

        return timeInStr;
    }

    //***********  Return Minute in string
    public String returnMinuteInStr(int item){
        String timeInStr = null;
        if(item == 0)
            timeInStr = "00";
        else if(item < 10)
            timeInStr = "0"+item;
        else
            timeInStr = ""+item;

        return timeInStr;
    }

    //****************  Set default (current) time
    private void setDefaultTime(TextView selectedDatetxt, int isPickUpDateTime) {
        String hourIndexValue = arrayOfHour[hourWheelPicker.getCurrentItem()];
        indexValueOfHour = Integer.parseInt(hourIndexValue);
        if(indexValueOfHour == 12)
            indexValueOfHour = 0;
        if (isPickUpDateTime == 1) {
            if (selectedDatetxt.getText().toString().equals(arrayOFDate.get(0)))
                setDefaultTimeFromWheel();
        } else {
            if (dropDateField.getText().toString().equals(arrayOFDate.get(0)))
                setDefaultTimeFromWheel();
        }
    }

    private void setDefaultTimeFromWheel() {
        if(ampmWheelPicker.getCurrentItem() == calendar1.get(Calendar.AM_PM)){
            if(indexValueOfHour <= calendar1.get(Calendar.HOUR)){
                hourWheelPicker.setCurrentItem(calendar1.get(Calendar.HOUR)-1);
                if(minuteWheelPicker.getCurrentItem() < calendar1.get(Calendar.MINUTE))
                    minuteWheelPicker.setCurrentItem(calendar1.get(Calendar.MINUTE));
            }
        }else if(ampmWheelPicker.getCurrentItem() < calendar1.get(Calendar.AM_PM)){
            ampmWheelPicker.setCurrentItem(calendar1.get(Calendar.AM_PM));
            if(indexValueOfHour <= calendar1.get(Calendar.HOUR)){
                hourWheelPicker.setCurrentItem(calendar1.get(Calendar.HOUR)-1);
                if(minuteWheelPicker.getCurrentItem() < calendar1.get(Calendar.MINUTE))
                    minuteWheelPicker.setCurrentItem(calendar1.get(Calendar.MINUTE));
            }
        }
    }

    // ********** Get current time ***********
    String getCurrentTime(){
        String currentTimeStr = "";
        try {
            if(calendar1 != null)
                calendar1 = null;
            calendar1 = Calendar.getInstance(Locale.US);
            String hourStr = returnHourInStr(calendar1.get(Calendar.HOUR));
            String minStr = returnMinuteInStr(calendar1.get(Calendar.MINUTE));
            if(calendar1.get(Calendar.AM_PM) == 0)
                currentTimeStr = hourStr+":"+minStr+" "+"AM";
            else
                currentTimeStr = hourStr+":"+minStr+" "+"PM";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentTimeStr;
    }

    void setTimeforDrop(String setDropTimeStr){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date pickTime = sdf.parse(pickupTimeField.getText().toString());
            Date dropTime = sdf.parse(setDropTimeStr);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dropTime);
            //calendar.add(Calendar.HOUR, -1);

            if(calendar.getTime().compareTo(pickTime) <= 0){
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(pickTime);
                calendar1.add(Calendar.HOUR, 1);
                dropTimeField.setText(sdf.format(calendar1.getTime()));
                calendar1 = null;
            }else{
                dropTimeField.setText(setDropTimeStr);
            }
            calendar = null;
            sdf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setDefaultDropTimeTOOneHour(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date date = sdf.parse(pickupTimeField.getText().toString());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR, 1);
            dropTimeField.setText(sdf.format(calendar.getTime()));
            calendar = null;
            sdf = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected boolean checkForDropTimeSelection(String setDropTimeStr, String pickTimeStr) {
        try {
            SimpleDateFormat converter = new SimpleDateFormat("hh:mm a");
            Date getTimeValue = null, checkTime;

            getTimeValue = converter.parse(setDropTimeStr);
            checkTime = converter.parse(pickTimeStr);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(checkTime);
            calendar.add(Calendar.HOUR, 1);
            checkTime = calendar.getTime();
            calendar = null;
            converter = null;
            if(getTimeValue.compareTo(checkTime) <= 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    int checkForDropDateSelection(String setDropDateStr, String pickDateStr) {
        try {
            SimpleDateFormat converter = new SimpleDateFormat("dd-MM-yyyy");
            Date getTimeValue = null, checkTime;

            getTimeValue = converter.parse(setDropDateStr);
            checkTime = converter.parse(pickDateStr);
            if(getTimeValue.compareTo(checkTime) == 0)
                return 0;
            else if(getTimeValue.compareTo(checkTime) > 0)
                return 1;
            else
                return -1;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    protected boolean checkForPickUpTimeIs_11PM_Or_After(String pickTimeStr) {
        try {
            SimpleDateFormat converter = new SimpleDateFormat("hh:mm a");
            Date startTime = null, endTime = null, checkTime;

            startTime = converter.parse("10:59 PM");
            endTime = converter.parse("11:59 PM");
            checkTime = converter.parse(pickTimeStr);
            if(checkTime.compareTo(startTime) > 0 && checkTime.compareTo(endTime) <= 0)
                return true;
            else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private int setFutureDate_In_DropDateField(String dropDate){
        int indexNo = arrayOFDate.indexOf(dropDate);
        return (indexNo+1);
    }

}
