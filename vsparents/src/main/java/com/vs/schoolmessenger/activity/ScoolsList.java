package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SchoolsAdapter;
import com.vs.schoolmessenger.adapter.SchoolsListAdapter;
import com.vs.schoolmessenger.interfaces.SchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScoolsList extends AppCompatActivity implements SchoolsListener {
    ImageView schoollist_ToolBarIvBack;
    RecyclerView school_list_recyle;
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    ArrayList<TeacherSchoolsModel> selected_schoolList = new ArrayList<TeacherSchoolsModel>();
    SchoolsListAdapter mAdapter;
    SchoolsAdapter schoolAdapter;


    String SchoolsScreen = "";
    String Title = "";
    String Description = "";
    Button btnSend;

    String filePath = "";
    String Duration = "";
    String voiceTitle = "";
    String voicetype="";


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.school_list);


        school_list_recyle = (RecyclerView) findViewById(R.id.school_list_recyle);
        schoollist_ToolBarIvBack = (ImageView) findViewById(R.id.schoollist_ToolBarIvBack);
        btnSend = (Button) findViewById(R.id.btnSend);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("EXTRAS", extras.toString());
            SchoolsScreen = extras.getString("schools","");
            Title = extras.getString("Title","");
            Description = extras.getString("Description","");

            schools_list = (ArrayList<TeacherSchoolsModel>) getIntent().getSerializableExtra("TeacherSchoolsModel");

            voiceTitle = extras.getString("TITTLE","");
            filePath = extras.getString("FILEPATH","");
            Duration = extras.getString("DURATION","");

            voicetype=extras.getString("VOICE","");
        }
        if (SchoolsScreen.equals("groupHeadEmergency")||SchoolsScreen.equals("text")||SchoolsScreen.equals("PrincipalEmergency")||SchoolsScreen.equals("groupheadvoice")) {
            btnSend.setVisibility(View.VISIBLE);
        }

        schoollist_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (schools_list.size() > 0) {

                    if (SchoolsScreen.equals("text")) {
                        sendSmsApi();
                    } else if (SchoolsScreen.equals("PrincipalEmergency")) {

                        if(voicetype.equals("VoiceHistory")){
                           SendToEmergencyPrincipalVoiceHistory();
                        }
                        else {
                            SendEmergencyVoicePrincipalAPI();
                        }
                    }
                    else if(SchoolsScreen.equals("groupHeadEmergency")){

                        if(voicetype.equals("VoiceHistory")){
                            SendToEmergencyGroupHeadVoiceHistory();
                        }
                        else {
                            SendEmergencyVoiceGroupheadAPI();
                        }
                    }
                    else if(SchoolsScreen.equals("groupheadvoice")){

                        if(voicetype.equals("VoiceHistory")){
                            SendToGroupHeadVoiceHistory();
                        }
                        else {
                            SendnormalVoiceGroupheadAPI();
                        }
                    }




                } else {
                    showToast(getResources().getString(R.string.select_atleast_one_school));
                }

            }
        });


        if (SchoolsScreen.equals("text")) {
            schoolAdapter = new SchoolsAdapter(schools_list, this, ScoolsList.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScoolsList.this);
            school_list_recyle.setLayoutManager(mLayoutManager);
            school_list_recyle.setItemAnimator(new DefaultItemAnimator());
            school_list_recyle.setAdapter(schoolAdapter);
            school_list_recyle.getRecycledViewPool().setMaxRecycledViews(0, 80);

        } else if (SchoolsScreen.equals("groupHeadEmergency")) {

            schoolAdapter = new SchoolsAdapter(schools_list, this, ScoolsList.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScoolsList.this);
            school_list_recyle.setLayoutManager(mLayoutManager);
            school_list_recyle.setItemAnimator(new DefaultItemAnimator());
            school_list_recyle.setAdapter(schoolAdapter);
            school_list_recyle.getRecycledViewPool().setMaxRecycledViews(0, 80);
        }

        else if (SchoolsScreen.equals("PrincipalEmergency")) {

            schoolAdapter = new SchoolsAdapter(schools_list, this, ScoolsList.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScoolsList.this);
            school_list_recyle.setLayoutManager(mLayoutManager);
            school_list_recyle.setItemAnimator(new DefaultItemAnimator());
            school_list_recyle.setAdapter(schoolAdapter);
            school_list_recyle.getRecycledViewPool().setMaxRecycledViews(0, 80);
        }
        else if (SchoolsScreen.equals("groupheadvoice")) {

            schoolAdapter = new SchoolsAdapter(schools_list, this, ScoolsList.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScoolsList.this);
            school_list_recyle.setLayoutManager(mLayoutManager);
            school_list_recyle.setItemAnimator(new DefaultItemAnimator());
            school_list_recyle.setAdapter(schoolAdapter);
            school_list_recyle.getRecycledViewPool().setMaxRecycledViews(0, 80);
        }

        else {
            mAdapter = new SchoolsListAdapter(schools_list, ScoolsList.this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ScoolsList.this);
            school_list_recyle.setLayoutManager(mLayoutManager);
            school_list_recyle.setItemAnimator(new DefaultItemAnimator());
            school_list_recyle.setAdapter(mAdapter);
            school_list_recyle.getRecycledViewPool().setMaxRecycledViews(0, 80);
        }


    }

    private void SendToGroupHeadVoiceHistory() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = jsonArrayNormalVoiceGroupHeadVoiceHistory();

        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchoolsByVoiceHistory(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);

                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayNormalVoiceGroupHeadVoiceHistory() {
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "0");
            jsonObjectSchoolgrouphead.addProperty("Description", Description);
            jsonObjectSchoolgrouphead.addProperty("Duration", Duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");
            jsonObjectSchoolgrouphead.addProperty("filepath", filePath);

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(schools_list.size()));
                    Log.d("schoolid", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolgrouphead;
    }

    private void SendToEmergencyGroupHeadVoiceHistory() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = jsonArrayEmergencyVoiceGroupHeadVoiceHistory();

        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchoolsByVoiceHistory(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);

                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayEmergencyVoiceGroupHeadVoiceHistory() {
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("Description", Description);
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "1");
            jsonObjectSchoolgrouphead.addProperty("Duration", Duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");
            jsonObjectSchoolgrouphead.addProperty("filepath", filePath);

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(schools_list.size()));
                    Log.d("schoolid", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolgrouphead;
    }

    private void SendToEmergencyPrincipalVoiceHistory() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = jsonArrayObjectFromPrincipalEmergencyVoiceHistory();

        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchoolsByVoiceHistory(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject jsonArrayObjectFromPrincipalEmergencyVoiceHistory() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("Description", voiceTitle);
            jsonObjectSchool.addProperty("isEmergency", "1");
            jsonObjectSchool.addProperty("Duration", String.valueOf(Duration));//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchool.addProperty("CallerType", "M");
            jsonObjectSchool.addProperty("filepath", filePath);

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolId = new JsonObject();
                    jsonObjectschoolId.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(schools_list.size()));
                    Log.d("schoolid", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolId.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolId);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchool.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchool;
    }

    private void SendEmergencyVoicePrincipalAPI() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filePath);
        Log.d("FILE_Path",filePath);
        Log.d("file", file.getName());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsprincipal();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);

                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolsprincipal() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("Description", voiceTitle);
            jsonObjectSchool.addProperty("isEmergency", "1");
            jsonObjectSchool.addProperty("Duration", String.valueOf(Duration));//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchool.addProperty("CallerType", "M");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolId = new JsonObject();
                    jsonObjectschoolId.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolId.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolId);
                }
            }

            jsonObjectSchool.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
        }

        return jsonObjectSchool;
    }


    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filePath);
        Log.d("FILE_Path", filePath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtEmergencyGrouphead();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtEmergencyGrouphead() {
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("Description", Description);
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "1");
            jsonObjectSchoolgrouphead.addProperty("Duration", Duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(schools_list.size()));
                    Log.d("schoolid", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }
            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolgrouphead;
    }

    private void SendnormalVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(filePath);
        Log.d("FILE_Path", filePath);
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsvoiceGrouphead();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolsvoiceGrouphead() {
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "0");
            jsonObjectSchoolgrouphead.addProperty("Description", Description);
            jsonObjectSchoolgrouphead.addProperty("Duration", Duration);//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                if (schools_list.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(schools_list.size()));
                    Log.d("schoolid", schools_list.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }

            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolgrouphead;
    }

    private void sendSmsApi() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(ScoolsList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(ScoolsList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        Call<JsonArray> call = apiService.SendSMSToEntireSchools(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            if ((strStatus.toLowerCase()).equals("1")) {


                                showAlert(strMsg,strStatus);
                            } else {
                                showAlert(strMsg,strStatus);
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolsGrouphead() {

        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("Description", Title);
            jsonObjectSchool.addProperty("Message", Description);
            jsonObjectSchool.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < schools_list.size(); i++) {
                JsonObject jsonObjectschoolId = new JsonObject();
                jsonObjectschoolId.addProperty("SchoolId", schools_list.get(i).getStrSchoolID());
                jsonObjectschoolId.addProperty("StaffID", schools_list.get(i).getStrStaffID());
                jsonArrayschool.add(jsonObjectschoolId);
            }

            jsonObjectSchool.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchool;
    }

    private void showAlert(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ScoolsList.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(ScoolsList.this, Teacher_AA_Test.class);
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

    private void showToast(String string) {
        Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void schools_add(TeacherSchoolsModel student) {
        schools_list.add(student);
    }

    @Override
    public void schools_remove(TeacherSchoolsModel student) {
        schools_list.remove(student);
    }
}

