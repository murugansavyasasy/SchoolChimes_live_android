package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class DailyCollectionData {
//    @SerializedName("id")
    private int id;

//    @SerializedName("yearName")
    private String yearName;

//    @SerializedName("currentAcademicYear")
    private int currentAcademicYear;


    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setYearName(String yearName) {
        this.yearName = yearName;
    }

    public String getYearName() {
        return yearName;
    }

    public void setCurrentAcademicYear(int currentAcademicYear) {
        this.currentAcademicYear = currentAcademicYear;
    }

    public int getCurrentAcademicYear() {
        return currentAcademicYear;
    }

    public  DailyCollectionData(int isIds,String YearName,int Yearid){
        this.id=isIds;
        this.yearName=YearName;
        this.currentAcademicYear=Yearid;
    }

}
