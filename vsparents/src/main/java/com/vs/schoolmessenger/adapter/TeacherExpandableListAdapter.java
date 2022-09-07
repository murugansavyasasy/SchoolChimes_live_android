package com.vs.schoolmessenger.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherOnClassGroupItemCheckListener;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;

import java.util.ArrayList;
import java.util.Map;



/**
 * Created by devi on 6/1/2017.
 */

public class TeacherExpandableListAdapter extends BaseExpandableListAdapter {

    private Activity context;
    private Map<String, ArrayList<TeacherClassGroupModel>> groupclassCollections;
    private ArrayList<String> groupclass;
    ExpandableListView expListView;
    private TeacherOnClassGroupItemCheckListener onClassGroupItemCheckListener;

    public TeacherExpandableListAdapter(Activity context, ArrayList<String> groupclass,
                                        Map<String, ArrayList<TeacherClassGroupModel>> groupclassCollections,
                                        ExpandableListView expListView, TeacherOnClassGroupItemCheckListener onClassGroupItemCheckListener) {
        this.context = context;
        this.groupclassCollections = groupclassCollections;
        this.groupclass = groupclass;
        this.expListView = expListView;
        this.onClassGroupItemCheckListener = onClassGroupItemCheckListener;
    }

    public Object getChild(int groupPosition, int childPosition) {
        return groupclassCollections.get(groupclass.get(groupPosition)).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }


    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final TeacherClassGroupModel classGroup = (TeacherClassGroupModel) getChild(groupPosition, childPosition);

        LayoutInflater inflater = context.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.teacher_class_groups, null);
        }


        TextView item = (TextView) convertView.findViewById(R.id.class_group);

        final CheckBox checkitem = (CheckBox) convertView.findViewById(R.id.checkbox);
        checkitem.setOnCheckedChangeListener(null);
        checkitem.setChecked(classGroup.isbSelected());
        checkitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                classGroup.setbSelected(isChecked);
                if (checkitem.isChecked()) {
                    Log.d("EXP:Checked", classGroup.getStrName());
                    onClassGroupItemCheckListener.classGropItem_addClass(groupclass.get(groupPosition), classGroup);
                } else {
                    Log.d("EXP:UnChecked", classGroup.getStrName());
                    onClassGroupItemCheckListener.classGropItem_removeClass(groupclass.get(groupPosition), classGroup);
                }
            }
        });

        item.setText(classGroup.getStrName());
        return convertView;
    }

    public int getChildrenCount(int groupPosition) {
        return groupclassCollections.get(groupclass.get(groupPosition)).size();
    }

    public Object getGroup(int groupPosition) {
        return groupclass.get(groupPosition);
    }

    public int getGroupCount() {
        return groupclass.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String selectName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.teacher_group_item,
                    null);
        }




        TextView item = (TextView) convertView.findViewById(R.id.selectitem);
        ImageView image = (ImageView) convertView.findViewById(R.id.img);
        if (isExpanded) {
            image.setImageResource(R.drawable.teacher_arrow_down);
        } else {
            image.setImageResource(R.drawable.teacher_arrow_up);
        }
        item.setTypeface(null, Typeface.BOLD);
        item.setText(selectName);
        return convertView;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
