package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.adapter.VoiceHistoryAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.interfaces.VoiceHistoryListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EMERGENCY;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;


public class TeacherEmergencyVoice extends AppCompatActivity implements View.OnClickListener, VoiceHistoryListener {

    Button btnNext;
    Button btnToSections, btnToStudents;
    RelativeLayout rlVoicePreview;
    ImageView ivRecord;
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration, tvRecordDuration, tvRecordTitle, tvEmergTitle;
    RecyclerView rvSchoolsList;
    String tittletext, schoolId = "", staffId = "";
    private MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int recTime;
    MediaRecorder recorder;
    Handler recTimerHandler = new Handler();
    boolean bIsRecording = false;
    int iMediaDuration = 0;
    File futureStudioIconFile;
    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;
    TeacherSchoolsModel schoolsModel = null;
    //    boolean bIsGroupHead;
    private int iRequestCode;
    boolean bEmergency;
    int iMaxRecDur;
    TeacherSchoolsModel schoolmodel;
    String loginType;
    EditText et_tittle;

    Spinner spinnerList;
    NestedScrollView NestedScrollView;
    RelativeLayout rytVoiceHistory;
    Button btn_Select_receipients, btnSelectSchool;

    RecyclerView VoiceHistoryRecycle;
    Button btnHistorySlectReceipients, btnSlectSchool, btnSmsHistoryStandard, btnSmsHistorySectionOrStudents;

    String VoiceType = "";

    String SelectedStaffId = "";
    String SelectedSchoolId = "";
    JsonObject jsonObject;

    VoiceHistoryAdapter voiceHistoryAdapter;
    private ArrayList<MessageModel> voiceHistoryList = new ArrayList<>();
    private ArrayList<MessageModel> selectedVoiceList = new ArrayList<>();

    String FilePath = "";
    String duration = "";
    String description = "";

    LinearLayout selectSpinner;
    int count = 0;

    RadioGroup VoiceRadio;
    RadioButton radioRecordVoice, radioVoiceHistory;

