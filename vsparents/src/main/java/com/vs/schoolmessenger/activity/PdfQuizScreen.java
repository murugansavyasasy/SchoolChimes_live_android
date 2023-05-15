package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SkillAttachmentModel;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.Util_Common.milliSecondsToTimer;


public class PdfQuizScreen extends AppCompatActivity {
    WebView pdfview,myWebView;
    ImageView imgBack;
    ZoomageView imgZoom;
    Button btnNext;
    RelativeLayout rytTitle, rytVoiceLayout, rytTextLayout, rytPdfLayout, rytImageLayout,rytVideoLayout;
    TextView txtmsg, txtType,txtTitle;
    TextView tvDuartion, tvTotDuration;

    String path, AttachmentType,Content;
    ProgressDialog pDialog;

    ImageButton imgplaypause;
    MediaPlayer mediaPlayer = new MediaPlayer();
    int iRequestCode;
    SeekBar myplayerseekber;
    String isNewVersion;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_preview_l_s_r_w_screen);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Pdf");
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

        AttachmentType=getIntent().getStringExtra("TYPE");
        Content=getIntent().getStringExtra("CONTENT");

        SetViewType();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

         if(AttachmentType.equals("PDF")){
            rytPdfLayout.setVisibility(View.VISIBLE);
            ViewPdf();

        }

    }

    private  void ViewPdf(){
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


}
