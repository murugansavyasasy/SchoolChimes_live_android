package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 8/31/2016.
 */

@SuppressWarnings("serial")
public class TeacherProfiles implements Serializable {
    private String staffName, staffID, schoolID, schoolName, schoolAddress, schoolLogoUrl;
    private boolean isAdmin;
    private boolean isManagement;
    private boolean isStaff;
    private boolean isGrouphead;

    public TeacherProfiles() {
    }

    public TeacherProfiles(String staffID) {
        this.staffID = staffID;
    }

    public TeacherProfiles(String staffName, String staffID, String schoolID, String schoolName, String schoolAddress,
                           String schoolLogoUrl, boolean isAdmin, boolean isManagement, boolean isStaff, boolean isGrouphead) {
        this.staffName = staffName;
        this.staffID = staffID;
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
        this.schoolLogoUrl = schoolLogoUrl;
        this.isAdmin = isAdmin;
        this.isManagement = isManagement;
        this.isStaff = isStaff;
        this.isGrouphead = isGrouphead;
    }

    public boolean isGrouphead() {
        return isGrouphead;
    }

    public void setGrouphead(boolean grouphead) {
        isGrouphead = grouphead;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getSchoolLogoUrl() {
        return schoolLogoUrl;
    }

    public void setSchoolLogoUrl(String schoolLogoUrl) {
        this.schoolLogoUrl = schoolLogoUrl;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public boolean isManagement() {
        return isManagement;
    }

    public void setManagement(boolean management) {
        isManagement = management;
    }

    public boolean isStaff() {
        return isStaff;
    }

    public void setStaff(boolean staff) {
        isStaff = staff;
    }
}
