package com.vs.schoolmessenger.CouponView.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.R;

import java.util.List;

public class CouponSummaryAdapter  extends RecyclerView.Adapter<CouponSummaryAdapter.ViewHolder> {
    private Context context;
    private List<Summary> summaryList;

    public CouponSummaryAdapter(Context context, List<Summary> summaryList) {
        this.context = context;
        this.summaryList = summaryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Summary summary = summaryList.get(position);
        holder.categoryname.setText(summary.getCategoryName());
        holder.campaignname.setText(summary.getCampaignName());
        holder.merchantname.setText(summary.getMerchantName());
        Glide.with(context)
                .load(summary.getThumbnail())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView categoryname, campaignname, merchantname;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            categoryname = itemView.findViewById(R.id.categoryname);
            campaignname = itemView.findViewById(R.id.campaignname);
            merchantname = itemView.findViewById(R.id.merchantname);
        }
    }

}
