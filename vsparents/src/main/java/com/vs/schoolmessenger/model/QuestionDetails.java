package com.vs.schoolmessenger.model;

/**
 * Created by voicesnap on 5/10/2018.
 */

public class QuestionDetails {

    private String QustnID, Question;
    public QuestionDetails(String Id, String Qustn) {
        this.QustnID = Id;
        this.Question = Qustn;
    }

    public QuestionDetails() {}

    public String getID() {
        return QustnID;
    }

    public void setID(String id) {
        this.QustnID = id;
    }


    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        this.Question = question;
    }
}
