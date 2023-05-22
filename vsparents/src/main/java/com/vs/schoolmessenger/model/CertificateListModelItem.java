package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateListModelItem{
	private String Status;
	private String Message;
	private List<CertificateListDataItem> data;

	public void setStatus(String status){
		this.Status = status;
	}

	public String getStatus(){
		return Status;
	}

	public void setMessage(String message){
		this.Message = message;
	}

	public String getMessage(){
		return Message;
	}

	public void setData(List<CertificateListDataItem> data){
		this.data = data;
	}

	public List<CertificateListDataItem> getData(){
		return data;
	}
}