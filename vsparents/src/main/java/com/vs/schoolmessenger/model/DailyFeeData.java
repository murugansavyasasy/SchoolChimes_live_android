package com.vs.schoolmessenger.model;

import org.json.JSONArray;

import java.util.ArrayList;

public class DailyFeeData {
    private String typeName;
    private String total;

    ArrayList<DailyFeeReportData> data = new ArrayList<>();

    JSONArray jsonArray;

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String value) {
        this.typeName = value;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String value) {
        this.total = value;
    }

    public ArrayList<DailyFeeReportData> getData() {
        return data;
    }

    public void setData(ArrayList<DailyFeeReportData> value) {
        this.data = value;
    }

    public DailyFeeData(String typeName, String total, ArrayList<DailyFeeReportData> data){
        this.typeName = typeName;
        this.total = total;
        this.data=data;
    }
}
