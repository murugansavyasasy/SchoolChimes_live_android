package com.vs.schoolmessenger.activity;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VIDEO_GALLERY;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

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

public class ToStaffGroupList extends AppCompatActivity {
    GridLayout  gridlayoutgroups;
    CheckBox[]  cbListGroups;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;
    Button btnConfirm;
    String SchoolID, StaffID;
    int iSelStdGrpCount = 0;
    private int iRequestCode = 0;
    String filepath, tittle, strmessage, strdate, strtime, strfilepathimage;
    String duration;
    ImageView genTextPopup_ToolBarIvBack;

    String fileNameDateTime;
    S3Uploader s3uploaderObj;

    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType="";
    String uploadFilePath="";
    int pathIndex=0;
    private  ArrayList<String> UploadedS3URlList = new ArrayList<>();

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    TextView lblStandard;
    String  strPDFFilepath,strsize;

    String upload_link,link,iframe;
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.to_staff_groups);

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
        Log.d("FILEPATH", filepath);
        strsize = getIntent().getExtras().getString("FILE_SIZE", "");

        s3uploaderObj = new S3Uploader(ToStaffGroupList.this);

        strPDFFilepath = getIntent().getExtras().getString("FILE_PATH_PDF", "");
        if (strPDFFilepath.equals("")) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }

        if (TeacherUtil_Common.listschooldetails.size() == 1) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }

        String countryID = TeacherUtil_SharedPreference.getCountryID(ToStaffGroupList.this);
        if(countryID.equals("11")){
            lblStandard.setText("Grades");
        }
        btnConfirm = (Button) findViewById(R.id.sectiongroup_btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msgTypeAndAPIs();
            }
        });
        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        gridlayoutgroups = (GridLayout) findViewById(R.id.gridlayout_groups);
        LayoutInflater inflater = getLayoutInflater();
        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();

        getStaffGroups();
    }

    private void getStaffGroups() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(ToStaffGroupList.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ToStaffGroupList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ToStaffGroupList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetStaffGroups(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("staffgroups:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("staffgroups:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", js.length() + "");
                        TeacherClassGroupModel classgrp;
                        cbListGroups = new CheckBox[js.length()];
                            for (int j = 0; j < js.length(); j++) {
                                JSONObject jsonObjectgroups = js.getJSONObject(j);
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
                                            listGroups.get(finalI).setbSelected(isChecked);
                                            if (isChecked)
                                                iSelStdGrpCount++;
                                            else iSelStdGrpCount--;
                                        }
                                    });
                                } else {
                                    showErrorAlert(GroupName);
                                    //showToast(GroupName);
                                }
                            }


                    } else {
                        showErrorAlert(getResources().getString(R.string.no_records));
                        // showToast(getResources().getString(R.string.no_records));
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

    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            jsonObjectSchool.addProperty("StaffId", StaffID);
            Log.d("req",jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
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

    private void msgTypeAndAPIs() {
        switch (iRequestCode) {

            case STAFF_VOICE:
                if(iSelStdGrpCount>0) {
                    SendVoiceAsStaffToGroups();
                }
                else {
                    showToast(getResources().getString(R.string.select_participants));

                }
                break;

            case STAFF_TEXT:
                if(iSelStdGrpCount>0) {
                    SendSMSAsStaffToGroups();
                }
                else {
                    showToast(getResources().getString(R.string.select_participants));
                }
                break;

            case STAFF_PHOTOS:

                if(iSelStdGrpCount>0) {
                    if (!strPDFFilepath.equals("")) {
                        contentType="application/pdf";
                        slectedImagePath.clear();
                        slectedImagePath.add(strPDFFilepath);
                        UploadedS3URlList.clear();
                        uploadFileToAWSs3(pathIndex,".pdf");
                    } else {
                        slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
                        contentType="image/png";
                        UploadedS3URlList.clear();
                        uploadFileToAWSs3(pathIndex,"IMG");
                    }
                }
                else {
                    showToast(getResources().getString(R.string.select_participants));
                }

                break;

            case VIDEO_GALLERY:
                if(iSelStdGrpCount>0) {
                    VimeoAPi();
                }
                else {
                    showToast(getResources().getString(R.string.select_participants));

                }

                break;

            default:
                break;
        }
    }

    private void VimeoAPi() {

        Log.d("StrVideoSize",strsize);
        Log.d("Videofilepath",filepath);

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
        final ProgressDialog mProgressDialog = new ProgressDialog(ToStaffGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);


        JsonObject object = new JsonObject();

        JsonObject jsonObjectclasssec = new JsonObject();
        jsonObjectclasssec.addProperty("approach","post");
        jsonObjectclasssec.addProperty("size",String.valueOf(strsize) );

        JsonObject jsonprivacy = new JsonObject();
        jsonprivacy.addProperty("view","unlisted");

        JsonObject jsonshare = new JsonObject();
        jsonshare.addProperty("share","false");

        JsonObject jsonembed = new JsonObject();
        jsonembed.add("buttons",jsonshare);

        object.add("upload", jsonObjectclasssec);
        object.addProperty("name",tittle);
        object.addProperty("description",strmessage);
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);

        String head="Bearer "+TeacherUtil_SharedPreference.getVideotoken(ToStaffGroupList.this);
        Log.d("video_token",head);
        Call<JsonObject> call = service.VideoUpload(object,head);
        Log.d("jsonOBJECT", object.toString());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try","testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        Log.d("Response sucess", object1.toString());
                        JSONObject obj = object1.getJSONObject("upload");
                        JSONObject obj1 = object1.getJSONObject("embed");
                        upload_link = obj.getString("upload_link");
                        link = object1.getString("link");
                        iframe = obj1.getString("html");
                        Log.d("c", upload_link);
                        Log.d("iframe", iframe);

                        String[] separated = upload_link.split("\\?(?!\\?)");

                        String name = separated[0];  //"/"
                        Log.d("name", name);

                        String FileName = separated[1];
                        Log.d("FileName", FileName);

                        String upload = name.replace("upload", "");
                        Log.d("replace", upload);

                        try{
                            VIDEOUPLOAD(upload_link);
                        }catch (Exception e) {
                            Log.e("VIMEO Exception", e.getMessage());
                            showAlertfinal("Video sending failed.Retry","0");
                        }


                    } catch (Exception e) {
                        Log.e("VIMEO Exception", e.getMessage());
                        showAlertfinal(e.getMessage(),"0");
                    }

                } else {
                    Log.d("Response fail", "fail");
                    showAlertfinal("Video sending failed.Retry","0");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                showAlertfinal("Video sending failed.Retry","0");

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

        String ticket = seperate1[0];
        Log.d("sp1", ticket);

        String ticket2 = seperate1[1];
        Log.d("ticket2", ticket2);


        String[] seperate2 = video_file_id.split("=");

        String ticket1 = seperate2[0];
        Log.d("sp2", ticket1);

        String ticket3 = seperate2[1];
        Log.d("tic", ticket3);

        String[] seper = signature.split("=");

        String ticke = seper[0];
        Log.d("sp3", ticke);

        String tick = seper[1];
        Log.d("signature_number", tick);

        String[] sepera = v6.split("=");

        String str = sepera[0];
        Log.d("str", str);

        String str1 = sepera[1];
        Log.d("v6123", str1);

        String[] sucess = redirect_url.split("=");

        String urlRIDERCT = sucess[0];
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

        final ProgressDialog mProgressDialog = new ProgressDialog(ToStaffGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        RequestBody requestFile = null;

        try {
            InputStream in = new FileInputStream(filepath);

            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            requestFile = RequestBody.create(MediaType.parse("application/offset+octet-stream"), buf);

        } catch (IOException e) {
            showAlertfinal(e.getMessage(),"0");
        }

        Call<ResponseBody> call = service.patchVimeoVideoMetaData(ticket2,ticket3,tick,str1,redirect_url123+"www.voicesnapforschools.com", requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        SendVideoAsStaffToGroups();
                    }
                    else{
                        showAlertfinal("Video sending failed.Retry","0");
                    }
                } catch (Exception e) {
                    showAlertfinal(e.getMessage(),"0");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showAlertfinal("Video sending failed.Retry","0");
            }
        });
    }

    private void showAlertfinal(String msg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ToStaffGroupList.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(ToStaffGroupList.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                }
                else{
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

    private void uploadFileToAWSs3(int pathind, final String fileType) {

        pathIndex=pathind;
        progressDialog = new ProgressDialog(ToStaffGroupList.this);
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
                                    SendMultipleImagePDFAsStaffToGroupsWithCloudURL(fileType);

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

    private void SendVoiceAsStaffToGroups() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ToStaffGroupList.this);
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

        JsonObject jsonReqArray = sendVoiceStaffGroups();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(ToStaffGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceAsStaffToGroups(requestBody, bodyFile);
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
                                showAlert(strStatus,strMsg);
                            } else {
                                showAlert(strStatus,strMsg);
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

    private JsonObject sendVoiceStaffGroups() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("Duration", duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

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
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendSMSAsStaffToGroups() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ToStaffGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(ToStaffGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = sendStaffGroupSMS();
        Call<JsonArray> call = apiService.SendSMSAsStaffToGroups(jsonReqArray);
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
                                showAlert(strStatus,strMsg);
                            } else {
                                showAlert(strStatus,strMsg);
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

    private JsonObject sendStaffGroupSMS() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);

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
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void showAlert(String status,String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ToStaffGroupList.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(ToStaffGroupList.this, Teacher_AA_Test.class);
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

    private void showErrorAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ToStaffGroupList.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void SendMultipleImagePDFAsStaffToGroupsWithCloudURL(String fileType) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ToStaffGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = sendPdfImageStaffGroups(fileType);
        Call<JsonArray> call = apiService.SendMultipleImagePDFAsStaffToGroupsWithCloudURL(jsonReqArray);
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
                                showAlert(strStatus,strMsg);
                            } else {
                                showAlert(strStatus,strMsg);
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

    private JsonObject sendPdfImageStaffGroups(String fileType) {
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

    private void SendVideoAsStaffToGroups() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ToStaffGroupList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(ToStaffGroupList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = sendVideoStaffGroups();
        Call<JsonArray> call = apiService.SendVideoAsStaffToGroups(jsonReqArray);
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
                            String strStatus = jsonObject.getString("result");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlertfinal(strMsg,strStatus);

                            } else {
                                showAlertfinal(strMsg,strStatus);
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

    private JsonObject sendVideoStaffGroups() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("Title", tittle);
            jsonObjectSchoolstdgrp.addProperty("Description", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SchoolId",Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",Principal_staffId );
            jsonObjectSchoolstdgrp.addProperty("Iframe", iframe);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolstdgrp.addProperty("URL", link);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd1 = new JsonArray();
            for (int j = 0; j < listGroups.size(); j++) {
                if (listGroups.get(j).isbSelected()) {
                    JsonObject jsonObjectclass1 = new JsonObject();
                    jsonObjectclass1.addProperty("TargetCode", listGroups.get(j).getStrID());
                    Log.d("schoolid", listGroups.get(j).getStrID());
                    jsonArrayschoolstd1.add(jsonObjectclass1);
                }
            }
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolstd1);
            Log.d("reqVideo",jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchoolstdgrp;
    }

}

