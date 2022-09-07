package com.vs.schoolmessenger.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.Group;
import com.vs.schoolmessenger.model.Subgroup;
import java.util.List;
public class SubGroupExamListAdapter extends
        RecyclerView.Adapter<SubGroupExamListAdapter.MyViewHolder> {

    private List<Subgroup> subgrouplist;
    Context context;

    public SubGroupExamListAdapter(Context context, List<Subgroup> subgrouplist) {
        this.context=context;
        this.subgrouplist=subgrouplist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView lblname,lblmark,lblgroupname,lblgroupmark;
        LinearLayout lnrgroup;
        RecyclerView rcySubgroups;

        public MyViewHolder(View view) {
            super(view);
            lblname = (TextView) view.findViewById(R.id.lblname);
            lblmark = (TextView) view.findViewById(R.id.lblmark);
            rcySubgroups=view.findViewById(R.id.rcysubgroups);

        }
    }
    @Override
    public SubGroupExamListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())

                .inflate(R.layout.elements_exam_marks, parent, false);
        return new SubGroupExamListAdapter.MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final SubGroupExamListAdapter.MyViewHolder holder, final
    int position) {
        Subgroup subgroup=subgrouplist.get(position);
        holder.lblname.setText(subgroup.getName());
        holder.lblmark.setText(subgroup.getMark());
        holder.rcySubgroups.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        return subgrouplist.size();
    }

}