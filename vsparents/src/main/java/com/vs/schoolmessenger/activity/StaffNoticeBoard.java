package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TextCircularListAdapternew;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffNoticeBoard extends AppCompatActivity {

    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    TextCircularListAdapternew tvadapter;
    RecyclerView text_rvCircularList;
    String SchoolID, StaffID;
    TextView norecords;
    EditText Searchable;
    ImageView imgSearch;
    RelativeLayout rytSearch;
    TextView LoadMore;
    ArrayList<MessageModel> arrayList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_noticeboard);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        text_rvCircularList = findViewById(R.id.text_rvCircularList);
        rytSearch = findViewById(R.id.rytSearch);
        norecords = findViewById(R.id.lblNoMessagesLable);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Notice Board");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMore.setVisibility(View.GONE);
                LoadMorecircularsNoticeboardAPI();

            }
        });

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(StaffNoticeBoard.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(StaffNoticeBoard.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }
        circularsNoticeboardAPI();


        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tvadapter == null)
                    return;

                if (tvadapter.getItemCount() < 1) {
                    text_rvCircularList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        text_rvCircularList.setVisibility(View.VISIBLE);
                    }
                } else {
                    text_rvCircularList.setVisibility(View.VISIBLE);
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
    }

    private void filterlist(String s) {
        ArrayList<MessageModel> temp = new ArrayList();
        for (MessageModel d : msgModelList) {

            if (d.getMsgContent().toLowerCase().contains(s.toLowerCase()) || d.getMsgDate().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }
        }
        tvadapter.updateList(temp);
    }


    private void circularsNoticeboardAPI() {
        LoadMore.setVisibility(View.VISIBLE);
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(StaffNoticeBoard.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StaffNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getNoticeBoardStaff(StaffID, SchoolID);
        Call<JsonArray> call = apiService.GetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if (strStatus.equals("1")) {
                            norecords.setVisibility(View.GONE);
                            text_rvCircularList.setVisibility(View.VISIBLE);
                            rytSearch.setVisibility(View.VISIBLE);
                            MessageModel msgModel;
                            msgModelList.clear();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"), "", false);
                                msgModelList.add(msgModel);
                            }
                            tvadapter = new TextCircularListAdapternew(msgModelList, StaffNoticeBoard.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            text_rvCircularList.setLayoutManager(layoutManager);
                            text_rvCircularList.setItemAnimator(new DefaultItemAnimator());
                            text_rvCircularList.setAdapter(tvadapter);

                        } else {
                            norecords.setVisibility(View.VISIBLE);
                            norecords.setText(strMessage);
                            text_rvCircularList.setVisibility(View.GONE);
                            rytSearch.setVisibility(View.GONE);
                        }
                    } else {
                        norecords.setVisibility(View.VISIBLE);
                        text_rvCircularList.setVisibility(View.GONE);
                        rytSearch.setVisibility(View.GONE);
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }


                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                norecords.setVisibility(View.VISIBLE);
                text_rvCircularList.setVisibility(View.GONE);
                rytSearch.setVisibility(View.GONE);
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }


    private void LoadMorecircularsNoticeboardAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StaffNoticeBoard.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StaffNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetnoticeboardStaffSze(StaffID, SchoolID);
        Call<JsonArray> call = apiService.LoadMoreGetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());
                norecords.setVisibility(View.GONE);
                LoadMore.setVisibility(View.GONE);
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            text_rvCircularList.setVisibility(View.VISIBLE);
                            MessageModel msgModel;
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"), "", jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                            }
                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            tvadapter = new TextCircularListAdapternew(arrayList, StaffNoticeBoard.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            text_rvCircularList.setLayoutManager(layoutManager);
                            text_rvCircularList.setItemAnimator(new DefaultItemAnimator());
                            text_rvCircularList.setAdapter(tvadapter);

                        } else {
                            showAlertRecords(strMessage);
                        }
                    } else {
                        showAlertRecords(getResources().getString(R.string.no_records));
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
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(StaffNoticeBoard.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlertRecords(String no_events_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StaffNoticeBoard.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_events_found);

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
}
