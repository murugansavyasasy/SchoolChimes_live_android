package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.vs.schoolmessenger.adapter.ImageCircularListAdapterNEW;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CircularDates;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;

public class ImageCircular extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;

    RecyclerView rvImageList;
    ImageCircularListAdapterNEW imgAdapter;
    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    public ArrayList<MessageModel> OfflinemsgModelList = new ArrayList<>();
    public ArrayList<MessageModel> totalmsgModelList = new ArrayList<>();
    public ArrayList<MessageModel> totalOfflineData = new ArrayList<>();

    String selDate;
    private int iRequestCode;

    SqliteDB myDb;
    ArrayList<MessageModel> arrayList;

    private final String android_image_urls[] = {
            "https://static.pexels.com/photos/3247/nature-forest-industry-rails.jpg",
            "https://static.pexels.com/photos/33109/fall-autumn-red-season.jpg",
            "https://static.pexels.com/photos/115045/pexels-photo-115045.jpeg",
            "https://static.pexels.com/photos/26750/pexels-photo-26750.jpg",
            "https://static.pexels.com/photos/158607/cairn-fog-mystical-background-158607.jpeg"
    };

    private final String android_image_status[] = {"1", "0", "0", "1", "0"};
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;
    String childID;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_circular);
        c = Calendar.getInstance();

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        selDate = getIntent().getExtras().getString("HEADER", "");

        ImageView ivBack = (ImageView) findViewById(R.id.image_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

         childID = Util_SharedPreference.getChildIdFromSP(ImageCircular.this);
         LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               LoadMorecircularsImageAPI();
            }
        });
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ImageCircular.this);
        seeMoreButtonVisiblity();

        TextView tvTitle = (TextView) findViewById(R.id.image_ToolBarTvTitle);
        tvTitle.setText(selDate);

        rvImageList = (RecyclerView) findViewById(R.id.image_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvImageList.setLayoutManager(layoutManager);
        rvImageList.setItemAnimator(new DefaultItemAnimator());
        imgAdapter = new ImageCircularListAdapterNEW(msgModelList, ImageCircular.this);
        rvImageList.setAdapter(imgAdapter);
        rvImageList.getRecycledViewPool().setMaxRecycledViews(0, 20);
    }

    private void LoadoffLineData() {

        previousDate=TeacherUtil_SharedPreference.getImageDate(ImageCircular.this);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = df.format(c.getTime());
        if (previousDate.equals("") || previousDate.compareTo(currentDate)<0)
        {
            LoadMorecircularsImageAPI();

        }
        else {
            myDb = new SqliteDB(ImageCircular.this);
            if (myDb.checkImages()) {
                msgModelList.clear();
                totalOfflineData.clear();
                totalOfflineData.addAll(myDb.getImages(childID));

                totalmsgModelList.addAll(totalOfflineData);

                msgModelList.addAll(totalmsgModelList);
                imgAdapter.notifyDataSetChanged();
                LoadMore.setVisibility(View.GONE);

            }
            else {
                showAlertRecords("No Records Found..");
            }
        }
    }



    private void seeMoreButtonVisiblity() {
        if (isNewVersion.equals("1")) {
            LoadMore.setVisibility(View.VISIBLE);
            lblNoMessages.setVisibility(View.VISIBLE);
        } else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    private void LoadMorecircularsImageAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(ImageCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ImageCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ImageCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(ImageCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ImageCircular.this);

        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(ImageCircular.this);
        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID, "IMAGE", MobileNumber);
        Call<JsonArray> call = apiService.LoadMoreGetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);


//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = df.format(c.getTime());
//                Log.d("currentDate",currentDate);
//                TeacherUtil_SharedPreference.putImageCurrentDate(ImageCircular.this,currentDate);

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

//                            imgAdapter.clearAllData();
                            OfflinemsgModelList.clear();
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);

                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"),jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                                OfflinemsgModelList.add(msgModel);
                            }




