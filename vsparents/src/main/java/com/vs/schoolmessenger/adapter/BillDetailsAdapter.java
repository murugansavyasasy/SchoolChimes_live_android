package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.BillDetails;

import java.util.List;

public class BillDetailsAdapter extends RecyclerView.Adapter<BillDetailsAdapter.MyViewHolder> {

    private List<BillDetails> lib_list;
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
        public TextView lblFeeName,lblPaidAmount,lblSno;
        public RelativeLayout rytDetailsDisplay;


        public MyViewHolder(View view) {
            super(view);

            lblFeeName = (TextView) view.findViewById(R.id.lblFeeName);
            lblSno = (TextView) view.findViewById(R.id.lblSno);
            lblPaidAmount = (TextView) view.findViewById(R.id.lblPaidAmount);



        }
    }
    public BillDetailsAdapter(List<BillDetails> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public BillDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.total_paid_details_list, parent, false);
        return new BillDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BillDetailsAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final BillDetails paid = lib_list.get(position);

        holder.lblFeeName.setText(paid.getFeeName()+"("+paid.getFeeTerm()+")");
        holder.lblSno.setText(String.valueOf(paid.getSerialNo()));
        holder.lblPaidAmount.setText("Rs:"+paid.getPaidAmount()+"0");



        }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


