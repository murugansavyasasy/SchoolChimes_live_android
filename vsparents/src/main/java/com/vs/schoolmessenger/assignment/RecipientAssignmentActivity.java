package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;

import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.adapter.TeacherNewSectionsListAdapter;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSectionListListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.Constants.category_id;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_IMAGEASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PDFASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXTASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICEASSIGNMENT;


public class RecipientAssignmentActivity extends AppCompatActivity {
    Spinner spinStandard, spinSection, spinSubject;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSubject;
    Button btnSend, btnSelectStudents;

    List<String> listStd = new ArrayList<>();
    List<TeacherSectionsListNEW> arrSectionCollections;

    ImageView genTextPopup_ToolBarIvBack;
    private ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();
    String strStdName, strSecName, strSecCode, strSubjectCode="", strSubjectName, strTotalStudents,strDate="";
    TeacherSectionModel stdSec;

    private int iRequestCode = 0;
    String strToWhom = "";
    String  strtittle, strmessage, filepath;

    RecyclerView rvSectionList;
    private ArrayList<TeacherSectionsListNEW> seletedSectionsList = new ArrayList<>();
    private int i_sections_count = 0;
    List<String> listSubjectName= new ArrayList<>();
    List<String> listSubjectCode= new ArrayList<>();

    Button btnSelectSubjects;
    ArrayList<TeacherSubjectModel> listSubjects1 = new ArrayList<TeacherSubjectModel>();

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    ArrayList<TeacherSubjectModel> SubjectsList = new ArrayList<TeacherSubjectModel>();

    Button btnGetSubject;
    TextView lblSubject;
    String sectionsTargetCode="";
    String AssignId="";
    String fileNameDateTime;

    String urlFromS3 = null;
    ProgressDialog progressDialog;
    String contentType="";

    String uploadFilePath="";
    int pathIndex=0;

