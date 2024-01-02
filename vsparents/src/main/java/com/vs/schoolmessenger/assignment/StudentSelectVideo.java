package com.vs.schoolmessenger.assignment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.adapter.TeacherStudendListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStudentListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
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

public class StudentSelectVideo extends AppCompatActivity implements TeacherOnCheckStudentListener {
    RecyclerView rvStudentList;
    private TeacherStudendListAdapter adapter;
    TextView tvOK, tvCancel, tvSelectedCount, tvTotalCount, tvStdSec, tvSubject; //tvSelectUnselect
    private ArrayList<TeacherStudentsModel> studentList = new ArrayList<>();

    String schoolID, targetCode,stdcode,subcode,strtittle;
    TeacherSectionModel selSection;

    private int i_students_count;

    CheckBox cbSelectAll,chUnselect;
    String schoolId, sectioncode,filepath,duration,Description,strmessage;
    int iRequestCode;

    String voicetype="",strPDFFilepath;

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String sectionsTargetCode="";
    String AssignId="",strDate="",iframe,link,upload_link;
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url,strsize;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_student_assignment);




        schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(StudentSelectVideo.this);
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
        strsize = getIntent().getExtras().getString("FILE_SIZE", "");


        if(iRequestCode==STAFF_IMAGEASSIGNMENT){
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }

        targetCode = sectioncode;
        rvStudentList = (RecyclerView) findViewById(R.id.attenStudent_rvStudentList);
        adapter = new TeacherStudendListAdapter(StudentSelectVideo.this, this, studentList,0);

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
        chUnselect = (CheckBox) findViewById(R.id.chUnselect);
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
        chUnselect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < studentList.size(); i++) {
                    studentList.get(i).setSelectStatus(isChecked);
                }

                adapter.notifyDataSetChanged();

                if (isChecked) {
                    i_students_count = studentList.size();
                    chUnselect.setVisibility(View.GONE);
                    cbSelectAll.setVisibility(View.VISIBLE);
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

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StudentSelectVideo.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StudentSelectVideo.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StudentSelectVideo.this);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSelectVideo.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



            }
        });

        alertDialog.show();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void student_addClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count++;
            enableDisableNext();
            Log.d("size", String.valueOf(studentList.size()));
            Log.d("count", String.valueOf(i_students_count));
            if(i_students_count<studentList.size()){
                cbSelectAll.setVisibility(View.GONE);
                chUnselect.setVisibility(View.VISIBLE);
                chUnselect.setButtonTintList(ColorStateList.valueOf(R.color.clr_white));
            }
            else{
                cbSelectAll.setVisibility(View.VISIBLE);
                chUnselect.setVisibility(View.GONE);
            }

        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void student_removeClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count--;
            enableDisableNext();
            Log.d("sizeremove", String.valueOf(studentList.size()));
            Log.d("countremove", String.valueOf(i_students_count));
            if(i_students_count<studentList.size()){
                cbSelectAll.setVisibility(View.GONE);
                chUnselect.setVisibility(View.VISIBLE);
                chUnselect.setButtonTintList(ColorStateList.valueOf(R.color.clr_white));
            }
            else{
                cbSelectAll.setVisibility(View.VISIBLE);
                chUnselect.setVisibility(View.GONE);
            }

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
                StudentSelectVideo.this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to send the Video");


        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });

        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

              VimeoAPi();

            }
        });

        alertDialog.show();
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

    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSelectVideo.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {



                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(StudentSelectVideo.this, Teacher_AA_Test.class);
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

    private JsonObject JsonVideotoSection() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("Title", strtittle);
            jsonObjectSchoolstdgrp.addProperty("Description", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SchoolId",Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy",Principal_staffId );
            jsonObjectSchoolstdgrp.addProperty("Iframe", iframe);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolstdgrp.addProperty("URL", link);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            JsonArray jsonArrayschoolstd = new JsonArray();


            for (int i = 0; i < studentList.size(); i++) {
                if (studentList.get(i).isSelectStatus()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("ID", studentList.get(i).getStudentID());
                    Log.d("schoolid", studentList.get(i).getStudentID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }
            jsonObjectSchoolstdgrp.add("IDS", jsonArrayschoolstd);
            Log.d("reqVideo",jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    void SendVideotoSecApi() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(StudentSelectVideo.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectVideo.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = JsonVideotoSection();
        Call<JsonArray> call = apiService.SendVideoAsStaffToSpecificStudents(jsonReqArray);
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


    private void VimeoAPi() {

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
        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectVideo.this);
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
        object.addProperty("name",strtittle);
        object.addProperty("description",strmessage);
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);

        String head="Bearer "+TeacherUtil_SharedPreference.getVideotoken(StudentSelectVideo.this);
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
                }
                else {
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

                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.MINUTES)
                .writeTimeout(40, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl(upload)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSelectVideo.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
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
                        SendVideotoSecApi();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSelectVideo.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(StudentSelectVideo.this, Teacher_AA_Test.class);
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

}

