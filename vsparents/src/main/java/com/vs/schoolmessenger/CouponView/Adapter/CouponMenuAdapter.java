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
import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.R;

import java.util.List;


public class CouponMenuAdapter extends RecyclerView.Adapter<CouponMenuAdapter.ViewHolder> {
    private Context context;
    private List<Category> categoryList;

    public CouponMenuAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.coupon_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.textView.setText(category.getCategoryName());

        Glide.with(context)
                .load(category.getCategoryImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView1);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
