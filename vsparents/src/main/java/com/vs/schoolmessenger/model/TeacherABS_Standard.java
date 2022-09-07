package com.vs.schoolmessenger.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by voicesnap on 11/28/2017.
 */

public class TeacherABS_Standard implements Serializable
{
    private String sid, standard, count;
    private ArrayList<TeacherABS_Section> sections;

    public TeacherABS_Standard() {
    }

    public TeacherABS_Standard(String standard, String count){//, ArrayList<TeacherABS_Section> sections) {

        this.standard = standard;
        this.count = count;
//        this.sections = sections;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public ArrayList<TeacherABS_Section> getSections() {
        return sections;
    }

    public void setSections(ArrayList<TeacherABS_Section> sections) {
        this.sections = sections;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
