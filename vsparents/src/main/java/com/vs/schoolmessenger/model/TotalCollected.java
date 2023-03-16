package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class TotalCollected{

	@SerializedName("name")
	private String name;

	@SerializedName("paid_amount")
	private String paidAmount;

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setPaidAmount(String paidAmount){
		this.paidAmount = paidAmount;
	}

	public String getPaidAmount(){
		return paidAmount;
	}
}
