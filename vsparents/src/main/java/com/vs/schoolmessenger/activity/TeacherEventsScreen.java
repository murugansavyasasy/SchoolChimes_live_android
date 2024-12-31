package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_EVENTS;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class TeacherEventsScreen extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener, RadialTimePickerDialogFragment.OnTimeSetListener {

    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "fragment_time_picker_name";
    EditText etMessage, etTopic;
    TextView tvcount;
    String strmessage;
    RecyclerView rvSchoolsList;
    TextView tvDate, tvTime;
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
            //tvcount.setText("" + (460 - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };
    PopupWindow pwindow;
    private final ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private final ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;
    private int iRequestCode;

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
        setContentView(R.layout.teacher_activity_events_screen);


        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        etMessage = (EditText) findViewById(R.id.events_txtmessage);
        tvcount = (TextView) findViewById(R.id.events_msgcount);
        etMessage.addTextChangedListener(mTextEditorWatcher);


        etMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.events_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }
                return false;
            }
        });


        tvDate = (TextView) findViewById(R.id.events_tvDate);
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        tvTime = (TextView) findViewById(R.id.events_tvTime);
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

        etTopic = (EditText) findViewById(R.id.events_txtTitle);
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

        rvSchoolsList = (RecyclerView) findViewById(R.id.events_rvSchoolsList);
        ImageView ivBack = (ImageView) findViewById(R.id.eventsPopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        listSchoolsAPI();
        setMinDateTime();
        setupDateTimeWarningPopUp();
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

    }

    @Override
    public void onBackPressed() {
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

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getSchoolNameRegional(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(), ss.getIsPaymentPending(), ss.getIsSchoolType(), ss.getIsBiometricEnable(),ss.getAllowDownload());
            arrSchoolList.add(ss);
        }
        if (iRequestCode == PRINCIPAL_EVENTS) {
            TeacherSchoolListForPrincipalAdapter schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(TeacherEventsScreen.this, arrSchoolList, false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            String title = etTopic.getText().toString();
                            String message = etMessage.getText().toString();
                            if (enableSubmitIfReady()) {
                                Intent inPrincipal = new Intent(TeacherEventsScreen.this, TeacherStandardsAndGroupsList.class);
                                inPrincipal.putExtra("REQUEST_CODE", iRequestCode);
                                inPrincipal.putExtra("SCHOOL_ID", item.getStrSchoolID());
                                inPrincipal.putExtra("STAFF_ID", item.getStrStaffID());
                                inPrincipal.putExtra("TITTLE", title);
                                inPrincipal.putExtra("MESSAGE", message);
                                inPrincipal.putExtra("DATE", strDate);
                                inPrincipal.putExtra("TIME", tvTime.getText().toString());
                                Log.d("Dateandtime", strDate + strTime);
                                startActivityForResult(inPrincipal, iRequestCode);
                            } else showToast(getResources().getString(R.string.fill_all_details));
                        }
                    });

            rvSchoolsList.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEventsScreen.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherEventsScreen.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        } else {


            TeacherSchoolsListAdapter schoolsListAdapter =
                    new TeacherSchoolsListAdapter(TeacherEventsScreen.this, new TeacherOnCheckSchoolsListener() {
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
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherEventsScreen.this);
            rvSchoolsList.setLayoutManager(layoutManager);
            rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherEventsScreen.this, LinearLayoutManager.VERTICAL));
            rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
            rvSchoolsList.setAdapter(schoolsListAdapter);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public boolean enableSubmitIfReady() {
        boolean isTitleReady = etMessage.getText().toString().length() > 0;
        boolean isContentReady = etTopic.getText().toString().length() > 0;
        //&& (i_schools_count > 0))
        return isContentReady && isTitleReady && bMinDateTime;
    }


    private void setMinDateTime() {
        tvDate.setText(dateFormater(System.currentTimeMillis(), "dd MMM yyyy"));
        strDate = dateFormater(System.currentTimeMillis(), "dd-MM-yyyy");
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


        tvTime.setText(timeFormater(minimumHour, minimumMinute));

    }

    private void datePicker() {
        Calendar today = Calendar.getInstance();
        MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(today);
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialog)
                .setOnDateSetListener(TeacherEventsScreen.this)
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
                .setOnTimeSetListener(TeacherEventsScreen.this)
                .setStartTime(startHour, startminute)
                .setForced12hFormat()
                .setDoneText(getResources().getString(R.string.teacher_btn_ok))
                .setCancelText(getResources().getString(R.string.btn_sign_cancel));
        rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
    }


    private String timeFormater(int hh, int mm) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hh);
        cal.set(Calendar.MINUTE, mm);

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        timeString = formatter.format(new Date(cal.getTimeInMillis()));
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm ", Locale.US);
        strTime = formatter1.format(new Date(cal.getTimeInMillis()));

        return timeString;
    }


    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selDay = dayOfMonth;
        selMonth = monthOfYear;
        selYear = year;

        tvDate.setText(dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd MMM yyyy"));
        strDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd/MM/yyyy");
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        tvTime.setText(timeFormater(hourOfDay, minute));

        selHour = Integer.toString(hourOfDay);
        selMin = Integer.toString(minute);

        if (strDate.equals(strCurrentDate) && (hourOfDay < minimumHour)) {
            pwindow.showAtLocation(tvTime, Gravity.NO_GRAVITY, 0, 0);
            bMinDateTime = false;
            enableSubmitIfReady();

        } else if (strDate.equals(strCurrentDate) && (hourOfDay == minimumHour) && (minute < minimumMinute)) {
            pwindow.showAtLocation(tvTime, Gravity.NO_GRAVITY, 0, 0);
            bMinDateTime = false;
            enableSubmitIfReady();

        } else {
            bMinDateTime = true;
            enableSubmitIfReady();
        }
    }

    private void setupDateTimeWarningPopUp() {
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_alert, null);

        pwindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pwindow.setContentView(layout);

        TextView tvTitle = (TextView) layout.findViewById(R.id.popupRemove_tvTitle);
        tvTitle.setText(getResources().getString(R.string.date_time));

        TextView tvMsg = (TextView) layout.findViewById(R.id.popupRemove_tvMsg);
        tvMsg.setText(getResources().getString(R.string.set_10minis));

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
                tvTime.setText(timeFormater(minimumHour, minimumMinute));

                pwindow.dismiss();
            }
        });
    }

}
