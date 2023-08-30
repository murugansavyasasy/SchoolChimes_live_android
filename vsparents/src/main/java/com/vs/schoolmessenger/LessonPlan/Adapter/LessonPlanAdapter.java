package com.vs.schoolmessenger.LessonPlan.Adapter;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_LESSON_PLAN;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.LessonPlan.Activity.ViewLessonPlanActivity;
import com.vs.schoolmessenger.LessonPlan.Model.LessPlanData;
import com.vs.schoolmessenger.R;

import org.w3c.dom.Text;

import java.security.PublicKey;
import java.util.List;

public class LessonPlanAdapter extends RecyclerView.Adapter<LessonPlanAdapter.MyViewHolder> {

    private List<LessPlanData> dateList;
    Context context;
    int requestCode;

    private int progressStatus=0;
    Point maxSizePoint = new Point();
    final int maxX = maxSizePoint.x;


    @Override
    public LessonPlanAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lesson_plan_list_item, parent, false);

        return new LessonPlanAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final LessonPlanAdapter.MyViewHolder holder, int position) {

        final LessPlanData profile = dateList.get(position);
        holder.lblStaffName.setText(profile.getStaffName());
        holder.lblClassName.setText(profile.getClassName());
        holder.lblSectionName.setText(" - "+profile.getSectionName());
        holder.lblSubjectName.setText(profile.getSubjectName());
        holder.lblStatus.setText(profile.getItemsCompleted());

        holder.lblStaff.setTypeface(holder.lblStaff.getTypeface(), Typeface.BOLD);
        holder.lblCompleted.setTypeface(holder.lblCompleted.getTypeface(), Typeface.BOLD);

        int total_items = 0;
        total_items = Integer.parseInt(profile.getTotalItems());

        if(total_items != 0) {
            int percentage = Integer.parseInt(profile.getPercentageValue());
            holder.progressBar.setProgress(percentage);
            holder.lblPercentage.setText(String.valueOf(percentage) + "%");


            holder.lblPercent.setText(String.valueOf(percentage) + "%");
            ((LinearLayout.LayoutParams)holder.lblZero.getLayoutParams()).weight = (float) 10;

            if(percentage == 100){
                holder.lblPercent.setVisibility(View.GONE);
                ((LinearLayout.LayoutParams)holder.lblEmptyWhite.getLayoutParams()).weight = (float) 90;
            }
            else  if(percentage == 0){
                holder.lblPercent.setVisibility(View.GONE);
                ((LinearLayout.LayoutParams)holder.lblZero.getLayoutParams()).weight = (float) 10;
                ((LinearLayout.LayoutParams)holder.lblEmptyWhite.getLayoutParams()).weight = (float) 90;
            }
            else {
                holder.lblPercent.setVisibility(View.VISIBLE);
                ((LinearLayout.LayoutParams)holder.lblPercent.getLayoutParams()).weight = (float) percentage-10;
                ((LinearLayout.LayoutParams)holder.lblEmptyWhite.getLayoutParams()).weight = (float) 100-percentage-10;
            }

        }
        else {

            holder.lblPercent.setVisibility(View.GONE);
            ((LinearLayout.LayoutParams)holder.lblZero.getLayoutParams()).weight = (float) 10;
            ((LinearLayout.LayoutParams)holder.lblEmptyWhite.getLayoutParams()).weight = (float) 90;

            holder.progressBar.setProgress(0);
            holder.lblPercentage.setText("0" + "%");
        }

        if(profile.getTotalItems().equals("0")){
            holder.btnViewLesson.setVisibility(View.GONE);
        }
        else {
            holder.btnViewLesson.setVisibility(View.VISIBLE);
        }

        holder.btnViewLesson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ViewLessonPlanActivity.class);
                i.putExtra("section_subject_id", profile.getSectionSubjectId());
                i.putExtra("REQUEST_CODE", requestCode);

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public Button btnViewLesson;
        public TextView lblStaffName, lblClassName, lblSectionName, lblSubjectNo, lblTotalItems, lblCompletedItems, lblOutOfCompleted,
                lblPercentage,lblStatus,lblCompleted,lblStaff,lblSubjectName;

        public TextView   lblZero,lblPercent,lblEmptyWhite;


        public ProgressBar progressBar;

        public MyViewHolder(View view) {
            super(view);

            lblStaffName = (TextView) view.findViewById(R.id.lblStaffName);
            lblClassName = (TextView) view.findViewById(R.id.lblClassName);
            lblSectionName = (TextView) view.findViewById(R.id.lblSectionName);
            lblSubjectName = (TextView) view.findViewById(R.id.lblSubjectName);
            lblStatus = (TextView) view.findViewById(R.id.lblStatus);
            lblCompleted = (TextView) view.findViewById(R.id.lblCompleted);
            lblStaff = (TextView) view.findViewById(R.id.lblStaff);
            lblPercentage = (TextView) view.findViewById(R.id.lblPercentage);
            btnViewLesson = (Button) view.findViewById(R.id.btnViewLesson);
            progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

            lblZero = (TextView) view.findViewById(R.id.lblZero);
            lblPercent = (TextView) view.findViewById(R.id.lblPercent);
            lblEmptyWhite = (TextView) view.findViewById(R.id.lblEmptyWhite);


        }
    }

    public LessonPlanAdapter(List<LessPlanData> dateList,int requestCode, Context context) {
        this.context = context;
        this.dateList = dateList;
        this.requestCode = requestCode;
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

    public void updateList(List<LessPlanData> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }
}
