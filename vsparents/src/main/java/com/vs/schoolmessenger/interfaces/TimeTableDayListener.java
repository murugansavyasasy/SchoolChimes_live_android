package com.vs.schoolmessenger.interfaces;


import com.vs.schoolmessenger.model.DayClass;

public interface TimeTableDayListener {

    void onDayClick(Integer position, DayClass item);

}