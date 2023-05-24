package com.z2u.chatview;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.z2u.chat.Chat;
import com.z2u.chat.ChatListAdapter;
import com.z2u.chat.FirebaseListAdapter;
import com.z2u.chat.Firebase_Auth_Provider;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.MyAsyncTask.MyAsyncTasks;
import com.zoom2u.MyContentProviderAtLocal;
import com.zoom2u.PushReceiver;
import com.zoom2u.dialogactivity.Custom_ProgressDialogBar;
import com.zoom2u.slidemenu.accountdetail_section.MyProfile_Information;
import com.zoom2u.utility.Functional_Utility;
import com.zoom2u.R;
import com.zoom2u.dialogactivity.DialogActivity;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

public class ChatDetailActivity extends Activity {

    private ImageView btnBack;
    ImageView sendButtonChatDetail;
    private TextView txtChatPersonNameHeading, customerStatusTxt;
    private EditText messageInputChatDetail;
    private ListView my_recycler_view_chatDetail;
    private ImageView iconCallBtn;

    RelativeLayout chatViewFullImage;

    private String mUsername;

    private ImageView attachIcon_ChatView;

    private ChatListAdapter mChatListAdapter;
    boolean isTyping = false;

    final int TYPING_TIMEOUT = 2000; // 5 seconds timeout
    final Handler timeoutHandler = new Handler();

    private int chatItemCountForNotificationSound = 0;

    public static int chatItemCount = 1;

    private Model_DeliveriesToChat modelChatDeliveryItem;
    long unreadMessageCountForCustomer = 0;
    long isCustomerOnline = 0;
    private Timer listScrollToTopTimer;

    private boolean isChatBookingScreenOpne = false;

    public static String BOOKING_CHAT_NODE = "";

    private MediaRecorder mRecorder;
    private String fileNameRecordAudio = null;
    private Bitmap attached_Image;
    private int RECORD_AUDIO_REQUEST_CODE =123 ;

    private static final int TAKE_PHOTO = 191;
    private static final int PICK_FROM_GALLERY = 192;

    private ValueEventListener courierUnreadEventListner;
    private ValueEventListener courierTypingEventListner;
    private ValueEventListener courierOnlineStatusEventListner;
    private ValueEventListener courierMessageEventListner;

    public static final String COURIER_ADMIN_MESSAGE_CHAT = "courier-admin-chat-messages/";
    public static final String COURIER_ADMIN_UNREADS = "courier-admin-chat/";
    public static final String CUSTOMER_COURIER_BOOKINGCHAT = "customer-courier-booking-chat/";
    Window window;
    private static int CALL_FOR_ATTACHMENT = 0;
    ProgressDialog progressToUploadImage;
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        new Firebase_Auth_Provider(ChatDetailActivity.this, false);            //******** In it firebase current user ********

        removeAllActiveListners();  //********* Remove all old active listner ***********

        if (intent.getExtras() != null) {
            modelChatDeliveryItem = intent.getExtras().getParcelable("ModelDeliveryChatItem");
            unreadMessageCountForCustomer = modelChatDeliveryItem.getUnreadMsgCountOfCourier();

            inItChatDetailView();
        }

        onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_detail);
        RelativeLayout heading=findViewById(R.id.rel);
        window = ChatDetailActivity.this.getWindow();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }

        if(MainActivity.isIsBackGroundGray()){
            heading.setBackgroundResource(R.color.base_color_gray);
            window.setStatusBarColor(Color.parseColor("#374350"));
        }else{
            heading.setBackgroundResource(R.color.base_color1);
            window.setStatusBarColor(Color.parseColor("#00A6E2"));
        }

        new Firebase_Auth_Provider(ChatDetailActivity.this, false);            //******** In it firebase current user ********

        //************ Check activity stack to launch Chatview from notification
        int launchChatDetailPageCount = ChatView_BidConfirmation.checkActivityIsInTop(ChatDetailActivity.this);
        //*********** launchChatDetailPageCount = 0  // To continue with page
        //*********** launchChatDetailPageCount = 1  // To Launch app because it no longer active
        //*********** launchChatDetailPageCount = 2  // Do nothing or destroy this view because courier is in Login page
        if (launchChatDetailPageCount == 0) {

            chatItemCount = 1;
            isChatBookingScreenOpne = true;

            if (savedInstanceState != null) {
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
                modelChatDeliveryItem = savedInstanceState.getParcelable("modelChatDeliveryItem");
                LoginZoomToU.courierID = savedInstanceState.getString("CourierID");
                if (LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(ChatDetailActivity.this);

                inItChatDetailView();
            } else {
                if (getIntent().getExtras() != null) {
                    modelChatDeliveryItem = getIntent().getExtras().getParcelable("ModelDeliveryChatItem");
                    if (modelChatDeliveryItem == null)
                        finish();
                    else {
                        unreadMessageCountForCustomer = modelChatDeliveryItem.getUnreadMsgCountOfCourier();
                        inItChatDetailView();
                    }
                }
            }
        } else if (launchChatDetailPageCount == 1) {
            Intent launchZoom2uAppIntent = new Intent(ChatDetailActivity.this, MainActivity.class);
            launchZoom2uAppIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            launchZoom2uAppIntent.setAction(Intent.ACTION_MAIN);
            launchZoom2uAppIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(launchZoom2uAppIntent);
            launchZoom2uAppIntent = null;
            finish();
        } else
            finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CALL_FOR_ATTACHMENT == 0) {
            chatItemCount = 1;
            if (BOOKING_CHAT_NODE.equals("")) {
                if (getIntent().getExtras() != null) {
                    modelChatDeliveryItem = getIntent().getExtras().getParcelable("ModelDeliveryChatItem");
                    unreadMessageCountForCustomer = modelChatDeliveryItem.getUnreadMsgCountOfCourier();

                    inItChatDetailView();
                }
                onStart();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsOtherScreenOpen =false;
        if (CALL_FOR_ATTACHMENT == 0) {
            Log.e("On pause Chat detail", "On Pause called");
            removeAllActiveListners();  //********* Remove all old active listner ***********
            mChatListAdapter.cleanup();
            BOOKING_CHAT_NODE = "";
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
        outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
        outState.putParcelable("modelChatDeliveryItem", modelChatDeliveryItem);
        outState.putString("CourierID", LoginZoomToU.courierID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        try{
            if(savedInstanceState != null){
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
                modelChatDeliveryItem = savedInstanceState.getParcelable("modelChatDeliveryItem");
                LoginZoomToU.courierID = savedInstanceState.getString("CourierID");
                if(LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(ChatDetailActivity.this);
            }
            inItChatDetailView();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItChatDetailView() {
        try{

            chatViewFullImage = (RelativeLayout) findViewById(R.id.chatViewFullImage);
            attachIcon_ChatView = (ImageView) findViewById(R.id.attachIcon_ChatView);

            int notificationID = 1;
            if (modelChatDeliveryItem.getBookingId() != 0) {
                notificationID =  modelChatDeliveryItem.getBookingId();
                BOOKING_CHAT_NODE = modelChatDeliveryItem.getBookingId() + "_" + LoginZoomToU.courierID;
                attachIcon_ChatView.setVisibility(View.GONE);
            } else {
                notificationID = 1;
                BOOKING_CHAT_NODE = LoginZoomToU.courierID;
                attachIcon_ChatView.setVisibility(View.VISIBLE);
                attachIcon_ChatView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDiag();
                    }
                });
            }

            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationID);
            clearChatNotification();
            notificationManager = null;

            if (my_recycler_view_chatDetail == null)
                my_recycler_view_chatDetail = (ListView) findViewById(R.id.my_recycler_view_chatDetail);
            my_recycler_view_chatDetail.setCacheColorHint(Color.parseColor("#d8d8d8"));
            my_recycler_view_chatDetail.requestFocus();
            if (iconCallBtn == null)
                iconCallBtn = (ImageView) findViewById(R.id.iconCallBtn);
            if (customerStatusTxt == null)
                customerStatusTxt = (TextView) findViewById(R.id.customerStatusTxt);

            customerStatusTxt.setVisibility(View.GONE);
            if (txtChatPersonNameHeading == null)
                 txtChatPersonNameHeading = (TextView) findViewById(R.id.txtChatPersonNameHeading);

            txtChatPersonNameHeading.setText(modelChatDeliveryItem.getCustomer());

            if (btnBack == null)
                btnBack =  findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backFromChatDetailView();
                }
            });
            if (sendButtonChatDetail == null)
                sendButtonChatDetail = findViewById(R.id.sendButtonChatDetail);


            if (messageInputChatDetail == null)
                messageInputChatDetail = (EditText) findViewById(R.id.messageInputChatDetail);


            final Runnable typingTimeout = new Runnable() {
                public void run() {
                    isTyping = false;
                    if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                        mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/typing").setValue(0);
                    else
                        mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/status/courier/typing").setValue(0);
                }
            };

            // Make sure we have a mUsername
            setupUsername();

            messageInputChatDetail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                    if (actionId == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                        sendMessage();
                    }
                    return true;
                }
            });

            messageInputChatDetail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if(hasFocus){
                        if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                            mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/admin/unread").setValue(0);
                        else
                            mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/status/customer/unread").setValue(0);
                    }
                }
            });

            messageInputChatDetail.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    timeoutHandler.removeCallbacks(typingTimeout);
                    if (messageInputChatDetail.getText().toString().trim().length() > 0) {
                        timeoutHandler.postDelayed(typingTimeout, TYPING_TIMEOUT);
                        if (!isTyping){
                            isTyping = true;
                            if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                                mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/typing").setValue(1);
                            else
                                mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/status/courier/typing").setValue(1);
                        }
                    }else {
                        isTyping = false;
                        if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                            mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/typing").setValue(0);
                        else
                            mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/status/courier/typing").setValue(0);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            sendButtonChatDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });

            iconCallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (modelChatDeliveryItem.getCustomerMobile() != null && !modelChatDeliveryItem.getCustomerMobile().equals("")) {
                        Intent inten = new Intent(Intent.ACTION_DIAL);
                        inten.setData(Uri.parse("tel:" + modelChatDeliveryItem.getCustomerMobile()));
                        startActivity(inten);
                        inten = null;
                    }else
                        DialogActivity.alertDialogView(ChatDetailActivity.this, "Sorry!", "Contact number is not available");
                }
            });

            my_recycler_view_chatDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    LoginZoomToU.imm.hideSoftInputFromWindow(messageInputChatDetail.getWindowToken(), 0);
                    return false;
                }
            });

            my_recycler_view_chatDetail.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @SuppressWarnings("deprecation")
                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0) {
                        // check if we reached the top or bottom of the list
                        View v = my_recycler_view_chatDetail.getChildAt(0);
                        int offset = (v == null) ? 0 : v.getTop();
                        if (offset == 0) {
                            if(FirebaseListAdapter.mKeys != null){
                                if(FirebaseListAdapter.mKeys.size() > (24*chatItemCount)){
                                    String childUrlStr;
                                    if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                                        childUrlStr = COURIER_ADMIN_MESSAGE_CHAT+LoginZoomToU.courierID+"/message";
                                    else
                                        childUrlStr = CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/message";
                                    Query queryRef = mFirebaseRef.child(childUrlStr).orderByKey().endAt(FirebaseListAdapter.mKeys.get(0)).limitToLast(25);
                                    queryRef.addChildEventListener(new ChildEventListener() {
                                        @Override
                                        public void onChildRemoved(DataSnapshot arg0) {
                                        }
                                        @Override
                                        public void onChildMoved(DataSnapshot arg0, String arg1) {
                                        }
                                        @Override
                                        public void onChildChanged(DataSnapshot arg0, String arg1) {
                                        }
                                        @Override
                                        public void onChildAdded(DataSnapshot arg0, String arg1) {
                                            if(mChatListAdapter!=null)
                                            mChatListAdapter.addItems(arg0, arg1);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError arg0) {
                                        }
                                    });
                                    if(listScrollToTopTimer != null)
                                        listScrollToTopTimer = null;
                                    listScrollToTopTimer = new Timer();
                                    listScrollToTopTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            runOnUiThread(new Runnable() {
                                                public void run() {
                                                    my_recycler_view_chatDetail.clearFocus();
                                                    my_recycler_view_chatDetail.setSelection(25);
                                                    chatItemCount++;
                                                }
                                            });
                                        }
                                    }, 300);
                                }
                            }
                        }
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backFromChatDetailView();
    }

    private void backFromChatDetailView() {
        BOOKING_CHAT_NODE = "";
        isChatBookingScreenOpne = false;
        try {
            clearChatNotification();
            if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/admin/unread").setValue(0);
        } catch (Exception e) {
        }
        // LoginZoomToU.imm.hideSoftInputFromWindow(messageInputChatDetail.getWindowToken(), 0);
        finish();
        //overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (CALL_FOR_ATTACHMENT == 0) {
            mChatListAdapter.cleanup();
        }
    }

    //********* Remove all active chat event listner ********
    private void removeAllActiveListners() {
        //    mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
        if (!modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin")) {
            mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE + "/status/courier/unread").removeEventListener(courierUnreadEventListner);
            mFirebaseRef.child("/customers/" + modelChatDeliveryItem.getCustomerId() + "/status/online").removeEventListener(courierOnlineStatusEventListner);
            mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE + "/status/customer/typing").removeEventListener(courierTypingEventListner);
            mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE + "/message").removeEventListener(courierMessageEventListner);
        } else {
            mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/unread").removeEventListener(courierUnreadEventListner);
            mFirebaseRef.child("admin/status/online").removeEventListener(courierOnlineStatusEventListner);
            mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/admin/typing").removeEventListener(courierTypingEventListner);
            mFirebaseRef.child(COURIER_ADMIN_MESSAGE_CHAT+LoginZoomToU.courierID+"/message").removeEventListener(courierMessageEventListner);
        }
    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        mUsername = "courier";
        if (mUsername == null) {
            mUsername = "courier";
            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void sendMessage() {
        String inputMsgTxt = messageInputChatDetail.getText().toString();
        if (!inputMsgTxt.equals("")) {
            unreadMessageCountForCustomer++;
            String currentDate  = sendTimeToServer();
            // Create our 'model', a Chat object
            Chat chat = new Chat(inputMsgTxt,"courier", 0, currentDate, "");
            // Create a new, auto-generated child of that chat location, and save our chat data there
            if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin")) {
                mFirebaseRef.child(COURIER_ADMIN_MESSAGE_CHAT+LoginZoomToU.courierID+"/message").push().setValue(chat);
                mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/unread").setValue(unreadMessageCountForCustomer);
				mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/lastConversation").setValue(currentDate);
            }else{
                mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/message").push().setValue(chat);
                mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT+BOOKING_CHAT_NODE+"/status/courier/unread").setValue(unreadMessageCountForCustomer);
                if (isCustomerOnline == 0){
                    Intent notifyCustomerChat = new Intent(ChatDetailActivity.this, ServiceNotifyCustomerAboutChat.class);
                    notifyCustomerChat.putExtra("CustomerId", modelChatDeliveryItem.getCustomerId());
                    notifyCustomerChat.putExtra("Message", inputMsgTxt);
                    notifyCustomerChat.putExtra("isBidChat", 0);
                    startService(notifyCustomerChat);
                    notifyCustomerChat = null;
                }
            }
            messageInputChatDetail.setText("");
        }
    }

    public static String sendTimeToServer(){
        String dateStr = "";
        try {
            SimpleDateFormat converter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            //getting GMT timezone, you can get any timezone e.g. UTC
            converter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date convertedDate = new Date();
            try {
                dateStr = converter.format(convertedDate);
                converter = null;
                convertedDate = null;
                return dateStr;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateStr;
    }

    @Override
    public void onStart() {
        super.onStart();
        PushReceiver.IsOtherScreenOpen =true;
        if (CALL_FOR_ATTACHMENT == 0) {
            if (mChatListAdapter != null)
                mChatListAdapter.cleanup();
            // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
            my_recycler_view_chatDetail.setCacheColorHint(Color.TRANSPARENT);
            my_recycler_view_chatDetail.setScrollingCacheEnabled(false);
            my_recycler_view_chatDetail.setAnimationCacheEnabled(false);

            // Tell our list adapter that we only want 50 messages at a time
            if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin"))
                mChatListAdapter = new ChatListAdapter(mFirebaseRef.child(COURIER_ADMIN_MESSAGE_CHAT + LoginZoomToU.courierID + "/message").limitToLast(25), ChatDetailActivity.this, R.layout.chat_message_item, mUsername, modelChatDeliveryItem, chatViewFullImage);
            else
                mChatListAdapter = new ChatListAdapter(mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE
                        + "/message").limitToLast(25), ChatDetailActivity.this, R.layout.chat_message_item, mUsername, modelChatDeliveryItem, chatViewFullImage);
            my_recycler_view_chatDetail.setAdapter(mChatListAdapter);
            mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    my_recycler_view_chatDetail.setSelection(mChatListAdapter.getCount() - 1);
                }
            });

            // Finally, a little indication of connection status
