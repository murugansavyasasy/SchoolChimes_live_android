package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SlotDetailsForStaff {

    private int Status;
    private String Message;
    private List<DateModel> data;

    // Getters and setters
    public int getStatus() { return Status; }
    public void setStatus(int Status) { this.Status = Status; }
    public String getMessage() { return Message; }
    public void setMessage(String Message) { this.Message = Message; }
    public List<DateModel> getData() { return data; }
    public void setData(List<DateModel> data) { this.data = data; }
}
