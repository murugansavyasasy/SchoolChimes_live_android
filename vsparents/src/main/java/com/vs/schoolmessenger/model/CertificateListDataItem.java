package com.vs.schoolmessenger.model;

public class CertificateListDataItem {
	private String reason;
	private String certificateUrl;
	private String isIssuedOnApp;
	private String createdOn;
	private String urgencyLevel;
	private String requestedFor;

	public void setReason(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return reason;
	}

	public void setCertificateUrl(String certificateUrl){
		this.certificateUrl = certificateUrl;
	}

	public String getCertificateUrl(){
		return certificateUrl;
	}

	public void setIsIssuedOnApp(String isIssuedOnApp){
		this.isIssuedOnApp = isIssuedOnApp;
	}

	public String getIsIssuedOnApp(){
		return isIssuedOnApp;
	}

	public void setCreatedOn(String createdOn){
		this.createdOn = createdOn;
	}

	public String getCreatedOn(){
		return createdOn;
	}

	public void setUrgencyLevel(String urgencyLevel){
		this.urgencyLevel = urgencyLevel;
	}

	public String getUrgencyLevel(){
		return urgencyLevel;
	}

	public void setRequestedFor(String requestedFor){
		this.requestedFor = requestedFor;
	}

	public String getRequestedFor(){
		return requestedFor;
	}
}
