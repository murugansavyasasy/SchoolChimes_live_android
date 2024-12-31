package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Constants.isOnBackPressed;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.HomeWorkDateWiseAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.util.Constants;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class HomeworkListActivity extends AppCompatActivity {

    RecyclerView rvHomeWorks;
    RelativeLayout rytParent;

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
        setContentView(R.layout.home_work_list);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(Constants.homeWorkData.getDate());
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isOnBackPressed = true;
                onBackPressed();
            }
        });

        rvHomeWorks = (RecyclerView) findViewById(R.id.rvHomeWorks);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        HomeWorkDateWiseAdapter mAdapter = new HomeWorkDateWiseAdapter(Constants.homeWorkData.getHw(), HomeworkListActivity.this, rytParent);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(HomeworkListActivity.this);
        rvHomeWorks.setLayoutManager(mLayoutManager);
        rvHomeWorks.setItemAnimator(new DefaultItemAnimator());
        rvHomeWorks.setAdapter(mAdapter);
        rvHomeWorks.getRecycledViewPool().setMaxRecycledViews(0, 80);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (HomeWorkDateWiseAdapter.mediaPlayer != null) {
            if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                HomeWorkDateWiseAdapter.mediaPlayer.stop();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (HomeWorkDateWiseAdapter.mediaPlayer != null) {
            if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                HomeWorkDateWiseAdapter.mediaPlayer.stop();
            }
        }
        isOnBackPressed = true;
        finish();
    }
}