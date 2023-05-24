package com.zoom2u.slidemenu.offerrequesthandlr;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import java.util.ArrayList;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;

public class DateWheelAdapter  extends AbstractWheelTextAdapter {

    ArrayList<String> dates;
    int isDateSelection;
    boolean isFromDriverSupport = false;

    //An object of this class must be initialized with an array of Date type
    protected DateWheelAdapter(Context context, ArrayList<String> dates, int isDateSelection) {
        //Pass the context and the custom layout for the text to the super method
        super(context, R.layout.wheelpicker_item);
        this.dates = dates;
        this.isDateSelection = isDateSelection;
    }

    //An object of this class must be initialized with an array of Date type
    protected DateWheelAdapter(Context context, ArrayList<String> dates, int isDateSelection, boolean isFromDriverSupport) {
        //Pass the context and the custom layout for the text to the super method
        super(context, R.layout.wheelpicker_item);
        this.dates = dates;
        this.isDateSelection = isDateSelection;
        this.isFromDriverSupport = isFromDriverSupport;
    }

    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        TextView weekday = (TextView) view.findViewById(R.id.time_item);

        weekday.setText(dates.get(index));
        if (isFromDriverSupport) {
            if (isDateSelection == dates.size() - 1) {
                if (index == dates.size() - 1)
                    weekday.setText("Today");     //If it is the first date of the array, set the color blue
            }
        } else {
            if (isDateSelection == 0) {
                if (index == 0)
                    weekday.setText("Today");     //If it is the first date of the array, set the color blue
            }
        }
        return view;
    }

    @Override
    public int getItemsCount() {
        return dates.size();
    }

    @Override
    protected CharSequence getItemText(int index) {
        return "";
    }
}