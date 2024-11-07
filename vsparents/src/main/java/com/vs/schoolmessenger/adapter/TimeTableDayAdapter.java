package com.vs.schoolmessenger.adapter;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PasswordScreen;
import com.vs.schoolmessenger.interfaces.TimeTableDayListener;
import com.vs.schoolmessenger.model.DayClass;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.List;

/**
 * Created by voicesnap on 8/31/2016.
 */
public class TimeTableDayAdapter extends RecyclerView.Adapter<TimeTableDayAdapter.MyViewHolder> {

    private List<DayClass> dateList;
    Context context;
    private TimeTableDayListener listener;

    private int row_index=0;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_time_table_day, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener,position);

        final DayClass data = dateList.get(position);
        holder.lblDay.setText(data.getDay());

        holder.LayoutOverall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = position;
                notifyDataSetChanged();
                if (listener != null){
                    listener.onDayClick(position,data);
                }
            }
        });

        if (row_index == position) {
            holder.lblDay.setText(data.getDay());
            listener.onDayClick(position,data);
            holder.LayoutOverall.setBackgroundResource(R.drawable.bg_timetable_selected);
            holder.lblDay.setTextColor(Color.parseColor("#FFFEFE"));

        }
        else {
            holder.lblDay.setText(data.getDay());
            holder.LayoutOverall.setBackgroundResource(R.drawable.bg_timetable_unselected);
            holder.lblDay.setTextColor(Color.parseColor("#006064"));
        }



    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblDay;
        public ConstraintLayout LayoutOverall;
        public MyViewHolder(View view) {
            super(view);

            lblDay = (TextView) view.findViewById(R.id.lblDay);
            LayoutOverall = (ConstraintLayout) view.findViewById(R.id.LayoutOverall);
        }

        public void bind(final DayClass item, final TimeTableDayListener listener,int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onDayClick(position,item);
                }
            });
        }
    }

    public TimeTableDayAdapter(Context context, List<DayClass> dateList, TimeTableDayListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }


}