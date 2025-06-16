package com.vs.schoolmessenger.CouponModel.CouponMenu;


import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("id")
    private int id;

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setId(int id) {
        this.id = id;
    }



    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    @SerializedName("category_name")
    private String categoryName;

    @SerializedName("category_image")
    private String categoryImage;

    public int getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }


    private int drawableResId = -1;

    public int getDrawableResId() {
        return drawableResId;
    }

    public void setDrawableResId(int drawableResId) {
        this.drawableResId = drawableResId;
    }

}

