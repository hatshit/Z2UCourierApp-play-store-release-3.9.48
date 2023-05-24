package com.zoom2u.utility;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class WorkaroundMapFragment extends MapFragment implements OnMapReadyCallback {
    private OnTouchListener mListener;
   private OnWorkAroundMapReady onWorkAroundMapReady;
    private GoogleMap map;
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle savedInstance) {
        View layout = null;
        getMapAsync(this);
        try {
			layout = super.onCreateView(layoutInflater, viewGroup, savedInstance);
			TouchableWrapper frameLayout = new TouchableWrapper(getActivity());
			frameLayout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
 
			((ViewGroup) layout).addView(frameLayout,
			        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		
        } catch (Exception e) {
			e.printStackTrace();
		}
 
        return layout;
    }


 
    public WorkaroundMapFragment setListener(OnTouchListener listener) {
        mListener = listener;
		return null;

    }
    public void setMapReadyCallback(OnWorkAroundMapReady listener) {
        onWorkAroundMapReady = listener;
        if(map!=null)
            onWorkAroundMapReady.onMapReady(map);
    }

    public GoogleMap getMap() {
       return map;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        if (onWorkAroundMapReady!=null)
            onWorkAroundMapReady.onMapReady(map);
    }

    public interface OnTouchListener {
        public abstract void onTouch();
    }

    public interface OnWorkAroundMapReady{
        public void onMapReady(GoogleMap googleMap);
    }
 
    public class TouchableWrapper extends FrameLayout {
 
      public TouchableWrapper(Context context) {
        super(context);
      }
 
      @Override
      public boolean dispatchTouchEvent(MotionEvent event) {
        try {
			switch (event.getAction()) {
			  case MotionEvent.ACTION_DOWN:
			      mListener.onTouch();
			        break;
			  case MotionEvent.ACTION_UP:
			      mListener.onTouch();
			        break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
        return super.dispatchTouchEvent(event);
      }
    }
}