package com.vs.schoolmessenger.LessonPlan.Model;

public class LessPlanData {


	private String sectionName;
	private int completed_items;
	private int section_subject_id;
	private String percentage_value;
	private String itemsCompleted;
	private String staffName;
	private String className;
	private String total_items;
	private String subjectName;

	public void setSectionName(String sectionName){
		this.sectionName = sectionName;
	}

	public String getSectionName(){
		return sectionName;
	}

	public void setCompletedItems(int completedItems){
		this.completed_items = completedItems;
	}

	public int getCompletedItems(){
		return completed_items;
	}

	public void setSectionSubjectId(int sectionSubjectId){
		this.section_subject_id = sectionSubjectId;
	}

	public int getSectionSubjectId(){
		return section_subject_id;
	}

	public void setPercentageValue(String percentageValue){
		this.percentage_value = percentageValue;
	}

	public String getPercentageValue(){
		return percentage_value;
	}

	public void setItemsCompleted(String itemsCompleted){
		this.itemsCompleted = itemsCompleted;
	}

	public String getItemsCompleted(){
		return itemsCompleted;
	}

	public void setStaffName(String staffName){
		this.staffName = staffName;
	}

	public String getStaffName(){
		return staffName;
	}

	public void setClassName(String className){
		this.className = className;
	}

	public String getClassName(){
		return className;
	}

	public void setTotalItems(String totalItems){
		this.total_items = totalItems;
	}

	public String getTotalItems(){
		return total_items;
	}

	public void setSubjectName(String subjectName){
		this.subjectName = subjectName;
	}

	public String getSubjectName(){
		return subjectName;
	}
}
