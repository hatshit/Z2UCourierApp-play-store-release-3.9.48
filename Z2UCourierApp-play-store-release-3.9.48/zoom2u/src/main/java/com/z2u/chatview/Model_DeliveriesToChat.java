package com.z2u.chatview;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import com.zoom2u.ConfirmPickUpForUserName;
import com.zoom2u.LoginZoomToU;
import com.zoom2u.R;
import com.zoom2u.SlideMenuZoom2u;
import com.zoom2u.slidemenu.RequestView;
import com.zoom2u.slidemenu.offerrequesthandlr.RequestView_DetailPojo;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class Model_DeliveriesToChat implements Parcelable {

    public static String BOOKING_ID_CHAT = "";

    private int bookingId;
    private String bookingRef;
    private String customerId;
    private String courierId;
    private String customer;
    private String pickupSuburb;
    private String dropSuburb;
    private String dropDateTime;
    private String lastUpdatedTime;
    private int unreadMsgCountOfCourier;
    private int unreadMsgCountOfCustomer;
    private String customerMobile;
    private String dropETA;
    boolean isBookingChatModelInit = false;
    private ValueEventListener valueEventListnerOfCourierChat;
    private ValueEventListener valueEventListnerOfCustomerChat;
    private String CustomerPhoto;
    private String vehicle;
    Context context;
    boolean isNotificationSoundPlay = false;
    String PickupDateTime;
    String PickupETA;

    public Model_DeliveriesToChat(){
    }

    public Model_DeliveriesToChat(Context context, JSONObject jObjOfChatDelivery) {
        super();
        try {
            this.context = context;
            isBookingChatModelInit = true;
            this.bookingId = jObjOfChatDelivery.getInt("BookingId");
            this.bookingRef = jObjOfChatDelivery.getString("BookingRef");
            this.customerId = jObjOfChatDelivery.getString("CustomerId");
            this.courierId = jObjOfChatDelivery.getString("CourierId");
            this.customer = jObjOfChatDelivery.getString("Customer").trim();
            this.pickupSuburb = jObjOfChatDelivery.getString("PickupSuburb");
            this.dropSuburb = jObjOfChatDelivery.getString("DropSuburb");
            this.dropDateTime = jObjOfChatDelivery.getString("DropDateTime");
            this.lastUpdatedTime = "";
            this.unreadMsgCountOfCourier = 0;
            this.unreadMsgCountOfCustomer = 0;
            this.customerMobile = jObjOfChatDelivery.getString("CustomerMobile");
            this.dropETA = jObjOfChatDelivery.getString("DropETA");
            this.vehicle = jObjOfChatDelivery.getString("Vehicle");
            this.CustomerPhoto = jObjOfChatDelivery.getString("CustomerImage");
            this.PickupDateTime = jObjOfChatDelivery.getString("PickupDateTime");
            this.PickupETA= jObjOfChatDelivery.getString("PickupETA");
            getUnreadMsgCountOfBookingId(ChatDetailActivity.CUSTOMER_COURIER_BOOKINGCHAT + jObjOfChatDelivery.getInt("BookingId") + "_"
                    + jObjOfChatDelivery.getString("CourierId") + "/status", "/customer");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Model_DeliveriesToChat(Context context, String courierIdStr, String bookingRefTxt) {
        super();
        this.context = context;
        isBookingChatModelInit = true;
        this.bookingId = 0;
        this.bookingRef ="";
        this.customerId = "";
        this.courierId = courierIdStr;
        this.customer = bookingRefTxt;
        this.pickupSuburb = "";
        this.dropSuburb = "";
        this.dropDateTime = "";
        this.lastUpdatedTime = "";
        this.unreadMsgCountOfCourier = 0;
        this.unreadMsgCountOfCustomer = 0;
        this.customerMobile = "1300 318 675";
        this.dropETA = "";
        this.vehicle="";
        this.CustomerPhoto="";
        this.PickupETA="";
        this.PickupDateTime="";
        getUnreadMsgCountOfBookingId(ChatDetailActivity.COURIER_ADMIN_UNREADS + courierIdStr + "/status", "/admin");
    }

    public ValueEventListener getValueEventListnerOfCourierChat() {
        return valueEventListnerOfCourierChat;
    }

    public ValueEventListener getValueEventListnerOfCustomerChat() {
        return valueEventListnerOfCustomerChat;
    }

    public static String getBookingIdChat() {
        return BOOKING_ID_CHAT;
    }

    public static void setBookingIdChat(String bookingIdChat) {
        BOOKING_ID_CHAT = bookingIdChat;
    }

    public String getCustomerPhoto() {
        return CustomerPhoto;
    }

    public void setCustomerPhoto(String customerPhoto) {
        CustomerPhoto = customerPhoto;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDropETA() {
        return dropETA;
    }

    public String getCustomerMobile() {
        return customerMobile;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public String getCourierId() {
        return courierId;
    }

    public String getCustomer() {
        return customer;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDropSuburb() {
        return dropSuburb;
    }

    public String getPickupSuburb() {
        return pickupSuburb;
    }

    public String getDropDateTime() {
        return dropDateTime;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public int getUnreadMsgCountOfCourier() {
        return unreadMsgCountOfCourier;
    }

    public int getUnreadMsgCountOfCustomer() {
        return unreadMsgCountOfCustomer;
    }

    public String getPickupDateTime() {
        return PickupDateTime;
    }

    public String getPickupETA() {
        return PickupETA;
    }

    private void addMessageEventListnerOfCourierChat(String childUrlForStatusUpdate){
        valueEventListnerOfCourierChat = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                try {
                    long unreadMsgAdmin = 0;
                    if (arg0.getValue() != null) {
                        unreadMsgAdmin = (long) arg0.getValue();
                        /* Get unread message count of courier message to show unread count in list  */
                        unreadMsgCountOfCourier = (int) unreadMsgAdmin;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {
            }
        };
        ChatViewBookingScreen.mFirebaseRef.child(childUrlForStatusUpdate+"/courier/unread").addValueEventListener(valueEventListnerOfCourierChat);
    }

    private void addMessageEventListnerOfCustomerChat(final String childUrlForStatusUpdate, String isCustomerOrAdminTxt){
        valueEventListnerOfCustomerChat = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot arg0) {
                if (arg0.getValue() != null) {
                    long unreadCount = 0;
                    unreadCount = (long) arg0.getValue();
                    /* Get unread message count of customer or admin and update it to server if courier sent message to this perticular customer  */
                    unreadMsgCountOfCustomer = (int) unreadCount;
                    if (unreadMsgCountOfCustomer > 0) {
                        Log.e("---------", " ---isBookingChatModelInit--- "+isBookingChatModelInit+""+" ---isNotificationSoundPlay--- "+isNotificationSoundPlay);
                        if (!isBookingChatModelInit && !isNotificationSoundPlay) {
                            isNotificationSoundPlay = true;
                            valueEventListnerForNotification(childUrlForStatusUpdate);         //*** Add value event listner for Notification ****
                        } else
                            isBookingChatModelInit = false;
                    }else
                        isBookingChatModelInit = false;

                    if (ChatViewBookingScreen.chatListAdapter != null)
                        ChatViewBookingScreen.chatListAdapter.notifyDataSetChanged();

                    showExclamationForUnreadChat(SlideMenuZoom2u.countChatBookingView);
                }
            }

            @Override
            public void onCancelled(DatabaseError arg0) {

            }
        };
        ChatViewBookingScreen.mFirebaseRef.child(childUrlForStatusUpdate+isCustomerOrAdminTxt+"/unread").addValueEventListener(valueEventListnerOfCustomerChat);
    }

    private void valueEventListnerForNotification(String childUrlForStatusUpdate) {
        final ValueEventListener valueEventListenerOfNotificcation;
        valueEventListenerOfNotificcation = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    Chat post = postSnapShot.getValue(Chat.class);
                    try {
                        if (!post.getSender().equalsIgnoreCase("courier")) {
                           if (bookingId != 0)
                               BOOKING_ID_CHAT = bookingId+"_"+LoginZoomToU.courierID;
                           else
                               BOOKING_ID_CHAT = LoginZoomToU.courierID;
                           if (ChatDetailActivity.BOOKING_CHAT_NODE.equals("") && !ChatDetailActivity.BOOKING_CHAT_NODE.equals(BOOKING_ID_CHAT)) {
                                Bundle bModelDeliveryChat = new Bundle();
                                bModelDeliveryChat.putParcelable("ModelDeliveryChatItem", Model_DeliveriesToChat.this);
                                Intent notificationIntent = new Intent(context, ChatDetailActivity.class);
                                notificationIntent.putExtras(bModelDeliveryChat);

                                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                notificationIntent.setAction(Intent.ACTION_MAIN);
                                notificationIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                                int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
                                PendingIntent pendingIntent = PendingIntent.getActivity(context, uniqueInt, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

                               NotificationManager mNotificationManager =
                                       (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                               NotificationCompat.Builder builder;

                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                   String id = "Z2U Channel";
                                   int importance = NotificationManager.IMPORTANCE_HIGH;
                                   NotificationChannel mChannel = new NotificationChannel(id, context.getString(R.string.app_name), importance);
                                   mChannel.enableVibration(true);
                                   mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                                   mNotificationManager.createNotificationChannel(mChannel);

                                   builder = new NotificationCompat.Builder(context, id)
                                           .setContentTitle(context.getString(R.string.app_name))                            // required
                                           .setSmallIcon(getNotificationIcon())   // required
                                           .setContentText(post.getMessage()) // required
                                           .setDefaults(Notification.DEFAULT_ALL)
                                           .setAutoCancel(true)
                                           .setContentIntent(pendingIntent)
                                           .setSound(Uri.parse("android.resource://com.zoom2u/" + R.raw.chatnotification))
                                           .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                                           .setTicker(post.getMessage())
                                           .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                               } else {
                                   builder = new NotificationCompat.Builder(context)
                                           .setSound(Uri.parse("android.resource://com.zoom2u/" + R.raw.chatnotification))
                                           .setContentTitle("Zoom2u")
                                           .setContentText(customer + " - " + post.getMessage())
                                           .setStyle(new NotificationCompat.InboxStyle()
                                                   .addLine(customer)
                                                   .addLine(post.getMessage()))
                                           .setGroup("Message")
                                           .setContentIntent(pendingIntent)
                                           .setSmallIcon(getNotificationIcon())
                                           .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                                           .setAutoCancel(true)
                                           .setOngoing(true)
                                           .setChannelId("Z2U Channel");

                                   if (android.os.Build.VERSION.SDK_INT >= 21) {
                                       builder.setColor(context.getResources().getColor(R.color.colorAccent))
                                               .setVisibility(NotificationCompat.VISIBILITY_PRIVATE);
                                   }
                               }

                               Notification notification = builder.build();
                               if (bookingId != 0)
                                   mNotificationManager.notify(bookingId, notification);
                               else
                                   mNotificationManager.notify(3, notification);
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

        try {
            final String newUrl = childUrlForStatusUpdate.replace("/status", "").trim();
            if (newUrl.contains(ChatDetailActivity.COURIER_ADMIN_UNREADS)) {
                final String adminCourierMsgChatUrl = newUrl.replace(ChatDetailActivity.COURIER_ADMIN_UNREADS, ChatDetailActivity.COURIER_ADMIN_MESSAGE_CHAT);
                ChatViewBookingScreen.mFirebaseRef.child(adminCourierMsgChatUrl + "/message").limitToLast(1).addValueEventListener(valueEventListenerOfNotificcation);
                Timer removeTaskTimer = new Timer();
                removeTaskTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ChatViewBookingScreen.mFirebaseRef.child(adminCourierMsgChatUrl + "/message").limitToLast(1).removeEventListener(valueEventListenerOfNotificcation);
                    }
                }, 500);
            } else {
                ChatViewBookingScreen.mFirebaseRef.child(newUrl + "/message").limitToLast(1).addValueEventListener(valueEventListenerOfNotificcation);
                Timer removeTaskTimer = new Timer();
                removeTaskTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ChatViewBookingScreen.mFirebaseRef.child(newUrl + "/message").limitToLast(1).removeEventListener(valueEventListenerOfNotificcation);
                    }
                }, 500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //    notificationSoundForChat(context, R.raw.chatnotification);
        isNotificationSoundPlay = false;
    }

    //************ Show exclamation for unread chat ************
    public static void showExclamationForUnreadChat(TextView countChatBookingView) {

        if (ConfirmPickUpForUserName.isDropOffSuccessfull != 13){
            showChatExclamation(countChatBookingView);
        } else if (ConfirmPickUpForUserName.isDropOffSuccessfull == 13
                && RequestView.requestView_pojoList!=null&&RequestView.requestView_pojoList.size() == 0) {
            showChatExclamation(countChatBookingView);
        }
    }

    private static void showChatExclamation(TextView countChatBookingView) {
        if (countChatBookingView != null) {
            int totalUnreadCount = 0;
            try {
                for (Model_DeliveriesToChat modelDeliveryChat : LoadChatBookingArray.arrayOfChatDelivery)
                    totalUnreadCount = totalUnreadCount + modelDeliveryChat.getUnreadMsgCountOfCustomer();
                for (RequestView_DetailPojo requestView_detailPojo : RequestView.requestView_pojoList)
                    totalUnreadCount = (int) (totalUnreadCount + requestView_detailPojo.getBidRequest_chatModel().getUnreadBidChatCountForCustomer());

                if (totalUnreadCount > 0) {
                    countChatBookingView.setText("!");
                    countChatBookingView.setVisibility(View.VISIBLE);
                } else
                    countChatBookingView.setVisibility(View.GONE);
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(bookingId);
        parcel.writeString(bookingRef);
        parcel.writeString(customerId);
        parcel.writeString(courierId);
        parcel.writeString(customer);
        parcel.writeString(pickupSuburb);
        parcel.writeString(dropSuburb);
        parcel.writeString(dropDateTime);
        parcel.writeString(lastUpdatedTime);
        parcel.writeInt(unreadMsgCountOfCourier);
        parcel.writeInt(unreadMsgCountOfCustomer);
        parcel.writeString(customerMobile);
        parcel.writeString(dropETA);
        parcel.writeString(PickupDateTime);
        parcel.writeString(PickupETA);
    }

    public static final Creator<Model_DeliveriesToChat> CREATOR = new Creator<Model_DeliveriesToChat>() {
        public Model_DeliveriesToChat createFromParcel(Parcel in) {
            return new Model_DeliveriesToChat(in);
        }

        public Model_DeliveriesToChat[] newArray(int size) {
            return new Model_DeliveriesToChat[size];
        }
    };

    private Model_DeliveriesToChat(Parcel in) {
        bookingId = in.readInt();
        bookingRef = in.readString();
        customerId = in.readString();
        courierId = in.readString();
        customer = in.readString();
        pickupSuburb = in.readString();
        dropSuburb = in.readString();
        dropDateTime = in.readString();
        lastUpdatedTime = in.readString();
        unreadMsgCountOfCourier = in.readInt();
        unreadMsgCountOfCustomer = in.readInt();
        customerMobile = in.readString();
        dropETA = in.readString();
        PickupDateTime = in.readString();
        PickupETA = in.readString();
    }

    void getUnreadMsgCountOfBookingId(String childUrlForStatusUpdate, String isCustomerOrAdminTxt) {
        addMessageEventListnerOfCourierChat(childUrlForStatusUpdate);
        addMessageEventListnerOfCustomerChat(childUrlForStatusUpdate, isCustomerOrAdminTxt);
    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notification_icon : R.drawable.app_icon;
    }

    public static void notificationSoundForChat(Context activitycontext, int notificationSound){
//        NotificationManager notificationManager;
//        Notification myNotification;
//        myNotification=new NotificationCompat.Builder(activitycontext)
//                .setSound(Uri.parse("android.resource://com.zoom2u/"+notificationSound))
//                .setAutoCancel(true)
//                .build();
//
//        notificationManager = (NotificationManager)activitycontext.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(10000000, myNotification);
//        notificationManager = null;
//        myNotification = null;

        try {
            MediaPlayer mediaPlayer = MediaPlayer.create(activitycontext, notificationSound);
            mediaPlayer.start();
            mediaPlayer = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
