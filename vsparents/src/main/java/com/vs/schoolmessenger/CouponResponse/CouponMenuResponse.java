package com.vs.schoolmessenger.CouponResponse;

import com.vs.schoolmessenger.CouponDataClass.CouponData;

public class CouponMenuResponse {
    private boolean status;
    private String message;
    private CouponData data;

    public CouponMenuResponse(boolean status, String message, CouponData data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


    public boolean isStatus() {
        return status;
    }

    public  String getMessage() {
        return message;
    }

    public CouponData getData() {
        return data;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(CouponData data) {
        this.data = data;
    }

}
