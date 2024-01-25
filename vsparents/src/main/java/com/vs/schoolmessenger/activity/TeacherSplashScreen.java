package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vs.schoolmessenger.BuildConfig;
import com.vs.schoolmessenger.LessonPlan.Activity.LessonPlanActivity;
import com.vs.schoolmessenger.LessonPlan.Adapter.LessonPlanAdapter;
import com.vs.schoolmessenger.LessonPlan.Model.LessonPlanModel;
import com.vs.schoolmessenger.OTP.AutoReadOTPCallNumberScreen;
import com.vs.schoolmessenger.R;

import com.vs.schoolmessenger.adapter.NewupdatesAdapter;
import com.vs.schoolmessenger.fcmservices.TokenUpdateScheduler;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherCountryList;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LoadingView;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService.pubStArrChildList;
import static com.vs.schoolmessenger.rest.TeacherSchoolsApiClient.BASE_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_OFFICE_STAFF;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherSplashScreen extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;

    ArrayList<TeacherCountryList> arrCountryList = new ArrayList<>();
    ArrayList<String> countryNameList = new ArrayList<>();
    boolean bForceUpdate = false;

    String strMobile, strPassword;
    ArrayList<Profiles> arrChildList = new ArrayList<>();

    ArrayList<Profiles> arrayList;
    ArrayList<String> schoolNamelist = new ArrayList<>();

    ArrayList<Languages> LanguageList = new ArrayList<>();
    String CountrID = "";

    LinearLayout lnrInternetConnection;
    Button btnRetry;
    PopupWindow popupWindow;
    ProgressDialog pDialog;
    String trmsAndConditions = "https://schoolchimes.com/vs_web/terms_conditions/";

    private AppUpdateManager appUpdateManager;
    private InstallStateUpdatedListener installStateUpdatedListener;
    private static final int FLEXIBLE_APP_UPDATE_REQ_CODE = 123;
    private static final int IMMEDIATE_APP_UPDATE_REQ_CODE = 124;

    LinearLayout lnrSnackBar;
    TextView lblInstall;
    private PopupWindow whatsNewPopupWindow;
    RelativeLayout rytParent;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_splash_screen);
        lnrSnackBar = (LinearLayout) findViewById(R.id.lnrSnackBar);
        lblInstall = (TextView) findViewById(R.id.lblInstall);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);

        TokenUpdateScheduler.scheduleTokenUpdate(TeacherSplashScreen.this);


        try {
            File dir = TeacherSplashScreen.this.getCacheDir();
            Boolean a = deleteDirCache(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }


        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        installStateUpdatedListener = state -> {
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else if (state.installStatus() == InstallStatus.INSTALLED) {
                lnrSnackBar.setVisibility(View.GONE);
                removeInstallStateUpdateListener();
            }
        };
        appUpdateManager.registerListener(installStateUpdatedListener);

        lnrInternetConnection = (LinearLayout) findViewById(R.id.lnrInternetConnection);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                networkCheck();
            }
        });

        CountrID = TeacherUtil_SharedPreference.getCountryID(TeacherSplashScreen.this);
        String lang = TeacherUtil_SharedPreference.getLanguageType(TeacherSplashScreen.this);

        if (!lang.equals("")) {
            changeLanguageInitial(lang);
        }
        TeacherUtil_SharedPreference.putScreen(TeacherSplashScreen.this, "Screen_shots");
        networkCheck();

        String getLastDate = TeacherUtil_SharedPreference.getCurrentDate(TeacherSplashScreen.this);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        if (!date.equals(getLastDate)) {
            TeacherUtil_SharedPreference.putNewProduct(TeacherSplashScreen.this, "0");
        } else {
            TeacherUtil_SharedPreference.putNewProduct(TeacherSplashScreen.this, "1");
        }
    }


    private Boolean deleteDirCache(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirCache(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d("Contacts", String.valueOf(data));
        if (requestCode == FLEXIBLE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                checkAutoLoginAndSDcardPermission();
            } else if (resultCode == RESULT_OK) {
            } else {
                checkFlexibleUpdate();
            }
        } else if (requestCode == IMMEDIATE_APP_UPDATE_REQ_CODE) {
            if (resultCode == RESULT_CANCELED) {
                finish();
            } else if (resultCode == RESULT_OK) {
            } else {
                checkImmediateUpdate();
            }
        }
    }

    private void checkImmediateUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                startImmediateUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdateFlow(appUpdateInfo);
            } else {
                checkAutoLoginAndSDcardPermission();
            }
        });
    }

    private void checkFlexibleUpdate() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                startFlexibleUpdateFlow(appUpdateInfo);
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackBarForCompleteUpdate();
            } else {
                checkAutoLoginAndSDcardPermission();
            }
        });
    }

    private void startFlexibleUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, this, TeacherSplashScreen.FLEXIBLE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void startImmediateUpdateFlow(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, TeacherSplashScreen.IMMEDIATE_APP_UPDATE_REQ_CODE);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    private void popupSnackBarForCompleteUpdate() {
        lnrSnackBar.setVisibility(View.VISIBLE);
        lblInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appUpdateManager != null) {
                    appUpdateManager.completeUpdate();
                }
            }
        });
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(installStateUpdatedListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeInstallStateUpdateListener();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void networkCheck() {
        if (isNetworkConnected()) {
            lnrInternetConnection.setVisibility(View.GONE);
            isContactPermissionGranted();
        } else {
            lnrInternetConnection.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.connect_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private void isContactPermissionGranted() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_WIFI_STATE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            checkNetworkAndLoginCredentials();
                        } else {
                            finish();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSplashScreen.this);
        builder.setTitle(R.string.need_permission);
        builder.setMessage(R.string.this_app_need_permission);
        builder.setPositiveButton(R.string.go_to_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(R.string.teacher_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void changeLanguageInitial(String lang) {
        Locale myLocale = new Locale(lang);
        Locale.setDefault(myLocale);
        Configuration config = new Configuration();
        config.locale = myLocale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherSplashScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.connect_internet);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();

            }
        });

        alertDialog.show();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) TeacherSplashScreen.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }


    private void checkNetworkAndLoginCredentials() {
        if (TeacherUtil_Common.isNetworkAvailable(TeacherSplashScreen.this)) {
            int SPLASH_TIME_OUT = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String strBaseURL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherSplashScreen.this);
                    if (!strBaseURL.equals("")) {
                        String forget = TeacherUtil_SharedPreference.getForgetPasswordOtp(TeacherSplashScreen.this);
                        if (forget.equals("1")) {
                            TeacherUtil_SharedPreference.putForgetPasswordOTP(TeacherSplashScreen.this, "0");
                            otpCallApi();
                        } else {
                            checkAppUpdateAPI();
                        }
                    } else {
                        appTermsAndConditions("1");

                    }
                }
            }, SPLASH_TIME_OUT);
        } else {
            showAlert(getResources().getString(R.string.alert), getResources().getString(R.string.check_internet));
        }
    }

    private void otpCallApi() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherSplashScreen.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherSplashScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSplashScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        LoadingView.showProgress(TeacherSplashScreen.this);

        String number = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherSplashScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", number);

        Log.d("request", jsonObject.toString());
        Call<JsonArray> call = apiService.GetPasswordResetStatus(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    LoadingView.hideProgress();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");

                            if (Status.equals("1")) {

                                String forgot = Util_SharedPreference.getForget(TeacherSplashScreen.this);
                                if (forgot.equals("forget")) {
                                    Intent intent = new Intent(TeacherSplashScreen.this, OTPCallNumberScreen.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                checkAppUpdateAPI();
                            }


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void appTermsAndConditions(final String newinstallOrUpdate) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.app_terms_and_conditions, null);
        popupWindow = new PopupWindow(layout, androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT, androidx.appcompat.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        final Button btnAgree = (Button) layout.findViewById(R.id.btnAgree);
        final WebView terms_webView = (WebView) layout.findViewById(R.id.terms_webView);

        pDialog = new ProgressDialog(TeacherSplashScreen.this);
        pDialog.setMessage("");
        pDialog.setCancelable(false);
        terms_webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();

                }
            }
        });

        WebSettings webSettings = terms_webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        terms_webView.getSettings().setBuiltInZoomControls(true);
        terms_webView.loadUrl(trmsAndConditions);

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String androidId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                termsAndConditionsAgreeApi(androidId);
                if (newinstallOrUpdate.equals("1")) {
                    popupWindow.dismiss();
                    TeacherUtil_SharedPreference.putAppTermsAndConditions(TeacherSplashScreen.this, "1");
                    countryListAPI();
                } else {
                    popupWindow.dismiss();
                    TeacherUtil_SharedPreference.putAppTermsAndConditions(TeacherSplashScreen.this, "1");
                    String update = TeacherUtil_SharedPreference.getupdateversionFromSP(TeacherSplashScreen.this);
                    String version = getString(R.string.teacher_app_version_id);
                    if (update.equals("")) {
                        TeacherUtil_SharedPreference.putautoupdateToSP(TeacherSplashScreen.this, version);
                        countryListAPI();

                    } else {
                        checkAutoLoginAndSDcardPermission();
                    }
                }

            }
        });


    }

    private void termsAndConditionsAgreeApi(String androidId) {

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SecureID", androidId);
        jsonObject.addProperty("OtherDetails", "Android");
        jsonObject.addProperty("isAgreed", "1");

        Log.d("Request", jsonObject.toString());
        Call<JsonArray> call = apiService.AgreeTermsAndConditions(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {

                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void goToSignInScreen(String msg) {
        Intent inChangePass = new Intent(TeacherSplashScreen.this, TeacherSignInScreen.class);
        startActivity(inChangePass);
        finish();
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                TeacherSplashScreen.this);

        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.teacher_ic_close);

        alertDialog.setNeutralButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        alertDialog.show();
    }

    public boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED ) {

                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
                return true;
            } else {
                Log.v("EWRITE_EXTERNAL_STORAG", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SCHEDULE_EXACT_ALARM}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;

            }
        } else {
            Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    String value = TeacherUtil_SharedPreference.getInstall(TeacherSplashScreen.this);
                    String otpValue = TeacherUtil_SharedPreference.getOTPNum(TeacherSplashScreen.this);
                    String mobilescreen = TeacherUtil_SharedPreference.getMobileNumberScreen(TeacherSplashScreen.this);

                    if (mobilescreen.equals("1")) {
                        openMobileNumbeScreen();
                    } else if (otpValue.equals("1")) {
                        openMobileNumbeScreen();

                    } else if (value.equals("1")) {
                        openSingInScreen();
                    } else {

                        openMobileNumbeScreen();
                    }
                }
                return;
            }
        }
    }

    private void openSingInScreen() {

        startActivity(new Intent(TeacherSplashScreen.this, TeacherSignInScreen.class));
        finish();

    }

    private void openMobileNumbeScreen() {
        startActivity(new Intent(TeacherSplashScreen.this, MobileNumberScreen.class));
        finish();
    }

    private void countryListAPI() {
        LoadingView.showProgress(TeacherSplashScreen.this);
        String strBase = BASE_URL;
        TeacherSchoolsApiClient.changeApiBaseUrl(strBase);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetCountryList(getString(R.string.teacher_app_id));
        Call<JsonArray> call = apiService.GetCountryList(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                LoadingView.hideProgress();


                Log.d("GetCountry:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetCountry:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        if (!(jsonObject.getString("CountryID")).equals("")) {
                            TeacherCountryList countryList;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                countryList = new TeacherCountryList(jsonObject.getString("CountryID"), jsonObject.getString("CountryName")
                                        , jsonObject.getString("MobileNumberLength"), jsonObject.getString("CountryCode"), jsonObject.getString("BaseUrl"));

                                arrCountryList.add(countryList);
                                countryNameList.add(countryList.getStrCountyName());
                            }
                            showCountryListBox();
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                            finish();
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        finish();
                    }

                } catch (Exception e) {
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                LoadingView.hideProgress();

                showToast(getResources().getString(R.string.check_internet));
                finish();
            }
        });
    }

    private void showCountryListBox() {
        String[] countriesArray = countryNameList.toArray(new String[countryNameList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSplashScreen.this);
        AlertDialog alertDialog;
        builder.setTitle(getResources().getString(R.string.choose_country));
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                TeacherCountryList item = arrCountryList.get(selectedPosition);
                TeacherUtil_SharedPreference.putCountryInforToSP(TeacherSplashScreen.this, item.getStrCountyID(),
                        item.getStrCountyName(), item.getStrCountyMobileLength(), item.getStrCountyCode(),
                        item.getStrBaseURL());

                TeacherUtil_SharedPreference.putBaseURL(TeacherSplashScreen.this, item.getStrBaseURL());
                CountrID = TeacherUtil_SharedPreference.getCountryID(TeacherSplashScreen.this);
                BASE_URL = item.getStrBaseURL();
                TeacherSchoolsApiClient.changeApiBaseUrl(item.getStrBaseURL());
                checkUpdateFirstTime();

            }
        });
        builder.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
    }


    private void checkUpdateFirstTime() {
        String strBaseURL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherSplashScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(strBaseURL);
        LoadingView.showProgress(TeacherSplashScreen.this);
        String countyID = TeacherUtil_SharedPreference.getCountryID(TeacherSplashScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_VersionCheck(getString(R.string.teacher_app_version_id), getString(R.string.teacher_app_id), "Android", countyID);
        Call<JsonArray> call = apiService.VersionCheck(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                LoadingView.hideProgress();

                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String Offerslink = jsonObject.getString("Offerslink");
                        String ImageSize = jsonObject.getString("ImageSize");
                        String PdfSize = jsonObject.getString("PdfSize");
                        String FileContent = jsonObject.getString("FileContent");
                        String VideoSizeLimit = jsonObject.getString("VideoSizeLimit");
                        String VideoSizeLimitAlert = jsonObject.getString("VideoSizeLimitAlert");
                        String Videotoken = jsonObject.getString("VideoJson");
                        String numbers = jsonObject.getString("OtpDialInbound");
                        String ReportNewLink = jsonObject.getString("ReportsLink");
                        String helplineURL = jsonObject.getString("helplineURL");
                        TeacherUtil_SharedPreference.putHelpLineUrl(TeacherSplashScreen.this, helplineURL);
                        TeacherUtil_SharedPreference.putReportURL(TeacherSplashScreen.this, ReportNewLink);
                        TeacherUtil_SharedPreference.putDialNumbers(TeacherSplashScreen.this, numbers);
                        String FeePaymentGateway_URL = jsonObject.getString("FeePaymentGateway");
                        TeacherUtil_SharedPreference.putPaymentUrl(TeacherSplashScreen.this, FeePaymentGateway_URL);
                        String ProfileTitle = jsonObject.getString("ProfileTitle");
                        String ProfileLink = jsonObject.getString("ProfileLink");
                        String ProfileUploadTitle = jsonObject.getString("UploadProfileTitle");
                        String NewProductLink = jsonObject.getString("NewProductLink");
                        String adTimerInterval = jsonObject.getString("adTimerInterval");
                        TeacherUtil_SharedPreference.putAdTimeInterval(TeacherSplashScreen.this, adTimerInterval);
                        TeacherUtil_SharedPreference.putNewProductsLink(TeacherSplashScreen.this, NewProductLink);
                        TeacherUtil_SharedPreference.putProfilLink(TeacherSplashScreen.this, ProfileLink);
                        TeacherUtil_SharedPreference.putProfilTitle(TeacherSplashScreen.this, ProfileTitle);
                        TeacherUtil_SharedPreference.putUploadProfileTitle(TeacherSplashScreen.this, ProfileUploadTitle);
                        TeacherUtil_SharedPreference.putSize(TeacherSplashScreen.this, ImageSize, PdfSize, FileContent, VideoSizeLimit, VideoSizeLimitAlert, Videotoken);
                        TeacherUtil_SharedPreference.putOfferLink(TeacherSplashScreen.this, Offerslink);
                        TeacherUtil_SharedPreference.putLanguages(TeacherSplashScreen.this, LanguageList, "Language");
                        TeacherUtil_SharedPreference.putOTPTimer(TeacherSplashScreen.this, jsonObject.getString("ResendOTPTimer"));


                        String version = getString(R.string.teacher_app_version_id);
                        if (!("108".equals(version))) {
                            TeacherUtil_SharedPreference.putautoupdateToSP(TeacherSplashScreen.this, version);
                            checkAppUpdateAPI();
                        } else {

                            String value = TeacherUtil_SharedPreference.getInstall(TeacherSplashScreen.this);
                            String otpValue = TeacherUtil_SharedPreference.getOTPNum(TeacherSplashScreen.this);
                            String mobilescreen = TeacherUtil_SharedPreference.getMobileNumberScreen(TeacherSplashScreen.this);

                            if (mobilescreen.equals("1")) {
                                openMobileNumbeScreen();
                            } else if (otpValue.equals("1")) {
                                openMobileNumbeScreen();
                            } else if (value.equals("1")) {
                                openSingInScreen();
                            } else {
                                openMobileNumbeScreen();
                            }
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());
                    checkAutoLoginAndSDcardPermission();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                LoadingView.hideProgress();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }

    private void checkAppUpdateAPI() {
        String strBaseURL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherSplashScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(strBaseURL);
        LoadingView.showProgress(TeacherSplashScreen.this);

        String countyID = TeacherUtil_SharedPreference.getCountryID(TeacherSplashScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_VersionCheck(getString(R.string.teacher_app_version_id), getString(R.string.teacher_app_id), "Android", countyID);
        Call<JsonArray> call = apiService.VersionCheck(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                LoadingView.hideProgress();

                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        final String strUpdateAvailable = jsonObject.getString("UpdateAvailable");
                        final String strForceUpdate = jsonObject.getString("ForceUpdate");
                        final String PlaystoreMarketId = jsonObject.getString("PlaystoreMarketId");
                        final String PlayStoreLink = jsonObject.getString("PlayStoreLink");
                        final String VersionAlertTitle = jsonObject.getString("VersionAlertTitle");
                        final String VersionAlertContent = jsonObject.getString("VersionAlertContent");
                        String Offerslink = jsonObject.getString("Offerslink");
                        String isAlertAvailable = jsonObject.getString("isAlertAvailable");
                        String AlertContent = jsonObject.getString("AlertContent");
                        String AlertTitle = jsonObject.getString("AlertTitle");
                        String ImageSize = jsonObject.getString("ImageSize");
                        String PdfSize = jsonObject.getString("PdfSize");
                        String FileContent = jsonObject.getString("FileContent");
                        String VideoSizeLimit = jsonObject.getString("VideoSizeLimit");
                        String VideoSizeLimitAlert = jsonObject.getString("VideoSizeLimitAlert");
                        String Videotoken = jsonObject.getString("VideoJson");

                        String numbers = jsonObject.getString("OtpDialInbound");
                        String ReportNewLink = jsonObject.getString("ReportsLink");
                        String helplineURL = jsonObject.getString("helplineURL");

                        String NewVersion = jsonObject.getString("NewVersion");
                        String NewUpdates = jsonObject.getString("NewUpdates");

                        TeacherUtil_SharedPreference.putHelpLineUrl(TeacherSplashScreen.this, helplineURL);

                        TeacherUtil_SharedPreference.putReportURL(TeacherSplashScreen.this, ReportNewLink);
                        TeacherUtil_SharedPreference.putDialNumbers(TeacherSplashScreen.this, numbers);

                        String FeePaymentGateway_URL = jsonObject.getString("FeePaymentGateway");
                        TeacherUtil_SharedPreference.putPaymentUrl(TeacherSplashScreen.this, FeePaymentGateway_URL);


                        String ProfileTitle = jsonObject.getString("ProfileTitle");
                        String ProfileLink = jsonObject.getString("ProfileLink");
                        String ProfileUploadTitle = jsonObject.getString("UploadProfileTitle");
                        String NewProductLink = jsonObject.getString("NewProductLink");
                        String InAppUpdate = jsonObject.getString("InAppUpdate");
                        String adTimerInterval = jsonObject.getString("adTimerInterval");
                        TeacherUtil_SharedPreference.putAdTimeInterval(TeacherSplashScreen.this, adTimerInterval);

                        TeacherUtil_SharedPreference.putNewProductsLink(TeacherSplashScreen.this, NewProductLink);
                        TeacherUtil_SharedPreference.putProfilLink(TeacherSplashScreen.this, ProfileLink);
                        TeacherUtil_SharedPreference.putProfilTitle(TeacherSplashScreen.this, ProfileTitle);
                        TeacherUtil_SharedPreference.putUploadProfileTitle(TeacherSplashScreen.this, ProfileUploadTitle);

                        TeacherUtil_SharedPreference.putSize(TeacherSplashScreen.this, ImageSize, PdfSize, FileContent, VideoSizeLimit, VideoSizeLimitAlert, Videotoken);
                        TeacherUtil_SharedPreference.putOfferLink(TeacherSplashScreen.this, Offerslink);
                        Languages model;

                        JSONArray languages = jsonObject.getJSONArray("Languages");
                        for (int i = 0; i < languages.length(); i++) {
                            JSONObject object = languages.getJSONObject(i);
                            String ID = object.getString("Id");
                            String name = object.getString("Language");
                            String languageCode = object.getString("ScriptCode");
                            model = new Languages(ID, name, languageCode);
                            LanguageList.add(model);
                        }

                        TeacherUtil_SharedPreference.putLanguages(TeacherSplashScreen.this, LanguageList, "Language");
                        TeacherUtil_SharedPreference.putOTPTimer(TeacherSplashScreen.this, jsonObject.getString("ResendOTPTimer"));

                        if (isAlertAvailable.equals("1")) {
                            {
                                final Dialog dialog = new Dialog(TeacherSplashScreen.this);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.custom_aert_dialog);
                                TextView textTitle = (TextView) dialog.findViewById(R.id.textTitle);
                                TextView textContent = (TextView) dialog.findViewById(R.id.textContent);
                                TextView btnOk = (TextView) dialog.findViewById(R.id.btnOk);
                                textTitle.setText(AlertTitle);
                                textContent.setText(AlertContent);
                                btnOk.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialog.dismiss();
                                        nextStep(strUpdateAvailable, strForceUpdate, VersionAlertTitle,
                                                VersionAlertContent, PlaystoreMarketId, PlayStoreLink, InAppUpdate, NewVersion, NewUpdates);
                                    }
                                });

                                dialog.show();

                            }
                        } else {
                            nextStep(strUpdateAvailable, strForceUpdate, VersionAlertTitle,
                                    VersionAlertContent, PlaystoreMarketId, PlayStoreLink, InAppUpdate, NewVersion, NewUpdates);
                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());
                    checkAutoLoginAndSDcardPermission();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                LoadingView.hideProgress();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }


    private void whatsNewPopup(String inAppUpdate, String strUpdateAvailable, String strForceUpdate, String playstoreMarketId, String playStoreLink, String new_version, String new_updates) {
        bForceUpdate = strForceUpdate.equals("1");

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.whats_new_popup, null);
        whatsNewPopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        whatsNewPopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                whatsNewPopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView btnNotNow = (TextView) layout.findViewById(R.id.btnNotNow);
        TextView btnUpdate = (TextView) layout.findViewById(R.id.btnUpdate);
        TextView idUpdateTitle = (TextView) layout.findViewById(R.id.idUpdateTitle);
        TextView lblYourVersion = (TextView) layout.findViewById(R.id.lblYourVersion);
        TextView lblNewVersion = (TextView) layout.findViewById(R.id.lblNewVersion);
        TextView lblYourAppVersion = (TextView) layout.findViewById(R.id.lblYourAppVersion);
        TextView lblNewVersionCode = (TextView) layout.findViewById(R.id.lblNewVersionCode);

        RecyclerView recycleNewUpdates = (RecyclerView) layout.findViewById(R.id.recycleNewUpdates);
        String current_version = BuildConfig.VERSION_NAME;
        lblYourAppVersion.setText(current_version);
        lblNewVersionCode.setText(new_version);

        String[] separated = new_updates.split("~");
        NewupdatesAdapter paymentAdapter = new NewupdatesAdapter(separated, TeacherSplashScreen.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleNewUpdates.setLayoutManager(mLayoutManager);
        recycleNewUpdates.setItemAnimator(new DefaultItemAnimator());
        recycleNewUpdates.setAdapter(paymentAdapter);
        paymentAdapter.notifyDataSetChanged();


        if (bForceUpdate) {
            btnNotNow.setVisibility(View.GONE);
        } else {
            btnNotNow.setVisibility(View.VISIBLE);
        }

        idUpdateTitle.setTypeface(null, Typeface.BOLD);
        lblNewVersion.setTypeface(null, Typeface.BOLD);
        lblYourVersion.setTypeface(null, Typeface.BOLD);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsNewPopupWindow.dismiss();
                if (inAppUpdate.equals("1")) {
                    if (strUpdateAvailable.equals("1") && strForceUpdate.equals("1")) {
                        checkImmediateUpdate();
                    } else if (strUpdateAvailable.equals("1") && strForceUpdate.equals("0")) {
                        checkFlexibleUpdate();
                    }
                } else {
                    TeacherUtil_SharedPreference.putAppTermsAndConditions(TeacherSplashScreen.this, "");
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(playstoreMarketId + appPackageName));
                        startActivity(intent);

                    } catch (android.content.ActivityNotFoundException anfe) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(Uri.parse(playStoreLink + appPackageName));
                        startActivity(intent);
                    }
                }
            }
        });

        btnNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whatsNewPopupWindow.dismiss();
                checkAutoLoginAndSDcardPermission();
            }
        });
    }

    private void nextStep(String strUpdateAvailable, String strForceUpdate, String versionAlertTitle, String versionAlertContent, String playstoreMarketId, String playStoreLink, String InAppUpdate, String new_version, String new_updates) {

        if (strUpdateAvailable.equals("1")) {
            bForceUpdate = strForceUpdate.equals("1");
            TeacherUtil_SharedPreference.putautoupdateToSP(TeacherSplashScreen.this, getString(R.string.teacher_app_version_id));

            whatsNewPopup(InAppUpdate, strUpdateAvailable, strForceUpdate, playstoreMarketId, playStoreLink, new_version, new_updates);

//            if(InAppUpdate.equals("1")) {
//
//                if (strUpdateAvailable.equals("1") && strForceUpdate.equals("1")) {
//                    checkImmediateUpdate();
//                } else if (strUpdateAvailable.equals("1") && strForceUpdate.equals("0")) {
//                    checkFlexibleUpdate();
//                }
//            }
//            else {
//                showPlayStoreAlert(versionAlertTitle, versionAlertContent, playstoreMarketId, playStoreLink);
//            }


        } else if (strUpdateAvailable.equals("0")) {
            TeacherUtil_SharedPreference.putautoupdateToSP(TeacherSplashScreen.this, getString(R.string.teacher_app_version_id));
            String update = TeacherUtil_SharedPreference.getupdateversionFromSP(TeacherSplashScreen.this);
            String version = getString(R.string.teacher_app_version_id);
            String appterms = TeacherUtil_SharedPreference.getAppTermsAndCondition(TeacherSplashScreen.this);
            if (appterms.equals("")) {
                appTermsAndConditions("1");
            } else {
                if (update.equals("")) {
                    TeacherUtil_SharedPreference.putautoupdateToSP(TeacherSplashScreen.this, version);
                    countryListAPI();

                } else {
                    Log.d("auto", "auto");
                    checkAutoLoginAndSDcardPermission();
                }
            }
        } else {
            showToast(strForceUpdate);
        }

    }

    private void getUserDetails() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSplashScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

