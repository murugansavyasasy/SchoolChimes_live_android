package com.vs.schoolmessenger.model;

import java.util.ArrayList;

/**
 * Created by voicesnap on 6/10/2017.
 */

public class TeacherStandardSectionsListModel {
    String strStandardName;
    String strStandardCode;
    ArrayList<TeacherSectionsListModel> listSections;
    ArrayList<TeacherSubjectModel> listSubjects;

    public ArrayList<TeacherSubjectModel> getListSubjects() {
        return listSubjects;
    }

    public void setListSubjects(ArrayList<TeacherSubjectModel> listSubjects) {
        this.listSubjects = listSubjects;
    }

    ArrayList<TeacherSectionsListNEW> listSectionsNew;

    public ArrayList<TeacherSectionsListNEW> getListSectionsNew() {
        return listSectionsNew;
    }

    public void setListSectionsNew(ArrayList<TeacherSectionsListNEW> listSectionsNew) {
        this.listSectionsNew = listSectionsNew;
    }

    public TeacherStandardSectionsListModel() {
    }

    public TeacherStandardSectionsListModel(String strStandardName, String strStandardCode) {
        this.strStandardName = strStandardName;
        this.strStandardCode = strStandardCode;
    }

    public String getStrStandardName() {
        return strStandardName;
    }

    public void setStrStandardName(String strStandardName) {
        this.strStandardName = strStandardName;
    }

    public String getStrStandardCode() {
        return strStandardCode;
    }

    public void setStrStandardCode(String strStandardCode) {
        this.strStandardCode = strStandardCode;
    }

    public ArrayList<TeacherSectionsListModel> getListSections() {
        return listSections;
    }

    public void setListSections(ArrayList<TeacherSectionsListModel> listSections) {
        this.listSections = listSections;
    }
}
