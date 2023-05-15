package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherVoiceCircularListAdapterNEW;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherVoiceCircular extends AppCompatActivity {
    RecyclerView rvVoiceList;
    TeacherVoiceCircularListAdapterNEW voiceAdapter;
    public ArrayList<TeacherMessageModel> msgModelList = new ArrayList<>();

    private int iRequestCode;

    TextView voice_ToolBarTvTitle;
    String staff_id,school_id,date;

    ImageView voice_ToolBarIvBack;
    Boolean is_Archive;
    String isNewVersion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_voice_circular);


        voice_ToolBarIvBack=(ImageView) findViewById(R.id.voice_ToolBarIvBack);
        voice_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressed(TeacherVoiceCircular.this,"1");
                onBackPressed();
            }
        });

        voice_ToolBarTvTitle=(TextView) findViewById(R.id.voice_ToolBarTvTitle);
         date=getIntent().getExtras().getString("SEL_DATE");

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherVoiceCircular.this);


        voice_ToolBarTvTitle.setText(date);

        rvVoiceList = (RecyclerView) findViewById(R.id.voice_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVoiceList.setLayoutManager(layoutManager);
        rvVoiceList.setItemAnimator(new DefaultItemAnimator());
        voiceAdapter = new TeacherVoiceCircularListAdapterNEW(TeacherVoiceCircular.this, msgModelList,is_Archive);
        rvVoiceList.setAdapter(voiceAdapter);


        staff_id=Util_SharedPreference.getStaffID(TeacherVoiceCircular.this);
        school_id=Util_SharedPreference.getSchoolId(TeacherVoiceCircular.this);


    }

    @Override
    public void onResume(){
        super.onResume();
        getVoiveMessages();

    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(TeacherVoiceCircular.this,"1");
        finish();
    }

    private void getVoiveMessages() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherVoiceCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherVoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherVoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();



        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolId", school_id);
        jsonObject.addProperty("MemberId", staff_id);
        jsonObject.addProperty("CircularDate", date);
        jsonObject.addProperty("Type", "VOICE");

        Log.d(" jsonObject12344555", String.valueOf(jsonObject));


        Call<JsonArray> call;
        if(isNewVersion.equals("1")&&is_Archive){
            call = apiService.GetFilesStaff_Archive(jsonObject);
        }
        else {
            call = apiService.GetFilesStaff(jsonObject);
        }

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("overallUnreadCount:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        TeacherMessageModel msgModel;
                        Log.d("json length", js.length() + "");

                        voiceAdapter.clearAllData();
                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);

                            String id = jsonObject.getString("ID");
                            if (!id.equals("")) {
                                msgModel = new TeacherMessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), "");
                                msgModelList.add(msgModel);
                            }

                            else {
                                showToast(jsonObject.getString("URL"));
                            }
                        }
                        voiceAdapter.notifyDataSetChanged();
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



    private void circularsForGivenDateAPI2() {


        try {
            JSONArray js = new JSONArray("[{\"ID\":\"2450051\",\"URL\":\"http://vs3.voicesnapforschools.com/files/29-12-2017/2030/9731860063_20171229_124325_81.wav\",\"Date\":\"29-12-2017\",\"Time\":\"12:43:23\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null},{\"ID\":\"2440510\",\"URL\":\"http://vs3.voicesnapforschools.com/files/29-12-2017/2030/9731860063_20171229_075037_176.wav\",\"Date\":\"29-12-2017\",\"Time\":\"07:50:32\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null}]");
            if (js.length() > 0) {
                JSONObject jsonObject = js.getJSONObject(0);
                String strDate = jsonObject.getString("ID");
                String strTotalSMS = jsonObject.getString("URL");

                if (!strDate.equals("")) {
                    TeacherMessageModel msgModel;

                    voiceAdapter.clearAllData();
                    for (int i = 0; i < js.length(); i++) {
                        jsonObject = js.getJSONObject(i);
                        msgModel = new TeacherMessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"));
                        msgModelList.add(msgModel);
                    }

                    voiceAdapter.notifyDataSetChanged();

                } else {
                    showToast(strTotalSMS);
                }
            } else {
                showToast("Server Response Failed. Try again");
            }

        } catch (Exception e) {
            Log.e("TextMsg:Exception", e.getMessage());
        }
    }
    private void showToast(String msg) {
        Toast.makeText(TeacherVoiceCircular.this, msg, Toast.LENGTH_SHORT).show();
    }
}
