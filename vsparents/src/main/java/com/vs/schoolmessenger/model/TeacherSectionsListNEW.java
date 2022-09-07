package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 6/10/2017.
 */

public class TeacherSectionsListNEW implements Serializable {
    String strSectionName, StrSectionCode;
    String strTotalStudents;
    boolean selectStatus;

    public TeacherSectionsListNEW(String strSectionName, String strSectionCode, String strTotalStudents, boolean selectStatus) {
        this.strSectionName = strSectionName;
        StrSectionCode = strSectionCode;
        this.strTotalStudents = strTotalStudents;
        this.selectStatus = selectStatus;
    }

    public TeacherSectionsListNEW() {
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

    public boolean isSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
    }
}
