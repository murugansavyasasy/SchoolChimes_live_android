package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class getSlotAvailability {

    @SerializedName("status")
    String Status;

    @SerializedName("message")
    String Message;

    @SerializedName("data")
    List<getSlotAvailabilityData> getSlotAvailabilityDataList;


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        this.Status = status;
    }

    public String getMessage() {
        return Message;

    }

    public void setMessage(String message) {
        this.Message = message;
    }

    public void setData(ArrayList<getSlotAvailabilityData> data) {
        this.getSlotAvailabilityDataList = data;
    }

    public List<getSlotAvailabilityData> getData() {
        return getSlotAvailabilityDataList;
    }
}


