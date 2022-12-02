package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SubjectAndMarkList;

import java.util.List;

public class MarkListAdapter extends RecyclerView.Adapter<MarkListAdapter.MyViewHolder> {


    private List<SubjectAndMarkList> lib_list;
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
        public TextView lblSubject, lblMarks;

        public MyViewHolder(View view) {
            super(view);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            lblMarks = (TextView) view.findViewById(R.id.lblMarks);
        }
    }

    public MarkListAdapter(List<SubjectAndMarkList> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }
    @Override
    public MarkListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marks_list_adapter, parent, false);
        return new MarkListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MarkListAdapter.MyViewHolder holder, final int position) {

        final SubjectAndMarkList library = lib_list.get(position);
        if(library.getSubject().equals("Total")){
            holder.lblSubject.setText(library.getSubject());
            holder.lblMarks.setText(library.getMark());
            holder.lblSubject.setTypeface(null, Typeface.BOLD);
            holder.lblMarks.setTypeface(null, Typeface.BOLD);
        }
        if(library.getSubject().equals("Rank")){
            holder.lblSubject.setText(library.getSubject());
            holder.lblMarks.setText(library.getMark());
            holder.lblSubject.setTypeface(null, Typeface.BOLD);
            holder.lblMarks.setTypeface(null, Typeface.BOLD);
            }

        if(library.getSubject().equals("Remarks")){
            holder.lblSubject.setText(library.getSubject());
            holder.lblMarks.setText(library.getMark());
            holder.lblSubject.setTypeface(null, Typeface.BOLD);
            holder.lblMarks.setTypeface(null, Typeface.BOLD);
        }
        holder.lblSubject.setText(library.getSubject());
        holder.lblMarks.setText(library.getMark());

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

