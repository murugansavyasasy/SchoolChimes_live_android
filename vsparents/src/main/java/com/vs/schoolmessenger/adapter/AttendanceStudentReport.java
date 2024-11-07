package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.StudentAttendanceReport;
import com.vs.schoolmessenger.model.TeacherStudentsModel;

import java.util.ArrayList;
import java.util.List;


public class AttendanceStudentReport extends RecyclerView.Adapter<com.vs.schoolmessenger.adapter.AttendanceStudentReport.MyViewHolder> {
    public List<StudentAttendanceReport.AttendanceReport> isStudentAttendanceReport = new ArrayList<StudentAttendanceReport.AttendanceReport>();

    Context context;

    public AttendanceStudentReport(List<StudentAttendanceReport.AttendanceReport> isStudentAttendanceReport, Context context) {
        this.context = context;
        this.isStudentAttendanceReport = isStudentAttendanceReport;

    }

    @Override
    public com.vs.schoolmessenger.adapter.AttendanceStudentReport.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_attendance_report, parent, false);

        return new com.vs.schoolmessenger.adapter.AttendanceStudentReport.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final com.vs.schoolmessenger.adapter.AttendanceStudentReport.MyViewHolder holder, int position) {


        final StudentAttendanceReport.AttendanceReport isData = isStudentAttendanceReport.get(position);
        holder.tvstudentid.setText(isData.getAdmission_no());
        holder.tvstudentname.setText(isData.getStudent_name());

        if (isData.getAtt_status().equals("P")) {
            holder.lnrAbsent.setVisibility(View.GONE);
            holder.lnrPresent.setVisibility(View.VISIBLE);
            holder.lnrNotTaken.setVisibility(View.GONE);
        } else if (isData.getAtt_status().equals("A")) {
            holder.lnrAbsent.setVisibility(View.VISIBLE);
            holder.lnrPresent.setVisibility(View.GONE);
            holder.lnrNotTaken.setVisibility(View.GONE);
        } else if (isData.getAtt_status().equals("Not taken")) {
            holder.lnrNotTaken.setVisibility(View.VISIBLE);
            holder.lblNotTaken.setText(isData.getAtt_status());
            holder.lnrAbsent.setVisibility(View.GONE);
            holder.lnrPresent.setVisibility(View.GONE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return isStudentAttendanceReport.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvstudentid, tvstudentname,lblNotTaken;
        LinearLayout lnrAbsent, lnrPresent,lnrNotTaken;

        public MyViewHolder(View view) {
            super(view);

            tvstudentid = (TextView) view.findViewById(R.id.Student_id);
            lblNotTaken = (TextView) view.findViewById(R.id.lblNotTaken);
            tvstudentname = (TextView) view.findViewById(R.id.Student_name);
            lnrNotTaken = (LinearLayout) view.findViewById(R.id.lnrNotTaken);
            lnrAbsent = (LinearLayout) view.findViewById(R.id.lnrAbsent);
            lnrPresent = (LinearLayout) view.findViewById(R.id.lnrPresent);

        }

        public void bind(TeacherStudentsModel studentsModel) {
        }
    }
}

