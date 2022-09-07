package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSelectedsectionListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
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

import static com.vs.schoolmessenger.activity.TeacherListAllSection.SELECTED_STD_SEC_STUD_CODE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.list_staffStdSecs;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.strNoClassWarning;


public class TeacherStaffTextCircular extends AppCompatActivity implements View.OnClickListener {

    Button ChooseRecipents, btnSend;
    TextView tvMsg;

    RecyclerView selectedsection;
    TeacherSelectedsectionListAdapter adapter;
    LinearLayout llRecipients;

    ArrayList<TeacherSectionModel> stdSecList = new ArrayList<>();
    ArrayList<TeacherSectionModel> selectedStdSecStudList = new ArrayList<>();

    String strMsg = "";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_choose_recipents);


        selectedsection = (RecyclerView) findViewById(R.id.rvselectedsectionList1);

        ImageView ivBack = (ImageView) findViewById(R.id.text1_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        strMsg = getIntent().getExtras().getString("MESSAGE", "");
        tvMsg = (TextView) findViewById(R.id.staffText_tvMsg);
        tvMsg.setText(strMsg);

        btnSend = (Button) findViewById(R.id.staffText_btnSend);
        btnSend.setOnClickListener(this);

        ChooseRecipents = (Button) findViewById(R.id.staffText_btnChoose);
        ChooseRecipents.setOnClickListener(this);

        adapter = new TeacherSelectedsectionListAdapter(TeacherStaffTextCircular.this, selectedStdSecStudList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        selectedsection.setHasFixedSize(true);
        selectedsection.setLayoutManager(mLayoutManager);
        selectedsection.setItemAnimator(new DefaultItemAnimator());
        selectedsection.setAdapter(adapter);

        llRecipients = (LinearLayout) findViewById(R.id.staffText_llRecipient);
        llRecipients.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        stdSecListAPI();
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(1, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_STD_SEC_STUD_CODE) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {

                llRecipients.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
                adapter.clearAllData();
                selectedStdSecStudList.addAll((ArrayList<TeacherSectionModel>) data.getSerializableExtra("STUDENTS"));


                adapter.notifyDataSetChanged();

            } else {
                llRecipients.setVisibility(View.GONE);
                btnSend.setEnabled(false);
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staffText_btnChoose:
                Intent inStdSec = new Intent(TeacherStaffTextCircular.this, TeacherListAllSection.class);
                inStdSec.putExtra("STD_SEC_LIST", stdSecList);
                startActivityForResult(inStdSec, SELECTED_STD_SEC_STUD_CODE);
                break;

            case R.id.staffText_btnSend:
                sendStaffTextAPI();
                break;
        }
    }

    private void stdSecListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffTextCircular.this);
        final String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffTextCircular.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_SubjectHandling(schoolID, staffID);
        Call<JsonArray> call = apiService.staffSubjectHandling(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("SubjectHandling:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("SubjectHandling:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strSubName = jsonObject.getString("SubjectName");
                        String strSubCode = jsonObject.getString("SubjectCode");

                        if (strSubName.equals("")) {

                            list_staffStdSecs = new ArrayList<>();
                            strNoClassWarning = strSubCode;
                        } else {
                            TeacherSectionModel sectionModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                sectionModel = new TeacherSectionModel(false, jsonObject.getString("Class"), jsonObject.getString("Section")
                                        , jsonObject.getString("ClassSecCode"), jsonObject.getString("SubjectName")
                                        , jsonObject.getString("SubjectCode"), jsonObject.getString("NoOfStudents"), jsonObject.getString("NoOfStudents"), true);
                                stdSecList.add(sectionModel);
                            }

                            list_staffStdSecs = new ArrayList<>();
                            list_staffStdSecs.addAll(stdSecList);
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    try {
                        JSONArray jsError = new JSONArray(response.body().toString());
                        if (jsError.length() > 0) {
                            JSONObject jsonObjectError = jsError.getJSONObject(0);

                            String status = jsonObjectError.getString("Status");
                            String msg = jsonObjectError.getString("Message");
                            showToast(msg);
                        }
                    } catch (Exception ex) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.e("SubjectHandling:Excep", ex.getMessage());
                    }
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("SubjectHandling:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private void sendStaffTextAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = constructResultJsonArray();
        Call<JsonArray> call = apiService.SendSmsStaffwise(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextStaff:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextStaff:Res", response.body().toString());

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
                    try {
                        JSONArray jsError = new JSONArray(response.body().toString());
                        if (jsError.length() > 0) {
                            JSONObject jsonObjectError = jsError.getJSONObject(0);

                            String msg = jsonObjectError.getString("Message");
                            showToast(msg);
                        }
                    } catch (Exception ex) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.e("TextStaff:Excep", ex.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextStaff:Failure", t.toString());
            }
        });
    }

    private JsonArray constructResultJsonArray() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
            String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffTextCircular.this);
            String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffTextCircular.this);


            JsonObject jsonObjectSchool = new JsonObject();
            jsonObjectSchool.addProperty("SchoolID", schoolID);
            jsonObjectSchool.addProperty("StaffID", staffID);
            jsonObjectSchool.addProperty("Message", strMsg);

            JsonArray jsonArrayStdSections = new JsonArray();
            for (int i = 0; i < selectedStdSecStudList.size(); i++) {

                {
                    JsonObject jsonObjectStdSections = new JsonObject();
                    jsonObjectStdSections.addProperty("SecCode", selectedStdSecStudList.get(i).getStdSecCode());
                    jsonObjectStdSections.addProperty("SubCode", selectedStdSecStudList.get(i).getSubjectCode());
                    String strMsgToAll = "T";
                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected())
                        strMsgToAll = "F";
                    jsonObjectStdSections.addProperty("MsgToAll", strMsgToAll);

                    JsonArray jsonArrayStudents = new JsonArray();

                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected()) {
                        ArrayList<TeacherStudentsModel> seleStudents = new ArrayList<>();
                        seleStudents.addAll(selectedStdSecStudList.get(i).getStudentsList());

                        for (int j = 0; j < seleStudents.size(); j++) {
                            if (seleStudents.get(j).isSelectStatus()) {
                                JsonObject jsonObjectStudents = new JsonObject();
                                jsonObjectStudents.addProperty("StudentID", seleStudents.get(j).getStudentID());
                                jsonArrayStudents.add(jsonObjectStudents);
                            }
                        }

                    }

                    jsonObjectStdSections.add("Student", jsonArrayStudents);
                    jsonArrayStdSections.add(jsonObjectStdSections);
                }
            }
            jsonObjectSchool.add("TargetCode", jsonArrayStdSections);
            jsonArraySchool.add(jsonObjectSchool);

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }
}
