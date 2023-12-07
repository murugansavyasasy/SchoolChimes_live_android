package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;
import com.vs.schoolmessenger.LessonPlan.Model.DataArrayItem;

import java.util.ArrayList;
import java.util.List;

public class HomeWorkModel {

    @SerializedName("Status")
    private String Status;

    @SerializedName("Message")
    private String Message;

    @SerializedName("data")
    private List<HomeWorkData> data;

    @SerializedName("is_Archive")
    private boolean is_Archive;



    public void setStatus(String status){
        this.Status = status;
    }

    public String getStatus(){
        return Status;
    }

    public void setMessage(String message){
        this.Message = message;
    }

    public String getMessage(){
        return Message;
    }

    public void setData(List<HomeWorkData> data){
        this.data = data;
    }

    public List<HomeWorkData> getData(){
        return data;
    }

    public void setIs_Archive(boolean message){
        this.is_Archive = message;
    }

    public boolean getIs_Archive(){
        return is_Archive;
    }
}
