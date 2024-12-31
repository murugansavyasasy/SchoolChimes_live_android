package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_MEETING_URL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.OnlineClassByStaffAdapter;
import com.vs.schoolmessenger.adapter.OnlineMeetingTypeAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnlineClassStaffListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.OnlineClassByStaffModel;
import com.vs.schoolmessenger.model.OnlineMeetingTypeModel;
import com.vs.schoolmessenger.model.OnlineStepsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TeacherMeetingURLScreen extends AppCompatActivity implements View.OnClickListener, CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener, OnlineClassStaffListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    public ArrayList<OnlineMeetingTypeModel> msgModelList = new ArrayList<>();
    public ArrayList<OnlineStepsModel> StepsList = new ArrayList<>();
    public ArrayList<OnlineClassByStaffModel> MeetingList = new ArrayList<>();
    EditText genText_txtTitle, genText_txtmessage, txtMeetingURL;
    TextView lblSelectDate, lblSelectTime, genText_msgcount;
    Button genText_btnToSections, btnSendToGroupsStandard;
    Spinner spinnerMeetingPlatform;
    String strDate, strCurrentDate, timeString, strTime;//strDuration
    int selDay, selMonth, selYear;
    String selHour, selMin;
    int minimumHour, minimumMinute;
    boolean bMinDateTime = true;
    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };
    PopupWindow pwindow;
    ArrayAdapter<String> platforms;
    List<String> meetingPlatforms = new ArrayList<>();
    int iRequestCode;
    OnlineMeetingTypeAdapter stepsAdapter;
    RelativeLayout rytParent;
    ImageView genTextPopup_ToolBarIvBack, imgEye, imgeditclose;
    String SchoolID, StaffID;
    LinearLayout lnrEye;
    Boolean SlectMeetingType = false;
    TextView tapCreate, tapUpcoming;
    RecyclerView recycleUpcomingMeetings;
    NestedScrollView ComposeMessgeNested;
    OnlineClassByStaffAdapter textAdapter;

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
        setContentView(R.layout.meeting_link_sharing_screen);

        btnSendToGroupsStandard = (Button) findViewById(R.id.btnSendToGroupsStandard);
        genText_btnToSections = (Button) findViewById(R.id.genText_btnToSections);
        lblSelectDate = (TextView) findViewById(R.id.lblSelectDate);
        lblSelectTime = (TextView) findViewById(R.id.lblSelectTime);
        genText_msgcount = (TextView) findViewById(R.id.genText_msgcount);
        spinnerMeetingPlatform = (Spinner) findViewById(R.id.spinnerMeetingPlatform);
        genText_txtTitle = (EditText) findViewById(R.id.genText_txtTitle);
        genText_txtmessage = (EditText) findViewById(R.id.genText_txtmessage);
        txtMeetingURL = (EditText) findViewById(R.id.txtMeetingURL);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        imgEye = (ImageView) findViewById(R.id.imgEye);
        imgeditclose = (ImageView) findViewById(R.id.imgeditclose);
        lnrEye = (LinearLayout) findViewById(R.id.lnrEye);
        genTextPopup_ToolBarIvBack = (ImageView) findViewById(R.id.genTextPopup_ToolBarIvBack);
        tapCreate = (TextView) findViewById(R.id.tapCreate);
        tapUpcoming = (TextView) findViewById(R.id.tapUpcoming);
        recycleUpcomingMeetings = (RecyclerView) findViewById(R.id.recycleUpcomingMeetings);
        ComposeMessgeNested = (NestedScrollView) findViewById(R.id.ComposeMessgeNested);

        btnSendToGroupsStandard.setOnClickListener(this);
        genText_btnToSections.setOnClickListener(this);
        lblSelectDate.setOnClickListener(this);
        lblSelectTime.setOnClickListener(this);
        lnrEye.setOnClickListener(this);
        imgeditclose.setOnClickListener(this);
        tapCreate.setOnClickListener(this);
        tapUpcoming.setOnClickListener(this);

        genText_txtmessage.addTextChangedListener(mTextEditorWatcher);

        tapCreate.setBackgroundColor(Color.parseColor("#b65908"));
        tapCreate.setTextColor(Color.parseColor("#ffffff"));


        String countryID = TeacherUtil_SharedPreference.getCountryID(TeacherMeetingURLScreen.this);
        if (countryID.equals("11")) {
            btnSendToGroupsStandard.setText("Send to Grade/Groups");
            genText_btnToSections.setText("Send to Grade/sections");
        }

        getOnlineMeetingTypes();

        strDate = dateFormater(System.currentTimeMillis(), "yyyy-MM-dd");
        strCurrentDate = strDate;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE, 10);
        selDay = cal.get(Calendar.DAY_OF_MONTH);
        selMonth = cal.get(Calendar.MONTH);// - 1;
        selYear = cal.get(Calendar.YEAR);

        minimumHour = cal.get(Calendar.HOUR_OF_DAY);
        minimumMinute = cal.get(Calendar.MINUTE);
        selHour = Integer.toString(minimumHour);
        selMin = Integer.toString(minimumMinute);

        setupDateTimeWarningPopUp();

        if ((listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMeetingURLScreen.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        }
        genTextPopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        if (iRequestCode == PRINCIPAL_MEETING_URL) {
            btnSendToGroupsStandard.setVisibility(View.VISIBLE);
            genText_btnToSections.setVisibility(View.VISIBLE);
        } else {
            btnSendToGroupsStandard.setVisibility(View.GONE);
            genText_btnToSections.setVisibility(View.VISIBLE);
        }


        genText_txtmessage.setOnTouchListener(new View.OnTouchListener() {
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

        spinnerMeetingPlatform.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position > 0) {
                    SlectMeetingType = true;
                    Constants.MeetingPlatform = meetingPlatforms.get(position - 1);
                    Constants.MeetingID = msgModelList.get(position - 1).getID();
                    Constants.MeetingType = msgModelList.get(position - 1).getType();
                    StepsList = msgModelList.get(position - 1).getListSteps();

                    if (StepsList.size() > 0) {
                        showStepsPopup();
                    }
                } else {
                    SlectMeetingType = false;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupDateTimeWarningPopUp() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_alert, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        TextView tvTitle = (TextView) layout.findViewById(R.id.popupRemove_tvTitle);
        tvTitle.setText(getResources().getString(R.string.date_time));

        TextView tvMsg = (TextView) layout.findViewById(R.id.popupRemove_tvMsg);
        tvMsg.setText(R.string.Set_from_for_today_meeting);

        TextView tvCancel = (TextView) layout.findViewById(R.id.popupRemove_tvCancel);
        tvCancel.setVisibility(View.GONE);


        TextView tvOk = (TextView) layout.findViewById(R.id.popupRemove_tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, 10);
                minimumHour = cal.get(Calendar.HOUR_OF_DAY);
                minimumMinute = cal.get(Calendar.MINUTE);
                selHour = Integer.toString(minimumHour);
                selMin = Integer.toString(minimumMinute);

                pwindow.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        backToResultActvity("SENT");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(iRequestCode, returnIntent);
        finish();
    }

    private void getOnlineMeetingTypes() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(TeacherMeetingURLScreen.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherMeetingURLScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherMeetingURLScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.getOnlineMeetingTypes();
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
                    msgModelList.clear();
                    meetingPlatforms.clear();

                    if (js.length() > 0) {
                        OnlineMeetingTypeModel msgModel;
                        meetingPlatforms.add("SELECT MEETING PLATFORM");
                        for (int i = 0; i < js.length(); i++) {

                            JSONObject jsonObject = js.getJSONObject(i);
                            meetingPlatforms.add(jsonObject.getString("type"));

                            ArrayList<OnlineStepsModel> listSteps = new ArrayList<>();
                            JSONArray stepsArray = jsonObject.getJSONArray("steps");
                            OnlineStepsModel stepsModel;
                            for (int j = 0; j < stepsArray.length(); j++) {
                                JSONObject step = stepsArray.getJSONObject(j);
                                stepsModel = new OnlineStepsModel(step.getString("id"), step.getString("st"));
                                listSteps.add(stepsModel);
                            }
                            msgModel = new OnlineMeetingTypeModel(jsonObject.getString("id"), jsonObject.getString("type"), listSteps);
                            msgModelList.add(msgModel);

                        }


                        platforms = new ArrayAdapter(TeacherMeetingURLScreen.this, R.layout.teacher_spin_title, meetingPlatforms);
                        platforms.setDropDownViewResource(R.layout.teacher_spin_dropdown);
                        spinnerMeetingPlatform.setAdapter(platforms);

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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(TeacherMeetingURLScreen.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showAlertRecords(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherMeetingURLScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);

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

    private void showStepsPopup() {


        LayoutInflater li = LayoutInflater.from(TeacherMeetingURLScreen.this);
        final View promptsView = li.inflate(R.layout.online_steps_popup, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                TeacherMeetingURLScreen.this);
        alertDialogBuilder.setView(promptsView);
        TextView lblCancel = (TextView) promptsView.findViewById(R.id.lblCancel);
        RecyclerView recycleSteps = (RecyclerView) promptsView.findViewById(R.id.recycleSteps);

        stepsAdapter = new OnlineMeetingTypeAdapter(StepsList, TeacherMeetingURLScreen.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recycleSteps.setLayoutManager(mLayoutManager);
        recycleSteps.setItemAnimator(new DefaultItemAnimator());
        recycleSteps.setAdapter(stepsAdapter);
        recycleSteps.getRecycledViewPool().setMaxRecycledViews(0, 80);
        stepsAdapter.notifyDataSetChanged();

        final AlertDialog alertDialog = alertDialogBuilder.create();
        lblCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.cancel();

            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.genText_btnToSections:

                if (SlectMeetingType && txtMeetingURL.getText().toString().length() > 0) {
                    saveValues();
                    Intent toSections = new Intent(TeacherMeetingURLScreen.this, TeacherStaffStandardSection.class);
                    toSections.putExtra("TO", "SEC");
                    toSections.putExtra("REQUEST_CODE", iRequestCode);
                    toSections.putExtra("SCHOOL_ID", SchoolID);
                    toSections.putExtra("STAFF_ID", StaffID);
                    startActivity(toSections);
                } else {
                    showToast(getResources().getString(R.string.meeting_platform_meetingpaste));
                }


                break;
            case R.id.btnSendToGroupsStandard:

                if (SlectMeetingType && txtMeetingURL.getText().toString().length() > 0) {
                    saveValues();
                    Intent toStaandardGroups = new Intent(TeacherMeetingURLScreen.this, TextStandardAndGroupList.class);
                    toStaandardGroups.putExtra("REQUEST_CODE", iRequestCode);
                    toStaandardGroups.putExtra("SCHOOL_ID", SchoolID);
                    toStaandardGroups.putExtra("STAFF_ID", StaffID);
                    startActivity(toStaandardGroups);
                } else {
                    showToast(getResources().getString(R.string.meeting_platform_meetingpaste));

                }

                break;

            case R.id.lblSelectDate:
                datePicker();
                break;
            case R.id.lblSelectTime:
                if (lblSelectDate.getText().toString().equalsIgnoreCase("Select Date")) {
                    showToast(getResources().getString(R.string.Please_select_date));
                } else {
                    timePicker();
                }
                break;

            case R.id.lnrEye:
                if (SlectMeetingType) {
                    if (StepsList.size() > 0) {
                        showStepsPopup();
                    }
                }
                break;

            case R.id.txtMeetingURL:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                break;

            case R.id.imgeditclose:
                txtMeetingURL.setText("");
                txtMeetingURL.setHint(R.string.paste_your_meeting_link);

                break;

            case R.id.tapCreate:
                tapCreate.setBackgroundColor(Color.parseColor("#b65908"));
                tapCreate.setTextColor(Color.parseColor("#ffffff"));

                tapUpcoming.setBackgroundColor(Color.parseColor("#ffffff"));
                tapUpcoming.setTextColor(Color.parseColor("#ff000000"));

                ComposeMessgeNested.setVisibility(View.VISIBLE);
                recycleUpcomingMeetings.setVisibility(View.GONE);

                break;

            case R.id.tapUpcoming:

                tapUpcoming.setBackgroundColor(Color.parseColor("#b65908"));
                tapUpcoming.setTextColor(Color.parseColor("#ffffff"));

                tapCreate.setBackgroundColor(Color.parseColor("#ffffff"));
                tapCreate.setTextColor(Color.parseColor("#ff000000"));

                ComposeMessgeNested.setVisibility(View.GONE);
                recycleUpcomingMeetings.setVisibility(View.VISIBLE);

                getUpcommingMeetings();
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                recycleUpcomingMeetings.setLayoutManager(layoutManager);
                recycleUpcomingMeetings.setItemAnimator(new DefaultItemAnimator());
                textAdapter = new OnlineClassByStaffAdapter(MeetingList, TeacherMeetingURLScreen.this, new OnlineClassStaffListener() {
                    @Override
                    public void onItemClick(OnlineClassByStaffModel item) {

                        showCancelMeetAlert(getResources().getString(R.string.Are_you_sure_want_cancel_meeting), item);

                    }
                });
                recycleUpcomingMeetings.setAdapter(textAdapter);
                break;
        }
    }

    private void showCancelMeetAlert(final String strMsg, final OnlineClassByStaffModel item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherMeetingURLScreen.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                cancelMeeting(item);
            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
        negativeButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void cancelMeeting(OnlineClassByStaffModel item) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherMeetingURLScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMeetingURLScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("header_id", item.getHeader_id());
        jsonObject.addProperty("subheader_id", item.getSubheader_id());
        jsonObject.addProperty("process_by", StaffID);
        Call<JsonArray> call = apiService.CancelOnlineClassRoom(jsonObject);
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
                            String strStatus = jsonObject.getString("status");
                            String strMsg = jsonObject.getString("message");
                            showCancelAlert(strStatus, strMsg);
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
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void showCancelAlert(final String strStatus, String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherMeetingURLScreen.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (strStatus.equals("1")) {
                    dialog.cancel();
                    getUpcommingMeetings();
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

    private void getUpcommingMeetings() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(TeacherMeetingURLScreen.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(TeacherMeetingURLScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherMeetingURLScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("schoolid", SchoolID);
        jsonObject.addProperty("staffid", StaffID);

        Log.d("Request", jsonObject.toString());


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetOnlineClassRoomsByStaffID(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    String status = jsonObject.getString("status");
                    String message = jsonObject.getString("message");

                    if (status.equals("1")) {
                        JSONArray data = jsonObject.getJSONArray("data");

                        textAdapter.clearAllData();
                        MeetingList.clear();


                        OnlineClassByStaffModel msgModel;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject js = data.getJSONObject(i);
                            msgModel = new OnlineClassByStaffModel(js.getString("header_id"), js.getString("subheader_id"),
                                    js.getString("topic"), js.getString("description"), js.getString("url"), js.getString("meetingtype"), js.getString("meetingdatetime"),
                                    js.getString("subject_name"), js.getString("target_type"), js.getString("created_on"), js.getInt("can_cancel"));
                            MeetingList.add(msgModel);
                        }


                        textAdapter.notifyDataSetChanged();


                    } else {
                        showAlertRecords(message);

                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void saveValues() {
        Constants.Title = genText_txtTitle.getText().toString();
        Constants.Description = genText_txtmessage.getText().toString();
        Constants.MeetingDate = strDate;
        Constants.MeetingTime = lblSelectTime.getText().toString();
        Constants.MeetingURL = txtMeetingURL.getText().toString();
    }

    private void timePicker() {
        int startHour = 0, startminute = 0;

        selHour = "";
        selMin = "";


        if (strCurrentDate.equals(strDate)) {
            startHour = minimumHour;
            startminute = minimumMinute;
        }

        RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnTimeSetListener(TeacherMeetingURLScreen.this)
                .setStartTime(startHour, startminute)
                .setForced12hFormat()
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.btn_sign_cancel));
        rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(TeacherMeetingURLScreen.this)
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setPreselectedDate(selYear, selMonth, selDay)
                .setDateRange(minDate, null)
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.btn_sign_cancel));
        cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
    }

    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString);
        String dateString = formatter.format(new Date(dateInMillis));
        Log.d("dateString", dateString);
        return dateString;
    }

    private String timeFormater(int hh, int mm) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        timeString = formatter.format(new Date(cal.getTimeInMillis()));
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm ", Locale.US);
        strTime = formatter1.format(new Date(cal.getTimeInMillis()));
        Log.d("TIME", strTime + "  " + timeString);

        return timeString;
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        lblSelectDate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
        strDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "yyyy-MM-dd");
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {

        selHour = Integer.toString(hourOfDay);
        selMin = Integer.toString(minute);

        if (strDate.equals(strCurrentDate) && (hourOfDay < minimumHour)) {
            pwindow.showAtLocation(lblSelectTime, Gravity.NO_GRAVITY, 0, 0);
            bMinDateTime = false;
            enableSubmitIfReady();

        } else if (strDate.equals(strCurrentDate) && (hourOfDay == minimumHour) && (minute < minimumMinute)) {
            pwindow.showAtLocation(lblSelectTime, Gravity.NO_GRAVITY, 0, 0);
            bMinDateTime = false;
            enableSubmitIfReady();

        } else {
            lblSelectTime.setText(timeFormater(hourOfDay, minute));
            bMinDateTime = true;
            enableSubmitIfReady();
        }
    }

    private void enableSubmitIfReady() {

        if (bMinDateTime && genText_txtmessage.getText().toString().length() > 0) {
            genText_btnToSections.setEnabled(true);
            btnSendToGroupsStandard.setEnabled(true);
        } else {
            genText_btnToSections.setEnabled(false);
            btnSendToGroupsStandard.setEnabled(false);
        }
    }


    @Override
    public void onItemClick(OnlineClassByStaffModel item) {

    }
}
