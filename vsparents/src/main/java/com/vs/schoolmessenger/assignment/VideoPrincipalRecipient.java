package com.vs.schoolmessenger.assignment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherStaffStandardSection;
import com.vs.schoolmessenger.activity.TeacherStandardsAndGroupsList;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.activity.VoiceStandardAndGroupList;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.VimeoUploader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AppCompatActivity;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VIDEOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;

public class VideoPrincipalRecipient extends AppCompatActivity implements View.OnClickListener, VimeoUploader.UploadCompletionListener {


    Button SendToEntireSchool, SendToStansGroups, SendToSpecificSection;

    String SchoolID, StaffID, filepath, duration, tittle, strmessage, strdate, strtime, strfilepathimage;
    int iRequestCode = 0;

    ArrayList<TeacherClassGroupModel> listClasses, listGroups;

    String voicetype = "";
    String PRINCIPAL_IMAGE = "";

    ArrayList<String> slectedImagePath = new ArrayList<String>();
    String strPDFFilepath, strVideoFilePath, VideoDescription, upload_link, link, iframe;
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url, strsize;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.sens_specific_section);


        SendToEntireSchool = (Button) findViewById(R.id.SendToEntireSchool);
        SendToEntireSchool.setOnClickListener(this);
        SendToStansGroups = (Button) findViewById(R.id.SendToStansGroups);
        SendToStansGroups.setOnClickListener(this);
        SendToSpecificSection = (Button) findViewById(R.id.SendToSpecificSection);
        SendToSpecificSection.setOnClickListener(this);


        ImageView ivBack = (ImageView) findViewById(R.id.emergVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        SchoolID = Principal_SchoolId;
        StaffID = Principal_staffId;

        if (TeacherUtil_Common.listschooldetails.size() == 1) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        strsize = getIntent().getExtras().getString("FILE_SIZE", "");
        duration = getIntent().getExtras().getString("DURATION", "");
        tittle = getIntent().getExtras().getString("TITLE", "");
        strmessage = getIntent().getExtras().getString("CONTENT", "");
        strdate = getIntent().getExtras().getString("DATE", "");
        strtime = getIntent().getExtras().getString("TIME", "");
        strfilepathimage = getIntent().getExtras().getString("FILE_PATH_IMAGE", "");
        voicetype = getIntent().getExtras().getString("VOICE", "");
        PRINCIPAL_IMAGE = getIntent().getExtras().getString("PRINCIPAL_IMAGE", "");
        VideoDescription = getIntent().getExtras().getString("VIDEO_DESCRIPTION", "");

        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();

        strPDFFilepath = getIntent().getExtras().getString("FILE_PATH_PDF", "");
        strVideoFilePath = getIntent().getExtras().getString("VIDEO_FILE_PATH", "");

        if (strPDFFilepath.equals("")) {
            slectedImagePath = (ArrayList<String>) getIntent().getSerializableExtra("PATH_LIST");
        }


    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SendToEntireSchool:
                showAlert("Do you want to send the video");
                break;
            case R.id.SendToStansGroups:

                Intent inPrincipal = new Intent(VideoPrincipalRecipient.this, GroupListVideoActivity.class);
                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                inPrincipal.putExtra("FILEPATH", filepath);
                inPrincipal.putExtra("FILE_SIZE", strsize);
                inPrincipal.putExtra("CONTENT", strmessage);
                inPrincipal.putExtra("TITLE", tittle);
                startActivityForResult(inPrincipal, iRequestCode);

                break;

            case R.id.SendToSpecificSection:

                Intent intoSec = new Intent(VideoPrincipalRecipient.this, RecipientVideoActivity.class);
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("SEC", "1");
                intoSec.putExtra("FILEPATH", filepath);
                intoSec.putExtra("FILE_SIZE", strsize);
                intoSec.putExtra("CONTENT", strmessage);
                intoSec.putExtra("TITLE", tittle);
                startActivityForResult(intoSec, iRequestCode);

                break;
        }
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoPrincipalRecipient.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // VimeoAPi();
                uploadVimeoVideo();
            }
        });

        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }

    private void uploadVimeoVideo() {
        String authToken = TeacherUtil_SharedPreference.getVideotoken(VideoPrincipalRecipient.this);
        VimeoUploader.uploadVideo(VideoPrincipalRecipient.this, tittle, strmessage, authToken, filepath, this);
    }

    @Override
    public void onUploadComplete(boolean success, String isIframe, String isLink) {
        Log.d("Vime_Video_upload", String.valueOf(success));
        Log.d("VimeoIframe", isIframe);
        Log.d("link", isLink);

        if (success) {
            iframe = isIframe;
            link = isLink;
            SendVideotoSecApi();
        } else {
            showAlertfinal("Video sending failed.Retry", "0");
        }
    }


    private void showAlert1(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoPrincipalRecipient.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(VideoPrincipalRecipient.this, Teacher_AA_Test.class);
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


    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private JsonObject JsonVideotoSection() {
        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("Title", tittle);
            jsonObjectSchoolstdgrp.addProperty("Description", strmessage);
            jsonObjectSchoolstdgrp.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchoolstdgrp.addProperty("ProcessBy", Principal_staffId);
            jsonObjectSchoolstdgrp.addProperty("Iframe", iframe);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolstdgrp.addProperty("URL", link);//getIntent().getExtras().getString("MEDIA_DURATION", "0")

            Log.d("reqVideo", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    void SendVideotoSecApi() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VideoPrincipalRecipient.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);


//        final ProgressDialog mProgressDialog = new ProgressDialog(VideoPrincipalRecipient.this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Uploading...");
//        mProgressDialog.setCancelable(false);

      //  if (!this.isFinishing())
          //  mProgressDialog.show();
        JsonObject jsonReqArray = JsonVideotoSection();
        Call<JsonArray> call = apiService.SendVideoFromAppForEnitireSchool(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();

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
                                showAlertfinal(strMsg, strStatus);

                            } else {
                                showAlertfinal(strMsg, strStatus);
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
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }


    private void VimeoAPi() {
        OkHttpClient.Builder clientinterceptor = new OkHttpClient.Builder();
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientinterceptor.interceptors().add(interceptor);
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

        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(VideoPrincipalRecipient.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject object = new JsonObject();

        JsonObject jsonObjectclasssec = new JsonObject();
        jsonObjectclasssec.addProperty("approach", "post");
        jsonObjectclasssec.addProperty("size", String.valueOf(strsize));

        JsonObject jsonprivacy = new JsonObject();
        jsonprivacy.addProperty("view", "unlisted");

        JsonObject jsonshare = new JsonObject();
        jsonshare.addProperty("share", "false");

        JsonObject jsonembed = new JsonObject();
        jsonembed.add("buttons", jsonshare);

        object.add("upload", jsonObjectclasssec);
        object.addProperty("name", tittle);
        object.addProperty("description", strmessage);
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);
        String head = "Bearer " + TeacherUtil_SharedPreference.getVideotoken(VideoPrincipalRecipient.this);
        Log.d("header", head);
        Call<JsonObject> call = service.VideoUpload(object, head);
        Log.d("jsonOBJECT", object.toString());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try", "testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        Log.d("Response sucess", object1.toString());


                        JSONObject obj = object1.getJSONObject("upload");
                        JSONObject obj1 = object1.getJSONObject("embed");
                        upload_link = obj.getString("upload_link");
                        link = object1.getString("link");
                        iframe = obj1.getString("html");
                        Log.d("c", upload_link);
                        Log.d("iframe", iframe);

                        try {
                            VIDEOUPLOAD(upload_link);
                        } catch (Exception e) {
                            Log.e("VIMEO Exception", e.getMessage());
                            showAlertfinal("Video sending failed.Retry", "0");
                        }

                    } catch (Exception e) {

                        Log.e("VIMEO Exception", e.getMessage());
                        showAlertfinal(e.getMessage(), "0");
                    }


                } else {
                    Log.d("Response fail", "fail");
                    showAlertfinal("Video sending failed.Retry", "0");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                showAlertfinal("Video sending failed.Retry", "0");
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
        final ProgressDialog mProgressDialog = new ProgressDialog(VideoPrincipalRecipient.this);
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
            showAlertfinal(e.getMessage(), "0");
        }

        Call<ResponseBody> call = service.patchVimeoVideoMetaData(ticket2, ticket3, tick, str1, redirect_url123 + "www.voicesnapforschools.com", requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        SendVideotoSecApi();
                    } else {
                        showAlertfinal("Video sending failed.Retry", "0");
                    }
                } catch (Exception e) {
                    showAlertfinal(e.getMessage(), "0");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showAlertfinal("Video sending failed.Retry", "0");

            }
        });


    }


    private void showAlertfinal(String msg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VideoPrincipalRecipient.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(VideoPrincipalRecipient.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
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

