package com.vs.schoolmessenger.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.PlayerView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PreviewActivity extends AppCompatActivity {

    private ImageView imagePreview;
    private WebView webPreview;
    private ProgressBar progressBar;
    private ImageButton btnBack;
    private PlayerView playerView;
    private ExoPlayer exoPlayer;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        imagePreview = findViewById(R.id.imagePreview);
        webPreview = findViewById(R.id.webPreview);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        playerView = findViewById(R.id.playerView);

        btnBack.setOnClickListener(v -> onBackPressed());

        String fileUrl = getIntent().getStringExtra("fileUrl");
        Log.d("fileUrl", fileUrl);

        if (fileUrl == null || fileUrl.isEmpty()) {
            finish();
            return;
        }

        fileUrl = fileUrl.trim();

        if (fileUrl.contains("vimeo.com")) {
            playVimeoVideo(fileUrl);
            return;
        }

        if (isImageFile(fileUrl)) {
            showImage(fileUrl);
        } else {
            showWebContent(fileUrl);
        }
    }

    private boolean isImageFile(String filePath) {
        filePath = filePath.toLowerCase();
        return filePath.endsWith(".jpg") ||
                filePath.endsWith(".jpeg") ||
                filePath.endsWith(".png") ||
                filePath.endsWith(".gif") ||
                filePath.endsWith(".bmp") ||
                filePath.endsWith(".webp") ||
                filePath.endsWith(".tiff");
    }

    private void showImage(String imageUrl) {
        playerView.setVisibility(View.GONE);
        webPreview.setVisibility(View.GONE);
        imagePreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);

        Glide.with(this).load(imageUrl).into(imagePreview);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void showWebContent(String fileUrl) {

        playerView.setVisibility(View.GONE);
        imagePreview.setVisibility(View.GONE);

        webPreview.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        WebSettings settings = webPreview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        webPreview.setBackgroundColor(Color.BLACK);
        webPreview.setWebChromeClient(new WebChromeClient());

        webPreview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
            }
        });

        // If PDF, DOC, PPT → load via Google viewer
        if (fileUrl.endsWith(".pdf") || fileUrl.endsWith(".doc") ||
                fileUrl.endsWith(".docx") || fileUrl.endsWith(".ppt") ||
                fileUrl.endsWith(".pptx") || fileUrl.endsWith(".xls") ||
                fileUrl.endsWith(".xlsx")) {
            fileUrl = "https://docs.google.com/gview?embedded=true&url=" + fileUrl;
        }

        webPreview.loadUrl(fileUrl);
    }

    private String[] parseVimeoUrl(String url) {

        try {
            url = url.replace("https://", "")
                    .replace("http://", "")
                    .replace("www.", "");

            // TYPE 1 → vimeo.com/ID/HASH
            if (url.startsWith("vimeo.com/")) {
                String[] parts = url.split("/");
                if (parts.length >= 3) {
                    return new String[]{parts[1], parts[2]};
                }
            }

            // TYPE 2 → player.vimeo.com/video/ID?h=HASH
            if (url.startsWith("player.vimeo.com/video/")) {
                String idPart = url.split("/video/")[1];
                String videoId = idPart.split("\\?")[0];
                String hash = idPart.contains("h=") ? idPart.split("h=")[1] : "";
                return new String[]{videoId, hash};
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private void playVimeoVideo(String vimeoUrl) {

        playerView.setVisibility(View.VISIBLE);
        webPreview.setVisibility(View.GONE);
        imagePreview.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {

                String[] data = parseVimeoUrl(vimeoUrl);

                if (data == null) {
                    runOnUiThread(() ->
                            Toast.makeText(this, "Invalid Vimeo URL", Toast.LENGTH_SHORT).show());
                    return;
                }

                String videoId = data[0];

                String apiUrl = "https://api.vimeo.com/videos/" + videoId;
                URL url = new URL(apiUrl);
                String authToken = TeacherUtil_SharedPreference.getVideotoken(PreviewActivity.this);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + authToken);
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject json = new JSONObject(result.toString());
                JSONArray downloads = json.getJSONArray("download");

                String mp4Url = null;

                for (int i = 0; i < downloads.length(); i++) {
                    JSONObject file = downloads.getJSONObject(i);
                    if (file.getString("type").equals("video/mp4")) {
                        mp4Url = file.getString("link");
                        break;
                    }
                }

                String finalMp4Url = mp4Url;

                runOnUiThread(() -> {
                    if (finalMp4Url == null) {
                        Toast.makeText(this, "No MP4 found for this Vimeo video", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    startExoPlayer(finalMp4Url);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(this, "Failed to load Vimeo video", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
    private void startExoPlayer(String videoUrl) {

        progressBar.setVisibility(View.GONE);

        exoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(exoPlayer);

        MediaItem item = MediaItem.fromUri(Uri.parse(videoUrl));
        exoPlayer.setMediaItem(item);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) exoPlayer.release();
    }

    @Override
    public void onBackPressed() {
        if (exoPlayer != null) exoPlayer.release();
        super.onBackPressed();
    }
}
