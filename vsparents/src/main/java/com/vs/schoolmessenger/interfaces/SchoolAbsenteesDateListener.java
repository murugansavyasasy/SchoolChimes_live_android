package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.adapter.SchoolAttendanceReport;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;

public interface SchoolAbsenteesDateListener {
    void onItemClick(TeacherAbsenteesDates item, SchoolAttendanceReport.MyViewHolder holder, int position);
}