//        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot){
//            }
//
//            @Override
//            public void onCancelled(DatabaseError firebaseError) {
//                Toast.makeText(ChatDetailActivity.this, "You are disconnected from chat", Toast.LENGTH_LONG).show();
//            }
//        });

            if (!modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin")) {
                courierUnreadEventListner = mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE +
                        "/status/courier/unread").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null)
                                unreadMessageCountForCustomer = (long) arg0.getValue();
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierOnlineStatusEventListner = mFirebaseRef.child("/customers/" + modelChatDeliveryItem.getCustomerId() + "/status/online").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null) {
                                isCustomerOnline = (long) arg0.getValue();
                                if (isCustomerOnline == 1) {
                                    customerStatusTxt.setText("Online");
                                    customerStatusTxt.setVisibility(View.VISIBLE);
                                } else
                                    customerStatusTxt.setVisibility(View.GONE);

                                chatItemCountForNotificationSound = mChatListAdapter.getCount() + 1;
                            }
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierTypingEventListner = mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE +
                        "/status/customer/typing").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null) {
                                long isTyping = (long) arg0.getValue();
                                if (isTyping == 1) {
                                    customerStatusTxt.setText("Typing...");
                                    customerStatusTxt.setVisibility(View.VISIBLE);
                                } else {
                                    customerStatusTxt.setText("Online");
                                    customerStatusTxt.setVisibility(View.VISIBLE);
                                }

                                chatItemCountForNotificationSound = mChatListAdapter.getCount() + 1;
                            }
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierMessageEventListner = mFirebaseRef.child(CUSTOMER_COURIER_BOOKINGCHAT + BOOKING_CHAT_NODE + "/message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (isChatBookingScreenOpne)
                                my_recycler_view_chatDetail.setSelection(mChatListAdapter.getCount());
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

            } else {
                courierOnlineStatusEventListner = mFirebaseRef.child("admin/status/online").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null) {
                                long isOnline = (long) arg0.getValue();
                                if (isOnline == 1) {
                                    customerStatusTxt.setText("Online");
                                    customerStatusTxt.setVisibility(View.VISIBLE);
                                } else
                                    customerStatusTxt.setVisibility(View.GONE);

                                chatItemCountForNotificationSound = mChatListAdapter.getCount() + 1;
                            }
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierTypingEventListner = mFirebaseRef.child(COURIER_ADMIN_UNREADS + LoginZoomToU.courierID + "/status/admin/typing").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null) {
                                long isTyping = (long) arg0.getValue();
                                if (isTyping == 1)
                                    customerStatusTxt.setText("Typing...");
                                else
                                    customerStatusTxt.setText("Online");

                                customerStatusTxt.setVisibility(View.VISIBLE);
                                chatItemCountForNotificationSound = mChatListAdapter.getCount() + 1;
                            }
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierUnreadEventListner = mFirebaseRef.child(COURIER_ADMIN_UNREADS + LoginZoomToU.courierID + "/status/courier/unread").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (arg0.getValue() != null)
                                unreadMessageCountForCustomer = (long) arg0.getValue();
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });

                courierMessageEventListner = mFirebaseRef.child(COURIER_ADMIN_MESSAGE_CHAT + LoginZoomToU.courierID + "/message").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot arg0) {
                        try {
                            if (isChatBookingScreenOpne)
                                my_recycler_view_chatDetail.setSelection(chatItemCountForNotificationSound);
                        } catch (Exception e) {
                            //	e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError arg0) {
                    }
                });
            }
        }
    }

    private void showDiag() {

        final Dialog dialog = new Dialog(ChatDetailActivity.this);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.upload_doc_chat_dialog);

        ImageView crossImgChatDialog = (ImageView)dialog.findViewById(R.id.crossImgChatDialog);
        crossImgChatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        TextView attachmentViewHeader = (TextView) dialog.findViewById(R.id.attachmentViewHeader);


        ImageView gallaryImgChatDialog = (ImageView)dialog.findViewById(R.id.gallaryImgChatDialog);
        gallaryImgChatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectImageFromGallery();
            }
        });

        ImageView cameraImgChatDialog = (ImageView)dialog.findViewById(R.id.cameraImgChatDialog);
        cameraImgChatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectImageFromCamera();
            }
        });

        ImageView audioImgChatDialog = (ImageView)dialog.findViewById(R.id.audioImgChatDialog);
        audioImgChatDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                getPermissionToRecordAudio();
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    private void selectImageFromGallery() {
        CALL_FOR_ATTACHMENT = 1;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        try {
            intent.putExtra("return-data", true);
            startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_GALLERY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectImageFromCamera() {
        try {
            if((int) Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.TIRAMISU){
                String[] permission = {Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA};
                if (ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[1]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(ChatDetailActivity.this);

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
                            ActivityCompat.requestPermissions(ChatDetailActivity.this,
                                    new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.CAMERA}, 1);
                            enterFieldDialog.dismiss();
                        }
                    });
                    enterFieldDialog.show();

                } else
                    openCamera();
            }

          else  if ((int) Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.TIRAMISU) {
                String[] permission = {android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
                if (ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[0]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[1]) == PackageManager.PERMISSION_DENIED ||
                        ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[2]) == PackageManager.PERMISSION_DENIED) {

                    Dialog enterFieldDialog  = new Dialog(ChatDetailActivity.this);

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
                            ActivityCompat.requestPermissions(ChatDetailActivity.this,
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

    private void openCamera(){
        try {
            PushReceiver.isCameraOpen = true;
            CALL_FOR_ATTACHMENT = 1;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = LoginZoomToU.checkInternetwithfunctionality.createImageFile();
                    LoginZoomToU.isImgFromInternalStorage = false;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    LoginZoomToU.isImgFromInternalStorage = true;
                    Toast.makeText(ChatDetailActivity.this, "Image file at internal",Toast.LENGTH_LONG).show();
                }
                if (LoginZoomToU.isImgFromInternalStorage) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, MyContentProviderAtLocal.CONTENT_URI);
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                } else {
                    //    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                    Uri photoURI = FileProvider.getUriForFile(ChatDetailActivity.this,
                            getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, TAKE_PHOTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ChatDetailActivity.this, "Error while opening camera",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            PushReceiver.isCameraOpen = false;
            if (requestCode == TAKE_PHOTO
                    && resultCode == RESULT_OK) {
                /*************************  2nd from android developer ******************/
                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                if (LoginZoomToU.isImgFromInternalStorage) {
                    File out = new File(getFilesDir(), "chatCameraImage.png");
                    if(!out.exists())
                        Toast.makeText(ChatDetailActivity.this, "Error while capturing image", Toast.LENGTH_LONG) .show();
                    else {
                        int photoW = bmOptions.outWidth;
                        int photoH = bmOptions.outHeight;
                        // Determine how much to scale down the image
                        int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                        // Decode the image file into a Bitmap sized to fill the View
                        bmOptions.inJustDecodeBounds = false;
                        bmOptions.inSampleSize = scaleFactor;
                        bmOptions.inPurgeable = true;
                        Functional_Utility.mCurrentPhotoPath = out.getAbsolutePath();
                        attached_Image = BitmapFactory.decodeFile(out.getAbsolutePath(), bmOptions);
                    }
                } else {
                    int photoW = bmOptions.outWidth;
                    int photoH = bmOptions.outHeight;
                    // Determine how much to scale down the image
                    int scaleFactor = Math.min(photoW / LoginZoomToU.width, photoH / LoginZoomToU.height);
                    // Decode the image file into a Bitmap sized to fill the View
                    bmOptions.inJustDecodeBounds = false;
                    bmOptions.inSampleSize = scaleFactor;
                    bmOptions.inPurgeable = true;

                    attached_Image = BitmapFactory.decodeFile(Functional_Utility.mCurrentPhotoPath, bmOptions);

                    deleteFileFromStorage(Functional_Utility.mCurrentPhotoPath);
                }
            } else if (requestCode == PICK_FROM_GALLERY) {
                if (resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    ContentResolver contentResolver = getContentResolver();
                    try {
                        if(Build.VERSION.SDK_INT < 28) {
                            attached_Image = MediaStore.Images.Media.getBitmap(contentResolver, selectedImage);
                        } else {
                            ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, selectedImage);
                            attached_Image = ImageDecoder.decodeBitmap(source);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    CALL_FOR_ATTACHMENT = 0;
                }
            }else{
                CALL_FOR_ATTACHMENT = 0;
            }

            if (attached_Image != null)
                uploadChatImageTask(0);
        } catch(Exception e){
            e.printStackTrace();
            CALL_FOR_ATTACHMENT = 0;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadChatImageTask(int isVoiceMessage){
        if(LoginZoomToU.checkInternetwithfunctionality.isConnectingToInternet())
            UploadImageForChat(isVoiceMessage);
            // new UploadImageForChat().execute(isVoiceMessage);
        else
            DialogActivity.alertDialogView(ChatDetailActivity.this, "No Network!", "No Network connection, Please try again later.");
    }

    boolean isAudioRecodingStarted = false;
    private void showDialogToRecordAudio() {

        final Dialog dialog = new Dialog(ChatDetailActivity.this);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_to_record_audio);

        TextView recordTxt_RecordAudio = (TextView) dialog.findViewById(R.id.recordTxt_RecordAudio);

        final Button startStopBtn_RecordAudio = (Button) dialog.findViewById(R.id.startStopBtn_RecordAudio);

        final Chronometer chronometerTimer_RecordAudio = (Chronometer) dialog.findViewById(R.id.chronometerTimer_RecordAudio);

        chronometerTimer_RecordAudio.setBase(SystemClock.elapsedRealtime());
        Button cancelBtn_RecordAudio = (Button) dialog.findViewById(R.id.cancelBtn_RecordAudio);

        final Button sendBtn_Chat_RecordAudio = (Button) dialog.findViewById(R.id.sendBtn_Chat_RecordAudio);

        sendBtn_Chat_RecordAudio.setEnabled(false);
        sendBtn_Chat_RecordAudio.setOnClickListener(null);
        sendBtn_Chat_RecordAudio.setAlpha(0.5f);

        startStopBtn_RecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAudioRecodingStarted) {
                    isAudioRecodingStarted = !isAudioRecodingStarted;
                    startStopBtn_RecordAudio.setText("Start");
                    stopRecording(chronometerTimer_RecordAudio);
                    sendBtn_Chat_RecordAudio.setAlpha(1.0f);
                    sendBtn_Chat_RecordAudio.setEnabled(true);
                    sendBtn_Chat_RecordAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            uploadChatImageTask(1);
                        }
                    });
                } else {
                    isAudioRecodingStarted = !isAudioRecodingStarted;
                    startStopBtn_RecordAudio.setText("Stop");
                    startRecording(chronometerTimer_RecordAudio);
                    sendBtn_Chat_RecordAudio.setEnabled(false);
                    sendBtn_Chat_RecordAudio.setOnClickListener(null);
                    sendBtn_Chat_RecordAudio.setAlpha(0.5f);
                }
            }
        });

        cancelBtn_RecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                stopRecording(chronometerTimer_RecordAudio);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK){
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        dialog.show();
    }

    private void startRecording(Chronometer chronometerTimer_RecordAudio) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        File root = getCacheDir();
        File file = new File(root + "/Zoom2u/Audios");
        if (!file.exists()) {
            file.mkdirs();
        } else {
            deleteFileFromStorage(root + "/Zoom2u/Audios/Z2U_194359_RecordAudio.m4a");
        }

        fileNameRecordAudio = root + "/Zoom2u/Audios/Z2U_194359_RecordAudio.m4a";
        Log.d("filename", fileNameRecordAudio);
        mRecorder.setOutputFile(fileNameRecordAudio);


        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //starting the chronometer
        chronometerTimer_RecordAudio.setBase(SystemClock.elapsedRealtime());
        chronometerTimer_RecordAudio.start();
    }

    private void deleteFileFromStorage(String fileName) {
        try {
            File audioRecordedFile = new File(fileName);
            if(audioRecordedFile.isFile())
                audioRecordedFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecording(Chronometer chronometerTimer_RecordAudio) {

        try {
            mRecorder.stop();
            mRecorder.release();
        } catch (Exception e){
            e.printStackTrace();
        }
        mRecorder = null;
        //starting the chronometer
        chronometerTimer_RecordAudio.stop();
        //showing the play button
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void getPermissionToRecordAudio() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if((int) Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.TIRAMISU){
            String[] permission = {Manifest.permission.RECORD_AUDIO};
            if (ContextCompat.checkSelfPermission(ChatDetailActivity.this, permission[0]) == PackageManager.PERMISSION_DENIED ) {
                requestPermissions(new String[]{ Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_MEDIA_IMAGES},
                        RECORD_AUDIO_REQUEST_CODE);
            }
            else
                showDialogToRecordAudio();

        }
        else  if ((int) Build.VERSION.SDK_INT<android.os.Build.VERSION_CODES.TIRAMISU) {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    RECORD_AUDIO_REQUEST_CODE);
        }
            else
                showDialogToRecordAudio();
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == RECORD_AUDIO_REQUEST_CODE) {
            //check permission for the android 13 and above
            if((int) Build.VERSION.SDK_INT>=android.os.Build.VERSION_CODES.TIRAMISU){
                if (grantResults.length == 2 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                {
                    showDialogToRecordAudio();
                }else{
                    Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
                }

            }else
          //checking code for the below android 13
            if (grantResults.length == 3 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    && grantResults[2] == PackageManager.PERMISSION_GRANTED){

                showDialogToRecordAudio();
            } else {
                Toast.makeText(this, "You must give permissions to use this app. App is exiting.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void UploadImageForChat(int isVoiceMessage){

        final String[] uploadAttachment_ResponseStr = {"0"};

        new MyAsyncTasks(){
            @Override
            public void doInBackground() {

                try {
                    WebserviceHandler webServiceHandler = new WebserviceHandler();
                    if (isVoiceMessage == 1) {
                        uploadAttachment_ResponseStr[0] = webServiceHandler.uploadChat_VoiceMessage(new File(fileNameRecordAudio));
                    } else
                        uploadAttachment_ResponseStr[0] = webServiceHandler.uploadProfileImage(attached_Image, "Chat_IMG.png", true);
                    webServiceHandler = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onPreExecute() {
                if (progressToUploadImage == null)
                    progressToUploadImage = new ProgressDialog(ChatDetailActivity.this);
                Custom_ProgressDialogBar.inItProgressBar(progressToUploadImage);
            }

            @Override
            public void onPostExecute() {
                Custom_ProgressDialogBar.dismissProgressBar(progressToUploadImage);

                try {
                    JSONObject jObjOfAttachMentResponse = new JSONObject(uploadAttachment_ResponseStr[0]);
                    String inputMsgTxt = jObjOfAttachMentResponse.getString("url");
                    String msgType;
                    if (isVoiceMessage == 1)
                        msgType = "audio";
                    else {
                        msgType = "photo";
                        if (attached_Image != null)
                            attached_Image.recycle();
                        attached_Image = null;
                    }

                    if (!inputMsgTxt.equals("")) {
                        unreadMessageCountForCustomer++;
                        String currentDate = sendTimeToServer();
                        Chat chat;
                        if (msgType.equals("photo")) {
                            String thumbnailImgURL = jObjOfAttachMentResponse.getString("thumbnailUrl");
                            // Create our 'model', a Chat object
                            chat = new Chat(inputMsgTxt, "courier", 0, currentDate, msgType, thumbnailImgURL);
                        } else
                            chat = new Chat(inputMsgTxt, "courier", 0, currentDate, msgType);

                        // Create a new, auto-generated child of that chat location, and save our chat data there
                        if (modelChatDeliveryItem.getCustomer().equals("Zoom2u-Admin")) {
                            mFirebaseRef.child(COURIER_ADMIN_MESSAGE_CHAT + LoginZoomToU.courierID + "/message").push().setValue(chat);
                            mFirebaseRef.child(COURIER_ADMIN_UNREADS+LoginZoomToU.courierID+"/status/courier/unread").setValue(unreadMessageCountForCustomer);
                            mFirebaseRef.child(COURIER_ADMIN_UNREADS + LoginZoomToU.courierID + "/lastConversation").setValue(currentDate);
                        }

                        messageInputChatDetail.setText("");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ChatDetailActivity.this, "Failed to upload document", Toast.LENGTH_LONG).show();
                }

                CALL_FOR_ATTACHMENT = 0;
            }
        }.execute();
    }

    public void clearChatNotification(){
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(3);
        notificationManager = null;
    }

}
