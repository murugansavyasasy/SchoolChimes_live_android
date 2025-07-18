package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Constants.imagepathList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_SchoolId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.Principal_staffId;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.maxGeneralSMSCount;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SmsHistoryAdapter;
import com.vs.schoolmessenger.adapter.StaffHomeWorkReport;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.aws.AwsUploadingPreSigned;
import com.vs.schoolmessenger.interfaces.HomeworkEditClickListener;
import com.vs.schoolmessenger.interfaces.SmsHistoryListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.SmsHistoryModel;
import com.vs.schoolmessenger.model.StaffNoticeBoard;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.model.TeacherSectionsListNEW;
import com.vs.schoolmessenger.model.TeacherStandardSectionsListModel;
import com.vs.schoolmessenger.model.TeacherSubjectModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AddCouponPoints;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherGeneralText extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, SmsHistoryListener, HomeworkEditClickListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    public static PopupWindow popupWindow;
    public List<StaffNoticeBoard.StaffNoticeBoardData> isStaffNoticeBoardData = new ArrayList<StaffNoticeBoard.StaffNoticeBoardData>();
    Button btnNext;
    Button btnToSections, btnToStudents;
    EditText etMessage, et_tittle;
    TextView tvcount, tvtotcount, tvheader;
    String strmessage;
    RecyclerView rvSchoolsList;
    LinearLayout AddSubjectlayout;
    RelativeLayout Select_Exam_Date;
    int a = 0;
    String loginType;
    String schoolId, staffId;
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    String strfromdate, strdatevalue;
    TextView Date;
    Spinner spinnerList;
    NestedScrollView NestedScrollView;
    RelativeLayout rytSmsHistory;
    Button btn_Select_receipients, btnSelectSchool;
    String SelectedStaffId = "";
    String SelectedSchoolId = "";
    LinearLayout lytProgressBar;
    JsonObject jsonObject;
    Button btnHistorySlectReceipients, btnSlectSchool, btnSmsHistoryStandard, btnSmsHistorySectionOrStudents;
    SmsHistoryAdapter smsAdapter;
    RecyclerView SmsHistoryRecycle;

    boolean isArchive;
    String HistoryContent = "";
    String HistoryDescription = "";
    LinearLayout selectSpinner, lnrAddVoice;
    int count = 0;
    RadioGroup TextRadio;
    RadioButton radioGeneralText, radioSmsHistory;
    Button btnStaffGroups, btnAttachments;
    File photoFile;
    String imageFilePath;
    String strPDfFilePath = "";
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    MediaRecorder recorder;
    Handler recTimerHandler = new Handler();
    boolean bIsRecording = false;
    int iMediaDuration = 0;
    File futureStudioIconFile;
    int mediaFileLengthInMilliseconds = 0;
    TextView tvPlayDuration, tvRecordDuration, tvRecordTitle, tvEmergTitle;
    ImageView ivRecord, imgClose;
    RelativeLayout rlVoicePreview, genText_relativeLayoutFoot;
    int recTime;
    TextView emergVoice_tvTitle, lblAttachments;
    List<String> listStd = new ArrayList<>();
    int iMaxRecDur;
    Handler handler = new Handler();
    List<String> listStdcode = new ArrayList<>();
    String voice_file_path = "";
    LinearLayout lnrAttachments;
    RelativeLayout rytParent;
    TextView lblHomework, lblHomeworkReport;

    int isSelectedFilePathCount = 0;
    LinearLayout rlaTitle;
    RecyclerView rcyHomeworkReport;
    StaffHomeWorkReport isStaffHomeWorkReport;
    RelativeLayout rlySection;
    TextView lblNoRecords;
    List<TeacherSectionsListNEW> arrSectionCollections;
    List<String> listSection;
    List<String> listSectionID;
    List<String> listTotalStudentsInSec;
    String strStdName, strstdcode, strSecName, strSecCode, strTotalStudents;
    TextView lblDatePicking;
    ArrayAdapter<String> adaStd;
    ArrayAdapter<String> adaSection;
    Spinner attendance_spinStandard, attendance_spinSection;
    String isAttendanceDate;
    private final ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private final ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;
    private int iRequestCode;

    Button btnUpdateHomeWork;

    private final ArrayList<String> UploadedS3URlList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;
    public List<StaffNoticeBoard.StaffNoticeBoardData> isHomeWorkReportData = new ArrayList<StaffNoticeBoard.StaffNoticeBoardData>();

    private String HOMEWORK_TYPE = "";

    String contentType = "";


    //    private String isHomeWorkReportId = "0";
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // tvcount.setText("" + (maxGeneralSMSCount - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };
    private final TextWatcher mTextEditorWatcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // tvcount.setText("" + (maxHomeWorkSMSCount - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };
    private final ArrayList<SmsHistoryModel> SmsHistoryList = new ArrayList<>();
    private final ArrayList<SmsHistoryModel> SelectedSmsHistory = new ArrayList<>();
    private ArrayList<String> imagePathList = new ArrayList<>();
    private MediaPlayer mediaPlayer;

    private boolean isHomeWorkReport = false;
    private final ArrayList<TeacherStandardSectionsListModel> arrStandardsAndSectionsList = new ArrayList<>();
    private boolean isPlaying = false;


    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_general_text);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("Reqcode", String.valueOf(iRequestCode));

        if ((listschooldetails.size() == 1)) {
            schoolId = TeacherUtil_Common.Principal_SchoolId;
            staffId = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this).equals(LOGIN_TYPE_TEACHER)) {
            schoolId = TeacherUtil_Common.Principal_SchoolId;
            staffId = TeacherUtil_Common.Principal_staffId;
        } else {
            schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
            staffId = getIntent().getExtras().getString("STAFF_ID", "");
        }


        tvheader = (TextView) findViewById(R.id.genTextPopup_tvTitle);
        btnNext = (Button) findViewById(R.id.genText_btnmsg);
        etMessage = (EditText) findViewById(R.id.genText_txtmessage);
        lblAttachments = (TextView) findViewById(R.id.lblAttachments);
        lnrAttachments = (LinearLayout) findViewById(R.id.lnrAttachments);
        lblHomework = findViewById(R.id.lblHomework);
        lblHomeworkReport = findViewById(R.id.lblHomeworkReport);
        rlaTitle = findViewById(R.id.rlaTitle);
        rcyHomeworkReport = findViewById(R.id.rcyHomeworkReport);
        rytParent = findViewById(R.id.rytParent);
        rlySection = findViewById(R.id.rlySection);
        btnUpdateHomeWork = findViewById(R.id.btnUpdateHomeWork);
        attendance_spinStandard = findViewById(R.id.attendance_spinStandard);
        attendance_spinSection = findViewById(R.id.attendance_spinSection);
        lblNoRecords = findViewById(R.id.lblNoRecords);
        lblDatePicking = findViewById(R.id.lblDatePicking);
        lytProgressBar = findViewById(R.id.lytProgressBar);


        NestedScrollView = findViewById(R.id.ComposeMessgeNested);
        rytSmsHistory = findViewById(R.id.rytSmsHistory);
        genText_relativeLayoutFoot = findViewById(R.id.genText_relativeLayoutFoot);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        String currentDate = sdf.format(new Date());

        try {
            lblDatePicking.setText(convertDateFormat(currentDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        isAwsUploadingPreSigned = new AwsUploadingPreSigned();

        btnUpdateHomeWork.setVisibility(View.GONE);
        isAttendanceDate = currentDate;


        lblDatePicking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDatePicking();
            }
        });

        lblHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAttachments.setVisibility(View.GONE);
                btnUpdateHomeWork.setVisibility(View.GONE);
                lnrAddVoice.setVisibility(View.GONE);
                isHomeWorkReport = false;
                et_tittle.setText("");
                etMessage.setText("");
                lblDatePicking.setVisibility(View.GONE);
                genText_relativeLayoutFoot.setVisibility(View.VISIBLE);
                rytSmsHistory.setVisibility(View.VISIBLE);
                NestedScrollView.setVisibility(View.VISIBLE);
                rcyHomeworkReport.setVisibility(View.GONE);
                rlySection.setVisibility(View.GONE);
                rcyHomeworkReport.setVisibility(View.GONE);
                lblHomework.setTextColor(Color.WHITE);
                lblHomeworkReport.setTextColor(Color.BLACK);
                lblHomework.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
                lblHomeworkReport.setBackgroundColor(getResources().getColor(R.color.clr_white));
            }
        });


        attendance_spinStandard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                arrSectionCollections = new ArrayList<>();
                arrSectionCollections.addAll(arrStandardsAndSectionsList.get(position).getListSectionsNew());
                listSection = new ArrayList<>();
                listSectionID = new ArrayList<>();
                listTotalStudentsInSec = new ArrayList<String>();
                strStdName = listStd.get(position);
                strstdcode = listStdcode.get(position);
                for (int i = 0; i < arrSectionCollections.size(); i++) {
                    listSection.add(arrSectionCollections.get(i).getStrSectionName());
                    listSectionID.add(arrSectionCollections.get(i).getStrSectionCode());
                    listTotalStudentsInSec.add(arrSectionCollections.get(i).getStrTotalStudents());
                }

                adaSection = new ArrayAdapter<>(TeacherGeneralText.this, R.layout.teacher_spin_title, listSection);
                adaSection.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                attendance_spinSection.setAdapter(adaSection);
                attendance_spinSection.setAdapter(adaSection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attendance_spinSection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSecName = listSection.get(position);
                strSecCode = listSectionID.get(position);
                strTotalStudents = listTotalStudentsInSec.get(position);
                getHomeWorkReport();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        lblHomeworkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isHomeWorkReport = true;
                et_tittle.setText("");
                etMessage.setText("");
                lblDatePicking.setVisibility(View.VISIBLE);
                genText_relativeLayoutFoot.setVisibility(View.GONE);
                rytSmsHistory.setVisibility(View.GONE);
                NestedScrollView.setVisibility(View.GONE);
                selectSpinner.setVisibility(View.GONE);
                rcyHomeworkReport.setVisibility(View.VISIBLE);
                rlySection.setVisibility(View.VISIBLE);
                lnrAttachments.setVisibility(View.GONE);
                lblHomeworkReport.setTextColor(Color.WHITE);
                lblHomework.setTextColor(Color.BLACK);
                lblHomeworkReport.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
                lblHomework.setBackgroundColor(getResources().getColor(R.color.clr_white));
                standardsAndSectoinsListAPI();
            }
        });


        lnrAttachments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPreviewPopup();
            }
        });

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.genText_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });

        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);
        rlVoicePreview = (RelativeLayout) findViewById(R.id.emergVoice_rlPlayPreview);
        ivRecord = (ImageView) findViewById(R.id.emergVoice_ivRecord);
        ivRecord.setOnClickListener(this);
        tvRecordDuration = (TextView) findViewById(R.id.emergVoice_tvRecDuration);
        tvRecordTitle = (TextView) findViewById(R.id.emergVoice_tvRecTitle);
        tvEmergTitle = (TextView) findViewById(R.id.emergVoice_tvEmergenyTitle);
        imgClose = (ImageView) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(this);


        setupAudioPlayer();

        if (iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == STAFF_TEXT_HW) {
            rlaTitle.setVisibility(View.VISIBLE);
            iMaxRecDur = TeacherUtil_Common.maxHWVoiceDuration;// 181; // 3 mins
            tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));
        }

        et_tittle = (EditText) findViewById(R.id.genText_txtTitle);
        tvcount = (TextView) findViewById(R.id.genText_msgcount);
        tvtotcount = (TextView) findViewById(R.id.genText_count);

        Select_Exam_Date = (RelativeLayout) findViewById(R.id.Select_Exam_Date);
        Date = (TextView) findViewById(R.id.Date);
        SmsHistoryRecycle = (RecyclerView) findViewById(R.id.SmsHistoryRecycle);


        spinnerList = (Spinner) findViewById(R.id.spinnerList);
        btn_Select_receipients = (Button) findViewById(R.id.btn_Select_receipients);
        btnSelectSchool = (Button) findViewById(R.id.btnSelectSchool);
        selectSpinner = (LinearLayout) findViewById(R.id.selectSpinner);
        lnrAddVoice = (LinearLayout) findViewById(R.id.lnrAddVoice);


        rvSchoolsList = (RecyclerView) findViewById(R.id.genText_rvSchoolsList);
        rvSchoolsList.setNestedScrollingEnabled(false);

        btnToSections = (Button) findViewById(R.id.genText_btnToSections);
        btnToSections.setOnClickListener(this);
        btnToStudents = (Button) findViewById(R.id.genText_btnToStudents);
        btnToStudents.setOnClickListener(this);
        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);

        btnStaffGroups = (Button) findViewById(R.id.btnStaffGroups);
        btnAttachments = (Button) findViewById(R.id.btnAttachments);
        btnStaffGroups.setOnClickListener(this);
        btnAttachments.setOnClickListener(this);
        btnAttachments.setEnabled(false);

        btnStaffGroups.setEnabled(false);


        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherGeneralText.this);
        if (countryID.equals("11")) {
            btnToSections.setText(R.string.To_Grade_or_Sections);
        }

        TextRadio = (RadioGroup) findViewById(R.id.TextRadio);
        radioGeneralText = (RadioButton) findViewById(R.id.radioGeneralText);
        radioSmsHistory = (RadioButton) findViewById(R.id.radioSmsHistory);

        btn_Select_receipients.setEnabled(false);
        btnSelectSchool.setEnabled(false);

        if (iRequestCode == STAFF_TEXT || iRequestCode == PRINCIPAL_TEXT || iRequestCode == GH_TEXT) {
            selectSpinner.setVisibility(View.VISIBLE);
        }


        btnHistorySlectReceipients = (Button) findViewById(R.id.btnHistorySlectReceipients);
        btnSlectSchool = (Button) findViewById(R.id.btnSlectSchool);
        btnSmsHistoryStandard = (Button) findViewById(R.id.btnSmsHistoryStandard);
        btnSmsHistorySectionOrStudents = (Button) findViewById(R.id.btnSmsHistorySectionOrStudents);

        btnSmsHistorySectionOrStudents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = 0;
                pickDescriptionFromSmsHistory(0);

                if (count > 0) {
                    Intent intoStu = new Intent(TeacherGeneralText.this, TeacherStaffStandardSection.class);
                    String strtittle1 = et_tittle.getText().toString();
                    String message1 = etMessage.getText().toString();
                    intoStu.putExtra("REQUEST_CODE", iRequestCode);
                    intoStu.putExtra("SCHOOL_ID", schoolId);
                    intoStu.putExtra("STAFF_ID", staffId);
                    intoStu.putExtra("TITTLE", HistoryContent);
                    intoStu.putExtra("MESSAGE", HistoryDescription);
                    intoStu.putExtra("TO", "STU");
                    Log.d("STUDENT", "STU");
                    startActivityForResult(intoStu, iRequestCode);
                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));

                }

            }
        });

        btnSmsHistoryStandard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                pickDescriptionFromSmsHistory(0);

                if (count > 0) {

                    Intent intoSec = new Intent(TeacherGeneralText.this, TeacherStaffStandardSection.class);
                    intoSec.putExtra("REQUEST_CODE", iRequestCode);
                    intoSec.putExtra("SCHOOL_ID", schoolId);
                    intoSec.putExtra("STAFF_ID", staffId);
                    intoSec.putExtra("TITTLE", HistoryContent);
                    intoSec.putExtra("MESSAGE", HistoryDescription);
                    intoSec.putExtra("TO", "SEC");
                    Log.d("SECTION", "SEC");
                    startActivityForResult(intoSec, iRequestCode);
                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }

            }
        });

        btnHistorySlectReceipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = 0;
                pickDescriptionFromSmsHistory(0);


                if (count > 0) {
                    Intent inPrincipal = new Intent(TeacherGeneralText.this, SendToTextSpecificSection.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                    TeacherUtil_Common.Principal_SchoolId = schoolId;
                    TeacherUtil_Common.Principal_staffId = staffId;
                    inPrincipal.putExtra("SCHOOL_ID", schoolId);
                    inPrincipal.putExtra("STAFF_ID", staffId);
                    inPrincipal.putExtra("TITTLE", HistoryContent);
                    inPrincipal.putExtra("MESSAGE", HistoryDescription);
                    startActivityForResult(inPrincipal, iRequestCode);

                } else {
                    showAlertMessage(getResources().getString(R.string.please_select_message));
                }

            }
        });

        btnSelectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = et_tittle.getText().toString();
                String description = etMessage.getText().toString();

                if (description.length() > 0) {
                    Intent schoolslist = new Intent(TeacherGeneralText.this, ScoolsList.class);
                    schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
                    schoolslist.putExtra("schools", "text");
                    schoolslist.putExtra("Title", title);
                    schoolslist.putExtra("Description", description);
                    startActivity(schoolslist);
                } else {
                    showToast(getResources().getString(R.string.enter_message));
                }
            }
        });

        btnSlectSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count = 0;
                pickDescriptionFromSmsHistory(0);

                if (count > 0) {
                    Intent schoolslist = new Intent(TeacherGeneralText.this, ScoolsList.class);
                    schoolslist.putExtra("TeacherSchoolsModel", listschooldetails);
                    schoolslist.putExtra("Title", HistoryContent);
                    schoolslist.putExtra("schools", "text");
                    schoolslist.putExtra("Description", HistoryDescription);
                    startActivity(schoolslist);

                } else {
                    showAlertMessage(getResources().getString(R.string.select_atleast_one_school));
                }

            }
        });

        String[] list = {getResources().getString(R.string.text_compose_msg), getResources().getString(R.string.sms_history)};

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(TeacherGeneralText.this, android.R.layout.simple_spinner_item, list);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerList.setAdapter(adapter1);
        spinnerList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedItemText = (String) parent.getItemAtPosition(position);

                if (position == 0) {
                    NestedScrollView.setVisibility(View.VISIBLE);
                    rytSmsHistory.setVisibility(View.GONE);

                    selectedViewButtons();

                } else if (position == 1) {
                    NestedScrollView.setVisibility(View.GONE);
                    rytSmsHistory.setVisibility(View.VISIBLE);
                    selectedViewButtons();


                    smsAdapter = new SmsHistoryAdapter(TeacherGeneralText.this, TeacherGeneralText.this, SmsHistoryList);

                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherGeneralText.this);
                    SmsHistoryRecycle.setLayoutManager(layoutManager);
                    SmsHistoryRecycle.addItemDecoration(new DividerItemDecoration(TeacherGeneralText.this, LinearLayoutManager.VERTICAL));
                    SmsHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
                    SmsHistoryRecycle.setAdapter(smsAdapter);

                    SmsHistory();

                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        radioGeneralText.setChecked(true);
        NestedScrollView.setVisibility(View.VISIBLE);
        rytSmsHistory.setVisibility(View.GONE);
        selectedViewButtons();

        TextRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioGeneralText:
                        NestedScrollView.setVisibility(View.VISIBLE);
                        rytSmsHistory.setVisibility(View.GONE);
                        selectedViewButtons();
                        break;
                    case R.id.radioSmsHistory:

                        NestedScrollView.setVisibility(View.GONE);
                        rytSmsHistory.setVisibility(View.VISIBLE);
                        selectedViewButtons();
                        smsAdapter = new SmsHistoryAdapter(TeacherGeneralText.this, TeacherGeneralText.this, SmsHistoryList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherGeneralText.this);
                        SmsHistoryRecycle.setLayoutManager(layoutManager);
                        SmsHistoryRecycle.addItemDecoration(new DividerItemDecoration(TeacherGeneralText.this, LinearLayoutManager.VERTICAL));
                        SmsHistoryRecycle.setItemAnimator(new DefaultItemAnimator());
                        SmsHistoryRecycle.setAdapter(smsAdapter);
                        SmsHistory();

                        break;

                }
            }
        });


        if (iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == STAFF_TEXT_HW) {
            tvheader.setText(getResources().getText(R.string.teacher_txt_composehwmsg));
            et_tittle.setHint(getResources().getText(R.string.teacher_txt_hw_title));//"HomeWork Topic");
            etMessage.setHint(getResources().getText(R.string.teacher_txt_typemsg));
            etMessage.addTextChangedListener(mTextEditorWatcher1);

            btnAttachments.setVisibility(View.VISIBLE);
            rlaTitle.setVisibility(View.VISIBLE);

        } else if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
            tvheader.setText(getResources().getText(R.string.teacher_txt_composeExammsg));
            String totcount = String.valueOf(maxGeneralSMSCount);
            tvtotcount.setText(totcount);
            et_tittle.setHint(getResources().getText(R.string.teacher_txt_exam_title));//"HomeWork Topic");
            etMessage.setHint(getResources().getText(R.string.teacher_txt_exam_typemsg));
            etMessage.addTextChangedListener(mTextEditorWatcher);
            et_tittle.addTextChangedListener(mTextEditorWatcher);
            Date.setOnClickListener(this);
            rlaTitle.setVisibility(View.GONE);
        } else {
            String totcount = String.valueOf(maxGeneralSMSCount);
            tvtotcount.setText(totcount);
            etMessage.addTextChangedListener(mTextEditorWatcher);
            rlaTitle.setVisibility(View.GONE);
        }


        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == PRINCIPAL_TEXT_HW || iRequestCode == PRINCIPAL_VOICE_HW) {
            TeacherUtil_Common.Principal_SchoolId = schoolId;
            TeacherUtil_Common.Principal_staffId = staffId;
        }

        ImageView ivBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                isHomeWorkReport = false;
            }
        });

        btnNext.setOnClickListener(this);


        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this);
        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            listSchoolsAPI();
            btnToSections.setVisibility(View.GONE);
            btnToStudents.setVisibility(View.GONE);
        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            if (iRequestCode == PRINCIPAL_EXAM_TEST) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
            } else if (iRequestCode == PRINCIPAL_TEXT_HW) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
            } else {
                listSchoolsAPI();

                btnNext.setVisibility(View.GONE);
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
            }
        } else if (loginType.equals(LOGIN_TYPE_TEACHER)) {

            if (iRequestCode == STAFF_TEXT_HW) {
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                rlaTitle.setVisibility(View.VISIBLE);
            } else {
                if (iRequestCode == STAFF_TEXT) {
                    btnStaffGroups.setVisibility(View.VISIBLE);
                }
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                rlaTitle.setVisibility(View.GONE);
            }
        }


        btn_Select_receipients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etMessage.getText().toString().length() > 0) {
                    String strtittle = et_tittle.getText().toString();
                    String strmessage = etMessage.getText().toString();

                    Intent inPrincipal = new Intent(TeacherGeneralText.this, SendToTextSpecificSection.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);

                    TeacherUtil_Common.Principal_SchoolId = schoolId;
                    TeacherUtil_Common.Principal_staffId = staffId;

                    inPrincipal.putExtra("SCHOOL_ID", schoolId);
                    inPrincipal.putExtra("STAFF_ID", staffId);
                    inPrincipal.putExtra("TITTLE", strtittle);
                    inPrincipal.putExtra("MESSAGE", strmessage);
                    startActivityForResult(inPrincipal, iRequestCode);
                } else showToast(getResources().getString(R.string.enter_message));
            }
        });

        btnUpdateHomeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lytProgressBar.setVisibility(View.VISIBLE);
                UploadedS3URlList.clear();
                if (!strPDfFilePath.equals("")) {
                    contentType = "application/pdf";
                    HOMEWORK_TYPE = "PDF";
                    isUploadAWS("pdf", ".pdf", "");
                } else if (!voice_file_path.equals("")) {
                    HOMEWORK_TYPE = "VOICE";
                    contentType = "audio/mp3";
                    imagePathList.clear();
                    imagePathList.add(voice_file_path);
                    UploadedS3URlList.clear();
                    isUploadAWS("audio", ".mp3", "");
                } else {
                    HOMEWORK_TYPE = "IMAGE";
                    contentType = "image/png";
                    isUploadAWS("image", "IMG", "");
                }
            }
        });
    }

    private void isDatePicking() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherGeneralText.this, (view, selectedYear, selectedMonth, selectedDay) -> {
            String selectedDate = String.format("%02d/%02d/%d", selectedDay, selectedMonth + 1, selectedYear);

            try {
                lblDatePicking.setText(convertDateFormat(selectedDate));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            isAttendanceDate = selectedDate;
            getHomeWorkReport();
        }, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    public static String convertDateFormat(String inputDate) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH); // ✅ MMM for month name

        Date date = inputFormat.parse(inputDate);
        return outputFormat.format(date);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isHomeWorkReport) {
            btnUpdateHomeWork.setVisibility(View.VISIBLE);
        } else {
            btnUpdateHomeWork.setVisibility(View.GONE);
        }
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

    private void showAlertMessage(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherGeneralText.this);

        //Setting Dialog Title
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

    private void pickDescriptionFromSmsHistory(int a) {
        for (int i = 0; i < SelectedSmsHistory.size(); i++) {
            SmsHistoryModel data = SelectedSmsHistory.get(i);
            if (data.getSelectedStatus()) {
                count = a + 1;
                HistoryContent = data.getContent();
                HistoryDescription = data.getDescription();

            }
        }
    }

    private void SmsHistory() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherGeneralText.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        SelectedSchoolId = "";
        SelectedStaffId = "";

        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this);

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

        } else {
            jsonObject = new JsonObject();
            jsonObject.addProperty("StaffID", staffId);
            jsonObject.addProperty("SchoolId", schoolId);

        }
        Log.d("Request", jsonObject.toString());
        Call<JsonArray> call = apiService.GetSMSHistory(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());

                        smsAdapter.clearAllData();
                        SmsHistoryModel data;

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
                                    String Id = jsonObject.getString("MessageID");
                                    String content = jsonObject.getString("Content");
                                    String description = jsonObject.getString("Description");
                                    data = new SmsHistoryModel(Id, content, description, false);
                                    SmsHistoryList.add(data);
                                }
                            }
                            smsAdapter.notifyDataSetChanged();
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherGeneralText.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                NestedScrollView.setVisibility(View.VISIBLE);
                rytSmsHistory.setVisibility(View.GONE);
                radioGeneralText.setChecked(true);
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
        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this);
        if (loginType.equals(LOGIN_TYPE_TEACHER)) {

            if (iRequestCode == STAFF_TEXT) {
                btnToSections.setVisibility(View.VISIBLE);
                btnToStudents.setVisibility(View.VISIBLE);
                btnStaffGroups.setVisibility(View.VISIBLE);
                btnNext.setVisibility(View.GONE);
                btn_Select_receipients.setVisibility(View.GONE);
                btnSmsHistoryStandard.setVisibility(View.VISIBLE);
                btnSmsHistorySectionOrStudents.setVisibility(View.VISIBLE);
                btnSlectSchool.setVisibility(View.GONE);
                btnHistorySlectReceipients.setVisibility(View.GONE);
            }

        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {

            if (iRequestCode == PRINCIPAL_TEXT) {
                btnToSections.setVisibility(View.GONE);
                btnToStudents.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
                btn_Select_receipients.setVisibility(View.VISIBLE);

                btnSmsHistoryStandard.setVisibility(View.GONE);
                btnSmsHistorySectionOrStudents.setVisibility(View.GONE);
                btnSlectSchool.setVisibility(View.GONE);
                btnHistorySlectReceipients.setVisibility(View.VISIBLE);
            }

        } else if (loginType.equals(LOGIN_TYPE_HEAD)) {

            if (iRequestCode == GH_TEXT) {
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

    public void onAddField(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.add_subjects, null);
        AddSubjectlayout.addView(rowView, AddSubjectlayout.getChildCount() - 1);

    }

    public void onDate(View v) {
        datePicker();
        setMinDateTime(a);
    }

    private void setMinDateTime(int a) {
        strfromdate = dateFormater(System.currentTimeMillis(), "dd-MM-yyyy");

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);

        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        minimumHour = cal.get(Calendar.HOUR_OF_DAY);
        minimumMinute = cal.get(Calendar.MINUTE);

        selHour = Integer.toString(minimumHour);
        selMin = Integer.toString(minimumMinute);
    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment().setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog).setOnDateSetListener(TeacherGeneralText.this).setFirstDayOfWeek(Calendar.SUNDAY).setPreselectedDate(selYear, selMonth, selDay).setDateRange(minDate, null).setDoneText(getResources().getString(R.string.teacher_btn_ok)).setCancelText(getResources().getString(R.string.teacher_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getSchoolNameRegional(), ss.getStrSchoolID(), ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(), ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(), ss.getIsPaymentPending(), ss.getIsSchoolType(), ss.getIsBiometricEnable(), ss.getAllowDownload());
            arrSchoolList.add(ss);
        }

        if (iRequestCode == PRINCIPAL_TEXT) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter = new TeacherSchoolListForPrincipalAdapter(TeacherGeneralText.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                @Override
                public void onItemClick(TeacherSchoolsModel item) {
                    if (etMessage.getText().toString().length() > 0) {
                        String strtittle = et_tittle.getText().toString();
                        String strmessage = etMessage.getText().toString();

                        Intent inPrincipal = new Intent(TeacherGeneralText.this, SendToTextSpecificSection.class);
                        inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                        TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                        TeacherUtil_Common.Principal_staffId = item.getStrStaffID();
                        inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                        inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                        inPrincipal.putExtra("TITTLE", strtittle);
                        inPrincipal.putExtra("MESSAGE", strmessage);
                        startActivityForResult(inPrincipal, iRequestCode);
                    } else showToast(getResources().getString(R.string.enter_message));
                }
            });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherGeneralText.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherGeneralText.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        } else {
            TeacherSchoolsListAdapter schoolsListAdapter = new TeacherSchoolsListAdapter(TeacherGeneralText.this, new TeacherOnCheckSchoolsListener() {
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
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherGeneralText.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherGeneralText.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying()) mediaPlayer.stop();
        isHomeWorkReport = false;
        if (bIsRecording) stop_RECORD();
        backToResultActvity("SENT");
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


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void enableSubmitIfReady() {
        boolean isReady = etMessage.getText().toString().length() > 0;
        boolean istitleReady = et_tittle.getText().toString().length() > 0;
        Log.d("loginType", String.valueOf(loginType));
        Log.d("isRequestCode", String.valueOf(iRequestCode));
        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {

            btnToSections.setEnabled((isReady) && (istitleReady));
            btnToStudents.setEnabled((isReady) && (istitleReady));
            btnStaffGroups.setEnabled((isReady) && (istitleReady));
            rlaTitle.setVisibility(View.GONE);
        } else if ((iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW) || iRequestCode == PRINCIPAL_TEXT_HW) {
            btnToSections.setEnabled(isReady);
            btnToStudents.setEnabled(isReady);
            btnStaffGroups.setEnabled(isReady);
            btnAttachments.setEnabled(isReady);
            btn_Select_receipients.setEnabled(isReady);
            btnSelectSchool.setEnabled(isReady);
            rlaTitle.setVisibility(View.VISIBLE);
        } else if (iRequestCode == STAFF_TEXT || iRequestCode == PRINCIPAL_TEXT) {
            btnToSections.setEnabled(isReady);
            btnToStudents.setEnabled(isReady);
            btnStaffGroups.setEnabled(isReady);
            btnAttachments.setEnabled(isReady);
            btn_Select_receipients.setEnabled(isReady);
            btnSelectSchool.setEnabled(isReady);
            rlaTitle.setVisibility(View.GONE);
        } else btnNext.setEnabled(isReady);

        btn_Select_receipients.setEnabled(isReady);
        btnSelectSchool.setEnabled(isReady);
    }


    public void validation() {
        strmessage = etMessage.getText().toString().trim();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genText_btnmsg:
                validation();
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this).equals(LOGIN_TYPE_PRINCIPAL)) {
                    Intent inPrincipal = new Intent(TeacherGeneralText.this, TeacherStandardsAndGroupsList.class);
                    inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                    startActivityForResult(inPrincipal, iRequestCode);
                } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherGeneralText.this).equals(LOGIN_TYPE_HEAD)) {
                    Log.d("SelectedCount", String.valueOf(i_schools_count));
                    if (i_schools_count > 0) {

                        SendEmergencyVoiceGroupheadAPI();
                    } else {
                        showToast(getResources().getString(R.string.teacher_Select_school));
                    }
                }

                break;

            case R.id.genText_btnToSections:
                Intent intoSec = new Intent(TeacherGeneralText.this, TeacherStaffStandardSection.class);
                String strtittle = et_tittle.getText().toString();
                String message = etMessage.getText().toString();
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("SCHOOL_ID", schoolId);
                intoSec.putExtra("STAFF_ID", staffId);
                intoSec.putExtra("TITTLE", strtittle);
                intoSec.putExtra("MESSAGE", message);
                intoSec.putExtra("TO", "SEC");
                intoSec.putExtra("PATH_LIST", imagePathList);
                intoSec.putExtra("FILE_PATH_PDF", strPDfFilePath);
                intoSec.putExtra("FILEPATH", voice_file_path);
                Log.d("SECTION", "SEC");
                startActivityForResult(intoSec, iRequestCode);
                break;


            case R.id.btnStaffGroups:
                Intent intoSec1 = new Intent(TeacherGeneralText.this, ToStaffGroupList.class);
                String strtittle2 = et_tittle.getText().toString();
                String message2 = etMessage.getText().toString();
                intoSec1.putExtra("REQUEST_CODE", iRequestCode);
                intoSec1.putExtra("SCHOOL_ID", schoolId);
                intoSec1.putExtra("STAFF_ID", staffId);
                intoSec1.putExtra("TITTLE", strtittle2);
                intoSec1.putExtra("MESSAGE", message2);
                startActivityForResult(intoSec1, iRequestCode);
                break;

            case R.id.genText_btnToStudents:
                Intent intoStu = new Intent(TeacherGeneralText.this, TeacherStaffStandardSection.class);
                String strtittle1 = et_tittle.getText().toString();
                String message1 = etMessage.getText().toString();
                intoStu.putExtra("REQUEST_CODE", iRequestCode);
                intoStu.putExtra("SCHOOL_ID", schoolId);
                intoStu.putExtra("STAFF_ID", staffId);
                intoStu.putExtra("TITTLE", strtittle1);
                intoStu.putExtra("MESSAGE", message1);
                intoStu.putExtra("TO", "STU");
                Log.d("STUDENT", "STU");
                startActivityForResult(intoStu, iRequestCode);
                break;

            case R.id.Date:
                strdatevalue = "1";
                datePicker();
                break;

            case R.id.btnAttachments:
                attachmentsPopup();
                break;

            case R.id.imgClose:
                lnrAddVoice.setVisibility(View.GONE);

                break;

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.emergVoice_ivRecord:
                if (bIsRecording) {
                    stop_RECORD();
                } else {
                    ivRecord.setEnabled(false);
                    start_RECORD();
                }
                break;


            default:
                break;
        }
    }

    private void start_RECORD() {
        imagePathList.clear();
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
            rlaTitle.setVisibility(View.VISIBLE);

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

    private void recTimeUpdation() {
        recTime = 1;
        recTimerHandler.postDelayed(runson, 1000);

    }

    private String getRecFilename() {

        String filepath;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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
            rlaTitle.setVisibility(View.VISIBLE);
        } else btnNext.setEnabled(true);

        btnSelectSchool.setEnabled(true);
        btn_Select_receipients.setEnabled(true);

        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
        ivRecord.setImageResource(R.drawable.teacher_ic_mic);

        if (!tvRecordDuration.getText().toString().equals("00:00")) {
            rlVoicePreview.setVisibility(View.VISIBLE);
        }
        lnrAttachments.setVisibility(View.GONE);

        if (loginType.equals(LOGIN_TYPE_HEAD)) {
            rvSchoolsList.setVisibility(View.GONE);
        } else if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            rvSchoolsList.setVisibility(View.GONE);
        } else {
            rvSchoolsList.setVisibility(View.GONE);
        }
        fetchSong();
    }

    private final Runnable runson = new Runnable() {
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

    private void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {
            String filepath;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
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

            voice_file_path = futureStudioIconFile.getPath();

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

    private void attachmentsPopup() {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_attachments);
        TextView close = bottomSheetDialog.findViewById(R.id.lblClose);
        RelativeLayout rytGallery = bottomSheetDialog.findViewById(R.id.rytGallery);
        RelativeLayout rytCamera = bottomSheetDialog.findViewById(R.id.rytCamera);
        RelativeLayout rytPdf = bottomSheetDialog.findViewById(R.id.rytPdf);
        RelativeLayout rytVoice = bottomSheetDialog.findViewById(R.id.rytVoice);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        rytGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddVoice.setVisibility(View.GONE);
                Intent i = new Intent(TeacherGeneralText.this, AlbumSelectActivity.class);
                startActivityForResult(i, 1);
                bottomSheetDialog.dismiss();
            }
        });

        rytCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddVoice.setVisibility(View.GONE);
                imagePathList.clear();
                imagepathList = 0;

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d("photoFile", photoFile.toString());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(TeacherGeneralText.this, "com.vs.schoolmessenger.provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(intent, 3);
                    bottomSheetDialog.dismiss();

                }

            }
        });

        rytPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddVoice.setVisibility(View.GONE);
                imagePathList.clear();
                imagepathList = 0;
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
                bottomSheetDialog.dismiss();
            }
        });

        rytVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnrAddVoice.setVisibility(View.VISIBLE);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);
        imageFilePath = image.getAbsolutePath();
        return image;
    }

    public boolean isAudioFile(String filePath) {
        String[] audioExtensions = {".mp3", ".m4a", ".aac", ".wav", ".flac", ".ogg", ".oga", ".wma", ".amr", ".opus", ".3gp"};
        filePath = filePath.toLowerCase();

        for (String ext : audioExtensions) {
            if (filePath.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        btnToSections.setEnabled(true);
        btnToStudents.setEnabled(true);
        btnStaffGroups.setEnabled(true);
        lnrAttachments.setVisibility(View.VISIBLE);
        if (requestCode == 1 && null != data) {

            try {
                if (resultCode == RESULT_OK) {
                    strPDfFilePath = "";
                    voice_file_path = "";

                    if (!imagePathList.isEmpty()) {
                        if (isAudioFile(imagePathList.get(0))) {
                            imagePathList.clear();
                        }
                    }

                    ArrayList<String> newImageList = data.getStringArrayListExtra("images");
                    String isImageCount = TeacherUtil_SharedPreference.getImageCount(TeacherGeneralText.this);
                    Log.d("newImageList", String.valueOf(newImageList.size()));
                    int currentSize = imagePathList.size();
                    int newSize = newImageList.size();
                    if (currentSize + newSize <= Integer.parseInt(isImageCount)) {
                        Log.d("isComing", "isComing");
                        imagePathList.addAll(newImageList);
                        imageFilePath = imagePathList.get(0);
                        lblAttachments.setText("Attachments +" + imagePathList.size());
                    } else {
                        Toast.makeText(this, "Only " + Integer.parseInt(isImageCount) + " images are allowed", Toast.LENGTH_SHORT).show();
                    }
                    openPreviewPopup();
                }
            } catch (Exception e) {
                Log.d("isExceptionImagePick", String.valueOf(e));
            }
        } else if (requestCode == 2) {

            try {
                if (data != null && resultCode == RESULT_OK) {
                    voice_file_path = "";

                    Uri uri = data.getData();
                    File outputDir = TeacherGeneralText.this.getCacheDir(); // context being the Activity pointer
                    File outputFile = File.createTempFile("School_document", ".pdf", outputDir);
                    try (InputStream in = getContentResolver().openInputStream(uri)) {
                        if (in == null) return;
                        try (OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(outputFile))) {
                            if (out == null) return;
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }

                    imagePathList.add(outputFile.getPath());
                    strPDfFilePath = outputFile.getPath();
                    lblAttachments.setText("Attachments +" + imagePathList.size());
                    imagepathList = imagePathList.size();

                    openPreviewPopup();

                }
            } catch (Exception e) {
                alert(getResources().getString(R.string.Please_choose_pdf_file_send));
            }
        } else if (requestCode == 3) {
            try {
                if (resultCode == RESULT_OK) {
                    strPDfFilePath = "";
                    voice_file_path = "";
                    String imagesize = TeacherUtil_SharedPreference.getImagesize(TeacherGeneralText.this);
                    long sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
                    Log.d("length", String.valueOf(sizekb));
                    File img = new File(imageFilePath);
                    long length = img.length();
                    Log.d("length", String.valueOf(length));

                    lblAttachments.setText(R.string.Attachments + imagePathList.size());
                    imagepathList = imagePathList.size();

                    if (length <= sizekb) {
                        imagePathList.add(imageFilePath);
                        openPreviewPopup();
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(TeacherGeneralText.this);
                        alert(filecontent);
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    imagePathList.clear();
                    lnrAttachments.setVisibility(View.GONE);
                    imagepathList = 0;
                }

            } catch (Exception e) {

            }
        } else {
            imagePathList.clear();
            imagepathList = 0;
            lnrAttachments.setVisibility(View.GONE);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void openPreviewPopup() {

        Log.d("imagePathList", String.valueOf(imagePathList.size()));

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_preview_popup);

        ImageView imgClose1 = bottomSheetDialog.findViewById(R.id.imgClose1);
        ImageView imgClose2 = bottomSheetDialog.findViewById(R.id.imgClose2);
        ImageView imgClose3 = bottomSheetDialog.findViewById(R.id.imgClose3);
        ImageView imgClose4 = bottomSheetDialog.findViewById(R.id.imgClose4);

        ShapeableImageView img1 = bottomSheetDialog.findViewById(R.id.img1);
        ShapeableImageView img2 = bottomSheetDialog.findViewById(R.id.img2);
        ShapeableImageView img3 = bottomSheetDialog.findViewById(R.id.img3);
        ShapeableImageView img4 = bottomSheetDialog.findViewById(R.id.img4);

        LinearLayout lnrView1 = bottomSheetDialog.findViewById(R.id.lnrView1);
        LinearLayout lnrView2 = bottomSheetDialog.findViewById(R.id.lnrView2);
        LinearLayout lnrView3 = bottomSheetDialog.findViewById(R.id.lnrView3);
        LinearLayout lnrView4 = bottomSheetDialog.findViewById(R.id.lnrView4);

        TextView lblClose = bottomSheetDialog.findViewById(R.id.lblClose);
        TextView lblNext = bottomSheetDialog.findViewById(R.id.lblNext);

        if (isHomeWorkReport) {
            lblNext.setVisibility(View.GONE);
        } else {
            lblNext.setVisibility(View.VISIBLE);
        }

        lblClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
            }
        });

        imgClose1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePathList.remove(0);
                lnrView1.setVisibility(View.GONE);
                visibleAttachments(bottomSheetDialog);
                if (imagePathList.size() == 0) {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        imgClose2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePathList.remove(1);
                lnrView2.setVisibility(View.GONE);

                visibleAttachments(bottomSheetDialog);

                if (imagePathList.size() == 0) {
                    bottomSheetDialog.dismiss();
                }
            }
        });

        imgClose3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePathList.remove(2);
                lnrView3.setVisibility(View.GONE);
                visibleAttachments(bottomSheetDialog);
                if (imagePathList.size() == 0) {
                    bottomSheetDialog.dismiss();
                }
            }
        });

        imgClose4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePathList.remove(3);
                lnrView4.setVisibility(View.GONE);
                visibleAttachments(bottomSheetDialog);
                if (imagePathList.size() == 0) {
                    bottomSheetDialog.dismiss();
                }
            }
        });
        lblNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();

                Intent intoSec = new Intent(TeacherGeneralText.this, TeacherStaffStandardSection.class);
                String strtittle = et_tittle.getText().toString();
                String message = etMessage.getText().toString();
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                intoSec.putExtra("SCHOOL_ID", schoolId);
                intoSec.putExtra("STAFF_ID", staffId);
                intoSec.putExtra("TITTLE", strtittle);
                intoSec.putExtra("MESSAGE", message);
                intoSec.putExtra("TO", "SEC");
                intoSec.putExtra("PATH_LIST", imagePathList);
                intoSec.putExtra("FILE_PATH_PDF", strPDfFilePath);
                intoSec.putExtra("FILEPATH", voice_file_path);
                Log.d("SECTION", "SEC");
                startActivityForResult(intoSec, iRequestCode);
            }
        });

        if (imagePathList.size() == 1) {

            if (strPDfFilePath.equals("")) {
                Glide.with(this).load(imagePathList.get(0)).into(img1);
            } else {
                img1.setImageResource(R.drawable.pdf_image);
            }

            lnrView1.setVisibility(View.VISIBLE);
            lnrView2.setVisibility(View.GONE);
            lnrView3.setVisibility(View.GONE);
            lnrView4.setVisibility(View.GONE);

        } else if (imagePathList.size() == 2) {

            Glide.with(this).load(imagePathList.get(0)).into(img1);
            Glide.with(this).load(imagePathList.get(1)).into(img2);

            lnrView1.setVisibility(View.VISIBLE);
            lnrView2.setVisibility(View.VISIBLE);
            lnrView3.setVisibility(View.GONE);
            lnrView4.setVisibility(View.GONE);
        } else if (imagePathList.size() == 3) {

            Glide.with(this).load(imagePathList.get(0)).into(img1);
            Glide.with(this).load(imagePathList.get(1)).into(img2);
            Glide.with(this).load(imagePathList.get(2)).into(img3);

            lnrView1.setVisibility(View.VISIBLE);
            lnrView2.setVisibility(View.VISIBLE);
            lnrView3.setVisibility(View.VISIBLE);
            lnrView4.setVisibility(View.GONE);

        } else if (imagePathList.size() == 4) {

            Glide.with(this).load(imagePathList.get(0)).into(img1);
            Glide.with(this).load(imagePathList.get(1)).into(img2);
            Glide.with(this).load(imagePathList.get(2)).into(img3);
            Glide.with(this).load(imagePathList.get(3)).into(img4);

            lnrView1.setVisibility(View.VISIBLE);
            lnrView2.setVisibility(View.VISIBLE);
            lnrView3.setVisibility(View.VISIBLE);
            lnrView4.setVisibility(View.VISIBLE);
        }

        bottomSheetDialog.show();
    }

    private void visibleAttachments(BottomSheetDialog bottomSheetDialog) {
        lblAttachments.setText("Attachments " + imagePathList.size());
        imagepathList = imagePathList.size();

        if (imagePathList.size() > 0) {
            lnrAttachments.setVisibility(View.VISIBLE);
        } else {
            lnrAttachments.setVisibility(View.GONE);
            bottomSheetDialog.dismiss();

        }
    }

    private void alert(String s) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(TeacherGeneralText.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(s);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alertDialog.show();
    }

    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherGeneralText.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Uploading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing()) mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        Call<JsonArray> call = apiService.SendSMSToEntireSchools(jsonReqArray);
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
                                AddCouponPoints.addPoints(TeacherGeneralText.this, Util_Common.SEND_TEXT_POINTS);

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

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherGeneralText.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                onBackPressed();
                dialog.cancel();
                finish();

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private JsonObject constructJsonArrayMgtSchoolsGrouphead() {
        String strdescription = et_tittle.getText().toString();
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("Description", strdescription);
            jsonObjectSchool.addProperty("Message", strmessage);
            jsonObjectSchool.addProperty("CallerType", "A");

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                JsonObject jsonObjectschoolId = new JsonObject();
                jsonObjectschoolId.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                jsonObjectschoolId.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                jsonArrayschool.add(jsonObjectschoolId);
            }

            jsonObjectSchool.add("School", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }

    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        String dateString = formatter.format(new Date(dateInMillis));
        Log.d("dateString", dateString);
        return dateString;
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;
        strfromdate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd-MM-yyyy");

    }

    @Override
    public void smsHistoryAddList(SmsHistoryModel contact) {
        SmsHistoryList.get(SmsHistoryList.indexOf(contact)).setSeletedStatus(true);

        SelectedSmsHistory.add(contact);
    }

    @Override
    public void smsHistoryRemoveList(SmsHistoryModel contact) {
        SmsHistoryList.get(SmsHistoryList.indexOf(contact)).setSeletedStatus(false);
        SelectedSmsHistory.remove(contact);
    }

    private void getHomeWorkReport() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherGeneralText.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<StaffNoticeBoard> call = apiService.getHomeWorkReport(schoolId, strSecCode, isAttendanceDate, staffId);
        call.enqueue(new Callback<StaffNoticeBoard>() {
            @Override
            public void onResponse(Call<StaffNoticeBoard> call, retrofit2.Response<StaffNoticeBoard> response) {
                try {
                    rcyHomeworkReport.setVisibility(View.GONE);
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("attendance:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("HomeWork", data);
                        Log.d("HomeWorkReport", response.body().toString());
                        isStaffNoticeBoardData.clear();
                        if (response.body().getStatus() == 1) {
                            rcyHomeworkReport.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            isStaffNoticeBoardData = response.body().getData();
                            isStaffHomeWorkReport = new StaffHomeWorkReport(isStaffNoticeBoardData, TeacherGeneralText.this, rytParent, TeacherGeneralText.this, staffId);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(TeacherGeneralText.this);
                            rcyHomeworkReport.setLayoutManager(mLayoutManager);
                            rcyHomeworkReport.setItemAnimator(new DefaultItemAnimator());
                            rcyHomeworkReport.setAdapter(isStaffHomeWorkReport);
                            rcyHomeworkReport.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            isStaffHomeWorkReport.notifyDataSetChanged();
                        } else {
                            isStaffNoticeBoardData.clear();
                            rcyHomeworkReport.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                        }

                    } else {
                        lblNoRecords.setVisibility(View.VISIBLE);
                        rcyHomeworkReport.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    lblNoRecords.setVisibility(View.VISIBLE);
                    rcyHomeworkReport.setVisibility(View.GONE);
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<StaffNoticeBoard> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void standardsAndSectoinsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherGeneralText.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherGeneralText.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = constructJsonArraySchoolStd();
        Call<JsonArray> call = apiService.GetStandardsAndSubjectsAsStaffWithoutNewOld(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("StdSecList:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("StdSecList:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());

                    if (js.length() > 0) {

                        TeacherStandardSectionsListModel stdSecList;
                        Log.d("json length", String.valueOf(js.length()));

                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);
                            if (jsonObject.getString("StandardId").equals("0")) {
                                showToast(getResources().getString(R.string.standard_sections_not_assigned));
                                finish();
                            } else {
                                stdSecList = new TeacherStandardSectionsListModel(jsonObject.getString("Standard"), jsonObject.getString("StandardId"));

                                listStdcode.add(jsonObject.getString("StandardId"));
                                listStd.add(jsonObject.getString("Standard"));

                                ArrayList<TeacherSectionsListNEW> listSections = new ArrayList<>();
                                ArrayList<TeacherSubjectModel> listSubjects = new ArrayList<>();
                                JSONArray jsArySections = jsonObject.getJSONArray("Sections");
                                if (jsArySections.length() > 0) {
                                    JSONObject jObjStd;
                                    TeacherSectionsListNEW sectionsList;
                                    for (int j = 0; j < jsArySections.length(); j++) {
                                        jObjStd = jsArySections.getJSONObject(j);
                                        if (jObjStd.getString("SectionId").equals("0")) {
                                            showToast(jObjStd.getString("SectionName"));
                                            finish();
                                        } else {
                                            sectionsList = new TeacherSectionsListNEW(jObjStd.getString("SectionName"), jObjStd.getString("SectionId"), "", false);
                                            listSections.add(sectionsList);
                                        }
                                    }
                                }
                                stdSecList.setListSectionsNew(listSections);
                                arrStandardsAndSectionsList.add(stdSecList);
                            }
                            adaStd = new ArrayAdapter<>(TeacherGeneralText.this, R.layout.teacher_spin_title, listStd);
                            adaStd.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                            attendance_spinStandard.setAdapter(adaStd);
                            attendance_spinStandard.setAdapter(adaStd);
                        }
                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.check_internet));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                onBackPressed();
            }
        });
    }

    private JsonObject constructJsonArraySchoolStd() {
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("SchoolId", Principal_SchoolId);
            jsonObjectSchool.addProperty("StaffID", Principal_staffId);
            jsonObjectSchool.addProperty("isAttendance", "0");

            Log.d("request", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }
        return jsonObjectSchool;
    }


    @Override
    public void onItemClick(StaffNoticeBoard.StaffNoticeBoardData item) {
        Log.d("item", item.getCreatedby());
        isHomeWorkReportData.add(item);
        imagePathList.clear();
        lblDatePicking.setVisibility(View.GONE);
        genText_relativeLayoutFoot.setVisibility(View.VISIBLE);
        btnUpdateHomeWork.setVisibility(View.VISIBLE);
        btnToSections.setVisibility(View.GONE);
        rytSmsHistory.setVisibility(View.VISIBLE);
        NestedScrollView.setVisibility(View.VISIBLE);
        rcyHomeworkReport.setVisibility(View.GONE);
        rlySection.setVisibility(View.GONE);
        rcyHomeworkReport.setVisibility(View.GONE);
        lblHomework.setTextColor(Color.WHITE);
        lblHomeworkReport.setTextColor(Color.BLACK);
        lblHomework.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
        lblHomeworkReport.setBackgroundColor(getResources().getColor(R.color.clr_white));
        et_tittle.setText(item.getHomeworktopic());
        etMessage.setText(item.getHomeworkcontent());
        btnAttachments.setEnabled(true);
        btnToSections.setEnabled(true);
        btnToStudents.setEnabled(true);
        btnStaffGroups.setEnabled(true);

        List<StaffNoticeBoard.StaffNoticeBoardData.StaffNoticeBoardFilePath> paths = item.getFile_path();
        if (paths != null) {
            for (StaffNoticeBoard.StaffNoticeBoardData.StaffNoticeBoardFilePath file : paths) {
                imagePathList.add(file.getPath());
            }
        }
        imagepathList = imagePathList.size();
        if (!imagePathList.isEmpty()) {
            if (imagePathList.get(0).contains(".mp3")) {
                lnrAttachments.setVisibility(View.GONE);
            } else {
                lnrAttachments.setVisibility(View.VISIBLE);
                lblAttachments.setText("Attachments +" + imagePathList.size());
            }
        } else {
            lnrAttachments.setVisibility(View.GONE);
        }

        if (!item.getFile_path().isEmpty()) {
            switch (item.getFile_path().get(0).getType()) {
                case "IMAGE":
                    strPDfFilePath = "";
                    voice_file_path = "";
                    lblAttachments.setText("Attachments +" + imagePathList.size());
                    openPreviewPopup();
                    break;
                case "PDF":
                    strPDfFilePath = item.getFile_path().get(0).getPath();
                    voice_file_path = "";
                    lblAttachments.setText("Attachments +" + imagePathList.size());
                    openPreviewPopup();
                    break;
                case "VOICE":
                    strPDfFilePath = "";
                    voice_file_path = item.getFile_path().get(0).getPath();
                    setupAudioPlayer(item.getFile_path().get(0).getPath());
                    break;
            }
        }
    }

    private void setupAudioPlayer(String url) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mediaPlayer.getDuration());
                tvRecordDuration.setText(formatDuration(mediaPlayer.getDuration()));

                imgBtnPlayPause.setOnClickListener(v -> {
                    if (isPlaying) {
                        imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                        imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
                        mediaPlayer.pause();
                        isPlaying = false;
                    } else {
                        imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_pause);
                        imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_clr_red));
                        mediaPlayer.start();
                        isPlaying = true;
                        updateSeekBar();
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mediaPlayer.seekTo(progress);
                            updateTimeText();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                });
            });

            lblAttachments.setVisibility(View.GONE);
            lnrAddVoice.setVisibility(View.VISIBLE);
            tvRecordDuration.setText(formatDuration(mediaPlayer.getDuration()));
            rlVoicePreview.setVisibility(View.VISIBLE);

            mediaPlayer.setOnCompletionListener(mp -> {
                isPlaying = false;
                imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));

                runOnUiThread(() -> {
                    seekBar.setProgress(0);
                    tvPlayDuration.setText("00:00 / " + formatDuration(mediaPlayer.getDuration()));
                });
            });


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateSeekBar() {
        if (mediaPlayer != null) {
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
            updateTimeText();
            if (isPlaying) {
                handler.postDelayed(this::updateSeekBar, 1000);
            }
        }
    }

    private void updateTimeText() {
        int current = mediaPlayer.getCurrentPosition();
        int total = mediaPlayer.getDuration();
        tvPlayDuration.setText(formatDuration(current) + " / " + formatDuration(total));
    }

    private String formatDuration(int ms) {
        int seconds = (ms / 1000) % 60;
        int minutes = (ms / 1000) / 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private void isHomeWorkSendReport() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        runOnUiThread(() -> {
            JsonObject jsonReqArray = constructJsonArraySMSHWReport();
            Call<JsonArray> call = apiService.InsertHomeWorkReport(jsonReqArray);
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    //  hideLoading();

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
                                    lytProgressBar.setVisibility(View.GONE);
                                    showAlert(strMsg, strStatus);
                                    AddCouponPoints.addPoints(TeacherGeneralText.this, Util_Common.SEND_HOMEWORK_POINTS);
                                } else {
                                    lytProgressBar.setVisibility(View.GONE);
                                    showAlert(strMsg, strStatus);
                                }
                            } else {
                                lytProgressBar.setVisibility(View.GONE);
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
                    //   hideLoading();
                    showToast(getResources().getString(R.string.check_internet));
                    lytProgressBar.setVisibility(View.GONE);
                    Log.d("Upload error:", t.getMessage() + "\n" + t);
                    showToast(t.toString());
                }
            });
        });
    }

    private void showAlert(String strMsg, final String status) {
//        lnrProgress.setVisibility(View.GONE);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherGeneralText.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (status.equals("1")) {

                    dialog.cancel();
                    Intent homescreen = new Intent(TeacherGeneralText.this, Teacher_AA_Test.class);
                    homescreen.putExtra("Homescreen", "1");
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                } else {
                    dialog.cancel();
                }


            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    public static boolean isDateToday(String pickedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH);

        try {
            Date selectedDate = sdf.parse(pickedDate);
            String todayStr = sdf.format(new Date());
            Date todayDate = sdf.parse(todayStr);

            return selectedDate != null && selectedDate.equals(todayDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }


    private JsonObject constructJsonArraySMSHWReport() {

        JsonObject jsonObjectSchoolstdgrp = new JsonObject();
        try {

            if (isDateToday(lblDatePicking.getText().toString())) {
                isArchive = false;
            } else {
                isArchive = true;
            }


            jsonObjectSchoolstdgrp.addProperty("school_id", schoolId);
            jsonObjectSchoolstdgrp.addProperty("staff_id", staffId);
            jsonObjectSchoolstdgrp.addProperty("subject_id", isHomeWorkReportData.get(0).getSubjectid());
            jsonObjectSchoolstdgrp.addProperty("homework_subject_id", isHomeWorkReportData.get(0).getHomeworksubjectid());
            jsonObjectSchoolstdgrp.addProperty("homework_topic", etMessage.getText().toString());
            jsonObjectSchoolstdgrp.addProperty("homework_text", et_tittle.getText().toString());
            jsonObjectSchoolstdgrp.addProperty("isarchive", isArchive);

            JsonArray jsonFiles = new JsonArray();
            for (int i = 0; i < UploadedS3URlList.size(); i++) {
                JsonObject jsonObjectclass = new JsonObject();
                jsonObjectclass.addProperty("path", UploadedS3URlList.get(i));
                jsonObjectclass.addProperty("type", HOMEWORK_TYPE);
                jsonFiles.add(jsonObjectclass);
            }
            jsonObjectSchoolstdgrp.add("file_path", jsonFiles);
            Log.d("Final_Array", jsonObjectSchoolstdgrp.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchoolstdgrp;
    }

    private void isUploadAWS(String contentType, String isType, String value) {

//        if (voice_file_path.equals("")) {
        isSelectedFilePathCount = imagePathList.size();
        Log.d("selectedImagePath", String.valueOf(imagePathList.size()));
        if (!imagePathList.isEmpty()) {
            Iterator<String> iterator = imagePathList.iterator();
            while (iterator.hasNext()) {
                String path = iterator.next();

                if (path.contains("amazonaws.")) {
                    UploadedS3URlList.add(path);
                    iterator.remove();
                }
            }
//            }
        }
        if (!imagePathList.isEmpty()) {
            for (int i = 0; i < imagePathList.size(); i++) {
                AwsUploadingFile(String.valueOf(imagePathList.get(i)), schoolId, contentType, isType, value);
            }
        } else {
            isHomeWorkSendReport();
        }
    }

    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type) {
        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherGeneralText.this);

        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this, countryID, true, false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);
                UploadedS3URlList.add(isAwsFile);
                if (isSelectedFilePathCount == UploadedS3URlList.size()) {
                    isHomeWorkSendReport();
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }
}

