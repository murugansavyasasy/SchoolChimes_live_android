package com.vs.schoolmessenger.model;

import org.json.JSONArray;

public class QuestionForQuiz {

    private int QestionId;
    private String Questionnum;
    private String Question;
    private String VideoUrl;
    private String FileUrl;
    private int Mark;
    private String FileType;
    private JSONArray Answer;
    private boolean selectedstatus = false;
    private String qImage;
    private String aImage;
    private String bImage;
    private String cImage;
    private String dImage;
    public QuestionForQuiz(
            int QestionId,
            String Question,
            String VideoUrl,
            String FileType,
            String FileUrl,
            int Mark,
            String questionnum,
            JSONArray Answer,
            String qImage,
            String aImage,
            String bImage,
            String cImage,
            String dImage
    ) {
        this.QestionId = QestionId;
        this.Question = Question;
        this.VideoUrl = VideoUrl;
        this.FileType = FileType;
        this.FileUrl = FileUrl;
        this.Mark = Mark;
        this.Questionnum = questionnum;
        this.Answer = Answer;
        this.qImage = qImage;
        this.aImage = aImage;
        this.bImage = bImage;
        this.cImage = cImage;
        this.dImage = dImage;
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

    public void setFileType(String fileType) {
        FileType = fileType;
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

    public String getqImage() {
        return qImage;
    }

    public void setqImage(String qImage) {
        this.qImage = qImage;
    }

    public String getaImage() {
        return aImage;
    }

    public void setaImage(String aImage) {
        this.aImage = aImage;
    }

    public String getbImage() {
        return bImage;
    }

    public void setbImage(String bImage) {
        this.bImage = bImage;
    }

    public String getcImage() {
        return cImage;
    }

    public void setcImage(String cImage) {
        this.cImage = cImage;
    }

    public String getdImage() {
        return dImage;
    }

    public void setdImage(String dImage) {
        this.dImage = dImage;
    }
}













//package com.vs.schoolmessenger.model;
//
//import org.json.JSONArray;
//
//public class QuestionForQuiz {
//
//    public int QestionId;
//    public String Questionnum;
//    public String Question;
//    public String VideoUrl;
//    public String FileUrl;
//    public int Mark;
//    public String FileType;
//    public JSONArray Answer;
//    boolean selectedstatus=false;
//
//    public QuestionForQuiz(int QestionId, String Question, String VideoUrl, String FileType, String FileUrl, int Mark,String questionnum, JSONArray Answer){
//        this.QestionId=QestionId;
//        this.Question=Question;
//        this.VideoUrl=VideoUrl;
//        this.FileType=FileType;
//        this.FileUrl=FileUrl;
//        this.Mark=Mark;
//        this.Questionnum=questionnum;
//        this.Answer=Answer;
//    }
//
//    public int getQestionId() {
//        return QestionId;
//    }
//
//    public void setQestionId(int qestionId) {
//        QestionId = qestionId;
//    }
//
//    public String getQuestion() {
//        return Question;
//    }
//
//    public void setQuestion(String question) {
//        Question = question;
//    }
//
//    public String getVideoUrl() {
//        return VideoUrl;
//    }
//
//    public void setVideoUrl(String videoUrl) {
//        VideoUrl = videoUrl;
//    }
//
//    public String getFileType() {
//        return FileType;
//    }
//
//    public void setFileType(String FileType) {
//        FileType = FileType;
//    }
//
//    public String getFileUrl() {
//        return FileUrl;
//    }
//
//    public void setFileUrl(String fileUrl) {
//        FileUrl = fileUrl;
//    }
//
//    public int getMark() {
//        return Mark;
//    }
//
//    public void setMark(int mark) {
//        Mark = mark;
//    }
//
//    public JSONArray getAnswer() {
//        return Answer;
//    }
//
//    public void setAnswer(JSONArray answer) {
//        Answer = answer;
//    }
//
//    public boolean isSelectedstatus() {
//        return selectedstatus;
//    }
//
//    public void setSelectedstatus(boolean selectedstatus) {
//        this.selectedstatus = selectedstatus;
//    }
//
//    public String getQuestionnum() {
//        return Questionnum;
//    }
//
//    public void setQuestionnum(String questionnum) {
//        Questionnum = questionnum;
//    }
//
//
//}
