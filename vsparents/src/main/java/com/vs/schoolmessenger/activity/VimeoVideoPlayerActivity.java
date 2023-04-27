package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleObserver;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.RecipientVideoActivity;
import com.vs.schoolmessenger.assignment.ViewTypeActivity;
import com.vs.schoolmessenger.assignment.view.VimeoPlayerView;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.Config;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.videoalbum.Video;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import static com.vs.schoolmessenger.assignment.VideoUpload.imagePathList;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_ASSIGNMENT;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VIDEO;

public class VimeoVideoPlayerActivity extends AppCompatActivity {

    //    private static final int RECOVERY_REQUEST = 1;
//    private YouTubePlayerView youTubeView;
    String VideoID, DETAILID, ISAPPVIEW, vimeoVideo;
    ImageView imgBack;
    //    ProgressDialog pDialog;
    WebView myWebView;
    VideoView videoView;
    ImageView prev, next, pause,imgback;
    SeekBar seekBar;
    int video_index;
    double current_pos, total_duration;
    TextView current, total;
    LinearLayout showProgress;
    Handler mHandler,handler;
    boolean isVisible = true;
    RelativeLayout relativeLayout;

    String isNewVersion;
    Boolean is_Archive;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vimeo_player);
        VideoID = getIntent().getExtras().getString("VIDEO_ID", "");
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

                "<iframe  style=\"position:absolute;top:0;bottom:0;width:100%;height:100%\" src=\""+ VideoID+"\" frameborder=\"0\">" +
                "</iframe>" +
                " </div>" +
                " </body>" +
                " </html> ";

        myWebView.loadData(data_html, "text/html", "UTF-8");

    }

    private void VimeoAPi(String url) {


        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl("https://player.vimeo.com/video/" + url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(VimeoVideoPlayerActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);

        Call<JsonObject> call = service.Videoplay();

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try", "testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        JSONObject obj = object1.getJSONObject("request");
                        JSONObject files = obj.getJSONObject("files");

                        JSONArray progressive = files.getJSONArray("progressive");
                        JSONObject url = progressive.getJSONObject(0);
                        final String playurl = url.getString("url");


                        Log.d("Response sucess", playurl.toString());
                        videoView.setVideoPath(playurl);
                        videoView.start();
                        pause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);

                    } catch (Exception e) {
                        Log.e("VIMEO Exception", e.getMessage());
                    }

                } else {
                    Log.d("Response fail", "fail");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());

            }
        });
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
        TeacherUtil_SharedPreference.putOnBackPressedVideo(VimeoVideoPlayerActivity.this,"1");
        //TeacherUtil_SharedPreference.putOnBackPressed(VoiceCircular.this,"1");
        finish();
    }
}