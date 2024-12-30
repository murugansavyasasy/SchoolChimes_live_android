package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentSubmissionListActivity extends AppCompatActivity {
      RecyclerView recyclerView;
      ImageView imgBack;
      RelativeLayout relativeLayout;
      StudentListAdapter studentListAdapter;
      String id,type,userType,userID;
    private ArrayList<Studentclass> studentlist = new ArrayList<>();
    String MobileNumber,schoolID;
    Boolean is_Archive;
    String isNewVersion;

    Boolean IsArchice;
    ImageView imgSearch;
    EditText Searchable;
    ImageView adImage;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_assignment);
        recyclerView = findViewById(R.id.recyclerView);
        imgBack = findViewById(R.id.imgBack);
        relativeLayout = findViewById(R.id.layout_tittle);
        imgBack.setVisibility(View.VISIBLE);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        adImage = (ImageView) findViewById(R.id.adImage);

        adImage.setVisibility(View.GONE);


         id=getIntent().getExtras().getString("ID","");
         type=getIntent().getExtras().getString("TYPE","");
         userType=getIntent().getExtras().getString("USER_TYPE","");
        onBackControl();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackControl();
                onBackPressed();
            }
        });

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (studentListAdapter == null)
                    return;

                if (studentListAdapter.getItemCount() < 1) {
                    recyclerView.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.VISIBLE);
                }
                filterlist(editable.toString());
            }
        });

        is_Archive=getIntent().getExtras().getBoolean("is_Archive",false);
        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StudentSubmissionListActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(StudentSubmissionListActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);

        studentListAdapter = new StudentListAdapter(StudentSubmissionListActivity.this, studentlist,id);
        recyclerView.setAdapter(studentListAdapter);
    }

    private void filterlist(String s) {
        List<Studentclass> temp = new ArrayList();
        for (Studentclass d : studentlist) {

            if (d.getMessage().toLowerCase().contains(s.toLowerCase()) || d.getStudentname().toLowerCase().contains(s.toLowerCase()) || d.getStandard().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        studentListAdapter.updateList(temp);
    }

    private void onBackControl() {
        if(userType.equals("parent")){
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(StudentSubmissionListActivity.this,"1");
        }
        else {
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(StudentSubmissionListActivity.this,"1");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        StudentList();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackControl();
    }

    private void StudentList() {
        final ProgressDialog mProgressDialog = new ProgressDialog(StudentSubmissionListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StudentSubmissionListActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StudentSubmissionListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StudentSubmissionListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        if(userType.equals("parent")){
             userID= Util_SharedPreference.getChildIdFromSP(StudentSubmissionListActivity.this);
             schoolID=Util_SharedPreference.getSchoolIdFromSP(StudentSubmissionListActivity.this);
             MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(StudentSubmissionListActivity.this);
        }
        else {
            userID= TeacherUtil_Common.Principal_staffId;
            schoolID=TeacherUtil_Common.Principal_SchoolId;
            MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(StudentSubmissionListActivity.this);

        }

        JsonObject object = new JsonObject();
        object.addProperty("AssignmentId",id);
        object.addProperty("ProcessBy",userID);
        object.addProperty("Type",type);
        object.addProperty("SchoolID", schoolID);
        object.addProperty("MobileNumber", MobileNumber);
        Log.d("view:req", object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call ;

        if(isNewVersion.equals("1") && is_Archive){
            call = apiService.GetAssignmentMemberCount_Archive(object);
            IsArchice=is_Archive;
        }
        else {
            call = apiService.GetAssignmentMemberCount(object);
            IsArchice=false;
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    Log.d("Upload-Code:Response", response.code() + "-" + response);

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    mProgressDialog.dismiss();
                    try {
                        studentlist.clear();
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String studentid = jsonObject.getString("StudentId");
                                String Studentname = jsonObject.getString("Studentname");
                                if (!studentid.equals("0")) {

                                    String Standard = jsonObject.getString("Standard");
                                    String Section = jsonObject.getString("Section");
                                    String Message = jsonObject.getString("Message");
                                    Studentclass report = new Studentclass(studentid, Studentname, Standard, Section, Message,IsArchice);
                                    studentlist.add(report);
                                }
                                else{
                                    String Message = jsonObject.getString("Message");
                                    alert(Message);
                                }
                            }

                            studentListAdapter.notifyDataSetChanged();
                        }
                        else{
                                alert("No Records Found");
                        }


                    } catch (Exception e) {
                        Log.e("Response Exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Log.e("Response Failure", t.getMessage());
                showToast("Server Connection Failed");
            }


        });
    }


    private void showToast(String msg) {
        Toast.makeText(StudentSubmissionListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentSubmissionListActivity.this);


            alertDialog.setTitle(R.string.alert);
            alertDialog.setMessage(strStudName);
            alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            alertDialog.show();

    }
}
