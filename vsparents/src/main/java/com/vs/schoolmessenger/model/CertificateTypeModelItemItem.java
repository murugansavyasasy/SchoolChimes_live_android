package com.vs.schoolmessenger.model;

import java.util.List;

public class CertificateTypeModelItemItem{
	private String Status;
	private String Message;
	private List<CertificateDataItem> data;
	private List<String> urgenctLevelList;

	public String getStatus(){
		return Status;
	}

	public String getMessage(){
		return Message;
	}

	public List<CertificateDataItem> getData(){
		return data;
	}

	public List<String> getUrgenctLevelList(){
		return urgenctLevelList;
	}
}