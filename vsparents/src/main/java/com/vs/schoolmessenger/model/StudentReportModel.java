package com.vs.schoolmessenger.model;

public class StudentReportModel {
    private String  studentName,primaryMobile,admissionNo,gender,dob,className,sectionName,fatherName,classTeacher;
    private int studentId,classId,sectionId;

    public StudentReportModel(int Id, String studentName,String primaryMobile,String admissionNo, String gender,
                              String dob,int classId,String className,int sectionId,String sectionName,String isFatherName,String isClassTeacher) {
        this.studentId = Id;
        this.classId = classId;
        this.sectionId = sectionId;
        this.studentName = studentName;
        this.primaryMobile = primaryMobile;
        this.admissionNo = admissionNo;
        this.gender = gender;
        this.dob = dob;
        this.className = className;
        this.sectionName = sectionName;
        this.fatherName=isFatherName;
        this.classTeacher=isClassTeacher;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int Id) {
        this.studentId = Id;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int Id) {
        this.classId = Id;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int Id) {
        this.sectionId = Id;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String name) {
        this.studentName = name;
    }

    public String getPrimaryMobile() {
        return primaryMobile;
    }

    public void setPrimaryMobile(String name) {
        this.primaryMobile = name;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String name) {
        this.admissionNo = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String name) {
        this.gender = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String name) {
        this.dob = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String name) {
        this.className = name;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String name) {
        this.sectionName = name;
    }


    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String name) {
        this.fatherName = name;
    }

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String name) {
        this.classTeacher = name;
    }



}
