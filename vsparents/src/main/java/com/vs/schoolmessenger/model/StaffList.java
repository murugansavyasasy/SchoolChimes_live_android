package com.vs.schoolmessenger.model;

public class StaffList {
    private String StaffId, StaffName, StaffType, StaffMobile, Designation, MemberRole;
    private  boolean selecteStatus;

    public StaffList(String staffId, String staffName, String staffType, String staffMobile, String designation, boolean selecteStatus, String isStaffRole) {
        this.StaffId = staffId;
        this.StaffName = staffName;
        this.StaffType = staffType;
        this.StaffMobile = staffMobile;
        this.Designation = designation;
        this.selecteStatus = selecteStatus;
        this.MemberRole = isStaffRole;
    }

    public StaffList() {}

    public String getStaffId() {
        return StaffId;
    }

    public void setStaffId(String id) {
        this.StaffId = id;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String name) {
        this.StaffName = name;
    }


    public String getStaffRole() {
        return MemberRole;
    }

    public void setStaffRole(String staffRole) {
        this.MemberRole = staffRole;
    }

    public String getStaffType() {
        return StaffType;
    }

    public void setStaffType(String type) {
        this.StaffType = type;
    }

    public String getStaffMobile() {
        return StaffMobile;
    }

    public void setStaffMobile(String mobile) {
        this.StaffMobile = mobile;
    }

    public String getDesignation() {
        return Designation;
    }

    public void setDesignation(String designation) {
        this.Designation = designation;
    }

    public boolean getSelecteStatus() {
        return selecteStatus;
    }

    public void setSelecteStatus(boolean status) {
        this.selecteStatus = status;
    }
}
