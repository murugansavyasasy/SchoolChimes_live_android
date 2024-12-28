package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

public class HelpActivity extends AppCompatActivity {

    WebView webView;
    ProgressDialog pDialog;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_offer);
        webView = (WebView) findViewById(R.id.specila_offer_webview);
        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(R.string.txt_menu_help);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pDialog = new ProgressDialog(HelpActivity.this);
        pDialog.setMessage("");
        pDialog.setCancelable(false);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();
                }
            }
        });

        String URL = TeacherUtil_SharedPreference.getHelpLineUrl(HelpActivity.this);
        Log.d("Help Url", URL);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(URL);

    }
}