package com.vs.schoolmessenger.model;
import com.google.gson.annotations.SerializedName;

public class Subgroup {
    @SerializedName("mark")
    private String mMark;
    @SerializedName("name")
    private String mName;
    public Subgroup(String mMark, String mName) {
        this.mMark = mMark;
        this.mName = mName;
    }
    public String getMark() {
        return mMark;
    }
    public void setMark(String mark) {
        mMark = mark;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName = name;
    }
}