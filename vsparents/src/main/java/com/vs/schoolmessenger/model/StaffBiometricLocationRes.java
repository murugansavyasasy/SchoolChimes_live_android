package com.vs.schoolmessenger.model;

import java.util.List;

public class StaffBiometricLocationRes {

    private int status;

    private String message;

    private List<BiometricLoationData> data;


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


    public void setData(List<BiometricLoationData> data) {
        this.data = data;
    }
    public List<BiometricLoationData> getData() {
        return data;
    }



    public class BiometricLoationData {
        private String latitude;

        private String longitude;

        //    @SerializedName("currentAcademicYear")
        private String location;
        private String distance;
        private int id;


        public void setLatitude(String id) {
            this.latitude = id;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLongitude(String id) {
            this.longitude = id;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLocation(String id) {
            this.location = id;
        }

        public String getLocation() {
            return location;
        }

        public void setDistance(String id) {
            this.distance = id;
        }

        public String getDistance() {
            return distance;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

    }
}