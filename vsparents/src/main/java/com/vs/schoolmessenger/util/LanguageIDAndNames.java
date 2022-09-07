package com.vs.schoolmessenger.util;

import android.app.Activity;

import com.vs.schoolmessenger.activity.ChildrenScreen;

import java.util.ArrayList;

public class  LanguageIDAndNames {

   static ArrayList<Integer> isAdminMenuID = new ArrayList<>();
   static ArrayList<Integer> isStaffMenuID = new ArrayList<>();
    static  ArrayList<Integer> isPrincipalMenuID = new ArrayList<>();
    static ArrayList<Integer> isGroupHedMenuID = new ArrayList<>();
    static  ArrayList<Integer> isParentMenuID = new ArrayList<>();


    static  ArrayList<String> isAdminMenuNames = new ArrayList<>();
    static ArrayList<String> isIsStaffMenuNames = new ArrayList<>();
    static ArrayList<String> isPrincipalMenuNames = new ArrayList<>();
    static ArrayList<String> isGroupHedMenuNames = new ArrayList<>();
    static  ArrayList<String> isParentMenuNames = new ArrayList<>();


    public static void putParentNamestoSharedPref(String isParent,Activity activity) {
        String[] name4 = isParent.split(",");

        isParentMenuNames.clear();

        for (String itemtemp : name4) {
            isParentMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putParentMenuNames(isParentMenuNames, activity);

    }

    public static void putGroupHeadtoSharedPref(String idGroupHead,Activity activity) {
        String[] name3 = idGroupHead.split(",");
        isGroupHedMenuNames.clear();

        for (String itemtemp : name3) {
            isGroupHedMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putGroupHeadNames(isGroupHedMenuNames, activity);

    }

    public static void putAdminNamestoSharedPref(String isAdmin,Activity activity) {
        String[] name2 = isAdmin.split(",");
        isAdminMenuNames.clear();
        for (String itemtemp : name2) {
            isAdminMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putAdminNames(isAdminMenuNames, activity);

    }

    public static void putStaffNamestoSharedPref(String isStaff,Activity activity) {
        String[] name1 = isStaff.split(",");
        isIsStaffMenuNames.clear();
        for (String itemtemp : name1) {
            isIsStaffMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putStaffNames(isIsStaffMenuNames, activity);

    }

    public static void putPrincipalNametoSharedPref(String isPrincipal,Activity activity) {
        String[] name = isPrincipal.split(",");
        isPrincipalMenuNames.clear();
        for (String itemtemp : name) {
            isPrincipalMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putPrincipalNames(isPrincipalMenuNames, activity);

    }

    public static  void putParentIdstoSharedPref(String isParentID,Activity activity) {
        String[] items4 = isParentID.split(",");
        isParentMenuID.clear();
        for (String itemtemp : items4) {
            int result = Integer.parseInt(itemtemp);
            isParentMenuID.add(result);

        }

        TeacherUtil_SharedPreference.putparentIDs(isParentMenuID, activity);

    }

    public static  void putGroupHeadIdstosharedPref(String idGroupHeadID,Activity activity) {
        String[] items3 = idGroupHeadID.split(",");
        isGroupHedMenuID.clear();
        for (String itemtemp : items3) {
            int result = Integer.parseInt(itemtemp);
            isGroupHedMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putGroupHeadIDs(isGroupHedMenuID, activity);
    }

    public static void putAdminIdstoSharedPref(String isAdminID,Activity activity) {
        String[] items2 = isAdminID.split(",");

        isAdminMenuID.clear();

        for (String itemtemp : items2) {
            int result = Integer.parseInt(itemtemp);
            isAdminMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putAdminIDs(isAdminMenuID, activity);
    }

    public static void putStaffIdstoSharedPref(String isStaffID,Activity activity) {
        String[] items1 = isStaffID.split(",");
        isStaffMenuID.clear();
        for (String itemtemp : items1) {
            int result = Integer.parseInt(itemtemp);
            isStaffMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putStaffIDs(isStaffMenuID, activity);
    }

    public static void putPrincipalIdstoSharedPref(String isPrincipalID, Activity activity) {
        String[] items = isPrincipalID.split(",");
        isPrincipalMenuID.clear();
        for (String itemtemp : items) {
            int result = Integer.parseInt(itemtemp);
            isPrincipalMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putPrincipalIDs(isPrincipalMenuID, activity);
    }
}
