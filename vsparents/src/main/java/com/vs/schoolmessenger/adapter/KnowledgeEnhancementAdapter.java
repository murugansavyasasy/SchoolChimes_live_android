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
import com.vs.schoolmessenger.activity.KnowledgeEnhancementQuestions;
import com.vs.schoolmessenger.activity.ViewQuizResult;
import com.vs.schoolmessenger.model.KnowledgeEnhancementModel;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;

public class KnowledgeEnhancementAdapter extends RecyclerView.Adapter<KnowledgeEnhancementAdapter.MyViewHolder> {

    private ArrayList<KnowledgeEnhancementModel> dateList;
    Context context;
    String type;

    @Override
    public KnowledgeEnhancementAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_enhancement, parent, false);

        return new KnowledgeEnhancementAdapter.MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final KnowledgeEnhancementAdapter.MyViewHolder holder, int position) {
        final KnowledgeEnhancementModel data = dateList.get(position);


        holder.lnrStarttime.setVisibility(View.VISIBLE);
        holder.lnrendtime.setVisibility(View.VISIBLE);
        holder.lnrexamdate.setVisibility(View.GONE);
        holder.lnrtimeque.setVisibility(View.GONE);

        holder.txt_date.setText(data.getCreatedOn());
        holder.txt_type.setText(data.getSentBy());
        holder.lblsubject.setText(data.getSubject());

        holder.lblSent.setText(data.getSentBy());
//        holder.lblmarkque.setText(String.valueOf(data.getMarkPerQuestion()));
        holder.lbltotalque.setText(data.getTotalNumberOfQuestions());
        holder.lblrightanswer.setText(data.getRightAnswer());
        holder.lblwrongans.setText(data.getWrongAnswer());
        holder.lbltotalmarks.setText(data.getTotalMark());
        holder.lblTextTitle.setText(data.getTitle());
        holder.lblDesc.setText(data.getDescription());

        holder.labelstarttime.setText("Level");
        holder.lblStarttime.setText("Level"+" "+data.getLevel());

        if(type.equals("1")){
            holder.labelendtime.setText("Number of Levels");
            holder.lblEndtime.setText(data.getNoOfLevels());
            holder.btnStart.setText("Get Questions");
            holder.lnrBtn.setVisibility(View.VISIBLE);
            holder.lnrRightAns.setVisibility(View.GONE);
            holder.lnrWrongAns.setVisibility(View.GONE);
            if(data.getIsAppRead().equals("0")){
                holder.lblNew.setVisibility(View.VISIBLE);
            }
            else{
                holder.lblNew.setVisibility(View.GONE);
            }
        }
        else{
            holder.btnStart.setText("View");
            holder.lnrendtime.setVisibility(View.GONE);
            holder.lnrexamdate.setVisibility(View.VISIBLE);
            holder.labelexamdate.setText("Submission on");
            holder.lblExamdate.setText(data.getSubmittedOn());
            holder.lnrBtn.setVisibility(View.VISIBLE);
            holder.lblNew.setVisibility(View.GONE);
            holder.lnrRightAns.setVisibility(View.VISIBLE);
            holder.lnrWrongAns.setVisibility(View.VISIBLE);
        }


        holder.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(type.equals("1")) {
                    Intent i = new Intent(context, KnowledgeEnhancementQuestions.class);
                    i.putExtra("Knowledge", data);
                    context.startActivity(i);
                }
                else{
                    Intent i = new Intent(context, ViewQuizResult.class);
                    i.putExtra("id", data.getQuizId());
                    context.startActivity(i);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

       TextView txt_date,txt_time,txt_type,lblsubject,lblExamdate,lblEndtime,lblStarttime,lblSent,
        lbltimeque,lblmarkque,lbltotalque,lblrightanswer,lblwrongans,lbltotalmarks,lblNew,
        lblTextTitle,lblDesc,labelstarttime,labelendtime,labelexamdate;
        Button btnStart;
        LinearLayout lnrSubject,lnrStarttime,lnrendtime,lnrexamdate,lnrSentby,lnrtimeque,lnrmarkque,lnrTotalque,
                lnrRightAns,lnrWrongAns,lnrTotalMarks,lnrBtn;




        public MyViewHolder(View view) {
            super(view);

            txt_date = (TextView) view.findViewById(R.id.txt_date);
            txt_time = (TextView) view.findViewById(R.id.txt_time);
            txt_type = (TextView) view.findViewById(R.id.txt_type);
            lblsubject = (TextView) view.findViewById(R.id.lblsubject);
            lblExamdate = (TextView) view.findViewById(R.id.lblExamdate);
            lblEndtime = (TextView) view.findViewById(R.id.lblEndtime);
            lblStarttime = (TextView) view.findViewById(R.id.lblStarttime);
            lblSent = (TextView) view.findViewById(R.id.lblSent);
            lbltimeque = (TextView) view.findViewById(R.id.lbltimeque);
            lblmarkque = (TextView) view.findViewById(R.id.lblmarkque);
            lbltotalque = (TextView) view.findViewById(R.id.lbltotalque);
            lblrightanswer = (TextView) view.findViewById(R.id.lblrightanswer);
            lblwrongans = (TextView) view.findViewById(R.id.lblwrongans);
            lbltotalmarks = (TextView) view.findViewById(R.id.lbltotalmarks);
            lblNew = (TextView) view.findViewById(R.id.lblNew);
            lblTextTitle = (TextView) view.findViewById(R.id.lblTextTitle);
            lblDesc = (TextView) view.findViewById(R.id.lblDesc);
            labelstarttime = (TextView) view.findViewById(R.id.labelstarttime);
            labelendtime = (TextView) view.findViewById(R.id.labelendtime);
            labelexamdate = (TextView) view.findViewById(R.id.labelexamdate);
            btnStart = (Button) view.findViewById(R.id.btnStart);

            lnrSubject = (LinearLayout) view.findViewById(R.id.lnrSubject);
            lnrStarttime = (LinearLayout) view.findViewById(R.id.lnrStarttime);
            lnrendtime = (LinearLayout) view.findViewById(R.id.lnrendtime);
            lnrexamdate = (LinearLayout) view.findViewById(R.id.lnrexamdate);
            lnrSentby = (LinearLayout) view.findViewById(R.id.lnrSentby);
            lnrtimeque = (LinearLayout) view.findViewById(R.id.lnrtimeque);
            lnrmarkque = (LinearLayout) view.findViewById(R.id.lnrmarkque);
            lnrTotalque = (LinearLayout) view.findViewById(R.id.lnrTotalque);
            lnrRightAns = (LinearLayout) view.findViewById(R.id.lnrRightAns);
            lnrWrongAns = (LinearLayout) view.findViewById(R.id.lnrWrongAns);
            lnrTotalMarks = (LinearLayout) view.findViewById(R.id.lnrTotalMarks);
            lnrBtn = (LinearLayout) view.findViewById(R.id.lnrBtn);

        }
    }

    public KnowledgeEnhancementAdapter(Context context, ArrayList<KnowledgeEnhancementModel> dateList, String type) {
        this.context = context;
        this.dateList = dateList;
        this.type = type;


    }





}
