package com.vs.schoolmessenger.payment;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ProfileLinkScreen;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

public class FeeFragment extends Fragment {

    String childID = "";
    String SchoolID = "";
    String PaymentUrl="";
    WebView web_view;
    ProgressDialog pDialog;

    private WebSettings webSettings;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_payment_screen, container, false);

        web_view = rootView.findViewById(R.id.web_view);

        childID = Util_SharedPreference.getChildIdFromSP(getActivity());
        SchoolID = Util_SharedPreference.getSchoolIdFromSP(getActivity());

        String url= TeacherUtil_SharedPreference.getPaymentUrl(getActivity());
        String s = url;
        s = s.replace(":student_id", childID);
        PaymentUrl = s.replace(":school_id", SchoolID);

        Log.d("URL",PaymentUrl);
        loadWebView("");



        return rootView;
    }

    @SuppressLint("JavascriptInterface")
    private void loadWebView(String data) {
        String mime = "text/html";
        String encoding = "utf-8";
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading..");
        progressDialog.setCancelable(false);
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
        web_view.loadUrl(PaymentUrl);

        web_view.setWebChromeClient(new WebChromeClient() {

            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                Log.d("Webview Error", consoleMessage.message() + " -- From line "
                        + consoleMessage.lineNumber() + " of "
                        + consoleMessage.sourceId());
                return super.onConsoleMessage(consoleMessage);
            }
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
                if (url.contains("/#/paymentsucccess/success")) {
                    showAlert("Payment Done!!", "Your payment has been done successfully..Thank You");
                }
                else if(url.contains("/#/paymentsucccess/failed")){
                    showAlert("Payment failed..", "Please try again later!!");

                }
            }
        });
    }

    private void showAlert(String title, String mesage) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        final View promptsView = li.inflate(R.layout.payment_success_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
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
                getActivity().finish();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }




}
