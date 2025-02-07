package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TodaySlotsStaff {
    @SerializedName("Status")
    int Status;

    @SerializedName("Message")
    String Message;

    @SerializedName("data")
    List<TodaySlotsData> data;

    public void setStatus(int status) {
        this.Status = status;
    }

    public int getStatus() {
        return Status;
    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public String getMessage() {
        return Message;
    }

    public void setData(List<TodaySlotsData> data) {
        this.data = data;
    }

    public List<TodaySlotsData> getData() {
        return data;
    }

}
