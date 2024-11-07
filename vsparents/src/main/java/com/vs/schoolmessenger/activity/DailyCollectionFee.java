package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.EditDataList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Activity.LessonPlanActivity;
import com.vs.schoolmessenger.LessonPlan.Adapter.LessonPlanAdapter;
import com.vs.schoolmessenger.LessonPlan.Model.EditDataItem;
import com.vs.schoolmessenger.LessonPlan.Model.LessonPlanModel;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.DailyCollectionModeType;
import com.vs.schoolmessenger.adapter.DailyFeeReportAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CertificateDataItem;
import com.vs.schoolmessenger.model.CertificateTypeModelItemItem;
import com.vs.schoolmessenger.model.DailyCollection;
import com.vs.schoolmessenger.model.DailyCollectionData;
import com.vs.schoolmessenger.model.DailyCollectionModeTypeData;
import com.vs.schoolmessenger.model.DailyFeeData;
import com.vs.schoolmessenger.model.DailyFeeReportData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DailyCollectionFee extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, CalendarDatePickerDialogFragment.OnDateSetListener {

    TextView lblDateFrom, lblDateTo, lblCategory, lblMode, lblClass, lblNoRecords;
    Spinner SpinnerSection;
    String isAcademicName;
    String isAcademicId;
    int isDateType = 1;
    DailyFeeReportAdapter dailyFeeReportAdapter;
    ArrayList<DailyFeeData> iaDailyFeeData = new ArrayList<>();
    RecyclerView rcy_DailyReport;
    RelativeLayout rlySection;
    LinearLayout rlyDatepick;
    String SchoolID, StaffID;
    int selDay, selMonth, selYear;

    public static List<DailyCollectionData> dailyCollectionData = new ArrayList<DailyCollectionData>();

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";


//    @Override
//    protected void attachBaseContext(Context newBase) {
//        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_collection_fee);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Daily Collection Fee");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(DailyCollectionFee.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(DailyCollectionFee.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }


        lblDateFrom = findViewById(R.id.lblDateFrom);
        lblDateTo = findViewById(R.id.lblDateTo);
        lblCategory = findViewById(R.id.lblCategory);
        lblClass = findViewById(R.id.lblClass);
        lblMode = findViewById(R.id.lblMode);
        SpinnerSection = findViewById(R.id.SpinnerSection);
        rcy_DailyReport = findViewById(R.id.rcy_DailyReport);
        rcy_DailyReport.setNestedScrollingEnabled(false);
        rlySection = findViewById(R.id.rlySection);
        lblNoRecords = findViewById(R.id.lblNoRecords);
        rlyDatepick = findViewById(R.id.rlyDatepick);

        lblDateFrom.setOnClickListener(this);
        lblDateTo.setOnClickListener(this);
        lblCategory.setOnClickListener(this);
        lblClass.setOnClickListener(this);
        lblMode.setOnClickListener(this);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        String formattedDate = df.format(c);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        lblDateFrom.setText(formattedDate);
        lblDateTo.setText(formattedDate);
        Util_Common.isType = 1;
        DailyCollection();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.lblDateFrom:
                isDateType = 1;
                datePicking();
                break;
            case R.id.lblDateTo:
                isDateType = 0;
                datePicking();
                break;
            case R.id.lblCategory:
                rlyDatepick.setVisibility(View.VISIBLE);
                rcy_DailyReport.setVisibility(View.GONE);
                isBackRoundChange(lblCategory);
                Util_Common.isType = 1;
                DailyCollection();
                break;
            case R.id.lblClass:
                rlyDatepick.setVisibility(View.VISIBLE);
                rcy_DailyReport.setVisibility(View.GONE);
                Util_Common.isType = 2;
                isBackRoundChange(lblClass);
                DailyCollection();
                break;
            case R.id.lblMode:
                Util_Common.isType = 3;
                rlyDatepick.setVisibility(View.VISIBLE);
                rcy_DailyReport.setVisibility(View.GONE);
                SpinnerSection.setSelection(0);
                isBackRoundChange(lblMode);
                iaDailyFeeData.clear();
                DailyCollection();
                break;

        }
    }

    private void isBackRoundChange(TextView isFeeDetailsId) {

        if (isFeeDetailsId.equals(lblCategory)) {
            isFeeDetailsId.setBackgroundColor(ContextCompat.getColor(this, R.color.is_dailycollection_header));
            lblMode.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblClass.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblMode.setTextColor(this.getResources().getColor(R.color.clr_black));
            lblClass.setTextColor(this.getResources().getColor(R.color.clr_black));
            lblCategory.setTextColor(this.getResources().getColor(R.color.clr_white));

        } else if (isFeeDetailsId.equals(lblClass)) {
            isFeeDetailsId.setBackgroundColor(ContextCompat.getColor(this, R.color.is_dailycollection_header));
            lblMode.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblCategory.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblMode.setTextColor(this.getResources().getColor(R.color.clr_black));
            lblClass.setTextColor(this.getResources().getColor(R.color.clr_white));
            lblCategory.setTextColor(this.getResources().getColor(R.color.clr_black));

        } else if (isFeeDetailsId.equals(lblMode)) {
            isFeeDetailsId.setBackgroundColor(ContextCompat.getColor(this, R.color.is_dailycollection_header));
            lblClass.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblCategory.setBackgroundColor(ContextCompat.getColor(this, R.color.clr_white));
            lblMode.setTextColor(this.getResources().getColor(R.color.clr_white));
            lblClass.setTextColor(this.getResources().getColor(R.color.clr_black));
            lblCategory.setTextColor(this.getResources().getColor(R.color.clr_black));

        }
    }

    private void datePicking() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay maxDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener((CalendarDatePickerDialogFragment.OnDateSetListener) DailyCollectionFee.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(null, maxDate)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.teacher_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        String isPickingDate = selDay + "-" + (selMonth + 1) + "-" + selYear;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

        try {
            Date date = inputDateFormat.parse(isPickingDate);
            String outputDateStr = outputDateFormat.format(date);
            if (isDateType == 1) {
                if (lblDateTo.getText().toString().equals("")) {
                    lblDateFrom.setText(outputDateStr);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                    try {
                        Date date1 = dateFormat.parse(lblDateTo.getText().toString());
                        Date date2 = dateFormat.parse(outputDateStr);

                        if (date1.compareTo(date2) < 0) {
                            lblDateFrom.setText(outputDateStr);
                            Toast.makeText(getApplicationContext(), "Please select toDate is after fromDate", Toast.LENGTH_SHORT).show();
                            lblDateTo.setText("");
                            clearAdapter();
                        } else {
                            lblDateFrom.setText(outputDateStr);
                            DailyCollection();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
                try {
                    Date date1 = dateFormat.parse(lblDateFrom.getText().toString());
                    Date date2 = dateFormat.parse(outputDateStr);

                    if (date1.compareTo(date2) > 0) {
                        Toast.makeText(getApplicationContext(), "Please select toDate is after fromDate", Toast.LENGTH_SHORT).show();
                        lblDateTo.setText("");
                        clearAdapter();
                    } else {
                        lblDateTo.setText(outputDateStr);
                        DailyCollection();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void clearAdapter() {
        iaDailyFeeData.clear();
        dailyFeeReportAdapter = new DailyFeeReportAdapter(DailyCollectionFee.this, iaDailyFeeData);
        RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(DailyCollectionFee.this, LinearLayoutManager.VERTICAL, false);
        rcy_DailyReport.setHasFixedSize(true);
        rcy_DailyReport.setLayoutManager(mLayoutManagerS);
        rcy_DailyReport.setItemAnimator(new DefaultItemAnimator());
        rcy_DailyReport.setAdapter(dailyFeeReportAdapter);
    }

    private void DailyCollection() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(DailyCollectionFee.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(DailyCollectionFee.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DailyCollectionFee.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing()) mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonObject> call;
        call = apiService.getDailyFeeReport(jsonReqArray);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("StudentsList:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());

                        int status = jsonObject.getInt("Status");
                        String message = jsonObject.getString("Message");

                        if (status == 1) {
                            JSONArray jSONArray = jsonObject.getJSONArray("data");
                            if (jSONArray.length() > 0) {
                                iaDailyFeeData.clear();
                                rcy_DailyReport.setVisibility(View.VISIBLE);
                                lblNoRecords.setVisibility(View.GONE);
                                for (int j = 0; j < jSONArray.length(); j++) {
                                    JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
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
                                    dailyFeeReportAdapter = new DailyFeeReportAdapter(DailyCollectionFee.this, iaDailyFeeData);
                                    RecyclerView.LayoutManager mLayoutManagerS = new LinearLayoutManager(DailyCollectionFee.this, LinearLayoutManager.VERTICAL, false);
                                    rcy_DailyReport.setHasFixedSize(true);
                                    rcy_DailyReport.setLayoutManager(mLayoutManagerS);
                                    rcy_DailyReport.setItemAnimator(new DefaultItemAnimator());
                                    rcy_DailyReport.setAdapter(dailyFeeReportAdapter);
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
                        Log.e("GroupList:Excep", String.valueOf(e));
                    }
                } else {
                    rcy_DailyReport.setVisibility(View.GONE);
                    lblNoRecords.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("instituteId", SchoolID);
            jsonObjectSchool.addProperty("Type", Util_Common.isType);

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMMM d, yyyy", Locale.US);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

            // isFromDate
            try {
                Date date = inputDateFormat.parse(lblDateFrom.getText().toString());
                String outputDateStr = outputDateFormat.format(date);
                jsonObjectSchool.addProperty("fromDate", outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // isToDate
            try {
                Date date = inputDateFormat.parse(lblDateTo.getText().toString());
                String outputDateStr = outputDateFormat.format(date);
                jsonObjectSchool.addProperty("toDate", outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.d("jsonObjectSchool", String.valueOf(jsonObjectSchool));
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
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

