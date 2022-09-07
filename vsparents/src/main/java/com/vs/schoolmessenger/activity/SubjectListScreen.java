package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SubjectListAdapter;
import com.vs.schoolmessenger.interfaces.SubjecstListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamList;
import com.vs.schoolmessenger.model.SubjectDetails;
import com.vs.schoolmessenger.model.TeacherSectionModel;
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

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;

public class SubjectListScreen extends AppCompatActivity implements SubjecstListener {

    String Staff_ID, school_ID, ExamName, ExamSyllabus, Sections, strSecCode;

    RecyclerView Exam_Date_recycle;
    Button btnSend, staffStdSecSub_btnToStudents;
    private List<ExamList> Exam_list = new ArrayList<>();

    public SubjectListAdapter mAdapter;
    ImageView schoollist_ToolBarIvBack;

    private int iRequestCode = 0;
    String strToWhom = "";

    String schoolId, sectioncode, stdcode, subcode, filepath, Description, duration, strmessage, targetCode;
    String strStdName, strSecName, strSecCode1, strSubjectCode, strSubjectName, strTotalStudents;


    TeacherSectionModel selSection;

    private ArrayList<TeacherSectionsListNEW> seletedSectionsList = new ArrayList<>();
    ArrayList<TeacherSubjectModel> listSubjects1 = new ArrayList<TeacherSubjectModel>();
    ArrayList<TeacherSubjectModel> listSubjects2 = new ArrayList<TeacherSubjectModel>();

    ArrayList<TeacherSubjectModel> SelectedSubjects = new ArrayList<TeacherSubjectModel>();

    int i_sections_count;


    public static ArrayList<SubjectDetails> SubjectDetailsList = new ArrayList<SubjectDetails>();

    ArrayList<TeacherSectionsListNEW> Section = new ArrayList<TeacherSectionsListNEW>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.exam_date);

        Exam_Date_recycle = (RecyclerView) findViewById(R.id.Exam_Date_recycle);
        btnSend = (Button) findViewById(R.id.btnSend);
        staffStdSecSub_btnToStudents = (Button) findViewById(R.id.staffStdSecSub_btnToStudents);
        schoollist_ToolBarIvBack = (ImageView) findViewById(R.id.schoollist_ToolBarIvBack);


        Staff_ID = TeacherUtil_Common.Principal_staffId;
        school_ID = TeacherUtil_Common.Principal_SchoolId;
        ExamName = getIntent().getExtras().getString("ExamName");
        ExamSyllabus = getIntent().getExtras().getString("ExamSyllabus");
        Sections = getIntent().getExtras().getString("SectionList");
        strSecCode = getIntent().getExtras().getString("SECCODE");

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));
        strToWhom = getIntent().getExtras().getString("TO", "");
        Log.d("strToWhom", strToWhom);

        Section = (ArrayList<TeacherSectionsListNEW>) getIntent().getSerializableExtra("SectionList123");

        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));
        selSection = (TeacherSectionModel) getIntent().getSerializableExtra("STD_SEC");
        schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
        sectioncode = getIntent().getExtras().getString("SECCODE", "");
        stdcode = getIntent().getExtras().getString("STDCODE", "");
        subcode = getIntent().getExtras().getString("SUBCODE", "");
        filepath = getIntent().getExtras().getString("filepath", "");
        Description = getIntent().getExtras().getString("Description", "");
        duration = getIntent().getExtras().getString("duration", "");
        strmessage = getIntent().getExtras().getString("Message", "");

        SubjectDetailsList.clear();

        listSubjects1 = (ArrayList<TeacherSubjectModel>) getIntent().getSerializableExtra("SubjectsList");

        if (strToWhom.equals("STU")) {
            staffStdSecSub_btnToStudents.setVisibility(View.VISIBLE);
        }


        staffStdSecSub_btnToStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (SubjectDetailsList.size() > 0) {

                    TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode1, "", "", strTotalStudents, "0", false);
                    Intent inStud = new Intent(SubjectListScreen.this, TeacherAttendanceStudentList.class);
                    inStud.putExtra("STD_SEC", selSection);

                    inStud.putExtra("REQUEST_CODE", iRequestCode);
                    inStud.putExtra("SCHOOL_ID", TeacherUtil_Common.Principal_SchoolId);
                    inStud.putExtra("SECCODE", strSecCode);
                    inStud.putExtra("SUBCODE", subcode);
                    inStud.putExtra("filepath", filepath);
                    inStud.putExtra("duration", duration);

                    inStud.putExtra("Description", ExamSyllabus);
                    inStud.putExtra("Message", ExamName);

                    Log.d("DD", duration + " " + Description);
                    startActivity(inStud);
                } else {
                    showToast("Please Add Atleast one Subject ");
                }

            }
        });
        schoollist_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (SubjectDetailsList.size() > 0) {
                   // if (strToWhom.equals("SEC")) {
                        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
                            SendExam();
                        }
                    //}
