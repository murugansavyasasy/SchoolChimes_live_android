package com.vs.schoolmessenger.model;

import org.json.JSONArray;

public class QuestionForQuiz {
//      "QestionId": 11,
//              "Question": "test",
//              "Answer": [
//              "1~testaaa",
//              "2~testbbb",
//              "3~testccc",
//              "4~testddd"
//              ],
//              "VideoUrl": "test/video/1",
//              "Date": "18/05/2021",
//              "Time": "19:48:46"

    public int QestionId;
    public String Questionnum;
    public String Question;
    public String VideoUrl;
    public String FileUrl;
    public int Mark;
    public String FileType;
    public JSONArray Answer;
    boolean selectedstatus=false;

    public QuestionForQuiz(int QestionId, String Question, String VideoUrl, String FileType, String FileUrl, int Mark,String questionnum, JSONArray Answer){
        this.QestionId=QestionId;
        this.Question=Question;
        this.VideoUrl=VideoUrl;
        this.FileType=FileType;
        this.FileUrl=FileUrl;
        this.Mark=Mark;
        this.Questionnum=questionnum;
        this.Answer=Answer;
    }

    public int getQestionId() {
        return QestionId;
    }

    public void setQestionId(int qestionId) {
        QestionId = qestionId;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getVideoUrl() {
        return VideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        VideoUrl = videoUrl;
    }





    public String getFileType() {
        return FileType;
    }

    public void setFileType(String FileType) {
        FileType = FileType;
    }

    public String getFileUrl() {
        return FileUrl;
    }

    public void setFileUrl(String fileUrl) {
        FileUrl = fileUrl;
    }

    public int getMark() {
        return Mark;
    }

    public void setMark(int mark) {
        Mark = mark;
    }

    public JSONArray getAnswer() {
        return Answer;
    }

    public void setAnswer(JSONArray answer) {
        Answer = answer;
    }

    public boolean isSelectedstatus() {
        return selectedstatus;
    }

    public void setSelectedstatus(boolean selectedstatus) {
        this.selectedstatus = selectedstatus;
    }

    public String getQuestionnum() {
        return Questionnum;
    }

    public void setQuestionnum(String questionnum) {
        Questionnum = questionnum;
    }


}
