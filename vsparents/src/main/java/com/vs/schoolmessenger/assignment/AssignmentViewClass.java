package com.vs.schoolmessenger.assignment;

import java.io.Serializable;

public class AssignmentViewClass implements Serializable {
   String DeTailId,AssignmentId, Type, Content, Title, Date, Time, submittedCount,totalcount,enddate,isAppread,Category;
   Boolean is_Archive;

    public AssignmentViewClass(String assignmentId, String type, String content, String title, String date, String time, String submittedCount
    ,String totalcount,String enddate,String isAppread,String DeTailId,Boolean archive,String category) {
        AssignmentId = assignmentId;
        Type = type;
        Content = content;
        Title = title;
        Date = date;
        Time = time;
        this.submittedCount = submittedCount;
        this.totalcount = totalcount;
        this.enddate = enddate;
        this.isAppread = isAppread;
        this.DeTailId = DeTailId;
        this.is_Archive = archive;
        this.Category = category;
    }

    public AssignmentViewClass() {}

    public String getDeTailId() {
        return DeTailId;
    }

    public void setDeTailId(String deTailId) {
        DeTailId = deTailId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getIsAppread() {
        return isAppread;
    }

    public void setIsAppread(String isAppread) {
        this.isAppread = isAppread;
    }

    public String getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(String totalcount) {
        this.totalcount = totalcount;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getAssignmentId() {
        return AssignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        AssignmentId = assignmentId;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getSubmittedCount() {
        return submittedCount;
    }

    public void setSubmittedCount(String submittedCount) {
        this.submittedCount = submittedCount;
    }

    public Boolean getIs_Archive() {
        return is_Archive;
    }

    public void setIs_Archive(Boolean archive) {
        this.is_Archive = archive;
    }
}
