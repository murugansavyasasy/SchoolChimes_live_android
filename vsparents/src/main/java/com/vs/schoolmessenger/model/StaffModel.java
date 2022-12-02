package com.vs.schoolmessenger.model;

public class StaffModel {
    private String SubName, staffName,StaffId;

    public StaffModel( String subname, String StaffName,String staffid) {
        this.SubName = subname;
        this.staffName = StaffName;
        this.StaffId = staffid;
    }

    public StaffModel() {}

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String Id) {
        this.SubName = Id;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String name) {
        this.staffName = name;
    }

    public String getStaffId() {
        return StaffId;
    }

    public void setStaffId(String name) {
        this.StaffId = name;
    }
}
