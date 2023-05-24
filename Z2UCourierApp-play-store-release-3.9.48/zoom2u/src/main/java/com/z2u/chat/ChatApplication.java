package com.z2u.chat;

import android.content.Context;
import androidx.multidex.MultiDex;


public class ChatApplication extends android.app.Application {

    // Singleton instance
    private static ChatApplication sInstance = null;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup singleton instance
        sInstance = this;
    }

    // Getter to access Singleton instance
    public static ChatApplication getInstance() {
        return sInstance;
    }

    // ********** Code to stack trace the Dean taylor courier issue on Bid notification *************
//    public void writeLogs() {
//        try {
//            if (isExternalStorageWritable()) {
//                File appDirectory = new File(Environment.getExternalStorageDirectory() + "/Zoom2uLogs");
//                File logDirectory = new File(appDirectory + "/log");
//                File logFile = new File(logDirectory, "logcat_" + System.currentTimeMillis() + ".txt");
//                // create app folder
//                if (!appDirectory.exists()) {
//                    appDirectory.mkdir();
//                }
//                // create log folder
//                if (!logDirectory.exists()) {
//                    logDirectory.mkdir();
//                }
//                // clear the previous logcat and then write the new one to the file
//                try {
//                    Process process = Runtime.getRuntime().exec("logcat - c");
//                    process = Runtime.getRuntime().exec("logcat -f " + logFile);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else if (isExternalStorageReadable()) {
//                // only readable
//            } else {
//                // not accessible
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error while createing log file", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    /* Checks if external storage is available for read and write */
//    private boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            return true;
//        }
//        return false;
//    }
//
//    /* Checks if external storage is available to at least read */
//    private boolean isExternalStorageReadable() {
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state) ||
//                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            return true;
//        }
//        return false;
//    }
}
