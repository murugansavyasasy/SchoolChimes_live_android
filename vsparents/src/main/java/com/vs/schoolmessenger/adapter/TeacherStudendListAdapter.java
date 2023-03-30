package com.vs.schoolmessenger.adapter;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStudentListener;
import com.vs.schoolmessenger.model.TeacherStudentsModel;

import java.util.List;


/**
 * Created by devi on 5/19/2017.
 */

public class TeacherStudendListAdapter extends RecyclerView.Adapter<TeacherStudendListAdapter.MyViewHolder> {
    private List<TeacherStudentsModel> studentlist;
    Context context;
    private TeacherOnCheckStudentListener onCheckStudentListener;
    private int requestCode;

    public TeacherStudendListAdapter(Context context, TeacherOnCheckStudentListener onCheckStudentListener, List<TeacherStudentsModel> studentlist, int irequestCode) {
        this.context = context;
        this.onCheckStudentListener = onCheckStudentListener;
        this.studentlist = studentlist;
        this.requestCode = irequestCode;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_student_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Log.d("request_code", String.valueOf(requestCode));

        if (requestCode == PRINCIPAL_ATTENDANCE || requestCode == STAFF_ATTENDANCE) {
            holder.cbSelect.setVisibility(View.GONE);
            holder.lnrAbsent.setVisibility(View.GONE);
            holder.lnrPresent.setVisibility(View.VISIBLE);
        } else {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.lnrAbsent.setVisibility(View.GONE);
            holder.lnrPresent.setVisibility(View.GONE);
        }

        holder.bind(studentlist.get(position));
        final TeacherStudentsModel profile = studentlist.get(position);
        holder.tvstudentid.setText(profile.getAdmisionNo());
        holder.tvstudentname.setText(profile.getStudentName());

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setChecked(profile.isSelectStatus());
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setSelectStatus(isChecked);
                if (isChecked) {
                    onCheckStudentListener.student_addClass(profile);
                } else {
                    onCheckStudentListener.student_removeClass(profile);
                }
            }
        });

        if (requestCode == PRINCIPAL_ATTENDANCE || requestCode == STAFF_ATTENDANCE) {
            if (profile.isSelectStatus()) {
                holder.lnrPresent.setVisibility(View.GONE);
                holder.lnrAbsent.setVisibility(View.VISIBLE);
            } else {
                holder.lnrPresent.setVisibility(View.VISIBLE);
                holder.lnrAbsent.setVisibility(View.GONE);
            }
        }

        holder.lnrPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setSelectStatus(true);
                onCheckStudentListener.student_addClass(profile);
                holder.lnrPresent.setVisibility(View.GONE);
                holder.lnrAbsent.setVisibility(View.VISIBLE);
            }
        });

        holder.lnrAbsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profile.setSelectStatus(false);
                onCheckStudentListener.student_removeClass(profile);
                holder.lnrPresent.setVisibility(View.VISIBLE);
                holder.lnrAbsent.setVisibility(View.GONE);

            }
        });


    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return studentlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvstudentid, tvstudentname;
        CheckBox cbSelect;
        LinearLayout lnrPresent, lnrAbsent;

        public MyViewHolder(View view) {
            super(view);

            tvstudentid = (TextView) view.findViewById(R.id.Student_id);
            tvstudentname = (TextView) view.findViewById(R.id.Student_name);

            cbSelect = (CheckBox) view.findViewById(R.id.Student_cbSelect);
            lnrPresent = (LinearLayout) view.findViewById(R.id.lnrPresent);
            lnrAbsent = (LinearLayout) view.findViewById(R.id.lnrAbsent);


        }

        public void bind(TeacherStudentsModel studentsModel) {
        }
    }
}
