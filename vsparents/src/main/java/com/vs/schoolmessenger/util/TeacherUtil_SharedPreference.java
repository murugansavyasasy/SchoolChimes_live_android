package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vs.schoolmessenger.activity.HomeActivity;
import com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by voicesnap on 9/17/2016.
 */
public class TeacherUtil_SharedPreference {
    public static final String spUpdateRequired = "update_required";

    public static final String spCountryName = "SP_SCHOOLS_COUNTRY";
    public static final String SH_COUNTRY_ID = "CountryID";
    public static final String SH_COUNTRY_NAME = "CountryName";
    public static final String SH_COUNTRY_MOBILE_LENGTH = "CountryMobileLength";
    public static final String SH_COUNTRY_CODE = "CountryCode";
    public static final String SH_COUNTRY_BASE_URL = "BaseURL";
    public static final String CHILD_DATAS = "Datas";


    public static final String PEOPLE_DETAILS = "Details";

    public static final String SH_PRINCIPAL = "PRINCIPAL";
    public static final String SH_STAFF = "STAFF";
    public static final String SH_ADMIN = "ADMIN";
    public static final String SH_PARENT = "PARENT";
    public static final String SH_GROUPHEAD = "GROUPHEAD";

    public static final String SH_IMAGE_COUNT = "Images";

    public static final String childFeeVisible ="";
    public static final String childFeeMonthVisible ="";


    public static final String spNameinstall="SP_NAMEINSATALL";
    public static final String spMobileNum="SP_MOBILENUM";
    public static final String OTP_NUMBER="OTP_NUMBER";
    public static final String MOBILENUMBER_SCREEN="MOBILE";
    public static final String PERMISSION_PASSWORDSCREEN="PERMISSION_PASSWORDSCREEN";

    public static final String COUNT_DETAILS="COUNT_DETAILS";

    public static final String SH_AUTOUPDATE="Autoupdate";
    public static final String SH_UPDATE="update";
    public static final String SH_autoupdate="false";


    public static final String CHECK_INSTALL="";
    public static final String Mobile_number="";
    public static final String IMEI_NUMBER="IMEI_NUMBER";
    public static final String imei_number="";

    public static final String otp_num="";
    public static final String permission="";
    public static final String child_screen="";
    public static final String mobile_screen="";

    public static final String PAID="PAID";
    public static final String PENDING="PENDING";
    public static final String Paid_Size="Paid_Size";
    public static final String Pending_size="Pending_size";
    public static final String PASSWORD_SCREEN="PASSWORD_SCREEN";
    public static final String password_type="password_screen";



    public static final String PDF="";
    public static final String IMAGE="";
    public static final String EMERGENCYVOICE="";
    public static final String VOICE="";
    public static final String SMS="";
    public static final String NOTICEBOARD="";
    public static final String EVENTS="";
    public static final String HOMEWORK="";
    public static final String EXAM="";

    public static final String princi="";
    public static final String staff="";
    public static final String admin="";
    public static final String grouphead="";
    public static final String parent="";

    public static final String SH_princi="";
    public static final String SH_staff="";
    public static final String SH_admin="";
    public static final String SH_grouphead="";
    public static final String SH_parent="";

    public static final String OTP_timer="";

    public static final String SH_ImageCount="";


    public static final String SCREEN_SHOTS="SCREEN_SHOTS";
    public static final String MENU_IDS="MENU_ID";
    public static final String screen_shot="";

    public static final String NotificationCount ="";
    public static final String Count_notifi = "Notification";


    public static final String language_type = "en";
    public static final String LANGUAGE = "Language";

    public static final String appTermsAndCondition ="";
    public static final String APP_TERMS = "TERMS_AND_CONDITION";
    public static final String OFFER_LINK = "OfferLink";
    public static final String offerlink = "";

    public static final String FORGET_PASSWORD_OTP = "FORGET";
    public static final String forget = "";

    public static final String DIAL_NUMBERS = "DIAL_NUMBER";
    public static final String DialNumber = "";


    public static final String FORGET_OTP_MESSAGE = "FORGET_MESSAGE";
    public static final String forget_message ="";

    public static final String SIZES = "SIZES";
    public static final String Imagesize ="img";
    public static final String pdfsize ="pdf";
    public static final String filecontent ="content";
    public static final String videosize ="video";
    public static final String videoalert ="videoalert";
    public static final String Videotoken ="Videotoken";

    public static final String NewVersion ="NEWVERSION";
    public static final String isNewVersion ="";

    public static final String BASE_URL ="URL";
    public static final String change_url ="change_url";

    public static final String REPORT_BASE_URL ="REPORT_URL";
    public static final String Report_change_url ="Report_change_url";

    public static final String HELPLINE_BASE_URL ="HELPLINE_BASE_URL";
    public static final String help_line_url ="help_line_url";

    public static final String Current_Date ="currentDate";
    public static final String Current_Date_voive ="currentDate_voice";

    public static void putPrincipalIDs(ArrayList<Integer> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("princi" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putInt("Principal_ID" + "_" + i, array.get(i));
             editor.commit();
        }
    }

