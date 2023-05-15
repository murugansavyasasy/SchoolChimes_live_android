package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateRequestModel{
	private List<CertificateRequestModelItem> certificateRequestModel;

	public void setCertificateRequestModel(List<CertificateRequestModelItem> certificateRequestModel){
		this.certificateRequestModel = certificateRequestModel;
	}

	public List<CertificateRequestModelItem> getCertificateRequestModel(){
		return certificateRequestModel;
	}
}