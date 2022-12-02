package com.vs.schoolmessenger.interfaces;

import com.vs.schoolmessenger.model.StaffList;

public interface StaffListListener {
    public void student_addClass(StaffList student);
    public void student_removeClass(StaffList student);
}
