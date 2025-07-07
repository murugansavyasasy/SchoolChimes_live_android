package com.vs.schoolmessenger.CouponView.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummary;
import com.vs.schoolmessenger.CouponView.MycouponDashboard.MycouponViewActivity;
import com.vs.schoolmessenger.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TicketCouponAdapter extends RecyclerView.Adapter<TicketCouponAdapter.ViewHolder> {

    private final Context context;
    private List<TicketSummary> ticketSummaryList;
    public TicketCouponAdapter(Context context,List<TicketSummary> ticketSummaryList) {
        this.context = context;
        this.ticketSummaryList = ticketSummaryList;
    }

    public void updateList(List<TicketSummary> newList) {
        ticketSummaryList = newList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.ticket_couponsummary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Hardcoded data – You can customize each item based on `position` if needed
        TicketSummary ticketSummary = ticketSummaryList.get(position);
        holder.categoryName.setText(ticketSummary.getMerchant_name());
        holder.discount.setText(ticketSummary.getOffer_to_show());
        holder.merchantName.setText("Expires in "+ticketSummary.getExpires_in()+ " days");


        Glide.with(context)
                .load(ticketSummary.getMerchant_logo())
                .into(holder.centerImage);



        holder.cardview.setOnClickListener(view ->{
            Intent intent = new Intent(context, MycouponViewActivity.class);
            List<TicketSummary.Location> locationList = ticketSummary.getLocation_list();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("merchant_name", ticketSummary.getMerchant_name());
            intent.putExtra("offer_to_show", ticketSummary.getOffer_to_show());
            intent.putExtra("how_to_use", ticketSummary.getHow_to_use());
            intent.putExtra("coupon_code", ticketSummary.getCoupon_code());
            intent.putExtra("cover_image", ticketSummary.getCover_image());
            intent.putExtra("expiry_date", ticketSummary.getExpiry_date());
            intent.putExtra("expiry_type", ticketSummary.getExpiry_type());
            intent.putExtra("merchant_logo", ticketSummary.getMerchant_logo());
            intent.putExtra("CTAredirect", ticketSummary.getCTAredirect());
            intent.putExtra("coupon_status", ticketSummary.getCoupon_status());
            intent.putExtra("location_list", new ArrayList<>(locationList));
            context.startActivity(intent);
        });

        if (Objects.equals(ticketSummary.getCoupon_status(), "activated")) {
            holder.couponstatus.setVisibility(View.GONE);
        } else if (Objects.equals(ticketSummary.getCoupon_status(), "claimed")) {
            holder.couponstatus.setVisibility(View.VISIBLE);
            holder.couponstatus.setBackground(context.getDrawable(R.drawable.redeemed_backgroundgreen));
            holder.couponstatus.setText("Redeemed");
        } else if (Objects.equals(ticketSummary.getCoupon_status(), "expired")) {
            holder.couponstatus.setVisibility(View.VISIBLE);
            holder.couponstatus.setBackground(context.getDrawable(R.drawable.redeemed_backgroundgrey));
            holder.couponstatus.setText("Expired");
        } else {
            holder.couponstatus.setVisibility(View.GONE);
        }


    }


    @Override
    public int getItemCount() {
        return ticketSummaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView thumbnail;
        ImageView centerImage;
        TextView categoryName, discount, merchantName;
        CardView cardview;
        TextView couponstatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail);
            centerImage = itemView.findViewById(R.id.center_image);
            categoryName = itemView.findViewById(R.id.categoryname);
            discount = itemView.findViewById(R.id.discount);
            merchantName = itemView.findViewById(R.id.merchantname);
            cardview =itemView.findViewById(R.id.card_view);
            couponstatus = itemView.findViewById(R.id.couponstatus);
        }
    }
}
