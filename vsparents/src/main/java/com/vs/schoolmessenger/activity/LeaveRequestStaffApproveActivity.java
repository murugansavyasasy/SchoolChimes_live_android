package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.LeaveRequestDetails;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class LeaveRequestStaffApproveActivity extends AppCompatActivity {

    LeaveRequestDetails history;
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
//        setContentView(R.layout.popup_leave_history);
        setContentView(R.layout.levae_approval_item);

        history = (LeaveRequestDetails) getIntent().getSerializableExtra("history");


        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvStandard = (TextView) findViewById(R.id.tvStandard);
        TextView isLeaveApplyOn = (TextView) findViewById(R.id.isLeaveApplyOn);
        TextView lblFromDate = (TextView) findViewById(R.id.lblFromDate);
        TextView lblToDate = (TextView) findViewById(R.id.lblToDate);
        TextView lblReason = (TextView) findViewById(R.id.lblReason);
        TextView btnPopupApprove = (Button) findViewById(R.id.btnPopupApprove);
        TextView btnPopupDecline = (Button) findViewById(R.id.btnPopupDecline);
        TextView lblDays = (TextView) findViewById(R.id.lblDays);
        TextView lblApprovalBy = (TextView) findViewById(R.id.lblApprovalBy);
        LinearLayout lytApprovalBy = (LinearLayout) findViewById(R.id.lytApprovalBy);
        ImageView btn_ok = (ImageView) findViewById(R.id.btn_ok);
        EditText Reason = (EditText) findViewById(R.id.txtReason);

        if (history.getLoginType()) {
            if (history.getApproved().equals("0")) {
                btnPopupApprove.setVisibility(View.VISIBLE);
                btnPopupDecline.setVisibility(View.VISIBLE);
                lytApprovalBy.setVisibility(View.GONE);
                Reason.setVisibility(View.GONE);
            }else {
                lytApprovalBy.setVisibility(View.GONE);
            }
        } else {
            btnPopupApprove.setVisibility(View.GONE);
            btnPopupDecline.setVisibility(View.GONE);
            Reason.setVisibility(View.GONE);
            lytApprovalBy.setVisibility(View.VISIBLE);
            lblApprovalBy.setText("Sathish");
        }

        tvName.setText(history.getName());
        tvStandard.setText(history.getSection() + " - " + history.getCLS());
        isLeaveApplyOn.setText(history.getLeaveAppliedOn());
        lblFromDate.setText(convertDateFormat(history.getLeaveFromDate()));
        lblToDate.setText(convertDateFormat(history.getLeaveToDate()));
        lblReason.setText(history.getReason());

        long totalDays = calculateDays(history.getLeaveFromDate(), history.getLeaveToDate());
        lblDays.setText(String.valueOf(totalDays));

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

    public String convertDateFormat(String inputDate) {
        SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        SimpleDateFormat toFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

        try {
            Date date = fromFormat.parse(inputDate);
            if (date != null) {
                return toFormat.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return ""; // fallback if parsing fails
    }


    public long calculateDays(String fromDate, String toDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);

        try {
            Date start = sdf.parse(fromDate);
            Date end = sdf.parse(toDate);

            if (start != null && end != null) {
                long diffInMillis = end.getTime() - start.getTime();
                long days = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS) + 1;
                return days;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }
    private void leaveApproveDeclineApi(String approve, String id, String updatedOn, String reason) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(LeaveRequestStaffApproveActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        String staffid = TeacherUtil_Common.Principal_staffId;
        final ProgressDialog mProgressDialog = new ProgressDialog(LeaveRequestStaffApproveActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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
                        Toast.makeText(LeaveRequestStaffApproveActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(LeaveRequestStaffApproveActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
