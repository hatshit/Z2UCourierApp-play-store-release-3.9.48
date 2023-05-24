package com.zoom2u.utility;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

import com.z2u.chatview.Model_DeliveriesToChat;
import com.zoom2u.R;

public class Notification_Utils {

    public static String soundUri = "android.resource://com.zoom2u/" + R.raw.chatnotification;

    public static NotificationCompat.Builder notificationBuilderFor_Oreo (Context context, NotificationManager mNotificationManager, PendingIntent pendingIntent,
                                                                          String notificationTitle, String notificationMsgStr, String soundURI){
        String id = "Z2U Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, notificationTitle, importance);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mNotificationManager.createNotificationChannel(mChannel);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setContentTitle(notificationTitle)                            // required
                .setSmallIcon(Model_DeliveriesToChat.getNotificationIcon())   // required
                .setContentText(notificationMsgStr)
//                                            .setStyle(new NotificationCompat.InboxStyle()
//                                                    .addLine(customerName)
//                                                    .addLine(post.getMessage()))
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(Uri.parse(soundURI))
                .setBadgeIconType(NotificationCompat.BADGE_ICON_LARGE)
                .setTicker(notificationMsgStr)
                .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        return builder;
    }
}
