package com.vs.schoolmessenger.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.databinding.StaffDetailItemBinding;
import com.vs.schoolmessenger.interfaces.StaffSelectedListener;
import com.vs.schoolmessenger.model.StaffDetail;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StaffChatDetailAdapter extends RecyclerView.Adapter<BaseViewHolders<StaffDetailItemBinding>> {
    ArrayList<StaffDetail> staffDetails;
    StaffSelectedListener listener;

    public StaffChatDetailAdapter(ArrayList<StaffDetail> staffDetails, StaffSelectedListener listener) {
        this.staffDetails = staffDetails;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolders<StaffDetailItemBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolders<StaffDetailItemBinding>(StaffDetailItemBinding.bind(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.staff_detail_item, parent, false))) {
            @Override
            public int getVariable() {
                return com.vs.schoolmessenger.BR.staffDetail;
            }
        };
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolders<StaffDetailItemBinding> holder, final int position) {
        holder.bind(staffDetails.get(position));
        holder.getBinding().staff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.selectStaff(staffDetails.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return staffDetails.size();
    }
}
