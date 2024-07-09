package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DailyFeeData;
import com.vs.schoolmessenger.model.DailyFeeReportData;

import java.util.ArrayList;

public class DailyFeeReportAdapter extends RecyclerView.Adapter<DailyFeeReportAdapter.MyViewHolder> {

    Context context;
    ArrayList<DailyFeeData> isDailyFeeData = new ArrayList<>();
    ArrayList<DailyFeeReportData> isDailyFeeReportData = new ArrayList<>();
    DailyCollectionAdapter isDailyCollectionAdapter;

    @NonNull
    @Override
    public DailyFeeReportAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailyfee_report, parent, false);
        return new DailyFeeReportAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyFeeReportAdapter.MyViewHolder holder, int position) {

        final DailyFeeData DailyFeeDataDailyFeeData = isDailyFeeData.get(position);
        holder.lblAmountType.setText(DailyFeeDataDailyFeeData.getTypeName());
        holder.lblAmount.setText(String.valueOf("\u20B9 " + DailyFeeDataDailyFeeData.getTotal()));

        isDailyCollectionAdapter = new DailyCollectionAdapter(context, DailyFeeDataDailyFeeData.getData());
        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        holder.rcyDailyCollection.setHasFixedSize(true);
        holder.rcyDailyCollection.setLayoutManager(mLayoutManagerS);
        holder.rcyDailyCollection.setItemAnimator(new DefaultItemAnimator());
        holder.rcyDailyCollection.setAdapter(isDailyCollectionAdapter);

    }

    public DailyFeeReportAdapter(Context context, ArrayList<DailyFeeData> dateList) {
        this.context = context;
        this.isDailyFeeData = dateList;
    }

    @Override
    public int getItemCount() {
        return isDailyFeeData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblAmountType, lblAmount;

        RecyclerView rcyDailyCollection;

        public MyViewHolder(View view) {
            super(view);

            lblAmountType = view.findViewById(R.id.lblAmountType);
            lblAmount = view.findViewById(R.id.lblAmount);
            rcyDailyCollection = view.findViewById(R.id.rcyDailyCollection);
        }

    }
}
