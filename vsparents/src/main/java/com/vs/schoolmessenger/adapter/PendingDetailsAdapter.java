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
import com.vs.schoolmessenger.model.PendingFeeDetails;

import java.util.List;

public class PendingDetailsAdapter extends RecyclerView.Adapter<PendingDetailsAdapter.MyViewHolder> {

    private List<PendingFeeDetails> lib_list;
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
        public TextView lblFeeName, lblTerm1, lbltermFrom, lblTerm2, lblterm2From, lblTerm3, lblterm3From, lblTerm4, lblterm4From, lblTotal,lblYealyFees;
        RelativeLayout rytTerm1, rytTerm2, rytTerm3, rytTerm4, rytDetailsDisplay,rytYearly;


        public MyViewHolder(View view) {
            super(view);

            lblFeeName = (TextView) view.findViewById(R.id.lblFeeName);
            lblTerm1 = (TextView) view.findViewById(R.id.lblTerm1);
            lblTerm2 = (TextView) view.findViewById(R.id.lblTerm2);
            lblTerm3 = (TextView) view.findViewById(R.id.lblTerm3);
            lblTerm4 = (TextView) view.findViewById(R.id.lblTerm4);
            lblTotal = (TextView) view.findViewById(R.id.lblTotal);
            lblYealyFees = (TextView) view.findViewById(R.id.lblYealyFees);

            rytTerm1 = (RelativeLayout) view.findViewById(R.id.rytTerm1);
            rytTerm2 = (RelativeLayout) view.findViewById(R.id.rytTerm2);
            rytTerm3 = (RelativeLayout) view.findViewById(R.id.rytTerm3);
            rytTerm4 = (RelativeLayout) view.findViewById(R.id.rytTerm4);
            rytDetailsDisplay = (RelativeLayout) view.findViewById(R.id.rytDetailsDisplay);
            rytYearly = (RelativeLayout) view.findViewById(R.id.rytYearly);

        }
    }

    public PendingDetailsAdapter(List<PendingFeeDetails> lib_list, Context context) {
        this.lib_list = lib_list;
        this.context = context;
    }

    @Override
    public PendingDetailsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pending_fee_details, parent, false);
        return new PendingDetailsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final PendingDetailsAdapter.MyViewHolder holder, final int position) {

        final PendingFeeDetails paid = lib_list.get(position);
        holder.lblFeeName.setText(": " + paid.getFeeName());
        holder.lblTotal.setText(": " + paid.getTotal() + "0");
        if (paid.getFeesID().equals("5.0")){
            if (!paid.getYearlyFees().equals("0.0")) {
                holder.rytYearly.setVisibility(View.VISIBLE);
                holder.lblYealyFees.setText(": " + paid.getYearlyFees() + "0");
            }
        }

        if(paid.getFeesID().equals("1.0")) {

            if (paid.getFeeTerms().equals("1")) {

                if (!paid.getTerm_I().equals("0.0")) {
                    holder.rytTerm1.setVisibility(View.VISIBLE);
                    holder.lblTerm1.setText(": " + paid.getTerm_I() + "0"+"  From " + paid.getTerm1_From() + " To " + paid.getTerm1_To());
                }
            } else if (paid.getFeeName().equals("2")) {

                if (!paid.getTerm_I().equals("0.0")) {
                    holder.rytTerm1.setVisibility(View.VISIBLE);
                    holder.lblTerm1.setText(": " + paid.getTerm_I() + "0"+"  From " + paid.getTerm1_From() + " To " + paid.getTerm1_To());
                } else {
                    holder.rytTerm1.setVisibility(View.GONE);
                }

                if (!paid.getTerm_II().equals("0.0")) {
                    holder.rytTerm2.setVisibility(View.VISIBLE);
                    holder.lblTerm2.setText(": " + paid.getTerm_II() + "0"+"  From " + paid.getTerm2_From() + " To " + paid.getTerm2_To());
                } else {
                    holder.rytTerm2.setVisibility(View.GONE);
                }

            } else if (paid.getFeeTerms().equals("3")) {

                if (!paid.getTerm_I().equals("0.0")) {
                    holder.rytTerm1.setVisibility(View.VISIBLE);
                    holder.lblTerm1.setText(": " + paid.getTerm_I() + "0"+"  From " + paid.getTerm1_From() + " To " + paid.getTerm1_To());
                } else {
                    holder.rytTerm1.setVisibility(View.GONE);
                }

                if (!paid.getTerm_II().equals("0.0")) {
                    holder.rytTerm2.setVisibility(View.VISIBLE);
                    holder.lblTerm2.setText(": " + paid.getTerm_II() + "0"+"  From " + paid.getTerm2_From() + " To " + paid.getTerm2_To());
                } else {
                    holder.rytTerm2.setVisibility(View.GONE);
                }


                if (!paid.getTerm_III().equals("0.0")) {
                    holder.rytTerm3.setVisibility(View.VISIBLE);
                    holder.lblTerm3.setText(": " + paid.getTerm_III() + "0"+"  From " + paid.getTerm3_From() + " To " + paid.getTerm3_To());
                } else {
                    holder.rytTerm3.setVisibility(View.GONE);
                }


            } else if (paid.getFeeTerms().equals("4")) {

                if (!paid.getTerm_I().equals("0.0")) {
                    holder.rytTerm1.setVisibility(View.VISIBLE);
                    holder.lblTerm1.setText(": " + paid.getTerm_I() + "0"+"  From " + paid.getTerm1_From() + " To " + paid.getTerm1_To());
                } else {
                    holder.rytTerm1.setVisibility(View.GONE);
                }

                if (!paid.getTerm_II().equals("0.0")) {
                    holder.rytTerm2.setVisibility(View.VISIBLE);
                    holder.lblTerm2.setText(": " + paid.getTerm_II() + "0"+"  From " + paid.getTerm2_From() + " To " + paid.getTerm2_To());
                } else {
                    holder.rytTerm2.setVisibility(View.GONE);
                }


                if (!paid.getTerm_III().equals("0.0")) {
                    holder.rytTerm3.setVisibility(View.VISIBLE);
                    holder.lblTerm3.setText(": " + paid.getTerm_III() + "0"+"  From " + paid.getTerm3_From() + " To " + paid.getTerm3_To());
                } else {
                    holder.rytTerm3.setVisibility(View.GONE);
                }


                if (!paid.getTerm_IV().equals("0.0")) {
                    holder.rytTerm4.setVisibility(View.VISIBLE);
                    holder.lblTerm4.setText(": " + paid.getTerm_IV() + "0"+"  From " + paid.getTerm4_From() + " To " + paid.getTerm4_To());

                } else {
                    holder.rytTerm4.setVisibility(View.GONE);
                }
            }


        }
    }

    @Override
    public int getItemCount() {
        return lib_list.size();

    }
}


