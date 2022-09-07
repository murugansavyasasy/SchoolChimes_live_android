package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherAbsenteesExpandableListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherABS_Section;
import com.vs.schoolmessenger.model.TeacherABS_Standard;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherParticularsScreen extends AppCompatActivity {

    ExpandableListView expListView;
    ArrayList<TeacherABS_Section> listGropus = new ArrayList<>();
    Map<TeacherABS_Standard, ArrayList<TeacherABS_Section>> mapStandardsAndGroups;
    ArrayList<TeacherABS_Standard> listHeaderParent;
    TeacherAbsenteesExpandableListAdapter absenteesExpandableListAdapter;
    String SchoolID, StaffID;
    ArrayList<TeacherABS_Section> listClassGroupsCollection;
    String totstudstrength, totstaffstrength;
    TextView tv_staffcount, tv_studentcount;
    int iRequestCode;
    private int lastExpandedPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_particulars_screen);

//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("Reqcode", String.valueOf(iRequestCode));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.school);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.strenth);


        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);

        expListView = (ExpandableListView) findViewById(R.id.parti_exlvList);
        tv_staffcount = (TextView) findViewById(R.id.parti_tv2);
        tv_studentcount = (TextView) findViewById(R.id.parti_tv4);

//        if(iRequestCode == PRINCIPAL_SCHOOLSTRENGTH && (listschooldetails.size()).)

        if((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherParticularsScreen.this).equals(LOGIN_TYPE_PRINCIPAL))&&(listschooldetails.size()==1)){
            SchoolID= TeacherUtil_Common.Principal_SchoolId ;
            StaffID= TeacherUtil_Common.Principal_staffId;
        }
        else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
//        TeacherUtil_Common.Principal_StaffId = StaffID;
            Log.d("SchoolID", SchoolID + " " + StaffID);

        }

//        tempListItems();
//        createGroupList();
//        createCollection();

        ParticularsListAPI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
    }
    private void tempListItems() {
        listGropus.add(new TeacherABS_Section("A", "50"));
        listGropus.add(new TeacherABS_Section("B", "52"));
        listGropus.add(new TeacherABS_Section("C", "49"));
        listGropus.add(new TeacherABS_Section("D", "55"));
    }

//    private void createGroupList() {
//        for (int i = 0; i < 5; i++) {
//            listHeaderParent.add(new TeacherABS_Standard("" + (i + 1), (i + 200) + ""));
//        }
//    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
//        returnIntent.putExtra("STD_SEC", stdSec);
        setResult(iRequestCode, returnIntent);
        finish();
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
        backToResultActvity("BACK");
    }
    private void createCollection() {
        mapStandardsAndGroups = new LinkedHashMap<>();
        listClassGroupsCollection = new ArrayList<>();
        for (TeacherABS_Standard groupclass : listHeaderParent) {
//            if (groupclass.equals(LIST_STANDARDS))
            loadSubList(listGropus);

            mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void loadSubList(ArrayList<TeacherABS_Section> laptopModels) {

        for (TeacherABS_Section model : laptopModels) {
            Log.e("SECTION", model.getSection());
            listClassGroupsCollection.add(model);
        }
    }


    private void ParticularsListAPI() {
        listHeaderParent = new ArrayList<>();

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherParticularsScreen.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherParticularsScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherParticularsScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetSchoolStrengthBySchoolID(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        totstudstrength = jsonObject.getString("TotalStudentStrength");
                        totstaffstrength = jsonObject.getString("TotalStaffStrength");
                        tv_staffcount.setText(totstaffstrength);
                        tv_studentcount.setText(totstudstrength);

                        Log.d("json length", js.length() + "");
//                        for (int i = 0; i < js.length(); i++)
//                        {
//                            mapStandardsAndGroups = new LinkedHashMap<>();
//                            listClassGroupsCollection = new ArrayList<>();
                            JSONArray jSONArray = jsonObject.getJSONArray("Standards");

                            for (int j = 0; j < jSONArray.length(); j++)
                            {
                                JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
                                TeacherABS_Standard abs_standard;

                                abs_standard = new TeacherABS_Standard(jsonObjectgroups.getString("StdName"), jsonObjectgroups.getString("TotalStudents"));
                                Log.d("STD&SEC", jsonObjectgroups.getString("StdName") + jsonObjectgroups.getString("TotalStudents"));
//                                listHeaderParent.add(abs_standard);
//                                Log.d("Size", String.valueOf(listHeaderParent.size()));
//                                Log.d("tttt", jsonObjectgroups.toString());
                                JSONArray jSONArraysection = jsonObjectgroups.getJSONArray("Sections");
//                                Log.d("jSONArraysection",jsonObjectgroups.getJSONArray("Sections")+"");

                                if (jSONArraysection.length() > 0) {
                                    listGropus = new ArrayList<>();
                                    for (int k = 0; k < jSONArraysection.length(); k++) {
                                        JSONObject jsonObjectsections = jSONArraysection.getJSONObject(k);
                                        TeacherABS_Section abs_section;
                                        abs_section = new TeacherABS_Section(jsonObjectsections.getString("SecName"), jsonObjectsections.getString("TotalStudents"));
                                        Log.d("childlist", abs_section.getSection());
                                        listGropus.add(abs_section);
                                    }
                                    abs_standard.setSections(listGropus);
                                    listHeaderParent.add(abs_standard);

                                }
                                else{

                                    listGropus = new ArrayList<>();
                                    for (int k = 0; k < jSONArraysection.length(); k++) {
                                        JSONObject jsonObjectsections = jSONArraysection.getJSONObject(k);
                                        TeacherABS_Section abs_section;
                                        abs_section = new TeacherABS_Section("", "");
                                        Log.d("childlist", abs_section.getSection());
                                        listGropus.add(abs_section);
                                    }
                                    abs_standard.setSections(listGropus);
                                    Log.d("Sections","No Sections assigned");
                                    listHeaderParent.add(abs_standard);
                                }


                            }


                            mapStandardsAndGroups = new LinkedHashMap<>();
//                            listClassGroupsCollection = new ArrayList<>();
                            for (TeacherABS_Standard groupclass : listHeaderParent) {
                                listClassGroupsCollection = new ArrayList<>();
                                loadSubList(groupclass.getSections());
                                mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
                            }

                            absenteesExpandableListAdapter = new TeacherAbsenteesExpandableListAdapter(
                                    TeacherParticularsScreen.this, listHeaderParent, mapStandardsAndGroups, expListView);



                        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

                            @Override
                            public void onGroupExpand(int groupPosition) {
                                if (lastExpandedPosition != -1
                                        && groupPosition != lastExpandedPosition) {
                                    expListView.collapseGroup(lastExpandedPosition);
                                }
                                lastExpandedPosition = groupPosition;
                            }
                        });



                            expListView.setAdapter(absenteesExpandableListAdapter);
//                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("SubjectHandling:Failure", t.toString());
            }
        });
    }


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", SchoolID);
            Log.d("schoolid", SchoolID);
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
