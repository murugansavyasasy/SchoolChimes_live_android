package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SlotsHistory {

    @SerializedName("Status")
    int Status;

    @SerializedName("Message")
    String Message;

    @SerializedName("data")
    List<SlotsHistoryData> data;

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

    public void setData(List<SlotsHistoryData> data) {
        this.data = data;
    }

    public List<SlotsHistoryData> getData() {
        return data;
    }

}
