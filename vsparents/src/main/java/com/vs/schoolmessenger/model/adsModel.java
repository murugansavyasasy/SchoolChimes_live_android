package com.vs.schoolmessenger.model;

public class adsModel {
    int ID;
    String adName;
    String adImage;
    String redirectURL;



    public adsModel(int id, String adname,String adImage,String redirectUrl) {
        this.ID = id;
        this.adName = adname;
        this.adImage = adImage;
        this.redirectURL = redirectUrl;

    }

    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public String getAdImage() {
        return adImage;
    }

    public void setAdImage(String day) {
        this.adImage = day;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String name) {
        this.adName = name;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String url) {
        this.redirectURL = url;
    }
}