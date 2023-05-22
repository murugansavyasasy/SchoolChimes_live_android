package com.vs.schoolmessenger.model;

public class CertificateListDataItem {


	private String reason;
	private String certificate_url;
	private String is_issued_on_app;
	private String created_on;
	private String urgency_level;
	private String requested_for;

	public void setReason(String reason){
		this.reason = reason;
	}

	public String getReason(){
		return reason;
	}

	public void setCertificateUrl(String certificateUrl){
		this.certificate_url = certificateUrl;
	}

	public String getCertificateUrl(){
		return certificate_url;
	}

	public void setIsIssuedOnApp(String isIssuedOnApp){
		this.is_issued_on_app = isIssuedOnApp;
	}

	public String getIsIssuedOnApp(){
		return is_issued_on_app;
	}

	public void setCreatedOn(String createdOn){
		this.created_on = createdOn;
	}

	public String getCreatedOn(){
		return created_on;
	}

	public void setUrgencyLevel(String urgencyLevel){
		this.urgency_level = urgencyLevel;
	}

	public String getUrgencyLevel(){
		return urgency_level;
	}

	public void setRequestedFor(String requestedFor){
		this.requested_for = requestedFor;
	}

	public String getRequestedFor(){
		return requested_for;
	}
}
