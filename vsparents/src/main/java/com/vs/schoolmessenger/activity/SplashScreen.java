package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CountryList;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashScreen extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;

    ArrayList<Profiles> arrChildList = new ArrayList<>();
    ArrayList<CountryList> arrCountryList = new ArrayList<>();
    ArrayList<String> countryNameList = new ArrayList<>();
    boolean bForceUpdate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        checkNetworkAndLoginCredentials();
    }
    public void onBackPressed() {
//        moveTaskToBack(true);
    }

    private void checkNetworkAndLoginCredentials() {
        if (Util_Common.isNetworkAvailable(SplashScreen.this)) {
            int SPLASH_TIME_OUT = 2000;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String strBaseURL = Util_SharedPreference.getBaseUrlFromSP(SplashScreen.this);
                    if (!strBaseURL.equals("")) {
                        Log.d("BASE URL", strBaseURL);
                        if (strBaseURL.contains("V4")) {
                            checkAppUpdateAPI();
                            Log.d("strBaseURL", "strBaseURL.containsV4");
                        } else {
                            Util_SharedPreference.clearChildSharedPreference(SplashScreen.this);
                            countryListAPI();
                            Log.d("strBaseURL", "notcontainstrBaseURL.containsV4");
                        }
//                        checkAppUpdateAPI();
//                        checkAutoLoginAndSDcardPermission();
                    } else {
                        countryListAPI();
                    }
                }
            }, SPLASH_TIME_OUT);
        } else {
            showAlert("Connectivity", "Check your internet Connection");
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashScreen.this);
        AlertDialog alertDialog;

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(R.drawable.ic_close);

        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
//				showToast("Enable Internet Connection");
                finish();
            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
                return true;
            } else {

                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
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
                    Log.v("SDCard_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission

                    if (Util_SharedPreference.getAutoLoginStatusFromSP(SplashScreen.this)) {

                    } else {
                        startActivity(new Intent(SplashScreen.this, SignInScreen.class));
                        finish();
                    }
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                    showToast("Please enable Storage permission ");
                    isWriteExternalPermissionGranted();
                }
                return;
            }
        }
    }

    private void countryListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

//        MessengerApiClient.BASE_URL = "http://106.51.127.215:8089/ERP/";
//        MessengerApiClient.BASE_URL = "http://45.113.138.248/apk/AppService/";

        TeacherSchoolsApiClient.BASE_URL = "http://vs3.voicesnapforschools.com/api/ParentsApiV4/";
//        String strBase = "http://country.voicesnapforschools.com/apk/";
        TeacherSchoolsApiClient.changeApiBaseUrl(TeacherSchoolsApiClient.BASE_URL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetCountryList(getString(R.string.app_id));
        Call<JsonArray> call = apiService.GetCountryListnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("GetCountry:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetCountry:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        if (!(jsonObject.getString("CountryID")).equals("")) {
                            CountryList countryList;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                countryList = new CountryList(jsonObject.getString("CountryID"), jsonObject.getString("CountryName")
                                        , jsonObject.getString("MobileNumberLength"), jsonObject.getString("CountryCode"), jsonObject.getString("BaseUrl"));
                                arrCountryList.add(countryList);
                                countryNameList.add(countryList.getStrCountyName());
                            }

                            showCountryListBox();

                        } else {
                            showToast("Country Info Not loaded. try again");
                            finish();
                        }
                    } else {
                        showToast("Server Response Failed. Try again");
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("GetCountry:Exception", e.getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                finish();
                Log.d("GetCountry:Failure", t.toString());
            }
        });
    }

    private void showCountryListBox() {
        String[] countriesArray = countryNameList.toArray(new String[countryNameList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
        AlertDialog alertDialog;
        builder.setTitle("Choose Country");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                CountryList item = arrCountryList.get(selectedPosition);
                Util_SharedPreference.putCountryInforToSP(SplashScreen.this, item.getStrCountyID(),
                        item.getStrCountyName(), item.getStrCountyMobileLength(), item.getStrCountyCode(),
                        item.getStrBaseURL());

//                        MessengerApiClient.BASE_URL = item.getStrBaseURL();
                TeacherSchoolsApiClient.changeApiBaseUrl(item.getStrBaseURL());

                String version = getString(R.string.app_version_id);
                if (!("16".equals(version))) {
                    Util_SharedPreference.putautoupdateToSP(SplashScreen.this, version);
                    String update = Util_SharedPreference.getupdateversionFromSP(SplashScreen.this);
                    Log.d("update", update);
                    checkAppUpdateAPI();
                } else {
                    startActivity(new Intent(SplashScreen.this, SignInScreen.class));
                }

            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }


    private void checkAppUpdateAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

//        MessengerApiClient.BASE_URL = Util_SharedPreference.getBaseUrlFromSP(SplashScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_VersionChecknew(getString(R.string.app_version_id), getString(R.string.app_id), "Android");
        Call<JsonArray> call = apiService.VersionCheck(jsonReqArray);
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
                        String strUpdateAvailable = jsonObject.getString("UpdateAvailable");
                        String strForceUpdate = jsonObject.getString("ForceUpdate");

                        if (strUpdateAvailable.equals("1")) {
                            bForceUpdate = strForceUpdate.equals("1");

                            if (bForceUpdate) {
                                Util_SharedPreference.clearCountrySharedPreference(SplashScreen.this);
                                Util_SharedPreference.clearChildSharedPreference(SplashScreen.this);
                            }
                            showPlayStoreAlert("Update Available.!", "Latest Version of Parents App Available in the store.");
                        } else if (strUpdateAvailable.equals("0")) {
//                            checkAutoLoginAndSDcardPermission();
                            String update = Util_SharedPreference.getupdateversionFromSP(SplashScreen.this);
                            String version = getString(R.string.app_version_id);

                            Log.d("version1", version + "equals" + update);
//                            if(isautoupdate) {
                            if (update.equals("")) {
                                Util_SharedPreference.putautoupdateToSP(SplashScreen.this, version);
                                countryListAPI();

                            } else if (!(update.equals(version))) {
                                Util_SharedPreference.putautoupdateToSP(SplashScreen.this, version);
                                countryListAPI();
                            } else {
//                                isautoupdate=true;
                                checkAutoLoginAndSDcardPermission();
                            }
                        } else {
                            showToast(strForceUpdate);
                        }
                    } else {
                        showToast("Server Response Failed. Try again");
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                finish();
                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }

    private void showPlayStoreAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SplashScreen.this);
        AlertDialog alertDialog;

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(R.drawable.ic_voice_snap);

        builder.setNegativeButton("Now", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        if (!bForceUpdate) {
            builder.setPositiveButton("Later", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    checkAutoLoginAndSDcardPermission();
                }
            });
        }
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void checkAutoLoginAndSDcardPermission() {
        if (isWriteExternalPermissionGranted()) {
            if (Util_SharedPreference.getAutoLoginStatusFromSP(SplashScreen.this)) {

            } else {
                startActivity(new Intent(SplashScreen.this, SignInScreen.class));
                finish();
            }
        }
//        else {
////                        showAlert("Contact Permission", "Please enable Lets Huddle to access Contacts from your mobile.");
//            showToast("Please enable School Messenger to access files from your mobile.");
//        }
    }


}
