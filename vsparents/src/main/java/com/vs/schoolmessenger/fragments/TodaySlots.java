package com.vs.schoolmessenger.fragments;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.google.gson.Gson;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.NewSlotCreating;
import com.vs.schoolmessenger.adapter.Ptm.TodaySlotsStaffAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TodaySlotsData;
import com.vs.schoolmessenger.model.TodaySlotsStaff;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import soup.neumorphism.NeumorphCardView;


public class TodaySlots extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    TextView isDate, lblNoRecords,lblOpenNew;
    RecyclerView isRecyclerTodaySlots;
    NeumorphCardView isNeumorphCardView;
    ArrayList<TodaySlotsData> todaySlotsData = new ArrayList<TodaySlotsData>();
    TodaySlotsStaffAdapter isTodaySlotsStaffAdapter;
    RecyclerFastScroller fastScroller;
    String SchoolID, StaffID;
    ImageView imgAddSlot;
    LinearLayout lnrNewSlotCreate;
    private int selYear, selMonth, selDay;

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.today_slots, container, false);

        isNeumorphCardView = view.findViewById(R.id.crdViewNeumorph);
        isDate = view.findViewById(R.id.lblDate);
        lblNoRecords = view.findViewById(R.id.lblNoRecord);
        lblOpenNew = view.findViewById(R.id.lblOpenNew);
        isRecyclerTodaySlots = view.findViewById(R.id.rcyTodaySlots);
        fastScroller = view.findViewById(R.id.fasttrcv);
        lblOpenNew.setTypeface(null, Typeface.BOLD);


        imgAddSlot = view.findViewById(R.id.imgAddSlot);
        lnrNewSlotCreate = view.findViewById(R.id.lnrNewSlotCreate);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String currentDate = dateFormat.format(calendar.getTime());
        isDate.setText(currentDate);

        if ((TeacherUtil_SharedPreference.getLoginTypeFromSP(requireActivity()).equals(LOGIN_TYPE_PRINCIPAL)) && (listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else if (TeacherUtil_SharedPreference.getLoginTypeFromSP(requireActivity()).equals(LOGIN_TYPE_TEACHER)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        } else {
            Intent intent = getActivity().getIntent();
            SchoolID = intent.getStringExtra("SCHOOL_ID");
            TeacherUtil_Common.Principal_SchoolId = SchoolID;
            Intent intent1 = getActivity().getIntent();
            StaffID = intent1.getStringExtra("STAFF_ID");
        }

        lnrNewSlotCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent in = new Intent(requireActivity(), NewSlotCreating.class);
                in.putExtra("SCHOOL_ID", SchoolID);
                in.putExtra("STAFF_ID", StaffID);
                startActivity(in);

            }
        });

        isNeumorphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDatePicking();
            }
        });
        getTodaySlots(isDate.getText().toString());
        return view;
    }

    private void isDatePicking() {
        // Get today's date from the Calendar instance
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Month is 0-based (January = 0)
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the date picker dialog
        CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                .setThemeCustom(R.style.MyBetterPickersRadialTimePickerDialogStaffTheme)
                .setOnDateSetListener(this)
                .setFirstDayOfWeek(Calendar.SUNDAY) // Optional: Set the first day of the week
                .setPreselectedDate(year, month, day) // Use today's date
                .setDateRange(null, null) // Set the minimum date to today
                .setDoneText("OK")
                .setCancelText("Cancel");

        cdp.show(getChildFragmentManager(), "DATE_PICKER_TAG");
    }

    private String dateFormater(long dateInMillis, String formatString) {
        SimpleDateFormat formatter = new SimpleDateFormat(formatString, Locale.getDefault());
        return formatter.format(new Date(dateInMillis));
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        selYear = year;
        selMonth = monthOfYear;
        selDay = dayOfMonth;
        String formattedDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd/MM/yyyy");
        isDate.setText(formattedDate);

        todaySlotsData.clear();
        isLoadingData();
        getTodaySlots(formattedDate);
    }


    private void getTodaySlots(String selectedDate) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<TodaySlotsStaff> call = apiService.isTodaySlotsTeacherSide(StaffID, selectedDate, SchoolID);

        call.enqueue(new Callback<TodaySlotsStaff>() {
            @Override
            public void onResponse(Call<TodaySlotsStaff> call, retrofit2.Response<TodaySlotsStaff> response) {

                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Log.d("daily:code-res", response.code() + " - " + response);

                    if (response.body() != null) {
                        Log.d("Raw Response", response.body().toString());
                        Gson gson = new Gson();
                        String jsonString = gson.toJson(response.body());
                        JSONObject jsonObject = new JSONObject(jsonString);
                        Log.d("Converted JSON String", jsonString);

                        if (jsonObject.has("Status")) {
                            int status = jsonObject.getInt("Status");
                            String message = jsonObject.getString("Message");
                            if (status == 1) {
                                lblNoRecords.setVisibility(View.GONE);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    todaySlotsData.clear();
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jsonObjectGroups = jsonArray.getJSONObject(j);
                                        String eventDate = jsonObjectGroups.getString("eventDate");
                                        String slotId = jsonObjectGroups.getString("slotId");
                                        String slotFrom = jsonObjectGroups.getString("slotFrom");
                                        String slotTo = jsonObjectGroups.getString("slotTo");
                                        String eventName = jsonObjectGroups.getString("eventName");
                                        String eventMode = jsonObjectGroups.getString("eventMode");
                                        String eventLink = jsonObjectGroups.getString("eventLink");
                                        String studentId = jsonObjectGroups.getString("studentId");
                                        String studentName = jsonObjectGroups.getString("studentName");
                                        String classId = jsonObjectGroups.getString("classId");
                                        String sectionId = jsonObjectGroups.getString("sectionId");
                                        String className = jsonObjectGroups.getString("className");
                                        String sectionName = jsonObjectGroups.getString("sectionName");
                                        String slotStatus = jsonObjectGroups.getString("slotStatus");
                                        String isBooked = jsonObjectGroups.getString("isBooked");

                                        TodaySlotsData isSlotsHistoryData = new TodaySlotsData(eventDate, slotId, slotFrom, slotTo, eventName, eventMode, eventLink, studentId, studentName, classId, sectionId, className, sectionName, slotStatus, isBooked);
                                        todaySlotsData.add(isSlotsHistoryData);
                                    }
                                    isLoadingData();
                                }
                            }
                        } else {
                            lblNoRecords.setVisibility(View.VISIBLE);
                            isRecyclerTodaySlots.setVisibility(View.GONE);
                            Log.e("JSON Error", "No 'Status' field in the JSON response.");
                        }
                    } else {
                        lblNoRecords.setVisibility(View.VISIBLE);
                        isRecyclerTodaySlots.setVisibility(View.GONE);
                        Log.e("Response Error", "Response body is null or empty.");
                    }
                } catch (Exception e) {
                    lblNoRecords.setVisibility(View.VISIBLE);
                    isRecyclerTodaySlots.setVisibility(View.GONE);
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<TodaySlotsStaff> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                lblNoRecords.setVisibility(View.VISIBLE);
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void isLoadingData() {
        isTodaySlotsStaffAdapter = new TodaySlotsStaffAdapter(requireActivity(), todaySlotsData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        isRecyclerTodaySlots.setHasFixedSize(true);
        isRecyclerTodaySlots.setLayoutManager(layoutManager);
        isRecyclerTodaySlots.setItemAnimator(new DefaultItemAnimator());
        isRecyclerTodaySlots.setAdapter(isTodaySlotsStaffAdapter);
        fastScroller.attachRecyclerView(isRecyclerTodaySlots);
    }
}

