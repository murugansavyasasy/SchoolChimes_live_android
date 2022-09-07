package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.Subject;

public interface TeacherSelectListener {
    void click(Subject subject);

    void showToast(String message);
}
