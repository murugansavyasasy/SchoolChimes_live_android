package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.TeacherClassGroupModel;

/**
 * Created by voicesnap on 9/18/2016.
 */
public interface TeacherOnClassGroupItemCheckListener
{
    public void classGropItem_addClass(String type, TeacherClassGroupModel classGroup); //0-class, 1-group

    public void classGropItem_removeClass(String type, TeacherClassGroupModel classGroup);
}
