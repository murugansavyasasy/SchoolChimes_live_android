package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.DailyFeeReportAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DailyCollection;
import com.vs.schoolmessenger.model.DailyCollectionData;
import com.vs.schoolmessenger.model.DailyFeeData;
import com.vs.schoolmessenger.model.DailyFeeReportData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeePendingReport extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static List<DailyCollectionData> isDailyCollectionData = new ArrayList<DailyCollectionData>();
    public static List<DailyCollectionData> dailyCollectionData = new ArrayList<DailyCollectionData>();
    TextView lblCategory, lblClass, lblNoRecords;
    RecyclerView rcy_DailyReport;
    Spinner SpinnerSection;
    String isAcademicName;
    String isAcademicId;
    String isType = "CategoryWise";
    String SchoolID = "";
    String StaffID = "";
    DailyCollectionData dailyCollection;
    ArrayList<DailyFeeData> iaDailyFeeData = new ArrayList<>();
    DailyFeeReportAdapter dailyFeeReportAdapter;
    ArrayList<String> isAcademicYearList = new ArrayList<String>();

//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feepending_report);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Fee_Pending));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(FeePendingReport.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(FeePendingReport.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }

        lblCategory = findViewById(R.id.lblCategoryWise);
        lblClass = findViewById(R.id.lblClassWise);
        rcy_DailyReport = findViewById(R.id.rcy_DailyReport);
        SpinnerSection = findViewById(R.id.SpinnerSection);
        lblNoRecords = findViewById(R.id.lblNoRecords);
        lblCategory.setOnClickListener(this);
        lblClass.setOnClickListener(this);
        getAcademicYear();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.lblCategoryWise:
                SpinnerSection.setSelection(0);
                rcy_DailyReport.setVisibility(View.GONE);
                isBackRoundChange(lblCategory);
                isType = "CategoryWise";
                DailyCollection();
                break;
            case R.id.lblClassWise:
                SpinnerSection.setSelection(0);
                rcy_DailyReport.setVisibility(View.GONE);
                isBackRoundChange(lblClass);
                isType = "ClassWise";
                DailyCollection();
                break;
        }
    }

    private void isBackRoundChange(TextView isFeeDetailsId) {

        if (isFeeDetailsId.equals(lblCategory)) {
            isFeeDetailsId.setBackgroundColor(ContextCompat.getColor(this, R.color.is_dailycollection_header));
            lblClass.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblClass.setTextColor(this.getResources().getColor(R.color.clr_black));
            lblCategory.setTextColor(this.getResources().getColor(R.color.clr_white));

        } else if (isFeeDetailsId.equals(lblClass)) {
            isFeeDetailsId.setBackgroundColor(ContextCompat.getColor(this, R.color.is_dailycollection_header));
            lblCategory.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblClass.setTextColor(this.getResources().getColor(R.color.clr_white));
            lblCategory.setTextColor(this.getResources().getColor(R.color.clr_black));
        }
    }

    private void getAcademicYear() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(FeePendingReport.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<DailyCollection> call = apiService.getAcademicYear(SchoolID);

        call.enqueue(new Callback<DailyCollection>() {
            @Override
            public void onResponse(Call<DailyCollection> call, retrofit2.Response<DailyCollection> response) {
                try {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response);

                    Gson gson = new Gson();
                    String data = gson.toJson(response.body());
                    Log.d("academicYear_res", data);

                    if (response.code() == 200 || response.code() == 201) {
                        int status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if (status == 1) {
                            isAcademicYearList.clear();
                            dailyCollectionData.clear();
                            isDailyCollectionData.clear();

                            isDailyCollectionData = response.body().getData();
                            if (!isDailyCollectionData.isEmpty()) {
                                for (int j = 0; j < isDailyCollectionData.size(); j++) {
                                    int isId = isDailyCollectionData.get(j).getId();
                                    String isYearName = isDailyCollectionData.get(j).getYearName();
                                    int isAcademicYear = isDailyCollectionData.get(j).getCurrentAcademicYear();
                                    dailyCollection = new DailyCollectionData(isId, isYearName, isAcademicYear);
                                    dailyCollectionData.add(dailyCollection);
                                }

                                for (int i = 0; i < dailyCollectionData.size(); i++) {
                                    int value = dailyCollectionData.get(i).getCurrentAcademicYear();
                                    if (value != 1) {
                                        isAcademicYearList.add(dailyCollectionData.get(i).getYearName());
                                    } else {
                                        isAcademicYearList.add(0, dailyCollectionData.get(i).getYearName());
                                    }
                                }

                                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(FeePendingReport.this, R.layout.spinner_drop_down, isAcademicYearList);
                                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerSection.setAdapter(dataAdapter);
                                SpinnerSection.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) FeePendingReport.this);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DailyCollection> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void DailyCollection() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(FeePendingReport.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(FeePendingReport.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(FeePendingReport.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonObject> call;


        if (isType.equals("CategoryWise")) {
            call = apiService.getPendingAmount(jsonReqArray);
        } else {
            call = apiService.getSectionPendingAmount(jsonReqArray);
        }
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("StudentsList:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.length() > 0) {

                            int status = jsonObject.getInt("Status");
                            String message = jsonObject.getString("Message");
                            iaDailyFeeData.clear();
                            JSONArray jSONArray = jsonObject.getJSONArray("data");
                            if (jSONArray.length() > 0) {
                                rcy_DailyReport.setVisibility(View.VISIBLE);
                                lblNoRecords.setVisibility(View.GONE);
                                for (int j = 0; j < jSONArray.length(); j++) {
                                    JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
                                    if (isType.equals("CategoryWise")) {

                                        String isamountType = jsonObjectgroups.getString("Category");
                                        String isAmount = jsonObjectgroups.getString("total");
                                        JSONArray jSONArrayGroup = jsonObjectgroups.getJSONArray("data");

                                        ArrayList<DailyFeeReportData> isDailyFeeData = new ArrayList<>();
                                        for (int i = 0; i < jSONArrayGroup.length(); i++) {
                                            JSONObject isFeeReport = jSONArrayGroup.getJSONObject(i);
                                            String isFeeName = isFeeReport.getString("TypeName");
                                            String isFeeAmount = isFeeReport.getString("amount");
                                            DailyFeeReportData isFeeReportData = new DailyFeeReportData(isFeeName, isFeeAmount);
                                            isDailyFeeData.add(isFeeReportData);
                                        }
                                        DailyFeeData dailyFeeData;
                                        dailyFeeData = new DailyFeeData(isamountType, isAmount, isDailyFeeData);
                                        iaDailyFeeData.add(dailyFeeData);
                                        dailyFeeReportAdapter = new DailyFeeReportAdapter(FeePendingReport.this, iaDailyFeeData);
                                        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(FeePendingReport.this, LinearLayoutManager.VERTICAL, false);
                                        rcy_DailyReport.setHasFixedSize(true);
                                        rcy_DailyReport.setLayoutManager(mLayoutManagerS);
                                        rcy_DailyReport.setItemAnimator(new DefaultItemAnimator());
                                        rcy_DailyReport.setAdapter(dailyFeeReportAdapter);
                                    } else {

                                        String isamountType = jsonObjectgroups.getString("Category");
                                        String isAmount = jsonObjectgroups.getString("total");
                                        JSONArray jSONArrayGroup = jsonObjectgroups.getJSONArray("data");

                                        ArrayList<DailyFeeReportData> isDailyFeeData = new ArrayList<>();
                                        for (int i = 0; i < jSONArrayGroup.length(); i++) {
                                            JSONObject isFeeReport = jSONArrayGroup.getJSONObject(i);
                                            String isFeeName = isFeeReport.getString("TypeName");
                                            String isFeeAmount = isFeeReport.getString("amount");
                                            DailyFeeReportData isFeeReportData = new DailyFeeReportData(isFeeName, isFeeAmount);
                                            isDailyFeeData.add(isFeeReportData);
                                        }
                                        DailyFeeData dailyFeeData;
                                        dailyFeeData = new DailyFeeData(isamountType, isAmount, isDailyFeeData);
                                        iaDailyFeeData.add(dailyFeeData);
                                        dailyFeeReportAdapter = new DailyFeeReportAdapter(FeePendingReport.this, iaDailyFeeData);
                                        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(FeePendingReport.this, LinearLayoutManager.VERTICAL, false);
                                        rcy_DailyReport.setHasFixedSize(true);
                                        rcy_DailyReport.setLayoutManager(mLayoutManagerS);
                                        rcy_DailyReport.setItemAnimator(new DefaultItemAnimator());
                                        rcy_DailyReport.setAdapter(dailyFeeReportAdapter);
                                    }
                                }
                            } else {
                                rcy_DailyReport.setVisibility(View.GONE);
                                lblNoRecords.setVisibility(View.VISIBLE);
                            }
                        } else {
                            rcy_DailyReport.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                        }

                    } catch (Exception e) {
                        Log.e("GroupList:Excep", e.getMessage());
                    }
                } else {
                    rcy_DailyReport.setVisibility(View.GONE);
                    lblNoRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("instituteId", SchoolID);
            jsonObjectSchool.addProperty("acadamicYearId", isAcademicId);
            Log.d("jsonObjectSchool", String.valueOf(jsonObjectSchool));
        } catch (Exception e) {
            Log.d("ASF", e.toString());
        }
        return jsonObjectSchool;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {

        isAcademicName = (String) SpinnerSection.getSelectedItem();
        for (int i = 0; i < dailyCollectionData.size(); i++) {
            if (isAcademicName.equals(dailyCollectionData.get(i).getYearName())) {
                isAcademicId = String.valueOf(dailyCollectionData.get(i).getId());
                Log.d("isAcademicId", isAcademicId);
            }
        }
        DailyCollection();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
