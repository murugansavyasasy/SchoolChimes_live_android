package com.vs.schoolmessenger.CouponModel.CouponMenu;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CouponMenuData {
    @SerializedName("categories")
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }
}
