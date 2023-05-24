package com.zoom2u.build_me_route;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.aigestudio.wheelpicker.WheelPicker;
import com.zoom2u.R;

import java.util.Arrays;

public class TimePicker implements WheelPicker.OnItemSelectedListener {
    Dialog dialogTimePicker;
    WheelPicker wheelHr, wheelMin, wheelAmPm;
    String pickHr[] = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};
    String pickMin[] = {"00", "15", "30", "45"};
    String pickAmPm[] = {"AM", "PM"};
    String selectedHr, selectedMin, selectedAmPm, selectedTime;
    Window window;
    Context context;
    MyCallback callback;

    interface MyCallback {
        void onTimeClick(String time,Boolean isPickup);
    }


    public void timePickerDialog(MyCallback myCallback,Context context, String selectedTimeWindowItem,Boolean isPickup) {
        this.callback=myCallback;
        this.context=context;
        String a[] = selectedTimeWindowItem.split(":");
        selectedHr = a[0];
        String a1[] = a[1].split(" ");
        selectedMin = "00";
        selectedAmPm = a1[1];

        dialogTimePicker = new Dialog(context, R.style.DialogSlideAnim);
        dialogTimePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTimePicker.setContentView(R.layout.dialog_time_picker1);

        window = dialogTimePicker.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);

        if (dialogTimePicker.isShowing()) {
            dialogTimePicker.dismiss();
        }


        Button done = dialogTimePicker.findViewById(R.id.btn_Done_TimeSelect);
        Button cancel = dialogTimePicker.findViewById(R.id.btn_Cancel_TimeSelect);
        wheelHr = dialogTimePicker.findViewById(R.id.hr) ;
        wheelMin = dialogTimePicker.findViewById(R.id.min) ;
        wheelAmPm = dialogTimePicker.findViewById(R.id.am_pm) ;
        wheelHr.setOnItemSelectedListener(this);
        wheelMin.setOnItemSelectedListener(this);
        wheelAmPm.setOnItemSelectedListener(this);

        wheelHr.setData(Arrays.asList(pickHr));
        wheelMin.setData(Arrays.asList(pickMin));
        wheelAmPm.setData(Arrays.asList(pickAmPm));

        timeSelection(selectedHr, selectedMin, selectedAmPm);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTime= selectedHr+":"+selectedMin+" "+selectedAmPm;
                callback.onTimeClick(selectedTime,isPickup);
                dialogTimePicker.dismiss();
            }
        }) ;

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogTimePicker.dismiss();
            }
        });

        dialogTimePicker.show();

    }
    private void timeSelection(String hr,String min,String ampm) {
        wheelHr.setSelectedItemPosition(Arrays.asList(pickHr).indexOf(hr));
        wheelHr.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));

        wheelMin.setSelectedItemPosition(Arrays.asList(pickMin).indexOf(min));
        wheelMin.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));

        wheelAmPm.setSelectedItemPosition(Arrays.asList(pickAmPm).indexOf(ampm));
        wheelAmPm.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));
    }


    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        if(picker == wheelHr){
            wheelHr.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));
            selectedHr = Arrays.asList(pickHr).get(position);
        }
        else if(picker == wheelMin){
            wheelMin.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));
            selectedMin = Arrays.asList(pickMin).get(position);
        }
        else if(picker == wheelAmPm){
            wheelAmPm.setSelectedItemTextColor(context.getResources().getColor(R.color.base_color1));
            selectedAmPm = Arrays.asList(pickAmPm).get(position);
        }
    }
}
