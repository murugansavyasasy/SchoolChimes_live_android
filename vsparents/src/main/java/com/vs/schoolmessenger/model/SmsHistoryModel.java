package com.vs.schoolmessenger.model;

public class SmsHistoryModel {

    private String ID, description,content;
    private Boolean SeletedStatus;

    public SmsHistoryModel(String Id, String des,String content,Boolean SelectedStatus) {
        this.ID = Id;
        this.description = des;
        this.content = content;
        this.SeletedStatus = SelectedStatus;


        }

    public SmsHistoryModel() {}

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String des) {
        this.description = des;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String con) {
        this.content = con;
    }


    public boolean getSelectedStatus() {
        return SeletedStatus;
    }

    public void setSeletedStatus(boolean con) {
        this.SeletedStatus = con;
    }

}
