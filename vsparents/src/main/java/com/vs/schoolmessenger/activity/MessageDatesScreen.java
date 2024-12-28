package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Constants.isOnBackPressed;

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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAdvancedNativeAds;
import com.vs.schoolmessenger.adapter.HomeWorkDateWiseAdapter;
import com.vs.schoolmessenger.adapter.HomeWorkGridAdapter;
import com.vs.schoolmessenger.interfaces.OnItemHomeworkClick;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CircularDates;
import com.vs.schoolmessenger.model.HomeWorkData;
import com.vs.schoolmessenger.model.HomeWorkModel;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
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

public class MessageDatesScreen extends AppCompatActivity implements View.OnClickListener {

    Profiles childItem = new Profiles();
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    ImageView imgSearch;
    EditText Searchable;
    Slider slider;
    ImageView adImage;
    AdView mAdView;
    GridView rvGridHW;
    HomeWorkGridAdapter mAdapter;
    RelativeLayout rytParent;
    TemplateView native_ads;
    ImageView adsClose;
    private final ArrayList<CircularDates> datesList = new ArrayList<>();
    private final ArrayList<CircularDates> totaldatesList = new ArrayList<>();
    private final ArrayList<CircularDates> OfflinedatesList = new ArrayList<>();
    private PopupWindow pHelpWindow;
    private ArrayList<Profiles> childList = new ArrayList<>();
    private ArrayList<HomeWorkData> HomeWorkData = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_dates_screen);
        c = Calendar.getInstance();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(R.string.home_homework_text);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Slider.init(new PicassoImageLoadingService(MessageDatesScreen.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);

        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                native_ads.setVisibility(View.GONE);
                adsClose.setVisibility(View.GONE);
                LoadMoreGetHomeWorkDetails();

            }
        });

        MobileAds.initialize(this);
        native_ads = findViewById(R.id.my_template);
        adsClose = findViewById(R.id.lblClose);
        adsClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                native_ads.setVisibility(View.GONE);
                adsClose.setVisibility(View.GONE);
            }
        });


        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);
        seeMoreButtonVisiblity();

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getCount() < 1) {
                    rvGridHW.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvGridHW.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvGridHW.setVisibility(View.VISIBLE);
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
        rvGridHW = (GridView) findViewById(R.id.GridHW);
    }


    private void LoadMoreGetHomeWorkDetails() {
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();
        String strChildID = Util_SharedPreference.getChildIdFromSP(MessageDatesScreen.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(MessageDatesScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetMessageCount(strChildID, strSchoolID);
        Call<List<HomeWorkModel>> call = apiService.getHomeWorkDetails_Archive(jsonReqArray);
        call.enqueue(new Callback<List<HomeWorkModel>>() {

            @Override
            public void onResponse(Call<List<HomeWorkModel>> call, Response<List<HomeWorkModel>> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("MsgDates:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("MsgDates:Res", response.body().toString());
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                Log.d("Archive_Response", json);
                LoadMore.setVisibility(View.GONE);
                isOnBackPressed = false;
                lblNoMessages.setVisibility(View.GONE);

                try {
                    String status = response.body().get(0).getStatus();
                    String message = response.body().get(0).getMessage();
                    if (status.equals("1")) {

                        ArrayList<HomeWorkData> data = new ArrayList<>();
                        data = (ArrayList<com.vs.schoolmessenger.model.HomeWorkData>) response.body().get(0).getData();
                        HomeWorkData.addAll(data);
                        mAdapter = new HomeWorkGridAdapter(MessageDatesScreen.this, HomeWorkData, rytParent, new OnItemHomeworkClick() {
                            @Override
                            public void onMsgItemClick(HomeWorkData item) {

                            }
                        });
                        rvGridHW.setAdapter((ListAdapter) mAdapter);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        showrecordsFound(message);
                    }

                } catch (Exception e) {
                    Log.e("MsgDates:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<HomeWorkModel>> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("MsgDates:Failure", t.toString());
            }
        });
    }

    private void getHomeWorkDetails() {
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(MessageDatesScreen.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(MessageDatesScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetMessageCount(strChildID, strSchoolID);
        Call<List<HomeWorkModel>> call = apiService.getHomeWorkDetails(jsonReqArray);
        call.enqueue(new Callback<List<HomeWorkModel>>() {

            @Override
            public void onResponse(Call<List<HomeWorkModel>> call, Response<List<HomeWorkModel>> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("MsgDates:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("MsgDates:Res", response.body().toString());

                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                Log.d("Response", json);

                try {

                    String status = response.body().get(0).getStatus();
                    String message = response.body().get(0).getMessage();
                    HomeWorkData.clear();
                    if (status.equals("1")) {
                        HomeWorkData = (ArrayList<com.vs.schoolmessenger.model.HomeWorkData>) response.body().get(0).getData();

                        if (HomeWorkData.size() < 4) {
                            native_ads.setVisibility(View.VISIBLE);
                            adsClose.setVisibility(View.VISIBLE);
                        } else {
                            native_ads.setVisibility(View.GONE);
                            adsClose.setVisibility(View.GONE);

                        }


                        mAdapter = new HomeWorkGridAdapter(MessageDatesScreen.this, HomeWorkData, rytParent, new OnItemHomeworkClick() {
                            @Override
                            public void onMsgItemClick(HomeWorkData item) {

                            }
                        });
                        rvGridHW.setAdapter((ListAdapter) mAdapter);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        native_ads.setVisibility(View.VISIBLE);

                        if (isNewVersion.equals("1")) {
                            lblNoMessages.setVisibility(View.VISIBLE);
                            lblNoMessages.setText(message);
                            if (isOnBackPressed) {
                                LoadMoreGetHomeWorkDetails();
                            }
                        } else {
                            lblNoMessages.setVisibility(View.GONE);
                            showrecordsFound(message);
                        }
                    }
                    ShowAdvancedNativeAds.getAds(MessageDatesScreen.this, adImage, slider, "", native_ads, adsClose);


                } catch (Exception e) {
                    Log.e("MsgDates:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<HomeWorkModel>> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("MsgDates:Failure", t.toString());
            }
        });
    }

    private void filterlist(String s) {
        List<HomeWorkData> temp = new ArrayList();
        for (HomeWorkData d : HomeWorkData) {
            if (d.getDate().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList((ArrayList<com.vs.schoolmessenger.model.HomeWorkData>) temp);
    }

    private void seeMoreButtonVisiblity() {
        if (isNewVersion.equals("1")) {
            LoadMore.setVisibility(View.VISIBLE);
        } else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isNetworkConnected()) {
            LoadMore.setVisibility(View.VISIBLE);
            getHomeWorkDetails();
        }

        // ShowAds.getAds(this,adImage,slider,"",mAdView);
//        ShowAdvancedNativeAds.getAds(this, adImage, slider, "", native_ads, adsClose);

        if (HomeWorkDateWiseAdapter.mediaPlayer != null) {
            if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                HomeWorkDateWiseAdapter.mediaPlayer.stop();
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (HomeWorkDateWiseAdapter.mediaPlayer != null) {
            if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                HomeWorkDateWiseAdapter.mediaPlayer.stop();
            }
        }
    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) MessageDatesScreen.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        if (HomeWorkDateWiseAdapter.mediaPlayer != null) {
            if (HomeWorkDateWiseAdapter.mediaPlayer.isPlaying()) {
                HomeWorkDateWiseAdapter.mediaPlayer.stop();
            }
        }
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(MessageDatesScreen.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showrecordsFound(String s) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageDatesScreen.this);
        alertDialog.setTitle(R.string.alert);
        //Setting Dialog Message
        alertDialog.setMessage(s);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:
                Intent homescreen = new Intent(MessageDatesScreen.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq = new Intent(MessageDatesScreen.this, FAQScreen.class);
                startActivity(faq);

                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(MessageDatesScreen.this, "change");
                startActivity(new Intent(MessageDatesScreen.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(MessageDatesScreen.this, v, "1");
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
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();
        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(MessageDatesScreen.this);
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
        LanguageList = TeacherUtil_SharedPreference.getLanguages(MessageDatesScreen.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageDatesScreen.this);
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
        TeacherUtil_SharedPreference.putLanguageType(MessageDatesScreen.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(MessageDatesScreen.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(MessageDatesScreen.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(MessageDatesScreen.this);

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

                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), MessageDatesScreen.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), MessageDatesScreen.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), MessageDatesScreen.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), MessageDatesScreen.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), MessageDatesScreen.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), MessageDatesScreen.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), MessageDatesScreen.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), MessageDatesScreen.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), MessageDatesScreen.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), MessageDatesScreen.this);

                        if (Integer.parseInt(status) > 0) {
                            showToast(message);

                            Locale myLocale = new Locale(lang);
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
