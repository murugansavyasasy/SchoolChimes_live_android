package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.TeacherSchoolsModel;

/**
 * Created by voicesnap on 9/18/2016.
 */
public interface TeacherOnCheckSchoolsListener
{
    public void school_addSchool(TeacherSchoolsModel school);
    public void school_removeSchool(TeacherSchoolsModel school);
}
