package com.vs.schoolmessenger.LessonPlan.Model;

import java.util.List;

public class LessonPlanModel{
	private List<LessPlanData> data;
	private String message;
	private int status;

	public void setData(List<LessPlanData> data){
		this.data = data;
	}

	public List<LessPlanData> getData(){
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