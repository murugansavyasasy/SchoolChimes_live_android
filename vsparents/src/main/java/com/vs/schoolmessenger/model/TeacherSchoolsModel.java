package com.vs.schoolmessenger.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by voicesnap on 6/7/2017.
 */

public class TeacherSchoolsModel implements Serializable {
    String strSchoolName;
    String SchoolNameRegional;
    String strSchoolID;
    String strSchoolLogoUrl;
    String strSchoolAddress;
    String strCity;
    String strStaffID;
    String strStaffName;

    int isSchoolType;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;
    ArrayList<TeacherSchoolsModel> listSchools;
    boolean selectStatus;
    String onlineLink;
    String BookEnable;
    String isPaymentPending;
    int isBiometricEnable;
    boolean allowVideoDownload;

    public TeacherSchoolsModel() {
    }

    public TeacherSchoolsModel(String strSchoolName, String SchoolNameRegional, String strSchoolID, String strCity, String strSchoolAddress, String strSchoolLogoUrl, String strStaffID, String strStaffName, boolean selectStatus,
                               String bookEnable, String Link, String isPayment, int isSchoolType, int isBiometricEnable, boolean allowVideoDownload) {

        this.strSchoolName = strSchoolName;
        this.SchoolNameRegional = SchoolNameRegional;
        this.strSchoolID = strSchoolID;
        this.strCity = strCity;
        this.strSchoolAddress = strSchoolAddress;
        this.strSchoolLogoUrl = strSchoolLogoUrl;
        this.strStaffID = strStaffID;
        this.strStaffName = strStaffName;
        this.selectStatus = selectStatus;
        this.onlineLink = Link;
        this.BookEnable = bookEnable;
        this.isPaymentPending = isPayment;
        this.isSchoolType = isSchoolType;
        this.isBiometricEnable = isBiometricEnable;
        this.allowVideoDownload = allowVideoDownload;
    }


    public TeacherSchoolsModel(String strSchoolName, String strSchoolID, String strSchoolLogoUrl, String strSchoolAddress, boolean selectStatus) {
        this.strSchoolName = strSchoolName;
        this.strSchoolID = strSchoolID;
        this.strSchoolLogoUrl = strSchoolLogoUrl;
        this.strSchoolAddress = strSchoolAddress;
        this.selectStatus = selectStatus;
    }


    public boolean getAllowDownload() {
        return allowVideoDownload;
    }

    public void setAllowVideoDownload(boolean allowVideoDownload) {
        this.allowVideoDownload = allowVideoDownload;
    }

    public int getIsBiometricEnable() {
        return isBiometricEnable;
    }

    public void setIsBiometricEnable(int isBiometricEnable) {
        this.isBiometricEnable = isBiometricEnable;
    }

    public String getBookEnable() {
        return BookEnable;
    }

    public void setBookEnable(String bookEnable) {
        this.BookEnable = bookEnable;
    }

    public String getIsPaymentPending() {
        return isPaymentPending;
    }

    public void setIsPaymentPending(String bookEnable) {
        this.isPaymentPending = bookEnable;
    }

    public int getIsSchoolType() {
        return isSchoolType;
    }

    public void setIsSchoolType(int isSchoolType) {
        this.isSchoolType = isSchoolType;
    }


    public String getOnlineLink() {
        return onlineLink;
    }

    public void setOnlineLink(String link) {
        this.onlineLink = link;
    }


    public String getStrCity() {
        return strCity;
    }

    public void setStrCity(String strCity) {
        this.strCity = strCity;
    }

    public String getStrStaffName() {
        return strStaffName;
    }

    public void setStrStaffName(String strStaffName) {
        this.strStaffName = strStaffName;
    }

    public String getStrStaffID() {
        return strStaffID;
    }

    public void setStrStaffID(String strStaffID) {
        this.strStaffID = strStaffID;
    }

    public String getStrSchoolName() {
        return strSchoolName;
    }

    public void setStrSchoolName(String strSchoolName) {
        this.strSchoolName = strSchoolName;
    }

    public String getSchoolNameRegional() {
        return SchoolNameRegional;
    }

    public void setSchoolNameRegional(String strSchoolName) {
        this.SchoolNameRegional = strSchoolName;
    }

    public String getStrSchoolID() {
        return strSchoolID;
    }

    public void setStrSchoolID(String strSchoolID) {
        this.strSchoolID = strSchoolID;
    }

    public String getStrSchoolLogoUrl() {
        return strSchoolLogoUrl;
    }

    public void setStrSchoolLogoUrl(String strSchoolLogoUrl) {
        this.strSchoolLogoUrl = strSchoolLogoUrl;
    }

    public String getStrSchoolAddress() {
        return strSchoolAddress;
    }

    public void setStrSchoolAddress(String strSchoolAddress) {
        this.strSchoolAddress = strSchoolAddress;
    }

    public boolean isSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
    }

    public ArrayList<TeacherClassGroupModel> getListClasses() {
        return listClasses;
    }

    public void setListClasses(ArrayList<TeacherClassGroupModel> listClasses) {
        this.listClasses = listClasses;
    }

    public ArrayList<TeacherClassGroupModel> getListGroups() {
        return listGroups;
    }

    public void setListGroups(ArrayList<TeacherClassGroupModel> listGroups) {
        this.listGroups = listGroups;
    }

    public ArrayList<TeacherSchoolsModel> getListSchools() {
        return listSchools;
    }

    public void setListSchools(ArrayList<TeacherSchoolsModel> listSchools) {
        this.listSchools = listSchools;
    }
}
