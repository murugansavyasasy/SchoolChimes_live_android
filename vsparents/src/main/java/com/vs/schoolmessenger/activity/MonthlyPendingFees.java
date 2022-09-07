package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.MonthlyPendingAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MonthlyPending;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class MonthlyPendingFees extends AppCompatActivity {

    String child_id, school_id, amount, invoive_id;

    RecyclerView monthly_fees_recycle;
    private ArrayList<MonthlyPending> monthly_list = new ArrayList<>();
    public MonthlyPendingAdapter mAdapter;
    Button btnMakePayment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.monthly_recycle);


        btnMakePayment = (Button) findViewById(R.id.btnMakePayment);
        monthly_fees_recycle = (RecyclerView) findViewById(R.id.monthly_fees_recycle);
        child_id = Util_SharedPreference.getChildIdFromSP(MonthlyPendingFees.this);
        school_id = Util_SharedPreference.getSchoolIdFromSP(MonthlyPendingFees.this);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.monthly_pending);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.fee);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        monthlyFees();



        mAdapter = new MonthlyPendingAdapter(monthly_list, MonthlyPendingFees.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        monthly_fees_recycle.setLayoutManager(mLayoutManager);
        monthly_fees_recycle.setItemAnimator(new DefaultItemAnimator());
        monthly_fees_recycle.setAdapter(mAdapter);
        monthly_fees_recycle.getRecycledViewPool().setMaxRecycledViews(0, 80);


        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payment=new Intent(MonthlyPendingFees.this,PaymentWebViewActivity.class);
                startActivity(payment);
            }
        });
    }

    private void monthlyFees() {

        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(MonthlyPendingFees.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(MonthlyPendingFees.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("SchoolID", school_id);
        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetStudentPendingFee(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        Log.e("Showing", "showing");

                        JSONObject jresponse = new JSONObject(response.body().toString());
                        JSONArray monthly = jresponse.getJSONArray("StudentMonthlyFees");
                        if (monthly.length() > 0) {

                            mAdapter.clearAllData();
                            MonthlyPending data;
                            for (int j = 0; j < monthly.length(); j++) {
                                JSONObject jsonObject = monthly.getJSONObject(j);
                                String FeeName = jsonObject.getString("FeeName");
                                String Monthy = jsonObject.getString("Monthly");
                                String total = jsonObject.getString("TotalMonthly");
                                String StartMonthName = jsonObject.getString("StartMonthName");
                                String EndMonthName = jsonObject.getString("EndMonthName");

                                String PendingAmount = jsonObject.getString("PendingAmount");

                                data = new MonthlyPending(FeeName, Monthy, total, StartMonthName, EndMonthName,PendingAmount);
                                monthly_list.add(data);
                            }

                            mAdapter.notifyDataSetChanged();
                            Log.e("show_sizee", String.valueOf(monthly_list.size()));

                        } else {

                            showAlert();
                        }

                    } else {
                        Toast.makeText(MonthlyPendingFees.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(MonthlyPendingFees.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MonthlyPendingFees.this);


        alertDialog.setTitle(R.string.alert);


        alertDialog.setMessage(R.string.no_paid_records);
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
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }


}
