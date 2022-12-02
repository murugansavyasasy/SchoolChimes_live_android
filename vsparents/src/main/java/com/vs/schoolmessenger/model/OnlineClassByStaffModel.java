package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class OnlineClassByStaffModel implements Serializable {
    String header_id;
    String subheader_id;
    String topic;
    String description;
    String url;
    String meetingtype;
    String meetingdatetime;
    String subject_name;
    String target_type;
    String created_on;
    int can_cancel;
    public OnlineClassByStaffModel() {
    }

    public OnlineClassByStaffModel(String hid, String sid, String top, String desc, String url,String meetType,
                            String meetDateTime,String subject,String type,String createdon,int cancel) {
        this.header_id = hid;
        this.subheader_id = sid;
        this.topic = top;
        this.description = desc;
        this.url = url;
        this.meetingtype = meetType;
        this.meetingdatetime = meetDateTime;
        this.subject_name = subject;
        this.target_type = type;
        this.created_on = createdon;
        this.can_cancel = cancel;
    }

    public int getCan_cancel() {
        return can_cancel;
    }

    public void setCan_cancel(int msgID) {
        this.can_cancel = msgID;
    }

    public String getHeader_id() {
        return header_id;
    }

    public void setHeader_id(String msgID) {
        this.header_id = msgID;
    }

    public String getSubheader_id() {
        return subheader_id;
    }

    public void setSubheader_id(String msgID) {
        this.subheader_id = msgID;
    }
    public String getTopic() {
        return topic;
    }

    public void setTopic(String msgID) {
        this.topic = msgID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String msgID) {
        this.description = msgID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String msgID) {
        this.url = msgID;
    }

    public String getMeetingtype() {
        return meetingtype;
    }

    public void setMeetingtype(String msgID) {
        this.meetingtype = msgID;
    }


    public String getMeetingdatetime() {
        return meetingdatetime;
    }

    public void setMeetingdatetime(String msgID) {
        this.meetingdatetime = msgID;
    }
    public String getTarget_type() {
        return target_type;
    }

    public void setTarget_type(String msgID) {
        this.target_type = msgID;
    }

    public String getSubject_name() {
        return subject_name;
    }

    public void setSubject_name(String msgID) {
        this.subject_name = msgID;
    }
    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String msgID) {
        this.created_on = msgID;
    }

}