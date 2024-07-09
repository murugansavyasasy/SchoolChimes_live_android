package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DailyCollectionModeTypeData;

import java.util.ArrayList;

public class DailyCollectionModeType extends RecyclerView.Adapter<DailyCollectionModeType.MyViewHolder> {
    Context context;

    ArrayList<DailyCollectionModeTypeData> isDailyFeeData = new ArrayList<>();

    @NonNull
    @Override
    public DailyCollectionModeType.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailycollection_modetype, parent, false);
        return new DailyCollectionModeType.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyCollectionModeType.MyViewHolder holder, int position) {
        final DailyCollectionModeTypeData dailyFeeReportData = isDailyFeeData.get(position);
        holder.lblPyamentmethod.setText(dailyFeeReportData.getPaymentMode());
        holder.lblAmount.setText(String.valueOf(dailyFeeReportData.getAmount()));
    }

    @Override
    public int getItemCount() {
        return isDailyFeeData.size();
    }

    public DailyCollectionModeType(Context context, ArrayList<DailyCollectionModeTypeData> dateList) {
        this.context = context;
        this.isDailyFeeData = dateList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblAmount, lblPyamentmethod, lblTitle, lblmeetingtopic;

        public MyViewHolder(View view) {
            super(view);
            lblAmount = view.findViewById(R.id.lblAmount);
            lblmeetingtopic = view.findViewById(R.id.lblmeetingtopic);
            lblTitle = view.findViewById(R.id.lblTitle);
            lblPyamentmethod = view.findViewById(R.id.lblPyamentmethod);
        }
    }
}
