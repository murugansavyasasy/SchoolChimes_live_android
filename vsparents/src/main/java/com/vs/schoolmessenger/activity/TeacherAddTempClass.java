package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListModel;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherAddTempClass extends AppCompatActivity {


    Spinner spinStandard, spinSection, spinSubject;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    ArrayAdapter<String> adaSubject;
    Button btnAdd;

    List<String> listStd = new ArrayList<>();
    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListModel> arrSectionCollections;

    List<String> listSubjectName;
    List<String> listSubjectCode;
    List<String> listTotalStudentsInSec;

    private ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();

    String strStdName, strSecName, strSecCode, strSubjectCode, strSubjectName, strTotalStudents;
    public static int ADD_TEMP_CLASS_STATUS = 222;

    TeacherSectionModel stdSec;// = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "English", "101", "9", "0", false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_add_temp_class);
        ImageView ivBack = (ImageView) findViewById(R.id.addTempClass_IvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAdd = (Button) findViewById(R.id.addTempClass_btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, strSubjectName, strSubjectCode, strTotalStudents, strTotalStudents, true);
                backToResultActvity("OK");
            }
        });

        spinStandard = (Spinner) findViewById(R.id.addTempClass_spinStandard);
        spinSection = (Spinner) findViewById(R.id.addTempClass_spinSection);
        spinSubject = (Spinner) findViewById(R.id.addTempClass_spinSubject);
        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSections());
                listSection = new ArrayList<>();
                listSectionID = new ArrayList<>();
                listTotalStudentsInSec = new ArrayList<String>();
                strStdName = listStd.get(position);

                for (int i = 0; i < arrSectionCollections.size(); i++) {
                    listSection.add(arrSectionCollections.get(i).getStrSectionName());
                    listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                    listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                }

                adaSection = new ArrayAdapter<>(TeacherAddTempClass.this, R.layout.teacher_spin_title, listSection);
                adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                spinSection.setAdapter(adaSection);
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

                subjectsListAPI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSubjectCode = listSubjectCode.get(position);
                strSubjectName = listSubjectName.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        standardsAndSectoinsListAPI();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        returnIntent.putExtra("STD_SEC", stdSec);
        setResult(ADD_TEMP_CLASS_STATUS, returnIntent);
        finish();
    }

    private void standardsAndSectoinsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAddTempClass.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetSecAtt(schoolID);
        Call<JsonArray> call = apiService.GetStandardsAndSectionsList(jsonReqArray);
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
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStdName = jsonObject.getString("StdName");
                        String strStdCode = jsonObject.getString("StdCode");

                        if (strStdName.equals("")) {
                            showToast(strStdCode);
                            onBackPressed();
                        } else {
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("StdName"), jsonObject.getString("StdCode"));
                                listStd.add(jsonObject.getString("StdName"));

                                ArrayList<TeacherSectionsListModel> listSections = new ArrayList<>();
                                JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                                if (jsArySections.length() > 0) {
                                    JSONObject jObjStd;
                                    TeacherSectionsListModel sectionsList;
                                    for (int j = 0; j < jsArySections.length(); j++) {
                                        jObjStd = jsArySections.getJSONObject(j);
                                        sectionsList = new TeacherSectionsListModel(jObjStd.getString("SecName"), jObjStd.getString("SecCode"));
                                        sectionsList.setStrTotalStudents(jObjStd.getString("TotalStudents"));
                                        listSections.add(sectionsList);

                                    }
                                }

                                stdSecList.setListSections(listSections);
                                arrStandardsAndSectionsList.add(stdSecList);

                            }

                            adaStd = new ArrayAdapter<>(TeacherAddTempClass.this, R.layout.teacher_spin_title, listStd);
                            adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinStandard.setAdapter(adaStd);
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
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


    private void subjectsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAddTempClass.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetSubjects(schoolID, strSecCode);
        Call<JsonArray> call = apiService.GetClassSubjects(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("SubjectsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("SubjectsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String subName = jsonObject.getString("SubName");
                        String subCode = jsonObject.getString("SubCode");

                        if (subCode.equals("")) {
                            showToast(subCode);
                            onBackPressed();
                        } else {
                            Log.d("json length", js.length() + "");

                            listSubjectName = new ArrayList<String>();
                            listSubjectCode = new ArrayList<String>();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                listSubjectName.add(jsonObject.getString("SubName"));
                                listSubjectCode.add(jsonObject.getString("SubCode"));

                            }

                            adaSubject = new ArrayAdapter<>(TeacherAddTempClass.this, R.layout.teacher_spin_title, listSubjectName);
                            adaSubject.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinSubject.setAdapter(adaSubject);

                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
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
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
