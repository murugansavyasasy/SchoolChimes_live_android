package com.vs.schoolmessenger.LessonPlan.Model;

import java.util.List;

public class EditLessonModel{
	private List<EditDataItem> data;
	private String message;
	private int status;

	public void setData(List<EditDataItem> data){
		this.data = data;
	}

	public List<EditDataItem> getData(){
		return data;
	}

	public void setMessage(String message){
		this.message = message;
	}

	public String getMessage(){
		return message;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}