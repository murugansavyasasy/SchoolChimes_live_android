package com.vs.schoolmessenger.fcmservices;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherSplashScreen;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.util.ScreenState;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by devi on 5/9/2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    public static ArrayList<Profiles> pubStArrChildList = new ArrayList<>();
    NotificationChannel mChannel;
    Uri message_voice = null;
    Uri emergency_message_voice = null;
    private Handler handler = new Handler();

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed New token: " + token);
        // If you want to send messages to this application instance or
        storeRegIdInPref(token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "ReceiverFrom: " + remoteMessage.getFrom());
        Log.d(TAG, "ReceiverFrom: " + remoteMessage.getData());
        if (remoteMessage.getData().get("body") != null) {
            if (remoteMessage.getData().get("type").equals("isCall")) {
                Log.d(TAG, "Notification Message type: " + remoteMessage.getData().get("type"));
                boolean isCallScreen = ScreenState.getInstance().isIncomingCallScreen();
                Log.d("isDashboardOpen", String.valueOf(isCallScreen));
                if (!isCallScreen) {
                    showNotificationCall(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), remoteMessage.getData().get("url"), remoteMessage.getData().get("receiver_id"),
                            remoteMessage.getData().get("retrycount"), remoteMessage.getData().get("circular_id"), remoteMessage.getData().get("ei1"), remoteMessage.getData().get("ei2"), remoteMessage.getData().get("ei3"), remoteMessage.getData().get("ei4")
                            , remoteMessage.getData().get("ei5"), remoteMessage.getData().get("role"), remoteMessage.getData().get("menu_id"));
                } else {
                    Log.d("PLEASE_WAIT", "Already playing the voice call right now so please wait.");
                }
            } else {
                createNotification(remoteMessage.getData().get("body"), remoteMessage.getData().get("title"), remoteMessage.getData().get("sound"), remoteMessage.getData().get("tone"));
            }
        }
    }

    private RemoteViews getCustomDesign(String title, String message, String type) {
        @SuppressLint("RemoteViewLayout") RemoteViews remoteViews = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.custom_notification);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        return remoteViews;
    }

    private void createNotification(String messageBody, String title, String sound, String tone) {
        Log.d("Received", "Received");

        Intent intent = new Intent(this, TeacherSplashScreen.class);
        intent.putExtra("CHILD_LIST", pubStArrChildList);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        } else {
            resultIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

        Uri messageVoice = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.message);
        Uri emergencyMessageVoice = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.emergencyvoice);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID;
            CharSequence channelName;
            Uri notificationSound = null;

            if ("Enabled".equals(sound)) {
                if ("message".equals(tone)) {
                    CHANNEL_ID = "savyasasy_channel_01";
                    channelName = "savyasasy";
                    notificationSound = messageVoice;
                } else if ("emergency_voice".equals(tone)) {
                    CHANNEL_ID = "savyasasy_channel_02";
                    channelName = "ssssasy";
                    notificationSound = emergencyMessageVoice;
                } else {
                    throw new IllegalArgumentException("Invalid tone provided");
                }
            } else {
                CHANNEL_ID = "savyasasy_channel_03";
                channelName = "savyasasyvoice";
                notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
                channel.enableVibration(true);
                if (notificationSound != null) {
                    AudioAttributes attributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                            .build();
                    channel.setSound(notificationSound, attributes);
                }
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.school_chimes)
                    .setContentTitle(title)
//                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContent(getCustomDesign(title, messageBody, ""))
                    .setCustomContentView(getCustomDesign(title, messageBody, ""))
                    .setCustomBigContentView(getCustomDesign(title, messageBody, ""))
                    .setCustomHeadsUpContentView(getCustomDesign(title, messageBody, ""))
                    .setContentIntent(resultIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);

            Config.NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.school_chimes)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(messageVoice)
                    .setContentIntent(resultIntent);

            Config.NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            notificationManager.notify(Config.NOTIFICATION_ID, builder.build());
        }
    }


    private void showNotificationCall(String title, String body, String url, String receiverId, String retrycount, String circularId, String ei1, String ei2, String ei3, String ei4, String ei5, String role, String menuId) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notification_channel";

        // Generate a unique notification ID using current time in milliseconds
        int notificationId = Integer.parseInt(ei5);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Set audio attributes for the notification sound
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("My Notification Channel");
            channel.setSound(null, attributes);  // Disabling default sound
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (!Util_Common.mediaPlayer.isPlaying()) {
                Util_Common.mediaPlayer = MediaPlayer.create(this, R.raw.call_notification);
                Util_Common.mediaPlayer.setLooping(true);
                Util_Common.mediaPlayer.start();
            }
            notificationManager.createNotificationChannel(channel);

            handler.postDelayed(stopMediaPlayerRunnable, 30000);
        }

        Log.d("Received", "Notification Received");

        // Create an Intent for the NotificationCall Activity
        Intent intent = new Intent(this, NotificationCall.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("isNotificationId", notificationId);
        intent.putExtra("isVoiceUrl", url);
        intent.putExtra("isReceiverId", receiverId);
        intent.putExtra("retrycount", retrycount);
        intent.putExtra("circularId", circularId);
        intent.putExtra("ei1", ei1);
        intent.putExtra("ei2", ei2);
        intent.putExtra("ei3", ei3);
        intent.putExtra("ei4", ei4);
        intent.putExtra("ei5", ei5);
        intent.putExtra("role", role);
        intent.putExtra("menuId", menuId);

        // Create a unique PendingIntent for each notification
        PendingIntent resultIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultIntent = PendingIntent.getActivity(this, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            resultIntent = PendingIntent.getActivity(this, notificationId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
        }

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_call_notification);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.lblContent, body);

        // Create the notification
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.school_chimes_trans_logo)  // Replace with your icon
                .setContentIntent(resultIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.clr_white))
                .setDeleteIntent(createDeleteIntent()) // Add delete intent for dismissal
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCustomContentView(remoteViews)
                .build();

        notification.contentView = remoteViews;
        notification.bigContentView = remoteViews;
        notification.headsUpContentView = remoteViews;

        // Show the notification with the unique ID
        notificationManager.notify(notificationId, notification);
    }

    private Runnable stopMediaPlayerRunnable = () -> {
        if (Util_Common.mediaPlayer != null && Util_Common.mediaPlayer.isPlaying()) {
            Util_Common.mediaPlayer.stop();
            Util_Common.mediaPlayer.release();
            Util_Common.mediaPlayer = new MediaPlayer();
        }
    };

    // Creates a delete intent for handling notification dismissal
    private PendingIntent createDeleteIntent() {
        Intent dismissIntent = new Intent(this, NotificationDismissService.class);
        dismissIntent.setAction("NOTIFICATION_DISMISSED");
        return PendingIntent.getService(
                this,
                0,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
}