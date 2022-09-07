package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by devi on 5/3/2017.
 */

public class TeacherMessageModel implements Serializable {

    String msgID;
    String msgTitle;
    String msgContent;
    String msgReadStatus;
    String msgDate;
    String msgTime;
    String msgdescription;
    String strQueryAvailable, strQuestion;

    public TeacherMessageModel() {
    }

    public TeacherMessageModel(String msgID, String msgTitle, String msgContent, String msgReadStatus, String msgDate, String msgTime, String msgdescription) {
        this.msgID = msgID;
        this.msgTitle = msgTitle;
        this.msgContent = msgContent;
        this.msgReadStatus = msgReadStatus;
        this.msgDate = msgDate;
        this.msgTime = msgTime;
        this.msgdescription = msgdescription;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getMsgReadStatus() {
        return msgReadStatus;
    }

    public void setMsgReadStatus(String msgReadStatus) {
        this.msgReadStatus = msgReadStatus;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getStrQueryAvailable() {
        return strQueryAvailable;
    }

    public void setStrQueryAvailable(String strQueryAvailable) {
        this.strQueryAvailable = strQueryAvailable;
    }

    public String getStrQuestion() {
        return strQuestion;
    }

    public void setStrQuestion(String strQuestion) {
        this.strQuestion = strQuestion;
    }

    public String getMsgdescription() {
        return msgdescription;
    }

    public void setMsgdescription(String msgdescription) {
        this.msgdescription = msgdescription;
    }
}

