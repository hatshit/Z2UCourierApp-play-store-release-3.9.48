package com.z2u.booking.vc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;

import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.suggestprice_team.AssignToOtherCourier_Functionality;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionInterface;
import com.z2u.booking.vc.dhlgroupingmodel.DHL_SectionItemsModel;
import com.z2u.booking.vc.dhlgroupingmodel.Runs_RouteSectionModel;
import com.z2u.booking.vc.endlesslistview.ActiveBooking_EndlessListview;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.LoadChatBookingArray;
import com.zoom2u.ActiveBookingDetail_New;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyContentProviderAtLocal;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.alcohol_delivery.dropdelivery_alert.DropAlcoholDelivery_Alerts;
import com.zoom2u.datamodels.All_Bookings_DataModels;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.dialogactivity.DialogReasonForLateDelivery;
import com.zoom2u.dialogactivity.DropOffDoneDialog;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.services.BG_ImageUploadToServer;
import com.zoom2u.services.ServiceToUpdate_ActiveBookingList;
import com.zoom2u.services.Service_CheckBatteryLevel;
import com.zoom2u.slidemenu.BarcodeScanner;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.slidemenu.DhlSaveInLocal;
import com.zoom2u.slidemenu.barcode_reader.CameraOverlay_Activity;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.utility.MyLocation;
import com.zoom2u.webservice.WebserviceHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import me.pushy.sdk.lib.jackson.databind.ObjectMapper;

public class ActiveBookingView implements ActiveBooking_EndlessListview.EndLessListener {
    private ArrayList<DhlSaveInLocal> dhlSaveInLocal = new ArrayList<>();
    Context activeBookingContext;
    ProgressDialog progressDialogToLoadActiveBookings;
    private TextView noBookingAvailableActiveList, etaMsgTextBGActive, todayBookingBtnActiveList, dhlBookingActiveList,
            tomorrowBookingBtnActiveList, etaTitleTextBG;
    private ImageView selectPickUpEtaImg;
    private ActiveBooking_EndlessListview activeBookingListView;

    AssignToOtherCourier_Functionality assignToOtherCourier_functionality;
    int PERMISSION_ID = 99;
    LinearLayout dhlTabsForActiveOrTriedToDeliver;
    TextView activeDHLBookings, triedToDeliverDHLBookings;
    LinearLayout dhl_active_rl, dhl_tod_rl;
    String positionForTriedToDeliverReason = "";
    Dialog attemtDeliveryDialog, etaDialogInactiveBookingList, alertDialogInActiveList;

    public ActiveBookingListAdapter activeBookingListAdapter;

    boolean isSwipeLeft_ActiveBookingList = false;
    boolean isSwipeRight_ActiveBookingList = false;
    boolean isRejectBooking_ActiveBookingList = false;

    private final int INVALID_POS_ACTIVEBOOKINGLIST = -1;
    protected int DELETE_POS_ACTIVEBOOKINGLIST = -1;
    protected int ADD_POS_ACTIVEBOOKINGLIST = -1;

    private String uploadDateBookingStrActiveList;
    private TimePickerFragment timeFragmentActiveList;

    public int itemSelectedInActiveBookingList;
    public String userNameInActiveBookingList;

    public static Bitmap photo;

    public BroadcastReceiver receiverForSilentNotificationActiveList;
    String activeBookingResponseStr = "0";

    public int checkedCount = 0;
    public static ActionMode mActionMode;
    public View titleView;
    public static HashSet<Integer> arrayOfBookingId;
    public HashSet<Integer> adapterPosition;
    public String statusStr = "", customerId = "";
    public View statusTxtOnHeader;
    boolean isActionBarButtonTaped = false;
    LinearLayout active_today_rl, active_dhl_rl,active_complete_rl;
    RelativeLayout subBookingView;
    String deliveryAttemptNoteTxt = "";
    public BookingView bookingView;
    ImageView dhlBookingCountBtn, searchDHLBookingByAWB;
    LinearLayout todayFutureBtnLayoutForActiveJobs;
//	ArrayList<DHL_SectionInterface> activeBookingListArray;

    public static int DHL_ON_ROUTE_PICK_BOOKING_COUNT;
    public static int DHL_PICKED_UP_BOOKING_COUNT;
    public static int DHL_ON_ROUTE_DROP_BOOKING_COUNT;

    public int activeDHLBookingCount;
    String awbNumber = "";
    public boolean isDropOffFromATL = false;
    SwipeRefreshLayout swipeRefreshLayoutActive;
    DropAlcoholDelivery_Alerts dropAlcoholDeliveryAlerts_Obj;

    // In-it Active booking progress dialog to show
    public void inItActiveBookingProgressDialog() {
        if (progressDialogToLoadActiveBookings != null)
            progressDialogToLoadActiveBookings = null;
        progressDialogToLoadActiveBookings = new ProgressDialog(activeBookingContext);
        Custom_ProgressDialogBar.inItProgressBar(progressDialogToLoadActiveBookings);
    }

