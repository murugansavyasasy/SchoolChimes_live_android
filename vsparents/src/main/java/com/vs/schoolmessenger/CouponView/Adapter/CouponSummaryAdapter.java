package com.vs.schoolmessenger.CouponView.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponView.AcivateCoupon.BottomSheetActivity;
import com.vs.schoolmessenger.R;

import java.util.List;

public class CouponSummaryAdapter extends RecyclerView.Adapter<CouponSummaryAdapter.ViewHolder> {
    private Context context;
    private List<Summary> summaryList;

    public CouponSummaryAdapter(Context context, List<Summary> summaryList) {
        this.context = context;
        this.summaryList = summaryList;
    }

    public void updateList(List<Summary> newList) {
        summaryList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_coupon, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Summary summary = summaryList.get(position);
        holder.lblProductName.setText(summary.getCategoryName());
        holder.lblProductOffer.setText(summary.getDiscount() + "% Off");
        holder.lblCompanyName.setText(summary.getMerchantName());

        Glide.with(context)
                .load(summary.getThumbnail())
                .into(holder.imgProduct);

        Glide.with(context)
                .load(summary.getMerchantLogo())
                .into(holder.imgOverlay);

        holder.header.setOnClickListener(view -> {
            Intent intent = new Intent(context, BottomSheetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("category_name", summary.getCategoryName());
            intent.putExtra("discount", summary.getDiscount());
            intent.putExtra("merchant_name", summary.getMerchantName());
            intent.putExtra("thumbnail", summary.getThumbnail());
            intent.putExtra("source_link", summary.getSource_link());
            intent.putExtra("coupon_status", summary.getCoupon_status());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct,imgUser,imgOverlay;
        TextView lblProductName,lblProductOffer,lblCompanyName,lblDays;
        ConstraintLayout header;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUser = itemView.findViewById(R.id.imgUser);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            imgOverlay = itemView.findViewById(R.id.imgOverlay);
            lblProductName = itemView.findViewById(R.id.lblProductName);
            lblCompanyName = itemView.findViewById(R.id.lblCompanyName);
            lblProductOffer = itemView.findViewById(R.id.lblProductOffer);
            lblDays = itemView.findViewById(R.id.lblDays);
            header = itemView.findViewById(R.id.header);
        }
    }
}
