package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.TeacherSubjectModel;

public interface SubjecstListener {
    public void student_addClass(TeacherSubjectModel student);
    public void student_removeClass(TeacherSubjectModel student);
}
