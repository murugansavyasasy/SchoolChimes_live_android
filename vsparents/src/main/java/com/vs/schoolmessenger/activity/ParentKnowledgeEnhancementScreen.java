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
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.fragments.CompletedKnowledgeEnhancment;
import com.vs.schoolmessenger.fragments.UpcomingKnowledgeEnhancement;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ParentKnowledgeEnhancementScreen extends AppCompatActivity implements View.OnClickListener {

    public static ParentKnowledgeEnhancementScreen instance;
    String strChildID = "", strSchoolID = "";
    int complete = 0;
    private TabLayout allTabs;
    private UpcomingKnowledgeEnhancement fragmentOne;
    private CompletedKnowledgeEnhancment fragmentTwo;

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.parent_online_exam_tap);


        strChildID = Util_SharedPreference.getChildIdFromSP(ParentKnowledgeEnhancementScreen.this);
        strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ParentKnowledgeEnhancementScreen.this);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.QUIZ);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        complete = getIntent().getIntExtra("complete", 0);
        instance = this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();
    }

    private void setupTabLayout() {
        fragmentOne = new UpcomingKnowledgeEnhancement();
        fragmentTwo = new CompletedKnowledgeEnhancment();
        allTabs.removeAllTabs();
        if (complete == 0) {
            allTabs.addTab(allTabs.newTab().setText(R.string.upcomming), true);
            allTabs.addTab(allTabs.newTab().setText(R.string.lbl_completed));
        } else if (complete == 1) {
            allTabs.addTab(allTabs.newTab().setText(R.string.upcomming));
            allTabs.addTab(allTabs.newTab().setText(R.string.lbl_completed), true);
        }
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
