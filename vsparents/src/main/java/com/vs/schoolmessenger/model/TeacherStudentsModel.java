package com.vs.schoolmessenger.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by devi on 5/19/2017.
 */

public class TeacherStudentsModel implements Serializable {
    private String studentID, studentName, admisionNo,rollNo;
    boolean selectStatus;

    public TeacherStudentsModel(){}

    public TeacherStudentsModel(String studentID, String studentName, String admisionNo,String rollNo, boolean selectStatus) {
        this.studentID = studentID;
        this.studentName = studentName;
        this.admisionNo = admisionNo;
        this.rollNo = rollNo;
        this.selectStatus = selectStatus;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getAdmisionNo() {
        return admisionNo;
    }

    public void setAdmisionNo(String admisionNo) {
        this.admisionNo = admisionNo;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String admisionNo) {
        this.rollNo = admisionNo;
    }

    public boolean isSelectStatus() {
        return selectStatus;
    }

    public void setSelectStatus(boolean selectStatus) {
        this.selectStatus = selectStatus;
    }


    public static Comparator<TeacherStudentsModel> sortByAscRollNo = new Comparator<TeacherStudentsModel>() {
        @Override
        public int compare(TeacherStudentsModel o1, TeacherStudentsModel o2) {
            // Handle empty RollNO by placing it at the end of the list
            if (o1.rollNo.isEmpty() && o2.rollNo.isEmpty()) {
                return 0;
            }
            if (o1.rollNo.isEmpty()) {
                return 1; // o1 goes after o2
            }
            if (o2.rollNo.isEmpty()) {
                return -1; // o1 goes before o2
            }
            return o1.rollNo.compareTo(o2.rollNo); // Default string comparison
        }
    };

    // Descending order comparator
    public static Comparator<TeacherStudentsModel> sortByDescRollNo = new Comparator<TeacherStudentsModel>() {
        @Override
        public int compare(TeacherStudentsModel o1, TeacherStudentsModel o2) {
            // Handle empty RollNO by placing it at the end of the list
            if (o1.rollNo.isEmpty() && o2.rollNo.isEmpty()) {
                return 0;
            }
            if (o1.rollNo.isEmpty()) {
                return 1; // o1 goes after o2
            }
            if (o2.rollNo.isEmpty()) {
                return -1; // o1 goes before o2
            }
            return o2.rollNo.compareTo(o1.rollNo); // Default string comparison
        }
    };


    public static Comparator<TeacherStudentsModel> sortByNameAZ = new Comparator<TeacherStudentsModel>() {
        @Override
        public int compare(TeacherStudentsModel o1, TeacherStudentsModel o2) {
            return o1.studentName.compareTo(o2.studentName);
        }
    };

    public static Comparator<TeacherStudentsModel> sortByNameZA = new Comparator<TeacherStudentsModel>() {
        @Override
        public int compare(TeacherStudentsModel o1, TeacherStudentsModel o2) {
            return o2.studentName.compareTo(o1.studentName);
        }
    };
}
