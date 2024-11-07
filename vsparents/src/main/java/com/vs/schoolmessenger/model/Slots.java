package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Slots {

    private int Status;
    private String Message;
    private List<SlotsData> data;

    // Getters and setters
    public int getStatus() { return Status; }
    public void setStatus(int Status) { this.Status = Status; }
    public String getMessage() { return Message; }
    public void setMessage(String Message) { this.Message = Message; }
    public List<SlotsData> getData() { return data; }
    public void setData(List<SlotsData> data) { this.data = data; }



}
