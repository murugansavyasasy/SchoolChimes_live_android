package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.model.TeacherSectionModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



/**
 * Created by devi on 5/16/2017.
 */

public class TeacherUtil_Common {
    public static String selectedCircularDate;

    public static List<TeacherSectionModel> list_staffStdSecs = null;// = new ArrayList<>();

    public static ArrayList<TeacherSchoolsModel> listschooldetails = null;// new ArrayList<>();
    public static String strNoClassWarning = "";

    public static String LOGIN_TYPE_HEAD = "Group Head";
    public static final String LOGIN_TYPE_PRINCIPAL = "Principal";
    public static final String LOGIN_TYPE_TEACHER = "Teacher";
    public static final String LOGIN_TYPE_ADMIN = "Admin";
    public static final String LOGIN_TYPE_OFFICE_STAFF = "Office_staff";

    public static final String LIST_STANDARDS = "Select Classes";
    public static final String LIST_GROUPS = "Select Groups";

    public static final String VOICE_FOLDER_NAME = "School Messenger/Voice";
    public static final String VOICE_FILE_NAME = "schoolVoice.mp3";
    //public static final String VOICE_FILE_NAME = "schoolVoice";


    public static int maxEmergencyvoicecount =30;
    public static int maxGeneralvoicecount =180;
    public static int maxHWVoiceDuration = 180;
    public static int maxGeneralSMSCount = 460;
    public static int maxHomeWorkSMSCount =120;

    public static JSONObject loginJsonObject;

    public static final int GH_EMERGENCY = 8301;
    public static final int GH_VOICE = 8302;
    public static final int GH_TEXT = 8303;
    public static final int GH_NOTICE_BOARD = 8304;

    public static final int PRINCIPAL_EMERGENCY = 8201;
    public static final int PRINCIPAL_VOICE = 8202;
    public static final int PRINCIPAL_TEXT = 8203;
    public static final int PRINCIPAL_NOTICE_BOARD = 8204;
    public static final int PRINCIPAL_EVENTS = 8205;
    public static final int PRINCIPAL_PHOTOS = 8206;
    public static final int PRINCIPAL_ABSENTEES = 8207;
    public static final int PRINCIPAL_SCHOOLSTRENGTH = 8208;
    public static final int PRINCIPAL_ATTENDANCE = 8209;
    public static final int PRINCIPAL_TEXT_HW = 8210;
    public static final int PRINCIPAL_VOICE_HW = 8211;
    public static final int PRINCIPAL_EXAM_TEST = 8212;
    public static final int PRINCIPAL_ASSIGNMENT= 8220;
    public static final int PRINCIPAL_CHAT= 8221;
    public static final int PRINCIPAL_MESSAGESFROMMANAGEMENT= 8222;


    public static final int PRINCIPAL_MEETING_URL= 8223;
    public static final int STAFF_MEETING_URL= 8224;


    public static final int STAFF_PHOTOS = 8101;
    public static final int STAFF_VOICE = 8102;
    public static final int STAFF_VOICE_HW = 8103;
    public static final int STAFF_TEXT = 8104;
    public static final int STAFF_TEXT_HW = 8105;
    public static final int STAFF_TEXT_EXAM = 8106;
    public static final int STAFF_ATTENDANCE = 8107;

    public static final int PRINCIPAL_VIDEOS = 8108;
    public static final int STAFF_VIDEOS = 8109;
    public static final int STAFF_TEXTASSIGNMENT = 8110;
    public static final int STAFF_VOICEASSIGNMENT = 8111;
    public static final int STAFF_IMAGEASSIGNMENT = 8112;
    public static final int STAFF_PDFASSIGNMENT = 8113;


    public static final int VIDEO_GALLERY = 8114;
    public static final int MEETING_URL = 8115;


    public static String Principal_SchoolId = "";
    public static String Principal_staffId = "";

    public static String staff_schoolId = "";

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