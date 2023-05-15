package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateListModelItem{
	private Integer status;
	private String message;
	private List<CertificateListDataItem> data;

	public void setStatus(Integer status){
		this.status = status;
	}

	public Integer getStatus(){
		return status;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setData(List<CertificateListDataItem> data){
		this.data = data;
	}

	public List<CertificateListDataItem> getData(){
		return data;
	}
}