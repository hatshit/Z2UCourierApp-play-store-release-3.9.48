package com.zoom2u.slidemenu.offerrequesthandlr;

import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.zoom2u.slidemenu.BookingView;

/**
 * Created by Arun on 15/5/17.
 */

public class TimerForBid_Active {

    Context currentViewContext;
    public static RequestView_Adapter requestView_adapter;

    public TimerForBid_Active (Context currentViewContext){
        this.currentViewContext = currentViewContext;
      //  callTimerActiveBookingList();
    }

    // ************  Start timer for Bid request list *********
    public void callTimerActiveBookingList(){
        if(BookingView.handlerForTimeCounter != null)
            BookingView.handlerForTimeCounter = null;
        BookingView.handlerForTimeCounter = new Handler();
        BookingView.handlerForTimeCounter.postDelayed(TimerForBidActiveFor, 60*1000 );
    }

    Runnable TimerForBidActiveFor = new Runnable() {
        @Override
        public void run() {
            try {
                if(requestView_adapter != null)
                    requestView_adapter.notifyDataSetChanged();
                BookingView.handlerForTimeCounter.postDelayed(this, 60*1000);
            } catch (Exception e) {
               e.printStackTrace();
            }
        }
    };

    public void removeMenulogHandlerInActiveList(){
        try {
            if(BookingView.handlerForTimeCounter != null && TimerForBidActiveFor != null)
                BookingView.handlerForTimeCounter.removeCallbacks(TimerForBidActiveFor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
