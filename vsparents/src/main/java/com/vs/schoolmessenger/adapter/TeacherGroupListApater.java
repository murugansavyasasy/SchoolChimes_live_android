package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherGroupclass;

import java.util.List;



/**
 * Created by devi on 5/20/2017.
 */

public class TeacherGroupListApater extends RecyclerView.Adapter<TeacherGroupListApater.MyViewHolder>  {

    private List<TeacherGroupclass> grouplist;
    Context context;
    public TeacherGroupListApater(Context context, List<TeacherGroupclass> grouplist) {
        this.context = context;
        this.grouplist = grouplist;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_group_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(grouplist.get(position));

        final TeacherGroupclass group = grouplist.get(position);
        holder.tvgroup.setText(group.getGroup());
    }

    @Override
    public int getItemCount() {
        return grouplist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvgroup;
        public MyViewHolder(View view) {
            super(view);
            tvgroup = (TextView) view.findViewById(R.id.groupname);

        }

        public void bind(TeacherGroupclass groupclass) {

        }
    }
}
