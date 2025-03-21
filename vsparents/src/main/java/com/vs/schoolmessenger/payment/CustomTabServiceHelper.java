package com.vs.schoolmessenger.payment;

import android.content.ComponentName;
import android.content.Context;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;

public class CustomTabServiceHelper {
    private CustomTabsClient mClient;
    private CustomTabsSession mSession;

    public void bindCustomTabsService(Context context) {
        CustomTabsServiceConnection connection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mClient = client;
                mClient.warmup(0L);
                mSession = mClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mClient = null;
            }
        };

        CustomTabsClient.bindCustomTabsService(context, "com.android.chrome", connection);
    }

    public CustomTabsSession getSession() {
        return mSession;
    }
}
