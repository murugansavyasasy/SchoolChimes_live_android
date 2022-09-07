package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PaidBillDetails;
import com.vs.schoolmessenger.model.PaidDetailsList;

import java.util.List;

public class PaidDetailsAdapter extends RecyclerView.Adapter<PaidDetailsAdapter.MyViewHolder> {

    private List<PaidDetailsList> lib_list;
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
        public TextView lblCreatedOn,lblTotalPaid,lblPaymentType,lblLateFee,lblPayment;
        public RelativeLayout rytDetailsDisplay;

        public MyViewHolder(View view) {
            super(view);

            lblCreatedOn = (TextView) view.findViewById(R.id.lblCreatedOn);
            lblTotalPaid = (TextView) view.findViewById(R.id.lblTotalPaid);
            lblPaymentType = (TextView) view.findViewById(R.id.lblPaymentType);
            lblLateFee = (TextView) view.findViewById(R.id.lblLateFee);
            lblPayment = (TextView) view.findViewById(R.id.lblPayment);
            rytDetailsDisplay = (RelativeLayout) view.findViewById(R.id.rytDetailsDisplay);

        }
    }
    public PaidDetailsAdapter(List<PaidDetailsList> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public PaidDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.paid_details_list, parent, false);
        return new PaidDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PaidDetailsAdapter.MyViewHolder holder, final int position) {

        Log.d("listsizeeee", String.valueOf(lib_list.size()));
        final PaidDetailsList paid = lib_list.get(position);

        holder.lblCreatedOn.setText(": "+paid.getCreatedOn());
        holder.lblTotalPaid.setText(": "+paid.getTotalPaid()+"0");
        holder.lblPaymentType.setText(": "+paid.getPaymentType());
        holder.lblLateFee.setText(": "+paid.getLateFee()+"0");



        if(paid.getIsRejected().equals("true")){
            holder.lblPayment.setText(": Failure");
        }
        else {
            holder.lblPayment.setText(": Successfull");
        }
        holder.rytDetailsDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent marklist=new Intent(context, PaidBillDetails.class);
                marklist.putExtra("invoice_id", paid.getInvoiceId());
                marklist.putExtra("total_amount", paid.getTotalPaid());
                marklist.putExtra("Date", paid.getCreatedOn());
                marklist.putExtra("Type", paid.getPaymentType());
                context.startActivity(marklist);

            }
        });

    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


