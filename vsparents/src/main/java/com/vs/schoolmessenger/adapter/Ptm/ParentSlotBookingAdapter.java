package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.GroupedSlot;
import com.vs.schoolmessenger.model.SlotDetail;

import java.util.ArrayList;
import java.util.List;


public class ParentSlotBookingAdapter extends RecyclerView.Adapter<ParentSlotBookingAdapter.MyViewHolder> {

    private final Context context;
    private final List<GroupedSlot> dateList;
    private final List<SlotDetail> allSlotDetails;
    private OnChildItemClickListener onChildItemClickListener;


    public ParentSlotBookingAdapter(Context context, List<GroupedSlot> dateList, OnChildItemClickListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.onChildItemClickListener = listener;
        this.allSlotDetails = new ArrayList<>();

        for (GroupedSlot group : dateList) {
            allSlotDetails.addAll(group.getSlots());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_meeing_parentside, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GroupedSlot groupedSlot = dateList.get(position);

        Log.d("isSpecificItem", String.valueOf(groupedSlot.getIsSpecificMeeting()));
        holder.lblEventName.setText(groupedSlot.getEvent_name());
        holder.txtStaffSubjectName.setText(groupedSlot.getSubject_name() + " - " + groupedSlot.getStaff_name());
        holder.lblModeName.setText(groupedSlot.getEvent_mode());

        holder.lblEventName.setTypeface(null, Typeface.BOLD);
        holder.txtStaffSubjectName.setTypeface(null, Typeface.BOLD);

        SlotDetailAdapter adapter = new SlotDetailAdapter(context, groupedSlot.getSlots(), allSlotDetails);
        adapter.setOnSlotSelectedListener(() -> {
            if (holder.gridViewSlots != null) {
                onChildItemClickListener.onChildItemClick("isClick");
                holder.gridViewSlots.post(() -> notifyDataSetChanged());
            }
        });

        holder.gridViewSlots.setAdapter(adapter);
        adjustGridViewHeight(holder.gridViewSlots, adapter.getCount());

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView lblEventName, txtStaffSubjectName, lblModeName, lblLink, lblLinkName;
        GridView gridViewSlots;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lblEventName = itemView.findViewById(R.id.lblEventName);
            txtStaffSubjectName = itemView.findViewById(R.id.txtStaffSubjectName);
            lblModeName = itemView.findViewById(R.id.lblModeName);
            lblLink = itemView.findViewById(R.id.lblLink);
            gridViewSlots = itemView.findViewById(R.id.gridViewSlots);
            lblLinkName = itemView.findViewById(R.id.lblLinkName);
        }
    }

    // Adjust GridView height based on the number of items
    private void adjustGridViewHeight(GridView gridView, int itemCount) {
        ListAdapter gridAdapter = gridView.getAdapter();
        if (gridAdapter == null) return;

        int totalHeight = 0;
        int itemsPerRow = 2;  // Number of items per row in the GridView (adjust as needed)
        int numRows = (int) Math.ceil((double) itemCount / itemsPerRow);

        for (int i = 0; i < numRows; i++) {
            View listItem = gridAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() * (numRows - 1));
        gridView.setLayoutParams(params);
    }

    // Interface to handle child item clicks
    public interface OnChildItemClickListener {
        void onChildItemClick(String date);
    }
}
