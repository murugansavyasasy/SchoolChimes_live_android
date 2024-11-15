package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by devi on 5/19/2017.
 */

public class TeacherStudentsModel implements Serializable {
    private String studentID, studentName, admisionNo,rollNo;
    boolean selectStatus;

    public TeacherStudentsModel(){}

    public TeacherStudentsModel(String studentID, String studentName, String admisionNo,String rollNo, boolean selectStatus) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.admisionNo = admisionNo;
        this.rollNo = rollNo;
        this.selectStatus = selectStatus;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmisionNo() {
        return admisionNo;
    }

    public void setAdmisionNo(String admisionNo) {
        this.admisionNo = admisionNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String admisionNo) {
        this.rollNo = admisionNo;
    }

    public boolean isSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
    }
}
