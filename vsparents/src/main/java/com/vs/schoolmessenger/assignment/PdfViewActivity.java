package com.vs.schoolmessenger.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ImageListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.MyWebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;


public class PdfViewActivity extends AppCompatActivity {
    WebView pdfview;
    ImageView imgBack,img1,img2,img3,img4;
    RelativeLayout rytTitle, rytVoiceLayout, rytTextLayout, rytPdfLayout, rytImageLayout;
    TextView  txtmsg, txtType,lblImageCount,lbldescription;
    LinearLayout lnrImages;

    String Pdf,description;
    ProgressDialog pDialog;

    ImageButton imgplaypause;
    SeekBar myplayerseekber;

    FrameLayout frameLayout;

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
        setContentView(R.layout.activity_assignment_viewtype);

        imgBack = findViewById(R.id.imgBack);
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);
        img4 = findViewById(R.id.img4);
        imgplaypause = findViewById(R.id.imgplaypause);
        txtmsg = findViewById(R.id.txtmsg);
        lbldescription = findViewById(R.id.txt_description);
        lblImageCount = findViewById(R.id.lblImageCount);
        frameLayout = findViewById(R.id.frameLayout1);
        txtType = findViewById(R.id.txtType);
        rytTitle = findViewById(R.id.rytTitle);
        rytVoiceLayout = findViewById(R.id.rytVoiceLayout);
        rytTextLayout = findViewById(R.id.rytTextLayout);
        rytPdfLayout = findViewById(R.id.rytPdfLayout);
        rytImageLayout = findViewById(R.id.rytImageLayout);
        myplayerseekber = findViewById(R.id.myplayerseekber);
        lnrImages = findViewById(R.id.lnrImages);

        pdfview = findViewById(R.id.pdfwebview);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Pdf=getIntent().getExtras().getString("PDF","");
        description=getIntent().getExtras().getString("DESC","");
        if(description.equals("")||description.isEmpty()){
            lbldescription.setVisibility(View.GONE);
        }
        else{
            lbldescription.setVisibility(View.VISIBLE);
            lbldescription.setText(description);
        }


        rytPdfLayout.setVisibility(View.VISIBLE);
        txtType.setText("PDF");
        pDialog = new ProgressDialog(PdfViewActivity.this);
        pDialog.setMessage(getResources().getString(R.string.Loading));
        pDialog.setCancelable(false);

        pdfview.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                pDialog.show();
                setProgress(progress * 100);
                //Make the bar disappear after URL is loaded

                if (progress == 100) {
                    pDialog.dismiss();

                }
            }
        });
        pdfview.setWebViewClient(new MyWebViewClient(PdfViewActivity.this));
        pdfview.getSettings().setLoadsImagesAutomatically(true);
        pdfview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = pdfview.getSettings();
        pdfview.getSettings().setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);

//            https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf
        pdfview.loadUrl("https://docs.google.com/viewerng/viewer?url=" + Pdf);
    }

}
