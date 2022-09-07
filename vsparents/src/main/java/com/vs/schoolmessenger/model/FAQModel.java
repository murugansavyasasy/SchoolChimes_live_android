package com.vs.schoolmessenger.model;

public class FAQModel {
    private String Question, Answer;

    public FAQModel(String question, String answer) {
        this.Question = question;
        this.Answer = answer;

    }

    public FAQModel() {}

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String qus) {
        this.Question = qus;
    }

    public String getAnswer() {
        return Answer;
    }

    public void setAnswer(String ans) {
        this.Answer = ans;
    }


}
