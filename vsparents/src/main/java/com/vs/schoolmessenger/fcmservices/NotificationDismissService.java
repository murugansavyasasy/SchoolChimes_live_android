package com.vs.schoolmessenger.fcmservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.vs.schoolmessenger.util.Util_Common;

public class NotificationDismissService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && "NOTIFICATION_DISMISSED".equals(intent.getAction())) {
            Log.d("Notification", "Notification was dismissed");
            Util_Common.mediaPlayer.stop();
        }
        stopSelf();
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
