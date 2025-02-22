package com.vs.schoolmessenger.CouponDataClass;

import android.icu.util.ULocale;

import java.util.List;

public class CategoryData {
    private List<ULocale.Category> categories;
    private String totalPages; // Renamed to camelCase for Java conventions

    // Constructor
    public CategoryData(List<ULocale.Category> categories, String totalPages) {
        this.categories = categories;
        this.totalPages = totalPages;
    }

    // Getters
    public List<ULocale.Category> getCategories() {
        return categories;
    }

    public String getTotalPages() {
        return totalPages;
    }

    // Setters
    public void setCategories(List<ULocale.Category> categories) {
        this.categories = categories;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    // toString method (optional)

}