    // Dismiss Active booking progress dialog
    public void dismissActiveBookingProgressDialog() {
        try {
            if (progressDialogToLoadActiveBookings != null)
                if (progressDialogToLoadActiveBookings.isShowing())
                    Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadActiveBookings);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	public ActiveBookingView(BookingView bookingView, Context activeBookingContext, TextView noBookingAvailable, RelativeLayout subBookingView,
							 ActiveBooking_EndlessListview activeBookingListView, TextView todayActiveListTxt, TextView dhlBookingActiveList, TextView futureActiveListTxt, ImageView dhlBookingCountBtn,
							 ImageView searchDHLBookingByAWB, LinearLayout dhlTabsForActiveOrTriedToDeliver, TextView activeDHLBookings, TextView triedToDeliverDHLBookings,
							 LinearLayout active_today_rl, LinearLayout active_dhl_rl, LinearLayout active_complete_rl, LinearLayout todayFutureBtnLayoutForActiveJobs,
							 LinearLayout dhl_active_rl, LinearLayout dhl_tod_rl, SwipeRefreshLayout swipeRefreshLayoutActive,RelativeLayout count_rl) {
		try {
			this.todayFutureBtnLayoutForActiveJobs=todayFutureBtnLayoutForActiveJobs;
			this.active_today_rl=active_today_rl;
			this.active_dhl_rl=active_dhl_rl;
			this.active_complete_rl=active_complete_rl;
            this.dhl_active_rl=dhl_active_rl;
            this.dhl_tod_rl=dhl_tod_rl;
			this.searchDHLBookingByAWB = searchDHLBookingByAWB;
			this.dhlBookingCountBtn = dhlBookingCountBtn;
            this.dhlBookingActiveList = dhlBookingActiveList;
			this.subBookingView = subBookingView;
			count_rl.setVisibility(View.GONE);
			this.activeBookingContext = activeBookingContext;
			this.noBookingAvailableActiveList = noBookingAvailable;
            this.swipeRefreshLayoutActive=swipeRefreshLayoutActive;
			this.activeBookingListView = activeBookingListView;
			//this.activeBookingListView.setCacheColorHint(Color.parseColor("#ffffff"));
			this.activeBookingListView.setListener(this);
			CompletedView.endlessCount = 0;

			this.todayBookingBtnActiveList = todayActiveListTxt;
			this.bookingView = bookingView;
			this.todayBookingBtnActiveList.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(LoginZoomToU.activeBookingTab != 0){
						ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.GONE);
						ActiveBookingView.this.searchDHLBookingByAWB.setVisibility(View.GONE);
						LoginZoomToU.filterDayActiveListStr = "today";
						CompletedView.endlessCount = 0;
						LoginZoomToU.activeBookingTab = 0;
						setBackColorForTodaysBooking();
						clearArrayOfActiveList();
                        destroyActionMode();
						hideDHLTabs();
						callAsyncTaskForGetActiveBooking("", "");
					}
				}
			});
            this.dhlBookingActiveList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.activeBookingTab != 1) {
                        ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.VISIBLE);
                        ActiveBookingView.this.searchDHLBookingByAWB.setVisibility(View.VISIBLE);
                        LoginZoomToU.filterDayActiveListStr = "DeliveryRuns";
                        CompletedView.endlessCount = 0;
                        setBackColorForDHLBooking();
                        clearArrayOfActiveList();
                        destroyActionMode();
                        LoginZoomToU.getDHLBookingStr = "Active";
                        visibleDHLTabs();
                        setBackColorForDHLActiveBooking();
                        callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
                        LoginZoomToU.activeBookingTab = 1;
                    }
                }
            });
            this.tomorrowBookingBtnActiveList = futureActiveListTxt;
            this.tomorrowBookingBtnActiveList.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.activeBookingTab != 2) {
                        ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.GONE);
                        ActiveBookingView.this.searchDHLBookingByAWB.setVisibility(View.GONE);
                        LoginZoomToU.filterDayActiveListStr = "tomorrow";
                        CompletedView.endlessCount = 0;
                        LoginZoomToU.activeBookingTab = 2;
                        setBackColorForTomorrowBooking();
                        clearArrayOfActiveList();
                        destroyActionMode();
                        hideDHLTabs();
                        callAsyncTaskForGetActiveBooking("", "");
                    }
                }
            });
            this.searchDHLBookingByAWB.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchDHLBookings();
                }
            });

            this.dhlTabsForActiveOrTriedToDeliver = dhlTabsForActiveOrTriedToDeliver;
            this.activeDHLBookings = activeDHLBookings;
            this.triedToDeliverDHLBookings = triedToDeliverDHLBookings;
            this.activeDHLBookings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.getDHLBookingStr.equals("Tried to deliver")) {
                        ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.VISIBLE);
                        LoginZoomToU.filterDayActiveListStr = "DeliveryRuns";
                        CompletedView.endlessCount = 0;
                        setBackColorForDHLBooking();
                        clearArrayOfActiveList();
                        destroyActionMode();
                        awbNumber = "";
                        LoginZoomToU.getDHLBookingStr = "Active";
                        setBackColorForDHLActiveBooking();
                        callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
                        LoginZoomToU.activeBookingTab = 1;
                    }
                }
            });
            this.triedToDeliverDHLBookings.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.getDHLBookingStr.equals("Active")) {
                        ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.VISIBLE);
                        LoginZoomToU.filterDayActiveListStr = "DeliveryRuns";
                        CompletedView.endlessCount = 0;
                        setBackColorForDHLBooking();
                        clearArrayOfActiveList();
                        destroyActionMode();
                        awbNumber = "";
                        LoginZoomToU.getDHLBookingStr = "Tried to deliver";
                        setBackColorForDHLTriedToDeliverBooking();
                        callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
                        LoginZoomToU.activeBookingTab = 1;
                    }
                }
            });
            inItActiveBookingView();
            swipeRefreshLayoutActive.setVisibility(View.VISIBLE);
            swipeRefreshLayoutActive.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    inItActiveBookingView();
                    swipeRefreshLayoutActive.setRefreshing(false);
                }
            });
            destroyActionMode();
        } catch (Exception e) {
            PushReceiver.isCameraOpen = false;
            e.printStackTrace();
        }
    }

    void callDefaultDHLBookingRequest() {
        if (LoginZoomToU.getDHLBookingStr.equals("Active"))
            setBackColorForDHLActiveBooking();
        else if (LoginZoomToU.getDHLBookingStr.equals("Tried to deliver"))
            setBackColorForDHLTriedToDeliverBooking();

        ActiveBookingView.this.dhlBookingCountBtn.setVisibility(View.VISIBLE);
        LoginZoomToU.filterDayActiveListStr = "DeliveryRuns";
        setBackColorForDHLBooking();
        clearArrayOfActiveList();
        destroyActionMode();
        awbNumber = "";
        callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
        LoginZoomToU.activeBookingTab = 1;
    }

    private void inItActiveBookingView() {
        try {
            if (LoginZoomToU.activeBookingTab == 0) {
                dhlBookingCountBtn.setVisibility(View.GONE);
                searchDHLBookingByAWB.setVisibility(View.GONE);
                LoginZoomToU.filterDayActiveListStr = "today";
                LoginZoomToU.activeBookingTab = 0;
                setBackColorForTodaysBooking();
            } else if (LoginZoomToU.activeBookingTab == 1) {
                dhlBookingCountBtn.setVisibility(View.VISIBLE);
                searchDHLBookingByAWB.setVisibility(View.VISIBLE);
                LoginZoomToU.filterDayActiveListStr = "today";
                LoginZoomToU.activeBookingTab = 1;
                setBackColorForDHLBooking();
            } else {
                dhlBookingCountBtn.setVisibility(View.GONE);
                searchDHLBookingByAWB.setVisibility(View.GONE);
                LoginZoomToU.filterDayActiveListStr = "tomorrow";
                LoginZoomToU.activeBookingTab = 2;
                setBackColorForTomorrowBooking();
            }
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                if (arrayOfBookingId == null)
                    arrayOfBookingId = new HashSet<Integer>();
                if (adapterPosition == null)
                    adapterPosition = new HashSet<Integer>();

                clearArrayOfActiveList();

                if (LoginZoomToU.activeBookingTab != 1) {
                    hideDHLTabs();
                    //********  Access live data with background Asynctask for active booking list  *****//
                    callAsyncTaskForGetActiveBooking("", "");
                    //**********  Call local broadcast on silent notification for calculated ETA  ********//
                    receiverForSilentNotificationActiveList = new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {
                                activeBookingResponseStr = intent.getStringExtra("result");
                                activeListUI_Update();
                            } catch (Exception e) {
                                activeBookingResponseStr = "0";
                                e.printStackTrace();
                            }
                        }
                    };

                    //*************  Register local broadcast receiver   ****************//
                    if (receiverForSilentNotificationActiveList != null)
                        LocalBroadcastManager.getInstance(activeBookingContext).registerReceiver((receiverForSilentNotificationActiveList), new IntentFilter(ServiceToUpdate_ActiveBookingList.SILENT_NOTIFICATION));
                } else {
                    visibleDHLTabs();
                    callDefaultDHLBookingRequest();
                }
                callTimerActiveBookingList();
            } else
                DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**************  Visible DHL Tabs ************/
    public void visibleDHLTabs() {
        dhlTabsForActiveOrTriedToDeliver.setVisibility(View.VISIBLE);
    }

    /**************  Hide DHL Tabs ************/
    public void hideDHLTabs() {
        dhlTabsForActiveOrTriedToDeliver.setVisibility(View.GONE);
    }

    /************ Select multiple booking for pick ************/
    public void multiplePickBookingSelection(int position) {
        try {
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Accepted") ||
                    ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Pickup")) {
                statusStr = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus();
                customerId = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getCustomerId();
                activeListItemLongClickDialog(position);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Dialog activeListItemLongClickDialog;

    void activeListItemLongClickDialog(final int position) {
        if (arrayOfBookingId != null)
            arrayOfBookingId.clear();
        if (activeListItemLongClickDialog != null)
            if (activeListItemLongClickDialog.isShowing())
                activeListItemLongClickDialog.dismiss();

        if (activeListItemLongClickDialog != null)
            activeListItemLongClickDialog = null;
        activeListItemLongClickDialog = new Dialog(activeBookingContext);
        activeListItemLongClickDialog.setCancelable(false);
        activeListItemLongClickDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activeListItemLongClickDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activeListItemLongClickDialog.setContentView(R.layout.multiselectionalert);

        Window window = activeListItemLongClickDialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView dialogMultiSelectMsgText = (TextView) activeListItemLongClickDialog.findViewById(R.id.dialogMultiSelectMsgText);

        dialogMultiSelectMsgText.setText("Do you want to select all bookings or only specific ones?");

        Button selectOneBtn = (Button) activeListItemLongClickDialog.findViewById(R.id.selectOneBtn);

        selectOneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedCount++;
                arrayOfBookingId.add(((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getBookingId());
                adapterPosition.add(position);
                onListLongClickEvent(position);
            }
        });
        Button selectAllBtn = (Button) activeListItemLongClickDialog.findViewById(R.id.selectAllBtn);

        selectAllBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (BookingView.bookingListArray.size() > 0) {
                        for (int i = 0; i < BookingView.bookingListArray.size(); i++) {
                            if ((BookingView.bookingListArray.get(i) instanceof All_Bookings_DataModels)) {
                                if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(i)).getRunType().equals("SMARTSORT"))
                                    if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(i)).getSource().equals("DHL")) {
                                        if (statusStr.equals(((All_Bookings_DataModels) BookingView.bookingListArray.get(i)).getStatus())
                                                && customerId.equals(((All_Bookings_DataModels) BookingView.bookingListArray.get(i)).getCustomerId())) {
                                            checkedCount++;
                                            adapterPosition.add(i);
                                            arrayOfBookingId.add(((All_Bookings_DataModels) BookingView.bookingListArray.get(i)).getBookingId());
                                        }
                                    }
                            }
                        }
                    }
                    onListLongClickEvent(position);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        activeListItemLongClickDialog.show();
    }

    //*********** List long click for multiple selection ************
    private void onListLongClickEvent(int position) {
        activeListItemLongClickDialog.dismiss();
        mActionMode = ((Activity) activeBookingContext).startActionMode(mActionModeCallback);
        // Set the CAB title according to total checked items
        itemSelectedInActiveBookingList = position;
        if (statusStr.equals("Accepted"))
            ((TextView) statusTxtOnHeader).setText("On route");
        else if (statusStr.equals("On Route to Pickup"))
            ((TextView) statusTxtOnHeader).setText("Pick up");

        mActionMode.setTitle(checkedCount + "  selected");
        int titleID = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        titleView = ((Activity) activeBookingContext).getWindow().getDecorView().findViewById(titleID);
        if ((titleView != null) && (titleView instanceof TextView)) {
            ((TextView) titleView).setTextColor(Color.WHITE);
            ((TextView) titleView).setTextSize(13);

        }
        activeBookingListAdapter.notifyDataSetChanged();
    }


    public ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    if (arrayOfBookingId != null)
                        arrayOfBookingId.clear();
                    mActionMode = null;
                    mode.finish();
                    checkedCount = 0;
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public boolean onCreateActionMode(final ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.activity_main, menu);
            MenuItem item = menu.findItem(R.id.delete);
            statusTxtOnHeader = item.getActionView();
            if (statusTxtOnHeader instanceof TextView) {
                if (LoginZoomToU.width >= 320 && LoginZoomToU.width <= 480) {
                    ((TextView) statusTxtOnHeader).setWidth(80);
                    ((TextView) statusTxtOnHeader).setHeight(50);
                } else if (LoginZoomToU.width >= 480 && LoginZoomToU.width <= 600) {
                    ((TextView) statusTxtOnHeader).setWidth(125);
                    ((TextView) statusTxtOnHeader).setHeight(75);
                } else if (LoginZoomToU.width >= 600 && LoginZoomToU.width <= 800) {
                    ((TextView) statusTxtOnHeader).setWidth(160);
                    ((TextView) statusTxtOnHeader).setHeight(100);
                } else {
                    ((TextView) statusTxtOnHeader).setWidth(240);
                    ((TextView) statusTxtOnHeader).setHeight(150);
                }
                ((TextView) statusTxtOnHeader).setBackgroundColor(Color.DKGRAY);
                ((TextView) statusTxtOnHeader).setText(R.string.bookingstatus);
                ((TextView) statusTxtOnHeader).setTextColor(Color.WHITE);
                ((TextView) statusTxtOnHeader).setGravity(Gravity.CENTER);
                ((TextView) statusTxtOnHeader).setTextSize(13);


                ((TextView) statusTxtOnHeader).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isActionBarButtonTaped = true;
                        if (statusStr.equals("Accepted")) {
                            //new OnRouteAsyncTask().execute();
                            OnRouteAsyncTask();
                        } else if (statusStr.equals("On Route to Pickup")) {
                            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL"))
                                //new PackageUploadWithETAAsyncTask().execute();
                                PackageUploadWithETAAsyncTask();
                            else {
                                try {
                                    PushReceiver.isCameraOpen = true;
                                    Intent i = new Intent(activeBookingContext, ConfirmPickUpForUserName.class);
                                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName();
                                    i.putExtra("isActionBarButtonTaped", true);
                                    isActionBarButtonTaped = false;
                                    i.putExtra("dropOffEta", uploadDateBookingStrActiveList);
                                    i.putExtra("positionActiveFragment", itemSelectedInActiveBookingList);
                                    i.putExtra("userName", userNameInActiveBookingList);
                                    i.putExtra("dataFromActiveList", 11);
                                    activeBookingContext.startActivity(i);
                                    i = null;
                                    try {
                                        LocalBroadcastManager.getInstance(activeBookingContext).unregisterReceiver(receiverForSilentNotificationActiveList);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        //Log.e("", "-----------------  Array of id ----  "+arrayOfBookingId);
                        mode.finish();
                    }
                });
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            destroyActionMode();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
    };

    // ************  Start timer for active booking list *********
    void callTimerActiveBookingList() {
        if (BookingView.handlerForTimeCounter != null)
            BookingView.handlerForTimeCounter = null;
        BookingView.handlerForTimeCounter = new Handler();
        BookingView.handlerForTimeCounter.postDelayed(MenuLogTimerForActiveList, 60 * 1000);
    }

    Runnable MenuLogTimerForActiveList = new Runnable() {
        @Override
        public void run() {
            if (activeBookingListAdapter != null)
                activeBookingListAdapter.notifyDataSetChanged();
            BookingView.handlerForTimeCounter.postDelayed(this, 60 * 1000);
        }
    };

    public void removeMenulogHandlerInActiveList() {
        if (BookingView.handlerForTimeCounter != null && MenuLogTimerForActiveList != null)
            BookingView.handlerForTimeCounter.removeCallbacks(MenuLogTimerForActiveList);
    }

	/*********** set Background Color for todays booking button  ***************/
	void setBackColorForTodaysBooking(){
		if(MainActivity.isIsBackGroundGray()){
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color_gray);
			active_today_rl.setBackgroundResource(R.color.base_color_gray);
			active_dhl_rl.setBackgroundResource(R.color.base_color_gray);
			active_complete_rl.setBackgroundResource(R.color.base_color_gray);
			todayBookingBtnActiveList.setBackgroundResource(R.drawable.selected_background_gray);
			todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
			dhlBookingActiveList.setBackgroundResource(R.color.base_color_gray);
			dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
			tomorrowBookingBtnActiveList.setBackgroundResource(R.color.base_color_gray);
			tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		}else{
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color1);
			active_today_rl.setBackgroundResource(R.color.base_color1);
		active_dhl_rl.setBackgroundResource(R.color.base_color1);
		active_complete_rl.setBackgroundResource(R.color.base_color1);
		todayBookingBtnActiveList.setBackgroundResource(R.drawable.selected_background);
		todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
        dhlBookingActiveList.setBackgroundResource(R.color.base_color1);
        dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		tomorrowBookingBtnActiveList.setBackgroundResource(R.color.base_color1);
		tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
	}
	}

    /*********** set Background Color for DHL booking  button  ***************/
    void setBackColorForDHLBooking() {
		if (MainActivity.isIsBackGroundGray()) {
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color_gray);
			active_today_rl.setBackgroundResource(R.color.base_color_gray);
			active_dhl_rl.setBackgroundResource(R.color.base_color_gray);
			active_complete_rl.setBackgroundResource(R.color.base_color_gray);
			tomorrowBookingBtnActiveList.setBackgroundResource(R.color.base_color_gray);
			tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
			dhlBookingActiveList.setBackgroundResource(R.drawable.selected_background_gray);
			dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
			todayBookingBtnActiveList.setBackgroundResource(R.color.base_color_gray);
			todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		} else {
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color1);
            active_today_rl.setBackgroundResource(R.color.base_color1);
            active_dhl_rl.setBackgroundResource(R.color.base_color1);
            active_complete_rl.setBackgroundResource(R.color.base_color1);
            tomorrowBookingBtnActiveList.setBackgroundResource(R.color.base_color1);
            tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
            dhlBookingActiveList.setBackgroundResource(R.drawable.selected_background);
            dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
            todayBookingBtnActiveList.setBackgroundResource(R.color.base_color1);
            todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		}
	}
	/*********** set Background Color for tomorrow booking  button  ***************/
	void setBackColorForTomorrowBooking(){
		if(MainActivity.isIsBackGroundGray()){
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color_gray);
			active_today_rl.setBackgroundResource(R.color.base_color_gray);
		active_dhl_rl.setBackgroundResource(R.color.base_color_gray);
		active_complete_rl.setBackgroundResource(R.color.base_color_gray);
		tomorrowBookingBtnActiveList.setBackgroundResource(R.drawable.selected_background_gray);
		tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
        dhlBookingActiveList.setBackgroundResource(R.color.base_color_gray);
        dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		todayBookingBtnActiveList.setBackgroundResource(R.color.base_color_gray);
		todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
	}else{
			todayFutureBtnLayoutForActiveJobs.setBackgroundResource(R.color.base_color1);
			active_today_rl.setBackgroundResource(R.color.base_color1);
            active_dhl_rl.setBackgroundResource(R.color.base_color1);
            active_complete_rl.setBackgroundResource(R.color.base_color1);
            tomorrowBookingBtnActiveList.setBackgroundResource(R.drawable.selected_background);
            tomorrowBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
            dhlBookingActiveList.setBackgroundResource(R.color.base_color1);
            dhlBookingActiveList.setTextColor(Color.parseColor("#FFFFFF"));
            todayBookingBtnActiveList.setBackgroundResource(R.color.base_color1);
            todayBookingBtnActiveList.setTextColor(Color.parseColor("#FFFFFF"));
		}

	}


	/*********** set Background Color for DHL active booking button  ***************/
	void setBackColorForDHLActiveBooking(){
		if(MainActivity.isIsBackGroundGray()) {
			clearSubArrayOfActiveList();
			dhl_tod_rl.setBackgroundResource(R.color.base_color_gray);
			dhl_active_rl.setBackgroundResource(R.color.base_color_gray);
			triedToDeliverDHLBookings.setBackgroundResource(R.color.base_color_gray);
			triedToDeliverDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
			activeDHLBookings.setBackgroundResource(R.drawable.selected_background_gray);
			activeDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
		}else{
            clearSubArrayOfActiveList();
			dhl_tod_rl.setBackgroundResource(R.color.base_color1);
			dhl_active_rl.setBackgroundResource(R.color.base_color1);
            triedToDeliverDHLBookings.setBackgroundResource(R.color.base_color1);
            triedToDeliverDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
            activeDHLBookings.setBackgroundResource(R.drawable.selected_background);
            activeDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
		}
		}

	/*********** set Background Color for DHL booking  button  ***************/
	void setBackColorForDHLTriedToDeliverBooking(){
		if(MainActivity.isIsBackGroundGray()){
		clearSubArrayOfActiveList();
		dhl_tod_rl.setBackgroundResource(R.color.base_color_gray);
		dhl_active_rl.setBackgroundResource(R.color.base_color_gray);
		activeDHLBookings.setBackgroundResource(R.color.base_color_gray);
		activeDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
		triedToDeliverDHLBookings.setBackgroundResource(R.drawable.selected_background_gray);
		triedToDeliverDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
	}else{
            clearSubArrayOfActiveList();
			dhl_tod_rl.setBackgroundResource(R.color.base_color1);
			dhl_active_rl.setBackgroundResource(R.color.base_color1);
            activeDHLBookings.setBackgroundResource(R.color.base_color1);
            activeDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
            triedToDeliverDHLBookings.setBackgroundResource(R.drawable.selected_background);
            triedToDeliverDHLBookings.setTextColor(Color.parseColor("#FFFFFF"));
		}
	}

    /************* clear Active booking list sub array **********/
    void clearSubArrayOfActiveList() {
//		if (activeBookingListArray != null)
//			if (activeBookingListArray.size() > 0)
//				activeBookingListArray.clear();
    }

    /************* clear Active booking list main array **********/
    void clearArrayOfActiveList() {
        if (BookingView.bookingListArray != null)
            if (BookingView.bookingListArray.size() > 0)
                BookingView.bookingListArray.clear();
    }

    /*********** Call Async task for getting Active booking list  ***************/
    void callAsyncTaskForGetActiveBooking(String awbNumberStr, String dhlStatusStr) {
        ActiveBookingDetail_New.isPickBtn = false;
        isDropOffFromATL = false;
        ActiveBookingView.getCurrentLocation(activeBookingContext);
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
           /* GetActiveBookinListData getActiveBookinListData = new GetActiveBookinListData(awbNumberStr, dhlStatusStr);
            getActiveBookinListData.execute();
            getActiveBookinListData = null;*/
            GetActiveBookinListData(awbNumberStr, dhlStatusStr);
        } else
            DialogActivity.alertDialogView(activeBookingContext, "No network!", "No network connection, Please try later.");
    }

    public void destroyActionMode() {
        statusStr = "";
        customerId = "";
        if (mActionMode != null)
            mActionMode.finish();
        mActionMode = null;
        checkedCount = 0;
        if (adapterPosition != null)
            adapterPosition.clear();
        if (isActionBarButtonTaped == false) {
            itemSelectedInActiveBookingList = 0;
        }
        if (activeBookingListAdapter != null)
            activeBookingListAdapter.notifyDataSetChanged();
    }

    @Override
    public void loadData() {
//		CompletedView.endlessCount++;
//		if (LoginZoomToU.activeBookingTab != 1)
//			callAsyncTaskForGetActiveBooking("", "");
//		else if(!awbNumber.equals(""))
//			callAsyncTaskForGetActiveBooking(awbNumber, LoginZoomToU.getDHLBookingStr);
//		else
//			callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
    }

    private void GetActiveBookinListData(String awbNumberStr, String statusForDHLActiveListStr){
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    if (progressDialogToLoadActiveBookings != null)
                        if (progressDialogToLoadActiveBookings.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadActiveBookings);

                    inItActiveBookingProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                    inItActiveBookingProgressDialog();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    activeListBackGroundCalling(awbNumberStr, statusForDHLActiveListStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    dismissActiveBookingProgressDialog();
                    activeListUI_Update();
                } catch (Exception e) {
                    e.printStackTrace();
                    activeBookingResponseStr = null;
                    DialogActivity.alertDialogView(activeBookingContext, "Server Error!", "Something went wrong, Please try again later.");
                } finally {
                    dismissActiveBookingProgressDialog();
                    if (SlideMenuZoom2u.isDropOffCompleted) {
                        DropOffDoneDialog.alertDialogToFinishView(activeBookingContext, "Drop off Completed");
                        SlideMenuZoom2u.isDropOffCompleted = false;
                    } else if (SlideMenuZoom2u.isPickupOffCompleted) {
                        DropOffDoneDialog.alertDialogToFinishView(activeBookingContext, "Pick up Completed");
                        SlideMenuZoom2u.isPickupOffCompleted = false;
                    }
                }
            }
        }.execute();
    }



    private void saveDHLResp(String serverResp) {
        try {
            JSONObject jObjOfActiveList = new JSONObject(serverResp);
            JSONArray responseArrayOfActiveBookingData = jObjOfActiveList.getJSONArray("data");
            JSONObject mainResponseJOBJ = null;
            dhlSaveInLocal.clear();
            for (int i = 0; i < responseArrayOfActiveBookingData.length(); i++) {
                mainResponseJOBJ = responseArrayOfActiveBookingData.getJSONObject(i);
                if (mainResponseJOBJ.getString("source").equals("DHL")) {
                    DhlSaveInLocal dhlSaveInLocal1 = new DhlSaveInLocal(
                            mainResponseJOBJ.getInt("runId"),
                            mainResponseJOBJ.getString("orderNo"),
                            mainResponseJOBJ.getInt("bookingId"),
                            mainResponseJOBJ.getString("pickupAddress"),
                            mainResponseJOBJ.getString("status"));
                    dhlSaveInLocal.add(dhlSaveInLocal1);
                }
            }
			/*Gson gson = new Gson();
			String json = gson.toJson(dhlSaveInLocal);
			App_preference.getSharedprefInstance().setDhlBookingFiled(json);*/
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public class ActiveBookingListAdapter extends ArrayAdapter<DHL_SectionInterface> {

        int resourceId;
        Handler mHandlerForUIvisibility = new Handler();
        private int[] colors;
        LayoutInflater inflater;

        @Override
        public int getCount() {
           if(BookingView.bookingListArray!=null)
               return BookingView.bookingListArray.size();
           else
               return 0;
        }

        @Override
        public DHL_SectionInterface getItem(int arg0) {
            return BookingView.bookingListArray.get(arg0);
        }

		@Override
		public int getViewTypeCount() {
			return getCount();
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

        public ActiveBookingListAdapter(Context context, int resourceId) {
            super(context, resourceId);
            this.resourceId = resourceId;
            if (BookingView.bookingListArray.size() % 2 != 0)
                colors = new int[]{0xF0F0F0F3, 0xFFFFFFFF};
            else
                colors = new int[]{0xFFFFFFFF, 0xF0F0F0F3};
        }

        //**********   Work with active booking swipe right event
        public void onSwipeItemRight(boolean isLeft, int position) {

            if (isSwipeRight_ActiveBookingList) {
                isSwipeRight_ActiveBookingList = false;
                ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
            } else if (isSwipeLeft_ActiveBookingList) {
                isSwipeLeft_ActiveBookingList = false;
                if (isLeft == false) {
                    ADD_POS_ACTIVEBOOKINGLIST = position;
                    DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                } else if (ADD_POS_ACTIVEBOOKINGLIST == position) {
                    ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                }
            } else {
                isSwipeRight_ActiveBookingList = false;
                isSwipeLeft_ActiveBookingList = true;
                ADD_POS_ACTIVEBOOKINGLIST = position;
                DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
            }
            notifyDataSetChanged();
        }

        //**********   Work with active booking swipe left event
        public void onSwipeItemLeft(boolean isRight, int position) {

            if (isSwipeLeft_ActiveBookingList) {
                isSwipeLeft_ActiveBookingList = false;
                ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
            } else if (isSwipeRight_ActiveBookingList) {
                isSwipeRight_ActiveBookingList = false;
                if (isRight == false) {
                    DELETE_POS_ACTIVEBOOKINGLIST = position;
                    ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                } else if (DELETE_POS_ACTIVEBOOKINGLIST == position) {
                    DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                }
            } else {
                isSwipeLeft_ActiveBookingList = false;
                isSwipeRight_ActiveBookingList = true;
                ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                DELETE_POS_ACTIVEBOOKINGLIST = position;
            }
            notifyDataSetChanged();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            try {
                RelativeLayout frontText;
                if (LoginZoomToU.filterDayActiveListStr.equals("DeliveryRuns")) {
                    final DHL_SectionInterface i = BookingView.bookingListArray.get(position);
                    if (i.isRouteSection()) {
                        Runs_RouteSectionModel si = (Runs_RouteSectionModel) i;

                        inflater = (LayoutInflater) activeBookingContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.activebookinglistitem, null);
                        frontText = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack_text);
                        frontText.setVisibility(View.VISIBLE);
                        RelativeLayout frontTagSecond = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack);
                        frontTagSecond.setVisibility(View.GONE);
                        convertView.setOnClickListener(null);
                        convertView.setOnLongClickListener(null);
                        convertView.setLongClickable(false);
                        final TextView sectionView = (TextView) convertView.findViewById(R.id.list_item_section_text);
                        sectionView.setTextColor(activeBookingContext.getResources().getColor(R.color.white));
                        frontText.setBackgroundResource(R.drawable.new_gray_back);
                        try {
                            if (si.getTitle().contains("0"))
                                sectionView.setText("Runs");
                            else
                                sectionView.setText(si.getTitle());
                        } catch (Exception e) {
                            e.printStackTrace();
                            sectionView.setText("Runs");
                        }
                    } else if (i.isSection()) {
                        DHL_SectionItemsModel si = (DHL_SectionItemsModel) i;
                        inflater = (LayoutInflater) activeBookingContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.activebookinglistitem, null);
                        frontText = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack_text);
                        frontText.setVisibility(View.VISIBLE);
                        RelativeLayout frontTagSecond = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack);
                        frontTagSecond.setVisibility(View.GONE);
                        convertView.setOnClickListener(null);
                        convertView.setOnLongClickListener(null);
                        convertView.setLongClickable(false);
                        final TextView sectionView = (TextView) convertView.findViewById(R.id.list_item_section_text);
                        frontText.setBackgroundResource(R.drawable.new_white_back);
                        sectionView.setTextColor(activeBookingContext.getResources().getColor(R.color.gun_metal));
                        sectionView.setText(si.getTitle());
                    } else {
                        inflater = (LayoutInflater) activeBookingContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = inflater.inflate(R.layout.activebookinglistitem, null);
                        frontText = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack_text);
                        frontText.setVisibility(View.GONE);
                        setDataItemView(convertView, position);
                    }
                } else {
                    inflater = (LayoutInflater) activeBookingContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.activebookinglistitem, null);
                    setDataItemView(convertView, position);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        void setDataItemView(View convertView, final int position) {

			final All_Bookings_DataModels activeBookingModel = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position));
			View line_blank= ViewHolderPatternActiveBooking.get(convertView, R.id.line_blank);

			if(position==getCount()-1)
				line_blank.setVisibility(View.VISIBLE);
			else
				line_blank.setVisibility(View.GONE);


            RelativeLayout frontTagActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.front);
            RelativeLayout frontTagSecond = ViewHolderPatternActiveBooking.get(convertView, R.id.frontBack);
            RelativeLayout frontViewActiveBookingListForRun = ViewHolderPatternActiveBooking.get(convertView, R.id.frontViewActiveBookingListForRun);
            //************ Delivery run UI containers ***********
            if (LoginZoomToU.activeBookingTab != 2 &&
                    activeBookingModel.getRunType().equals("SMARTSORT")) {
                frontTagSecond.setVisibility(View.GONE);
                frontViewActiveBookingListForRun.setVisibility(View.VISIBLE);

                TextView pickupNameActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.pickupNameActiveBListRun);

                TextView awbTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.awbTxtActiveBListRun);

                TextView pickupNameTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.pickupNameTxtActiveBListRun);

                TextView locationTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.locationTxtActiveBListRun);


                TextView awbValueTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.awbValueTxtActiveBListRun);

                TextView pickupNameValueTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.pickupNameValueTxtActiveBListRun);

                TextView locationValueTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.locationValueTxtActiveBListRun);


                TextView pickupTimeTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.pickupTimeTxtActiveBListRun);

                TextView statusTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.statusTxtActiveBListRun);

                TextView dropCountActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.dropCountActiveBListRun);

                TextView dropTxtActiveBListRun = ViewHolderPatternActiveBooking.get(convertView, R.id.dropTxtActiveBListRun);

                pickupNameActiveBListRun.setText(activeBookingModel.getDrop_ContactName());
                awbValueTxtActiveBListRun.setText(activeBookingModel.getOrderNumber());
                pickupNameValueTxtActiveBListRun.setText(activeBookingModel.getPick_ContactName());
                locationValueTxtActiveBListRun.setText(activeBookingModel.getDrop_Address());

                String bookingCreateTimeNewDeliveryRun = null;
                bookingCreateTimeNewDeliveryRun = LoginZoomToU.checkInternetwithfunctionality.getDateTimeFromServer("" + activeBookingModel.getPickupDateTime());
                String[] pickUpTimeArray = bookingCreateTimeNewDeliveryRun.split(" ");
                pickupTimeTxtActiveBListRun.setText(pickUpTimeArray[0]);

                statusTxtActiveBListRun.setText(activeBookingModel.getStatus());

                //if (activeBookingModel.getRunCompletedDeliveryCount() > 0)
                dropCountActiveBListRun.setText(activeBookingModel.getRunCompletedDeliveryCount() + "/"
                        + activeBookingModel.getRunTotalDeliveryCount());
