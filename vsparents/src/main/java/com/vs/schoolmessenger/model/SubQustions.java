package com.vs.schoolmessenger.model;

/**
 * Created by voicesnap on 5/10/2018.
 */

public class SubQustions {
    private String QustnID, Question,MainQuestion,ManQuestionId;
    public SubQustions(String subId, String subqs,String mainid,String mainqstn ) {
        this.QustnID = subId;
        this.Question = subqs;
        this.MainQuestion = mainqstn;
        this.ManQuestionId = mainid;
    }

    public SubQustions() {}

    public String getId() {
        return QustnID;
    }

    public void setId(String id) {
        this.QustnID = id;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String qs) {
        this.Question = qs;
    }

    public String getMainQuestionID() {
        return ManQuestionId;
    }

    public void setMainQuestinID(String qs) {
        this.ManQuestionId = qs;
    }

    public String getMainQuestion() {
        return MainQuestion;
    }

    public void setMainQuestion(String qs) {
        this.MainQuestion = qs;
    }
}
