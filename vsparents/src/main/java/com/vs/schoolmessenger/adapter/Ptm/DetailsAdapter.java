package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DetailsModel;
import com.vs.schoolmessenger.util.GridViewUtility;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsViewHolder> {

    private List<DetailsModel> detailsModelList;
    Context context;

    public DetailsAdapter(Context context, List<DetailsModel> detailsModelList) {
        this.context = context;
        this.detailsModelList = detailsModelList;
    }

    @NonNull
    @Override
    public DetailsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.details_item, parent, false);
        return new DetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsViewHolder holder, int position) {
        DetailsModel detailsModel = detailsModelList.get(position);


        // Set up SlotAdapter
        SlotAdapter slotAdapter = new SlotAdapter(context, detailsModel.getSlots());
        holder.slotsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.slotsRecyclerView.setAdapter(slotAdapter);

        StdSecAdapter adapter = new StdSecAdapter(context, detailsModel.getStd_sec_details());
        holder.stdSecDetailsRecyclerView.setAdapter(adapter);
        GridViewUtility.setDynamicHeight(holder.stdSecDetailsRecyclerView);
        // adjustGridViewHeight(holder.stdSecDetailsRecyclerView, adapter.getCount());

    }

    @Override
    public int getItemCount() {
        return detailsModelList.size();
    }

    public static class DetailsViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventNameTextView;
        private final RecyclerView slotsRecyclerView;
        private final GridView stdSecDetailsRecyclerView;

        public DetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            eventNameTextView = itemView.findViewById(R.id.eventNameTextView);
            slotsRecyclerView = itemView.findViewById(R.id.slotsRecyclerView);
            stdSecDetailsRecyclerView = itemView.findViewById(R.id.stdSecDetailsRecyclerView);
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

}
