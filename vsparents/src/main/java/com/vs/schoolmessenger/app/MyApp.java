package com.vs.schoolmessenger.app;

import android.app.Application;

import com.vs.schoolmessenger.R;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;

/**
 * Created by voicesnap on 3/21/2017.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();


        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/zawgyione.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());

    }
}
