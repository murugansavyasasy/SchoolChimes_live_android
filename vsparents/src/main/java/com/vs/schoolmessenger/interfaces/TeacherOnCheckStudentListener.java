package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.TeacherStudentsModel;

/**
 * Created by voicesnap on 9/18/2016.
 */
public interface TeacherOnCheckStudentListener
{
    public void student_addClass(TeacherStudentsModel student);
    public void student_removeClass(TeacherStudentsModel student);
}
