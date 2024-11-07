package com.vs.schoolmessenger.model;

import java.util.List;

public class StaffAttendanceBiometricReportRes {

    private int status;

    private String message;

    private List<BiometriStaffReportData> data;

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


    public void setData(List<BiometriStaffReportData> data) {
        this.data = data;
    }
    public List<BiometriStaffReportData> getData() {
        return data;
    }

    public class BiometriStaffReportData {
        private String staffName;
        private String staffId;

        private String date;

        private String leave_type;

        //    @SerializedName("currentAcademicYear")
        private String attendance_type;
        private String in_time;
        private String out_time;
        private int working_hours;


        public void setStaffName(String id) {
            this.staffName = id;
        }

        public String getStaffName() {
            return staffName;
        }

        public void setStaffId(String id) {
            this.staffId = id;
        }

        public String getStaffId() {
            return staffId;
        }
        public void setDate(String id) {
            this.date = id;
        }

        public String getDate() {
            return date;
        }

        public void setLeave_type(String id) {
            this.leave_type = id;
        }

        public String getLeave_type() {
            return leave_type;
        }

        public void setAttendance_type(String id) {
            this.attendance_type = id;
        }

        public String getAttendance_type() {
            return attendance_type;
        }

        public void setIn_time(String id) {
            this.in_time = id;
        }

        public String getIn_time() {
            return in_time;
        }

        public void setOut_time(String id) {
            this.out_time = id;
        }

        public String getOut_time() {
            return out_time;
        }

        public void setWorking_hours(int id) {
            this.working_hours = id;
        }

        public int getWorking_hours() {
            return working_hours;
        }

    }
}
