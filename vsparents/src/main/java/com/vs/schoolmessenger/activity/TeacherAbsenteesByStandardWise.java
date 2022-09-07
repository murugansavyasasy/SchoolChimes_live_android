package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
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
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;


public class TeacherAbsenteesByStandardWise extends AppCompatActivity {

    ExpandableListView expListView;
    ArrayList<TeacherABS_Section> listGropus = new ArrayList<>();
    Map<TeacherABS_Standard, ArrayList<TeacherABS_Section>> mapStandardsAndGroups;
    ArrayList<TeacherABS_Standard> listHeaderParent = new ArrayList<>();
    TeacherAbsenteesExpandableListAdapter absenteesExpandableListAdapter;

    ArrayList<TeacherABS_Section> listClassGroupsCollection;

    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_absentees_by_standard_wise);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.absentess_report);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.standard_wise);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);

        expListView = (ExpandableListView) findViewById(R.id.absEx_exlvList);


        listHeaderParent = (ArrayList<TeacherABS_Standard>) getIntent().getSerializableExtra(
                "LIST_STDS");

        Log.d("standard List", String.valueOf(listHeaderParent.size()));
        loadStdSections();

        absenteesExpandableListAdapter = new TeacherAbsenteesExpandableListAdapter(
                this, listHeaderParent, mapStandardsAndGroups, expListView);

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

    private void tempListItems() {
        listGropus.add(new TeacherABS_Section("A", "5/50"));
        listGropus.add(new TeacherABS_Section("B", "4/52"));
        listGropus.add(new TeacherABS_Section("C", "5/49"));
        listGropus.add(new TeacherABS_Section("D", "7/55"));
    }

    private void createGroupList() {
        for (int i = 0; i < 5; i++) {
            listHeaderParent.add(new TeacherABS_Standard("" + (i + 1), (i + 10) + "/200"));
        }
    }

    private void createCollection() {
        mapStandardsAndGroups = new LinkedHashMap<>();
        for (TeacherABS_Standard groupclass : listHeaderParent) {
            loadSubList(listGropus);

            mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
        }
    }

    private void loadSubList(ArrayList<TeacherABS_Section> laptopModels) {
        Log.d("SectionCount", "" + laptopModels.size());
        listClassGroupsCollection = new ArrayList<>();
        for (TeacherABS_Section model : laptopModels)
            listClassGroupsCollection.add(model);
    }


    private void loadStdSections() {
        mapStandardsAndGroups = new LinkedHashMap<>();
        for (TeacherABS_Standard groupclass : listHeaderParent) {
            Log.d("STD", groupclass.getStandard());
            loadSubList(groupclass.getSections());
            mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
        }
    }



    private void AbsenteesByStdListAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherAbsenteesByStandardWise.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherAbsenteesByStandardWise.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherAbsenteesByStandardWise.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        listHeaderParent = new ArrayList<>();
        listGropus = new ArrayList<>();

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolStd();
        Call<JsonArray> call = apiService.GetAbsenteesCountByDate(jsonReqArray);
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
                        Log.d("json length", js.length() + "");
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);

                            JSONArray jSONArray = jsonObject.getJSONArray("ClassWise");
                            for (int j = 0; j < jSONArray.length(); j++) {
                                JSONObject jsonObjectgroups = jSONArray.getJSONObject(j);
                                TeacherABS_Standard abs_standard;
                                abs_standard = new TeacherABS_Standard(jsonObjectgroups.getString("ClassName"), jsonObjectgroups.getString("TotalAbsentees"));
                                Log.d("STD&SEC", jsonObjectgroups.getString("ClassName") + jsonObjectgroups.getString("TotalAbsentees"));
                                Log.d("Size", String.valueOf(listHeaderParent.size()));
                                Log.d("tttt", jsonObjectgroups.toString());
                                JSONArray jSONArraysection = jsonObjectgroups.getJSONArray("SectionWise");

                                if (jSONArraysection.length() > 0) {
                                    listGropus = new ArrayList<>();
                                    for (int k = 0; k < jSONArraysection.length(); k++) {
                                        JSONObject jsonObjectsections = jSONArraysection.getJSONObject(k);
                                        TeacherABS_Section abs_section;
                                        abs_section = new TeacherABS_Section(jsonObjectsections.getString("SectionName"), jsonObjectsections.getString("TotalAbsentees"));
                                        Log.d("childlist", abs_section.getSection());
                                        listGropus.add(abs_section);
                                    }
                                    abs_standard.setSections(listGropus);
                                }

                                listHeaderParent.add(abs_standard);
                            }


                            mapStandardsAndGroups = new LinkedHashMap<>();
                            for (TeacherABS_Standard groupclass : listHeaderParent) {
                                listClassGroupsCollection = new ArrayList<>();
                                loadSubList(groupclass.getSections());
                                mapStandardsAndGroups.put(groupclass, listClassGroupsCollection);
                            }

                            absenteesExpandableListAdapter = new TeacherAbsenteesExpandableListAdapter(
                                    TeacherAbsenteesByStandardWise.this, listHeaderParent, mapStandardsAndGroups, expListView);
                            expListView.setAdapter(absenteesExpandableListAdapter);


                        }
                    } else {
                        showToast("Server Response Failed. Try again");
                    }

                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("SubjectHandling:Failure", t.toString());
            }
        });
    }


    private JsonObject constructJsonArrayMgtSchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);
            Log.d("schoolid", Principal_SchoolId);
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
