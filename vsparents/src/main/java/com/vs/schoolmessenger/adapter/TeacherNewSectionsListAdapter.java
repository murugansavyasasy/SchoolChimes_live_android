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
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSectionListListener;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;

import java.util.List;


/**
 * Created by voicesnap on 3/10/2017.
 */

public class TeacherNewSectionsListAdapter extends RecyclerView.Adapter<TeacherNewSectionsListAdapter.SchoolsListViewHolder> {

    private List<TeacherSectionsListNEW> listContacts;
    private final Context context;
    private TeacherOnCheckSectionListListener onCheckSchoolsListener;

    public static class SchoolsListViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        CheckBox cbSelect;

        public SchoolsListViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.groupname);
            cbSelect = (CheckBox) v.findViewById(R.id.check_group);
        }
    }

    public TeacherNewSectionsListAdapter(Context context, TeacherOnCheckSectionListListener onCheckSchoolsListener,
                                         List<TeacherSectionsListNEW> listContacts) {
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
        final TeacherSectionsListNEW school = listContacts.get(position);
        holder.tvName.setText(school.getStrSectionName());
        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                school.setSelectStatus(isChecked);
                if (isChecked) {
                    onCheckSchoolsListener.section_addSection(school);
                } else {
                    onCheckSchoolsListener.section_removeSection(school);
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