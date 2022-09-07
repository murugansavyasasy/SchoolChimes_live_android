package com.vs.schoolmessenger.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.HelpActivity;
import com.vs.schoolmessenger.activity.HomeActivity;
import com.vs.schoolmessenger.activity.ProfileLinkScreen;
import com.vs.schoolmessenger.activity.UploadProfileScreen;

import androidx.core.app.ActivityCompat;

/**
 * Created by voicesnap on 9/9/2016.
 */

public class Util_Common {

    public static final int MENU_EMERGENCY = 8201;
    public static final int MENU_VOICE = 8202;
    public static final int MENU_TEXT = 8203;
    public static final int MENU_NOTICE_BOARD = 8204;
    public static final int MENU_EVENTS = 8205;
    public static final int MENU_PHOTOS = 8206;
    public static final int MENU_DOCUMENTS = 8207;
    public static final int MENU_HW = 8208;
    public static final int MENU_EXAM_TEST = 8209;
    public static final int MENU_ATTENDANCE = 8210;
    public static final int MENU_LEAVE_REQUEST = 8211;
    public static final int MENU_TEXT_HW = 8212;
    public static final int MENU_VOICE_HW = 8213;

    //    public static Profiles selectedChildProfile;
    public static String selectedCircularDate;

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (NetworkInfo anInfo : info) {
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SuppressLint("ResourceType")
    public static void popUpMenu(final Activity activity, View v, String type){

        final String ProfileTitle=TeacherUtil_SharedPreference.getProfileTitle(activity);
        final String UploadProfileTitle=TeacherUtil_SharedPreference.getUploadProfileTitle(activity);

        PopupMenu popup = new PopupMenu(activity, v);

        popup.getMenuInflater().inflate(R.menu.settings_popup, popup.getMenu());
        Menu menuOpts = popup.getMenu();
        menuOpts.getItem(0).setTitle(UploadProfileTitle);
        menuOpts.getItem(1).setTitle(ProfileTitle);
        if(type.equals("1")){
            menuOpts.getItem(0).setVisible(true);
            menuOpts.getItem(1).setVisible(true);
        }
        else {
            menuOpts.getItem(0).setVisible(false);
            menuOpts.getItem(1).setVisible(false);
        }

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getTitle().equals("Clear Cache")){
                    HomeActivity object=new HomeActivity();
                    object.deleteCache(activity);
                }

                else  if(item.getTitle().equals("Logout")){
                    HomeActivity object=new HomeActivity();
                    object.showLogoutAlert(activity);
                }
                else  if(item.getTitle().equals(UploadProfileTitle)){
                    Intent profile=new Intent(activity, UploadProfileScreen.class);
                    activity.startActivity(profile);
                }
                else  if(item.getTitle().equals(ProfileTitle)){
                    Intent profile=new Intent(activity, ProfileLinkScreen.class);
                    activity.startActivity(profile);
                }
                else if(item.getTitle().equals("Help")){
                    Intent help=new Intent(activity, HelpActivity.class);
                    activity.startActivity(help);
                }
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        android.net.NetworkInfo networkinfo = cm.getActiveNetworkInfo();
        if (networkinfo != null && networkinfo.isConnected()) {
            return true;
        }
        return false;
    }

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to Minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = "" + minutes;
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}
