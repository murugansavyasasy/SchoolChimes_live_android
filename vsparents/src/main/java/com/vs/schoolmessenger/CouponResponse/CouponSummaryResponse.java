package com.vs.schoolmessenger.CouponResponse;

import com.vs.schoolmessenger.CouponDataClass.CategoryData;

public class CouponSummaryResponse {

    private boolean status;
    private String message;
    private CategoryData data;

    public CouponSummaryResponse(boolean status, String message, CategoryData data) {
        this.status = status;
        this.message = message;
        this.data= data;
    }

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CategoryData getData() {
        return data;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setData(CategoryData data) {
        this.data = data;
    }


}
