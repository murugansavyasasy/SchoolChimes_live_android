package com.vs.schoolmessenger.model;

public class monthsModel {
    int ID;
    String MonthName;

    public monthsModel(int id, String adname) {
        this.ID = id;
        this.MonthName = adname;
    }
    public int getID() {
        return ID;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public String getMonthName() {
        return MonthName;
    }

    public void setMonthName(String id) {
        this.MonthName = id;
    }
}

