package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.fragments.CertifiatesFragments;
import com.vs.schoolmessenger.fragments.RequestCertificateFragment;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import ss.com.bannerslider.Slider;

public class CertificateRequestActivity extends AppCompatActivity {

    public static CertificateRequestActivity instance;
    Slider slider;
    LinearLayout lnrAdView;
    LinearLayout mAdView;
    private TabLayout allTabs;
    private RequestCertificateFragment fragmentOne;
    private CertifiatesFragments fragmentTwo;

    public static CertificateRequestActivity getInstance() {
        return instance;
    }

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
        setContentView(R.layout.activity_certificate_request);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Certificate));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(getResources().getString(R.string.Request));

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lnrAdView = (LinearLayout) findViewById(R.id.lnrAdView);
        mAdView = findViewById(R.id.adView);

        lnrAdView.setVisibility(View.VISIBLE);

        Slider.init(new PicassoImageLoadingService(CertificateRequestActivity.this));
        slider = findViewById(R.id.banner);
        ImageView adImage = findViewById(R.id.adImage);

        ShowAds.getAds(CertificateRequestActivity.this, adImage, slider, "", mAdView);

        instance = this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setupTabLayout() {
        fragmentOne = new RequestCertificateFragment();
        fragmentTwo = new CertifiatesFragments();
        allTabs.addTab(allTabs.newTab().setText(getResources().getString(R.string.Request)), true);
        allTabs.addTab(allTabs.newTab().setText(getResources().getString(R.string.Certificates)));
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

    private void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }
}