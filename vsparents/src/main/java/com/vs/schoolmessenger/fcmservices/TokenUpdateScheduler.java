package com.vs.schoolmessenger.fcmservices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;

public class TokenUpdateScheduler {
    // Schedule a periodic task to check and update the token
    public static void scheduleTokenUpdate(@NonNull Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        // Set the interval for token update (e.g., once a day)
        long intervalMillis = AlarmManager.INTERVAL_DAY;
//        long intervalMillis = 60000L;

        Intent intent = new Intent(context, TokenUpdateReceiver.class);
        PendingIntent resultIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_MUTABLE
            );
        }
        else {
            resultIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        }

        // Set the alarm to trigger at a specific interval
        alarmManager.setInexactRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() + intervalMillis,
                intervalMillis,
                resultIntent
        );
    }
}
