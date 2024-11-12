package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.MarkListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.SubjectAndMarkList;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class MarksListScreen extends AppCompatActivity {
    public MarkListAdapter mAdapter;
    RecyclerView marks_list_recycle;
    ArrayList<SubjectAndMarkList> mark_list_item = new ArrayList<SubjectAndMarkList>();
    String child_ID, school_ID, Exam_ID;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.marks_list);


        marks_list_recycle = (RecyclerView) findViewById(R.id.marks_list_recycle);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        child_ID = Util_SharedPreference.getChildIdFromSP(MarksListScreen.this);
        school_ID = Util_SharedPreference.getSchoolIdFromSP(MarksListScreen.this);
        Exam_ID = getIntent().getExtras().getString("exam_id");

        getMarks();

        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.student);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.marksss);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mAdapter = new MarkListAdapter(mark_list_item, MarksListScreen.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        marks_list_recycle.setLayoutManager(mLayoutManager);
        marks_list_recycle.setItemAnimator(new DefaultItemAnimator());
        marks_list_recycle.setAdapter(mAdapter);
        marks_list_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);

    }

    private void getMarks() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(MarksListScreen.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(MarksListScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(MarksListScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", child_ID);
        jsonObject.addProperty("SchoolID", school_ID);
        jsonObject.addProperty("ExamID", Exam_ID);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.GetStudentExamMarks(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {

                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);


                                String StudentID = jsonObject.getString("StudentID");
                                String StudentName = jsonObject.getString("StudentName");
                                if (StudentID.equals("-2")) {
                                    Toast.makeText(getApplicationContext(), StudentName, Toast.LENGTH_SHORT).show();
                                } else {
                                    JSONArray marks_array = jsonObject.getJSONArray("details");
                                    mAdapter.clearAllData();
                                    SubjectAndMarkList values;

                                    for (int j = 0; j < marks_array.length(); j++) {
                                        JSONObject object = marks_array.getJSONObject(j);
                                        String subject = object.getString("Subject");
                                        String mark = object.getString("Marks");
                                        values = new SubjectAndMarkList(subject, mark);
                                        mark_list_item.add(values);

                                    }

                                    Log.d("size1234", String.valueOf(mark_list_item.size()));
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }
}



