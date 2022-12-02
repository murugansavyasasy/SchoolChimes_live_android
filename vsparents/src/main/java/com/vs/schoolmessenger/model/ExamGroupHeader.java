package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class ExamGroupHeader implements Serializable {
    String Id, ExamName,Syllabus;
    public ExamGroupHeader(String Id, String Name,String syllabus) {
        this.Id = Id;
        this.ExamName = Name;
        this.Syllabus = syllabus;
    }

    public ExamGroupHeader() {}

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getExamName() {
        return ExamName;
    }

    public void setExamName(String name) {
        this.ExamName = name;
    }

    public String getSyllabus() {
        return Syllabus;
    }

    public void setSyllabus(String syllabus) {
        this.Syllabus = syllabus;
    }

}

