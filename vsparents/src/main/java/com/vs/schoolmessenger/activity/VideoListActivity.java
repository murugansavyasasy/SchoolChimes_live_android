package com.vs.schoolmessenger.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.VideosAdapter;
import com.vs.schoolmessenger.assignment.AssignmentViewClass;
import com.vs.schoolmessenger.assignment.ParentAssignmentListActivity;
import com.vs.schoolmessenger.assignment.VideoUpload;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.VideoModelClass;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VideoListActivity extends AppCompatActivity {

    RecyclerView videos_circularList;
    public ArrayList<VideoModelClass> videoList = new ArrayList<>();
    public ArrayList<VideoModelClass> totalvideoList = new ArrayList<>();
    public ArrayList<VideoModelClass> OfflinevideoList = new ArrayList<>();

    VideosAdapter imgAdapter;
    String videoId;
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;

    Calendar c;
    String previousDate;
    SqliteDB myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);

        c = Calendar.getInstance();
        ImageView ivBack = (ImageView) findViewById(R.id.image_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.image_ToolBarTvTitle);
         LoadMore=(TextView) findViewById(R.id.btnSeeMore);
         lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreVideoListApi();


//                previousDate=TeacherUtil_SharedPreference.getVideoDate(VideoListActivity.this);
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = df.format(c.getTime());
//                if (previousDate.equals("") || previousDate.compareTo(currentDate)<0)
//                {
//                    LoadMoreVideoListApi();
//                }
//                else {
//                    myDb = new SqliteDB(VideoListActivity.this);
//                    if (myDb.checkVideos()) {
//                        videoList.clear();
//                        totalvideoList.addAll(myDb.getVideos());
//                        videoList.addAll(totalvideoList);
//                        imgAdapter.notifyDataSetChanged();
//                        LoadMore.setVisibility(View.GONE);
//
//                    }
//                    else {
//                        alert("No Records Found..");
//                    }
//                }
            }
        });

         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(VideoListActivity.this);

        seeMoreButtonVisiblity();



        videos_circularList = (RecyclerView) findViewById(R.id.videos_circularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        videos_circularList.setLayoutManager(layoutManager);
        videos_circularList.setItemAnimator(new DefaultItemAnimator());
        imgAdapter = new VideosAdapter(videoList, VideoListActivity.this);
        videos_circularList.setAdapter(imgAdapter);
//        VideoListApi();
//        addDatas();

    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    private void LoadMoreVideoListApi() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(VideoListActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(VideoListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(VideoListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(VideoListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String childID= Util_SharedPreference.getChildIdFromSP(VideoListActivity.this);
        JsonObject object = new JsonObject();
        object.addProperty("StudentId",childID);
        Log.d("video:req", object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.LoadMoreGetVideosForStudent(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    Log.d("Upload-Code:Response", response.code() + "-" + response);

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    mProgressDialog.dismiss();
                    LoadMore.setVisibility(View.GONE);
                    lblNoMessages.setVisibility(View.GONE);

//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    String currentDate = df.format(c.getTime());
//                    Log.d("currentDate",currentDate);
//                    TeacherUtil_SharedPreference.putVideoCurrentDate(VideoListActivity.this,currentDate);

                    try {
                       // videoList.clear();

                        OfflinevideoList.clear();
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");

                                String Message = jsonObject.getString("Message");
                                if (result.equals("1")) {
                                    String VideoId = jsonObject.getString("VideoId");
                                    String CreatedBy = jsonObject.getString("CreatedBy");
                                    String CreatedOn = jsonObject.getString("CreatedOn");
                                    String Title = jsonObject.getString("Title");
                                    String Description = jsonObject.getString("Description");
                                    String VimeoUrl = jsonObject.getString("VimeoUrl");
                                    String VimeoId = jsonObject.getString("VimeoId");
                                    String DetailID = jsonObject.getString("DetailID");
                                    String IsAppViewed = jsonObject.getString("IsAppViewed");
                                    String Iframe = jsonObject.getString("Iframe");
                                    boolean is_Archive = jsonObject.getBoolean("is_Archive");
                                    VideoModelClass report = new VideoModelClass(VideoId,CreatedBy,CreatedOn,Title,Description,VimeoUrl,VimeoId,DetailID,IsAppViewed,Iframe,is_Archive);
                                    videoList.add(report);
                                    OfflinevideoList.add(report);
                                }
                                else{
                                    alert(Message);
                                }
                            }
//                            myDb = new SqliteDB(VideoListActivity.this);
//                            if(myDb.checkVideos()){
//                                myDb.deleteVideos();
//                            }
//                            myDb.addVideos( (ArrayList<VideoModelClass>) OfflinevideoList, VideoListActivity.this);


                            imgAdapter.notifyDataSetChanged();
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

    private void addDatas() {

//        VideoModelClass msgModel=new VideoModelClass("P3mAtvs5Elc","http://img.youtube.com/vi/P3mAtvs5Elc/0.jpg");
//        videoList.add(msgModel);
        imgAdapter.notifyDataSetChanged();
    }

    private void getImagesURL() {

            videoId=extractYoutubeId("http://www.youtube.com/watch?v=P3mAtvs5Elc");
            Log.e("VideoId is->","" + videoId);
            String img_url="http://img.youtube.com/vi/"+videoId+"/0.jpg";

//            VideoModelClass msgModel=new VideoModelClass("P3mAtvs5Elc",img_url);
//            videoList.add(msgModel);
    }

    private String extractYoutubeId(String url) {
        String query = null;
        try {
            query = new URL(url).getQuery();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String[] param = query.split("&");
        String id = null;
        for (String row : param) {
            String[] param1 = row.split("=");
            if (param1[0].equals("v")) {
                id = param1[1];
            }
        }
        return id;
    }

    @Override
    public void onResume() {
        super.onResume();
        VideoListApi();
    }

    private void VideoListApi() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(VideoListActivity.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(VideoListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(VideoListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(VideoListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String childID= Util_SharedPreference.getChildIdFromSP(VideoListActivity.this);
        JsonObject object = new JsonObject();
        object.addProperty("StudentId",childID);
        Log.d("video:req", object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.GetVideosForStudent(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    Log.d("Upload-Code:Response", response.code() + "-" + response);

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    mProgressDialog.dismiss();
                    try {
                        videoList.clear();
                        totalvideoList.clear();

                        if(isNewVersion.equals("1")){
                            LoadMore.setVisibility(View.VISIBLE);
                            lblNoMessages.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");
                                String Message = jsonObject.getString("Message");
                                if (result.equals("1")) {
                                    String VideoId = jsonObject.getString("VideoId");
                                    String CreatedBy = jsonObject.getString("CreatedBy");
                                    String CreatedOn = jsonObject.getString("CreatedOn");
                                    String Title = jsonObject.getString("Title");
                                    String Description = jsonObject.getString("Description");
                                    String VimeoUrl = jsonObject.getString("VimeoUrl");
                                    String VimeoId = jsonObject.getString("VimeoId");
                                    String DetailID = jsonObject.getString("DetailID");
                                    String IsAppViewed = jsonObject.getString("IsAppViewed");
                                    String Iframe = jsonObject.getString("Iframe");

                                    VideoModelClass report = new VideoModelClass(VideoId,CreatedBy,CreatedOn,Title,Description,VimeoUrl,VimeoId,DetailID,IsAppViewed,Iframe,false);
                                    videoList.add(report);
                                    totalvideoList.add(report);
                                }
                                else{
                                    if(isNewVersion.equals("1")){
                                        lblNoMessages.setVisibility(View.VISIBLE);
                                        lblNoMessages.setText(Message);

                                        String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodVideo(VideoListActivity.this);
                                        if(loadMoreCall.equals("1")){
                                            TeacherUtil_SharedPreference.putOnBackPressedVideo(VideoListActivity.this,"");
                                            LoadMoreVideoListApi();
                                        }
                                    }
                                    else {
                                        lblNoMessages.setVisibility(View.GONE);
                                        alert(Message);
                                    }

                                }
                            }

                            imgAdapter.notifyDataSetChanged();
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
        Toast.makeText(VideoListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoListActivity.this);
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
