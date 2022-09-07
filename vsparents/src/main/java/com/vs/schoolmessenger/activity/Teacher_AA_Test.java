package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.FeePendingAlertAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.assignment.AssignmentActivity;
import com.vs.schoolmessenger.assignment.ContentAdapter;
import com.vs.schoolmessenger.assignment.VideoUpload;
import com.vs.schoolmessenger.fcmservices.Config;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Image;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ABSENTEES;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_CHAT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MESSAGESFROMMANAGEMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_SCHOOLSTRENGTH;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_PHOTOS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

public class Teacher_AA_Test extends AppCompatActivity implements View.OnClickListener {

    private PopupWindow popupWindow;
    FeePendingAlertAdapter contentadapter;

    ImageView nivSchoolLogo;
    ImageLoader imageLoader;

    TextView tvLoggedInAs, tvSchoolName, tvSchoolAddress;
    Button btnChange;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;

    private PopupWindow pHelpWindow;

    String IDs = "";
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    ArrayList<String> loginTypeList;

    ArrayList<Integer> isPrincipalMenuID = new ArrayList<>();
    ArrayList<String> isPrincipalMenuNames = new ArrayList<>();

    LinearLayout llEmergency, llVoiceParents, llTextParents, llNoticeBoard, llEvents, llPhotos;
    LinearLayout llAbsentees, llParticulars, llCallSmsUsage, llHomeworkVoice, llHomeworkText,
            llExam, llAttendance, llmanagemsg, llFeedBack, aHome_llLibrary, aHome_llCalls,
            aHome_llRequestMeeting, aHome_llVideoUpload, aHome_llSpecialOffer, aHom_llAssignment,
            aHom_llVideovimeo, aHom_llChat, aHom_llNewProducts, aHom_llMettingURL, aHom_llOnlineQuiz;

    TextView lblEmergency,
            lblNormalVoice,
            lblTextToParents,
            lblNoticeBoard,
            lblSchoolEvents,
            lblImageupload,
            lblAbsenteesimReport,
            lblSchoolStrength,
            lblcallsUsageCount,
            lbltextHomeWork,
            lblVoiceHomeWork,
            lblExamSchedule,
            lblAttedanceMarking,
            lblMessagesFromManagement,
            lblFeedBack,
            lblLibraryDetails,
            lblConferenceCall,
            lblOnlineTextBook,
            lblRequestMeeting,
            lblVideoUpload, lblSpecialOffer, lblAssignment, lblVideovimeo, lblChat, lblNewProduct, lblMeetingURL, lblOnlineQuiz;

    CardView aHome_CardLeaveRequests, aHome_RequestMeeting, aHome_Books;
    LinearLayout aHome_llLeaverequest;
    TextView lblLeaveRequest;

    String[] MenuNamesnames;
    GridLayout glMenu;

    View[] mAllMenu = new View[28];
    int[] iGroupHeadMenu = {0, 1, 2, 3, 17};
    int[] iPrincipalMenu = {0, 1, 2, 9, 10, 12, 11, 3, 4, 6, 7, 5, 16, 14, 17}; //,8
    int[] iTeacherMenu = {1, 2, 12, 9, 10, 11, 5, 13, 15, 17};
    int[] iAdminMenu = {3, 12, 17};

    TeacherSchoolsModel schoolmodel;
    int PERMISSION_ALL = 1;

    BottomNavigationView bottomNavigationView;

    LinearLayout lnrBottom;
    String BookLink;
    Boolean BookEnabled = false;
    ArrayList<String> myArray = new ArrayList<>();
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    RelativeLayout rytHome, rytLanguage, rytPassword, rytHelp, rytLogout;
    RelativeLayout rytParent;

