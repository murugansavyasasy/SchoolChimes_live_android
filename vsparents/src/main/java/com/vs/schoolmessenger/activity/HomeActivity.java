package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.assignment.ParentAssignmentListActivity;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.payment.FeesTab;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import androidx.gridlayout.widget.GridLayout;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import static com.vs.schoolmessenger.util.Util_Common.MENU_ATTENDANCE;
import static com.vs.schoolmessenger.util.Util_Common.MENU_DOCUMENTS;
import static com.vs.schoolmessenger.util.Util_Common.MENU_EMERGENCY;
import static com.vs.schoolmessenger.util.Util_Common.MENU_HW;
import static com.vs.schoolmessenger.util.Util_Common.MENU_LEAVE_REQUEST;
import static com.vs.schoolmessenger.util.Util_Common.MENU_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.Util_Common.MENU_PHOTOS;
import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT;
import static com.vs.schoolmessenger.util.Util_Common.MENU_VOICE;
import static com.vs.schoolmessenger.util.Util_Common.hasPermissions;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {
    static ArrayList<Integer> isParentMenuID = new ArrayList<>();
    static ArrayList<String> isParentMenuNames = new ArrayList<>();

    String strChildName, child_ID;
    Profiles childItem = new Profiles();
    TextView tv_schoolname, tvSchoolAddress;
    NetworkImageView nivSchoolLogo;
    ImageLoader imageLoader;
    String[] PERMISSIONS = {Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    int PERMISSION_ALL = 1;

    LinearLayout aHome_llBooks,llEmergency, llVoiceParents, llTextParents, llNoticeBoard, llEvents, llPhotos, llfeespayment, aHome_Library_Details,
            aHome_StaffDetails,aHome_Exam_Marks;
    LinearLayout llDocuments, llHomeworkText, llExam, llAttendance, llLeaveRequest,
            aHome_llStaffDetails,aHome_llRequestMeeting,aHome_llAssignment,aHome_llVideosvimeo,aHome_llMeetingURL,aHome_llOnlineQuiz,aHome_llsrw,aHome_ll_time_table;

    TextView tv_emgvoicecount, tv_voicemsgcount, tv_textmessagecount, tv_noticeboard, tv_schoolevent, tv_photoscount,
            tv_documentcount, tv_homework, tv_examtest, lblStaffDetails,tv_assigncount,aVideosvimeo,lblMeetingURL,lblOnlineQuiz;

    Intent inNext;
    SqliteDB myDb;

    ImageView aHome_nivSchoolLogo;
    TextView lblEmergencyNme,
            lblGeneralVoice,
            lblTextMessages,
            lblhomeWork,
            lblExamtest,
            lblexamMarks,
            lblCirculars,
            lblNoticeBoard,
            lblSchoolEvents,
            lblAttedance,
            lblleaveInformation,
            lblFeeDetails,
            lblImages,
            lblLibraryDetails,
            lblOnlineTextBook,lblRequestMeeting,lblVideos,lblAssignment,lblVideosvimeo,
            aMeetingURL,lbllsrw,aOnlineQuiz,alsrw,lbltime_table;

    CardView aHome_Books,aHome_RequestMeeting;
    RelativeLayout rytHome,rytLanguage, rytPassword,rytHelp,rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();

    String[] MenuNamesnames;
    String BookLink;

    GridLayout glMenu;
    View[] mAllMenu = new View[24];
    Button btnQuize,btnlsrw;
    RelativeLayout rytParent;
    TextView scrollingtext;
    LinearLayout lnrScroll;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_home);

        String home=getIntent().getExtras().getString("HomeScreen","");
        if(!home.equals("1")) {
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

        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        btnQuize = (Button) findViewById(R.id.btnQuize);
        btnlsrw = (Button) findViewById(R.id.btnlsrw);

        scrollingtext = (TextView) findViewById(R.id.scrollingtext);
        lnrScroll = (LinearLayout) findViewById(R.id.lnrScroll);
        scrollingtext.setSelected (true);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        btnQuize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizescreen=new Intent(HomeActivity.this,ParentQuizScreen.class);
                startActivity(quizescreen);

            }
        });
        btnlsrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent quizescreen=new Intent(HomeActivity.this,LSRWListActivity.class);
                startActivity(quizescreen);

            }
        });

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

         aHome_Books = (CardView) findViewById(R.id.aHome_Books);
         aHome_llBooks = (LinearLayout) findViewById(R.id.aHome_llBooks);

         glMenu = (GridLayout) findViewById(R.id.aHome_glMenu);
         aHome_nivSchoolLogo = (ImageView) findViewById(R.id.aHome_nivSchoolLogo);
        String url = childItem.getSchoolThumbnailImgUrl();

        try {
            if (!url.equals("")) {
                Glide.with(this).load(url).centerCrop().into(aHome_nivSchoolLogo);
            }
        } catch (Exception e) {
            showToast(e.getMessage());
        }


        String bookenable = childItem.getBookEnable();
        final String booklink = childItem.getBookLink();
        aHome_llBooks.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent browse = new Intent(HomeActivity.this, TextBookForParent.class);
                    browse.putExtra("url", BookLink);
                    startActivity(browse);
                }
            });


        child_ID = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);

        tv_schoolname = (TextView) findViewById(R.id.aHome_tvSchoolName);
        tv_schoolname.setText(Util_SharedPreference.getSchoolnameFromSP(HomeActivity.this));

        tvSchoolAddress = (TextView) findViewById(R.id.aHome_tvSchoolAddress);
        tvSchoolAddress.setText(Util_SharedPreference.getSchooladdressFromSP(HomeActivity.this));
        llAttendance = (LinearLayout) findViewById(R.id.aHome_llAttendance);
        llDocuments = (LinearLayout) findViewById(R.id.aHome_llDocuments);
        llEmergency = (LinearLayout) findViewById(R.id.aHome_llEmergency);
        llEvents = (LinearLayout) findViewById(R.id.aHome_llEvents);
        llExam = (LinearLayout) findViewById(R.id.aHome_llExam);
        llHomeworkText = (LinearLayout) findViewById(R.id.aHome_llHomeWorkText);
        llNoticeBoard = (LinearLayout) findViewById(R.id.aHome_llNoticeBoard);
        llPhotos = (LinearLayout) findViewById(R.id.aHome_llPhotos);
        llTextParents = (LinearLayout) findViewById(R.id.aHome_llTextParents);
        llVoiceParents = (LinearLayout) findViewById(R.id.aHome_llVoiceParents);
        llLeaveRequest = (LinearLayout) findViewById(R.id.aHome_llLeaveRequest);
        llfeespayment = (LinearLayout) findViewById(R.id.aHome_llPayfees);
        aHome_Library_Details = (LinearLayout) findViewById(R.id.aHome_Library_Details);
        aHome_llStaffDetails = (LinearLayout) findViewById(R.id.aHome_llStaffDetails);
        aHome_llRequestMeeting = (LinearLayout) findViewById(R.id.aHome_llRequestMeeting);
        aHome_llAssignment = (LinearLayout) findViewById(R.id.aHome_llAssignment);
        aHome_llVideosvimeo = (LinearLayout) findViewById(R.id.aHome_llVideosvimeo);
        aHome_llMeetingURL = (LinearLayout) findViewById(R.id.aHome_llMeetingURL);
        aHome_llOnlineQuiz = (LinearLayout) findViewById(R.id.aHome_llOnlineQuiz);
        aHome_llsrw = (LinearLayout) findViewById(R.id.aHome_llsrw);
        aHome_ll_time_table = (LinearLayout) findViewById(R.id.aHome_ll_time_table);

        aHome_Exam_Marks = (LinearLayout) findViewById(R.id.aHome_Exam_Marks);

        tv_emgvoicecount = (TextView) findViewById(R.id.aHome_tvUnreadEmergency);
        tv_voicemsgcount = (TextView) findViewById(R.id.aHome_tvUnreadvoice);
        tv_textmessagecount = (TextView) findViewById(R.id.aHome_tvUnreadText);
        tv_noticeboard = (TextView) findViewById(R.id.aHome_tvUnreadNoticeboard);
        tv_schoolevent = (TextView) findViewById(R.id.aHome_tvUnreadEvents);
        tv_photoscount = (TextView) findViewById(R.id.aHome_tvUnreadPhotos);
        tv_documentcount = (TextView) findViewById(R.id.aHome_tvUnreaddocuments);
        tv_homework = (TextView) findViewById(R.id.aHome_tvUnreadHW);
        tv_examtest = (TextView) findViewById(R.id.aHome_tvUnreadExam);
        tv_assigncount = (TextView) findViewById(R.id.aAssign);
        aVideosvimeo = (TextView) findViewById(R.id.aVideosvimeo);
        aMeetingURL = (TextView) findViewById(R.id.aMeetingURL);


        lblEmergencyNme = (TextView) findViewById(R.id.lblEmergencyNme);
        lblGeneralVoice = (TextView) findViewById(R.id.lblGeneralVoice);
        lblTextMessages = (TextView) findViewById(R.id.lblTextMessages);
        lblhomeWork = (TextView) findViewById(R.id.lblhomeWork);
        lblExamtest = (TextView) findViewById(R.id.lblExamtest);
        lblexamMarks = (TextView) findViewById(R.id.lblexamMarks);
        lblCirculars = (TextView) findViewById(R.id.lblCirculars);
        lblNoticeBoard = (TextView) findViewById(R.id.lblNoticeBoard);
        lblSchoolEvents = (TextView) findViewById(R.id.lblSchoolEvents);
        lblAttedance = (TextView) findViewById(R.id.lblAttedance);
        lblleaveInformation = (TextView) findViewById(R.id.lblleaveInformation);
        lblFeeDetails = (TextView) findViewById(R.id.lblFeeDetails);
        lblImages = (TextView) findViewById(R.id.lblImages);
        lblLibraryDetails = (TextView) findViewById(R.id.lblLibraryDetails);
        lblOnlineTextBook = (TextView) findViewById(R.id.lblOnlineTextBook);
        lblStaffDetails = (TextView) findViewById(R.id.lblStaffDetails);
        lblRequestMeeting = (TextView) findViewById(R.id.lblRequestMeeting);
        lblVideos = (TextView) findViewById(R.id.lblVideos);
        lblAssignment = (TextView) findViewById(R.id.lblAssignment);
        lblVideosvimeo = (TextView) findViewById(R.id.lblVideosvimeo);
        lblMeetingURL = (TextView) findViewById(R.id.lblMeetingURL);
        lblOnlineQuiz = (TextView) findViewById(R.id.lblOnlineQuiz);
        lbllsrw = (TextView) findViewById(R.id.lbllsrw);
        aOnlineQuiz = (TextView) findViewById(R.id.aOnlineQuiz);
        alsrw = (TextView) findViewById(R.id.alsrw);

        lbltime_table = (TextView) findViewById(R.id.lbltime_table);
        llDocuments.setOnClickListener(this);
        llVoiceParents.setOnClickListener(this);
        llTextParents.setOnClickListener(this);
        llPhotos.setOnClickListener(this);
        llAttendance.setOnClickListener(this);
        llEmergency.setOnClickListener(this);
        llEvents.setOnClickListener(this);
        llExam.setOnClickListener(this);
        llHomeworkText.setOnClickListener(this);
        llNoticeBoard.setOnClickListener(this);
        llLeaveRequest.setOnClickListener(this);
        llfeespayment.setOnClickListener(this);
        aHome_Library_Details.setOnClickListener(this);
        aHome_Exam_Marks.setOnClickListener(this);
        aHome_llStaffDetails.setOnClickListener(this);
        aHome_llRequestMeeting.setOnClickListener(this);

        aHome_llAssignment.setOnClickListener(this);
        aHome_llVideosvimeo.setOnClickListener(this);
        aHome_llMeetingURL.setOnClickListener(this);
        aHome_llOnlineQuiz.setOnClickListener(this);
        aHome_llsrw.setOnClickListener(this);
        aHome_ll_time_table.setOnClickListener(this);


        getMenuDetails();
    }

    private void getMenuDetails() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(HomeActivity.this);
        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(HomeActivity.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(HomeActivity.this, "child_list");
        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("type","parent");
        jsonObject.addProperty("id",childItem.getChildID());
        jsonObject.addProperty("schoolid",childItem.getSchoolID());
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

                        if(status.equals("1")) {
                            String menu_name = jsonObject.getString("menu_name");
                            String menu_id = jsonObject.getString("menu_id");
                            putParentNamestoSharedPref(menu_name);
                            putParentIdstoSharedPref(menu_id);
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
                        selectedParentViews();
                        setMenuNames();

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

    public void deleteCache(Activity activity) {
        try {
            File dir = activity.getCacheDir();
            if(deleteDir(dir)){
                Toast.makeText(activity,"Cache cleared",Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(activity,"Cache not cleared",Toast.LENGTH_LONG).show();

            }
        } catch (Exception e) { e.printStackTrace();}
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
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    private void putParentNamestoSharedPref(String isParent) {
        String[] name4 = isParent.split(",");
        isParentMenuNames.clear();
        for (String itemtemp : name4) {
            isParentMenuNames.add(itemtemp);

        }
        TeacherUtil_SharedPreference.putParentMenuNames(isParentMenuNames, HomeActivity.this);

    }

    private void putParentIdstoSharedPref(String isParentID) {
        String[] items4 = isParentID.split(",");
        isParentMenuID.clear();
        for (String itemtemp : items4) {
            int result = Integer.parseInt(itemtemp);
            isParentMenuID.add(result);

        }
        TeacherUtil_SharedPreference.putparentIDs(isParentMenuID, HomeActivity.this);
    }

    private void setMenuNames() {
        MenuNamesnames = TeacherUtil_SharedPreference.getParentMenuNames(HomeActivity.this);
        for (int i = 0; i < MenuNamesnames.length; i++) {


            String newString = MenuNamesnames[i].substring(0, MenuNamesnames[i].length() - 2);
            String substring = MenuNamesnames[i].substring(Math.max(MenuNamesnames[i].length() - 2, 0));

            if (substring.equals("_0")) {

                lblEmergencyNme.setText(newString);

            } else if (substring.equals("_1")) {

                lblGeneralVoice.setText(newString);

            } else if (substring.equals("_2")) {

                lblTextMessages.setText(newString);

            } else if (substring.equals("_3")) {

                lblhomeWork.setText(newString);

            } else if (substring.equals("_4")) {

                lblExamtest.setText(newString);

            } else if (substring.equals("_5")) {

                lblexamMarks.setText(newString);

            } else if (substring.equals("_6")) {


                lblCirculars.setText(newString);

            } else if (substring.equals("_7")) {


                lblNoticeBoard.setText(newString);

            } else if (substring.equals("_8")) {

                lblSchoolEvents.setText(newString);

            } else if (substring.equals("_9")) {

                lblAttedance.setText(newString);

            }

            String newString1 = MenuNamesnames[i].substring(0, MenuNamesnames[i].length() - 3);
            String substring1 = MenuNamesnames[i].substring(Math.max(MenuNamesnames[i].length() - 3, 0));

            if (substring1.equals("_10")) {
                lblleaveInformation.setText(newString1);


            } else if (substring1.equals("_11")) {


                lblFeeDetails.setText(newString1);


            } else if (substring1.equals("_12")) {


                lblImages.setText(newString1);


            } else if (substring1.equals("_13")) {


                lblLibraryDetails.setText(newString1);

            } else if (substring1.equals("_14")) {


                lblStaffDetails.setText(newString1);


            } else if (substring1.equals("_15")) {

                lblOnlineTextBook.setText(newString1);

                }

            else if(substring1.equals("_16")){
                lblRequestMeeting.setText(newString1);
            }
            else if(substring1.equals("_17")){
                lblVideos.setText(newString1);
            }
            else if(substring1.equals("_18")){
                lblAssignment.setText(newString1);
            }
            else if(substring1.equals("_19")){
                lblVideosvimeo.setText(newString1);
            }

            else if(substring1.equals("_20")){
                lblMeetingURL.setText(newString1);
            }

            else if(substring1.equals("_21")){
                lblOnlineQuiz.setText(newString1);
            }
            else if(substring1.equals("_22")){
                lbllsrw.setText(newString1);
            }

            else if(substring1.equals("_23")){
                lbltime_table.setText(newString1);
            }

        }

    }

    private void selectedParentViews() {
        glMenu.removeAllViews();

        Integer[] parentMenu;
        parentMenu = TeacherUtil_SharedPreference.getParentIDs(HomeActivity.this);
        Log.d("parentMenu", String.valueOf(parentMenu.length));
        for (int i = 0; i < parentMenu.length; i++) {
            String bookenable = childItem.getBookEnable();
            BookLink = childItem.getBookLink();

            if(parentMenu[i]==15){
                    if(bookenable.equals("1")){
                        glMenu.addView(mAllMenu[parentMenu[i]]);
                    }
                }
                else {
                glMenu.addView(mAllMenu[parentMenu[i]]);
                }
        }

    }
    private void setAllHomeMenus() {
        for (int i = 0; i < glMenu.getChildCount(); i++) {
            mAllMenu[i] = glMenu.getChildAt(i);
        }

        glMenu.removeAllViews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aHome_llDocuments:
                inNext = new Intent(HomeActivity.this, PdfCircular.class);
                inNext.putExtra("REQUEST_CODE", MENU_DOCUMENTS);
                inNext.putExtra("HEADER", R.string.recent_files);
                startActivity(inNext);
                break;

            case R.id.aHome_llAttendance:
                inNext = new Intent(HomeActivity.this, Attendance.class);
                inNext.putExtra("REQUEST_CODE", MENU_ATTENDANCE);
                inNext.putExtra("HEADER", R.string.attedance);
                inNext.putExtra("Profiles", childItem);
                startActivity(inNext);
                break;

            case R.id.aHome_llEmergency:
                inNext = new Intent(HomeActivity.this, VoiceCircular.class);
                inNext.putExtra("REQUEST_CODE", MENU_EMERGENCY);
                inNext.putExtra("HEADER", R.string.emergency);
                startActivity(inNext);
                break;

            case R.id.aHome_llEvents:

                inNext = new Intent(HomeActivity.this, EventsTapScreen.class);
                startActivity(inNext);

                break;

            case R.id.aHome_llExam:

                inNext = new Intent(HomeActivity.this, ExamCircularActivity.class);
                startActivity(inNext);
                break;

            case R.id.aHome_llHomeWorkText:
                inNext = new Intent(HomeActivity.this, MessageDatesScreen.class);
                inNext.putExtra("REQUEST_CODE", MENU_HW);
                inNext.putExtra("Profiles", childItem);
                startActivity(inNext);
                break;

            case R.id.aHome_llNoticeBoard:
                inNext = new Intent(HomeActivity.this, TextCircular.class);
                inNext.putExtra("REQUEST_CODE", MENU_NOTICE_BOARD);
                inNext.putExtra("HEADER", R.string.home_notice_board);
                inNext.putExtra("Profiles", childItem);
                startActivity(inNext);
                break;

            case R.id.aHome_llPhotos:
                inNext = new Intent(HomeActivity.this, ImageCircular.class);
                inNext.putExtra("REQUEST_CODE", MENU_PHOTOS);
                inNext.putExtra("HEADER", R.string.recent_photos);
                startActivity(inNext);
                break;

            case R.id.aHome_llTextParents:
                inNext = new Intent(HomeActivity.this, DatesList.class);
                inNext.putExtra("REQUEST_CODE", MENU_TEXT);
                inNext.putExtra("HEADER", R.string.recent_messages);
                startActivity(inNext);
                break;

            case R.id.aHome_llVoiceParents:
                inNext = new Intent(HomeActivity.this, DatesList.class);
                inNext.putExtra("REQUEST_CODE", MENU_VOICE);
                    inNext.putExtra("HEADER", R.string.recent_voice_messages);
                startActivity(inNext);
                break;

            case R.id.aHome_llLeaveRequest:
                inNext = new Intent(HomeActivity.this, ApplyLeave.class);
                inNext.putExtra("REQUEST_CODE", MENU_LEAVE_REQUEST);
                inNext.putExtra("HEADER", R.string.leave+" "+R.string.requesttttt);
                startActivity(inNext);
                break;

            case R.id.aHome_llPayfees:

               // inNext = new Intent(HomeActivity.this, FeesDetails.class);
               // inNext = new Intent(HomeActivity.this, PaymentFeesActrivity.class);
                //inNext = new Intent(HomeActivity.this, PaymentWebView.class);
                inNext = new Intent(HomeActivity.this, FeesTab.class);
                startActivity(inNext);


                break;

            case R.id.aHome_Library_Details:
                inNext = new Intent(HomeActivity.this, StudentLibraryDetails.class);
                String id = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
                inNext.putExtra("CHILD_ID", id);
                startActivity(inNext);
                break;

            case R.id.aHome_Exam_Marks:
                inNext = new Intent(HomeActivity.this, ExamListScreen.class);
                String stu_id = Util_SharedPreference.getChildIdFromSP(HomeActivity.this);
                String school_id = Util_SharedPreference.getSchoolIdFromSP(HomeActivity.this);
                inNext.putExtra("CHILD_ID", stu_id);
                inNext.putExtra("SHOOL_ID", school_id);
                startActivity(inNext);
                break;




            case R.id.aHome_llRequestMeeting:
                inNext = new Intent(HomeActivity.this, RequestMeetingForParent.class);
                startActivity(inNext);
                break;


                case R.id.aHome_llAssignment:
                inNext = new Intent(HomeActivity.this, ParentAssignmentListActivity.class);
                startActivity(inNext);
                break;

            case R.id.aHome_llVideosvimeo:
                inNext = new Intent(HomeActivity.this, VideoListActivity.class);
                startActivity(inNext);
                break;
            case R.id.aHome_llStaffDetails:
                inNext = new Intent(HomeActivity.this, StaffListActivity.class);
                startActivity(inNext);
                break;


            case R.id.aHome_llMeetingURL:
                inNext = new Intent(HomeActivity.this, OnlineClassParentScreen.class);
                startActivity(inNext);

                break;

            case R.id.aHome_llOnlineQuiz:
                inNext = new Intent(HomeActivity.this, ParentQuizScreen.class);
                inNext.putExtra("Type","Parent");
                startActivity(inNext);

                break;

                case R.id.aHome_llsrw:
                inNext = new Intent(HomeActivity.this, LSRWListActivity.class);
                inNext.putExtra("Type","Parent");
                startActivity(inNext);

                break;

            case R.id.aHome_ll_time_table:
                inNext = new Intent(HomeActivity.this, TimeTableActivity.class);
                startActivity(inNext);

                break;

            case R.id.rytHome:

                Intent homescreen=new Intent(HomeActivity.this,ChildrenScreen.class);
                homescreen.putExtra("HomeScreen","1");
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:
                Intent faq=new Intent(HomeActivity.this,FAQScreen.class);
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
                Util_Common.popUpMenu(HomeActivity.this,v,"1");

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
        TeacherUtil_SharedPreference.putLanguageType(HomeActivity.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
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

                        LanguageIDAndNames. putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"),HomeActivity.this);
                        LanguageIDAndNames.  putStaffIdstoSharedPref(jsonObject.getString("isStaffID"),HomeActivity.this);
                        LanguageIDAndNames. putAdminIdstoSharedPref(jsonObject.getString("isAdminID"),HomeActivity.this);
                        LanguageIDAndNames. putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"),HomeActivity.this);
                        LanguageIDAndNames. putParentIdstoSharedPref(jsonObject.getString("isParentID"),HomeActivity.this);
                        LanguageIDAndNames. putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"),HomeActivity.this);
                        LanguageIDAndNames. putStaffNamestoSharedPref(jsonObject.getString("isStaff"),HomeActivity.this);
                        LanguageIDAndNames. putAdminNamestoSharedPref(jsonObject.getString("isAdmin"),HomeActivity.this);
                        LanguageIDAndNames. putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"),HomeActivity.this);
                        LanguageIDAndNames. putParentNamestoSharedPref(jsonObject.getString("isParent"),HomeActivity.this);



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


        if (isNetworkConnected()) {
            UnreadCount();
        } else {

            myDb = new SqliteDB(HomeActivity.this);
            if (myDb.checkCountDetails(child_ID)) {
                viewall(child_ID);

            } else {
                showSettingsAlert1();
            }


        }

    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeActivity.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.connect_internet);
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

    private void viewall(String child_id) {

        Cursor res = myDb.getCountDetails(child_id);
        while (res.moveToNext()) {

            String document_count = res.getString(1);
            String image_count = res.getString(2);
            String emergncy_count = res.getString(3);
            String voice_count = res.getString(4);
            String sms_count = res.getString(5);
            String noticeboard_count = res.getString(6);
            String events_count = res.getString(7);
            String homework_count = res.getString(8);
            String exam_count = res.getString(9);

            if ((!document_count.equals("0")) || (!emergncy_count.equals("0")) || (!voice_count.equals("0")) ||
                    (!sms_count.equals("0")) || (!image_count.equals("0")) || (!noticeboard_count.equals("0")) ||
                    (!homework_count.equals("0")) || (!events_count.equals("0")) || (!exam_count.equals("0"))) {

                if (emergncy_count.equals("0")) {
                    tv_emgvoicecount.setVisibility(View.INVISIBLE);
                } else {
                    tv_emgvoicecount.setVisibility(View.VISIBLE);
                    tv_emgvoicecount.setText(emergncy_count);


                }

                if (voice_count.equals("0")) {
                    tv_voicemsgcount.setVisibility(View.INVISIBLE);
                } else {
                    tv_voicemsgcount.setVisibility(View.VISIBLE);
                    tv_voicemsgcount.setText(voice_count);
                }

                if (sms_count.equals("0")) {
                    tv_textmessagecount.setVisibility(View.INVISIBLE);
                } else {
                    tv_textmessagecount.setVisibility(View.VISIBLE);
                    tv_textmessagecount.setText(sms_count);
                }

                if (image_count.equals("0")) {
                    tv_photoscount.setVisibility(View.INVISIBLE);
                } else {
                    tv_photoscount.setVisibility(View.VISIBLE);
                    tv_photoscount.setText(image_count);
                }

                if (document_count.equals("0")) {
                    tv_documentcount.setVisibility(View.INVISIBLE);
                } else {
                    tv_documentcount.setVisibility(View.VISIBLE);
                    tv_documentcount.setText(document_count);
                }
                if (noticeboard_count.equals("0")) {
                    tv_noticeboard.setVisibility(View.INVISIBLE);
                } else {
                    tv_noticeboard.setVisibility(View.VISIBLE);
                    tv_noticeboard.setText(noticeboard_count);
                }
                if (exam_count.equals("0")) {
                    tv_examtest.setVisibility(View.INVISIBLE);
                } else {
                    tv_examtest.setVisibility(View.VISIBLE);
                    tv_examtest.setText(exam_count);
                }

                if (events_count.equals("0")) {
                    tv_schoolevent.setVisibility(View.INVISIBLE);
                } else {
                    tv_schoolevent.setVisibility(View.VISIBLE);
                    tv_schoolevent.setText(events_count);
                }
                if (homework_count.equals("0")) {
                    tv_homework.setVisibility(View.INVISIBLE);
                } else {
                    tv_homework.setVisibility(View.VISIBLE);
                    tv_homework.setText(homework_count);
                }

                }

            }
    }

    private void blink() {
        ObjectAnimator anim = ObjectAnimator.ofInt(tv_emgvoicecount, "backgroundColor", getResources().getColor(R.color.clr_white), getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.clr_white));
        anim.setDuration(1000);
        anim.setEvaluator(new ArgbEvaluator());

        anim.setRepeatCount(Animation.INFINITE);
        anim.start();

        }


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) HomeActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void UnreadCount() {
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(HomeActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(HomeActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

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

                        if(quiz.equals("0")){
                            aOnlineQuiz.setVisibility(View.INVISIBLE);
                        }
                        else {
                            aOnlineQuiz.setVisibility(View.VISIBLE);
                            aOnlineQuiz.setText(quiz);
                        }
                        if(lsrw.equals("0")){
                            alsrw.setVisibility(View.INVISIBLE);
                        }
                        else {
                            alsrw.setVisibility(View.VISIBLE);
                            alsrw.setText(lsrw);
                        }

                        if(onlineClass.equals("0")){
                            aMeetingURL.setVisibility(View.INVISIBLE);
                        }
                        else {
                            aMeetingURL.setVisibility(View.VISIBLE);
                            aMeetingURL.setText(onlineClass);
                        }

                        if(video.equals("0")){
                            aVideosvimeo.setVisibility(View.INVISIBLE);
                        }
                        else {
                            aVideosvimeo.setVisibility(View.VISIBLE);
                            aVideosvimeo.setText(video);
                        }
                        if(assignment.equals("0")){
                            tv_assigncount.setVisibility(View.INVISIBLE);
                        }
                        else {
                            tv_assigncount.setVisibility(View.VISIBLE);
                            tv_assigncount.setText(assignment);
                        }

                        if (emgvoicecount.equals("0")) {
                            tv_emgvoicecount.setVisibility(View.INVISIBLE);
                        } else {
                            tv_emgvoicecount.setVisibility(View.VISIBLE);
                            tv_emgvoicecount.setText(emgvoicecount);

                        }

                        if (voicemsgcount.equals("0")) {
                            tv_voicemsgcount.setVisibility(View.INVISIBLE);
                        } else {
                            tv_voicemsgcount.setVisibility(View.VISIBLE);
                            tv_voicemsgcount.setText(voicemsgcount);
                        }

                        if (textmessagecount.equals("0")) {
                            tv_textmessagecount.setVisibility(View.INVISIBLE);
                        } else {
                            tv_textmessagecount.setVisibility(View.VISIBLE);
                            tv_textmessagecount.setText(textmessagecount);
                        }

                        if (photoscount.equals("0")) {
                            tv_photoscount.setVisibility(View.INVISIBLE);
                        } else {
                            tv_photoscount.setVisibility(View.VISIBLE);
                            tv_photoscount.setText(photoscount);
                        }

                        if (documentcount.equals("0")) {
                            tv_documentcount.setVisibility(View.INVISIBLE);
                        } else {
                            tv_documentcount.setVisibility(View.VISIBLE);
                            tv_documentcount.setText(documentcount);
                        }
                        if (noticeboard.equals("0")) {
                            tv_noticeboard.setVisibility(View.INVISIBLE);
                        } else {
                            tv_noticeboard.setVisibility(View.VISIBLE);
                            tv_noticeboard.setText(noticeboard);
                        }
                        if (examtest.equals("0")) {
                            tv_examtest.setVisibility(View.INVISIBLE);
                        } else {
                            tv_examtest.setVisibility(View.VISIBLE);
                            tv_examtest.setText(examtest);
                        }

                        if (schoolevent.equals("0")) {
                            tv_schoolevent.setVisibility(View.INVISIBLE);
                        } else {
                            tv_schoolevent.setVisibility(View.VISIBLE);
                            tv_schoolevent.setText(schoolevent);
                        }
                        if (homework.equals("0")) {
                            tv_homework.setVisibility(View.INVISIBLE);
                        } else {
                            tv_homework.setVisibility(View.VISIBLE);
                            tv_homework.setText(homework);
                        }


                        myDb = new SqliteDB(HomeActivity.this);

                        if (myDb.checkCountDetails(child_ID)) {
                            myDb.updateCountDetails(emgvoicecount, voicemsgcount, textmessagecount, photoscount,
                                    documentcount, noticeboard, examtest, schoolevent, homework, child_ID);
                        } else {
                            myDb.addCountDetails(emgvoicecount, voicemsgcount, textmessagecount, photoscount,
                                    documentcount, noticeboard, examtest, schoolevent, homework, child_ID);
                        }




                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("Unreadcount:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Unreadcount:Failure", t.toString());
            }
        });

    }

    private void showToast(String msg) {
        Toast.makeText(HomeActivity.this, msg, Toast.LENGTH_SHORT).show();
    }


}
