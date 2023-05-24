package com.zoom2u.lateview.vc;

import java.util.ArrayList;

import com.zoom2u.R;
import com.zoom2u.dialogactivity.models.Model_DialogReasonLateDelivey;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomListForLateDetail extends BaseAdapter{

	Context con; 
	int resourceId; 
	ArrayList<Model_DialogReasonLateDelivey> lateDetailArray;
	
	public CustomListForLateDetail(Context con, int resourceId, ArrayList<Model_DialogReasonLateDelivey> lateDetailArray){
		this.con = con;
		this.resourceId = resourceId;
		this.lateDetailArray = lateDetailArray;
	}
	
	@Override
	public int getCount() {
		return lateDetailArray.size();
	}

	@Override
	public Object getItem(int arg0) {
		return arg0;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = arg1;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(resourceId, null);
        }
		
        TextView listTxt = (TextView) v.findViewById(R.id.textItemId);
        listTxt.setText(lateDetailArray.get(arg0).getReasonForLate());
		
		return v;
	}

}
