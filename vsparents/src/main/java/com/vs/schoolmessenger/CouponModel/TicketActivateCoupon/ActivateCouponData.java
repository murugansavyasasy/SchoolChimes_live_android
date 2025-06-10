package com.vs.schoolmessenger.CouponModel.TicketActivateCoupon;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ActivateCouponData {
    @SerializedName("coupons")
    private List<ActivateCoupon> coupons;

    @SerializedName("merchant_logo")
    private String merchant_logo;

    @SerializedName("offer")
    private String offer;

    @SerializedName("redirect_url")
    private String redirect_url;

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("isCTAvalid")
    private boolean isCTAvalid;

    @SerializedName("CTAname")
    private String CTAname;

    @SerializedName("CTAredirect")
    private String CTAredirect;

    // Getters for all fields
    public List<ActivateCoupon> getCoupons() {
        return coupons;
    }

    public String getMerchant_logo() {
        return merchant_logo;
    }

    public String getOffer() {
        return offer;
    }

    public String getRedirect_url() {
        return redirect_url;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public boolean isCTAvalid() {
        return isCTAvalid;
    }

    public String getCTAname() {
        return CTAname;
    }

    public String getCTAredirect() {
        return CTAredirect;
    }
}
