package com.vs.schoolmessenger.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AttendanceStudentList {
    @SerializedName("status")
    int status;

    @SerializedName("message")
    String message;

    @SerializedName("data")
    List<AttendanceListStudentData> data;


    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(List<AttendanceListStudentData> data) {
        this.data = data;
    }
    public List<AttendanceListStudentData> getData() {
        return data;
    }
}