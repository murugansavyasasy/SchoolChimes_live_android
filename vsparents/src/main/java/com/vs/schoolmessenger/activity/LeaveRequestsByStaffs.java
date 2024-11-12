package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.vs.schoolmessenger.adapter.LeaveRequestAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.LeaveRequestDetails;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class LeaveRequestsByStaffs extends AppCompatActivity {


    public LeaveRequestAdapter mAdapter;
    String Staff_ID;
    TeacherSchoolsModel schoolmodel;
    String loginType = "";
    String Type = "";
    boolean login;
    ImageView imgSearch;
    TextView Searchable;
    private final List<LeaveRequestDetails> leaveHistoryList = new ArrayList<>();
    private RecyclerView Leave_History_recycle;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.leave_history_recycle);
        Leave_History_recycle = (RecyclerView) findViewById(R.id.Leave_History_recycle);

        loginType = getIntent().getExtras().getString("type", "");
        if (loginType.equals("child")) {
            Staff_ID = Util_SharedPreference.getChildIdFromSP(LeaveRequestsByStaffs.this);
            Type = "student";
        } else {
            schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
            Staff_ID = schoolmodel.getStrStaffID();
            Type = "staff";
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        if (Type.equals("student")) {
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.leave_history);
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        } else {
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.leave_request);
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.teacher_colorAccent)));
        }

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    Leave_History_recycle.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        Leave_History_recycle.setVisibility(View.VISIBLE);
                    }

                } else {
                    Leave_History_recycle.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.VISIBLE);
                }
                filterlist(editable.toString());
            }
        });

        mAdapter = new LeaveRequestAdapter(leaveHistoryList, LeaveRequestsByStaffs.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        Leave_History_recycle.setLayoutManager(mLayoutManager);
        Leave_History_recycle.setItemAnimator(new DefaultItemAnimator());
        Leave_History_recycle.setAdapter(mAdapter);
        Leave_History_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }

    private void filterlist(String s) {
        List<LeaveRequestDetails> temp = new ArrayList();
        for (LeaveRequestDetails d : leaveHistoryList) {

            if (d.getName().toLowerCase().contains(s.toLowerCase()) || d.getSection().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    @Override
    protected void onResume() {
        super.onResume();

        leaveRequestDetails();
    }

    private void leaveRequestDetails() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(LeaveRequestsByStaffs.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(LeaveRequestsByStaffs.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(LeaveRequestsByStaffs.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ReqId", Staff_ID);
        jsonObject.addProperty("type", Type);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.GetLeaveRequests(jsonObject);

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
                            mAdapter.clearAllData();
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String ID = jsonObject.getString("LeaveId");

                                if (!ID.equals("0")) {
                                    String name = jsonObject.getString("StudentName");
                                    String cls = jsonObject.getString("Class");
                                    String section = jsonObject.getString("Section");
                                    String appliedOn = jsonObject.getString("LeaveAppliedOn");
                                    String fromDate = jsonObject.getString("LeaveFromDate");
                                    String ToDate = jsonObject.getString("LeaveTODate");
                                    String reason = jsonObject.getString("Reason");
                                    String approved = jsonObject.getString("Status");
                                    String updatedOn = jsonObject.getString("UpdatedOn");

                                    login = loginType.equals("staff");
                                    LeaveRequestDetails data = new LeaveRequestDetails(ID, name, cls, section, appliedOn, fromDate, ToDate, reason, approved, login, updatedOn);
                                    leaveHistoryList.add(data);
                                } else {
                                    showRecordsFound(getResources().getString(R.string.no_records));
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                        } else {

                            showRecordsFound(getResources().getString(R.string.no_records));
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

    private void showRecordsFound(String no_records_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LeaveRequestsByStaffs.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_records_found);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }
}
