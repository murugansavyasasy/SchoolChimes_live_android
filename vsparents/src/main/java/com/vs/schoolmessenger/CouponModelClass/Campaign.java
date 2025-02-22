package com.vs.schoolmessenger.CouponModelClass;

public class Campaign {
    private String sourceLink;
    private String campaignName;
    private String campaignType;
    private String thumbnail;
    private String expiryDate;
    private String endDate;
    private String offerType;
    private int discount;
    private String merchantName;
    private String categoryName;
    private String categoryImage;
    private String merchantLogo;

    // Constructor
    public Campaign(String sourceLink, String campaignName, String campaignType, String thumbnail,
                    String expiryDate, String endDate, String offerType, int discount,
                    String merchantName, String categoryName, String categoryImage, String merchantLogo) {
        this.sourceLink = sourceLink;
        this.campaignName = campaignName;
        this.campaignType = campaignType;
        this.thumbnail = thumbnail;
        this.expiryDate = expiryDate;
        this.endDate = endDate;
        this.offerType = offerType;
        this.discount = discount;
        this.merchantName = merchantName;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
        this.merchantLogo = merchantLogo;
    }

    // Getters
    public String getSourceLink() {
        return sourceLink;
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

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getEndDate() {
        return endDate;
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

    // Setters
    public void setSourceLink(String sourceLink) {
        this.sourceLink = sourceLink;
    }

    public void setCampaignName(String campaignName) {
        this.campaignName = campaignName;
    }

    public void setCampaignType(String campaignType) {
        this.campaignType = campaignType;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setOfferType(String offerType) {
        this.offerType = offerType;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }


}
