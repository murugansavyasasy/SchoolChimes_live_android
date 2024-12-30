package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.RequestMeetingForSchoolTapAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestMeetingForSchool extends AppCompatActivity implements View.OnClickListener {
    private static final String SH_USERID = "UserId";
    private static final String SH_USERS = "userInfo";
    ViewPager viewPager;
    String userId;
    SharedPreferences shpRemember;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private PopupWindow pHelpWindow;
    private ArrayList<Profiles> childList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.request_meeting_tap_school);

        shpRemember = getSharedPreferences(SH_USERS, MODE_PRIVATE);
        userId = shpRemember.getString(SH_USERID, "");

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

        TabLayout tabLayout = (TabLayout) findViewById(R.id.request_tabLayoutrecovery);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mt_waiting_for));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mt_scheduled));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mt_rejected));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.mt_mtng_details);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        viewPager = (ViewPager) findViewById(R.id.request_pagerrecovery);
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());
        final RequestMeetingForSchoolTapAdapter adapter = new RequestMeetingForSchoolTapAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount(), userId);//, invoiceno, dmdid);
        viewPager.setAdapter(adapter);


        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:

                        break;

                    case 1:

                        break;
                    case 2:

                        break;
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void next() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHelp:

                Intent faq = new Intent(RequestMeetingForSchool.this, FAQScreen.class);
                faq.putExtra("School", "School");
                startActivity(faq);


                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(RequestMeetingForSchool.this, "change");
                startActivity(new Intent(RequestMeetingForSchool.this, TeacherChangePassword.class));

                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytLogout:

                showLogoutAlert();

                break;

            case R.id.rytHome:

                Intent home = new Intent(RequestMeetingForSchool.this, ChildrenScreen.class);
                home.putExtra("HomeScreen", "1");
                startActivity(home);
                finish();

                break;

        }
    }


    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(RequestMeetingForSchool.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(RequestMeetingForSchool.this);
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
        TeacherUtil_SharedPreference.putLanguageType(RequestMeetingForSchool.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(RequestMeetingForSchool.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(RequestMeetingForSchool.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(RequestMeetingForSchool.this);

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
        mProgressDialog.setMessage(String.valueOf(R.string.Loading));
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


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), RequestMeetingForSchool.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), RequestMeetingForSchool.this);


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

    private void showToast(String message) {

        Toast.makeText(RequestMeetingForSchool.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(RequestMeetingForSchool.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(RequestMeetingForSchool.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(RequestMeetingForSchool.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(RequestMeetingForSchool.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(RequestMeetingForSchool.this);
                startActivity(new Intent(RequestMeetingForSchool.this, TeacherSignInScreen.class));
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
