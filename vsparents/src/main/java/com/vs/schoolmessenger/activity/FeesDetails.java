package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.vs.schoolmessenger.fragments.FeesPaidFragment;
import com.vs.schoolmessenger.fragments.UpcommingFeesFragment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by voicesnap on 5/17/2018.
 */

public class FeesDetails extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    String userId;
    private TabLayout allTabs;
    public static FeesDetails instance;
    private FeesPaidFragment fragmentOne;
    private UpcommingFeesFragment fragmentTwo;

    SharedPreferences shpRemember;
    private static final String SH_USERID = "UserId";
    private static final String SH_USERS = "userInfo";

    private PopupWindow pHelpWindow;
    RelativeLayout rytHome,rytLanguage, rytPassword,rytHelp,rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_parent_fee_details);


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

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.fee);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.details);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        instance=this;
        getAllWidgets();
        bindWidgetsWithAnEvent();
        setupTabLayout();
        
        }

    private void setupTabLayout() {
        fragmentOne = new FeesPaidFragment();
        fragmentTwo = new UpcommingFeesFragment();
        allTabs.addTab(allTabs.newTab().setText(R.string.paid_details),true);
        allTabs.addTab(allTabs.newTab().setText(R.string.upcomming));
    }


    public static FeesDetails getInstance() {
        return instance;
    }

    private void getAllWidgets() {
        allTabs = (TabLayout) findViewById(R.id.tabs);
    }

    private void bindWidgetsWithAnEvent()
    {
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
    private void setCurrentTabFragment(int tabPosition)
    {
        switch (tabPosition)
        {
            case 0 :
                replaceFragment(fragmentOne);
                break;
            case 1 :
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
     //   viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);

        }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:

                Intent homescreen=new Intent(FeesDetails.this,HomeActivity.class);
                homescreen.putExtra("HomeScreen","1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq=new Intent(FeesDetails.this,FAQScreen.class);
                startActivity(faq);



                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(FeesDetails.this, "change");
                startActivity(new Intent(FeesDetails.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                showLogoutAlert();

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


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(FeesDetails.this);


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
        LanguageList = TeacherUtil_SharedPreference.getLanguages(FeesDetails.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(FeesDetails.this);
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
        TeacherUtil_SharedPreference.putLanguageType(FeesDetails.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(FeesDetails.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(FeesDetails.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(FeesDetails.this);


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



                        LanguageIDAndNames. putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"),FeesDetails.this);
                        LanguageIDAndNames.  putStaffIdstoSharedPref(jsonObject.getString("isStaffID"),FeesDetails.this);
                        LanguageIDAndNames. putAdminIdstoSharedPref(jsonObject.getString("isAdminID"),FeesDetails.this);
                        LanguageIDAndNames. putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"),FeesDetails.this);
                        LanguageIDAndNames. putParentIdstoSharedPref(jsonObject.getString("isParentID"),FeesDetails.this);
                        LanguageIDAndNames. putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"),FeesDetails.this);
                        LanguageIDAndNames. putStaffNamestoSharedPref(jsonObject.getString("isStaff"),FeesDetails.this);
                        LanguageIDAndNames. putAdminNamestoSharedPref(jsonObject.getString("isAdmin"),FeesDetails.this);
                        LanguageIDAndNames. putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"),FeesDetails.this);
                        LanguageIDAndNames. putParentNamestoSharedPref(jsonObject.getString("isParent"),FeesDetails.this);



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

    private void showToast(String message) {

        Toast.makeText(FeesDetails.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showLogoutAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(FeesDetails.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(FeesDetails.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(FeesDetails.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(FeesDetails.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(FeesDetails.this);
                startActivity(new Intent(FeesDetails.this, TeacherSignInScreen.class));
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
