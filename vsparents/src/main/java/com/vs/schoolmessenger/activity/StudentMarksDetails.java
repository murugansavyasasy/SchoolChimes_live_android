package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.MarkListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamList;
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

public class StudentMarksDetails extends AppCompatActivity {


    public MarkListAdapter mAdapter;
    String child_ID, school_ID, Exam_ID;
    TextView lblStudentName, lblClStandard, lblexamName, lblMaximumMarks, lblHighest, lblAverage, lblSchoolName, lblSection;
    LinearLayout lnrStudentDetails;
    Button btnDisplayMarks;
    private final ArrayList<SubjectAndMarkList> mark_list = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.exams_marks);


        lblStudentName = (TextView) findViewById(R.id.lblStudentName);
        lblClStandard = (TextView) findViewById(R.id.lblClStandard);
        lblexamName = (TextView) findViewById(R.id.lblexamName);
        lblMaximumMarks = (TextView) findViewById(R.id.lblMaximumMarks);
        lblHighest = (TextView) findViewById(R.id.lblHighest);
        lblAverage = (TextView) findViewById(R.id.lblAverage);
        lblSchoolName = (TextView) findViewById(R.id.lblSchoolName);
        lblSection = (TextView) findViewById(R.id.lblSection);
        lnrStudentDetails = (LinearLayout) findViewById(R.id.lnrStudentDetails);
        btnDisplayMarks = (Button) findViewById(R.id.btnDisplayMarks);

        child_ID = Util_SharedPreference.getChildIdFromSP(StudentMarksDetails.this);
        school_ID = Util_SharedPreference.getSchoolIdFromSP(StudentMarksDetails.this);
        Exam_ID = getIntent().getExtras().getString("exam_id");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.exam);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.marksss);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        getStudentMarks();

        btnDisplayMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent marklist = new Intent(StudentMarksDetails.this, MarksListScreen.class);
                marklist.putExtra("list", mark_list);
                startActivity(marklist);

            }
        });
    }

    private void getStudentMarks() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StudentMarksDetails.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StudentMarksDetails.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StudentMarksDetails.this);
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
                        ExamList data;
                        JSONArray js = new JSONArray(response.body().toString());

                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);

                            String StudentName = jsonObject.getString("StudentName");
                            lblStudentName.setText(": " + StudentName);
                            String Standard = jsonObject.getString("Standard");
                            lblClStandard.setText(": " + Standard);
                            String Section = jsonObject.getString("Section");
                            lblSection.setText(": " + Section);
                            String ExamName = jsonObject.getString("ExamName");
                            lblexamName.setText(": " + ExamName);
                            String MaxMark = jsonObject.getString("MaxMark");
                            lblMaximumMarks.setText(": " + MaxMark);
                            String HighestMark = jsonObject.getString("HighestMark");
                            lblHighest.setText(": " + HighestMark);
                            String Avarage = jsonObject.getString("Avarage");
                            lblAverage.setText(": " + Avarage);

                            String schoolname = jsonObject.getString("Avarage");
                            lblSchoolName.setText(": " + schoolname);

                            JSONArray marks_array = jsonObject.getJSONArray("details");

                            SubjectAndMarkList values;
                            for (int j = 0; j < marks_array.length(); j++) {
                                JSONObject object = marks_array.getJSONObject(i);
                                String subject = object.getString("Subject");
                                String mark = object.getString("Marks");
                                values = new SubjectAndMarkList(subject, mark);
                                mark_list.add(values);
                            }


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
