package com.vs.schoolmessenger.payment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.gms.ads.AdView;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.activity.FAQScreen;
import com.vs.schoolmessenger.activity.HomeActivity;
import com.vs.schoolmessenger.activity.TeacherChangePassword;
import com.vs.schoolmessenger.activity.TeacherSignInScreen;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
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
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class FeesTab extends AppCompatActivity implements View.OnClickListener {
    ViewPager viewPager;
    String userId;

    private TabLayout allTabs;
    public static FeesTab instance;
    private FeeFragment fragmentOne;
    private InvoiceFragment fragmentTwo;

    SharedPreferences shpRemember;
    private static final String SH_USERID = "UserId";
    private static final String SH_USERS = "userInfo";

    String strChildID = "", strSchoolID = "";
    private PopupWindow pHelpWindow;

    RelativeLayout rytHome,rytLanguage, rytPassword,rytHelp,rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();

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
    AdView mAdView;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.fees_tap);

        strChildID = Util_SharedPreference.getChildIdFromSP(FeesTab.this);
        strSchoolID = Util_SharedPreference.getSchoolIdFromSP(FeesTab.this);

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        lnrAdView = (LinearLayout) findViewById(R.id.lnrAdView);
        lnrAdView.setVisibility(View.VISIBLE);


        Slider.init(new PicassoImageLoadingService(FeesTab.this));
        slider = findViewById(R.id.banner);
        ImageView adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        ShowAds.getAds(FeesTab.this,adImage,slider,"",mAdView);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);
        shpRemember = getSharedPreferences(SH_USERS, MODE_PRIVATE);
        userId = shpRemember.getString(SH_USERID, "");
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.home_pay_fees);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

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
        fragmentOne = new FeeFragment();
        fragmentTwo = new InvoiceFragment();
        allTabs.addTab(allTabs.newTab().setText(R.string.payment),true);
        allTabs.addTab(allTabs.newTab().setText(R.string.Receipt));
    }

    public static FeesTab getInstance() {
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
        //  viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:
                Intent homescreen=new Intent(FeesTab.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen","1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();
                break;
            case R.id.rytHelp:
                Intent faq=new Intent(FeesTab.this, FAQScreen.class);
                startActivity(faq);
                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(FeesTab.this, "change");
                startActivity(new Intent(FeesTab.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(FeesTab.this,v,"1");
                break;


        }
    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(FeesTab.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(FeesTab.this);
        AlertDialog alertDialog;
        builder.setTitle(R.string.choose_language);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
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
        builder.setNegativeButton(R.string.pop_password_btnCancel, new DialogInterface.OnClickListener() {
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
        TeacherUtil_SharedPreference.putLanguageType(FeesTab.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String  lang) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(FeesTab.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(FeesTab.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(FeesTab.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(FeesTab.this);


        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

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




                        LanguageIDAndNames. putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"),FeesTab.this);
                        LanguageIDAndNames. putStaffIdstoSharedPref(jsonObject.getString("isStaffID"),FeesTab.this);
                        LanguageIDAndNames. putAdminIdstoSharedPref(jsonObject.getString("isAdminID"),FeesTab.this);
                        LanguageIDAndNames. putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"),FeesTab.this);
                        LanguageIDAndNames. putParentIdstoSharedPref(jsonObject.getString("isParentID"),FeesTab.this);
                        LanguageIDAndNames. putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"),FeesTab.this);
                        LanguageIDAndNames. putStaffNamestoSharedPref(jsonObject.getString("isStaff"),FeesTab.this);
                        LanguageIDAndNames. putAdminNamestoSharedPref(jsonObject.getString("isAdmin"),FeesTab.this);
                        LanguageIDAndNames. putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"),FeesTab.this);
                        LanguageIDAndNames. putParentNamestoSharedPref(jsonObject.getString("isParent"),FeesTab.this);




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

    private void putParentNamestoSharedPref(String isParent) {
        String[] name4 = isParent.split(",");
        for (String itemtemp : name4) {
            isParentMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putParentMenuNames(isParentMenuNames, FeesTab.this);

    }

    private void putGroupHeadtoSharedPref(String idGroupHead) {
        String[] name3 = idGroupHead.split(",");
        for (String itemtemp : name3) {
            isGroupHedMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putGroupHeadNames(isGroupHedMenuNames, FeesTab.this);

    }

    private void putAdminNamestoSharedPref(String isAdmin) {
        String[] name2 = isAdmin.split(",");
        for (String itemtemp : name2) {
            isAdminMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putAdminNames(isAdminMenuNames, FeesTab.this);

    }

    private void putStaffNamestoSharedPref(String isStaff) {
        String[] name1 = isStaff.split(",");
        for (String itemtemp : name1) {
            isIsStaffMenuNames.add(itemtemp);

        }

        TeacherUtil_SharedPreference.putStaffNames(isIsStaffMenuNames, FeesTab.this);

    }

    private void putPrincipalNametoSharedPref(String isPrincipal) {
        String[] name = isPrincipal.split(",");
        for (String itemtemp : name) {
            isPrincipalMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putPrincipalNames(isPrincipalMenuNames, FeesTab.this);

    }

    private void putParentIdstoSharedPref(String isParentID) {
        String[] items4 = isParentID.split(",");
        for (String itemtemp : items4) {
            int result = Integer.parseInt(itemtemp);
            isParentMenuID.add(result);

        }

        TeacherUtil_SharedPreference.putparentIDs(isParentMenuID, FeesTab.this);

    }

    private void putGroupHeadIdstosharedPref(String idGroupHeadID) {
        String[] items3 = idGroupHeadID.split(",");
        for (String itemtemp : items3) {
            int result = Integer.parseInt(itemtemp);
            isGroupHedMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putGroupHeadIDs(isGroupHedMenuID, FeesTab.this);
    }

    private void putAdminIdstoSharedPref(String isAdminID) {
        String[] items2 = isAdminID.split(",");
        for (String itemtemp : items2) {
            int result = Integer.parseInt(itemtemp);
            isAdminMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putAdminIDs(isAdminMenuID, FeesTab.this);
    }

    private void putStaffIdstoSharedPref(String isStaffID) {
        String[] items1 = isStaffID.split(",");
        for (String itemtemp : items1) {
            int result = Integer.parseInt(itemtemp);
            isStaffMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putStaffIDs(isStaffMenuID, FeesTab.this);
    }

    private void putPrincipalIdstoSharedPref(String isPrincipalID) {
        String[] items = isPrincipalID.split(",");
        for (String itemtemp : items) {
            int result = Integer.parseInt(itemtemp);
            isPrincipalMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putPrincipalIDs(isPrincipalMenuID, FeesTab.this);
    }


    private void showLogoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FeesTab.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(FeesTab.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(FeesTab.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(FeesTab.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(FeesTab.this);
                startActivity(new Intent(FeesTab.this, TeacherSignInScreen.class));
                finish();


            }
        });
        alertDialog.setPositiveButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
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


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(FeesTab.this);


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

    private void showToast(String strMessage) {
        Toast.makeText(FeesTab.this, strMessage, Toast.LENGTH_SHORT).show();
    }

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


    }
}

