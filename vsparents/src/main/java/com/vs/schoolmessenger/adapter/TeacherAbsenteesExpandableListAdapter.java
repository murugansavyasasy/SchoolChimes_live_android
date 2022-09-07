package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherABS_Standard;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by devi on 6/1/2017.
 */


public class TeacherAbsenteesExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<TeacherABS_Standard, ArrayList<TeacherABS_Section>> dropDownListCollections;
    private ArrayList<TeacherABS_Standard> exHeaderTitle;
    ExpandableListView expListView;

    public TeacherAbsenteesExpandableListAdapter(Activity context, ArrayList<TeacherABS_Standard> exHeaderTitle,
                                                 Map<TeacherABS_Standard, ArrayList<TeacherABS_Section>> dropDownListCollections,
                                                 ExpandableListView expListView) {
        this.context = context;
        this.dropDownListCollections = dropDownListCollections;
        this.exHeaderTitle = exHeaderTitle;
        this.expListView = expListView;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return dropDownListCollections.get(exHeaderTitle.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TeacherABS_Section classGroup = (TeacherABS_Section) getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.teacher_absentees_ex_list_item, null);
        }


        TextView title = (TextView) convertView.findViewById(R.id.absExItem_tvTitle);
        TextView count = (TextView) convertView.findViewById(R.id.absExItem_tvCount);

        title.setText(classGroup.getSection());
        count.setText(classGroup.getCount());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return dropDownListCollections.get(exHeaderTitle.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return exHeaderTitle.get(groupPosition);
    }

    public int getGroupCount() {
        return exHeaderTitle.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String stdName = ((TeacherABS_Standard) getGroup(groupPosition)).getStandard();
        String stdCount = ((TeacherABS_Standard) getGroup(groupPosition)).getCount();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.teacher_absentees_ex_header,
                    null);
        }




        TextView hTitle = (TextView) convertView.findViewById(R.id.absExTitle_tvTitle);
        TextView hCount = (TextView) convertView.findViewById(R.id.absExTitle_tvCount);
        ImageView image = (ImageView) convertView.findViewById(R.id.absExTitle_ivArrow);
        if (isExpanded) {
            image.setImageResource(R.drawable.teacher_arrow_down);
        } else {
            image.setImageResource(R.drawable.teacher_arrow_right);
        }
        hTitle.setTypeface(null, Typeface.BOLD);
        hTitle.setText(stdName);
        hCount.setText(stdCount);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
