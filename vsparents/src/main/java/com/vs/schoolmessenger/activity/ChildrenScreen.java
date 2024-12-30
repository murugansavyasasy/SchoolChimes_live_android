package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ChildProfileAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.fcmservices.Config;
import com.vs.schoolmessenger.interfaces.OnProfileItemClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChildrenScreen extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ChildrenScreen.class.getSimpleName();
    static ArrayList<Integer> isAdminMenuID = new ArrayList<>();
    static ArrayList<Integer> isStaffMenuID = new ArrayList<>();
    static ArrayList<Integer> isPrincipalMenuID = new ArrayList<>();
    static ArrayList<Integer> isGroupHedMenuID = new ArrayList<>();
    static ArrayList<Integer> isParentMenuID = new ArrayList<>();
    static ArrayList<String> isAdminMenuNames = new ArrayList<>();
    static ArrayList<String> isIsStaffMenuNames = new ArrayList<>();
    static ArrayList<String> isPrincipalMenuNames = new ArrayList<>();
    static ArrayList<String> isGroupHedMenuNames = new ArrayList<>();
    static ArrayList<String> isParentMenuNames = new ArrayList<>();
    RecyclerView rvChildList;
    LinearLayout aHome_llSchoollayout, lnrLogout, lnrFeedBack, lnrMore, Schoollayout;
    TextView aHome_tvSchoolName;
    ArrayList<String> schoolNamelist = new ArrayList<>();
    TeacherSchoolsModel schoolmodel;
    ArrayList<String> myArray = new ArrayList<>();
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    String schoolname, schooladdress;
    String IDs = "";
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    BottomNavigationView bottomNavigationView;
    RelativeLayout rytLanguage, rytPassword, rytHelp, rytLogout;
    private ChildProfileAdapter adapter;
    private ArrayList<Profiles> childList = new ArrayList<>();
    private PopupWindow pHelpWindow;
    // FCM
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_children_screen);
        TeacherUtil_SharedPreference.putChild(ChildrenScreen.this, "1");

        aHome_llSchoollayout = (LinearLayout) findViewById(R.id.aHome_llSchoollayout);
        Schoollayout = (LinearLayout) findViewById(R.id.Schoollayout);

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);

        aHome_llSchoollayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setBackgroundColor(Color.parseColor("#581845"));
                }

                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    v.setBackgroundColor(Color.parseColor("#e2dfdf"));
                }
                return false;
            }
        });

        String home = getIntent().getExtras().getString("HomeScreen", "");
        if (!home.equals("1")) {
            schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
            TeacherUtil_SharedPreference.PutChildrenScreenschoolmodel(ChildrenScreen.this, schoolmodel, "schoolModel");

            myArray = (ArrayList<String>) getIntent().getSerializableExtra("schoollist");
            TeacherUtil_SharedPreference.PutChildrenScreenMyArray(ChildrenScreen.this, myArray, "myarray");

            schools_list = (ArrayList<TeacherSchoolsModel>) getIntent().getSerializableExtra("list");
            TeacherUtil_SharedPreference.putChildrenScreenSchool_list(ChildrenScreen.this, schools_list, "schools_list");


            childList = (ArrayList<Profiles>) getIntent().getSerializableExtra("CHILD_LIST");
            TeacherUtil_SharedPreference.putChildrenDetails(ChildrenScreen.this, childList, "child_list");

            Bundle extras = getIntent().getExtras();
            schoolname = extras.getString("schoolname", "");
            TeacherUtil_SharedPreference.putChildrenScreenSchoolName(ChildrenScreen.this, schoolname);

            schooladdress = extras.getString("schooladdress", "");
            TeacherUtil_SharedPreference.putChildrenScreenSchoolAddress(ChildrenScreen.this, schooladdress);
        }


        schoolmodel = TeacherUtil_SharedPreference.getChildrenScreenSchoolModel(ChildrenScreen.this, "schoolModel");
        myArray = TeacherUtil_SharedPreference.getChildrenScreenMyArray(ChildrenScreen.this, "myarray");
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(ChildrenScreen.this, "schools_list");
        schoolname = TeacherUtil_SharedPreference.getChildrenScreenSchoolName(ChildrenScreen.this);
        schooladdress = TeacherUtil_SharedPreference.getChildrenScreenSchoolAddress(ChildrenScreen.this);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);

        String role_display = TeacherUtil_SharedPreference.getDisplayRoleMessage(ChildrenScreen.this);
        aHome_tvSchoolName = (TextView) findViewById(R.id.aHome_tvSchoolName);

        Boolean is_staff = TeacherUtil_SharedPreference.getIsStaff(ChildrenScreen.this);
        Boolean is_parent = TeacherUtil_SharedPreference.getIsParent(ChildrenScreen.this);

        if (is_staff && is_parent) {
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.choose));
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(getResources().getString(R.string.role));

            aHome_llSchoollayout.setVisibility(View.VISIBLE);
            aHome_tvSchoolName.setText(role_display);
            aHome_llSchoollayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkConnected()) {

                        String loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(ChildrenScreen.this);
                        if (loginType.equals(LOGIN_TYPE_TEACHER)) {
                            TeacherUtil_Common.school_scroll_to_position = 0;

                            if (listschooldetails.size() == 1) {
                                Intent i = new Intent(ChildrenScreen.this, Teacher_AA_Test.class);
                                i.putExtra("schoolname", schoolname);
                                i.putExtra("schooladdress", schooladdress);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                i.putExtra("schoollist", myArray);
                                i.putExtra("list", schools_list);
                                Log.d("Schoolid", TeacherUtil_Common.Principal_SchoolId);
                                i.putExtra("bottom_menu", "1");
                                startActivity(i);
                            } else if (listschooldetails.size() > 1) {
                                Intent i = new Intent(ChildrenScreen.this, SelectStaffSchools.class);
                                startActivity(i);
                            }
                        } else {
                            TeacherUtil_Common.school_scroll_to_position = 0;
                            Intent i = new Intent(ChildrenScreen.this, Teacher_AA_Test.class);
                            i.putExtra("schoolname", schoolname);
                            i.putExtra("schooladdress", schooladdress);
                            i.putExtra("TeacherSchoolsModel", schoolmodel);
                            i.putExtra("schoollist", myArray);
                            i.putExtra("list", schools_list);
                            Log.d("Schoolid", TeacherUtil_Common.Principal_SchoolId);
                            i.putExtra("bottom_menu", "1");
                            startActivity(i);
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_internet), Toast.LENGTH_LONG).show();
                    }

                }
            });

        } else {
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.choose));
            ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(getResources().getString(R.string.your_child));
        }

        childList = TeacherUtil_SharedPreference.getChildrenDetails(ChildrenScreen.this, "child_list");
        rvChildList = (RecyclerView) findViewById(R.id.child_rvChildList);
        adapter = new ChildProfileAdapter(ChildrenScreen.this, childList, new OnProfileItemClickListener() {
            @Override
            public void onItemClick(Profiles item) {
                Log.d(TAG, "Child Name: " + item.getChildName());
                if (!item.getIsNotAllow().equals("1")) {
                    Intent in = new Intent(ChildrenScreen.this, HomeActivity.class);//MessageDatesScreen.class
                    in.putExtra("Profiles", item);
                    startActivity(in);
                }

            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvChildList.setHasFixedSize(true);
        rvChildList.setLayoutManager(mLayoutManager);
        rvChildList.setItemAnimator(new DefaultItemAnimator());
        rvChildList.setAdapter(adapter);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Log.d("FCM REGISTRATION", intent.getAction());
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.d("MESSAGE", message);
                }
            }
        };
        setupBottomBar();
        displayFirebaseRegId();
    }


    private void setupBottomBar() {
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.home_bottom_navigation);

        bottomNavigationView.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_home:

                                break;


                            case R.id.menu_language_change:
                                showLanguageListPopup();
                                break;

                            case R.id.menu_more:

                                break;

                        }
                        return true;
                    }
                });
        selectHomeTab();


    }

    private void showMoreMenu(View view) {


        android.widget.PopupMenu popup = new android.widget.PopupMenu(ChildrenScreen.this, view);
        popup.getMenuInflater().inflate(R.menu.more_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.menu_ParentFAQ:

                        Intent faq = new Intent(ChildrenScreen.this, FAQScreen.class);
                        startActivity(faq);

                        return true;
                    case R.id.menu_SchoolFAQ:

                        Intent faq1 = new Intent(ChildrenScreen.this, FAQScreen.class);
                        faq1.putExtra("School", "School");
                        startActivity(faq1);

                        return true;

                    default:
                        return false;
                }


            }
        });

        popup.show();
    }

    private void selectHomeTab() {
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void changeLanguageInitial(String lang) {

        LocaleHelper local = new LocaleHelper();
        LocaleHelper.setLocale(ChildrenScreen.this, lang);
    }


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) ChildrenScreen.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHelp:

                if (schools_list != null && childList != null) {
                    if (schools_list.size() > 0 && childList.size() > 0) {
                        showMoreMenu(v);
                    } else {
                        Intent faq = new Intent(ChildrenScreen.this, FAQScreen.class);
                        startActivity(faq);
                    }
                } else {
                    Intent faq = new Intent(ChildrenScreen.this, FAQScreen.class);
                    startActivity(faq);
                }


                break;
            case R.id.rytLanguage:

                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(ChildrenScreen.this, "change");
                startActivity(new Intent(ChildrenScreen.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(ChildrenScreen.this, v, "");

                break;


        }
    }


    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.exit_app));
        builder.setPositiveButton(R.string.rb_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
                    finishAffinity();
                    moveTaskToBack(true);
                } else if (Build.VERSION.SDK_INT >= 21) {
                    finishAndRemoveTask();
                    moveTaskToBack(true);
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.teacher_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void showToast(String msg) {
        Toast.makeText(ChildrenScreen.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_homeHelp:
                setupHelpPopUp();
                pHelpWindow.showAtLocation(rvChildList, Gravity.NO_GRAVITY, 0, 0);
                return true;

            case R.id.menu_homeChangePassword:
                Util_SharedPreference.putForget(ChildrenScreen.this, "change");
                startActivity(new Intent(ChildrenScreen.this, TeacherChangePassword.class));
                return true;

            case R.id.menu_homeLogout:

                TeacherUtil_SharedPreference.putInstall(ChildrenScreen.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(ChildrenScreen.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(ChildrenScreen.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(ChildrenScreen.this);
                startActivity(new Intent(ChildrenScreen.this, TeacherSignInScreen.class));
                finish();
                return true;


            case R.id.menu_language_change:
                showLanguageListPopup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(ChildrenScreen.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(ChildrenScreen.this);
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

    private void changeLanguage(String lang, String Id) {
        TeacherUtil_SharedPreference.putLanguageType(ChildrenScreen.this, lang);
        languageChangeApi(Id, lang);

    }

    private void languageChangeApi(String languageId, final String lang) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ChildrenScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(ChildrenScreen.this);

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
        jsonObjectlanguage.addProperty("LanguageId", languageId);
        jsonObjectlanguage.addProperty("CountryID", countryId);

        Log.d("Request", jsonObject.toString());
        Log.d("RequestLanguage", jsonObjectlanguage.toString());

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

                        putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"));
                        putStaffIdstoSharedPref(jsonObject.getString("isStaffID"));
                        putAdminIdstoSharedPref(jsonObject.getString("isAdminID"));
                        putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"));
                        putParentIdstoSharedPref(jsonObject.getString("isParentID"));
                        putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"));
                        putStaffNamestoSharedPref(jsonObject.getString("isStaff"));
                        putAdminNamestoSharedPref(jsonObject.getString("isAdmin"));
                        putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"));
                        putParentNamestoSharedPref(jsonObject.getString("isParent"));


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
        isParentMenuNames.clear();
        Collections.addAll(isParentMenuNames, name4);
        TeacherUtil_SharedPreference.putParentMenuNames(isParentMenuNames, ChildrenScreen.this);

    }

    private void putGroupHeadtoSharedPref(String idGroupHead) {
        String[] name3 = idGroupHead.split(",");
        isGroupHedMenuNames.clear();
        Collections.addAll(isGroupHedMenuNames, name3);

        TeacherUtil_SharedPreference.putGroupHeadNames(isGroupHedMenuNames, ChildrenScreen.this);

    }

    private void putAdminNamestoSharedPref(String isAdmin) {
        String[] name2 = isAdmin.split(",");
        isAdminMenuNames.clear();
        Collections.addAll(isAdminMenuNames, name2);

        TeacherUtil_SharedPreference.putAdminNames(isAdminMenuNames, ChildrenScreen.this);

    }

    private void putStaffNamestoSharedPref(String isStaff) {
        String[] name1 = isStaff.split(",");
        isIsStaffMenuNames.clear();
        Collections.addAll(isIsStaffMenuNames, name1);

        TeacherUtil_SharedPreference.putStaffNames(isIsStaffMenuNames, ChildrenScreen.this);

    }

    private void putPrincipalNametoSharedPref(String isPrincipal) {
        String[] name = isPrincipal.split(",");
        isPrincipalMenuNames.clear();
        Collections.addAll(isPrincipalMenuNames, name);
        TeacherUtil_SharedPreference.putPrincipalNames(isPrincipalMenuNames, ChildrenScreen.this);

    }

    private void putParentIdstoSharedPref(String isParentID) {
        String[] items4 = isParentID.split(",");
        isParentMenuID.clear();
        for (String itemtemp : items4) {
            int result = Integer.parseInt(itemtemp);
            isParentMenuID.add(result);

        }

        TeacherUtil_SharedPreference.putparentIDs(isParentMenuID, ChildrenScreen.this);

    }

    private void putGroupHeadIdstosharedPref(String idGroupHeadID) {
        String[] items3 = idGroupHeadID.split(",");
        isGroupHedMenuID.clear();
        for (String itemtemp : items3) {
            int result = Integer.parseInt(itemtemp);
            isGroupHedMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putGroupHeadIDs(isGroupHedMenuID, ChildrenScreen.this);
    }

    private void putAdminIdstoSharedPref(String isAdminID) {
        String[] items2 = isAdminID.split(",");
        isAdminMenuID.clear();
        for (String itemtemp : items2) {
            int result = Integer.parseInt(itemtemp);
            isAdminMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putAdminIDs(isAdminMenuID, ChildrenScreen.this);
    }

    private void putStaffIdstoSharedPref(String isStaffID) {
        String[] items1 = isStaffID.split(",");
        isStaffMenuID.clear();
        for (String itemtemp : items1) {
            int result = Integer.parseInt(itemtemp);
            isStaffMenuID.add(result);
        }
        TeacherUtil_SharedPreference.putStaffIDs(isStaffMenuID, ChildrenScreen.this);
    }

    private void putPrincipalIdstoSharedPref(String isPrincipalID) {
        String[] items = isPrincipalID.split(",");
        isPrincipalMenuID.clear();
        for (String itemtemp : items) {
            int result = Integer.parseInt(itemtemp);
            isPrincipalMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putPrincipalIDs(isPrincipalMenuID, ChildrenScreen.this);
    }

    public void setupHelpPopUp() {

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

    private void helpAPI(String msg) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChildrenScreen.this);
        Log.d("Help:Mob-Query", mobNumber + " - " + msg);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetHelp(mobNumber, msg);
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

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        Log.e(TAG, "Firebase reg id: " + regId);
        updateDeviceTokenAPI(regId);

    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    private void updateDeviceTokenAPI(String strDeviceToken) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ChildrenScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChildrenScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_DeviceToken(mobNumber, strDeviceToken, "Android");
        Call<JsonArray> call = apiService.DeviceTokennew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("UpdateToken:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("UpdateToken:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");

                        Log.d("DEVICE_Token-Res", strStatus + " - " + strMsg);

                    } else {
                        Log.d("DEVICE_Token-Error", "erver Response Failed. Try again");
                    }

                } catch (Exception e) {
                    Log.e("UpdateToken:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("UpdateToken:Failure", t.toString());
            }
        });
    }
}
