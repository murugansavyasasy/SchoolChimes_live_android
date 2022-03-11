package com.vs.schoolmessenger.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
public class PaymentWebView extends AppCompatActivity {

    String childID = "";
    String SchoolID = "";
    String PaymentUrl="";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_screen);

        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText("Fee Payment");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        childID = Util_SharedPreference.getChildIdFromSP(PaymentWebView.this);
        SchoolID = Util_SharedPreference.getSchoolIdFromSP(PaymentWebView.this);

        String url= TeacherUtil_SharedPreference.getPaymentUrl(PaymentWebView.this);
        String s = url;
        s = s.replace(":student_id", childID);
        PaymentUrl = s.replace(":school_id", SchoolID);

        Log.d("URL",PaymentUrl);
        loadWebView("");

    }
    @SuppressLint("JavascriptInterface")
    private void loadWebView(String data) {
        String mime = "text/html";
        String encoding = "utf-8";
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
        final WebView web_view = findViewById(R.id.web_view);
        WebSettings settings = web_view.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDomStorageEnabled(true);
        web_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            web_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
//        PaymentInterface paymentInterface = new PaymentInterface();
//        web_view.addJavascriptInterface(paymentInterface, "PaymentInterface");
       // web_view.loadDataWithBaseURL(TeacherSchoolsApiClient.BASE_URL, data, mime, encoding, TeacherSchoolsApiClient.BASE_URL);
        web_view.loadUrl(PaymentUrl);
        web_view.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress < 100) {
                    progressDialog.show();
                }
                if (progress == 100) {
                    progressDialog.dismiss();
                }
            }
        });
        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("CallbackURl", url);
                if (url.contains("https://vs8.voicesnapforschools.com/#/paymentsucccess/success")) {
                    showAlert("Payment Done!!", "Your payment has been done successfully..Thank You");
                }
                else if(url.contains("https://vs8.voicesnapforschools.com/#/paymentsucccess/failed")){
                    showAlert("Payment failed..", "Please try again later!!");

                }
            }
        });
        web_view.clearCache(true);
    }
    private void showAlert(String title, String mesage) {
        LayoutInflater li = LayoutInflater.from(PaymentWebView.this);
        final View promptsView = li.inflate(R.layout.payment_success_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PaymentWebView.this);
        alertDialogBuilder.setView(promptsView);
        TextView btnOk = (TextView) promptsView.findViewById(R.id.btnOk);
        TextView lblMessages = (TextView) promptsView.findViewById(R.id.lblMessages);
        TextView lblAlertTitle = (TextView) promptsView.findViewById(R.id.lblAlertTitle);
        lblAlertTitle.setText(title);
        lblMessages.setText(mesage);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
                finish();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    private static class PaymentInterface {
        @JavascriptInterface
        public void success(String data) {
            Log.d("Data", data);
        }

        @JavascriptInterface
        public void error(String data) {
            Log.d("Data", data);
        }
    }
}
