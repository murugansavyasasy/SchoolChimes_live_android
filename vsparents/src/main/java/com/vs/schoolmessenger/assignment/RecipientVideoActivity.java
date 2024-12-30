package com.vs.schoolmessenger.assignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.activity.TeacherEmergencyVoice;
import com.vs.schoolmessenger.activity.TeacherStaffStandardSection;
import com.vs.schoolmessenger.activity.TeacherStandardsAndGroupsList;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.adapter.TeacherNewSectionsListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSectionListListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.VimeoUploader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_IMAGEASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PDFASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICEASSIGNMENT;

public class RecipientVideoActivity extends AppCompatActivity implements VimeoUploader.UploadCompletionListener {
    Spinner spinStandard, spinSection, spinSubject;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    ArrayAdapter<String> adaSubject;
    Button btnSend, btnSelectStudents;
    LinearLayout llSubject;

    List<String> listStd = new ArrayList<>();
    List<String> listStd1 = new ArrayList<>();
    List<String> listSection;
    List<String> listSectionID;
    List<TeacherSectionsListNEW> arrSectionCollections;
    String[] itemstdId;

    ImageView genTextPopup_ToolBarIvBack;
    private ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();
    String strStdName, strSecName, strSecCode, strSubjectCode = "", strSubjectName, strTotalStudents, strDate = "";
    TeacherSectionModel stdSec;

    private int iRequestCode = 0;
    String strToWhom = "";
    String SchoolID, loginType, StaffID, strtittle, strmessage, Description, duration, filepath;
    //
    RecyclerView rvSectionList;
    private ArrayList<TeacherSectionsListNEW> seletedSectionsList = new ArrayList<>();
    private int i_sections_count = 0;
    List<String> listSubjectName = new ArrayList<>();
    List<String> listSubjectCode = new ArrayList<>();
    List<TeacherSubjectModel> arrSubjectCollections = new ArrayList<>();
    List<String> listTotalStudentsInSec = new ArrayList<>();

    Button btnSelectSubjects;

    ArrayList<TeacherSubjectModel> listSubjects1 = new ArrayList<TeacherSubjectModel>();
    String voicetype = "", strPDFFilepath, strVideoFilePath, VideoDescription;

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    ArrayList<TeacherSubjectModel> SubjectsList = new ArrayList<TeacherSubjectModel>();

