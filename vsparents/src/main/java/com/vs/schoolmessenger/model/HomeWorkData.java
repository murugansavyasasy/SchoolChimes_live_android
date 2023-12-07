package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeWorkData {
    @SerializedName("date")
    private String date;

    @SerializedName("hw")
    private List<HomeWorkDateWise> hw;

    public void setDate(String status){
        this.date = status;
    }

    public String getDate(){
        return date;
    }

    public void setHw(List<HomeWorkDateWise> data){
        this.hw = data;
    }

    public List<HomeWorkDateWise> getHw(){
        return hw;
    }
}
