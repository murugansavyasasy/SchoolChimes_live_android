package com.vs.schoolmessenger.util;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.populateNativeAdView;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.vs.schoolmessenger.R;

public class NativeAdManager {
    private static NativeAd nativeAd;
    private static NativeAdView nativeAdView;


    public static void initialize(Context context) {
        MobileAds.initialize(context);
//        loadNativeAd(context);
    }

    public static void loadNativeAd(Context context) {
        if (nativeAd != null) return; // Prevent multiple ad requests

//        AdLoader adLoader = new AdLoader.Builder(context, context.getResources().getString(R.string.native_ads_unit_one))
        AdLoader adLoader = new AdLoader.Builder(context, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(ad -> {
                    if (nativeAd != null) {
                        nativeAd.destroy(); // Destroy the old ad to prevent memory leaks
                    }
                    nativeAd = ad;

                    Log.d("AdMobNative", "Native Ad Loaded Successfully");
                })
                .withAdListener(new com.google.android.gms.ads.AdListener() {
                    @Override
                    public void onAdFailedToLoad(LoadAdError adError) {
                        Log.e("AdMobNative", "Failed to load native ad: " + adError.getMessage());
                    }
                })
                .withNativeAdOptions(new NativeAdOptions.Builder().build())
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());
    }

    public static NativeAd getNativeAd() {
        return nativeAd;
    }

    public static void destroyNativeAd() {
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
    }
}
