package com.vs.schoolmessenger.model;

import java.util.List;

public class PunchHistoryRes {
    private int status;

    private String message;

    private List<PunchHistoryRes.PunchHistoryData> data;


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

    public void setData(List<PunchHistoryRes.PunchHistoryData> data) {
        this.data = data;
    }
    public List<PunchHistoryRes.PunchHistoryData> getData() {
        return data;
    }


    public class PunchHistoryData {

        private String date;

        private List<PunchHistoryTimings> timings;

        public void setDate(String id) {
            this.date = id;
        }

        public String getDate() {
            return date;
        }

        public void setTimings(List<PunchHistoryTimings> data) {
            this.timings = data;
        }
        public List<PunchHistoryTimings> getTimings() {
            return timings;
        }

        public class PunchHistoryTimings {

            private String time;
            private String deviceId;

            private String deviceModel;
            private PunchType punchType;

            public void setTime(String id) {
                this.time = id;
            }

            public String getTime() {
                return time;
            }

            public void setDeviceId(String id) {
                this.deviceId = id;
            }

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceModel(String id) {
                this.deviceModel = id;
            }

            public String getDeviceModel() {
                return deviceModel;
            }

            public void setPunchType(PunchType id) {
                this.punchType = id;
            }

            public PunchType getPunchType() {
                return punchType;
            }

            public class PunchType {

                private int id;
                private String value;

                public void setId(int id) {
                    this.id = id;
                }

                public int getId() {
                    return id;
                }

                public void setValue(String id) {
                    this.value = id;
                }

                public String getValue() {
                    return value;
                }

            }

        }



    }
}