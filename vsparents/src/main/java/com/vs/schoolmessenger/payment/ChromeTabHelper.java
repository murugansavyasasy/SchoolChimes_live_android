package com.vs.schoolmessenger.payment;

import android.content.Context;
import android.net.Uri;
import androidx.browser.customtabs.CustomTabsIntent;

public class ChromeTabHelper {
    public static void openChromeTab(Context context, String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(0xFF6200EE); // Custom toolbar color
        builder.setShowTitle(true); // Show page title

        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(context, Uri.parse(url));
    }
}
