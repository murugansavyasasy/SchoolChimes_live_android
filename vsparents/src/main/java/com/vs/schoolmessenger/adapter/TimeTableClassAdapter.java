package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TimeTableClass;

import java.util.List;

public class TimeTableClassAdapter extends RecyclerView.Adapter<TimeTableClassAdapter.MyViewHolder> {

    private List<TimeTableClass> classList;
    Context context;

    @Override
    public TimeTableClassAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.timetable_class_list, parent, false);

        return new TimeTableClassAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final TimeTableClassAdapter.MyViewHolder holder, int position) {

        final TimeTableClass data = classList.get(position);

        holder.lblSubjectName.setText(data.getSubjectName());
        holder.lblclassDuration.setText(data.getDuration()+" - "+"mins");
        holder.lblclassType.setText(data.getName());
        holder.lblstarttime.setText(data.getFromTime());
        holder.lblendtime.setText(data.getToTime());



        if(data.getHourType().equals("2")){

            if(!data.getStaffName().equals("")) {
                holder.lblSubjectName.setVisibility(View.VISIBLE);
                holder.lblSubjectName.setText(data.getStaffName());
            }
            else {
                holder.lblSubjectName.setVisibility(View.GONE);
            }

        }else if(data.getHourType().equals("1")){

            if(!data.getSubjectName().equals("")) {
                holder.lblSubjectName.setVisibility(View.VISIBLE);
                holder.lblSubjectName.setText(data.getSubjectName());
            }
            else {
                holder.lblSubjectName.setVisibility(View.GONE);

            }

        }
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblSubjectName, lblclassDuration, lblclassType, lblstarttime, lblendtime;


        public MyViewHolder(View view) {
            super(view);

            lblSubjectName = (TextView) view.findViewById(R.id.lbltimeSubjectName);
            lblclassDuration = (TextView) view.findViewById(R.id.lblclassDuration);
            lblclassType = (TextView) view.findViewById(R.id.lblclassType);
            lblstarttime = (TextView) view.findViewById(R.id.lblstarttime);
            lblendtime = (TextView) view.findViewById(R.id.lblendtime);


        }
    }

    public TimeTableClassAdapter(Context context, List<TimeTableClass> classList) {
        this.context = context;
        this.classList = classList;
    }

}