//                            myDb = new SqliteDB(ImageCircular.this);
//                            if(myDb.checkImages()){
//                                //myDb.deleteImages();
//                                myDb.addImages( (ArrayList<MessageModel>) OfflinemsgModelList, ImageCircular.this,childID);
//
//                            }
//                            else {
//                                myDb.addImages((ArrayList<MessageModel>) OfflinemsgModelList, ImageCircular.this, childID);
//                            }
//
//                             //  myDb.addImages((ArrayList<MessageModel>) OfflinemsgModelList, ImageCircular.this, strChildID);



                            imgAdapter.notifyDataSetChanged();

                        } else {
                            showAlertRecords(strMessage);

                        }
                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isWriteExternalPermissionGranted()) {
            circularsImageAPI();
        }
    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageCircular.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.connect_internet);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) ImageCircular.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void showToast(String msg) {
        Toast.makeText(ImageCircular.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void prepareData() {
        for (int i = 0; i < android_image_urls.length; i++) {
            MessageModel model = new MessageModel();
            model.setMsgID(String.valueOf(i));
            long unixTime = System.currentTimeMillis() / 1000L;
            model.setMsgTitle(String.valueOf(unixTime));
            model.setMsgContent(android_image_urls[i]);
            model.setMsgDate("10 May 2017");
            model.setMsgTime("11:30 AM");
            model.setMsgReadStatus(android_image_status[i]);
            msgModelList.add(model);
        }

        imgAdapter.notifyDataSetChanged();
    }

    private void circularsForGivenDateAPI2() {
        try {
            JSONArray js = new JSONArray("[{\"ID\":\"168173\",\"URL\":\"http://vs3.voicesnapforschools.com/files//22-01-2018/2010/File_20180122180022795.png\",\"Date\":\"22-01-2018\",\"Time\":\"18:00:23\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"1\",\"Query\":null,\"Question\":null},{\"ID\":\"167349\",\"URL\":\"http://vs3.voicesnapforschools.com/files//22-01-2018/2010/File_20180122144128692.png\",\"Date\":\"22-01-2018\",\"Time\":\"14:41:29\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"1\",\"Query\":null,\"Question\":null}]");
            if (js.length() > 0) {
                JSONObject jsonObject = js.getJSONObject(0);
                String strDate = jsonObject.getString("ID");
                String strTotalSMS = jsonObject.getString("URL");

                if (!strDate.equals("")) {
                    MessageModel msgModel;
                    Log.d("json length", js.length() + "");

                    imgAdapter.clearAllData();

                    for (int i = 0; i < js.length(); i++) {
                        jsonObject = js.getJSONObject(i);
                        msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"),false);
                        msgModelList.add(msgModel);
                    }

                    imgAdapter.notifyDataSetChanged();

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

    private void circularsForGivenDateAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(ImageCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ImageCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = Util_JsonRequest.getJsonArray_GetFiles(selDate, strChildID, strSchoolID, MSG_TYPE_IMAGE);
        Call<JsonArray> call = apiService.GetFiles(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strDate = jsonObject.getString("ID");
                        String strTotalSMS = jsonObject.getString("URL");

                        if (!strDate.equals("")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            imgAdapter.clearAllData();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"),false);
                                msgModelList.add(msgModel);
                            }

                            imgAdapter.notifyDataSetChanged();

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

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void circularsImageAPI() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(ImageCircular.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ImageCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ImageCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(ImageCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ImageCircular.this);

        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(ImageCircular.this);


        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID, "IMAGE", MobileNumber);
        Call<JsonArray> call = apiService.GetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {

                    imgAdapter.clearAllData();
                    totalmsgModelList.clear();
                    msgModelList.clear();

                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (isNewVersion.equals("1")) {
                            LoadMore.setVisibility(View.VISIBLE);
                            lblNoMessages.setVisibility(View.VISIBLE);
                        } else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"),false);
                                msgModelList.add(msgModel);
                                totalmsgModelList.add(msgModel);
                            }

                            Log.d("msgModelList", String.valueOf(msgModelList.size()));

                            imgAdapter.notifyDataSetChanged();

                        }
                        else {
                            if (isNewVersion.equals("1")) {
                                lblNoMessages.setVisibility(View.VISIBLE);
                                lblNoMessages.setText(strMessage);
                                String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodImages(ImageCircular.this);
                                if(loadMoreCall.equals("1")){
                                    TeacherUtil_SharedPreference.putOnBackPressedImages(ImageCircular.this,"");
                                    LoadMorecircularsImageAPI();
                                }
                            }
                            else {
                                lblNoMessages.setVisibility(View.GONE);
                                showAlertRecords(strMessage);
                            }
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showAlertRecords(String strMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ImageCircular.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMessage);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    public boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
                return true;
            } else {
                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("SDCard_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                    circularsImageAPI();
                }
                return;
            }
        }
    }
}