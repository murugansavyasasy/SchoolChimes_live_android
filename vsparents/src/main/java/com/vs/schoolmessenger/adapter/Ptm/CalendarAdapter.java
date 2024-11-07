package com.vs.schoolmessenger.adapter.Ptm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.DateEventModel;
import com.vs.schoolmessenger.util.DateFormatter;
import com.vs.schoolmessenger.util.DateUtils;

import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<DateEventModel> dateList;
    private final Context context;
    private int selectedPosition = 0;
    private OnItemClickListener onItemClickListener;

    public CalendarAdapter(List<DateEventModel> dateList, Context context) {
        this.dateList = dateList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        DateEventModel dateEvent = dateList.get(position);

        String isEventDate = DateFormatter.formatDate(dateEvent.getEventDate());

        String[] dateComponents = DateUtils.splitDate(isEventDate);


        String month = dateComponents[0];
        String day = dateComponents[1];
        String year = dateComponents[2];

        holder.dateText.setText(month);
        holder.dateText.setTypeface(null, Typeface.BOLD);

        holder.lblYear.setText(day + ", " + year);
        holder.lblYear.setTypeface(null, Typeface.BOLD);


        if (dateEvent.getCount() > 0) {
            holder.countText.setVisibility(View.VISIBLE);
            holder.countText.setText("Available slots: " + dateEvent.getCount());
        } else {
            holder.countText.setVisibility(View.GONE);
        }

        if (position == selectedPosition) {
            holder.dateLinerLayout.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.clr_sandle));
            holder.countText.setTextColor(ContextCompat.getColor(context, R.color.clr_white));
            holder.lblYear.setTextColor(ContextCompat.getColor(context, R.color.clr_white));
        } else {
            holder.dateLinerLayout.setBackgroundColor(holder.itemView.getContext().getResources().getColor(R.color.color_lightselect));
            holder.countText.setTextColor(ContextCompat.getColor(context, R.color.clr_black));
            holder.lblYear.setTextColor(ContextCompat.getColor(context, R.color.clr_black));
        }

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                selectedPosition = position;
                onItemClickListener.onItemClick(dateEvent);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, countText, lblYear;
        LinearLayout MonthlinearLayout, dateLinerLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.date_text);
            lblYear = itemView.findViewById(R.id.lblYear);
            countText = itemView.findViewById(R.id.count_text);
            MonthlinearLayout = itemView.findViewById(R.id.MonthlinearLayout);
            dateLinerLayout = itemView.findViewById(R.id.dateLinerLayout);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DateEventModel dateEvent);
    }
}