    TextView staffStdSecSub_tv1;
    private  ArrayList<String> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_recipient_assignment);
        btnGetSubject = (Button) findViewById(R.id.btnGetSubject);
        staffStdSecSub_tv1 = (TextView) findViewById(R.id.staffStdSecSub_tv1);


        String countryID = TeacherUtil_SharedPreference.getCountryID(RecipientAssignmentActivity.this);
        if(countryID.equals("11")){
            staffStdSecSub_tv1.setText("Grades");
        }
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        isAwsUploadingPreSigned = new AwsUploadingPreSigned();
        strToWhom ="SEC";
        strtittle = getIntent().getExtras().getString("TITLE", "");
        strmessage = getIntent().getExtras().getString("CONTENT", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        AssignId = getIntent().getExtras().getString("ID", "");
        strDate = getIntent().getExtras().getString("DATE", "");
             if(!AssignId.equals("")){
                 btnGetSubject.setVisibility(View.GONE);
             }
             else{
                 btnGetSubject.setVisibility(View.VISIBLE);
             }
          if(iRequestCode==STAFF_IMAGEASSIGNMENT){
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
          }
          genTextPopup_ToolBarIvBack=(ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(RecipientAssignmentActivity.this,"1");
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(RecipientAssignmentActivity.this,"1");
                onBackPressed();
            }
        });
        btnSelectStudents = (Button) findViewById(R.id.staffStdSecSub_btnToStudents);
        btnSelectStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strSubjectCode.equals("")&& AssignId.equals("")){
                    alert("Please Choose Subject");
                }
                else if(strSubjectCode.equals("0")){
                    alert("No subject to create assignment");
                }
                else{
                    strSecCode=seletedSectionsList.get(0).getStrSectionCode();
                    strSecName=seletedSectionsList.get(0).getStrSectionName();

                    TeacherSectionModel stdSec = new TeacherSectionModel(false, strStdName, strSecName, strSecCode, "", "", strTotalStudents, "0", false);
                    Intent inStud = new Intent(RecipientAssignmentActivity.this, StudentSelectAssignment.class);

                    Log.d("secname",strSecName);
                    Log.d("secid",strSecCode);
                    inStud.putExtra("STD_SEC", stdSec);
                    inStud.putExtra("REQUEST_CODE", iRequestCode);
                    inStud.putExtra("ID", AssignId);
                    inStud.putExtra("SECCODE", strSecCode);
                    inStud.putExtra("SUBCODE", strSubjectCode);

                    inStud.putExtra("PATH_LIST", slectedImagePath);
                    inStud.putExtra("TITLE", strtittle);
                    inStud.putExtra("CONTENT", strmessage);
                    inStud.putExtra("FILEPATH", filepath);
                    inStud.putExtra("DATE", strDate);
                    inStud.putExtra("category", String.valueOf(category_id));

                    startActivityForResult(inStud, iRequestCode);
                }
            }
        });

        btnSend = (Button) findViewById(R.id.staffStdSecSub_btnSend);
        btnSelectSubjects = (Button) findViewById(R.id.btnSelectSubjects);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(strSubjectCode.equals("") && AssignId.equals("")){
                    alert("Please Choose Subject");
                }
                else if(strSubjectCode.equals("0")){
                    alert("No subject to create assignment");
                }
                else {
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
                        showLoading();
                        isUploadAWS("image", "IMG", "","IMAGE");

                    }
                    if (iRequestCode == STAFF_PDFASSIGNMENT) {
                        //SendPdfTOSectionApi();s
                        contentType="application/pdf";
                        slectedImagePath.clear();
                        slectedImagePath.add(filepath);
                        UploadedS3URlList.clear();
                        showLoading();
                        isUploadAWS("pdf", ".pdf", "","PDF");

                    }
                }


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
                if(seletedSectionsList.size()>0) {
                    getSubjectsApi();
                }
                else {
                    showToast(getResources().getString(R.string.selecte_atleast_one_section));
                }
            }
        });



        standardsAndSectoinsListAPI1();

        spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());

                if (strToWhom.equals("SEC")) {
                    strStdName = listStd.get(position);
                    listSubjects1.clear();
                    seletedSectionsList.clear();
                    listSectionsForSelectedStandard();

                }
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


    private void isUploadAWS(String contentType, String isType, String value,String fileType) {

        Log.d("selectedImagePath", String.valueOf(slectedImagePath.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();
        for (int i = 0; i < slectedImagePath.size(); i++) {
            AwsUploadingFile(String.valueOf(slectedImagePath.get(i)),  TeacherUtil_Common.Principal_SchoolId, contentType, isType, value,fileType);
        }
    }

    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type,String fileTypeNode) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(RecipientAssignmentActivity.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this,countryID,true,false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);

                if (UploadedS3URlList.size() == slectedImagePath.size()) {
                    ManageAssignmentFromAppWithCloudURL(fileTypeNode);
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }

    private void ManageAssignmentFromAppWithCloudURL(String fileType) {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
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

        String id = "";
        for (int i = 0; i < seletedSectionsList.size(); i++) {
            sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
            id = sectionsTargetCode;
            Log.d("values", sectionsTargetCode);
        }
        String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

        Log.d("UploadFileList",String.valueOf(UploadedS3URlList.size()));
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("AssignmentId", "0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType", fileType);
            jsonObjectSchoolstdgrp.addProperty("Title", strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId", targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection", "1");
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple", isMultiple);
            jsonObjectSchoolstdgrp.addProperty("processType", "add");
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", String.valueOf(category_id));

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

    //IMAGE

    private void showLoading() {
        if (progressDialog == null) {
            // Initialize the ProgressDialog if it hasn't been created yet
            progressDialog = new ProgressDialog(this); // Replace 'this' with your Context if not in an Activity
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getResources().getString(R.string.Loading));
            progressDialog.setCancelable(false);
        }

        // Show the ProgressDialog if it is not already showing
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        } else {
            Log.d("ProgressBar", "ProgressDialog is already showing");
        }
    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipientAssignmentActivity.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }

    private void ForwardApi(String id) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientAssignmentActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String secids = "";
        for (int i = 0; i < seletedSectionsList.size(); i++) {
            sectionsTargetCode = secids+seletedSectionsList.get(i).getStrSectionCode()+ "~";
            secids = sectionsTargetCode;
            Log.d("values", sectionsTargetCode);
        }
        String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("FromAssignmentId", id);
        jsonObject.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        jsonObject.addProperty("receiverId",targetCode);
        jsonObject.addProperty("isentireSection", "1");
        jsonObject.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
        Log.d("TextMsg:Req", jsonObject.toString());
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

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
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


        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientAssignmentActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
                id = sectionsTargetCode;
                Log.d("values", sectionsTargetCode);
            }
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","PDF");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","1");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);

            Log.d("pdf:req", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }
    private void SendMessageTOSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = constructJsonArrayMessageHW();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());


        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientAssignmentActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
                id = sectionsTargetCode;
                Log.d("values", sectionsTargetCode);
            }
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","SMS");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","1");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", String.valueOf(category_id));

            Log.d("message:req", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendVoiceTOSectionApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
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


        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientAssignmentActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
                id = sectionsTargetCode;
                Log.d("values", sectionsTargetCode);
            }
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","VOICE");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","1");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","0");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);
            jsonObjectSchoolstdgrp.addProperty("category", String.valueOf(category_id));

            Log.d("voice:req", jsonObjectSchoolstdgrp.toString());


        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void SendImageToSectionApi() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
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
        final ProgressDialog mProgressDialog = new ProgressDialog(RecipientAssignmentActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
            for (int i = 0; i < seletedSectionsList.size(); i++) {
                sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
                id = sectionsTargetCode;
                Log.d("values", sectionsTargetCode);
            }
            String targetCode = sectionsTargetCode.substring(0, sectionsTargetCode.length() - 1);

            jsonObjectSchoolstdgrp.addProperty("AssignmentId ","0");
            jsonObjectSchoolstdgrp.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("AssignmentType","IMAGE");
            jsonObjectSchoolstdgrp.addProperty("Title",strtittle);
            jsonObjectSchoolstdgrp.addProperty("content", strmessage);
            jsonObjectSchoolstdgrp.addProperty("receiverId",targetCode);
            jsonObjectSchoolstdgrp.addProperty("isentireSection","1");
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",TeacherUtil_Common.Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("isMultiple","1");
            jsonObjectSchoolstdgrp.addProperty("processType","add");
            jsonObjectSchoolstdgrp.addProperty("Duration", "0");
            jsonObjectSchoolstdgrp.addProperty("SubCode", strSubjectCode);
            jsonObjectSchoolstdgrp.addProperty("EndDate", strDate);

            Log.d("image:req", jsonObjectSchoolstdgrp.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void getSubjectsApi() {
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(RecipientAssignmentActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(RecipientAssignmentActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
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
            sectionsTargetCode = id+seletedSectionsList.get(i).getStrSectionCode()+ "~";
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
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
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
                            Log.d("entersubject","entersubjects");
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String SubjectID = jsonObject.getString("SubjectID");
                                String SubjectName = jsonObject.getString("SubjectName");
                                TeacherSubjectModel subjectList;
                                subjectList = new TeacherSubjectModel(SubjectName,SubjectID,false);
                                SubjectsList.add(subjectList);

                                listSubjectName.add(SubjectName);
                                listSubjectCode.add(SubjectID);
                            }
                            adaSubject = new ArrayAdapter<>(RecipientAssignmentActivity.this, R.layout.teacher_spin_title, listSubjectName);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(RecipientAssignmentActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(RecipientAssignmentActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(RecipientAssignmentActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStdHW();
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
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
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

                                            subjectList = new TeacherSubjectModel(jObjSub.getString("SubjectName"), jObjSub.getString("SubjectId"),false);
                                            listSubjects.add(subjectList);

                                        }

                                    }

                                    stdSecList.setListSectionsNew(listSections);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }

                                adaStd = new ArrayAdapter<>(RecipientAssignmentActivity.this, R.layout.teacher_spin_title, listStd);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("StdSecList:Failure", t.toString());
                onBackPressed();
            }
        });
    }



    private void standardsAndSectoinsListHWAPI() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStdHW();

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
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
                                                listSections.add(sectionsList);
                                            } else {
                                                sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"),
                                                        "", false);
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

                                            subjectList = new TeacherSubjectModel(jObjSub.getString("SubjectName"), jObjSub.getString("SubjectId"),false);
                                            listSubjects.add(subjectList);

                                        }

                                    }

                                    stdSecList.setListSectionsNew(listSections);
                                    stdSecList.setListSubjects(listSubjects);
                                    arrStandardsAndSectionsList.add(stdSecList);
                                }

                                adaStd = new ArrayAdapter<>(RecipientAssignmentActivity.this, R.layout.teacher_spin_title, listStd);
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

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
        TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(RecipientAssignmentActivity.this,"1");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
