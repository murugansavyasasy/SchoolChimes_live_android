package com.vs.schoolmessenger.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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


public class NewUpdateWebView extends AppCompatActivity {
    WebView receiptWebView;
    String PdfURL = "", Title = "";

    public static void showAlert(final Activity activity, String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                activity);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_pdf);

        alertDialog.setNeutralButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_updates_webview);

        PdfURL = getIntent().getExtras().getString("URL", "");
        Title = getIntent().getExtras().getString("tittle", "");
        Log.d("URL", PdfURL);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(Title);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        receiptWebView = findViewById(R.id.receiptWebView);
        receiptWebView.requestFocus();
        receiptWebView.getSettings().setJavaScriptEnabled(true);
        receiptWebView.getSettings().setBuiltInZoomControls(true);
        receiptWebView.getSettings().setSupportZoom(true);
        receiptWebView.loadUrl(PdfURL);
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

                }
                if (progress == 100) {

                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}