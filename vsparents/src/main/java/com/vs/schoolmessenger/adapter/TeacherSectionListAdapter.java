package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStdSecListener;
import com.vs.schoolmessenger.interfaces.TeacherOnSelectedStudentsListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;

import java.util.List;



/**
 * Created by devi on 5/19/2017.
 */

public class TeacherSectionListAdapter extends RecyclerView.Adapter<TeacherSectionListAdapter.MyViewHolder> {

    private List<TeacherSectionModel> sectionlist;
    Context context;
    private TeacherOnCheckStdSecListener onCheckStdSecListener;
    private TeacherOnSelectedStudentsListener onSelectedStudentsListener;

    public TeacherSectionListAdapter(Context context, TeacherOnCheckStdSecListener onCheckStdSecListener, TeacherOnSelectedStudentsListener onSelectedStudentsListener, List<TeacherSectionModel> sectionlist) {
        this.context = context;
        this.onCheckStdSecListener = onCheckStdSecListener;
        this.onSelectedStudentsListener = onSelectedStudentsListener;
        this.sectionlist = sectionlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_list_section, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.bind(sectionlist.get(position));

        final TeacherSectionModel profile = sectionlist.get(position);
        holder.tvsection.setText(profile.getStandard() + "-" + profile.getSection());
        holder.tvsubject.setText(profile.getSubject());
        holder.tvStudentcount.setText(profile.getSelectedStudentsCount());
        holder.tvStudentTotcount.setText(profile.getTotStudents());

        holder.cbSelect.setOnCheckedChangeListener(null);
        holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                profile.setSelectStatus(isChecked);
                if (isChecked) {
                    holder.btnEdit.setEnabled(true);
                    onCheckStdSecListener.stdSec_addClass(profile);
                }
                else
                {
                    holder.btnEdit.setEnabled(false);
                    onCheckStdSecListener.stdSec_removeClass(profile);
                }

            }
        });
        holder.cbSelect.setChecked(profile.isSelectStatus());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onSelectedStudentsListener.stdSec_selectedClass(profile);
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return sectionlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvsection, tvsubject, tvStudentcount, tvStudentTotcount;
        Button btnEdit;
        CheckBox cbSelect;

        public MyViewHolder(View view) {
            super(view);
            tvsection = (TextView) view.findViewById(R.id.liSection_tvSection);
            tvsubject = (TextView) view.findViewById(R.id.liSection_tvSubject);
            tvStudentcount = (TextView) view.findViewById(R.id.liSection_tvSeleStudCount);
            tvStudentTotcount = (TextView) view.findViewById(R.id.liSection_tvTotStudCount);
            btnEdit = (Button) view.findViewById(R.id.liSection_btnEdit);
            btnEdit.setEnabled(false);
            cbSelect = (CheckBox) view.findViewById(R.id.liSection_cbSelect);
        }

        public void bind(TeacherSectionModel sectionclass) {
        }
    }
}
