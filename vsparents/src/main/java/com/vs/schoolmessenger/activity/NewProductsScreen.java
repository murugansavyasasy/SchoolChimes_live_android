package com.vs.schoolmessenger.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.utils.Utils;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NewProductsScreen extends AppCompatActivity {

    WebView webView;
    ProgressDialog pDialog;
    String MobileNumber;
    String OfferLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.special_offer);
        webView=(WebView) findViewById(R.id.specila_offer_webview);


        TeacherUtil_SharedPreference.putNewProduct(NewProductsScreen.this,"1");
        MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(NewProductsScreen.this);
        Log.d("MobileNumber",MobileNumber);
        OfferLink=TeacherUtil_SharedPreference.getNewProductLink(NewProductsScreen.this);
        Log.d("products_OfferLink",OfferLink+MobileNumber);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("New Products");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pDialog = new ProgressDialog(NewProductsScreen.this);
        pDialog.setMessage("");
        pDialog.setCancelable(false);
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();
                }
            }
        });

        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            {
                //view.loadUrl(url);
                System.out.println("hello");
                return false;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(OfferLink+MobileNumber);

    }

}