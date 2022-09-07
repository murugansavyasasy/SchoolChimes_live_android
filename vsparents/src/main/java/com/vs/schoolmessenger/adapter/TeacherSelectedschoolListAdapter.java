package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherOnSelectedStdGroupListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;


/**
 * Created by devi on 5/31/2017.
 */

public class TeacherSelectedschoolListAdapter extends RecyclerView.Adapter<TeacherSelectedschoolListAdapter.MyViewHolder> {

    private ArrayList<TeacherSchoolsModel> seletedschoollist;
    private TeacherOnCheckSchoolsListener onCheckSchoolsListener;
    private TeacherOnSelectedStdGroupListener onSelectedStdGroupListener;
    Context context;

    public static boolean bEditClick;

    public TeacherSelectedschoolListAdapter(Context context, TeacherOnCheckSchoolsListener onCheckSchoolsListener,
                                            TeacherOnSelectedStdGroupListener onSelectedStdGroupListener, ArrayList<TeacherSchoolsModel> seletedschoollist) {
        this.context = context;
        this.onCheckSchoolsListener = onCheckSchoolsListener;
        this.onSelectedStdGroupListener = onSelectedStdGroupListener;
        this.seletedschoollist = seletedschoollist;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_selected_school, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.bind(seletedschoollist.get(position));

        final TeacherSchoolsModel schl = seletedschoollist.get(position);
        holder.tvselschl.setText(schl.getStrSchoolName());

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSelectedStdGroupListener.classGrp_selectedClassesAndGroups(schl);
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    onCheckSchoolsListener.school_removeSchool(seletedschoollist.get(position));
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return seletedschoollist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvselschl;
        ImageView ivDelete;
        Button btnEdit;

        public MyViewHolder(View view) {
            super(view);
            tvselschl = (TextView) view.findViewById(R.id.school_li_tvSchoolName);

            ivDelete = (ImageView) view.findViewById(R.id.school_li_ivDelete);
            btnEdit = (Button) view.findViewById(R.id.school_li_btnEdit);

            if (TeacherUtil_SharedPreference.getLoginTypeFromSP((Activity) context).equals(LOGIN_TYPE_ADMIN))
                btnEdit.setVisibility(View.GONE);
            else btnEdit.setVisibility(View.VISIBLE);
        }

        public void bind(TeacherSchoolsModel sectionclass) {

        }
    }

    public void clearAllData() {
        int size = this.seletedschoollist.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                this.seletedschoollist.remove(0);
            }

            this.notifyItemRangeRemoved(0, size);
        }
    }
}
