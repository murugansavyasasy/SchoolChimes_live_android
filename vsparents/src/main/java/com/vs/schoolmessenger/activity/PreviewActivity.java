package com.vs.schoolmessenger.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.vs.schoolmessenger.R;

public class PreviewActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private WebView webPreview;
    private ProgressBar progressBar;
    private ImageButton btnBack;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        imagePreview = findViewById(R.id.imagePreview);
        webPreview = findViewById(R.id.webPreview);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        String fileUrl = getIntent().getStringExtra("fileUrl");
        Log.d("fileUrl",fileUrl);
        if (fileUrl == null || fileUrl.isEmpty()) {
            finish();
            return;
        }

        fileUrl = fileUrl.toLowerCase();

        if (isImageFile(fileUrl)) {
            showImage(fileUrl);
        } else {
            showWebContent(fileUrl);
        }
    }

    private boolean isImageFile(String filePath) {
        return filePath.endsWith(".jpg") ||
                filePath.endsWith(".jpeg") ||
                filePath.endsWith(".png") ||
                filePath.endsWith(".gif") ||
                filePath.endsWith(".bmp") ||
                filePath.endsWith(".webp");
    }

    private void showImage(String imageUrl) {
        imagePreview.setVisibility(View.VISIBLE);
        webPreview.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        Glide.with(this)
                .load(imageUrl)
                .into(imagePreview);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showWebContent(String fileUrl) {
        imagePreview.setVisibility(View.GONE);
        webPreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        WebSettings settings = webPreview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false);

        webPreview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        webPreview.setWebChromeClient(new WebChromeClient());

        String finalUrl;

        if (fileUrl.contains("vimeo.com")) {
            finalUrl = fileUrl;
        }
        else if (fileUrl.endsWith(".pdf") || fileUrl.endsWith(".doc") ||
                fileUrl.endsWith(".docx") || fileUrl.endsWith(".ppt") ||
                fileUrl.endsWith(".pptx") || fileUrl.endsWith(".xls") ||
                fileUrl.endsWith(".xlsx")) {
            finalUrl = "https://docs.google.com/gview?embedded=true&url=" + fileUrl;
        }
        else {
            finalUrl = fileUrl;
        }

        webPreview.loadUrl(finalUrl);
    }

    @Override
    public void onBackPressed() {
        if (webPreview.getVisibility() == View.VISIBLE && webPreview.canGoBack()) {
            webPreview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
