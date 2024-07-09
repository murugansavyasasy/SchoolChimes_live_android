package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.standardListener;
import com.vs.schoolmessenger.model.TeacherABS_Standard;

import java.util.List;

public class StandardAdapter extends RecyclerView.Adapter<StandardAdapter.MyViewHolder> {

    private List<TeacherABS_Standard> dateList;
    Context context;
    String isDate;
    private final standardListener listener;

    @NonNull
    @Override
    public StandardAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.standard_layout, parent, false);
        return new StandardAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull StandardAdapter.MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);
        final TeacherABS_Standard isAbsentItem = dateList.get(position);
        holder.lblStandard.setText(isAbsentItem.getStandard());
        holder.lblDate.setText(isDate);
        holder.lblAbsentCount.setText(isAbsentItem.getCount());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblStandard, lblDate, lblAbsentCount;

        private RelativeLayout rlyAttendance;

        public MyViewHolder(View view) {
            super(view);

            lblStandard = view.findViewById(R.id.lblStandard);
            lblAbsentCount = view.findViewById(R.id.lblAbsentCount);
            lblDate = view.findViewById(R.id.lblDate);
        }

        public void bind(final TeacherABS_Standard item, final standardListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public StandardAdapter(Context context, String date, List<TeacherABS_Standard> dateList, standardListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
        this.isDate = date;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }
}
