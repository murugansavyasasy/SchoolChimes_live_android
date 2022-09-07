package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class OnlineStepsModel implements Serializable {
    String ID;
    String Step;

    public OnlineStepsModel(String id, String step) {
        this.ID = id;
        this.Step = step;
    }

    public String getID() {
        return ID;
    }

    public void setID(String strStandardName) {
        this.ID = strStandardName;
    }

    public String getStep() {
        return Step;
    }

    public void setStep(String strStandardCode) {
        this.Step = strStandardCode;
    }
}
