package com.vs.schoolmessenger.model;

import java.io.Serializable;

public class QuizSubmissions implements Serializable {

//  "id": 18,
//          "quizId": 27,
//          "aOption": "1~testaaaZ",
//          "bOption": "2~testbbbZ",
//          "cOption": "3~testcccZ",
//          "dOption": "4~testdddZ",
//          "mark": null,
//          "answer": "1",
//          "studentAnswer": "1",
//          "correctAnswer": "testaaaZ",
//          "givenFileType": 0


    public int id;
    public int quizId;
    public String question;
    public String aOption;
    public String bOption;
    public String cOption;

    public String dOption;
    public String mark;
    public String answer;
    public String studentAnswer;
    public String correctAnswer;


    public QuizSubmissions( int id, int quizId,String question,String aOption,String bOption,String cOption,
                            String dOption,String mark,String answer,String studentAnswer,String correctAnswer) {
        this.id = id;
        this.quizId = quizId;
        this.question = question;
        this.aOption = aOption;
        this.bOption = bOption;
        this.cOption = cOption;
        this.dOption = dOption;
        this.mark = mark;
        this.answer = answer;
        this.studentAnswer = studentAnswer;
        this.correctAnswer = correctAnswer;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getaOption() {
        return aOption;
    }

    public void setaOption(String aOption) {
        this.aOption = aOption;
    }

    public String getbOption() {
        return bOption;
    }

    public void setbOption(String bOption) {
        this.bOption = bOption;
    }

    public String getcOption() {
        return cOption;
    }

    public void setcOption(String cOption) {
        this.cOption = cOption;
    }

    public String getdOption() {
        return dOption;
    }

    public void setdOption(String dOption) {
        this.dOption = dOption;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getStudentAnswer() {
        return studentAnswer;
    }

    public void setStudentAnswer(String studentAnswer) {
        this.studentAnswer = studentAnswer;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(String correctAnswer) {
        this.correctAnswer = correctAnswer;
    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}


