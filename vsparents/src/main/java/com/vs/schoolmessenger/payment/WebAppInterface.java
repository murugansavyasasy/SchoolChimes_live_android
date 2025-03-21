package com.vs.schoolmessenger.payment;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {
    Context mContext;

    WebAppInterface(Context context) {
        mContext = context;
    }

    @JavascriptInterface
    public void postMessage(String message) {
        Log.d("WebViewMessage", "Message from JS: " + message);
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}