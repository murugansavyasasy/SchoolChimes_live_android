package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherSectionModel;

import java.util.List;



/**
 * Created by devi on 5/31/2017.
 */

public class TeacherSelectedsectionListAdapter extends RecyclerView.Adapter<TeacherSelectedsectionListAdapter.MyViewHolder> {

    private List<TeacherSectionModel> seletedsectionlist;
    Context context;

    public TeacherSelectedsectionListAdapter(Context context, List<TeacherSectionModel> seletedsectionlist) {
        this.context = context;
        this.seletedsectionlist = seletedsectionlist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_selectedsection, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.bind(seletedsectionlist.get(position));

        final TeacherSectionModel sect = seletedsectionlist.get(position);
        holder.tvsec.setText(sect.getStandard() + "-" + sect.getSection());
        holder.tvsub.setText(sect.getSubject());
    }

    @Override
    public int getItemCount() {
        return seletedsectionlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvsec, tvsub;

        public MyViewHolder(View view) {
            super(view);
            tvsec = (TextView) view.findViewById(R.id.tv_selsection);
            tvsub = (TextView) view.findViewById(R.id.tv_selsubject);

        }

        public void bind(TeacherSectionModel sectionclass) {

        }
    }

    public void clearAllData() {
        int size = this.seletedsectionlist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.seletedsectionlist.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}
