package com.vs.schoolmessenger.adapter;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.HolidayModel;

import java.util.List;

public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.MyViewHolder> {

    private List<HolidayModel> lib_list;
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
        public TextView lblHolidayDate,lblHolidayReason;
        public MyViewHolder(View view) {
            super(view);

            lblHolidayDate = (TextView) view.findViewById(R.id.lblHolidayDate);
            lblHolidayReason = (TextView) view.findViewById(R.id.lblHolidayReason);
        }
    }
    public HolidaysAdapter(List<HolidayModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public HolidaysAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.holidays_list, parent, false);
        return new HolidaysAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final HolidaysAdapter.MyViewHolder holder, final int position) {

        final HolidayModel holiday = lib_list.get(position);
        holder.lblHolidayDate.setText(" : "+holiday.getDate());
        holder.lblHolidayReason.setText(" : "+holiday.getReason());
    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}

