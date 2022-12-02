package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.StaffModel;

import java.util.List;

public class StaffDetailsAdapter extends RecyclerView.Adapter<StaffDetailsAdapter.MyViewHolder> {

    private List<StaffModel> lib_list;
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
        public TextView lblStaffName, lblSubjectName;

        public MyViewHolder(View view) {
            super(view);

            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblSubjectName = (TextView) view.findViewById(R.id.lblSubjectName);

        }
    }

    public StaffDetailsAdapter(List<StaffModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public StaffDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_details_list, parent, false);
        return new StaffDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StaffDetailsAdapter.MyViewHolder holder, final int position) {

        final StaffModel library = lib_list.get(position);

        holder.lblStaffName.setText(": "+library.getStaffName());
        holder.lblSubjectName.setText(": "+library.getSubName());



    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

