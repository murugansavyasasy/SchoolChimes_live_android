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

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileLinkScreen extends AppCompatActivity {

    WebView webView;
    ProgressDialog pDialog;
    String ChildId;
    String ProfileLink;
    private WebSettings webSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_offer);
        webView=(WebView) findViewById(R.id.specila_offer_webview);

        ChildId=Util_SharedPreference.getChildIdFromSP(ProfileLinkScreen.this);
        Log.d("MobileNumber",ChildId);
        ProfileLink=TeacherUtil_SharedPreference.getProfileLink(ProfileLinkScreen.this);
        String Title=TeacherUtil_SharedPreference.getProfileTitle(ProfileLinkScreen.this);
        Log.d("profile_OfferLink",ProfileLink+ChildId);


        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(Title);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pDialog = new ProgressDialog(ProfileLinkScreen.this);
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

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(ProfileLink+ChildId);

    }
}