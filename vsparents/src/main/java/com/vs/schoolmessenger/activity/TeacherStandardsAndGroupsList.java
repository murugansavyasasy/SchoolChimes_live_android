package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherStandardsAndGroupsList extends AppCompatActivity {
    GridLayout gridlayoutSection, gridlayoutgroups;

    CheckBox[] cbListStd, cbListGroups;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;
    Button btnConfirm;
    EditText et_tittle;
    CheckBox cbToAll;
    LinearLayout llStdGrp;
    String SchoolID, StaffID;
    int iSelStdGrpCount = 0;
    String filepath, tittle, strmessage, strdate, strtime, strfilepathimage, strfilepathPDF;
    String duration;
    ImageView genTextPopup_ToolBarIvBack;
    String PRINCIPAL_IMAGE = "";
    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String fileNameDateTime;
    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType = "";
    String uploadFilePath = "";
    int pathIndex = 0;
    TextView lblStandard;
    private int iRequestCode = 0;
    private final ArrayList<String> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;

    private static void setViewAndChildrenEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                setViewAndChildrenEnabled(child, enabled);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_standards_groups);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
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
        strfilepathPDF = getIntent().getExtras().getString("FILE_PATH_PDF", "");

        PRINCIPAL_IMAGE = getIntent().getExtras().getString("PRINCIPAL_IMAGE", "");
        Log.d("FILEPATH", filepath);
        cbToAll = (CheckBox) findViewById(R.id.princiStdGrp_cbToAll);
        llStdGrp = (LinearLayout) findViewById(R.id.princiStdGrp_llStdGroups);
        isAwsUploadingPreSigned = new AwsUploadingPreSigned();

        lblStandard = (TextView) findViewById(R.id.lblStandard);
        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherStandardsAndGroupsList.this);
        if (countryID.equals("11")) {
            lblStandard.setText("Grades");
        }

        cbToAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                disableEnableControls(!isChecked, llStdGrp);
            }
        });

        btnConfirm = (Button) findViewById(R.id.sectiongroup_btnConfirm);
        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgTypeAndAPIs();
            }
        });

        gridlayoutSection = (GridLayout) findViewById(R.id.gridlayout_Section);
        gridlayoutgroups = (GridLayout) findViewById(R.id.gridlayout_groups);
        LayoutInflater inflater = getLayoutInflater();
        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();
        standardsListAPI();

    }

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }

    private void standardsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherStandardsAndGroupsList.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherStandardsAndGroupsList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();

        Call<JsonArray> call = apiService.GetAllStandardsAndGroups(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        for (int i = 0; i < js.length(); i++) {
                            JSONArray jSONArray = jsonObject.getJSONArray("Standards");
                            TeacherClassGroupModel classstd;
                            cbListStd = new CheckBox[jSONArray.length()];
                            for (int j = 0; j < jSONArray.length(); j++) {
                                JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);

                                String StandardID = jsonObjectgroups.getString("StandardID");
                                String StandardName = jsonObjectgroups.getString("StandardName");

                                if (!StandardID.equals("0")) {
                                    classstd = new TeacherClassGroupModel(jsonObjectgroups.getString("StandardName"), jsonObjectgroups.getString("StandardID"), false);
                                    listClasses.add(classstd);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View addView1 = inflater.inflate(R.layout.teacher_section_list, null, false);
                                    gridlayoutSection.addView(addView1);
                                    cbListStd[j] = (CheckBox) addView1.findViewById(R.id.ch_section);
                                    cbListStd[j].setText(listClasses.get(j).getStrName());
                                    final int finalI = j;
                                    cbListStd[j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            listClasses.get(finalI).setbSelected(isChecked);

                                            if (isChecked)
                                                iSelStdGrpCount++;
                                            else iSelStdGrpCount--;
                                        }
                                    });
                                } else {
                                    showToast(StandardName);
                                }
                            }


                            JSONArray jSONArray1 = jsonObject.getJSONArray("Groups");
                            TeacherClassGroupModel classgrp;
                            cbListGroups = new CheckBox[jSONArray1.length()];
                            for (int j = 0; j < jSONArray1.length(); j++) {
                                JSONObject jsonObjectgroups = jSONArray1.getJSONObject(j);
                                String GroupName = jsonObjectgroups.getString("GroupName");
                                String GroupID = jsonObjectgroups.getString("GroupID");
                                if (!GroupID.equals("0")) {
                                    classgrp = new TeacherClassGroupModel(jsonObjectgroups.getString("GroupName"), jsonObjectgroups.getString("GroupID"), false);
                                    listGroups.add(classgrp);
                                    LayoutInflater inflater = getLayoutInflater();
                                    View addView1 = inflater.inflate(R.layout.teacher_section_list, null, false);
                                    gridlayoutgroups.addView(addView1);
                                    cbListGroups[j] = (CheckBox) addView1.findViewById(R.id.ch_section);
                                    cbListGroups[j].setText(listGroups.get(j).getStrName());
                                    final int finalI = j;
                                    cbListGroups[j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            listGroups.get(finalI).setbSelected(isChecked);

                                            if (isChecked)
                                                iSelStdGrpCount++;
                                            else iSelStdGrpCount--;
                                        }
                                    });

                                } else {
                                    showToast(GroupName);
                                }
                            }

                        }


                        cbToAll.setChecked(true);

                        if (PRINCIPAL_IMAGE.equals("IMAGE")) {
                            cbToAll.setChecked(false);
                            cbToAll.setVisibility(View.GONE);
                        }


                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void listOfSelectedStdAndGroups(String strMsgType) {
        StringBuilder sbStd = new StringBuilder();
        StringBuilder sbGrp = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            TeacherClassGroupModel stdMod = listClasses.get(i);
            TeacherClassGroupModel grpMod = listGroups.get(i);
            if (stdMod.isbSelected())
                sbStd.append(stdMod.getStrID() + ", ");
            if (grpMod.isbSelected())
                sbGrp.append(grpMod.getStrID() + ", ");
        }

        if (iSelStdGrpCount > 0) {
            showToast(strMsgType + "-" + sbStd + "\n" + sbGrp.toString().trim());
            backToResultActvity("SENT");
        } else showToast(getResources().getString(R.string.select_participants));

    }

    private void msgTypeAndAPIs() {
        switch (iRequestCode) {
            case PRINCIPAL_EMERGENCY:
                if (cbToAll.isChecked()) {
                } else {
                    listOfSelectedStdAndGroups("EV");
                }
                break;

            case PRINCIPAL_VOICE:
                if (cbToAll.isChecked()) {
                    SendVoiceAPIEntireschool();
                } else {
                    if (iSelStdGrpCount > 0) {
                        SendImageAPI();
                    } else {
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            case PRINCIPAL_TEXT:
                if (cbToAll.isChecked()) {
                    SendTextAPIentireschool();
                } else {
                    if (iSelStdGrpCount > 0) {
                        SendTextAPIgrpstd();
                    } else {
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            case PRINCIPAL_NOTICE_BOARD:
                if (cbToAll.isChecked()) {
                    showToast("NB-Sent to entire school");
                    backToResultActvity("SENT");
                } else {
                    listOfSelectedStdAndGroups("NB");
                }
                break;

            case PRINCIPAL_EVENTS:
                if (cbToAll.isChecked()) {
                    SendEventsAPIentireschool();
                } else {

                    if (iSelStdGrpCount > 0) {
                        SendEventsAPIstdgrp();
                    } else {
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            case PRINCIPAL_PHOTOS:
                if (cbToAll.isChecked()) {
                    if (!strfilepathPDF.equals("")) {
                        contentType = "application/pdf";
                        slectedImagePath.clear();
                        slectedImagePath.add(strfilepathPDF);
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

                    if (iSelStdGrpCount > 0) {

                        if (!strfilepathPDF.equals("")) {

                            contentType = "application/pdf";
                            slectedImagePath.clear();
                            slectedImagePath.add(strfilepathPDF);
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
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            default:
                break;
        }
    }

    private void isUploadAWS(String contentType, String isType, String value) {

        Log.d("selectedImagePath", String.valueOf(slectedImagePath.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();

        for (int i = 0; i < slectedImagePath.size(); i++) {
            AwsUploadingFile(String.valueOf(slectedImagePath.get(i)), SchoolID, contentType, isType, value);
        }
    }

    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherStandardsAndGroupsList.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this,countryID,true,false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);

                if (UploadedS3URlList.size() == slectedImagePath.size()) {
                    SendMultipleImagePDFToGroupsAndStandardsWithCloudURL(filetype, type);
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }

    private void SendMultipleImagePDFToGroupsAndStandardsWithCloudURL(String fileType, String type) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        runOnUiThread(() -> {

            JsonObject jsonReqArray = SendGroupAndStandardJson(fileType);
            Call<JsonArray> call = apiService.SendMultipleImagePDFToGroupsAndStandardsWithCloudURL(jsonReqArray);
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

        });
    }

    private JsonObject SendGroupAndStandardJson(String fileType) {
        String isMultiple = "";
        if (fileType.equals(".pdf")) {
            isMultiple = "0";
        } else {
            isMultiple = "1";
        }

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", fileType);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");


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

            jsonObjectSchoolstdgrp.add("Stdcode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);


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
            progressDialog.setMessage("Uploading..");
            progressDialog.setCancelable(false);
        }

        // Show the ProgressDialog if it is not already showing
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            Log.d("ProgressBar", "ProgressDialog is already showing");
        }
    }


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            Log.d("req", jsonObjectSchool.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void SendImageAPI() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filepath);
        Log.d("FILE_Path", filepath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArraySchoolsvoiceprincipal();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
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
                                showAlert(strMsg);
                            } else {
                                showAlert(strMsg);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
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


    private JsonObject constructJsonArraySchoolsvoiceprincipal() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

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


    void SendVoiceAPIEntireschool() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
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
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        //   Call<JsonArray> call = apiService.SendVoiceToGroupsAndStandards(requestBody, bodyFile);

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
                showToast(t.toString());
            }
        });
    }

    private void showEventAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherStandardsAndGroupsList.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                backToResultActvity("");
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

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherStandardsAndGroupsList.this);


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

    private void SendTextAPIgrpstd() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstext();
        Call<JsonArray> call = apiService.SendSMSToGroupsAndStandards(jsonReqArray);
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

    private JsonObject constructJsonArrayMgtSchoolstext() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);

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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendTextAPIentireschool() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstextentireschool();
        Call<JsonArray> call = apiService.SendSMSToGroupsAndStandards(jsonReqArray);
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
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolstextentireschool() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);

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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendEventsAPIstdgrp() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsevents();
        Call<JsonArray> call = apiService.ManageSchoolEvents(jsonReqArray);
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

                            if (strStatus.equals("1")) {
                                showAlert(strMsg);
                            } else {
                                showEventAlert(strMsg);
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

    private JsonObject constructJsonArrayMgtSchoolsevents() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("EventDate", strdate);
            jsonObjectSchoolstdgrp.addProperty("EventTime", strtime);
            jsonObjectSchoolstdgrp.addProperty("EventTopic", tittle);
            jsonObjectSchoolstdgrp.addProperty("EventBody", strmessage);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");


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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendEventsAPIentireschool() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherStandardsAndGroupsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStandardsAndGroupsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolseventsentireschool();
        Call<JsonArray> call = apiService.ManageSchoolEvents(jsonReqArray);
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

                            if (strStatus.equals("1")) {
                                showAlert(strMsg);
                            } else {
                                showEventAlert(strMsg);
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

    private JsonObject constructJsonArrayMgtSchoolseventsentireschool() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("EventDate", strdate);
            jsonObjectSchoolstdgrp.addProperty("EventTime", strtime);
            jsonObjectSchoolstdgrp.addProperty("EventTopic", tittle);
            jsonObjectSchoolstdgrp.addProperty("EventBody", strmessage);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");


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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private JsonObject constructResultJsonArray(String multiple, String filetype) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            jsonObjectSchoolstdgrp.addProperty("isMultiple", multiple);
            jsonObjectSchoolstdgrp.addProperty("FileType", filetype);


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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

}
