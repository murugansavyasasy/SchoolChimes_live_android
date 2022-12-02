package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.OnlineStepsModel;

import java.util.List;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.RecyclerView;

public class OnlineMeetingTypeAdapter extends RecyclerView.Adapter<OnlineMeetingTypeAdapter.MyViewHolder> {

    private List<OnlineStepsModel> lib_list;
    Context context;

    public void clearAllData() {
        int size = this.lib_list.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.lib_list.remove(0);
            }
            this.notifyItemRangeRemoved(0, size);
        }
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblStep,lblNo;
        public MyViewHolder(View view) {
            super(view);

            lblStep = (TextView) view.findViewById(R.id.lblStep);
            lblNo = (TextView) view.findViewById(R.id.lblNo);


        }
    }
    public OnlineMeetingTypeAdapter(List<OnlineStepsModel> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public OnlineMeetingTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.online_step_item, parent, false);
        return new OnlineMeetingTypeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OnlineMeetingTypeAdapter.MyViewHolder holder, final int position) {

        final OnlineStepsModel holiday = lib_list.get(position);
            holder.lblStep.setText(Html.fromHtml(holiday.getStep()));

        if(!holiday.getID().equals("")) {
            holder.lblNo.setText(holiday.getID() + " .");
        }

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}
