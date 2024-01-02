package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.adapter.TeacherStudendListAdapter;
import com.vs.schoolmessenger.aws.S3Uploader;
import com.vs.schoolmessenger.aws.S3Utils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStudentListener;
import com.vs.schoolmessenger.model.SubjectDetails;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
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
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_IMAGEASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PDFASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICEASSIGNMENT;

public class StudentSelectAssignment extends AppCompatActivity implements TeacherOnCheckStudentListener {
    RecyclerView rvStudentList;
    private TeacherStudendListAdapter adapter;
    TextView tvOK, tvCancel, tvSelectedCount, tvTotalCount, tvStdSec, tvSubject; //tvSelectUnselect
    private ArrayList<TeacherStudentsModel> studentList = new ArrayList<>();
//    private ArrayList<TeacherStudentsModel> selectedstudentList = new ArrayList<>();

    String schoolID, targetCode,stdcode,subcode,strtittle;
    TeacherSectionModel selSection;

    private int i_students_count;

    CheckBox cbSelectAll;
    String schoolId, sectioncode,filepath,duration,Description,strmessage;
    int iRequestCode;

    String voicetype="",strPDFFilepath;

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String sectionsTargetCode="";
    String AssignId="",strDate="";

    String fileNameDateTime;

    AmazonS3 s3Client;
    TransferUtility transferUtility;
    S3Uploader s3uploaderObj;

    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType="";

    String uploadFilePath="";
    int pathIndex=0;
    String Category = "";


