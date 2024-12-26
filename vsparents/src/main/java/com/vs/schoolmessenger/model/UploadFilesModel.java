package com.vs.schoolmessenger.model;

public class UploadFilesModel {
    private String wsUploadedDoc, displayname,isFileName;

    public UploadFilesModel(String link, String filename,String isFileName) {
        this.wsUploadedDoc = link;
        this.displayname = filename;
        this.isFileName=isFileName;

    }

    public String getWsUploadedDoc() {
        return wsUploadedDoc;
    }

    public void setWsUploadedDoc(String doc) {
        this.wsUploadedDoc = doc;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String name) {
        this.displayname = name;
    }


    public String getIsFileName() {
        return isFileName;
    }

    public void setIsFileName(String FileName) {
        this.displayname = FileName;
    }




}
