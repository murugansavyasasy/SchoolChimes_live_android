package com.vs.schoolmessenger.model;

/**
 * Created by voicesnap on 6/10/2017.
 */

public class TeacherSectionsListModel {
    String strSectionName, StrSectionCode;
    String strTotalStudents;

    public TeacherSectionsListModel(String strSectionName, String strSectionCode) {
        this.strSectionName = strSectionName;
        StrSectionCode = strSectionCode;
    }

    public TeacherSectionsListModel() {
    }

    public String getStrSectionName() {
        return strSectionName;
    }

    public void setStrSectionName(String strSectionName) {
        this.strSectionName = strSectionName;
    }

    public String getStrSectionCode() {
        return StrSectionCode;
    }

    public void setStrSectionCode(String strSectionCode) {
        StrSectionCode = strSectionCode;
    }

    public String getStrTotalStudents() {
        return strTotalStudents;
    }

    public void setStrTotalStudents(String strTotalStudents) {
        this.strTotalStudents = strTotalStudents;
    }
}
