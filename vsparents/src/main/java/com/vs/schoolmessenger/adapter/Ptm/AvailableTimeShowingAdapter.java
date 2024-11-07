package com.vs.schoolmessenger.adapter.Ptm;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.slotsTime;

import java.util.ArrayList;
import java.util.List;

public class AvailableTimeShowingAdapter extends RecyclerView.Adapter<AvailableTimeShowingAdapter.ItemViewHolder> {
    private final List<slotsTime> itemList;
    private final OnDeleteClickListener onDeleteClickListener;
    private final int isPosition;

    public AvailableTimeShowingAdapter(int isDatePosition, List<slotsTime> itemList, OnDeleteClickListener onDeleteClickListener) {
        this.isPosition = isDatePosition;
        this.itemList = itemList != null ? itemList : new ArrayList<>();
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slots_time_showing, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        if (itemList != null && !itemList.isEmpty()) {
            slotsTime slot = itemList.get(position);
            holder.lblTime.setText(slot.getFrom_time() + " - " + slot.getTo_time());
            holder.lblAvailable.setText(slot.getSlot_Availablity());
            holder.lblAvailable.setVisibility(View.VISIBLE);

            if (slot.getSlot_Availablity().equals("Available")) {
                holder.imgDelete.setVisibility(View.VISIBLE);
                holder.lblAvailable.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.color_green));
            } else {
                holder.lblAvailable.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.gnt_red));

                holder.imgDelete.setVisibility(View.GONE);
            }
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    isDateDeleted(position);
                }
            });

            Log.d("itemList.size()", String.valueOf(itemList.size()));
            Log.d("position", String.valueOf(position));
            if (itemList.size() - 1 == position) {
                Log.d("itemList.size()__", String.valueOf(itemList.size()));
                Log.d("position__", String.valueOf(position));
                for (int i = 0; i <= itemList.size() - 1; i++) {
                    if (!itemList.get(i).getSlot_Availablity().equals("Available")) {
                        Log.d("itemList.get(i).getFrom_time()", String.valueOf(itemList.get(i).getFrom_time()));
                        itemList.remove(itemList.get(i).getFrom_time());
                        itemList.remove(itemList.get(i).getTo_time());
                    }
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList != null ? itemList.size() : 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView lblTime;
        ImageView imgDelete;
        TextView lblAvailable;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lblTime = itemView.findViewById(R.id.lblTime);
            imgDelete = itemView.findViewById(R.id.imgDelete);
            lblAvailable = itemView.findViewById(R.id.lblAvailable);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int isDateAdapterPosition);
    }

    private void isDateDeleted(int position) {
        if (itemList != null && position >= 0 && position < itemList.size()) {
            itemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, itemList.size());
            if (itemList.isEmpty() && onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClicked(isPosition);
            }
        }
    }
}

