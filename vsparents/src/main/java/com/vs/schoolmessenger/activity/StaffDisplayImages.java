package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
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
import com.vs.schoolmessenger.adapter.StaffDisplayImageAdapter;
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

public class StaffDisplayImages extends AppCompatActivity {
    RecyclerView rvVoiceList;
    StaffDisplayImageAdapter imageAdapter;
    public ArrayList<TeacherMessageModel> msgModelList = new ArrayList<>();

    ImageView voice_ToolBarIvBack;

    String selDate,staff_id,school_id;
    private int iRequestCode;

    Boolean is_Archive;
    String isNewVersion;

    private final String android_image_urls[] = {
            "https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3",
            "http://9xmusiq.com/songs/Tamil%20Songs/2016%20Tamil%20Mp3/Devi%20(2016)/Chalmaar%20%5bStarmusiq.cc%5d.mp3"
    };

    private final String android_image_status[] = {"1", "0"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_voice_circular);



        voice_ToolBarIvBack=(ImageView) findViewById(R.id.voice_ToolBarIvBack);
        voice_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayImages.this,"1");
                onBackPressed();
            }
        });

        staff_id=Util_SharedPreference.getStaffID(StaffDisplayImages.this);
        school_id=Util_SharedPreference.getSchoolId(StaffDisplayImages.this);
        selDate=getIntent().getExtras().getString("SEL_DATE");

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StaffDisplayImages.this);

        rvVoiceList = (RecyclerView) findViewById(R.id.voice_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVoiceList.setLayoutManager(layoutManager);
        rvVoiceList.setItemAnimator(new DefaultItemAnimator());
        imageAdapter = new StaffDisplayImageAdapter(msgModelList, StaffDisplayImages.this,is_Archive);
        rvVoiceList.setAdapter(imageAdapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        getImages();

    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayImages.this,"1");
        finish();
    }

    private void getImages() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StaffDisplayImages.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StaffDisplayImages.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StaffDisplayImages.this);
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
        jsonObject.addProperty("CircularDate", selDate);
        jsonObject.addProperty("Type", "IMAGE");

        Log.d("jsonObject___", String.valueOf(jsonObject));
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

                        imageAdapter.clearAllData();
                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);
                            String id = jsonObject.getString("ID");
                            if (!id.equals("")) {
                                msgModel = new TeacherMessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"), jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                            }
                            else {
                                showToast(jsonObject.getString("URL"));
                            }
                        }
                        imageAdapter.notifyDataSetChanged();
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
            }
        });

    }

    private void showToast(String msg) {
        Toast.makeText(StaffDisplayImages.this, msg, Toast.LENGTH_SHORT).show();
    }
}


