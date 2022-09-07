package com.vs.schoolmessenger.model;

public class SubjectDetails
{
    String strSubName, strSubCode,Date,MaxMark,Session,SubjectSyllabus;
    boolean SelectedStatus;

    public SubjectDetails() {
    }

    public SubjectDetails(String strSubName, String strSubCode,String date,String maxmark,String session,boolean selectedStatus,String syllabus) {
        this.strSubName = strSubName;
        this.strSubCode = strSubCode;
        this.Date = date;
        this.MaxMark = maxmark;
        this.Session = session;
        this.SelectedStatus = selectedStatus;
        this.SubjectSyllabus = syllabus;

    }

    public String getSubjectSyllabus() {
        return SubjectSyllabus;
    }

    public void setSubjectSyllabus(String sylabus) {
        this.SubjectSyllabus = sylabus;
    }

    public String getStrSubName() {
        return strSubName;
    }

    public void setStrSubName(String strSubName) {
        this.strSubName = strSubName;
    }

    public String getStrSubCode() {
        return strSubCode;
    }

    public void setStrSubCode(String strSubCode) {
        this.strSubCode = strSubCode;
    }


    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        this.Date = date;
    }

    public String getMaxMark() {
        return MaxMark;
    }

    public void setMaxMark(String maxMark) {
        this.MaxMark = maxMark;
    }

    public String getSession() {
        return Session;
    }

    public void setSession(String session) {
        this.Session = session;
    }

    public boolean getSelectedStatus() {
        return SelectedStatus;
    }

    public void setSelectedStatus(boolean SelectedStatus) {
        this.SelectedStatus = SelectedStatus;
    }

}
