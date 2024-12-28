package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.fragments.CompletedExamEnhancement;
import com.vs.schoolmessenger.fragments.ExpiredExamEnhancement;
import com.vs.schoolmessenger.fragments.UpcomingExamEnhancement;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ParentOnlineExamScreen extends AppCompatActivity implements View.OnClickListener {

    public static ParentOnlineExamScreen instance;
    String strChildID = "", strSchoolID = "";
    private TabLayout allTabs;
    private UpcomingExamEnhancement fragmentOne;
    private CompletedExamEnhancement fragmentTwo;
    private ExpiredExamEnhancement fragmentThree;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.parent_online_exam_tap);


        strChildID = Util_SharedPreference.getChildIdFromSP(ParentOnlineExamScreen.this);
        strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ParentOnlineExamScreen.this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.exam);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        instance = this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        fragmentOne = new UpcomingExamEnhancement();
        fragmentTwo = new CompletedExamEnhancement();
        fragmentThree = new ExpiredExamEnhancement();

        allTabs.addTab(allTabs.newTab().setText(R.string.upcoming), true);
        allTabs.addTab(allTabs.newTab().setText(R.string.completed));
        allTabs.addTab(allTabs.newTab().setText(R.string.Expired));
    }


    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }

    private void bindWidgetsWithAnEvent() {
        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentOne);
                break;
            case 1:
                replaceFragment(fragmentTwo);
                break;
            case 2:
                replaceFragment(fragmentThree);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    public void onClick(View v) {

    }
}