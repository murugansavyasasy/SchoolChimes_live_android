package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class HomeWorkFilePaths {

    @SerializedName("path")
    private String path;

    @SerializedName("type")
    private String type;

    public void setPath(String status){
        this.path = status;
    }

    public String getPath(){
        return path;
    }

    public void setType(String status){
        this.type = status;
    }

    public String getType(){
        return type;
    }

}
