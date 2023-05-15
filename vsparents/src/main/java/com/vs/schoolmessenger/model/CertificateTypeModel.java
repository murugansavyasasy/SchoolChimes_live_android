package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateTypeModel{
	private List<CertificateTypeModelItem> certificateTypeModel;

	public void setCertificateTypeModel(List<CertificateTypeModelItem> certificateTypeModel){
		this.certificateTypeModel = certificateTypeModel;
	}

	public List<CertificateTypeModelItem> getCertificateTypeModel(){
		return certificateTypeModel;
	}
}