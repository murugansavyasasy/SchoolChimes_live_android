package com.vs.schoolmessenger.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherExpandableListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherOnClassGroupItemCheckListener;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.vs.schoolmessenger.activity.TeacherListAllSection.SELECTED_STD_SEC_POSITION;
import static com.vs.schoolmessenger.adapter.TeacherSelectedschoolListAdapter.bEditClick;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LIST_GROUPS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LIST_STANDARDS;


public class TeacherExtandablegroupclasslist extends AppCompatActivity {

    ExpandableListView expListView;
    Button btnConfirm, btnRemove;

    TeacherSchoolsModel schoolStdGroups;
    ArrayList<TeacherClassGroupModel> listGropus = new ArrayList<>();
    ArrayList<TeacherClassGroupModel> listStandards = new ArrayList<>();

    ArrayList<TeacherClassGroupModel> listGropusSELECTED = new ArrayList<>();
    ArrayList<TeacherClassGroupModel> listStandardsSELECTED = new ArrayList<>();

    Map<String, ArrayList<TeacherClassGroupModel>> mapStandardsAndGroups;
    ArrayList<String> listHeaderParent = new ArrayList<>();
    ArrayList<TeacherClassGroupModel> listClassGroupsCollection;// = new ArrayList<>();
    boolean bClassAvailable = false;
    boolean bGroupsAvailable = false;

    TeacherExpandableListAdapter expListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_extandablegroupclasslist);


        btnConfirm = (Button) findViewById(R.id.stdGrp_btnConfirm);
        btnRemove = (Button) findViewById(R.id.stdGrp_btnRemove);
        ImageView ivBack = (ImageView) findViewById(R.id.extendlist_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                schoolStdGroups.setListGroups(listGropus);
                schoolStdGroups.setListClasses(listStandards);
                backToResultActvity("OK");
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnRemove.setVisibility(View.GONE);

        schoolStdGroups = (TeacherSchoolsModel) getIntent().getSerializableExtra("STD_GROUP");
        listGropus.addAll(schoolStdGroups.getListGroups());
        listStandards.addAll(schoolStdGroups.getListClasses());
        createGroupList();
        createCollection();

        expListView = (ExpandableListView) findViewById(R.id.stdGrp_exListGrpStd);
        expListAdapter = new TeacherExpandableListAdapter(
                this, listHeaderParent, mapStandardsAndGroups, expListView, new TeacherOnClassGroupItemCheckListener() {
            @Override
            public void classGropItem_addClass(String type, TeacherClassGroupModel classGroup) {
                // 0-Class, 1-group

                Log.d("TYPE", type);

                if (type.equals(LIST_STANDARDS) && classGroup != null) {
                    listStandardsSELECTED.add(classGroup);
                } else {
                    listGropusSELECTED.add(classGroup);
                }

                enableConfirm();
            }

            @Override
            public void classGropItem_removeClass(String type, TeacherClassGroupModel classGroup) {
                Log.d("TYPE", type);

                if (type.equals(LIST_STANDARDS) && classGroup != null) {
                    listStandardsSELECTED.remove(classGroup);
                } else {
                    listGropusSELECTED.remove(classGroup);
                }

                enableConfirm();
            }
        });
        expListView.setAdapter(expListAdapter);
        int len = expListView.getCount();
        Log.d("LENGTH", len + "");

        for (int i = 0; i < len; i++) {
            Log.d("POSITION", i + "");
            expListView.expandGroup(i);
        }


        if (!bEditClick) {

            for (int i = 0; i < listStandards.size(); i++) {
                listStandards.get(i).setbSelected(false);
            }
            for (int i = 0; i < listGropus.size(); i++) {
                listGropus.get(i).setbSelected(false);
            }

        } else {

            listGropusSELECTED.addAll(schoolStdGroups.getListGroups());
            listStandardsSELECTED.addAll(schoolStdGroups.getListClasses());
        }

        enableConfirm();
    }

    private void createGroupList() {

        if (listStandards.size() > 0) {
            listHeaderParent.add(LIST_STANDARDS);
            bClassAvailable = true;
        }
        if (listGropus.size() > 0) {
            listHeaderParent.add(LIST_GROUPS);
            bGroupsAvailable = true;
        }
    }

    private void createCollection() {

        mapStandardsAndGroups = new LinkedHashMap<>();

        for (String groupclass : listHeaderParent) {
            if (groupclass.equals(LIST_STANDARDS)) {
                loadSubList(listStandards);
            } else if (groupclass.equals(LIST_GROUPS))
                loadSubList(listGropus);

            mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
        }
    }

    private void loadSubList(ArrayList<TeacherClassGroupModel> laptopModels) {
        listClassGroupsCollection = new ArrayList<>();
        for (TeacherClassGroupModel model : laptopModels)
            listClassGroupsCollection.add(model);
    }


    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");

        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.you_have_not_changed),
                Toast.LENGTH_LONG).show();
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        returnIntent.putExtra("SCHOOL", schoolStdGroups);
        setResult(SELECTED_STD_SEC_POSITION, returnIntent);
        finish();
    }

    private void enableConfirm() {
        if (listStandardsSELECTED.size() > 0 || listGropusSELECTED.size() > 0)
            btnConfirm.setEnabled(true);
        else btnConfirm.setEnabled(false);

        Log.d("ENABLE/DISABLE-CLASS", "Selected: " + listStandardsSELECTED.size());
        Log.d("ENABLE/DISABLE-GROUP", "Selected: " + listGropusSELECTED.size());
    }
}