    ImageView imgNew;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final String TAG = Teacher_AA_Test.class.getSimpleName();
    Button btnVideo;
    TextView scrollingtext;
    LinearLayout lnrScroll;
    String Role="";

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
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE};

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        imgNew = (ImageView) findViewById(R.id.imgNew);

        scrollingtext = (TextView) findViewById(R.id.scrollingtext);
        lnrScroll = (LinearLayout) findViewById(R.id.lnrScroll);
        scrollingtext.setSelected (true);


        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        tvLoggedInAs = (TextView) findViewById(R.id.aHome_tvLoggedInAs);
        tvLoggedInAs.setText(TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this));

        tvSchoolName = (TextView) findViewById(R.id.aHome_tvSchoolName);
        tvSchoolName.setText(TeacherUtil_SharedPreference.getSchoolNameFromSP(Teacher_AA_Test.this));

        tvSchoolAddress = (TextView) findViewById(R.id.aHome_tvSchoolAddress);
        tvSchoolAddress.setText(TeacherUtil_SharedPreference.getShSchoolAddressFromSP(Teacher_AA_Test.this));


        CardView aHome_cvTop = (CardView) findViewById(R.id.aHome_cvTop);
        aHome_Books = (CardView) findViewById(R.id.aHome_Books);
        LinearLayout aHome_llBooks = (LinearLayout) findViewById(R.id.aHome_llBooks);
        aHome_llBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browse = new Intent(Teacher_AA_Test.this, TextBookActivity.class);
                browse.putExtra("url", BookLink);
                startActivity(browse);
            }
        });

        nivSchoolLogo = (ImageView) findViewById(R.id.aHome_nivSchoolLogo);
        aHome_CardLeaveRequests = (CardView) findViewById(R.id.aHome_LeaveRequests);
        aHome_RequestMeeting = (CardView) findViewById(R.id.aHome_RequestMeeting);
        aHome_llLeaverequest = (LinearLayout) findViewById(R.id.aHome_llLeaverequest);
        lblLeaveRequest = (TextView) findViewById(R.id.lblLeaveRequest);


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

        glMenu = (GridLayout) findViewById(R.id.aHome_glMenu);
        llAttendance = (LinearLayout) findViewById(R.id.aHome_llAttendance);
        llAbsentees = (LinearLayout) findViewById(R.id.aHome_llAbsentees);
        llCallSmsUsage = (LinearLayout) findViewById(R.id.aHome_llCallSmsUsage);
        llEmergency = (LinearLayout) findViewById(R.id.aHome_llEmergency);
        llEvents = (LinearLayout) findViewById(R.id.aHome_llEvents);
        llExam = (LinearLayout) findViewById(R.id.aHome_llExam);
        llHomeworkText = (LinearLayout) findViewById(R.id.aHome_llHomeWorkText);
        llHomeworkVoice = (LinearLayout) findViewById(R.id.aHome_llHomeWorkVoice);
        llNoticeBoard = (LinearLayout) findViewById(R.id.aHome_llNoticeBoard);
        llParticulars = (LinearLayout) findViewById(R.id.aHome_llParticulars);
        llPhotos = (LinearLayout) findViewById(R.id.aHome_llPhotos);
        llTextParents = (LinearLayout) findViewById(R.id.aHome_llTextParents);
        llVoiceParents = (LinearLayout) findViewById(R.id.aHome_llVoiceParents);
        llmanagemsg = (LinearLayout) findViewById(R.id.aHome_llmanagementmessage);
        aHome_llCalls = (LinearLayout) findViewById(R.id.aHome_llCalls);
        llFeedBack = (LinearLayout) findViewById(R.id.aHome_llFeedBack);
        aHome_llLibrary = (LinearLayout) findViewById(R.id.aHome_llLibrary);
        aHome_llRequestMeeting = (LinearLayout) findViewById(R.id.aHome_llRequestMeeting);
        aHome_llSpecialOffer = (LinearLayout) findViewById(R.id.aHome_llSpecialOffer);
        aHom_llAssignment = (LinearLayout) findViewById(R.id.aHom_llAssignment);
        aHom_llVideovimeo = (LinearLayout) findViewById(R.id.aHom_llVideovimeo);
        aHom_llChat = (LinearLayout) findViewById(R.id.aHom_llChat);
        aHom_llNewProducts = (LinearLayout) findViewById(R.id.aHom_llNewProducts);
        aHom_llMettingURL = (LinearLayout) findViewById(R.id.aHom_llMettingURL);
        aHom_llOnlineQuiz = (LinearLayout) findViewById(R.id.aHom_llOnlineQuiz);

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

        lblEmergency = (TextView) findViewById(R.id.lblEmergency);
        lblNormalVoice = (TextView) findViewById(R.id.lblNormalVoice);
        lblTextToParents = (TextView) findViewById(R.id.lblTextToParents);
        lblNoticeBoard = (TextView) findViewById(R.id.lblNoticeBoard);
        lblSchoolEvents = (TextView) findViewById(R.id.lblSchoolEvents);
        lblImageupload = (TextView) findViewById(R.id.lblImageupload);
        lblAbsenteesimReport = (TextView) findViewById(R.id.lblAbsenteesimReport);
        lblSchoolStrength = (TextView) findViewById(R.id.lblSchoolStrength);
        lblcallsUsageCount = (TextView) findViewById(R.id.lblcallsUsageCount);
        lbltextHomeWork = (TextView) findViewById(R.id.lbltextHomeWork);
        lblVoiceHomeWork = (TextView) findViewById(R.id.lblVoiceHomeWork);
        lblExamSchedule = (TextView) findViewById(R.id.lblExamSchedule);
        lblAttedanceMarking = (TextView) findViewById(R.id.lblAttedanceMarking);
        lblMessagesFromManagement = (TextView) findViewById(R.id.lblMessagesFromManagement);
        lblFeedBack = (TextView) findViewById(R.id.lblFeedBack);
        lblLibraryDetails = (TextView) findViewById(R.id.lblLibraryDetails);
        lblConferenceCall = (TextView) findViewById(R.id.lblConferenceCall);
        lblOnlineTextBook = (TextView) findViewById(R.id.lblOnlineTextBook);
        lblRequestMeeting = (TextView) findViewById(R.id.lblRequestMeeting);
        lblVideoUpload = (TextView) findViewById(R.id.lblVideoUpload);
        lblSpecialOffer = (TextView) findViewById(R.id.lblSpecialOffer);
        lblAssignment = (TextView) findViewById(R.id.lblAssignment);
        lblVideovimeo = (TextView) findViewById(R.id.lblVideovimeo);
        lblChat = (TextView) findViewById(R.id.lblChat);
        lblNewProduct = (TextView) findViewById(R.id.lblNewProduct);
        lblMeetingURL = (TextView) findViewById(R.id.lblMeetingURL);
        lblOnlineQuiz = (TextView) findViewById(R.id.lblOnlineQuiz);


        llVoiceParents.setOnClickListener(this);
        llTextParents.setOnClickListener(this);
        llPhotos.setOnClickListener(this);
        llParticulars.setOnClickListener(this);
        llAbsentees.setOnClickListener(this);
        llAttendance.setOnClickListener(this);
        llCallSmsUsage.setOnClickListener(this);
        llEmergency.setOnClickListener(this);
        llEvents.setOnClickListener(this);
        llExam.setOnClickListener(this);
        llHomeworkText.setOnClickListener(this);
        llHomeworkVoice.setOnClickListener(this);
        llNoticeBoard.setOnClickListener(this);
        llmanagemsg.setOnClickListener(this);
        llFeedBack.setOnClickListener(this);
        aHome_llLibrary.setOnClickListener(this);
        aHome_llCalls.setOnClickListener(this);
        aHome_llRequestMeeting.setOnClickListener(this);

        aHome_llLeaverequest.setOnClickListener(this);
        aHome_llSpecialOffer.setOnClickListener(this);
        aHom_llAssignment.setOnClickListener(this);
        aHom_llVideovimeo.setOnClickListener(this);
        aHom_llChat.setOnClickListener(this);
        aHom_llNewProducts.setOnClickListener(this);
        aHom_llMettingURL.setOnClickListener(this);
        aHom_llOnlineQuiz.setOnClickListener(this);

        getMenuDetails();
        String alertMessage =TeacherUtil_SharedPreference.getLoginMessage(Teacher_AA_Test.this);
        if(!alertMessage.equals("Success")) {
            if (Role.equals("p1") || Role.equals("p2")) {
                showPaymentPendingAlert();
            }
        }
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
                            putPrincipalNametoSharedPref(menu_name);
                            putPrincipalIdstoSharedPref(menu_id);
                        }
                        String alert_message = jsonObject.getString("alert_message");
                        if(!alert_message.equals("")){
                            lnrScroll.setVisibility(View.VISIBLE);
                            scrollingtext.setText(alert_message);
                        }
                        else {
                            lnrScroll.setVisibility(View.GONE);
                        }

                        setAllHomeMenus();
                        selectedLoginType();
                        setupBottomBar();
                        glMenu.setVisibility(View.VISIBLE);


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

                    } else {
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

        String new_product = TeacherUtil_SharedPreference.getNewProduct(Teacher_AA_Test.this);
        if (new_product.equals("1")) {
            Glide.with(this).load(R.drawable.new_products).into(imgNew);
        } else {
            Glide.with(this).load(R.raw.new_gif).into(imgNew);
        }

    }

    private void bookLinkEnable() {
        for (int i = 0; i < schools_list.size(); i++) {
            final TeacherSchoolsModel name = schools_list.get(i);
            String BookEnable = name.getBookEnable();
            BookLink = name.getOnlineLink();

            if (BookEnable.equals("1")) {
                aHome_Books.setVisibility(View.VISIBLE);
            }
        }
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

    private void setLabelNames() {
        MenuNamesnames = TeacherUtil_SharedPreference.getPrincipalNames(Teacher_AA_Test.this);
        for (int i = 0; i < MenuNamesnames.length; i++) {

            String newString = MenuNamesnames[i].substring(0, MenuNamesnames[i].length() - 2);
            String substring = MenuNamesnames[i].substring(Math.max(MenuNamesnames[i].length() - 2, 0));

            if (substring.equals("_0")) {

                lblEmergency.setText(newString);

            } else if (substring.equals("_1")) {

                lblNormalVoice.setText(newString);

            } else if (substring.equals("_2")) {


                lblTextToParents.setText(newString);

            } else if (substring.equals("_3")) {


                lblNoticeBoard.setText(newString);

            } else if (substring.equals("_4")) {

                lblSchoolEvents.setText(newString);

            } else if (substring.equals("_5")) {


                lblImageupload.setText(newString);

            } else if (substring.equals("_6")) {


                lblAbsenteesimReport.setText(newString);

            } else if (substring.equals("_7")) {


                lblSchoolStrength.setText(newString);

            } else if (substring.equals("_8")) {

                lblcallsUsageCount.setText(MenuNamesnames[i]);

            } else if (substring.equals("_9")) {

                lbltextHomeWork.setText(newString);

            }

            String newString1 = MenuNamesnames[i].substring(0, MenuNamesnames[i].length() - 3);
            String substring1 = MenuNamesnames[i].substring(Math.max(MenuNamesnames[i].length() - 3, 0));


            if (substring1.equals("_10")) {

                lblVoiceHomeWork.setText(newString1);
            } else if (substring1.equals("_11")) {

                lblExamSchedule.setText(newString1);
            } else if (substring1.equals("_12")) {

                lblAttedanceMarking.setText(newString1);
            } else if (substring1.equals("_13")) {

                lblMessagesFromManagement.setText(newString1);
            } else if (substring1.equals("_14")) {

                lblFeedBack.setText(newString1);
            } else if (substring1.equals("_15")) {

                lblLibraryDetails.setText(newString1);
            } else if (substring1.equals("_16")) {

                lblConferenceCall.setText(newString1);
            } else if (substring1.equals("_17")) {
                lblLeaveRequest.setText(newString1);
            } else if (substring1.equals("_18")) {

                lblOnlineTextBook.setText(newString1);
            } else if (substring1.equals("_19")) {
                lblRequestMeeting.setText(newString1);
            } else if (substring1.equals("_20")) {
                lblVideoUpload.setText(newString1);
            } else if (substring1.equals("_21")) {
                lblSpecialOffer.setText(newString1);
            } else if (substring1.equals("_22")) {
                lblAssignment.setText(newString1);
            } else if (substring1.equals("_23")) {
                lblVideovimeo.setText(newString1);
            } else if (substring1.equals("_24")) {
                lblChat.setText(newString1);
            } else if (substring1.equals("_25")) {
                lblNewProduct.setText(newString1);
            } else if (substring1.equals("_26")) {
                lblMeetingURL.setText(newString1);
            } else if (substring1.equals("_27")) {
                lblOnlineQuiz.setText(newString1);
            }

        }

    }

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setAllHomeMenus() {
        for (int i = 0; i < glMenu.getChildCount(); i++) {
            mAllMenu[i] = glMenu.getChildAt(i);
        }

        glMenu.removeAllViews();
    }

    private void setGroupHeadMenus() {
        glMenu.removeAllViews();
        Integer[] groupheadMenu;
        groupheadMenu = TeacherUtil_SharedPreference.getGroupHeadIDs(Teacher_AA_Test.this);
        Log.d("grouphead_length", String.valueOf(groupheadMenu.length));

        for (int i = 0; i < groupheadMenu.length; i++) {


            if (groupheadMenu[i] == 18) {
                if (BookEnabled) {
                    glMenu.addView(mAllMenu[groupheadMenu[i]]);
                    ViewGroup.LayoutParams layoutParams = glMenu.getLayoutParams();
                    layoutParams.width = getPixelsFromDPs(Teacher_AA_Test.this, 400);
                    layoutParams.height = getPixelsFromDPs(Teacher_AA_Test.this, 250);
                    glMenu.setLayoutParams(layoutParams);
                } else {
                }
            } else {
                glMenu.addView(mAllMenu[groupheadMenu[i]]);
                ViewGroup.LayoutParams layoutParams = glMenu.getLayoutParams();
                layoutParams.width = getPixelsFromDPs(Teacher_AA_Test.this, 400);
                layoutParams.height = getPixelsFromDPs(Teacher_AA_Test.this, 250);
                glMenu.setLayoutParams(layoutParams);
            }

        }
    }

    private int getPixelsFromDPs(Teacher_AA_Test teacher_aa_test, int i) {
        Resources r = this.getResources();
        int px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, i, r.getDisplayMetrics()));
        return px;
    }

    private void setPrincipalMenus() {
        glMenu.removeAllViews();
        Integer[] PrincipalMenu;
        PrincipalMenu = TeacherUtil_SharedPreference.getPrincipalIDs(Teacher_AA_Test.this);
        for (int i = 0; i < PrincipalMenu.length; i++) {

            if (PrincipalMenu[i] == 18) {
                if (BookEnabled) {
                    glMenu.addView(mAllMenu[PrincipalMenu[i]]);
                } else {
                }
            } else {
                glMenu.addView(mAllMenu[PrincipalMenu[i]]);
            }
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Write_Etnl_Permission", "Permission is granted");
                return true;
            } else {

                Log.v("Write_Etnl_Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Write_Etnl_Permission", "Permission is granted");
            return true;
        }
    }

    public boolean isRecordPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Rec_Audio_Permission", "Permission is granted");
                return true;
            } else {

                Log.v("Rec_Audio_Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Rec_Audio_Permission", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Write_Etnl_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Rec_Audio_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                }
                return;
            }
        }
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
                pHelpWindow.showAtLocation(llAttendance, Gravity.NO_GRAVITY, 0, 0);
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

    private void changeLanguage(String lang, String Id) {
        TeacherUtil_SharedPreference.putLanguageType(Teacher_AA_Test.this, lang);
        //languageChangeApi(Id, lang);
    }


    private void putPrincipalNametoSharedPref(String isPrincipal) {
        String[] name = isPrincipal.split(",");
        isPrincipalMenuNames.clear();
        for (String itemtemp : name) {
            isPrincipalMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putPrincipalNames(isPrincipalMenuNames, Teacher_AA_Test.this);

    }

    private void putPrincipalIdstoSharedPref(String isPrincipalID) {
        String[] items = isPrincipalID.split(",");
        isPrincipalMenuID.clear();
        for (String itemtemp : items) {
            int result = Integer.parseInt(itemtemp);
            isPrincipalMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putPrincipalIDs(isPrincipalMenuID, Teacher_AA_Test.this);
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
                Log.d("LOGIN_TYPE", strLoginType);

                TeacherUtil_SharedPreference.putLoggedInAsToSP(Teacher_AA_Test.this, strLoginType);
                tvLoggedInAs.setText(strLoginType);
                selectedLoginType();

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

    private void selectedLoginType() {
        for (int i = 0; i < schools_list.size(); i++) {
            final TeacherSchoolsModel name = schools_list.get(i);
            String BookEnable = name.getBookEnable();
            BookLink = name.getOnlineLink();
            if (BookEnable.equals("1")) {
                BookEnabled = true;
            }
        }
            setPrincipalMenus();
            setLabelNames();
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
                Log.d("Help:Failure", t.toString());
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aHome_llAbsentees:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {

                    Log.d("Listsize", String.valueOf(listschooldetails.size()));
                    if (listschooldetails.size() == 1) {
                        Intent inAbs = new Intent(Teacher_AA_Test.this, TeacherAbsenteesReport.class);
                        inAbs.putExtra("REQUEST_CODE", PRINCIPAL_ABSENTEES);
                        startActivity(inAbs);
                    } else {
                        Intent inAbs = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inAbs.putExtra("REQUEST_CODE", PRINCIPAL_ABSENTEES);
                        startActivity(inAbs);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inAbsstaff = new Intent(Teacher_AA_Test.this, TeacherAbsenteesReport.class);
                    inAbsstaff.putExtra("REQUEST_CODE", STAFF_PHOTOS);
                    startActivity(inAbsstaff);
                }
                break;

            case R.id.aHome_llAttendance:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL) || TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_ADMIN)) {
                    if (listschooldetails.size() == 1) {
                        Intent inAtten = new Intent(Teacher_AA_Test.this, TeacherAttendanceScreen.class);
                        inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ATTENDANCE);
                        startActivity(inAtten);
                    } else {
                        Intent inAtten = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inAtten.putExtra("REQUEST_CODE", PRINCIPAL_ATTENDANCE);
                        startActivity(inAtten);
                    }
                }
                else {
                    Intent inPrincipal = new Intent(Teacher_AA_Test.this, TeacherAttendanceScreen.class);
                    inPrincipal.putExtra("REQUEST_CODE", STAFF_ATTENDANCE);
                    inPrincipal.putExtra("SCHOOL_ID", TeacherUtil_Common.Principal_SchoolId);
                    inPrincipal.putExtra("STAFF_ID", TeacherUtil_Common.Principal_staffId);
                    startActivity(inPrincipal);
                }
                break;

            case R.id.aHome_llCallSmsUsage:
                Intent inCallsSMS = new Intent(Teacher_AA_Test.this, TeacherCallsSmsUsages.class);
                startActivity(inCallsSMS);
                break;

            case R.id.aHome_llEmergency:
                if (isRecordPermissionGranted())
                    if (isStoragePermissionGranted()) {
                        Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                        inVoice.putExtra("EMERGENCY", true);
                        inVoice.putExtra("TeacherSchoolsModel", schoolmodel);

                        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_HEAD)) {
                            inVoice.putExtra("REQUEST_CODE", GH_EMERGENCY);
                        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_HEAD)) {
                            inVoice.putExtra("REQUEST_CODE", GH_VOICE);
                        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                            inVoice.putExtra("REQUEST_CODE", PRINCIPAL_EMERGENCY);
                        }
                        startActivity(inVoice);
                    }
                break;

            case R.id.aHome_llEvents:
                Intent inEvents = new Intent(Teacher_AA_Test.this, TeacherEventsScreen.class);
                inEvents.putExtra("REQUEST_CODE", PRINCIPAL_EVENTS);
                startActivity(inEvents);
                break;

            case R.id.aHome_llExam:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (listschooldetails.size() == 1) {
                        Intent inExam = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                        inExam.putExtra("REQUEST_CODE", PRINCIPAL_EXAM_TEST);
                        startActivity(inExam);
                    } else {
                        Intent inExam = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inExam.putExtra("REQUEST_CODE", PRINCIPAL_EXAM_TEST);
                        startActivity(inExam);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inExam = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                    inExam.putExtra("REQUEST_CODE", STAFF_TEXT_EXAM);
                    startActivity(inExam);
                }
                break;

            case R.id.aHome_llHomeWorkText:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (listschooldetails.size() == 1) {
                        Intent inHwText = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                        inHwText.putExtra("REQUEST_CODE", PRINCIPAL_TEXT_HW);
                        startActivity(inHwText);
                    } else {
                        Intent inHwText = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inHwText.putExtra("REQUEST_CODE", PRINCIPAL_TEXT_HW);
                        startActivity(inHwText);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inHwText = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                    inHwText.putExtra("REQUEST_CODE", STAFF_TEXT_HW);
                    startActivity(inHwText);
                }
                break;

            case R.id.aHome_llHomeWorkVoice:
                if (isRecordPermissionGranted())
                    if (isStoragePermissionGranted()) {
                        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                            if (listschooldetails.size() == 1) {
                                Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                                inHomeWorkVoice.putExtra("EMERGENCY", false);
                                inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE_HW);
                                startActivity(inHomeWorkVoice);
                            } else {
                                Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                                inHomeWorkVoice.putExtra("EMERGENCY", false);
                                inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE_HW);
                                startActivity(inHomeWorkVoice);
                            }
                        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                            Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                            inHomeWorkVoice.putExtra("EMERGENCY", false);
                            inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_VOICE_HW);
                            startActivity(inHomeWorkVoice);
                        }
                    }
                break;

            case R.id.aHome_llNoticeBoard:
                Intent inNoticeBoard = new Intent(Teacher_AA_Test.this, TeacherNoticeBoard.class);
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_HEAD))
                    inNoticeBoard.putExtra("REQUEST_CODE", GH_NOTICE_BOARD);
                else inNoticeBoard.putExtra("REQUEST_CODE", PRINCIPAL_NOTICE_BOARD);
                startActivity(inNoticeBoard);
                break;

            case R.id.aHome_llParticulars:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    Log.d("listsize", String.valueOf(listschooldetails.size()));
                    if (listschooldetails.size() == 1) {
                        Intent inParti = new Intent(Teacher_AA_Test.this, TeacherParticularsScreen.class);
                        inParti.putExtra("REQUEST_CODE", PRINCIPAL_SCHOOLSTRENGTH);
                        inParti.putExtra("SINGLESCHOOLLOGIN", true);
                        startActivity(inParti);
                    } else {
                        Intent inParti = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inParti.putExtra("REQUEST_CODE", PRINCIPAL_SCHOOLSTRENGTH);
                        startActivity(inParti);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    Intent inParti = new Intent(Teacher_AA_Test.this, TeacherParticularsScreen.class);
                    startActivity(inParti);
                }
                break;

            case R.id.aHome_llPhotos:
                if (isStoragePermissionGranted()) {
                    Intent inImg = new Intent(Teacher_AA_Test.this, TeacherPhotosScreen.class);

                    if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL))
                        inImg.putExtra("REQUEST_CODE", PRINCIPAL_PHOTOS);
                    else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER))
                        inImg.putExtra("REQUEST_CODE", STAFF_PHOTOS);
                    startActivity(inImg);
                }
                break;

            case R.id.aHome_llTextParents:

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (listschooldetails.size() == 1) {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_TEXT);
                        startActivity(inHomeWorkVoice);
                    } else {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_TEXT);
                        startActivity(inHomeWorkVoice);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    if (listschooldetails.size() == 1) {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_TEXT);
                        startActivity(inHomeWorkVoice);
                    } else {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_TEXT);
                        startActivity(inHomeWorkVoice);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_HEAD)) {

                    Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherGeneralText.class);
                    inHomeWorkVoice.putExtra("REQUEST_CODE", GH_TEXT);
                    startActivity(inHomeWorkVoice);

                }


                break;

            case R.id.aHome_llVoiceParents:
                if (isRecordPermissionGranted())
                    if (isStoragePermissionGranted()) {

                        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                            if (listschooldetails.size() == 1) {
                                Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE);
                                inVoice.putExtra("EMERGENCY", false);
                                startActivity(inVoice);
                            } else {
                                Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                                inVoice.putExtra("REQUEST_CODE", PRINCIPAL_VOICE);
                                inVoice.putExtra("EMERGENCY", false);
                                startActivity(inVoice);
                            }
                        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                            if (listschooldetails.size() == 1) {
                                Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                                inVoice.putExtra("REQUEST_CODE", STAFF_VOICE);
                                inVoice.putExtra("EMERGENCY", false);
                                startActivity(inVoice);
                            } else {
                                Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                                inVoice.putExtra("REQUEST_CODE", STAFF_VOICE);
                                inVoice.putExtra("EMERGENCY", false);
                                startActivity(inVoice);
                            }
                        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_HEAD)) {

                            Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherEmergencyVoice.class);
                            inVoice.putExtra("REQUEST_CODE", GH_VOICE);
                            inVoice.putExtra("EMERGENCY", false);
                            startActivity(inVoice);
                        }
                    }
                break;


            case R.id.aHome_llFeedBack:

                Intent feed = new Intent(Teacher_AA_Test.this, FeedBackDetails.class);
                feed.putExtra("TeacherSchoolsModel", schoolmodel);
                feed.putExtra("schools", myArray);
                feed.putExtra("list", schools_list);
                startActivity(feed);

                break;

            case R.id.aHome_llLibrary:

                Intent library = new Intent(Teacher_AA_Test.this, StaffLibraryDetails.class);
                library.putExtra("TeacherSchoolsModel", schoolmodel);
                startActivity(library);

                break;

            case R.id.aHome_llCalls:

                Intent schoolslist = new Intent(Teacher_AA_Test.this, ScoolsList.class);
                schoolslist.putExtra("TeacherSchoolsModel", schools_list);
                schoolslist.putExtra("schools", "");
                startActivity(schoolslist);

                break;
            case R.id.aHome_llmanagementmessage:

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (isRecordPermissionGranted())
                        if (isStoragePermissionGranted()) {
                            Intent messages = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                            messages.putExtra("REQUEST_CODE", PRINCIPAL_MESSAGESFROMMANAGEMENT);
                            startActivity(messages);
                        }
                } else {
                    if (isRecordPermissionGranted())
                        if (isStoragePermissionGranted()) {
                            Intent messages = new Intent(Teacher_AA_Test.this, TeacherMessageDatesScreen.class);
                            messages.putExtra("TeacherSchoolsModel", schoolmodel);
                            startActivity(messages);
                        }
                }
                break;


            case R.id.aHome_llLeaverequest:
                Intent messages = new Intent(Teacher_AA_Test.this, LeaveRequestsByStaffs.class);
                messages.putExtra("TeacherSchoolsModel", schoolmodel);
                messages.putExtra("type", "staff");
                startActivity(messages);

                break;


            case R.id.aHome_llRequestMeeting:
                Intent Request = new Intent(Teacher_AA_Test.this, RequestMeetingForSchool.class);
                Request.putExtra("TeacherSchoolsModel", schoolmodel);
                Request.putExtra("type", "staff");
                startActivity(Request);

                break;

            case R.id.aHom_llOnlineQuiz:
                Intent online = new Intent(Teacher_AA_Test.this, ParentQuizScreen.class);
                online.putExtra("Type", "SChool");
                startActivity(online);

                break;


            case R.id.aHome_llSpecialOffer:
                Intent offers = new Intent(Teacher_AA_Test.this, SpecialOfferScreen.class);
                startActivity(offers);

                break;

            case R.id.aHom_llNewProducts:

                Intent newProduct = new Intent(Teacher_AA_Test.this, NewProductsScreen.class);
                startActivity(newProduct);

                break;

            case R.id.aHom_llAssignment:
                Intent assignment = new Intent(Teacher_AA_Test.this, AssignmentActivity.class);
                startActivity(assignment);

                break;

            case R.id.aHom_llVideovimeo:
                Intent videovimeo = new Intent(Teacher_AA_Test.this, VideoUpload.class);
                startActivity(videovimeo);

                break;

            case R.id.aHom_llChat:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (listschooldetails.size() == 1) {
                        Intent inVoice = new Intent(Teacher_AA_Test.this, StaffDetailListActivity.class);
                        inVoice.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
                        startActivity(inVoice);
                    } else {
                        Intent inVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inVoice.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
                        startActivity(inVoice);
                    }
                } else {
                    Intent chat = new Intent(Teacher_AA_Test.this, SubjectListActivity.class);
                    chat.putExtra(Constants.STAFF_ID, Principal_staffId);
                    chat.putExtra(Constants.COME_FROM, Constants.STAFF);
                    startActivity(chat);
                }

                break;


            case R.id.aHom_llMettingURL:

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (listschooldetails.size() == 1) {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherMeetingURLScreen.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_MEETING_URL);
                        startActivity(inHomeWorkVoice);
                    } else {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", PRINCIPAL_MEETING_URL);
                        startActivity(inHomeWorkVoice);
                    }
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(Teacher_AA_Test.this).equals(LOGIN_TYPE_TEACHER)) {
                    if (listschooldetails.size() == 1) {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherMeetingURLScreen.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_MEETING_URL);
                        startActivity(inHomeWorkVoice);
                    } else {
                        Intent inHomeWorkVoice = new Intent(Teacher_AA_Test.this, TeacherSchoolList.class);
                        inHomeWorkVoice.putExtra("REQUEST_CODE", STAFF_MEETING_URL);
                        startActivity(inHomeWorkVoice);
                    }
                }

                break;

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

    private void showLogoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Teacher_AA_Test.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(Teacher_AA_Test.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(Teacher_AA_Test.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(Teacher_AA_Test.this, "");


                TeacherUtil_SharedPreference.clearStaffSharedPreference(Teacher_AA_Test.this);
                startActivity(new Intent(Teacher_AA_Test.this, TeacherSignInScreen.class));
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
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
        Button negativebutton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }
}
