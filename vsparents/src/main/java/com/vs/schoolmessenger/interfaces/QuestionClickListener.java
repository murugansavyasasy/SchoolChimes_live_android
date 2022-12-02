package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.QuestionForQuiz;

public interface QuestionClickListener {
    public void addclass(QuestionForQuiz menu, int position);
    public void removeclass(QuestionForQuiz menu);
}
