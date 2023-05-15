package com.vs.schoolmessenger.activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.VideosAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.model.VideoModelClass;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffDisplayVideos extends AppCompatActivity {

    RecyclerView rvVoiceList;
    public ArrayList<TeacherMessageModel> msgModelList = new ArrayList<>();
    String selDate, staff_id, school_id;
    ImageView voice_ToolBarIvBack;

    VideosAdapter textAdapter;
    public ArrayList<VideoModelClass> videoList = new ArrayList<>();

    Boolean is_Archive;
    String isNewVersion;
    TextView voice_ToolBarTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_voice_circular);
        voice_ToolBarIvBack = (ImageView) findViewById(R.id.voice_ToolBarIvBack);
        voice_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayVideos.this, "1");
                onBackPressed();
            }
        });
        voice_ToolBarTvTitle=(TextView) findViewById(R.id.voice_ToolBarTvTitle);
        selDate = getIntent().getExtras().getString("SEL_DATE");
        voice_ToolBarTvTitle.setText(selDate);

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StaffDisplayVideos.this);
        staff_id = Util_SharedPreference.getStaffID(StaffDisplayVideos.this);
        school_id = Util_SharedPreference.getSchoolId(StaffDisplayVideos.this);
        rvVoiceList = (RecyclerView) findViewById(R.id.voice_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVoiceList.setLayoutManager(layoutManager);
        rvVoiceList.setItemAnimator(new DefaultItemAnimator());
        textAdapter = new VideosAdapter(videoList, StaffDisplayVideos.this);
        rvVoiceList.setAdapter(textAdapter);

    }

    @Override
    public void onBackPressed() {
        TeacherUtil_SharedPreference.putOnBackPressed(StaffDisplayVideos.this,"1");
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        VideoListApi();
    }

    private void VideoListApi() {
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(StaffDisplayVideos.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(StaffDisplayVideos.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(StaffDisplayVideos.this);
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
        jsonObject.addProperty("Type", "VIDEO");
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
                        Log.d("json length", js.length() + "");
                        textAdapter.clearAllData();
                        videoList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);
                            String id = jsonObject.getString("ID");
                            if (!id.equals("")) {
                                String VideoId = "";
                                String CreatedBy = "";
                                String CreatedOn = jsonObject.getString("Date");
                                String Title = jsonObject.getString("Subject");
                                String Description = "";
                                String VimeoUrl = jsonObject.getString("VimeoUrl");
                                String VimeoId = jsonObject.getString("VimeoId");
                                String DetailID = jsonObject.getString("ID");
                                String IsAppViewed = jsonObject.getString("AppReadStatus");
                                String Iframe = jsonObject.getString("URL");
                                boolean is_Archive = false;
                                VideoModelClass report = new VideoModelClass(VideoId,CreatedBy,CreatedOn,Title,Description,VimeoUrl,VimeoId,DetailID,IsAppViewed,Iframe,is_Archive);
                                videoList.add(report);

                            } else {
                                alert(jsonObject.getString("URL"));
                            }
                        }
                        textAdapter.notifyDataSetChanged();
                    } else {
                        alert(getResources().getString(R.string.no_records));
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
        Toast.makeText(StaffDisplayVideos.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StaffDisplayVideos.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();

    }
}
