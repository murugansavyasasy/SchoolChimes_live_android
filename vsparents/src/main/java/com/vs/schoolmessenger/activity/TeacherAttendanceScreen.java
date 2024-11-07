package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Constants.attendanceType;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.AttendanceStudentReport;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StudentAttendanceReport;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherAttendanceScreen extends AppCompatActivity {

    Spinner spinStandard, spinSection, attendance_spinSession, attendance_spinType;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;

    ArrayAdapter<String> adaAttendanceTypes;
    ArrayAdapter<String> adaSessionTypes;

    Button btnMarkAllPresent, btnSelectStudents;

    TextView attendance_tv5, lblDatePicking;

    List<String> listStd = new ArrayList<>();
    List<String> listStdcode = new ArrayList<>();

    List<String> listAttendanceTypes = new ArrayList<>();
    List<String> listSessionTypes = new ArrayList<>();

    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListNEW> arrSectionCollections;
    String SchoolID, StaffID;
    List<String> listTotalStudentsInSec;
    private int iRequestCode = 0;
    private ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();
    String strStdName, strstdcode, strSecName, strSecCode, strTotalStudents;
    String isAttendanceDate = "";
    RelativeLayout rlaAttendanceReport;
    LinearLayout lnrAttendanceMarking;
    TextView lblAttendanceMark, lblAttendanceReport, lblDatePickingReport;
    Spinner attendance_spinStandardReport, attendance_spinSectionReport;
    Boolean isAttendanceReport = false;
    RecyclerView rcyAttendanceReport;
    AttendanceStudentReport isAttendanceStudentReport;
    public List<StudentAttendanceReport.AttendanceReport> isStudentAttendanceReport = new ArrayList<StudentAttendanceReport.AttendanceReport>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_attendance_screen);

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1) || (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_ADMIN)) && (listschooldetails.size() == 1)) {
            SchoolID = Principal_SchoolId;
            StaffID = Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            Principal_SchoolId = SchoolID;
            Principal_staffId = StaffID;
        }


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        ImageView ivBack = (ImageView) findViewById(R.id.attendance_IvBack);
        lblDatePicking = (TextView) findViewById(R.id.lblDatePicking);
        lblDatePickingReport = (TextView) findViewById(R.id.lblDatePickingReport);
        attendance_spinStandardReport = (Spinner) findViewById(R.id.attendance_spinStandardReport);
        attendance_spinSectionReport = (Spinner) findViewById(R.id.attendance_spinSectionReport);
        rcyAttendanceReport = (RecyclerView) findViewById(R.id.rcyAttendanceReport);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnMarkAllPresent = (Button) findViewById(R.id.attendance_btnMarkAllPresent);
        rlaAttendanceReport = (RelativeLayout) findViewById(R.id.rlaAttendanceReport);
        lnrAttendanceMarking = (LinearLayout) findViewById(R.id.lnrAttendanceMarking);
        lblAttendanceReport = (TextView) findViewById(R.id.lblAttendanceReport);
        lblAttendanceMark = (TextView) findViewById(R.id.lblAttendanceMark);

        lblAttendanceReport.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                lnrAttendanceMarking.setVisibility(View.GONE);
                rlaAttendanceReport.setVisibility(View.VISIBLE);
                lblAttendanceMark.setTextColor(Color.BLACK);
                lblAttendanceReport.setTextColor(Color.WHITE);

                lblAttendanceMark.setBackgroundColor(getResources().getColor(R.color.clr_white));
                lblAttendanceReport.setBackgroundColor(getResources().getColor(R.color.clr_yellow));

                try {
                    getAttendanceReport();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        lblAttendanceMark.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                lnrAttendanceMarking.setVisibility(View.VISIBLE);
                rlaAttendanceReport.setVisibility(View.GONE);
                lblAttendanceMark.setTextColor(Color.WHITE);
                lblAttendanceReport.setTextColor(Color.BLACK);
                lblAttendanceMark.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
                lblAttendanceReport.setBackgroundColor(getResources().getColor(R.color.clr_white));

            }
        });

        btnMarkAllPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmAlert("Confirm", "Mark All as Present?");
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String currentDate = sdf.format(new Date());
        try {
            lblDatePicking.setText(convertDateFormat(currentDate));
            lblDatePickingReport.setText(convertDateFormat(currentDate));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        isAttendanceDate = currentDate;

        btnSelectStudents = (Button) findViewById(R.id.attendance_btnSelectStudents);
        attendance_tv5 = (TextView) findViewById(R.id.attendance_tv5);
        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!attendanceType.equals("")) {
                    TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "English", "101", strTotalStudents, "0", false);
                    Intent inStud = new Intent(TeacherAttendanceScreen.this, TeacherAttendanceStudentList.class);
                    inStud.putExtra("STD_SEC", stdSec);
                    inStud.putExtra("SECCODE", strSecCode);
                    inStud.putExtra("STDCODE", strstdcode);
                    inStud.putExtra("REQUEST_CODE", iRequestCode);
                    inStud.putExtra("ATTENDANCE_DATE", isAttendanceDate);
                    startActivityForResult(inStud, iRequestCode);
                } else {
                    showToast("Kindly select the attendance type");
                }
            }
        });

        lblDatePicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAttendanceReport = false;
                isDatePicking();
            }
        });

        lblDatePickingReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAttendanceReport = true;
                isDatePicking();
            }
        });

        spinStandard = (Spinner) findViewById(R.id.attendance_spinStandard);
        spinSection = (Spinner) findViewById(R.id.attendance_spinSection);

        attendance_spinType = (Spinner) findViewById(R.id.attendance_spinType);
        attendance_spinSession = (Spinner) findViewById(R.id.attendance_spinSession);

        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());
                listSection = new ArrayList<>();
                listSectionID = new ArrayList<>();
                listTotalStudentsInSec = new ArrayList<String>();
                strStdName = listStd.get(position);
                strstdcode = listStdcode.get(position);
                for (int i = 0; i < arrSectionCollections.size(); i++) {
                    listSection.add(arrSectionCollections.get(i).getStrSectionName());
                    listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                    listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                }

                adaSection = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listSection);
                adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                spinSection.setAdapter(adaSection);
                attendance_spinSectionReport.setAdapter(adaSection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attendance_spinStandardReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());
                listSection = new ArrayList<>();
                listSectionID = new ArrayList<>();
                listTotalStudentsInSec = new ArrayList<String>();
                strStdName = listStd.get(position);
                strstdcode = listStdcode.get(position);
                for (int i = 0; i < arrSectionCollections.size(); i++) {
                    listSection.add(arrSectionCollections.get(i).getStrSectionName());
                    listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                    listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                }

                adaSection = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listSection);
                adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                spinSection.setAdapter(adaSection);
                attendance_spinSectionReport.setAdapter(adaSection);
                //getAttendanceReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSecName = listSection.get(position);
                strSecCode = listSectionID.get(position);
                strTotalStudents = listTotalStudentsInSec.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attendance_spinSectionReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSecName = listSection.get(position);
                strSecCode = listSectionID.get(position);
                strTotalStudents = listTotalStudentsInSec.get(position);
                try {
                    getAttendanceReport();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listAttendanceTypes.clear();
        listSessionTypes.clear();

        listAttendanceTypes.add("Select attendance type");
        listAttendanceTypes.add("Full Day");
        listAttendanceTypes.add("Half Day");

        listSessionTypes.add("First Half");
        listSessionTypes.add("Second Half");
        attendance_spinType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(listAttendanceTypes.get(position).equals("Half Day")){
                    attendanceType = "H";
                }
                else if(listAttendanceTypes.get(position).equals("Full Day")) {
                    attendanceType = "F";
                }
                else {
                    attendanceType = "";
                }

                if(attendanceType.equals("H")){
                    attendance_tv5.setVisibility(View.VISIBLE);
                    attendance_spinSession.setVisibility(View.VISIBLE);
                }
                else {
                    Constants.sessionType = "";
                    attendance_tv5.setVisibility(View.GONE);
                    attendance_spinSession.setVisibility(View.GONE);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attendance_spinSession.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(listSessionTypes.get(position).equals("First Half")){
                    Constants.sessionType = "FH";
                }
                else {
                    Constants.sessionType = "SH";
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        adaAttendanceTypes = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listAttendanceTypes);
        adaAttendanceTypes.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        attendance_spinType.setAdapter(adaAttendanceTypes);

        adaSessionTypes = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listSessionTypes);
        adaSessionTypes.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        attendance_spinSession.setAdapter(adaSessionTypes);
        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1) || (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_ADMIN)) && (listschooldetails.size() == 1)) {
            SchoolID = Principal_SchoolId;
            StaffID = Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            Principal_SchoolId = SchoolID;
            Principal_staffId = StaffID;
        }

        standardsAndSectoinsListAPI();
    }


    private void isDatePicking() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                TeacherAttendanceScreen.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);
                    try {
                        if (isAttendanceReport) {
                            lblDatePickingReport.setText(convertDateFormat(selectedDate));
                            getAttendanceReport();
                        } else {
                            lblDatePicking.setText(convertDateFormat(selectedDate));
                        }

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    isAttendanceDate = selectedDate;
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private String convertDateFormat(String dateStr) throws ParseException {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = originalFormat.parse(dateStr);
        return desiredFormat.format(date);
    }

    private String convertDateFormat1(String dateStr) throws ParseException {
        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd MMM yyyy");

        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = desiredFormat.parse(dateStr);
        return originalFormat.format(date);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void standardsAndSectoinsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherAttendanceScreen.this);
        if (isNewVersion.equals("1")) {
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherAttendanceScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArraySchoolStd();
        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());

                    if (js.length() > 0)
                    {
                        {
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                }

                                else {
                                    stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));

                                    listStdcode.add(jsonObject.getString("StandardId"));
                                    listStd.add(jsonObject.getString("Standard"));

                                    ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                                    ArrayList<TeacherSubjectModel> listSubjects = new ArrayList<>();
                                    JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                                    if (jsArySections.length() > 0) {
                                        JSONObject jObjStd;
                                        TeacherSectionsListNEW sectionsList;
                                        for (int j = 0; j < jsArySections.length(); j++) {
                                            jObjStd = jsArySections.getJSONObject(j);
                                            if (jObjStd.getString("SectionId").equals("0")) {
                                                showToast(jObjStd.getString("SectionName"));
                                                finish();
                                            } else {
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            }
                                        }
                                    }
                                    stdSecList.setListSectionsNew(listSections);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }
                                adaStd = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listStd);
                                adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                                spinStandard.setAdapter(adaStd);
                                attendance_spinStandardReport.setAdapter(adaStd);
                            }
                        }
                    }
                    else {
                        showToast(getResources().getString(R.string.no_records));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.check_internet));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                onBackPressed();
            }
        });
    }

    private JsonObject constructJsonArraySchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", Principal_staffId);
            jsonObjectSchool.addProperty("isAttendance", "0");

            Log.d("request",jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }



    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void sendAttenAPIPresent() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayPresent();
        Call<JsonArray> call = apiService.SendAbsenteesSMS(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("AtteSMS:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("AtteSMS:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");

                        if ((strStatus.toLowerCase()).equals("1")) {
                            showAlert(strMsg);
                        }
                        else{
                            showAlert(strMsg);
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    showToast(getResources().getString(R.string.check_internet));
                    Log.e("AtteSMS:Excep", e.getMessage());
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

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherAttendanceScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // backToResultActvity("SENT");

                Intent inStud = new Intent(TeacherAttendanceScreen.this, TeacherAttendanceScreen.class);
                inStud.putExtra("REQUEST_CODE", iRequestCode);
                startActivity(inStud);
                dialog.cancel();
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private JsonObject constructJsonArrayPresent() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", Principal_staffId);
            jsonObjectSchool.addProperty("ClassId", strstdcode);
            jsonObjectSchool.addProperty("SectionID", strSecCode);
            jsonObjectSchool.addProperty("AllPresent", "T");
            jsonObjectSchool.addProperty("AttendanceType", attendanceType);
            jsonObjectSchool.addProperty("SessionType", Constants.sessionType);
            jsonObjectSchool.addProperty("AttendanceDate", isAttendanceDate);
            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < listschooldetails.size(); i++) {

            }
            jsonObjectSchool.add("StudentID", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }
    private void showConfirmAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherAttendanceScreen.this);
        AlertDialog alertDialog;
        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(R.drawable.teacher_ic_voice_snap);
        builder.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(!attendanceType.equals("")) {
                    sendAttenAPIPresent();
                }
                else {
                    showToast("Kindly select the attendance type");
                }
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void getAttendanceReport() throws ParseException {

//        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceScreen.this);
//        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherAttendanceScreen.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherAttendanceScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<StudentAttendanceReport> call = apiService.getAttendanceReport(SchoolID, strSecCode, convertDateFormat1(lblDatePickingReport.getText().toString()), convertDateFormat1(lblDatePickingReport.getText().toString()), strstdcode, StaffID);
        call.enqueue(new Callback<StudentAttendanceReport>() {
            @Override
            public void onResponse(Call<StudentAttendanceReport> call, retrofit2.Response<StudentAttendanceReport> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("attendanceReport", data);
                        Log.d("attendanceReport", response.body().toString());
                        isStudentAttendanceReport.clear();
                        if (response.body().getStatus() == 1) {
                            isStudentAttendanceReport = response.body().getData();
                            isAttendanceStudentReport = new AttendanceStudentReport(isStudentAttendanceReport, TeacherAttendanceScreen.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TeacherAttendanceScreen.this);
                            rcyAttendanceReport.setLayoutManager(mLayoutManager);
                            rcyAttendanceReport.setItemAnimator(new DefaultItemAnimator());
                            rcyAttendanceReport.setAdapter(isAttendanceStudentReport);
                            rcyAttendanceReport.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            isAttendanceStudentReport.notifyDataSetChanged();
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
            public void onFailure(Call<StudentAttendanceReport> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
