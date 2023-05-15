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

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherStudendListAdapter;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.aws.S3Uploader;
import com.vs.schoolmessenger.aws.S3Utils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStudentListener;
import com.vs.schoolmessenger.model.SubjectDetails;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.activity.SubjectListScreen.SubjectDetailsList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;


public class TeacherAttendanceStudentList extends AppCompatActivity implements TeacherOnCheckStudentListener {

    RecyclerView rvStudentList;
    private TeacherStudendListAdapter adapter;
    TextView tvOK, tvCancel, tvSelectedCount, tvTotalCount, tvStdSec, tvSubject; //tvSelectUnselect
    private ArrayList<TeacherStudentsModel> studentList = new ArrayList<>();

    String schoolID, targetCode,stdcode,subcode;
    TeacherSectionModel selSection;

    private int i_students_count;
    CheckBox cbSelectAll;
    String schoolId, sectioncode,filepath,duration,Description,strmessage;
    int iRequestCode;
    String voicetype="",strPDFFilepath;
    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String fileNameDateTime;
    AmazonS3 s3Client;
    TransferUtility transferUtility;
    S3Uploader s3uploaderObj;

    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType="";
    String flag="";
    String uploadFilePath="";
    String SuccessFilePath="";
    int pathIndex=0;

    String[] UploadedURLStringArray;
    private  ArrayList<String> UploadedS3URlList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_attendance_student_list);
        schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceStudentList.this);
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));

        selSection = (TeacherSectionModel) getIntent().getSerializableExtra("STD_SEC");
        schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
        sectioncode = getIntent().getExtras().getString("SECCODE", "");
        stdcode = getIntent().getExtras().getString("STDCODE", "");
        subcode = getIntent().getExtras().getString("SUBCODE", "");
        filepath= getIntent().getExtras().getString("filepath", "");
        Description=getIntent().getExtras().getString("Description", "");
        duration=getIntent().getExtras().getString("duration", "");
        strmessage=getIntent().getExtras().getString("Message", "");

        voicetype=getIntent().getExtras().getString("VOICE", "");

        strPDFFilepath = getIntent().getExtras().getString("FILE_PATH_PDF", "");
        if(strPDFFilepath.equals("")){
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }

        targetCode = selSection.getStdSecCode();
        s3uploaderObj = new S3Uploader(TeacherAttendanceStudentList.this);
        rvStudentList = (RecyclerView) findViewById(R.id.attenStudent_rvStudentList);
        adapter = new TeacherStudendListAdapter(TeacherAttendanceStudentList.this, this, studentList,iRequestCode);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvStudentList.setHasFixedSize(true);
        rvStudentList.setLayoutManager(mLayoutManager);
        rvStudentList.setItemAnimator(new DefaultItemAnimator());
        rvStudentList.setAdapter(adapter);
        rvStudentList.getRecycledViewPool().setMaxRecycledViews(0, 80);

        tvSelectedCount = (TextView) findViewById(R.id.attenStudent_tvSelCount);
        tvTotalCount = (TextView) findViewById(R.id.attenStudent_tvTotCount);
        tvStdSec = (TextView) findViewById(R.id.attenStudent_tvStdSec);
        tvSubject = (TextView) findViewById(R.id.attenStudent_tvSubject);

        tvStdSec.setText(selSection.getStandard() + "-" + selSection.getSection());
        tvSubject.setText(selSection.getSubject());
        tvSelectedCount.setText(selSection.getSelectedStudentsCount());

        tvOK = (TextView) findViewById(R.id.attenStudent_tvOk);
        tvCancel = (TextView) findViewById(R.id.attenStudent_tvCancel);
        tvOK.setEnabled(false);
        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (studentList.size()>0) {
                    showAttendanceConfirmationAlert();
                }
                else {
                    showToast(getResources().getString(R.string.no_students));
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        cbSelectAll = (CheckBox) findViewById(R.id.attenStudent_cbSelectAll);

        if(iRequestCode == PRINCIPAL_ATTENDANCE || iRequestCode == STAFF_ATTENDANCE ){
            cbSelectAll.setText("Absentees All");
        }

        cbSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CompoundButton) view).isChecked()){

                    for (int i = 0; i < studentList.size(); i++) {
                        studentList.get(i).setSelectStatus(true);

                    }
                    adapter.notifyDataSetChanged();
                    i_students_count = studentList.size();
                    enableDisableNext();
                }
                else {
                    System.out.println("Un-Checked");
                    for (int i = 0; i < studentList.size(); i++) {
                        studentList.get(i).setSelectStatus(false);

                    }
                    adapter.notifyDataSetChanged();
                    i_students_count = 0;
                    enableDisableNext();
                }
            }
        });
        studentListAPI();
    }


    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


    private void studentListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherAttendanceStudentList.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherAttendanceStudentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetStudDetailForSection(jsonReqArray);
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
                        String strStudName = jsonObject.getString("StudentName");
                        String strStudID = jsonObject.getString("StudentID");

                        if (strStudID.equals("0")) {

                            showSettingsAlert1(strStudName);

                            cbSelectAll.setEnabled(false);
                        } else {
                            TeacherStudentsModel studentsModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                studentsModel = new TeacherStudentsModel(jsonObject.getString("StudentID"), jsonObject.getString("StudentName")
                                        , jsonObject.getString("StudentAdmissionNo"), false);
                                studentList.add(studentsModel);
                            }
                            selSection.setTotStudents(String.valueOf(0));//String.valueOf(studentList.size()));
                            tvTotalCount.setText(String.valueOf(studentList.size()));
                            cbSelectAll.setChecked(false);
                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("StudentsList:Excep", e.getMessage());
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

    private void showSettingsAlert1(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherAttendanceStudentList.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    @Override
    public void student_addClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count++;
            enableDisableNext();
        }
    }

    @Override
    public void student_removeClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count--;
            cbSelectAll.setChecked(false);
            enableDisableNext();
        }
    }

    private void enableDisableNext() {
        tvSelectedCount.setText(i_students_count + "");
        if (i_students_count > 0)
            tvOK.setEnabled(true);
        else tvOK.setEnabled(false);
        if(studentList.size()==i_students_count) {
            cbSelectAll.setChecked(true);
        }
    }
    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void showAttendanceConfirmationAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TeacherAttendanceStudentList.this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(getResources().getString(R.string.confirmation));
        if((iRequestCode == PRINCIPAL_ATTENDANCE) || (iRequestCode== STAFF_ATTENDANCE)) {
            alertDialog.setMessage(getStudentsName());
        }

        else if(iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM){
            alertDialog.setMessage(getResources().getString(R.string.exam_will_be));
            }

            else{
            alertDialog.setMessage(getStudentsName1());
        }

        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (iRequestCode == STAFF_VOICE) {
                    if(voicetype.equals("VoiceHistory")){
                      sendVoiceFromVoivehistory();
                    }
                    else {
                        SendVoiceToEntireSection();
                    }

                }
                if(iRequestCode == STAFF_TEXT){
                    SendSMSToEntireSection();
                }
                if(iRequestCode == STAFF_PHOTOS){

                    if(!strPDFFilepath.equals("")){
                        contentType="application/pdf";
                        slectedImagePath.clear();
                        slectedImagePath.add(strPDFFilepath);
                        UploadedS3URlList.clear();
                        uploadFileToAWSs3(pathIndex,".pdf");
                    }
                    else {
                        contentType="image/png";
                        UploadedS3URlList.clear();
                        uploadFileToAWSs3(pathIndex,"IMG");
                    }

                }
                if((iRequestCode == PRINCIPAL_ATTENDANCE) || (iRequestCode== STAFF_ATTENDANCE)){
                    sendAttenAPIAbsent();
                        }
                if((iRequestCode == PRINCIPAL_EXAM_TEST) || (iRequestCode== STAFF_TEXT_EXAM)){
                    SendExamstud();
                }
            }
        });

        alertDialog.show();
    }

    private void uploadFileToAWSs3(int pathind, final String fileType) {

        pathIndex=pathind;
        progressDialog = new ProgressDialog(TeacherAttendanceStudentList.this);
        for (int index = pathIndex; index < slectedImagePath.size(); index++) {
            uploadFilePath = slectedImagePath.get(index);
            break;
        }

        if(UploadedS3URlList.size()<slectedImagePath.size()) {
            if (uploadFilePath != null) {
                showLoading();
                fileNameDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                fileNameDateTime="File_"+fileNameDateTime;
                s3uploaderObj.initUpload(uploadFilePath, contentType,fileNameDateTime);
                s3uploaderObj.setOns3UploadDone(new S3Uploader.S3UploadInterface() {
                    @Override
                    public void onUploadSuccess(String response) {
                        if (response.equalsIgnoreCase("Success")) {
                            urlFromS3 = S3Utils.generates3ShareUrl(getApplicationContext(), uploadFilePath,fileNameDateTime);
                            if (!TextUtils.isEmpty(urlFromS3)) {
                                UploadedS3URlList.add(urlFromS3);
                                uploadFileToAWSs3(pathIndex + 1,fileType);

                                if (slectedImagePath.size() == UploadedS3URlList.size()) {
                                    SendMultipleImagePDFAsStaffToSpecificStudentsWithCloudURL(fileType);
                                }

                            }
                        }
                    }

                    @Override
                    public void onUploadError(String response) {
                        hideLoading();
                        Log.d("error", "Error Uploading");
                    }
                });


            }

        }

    }

    private void SendMultipleImagePDFAsStaffToSpecificStudentsWithCloudURL(String fileType) {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = SendStudentJson(fileType);
        Call<JsonArray> call = apiService. SendMultipleImagePDFAsStaffToSpecificStudentsWithCloudURL(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                hideLoading();

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
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                hideLoading();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject SendStudentJson(String fileType) {

        String isMultiple="";
        if(fileType.equals(".pdf")){
            isMultiple="0";
        }
        else {
            isMultiple="1";
        }
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("TargetCode", targetCode);
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", fileType);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);

                }
            }
                jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
                JsonArray jsonArrayschoolstd1 = new JsonArray();
                for (int j = 0; j < UploadedS3URlList.size(); j++) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("FileName", UploadedS3URlList.get(j));
                    jsonArrayschoolstd1.add(jsonObjectclass);

                }
                jsonObjectSchoolstdgrp.add("FileNameArray", jsonArrayschoolstd1);
                Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;

    }

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showLoading() {
        {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("loading..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
    }


    private void sendVoiceFromVoivehistory() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = jsonArrayFromVoiceHistory();
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherAttendanceStudentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoicetoSpecificStudentsfromVoiceHistory(jsonReqArray);
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
                            }
                            else{
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

    private JsonObject jsonArrayFromVoiceHistory() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            jsonObjectSchoolstdgrp.addProperty("TargetCode", targetCode);
            jsonObjectSchoolstdgrp.addProperty("filepath", filepath);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);

                }

                Log.d("TTgroup", "1");
                jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
                Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

            }
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;

    }

    private void SendVoiceToEntireSection() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        Log.d("FILE_Path", filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);


        JsonObject jsonReqArray = constructJsonArrayVoice();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherAttendanceStudentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceAsStaffToSpecificStudents(requestBody, bodyFile);
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
                            }
                            else{
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
                else {
                    Log.d("Response Body:", response.body()+"Response Error Body "+response.errorBody() );
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getStackTrace() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherAttendanceStudentList.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
              finish();
            }
        }
    }
    private JsonObject constructJsonArrayVoice() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            jsonObjectSchoolstdgrp.addProperty("TargetCode", targetCode);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);

                }

                jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
                Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

            }
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;

    }

    private void SendSMSToEntireSection() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherAttendanceStudentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArraySMS();
        Call<JsonArray> call = apiService.SendSMSAsStaffToSpecificStudents(jsonReqArray);
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
                            }
                            else{
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
                showToast(t.toString());
            }
        });
    }


    private JsonObject constructJsonArraySMS() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);
            jsonObjectSchoolstdgrp.addProperty("Description", Description);
            jsonObjectSchoolstdgrp.addProperty("TargetCode", targetCode);

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
                jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
                Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

            }
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }



    private String getStudentsName() {
        StringBuilder strStudList = new StringBuilder();
            strStudList.append("Below mentioned Student(s) will be marked as absent\n");

        try {
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus())
                    strStudList.append("\n" + studentList.get(i).getStudentName());
            }
            Log.d("Final_List", strStudList.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return strStudList.toString();
    }

    private String getStudentsName1() {
        StringBuilder strStudList = new StringBuilder();
            strStudList.append("Message will be sent to the below listed student(s) \n");

        try {
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus())
                    strStudList.append("\n" + studentList.get(i).getStudentName());

            }
            Log.d("Final_List", strStudList.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return strStudList.toString();
    }

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchool.addProperty("TargetCode", sectioncode);
            jsonObjectSchool.addProperty("StaffID", Principal_staffId);

            Log.d("schoolid", schoolID);
            Log.d("staffstd&sec", sectioncode);
            Log.d("Studetlist:req", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void sendAttenAPIAbsent() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherAttendanceStudentList.this);
        String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherAttendanceStudentList.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayAbsent();
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
                    showToast(e.toString());
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

    private JsonObject constructJsonArrayAbsent() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", Principal_staffId);
            jsonObjectSchool.addProperty("ClassId", stdcode);
            jsonObjectSchool.addProperty("SectionID", sectioncode);
            jsonObjectSchool.addProperty("AllPresent", "F");

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if(studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
                }
            jsonObjectSchool.add("StudentID", jsonArrayschoolstd);
            Log.d("Final_Array", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void SendExamstud() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherAttendanceStudentList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherAttendanceStudentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstextExam();
        Call<JsonArray> call = apiService.InsertExamToSpecificStudents_WithSubjectSyllabus(jsonReqArray);
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
                            }
                            else{
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
                showToast(t.toString());
            }
        });
    }
    private JsonObject constructJsonArrayMgtSchoolstextExam() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("SchoolID", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("StaffID", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("SectionCode", sectioncode);
            jsonObjectSchoolstdgrp.addProperty("ExamName", strmessage);
            jsonObjectSchoolstdgrp.addProperty("ExamSyllabus", Description);
            JsonArray subjects = new JsonArray();
            for (int i = 0; i < SubjectDetailsList.size(); i++) {
                SubjectDetails sectionsListNEW = SubjectDetailsList.get(i);
                JsonObject jsonObjectclass1 = new JsonObject();
                jsonObjectclass1.addProperty("Subcode", sectionsListNEW.getStrSubCode());
                jsonObjectclass1.addProperty("ExamDate", sectionsListNEW.getDate());
                jsonObjectclass1.addProperty("Session", sectionsListNEW.getSession());
                jsonObjectclass1.addProperty("MaxMark", sectionsListNEW.getMaxMark());
                jsonObjectclass1.addProperty("Syllabus", sectionsListNEW.getSubjectSyllabus());
                subjects.add(jsonObjectclass1);

            }

            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("StudentID", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
                jsonObjectSchoolstdgrp.add("Subjects", subjects);

                jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
                Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());
            }
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
}
