package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.TimeTableClassAdapter;
import com.vs.schoolmessenger.adapter.TimeTableDayAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TimeTableDayListener;
import com.vs.schoolmessenger.model.DayClass;
import com.vs.schoolmessenger.model.TimeTableClass;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class TimeTableActivity extends AppCompatActivity {

    public ArrayList<TimeTableClass> classList = new ArrayList<>();
    public ArrayList<DayClass> DayList = new ArrayList<>();
    RecyclerView recycleviewDay, recyclerClass;
    TimeTableClassAdapter timeTableclassAdapter;
    TimeTableDayAdapter dayAdapter;

    String DayID;

    Slider slider;
    ImageView adImage;

    AdView mAdView;

    Boolean isApiCall = true;
    int isApiCallPosition = 10;

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
        setContentView(R.layout.activity_parent_timetable);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.Time_table);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        DayMethod();

        recycleviewDay = (RecyclerView) findViewById(R.id.recycleViewDay);
        recyclerClass = (RecyclerView) findViewById(R.id.rcyclass);

        Slider.init(new PicassoImageLoadingService(TimeTableActivity.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        dayAdapter = new TimeTableDayAdapter(this, DayList, new TimeTableDayListener() {

            @Override
            public void onDayClick(Integer position, DayClass item) {
                Log.d("DayIDtestActivitty", item.getId());
                Log.d("DayPosition", String.valueOf(position));
                DayID = item.getId();

                if (isApiCallPosition != position) {
                    getTimeTableApi(position);
                    timeTableclassAdapter = new TimeTableClassAdapter(TimeTableActivity.this, classList);
                    recyclerClass.setAdapter(timeTableclassAdapter);
                    recyclerClass.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    recyclerClass.setNestedScrollingEnabled(false);
                    recyclerClass.setHasFixedSize(true);
                    recyclerClass.setItemAnimator(new DefaultItemAnimator());
                    timeTableclassAdapter.notifyDataSetChanged();
                }

            }
        });
        recycleviewDay.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
        recycleviewDay.setHasFixedSize(true);
        recycleviewDay.setItemAnimator(new DefaultItemAnimator());
        recycleviewDay.setAdapter(dayAdapter);

    }

    private void DayMethod() {

        DayClass day = new DayClass("Mon", "1");
        DayList.add(day);

        day = new DayClass("Tue", "2");
        DayList.add(day);

        day = new DayClass("Wed", "3");
        DayList.add(day);

        day = new DayClass("Thurs", "4");
        DayList.add(day);

        day = new DayClass("Fri", "5");
        DayList.add(day);

        day = new DayClass("Sat", "6");
        DayList.add(day);
        day = new DayClass("Sun", "7");
        DayList.add(day);


    }

    @Override
    public void onResume() {
        super.onResume();
        ShowAds.getAds(this, adImage, slider, "", mAdView);
//        getTimeTableApi();

    }


    private void getTimeTableApi(Integer position) {
        isApiCallPosition = position;
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(this);
        if (isNewVersionn.equals("1")) {

            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SchoolId", strSchoolID);
        jsonObject.addProperty("ChildId", strChildID);
        jsonObject.addProperty("DayId", DayID);

        Log.d("TimeTableRequest", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetTimetable(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Timetable:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Timetable:Res", response.body().toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    int status = jsonObject.getInt("Status");
                    String message = jsonObject.getString("Message");

                    if (status == 1) {
                        classList.clear();
                        JSONArray data = jsonObject.getJSONArray("data");
                        if (data.length() != 0) {
                            TimeTableClass timetabledata;
                            for (int i = 0; i < data.length(); i++) {
                                jsonObject = data.getJSONObject(i);

                                timetabledata = new TimeTableClass(jsonObject.getString("name"),
                                        jsonObject.getString("fromTime"),
                                        jsonObject.getString("toTime"),
                                        jsonObject.getString("duration"),
                                        jsonObject.getString("hourType"),
                                        jsonObject.getString("subjectName"),
                                        jsonObject.getString("staffName")
                                );
                                classList.add(timetabledata);
                            }
                            timeTableclassAdapter.notifyDataSetChanged();
                        } else {
                            timeTableclassAdapter.notifyDataSetChanged();
                            alert(message);

                        }
                    } else {
                        alert(message);
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Knowledge:Failure", t.toString());
            }
        });
    }

    private void alert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TimeTableActivity.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}