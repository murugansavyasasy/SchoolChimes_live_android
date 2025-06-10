package com.vs.schoolmessenger.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.StaffListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.StaffListListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StaffList;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendToStaff extends AppCompatActivity implements StaffListListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    public StaffListAdapter mAdapter;
    RecyclerView staff_list_recycle;
    Button btnCalls;
    ImageView imgSearch;
    int i_sections_count;
    CheckBox select_All;
    EditText Searchable;
    RelativeLayout rytNoRecords;
    ArrayList<StaffList> staff_list = new ArrayList<StaffList>();
    ArrayList<StaffList> SelectedSubjects = new ArrayList<StaffList>();
    private int i_students_count;
    String SchoolID, StaffID, strtittle, strmessage, Description, duration, filepath;


    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_send_staff);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        staff_list_recycle = (RecyclerView) findViewById(R.id.staff_list_recycle);
        select_All = (CheckBox) findViewById(R.id.select_All);

        btnCalls = (Button) findViewById(R.id.btnCalls);
        rytNoRecords = (RelativeLayout) findViewById(R.id.rytNoRecords);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.staff_list);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        strtittle = getIntent().getExtras().getString("TITTLE", "");
        strmessage = getIntent().getExtras().getString("MESSAGE", "");
        Description = getIntent().getExtras().getString("TITTLE", "");
        duration = getIntent().getExtras().getString("DURATION", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");

        getStaffList();
        btnCalls.setEnabled(false);

        i_sections_count = 0;
        mAdapter = new StaffListAdapter(SendToStaff.this, new StaffListListener() {
            @Override
            public void student_addClass(StaffList subjects) {
                if ((subjects != null) && (!SelectedSubjects.contains(subjects))) {
                    SelectedSubjects.add(subjects);
                    i_sections_count++;
                    enableDisableNext();
                }
            }

            @Override
            public void student_removeClass(StaffList subjects) {
                if ((subjects != null) && (SelectedSubjects.contains(subjects))) {
                    SelectedSubjects.remove(subjects);
                    i_sections_count--;
                    enableDisableNext();
                }
            }
        }, staff_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SendToStaff.this);
        staff_list_recycle.setLayoutManager(mLayoutManager);
        staff_list_recycle.setItemAnimator(new DefaultItemAnimator());
        staff_list_recycle.setAdapter(mAdapter);
        staff_list_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);


        if (Searchable != null) {
            Searchable.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    if (mAdapter == null)
                        return;

                    if (mAdapter.getItemCount() < 1) {
                        btnCalls.setVisibility(View.GONE);
                        rytNoRecords.setVisibility(View.VISIBLE);
                        staff_list_recycle.setVisibility(View.GONE);

                        if (Searchable.getText().toString().isEmpty()) {
                            staff_list_recycle.setVisibility(View.VISIBLE);
                            btnCalls.setVisibility(View.VISIBLE);
                            rytNoRecords.setVisibility(View.GONE);
                        }

                    } else {
                        btnCalls.setVisibility(View.VISIBLE);
                        rytNoRecords.setVisibility(View.GONE);
                        staff_list_recycle.setVisibility(View.VISIBLE);
                    }


                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() > 0) {
                        imgSearch.setVisibility(View.GONE);
                    } else {
                        imgSearch.setVisibility(View.VISIBLE);
                    }

                    filter(s.toString());
                }
            });

        }


        select_All.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < staff_list.size(); i++) {
                    staff_list.get(i).setSelecteStatus(isChecked);

                    btnCalls.setEnabled(true);
                }
                mAdapter.notifyDataSetChanged();
                if (isChecked) {
                    btnCalls.setEnabled(true);
                    i_students_count = staff_list.size();
                    Log.d("CHECK-i_students_count", "True - " + i_students_count);
                } else {
                    i_students_count = 0;
                    btnCalls.setEnabled(false);
                    Log.d("CHECK-i_students_count", "False - " + i_students_count);
                }

            }
        });
        btnCalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlert("Are you want to send?", "");
            }
        });

    }

    @SuppressLint("ResourceAsColor")
    private void enableDisableNext() {
        btnCalls.setEnabled(i_sections_count > 0);
    }

    private void filter(String s) {
        List<StaffList> temp = new ArrayList();
        for (StaffList d : staff_list) {
            if (d.getStaffName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }
        }
        mAdapter.updateList(temp);
    }


    private void sendVoiceToStaff() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToStaff.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        Log.d("FILE_Path", filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArraySentToStaff();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(SendToStaff.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();


        Call<JsonArray> call;
//        if (Util_Common.isScheduleCall) {
//            call = apiService.ScheduleToGroupsAndStandards(requestBody, bodyFile);
//        } else {
        call = apiService.SendVoiceToStaff(requestBody, bodyFile);
//        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg, strStatus);


                            } else {
                                showAlert(strMsg, strStatus);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg, String status) {
//        lnrProgress.setVisibility(View.GONE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendToStaff.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {

                    dialog.cancel();
                    Intent homescreen = new Intent(SendToStaff.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
                    sendVoiceToStaff();
                    dialog.cancel();
                }


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void getStaffList() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(SendToStaff.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(SendToStaff.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToStaff.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("DialerId", StaffID);
        jsonObject.addProperty("SchoolId", SchoolID);

        Log.d("Request", jsonObject.toString());

        Call<JsonArray> call = apiService.GetAllStaffs(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {

                            mAdapter.clearAllData();
                            StaffList data;

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String staffId = jsonObject.getString("StaffId");

                                if (staffId.equals("0")) {
                                    String staff_name1 = jsonObject.getString("StaffName");
                                    Toast.makeText(getApplicationContext(), staff_name1, Toast.LENGTH_SHORT).show();
                                } else {
                                    String staff_name = jsonObject.getString("StaffName");
                                    String staff_type = jsonObject.getString("StaffType");
                                    String staff_mobile = jsonObject.getString("StaffMobile");
                                    String designation = jsonObject.getString("Designation");
                                    data = new StaffList(staffId, staff_name, staff_type, staff_mobile, designation, false);
                                    staff_list.add(data);
                                }
                            }

                            select_All.setChecked(false);

                            mAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void student_addClass(StaffList student) {
        if (student != null) {
            i_students_count++;
        }
    }

    @Override
    public void student_removeClass(StaffList student) {
        if (student != null) {
            i_students_count--;
        }
    }

    private JsonObject constructJsonArraySentToStaff() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", strtittle);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            JsonArray jsonArrayschoolstd = new JsonArray();
            List<StaffList> selectedStaff = mAdapter.getSelectedStaff();
            for (StaffList staff : selectedStaff) {
                Log.d("SelectedStaffID", staff.getStaffId());
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("ID", staff.getStaffId());
                jsonArrayschoolstd.add(jsonObject);
            }
            jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
}
