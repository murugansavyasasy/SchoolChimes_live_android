package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.text.TextUtils;
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
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.aws.S3Uploader;
import com.vs.schoolmessenger.aws.S3Utils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;

public class VoiceStandardAndGroupList extends AppCompatActivity {
    GridLayout gridlayoutSection, gridlayoutgroups;

    CheckBox[] cbListStd, cbListGroups;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;
    Button btnConfirm;
    EditText et_tittle;
    CheckBox cbToAll;
    LinearLayout llStdGrp;
    String SchoolID, StaffID;
    int iSelStdGrpCount = 0;
    private int iRequestCode = 0;
    String filepath, tittle, strmessage, strdate, strtime, strfilepathimage;
    String duration,strVdeoFilePath;

    ImageView genTextPopup_ToolBarIvBack;

    String voicetype = "",VideoDescription;

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

    ArrayList<String> slectedImagePath = new ArrayList<String>();

    boolean isStandardDisable = false;

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
        strVdeoFilePath = getIntent().getExtras().getString("VIDEO_FILE_PATH", "");
        voicetype = getIntent().getExtras().getString("VOICE", "");
        VideoDescription = getIntent().getExtras().getString("VIDEO_DESCRIPTION", "");
        Log.d("FILEPATH", filepath);
        cbToAll = (CheckBox) findViewById(R.id.princiStdGrp_cbToAll);
        llStdGrp = (LinearLayout) findViewById(R.id.princiStdGrp_llStdGroups);
        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);

        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        cbToAll.setVisibility(View.GONE);

        btnConfirm = (Button) findViewById(R.id.sectiongroup_btnConfirm);
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


    private void standardsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(VoiceStandardAndGroupList.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(VoiceStandardAndGroupList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
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

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StandardGroups", response.body().toString());

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
                                            if(isStandardDisable) {
                                                cbListStd[finalI].setChecked(false);
                                                listClasses.get(finalI).setbSelected(false);
                                            }
                                            else {
                                                listClasses.get(finalI).setbSelected(isChecked);
                                                if (isChecked)
                                                    iSelStdGrpCount++;
                                                else iSelStdGrpCount--;
                                            }


//                                            listClasses.get(finalI).setbSelected(isChecked);
//                                            if (isChecked)
//                                                iSelStdGrpCount++;
//                                            else iSelStdGrpCount--;
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

                                String GroupID = jsonObjectgroups.getString("GroupID");
                                String GroupName = jsonObjectgroups.getString("GroupName");
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

                                            if(listGroups.get(finalI).getStrName().equalsIgnoreCase("All Student Group")){
                                                if(isChecked){
                                                    isStandardDisable = true;
                                                    for (int i =0;i<listClasses.size();i++){
                                                        cbListStd[i].setChecked(false);
                                                        listClasses.get(i).setbSelected(false);
                                                    }
                                                }
                                                else {
                                                    isStandardDisable = false;
                                                }
                                            }


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


                            cbToAll.setChecked(false);
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
            showToast(strMsgType + "-" + sbStd.toString() + "\n" + sbGrp.toString().trim());
            backToResultActvity("SENT");
        } else showToast("Select participants");

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
                    if (voicetype.equals("VoiceHistory")) {
                        SendToVoiceFromVoiceHistory();
                    } else {
                        SendVoiceAPIEntireschool();
                    }
                } else {
                    if(voicetype.equals("VoiceHistory")){
                        if(iSelStdGrpCount>0) {
                            SendToVoiceFromVoiceHistory();
                        }
                        else {
                            showToast(getResources().getString(R.string.select_participants));
                        }
                    }
                    else {
                        if(iSelStdGrpCount>0) {
                            SendImageAPI();
                        }
                        else {
                            showToast(getResources().getString(R.string.select_participants));
                        }
                    }
                }
                break;


            case GH_VOICE:
                if (cbToAll.isChecked()) {
                    if (voicetype.equals("VoiceHistory")) {
                        SendToVoiceFromVoiceHistory();
                    } else {
                        SendVoiceAPIEntireschool();
                    }
                } else {
                    if(voicetype.equals("VoiceHistory")){
                        if(iSelStdGrpCount>0) {
                            SendToVoiceFromVoiceHistory();
                        }
                        else {
                            showToast(getResources().getString(R.string.select_participants));
                        }
                    }
                    else {
                        if(iSelStdGrpCount>0) {
                            SendImageAPI();
                        }
                        else {
                            showToast(getResources().getString(R.string.select_participants));
                        }
                    }
                }
                break;

            case PRINCIPAL_TEXT:
                if (cbToAll.isChecked()) {
                    SendTextAPIentireschool();
                } else {
                    if(iSelStdGrpCount>0) {
                        SendTextAPIgrpstd();
                    }
                    else {
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
                    if(iSelStdGrpCount>0) {
                        SendEventsAPIstdgrp();
                    }
                    else {
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            case PRINCIPAL_PHOTOS:
                if (cbToAll.isChecked()) {
                    SendImageCircularAPIentireschool();


                } else {
                    if(iSelStdGrpCount>0) {
                        SendImageCircularAPIstdgrp();
                    }
                    else {
                        showToast(getResources().getString(R.string.select_participants));
                    }
                }
                break;

            case PRINCIPAL_VIDEOS:

                if(iSelStdGrpCount>0) {
                    SendVideoToStandardAndGroups("2");
                }
                else {
                    showToast(getResources().getString(R.string.select_participants));
                }


                break;

            default:
                break;
        }
    }




    private void SendVideoToStandardAndGroups(String type) {

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        File file = new File(strVdeoFilePath);
        Log.d("FILE_Path", strVdeoFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("video", file.getName(), requestFile);

        JsonObject jsonReqArray = JsonArrayFromGroupsAndStandrsVideo(type);
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        Call<JsonArray> call = apiService.UploadVideostoYoutube(requestBody, bodyFile);

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

                            if (strStatus.equals("1")) {
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
                showToast(t.toString());
            }
        });
    }

    private JsonObject JsonArrayFromGroupsAndStandrsVideo(String type) {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("VideoTitle", tittle);
            jsonObjectSchoolstdgrp.addProperty("VideoDescription", VideoDescription);
            jsonObjectSchoolstdgrp.addProperty("sEntireSchool", "F");
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

    private void SendToVoiceFromVoiceHistory() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = jsonArrayFromVoiceHistory();
        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayFromVoiceHistory() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            if(cbToAll.isChecked()) {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            }
            else {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            }
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


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            Log.d("schoolid", SchoolID);
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void SendImageAPI() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
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
        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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


    private JsonObject constructJsonArraySchoolsvoiceprincipal() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            if(cbToAll.isChecked()) {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            }
            else {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            }
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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }


    private void SendVoiceAPIEntireschool() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
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
        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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

    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VoiceStandardAndGroupList.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(VoiceStandardAndGroupList.this, Teacher_AA_Test.class);
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
            if(cbToAll.isChecked()) {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            }
            else {
                jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "F");
            }
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

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendTextAPIgrpstd() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
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


    private void SendImageCircularAPIstdgrp() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strfilepathimage);
        Log.d("FILE_Path", strfilepathimage);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        JsonObject jsonReqArray = constructResultJsonArray();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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
                showToast(t.toString());
            }
        });
    }


    private JsonObject constructResultJsonArray() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
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


    private void SendImageCircularAPIentireschool() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(VoiceStandardAndGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strfilepathimage);
        Log.d("FILE_Path", strfilepathimage);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        JsonObject jsonReqArray = constructResultJsonArrayentireschool();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(VoiceStandardAndGroupList.this);
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
                showToast(t.toString());
            }
        });
    }


    private JsonObject constructResultJsonArrayentireschool() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolId", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffId", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
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

}

