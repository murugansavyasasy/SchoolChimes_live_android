package com.vs.schoolmessenger.model;

public class CertificateDataItem {
	private String certificateName;
	private Integer id;

	public void setCertificateName(String certificateName){
		this.certificateName = certificateName;
	}

	public String getCertificateName(){
		return certificateName;
	}

	public void setId(Integer id){
		this.id = id;
	}

	public Integer getId(){
		return id;
	}
}
