package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.PdfCircularListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;

public class PdfCircular extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;

    RecyclerView rvPdfList;
    PdfCircularListAdapter pdfAdapter;
    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    public ArrayList<MessageModel> OfflinemsgModelList = new ArrayList<>();
    public ArrayList<MessageModel> totalmsgModelList = new ArrayList<>();
    String selDate;
    private int iRequestCode;

    SqliteDB myDb;
    ArrayList<MessageModel> arrayList;

    private final String android_image_urls[] = {
            "http://www.axmag.com/download/pdfurl-guide.pdf",
            "http://www.pdf995.com/samples/pdf.pdf",
            "http://unec.edu.az/application/uploads/2014/12/pdf-sample.pdf"
    };

    private final String android_image_status[] = {"1", "0", "1"};
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;

    ImageView imgSearch;
    TextView Searchable;
    Slider slider;
    ImageView adImage;
    RelativeLayout voice_rlToolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_circular);

        c = Calendar.getInstance();
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        selDate = getIntent().getExtras().getString("HEADER", "");


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Circulars");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ImageView ivBack = (ImageView) findViewById(R.id.pdf_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.pdf_ToolBarTvTitle);
        tvTitle.setText(selDate);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        voice_rlToolbar = (RelativeLayout) findViewById(R.id.pdf_rlToolbar);
        voice_rlToolbar.setVisibility(View.GONE);

        Slider.init(new PicassoImageLoadingService(PdfCircular.this));
        slider = findViewById(R.id.banner);
         adImage = findViewById(R.id.adImage);



        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pdfAdapter == null)
                    return;

                if (pdfAdapter.getItemCount() < 1) {
                    rvPdfList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvPdfList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvPdfList.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.VISIBLE);
                }
                filterlist(editable.toString());
            }
        });


        LoadMore=(TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMorecircularsPdfAPI();


            }
        });
         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(PdfCircular.this);
         seeMoreButtonVisiblity();

        rvPdfList = (RecyclerView) findViewById(R.id.pdf_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvPdfList.setLayoutManager(layoutManager);
        rvPdfList.setItemAnimator(new DefaultItemAnimator());
        pdfAdapter = new PdfCircularListAdapter(PdfCircular.this, msgModelList);
        rvPdfList.setAdapter(pdfAdapter);

    }

    private void filterlist(String s) {
        ArrayList<MessageModel> temp = new ArrayList();
        for (MessageModel d : msgModelList) {

            if (d.getMsgContent().toLowerCase().contains(s.toLowerCase()) || d.getMsgDate().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        pdfAdapter.updateList(temp);
    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    private void LoadMorecircularsPdfAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(PdfCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(PdfCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PdfCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(PdfCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(PdfCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(PdfCircular.this);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID,"PDF",MobileNumber);
        Call<JsonArray> call = apiService.LoadMoreGetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            OfflinemsgModelList.clear();
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),jsonObject.getBoolean("is_Archive"));

                                msgModel.setStrQueryAvailable(jsonObject.getString("Query").toLowerCase());
                                msgModel.setStrQuestion(jsonObject.getString("Question"));
                                msgModelList.add(msgModel);
                                OfflinemsgModelList.add(msgModel);
                            }



                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            pdfAdapter.notifyDataSetChanged();

                        } else {
                            showRecordsfound(strMessage);
                        }
                    } else {
                        showRecordsfound(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ShowAds.getAds(this,adImage,slider,"");
        if (isWriteExternalPermissionGranted())
            if (isNetworkConnected()) {
                circularsPdfAPI();
            }
    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PdfCircular.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(R.string.connect_internet);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) PdfCircular.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void showToast(String msg) {
        Toast.makeText(PdfCircular.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void prepareData() {
        for (int i = 0; i < android_image_urls.length; i++) {
            MessageModel model = new MessageModel();
            model.setMsgID(String.valueOf(i));
            long unixTime = System.currentTimeMillis() / 1000L;
            model.setMsgTitle(String.valueOf(unixTime));
            model.setMsgContent(android_image_urls[i]);
            model.setMsgDate("10 May 2017");
            model.setMsgTime("11:30 AM");
            model.setMsgReadStatus(android_image_status[i]);
            msgModelList.add(model);
        }

        pdfAdapter.notifyDataSetChanged();
    }

    private void circularsForGivenDateAPI2() {
        try {
            JSONArray js = new JSONArray("");
            if (js.length() > 0) {
                JSONObject jsonObject = js.getJSONObject(0);
                String strDate = jsonObject.getString("ID");
                String strTotalSMS = jsonObject.getString("URL");

                if (!strDate.equals("")) {
                    MessageModel msgModel;
                    Log.d("json length", js.length() + "");

                    pdfAdapter.clearAllData();

                    for (int i = 0; i < js.length(); i++) {
                        jsonObject = js.getJSONObject(i);
                        msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);

                        msgModel.setStrQueryAvailable(jsonObject.getString("Query").toLowerCase());
                        msgModel.setStrQuestion(jsonObject.getString("Question"));

                        msgModelList.add(msgModel);
                    }

                    pdfAdapter.notifyDataSetChanged();

                } else {
                    showToast(strTotalSMS);
                }
            } else {
                showToast(getResources().getString(R.string.no_records));
            }

        } catch (Exception e) {
            Log.e("TextMsg:Exception", e.getMessage());
        }
    }

    private void circularsForGivenDateAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(PdfCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(PdfCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = Util_JsonRequest.getJsonArray_GetFiles(selDate, strChildID, strSchoolID, MSG_TYPE_PDF);
        Call<JsonArray> call = apiService.GetFiles(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strDate = jsonObject.getString("ID");
                        String strTotalSMS = jsonObject.getString("URL");

                        if (!strDate.equals("")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            pdfAdapter.clearAllData();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);

                                msgModel.setStrQueryAvailable(jsonObject.getString("Query").toLowerCase());
                                msgModel.setStrQuestion(jsonObject.getString("Question"));

                                msgModelList.add(msgModel);
                            }

                            pdfAdapter.notifyDataSetChanged();

                        } else {
                            showToast(strTotalSMS);
                        }
                    } else {
                        showToast("Server Response Failed. Try again");
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }
    private void circularsPdfAPI() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(PdfCircular.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(PdfCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PdfCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(PdfCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(PdfCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(PdfCircular.this);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID,"PDF",MobileNumber);
        Call<JsonArray> call = apiService.GetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if(isNewVersion.equals("1")){
                            LoadMore.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        pdfAdapter.clearAllData();
                        totalmsgModelList.clear();

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);

                                msgModel.setStrQueryAvailable(jsonObject.getString("Query").toLowerCase());
                                msgModel.setStrQuestion(jsonObject.getString("Question"));
                                msgModelList.add(msgModel);
                                totalmsgModelList.add(msgModel);
                            }



                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);
                            pdfAdapter.notifyDataSetChanged();

                        } else {

                            if(isNewVersion.equals("1")){
                                lblNoMessages.setVisibility(View.VISIBLE);
                                lblNoMessages.setText(strMessage);

                                String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodCirculars(PdfCircular.this);
                                if(loadMoreCall.equals("1")){
                                    TeacherUtil_SharedPreference.putOnBackPressedCirculars(PdfCircular.this,"");
                                    LoadMorecircularsPdfAPI();
                                }
                            }
                            else {
                                lblNoMessages.setVisibility(View.GONE);
                                showRecordsfound(strMessage);
                            }


                        }
                    }

                    else {
                        showRecordsfound(getResources().getString(R.string.no_records));
                    }


                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showRecordsfound(String strMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PdfCircular.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMessage);


        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();


            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    public boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
                return true;
            } else {

                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("SDCard_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    circularsPdfAPI();
                }
                return;
            }
        }
    }
}
