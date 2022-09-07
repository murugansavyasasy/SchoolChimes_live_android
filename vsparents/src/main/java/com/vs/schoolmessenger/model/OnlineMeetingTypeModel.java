package com.vs.schoolmessenger.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OnlineMeetingTypeModel implements Serializable {
    String ID;
    String Type;
    ArrayList<OnlineStepsModel> listSteps;

    public OnlineMeetingTypeModel(String id, String type, ArrayList<OnlineStepsModel> listStep) {
        this.ID = id;
        this.Type = type;
        this.listSteps = listStep;
    }

    public String getID() {
        return ID;
    }

    public void setID(String strStandardName) {
        this.ID = strStandardName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String strStandardCode) {
        this.Type = strStandardCode;
    }

    public ArrayList<OnlineStepsModel> getListSteps() {
        return listSteps;
    }

    public void setListSteps(ArrayList<OnlineStepsModel> listSubjects) {
        this.listSteps = listSubjects;
    }
}
