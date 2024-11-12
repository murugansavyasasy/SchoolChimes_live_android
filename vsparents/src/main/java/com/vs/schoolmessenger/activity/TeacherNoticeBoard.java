package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.adapter.TextCircularListAdapternew;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherNoticeBoard extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    public ArrayList<String> isSchoolList = new ArrayList<>();
    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    Button btnNext;
    EditText etMessage, etTopic;
    RelativeLayout RlaNoticeBoard;
    TextView tvcount;
    String strmessage;
    RecyclerView rvSchoolsList;
    LinearLayout nb_composemsg;
    int isDateType = 1;
    int selDay, selMonth, selYear;
    String loginType;
    TextView lblFromDate, lblToDate;
    DatePickerDialog datePickerDialog;
    TextView lblHomework, lblHomeworkReport;
    TextCircularListAdapternew tvadapter;
    RecyclerView text_rvCircularList;
    String SchoolID, StaffID;
    TextView norecords;
    EditText Searchable;
    ImageView imgSearch;
    RelativeLayout rytSearch;
    TextView LoadMore;
    ArrayList<MessageModel> arrayList;
    String Role = "";
    Spinner schoolList;
    LinearLayout rlaTitle;
    private final ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private final ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //tvcount.setText("" + (460 - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };
    private int iRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_notice_board);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        btnNext = (Button) findViewById(R.id.nb_btnmsg);
        etMessage = (EditText) findViewById(R.id.nb_txtmessage);
        tvcount = (TextView) findViewById(R.id.nb_msgcount);
        lblFromDate = (TextView) findViewById(R.id.lblFromDate);
        lblToDate = (TextView) findViewById(R.id.lblToDate);
        etMessage.addTextChangedListener(mTextEditorWatcher);


        lblHomework = findViewById(R.id.lblHomework);
        lblHomeworkReport = findViewById(R.id.lblHomeworkReport);
        nb_composemsg = findViewById(R.id.nb_composemsg);
        RlaNoticeBoard = findViewById(R.id.RlaNoticeBoard);
        schoolList = findViewById(R.id.schoolList);
        rlaTitle = findViewById(R.id.rlaTitle);


        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        text_rvCircularList = findViewById(R.id.text_rvCircularList);
        rytSearch = findViewById(R.id.rytSearch);
        norecords = findViewById(R.id.lblNoMessagesLable);


        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMore.setVisibility(View.GONE);
                LoadMorecircularsNoticeboardAPI();

            }
        });

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherNoticeBoard.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherNoticeBoard.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (tvadapter == null)
                    return;

                if (tvadapter.getItemCount() < 1) {
                    text_rvCircularList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        text_rvCircularList.setVisibility(View.VISIBLE);
                    }
                } else {
                    text_rvCircularList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.VISIBLE);
                }
                filterlist(editable.toString());
            }
        });

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.nb_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });

        lblHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nb_composemsg.setVisibility(View.VISIBLE);
                RlaNoticeBoard.setVisibility(View.GONE);

                lblHomework.setTextColor(Color.WHITE);
                lblHomeworkReport.setTextColor(Color.BLACK);
                lblHomework.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
                lblHomeworkReport.setBackgroundColor(getResources().getColor(R.color.clr_white));
            }
        });

        lblHomeworkReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nb_composemsg.setVisibility(View.GONE);
                RlaNoticeBoard.setVisibility(View.VISIBLE);

                lblHomeworkReport.setTextColor(Color.WHITE);
                lblHomework.setTextColor(Color.BLACK);
                lblHomeworkReport.setBackgroundColor(getResources().getColor(R.color.clr_yellow));
                lblHomework.setBackgroundColor(getResources().getColor(R.color.clr_white));

                if (listschooldetails.size() == 1) {
                    circularsNoticeboardAPI();
                } else {
                    isSchoolList();
                }
            }
        });

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("MMMM dd , yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        lblFromDate.setText(formattedDate);
        lblToDate.setText(formattedDate);

        lblFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateType = 1;
                openDatePicker();
            }
        });
        lblToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDateType = 2;
                openDatePicker();

            }
        });


        etTopic = (EditText) findViewById(R.id.nb_txtTitle);
        etTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSubmitIfReady();
            }
        });

        rvSchoolsList = (RecyclerView) findViewById(R.id.nb_rvSchoolsList);
        rvSchoolsList.setNestedScrollingEnabled(false);

        ImageView ivBack = (ImageView) findViewById(R.id.nbPopup_ToolBarIvBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                Log.d("SelectedCount", String.valueOf(i_schools_count));
                if (i_schools_count > 0) {
                    SendEmergencyVoiceGroupheadAPI();
                } else {
                    btnNext.setEnabled(true);
                    showToast(getResources().getString(R.string.teacher_Select_school));
                }
            }
        });


        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherNoticeBoard.this);
        if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        } else if (loginType.equals(LOGIN_TYPE_HEAD)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        } else if (loginType.equals(LOGIN_TYPE_ADMIN)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        } else if (loginType.equals(LOGIN_TYPE_TEACHER)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        }


    }

    public void isSchoolList() {
        Log.d("isSchoolList+++", String.valueOf(isSchoolList.size()));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.ic_down, isSchoolList);
        adapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        schoolList.setAdapter(adapter);
        schoolList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String isSchoolName = (String) parent.getItemAtPosition(position);
                Log.d("isSchoolName", String.valueOf(isSchoolName));

                SchoolID = String.valueOf(listschooldetails.get(position).getStrSchoolID());
                circularsNoticeboardAPI();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void openDatePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener((CalendarDatePickerDialogFragment.OnDateSetListener) TeacherNoticeBoard.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(minDate, null)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.teacher_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        String isPickingDate = selDay + "-" + (selMonth + 1) + "-" + selYear;
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMMM dd , yyyy", Locale.US);

        try {
            Date date = inputDateFormat.parse(isPickingDate);
            String outputDateStr = outputDateFormat.format(date);
            if (isDateType == 1) {
                if (lblToDate.getText().toString().equals("")) {
                    lblFromDate.setText(outputDateStr);
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d , yyyy", Locale.US);
                    try {
                        Date date1 = dateFormat.parse(lblToDate.getText().toString());
                        Date date2 = dateFormat.parse(outputDateStr);

                        if (date1.compareTo(date2) < 0) {
                            lblFromDate.setText(outputDateStr);
                            Toast.makeText(getApplicationContext(), "Please select toDate is after fromDate", Toast.LENGTH_SHORT).show();
                            lblToDate.setText("");
                        } else {
                            lblFromDate.setText(outputDateStr);
                        }
                        enableSubmitIfReady();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d , yyyy", Locale.US);
                try {
                    Date date1 = dateFormat.parse(lblFromDate.getText().toString());
                    Date date2 = dateFormat.parse(outputDateStr);

                    if (date1.compareTo(date2) > 0) {
                        Toast.makeText(getApplicationContext(), "Please select toDate is after fromDate", Toast.LENGTH_SHORT).show();
                        lblToDate.setText("");
                    } else {
                        lblToDate.setText(outputDateStr);
                    }
                    enableSubmitIfReady();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

    }

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            isSchoolList.add(listschooldetails.get(i).getStrSchoolName());
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getSchoolNameRegional(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(), ss.getIsPaymentPending(), ss.getIsSchoolType(), ss.getIsBiometricEnable());
            arrSchoolList.add(ss);

            Role = TeacherUtil_SharedPreference.getRole(TeacherNoticeBoard.this);
            if (Role.equals("p3")) {
                nb_composemsg.setVisibility(View.GONE);
                if (listschooldetails.size() == 1) {
                    rlaTitle.setVisibility(View.GONE);
                    circularsNoticeboardAPI();
                } else {
                    rlaTitle.setVisibility(View.GONE);
                    RlaNoticeBoard.setVisibility(View.VISIBLE);
                    schoolList.setVisibility(View.VISIBLE);
                    nb_composemsg.setVisibility(View.GONE);
                    isSchoolList();
                }
            } else {
                rlaTitle.setVisibility(View.VISIBLE);
                nb_composemsg.setVisibility(View.VISIBLE);
                RlaNoticeBoard.setVisibility(View.GONE);
            }
        }


        TeacherSchoolsListAdapter schoolsListAdapter =
                new TeacherSchoolsListAdapter(TeacherNoticeBoard.this, new TeacherOnCheckSchoolsListener() {
                    @Override
                    public void school_addSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (!seletedschoollist.contains(school))) {
                            seletedschoollist.add(school);
                            i_schools_count++;
                            enableSubmitIfReady();
                        }
                    }

                    @Override
                    public void school_removeSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (seletedschoollist.contains(school))) {
                            seletedschoollist.remove(school);
                            i_schools_count--;
                            enableSubmitIfReady();
                        }
                    }
                }, arrSchoolList);

        rvSchoolsList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherNoticeBoard.this);
        rvSchoolsList.setLayoutManager(layoutManager);
        rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherNoticeBoard.this, LinearLayoutManager.VERTICAL));
        rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
        rvSchoolsList.setAdapter(schoolsListAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void enableSubmitIfReady() {
        boolean isTitleReady = etMessage.getText().toString().length() > 0;
        boolean isContentReady = etTopic.getText().toString().length() > 0;

        if (isContentReady && isTitleReady && (i_schools_count > 0)) {
            btnNext.setEnabled(!lblToDate.getText().toString().equals("") && !lblFromDate.getText().toString().equals(""));
        } else if ((isContentReady && isTitleReady) && (i_schools_count == 0)) {
            btnNext.setEnabled(!lblToDate.getText().toString().equals("") && !lblFromDate.getText().toString().equals(""));
        } else {
            btnNext.setEnabled(false);
        }


    }

    public void validation() {
        strmessage = etMessage.getText().toString().trim();
    }


    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherNoticeBoard.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherNoticeBoard.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        Call<JsonArray> call = apiService.ManageNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

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
                            showToast(getResources().getString(R.string.check_internet));
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
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherNoticeBoard.this);

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

    private JsonObject constructJsonArrayMgtSchoolsGrouphead() {
        String strdescription = etTopic.getText().toString();
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("TopicHeading", strdescription);
            jsonObjectSchool.addProperty("TopicBody", strmessage);

            SimpleDateFormat inputDateFormat = new SimpleDateFormat("MMMM dd , yyyy", Locale.US);
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

            // isFromDate
            try {
                Date date = inputDateFormat.parse(lblFromDate.getText().toString());
                String outputDateStr = outputDateFormat.format(date);
                jsonObjectSchool.addProperty("FromDate", outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // isToDate
            try {
                Date date = inputDateFormat.parse(lblToDate.getText().toString());
                String outputDateStr = outputDateFormat.format(date);
                jsonObjectSchool.addProperty("ToDate", outputDateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                JsonObject jsonObjectschoolId = new JsonObject();
                jsonObjectschoolId.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                jsonObjectschoolId.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                jsonArrayschool.add(jsonObjectschoolId);
            }

            jsonObjectSchool.add("Schools", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchool;
    }


    private void filterlist(String s) {
        ArrayList<MessageModel> temp = new ArrayList();
        for (MessageModel d : msgModelList) {

            if (d.getMsgContent().toLowerCase().contains(s.toLowerCase()) || d.getMsgDate().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }
        }
        tvadapter.updateList(temp);
    }


    private void circularsNoticeboardAPI() {
        LoadMore.setVisibility(View.VISIBLE);
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(TeacherNoticeBoard.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getNoticeBoardStaff(StaffID, SchoolID);
        Call<JsonArray> call = apiService.GetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if (strStatus.equals("1")) {
                            norecords.setVisibility(View.GONE);
                            text_rvCircularList.setVisibility(View.VISIBLE);
                            rytSearch.setVisibility(View.VISIBLE);
                            MessageModel msgModel;
                            msgModelList.clear();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"), "", false);
                                msgModelList.add(msgModel);
                            }
                            tvadapter = new TextCircularListAdapternew(msgModelList, TeacherNoticeBoard.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            text_rvCircularList.setLayoutManager(layoutManager);
                            text_rvCircularList.setItemAnimator(new DefaultItemAnimator());
                            text_rvCircularList.setAdapter(tvadapter);

                        } else {

                            RlaNoticeBoard.setVisibility(View.VISIBLE);
                            norecords.setVisibility(View.VISIBLE);
                            norecords.setText(strMessage);
                            text_rvCircularList.setVisibility(View.GONE);
                            rytSearch.setVisibility(View.GONE);
                        }
                    } else {
                        norecords.setVisibility(View.VISIBLE);
                        text_rvCircularList.setVisibility(View.GONE);
                        rytSearch.setVisibility(View.GONE);
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }


                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                norecords.setVisibility(View.VISIBLE);
                text_rvCircularList.setVisibility(View.GONE);
                rytSearch.setVisibility(View.GONE);
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }


    private void LoadMorecircularsNoticeboardAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(TeacherNoticeBoard.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherNoticeBoard.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetnoticeboardStaffSze(StaffID, SchoolID);
        Call<JsonArray> call = apiService.LoadMoreGetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());
                norecords.setVisibility(View.GONE);
                LoadMore.setVisibility(View.GONE);
                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            text_rvCircularList.setVisibility(View.VISIBLE);
                            MessageModel msgModel;
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"), "", jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                            }
                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            tvadapter = new TextCircularListAdapternew(arrayList, TeacherNoticeBoard.this);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            text_rvCircularList.setLayoutManager(layoutManager);
                            text_rvCircularList.setItemAnimator(new DefaultItemAnimator());
                            text_rvCircularList.setAdapter(tvadapter);

                        } else {
                            showAlertRecords(strMessage);
                        }
                    } else {
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }
                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
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


    private void showAlertRecords(String no_events_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherNoticeBoard.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_events_found);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                // finish();
            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

    }
}

