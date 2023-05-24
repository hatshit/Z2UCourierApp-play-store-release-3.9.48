package com.zoom2u.slidemenu;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.z2u.booking.vc.ActiveBookingView;
import com.z2u.booking.vc.CompletedView;
import com.z2u.booking.vc.NewBookingView;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.booking.vc.endlesslistview.ActiveBooking_EndlessListview;
import com.z2u.booking.vc.endlesslistview.NewBooking_EndlessListView;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.BookingDetail_New;
import com.zoom2u.BookingHistory;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.endlessadapter.EndlessListView;
import com.zoom2u.services.ServiceForCourierBookingCount;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.slidemodel.SwipeListView;
import com.zoom2u.slidemodel.SwipeListView.SwipeListViewCallback;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BookingView extends Fragment implements OnClickListener {

    public static final int REQUEST_CODE_FOR_ACTIVE_DETAIL = 1021;
    public static final int REQUEST_CODE_FOR_NEW_BOOKING_DETAIL = 1020;

    public static ArrayList<DHL_SectionInterface> bookingListArray;

    RelativeLayout newBookingBtnLayout, activeBookingBtnLayout, completeBtnLayout;
    public TextView newBookingBtn, activeBookingBtn, completeBtn;
//	public ImageView newBookingNotifi;

    //*************  DHL sub items Active or Tried to deliver  ****************
    LinearLayout dhlTabsForActiveOrTriedToDeliver;
    TextView activeDHLBookings, triedToDeliverDHLBookings,online_offline_color;
    //*************  DHL sub items Active or Tried to deliver  ****************

    RelativeLayout mainLayoutBookingView, subBookingView;
    LinearLayout active_today_rl, active_dhl_rl, active_complete_rl, dhl_active_rl, dhl_tod_rl;
    RelativeLayout count_rl;
    TextView count;
    LinearLayout todayFutureBtnLayoutForActiveJobs;
    TextView noBookingAvailableTxt, onLineOffLineBtnForNewBooking, todayBookingBtn, dhlBookingBtn, tomorrowBookingBtn;
    LinearLayout online_offline,bookingTapButtons;
    NewBooking_EndlessListView newBookingListView;
    SwipeRefreshLayout swipeRefreshLayoutNew;
    ActiveBooking_EndlessListview activeBookingListView;
    SwipeRefreshLayout swipeRefreshLayoutActive;
    EndlessListView completedBookingListView;
    SwipeRefreshLayout swipeRefreshLayoutCompleted;
    LinearLayout onLineOffLineBtnForNewBooking_ll;
    ImageView dhlBookingCountBtn, searchDHLBookingByAWB;

    public static int bookingViewSelection = 1;
    public static boolean isCourierOnline = false;

    ProgressDialog progressDialogForOnOffline;

    SwipeListViewCallback swipeCallBackForNewBookingView;
    SwipeListViewCallback swipeCallBackForActiveBookingView;

    NewBookingView objNewBookingView;
    ActiveBookingView objActiveBookingView;
    CompletedView objCompletedBookingView;

    public static Handler handlerForTimeCounter;
    BroadcastReceiver receiverForDeliveryCount;
    TextView slideMenuTxt;

    View rootView = null;

    public BookingView() {
    }

//	public BookingView(TextView slideMenuTxt){
//		this.slideMenuTxt = slideMenuTxt;
//	}

    public void setSlideMenuChatCounterTxt(TextView slideMenuTxt) {
        this.slideMenuTxt = slideMenuTxt;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                try {
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED
                            && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                        if (objActiveBookingView != null) {
                            objActiveBookingView.openCamera();
                        }
                    } else
                        Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        showNewBookingCount();
        showActiveBookingCount();

        if (objActiveBookingView != null) {
            try {
                if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 2) {
                    BarcodeScanner.isScannedSuccessFully = false;
                    ActiveBookingView.getCurrentLocation(getActivity());
                    objActiveBookingView.callForTTDAttempt(((All_Bookings_DataModels) BookingView.bookingListArray.get(objActiveBookingView.itemSelectedInActiveBookingList)).isDoesAlcoholDeliveries());
                } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 4) {
                    BarcodeScanner.isScannedSuccessFully = false;
                    objActiveBookingView.takePic("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                } else if (BarcodeScanner.isScannedSuccessFully && BarcodeScanner.ScanAWBForPick == 5) {
                    BarcodeScanner.isScannedSuccessFully = false;
                    ActiveBookingView.getCurrentLocation(getActivity());
                    objActiveBookingView.atlDialogAlert();
                } else if (BarcodeScanner.ScanAWBForPick == 6 && ActiveBookingView.photo != null) {
                    BarcodeScanner.ScanAWBForPick = 1;
                    objActiveBookingView.pickAndDropBookingAfterTakePkgPhoto();
                }


                //*************  Register local broadcast receiver   ****************//
                try {
                    if (objActiveBookingView.receiverForSilentNotificationActiveList != null)
                        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((objActiveBookingView.receiverForSilentNotificationActiveList), new IntentFilter(ServiceToUpdate_ActiveBookingList.SILENT_NOTIFICATION));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SlideMenuZoom2u.setCourierToOnlineForChat();
        SlideMenuZoom2u.countChatBookingView = slideMenuTxt;
        Model_DeliveriesToChat.showExclamationForUnreadChat(slideMenuTxt);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        try {
            outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
            outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
            outState.putBoolean("Routific", WebserviceHandler.isRoutific);
            outState.putString("imageFilePath", Functional_Utility.mCurrentPhotoPath);        // *** for Camera crash on samsung galaxy S4 25-Jan-2017
//			outState.putParcelableArrayList("BookingArryList", BookingView.bookingListArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (objNewBookingView != null)
            unRegisterBookingViewReceiver(objNewBookingView.receiverForRefreshNewBookingList);   //******* Unregister NewBooking view receiver
        if (objActiveBookingView != null)
            unRegisterBookingViewReceiver(objActiveBookingView.receiverForSilentNotificationActiveList);    //******* Unregister ActiveBooking view receiver
    }

    /* **************************
    Unregister NewBooking and ActiveBooking view receiver before destroy
    **************************** */
    private void unRegisterBookingViewReceiver(BroadcastReceiver receiverForBookingViewListUpdate) {
        try {
            if (receiverForBookingViewListUpdate != null)
                unregisterReceivers(receiverForBookingViewListUpdate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void inItNewBookingView() {
        //	removeTimerTaskToUpdateUI();
        newBookingListView.setVisibility(View.VISIBLE);
        activeBookingListView.setVisibility(View.GONE);
        completedBookingListView.setVisibility(View.GONE);
        removeActiveBookingListCounter();
        dhlBookingCountBtn.setVisibility(View.GONE);
        searchDHLBookingByAWB.setVisibility(View.GONE);
        if (objActiveBookingView != null)
            objActiveBookingView.hideDHLTabs();
        if (objNewBookingView != null)
            objNewBookingView = null;
        swipeRefreshLayoutNew.setVisibility(View.VISIBLE);
        swipeRefreshLayoutActive.setVisibility(View.GONE);
        swipeRefreshLayoutCompleted.setVisibility(View.GONE);
        objNewBookingView = new NewBookingView(getActivity(), noBookingAvailableTxt,
                newBookingListView, onLineOffLineBtnForNewBooking,onLineOffLineBtnForNewBooking_ll, swipeRefreshLayoutNew,online_offline_color,count_rl,count);
    }

    void inItActiveBookingView() {
        removeNewBookingListCounter();
        newBookingListView.setVisibility(View.GONE);
        activeBookingListView.setVisibility(View.VISIBLE);
        completedBookingListView.setVisibility(View.GONE);
        if (objActiveBookingView != null)
            objActiveBookingView.hideDHLTabs();
        if (objActiveBookingView != null)
            objActiveBookingView = null;
        swipeRefreshLayoutNew.setVisibility(View.GONE);
        swipeRefreshLayoutActive.setVisibility(View.VISIBLE);
        swipeRefreshLayoutCompleted.setVisibility(View.GONE);
        objActiveBookingView = new ActiveBookingView(BookingView.this, getActivity(), noBookingAvailableTxt, subBookingView,
                activeBookingListView, todayBookingBtn, dhlBookingBtn, tomorrowBookingBtn, dhlBookingCountBtn, searchDHLBookingByAWB,
                dhlTabsForActiveOrTriedToDeliver, activeDHLBookings, triedToDeliverDHLBookings, active_today_rl, active_dhl_rl, active_complete_rl,
                todayFutureBtnLayoutForActiveJobs, dhl_active_rl, dhl_tod_rl, swipeRefreshLayoutActive,count_rl);
    }

    void inItCompletedBookingView() {
        removeNewBookingListCounter();
        newBookingListView.setVisibility(View.GONE);
        activeBookingListView.setVisibility(View.GONE);
        completedBookingListView.setVisibility(View.VISIBLE);
        dhlBookingCountBtn.setVisibility(View.GONE);
        searchDHLBookingByAWB.setVisibility(View.GONE);
        if (objActiveBookingView != null)
            objActiveBookingView.hideDHLTabs();
        removeActiveBookingListCounter();
        if (objCompletedBookingView != null)
            objCompletedBookingView = null;
        swipeRefreshLayoutNew.setVisibility(View.GONE);
        swipeRefreshLayoutActive.setVisibility(View.GONE);
        swipeRefreshLayoutCompleted.setVisibility(View.VISIBLE);

        objCompletedBookingView = new CompletedView(getActivity(), completedBookingListView, noBookingAvailableTxt, swipeRefreshLayoutCompleted,count_rl);
    }

    // *********** Remove timer from Active booking list *********
    void removeActiveBookingListCounter() {
        if (objActiveBookingView != null) {
            objActiveBookingView.destroyActionMode();
            objActiveBookingView.removeMenulogHandlerInActiveList();
        }
    }

    void removeNewBookingListCounter() {
        if (objNewBookingView != null)
            objNewBookingView.removeMenulogHandlerForNewBookingList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.bookingview, container, false);
        try {
            clearBookingArray();
            if (savedInstanceState != null) {
                try {
                    bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
                    ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                    WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                    Functional_Utility.mCurrentPhotoPath = savedInstanceState.getString("imageFilePath");   // *** for Camera crash on samsung galaxy S4 25-Jan-2017
                    if (savedInstanceState.getParcelableArrayList("BookingArryList") != null)
                        BookingView.bookingListArray = savedInstanceState.getParcelableArrayList("BookingArryList");
                    if (LoginZoomToU.NOVA_BOLD == null)
                        LoginZoomToU.staticFieldInit(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            inItBookingView(rootView);

            //**********  Call local broadcast on silent notification for calculated ETA  ********//
            receiverForDeliveryCount = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    try {
                        showNewBookingCount();
                        showActiveBookingCount();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            //*************  Register local broadcast receiver   ****************//
            if (receiverForDeliveryCount != null)
                LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiverForDeliveryCount), new IntentFilter(ServiceForCourierBookingCount.COURIER_BOOKING_COUNT));

            setRetainInstance(true);    // *** for Camera crash on samsung galaxy S4 25-Jan-2017
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //*************  Register local broadcast receiver   ****************//
        if (receiverForDeliveryCount != null)
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiverForDeliveryCount);
    }

    private void showNewBookingCount() {
        if (newBookingBtn != null && bookingViewSelection != 1) {
            if (WebserviceHandler.NEWBOOKING_COUNT > 0) {
                newBookingBtn.setCompoundDrawablePadding(10);
                newBookingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.infoicon, 0);
            } else {
                newBookingBtn.setCompoundDrawablePadding(0);
                newBookingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_ACTIVE_DETAIL:
                if (resultCode == getActivity().RESULT_OK) {
                    showNewBookingCount();
                    showActiveBookingCount();
                    if (bookingViewSelection == 2 && objActiveBookingView != null) {
                        objActiveBookingView.refreshActiveBookingList();
                    }
                }
                break;
            case REQUEST_CODE_FOR_NEW_BOOKING_DETAIL:
                if (resultCode == getActivity().RESULT_OK) {
                    showNewBookingCount();
                    showActiveBookingCount();
                    if (bookingViewSelection == 1 && objNewBookingView != null) {
                        CompletedView.endlessCount = 0;
                        objNewBookingView.inItNewBookingArray();
                        objNewBookingView.callNewBookingAsyncTask();
                    }
                }
                break;
            default:
                if (resultCode == getActivity().RESULT_OK) {
                    if (bookingViewSelection == 2 && objActiveBookingView != null)
                        objActiveBookingView.getCameraPic_OnActivityResult(requestCode, data);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
//		if(bookingViewSelection == 2 && objActiveBookingView != null && resultCode == RESULT_OK)
//			objActiveBookingView.getCameraPic_OnActivityResult(requestCode, data);
    }

    public void showActiveBookingCount() {
        if (activeBookingBtn != null) {
            if (WebserviceHandler.ACTIVEBOOKING_COUNT > 0)
                activeBookingBtn.setText("Active (" + WebserviceHandler.ACTIVEBOOKING_COUNT + ")");
            else
                activeBookingBtn.setText("Active");
        }
    }

    void inItBookingView(View rootView) {
        try {
            bookingTapButtons=rootView.findViewById(R.id.bookingTapButtons);
            count_rl=rootView.findViewById(R.id.count_rl);
            count = rootView.findViewById(R.id.count);

            if (dhl_active_rl == null)
                dhl_active_rl = (LinearLayout) rootView.findViewById(R.id.dhl_active_rl);
            if (dhl_tod_rl == null)
                dhl_tod_rl = (LinearLayout) rootView.findViewById(R.id.dhl_tod_rl);

            if (active_today_rl == null)
                active_today_rl = (LinearLayout) rootView.findViewById(R.id.active_today_rl);
            if (active_dhl_rl == null)
                active_dhl_rl = (LinearLayout) rootView.findViewById(R.id.active_dhl_rl);
            if (active_complete_rl == null)
                active_complete_rl = (LinearLayout) rootView.findViewById(R.id.active_complete_rl);
            if (mainLayoutBookingView == null)
                mainLayoutBookingView = (RelativeLayout) rootView.findViewById(R.id.mainLayoutBookingView);
            if (subBookingView == null)
                subBookingView = (RelativeLayout) rootView.findViewById(R.id.subBookingView);
            subBookingView.setVisibility(View.GONE);
            if (newBookingBtnLayout == null)
                newBookingBtnLayout = (RelativeLayout) rootView.findViewById(R.id.newBookingBtnLayout);
            newBookingBtnLayout.setOnClickListener(this);
            if (activeBookingBtnLayout == null)
                activeBookingBtnLayout = (RelativeLayout) rootView.findViewById(R.id.activeBookingBtnLayout);
            activeBookingBtnLayout.setOnClickListener(this);
            if (completeBtnLayout == null)
                completeBtnLayout = (RelativeLayout) rootView.findViewById(R.id.completeBtnLayout);
            completeBtnLayout.setOnClickListener(this);

            if (newBookingBtn == null)
                newBookingBtn = rootView.findViewById(R.id.newBookingBtn);
            newBookingBtn.setText("New");

//			if (newBookingNotifi == null)
//				newBookingNotifi = (ImageView) rootView.findViewById(R.id.newBookingNotifi);
            showNewBookingCount();

            inItBookingButtons(newBookingBtn);
            if (activeBookingBtn == null)
                activeBookingBtn = rootView.findViewById(R.id.activeBookingBtn);
            showActiveBookingCount();
            inItBookingButtons(activeBookingBtn);
            if (completeBtn == null)
                completeBtn = rootView.findViewById(R.id.completeBtn);
            inItBookingButtons(completeBtn);

            if (dhlBookingCountBtn == null)
                dhlBookingCountBtn = (ImageView) rootView.findViewById(R.id.dhlBookingCount);
            dhlBookingCountBtn.setOnClickListener(this);

            if (searchDHLBookingByAWB == null)
                searchDHLBookingByAWB = (ImageView) rootView.findViewById(R.id.searchDHLBookingByAWB);

            if (noBookingAvailableTxt == null)
                noBookingAvailableTxt = (TextView) rootView.findViewById(R.id.noBookingAvailableTxt);

            online_offline = rootView.findViewById(R.id.online_offline);
            online_offline.setOnClickListener(this);

            if (onLineOffLineBtnForNewBooking == null)
                onLineOffLineBtnForNewBooking = (TextView) rootView.findViewById(R.id.onLineOffLineBtnForNewBooking);
            onLineOffLineBtnForNewBooking_ll=rootView.findViewById(R.id.onLineOffLineBtnForNewBooking_ll);
            online_offline_color=rootView.findViewById(R.id.online_offline_color);

            if (BookingView.isCourierOnline == true) {
                onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg);
                online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton_green);
                onLineOffLineBtnForNewBooking.setText("Online");
            } else {
                onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg_gray);
                online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton);
                onLineOffLineBtnForNewBooking.setText("Offline");
            }

            if (todayBookingBtn == null)
                todayBookingBtn = (TextView) rootView.findViewById(R.id.todayBookingBtn);

            if (dhlBookingBtn == null)
                dhlBookingBtn = (TextView) rootView.findViewById(R.id.dhlBookingBtn);

            if (tomorrowBookingBtn == null)
                tomorrowBookingBtn = (TextView) rootView.findViewById(R.id.tomorrowBookingBtn);

            if (newBookingListView != null)
                newBookingListView = null;
            newBookingListView = (NewBooking_EndlessListView) rootView.findViewById(R.id.newBookingListView);
            newBookingListView.setVisibility(View.VISIBLE);
            newBookingListView.setScrollingCacheEnabled(false);
            newBookingListView.setAnimationCacheEnabled(false);
            swipeRefreshLayoutNew = rootView.findViewById(R.id.new_booking_swipe_refresh);
            swipeRefreshLayoutActive = rootView.findViewById(R.id.active_booking_swipe_refresh);
            swipeRefreshLayoutCompleted = rootView.findViewById(R.id.completed_booking_swipe_refresh);
            if (activeBookingListView != null)
                activeBookingListView = null;
            activeBookingListView = (ActiveBooking_EndlessListview) rootView.findViewById(R.id.activeBookingListView);
            activeBookingListView.setVisibility(View.VISIBLE);
            activeBookingListView.setScrollingCacheEnabled(false);
            activeBookingListView.setAnimationCacheEnabled(false);

            if (completedBookingListView != null)
                completedBookingListView = null;
            completedBookingListView = (EndlessListView) rootView.findViewById(R.id.completedBookingListView);
            completedBookingListView.setVisibility(View.VISIBLE);
            completedBookingListView.setScrollingCacheEnabled(false);
            completedBookingListView.setAnimationCacheEnabled(false);

            if (todayFutureBtnLayoutForActiveJobs == null)
                todayFutureBtnLayoutForActiveJobs = (LinearLayout) rootView.findViewById(R.id.todayFutureBtnLayoutForActiveJobs);
            todayFutureBtnLayoutForActiveJobs.setVisibility(View.GONE);

            //*************  DHL sub items Active or Tried to deliver  ****************
            if (dhlTabsForActiveOrTriedToDeliver == null)
                dhlTabsForActiveOrTriedToDeliver = (LinearLayout) rootView.findViewById(R.id.dhlTabsForActiveOrTriedToDeliver);
            dhlTabsForActiveOrTriedToDeliver.setVisibility(View.GONE);
            if (activeDHLBookings == null)
                activeDHLBookings = (TextView) rootView.findViewById(R.id.activeDHLBookings);

            if (triedToDeliverDHLBookings == null)
                triedToDeliverDHLBookings = (TextView) rootView.findViewById(R.id.triedToDeliverDHLBookings);

            //*************  DHL sub items Active or Tried to deliver  ****************
            //setTabBackground();
            if (bookingViewSelection == 1)
                newBookingListSelection();                // New booking list functionality
            else if (bookingViewSelection == 2)
                activeBookingListSelection();            // Active booking list functionality
            else if (bookingViewSelection == 3)
                completeBookingListSelection();            // Complete booking list functionality

            newBookingListView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            newBookingListView.smoothScrollBy(0, 0);
                        case MotionEvent.ACTION_DOWN:
                            newBookingListView.smoothScrollBy(0, 0);
                            break;
                    }
                    return true;
                }
            });

            activeBookingListView.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_UP:
                            activeBookingListView.smoothScrollBy(0, 0);
                        case MotionEvent.ACTION_DOWN:
                            activeBookingListView.smoothScrollBy(0, 0);
                            break;
                    }
                    return true;
                }
            });

