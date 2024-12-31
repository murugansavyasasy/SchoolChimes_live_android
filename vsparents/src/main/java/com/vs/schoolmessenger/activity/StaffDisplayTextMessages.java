package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.StaffDisplayTextAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnMsgItemClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffDisplayTextMessages extends AppCompatActivity {
    public ArrayList<TeacherMessageModel> msgModelList = new ArrayList<>();
    RecyclerView rvVoiceList;
    StaffDisplayTextAdapter textAdapter;
    String selDate, staff_id, school_id;
    ImageView voice_ToolBarIvBack;
    Boolean is_Archive;
    String isNewVersion;
    private int iRequestCode;
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
        setContentView(R.layout.teacher_activity_voice_circular);

        voice_ToolBarIvBack = (ImageView) findViewById(R.id.voice_ToolBarIvBack);
        voice_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayTextMessages.this, "1");
                onBackPressed();
            }
        });

        selDate = getIntent().getExtras().getString("SEL_DATE");

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StaffDisplayTextMessages.this);

        staff_id = Util_SharedPreference.getStaffID(StaffDisplayTextMessages.this);
        school_id = Util_SharedPreference.getSchoolId(StaffDisplayTextMessages.this);

        rvVoiceList = (RecyclerView) findViewById(R.id.voice_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVoiceList.setLayoutManager(layoutManager);
        rvVoiceList.setItemAnimator(new DefaultItemAnimator());
        textAdapter = new StaffDisplayTextAdapter(msgModelList, StaffDisplayTextMessages.this, new OnMsgItemClickListener() {
            @Override
            public void onMsgItemClick(TeacherMessageModel item) {
                Intent inTextPopup = new Intent(StaffDisplayTextMessages.this, TextMessagePopup.class);
                inTextPopup.putExtra("TEXT_ITEM", item);
                inTextPopup.putExtra("is_Archive", item.getIs_Archive());
                startActivity(inTextPopup);
            }
        });
        rvVoiceList.setAdapter(textAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        textMessages();

    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayTextMessages.this, "1");
        finish();
    }

    private void textMessages() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StaffDisplayTextMessages.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StaffDisplayTextMessages.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StaffDisplayTextMessages.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolId", school_id);
        jsonObject.addProperty("MemberId", staff_id);
        jsonObject.addProperty("CircularDate", selDate);
        jsonObject.addProperty("Type", "SMS");

        Log.d(" jsonObject12344555", String.valueOf(jsonObject));

        Call<JsonArray> call;
        if (isNewVersion.equals("1") && is_Archive) {
            call = apiService.GetFilesStaff_Archive(jsonObject);
        } else {
            call = apiService.GetFilesStaff(jsonObject);
        }

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("overallUnreadCount:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        TeacherMessageModel msgModel;
                        Log.d("json length", String.valueOf(js.length()));

                        textAdapter.clearAllData();

                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);

                            String id = jsonObject.getString("ID");
                            if (!id.equals("")) {
                                msgModel = new TeacherMessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"), jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                            } else {
                                showToast(jsonObject.getString("URL"));
                            }
                        }
                        textAdapter.notifyDataSetChanged();
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Unreadcount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Unreadcount:Failure", t.toString());
            }
        });

    }


    private void showToast(String msg) {
        Toast.makeText(StaffDisplayTextMessages.this, msg, Toast.LENGTH_SHORT).show();
    }
}

