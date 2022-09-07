package com.vs.schoolmessenger.model;

public class RequestMeetingForParentModel {


    private String RequestId,StaffName, ParentComment,ApprovalStatus,StaffComment,ScheduleDate,ScheduleTime,RequestedON,ApprovedOn,CLS;

    public RequestMeetingForParentModel(String id, String name, String parentcomment, String status, String staffcomment, String date, String time, String requestedOn, String approvedOn,String cls  ) {
        this.RequestId = id;
        this.StaffName = name;
        this.ParentComment = parentcomment;
        this.ApprovalStatus = status;
        this.StaffComment = staffcomment;
        this.ScheduleDate = date;
        this.ScheduleTime = time;
        this.RequestedON = requestedOn;
        this.ApprovedOn = approvedOn;
        this.CLS = cls;



    }

    public RequestMeetingForParentModel() {}

    public String getRequestId() {
        return RequestId;
    }

    public void setRequestId(String Id) {
        this.RequestId = Id;
    }

    public String getStaffName() {
        return StaffName;
    }

    public void setStaffName(String total) {
        this.StaffName = total;
    }

    public String getApprovalStatus() {
        return ApprovalStatus;
    }

    public void setApprovalStatus(String type) {
        this.ApprovalStatus = type;
    }

    public String getStaffComment() {
        return StaffComment;
    }

    public void setStaffComment(String create) {
        this.StaffComment = create;
    }
    public String getParentComment() {
        return ParentComment;
    }

    public void setParentComment(String late) {
        this.ParentComment = late;
    }

    public String getScheduleDate() {
        return ScheduleDate;
    }

    public void setScheduleDate(String reject) {
        this.ScheduleDate = reject;
    }


    public String getScheduleTime() {
        return ScheduleTime;
    }

    public void setScheduleTime(String reject) {
        this.ScheduleTime = reject;
    }
    public String getRequestedON() {
        return RequestedON;
    }

    public void setRequestedON(String reject) {
        this.RequestedON = reject;
    }
    public String getApprovedOn() {
        return ApprovedOn;
    }

    public void setApprovedOn(String reject) {
        this.ApprovedOn = reject;
    }

    public String getCLS() {
        return CLS;
    }

    public void setCLS(String reject) {
        this.CLS = reject;
    }
}


