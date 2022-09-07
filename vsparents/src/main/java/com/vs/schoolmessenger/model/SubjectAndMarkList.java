package com.vs.schoolmessenger.model;

public class SubjectAndMarkList {
    private String subject, mark;

    public SubjectAndMarkList(String sub, String mark) {
        this.subject = sub;
        this.mark = mark;

    }

    public SubjectAndMarkList() {}

    public String getSubject() {
        return subject;
    }

    public void setSubject(String sub) {
        this.subject = sub;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }


}