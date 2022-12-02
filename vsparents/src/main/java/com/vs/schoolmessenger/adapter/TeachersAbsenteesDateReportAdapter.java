package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherAbsenteesDateListener;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;

import java.util.List;



/**
 * Created by voicesnap on 8/31/2016.
 */
public class TeachersAbsenteesDateReportAdapter extends RecyclerView.Adapter<TeachersAbsenteesDateReportAdapter.MyViewHolder> {

    private List<TeacherAbsenteesDates> dateList;
    Context context;
    private final TeacherAbsenteesDateListener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_cardview_absentees_date_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);

        final TeacherAbsenteesDates profile = dateList.get(position);


        holder.tvDate.setText(profile.getDate());
        holder.tvDay.setText(profile.getDay());
        holder.tvCount.setText(profile.getCount());
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvDay, tvCount;

        public MyViewHolder(View view) {
            super(view);

            tvDate = (TextView) view.findViewById(R.id.cardAbsen_tvDate);
            tvDay = (TextView) view.findViewById(R.id.cardAbsen_tvDay);
            tvCount = (TextView) view.findViewById(R.id.cardAbsen_tvCount);
        }

        public void bind(final TeacherAbsenteesDates item, final TeacherAbsenteesDateListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public TeachersAbsenteesDateReportAdapter(Context context, List<TeacherAbsenteesDates> dateList, TeacherAbsenteesDateListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }

    public void updateList(List<TeacherAbsenteesDates> temp) {
        dateList = temp;
        notifyDataSetChanged();
    }


    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}
