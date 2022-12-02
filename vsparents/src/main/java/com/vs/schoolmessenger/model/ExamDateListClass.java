package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class ExamDateListClass implements Serializable {
     String Syllabus,SubName,ExamDate,ExamSession,Maxmark,SubjectSyllabus;
    public ExamDateListClass(String syllabus,String subname,String examdate,String examSession,String maxmark,String subsyllabus) {

        this.Syllabus = syllabus;
        this.SubName = subname;
        this.ExamDate = examdate;
        this.ExamSession = examSession;
        this.Maxmark = maxmark;
        this.SubjectSyllabus = subsyllabus;

    }

    public ExamDateListClass() {}

    public String getSyllabus() {
        return Syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.Syllabus = syllabus;
    }

    public String getSubjectSyllabus() {
        return SubjectSyllabus;
    }

    public void setSubjectSyllabus(String syllabus) {
        this.SubjectSyllabus = syllabus;
    }

    public String getSubName() {
        return SubName;
    }

    public void setSubName(String subName) {
        this.SubName = subName;
    }


    public String getExamDate() {
        return ExamDate;
    }

    public void setExamDate(String examDate) {
        this.ExamDate = examDate;
    }


    public String getExamSession() {
        return ExamSession;
    }

    public void setExamSession(String examSession) {
        this.ExamSession = examSession;
    }


    public String getMaxmark() {
        return Maxmark;
    }

    public void setMaxmark(String maxmark) {
        this.Maxmark = maxmark;
    }
}
