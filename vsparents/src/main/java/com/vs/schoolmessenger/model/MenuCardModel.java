package com.vs.schoolmessenger.model;

public class MenuCardModel {

    String MenuName;
    int MenuId;

    public MenuCardModel(String name,int id)
    {
        this.MenuId=id;
        this.MenuName=name;
    }
    public String getMenuName()
    {
        return MenuName;
    }
    public int getMenuId()
    {
        return MenuId;
    }
}