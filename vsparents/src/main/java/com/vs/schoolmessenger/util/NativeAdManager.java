package com.vs.schoolmessenger.util;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.vs.schoolmessenger.R;

public class NativeAdManager {
    private static NativeAdManager instance;
    private NativeAd nativeAd;
    private Context context;

    // ✅ Private Constructor for Singleton Pattern
    private NativeAdManager(Context context) {
        this.context = context.getApplicationContext();
        loadNativeAd();  // 🔥 Load ad once
    }

    // ✅ Singleton Instance
    public static NativeAdManager getInstance(Context context) {
        if (instance == null) {
            instance = new NativeAdManager(context);
        }
        return instance;
    }

    // ✅ Load Native Ad Once
    private void loadNativeAd() {
//        AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
        AdLoader adLoader = new AdLoader.Builder(context, context.getResources().getString(R.string.native_ads_unit_one))
                .forNativeAd(ad -> {
                    // 🔥 Cache the Loaded Ad
                    nativeAd = ad;
                    Log.d("NativeAdManager", "ads loaded successfully");
                })
                .withAdListener(new AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e("NativeAdManager", "Failed to load native ad: " + adError.getMessage());
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    // ✅ Get Native Ad Instance
    public NativeAd getNativeAd() {
        return nativeAd;
    }
}