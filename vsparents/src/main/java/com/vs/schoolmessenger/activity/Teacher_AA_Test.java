package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.BuildConfig;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.FeePendingAlertAdapter;
import com.vs.schoolmessenger.adapter.SchoolMenuAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.fcmservices.Config;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;

public class Teacher_AA_Test extends AppCompatActivity implements View.OnClickListener {

    GridView idGridMenus;
    private PopupWindow popupWindow;
    FeePendingAlertAdapter contentadapter;
    ImageView nivSchoolLogo;
    TextView tvLoggedInAs, tvSchoolName, tvSchoolAddress;
    Button btnChange;
    private PopupWindow pHelpWindow;
    String IDs = "";
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    ArrayList<String> loginTypeList;
    ArrayList<String> isPrincipalMenuNames = new ArrayList<>();
    public static TeacherSchoolsModel schoolmodel;
    int PERMISSION_ALL = 1;
    BottomNavigationView bottomNavigationView;
    LinearLayout lnrBottom;
    public static ArrayList<String> myArray = new ArrayList<>();
    public static ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    RelativeLayout rytParent;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private static final String TAG = Teacher_AA_Test.class.getSimpleName();
    TextView scrollingtext;
    LinearLayout lnrScroll;
    String Role="";
    String BookLink;
    Boolean BookEnabled = false;
    int Contact_Count = 0;
    int exist_Count = 0;

