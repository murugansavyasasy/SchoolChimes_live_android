package com.vs.schoolmessenger.model;

public class SectionAndClass {
    int class_id;
    int section_id;



    public SectionAndClass(int day, int id) {
        this.class_id = day;
        this.section_id = id;

    }

    public int getClass_id() {
        return class_id;
    }

    public void setClass_id(int id) {
        this.class_id = id;
    }

    public int getSection_id() {
        return section_id;
    }

    public void setSection_id(int day) {
        this.section_id = day;
    }
}