    String isEmergencyVoice="";
    TextView emergVoice_tvTitle;
    Button btnStaffGroups;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_emergency_voice);


        bEmergency = getIntent().getExtras().getBoolean("EMERGENCY", true);
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        if ((listschooldetails.size() == 1)) {
            schoolId = TeacherUtil_Common.Principal_SchoolId;
            staffId = TeacherUtil_Common.Principal_staffId;
        } else {
            schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
            staffId = getIntent().getExtras().getString("STAFF_ID", "");
        }
        Log.d("iRequestCodevoice", String.valueOf(iRequestCode));
        schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);
        et_tittle = (EditText) findViewById(R.id.emergVoice_txtTitle);
        rlVoicePreview = (RelativeLayout) findViewById(R.id.emergVoice_rlPlayPreview);
        ivRecord = (ImageView) findViewById(R.id.emergVoice_ivRecord);
        ivRecord.setOnClickListener(this);
        tvRecordDuration = (TextView) findViewById(R.id.emergVoice_tvRecDuration);
        tvRecordTitle = (TextView) findViewById(R.id.emergVoice_tvRecTitle);
        tvEmergTitle = (TextView) findViewById(R.id.emergVoice_tvEmergenyTitle);
        emergVoice_tvTitle = (TextView) findViewById(R.id.emergVoice_tvTitle);
        btnNext = (Button) findViewById(R.id.emergVoice_btnNext);
        btnNext.setOnClickListener(this);

        rvSchoolsList = (RecyclerView) findViewById(R.id.emergVoice_rvSchoolsList);
        rvSchoolsList.setNestedScrollingEnabled(false);
        rlVoicePreview.setVisibility(View.GONE);
        rvSchoolsList.setVisibility(View.GONE);
        btnNext.setEnabled(false);

        btnToSections = (Button) findViewById(R.id.emergVoice_btnToSections);
        btnToSections.setOnClickListener(this);
        btnToStudents = (Button) findViewById(R.id.emergVoice_btnToStudents);
        btnToStudents.setOnClickListener(this);

        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);
        rlVoicePreview.setVisibility(View.GONE);

        btnStaffGroups = (Button) findViewById(R.id.btnStaffGroups);
        btnStaffGroups.setOnClickListener(this);
        btnStaffGroups.setEnabled(false);


        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherEmergencyVoice.this);
        if(countryID.equals("11")){
            btnToSections.setText("To Grade or Sections");
        }

        VoiceHistoryRecycle = (RecyclerView) findViewById(R.id.VoiceHistoryRecycle);
        spinnerList = (Spinner) findViewById(R.id.spinnerList);
        NestedScrollView = (NestedScrollView) findViewById(R.id.ComposeMessgeNested);
        rytVoiceHistory = (RelativeLayout) findViewById(R.id.rytVoiceHistory);
        btn_Select_receipients = (Button) findViewById(R.id.btn_Select_receipients);
        btnSelectSchool = (Button) findViewById(R.id.btnSelectSchool);
        selectSpinner = (LinearLayout) findViewById(R.id.selectSpinner);


        VoiceRadio = (RadioGroup) findViewById(R.id.VoiceRadio);
        radioRecordVoice = (RadioButton) findViewById(R.id.radioRecordVoice);
        radioVoiceHistory = (RadioButton) findViewById(R.id.radioVoiceHistory);

        btn_Select_receipients.setEnabled(false);
        btnSelectSchool.setEnabled(false);

        if (iRequestCode == PRINCIPAL_VOICE || iRequestCode == PRINCIPAL_EMERGENCY ||
                iRequestCode == STAFF_VOICE || iRequestCode == GH_EMERGENCY || iRequestCode == GH_VOICE) {
            selectSpinner.setVisibility(View.VISIBLE);

        }

        btnHistorySlectReceipients = (Button) findViewById(R.id.btnHistorySlectReceipients);
        btnSlectSchool = (Button) findViewById(R.id.btnSlectSchool);
        btnSmsHistoryStandard = (Button) findViewById(R.id.btnSmsHistoryStandard);
        btnSmsHistorySectionOrStudents = (Button) findViewById(R.id.btnSmsHistorySectionOrStudents);

        btnSmsHistoryStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = 0;
                pickFilepathFromVoiceHistory(0);
                if (count > 0) {
                    Intent intoSec = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("FILEPATH", FilePath);
                    intoSec.putExtra("DURATION", duration);
                    intoSec.putExtra("TITTLE", description);
                    intoSec.putExtra("TO", "SEC");
                    intoSec.putExtra("VOICE", "VoiceHistory");
                    startActivityForResult(intoSec, iRequestCode);
                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }
            }
        });

        btnSmsHistorySectionOrStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = 0;
                pickFilepathFromVoiceHistory(0);
                if (count > 0) {

                    Intent intoStu = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
                    intoStu.putExtra("REQUEST_CODE", iRequestCode);
                    intoStu.putExtra("DURATION", duration);
                    intoStu.putExtra("FILEPATH", FilePath);
                    intoStu.putExtra("TITTLE", description);
                    intoStu.putExtra("SCHOOL_ID", "");
                    intoStu.putExtra("TO", "STU");
                    intoStu.putExtra("VOICE", "VoiceHistory");
                    startActivityForResult(intoStu, iRequestCode);

                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }
            }
        });
        btnHistorySlectReceipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                pickFilepathFromVoiceHistory(0);

                if (count > 0) {

                    Intent inPrincipal = new Intent(TeacherEmergencyVoice.this, SendToVoiceSpecificSection.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                    TeacherUtil_Common.Principal_SchoolId = schoolId;
                    TeacherUtil_Common.Principal_staffId = staffId;
                    inPrincipal.putExtra("SCHOOL_ID", schoolId);
                    inPrincipal.putExtra("STAFF_ID", staffId);
                    inPrincipal.putExtra("FILEPATH", FilePath);
                    inPrincipal.putExtra("DURATION", duration);
                    inPrincipal.putExtra("TITTLE", description);
                    inPrincipal.putExtra("VOICE", "VoiceHistory");
                    Log.d("tittle", description);
                    startActivityForResult(inPrincipal, iRequestCode);
                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }

            }
        });

        btnSelectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                selectVoiceType();

                String strtittle = et_tittle.getText().toString();
                Intent schoolslist = new Intent(TeacherEmergencyVoice.this, ScoolsList.class);
                schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
                schoolslist.putExtra("schools", VoiceType);
                schoolslist.putExtra("FILEPATH", String.valueOf(futureStudioIconFile));
                schoolslist.putExtra("DURATION", String.valueOf(iMediaDuration));
                schoolslist.putExtra("TITTLE", strtittle);
                startActivity(schoolslist);

            }
        });

        btnSlectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVoiceType();
                count = 0;
                pickFilepathFromVoiceHistory(0);


                if (count > 0) {

                    Intent schoolslist = new Intent(TeacherEmergencyVoice.this, ScoolsList.class);
                    schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
                    schoolslist.putExtra("schools", VoiceType);
                    schoolslist.putExtra("FILEPATH", FilePath);
                    schoolslist.putExtra("DURATION", duration);
                    schoolslist.putExtra("TITTLE", description);
                    schoolslist.putExtra("VOICE", "VoiceHistory");
                    startActivity(schoolslist);

                } else {

                    showAlertMessage(getResources().getString(R.string.select_atleast_one_school));
                }


            }
        });


        btn_Select_receipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strtittle = et_tittle.getText().toString();
                Intent inPrincipal = new Intent(TeacherEmergencyVoice.this, SendToVoiceSpecificSection.class);
                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                TeacherUtil_Common.Principal_SchoolId = schoolId;
                TeacherUtil_Common.Principal_staffId = staffId;
                inPrincipal.putExtra("SCHOOL_ID", schoolId);
                inPrincipal.putExtra("STAFF_ID", staffId);
                inPrincipal.putExtra("FILEPATH", String.valueOf(futureStudioIconFile));
                inPrincipal.putExtra("DURATION", String.valueOf(iMediaDuration));
                inPrincipal.putExtra("TITTLE", strtittle);
                Log.d("tittle", strtittle);
                Log.d("filepath", String.valueOf(futureStudioIconFile));
                startActivityForResult(inPrincipal, iRequestCode);


            }
        });

        String[] list = {getResources().getString(R.string.voice_compose_msg), getResources().getString(R.string.voice_history)};


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(TeacherEmergencyVoice.this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerList.setAdapter(adapter1);
        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItemText = (String) parent.getItemAtPosition(position);
                Log.d("selectedvalue", selectedItemText);

                if (position == 0) {
                    NestedScrollView.setVisibility(View.VISIBLE);
                    rytVoiceHistory.setVisibility(View.GONE);

                    selectedViewButtons();

                } else if (position == 1) {
                    NestedScrollView.setVisibility(View.GONE);
                    rytVoiceHistory.setVisibility(View.VISIBLE);
                    selectedViewButtons();

                    voiceHistoryAdapter = new VoiceHistoryAdapter(TeacherEmergencyVoice.this, TeacherEmergencyVoice.this, voiceHistoryList);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEmergencyVoice.this);
                    VoiceHistoryRecycle.setLayoutManager(layoutManager);
                    VoiceHistoryRecycle.addItemDecoration(new DividerItemDecoration(TeacherEmergencyVoice.this, LinearLayoutManager.VERTICAL));
                    VoiceHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
                    VoiceHistoryRecycle.setAdapter(voiceHistoryAdapter);

                    VoiceHistoryApi();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        radioRecordVoice.setChecked(true);

        NestedScrollView.setVisibility(View.VISIBLE);
        rytVoiceHistory.setVisibility(View.GONE);

        selectedViewButtons();

        VoiceRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioRecordVoice:

                        NestedScrollView.setVisibility(View.VISIBLE);
                        rytVoiceHistory.setVisibility(View.GONE);

                        selectedViewButtons();
                        break;
                    case R.id.radioVoiceHistory:

                        NestedScrollView.setVisibility(View.GONE);
                        rytVoiceHistory.setVisibility(View.VISIBLE);
                        selectedViewButtons();

                        voiceHistoryAdapter = new VoiceHistoryAdapter(TeacherEmergencyVoice.this, TeacherEmergencyVoice.this, voiceHistoryList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEmergencyVoice.this);
                        VoiceHistoryRecycle.setLayoutManager(layoutManager);
                        VoiceHistoryRecycle.addItemDecoration(new DividerItemDecoration(TeacherEmergencyVoice.this, LinearLayoutManager.VERTICAL));
                        VoiceHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
                        VoiceHistoryRecycle.setAdapter(voiceHistoryAdapter);

                        VoiceHistoryApi();

                        break;

                }
            }
        });


        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == PRINCIPAL_VOICE_HW) {
            TeacherUtil_Common.Principal_SchoolId = schoolId;
            TeacherUtil_Common.Principal_staffId = staffId;
        }
        ImageView ivBack = (ImageView) findViewById(R.id.emergVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this);
        Log.d("Logintype", loginType);

        if (bEmergency) {
            iMaxRecDur = TeacherUtil_Common.maxEmergencyvoicecount;// 31; // 30 seconds
            tvEmergTitle.setText(getText(R.string.teacher_txt_emergency_title));
        } else if (iRequestCode == PRINCIPAL_VOICE_HW || iRequestCode == STAFF_VOICE_HW) {
            emergVoice_tvTitle.setText("Record Homework");
            iMaxRecDur = TeacherUtil_Common.maxHWVoiceDuration;// 181; // 3 mins
            tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));
        } else {
            iMaxRecDur = TeacherUtil_Common.maxGeneralvoicecount;// 181; // 3 mins
            tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));
        }


        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            listSchoolsAPI();
            btnToSections.setVisibility(View.GONE);
            btnToStudents.setVisibility(View.GONE);
        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            if (iRequestCode == PRINCIPAL_EXAM_TEST) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            } else if (iRequestCode == PRINCIPAL_VOICE_HW) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
            } else {
                listSchoolsAPI();
                if (bEmergency) {
                    btnToSections.setVisibility(View.GONE);
                    btnToStudents.setVisibility(View.GONE);
                } else {

                    btnNext.setVisibility(View.GONE);
                    rvSchoolsList.setVisibility(View.GONE);
                    btnToSections.setVisibility(View.GONE);
                    btnToStudents.setVisibility(View.GONE);
                }
            }
        } else if (loginType.equals(LOGIN_TYPE_TEACHER)) {
            if (iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
            } else {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnStaffGroups.setVisibility(View.VISIBLE);
            }
        }

        setupAudioPlayer();
    }



    private void showAlertMessage(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherEmergencyVoice.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);


        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
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

    }

    private void selectVoiceType() {
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_PRINCIPAL)) {
            if (iRequestCode == PRINCIPAL_EMERGENCY) {
                VoiceType = "PrincipalEmergency";
            }
        }
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_HEAD)) {
            if (iRequestCode == GH_VOICE) {
                VoiceType = "groupheadvoice";
            }
            if (iRequestCode == GH_EMERGENCY) {
                VoiceType = "groupHeadEmergency";
            }
        }
    }

    private void pickFilepathFromVoiceHistory(int a) {
        for (int i = 0; i < selectedVoiceList.size(); i++) {

            MessageModel data = selectedVoiceList.get(i);
            if (data.getSelectedStatus()) {
                count = a + 1;
                FilePath = data.getMsgReadStatus();
                duration = data.getMsgTime();
                description = data.getMsgdescription();

            }
        }
    }

    private void VoiceHistoryApi() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherEmergencyVoice.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherEmergencyVoice.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this);
        SelectedSchoolId="";
        SelectedStaffId="";

        if(iRequestCode==PRINCIPAL_EMERGENCY||iRequestCode==GH_EMERGENCY){
            isEmergencyVoice="1";
        }
        else {
            isEmergencyVoice="0";
        }

        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            for (int i = 0; i < listschooldetails.size(); i++) {
                String schoolId = listschooldetails.get(i).getStrSchoolID();
                String StaffId = listschooldetails.get(i).getStrStaffID();
                SelectedSchoolId = SelectedSchoolId + schoolId + "~";
                SelectedStaffId = SelectedStaffId + StaffId + "~";
            }
            String staffid = SelectedStaffId.substring(0, SelectedStaffId.length() - 1);
            String schoolid = SelectedSchoolId.substring(0, SelectedSchoolId.length() - 1);
            jsonObject = new JsonObject();
            jsonObject.addProperty("StaffID", staffid);
            jsonObject.addProperty("SchoolId", schoolid);
            jsonObject.addProperty("isEmergency", isEmergencyVoice);

        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            if (iRequestCode == PRINCIPAL_EMERGENCY) {
                for (int i = 0; i < listschooldetails.size(); i++) {
                    String schoolId = listschooldetails.get(i).getStrSchoolID();
                    String StaffId = listschooldetails.get(i).getStrStaffID();

                    SelectedSchoolId = SelectedSchoolId + schoolId + "~";
                    SelectedStaffId = SelectedStaffId + StaffId + "~";
                }
                String staffid = SelectedStaffId.substring(0, SelectedStaffId.length() - 1);
                String schoolid = SelectedSchoolId.substring(0, SelectedSchoolId.length() - 1);
                jsonObject = new JsonObject();
                jsonObject.addProperty("StaffID", staffid);
                jsonObject.addProperty("SchoolId", schoolid);
                jsonObject.addProperty("isEmergency", isEmergencyVoice);
            } else {

                jsonObject = new JsonObject();
                jsonObject.addProperty("StaffID", staffId);
                jsonObject.addProperty("SchoolId", schoolId);
                jsonObject.addProperty("isEmergency", isEmergencyVoice);
            }

        } else {
            jsonObject = new JsonObject();
            jsonObject.addProperty("StaffID", staffId);
            jsonObject.addProperty("SchoolId", schoolId);
            jsonObject.addProperty("isEmergency", isEmergencyVoice);

        }
        Log.d("Request", jsonObject.toString());
        Call<JsonArray> call = apiService.GetVoiceHistory(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        JSONArray js = new JSONArray(response.body().toString());
                        voiceHistoryAdapter.clearAllData();
                        MessageModel data;

                        if (js.length() > 0) {
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String status = jsonObject.getString("Status");
                                String message = jsonObject.getString("Message");
                                if (status.equals("0")) {
                                    showSmsNotFount(message);
                                } else if (status.equals("-1")) {
                                    showSmsNotFount(message);
                                } else {

                                    String url = jsonObject.getString("URL");
                                    String filepath = jsonObject.getString("FilePath");
                                    String description = jsonObject.getString("Description");
                                    String date = jsonObject.getString("SentOn");
                                    String title = jsonObject.getString("SchoolID");
                                    String Id = jsonObject.getString("HeaderID");
                                    String duration = jsonObject.getString("Duration");
                                    data = new MessageModel(Id, title, url, filepath, date, duration, description,false);
                                    data.setSelectedStaus(false);
                                    voiceHistoryList.add(data);
                                }


                            }
                            voiceHistoryAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.no_records), Toast.LENGTH_SHORT).show();
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showSmsNotFount(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherEmergencyVoice.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                NestedScrollView.setVisibility(View.VISIBLE);
                rytVoiceHistory.setVisibility(View.GONE);

                radioRecordVoice.setChecked(true);

                dialog.cancel();


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void selectedViewButtons() {

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this);


        if (loginType.equals(LOGIN_TYPE_TEACHER)) {

            if (iRequestCode == STAFF_VOICE) {
                btnToSections.setVisibility(View.VISIBLE);
                btnToStudents.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                btn_Select_receipients.setVisibility(View.GONE);

                btnSmsHistoryStandard.setVisibility(View.VISIBLE);
                btnSmsHistorySectionOrStudents.setVisibility(View.VISIBLE);
                btnSlectSchool.setVisibility(View.GONE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            }

        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {

            if (iRequestCode == PRINCIPAL_VOICE) {
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btn_Select_receipients.setVisibility(View.VISIBLE);

                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.GONE);
                btnHistorySlectReceipients.setVisibility(View.VISIBLE);
            } else if (iRequestCode == PRINCIPAL_EMERGENCY) {
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnSelectSchool.setVisibility(View.VISIBLE);
                btn_Select_receipients.setVisibility(View.GONE);


                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.VISIBLE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            }

        } else if (loginType.equals(LOGIN_TYPE_HEAD)) {

            if (iRequestCode == GH_VOICE) {
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnSelectSchool.setVisibility(View.VISIBLE);
                btn_Select_receipients.setVisibility(View.GONE);


                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.VISIBLE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            } else if (iRequestCode == GH_EMERGENCY) {
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnSelectSchool.setVisibility(View.VISIBLE);
                btn_Select_receipients.setVisibility(View.GONE);


                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.VISIBLE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

        if (bIsRecording)
            stop_RECORD();
        backToResultActvity("SENT");
    }



    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

       // VoiceHistoryApi();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }

        if (bIsRecording)
            stop_RECORD();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_tittle.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.emergVoice_btnNext:

                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    if (bEmergency) {
                        Log.d("SelectedCount", "" + i_schools_count);
                        if (i_schools_count > 0) {

                            SendEmergencyVoicePrincipalAPI();

                        } else {
                            showToast(getResources().getString(R.string.teacher_Select_school));
                        }
                    }

                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_HEAD)) {
                    Log.d("SelectedCount", "" + i_schools_count);
                    if (i_schools_count > 0) {
                        if (iRequestCode == GH_EMERGENCY) {
                            SendEmergencyVoiceGroupheadAPI();
                        } else if (iRequestCode == GH_VOICE) {
                            SendEmergencynormalVoiceGroupheadAPI();
                        }

                    } else {
                        showToast(getResources().getString(R.string.teacher_Select_school));
                    }
                }
                break;

            case R.id.emergVoice_btnToSections:
                Intent intoSec = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
                String strtittle = et_tittle.getText().toString();
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("FILEPATH", String.valueOf(futureStudioIconFile.getPath()));
                intoSec.putExtra("DURATION", String.valueOf(iMediaDuration));
                intoSec.putExtra("TITTLE", strtittle);
                intoSec.putExtra("TO", "SEC");

                intoSec.putExtra("SCHOOL_ID", schoolId);
                intoSec.putExtra("STAFF_ID", staffId);

                startActivityForResult(intoSec, iRequestCode);
                break;

            case R.id.emergVoice_btnToStudents:
                Intent intoStu = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
                String strtittle1 = et_tittle.getText().toString();

                intoStu.putExtra("REQUEST_CODE", iRequestCode);
                intoStu.putExtra("DURATION", String.valueOf(iMediaDuration));
                intoStu.putExtra("FILEPATH", String.valueOf(futureStudioIconFile.getPath()));
                intoStu.putExtra("TITTLE", strtittle1);
                intoStu.putExtra("SCHOOL_ID", "");
                intoStu.putExtra("TO", "STU");
                startActivityForResult(intoStu, iRequestCode);
                break;


            case R.id.btnStaffGroups:
                Intent intoStu2 = new Intent(TeacherEmergencyVoice.this, ToStaffGroupList.class);
                String strtittle2 = et_tittle.getText().toString();
                intoStu2.putExtra("REQUEST_CODE", iRequestCode);
                intoStu2.putExtra("DURATION", String.valueOf(iMediaDuration));
                intoStu2.putExtra("FILEPATH", String.valueOf(futureStudioIconFile.getPath()));
                intoStu2.putExtra("TITTLE", strtittle2);
                intoStu2.putExtra("SCHOOL_ID", schoolId);
                intoStu2.putExtra("STAFF_ID", staffId);
                startActivityForResult(intoStu2, iRequestCode);
                break;

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.emergVoice_ivRecord:
                if (bIsRecording) {
                    stop_RECORD();
                }
                else {
                    ivRecord.setEnabled(false);
                    start_RECORD();

                }
                break;
        }
    }

    private void SendEmergencyVoicePrincipalAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("FILE_Path", futureStudioIconFile.getPath());
        Log.d("file", file.getName());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsprincipal();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);

                            } else {
                                showAlert(strMsg);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherEmergencyVoice.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();
                finish();
                onBackPressed();

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }


    private JsonObject constructJsonArrayMgtSchoolsprincipal() {
        String tittletext = et_tittle.getText().toString();
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("Description", tittletext);
            jsonObjectSchool.addProperty("isEmergency", "1");
            jsonObjectSchool.addProperty("Duration", String.valueOf(iMediaDuration));//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchool.addProperty("CallerType", "M");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                if (seletedschoollist.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolId = new JsonObject();
                    jsonObjectschoolId.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(seletedschoollist.size()));
                    Log.d("schoolid", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectschoolId.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolId);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchool.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchool;
    }

    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("FILE_Path", futureStudioIconFile.getPath());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);
                            } else {
                                showAlert(strMsg);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }


    private JsonObject constructJsonArrayMgtSchoolsGrouphead() {
        tittletext = et_tittle.getText().toString();
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("Description", tittletext);
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "1");
            jsonObjectSchoolgrouphead.addProperty("Duration", String.valueOf(iMediaDuration));//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                if (seletedschoollist.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(seletedschoollist.size()));
                    Log.d("schoolid", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchoolgrouphead;
    }

    private void SendEmergencynormalVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("FILE_Path", futureStudioIconFile.getPath());
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsvoiceGrouphead();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);
                            } else {
                                showAlert(strMsg);
                            }
                        }
                        else {
                            showToast(getResources().getString(R.string.no_records));
                        }

                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonObject constructJsonArrayMgtSchoolsvoiceGrouphead() {
        tittletext = et_tittle.getText().toString();
        JsonObject jsonObjectSchoolgrouphead = new JsonObject();
        try {
            jsonObjectSchoolgrouphead.addProperty("isEmergency", "0");
            jsonObjectSchoolgrouphead.addProperty("Description", tittletext);
            jsonObjectSchoolgrouphead.addProperty("Duration", String.valueOf(iMediaDuration));//getIntent().getExtras().getString("MEDIA_DURATION", "0")
            jsonObjectSchoolgrouphead.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                if (seletedschoollist.get(i).isSelectStatus()) {
                    JsonObject jsonObjectschoolIdgrouphead = new JsonObject();
                    jsonObjectschoolIdgrouphead.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                    Log.d("selectsize", String.valueOf(seletedschoollist.size()));
                    Log.d("schoolid", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectschoolIdgrouphead.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                    jsonArrayschool.add(jsonObjectschoolIdgrouphead);
                }
            }

            Log.d("TTgroup", "1");
            jsonObjectSchoolgrouphead.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchoolgrouphead.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolgrouphead;
    }




    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void listSchoolsAPI() {
        Log.d("test1", "test1");
        i_schools_count = 0;
        Log.d("test2", "test2" + listschooldetails.size());
        for (int i = 0; i < listschooldetails.size(); i++) {
            Log.d("test3", "test3" + listschooldetails.size());
            TeacherSchoolsModel ss = listschooldetails.get(i);
            Log.d("test4", "test4");
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");
        }

        if (iRequestCode == PRINCIPAL_VOICE) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherEmergencyVoice.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            String strtittle = et_tittle.getText().toString();
                            Intent inPrincipal = new Intent(TeacherEmergencyVoice.this, SendToVoiceSpecificSection.class);

                            inPrincipal.putExtra("REQUEST_CODE", iRequestCode);

                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();

                            inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                            inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());

                            inPrincipal.putExtra("FILEPATH", String.valueOf(futureStudioIconFile));
                            inPrincipal.putExtra("DURATION", String.valueOf(iMediaDuration));
                            inPrincipal.putExtra("TITTLE", strtittle);
                            Log.d("tittle", strtittle);
                            Log.d("filepath", String.valueOf(futureStudioIconFile));
                            startActivityForResult(inPrincipal, iRequestCode);
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEmergencyVoice.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherEmergencyVoice.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        } else {
            TeacherSchoolsListAdapter schoolsListAdapter =
                    new TeacherSchoolsListAdapter(TeacherEmergencyVoice.this, new TeacherOnCheckSchoolsListener() {
                        @Override
                        public void school_addSchool(TeacherSchoolsModel school) {
                            if ((school != null) && (!seletedschoollist.contains(school))) {
                                seletedschoollist.add(school);
                                i_schools_count++;
                            }
                        }

                        @Override
                        public void school_removeSchool(TeacherSchoolsModel school) {
                            if ((school != null) && (seletedschoollist.contains(school))) {
                                seletedschoollist.remove(school);
                                i_schools_count--;
                            }
                        }
                    }, arrSchoolList);

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEmergencyVoice.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherEmergencyVoice.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
    }

    private void enableDisableNext() {
        Log.d("i_students_count", "count: " + i_schools_count);

        if (i_schools_count > 0) {
            btnNext.setEnabled(true);
        } else {
            btnNext.setEnabled(false);
        }
    }



    private void start_RECORD() {
        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_stop);
        ivRecord.setImageResource(R.drawable.teacher_ic_stop);
        rlVoicePreview.setVisibility(View.GONE);
        rvSchoolsList.setVisibility(View.GONE);
        tvRecordTitle.setText(getText(R.string.teacher_txt_stop_record));
        if ((loginType.equals(LOGIN_TYPE_TEACHER)) || ((iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW || iRequestCode == STAFF_TEXT_EXAM || iRequestCode == PRINCIPAL_VOICE || iRequestCode == STAFF_VOICE || iRequestCode == PRINCIPAL_VOICE_HW))) {
            btnToSections.setEnabled(false);
            btnToStudents.setEnabled(false);
            btnStaffGroups.setEnabled(false);

            btnSelectSchool.setEnabled(false);
            btn_Select_receipients.setEnabled(false);

        } else btnNext.setEnabled(false);

        btnSelectSchool.setEnabled(false);
        btn_Select_receipients.setEnabled(false);

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            //recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(256000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(getRecFilename());
            recorder.prepare();
            recorder.start();


            recTimeUpdation();

            bIsRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("AudioException", String.valueOf(e));
        }
    }

    private void stop_RECORD() {
        recorder.stop();
        recTimerHandler.removeCallbacks(runson);
        bIsRecording = false;
        tvRecordTitle.setText(getText(R.string.teacher_txt_start_record));
        if (loginType.equals(LOGIN_TYPE_TEACHER) || (iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW || iRequestCode == STAFF_TEXT_EXAM || iRequestCode == PRINCIPAL_VOICE || iRequestCode == STAFF_VOICE || iRequestCode == PRINCIPAL_VOICE_HW)) {
            btnToSections.setEnabled(true);
            btnToStudents.setEnabled(true);
            btnStaffGroups.setEnabled(true);

            btnSelectSchool.setEnabled(true);
            btn_Select_receipients.setEnabled(true);

        } else btnNext.setEnabled(true);

        btnSelectSchool.setEnabled(true);
        btn_Select_receipients.setEnabled(true);

        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
        ivRecord.setImageResource(R.drawable.teacher_ic_mic);


        if(!tvRecordDuration.getText().toString().equals("00:00")){
            rlVoicePreview.setVisibility(View.VISIBLE);
        }


        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            rvSchoolsList.setVisibility(View.GONE);
        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            rvSchoolsList.setVisibility(View.GONE);
        } else {
            rvSchoolsList.setVisibility(View.GONE);
        }

        fetchSong();
    }

    private String getRecFilename() {

        String filepath;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            //filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            filepath=getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

        }
        else{
            filepath = Environment.getExternalStorageDirectory().getPath();
        }

        File fileDir = new File(filepath, VOICE_FOLDER_NAME);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File fileNamePath = new File(fileDir, VOICE_FILE_NAME);
        Log.d("FILE_PATH", fileNamePath.getPath());
        return (fileNamePath.getPath()); //+ System.currentTimeMillis()

    }

    public void recTimeUpdation() {
        recTime = 1;
        recTimerHandler.postDelayed(runson, 1000);

    }

    private Runnable runson = new Runnable() {
        @Override
        public void run() {
            tvRecordDuration.setText(milliSecondsToTimer(recTime * 1000));
            if(!tvRecordDuration.getText().toString().equals("00:00")){
                ivRecord.setEnabled(true);
            }

            recTime = recTime + 1;
            if (recTime != iMaxRecDur)
                recTimerHandler.postDelayed(this, 1000);
            else
                stop_RECORD();
        }
    };

    public void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {

            String filepath;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
               // filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                filepath=getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

            }
            else{
                filepath = Environment.getExternalStorageDirectory().getPath();
            }

            File file = new File(filepath, VOICE_FOLDER_NAME);
            File dir = new File(file.getAbsolutePath());

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

            futureStudioIconFile = new File(dir, VOICE_FILE_NAME);
            System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(futureStudioIconFile.getPath());
            mediaPlayer.prepare();
            iMediaDuration = (int) (mediaPlayer.getDuration() / 1000.0);
//            seekBar.setProgress(0);
//            seekBar.setMax(99);

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }


    private void setupAudioPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
                mediaPlayer.seekTo(0);
            }
        });

        seekBar.setMax(99); // It means 100% .0-99
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myAudioPlayer_seekBar) {
//                    if (holder.mediaPlayer.isPlaying())
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
//                        Log.d("Position: ", ""+playPositionInMillisecconds);
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });
    }

    private void recVoicePlayPause() {

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_pause);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_clr_red));
        } else {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }

        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds);
    }

    private void primarySeekBarProgressUpdater(final int fileLength) {
        int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
        seekBar.setProgress(iProgress); // This math construction give a percentage of "was playing"/"song length"

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    tvPlayDuration.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    primarySeekBarProgressUpdater(fileLength);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    @Override
    public void voiceHistoryAddList(MessageModel contact) {
        selectedVoiceList.add(contact);
    }

    @Override
    public void voiceHistoryRemoveList(MessageModel contact) {
        selectedVoiceList.remove(contact);

    }
}
