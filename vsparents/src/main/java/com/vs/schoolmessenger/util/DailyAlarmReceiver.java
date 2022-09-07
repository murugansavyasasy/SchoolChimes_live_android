package com.vs.schoolmessenger.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.vs.schoolmessenger.fcmservices.Config;

public class DailyAlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DailyAlarmReceiver","Received");
        Toast.makeText(context, "Received ", Toast.LENGTH_SHORT).show();

        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        Log.d( "Daily Firebase reg id: " , regId);
    }
}