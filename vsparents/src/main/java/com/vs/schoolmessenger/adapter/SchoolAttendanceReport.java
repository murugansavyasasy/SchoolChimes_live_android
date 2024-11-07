package com.vs.schoolmessenger.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SchoolAbsenteesReport;
import com.vs.schoolmessenger.interfaces.SchoolAbsenteesDateListener;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;
import com.vs.schoolmessenger.util.Util_Common;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SchoolAttendanceReport extends RecyclerView.Adapter<SchoolAttendanceReport.MyViewHolder> {
    private final List<TeacherAbsenteesDates> dateList;
    Context context;
    //  private final TeacherAbsenteesDateListener listener;
    private final SchoolAbsenteesDateListener listener;
    StandardAdapter standardAdapter;

    @NonNull
    @Override
    public SchoolAttendanceReport.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendance_schoolreport, parent, false);
        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblMonthName, lblAbsentCount, lblNameOfDate, lblDate;
        private RecyclerView rcyStandard;
        public ConstraintLayout conDate;

        public RelativeLayout rlyAttendance;

        public MyViewHolder(View view) {
            super(view);
            lblMonthName = view.findViewById(R.id.lblMonthname);
            lblAbsentCount = view.findViewById(R.id.lblAbsentCount);
            lblNameOfDate = view.findViewById(R.id.lblNameOfDate);
            lblDate = view.findViewById(R.id.txtDate);
            rlyAttendance = view.findViewById(R.id.rlyAttendance);
            conDate = view.findViewById(R.id.conDate);
        }

        public void bind(final TeacherAbsenteesDates item, final SchoolAbsenteesDateListener listener, MyViewHolder holder, int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, holder, position);
                }
            });
        }
    }

    public SchoolAttendanceReport(Context context, List<TeacherAbsenteesDates> dateList, SchoolAbsenteesDateListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull SchoolAttendanceReport.MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener, holder, position);
        final TeacherAbsenteesDates isAbsentItem = dateList.get(position);

        String monthName = getMonthNameFromDate(isAbsentItem.getDate());
        String dayOfWeek = getDayOfWeekFromDate(isAbsentItem.getDate());
        int[] monthAndDay = getMonthAndDayFromDate(isAbsentItem.getDate());
        int day = monthAndDay[1];
        Log.d("day", String.valueOf(day));
        holder.lblDate.setText(String.valueOf(day));
        holder.lblAbsentCount.setText(isAbsentItem.getCount());
        holder.lblNameOfDate.setText(dayOfWeek);
        holder.lblMonthName.setText(monthName);

        if (position == Util_Common.isPosition) {
            Util_Common.isDate = isAbsentItem.getAbsentdateonly();
            holder.conDate.setBackgroundColor(
                    ContextCompat.getColor(holder.conDate.getContext(), R.color.clr_sandle));
            holder.lblDate.setTextColor(Color.WHITE);
            holder.lblNameOfDate.setTextColor(Color.WHITE);
        } else {
            holder.conDate.setBackgroundColor(
                    ContextCompat.getColor(holder.conDate.getContext(), R.color.clr_white));
            holder.lblDate.setTextColor(Color.BLACK);
            holder.lblNameOfDate.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    private String getMonthNameFromDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        String[] monthNames = new DateFormatSymbols().getMonths();
        return monthNames[month];
    }

    private String getDayOfWeekFromDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // Return empty string if parsing fails
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String[] dayOfWeekNames = new DateFormatSymbols().getWeekdays();
        return dayOfWeekNames[dayOfWeek];
    }

    private int[] getMonthAndDayFromDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date;
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new int[]{-1, -1};
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new int[]{month, day};
    }
}
