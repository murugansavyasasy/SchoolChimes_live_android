package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateTypeModelItem{
	private String status;
	private String message;
	private List<CertificateDataItem> data;

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

	public void setData(List<CertificateDataItem> data){
		this.data = data;
	}

	public List<CertificateDataItem> getData(){
		return data;
	}
}