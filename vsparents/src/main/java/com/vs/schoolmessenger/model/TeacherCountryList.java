package com.vs.schoolmessenger.model;

/**
 * Created by voicesnap on 5/12/2017.
 */

public class TeacherCountryList {
    String strCountyID, strCountyName, strCountyMobileLength, strCountyCode, strBaseURL,strMobileHint;

    public TeacherCountryList() {}

    public TeacherCountryList(String strCountyID, String strCountyName, String strCountyMobileLength, String strCountyCode, String strBaseURL,String mobileHint) {
        this.strCountyID = strCountyID;
        this.strCountyName = strCountyName;
        this.strCountyMobileLength = strCountyMobileLength;
        this.strCountyCode = strCountyCode;
        this.strBaseURL = strBaseURL;
        this.strMobileHint = mobileHint;

    }

    public String getStrCountyID() {
        return strCountyID;
    }

    public void setStrCountyID(String strCountyID) {
        this.strCountyID = strCountyID;
    }

    public String getStrCountyName() {
        return strCountyName;
    }

    public void setStrCountyName(String strCountyName) {
        this.strCountyName = strCountyName;
    }

    public String getStrCountyMobileLength() {
        return strCountyMobileLength;
    }

    public void setStrCountyMobileLength(String strCountyMobileLength) {
        this.strCountyMobileLength = strCountyMobileLength;
    }

    public String getStrCountyCode() {
        return strCountyCode;
    }

    public void setStrCountyCode(String strCountyCode) {
        this.strCountyCode = strCountyCode;
    }

    public String getStrBaseURL() {
        return strBaseURL;
    }

    public void setStrBaseURL(String strBaseURL) {
        this.strBaseURL = strBaseURL;
    }

    public String getStrMobileHint() {
        return strMobileHint;
    }

    public void setStrMobileHint(String strBaseURL) {
        this.strMobileHint = strBaseURL;
    }



}