    String contact_alert_title="",contact_alert_Content="",contact_display_name="",contact_numbers="",contact_button = "";
    String[] contacts ;
    String Display_Name = "";
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private PopupWindow SettingspopupWindow;
    private PopupWindow ContactpopupWindow;



    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_aaa_home);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        String lang = TeacherUtil_SharedPreference.getLanguageType(Teacher_AA_Test.this);
        changeLanguageInitial(lang);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Log.d("MESSAGE", message);
                }
            }
        };
        displayFirebaseRegId();
        Role = TeacherUtil_SharedPreference.getRole(Teacher_AA_Test.this);
        if (Role.equals("p1")) {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        } else if (Role.equals("p2")) {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        } else if (Role.equals("p3")) {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        }
        else if (Role.equals("p4")) {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        }
        else if (Role.equals("p5")) {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.GONE);
        }
        else {
            ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setVisibility(View.VISIBLE);
        }

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.imgVoiceSnap)).setVisibility(View.VISIBLE);
        String home = getIntent().getExtras().getString("Homescreen", "");
        if (!home.equals("1")) {
            schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
            TeacherUtil_SharedPreference.PutChildrenScreenschoolmodel(Teacher_AA_Test.this, schoolmodel, "schoolModel");
            schoolmodel = TeacherUtil_SharedPreference.getChildrenScreenSchoolModel(Teacher_AA_Test.this, "schoolModel");
        }
        else {
            schoolmodel = TeacherUtil_SharedPreference.getChildrenScreenSchoolModel(Teacher_AA_Test.this, "schoolModel");
        }
        if (!home.equals("1")) {
            myArray = (ArrayList<String>) getIntent().getSerializableExtra("schoollist");
            TeacherUtil_SharedPreference.PutChildrenScreenMyArray(Teacher_AA_Test.this, myArray, "myarray");
            myArray = TeacherUtil_SharedPreference.getChildrenScreenMyArray(Teacher_AA_Test.this, "myarray");
        } else {
            myArray = TeacherUtil_SharedPreference.getChildrenScreenMyArray(Teacher_AA_Test.this, "myarray");

        }
        if (!home.equals("1")) {
            schools_list = (ArrayList<TeacherSchoolsModel>) getIntent().getSerializableExtra("list");
            TeacherUtil_SharedPreference.putChildrenScreenSchool_list(Teacher_AA_Test.this, schools_list, "schools_list");
            schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(Teacher_AA_Test.this, "schools_list");
        } else {
            schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(Teacher_AA_Test.this, "schools_list");

        }


        idGridMenus = (GridView) findViewById(R.id.idGridMenus);
        scrollingtext = (TextView) findViewById(R.id.scrollingtext);
        lnrScroll = (LinearLayout) findViewById(R.id.lnrScroll);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        tvLoggedInAs = (TextView) findViewById(R.id.aHome_tvLoggedInAs);
        tvLoggedInAs.setText(TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this));

        tvSchoolName = (TextView) findViewById(R.id.aHome_tvSchoolName);
        tvSchoolName.setText(TeacherUtil_SharedPreference.getSchoolNameFromSP(Teacher_AA_Test.this));

        tvSchoolAddress = (TextView) findViewById(R.id.aHome_tvSchoolAddress);
        tvSchoolAddress.setText(TeacherUtil_SharedPreference.getShSchoolAddressFromSP(Teacher_AA_Test.this));
        CardView aHome_cvTop = (CardView) findViewById(R.id.aHome_cvTop);
        nivSchoolLogo = (ImageView) findViewById(R.id.aHome_nivSchoolLogo);

        String display_name = TeacherUtil_SharedPreference.getDisplayRoleMessage(Teacher_AA_Test.this);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.logged);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("As "+display_name);

        if (Role.equals("p3")) {
            Bundle extras = getIntent().getExtras();
            String schoolname = extras.getString("schoolname", "");
            TeacherUtil_SharedPreference.putChildrenScreenSchoolName(Teacher_AA_Test.this, schoolname);
            schoolname = TeacherUtil_SharedPreference.getChildrenScreenSchoolName(Teacher_AA_Test.this);

            String schooladdress = extras.getString("schooladdress", "");
            TeacherUtil_SharedPreference.putChildrenScreenSchoolAddress(Teacher_AA_Test.this, schooladdress);
            schooladdress = TeacherUtil_SharedPreference.getChildrenScreenSchoolAddress(Teacher_AA_Test.this);
            String url = TeacherUtil_SharedPreference.getSchoolLogo(Teacher_AA_Test.this);
            try {
                if (!url.equals("")) {
                    Glide.with(this).load(url).centerCrop().into(nivSchoolLogo);
                }
            } catch (Exception e) {
                showToast(e.getMessage());
            }

            aHome_cvTop.setVisibility(View.VISIBLE);
            tvSchoolName.setText(schoolname);
            tvSchoolAddress.setText(schooladdress);
        }

        if (Role.equals("p2")) {
            if (schools_list.size() == 1) {
                final TeacherSchoolsModel name = schools_list.get(0);
                String school = name.getStrSchoolName();
                String schoolAddress = name.getStrSchoolAddress();
                aHome_cvTop.setVisibility(View.VISIBLE);
                tvSchoolName.setText(school);
                tvSchoolAddress.setText(schoolAddress);
                String url = TeacherUtil_SharedPreference.getSchoolLogo(Teacher_AA_Test.this);
                try {
                    if (!url.equals("")) {
                        Glide.with(this).load(url).centerCrop().into(nivSchoolLogo);
                    }
                } catch (Exception e) {
                    showToast(e.getMessage());
                }
            }
        }
        btnChange = (Button) findViewById(R.id.aHome_btnChange);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLoginType();
            }
        });
        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);
        lnrBottom = (LinearLayout) findViewById(R.id.lnrBottom);
        String bottom_menu = getIntent().getExtras().getString("bottom_menu", "");
        if (bottom_menu.equals("1")) {
            rytHome.setVisibility(View.VISIBLE);
        }

        getMenuDetails();

        String alertMessage =TeacherUtil_SharedPreference.getLoginMessage(Teacher_AA_Test.this);
        if(!alertMessage.equals("Success")) {
            if (Role.equals("p1") || Role.equals("p2")) {
                showPaymentPendingAlert();
            }
        }
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
                String isPermissionDeniedCount = TeacherUtil_SharedPreference.getReadContactsPermission(Teacher_AA_Test.this);

                if(isPermissionDeniedCount.equals("2")){
                    settingsContactPermission();
                }

                else if(isPermissionDeniedCount.equals("1")) {
                    TeacherUtil_SharedPreference.putReadContactsPermission(Teacher_AA_Test.this, "2");
                }
                else {
                    TeacherUtil_SharedPreference.putReadContactsPermission(Teacher_AA_Test.this, "1");

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
                SettingspopupWindow.dismiss();
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

            }
        });
    }

    private void getMenuDetails() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(Teacher_AA_Test.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(Teacher_AA_Test.this);
        JsonArray jsonArray = new JsonArray();

        if (schools_list != null) {
            for (int i = 0; i < schools_list.size(); i++) {
                final TeacherSchoolsModel model = schools_list.get(i);
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("type", "staff");
                jsonObject.addProperty("id", model.getStrStaffID());
                jsonObject.addProperty("schoolid", model.getStrSchoolID());
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

                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        if(status.equals("1")) {
                            String menu_name = jsonObject.getString("menu_name");
                            String menu_id = jsonObject.getString("menu_id");

                            String[] name = menu_name.split(",");
                            isPrincipalMenuNames.clear();
                            for (String itemtemp : name) {
                                isPrincipalMenuNames.add(itemtemp);
                            }

                          //  isPrincipalMenuNames.add("Lesson Plan_30");

                            String alert_message = jsonObject.getString("alert_message");
                            if(!alert_message.equals("")){
                                lnrScroll.setVisibility(View.VISIBLE);
                                scrollingtext.setText(alert_message);
                            }
                            else {
                                lnrScroll.setVisibility(View.GONE);
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

                        }

                        selectedLoginType();
                        if(!BookEnabled){
                            for (int i=0;i<isPrincipalMenuNames.size();i++){
                                String name = isPrincipalMenuNames.get(i);
                                String substring1 = name.substring(Math.max(name.length() - 3, 0));

                                if(substring1.equals("_18")){
                                    isPrincipalMenuNames.remove(name);
                                }
                            }
                        }
                        setupBottomBar();
                        SchoolMenuAdapter myAdapter=new SchoolMenuAdapter(Teacher_AA_Test.this,R.layout.school_menu_card_item,isPrincipalMenuNames,BookLink,rytParent);
                        idGridMenus.setAdapter(myAdapter);
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

    private void checkIfContactsExist() {

        exist_Count = 0;
        ContentResolver contentResolver = Teacher_AA_Test.this.getContentResolver();
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
                String[] mPhoneNumberProjection = {ContactsContract.PhoneLookup.DISPLAY_NAME };
                Cursor cur = Teacher_AA_Test.this.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
                try {
                    if (cur.moveToFirst()) {
                        Contact_Count = Contact_Count +1;

                        int indexName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        Display_Name = cur.getString(indexName);
                        Log.d("Display_Name", Display_Name);

                        if(!Display_Name.equals(contact_display_name)){
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
       PopupWindow ContactpopupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        ContactpopupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                ContactpopupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
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
                try {
                    ContactpopupWindow.dismiss();
                    saveContacts();
                }
                catch (Exception e){
                    Log.d("failure_popup_error",e.toString());
                }

            }
        });

        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactpopupWindow.dismiss();
            }
        });
    }

    private void saveContacts() {

            Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.conatct_school);
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

    private void selectedLoginType() {
        for (int i = 0; i < schools_list.size(); i++) {
            final TeacherSchoolsModel name = schools_list.get(i);
            String BookEnable = name.getBookEnable();
            BookLink = name.getOnlineLink();
            if (BookEnable.equals("1")) {
                BookEnabled = true;
            }
        }
    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", "");
        Log.e(TAG, "Firebase reg id: " + regId);
        updateDeviceTokenAPI(regId);
    }

    private void updateDeviceTokenAPI(String strDeviceToken) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(Teacher_AA_Test.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(Teacher_AA_Test.this);
        Log.d("UpdateToken:mob-Token", mobNumber + " - " + strDeviceToken);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_DeviceToken(mobNumber, strDeviceToken, "Android");
        Call<JsonArray> call = apiService.DeviceTokennew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("UpdateToken:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("UpdateToken:Res", response.body().toString());
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");
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

    private void showPaymentPendingAlert() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.payment_pending_dialog, null);
        popupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindow.setContentView(layout);
        rytParent.post(new Runnable() {
            public void run() {
                popupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0);
            }
        });

        String loginMessage = TeacherUtil_SharedPreference.getLoginMessage(Teacher_AA_Test.this);
        RecyclerView ryccontent = (RecyclerView) layout.findViewById(R.id.rycContent);
        Button btnAgree = (Button) layout.findViewById(R.id.btnAgree);
        TextView lblHeader = (TextView) layout.findViewById(R.id.lblHeader);
        lblHeader.setText(loginMessage);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ryccontent.setLayoutManager(layoutManager);
        ryccontent.setItemAnimator(new DefaultItemAnimator());
        contentadapter = new FeePendingAlertAdapter(schools_list, Teacher_AA_Test.this);
        ryccontent.setAdapter(contentadapter);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Boolean is_staff = TeacherUtil_SharedPreference.getIsStaff(Teacher_AA_Test.this);
        Boolean is_parent = TeacherUtil_SharedPreference.getIsParent(Teacher_AA_Test.this);
        if (is_staff && is_parent) {
            finish();
        }
        else {
            ExitAlert();
        }
    }
    private void ExitAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.exit_app);
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
        builder.setNegativeButton(R.string.teacher_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        TeacherUtil_SharedPreference.putCurrentDate(Teacher_AA_Test.this, date);

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
    private void selectHomeTab() {
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }

    private void changeLanguageInitial(String lang) {
        LocaleHelper local = new LocaleHelper();
        local.setLocale(Teacher_AA_Test.this, lang);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_homeHelp:
                setupHelpPopUp();
                pHelpWindow.showAtLocation(rytParent, Gravity.NO_GRAVITY, 0, 0);
                return true;

            case R.id.menu_homeChangePassword:
                Util_SharedPreference.putForget(Teacher_AA_Test.this, "change");
                startActivity(new Intent(Teacher_AA_Test.this, TeacherChangePassword.class));
                return true;

            case R.id.menu_homeLogout:
                TeacherUtil_SharedPreference.putInstall(Teacher_AA_Test.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(Teacher_AA_Test.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(Teacher_AA_Test.this, "");
                TeacherUtil_SharedPreference.clearStaffSharedPreference(Teacher_AA_Test.this);
                startActivity(new Intent(Teacher_AA_Test.this, TeacherSignInScreen.class));
                finish();
                return true;

            case R.id.menu_language_change:
                showLanguageListPopup();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(Teacher_AA_Test.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];
        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(Teacher_AA_Test.this);
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

    private void changeLanguage(String lang, String Id) {
        TeacherUtil_SharedPreference.putLanguageType(Teacher_AA_Test.this, lang);
    }
    public boolean onPrepareOptionsMenu(Menu menu) {
        Boolean is_staff = TeacherUtil_SharedPreference.getIsStaff(Teacher_AA_Test.this);
        Boolean is_parent = TeacherUtil_SharedPreference.getIsParent(Teacher_AA_Test.this);
        MenuItem register = menu.findItem(R.id.menu_homeOption);

        if (is_staff && is_parent) {
            register.setVisible(false);
        }
        else {
            register.setVisible(false);
        }
        return true;
    }

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }

    private void setupHelpPopUp() {
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_help_txt, null);
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

        TextView tvClear = (TextView) layout.findViewById(R.id.popupHelp_tvClear);
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etmsg.setText("");
            }
        });
    }
    private void showToast(String msg) {
        Toast.makeText(Teacher_AA_Test.this, msg, Toast.LENGTH_SHORT).show();
    }
    private void changeLoginType() {
        loginTypeList = new ArrayList<>();
        loginTypeList.add(LOGIN_TYPE_ADMIN);
        showLoginType();
    }

    private void showLoginType() {
        String[] LoginTypeArray = loginTypeList.toArray(new String[loginTypeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(Teacher_AA_Test.this);
        AlertDialog alertDialog;
        builder.setTitle(R.string.alert);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(LoginTypeArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                String strLoginType = loginTypeList.get(selectedPosition);
                TeacherUtil_SharedPreference.putLoggedInAsToSP(Teacher_AA_Test.this, strLoginType);
                tvLoggedInAs.setText(strLoginType);

            }
        });
        builder.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
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

    private void helpAPI(String msg) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();
        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(Teacher_AA_Test.this);
        Log.d("Help:Mob-Query", mobNumber + " - " + msg);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetHelp(mobNumber, msg);
        Call<JsonArray> call = apiService.GetHelp(jsonReqArray);
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
                            if (pHelpWindow.isShowing()) {
                                showToast(strMessage);
                                pHelpWindow.dismiss();
                                hideKeyBoard();
                            } else {
                                showToast(strMessage);
                            }
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Help:Exception", e.getMessage());
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
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHelp:
                Intent faq = new Intent(Teacher_AA_Test.this, FAQScreen.class);
                faq.putExtra("School", "School");
                startActivity(faq);
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(Teacher_AA_Test.this, "change");
                startActivity(new Intent(Teacher_AA_Test.this, TeacherChangePassword.class));
                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytLogout:
                Util_Common.popUpMenu(Teacher_AA_Test.this, v, "");
                break;
            case R.id.rytHome:
                Intent home = new Intent(Teacher_AA_Test.this, ChildrenScreen.class);
                home.putExtra("HomeScreen", "1");
                home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(home);
                finish();
                break;
            default:
                break;
        }
    }
}
