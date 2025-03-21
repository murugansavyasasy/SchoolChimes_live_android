package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
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

import com.google.gson.Gson;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.AttendanceReportsAdapter;
import com.vs.schoolmessenger.adapter.PunchHistoryAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.ViewPunchHistoryListener;
import com.vs.schoolmessenger.model.PunchHistoryRes;
import com.vs.schoolmessenger.model.StaffAttendanceBiometricReportRes;
import com.vs.schoolmessenger.model.StaffListRes;
import com.vs.schoolmessenger.model.monthsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class StaffWiseAttendanceReports extends AppCompatActivity implements View.OnClickListener {
    public List<PunchHistoryRes.PunchHistoryData> punchTimingList = new ArrayList<PunchHistoryRes.PunchHistoryData>();
    public PunchHistoryAdapter punchHistoryAdapter;
    public List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> attendanceReportsList = new ArrayList<StaffAttendanceBiometricReportRes.BiometriStaffReportData>();
    public List<StaffListRes.StaffListData> StaffList = new ArrayList<StaffListRes.StaffListData>();
    public AttendanceReportsAdapter mAdapter;
    String SchoolID = "", StaffID = "";
    TextView btnTodaysReport, btnMonthWiseReports, lblNoRecords;
    RecyclerView recycleReports;
    Spinner spinnerStaffs, spinnerYears, spinnerMonths;
    LinearLayout lnrHeaderSpinners, lnrDatesSpinners;
    RelativeLayout rytStaffSpinner, rytParent;
    String monthID = "";
    int currentYear = 0;
    int selectedStaffID = 0;
    String[] Years = new String[20];
    String cardType = "DayWise";
    EditText txtSearch;
    private PopupWindow viewPunchHistoryPopup;

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
        setContentView(R.layout.staff_wise_attendance_reports);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.Attendance_Reports);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");

        btnTodaysReport = (TextView) findViewById(R.id.btnTodaysReport);
        btnMonthWiseReports = (TextView) findViewById(R.id.btnMonthWiseReports);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);
        recycleReports = (RecyclerView) findViewById(R.id.recycleReports);
        spinnerStaffs = (Spinner) findViewById(R.id.spinnerStaffs);
        spinnerYears = (Spinner) findViewById(R.id.spinnerYears);
        spinnerMonths = (Spinner) findViewById(R.id.spinnerMonths);
        txtSearch = (EditText) findViewById(R.id.txtSearch);

        rytStaffSpinner = (RelativeLayout) findViewById(R.id.rytStaffSpinner);
        lnrHeaderSpinners = (LinearLayout) findViewById(R.id.lnrHeaderSpinners);
        lnrDatesSpinners = (LinearLayout) findViewById(R.id.lnrDatesSpinners);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);

        btnTodaysReport.setOnClickListener(this);
        btnMonthWiseReports.setOnClickListener(this);

        rytStaffSpinner.setVisibility(View.GONE);
        lnrDatesSpinners.setVisibility(View.GONE);
        getStaffWiseAttendanceReports();

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    recycleReports.setVisibility(View.GONE);
                    lblNoRecords.setText(R.string.no_records);
                    lblNoRecords.setVisibility(View.VISIBLE);
                    if (txtSearch.getText().toString().isEmpty()) {
                        recycleReports.setVisibility(View.VISIBLE);
                    }
                } else {
                    recycleReports.setVisibility(View.VISIBLE);
                    lblNoRecords.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!editable.toString().isEmpty()) {
                    filterlist(editable.toString());
                }
            }
        });

    }

    private void filterlist(String s) {
        List<StaffAttendanceBiometricReportRes.BiometriStaffReportData> temp = new ArrayList();
        for (StaffAttendanceBiometricReportRes.BiometriStaffReportData d : attendanceReportsList) {

            if (d.getStaffName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    private void getStaffsList() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffWiseAttendanceReports.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<StaffListRes> call = apiService.getStaffList(SchoolID);
        call.enqueue(new Callback<StaffListRes>() {
            @Override
            public void onResponse(Call<StaffListRes> call, retrofit2.Response<StaffListRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("attendanceReport", data);
                        Log.d("attendanceReport", response.body().toString());
                        StaffList.clear();
                        if (response.body().getStatus() == 1) {
                            StaffList = response.body().getData();
                            loadStaffListSpinner();
                        } else {
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<StaffListRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadStaffListSpinner() {
        String[] staffs = new String[StaffList.size()];

        for (int i = 0; i < StaffList.size(); i++) {
            String staffname = StaffList.get(i).getStaffName();
            staffs[i] = staffname;
        }

        lnrDatesSpinners.setVisibility(View.VISIBLE);
        ArrayAdapter ad1 = new ArrayAdapter(this, R.layout.spinner_textview, staffs);
        ad1.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerStaffs.setAdapter(ad1);
        spinnerStaffs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedStaffID = StaffList.get(position).getStaffId();
                Log.d("selectedStaffID", String.valueOf(selectedStaffID));
                getStaffWiseAttendanceReports();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getStaffWiseAttendanceReports() {

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        // Format the current date
        String formattedDate = dateFormat.format(currentDate);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffWiseAttendanceReports.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<StaffAttendanceBiometricReportRes> call;
        if (cardType.equals("DayWise")) {
            call = apiService.getDayWiseBiometricAttendanceReport(formattedDate, SchoolID);
        } else {
            call = apiService.getStaffWiseBiometricAttendanceReport(String.valueOf(selectedStaffID), monthID, SchoolID);
        }

        call.enqueue(new Callback<StaffAttendanceBiometricReportRes>() {
            @Override
            public void onResponse(Call<StaffAttendanceBiometricReportRes> call, retrofit2.Response<StaffAttendanceBiometricReportRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("attendanceReport", data);
                        Log.d("attendanceReport", response.body().toString());
                        attendanceReportsList.clear();
                        if (response.body().getStatus() == 1) {
                            txtSearch.setEnabled(true);
                            recycleReports.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            attendanceReportsList = response.body().getData();
                            mAdapter = new AttendanceReportsAdapter(attendanceReportsList, StaffWiseAttendanceReports.this, "StaffWise", new ViewPunchHistoryListener() {
                                @Override
                                public void onItemClick(StaffAttendanceBiometricReportRes.BiometriStaffReportData item) {
                                    viewPunchHistoryPopup(item);
                                }
                            });
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StaffWiseAttendanceReports.this);
                            recycleReports.setLayoutManager(mLayoutManager);
                            recycleReports.setItemAnimator(new DefaultItemAnimator());
                            recycleReports.setAdapter(mAdapter);
                            recycleReports.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            txtSearch.setEnabled(false);
                            recycleReports.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());
                            lblNoRecords.setTypeface(null, Typeface.BOLD);

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<StaffAttendanceBiometricReportRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void viewPunchHistoryPopup(StaffAttendanceBiometricReportRes.BiometriStaffReportData item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.punch_history_popup, null);
        viewPunchHistoryPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        viewPunchHistoryPopup.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                viewPunchHistoryPopup.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        TextView lblTitle = (TextView) layout.findViewById(R.id.lblTitle);
        TextView lblNoRecords = (TextView) layout.findViewById(R.id.lblNoRecords);
        RecyclerView recycleHistory = (RecyclerView) layout.findViewById(R.id.recycleHistory);
        lblTitle.setTypeface(null, Typeface.BOLD);
        lblNoRecords.setTypeface(null, Typeface.BOLD);

        viewPunchHistory(item, recycleHistory, lblNoRecords);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPunchHistoryPopup.dismiss();
            }
        });
    }

    private void viewPunchHistory(StaffAttendanceBiometricReportRes.BiometriStaffReportData item, RecyclerView recycleHistory, TextView lblNoRecords) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffWiseAttendanceReports.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<PunchHistoryRes> call = apiService.viewPunchHistory(SchoolID, item.getStaffId(), item.getDate(), item.getDate());
        call.enqueue(new Callback<PunchHistoryRes>() {
            @Override
            public void onResponse(Call<PunchHistoryRes> call, retrofit2.Response<PunchHistoryRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("punchHistoryReport", data);
                        Log.d("punchHistoryReport", response.body().toString());
                        punchTimingList.clear();
                        if (response.body().getStatus() == 1) {
                            recycleHistory.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            punchTimingList = response.body().getData();
                            punchHistoryAdapter = new PunchHistoryAdapter(punchTimingList.get(0).getTimings(), StaffWiseAttendanceReports.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StaffWiseAttendanceReports.this);
                            recycleHistory.setLayoutManager(mLayoutManager);
                            recycleHistory.setItemAnimator(new DefaultItemAnimator());
                            recycleHistory.setAdapter(punchHistoryAdapter);
                            recycleHistory.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            punchHistoryAdapter.notifyDataSetChanged();
                        } else {
                            recycleHistory.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());
                        }
                    }
                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<PunchHistoryRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadYearsSpinner() {

        Calendar calendar = Calendar.getInstance();
        currentYear = calendar.get(Calendar.YEAR);
        Years[0] = String.valueOf(currentYear);
        for (int i = 1; i < 20; i++) {
            currentYear = currentYear - 1;
            Years[i] = String.valueOf(currentYear);
        }

        ArrayAdapter ad = new ArrayAdapter(this, R.layout.spinner_textview, Years);
        ad.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerYears.setAdapter(ad);
        spinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SelectedYear", Years[position]);
                loadMonthsSpinner(Years[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void loadMonthsSpinner(String selectedYear) {
        Calendar calendar = Calendar.getInstance();
        int currentMonthIndex = calendar.get(Calendar.MONTH); // 0 = January, 11 = December
        // Get all month names
        String[] monthNames = new DateFormatSymbols().getMonths();
        List<String> monthList = new ArrayList<>();
        monthList.add(monthNames[currentMonthIndex]);

        monthsModel data;
        List<monthsModel> monthDataList = new ArrayList<>();
        data = new monthsModel(currentMonthIndex, monthNames[currentMonthIndex]);
        monthDataList.add(data);
        Log.d("currentMonthIndex", String.valueOf(currentMonthIndex));
        // Add the rest of the months
        for (int i = 0; i < monthNames.length; i++) {
            if (i != currentMonthIndex) {
                monthList.add(monthNames[i]);
                data = new monthsModel(i, monthNames[i]);
                monthDataList.add(data);
            }
        }

        ArrayAdapter ad1 = new ArrayAdapter(this, R.layout.spinner_textview, monthList);
        ad1.setDropDownViewResource(R.layout.dropdown_spinner);
        spinnerMonths.setAdapter(ad1);
        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("monthPosition", String.valueOf(position));
                int ID = monthDataList.get(position).getID();
                if (ID > 8) {
                    monthID = selectedYear + "-" + (ID + 1);
                } else {
                    monthID = selectedYear + "-" + "0" + (ID + 1);
                }

                if (StaffList.size() == 0) {
                    getStaffsList();
                } else {
                    getStaffWiseAttendanceReports();
                }

                Log.d("SelectedMonthID", monthID);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTodaysReport:
                cardType = "DayWise";
                rytStaffSpinner.setVisibility(View.GONE);
                lnrDatesSpinners.setVisibility(View.GONE);
                txtSearch.setVisibility(View.VISIBLE);
                txtSearch.setText("");

                btnTodaysReport.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rect_yellow));
                btnMonthWiseReports.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_stroke_window_grey));
                btnTodaysReport.setTextColor(ContextCompat.getColor(this, R.color.clr_white));
                btnMonthWiseReports.setTextColor(ContextCompat.getColor(this, R.color.clr_black));

                getStaffWiseAttendanceReports();

                break;

            case R.id.btnMonthWiseReports:
                cardType = "MonthWise";

                rytStaffSpinner.setVisibility(View.VISIBLE);
                lnrDatesSpinners.setVisibility(View.VISIBLE);
                txtSearch.setVisibility(View.GONE);

                loadYearsSpinner();

                btnMonthWiseReports.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_rect_yellow));
                btnTodaysReport.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_stroke_window_grey));
                btnMonthWiseReports.setTextColor(ContextCompat.getColor(this, R.color.clr_white));
                btnTodaysReport.setTextColor(ContextCompat.getColor(this, R.color.clr_black));

                break;


            default:
                break;
        }

    }
}