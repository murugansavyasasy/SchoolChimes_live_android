package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
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

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherAttendanceScreen extends AppCompatActivity {

    Spinner spinStandard, spinSection;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    Button btnMarkAllPresent, btnSelectStudents;

    List<String> listStd = new ArrayList<>();
    List<String> listStdcode = new ArrayList<>();
    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListNEW> arrSectionCollections;
    String SchoolID,StaffID;
    List<String> listTotalStudentsInSec;
    private int iRequestCode = 0;

    private ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();

    String strStdName,strstdcode, strSecName, strSecCode, strTotalStudents;
    public static int MARK_ATTENDASNCE_STATUS = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_attendance_screen);




        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)||(TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherAttendanceScreen.this).equals(LOGIN_TYPE_ADMIN)) && (listschooldetails.size() == 1)) {
            SchoolID= Principal_SchoolId ;
            StaffID= Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            Principal_SchoolId = SchoolID;
            Principal_staffId=StaffID;
        }


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        ImageView ivBack = (ImageView) findViewById(R.id.attendance_IvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnMarkAllPresent = (Button) findViewById(R.id.attendance_btnMarkAllPresent);
        btnMarkAllPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmAlert("Confirm", "Mark All as Present?");
            }
        });

        btnSelectStudents = (Button) findViewById(R.id.attendance_btnSelectStudents);
        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "English", "101", strTotalStudents, "0", false);
                Intent inStud = new Intent(TeacherAttendanceScreen.this, TeacherAttendanceStudentList.class);
                inStud.putExtra("STD_SEC", stdSec);
                inStud.putExtra("SECCODE", strSecCode);
                inStud.putExtra("STDCODE", strstdcode);
                inStud.putExtra("REQUEST_CODE", iRequestCode);
                startActivityForResult(inStud, iRequestCode);
            }
        });

        spinStandard = (Spinner) findViewById(R.id.attendance_spinStandard);
        spinSection = (Spinner) findViewById(R.id.attendance_spinSection);

        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());
                listSection = new ArrayList<>();
                listSectionID = new ArrayList<>();
                listTotalStudentsInSec = new ArrayList<String>();
                strStdName = listStd.get(position);
                strstdcode=listStdcode.get(position);
                for (int i = 0; i < arrSectionCollections.size(); i++) {
                    listSection.add(arrSectionCollections.get(i).getStrSectionName());
                    listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                    listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                }

                adaSection = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listSection);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                onBackPressed();
            }
        }
    }

    private void standardsAndSectoinsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherAttendanceScreen.this);
        if(isNewVersion.equals("1")){
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
                Log.d("StdSecList:Failure", t.toString());
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

            Log.d("schoolid", Principal_SchoolId);
            Log.d("staffstd&sec", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void standardsAndSectoinsListAPI_TEMP() {
        try {
            JSONArray js = new JSONArray("[{\"StdName\":\"II\",\"StdCode\":\"79\",\"Sections\":[{\"SecName\":\"NEW\",\"SecCode\":\"330\",\"TotalStudents\":\"1\"},{\"SecName\":\"B\",\"SecCode\":\"331\",\"TotalStudents\":\"8\"}]},{\"StdName\":\"IV\",\"StdCode\":\"80\",\"Sections\":[{\"SecName\":\"A\",\"SecCode\":\"334\",\"TotalStudents\":\"6\"}]},{\"StdName\":\"V\",\"StdCode\":\"81\",\"Sections\":[{\"SecName\":\"A\",\"SecCode\":\"338\",\"TotalStudents\":\"9\"}]},{\"StdName\":\"VI\",\"StdCode\":\"82\",\"Sections\":[{\"SecName\":\"A\",\"SecCode\":\"341\",\"TotalStudents\":\"5\"}]},{\"StdName\":\"VII\",\"StdCode\":\"83\",\"Sections\":[{\"SecName\":\"A\",\"SecCode\":\"344\",\"TotalStudents\":\"3\"}]},{\"StdName\":\"VIII\",\"StdCode\":\"84\",\"Sections\":[{\"SecName\":\"NEW\",\"SecCode\":\"346\",\"TotalStudents\":\"2\"},{\"SecName\":\"A\",\"SecCode\":\"347\",\"TotalStudents\":\"5\"}]}]");
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

                        ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                        JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                        if (jsArySections.length() > 0) {
                            JSONObject jObjStd;
                            TeacherSectionsListNEW sectionsList;
                            for (int j = 0; j < jsArySections.length(); j++) {
                                jObjStd = jsArySections.getJSONObject(j);
                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SecName"), jObjStd.getString("SecCode"),
                                        jObjStd.getString("TotalStudents"), false);
                                listSections.add(sectionsList);

                            }
                        }

                        stdSecList.setListSectionsNew(listSections);
                        arrStandardsAndSectionsList.add(stdSecList);

                    }

                    adaStd = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listStd);
                    adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                    spinStandard.setAdapter(adaStd);


                }

            } else {
                showToast("Server Response Failed. Try again");
                onBackPressed();
            }

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            showToast("Server Response Failed");
            onBackPressed();
        }
    }

    private void standardsAndSectoinsListAPI1() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceScreen.this);

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

                            adaStd = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listStd);
                            adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinStandard.setAdapter(adaStd);

                        }

                    } else {
                        showToast("Server Response Failed. Try again");
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast("Server Response Failed");
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Error! Try Again");
                Log.d("StdSecList:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private void stdsecListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetSchoolStrengthBySchoolID(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        Log.d("json length", js.length() + "");
                        for (int i = 0; i < js.length(); i++) {
                            JSONArray jSONArray = jsonObject.getJSONArray("Standards");
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", jSONArray.length() + "");

                            for (int j = 0; j < jSONArray.length(); j++) {
                                JSONObject jsonObjectstd = jSONArray.getJSONObject(j);
                                stdSecList = new TeacherStandardSectionsListModel(jsonObjectstd.getString("StdName"), jsonObjectstd.getString("StdCode"));
                                listStd.add(jsonObjectstd.getString("StdName"));

                                ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                                JSONArray jsArySections = jsonObjectstd.getJSONArray("Sections");
                                if (jsArySections.length() > 0) {
                                    JSONObject jObjStd;
                                    TeacherSectionsListNEW sectionsList;
                                    for (int k = 0; k < jsArySections.length(); k++) {
                                        jObjStd = jsArySections.getJSONObject(k);
                                        sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SecName"), jObjStd.getString("SecCode"),
                                                jObjStd.getString("TotalStudents"), false);
                                        listSections.add(sectionsList);

                                    }
                                }

                                stdSecList.setListSectionsNew(listSections);
                                arrStandardsAndSectionsList.add(stdSecList);

                            }

                            adaStd = new ArrayAdapter<>(TeacherAttendanceScreen.this, R.layout.teacher_spin_title, listStd);
                            adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinStandard.setAdapter(adaStd);
                        }

                    } else {
                        showToast("Server Response Failed. Try again");
                    }

                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Error! Try Again");
                Log.d("SubjectHandling:Failure", t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);
            Log.d("schoolid", Principal_SchoolId);
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void sendAttenAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceScreen.this);
        String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherAttendanceScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_SendSMSAtt(schoolID, staffID, "T", new JsonArray());
        Call<JsonArray> call = apiService.SendAttendanceReport(jsonReqArray);
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
                        showToast(strMsg);

                        if ((strStatus.toLowerCase()).equals("y")) {
                            onBackPressed();
                        }

                    } else {
                        showToast("Server Response Failed. Try again");
                    }

                } catch (Exception e) {
                    showToast("Server Response Failed. Try again");
                    Log.e("AtteSMS:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Service Not Available");
                Log.d("AtteSMS:Failure", t.toString());
            }
        });
    }


    private void sendAttenAPIPresent() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceScreen.this);
        String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherAttendanceScreen.this);

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
                Log.d("AtteSMS:Failure", t.toString());
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

                backToResultActvity("SENT");
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

    private void  backToResultActvity(String msg) {
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

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < listschooldetails.size(); i++) {


            }

            jsonObjectSchool.add("StudentID", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
//
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
                sendAttenAPIPresent();
            }
        });


        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }
}