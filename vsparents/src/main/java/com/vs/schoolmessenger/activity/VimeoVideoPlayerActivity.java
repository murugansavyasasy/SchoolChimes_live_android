package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VIDEO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.assignment.view.VimeoPlayerView;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.VimeoHelper;

import java.io.File;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class VimeoVideoPlayerActivity extends AppCompatActivity implements VimeoHelper.VimeoDownloadCallback {

    private static final String VIDEO_FOLDER = "//SchoolChimesVideo";
    String VideoID, DETAILID, ISAPPVIEW;
    ImageView imgBack;
    WebView myWebView;
    VideoView videoView;
    ImageView prev, next, pause;
    SeekBar seekBar;
    double current_pos, total_duration;
    TextView current, total;
    LinearLayout showProgress;
    Handler mHandler, handler;
    String downloadVideoId;
    String isNewVersion;
    Boolean is_Archive;
    ImageView imgDownload;
    RelativeLayout RlaDownload;
    boolean isDownload = true;
    String isVideoDownloadId;
    String isVideoTitle;
    int progressLoading = 0;
    ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vimeo_player);
        VideoID = getIntent().getExtras().getString("VIDEO_ID", "");
        downloadVideoId = getIntent().getExtras().getString("Video_id", "");
        DETAILID = getIntent().getExtras().getString("DETAILID", "");
        ISAPPVIEW = getIntent().getExtras().getString("ISAPPVIEW", "");
        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isDownload = getIntent().getExtras().getBoolean("is_Download", false);
        isVideoDownloadId = getIntent().getExtras().getString("isVideoDownloadId", "");
        isVideoTitle = getIntent().getExtras().getString("isVideoTitle", "");

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressedVideo(VimeoVideoPlayerActivity.this, "1");
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                finish();
            }
        });
        myWebView = findViewById(R.id.myWebView);
        videoView = findViewById(R.id.videoview);
        imgDownload = findViewById(R.id.imgDownload);
        RlaDownload = findViewById(R.id.RlaDownload);

        if (isDownload) {
            RlaDownload.setVisibility(View.VISIBLE);
        } else {
            RlaDownload.setVisibility(View.GONE);
        }

        VimeoPlayerView vimeoPlayer = findViewById(R.id.vimeoPlayer);
        vimeoPlayer.setVisibility(View.GONE);
        videoView.setVisibility(View.GONE);

        prev = (ImageView) findViewById(R.id.prev);
        next = (ImageView) findViewById(R.id.next);
        pause = (ImageView) findViewById(R.id.pause);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        current = (TextView) findViewById(R.id.current);
        total = (TextView) findViewById(R.id.total);
        showProgress = (LinearLayout) findViewById(R.id.showProgress);
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VimeoVideoPlayerActivity.this);


        if (ISAPPVIEW.equals("0")) {
            ChangeMsgReadStatus.changeReadStatus(VimeoVideoPlayerActivity.this, DETAILID, MSG_TYPE_VIDEO, "", isNewVersion, is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                }
            });
        }
        mHandler = new Handler();
        handler = new Handler();
        setPause();

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onBackPressed();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
            }
        });
        myWebView.setBackgroundColor(getResources().getColor(R.color.clr_black));
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setSupportZoom(false);
        myWebView.getSettings().setBuiltInZoomControls(false);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.setScrollContainer(false);
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(false);

        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                return true;
            }
        });
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
                if (progress == 100) {
//                    myWebView.loadUrl(playurl);
                }
            }
        });
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        String VIDEO_URL = "https://player.vimeo.com/video/" + "425233784";
        String data_html = "<!DOCTYPE html><html> " +
                "<head>" +
                " <meta charset=\"UTF-8\">" +
                "</head>" +
                " <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> " +
                "<div class=\"vimeo\">" +
                "<iframe  style=\"position:absolute;top:0;bottom:0;width:100%;height:100%\" src=\"" + VideoID + "\" frameborder=\"0\">" +
                "</iframe>" +
                " </div>" +
                " </body>" +
                " </html> ";
        myWebView.loadData(data_html, "text/html", "UTF-8");

        RlaDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDownload = true;
                Log.d("isVideoDownloadId", isVideoDownloadId);
                String authToken = TeacherUtil_SharedPreference.getVideotoken(VimeoVideoPlayerActivity.this);
                VimeoHelper.getVimeoDownloadUrl(isVideoDownloadId,authToken, VimeoVideoPlayerActivity.this);

            }
        });
    }

    public static boolean isVideoDownloaded(String isVideoTitle) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), VIDEO_FOLDER);
        File videoFile = new File(directory, isVideoTitle);
        return videoFile.exists();
    }

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
    public void onDownloadUrlRetrieved(String quality, String downloadUrl) {
        if (isDownload) {
            isDownload = false;
            if (!isVideoDownloaded(isVideoTitle)) {
                downloadVideo(VimeoVideoPlayerActivity.this, downloadUrl);
            } else {
                ((Activity) VimeoVideoPlayerActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(VimeoVideoPlayerActivity.this, R.string.Video_already_downloaded, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void onError(String errorMessage) {
        // Handle error
        Log.e("MainActivity", "Error: " + errorMessage);

        ((Activity) VimeoVideoPlayerActivity.this).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(VimeoVideoPlayerActivity.this, R.string.Error + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void downloadVideo(final Context context, String downloadUrl) {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Inflate the custom layout for the dialog
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_progress, null);

                progressBar = dialogView.findViewById(R.id.progress_bar);
                TextView progressText = dialogView.findViewById(R.id.progress_text);

                progressLoading = 13;
                progressText.setText(progressLoading + "%");
                progressBar.setProgress(progressLoading);
                // Create and show the AlertDialog on the main thread
                AlertDialog progressDialog = new AlertDialog.Builder(context)
                        .setView(dialogView)
                        .setCancelable(false)
                        .create();
                progressDialog.show();
                progressLoading = 27;
                progressText.setText(progressLoading + "%");
                progressBar.setProgress(progressLoading);
                // Initialize the download after showing the dialog
                startDownload(context, downloadUrl, progressDialog, progressBar, progressText);
            }
        });
    }

    private void startDownload(Context context, String downloadUrl, AlertDialog progressDialog, ProgressBar progressBar, TextView progressText) {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), VIDEO_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        Uri uri = Uri.parse(downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, VIDEO_FOLDER + "/" + isVideoTitle);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);
            progressLoading = 49;
            progressText.setText(progressLoading + "%");
            progressBar.setProgress(progressLoading);
            // Handler to periodically check download progress
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor cursor = downloadManager.query(query);

                    if (cursor != null && cursor.moveToFirst()) {
                        @SuppressLint("Range") int bytesDownloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        @SuppressLint("Range") int totalBytes = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                        progressLoading = 61;
                        progressText.setText(progressLoading + "%");
                        progressBar.setProgress(progressLoading);
                        if (totalBytes > 0) {
//                            int progress = (int) ((bytesDownloaded * 100L) / totalBytes);
//                            progressBar.setProgress(progress);
//                            progressText.setText(progress + "%");
                            progressLoading = 91;
                            progressText.setText(progressLoading + "%");
                            progressBar.setProgress(progressLoading);
                        }

                        @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) {
                            cursor.close();
                            progressDialog.dismiss(); // Dismiss dialog on completion or failure
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                progressLoading = 100;
                                progressText.setText(progressLoading + "%");
                                progressBar.setProgress(progressLoading);
                                showAlert((Activity) context, getResources().getString(R.string.Downloaded_successfully), R.string.File_stored + VIDEO_FOLDER + "/" + isVideoTitle);
                            } else if (status == DownloadManager.STATUS_FAILED) {
                                showAlert((Activity) context, getResources().getString(R.string.Download_failed), "");
                            }
                            return;
                        }

                        cursor.close();
                    }

                    // Re-run this block after a short delay to update the progress
                    handler.postDelayed(this, 500);
                }
            });
        }
    }

    public void setVideoProgress() {
        //get the video duration
        current_pos = videoView.getCurrentPosition();
        total_duration = videoView.getDuration();

        total.setText(timeConversion((long) total_duration));
        current.setText(timeConversion((long) current_pos));
        seekBar.setMax((int) total_duration);
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = videoView.getCurrentPosition();
                    current.setText(timeConversion((long) current_pos));
                    seekBar.setProgress((int) current_pos);
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed) {
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                current_pos = seekBar.getProgress();
                videoView.seekTo((int) current_pos);
            }
        });
    }

    //pause video
    public void setPause() {
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    videoView.pause();
                    pause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                } else {
                    videoView.start();
                    pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                }
            }
        });
    }

    //time conversion
    public String timeConversion(long value) {
        String songTime;
        int dur = (int) value;
        int hrs = (dur / 3600000);
        int mns = (dur / 60000) % 60000;
        int scs = dur % 60000 / 1000;

        if (hrs > 0) {
            songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            songTime = String.format("%02d:%02d", mns, scs);
        }
        return songTime;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        TeacherUtil_SharedPreference.putOnBackPressedVideo(VimeoVideoPlayerActivity.this, "1");
        finish();
    }
}