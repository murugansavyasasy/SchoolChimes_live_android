package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.AttendanceStudentlist;
import com.vs.schoolmessenger.adapter.SectionListAttendance;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.AttendanceStudentClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.AttendanceListStudentData;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceSectionList extends AppCompatActivity {

    ArrayList<TeacherABS_Section> sections = new ArrayList<>();
    ArrayList<AttendanceListStudentData> absenteeList = new ArrayList<>();
    RecyclerView rvDateListSection, absentee_studentlist;
    SectionListAttendance sectionListAttendance;
    AttendanceStudentlist AttendanceStudentlist;
    String isSectionId;
    String SchoolID, StaffID;
    TextView lblNoRecords;
    String isSectionName = "";

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
        setContentView(R.layout.attendance_strength);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Absentees_Students));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        rvDateListSection = (RecyclerView) findViewById(R.id.absentee_rvDateList);
        absentee_studentlist = (RecyclerView) findViewById(R.id.absentee_studentlist);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);

        ArrayList<String> jsonStrings = getIntent().getStringArrayListExtra("SectionData");
        ArrayList<TeacherABS_Section> sections = new ArrayList<>();
        for (String jsonString : jsonStrings) {
            TeacherABS_Section section = new Gson().fromJson(jsonString, TeacherABS_Section.class);
            sections.add(section);
        }

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(AttendanceSectionList.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(AttendanceSectionList.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }

        isSectionId = sections.get(0).getSectionId();
        isSectionName = Util_Common.isAttendanceClass + "-" + sections.get(0).getSection();
        sectionListAttendance = new SectionListAttendance(AttendanceSectionList.this, sections, new AttendanceStudentClickListener() {

            @Override
            public void onItemClick(TeacherABS_Section item, SectionListAttendance.MyViewHolder holder, int position) {
                Util_Common.isPositionSection = position;
                isSectionId = item.getSectionId();
                isSectionName = Util_Common.isAttendanceClass + "-" + item.getSection();
                AbsenteesStudentListAPI();
                sectionListAttendance.notifyDataSetChanged();
            }
        });
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvDateListSection.setHasFixedSize(true);
        rvDateListSection.setLayoutManager(mLayoutManager);
        rvDateListSection.setItemAnimator(new DefaultItemAnimator());
        rvDateListSection.setAdapter(sectionListAttendance);

        AbsenteesStudentListAPI();

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(AttendanceSectionList.this, msg, Toast.LENGTH_SHORT).show();
    }

    private JsonObject isAttendanceStudentList() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {

            jsonObjectSchool.addProperty("schoolId", SchoolID);
            jsonObjectSchool.addProperty("sectionId", isSectionId);
            jsonObjectSchool.addProperty("absentOn", Util_Common.isDate);

            Log.d("jsonObjectSchool", String.valueOf(jsonObjectSchool));
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void AbsenteesStudentListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(AttendanceSectionList.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(AttendanceSectionList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(AttendanceSectionList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        mProgressDialog.show();
        JsonObject jsonReqArray = isAttendanceStudentList();
        Call<JsonObject> call = apiService.getAttenfdanceList(jsonReqArray);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201) {
                    absenteeList.clear();
                    lblNoRecords.setVisibility(View.GONE);
                    Log.d("StudentsList:Res", response.body().toString());
                    try {
                        JSONObject jsonData = new JSONObject(response.body().toString());

                        int status = jsonData.getInt("Status");
                        if (status != 1) {
                            absentee_studentlist.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                        } else {
                            absentee_studentlist.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                        }
                        String message = jsonData.getString("Message");
                        JSONArray dataArray = jsonData.getJSONArray("data");

                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject studentObject = dataArray.getJSONObject(i);

                            int studentId = studentObject.getInt("studentId");
                            String studentName = studentObject.getString("studentName");
                            String primaryMobile = studentObject.getString("primaryMobile");
                            String admissionNo = studentObject.getString("admissionNo");
                            String photoPath = studentObject.getString("photoPath");
                            String rollNo = studentObject.getString("rollNo");
                            AttendanceListStudentData absentee;
                            absentee = new AttendanceListStudentData(studentId, studentName, primaryMobile, photoPath, admissionNo, rollNo);
                            absenteeList.add(absentee);
                            AttendanceStudentlist = new AttendanceStudentlist(AttendanceSectionList.this, absenteeList, isSectionName);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(AttendanceSectionList.this, LinearLayoutManager.VERTICAL, false);
                            absentee_studentlist.setHasFixedSize(true);
                            absentee_studentlist.setLayoutManager(mLayoutManager);
                            absentee_studentlist.setItemAnimator(new DefaultItemAnimator());
                            absentee_studentlist.setAdapter(AttendanceStudentlist);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    absentee_studentlist.setVisibility(View.GONE);
                    lblNoRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Log.d("t_Value", String.valueOf(t));
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Util_Common.isPositionSection = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util_Common.isPositionSection = 0;
    }
}