    private  ArrayList<String> UploadedS3URlList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_student_assignment);


        schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(StudentSelectAssignment.this);
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("REQUEST_CODE", String.valueOf(iRequestCode));

        selSection = (TeacherSectionModel) getIntent().getSerializableExtra("STD_SEC");
        sectioncode = getIntent().getExtras().getString("SECCODE", "");
        subcode = getIntent().getExtras().getString("SUBCODE", "");
        filepath= getIntent().getExtras().getString("FILEPATH", "");
        strtittle=getIntent().getExtras().getString("TITLE", "");
        strmessage=getIntent().getExtras().getString("CONTENT", "");
        AssignId = getIntent().getExtras().getString("ID", "");
        strDate = getIntent().getExtras().getString("DATE", "");
        Category = getIntent().getExtras().getString("category", "");

        if(iRequestCode==STAFF_IMAGEASSIGNMENT){
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }

        targetCode = sectioncode;
        rvStudentList = (RecyclerView) findViewById(R.id.attenStudent_rvStudentList);
        adapter = new TeacherStudendListAdapter(StudentSelectAssignment.this, this, studentList,0);

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
        s3uploaderObj = new S3Uploader(StudentSelectAssignment.this);
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
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < studentList.size(); i++) {
                    studentList.get(i).setSelectStatus(isChecked);
                }

                adapter.notifyDataSetChanged();

                if (isChecked) {
                    i_students_count = studentList.size();
                    Log.d("CHECK-i_students_count", "True - " + i_students_count);
                } else {
                    i_students_count = 0;
                    Log.d("CHECK-i_students_count", "False - " + i_students_count);
                }

                enableDisableNext();
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

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StudentSelectAssignment.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StudentSelectAssignment.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
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
                                        , jsonObject.getString("StudentAdmissionNo"),jsonObject.getString("RollNO"), false);
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
                Log.d("SubjectHandling:Failure", t.toString());
            }
        });
    }

    private void showSettingsAlert1(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSelectAssignment.this);


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
            enableDisableNext();

        }
    }

    private void enableDisableNext() {

        tvSelectedCount.setText(i_students_count + "");

        if (i_students_count > 0)
            tvOK.setEnabled(true);
        else tvOK.setEnabled(false);
    }

    public String getSelectedStudentsName() {
        StringBuilder strStudList = new StringBuilder();
        try {
            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus())
                    strStudList.append(studentList.get(i).getStudentID()+"~");
            }
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return strStudList.toString();
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void showAttendanceConfirmationAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                StudentSelectAssignment.this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to send the assignment");
        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if(!AssignId.equals("")){
                    ForwardApi(AssignId);
                }

                if (iRequestCode == STAFF_TEXTASSIGNMENT) {
                    SendMessageTOSectionApi();
                }
                if (iRequestCode == STAFF_VOICEASSIGNMENT) {
                    SendVoiceTOSectionApi();
                }
                if (iRequestCode == STAFF_IMAGEASSIGNMENT) {
                    contentType="image/png";
                    UploadedS3URlList.clear();
                    uploadFileToAWSs3(pathIndex,"IMAGE");
                }
                if (iRequestCode == STAFF_PDFASSIGNMENT) {

                    contentType="application/pdf";
                    slectedImagePath.clear();
                    slectedImagePath.add(filepath);
                    UploadedS3URlList.clear();
                    uploadFileToAWSs3(pathIndex,"PDF");
                }

            }
        });

        alertDialog.show();
    }

    private void uploadFileToAWSs3(int pathind, final String fileType) {

        pathIndex=pathind;
        progressDialog = new ProgressDialog(StudentSelectAssignment.this);
        for (int index = pathIndex; index < slectedImagePath.size(); index++) {
            uploadFilePath = slectedImagePath.get(index);
            break;
        }

        if(UploadedS3URlList.size()<slectedImagePath.size()) {
            Log.d("upload file", uploadFilePath);
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
                                    ManageAssignmentFromAppWithCloudURL(fileType);
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


        else {
            // Toast.makeText(this, "Null Path", Toast.LENGTH_SHORT).show();
        }



    }

    private void ManageAssignmentFromAppWithCloudURL(String fileType) {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = manageAssignmentJson(fileType);
        Call<JsonArray> call = apiService. ManageAssignmentFromAppWithCloudURL(jsonReqArray);
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

                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject manageAssignmentJson(String fileType) {
        String isMultiple="";
        if(fileType.equals("PDF")){
            isMultiple="0";
        }
        else {
            isMultiple="1";
        }

        Log.d("UploadFileList",String.valueOf(UploadedS3URlList.size()));
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            sectionsTargetCode=getSelectedStudentsName();
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId", "0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType", fileType);
            jsonObjectSchoolstdgrp.addProperty("Title", strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId", targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", subcode);
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("processType", "add");
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", Category);


            JsonArray jsonArrayschoolstd = new JsonArray();
            for (int i = 0; i < UploadedS3URlList.size(); i++) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("FileName", UploadedS3URlList.get(i));
                jsonArrayschoolstd.add(jsonObjectclass);

            }
            jsonObjectSchoolstdgrp.add("FileNameArray", jsonArrayschoolstd);
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

    private void ForwardApi(String id) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectAssignment.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String secids = "";
//        for (int i = 0; i < selectedstudentList.size(); i++) {
//            sectionsTargetCode = secids+selectedstudentList.get(i).getStudentID()+ "~";
//            secids = sectionsTargetCode;
//            Log.d("values", sectionsTargetCode);
//        }
        sectionsTargetCode=getSelectedStudentsName();
        String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("FromAssignmentId", id);
        jsonObject.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        jsonObject.addProperty("receiverId",targetCode);
        jsonObject.addProperty("isentireSection", "0");
        jsonObject.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);

        Call<JsonArray> call = apiService.ForwardAssignment(jsonObject);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                mProgressDialog.dismiss();
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");


                        if ((strStatus.toLowerCase()).equals("1")) {
                            showAlert(strMsg,strStatus);
                        } else {
                            showAlert(strMsg,strStatus);
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }


                } catch (Exception e) {
                    showToast(getResources().getString(R.string.check_internet));
                    Log.d("Ex", e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }


    private void SendPdfTOSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        Log.d("FILE_Path", filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayPdf();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectAssignment.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.ManageAssignmentFromApp(requestBody, bodyFile);
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
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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

    private JsonObject constructJsonArrayPdf() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            String id = "";
//            for (int i = 0; i < selectedstudentList.size(); i++) {
//                sectionsTargetCode = id+selectedstudentList.get(i).getStudentID()+ "~";
//                id = sectionsTargetCode;
//                Log.d("values", sectionsTargetCode);
//            }
            sectionsTargetCode=getSelectedStudentsName();
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","PDF");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", subcode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            Log.d("pdf:req", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
    private void SendMessageTOSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        JsonObject jsonReqArray = constructJsonArrayMessageHW();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectAssignment.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.ManageAssignmentFromAppmessage(requestBody);
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
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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

    private JsonObject constructJsonArrayMessageHW() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            String id = "";
//            for (int i = 0; i < selectedstudentList.size(); i++) {
//                sectionsTargetCode = id+selectedstudentList.get(i).getStudentID()+ "~";
//                id = sectionsTargetCode;
//                Log.d("values", sectionsTargetCode);
//            }
            sectionsTargetCode=getSelectedStudentsName();
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","SMS");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", subcode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", Category);

            Log.d("message:req", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendVoiceTOSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        Log.d("FILE_Path", filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayVoiceHW();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectAssignment.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.ManageAssignmentFromApp(requestBody, bodyFile);
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
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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

    private JsonObject constructJsonArrayVoiceHW() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            String id = "";
//            for (int i = 0; i < selectedstudentList.size(); i++) {
//                sectionsTargetCode = id+selectedstudentList.get(i).getStudentID()+ "~";
//                id = sectionsTargetCode;
//                Log.d("values", sectionsTargetCode);
//            }
            sectionsTargetCode=getSelectedStudentsName();
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","VOICE");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", subcode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", Category);


            Log.d("voice:req", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendImageToSectionApi() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectAssignment.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[slectedImagePath.size()];
        for (int index = 0; index < slectedImagePath.size(); index++) {
            File file = new File(slectedImagePath.get(index));
            RequestBody surveyBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            surveyImagesParts[index] = MultipartBody.Part.createFormData("file", file.getName(), surveyBody);
        }

        JsonObject jsonReqArray = constructJsonArrayImage("1","IMG");
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectAssignment.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.ManageAssignmentFromAppImage(requestBody, surveyImagesParts);
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

                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }


    private JsonObject constructJsonArrayImage(String multiple,String filetype) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            String id = "";
//            for (int i = 0; i < selectedstudentList.size(); i++) {
//                sectionsTargetCode = id+selectedstudentList.get(i).getStudentID()+ "~";
//                id = sectionsTargetCode;
//                Log.d("values", sectionsTargetCode);
//            }
            sectionsTargetCode=getSelectedStudentsName();
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","IMAGE");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","1");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", subcode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);


            Log.d("image:req", jsonObjectSchoolstdgrp.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSelectAssignment.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(StudentSelectAssignment.this, Teacher_AA_Test.class);
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


}

