package com.vs.schoolmessenger.model;

import java.util.ArrayList;

/**
 * Created by voicesnap on 11/28/2017.
 */

public class TeacherAbsenteesDates
{
    private String date, day, count,absentdateonly;
    private ArrayList<TeacherABS_Standard> standards;

    public TeacherAbsenteesDates(String date, String day, String count,String absentdateonly) {
        this.date = date;
        this.day = day;
        this.count = count;
        this.absentdateonly=absentdateonly;
    }

    public TeacherAbsenteesDates() {}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAbsentdateonly() {
        return absentdateonly;
    }

    public void setAbsentdateonly(String absentdateonly) {
        this.absentdateonly = absentdateonly;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public ArrayList<TeacherABS_Standard> getStandards() {
        return standards;
    }

    public void setStandards(ArrayList<TeacherABS_Standard> standards) {
        this.standards = standards;
    }
}
