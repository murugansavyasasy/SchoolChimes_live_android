package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.Group;
import com.vs.schoolmessenger.model.Subgroup;
import java.util.List;
public class GroupExamListAdapter extends
        RecyclerView.Adapter<GroupExamListAdapter.MyViewHolder> {
    private List<Group> groupList;
    private List<Subgroup> subgrouplist;
    SubGroupExamListAdapter subGroupExamListAdapter;
    Context context;

    public GroupExamListAdapter(Context context, List<Group> groupList) {
        this.context=context;
        this.groupList=groupList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblname,lblmark;
        RecyclerView rcySubgroups;

        public MyViewHolder(View view) {
            super(view);
            lblname = (TextView) view.findViewById(R.id.lblname);
            lblmark = (TextView) view.findViewById(R.id.lblmark);
            rcySubgroups=view.findViewById(R.id.rcysubgroups);

        }
    }
    @Override
    public GroupExamListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int
            viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.elements_exam_marks, parent, false);
        return new GroupExamListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final GroupExamListAdapter.MyViewHolder holder, final int
            position) {

        Group group=groupList.get(position);
        holder.lblname.setTypeface(null, Typeface.BOLD);
        holder.lblmark.setTypeface(null, Typeface.BOLD);
        holder.lblname.setText(group.getmGroupname());
        holder.lblmark.setText(group.getmGroupmark());
        subgrouplist=group.getSubgroups();
        holder.rcySubgroups.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManagergroup = new
                LinearLayoutManager(context.getApplicationContext());
        holder.rcySubgroups.setLayoutManager(layoutManagergroup);
        holder.rcySubgroups.setItemAnimator(new DefaultItemAnimator());
        subGroupExamListAdapter = new SubGroupExamListAdapter(context,subgrouplist);
        holder.rcySubgroups.setAdapter(subGroupExamListAdapter);

    }
    @Override
    public int getItemCount() {
        return groupList.size();
    }

}