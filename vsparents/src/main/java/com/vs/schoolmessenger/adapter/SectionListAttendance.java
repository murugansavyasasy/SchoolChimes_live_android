package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.AttendanceStudentClickListener;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.List;

public class SectionListAttendance extends RecyclerView.Adapter<SectionListAttendance.MyViewHolder> {

    Context context;
    private List<TeacherABS_Section> dateList;

    AttendanceStudentClickListener listener;

    @NonNull
    @Override
    public SectionListAttendance.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sectionlist_attendance, parent, false);
        return new SectionListAttendance.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SectionListAttendance.MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener, holder, position);
        final TeacherABS_Section isAbsentItem = dateList.get(position);
        holder.lblStandard.setText(Util_Common.isAttendanceClass + "-" + isAbsentItem.getSection());
        holder.lblAbsentCount.setText(isAbsentItem.getCount());

        if (position == Util_Common.isPositionSection) {
            holder.rlySection.setBackground(context.getResources().getDrawable(R.drawable.bg_card_attendance));
            holder.lblStandard.setTextColor(Color.WHITE);
        } else {
            holder.rlySection.setBackground(context.getResources().getDrawable(R.drawable.bg_card));
            holder.lblStandard.setTextColor(Color.BLACK);
        }
    }

    public SectionListAttendance(Context context, List<TeacherABS_Section> isSectionlist, AttendanceStudentClickListener listener) {
        this.context = context;
        this.dateList = isSectionlist;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblStandard, lblAbsentCount;

        private RelativeLayout rlyAttendance, rlySection;

        public MyViewHolder(View view) {
            super(view);

            lblStandard = view.findViewById(R.id.lblStandard);
            lblAbsentCount = view.findViewById(R.id.lblAbsentCount);
            rlyAttendance = view.findViewById(R.id.rlyAttendance);
            rlySection = view.findViewById(R.id.rlySection);
        }

        public void bind(final TeacherABS_Section item, final AttendanceStudentClickListener listener, SectionListAttendance.MyViewHolder holder, int position) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item, holder, position);
                }
            });
        }
    }
}
