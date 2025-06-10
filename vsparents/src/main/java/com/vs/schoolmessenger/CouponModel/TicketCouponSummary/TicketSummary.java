package com.vs.schoolmessenger.CouponModel.TicketCouponSummary;

import java.util.ArrayList;
import java.util.List;

public class TicketSummary {
    private int id;
    private String merchant_name;
    private String campaign_name;
    private int merchant_id;
    private String merchant_logo;
    private String about_merchant;
    private String campaign_type;
    private String offer_type;
    private int discount;
    private List<String> template_files;
    private String threshold_amount;
    private String expiry_date;
    private String expiry_type;
    private String coupon_valid_for;
    private String how_to_use;
    private String terms_and_conditions;
    private String cover_image;
    private String cta_url;
    private String offer_text;
    private List<Location> location_list;

    private String redeemed_on;
    private String qr_code;
    private String category_name;
    private String source_link;
    private String industry_name;
    private String coupon_code;
    private String coupon_status;
    private int expires_in;
    private boolean isCTAvalid;
    private String CTAname;
    private String CTAredirect;
    private String offer_to_show;

    // --- Getters and Setters for all fields ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMerchant_name() {
        return merchant_name;
    }

    public void setMerchant_name(String merchant_name) {
        this.merchant_name = merchant_name;
    }

    public String getCampaign_name() {
        return campaign_name;
    }

    public void setCampaign_name(String campaign_name) {
        this.campaign_name = campaign_name;
    }

    public int getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(int merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getMerchant_logo() {
        return merchant_logo;
    }

    public void setMerchant_logo(String merchant_logo) {
        this.merchant_logo = merchant_logo;
    }

    public String getAbout_merchant() {
        return about_merchant;
    }

    public void setAbout_merchant(String about_merchant) {
        this.about_merchant = about_merchant;
    }

    public String getCampaign_type() {
        return campaign_type;
    }

    public void setCampaign_type(String campaign_type) {
        this.campaign_type = campaign_type;
    }

    public String getOffer_type() {
        return offer_type;
    }

    public void setOffer_type(String offer_type) {
        this.offer_type = offer_type;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public List<String> getTemplate_files() {
        return template_files;
    }

    public void setTemplate_files(List<String> template_files) {
        this.template_files = template_files;
    }

    public String getThreshold_amount() {
        return threshold_amount;
    }

    public void setThreshold_amount(String threshold_amount) {
        this.threshold_amount = threshold_amount;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getExpiry_type() {
        return expiry_type;
    }

    public void setExpiry_type(String expiry_type) {
        this.expiry_type = expiry_type;
    }

    public String getCoupon_valid_for() {
        return coupon_valid_for;
    }

    public void setCoupon_valid_for(String coupon_valid_for) {
        this.coupon_valid_for = coupon_valid_for;
    }

    public String getHow_to_use() {
        return how_to_use;
    }

    public void setHow_to_use(String how_to_use) {
        this.how_to_use = how_to_use;
    }

    public String getTerms_and_conditions() {
        return terms_and_conditions;
    }

    public void setTerms_and_conditions(String terms_and_conditions) {
        this.terms_and_conditions = terms_and_conditions;
    }

    public String getCover_image() {
        return cover_image;
    }

    public void setCover_image(String cover_image) {
        this.cover_image = cover_image;
    }

    public String getCta_url() {
        return cta_url;
    }

    public void setCta_url(String cta_url) {
        this.cta_url = cta_url;
    }

    public String getOffer_text() {
        return offer_text;
    }

    public void setOffer_text(String offer_text) {
        this.offer_text = offer_text;
    }

    public List<Location> getLocation_list() {
        return location_list;
    }

    public void setLocation_list(List<Location> location_list) {
        this.location_list = location_list;
    }

    public String getRedeemed_on() {
        return redeemed_on;
    }

    public void setRedeemed_on(String redeemed_on) {
        this.redeemed_on = redeemed_on;
    }

    public String getQr_code() {
        return qr_code;
    }

    public void setQr_code(String qr_code) {
        this.qr_code = qr_code;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSource_link() {
        return source_link;
    }

    public void setSource_link(String source_link) {
        this.source_link = source_link;
    }

    public String getIndustry_name() {
        return industry_name;
    }

    public void setIndustry_name(String industry_name) {
        this.industry_name = industry_name;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_status() {
        return coupon_status;
    }

    public void setCoupon_status(String coupon_status) {
        this.coupon_status = coupon_status;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public boolean isCTAvalid() {
        return isCTAvalid;
    }

    public void setCTAvalid(boolean CTAvalid) {
        isCTAvalid = CTAvalid;
    }

    public String getCTAname() {
        return CTAname;
    }

    public void setCTAname(String CTAname) {
        this.CTAname = CTAname;
    }

    public String getCTAredirect() {
        return CTAredirect;
    }

    public void setCTAredirect(String CTAredirect) {
        this.CTAredirect = CTAredirect;
    }

    public String getOffer_to_show() {
        return offer_to_show;
    }

    public void setOffer_to_show(String offer_to_show) {
        this.offer_to_show = offer_to_show;
    }

    // Inner class for location
    public static class Location {
        private String location_name;
        private String latitude;
        private String longitude;

        public String getLocation_name() {
            return location_name;
        }

        public void setLocation_name(String location_name) {
            this.location_name = location_name;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }
    }
}


//    @SerializedName("campaign_name")
//    private String campaignName;
//
//    @SerializedName("campaign_type")
//    private String campaignType;
//
/// /    @SerializedName("thumbnail")
/// /    private String thumbnail;
//
//    @SerializedName("offer_type")
//    private String offerType;
//
//    @SerializedName("discount")
//    private int discount;
//
//    @SerializedName("merchant_name")
//    private String merchantName;
//
//    @SerializedName("merchant_id")
//    private String merchant_id;
//
//    @SerializedName("category_name")
//    private String categoryName;
//
//    @SerializedName("category_image")
//    private String categoryImage;
//
//    @SerializedName("about_merchant")
//    private  String about_merchant;
//
//    @SerializedName("merchant_logo")
//    private String merchantLogo;
//    @SerializedName("expires_in")
//    private String expires_in;
//
//
//    @SerializedName("offer_to_show")
//    private String offer_to_show;
//
//    @SerializedName("cover_image")
//    private String cover_image;
//
//    public String getCoupon_code() {
//        return coupon_code;
//    }
//
//    @SerializedName("coupon_code")
//    private String coupon_code;
//
//    public String getHow_to_use() {
//        return how_to_use;
//    }
//
//    @SerializedName("how_to_use")
//    private String how_to_use;
//
//
//    public String getCover_image() {
//        return cover_image;
//    }
//    public String getExpires_in() {
//        return expires_in;
//    }
//
//
//    public String getCampaignName() {
//        return campaignName;
//    }
//
//    public String getCampaignType() {
//        return campaignType;
//    }
//
/// /    public String getThumbnail() {
/// /        return thumbnail;
/// /    }
//
//    public String getOfferType() {
//        return offerType;
//    }
//
//
//    public String getOffer_to_show() {
//        return offer_to_show;
//    }
//
//    public int getDiscount() {
//        return discount;
//    }
//
//    public String getMerchantName() {
//        return merchantName;
//    }
//
//    public String getCategoryName() {
//        return categoryName;
//    }
//
//    public String getCategoryImage() {
//        return categoryImage;
//    }
//
//    public String getMerchantLogo() {
//        return merchantLogo;
//    }
//}
