package com.z2u.chatview;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.z2u.chat.Chat;
import com.z2u.chat.ChatListAdapter;
import com.z2u.chat.Firebase_Auth_Provider;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.MainActivity;
import com.zoom2u.PushReceiver;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.BookingView;
import com.zoom2u.webservice.WebserviceHandler;

import java.util.List;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

/**
 * Created by Arun Dey on 19/12/16.
 **/

public class ChatView_BidConfirmation extends Activity{

    private ImageView btnBack, sendButtonChatDetail;
    private TextView txtChatPersonNameHeading, customerStatusTxt;
    private EditText messageInputChatDetail;
    private ListView my_recycler_view_chatDetail;
    private String mUsername;

    private ValueEventListener mConnectedListener, onDataChangesListner, customerOnlineListner, courierUnreadListner;

    private ChatListAdapter mChatListAdapter;

    public static String BID_ID_NODE = "";
    private String customerIdToBidChat;
    private long isCustomerOnline;
    private long unreadMessageCountForCustomer;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        new Firebase_Auth_Provider(ChatView_BidConfirmation.this, false);            //******** In it firebase current user ********

        removeAllListners();
        inItChatView(intent);
        onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_detail);
        PushReceiver.IsOtherScreenOpen =true;
        RelativeLayout heading=findViewById(R.id.rel);
        Window window = ChatView_BidConfirmation.this.getWindow();
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
        new Firebase_Auth_Provider(ChatView_BidConfirmation.this, false);            //******** In it firebase current user ********

        //************ Check activity stack to launch Chatview from notification
        int launchChatDetailPageCount = checkActivityIsInTop(ChatView_BidConfirmation.this);
        //*********** launchChatDetailPageCount = 0  // To continue with page
        //*********** launchChatDetailPageCount = 1  // To Launch app because it no longer active
        //*********** launchChatDetailPageCount = 2  // Do nothing because courier is in Login page
        if (launchChatDetailPageCount == 0) {
            SlideMenuZoom2u.connectWithFireBaseChat();
            if (savedInstanceState != null) {
                WebserviceHandler.isRoutific = savedInstanceState.getBoolean("Routific");
                ConfirmPickUpForUserName.isDropOffSuccessfull = savedInstanceState.getInt("SlideMenuItemCount");
                BookingView.bookingViewSelection = savedInstanceState.getInt("bookingViewSelection");
                LoginZoomToU.courierID = savedInstanceState.getString("CourierID");
                if (LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(ChatView_BidConfirmation.this);
            }

            inItChatView(getIntent());
        } else if (launchChatDetailPageCount == 1) {
            Intent launchZoom2uAppIntent = new Intent(ChatView_BidConfirmation.this, MainActivity.class);
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
    public void onStop() {
        super.onStop();
        mChatListAdapter.cleanup();
    }

    //************ Remove All current listners *******
    private void removeAllListners() {
        if (!BID_ID_NODE.equals("")) {
            mFirebaseRef.getRoot().child(".info/connected").removeEventListener(mConnectedListener);
            mFirebaseRef.child("quote-request-comments/" + BID_ID_NODE + "/message").removeEventListener(onDataChangesListner);
            mFirebaseRef.child("/customers/" + customerIdToBidChat + "/status/online").removeEventListener(customerOnlineListner);
            mFirebaseRef.child("quote-request-comments/" + BID_ID_NODE + "/status/courier/unread").removeEventListener(courierUnreadListner);
        }
    }

    private void inItChatView (Intent intent){
        int bidID = intent.getIntExtra("BidIDForChat", 0);
        customerIdToBidChat = intent.getStringExtra("CustomerIDForChat");

        BID_ID_NODE = bidID+"_"+LoginZoomToU.courierID;

        inItChatDetailView(intent);

        try {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(bidID);
            notificationManager = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PushReceiver.IsOtherScreenOpen =false;
        Log.e("On Pause", "Pn pause is called");
        removeAllListners();
        mChatListAdapter.cleanup();
        BID_ID_NODE = "";
    }

    @Override
    protected void onResume() {
        super.onResume();
        PushReceiver.IsOtherScreenOpen =true;
        Log.e("On Resume", "On Resume called");
        if (BID_ID_NODE.equals("")) {
            inItChatView(getIntent());
            onStart();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //   SlideMenuZoom2u.setCourierToOfflineFromChat();
        outState.putInt("SlideMenuItemCount", ConfirmPickUpForUserName.isDropOffSuccessfull);
        outState.putBoolean("Routific", WebserviceHandler.isRoutific);
        outState.putInt("bookingViewSelection", BookingView.bookingViewSelection);
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
                LoginZoomToU.courierID = savedInstanceState.getString("CourierID");
                if(LoginZoomToU.NOVA_BOLD == null)
                    LoginZoomToU.staticFieldInit(ChatView_BidConfirmation.this);
            }
            inItChatDetailView(getIntent());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inItChatDetailView(Intent intent) {
        try{
            if (my_recycler_view_chatDetail != null)
                my_recycler_view_chatDetail = null;
            my_recycler_view_chatDetail = (ListView) findViewById(R.id.my_recycler_view_chatDetail);
            my_recycler_view_chatDetail.setCacheColorHint(Color.parseColor("#d8d8d8"));

            findViewById(R.id.iconCallBtn).setVisibility(View.GONE);
            findViewById(R.id.attachIcon_ChatView).setVisibility(View.GONE);
            if (customerStatusTxt != null)
                customerStatusTxt = null;
            customerStatusTxt = (TextView) findViewById(R.id.customerStatusTxt);

            customerStatusTxt.setVisibility(View.GONE);
            if (txtChatPersonNameHeading != null)
                txtChatPersonNameHeading = null;
            txtChatPersonNameHeading = (TextView) findViewById(R.id.txtChatPersonNameHeading);

            if (intent.getStringExtra("CustomerNameForChat") != null && !intent.getStringExtra("CustomerNameForChat").equals("")
                    && !intent.getStringExtra("CustomerNameForChat").equals("null"))
                txtChatPersonNameHeading.setText(intent.getStringExtra("CustomerNameForChat"));
            else
                txtChatPersonNameHeading.setText("Chat for bid confirmation");


            btnBack =findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    backFromChatDetailView();
                }
            });

            sendButtonChatDetail =  findViewById(R.id.sendButtonChatDetail);

            if (messageInputChatDetail != null)
                messageInputChatDetail = null;
            messageInputChatDetail = (EditText) findViewById(R.id.messageInputChatDetail);


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
                    if (hasFocus)
                        mFirebaseRef.child("quote-request-comments/"+BID_ID_NODE+"/status/customer/unread").setValue(0);
                }
            });

            sendButtonChatDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendMessage();
                }
            });

            my_recycler_view_chatDetail.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    LoginZoomToU.imm.hideSoftInputFromWindow(messageInputChatDetail.getWindowToken(), 0);
                    return false;
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
        removeAllListners();
        BID_ID_NODE = "";
       // LoginZoomToU.imm.hideSoftInputFromWindow(messageInputChatDetail.getWindowToken(), 0);
        finish();
       // overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }

    private void setupUsername() {
        SharedPreferences prefs = getApplication().getSharedPreferences("ChatPrefs", 0);
        if (mUsername == null) {
            mUsername = "courier";
            prefs.edit().putString("username", mUsername).commit();
        }
    }

    private void sendMessage() {
        String inputMsgTxt = messageInputChatDetail.getText().toString();
        if (!inputMsgTxt.equals("")) {
            unreadMessageCountForCustomer++;
            String currentDate  = ChatDetailActivity.sendTimeToServer();
            // Create our 'model', a Chat object
            Chat chat = new Chat(inputMsgTxt, "courier", 0, currentDate, LoginZoomToU.courierName);
            // Create a new, auto-generated child of that chat location, and save our chat data there
            mFirebaseRef.child("quote-request-comments/"+BID_ID_NODE+"/message").push().setValue(chat);
            mFirebaseRef.child("quote-request-comments/"+BID_ID_NODE+"/status/courier/unread").setValue(unreadMessageCountForCustomer);
            messageInputChatDetail.setText("");
            if (isCustomerOnline == 0){
                    Intent notifyCustomerChat = new Intent(ChatView_BidConfirmation.this, ServiceNotifyCustomerAboutChat.class);
                    notifyCustomerChat.putExtra("CustomerId", customerIdToBidChat);
                    notifyCustomerChat.putExtra("Message", inputMsgTxt);
                    notifyCustomerChat.putExtra("isBidChat", 1);
                    startService(notifyCustomerChat);
                    notifyCustomerChat = null;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        PushReceiver.IsOtherScreenOpen =true;
        // Setup our view and list adapter. Ensure it scrolls to the bottom as data changes
        my_recycler_view_chatDetail.setCacheColorHint(Color.TRANSPARENT);
        my_recycler_view_chatDetail.setScrollingCacheEnabled(false);
        my_recycler_view_chatDetail.setAnimationCacheEnabled(false);

        mChatListAdapter = new ChatListAdapter(mFirebaseRef.child("quote-request-comments/" + BID_ID_NODE + "/message"), ChatView_BidConfirmation.this, R.layout.chat_message_item, "quote-request-comments/" + BID_ID_NODE);
        my_recycler_view_chatDetail.setAdapter(mChatListAdapter);
        mChatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                my_recycler_view_chatDetail.setSelection(mChatListAdapter.getCount() - 1);
            }
        });

        // Finally, a little indication of connection status
        mConnectedListener = mFirebaseRef.getRoot().child(".info/connected").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Toast.makeText(ChatView_BidConfirmation.this, "You are disconnected from chat", Toast.LENGTH_LONG).show();
            }
        });

        onDataChangesListner = mFirebaseRef.child("quote-request-comments/" + BID_ID_NODE + "/message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                try {
                    my_recycler_view_chatDetail.setSelection(mChatListAdapter.getCount());
                } catch (Exception e) {
                    //	e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });

        customerOnlineListner = mFirebaseRef.child("/customers/" + customerIdToBidChat + "/status/online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                try {
                    if (arg0.getValue() != null) {
                        isCustomerOnline = (long) arg0.getValue();
                    }
                } catch (Exception e) {
                    //	e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        });

        courierUnreadListner = mFirebaseRef.child("quote-request-comments/" + BID_ID_NODE + "/status/courier/unread").addValueEventListener(new ValueEventListener() {
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
    }


    //************ Check activity stack to launch Chatview from notification ***************
    public static int checkActivityIsInTop(Context currentViewContext) {
        ActivityManager mngr = (ActivityManager) currentViewContext.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if (taskList.get(0).numActivities == 1 && taskList.get(0).topActivity.getClassName().equals(currentViewContext.getClass().getName())) {
            return 1;
        } else if (taskList.get(0).baseActivity.getClassName().equals("com.zoom2u.LoginZoomToU")) {
            return 2;
        }else
            return 0;
    }
}
