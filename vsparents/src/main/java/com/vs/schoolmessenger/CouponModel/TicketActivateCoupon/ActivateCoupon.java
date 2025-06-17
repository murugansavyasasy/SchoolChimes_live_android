package com.vs.schoolmessenger.CouponModel.TicketActivateCoupon;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ActivateCoupon implements Serializable {

    @SerializedName("coupon_code")
    private String coupon_code;

    @SerializedName("qr_code")
    private String qr_code;

    @SerializedName("expiry_date")
    private String expiry_date;

    // Existing fields (in case they're still being used individually)
    @SerializedName("merchant_logo")
    private String merchant_logo;

    @SerializedName("offer")
    private String offer;

    @SerializedName("redirect_url")
    private String redirect_url;

    @SerializedName("isCTAvalid")
    private String isCTAvalid;

    @SerializedName("CTAname")
    private String CTAname;

    @SerializedName("CTAredirect")
    private String CTAredirect;

    // Getters
    public String getCoupon_code() { return coupon_code; }

    public String getQr_code() { return qr_code; }

    public String getExpiry_date() { return expiry_date; }

    public String getMerchant_logo() { return merchant_logo; }

    public String getOffer() { return offer; }

    public String getRedirect_url() { return redirect_url; }

    public String getIsCTAvalid() { return isCTAvalid; }

    public String getCTAname() { return CTAname; }

    public String getCTAredirect() { return CTAredirect; }
}
