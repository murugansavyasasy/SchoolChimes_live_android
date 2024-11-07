package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class StdSecDetailsModel {
    private String class_id;
    private String section_id;
    private String class_name;
    private String section_name;

    // Getters and setters
    public String getClass_id() { return class_id; }
    public void setClass_id(String class_id) { this.class_id = class_id; }
    public String getSection_id() { return section_id; }
    public void setSection_id(String section_id) { this.section_id = section_id; }
    public String getClass_name() { return class_name; }
    public void setClass_name(String class_name) { this.class_name = class_name; }
    public String getSection_name() { return section_name; }
    public void setSection_name(String section_name) { this.section_name = section_name; }
}

