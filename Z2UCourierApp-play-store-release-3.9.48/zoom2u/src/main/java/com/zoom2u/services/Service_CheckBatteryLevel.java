package com.zoom2u.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.z2u.chat.Chat;
import com.z2u.chatview.ChatDetailActivity;
import com.zoom2u.LoginZoomToU;

import static com.z2u.chatview.ChatViewBookingScreen.mFirebaseRef;

/**
 * Created by arun on 14/12/16.
 */

public class Service_CheckBatteryLevel {

    private Context currentActivityContext;

    public Service_CheckBatteryLevel(Context currentActivityContext){
        this.currentActivityContext = currentActivityContext;
        getBatteryLevel();
    }

    //*********** Get battery level to send to admin on fire base ******************//
    private void getBatteryLevel(){
        double level = 0;
        try {
            Intent batteryIntent = currentActivityContext.getApplicationContext().registerReceiver(null,
                    new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int rawlevel = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            double scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            level = -1;
            if (rawlevel >= 0 && scale > 0) {
                level = rawlevel * 100.0 / scale;
            }

            if (level < 10)
                sentChatToAdminForBatteryLow("⚠️ Battery level falling below 10%");
            else if (level < 20)
                sentChatToAdminForBatteryLow("⚠️ Battery level falling below 20%");

            batteryIntent = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //*********** Send battery status to admin on fire base if battery is lower that 10 and 30 % ******************//
    private void sentChatToAdminForBatteryLow(String msgStr){
        Chat chat = new Chat(msgStr,"courier", 0, ChatDetailActivity.sendTimeToServer(), "");
        mFirebaseRef.child(ChatDetailActivity.COURIER_ADMIN_MESSAGE_CHAT+ LoginZoomToU.courierID+"/message").push().setValue(chat);
    }
}