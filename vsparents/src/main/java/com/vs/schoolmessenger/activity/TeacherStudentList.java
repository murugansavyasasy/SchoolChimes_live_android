package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.activity.TeacherListAllSection.SELECTED_STD_SEC_POSITION;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherStudendListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckStudentListener;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherStudentList extends AppCompatActivity implements TeacherOnCheckStudentListener {
    RecyclerView rvStudentList;
    TextView tvOK, tvCancel, tvSelectedCount, tvTotalCount, tvStdSec, tvSubject;//tvSelectUnselect
    String schoolID, targetCode;
    TeacherSectionModel selSection;
    boolean bLoadStudentsFromAPI = false;
    CheckBox cbSelectAll;
    private TeacherStudendListAdapter adapter;
    private final ArrayList<TeacherStudentsModel> studentList = new ArrayList<>();
    private int i_students_count;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_student_list);
        schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStudentList.this);
        selSection = (TeacherSectionModel) getIntent().getSerializableExtra("STD_SEC");
        targetCode = selSection.getStdSecCode();
        i_students_count = Integer.parseInt(selSection.getSelectedStudentsCount());

        rvStudentList = (RecyclerView) findViewById(R.id.student_rvStudentList);
        adapter = new TeacherStudendListAdapter(TeacherStudentList.this, this, studentList, 0);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvStudentList.setHasFixedSize(true);
        rvStudentList.setLayoutManager(mLayoutManager);
        rvStudentList.setItemAnimator(new DefaultItemAnimator());
        rvStudentList.setAdapter(adapter);
        rvStudentList.getRecycledViewPool().setMaxRecycledViews(0, 80);

        tvSelectedCount = (TextView) findViewById(R.id.student_tvSelCount);
        tvTotalCount = (TextView) findViewById(R.id.student_tvTotCount);
        tvStdSec = (TextView) findViewById(R.id.student_tvStdSec);
        tvSubject = (TextView) findViewById(R.id.student_tvSubject);

        tvStdSec.setText(selSection.getStandard() + "-" + selSection.getSection());
        tvSubject.setText(selSection.getSubject());
        tvTotalCount.setText(selSection.getTotStudents());
        tvSelectedCount.setText(selSection.getSelectedStudentsCount());

        tvOK = (TextView) findViewById(R.id.student_tvOk);
        tvCancel = (TextView) findViewById(R.id.student_tvCancel);

        tvOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToResultActvity("OK");
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (selSection.getStudentsList() != null) {
            studentList.addAll(selSection.getStudentsList());
            adapter.notifyDataSetChanged();
        } else {
            bLoadStudentsFromAPI = true;
        }

        cbSelectAll = (CheckBox) findViewById(R.id.student_cbSelectAll);
        cbSelectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int i = 0; i < studentList.size(); i++) {
                    studentList.get(i).setSelectStatus(isChecked);
                }

                adapter.notifyDataSetChanged();

                if (isChecked) {
                    i_students_count = studentList.size();
                    Log.d("CHECK-i_students_count", "True - " + i_students_count);
                } else {
                    i_students_count = 0;
                    Log.d("CHECK-i_students_count", "False - " + i_students_count);
                }

                enableDisableNext();
            }
        });

        if (bLoadStudentsFromAPI)
            studentListAPI();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void studentListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetStudDetail(schoolID, targetCode);
        Call<JsonArray> call = apiService.GetStudDetail(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStudName = jsonObject.getString("StudentName");

                        String strStudID = jsonObject.getString("StudentID");
                        Log.d("StudentId", strStudID);
                        if (strStudID.equals("0")) {
                            showToast(strStudName);
                            cbSelectAll.setEnabled(false);
                        } else {
                            TeacherStudentsModel studentsModel;
                            Log.d("json length", String.valueOf(js.length()));
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                studentsModel = new TeacherStudentsModel(jsonObject.getString("StudentID"), jsonObject.getString("StudentName")
                                        , jsonObject.getString("StudentAdmissionNo"), "", true);
                                studentList.add(studentsModel);
                            }


                            tvTotalCount.setText(String.valueOf(studentList.size()));
                            cbSelectAll.setChecked(false);


                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                    }

                } catch (Exception e) {
                    Log.e("StudentsList:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    @Override
    public void student_addClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count++;
            enableDisableNext();
        }
    }

    @Override
    public void student_removeClass(TeacherStudentsModel student) {
        if (student != null) {
            i_students_count--;
            enableDisableNext();
        }
    }

    private void enableDisableNext() {
        tvSelectedCount.setText(String.valueOf(i_students_count));
        tvOK.setEnabled(i_students_count > 0);
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        returnIntent.putExtra("STUDENTS", studentList);//listSelectedStudents);
        setResult(SELECTED_STD_SEC_POSITION, returnIntent);
        finish();
    }
}
