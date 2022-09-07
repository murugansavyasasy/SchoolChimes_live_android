package com.vs.schoolmessenger.activity;

import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.fragments.FeesPaidFragment;
import com.vs.schoolmessenger.fragments.UpcommingFeesFragment;

public class ParentFeeDetails extends AppCompatActivity {
    public static ParentFeeDetails instance;
    private FeesPaidFragment fragmentOne;
    private UpcommingFeesFragment fragmentTwo;
    private TabLayout allTabs;

    String userId;
    SharedPreferences shpRemember;
    private static final String SH_USERID = "UserId";
    private static final String SH_USERS = "userInfo";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_fee_details);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.fee);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.details);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        shpRemember = getSharedPreferences(SH_USERS, MODE_PRIVATE);
        userId = shpRemember.getString(SH_USERID, "");



        instance=this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }
    public static ParentFeeDetails getInstance() {
        return instance;
    }
    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }
    private void setupTabLayout() {
        fragmentOne = new FeesPaidFragment();
        fragmentTwo = new UpcommingFeesFragment();
        allTabs.addTab(allTabs.newTab().setText(R.string.paid_details),true);
        allTabs.addTab(allTabs.newTab().setText(R.string.upcomming));
    }
    private void bindWidgetsWithAnEvent()
    {
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
    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(fragmentOne);
                break;
            case 1 :
                replaceFragment(fragmentTwo);
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
}