//            completedBookingListView.setOnItemClickListener(new OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Intent callPickedUpDetail = new Intent(getActivity(), BookingHistory.class);
//                    callPickedUpDetail.putExtra("positionCompleteBooking", position);
//                    startActivity(callPickedUpDetail);
//                    callPickedUpDetail = null;
//                    getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
//                }
//            });

            inItSwipeCallBackForNewBookingList(newBookingListView);            // Swipe callback for New booking list
            inItSwipeCallBackForActiveBookingList(activeBookingListView);            // Swipe call back for Active booking list
            PushReceiver.isCameraOpen = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //********** Swipe left or right callback for New booking list view ***********
    private void inItSwipeCallBackForNewBookingList(final NewBooking_EndlessListView newBookingListView) {
        swipeCallBackForNewBookingView = new SwipeListViewCallback() {
            @Override
            public void onSwipeItemRight(boolean isleft, int position) {
                objNewBookingView.newBookingListAdapter.onSwipeItemRight(isleft, position);
            }

            @Override
            public void onSwipeItemLeft(boolean isRight, int position) {
                objNewBookingView.newBookingListAdapter.onSwipeItemLeft(isRight, position);
            }

            @Override
            public void onItemClickListener(ListAdapter adapter, int position) {
                Intent i = new Intent(getActivity(), BookingDetail_New.class);
                Bundle newBookingDetailBundle = new Bundle();
                newBookingDetailBundle.putInt("position", position);
                i.putExtra("NewBookingBundle", newBookingDetailBundle);
                startActivityForResult(i, REQUEST_CODE_FOR_NEW_BOOKING_DETAIL);
                newBookingDetailBundle = null;
                i = null;
            }

            @Override
            public ListView getListView() {
                return newBookingListView;
            }

            @Override
            public void onItemLongClickListener(ListAdapter adapter, int position) {
            }
        };

        SwipeListView swipeListView = new SwipeListView(getActivity(), swipeCallBackForNewBookingView);
        swipeListView.exec();
        swipeCallBackForNewBookingView = null;
        swipeListView = null;
    }

    //********** Swipe left or right callback for Active booking list view ***********
    private void inItSwipeCallBackForActiveBookingList(final ActiveBooking_EndlessListview activeBookingListView) {
        swipeCallBackForActiveBookingView = new SwipeListViewCallback() {
            @Override
            public void onSwipeItemRight(boolean isleft, int position) {
                objActiveBookingView.activeBookingListAdapter.onSwipeItemRight(isleft, position);
            }

            @Override
            public void onSwipeItemLeft(boolean isRight, int position) {
                objActiveBookingView.activeBookingListAdapter.onSwipeItemLeft(isRight, position);

            }

            @Override
            public void onItemClickListener(ListAdapter adapter, int position) {
                onClickEventOfActiveBookingList(position);
            }

            @Override
            public ListView getListView() {
                return activeBookingListView;
            }

            @Override
            public void onItemLongClickListener(ListAdapter adapter, int position) {
                try {
                    All_Bookings_DataModels sourceType = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position));

                    if (!(sourceType.getSource().equals("DHL") || sourceType.getRunType().equals("SMARTSORT"))) {

                        if (ActiveBookingView.arrayOfBookingId != null) {
                            if (ActiveBookingView.arrayOfBookingId.size() > 0) {
                                objActiveBookingView.destroyActionMode();
                                ActiveBookingView.arrayOfBookingId.clear();
                            }
                        }
                        objActiveBookingView.multiplePickBookingSelection(position);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        SwipeListView swipeListView = new SwipeListView(getActivity(), swipeCallBackForActiveBookingView);
        swipeListView.exec();
        swipeCallBackForActiveBookingView = null;
        swipeListView = null;
    }

    /********** On-click event of Active booking list ***********/
    protected void onClickEventOfActiveBookingList(int position) {
        try {
            if (ActiveBookingView.mActionMode != null) {
                All_Bookings_DataModels bookingsDataModel = (All_Bookings_DataModels) bookingListArray.get(position);
                if (objActiveBookingView.statusStr.equals(bookingsDataModel.getStatus())
                        && objActiveBookingView.customerId.equals(bookingsDataModel.getCustomerId())) {
                    objActiveBookingView.itemSelectedInActiveBookingList = position;
                    // Capture total checked items
                    if (objActiveBookingView.adapterPosition.contains(position))
                        objActiveBookingView.adapterPosition.remove(position);
                    else
                        objActiveBookingView.adapterPosition.add(position);

                    if (ActiveBookingView.arrayOfBookingId.contains(bookingsDataModel.getBookingId())) {
                        objActiveBookingView.checkedCount--;
                        objActiveBookingView.activeBookingListAdapter.notifyDataSetChanged();
                        ActiveBookingView.arrayOfBookingId.remove(bookingsDataModel.getBookingId());
                        ((RelativeLayout) objActiveBookingView.activeBookingListAdapter.getView(position, null, null)).setBackgroundColor(Color.WHITE);
                        // Set the CAB title according to total checked items
                        ActiveBookingView.mActionMode.setTitle(objActiveBookingView.checkedCount + "  selected");
                        if (ActiveBookingView.arrayOfBookingId.size() <= 0) {
                            objActiveBookingView.statusStr = "";
                            objActiveBookingView.customerId = "";
                            ActiveBookingView.arrayOfBookingId.clear();
                            ActiveBookingView.mActionMode.finish();
                            objActiveBookingView.checkedCount = 0;
                            ActiveBookingView.mActionMode = null;
                            objActiveBookingView.itemSelectedInActiveBookingList = 0;
                            if (objActiveBookingView.adapterPosition != null)
                                objActiveBookingView.adapterPosition.clear();
                        }
                    } else {
                        objActiveBookingView.checkedCount++;
                        ActiveBookingView.arrayOfBookingId.add(bookingsDataModel.getBookingId());
                        ((RelativeLayout) objActiveBookingView.activeBookingListAdapter.getView(position, null, null)).setBackgroundColor(Color.BLUE);
                        ActiveBookingView.mActionMode.setTitle(objActiveBookingView.checkedCount + "  selected");
                    }
                    objActiveBookingView.activeBookingListAdapter.notifyDataSetChanged();
                }
            } else {
                if (objActiveBookingView != null)
                    unRegisterBookingViewReceiver(objActiveBookingView.receiverForSilentNotificationActiveList);    //******* Unregister ActiveBooking view receiver
                Intent i = new Intent(getActivity(), ActiveBookingDetail_New.class);
                i.putExtra("RouteViewCalling", 0);
                i.putExtra("positionActiveBooking", position);
                if (LoginZoomToU.filterDayActiveListStr.equals("DeliveryRuns") && LoginZoomToU.getDHLBookingStr.equals("Tried to deliver"))
                    i.putExtra("IsReturnToDHL", true);
                else
                    i.putExtra("IsReturnToDHL", false);
                startActivityForResult(i, REQUEST_CODE_FOR_ACTIVE_DETAIL);
                i = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTabBackgroundgary() {
        newBookingBtnLayout.setBackgroundResource(R.color.base_color_gray);
        activeBookingBtnLayout.setBackgroundResource(R.color.base_color_gray);
        completeBtnLayout.setBackgroundResource(R.color.base_color_gray);
        newBookingBtn.setBackgroundResource(R.drawable.selected_background_gray);
        activeBookingBtn.setBackgroundResource(R.color.base_color_gray);
        completeBtn.setBackgroundResource(R.color.base_color_gray);
    }

    public void setTabBackgroundblue() {
        newBookingBtnLayout.setBackgroundResource(R.color.base_color1);
        activeBookingBtnLayout.setBackgroundResource(R.color.base_color1);
        completeBtnLayout.setBackgroundResource(R.color.base_color1);
        newBookingBtn.setBackgroundResource(R.drawable.selected_background);
        activeBookingBtn.setBackgroundResource(R.color.base_color1);
        completeBtn.setBackgroundResource(R.color.base_color1);
    }

   public void setTabBackground() {
        if (MainActivity.isIsBackGroundGray()) {
            newBookingBtnLayout.setBackgroundResource(R.color.base_color_gray);
            activeBookingBtnLayout.setBackgroundResource(R.color.base_color_gray);
            completeBtnLayout.setBackgroundResource(R.color.base_color_gray);
            newBookingBtn.setBackgroundResource(R.drawable.selected_background_gray);
            activeBookingBtn.setBackgroundResource(R.color.base_color_gray);
            completeBtn.setBackgroundResource(R.color.base_color_gray);
        } else {
            newBookingBtnLayout.setBackgroundResource(R.color.base_color1);
            activeBookingBtnLayout.setBackgroundResource(R.color.base_color1);
            completeBtnLayout.setBackgroundResource(R.color.base_color1);
            newBookingBtn.setBackgroundResource(R.drawable.selected_background);
            activeBookingBtn.setBackgroundResource(R.color.base_color1);
            completeBtn.setBackgroundResource(R.color.base_color1);
        }
    }


    // Initialize booking buttons
    void inItBookingButtons(TextView bookingButtons) {
        bookingButtons.setOnClickListener(this);
    }

    // Focus on booking buttons header
    void focusOnNewBookingBtn(TextView bookingBtnTxtView, RelativeLayout selectViewLayout) {
        if (MainActivity.isIsBackGroundGray()) {
            bookingBtnTxtView.setBackgroundResource(R.drawable.selected_background_gray);
            selectViewLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            bookingBtnTxtView.setBackgroundResource(R.drawable.selected_background);
            selectViewLayout.setBackgroundResource(R.color.base_color1);
        }
    }

    // Clear focus from booking buttons header
    void clearFocusFromNewBookingBtn(TextView bookingBtnTxtView, RelativeLayout selectViewLayout) {
        if (MainActivity.isIsBackGroundGray()) {
            bookingBtnTxtView.setBackgroundResource(R.color.base_color_gray);
            selectViewLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            bookingBtnTxtView.setBackgroundResource(R.color.base_color1);
            selectViewLayout.setBackgroundResource(R.color.base_color1);
        }
    }

    // Focus on booking buttons header
    void focusOnBookingBtn(TextView bookingBtnTxtView, RelativeLayout selectViewLayout) {
        if (MainActivity.isIsBackGroundGray()) {
            bookingBtnTxtView.setBackgroundResource(R.drawable.selected_background_gray);
            selectViewLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            bookingBtnTxtView.setBackgroundResource(R.drawable.selected_background);
            selectViewLayout.setBackgroundResource(R.color.base_color1);
        }
    }

    // Clear focus from booking buttons header
    void clearFocusFromBookingBtn(TextView bookingBtnTxtView, RelativeLayout selectViewLayout) {
        if (MainActivity.isIsBackGroundGray()) {
            bookingBtnTxtView.setBackgroundResource(R.color.base_color_gray);
            bookingBtnTxtView.setTextColor(Color.parseColor("#FFFFFF"));
            selectViewLayout.setBackgroundResource(R.color.base_color_gray);
        } else {
            bookingBtnTxtView.setBackgroundResource(R.color.base_color1);
            bookingBtnTxtView.setTextColor(Color.parseColor("#FFFFFF"));
            selectViewLayout.setBackgroundResource(R.color.base_color1);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.newBookingBtnLayout:
                if (bookingViewSelection != 1) {
                    newBookingBtn.setText("New");
                    WebserviceHandler.NEWBOOKING_COUNT = 0;
                    newBookingListSelection();
                }
                break;
            case R.id.activeBookingBtnLayout:
                if (bookingViewSelection != 2) {
                    LoginZoomToU.filterDayActiveListStr = "today";
                    LoginZoomToU.activeBookingTab = 0;
                    activeBookingListSelection();
                }
                break;
            case R.id.completeBtnLayout:
                if (bookingViewSelection != 3)
                    completeBookingListSelection();
                break;
            case R.id.newBookingBtn:
                if (bookingViewSelection != 1) {
                    newBookingBtn.setText("New");
                    WebserviceHandler.NEWBOOKING_COUNT = 0;
                    newBookingListSelection();
                }
                break;
            case R.id.activeBookingBtn:
                if (bookingViewSelection != 2) {
                    LoginZoomToU.filterDayActiveListStr = "today";
                    LoginZoomToU.activeBookingTab = 0;
                    activeBookingListSelection();
                }
                break;
            case R.id.completeBtn:
                if (bookingViewSelection != 3)
                    completeBookingListSelection();
                break;
            case R.id.online_offline:
                dialogForCourierOnline();
                break;
            case R.id.dhlBookingCount:
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                    if (dhlBookingArray == null)
                        dhlBookingArray = new ArrayList<HashMap<String, Object>>();
                    else
                        dhlBookingArray.clear();
                    DHLBookingSuburbList();
                } else
                    DialogActivity.alertDialogView(getActivity(), "No Network!", "No Network connection, Please try again later.");
                break;
            default:
                break;
        }
    }

    /************ Clear main booking array **********/
    void clearBookingArray() {
        if (bookingListArray != null)
            bookingListArray.clear();
        else
            bookingListArray = new ArrayList<DHL_SectionInterface>();
    }

    /************ New booking list selection **********/
    void newBookingListSelection() {
        clearBookingArray();
        inItNewBookingView();
        bookingViewSelection = 1;
        focusOnNewBookingBtn(newBookingBtn, newBookingBtnLayout);
        clearFocusFromBookingBtn(activeBookingBtn, activeBookingBtnLayout);
        clearFocusFromBookingBtn(completeBtn, completeBtnLayout);
        todayFutureBtnLayoutForActiveJobs.setVisibility(View.GONE);
        onLineOffLineBtnForNewBooking.setVisibility(View.VISIBLE);
        if (objActiveBookingView != null) {
            unregisterReceivers(objActiveBookingView.receiverForSilentNotificationActiveList);
            objActiveBookingView = null;
        }
        if (objCompletedBookingView != null)
            objCompletedBookingView = null;
    }

    /************ Active booking list selection **********/
    void activeBookingListSelection() {
        clearBookingArray();
        inItActiveBookingView();
        bookingViewSelection = 2;
        focusOnBookingBtn(activeBookingBtn, activeBookingBtnLayout);
        clearFocusFromNewBookingBtn(newBookingBtn, newBookingBtnLayout);
        clearFocusFromBookingBtn(completeBtn, completeBtnLayout);
        todayFutureBtnLayoutForActiveJobs.setVisibility(View.VISIBLE);
        onLineOffLineBtnForNewBooking.setVisibility(View.VISIBLE);
        stopRefreshingNewBookingList();
        //	visibleNewBookingCounter();
        if (objCompletedBookingView != null)
            objCompletedBookingView = null;
    }

//	private void visibleNewBookingCounter() {
//		if (WebserviceHandler > 0) {
//			newBookingBtn.setCompoundDrawablePadding(10);
//			newBookingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.exclamation_newoffer, 0);
//		} else {
//			newBookingBtn.setCompoundDrawablePadding(0);
//			newBookingBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
//		}
//	}

    private void stopRefreshingNewBookingList() {
        //	removeTimerTaskToUpdateUI();
        if (objNewBookingView != null) {
            unregisterReceivers(objNewBookingView.receiverForRefreshNewBookingList);
            objNewBookingView = null;
        }
    }

    void unregisterReceivers(BroadcastReceiver receiver) {
        try {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



	/************ Complete booking list selection **********/
	void completeBookingListSelection() {
		CompletedView.endlessCount = 0;
		clearBookingArray();
		inItCompletedBookingView();
		bookingViewSelection = 3;
		focusOnBookingBtn(completeBtn, completeBtnLayout);
		clearFocusFromBookingBtn(activeBookingBtn, activeBookingBtnLayout);
		clearFocusFromNewBookingBtn(newBookingBtn, newBookingBtnLayout);
		todayFutureBtnLayoutForActiveJobs.setVisibility(View.GONE);
		onLineOffLineBtnForNewBooking.setVisibility(View.VISIBLE);
		stopRefreshingNewBookingList();
		//	visibleNewBookingCounter();
		if (objActiveBookingView != null) {
			unregisterReceivers(objActiveBookingView.receiverForSilentNotificationActiveList);
			objActiveBookingView = null;
		}
	}
	
	private void callToWorkOnlineAsyncTask(){
		/*WorkOnlineOfflineAsyncTask workOnlineOfflineAsyncTask = new WorkOnlineOfflineAsyncTask();
		workOnlineOfflineAsyncTask.execute();
		workOnlineOfflineAsyncTask = null;*/
		WorkOnlineOfflineAsyncTask();
	}
	
	// In-it New booking progress dialog to show
		public void inItNewBookingProgressDialog() {
			if(progressDialogForOnOffline != null)
				progressDialogForOnOffline = null;
			progressDialogForOnOffline = new ProgressDialog(getActivity());
			Custom_ProgressDialogBar.inItProgressBar(progressDialogForOnOffline);
		}
				
		// Dismiss New booking progress dialog
		public void dismissNewBookingProgressDialog() {
			if(progressDialogForOnOffline != null)
				if(progressDialogForOnOffline.isShowing())
					Custom_ProgressDialogBar.dismissProgressBar(progressDialogForOnOffline);
		}

		private void WorkOnlineOfflineAsyncTask(){

			final String[] responseForWorkOnlineOffline = {"0"};

			new MyAsyncTasks(){
				@Override
				public void onPreExecute() {
					inItNewBookingProgressDialog();
				}

				@Override
				public void doInBackground() {
					try {
						WebserviceHandler webServiceHandler = new WebserviceHandler();
						responseForWorkOnlineOffline[0] = webServiceHandler.SetIsCourierOnline(isCourierOnline);
					}catch(Exception e){
						responseForWorkOnlineOffline[0] = "0";
						e.printStackTrace();
					}
				}

				@Override
				public void onPostExecute() {
					try {
						if(!responseForWorkOnlineOffline[0].equals("0")){
							JSONObject responseJOBJ = new JSONObject(responseForWorkOnlineOffline[0]);
							if(responseJOBJ.getBoolean("success") == true){
								if (responseJOBJ.getBoolean("isOnline") == true) {
									onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg);
									onLineOffLineBtnForNewBooking.setText("Online");
									online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton_green);
									isCourierOnline = true;
								}else{
									onLineOffLineBtnForNewBooking_ll.setBackgroundResource(R.drawable.roundedskybluebg_gray);
									onLineOffLineBtnForNewBooking.setText("Offline");
									online_offline_color.setBackgroundResource(R.drawable.circleredbgbutton);
									isCourierOnline = false;
								}
							}
							responseJOBJ = null;
						}
					} catch (Exception e) {
						e.printStackTrace();
						DialogActivity.alertDialogView(getActivity(), "Server error !", "Please try later!");
					}finally{
						try{
							dismissNewBookingProgressDialog();
						}catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			}.execute();

		}

    Dialog dialogForCourierOnline = null;

    // Dialog for selecting courier online/off-line
    public void dialogForCourierOnline() {
        if (dialogForCourierOnline != null)
            dialogForCourierOnline = null;
        dialogForCourierOnline = new Dialog(getActivity(), R.style.DialogSlideAnim);
        dialogForCourierOnline.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogForCourierOnline.setContentView(R.layout.checkonline_dialog);

        Window window = dialogForCourierOnline.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        wlp = null;
        RelativeLayout onLineBtn = (RelativeLayout) dialogForCourierOnline.findViewById(R.id.onLineBtn);
        RelativeLayout offLineBtn = (RelativeLayout) dialogForCourierOnline.findViewById(R.id.offLineBtn);
        RelativeLayout rlActionSheet = (RelativeLayout) dialogForCourierOnline.findViewById(R.id.rlActionSheet);
        ImageView online_check = dialogForCourierOnline.findViewById(R.id.online_check);
        ImageView offline_check = dialogForCourierOnline.findViewById(R.id.offline_check);

        dialogForCourierOnline.findViewById(R.id.parent_view).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForCourierOnline.dismiss();
            }
        });

        if (BookingView.isCourierOnline == true) {
            rlActionSheet.setBackgroundResource(R.drawable.roundedwhite);
            online_check.setVisibility(View.VISIBLE);
            offline_check.setVisibility(View.GONE);

        } else {
            rlActionSheet.setBackgroundResource(R.drawable.roundedwhite);
            online_check.setVisibility(View.GONE);
            offline_check.setVisibility(View.VISIBLE);
        }


        onLineBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForCourierOnline.dismiss();
                dialogForCourierOnline = null;
                isCourierOnline = true;
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    callToWorkOnlineAsyncTask();
                else
                    DialogActivity.alertDialogView(getActivity(), "No Network !", "No Network connection, Please try again later.");
            }
        });

        offLineBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogForCourierOnline.dismiss();
                dialogForCourierOnline = null;
                isCourierOnline = false;
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    callToWorkOnlineAsyncTask();
                else
                    DialogActivity.alertDialogView(getActivity(), "No Network !", "No Network connection, Please try again later.");
            }
        });


        dialogForCourierOnline.show();
    }

	private void DHLBookingSuburbList(){

		final String[] responseForDHLSuburbList = {"0"};

		new MyAsyncTasks(){
			@Override
			public void onPreExecute() {
				addDHLStaticCount();
				inItNewBookingProgressDialog();
			}

			@Override
			public void doInBackground() {
				try {
					WebserviceHandler webServiceHandler = new WebserviceHandler();
					responseForDHLSuburbList[0] = webServiceHandler.getDHLBookingSuburb();
				}catch(Exception e){
					responseForDHLSuburbList[0] = "0";
					e.printStackTrace();
				}
			}

			@Override
			public void onPostExecute() {
				try{
					if (!responseForDHLSuburbList[0].equals("0")){
						JSONObject jObjOfDHLArray = new JSONObject(responseForDHLSuburbList[0]);
						if (jObjOfDHLArray.getBoolean("Success")){
							JSONArray jArrayOfDHL = jObjOfDHLArray.getJSONArray("data");
							for (int i = 0; i < jArrayOfDHL.length(); i++){
								if (!jArrayOfDHL.getJSONObject(i).getString("Suburb").equals("")) {
									HashMap<String, Object> dhlbookingSuburbCount = new HashMap<String, Object>();
									dhlbookingSuburbCount.put("Suburb", jArrayOfDHL.getJSONObject(i).getString("Suburb"));
									dhlbookingSuburbCount.put("Count", jArrayOfDHL.getJSONObject(i).getInt("Count"));
									dhlBookingArray.add(dhlbookingSuburbCount);
									dhlbookingSuburbCount = null;
								}
							}
							jArrayOfDHL = null;
						}
						jObjOfDHLArray = null;
					}
				}catch (Exception e){
					e.printStackTrace();
				}finally{
					try{
						dismissNewBookingProgressDialog();
						dialogDHLCount();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.execute();

	}

	private void addDHLStaticCount() {

		HashMap<String, Object> dhlbookingSuburbdic1 = new HashMap<String, Object>();
		dhlbookingSuburbdic1.put("Suburb", "Picked up");
		dhlbookingSuburbdic1.put("Count",ActiveBookingView.DHL_PICKED_UP_BOOKING_COUNT);
		dhlBookingArray.add(dhlbookingSuburbdic1);
		dhlbookingSuburbdic1 = null;

		HashMap<String, Object> dhlbookingSuburbdic2 = new HashMap<String, Object>();
		dhlbookingSuburbdic2.put("Suburb", "On route to dropoff");
		dhlbookingSuburbdic2.put("Count",ActiveBookingView.DHL_ON_ROUTE_DROP_BOOKING_COUNT);
		dhlBookingArray.add(dhlbookingSuburbdic2);
		dhlbookingSuburbdic2 = null;
	}

    Dialog dialogDHLCount;
    ArrayList<HashMap<String, Object>> dhlBookingArray;

    void dialogDHLCount() {
        try {
            subBookingView.setVisibility(View.VISIBLE);
            try {
                if (dialogDHLCount != null)
                    if (dialogDHLCount.isShowing())
                        dialogDHLCount.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (dialogDHLCount != null)
                dialogDHLCount = null;
            dialogDHLCount = new Dialog(getActivity());
            dialogDHLCount.setCancelable(false);
            dialogDHLCount.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialogDHLCount.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogDHLCount.setContentView(R.layout.dhlbooking_count);

            Window window = dialogDHLCount.getWindow();
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            TextView dhlBookingAlrtTotalCountTxt = (TextView) dialogDHLCount.findViewById(R.id.dhlBookingAlrtTotalCountTxt);
            dhlBookingAlrtTotalCountTxt.setText("" + objActiveBookingView.activeDHLBookingCount);

            ListView dhlCountList = (ListView) dialogDHLCount.findViewById(R.id.dhlCountList);
            dhlCountList.setCacheColorHint(Color.parseColor("#ffffff"));
            DHLAlert_Adapter adapterDHLAlert = new DHLAlert_Adapter(getActivity(), R.layout.completebookinglist_item);
            dhlCountList.setAdapter(adapterDHLAlert);
            adapterDHLAlert = null;

            Button dhlAlertOkBtn = (Button) dialogDHLCount.findViewById(R.id.dhlAlertOkBtn);

            dhlAlertOkBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    subBookingView.setVisibility(View.GONE);
                    dialogDHLCount.dismiss();
                }
            });
            dialogDHLCount.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class DHLAlert_Adapter extends ArrayAdapter<All_Bookings_DataModels> {

        int resourceId;

        public DHLAlert_Adapter(Context context, int resourceId) {
            super(context, resourceId);
            this.resourceId = resourceId;
        }

        @Override
        public int getCount() {
            return dhlBookingArray.size();
        }


        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.dhlbooking_count_items, parent, false);
                TextView dhlBookingSuburbTxt = (TextView) convertView.findViewById(R.id.dhlBookingSuburbTxt);

                dhlBookingSuburbTxt.setText("" + dhlBookingArray.get(position).get("Suburb"));
                TextView dhlBookingAlrtCountTxt = (TextView) convertView.findViewById(R.id.dhlBookingAlrtCountTxt);

                dhlBookingAlrtCountTxt.setText("" + dhlBookingArray.get(position).get("Count"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }
    }
}
