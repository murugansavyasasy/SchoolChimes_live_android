package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class KnowledgeEnhancementModel implements Serializable {

    int QuizId;
    int detailId;
    int Level;
    String Title;
    String Description;
    String Subject;
    String SubmittedOn;
    String Issubmitted;
    String isAppRead;
    String SentBy;
    String TotalNumberOfQuestions;
    String RightAnswer;
    String WrongAnswer;
    String totalMark;
    String NoOfLevels;
    String createdOn;

    public KnowledgeEnhancementModel(int quizId, String title, String description, String subject,
                                     String submittedOn, String issubmitted, String isAppRead, String sentBy, String totalNumberOfQuestions, String rightAnswer,
                                     String wrongAnswer, String totalMark,int Level,String NoOfLevels,String createdOn,int detailId){

        this.QuizId=quizId;
        this.Title=title;
        this.Description=description;
        this.Subject=subject;
        this.SubmittedOn=submittedOn;
        this.Issubmitted=issubmitted;
        this.isAppRead=isAppRead;
        this.SentBy=sentBy;
        this.TotalNumberOfQuestions=totalNumberOfQuestions;
        this.RightAnswer=rightAnswer;
        this.WrongAnswer=wrongAnswer;
        this.totalMark=totalMark;
        this.Level=Level;
        this.NoOfLevels=NoOfLevels;
        this.createdOn=createdOn;
        this.detailId=detailId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getNoOfLevels() {
        return NoOfLevels;
    }

    public void setNoOfLevels(String noOfLevels) {
        NoOfLevels = noOfLevels;
    }

    public int getQuizId()
    {
        return QuizId;
    }

    public void setQuizId(int quizId) {
        QuizId = quizId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getSubmittedOn() {
        return SubmittedOn;
    }

    public void setSubmittedOn(String submittedOn) {
        SubmittedOn = submittedOn;
    }

    public String getIssubmitted() {
        return Issubmitted;
    }

    public void setIssubmitted(String issubmitted) {
        Issubmitted = issubmitted;
    }

    public String getIsAppRead() {
        return isAppRead;
    }

    public void setIsAppRead(String isAppRead) {
        this.isAppRead = isAppRead;
    }

    public String getSentBy() {
        return SentBy;
    }

    public void setSentBy(String sentBy) {
        SentBy = sentBy;
    }

    public String getTotalNumberOfQuestions() {
        return TotalNumberOfQuestions;
    }

    public void setTotalNumberOfQuestions(String totalNumberOfQuestions) {
        TotalNumberOfQuestions = totalNumberOfQuestions;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        RightAnswer = rightAnswer;
    }

    public String getWrongAnswer() {
        return WrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        WrongAnswer = wrongAnswer;
    }

    public String getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(String totalMark) {
        this.totalMark = totalMark;
    }

    public int getLevel() {
        return Level;
    }

    public void setLevel(int level) {
        Level = level;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }
}
