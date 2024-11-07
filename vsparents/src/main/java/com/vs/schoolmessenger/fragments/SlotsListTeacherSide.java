package com.vs.schoolmessenger.fragments;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
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
import com.vs.schoolmessenger.adapter.Ptm.DateAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DateModel;
import com.vs.schoolmessenger.model.SlotDetailsForStaff;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;

public class SlotsListTeacherSide extends Fragment implements CalendarDatePickerDialogFragment.OnDateSetListener {

    private DateAdapter dateAdapter;
    private List<DateModel> dateModelList = new ArrayList<>();
    TextView isDate, lblNoRecords;
    RecyclerView isRecyclerTodaySlots;
    RelativeLayout isNeumorphCardView;
    List<DateModel> dateModels = new ArrayList<>();
    String SchoolID, StaffID;
    RecyclerFastScroller fastScroller;
    private int selYear, selMonth, selDay;

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slotslist_teacher, container, false);

        isNeumorphCardView = view.findViewById(R.id.crdViewNeumorph);
        isDate = view.findViewById(R.id.lblDate);
        lblNoRecords = view.findViewById(R.id.lblNoRecord);
        isRecyclerTodaySlots = view.findViewById(R.id.rcyTodaySlots);
        fastScroller = view.findViewById(R.id.fasttrcv);
        Util_Common.isChooseDate = true;

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

        isNeumorphCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isDatePicking();
            }
        });
        isSlotsListTeacherSide("ALL");
        return view;
    }


    private void isDatePicking() {

        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Month is 0-based (January = 0)
        int day = calendar.get(Calendar.DAY_OF_MONTH);

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
        // Store the selected date
        selYear = year;
        selMonth = monthOfYear;
        selDay = dayOfMonth;

        String formattedDate = dateFormater(dialog.getSelectedDay().getDateInMillis(), "dd/MM/yyyy");
        isDate.setText(formattedDate);

        if (dateModels.size() > 0) {
            dateModels.clear();
        }
        Util_Common.isChooseDate = false;
        isDataLoading(dateModels);
        isSlotsListTeacherSide(formattedDate);
    }

    private void isSlotsListTeacherSide(String selectedDate) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<SlotDetailsForStaff> call = apiService.isSlotDetailsForStaff(StaffID, selectedDate, SchoolID);

        call.enqueue(new Callback<SlotDetailsForStaff>() {
            @Override
            public void onResponse(Call<SlotDetailsForStaff> call, retrofit2.Response<SlotDetailsForStaff> response) {

                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Log.d("daily:code-res___", response.code() + " - " + response);
                    if (response.body() != null) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("updates_Response", json);


                        int status = response.body().getStatus();
                        String message = response.body().getMessage();
                        SlotDetailsForStaff slotDetails = response.body();
                        if (status == 1) {
                            dateModels.clear();
                            dateModels = slotDetails.getData();
                            if (dateModels.size() > 0) {
                                isRecyclerTodaySlots.setVisibility(View.VISIBLE);
                                lblNoRecords.setVisibility(View.GONE);
                                isDataLoading(dateModels);
                            } else {
                                isRecyclerTodaySlots.setVisibility(View.GONE);
                                lblNoRecords.setVisibility(View.VISIBLE);
                            }
                        } else {
                            lblNoRecords.setVisibility(View.VISIBLE);
                            isRecyclerTodaySlots.setVisibility(View.GONE);
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
            public void onFailure(Call<SlotDetailsForStaff> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                lblNoRecords.setVisibility(View.VISIBLE);
                Log.e("Response_Failure", t.getMessage());
                Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void isDataLoading(List<DateModel> dateModels) {
        dateAdapter = new DateAdapter(requireActivity(), dateModels);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        isRecyclerTodaySlots.setHasFixedSize(true);
        isRecyclerTodaySlots.setLayoutManager(layoutManager);
        isRecyclerTodaySlots.setItemAnimator(new DefaultItemAnimator());
        isRecyclerTodaySlots.setAdapter(dateAdapter);
        fastScroller.attachRecyclerView(isRecyclerTodaySlots);

    }
}
