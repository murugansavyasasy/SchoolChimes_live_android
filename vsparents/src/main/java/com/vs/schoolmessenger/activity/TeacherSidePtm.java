package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.fragments.SlotsListTeacherSide;
import com.vs.schoolmessenger.fragments.TodaySlots;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class TeacherSidePtm extends AppCompatActivity implements View.OnClickListener {

    TextView isSlotsHistory, isTodaySlots;
    String SchoolID, StaffID;
    String Root_Frag = "root_fagment";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacherside_ptm);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Meeting Slot");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        loadFrag(new TodaySlots(), 0);


        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherSidePtm.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherSidePtm.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }

        isTodaySlots = findViewById(R.id.btnMessages);
        isSlotsHistory = findViewById(R.id.btnStatus);


        isTodaySlots.setTypeface(null, Typeface.BOLD);
        isSlotsHistory.setTypeface(null, Typeface.BOLD);

        isTodaySlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util_Common.isChooseDate = true;
                isTodaySlots.setBackgroundColor(ContextCompat.getColor(TeacherSidePtm.this, R.color.teacher_colorPrimary));
                isSlotsHistory.setBackgroundColor(ContextCompat.getColor(TeacherSidePtm.this, R.color.clr_grey));
                loadFrag(new TodaySlots(), 0);
            }
        });

        isSlotsHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Util_Common.isChooseDate = true;
                isSlotsHistory.setBackgroundColor(ContextCompat.getColor(TeacherSidePtm.this, R.color.teacher_colorPrimary));
                isTodaySlots.setBackgroundColor(ContextCompat.getColor(TeacherSidePtm.this, R.color.clr_grey));
                loadFrag(new SlotsListTeacherSide(), 1);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void loadFrag(Fragment fragment_name, int flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (flag == 0) {
            ft.add(R.id.FL, fragment_name);
            fm.popBackStack(Root_Frag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack(Root_Frag);
        } else {
            ft.replace(R.id.FL, fragment_name);
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onClick(View view) {

    }
}
