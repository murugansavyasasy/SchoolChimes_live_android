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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.StaffChatDetailAdapter;
import com.vs.schoolmessenger.databinding.ActivityStaffDetailListBinding;
import com.vs.schoolmessenger.interfaces.StaffSelectedListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StaffDetail;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;

public class StaffDetailListActivity extends AppCompatActivity {
    ActivityStaffDetailListBinding binding;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_staff_detail_list);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        subjectListApi();
    }

    public void subjectListApi() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StaffDetailListActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StaffDetailListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StaffDetailListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String Code= TeacherUtil_SharedPreference.getShCountryCode(StaffDetailListActivity.this);

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("CountryCode", Code);
        jsonObject.addProperty("DialerId", Principal_staffId);
        jsonObject.addProperty("SchoolId", Principal_SchoolId);
        Log.d("reqallstaffs",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.staffDetailsList(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        Type userListType = new TypeToken<ArrayList<StaffDetail>>(){}.getType();
                        ArrayList<StaffDetail> staffDetails = new Gson().fromJson(response.body(), userListType);
                        StaffChatDetailAdapter adapter = new StaffChatDetailAdapter(staffDetails, new StaffSelectedListener() {
                            @Override
                            public void selectStaff(StaffDetail staffDetail) {
                                Intent intent = new Intent(StaffDetailListActivity.this, SubjectListActivity.class);
                                intent.putExtra(Constants.COME_FROM, Constants.PRINCIPLE);
                                intent.putExtra(Constants.STAFF_ID, String.valueOf(staffDetail.StaffId));
                                startActivity(intent);
                            }
                        });
                        binding.staffList.setAdapter(adapter);
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
}
