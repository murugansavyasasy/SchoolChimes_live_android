package com.vs.schoolmessenger.CouponModel.CouponSummary;

import com.google.gson.annotations.SerializedName;

public class Summary {

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
