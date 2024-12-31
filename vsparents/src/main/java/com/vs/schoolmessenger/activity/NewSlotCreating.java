package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import static com.vs.schoolmessenger.util.Util_Common.isSelectedDate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.annimon.stream.Stream;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.CalendarWeekDay;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.applandeo.materialcalendarview.utils.DateUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.Ptm.DateAvailableAdapter;
import com.vs.schoolmessenger.adapter.Ptm.DateShowingAdapter;
import com.vs.schoolmessenger.adapter.Ptm.SectionListAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatePicking;
import com.vs.schoolmessenger.model.GetSection;
import com.vs.schoolmessenger.model.GetSectionData;
import com.vs.schoolmessenger.model.SectionAndClass;
import com.vs.schoolmessenger.model.SlotsAvailableTimeData;
import com.vs.schoolmessenger.model.TimeSlot;
import com.vs.schoolmessenger.model.slotsTime;
import com.vs.schoolmessenger.model.std_sec_details;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.TimeSlotGenerator;
import com.vs.schoolmessenger.util.Util_Common;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class NewSlotCreating extends AppCompatActivity implements OnSelectDateListener {

    TextView lblPickFromTime, lblPickToTime;
    RelativeLayout lblSelectDate;
    EditText edtDiscussion;
    Spinner spinnerSelectionMode, spinnerDuration, spinnerBreakBetween, spinnerSlots;
    Switch SwitchBreak;
    int isNeedBreak = 0;
    List<String> isDifferentDates = new ArrayList<>();
    TextView EdtOnlineLink;
    RelativeLayout rlaLink;
    LinearLayout lnrBreak;
    GridView gridViewSection;
    String SchoolID, StaffID;
    String isSlotsCount = "0", isDuration = "10", isBreakDuration = "0 Mins";
    String isSelectMode;
    SectionListAdapter isSectionListAdapter;
    List<TimeSlot> slots;
    List<DatePicking> dynamicDateSlotsList = new ArrayList<>();
    List<SlotsAvailableTimeData> isSlotsAvailableTimeData = new ArrayList<>();
    String isCurrentTime;
    String isCurrentDate;
    EditText edt_slotDuration;
    ImageView imgClearLink;
    TextView lblPurPose, lblSelectMode, lblSelectClass, lblOpenSlot, lblSlotDuration, lblNeedBreak, txtSelectDate;
    private int fromHour, fromMinute, toHour, toMinute;
    private SimpleDateFormat timeFormat;

    public static long getDateDifferenceInDays(int year, int month, int day) {
        Calendar currentDate = Calendar.getInstance();
        Calendar specifiedDate = Calendar.getInstance();
        specifiedDate.set(year, month - 1, day); // Month is zero-based
        long differenceInMillis = specifiedDate.getTimeInMillis() - currentDate.getTimeInMillis();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMillis);
        return differenceInDays;
    }

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
        setContentView(R.layout.slotcreating);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Slot_Creation));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util_Common.isSelectedDateList.clear();
                finish();
            }
        });
        lblPickFromTime = findViewById(R.id.lblPickFromTime);
        lblPurPose = findViewById(R.id.lblPurPose);
        lblSelectMode = findViewById(R.id.lblSelectMode);
        txtSelectDate = findViewById(R.id.txtSelectDate);
        lblNeedBreak = findViewById(R.id.lblNeedBreak);
        lblSlotDuration = findViewById(R.id.lblSlotDuration);
        lblSelectClass = findViewById(R.id.lblSelectClass);
        lblOpenSlot = findViewById(R.id.lblOpenSlot);
        lblPickToTime = findViewById(R.id.lblPickToTime);
        lblSelectDate = findViewById(R.id.lblSelectDate);
        edtDiscussion = findViewById(R.id.edtDiscussion);
        spinnerSelectionMode = findViewById(R.id.spinnerSelectionMode);
        spinnerDuration = findViewById(R.id.spinnerDuration);
        spinnerBreakBetween = findViewById(R.id.spinnerBreakBetween);
        spinnerSlots = findViewById(R.id.spinnerSlots);
        SwitchBreak = findViewById(R.id.SwitchBreak);
        EdtOnlineLink = findViewById(R.id.EdtOnlineLink);
        rlaLink = findViewById(R.id.rlaLink);
        lnrBreak = findViewById(R.id.lnrBreak);
        gridViewSection = findViewById(R.id.gridViewSection);
        edt_slotDuration = findViewById(R.id.edt_slotDuration);
        imgClearLink = findViewById(R.id.imgClearLink);

        timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());


        lblPurPose.setTypeface(null, Typeface.BOLD);
        lblSelectMode.setTypeface(null, Typeface.BOLD);
        lblSelectClass.setTypeface(null, Typeface.BOLD);
        lblOpenSlot.setTypeface(null, Typeface.BOLD);
        lblSlotDuration.setTypeface(null, Typeface.BOLD);
        lblNeedBreak.setTypeface(null, Typeface.BOLD);
        txtSelectDate.setTypeface(null, Typeface.BOLD);


        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(NewSlotCreating.this).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(NewSlotCreating.this).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
            TeacherUtil_Common.Principal_staffId = StaffID;
        }


        EdtOnlineLink.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard.hasPrimaryClip()) {
                    ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    String pastedText = item.getText() != null ? item.getText().toString() : "";

                    if (isValidURL(pastedText)) {
                        imgClearLink.setVisibility(View.VISIBLE);
                        EdtOnlineLink.setText(pastedText);
                    } else {
                        imgClearLink.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.Onlylinksareallowed), Toast.LENGTH_SHORT).show();
                    }
                }
                return true; // Return true to indicate the event is handled
            }
        });

        imgClearLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EdtOnlineLink.setText("");
                imgClearLink.setVisibility(View.GONE);
            }
        });


        lblSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!lblPickFromTime.getText().toString().equals("Select from time") && !lblPickToTime.getText().toString().equals("Select to time")) {
                    if (!edtDiscussion.getText().toString().equals("")) {
                        //     if (!isSelectMode.equals("Select Mode")) {
                        ArrayList<SectionAndClass> selectedIds = isSectionListAdapter.getSelectedIds();
                        if (selectedIds.size() > 0) {
                            if (isSelectMode.equals("Online")) {
                                if (!EdtOnlineLink.getText().toString().equals("Paste your link here.")) {
                                    if (isDuration.equals("Custom")) {
                                        isDuration = "";
                                        if (!edt_slotDuration.getText().toString().equals("")) {
                                            isDuration = edt_slotDuration.getText().toString();
                                            DatePicker();
                                        } else {
                                            Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Entertheslotminutes), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        DatePicker();
                                    }
                                } else {
                                    Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Pasteyourlinkhere), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                if (isDuration.equals("Custom")) {
                                    isDuration = "";
                                    if (!edt_slotDuration.getText().toString().equals("")) {
                                        isDuration = edt_slotDuration.getText().toString();
                                        DatePicker();
                                    } else {
                                        Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Entertheslotminutes), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    DatePicker();
                                }
                            }
                        } else {
                            Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Selectthesection), Toast.LENGTH_SHORT).show();
                        }
