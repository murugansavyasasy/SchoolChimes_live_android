package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.adapter.AttendanceStudentlist;
import com.vs.schoolmessenger.adapter.SchoolAttendanceReport;
import com.vs.schoolmessenger.adapter.SectionListAttendance;
import com.vs.schoolmessenger.model.AttendanceListStudentData;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;

public interface AttendanceStudentClickListener {
    void onItemClick(TeacherABS_Section item, SectionListAttendance.MyViewHolder holder, int position);
}
