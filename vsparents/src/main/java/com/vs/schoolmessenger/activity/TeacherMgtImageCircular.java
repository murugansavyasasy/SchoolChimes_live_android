package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.adapter.TeacherSelectedschoolListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherOnSelectedStdGroupListener;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
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

import static com.vs.schoolmessenger.activity.TeacherPhotosScreen.IMAGE_UPLOAD_STATUS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;


public class TeacherMgtImageCircular extends AppCompatActivity implements View.OnClickListener {

    Button btnSelectSchool, btnSend, btnSendToAllSchool;
    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();

    RecyclerView tvSelectedSchools;
    TeacherSelectedschoolListAdapter adapter;
    LinearLayout llRecipients;

    PopupWindow pWindowSchools;
    private int i_schools_count;

    static int SELECTED_SCHOOL_POSITION = 0;

    ImageView ivCircular;
    String strSelectedImgFilePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_send_imageto_entire_school);


        i_schools_count = 0;

        ImageView ivBack = (ImageView) findViewById(R.id.img_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btnSelectSchool = (Button) findViewById(R.id.mgtImage_btnChooseSchools);
        btnSend = (Button) findViewById(R.id.mgtImage_btnSend);
        btnSendToAllSchool = (Button) findViewById(R.id.mgtImage_btnSendToAllSchools);

        btnSend.setOnClickListener(this);
        btnSendToAllSchool.setOnClickListener(this);
        btnSelectSchool.setOnClickListener(this);

        strSelectedImgFilePath = getIntent().getExtras().getString("IMAGE_PATH", "");
        ivCircular = (ImageView) findViewById(R.id.mgtImage_ivCircular);


        Bitmap d = new BitmapDrawable(strSelectedImgFilePath).getBitmap();
        int nh = (int) (d.getHeight() * (512.0 / d.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(d, 512, nh, true);
        ivCircular.setImageBitmap(scaled);


        tvSelectedSchools = (RecyclerView) findViewById(R.id.rvselectedschoolList);
        adapter = new TeacherSelectedschoolListAdapter(TeacherMgtImageCircular.this, new TeacherOnCheckSchoolsListener() {
            @Override
            public void school_addSchool(TeacherSchoolsModel school) {
                if ((school != null) && (!seletedschoollist.contains(school))) {
                    seletedschoollist.add(school);
                    i_schools_count++;
                }
            }

            @Override
            public void school_removeSchool(TeacherSchoolsModel school) {
                if ((school != null) && (seletedschoollist.contains(school))) {
                    seletedschoollist.remove(school);
                    i_schools_count--;

                    int index = arrSchoolList.indexOf(school);
                    arrSchoolList.get(index).setSelectStatus(false);
                }

                enableSend();
            }
        }, new TeacherOnSelectedStdGroupListener() {
            @Override
            public void classGrp_selectedClassesAndGroups(TeacherSchoolsModel school) {

                SELECTED_SCHOOL_POSITION = seletedschoollist.indexOf(school);
                Intent inStdGroups = new Intent(TeacherMgtImageCircular.this, TeacherExtandablegroupclasslist.class);
                inStdGroups.putExtra("STD_GROUP", school);
                startActivityForResult(inStdGroups, SELECTED_SCHOOL_POSITION);
            }
        }, seletedschoollist);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        tvSelectedSchools.setHasFixedSize(true);
        tvSelectedSchools.setLayoutManager(mLayoutManager);
        tvSelectedSchools.setItemAnimator(new DefaultItemAnimator());
        tvSelectedSchools.setAdapter(adapter);

        llRecipients = (LinearLayout) findViewById(R.id.mgtImage_llRecipient);
        llRecipients.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        schoolsListAPI();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra("MESSAGE", msg);
        setResult(IMAGE_UPLOAD_STATUS, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_SCHOOL_POSITION) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {

                TeacherSchoolsModel schoolsModel = (TeacherSchoolsModel) data.getSerializableExtra("SCHOOL");
                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListClasses(schoolsModel.getListClasses());
                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListGroups(schoolsModel.getListGroups());

                TeacherSelectedschoolListAdapter.bEditClick = true;
            }


        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mgtImage_btnChooseSchools:

                setupSchoolsListPopUp();
                pWindowSchools.showAtLocation(btnSelectSchool, Gravity.NO_GRAVITY, 0, 0);
                break;

            case R.id.mgtImage_btnSend:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtImageCircular.this).equals(LOGIN_TYPE_ADMIN))
                    sendAdminImageToListOfSchoolsAPI();
                else sendMgtImageToListOfSchoolsAPI();
                break;

            case R.id.mgtImage_btnSendToAllSchools:
                sendImageToAllSchoolAPI();
                break;
        }
    }

    private void schoolsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String mobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherMgtImageCircular.this);
        final String callerType;
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtImageCircular.this).equals(LOGIN_TYPE_ADMIN))
            callerType = "A";
        else callerType = "M";

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetSchoolLists(mobileNumber, callerType);
        Call<JsonArray> call = apiService.GetSchoolLists(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("GetSchools:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetSchools:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strSubName = jsonObject.getString("SchoolName");
                        String strSubCode = jsonObject.getString("SchoolID");

                        if (strSubName.equals("")) {
                            showToast(strSubCode);
                            onBackPressed();
                        } else {
                            TeacherSchoolsModel schoolsModel;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                schoolsModel = new TeacherSchoolsModel(jsonObject.getString("SchoolName"), jsonObject.getString("SchoolID")
                                        , jsonObject.getString("SchoolLogo"), jsonObject.getString("SchoolAddress"), false);

                                schoolsModel.setStrStaffID(jsonObject.getString("StaffID"));

                                Log.d("Testing", "1***********************");
                                ArrayList<TeacherClassGroupModel> listStd = new ArrayList<>();
                                JSONArray jsAryStd = jsonObject.getJSONArray("StdCode");
                                Log.d("Testing", "2***********************");
                                if (jsAryStd.length() > 0) {
                                    JSONObject jObjStd;
                                    TeacherClassGroupModel classGroups;
                                    Log.d("Testing", "3***********************");
                                    for (int j = 0; j < jsAryStd.length(); j++) {
                                        jObjStd = jsAryStd.getJSONObject(j);
                                        classGroups = new TeacherClassGroupModel(jObjStd.getString("Stdname"), jObjStd.getString("StdId"), true);
                                        listStd.add(classGroups);

                                        Log.d("Testing", "4***********************");
                                    }
                                }

                                ArrayList<TeacherClassGroupModel> listGroups = new ArrayList<>();
                                JSONArray jsAryGrp = jsonObject.getJSONArray("GrpCode");

                                Log.d("Testing", "5***********************");
                                if (jsAryGrp.length() > 0) {
                                    JSONObject jObjGrp;
                                    TeacherClassGroupModel classGroups;

                                    Log.d("Testing", "6***********************");
                                    for (int j = 0; j < jsAryGrp.length(); j++) {
                                        jObjGrp = jsAryGrp.getJSONObject(j);
                                        classGroups = new TeacherClassGroupModel(jObjGrp.getString("GroupName"), jObjGrp.getString("GroupCode"), true);
                                        listGroups.add(classGroups);

                                        Log.d("Testing", "7***********************");
                                    }
                                }

                                schoolsModel.setListClasses(listStd);
                                schoolsModel.setListGroups(listGroups);
                                arrSchoolList.add(schoolsModel);

                                Log.d("Testing", "8***********************");
                            }

                            TeacherSelectedschoolListAdapter.bEditClick = false;
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.check_internet));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("GetSchools:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private void setupSchoolsListPopUp() {

        final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_schools, null);

        RecyclerView rvMembers = (RecyclerView) layout.findViewById(R.id.popupSchools_rvSchools);

        TextView tvCancel = (TextView) layout.findViewById(R.id.popupSchools_tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pWindowSchools.dismiss();
            }
        });

        final TextView tvOk = (TextView) layout.findViewById(R.id.popupSchools_tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pWindowSchools.dismiss();
                adapter.notifyDataSetChanged();
                enableSend();
            }
        });

        TeacherSchoolsListAdapter schoolsListAdapter =
                new TeacherSchoolsListAdapter(TeacherMgtImageCircular.this, new TeacherOnCheckSchoolsListener() {
                    @Override
                    public void school_addSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (!seletedschoollist.contains(school))) {
                            seletedschoollist.add(school);
                            i_schools_count++;
                        }
                    }

                    @Override
                    public void school_removeSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (seletedschoollist.contains(school))) {
                            seletedschoollist.remove(school);
                            i_schools_count--;
                        }
                    }
                }, arrSchoolList);

        pWindowSchools = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pWindowSchools.setContentView(layout);

        rvMembers.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherMgtImageCircular.this);
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.addItemDecoration(new DividerItemDecoration(TeacherMgtImageCircular.this, LinearLayoutManager.VERTICAL));
        rvMembers.setItemAnimator(new DefaultItemAnimator());
        rvMembers.setAdapter(schoolsListAdapter);
    }

    private void enableDisableNext(TextView tvOK) {
        Log.d("i_students_count", "count: " + i_schools_count);


        if (i_schools_count > 0) {
            tvOK.setEnabled(true);
        } else {
            tvOK.setEnabled(false);
        }
    }

    private void enableSend() {
        Log.d("i_students_count", "count: " + i_schools_count);


        if (i_schools_count > 0) {
            llRecipients.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
        } else {
            llRecipients.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }
    }

    private void sendImageCircularRetroFit() {


        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strSelectedImgFilePath);
        Log.d("FILE_Path", strSelectedImgFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        JsonArray jsonReqArray = constructResultJsonArray();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtImageCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();



        Call<JsonArray> call = apiService.StaffwiseImage(requestBody, bodyFile);
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
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {


                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast("Server Response Failed. Try again");
                        }


                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }


    private JsonArray constructResultJsonArray() {
        JsonArray jsonArraySchool = new JsonArray();

        try {

            JsonObject jsonObjectSchool = new JsonObject();

            JsonArray jsonArrayStdSections = new JsonArray();
            for (int i = 0; i < seletedschoollist.size(); i++) {

                {
                    JsonObject jsonObjectStdSections = new JsonObject();

                    jsonObjectStdSections.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectStdSections.addProperty("SchoolName", seletedschoollist.get(i).getStrSchoolName());
                    jsonObjectStdSections.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());



                    JsonArray jsonArrayStandards = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleStds = new ArrayList<>();
                    seleStds.addAll(seletedschoollist.get(i).getListClasses());

                    if (seleStds.size() > 0) {
                        for (int j = 0; j < seleStds.size(); j++) {
                            if (seleStds.get(j).isbSelected()) {
                                JsonObject jsonObjectStandards = new JsonObject();
                                jsonObjectStandards.addProperty("StdID", seleStds.get(j).getStrID());
                                jsonObjectStandards.addProperty("StdName", seleStds.get(j).getStrName());
                                jsonArrayStandards.add(jsonObjectStandards);
                            }
                        }
                    }

                    JsonArray jsonArrayGroups = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleGroups = new ArrayList<>();
                    seleGroups.addAll(seletedschoollist.get(i).getListGroups());

                    if (seleGroups.size() > 0) {
                        for (int j = 0; j < seleGroups.size(); j++) {
                            if (seleGroups.get(j).isbSelected()) {
                                JsonObject jsonObjectGrops = new JsonObject();
                                jsonObjectGrops.addProperty("GroupID", seleGroups.get(j).getStrID());
                                jsonObjectGrops.addProperty("GroupName", seleGroups.get(j).getStrName());
                                jsonArrayGroups.add(jsonObjectGrops);
                            }
                        }
                    }

                    jsonObjectStdSections.add("Stds", jsonArrayStandards);
                    jsonObjectStdSections.add("Groups", jsonArrayGroups);
                    jsonArrayStdSections.add(jsonObjectStdSections);
                }
            }
            jsonObjectSchool.add("Schools", jsonArrayStdSections);
            jsonArraySchool.add(jsonObjectSchool);

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }

    // Upload API

    private void sendImageToAllSchoolAPI() {
        String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherMgtImageCircular.this);
        String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherMgtImageCircular.this);
        String mobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherMgtImageCircular.this);
        final String callerType;
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtImageCircular.this).equals(LOGIN_TYPE_ADMIN))
            callerType = "A";
        else callerType = "M";
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strSelectedImgFilePath);
        Log.d("FILE_Path", strSelectedImgFilePath);

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetImageMgtAdmin(schoolID, staffID, callerType, mobileNumber);
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtImageCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendImageMgtAdminToAllSchools(requestBody, bodyFile);
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
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
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

    private void sendAdminImageToListOfSchoolsAPI() {
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strSelectedImgFilePath);
        Log.d("FILE_Path", strSelectedImgFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);//"image/png"

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        JsonArray jsonReqArray = constructJsonArrayAdminSchools();
        RequestBody requestBody =
                RequestBody.create(MultipartBody.FORM
                        , jsonReqArray.toString());//MediaType.parse("text/plain")



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtImageCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendImageAdminToSchools(requestBody, bodyFile);

        Log.d("Upload:Req", "Call:" + call.toString());
        Log.d("Upload:Req", "REQUEST:" + call.request().toString());

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
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {

                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
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

    private JsonArray constructJsonArrayAdminSchools() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
            for (int i = 0; i < seletedschoollist.size(); i++) {
                JsonObject jsonObjectSchool = new JsonObject();

                jsonObjectSchool.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                jsonObjectSchool.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                jsonArraySchool.add(jsonObjectSchool);
            }

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }

    private void sendMgtImageToListOfSchoolsAPI() {
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(strSelectedImgFilePath);
        Log.d("FILE_Path", strSelectedImgFilePath);

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        JsonArray jsonReqArray = constructJsonArrayMgtSchools();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtImageCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendImageMgtStdGrp(requestBody, bodyFile);
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
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
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

    private JsonArray constructJsonArrayMgtSchools() {
        JsonArray jsonArraySchool = new JsonArray();

        try {

            for (int i = 0; i < seletedschoollist.size(); i++) {

                {
                    JsonObject jsonObjectSchool = new JsonObject();
                    jsonObjectSchool.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectSchool.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());

                    JsonArray jsonArrayStandards = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleStds = new ArrayList<>();
                    seleStds.addAll(seletedschoollist.get(i).getListClasses());

                    if (seleStds.size() > 0) {
                        for (int j = 0; j < seleStds.size(); j++) {
                            if (seleStds.get(j).isbSelected()) {
                                JsonObject jsonObjectStandards = new JsonObject();
                                jsonObjectStandards.addProperty("TargetCode", seleStds.get(j).getStrID());
                                jsonArrayStandards.add(jsonObjectStandards);
                            }
                        }
                    }

                    JsonArray jsonArrayGroups = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleGroups = new ArrayList<>();
                    seleGroups.addAll(seletedschoollist.get(i).getListGroups());

                    if (seleGroups.size() > 0) {
                        for (int j = 0; j < seleGroups.size(); j++) {
                            if (seleGroups.get(j).isbSelected()) {
                                JsonObject jsonObjectGrops = new JsonObject();
                                jsonObjectGrops.addProperty("TargetCode", seleGroups.get(j).getStrID());
                                jsonArrayGroups.add(jsonObjectGrops);
                            }
                        }
                    }

                    jsonObjectSchool.add("Seccode", jsonArrayStandards);
                    jsonObjectSchool.add("GrpCode", jsonArrayGroups);
                    jsonArraySchool.add(jsonObjectSchool);
                }
            }

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }
}
