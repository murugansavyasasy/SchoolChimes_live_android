package com.vs.schoolmessenger.LessonPlan.Model;

import java.util.List;

public class ViewDataItem {
	private int particular_id;
	private int status;
	private List<DataArrayItem> data_array;

	public void setParticularId(int particularId){
		this.particular_id = particularId;
	}

	public int getParticularId(){
		return particular_id;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}

	public void setDataArray(List<DataArrayItem> dataArray){
		this.data_array = dataArray;
	}

	public List<DataArrayItem> getDataArray(){
		return data_array;
	}
}