//				else
//					dropCountActiveBListRun.setText(""+activeBookingModel.getRunTotalDeliveryCount());

                //************ Delivery run UI containers ***********

            } else {
                frontTagSecond.setVisibility(View.VISIBLE);
                frontViewActiveBookingListForRun.setVisibility(View.GONE);
                TextView newCustomerTxtInABL = ViewHolderPatternActiveBooking.get(convertView, R.id.newCustomerTxtInABL);

                if (activeBookingModel.isNewCustomer())
                    newCustomerTxtInABL.setVisibility(View.VISIBLE);
                else
                    newCustomerTxtInABL.setVisibility(View.GONE);

                TextView userNameActiveBookingText = ViewHolderPatternActiveBooking.get(convertView, R.id.userNameActiveBookingText);

             //   userNameActiveBookingText.setClickable(false);

              //  userNameActiveBookingText.setTextColor(Color.parseColor("#162C45"));
                try {
                    if (!activeBookingModel.getPick_ContactName().equals(""))
                        userNameActiveBookingText.setText("" + activeBookingModel.getPick_ContactName());
                    else
                        userNameActiveBookingText.setText("Pickup contact  -  NA");
                } catch (Exception e) {
                    e.printStackTrace();
                    userNameActiveBookingText.setText("Pickup contact  -  NA");
                }

                if (activeBookingModel.getPick_ContactName().equalsIgnoreCase("Telstra")
                        || activeBookingModel.getSource().equals("DHL"))
                    userNameActiveBookingText.setText("" + activeBookingModel.getDrop_ContactName());

                //set the drop contact name when runId>0
                if (activeBookingModel.getRunId() > 0)
                    userNameActiveBookingText.setText("" + activeBookingModel.getDrop_ContactName());


                TextView bookingStatusActiveList = ViewHolderPatternActiveBooking.get(convertView, R.id.bookingStatusActiveList);

                bookingStatusActiveList.setText(activeBookingModel.getStatus());

                TextView chargesTextActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.chargesTextActiveBooking);


                TextView distanceActiveBookingText = ViewHolderPatternActiveBooking.get(convertView, R.id.distanceActiveBookingText);

                if (adapterPosition != null) {
                    if (adapterPosition.contains(position)) {
                        //frontTagActiveBooking.setBackgroundColor(Color.parseColor("#a4cbe3"));
                        frontTagSecond.setBackgroundResource(R.drawable.new_selected_back);
                    }
                }
                TextView orderNumberMenulog = ViewHolderPatternActiveBooking.get(convertView, R.id.orderNumberMenulog);

                if (activeBookingModel.getSource().equals("DHL")) {
                    orderNumberMenulog.setVisibility(View.VISIBLE);
                    try {
                        if (!activeBookingModel.getOrderNumber().equals("")) {
                            orderNumberMenulog.setText("AWB - " + activeBookingModel.getOrderNumber());
                        } else
                            orderNumberMenulog.setText("AWB - NA");
                    } catch (Exception e) {
                        e.printStackTrace();
                        orderNumberMenulog.setText("AWB - NA");
                    }
                } else
                    orderNumberMenulog.setVisibility(View.GONE);

                TextView pendingPieceTxt = ViewHolderPatternActiveBooking.get(convertView, R.id.pendingPieceTxt);

                TextView pieceCountText = ViewHolderPatternActiveBooking.get(convertView, R.id.pieceCountText);

                if (activeBookingModel.getSource().equals("DHL")) {
                    if (activeBookingModel.getStatus().equals("Accepted") || activeBookingModel.getStatus().equals("On Route to Pickup")) {
                        pendingPieceTxt.setVisibility(View.GONE);
                        pieceCountText.setText("Pieces: " + activeBookingModel.getTotalPieceCount());
                    } else {
                        int pickedUpPieceCount = activeBookingModel.getPickedUpPieceCount();
                        int totalPieceCount = activeBookingModel.getTotalPieceCount();
                        if (pickedUpPieceCount < totalPieceCount) {
                            pendingPieceTxt.setVisibility(View.VISIBLE);
                            pieceCountText.setBackgroundColor(Color.parseColor("#FFCB2A"));
                        } else {
                            pendingPieceTxt.setVisibility(View.GONE);
                            pieceCountText.setBackgroundColor(Color.parseColor("#5EAC40"));
                        }
                        pieceCountText.setText("Pieces: " + pickedUpPieceCount + "/" + totalPieceCount);
                    }
                } else {
                    pendingPieceTxt.setVisibility(View.GONE);
                    pieceCountText.setVisibility(View.GONE);
                }

                TextView textPickupActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.textPickupActiveBooking);


                TextView menuLogTimerText = ViewHolderPatternActiveBooking.get(convertView, R.id.MenuLogTimerText);
                if (activeBookingModel.getStatus().equals("Tried to deliver")
                        || activeBookingModel.getStatus().equals("Delivery Attempted")) {

                    menuLogTimerText.setVisibility(View.VISIBLE);
                    menuLogTimerText.setText("Attempted");
                    menuLogTimerText.setGravity(Gravity.CENTER | Gravity.RIGHT);
                    menuLogTimerText.setTextColor(Color.parseColor("#ffffff"));
                    menuLogTimerText.setBackgroundResource(R.drawable.rounded_recruite_level);
                } else {
                    menuLogTimerText.setVisibility(View.VISIBLE);
                    menuLogTimerText.setTextColor(Color.parseColor("#ffffff"));
                    menuLogTimerText.setGravity(Gravity.CENTER);
                    menuLogTimerText.setBackgroundResource(R.drawable.rounded_recruite_level_red);
                    try {
                        if (!activeBookingModel.getDropDateTime().equals("")) {
                            //String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiff(activeBookingModel.getDropDateTime(), false);
                            String minInStr = LoginZoomToU.checkInternetwithfunctionality.getNormalTimeDiffActive(activeBookingModel.getDropDateTime(), false,menuLogTimerText);
                            menuLogTimerText.setText(minInStr);
                        } else {
                            menuLogTimerText.setText("NA");
                        }
                    } catch (Exception e) {
                        menuLogTimerText.setText("NA");
                    }
                }

                TextView textDropoffActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.textDropoffActiveBooking);

                ReadMoreTextView textDeliveryNotesActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.delivryNotesActiveBooking);
                textDeliveryNotesActiveBooking.setVisibility(View.VISIBLE);
                try {
                    if (!activeBookingModel.getNotes().equals(""))
                        textDeliveryNotesActiveBooking.setText("" + activeBookingModel.getNotes());
                    else
                        textDeliveryNotesActiveBooking.setText("No Delivery Notes");
                } catch (Exception e) {
                    e.printStackTrace();
                    textDeliveryNotesActiveBooking.setText("No Delivery Notes");
                }

                TextView textDeliverySpeedActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.timeToArriveActiveBooking);

                textDeliverySpeedActiveBooking.setText("" + activeBookingModel.getDeliverySpeed());



                TextView bookingCreatedtimeActive = ViewHolderPatternActiveBooking.get(convertView, R.id.bookingCreatedtimeActive);
                ImageView imglocationMark= ViewHolderPatternActiveBooking.get(convertView,R.id.imglocationMark);
                imglocationMark.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            String address;
                            if (((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getStatus().equals("Picked up") ||
                                    ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Dropoff") ||
                                    ((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getStatus().equals("Tried to deliver")) {
                                address=((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getDrop_Address();
                            }else{
                                address=((All_Bookings_DataModels)BookingView.bookingListArray.get(position)).getPick_Address();
                            }
                            address = address.replace(' ', '+');
                            Intent geoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address)); // Prepare intent
                            activeBookingContext.startActivity(geoIntent);    // Initiate lookup
                        } catch (Exception e) {
                            e.printStackTrace();
							/*Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
									Uri.parse("google.navigation:q="));*/
                        }
                    }
                });


				String bookingCreateTimeNewBookingDetail = null,bookingCreateDateNewBookingDetail = null;
				bookingCreateTimeNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getTimeFromServer("" + activeBookingModel.getPickupDateTime());
				bookingCreateDateNewBookingDetail = LoginZoomToU.checkInternetwithfunctionality.getDateFromServer("" + activeBookingModel.getPickupDateTime());

				bookingCreatedtimeActive.setText(""+bookingCreateTimeNewBookingDetail +" | "+bookingCreateDateNewBookingDetail);
				bookingCreateTimeNewBookingDetail = null;
				bookingCreateDateNewBookingDetail = null;
                TextView packageTextActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.suburbTextActiveBooking);
                packageTextActiveBooking.setText("");
                try {
                    if (activeBookingModel.getShipmentsArray().size() > 0) {
                        for (int i = 0; i < activeBookingModel.getShipmentsArray().size(); i++) {
                            if (i < 3) {
                                if (i == 0)
                                    packageTextActiveBooking.append(activeBookingModel.getShipmentsArray().get(i).get("Category") + " (" + activeBookingModel.getShipmentsArray().get(i).get("Quantity") + ")");
                                else
                                    packageTextActiveBooking.append(", " + activeBookingModel.getShipmentsArray().get(i).get("Category") + " (" + activeBookingModel.getShipmentsArray().get(i).get("Quantity") + ")");
                            } else {
                                packageTextActiveBooking.append("...");
                                break;
                            }
                        }
                    } else
                        packageTextActiveBooking.append("" + activeBookingModel.getPackage());
                } catch (Exception e) {
                    e.printStackTrace();
                    packageTextActiveBooking.append("" + activeBookingModel.getPackage());
                }

                TextView weight = ViewHolderPatternActiveBooking.get(convertView, R.id.weight);


                try {
                    double totalWeight = 0.0;
                    if (activeBookingModel.getShipmentsArray().size() > 0) {
                        for (int i = 0; i < activeBookingModel.getShipmentsArray().size(); i++) {
                            totalWeight = totalWeight + (double) activeBookingModel.getShipmentsArray().get(i).get("TotalWeightKg");

                        }
                        if (totalWeight > 0.0) {
                            weight.setVisibility(View.VISIBLE);
                            weight.setText("Total weight : " + totalWeight + "kg");
                        } else
                            weight.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String priceInt = Functional_Utility.returnCourierPrice(activeBookingModel.getPrice());
                chargesTextActiveBooking.setText("$" + priceInt);

                frontTagActiveBooking.setClickable(false);





                // ************* For Temando bookings test  ************



                String pickUpAddressTxt = "";

                if (activeBookingModel.getSource().equals("Temando")) {
                    if (!activeBookingModel.getPick_Address().equals(""))
                        textPickupActiveBooking.setText(activeBookingModel.getPick_StreetName());
                    else
                        textPickupActiveBooking.setText("No Address");
                } else {
                    if (!activeBookingModel.getPick_StreetNo().equals(""))
                        pickUpAddressTxt = activeBookingModel.getPick_StreetNo() + ", ";
                    if (!activeBookingModel.getPick_StreetName().equals("")) {
                        if (activeBookingModel.getPick_StreetName().contains(",")) {
                            String[] pickStreetName = activeBookingModel.getPick_StreetName().split(",");
                            pickUpAddressTxt = pickUpAddressTxt + pickStreetName[0] + ", ";
                            pickStreetName = null;
                        } else
                            pickUpAddressTxt = pickUpAddressTxt + activeBookingModel.getPick_StreetName() + ", ";
                    }
                    if (!activeBookingModel.getPick_Suburb().equals(""))
                        pickUpAddressTxt = pickUpAddressTxt + activeBookingModel.getPick_Suburb();

                    textPickupActiveBooking.setText(activeBookingModel.getPick_Address());
                }


                String dropAddressTxt = "";
                if (activeBookingModel.getSource().equals("Temando")) {
                    if (!activeBookingModel.getPick_Address().equals(""))
                        textDropoffActiveBooking.setText( activeBookingModel.getDrop_Address());
                    else
                        textDropoffActiveBooking.setText("No Address");
                } else {
                    if (!activeBookingModel.getDrop_StreetNo().equals(""))
                        dropAddressTxt = activeBookingModel.getDrop_StreetNo() + ", ";
                    if (!activeBookingModel.getDrop_StreetName().equals("")) {
                        if (activeBookingModel.getDrop_StreetName().contains(",")) {
                            String[] dropStreetName = activeBookingModel.getDrop_StreetName().split(",");
                            dropAddressTxt = dropAddressTxt + dropStreetName[0] + ", ";
                            dropStreetName = null;
                        } else
                            dropAddressTxt = dropAddressTxt + activeBookingModel.getDrop_StreetName() + ", ";
                    }
                    if (!activeBookingModel.getDrop_Suburb().equals(""))
                        dropAddressTxt = dropAddressTxt + activeBookingModel.getDrop_Suburb();

                    textDropoffActiveBooking.setText(activeBookingModel.getDrop_Address());
                }


                distanceActiveBookingText.setText(activeBookingModel.getDistance());
                if (!activeBookingModel.getDistance().equals(""))
                    distanceActiveBookingText.setText(activeBookingModel.getDistance());
                else
                    distanceActiveBookingText.setText("0 km");


			}

			final Button onRouteActiveBookingBtn = ViewHolderPatternActiveBooking.get(convertView, R.id.onRouteBtnInActiveBooking);

			if (activeBookingModel.getSource().equals("Temando"))
				onRouteActiveBookingBtn.setVisibility(View.GONE);
			else
				onRouteActiveBookingBtn.setVisibility(View.VISIBLE);

			final Button reassignBtnInActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.reassignBtnInActiveBooking);


            final Button attemptdeliveryBtnInActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.attemptdeliveryBtnInActiveBooking);

            String firstCharOfReferenceNo = activeBookingModel.getBookingRefNo().substring(0, 1);
            if (activeBookingModel.getStatus().equals("On Route to Dropoff")
                    || activeBookingModel.getStatus().equals("Tried to deliver")) {
                attemptdeliveryBtnInActiveBooking.setVisibility(View.VISIBLE);
                attemptdeliveryBtnInActiveBooking.setBackgroundColor(Color.parseColor("#00A7E2"));
                attemptdeliveryBtnInActiveBooking.setText("Tried to \ndeliver");
            } else if (activeBookingModel.getStatus().equals("Accepted") || activeBookingModel.getStatus().equals("On Route to Pickup")) {
                onRouteActiveBookingBtn.setVisibility(View.VISIBLE);
                //********** To Show Test booking alert *************
                if (firstCharOfReferenceNo.equalsIgnoreCase("T")
                        || activeBookingModel.getPackage().equals("XL")
                        || activeBookingModel.getRunId() != 0) {
                    attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                } else {
                    attemptdeliveryBtnInActiveBooking.setVisibility(View.VISIBLE);
                    attemptdeliveryBtnInActiveBooking.setBackgroundColor(Color.parseColor("#FF476A"));
                    attemptdeliveryBtnInActiveBooking.setTextColor(Color.WHITE);
                    attemptdeliveryBtnInActiveBooking.setText("Request\nnew\ndriver");
                }
            } else
                attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);

            final ImageView etaButtonInActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.etaButtonInActiveBooking);
            etaButtonInActiveBooking.setScaleType(ScaleType.FIT_XY);

            final ImageView pickupButtonInActiveBooking = ViewHolderPatternActiveBooking.get(convertView, R.id.pickupButtonInActiveBooking);
            pickupButtonInActiveBooking.setScaleType(ScaleType.FIT_XY);

            if (activeBookingModel.getSource().equals("Temando")) {
                if (activeBookingModel.getStatus().equals("Accepted"))
                    pickupButtonInActiveBooking.setImageResource(R.drawable.pickupslide);
                else if (activeBookingModel.getStatus().equals("Picked up"))
                    pickupButtonInActiveBooking.setImageResource(R.drawable.dropoffslide);
            } else {
                if (activeBookingModel.getStatus().equals("On Route to Pickup"))
                    pickupButtonInActiveBooking.setImageResource(R.drawable.pickupslide);
                else if (activeBookingModel.getStatus().equals("On Route to Dropoff")
                        || activeBookingModel.getStatus().equals("Tried to deliver")
                        || activeBookingModel.getStatus().equals("Delivery Attempted"))
                    pickupButtonInActiveBooking.setImageResource(R.drawable.dropoffslide);
                else if (activeBookingModel.getStatus().equals("Dropped Off")
                        || activeBookingModel.getStatus().equals("Returned")) {
                    ADD_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                    DELETE_POS_ACTIVEBOOKINGLIST = INVALID_POS_ACTIVEBOOKINGLIST;
                }
            }

            //	if(adapterPosition.size() <= 0){
            //RelativeLayout.LayoutParams onRouteActiveBookingBtnParam = null;
            //************** Handling views at right swipe
            if (DELETE_POS_ACTIVEBOOKINGLIST == position) {
                //	listActiveBooking.setScrollContainer(true);
                frontTagActiveBooking.animate().translationX(0).withLayer();

                etaButtonInActiveBooking.setClickable(false);
                pickupButtonInActiveBooking.setClickable(false);
                onRouteActiveBookingBtn.setClickable(false);


                if (activeBookingModel.getStatus().equals("On Route to Dropoff") || activeBookingModel.getStatus().equals("Tried to deliver")
                        || activeBookingModel.getStatus().equals("Accepted") || activeBookingModel.getStatus().equals("On Route to Pickup")
                        || activeBookingModel.getStatus().equals("Delivery Attempted")) {
                    if (!firstCharOfReferenceNo.equalsIgnoreCase("T") && !activeBookingModel.getPackage().equals("XL")) {
                        if (activeBookingModel.getStatus().equals("Accepted")) {
                            if (activeBookingModel.getRunType().equals("SMARTSORT")) {
                                //slideRightForAttemptDelivery(110, frontTagActiveBooking);
                                attemptdeliveryBtnInActiveBooking.setClickable(false);
                                attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                                reassignBtnInActiveBooking.setClickable(false);
                                reassignBtnInActiveBooking.setVisibility(View.GONE);
                            } else if (activeBookingModel.getRunId() != 0) {
                                attemptdeliveryBtnInActiveBooking.setClickable(false);
                                attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                                reassignBtnInActiveBooking.setClickable(true);
                                reassignBtnInActiveBooking.setVisibility(View.VISIBLE);
                                slideRightForAttemptDelivery(110, frontTagActiveBooking);
                            } else {
                                reassignBtnInActiveBooking.setClickable(true);
                                reassignBtnInActiveBooking.setVisibility(View.VISIBLE);
                                slideRightForAttemptDelivery(220, frontTagActiveBooking);
                            }
                        } else {
                            reassignBtnInActiveBooking.setVisibility(View.GONE);
                            if (activeBookingModel.getStatus().equals("Accepted")
                                    || activeBookingModel.getStatus().equals("On Route to Pickup")) {
                                if (activeBookingModel.getRunType().equals("SMARTSORT")
                                        || activeBookingModel.getRunId() != 0) {
                                    attemptdeliveryBtnInActiveBooking.setClickable(false);
                                    attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                                } else
                                    slideRightForAttemptDelivery(110, frontTagActiveBooking);
                            } else if (activeBookingModel.isTTDReasonForAlcoholDelivery()) {
                                attemptdeliveryBtnInActiveBooking.setClickable(false);
                                attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                                reassignBtnInActiveBooking.setClickable(false);
                                reassignBtnInActiveBooking.setVisibility(View.GONE);
                            } else
                                slideRightForAttemptDelivery(110, frontTagActiveBooking);
                        }
                    } else {
                        if (activeBookingModel.getStatus().equals("Accepted") && LoginZoomToU.IS_TEAM_LEADER) {
                            slideRightForAttemptDelivery(110, frontTagActiveBooking);
                            reassignBtnInActiveBooking.setClickable(true);
                            reassignBtnInActiveBooking.setVisibility(View.VISIBLE);
                        } else if (activeBookingModel.getStatus().equals("On Route to Dropoff")
                                || activeBookingModel.getStatus().equals("Tried to deliver")) {
                            if (activeBookingModel.isTTDReasonForAlcoholDelivery()) {
                                attemptdeliveryBtnInActiveBooking.setClickable(false);
                                attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                                reassignBtnInActiveBooking.setClickable(false);
                                reassignBtnInActiveBooking.setVisibility(View.GONE);
                            } else {
                                slideRightForAttemptDelivery(110, frontTagActiveBooking);
                                attemptdeliveryBtnInActiveBooking.setVisibility(View.VISIBLE);
                            }
                        } else {
                            attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                            reassignBtnInActiveBooking.setVisibility(View.GONE);
                        }
                    }
                } else {
                    attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                    reassignBtnInActiveBooking.setVisibility(View.GONE);
                }

                mHandlerForUIvisibility.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        etaButtonInActiveBooking.setVisibility(View.GONE);
                        pickupButtonInActiveBooking.setVisibility(View.GONE);
                        onRouteActiveBookingBtn.setVisibility(View.GONE);
                    }
                }, 200);

                //*********** Handling views at left swipe
            } else if (ADD_POS_ACTIVEBOOKINGLIST == position) {

                if (activeBookingModel.getStatus().equals("On Route to Pickup")
                        || activeBookingModel.getStatus().equals("On Route to Dropoff")
                        || activeBookingModel.getStatus().equals("Tried to deliver")
                        || activeBookingModel.getStatus().equals("Delivery Attempted")) {
                    if (activeBookingModel.getStatus().equals("On Route to Pickup")) {
                        if (activeBookingModel.getSource().equals("DHL")) {
                            frontTagActiveBooking.animate().translationX(0).withLayer();
                            hideActiveListSlideButton(etaButtonInActiveBooking, pickupButtonInActiveBooking,
                                    onRouteActiveBookingBtn, attemptdeliveryBtnInActiveBooking);
                        } else if (activeBookingModel.getRunType().equals("SMARTSORT")) {
                            onRouteActiveBookingBtn.setText("Open\nBarcode\nScanner");
                            setOnRouteButtonForRunOrNormal(position, frontTagActiveBooking,
                                    etaButtonInActiveBooking, pickupButtonInActiveBooking, onRouteActiveBookingBtn);
                        } else {
                            openPickDropSideButton(activeBookingModel, etaButtonInActiveBooking, pickupButtonInActiveBooking, onRouteActiveBookingBtn,
                                    frontTagActiveBooking);
                        }
                    } else if (activeBookingModel.isTTDReasonForAlcoholDelivery()) {
                        etaButtonInActiveBooking.setClickable(false);
                        pickupButtonInActiveBooking.setClickable(false);
                        onRouteActiveBookingBtn.setClickable(false);
                        etaButtonInActiveBooking.setVisibility(View.GONE);
                        pickupButtonInActiveBooking.setVisibility(View.GONE);
                        onRouteActiveBookingBtn.setVisibility(View.GONE);
                    } else
                        openPickDropSideButton(activeBookingModel, etaButtonInActiveBooking, pickupButtonInActiveBooking, onRouteActiveBookingBtn,
                                frontTagActiveBooking);
                } else if (activeBookingModel.getStatus().equals("Accepted")
                        || activeBookingModel.getStatus().equals("Picked up")) {
                    //	listActiveBooking.setScrollContainer(false);
                    if (activeBookingModel.getSource().equals("Temando")) {

                        etaButtonInActiveBooking.setClickable(false);
                        pickupButtonInActiveBooking.setClickable(true);
                        onRouteActiveBookingBtn.setClickable(false);

                        onRouteActiveBookingBtn.setVisibility(View.GONE);
                        etaButtonInActiveBooking.setVisibility(View.VISIBLE);
                        pickupButtonInActiveBooking.setVisibility(View.VISIBLE);

                        if (LoginZoomToU.isTablet(activeBookingContext))
                            frontTagActiveBooking.animate().translationX(-220).withLayer();
                        else if (LoginZoomToU.device_Density == 1.0)
                            frontTagActiveBooking.animate().translationX(-220).withLayer();
                        else if (LoginZoomToU.device_Density == 1.5)
                            frontTagActiveBooking.animate().translationX(-330).withLayer();
                        else if (LoginZoomToU.device_Density == 2.0)
                            frontTagActiveBooking.animate().translationX(-440).withLayer();
                        else if (LoginZoomToU.device_Density == 3.0)
                            frontTagActiveBooking.animate().translationX(-660).withLayer();
                        else
                            frontTagActiveBooking.animate().translationX(-880).withLayer();

                    } else if (activeBookingModel.getStatus().equals("Accepted")
                            && activeBookingModel.getSource().equals("DHL")) {
                        frontTagActiveBooking.animate().translationX(0).withLayer();

                        hideActiveListSlideButton(etaButtonInActiveBooking, pickupButtonInActiveBooking,
                                onRouteActiveBookingBtn, attemptdeliveryBtnInActiveBooking);
                    } else if (activeBookingModel.getStatus().equals("Accepted")
                            && activeBookingModel.getRunType().equals("SMARTSORT")) {
                        onRouteActiveBookingBtn.setText("Open\nBarcode\nScanner");
                        setOnRouteButtonForRunOrNormal(position, frontTagActiveBooking,
                                etaButtonInActiveBooking, pickupButtonInActiveBooking, onRouteActiveBookingBtn);
                    } else {
                        onRouteActiveBookingBtn.setText("On route");
                        setOnRouteButtonForRunOrNormal(position, frontTagActiveBooking,
                                etaButtonInActiveBooking, pickupButtonInActiveBooking, onRouteActiveBookingBtn);
                    }
                }

                reassignBtnInActiveBooking.setClickable(false);
                attemptdeliveryBtnInActiveBooking.setClickable(false);
                mHandlerForUIvisibility.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                        reassignBtnInActiveBooking.setVisibility(View.GONE);
                    }
                }, 200);
            } else {
                //listActiveBooking.setScrollContainer(true);
                frontTagActiveBooking.animate().translationX(0).withLayer();

                etaButtonInActiveBooking.setClickable(false);
                pickupButtonInActiveBooking.setClickable(false);
                onRouteActiveBookingBtn.setClickable(false);
                attemptdeliveryBtnInActiveBooking.setClickable(false);
                reassignBtnInActiveBooking.setClickable(false);

                mHandlerForUIvisibility.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        etaButtonInActiveBooking.setVisibility(View.GONE);
                        pickupButtonInActiveBooking.setVisibility(View.GONE);
                        onRouteActiveBookingBtn.setVisibility(View.GONE);
                        attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
                        reassignBtnInActiveBooking.setVisibility(View.GONE);
                    }
                }, 200);
            }
            //	}

            reassignBtnInActiveBooking.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (LoginZoomToU.IS_TEAM_LEADER) {
                        new AssignToOtherCourier_Functionality(activeBookingContext, ActiveBookingView.this, null, true, activeBookingModel.getBookingId());
                    }else {
                        assignToOtherCourier_functionality=new AssignToOtherCourier_Functionality(activeBookingContext,activeBookingModel.getBookingId());
                        assignToOtherCourier_functionality.dialogToShowDriversListToAllocateBooking();
                    }
                }
            });


            /************* Dispatch again to other couriers **/
            attemptdeliveryBtnInActiveBooking.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    LocationManager manager = (LocationManager) activeBookingContext.getSystemService(Context.LOCATION_SERVICE);
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);

                    if (activeBookingModel.getStatus().equals("On Route to Dropoff") || activeBookingModel.getStatus().equals("Tried to deliver")
                            || activeBookingModel.getStatus().equals("Delivery Attempted")) {
                        if (checkPermissions()) {
                            if (statusOfGPS) {
                            	itemSelectedInActiveBookingList = position;
                                if (!activeBookingModel.getSource().equals("DHL") && activeBookingModel.isATL()) {
                                    atlDialogAlert();
                                } else if (activeBookingModel.getPick_ContactName().equalsIgnoreCase("Telstra")) {
                                    callForTTDAttempt(activeBookingModel.isDoesAlcoholDeliveries());
                                } else {
                                    int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(activeBookingModel.getDrop_GPSX(),
                                            activeBookingModel.getDrop_GPSY());
                                    if (activeBookingModel.getSource().equals("DHL")
                                            && activeBookingModel.isATL() == false
                                            && activeBookingModel.isAuthorityToLeavePermitted() == false) {
                                        if (distanceFromCurrentToDrop > 1000)
                                            DialogActivity.alertDialogView(activeBookingContext, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                        else
                                            dialogNonATLBooking(2);
                                    } else if ((activeBookingModel.getSource().equals("DHL") || activeBookingModel.getRunType().equals("SMARTSORT"))
                                            && activeBookingModel.isATL())
                                        openBarCodeScannerView(5);
                                    else if (activeBookingModel.getSource().equals("DHL") || activeBookingModel.getRunType().equals("SMARTSORT"))
                                        if (distanceFromCurrentToDrop > 1000)
                                            DialogActivity.alertDialogView(activeBookingContext, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                        else
                                            openBarCodeScannerView(2);
                                    else {
                                        getCurrentLocation(activeBookingContext);
                                        if (distanceFromCurrentToDrop > 1000)
                                            DialogActivity.alertDialogView(activeBookingContext, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                                        else {
                                            callForTTDAttempt(activeBookingModel.isDoesAlcoholDeliveries());
                                        }
                                    }
                                }
                            } else
                                DialogActivity.alertDialogView(activeBookingContext, "Error!", "Please enable GPS location to complete this delivery. If you’re having trouble, please contact support via the help button in the top right corner.");
                        } else
                            requestPermissions();

                    } else if (activeBookingModel.getStatus().equals("Accepted") || activeBookingModel.getStatus().equals("On Route to Pickup")) {
                        if (mActionMode != null)
                            mActionMode.finish();
                        itemSelectedInActiveBookingList = position;
                        dispatchWindow();
                    }
                }
            });

            //*********** On Route click event
            onRouteActiveBookingBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionMode != null)
                        mActionMode.finish();
                    itemSelectedInActiveBookingList = position;

                    if (activeBookingModel.getRunType().equals("SMARTSORT")) {
                        if (activeBookingModel.getStatus().equals("Picked up")) {
                            callOnRouteTask();
                        } else {
                            Intent intent = new Intent(activeBookingContext, BarcodeScanner.class);
                            intent.putExtra("ScanAWBForPick", 1);
                            intent.putExtra("RunType", activeBookingModel.getRunType());
                            activeBookingContext.startActivity(intent);
                            intent = null;
                        }
                    } else
                        callOnRouteTask();
                }
            });

            //*********** Pickup button click event
            pickupButtonInActiveBooking.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BookingView.bookingListArray.size() > 0) {
                        if (mActionMode != null)
                            mActionMode.finish();
                        itemSelectedInActiveBookingList = position;
                        if (uploadDateBookingStrActiveList != null)
                            uploadDateBookingStrActiveList = null;

                        if (activeBookingModel.getStatus().equals("On Route to Dropoff")
                                || activeBookingModel.getStatus().equals("Tried to deliver")
                                || activeBookingModel.getStatus().equals("Delivery Attempted")) {
                            uploadDateBookingStrActiveList = activeBookingModel.getDropDateTime();
                            if (activeBookingModel.getSource().equals("DHL")) {
                                if (activeBookingModel.getSource().equals("DHL")
                                        && activeBookingModel.isATL() == false
                                        && activeBookingModel.isAuthorityToLeavePermitted() == false)
                                    dialogNonATLBooking(1);
                                else
                                    alertToShowSpecialInsBeforePickOrDrop(position, activeBookingModel.getDrop_Notes());
                            } else if (activeBookingModel.getRunType().equals("SMARTSORT")) {
                                userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_ContactName();
//								if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).isDoesAlcoholDeliveries())
//									dropAlcoholDeliveryAlerts_Obj = new DropAlcoholDelivery_Alerts(activeBookingContext, ActiveBookingView.this, ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)), itemSelectedInActiveBookingList);
//								else
//									openBarCodeScannerView(4);
                                openBarCodeScannerView(4);
                            } else
                                checkDeliveryDropIdAlert(position);
                        } else if (activeBookingModel.getStatus().equals("On Route to Pickup")
                                || activeBookingModel.getStatus().equals("Accepted")) {
                            uploadDateBookingStrActiveList = activeBookingModel.getPickupDateTime();
                            alertToShowSpecialInsBeforePickOrDrop(position, activeBookingModel.getPick_Notes());
                        }
                    }
                }
            });

            //*********** Update ETA click event
            etaButtonInActiveBooking.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (BookingView.bookingListArray.size() > 0) {
                        if (mActionMode != null)
                            mActionMode.finish();
                        itemSelectedInActiveBookingList = position;
                        if (etaDialogInactiveBookingList != null)
                            etaDialogInactiveBookingList = null;
                        etaDialogInactiveBookingList = new Dialog(activeBookingContext);
                        etaDialogInactiveBookingList.setCancelable(false);
                        etaDialogInactiveBookingList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        etaDialogInactiveBookingList.setContentView(R.layout.etadialog);

                        Window window = etaDialogInactiveBookingList.getWindow();
                        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.TOP;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);

                        RelativeLayout etaTimeLayout = (RelativeLayout) etaDialogInactiveBookingList.findViewById(R.id.etaTimeLayout);
                        String dateBookingStrActiveBooking = null;

                        if (etaTitleTextBG != null)
                            etaTitleTextBG = null;
                        etaTitleTextBG = (TextView) etaDialogInactiveBookingList.findViewById(R.id.etaTitleTextBG);

                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss.SSS");

                        if (activeBookingModel.getStatus().equals("On Route to Pickup")) {
                            etaTitleTextBG.setText("Please enter in your arrival time to pickup the goods.");
                            dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModel.getPickupDateTime());
                        } else if (activeBookingModel.getStatus().equals("On Route to Dropoff") || activeBookingModel.getStatus().equals("Tried to deliver")
                                || activeBookingModel.getStatus().equals("Delivery Attempted")) {
                            etaTitleTextBG.setText("Update your ETA, and notify the drop off.");
                            dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModel.getDropDateTime());
                        } else {
                            if (activeBookingModel.getSource().equals("Temando")) {
                                if (activeBookingModel.getStatus().equals("Accepted")) {
                                    etaTitleTextBG.setText("Please enter in your arrival time to pickup the goods.");
                                    dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModel.getPickupDateTime());
                                } else if (activeBookingModel.getStatus().equals("Picked up")) {
                                    etaTitleTextBG.setText("Update your ETA, and notify the drop off.");
                                    dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + activeBookingModel.getDropDateTime());
                                }
                            }
                        }

                        dateBookingStrActiveBooking = dateBookingStrActiveBooking + "T" + sdf1.format(c.getTime());
                        SimpleDateFormat sdf;
                        sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
                        Date convertedDate = new Date();
                        try {
                            convertedDate = sdf.parse(dateBookingStrActiveBooking);
                            sdf = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (uploadDateBookingStrActiveList != null)
                            uploadDateBookingStrActiveList = null;
                        // Date uploaded to server
                        uploadDateBookingStrActiveList = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
                        convertedDate = null;
                        dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.getPickOrDropDateTimeFromServer(dateBookingStrActiveBooking);

                        etaMsgTextBGActive = (TextView) etaDialogInactiveBookingList.findViewById(R.id.etaMsgTextBG);

                        etaMsgTextBGActive.setText(dateBookingStrActiveBooking);

                        if (selectPickUpEtaImg == null)
                            selectPickUpEtaImg = (ImageView) etaDialogInactiveBookingList.findViewById(R.id.selectPickUpEta);

                        Button etaCancelBtnBG = (Button) etaDialogInactiveBookingList.findViewById(R.id.etaCancelBtnBG);
                        Button etaDoneBtnBG = (Button) etaDialogInactiveBookingList.findViewById(R.id.etaDoneBtnBG);

                        etaTimeLayout.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectPickUpEtaImg.setImageResource(R.drawable.icon_up);
                                if (timeFragmentActiveList != null)
                                    timeFragmentActiveList = null;
                                timeFragmentActiveList = new TimePickerFragment();
                                ((TimePickerFragment) timeFragmentActiveList).setTxtOnETADialogView(etaMsgTextBGActive, selectPickUpEtaImg,
                                        activeBookingModel.getPickupDateTime());
                                timeFragmentActiveList.show(((FragmentActivity) activeBookingContext).getSupportFragmentManager(), "timePicker");
                            }
                        });

                        etaCancelBtnBG.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (timeFragmentActiveList != null)
                                    timeFragmentActiveList = null;
                                etaDialogInactiveBookingList.dismiss();
                                etaDialogInactiveBookingList = null;
                            }
                        });

                        etaDoneBtnBG.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    uploadDateBookingStrActiveList = timeFragmentActiveList.uploadTimeStr;
                                    timeFragmentActiveList = null;
                                    etaDialogInactiveBookingList.dismiss();
                                    etaDialogInactiveBookingList = null;
                                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                                        UpdatePickUpETAAsyncTask();
                                        /*new UpdatePickUpETAAsyncTask().execute();*/
                                    else
                                        DialogActivity.alertDialogView(activeBookingContext, "No Network!", "No Network connection, Please try again later.");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        etaDialogInactiveBookingList.show();
                    }
                }
            });
        }

        private boolean checkPermissions() {
            return ActivityCompat.checkSelfPermission(activeBookingContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activeBookingContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }

        private void requestPermissions() {
            ActivityCompat.requestPermissions((Activity) activeBookingContext, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
        }


        private void openPickDropSideButton(All_Bookings_DataModels activeBookingModel, ImageView etaButtonInActiveBooking, ImageView pickupButtonInActiveBooking, Button onRouteActiveBookingBtn, RelativeLayout frontTagActiveBooking) {
            etaButtonInActiveBooking.setClickable(true);
            pickupButtonInActiveBooking.setClickable(true);
            onRouteActiveBookingBtn.setClickable(false);

            etaButtonInActiveBooking.setVisibility(View.VISIBLE);
            pickupButtonInActiveBooking.setVisibility(View.VISIBLE);
            onRouteActiveBookingBtn.setVisibility(View.GONE);

            if (LoginZoomToU.isTablet(activeBookingContext))
                frontTagActiveBooking.animate().translationX(-220).withLayer();
            else if (LoginZoomToU.device_Density == 1.0)
                frontTagActiveBooking.animate().translationX(-220).withLayer();
            else if (LoginZoomToU.device_Density == 1.5)
                frontTagActiveBooking.animate().translationX(-330).withLayer();
            else if (LoginZoomToU.device_Density == 2.0)
                frontTagActiveBooking.animate().translationX(-440).withLayer();
            else if (LoginZoomToU.device_Density == 3.0)
                frontTagActiveBooking.animate().translationX(-660).withLayer();
            else
                frontTagActiveBooking.animate().translationX(-880).withLayer();
        }

        private void hideActiveListSlideButton(ImageView etaButtonInActiveBooking, ImageView pickupButtonInActiveBooking, Button onRouteActiveBookingBtn, Button attemptdeliveryBtnInActiveBooking) {
            etaButtonInActiveBooking.setClickable(false);
            pickupButtonInActiveBooking.setClickable(false);
            onRouteActiveBookingBtn.setClickable(false);
            attemptdeliveryBtnInActiveBooking.setClickable(false);
            etaButtonInActiveBooking.setVisibility(View.GONE);
            pickupButtonInActiveBooking.setVisibility(View.GONE);
            onRouteActiveBookingBtn.setVisibility(View.GONE);
            attemptdeliveryBtnInActiveBooking.setVisibility(View.GONE);
        }

        private void setOnRouteButtonForRunOrNormal(int position, RelativeLayout frontTagActiveBooking,
                                                    ImageView etaButtonInActiveBooking, ImageView pickupButtonInActiveBooking, Button onRouteActiveBookingBtn) {

            if (LoginZoomToU.isTablet(activeBookingContext)) {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(110, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-110).withLayer();
            } else if (LoginZoomToU.device_Density == 1.0) {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(110, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-110).withLayer();
            } else if (LoginZoomToU.device_Density == 1.5) {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(165, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-165).withLayer();
            } else if (LoginZoomToU.device_Density == 2.0) {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(220, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-220).withLayer();
            } else if (LoginZoomToU.device_Density == 3.0) {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(330, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-330).withLayer();
            } else {
                //onRouteActiveBookingBtnParam = new RelativeLayout.LayoutParams(440, frontTagActiveBooking.getHeight());
                frontTagActiveBooking.animate().translationX(-440).withLayer();
            }

            etaButtonInActiveBooking.setClickable(false);
            pickupButtonInActiveBooking.setClickable(false);
            onRouteActiveBookingBtn.setClickable(true);

            onRouteActiveBookingBtn.setVisibility(View.VISIBLE);
            etaButtonInActiveBooking.setVisibility(View.GONE);
            pickupButtonInActiveBooking.setVisibility(View.GONE);
        }

        private void slideRightForAttemptDelivery(int translationOfX, RelativeLayout frontTagActiveBooking) {
            int viewTranslation;
            if (LoginZoomToU.isTablet(activeBookingContext))
                viewTranslation = translationOfX;
            else if (LoginZoomToU.width <= 320)
                viewTranslation = translationOfX;
            else if (LoginZoomToU.width > 320 && LoginZoomToU.width <= 540)
                viewTranslation = (int) (translationOfX * 1.5);
            else if (LoginZoomToU.width > 540 && LoginZoomToU.width <= 720)
                viewTranslation = translationOfX * 2;
            else if (LoginZoomToU.width > 720 && LoginZoomToU.width <= 1080)
                viewTranslation = translationOfX * 3;
            else
                viewTranslation = translationOfX * 4;

            frontTagActiveBooking.animate().translationX(viewTranslation).withLayer();
        }

        private void showCounterFromCreatedInMenulog(TextView menuLogTimerText, int position) {

            menuLogTimerText.setVisibility(View.VISIBLE);
            menuLogTimerText.setTextColor(Color.WHITE);
            menuLogTimerText.setGravity(Gravity.CENTER);
            menuLogTimerText.setBackgroundResource(R.drawable.roundedskybluebg);
            try {
                if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getCreatedDateTime().equals("")) {
                    String minInStr = LoginZoomToU.checkInternetwithfunctionality.getMenulogTimeDiff(((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getCreatedDateTime(), true);
                    menuLogTimerText.setText(minInStr);
                } else {
                    menuLogTimerText.setText("NA");
                }
            } catch (Exception e) {
                menuLogTimerText.setText("NA");
            }
        }
    }

    //************** Call For TTD attempt window *************
    public void callForTTDAttempt(boolean doesAlcoholDeliveries) {
        if (doesAlcoholDeliveries)
            attemptDeliveryWindow(1);
        else
            attemptDeliveryWindow(0);
    }

    //********* Show Alert if any ID exist against customer provided id
    public void checkDeliveryDropIdAlert(int position) {
        try {
            if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityNumber().equals("")
                    && !((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityNumber().equals("null")
                    && ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityNumber() != null) {
                deliveryIdCheckAlert(position);
            } else
                alertToShowSpecialInsBeforePickOrDrop(position, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_Notes());
        } catch (Exception e) {
            e.printStackTrace();
            alertToShowSpecialInsBeforePickOrDrop(position, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_Notes());
        }
    }

    //********************* Customer ID check alert before Drop off the booking
    Dialog alertDialogIDCheckAlert;

    private void deliveryIdCheckAlert(final int position) {
        try {
            try {
                if (alertDialogIDCheckAlert != null)
                    if (alertDialogIDCheckAlert.isShowing())
                        alertDialogIDCheckAlert.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
            alertDialogIDCheckAlert = new Dialog(activeBookingContext);
            alertDialogIDCheckAlert.setCancelable(false);
            alertDialogIDCheckAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ACACB0")));
            alertDialogIDCheckAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            alertDialogIDCheckAlert.setContentView(R.layout.delivery_id_check_alert);

            Window window = alertDialogIDCheckAlert.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            WindowManager.LayoutParams wlp = window.getAttributes();

            wlp.gravity = Gravity.CENTER;
            wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(wlp);

            ImageView deliveryCheckDialog_Image = (ImageView) alertDialogIDCheckAlert.findViewById(R.id.deliveryCheckDialog_Image);
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityType().equals("DriversLicence"))
                deliveryCheckDialog_Image.setImageResource(R.drawable.licence);
            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityType().equals("Passport"))
                deliveryCheckDialog_Image.setImageResource(R.drawable.customer_passport);
            else
                deliveryCheckDialog_Image.setImageResource(R.drawable.customer_citizencard);

            TextView headingTxtDeliveryIdCheck = (TextView) alertDialogIDCheckAlert.findViewById(R.id.headingTxtDeliveryIdCheck);


            TextView msgTxtDeliveryIdCheck = (TextView) alertDialogIDCheckAlert.findViewById(R.id.msgTxtDeliveryIdCheck);

            try {
                msgTxtDeliveryIdCheck.setText("Please enter the last 4 digits of the recipient's " + ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityType() + "\n\n" +
                        "NOTE:\n" +
                        "- Photocopies are not valid ID\n" +
                        "- Name of the recipient must also match name on the ID.");
            } catch (Exception e) {
                e.printStackTrace();
                msgTxtDeliveryIdCheck.setText("Please enter the last 4 digits of the recipient's ID number\n\n" +
                        "NOTE:\n" +
                        "- Photocopies are not valid ID\n" +
                        "- Name of the recipient must also match name on the ID.");
            }


            final EditText edtEnterPin = (EditText) alertDialogIDCheckAlert.findViewById(R.id.edtEnterPin);


            TextView closeBtnIdAlert = (TextView) alertDialogIDCheckAlert.findViewById(R.id.closeBtnIdAlert);

            Paint paint = new Paint();
            paint.setARGB(255, 255, 71, 106);
            paint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
            closeBtnIdAlert.setPaintFlags(paint.getFlags());

            closeBtnIdAlert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogIDCheckAlert.dismiss();
                    alertDialogIDCheckAlert = null;
                }
            });

            Button confirmBtnDeliveryIdCheck = (Button) alertDialogIDCheckAlert.findViewById(R.id.confirmBtnDeliveryIdCheck);

            confirmBtnDeliveryIdCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!edtEnterPin.getText().toString().equals("")) {
                        if (edtEnterPin.getText().toString().equalsIgnoreCase(((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityNumber())) {
                            alertToShowSpecialInsBeforePickOrDrop(position, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_Notes());
                            alertDialogIDCheckAlert.dismiss();
                            alertDialogIDCheckAlert = null;
                        } else
                            dialogInActiveDetailToShow_IDCkeck_Error_Msg("", Html.fromHtml("Please try again, or contact<br><font color=\"##45515b\"><big><b>" + ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getCustomerName() + "</b></big></font><br>on " +
                                    "<font color=\"#00A6E3\"><big><b>" + ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getCustomerContact() + "</b></big></font><br>to check the ID they received."));
                    } else
                        Toast.makeText(activeBookingContext, "Please enter the ID number against customer provided number to drop this delivery", Toast.LENGTH_LONG).show();
                }
            });
            alertDialogIDCheckAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
            alertToShowSpecialInsBeforePickOrDrop(position, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_Notes());
        }
    }

    //********************* Customer ID check error alert
    Dialog idCheckErrorDialog;

    private void dialogInActiveDetailToShow_IDCkeck_Error_Msg(String headerMsg, Spanned msgStr) {
        try {
            if (idCheckErrorDialog != null)
                if (idCheckErrorDialog.isShowing())
                    idCheckErrorDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        idCheckErrorDialog = null;
        idCheckErrorDialog = new Dialog(activeBookingContext);
        idCheckErrorDialog.setCancelable(false);
        idCheckErrorDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        idCheckErrorDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#29000000")));
        idCheckErrorDialog.setContentView(R.layout.id_check_error_alert);

        Window window = idCheckErrorDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView errorMsgTxt_IDCheck = (TextView) idCheckErrorDialog.findViewById(R.id.errorMsgTxt_IDCheck);


        TextView errorMsgHint_IDCheck = (TextView) idCheckErrorDialog.findViewById(R.id.errorMsgHint_IDCheck);

        errorMsgHint_IDCheck.setText(msgStr);

        Button okBtn__IDCheckError = (Button) idCheckErrorDialog.findViewById(R.id.okBtn__IDCheckError);

        okBtn__IDCheckError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheckErrorDialog.dismiss();
            }
        });

        idCheckErrorDialog.show();
    }


    //************* Alert dialog to show special instruction before Pickup or Dropoff ***********
    private void alertToShowSpecialInsBeforePickOrDrop(final int position, String msgStr) {
        try {
            if (!msgStr.equals("") && !msgStr.equals("null")) {
                try {
                    if (alertDialogInActiveList != null)
                        if (alertDialogInActiveList.isShowing())
                            alertDialogInActiveList.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                alertDialogInActiveList = new Dialog(activeBookingContext);
                alertDialogInActiveList.setCancelable(false);
                alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                alertDialogInActiveList.setContentView(R.layout.new_dialogview);

                Window window = alertDialogInActiveList.getWindow();
                window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.CENTER;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                TextView titleDialogNew = (TextView) alertDialogInActiveList.findViewById(R.id.titleDialogNew);

                titleDialogNew.setText("Special Instruction!");

                TextView dialogMessageTextNew = (TextView) alertDialogInActiveList.findViewById(R.id.dialogMessageTextNew);

                dialogMessageTextNew.setText(msgStr);

                Button okBtnNewDialog = (Button) alertDialogInActiveList.findViewById(R.id.okBtnNewDialog);

                okBtnNewDialog.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        alertDialogInActiveList.dismiss();
                        alertDialogInActiveList = null;
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                           /* new AcceptNotesTimeStampAsyncTask(position).execute();*/
                            AcceptNotesTimeStampAsyncTask(position);
                        else
                            DialogActivity.alertDialogView(activeBookingContext, "No Network!", "No network connection, Please try again later.");
                    }
                });

                alertDialogInActiveList.show();
            } else
                pickOrDropFunctionality(position);
        } catch (Exception e) {
            e.printStackTrace();
            pickOrDropFunctionality(position);
        }
    }

    private void AcceptNotesTimeStampAsyncTask(int acceptNoteItemposition){
        final boolean[] isAcceptNotesTimeStampSuccess = {false};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                if (progressDialogToLoadActiveBookings != null)
                    progressDialogToLoadActiveBookings = null;
                progressDialogToLoadActiveBookings = new ProgressDialog(activeBookingContext);
                Custom_ProgressDialogBar.inItProgressBar(progressDialogToLoadActiveBookings);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    String responseAcceptNotesTimeStamp = "";
                    if (((All_Bookings_DataModels) BookingView.bookingListArray.get(acceptNoteItemposition)).getStatus().equals("On Route to Pickup"))
                        responseAcceptNotesTimeStamp = webServiceHandler.addAcceptNotesTimeStamp((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(acceptNoteItemposition)).getBookingId(), false);
                    else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(acceptNoteItemposition)).getStatus().equals("On Route to Dropoff")
                            || ((All_Bookings_DataModels) BookingView.bookingListArray.get(acceptNoteItemposition)).getStatus().equals("Tried to deliver"))
                        responseAcceptNotesTimeStamp = webServiceHandler.addAcceptNotesTimeStamp((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(acceptNoteItemposition)).getBookingId(), true);

                    JSONObject jOBJForRequestPick = new JSONObject(responseAcceptNotesTimeStamp);
                    isAcceptNotesTimeStampSuccess[0] = jOBJForRequestPick.getBoolean("success");
                    jOBJForRequestPick = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (progressDialogToLoadActiveBookings != null)
                        if (progressDialogToLoadActiveBookings.isShowing())
                            Custom_ProgressDialogBar.dismissProgressBar(progressDialogToLoadActiveBookings);

                    if (isAcceptNotesTimeStampSuccess[0])
                        pickOrDropFunctionality(acceptNoteItemposition);
                    else
                        DialogActivity.alertDialogView(activeBookingContext, "Sorry!", "Please try again.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }


    //******** Pickup and Dropoff functionality *********
    private void pickOrDropFunctionality(final int position) {
        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getSource().equals("Temando")
                || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getSource().equals("DHL")) {
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getSource().equals("Temando")) {
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Accepted")) {
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getPick_ContactName();
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        //new PackageUploadWithETAAsyncTask().execute();
                        PackageUploadWithETAAsyncTask();
                    else
                        DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
                } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Picked up")) {
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_ContactName();
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        /*new RequestForDropOffTemendoAsyncTask().execute();*/
                        RequestForDropOffTemendoAsyncTask();
                    else
                        DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
                }
            } else {
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Pickup")) {
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getPick_ContactName();
                    if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                        //new PackageUploadWithETAAsyncTask().execute();
                        PackageUploadWithETAAsyncTask();
                    else
                        DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
                } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Tried to deliver")
                        || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Delivery Attempted")) {
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDrop_ContactName();
//					if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).isDoesAlcoholDeliveries())
//						dropAlcoholDeliveryAlerts_Obj = new DropAlcoholDelivery_Alerts(activeBookingContext, ActiveBookingView.this, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)), position);
//					else
//						openBarCodeScannerView(4);
                    openBarCodeScannerView(4);
                }
            }
        } else if (WebserviceHandler.isRoutific == true) {
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Tried to deliver")
                    || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Delivery Attempted")) {
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getRunType().equals("SMARTSORT"))
                    openBarCodeScannerView(4);
                else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).isDoesAlcoholDeliveries())
                    dropAlcoholDeliveryAlerts_Obj = new DropAlcoholDelivery_Alerts(activeBookingContext, ActiveBookingView.this, ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)), position);
                else
                    processToDropNonDHLAndNormalDelivery(position);
            } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Pickup"))
                takePic("Do you want to take photo of item to pick.");
        } else {
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Tried to deliver")
                    || ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("Delivery Attempted"))
                takePic("Please take a photo of the parcel you are delivering. Please note this should be a picture of the parcel only.");
            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getStatus().equals("On Route to Pickup")) {
                if (etaDialogInactiveBookingList != null)
                    etaDialogInactiveBookingList = null;
                etaDialogInactiveBookingList = new Dialog(activeBookingContext);
                etaDialogInactiveBookingList.setCancelable(false);
                etaDialogInactiveBookingList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                etaDialogInactiveBookingList.setContentView(R.layout.etadialog);

                Window window = etaDialogInactiveBookingList.getWindow();
                window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.TOP;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                RelativeLayout etaTimeLayout = (RelativeLayout) etaDialogInactiveBookingList.findViewById(R.id.etaTimeLayout);

                if (etaTitleTextBG != null)
                    etaTitleTextBG = null;
                etaTitleTextBG = (TextView) etaDialogInactiveBookingList.findViewById(R.id.etaTitleTextBG);

                etaTitleTextBG.setText("Update your ETA, and notify the drop off");

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss.SSS");

                String dateBookingStrActiveBooking;
                dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.returnDateFromDeviceToPick("" + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime());
                dateBookingStrActiveBooking = dateBookingStrActiveBooking + "T" + sdf1.format(c.getTime());

                SimpleDateFormat sdf;
                sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
                Date convertedDate = new Date();
                try {
                    convertedDate = sdf.parse(dateBookingStrActiveBooking);
                    sdf = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (uploadDateBookingStrActiveList != null)
                    uploadDateBookingStrActiveList = null;
                // Date uploaded to server
                uploadDateBookingStrActiveList = LoginZoomToU.checkInternetwithfunctionality.getPickerDateTimeFromDevice(convertedDate);
                convertedDate = null;
                dateBookingStrActiveBooking = LoginZoomToU.checkInternetwithfunctionality.getPickOrDropDateTimeFromServer(dateBookingStrActiveBooking);

                if (etaMsgTextBGActive != null)
                    etaMsgTextBGActive = null;
                etaMsgTextBGActive = (TextView) etaDialogInactiveBookingList.findViewById(R.id.etaMsgTextBG);


                etaMsgTextBGActive.setText(dateBookingStrActiveBooking);

                if (selectPickUpEtaImg == null)
                    selectPickUpEtaImg = (ImageView) etaDialogInactiveBookingList.findViewById(R.id.selectPickUpEta);

                ImageView etaCancelBtnBG = (ImageView) etaDialogInactiveBookingList.findViewById(R.id.etaCancelBtnBG);
                ImageView etaDoneBtnBG = (ImageView) etaDialogInactiveBookingList.findViewById(R.id.etaDoneBtnBG);

                etaTimeLayout.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectPickUpEtaImg.setImageResource(R.drawable.icon_up);
                        if (timeFragmentActiveList != null)
                            timeFragmentActiveList = null;
                        timeFragmentActiveList = new TimePickerFragment();
                        ((TimePickerFragment) timeFragmentActiveList).setTxtOnETADialogView(etaMsgTextBGActive, selectPickUpEtaImg,
                                ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getPickupDateTime());
                        timeFragmentActiveList.show(((FragmentActivity) activeBookingContext).getSupportFragmentManager(), "timePicker");
                    }
                });

                etaCancelBtnBG.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timeFragmentActiveList != null)
                            timeFragmentActiveList = null;
                        etaDialogInactiveBookingList.dismiss();
                        etaDialogInactiveBookingList = null;
                    }
                });

                etaDoneBtnBG.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (timeFragmentActiveList != null) {
                            uploadDateBookingStrActiveList = timeFragmentActiveList.uploadTimeStr;
                            timeFragmentActiveList = null;
                        }
                        etaDialogInactiveBookingList.dismiss();
                        etaDialogInactiveBookingList = null;
                        takePic("Do you want to take photo of item to pick.");
                    }
                });
                etaDialogInactiveBookingList.show();
            }
        }
    }

    public void processToDropNonDHLAndNormalDelivery(int position) {
        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getDropIdentityNumber().equals("")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(position)).getPick_ContactName().equalsIgnoreCase("Telstra"))
            takePic("Please take a photo of the item to be delivered.\nNote:\nDO NOT photograph the recipient, or the ID");
        else
            takePic("Please take a photo of the parcel you are delivering. Please note this should be a picture of the parcel only.");
    }


    public static class ViewHolderPatternActiveBooking {
        // generic return type to reduce the casting
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }

    //*********** Pop up for take picture of pick/drop item  *************//
    public void takePic(String alrtMsg) {
        Dialog enterFieldDialog = new Dialog(activeBookingContext);
        enterFieldDialog.setCancelable(true);
        enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        enterFieldDialog.setContentView(R.layout.dialogview_take_photo);

        Window window = enterFieldDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        android.view.WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        TextView dialogMessageText = enterFieldDialog.findViewById(R.id.dialogMessageText);
        Button take_photoBtn = enterFieldDialog.findViewById(R.id.take_photoBtn);
        dialogMessageText.setText(alrtMsg);
        take_photoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL")) {
                    takeImgForDHLDelivery();
                } else
                    selectImage();


                try {
                    LocalBroadcastManager.getInstance(activeBookingContext).unregisterReceiver(receiverForSilentNotificationActiveList);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                enterFieldDialog.cancel();
            }
        });


        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName().equalsIgnoreCase("Telstra"))
            showSkipBtnOnPackagePhoto(enterFieldDialog);
        else if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getIsCakeAndFlower() == 0)
            showSkipBtnOnPackagePhoto(enterFieldDialog);

        enterFieldDialog.show();
    }

    private void takeImgForDHLDelivery() {
        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff")
                || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")) {
            int screenCount = 1;
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                    && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).isATL())
                screenCount = 3;
            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                    && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).isATL())
                screenCount = 2;
            else
                screenCount = 1;

            Intent i = new Intent(activeBookingContext, CameraOverlay_Activity.class);
            i.putExtra("ScreenCount", screenCount);
            activeBookingContext.startActivity(i);
            i = null;
        } else
            selectImage();
    }

    //************ Show Skip button while taking package image **************
    private void showSkipBtnOnPackagePhoto(Dialog alertDialog) {
        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff")
                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")) {
            // on pressing skip button
            alertDialog.findViewById(R.id.skipBtn).setVisibility(View.VISIBLE);
            alertDialog.findViewById(R.id.skipBtn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.cancel();
                    skipOnPickOrDropBooking();
                }
            });

        }
    }

    private void skipOnPickOrDropBooking() {
        try {
            if (photo != null)
                photo.recycle();
            photo = null;
            if (isDropOffFromATL) {
                userNameInActiveBookingList = "ATL - " + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName();
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    /*new DropOffFromATLAsyncTask().execute();*/
                    DropOffFromATLAsyncTask();
                else
                    DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
            } else {
                PushReceiver.isCameraOpen = true;
                Intent i = new Intent(activeBookingContext, ConfirmPickUpForUserName.class);
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Pickup"))
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName();
                else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff")
                        || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                        || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Delivery Attempted"))
                    userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_ContactName();

                if (isActionBarButtonTaped == true) {
                    i.putExtra("isActionBarButtonTaped", true);
                    isActionBarButtonTaped = false;
                } else {
                    i.putExtra("isActionBarButtonTaped", false);
                    isActionBarButtonTaped = false;
                }
                i.putExtra("dropOffEta", uploadDateBookingStrActiveList);
                i.putExtra("positionActiveFragment", itemSelectedInActiveBookingList);
                i.putExtra("userName", userNameInActiveBookingList);
                i.putExtra("dataFromActiveList", 11);
                activeBookingContext.startActivity(i);
                i = null;
                try {
                    LocalBroadcastManager.getInstance(activeBookingContext).unregisterReceiver(receiverForSilentNotificationActiveList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ************ Get image from camera for active booking list
    @SuppressWarnings("deprecation")
    public void getCameraPic_OnActivityResult(int requestCode, Intent data) {
        try {
            PushReceiver.isCameraOpen = false;
            if (requestCode == ActiveBookingDetail_New.TAKE_PHOTO) {
                /******************  1st getting problem on some device ****************/
//				ContentResolver cr = getActivity().getContentResolver();
//	            InputStream in = cr.openInputStream(imageUri);
//	            BitmapFactory.Options options = new BitmapFactory.Options();
//	            options.inSampleSize = 4;
//	            photo = BitmapFactory.decodeStream(in,null,options);

                /*************************  2nd from android developer ******************/
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                if (LoginZoomToU.isImgFromInternalStorage) {
                    File out = new File(activeBookingContext.getFilesDir(), "packageImage.png");
                    if (!out.exists())
                        Toast.makeText(activeBookingContext, "Error while capturing image", Toast.LENGTH_LONG).show();
                    else {
                        BitmapFactory.decodeFile(out.getAbsolutePath(), bmOptions);
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;
                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;
                        photo = BitmapFactory.decodeFile(out.getAbsolutePath(), bmOptions);
                        Functional_Utility.mCurrentPhotoPath = out.getAbsolutePath();
                    }
                } else {
                    BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;
                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;
                    photo = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);
                }
                try {
                    LocalBroadcastManager.getInstance(activeBookingContext).unregisterReceiver(receiverForSilentNotificationActiveList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //	photo = Functional_Utility.getRotatedCameraImg(Functional_Utility.mCurrentPhotoPath, photo);
                pickAndDropBookingAfterTakePkgPhoto();  //******** Take package image photo before Pick and Drop ******
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //******** Take package image photo before Pick and Drop ******
    public void pickAndDropBookingAfterTakePkgPhoto() {
        if (isDropOffFromATL) {
            userNameInActiveBookingList = "ATL - " + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName();
            if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
               /* new DropOffFromATLAsyncTask().execute();*/
                DropOffFromATLAsyncTask();
            else
                DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
        } else {
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Pickup"))
                userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName();
            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                    || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Delivery Attempted"))
                userNameInActiveBookingList = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_ContactName();
            PushReceiver.isCameraOpen = true;
            Intent i = new Intent(activeBookingContext, ConfirmPickUpForUserName.class);
            if (isActionBarButtonTaped == true) {
                i.putExtra("isActionBarButtonTaped", true);
                i.putExtra("cameraPic", ActiveBookingDetail_New.TAKE_PHOTO);
                isActionBarButtonTaped = false;
            } else {
                i.putExtra("cameraPic", ActiveBookingDetail_New.TAKE_PHOTO);
                i.putExtra("isActionBarButtonTaped", false);
                isActionBarButtonTaped = false;
            }
            i.putExtra("userName", userNameInActiveBookingList);
            i.putExtra("dropOffEta", uploadDateBookingStrActiveList);
            i.putExtra("positionActiveFragment", itemSelectedInActiveBookingList);
            i.putStringArrayListExtra("PieceArray",
                    Functional_Utility.getScannedPieceArray(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPiecesArray()));
            i.putExtra("dataFromActiveList", 11);

            activeBookingContext.startActivity(i);
            i = null;
        }
    }

    int ttdSelectedItemPosition = 0;

    /******************  Dispatched to other courier window  ******/
    public void attemptDeliveryWindow(int ttdForAlcoholeDelivery) {
        subBookingView.setVisibility(View.VISIBLE);

        if (attemtDeliveryDialog != null)
            attemtDeliveryDialog = null;
        attemtDeliveryDialog = new Dialog(activeBookingContext);
        attemtDeliveryDialog.setCancelable(false);
        attemtDeliveryDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        attemtDeliveryDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        attemtDeliveryDialog.setContentView(R.layout.attemptdelivery_window);

        Window window = attemtDeliveryDialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER | Gravity.TOP;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView attemptDelievryTitleTxt = (TextView) attemtDeliveryDialog.findViewById(R.id.attemptDelievryTitleTxt);

        attemptDelievryTitleTxt.setText("Attempt delivery!");

        TextView textReasonForLate = (TextView) attemtDeliveryDialog.findViewById(R.id.textReasonForLate);

        textReasonForLate.setVisibility(View.VISIBLE);


        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL"))
            attemtDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt).setVisibility(View.VISIBLE);
        else
            attemtDeliveryDialog.findViewById(R.id.attemptDeliveryBelowTxt).setVisibility(View.GONE);

        Spinner spinnerForTriedToDeliver = (Spinner) attemtDeliveryDialog.findViewById(R.id.spinnerForTriedToDeliver);
        String[] triedToDeliverReason;

        if (ttdForAlcoholeDelivery == 0)
            triedToDeliverReason = new String[]{"NH – Not Home", "RD – Customer Refused Delivery", "CA – Closed on arrival (Business)",
                    "ND – Parcel Not Delivered"};
        else
            triedToDeliverReason = new String[]{"NH – Not Home", "RD – Customer Refused Delivery", "CA – Closed on arrival (Business)",
                    "ND – Parcel Not Delivered / Other", "PA - Person is under the age of 18", "PI - Person is intoxicated"};

        positionForTriedToDeliverReason = triedToDeliverReason[0];
        ArrayAdapter<String> adapter_Position = new ArrayAdapter<String>(activeBookingContext, R.layout.spinneritemxml_white1, triedToDeliverReason) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                ((TextView) v).setTextColor(Color.parseColor("#808080"));
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);

                ((TextView) v).setTextColor(Color.parseColor("#808080"));
                return v;
            }
        };

        spinnerForTriedToDeliver.setAdapter(adapter_Position);
        adapter_Position = null;
        spinnerForTriedToDeliver.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ttdSelectedItemPosition = position;
                positionForTriedToDeliverReason = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (ttdForAlcoholeDelivery == 2) {
            ttdSelectedItemPosition = 4;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason[ttdSelectedItemPosition];
            spinnerForTriedToDeliver.setSelection(ttdSelectedItemPosition);
        } else if (ttdForAlcoholeDelivery == 3) {
            ttdSelectedItemPosition = 5;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason[ttdSelectedItemPosition];
            spinnerForTriedToDeliver.setSelection(ttdSelectedItemPosition);
        } else {
            ttdSelectedItemPosition = 0;           //************ For return to pickup delivery
            positionForTriedToDeliverReason = triedToDeliverReason[ttdSelectedItemPosition];
        }

        triedToDeliverReason = null;
        final EditText attempDeliveryMsgText = (EditText) attemtDeliveryDialog.findViewById(R.id.attempDeliveryMsgText);

        attempDeliveryMsgText.setHint("Please enter any additional notes around why the delivery failed.");

        Button attemptDeliveryCancelBtn = (Button) attemtDeliveryDialog.findViewById(R.id.attemptDeliveryCancelBtn);

        attemptDeliveryCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemtDeliveryDialog.dismiss();
                LoginZoomToU.imm.hideSoftInputFromWindow(attempDeliveryMsgText.getWindowToken(), 0);
                subBookingView.setVisibility(View.GONE);
            }
        });
        Button attemptDeliverySaveBtn = (Button) attemtDeliveryDialog.findViewById(R.id.attemptDeliverySaveBtn);

        attemptDeliverySaveBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginZoomToU.imm.hideSoftInputFromWindow(attempDeliveryMsgText.getWindowToken(), 0);
                deliveryAttemptNoteTxt = attempDeliveryMsgText.getText().toString();
                if (ttdSelectedItemPosition != 3) {
                    callForTTDAttempt();
                } else {
                    if (!deliveryAttemptNoteTxt.equals("")) {
                        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet()) {
                            callForTTDAttempt();
                        } else
                            DialogActivity.alertDialogView(activeBookingContext, "No Network!", "No network connection, Please try again later.");
                    } else {
                        DialogActivity.alertDialogView(activeBookingContext, "Alert!", "Please enter notes.");
                    }
                }
            }

            private void callForTTDAttempt() {
                attemtDeliveryDialog.dismiss();
                attemtDeliveryDialog = null;
                subBookingView.setVisibility(View.GONE);
                /*new AttemptDeliveryAsyncTask().execute()*/;
                AttemptDeliveryAsyncTask();
            }
        });
        attemtDeliveryDialog.show();
    }

    // *********** Background service to Get active booking list items ***********
    public synchronized void activeListBackGroundCalling(String awbNumber, String statusStr) {
        try {
            WebserviceHandler webServiceHandler = new WebserviceHandler();
            activeBookingResponseStr = webServiceHandler.getActiveBookingList(LoginZoomToU.filterDayActiveListStr, CompletedView.endlessCount, awbNumber, statusStr);
            saveDHLResp(activeBookingResponseStr);
            webServiceHandler = null;
        } catch (Exception e) {
            e.printStackTrace();
            activeBookingResponseStr = "0";
        }
    }

    // ***********  Active booking list UI update ***********
    public synchronized void activeListUI_Update() {
        try {
            ADD_POS_ACTIVEBOOKINGLIST = -1;
            DELETE_POS_ACTIVEBOOKINGLIST = -1;
            JSONObject jObjOfActiveList = new JSONObject(activeBookingResponseStr);

            activeDHLBookingCount = jObjOfActiveList.getInt("activeDeliveryRunCount");
            clearArrayOfActiveList();
            JSONArray responseArrayOfActiveBookingData = jObjOfActiveList.getJSONArray("data");
            ArrayList<DHL_SectionInterface> dhlArrayInActiveBookings = null,
                    acceptedDhlBookingArray = null,
                    onRouteToPickDhlBookingArray = null,
                    pickedUpDhlBookingArray = null,
                    onRouteDropOffDhlBookingArray = null,
                    triedToDeliverDhlBookingArray = null,
                    otherStatusDhlBookingArray = null,
                    routeOfRunsFromBookingArray = null;
            ;

            if (LoginZoomToU.activeBookingTab == 1) {
                dhlArrayInActiveBookings = new ArrayList<DHL_SectionInterface>();
                if (LoginZoomToU.getDHLBookingStr.equals("Active")) {
                    DHL_ON_ROUTE_DROP_BOOKING_COUNT = 0;
                    DHL_ON_ROUTE_PICK_BOOKING_COUNT = 0;
                    DHL_PICKED_UP_BOOKING_COUNT = 0;
                }
                acceptedDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                onRouteToPickDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                pickedUpDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                onRouteDropOffDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                triedToDeliverDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                otherStatusDhlBookingArray = new ArrayList<DHL_SectionInterface>();
                routeOfRunsFromBookingArray = new ArrayList<DHL_SectionInterface>();
            }

            JSONObject mainResponseJOBJ = null;
            for (int i = 0; i < responseArrayOfActiveBookingData.length(); i++) {
                mainResponseJOBJ = responseArrayOfActiveBookingData.getJSONObject(i);

                String distanceFromCurrentLoc = "";
                try {
                    distanceFromCurrentLoc = LoginZoomToU.checkInternetwithfunctionality.distanceBetweenTwoPosition
                            (LoginZoomToU.getCurrentLocatnlatitude, LoginZoomToU.getCurrentLocatnLongitude,
                                    mainResponseJOBJ.getString("pickupGPSX"), mainResponseJOBJ.getString("pickupGPSY"));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }

                String orderNumberForMenuLog = "";
                try {
                    if (!mainResponseJOBJ.getString("orderNo").equals("null") || mainResponseJOBJ.getString("orderNo") != null)
                        orderNumberForMenuLog = mainResponseJOBJ.getString("orderNo");
                    else
                        orderNumberForMenuLog = "";
                } catch (Exception e) {
                    e.printStackTrace();
                    orderNumberForMenuLog = "";
                }

                All_Bookings_DataModels dataModel_AllBookingList = new All_Bookings_DataModels();

                dataModel_AllBookingList.setBookingId(mainResponseJOBJ.getInt("bookingId"));
                dataModel_AllBookingList.setCustomerId(mainResponseJOBJ.getString("customerId"));
                dataModel_AllBookingList.setNewCustomer(mainResponseJOBJ.getBoolean("isNewCustomer"));
                dataModel_AllBookingList.setCustomerName(mainResponseJOBJ.getString("customerName"));
                dataModel_AllBookingList.setCustomerCompany(mainResponseJOBJ.getString("customerCompany"));
                dataModel_AllBookingList.setCustomerContact(mainResponseJOBJ.getString("customerContact"));
                dataModel_AllBookingList.setBookingRefNo(mainResponseJOBJ.getString("bookingRefNo"));
                dataModel_AllBookingList.setPickupDateTime(mainResponseJOBJ.getString("pickupDateTime"));
                dataModel_AllBookingList.setPick_Address(mainResponseJOBJ.getString("pickupAddress"));
                dataModel_AllBookingList.setPick_ContactName(mainResponseJOBJ.getString("pickupContactName"));
                dataModel_AllBookingList.setPick_GPSX(mainResponseJOBJ.getString("pickupGPSX"));
                dataModel_AllBookingList.setPick_GPSY(mainResponseJOBJ.getString("pickupGPSY"));
                dataModel_AllBookingList.setPick_Notes(mainResponseJOBJ.getString("pickupNotes"));
                dataModel_AllBookingList.setPick_Phone(mainResponseJOBJ.getString("pickupPhone"));
                dataModel_AllBookingList.setPick_StreetNo(mainResponseJOBJ.getString("pickupStreetNumber"));
                dataModel_AllBookingList.setPick_StreetName(mainResponseJOBJ.getString("pickupStreet"));
                dataModel_AllBookingList.setPick_Suburb(mainResponseJOBJ.getString("pickupSuburb"));
                dataModel_AllBookingList.setDropDateTime(mainResponseJOBJ.getString("dropDateTime"));
                dataModel_AllBookingList.setDrop_Address(mainResponseJOBJ.getString("dropAddress"));
                dataModel_AllBookingList.setDrop_ContactName(mainResponseJOBJ.getString("dropContactName"));
                dataModel_AllBookingList.setDrop_GPSX(mainResponseJOBJ.getString("dropGPSX"));
                dataModel_AllBookingList.setDrop_GPSY(mainResponseJOBJ.getString("dropGPSY"));
                dataModel_AllBookingList.setDrop_Notes(mainResponseJOBJ.getString("dropNotes"));
                dataModel_AllBookingList.setDrop_Phone(mainResponseJOBJ.getString("dropPhone"));
                dataModel_AllBookingList.setDrop_StreetNo(mainResponseJOBJ.getString("dropStreetNumber"));
                dataModel_AllBookingList.setDrop_StreetName(mainResponseJOBJ.getString("dropStreet"));
                dataModel_AllBookingList.setDrop_Suburb(mainResponseJOBJ.getString("dropSuburb"));
                dataModel_AllBookingList.setCreatedDateTime(mainResponseJOBJ.getString("createdDateTime"));
                dataModel_AllBookingList.setDeliverySpeed(mainResponseJOBJ.getString("deliverySpeed"));
                dataModel_AllBookingList.setDistance(mainResponseJOBJ.getString("distance"));
                dataModel_AllBookingList.setNotes(mainResponseJOBJ.getString("notes"));
                dataModel_AllBookingList.setVehicle(mainResponseJOBJ.getString("vehicle"));
                dataModel_AllBookingList.setSource(mainResponseJOBJ.getString("source"));
                dataModel_AllBookingList.setPackage(mainResponseJOBJ.getString("packageType"));
                dataModel_AllBookingList.setPrice(mainResponseJOBJ.getDouble("courierPrice"));
                dataModel_AllBookingList.setStatus(mainResponseJOBJ.getString("status"));
                dataModel_AllBookingList.setPickupETA(mainResponseJOBJ.getString("pickupETA"));
                dataModel_AllBookingList.setDropETA(mainResponseJOBJ.getString("dropETA"));
                dataModel_AllBookingList.setPickupActual(mainResponseJOBJ.getString("pickupActual"));
                dataModel_AllBookingList.setDropActual(mainResponseJOBJ.getString("dropActual"));
                dataModel_AllBookingList.setPickupPerson(mainResponseJOBJ.getString("pickupPerson"));
                dataModel_AllBookingList.setDropPerson(mainResponseJOBJ.getString("dropPerson"));
                dataModel_AllBookingList.setPickupSignature(mainResponseJOBJ.getString("pickupSignature"));
                dataModel_AllBookingList.setDropSignature(mainResponseJOBJ.getString("dropSignature"));
                dataModel_AllBookingList.setPickupCompanyName(mainResponseJOBJ.getString("pickupCompany"));
                dataModel_AllBookingList.setATL(mainResponseJOBJ.getBoolean("isATL"));
                dataModel_AllBookingList.setATLLeaveAt(mainResponseJOBJ.getString("atlLeaveAt"));
                dataModel_AllBookingList.setATLReceiverName(mainResponseJOBJ.getString("atlReceiverName"));
                dataModel_AllBookingList.setATLDoorCode(mainResponseJOBJ.getString("atlDoorCode"));
                dataModel_AllBookingList.setATLInstructions(mainResponseJOBJ.getString("atlInstructions"));

                try {
                    dataModel_AllBookingList.setDrop_Company(mainResponseJOBJ.getString("dropCompany"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataModel_AllBookingList.setDrop_Company("Not available");
                }

                try {
                    dataModel_AllBookingList.setNoContactDelivery(mainResponseJOBJ.getBoolean("atlNoContact"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataModel_AllBookingList.setNoContactDelivery(false);
                }

                try {
                    dataModel_AllBookingList.setRoutePolyline(mainResponseJOBJ.getString("routePolyline"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    dataModel_AllBookingList.setRunId(mainResponseJOBJ.getInt("runId"));
                    dataModel_AllBookingList.setRunNumber(mainResponseJOBJ.getInt("runNumber"));
                    dataModel_AllBookingList.setDropSuburbCount(mainResponseJOBJ.getInt("dropSuburbCount"));
                    dataModel_AllBookingList.setRunTotalDeliveryCount(mainResponseJOBJ.getInt("runTotalDeliveryCount"));
                    dataModel_AllBookingList.setRunCompletedDeliveryCount(mainResponseJOBJ.getInt("runCompletedDeliveryCount"));
                    dataModel_AllBookingList.setRunType(mainResponseJOBJ.getString("runType"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    dataModel_AllBookingList.setFirstDropAttemptWasLate(mainResponseJOBJ.getString("firstDropAttemptWasLate"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    dataModel_AllBookingList.setAuthorityToLeavePermitted(mainResponseJOBJ.getBoolean("authorityToLeavePermitted"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataModel_AllBookingList.setDistanceFromCurrentLocation(distanceFromCurrentLoc);
                dataModel_AllBookingList.setOrderNumber(orderNumberForMenuLog);

                dataModel_AllBookingList.setDoesAlcoholDeliveries(mainResponseJOBJ.getBoolean("doesAlcoholDeliveries"));
                //	dataModel_AllBookingList.setDoesAlcoholDeliveries(true);
                try {
                    dataModel_AllBookingList.setDropIdentityNumber(mainResponseJOBJ.getString("dropIdentityNumber"));
                    dataModel_AllBookingList.setDropIdentityType(mainResponseJOBJ.getString("dropIdentityType"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    dataModel_AllBookingList.setDropIdentityNumber("");
                    dataModel_AllBookingList.setDropIdentityType("");
                }

                //************ Temp work to process DHL portal booking in normal way *******
//							if (mainResponseJOBJ.getString("source").equals("DHL") && orderNumberForDHL.equals(""))
//								dataModel_AllBookingList.setSource("Deliveries");
                //************ Temp work to process DHL portal booking in normal way *******

                ArrayList<String> piecesArrayActiveList = new ArrayList<String>();
                HashMap<String, Boolean> pieceScannedMap = null;
                JSONArray pieceJsonArray = mainResponseJOBJ.getJSONArray("pieces");
                int pieceArrayLength = pieceJsonArray.length();
                if (pieceArrayLength > 0) {
                    if (!mainResponseJOBJ.getString("status").equals("Accepted") && !mainResponseJOBJ.getString("status").equals("On Route to Pickup")
                            && !mainResponseJOBJ.getString("status").equals("Picked up")) {
                        if (pieceArrayLength > 1)
                            pieceScannedMap = new HashMap<String, Boolean>();
                    }
                    for (int p = 0; p < pieceArrayLength; p++) {
                        piecesArrayActiveList.add(pieceJsonArray.getJSONObject(p).getString("barcode"));

                        if (pieceScannedMap != null) {
                            if (pieceArrayLength > 1) {
                                try {
                                    if (pieceJsonArray.getJSONObject(p).getString("status").equals("Picked up")
                                            || pieceJsonArray.getJSONObject(p).getString("status").equals("Tried to deliver"))
                                        pieceScannedMap.put(pieceJsonArray.getJSONObject(p).getString("barcode"), true);
                                    else
                                        pieceScannedMap.put(pieceJsonArray.getJSONObject(p).getString("barcode"), false);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    pieceScannedMap.put(pieceJsonArray.getJSONObject(p).getString("barcode"), false);
                                }
                            }
                        }
                    }
                }

                dataModel_AllBookingList.setPiecesScannedMap(pieceScannedMap);
                dataModel_AllBookingList.setPiecesArray(piecesArrayActiveList);
                piecesArrayActiveList = null;

                dataModel_AllBookingList.setPickedUpPieceCount(mainResponseJOBJ.getInt("pickedUpPieceCount"));
                dataModel_AllBookingList.setTotalPieceCount(mainResponseJOBJ.getInt("totalPieceCount"));

                dataModel_AllBookingList.setLateReasonId(mainResponseJOBJ.getInt("lateReasonId"));
                try {
                    if (mainResponseJOBJ.has("triedToDeliverReason")
                            && mainResponseJOBJ.getString("status").equals("Tried to deliver")
                            && (mainResponseJOBJ.getString("triedToDeliverReason").equals("PA - Person is under the age of 18")
                            || mainResponseJOBJ.getString("triedToDeliverReason").equals("PI - Person is intoxicated"))) {
                        dataModel_AllBookingList.setTTDReasonForAlcoholDelivery(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //********** To Show Test booking alert *************
                try {
                    if (!mainResponseJOBJ.getString("bookingRefNo").equals("")) {
                        String firstCharOfReferenceNo = mainResponseJOBJ.getString("bookingRefNo").substring(0, 1);
                        if (firstCharOfReferenceNo.equalsIgnoreCase("T")) {
                            String testBookingPickNotes = "This is a test booking to show you how the app works.\nPlease Accept and complete the booking to be eligible for real work.\nNote: You do not need to visit the location in the test booking.\n" + mainResponseJOBJ.getString("pickupNotes");
                            String testBookingDropNotes = "This is a test booking to show you how the app works.\nPlease Accept and complete the booking to be eligible for real work.\nNote: You do not need to visit the location in the test booking.\n" + mainResponseJOBJ.getString("dropNotes");
                            dataModel_AllBookingList.setPick_Notes(testBookingPickNotes);
                            dataModel_AllBookingList.setDrop_Notes(testBookingDropNotes);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                ArrayList<HashMap<String, Object>> arrayOfShipments = new ArrayList<HashMap<String, Object>>();
                for (int k = 0; k < mainResponseJOBJ.getJSONArray("shipments").length(); k++) {
                    HashMap<String, Object> objOFShipments = new HashMap<String, Object>();
                    JSONObject jObjOfShipmentItem = mainResponseJOBJ.getJSONArray("shipments").getJSONObject(k);
                    objOFShipments.put("Category", jObjOfShipmentItem.getString("category"));
                    objOFShipments.put("Quantity", jObjOfShipmentItem.getInt("quantity"));
                    try {
                        objOFShipments.put("LengthCm", jObjOfShipmentItem.getInt("lengthCm"));
                        objOFShipments.put("WidthCm", jObjOfShipmentItem.getInt("widthCm"));
                        objOFShipments.put("HeightCm", jObjOfShipmentItem.getInt("heightCm"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        objOFShipments.put("LengthCm", 0);
                        objOFShipments.put("WidthCm", 0);
                        objOFShipments.put("HeightCm", 0);
                    }
                    try {
                        objOFShipments.put("ItemWeightKg", jObjOfShipmentItem.getDouble("itemWeightKg"));
                        objOFShipments.put("TotalWeightKg", jObjOfShipmentItem.getDouble("totalWeightKg"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        objOFShipments.put("ItemWeightKg", 0);
                        objOFShipments.put("TotalWeightKg", 0);
                    }
                    arrayOfShipments.add(objOFShipments);
                    objOFShipments = null;
                    if (jObjOfShipmentItem.getString("category").equals("Flowers"))
                        dataModel_AllBookingList.setIsCakeAndFlower(1);
                }
                dataModel_AllBookingList.setShipmentsArray(arrayOfShipments);
                arrayOfShipments = null;

                if (LoginZoomToU.activeBookingTab != 1)
                    BookingView.bookingListArray.add(dataModel_AllBookingList);
                else {
                    dhlArrayInActiveBookings.add(dataModel_AllBookingList);

                }

                dataModel_AllBookingList = null;
                mainResponseJOBJ = null;
            }

            if (LoginZoomToU.activeBookingTab == 1) {
                showActiveBookingTabs(3);
                dhlBookingCountBtn.setVisibility(View.VISIBLE);
                searchDHLBookingByAWB.setVisibility(View.VISIBLE);

//							Stream<DHL_SectionInterface> studs = null;
//							for (DHL_SectionInterface dhlModelArray : dhlArrayInActiveBookings) {
//								studs = Stream.of(dhlModelArray);
//							}
//							Map<String, Map<Object, List<DHL_SectionInterface>>> map= studs.collect(Collectors.groupingBy(All_Bookings_DataModels::getRunNumber,Collectors.groupingBy(All_Bookings_DataModels::getRunNumber)));
//							System.out.println(map);//print by name and then location


                Map<Integer, List<DHL_SectionInterface>> map1 = new HashMap<Integer, List<DHL_SectionInterface>>();
                for (DHL_SectionInterface dhlModelArray : dhlArrayInActiveBookings) {
                    int key = ((All_Bookings_DataModels) dhlModelArray).getRunNumber();
                    if (map1.containsKey(key)) {
                        List<DHL_SectionInterface> list = map1.get(key);
                        list.add(dhlModelArray);
                    } else {
                        List<DHL_SectionInterface> list = new ArrayList<DHL_SectionInterface>();
                        list.add(dhlModelArray);
                        map1.put(key, list);
                    }
                }

                for (Map.Entry<Integer, List<DHL_SectionInterface>> entry : map1.entrySet()) {
                    BookingView.bookingListArray.add(new Runs_RouteSectionModel("Route #" + entry.getKey()));
                    List<DHL_SectionInterface> arrayOfRouteSection = entry.getValue();

                    clearStatusArray(acceptedDhlBookingArray);
                    clearStatusArray(pickedUpDhlBookingArray);
                    clearStatusArray(onRouteToPickDhlBookingArray);
                    clearStatusArray(onRouteDropOffDhlBookingArray);
                    clearStatusArray(triedToDeliverDhlBookingArray);
                    clearStatusArray(otherStatusDhlBookingArray);

                    for (DHL_SectionInterface dhlActiveListModel : arrayOfRouteSection) {
                        if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("Accepted")) {
                            acceptedDhlBookingArray.add(dhlActiveListModel);
                        } else if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("Picked up")) {
                            ActiveBookingView.DHL_PICKED_UP_BOOKING_COUNT++;
                            pickedUpDhlBookingArray.add(dhlActiveListModel);
                        } else if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("On Route to Pickup")) {
                            ActiveBookingView.DHL_ON_ROUTE_PICK_BOOKING_COUNT++;
                            onRouteToPickDhlBookingArray.add(dhlActiveListModel);
                        } else if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("On Route to Dropoff")) {
                            ActiveBookingView.DHL_ON_ROUTE_DROP_BOOKING_COUNT++;
                            onRouteDropOffDhlBookingArray.add(dhlActiveListModel);
                        } else if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("Tried to deliver")) {
                            triedToDeliverDhlBookingArray.add(dhlActiveListModel);
                        } else if (((All_Bookings_DataModels) dhlActiveListModel).getStatus().equals("Returning")) {
                            otherStatusDhlBookingArray.add(dhlActiveListModel);
                        }
                    }

                    if (onRouteDropOffDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("On route to dropoff"));
                        for (DHL_SectionInterface dhlModelArray : onRouteDropOffDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }

                    if (pickedUpDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("Picked up"));
                        for (DHL_SectionInterface dhlModelArray : pickedUpDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }

                    if (onRouteToPickDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("On route to pickup"));
                        for (DHL_SectionInterface dhlModelArray : onRouteToPickDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }

                    if (acceptedDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("Accepted"));
                        for (DHL_SectionInterface dhlModelArray : acceptedDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }

                    if (triedToDeliverDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("Tried to deliver"));
                        for (DHL_SectionInterface dhlModelArray : triedToDeliverDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }

                    if (otherStatusDhlBookingArray.size() > 0) {
                        BookingView.bookingListArray.add(new DHL_SectionItemsModel("To Return"));
                        for (DHL_SectionInterface dhlModelArray : otherStatusDhlBookingArray)
                            BookingView.bookingListArray.add((All_Bookings_DataModels) dhlModelArray);
                    }
                }

                //if (CompletedView.endlessCount == 0) {
                if (BookingView.bookingListArray.size() > 0) {
                    activeBookingListAdapter = new ActiveBookingListAdapter(activeBookingContext, R.layout.activebookinglistitem);
                    ActiveBookingView.this.activeBookingListView.setActiveBookingAdapter(activeBookingListAdapter);
                    ActiveBookingView.this.activeBookingListView.setVisibility(View.VISIBLE);
                    noBookingAvailableActiveList.setVisibility(View.GONE);
                } else {
                    if (LoginZoomToU.activeBookingTab == 1) {
                        noBookingAvailableActiveList.setVisibility(View.VISIBLE);
                        activeBookingListView.setVisibility(View.GONE);
                        if (LoginZoomToU.getDHLBookingStr.equals("Active"))
                            noBookingAvailableActiveList.setText("No delivery runs available");
                        else
                            noBookingAvailableActiveList.setText("Bookings not available");
                    }
                }
//							}
//						else
//								activeBookingListView.addNewData(dhlArrayInActiveBookings);
            } else {
//							for (DHL_SectionInterface activeDataModels : activeBookingListArray)
//								BookingView.bookingListArray.add((All_Bookings_DataModels) activeDataModels);
                if (activeDHLBookingCount > 0)
                    showActiveBookingTabs(3);
                else
                    showActiveBookingTabs(2);
                searchDHLBookingByAWB.setVisibility(View.GONE);
                dhlBookingCountBtn.setVisibility(View.GONE);
                //if (CompletedView.endlessCount == 0) {
                if (BookingView.bookingListArray.size() > 0) {
                    activeBookingListAdapter = new ActiveBookingListAdapter(activeBookingContext, R.layout.activebookinglistitem);
                    activeBookingListView.setActiveBookingAdapter(activeBookingListAdapter);
                    activeBookingListView.setVisibility(View.VISIBLE);
                    noBookingAvailableActiveList.setVisibility(View.GONE);
                } else {
                    noBookingAvailableActiveList.setVisibility(View.VISIBLE);
                    activeBookingListView.setVisibility(View.GONE);
                    if (LoginZoomToU.activeBookingTab == 2)
                        noBookingAvailableActiveList.setText("Future bookings not available");
                    else
                        noBookingAvailableActiveList.setText("Active bookings not available");
                }
                //}
                //else
                //activeBookingListView.addNewData(activeBookingListArray);

            }
            dhlArrayInActiveBookings = null;
            onRouteDropOffDhlBookingArray = null;
            acceptedDhlBookingArray = null;
            onRouteToPickDhlBookingArray = null;
            pickedUpDhlBookingArray = null;
            triedToDeliverDhlBookingArray = null;
            responseArrayOfActiveBookingData = null;
            activeBookingResponseStr = null;
            if (activeBookingListAdapter != null)
                activeBookingListAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********** Clear status array while section route data ***********
    private void clearStatusArray(ArrayList<DHL_SectionInterface> dhlStatusBookingArray) {
        if (dhlStatusBookingArray != null)
            if (dhlStatusBookingArray.size() > 0)
                dhlStatusBookingArray.clear();
    }

    private void showActiveBookingTabs(int divideActiveBookingTabs) {
        if (divideActiveBookingTabs == 3) {
            dhlBookingActiveList.setVisibility(View.VISIBLE);
            active_dhl_rl.setVisibility(View.VISIBLE);
            setViewParamOfActiveTabs(todayBookingBtnActiveList, active_today_rl,0.33f, "Today");
            setViewParamOfActiveTabs(tomorrowBookingBtnActiveList,active_complete_rl, 0.34f, "Future");
            setViewParamOfActiveTabs(dhlBookingActiveList,active_dhl_rl, 0.33f, "Delivery Runs");
        }else {
            dhlBookingActiveList.setVisibility(View.GONE);
            active_dhl_rl.setVisibility(View.GONE);
            setViewParamOfActiveTabs(todayBookingBtnActiveList, active_today_rl,0.5f, "Today");
            setViewParamOfActiveTabs(tomorrowBookingBtnActiveList, active_complete_rl,0.5f, "Future");
        }
    }

    private void setViewParamOfActiveTabs(TextView activeBookingTabView,LinearLayout tab_ll ,float weight, String tabsTxtStr){
        LinearLayout.LayoutParams paramActiveBookinTabs = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, weight);
        paramActiveBookinTabs.setMargins(1, 1, 1, 1);
        tab_ll.setGravity(Gravity.CENTER);
        activeBookingTabView.setText(tabsTxtStr);
        activeBookingTabView.setTextSize(14f);
        tab_ll.setLayoutParams(paramActiveBookinTabs);
        paramActiveBookinTabs = null;
    }

    // ***********  Select image from camera to upload package image ***********
    private void selectImage() {
        try {
            if ((int) Build.VERSION.SDK_INT >= 23) {
                String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (/*ContextCompat.checkSelfPermission(activeBookingContext, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(activeBookingContext, permission[1]) == PackageManager.PERMISSION_DENIED ||*/
                        ContextCompat.checkSelfPermission(activeBookingContext, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(activeBookingContext);

                    enterFieldDialog.setCancelable(true);
                    enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    enterFieldDialog.setContentView(R.layout.permission_dailog);

                    Window window = enterFieldDialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    WindowManager.LayoutParams wlp = window.getAttributes();

                    wlp.gravity = Gravity.CENTER;
                    wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    window.setAttributes(wlp);
                    TextView enterFieldDialogHEader = (TextView) enterFieldDialog.findViewById(R.id.titleDialog);

                    enterFieldDialogHEader.setText("Permission required!");

                    TextView enterFieldDialogMsg = (TextView) enterFieldDialog.findViewById(R.id.dialogMessageText);

                    enterFieldDialogMsg.setText("Z2U for couriers app need to access your images for picture post.");

                    Button enterFieldDialogDoneBtn = (Button) enterFieldDialog.findViewById(R.id.dialogDoneBtn);

                    enterFieldDialogDoneBtn.setText("Got it!");

                    enterFieldDialogDoneBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ActivityCompat.requestPermissions((Activity) activeBookingContext,
                                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();
                } else
                    openCamera();
            } else
                openCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openCamera() {
        try {
            PushReceiver.isCameraOpen = true;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(activeBookingContext.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                    LoginZoomToU.isImgFromInternalStorage = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LoginZoomToU.isImgFromInternalStorage = true;
                    Toast.makeText(activeBookingContext, "Image file at internal ", Toast.LENGTH_LONG).show();
                }
                if (LoginZoomToU.isImgFromInternalStorage) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyContentProviderAtLocal.CONTENT_URI);
                    bookingView.startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                } else {
                    //************* Old code ***********
                    //	takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

                    Uri photoURI = FileProvider.getUriForFile(
                            activeBookingContext,
                            activeBookingContext.getApplicationContext()
                                    .getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    bookingView.startActivityForResult(takePictureIntent, ActiveBookingDetail_New.TAKE_PHOTO);
                }
            }
            takePictureIntent = null;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activeBookingContext, "Error while opening camera", Toast.LENGTH_LONG).show();
        }
    }

    private void UpdatePickUpETAAsyncTask(){
        final String[] requestPickResponseStr = {null};
        final boolean[] responseDataUpdateTime = {false};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    inItActiveBookingProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Pickup")) {
                        requestPickResponseStr[0] = webServiceHandler.updatePickUpETAForBookingID(uploadDateBookingStrActiveList, String.valueOf(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()));
                    } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                            || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Delivery Attempted"))
                        requestPickResponseStr[0] = webServiceHandler.updateDropOffETAForBookingID(uploadDateBookingStrActiveList, String.valueOf(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()));
                    else {
                        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("Temando")) {
                            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Accepted"))
                                requestPickResponseStr[0] = webServiceHandler.updatePickUpETAForBookingID(uploadDateBookingStrActiveList, String.valueOf(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()));
                            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Picked up"))
                                requestPickResponseStr[0] = webServiceHandler.updateDropOffETAForBookingID(uploadDateBookingStrActiveList, String.valueOf(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()));
                        }
                    }
                    JSONObject jOBJForRequestPick = new JSONObject(requestPickResponseStr[0]);
                    responseDataUpdateTime[0] = jOBJForRequestPick.getBoolean("success");
                    jOBJForRequestPick = null;
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    dismissActiveBookingProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (etaDialogInactiveBookingList != null)
                        if (etaDialogInactiveBookingList.isShowing())
                            etaDialogInactiveBookingList.dismiss();

                    if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                            || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Delivery Attempted")) {
                        if (!requestPickResponseStr.equals("")) {
                            if (responseDataUpdateTime[0]) {
                                refreshActiveBookingList();
                            }
                        } else {
                            DialogActivity.alertDialogView(activeBookingContext, "Error !", "Dropoff time not updated! try again.");
                            requestPickResponseStr[0] = null;
                        }
                    } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Pickup")) {
                        if (!requestPickResponseStr.equals("")) {
                            if (responseDataUpdateTime[0]) {
                                refreshActiveBookingList();
                            }
                        } else {
                            DialogActivity.alertDialogView(activeBookingContext, "Error !", "Pick up time not updated! try again.");
                            requestPickResponseStr[0] = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (etaDialogInactiveBookingList != null)
                        if (etaDialogInactiveBookingList.isShowing())
                            etaDialogInactiveBookingList.dismiss();

                    requestPickResponseStr[0] = null;
                    DialogActivity.alertDialogView(activeBookingContext, "Server Error !", "Something went wrong, Please try again later.");
                }
            }
        }.execute();
    }


    private void PackageUploadWithETAAsyncTask(){
        final String[] requestPickResponseStr = {null};
        final JSONObject[] jOBJForRequestPick = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    inItActiveBookingProgressDialog();
                    ActiveBookingView.getCurrentLocation(activeBookingContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (isActionBarButtonTaped) {
                        isActionBarButtonTaped = false;
                        Map<String, Object> mapObject = new HashMap<String, Object>();
                        mapObject.put("bookingIds", ActiveBookingView.arrayOfBookingId);
                        mapObject.put("pickupPerson", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName());
                        mapObject.put("signeesPosition", 0);
                        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                            String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                            mapObject.put("location", currentLocation);
                        }
                        ObjectMapper objectMapper = new ObjectMapper();
                        String bookingIdObj = objectMapper.writeValueAsString(mapObject);
                        requestPickResponseStr[0] = webServiceHandler.pickUpMultipleBookings(bookingIdObj);
                        objectMapper = null;
                        mapObject = null;
                    } else {
                        requestPickResponseStr[0] = webServiceHandler.requestForPickPersonName(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName(),
                                "", 5, String.valueOf(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()),
                                uploadDateBookingStrActiveList);
                    }
                    jOBJForRequestPick[0] = new JSONObject(requestPickResponseStr[0]);
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    if (jOBJForRequestPick[0].getBoolean("success") == true) {
                        //*********** Sent battery info to admin on firebase ***************
                        if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                            new Service_CheckBatteryLevel(activeBookingContext);
                        if (alertDialogInActiveList != null)
                            alertDialogInActiveList = null;
                        alertDialogInActiveList = new Dialog(activeBookingContext);
                        alertDialogInActiveList.setCancelable(false);
                        alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        alertDialogInActiveList.setContentView(R.layout.dialogview);

                        Window window = alertDialogInActiveList.getWindow();
                        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                        WindowManager.LayoutParams wlp = window.getAttributes();

                        wlp.gravity = Gravity.CENTER;
                        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        window.setAttributes(wlp);

                        TextView enterFieldDialogHEader = (TextView) alertDialogInActiveList.findViewById(R.id.titleDialog);

                        enterFieldDialogHEader.setText("Congrats!");

                        TextView enterFieldDialogMsg = (TextView) alertDialogInActiveList.findViewById(R.id.dialogMessageText);

                        enterFieldDialogMsg.setText("Picked up successfully.");

                        Button enterFieldDialogDoneBtn = (Button) alertDialogInActiveList.findViewById(R.id.dialogDoneBtn);
                        enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                alertDialogInActiveList.dismiss();
                                alertDialogInActiveList = null;
                                requestPickResponseStr[0] = null;
                                refreshActiveBookingList();
                            }
                        });
                        alertDialogInActiveList.show();
                    } else
                        DialogActivity.alertDialogView(activeBookingContext, "Alert!", jOBJForRequestPick[0].getString("Message"));
                } catch (Exception e) {
                    e.printStackTrace();
                    requestPickResponseStr[0]= null;
                    DialogActivity.alertDialogView(activeBookingContext, "Server Error !", "Something went wrong, Please try again later.");
                } finally {
                    try {
                        dismissActiveBookingProgressDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }


    private void OnRouteAsyncTask(){
      final String[] requestOnRouteResponseStr = {null};
      final boolean[] responseDataOnRoute = {false};
      final JSONObject[] jOBJForRequestOnRoute = new JSONObject[1];
      new MyAsyncTasks(){
          @Override
          public void onPreExecute() {
              try {
                  inItActiveBookingProgressDialog();
                  ActiveBookingView.getCurrentLocation(activeBookingContext);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

          @Override
          public void doInBackground() {
              try {
                  WebserviceHandler webServiceHandler = new WebserviceHandler();
                  //    Functional_Utility.sendLocationToServer();
                  if (isActionBarButtonTaped == true) {
                      if (arrayOfBookingId.size() > 0) {
                          Map<String, Object> mapObject = new HashMap<String, Object>();
                          mapObject.put("bookingIds", arrayOfBookingId);
                          if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                                  !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                              String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                              mapObject.put("location", currentLocation);
                          }
                          ObjectMapper objectMapper = new ObjectMapper();
                          String bookingIds = objectMapper.writeValueAsString(mapObject);
                          requestOnRouteResponseStr[0] = webServiceHandler.OnRoutePickupMultiple(bookingIds);
                          mapObject = null;
                          objectMapper = null;
                          arrayOfBookingId.clear();
                          //Log.e("", "-----------------------------"+bookingIds);
                      }
                  } else {
                      if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Accepted")) {
                          requestOnRouteResponseStr[0] = webServiceHandler.onRoutePickUp(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                      } else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Picked up"))
                          requestOnRouteResponseStr[0] = webServiceHandler.onRouteDropOff(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                  }
                  jOBJForRequestOnRoute[0] = new JSONObject(requestOnRouteResponseStr[0]);
                  responseDataOnRoute[0] = jOBJForRequestOnRoute[0].getBoolean("success");
                  webServiceHandler = null;
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

          @Override
          public void onPostExecute() {
              try {
                  dismissActiveBookingProgressDialog();
              } catch (Exception e) {
                  e.printStackTrace();
              }

              isActionBarButtonTaped = false;
              try {
                  if (!responseDataOnRoute[0]) {
                      requestOnRouteResponseStr[0] = null;
                      try {
                          DialogActivity.alertDialogView(activeBookingContext, "Information!", jOBJForRequestOnRoute[0].getString("message"));
                      } catch (Exception e) {
                          e.printStackTrace();
                      }
                  }
                  refreshActiveBookingList();
                  jOBJForRequestOnRoute[0] = null;
              } catch (Exception e) {
                  e.printStackTrace();
                  requestOnRouteResponseStr[0] = null;
                  DialogActivity.alertDialogView(activeBookingContext, "Error !", "Something went wrong, Please try again later.");
              }
          }
      }.execute();
  }



    //***************  Refresh list on status change *************
    public void refreshActiveBookingList() {
        CompletedView.endlessCount = 0;
        awbNumber = "";
        clearArrayOfActiveList();
        clearSubArrayOfActiveList();
        if (LoginZoomToU.activeBookingTab == 1)
            callAsyncTaskForGetActiveBooking("", LoginZoomToU.getDHLBookingStr);
        else
            callAsyncTaskForGetActiveBooking("", "");
    }

    private void  RequestForDropOffTemendoAsyncTask(){
      final String[] requestDropResponseStr = {null};
      final boolean[] responseDataRequestDrop = {false};

      new MyAsyncTasks(){
          @Override
          public void onPreExecute() {
              try {
                  inItActiveBookingProgressDialog();
                  ActiveBookingView.getCurrentLocation(activeBookingContext);
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

          @Override
          public void doInBackground() {
              try {
                  // Functional_Utility.sendLocationToServer();
                  WebserviceHandler webServiceHandler = new WebserviceHandler();
                  try {
                      requestDropResponseStr[0] = webServiceHandler.requestForDropOffPersonName(userNameInActiveBookingList, "", 5,
                              String.valueOf((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId()));
                      bookingView.showActiveBookingCount();
                      JSONObject jOBJForRequestPick = new JSONObject(requestDropResponseStr[0]);
                      responseDataRequestDrop[0] = jOBJForRequestPick.getBoolean("success");
                      jOBJForRequestPick = null;
                      if (responseDataRequestDrop[0] && isDropOffFromATL && photo != null) {
                          try {
                              ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                              Intent bgUploadImage = new Intent(activeBookingContext, BG_ImageUploadToServer.class);
                              bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                              bgUploadImage.putExtra("isActionBarPickup", false);
                              bgUploadImage.putExtra("isDropOffFromATL", true);
                              bgUploadImage.putExtra("isDropoffBooking", false);
                              activeBookingContext.startService(bgUploadImage);
                              isDropOffFromATL = false;
                              ActiveBookingDetail_New.bookingIdActiveBooking = "";
                              bgUploadImage = null;
                          } catch (Exception e1) {
                              e1.printStackTrace();
                          }
                      }
                      webServiceHandler = null;
                  } catch (Exception e) {
                      e.printStackTrace();
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              }
          }

          @Override
          public void onPostExecute() {
              try {
                  if (responseDataRequestDrop[0] == true) {
                      dismissActiveBookingProgressDialog();
                      dismissATLDialog();
                      WebserviceHandler.reCountActiveBookings(1, 2);
                      new LoadChatBookingArray(activeBookingContext, 0);
                      if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL"))
                          LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                      clearArrayOfActiveList();
                      refreshActiveBookingList();
                  } else
                      alertDialogToRefreshList("Error!", "Package not uploaded, Please try again.");
              } catch (Exception e) {
                  e.printStackTrace();
                  alertDialogToRefreshList("Server Error!!", "Something went wrong. Please try again.");
              }
              requestDropResponseStr[0] = null;
          }
      }.execute();
  }



    private void DropOffFromATLAsyncTask(){
        final String[] requestDropResponseStr = {null};

        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                inItActiveBookingProgressDialog();
                ActiveBookingView.getCurrentLocation(activeBookingContext);
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    Map<String, Object> mapObject = new HashMap<String, Object>();
                    mapObject.put("bookingId", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                    mapObject.put("recipientName", userNameInActiveBookingList);
                    mapObject.put("pieceBarcodes", Functional_Utility.getScannedPieceArray(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPiecesArray()));
                    mapObject.put("notes", "Write some note here");
                    mapObject.put("signeesPosition", 5);
                    mapObject.put("forceIncompleteDropOff", false);
                    if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnLongitude.equals("") &&
                            !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && !LoginZoomToU.getCurrentLocatnLongitude.equals("0.0")) {
                        String currentLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
                        mapObject.put("latitude", LoginZoomToU.getCurrentLocatnlatitude);
                        mapObject.put("longitude", LoginZoomToU.getCurrentLocatnLongitude);
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    String requestDropoffParams = objectMapper.writeValueAsString(mapObject);
                    ;
                    requestDropResponseStr[0] = webServiceHandler.dropDeliveryUsingBarcode(requestDropoffParams);
                    if (LoginZoomToU.isLoginSuccess == 0) {
                        if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                            if (isDropOffFromATL && photo != null) {
                                try {
                                    ActiveBookingDetail_New.bookingIdActiveBooking = String.valueOf((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                                    Intent bgUploadImage = new Intent(activeBookingContext, BG_ImageUploadToServer.class);
                                    bgUploadImage.putExtra("bookingIdStrForUploadImg", ActiveBookingDetail_New.bookingIdActiveBooking);
                                    bgUploadImage.putExtra("isActionBarPickup", false);
                                    bgUploadImage.putExtra("isDropOffFromATL", true);
                                    bgUploadImage.putExtra("isDropoffBooking", false);
                                    activeBookingContext.startService(bgUploadImage);
                                    isDropOffFromATL = false;
                                    ActiveBookingDetail_New.bookingIdActiveBooking = "";
                                    bgUploadImage = null;
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    switch (LoginZoomToU.isLoginSuccess) {
                        case 0:
                            try {
                                if (new JSONObject(requestDropResponseStr[0]).getBoolean("isDropDeliveryComplete")) {
                                    WebserviceHandler.reCountActiveBookings(1, 2);
                                    try {
                                        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals("")
                                                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals("null")
                                                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals(null)) {
                                            if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime(),
                                                    ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate())
                                                    && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                                                lateDeliveryReasonView(false);
                                            }
                                        } else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime())
                                                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                                            lateDeliveryReasonView(false);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime())
                                                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                                            lateDeliveryReasonView(false);
                                        }
                                    }
                                    refreshBookingList();
                                } else
                                    alertDialogToRefreshList("Error!", "Package not uploaded, Please try again.");
                            } catch (Exception e) {
                                alertDialogToRefreshList("Error!", "Package not uploaded, Please try again.");
                            }
                            break;

                        case 1:
                            DialogActivity.alertDialogView(activeBookingContext, "No network!", "No network connection, Please check your connection and try again");
                            break;
                        default:
                            alertDialogToRefreshList("Server Error!", "Something went wrong. Please try again.");
                            break;
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                    alertDialogToRefreshList("Server Error!", "Something went wrong. Please try again.");
                    requestDropResponseStr[0] = null;
                } finally {
                    LoginZoomToU.isLoginSuccess = 0;
                    requestDropResponseStr[0] = null;
                    dismissActiveBookingProgressDialog();
                }
            }
        }.execute();
    }


    private void lateDeliveryReasonView(boolean isttd) {
        Intent callDialogReasonLate = new Intent(activeBookingContext, DialogReasonForLateDelivery.class);
        callDialogReasonLate.putExtra("ReasonLateBookingID", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
        callDialogReasonLate.putExtra("BookingTypeSource", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource());
        callDialogReasonLate.putExtra("IsFromTtd", isttd);
        activeBookingContext.startActivity(callDialogReasonLate);
        callDialogReasonLate = null;
    }

    private void refreshBookingList() {
        dismissATLDialog();
        new LoadChatBookingArray(activeBookingContext, 0);
        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getSource().equals("DHL"))
            LoadChatBookingArray.updateRecipientAdminChatAsCloseForDHL(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
        clearArrayOfActiveList();
        refreshActiveBookingList();
        bookingView.showActiveBookingCount();
    }

    private void alertDialogToRefreshList(String titleSTR, String msgSTr) {
        dismissATLDialog();

        try {
            if (alertDialogInActiveList != null)
                if (alertDialogInActiveList.isShowing())
                    alertDialogInActiveList.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialogInActiveList != null)
            alertDialogInActiveList = null;
        alertDialogInActiveList = new Dialog(activeBookingContext);
        alertDialogInActiveList.setCancelable(false);
        alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogInActiveList.setContentView(R.layout.dialogview);
        Window window = alertDialogInActiveList.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView enterFieldDialogHEader = (TextView) alertDialogInActiveList.findViewById(R.id.titleDialog);

        enterFieldDialogHEader.setText(titleSTR);

        TextView enterFieldDialogMsg = (TextView) alertDialogInActiveList.findViewById(R.id.dialogMessageText);

        enterFieldDialogMsg.setText(msgSTr);

        Button enterFieldDialogDoneBtn = (Button) alertDialogInActiveList.findViewById(R.id.dialogDoneBtn);
        enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogInActiveList.dismiss();
                alertDialogInActiveList = null;
                refreshActiveBookingList();
            }
        });
        alertDialogInActiveList.show();
    }

    private void AttemptDeliveryAsyncTask(){
       final JSONObject[] jOBJResponseForAttemptDelivery = new JSONObject[1];
       new MyAsyncTasks(){
           @Override
           public void onPreExecute() {
               try {
                   inItActiveBookingProgressDialog();
                   ActiveBookingView.getCurrentLocation(activeBookingContext);
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void doInBackground() {
               try {
                   Functional_Utility.sendLocationToServer();
                   WebserviceHandler webServiceHandler = new WebserviceHandler();
                   try {
                       String responseDataForAttemptDeliveryStr = "";
                       if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("On Route to Dropoff") || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Tried to deliver")
                               || ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Delivery Attempted"))
                           if (LoginZoomToU.getCurrentLocatnlatitude.equals("0.0") && LoginZoomToU.getCurrentLocatnLongitude.equals("0.0"))
                               responseDataForAttemptDeliveryStr = webServiceHandler.attemptDelivery(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId(), deliveryAttemptNoteTxt, positionForTriedToDeliverReason, "");
                           else
                               responseDataForAttemptDeliveryStr = webServiceHandler.attemptDelivery(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId(), deliveryAttemptNoteTxt, positionForTriedToDeliverReason, LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude);
                       jOBJResponseForAttemptDelivery[0] = new JSONObject(responseDataForAttemptDeliveryStr);
                       webServiceHandler = null;
                   } catch (Exception e) {
                       e.printStackTrace();
                   }
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }

           @Override
           public void onPostExecute() {
               try {
                   dismissActiveBookingProgressDialog();
                   if (jOBJResponseForAttemptDelivery[0].getBoolean("success") == true) {
                       //*********** Sent battery info to admin on firebase ***************
                       if (!LoginZoomToU.courierID.equals("") && ChatViewBookingScreen.mFirebaseRef != null)
                           new Service_CheckBatteryLevel(activeBookingContext);
                       //*********** Sent battery info to admin on firebase ***************//
                       Toast.makeText(activeBookingContext, "Delivery attempted successfully", Toast.LENGTH_LONG).show();

                       try {
                           if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals("")
                                   && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals("null")
                                   && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate().equals(null)) {
                               if (LoginZoomToU.checkInternetwithfunctionality.checkIsFirstDropAttemptWasLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime(),
                                       ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getFirstDropAttemptWasLate())
                                       && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                                   lateDeliveryReasonView(true);
                               }
                           } else if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime())
                                   && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                               lateDeliveryReasonView(true);
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                           if (LoginZoomToU.checkInternetwithfunctionality.checkIsDeliveryDropLate(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDropDateTime())
                                   && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getLateReasonId() == 0) {
                               lateDeliveryReasonView(true);
                           }
                       }

                       refreshActiveBookingList();
                   }
                   jOBJResponseForAttemptDelivery[0] = null;
               } catch (Exception e) {
                   e.printStackTrace();
                   DialogActivity.alertDialogView(activeBookingContext, "Sorry!", "Something went wrong, Please try again later");
               }
           }
       }.execute();
   }



    // ************** Getting current location to attempt delivery
    public static void getCurrentLocation(Context currentContext) {
        try {
            //************* For more accurate location using fused location *********
            MyLocation myLocation = new MyLocation();
            MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
                @Override
                public void gotLocation(Location location) {
                    try {
                        LoginZoomToU.getCurrentLocatnlatitude = String.valueOf(location.getLatitude());
                        LoginZoomToU.getCurrentLocatnLongitude = String.valueOf(location.getLongitude());
                    } catch (Exception e) {
                        e.printStackTrace();
                        LoginZoomToU.getCurrentLocatnlatitude = "0.0";
                        LoginZoomToU.getCurrentLocatnLongitude = "0.0";
                    }
                }
            };
            myLocation.getLocation(currentContext, locationResult);
            myLocation = null;
            //	**************************************************************** */
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

//	public void switchToBookDelivery(int bookingViewSelection) {
//		BookingView.bookingViewSelection = bookingViewSelection;
//		ConfirmPickUpForUserName.isDropOffSuccessfull = 11;
//		Intent callCompleteBookingfragment = new Intent(activeBookingContext, SlideMenuZoom2u.class);
//		activeBookingContext.startActivity(callCompleteBookingfragment);
//		((Activity) activeBookingContext).finish();
//		callCompleteBookingfragment = null;
//	}

    // ************** Setting background blur when showing alert window
    public static void setBackgroundBlur(RelativeLayout mainLayout, float alpha) {
        AlphaAnimation alphaUp = new AlphaAnimation(alpha, alpha);
        alphaUp.setFillAfter(true);
        mainLayout.startAnimation(alphaUp);
        alphaUp = null;
    }

    // *************  Alert for send job back to other couriers
    void dispatchWindow() {
        if (DialogActivity.enterFieldDialog != null)
            DialogActivity.enterFieldDialog = null;
        DialogActivity.enterFieldDialog = new Dialog(activeBookingContext);
        DialogActivity.enterFieldDialog.setCancelable(false);
        DialogActivity.enterFieldDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        DialogActivity.enterFieldDialog.setContentView(R.layout.logoutwindow);

        Window window = DialogActivity.enterFieldDialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView dispatchDialogFieldMsg = (TextView) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowMessageText);

        dispatchDialogFieldMsg.setText("Are you sure, you want to request for a new driver for this booking?");

        Button dispatchDialogFieldCancelBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowCancelBtn);

        dispatchDialogFieldCancelBtn.setText("No");
        dispatchDialogFieldCancelBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity.enterFieldDialog.dismiss();
                DialogActivity.enterFieldDialog = null;
            }
        });
        Button dispatchDialogFieldLogoutBtn = (Button) DialogActivity.enterFieldDialog.findViewById(R.id.logoutWindowLogoutBtn);

        dispatchDialogFieldLogoutBtn.setText("Yes");
        dispatchDialogFieldLogoutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogActivity.enterFieldDialog.dismiss();
                DialogActivity.enterFieldDialog = null;
                if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
                    /*new DispatchToOtherCourierAsyncTask().execute();*/
                    DispatchToOtherCourierAsyncTask();
                else
                    DialogActivity.alertDialogView(activeBookingContext, "No Network!", "No network connection, Please try again later.");
            }
        });

        DialogActivity.enterFieldDialog.show();
    }

    private void DispatchToOtherCourierAsyncTask(){
        final String[] requestDispatchToOtherCourierMsgStr = {""};
        final boolean[] responseDataRequestDrop = {false};
        new MyAsyncTasks(){
            @Override
            public void onPreExecute() {
                try {
                    inItActiveBookingProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInBackground() {
                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    try {
                        String requestDropResponseStr = webServiceHandler.dispatchToOtherCourier((Integer) ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getBookingId());
                        JSONObject jOBJForRequestPick = new JSONObject(requestDropResponseStr);
                        responseDataRequestDrop[0] = jOBJForRequestPick.getBoolean("success");
                        try {
                            requestDispatchToOtherCourierMsgStr[0] = jOBJForRequestPick.getString("message");
                        } catch (Exception e) {
                            requestDispatchToOtherCourierMsgStr[0] = "";
                        }
                        jOBJForRequestPick = null;
                        webServiceHandler = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPostExecute() {
                try {
                    dismissActiveBookingProgressDialog();

                    if (requestDispatchToOtherCourierMsgStr[0].equals("")) {
                        if (responseDataRequestDrop[0] == true) {
                            WebserviceHandler.reCountActiveBookings(1, 2);
                            bookingView.showActiveBookingCount();

                            new LoadChatBookingArray(activeBookingContext, 0);
                            dialogInActiveDetail(activeBookingContext, "Sent!", "Booking sent to the other drivers successfully.");
                        } else
                            DialogActivity.alertDialogView(activeBookingContext, "Sorry!", "Booking can't be sent to the other drivers, Please try again");
                    } else
                        dialogInActiveDetail(activeBookingContext, "Alert!", requestDispatchToOtherCourierMsgStr[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogActivity.alertDialogView(activeBookingContext, "Sorry!", "Something went wrong, Please try again later");
                }
            }
        }.execute();
    }




    // ********** Alert dialog to refresh active booking list
    void dialogInActiveDetail(Context con, String titleTxt, String msgTxt) {
        if (alertDialogInActiveList != null)
            alertDialogInActiveList = null;
        alertDialogInActiveList = new Dialog(con);
        alertDialogInActiveList.setCancelable(false);
        alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogInActiveList.setContentView(R.layout.dialogview);

        Window window = alertDialogInActiveList.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView enterFieldDialogHEader = (TextView) alertDialogInActiveList.findViewById(R.id.titleDialog);

        enterFieldDialogHEader.setText(titleTxt);

        TextView enterFieldDialogMsg = (TextView) alertDialogInActiveList.findViewById(R.id.dialogMessageText);

        enterFieldDialogMsg.setText(msgTxt);

        Button enterFieldDialogDoneBtn = (Button) alertDialogInActiveList.findViewById(R.id.dialogDoneBtn);

        enterFieldDialogDoneBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialogInActiveList.dismiss();
                alertDialogInActiveList = null;
                refreshActiveBookingList();
            }
        });

        alertDialogInActiveList.show();
    }

    Dialog searchDHLDialog;

    /* *************  Alert for search DHL bookings and
     ************** show Direct direction functionality for On route to pick and Drop************* */
    void searchDHLBookings() {
        subBookingView.setVisibility(View.VISIBLE);
        if (searchDHLDialog != null)
            if (searchDHLDialog.isShowing())
                searchDHLDialog.dismiss();

        if (searchDHLDialog != null)
            searchDHLDialog = null;
        searchDHLDialog = new Dialog(activeBookingContext);
        searchDHLDialog.setCancelable(false);
        searchDHLDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDHLDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchDHLDialog.setContentView(R.layout.dhlsearchdialog);

        Window window = searchDHLDialog.getWindow();
        window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView dispatchDialogFieldMsg = (TextView) searchDHLDialog.findViewById(R.id.dialogHeaderDHLSearchBookingText);


        final EditText edtSearchTxt = (EditText) searchDHLDialog.findViewById(R.id.edtSearchDHLBooking);


        final TextView dhlSearchValidationTxt = (TextView) searchDHLDialog.findViewById(R.id.dhlSearchValidationTxt);


        Button cancelDhlSearchBtn = (Button) searchDHLDialog.findViewById(R.id.cancelDhlSearchBtn);


        final Button searchDhlBtn = (Button) searchDHLDialog.findViewById(R.id.searchDhlBtn);


        dispatchDialogFieldMsg.setText("Search booking");
        cancelDhlSearchBtn.setText("Cancel");
        searchDhlBtn.setText("Search");
        edtSearchTxt.setVisibility(View.VISIBLE);
        dhlSearchValidationTxt.setVisibility(View.GONE);

        cancelDhlSearchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDHLDialog.dismiss();
                searchDHLDialog = null;
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSearchTxt.getWindowToken(), 0);
                subBookingView.setVisibility(View.GONE);
            }
        });

        searchDhlBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtSearchTxt.getText().toString().equals("")) {
                    dhlSearchValidationTxt.setVisibility(View.VISIBLE);
                } else {
                    dhlSearchValidationTxt.setVisibility(View.GONE);
                    clearSubArrayOfActiveList();
                    CompletedView.endlessCount = 0;
                    callAsyncTaskForGetActiveBooking(edtSearchTxt.getText().toString(), LoginZoomToU.getDHLBookingStr);
                    awbNumber = "";

                    subBookingView.setVisibility(View.GONE);
                    searchDHLDialog.dismiss();
                    searchDHLDialog = null;
                }
                LoginZoomToU.imm.hideSoftInputFromWindow(edtSearchTxt.getWindowToken(), 0);

            }
        });

        searchDHLDialog.show();
    }

    //******** Call async task request for On route to pick and Drop ************
    private void callOnRouteTask() {
        if (LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            //new OnRouteAsyncTask().execute();
            OnRouteAsyncTask();
        else
            DialogActivity.alertDialogView(activeBookingContext, "No Network !", "No Network connection, Please try again later.");
    }

    //******** Show driving direction on Google map ************
    private void showDrivingDirection() {
        Intent mapIntent;
        if (!LoginZoomToU.getCurrentLocatnlatitude.equals("") && !LoginZoomToU.getCurrentLocatnlatitude.equals("0.0")) {
            String currLocation = LoginZoomToU.getCurrentLocatnlatitude + "," + LoginZoomToU.getCurrentLocatnLongitude;
            String desLocation = "";
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Accepted"))
                desLocation = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_GPSX() + "," + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_GPSY();
            else if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Picked up"))
                desLocation = ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_GPSX() + "," + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_GPSY();

            // "d" means driving car, "w" means walking "r" means by bus
            mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?"
                    + "saddr=" + currLocation + "&daddr=" + desLocation + "&dirflg=d"));
            mapIntent.setPackage("com.google.android.apps.maps");
            mapIntent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        } else {
            Uri gmmIntentUri;
            if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getStatus().equals("Accepted"))
                gmmIntentUri = Uri.parse("geo:0,0?q=" + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_Address());
            else
                gmmIntentUri = Uri.parse("geo:0,0?q=" + ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_Address());
            mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
        }

        if (mapIntent.resolveActivity(activeBookingContext.getPackageManager()) != null)
            activeBookingContext.startActivity(mapIntent);
        mapIntent = null;
    }

    public Dialog atlDialog;
    private View bgBlurDefaultView;

    // *************  Alert for search DHL bookings **************
    public void atlDialogAlert() {
        subBookingView.setVisibility(View.VISIBLE);
        dismissATLDialog();
        isDropOffFromATL = false;
        if (atlDialog != null)
            atlDialog = null;
        atlDialog = new Dialog(activeBookingContext);
        atlDialog.setCancelable(false);
        atlDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        atlDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        atlDialog.setContentView(R.layout.dialog_safe_delivery_avail_atl);

        Window window = atlDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        bgBlurDefaultView = atlDialog.findViewById(R.id.bgBlurDefaultView);
        bgBlurDefaultView.setVisibility(View.GONE);

        TextView safeDelDialogTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogTitleTxt);


        TextView safeDelDialogMsgTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogMsgTxt);


        TextView safeDelDialogLocationTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogLocationTitleTxt);

        TextView safeDelDialogLocationValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogLocationValueTxt);

        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLLeaveAt().equals("")
                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLLeaveAt().equals("null")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLLeaveAt() != null)
            safeDelDialogLocationValueTxt.setText(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLLeaveAt());
        else
            safeDelDialogLocationValueTxt.setText("-NA-");

        TextView safeDelDialogDoorCodeTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogDoorCodeTitleTxt);

        TextView safeDelDialogDoorCodeValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogDoorCodeValueTxt);

        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLDoorCode().equals("")
                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLDoorCode().equals("null")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLDoorCode() != null)
            safeDelDialogDoorCodeValueTxt.setText(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLDoorCode());
        else
            safeDelDialogDoorCodeValueTxt.setText("-NA-");

        TextView safeDelDialogRecipientNameTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogRecipientNameTitleTxt);

        TextView safeDelDialogRecipientValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogRecipientValueTxt);

        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName().equals("")
                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName().equals("null")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName() != null)
            safeDelDialogRecipientValueTxt.setText(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLReceiverName());
        else
            safeDelDialogRecipientValueTxt.setText("-NA-");

        TextView safeDelDialogInstructionTitleTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogInstructionTitleTxt);

        TextView safeDelDialogInstructionValueTxt = (TextView) atlDialog.findViewById(R.id.safeDelDialogInstructionValueTxt);

        if (!((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLInstructions().equals("")
                && !((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLInstructions().equals("null")
                && ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLInstructions() != null)
            safeDelDialogInstructionValueTxt.setText(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getATLInstructions());
        else
            safeDelDialogInstructionValueTxt.setText("-NA-");

        Button safeDelDialogSafeLocationBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogSafeLocationBtn);

        safeDelDialogSafeLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogWhatIsSafe();
            }
        });

        Button safeDelDialogDeliveryAttemptBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogDeliveryAttemptBtn);

        safeDelDialogDeliveryAttemptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissATLDialog();
                isDropOffFromATL = false;
                if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPick_ContactName().equalsIgnoreCase("Telstra"))
                    callForTTDAttempt(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).isDoesAlcoholDeliveries());
                else {
                    int distanceFromCurrentToDrop = (int) LoginZoomToU.checkInternetwithfunctionality.getDistanceFromCurrentToDropLocation(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_GPSX(),
                            ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_GPSY());
                    if (distanceFromCurrentToDrop <= 1000) {
                        callForTTDAttempt(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).isDoesAlcoholDeliveries());
                    } else
                        DialogActivity.alertDialogView(activeBookingContext, "Error!", "Booking can only be marked as “Tried to deliver” when attempted at Dropoff Address only.");
                }
            }
        });

        Button safeDelDialogCancelBtn = (Button) atlDialog.findViewById(R.id.safeDelDialogCancelBtn);

        safeDelDialogCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissATLDialog();
                isDropOffFromATL = false;
            }
        });

        atlDialog.show();
    }

    private void dialogWhatIsSafe() {
        if (bgBlurDefaultView != null)
            bgBlurDefaultView.setVisibility(View.VISIBLE);
        try {
            if (alertDialogInActiveList != null)
                if (alertDialogInActiveList.isShowing())
                    alertDialogInActiveList.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialogInActiveList != null)
            alertDialogInActiveList = null;
        alertDialogInActiveList = new Dialog(activeBookingContext);
        alertDialogInActiveList.setCancelable(false);
        alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogInActiveList.setContentView(R.layout.dialog_what_safe_for_atl);

        Window window = alertDialogInActiveList.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView titleTxtForWhatIsSafe = (TextView) alertDialogInActiveList.findViewById(R.id.titleTxtForWhatIsSafe);


        final CheckBox checkBoxForWhatIsSafe = (CheckBox) alertDialogInActiveList.findViewById(R.id.checkBoxForWhatIsSafe);



        Button atlDropOffBtnWhatIsSafe = (Button) alertDialogInActiveList.findViewById(R.id.atlDropOffBtnWhatIsSafe);

        atlDropOffBtnWhatIsSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxForWhatIsSafe.isChecked()) {
                    if (bgBlurDefaultView != null)
                        bgBlurDefaultView.setVisibility(View.GONE);
                    isDropOffFromATL = true;
                    alertDialogInActiveList.dismiss();
                    dismissATLDialog();
                    takePic("Please take a picture of the parcel or where you have left the parcel safely. Do not take a photo of the customer.");
                } else
                    DialogActivity.alertDialogView(activeBookingContext, "Alert!", "Can you make sure Safe location option is ticked?");
            }
        });

        Button cancelBtnWhatIsSafe = (Button) alertDialogInActiveList.findViewById(R.id.cancelBtnWhatIsSafe);

        cancelBtnWhatIsSafe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogInActiveList.dismiss();
                isDropOffFromATL = false;
                if (bgBlurDefaultView != null)
                    bgBlurDefaultView.setVisibility(View.GONE);
            }
        });

        alertDialogInActiveList.show();
    }


    //************** open barcode scanner view to read AWB number *************/
    public void openBarCodeScannerView(int ScannedAWB) {
        try {
            LocalBroadcastManager.getInstance(activeBookingContext).unregisterReceiver(receiverForSilentNotificationActiveList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(activeBookingContext, BarcodeScanner.class);
        intent.putExtra("ScanAWBForPick", ScannedAWB);
        intent.putExtra("RunType", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getRunType());
        intent.putExtra("AWB_NUMBER", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getOrderNumber());
        intent.putStringArrayListExtra("PIECE_ARRAY", ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPiecesArray());
        if (((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPiecesArray().size() > 1) {
            ActiveBookingDetail_New.inItScannedPieceMap();
            ActiveBookingDetail_New.scannedPieceMap.putAll(((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getPiecesScannedMap());
        }
        activeBookingContext.startActivity(intent);
        intent = null;
    }

    private void dismissATLDialog() {
        try {
            if (atlDialog != null)
                if (atlDialog.isShowing())
                    atlDialog.dismiss();
            atlDialog = null;
            subBookingView.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*************** Show Non-ATL dialog when ATL = 0 & AuthorityToLeavePermitted = 0 ************
    //*****  isProccedForDropOff = 1 When trying to drop off the booking
    //*****  isProccedForDropOff = 2 When trying to Tried to deliver the booking
    private void dialogNonATLBooking(final int isProccedForDropOff) {
        try {
            if (alertDialogInActiveList != null)
                if (alertDialogInActiveList.isShowing())
                    alertDialogInActiveList.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (alertDialogInActiveList != null)
            alertDialogInActiveList = null;
        alertDialogInActiveList = new Dialog(activeBookingContext);
        alertDialogInActiveList.setCancelable(false);
        alertDialogInActiveList.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialogInActiveList.setContentView(R.layout.dialog_non_atl_booking);

        Window window = alertDialogInActiveList.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams wlp = window.getAttributes();

        wlp.gravity = Gravity.CENTER;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        TextView titleTxtNonATLDialog = (TextView) alertDialogInActiveList.findViewById(R.id.titleTxtNonATLDialog);


        TextView msgTxtNonATLDialog = (TextView) alertDialogInActiveList.findViewById(R.id.msgTxtNonATLDialog);


        Button doneBtnNonATLDialog = (Button) alertDialogInActiveList.findViewById(R.id.doneBtnNonATLDialog);

        doneBtnNonATLDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialogInActiveList.dismiss();
                if (isProccedForDropOff == 2)
                    openBarCodeScannerView(2);
                else
                    alertToShowSpecialInsBeforePickOrDrop(itemSelectedInActiveBookingList, ((All_Bookings_DataModels) BookingView.bookingListArray.get(itemSelectedInActiveBookingList)).getDrop_Notes());
            }
        });

        Button backBtnNonATLDialog = (Button) alertDialogInActiveList.findViewById(R.id.backBtnNonATLDialog);

        backBtnNonATLDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialogInActiveList.dismiss();
            }
        });

        alertDialogInActiveList.show();
    }

}
