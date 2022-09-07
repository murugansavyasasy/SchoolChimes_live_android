package com.vs.schoolmessenger.model;
import java.util.List;
import com.google.gson.annotations.SerializedName;
public class Group {
    @SerializedName("name")
    private String mGroupname;
    @SerializedName("mark")
    private String mGroupmark;
    @SerializedName("subgroups")
    private List<Subgroup> mSubgroups;
    public Group(String mGroupname, String mGroupmark, List<Subgroup> mSubgroups) {
        this.mGroupname = mGroupname;
        this.mGroupmark = mGroupmark;
        this.mSubgroups = mSubgroups;
    }

    public String getmGroupname() {
        return mGroupname;
    }
    public void setmGroupname(String mGroupname) {
        this.mGroupname = mGroupname;
    }
    public String getmGroupmark() {
        return mGroupmark;
    }
    public void setmGroupmark(String mGroupmark) {
        this.mGroupmark = mGroupmark;
    }
    public List<Subgroup> getSubgroups() {
        return mSubgroups;
    }
    public void setSubgroups(List<Subgroup> subgroups) {
        mSubgroups = subgroups;
    }
}