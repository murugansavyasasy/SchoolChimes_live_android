package com.vs.schoolmessenger.activity;

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
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.ExamSubjectsExpandableAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamDateListClass;
import com.vs.schoolmessenger.model.ExamGroupHeader;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class ExamCircularActivity extends AppCompatActivity implements View.OnClickListener {

    ExpandableListView lvExp;
    ExamSubjectsExpandableAdapter mAdapter;
    EditText Searchable;
    ImageView imgSearch;
    RelativeLayout rytNoRecords;
    List<ExamGroupHeader> listDataHeader;
    HashMap<ExamGroupHeader, List<ExamDateListClass>> listDataChild;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    Slider slider;
    ImageView adImage;
    AdView mAdView;
    private int lastExpandedPosition = -1;
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
        setContentView(R.layout.exam_circular);

        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        mAdView = findViewById(R.id.adView);


        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        Slider.init(new PicassoImageLoadingService(ExamCircularActivity.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.exam_test));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(getResources().getString(R.string.circulars));

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        lvExp = (ExpandableListView) findViewById(R.id.lvExp);
        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        rytNoRecords = (RelativeLayout) findViewById(R.id.rytNoRecords);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


                if (mAdapter == null)
                    return;

                if (mAdapter.getGroupCount() < 1) {
                    rytNoRecords.setVisibility(View.VISIBLE);
                    lvExp.setVisibility(View.GONE);


                    if (Searchable.getText().toString().isEmpty()) {
                        lvExp.setVisibility(View.VISIBLE);
                        rytNoRecords.setVisibility(View.GONE);
                    }

                } else {
                    rytNoRecords.setVisibility(View.GONE);
                    lvExp.setVisibility(View.VISIBLE);
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

        listDataHeader = new ArrayList<ExamGroupHeader>();
        listDataChild = new HashMap<ExamGroupHeader, List<ExamDateListClass>>();

        if (isNetworkConnected()) {
            circularsExameAPI();
        } else {
            showSettingsAlert1(getResources().getString(R.string.connect_internet));
        }


        mAdapter = new ExamSubjectsExpandableAdapter(this, listDataHeader, listDataChild);

        lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    lvExp.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        lvExp.setAdapter(mAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this, adImage, slider, "", mAdView);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) ExamCircularActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void showSettingsAlert1(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ExamCircularActivity.this);

        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);


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

    private void filterlist(String s) {
        List<ExamGroupHeader> temp = new ArrayList();
        for (ExamGroupHeader d : listDataHeader) {

            if (d.getExamName().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    private void circularsExameAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ExamCircularActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(ExamCircularActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ExamCircularActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(ExamCircularActivity.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(ExamCircularActivity.this);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Getnoticeboard(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetExamsOrTests(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        listDataHeader.clear();

                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String strStatus = jsonObject.getString("ExamId");
                            String strMessage = jsonObject.getString("ExaminationName");
                            String Syllabus = jsonObject.getString("ExaminationSyllabus");

                            if (strStatus.equals("0") || strStatus.equals("-1")) {
                                showSettingsAlert1(strMessage);
                            } else {
                                ExamGroupHeader header;
                                header = new ExamGroupHeader(strStatus, strMessage, Syllabus);
                                listDataHeader.add(header);

                                lvExp.setAdapter(mAdapter);

                                JSONArray Details = new JSONArray(jsonObject.getString("SubjectDetails"));
                                ExamDateListClass model;
                                Log.d("json length", String.valueOf(js.length()));

                                List<ExamDateListClass> subject_details = new ArrayList<ExamDateListClass>();


                                subject_details.clear();

                                for (int j = 0; j < Details.length(); j++) {

                                    JSONObject jsonObject1 = Details.getJSONObject(j);

                                    model = new ExamDateListClass(Syllabus, jsonObject1.getString("Subname"), jsonObject1.getString("ExamDate"),
                                            jsonObject1.getString("ExamSession"), jsonObject1.getString("maxMark"), jsonObject1.getString("Syllabus"));

                                    subject_details.add(model);

                                }
                                listDataChild.put(listDataHeader.get(i), subject_details);
                                mAdapter.notifyDataSetChanged();

                            }


                        }
                    } else {
                        showSettingsAlert1(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }

        });
    }

    private void showToast(String check_your_internet_connectivity) {
        Toast.makeText(ExamCircularActivity.this, check_your_internet_connectivity, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:

                Intent homescreen = new Intent(ExamCircularActivity.this, HomeActivity.class);
                homescreen.putExtra("HomeScreen", "1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq = new Intent(ExamCircularActivity.this, FAQScreen.class);
                startActivity(faq);


                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(ExamCircularActivity.this, "change");
                startActivity(new Intent(ExamCircularActivity.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(ExamCircularActivity.this, v, "1");


                break;


        }
    }

    private void showLogoutAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(ExamCircularActivity.this);
        alertDialog.setTitle(getResources().getString(R.string.txt_menu_logout));
        alertDialog.setMessage(getResources().getString(R.string.want_to_logut));
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(ExamCircularActivity.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(ExamCircularActivity.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(ExamCircularActivity.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(ExamCircularActivity.this);
                startActivity(new Intent(ExamCircularActivity.this, TeacherSignInScreen.class));
                finish();


            }
        });
        alertDialog.setPositiveButton(getResources().getString(R.string.btn_sign_cancel), new DialogInterface.OnClickListener() {
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

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(ExamCircularActivity.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(ExamCircularActivity.this);
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
        TeacherUtil_SharedPreference.putLanguageType(ExamCircularActivity.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ExamCircularActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(ExamCircularActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(ExamCircularActivity.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(ExamCircularActivity.this);

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


                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), ExamCircularActivity.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), ExamCircularActivity.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), ExamCircularActivity.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), ExamCircularActivity.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), ExamCircularActivity.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), ExamCircularActivity.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), ExamCircularActivity.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), ExamCircularActivity.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), ExamCircularActivity.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), ExamCircularActivity.this);

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
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(ExamCircularActivity.this);


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



