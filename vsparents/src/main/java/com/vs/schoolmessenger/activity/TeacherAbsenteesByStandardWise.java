package com.vs.schoolmessenger.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherAbsenteesExpandableListAdapter;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherABS_Standard;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


public class TeacherAbsenteesByStandardWise extends AppCompatActivity {

    ExpandableListView expListView;
    Map<TeacherABS_Standard, ArrayList<TeacherABS_Section>> mapStandardsAndGroups;
    ArrayList<TeacherABS_Standard> listHeaderParent = new ArrayList<>();
    TeacherAbsenteesExpandableListAdapter absenteesExpandableListAdapter;

    ArrayList<TeacherABS_Section> listClassGroupsCollection;
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_absentees_by_standard_wise);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.absentess_report);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.standard_wise);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);

        expListView = (ExpandableListView) findViewById(R.id.absEx_exlvList);
        listHeaderParent = (ArrayList<TeacherABS_Standard>) getIntent().getSerializableExtra(
                "LIST_STDS");

        loadStdSections();
        absenteesExpandableListAdapter = new TeacherAbsenteesExpandableListAdapter(
                this, listHeaderParent, mapStandardsAndGroups, expListView);

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        expListView.setAdapter(absenteesExpandableListAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadSubList(ArrayList<TeacherABS_Section> laptopModels) {
        listClassGroupsCollection = new ArrayList<>();
        for (TeacherABS_Section model : laptopModels)
            listClassGroupsCollection.add(model);
    }


    private void loadStdSections() {
        mapStandardsAndGroups = new LinkedHashMap<>();
        for (TeacherABS_Standard groupclass : listHeaderParent) {
            loadSubList(groupclass.getSections());
            mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
        }
    }
}
