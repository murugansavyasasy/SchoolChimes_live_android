package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.PdfAppRead;
import com.vs.schoolmessenger.model.CertificateListDataItem;
import com.vs.schoolmessenger.payment.PdfWebView;

import java.util.List;

public class CertificatesListAdapter extends RecyclerView.Adapter<CertificatesListAdapter.MyViewHolder> {
    private List<CertificateListDataItem> dateList;
    Context context;

    @Override
    public CertificatesListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.certificates_list_items, parent, false);

        return new CertificatesListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CertificatesListAdapter.MyViewHolder holder, int position) {

        final CertificateListDataItem profile = dateList.get(position);
        holder.lblCertificateType.setText(profile.getRequestedFor());
        holder.lblReason.setText(profile.getReason());
        holder.lblStatus.setText(profile.getIsIssuedOnApp());
        holder.lblCreatedOn.setText(profile.getCreatedOn());

        if( profile.getCertificateUrl() != null){
            if (!profile.getCertificateUrl().equals("")){
                holder.btnViewCertificate.setVisibility(View.VISIBLE);
            }
            else {
               holder.btnViewCertificate.setVisibility(View.GONE);
            }
        }
        else {
            holder.btnViewCertificate.setVisibility(View.GONE);
        }


        holder.btnViewCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent receipt = new Intent(context, PdfWebView.class);
                receipt.putExtra("URL",profile.getCertificateUrl());
                receipt.putExtra("tittle",profile.getRequestedFor());
                context.startActivity(receipt);

            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public Button btnViewCertificate;
        public TextView lblCertificateType, lblCreatedOn, lblReason, lblStatus;

        public MyViewHolder(View view) {
            super(view);
            lblCertificateType = (TextView) view.findViewById(R.id.lblCertificateType);
            lblCreatedOn = (TextView) view.findViewById(R.id.lblCreatedOn);
            lblReason = (TextView) view.findViewById(R.id.lblReason);
            lblStatus = (TextView) view.findViewById(R.id.lblStatus);
            btnViewCertificate = (Button) view.findViewById(R.id.btnViewCertificate);
        }
    }

    public CertificatesListAdapter(List<CertificateListDataItem> dateList, Context context) {
        this.context = context;
        this.dateList = dateList;
    }

    public void clearAllData() {
        int size = this.dateList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.dateList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void updateList(List<CertificateListDataItem> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }
}
