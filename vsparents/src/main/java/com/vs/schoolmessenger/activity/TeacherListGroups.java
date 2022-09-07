package com.vs.schoolmessenger.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherGroupListApater;
import com.vs.schoolmessenger.model.TeacherGroupclass;

import java.util.ArrayList;
import java.util.List;


public class TeacherListGroups extends AppCompatActivity {
    RecyclerView rvGroupList;
    TextView tvok, tvcancel;

    private TeacherGroupListApater adapter;
    private List<TeacherGroupclass> grouplist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_list_groups);


        rvGroupList=(RecyclerView)findViewById(R.id.rvGroupList);
        ImageView ivBack = (ImageView) findViewById(R.id.groupPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvok=(TextView)findViewById(R.id.group_ok);
        tvcancel=(TextView)findViewById(R.id.group_cancel);

        tvok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter=new TeacherGroupListApater(TeacherListGroups.this,grouplist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvGroupList.setHasFixedSize(true);
        rvGroupList.setLayoutManager(mLayoutManager);
        rvGroupList.setItemAnimator(new DefaultItemAnimator());
        rvGroupList.setAdapter(adapter);


        TeacherGroupclass grp1 = new TeacherGroupclass();
        grp1.setGroup("School 1");
        grouplist.add(grp1);
        TeacherGroupclass grp2 = new TeacherGroupclass();
        grp2.setGroup("School 2");
        grouplist.add(grp2);
        TeacherGroupclass grp3 = new TeacherGroupclass();
        grp3.setGroup("School 3");
        grouplist.add(grp3);
        TeacherGroupclass grp4 = new TeacherGroupclass();
        grp4.setGroup("School 4");
        grouplist.add(grp4);
        TeacherGroupclass grp5 = new TeacherGroupclass();
        grp5.setGroup("School 5");
        grouplist.add(grp5);
        adapter.notifyDataSetChanged();

    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
