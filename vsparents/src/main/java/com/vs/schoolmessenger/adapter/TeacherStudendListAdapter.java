package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

    public TeacherStudendListAdapter(Context context, TeacherOnCheckStudentListener onCheckStudentListener, List<TeacherStudentsModel> studentlist) {
        this.context = context;
        this.onCheckStudentListener = onCheckStudentListener;
        this.studentlist = studentlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_student_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

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

        public MyViewHolder(View view) {
            super(view);

            tvstudentid = (TextView) view.findViewById(R.id.Student_id);
            tvstudentname = (TextView) view.findViewById(R.id.Student_name);

            cbSelect = (CheckBox) view.findViewById(R.id.Student_cbSelect);
        }

        public void bind(TeacherStudentsModel studentsModel) {
        }
    }
}
