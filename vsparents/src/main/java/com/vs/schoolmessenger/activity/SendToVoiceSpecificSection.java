package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.ParentSubmitActivity;
import com.vs.schoolmessenger.assignment.RecipientAssignmentActivity;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.aws.S3Uploader;
import com.vs.schoolmessenger.aws.S3Utils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
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

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

public class SendToVoiceSpecificSection extends AppCompatActivity implements View.OnClickListener {


    Button SendToEntireSchool, SendToStansGroups, SendToSpecificSection;

    String SchoolID, StaffID, filepath, duration, tittle, strmessage, strdate, strtime, strfilepathimage;
    int iRequestCode = 0;

    ArrayList<TeacherClassGroupModel> listClasses, listGroups;

    String voicetype = "";
    String PRINCIPAL_IMAGE = "";

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String strPDFFilepath, strVideoFilePath, VideoDescription;


    String fileNameDateTime;

    AmazonS3 s3Client;
    TransferUtility transferUtility;
    S3Uploader s3uploaderObj;

    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType = "";

    String flag = "";
    String uploadFilePath = "";
    String SuccessFilePath = "";
    int pathIndex = 0;

    String[] UploadedURLStringArray;
    private ArrayList<String> UploadedS3URlList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.sens_specific_section);


        SendToEntireSchool = (Button) findViewById(R.id.SendToEntireSchool);
        SendToEntireSchool.setOnClickListener(this);
        SendToStansGroups = (Button) findViewById(R.id.SendToStansGroups);
        SendToStansGroups.setOnClickListener(this);
        SendToSpecificSection = (Button) findViewById(R.id.SendToSpecificSection);
        SendToSpecificSection.setOnClickListener(this);

        String countryID = TeacherUtil_SharedPreference.getCountryID(SendToVoiceSpecificSection.this);
        if(countryID.equals("11")){
            SendToStansGroups.setText("Send to Grade/Groups");
            SendToSpecificSection.setText("Send to Grade/sections");
        }

        s3uploaderObj = new S3Uploader(SendToVoiceSpecificSection.this);

        ImageView ivBack = (ImageView) findViewById(R.id.emergVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        duration = getIntent().getExtras().getString("DURATION", "");
        tittle = getIntent().getExtras().getString("TITTLE", "");
        strmessage = getIntent().getExtras().getString("MESSAGE", "");
        strdate = getIntent().getExtras().getString("DATE", "");
        strtime = getIntent().getExtras().getString("TIME", "");
        strfilepathimage = getIntent().getExtras().getString("FILE_PATH_IMAGE", "");
        voicetype = getIntent().getExtras().getString("VOICE", "");
        PRINCIPAL_IMAGE = getIntent().getExtras().getString("PRINCIPAL_IMAGE", "");
        VideoDescription = getIntent().getExtras().getString("VIDEO_DESCRIPTION", "");

        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();

        strPDFFilepath = getIntent().getExtras().getString("FILE_PATH_PDF", "");
        strVideoFilePath = getIntent().getExtras().getString("VIDEO_FILE_PATH", "");
        Log.d("SchoolID", SchoolID);
        Log.d("StaffID", StaffID);

        if ((listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }

        if (strPDFFilepath.equals("")) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SendToEntireSchool:
                showAlert();
                break;
            case R.id.SendToStansGroups:

                if (PRINCIPAL_IMAGE.equals("IMAGE")) {
                    Intent inPrincipal = new Intent(SendToVoiceSpecificSection.this, TeacherStandardsAndGroupsList.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                    inPrincipal.putExtra("SCHOOL_ID", SchoolID);
                    inPrincipal.putExtra("STAFF_ID", StaffID);
                    inPrincipal.putExtra("TITTLE", tittle);
                    inPrincipal.putExtra("FILE_PATH_PDF", strPDFFilepath);
                    inPrincipal.putExtra("PATH_LIST", slectedImagePath);
                    inPrincipal.putExtra("PRINCIPAL_IMAGE", "IMAGE");
                    startActivityForResult(inPrincipal, iRequestCode);
                } else {
                    Intent inPrincipal = new Intent(SendToVoiceSpecificSection.this, VoiceStandardAndGroupList.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                    inPrincipal.putExtra("SCHOOL_ID", SchoolID);
                    inPrincipal.putExtra("STAFF_ID", StaffID);
                    inPrincipal.putExtra("FILEPATH", filepath);
                    inPrincipal.putExtra("DURATION", duration);
                    inPrincipal.putExtra("TITTLE", tittle);
                    inPrincipal.putExtra("VOICE", voicetype);
                    inPrincipal.putExtra("VIDEO_FILE_PATH", strVideoFilePath);
                    inPrincipal.putExtra("VIDEO_DESCRIPTION", VideoDescription);
                    Log.d("tittle", tittle);
                    startActivity(inPrincipal);

                }
                break;

            case R.id.SendToSpecificSection:
                if (PRINCIPAL_IMAGE.equals("IMAGE")) {
                    Intent intoSec = new Intent(SendToVoiceSpecificSection.this, TeacherStaffStandardSection.class);
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("TO", "SEC");
                    intoSec.putExtra("SCHOOL_ID", SchoolID);
                    intoSec.putExtra("STAFF_ID", StaffID);
                    intoSec.putExtra("FILEPATH", filepath);
                    intoSec.putExtra("FILE_PATH_PDF", strPDFFilepath);
                    intoSec.putExtra("PATH_LIST", slectedImagePath);
                    intoSec.putExtra("TITTLE", tittle);
                    startActivityForResult(intoSec, iRequestCode);
                } else {
                    Intent intoSec = new Intent(SendToVoiceSpecificSection.this, TeacherStaffStandardSection.class);
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("FILEPATH", filepath);
                    intoSec.putExtra("SCHOOL_ID", SchoolID);
                    intoSec.putExtra("STAFF_ID", StaffID);
                    intoSec.putExtra("DURATION", duration);
                    intoSec.putExtra("TITTLE", tittle);
                    intoSec.putExtra("TO", "SEC");
                    intoSec.putExtra("VOICE", voicetype);
                    intoSec.putExtra("VIDEO_FILE_PATH", strVideoFilePath);
                    intoSec.putExtra("VIDEO_DESCRIPTION", VideoDescription);
                    startActivity(intoSec);

                }
                break;
        }
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendToVoiceSpecificSection.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.confirm_to_send);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (iRequestCode == PRINCIPAL_VIDEOS) {
                    sendVideoEntireSchool("1");

                } else {
                    if (PRINCIPAL_IMAGE.equals("IMAGE")) {
                        if (!strPDFFilepath.equals("")) {
                           // sendPDFCircularApiEntireSchool();a
                            contentType="application/pdf";
                            slectedImagePath.clear();
                            slectedImagePath.add(strPDFFilepath);
                            UploadedS3URlList.clear();
                            uploadFileToAWSs3(pathIndex, ".pdf");

                        } else {
                            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");

                            //SendImageCircularAPIentireschool();a
                            contentType="image/png";
                            UploadedS3URlList.clear();
                            uploadFileToAWSs3(pathIndex, "IMG");
                        }
                    } else {
                        if (voicetype.equals("VoiceHistory")) {
                            sendToEntireSchoolFromVoiceHistory();
                        } else {
                            sendToEntireSchool();
                        }

                    }

                }
            }
        });

        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }

    private void uploadFileToAWSs3(final int pathind, final String fileType) {

        pathIndex = pathind;
        progressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        for (int index = pathIndex; index < slectedImagePath.size(); index++) {
            uploadFilePath = slectedImagePath.get(index);
            break;
        }

        if (UploadedS3URlList.size() < slectedImagePath.size()) {
            if (uploadFilePath != null) {
                showLoading();
                fileNameDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                fileNameDateTime = "File_" + fileNameDateTime;
                s3uploaderObj.initUpload(uploadFilePath, contentType, fileNameDateTime);
                s3uploaderObj.setOns3UploadDone(new S3Uploader.S3UploadInterface() {
                    @Override
                    public void onUploadSuccess(String response) {
                        if (response.equalsIgnoreCase("Success")) {
                            urlFromS3 = S3Utils.generates3ShareUrl(getApplicationContext(), uploadFilePath, fileNameDateTime);
                            if (!TextUtils.isEmpty(urlFromS3)) {
                                UploadedS3URlList.add(urlFromS3);
                                uploadFileToAWSs3(pathIndex + 1, fileType);

                                if (slectedImagePath.size() == UploadedS3URlList.size()) {
                                    //SubmitAssignmentFromAppWithCloudURL();
                                    SendMultipleImagePDFToEntireSchoolsWithCloudURL(fileType);
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

        } else {
            // Toast.makeText(this, "Null Path", Toast.LENGTH_SHORT).show();
        }


    }

    private void SendMultipleImagePDFToEntireSchoolsWithCloudURL(String fileType) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = SendEntireSChoolJson(fileType);
        Call<JsonArray> call = apiService.SendMultipleImagePDFToEntireSchoolsWithCloudURL(jsonReqArray);
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

                                showAlert1(strMsg, strStatus);
                            } else {
                                showAlert1(strMsg, strStatus);
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

    private JsonObject SendEntireSChoolJson(String fileType) {
        String isMultiple = "";
        if (fileType.equals(".pdf")) {
            isMultiple = "0";
        } else {
            isMultiple = "1";
        }
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("CallerType", StaffID);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", fileType);


            JsonArray jsonArrayschoolgrp = new JsonArray();
            JsonObject jsonObjectgroups = new JsonObject();
            jsonObjectgroups.addProperty("SchoolId", SchoolID);
            jsonObjectgroups.addProperty("StaffID", StaffID);
            jsonArrayschoolgrp.add(jsonObjectgroups);

            jsonObjectSchoolstdgrp.add("School", jsonArrayschoolgrp);

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

    private void sendVideoEntireSchool(String type) {


        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        File file = new File(strVideoFilePath);
        Log.d("FILE_Path", strVideoFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("video", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayForEntireSchoolVideo(type);
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        Call<JsonArray> call = apiService.UploadVideostoYoutube(requestBody, bodyFile);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

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

                            if (strStatus.equals("1")) {
                                showAlert1(strMsg, strStatus);

                            } else {
                                showAlert1(strMsg, strStatus);
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

    private JsonObject constructJsonArrayForEntireSchoolVideo(String type) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("VideoTitle", tittle);
            jsonObjectSchoolstdgrp.addProperty("VideoDescription", VideoDescription);
            jsonObjectSchoolstdgrp.addProperty("sEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("type", type);

            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    Log.d("schoolid", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    Log.d("schoolid", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }
            JsonArray jsonArrayschoolgSections = new JsonArray();
            JsonArray jsonArrayschoolgStudents = new JsonArray();

            jsonObjectSchoolstdgrp.add("Standards", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("Groups", jsonArrayschoolgrp);
            jsonObjectSchoolstdgrp.add("Sections", jsonArrayschoolgSections);
            jsonObjectSchoolstdgrp.add("Students", jsonArrayschoolgStudents);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void sendPDFCircularApiEntireSchool() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        File file = new File(strPDFFilepath);
        Log.d("FILE_Path", strPDFFilepath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        JsonObject jsonReqArray = constructResultJsonArrayentireschool("0", ".pdf");
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        Call<JsonArray> call = apiService.SendImageToGroupsAndStandards(requestBody, bodyFile);

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

                                showAlert1(strMsg, strStatus);

                            } else {
                                showAlert1(strMsg, strStatus);
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

    private void SendImageCircularAPIentireschool() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");

        Log.d("pathList_size", String.valueOf(slectedImagePath.size()));
        Log.d("imageList", slectedImagePath.toString());

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[slectedImagePath.size()];
        for (int index = 0; index < slectedImagePath.size(); index++) {

            //Log.d("file path",slectedImagePath.get(index));

            //  Log.d(TAG, "requestUploadSurvey: survey image " + index + "  " + surveyModel.getPicturesList().get(index).getImagePath());

            File file = new File(slectedImagePath.get(index));
            RequestBody surveyBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
            surveyImagesParts[index] = MultipartBody.Part.createFormData("image", file.getName(), surveyBody);
        }

        JsonObject jsonReqArray = constructResultJsonArrayentireschool("1", "IMG");
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        Call<JsonArray> call = apiService.sendMultipleImagesToGroupAndStand(requestBody, surveyImagesParts);

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

                                showAlert1(strMsg, strStatus);

                            } else {
                                showAlert1(strMsg, strStatus);
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

    private JsonObject constructResultJsonArrayentireschool(String multiple, String filetype) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("isMultiple", multiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", filetype);


            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    Log.d("schoolid", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    Log.d("schoolid", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void sendToEntireSchoolFromVoiceHistory() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        JsonObject jsonReqArray = jsonArrayEntireSchoolFromVoiceHistory();


        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoicetoGroupsStandardsfromVoiceHistory(jsonReqArray);
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
                                showAlert1(strMsg, strStatus);


                            } else {
                                showAlert1(strMsg, strStatus);
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

    private JsonObject jsonArrayEntireSchoolFromVoiceHistory() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            jsonObjectSchoolstdgrp.addProperty("filepath", filepath);

            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    Log.d("schoolid", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    Log.d("schoolid", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void sendToEntireSchool() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
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

//        // add another part within the multipart request
        JsonObject jsonReqArray = constructJsonArraySchoolsvoiceprincipalEntireschool();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToGroupsAndStandards(requestBody, bodyFile);
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
                                showAlert1(strMsg, strStatus);


                            } else {
                                showAlert1(strMsg, strStatus);
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

    private void showAlert1(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendToVoiceSpecificSection.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(SendToVoiceSpecificSection.this, Teacher_AA_Test.class);
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

    private JsonObject constructJsonArraySchoolsvoiceprincipalEntireschool() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    Log.d("schoolid", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    Log.d("schoolid", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


}
