package com.zoom2u.slidemenu.offerrequesthandlr;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.core.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.z2u.chat.Chat;
import com.z2u.chatview.ChatViewBookingScreen;
import com.z2u.chatview.ChatView_BidConfirmation;
import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.R;
import com.zoom2u.utility.Notification_Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Arun on 24/5/17.
 */

public class BidRequest_ChatModel implements Parcelable {

    public static String BID_NODE_CHAT = "";

    private int requestID;
    private String customerID;
    private long unreadBidChatCountForCustomer;
    private ValueEventListener valueEventListnerOfCustomerBidChat;
    private boolean isBidCancelled;
    String  Vehicle;
    String CustomerPhoto;
    //requestView_pojo.setVehicle(objDelivery.getString("Vehicle"));
    //requestView_pojo.setCustomerPhoto(objDelivery.getString("CustomerPhoto"));
    Context context;
    boolean isNotificationSoundPlay = false;
    boolean isBidChatModelInit = false;

    public BidRequest_ChatModel(Context context, int requestId, String customerName, String customerID, boolean isBidCancelled,String  Vehicle,String CustomerPhoto){
        this.context = context;
        this.requestID = requestId;
        this.customerID = customerID;
        isBidChatModelInit = true;
        this.isBidCancelled = isBidCancelled;
        this.Vehicle=Vehicle;
        this.CustomerPhoto=CustomerPhoto;
        getUnreadMsgCountOfCustomer("quote-request-comments/" + requestID + "_" + LoginZoomToU.courierID + "/status/customer/unread", customerName);
    }

    public String getVehicle() {
        return Vehicle;
    }

    public void setVehicle(String vehicle) {
        Vehicle = vehicle;
    }

    public String getCustomerPhoto() {
        return CustomerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto) {
        CustomerPhoto = customerPhoto;
    }

    public int getRequestID() {
        return requestID;
    }

    public long getUnreadBidChatCountForCustomer() {
        return unreadBidChatCountForCustomer;
    }

    public ValueEventListener getValueEventListnerOfCustomerBidChat() {
        return valueEventListnerOfCustomerBidChat;
    }

    private void getUnreadMsgCountOfCustomer(String baseChatURL, String customerName) {
        addMessageEventListnerOfCustomerChat(baseChatURL, customerName);
    }

