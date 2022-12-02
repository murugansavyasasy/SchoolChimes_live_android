package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.TeacherSectionsListNEW;

/**
 * Created by voicesnap on 11/30/2017.
 */

public interface TeacherOnCheckSectionListListener {
    public void section_addSection(TeacherSectionsListNEW sectionsListNEW);
    public void section_removeSection(TeacherSectionsListNEW sectionsListNEW);
}
