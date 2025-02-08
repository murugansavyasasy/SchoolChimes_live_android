package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.AttendanceDateListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.DatesListListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class Attendance extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DatesList.class.getSimpleName();
    RecyclerView rvDateList;
    AttendanceDateListAdapter datesListAdapter;
    String strTitle;
    String date, absentcount, day;
    TextView attendance_tv1;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    ArrayList<Integer> isAdminMenuID = new ArrayList<>();
    ArrayList<Integer> isStaffMenuID = new ArrayList<>();
    ArrayList<Integer> isPrincipalMenuID = new ArrayList<>();
    ArrayList<Integer> isGroupHedMenuID = new ArrayList<>();
    ArrayList<Integer> isParentMenuID = new ArrayList<>();
    ArrayList<String> isAdminMenuNames = new ArrayList<>();
    ArrayList<String> isIsStaffMenuNames = new ArrayList<>();
    ArrayList<String> isPrincipalMenuNames = new ArrayList<>();
    ArrayList<String> isGroupHedMenuNames = new ArrayList<>();
    ArrayList<String> isParentMenuNames = new ArrayList<>();
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;
    ImageView imgSearch;
    EditText Searchable;
    Slider slider;
    ImageView adImage;
    AdView mAdView;
    private final List<DatesModel> dateList = new ArrayList<>();
    private final List<DatesModel> OfflineDateList = new ArrayList<>();
    private final List<DatesModel> totalDateList = new ArrayList<>();
    private int iRequestCode;
    private PopupWindow pHelpWindow;
    private ArrayList<Profiles> childList = new ArrayList<>();

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
        setContentView(R.layout.activity_attendance);
        c = Calendar.getInstance();


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        strTitle = getIntent().getExtras().getString("HEADER", "");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Attendance_Report));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");


        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        mAdView = findViewById(R.id.adView);


        Slider.init(new PicassoImageLoadingService(Attendance.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);


        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (datesListAdapter == null)
                    return;

                if (datesListAdapter.getItemCount() < 1) {
                    rvDateList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvDateList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvDateList.setVisibility(View.VISIBLE);
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


        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        LoadMore.setEnabled(true);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreAttendenceReportAPI();

            }
        });
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(Attendance.this);
        if (isNewVersion.equals("1")) {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        } else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }


        rvDateList = (RecyclerView) findViewById(R.id.attendance_rvDateList);
        datesListAdapter = new AttendanceDateListAdapter(Attendance.this, dateList, new DatesListListener() {
            @Override
            public void onItemClick(DatesModel item) {
                Log.d(TAG, "Selected Date: " + item.getDate());

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvDateList.setHasFixedSize(true);
        rvDateList.setLayoutManager(mLayoutManager);
        rvDateList.setItemAnimator(new DefaultItemAnimator());
        rvDateList.setAdapter(datesListAdapter);
        AttendenceReportAPI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this, adImage, slider, "", mAdView);
    }

    @Override
    protected void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mAdView != null) {
            mAdView.pause();  // Pause the ad
        }
        super.onPause();
    }

    private void filterlist(String s) {
        List<DatesModel> temp = new ArrayList();
        for (DatesModel d : dateList) {

            if (d.getDate().toLowerCase().contains(s.toLowerCase()) || d.getDay().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        datesListAdapter.updateList(temp);
    }

    private void LoadMoreAttendenceReportAPI() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(Attendance.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(Attendance.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(Attendance.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // MessengerApiClient.changeApiBaseUrl(Util_SharedPreference.getBaseUrlFromSP(Attendance.this));
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseSMS();
        Call<JsonArray> call = apiService.LoadMoreGetAbsentDatesForChild(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);

//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = df.format(c.getTime());
//                Log.d("currentDate",currentDate);
//                TeacherUtil_SharedPreference.putAttedanceCuurentDate(Attendance.this,currentDate);


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", String.valueOf(js.length()));
                        OfflineDateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Date");
                            String strMessage = jsonObject.getString("Day");

                            if (!strStatus.equals("0")) {
                                date = jsonObject.getString("Date");
//                                absentcount = jsonObject.getString("TotalAbsentees");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, absentcount, false);
                                dateList.add(absentee);
                                OfflineDateList.add(absentee);
                            } else {
                                // showToast(strMessage);
                                showAlertRecords(strMessage);
                            }

//                            myDb = new SqliteDB(Attendance.this);
//                            if (myDb.checkAttedance()) {
//                                myDb.deletAttedance();
//                            }
//                            myDb.addAttedanceMessages((ArrayList<DatesModel>) OfflineDateList, Attendance.this);


                            datesListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // showToast("No records found. Try again");
                        showAlertRecords(getResources().getString(R.string.no_records));
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

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(Attendance.this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);

    }

    private void AbsenteesReportListAPI2() {
        try {
            JSONArray js = new JSONArray("[{\"Date\":\"22-12-2017\",\"Day\":\"Friday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"23\",\"TotalAbsentees\":\"3\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"23\",\"TotalAbsentees\":\"3\"}]}]},{\"Date\":\"21-12-2017\",\"Day\":\"Thursday\",\"TotalAbsentees\":\"6\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"23\",\"TotalAbsentees\":\"6\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"23\",\"TotalAbsentees\":\"6\"}]}]},{\"Date\":\"18-01-2018\",\"Day\":\"Thursday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"2\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"2\"}]},{\"ClassName\":\"IV\",\"ClassId\":\"80\",\"TotalAbsentees\":\"1\",\"SectionWise\":[{\"SectionName\":\"A\",\"SectionId\":\"334\",\"TotalAbsentees\":\"1\"}]}]},{\"Date\":\"17-01-2018\",\"Day\":\"Wednesday\",\"TotalAbsentees\":\"1\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"1\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"1\"}]}]},{\"Date\":\"08-01-2018\",\"Day\":\"Monday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"3\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"2\"},{\"SectionName\":\"B\",\"SectionId\":\"331\",\"TotalAbsentees\":\"1\"}]}]}]");
            if (js.length() > 0) {
                Log.d("json length", String.valueOf(js.length()));
                for (int i = 0; i < js.length(); i++) {
                    JSONObject jsonObject = js.getJSONObject(i);
                    date = jsonObject.getString("Date");
                    absentcount = jsonObject.getString("TotalAbsentees");
                    day = jsonObject.getString("Day");
                    DatesModel absentee;
                    absentee = new DatesModel(date, day, absentcount, false);
                    dateList.add(absentee);

                }
                datesListAdapter.notifyDataSetChanged();

            } else {
                showToast(getResources().getString(R.string.Server_Response_Failed));
            }

        } catch (Exception e) {
            Log.e("GroupList:Excep", e.getMessage());
        }
    }

    private void AttendenceReportAPI() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(Attendance.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(Attendance.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(Attendance.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // MessengerApiClient.changeApiBaseUrl(Util_SharedPreference.getBaseUrlFromSP(Attendance.this));
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseSMS();
        Call<JsonArray> call = apiService.GetAbsentDatesForChild(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
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
                        totalDateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Date");
                            String strMessage = jsonObject.getString("Day");

                            if (!strStatus.equals("0")) {
                                date = jsonObject.getString("Date");
//                                absentcount = jsonObject.getString("TotalAbsentees");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, absentcount, false);
                                dateList.add(absentee);
                                totalDateList.add(absentee);

                            } else {
                                showAlertRecords(strMessage);
                            }
                            datesListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        // showToast("No records found. Try again");
                        showAlertRecords(getResources().getString(R.string.no_records));
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

    private void showAlertRecords(String strMessage) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Attendance.this);

        //Setting Dialog Title
        alertDialog.setTitle(getResources().getString(R.string.alert));

        //Setting Dialog Message
        alertDialog.setMessage(strMessage);

        //On Pressing Setting button
        // On pressing cancel button
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
//                locationtrack();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

        //  alertDialog.show();
    }


    private JsonObject constructJsonArrayDatewiseSMS() {

        // Send Request code (menu type) as an input
        String strChildID = Util_SharedPreference.getChildIdFromSP(Attendance.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(Attendance.this);
        JsonObject jsonObjectSchool = new JsonObject();
        try {

            jsonObjectSchool.addProperty("ChildID", strChildID);
            jsonObjectSchool.addProperty("SchoolID", strSchoolID);
            Log.d("schoolid", strSchoolID);
            Log.d("AbsenteeReport:req", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:

                Intent homescreen = new Intent(Attendance.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:
                Intent faq = new Intent(Attendance.this, FAQScreen.class);
                startActivity(faq);

//                setupHelpPopUp();
//                pHelpWindow.showAtLocation(rvDateList, Gravity.NO_GRAVITY, 0, 0);

                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(Attendance.this, "change");
                startActivity(new Intent(Attendance.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(Attendance.this, v, "1");

                break;


        }
    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(Attendance.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(Attendance.this);
        AlertDialog alertDialog;
        builder.setTitle(getResources().getString(R.string.choose_language));
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();

                final Languages model = LanguageList.get(selectedPosition);
                String ID = model.getStrLanguageID();
                String code = model.getScriptCode();

                Log.d("code", code);
                Log.d("ID", ID);

                changeLanguage(code, ID);
                dialog.cancel();


            }
        });
        builder.setNegativeButton(getResources().getString(R.string.pop_password_btnCancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void changeLanguage(String code, String id) {
        TeacherUtil_SharedPreference.putLanguageType(Attendance.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(Attendance.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(Attendance.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(Attendance.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(Attendance.this);


        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();


//        if (schools_list != null) {
//            for (int i = 0; i < schools_list.size(); i++) {
//                final TeacherSchoolsModel model = schools_list.get(i);
//                IDs = IDs + model.getStrStaffID() + "~";
//
//            }
//        }
//        if (childList != null) {
//            for (int i = 0; i < childList.size(); i++) {
//                final Profiles model = childList.get(i);
//                IDs = IDs + model.getChildID() + "~";
//            }
//        }
//
//        IDs = IDs.substring(0, IDs.length() - 1);
        //Log.d("IDS", IDs);

        if (schools_list != null) {
            for (int i = 0; i < schools_list.size(); i++) {
                final TeacherSchoolsModel model = schools_list.get(i);

                jsonObject.addProperty("type", "staff");
                jsonObject.addProperty("id", model.getStrStaffID());
                jsonObject.addProperty("schoolid", model.getStrSchoolID());
                jsonArray.add(jsonObject);

            }
        }
        if (childList != null) {
            for (int i = 0; i < childList.size(); i++) {
                final Profiles model = childList.get(i);
                jsonObject.addProperty("type", "parent");
                jsonObject.addProperty("id", model.getChildID());
                jsonObject.addProperty("schoolid", model.getSchoolID());
                jsonArray.add(jsonObject);
            }
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonObjectlanguage = new JsonObject();
        jsonObjectlanguage.add("MemberData", jsonArray);
        jsonObjectlanguage.addProperty("LanguageId", id);
        jsonObjectlanguage.addProperty("CountryID", countryId);

        Log.d("Request", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ChangeLanguage(jsonObjectlanguage);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("VersionCheck:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), Attendance.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), Attendance.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), Attendance.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), Attendance.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), Attendance.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), Attendance.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), Attendance.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), Attendance.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), Attendance.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), Attendance.this);


                        if (Integer.parseInt(status) > 0) {
                            showToast(message);

                            Locale myLocale = new Locale(lang);
                            //saveLocale(lang);
                            Locale.setDefault(myLocale);
                            Configuration config = new Configuration();
                            config.locale = myLocale;
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                            recreate();

                        } else {
                            showToast(message);
                        }

                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));

                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }

    private void putParentNamestoSharedPref(String isParent) {
        String[] name4 = isParent.split(",");
        Collections.addAll(isParentMenuNames, name4);
        TeacherUtil_SharedPreference.putParentMenuNames(isParentMenuNames, Attendance.this);

    }

    private void putGroupHeadtoSharedPref(String idGroupHead) {
        String[] name3 = idGroupHead.split(",");
        Collections.addAll(isGroupHedMenuNames, name3);

        TeacherUtil_SharedPreference.putGroupHeadNames(isGroupHedMenuNames, Attendance.this);

    }

    private void putAdminNamestoSharedPref(String isAdmin) {
        String[] name2 = isAdmin.split(",");
        Collections.addAll(isAdminMenuNames, name2);

        TeacherUtil_SharedPreference.putAdminNames(isAdminMenuNames, Attendance.this);

    }

    private void putStaffNamestoSharedPref(String isStaff) {
        String[] name1 = isStaff.split(",");
        Collections.addAll(isIsStaffMenuNames, name1);

        TeacherUtil_SharedPreference.putStaffNames(isIsStaffMenuNames, Attendance.this);

    }

    private void putPrincipalNametoSharedPref(String isPrincipal) {
        String[] name = isPrincipal.split(",");
        Collections.addAll(isPrincipalMenuNames, name);
        TeacherUtil_SharedPreference.putPrincipalNames(isPrincipalMenuNames, Attendance.this);

    }

    private void putParentIdstoSharedPref(String isParentID) {
        String[] items4 = isParentID.split(",");
        for (String itemtemp : items4) {
            int result = Integer.parseInt(itemtemp);
            isParentMenuID.add(result);

        }

        TeacherUtil_SharedPreference.putparentIDs(isParentMenuID, Attendance.this);

    }

    private void putGroupHeadIdstosharedPref(String idGroupHeadID) {
        String[] items3 = idGroupHeadID.split(",");
        for (String itemtemp : items3) {
            int result = Integer.parseInt(itemtemp);
            isGroupHedMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putGroupHeadIDs(isGroupHedMenuID, Attendance.this);
    }

    private void putAdminIdstoSharedPref(String isAdminID) {
        String[] items2 = isAdminID.split(",");
        for (String itemtemp : items2) {
            int result = Integer.parseInt(itemtemp);
            isAdminMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putAdminIDs(isAdminMenuID, Attendance.this);
    }

    private void putStaffIdstoSharedPref(String isStaffID) {
        String[] items1 = isStaffID.split(",");
        for (String itemtemp : items1) {
            int result = Integer.parseInt(itemtemp);
            isStaffMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putStaffIDs(isStaffMenuID, Attendance.this);
    }

    private void putPrincipalIdstoSharedPref(String isPrincipalID) {
        String[] items = isPrincipalID.split(",");
        for (String itemtemp : items) {
            int result = Integer.parseInt(itemtemp);
            isPrincipalMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putPrincipalIDs(isPrincipalMenuID, Attendance.this);
    }


    private void showLogoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Attendance.this);
        alertDialog.setTitle(getResources().getString(R.string.txt_menu_logout));
        alertDialog.setMessage(getResources().getString(R.string.want_to_logut));
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(Attendance.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(Attendance.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(Attendance.this, "");

                //  Util_SharedPreference.clearParentSharedPreference(ChildrenScreen.this);
                TeacherUtil_SharedPreference.clearStaffSharedPreference(Attendance.this);
                startActivity(new Intent(Attendance.this, TeacherSignInScreen.class));
                finish();


            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.btn_sign_cancel), new DialogInterface.OnClickListener() {
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
        Button negativebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }

    private void setupHelpPopUp() {

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_help_txt, null);

        pHelpWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pHelpWindow.setContentView(layout);

        ImageView ivClose = (ImageView) layout.findViewById(R.id.popupHelp_ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pHelpWindow.dismiss();
                hideKeyBoard();
            }
        });

        final EditText etmsg = (EditText) layout.findViewById(R.id.popupHelp_etMsg);

//        final TextView tvTitle = (TextView) layout.findViewById(R.id.popupHelp_tvTitle);
//        tvTitle.setText("");

        final TextView tvTxtCount = (TextView) layout.findViewById(R.id.popupHelp_tvTxtCount);
        etmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTxtCount.setText(String.valueOf(460 - (s.length())));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView tvSend = (TextView) layout.findViewById(R.id.popupHelp_tvSend);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMsg = etmsg.getText().toString().trim();

                if (strMsg.length() > 0)
                    helpAPI(strMsg);
                else
                    showToast(getResources().getString(R.string.enter_message));
            }
        });

    }

    private void helpAPI(String strMsg) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        // MessengerApiClient.changeApiBaseUrl(Util_SharedPreference.getBaseUrlFromSP(ChildrenScreen.this));
        // String mobNumber = Util_SharedPreference.getMobileNumberFromSP(ChildrenScreen.this);
        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(Attendance.this);


        Log.d("Help:Mob-Query", mobNumber + " - " + strMsg);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetHelp(mobNumber, strMsg);
        Call<JsonArray> call = apiService.GetHelpnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Help:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Help:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if ((strStatus).equalsIgnoreCase("1")) {
                            showToast(strMessage);
                            if (pHelpWindow.isShowing()) {
                                pHelpWindow.dismiss();
                                hideKeyBoard();
                            }
                        } else {
                            showToast(strMessage);
                        }
                    } else {
                        showToast(getResources().getString(R.string.else_error_message));
                    }

                } catch (Exception e) {
                    showToast(getResources().getString(R.string.catch_message));
                    Log.e("Help:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Help:Failure", t.toString());
            }
        });
    }

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


    }
}
