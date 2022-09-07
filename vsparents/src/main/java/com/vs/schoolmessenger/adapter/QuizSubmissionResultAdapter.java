package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ExamEnhancementQuestions;
import com.vs.schoolmessenger.model.ExamEnhancement;
import com.vs.schoolmessenger.model.QuizSubmissions;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QuizSubmissionResultAdapter extends RecyclerView.Adapter<QuizSubmissionResultAdapter.MyViewHolder> {

    private ArrayList<QuizSubmissions> textDataList;

    private Context context;
    String type,ansid,answer;

    public QuizSubmissionResultAdapter(ArrayList<QuizSubmissions> textDataList, Context context, String type) {
        this.textDataList = textDataList;
        this.context = context;
        this.type = type;

    }

    @Override
    public QuizSubmissionResultAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.submissions_quiz_adapter, parent, false);

        return new QuizSubmissionResultAdapter.MyViewHolder(itemView);
    }



    @Override
    public int getItemCount() {
        return textDataList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView lblqueno,lblque,lblPDF,lblcorrectans;
        RadioGroup rgoptions;
        FrameLayout videoview;
        LinearLayout lnrPDFtext;
        ImageView imgVideo,imgShadow,imgplay,imgview;





        public MyViewHolder(View view) {
            super(view);

            lblqueno = (TextView) view.findViewById(R.id.lblqueno);
            lblque = (TextView) view.findViewById(R.id.lblque);
            rgoptions = (RadioGroup) view.findViewById(R.id.rgoptions);
            videoview = (FrameLayout) view.findViewById(R.id.videoview);
            imgVideo = (ImageView) view.findViewById(R.id.imgVideo);
            imgShadow = (ImageView) view.findViewById(R.id.imgShadow);
            imgplay = (ImageView) view.findViewById(R.id.imgplay);
            imgview = (ImageView) view.findViewById(R.id.imgview);
            lnrPDFtext = (LinearLayout) view.findViewById(R.id.lnrPDFtext);
            lblPDF = (TextView) view.findViewById(R.id.lblPDF);
            lblcorrectans = (TextView) view.findViewById(R.id.lblcorrectans);

        }

//        public void bind(final OnlineClassByStaffModel item, final OnlineClassStaffListener listener) {
//
//        }
    }
    @Override
    public void onBindViewHolder(final QuizSubmissionResultAdapter.MyViewHolder holder, final int position) {

        final QuizSubmissions msgModel = textDataList.get(position);

        holder.lblqueno.setText(position+1+" .");
        holder.lblque.setText(msgModel.getQuestion());
        holder.lblcorrectans.setText("Correct Answer : "+msgModel.getCorrectAnswer());


        ArrayList<String>answers=new ArrayList<>();
        answers.clear();
        answers.add(msgModel.getaOption());
        answers.add(msgModel.getbOption());
        answers.add(msgModel.getcOption());
        answers.add(msgModel.getdOption());

        holder.rgoptions.removeAllViews();

        for (int i = 0; i < answers.size(); i++) {

            String[] id = new String[0];
            id = answers.get(i).toString().split("~");
            ansid = id[0];
            Log.d("ansid", ansid);
            answer = id[1];
            Log.d("answer", answer);
            RadioButton radioButton = new RadioButton(context);

            radioButton.setText(answer);
            radioButton.setId(Integer.parseInt(ansid));
            radioButton.setTextSize(16);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setEnabled(false);
            radioButton.setPadding(1, 10, 1, 1);

            if(radioButton.getId()==Integer.parseInt(msgModel.getStudentAnswer())){
                if(msgModel.getStudentAnswer().equals(msgModel.getAnswer())) {
                    radioButton.setChecked(true);
                    radioButton.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.colorPrimaryDark)));

                }
                else{
                    radioButton.setChecked(true);
                    radioButton.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bpRed)));

                }
            }
            holder.rgoptions.addView(radioButton);

        }






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

