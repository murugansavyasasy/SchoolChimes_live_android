package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.DatesListAdapter;
import com.vs.schoolmessenger.interfaces.DatesListListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT;
import static com.vs.schoolmessenger.util.Util_Common.MENU_VOICE;


public class DatesList extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DatesList.class.getSimpleName();
    RecyclerView rvDateList;
    DatesListAdapter datesListAdapter;
    private List<DatesModel> dateList = new ArrayList<>();
    private List<DatesModel> OffLineDateList = new ArrayList<>();
    private List<DatesModel> totaldateList = new ArrayList<>();
    String strTitle;
    String date, unreadcount, day;

    private int iRequestCode;

    SqliteDB myDb;
    ArrayList<DatesModel> arrayList;
    TextView lblNoMessages;

    private PopupWindow pHelpWindow;
    RelativeLayout rytHome,rytLanguage, rytPassword,rytHelp,rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;
    String previousDateVoice;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dates_list);
         c = Calendar.getInstance();

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        strTitle = getIntent().getExtras().getString("HEADER", "");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(strTitle);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");



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


         LoadMore=(TextView) findViewById(R.id.btnSeeMore);
         lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
         LoadMore.setEnabled(true);

        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(iRequestCode==MENU_TEXT){
                    LaodMoreDatewisetListSmsAPI();


//                    previousDate=TeacherUtil_SharedPreference.getCurrent_Date_DateList(DatesList.this);
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    String currentDate = df.format(c.getTime());
//                    if (previousDate.equals("") || previousDate.compareTo(currentDate)<0)
//                    {
//                        LaodMoreDatewisetListSmsAPI();
//                    }
//                    else {
//                        myDb = new SqliteDB(DatesList.this);
//                        if (myDb.checkTextMeassages()) {
//                            dateList.clear();
//                            totaldateList.addAll(myDb.getTextMessages());
//                            dateList.addAll(totaldateList);
//                            datesListAdapter.notifyDataSetChanged();
//                            LoadMore.setVisibility(View.GONE);
//
//                        }
//                        else {
//                            showRecords("No Records found...");
//                        }
//                    }

                }
                else if(iRequestCode==MENU_VOICE){
                   LaodMoreDatewisetListVoiceAPI();

//                    previousDateVoice=TeacherUtil_SharedPreference.getDateListVoiceDate(DatesList.this);
//                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                    String currentDate = df.format(c.getTime());
//                    if (previousDateVoice.equals("") || previousDateVoice.compareTo(currentDate)<0)
//                    {
//                        LaodMoreDatewisetListVoiceAPI();
//                    }
//                    else {
//                        myDb = new SqliteDB(DatesList.this);
//                        if (myDb.checkVoiceMeassageCount()) {
//                            dateList.clear();
//                            totaldateList.addAll(myDb.getVoiceMessages());
//                            dateList.addAll(totaldateList);
//                            datesListAdapter.notifyDataSetChanged();
//                            LoadMore.setVisibility(View.GONE);
//
//                        }
//                        else {
//                            showRecords("No Records found...");
//                        }
//                    }

                }


            }
        });

         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(DatesList.this);

         seeMoreButtonVisiblity();


        rvDateList = (RecyclerView) findViewById(R.id.datesList_rvDateList);
        datesListAdapter = new DatesListAdapter(DatesList.this, dateList, new DatesListListener() {
            @Override
            public void onItemClick(DatesModel item) {
                Log.d(TAG, "Selected Date: " + item.getDate());

                if (iRequestCode == MENU_TEXT) {
                    Intent in = new Intent(DatesList.this, TextCircular.class);
                    in.putExtra("REQUEST_CODE", iRequestCode);
                    in.putExtra("SEL_DATE", item.getDate());
                    in.putExtra("is_Archive", item.getIs_Archive());
                    startActivity(in);
                } else if (iRequestCode == MENU_VOICE) {
                    Intent in = new Intent(DatesList.this, VoiceCircular.class);
                    in.putExtra("REQUEST_CODE", iRequestCode);
                    in.putExtra("SEL_DATE", item.getDate());
                    in.putExtra("is_Archive", item.getIs_Archive());
                    startActivity(in);
                }
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvDateList.setHasFixedSize(true);
        rvDateList.setLayoutManager(mLayoutManager);
        rvDateList.setItemAnimator(new DefaultItemAnimator());
        rvDateList.setAdapter(datesListAdapter);

    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
    }

    private void LaodMoreDatewisetListVoiceAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseVOICE();
        Call<JsonArray> call = apiService.LoadMoreGetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);


//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = df.format(c.getTime());
//                Log.d("currentDate",currentDate);
//                TeacherUtil_SharedPreference.putDateListVoiceCurrentDate(DatesList.this,currentDate);


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", js.length() + "");
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");
                            OffLineDateList.clear();
                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                boolean is_Archive = jsonObject.getBoolean("is_Archive");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount,is_Archive);
                                dateList.add(absentee);
                                OffLineDateList.add(absentee);
                            } else {
                                showRecords(strMessage);
                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);
