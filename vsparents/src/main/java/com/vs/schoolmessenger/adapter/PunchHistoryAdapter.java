package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.PunchHistoryRes;

import java.util.List;

public class PunchHistoryAdapter extends RecyclerView.Adapter<PunchHistoryAdapter.MyViewHolder> {
    private List<PunchHistoryRes.PunchHistoryData.PunchHistoryTimings> lib_list;
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
        public TextView lblTiming,lblPunchType,lblDeviceModel;
        public ImageView imgDelete;
        public MyViewHolder(View view) {
            super(view);
            lblTiming = (TextView) view.findViewById(R.id.lblTiming);
            lblPunchType = (TextView) view.findViewById(R.id.lblPunchType);
            lblDeviceModel = (TextView) view.findViewById(R.id.lblDeviceModel);
        }
    }
    public PunchHistoryAdapter(List<PunchHistoryRes.PunchHistoryData.PunchHistoryTimings> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public PunchHistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.punch_history_timings_list_item, parent, false);
        return new PunchHistoryAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final PunchHistoryAdapter.MyViewHolder holder, final int position) {
        final PunchHistoryRes.PunchHistoryData.PunchHistoryTimings holiday = lib_list.get(position);
        holder.lblTiming.setText(holiday.getTime());
        holder.lblPunchType.setText("Punch Type : " + holiday.getPunchType().getValue());
        holder.lblDeviceModel.setText("Device Model : "+holiday.getDeviceModel());
        holder.lblTiming.setTypeface(null, Typeface.BOLD);
        holder.lblDeviceModel.setTypeface(null, Typeface.BOLD);

    }

    @Override
    public int getItemCount() {
        return lib_list.size();
    }
}
