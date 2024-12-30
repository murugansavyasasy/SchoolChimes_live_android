package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ElementExamListAdapter;
import com.vs.schoolmessenger.adapter.ExamMarksExpandableListAdapter;
import com.vs.schoolmessenger.adapter.GroupExamListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Element;
import com.vs.schoolmessenger.model.Group;
import com.vs.schoolmessenger.model.Subgroup;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewExamMarks extends AppCompatActivity {
    String child_id, schoolid, examid;
    android.widget.ScrollView ScrollView;
    ExpandableListView expSubjects;
    RecyclerView rcyElements, rcyGroups;
    ElementExamListAdapter elementExamListAdapter;
    GroupExamListAdapter groupExamListAdapter;
    ExamMarksExpandableListAdapter examMarksExpandableListAdapter;
    HashMap<String, ArrayList<String>> mapchild = new HashMap<>();
    List<String> mapParent = new ArrayList<>();

    List<Element> elementList = new ArrayList<>();
    List<Group> groupList = new ArrayList<>();
    ConstraintLayout parentlayout;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_view_exam_marks);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView)
                getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.student);
        ((TextView)
                getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.marksss);
        ((ImageView)
                getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new
                                                                                                                       View.OnClickListener() {
                                                                                                                           @Override
                                                                                                                           public void onClick(View v) {
                                                                                                                               onBackPressed();
                                                                                                                           }
                                                                                                                       });
        ScrollView = findViewById(R.id.scrollView);
        expSubjects = findViewById(R.id.expSubjects);
        rcyGroups = findViewById(R.id.rcyGroups);
        rcyElements = findViewById(R.id.rcyElements);
        parentlayout = findViewById(R.id.parentlayout);
        examid = getIntent().getStringExtra("examid");
        child_id = Util_SharedPreference.getChildIdFromSP(this);
        schoolid = Util_SharedPreference.getSchoolIdFromSP(this);
        expSubjects.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });
        expSubjects.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
            }
        });
        expSubjects.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int
                    childPosition, long id) {
                return false;
            }
        });
        expSubjects.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int lastExpandedPosition = -1;

            @Override
            public void onGroupExpand(int groupPosition) {

                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expSubjects.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });
        marksApi();
    }

    private void marksApi() {
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService =
                TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolID", schoolid);
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("ExamID", examid);
        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetStudentExamMark(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("marks:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        mapParent.clear();
                        mapchild.clear();
                        groupList.clear();
                        elementList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            String status = jsonObject.getString("Status");
                            String message = jsonObject.getString("Message");
                            if (status.equals("1")) {
                                parentlayout.setVisibility(View.VISIBLE);
                                ScrollView.setVisibility(View.VISIBLE);
                                JSONArray data = jsonObject.getJSONArray("Data");
                                for (int k = 0; k < data.length(); k++) {

                                    JSONObject subjects = data.getJSONObject(k);

                                    JSONArray subjectarray = subjects.getJSONArray("subjects");
                                    JSONArray elementsarray = subjects.getJSONArray("elements");
                                    JSONArray groupsarray = subjects.getJSONArray("groups");

                                    for (int l = 0; l < subjectarray.length(); l++) {
                                        JSONObject splits = subjectarray.getJSONObject(l);
                                        String subjectname = splits.getString("subjectname");
                                        String maxmark = splits.getString("maxmark");
                                        String markobt = splits.getString("markobt");
                                        String is_fail = splits.getString("is_fail");

                                        JSONArray splitarray = splits.getJSONArray("split");
                                        String subjectcreds = subjectname + " : " + markobt + " / " + maxmark + "_" + is_fail;

                                        if (splitarray.length() != 0) {
                                            mapParent.add(subjectcreds);

                                            ArrayList<String> marks = new ArrayList<>();
                                            for (int m = 0; m < splitarray.length(); m++) {

                                                JSONObject splitsmarks = splitarray.getJSONObject(m);

                                                String name = splitsmarks.getString("name");
                                                String splitmaxmark = splitsmarks.getString("maxmark");
                                                String splitmarkobt = splitsmarks.getString("markobt");
                                                String fail = splits.getString("is_fail");
                                                Log.d("name", name);
                                                String splitscreds = name + " : " + splitmarkobt + " / " + splitmaxmark + "_" + fail;
                                                marks.add(splitscreds);

                                                mapchild.put(subjectcreds, marks);

                                            }
                                        }
                                    }

                                    for (int n = 0; n < elementsarray.length(); n++) {

                                        JSONObject elements = elementsarray.getJSONObject(n);
                                        String elementname = elements.getString("name");
                                        String elementmark = elements.getString("mark");

                                        Element element = new Element(elementmark, elementname);

                                        elementList.add(element);

                                    }

                                    for (int p = 0; p < groupsarray.length(); p++) {

                                        JSONObject groups = groupsarray.getJSONObject(p);
                                        String groupname = groups.getString("name");
                                        String groupmark = groups.getString("mark");
                                        JSONArray subgrouparray = groups.getJSONArray("subgroups");

                                        List<Subgroup> subgrouplist = new ArrayList<>();
                                        for (int i = 0; i < subgrouparray.length(); i++) {

                                            JSONObject subgroup = subgrouparray.getJSONObject(i);
                                            String subgroupname = subgroup.getString("name");
                                            String subgroupmark = subgroup.getString("mark");
                                            Subgroup subgroup1 = new Subgroup(subgroupmark, subgroupname);
                                            subgrouplist.add(subgroup1);
                                            Log.d("size", String.valueOf(subgrouplist.size()));

                                        }

                                        Group group = new Group(groupname, groupmark, subgrouplist);
                                        groupList.add(group);

                                    }

                                }
                                examMarksExpandableListAdapter = new
                                        ExamMarksExpandableListAdapter(ViewExamMarks.this, mapParent, mapchild);
                                expSubjects.setAdapter(examMarksExpandableListAdapter);
                                if (elementList.size() != 0) {
                                    rcyElements.setVisibility(View.VISIBLE);

                                    RecyclerView.LayoutManager layoutManager = new

                                            LinearLayoutManager(getApplicationContext());
                                    rcyElements.setLayoutManager(layoutManager);
                                    rcyElements.setItemAnimator(new DefaultItemAnimator());
                                    elementExamListAdapter = new
                                            ElementExamListAdapter(ViewExamMarks.this, elementList);
                                    rcyElements.setAdapter(elementExamListAdapter);
                                } else {

                                    rcyElements.setVisibility(View.GONE);
                                }
                                if (groupList.size() != 0) {
                                    rcyGroups.setVisibility(View.VISIBLE);

                                    RecyclerView.LayoutManager layoutManagergroup = new

                                            LinearLayoutManager(getApplicationContext());
                                    rcyGroups.setLayoutManager(layoutManagergroup);
                                    rcyGroups.setItemAnimator(new DefaultItemAnimator());
                                    groupExamListAdapter = new
                                            GroupExamListAdapter(ViewExamMarks.this, groupList);
                                    rcyGroups.setAdapter(groupExamListAdapter);
                                } else {

                                    rcyGroups.setVisibility(View.GONE);
                                }
                            } else {
                                ScrollView.setVisibility(View.GONE);
                                showAlertfinish(message);
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        Toast.makeText(ViewExamMarks.this, R.string.Server_Response_Failed,
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(ViewExamMarks.this, R.string.Server_Connection_Failed,
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlertfinish(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }
}