package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.TeacherSectionModel;

/**
 * Created by voicesnap on 9/18/2016.
 */
public interface TeacherOnCheckStdSecListener
{
    public void stdSec_addClass(TeacherSectionModel stdSec);

    public void stdSec_removeClass(TeacherSectionModel stdSec);
}
