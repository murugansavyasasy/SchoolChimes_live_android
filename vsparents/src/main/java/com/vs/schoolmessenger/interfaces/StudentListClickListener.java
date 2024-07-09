package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.adapter.SchoolAttendanceReport;
import com.vs.schoolmessenger.model.AttendanceStudentList;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;

public interface StudentListClickListener {
    void onItemClick(AttendanceStudentList item);

}
