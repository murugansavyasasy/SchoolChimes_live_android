package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ExamEnhancementQuestions;
import com.vs.schoolmessenger.interfaces.QuestionClickListener;
import com.vs.schoolmessenger.model.ExamEnhancement;
import com.vs.schoolmessenger.model.QuestionForQuiz;

import java.util.ArrayList;

public class QuestionForQuizAdapter extends RecyclerView.Adapter<QuestionForQuizAdapter.MyViewHolder> {

    private ArrayList<QuestionForQuiz> textDataList;
    private Context context;
    int positionsel;
    QuestionClickListener menuClickListener;
    private int selectedPos = -1;
    public QuestionForQuizAdapter(ArrayList<QuestionForQuiz> textDataList, Context context, int positionsel,QuestionClickListener menuClickListener) {
        this.textDataList = textDataList;
        this.context = context;
        this.positionsel = positionsel;
        this.menuClickListener = menuClickListener;

    }

    @Override
    public QuestionForQuizAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_horizontal_num, parent, false);

        return new QuestionForQuizAdapter.MyViewHolder(itemView);
    }



    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblno;
        RelativeLayout lnrbgnum;




        public MyViewHolder(View view) {
            super(view);

            lblno = (TextView) view.findViewById(R.id.lblno);
            lnrbgnum = (RelativeLayout) view.findViewById(R.id.lnrbgnum);

        }

        public void bind(final QuestionForQuiz menuModel, final QuestionClickListener menuClickListener) {
            menuModel.setSelectedstatus(false);

            if(positionsel!=-1) {
                if (selectedPos == -1 && positionsel == getAdapterPosition()) {
                    selectedPos = positionsel;
                    menuModel.setSelectedstatus(true);
                    menuClickListener.addclass(menuModel, positionsel);
                    positionsel = -1;
                }
            }


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    menuModel.setSelectedstatus(true);
                    if (textDataList != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            menuClickListener.addclass(menuModel,position);
                            notifyItemChanged(selectedPos);
                            selectedPos =getAdapterPosition();
                            notifyItemChanged(selectedPos);
                        }

                    }
                }
            });



        }
        public void changeToSelect(int resid) {
            lnrbgnum.setBackgroundResource(resid);

        }
        public void changeToSelectText(int colour) {
            lblno.setTextColor(colour);

        }
    }
    @Override
    public void onBindViewHolder(final QuestionForQuizAdapter.MyViewHolder holder, final int position) {

        final QuestionForQuiz msgModel = textDataList.get(position);


//        int positiondisc=position+1;
//        String no= String.valueOf(positiondisc);
        holder.lblno.setText(msgModel.Questionnum);

        holder.bind(msgModel,menuClickListener);

        holder.changeToSelect(selectedPos == position ? R.drawable.bg_que_dark : R.drawable.bg_que_light);

        holder.changeToSelectText(selectedPos == position ? Color.parseColor("#ffffff"): Color.parseColor("#000000"));

//        if(msgModel.isSelectedstatus()==false){
//
//            menuClickListener.removeclass(msgModel);
//            holder.lblno.setTextColor( Color.parseColor("#000000"));
//            holder.lnrbgnum.setBackgroundResource(R.drawable.bg_que_light);
//        }
//        if(msgModel.isSelectedstatus()==true){
//            menuClickListener.addclass(msgModel,position);
//            holder.lblno.setTextColor( Color.parseColor("#ffffff"));
//            holder.lnrbgnum.setBackgroundResource(R.drawable.bg_que_dark);
//        }

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

