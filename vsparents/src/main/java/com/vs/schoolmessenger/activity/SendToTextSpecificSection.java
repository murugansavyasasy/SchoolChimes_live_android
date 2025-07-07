package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AddCouponPoints;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendToTextSpecificSection extends AppCompatActivity implements View.OnClickListener {
    Button SendToEntireSchool, SendToStansGroups, SendToSpecificSection, SendToStaff;
    String SchoolID, StaffID, filepath, duration, tittle, strmessage, strdate, strtime, strfilepathimage;
    int iRequestCode;
    ArrayList<TeacherClassGroupModel> listClasses, listGroups;

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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.sens_specific_section);
        SendToEntireSchool = (Button) findViewById(R.id.SendToEntireSchool);
        SendToEntireSchool.setOnClickListener(this);
        SendToStansGroups = (Button) findViewById(R.id.SendToStansGroups);
        SendToStansGroups.setOnClickListener(this);
        SendToSpecificSection = (Button) findViewById(R.id.SendToSpecificSection);
        SendToStaff = findViewById(R.id.SendToStaff);
        SendToSpecificSection.setOnClickListener(this);
        SendToStaff.setOnClickListener(this);

        String countryID = TeacherUtil_SharedPreference.getCountryID(SendToTextSpecificSection.this);
        if (countryID.equals("11")) {
            SendToStansGroups.setText("Send to Grade/Groups");
            SendToSpecificSection.setText("Send to Grade/sections");
        }
        ImageView ivBack = (ImageView) findViewById(R.id.emergVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        filepath = getIntent().getExtras().getString("FILEPATH", "");
        duration = getIntent().getExtras().getString("DURATION", "");
        tittle = getIntent().getExtras().getString("TITTLE", "");
        strmessage = getIntent().getExtras().getString("MESSAGE", "");
        strdate = getIntent().getExtras().getString("DATE", "");
        strtime = getIntent().getExtras().getString("TIME", "");
        strfilepathimage = getIntent().getExtras().getString("FILE_PATH_IMAGE", "");

        listClasses = new ArrayList<>();
        listGroups = new ArrayList<>();


        if ((listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SendToEntireSchool:
                showAlert();
                break;
            case R.id.SendToStansGroups:
                Intent inPrincipal = new Intent(SendToTextSpecificSection.this, TextStandardAndGroupList.class);
                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                inPrincipal.putExtra("SCHOOL_ID", SchoolID);
                inPrincipal.putExtra("STAFF_ID", StaffID);
                inPrincipal.putExtra("FILEPATH", filepath);
                inPrincipal.putExtra("DURATION", duration);
                inPrincipal.putExtra("TITTLE", tittle);
                inPrincipal.putExtra("MESSAGE", strmessage);
                startActivity(inPrincipal);


                break;

            case R.id.SendToSpecificSection:
                Intent intoSec = new Intent(SendToTextSpecificSection.this, TeacherStaffStandardSection.class);
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("FILEPATH", filepath);
                intoSec.putExtra("SCHOOL_ID", SchoolID);
                intoSec.putExtra("STAFF_ID", StaffID);
                intoSec.putExtra("DURATION", duration);
                intoSec.putExtra("TITTLE", tittle);
                intoSec.putExtra("MESSAGE", strmessage);
                intoSec.putExtra("TO", "SEC");
                startActivity(intoSec);
                break;

        }

    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendToTextSpecificSection.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.confirm_to_send);
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendToEntireSchool();
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


    private void sendToEntireSchool() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(SendToTextSpecificSection.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(SendToTextSpecificSection.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolstextentireschool();
        Call<JsonArray> call = apiService.SendSMSToGroupsAndStandards(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert1(strMsg, strStatus);
                                AddCouponPoints.addPoints(SendToTextSpecificSection.this, Util_Common.SEND_TEXT_POINTS);

                            } else {
                                showAlert1(strMsg, strStatus);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void showAlert1(String strMsg, final String status) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SendToTextSpecificSection.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {
                    dialog.cancel();
                    Intent homescreen = new Intent(SendToTextSpecificSection.this, Teacher_AA_Test.class);
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

    private JsonObject constructJsonArrayMgtSchoolstextentireschool() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {
            jsonObjectSchoolstdgrp.addProperty("SchoolID", SchoolID);
            jsonObjectSchoolstdgrp.addProperty("StaffID", StaffID);
            jsonObjectSchoolstdgrp.addProperty("Description", tittle);
            jsonObjectSchoolstdgrp.addProperty("isEntireSchool", "T");
            jsonObjectSchoolstdgrp.addProperty("Message", strmessage);
            JsonArray jsonArrayschoolstd = new JsonArray();

            for (int i = 0; i < listClasses.size(); i++) {
                if (listClasses.get(i).isbSelected()) {
                    JsonObject jsonObjectclass = new JsonObject();
                    jsonObjectclass.addProperty("TargetCode", listClasses.get(i).getStrID());
                    jsonArrayschoolstd.add(jsonObjectclass);
                }
            }

            JsonArray jsonArrayschoolgrp = new JsonArray();
            for (int i = 0; i < listGroups.size(); i++) {
                if (listGroups.get(i).isbSelected()) {
                    JsonObject jsonObjectgroups = new JsonObject();
                    jsonObjectgroups.addProperty("TargetCode", listGroups.get(i).getStrID());
                    jsonArrayschoolgrp.add(jsonObjectgroups);
                }
            }

            jsonObjectSchoolstdgrp.add("StdCode", jsonArrayschoolstd);
            jsonObjectSchoolstdgrp.add("GrpCode", jsonArrayschoolgrp);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

