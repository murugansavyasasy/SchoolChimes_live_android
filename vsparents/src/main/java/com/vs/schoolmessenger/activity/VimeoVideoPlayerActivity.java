package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VIDEO;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.view.VimeoPlayerView;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.io.File;
import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class VimeoVideoPlayerActivity extends AppCompatActivity {


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
    private static final String VIDEO_FOLDER = "//SchoolChimesVideo";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vimeo_player);
        VideoID = getIntent().getExtras().getString("VIDEO_ID", "");
        downloadVideoId = getIntent().getExtras().getString("Video_id", "");
        DETAILID = getIntent().getExtras().getString("DETAILID", "");
        ISAPPVIEW = getIntent().getExtras().getString("ISAPPVIEW", "");
        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);

        imgBack = (ImageView) findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressedVideo(VimeoVideoPlayerActivity.this,"1");
                if (videoView.isPlaying()) {
                    videoView.pause();
                }
                finish();
            }
        });
        myWebView = findViewById(R.id.myWebView);
        videoView = findViewById(R.id.videoview);
        imgDownload = findViewById(R.id.imgDownload);

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
            ChangeMsgReadStatus.changeReadStatus(VimeoVideoPlayerActivity.this, DETAILID, MSG_TYPE_VIDEO, "",isNewVersion,is_Archive, new OnRefreshListener() {
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

        String VIDEO_URL="https://player.vimeo.com/video/"+ "425233784";
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

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("downloadVideoId",String.valueOf(downloadVideoId));
                getVideoDownloadLink(downloadVideoId);
            }
        });
    }

    public void getVideoDownloadLink(String isVideoId) {

        String API_URL = "https://api.vimeo.com/videos/";
        String BEARER_TOKEN = "8d74d8bf6b5742d39971cc7d3ffbb51a";
        OkHttpClient client = new OkHttpClient();

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL + isVideoId)
                .header("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        // Make the call asynchronously
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    System.out.println("Response: " + responseData);

                    JsonObject jsonObject = JsonParser.parseString(responseData).getAsJsonObject();
                    String description = jsonObject.get("description").getAsString();
                    System.out.println("Description: " + description);
                    JsonObject liveObject = jsonObject.getAsJsonObject("embed")
                            .getAsJsonObject("badges")
                            .getAsJsonObject("live");
                    boolean isLive = liveObject.get("streaming").getAsBoolean();
                    System.out.println("Is live: " + isLive);


                    if (!isVideoDownloaded()) {
                        downloadVideo(VimeoVideoPlayerActivity.this);
                    } else {
                        ((Activity) VimeoVideoPlayerActivity.this).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(VimeoVideoPlayerActivity.this, "Video is already downloaded!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    System.out.println("Request failed. Response code: " + response.code());
                }
            }
        });
    }


    public void downloadVideo(final Context context) {
        String url = "https://player.vimeo.com/progressive_redirect/download/1026844236/container/cc3c9cca-d29d-4051-9ed4-270ae5d3c2bf/f858b363/test_for_download%20%28360p%29.mp4?expires=1731051047&loc=external&oauth2_token_id=1346973768&signature=65b883e59a04e61156286392b5bec0b8969417fbeacd0ff8bd97e1f229392710";

        // Path to the public Downloads directory
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), VIDEO_FOLDER);
        if (!directory.exists()) {
            directory.mkdirs(); // Make sure the directory exists
        }

        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Set the destination directory and filename
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, VIDEO_FOLDER + "/test_for_download.mp4");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Get system DownloadManager and start the download
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        if (downloadManager != null) {
            long downloadId = downloadManager.enqueue(request);

            // Register a BroadcastReceiver to listen for download completion
            IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        long completedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                        if (completedDownloadId == downloadId) {
                            // Query the DownloadManager to check the status of the download
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(completedDownloadId);
                            Cursor cursor = downloadManager.query(query);

                            if (cursor != null && cursor.moveToFirst()) {
                                @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                    showAlert((Activity) context, "Downloaded successfully..", "File stored in: " + VIDEO_FOLDER + "/" + "test_for_download.mp4");
                                } else if (status == DownloadManager.STATUS_FAILED) {
                                    showAlert((Activity) context, "Download failed.", "");
                                }
                                cursor.close();
                            }
                        }
                    }
                }, filter, Context.RECEIVER_NOT_EXPORTED);
            }

            // Show a toast to notify that the download is starting
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Downloading video...", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public static boolean isVideoDownloaded() {
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), VIDEO_FOLDER);
        File videoFile = new File(directory, "test_for_download.mp4");
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
                } catch (IllegalStateException ed){
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