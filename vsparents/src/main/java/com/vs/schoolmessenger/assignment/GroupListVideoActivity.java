package com.vs.schoolmessenger.assignment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherEmergencyVoice;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.activity.ToStaffGroupList;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.VimeoUploader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

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

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;


public class GroupListVideoActivity extends AppCompatActivity implements VimeoUploader.UploadCompletionListener {
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
    String filepath, tittle, strmessage, strdate, strtime, strfilepathimage, strfilepathPDF;
    String duration;

    ImageView genTextPopup_ToolBarIvBack;

    String PRINCIPAL_IMAGE = "", upload_link, link, iframe;
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url, strsize;
    ArrayList<String> slectedImagePath = new ArrayList<String>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_groups);


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
        tittle = getIntent().getExtras().getString("TITLE", "");
        strmessage = getIntent().getExtras().getString("CONTENT", "");
        strsize = getIntent().getExtras().getString("FILE_SIZE", "");
        strdate = getIntent().getExtras().getString("DATE", "");
        strtime = getIntent().getExtras().getString("TIME", "");
        strfilepathimage = getIntent().getExtras().getString("FILE_PATH_IMAGE", "");
        strfilepathPDF = getIntent().getExtras().getString("FILE_PATH_PDF", "");

        PRINCIPAL_IMAGE = getIntent().getExtras().getString("PRINCIPAL_IMAGE", "");
        Log.d("FILEPATH", filepath);
        cbToAll = (CheckBox) findViewById(R.id.princiStdGrp_cbToAll);
        llStdGrp = (LinearLayout) findViewById(R.id.princiStdGrp_llStdGroups);
        llStdGrp.setEnabled(true);
        cbToAll.setChecked(false);
        cbToAll.setVisibility(View.GONE);
//        disableEnableControls(false, llStdGrp);
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
//                msgTypeAndAPIs();
                // VimeoAPi();
                uploadVimeoVideo();
            }
        });

        gridlayoutSection = (GridLayout) findViewById(R.id.gridlayout_Section);
//        gridlayoutSection.requestLayout();
        gridlayoutgroups = (GridLayout) findViewById(R.id.gridlayout_groups);
