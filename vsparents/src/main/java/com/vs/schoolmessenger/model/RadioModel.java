package com.vs.schoolmessenger.model;

public class RadioModel  {

    private String QustnID, Question1,Question2,Question3;

    public RadioModel(String Id, String Qustn,String qust2,String qstn3) {
        this.QustnID = Id;
        this.Question1 = Qustn;
        this.Question2 = qust2;
        this.Question3 = qstn3;

    }

    public RadioModel() {}

    public String getID() {
        return QustnID;
    }

    public void setID(String id) {
        this.QustnID = id;
    }


    public String getQuestion1() {
        return Question1;
    }

    public void setQuestion1(String question) {
        this.Question1 = question;
    }

    public String getQuestion2() {
        return Question2;
    }

    public void setQuestion2(String question) {
        this.Question2 = question;
    }
    public String getQuestion3() {
        return Question3;
    }

    public void setQuestion3(String question) {
        this.Question3 = question;
    }
}
