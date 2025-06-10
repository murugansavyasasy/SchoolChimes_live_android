package com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary;

import com.google.gson.annotations.SerializedName;

public class ActivateCouponSummary {
    @SerializedName("campaign_name")
    private String campaignName;

    @SerializedName("expiry_date")
    private String expiry_date;
    @SerializedName("cover_image")
    private String cover_image;
    @SerializedName("merchant_name")
    private String merchant_name;
    @SerializedName("threshold_amount")
    private String threshold_amount;
    @SerializedName("offer_text")
    private String offer_text;
    @SerializedName("merchant_logo")
    private String merchant_logo;
    @SerializedName("offer_type")
    private String offer_type;
    @SerializedName("how_to_use")
    private String how_to_use;
    @SerializedName("campaign_type")
    private String campaign_type;
    @SerializedName("terms_and_conditions")
    private String terms_and_conditions;


    @SerializedName("expiry_type")
    private String expiry_type;

    @SerializedName("coupon_valid_for")
    private String coupon_valid_for;

    @SerializedName("customer_buys_value")
    private String customer_buys_value;

    @SerializedName("customer_gets_value")
    private String customer_gets_value;


    @SerializedName("template_file")
    private String template_file;

    @SerializedName("offer_to_show")
    private String offer_to_show;


    public String getCover_image() {
        return cover_image;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public String getThreshold_amount() {
        return threshold_amount;
    }

    public String getOffer_text() {
        return offer_text;
    }

    public String getMerchant_logo() {
        return merchant_logo;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public String getHow_to_use() {
        return how_to_use;
    }

    public String getCampaign_type() {
        return campaign_type;
    }

    public String getTerms_and_conditions() {
        return terms_and_conditions;
    }

    public String getExpiry_type() {
        return expiry_type;
    }

    public String getCoupon_valid_for() {
        return coupon_valid_for;
    }

    public String getCustomer_buys_value() {
        return customer_buys_value;
    }

    public String getCustomer_gets_value() {
        return customer_gets_value;
    }

    public String getTemplate_file() {
        return template_file;
    }

    public String getOffer_to_show() {
        return offer_to_show;
    }


}
