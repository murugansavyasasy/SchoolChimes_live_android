package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.BuildConfig;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.ChildMenuAdapter;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.ParentMenuModel;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    static ArrayList<String> isParentMenuNames = new ArrayList<>();
    String strChildName, child_ID;
    public static Profiles childItem = new Profiles();
    TextView tv_schoolname, tvSchoolAddress;
    ImageView aHome_nivSchoolLogo;
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();
    String BookLink;
    RelativeLayout rytParent;
    TextView scrollingtext;
    LinearLayout lnrScroll;
    GridView idGridMenus;
    String isBookEnabled;
    ArrayList<ParentMenuModel> menuList = new ArrayList<ParentMenuModel>();
    String count;

    Slider slider;
    ImageView adImage;

    public Handler handler = new Handler();
    public static int i = 0;
    private PopupWindow popupWindow;

    int Contact_Count = 0;
    int exist_Count = 0;

    String contact_alert_title = "", contact_alert_Content = "", contact_display_name = "", contact_numbers = "",contact_button = "";
    String[] contacts;
    String Display_Name = "";

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private PopupWindow SettingspopupWindow;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);

        String home = getIntent().getExtras().getString("HomeScreen", "");
        if (!home.equals("1")) {
            childItem = (Profiles) getIntent().getSerializableExtra("Profiles");
            TeacherUtil_SharedPreference.putChildItems(HomeActivity.this, childItem, "childItem");
        }

        childItem = TeacherUtil_SharedPreference.getChildItems(HomeActivity.this, "childItem");
        Util_SharedPreference.putSelecedChildInfoToSP(HomeActivity.this, childItem.getChildID(), childItem.getChildName(), childItem.getSchoolID(),
                childItem.getSchoolName(), childItem.getSchoolAddress(), childItem.getSchoolThumbnailImgUrl(), childItem.getStandard(), childItem.getSection());
        strChildName = childItem.getChildName();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText(strChildName);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        Slider.init(new PicassoImageLoadingService(HomeActivity.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);

        idGridMenus = (GridView) findViewById(R.id.idGridMenus);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        scrollingtext = (TextView) findViewById(R.id.scrollingtext);
        lnrScroll = (LinearLayout) findViewById(R.id.lnrScroll);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);


        aHome_nivSchoolLogo = (ImageView) findViewById(R.id.aHome_nivSchoolLogo);
        String url = childItem.getSchoolThumbnailImgUrl();

        try {
            if (!url.equals("")) {
                Glide.with(this).load(url).centerCrop().into(aHome_nivSchoolLogo);
            }
        } catch (Exception e) {
            showToast(e.getMessage());
        }


        isBookEnabled = childItem.getBookEnable();
        BookLink = childItem.getBookLink();
        child_ID = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
        tv_schoolname = (TextView) findViewById(R.id.aHome_tvSchoolName);
        tv_schoolname.setText(Util_SharedPreference.getSchoolnameFromSP(HomeActivity.this));
        tvSchoolAddress = (TextView) findViewById(R.id.aHome_tvSchoolAddress);
        tvSchoolAddress.setText(Util_SharedPreference.getSchooladdressFromSP(HomeActivity.this));

    }

    private void getContactPermission() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            checkIfContactsExist();
            // Android version is lesser than 6.0 or the permission is already granted.
          Log.d("Granded","Granded");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                getContactPermission();
            } else {
                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getReadContactsPermission(HomeActivity.this);

               if(isPermissionDeniedCount.equals("2")){
                   settingsContactPermission();
               }

               else if(isPermissionDeniedCount.equals("1")) {
                    TeacherUtil_SharedPreference.putReadContactsPermission(HomeActivity.this, "2");
                }
                else {
                    TeacherUtil_SharedPreference.putReadContactsPermission(HomeActivity.this, "1");
                }
            }
        }
    }

    private void settingsContactPermission() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.settings_app_permission, null);
        SettingspopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        SettingspopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                SettingspopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView lblNotNow = (TextView) layout.findViewById(R.id.lblNotNow);
        TextView lblSettings = (TextView) layout.findViewById(R.id.lblSettings);
        lblNotNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingspopupWindow.dismiss();
            }
        });

        lblSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SettingspopupWindow.dismiss();
                    startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
                }
                catch (Exception e){
                    Log.d("saveexception",e.toString());
                }
            }
        });
    }

    private void getMenuDetails() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(HomeActivity.this);
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(HomeActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(HomeActivity.this, "child_list");
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", "parent");
        jsonObject.addProperty("id", childItem.getChildID());
        jsonObject.addProperty("schoolid", childItem.getSchoolID());
        jsonArray.add(jsonObject);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonObjectlanguage = new JsonObject();
        jsonObjectlanguage.add("MemberData", jsonArray);
        jsonObjectlanguage.addProperty("LanguageId", "1");
        jsonObjectlanguage.addProperty("CountryID", countryId);

        Log.d("Request", jsonObjectlanguage.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ChangeLanguage(jsonObjectlanguage);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("GetMenuDetails:code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetMenuDetails:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        if (status.equals("1")) {
                            String menu_name = jsonObject.getString("menu_name");

                            String[] name = menu_name.split(",");
                            isParentMenuNames.clear();
                            for (String itemtemp : name) {
                                isParentMenuNames.add(itemtemp);
                            }

                            contact_alert_title = jsonObject.getString("contact_alert_title");
                            contact_alert_Content = jsonObject.getString("contact_alert_content");
                            contact_display_name = jsonObject.getString("contact_display_name");
                            contact_numbers = jsonObject.getString("contact_numbers");
                            contact_button = jsonObject.getString("contact_button_content");


                            if(!contact_numbers.equals("")) {
                                contacts = contact_numbers.split(",");
                                getContactPermission();
                            }

                            String alert_message = jsonObject.getString("alert_message");
                            if (!alert_message.equals("")) {
                                lnrScroll.setVisibility(View.VISIBLE);
                                scrollingtext.setText(alert_message);
                            } else {
                                lnrScroll.setVisibility(View.GONE);
                            }

                            UnreadCount();
                        }
                    }

                } catch (Exception e) {
                    Log.e("menu:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void checkIfContactsExist() {

        exist_Count = 0;
        ContentResolver contentResolver = HomeActivity.this.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[] { ContactsContract.PhoneLookup._ID };
        String selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?";
        String[] selectionArguments = { contact_display_name };
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArguments, null);
        exist_Count = cursor.getCount();
        if (cursor != null) {
            while (cursor.moveToNext()) {
            }
        }

        Contact_Count = 0;
        for (int i = 0; i < contacts.length; i++) {
            String number = contacts[i];
            if (number != null) {
                Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
                String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME};
                Cursor cur = HomeActivity.this.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
                try {
                    if (cur.moveToFirst()) {
                        Contact_Count = Contact_Count + 1;
                        int indexName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        Display_Name = cur.getString(indexName);
                        Log.d("Display_Name", Display_Name);
                            if (!Display_Name.equals(contact_display_name)) {
                                Contact_Count = Contact_Count - 1;
                            }
                    }
                } finally {
                    if (cur != null)
                        cur.close();
                }
            }
        }
        if (contacts.length != Contact_Count ) {
            if(exist_Count == 0 || exist_Count < contacts.length) {
                contactSaveContent();
            }
        }
    }

    private void contactSaveContent() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.save_contact_alert, null);
        popupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        TextView btnSaveContact = (TextView) layout.findViewById(R.id.btnSaveContact);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        TextView lblHeader = (TextView) layout.findViewById(R.id.lblHeader);
        TextView lblContent = (TextView) layout.findViewById(R.id.lblContent);
        lblHeader.setText(contact_alert_title);
        lblContent.setText(contact_alert_Content);
        btnSaveContact.setText(contact_button);

        btnSaveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                saveContacts();
            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }


    private void saveContacts() {
        try {
            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.conatct_school);
            // Bitmap bit = Bitmap.createScaledBitmap(b, 100, 100, false);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
            intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
            ArrayList<ContentValues> data = new ArrayList<ContentValues>();
            for (int i = 0; i < contacts.length; i++) {
                ContentValues row = new ContentValues();
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                row.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contacts[i]);
                row.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
                data.add(row);
            }

            for (int i = 0; i < contacts.length; i++) {
                ContentValues row = new ContentValues();
                row.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
                row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray);
                data.add(row);
            }
            intent.putExtra(ContactsContract.Intents.Insert.NAME, contact_display_name);
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
            startActivityForResult(intent, 100);

        }
        catch (Exception e){
            Log.d("saveContactError",e.toString());
        }

    }

    public void deleteCache(Activity activity) {
        try {
            File dir = activity.getCacheDir();
            if (deleteDir(dir)) {
                Toast.makeText(activity, "Cache cleared", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Cache not cleared", Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:
                Intent homescreen = new Intent(HomeActivity.this, ChildrenScreen.class);
                homescreen.putExtra("HomeScreen", "1");
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:
                Intent faq = new Intent(HomeActivity.this, FAQScreen.class);
                startActivity(faq);
                break;

            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(HomeActivity.this, "change");
                startActivity(new Intent(HomeActivity.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(HomeActivity.this, v, "1");

                break;
            default:
                break;
        }
    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(HomeActivity.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];
        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
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
        TeacherUtil_SharedPreference.putLanguageType(HomeActivity.this, code);
        languageChangeApi(id, code);
    }

    private void languageChangeApi(String id, final String lang) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(HomeActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(HomeActivity.this, "child_list");
        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(HomeActivity.this);
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
                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        LanguageIDAndNames.putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"), HomeActivity.this);
                        LanguageIDAndNames.putStaffIdstoSharedPref(jsonObject.getString("isStaffID"), HomeActivity.this);
                        LanguageIDAndNames.putAdminIdstoSharedPref(jsonObject.getString("isAdminID"), HomeActivity.this);
                        LanguageIDAndNames.putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"), HomeActivity.this);
                        LanguageIDAndNames.putParentIdstoSharedPref(jsonObject.getString("isParentID"), HomeActivity.this);
                        LanguageIDAndNames.putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"), HomeActivity.this);
                        LanguageIDAndNames.putStaffNamestoSharedPref(jsonObject.getString("isStaff"), HomeActivity.this);
                        LanguageIDAndNames.putAdminNamestoSharedPref(jsonObject.getString("isAdmin"), HomeActivity.this);
                        LanguageIDAndNames.putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"), HomeActivity.this);
                        LanguageIDAndNames.putParentNamestoSharedPref(jsonObject.getString("isParent"), HomeActivity.this);

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
            }
        });
    }

    public void showLogoutAlert(final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle("Logout");
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                TeacherUtil_SharedPreference.putInstall(activity, "1");
                TeacherUtil_SharedPreference.putOTPNum(activity, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(activity, "");
                TeacherUtil_SharedPreference.clearStaffSharedPreference(activity);
                activity.startActivity(new Intent(activity, TeacherSignInScreen.class));
                activity.finish();
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
        positiveButton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
        Button negativebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(activity.getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(HomeActivity.this, adImage, slider, "Dashboard");
        getMenuDetails();
    }

    private void UnreadCount() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(HomeActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(HomeActivity.this);
        String strChildID = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(HomeActivity.this);
        Log.d("ChangePass:mob-Old-New", mobNumber + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_overallUnreadCount(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetOverallUnreadCount(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("overallUnreadCount:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("overallUnreadCount:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String emgvoicecount = jsonObject.getString("EMERGENCYVOICE");
                        String voicemsgcount = jsonObject.getString("VOICE");
                        String textmessagecount = jsonObject.getString("SMS");
                        String photoscount = jsonObject.getString("IMAGE");
                        String documentcount = jsonObject.getString("PDF");
                        String noticeboard = jsonObject.getString("NOTICEBOARD");
                        String examtest = jsonObject.getString("EXAM");
                        String schoolevent = jsonObject.getString("EVENTS");
                        String homework = jsonObject.getString("HOMEWORK");
                        String assignment = jsonObject.getString("ASSIGNMENT");
                        String video = jsonObject.getString("VIDEO");
                        String onlineClass = jsonObject.getString("ONLINECLASS");
                        String quiz = jsonObject.getString("QUIZEXAM");
                        String lsrw = jsonObject.getString("LSRW");
                        String exam_marks = jsonObject.getString("EXAMMARKS");

                        menuList.clear();

                        for (int i = 0; i < isParentMenuNames.size(); i++) {
                            String name = isParentMenuNames.get(i);
                            String substring = name.substring(Math.max(name.length() - 2, 0));
                            String substring1 = name.substring(Math.max(name.length() - 3, 0));

                            if (substring.equals("_0")) {
                                count = emgvoicecount;
                            } else if (substring.equals("_1")) {
                                count = voicemsgcount;

                            } else if (substring.equals("_2")) {
                                count = textmessagecount;

                            } else if (substring.equals("_3")) {
                                count = homework;

                            } else if (substring.equals("_4")) {
                                count = examtest;

                            } else if (substring.equals("_5")) {
                                count = exam_marks;

                            } else if (substring.equals("_6")) {
                                count = documentcount;

                            } else if (substring.equals("_7")) {
                                count = noticeboard;

                            } else if (substring.equals("_8")) {
                                count = schoolevent;

                            } else if (substring.equals("_9")) {
                                count = "0";

                            } else if (substring1.equals("_10")) {
                                count = "0";

                            } else if (substring1.equals("_11")) {
                                count = "0";

                            } else if (substring1.equals("_12")) {
                                count = photoscount;

                            } else if (substring1.equals("_13")) {
                                count = "0";

                            } else if (substring1.equals("_14")) {
                                count = "0";

                            } else if (substring1.equals("_15")) {
                                count = "0";

                            } else if (substring1.equals("_16")) {
                                count = "0";

                            } else if (substring1.equals("_17")) {
                                count = "0";

                            } else if (substring1.equals("_18")) {
                                count = assignment;

                            } else if (substring1.equals("_19")) {
                                count = video;

                            } else if (substring1.equals("_20")) {
                                count = onlineClass;

                            } else if (substring1.equals("_21")) {
                                count = quiz;

                            } else if (substring1.equals("_22")) {
                                count = lsrw;

                            } else if (substring1.equals("_23")) {
                                count = "0";
                            }


                            if (!substring1.equals("_15")) {
                                menuList.add(new ParentMenuModel(name, count));
                            } else {
                                if (isBookEnabled.equals("1")) {
                                    menuList.add(new ParentMenuModel(name, count));
                                }
                            }

                        }
                        ChildMenuAdapter myAdapter = new ChildMenuAdapter(HomeActivity.this, R.layout.child_menu_item, menuList, BookLink,rytParent);
                        idGridMenus.setSelection(TeacherUtil_Common.scroll_to_position);
                        idGridMenus.setAdapter(myAdapter);



                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Unreadcount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
