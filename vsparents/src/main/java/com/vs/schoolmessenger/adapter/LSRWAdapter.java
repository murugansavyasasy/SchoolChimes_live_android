package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SkillSubListActivity;
import com.vs.schoolmessenger.model.lsrwModelClass;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class LSRWAdapter extends RecyclerView.Adapter<LSRWAdapter.MyViewHolder> {

    private List<lsrwModelClass> dateList;
    Context context;

    @Override
    public LSRWAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lsrw_adapter_design, parent, false);

        return new LSRWAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final LSRWAdapter.MyViewHolder holder, int position) {

        final lsrwModelClass data = dateList.get(position);

        holder.lblTitle.setText(data.getTitle());
        holder.lblDescription.setText(data.getDescription());
        holder.lblSubmittedOn.setText(data.getSubmittedOn());
        holder.lblSubject.setText(data.getSubject());
        holder.lblSentBy.setText(data.getSentBy());

        if(data.getIsAppRead().equals("0")){
            holder.lblNew.setVisibility(View.VISIBLE);
        }
        else{
            holder.lblNew.setVisibility(View.GONE);
        }
        if (data.getIssubmitted().equals("0")) {
            holder.lnrLSRW.setVisibility(View.VISIBLE);
            holder.btnView.setText(data.getActivityType());
            holder.lnrSubmittedON.setVisibility(View.GONE);
        } else {
            holder.lnrLSRW.setVisibility(View.GONE);
            holder.lnrSubmittedON.setVisibility(View.VISIBLE);

        }

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SkillSubListActivity.class);
                i.putExtra("skillID",data.getSkillId());
                i.putExtra("isappread",data.getIsAppRead());
                i.putExtra("detailid",data.getDetailId());

                context.startActivity(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblTitle, lblDescription, lblSubmittedOn, lblSubject, lblSentBy,lblNew;
        public Button btnView;
        LinearLayout lnrLSRW,lnrSubmittedON;

        public MyViewHolder(View view) {
            super(view);

            lblTitle = (TextView) view.findViewById(R.id.lblTitle);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            lblSubmittedOn = (TextView) view.findViewById(R.id.lblSubmittedOn);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            lblSentBy = (TextView) view.findViewById(R.id.lblSentBy);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            btnView = (Button) view.findViewById(R.id.btnView);
            lnrLSRW = (LinearLayout) view.findViewById(R.id.lnrLSRW);
            lnrSubmittedON = (LinearLayout) view.findViewById(R.id.lnrSubmittedON);


        }
    }

    public LSRWAdapter(Context context, List<lsrwModelClass> dateList) {
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


}

