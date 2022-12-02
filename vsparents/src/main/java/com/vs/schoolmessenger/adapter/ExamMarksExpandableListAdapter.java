package com.vs.schoolmessenger.adapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Callback;

public class ExamMarksExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    List<String> mapParent;
    HashMap<String, ArrayList<String>> mapchild;

    public ExamMarksExpandableListAdapter(Context context, List<String> mapParent,
                                          HashMap<String, ArrayList<String>> mapchild) {
        this.context = context;
        this.mapParent = mapParent;
        this.mapchild = mapchild;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.mapParent.get(listPosition);
    }
    @Override
    public int getGroupCount() {
        return this.mapParent.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }
    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.exam_marks_list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.lblname);

        if(listTitle.contains("_1")){
            String[] value = listTitle.split("_");
            listTitleTextView.setText(value[0]);
            listTitleTextView.setTextColor(Color.parseColor("#e60b29")); // red color

        }
        else {
            String[] value = listTitle.split("_");
            listTitleTextView.setText(value[0]);
            listTitleTextView.setTextColor(Color.parseColor("#006064")); // primary color
        }
        return convertView;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.mapchild.get(this.mapParent.get(listPosition))
                .get(expandedListPosition);
    }
    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }
    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.exam_marks_list_view, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.lblsplitname);


        if(expandedListText.contains("_1")){
            String[] value = expandedListText.split("_");
            expandedListTextView.setText(value[0]);
            expandedListTextView.setTextColor(Color.parseColor("#e60b29")); // red color
        }
        else {
            String[] value = expandedListText.split("_");
            expandedListTextView.setText(value[0]);
            expandedListTextView.setTextColor(Color.parseColor("#006064")); // primary color

        }

        return convertView;
    }
    @Override
    public int getChildrenCount(int listPosition) {
        return this.mapchild.get(this.mapParent.get(listPosition)).size();
    }
    @Override
    public boolean hasStableIds() {

        return false;
    }
    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}