package com.vs.schoolmessenger.assignment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.CaptureSignature;
import com.vs.schoolmessenger.activity.PdfCircular;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.payment.PdfWebView;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import androidx.appcompat.app.AppCompatActivity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class PdfAppRead extends AppCompatActivity {

    WebView wbTerms;
    ProgressDialog pDialog;
    RelativeLayout rlPdf;
    TextView tvTitle, tvTime, tvStatus;
  //TeacherMessageModel pdfModel;
  MessageModel pdfModel;
    String strURL;

    LinearLayout llBotton;
    TextView tvQuestion, tvSignHere;
    RadioGroup rgAcceptance;
    RadioButton rbYes, rbNo;

    String strAcceptanceStatus = "";
    String isNewVersion;
    Boolean is_Archive;

    public static final int SIGNATURE_ACTIVITY = 1;

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
        setContentView(R.layout.activity_pdf_web_view_popup);

      //  pdfModel = (TeacherMessageModel) getIntent().getSerializableExtra("PDF_ITEM");
        pdfModel = (MessageModel) getIntent().getSerializableExtra("PDF_ITEM");
        strURL = pdfModel.getMsgContent();
        is_Archive = pdfModel.getIs_Archive();
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(PdfAppRead.this);

        Log.d("URL",strURL);
        TeacherUtil_SharedPreference.putOnBackPressedCirculars(PdfAppRead.this,"1");

        ImageView ivBack = (ImageView) findViewById(R.id.pdfPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvBarTitle = (TextView) findViewById(R.id.pdfPopup_ToolBarTvTitle);
        tvBarTitle.setText(pdfModel.getMsgDate());

        rlPdf = findViewById(R.id.rlPdf);
        rlPdf.setVisibility(View.GONE);
        tvTitle = (TextView) findViewById(R.id.pdfPopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.pdfPopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.pdfPopup_tvNew);

        llBotton = (LinearLayout) findViewById(R.id.pdfPopup_llBottom);
        tvQuestion = (TextView) findViewById(R.id.pdfPopup_tvQuestion);
        tvSignHere = (TextView) findViewById(R.id.pdfPopup_tvSign);
        tvSignHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PdfAppRead.this, CaptureSignature.class);
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
        if (pdfModel.getMsgReadStatus().equals("0")) {
            pdfModel.setMsgReadStatus("1");
            tvStatus.setVisibility(View.GONE);

            ChangeMsgReadStatus.changeReadStatus(PdfAppRead.this, pdfModel.getMsgID(), MSG_TYPE_PDF, pdfModel.getMsgDate(),isNewVersion,is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                    pdfModel.setMsgReadStatus("1");
                    if (pdfModel.getMsgReadStatus().equals("0"))
                        tvStatus.setVisibility(View.VISIBLE);
                    else tvStatus.setVisibility(View.GONE);
                }
            });
        }



//        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(strURL));
//        startActivity(browserIntent);
//        finish();

        Intent receipt = new Intent(PdfAppRead.this, PdfWebView.class);
        receipt.putExtra("URL",strURL);
        receipt.putExtra("tittle","Circular");
        receipt.putExtra("ID",pdfModel.getMsgID());
        startActivity(receipt);
        finish();

        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        wbTerms = (WebView) findViewById(R.id.pdfPopup_webView);
        wbTerms.setVisibility(View.GONE);

    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressedCirculars(PdfAppRead.this,"1");
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
