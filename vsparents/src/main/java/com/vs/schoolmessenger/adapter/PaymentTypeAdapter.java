package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.PaymentTypeWiseItem;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.List;

public class PaymentTypeAdapter extends RecyclerView.Adapter<PaymentTypeAdapter.MyViewHolder> {
    private List<PaymentTypeWiseItem> lib_list;
    Context context;
    String child_id, schoolid;


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

        public TextView lblName,lblAmount;

        public MyViewHolder(View view) {
            super(view);
            lblName = (TextView) view.findViewById(R.id.lblName);
            lblAmount = (TextView) view.findViewById(R.id.lblAmount);
        }
    }

    public PaymentTypeAdapter(List<PaymentTypeWiseItem> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public PaymentTypeAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.daily_collection_fee_list, parent, false);
        return new PaymentTypeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PaymentTypeAdapter.MyViewHolder holder, final int
            position) {
        final PaymentTypeWiseItem exam = lib_list.get(position);
        child_id = Util_SharedPreference.getChildIdFromSP(context);
        schoolid = Util_SharedPreference.getSchoolIdFromSP(context);
        holder.lblName.setText(exam.getName());
        holder.lblAmount.setText("\u20B9"+" "+exam.getPaidAmount());
    }


    @Override
    public int getItemCount() {
        return lib_list.size();
    }

    public void updateList(List<PaymentTypeWiseItem> temp) {
        lib_list = temp;
        notifyDataSetChanged();
    }
}