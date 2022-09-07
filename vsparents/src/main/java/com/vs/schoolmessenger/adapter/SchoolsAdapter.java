package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.SchoolsListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.List;

public class SchoolsAdapter extends RecyclerView.Adapter<SchoolsAdapter.MyViewHolder> {

    private List<TeacherSchoolsModel> lib_list;
    Context context;

    private final SchoolsListener onContactsListener;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView schoolNmae;
        public RelativeLayout rytSchoolList;
        public ImageView princiSchool_ivRight;
        public CheckBox SelectedCheckBox;


        public MyViewHolder(View view) {
            super(view);

            schoolNmae = (TextView) view.findViewById(R.id.schoolNmae);
            rytSchoolList = (RelativeLayout) view.findViewById(R.id.rytSchoolList);
            princiSchool_ivRight = (ImageView) view.findViewById(R.id.princiSchool_ivRight);
            SelectedCheckBox = (CheckBox) view.findViewById(R.id.SelectedCheckBox);


        }
    }

    public SchoolsAdapter(List<TeacherSchoolsModel> lib_list,SchoolsListener listener, Context context) {
        this.lib_list = lib_list;
        this.context = context;
        this.onContactsListener = listener;
    }

    @Override
    public SchoolsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_list_adapter, parent, false);
        return new SchoolsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SchoolsAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final TeacherSchoolsModel school = lib_list.get(position);

        holder.schoolNmae.setText(school.getStrSchoolName());
        holder.princiSchool_ivRight.setVisibility(View.GONE);
        holder.SelectedCheckBox.setVisibility(View.VISIBLE);


        holder.SelectedCheckBox.setOnCheckedChangeListener(null);
        holder.SelectedCheckBox.setChecked(school.isSelectStatus());
        holder.SelectedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                school.setSelectStatus(isChecked);

                if (isChecked) {

                    onContactsListener.schools_add(school);

                    } else {

                    onContactsListener.schools_remove(school);


                }



            }
        });

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


