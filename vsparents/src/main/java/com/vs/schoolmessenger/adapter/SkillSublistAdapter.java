package com.vs.schoolmessenger.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PreviewLSRWScreen;
import com.vs.schoolmessenger.model.SkillAttachmentModel;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import static android.view.View.GONE;
public class SkillSublistAdapter extends RecyclerView.Adapter<SkillSublistAdapter.MyViewHolder> {

    private List<SkillAttachmentModel> dateList;
    Context context;

    @Override
    public SkillSublistAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lsrw_adapter_design, parent, false);

        return new SkillSublistAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final SkillSublistAdapter.MyViewHolder holder, int position) {

        final SkillAttachmentModel data = dateList.get(position);
        holder.labeltext.setText("Attachment");
        holder.labeldesc.setText("Type");
        holder.btnView.setText("View Attachment");


        if(data.getType().equals("TEXT")) {
            holder.lblTitle.setText(data.getAttachment());
        }
        else {
            holder.lblTitle.setText(String.valueOf(data.getOrder()));
        }

        holder.lblDescription.setText(data.getType());
        holder.lnrLSRW.setVisibility(View.VISIBLE);
        holder.btnView.setVisibility(View.VISIBLE);
        holder.lblNew.setVisibility(GONE);
        holder.lnrSubject.setVisibility(GONE);
        holder.lnrSentBy.setVisibility(GONE);
        holder.lnrSubmittedON.setVisibility(GONE);

        holder.btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!data.getType().equals("PDF")) {
                    Intent i = new Intent(context, PreviewLSRWScreen.class);
                    i.putExtra("attachement", data);
                    context.startActivity(i);
                }else{
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url="+data.getAttachment()));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    browserIntent.setPackage("com.android.chrome");
                    context.startActivity(browserIntent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblTitle, lblDescription, lblSubmittedOn, lblSubject, lblSentBy,labeldesc,labeltext,lblNew;
        public Button btnView;
        LinearLayout lnrSubject, lnrSubmittedON, lnrSentBy,lnrLSRW;
        RelativeLayout rytOverall;

        public MyViewHolder(View view) {
            super(view);

            lblTitle = (TextView) view.findViewById(R.id.lblTitle);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            lblDescription = (TextView) view.findViewById(R.id.lblDescription);
            lblSubmittedOn = (TextView) view.findViewById(R.id.lblSubmittedOn);
            lblSubject = (TextView) view.findViewById(R.id.lblSubject);
            labeldesc = (TextView) view.findViewById(R.id.labeldesc);
            labeltext = (TextView) view.findViewById(R.id.labeltext);
            lblSentBy = (TextView) view.findViewById(R.id.lblSentBy);
            btnView = (Button) view.findViewById(R.id.btnView);
            rytOverall = (RelativeLayout) view.findViewById(R.id.rytOverall);
            lnrSubject = (LinearLayout) view.findViewById(R.id.lnrSubject);
            lnrSubmittedON = (LinearLayout) view.findViewById(R.id.lnrSubmittedON);
            lnrLSRW = (LinearLayout) view.findViewById(R.id.lnrLSRW);
            lnrSentBy = (LinearLayout) view.findViewById(R.id.lnrSentBy);



        }
    }

    public SkillSublistAdapter(Context context, List<SkillAttachmentModel> dateList) {
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


