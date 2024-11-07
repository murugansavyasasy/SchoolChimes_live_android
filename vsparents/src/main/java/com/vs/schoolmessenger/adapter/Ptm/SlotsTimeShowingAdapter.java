package com.vs.schoolmessenger.adapter.Ptm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TimeSlot;

import java.util.List;

public class SlotsTimeShowingAdapter extends RecyclerView.Adapter<SlotsTimeShowingAdapter.ItemViewHolder> {
    private List<TimeSlot> itemList;
    OnDeleteClickListener onDeleteClickListener;
    int isPosition;

    public SlotsTimeShowingAdapter(int isDatePosition, List<TimeSlot> itemList, OnDeleteClickListener onDeleteClickListener) {
        this.itemList = itemList;
        this.isPosition = isDatePosition;
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
        holder.lblTime.setText(itemList.get(position).getFromTime() + " - " + itemList.get(position).getToTime());

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDateDeleted(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView lblTime;
        ImageView imgDelete;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            lblTime = itemView.findViewById(R.id.lblTime);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }

    public interface OnDeleteClickListener {
        void onDeleteClicked(int isDateAdapterPosition);
    }

    private void isDateDeleted(int position) {
        itemList.remove(position);
        if (itemList.size() == 0) {
            if (onDeleteClickListener != null) {
                onDeleteClickListener.onDeleteClicked(isPosition);
            }
        }
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, itemList.size());
    }
}