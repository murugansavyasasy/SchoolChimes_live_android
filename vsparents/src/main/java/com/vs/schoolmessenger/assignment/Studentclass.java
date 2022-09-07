package com.vs.schoolmessenger.assignment;

class Studentclass {
    String studentid, Studentname, Standard, Section, Message;
    Boolean is_Archive;

    public Studentclass(String studentid, String studentname, String standard, String section, String message,Boolean archve) {
        this.studentid = studentid;
        Studentname = studentname;
        Standard = standard;
        Section = section;
        Message = message;
        is_Archive = archve;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getStudentname() {
        return Studentname;
    }

    public void setStudentname(String studentname) {
        Studentname = studentname;
    }

    public String getStandard() {
        return Standard;
    }

    public void setStandard(String standard) {
        Standard = standard;
    }

    public String getSection() {
        return Section;
    }

    public void setSection(String section) {
        Section = section;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }


    public Boolean getIs_Archive() {
        return is_Archive;
    }

    public void setIs_Archive(Boolean message) {
        is_Archive = message;
    }
}
