package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SubjectChatListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.databinding.ActivitySubjectListBinding;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherSelectListener;
import com.vs.schoolmessenger.model.Subject;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class SubjectListActivity extends AppCompatActivity {
    ActivitySubjectListBinding binding;
    String staffId;
    String comeFrom;
    AlertDialog alertDialog;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_subject_list);
        Intent intent = getIntent();
        staffId = intent.getStringExtra(Constants.STAFF_ID);
        comeFrom = intent.getStringExtra(Constants.COME_FROM);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("onResume", "onResume");
        subjectListApi();

    }

    public void subjectListApi() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(SubjectListActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(SubjectListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SubjectListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String schoolID = TeacherUtil_Common.Principal_SchoolId;
        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(SubjectListActivity.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StaffId", staffId);
        jsonObject.addProperty("SchoolID", schoolID);
        jsonObject.addProperty("MobileNumber", MobileNumber);
        Log.d("reqsublist", jsonObject.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.subjectList(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        Gson gson = new Gson();
                        Type userListType = new TypeToken<ArrayList<Subject>>() {
                        }.getType();
                        ArrayList<Subject> subjects = gson.fromJson(response.body(), userListType);
                        Log.d("Subjetcs", String.valueOf(subjects));
                        if (!subjects.isEmpty()) {
                            SubjectChatListAdapter adapter = new SubjectChatListAdapter(subjects, new TeacherSelectListener() {
                                @Override
                                public void click(Subject subject) {
                                    Intent intent = new Intent(SubjectListActivity.this, TeacherChatActivity.class);
                                    intent.putExtra(Constants.SUBJECT, new Gson().toJson(subject));
                                    intent.putExtra(Constants.STAFF_ID, staffId);
                                    intent.putExtra(Constants.COME_FROM, comeFrom);
                                    startActivity(intent);
                                }

                                @Override
                                public void showToast(String message) {
                                    alertDialog("No records found", "Ok");
                                }
                            });
                            binding.subjectList.setAdapter(adapter);
                        } else {
                            binding.subjectList.setVisibility(View.GONE);
                            binding.noRecords.setVisibility(View.VISIBLE);
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
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertDialog(String message, String positiveText) {
        if (alertDialog != null && alertDialog.isShowing())
            return;
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.alert);
        ad.setMessage(message);
        ad.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });
        ad.setCancelable(false);
        alertDialog = ad.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }
}
