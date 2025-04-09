package com.vs.schoolmessenger.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.Stack;

public class FeeFragment extends Fragment {

    String childID = "";
    String SchoolID = "";
    String PaymentUrl = "";
    WebView web_view;
    private Stack<WebView> popupWebViewStack = new Stack<>();
    private FrameLayout webViewContainer;

    ProgressDialog progressBar;
    private static final String TAG = "Main";
    AlertDialog alertDialogView;
    public FeeFragment() {
        // Required empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.activity_payment_screen, container, false);

        web_view = rootView.findViewById(R.id.web_view);
        webViewContainer = rootView.findViewById(R.id.webview_container);

        childID = Util_SharedPreference.getChildIdFromSP(getActivity());
        SchoolID = Util_SharedPreference.getSchoolIdFromSP(getActivity());

        String url = TeacherUtil_SharedPreference.getPaymentUrl(getActivity());
        String s = url;
        s = s.replace(":student_id", childID);
        PaymentUrl = s.replace(":school_id", SchoolID);
        Log.d("URL", PaymentUrl);
        loadWebView(web_view);

        progressBar = new ProgressDialog(requireActivity());
        progressBar.setMessage("Please wait...");
        alertDialogView = new AlertDialog.Builder(getActivity()).create();

        web_view.loadUrl(PaymentUrl);
        return rootView;
    }

    @SuppressLint("JavascriptInterface")
    private void loadWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebViewClient(new WebViewClient());
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(false);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        web_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view.setScrollbarFadingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            web_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }


        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(requireContext());
                loadWebView(newWebView);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                newWebView.setLayoutParams(params);

                webViewContainer.addView(newWebView);
                popupWebViewStack.push(newWebView);

                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }

            @Override
            public void onCloseWindow(WebView window) {
                if (!popupWebViewStack.isEmpty()) {
                    WebView closingWebView = popupWebViewStack.pop();
                    webViewContainer.removeView(closingWebView);
                    closingWebView.destroy();
                }
            }
        });

        web_view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                final Uri uri = Uri.parse(url);
                return handleUri(view, uri);

//                return false; // allow WebView to load the URL normally
            }

            // For older devices (< API 21)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("WebView", "Navigating to: " + url);
                final Uri uri = Uri.parse(url);
                return handleUri(view, uri);
//                view.loadUrl(url);
//                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!progressBar.isShowing()) {
                    progressBar.show();
                }
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                Log.d("callbackURL", url);

                if (url.contains("/#/paymentsucccess/success")) {
                     showAlert("Payment Done!!", "Payment Successful.View/Download Receipt on Receipt Tab.");
                } else if (url.contains("/#/paymentsucccess/failed")) {
                      showAlert("Payment failed..", "Please try again later!!");
                }
            }
        });
    }

    private boolean handleUri(WebView view, final Uri url) {
        Log.i(TAG, "Uri =" + url);
        System.out.println("Uri =" + url);

        final String scheme = url.getScheme();
        System.out.println("scheme detected: " + scheme);
        assert scheme != null;
        if (scheme.matches("upi|tez|gpay|phonepe|paytmmp")) {
            System.out.println("UPI Intent uri detected");
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);  // To show app chooser
                intent.setData(url);
                startActivity(intent);
                return true;
            } catch (Exception e) {
                alertDialogView.setTitle("Error");
                alertDialogView.setMessage("Check if you have UPI apps installed or not !");
                alertDialogView.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                alertDialogView.show();
            }
        } else {
            Log.i(TAG, "Processing webview url click...");
//            view.loadUrl(String.valueOf(url));
            return false;
        }
        return false;
    }

    // Optional: Handle back press in Fragment
    // Handle back button: Close last opened popup or go back in mainWebView
    public boolean handleBackPressed() {
        if (!popupWebViewStack.isEmpty()) {
            WebView lastPopup = popupWebViewStack.pop();
            webViewContainer.removeView(lastPopup);
            lastPopup.destroy();
            return true;
        } else if (web_view.canGoBack()) {
            web_view.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyView() {
        if (web_view != null) {
            web_view.destroy();
        }
        while (!popupWebViewStack.isEmpty()) {
            popupWebViewStack.pop().destroy();
        }
        super.onDestroyView();
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
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}