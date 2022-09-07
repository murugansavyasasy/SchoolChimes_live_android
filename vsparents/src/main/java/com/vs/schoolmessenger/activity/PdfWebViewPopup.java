package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;

public class PdfWebViewPopup extends AppCompatActivity {

    WebView wbTerms;
    ProgressDialog pDialog;

    TextView tvTitle, tvTime, tvStatus;
  TeacherMessageModel pdfModel;
    String strURL;

    LinearLayout llBotton;
    TextView tvQuestion, tvSignHere;
    RadioGroup rgAcceptance;
    RadioButton rbYes, rbNo;

    String strAcceptanceStatus = "";
    Boolean is_Archive;
    String isNewVersion;

    public static final int SIGNATURE_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_web_view_popup);

        pdfModel = (TeacherMessageModel) getIntent().getSerializableExtra("PDF_ITEM");
        strURL = pdfModel.getMsgContent();

        Log.d("URL",strURL);

        ImageView ivBack = (ImageView) findViewById(R.id.pdfPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion= TeacherUtil_SharedPreference.getNewVersion(PdfWebViewPopup.this);

        TextView tvBarTitle = (TextView) findViewById(R.id.pdfPopup_ToolBarTvTitle);
        tvBarTitle.setText(pdfModel.getMsgDate());

        tvTitle = (TextView) findViewById(R.id.pdfPopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.pdfPopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.pdfPopup_tvNew);

        llBotton = (LinearLayout) findViewById(R.id.pdfPopup_llBottom);
        tvQuestion = (TextView) findViewById(R.id.pdfPopup_tvQuestion);
        tvSignHere = (TextView) findViewById(R.id.pdfPopup_tvSign);
        tvSignHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PdfWebViewPopup.this, CaptureSignature.class);
                intent.putExtra("MSG_ID", pdfModel.getMsgID());
                intent.putExtra("ANSWER", strAcceptanceStatus);
                startActivityForResult(intent, SIGNATURE_ACTIVITY);
            }
        });

        rgAcceptance = (RadioGroup) findViewById(R.id.pdfPopup_rgAcceptance);
        rbYes = (RadioButton) findViewById(R.id.pdfPopup_rbYes);
        rbNo = (RadioButton) findViewById(R.id.pdfPopup_rbNo);

        rgAcceptance.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup rg, int arg1) {
                int ids = rg.getCheckedRadioButtonId();
                tvSignHere.setVisibility(View.VISIBLE);

                if (ids == R.id.pdfPopup_rbYes) {
                    strAcceptanceStatus = "Yes";
                } else if (ids == R.id.pdfPopup_rbNo) {
                    strAcceptanceStatus = "No";
                }

            }
        });

        tvTitle.setText(pdfModel.getMsgTitle());
        tvTime.setText(pdfModel.getMsgTime());
        if (pdfModel.getMsgReadStatus().equals("0"))
            tvStatus.setVisibility(View.VISIBLE);
        else tvStatus.setVisibility(View.GONE);

        if (pdfModel.getStrQueryAvailable().equals("y"))
            llBotton.setVisibility(View.VISIBLE);
        else llBotton.setVisibility(View.GONE);

        tvQuestion.setText(pdfModel.getStrQuestion());


        ///getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);

//        wbTerms = (WebView) findViewById(R.id.pdfPopup_webView);
//        pDialog = new ProgressDialog(PdfWebViewPopup.this);
//        pDialog.setMessage("Loading");
//        pDialog.setCancelable(false);

//        wbTerms.setWebChromeClient(new WebChromeClient() {
//            public void onProgressChanged(WebView view, int progress) {
//                //Make the bar disappear after URL is loaded, and changes string to Loading...
////                setTitle("Loading...");
//                pDialog.show();
//                setProgress(progress * 100); //Make the bar disappear after URL is loaded
//
//                if (progress == 100) {
//                    pDialog.dismiss();
//
//                    if (pdfModel.getMsgReadStatus().equals("0")) {
//                        pdfModel.setMsgReadStatus("1");
//                        tvStatus.setVisibility(View.GONE);
//
//                        ChangeMsgReadStatus.changeReadStatus(PdfWebViewPopup.this, pdfModel.getMsgID(), MSG_TYPE_PDF, pdfModel.getMsgDate(),isNewVersion,is_Archive, new OnRefreshListener() {
//                            @Override
//                            public void onRefreshItem() {
//                                pdfModel.setMsgReadStatus("1");
//                                if (pdfModel.getMsgReadStatus().equals("0"))
//                                    tvStatus.setVisibility(View.VISIBLE);
//                                else tvStatus.setVisibility(View.GONE);
//                            }
//                        });
//                    }
//                }
//            }
//        });

        if (pdfModel.getMsgReadStatus().equals("0")) {
            pdfModel.setMsgReadStatus("1");
            tvStatus.setVisibility(View.GONE);

            ChangeMsgReadStatus.changeReadStatus(PdfWebViewPopup.this, pdfModel.getMsgID(), MSG_TYPE_PDF, pdfModel.getMsgDate(),isNewVersion,is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                    pdfModel.setMsgReadStatus("1");
                    if (pdfModel.getMsgReadStatus().equals("0"))
                        tvStatus.setVisibility(View.VISIBLE);
                    else tvStatus.setVisibility(View.GONE);
                }
            });
        }

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strURL));
        startActivity(browserIntent);
        finish();

        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        wbTerms = (WebView) findViewById(R.id.pdfPopup_webView);
        wbTerms.setVisibility(View.GONE);

//        wbTerms.setWebViewClient(new MyWebViewClient(PdfWebViewPopup.this));
//        wbTerms.getSettings().setLoadsImagesAutomatically(true);
//        wbTerms.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        WebSettings webSettings = wbTerms.getSettings();
//        wbTerms.getSettings().setBuiltInZoomControls(true);
//        webSettings.setJavaScriptEnabled(true);
//        wbTerms.loadUrl("https://docs.google.com/viewerng/viewer?url=" + strURL);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        signVisiblityFalse(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case SIGNATURE_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    String status = bundle.getString("status");

                    if (status.equalsIgnoreCase("done")) {
                        onBackPressed();
                    }
                }
                break;
        }

    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, 105, 50);
        toast.show();
    }

    private void signVisiblityFalse(boolean bFlag) {
        if (!bFlag) {
            tvSignHere.setVisibility(View.GONE);
            rbNo.setChecked(false);
            rbYes.setChecked(false);
        }
    }
}