//        gridlayoutgroups.requestLayout();
        LayoutInflater inflater = getLayoutInflater();
        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();
        standardsListAPI();

    }

    private void uploadVimeoVideo() {
        String authToken = TeacherUtil_SharedPreference.getVideotoken(GroupListVideoActivity.this);
        VimeoUploader.uploadVideo(GroupListVideoActivity.this, tittle, strmessage, authToken, filepath, this);
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

    private void disableEnableControls(boolean enable, ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup) {
                disableEnableControls(enable, (ViewGroup) child);
            }
        }
    }


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

    private void groupListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(GroupListVideoActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

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
                            JSONArray jSONArray = jsonObject.getJSONArray("Groups");
                            TeacherClassGroupModel classgrp;
                            cbListGroups = new CheckBox[jSONArray.length()];
                            for (int j = 0; j < jSONArray.length(); j++) {
                                JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
                                classgrp = new TeacherClassGroupModel(jsonObjectgroups.getString("GroupName"), jsonObjectgroups.getString("GroupId"), false);
                                listGroups.add(classgrp);
                                Log.d("Exception1", "except1");
                                LayoutInflater inflater = getLayoutInflater();
                                View addView1 = inflater.inflate(R.layout.group_list_video, null, false);
                                Log.d("Exception2", "except2");
                                gridlayoutgroups.addView(addView1);
                                cbListGroups[j] = (CheckBox) addView1.findViewById(R.id.ch_section);
                                cbListGroups[j].setText(listGroups.get(j).getStrName());
                                final int finalI = j;
                                Log.d("Exception3", "except3");
                                cbListGroups[j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        listGroups.get(finalI).setbSelected(isChecked);

                                        if (isChecked)
                                            iSelStdGrpCount++;
                                        else iSelStdGrpCount--;
                                    }
                                });
                            }

                        }

                        cbToAll.setChecked(false);
                        cbToAll.setVisibility(View.GONE);

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
                Log.d("GroupList:Failure", t.toString());
            }
        });
    }


    private void standardsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(GroupListVideoActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(GroupListVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(GroupListVideoActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();

        Call<JsonArray> call = apiService.GetAllStandardsAndGroupsForVideo(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @SuppressLint("CutPasteId")
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
                            TeacherClassGroupModel classstd;
                            cbListStd = new CheckBox[jSONArray.length()];
                            for (int j = 0; j < jSONArray.length(); j++) {
                                JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);

                                String StandardID = jsonObjectgroups.getString("StandardID");
                                String StandardName = jsonObjectgroups.getString("StandardName");

                                if (!StandardID.equals("0")) {
                                    classstd = new TeacherClassGroupModel(jsonObjectgroups.getString("StandardName"), jsonObjectgroups.getString("StandardID"), false);
                                    listClasses.add(classstd);
                                    Log.d("Exception1", "except1std");
                                    LayoutInflater inflater = getLayoutInflater();
                                    View addView1 = inflater.inflate(R.layout.group_list_video, null, false);
                                    Log.d("Exception2", "except2std");
                                    gridlayoutSection.addView(addView1);
                                    cbListStd[j] = (CheckBox) addView1.findViewById(R.id.ch_section);
                                    cbListStd[j].setText(listClasses.get(j).getStrName());
                                    final int finalI = j;
                                    Log.d("Exception3", "except3std");
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
                                    Log.d("Exception1", "except1");
                                    LayoutInflater inflater = getLayoutInflater();
                                    // inflate the header description layout and add it to the linear layout:
                                    View addView1 = inflater.inflate(R.layout.group_list_video, null, false);
                                    Log.d("Exception2", "except2");
                                    gridlayoutgroups.addView(addView1);
                                    cbListGroups[j] = (CheckBox) addView1.findViewById(R.id.ch_section);
                                    cbListGroups[j].setText(listGroups.get(j).getStrName());
                                    final int finalI = j;
                                    Log.d("Exception3", "except3");
                                    cbListGroups[j].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                                            if(listGroups.get(finalI).getStrName().equalsIgnoreCase("All Staff Group")){
//                                                listGroups.get(finalI).setbSelected(false);
//                                                cbListGroups[finalI].setChecked(false);
//                                                showToast("Sorry!! You cannot send video to staff groups");
//
//                                            }

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

//                        if(PRINCIPAL_IMAGE.equals("IMAGE")){
                        cbToAll.setChecked(false);
                        cbToAll.setVisibility(View.GONE);


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
                Log.d("GroupList:Failure", t.toString());
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
        } else showToast(getResources().getString(R.string.select_participants));

    }


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);

            Log.d("req", jsonObjectSchool.toString());
            Log.d("schoolid", SchoolID);
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void showAlertfinal(String msg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupListVideoActivity.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(GroupListVideoActivity.this, Teacher_AA_Test.class);
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

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GroupListVideoActivity.this);


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

    private JsonObject JsonVideotoSection() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("Title", tittle);
            jsonObjectSchoolstdgrp.addProperty("Description", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Iframe", iframe);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolstdgrp.addProperty("URL", link);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            //getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();


            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    Log.d("schoolid", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolstd1 = new JsonArray();


            for (int j = 0; j < listGroups.size(); j++) {
                if (listGroups.get(j).isbSelected()) {
                    JsonObject jsonObjectclass1 = new JsonObject();
                    jsonObjectclass1.addProperty("TargetCode", listGroups.get(j).getStrID());
                    Log.d("schoolid", listGroups.get(j).getStrID());
                    jsonArrayschoolstd1.add(jsonObjectclass1);
                }
            }
            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolstd1);
            Log.d("reqVideo", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    void SendVideotoSecApi() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(GroupListVideoActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


//        final ProgressDialog mProgressDialog = new ProgressDialog(GroupListVideoActivity.this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Uploading...");
//        mProgressDialog.setCancelable(false);
//
//        if (!this.isFinishing())
//            mProgressDialog.show();
        JsonObject jsonReqArray = JsonVideotoSection();
        Call<JsonArray> call = apiService.AppSendVideoToGroupsAndStandards(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

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
//        OkHttpClient.Builder client = new OkHttpClient.Builder();

        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl("https://api.vimeo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(GroupListVideoActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);


        JsonObject object = new JsonObject();

        JsonObject jsonObjectclasssec = new JsonObject();
        jsonObjectclasssec.addProperty("approach", "post");
        jsonObjectclasssec.addProperty("size", String.valueOf(strsize));
//        jsonObjectclasssec.addProperty("redirect_url", "www.voicesnapforschools.com");

        JsonObject jsonprivacy = new JsonObject();
        jsonprivacy.addProperty("view", "unlisted");

        JsonObject jsonshare = new JsonObject();
        jsonshare.addProperty("share", "false");

        JsonObject jsonembed = new JsonObject();
        jsonembed.add("buttons", jsonshare);

        object.add("upload", jsonObjectclasssec);
        object.addProperty("name", tittle);
        object.addProperty("description", strmessage);
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);

        String head = "Bearer " + TeacherUtil_SharedPreference.getVideotoken(GroupListVideoActivity.this);
        Log.d("head", head);
        Call<JsonObject> call = service.VideoUpload(object, head);
        Log.d("jsonOBJECT", object.toString());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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
                        Log.d("c", upload_link);
                        Log.d("iframe", iframe);


//                        alert(res);

//                        UpoloadApiClient linkapi = new UpoloadApiClient(upload_link);
//                        Log.d("linkapi", String.valueOf(linkapi));


                        String[] separated = upload_link.split("\\?(?!\\?)");

                        String name = separated[0];  //"/"
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

//                    VIDEOUPLOAD(upload_link+"/");


                } else {
//                    alert(res);
                    Log.d("Response fail", "fail");
                    showAlertfinal("Video sending failed.Retry", "0");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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

        OkHttpClient client;
        client = new OkHttpClient.Builder()

                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.MINUTES)
                .writeTimeout(40, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit1 = new Retrofit.Builder()
                .client(client)
                .baseUrl(upload)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        TeacherMessengerApiInterface service = retrofit1.create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(GroupListVideoActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
//        Call<List<Repo>> repos = service.listRepos("Gino Osahon");

//        APICLIENT();
//        Interface apiService = UpoloadApiClient.getVideoClient().create(Interface.class);

//        File file = new File(imagePathList.get(0));
        RequestBody requestFile = null;

        try {
            InputStream in = new FileInputStream(filepath);

            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            requestFile = RequestBody.create(MediaType.parse("application/offset+octet-stream"), buf);

        } catch (IOException e) {
//            e.printStackTrace();
            showAlertfinal(e.getMessage(), "0");
        }

        Call<ResponseBody> call = service.patchVimeoVideoMetaData(ticket2, ticket3, tick, str1, redirect_url123 + "www.voicesnapforschools.com", requestFile);
//        Call<JsonObject> call = service.patchVimeoVideoMetaData(requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showAlertfinal("Video sending failed.Retry", "0");


            }
        });


    }
}
