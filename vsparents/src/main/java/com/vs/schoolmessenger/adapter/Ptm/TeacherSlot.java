package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DailyFeeData;

import java.util.ArrayList;

public class TeacherSlot extends RecyclerView.Adapter<TeacherSlot.MyViewHolder> {

    private Context context;
    private TeacherTimingSlot isTeacherTimingSlot;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    public TeacherSlot(Context context, ArrayList<DailyFeeData> dateList) {
        this.context = context;
        //  this.isDailyFeeData = dateList;
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        //   isTeacherTimingSlot = new TeacherTimingSlot(context, DailyFeeDataDailyFeeData.getData());
        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.isRecyclerViewTiming.setHasFixedSize(true);
        holder.isRecyclerViewTiming.setLayoutManager(mLayoutManagerS);
        holder.isRecyclerViewTiming.setItemAnimator(new DefaultItemAnimator());
        holder.isRecyclerViewTiming.setAdapter(isTeacherTimingSlot);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lblSubjectName, lblTeacherName;
        RecyclerView isRecyclerViewTiming;
        ImageView isShowTiming;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lblSubjectName = itemView.findViewById(R.id.lblSubjectName);
            lblTeacherName = itemView.findViewById(R.id.lblTeacherName);
            isRecyclerViewTiming = itemView.findViewById(R.id.recyclerViewViewTime);
            isShowTiming = itemView.findViewById(R.id.imgDown);
        }
    }
}
