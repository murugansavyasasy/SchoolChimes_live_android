package com.vs.schoolmessenger.model;

public class LibraryDetails {
    private String Id, Name, IssuedDate,DueDate;

    public LibraryDetails(String Id, String Name, String IssuedDate,String DueDate) {
        this.Id = Id;
        this.Name = Name;
        this.IssuedDate = IssuedDate;
        this.DueDate = DueDate;
    }

    public LibraryDetails() {}

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getIssuedDate() {
        return IssuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.IssuedDate = issuedDate;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        this.DueDate = dueDate;
    }
}
