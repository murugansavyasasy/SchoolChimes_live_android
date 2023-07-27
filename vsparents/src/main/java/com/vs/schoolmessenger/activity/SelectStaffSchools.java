package com.vs.schoolmessenger.activity;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SelectStaffSchoolsAdapter;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectStaffSchools extends AppCompatActivity {

    RecyclerView rvStaffSchools;
    SelectStaffSchoolsAdapter mAdapter;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_satff_schools);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Select");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("Your "+"School");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rvStaffSchools = (RecyclerView) findViewById(R.id.rvStaffSchools);


        mAdapter = new SelectStaffSchoolsAdapter(listschooldetails, SelectStaffSchools.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvStaffSchools.setLayoutManager(mLayoutManager);
        rvStaffSchools.setItemAnimator(new DefaultItemAnimator());
        rvStaffSchools.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }
}