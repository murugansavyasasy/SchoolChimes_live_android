package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.List;



/**
 * Created by voicesnap on 3/10/2017.
 */

public class TeacherSchoolsListAdapter extends RecyclerView.Adapter<TeacherSchoolsListAdapter.SchoolsListViewHolder> {

    private List<TeacherSchoolsModel> listContacts;
    private final Context context;
    private TeacherOnCheckSchoolsListener onCheckSchoolsListener;

    public static class SchoolsListViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbSelect;

        public SchoolsListViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.groupname);
            cbSelect = (CheckBox) v.findViewById(R.id.check_group);
        }
    }

    public TeacherSchoolsListAdapter(Context context, TeacherOnCheckSchoolsListener onCheckSchoolsListener,
                                     List<TeacherSchoolsModel> listContacts) {
        this.context = context;
        this.onCheckSchoolsListener = onCheckSchoolsListener;
        this.listContacts = listContacts;
    }

    @Override
    public SchoolsListViewHolder onCreateViewHolder(ViewGroup parent,
                                                    int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_group_list, parent, false);
        return new SchoolsListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final SchoolsListViewHolder holder, final int position) {
        final TeacherSchoolsModel school = listContacts.get(position);
        holder.tvName.setText(school.getStrSchoolName());

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                school.setSelectStatus(isChecked);
                if (isChecked) {
                    onCheckSchoolsListener.school_addSchool(school);
                } else {
                    onCheckSchoolsListener.school_removeSchool(school);
                }

            }
        });
        holder.cbSelect.setChecked(school.isSelectStatus());
    }

    @Override
    public int getItemCount() {
        return listContacts.size();
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}