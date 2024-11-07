package com.vs.schoolmessenger.fcmservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.vs.schoolmessenger.activity.TeacherSplashScreen;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("NotificationReceiver", "Notification clicked!");

        // Handle the notification click here
        if (intent != null && intent.getBooleanExtra("from_notification", false)) {
            Log.d("NotificationReceiver", "Notification clicked from BroadcastReceiver!");
            // You can start an activity if needed
            Intent splashIntent = new Intent(context, TeacherSplashScreen.class);
            splashIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(splashIntent);

        }
    }
}