//        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherSplashScreen.this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        if (!this.isFinishing())
//            mProgressDialog.show();

        LoadingView.showProgress(TeacherSplashScreen.this);


        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("ID_android", androidId);

        Log.d("Mobileno", strMobile + strPassword);
        strMobile = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherSplashScreen.this);
        strPassword = TeacherUtil_SharedPreference.getPasswordFromSP(TeacherSplashScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.GetUserDetails(strMobile, strPassword, androidId);
        Call<JsonArray> call = apiService.getStaffDetail(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();

                LoadingView.hideProgress();
                Log.d("Login:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Login:Res", response.body().toString());
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String Status = jsonObject.getString("Status");
                        String Message = jsonObject.getString("Message");


                        String strlogin = "";
                        TeacherSchoolsModel schoolmodel = null;
                        listschooldetails = new ArrayList<>();
                        if (Status.equals("1")) {

                            String role = jsonObject.getString("staff_role");
                            String display_role = jsonObject.getString("staff_display_role");
                            TeacherUtil_SharedPreference.putRole(TeacherSplashScreen.this, role);
                            TeacherUtil_SharedPreference.putDisplayRoleMessage(TeacherSplashScreen.this, display_role);
                            TeacherUtil_SharedPreference.putLoginMessage(TeacherSplashScreen.this, Message);
                            String ImageCount = jsonObject.getString("ImageCount");
                            TeacherUtil_SharedPreference.putImageCount(TeacherSplashScreen.this, ImageCount);

                            Boolean is_parent = jsonObject.getBoolean("is_parent");
                            Boolean is_staff = jsonObject.getBoolean("is_staff");
                            TeacherUtil_SharedPreference.putIsStaff(TeacherSplashScreen.this, is_staff);
                            TeacherUtil_SharedPreference.putIsParent(TeacherSplashScreen.this, is_parent);


                            TeacherUtil_Common.maxEmergencyvoicecount = jsonObject.getInt("MaxEmergencyVoiceDuration");
                            TeacherUtil_Common.maxGeneralvoicecount = jsonObject.getInt("MaxGeneralVoiceDuartion");
                            TeacherUtil_Common.maxHWVoiceDuration = jsonObject.getInt("MaxHWVoiceDuration");
                            TeacherUtil_Common.maxGeneralSMSCount = jsonObject.getInt("MaxGeneralSMSCount");
                            TeacherUtil_Common.maxHomeWorkSMSCount = jsonObject.getInt("MaxHomeWorkSMSCount");


                            if (is_staff && is_parent) {

                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");

                                for (int i = 0; i < jSONArray1.length(); i++) {

                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true,
                                            jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"));
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                    schoolNamelist.add(jsonObjectdetailsStaff.getString("SchoolName"));

                                }

                                JSONObject object = jSONArray1.getJSONObject(0);
                                if (listschooldetails.size() == 1) {

                                    String strSchoolId = object.getString("SchoolID");
                                    String strStaffId = object.getString("StaffID");
                                    TeacherUtil_Common.Principal_staffId = strStaffId;
                                    TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                                    TeacherUtil_SharedPreference.putShoolID(TeacherSplashScreen.this, strSchoolId);
                                    TeacherUtil_SharedPreference.putStaffID(TeacherSplashScreen.this, strStaffId);
                                    String logo = object.getString("SchoolLogo");
                                    TeacherUtil_SharedPreference.putSchoolLogo(TeacherSplashScreen.this, logo);
                                }


                                String logo = object.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(TeacherSplashScreen.this, logo);
                                String strSchoolId = object.getString("SchoolID");
                                String strStaffId1 = object.getString("StaffID");
                                String schoolname = object.getString("SchoolName");
                                String schooladdress = object.getString("SchoolAddress");
                                TeacherUtil_SharedPreference.putSchoolName(TeacherSplashScreen.this, schoolname);
                                TeacherUtil_SharedPreference.putStaffAddress(TeacherSplashScreen.this, schooladdress);


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", jSONArray.length() + "");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"),
                                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);

                                TeacherUtil_SharedPreference.listSchoolDetails(TeacherSplashScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(TeacherSplashScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(TeacherSplashScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(TeacherSplashScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                inHome.putExtra("SCHOOL_ID & Staff_ID", strSchoolId + " " + strStaffId1);
                                inHome.putExtra("schoolname", schoolname);
                                inHome.putExtra("Staff_ID1", strStaffId1);
                                inHome.putExtra("schoollist", schoolNamelist);
                                inHome.putExtra("schooladdress", schooladdress);
                                inHome.putExtra("TeacherSchoolsModel", schoolmodel);
                                inHome.putExtra("list", listschooldetails);
                                if (role.equals("p1")) {
                                    strlogin = LOGIN_TYPE_HEAD;
                                } else if (role.equals("p2")) {
                                    strlogin = LOGIN_TYPE_PRINCIPAL;

                                } else if (role.equals("p3")) {
                                    strlogin = LOGIN_TYPE_TEACHER;

                                } else if (role.equals("p4")) {
                                    strlogin = LOGIN_TYPE_ADMIN;

                                } else if (role.equals("p5")) {
                                    strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                }

                                startActivity(inHome);
                                finish();

                            } else if (!is_staff && is_parent) {

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", jSONArray.length() + "");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"),
                                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                Intent inHome = new Intent(TeacherSplashScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                startActivity(inHome);
                                finish();


                            } else if (role.equals("p2")) {
                                JSONArray jSONArray = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jsonObjectdetails = jSONArray.getJSONObject(i);

                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolID"),
                                            jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo")
                                            , jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"),
                                            jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"));
                                    Log.d("value1", jsonObjectdetails.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                    schoolNamelist.add(jsonObjectdetails.getString("SchoolName"));
                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetails.getString("SchoolID");
                                        String strStaffId = jsonObjectdetails.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;


                                        String logo = jsonObjectdetails.getString("SchoolLogo");
                                        TeacherUtil_SharedPreference.putSchoolLogo(TeacherSplashScreen.this, logo);
                                    }
                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                Intent i = new Intent(TeacherSplashScreen.this, Teacher_AA_Test.class);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                i.putExtra("schoollist", schoolNamelist);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                finish();

                            } else if (role.equals("p1")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolID"),
                                            jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo")
                                            , jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"),
                                            jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending")
                                    );
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);

                                Intent i = new Intent(TeacherSplashScreen.this, Teacher_AA_Test.class);
                                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                                TeacherUtil_SharedPreference.isAdminFromSP(TeacherSplashScreen.this);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_HEAD;
                                finish();

                            } else if (role.equals("p3")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                                        jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                        , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                        jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending")
                                );
                                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                listschooldetails.add(schoolmodel);


                                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(TeacherSplashScreen.this, logo);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");

                                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                                TeacherUtil_Common.Principal_staffId = strStaffId1;
                                TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                                if (listschooldetails.size() == 1) {
                                    Intent i = new Intent(TeacherSplashScreen.this, Teacher_AA_Test.class);
                                    i.putExtra("SCHOOL_ID & Staff_ID", strSchoolId + " " + strStaffId1);
                                    i.putExtra("schoolname", schoolname);
                                    i.putExtra("Staff_ID1", strStaffId1);
                                    i.putExtra("schooladdress", schooladdress);
                                    i.putExtra("TeacherSchoolsModel", schoolmodel);
                                    i.putExtra("list", listschooldetails);
                                    Log.d("Schoolid", TeacherUtil_Common.Principal_SchoolId);
                                    startActivity(i);
                                    strlogin = LOGIN_TYPE_TEACHER;
                                    finish();
                                } else if (listschooldetails.size() > 1) {
                                    Intent i = new Intent(TeacherSplashScreen.this, SelectStaffSchools.class);
                                    startActivity(i);
                                    strlogin = LOGIN_TYPE_TEACHER;
                                    finish();
                                }
                            } else if (role.equals("p4")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending")
                                    );
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);

                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                                    }

                                }


                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                Intent i = new Intent(TeacherSplashScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_ADMIN;
                                finish();

                            } else if (role.equals("p5")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending")
                                    );
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);

                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                                    }

                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, true);
                                Intent i = new Intent(TeacherSplashScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                finish();

                            }


                            TeacherUtil_SharedPreference.putLoggedInAsToSP(TeacherSplashScreen.this, strlogin);


                        } else if ((Status.toUpperCase()).equals("RESET")) {
                            Util_SharedPreference.putForget(TeacherSplashScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSplashScreen.this, strMobile, strPassword, false);
                            Intent inChangePass = new Intent(TeacherSplashScreen.this, TeacherChangePassword.class);
                            startActivity(inChangePass);
                        } else {
                            startActivity(new Intent(TeacherSplashScreen.this, TeacherSignInScreen.class));
                            finish();
                        }

                    } else {
                        showToast("No Records Found..");
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("Login:Exception", e.getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
//                if (mProgressDialog.isShowing())
//                    mProgressDialog.dismiss();

                LoadingView.hideProgress();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("Login:Failure", t.toString());
            }
        });
    }

    private void validateUser() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSplashScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

