package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DailyFeeReportData;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.ArrayList;

public class DailyCollectionAdapter extends RecyclerView.Adapter<DailyCollectionAdapter.MyViewHolder> {

    Context context;
    ArrayList<DailyFeeReportData> isDailyFeeReportData = new ArrayList<>();

    @NonNull
    @Override
    public DailyCollectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dailycollecton_amount, parent, false);
        return new DailyCollectionAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyCollectionAdapter.MyViewHolder holder, int position) {

        final DailyFeeReportData dailyFeeReportData = isDailyFeeReportData.get(position);

        if (Util_Common.isType == 2) {
            holder.lblTitle.setText("Class Name");
        }
        holder.lblTitle.setText(dailyFeeReportData.getFeeName());
        holder.lblPyamentmethod.setText(String.valueOf("\u20B9 " + dailyFeeReportData.getAmount()));

    }

    public DailyCollectionAdapter(Context context, ArrayList<DailyFeeReportData> isDailyFeeReportData) {
        this.context = context;
        this.isDailyFeeReportData = isDailyFeeReportData;
    }

    @Override
    public int getItemCount() {
        return isDailyFeeReportData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblAmount, lblPyamentmethod, lblTitle, lblmeetingtopic;

        public MyViewHolder(View view) {
            super(view);

            lblTitle = view.findViewById(R.id.lblTitle);
            lblPyamentmethod = view.findViewById(R.id.lblPyamentmethod);
        }

    }
}
