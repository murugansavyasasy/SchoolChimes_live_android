package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.vs.schoolmessenger.adapter.StudentReportAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StudentReportModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
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

public class StudentReportActivity extends AppCompatActivity {

    public StudentReportAdapter mAdapter;
    String school_ID, staff_ID;
    RecyclerView Exam_list_recycle;
    ImageView imgSearch;
    TextView Searchable, lblGetAllStudent, lblSection;
    Spinner spinStandard, spinSection;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    List<String> listStd = new ArrayList<>();
    List<String> listStdcode = new ArrayList<>();
    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListNEW> arrSectionCollections;
    String SchoolID;
    List<String> listTotalStudentsInSec;
    String strStdName, strstdcode, strSecName, strSecCode, strTotalStudents;
    private final List<StudentReportModel> Exam_list = new ArrayList<>();
    private final int iRequestCode = 0;
    private final ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.student_reports);

        Exam_list_recycle = (RecyclerView) findViewById(R.id.Exam_list_recycle);
        spinStandard = (Spinner) findViewById(R.id.attendance_spinStandard);
        spinSection = (Spinner) findViewById(R.id.attendance_spinSection);
        lblGetAllStudent = (TextView) findViewById(R.id.lblGetAllStudent);
        lblSection = (TextView) findViewById(R.id.lblSection);

        if ((listschooldetails.size() == 1)) {
            school_ID = TeacherUtil_Common.Principal_SchoolId;
            staff_ID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(StudentReportActivity.this).equals(LOGIN_TYPE_TEACHER)) {
            school_ID = TeacherUtil_Common.Principal_SchoolId;
            staff_ID = TeacherUtil_Common.Principal_staffId;
        } else {
            school_ID = getIntent().getExtras().getString("SCHOOL_ID", "");
            staff_ID = getIntent().getExtras().getString("STAFF_ID", "");
        }


        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    Exam_list_recycle.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        Exam_list_recycle.setVisibility(View.VISIBLE);
                    }

                } else {
                    Exam_list_recycle.setVisibility(View.VISIBLE);
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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Student Report");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdapter = new StudentReportAdapter(Exam_list, StudentReportActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        Exam_list_recycle.setLayoutManager(mLayoutManager);
        Exam_list_recycle.setItemAnimator(new DefaultItemAnimator());
        Exam_list_recycle.setAdapter(mAdapter);


        standardsAndSectoinsListAPI();

        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    lblSection.setVisibility(View.VISIBLE);
                    spinSection.setVisibility(View.VISIBLE);
                    arrSectionCollections = new ArrayList<>();
                    arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position - 1).getListSectionsNew());
                    listSection = new ArrayList<>();
                    listSectionID = new ArrayList<>();
                    listTotalStudentsInSec = new ArrayList<String>();
                    strStdName = listStd.get(position);
                    strstdcode = listStdcode.get(position);

                    listSection.clear();
                    listSectionID.clear();
                    listSection.add("Select Section");
                    listSectionID.add("");

                    for (int i = 0; i < arrSectionCollections.size(); i++) {
                        listSection.add(arrSectionCollections.get(i).getStrSectionName());
                        listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                        listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                    }

                    adaSection = new ArrayAdapter<>(StudentReportActivity.this, R.layout.teacher_spin_title, listSection);
                    adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                    spinSection.setAdapter(adaSection);
                } else {
                    lblSection.setVisibility(View.GONE);
                    spinSection.setVisibility(View.GONE);
                    getStudentReportList("", "");

                }
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

                getStudentReportList(strstdcode, strSecCode);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        lblGetAllStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStudentReportList("", "");

            }
        });
    }

    private void standardsAndSectoinsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StudentReportActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StudentReportActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StudentReportActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(StudentReportActivity.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArraySchoolStd();
        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());

                    if (js.length() > 0) {
                        {

                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", String.valueOf(js.length()));

                            listStd.clear();
                            listStdcode.clear();
                            listStd.add("Select Standard");
                            listStdcode.add("");

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                } else {
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
                                adaStd = new ArrayAdapter<>(StudentReportActivity.this, R.layout.teacher_spin_title, listStd);
                                adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                                spinStandard.setAdapter(adaStd);
                            }
                        }
                    } else {
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
            jsonObjectSchool.addProperty("SchoolId", school_ID);
            jsonObjectSchool.addProperty("StaffID", staff_ID);
            jsonObjectSchool.addProperty("isAttendance", "0");

            Log.d("staffstd&sec", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void filterlist(String s) {
        List<StudentReportModel> temp = new ArrayList();
        for (StudentReportModel d : Exam_list) {

            if (d.getStudentName().toLowerCase().contains(s.toLowerCase()) || d.getClassName().toLowerCase().contains(s.toLowerCase()) || d.getSectionName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) StudentReportActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void getStudentReportList(String stdCode, String secCode) {

        String ReportURL = TeacherUtil_SharedPreference.getReportURL(StudentReportActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("class_id", stdCode);
        jsonObject.addProperty("section_id", secCode);
        jsonObject.addProperty("institute_id", school_ID);
        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetStudentReport(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        StudentReportModel data;
                        JSONObject js = new JSONObject(response.body().toString());

                        int status = js.getInt("status");
                        String message = js.getString("message");
                        if (status == 1) {
                            mAdapter.clearAllData();
                            Exam_list.clear();
                            JSONArray array = js.getJSONArray("data");
                            if (array.length() > 0) {
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject jsonObject = array.getJSONObject(i);

                                    int studentId = jsonObject.getInt("studentId");
                                    int classId = jsonObject.getInt("classId");
                                    int sectionId = jsonObject.getInt("sectionId");
                                    String studentName = jsonObject.getString("studentName");
                                    String primaryMobile = jsonObject.getString("primaryMobile");
                                    String admissionNo = jsonObject.getString("admissionNo");
                                    String gender = jsonObject.getString("gender");
                                    String dob = jsonObject.getString("dob");
                                    String className = jsonObject.getString("className");
                                    String sectionName = jsonObject.getString("sectionName");
                                    String fatherName = jsonObject.getString("fatherName");
                                    String classTeacher = jsonObject.getString("classTeacher");

                                    data = new StudentReportModel(studentId, studentName, primaryMobile, admissionNo, gender,
                                            dob, classId, className, sectionId, sectionName, fatherName, classTeacher);
                                    Exam_list.add(data);

                                }
                                mAdapter.notifyDataSetChanged();
                            }

                        } else {
                            showRecords(message);
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecords(String name) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentReportActivity.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(name);
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

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }
}