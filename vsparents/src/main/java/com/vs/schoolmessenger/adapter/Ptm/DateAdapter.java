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
import com.vs.schoolmessenger.model.DateModel;
import com.vs.schoolmessenger.util.Util_Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// DateAdapter.java
public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private final List<DateModel> dateList;
    Context context;

    public DateAdapter(Context context, List<DateModel> dateList) {
        this.context = context;
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_item, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateModel dateModel = dateList.get(position);
        if (Util_Common.isChooseDate == true) {
            holder.tvDate.setVisibility(View.VISIBLE);
            try {
                holder.tvDate.setTypeface(null, Typeface.BOLD);
                holder.tvDate.setText(isDateConverted(dateModel.getDate()));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.tvDate.setVisibility(View.GONE);
        }
        DetailsAdapter detailsAdapter = new DetailsAdapter(context, dateModel.getDetails());
        holder.detailsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.detailsRecyclerView.setAdapter(detailsAdapter);
    }

    public String isDateConverted(String dateString) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        Date date = dateFormat.parse(dateString);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("EEE - MMM dd, yyyy", Locale.ENGLISH);
        String formattedDate = outputDateFormat.format(date);
        String output = formattedDate;
        System.out.println(output);
        return output;
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate;
        RecyclerView detailsRecyclerView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            detailsRecyclerView = itemView.findViewById(R.id.detailsRecyclerView);
        }
    }
}