//                        } else {
//                            Toast.makeText(NewSlotCreating.this, "Select the mode", Toast.LENGTH_SHORT).show();
//                        }
                    } else {
                        Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Enterthepurposeofdiscussion), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Choose_the_from_and_to_time), Toast.LENGTH_SHORT).show();
                }
            }
        });

        lblPickFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(true);
            }
        });

        lblPickToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!lblPickFromTime.getText().toString().equals("Select from time")) {
                    showTimePickerDialog(false);
                } else {
                    Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Choose_the_from_time), Toast.LENGTH_SHORT).show();

                }
            }
        });


        SwitchBreak.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNeedBreak = 1;
                    isBreakDuration = "5 Mins";
                    isSlotsCount = "1";
                    lnrBreak.setVisibility(View.VISIBLE);
                } else {
                    isNeedBreak = 0;
                    isBreakDuration = "0 Mins";
                    isSlotsCount = "0";
                    lnrBreak.setVisibility(View.GONE);
                }
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        isCurrentTime = sdf.format(new Date());
        Log.d("isCurrentTime", isCurrentTime);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = sdf1.format(new Date());
        isCurrentDate = currentDate;

        getSection();
        isSlotPicking();
        isBreakPicking();
        isDurationPicking();
        isSelectModePicking();

    }

    private boolean isValidURL(CharSequence url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        } else {
            return Patterns.WEB_URL.matcher(url).matches();
        }
    }

    void showTimePickerDialog(final boolean isFromTime) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        final TimePickerDialog mTimePicker = new TimePickerDialog(NewSlotCreating.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time = selectedHour + ":" + selectedMinute;
                SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", Locale.US); // 24-hour format
                Date date = null;
                try {
                    date = fmt.parse(time);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa", Locale.US); // 12-hour format
                String formattedTime = fmtOut.format(date);

                if (isFromTime) {
                    lblPickFromTime.setText(formattedTime);
                } else {
                    try {
                        String fromTime = lblPickFromTime.getText().toString();
                        if (fromTime.isEmpty()) {
                            Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Please_select_the_from_time_first), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Date fromTimeDate = fmtOut.parse(fromTime);
                        Date toTimeDate = fmtOut.parse(formattedTime);
                        if (toTimeDate.before(fromTimeDate) || toTimeDate.equals(fromTimeDate)) {
                            Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.To_time_cannot_be_before_or_equal_to_from_time), Toast.LENGTH_SHORT).show();
                        } else {
                            lblPickToTime.setText(formattedTime);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, hour, minute, false);

        mTimePicker.setOnShowListener(dialog -> {
            Button positiveButton = mTimePicker.getButton(TimePickerDialog.BUTTON_POSITIVE);
            Button negativeButton = mTimePicker.getButton(TimePickerDialog.BUTTON_NEGATIVE);
            positiveButton.setTextColor(ContextCompat.getColor(NewSlotCreating.this, R.color.teacher_colorPrimaryDark));
            negativeButton.setTextColor(ContextCompat.getColor(NewSlotCreating.this, R.color.teacher_colorPrimaryDark));
        });

        mTimePicker.setTitle(getResources().getString(R.string.Select_Time));
        mTimePicker.show();
    }

    private void isTimeCompare(String isPickTime, String isCurrentTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        try {
            Date pickTime = sdf.parse(isPickTime);
            Date currentTime = sdf.parse(isCurrentTime);

            if (pickTime != null && currentTime != null) {
                if (pickTime.before(currentTime)) {
                    isTimeScheduleError(getResources().getString(R.string.From_time_is_past_time_today));
                    System.out.println(isPickTime + " is before " + isCurrentTime);
                } else if (pickTime.after(currentTime)) {
                    isTimeSeparate();
                } else {
                    isTimeScheduleError(getResources().getString(R.string.From_time_is_past_time_today));
                    System.out.println(isPickTime + " is equal to " + isCurrentTime);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void isTimeScheduleError(String isMsg) {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert))
                .setMessage(isMsg)
                .setPositiveButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


    }

    private void DatePicker() {

        if (!Util_Common.isSelectedDateList.isEmpty()) {
            for (int i = 0; i < Util_Common.isSelectedDateList.size(); i++) {
                String str = String.valueOf(Util_Common.isSelectedDateList.get(i));
                String[] arrOfStr = str.split("/");
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
        maxDate.add(Calendar.DAY_OF_MONTH, 60);
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

        Util_Common.isSelectedDateList.clear();
        isDifferentDates.clear();
        isSelectedDate.clear();
        dynamicDateSlotsList.clear();
        Stream.of(calendars).forEach(calendar -> {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String isSelectedDate = format.format(calendar.getTime());
            Util_Common.isSelectedDateList.add(isSelectedDate);
        });

        Set<String> isSelectedDate = new LinkedHashSet<String>(Util_Common.isSelectedDateList);
        System.out.println(isSelectedDate);
        Util_Common.isSelectedDateList.clear();
        Util_Common.isSelectedDateList.addAll(isSelectedDate);
        Util_Common.isSelectedDate.addAll(Util_Common.isSelectedDateList);
        int isDateCount = 0;
        for (int i = 0; i <= Util_Common.isSelectedDate.size() - 1; i++) {
            if (Util_Common.isSelectedDate.get(i).equals(isCurrentDate)) {
                isTimeCompare(lblPickFromTime.getText().toString(), isCurrentTime);
                break;
            } else {
                isDateCount++;
                if (Util_Common.isSelectedDateList.size() == isDateCount) {
                    isTimeSeparate();
                }
            }
        }
    }

    private void dateShowing(List<DatePicking> dynamicDateSlotsList) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.date_availablity_check, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rcyDate);
        DateShowingAdapter adapter = new DateShowingAdapter(this, dynamicDateSlotsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Button dialogButton = dialogView.findViewById(R.id.btnCheck);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isCheckSlotAvailable();
            }
        });
        dialog.show();
    }


    private void isAvailableTime(List<SlotsAvailableTimeData> isSlotsData) {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.available_time_check, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        RecyclerView recyclerView = dialogView.findViewById(R.id.rcyDate);

        if (isSlotsData != null && !isSlotsData.isEmpty()) {
            DateAvailableAdapter adapter = new DateAvailableAdapter(isSlotsData, NewSlotCreating.this);
            recyclerView.setLayoutManager(new LinearLayoutManager(NewSlotCreating.this));
            recyclerView.setAdapter(adapter);
        }
        Button dialogButton = dialogView.findViewById(R.id.btnCheck);
        dialogButton.setText(getResources().getString(R.string.btn_submit));
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                removeSlotsAndProcess();
            }
        });
        dialog.show();

    }

    public void removeSlotsAndProcess() {
        List<slotsTime> slotsToRemove = new ArrayList<>();
        for (int i = 0; i < isSlotsAvailableTimeData.size(); i++) {
            List<slotsTime> slotsList = isSlotsAvailableTimeData.get(i).getData();
            for (slotsTime slot : slotsList) {
                if (slot.getSlot_Availablity().equals("Not Available")) {
                    Log.d("Incoming", "Removing slot: " + slot.getFrom_time() + " to " + slot.getTo_time());
                    slotsToRemove.add(slot);
                }
            }
        }

        for (int i = 0; i < isSlotsAvailableTimeData.size(); i++) {
            List<slotsTime> slotsList = isSlotsAvailableTimeData.get(i).getData();
            slotsList.removeAll(slotsToRemove);
        }
        if (isSlotsAvailableTimeData.get(0).getData().size() > 0) {
            isSubmit();
        } else {
            Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.Your_slot_time_is_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    private void isSubmit() {

        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.alert))
                .setMessage(getResources().getString(R.string.Are_you_sure_want_to_submit))
                .setPositiveButton(getResources().getString(R.string.permission_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isSlotSubmit(isSlotsAvailableTimeData);
                        dialog.dismiss();
                    }
                })

                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();


    }


    private void isSlotPicking() {

        List<String> spinnerData = Arrays.asList("1", "2", "3", "4");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ic_down, spinnerData);
        adapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        spinnerSlots.setAdapter(adapter);
        spinnerSlots.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSlotsCount = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void isBreakPicking() {
        List<String> spinnerData = Arrays.asList("5 Mins", "10 Mins", "15 Mins");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ic_down, spinnerData);
        adapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        spinnerBreakBetween.setAdapter(adapter);
        spinnerBreakBetween.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isBreakDuration = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void isDurationPicking() {
        List<String> spinnerData = Arrays.asList("Select duration   ", "10", "15", "20", "30", "Custom");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ic_down, spinnerData);
        adapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        spinnerDuration.setAdapter(adapter);
        spinnerDuration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isDuration = (String) parent.getItemAtPosition(position);
                if (isDuration.equals("Custom")) {
                    edt_slotDuration.setVisibility(View.VISIBLE);
                } else {
                    edt_slotDuration.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void isSelectModePicking() {
        List<String> spinnerData = Arrays.asList("In Person", "Online", "Phone Call");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.ic_down, spinnerData);
        adapter.setDropDownViewResource(R.layout.teacher_spin_dropdown);

        spinnerSelectionMode.setAdapter(adapter);
        spinnerSelectionMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelectMode = (String) parent.getItemAtPosition(position);
//                if (isSelectMode.equals("Select Mode")) {
//                    EdtOnlineLink.setVisibility(View.GONE);
//                    rlaLink.setVisibility(View.GONE);
//                } else
                if (isSelectMode.equals("Online")) {
                    EdtOnlineLink.setVisibility(View.VISIBLE);
                    rlaLink.setVisibility(View.VISIBLE);
                } else {
                    EdtOnlineLink.setVisibility(View.GONE);
                    rlaLink.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void getSection() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(this);
        Log.d("isNewVersion", String.valueOf(isNewVersion));
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<GetSection> call = apiService.getSubjectDate(StaffID, SchoolID);

        call.enqueue(new Callback<GetSection>() {
            @Override
            public void onResponse(Call<GetSection> call, retrofit2.Response<GetSection> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response);
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Log.d("jsonString", (jsonString));
                    int status = jsonObject.getInt("Status");
                    String message = jsonObject.getString("Message");

                    if (status == 1) {
                        mProgressDialog.dismiss();
                        GetSection isGetSection = response.body();
                        if (isGetSection.getData().size() > 0) {
                            List<GetSectionData> isGetSectionData = isGetSection.getData();

                            isSectionListAdapter = new SectionListAdapter(isGetSectionData, NewSlotCreating.this);
                            gridViewSection.setAdapter(isSectionListAdapter);
                            isSectionListAdapter.notifyDataSetChanged();
                            adjustGridViewHeight(gridViewSection, isSectionListAdapter.getCount());

//                            GridLayoutManager gridLayoutManager = new GridLayoutManager(NewSlotCreating.this, 3);
//                            isSectionListAdapter = new SectionListAdapter(isGetSectionData, NewSlotCreating.this);
//                            gridViewSection.setLayoutManager(gridLayoutManager);
//                            gridViewSection.setAdapter(isSectionListAdapter);
//                            gridViewSection.setNestedScrollingEnabled(true);
//                            isSectionListAdapter.notifyDataSetChanged();
                        } else {
                            isCheckSectionAssigning(message);
                        }
                    } else {
                        isCheckSectionAssigning(message);
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<GetSection> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(NewSlotCreating.this, getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isCheckSectionAssigning(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.info))
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.permission_ok), (dialog, which) -> {
                    onBackPressed();
                })
                .setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void isCheckSlotAvailable() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonArray jsonarray = new JsonArray();

        try {
            for (int i = 0; i < dynamicDateSlotsList.size(); i++) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("institute_id", SchoolID);
                jsonObject.addProperty("staff_id", StaffID);

                String[] parts = isBreakDuration.split(" ");
                isBreakDuration = "";
                isBreakDuration = parts[0];

                jsonObject.addProperty("break_time", isBreakDuration);
                if (EdtOnlineLink.getText().toString().equals("")) {
                    jsonObject.addProperty("event_link", "");
                } else {
                    jsonObject.addProperty("event_link", EdtOnlineLink.getText().toString());
                }
                jsonObject.addProperty("date", dynamicDateSlotsList.get(i).getDate());
                jsonObject.addProperty("duration", isDuration);
                jsonObject.addProperty("event_name", edtDiscussion.getText().toString());
                jsonObject.addProperty("from_time", lblPickFromTime.getText().toString());
                jsonObject.addProperty("to_time", lblPickToTime.getText().toString());
                jsonObject.addProperty("meeting_mode", isSelectMode);

                JsonArray jsonArraySlots = new JsonArray();
                for (int j = 0; j < dynamicDateSlotsList.get(i).getSlots().size(); j++) {
                    JsonObject jsonObjectSlots = new JsonObject();
                    jsonObjectSlots.addProperty("from_time", dynamicDateSlotsList.get(i).getSlots().get(j).getFromTime());
                    jsonObjectSlots.addProperty("to_time", dynamicDateSlotsList.get(i).getSlots().get(j).getToTime());
                    jsonArraySlots.add(jsonObjectSlots);
                }
                jsonObject.add("slots", jsonArraySlots);

                ArrayList<SectionAndClass> selectedIds = isSectionListAdapter.getSelectedIds();
                JsonArray jsonArraySection = new JsonArray();
                for (int k = 0; k < selectedIds.size(); k++) {
                    JsonObject jsonObjectSection = new JsonObject();
                    jsonObjectSection.addProperty("section_id", selectedIds.get(k).getSection_id());
                    jsonObjectSection.addProperty("class_id", selectedIds.get(k).getClass_id());
                    jsonArraySection.add(jsonObjectSection);
                }
                jsonObject.add("std_sec_details", jsonArraySection);
                jsonarray.add(jsonObject);
            }
        } catch (Exception e) {
            Log.d("JsonException", String.valueOf(e));
        }
        Log.d("jsonObjectSchool", jsonarray.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isPtmCheckSlotAvailable(jsonarray);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Log.d("Converted JSON String", jsonString);

                    if (jsonObject.has("Status")) {
                        int status = jsonObject.getInt("Status");
                        String message = jsonObject.getString("Message");
                        if (status == 1) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            mProgressDialog.dismiss();
                            if (jsonArray.length() > 0) {
                                isSlotsAvailableTimeData.clear();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObjectGroups = jsonArray.getJSONObject(j);

                                    String instituteId = jsonObjectGroups.getString("institute_id");
                                    String staffId = jsonObjectGroups.getString("staff_id");
                                    String breakTime = jsonObjectGroups.getString("break_time");
                                    String date = jsonObjectGroups.getString("date");
                                    String duration = jsonObjectGroups.getString("duration");
                                    String eventName = jsonObjectGroups.getString("event_name");
                                    String meetingMode = jsonObjectGroups.getString("meeting_mode");
                                    String fromTime = jsonObjectGroups.getString("from_time");
                                    String toTime = jsonObjectGroups.getString("to_time");

                                    // Handle nested slots array
                                    JSONArray slotsArray = jsonObjectGroups.getJSONArray("slots");
                                    List<slotsTime> slotsList = new ArrayList<>();
                                    for (int k = 0; k < slotsArray.length(); k++) {
                                        JSONObject slotObject = slotsArray.getJSONObject(k);
                                        String slotFrom = slotObject.getString("from_time");
                                        String slotTo = slotObject.getString("to_time");
                                        String type = slotObject.getString("type");
                                        String slotAvailability = slotObject.getString("slot_Availablity");

                                        slotsTime slotData = new slotsTime(slotFrom, slotTo, type, slotAvailability);
                                        slotsList.add(slotData);
                                    }

                                    // Handle nested standard section details
                                    JSONArray stdSecArray = jsonObjectGroups.getJSONArray("std_sec_details");
                                    List<std_sec_details> stdSecList = new ArrayList<>();
                                    for (int m = 0; m < stdSecArray.length(); m++) {
                                        JSONObject stdSecObject = stdSecArray.getJSONObject(m);
                                        String classId = stdSecObject.getString("class_id");
                                        String sectionId = stdSecObject.getString("section_id");

                                        std_sec_details stdSecDetails = new std_sec_details(classId, sectionId);
                                        stdSecList.add(stdSecDetails);
                                    }

                                    SlotsAvailableTimeData isSlotsHistoryData = new SlotsAvailableTimeData(instituteId, staffId, breakTime, date, duration, eventName, meetingMode, fromTime, toTime, slotsList, stdSecList);
                                    isSlotsAvailableTimeData.add(isSlotsHistoryData);
                                    isAvailableTime(isSlotsAvailableTimeData);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(NewSlotCreating.this, "Check your internet connectivity", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void isSlotSubmit(List<SlotsAvailableTimeData> isAvailableTimeData) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonArray jsonarray = new JsonArray();

        try {
            for (int i = 0; i < isAvailableTimeData.size(); i++) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("institute_id", SchoolID);
                jsonObject.addProperty("staff_id", StaffID);

                String[] parts = isBreakDuration.split(" ");
                isBreakDuration = "";
                isBreakDuration = parts[0];

                jsonObject.addProperty("break_time", isBreakDuration);
                jsonObject.addProperty("date", isAvailableTimeData.get(i).getDate());
                jsonObject.addProperty("duration", isDuration);
                if (EdtOnlineLink.getText().toString().equals("")) {
                    jsonObject.addProperty("event_link", "");
                } else {
                    jsonObject.addProperty("event_link", EdtOnlineLink.getText().toString());
                }
                jsonObject.addProperty("event_name", edtDiscussion.getText().toString());
                jsonObject.addProperty("from_time", lblPickFromTime.getText().toString());
                jsonObject.addProperty("to_time", lblPickToTime.getText().toString());
                jsonObject.addProperty("meeting_mode", isSelectMode);

                JsonArray jsonArraySlots = new JsonArray();
                for (int j = 0; j < isAvailableTimeData.get(i).getData().size(); j++) {
                    JsonObject jsonObjectSlots = new JsonObject();
                    jsonObjectSlots.addProperty("from_time", isAvailableTimeData.get(i).getData().get(j).getFrom_time());
                    jsonObjectSlots.addProperty("to_time", isAvailableTimeData.get(i).getData().get(j).getTo_time());
                    jsonArraySlots.add(jsonObjectSlots);
                }
                jsonObject.add("slots", jsonArraySlots);
                JsonArray jsonArraySection = new JsonArray();
                for (int k = 0; k < isAvailableTimeData.get(i).getStandardData().size(); k++) {
                    JsonObject jsonObjectSection = new JsonObject();
                    jsonObjectSection.addProperty("section_id", isAvailableTimeData.get(i).getStandardData().get(k).getSection_id());
                    jsonObjectSection.addProperty("class_id", isAvailableTimeData.get(i).getStandardData().get(k).getClass_id());
                    jsonArraySection.add(jsonObjectSection);
                }
                jsonObject.add("std_sec_details", jsonArraySection);
                jsonarray.add(jsonObject);
            }
        } catch (Exception e) {
            Log.d("JsonException", String.valueOf(e));
        }
        Log.d("jsonObjectSchool", jsonarray.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isCreatePtm(jsonarray);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Log.d("Converted JSON String++++++", jsonString);

                    if (jsonObject.has("Status")) {
                        int status = jsonObject.getInt("Status");
                        String message = jsonObject.getString("Message");
                        if (status == 1) {
                            mProgressDialog.dismiss();
                            isSuccessPopup(message);
                        }
                    }
                } catch (Exception e) {
                    Log.e("Response Exception=====", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure___", t.getMessage());
                Toast.makeText(NewSlotCreating.this, "Check your internet connectivity", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isSuccessPopup(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isSlotsAvailableTimeData.clear();
                        onBackPressed();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void adjustGridViewHeight(GridView gridView, int itemCount) {
        ListAdapter gridAdapter = gridView.getAdapter();
        if (gridAdapter == null) return;

        int totalHeight = 0;
        int itemsPerRow = 2;
        int numRows = (int) Math.ceil((double) itemCount / itemsPerRow);

        for (int i = 0; i < numRows; i++) {
            View listItem = gridAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + (gridView.getVerticalSpacing() * (numRows - 1));
        gridView.setLayoutParams(params);
    }

    private void isTimeSeparate() {

        String fromTime = lblPickFromTime.getText().toString();
        String toTime = lblPickToTime.getText().toString();

        for (String date : Util_Common.isSelectedDateList) {
            int duration = safeParseInt(isDuration, 10);
            String[] parts = isBreakDuration.split(" ");
            isBreakDuration = "";
            isBreakDuration = parts[0];

            int breakDurationVal = safeParseInt(isBreakDuration, 5);
            int slotsCount = safeParseInt(isSlotsCount, 1);

            slots = TimeSlotGenerator.generateTimeSlots(fromTime, toTime, duration, breakDurationVal, slotsCount);
            DatePicking dateSlots = new DatePicking(date, slots);
            dynamicDateSlotsList.add(dateSlots);
        }

        dateShowing(dynamicDateSlotsList);

        for (DatePicking dateSlots : dynamicDateSlotsList) {
            Log.d("DateSlots", dateSlots.toString());
        }
    }

    private int safeParseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    @Override
    public void onBackPressed() {
        Util_Common.isSelectedDateList.clear();
        super.onBackPressed();
    }
}