//                    if (strToWhom.equals("STU")) {
//                        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
//                            SendExamSpecificsection();
//                        }
//                    }

                } else {
                    showToast(getResources().getString(R.string.select_subjects));
                }
                Log.d("Sub_listSize", String.valueOf(SubjectDetailsList.size()));

            }
        });

        btnSend.setEnabled(false);

        i_sections_count = 0;
        mAdapter = new SubjectListAdapter(SubjectListScreen.this, new SubjecstListener() {
            @Override
            public void student_addClass(TeacherSubjectModel subjects) {
                if ((subjects != null) && (!SelectedSubjects.contains(subjects))) {
                    SelectedSubjects.add(subjects);
                    i_sections_count++;
                    enableDisableNext();
                }
            }

            @Override
            public void student_removeClass(TeacherSubjectModel subjects) {
                if ((subjects != null) && (SelectedSubjects.contains(subjects))) {
                    SelectedSubjects.remove(subjects);
                    i_sections_count--;
                    enableDisableNext();
                }
            }
        }, listSubjects1);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        Exam_Date_recycle.setLayoutManager(mLayoutManager);
        Exam_Date_recycle.setItemAnimator(new DefaultItemAnimator());
        Exam_Date_recycle.setAdapter(mAdapter);
        Exam_Date_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);



    }

    private void enableDisableNext() {
        if (i_sections_count > 0)
            btnSend.setEnabled(true);
        else
            btnSend.setEnabled(false);

    }

    private void subjectList() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStdHW();
        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaff(jsonReqArray);
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
                    if (js.length() > 0) {
                        {
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", js.length() + "");


                            mAdapter.clearAllData();
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                } else {
                                    stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));
                              ;

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
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            } else {
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            }
                                        }
                                    }


                                    JSONArray jsArySubjects = jsonObject.getJSONArray("Subjects");
                                    if (jsArySubjects.length() > 0) {
                                        JSONObject jObjSub;
                                        TeacherSubjectModel subjectList;

                                        for (int k = 0; k < jsArySubjects.length(); k++) {
                                            jObjSub = jsArySubjects.getJSONObject(k);

                                            subjectList = new TeacherSubjectModel(jObjSub.getString("SubjectName"), jObjSub.getString("SubjectId"), false);
                                            listSubjects2.add(subjectList);
                                        }
                                        mAdapter.notifyDataSetChanged();

                                    }



                                }


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
                Log.d("StdSecList:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolStdHW() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", TeacherUtil_Common.Principal_staffId);
            Log.d("schoolid", TeacherUtil_Common.Principal_SchoolId);
            Log.d("staffstd&sec", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void SendExamSpecificsection() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(SubjectListScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(SubjectListScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstextSpecificSec();
        Call<JsonArray> call = apiService.InsertExamToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);
                            } else {
                                showAlert(strMsg);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolstextSpecificSec() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("ExamName", ExamName);
            jsonObjectSchoolstdgrp.addProperty("ExamSyllabus", ExamSyllabus);

            JsonArray jsonArrayschoolstd = new JsonArray();
            JsonObject jsonObjectclass = new JsonObject();
            jsonObjectclass.addProperty("TargetCode", strSecCode);
            Log.d("schoolid", strSecCode);
            jsonArrayschoolstd.add(jsonObjectclass);


            JsonArray jsonArrayschoolstd1 = new JsonArray();
            for (int i = 0; i < SubjectDetailsList.size(); i++) {
                SubjectDetails sectionsListNEW = SubjectDetailsList.get(i);
                JsonObject jsonObjectclass1 = new JsonObject();
                jsonObjectclass1.addProperty("Subcode", sectionsListNEW.getStrSubCode());
                jsonObjectclass1.addProperty("ExamDate", sectionsListNEW.getDate());
                jsonObjectclass1.addProperty("Session", sectionsListNEW.getSession());
                jsonObjectclass1.addProperty("MaxMark", sectionsListNEW.getMaxMark());

                jsonArrayschoolstd1.add(jsonObjectclass1);

            }
            jsonObjectSchoolstdgrp.add("Subjects", jsonArrayschoolstd1);
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }


        Log.d("Request",jsonObjectSchoolstdgrp.toString());
        return jsonObjectSchoolstdgrp;
    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void SendExam() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(SubjectListScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(SubjectListScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstext();
        Call<JsonArray> call = apiService.InsertExamToEntireSection_WithSubjectSyllabus(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);

                            } else {
                                showAlert(strMsg);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubjectListScreen.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);


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
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private JsonObject constructJsonArrayMgtSchoolstext() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("ExamName", ExamName);
            jsonObjectSchoolstdgrp.addProperty("ExamSyllabus", ExamSyllabus);



            JsonArray jsonArrayschoolstd_section = new JsonArray();
            for (int i = 0; i < Section.size(); i++) {
                TeacherSectionsListNEW sectionsListNEW = Section.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", sectionsListNEW.getStrSectionCode());
                Log.d("schoolid", sectionsListNEW.getStrSectionCode());
                jsonArrayschoolstd_section.add(jsonObjectclass);

            }
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd_section);


            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < SubjectDetailsList.size(); i++) {
                SubjectDetails sectionsListNEW = SubjectDetailsList.get(i);
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("Subcode", sectionsListNEW.getStrSubCode());
                jsonObjectclass.addProperty("ExamDate", sectionsListNEW.getDate());
                jsonObjectclass.addProperty("Session", sectionsListNEW.getSession());
                jsonObjectclass.addProperty("MaxMark", sectionsListNEW.getMaxMark());
                jsonObjectclass.addProperty("Syllabus", sectionsListNEW.getSubjectSyllabus());
                jsonArrayschoolstd.add(jsonObjectclass);

            }

            Log.d("Subjects_list", jsonArrayschoolstd.toString());

            Log.d("TTgroup", "1");
            jsonObjectSchoolstdgrp.add("Subjects", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void examListApi() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(SubjectListScreen.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(SubjectListScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(SubjectListScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", school_ID);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.GetStudentExamList(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        ExamList data;
                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {
                            mAdapter.clearAllData();
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String value = jsonObject.getString("value");
                                if (value.equals("-2")) {
                                    String Name = jsonObject.getString("name");
                                    showRecords(Name);
                                } else {
                                    String ExamID = jsonObject.getString("value");
                                    String ExamName = jsonObject.getString("name");
                                    data = new ExamList(ExamID, ExamName);
                                    Exam_list.add(data);
                                }

                            }
                            mAdapter.notifyDataSetChanged();

                        } else {
                            showRecords("No Records found");
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Server Response Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecords(String name) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SubjectListScreen.this);

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
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }


    @Override
    public void student_addClass(TeacherSubjectModel student) {

    }

    @Override
    public void student_removeClass(TeacherSubjectModel student) {

    }
}

