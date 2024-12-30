package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.CurrentYearFeeAdapter;
import com.vs.schoolmessenger.adapter.PaymentTypeAdapter;
import com.vs.schoolmessenger.adapter.PreviousYearFeeAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CurrentYearFeeItem;
import com.vs.schoolmessenger.model.DailyFeeCollectionModelItem;
import com.vs.schoolmessenger.model.PaymentTypeWiseItem;
import com.vs.schoolmessenger.model.PreviousYearFeeItem;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class DailyFeeCollectionActivity extends AppCompatActivity {

    public PaymentTypeAdapter paymentAdapter;
    public PreviousYearFeeAdapter previousYearFeeAdapter;
    public CurrentYearFeeAdapter CurrentYearFeeAdapter;
    String school_ID;
    TextView lblTotalName, lblTotalAmount;
    LinearLayout lnrPaymentType, lnrPreviousYear, lnrCurrentYear, lnrTotal;
    RecyclerView recyclePaymentType, recyclePreviousYear, recycleCurrentYear;
    TextView lblFromDate, lblToDate, lblSubmit, lblNoRecords;
    DatePickerDialog datePickerDialog;
    private List<DailyFeeCollectionModelItem> mainDataItem = new ArrayList<DailyFeeCollectionModelItem>();
    private List<PaymentTypeWiseItem> paymentTypeList = new ArrayList<PaymentTypeWiseItem>();
    private List<PreviousYearFeeItem> previousYearFeeList = new ArrayList<PreviousYearFeeItem>();
    private List<CurrentYearFeeItem> currentYearFeeList = new ArrayList<CurrentYearFeeItem>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.daily_collection);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Daily_Collection_Fee));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if ((listschooldetails.size() == 1)) {
            school_ID = TeacherUtil_Common.Principal_SchoolId;
        } else {
            school_ID = getIntent().getExtras().getString("SCHOOL_ID", "");
        }

        lblTotalName = (TextView) findViewById(R.id.lblTotalName);
        lblTotalAmount = (TextView) findViewById(R.id.lblTotalAmount);
        lblFromDate = (TextView) findViewById(R.id.lblFromDate);
        lblToDate = (TextView) findViewById(R.id.lblToDate);
        lblSubmit = (TextView) findViewById(R.id.lblSubmit);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);
        lnrPaymentType = (LinearLayout) findViewById(R.id.lnrPaymentType);
        lnrPreviousYear = (LinearLayout) findViewById(R.id.lnrPreviousYear);
        lnrCurrentYear = (LinearLayout) findViewById(R.id.lnrCurrentYear);
        lnrTotal = (LinearLayout) findViewById(R.id.lnrTotal);
        recyclePaymentType = (RecyclerView) findViewById(R.id.recyclePaymentType);
        recyclePreviousYear = (RecyclerView) findViewById(R.id.recyclePreviousYear);
        recycleCurrentYear = (RecyclerView) findViewById(R.id.recycleCurrentYear);


        lblTotalName.setTypeface(null, Typeface.BOLD);
        lblTotalAmount.setTypeface(null, Typeface.BOLD);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        lblFromDate.setText(formattedDate);
        lblToDate.setText(formattedDate);

        lblFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetimePicker("from");
            }
        });

        lblToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datetimePicker("to");
            }
        });

        lblSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDailyFeeCollection();
            }
        });

        getDailyFeeCollection();

    }

    private void datetimePicker(String value) {

        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(DailyFeeCollectionActivity.this, R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = format.format(calendar.getTime());

                        if (value.equals("from")) {
                            lblFromDate.setText(dateString);
                        } else {
                            lblToDate.setText(dateString);
                        }
                    }
                }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    private void getDailyFeeCollection() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DailyFeeCollectionActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("schoolId", school_ID);
        jsonObject.addProperty("fromDate", lblFromDate.getText().toString());
        jsonObject.addProperty("toDate", lblToDate.getText().toString());
        Log.d("jsonObjectReq", jsonObject.toString());
        Call<List<DailyFeeCollectionModelItem>> call = apiService.getDailyCollection(jsonObject);

        call.enqueue(new Callback<List<DailyFeeCollectionModelItem>>() {
            @Override
            public void onResponse(Call<List<DailyFeeCollectionModelItem>> call, retrofit2.Response<List<DailyFeeCollectionModelItem>> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response);

                    mainDataItem.clear();
                    paymentTypeList.clear();
                    previousYearFeeList.clear();
                    currentYearFeeList.clear();

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Response", json);
                        mainDataItem = response.body();
                        String status = mainDataItem.get(0).getStatus();
                        String message = mainDataItem.get(0).getMessage();

                        if (status.equals("1")) {
                            lblNoRecords.setVisibility(View.GONE);
                            lnrTotal.setVisibility(View.VISIBLE);
                            lnrPaymentType.setVisibility(View.VISIBLE);
                            lnrCurrentYear.setVisibility(View.VISIBLE);
                            lnrPreviousYear.setVisibility(View.VISIBLE);

                            String totalCollectedName = mainDataItem.get(0).getData().getTotalCollected().getName();
                            String totalCollectedPaidAmount = mainDataItem.get(0).getData().getTotalCollected().getPaidAmount();
                            lblTotalName.setText(totalCollectedName);
                            lblTotalAmount.setText("\u20B9" + " " + totalCollectedPaidAmount);

                            paymentTypeList = mainDataItem.get(0).getData().getPaymentTypeWise();
                            previousYearFeeList = mainDataItem.get(0).getData().getPreviousYearFee();
                            currentYearFeeList = mainDataItem.get(0).getData().getCurrentYearFee();


                            if (paymentTypeList.size() > 0) {
                                lnrPaymentType.setVisibility(View.VISIBLE);
                            } else {
                                lnrPaymentType.setVisibility(View.GONE);
                            }

                            if (previousYearFeeList.size() > 0) {
                                lnrPreviousYear.setVisibility(View.VISIBLE);
                            } else {
                                lnrPreviousYear.setVisibility(View.GONE);

                            }

                            if (currentYearFeeList.size() > 0) {
                                lnrCurrentYear.setVisibility(View.VISIBLE);
                            } else {
                                lnrCurrentYear.setVisibility(View.GONE);
                            }

                            paymentAdapter = new PaymentTypeAdapter(paymentTypeList, DailyFeeCollectionActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclePaymentType.setLayoutManager(mLayoutManager);
                            recyclePaymentType.setItemAnimator(new DefaultItemAnimator());
                            recyclePaymentType.setAdapter(paymentAdapter);

                            previousYearFeeAdapter = new PreviousYearFeeAdapter(previousYearFeeList, DailyFeeCollectionActivity.this);
                            RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
                            recyclePreviousYear.setLayoutManager(mLayoutManager1);
                            recyclePreviousYear.setItemAnimator(new DefaultItemAnimator());
                            recyclePreviousYear.setAdapter(previousYearFeeAdapter);

                            CurrentYearFeeAdapter = new CurrentYearFeeAdapter(currentYearFeeList, DailyFeeCollectionActivity.this);
                            RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
                            recycleCurrentYear.setLayoutManager(mLayoutManager2);
                            recycleCurrentYear.setItemAnimator(new DefaultItemAnimator());
                            recycleCurrentYear.setAdapter(CurrentYearFeeAdapter);

                            paymentAdapter.notifyDataSetChanged();
                            previousYearFeeAdapter.notifyDataSetChanged();
                            CurrentYearFeeAdapter.notifyDataSetChanged();


                        } else {
                            lblNoRecords.setText(message);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblTotalAmount.setText("\u20B9" + " " + "0");
                            lnrPaymentType.setVisibility(View.GONE);
                            lnrCurrentYear.setVisibility(View.GONE);
                            lnrPreviousYear.setVisibility(View.GONE);

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<DailyFeeCollectionModelItem>> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecords(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DailyFeeCollectionActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

    }
}