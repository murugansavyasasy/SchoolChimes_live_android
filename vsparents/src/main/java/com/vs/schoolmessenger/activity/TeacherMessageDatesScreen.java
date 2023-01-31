package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherCircularsDateListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherCircularDates;
import com.vs.schoolmessenger.model.TeacherProfiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MESSAGESFROMMANAGEMENT;


public class TeacherMessageDatesScreen extends AppCompatActivity {

    String staff_id, school_id;
    TeacherProfiles childItem = new TeacherProfiles();

    TeacherSchoolsModel schoolmodel;

    int iTotMsgUnreadCount = 0;
    TextView tvSchoolName, tvSchoolAddress, tvMsgCount;
    NetworkImageView nivThumbNailSchoolImg;
    ImageLoader imageLoader;

    RecyclerView rvDatesList;
    private TeacherCircularsDateListAdapter dateListAdapter;
    private ArrayList<TeacherCircularDates> datesList = new ArrayList<>();
    private int iRequestCode;

    String isNewVersion;
    TextView LoadMore;
    TextView lblNoMessages;

    ImageView imgSearch;
    TextView Searchable;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_message_dates_screen);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        if (iRequestCode == PRINCIPAL_MESSAGESFROMMANAGEMENT) {
            staff_id = getIntent().getExtras().getString("STAFF_ID", "");
            school_id = getIntent().getExtras().getString("SCHOOL_ID", "");
        } else {
            schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
            staff_id = schoolmodel.getStrStaffID();
            school_id = schoolmodel.getStrSchoolID();
        }
        Util_SharedPreference.putStaffDetails(TeacherMessageDatesScreen.this, staff_id, school_id);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.messages);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.from_management);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imgVoiceSnap)).setVisibility(View.GONE);


        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (dateListAdapter == null)
                    return;

                if (dateListAdapter.getItemCount() < 1) {
                    rvDatesList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvDatesList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvDatesList.setVisibility(View.VISIBLE);
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



        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreGetDetails();
            }
        });
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherMessageDatesScreen.this);
        seeMoreButtonVisiblity();


        rvDatesList = (RecyclerView) findViewById(R.id.dates_rvChildList);
        dateListAdapter = new TeacherCircularsDateListAdapter(TeacherMessageDatesScreen.this, datesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvDatesList.setHasFixedSize(true);
        rvDatesList.setLayoutManager(mLayoutManager);
        rvDatesList.setItemAnimator(new DefaultItemAnimator());
        rvDatesList.setAdapter(dateListAdapter);
        rvDatesList.getRecycledViewPool().setMaxRecycledViews(0, 80);

    }

    private void filterlist(String s) {
        List<TeacherCircularDates> temp = new ArrayList();
        for (TeacherCircularDates d : datesList) {

            if (d.getCircularDate().toLowerCase().contains(s.toLowerCase()) || d.getCircularDay().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }
        }
        dateListAdapter.updateList(temp);

    }

    private void LoadMoreGetDetails() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(TeacherMessageDatesScreen.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherMessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherMessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MemberId", staff_id);
        jsonObject.addProperty("SchoolId", school_id);

        Log.d(" jsonObject12344555", String.valueOf(jsonObject));

        Call<JsonArray> call = apiService.GetMessageCount_Archive(jsonObject);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("overallUnreadCount:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());
                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        TeacherCircularDates cirDates;
                        Log.d("json length", js.length() + "");

//                        dateListAdapter.clearAllData();
                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);

                            String date = jsonObject.getString("Date");
                            if (!date.equals("0")) {
                                cirDates = new TeacherCircularDates(jsonObject.getString("Date"), jsonObject.getString("Day"),
                                        jsonObject.getString("TotalVOICE"), jsonObject.getString("UnreadVOICE"),
                                        jsonObject.getString("TotalSMS"), jsonObject.getString("UnreadSMS"),
                                        jsonObject.getString("TotalIMG"), jsonObject.getString("UnreadIMG"),
                                        jsonObject.getString("TotalPDF"), jsonObject.getString("UnreadPDF"),
                                        jsonObject.getString("TotalVIDEO"), jsonObject.getString("UnreadVIDEO"),jsonObject.getBoolean("is_Archive"));

                                datesList.add(cirDates);

                            } else {
                                showRecords(jsonObject.getString("Day"));

                            }
                        }
                        dateListAdapter.notifyDataSetChanged();
                    } else {
                        showRecords(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Unreadcount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Unreadcount:Failure", t.toString());
            }
        });

    }

    private void seeMoreButtonVisiblity() {
           isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherMessageDatesScreen.this);
           LoadMore.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDetails();
    }

    private void getDetails() {

        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherMessageDatesScreen.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherMessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherMessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MemberId", staff_id);
        jsonObject.addProperty("SchoolId", school_id);
        Log.d(" jsonObject12344555", String.valueOf(jsonObject));
        Call<JsonArray> call = apiService.GetMessageCount(jsonObject);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("overallUnreadCount:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        TeacherCircularDates cirDates;
                        Log.d("json length", js.length() + "");

                        dateListAdapter.clearAllData();
                        datesList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);

                            String date = jsonObject.getString("Date");
                            if (!date.equals("0")) {
                                cirDates = new TeacherCircularDates(jsonObject.getString("Date"), jsonObject.getString("Day"),
                                        jsonObject.getString("TotalVOICE"), jsonObject.getString("UnreadVOICE"),
                                        jsonObject.getString("TotalSMS"), jsonObject.getString("UnreadSMS"),
                                        jsonObject.getString("TotalIMG"), jsonObject.getString("UnreadIMG"),
                                        jsonObject.getString("TotalPDF"), jsonObject.getString("UnreadPDF"),
                                        jsonObject.getString("TotalVIDEO"), jsonObject.getString("UnreadVIDEO"),false);
                                datesList.add(cirDates);

                            } else {
                                if (isNewVersion.equals("1")) {
                                    lblNoMessages.setVisibility(View.VISIBLE);
                                    lblNoMessages.setText(jsonObject.getString("Day"));
                                    String loadMoreCall = TeacherUtil_SharedPreference.getOnBackMethod(TeacherMessageDatesScreen.this);
                                    if (loadMoreCall.equals("1")) {
                                        TeacherUtil_SharedPreference.putOnBackPressed(TeacherMessageDatesScreen.this, "");
                                        LoadMoreGetDetails();
                                    }
                                } else {
                                    lblNoMessages.setVisibility(View.GONE);
                                    showRecords(jsonObject.getString("Day"));
                                }
                            }
                        }

                        LoadMore.setVisibility(View.VISIBLE);
                        dateListAdapter.notifyDataSetChanged();
                    } else {
                        showRecords(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Unreadcount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Unreadcount:Failure", t.toString());
            }
        });

    }

    private void showRecords(String day) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherMessageDatesScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(day);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (iRequestCode == PRINCIPAL_MESSAGESFROMMANAGEMENT) {
                    backToResultActvity("SENT");
                    dialog.cancel();
                    finish();
                } else {
                    dialog.cancel();
                    finish();
                }

            }
        });
        alertDialog.show();
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(TeacherMessageDatesScreen.this, msg, Toast.LENGTH_SHORT).show();
    }
}
