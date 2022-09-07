package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.assignment.ImageAssignmentActivity;
import com.vs.schoolmessenger.assignment.MessageAssignmentActivity;
import com.vs.schoolmessenger.assignment.PdfAssignmentActivity;
import com.vs.schoolmessenger.assignment.VideoPrincipalRecipient;
import com.vs.schoolmessenger.assignment.ViewAssignment;
import com.vs.schoolmessenger.assignment.VoiceAssignmentActivity;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ABSENTEES;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ATTENDANCE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_CHAT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MESSAGESFROMMANAGEMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_SCHOOLSTRENGTH;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VIDEO_GALLERY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherSchoolList extends AppCompatActivity {
    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    RecyclerView rvSchoolsList;
    private int i_schools_count = 0;
    private int iRequestCode;
    String SchoolId;
    TeacherSchoolListForPrincipalAdapter schoolsListAdapter;
    boolean singleschoollogin = false;

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
        Log.d("Requestcode", String.valueOf(iRequestCode));
        rvSchoolsList = (RecyclerView) findViewById(R.id.schoolList_rvSchoolsList);
        ImageView ivBack = (ImageView) findViewById(R.id.schoollist_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listSchoolsAPI();

    }

    private void listSchoolsAPI() {
        Log.d("test1", "test1");
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            Log.d("test4", "test4");
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true,ss.getBookEnable(),ss.getOnlineLink(),ss.getIsPaymentPending());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");


        }

        if (iRequestCode == PRINCIPAL_ABSENTEES) {
            schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherAbsenteesReport.class);
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
        if (iRequestCode == PRINCIPAL_ATTENDANCE) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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

        if(iRequestCode == PRINCIPAL_MEETING_URL || iRequestCode == STAFF_MEETING_URL){
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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

        if(iRequestCode == PRINCIPAL_MESSAGESFROMMANAGEMENT){
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            Intent inPrincipal = new Intent(TeacherSchoolList.this, TeacherMessageDatesScreen.class);
                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                            startActivity(inPrincipal);
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
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId=item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId=item.getStrStaffID();

                            if(Type.equals("Message")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, MessageAssignmentActivity.class);
                                startActivity(inPrincipal);
                            }
                            else if(Type.equals("Voice")){
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, VoiceAssignmentActivity.class);
                                startActivity(inPrincipal);
                            }
                            else if(Type.equals("Image")){
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, ImageAssignmentActivity.class);
                                startActivity(inPrincipal);
                            }
                            else if(Type.equals("Pdf")){
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, PdfAssignmentActivity.class);
                                startActivity(inPrincipal);
                            }
                            else if(Type.equals("View")){
                                ViewAssignment fragment1 = new ViewAssignment();
                                FragmentTransaction ft = getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
                                ft.detach(fragment1);
                                ft.attach(fragment1);
                                Bundle bundle = new Bundle();
                                bundle.putString("view","1");
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

        if(iRequestCode==VIDEO_GALLERY){
            final String Type = getIntent().getExtras().getString("Type", "");
            final String title = getIntent().getExtras().getString("TITLE", "");
            final String content = getIntent().getExtras().getString("CONTENT", "");
            final String filepath = getIntent().getExtras().getString("FILEPATH", "");
            final String size = getIntent().getExtras().getString("FILE_SIZE", "");

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId=item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId=item.getStrStaffID();

                            if(Type.equals("Recipient")) {
                                Intent inPrincipal = new Intent(TeacherSchoolList.this, VideoPrincipalRecipient.class);
                                inPrincipal.putExtra("REQUEST_CODE", VIDEO_GALLERY);

                                inPrincipal.putExtra("TITLE",title);
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
        if(iRequestCode==PRINCIPAL_CHAT){

            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId=item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId=item.getStrStaffID();

                               Intent inPrincipal = new Intent(TeacherSchoolList.this, StaffDetailListActivity.class);
                                inPrincipal.putExtra("REQUEST_CODE", PRINCIPAL_CHAT);
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







        if(iRequestCode==PRINCIPAL_TEXT){

            generealTextScreen();

            }

        if(iRequestCode==STAFF_TEXT){
            generealTextScreen();


            }



        if(iRequestCode==STAFF_VOICE){
            voiceScreen();
            }

        if(iRequestCode==PRINCIPAL_VOICE){

            voiceScreen();
            }





    }

    private void generealTextScreen() {
        TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
                new TeacherSchoolListForPrincipalAdapter(TeacherSchoolList.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
//        backToResultActvity("SENT");
        finish();
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
//        returnIntent.putExtra("STD_SEC", stdSec);
        setResult(iRequestCode, returnIntent);
        finish();
    }
}