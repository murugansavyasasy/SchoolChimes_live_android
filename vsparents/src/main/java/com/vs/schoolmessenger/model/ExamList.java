package com.vs.schoolmessenger.model;

public class ExamList {
    private String Id, ExamName;

    public ExamList(String Id, String Name) {
        this.Id = Id;
        this.ExamName = Name;

    }

    public ExamList() {}

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return ExamName;
    }

    public void setName(String name) {
        this.ExamName = name;
    }


}
