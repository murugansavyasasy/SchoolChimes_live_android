package com.vs.schoolmessenger.CouponView.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponView.AcivateCoupon.BottomSheetActivity;
import com.vs.schoolmessenger.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

        holder.categoryname.setText(summary.getCategoryName());
        holder.discount.setText(summary.getDiscount() + "% Off");
        holder.merchantname.setText(summary.getMerchantName());

        String expiryDateStr = summary.getExpiry_date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            Date expiryDate = sdf.parse(expiryDateStr);
            Date currentDate = new Date();

            long diffInMillies = expiryDate.getTime() - currentDate.getTime();
            long daysLeft = TimeUnit.MILLISECONDS.toDays(diffInMillies);

            if (daysLeft >= 0) {
                holder.location_heade4.setText(daysLeft + " days");
            } else {
                holder.location_heade4.setText("Expired");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            holder.location_heade4.setText("Invalid date");
        }


        Glide.with(context)
                .load(summary.getThumbnail())
                .into(holder.thumbnail);

        holder.linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(context, BottomSheetActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("category_name", summary.getCategoryName());
            intent.putExtra("discount", summary.getDiscount());
            intent.putExtra("merchant_name", summary.getMerchantName());
            intent.putExtra("thumbnail", summary.getThumbnail());
            intent.putExtra("source_link", summary.getSource_link());
            intent.putExtra("coupon_status", summary.getCoupon_status());
            intent.putExtra("merchant_logo", summary.getMerchantLogo()); 
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return summaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        TextView categoryname, discount, merchantname, location_heade4;
        LinearLayout linearLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            categoryname = itemView.findViewById(R.id.categoryname);
            discount = itemView.findViewById(R.id.discount);
            merchantname = itemView.findViewById(R.id.merchantname);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            location_heade4 = itemView.findViewById(R.id.location_heade4);
        }
    }
}
