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
            Log.d(TAG, "Notification Message sound: " + remoteMessage.getData().get("sound"));
            Log.d(TAG, "Notification Message tone: " + remoteMessage.getData().get("tone"));
            Log.d(TAG, "Notification Message type: " + remoteMessage.getData().get("type"));
            if (remoteMessage.getData().get("type").equals("isCall")) {
                boolean isDashboardOpen = ScreenState.getInstance().isIncomingCallScreen();
                Log.d("isDashboardOpen", String.valueOf(isDashboardOpen));
                if (!isDashboardOpen) {
                    showNotificationCall(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"),remoteMessage.getData().get("url"));
                } else {
                    Log.d("PLEASE_WAIT", "Already playing the voice call right now so please wait.");
                }
            }
            else {
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
            resultIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_MUTABLE);

        } else {
            resultIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }

        message_voice = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.message);
        emergency_message_voice = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.emergencyvoice);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = "";
            CharSequence name = "";
            if (sound.equals("Enabled")) {
                if (tone.equals("message")) {
                    CHANNEL_ID = "voicesnap_channel_01"; // The id of the channel.
                    name = "Voicesnap"; // The user-visible name of the channel.
                } else if (tone.equals("emergency_voice")) {
                    CHANNEL_ID = "voicesnap_channel_02"; // The id of the channel.
                    name = "vssnap"; // The user-visible name of the channel.
                }
            } else {
                CHANNEL_ID = "voicesnap_channel_03";// The id of the channel.
                name = "snapvoice"; // The user-visible name of the channel.
            }
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            if (sound.equals("Enabled")) {
                if (tone.equals("message")) {
                    mChannel.setSound(message_voice, attributes);
                } else if (tone.equals("emergency_voice")) {
                    mChannel.setSound(emergency_message_voice, attributes);
                }
            } else {
                Uri notificationSoundURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mChannel.setSound(notificationSoundURI, attributes);
            }
            notificationManager.createNotificationChannel(mChannel);

            String GROUP_KEY_WORK_VOICESNAP = "com.vs.schoolmessenger.WORK_VOICESNAP";
            @SuppressLint("NotificationTrampoline") NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContent(getCustomDesign(title, messageBody, ""))
                    .setSmallIcon(R.drawable.school_chimes)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(resultIntent)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setGroup(GROUP_KEY_WORK_VOICESNAP)
                    .setGroupSummary(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            builder.build();

            Config.NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            Log.d("Config.NOTIFICATION_ID", "ID: " + Config.NOTIFICATION_ID);
            notificationManager.notify(Config.NOTIFICATION_ID, builder.build());


        } else {
            @SuppressLint("NotificationTrampoline") NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.school_chimes)
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setSound(message_voice)
                    .setContentIntent(resultIntent);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Config.NOTIFICATION_ID = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            Log.d("Config.NOTIFICATION_ID", "ID: " + Config.NOTIFICATION_ID);
            notificationManager.notify(Config.NOTIFICATION_ID, mNotificationBuilder.build());
        }
    }

    private void showNotificationCall(String title, String message,String url) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "notification_channel";

        int notificationId = (int) System.currentTimeMillis();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            channel.setSound(null, attributes);
            channel.enableVibration(true);
            Util_Common.mediaPlayer = MediaPlayer.create(this, R.raw.schoolchimes_tone);
            Util_Common.mediaPlayer.start();
            notificationManager.createNotificationChannel(channel);
        }

        Log.d("Received", "Received");
        Intent intent = new Intent(this, NotificationCall.class);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("VOICE_URL", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent resultIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            resultIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_MUTABLE);
        } else {
            resultIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);
        }
        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.call_notification);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.lblContent, message);

        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.school_chimes_trans_logo)
                .setContentIntent(resultIntent)
                .setAutoCancel(true)
                .setOngoing(false)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(this, R.color.clr_white))
                .build();
        notification.bigContentView = remoteViews;
        notification.headsUpContentView = remoteViews;
        notificationManager.notify(notificationId, notification);
    }
}