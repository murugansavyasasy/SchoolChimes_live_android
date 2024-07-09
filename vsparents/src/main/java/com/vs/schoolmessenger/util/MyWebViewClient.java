package com.vs.schoolmessenger.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.vs.schoolmessenger.R;

public class MyWebViewClient extends WebViewClient {
    Activity context;

    public MyWebViewClient(Activity context) {
        this.context = context;
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        if (errorCode == ERROR_TIMEOUT) {
            view.stopLoading();
        }

        view.loadData("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"></head>" +
                "<body><a href=\"" + view.getUrl() + "\">Turn on your network connection and click here</a></body></html>", "text/html", "utf-8");
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (Util_Common.isNetworkAvailable(context)) {
            view.loadUrl(url);
        } else
            showAlert("Connectivity", "Check Internet Connection");
        return true;
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_close);

        alertDialog.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }
}	
