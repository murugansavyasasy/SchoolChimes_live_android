package com.vs.schoolmessenger.adapter.Ptm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;

public class TeacherTimingSlot extends RecyclerView.Adapter<TeacherTimingSlot.MyViewHolder> {
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.istiming_ptm, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView isFromTime,isToTime;
        CheckBox isSlotBookingCh;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            isFromTime=itemView.findViewById(R.id.lblFromTime);
            isToTime=itemView.findViewById(R.id.lblToTime);
            isSlotBookingCh=itemView.findViewById(R.id.chSlot);

        }
    }
}
