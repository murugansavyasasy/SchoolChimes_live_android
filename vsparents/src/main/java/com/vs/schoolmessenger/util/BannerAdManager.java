package com.vs.schoolmessenger.util;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.vs.schoolmessenger.R;

public class BannerAdManager {
    private static BannerAdManager instance;
    private AdView adView;
    private Context context;

    private BannerAdManager(Context context) {
        this.context = context.getApplicationContext();
        createAdView();
    }

    // ✅ Singleton Pattern
    public static synchronized BannerAdManager getInstance(Context context) {
        if (instance == null) {
            instance = new BannerAdManager(context);
        }
        return instance;
    }

    // ✅ Create and Load Banner Ad
    private void createAdView() {
        adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
//        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111"); // 🔹 Test Ad Unit ID
        adView.setAdUnitId(context.getResources().getString(R.string.ads_unit_one)); // 🔹 Test Ad Unit ID
        adView.loadAd(new AdRequest.Builder().build());

        Log.d("AdmobFirstTime","AdmobFirstTime");
    }

    // ✅ Get Banner Ad for Display
    public AdView getAdView() {
        return adView;
    }

    // ✅ Reload Ad When Needed
    public void refreshAd() {
        adView.loadAd(new AdRequest.Builder().build());
    }
}
