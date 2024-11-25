package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ABSENTEES;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_CHAT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_DAILY_COLLECTION;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_FEE_PENDING_REPORT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_LESSON_PLAN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MESSAGESFROMMANAGEMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_NOTICEBOARD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_PTM_MEETING;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_SCHOOLSTRENGTH;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_STUDENT_REPORT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_ATTENDANCE_PRESENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_WISE_ATTENDANCE_REPORTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VIDEO_GALLERY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Activity.LessonPlanActivity;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.assignment.ImageAssignmentActivity;
import com.vs.schoolmessenger.assignment.MessageAssignmentActivity;
import com.vs.schoolmessenger.assignment.PdfAssignmentActivity;
import com.vs.schoolmessenger.assignment.VideoPrincipalRecipient;
import com.vs.schoolmessenger.assignment.ViewAssignment;
import com.vs.schoolmessenger.assignment.VoiceAssignmentActivity;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.StaffMsgMangeCount;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherSchoolList extends AppCompatActivity {
    public static ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    public static TeacherSchoolsModel schoolmodel;
    RecyclerView rvSchoolsList;
    String SchoolId;
    TeacherSchoolListForPrincipalAdapter schoolsListAdapter;
    boolean singleschoollogin = false;
    String Role = "";
    String schoolId, staffId, filePath, duration, title, voiceType;
    private final ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private int i_schools_count = 0;
    private int iRequestCode;
    private ProgressBar progressBar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_school_list);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        singleschoollogin = getIntent().getExtras().getBoolean("SINGLESCHOOLLOGIN", false);
        String home = getIntent().getExtras().getString("Homescreen", "");
        Role = TeacherUtil_SharedPreference.getRole(TeacherSchoolList.this);

        if (iRequestCode == GH_VOICE) {
            filePath = getIntent().getExtras().getString("FILEPATH", "");
            duration = getIntent().getExtras().getString("DURATION", "");
            title = getIntent().getExtras().getString("TITTLE", "");
            voiceType = getIntent().getExtras().getString("VOICE", "");

        }

        schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
        schools_list = (ArrayList<TeacherSchoolsModel>) getIntent().getSerializableExtra("list");


        Log.d("Requestcode", String.valueOf(iRequestCode));
        rvSchoolsList = (RecyclerView) findViewById(R.id.schoolList_rvSchoolsList);
        ImageView ivBack = (ImageView) findViewById(R.id.schoollist_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        progressBar = findViewById(R.id.progressBar);

        listSchoolsAPI();

    }

    private void listSchoolsAPI() {
        Log.d("test1", "test1");
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            Log.d("test4", "test4");
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getSchoolNameRegional(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(), ss.getIsPaymentPending(), ss.getIsSchoolType(), ss.getIsBiometricEnable(),ss.getAllowDownload());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");


        }


        if (iRequestCode == STAFF_ATTENDANCE_PRESENT) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.isBioMetricEnable = item.getIsBiometricEnable();
                            Intent inVoice = new Intent(TeacherSchoolList.this, PunchStaffAttendanceUsingFinger.class);
                            inVoice.putExtra("REQUEST_CODE", iRequestCode);
                            inVoice.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inVoice.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inVoice);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == STAFF_WISE_ATTENDANCE_REPORTS) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inVoice = new Intent(TeacherSchoolList.this, StaffWiseAttendanceReports.class);
                            inVoice.putExtra("REQUEST_CODE", iRequestCode);
                            inVoice.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inVoice.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inVoice);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == PRINCIPAL_ABSENTEES) {
            schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            //    Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherAbsenteesReport.class);
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, SchoolAbsenteesReport.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                            Util_Common.isPosition = 0;
                            Util_Common.isPositionSection = 0;
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_NOTICEBOARD) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, StaffNoticeBoard.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inPrincipal);
                            Util_Common.isPosition = 0;
                            Util_Common.isPositionSection = 0;
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == PRINCIPAL_ATTENDANCE) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherAttendanceScreen.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_SCHOOLSTRENGTH) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherParticularsScreen.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_VOICE_HW) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherEmergencyVoice.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("EMERGENCY", false);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_TEXT_HW) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherGeneralText.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_EXAM_TEST) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherGeneralText.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == PRINCIPAL_MEETING_URL || iRequestCode == STAFF_MEETING_URL) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherMeetingURLScreen.class);

                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

