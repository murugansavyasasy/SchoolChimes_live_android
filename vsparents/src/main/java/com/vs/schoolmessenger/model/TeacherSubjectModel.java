package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 1/12/2018.
 */

public class TeacherSubjectModel implements Serializable
{
    String strSubName, strSubCode;
    boolean SelectedStatus;

    public TeacherSubjectModel() {
    }

    public TeacherSubjectModel(String strSubName, String strSubCode,boolean selectedStatus) {
        this.strSubName = strSubName;
        this.strSubCode = strSubCode;
        this.SelectedStatus = selectedStatus;
    }

    public String getStrSubName() {
        return strSubName;
    }

    public void setStrSubName(String strSubName) {
        this.strSubName = strSubName;
    }

    public String getStrSubCode() {
        return strSubCode;
    }

    public void setStrSubCode(String strSubCode) {
        this.strSubCode = strSubCode;
    }


    public boolean getSelectedStatus() {
        return SelectedStatus;
    }

    public void setSelectedStatus(boolean selectedStatus) {
        this.SelectedStatus = selectedStatus;
    }
}
