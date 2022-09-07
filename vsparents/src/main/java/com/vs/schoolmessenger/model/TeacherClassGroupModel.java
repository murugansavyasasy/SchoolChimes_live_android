package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 6/7/2017.
 */

public class TeacherClassGroupModel implements Serializable
{
    String strName, strID;
    boolean bSelected;

    public TeacherClassGroupModel(String strName, String strID, boolean bSelected) {
        this.strName = strName;
        this.strID = strID;
        this.bSelected = bSelected;
    }

    public TeacherClassGroupModel()
    {

    }

    public String getStrName() {
        return strName;
    }

    public void setStrName(String strName) {
        this.strName = strName;
    }

    public String getStrID() {
        return strID;
    }

    public void setStrID(String strID) {
        this.strID = strID;
    }

    public boolean isbSelected() {
        return bSelected;
    }

    public void setbSelected(boolean bSelected) {
        this.bSelected = bSelected;
    }
}
