package com.vs.schoolmessenger.activity;

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
import static com.vs.schoolmessenger.util.Util_Common.isSelectedDate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.CallScheduleAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.adapter.VoiceHistoryAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.interfaces.UpdatesListener;
import com.vs.schoolmessenger.interfaces.VoiceHistoryListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherEmergencyVoice extends AppCompatActivity implements OnSelectDateListener, View.OnClickListener, VoiceHistoryListener {

    private static final int PICK_AUDIO_REQUEST = 1;
    private final ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private final ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private final int PICK_AUDIO = 1;
    Button btnNext;
    Button btnToSections, btnToStudents;
    RelativeLayout rlVoicePreview, rlyScheduleCall, rlyScheduleCallschedule;
    ImageView ivRecord;
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration, tvRecordDuration, tvRecordTitle, tvEmergTitle;
    RecyclerView rvSchoolsList;
    String tittletext, schoolId = "", staffId = "";
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int recTime;
    MediaRecorder recorder;
    Handler recTimerHandler = new Handler();
    boolean bIsRecording = false;
    int iMediaDuration = 0;
    RadioGroup rdoGroup, rdoGroupschedule;
    int Time = 0;
    File futureStudioIconFile;
    boolean bEmergency;
    int iMaxRecDur;
    TeacherSchoolsModel schoolmodel;
    String loginType;
    EditText et_tittle;
    Spinner spinnerList;
    NestedScrollView NestedScrollView;
    RelativeLayout rytVoiceHistory, rlyPathname;
    Button btn_Select_receipients, btnSelectSchool;
    GridView isDateSchedule;
    GridView isDateScheduleschedule;
    RecyclerView VoiceHistoryRecycle;
    Button btnHistorySlectReceipients, btnSlectSchool, btnSmsHistoryStandard, btnSmsHistorySectionOrStudents;
    String VoiceType = "";
    String SelectedStaffId = "";
    String SelectedSchoolId = "";
    JsonObject jsonObject;
    VoiceHistoryAdapter voiceHistoryAdapter;
    String FilePath = "";
    String duration = "";
    String description = "";
    LinearLayout selectSpinner;
    int count = 0;
    RadioGroup VoiceRadio;
    RadioButton radioRecordVoice, radioVoiceHistory;
    String isEmergencyVoice = "";
    TextView emergVoice_tvTitle, lblPickMp3File, lblFilepath;
    Button btnStaffGroups;
    Button btnGHStandardGroups, btnGHHistoryStandardGroups;
    RelativeLayout header;
    CallScheduleAdapter isCallScheduleAdapter;
    List<String> isSelectedDateList = new ArrayList<>();
    List<String> isDifferentDates = new ArrayList<>();
    RadioButton rdoBtnSchedule, rdoBtnInstant, rdoBtnInstantschedule, rdoBtnScheduleschedule;
    PopupWindow popupWindow;
    CardView isDatePick;
    RelativeLayout rlyRecord;
    TextView lblTimePicking, lbldialbeyondTimePicking, lblTimePickingschedule, lbldialbeyondTimePickingschedule;
    RelativeLayout mgtText_rlHeader;
    CardView crdDailyCollectionschedule;
    private MediaPlayer mediaPlayer;
    private int i_schools_count = 0;
    private int iRequestCode;
    private final ArrayList<MessageModel> voiceHistoryList = new ArrayList<>();
    private final ArrayList<MessageModel> selectedVoiceList = new ArrayList<>();

    public static long getDateDifferenceInDays(int year, int month, int day) {
        // Get current date
        Calendar currentDate = Calendar.getInstance();
        // Set the specified date
        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.set(year, month - 1, day); // Month is zero-based
        // Calculate the difference in milliseconds
        long differenceInMillis = specifiedDate.getTimeInMillis() - currentDate.getTimeInMillis();
        // Convert milliseconds to days
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
        return differenceInDays;
    }

    public static int parseTimeToSeconds(String timeString) throws NumberFormatException {
        String[] parts = timeString.split(":");
        if (parts.length != 2) {
            throw new NumberFormatException("Invalid time format");
        }

        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);

        return minutes * 60 + seconds;
    }

    public static String formateMilliSeccond(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        return finalTimerString;
    }

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
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_TEACHER)) {
            schoolId = TeacherUtil_Common.Principal_SchoolId;
            staffId = TeacherUtil_Common.Principal_staffId;
        } else {
            schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
            staffId = getIntent().getExtras().getString("STAFF_ID", "");
        }

        Util_Common.isHistory = false;
        Log.d("iRequestCodevoice", String.valueOf(iRequestCode));
        schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        rlyRecord = (RelativeLayout) findViewById(R.id.rlyRecord);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);
        et_tittle = (EditText) findViewById(R.id.emergVoice_txtTitle);
        rlVoicePreview = (RelativeLayout) findViewById(R.id.emergVoice_rlPlayPreview);
        rlyScheduleCall = (RelativeLayout) findViewById(R.id.rlyScheduleCall);
        rlyScheduleCallschedule = (RelativeLayout) findViewById(R.id.rlyScheduleCallschedule);
        ivRecord = (ImageView) findViewById(R.id.emergVoice_ivRecord);
        lblPickMp3File = (TextView) findViewById(R.id.lblAddAudio);
        ivRecord.setOnClickListener(this);
        tvRecordDuration = (TextView) findViewById(R.id.emergVoice_tvRecDuration);
        tvRecordTitle = (TextView) findViewById(R.id.emergVoice_tvRecTitle);
        tvEmergTitle = (TextView) findViewById(R.id.emergVoice_tvEmergenyTitle);
        emergVoice_tvTitle = (TextView) findViewById(R.id.emergVoice_tvTitle);
        btnNext = (Button) findViewById(R.id.emergVoice_btnNext);
        lblFilepath = (TextView) findViewById(R.id.lblFilepath);
        rdoBtnSchedule = (RadioButton) findViewById(R.id.rdoBtnSchedule);
        rdoBtnScheduleschedule = (RadioButton) findViewById(R.id.rdoBtnScheduleschedule);
        rdoBtnInstant = (RadioButton) findViewById(R.id.rdoBtnInstant);
        rdoBtnInstantschedule = (RadioButton) findViewById(R.id.rdoBtnInstantschedule);
        header = (RelativeLayout) findViewById(R.id.header);
        rdoGroup = (RadioGroup) findViewById(R.id.rdoGroup);
        rdoGroupschedule = (RadioGroup) findViewById(R.id.rdoGroupschedule);
        mgtText_rlHeader = (RelativeLayout) findViewById(R.id.mgtText_rlHeader);
        crdDailyCollectionschedule = (CardView) findViewById(R.id.crdDailyCollectionschedule);
        btnNext.setOnClickListener(this);
        rdoBtnSchedule.setOnClickListener(this);
        rdoBtnScheduleschedule.setOnClickListener(this);
        rdoBtnInstant.setOnClickListener(this);
        rdoBtnInstantschedule.setOnClickListener(this);
        lblPickMp3File.setOnClickListener(this);

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
        btnGHStandardGroups = (Button) findViewById(R.id.btnGHStandardGroups);
        btnGHHistoryStandardGroups = (Button) findViewById(R.id.btnGHHistoryStandardGroups);
        btnStaffGroups.setOnClickListener(this);
        btnGHHistoryStandardGroups.setOnClickListener(this);
        btnGHStandardGroups.setOnClickListener(this);
        btnStaffGroups.setEnabled(false);
        btnGHStandardGroups.setEnabled(false);
        isDatePick = findViewById(R.id.crdDailyCollection);
        lblTimePicking = findViewById(R.id.lblTimePicking);
        lblTimePickingschedule = findViewById(R.id.lblTimePickingschedule);
        lbldialbeyondTimePickingschedule = findViewById(R.id.lbldialbeyondTimePickingschedule);
        lbldialbeyondTimePicking = findViewById(R.id.lbldialbeyondTimePicking);
        isDateSchedule = findViewById(R.id.isDateSchedule);
        isDateScheduleschedule = findViewById(R.id.isDateScheduleschedule);
        isDatePick.setOnClickListener(this);
        crdDailyCollectionschedule.setOnClickListener(this);
        lblTimePicking.setOnClickListener(this);
        lblTimePickingschedule.setOnClickListener(this);
        lbldialbeyondTimePickingschedule.setOnClickListener(this);
        lbldialbeyondTimePicking.setOnClickListener(this);

        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherEmergencyVoice.this);
        if (countryID.equals("11")) {
            btnToSections.setText(R.string.To_Grade_or_Sections);
        }

        VoiceHistoryRecycle = (RecyclerView) findViewById(R.id.VoiceHistoryRecycle);
        spinnerList = (Spinner) findViewById(R.id.spinnerList);
        NestedScrollView = (NestedScrollView) findViewById(R.id.ComposeMessgeNested);
        rytVoiceHistory = (RelativeLayout) findViewById(R.id.rytVoiceHistory);
        rlyPathname = (RelativeLayout) findViewById(R.id.rlyPathname);
        btn_Select_receipients = (Button) findViewById(R.id.btn_Select_receipients);
        btnSelectSchool = (Button) findViewById(R.id.btnSelectSchool);
        selectSpinner = (LinearLayout) findViewById(R.id.selectSpinner);

        VoiceRadio = (RadioGroup) findViewById(R.id.VoiceRadio);
        radioRecordVoice = (RadioButton) findViewById(R.id.radioRecordVoice);
        radioVoiceHistory = (RadioButton) findViewById(R.id.radioVoiceHistory);

        btn_Select_receipients.setEnabled(false);
        btnSelectSchool.setEnabled(false);

        if (iRequestCode == PRINCIPAL_VOICE || iRequestCode == PRINCIPAL_EMERGENCY || iRequestCode == STAFF_VOICE || iRequestCode == GH_EMERGENCY || iRequestCode == GH_VOICE) {
            selectSpinner.setVisibility(View.VISIBLE);
        }

        Log.d("isSchoolType", String.valueOf(Util_Common.isSchoolType));
        if (Util_Common.isSchoolType == 2 && !bEmergency) {
            rdoGroup.setVisibility(View.VISIBLE);
        } else {
            rdoGroup.setVisibility(View.GONE);
        }

        if (Util_Common.isSchoolType == 2 && !bEmergency) {
            rdoGroupschedule.setVisibility(View.VISIBLE);
        } else {
            rdoGroupschedule.setVisibility(View.GONE);
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
                    if (Util_Common.isScheduleCall) {
                        if (!isSelectedDateList.isEmpty()) {
                            if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                                if (!lbldialbeyondTimePickingschedule.getText().toString().isEmpty()) {
                                    btnSmsHistoryStandard();
                                } else {
                                    showAlertMessage(getResources().getString(R.string.please_select_datetime));
                                }
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        btnSmsHistoryStandard();
                    }
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
                    if (Util_Common.isScheduleCall) {
                        if (!isSelectedDateList.isEmpty()) {
                            if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                                if (!lbldialbeyondTimePickingschedule.getText().toString().isEmpty()) {
                                    btnSmsHistorySectionOrStudents();
                                } else {
                                    showAlertMessage(getResources().getString(R.string.please_select_datetime));
                                }
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        btnSmsHistorySectionOrStudents();
                    }
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
                Log.d("isHistorySelectCount_", String.valueOf(count));

                if (count > 0) {
                    if (Util_Common.isScheduleCall) {
                        if (!isSelectedDateList.isEmpty()) {
                            if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                                if (!lbldialbeyondTimePickingschedule.getText().toString().isEmpty()) {
                                    btnHistorySlectReceipients();
                                } else {
                                    showAlertMessage(getResources().getString(R.string.please_select_datetime));
                                }
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        btnHistorySlectReceipients();
                    }
                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }
            }
        });

        btnSelectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                btnSelectSchool();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    btnSelectSchool();
                }
            }
        });

        btnSlectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectVoiceType();
                count = 0;
                pickFilepathFromVoiceHistory(0);
                if (count > 0) {
                    if (Util_Common.isScheduleCall) {
                        if (!isSelectedDateList.isEmpty()) {
                            if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                                if (!lbldialbeyondTimePickingschedule.getText().toString().isEmpty()) {
                                    btnSlectSchool();
                                } else {
                                    showAlertMessage(getResources().getString(R.string.please_select_datetime));
                                }
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        btnSlectSchool();
                    }
                } else {
                    showAlertMessage(getResources().getString(R.string.select_atleast_one_school));
                }
            }
        });


        btn_Select_receipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                lbldialbeyondTimePicking();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    lbldialbeyondTimePicking();
                }
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
                        Util_Common.isHistory = false;


                        if (Util_Common.isSchoolType == 2 && !bEmergency) {
                            rdoGroup.setVisibility(View.VISIBLE);
                        } else {
                            rdoGroup.setVisibility(View.GONE);
                        }

                        if (Util_Common.isSchoolType == 2 && !bEmergency) {
                            rdoGroupschedule.setVisibility(View.VISIBLE);
                        } else {
                            rdoGroupschedule.setVisibility(View.GONE);
                        }

                        mgtText_rlHeader.setVisibility(View.VISIBLE);
                        rlyRecord.setVisibility(View.VISIBLE);
                        et_tittle.setVisibility(View.VISIBLE);
                        VoiceHistoryRecycle.setVisibility(View.GONE);
                        isSelectedDateList.clear();
                        isDifferentDates.clear();
                        isCallScheduleAdapter.notifyDataSetChanged();
                        NestedScrollView.setVisibility(View.VISIBLE);
                        rytVoiceHistory.setVisibility(View.GONE);
                        selectedViewButtons();
                        ViewGroup.LayoutParams layoutParams1 = isDateSchedule.getLayoutParams();
                        layoutParams1.height = 0;
                        isDateSchedule.setLayoutParams(layoutParams1);
                        break;

                    case R.id.radioVoiceHistory:

                        if (Util_Common.isSchoolType == 2 && !bEmergency) {
                            rdoGroup.setVisibility(View.VISIBLE);
                        } else {
                            rdoGroup.setVisibility(View.GONE);
                        }

                        if (Util_Common.isSchoolType == 2 && !bEmergency) {
                            rdoGroupschedule.setVisibility(View.VISIBLE);
                        } else {
                            rdoGroupschedule.setVisibility(View.GONE);
                        }


                        Util_Common.isHistory = true;
                        isSelectedDateList.clear();
                        isDifferentDates.clear();
                        ViewGroup.LayoutParams layoutParams = isDateScheduleschedule.getLayoutParams();
                        isCallScheduleAdapter = new CallScheduleAdapter((Context) TeacherEmergencyVoice.this, 1, (ArrayList<String>) isSelectedDateList, new UpdatesListener() {
                            @Override
                            public void onMsgItemClick(String name) {
                                if (isSelectedDateList.size() - 1 == 1 || isSelectedDateList.size() - 1 == 2) {
                                    layoutParams.height = 200;
                                } else if (isSelectedDateList.size() - 1 == 3 || isSelectedDateList.size() - 1 == 4) {
                                    layoutParams.height = 350;
                                } else if (isSelectedDateList.size() - 1 == 5 || isSelectedDateList.size() - 1 == 6) {
                                    layoutParams.height = 500;
                                } else if (isSelectedDateList.size() - 1 == 7) {
                                    layoutParams.height = 600;
                                } else {
                                    layoutParams.height = 0;
                                }
                                isDateSchedule.setLayoutParams(layoutParams);
                            }
                        });
                        layoutParams.height = 0;
                        isDateSchedule.setLayoutParams(layoutParams);
                        isDateSchedule.setAdapter((ListAdapter) isCallScheduleAdapter);

                        rdoGroup.setVisibility(View.VISIBLE);
                        mgtText_rlHeader.setVisibility(View.GONE);
                        rlyRecord.setVisibility(View.GONE);
                        et_tittle.setVisibility(View.GONE);
                        VoiceHistoryRecycle.setVisibility(View.VISIBLE);
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
                Util_Common.isHistory = false;
            }
        });


        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this);
        Log.d("Logintype", loginType);

        if (bEmergency) {
            iMaxRecDur = TeacherUtil_Common.maxEmergencyvoicecount; // 31; // 30 seconds
            tvEmergTitle.setText(getText(R.string.teacher_txt_emergency_title));
        } else if (iRequestCode == PRINCIPAL_VOICE_HW || iRequestCode == STAFF_VOICE_HW) {
            emergVoice_tvTitle.setText(R.string.Record_Homework);
            iMaxRecDur = TeacherUtil_Common.maxHWVoiceDuration; // 181; // 3 mins
            tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));
        } else {
            iMaxRecDur = TeacherUtil_Common.maxGeneralvoicecount;// 181; // 3 mins
            tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));
        }


        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            listSchoolsAPI();
            btnToSections.setVisibility(View.GONE);
            btnToStudents.setVisibility(View.GONE);

            if (iRequestCode == GH_VOICE) {
                btnGHStandardGroups.setVisibility(View.VISIBLE);
            }
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

    private void btnSmsHistorySectionOrStudents() {
        Intent intoStu = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
        intoStu.putExtra("REQUEST_CODE", iRequestCode);
        intoStu.putExtra("DURATION", duration);
        intoStu.putExtra("FILEPATH", FilePath);
        intoStu.putExtra("TITTLE", description);
        intoStu.putExtra("SCHOOL_ID", "");
        intoStu.putExtra("TO", "STU");
        intoStu.putExtra("VOICE", "VoiceHistory");
        startActivityForResult(intoStu, iRequestCode);
    }

    private void btnSmsHistoryStandard() {
        Intent intoSec = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
        intoSec.putExtra("REQUEST_CODE", iRequestCode);
        intoSec.putExtra("FILEPATH", FilePath);
        intoSec.putExtra("DURATION", duration);
        intoSec.putExtra("TITTLE", description);
        intoSec.putExtra("TO", "SEC");
        intoSec.putExtra("VOICE", "VoiceHistory");
        startActivityForResult(intoSec, iRequestCode);
    }

    private void btnSlectSchool() {


        Intent schoolslist = new Intent(TeacherEmergencyVoice.this, ScoolsList.class);
        schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
        schoolslist.putExtra("schools", VoiceType);
        schoolslist.putExtra("FILEPATH", FilePath);
        schoolslist.putExtra("DURATION", duration);
        schoolslist.putExtra("TITTLE", description);
        schoolslist.putExtra("VOICE", "VoiceHistory");
        startActivity(schoolslist);
    }

    private void btnHistorySlectReceipients() {
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
    }

    private void btnSelectSchool() {

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

    private void lbldialbeyondTimePicking() {
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

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherEmergencyVoice.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherEmergencyVoice.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this);
        SelectedSchoolId = "";
        SelectedStaffId = "";

        if (iRequestCode == PRINCIPAL_EMERGENCY || iRequestCode == GH_EMERGENCY) {
            isEmergencyVoice = "1";
        } else {
            isEmergencyVoice = "0";
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
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
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
                                    data = new MessageModel(Id, title, url, filepath, date, duration, description, false);
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
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
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

                if (Util_Common.isSchoolType == 2 && !bEmergency) {
                    rdoGroup.setVisibility(View.VISIBLE);
                } else {
                    rdoGroup.setVisibility(View.GONE);
                }

                if (Util_Common.isSchoolType == 2 && !bEmergency) {
                    rdoGroupschedule.setVisibility(View.VISIBLE);
                } else {
                    rdoGroupschedule.setVisibility(View.GONE);
                }

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
                btnGHHistoryStandardGroups.setVisibility(View.VISIBLE);

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
                btnGHHistoryStandardGroups.setVisibility(View.GONE);


                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.VISIBLE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();

        if (bIsRecording) stop_RECORD();
        backToResultActvity("SENT");

        Util_Common.isHistory = false;
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

        if (Util_Common.isSchoolType == 2 && !bEmergency) {
            rdoGroup.setVisibility(View.VISIBLE);
        } else {
            rdoGroup.setVisibility(View.GONE);
        }

        if (Util_Common.isSchoolType == 2 && !bEmergency) {
            rdoGroupschedule.setVisibility(View.VISIBLE);
        } else {
            rdoGroupschedule.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }

        if (bIsRecording) stop_RECORD();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_tittle.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.emergVoice_btnNext:
                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                emergVoice_btnNext();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    emergVoice_btnNext();
                }
                break;

            case R.id.emergVoice_btnToSections:
                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                emergVoice_btnToSections();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    emergVoice_btnToSections();
                }
                break;

            case R.id.emergVoice_btnToStudents:

                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                emergVoice_btnToStudents();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    emergVoice_btnToStudents();
                }
                break;


            case R.id.btnStaffGroups:

                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                btnStaffGroups();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    btnStaffGroups();
                }
                break;

            case R.id.btnGHStandardGroups:

                if (Util_Common.isScheduleCall) {
                    if (!isSelectedDateList.isEmpty()) {
                        if (!lblTimePicking.getText().toString().isEmpty()) {
                            if (!lbldialbeyondTimePicking.getText().toString().isEmpty()) {
                                btnGHStandardGroups();
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        showAlertMessage(getResources().getString(R.string.please_select_datetime));
                    }
                } else {
                    btnGHStandardGroups();
                }
                break;

            case R.id.btnGHHistoryStandardGroups:
                selectVoiceType();
                count = 0;
                pickFilepathFromVoiceHistory(0);
                String title = et_tittle.getText().toString();
                if (count > 0) {
                    if (Util_Common.isScheduleCall) {
                        if (!isSelectedDateList.isEmpty()) {
                            if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                                if (!lbldialbeyondTimePickingschedule.getText().toString().isEmpty()) {
                                    btnGHHistoryStandardGroups(title);
                                } else {
                                    showAlertMessage(getResources().getString(R.string.please_select_datetime));
                                }
                            } else {
                                showAlertMessage(getResources().getString(R.string.please_select_datetime));
                            }
                        } else {
                            showAlertMessage(getResources().getString(R.string.please_select_datetime));
                        }
                    } else {
                        btnGHHistoryStandardGroups(title);
                    }
                } else {
                    showAlertMessage(String.valueOf(R.string.Please_select_atleast_one_message));
                }
                break;

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.lblAddAudio:

                tvRecordDuration.setText("00:00");
                ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
                ivRecord.setImageResource(R.drawable.teacher_ic_mic);
                imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
                mediaPlayer.seekTo(0);
                isPickTheAudio();
                break;

            case R.id.emergVoice_ivRecord:
                if (bIsRecording) {
                    stop_RECORD();
                    rlyPathname.setVisibility(View.GONE);
                } else {
                    ivRecord.setEnabled(false);
                    imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                    imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
                    mediaPlayer.seekTo(0);
                    rlyPathname.setVisibility(View.GONE);
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                    start_RECORD();
                }
                break;

            case R.id.rdoBtnSchedule:
                if (rdoBtnSchedule.isChecked()) {
                    isSelectedDateList.clear();
                    rlyScheduleCall.setVisibility(View.VISIBLE);
                    Util_Common.isScheduleCall = true;
                }
                break;

            case R.id.rdoBtnScheduleschedule:
                if (rdoBtnScheduleschedule.isChecked()) {
                    isSelectedDateList.clear();
                    rlyScheduleCallschedule.setVisibility(View.VISIBLE);
                    Util_Common.isScheduleCall = true;
                }
                break;


            case R.id.rdoBtnInstant:
                if (rdoBtnInstant.isChecked()) {
                    isSelectedDateList.clear();
                    rlyScheduleCall.setVisibility(View.GONE);
                    Util_Common.isScheduleCall = false;


                    if (Util_Common.isSchoolType == 2 && !bEmergency) {
                        rdoGroup.setVisibility(View.VISIBLE);
                    } else {
                        rdoGroup.setVisibility(View.GONE);
                    }

                    if (Util_Common.isSchoolType == 2 && !bEmergency) {
                        rdoGroupschedule.setVisibility(View.VISIBLE);
                    } else {
                        rdoGroupschedule.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.rdoBtnInstantschedule:

                if (rdoBtnInstantschedule.isChecked()) {
                    isSelectedDateList.clear();
                    rlyScheduleCallschedule.setVisibility(View.GONE);
                    Util_Common.isScheduleCall = false;

                    if (Util_Common.isSchoolType == 2 && !bEmergency) {
                        rdoGroup.setVisibility(View.VISIBLE);
                    } else {
                        rdoGroup.setVisibility(View.GONE);
                    }

                    if (Util_Common.isSchoolType == 2 && !bEmergency) {
                        rdoGroupschedule.setVisibility(View.VISIBLE);
                    } else {
                        rdoGroupschedule.setVisibility(View.GONE);
                    }
                }
                break;

            case R.id.crdDailyCollection:

                final ColorDrawable[] colors = {new ColorDrawable(Color.WHITE), new ColorDrawable(Color.GRAY)};
                final TransitionDrawable transitionDrawable = new TransitionDrawable(colors);
                isDatePick.setBackground(transitionDrawable);
                transitionDrawable.startTransition(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        transitionDrawable.reverseTransition(100);
                        openManyDaysPicker();
                    }
                }, 100);
                break;

            case R.id.crdDailyCollectionschedule:
                final ColorDrawable[] color = {new ColorDrawable(Color.WHITE), new ColorDrawable(Color.GRAY)};
                final TransitionDrawable transitionDrawables = new TransitionDrawable(color);
                crdDailyCollectionschedule.setBackground(transitionDrawables);
                transitionDrawables.startTransition(100);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        transitionDrawables.reverseTransition(100);
                        openManyDaysPicker();
                    }
                }, 100);
                break;

            case R.id.lblTimePicking:
                Time = 1;
                isTimePicking(lblTimePicking);
                break;

            case R.id.lbldialbeyondTimePicking:
                Time = 2;
                isTimePicking(lbldialbeyondTimePicking);
                break;

            case R.id.lblTimePickingschedule:
                Time = 3;
                isTimePicking(lblTimePickingschedule);
                break;

            case R.id.lbldialbeyondTimePickingschedule:
                Time = 4;
                isTimePicking(lbldialbeyondTimePickingschedule);
                break;
        }
    }

    private void btnGHHistoryStandardGroups(String title) {
        Intent schoolslist = new Intent(TeacherEmergencyVoice.this, TeacherSchoolList.class);
        schoolslist.putExtra("REQUEST_CODE", GH_VOICE);
        schoolslist.putExtra("EMERGENCY", false);
        schoolslist.putExtra("SCHOOL_ID", schoolId);
        schoolslist.putExtra("STAFF_ID", staffId);
        schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
        schoolslist.putExtra("schools", VoiceType);
        schoolslist.putExtra("FILEPATH", FilePath);
        schoolslist.putExtra("DURATION", duration);
        schoolslist.putExtra("TITTLE", title);
        schoolslist.putExtra("VOICE", "VoiceHistory");
        startActivity(schoolslist);
    }

    private void emergVoice_btnNext() {

        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_PRINCIPAL)) {
            if (bEmergency) {
                Log.d("SelectedCount", String.valueOf(i_schools_count));
                if (i_schools_count > 0) {
                    SendEmergencyVoicePrincipalAPI();
                } else {
                    showToast(getResources().getString(R.string.teacher_Select_school));
                }
            }

        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherEmergencyVoice.this).equals(LOGIN_TYPE_HEAD)) {
            Log.d("SelectedCount", String.valueOf(i_schools_count));
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

    }

    private void btnGHStandardGroups() {

        String strtittle3 = et_tittle.getText().toString();
        Intent inVoice = new Intent(TeacherEmergencyVoice.this, TeacherSchoolList.class);
        inVoice.putExtra("REQUEST_CODE", GH_VOICE);
        inVoice.putExtra("EMERGENCY", false);
        inVoice.putExtra("SCHOOL_ID", schoolId);
        inVoice.putExtra("STAFF_ID", staffId);
        inVoice.putExtra("FILEPATH", futureStudioIconFile.getPath());
        inVoice.putExtra("DURATION", duration);
        inVoice.putExtra("TITTLE", strtittle3);
        inVoice.putExtra("VOICE", "");
        startActivity(inVoice);

    }

    private void btnStaffGroups() {
        Intent intoStu2 = new Intent(TeacherEmergencyVoice.this, ToStaffGroupList.class);
        String strtittle2 = et_tittle.getText().toString();
        intoStu2.putExtra("REQUEST_CODE", iRequestCode);
        intoStu2.putExtra("DURATION", String.valueOf(iMediaDuration));
        intoStu2.putExtra("FILEPATH", futureStudioIconFile.getPath());
        intoStu2.putExtra("TITTLE", strtittle2);
        intoStu2.putExtra("SCHOOL_ID", schoolId);
        intoStu2.putExtra("STAFF_ID", staffId);
        startActivityForResult(intoStu2, iRequestCode);

    }

    private void emergVoice_btnToSections() {
        Intent intoSec = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
        String strtittle = et_tittle.getText().toString();
        intoSec.putExtra("REQUEST_CODE", iRequestCode);
        intoSec.putExtra("FILEPATH", futureStudioIconFile.getPath());
        intoSec.putExtra("DURATION", String.valueOf(iMediaDuration));
        intoSec.putExtra("TITTLE", strtittle);
        intoSec.putExtra("TO", "SEC");
        intoSec.putExtra("SCHOOL_ID", schoolId);
        intoSec.putExtra("STAFF_ID", staffId);
        startActivityForResult(intoSec, iRequestCode);
    }

    private void emergVoice_btnToStudents() {
        Intent intoStu = new Intent(TeacherEmergencyVoice.this, TeacherStaffStandardSection.class);
        String strtittle1 = et_tittle.getText().toString();
        intoStu.putExtra("REQUEST_CODE", iRequestCode);
        intoStu.putExtra("DURATION", String.valueOf(iMediaDuration));
        intoStu.putExtra("FILEPATH", futureStudioIconFile.getPath());
        intoStu.putExtra("TITTLE", strtittle1);
        intoStu.putExtra("SCHOOL_ID", "");
        intoStu.putExtra("TO", "STU");
        startActivityForResult(intoStu, iRequestCode);
    }

    private void isTimePicking(TextView isDatePickingView) {


        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(TeacherEmergencyVoice.this, android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
                String formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute);

                if (Time == 1) {
                    isDatePickingView.setText(formattedTime);
                    Util_Common.isStartTime = isDatePickingView.getText().toString();

                } else if (Time == 2) {

                    if (!lblTimePicking.getText().toString().isEmpty()) {
                        String isFromTime = lblTimePicking.getText().toString();
                        isDatePickingView.setText(formattedTime);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        try {
                            Date date1 = sdf.parse(isFromTime);
                            Date date2 = sdf.parse(isDatePickingView.getText().toString());
                            assert date1 != null;
                            if (date1.before(date2)) {
                                isDatePickingView.setText(formattedTime);
                                Util_Common.isEndTime = isDatePickingView.getText().toString();
                            } else if (date1.after(date2)) {
                                isDatePickingView.setText("");
                                showAlertMessage(String.valueOf(R.string.Please_select_dial_beyond_time));
                            } else {
                                isDatePickingView.setText("");
                                showAlertMessage(String.valueOf(R.string.Please_select_dial_beyond_time));
                            }
                        } catch (ParseException e) {
                            Log.d("Exception", String.valueOf(e));
                        }
                    } else {
                        showAlertMessage(String.valueOf(R.string.Select_initial_call_time));
                    }

                } else if (Time == 3) {
                    isDatePickingView.setText(formattedTime);
                    Util_Common.isStartTime = isDatePickingView.getText().toString();

                } else if (Time == 4) {

                    if (!lblTimePickingschedule.getText().toString().isEmpty()) {
                        String isFromTime = lblTimePickingschedule.getText().toString();
                        isDatePickingView.setText(formattedTime);

                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        try {
                            Date date1 = sdf.parse(isFromTime);
                            Date date2 = sdf.parse(isDatePickingView.getText().toString());
                            assert date1 != null;
                            if (date1.before(date2)) {
                                isDatePickingView.setText(formattedTime);
                                Util_Common.isEndTime = isDatePickingView.getText().toString();
                            } else if (date1.after(date2)) {
                                isDatePickingView.setText("");
                                showAlertMessage(String.valueOf(R.string.Please_select_dial_beyond_time));
                            } else {
                                isDatePickingView.setText("");
                                showAlertMessage(String.valueOf(R.string.Please_select_dial_beyond_time));
                            }
                        } catch (ParseException e) {
                            Log.d("Exception", String.valueOf(e));
                        }
                    } else {
                        showAlertMessage(String.valueOf(R.string.Select_initial_call_time));
                    }
                }
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(String.valueOf(R.string.Choose_hour));
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        if (Time == 2 || Time == 4) {
            if (!lblTimePicking.getText().toString().equals("") || !lblTimePickingschedule.getText().toString().equals("")) {
                timePickerDialog.show();
            } else {
                showAlertMessage(String.valueOf(R.string.Select_initial_call_time));
            }
        } else {
            timePickerDialog.show();
        }
    }

    private void openManyDaysPicker() {

        if (!isSelectedDateList.isEmpty()) {
            for (int i = 0; i < isSelectedDateList.size(); i++) {
                String str = String.valueOf(isSelectedDateList.get(i));
                String[] arrOfStr = str.split("-");
                int isDate = Integer.parseInt(arrOfStr[0]);
                int isMonth = Integer.parseInt(arrOfStr[1]);
                int isYear = Integer.parseInt(arrOfStr[2]);
                int differenceInDays = (int) getDateDifferenceInDays(isYear, isMonth, isDate);
                System.out.println("Difference in days: " + differenceInDays);
                isDifferentDates.add(String.valueOf(differenceInDays));
            }
        }


        Calendar min = Calendar.getInstance();
        for (int i = 0; i < isDifferentDates.size(); i++) {
            Log.d("isDifferentDates", isDifferentDates.get(i));
            min.add(Calendar.DAY_OF_MONTH, Integer.parseInt(isDifferentDates.get(i)));
        }
        List<Calendar> selectedDays = new ArrayList<>(getDisabledDays());
        selectedDays.add(min);
        selectedDays.remove(selectedDays.size() - 1);

        DatePickerBuilder manyDaysBuilder = new DatePickerBuilder(this, this)
                .pickerType(CalendarView.MANY_DAYS_PICKER)
                .headerColor(R.color.teacher_colorPrimary)
                .selectionColor(R.color.teacher_clr_yellow)
                .todayLabelColor(R.color.tab_indicator)
                .dialogButtonsColor(android.R.color.holo_red_dark)
                .selectedDays(selectedDays)
                .setAbbreviationsLabelsColor(R.color.clr_pink)
                .firstDayOfWeek(CalendarWeekDay.SUNDAY)
                .navigationVisibility(View.GONE);

        DatePicker manyDaysPicker = manyDaysBuilder.build();
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.DAY_OF_MONTH, -1);
        manyDaysBuilder.setMinimumDate(minDate);
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.DAY_OF_MONTH, 6);
        manyDaysBuilder.setMaximumDate(maxDate);
        manyDaysPicker.show();
    }

    private List<Calendar> getDisabledDays() {
        List<Calendar> calendars = new ArrayList<>();
        if (!isDifferentDates.isEmpty()) {
            for (int i = 0; i < isDifferentDates.size(); i++) {
                Calendar firstDisabled = DateUtils.getCalendar();
                firstDisabled.add(Calendar.DAY_OF_MONTH, Integer.parseInt(isDifferentDates.get(i)));
                calendars.add(firstDisabled);
            }
        }
        return calendars;
    }

    @Override
    public void onSelect(@NotNull List<Calendar> calendars) {

        isSelectedDateList.clear();
        isDifferentDates.clear();
        isSelectedDate.clear();

        Stream.of(calendars).forEach(calendar -> {
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            String isSelectedDate = format.format(calendar.getTime());
            isSelectedDateList.add(isSelectedDate);
        });

        Set<String> isSelectedDate = new LinkedHashSet<String>(isSelectedDateList);
        System.out.println(isSelectedDate);
        isSelectedDateList.clear();
        isSelectedDateList.addAll(isSelectedDate);
        Util_Common.isSelectedDate.addAll(isSelectedDateList);

        ViewGroup.LayoutParams layoutParams = isDateSchedule.getLayoutParams();
        ViewGroup.LayoutParams layoutParamsHistory = isDateScheduleschedule.getLayoutParams();

        isCallScheduleAdapter = new CallScheduleAdapter((Context) TeacherEmergencyVoice.this, 1, (ArrayList<String>) isSelectedDateList, new UpdatesListener() {
            @Override
            public void onMsgItemClick(String name) {
                if (isSelectedDateList.size() - 1 == 1 || isSelectedDateList.size() - 1 == 2) {
                    layoutParams.height = 200;
                    layoutParamsHistory.height = 200;
                } else if (isSelectedDateList.size() - 1 == 3 || isSelectedDateList.size() - 1 == 4) {
                    layoutParams.height = 350;
                    layoutParamsHistory.height = 350;
                } else if (isSelectedDateList.size() - 1 == 5 || isSelectedDateList.size() - 1 == 6) {
                    layoutParams.height = 500;
                    layoutParamsHistory.height = 500;
                } else if (isSelectedDateList.size() - 1 == 7) {
                    layoutParams.height = 650;
                    layoutParamsHistory.height = 650;
                } else {
                    layoutParams.height = 0;
                    layoutParamsHistory.height = 0;
                }
                isDateSchedule.setLayoutParams(layoutParams);
                isDateScheduleschedule.setLayoutParams(layoutParams);
            }
        });

        if (Util_Common.isHistory) {
            if (isSelectedDateList.size() == 1 || isSelectedDate.size() == 2) {
                layoutParamsHistory.height = 200;
            } else if (isSelectedDateList.size() == 3 || isSelectedDateList.size() == 4) {
                layoutParamsHistory.height = 350;
            } else if (isSelectedDateList.size() == 5 || isSelectedDateList.size() == 6) {
                layoutParamsHistory.height = 500;
            } else if (isSelectedDateList.size() == 7) {
                layoutParamsHistory.height = 650;
            } else {
                layoutParamsHistory.height = 0;
            }
            isDateScheduleschedule.setLayoutParams(layoutParamsHistory);
            isDateScheduleschedule.setAdapter(isCallScheduleAdapter);
        } else {
            if (isSelectedDateList.size() == 1 || isSelectedDate.size() == 2) {
                layoutParams.height = 200;
            } else if (isSelectedDateList.size() == 3 || isSelectedDateList.size() == 4) {
                layoutParams.height = 350;
            } else if (isSelectedDateList.size() == 5 || isSelectedDateList.size() == 6) {
                layoutParams.height = 500;
            } else if (isSelectedDateList.size() == 7) {
                layoutParams.height = 650;
            } else {
                layoutParams.height = 0;
            }
            isDateSchedule.setLayoutParams(layoutParams);
            isDateSchedule.setAdapter((ListAdapter) isCallScheduleAdapter);
        }
    }

    private void isPickTheAudio() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, PICK_AUDIO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_AUDIO_REQUEST && resultCode == RESULT_OK && data != null) {
            try {
                Uri audioUri = data.getData();
                Log.d("audioUri", String.valueOf(audioUri));
                rlyPathname.setVisibility(View.VISIBLE);
                lblFilepath.setText(String.valueOf(audioUri));
                playAudio(audioUri);
                //     File audioFile1 = FileUtils.getFile(TeacherEmergencyVoice.this, audioUri);
                //    futureStudioIconFile = audioFile1;
                File mp3File = performConversion(this, audioUri);
                if (mp3File != null) {
                    Log.d("mp3File", String.valueOf(mp3File));
                    futureStudioIconFile = mp3File;
                } else {
                    Toast.makeText(this, R.string.Please_try_again, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exception) {
                Log.d("isException", String.valueOf(exception));
            }
        }
    }

    private void convertToMP3(InputStream inputStream, OutputStream outputStream) {
        // Placeholder method for audio conversion
        // In a real application, you would use a library or implementation for audio conversion
        // For demonstration purposes, let's just copy the input stream to the output stream
        byte[] buffer = new byte[1024];
        int bytesRead;
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File performConversion(Context context, Uri audioUri) {
        try {
            // Create a temporary file to store the converted MP3
            File mp3File = File.createTempFile("converted_audio", ".mp3", context.getExternalCacheDir());

            // Open input stream from the content URI
            InputStream inputStream = context.getContentResolver().openInputStream(audioUri);

            // Open output stream to the temporary MP3 file
            OutputStream outputStream = new FileOutputStream(mp3File);

            // Convert the audio data to MP3 format
            convertToMP3(inputStream, outputStream);

            // Close streams
            inputStream.close();
            outputStream.close();

            return mp3File;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void playAudio(Uri audioUri) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(getApplicationContext(), audioUri);
            mediaPlayer.prepare();


            if (loginType.equals(LOGIN_TYPE_TEACHER) || (iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW || iRequestCode == STAFF_TEXT_EXAM || iRequestCode == PRINCIPAL_VOICE || iRequestCode == STAFF_VOICE || iRequestCode == PRINCIPAL_VOICE_HW)) {
                btnToSections.setEnabled(true);
                btnToStudents.setEnabled(true);
                btnStaffGroups.setEnabled(true);
                btnSelectSchool.setEnabled(true);
                btn_Select_receipients.setEnabled(true);
            } else btnNext.setEnabled(true);

            if (loginType.equals(LOGIN_TYPE_HEAD)) {
                btnGHStandardGroups.setEnabled(true);
            }
            btnSelectSchool.setEnabled(true);
            btn_Select_receipients.setEnabled(true);
            ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
            ivRecord.setImageResource(R.drawable.teacher_ic_mic);

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(TeacherEmergencyVoice.this, audioUri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            assert durationStr != null;
            int millSecond = Integer.parseInt(durationStr);
            tvRecordDuration.setText(formateMilliSeccond(millSecond));

            try {
                int totalSeconds = parseTimeToSeconds(tvRecordDuration.getText().toString());
                iMediaDuration = Integer.parseInt(String.valueOf(totalSeconds));
                Log.d("iMediaDuration", String.valueOf(iMediaDuration));
            } catch (NumberFormatException e) {
                System.err.println("Invalid time format: " + e.getMessage());
            }

            if (!tvRecordDuration.getText().toString().equals("00:00")) {
                rlVoicePreview.setVisibility(View.VISIBLE);
            }

            if (loginType.equals(LOGIN_TYPE_HEAD)) {
                rvSchoolsList.setVisibility(View.GONE);
            } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
                rvSchoolsList.setVisibility(View.GONE);
            } else {
                rvSchoolsList.setVisibility(View.GONE);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void SendEmergencyVoicePrincipalAPI() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("file", file.getName());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile = MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsprincipal();
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Uploading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);

        Call<JsonArray> call = null;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleSendVoiceToEntireSchools(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");


                            if ((strStatus).equalsIgnoreCase("1")) {
                                showAlert(strMsg);

                            } else {
                                showAlert(strMsg);
                            }
                        } else {
                            showToast(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex_Exception", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
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
                Util_Common.isHistory = false;

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

            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < isSelectedDate.size(); i++) {
                    String isSelected = (isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchool.add("Date", isSelectedArray);
                jsonObjectSchool.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchool.addProperty("EndTime", Util_Common.isEndTime);
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
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("FILE_Path", futureStudioIconFile.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile = MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Uploading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();
        Call<JsonArray> call = null;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleSendVoiceToEntireSchools(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        }
        //  Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
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
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
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
            jsonObjectSchoolgrouphead.addProperty("Duration", String.valueOf(iMediaDuration)); //getIntent().getExtras().getString("MEDIA_DURATION", "0")
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

            if (Util_Common.isScheduleCall) {
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < isSelectedDate.size(); i++) {
                    String isSelected = (isSelectedDate.get(i));
                    isSelectedArray.add(isSelected);
                }
                jsonObjectSchoolgrouphead.add("Date", isSelectedArray);
                jsonObjectSchoolgrouphead.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolgrouphead.addProperty("EndTime", Util_Common.isEndTime);
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
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherEmergencyVoice.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(futureStudioIconFile.getPath());
        Log.d("FILE_Path", futureStudioIconFile.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile = MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsvoiceGrouphead();
        RequestBody requestBody = RequestBody.create(MultipartBody.FORM, jsonReqArray.toString());
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherEmergencyVoice.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Uploading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();

        //  Call<JsonArray> call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        Call<JsonArray> call = null;
        if (Util_Common.isScheduleCall) {
            call = apiService.ScheduleSendVoiceToEntireSchools(requestBody, bodyFile);
        } else {
            call = apiService.SendVoiceToEntireSchools(requestBody, bodyFile);
        }

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus).equalsIgnoreCase("1")) {
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
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
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

            if (Util_Common.isScheduleCall) {
                JsonObject isSelectedObject = new JsonObject();
                JsonArray isSelectedArray = new JsonArray();
                for (int i = 0; i < isSelectedDateList.size(); i++) {
                    isSelectedObject.addProperty("Dates", isSelectedDateList.get(i));
                    isSelectedArray.add(isSelectedObject);
                }
                jsonObjectSchoolgrouphead.addProperty("StartTime", Util_Common.isStartTime);
                jsonObjectSchoolgrouphead.addProperty("EndTime", Util_Common.isEndTime);
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
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getSchoolNameRegional(), ss.getStrSchoolID(), ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(), ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(), ss.getIsPaymentPending(), ss.getIsSchoolType(), ss.getIsBiometricEnable(),ss.getAllowDownload());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");
        }

        if (iRequestCode == PRINCIPAL_VOICE) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter = new TeacherSchoolListForPrincipalAdapter(TeacherEmergencyVoice.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
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
            TeacherSchoolsListAdapter schoolsListAdapter = new TeacherSchoolsListAdapter(TeacherEmergencyVoice.this, new TeacherOnCheckSchoolsListener() {
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

        btnNext.setEnabled(i_schools_count > 0);
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

        seekBar.setProgress(0);
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

        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            btnGHStandardGroups.setEnabled(true);
        }

        btnSelectSchool.setEnabled(true);
        btn_Select_receipients.setEnabled(true);

        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
        ivRecord.setImageResource(R.drawable.teacher_ic_mic);


        if (!tvRecordDuration.getText().toString().equals("00:00")) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filepath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
        } else {
            filepath = Environment.getExternalStorageDirectory().getPath();
        }

        File fileDir = new File(filepath, VOICE_FOLDER_NAME);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }

        File fileNamePath = new File(fileDir, VOICE_FILE_NAME);
        Log.d("fileNamePath", fileNamePath.getPath());
        return (fileNamePath.getPath());
    }

    public void recTimeUpdation() {
        recTime = 1;
        recTimerHandler.postDelayed(runson, 1000);

    }

    public void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {
            String filepath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                filepath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();
            } else {
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
    }    private final Runnable runson = new Runnable() {
        @Override
        public void run() {
            tvRecordDuration.setText(milliSecondsToTimer(recTime * 1000L));
            if (!tvRecordDuration.getText().toString().equals("00:00")) {
                ivRecord.setEnabled(true);
            }

            recTime = recTime + 1;
            if (recTime != iMaxRecDur) recTimerHandler.postDelayed(this, 1000);
            else stop_RECORD();
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    private void setupAudioPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d("Complete", "Complete");
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
