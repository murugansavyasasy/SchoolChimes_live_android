package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.MonthlyPending;

import java.util.List;

public class MonthlyPendingAdapter extends RecyclerView.Adapter<MonthlyPendingAdapter.MyViewHolder> {

    private List<MonthlyPending> lib_list;
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
        public TextView lblFeeName,lblMonthly,lblTotal,lblTotalFrom,lblPendingAmount;


        public MyViewHolder(View view) {
            super(view);

            lblFeeName = (TextView) view.findViewById(R.id.lblFeeName);
            lblMonthly = (TextView) view.findViewById(R.id.lblMonthly);
            lblTotal = (TextView) view.findViewById(R.id.lblTotal);
            lblPendingAmount = (TextView) view.findViewById(R.id.lblPendingAmount);

            }
    }
    public MonthlyPendingAdapter(List<MonthlyPending> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public MonthlyPendingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.monthly_fees, parent, false);
        return new MonthlyPendingAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MonthlyPendingAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final MonthlyPending paid = lib_list.get(position);

        holder.lblFeeName.setText(": "+paid.getFeeName());
        holder.lblMonthly.setText(": "+paid.getMonthly());
        holder.lblTotal.setText(": "+paid.getTotalMonthly()+"0");
        holder.lblPendingAmount.setText(": "+paid.getPendingAmount()+"0"+"  From "+paid.getStartMonthName()+" To "+paid.getEndMonthName());


    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}



