package com.vs.schoolmessenger.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.ExamDateListClass;
import com.vs.schoolmessenger.model.ExamGroupHeader;

import java.util.HashMap;
import java.util.List;

public class ExamSubjectsExpandableAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<ExamGroupHeader> _listDataHeader; // header titles
    private HashMap<ExamGroupHeader, List<ExamDateListClass>> _listDataChild;



    public ExamSubjectsExpandableAdapter(Context context, List<ExamGroupHeader> listDataHeader,
                                         HashMap<ExamGroupHeader, List<ExamDateListClass>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {


        final ExamDateListClass exam = (ExamDateListClass) getChild(groupPosition,childPosition);


        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.subject_sub_list_item, null);
        }

        TextView lblSubjectName = (TextView) convertView.findViewById(R.id.lblSubjectName);
        TextView lblExamDate = (TextView) convertView.findViewById(R.id.lblExamDate);
        TextView lblSession = (TextView) convertView.findViewById(R.id.lblSession);
        TextView lblMaxMark = (TextView) convertView.findViewById(R.id.lblMaxMark);
        TextView lblSubjectSyllabus = (TextView) convertView.findViewById(R.id.lblSubjectSyllabus);
        RelativeLayout rytSubjectSyllabus = (RelativeLayout) convertView.findViewById(R.id.rytSubjectSyllabus);


        lblSubjectName.setText(exam.getSubName());
        lblExamDate.setText(exam.getExamDate());
        lblSession.setText(exam.getExamSession());
        lblMaxMark.setText(exam.getMaxmark());

        if(!exam.getSubjectSyllabus().equals("")) {
            rytSubjectSyllabus.setVisibility(View.VISIBLE);
            lblSubjectSyllabus.setText(exam.getSubjectSyllabus());
        }
        else {
            rytSubjectSyllabus.setVisibility(View.GONE);

        }

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {


        final ExamGroupHeader head = (ExamGroupHeader) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.subject_group, null);
        }

        TextView lblExamname = (TextView) convertView.findViewById(R.id.lblExamname);
        TextView lblSyllabusName = (TextView) convertView.findViewById(R.id.lblSyllabusName);
        ImageView image = (ImageView) convertView.findViewById(R.id.absExTitle_ivArrow);

        if (isExpanded) {
            image.setImageResource(R.drawable.teacher_arrow_down);
        } else {
            image.setImageResource(R.drawable.teacher_arrow_right);
        }
        lblExamname.setTypeface(null, Typeface.BOLD);
        lblSyllabusName.setTypeface(null, Typeface.BOLD);

        lblExamname.setText(head.getExamName());
        lblSyllabusName.setText(head.getSyllabus());

        return convertView;
    }




    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void updateList(List<ExamGroupHeader> temp) {
        _listDataHeader = temp;
        notifyDataSetChanged();
    }
}