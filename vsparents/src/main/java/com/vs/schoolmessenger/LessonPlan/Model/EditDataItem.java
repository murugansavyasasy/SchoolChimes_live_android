package com.vs.schoolmessenger.LessonPlan.Model;

import java.util.List;

public class EditDataItem {
	private int field_id;
	private List<FieldDataItem> field_data;
	private String name;
	private int isdisable;
	private int id;
	private String field_type;
	private String value;

	public void setFieldId(int fieldId){
		this.field_id = fieldId;
	}

	public int getFieldId(){
		return field_id;
	}

	public void setFieldData(List<FieldDataItem> fieldData){
		this.field_data = fieldData;
	}

	public List<FieldDataItem> getFieldData(){
		return field_data;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	public void setIsdisable(int isdisable){
		this.isdisable = isdisable;
	}

	public int getIsdisable(){
		return isdisable;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setFieldType(String fieldType){
		this.field_type = fieldType;
	}

	public String getFieldType(){
		return field_type;
	}

	public void setValue(String value){
		this.value = value;
	}

	public String getValue(){
		return value;
	}
}