    List<TeacherSectionsListNEW> sectionsCheckbox;
    Button btnGetSubject;
    TextView lblSubject;
    String sectionsTargetCode = "", upload_link, strsize, iframe, link;
    String AssignId = "";
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_recipient_assignment);
        btnGetSubject = (Button) findViewById(R.id.btnGetSubject);
        btnSelectStudents = (Button) findViewById(R.id.staffStdSecSub_btnToStudents);
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));

        strToWhom = getIntent().getExtras().getString("SEC", "");
        strtittle = getIntent().getExtras().getString("TITLE", "");
        strmessage = getIntent().getExtras().getString("CONTENT", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        AssignId = getIntent().getExtras().getString("ID", "");
        strsize = getIntent().getExtras().getString("FILE_SIZE", "");

        Log.d("filesec", filepath);
        if (strToWhom.equals("1")) {
            btnSelectStudents.setVisibility(View.GONE);
        } else {
            btnSelectStudents.setVisibility(View.VISIBLE);
        }

        btnGetSubject.setVisibility(View.GONE);

        if (iRequestCode == STAFF_IMAGEASSIGNMENT) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }
        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "", "", strTotalStudents, "0", false);
                Intent inStud = new Intent(RecipientVideoActivity.this, StudentSelectVideo.class);
                inStud.putExtra("STD_SEC", stdSec);
                inStud.putExtra("REQUEST_CODE", iRequestCode);
                inStud.putExtra("ID", AssignId);
                inStud.putExtra("SECCODE", strSecCode);
                inStud.putExtra("SUBCODE", strSubjectCode);

                inStud.putExtra("PATH_LIST", slectedImagePath);
                inStud.putExtra("TITLE", strtittle);
                inStud.putExtra("CONTENT", strmessage);
                inStud.putExtra("FILEPATH", filepath);
                inStud.putExtra("FILE_SIZE", strsize);
                inStud.putExtra("DATE", strDate);

                startActivityForResult(inStud, iRequestCode);

            }
        });

        btnSend = (Button) findViewById(R.id.staffStdSecSub_btnSend);
        btnSelectSubjects = (Button) findViewById(R.id.btnSelectSubjects);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert("Do you want to send video ?");

            }
        });


        btnSend.setEnabled(false);

        btnSelectSubjects.setEnabled(true);
        rvSectionList = (RecyclerView) findViewById(R.id.staffStdSecSub_rvSectionList);
        spinStandard = (Spinner) findViewById(R.id.staffStdSecSub_spinStandard);
        spinSection = (Spinner) findViewById(R.id.staffStdSecSub_spinSection);
        spinSubject = (Spinner) findViewById(R.id.staffStdSecSub_spinSubject);

        lblSubject = (TextView) findViewById(R.id.staffStdSecSub_tv4);
        btnGetSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seletedSectionsList.size() > 0) {
                    getSubjectsApi();
                } else {
                    showToast(getResources().getString(R.string.selecte_atleast_one_section));
                }
            }
        });

        if (strToWhom.equals("1")) {
            rvSectionList.setVisibility(View.VISIBLE);
            spinSection.setVisibility(View.GONE);

        } else {
            rvSectionList.setVisibility(View.GONE);
            spinSection.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
            btnSelectSubjects.setEnabled(true);
        }

        standardsAndSectoinsListAPI1();


        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();

                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());


                if (strToWhom.equals("1")) {
                    strStdName = listStd.get(position);


                    listSubjects1.clear();
                    seletedSectionsList.clear();
                    listSectionsForSelectedStandard();

                } else {
                    listSubjects1.clear();

                    listSection = new ArrayList<>();
                    listSectionID = new ArrayList<>();
                    listTotalStudentsInSec = new ArrayList<>();
                    strStdName = listStd.get(position);


                    arrSubjectCollections = new ArrayList<>();
                    for (int i = 0; i < arrSubjectCollections.size(); i++) {

                        TeacherSubjectModel subjectList;
                        subjectList = new TeacherSubjectModel(arrSubjectCollections.get(i).getStrSubName(), arrSubjectCollections.get(i).getStrSubCode(), false);
                        listSubjects1.add(subjectList);
                    }

                    for (int i = 0; i < arrSectionCollections.size(); i++) {
                        listSection.add(arrSectionCollections.get(i).getStrSectionName());
                        listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                        listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());

                    }

                    adaSection = new ArrayAdapter<>(RecipientVideoActivity.this, R.layout.teacher_spin_title, listSection);
                    adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                    spinSection.setAdapter(adaSection);

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
                strTotalStudents = listTotalStudentsInSec.get(position);

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

    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipientVideoActivity.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //   VimeoAPi();
                uploadVimeoVideo();
            }
        });
        alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }

    private void uploadVimeoVideo() {
        String authToken = TeacherUtil_SharedPreference.getVideotoken(RecipientVideoActivity.this);
        VimeoUploader.uploadVideo(RecipientVideoActivity.this, strtittle, strmessage, authToken, filepath, this);
    }

    @Override
    public void onUploadComplete(boolean success, String isIframe, String isLink) {
        Log.d("Vime_Video_upload", String.valueOf(success));
        Log.d("VimeoIframe", isIframe);
        Log.d("link", isLink);

        if (success) {
            iframe = isIframe;
            link = isLink;
            SendVideotoSecApi();
        } else {
            showAlertfinal("Video sending failed.Retry", "0");
        }
    }


    private void getSubjectsApi() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(RecipientVideoActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(RecipientVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(RecipientVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();


        String id = "";
        for (int i = 0; i < seletedSectionsList.size(); i++) {
            sectionsTargetCode = id + seletedSectionsList.get(i).getStrSectionCode() + "~";
            id = sectionsTargetCode;
            Log.d("values", sectionsTargetCode);
        }
        String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);


        jsonObject.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        jsonObject.addProperty("StaffId", TeacherUtil_Common.Principal_staffId);
        jsonObject.addProperty("TargetCode", targetCode);

        Log.d("Request", String.valueOf(jsonObject));

        Call<JsonArray> call = apiService.GetSubjects(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    SubjectsList.clear();
                    listSubjectName.clear();
                    listSubjectCode.clear();
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());


                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {
                            spinSubject.setVisibility(View.VISIBLE);
                            lblSubject.setVisibility(View.VISIBLE);
                            Log.d("entersubject", "entersubjects");
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String SubjectID = jsonObject.getString("SubjectID");
                                String SubjectName = jsonObject.getString("SubjectName");
                                TeacherSubjectModel subjectList;
                                subjectList = new TeacherSubjectModel(SubjectName, SubjectID, false);
                                SubjectsList.add(subjectList);

                                listSubjectName.add(SubjectName);
                                listSubjectCode.add(SubjectID);
                            }
                            adaSubject = new ArrayAdapter<>(RecipientVideoActivity.this, R.layout.teacher_spin_title, listSubjectName);
                            adaSubject.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            spinSubject.setAdapter(adaSubject);

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void standardsAndSectoinsListAPI1() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(RecipientVideoActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(RecipientVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(RecipientVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStdHW();
        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        {
                            TeacherStandardSectionsListModel stdSecList;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                if (jsonObject.getString("StandardId").equals("0")) {
                                    showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                    finish();
                                } else {
                                    stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));
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
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"), "", false);
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
                                            listSubjects.add(subjectList);

                                        }

                                    }

                                    stdSecList.setListSectionsNew(listSections);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }

                                adaStd = new ArrayAdapter<>(RecipientVideoActivity.this, R.layout.teacher_spin_title, listStd);
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
                    showToast(getResources().getString(R.string.no_records));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("StdSecList:Failure", t.toString());
                onBackPressed();
            }
        });
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
                backToResultActvity(message);
            }
        }
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }


    private JsonObject constructJsonArrayMgtSchoolStdHW() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", TeacherUtil_Common.Principal_staffId);

            jsonObjectSchool.addProperty("isAttendance", "0");

            Log.d("schoolid", TeacherUtil_Common.Principal_SchoolId);
            Log.d("staffstd&sec", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipientVideoActivity.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(RecipientVideoActivity.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
                    dialog.cancel();
                }


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }


    private void listSectionsForSelectedStandard() {
        i_sections_count = 0;

        for (int i = 0; i < arrSectionCollections.size(); i++) {
            arrSectionCollections.get(i).setSelectStatus(false);
        }


        TeacherNewSectionsListAdapter newSectionsListAdapter = new TeacherNewSectionsListAdapter(RecipientVideoActivity.this, new TeacherOnCheckSectionListListener() {
            @Override
            public void section_addSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (!seletedSectionsList.contains(sectionsListNEW))) {


                    seletedSectionsList.add(sectionsListNEW);
                    i_sections_count++;
                    enableDisableNext();

                    if (i_sections_count == 1) {

                    } else {

                    }
                }
            }

            @Override
            public void section_removeSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (seletedSectionsList.contains(sectionsListNEW))) {
                    seletedSectionsList.remove(sectionsListNEW);
                    i_sections_count--;
                    if (i_sections_count == 1) {

                    } else {

                    }
                    enableDisableNext();
                }
            }
        }, arrSectionCollections);

        rvSectionList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecipientVideoActivity.this);
        rvSectionList.setLayoutManager(layoutManager);
        rvSectionList.addItemDecoration(new DividerItemDecoration(RecipientVideoActivity.this, LinearLayoutManager.VERTICAL));
        rvSectionList.setItemAnimator(new DefaultItemAnimator());
        rvSectionList.setAdapter(newSectionsListAdapter);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void enableDisableNext() {
        if (i_sections_count > 0) {
            btnSend.setEnabled(true);
            btnSelectSubjects.setEnabled(true);
        } else {
            btnSend.setEnabled(false);
            btnSelectSubjects.setEnabled(false);
        }
    }
    private JsonObject JsonVideotoSection() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("Title", strtittle);
            jsonObjectSchoolstdgrp.addProperty("videoFileSize", Util_Common.isVideoSize);
            jsonObjectSchoolstdgrp.addProperty("Description", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Iframe", iframe);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolstdgrp.addProperty("URL", link);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();
            Log.d("sizelist", String.valueOf(seletedSectionsList.size()));

            if (seletedSectionsList.size() == 0) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("TargetCode", strSecCode);
                Log.d("strSecCode", strSecCode);
                jsonArrayschoolstd.add(jsonObjectclass);
            } else {
                for (int i = 0; i < seletedSectionsList.size(); i++) {
                    if (seletedSectionsList.get(i).isSelectStatus()) {
                        JsonObject jsonObjectclass = new JsonObject();
                        jsonObjectclass.addProperty("TargetCode", seletedSectionsList.get(i).getStrSectionCode());
                        Log.d("schoolid", seletedSectionsList.get(i).getStrSectionCode());
                        jsonArrayschoolstd.add(jsonObjectclass);
                    }
                }
            }
            jsonObjectSchoolstdgrp.add("Seccode", jsonArrayschoolstd);
            Log.d("reqVideo", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    void SendVideotoSecApi() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(RecipientVideoActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


//        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientVideoActivity.this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Uploading...");
//        mProgressDialog.setCancelable(false);
        //  mProgressDialog.show();
        //if (!this.isFinishing())

        JsonObject jsonReqArray = JsonVideotoSection();
        Call<JsonArray> call = apiService.SendVideoAsStaffToEntireSection(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("result");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlertfinal(strMsg, strStatus);

                            } else {
                                showAlertfinal(strMsg, strStatus);
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
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }


    private void VimeoAPi() {


        OkHttpClient client1;
        client1 = new OkHttpClient.Builder().connectTimeout(300, TimeUnit.SECONDS).readTimeout(5, TimeUnit.MINUTES).writeTimeout(5, TimeUnit.MINUTES).build();

        Retrofit retrofit = new Retrofit.Builder().client(client1).baseUrl("https://api.vimeo.com/").addConverterFactory(GsonConverterFactory.create()).build();
        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientVideoActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);


        JsonObject object = new JsonObject();

        JsonObject jsonObjectclasssec = new JsonObject();
        jsonObjectclasssec.addProperty("approach", "post");
        jsonObjectclasssec.addProperty("size", String.valueOf(strsize));


        JsonObject jsonprivacy = new JsonObject();
        jsonprivacy.addProperty("view", "unlisted");

        JsonObject jsonshare = new JsonObject();
        jsonshare.addProperty("share", "false");

        JsonObject jsonembed = new JsonObject();
        jsonembed.add("buttons", jsonshare);

        object.add("upload", jsonObjectclasssec);
        object.addProperty("name", strtittle);
        object.addProperty("description", strmessage);
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);

        String head = "Bearer " + TeacherUtil_SharedPreference.getVideotoken(RecipientVideoActivity.this);
        Call<JsonObject> call = service.VideoUpload(object, head);
        Log.d("jsonOBJECT", object.toString());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try", "testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        Log.d("Response sucess", object1.toString());


                        JSONObject obj = object1.getJSONObject("upload");
                        JSONObject obj1 = object1.getJSONObject("embed");
                        upload_link = obj.getString("upload_link");
                        link = object1.getString("link");
                        iframe = obj1.getString("html");
                        Log.d("isUploadLink", upload_link);
                        Log.d("iframe", iframe);


                        String[] separated = upload_link.split("\\?(?!\\?)");

                        String name = separated[0];
                        Log.d("name", name);

                        String FileName = separated[1];
                        Log.d("FileName", FileName);

                        String upload = name.replace("upload", "");
                        Log.d("replace", upload);

                        try {
                            VIDEOUPLOAD(upload_link);
                        } catch (Exception e) {
                            Log.e("VIMEO Exception", e.getMessage());
                            showAlertfinal("Video sending failed.Retry", "0");

                        }


                    } catch (Exception e) {
                        Log.e("VIMEO Exception", e.getMessage());
                        showAlertfinal(e.getMessage(), "0");

                    }

                } else {

                    Log.d("Response fail", "fail");
                    showAlertfinal("Video sending failed.Retry", "0");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                showAlertfinal("Video sending failed.Retry", "0");

            }


        });
    }


    private void VIDEOUPLOAD(String upload_link) {

        String[] separated = upload_link.split("\\?(?!\\?)");
        String name = separated[0];  //"/"
        Log.d("name2", name);
        String FileName = separated[1];
        Log.d("FileName", FileName);
        String upload = name.replace("upload", "");
        Log.d("replace234", upload);

        String[] id = FileName.split("&");

        ticket_id = id[0];
        Log.d("ticket_id", ticket_id);
        video_file_id = id[1];
        Log.d("video_file_id", video_file_id);
        signature = id[2];
        Log.d("signature", signature);
        v6 = id[3];
        Log.d("v6", v6);
        redirect_url = id[4];
        Log.d("redirect_url", redirect_url);

        String[] seperate1 = ticket_id.split("=");

        String ticket = seperate1[0];  //"/"
        Log.d("sp1", ticket);

        String ticket2 = seperate1[1];
        Log.d("ticket2", ticket2);


        String[] seperate2 = video_file_id.split("=");

        String ticket1 = seperate2[0];  //"/"
        Log.d("sp2", ticket1);

        String ticket3 = seperate2[1];
        Log.d("tic", ticket3);

        String[] seper = signature.split("=");

        String ticke = seper[0];  //"/"
        Log.d("sp3", ticke);

        String tick = seper[1];
        Log.d("signature_number", tick);

        String[] sepera = v6.split("=");

        String str = sepera[0];  //"/"
        Log.d("str", str);

        String str1 = sepera[1];
        Log.d("v6123", str1);

        String[] sucess = redirect_url.split("=");

        String urlRIDERCT = sucess[0];  //"/"
        Log.d("urlRIDERCT", urlRIDERCT);

        String redirect_url123 = sucess[1];
        Log.d("redirect_url123", redirect_url123);

        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()

                .connectTimeout(600, TimeUnit.SECONDS).readTimeout(40, TimeUnit.MINUTES).writeTimeout(40, TimeUnit.MINUTES).build();

        Retrofit retrofit = new Retrofit.Builder().client(client1).baseUrl(upload).addConverterFactory(GsonConverterFactory.create()).build();
        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientVideoActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);
        RequestBody requestFile = null;

        try {
            InputStream in = new FileInputStream(filepath);

            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            requestFile = RequestBody.create(MediaType.parse("application/offset+octet-stream"), buf);
        } catch (IOException e) {
            e.printStackTrace();
            showAlertfinal(e.getMessage(), "0");
        }

        Call<ResponseBody> call = service.patchVimeoVideoMetaData(ticket2, ticket3, tick, str1, redirect_url123 + "www.voicesnapforschools.com", requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        SendVideotoSecApi();
                    } else {
                        showAlertfinal("Video sending failed.Retry", "0");
                    }
                } catch (Exception e) {
                    showAlertfinal(e.getMessage(), "0");


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showAlertfinal("Video sending failed.Retry", "0");
            }
        });


    }

    private void showAlertfinal(String msg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipientVideoActivity.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(RecipientVideoActivity.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
                    dialog.dismiss();
                }

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }

}