//                finish();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RecipientAssignmentActivity.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(RecipientAssignmentActivity.this, Teacher_AA_Test.class);
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



        TeacherNewSectionsListAdapter newSectionsListAdapter = new TeacherNewSectionsListAdapter(RecipientAssignmentActivity.this, new TeacherOnCheckSectionListListener() {
            @Override
            public void section_addSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (!seletedSectionsList.contains(sectionsListNEW))) {


                    seletedSectionsList.add(sectionsListNEW);
                    i_sections_count++;
                    enableDisableNext();
//                    strSecCode=sectionsListNEW.getStrSectionCode();
//                    strSecName=sectionsListNEW.getStrSectionName();
//                    Log.d("secname",strSecName);
//                    Log.d("secid",strSecCode);
                    if(i_sections_count==1){
//                        strSecCode=seletedSectionsList.get(0).getStrSectionCode();
//                        strSecName=seletedSectionsList.get(0).getStrSectionName();
//                        Log.d("secname",strSecName);
//                        Log.d("secid",strSecCode);
                        btnSelectStudents.setVisibility(View.VISIBLE);
                    }
                    else{
                        btnSelectStudents.setVisibility(View.GONE);

                }
                }
            }

            @Override
            public void section_removeSection(TeacherSectionsListNEW sectionsListNEW) {
                if ((sectionsListNEW != null) && (seletedSectionsList.contains(sectionsListNEW))) {
                    seletedSectionsList.remove(sectionsListNEW);
                    i_sections_count--;
                    if(i_sections_count==1){
//                        strSecCode=seletedSectionsList.get(0).getStrSectionCode();
//                        strSecName=sectionsListNEW.getStrSectionName();
                        btnSelectStudents.setVisibility(View.VISIBLE);


                    }
                    else{
                        btnSelectStudents.setVisibility(View.GONE);

                    }
                    enableDisableNext();
                }
            }
        }, arrSectionCollections);

        rvSectionList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(RecipientAssignmentActivity.this);
        rvSectionList.setLayoutManager(layoutManager);
        rvSectionList.addItemDecoration(new DividerItemDecoration(RecipientAssignmentActivity.this, LinearLayoutManager.VERTICAL));
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
        }
        else{
            btnSend.setEnabled(false);
            btnSelectSubjects.setEnabled(false);
        }
    }

}

