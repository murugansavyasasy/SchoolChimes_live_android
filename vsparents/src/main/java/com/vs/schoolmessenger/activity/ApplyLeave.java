package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.SliderAdsImage.ShowAdvancedNativeAds;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.TemplateView;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class ApplyLeave extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    TextView tv_fromdate, tv_todate;
    String strfromdate, strtodate, strreason, strdatevalue;
    EditText et_reason;
    Button btn_apply;
    String strDate, strCurrentDate, timeString, strTime;//strDuration
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    boolean bMinDateTime = true;
    PopupWindow pwindow;
    Button btnLeaveHistory;
    Slider slider;
    ImageView adImage;
    LinearLayout mAdView;

    ImageView adsClose;
    FrameLayout native_ad_container;



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
        setContentView(R.layout.activity_apply_leave);

        tv_fromdate = (TextView) findViewById(R.id.appLeave_tvFromDate);
        tv_todate = (TextView) findViewById(R.id.appLeave_tvToDate);
        et_reason = (EditText) findViewById(R.id.applyleave_etReason);
        btn_apply = (Button) findViewById(R.id.appLeave_btnSignIn);
        btnLeaveHistory = (Button) findViewById(R.id.btnLeaveHistory);

        Slider.init(new PicassoImageLoadingService(ApplyLeave.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);

        native_ad_container = findViewById(R.id.native_ad_container);
        adsClose = findViewById(R.id.lblClose);
        adsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                native_ad_container.setVisibility(View.GONE);
                adsClose.setVisibility(View.GONE);
            }
        });



        btnLeaveHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent leavehistory = new Intent(ApplyLeave.this, LeaveRequestsByStaffs.class);
                leavehistory.putExtra("type", "child");
                startActivity(leavehistory);
            }
        });


        tv_fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strdatevalue = "1";
                datePicker();
            }
        });

        tv_todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strdatevalue = "2";
                datePicker();
            }
        });
        btn_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strreason = et_reason.getText().toString();
                if (strreason.isEmpty()) {
                    showToast(getResources().getString(R.string.enter_leave_reason));
                } else {
                    ApplyleaveAPI();
                }
            }
        });


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.leave));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(getResources().getString(R.string.requesttttt));

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setMinDateTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAdvancedNativeAds.getAds(ApplyLeave.this, adImage, slider, "", native_ad_container, adsClose);

        ShowAds.getAds(this, adImage, slider, "Dashboard", mAdView);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setMinDateTime() {
        tv_fromdate.setText(dateFormater(System.currentTimeMillis(), "dd MMM yyyy"));
        strfromdate = dateFormater(System.currentTimeMillis(), "dd-MM-yyyy");
        tv_todate.setText(dateFormater(System.currentTimeMillis(), "dd MMM yyyy"));
        strtodate = dateFormater(System.currentTimeMillis(), "dd-MM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        minimumHour = cal.get(Calendar.HOUR_OF_DAY);
        minimumMinute = cal.get(Calendar.MINUTE);

        selHour = Integer.toString(minimumHour);
        selMin = Integer.toString(minimumMinute);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(ApplyLeave.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(minDate, null)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.teacher_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        String dateString = formatter.format(new Date(dateInMillis));
        return dateString;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;
        if (strdatevalue.equals("1")) {
            tv_fromdate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
            strfromdate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd-MM-yyyy");
        } else if (strdatevalue.equals("2")) {
            tv_todate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
            strtodate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd-MM-yyyy");
        }
    }

    private void ApplyleaveAPI() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ApplyLeave.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseVOICE();
        Call<JsonArray> call = apiService.InsertLeaveInformation(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
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
                        Log.d("json length", String.valueOf(js.length()));
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            showRecords(strMessage);
                        } else {
                            showRecords(strMessage);
                        }
                    } else {
                        showRecords(getResources().getString(R.string.no_records));
                    }
                } catch (Exception e) {
                    Log.e("GroupList:Excep", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));

                Log.d("SubjectHandling:Failure", t.toString());
            }
        });
    }

    private void showRecords(String strMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ApplyLeave.this);

        //Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.alert));

        //Setting Dialog Message
        alertDialog.setMessage(strMessage);

        //On Pressing Setting button
        // On pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_reason.getWindowToken(), 0);

                dialog.cancel();
                finish();

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showToast(String msg) {
        Toast.makeText(ApplyLeave.this, msg, Toast.LENGTH_SHORT).show();
    }

    private JsonObject constructJsonArrayDatewiseVOICE() {

        String strChildID = Util_SharedPreference.getChildIdFromSP(ApplyLeave.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ApplyLeave.this);
        strreason = et_reason.getText().toString();
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("ChildID", strChildID);
            jsonObjectSchool.addProperty("SchoolID", strSchoolID);
            jsonObjectSchool.addProperty("LeaveFromDate", strfromdate);
            jsonObjectSchool.addProperty("LeaveToDate", strtodate);
            jsonObjectSchool.addProperty("Reason", strreason);
            Log.d("schoolid", strSchoolID);
            Log.d("AbsenteeReport:req", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

}