    public static Integer[] getPrincipalIDs(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("princi" + "size", 0);
        Integer array[] = new Integer[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt("Principal_ID" + "_" + i, 0);
        return array;
    }

    public static void putStaffIDs(ArrayList<Integer> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("staff" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putInt("Staff_ID" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static Integer[] getStaffIDs(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("staff" + "size", 0);
        Integer array[] = new Integer[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt("Staff_ID" + "_" + i, 0);
        return array;
    }


    public static void putAdminIDs(ArrayList<Integer> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("admin" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putInt("Admin_ID" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static Integer[] getAdminIDs(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("admin" + "size", 0);
        Integer array[] = new Integer[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt("Admin_ID" + "_" + i, 0);
        return array;
    }


    public static void putGroupHeadIDs(ArrayList<Integer> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("grouphead" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putInt("GroupHead_ID" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static Integer[] getGroupHeadIDs(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("grouphead" + "size", 0);
        Integer array[] = new Integer[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt("GroupHead_ID" + "_" + i, 0);
        return array;
    }

    public static void putparentIDs(ArrayList<Integer> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("parent" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putInt("parent_ID" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static Integer[] getParentIDs(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("parent" + "size", 0);
        Integer array[] = new Integer[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getInt("parent_ID" + "_" + i, 0);
        return array;
    }



    public static void putParentMenuNames(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("parent_name" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("parent_name" + "_" + i,array.get(i));
            editor.commit();
        }
    }

    public static String[] getParentMenuNames(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("parent_name" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("parent_name" + "_" + i, null);
        return array;
    }

    public static void putPrincipalNames(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("principal_name" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("principal_name" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static void putUploadedFileList(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("S3UploadedFiles", 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("s3_upload_file" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("s3_upload_file" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static String[] getUploadedFileList(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("S3UploadedFiles", 0);
        int size = prefs.getInt("s3_upload_file" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("s3_upload_file" + "_" + i, null);
        return array;
    }

    public static String[] getPrincipalNames(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("principal_name" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("principal_name" + "_" + i, null);
        return array;
    }

    public static void putStaffNames(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("staff_name" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("staff_name" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static String[] getStaffNames(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("staff_name" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("staff_name" + "_" + i, null);
        return array;
    }

    public static void putAdminNames(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("admin_name" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("Admin_name" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static String[] getAdminNames(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("admin_name" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("Admin_name" + "_" + i, null);
        return array;
    }


    public static void putGroupHeadNames(ArrayList<String> array, Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(MENU_IDS, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("group_name" +"size", array.size());
        for(int i=0;i<array.size();i++) {
            editor.putString("groupHead_name" + "_" + i, array.get(i));
            editor.commit();
        }
    }

    public static String[] getGroupHeadNames(Activity mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(MENU_IDS, 0);
        int size = prefs.getInt("group_name" + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString("groupHead_name" + "_" + i, null);
        return array;
    }

    public static void putChildVisible(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("CHILD_VISIBLE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(childFeeVisible, autoupdate);
        Log.d("CHILD_VISIBLE", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getChildFeeVisible(Activity activity) {
        String strupdate = activity.getSharedPreferences("CHILD_VISIBLE", MODE_PRIVATE).getString(childFeeVisible, "");
        return strupdate;
    }

    public static void putChildMonthVisible(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("CHILD_VISIBLE_MONTH", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(childFeeMonthVisible, autoupdate);
        Log.d("CHILD_VISIBLE_MONTH", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getChildFeeMonthVisible(Activity activity) {
        String strupdate = activity.getSharedPreferences("CHILD_VISIBLE_MONTH", MODE_PRIVATE).getString(childFeeMonthVisible, "");
        return strupdate;
    }

    public static void PutRazorPayKeyID(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("RP_KEY_ID", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString("Rp_key", autoupdate);
        Log.d("RP_KEY_ID", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getRazorPayKeyID(Activity activity) {
        String strupdate = activity.getSharedPreferences("RP_KEY_ID", MODE_PRIVATE).getString("Rp_key", "");
        return strupdate;
    }

    public static void putMakePayment(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("MAKE_PAYMENT", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString("make_enable", autoupdate);
        Log.d("RP_KEY_ID", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getMakePayment(Activity activity) {
        String strupdate = activity.getSharedPreferences("MAKE_PAYMENT", MODE_PRIVATE).getString("make_enable", "");
        return strupdate;
    }

    public static void putRazorPayAPIKey(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("RP_API_KEY", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString("Rp_api_key_id", autoupdate);
        Log.d("RP_API_KEY", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getRazorPayAPIKey(Activity activity) {
        String strupdate = activity.getSharedPreferences("RP_API_KEY", MODE_PRIVATE).getString("Rp_api_key_id", "");
        return strupdate;
    }

    public static void putLanguageType(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(LANGUAGE, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(language_type, autoupdate);
        Log.d(language_type, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getLanguageType(Activity activity) {
        String strupdate = activity.getSharedPreferences(LANGUAGE, MODE_PRIVATE).getString(language_type, "");
        return strupdate;
    }

    public static void putNewVersion(Activity activity,String value){
        SharedPreferences prefs = activity.getSharedPreferences(NewVersion, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(isNewVersion, value);
        ed.commit();
    }
    public static String getNewVersion(Context activity) {
        String strupdate = activity.getSharedPreferences(NewVersion, MODE_PRIVATE).getString(isNewVersion, "1");
        return strupdate;
    }

    public static void putLastVisibleUpdatesPosition(Activity activity,String value){
        SharedPreferences prefs = activity.getSharedPreferences("POPUP", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("position", value);
        ed.commit();
    }
    public static String getLastVisibleUpdatesPosition(Context activity) {
        String strupdate = activity.getSharedPreferences("POPUP", MODE_PRIVATE).getString("position", "0");
        return strupdate;
    }

    public static void putScreen(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SCREEN_SHOTS, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(screen_shot, autoupdate);
        Log.d(SCREEN_SHOTS, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getScreen(Activity activity) {
        String strupdate = activity.getSharedPreferences(SCREEN_SHOTS, MODE_PRIVATE).getString(screen_shot, "");
        return strupdate;
    }


    public static void putBiometricEnabled(Activity activity,Boolean autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("BIO_METRIC", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("enable_biometric", autoupdate);
        ed.commit();
    }
    public static boolean getBiometricEnabled(Activity activity) {
        boolean update = activity.getSharedPreferences("BIO_METRIC", MODE_PRIVATE).getBoolean("enable_biometric", false);
        return update;
    }

    public static void putBiometricSkip(Activity activity,Boolean autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("BIO_METRIC_SKIP", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("enable_biometric_skip", autoupdate);
        ed.commit();
    }
    public static boolean getBiometricSkip(Activity activity) {
        boolean update = activity.getSharedPreferences("BIO_METRIC_SKIP", MODE_PRIVATE).getBoolean("enable_biometric_skip", false);
        return update;
    }

    public static void putCountDetails(HomeActivity homeActivity, String emgvoicecount, String voicemsgcount,
                                       String textmessagecount, String photoscount, String documentcount, String noticeboard,
                                       String examtest, String schoolevent, String homework) {
        SharedPreferences sp = homeActivity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(PDF, documentcount);
        ed.putString(IMAGE, photoscount);
        ed.putString(EMERGENCYVOICE, emgvoicecount);
        ed.putString(VOICE, voicemsgcount);
        ed.putString(SMS, textmessagecount);
        ed.putString(NOTICEBOARD, noticeboard);
        ed.putString(EVENTS, schoolevent);
        ed.putString(HOMEWORK, homework);
        ed.putString(EXAM, examtest);

        Log.d(COUNT_DETAILS, "Country Data stored in SP");
        ed.commit();
    }

    public static String getPDF(Activity activity) {
        String baseURL = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(PDF, "");
        return baseURL;
    }

    public static String getImage(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(IMAGE, "");
        return mobileNumberlength;
    }
    public static String getEmergencyVoive(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(EMERGENCYVOICE, "");
        return mobileNumberlength;
    }
    public static String getVoice(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(VOICE, "");
        return mobileNumberlength;
    }
    public static String getSms(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(SMS, "");
        return mobileNumberlength;
    }
    public static String getNoticeBoard(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(NOTICEBOARD, "");
        return mobileNumberlength;
    }
    public static String getEvents(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(EVENTS, "");
        return mobileNumberlength;
    }
    public static String getHomeWork(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(HOMEWORK, "");
        return mobileNumberlength;
    }
    public static String getExam(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(COUNT_DETAILS, MODE_PRIVATE).getString(EXAM, "");
        return mobileNumberlength;
    }

    public static void putInstall(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(spNameinstall, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(CHECK_INSTALL, autoupdate);
        Log.d(spName, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getInstall(Activity activity) {
        String strupdate = activity.getSharedPreferences(spNameinstall, MODE_PRIVATE).getString(CHECK_INSTALL, "");
        return strupdate;
    }

    public static void putReportURL(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(REPORT_BASE_URL, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Report_change_url, autoupdate);
        ed.commit();
    }

    public static String getReportURL(Context activity) {
        String strupdate = activity.getSharedPreferences(REPORT_BASE_URL, MODE_PRIVATE).getString(Report_change_url, "");
        return strupdate;
    }

    public static void putHelpLineUrl(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(HELPLINE_BASE_URL, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(help_line_url, autoupdate);
        ed.commit();
    }

    public static String getHelpLineUrl(Context activity) {
        String strupdate = activity.getSharedPreferences(HELPLINE_BASE_URL, MODE_PRIVATE).getString(help_line_url, "");
        return strupdate;
    }


    public static void putBaseURL(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(BASE_URL, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(change_url, autoupdate);
        ed.commit();
    }

    public static String getBaseUrl(Context activity) {
        String strupdate = activity.getSharedPreferences(BASE_URL, MODE_PRIVATE).getString(change_url, "");
        return strupdate;
    }

    public static String getBaseUrlContext(Context activity) {
        String strupdate = activity.getSharedPreferences(BASE_URL, MODE_PRIVATE).getString(change_url, "");
        return strupdate;
    }

    public static void putOTPTimer(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("OTP_TIMER", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(OTP_timer, autoupdate);
        Log.d("OTP_TIMER", "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getOTpTimer(Activity activity) {
        String strupdate = activity.getSharedPreferences("OTP_TIMER", MODE_PRIVATE).getString(OTP_timer, "");
        return strupdate;
    }

    public static void putForgetPasswordOTP(Activity activity,String forgetotp){
        SharedPreferences prefs = activity.getSharedPreferences(FORGET_PASSWORD_OTP, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(forget, forgetotp);
        Log.d("forget", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getForgetPasswordOtp(Activity activity) {
        String forg = activity.getSharedPreferences(FORGET_PASSWORD_OTP, MODE_PRIVATE).getString(forget, "");
        return forg;
    }

    public static void putNoteMessage(Activity activity,String forgetotp){
        SharedPreferences prefs = activity.getSharedPreferences("Note_message", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("note_msg", forgetotp);
        Log.d("forget", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getNoteMessage(Activity activity) {
        String forg = activity.getSharedPreferences("Note_message", MODE_PRIVATE).getString("note_msg", "");
        return forg;
    }

    public static void putOTPNote(Activity activity,String forgetotp){
        SharedPreferences prefs = activity.getSharedPreferences("OTP_Note_message", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("otp_msg_note", forgetotp);
        ed.commit();
    }
    public static String getOTPNote(Activity activity) {
        String forg = activity.getSharedPreferences("OTP_Note_message", MODE_PRIVATE).getString("otp_msg_note", "");
        return forg;
    }



    public static void putDialNumbers(Activity activity,String forgetotp){
        SharedPreferences prefs = activity.getSharedPreferences(DIAL_NUMBERS, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(DialNumber, forgetotp);
        Log.d("DialNumbers", "autoupdate Data stored in SP");
        ed.commit();
    }
    public static String getDialNumbers(Activity activity) {
        String forg = activity.getSharedPreferences(DIAL_NUMBERS, MODE_PRIVATE).getString(DialNumber, "");
        return forg;
    }


    public static void putOfferLink(Activity activity,String link){
        SharedPreferences prefs = activity.getSharedPreferences(OFFER_LINK, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(offerlink, link);
        ed.commit();
    }

    public static String getOfferLink(Activity activity) {
        String strupdate = activity.getSharedPreferences(OFFER_LINK, MODE_PRIVATE).getString(offerlink, "");
        return strupdate;
    }
    public static void putSize(Activity activity,String imgsize,String pdfsizes,String filecontents,String size,String alert,String token){
        SharedPreferences prefs = activity.getSharedPreferences(SIZES, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Imagesize, imgsize);
        ed.putString(pdfsize, pdfsizes);
        ed.putString(filecontent, filecontents);
        ed.putString(videosize, size);
        ed.putString(videoalert,alert);
        ed.putString(Videotoken,token);
        ed.commit();
    }

    public static String getImagesize(Activity activity) {
        String Imagesizes = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(Imagesize, "");
        return Imagesizes;
    }

    public static String getPdfsize(Activity activity) {
        String pdf = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(pdfsize, "");
        return pdf;
    }

    public static String getFilecontent(Activity activity) {
        String content = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(filecontent, "");
        return content;
    }
    public static String getVideosize(Activity activity) {
        String content = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(videosize, "");
        return content;
    }
    public static String getVideoalert(Activity activity) {
        String content = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(videoalert, "");
        return content;
    }
    public static String getVideotoken(Activity activity) {
        String content = activity.getSharedPreferences(SIZES, MODE_PRIVATE).getString(Videotoken, "");
        return content;
    }
    public static void putPeopleDetails(Activity activity, String principal, String staf, String paren, String grouhead, String adminnn){
        SharedPreferences prefs = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_princi, principal);
        ed.putString(SH_staff, staf);
        ed.putString(SH_admin, adminnn);
        ed.putString(SH_grouphead, grouhead);
        ed.putString(SH_parent, paren);
        Log.d(PEOPLE_DETAILS, "autoupdate Data stored in SP");
        Log.d("Login values", principal +staf +adminnn +grouhead +paren );
        ed.commit();
    }
    public static String getprincipalFromSP(Activity activity) {
        String principal = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(SH_princi, "");
        return principal;
    }

    public static String getstaffFromSP(Activity activity) {
        String staff = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(SH_staff, "");
        return staff;
    }

    public static String getadminFromSP(Activity activity) {
        String admin = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(SH_admin, "");
        return admin;
    }

    public static String getparentFromSP(Activity activity) {
        String parent = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(SH_parent, "");
        return parent;
    }

    public static String getgroupheadFromSP(Activity activity) {
        String grouphead = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(SH_grouphead, "");
        return grouphead;
    }
    public static String getPrinci(Activity activity) {
        String strprinci = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(princi, "");
        Log.d("strprinci", strprinci );
        return strprinci;
    }

    public static String getStaff1(Activity activity) {
        String strstaff = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(staff, "");
        Log.d("strstaff", strstaff );
        return strstaff;
    }
    public static String getAdmin1(Activity activity) {
        String stradmin = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(admin, "");
        Log.d("stradmin", stradmin );
        return stradmin;
    }
    public static String getGrouphead(Activity activity) {
        String strgrphead = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(grouphead, "");
        Log.d("strgrphead", strgrphead );
        return strgrphead;
    }
    public static String getParent1(Activity activity) {
        String strparent = activity.getSharedPreferences(PEOPLE_DETAILS, MODE_PRIVATE).getString(parent, "");
        Log.d("strparent", strparent );
        return strparent;
    }


    public static void putPrincipal(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PRINCIPAL, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_princi, autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getPrincipal(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PRINCIPAL, MODE_PRIVATE).getString(SH_princi, "");
        return strupdate;
    }

    public static void putImageCount(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_IMAGE_COUNT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_ImageCount, autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getImageCount(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_IMAGE_COUNT, MODE_PRIVATE).getString(SH_ImageCount, "");
        return strupdate;
    }


    public static void putLoginMessage(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("LOGIN_MESSAGE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString("login_msg", autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getLoginMessage(Activity activity) {
        String strupdate = activity.getSharedPreferences("LOGIN_MESSAGE", MODE_PRIVATE).getString("login_msg", "");
        return strupdate;
    }

    public static void putRole(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("ROLE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("role", autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getRole(Activity activity) {
        String strupdate = activity.getSharedPreferences("ROLE", MODE_PRIVATE).getString("role", "");
        return strupdate;
    }

    public static void putIsStaff(Activity activity,Boolean autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("IS_STAFF", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("is_staff_type", autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static Boolean getIsStaff(Activity activity) {
        Boolean strupdate = activity.getSharedPreferences("IS_STAFF", MODE_PRIVATE).getBoolean("is_staff_type", false);
        return strupdate;
    }

    public static void putIsParent(Activity activity,Boolean autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("IS_PARENT", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean("is_parent_type", autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static Boolean getIsParent(Activity activity) {
        Boolean strupdate = activity.getSharedPreferences("IS_PARENT", MODE_PRIVATE).getBoolean("is_parent_type", false);
        return strupdate;
    }


    public static void putDisplayRoleMessage(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("ROLE_DISP", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString("role_display", autoupdate);
        Log.d(SH_PRINCIPAL, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getDisplayRoleMessage(Activity activity) {
        String strupdate = activity.getSharedPreferences("ROLE_DISP", MODE_PRIVATE).getString("role_display", "");
        return strupdate;
    }

    public static void putStaff(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_STAFF, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_staff, autoupdate);
        Log.d(SH_STAFF, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getStaff(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_STAFF, MODE_PRIVATE).getString(SH_staff, "");
        return strupdate;
    }





    public static void putGroupHead(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_GROUPHEAD, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_grouphead, autoupdate);
        Log.d(SH_GROUPHEAD, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getGroupHead(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_GROUPHEAD, MODE_PRIVATE).getString(SH_grouphead, "");
        return strupdate;
    }


    public static void putAdmin(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_ADMIN, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_admin, autoupdate);
        Log.d(SH_ADMIN, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getAdmin(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_ADMIN, MODE_PRIVATE).getString(SH_admin, "");
        return strupdate;
    }

    public static void putParent(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(SH_parent, autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getParent(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString(SH_parent, "");
        return strupdate;
    }


    public static void putNewProduct(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("SH_New_product", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("New_product", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getNewProduct(Activity activity) {
        String strupdate = activity.getSharedPreferences("SH_New_product", MODE_PRIVATE).getString("New_product", "");
        return strupdate;
    }


    public static void putCurrentDate(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences("SH_CURRENT_DATE", MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("current_date", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getCurrentDate(Activity activity) {
        String strupdate = activity.getSharedPreferences("SH_CURRENT_DATE", MODE_PRIVATE).getString("current_date", "");
        return strupdate;
    }
    public static void putProfilLink(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("ProfileLink", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getProfileLink(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("ProfileLink", "");
        return strupdate;
    }

    public static void putUploadProfileTitle(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("UploadProfileTitle", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getUploadProfileTitle(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("UploadProfileTitle", "");
        return strupdate;
    }


    public static void putPaymentUrl(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("PaymentUrl", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getPaymentUrl(Activity activity) {
        String payment_url = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("PaymentUrl", "");
        return payment_url;
    }


    public static void putNewProductsLink(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("NewProductsLink", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getNewProductLink(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("NewProductsLink", "");
        return strupdate;
    }



    public static void putAdTimeInterval(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("AdTimeInterval", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getAdTimeInterval(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("AdTimeInterval", "");
        return strupdate;
    }

    public static void putProfilTitle(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString("ProfileTitle", autoupdate);
        Log.d(SH_PARENT, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getProfileTitle(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_PARENT, MODE_PRIVATE).getString("ProfileTitle", "");
        return strupdate;
    }

    public static void putPermission(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(PERMISSION_PASSWORDSCREEN, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(permission, autoupdate);
        Log.d(spName, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getPermission(Activity activity) {
        String strupdate = activity.getSharedPreferences(PERMISSION_PASSWORDSCREEN, MODE_PRIVATE).getString(CHECK_INSTALL, "");
        return strupdate;
    }


    public static void putMobileNum(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(spMobileNum, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putString(Mobile_number, autoupdate);
        Log.d(spMobileNum, "autoupdate Data stored in SP");
        ed.commit();
    }

//    public static String getMobileNum(Activity activity) {
//        String strupdate = activity.getSharedPreferences(spMobileNum, MODE_PRIVATE).getString(Mobile_number, "");
//        return strupdate;
//    }

    public static void putPassWordType(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(PASSWORD_SCREEN, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(password_type, autoupdate);
        Log.d(PASSWORD_SCREEN, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getPassWordType(Activity activity) {
        String strupdate = activity.getSharedPreferences(PASSWORD_SCREEN, MODE_PRIVATE).getString(password_type, "");
        return strupdate;
    }


    public static void putOTPNum(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(OTP_NUMBER, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(otp_num, autoupdate);
        Log.d(OTP_NUMBER, "autoupdate Data stored in SP");
        ed.commit();
    }


    public static void putMobileNumberScreen(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(MOBILENUMBER_SCREEN, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(mobile_screen, autoupdate);
        Log.d(MOBILENUMBER_SCREEN, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getMobileNumberScreen(Activity activity) {
        String strupdate = activity.getSharedPreferences(MOBILENUMBER_SCREEN, MODE_PRIVATE).getString(mobile_screen, "");
        return strupdate;
    }

    public static String getChild(Activity activity) {
        String strupdate = activity.getSharedPreferences(CHILD_DATAS, MODE_PRIVATE).getString(child_screen, "");
        return strupdate;
    }


    public static void putChild(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(CHILD_DATAS, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(child_screen, autoupdate);
        Log.d(CHILD_DATAS, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getOTPNum(Activity activity) {
        String strupdate = activity.getSharedPreferences(OTP_NUMBER, MODE_PRIVATE).getString(Mobile_number, "");
        return strupdate;
    }
    public static void putIMEI(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(IMEI_NUMBER, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(imei_number, autoupdate);
        Log.d(IMEI_NUMBER, "autoupdate Data stored in SP");
        ed.commit();
    }

    public static String getIMEI(Activity activity) {
        String strupdate = activity.getSharedPreferences(IMEI_NUMBER, MODE_PRIVATE).getString(imei_number, "");
        return strupdate;
    }


    public static void putNotification(Activity activity, String value)
    {
        SharedPreferences sp = activity.getSharedPreferences(Count_notifi, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(NotificationCount, value);
        ed.commit();
    }


    public static void putAppTermsAndConditions(Activity activity, String value)
    {
        SharedPreferences sp = activity.getSharedPreferences(APP_TERMS, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(appTermsAndCondition, value);
        ed.commit();
    }

    public static String getAppTermsAndCondition(Activity activity) {
        String value = activity.getSharedPreferences(APP_TERMS, Context.MODE_PRIVATE).getString(appTermsAndCondition, "");
        return value;
    }


    public static void putForgetMessage(Activity activity, String value)
    {
        SharedPreferences sp = activity.getSharedPreferences(FORGET_OTP_MESSAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(forget_message, value);
        ed.commit();
    }

    public static String getForgetOtpMessage(Activity activity) {
        String value = activity.getSharedPreferences(FORGET_OTP_MESSAGE, Context.MODE_PRIVATE).getString(forget_message, "");
        return value;
    }

    public static String getNotification(Activity activity) {
        String value = activity.getSharedPreferences(Count_notifi, Context.MODE_PRIVATE).getString(NotificationCount, "");
        return value;
    }

    public static String getNotify(MyFirebaseMessagingService activity) {
        String value = activity.getSharedPreferences(Count_notifi, Context.MODE_PRIVATE).getString(NotificationCount, "");
        return value;
    }

    public static void putautoupdateToSP(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_AUTOUPDATE, MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_UPDATE, autoupdate);
        Log.d(SH_AUTOUPDATE, "autoupdate Data stored in SP");
        ed.commit();
    }
    public static boolean getupdateFromSP(Activity activity) {
        boolean update = activity.getSharedPreferences(SH_AUTOUPDATE, MODE_PRIVATE).getBoolean(SH_autoupdate, false);
        return update;
    }

    public static String getupdateversionFromSP(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_AUTOUPDATE, MODE_PRIVATE).getString(SH_UPDATE, "");
        return strupdate;
    }
    public static void updatePreferences(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(spCountryName, MODE_PRIVATE);
        if (prefs.getBoolean(spUpdateRequired, true)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();

            clearCountrySharedPreference(activity);

            editor.putBoolean(spUpdateRequired, false);
            editor.commit();
        }
    }

    public static void putCountryInforToSP(Activity activity, String CountryID,
                                           String CountryName, String CountryMobileLength, String CountryCode, String BaseURL) {
        SharedPreferences sp = activity.getSharedPreferences(spCountryName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SH_COUNTRY_ID, CountryID);
        ed.putString(SH_COUNTRY_NAME, CountryName);
        ed.putString(SH_COUNTRY_MOBILE_LENGTH, CountryMobileLength);
        ed.putString(SH_COUNTRY_CODE, CountryCode);
        ed.putString(SH_COUNTRY_BASE_URL, BaseURL);

        Log.d(spCountryName, "Country Data stored in SP");
        ed.commit();
    }
    public static String getShCountryCode(Activity activity) {
        String CountryCode = activity.getSharedPreferences(spCountryName, MODE_PRIVATE).getString(SH_COUNTRY_CODE, "");
        return CountryCode;
    }

    public static String getBaseUrlFromSP(Activity activity) {
        String baseURL = activity.getSharedPreferences(spCountryName, MODE_PRIVATE).getString(SH_COUNTRY_BASE_URL, "");
        return baseURL;
    }

    public static String getCountryID(Activity activity) {
        String CountryCode = activity.getSharedPreferences(spCountryName, MODE_PRIVATE).getString(SH_COUNTRY_ID, "");
        return CountryCode;
    }

    public static String getMobileNumberLengthFromSP(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(spCountryName, MODE_PRIVATE).getString(SH_COUNTRY_MOBILE_LENGTH, "");
        return mobileNumberlength;
    }
    public static void clearCountrySharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spCountryName, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }


    public static final String spName = "SP_MESSENGER_SCHOOLS";

    public static final String SH_STAFF_MOBILE = "mobileNumber";
    public static final String SH_STAFF_PASSWORD = "password";
    public static final String SH_REMEMBER_PASSWORD = "rememberMe";

    public static void putStaffLoginInfoToSP(Activity activity, String mobileNumber, String password, boolean rememberMe) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(SH_STAFF_MOBILE, mobileNumber);
        ed.putString(SH_STAFF_PASSWORD, password);
        ed.putBoolean(SH_REMEMBER_PASSWORD, rememberMe);

        Log.d(spName, "Staff Login Data stored in SP");
        ed.commit();
    }

    public static String getMobileNumber(Context activity) {
        String mobileNumber = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_STAFF_MOBILE, "");
        return mobileNumber;
    }

    public static String getMobileNumberFromSP(Activity activity) {
        String mobileNumber = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_STAFF_MOBILE, "");
        return mobileNumber;
    }
    public static String getPasswordFromSP(Activity activity) {
        String password = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_STAFF_PASSWORD, "");
        return password;
    }

    public static boolean getAutoLoginStatusFromSP(Activity activity) {
        boolean rememberMe = activity.getSharedPreferences(spName, MODE_PRIVATE).getBoolean(SH_REMEMBER_PASSWORD, false);
        return rememberMe;
    }

    public static void cleaStaffSharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spName, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    //********************

    public static final String SH_STAFF_ID = "StaffID";
    public static final String SH_STAFF_NAME = "StaffName";
    public static final String SH_SCHOOL_ID = "SchoolID";
    public static final String SH_SCHOOL_NAME = "SchoolName";
    public static final String SH_SCHOOL_ADDRESS = "SchoolAddress";
    public static final String SH_SCHOOL_LOGO = "SchoolLogo";

    public static final String SH_IS_ADMIN = "IsAdmin";
    public static final String SH_IS_MANAGEMENT = "IsManagement";
    public static final String SH_IS_STAFF = "IsStaff";
    public static final String SH_IS_GROUPHEAD = "IsGrouphead";
    public static final String SH_LOGGEDIN_AS = "LoggedInAs";

    public static void putSelecedStaffInfoToSP(Activity activity, List<String> staffID, String staffName, String schoolID,
                                               String schoolName, String schoolAddress, String schoolLogo, boolean isAdmin,
                                               boolean isManagement, boolean isStaff) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < staffID.size(); i++) {
            sb.append(staffID.get(i)).append(",");
        }
        ed.putString(SH_STAFF_ID, sb.toString());

        ed.putString(SH_STAFF_NAME, staffName);
        ed.putString(SH_SCHOOL_ID, schoolID);
        ed.putString(SH_SCHOOL_NAME, schoolName);
        ed.putString(SH_SCHOOL_ADDRESS, schoolAddress);
        ed.putString(SH_SCHOOL_LOGO, schoolLogo);

        ed.putBoolean(SH_IS_ADMIN, isAdmin);
        ed.putBoolean(SH_IS_MANAGEMENT, isManagement);
        ed.putBoolean(SH_IS_STAFF, isStaff);

        Log.d(spName, "Staff Data stored in SP");
        ed.commit();
    }
    public static boolean saveArraystaffId(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_STAFF_ID, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }


    public static String[] loadArraystaffId(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_STAFF_ID, 0);
        int size = prefs.getInt(arrayName + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }


    public static boolean saveArraystaffName(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_STAFF_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }


    public static String[] loadArraystaffName(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_STAFF_NAME, 0);
        int size = prefs.getInt(arrayName + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }


    public static boolean saveArrayschoolName(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_SCHOOL_NAME, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }


    public static String[] loadArrayschoolName(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_SCHOOL_NAME, 0);
        int size = prefs.getInt(arrayName + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }

    public static boolean saveArrayschoolId(String[] array, String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_SCHOOL_ID, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(arrayName +"size", array.length);
        for(int i=0;i<array.length;i++)
            editor.putString(arrayName + "_" + i, array[i]);
        return editor.commit();
    }


    public static String[] loadArrayschoolId(String arrayName, Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences(SH_SCHOOL_ID, 0);
        int size = prefs.getInt(arrayName + "size", 0);
        String array[] = new String[size];
        for (int i = 0; i < size; i++)
            array[i] = prefs.getString(arrayName + "_" + i, null);
        return array;
    }
    public static void putSelecedStaffInfoToSPnew(Activity activity, String staffID, String staffName, String schoolID,
                                                  String schoolName, String schoolAddress, String schoolLogo, boolean isAdmin,
                                                  boolean isManagement, boolean isStaff, boolean isGrouphead) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(SH_STAFF_ID, staffID);
        ed.putString(SH_STAFF_NAME, staffName);
        ed.putString(SH_SCHOOL_ID, schoolID);
        ed.putString(SH_SCHOOL_NAME, schoolName);
        ed.putString(SH_SCHOOL_ADDRESS, schoolAddress);
        ed.putString(SH_SCHOOL_LOGO, schoolLogo);

        ed.putBoolean(SH_IS_ADMIN, isAdmin);
        ed.putBoolean(SH_IS_MANAGEMENT, isManagement);
        ed.putBoolean(SH_IS_STAFF, isStaff);
        ed.putBoolean(SH_IS_GROUPHEAD, isGrouphead);
        Log.d(spName, "Staff Data stored in SP");
        ed.commit();
    }

    public static void putLoggedInAsToSP(Activity activity, String loggedInAs) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SH_LOGGEDIN_AS, loggedInAs);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getLoginTypeFromSP(Activity activity) {
        String loggedInAs = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_LOGGEDIN_AS, "");
        return loggedInAs;
    }
    public static String getLoginTypeContextFromSP(Context activity) {
        String loggedInAs = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_LOGGEDIN_AS, "");
        return loggedInAs;
    }

    public static String getStaffIdFromSP(Activity activity) {
        String staffID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_STAFF_ID, "");
        return staffID;
    }

    public static String getSchoolIdFromSP(Activity activity) {
        String schoolID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_SCHOOL_ID, "");
        return schoolID;
    }

    public static String getSchoolNameFromSP(Activity activity) {
        String schoolName = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_SCHOOL_NAME, "");
        return schoolName;
    }

    public static String getShSchoolAddressFromSP(Activity activity) {
        String schoolAddress = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_SCHOOL_ADDRESS, "");
        return schoolAddress;
    }



    public static void putSchoolLogo(Activity activity, String logo) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SH_SCHOOL_LOGO, logo);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getSchoolLogo(Activity activity) {
        String schoolLogo = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_SCHOOL_LOGO, "");
        return schoolLogo;
    }

    public static void putEmerGencyCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Emergency_date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getEmergencyCurrentDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Emergency_date", "");
        return Date;
    }

    public static void putEventCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Event_Date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getEventDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Event_Date", "");
        return Date;
    }
    public static void putHolidayCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Holiday_date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getHolidayDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Holiday_date", "");
        return Date;
    }

    public static void putCircularCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Circular_date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getCircularDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Circular_date", "");
        return Date;
    }

    public static void putAssignmentCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Assignment_date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getAssignmentDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Assignment_date", "");
        return Date;
    }

    public static void putVideoCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Video_Date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getVideoDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Video_Date", "");
        return Date;
    }

    public static void putOnBackPressed(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBack", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethod(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBack", "");
        return onback;
    }

    public static void putOnBackPressedText(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackText", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodText(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackText", "");
        return onback;
    }

    public static void putOnBackPressedVoice(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackVoice", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodVoice(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackVoice", "");
        return onback;
    }

    public static void putOnBackPressedEmeVoice(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackEmeVoice", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodEmeVoice(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackEmeVoice", "");
        return onback;
    }

    public static void putOnBackPressedHWTEXT(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackHWText", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodHWTEXT(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackHWText", "");
        return onback;
    }

    public static void putOnBackPressedHWVOICE(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackHWVoice", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodHWVOICE(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackHWVoice", "");
        return onback;
    }

    public static void putOnBackPressedCirculars(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackCirculars", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodCirculars(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackCirculars", "");
        return onback;
    }

    public static void putOnBackPressedImages(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackImages", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodImages(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackImages", "");
        return onback;
    }
    public static void putOnBackPressedAssignmentParent(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackAssignmentParent", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodAssignmentParent(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackAssignmentParent", "");
        return onback;
    }

    public static void putOnBackPressedAssignmentStaff(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackAssignmentStaff", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodAssignmentStaff(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackAssignmentStaff", "");
        return onback;
    }

    public static void putOnBackPressedVideo(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackVideo", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodVideo(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackVideo", "");
        return onback;
    }

    public static void putOnBackPressedMFM(Activity activity, String onback) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("OnBackMFM", onback);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getOnBackMethodMFM(Activity activity) {
        String onback = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("OnBackMFM", "");
        return onback;
    }



    public static void putImageCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Image_date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getImageDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Image_date", "");
        return Date;
    }

    public static void putHomeWorkCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Homework_Date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getHomeWorkDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Homework_Date", "");
        return Date;
    }

    public static void putNoticeBoardCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("NoticeBoard_Date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getNoticeBoardDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("NoticeBoard_Date", "");
        return Date;
    }


    public static void putAttedanceCuurentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Attedance_Date", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getAttedanceCurrentDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Attedance_Date", "");
        return Date;
    }



    public static void putReadContactsPermission(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences("CONTACTS", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Permission", date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getReadContactsPermission(Activity activity) {
        String Date = activity.getSharedPreferences("CONTACTS", MODE_PRIVATE).getString("Permission", "");
        return Date;
    }


    public static void putParentStoragePermission(Context activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences("PARENT_STORAGE", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("parent_media", date);
        ed.commit();
    }
    public static String getParentStoragePermission(Context activity) {
        String Date = activity.getSharedPreferences("PARENT_STORAGE", MODE_PRIVATE).getString("parent_media", "");
        return Date;
    }


    public static void putParentStorageandCameraPermission(Context activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences("PARENT_STORAGE_CAMERA", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("parent_media_camera", date);
        ed.commit();
    }
    public static String getParentStorageandCameraPermission(Context activity) {
        String Date = activity.getSharedPreferences("PARENT_STORAGE_CAMERA", MODE_PRIVATE).getString("parent_media_camera", "");
        return Date;
    }




    public static void putStaffRecordPermission(Context activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences("STAFF_RECORD_VOICE", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("staff_record", date);
        ed.commit();
    }
    public static String getStaffRecordPermission(Context activity) {
        String Date = activity.getSharedPreferences("STAFF_RECORD_VOICE", MODE_PRIVATE).getString("staff_record", "");
        return Date;
    }

    public static void putStaffCameraPermission(Context activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences("STAFF_CAMERA", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("staff_cameras", date);
        ed.commit();
    }
    public static String getStaffCameraPermission(Context activity) {
        String Date = activity.getSharedPreferences("STAFF_CAMERA", MODE_PRIVATE).getString("staff_cameras", "");
        return Date;
    }

    public static void putDateListVoiceCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Current_Date_voive, date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getDateListVoiceDate(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(Current_Date_voive, "");
        return Date;
    }
    public static void putDateListCurrentDate(Activity activity, String date) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(Current_Date, date);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }
    public static String getCurrent_Date_DateList(Activity activity) {
        String Date = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(Current_Date, "");
        return Date;
    }


    public static String getSchoolLogoUrlFromSP(Activity activity) {
        String schoolLogo = activity.getSharedPreferences(spName, MODE_PRIVATE).getString(SH_SCHOOL_LOGO, "");
        return schoolLogo;
    }

    public static boolean isAdminFromSP(Activity activity) {
        boolean bAdmin = activity.getSharedPreferences(spName, MODE_PRIVATE).getBoolean(SH_IS_ADMIN, false);
        return bAdmin;
    }

    public static boolean isManagementFromSP(Activity activity) {
        boolean bManagement = activity.getSharedPreferences(spName, MODE_PRIVATE).getBoolean(SH_IS_MANAGEMENT, false);
        return bManagement;
    }

    public static boolean isStaffFromSP(Activity activity) {
        boolean bStaff = activity.getSharedPreferences(spName, MODE_PRIVATE).getBoolean(SH_IS_STAFF, false);
        return bStaff;
    }

    public static void clearStaffSharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spName, MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public static class OnSharedPreferenceChangeListener {
    }



    public static void PutChildrenScreenMyArray(Context context, ArrayList<String> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static ArrayList<String> getChildrenScreenMyArray(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }


    public static void PutChildrenScreenschoolmodel(Context context, TeacherSchoolsModel arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static TeacherSchoolsModel getChildrenScreenSchoolModel(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<TeacherSchoolsModel>() {}.getType();
        return gson.fromJson(arrayList, type);
    }

    public static void putChildItems(Context context, Profiles arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static Profiles getChildItems(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<Profiles>() {}.getType();
        return gson.fromJson(arrayList, type);
    }



    public static void putChildrenDetails(Context context, ArrayList<Profiles> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static ArrayList<Profiles> getChildrenDetails(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<Profiles>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }




    public static void putChildrenScreenSchool_list(Context context, ArrayList<TeacherSchoolsModel> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static ArrayList<TeacherSchoolsModel> getChildrenScreenSchools_List(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<TeacherSchoolsModel>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }


    public static void putLanguages(Context context, ArrayList<Languages> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }
    public static ArrayList<Languages> getLanguages(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<Languages>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }


    public  static final String school_name="school";
    public  static final String school_Address="address";
    public  static final String school_SH="school_SH";


    public static void putChildrenScreenSchoolName(Activity activity, String value)
    {
        SharedPreferences sp = activity.getSharedPreferences(school_SH, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(school_name, value);
        ed.commit();
    }

    public static String getChildrenScreenSchoolName(Activity activity) {
        String value = activity.getSharedPreferences(school_SH, Context.MODE_PRIVATE).getString(school_name, "");
        return value;
    }
    public static void putChildrenScreenSchoolAddress(Activity activity, String value)
    {
        SharedPreferences sp = activity.getSharedPreferences(school_SH, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(school_Address, value);
        ed.commit();
    }

    public static String getChildrenScreenSchoolAddress(Activity activity) {
        String value = activity.getSharedPreferences(school_SH, Context.MODE_PRIVATE).getString(school_Address, "");
        return value;
    }















    public static void listSchoolDetails(Context context, ArrayList<TeacherSchoolsModel> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static ArrayList<TeacherSchoolsModel> getlistSchoolDetails(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<TeacherSchoolsModel>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }

    public static void schoollist(Context context, ArrayList<String> arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static ArrayList<TeacherSchoolsModel> getschoollist(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<ArrayList<String>>() {}.getType();

        return gson.fromJson(arrayList, type);
    }


    public static void schoolmodel(Context context, TeacherSchoolsModel arrayList, String key) {
        SharedPreferences db= PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor collection = db.edit();
        Gson gson = new Gson();
        String arrayList1 = gson.toJson(arrayList);

        collection.putString(key, arrayList1);
        collection.commit();
    }

    public static TeacherSchoolsModel getschoolmodel(Context context, String key) {
        SharedPreferences db=PreferenceManager.getDefaultSharedPreferences(context);

        Gson gson = new Gson();
        String arrayList = db.getString(key, "");
        Type type = new TypeToken<TeacherSchoolsModel>() {}.getType();
        return gson.fromJson(arrayList, type);
    }


    public static void putShoolID(Activity activity, String logo) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("SchoolID", logo);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getSchoolID(Activity activity) {
        String schoolID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("SchoolID", "");
        return schoolID;
    }


    public static void putStaffAddress(Activity activity, String logo) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Address", logo);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getStaffAddress(Activity activity) {
        String StaffID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Address", "");
        return StaffID;
    }


    public static void putSchoolName(Activity activity, String logo) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Name", logo);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getSchoolName(Activity activity) {
        String StaffID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("Name", "");
        return StaffID;
    }


    public static void putStaffID(Activity activity, String logo) {
        SharedPreferences sp = activity.getSharedPreferences(spName, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("StaffID", logo);
        Log.d(spName, "Login Type stored in SP");
        ed.commit();
    }

    public static String getStaffID(Activity activity) {
        String StaffID = activity.getSharedPreferences(spName, MODE_PRIVATE).getString("StaffID", "");
        return StaffID;
    }

    public static final String spquiz = "SP_QUIZ";
    public static void putQuizid(Activity activity, int quizid) {
        SharedPreferences sp = activity.getSharedPreferences(spquiz, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("Quizid", quizid);
        Log.d(spquiz, "Login Type stored in SP");
        ed.commit();
    }

    public static int getquizid(Activity activity) {
        int schoolID = activity.getSharedPreferences(spquiz, MODE_PRIVATE).getInt("Quizid", 0);
        return schoolID;
    }

    public static void putQuiztime(Activity activity, String time) {
        SharedPreferences sp = activity.getSharedPreferences(spquiz, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("Quiztime", time);
        Log.d(spquiz, "Login Type stored in SP");
        ed.commit();
    }

    public static String getQuiztime(Activity activity) {
        String quiztime = activity.getSharedPreferences(spquiz, MODE_PRIVATE).getString("Quiztime", "");
        return quiztime;
    }

}
