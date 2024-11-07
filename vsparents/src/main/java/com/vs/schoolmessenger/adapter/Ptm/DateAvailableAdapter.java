package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SlotsAvailableTimeData;
import com.vs.schoolmessenger.util.DateFormatter;

import java.util.List;

public class DateAvailableAdapter extends RecyclerView.Adapter<DateAvailableAdapter.ItemViewHolder> implements AvailableTimeShowingAdapter.OnDeleteClickListener {
    private final List<SlotsAvailableTimeData> itemList;
    Context context;

    public DateAvailableAdapter(List<SlotsAvailableTimeData> itemList, Context context) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dateshowing, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        holder.lblDate.setText(DateFormatter.formatDate(itemList.get(position).getDate()));
        holder.lblDate.setTypeface(null, Typeface.BOLD);

        AvailableTimeShowingAdapter adapter = new AvailableTimeShowingAdapter(position, itemList.get(position).getData(), this);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        int size = itemList != null ? itemList.size() : 0;
        return size;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView lblDate;
        RecyclerView recyclerView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lblDate = itemView.findViewById(R.id.lblDate);
            recyclerView = itemView.findViewById(R.id.rcytime);
        }
    }

    @Override
    public void onDeleteClicked(int parentPosition) {
        if (parentPosition >= 0 && parentPosition < itemList.size()) {
            itemList.remove(parentPosition);
            notifyItemRemoved(parentPosition);
            notifyItemRangeChanged(parentPosition, itemList.size());
        }
    }
}


