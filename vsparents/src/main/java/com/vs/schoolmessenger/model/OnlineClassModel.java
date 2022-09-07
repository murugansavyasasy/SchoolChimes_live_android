package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class OnlineClassModel implements Serializable {



     String header_id;
     String message_id;
     String topic;
     String description;
     String url;
     String meetingid;
     String meetingtype;
     String meetingdate;
     String meetingtime;
     String meetingdatetime;
     String staff_name;
     String subject_name;
     String is_app_viewed;


     public OnlineClassModel() {
     }

     public OnlineClassModel(String hid, String msgID, String top, String desc, String url, String meetID,String meetType,String meetDate,String meetTime,
                             String meetDateTime,String staff,String subject,String appView) {
         this.header_id = hid;
         this.message_id = msgID;
         this.topic = top;
         this.description = desc;
         this.url = url;
         this.meetingid = meetID;
         this.meetingtype = meetType;
         this.meetingdate = meetDate;
         this.meetingtime = meetTime;
         this.meetingdatetime = meetDateTime;
         this.staff_name = staff;
         this.subject_name = subject;
         this.is_app_viewed = appView;
     }

     public String getHeader_id() {
         return header_id;
     }

     public void setHeader_id(String msgID) {
         this.header_id = msgID;
     }

     public String getMessage_id() {
         return message_id;
     }

     public void setMessage_id(String msgID) {
         this.message_id = msgID;
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
     public String getMeetingid() {
         return meetingid;
     }

     public void setMeetingid(String msgID) {
         this.meetingid = msgID;
     }
     public String getMeetingtype() {
         return meetingtype;
     }

     public void setMeetingtype(String msgID) {
         this.meetingtype = msgID;
     }
     public String getMeetingdate() {
         return meetingdate;
     }

     public void setMeetingdate(String msgID) {
         this.meetingdate = msgID;
     }

     public String getMeetingtime() {
         return meetingtime;
     }

     public void setMeetingtime(String msgID) {
         this.meetingtime = msgID;
     }
     public String getMeetingdatetime() {
         return meetingdatetime;
     }

     public void setMeetingdatetime(String msgID) {
         this.meetingdatetime = msgID;
     }
     public String getStaff_name() {
         return staff_name;
     }

     public void setStaff_name(String msgID) {
         this.staff_name = msgID;
     }

     public String getSubject_name() {
         return subject_name;
     }

     public void setSubject_name(String msgID) {
         this.subject_name = msgID;
     }
     public String getIs_app_viewed() {
         return is_app_viewed;
     }

     public void setIs_app_viewed(String msgID) {
         this.is_app_viewed = msgID;
     }

}
