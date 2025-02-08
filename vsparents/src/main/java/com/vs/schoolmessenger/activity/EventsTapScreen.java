package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.fragments.EventsFragment;
import com.vs.schoolmessenger.fragments.HolidaysFragment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class EventsTapScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String SH_USERID = "UserId";
    private static final String SH_USERS = "userInfo";
    public static EventsTapScreen instance;
    ViewPager viewPager;
    String userId;
    SharedPreferences shpRemember;
    String strChildID = "", strSchoolID = "";
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
    Slider slider;
    LinearLayout lnrAdView;
    ImageView adImage;
    AdView mAdView;
    private TabLayout allTabs;
    private EventsFragment fragmentOne;
    private HolidaysFragment fragmentTwo;
    private PopupWindow pHelpWindow;
    private ArrayList<Profiles> childList = new ArrayList<>();

    public static EventsTapScreen getInstance() {
        return instance;
    }

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
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.events_tap);


        strChildID = Util_SharedPreference.getChildIdFromSP(EventsTapScreen.this);
        strSchoolID = Util_SharedPreference.getSchoolIdFromSP(EventsTapScreen.this);

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        mAdView = findViewById(R.id.adView);


        lnrAdView = (LinearLayout) findViewById(R.id.lnrAdView);
        lnrAdView.setVisibility(View.VISIBLE);


        Slider.init(new PicassoImageLoadingService(EventsTapScreen.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);


        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);


        shpRemember = getSharedPreferences(SH_USERS, MODE_PRIVATE);
        userId = shpRemember.getString(SH_USERID, "");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.events));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        instance = this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();


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

    private void setupTabLayout() {
        fragmentOne = new EventsFragment();
        fragmentTwo = new HolidaysFragment();
        allTabs.addTab(allTabs.newTab().setText(getResources().getString(R.string.events)), true);
        allTabs.addTab(allTabs.newTab().setText(getResources().getString(R.string.holidays)));
    }

    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }

    private void bindWidgetsWithAnEvent() {
        allTabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                setCurrentTabFragment(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                replaceFragment(fragmentOne);
                break;
            case 1:
                replaceFragment(fragmentTwo);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }


    public void next() {
        //  viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:

                Intent homescreen = new Intent(EventsTapScreen.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:
                Intent faq = new Intent(EventsTapScreen.this, FAQScreen.class);
                startActivity(faq);


                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(EventsTapScreen.this, "change");
                startActivity(new Intent(EventsTapScreen.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(EventsTapScreen.this, v, "1");

                break;


        }
    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(EventsTapScreen.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        AlertDialog.Builder builder = new AlertDialog.Builder(EventsTapScreen.this);
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
        TeacherUtil_SharedPreference.putLanguageType(EventsTapScreen.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(EventsTapScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(EventsTapScreen.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(EventsTapScreen.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(EventsTapScreen.this);

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


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), EventsTapScreen.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), EventsTapScreen.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), EventsTapScreen.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), EventsTapScreen.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), EventsTapScreen.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), EventsTapScreen.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), EventsTapScreen.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), EventsTapScreen.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), EventsTapScreen.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), EventsTapScreen.this);


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


    private void showToast(String strMessage) {
        Toast.makeText(EventsTapScreen.this, strMessage, Toast.LENGTH_SHORT).show();
    }

}

