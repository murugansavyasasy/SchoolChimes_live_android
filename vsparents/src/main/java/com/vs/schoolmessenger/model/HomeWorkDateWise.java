package com.vs.schoolmessenger.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HomeWorkDateWise {



    @SerializedName("subjectname")
    private String subjectname;

    @SerializedName("topic")
    private String topic;

    @SerializedName("content")
    private String content;


    @SerializedName("filepath")
    private List<HomeWorkFilePaths> filepath;



    public void setSubjectname(String status){
        this.subjectname = status;
    }

    public String getSubjectname(){
        return subjectname;
    }

    public void setTopic(String status){
        this.topic = status;
    }

    public String getTopic(){
        return topic;
    }

    public void setContent(String status){
        this.content = status;
    }

    public String getContent(){
        return content;
    }

    public void setFilepath(List<HomeWorkFilePaths> data){
        this.filepath = data;
    }

    public List<HomeWorkFilePaths> getFilepath(){
        return filepath;
    }

}
