package com.z2u.weeklycourierstats;

import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.zoom2u.services.ServiceWeeklyCourierUpdate;

public class CallWeeklyCourierStats {

	Context contxt;
	
	public CallWeeklyCourierStats(Context con){
		contxt = con;
	}
	
	BroadcastReceiver receiverForRefreshNewBookingList = null;
	public void weeklyCourierStatusNotify(){
			//********** ************  Courier weekly status used in notification view  *****************  ********//
		//	PushReceiver.contextOfPushReceiver = contxt;
			
			Intent serviceForWeeklyUpdate = new Intent(contxt, ServiceWeeklyCourierUpdate.class);
			contxt.startService(serviceForWeeklyUpdate);
			serviceForWeeklyUpdate = null;
			
			receiverForRefreshNewBookingList = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					try{ 
					   if(intent.getStringExtra("responseWeeklyupdate") != null && !intent.getStringExtra("responseWeeklyupdate").equals("")){
						   JSONObject jObjOfWeeklyUpdate = new JSONObject(intent.getStringExtra("responseWeeklyupdate"));
						   if(jObjOfWeeklyUpdate.getBoolean("success")){
							   Intent i = new Intent(contxt, WeeklyCourierUpdateView.class);
							   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							   i.putExtra("WeeklyCourierUpdateStr", intent.getStringExtra("responseWeeklyupdate"));
							   contxt.startActivity(i);
							   i = null;
						   }
						   jObjOfWeeklyUpdate = null;
					   }
			        }catch(Exception e){
			        	e.printStackTrace();
			        }
			if(receiverForRefreshNewBookingList != null){
				LocalBroadcastManager.getInstance(contxt).unregisterReceiver(receiverForRefreshNewBookingList);
				receiverForRefreshNewBookingList = null;
			}
		}
	};
			
			if(receiverForRefreshNewBookingList != null)
				LocalBroadcastManager.getInstance(contxt).registerReceiver((receiverForRefreshNewBookingList), new IntentFilter(ServiceWeeklyCourierUpdate.WEEKLY_COURIER_UPDATE_STR));
		//********** ************  Used in notification view  *****************  ********//
	}
}
