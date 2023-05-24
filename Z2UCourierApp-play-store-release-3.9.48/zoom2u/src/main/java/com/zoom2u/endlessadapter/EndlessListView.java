package com.zoom2u.endlessadapter;

import java.util.List;
import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.booking.vc.CompletedView;
import com.z2u.booking.vc.NewBookingView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.slidemenu.BookingView;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public class EndlessListView extends ListView implements OnScrollListener {
	
	Context context;
	private boolean isLoading;
	private EndLessListener listener;
	private EndlessAdapter_DropoffBookings completeBookingAdapter;

	public EndlessListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		this.setOnScrollListener(this);
		
		this.context = context;
	}

	public EndlessListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(this);
		this.context = context;
	}

	public EndlessListView(Context context) {
		super(context);		
		this.setOnScrollListener(this);
		this.context = context;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	//	4
	public void addNewData(List<DHL_SectionInterface> data) {
		try {
			completeBookingAdapter.addAll(data);
			completeBookingAdapter.notifyDataSetChanged();
			isLoading = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		try {
			if(getAdapter() == null)
				return;
			
			if(getAdapter().getCount() == 0)
				return;
			
			int l = visibleItemCount + firstVisibleItem;
            int count = CompletedView.endlessCount + 1;
            if(BookingView.bookingListArray != null){
				if(l >= totalItemCount && !isLoading && BookingView.bookingListArray.size() >= (20*count)){
					isLoading = true;      //	set progress boolean
					listener.loadData();   //	call interface method to load new data
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	2
	public void setListener(EndLessListener listener) {
		this.listener = listener;
	}	

	//	3
	public void setForDropOffBookingAdapter(EndlessAdapter_DropoffBookings adapter) {
		super.setAdapter(adapter);
		completeBookingAdapter = adapter;
	}

	//	interface
	public interface EndLessListener{
		public void loadData();
	}
}