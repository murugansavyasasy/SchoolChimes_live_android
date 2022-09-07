package com.vs.schoolmessenger.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.AssignmentViewClass;
import com.vs.schoolmessenger.assignment.ImageAdapter;
import com.vs.schoolmessenger.assignment.ImageCircularPop;
import com.vs.schoolmessenger.assignment.ImageCircularassgn;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.SkillAttachmentModel;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.vs.schoolmessenger.util.Util_Common.milliSecondsToTimer;


public class PreviewLSRWScreen extends AppCompatActivity {
    WebView pdfview,myWebView;
    ImageView imgBack;
    ZoomageView imgZoom;
    Button btnNext;
    RelativeLayout rytTitle, rytVoiceLayout, rytTextLayout, rytPdfLayout, rytImageLayout,rytVideoLayout;
    TextView txtmsg, txtType,txtTitle;
    TextView tvDuartion, tvTotDuration;

    SkillAttachmentModel skillmodel;
    String path, AttachmentType,Content;
    ProgressDialog pDialog;

    ImageButton imgplaypause;
    MediaPlayer mediaPlayer = new MediaPlayer();
    int iRequestCode;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int iMediaDuration = 0;
    SeekBar myplayerseekber;
    String isNewVersion;


    private static final String VOICE_FOLDER = "School Voice/Voice";
    //    LinearLayout lnrImages;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_preview_l_s_r_w_screen);

        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Preview LSRW");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgBack = findViewById(R.id.imgBack);
        imgplaypause = findViewById(R.id.imgplaypause);
        txtmsg = findViewById(R.id.txtmsg);
        txtType = findViewById(R.id.txtType);
        rytTitle = findViewById(R.id.rytTitle);
        rytVoiceLayout = findViewById(R.id.rytVoiceLayout);
        rytTextLayout = findViewById(R.id.rytTextLayout);
        rytVideoLayout = findViewById(R.id.rytVideoLayout);
        rytPdfLayout = findViewById(R.id.rytPdfLayout);
        rytImageLayout = findViewById(R.id.rytImageLayout);
        myplayerseekber = findViewById(R.id.myplayerseekber);
        txtTitle = findViewById(R.id.txtTitle);
        pdfview = findViewById(R.id.pdfwebview);
        myWebView = findViewById(R.id.videoWebView);
        imgZoom = findViewById(R.id.imgZoom);
        btnNext = findViewById(R.id.btnNext);
        tvDuartion = (TextView) findViewById(R.id.txtfromduration);
        tvTotDuration = (TextView) findViewById(R.id.txttoduration);

        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(this);

        skillmodel = (SkillAttachmentModel) getIntent().getSerializableExtra("attachement");
        AttachmentType=skillmodel.getType();
        Content=skillmodel.getAttachment();

        SetViewType();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(PreviewLSRWScreen.this,ParentSubmitLSRW.class);
                startActivity(i);
            }
        });


    }



    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        backToResultActvity("SENT");
    }
    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }
    private  void  SetViewType(){

        if (AttachmentType.equals("TEXT")) {
            rytTextLayout.setVisibility(View.VISIBLE);
            txtmsg.setText(Content);

        } else if(AttachmentType.equals("VOICE")) {
            Log.d("AttachmentType",AttachmentType);

            rytVoiceLayout.setVisibility(View.VISIBLE);
            Log.d("ContentVoice",Content);

            AudioPlayMethod(Content);

        }
        else if(AttachmentType.equals("IMAGE")){
            rytImageLayout.setVisibility(View.VISIBLE);
            ViewImage();

        }
        else if(AttachmentType.equals("PDF")){
            rytPdfLayout.setVisibility(View.VISIBLE);
            ViewPdf();

        }
        else if(AttachmentType.equals("VIDEO")){
            rytVideoLayout.setVisibility(View.VISIBLE);
            videoview();
        }
    }

    private  void ViewPdf(){
//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Content));
//        startActivity(browserIntent);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);
        pdfview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();
                }
            }
        });
        pdfview.setWebViewClient(new MyWebViewClient(this));
        pdfview.getSettings().setLoadsImagesAutomatically(true);
        pdfview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = pdfview.getSettings();
        pdfview.getSettings().setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        Log.d("content",Content);
        pdfview.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + Content);
    }

    private void ViewImage() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        Glide.with(this)
                .load(Content)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        onBackPressed();
                        showToast(getResources().getString(R.string.check_internet));
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        mProgressDialog.dismiss();
                        return false;
                    }
                })
                .into(imgZoom);

    }

    private  void videoview(){

        myWebView.setBackgroundColor(getResources().getColor(R.color.clr_black));
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setAppCacheEnabled(true);
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

                "<iframe  style=\"position:absolute;top:0;bottom:0;width:100%;height:100%\" src=\""+ Content+"\" frameborder=\"0\">" +
                "</iframe>" +
                " </div>" +
                " </body>" +
                " </html> ";

        myWebView.loadData(data_html, "text/html", "UTF-8");
    }



    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();

            }
        });

        alertDialog.show();

    }

    private class DownloadFile extends AsyncTask<String, String, String> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private boolean isDownloaded;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(PreviewLSRWScreen.this);
            this.progressDialog.setIndeterminate(true);
            this.progressDialog.setMessage("Downloading...");
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */

        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());


//                         fileName=voiceid+"_"+voicecontent+".mp3";

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf("//") + 1, f_url[0].length());
                Log.d("filename",fileName);
//
//                //Append timestamp to file name
//                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "SchoolAssignment/";

                //Create androiddeft folder if it does not exist
                File directory = new File(folder);

                if (!directory.exists()) {
                    directory.mkdirs();
                }


                // Output stream to write file
                OutputStream output = new FileOutputStream(folder + fileName);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));


                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();
            AudioPlayMethod(path);
        }
    }

    private void AudioPlayMethod(String path) {


        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgplaypause.setImageResource(R.drawable.teacher_ic_play);
                imgplaypause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mediaPlayer.seekTo(0);
            }
        });
        myplayerseekber.setMax(99); // It means 100% .0-99
        myplayerseekber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myplayerseekber) {
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });


        myplayerseekber.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {

                    int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * myplayerseekber.getProgress();

                    mediaPlayer.seekTo(playPositionInMillisecconds);

                }
            }
        });
        Log.d("FetchSong", "Start***************************************");
        try {


            mediaPlayer.reset();
            mediaPlayer.setDataSource(Content);
            mediaPlayer.prepare();
            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();

            iMediaDuration = (int) (mediaPlayer.getDuration() / 1000.0);
            tvTotDuration.setText(Util_Common.milliSecondsToTimer(mediaFileLengthInMilliseconds));

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");



        imgplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

                if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                    imgplaypause.setImageResource(R.drawable.teacher_ic_pause);
                    imgplaypause.setBackgroundColor(getResources().getColor(R.color.clr_red));
                } else {
                    mediaPlayer.pause();
                    imgplaypause.setImageResource(R.drawable.teacher_ic_play);
                    imgplaypause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                }


                primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds);
            }


        });



    }



    private void primarySeekBarProgressUpdater(final int fileLength) {
        int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
        myplayerseekber.setProgress(iProgress);
        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    tvDuartion.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    primarySeekBarProgressUpdater(fileLength);
                }


            };
            handler.postDelayed(notification, 1000);
        }
    }

}