//        if (iRequestCode == PRINCIPAL_MESSAGESFROMMANAGEMENT) {
//            getMegFromManageMentCount();
//        }

        if (iRequestCode == PRINCIPAL_FEE_PENDING_REPORT) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {

                            Intent inVoice = new Intent(TeacherSchoolList.this, FeePendingReport.class);
                            inVoice.putExtra("REQUEST_CODE", iRequestCode);
                            inVoice.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inVoice.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inVoice);
                            //startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == PRINCIPAL_PTM_MEETING) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {

                            Intent inVoice = new Intent(TeacherSchoolList.this, TeacherSidePtm.class);
                            inVoice.putExtra("REQUEST_CODE", iRequestCode);
                            inVoice.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inVoice.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inVoice);
                            //startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }


        if (iRequestCode == PRINCIPAL_ASSIGNMENT) {

            final String Type = getIntent().getExtras().getString("Type", "");

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();

                            if (Type.equals("Message")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, MessageAssignmentActivity.class);
                                startActivity(inPrincipal);
                            } else if (Type.equals("Voice")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, VoiceAssignmentActivity.class);
                                startActivity(inPrincipal);
                            } else if (Type.equals("Image")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, ImageAssignmentActivity.class);
                                startActivity(inPrincipal);
                            } else if (Type.equals("Pdf")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, PdfAssignmentActivity.class);
                                startActivity(inPrincipal);
                            } else if (Type.equals("View")) {
                                ViewAssignment fragment1 = new ViewAssignment();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
                                ft.detach(fragment1);
                                ft.attach(fragment1);
                                Bundle bundle = new Bundle();
                                bundle.putString("view", "1");
                                fragment1.setArguments(bundle);
                                ft.commit();
                            }
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == VIDEO_GALLERY) {
            final String Type = getIntent().getExtras().getString("Type", "");
            final String title = getIntent().getExtras().getString("TITLE", "");
            final String content = getIntent().getExtras().getString("CONTENT", "");
            final String filepath = getIntent().getExtras().getString("FILEPATH", "");
            final String size = getIntent().getExtras().getString("FILE_SIZE", "");

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();
                            TeacherUtil_Common.isVideoDownload = item.getAllowDownload();
                            if (Type.equals("Recipient")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, VideoPrincipalRecipient.class);
                                inPrincipal.putExtra("REQUEST_CODE", VIDEO_GALLERY);
                                inPrincipal.putExtra("TITLE", title);
                                inPrincipal.putExtra("CONTENT", content);
                                inPrincipal.putExtra("FILEPATH", filepath);
                                inPrincipal.putExtra("FILE_SIZE", size);
                                startActivity(inPrincipal);
                            }

                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
        if (iRequestCode == PRINCIPAL_CHAT) {

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();
//
//                            Intent inPrincipal = new Intent(TeacherSchoolList.this, StaffDetailListActivity.class);
//                            inPrincipal.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
//                            startActivity(inPrincipal);

                            Intent chat = new Intent(TeacherSchoolList.this, SubjectListActivity.class);
                            chat.putExtra(Constants.STAFF_ID, item.getStrStaffID());
                            chat.putExtra(Constants.COME_FROM, Constants.STAFF);
                            startActivity(chat);


                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }


        if (iRequestCode == PRINCIPAL_STUDENT_REPORT) {


            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();

                            Intent inPrincipal = new Intent(TeacherSchoolList.this, StudentReportActivity.class);
                            inPrincipal.putExtra("REQUEST_CODE", PRINCIPAL_STUDENT_REPORT);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inPrincipal);


                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }

        if (iRequestCode == PRINCIPAL_DAILY_COLLECTION) {

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();

                            //Intent inPrincipal = new Intent(TeacherSchoolList.this, DailyFeeCollectionActivity.class);
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, DailyCollectionFee.class);
                            inPrincipal.putExtra("REQUEST_CODE", PRINCIPAL_STUDENT_REPORT);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inPrincipal);


                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);

        }


        if (iRequestCode == PRINCIPAL_LESSON_PLAN) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();

                            Intent inPrincipal = new Intent(TeacherSchoolList.this, LessonPlanActivity.class);
                            inPrincipal.putExtra("REQUEST_CODE", PRINCIPAL_LESSON_PLAN);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inPrincipal);


                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);

        }

        if (iRequestCode == GH_VOICE) {
            schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, VoiceStandardAndGroupList.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            inPrincipal.putExtra("FILEPATH", filePath);
                            inPrincipal.putExtra("DURATION", duration);
                            inPrincipal.putExtra("TITTLE", title);
                            inPrincipal.putExtra("VOICE", voiceType);
                            startActivity(inPrincipal);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }


        if (iRequestCode == PRINCIPAL_TEXT) {

            generealTextScreen();

        }

        if (iRequestCode == STAFF_TEXT) {
            generealTextScreen();
        }


        if (iRequestCode == STAFF_VOICE) {
            voiceScreen();
        }

        if (iRequestCode == PRINCIPAL_VOICE) {

            voiceScreen();
        }


    }

    private void generealTextScreen() {
        TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                    @Override
                    public void onItemClick(TeacherSchoolsModel item) {
                        Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherGeneralText.class);
                        inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                        inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                        inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                        startActivityForResult(inPrincipal, iRequestCode);
                    }
                });

        rvSchoolsList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
        rvSchoolsList.setLayoutManager(layoutManager);
        rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
        rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
        rvSchoolsList.setAdapter(schoolsListAdapter);
    }

    private void voiceScreen() {
        TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                    @Override
                    public void onItemClick(TeacherSchoolsModel item) {
                        Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherEmergencyVoice.class);
                        inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                        inPrincipal.putExtra("EMERGENCY", false);
                        inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                        inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                        Util_Common.isSchoolType = item.getIsSchoolType();
                        startActivityForResult(inPrincipal, iRequestCode);
                    }
                });

        rvSchoolsList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
        rvSchoolsList.setLayoutManager(layoutManager);
        rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
        rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
        rvSchoolsList.setAdapter(schoolsListAdapter);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d("iRequestCode___", String.valueOf(iRequestCode));
