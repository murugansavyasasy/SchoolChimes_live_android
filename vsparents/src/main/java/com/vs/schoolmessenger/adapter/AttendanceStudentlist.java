package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.AttendanceListStudentData;

import java.util.List;


public class AttendanceStudentlist extends RecyclerView.Adapter<AttendanceStudentlist.MyViewHolder> {
    Context context;
    private List<AttendanceListStudentData> dateList;

    String isSectionName;

    @NonNull
    @Override
    public AttendanceStudentlist.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.attendanceabsend_studentlist, parent, false);
        return new AttendanceStudentlist.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AttendanceStudentlist.MyViewHolder holder, int position) {
        final AttendanceListStudentData isAbsentItem = dateList.get(position);

        holder.lblName.setText(isAbsentItem.getStudentName());
        holder.lblNumber.setText(isAbsentItem.getPrimaryMobile());
        holder.lblSection.setText(isSectionName);
        holder.lblAddmissionNumber.setText("Admission no : " + isAbsentItem.getAdmissionNo());
        if (!isAbsentItem.getPhotoPath().equals("")) {
            Picasso.get().load(isAbsentItem.getPhotoPath()).into(holder.imgProfile);
        } else {
            Picasso.get().load(R.drawable.empty_profile).into(holder.imgProfile);
        }
        holder.lblNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + isAbsentItem.getPrimaryMobile()));
                context.startActivity(intent);
            }
        });
    }

    public AttendanceStudentlist(Context context, List<AttendanceListStudentData> isSectionlist, String isSectionName) {
        this.context = context;
        this.dateList = isSectionlist;
        this.isSectionName = isSectionName;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName, lblSection, lblAddmissionNumber, lblNumber;
        ImageView imgProfile;

        public MyViewHolder(View view) {
            super(view);

            imgProfile = view.findViewById(R.id.imgProfile);
            lblName = view.findViewById(R.id.lblName);
            lblSection = view.findViewById(R.id.lblSection);
            lblAddmissionNumber = view.findViewById(R.id.lblAddmissionNumber);
            lblNumber = view.findViewById(R.id.lblNumber);

        }
    }
}
