package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class SubjectListScreen extends AppCompatActivity implements SubjecstListener {

    public static ArrayList<SubjectDetails> SubjectDetailsList = new ArrayList<SubjectDetails>();
    public SubjectListAdapter mAdapter;
    String Staff_ID, school_ID, ExamName, ExamSyllabus, Sections, strSecCode;
    RecyclerView Exam_Date_recycle;
    Button btnSend, staffStdSecSub_btnToStudents;
    ImageView schoollist_ToolBarIvBack;
    String strToWhom = "";
    String schoolId, sectioncode, stdcode, subcode, filepath, Description, duration, strmessage, targetCode;
    String strStdName, strSecName, strSecCode1, strSubjectCode, strSubjectName, strTotalStudents;
    TeacherSectionModel selSection;
    ArrayList<TeacherSubjectModel> listSubjects1 = new ArrayList<TeacherSubjectModel>();
    ArrayList<TeacherSubjectModel> listSubjects2 = new ArrayList<TeacherSubjectModel>();
    ArrayList<TeacherSubjectModel> SelectedSubjects = new ArrayList<TeacherSubjectModel>();
    int i_sections_count;
    ArrayList<TeacherSectionsListNEW> Section = new ArrayList<TeacherSectionsListNEW>();
    private final List<ExamList> Exam_list = new ArrayList<>();
    private int iRequestCode = 0;

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

        Section = (ArrayList<TeacherSectionsListNEW>) getIntent().getSerializableExtra("SectionList123");

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

        if (strToWhom.equals("STU") && iRequestCode != PRINCIPAL_EXAM_TEST && iRequestCode != STAFF_TEXT_EXAM) {
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
                    if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
                        SendExam();
                    }

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
        btnSend.setEnabled(i_sections_count > 0);

    }

    private void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }

    private void SendExam() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SubjectListScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(SubjectListScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
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
                Log.d("Upload error:", t.getMessage() + "\n" + t);
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

            jsonObjectSchoolstdgrp.add("Subjects", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    @Override
    public void student_addClass(TeacherSubjectModel student) {

    }

    @Override
    public void student_removeClass(TeacherSubjectModel student) {

    }
}

