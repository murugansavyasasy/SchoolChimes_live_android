package com.vs.schoolmessenger.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SmsHistoryAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.assignment.ContentAdapter;
import com.vs.schoolmessenger.assignment.VideoUpload;
import com.vs.schoolmessenger.interfaces.SmsHistoryListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.SmsHistoryModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.GH_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EXAM_TEST;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_EXAM;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_VOICE_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.maxGeneralSMSCount;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.maxHomeWorkSMSCount;


public class TeacherGeneralText extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, SmsHistoryListener {

    Button btnNext;
    Button btnToSections, btnToStudents;
    EditText etMessage, et_tittle;
    TextView tvcount, tvtotcount, tvheader;
    String strmessage;
    RecyclerView rvSchoolsList;

    LinearLayout AddSubjectlayout;
    RelativeLayout Select_Exam_Date;
    int a = 0;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;

    private int iRequestCode;
    String loginType;
    String schoolId, staffId;


    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    String strfromdate, strdatevalue;
    TextView Date;

    Spinner spinnerList;
    NestedScrollView NestedScrollView;
    RelativeLayout rytSmsHistory;
    Button btn_Select_receipients, btnSelectSchool;

    String SelectedStaffId = "";
    String SelectedSchoolId = "";

    JsonObject jsonObject;
    Button btnHistorySlectReceipients, btnSlectSchool, btnSmsHistoryStandard, btnSmsHistorySectionOrStudents;

    private ArrayList<SmsHistoryModel> SmsHistoryList = new ArrayList<>();
    private ArrayList<SmsHistoryModel> SelectedSmsHistory = new ArrayList<>();
    SmsHistoryAdapter smsAdapter;
    RecyclerView SmsHistoryRecycle;
    String HistoryContent = "";
    String HistoryDescription = "";

    LinearLayout selectSpinner;
    int count = 0;

    RadioGroup TextRadio;
    RadioButton radioGeneralText, radioSmsHistory;
    Button btnStaffGroups;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_general_text);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        Log.d("Reqcode", String.valueOf(iRequestCode));

        if ((listschooldetails.size() == 1)) {
            schoolId = TeacherUtil_Common.Principal_SchoolId;
            staffId = TeacherUtil_Common.Principal_staffId;
        }
        else {
            schoolId = getIntent().getExtras().getString("SCHOOL_ID", "");
            staffId = getIntent().getExtras().getString("STAFF_ID", "");
        }
        tvheader = (TextView) findViewById(R.id.genTextPopup_tvTitle);
        btnNext = (Button) findViewById(R.id.genText_btnmsg);
        etMessage = (EditText) findViewById(R.id.genText_txtmessage);

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.genText_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });

        et_tittle = (EditText) findViewById(R.id.genText_txtTitle);
        tvcount = (TextView) findViewById(R.id.genText_msgcount);
        tvtotcount = (TextView) findViewById(R.id.genText_count);

        Select_Exam_Date = (RelativeLayout) findViewById(R.id.Select_Exam_Date);
        Date = (TextView) findViewById(R.id.Date);
        SmsHistoryRecycle = (RecyclerView) findViewById(R.id.SmsHistoryRecycle);


        spinnerList = (Spinner) findViewById(R.id.spinnerList);
        NestedScrollView = (NestedScrollView) findViewById(R.id.ComposeMessgeNested);
        rytSmsHistory = (RelativeLayout) findViewById(R.id.rytSmsHistory);
        btn_Select_receipients = (Button) findViewById(R.id.btn_Select_receipients);
        btnSelectSchool = (Button) findViewById(R.id.btnSelectSchool);
        selectSpinner = (LinearLayout) findViewById(R.id.selectSpinner);


        rvSchoolsList = (RecyclerView) findViewById(R.id.genText_rvSchoolsList);
        rvSchoolsList.setNestedScrollingEnabled(false);

        btnToSections = (Button) findViewById(R.id.genText_btnToSections);
        btnToSections.setOnClickListener(this);
        btnToStudents = (Button) findViewById(R.id.genText_btnToStudents);
        btnToStudents.setOnClickListener(this);
        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);

        btnStaffGroups = (Button) findViewById(R.id.btnStaffGroups);
        btnStaffGroups.setOnClickListener(this);
        btnStaffGroups.setEnabled(false);


        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherGeneralText.this);
        if(countryID.equals("11")){
            btnToSections.setText("To Grade or Sections");
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

        } else if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {
            tvheader.setText(getResources().getText(R.string.teacher_txt_composeExammsg));
            String totcount = String.valueOf(maxGeneralSMSCount);
            tvtotcount.setText(totcount);
            et_tittle.setHint(getResources().getText(R.string.teacher_txt_exam_title));//"HomeWork Topic");
            etMessage.setHint(getResources().getText(R.string.teacher_txt_exam_typemsg));
            etMessage.addTextChangedListener(mTextEditorWatcher);
            et_tittle.addTextChangedListener(mTextEditorWatcher);
            Date.setOnClickListener(this);

        } else {
            String totcount = String.valueOf(maxGeneralSMSCount);
            tvtotcount.setText(totcount);
            etMessage.addTextChangedListener(mTextEditorWatcher);
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
            } else {
                if(iRequestCode == STAFF_TEXT){
                    btnStaffGroups.setVisibility(View.VISIBLE);
                }
                rvSchoolsList.setVisibility(View.GONE);
                btnNext.setVisibility(View.GONE);
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


    }



    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

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
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherGeneralText.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        SelectedSchoolId="";
        SelectedStaffId="";

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
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
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
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
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
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(TeacherGeneralText.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(minDate, null)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.teacher_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }


    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending());
            arrSchoolList.add(ss);
        }

        if (iRequestCode == PRINCIPAL_TEXT) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherGeneralText.this, arrSchoolList, new TeacherSchoolListPrincipalListener() {
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
            TeacherSchoolsListAdapter schoolsListAdapter =
                    new TeacherSchoolsListAdapter(TeacherGeneralText.this, new TeacherOnCheckSchoolsListener() {
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

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
           // tvcount.setText("" + (maxGeneralSMSCount - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };

    private final TextWatcher mTextEditorWatcher1 = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
           // tvcount.setText("" + (maxHomeWorkSMSCount - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };


    @Override
    public void onBackPressed() {
        backToResultActvity("SENT");
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void enableSubmitIfReady() {

        boolean isReady = etMessage.getText().toString().length() > 0;
        boolean istitleReady = et_tittle.getText().toString().length() > 0;

        if (iRequestCode == PRINCIPAL_EXAM_TEST || iRequestCode == STAFF_TEXT_EXAM) {

            btnToSections.setEnabled((isReady) && (istitleReady));
            btnToStudents.setEnabled((isReady) && (istitleReady));
            btnStaffGroups.setEnabled((isReady) && (istitleReady));

        }
        else if (loginType.equals(LOGIN_TYPE_TEACHER) || (iRequestCode == STAFF_TEXT_HW || iRequestCode == STAFF_VOICE_HW) || iRequestCode == PRINCIPAL_TEXT_HW) {
            btnToSections.setEnabled(isReady);
            btnToStudents.setEnabled(isReady);
            btnStaffGroups.setEnabled(isReady);

            btn_Select_receipients.setEnabled(isReady);
            btnSelectSchool.setEnabled(isReady);

        }

        else btnNext.setEnabled(isReady);

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
                    Log.d("SelectedCount", "" + i_schools_count);
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


            default:
                break;
        }
    }

    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherGeneralText.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherGeneralText.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        Call<JsonArray> call = apiService.SendSMSToEntireSchools(jsonReqArray);
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
}

