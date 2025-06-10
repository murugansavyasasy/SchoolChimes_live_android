package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendToVoiceSpecificSection extends AppCompatActivity implements View.OnClickListener {


    Button SendToEntireSchool, SendToStansGroups, SendToSpecificSection, SendToStaff;
    String SchoolID, StaffID, filepath, duration, tittle, strmessage, strdate, strtime, strfilepathimage;
    int iRequestCode = 0;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;
    String voicetype = "";
    String PRINCIPAL_IMAGE = "";
    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String strPDFFilepath, strVideoFilePath, VideoDescription;
    String fileNameDateTime;
    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType = "";
    String uploadFilePath = "";
    int pathIndex = 0;
    private final ArrayList<String> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
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

        SendToStaff = findViewById(R.id.SendToStaff);
        SendToStaff.setOnClickListener(this);


        String countryID = TeacherUtil_SharedPreference.getCountryID(SendToVoiceSpecificSection.this);
        if (countryID.equals("11")) {
            SendToStansGroups.setText("Send to Grade/Groups");
            SendToSpecificSection.setText("Send to Grade/sections");
        }

        ImageView ivBack = (ImageView) findViewById(R.id.emergVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        isAwsUploadingPreSigned = new AwsUploadingPreSigned();
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

        if ((listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }

        if (strPDFFilepath.equals("")) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }
        String Role = TeacherUtil_SharedPreference.getRole(SendToVoiceSpecificSection.this);

        if (Role.equals("p2") && iRequestCode == PRINCIPAL_VOICE) {
            SendToStaff.setVisibility(View.VISIBLE);
        } else {
            SendToStaff.setVisibility(View.GONE);
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

            case R.id.SendToStaff:
                Intent isSendStaff = new Intent(SendToVoiceSpecificSection.this, SendToStaff.class);
                isSendStaff.putExtra("REQUEST_CODE", iRequestCode);
                isSendStaff.putExtra("FILEPATH", filepath);
                isSendStaff.putExtra("SCHOOL_ID", SchoolID);
                isSendStaff.putExtra("STAFF_ID", StaffID);
                isSendStaff.putExtra("DURATION", duration);
                isSendStaff.putExtra("TITTLE", tittle);
                isSendStaff.putExtra("MESSAGE", strmessage);
                startActivity(isSendStaff);
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
                progressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
                showLoading();
                if (iRequestCode == PRINCIPAL_VIDEOS) {
                    sendVideoEntireSchool("1");

                } else {
                    if (PRINCIPAL_IMAGE.equals("IMAGE")) {
                        if (!strPDFFilepath.equals("")) {
                            contentType = "application/pdf";
                            slectedImagePath.clear();
                            slectedImagePath.add(strPDFFilepath);
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("pdf", ".pdf", "");
                        } else {
                            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
                            contentType = "image/png";
                            UploadedS3URlList.clear();
                            showLoading();
                            isUploadAWS("image", "IMG", "");
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
        }).setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
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

    private void isUploadAWS(String contentType, String isType, String value) {

        Log.d("selectedImagePath", String.valueOf(slectedImagePath.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();

        for (int i = 0; i < slectedImagePath.size(); i++) {
            AwsUploadingFile(String.valueOf(slectedImagePath.get(i)), SchoolID, contentType, isType, value);
        }
    }

    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(SendToVoiceSpecificSection.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this,countryID,true,false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);

                if (UploadedS3URlList.size() == slectedImagePath.size()) {
                    SendMultipleImagePDFToEntireSchoolsWithCloudURL(filetype, type);
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }


    private void SendMultipleImagePDFToEntireSchoolsWithCloudURL(String fileType, String type) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);


        runOnUiThread(() -> {

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
                        Log.d("Upload:Body", response.body().toString());

                        try {
                            JSONArray js = new JSONArray(response.body().toString());
                            if (js.length() > 0) {
                                JSONObject jsonObject = js.getJSONObject(0);
                                String strStatus = jsonObject.getString("Status");
                                String strMsg = jsonObject.getString("Message");

                                if ((strStatus).equalsIgnoreCase("1")) {

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
                    showToast(t.toString());
                }
            });

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
        if (progressDialog == null) {
            // Initialize the ProgressDialog if it hasn't been created yet
            progressDialog = new ProgressDialog(this); // Replace 'this' with your Context if not in an Activity
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.Uploading));
            progressDialog.setCancelable(false);
        }

        // Show the ProgressDialog if it is not already showing
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            Log.d("ProgressBar", "ProgressDialog is already showing");
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
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
                    Log.d("Upload:Body", response.body().toString());

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
                showToast(t.toString());
                Log.d("Exception_", t.toString());
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
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
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

    private void sendToEntireSchoolFromVoiceHistory() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToVoiceSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = jsonArrayEntireSchoolFromVoiceHistory();

        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoicetoGroupsStandardsfromVoiceHistory(jsonReqArray);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleVoicetoGroupsStandardsfromVoiceHistory(jsonReqArray);
        } else {
            call = apiService.SendVoicetoGroupsStandardsfromVoiceHistory(jsonReqArray);
        }
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
                Log.d("Upload error:", t.getMessage() + "\n" + t);
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
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }


            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

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

        JsonObject jsonReqArray = constructJsonArraySchoolsvoiceprincipalEntireschool();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(SendToVoiceSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoiceToGroupsAndStandards(requestBody, bodyFile);

        Call<JsonArray> call;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleToGroupsAndStandards(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceToGroupsAndStandards(requestBody, bodyFile);
        }

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
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);
            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < Util_Common.isSelectedDate.size(); i++) {
                    String isSelected = (Util_Common.isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolstdgrp.add("Dates", isSelectedArray);
                jsonObjectSchoolstdgrp.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolstdgrp.addProperty("EndTime", Util_Common.isEndTime);
            }

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

