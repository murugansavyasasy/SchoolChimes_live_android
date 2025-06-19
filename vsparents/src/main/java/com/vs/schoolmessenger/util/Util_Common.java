package com.vs.schoolmessenger.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.HelpActivity;
import com.vs.schoolmessenger.activity.HomeActivity;
import com.vs.schoolmessenger.activity.ProfileLinkScreen;
import com.vs.schoolmessenger.activity.TeacherSplashScreen;
import com.vs.schoolmessenger.activity.UploadProfileScreen;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.model.StaffMsgMangeCount;

import java.util.ArrayList;
import java.util.List;

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
    public static int isPosition = 0;
    public static int isSchoolType = 0;
    public static int isPositionSection = 0;
    public static int isType = 1;
    public static int isRemovingId = 1;
    public static String isDate = "";
    public static String isAttendanceClass = "Category";
    public static String isCurrentDate = "";
    public static String isStartTime = "";
    public static String isEndTime = "";

    //    public static Profiles selectedChildProfile;
    public static String selectedCircularDate;
    public static Boolean isScheduleCall = false;
    public static Boolean isChooseDate = true;
    public static Boolean isHistory = false;
    public static ArrayList<String> isSelectedDate = new ArrayList<String>();
    public static ArrayList<Integer> overlappingSlots = new ArrayList<>(); // Track overlapping slots by ID
    public static ArrayList<Integer> isSelectedSlotIds = new ArrayList<>();
    public static int isSpecificSlot = -1;
    public static int isSpecificSlotId = -1;
    public static ArrayList<String> isSelectedTime = new ArrayList<>();
    public static ArrayList<String> isHeaderSlotsIds = new ArrayList<>();
    public static List<String> isSelectedDateList = new ArrayList<>();
    public static ArrayList<Integer> isBookedIds = new ArrayList<>();
    public static Boolean isDataLoadingOver = true;
    public static String isVideoSize = "";
    public static ArrayList<StaffMsgMangeCount> isStaffMsgMangeCount = new ArrayList<StaffMsgMangeCount>();
    public static int isStaffMsgFromManagementCount = 0;
    public static MediaPlayer mediaPlayer = new MediaPlayer();



    public static String VIEW_PROGRESS_CARD_POINTS  = "VIEW_PROGRESS_CARD";
    public static String VIEW_EXAM_SCHUDLE_POINTS = "VIEW_EXAM_SCHUDLE";
    public static String  VIEW_EXAM_MARK_POINTS = "VIEW_EXAM_MARK";
    public static String   VIEW_EVENTS_POINTS = "VIEW_EVENTS";
    public static String SUBMIT_ASSIGNMENT_POINTS = "SUBMIT_ASSIGNMENT";
    public static String  APPLY_LEAVE_POINTS = "APPLY_LEAVE";
    public static String READ_MESSAGE_POINTS = "READ_MESSAGE";
    public static String HOMEWORK_POINTS = "HOMEWORK";
    public static String LISTEN_VOICE_POINTS = "LISTEN_VOICE";
    public static String VIEW_ASSIGNMENT_POINTS = "VIEW_ASSIGNMENT";
    public static String VIEW_IMAGE_PDF_POINTS = "VIEW_IMAGE_PDF";
    public static String EXAMMARKS_POINTS = "EXAMMARKS";
    public static String VIEW_VIDEO_POINTS = "VIEW_VIDEO";
    public static String LOGIN_POINTS = "LOGIN";
    public static String TEXT_POINTS = "TEXT";
    public static String  VOICE_POINTS = "VOICE";



    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

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

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @SuppressLint("ResourceType")
    public static void popUpMenu(final Activity activity, View v, String type) {

        final String ProfileTitle = TeacherUtil_SharedPreference.getProfileTitle(activity);
//        final String UploadProfileTitle = TeacherUtil_SharedPreference.getUploadProfileTitle(activity);
        final String UploadProfileTitle = activity.getString(R.string.Upload_profile_Documents);;

        PopupMenu popup = new PopupMenu(activity, v);

        popup.getMenuInflater().inflate(R.menu.settings_popup, popup.getMenu());
        Menu menuOpts = popup.getMenu();
//        menuOpts.getItem(1).setTitle(UploadProfileTitle);
        //  menuOpts.getItem(1).setTitle(ProfileTitle);
        if (type.equals("1")) {
            menuOpts.getItem(0).setVisible(true);
            //menuOpts.getItem(1).setVisible(true);
        } else {
            menuOpts.getItem(0).setVisible(false);
            // menuOpts.getItem(1).setVisible(false);
        }
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String selectedTitle = String.valueOf(item.getTitle());
                String clearCacheTitle = activity.getString(R.string.Clear_Cache);
                String logoutTitle = activity.getString(R.string.Logout);
                String helpTitle = activity.getString(R.string.Help);
                String changeLanguageTitle = activity.getString(R.string.Change_Language);

                Log.d("Selected Title", selectedTitle);

                if (selectedTitle.equals(clearCacheTitle)) {
                    HomeActivity object = new HomeActivity();
                    object.deleteCache(activity);
                } else if (selectedTitle.equals(logoutTitle)) {
                    HomeActivity object = new HomeActivity();
                    object.showLogoutAlert(activity);
                } else if (selectedTitle.equals(UploadProfileTitle)) {
                    Intent profile = new Intent(activity, UploadProfileScreen.class);
                    activity.startActivity(profile);
                } else if (selectedTitle.equals(ProfileTitle)) {
                    Intent profile = new Intent(activity, ProfileLinkScreen.class);
                    activity.startActivity(profile);
                } else if (selectedTitle.equals(helpTitle)) {
                    Intent help = new Intent(activity, HelpActivity.class);
                    activity.startActivity(help);
                } else if (selectedTitle.equals(changeLanguageTitle)) {
                    showLanguageSelectorDialog(activity);
                }
                return true;
            }
        });


        popup.show();//showing popup menu
    }


    private static void showLanguageSelectorDialog(Activity activity) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_language_selector, null);
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        final String[] isSelectedLanguage = {null}; // Wrapper for isSelectedLanguage
        final boolean[] isChecking = {false}; // Wrapper for isChecking

        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        RelativeLayout rlaEnglish = dialogView.findViewById(R.id.rlaEnglish);
        RelativeLayout rlaThai = dialogView.findViewById(R.id.rlaThai);
        CheckBox chEnglish = dialogView.findViewById(R.id.chEnglish);
        CheckBox chThai = dialogView.findViewById(R.id.chThai);
        TextView btnConfirm = dialogView.findViewById(R.id.btnConfirm);
        ImageView imgEnglish = dialogView.findViewById(R.id.imgEnglish);
        ImageView imgThai = dialogView.findViewById(R.id.imgThai);

        RelativeLayout rlaHindi = dialogView.findViewById(R.id.rlaHindi);
        CheckBox chHindi = dialogView.findViewById(R.id.chHindi);
        ImageView imgHindi = dialogView.findViewById(R.id.imgHindi);

        isRemoveCheckBox(chThai, chEnglish, chHindi);


        rlaHindi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRemoveCheckBox(chThai, chEnglish, chHindi);
                isChecking[0] = true;
                isSelectedLanguage[0] = "hi";
                chHindi.setChecked(true);
                isSelectedImageSetting(imgHindi, imgEnglish, imgThai, imgHindi);
            }
        });


        rlaEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRemoveCheckBox(chThai, chEnglish, chHindi);
                isChecking[0] = true;
                isSelectedLanguage[0] = "en";
                chEnglish.setChecked(true);
                isSelectedImageSetting(imgEnglish, imgEnglish, imgThai, imgHindi);
            }
        });

        rlaThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isRemoveCheckBox(chThai, chEnglish, chHindi);
                isChecking[0] = true;
                isSelectedLanguage[0] = "th";
                chThai.setChecked(true);
                isSelectedImageSetting(imgThai, imgEnglish, imgThai, imgHindi);
            }
        });


        chHindi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRemoveCheckBox(chThai, chEnglish, chHindi);
            if (isChecked) {
                isChecking[0] = true;
                isSelectedLanguage[0] = "hi";
                chHindi.setChecked(true);
                isSelectedImageSetting(imgHindi, imgEnglish, imgThai, imgHindi);
            } else {
                isChecking[0] = false;
            }
        });

        chEnglish.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRemoveCheckBox(chThai, chEnglish, chHindi);
            if (isChecked) {
                isChecking[0] = true;
                isSelectedLanguage[0] = "en";
                chEnglish.setChecked(true);
                isSelectedImageSetting(imgEnglish, imgEnglish, imgThai, imgHindi);
            } else {
                isChecking[0] = false;
            }
        });

        chThai.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isRemoveCheckBox(chThai, chEnglish, chHindi);
            if (isChecked) {
                isChecking[0] = true;
                isSelectedLanguage[0] = "th";
                chThai.setChecked(true);
                isSelectedImageSetting(imgThai, imgEnglish, imgThai, imgHindi);
            } else {
                isChecking[0] = false;
            }
        });

        String isAppLanguage = TeacherUtil_SharedPreference.getLanguageType(activity);
        Log.d("isAppLanguage", isAppLanguage != null ? isAppLanguage : "null");
        if (isAppLanguage == null || isAppLanguage.isEmpty()) {
            isAppLanguage = "en";
        }

        switch (isAppLanguage) {
            case "th":
                chThai.setChecked(true);
                break;
            case "hi":
                chHindi.setChecked(true);
                break;
            case "en":
            default:
                chEnglish.setChecked(true);
                break;
        }

        btnConfirm.setOnClickListener(v -> {
            if (isChecking[0]) {
                isChecking[0] = false;
                TeacherUtil_SharedPreference.putLanguageType(activity, isSelectedLanguage[0]);
                refreshActivity(activity,isSelectedLanguage[0]);
                alertDialog.dismiss();
            } else {
                Toast.makeText(activity, isSelectedLanguage[0], Toast.LENGTH_SHORT).show();
            }
        });

        imgClose.setOnClickListener(v -> {
            isChecking[0] = false;
            alertDialog.dismiss();
        });

        alertDialog.show();
    }


    private static void isRemoveCheckBox(CheckBox chThai, CheckBox chEnglish, CheckBox chHindi) {
        chEnglish.setChecked(false);
        chThai.setChecked(false);
        chHindi.setChecked(false);
    }


    private static void isSelectedImageSetting(ImageView isSelectedImage, ImageView imgEnglish, ImageView imgThai, ImageView imgHindi) {
        imgEnglish.setImageResource(R.drawable.en_language_gray);
        imgThai.setImageResource(R.drawable.th_language_gray);
        imgHindi.setImageResource(R.drawable.hi_language_gray);

        if (isSelectedImage == imgEnglish) {
            imgEnglish.setImageResource(R.drawable.en_language_orange);
        } else if (isSelectedImage == imgHindi) {
            imgHindi.setImageResource(R.drawable.hi_language_orange);
        } else {
            imgThai.setImageResource(R.drawable.th_language_orange);
        }
    }


    public static void refreshActivity(Activity activity, String isSelectedLanguage) {
        TeacherUtil_SharedPreference.putLanguageType(activity, isSelectedLanguage);
        LocaleHelper.setLocale(activity, isSelectedLanguage);
        Intent intent = new Intent(activity, TeacherSplashScreen.class); // Replace with your splash screen class
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.finish();
        activity.startActivity(intent);
        Log.d("language", "Language set to: " + isSelectedLanguage);
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
