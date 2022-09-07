package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ExamEnhancementQuestions;
import com.vs.schoolmessenger.activity.ViewQuizResult;
import com.vs.schoolmessenger.fragments.UpcomingExamEnhancement;
import com.vs.schoolmessenger.model.ExamEnhancement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ExamEnhancementAdapter extends RecyclerView.Adapter<ExamEnhancementAdapter.MyViewHolder> {

    private ArrayList<ExamEnhancement> textDataList;
    private Context context;
    String type;

    public ExamEnhancementAdapter(ArrayList<ExamEnhancement> textDataList, Context context, String type) {
        this.textDataList = textDataList;
        this.context = context;
        this.type = type;

    }

    @Override
    public ExamEnhancementAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exam_enhancement, parent, false);

        return new ExamEnhancementAdapter.MyViewHolder(itemView);
    }



    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date,txt_time,txt_type,lblsubject,lblExamdate,lblEndtime,lblStarttime,lblSent,
                lbltimeque,lblmarkque,lbltotalque,lblrightanswer,lblwrongans,lbltotalmarks,lblNew,
                lblTextTitle,lblDesc,labelstarttime;
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

//        public void bind(final OnlineClassByStaffModel item, final OnlineClassStaffListener listener) {
//
//        }
    }
    @Override
    public void onBindViewHolder(final ExamEnhancementAdapter.MyViewHolder holder, final int position) {

        final ExamEnhancement msgModel = textDataList.get(position);

        try {
            String _24HourTime = msgModel.getExamStartTime();
            String _24HourTime1= msgModel.getExamEndTime();
            String _24HourTimereadque= msgModel.getTimeForQuestionReading();
            SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
            SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
            Date _24HourDt = _24HourSDF.parse(_24HourTime);
            Date _24HourDt1 = _24HourSDF.parse(_24HourTime1);
            Date _24HourDtreadque = _24HourSDF.parse(_24HourTimereadque);
            System.out.println(_24HourDt);
            System.out.println(_12HourSDF.format(_24HourDt));
            System.out.println(_12HourSDF.format(_24HourDt1));

            holder.lblStarttime.setText(_12HourSDF.format(_24HourDt));
            holder.lblEndtime.setText(_12HourSDF.format(_24HourDt1));
            holder.lbltimeque.setText(_12HourSDF.format(_24HourDtreadque));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.txt_date.setText(msgModel.getCreatedOn());
        holder.txt_type.setText(msgModel.getSentBy());
        holder.lblsubject.setText(msgModel.getSubject());
        holder.lblExamdate.setText(msgModel.getExamDate());

        holder.lblSent.setText(msgModel.getSentBy());

        holder.lbltotalque.setText(msgModel.getTotalNumberOfQuestions());
        holder.lblrightanswer.setText(msgModel.getRightAnswer());
        holder.lblwrongans.setText(msgModel.getWrongAnswer());
        holder.lbltotalmarks.setText(msgModel.getTotalMark());
        holder.lblTextTitle.setText(msgModel.getTitle());
        holder.lblDesc.setText(msgModel.getDescription());


        //type ==1 Upcoming
        //type ==2 Completed
        //type ==3 Expired
        if(type.equals("1")){
            holder.lnrBtn.setVisibility(View.VISIBLE);
            holder.lnrRightAns.setVisibility(View.GONE);
            holder.lnrWrongAns.setVisibility(View.GONE);
            if(msgModel.getIsAppRead().equals("0")){
                holder.lblNew.setVisibility(View.VISIBLE);
            }
            else {
                holder.lblNew.setVisibility(View.GONE);
            }
        }
        if(type.equals("2")){
            holder.lnrBtn.setVisibility(View.VISIBLE);
            holder.lnrRightAns.setVisibility(View.VISIBLE);
            holder.lnrWrongAns.setVisibility(View.VISIBLE);
            holder.lnrStarttime.setVisibility(View.VISIBLE);
            holder.lnrendtime.setVisibility(View.GONE);
            holder.lnrtimeque.setVisibility(View.GONE);
            holder.lblNew.setVisibility(View.GONE);
            holder.labelstarttime.setText("Submission on");
            holder.lblStarttime.setText(msgModel.getSubmittedOn());
            holder.btnStart.setText("View");
        }
        if(type.equals("3")){
            holder.lnrBtn.setVisibility(View.GONE);
            holder.lnrRightAns.setVisibility(View.GONE);
            holder.lnrWrongAns.setVisibility(View.GONE);
            holder.lnrStarttime.setVisibility(View.GONE);
            holder.lnrendtime.setVisibility(View.GONE);
            holder.lnrtimeque.setVisibility(View.GONE);
            holder.lnrmarkque.setVisibility(View.GONE);
            holder.lblNew.setVisibility(View.GONE);
        }

        if(msgModel.getIsShow().equals("1")){
            holder.lnrRightAns.setVisibility(View.VISIBLE);
            holder.lnrWrongAns.setVisibility(View.VISIBLE);
            holder.lnrTotalMarks.setVisibility((View.VISIBLE));
        }
        else {
            holder.lnrRightAns.setVisibility(View.GONE);
            holder.lnrWrongAns.setVisibility(View.GONE);
            holder.lnrTotalMarks.setVisibility((View.GONE));
        }


            holder.btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(type.equals("1")) {
                        Intent i = new Intent(context, ExamEnhancementQuestions.class);
                        i.putExtra("Exam", msgModel);
                        i.putExtra("quetime", holder.lbltimeque.getText().toString());
                        context.startActivity(i);
                    }
                    else{
                        holder.btnStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(context, ViewQuizResult.class);
                                i.putExtra("id", msgModel.getQuizId());

                                context.startActivity(i);

                            }
                        });
                    }

                }
            });





    }
    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void clearAllData() {
        int size = this.textDataList.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.textDataList.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}

