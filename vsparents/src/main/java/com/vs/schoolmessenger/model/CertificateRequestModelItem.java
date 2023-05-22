package com.vs.schoolmessenger.model;

public class CertificateRequestModelItem{
	private String Status;
	private String Message;

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
}
