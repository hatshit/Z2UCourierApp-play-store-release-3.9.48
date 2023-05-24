package com.suggestprice_team;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import com.zoom2u.utility.Functional_Utility;

/**
 * Created by arun on 23/2/18.
 */

public class CustomRecyclerViewToShowDriver extends RecyclerView {

    Context context;

    public CustomRecyclerViewToShowDriver(Context context) {
        super(context);
        this.context = context;
    }

    public CustomRecyclerViewToShowDriver(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public CustomRecyclerViewToShowDriver(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        heightSpec = View.MeasureSpec.makeMeasureSpec(Functional_Utility.dip2px(context, 310), View.MeasureSpec.AT_MOST);
        super.onMeasure(widthSpec, heightSpec);
    }
}
