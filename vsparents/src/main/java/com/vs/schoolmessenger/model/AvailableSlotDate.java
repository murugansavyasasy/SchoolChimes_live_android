package com.vs.schoolmessenger.model;

import java.util.List;

public class AvailableSlotDate {

    //    @SerializedName("status")
    private int status;

    //    @SerializedName("message")
    private String message;

    //    @SerializedName("data")
    private List<DateEventModel> data;

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

    public void setData(List<DateEventModel> data) {
        this.data = data;
    }

    public List<DateEventModel> getData() {
        return data;
    }

}
