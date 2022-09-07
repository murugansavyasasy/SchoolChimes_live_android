package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class ParentQuizScreen extends AppCompatActivity implements View.OnClickListener {

LinearLayout menu_Knowledge_Enhancement,menu_Exam;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quize_screen);

        menu_Knowledge_Enhancement=(LinearLayout) findViewById(R.id.menu_Knowledge_Enhancement);
        menu_Exam=(LinearLayout) findViewById(R.id.menu_Exam);
        menu_Exam.setOnClickListener(this);
        menu_Knowledge_Enhancement.setOnClickListener(this);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Quiz");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menu_Exam:
               Intent exam = new Intent(ParentQuizScreen.this, ParentOnlineExamScreen.class);
               startActivity(exam);
                break;
            case R.id.menu_Knowledge_Enhancement:
                Intent KE = new Intent(ParentQuizScreen.this, ParentKnowledgeEnhancementScreen.class);
                startActivity(KE);
                break;
        }

    }
}
