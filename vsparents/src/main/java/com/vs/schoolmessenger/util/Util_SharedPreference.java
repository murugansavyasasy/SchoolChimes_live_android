package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by voicesnap on 9/17/2016.
 */
public class Util_SharedPreference {
    public static final String spCountryName = "SP_MESSENGER_COUNTRY";
    public static final String SH_COUNTRY_ID = "CountryID";
    public static final String SH_COUNTRY_NAME = "CountryName";
    public static final String SH_COUNTRY_MOBILE_LENGTH = "CountryMobileLength";
    public static final String SH_COUNTRY_CODE = "CountryCode";
    public static final String SH_COUNTRY_BASE_URL = "BaseURL";

    public static final String SH_AUTOUPDATE="Autoupdate";
    public static final String SH_UPDATE="update";
    public static final String SH_autoupdate="false";

    public static final String Staff_Details="Details";
    public static final String forget_password="";
    public static final String FORGET_PASS="ForgetValue";


    public static void putForget(Activity activity, String status) {
        SharedPreferences sp = activity.getSharedPreferences(FORGET_PASS, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(forget_password, status);
        ed.commit();
    }

    public static String getForget(Activity activity) {
        String Id = activity.getSharedPreferences(FORGET_PASS, Context.MODE_PRIVATE).getString(forget_password, "");
        return Id;
    }



    public static void putStaffDetails(Activity activity, String ID,String Shool_id) {
        SharedPreferences sp = activity.getSharedPreferences(Staff_Details, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString("ID", ID);
        ed.putString("SHOOL_ID", Shool_id);


        Log.d(Staff_Details, "Country Data stored in SP");
        ed.commit();
    }

    public static String getStaffID(Activity activity) {
        String Id = activity.getSharedPreferences(Staff_Details, Context.MODE_PRIVATE).getString("ID", "");
        return Id;
    }
    public static String getSchoolId(Activity activity) {
        String Scoolid = activity.getSharedPreferences(Staff_Details, Context.MODE_PRIVATE).getString("SHOOL_ID", "");
        return Scoolid;
    }


    public static void putCountryInforToSP(Activity activity, String CountryID,
                                           String CountryName, String CountryMobileLength, String CountryCode, String BaseURL) {
        SharedPreferences sp = activity.getSharedPreferences(spCountryName, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putString(SH_COUNTRY_ID, CountryID);
        ed.putString(SH_COUNTRY_NAME, CountryName);
        ed.putString(SH_COUNTRY_MOBILE_LENGTH, CountryMobileLength);
        ed.putString(SH_COUNTRY_CODE, CountryCode);
        ed.putString(SH_COUNTRY_BASE_URL, BaseURL);
        Log.d("CountryCode", CountryCode);
        Log.d(spCountryName, "Country Data stored in SP");
        ed.commit();
    }
    public static String getShCountryCode(Activity activity) {
        String Code = activity.getSharedPreferences(spCountryName, Context.MODE_PRIVATE).getString( SH_COUNTRY_CODE, "");
        return Code;
    }
    public static String getBaseUrlFromSP(Activity activity) {
        String baseURL = activity.getSharedPreferences(spCountryName, Context.MODE_PRIVATE).getString(SH_COUNTRY_BASE_URL, "");
        return baseURL;
    }
    public static String getMobileNumberLengthFromSP(Activity activity) {
        String mobileNumberlength = activity.getSharedPreferences(spCountryName, Context.MODE_PRIVATE).getString(SH_COUNTRY_MOBILE_LENGTH, "");
        return mobileNumberlength;
    }
    public static void clearCountrySharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spCountryName, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }
    public static String getupdateversionFromSP(Activity activity) {
        String strupdate = activity.getSharedPreferences(SH_AUTOUPDATE, Context.MODE_PRIVATE).getString(SH_UPDATE, "");
        return strupdate;
    }

    public static void putautoupdateToSP(Activity activity,String autoupdate){
        SharedPreferences prefs = activity.getSharedPreferences(SH_AUTOUPDATE, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = prefs.edit();

        ed.putString(SH_UPDATE, autoupdate);
        Log.d(SH_AUTOUPDATE, "autoupdate Data stored in SP");
        ed.commit();
    }
    //*************

    public static final String spName = "SP_MESSENGER_PARENTS";

    public static final String SH_PARENT_MOBILE = "mobileNumber";
    public static final String SH_PARENT_PASSWORD = "password";
    public static final String SH_REMEMBER_PASSWORD = "rememberMe";

    public static void putParentLoginInfoToSP(Activity activity, String mobileNumber, String password, boolean rememberMe) {
        SharedPreferences sp = activity.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(SH_PARENT_MOBILE, mobileNumber);
        ed.putString(SH_PARENT_PASSWORD, password);
        ed.putBoolean(SH_REMEMBER_PASSWORD, rememberMe);

        Log.d(spName, "Parent Login Data stored in SP");
        ed.commit();
    }

    public static String getMobileNumberFromSP(Activity activity) {
        String mobileNumber = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_PARENT_MOBILE, "");
        return mobileNumber;
    }

    public static String getPasswordFromSP(Activity activity) {
        String password = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_PARENT_PASSWORD, "");
        return password;
    }

    public static boolean getAutoLoginStatusFromSP(Activity activity) {
        boolean rememberMe = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getBoolean(SH_REMEMBER_PASSWORD, false);
        return rememberMe;
    }

    public static void clearParentSharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    //********************

    public static final String SH_CHILD_ID = "ChildID";
    public static final String SH_CHILD_NAME = "ChildName";
    public static final String SH_SCHOOL_ID = "SchoolID";
    public static final String SH_SCHOOL_NAME = "SchoolName";
    public static final String SH_SCHOOL_ADDRESS = "SchoolAddress";
    public static final String SH_SCHOOL_LOGO = "SchoolLogo";
    public static final String SH_STANDARD = "Standard";
    public static final String SH_SECTION = "section";

    public static void putSelecedChildInfoToSP(Activity activity, String childID, String childName, String schoolID,
                                               String schoolName, String schoolAddress, String schoolLogo,String standard,String section) {
        SharedPreferences sp = activity.getSharedPreferences(spName, Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        ed.putString(SH_CHILD_ID, childID);
        ed.putString(SH_CHILD_NAME, childName);
        ed.putString(SH_SCHOOL_ID, schoolID);
        ed.putString(SH_SCHOOL_NAME, schoolName);
        ed.putString(SH_SCHOOL_ADDRESS, schoolAddress);
        ed.putString(SH_SCHOOL_LOGO, schoolLogo);
        ed.putString(SH_STANDARD, standard);
        ed.putString(SH_SECTION, section);

        Log.d(spName, "Child Data stored in SP");
        ed.commit();
    }

    public static String getChildNameFromSP(Activity activity) {
        String childname = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_CHILD_NAME, "");
        return childname;
    }

    public static String getStandardFromSP(Activity activity) {
        String standard = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_STANDARD, "");
        return standard;
    }
    public static String getSectionFromSP(Activity activity) {
        String section = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_SECTION, "");
        return section;
    }


    public static String getChildIdFromSP(Context activity) {
        String childID = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_CHILD_ID, "");
        return childID;
    }

    public static String getSchoolIdFromSP(Context activity) {
        String schoolID = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_SCHOOL_ID, "");
        return schoolID;
    }

    public static String getSchoolnameFromSP(Activity activity) {
        String schoolname = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_SCHOOL_NAME, "");
        return schoolname;
    }

    public static String getSchooladdressFromSP(Activity activity) {
        String schooladdr = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_SCHOOL_ADDRESS, "");
        return schooladdr;
    }

    public static String getSchoollogoFromSP(Activity activity) {
        String schoollogo = activity.getSharedPreferences(spName, Context.MODE_PRIVATE).getString(SH_SCHOOL_LOGO, "");
        return schoollogo;
    }

    public static void clearChildSharedPreference(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(spName, Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    //**************

}