//        final ProgressDialog mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setIndeterminate(true);
//        mProgressDialog.setMessage("Loading...");
//        mProgressDialog.setCancelable(false);
//        mProgressDialog.show();

        LoadingView.showProgress(TeacherSplashScreen.this);


        strMobile = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherSplashScreen.this);
        strPassword = TeacherUtil_SharedPreference.getPasswordFromSP(TeacherSplashScreen.this);

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        JsonObject json_object = TeacherUtil_JsonRequest.getJsonArray_ValidateUser(strMobile, androidId, strPassword);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ValidateUser(json_object);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
//                    if (mProgressDialog.isShowing())
//                        mProgressDialog.dismiss();

                    LoadingView.hideProgress();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");
                            if (Status.equals("1")) {
                                String numberExists = jsonObject.getString("isNumberExists");
                                String passwordUpdated = jsonObject.getString("isPasswordUpdated");
                                String click_here = jsonObject.getString("MoreInfo");
                                String redirect_to_otp;
                                if (jsonObject.has("redirect_to_otp")) {
                                    redirect_to_otp = jsonObject.getString("redirect_to_otp");
                                } else {
                                    redirect_to_otp = "";
                                }
                                TeacherUtil_SharedPreference.putNoteMessage(TeacherSplashScreen.this, click_here);
                                TeacherUtil_SharedPreference.putOTPNote(TeacherSplashScreen.this, Message);

                                if (numberExists.equals("1") && passwordUpdated.equals("1")) {

                                    if (redirect_to_otp.equals("1")) {
                                        Intent inChangePass = new Intent(TeacherSplashScreen.this, AutoReadOTPCallNumberScreen.class);
                                        startActivity(inChangePass);
                                        finish();
                                    } else {
                                        getUserDetails();
                                    }
                                } else if (numberExists.equals("1") && passwordUpdated.equals("0")) {
                                    Util_SharedPreference.putForget(TeacherSplashScreen.this, "New");
                                    Intent inChangePass = new Intent(TeacherSplashScreen.this, OTPCallNumberScreen.class);
                                    inChangePass.putExtra("Type", "New");
                                    inChangePass.putExtra("note_message", click_here);
                                    startActivity(inChangePass);
                                } else {
                                    goToSignInScreen(Message);
                                }
                            } else {
                                goToSignInScreen(Message);
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void loginAPInew() {
        validateUser();
    }

    private void showPlayStoreAlert(String versionAlertTitle, String versionAlertContent, final String PlaystoreMarketId, final String PlayStoreLink) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherSplashScreen.this);
        AlertDialog alertDialog;

        builder.setCancelable(false);
        builder.setTitle(versionAlertTitle);
        builder.setMessage(versionAlertContent);
        builder.setIcon(R.drawable.teacher_ic_voice_snap);

        builder.setNegativeButton(R.string.now, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                TeacherUtil_SharedPreference.putAppTermsAndConditions(TeacherSplashScreen.this, "");
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(PlaystoreMarketId + appPackageName));
                    startActivity(intent);

                } catch (android.content.ActivityNotFoundException anfe) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(Uri.parse(PlayStoreLink + appPackageName));
                    startActivity(intent);
                }
            }
        });

        if (!bForceUpdate) {
            builder.setPositiveButton(R.string.later, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    checkAutoLoginAndSDcardPermission();
                }
            });
        }

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void checkAutoLoginAndSDcardPermission() {
        // if (isWriteExternalPermissionGranted()) {

        if (TeacherUtil_SharedPreference.getAutoLoginStatusFromSP(TeacherSplashScreen.this)) {
            loginAPInew();

        } else {
            String value = TeacherUtil_SharedPreference.getInstall(TeacherSplashScreen.this);
            String otpValue = TeacherUtil_SharedPreference.getOTPNum(TeacherSplashScreen.this);
            String mobilescreen = TeacherUtil_SharedPreference.getMobileNumberScreen(TeacherSplashScreen.this);

            if (mobilescreen.equals("1")) {
                openMobileNumbeScreen();
            } else if (otpValue.equals("1")) {
                openMobileNumbeScreen();
            } else if (value.equals("1")) {
                openSingInScreen();
            } else {
                openMobileNumbeScreen();
            }

        }
        //  }


    }
}
