package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.StaffListListener;
import com.vs.schoolmessenger.model.StaffList;

import java.util.ArrayList;
import java.util.List;

public class StaffListAdapter extends RecyclerView.Adapter<StaffListAdapter.MyViewHolder> {


    private List<StaffList> lib_list;
    Context context;

    Boolean isVoiceSending;
    private StaffListListener onCheckStudentListener;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public void updateList(List<StaffList> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblStaffName, lblAuthorizedCaller, lblStaffRole, lblStaffDesignation;
        public CheckBox check_staff;

        public MyViewHolder(View view) {
            super(view);

            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblStaffRole = (TextView) view.findViewById(R.id.lblStaffRole);
            lblStaffDesignation = (TextView) view.findViewById(R.id.lblStaffDesignation);
            lblAuthorizedCaller = (TextView) view.findViewById(R.id.lblAuthorizedCaller);
            check_staff = (CheckBox) view.findViewById(R.id.check_staff);

        }
    }

    public StaffListAdapter(Boolean isVoiceSend, Context context, StaffListListener onCheckListener, List<StaffList> lib_list) {
        this.lib_list = lib_list;
        this.context = context;
        this.isVoiceSending = isVoiceSend;
        this.onCheckStudentListener = onCheckListener;
    }

    @Override
    public StaffListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_list_adapter, parent, false);
        return new StaffListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final StaffListAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));

        final StaffList staffs = lib_list.get(position);

        holder.lblStaffName.setText(staffs.getStaffName());
        holder.lblStaffRole.setText(staffs.getStaffRole());
        holder.lblStaffDesignation.setText(staffs.getDesignation());
        if (staffs.getStaffType().equals("Authorized Caller") && !isVoiceSending) {
            holder.lblAuthorizedCaller.setVisibility(View.VISIBLE);
        } else {
            holder.lblAuthorizedCaller.setVisibility(View.GONE);
        }

        holder.check_staff.setOnCheckedChangeListener(null);
        holder.check_staff.setChecked(staffs.getSelecteStatus());
        holder.check_staff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                staffs.setSelecteStatus(isChecked);
                if (isChecked) {
                    onCheckStudentListener.student_addClass(staffs);

                } else {
                    onCheckStudentListener.student_removeClass(staffs);

                }

            }
        });


    }




    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }

    public List<StaffList> getSelectedStaff() {
        List<StaffList> selectedList = new ArrayList<>();
        for (StaffList staff : lib_list) {
            if (staff.getSelecteStatus()) {
                selectedList.add(staff);
            }
        }
        return selectedList;
    }
}

