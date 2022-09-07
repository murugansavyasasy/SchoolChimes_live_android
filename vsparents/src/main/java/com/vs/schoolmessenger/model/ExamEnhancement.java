package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class ExamEnhancement implements Serializable {
//    {
//        "Status": 1,
//            "Message": "Success!",
//            "data": [
//        {
//            "QuizId": 27,
//                "Title": "calculation test",
//                "Description": "engdesc",
//                "MarkPerQuestion": 2,
//                "Subject": "ENGLISH",
//                "Date": "11/06/2021",
//                "Time": "17:07:16",
//                "SubmittedOn": "11/06/2021",
//                "Issubmitted": "1",
//                "isAppRead": "1",
//                "SentBy": "vssales ",
//                "SortOrder": "2021-06-11T11:37:16.000Z",
//                "ExamStartTime": "18/06/2021 11:00:00",
//                "ExamEndTime": "18/06/2021 11:00:00",
//                "TimeForQuestionReading": "12/06/2021 15:40:35",
//                "ExamDate": "18/06/2021",
//                "TotalNumberOfQuestions": "20",
//                "RightAnswer": "19",
//                "WrongAnswer": "18",
//                "totalMark": "4"
//        }
//    ]
//    }
    public String Title;
    public String Description;
    public String Subject;

    public String SubmittedOn;
    public String Issubmitted;
    public String isAppRead;
    public String SentBy;
    public String ExamStartTime;
    public String ExamEndTime;
    public String TimeForQuestionReading;
    public String TotalNumberOfQuestions;
    public String RightAnswer;
    public String ExamDate;
    public String WrongAnswer;
    public String totalMark;
    public String createdOn;
    public int QuizId;
    public int detailId;
    public String isShow;


    public ExamEnhancement(String Title, String Description,  String Subject,
                            String SubmittedOn,String Issubmitted, String isAppRead,
                           String SentBy,String ExamStartTime,String ExamEndTime,String TimeForQuestionReading,
                           String TotalNumberOfQuestions,String RightAnswer,String ExamDate,String WrongAnswer,
                           String totalMark,String createdOn,int QuizId,int detailId,String is_Show) {
        this.Title = Title;
        this.Description = Description;
        this.Subject = Subject;

        this.SubmittedOn = SubmittedOn;
        this.Issubmitted = Issubmitted;
        this.isAppRead = isAppRead;
        this.SentBy = SentBy;
        this.ExamStartTime = ExamStartTime;
        this.ExamEndTime = ExamEndTime;
        this.TimeForQuestionReading = TimeForQuestionReading;
        this.TotalNumberOfQuestions = TotalNumberOfQuestions;
        this.RightAnswer = RightAnswer;
        this.ExamDate = ExamDate;
        this.WrongAnswer = WrongAnswer;
        this.totalMark = totalMark;
        this.createdOn = createdOn;
        this.QuizId = QuizId;
        this.detailId = detailId;
        this.isShow = is_Show;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }


    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }



    public String getSubmittedOn() {
        return SubmittedOn;
    }

    public void setSubmittedOn(String submittedOn) {
        SubmittedOn = submittedOn;
    }

    public String getIssubmitted() {
        return Issubmitted;
    }

    public void setIssubmitted(String issubmitted) {
        Issubmitted = issubmitted;
    }

    public String getIsAppRead() {
        return isAppRead;
    }

    public void setIsAppRead(String isAppRead) {
        this.isAppRead = isAppRead;
    }

    public String getSentBy() {
        return SentBy;
    }

    public void setSentBy(String sentBy) {
        SentBy = sentBy;
    }


    public String getExamStartTime() {
        return ExamStartTime;
    }

    public void setExamStartTime(String examStartTime) {
        ExamStartTime = examStartTime;
    }

    public String getExamEndTime() {
        return ExamEndTime;
    }

    public void setExamEndTime(String examEndTime) {
        ExamEndTime = examEndTime;
    }

    public String getTimeForQuestionReading() {
        return TimeForQuestionReading;
    }

    public void setTimeForQuestionReading(String timeForQuestionReading) {
        TimeForQuestionReading = timeForQuestionReading;
    }

    public String getTotalNumberOfQuestions() {
        return TotalNumberOfQuestions;
    }

    public void setTotalNumberOfQuestions(String totalNumberOfQuestions) {
        TotalNumberOfQuestions = totalNumberOfQuestions;
    }

    public String getRightAnswer() {
        return RightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        RightAnswer = rightAnswer;
    }

    public String getExamDate() {
        return ExamDate;
    }

    public void setExamDate(String examDate) {
        ExamDate = examDate;
    }

    public String getWrongAnswer() {
        return WrongAnswer;
    }

    public void setWrongAnswer(String wrongAnswer) {
        WrongAnswer = wrongAnswer;
    }

    public String getTotalMark() {
        return totalMark;
    }

    public void setTotalMark(String totalMark) {
        this.totalMark = totalMark;
    }

    public int getQuizId() {
        return QuizId;
    }

    public void setQuizId(int quizId) {
        QuizId = quizId;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public String getIsShow() {
        return isShow;
    }

    public void setIsShow(String show) {
        this.isShow = show;
    }
}


