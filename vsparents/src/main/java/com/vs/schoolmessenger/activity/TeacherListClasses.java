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
import com.vs.schoolmessenger.adapter.TeacherClassListAdapter;
import com.vs.schoolmessenger.model.TeacherClassesListclass;

import java.util.ArrayList;
import java.util.List;



public class TeacherListClasses extends AppCompatActivity {
    RecyclerView rvClassList;
    TextView tvok, tvcancel;

    private TeacherClassListAdapter adapter;
    private List<TeacherClassesListclass> classlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_list_classes);



        rvClassList=(RecyclerView)findViewById(R.id.rvClassList);
        ImageView ivBack = (ImageView) findViewById(R.id.classPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvok=(TextView)findViewById(R.id.class_ok);
        tvcancel=(TextView)findViewById(R.id.class_cancel);

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
        adapter=new TeacherClassListAdapter(TeacherListClasses.this,classlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvClassList.setHasFixedSize(true);
        rvClassList.setLayoutManager(mLayoutManager);
        rvClassList.setItemAnimator(new DefaultItemAnimator());
        rvClassList.setAdapter(adapter);


        TeacherClassesListclass cls1 = new TeacherClassesListclass();
        cls1.setClasses("Class 1");
        classlist.add(cls1);
        TeacherClassesListclass cls2 = new TeacherClassesListclass();
        cls2.setClasses("Class 2");
        classlist.add(cls2);
        TeacherClassesListclass cls3 = new TeacherClassesListclass();
        cls3.setClasses("Class 3");
        classlist.add(cls3);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
