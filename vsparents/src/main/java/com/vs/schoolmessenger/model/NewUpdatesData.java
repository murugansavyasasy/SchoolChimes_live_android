package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class NewUpdatesData {

    @SerializedName("id")
    private int id;

    @SerializedName("update_name")
    private String update_name;

    @SerializedName("update_description")
    private String update_description;

    @SerializedName("app_redirect_link")
    private String redirect_link;

    @SerializedName("video_link")
    private String video_link;

    @SerializedName("downloadable_image")
    private String downloadable_image;

    public void setId(int status){
        this.id = status;
    }

    public int getId(){
        return id;
    }

    public void setUpdate_name(String status){
        this.update_name = status;
    }

    public String getUpdate_name(){
        return update_name;
    }

    public void setUpdate_description(String status){
        this.update_description = status;
    }

    public String getUpdate_description(){
        return update_description;
    }

    public void setRedirect_link(String status){
        this.redirect_link = status;
    }

    public String getRedirect_link(){
        return redirect_link;
    }

    public void setVideo_link(String status){
        this.video_link = status;
    }

    public String getVideo_link(){
        return video_link;
    }

    public void setDownloadable_image(String status){
        this.downloadable_image = status;
    }

    public String getDownloadable_image(){
        return downloadable_image;
    }
}
