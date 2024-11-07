package com.vs.schoolmessenger.model;

public class StaffMsgMangeCount {


    String SCHOOLID;
    String OVERALLCOUNT;

    public String getSCHOOLID() {
        return SCHOOLID;
    }

    public void setSCHOOLID(String SCHOOLID) {
        this.SCHOOLID = SCHOOLID;
    }

    public String getOVERALLCOUNT() {
        return OVERALLCOUNT;
    }

    public void setOVERALLCOUNT(String OVERALLCOUNT) {
        this.OVERALLCOUNT = OVERALLCOUNT;
    }


    public StaffMsgMangeCount(String SCHOOLID, String OVERALLCOUNT) {
        this.SCHOOLID = SCHOOLID;
        this.OVERALLCOUNT = OVERALLCOUNT;
    }
}
