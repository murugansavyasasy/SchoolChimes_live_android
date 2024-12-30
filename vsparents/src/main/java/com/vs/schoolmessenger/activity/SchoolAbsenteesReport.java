package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SchoolAttendanceReport;
import com.vs.schoolmessenger.adapter.StandardAdapter;
import com.vs.schoolmessenger.interfaces.SchoolAbsenteesDateListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.standardListener;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherABS_Standard;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SchoolAbsenteesReport extends AppCompatActivity {
    int iRequestCode;
    String SchoolID, StaffID;
    String date, absentcount, day;
    RecyclerView rvDateList, absentee_rvDateListStandard;
    SchoolAttendanceReport SchoolAttendanceReport;
    StandardAdapter standardAdapter;
    ArrayList<TeacherABS_Standard> StandardItem = new ArrayList<>();
    String isDate;
    ArrayList<TeacherABS_Section> SectionItem = new ArrayList<>();
    private final List<TeacherAbsenteesDates> dateList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schoolabsenteesreport);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.absentess_report);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);

        rvDateList = (RecyclerView) findViewById(R.id.absentee_rvDateList);
        absentee_rvDateListStandard = (RecyclerView) findViewById(R.id.absentee_rvDateListStandard);

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(SchoolAbsenteesReport.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(SchoolAbsenteesReport.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }


        AbsenteesReportListAPI();

        SchoolAttendanceReport = new SchoolAttendanceReport(SchoolAbsenteesReport.this, dateList, new SchoolAbsenteesDateListener() {
            @Override
            public void onItemClick(TeacherAbsenteesDates item, SchoolAttendanceReport.MyViewHolder holder, int position) {
                isLoadingStandard(item.getDate(), item.getStandards());
                Util_Common.isDate = item.getAbsentdateonly();
                Util_Common.isPosition = position;
                SchoolAttendanceReport.notifyDataSetChanged();
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvDateList.setHasFixedSize(true);
        rvDateList.setLayoutManager(mLayoutManager);
        rvDateList.setItemAnimator(new DefaultItemAnimator());
        rvDateList.setAdapter(SchoolAttendanceReport);
    }

    private void isLoadingStandard(String date, ArrayList<TeacherABS_Standard> standards) {
        standardAdapter = new StandardAdapter(SchoolAbsenteesReport.this, date, standards, new standardListener() {
            @Override
            public void onItemClick(TeacherABS_Standard item) {
                ArrayList<TeacherABS_Section> sections = item.getSections();
                Util_Common.isAttendanceClass = item.getStandard();
                ArrayList<String> jsonStrings = new ArrayList<>();
                for (TeacherABS_Section section : sections) {
                    String jsonString = new Gson().toJson(section);
                    jsonStrings.add(jsonString);
                }
                Util_Common.isPositionSection = 0;
                Intent inNext = new Intent(SchoolAbsenteesReport.this, AttendanceSectionList.class);
                inNext.putStringArrayListExtra("SectionData", jsonStrings);
                inNext.putExtra("REQUEST_CODE", iRequestCode);
                inNext.putExtra("SCHOOL_ID", SchoolID);
                inNext.putExtra("STAFF_ID", StaffID);
                startActivity(inNext);
            }
        });

        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(SchoolAbsenteesReport.this, LinearLayoutManager.VERTICAL, false);
        absentee_rvDateListStandard.setHasFixedSize(true);
        absentee_rvDateListStandard.setLayoutManager(mLayoutManagerS);
        absentee_rvDateListStandard.setItemAnimator(new DefaultItemAnimator());
        absentee_rvDateListStandard.setAdapter(standardAdapter);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(SchoolAbsenteesReport.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void AbsenteesReportListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(SchoolAbsenteesReport.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(SchoolAbsenteesReport.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SchoolAbsenteesReport.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", String.valueOf(js.length()));
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            date = jsonObject.getString("Date");
                            absentcount = jsonObject.getString("TotalAbsentees");
                            day = jsonObject.getString("Day");
                            String absentdateonly = jsonObject.getString("absentdateonly");
                            TeacherAbsenteesDates absentee;

                            absentee = new TeacherAbsenteesDates(date, day, absentcount, absentdateonly);
                            ArrayList<TeacherABS_Standard> listStds = new ArrayList<>();

                            try {
                                JSONArray jSONArray = jsonObject.getJSONArray("ClassWise");
                                for (int j = 0; j < jSONArray.length(); j++) {
                                    JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
                                    TeacherABS_Standard abs_standard;

                                    abs_standard = new TeacherABS_Standard(jsonObjectgroups.getString("ClassName"), jsonObjectgroups.getString("TotalAbsentees"));
                                    abs_standard.setSid(jsonObjectgroups.getString("ClassId"));

                                    ArrayList<TeacherABS_Section> listSections = new ArrayList<>();
                                    JSONArray jSONArraysection = jsonObjectgroups.getJSONArray("SectionWise");
                                    {
                                        for (int k = 0; k < jSONArraysection.length(); k++) {
                                            JSONObject jsonObjectsections = jSONArraysection.getJSONObject(k);
                                            TeacherABS_Section abs_section;
                                            abs_section = new TeacherABS_Section(jsonObjectsections.getString("SectionName"), jsonObjectsections.getString("TotalAbsentees"), jsonObjectsections.getString("SectionId"));
                                            listSections.add(abs_section);
                                        }
                                        abs_standard.setSections(listSections);
                                        listStds.add(abs_standard);
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("GroupList:Excep", e.getMessage());
                            }
                            absentee.setStandards(listStds);
                            dateList.add(absentee);
                            if (!dateList.isEmpty()) {
                                StandardItem = dateList.get(0).getStandards();
                            }

                            if (!dateList.isEmpty()) {
                                isDate = dateList.get(0).getDate();
                                Log.d("isDate_", String.valueOf(isDate));
                            }
                            isLoadingStandard(isDate, StandardItem);
                        }
                        SchoolAttendanceReport.notifyDataSetChanged();

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
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

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
//            jsonObjectSchool.addProperty("SchoolId", "5512");
            jsonObjectSchool.addProperty("StaffID", StaffID);
            Log.d("jsonObjectSchool", String.valueOf(jsonObjectSchool));
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }
}
