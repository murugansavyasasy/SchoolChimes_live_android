package com.vs.schoolmessenger.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.StaffChatListAdapter;
import com.vs.schoolmessenger.assignment.ParentSubmitActivity;
import com.vs.schoolmessenger.databinding.ActivityStaffListBinding;
import com.vs.schoolmessenger.interfaces.SubjectSelectedListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StaffList;
import com.vs.schoolmessenger.model.StaffListChat;
import com.vs.schoolmessenger.model.SubjectDetail;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffListActivity extends AppCompatActivity {
    ActivityStaffListBinding binding;
    StaffListChat staffList;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_staff_list);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        staffListApi();
    }

    public void staffListApi() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StaffListActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StaffListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StaffListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String childID= Util_SharedPreference.getChildIdFromSP(StaffListActivity.this);
        String schoolid= Util_SharedPreference.getSchoolIdFromSP(StaffListActivity.this);
        Log.d("parentschlid",schoolid);
          String Code= TeacherUtil_SharedPreference.getShCountryCode(StaffListActivity.this);
          String mobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(StaffListActivity.this);
        final JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("CountryCode", Code);
        jsonObject.addProperty("memberid", childID);
        jsonObject.addProperty("SchoolID", schoolid);
        jsonObject.addProperty("MobileNumber", mobileNumber);
        Log.d("reqstafflist",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.staffList(jsonObject);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if(response.code()==200 || response.code()==201) {

                    try {
                        Log.d("Response", response.body().toString());
                        Gson gson = new Gson();
                        staffList = gson.fromJson(response.body(), StaffListChat.class);
                        SubjectDetail subjectDetail = new SubjectDetail();
                        subjectDetail.staffname = staffList.classteacher;
                        subjectDetail.StaffID = staffList.ClassTeacherID;
                        subjectDetail.subjectname = Constants.CLASS_TEACHER;
                        subjectDetail.SubjectID = "";
                        staffList.subjectdetails.add(0, subjectDetail);

                        StaffChatListAdapter adapter = new StaffChatListAdapter(staffList.subjectdetails, new SubjectSelectedListener() {
                            @Override
                            public void click(SubjectDetail staffDetail) {
                                Intent intent = new Intent(StaffListActivity.this, StudentChatActivity.class);
                                intent.putExtra(Constants.CLASS_TEACHER_ID, staffList.ClassTeacherID);
                                intent.putExtra(Constants.SECTION_ID, staffList.SectionID);
                                intent.putExtra(Constants.SUBJECT_DETAIL, new Gson().toJson(staffDetail));
                                startActivity(intent);
                            }
                        });
                        binding.staffList.setAdapter(adapter);

                    }
                    catch (Exception e) {
                        Log.e("TextMsg:Exception", e.getMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(StaffListActivity.this, msg, Toast.LENGTH_SHORT).show();

    }
}
