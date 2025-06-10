package com.vs.schoolmessenger.CouponModel.TicketActivateCoupon;

import com.google.gson.annotations.SerializedName;

public class ActivateCoupon {
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

    public String getIsCTAvalid() {
        return isCTAvalid;
    }

    public String getCTAname() {
        return CTAname;
    }

    public String getCTAredirect() {
        return CTAredirect;
    }

    @SerializedName("merchant_logo")
    private String merchant_logo;

    @SerializedName("offer")
    private String offer;

    @SerializedName("redirect_url")
    private String redirect_url;

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("isCTAvalid")
    private String isCTAvalid;

    @SerializedName("CTAname")
    private String CTAname;

    @SerializedName("CTAredirect")
    private String CTAredirect;

}