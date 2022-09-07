package com.vs.schoolmessenger.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.List;


/**
 * Created by voicesnap on 8/31/2016.
 */
public class TeacherSchoolListForPrincipalAdapter extends RecyclerView.Adapter<TeacherSchoolListForPrincipalAdapter.MyViewHolder> {

    private List<TeacherSchoolsModel> dateList;
    Context context;
    private final TeacherSchoolListPrincipalListener listener;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_principal_schools_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bind(dateList.get(position), listener);

        final TeacherSchoolsModel profile = dateList.get(position);

        holder.tvName.setText(profile.getStrSchoolName());
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName;

        public MyViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.princiSchool_tvName);
        }

        public void bind(final TeacherSchoolsModel item, final TeacherSchoolListPrincipalListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    public TeacherSchoolListForPrincipalAdapter(Context context, List<TeacherSchoolsModel> dateList, TeacherSchoolListPrincipalListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
    }
}
