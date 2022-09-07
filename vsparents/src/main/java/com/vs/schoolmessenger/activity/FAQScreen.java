package com.vs.schoolmessenger.activity;

import android.app.ActionBar;
import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.FAQAdapter;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.FAQModel;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class FAQScreen extends AppCompatActivity {

    WebView pdfPopup_webView;
    ProgressDialog pDialog;

    String pdfURL="";

    RecyclerView faq_recycle;
    private List<FAQModel> FAQList = new ArrayList<>();
    public FAQAdapter mAdapter;
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();

    String MemberID="";
    String UserType="",type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqscreen);

        faq_recycle = (RecyclerView) findViewById(R.id.faq_recycle);

        childList = TeacherUtil_SharedPreference.getChildrenDetails(FAQScreen.this, "child_list");
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(FAQScreen.this, "schools_list");


        Bundle extras = getIntent().getExtras();

        if(extras!=null) {
             type = extras.getString("School", "");
        }

        if(type.equals("School")){
            UserType="2";
            if (schools_list != null) {
                final TeacherSchoolsModel model = schools_list.get(0);
                MemberID=model.getStrStaffID();
                }
            }
        else {
            UserType="3";
            if (childList != null) {

                final Profiles model = childList.get(0);
                MemberID=model.getChildID();
                }
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.faq);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        getFAQuestions();

    }

    private void getFAQuestions() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(FAQScreen.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(FAQScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(FAQScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);




        Call<JsonObject> call = apiService.getFAQLink(MemberID,UserType);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject js = new JSONObject(response.body().toString());
                         String status=js.getString("Status");

                         if(status.equals("1")) {
                             pdfURL = js.getString("Message");
                             loadFAQ(pdfURL);
                         }

                         } else {
                        Toast.makeText(getApplicationContext(),  getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(),  getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadFAQ(String pdfURL) {

        pdfPopup_webView=(WebView) findViewById(R.id.pdfPopup_webView);
        pDialog = new ProgressDialog(FAQScreen.this);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);

        pdfPopup_webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                //Make the bar disappear after URL is loaded, and changes string to Loading...
//                setTitle("Loading...");
                pDialog.show();
                setProgress(progress * 100); //Make the bar disappear after URL is loaded

                // Return the app name after finish loading
                if (progress == 100) {
//                    setTitle(R.string.app_name);
                    pDialog.dismiss();


                }
            }
        });

        WebSettings webSettings = pdfPopup_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        pdfPopup_webView.getSettings().setBuiltInZoomControls(true);
        pdfPopup_webView.loadUrl(pdfURL);




    }
}
