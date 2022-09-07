package com.vs.schoolmessenger.model;

public class lsrwModelClass {


    String SkillId;
    String Title;
    String Description;
    String Subject;
    String SubmittedOn;
    String Issubmitted;
    String isAppRead;
    String SentBy;
    String detailId;
    String ActivityType;

    public lsrwModelClass(String skillId, String title, String description, String subject, String submittedOn, String issubmitted,String isAppRead,String sentBy,String ActivityType,String detailId) {
        this.SkillId = skillId;
        this.Title = title;
        this.Description = description;
        this.Subject = subject;
        this.SubmittedOn = submittedOn;
        this.Issubmitted = issubmitted;
        this.isAppRead = isAppRead;
        this.SentBy = sentBy;
        this.ActivityType = ActivityType;
        this.detailId = detailId;

    }

    public String getActivityType() {
        return ActivityType;
    }

    public void setActivityType(String activityType) {
        ActivityType = activityType;
    }

    public String getSkillId() {
        return SkillId;
    }

    public void setSkillId(String skillId) {
        SkillId = skillId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }

    public String getSubmittedOn() {
        return SubmittedOn;
    }

    public void setSubmittedOn(String submittedOn) {
        SubmittedOn = submittedOn;
    }

    public String getIssubmitted() {
        return Issubmitted;
    }

    public void setIssubmitted(String issubmitted) {
        Issubmitted = issubmitted;
    }

    public String getIsAppRead() {
        return isAppRead;
    }

    public void setIsAppRead(String isAppRead) {
        this.isAppRead = isAppRead;
    }

    public String getSentBy() {
        return SentBy;
    }

    public void setSentBy(String sentBy) {
        SentBy = sentBy;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }
}
