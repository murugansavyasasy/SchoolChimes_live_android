package com.vs.schoolmessenger.model;

import java.util.List;

public class StaffListRes {
    private int status;

    private String message;

    private List<StaffListData> data;


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

    public void setData(List<StaffListData> data) {
        this.data = data;
    }

    public List<StaffListData> getData() {
        return data;
    }


    public class StaffListData {

        private int staffId;

        private String staffName;

        public void setStaffId(int id) {
            this.staffId = id;
        }

        public int getStaffId() {
            return staffId;
        }

        public void setStaffName(String id) {
            this.staffName = id;
        }

        public String getStaffName() {
            return staffName;
        }


    }
}
