package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.ExamDateListClass;

import java.util.List;

public class ExamDateListAdapter extends RecyclerView.Adapter<ExamDateListAdapter.MyViewHolder> {

    private List<ExamDateListClass> lib_list;
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
        public TextView lblFeeName,lblMonthly,lblTotal,lblTotalFrom,lblPendingAmount;
        public MyViewHolder(View view) {
            super(view);

            lblFeeName = (TextView) view.findViewById(R.id.lblFeeName);
            lblMonthly = (TextView) view.findViewById(R.id.lblMonthly);
            lblTotal = (TextView) view.findViewById(R.id.lblTotal);
            lblPendingAmount = (TextView) view.findViewById(R.id.lblPendingAmount);

        }
    }
    public ExamDateListAdapter(List<ExamDateListClass> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public ExamDateListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_date_list_ext, parent, false);
        return new ExamDateListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ExamDateListAdapter.MyViewHolder holder, final int position) {
        final ExamDateListClass paid = lib_list.get(position);
    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}



