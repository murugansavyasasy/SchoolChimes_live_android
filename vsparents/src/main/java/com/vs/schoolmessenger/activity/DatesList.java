package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT;
import static com.vs.schoolmessenger.util.Util_Common.MENU_VOICE;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.DatesListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.DatesListListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.BannerAdManager;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.NativeAdManager;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.TemplateView;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;


public class DatesList extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = DatesList.class.getSimpleName();
    private final List<DatesModel> dateList = new ArrayList<>();
    private final List<DatesModel> OffLineDateList = new ArrayList<>();
    private final List<DatesModel> totaldateList = new ArrayList<>();
    RecyclerView rvDateList;
    DatesListAdapter datesListAdapter;
    String strTitle;
    String date, unreadcount, day;
    ArrayList<DatesModel> arrayList;
    TextView lblNoMessages;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    ImageView imgSearch;
    EditText Searchable;
    Slider slider;
    ImageView adImage;
    String Title = "";
    LinearLayout mAdView;
    private int iRequestCode;
    private PopupWindow pHelpWindow;
    private ArrayList<Profiles> childList = new ArrayList<>();
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
        setContentView(R.layout.activity_dates_list);
        c = Calendar.getInstance();

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        strTitle = getIntent().getExtras().getString("HEADER", "");

        if (iRequestCode == MENU_TEXT) {
            Title = "Text";
        } else if (iRequestCode == MENU_VOICE) {
            Title = "Voice";
        }


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(Title);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        mAdView = findViewById(R.id.adView);
        native_ad_container = findViewById(R.id.native_ad_container);




        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        Slider.init(new PicassoImageLoadingService(DatesList.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);


        adsClose = findViewById(R.id.lblClose);
        adsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                native_ad_container.setVisibility(View.GONE);
                adsClose.setVisibility(View.GONE);
            }
        });


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


        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setEnabled(true);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (iRequestCode == MENU_TEXT) {
                    LaodMoreDatewisetListSmsAPI();
                } else if (iRequestCode == MENU_VOICE) {
                    LaodMoreDatewisetListVoiceAPI();
                }
            }
        });

        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
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

    private void filterlist(String s) {
        List<DatesModel> temp = new ArrayList();
        for (DatesModel d : dateList) {

            if (d.getDate().toLowerCase().contains(s.toLowerCase()) || d.getDay().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        datesListAdapter.updateList(temp);
    }

    private void seeMoreButtonVisiblity() {
        if (isNewVersion.equals("1")) {
            LoadMore.setVisibility(View.VISIBLE);
        } else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
    }

    private void LaodMoreDatewisetListVoiceAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
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
        Call<JsonArray> call = apiService.LoadMoreGetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
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

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", String.valueOf(js.length()));
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
                                absentee = new DatesModel(date, day, unreadcount, is_Archive);
                                dateList.add(absentee);
                                OffLineDateList.add(absentee);
                            } else {
                                showRecords(strMessage);
                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);

                            datesListAdapter.notifyDataSetChanged();

                        }
                    } else {
                        showRecords(getResources().getString(R.string.no_records));
                    }

                    if(arrayList == null){

                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
                    }
                   else if(arrayList.size() < 4) {
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
                   }
                    else {
                        native_ad_container.setVisibility(View.GONE);
                        adsClose.setVisibility(View.GONE);
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
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
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

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", String.valueOf(js.length()));

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
                                absentee = new DatesModel(date, day, unreadcount, is_Archive);
                                dateList.add(absentee);
                                OffLineDateList.add(absentee);
                            } else {
                                showRecords(strMessage);
                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(dateList);

                            datesListAdapter.notifyDataSetChanged();
                        }
                    } else {
                        showRecords(getResources().getString(R.string.no_records));
                    }

                    if(arrayList == null){
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
                    }
                   else if(arrayList.size() < 4) {
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);

                    }
                    else {
                        native_ad_container.setVisibility(View.GONE);
                        adsClose.setVisibility(View.GONE);
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

//        BannerAdManager.getInstance(this).startAutoRefresh();

        if (iRequestCode == MENU_TEXT) {
            if (isNetworkConnected()) {
                DatewisetListSmsAPI();
            }
        } else if (iRequestCode == MENU_VOICE) {

            if (isNetworkConnected()) {
                DatewisetListVoiceAPI();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        BannerAdManager.getInstance(this).stopAutoRefresh();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) DatesList.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void showToast(String msg) {
        Toast.makeText(DatesList.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void DatewisetListSmsAPI() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
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

                Log.d("StudentsList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StudentsList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        Log.d("json length", String.valueOf(js.length()));
                        datesListAdapter.clearAllData();
                        totaldateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");

                            if (isNewVersion.equals("1")) {
                                LoadMore.setVisibility(View.VISIBLE);
                            } else {
                                LoadMore.setVisibility(View.GONE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }

                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount, false);
                                dateList.add(absentee);
                                totaldateList.add(absentee);
                            } else {
                                if (isNewVersion.equals("1")) {
                                    lblNoMessages.setVisibility(View.VISIBLE);
                                    lblNoMessages.setText(strMessage);

                                    String loadMoreCall = TeacherUtil_SharedPreference.getOnBackMethodText(DatesList.this);
                                    Log.d("loadMoreCall", loadMoreCall);
                                    if (loadMoreCall.equals("1")) {
                                        TeacherUtil_SharedPreference.putOnBackPressedText(DatesList.this, "");
                                        LaodMoreDatewisetListSmsAPI();
                                    }
                                } else {
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

                    if(arrayList == null){
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
                    }
                    else if(arrayList.size() < 4) {
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
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
        alertDialog.setTitle(getResources().getString(R.string.alert));

        //Setting Dialog Message
        alertDialog.setMessage(strMessage);


        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
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

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(DatesList.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


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
        Call<JsonArray> call = apiService.GetDateWiseUnreadCount(jsonReqArray);//GetAbsenteesCountByDate(jsonReqArray);
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
                        datesListAdapter.clearAllData();
                        totaldateList.clear();
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("Status");
                            String strMessage = jsonObject.getString("Message");

                            if (isNewVersion.equals("1")) {
                                LoadMore.setVisibility(View.VISIBLE);
                            } else {
                                LoadMore.setVisibility(View.GONE);
                                lblNoMessages.setVisibility(View.VISIBLE);
                            }

                            if (strStatus.equals("1")) {
                                date = jsonObject.getString("Date");
                                unreadcount = jsonObject.getString("UnreadCount");
                                day = jsonObject.getString("Day");
                                DatesModel absentee;
                                absentee = new DatesModel(date, day, unreadcount, false);
                                dateList.add(absentee);
                                totaldateList.add(absentee);
                            } else {
                                if (isNewVersion.equals("1")) {
                                    lblNoMessages.setVisibility(View.VISIBLE);
                                    lblNoMessages.setText(strMessage);

                                    String loadMoreCall = TeacherUtil_SharedPreference.getOnBackMethodVoice(DatesList.this);
                                    Log.d("loadMoreCall", loadMoreCall);
                                    if (loadMoreCall.equals("1")) {
                                        TeacherUtil_SharedPreference.putOnBackPressedVoice(DatesList.this, "");
                                        LaodMoreDatewisetListVoiceAPI();
                                    }

                                } else {
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

                    if(arrayList == null){
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);

                    }
                    else if(arrayList.size() < 4) {
                        ShowAds.getAds(DatesList.this, adImage, slider, "", mAdView,native_ad_container,adsClose);
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

                Intent homescreen = new Intent(DatesList.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq = new Intent(DatesList.this, FAQScreen.class);
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

                Util_Common.popUpMenu(DatesList.this, v, "1");
                break;

        }
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
        builder.setTitle(getResources().getString(R.string.choose_language));
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(getResources().getString(R.string.pop_password_btnCancel), new DialogInterface.OnClickListener() {
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

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(DatesList.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(DatesList.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(DatesList.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(DatesList.this);


        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();


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

                Log.d("VersionCheck:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), DatesList.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), DatesList.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), DatesList.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), DatesList.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), DatesList.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), DatesList.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), DatesList.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), DatesList.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), DatesList.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), DatesList.this);

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

}
