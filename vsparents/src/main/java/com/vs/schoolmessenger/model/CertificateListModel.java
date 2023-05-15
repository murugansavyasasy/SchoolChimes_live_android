package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateListModel{
	private List<CertificateListModelItem> certificateListModel;

	public void setCertificateListModel(List<CertificateListModelItem> certificateListModel){
		this.certificateListModel = certificateListModel;
	}

	public List<CertificateListModelItem> getCertificateListModel(){
		return certificateListModel;
	}
}