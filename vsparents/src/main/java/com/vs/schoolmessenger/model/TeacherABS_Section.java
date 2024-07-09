package com.vs.schoolmessenger.model;

import java.io.Serializable;

/**
 * Created by voicesnap on 11/28/2017.
 */

public class TeacherABS_Section implements Serializable {
    private String section, count,sectionId;

    public TeacherABS_Section() {
    }

    public TeacherABS_Section(String section, String count,String sectionId) {
        this.section = section;
        this.count = count;
        this.sectionId=sectionId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }
    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}
