package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class LeaveRequestDetails implements Serializable {
    private String Id, Name, CLS,Section,LeaveAppliedOn,LeaveFromDate,LeaveToDate,Reason,Approved,UpdatedOn;
    private  boolean loginType;




    public LeaveRequestDetails(String Id, String Name, String cls,String section,String leaveappliedOn,
                               String leaveFromdate,String leaveTodate,String reason,String approved,boolean type,String updated) {
        this.Id = Id;
        this.Name = Name;
        this.CLS = cls;
        this.Section = section;
        this.LeaveAppliedOn = leaveappliedOn;
        this.LeaveFromDate = leaveFromdate;
        this.LeaveToDate = leaveTodate;
        this.Reason = reason;
        this.Approved = approved;
        this.loginType = type;
        this.UpdatedOn = updated;
    }

    public LeaveRequestDetails() {}

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        this.Section = section;
    }

    public String getCLS() {
        return CLS;
    }

    public void setCLS(String cls) {
        this.CLS = cls;
    }

    public String getLeaveAppliedOn() {
        return LeaveAppliedOn;
    }

    public void setLeaveAppliedOn(String apply) {
        this.LeaveAppliedOn = apply;
    }

    public String getLeaveFromDate() {
        return LeaveFromDate;
    }

    public void setLeaveFromDate(String from) {
        this.LeaveFromDate = from;
    }

    public String getLeaveToDate() {
        return LeaveToDate;
    }

    public void setLeaveToDate(String to) {
        this.LeaveToDate = to;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        this.Reason = reason;
    }

    public String getApproved() {
        return Approved;
    }

    public void setApproved(String aprove) {
        this.Approved = aprove;
    }

    public boolean getLoginType() {
        return loginType;
    }

    public void setLoginType(boolean aprove) {
        this.loginType = aprove;
    }

    public String getUpdatedOn() {
        return UpdatedOn;
    }

    public void setUpdatedOn(String updated) {
        this.UpdatedOn = updated;
    }
}
