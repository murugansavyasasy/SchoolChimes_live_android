package com.vs.schoolmessenger.model;

import java.util.List;

public class StudentAttendanceReport {

    private int status;

    private String message;

    private List<AttendanceReport> data;


    public void setStatus(int id) {
        this.status = id;
    }

    public int getStatus() {
        return status;
    }

    public void setMessage(String id) {
        this.message = id;
    }

    public String getMessage() {
        return message;
    }


    public void setData(List<AttendanceReport> data) {
        this.data = data;
    }

    public List<AttendanceReport> getData() {
        return data;
    }

    public class AttendanceReport {

        private String student_name;

        private String admission_no;

        private String att_status;
        private String absent_date;

        public String getStudent_name() {
            return student_name;
        }

        public void setStudent_name(String student_name) {
            this.student_name = student_name;
        }

        public String getAdmission_no() {
            return admission_no;
        }

        public void setAdmission_no(String admission_no) {
            this.admission_no = admission_no;
        }

        public String getAtt_status() {
            return att_status;
        }

        public void setAtt_status(String att_status) {
            this.att_status = att_status;
        }

        public String getAbsent_date() {
            return absent_date;
        }

        public void setAbsent_date(String absent_date) {
            this.absent_date = absent_date;
        }
    }
}
