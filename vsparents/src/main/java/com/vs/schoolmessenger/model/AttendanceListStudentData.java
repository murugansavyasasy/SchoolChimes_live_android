package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class AttendanceListStudentData {

    @SerializedName("studentName")
    String studentName;

    @SerializedName("studentId")
    int studentId;

    @SerializedName("admissionNo")
    String admissionNo;

    @SerializedName("rollNo")
    String rollNo;

    @SerializedName("photoPath")
    String photoPath;

    @SerializedName("primaryMobile")
    String primaryMobile;


    public AttendanceListStudentData(int studenID, String Name, String number, String imagepath,String admissionNo,String rollNo) {
        this.studentId = studenID;
        this.studentName = Name;
        this.primaryMobile = number;
        this.photoPath = imagepath;
        this.admissionNo=admissionNo;
        this.rollNo=rollNo;
    }


    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setPrimaryMobile(String primaryMobile) {
        this.primaryMobile = primaryMobile;
    }

    public String getPrimaryMobile() {
        return primaryMobile;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setPhotoPath(String rollNo) {
        this.photoPath = rollNo;
    }

    public String getPhotoPath() {
        return photoPath;
    }

}