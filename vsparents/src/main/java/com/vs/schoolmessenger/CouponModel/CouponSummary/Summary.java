package com.vs.schoolmessenger.CouponModel.CouponSummary;

import com.google.gson.annotations.SerializedName;

public class Summary {


    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    @SerializedName("expiry_date")
    private String expiry_date;

    @SerializedName("source_link")
    private String source_link;

    @SerializedName("campaign_name")
    private String campaignName;

    @SerializedName("campaign_type")
    private String campaignType;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("offer_type")
    private String offerType;

    @SerializedName("discount")
    private int discount;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_image")
    private String categoryImage;

    @SerializedName("merchant_logo")
    private String merchantLogo;

    @SerializedName("coupon_status")
    private String coupon_status;

    public String getCoupon_status() {
        return coupon_status;
    }

    public String getSource_link() {
        return source_link;
    }

    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignType() {
        return campaignType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getOfferType() {
        return offerType;
    }

    public int getDiscount() {
        return discount;
    }




    public String getMerchantName() {
        return merchantName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

}
