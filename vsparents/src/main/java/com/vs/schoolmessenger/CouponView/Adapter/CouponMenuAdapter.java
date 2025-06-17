package com.vs.schoolmessenger.CouponView.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.R;

import java.util.List;


public class CouponMenuAdapter extends RecyclerView.Adapter<CouponMenuAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public CouponMenuAdapter(Context context, List<Category> categoryList, int initiallySelectedPosition, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
        this.selectedPosition = initiallySelectedPosition;
    }



    @NonNull
    @Override
    public CouponMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coupon_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponMenuAdapter.ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textView.setText(category.getCategoryName());

        if (category.getDrawableResId() != -1) {
            holder.imageView.setImageResource(category.getDrawableResId());
        } else {
            Glide.with(context)
                    .load(category.getCategoryImage())
                    .placeholder(R.drawable.allimage)
                    .into(holder.imageView);
        }

        if (position == selectedPosition) {
            holder.imageView.setBackgroundResource(R.drawable.custom_coupon_rounded_background_click);
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.gnt_blue));
        } else {
            holder.imageView.setBackgroundResource(R.drawable.custom_coupon_rounded_background);
            holder.textView.setTextColor(ContextCompat.getColor(context, R.color.clr_black));

        }

        holder.relative_layout.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }


    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        RelativeLayout relative_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1);
            textView = itemView.findViewById(R.id.textView);
            relative_layout = itemView.findViewById(R.id.relative_layout);
        }
    }
}
