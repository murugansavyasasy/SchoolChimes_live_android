package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.StaffList;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

public interface SchoolsListener {

    public void schools_add(TeacherSchoolsModel student);
    public void schools_remove(TeacherSchoolsModel student);
}
