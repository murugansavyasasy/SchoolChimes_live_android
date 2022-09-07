package com.vs.schoolmessenger.model;

public class UploadFilesModel {
    private String wsUploadedDoc, displayname;

    public UploadFilesModel(String link, String filename) {
        this.wsUploadedDoc = link;
        this.displayname = filename;

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


}