//                            myDb = new SqliteDB(DatesList.this);
//
//                            if (myDb.checkVoiceMeassageCount()) {
//                                myDb.deleteVoiceMessageRecords();
//                            }
//                            myDb.addVoiceMeassges((ArrayList<DatesModel>) OffLineDateList, DatesList.this);

                            datesListAdapter.notifyDataSetChanged();

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

    private void LaodMoreDatewisetListSmsAPI() {


        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseSMS();
        Call<JsonArray> call = apiService.LoadMoreGetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//                String currentDate = df.format(c.getTime());
//                Log.d("currentDate",currentDate);
//                TeacherUtil_SharedPreference.putDateListCurrentDate(DatesList.this,currentDate);
                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);



                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", js.length() + "");

                        OffLineDateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");

                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                boolean is_Archive = jsonObject.getBoolean("is_Archive");

                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount,is_Archive);
                                dateList.add(absentee);
                                OffLineDateList.add(absentee);
                            }
                            else {
                                showRecords(strMessage);
                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);
//                            myDb = new SqliteDB(DatesList.this);
//                            if (myDb.checkTextMeassages()) {
//                                myDb.deleteTextMeassages();
//                            }
//                            myDb.addTextMessages((ArrayList<DatesModel>) OffLineDateList, DatesList.this);

                            datesListAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (iRequestCode == MENU_TEXT) {
            if (isNetworkConnected()) {
                DatewisetListSmsAPI();
            }

        }
        else if (iRequestCode == MENU_VOICE) {

            if (isNetworkConnected()) {
                DatewisetListVoiceAPI();
            }

        }

    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatesList.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        //Setting Dialog Message
        alertDialog.setMessage(R.string.connect_internet);
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
      //  alertDialog.show();
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) DatesList.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void showToast(String msg) {
        Toast.makeText(DatesList.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void AbsenteesReportListAPI2() {
        try {
            JSONArray js = new JSONArray("[{\"Date\":\"22-12-2017\",\"Day\":\"Friday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"23\",\"TotalAbsentees\":\"3\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"23\",\"TotalAbsentees\":\"3\"}]}]},{\"Date\":\"21-12-2017\",\"Day\":\"Thursday\",\"TotalAbsentees\":\"6\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"23\",\"TotalAbsentees\":\"6\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"23\",\"TotalAbsentees\":\"6\"}]}]},{\"Date\":\"18-01-2018\",\"Day\":\"Thursday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"2\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"2\"}]},{\"ClassName\":\"IV\",\"ClassId\":\"80\",\"TotalAbsentees\":\"1\",\"SectionWise\":[{\"SectionName\":\"A\",\"SectionId\":\"334\",\"TotalAbsentees\":\"1\"}]}]},{\"Date\":\"17-01-2018\",\"Day\":\"Wednesday\",\"TotalAbsentees\":\"1\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"1\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"1\"}]}]},{\"Date\":\"08-01-2018\",\"Day\":\"Monday\",\"TotalAbsentees\":\"3\",\"ClassWise\":[{\"ClassName\":\"II\",\"ClassId\":\"79\",\"TotalAbsentees\":\"3\",\"SectionWise\":[{\"SectionName\":\"NEW\",\"SectionId\":\"330\",\"TotalAbsentees\":\"2\"},{\"SectionName\":\"B\",\"SectionId\":\"331\",\"TotalAbsentees\":\"1\"}]}]}]");
            if (js.length() > 0) {
                Log.d("json length", js.length() + "");
                for (int i = 0; i < js.length(); i++) {
                    JSONObject jsonObject = js.getJSONObject(i);
                    date = jsonObject.getString("Date");
                    unreadcount = jsonObject.getString("TotalAbsentees");
                    day = jsonObject.getString("Day");
                    DatesModel absentee;
                    absentee = new DatesModel(date, day, unreadcount,false);
                    dateList.add(absentee);

                }
                datesListAdapter.notifyDataSetChanged();

            } else {
                showToast("Server Response Failed. Try again");
            }

        } catch (Exception e) {
            Log.e("GroupList:Excep", e.getMessage());
        }
    }


    private void DatewisetListSmsAPI() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseSMS();
        Call<JsonArray> call = apiService.GetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", js.length() + "");
                        datesListAdapter.clearAllData();
                        totaldateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");

                            if(isNewVersion.equals("1")){
                                LoadMore.setVisibility(View.VISIBLE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }
                            else {
                                LoadMore.setVisibility(View.GONE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }

                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount,false);
                                dateList.add(absentee);
                                totaldateList.add(absentee);
                            }

                            else {
                                if(isNewVersion.equals("1")){
                                    lblNoMessages.setVisibility(View.VISIBLE);
                                    lblNoMessages.setText(strMessage);

                                    String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodText(DatesList.this);
                                    Log.d("loadMoreCall",loadMoreCall);
                                    if(loadMoreCall.equals("1")){
                                        TeacherUtil_SharedPreference.putOnBackPressedText(DatesList.this,"");
                                        LaodMoreDatewisetListSmsAPI();
                                    }
                                }
                                else {
                                    lblNoMessages.setVisibility(View.GONE);
                                    showRecords(strMessage);
                                }

                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);
                            datesListAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DatesList.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        //Setting Dialog Message
        alertDialog.setMessage(strMessage);


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


    private JsonObject constructJsonArrayDatewiseSMS() {

        String strChildID = Util_SharedPreference.getChildIdFromSP(DatesList.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(DatesList.this);
        JsonObject jsonObjectSchool = new JsonObject();
        try {

            jsonObjectSchool.addProperty("ChildID", strChildID);
            jsonObjectSchool.addProperty("SchoolID", strSchoolID);
            jsonObjectSchool.addProperty("Type", "SMS");
            Log.d("schoolid", strSchoolID);
            Log.d("AbsenteeReport:req", jsonObjectSchool.toString());
        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    private void DatewisetListVoiceAPI() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayDatewiseVOICE();
        Call<JsonArray> call = apiService.GetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("StudentsList:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", js.length() + "");
                        datesListAdapter.clearAllData();
                        totaldateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");

                            if(isNewVersion.equals("1")){
                                LoadMore.setVisibility(View.VISIBLE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }
                            else {
                                LoadMore.setVisibility(View.GONE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }

                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount,false);
                                dateList.add(absentee);
                                totaldateList.add(absentee);
                            } else {
                                if(isNewVersion.equals("1")){
                                    lblNoMessages.setVisibility(View.VISIBLE);
                                    lblNoMessages.setText(strMessage);

                                    String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodVoice(DatesList.this);
                                    Log.d("loadMoreCall",loadMoreCall);
                                    if(loadMoreCall.equals("1")){
                                        TeacherUtil_SharedPreference.putOnBackPressedVoice(DatesList.this,"");
                                        LaodMoreDatewisetListVoiceAPI();
                                    }

                                }
                                else {
                                    lblNoMessages.setVisibility(View.GONE);
                                    showRecords(strMessage);
                                }


                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);
                            datesListAdapter.notifyDataSetChanged();

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


    private JsonObject constructJsonArrayDatewiseVOICE() {

        String strChildID = Util_SharedPreference.getChildIdFromSP(DatesList.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(DatesList.this);
        JsonObject jsonObjectSchool = new JsonObject();
        try {

            jsonObjectSchool.addProperty("ChildID", strChildID);
            jsonObjectSchool.addProperty("SchoolID", strSchoolID);
            jsonObjectSchool.addProperty("Type", "VOICE");
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

                Intent homescreen=new Intent(DatesList.this,HomeActivity.class);
                homescreen.putExtra("HomeScreen","1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq=new Intent(DatesList.this,FAQScreen.class);
                startActivity(faq);



                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(DatesList.this, "change");
                startActivity(new Intent(DatesList.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(DatesList.this,v,"1");


                break;


        }
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



        final TextView tvTxtCount = (TextView) layout.findViewById(R.id.popupHelp_tvTxtCount);
        etmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTxtCount.setText("" + (460 - (s.length())));
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
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(DatesList.this);


        Log.d("Help:Mob-Query", mobNumber + " - " + strMsg);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetHelp(mobNumber, strMsg);
        Call<JsonArray> call = apiService.GetHelpnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Help:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Help:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if ((strStatus.toLowerCase()).equals("1")) {
                            showToast(strMessage);
                            if (pHelpWindow.isShowing()) {
                                pHelpWindow.dismiss();
                                hideKeyBoard();
                            }
                        } else {
                            showToast(strMessage);
                        }
                    } else {
                        showToast(String.valueOf(getResources().getText(R.string.else_error_message)));
                    }

                } catch (Exception e) {
                    showToast(String.valueOf(getResources().getText(R.string.catch_message)));
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

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(DatesList.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(DatesList.this);
        android.app.AlertDialog alertDialog;
        builder.setTitle(R.string.choose_language);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                int selectedPosition = ((android.app.AlertDialog) dialog).getListView().getCheckedItemPosition();

                final Languages model = LanguageList.get(selectedPosition);
                String ID = model.getStrLanguageID();
                String code = model.getScriptCode();

                Log.d("code", code);
                Log.d("ID", ID);

                changeLanguage(code, ID);




                dialog.cancel();


            }
        });
        builder.setNegativeButton(R.string.pop_password_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void changeLanguage(String code, String id) {
        TeacherUtil_SharedPreference.putLanguageType(DatesList.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(DatesList.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(DatesList.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(DatesList.this);


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

                jsonObject.addProperty("type","staff");
                jsonObject.addProperty("id",model.getStrStaffID());
                jsonObject.addProperty("schoolid",model.getStrSchoolID());
                jsonArray.add(jsonObject);

            }
        }
        if (childList != null) {
            for (int i = 0; i < childList.size(); i++) {
                final Profiles model = childList.get(i);
                jsonObject.addProperty("type","parent");
                jsonObject.addProperty("id",model.getChildID());
                jsonObject.addProperty("schoolid",model.getSchoolID());
                jsonArray.add(jsonObject);
            }
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
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

                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");



                        LanguageIDAndNames. putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"),DatesList.this);
                        LanguageIDAndNames.  putStaffIdstoSharedPref(jsonObject.getString("isStaffID"),DatesList.this);
                        LanguageIDAndNames. putAdminIdstoSharedPref(jsonObject.getString("isAdminID"),DatesList.this);
                        LanguageIDAndNames. putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"),DatesList.this);
                        LanguageIDAndNames. putParentIdstoSharedPref(jsonObject.getString("isParentID"),DatesList.this);
                        LanguageIDAndNames. putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"),DatesList.this);
                        LanguageIDAndNames. putStaffNamestoSharedPref(jsonObject.getString("isStaff"),DatesList.this);
                        LanguageIDAndNames. putAdminNamestoSharedPref(jsonObject.getString("isAdmin"),DatesList.this);
                        LanguageIDAndNames. putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"),DatesList.this);
                        LanguageIDAndNames. putParentNamestoSharedPref(jsonObject.getString("isParent"),DatesList.this);



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

    private void showLogoutAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(DatesList.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(DatesList.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(DatesList.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(DatesList.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(DatesList.this);
                startActivity(new Intent(DatesList.this, TeacherSignInScreen.class));
                finish();


            }
        });
        alertDialog.setPositiveButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button negativebutton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }
}