//        if (requestCode == iRequestCode) {
//            Log.d("coming", "coming");
//            String message = data.getStringExtra("MESSAGE");
//            if (message.equals("SENT")) {
//                finish();
//            }
//        }
//    }


    @Override
    public void onBackPressed() {
//        backToResultActvity("SENT");
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (iRequestCode == PRINCIPAL_MESSAGESFROMMANAGEMENT) {
            progressBar.setVisibility(View.VISIBLE);
            getMegFromManageMentCount();
        }
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
//        returnIntent.putExtra("STD_SEC", stdSec);
        setResult(iRequestCode, returnIntent);
        finish();
    }


    private void getMegFromManageMentCount() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(TeacherSchoolList.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherSchoolList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSchoolList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        JsonArray jsArray = new JsonArray();

        if ((Role.equals("p3"))) {
            if (schools_list.size() > 1) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
                jsonObject.addProperty("staffId", TeacherUtil_Common.Principal_staffId);
                jsArray.add(jsonObject);
            } else {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("SchoolID", schoolmodel.getStrSchoolID());
                jsonObject.addProperty("staffId", schoolmodel.getStrStaffID());
                jsArray.add(jsonObject);

            }
        } else {
            for (int i = 0; i < schools_list.size(); i++) {
                JsonObject jsonObject = new JsonObject();
                final TeacherSchoolsModel model = schools_list.get(i);
                jsonObject.addProperty("SchoolID", model.getStrSchoolID());
                jsonObject.addProperty("staffId", model.getStrStaffID());
                jsArray.add(jsonObject);
            }
        }

        Log.d("Requestt", jsArray.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.getCountFromMsg_MangeMent(jsArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("Count:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Count:Res", response.body().toString());
                Util_Common.isStaffMsgFromManagementCount = 0;
                Util_Common.isStaffMsgMangeCount.clear();
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        for (int i = 0; i <= js.length() - 1; i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            String isCount = jsonObject.getString("OVERALLCOUNT");
                            String isSchoolId = jsonObject.getString("SCHOOLID");
                            StaffMsgMangeCount StaffMsgMangeCount = new StaffMsgMangeCount(isSchoolId, isCount);
                            Util_Common.isStaffMsgMangeCount.add(StaffMsgMangeCount);
                        }

                        for (int i = 0; i < Util_Common.isStaffMsgMangeCount.size(); i++) {
                            Log.d("isStaffMsgMangeCount", Util_Common.isStaffMsgMangeCount.get(i).getOVERALLCOUNT());
                            Util_Common.isStaffMsgFromManagementCount = Util_Common.isStaffMsgFromManagementCount + Integer.parseInt(Util_Common.isStaffMsgMangeCount.get(i).getOVERALLCOUNT());
                        }
                        Log.d("countMsgMangeCount", String.valueOf(Util_Common.isStaffMsgFromManagementCount));
                        progressBar.setVisibility(View.GONE);


                        TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                                new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, true, new TeacherSchoolListPrincipalListener() {
                                    @Override
                                    public void onItemClick(TeacherSchoolsModel item) {
                                        Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherMessageDatesScreen.class);
                                        inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                                        inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                                        inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                                        startActivity(inPrincipal);
                                    }
                                });

                        rvSchoolsList.hasFixedSize();
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherSchoolList.this);
                        rvSchoolsList.setLayoutManager(layoutManager);
                        rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherSchoolList.this, LinearLayoutManager.VERTICAL));
                        rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
                        rvSchoolsList.setAdapter(schoolsListAdapter);
                    }


                } catch (Exception e) {
                    Log.e("Count:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }
}