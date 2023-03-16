package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

public class DailyFeeCollectionModelItem{

	@SerializedName("Status")
	private String status;

	@SerializedName("Message")
	private String message;

	@SerializedName("data")
	private CollectionData data;

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setData(CollectionData data){
		this.data = data;
	}

	public CollectionData getData(){
		return data;
	}
}
