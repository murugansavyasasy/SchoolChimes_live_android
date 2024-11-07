package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.List;


/**
 * Created by voicesnap on 8/31/2016.
 */
public class TeacherSchoolListForPrincipalAdapter extends RecyclerView.Adapter<TeacherSchoolListForPrincipalAdapter.MyViewHolder> {

    private List<TeacherSchoolsModel> dateList;
    Context context;
    private final TeacherSchoolListPrincipalListener listener;
    Boolean isMsgFromManagement;
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

        if (isMsgFromManagement) {
            for (int i = 0; i < Util_Common.isStaffMsgMangeCount.size(); i++) {
                if (String.valueOf(Util_Common.isStaffMsgMangeCount.get(i).getSCHOOLID()).equals(String.valueOf(profile.getStrSchoolID()))) {
                    if (!Util_Common.isStaffMsgMangeCount.get(i).getOVERALLCOUNT().equals("0")) {
                        holder.countMsgFromMangement.setVisibility(View.VISIBLE);
                        holder.countMsgFromMangement.setText(String.valueOf(Util_Common.isStaffMsgMangeCount.get(i).getOVERALLCOUNT()));
                    } else {
                        holder.countMsgFromMangement.setVisibility(View.GONE);
                    }
                }
            }
        }

        holder.tvName.setText(profile.getStrSchoolName());
        holder.princiSchool_tvRegionalName.setText(profile.getSchoolNameRegional());

        if (!profile.getSchoolNameRegional().equals("") && profile.getSchoolNameRegional() != null && !profile.getSchoolNameRegional().equals("null")) {
            holder.princiSchool_tvRegionalName.setVisibility(View.VISIBLE);
        } else {
            holder.princiSchool_tvRegionalName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, princiSchool_tvRegionalName, countMsgFromMangement;

        public MyViewHolder(View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.princiSchool_tvName);
            countMsgFromMangement = (TextView) view.findViewById(R.id.countMsgFromMangement);
            princiSchool_tvRegionalName = (TextView) view.findViewById(R.id.princiSchool_tvRegionalName);
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

    public TeacherSchoolListForPrincipalAdapter(Context context, List<TeacherSchoolsModel> dateList, Boolean isMsgFromManagement, TeacherSchoolListPrincipalListener listener) {
        this.context = context;
        this.dateList = dateList;
        this.listener = listener;
        this.isMsgFromManagement = isMsgFromManagement;
    }
}
