package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TodaySlotsStaff {
    @SerializedName("Status")
    int status;

    @SerializedName("Message")
    String message;

    @SerializedName("data")
    List<TodaySlotsData> data;

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

    public void setData(List<TodaySlotsData> data) {
        this.data = data;
    }

    public List<TodaySlotsData> getData() {
        return data;
    }

}