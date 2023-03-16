package com.vs.schoolmessenger.payment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.vs.schoolmessenger.R;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PdfWebView extends AppCompatActivity {
    WebView receiptWebView;
    String PdfURL = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt_web_view);

        PdfURL = getIntent().getExtras().getString("URL", "");
        Log.d("URL",PdfURL);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Fee Receipt");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading receipt...");
        progressDialog.setCancelable(false);
        receiptWebView = findViewById(R.id.receiptWebView);
        receiptWebView.requestFocus();
        receiptWebView.getSettings().setJavaScriptEnabled(true);
        receiptWebView.getSettings().setBuiltInZoomControls(true);
        receiptWebView.getSettings().setSupportZoom(true);

        String url = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + PdfURL;
        receiptWebView.loadUrl(url);
        receiptWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        receiptWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
    }
}