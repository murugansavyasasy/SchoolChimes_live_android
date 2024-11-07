package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class getSlotAvailabilityData {

    @SerializedName("eventdate")
    String isEventDate;

    @SerializedName("totalSlot")
    String isTotalSlot;

    @SerializedName("availableSlot")
    String isAvailableSlot;

    @SerializedName("bookedSlot")
    String isBookedSlot;

    @SerializedName("mySlot")
    String isMySlot;

    public  void setIsEventDate
            (String eventDate){
        this.isEventDate=eventDate;
    }
    public String getIsEventDate(){
        return   isEventDate;
    }
    public void setIsTotalSlot(String totalSlot){
        this.isTotalSlot=totalSlot;
    }
    public String getIsTotalSlot(){
        return isTotalSlot;
    }

    public void setIsAvailableSlot(String availableSlot){
        this.isAvailableSlot=availableSlot;
    }

    public String getIsAvailableSlot(){
        return isAvailableSlot;
    }

    public String getIsBookedSlot() {
        return isBookedSlot;
    }

    public void setIsBookedSlot(String isBookedSlot) {
        this.isBookedSlot = isBookedSlot;
    }

    public String getIsMySlot() {
        return isMySlot;
    }

    public void setIsMySlot(String isMySlot) {
        this.isMySlot = isMySlot;
    }
}
