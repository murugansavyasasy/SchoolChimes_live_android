package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeachersAbsenteesDateReportAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherABS_Standard;
import com.vs.schoolmessenger.model.TeacherAbsenteesDates;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherAbsenteesReport extends AppCompatActivity {

    private static final String TAG = TeacherAbsenteesDates.class.getSimpleName();
    RecyclerView rvDateList;
    TeachersAbsenteesDateReportAdapter absenteesDateReportAdapter;
    String SchoolID, StaffID;
    String date, absentcount, day;
    int iRequestCode;
    ImageView imgSearch;
    TextView Searchable;
    private final List<TeacherAbsenteesDates> dateList = new ArrayList<>();

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
        setContentView(R.layout.teacher_activity_absentees_report);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.absentess_report);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAbsenteesReport.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAbsenteesReport.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }
        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (absenteesDateReportAdapter == null)
                    return;

                if (absenteesDateReportAdapter.getItemCount() < 1) {
                    rvDateList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvDateList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvDateList.setVisibility(View.VISIBLE);
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

        rvDateList = (RecyclerView) findViewById(R.id.absentee_rvDateList);
//        absenteesDateReportAdapter = new TeachersAbsenteesDateReportAdapter(TeacherAbsenteesReport.this, dateList, new TeacherAbsenteesDateListener() {
//            @Override
//            public void onItemClick(TeacherAbsenteesDates item) {
//                Intent in = new Intent(TeacherAbsenteesReport.this, TeacherAbsenteesByStandardWise.class);
//                in.putExtra("LIST_STDS", item.getStandards());
//                startActivity(in);
//
//                showToast(item.getDate());
//            }
//        });
//        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
//        rvDateList.setHasFixedSize(true);
//        rvDateList.setLayoutManager(mLayoutManager);
//        rvDateList.setItemAnimator(new DefaultItemAnimator());
//        rvDateList.setAdapter(absenteesDateReportAdapter);
//        AbsenteesReportListAPI();

    }

    private void filterlist(String s) {
        List<TeacherAbsenteesDates> temp = new ArrayList();
        for (TeacherAbsenteesDates d : dateList) {

            if (d.getDate().toLowerCase().contains(s.toLowerCase()) || d.getDay().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        absenteesDateReportAdapter.updateList(temp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
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
        Toast.makeText(TeacherAbsenteesReport.this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void AbsenteesReportListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherAbsenteesReport.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherAbsenteesReport.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherAbsenteesReport.this);
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
                        }
                        absenteesDateReportAdapter.notifyDataSetChanged();

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
            jsonObjectSchool.addProperty("StaffID", StaffID);
            Log.d("jsonObjectSchool", String.valueOf(jsonObjectSchool));
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }
}
