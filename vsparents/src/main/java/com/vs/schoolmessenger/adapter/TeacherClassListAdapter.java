package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherClassesListclass;

import java.util.List;


/**
 * Created by devi on 5/24/2017.
 */

public class TeacherClassListAdapter extends RecyclerView.Adapter<TeacherClassListAdapter.MyViewHolder>  {
    private List<TeacherClassesListclass> classlist;
    Context context;


    public TeacherClassListAdapter(Context context, List<TeacherClassesListclass> classlist) {
        this.context = context;
        this.classlist = classlist;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_class_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(classlist.get(position));

        final TeacherClassesListclass classes = classlist.get(position);
        holder.tvclass.setText(classes.getClasses());
    }

    @Override
    public int getItemCount() {
        return classlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvclass;
        public MyViewHolder(View view) {
            super(view);
            tvclass = (TextView) view.findViewById(R.id.classname);
        }

        public void bind(TeacherClassesListclass classesListclass) {
        }
    }
}
