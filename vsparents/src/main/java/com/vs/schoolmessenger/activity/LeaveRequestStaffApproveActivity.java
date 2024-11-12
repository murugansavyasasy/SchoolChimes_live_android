package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.LeaveRequestDetails;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;

public class LeaveRequestStaffApproveActivity extends AppCompatActivity {

    LeaveRequestDetails history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.popup_leave_history);

        history = (LeaveRequestDetails) getIntent().getSerializableExtra("history");


        TextView lblPopupName = (TextView) findViewById(R.id.lblPopupName);
        TextView lblPopupStandard = (TextView) findViewById(R.id.lblPopupStandard);
        TextView lblPopupSection = (TextView) findViewById(R.id.lblPopupSection);
        TextView lblPopupAppliedOn = (TextView) findViewById(R.id.lblPopupAppliedOn);
        TextView lblPopupFromDate = (TextView) findViewById(R.id.lblPopupFromDate);
        TextView lblPopupToDate = (TextView) findViewById(R.id.lblPopupToDate);
        TextView lblPopupReason = (TextView) findViewById(R.id.lblPopupReason);
        ImageView btn_ok = (ImageView) findViewById(R.id.btn_ok);
        TextView btnPopupApprove = (Button) findViewById(R.id.btnPopupApprove);
        TextView btnPopupDecline = (Button) findViewById(R.id.btnPopupDecline);
        final EditText Reason = (EditText) findViewById(R.id.txtReason);

        if (history.getLoginType()) {
            if (history.getApproved().equals("0")) {
                btnPopupApprove.setVisibility(View.VISIBLE);
                btnPopupDecline.setVisibility(View.VISIBLE);
                Reason.setVisibility(View.GONE);
            }
        } else {
            btnPopupApprove.setVisibility(View.GONE);
            btnPopupDecline.setVisibility(View.GONE);
            Reason.setVisibility(View.GONE);
        }


        lblPopupName.setText(history.getName());
        lblPopupStandard.setText(" " + history.getCLS());
        lblPopupSection.setText(history.getSection());
        lblPopupAppliedOn.setText(" : " + history.getLeaveAppliedOn());
        lblPopupFromDate.setText(" : " + history.getLeaveFromDate());
        lblPopupToDate.setText(" : " + history.getLeaveToDate());

        lblPopupReason.setText(history.getReason());


        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnPopupApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = Reason.getText().toString();

                leaveApproveDeclineApi("1", history.getId(), history.getUpdatedOn(), reason);

            }
        });
        btnPopupDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = Reason.getText().toString();
                leaveApproveDeclineApi("2", history.getId(), history.getUpdatedOn(), reason);

            }
        });

    }

    private void leaveApproveDeclineApi(String approve, String id, String updatedOn, String reason) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(LeaveRequestStaffApproveActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String staffid = TeacherUtil_Common.Principal_staffId;
        final ProgressDialog mProgressDialog = new ProgressDialog(LeaveRequestStaffApproveActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("leaveid", id);
        jsonObject.addProperty("status", approve);
        jsonObject.addProperty("updatedby", staffid);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.Updateleavestatus(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject js = new JSONObject(response.body().toString());

                        String Status = js.getString("status");
                        String mesaage = js.getString("message");

                        if (Status.equals("0")) {
                            Toast.makeText(LeaveRequestStaffApproveActivity.this, mesaage, Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (Status.equals("1")) {
                            Toast.makeText(LeaveRequestStaffApproveActivity.this, mesaage, Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (Status.equals("2")) {
                            Toast.makeText(LeaveRequestStaffApproveActivity.this, mesaage, Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    } else {
                        Toast.makeText(LeaveRequestStaffApproveActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(LeaveRequestStaffApproveActivity.this, R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