    private void addMessageEventListnerOfCustomerChat(final String baseUrlForBidChat, final String customerName){
        valueEventListnerOfCustomerBidChat = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                if (arg0.getValue() != null) {
                    long unreadCount = 0;
                    unreadCount = (long) arg0.getValue();
                    if (!isBidCancelled) {
                        unreadBidChatCountForCustomer = (int) unreadCount;
                        /* Get unread message count of customer or admin and update it to server if courier sent message to this perticular customer  */
                        if (unreadBidChatCountForCustomer > 0) {
                            Log.e("", "----- isBidChatModelInit------ " + isBidChatModelInit + " ----  isNotificationSoundPlay --- " + isNotificationSoundPlay);
                            if (!isBidChatModelInit && !isNotificationSoundPlay && !isBidCancelled) {
                                isNotificationSoundPlay = true;
                                Log.e("", "********** Called event listner for notification ******** ");
                                valueEventListnerForNotification(baseUrlForBidChat, customerName);         //*** Add value event listner for Notification ****
                            } else
                                isBidChatModelInit = false;
                        } else
                            isBidChatModelInit = false;

                        //********** Refresh slide menu Bid list adapter *********
                        if (TimerForBid_Active.requestView_adapter != null)
                            TimerForBid_Active.requestView_adapter.notifyDataSetChanged();

                        //********** Refresh Bid chat list adapter *********
                        if (ChatListForBid.bidChatListAdapter != null)
                            ChatListForBid.bidChatListAdapter.notifyDataSetChanged();

                        //********** Notify Bid detail view counter *********
                        if (Bid_RequestView_Detail.isBidDetailPageOpen && Bid_RequestView_Detail.btnNotifyUnreadCountDetail != null) {
                            if (unreadBidChatCountForCustomer > 0 && requestID == Bid_RequestView_Detail.requestDetailBidId && !isBidCancelled) {
                                Bid_RequestView_Detail.btnNotifyUnreadCountDetail.setVisibility(View.VISIBLE);
                                Bid_RequestView_Detail.btnNotifyUnreadCountDetail.setText("" + unreadBidChatCountForCustomer);
                            } else
                                Bid_RequestView_Detail.btnNotifyUnreadCountDetail.setVisibility(View.GONE);
                        }

                        //********** Show Slide menu Bid list unread counter at header *********
                        if (SlideMenuZoom2u.countChatBookingView != null) {
                            if (ConfirmPickUpForUserName.isDropOffSuccessfull != 13)
                                Model_DeliveriesToChat.showExclamationForUnreadChat(SlideMenuZoom2u.countChatBookingView);
                            else if (ConfirmPickUpForUserName.isDropOffSuccessfull == 13
                                    && RequestView.requestView_pojoList.size() == 0) {
                                Model_DeliveriesToChat.showExclamationForUnreadChat(SlideMenuZoom2u.countChatBookingView);
                            } else
                                showTotalUnreadCountChat(SlideMenuZoom2u.countChatBookingView);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {

            }
        };
        ChatViewBookingScreen.mFirebaseRef.child(baseUrlForBidChat).addValueEventListener(valueEventListnerOfCustomerBidChat);
    }

    //	/************** Show exclamation icon to unread chat for courier Called in OnResume or Booking view selection  *************/
	public static void showTotalUnreadCountChat(TextView totalUnreadCountTxt) {
		long totalUnreadCount = 0;
		if (totalUnreadCountTxt != null) {
			try {
				for(RequestView_DetailPojo requestView_detailPojo : RequestView.requestView_pojoList)
                    totalUnreadCount = totalUnreadCount + requestView_detailPojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer();

				if (totalUnreadCount > 0) {
                    totalUnreadCountTxt.setVisibility(View.VISIBLE);
                    totalUnreadCountTxt.setText("!");
                } else if (ConfirmPickUpForUserName.isDropOffSuccessfull == 13)
                    totalUnreadCountTxt.setVisibility(View.GONE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}

    private void valueEventListnerForNotification(String childUrlForStatusUpdate, final String customerName) {
        final ValueEventListener valueEventListenerOfNotificcation;
        valueEventListenerOfNotificcation = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Chat post = postSnapShot.getValue(Chat.class);
                    try {
                        if (!post.getSender().equalsIgnoreCase("courier")) {
                            BID_NODE_CHAT = requestID+"_"+LoginZoomToU.courierID;
                            Log.e("", "+++++++++++ Request id +++++++ "+requestID+"   --- Bid node chat ---- "+BID_NODE_CHAT+"   Bid Node id = "+ChatView_BidConfirmation.BID_ID_NODE);
                            if (ChatView_BidConfirmation.BID_ID_NODE.equals("") || !ChatView_BidConfirmation.BID_ID_NODE.equals(BID_NODE_CHAT)) {
                                Intent notificationIntent = new Intent(context, ChatView_BidConfirmation.class);
                                notificationIntent.putExtra("BidIDForChat", requestID);
                                notificationIntent.putExtra("CustomerNameForChat", customerName);
                                notificationIntent.putExtra("CustomerIDForChat", customerID);
                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                                int requestIDForPendingIntent = (int) System.currentTimeMillis();
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, requestIDForPendingIntent, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                                NotificationManager mNotificationManager =
                                        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                androidx.core.app.NotificationCompat.Builder builder;

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    builder = Notification_Utils.notificationBuilderFor_Oreo(context, mNotificationManager, pendingIntent,
                                            context.getString(R.string.app_name)+" - Bid request", customerName + " - " + post.getMessage(), Notification_Utils.soundUri);
                                } else {
                                    builder = new androidx.core.app.NotificationCompat.Builder(context)
                                        .setSound(Uri.parse(Notification_Utils.soundUri))
                                        .setContentTitle("Zoom2u - Bid request")
                                        .setContentText(customerName + " - " + post.getMessage())
                                        .setStyle(new NotificationCompat.InboxStyle()
                                                .addLine(customerName)
                                                .addLine(post.getMessage()))
                                        .setGroup("Message")
                                        .setContentIntent(pendingIntent)
                                        .setSmallIcon(Model_DeliveriesToChat.getNotificationIcon())
                                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                                        .setAutoCancel(true).setChannelId("Z2U Channel");

                                if (android.os.Build.VERSION.SDK_INT >= 21) {
                                    builder.setColor(context.getResources().getColor(R.color.colorAccent))
                                            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                                }
                            }

                                Notification notification = builder.build();
                                mNotificationManager.notify(requestID, notification);
                                mNotificationManager = null;
                                builder = null;
                                pendingIntent = null;
                                isNotificationSoundPlay = false;
                            }
                        }
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        final String newUrl = childUrlForStatusUpdate.replace("/status/customer/unread", "").trim();
        ChatViewBookingScreen.mFirebaseRef.child(newUrl+"/message").limitToLast(1).addValueEventListener(valueEventListenerOfNotificcation);
        Timer removeTaskTimer = new Timer();
        removeTaskTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ChatViewBookingScreen.mFirebaseRef.child(newUrl+"/message").limitToLast(1).removeEventListener(valueEventListenerOfNotificcation);
            }
        }, 500);
        //Model_DeliveriesToChat.notificationSoundForChat(context, R.raw.chatnotification);
        isNotificationSoundPlay = false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(unreadBidChatCountForCustomer);
    }

    private BidRequest_ChatModel(Parcel in){
        unreadBidChatCountForCustomer = in.readLong();
    }

    public static final Parcelable.Creator<BidRequest_ChatModel> CREATOR  = new Parcelable.Creator<BidRequest_ChatModel>() {
        public BidRequest_ChatModel createFromParcel(Parcel in) {
            return new BidRequest_ChatModel(in);
        }

        public BidRequest_ChatModel[] newArray(int size) {
            return new BidRequest_ChatModel[size];
        }
    };
}
