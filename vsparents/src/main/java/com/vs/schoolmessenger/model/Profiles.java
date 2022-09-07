package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 8/31/2016.
 */

@SuppressWarnings("serial")
public class Profiles implements Serializable {
    private String childName, childID, rollNo, standard, section, schoolID, schoolName, schoolAddress,
            schoolThumbnailImgUrl, msgCount,BookEnable,BookLink,IsNotAllow,DisplayMessage;

    public Profiles() {
    }

    public Profiles(String childID) {
        this.childID = childID;
    }

    public Profiles(String name, String childID, String rollNo, String standard, String section, String schoolID, String schoolName, String schoolAddress, String schoolThumbnailImgUrl,
                    String bookEnable,String bookLink,String isnotallow,String displaymessage)//, String msgCount
    {
        this.childName = name;
        this.childID = childID;
        this.rollNo = rollNo;
        this.standard = standard;
        this.section = section;
        this.schoolID = schoolID;
        this.schoolName = schoolName;
        this.schoolAddress = schoolAddress;
        this.schoolThumbnailImgUrl = schoolThumbnailImgUrl;

        this.BookEnable=bookEnable;
        this.BookLink=bookLink;
        this.IsNotAllow=isnotallow;
        this.DisplayMessage=displaymessage;
//        this.msgCount = msgCount;
    }


    public String getIsNotAllow() {
        return IsNotAllow;
    }

    public void setIsNotAllow(String notallow) {
        this.IsNotAllow = notallow;
    }

    public String getDisplayMessage() {
        return DisplayMessage;
    }

    public void setDisplayMessage(String message) {
        this.DisplayMessage = message;
    }



    public String getBookEnable() {
        return BookEnable;
    }

    public void setBookEnable(String childName) {
        this.BookEnable = childName;
    }



    public String getBookLink() {
        return BookLink;
    }

    public void setBookLink(String childName) {
        this.BookLink = childName;
    }



    public String getChildName() {
        return childName;
    }

    public void setChildName(String childName) {
        this.childName = childName;
    }

    public String getChildID() {
        return childID;
    }

    public void setChildID(String childID) {
        this.childID = childID;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(String schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSchoolAddress() {
        return schoolAddress;
    }

    public void setSchoolAddress(String schoolAddress) {
        this.schoolAddress = schoolAddress;
    }

    public String getMsgCount() {
        return msgCount;
    }

    public void setMsgCount(String msgCount) {
        this.msgCount = msgCount;
    }

    public String getSchoolThumbnailImgUrl() {
        return schoolThumbnailImgUrl;
    }

    public void setSchoolThumbnailImgUrl(String schoolThumbnailImgUrl) {
        this.schoolThumbnailImgUrl = schoolThumbnailImgUrl;
    }

}
