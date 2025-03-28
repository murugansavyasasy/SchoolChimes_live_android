package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.app.LocaleHelper;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ss.com.bannerslider.Slider;


public class ParentQuizScreen extends AppCompatActivity implements View.OnClickListener {

    LinearLayout menu_Knowledge_Enhancement, menu_Exam;
    Slider slider;
    ImageView adImage;

    LinearLayout mAdView;

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
        setContentView(R.layout.quize_screen);

        menu_Knowledge_Enhancement = (LinearLayout) findViewById(R.id.menu_Knowledge_Enhancement);
        menu_Exam = (LinearLayout) findViewById(R.id.menu_Exam);
        menu_Exam.setOnClickListener(this);
        menu_Knowledge_Enhancement.setOnClickListener(this);

        Slider.init(new PicassoImageLoadingService(ParentQuizScreen.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.quiz);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAdsWithoutNative(this, adImage, slider, "", mAdView);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
