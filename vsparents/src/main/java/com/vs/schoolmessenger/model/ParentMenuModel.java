package com.vs.schoolmessenger.model;

public class ParentMenuModel {

    private String menu_name;
    private String unread_count;

    public ParentMenuModel(String name, String count) {
        this.menu_name = name;
        this.unread_count = count;
    }

    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String name) {
        this.menu_name = name;
    }

    public String getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(String count) {
        this.unread_count = count;
    }
}