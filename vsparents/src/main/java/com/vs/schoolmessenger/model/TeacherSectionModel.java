package com.vs.schoolmessenger.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by devi on 5/19/2017.
 */

public class TeacherSectionModel implements Serializable {

    private boolean selectStatus, allStudentsSelected;
    private String standard, section, stdSecCode, subject, subjectCode, totStudents, selectedStudentsCount;
    private ArrayList<TeacherStudentsModel> studentsList;

    public TeacherSectionModel() {}

    public TeacherSectionModel(boolean selectStatus, String standard, String section, String stdSecCode, String subject, String subjectCode, String totStudents, String selectedStudentsCount, boolean allStudentsSelected)
    {
        this.selectStatus = selectStatus;
        this.standard = standard;
        this.section = section;
        this.stdSecCode = stdSecCode;
        this.subject = subject;
        this.subjectCode = subjectCode;
        this.totStudents = totStudents;
        this.selectedStudentsCount = selectedStudentsCount;
        this.allStudentsSelected = allStudentsSelected;
    }

    public boolean isSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
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

    public String getStdSecCode() {
        return stdSecCode;
    }

    public void setStdSecCode(String stdSecCode) {
        this.stdSecCode = stdSecCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getTotStudents() {
        return totStudents;
    }

    public void setTotStudents(String totStudents) {
        this.totStudents = totStudents;
    }

    public String getSelectedStudentsCount() {
        return selectedStudentsCount;
    }

    public void setSelectedStudentsCount(String selectedStudentsCount) {
        this.selectedStudentsCount = selectedStudentsCount;
    }

    public ArrayList<TeacherStudentsModel>  getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(ArrayList<TeacherStudentsModel>  studentsList) {
        this.studentsList = studentsList;
    }

    public boolean isAllStudentsSelected() {
        return allStudentsSelected;
    }

    public void setAllStudentsSelected(boolean allStudentsSelected) {
        this.allStudentsSelected = allStudentsSelected;
    }
}
