package com.vs.schoolmessenger.CouponModel.TicketCouponSummary;

import com.google.gson.annotations.SerializedName;

public class TicketSummary {

    @SerializedName("campaign_name")
    private String campaignName;

    @SerializedName("campaign_type")
    private String campaignType;

//    @SerializedName("thumbnail")
//    private String thumbnail;

    @SerializedName("offer_type")
    private String offerType;

    @SerializedName("discount")
    private int discount;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("merchant_id")
    private String merchant_id;

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_image")
    private String categoryImage;

    @SerializedName("about_merchant")
    private  String about_merchant;

    @SerializedName("merchant_logo")
    private String merchantLogo;
    @SerializedName("expires_in")
    private String expires_in;


    @SerializedName("offer_to_show")
    private String offer_to_show;

    @SerializedName("cover_image")
    private String cover_image;

    public String getCoupon_code() {
        return coupon_code;
    }

    @SerializedName("coupon_code")
    private String coupon_code;

    public String getHow_to_use() {
        return how_to_use;
    }

    @SerializedName("how_to_use")
    private String how_to_use;


    public String getCover_image() {
        return cover_image;
    }
    public String getExpires_in() {
        return expires_in;
    }


    public String getCampaignName() {
        return campaignName;
    }

    public String getCampaignType() {
        return campaignType;
    }

//    public String getThumbnail() {
//        return thumbnail;
//    }

    public String getOfferType() {
        return offerType;
    }


    public String getOffer_to_show() {
        return offer_to_show;
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
