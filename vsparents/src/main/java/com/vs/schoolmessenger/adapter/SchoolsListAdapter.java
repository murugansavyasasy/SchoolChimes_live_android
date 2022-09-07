package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.StaffListCalls;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.List;

public class SchoolsListAdapter extends RecyclerView.Adapter<SchoolsListAdapter.MyViewHolder> {

    private List<TeacherSchoolsModel> lib_list;
    Context context;

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


        public MyViewHolder(View view) {
            super(view);

            schoolNmae = (TextView) view.findViewById(R.id.schoolNmae);
            rytSchoolList = (RelativeLayout) view.findViewById(R.id.rytSchoolList);


        }
    }

    public SchoolsListAdapter(List<TeacherSchoolsModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public SchoolsListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.school_list_adapter, parent, false);
        return new SchoolsListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final SchoolsListAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final TeacherSchoolsModel school = lib_list.get(position);

        holder.schoolNmae.setText(school.getStrSchoolName());

        holder.rytSchoolList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent stafflist = new Intent(context, StaffListCalls.class);
                stafflist.putExtra("schools", school);
                context.startActivity(stafflist);

            }
        });


    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


