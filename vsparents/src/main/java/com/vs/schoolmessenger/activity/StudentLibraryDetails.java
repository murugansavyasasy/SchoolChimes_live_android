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
import android.view.View;
import android.view.Window;
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
import com.vs.schoolmessenger.adapter.LibraryDetailsAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.LibraryDetails;
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
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class StudentLibraryDetails extends AppCompatActivity implements View.OnClickListener {


    public LibraryDetailsAdapter mAdapter;
    String child_ID;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    ImageView imgSearch;
    TextView Searchable;
    Slider slider;
    ImageView adImage;
    AdView mAdView;
    private final List<LibraryDetails> librarylist = new ArrayList<>();
    private RecyclerView library_student_list;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.library_details);


        library_student_list = (RecyclerView) findViewById(R.id.library_student_list);

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

        Slider.init(new PicassoImageLoadingService(StudentLibraryDetails.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        child_ID = getIntent().getExtras().getString("CHILD_ID");

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.library);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.details);

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    library_student_list.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        library_student_list.setVisibility(View.VISIBLE);
                    }

                } else {
                    library_student_list.setVisibility(View.VISIBLE);
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


        libraryDetails();

        mAdapter = new LibraryDetailsAdapter(librarylist, StudentLibraryDetails.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        library_student_list.setLayoutManager(mLayoutManager);
        library_student_list.setItemAnimator(new DefaultItemAnimator());
        library_student_list.setAdapter(mAdapter);
        library_student_list.getRecycledViewPool().setMaxRecycledViews(0, 80);


    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this, adImage, slider, "", mAdView);
    }

    private void filterlist(String s) {
        List<LibraryDetails> temp = new ArrayList();
        for (LibraryDetails d : librarylist) {

            if (d.getName().toLowerCase().contains(s.toLowerCase()) || d.getIssuedDate().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }


    private void libraryDetails() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StudentLibraryDetails.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StudentID", child_ID);
        jsonObject.addProperty("Option", "BOOKDETAILS");

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.GetMemberBookList(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());

                        if (js.length() > 0) {
                            mAdapter.clearAllData();
                            LibraryDetails data;
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String RefBookID = jsonObject.getString("RefBookID");
                                String BookName1 = jsonObject.getString("BookName");

                                if (!RefBookID.equals("-2")) {
                                    String BookName = jsonObject.getString("BookName");
                                    String IssuedOn = jsonObject.getString("IssuedOn");
                                    String DueDate = jsonObject.getString("DueDate");
                                    data = new LibraryDetails(RefBookID, BookName, IssuedOn, DueDate);
                                    librarylist.add(data);
                                } else {
                                    showRecordsFound(BookName1);
                                }

                            }
                            mAdapter.notifyDataSetChanged();
                        } else {
                            showRecordsFound(getResources().getString(R.string.no_records));
                        }
                    } else {
                        //showToast("Server Response Failed");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showRecordsFound(String no_records_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(StudentLibraryDetails.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_records_found);

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

                Intent homescreen = new Intent(StudentLibraryDetails.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq = new Intent(StudentLibraryDetails.this, FAQScreen.class);
                startActivity(faq);


                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(StudentLibraryDetails.this, "change");
                startActivity(new Intent(StudentLibraryDetails.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(StudentLibraryDetails.this, v, "1");


                break;


        }
    }


    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(StudentLibraryDetails.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StudentLibraryDetails.this);
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
        TeacherUtil_SharedPreference.putLanguageType(StudentLibraryDetails.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(StudentLibraryDetails.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(StudentLibraryDetails.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(StudentLibraryDetails.this);


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


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), StudentLibraryDetails.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), StudentLibraryDetails.this);


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
        Toast.makeText(StudentLibraryDetails.this, message, Toast.LENGTH_SHORT).show();
    }

}
