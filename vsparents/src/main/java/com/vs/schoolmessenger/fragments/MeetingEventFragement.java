package com.vs.schoolmessenger.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.PTM;
import com.vs.schoolmessenger.adapter.Ptm.CalendarAdapter;
import com.vs.schoolmessenger.adapter.Ptm.ParentSlotBookingAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.AvailableSlotDate;
import com.vs.schoolmessenger.model.DateEventModel;
import com.vs.schoolmessenger.model.GroupedSlot;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.PtmSubject;
import com.vs.schoolmessenger.model.PtmSubjectdata;
import com.vs.schoolmessenger.model.SlotDetail;
import com.vs.schoolmessenger.model.Slots;
import com.vs.schoolmessenger.model.SlotsData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;


public class MeetingEventFragement extends Fragment implements CalendarAdapter.OnItemClickListener, ParentSlotBookingAdapter.OnChildItemClickListener {
    private RecyclerView calendarRecyclerView, recycleviewSlotsList;
    private CalendarAdapter dateAdapter;
    private List<DateEventModel> dateList;
    public static Profiles childItem = new Profiles();
    Spinner spinnerSubjects;
    String isEventDate;
    String isSubjectID = "0";
    TextView BtnSlotBook, lblNoRecords;
    String isClassTeacherID = "0";
    List<SlotsData> slotsData;
    ParentSlotBookingAdapter parentSlotBookingAdapter;
    int isSpecificMeeting = 1;

    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_parent_meeting, container, false);

        childItem = TeacherUtil_SharedPreference.getChildItems(requireActivity(), "childItem");

        calendarRecyclerView = view.findViewById(R.id.calendarRecyclerView);
        recycleviewSlotsList = view.findViewById(R.id.recycleviewSlotsList);
        BtnSlotBook = view.findViewById(R.id.BtnSlotBook);
        calendarRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        spinnerSubjects = view.findViewById(R.id.spinner_subjects);
        lblNoRecords = view.findViewById(R.id.lblNoRecords);

        dateList = new ArrayList<>();
        dateAdapter = new CalendarAdapter(dateList, requireActivity());
        dateAdapter.setOnItemClickListener(this);
        calendarRecyclerView.setAdapter(dateAdapter);

        BtnSlotBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util_Common.isSelectedSlotIds.size() > 0) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                    builder.setTitle(requireActivity().getString(R.string.info));
                    builder.setMessage(requireActivity().getString(R.string.Are_you_sure_booking));

                    builder.setPositiveButton(requireActivity().getString(R.string.ok), (dialog, which) -> {
                        Log.d("isSelectedSlotIdsSize", String.valueOf(Util_Common.isSelectedSlotIds.size()));
                        for (int i = 0; i <= Util_Common.isSelectedSlotIds.size() - 1; i++) {
                            Log.d("SelectedIds", String.valueOf(Util_Common.isSelectedSlotIds.get(i)));
                        }
                        isBookingSlots();
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        isEventDate = getCurrentDate();
        getSlotDate();
        getSubject();
        return view;
    }

    public String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date currentDate = new Date();
        return sdf.format(currentDate);
    }

    private void populateCalendarWithEvents(List<DateEventModel> apiData) {
        LocalDate startDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startDate = LocalDate.now();
        }
        DateTimeFormatter formatter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        }

        for (int i = 0; i < 60; i++) {
            LocalDate date = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                date = startDate.plusDays(i);
            }
            String formattedDate = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                formattedDate = date.format(formatter);
            }

            int count = 0;
            for (DateEventModel event : apiData) {
                if (event.getEventDate().equals(formattedDate)) {
                    count = event.getCount();
                    break;
                }
            }
            dateList.add(new DateEventModel(formattedDate, count));
        }
        dateAdapter.notifyDataSetChanged();
    }

    private void getSlotDate() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireContext());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(requireActivity().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<AvailableSlotDate> call = apiService.getthedate(childItem.getChildID(), childItem.getSchoolID());

        call.enqueue(new Callback<AvailableSlotDate>() {
            @Override
            public void onResponse(Call<AvailableSlotDate> call, retrofit2.Response<AvailableSlotDate> response) {
                try {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Lessons_Response", json);

                        int status = response.body().getStatus();
                        String message = response.body().getMessage();

                        List<DateEventModel> isEventModelData = new ArrayList<>();
                        JsonArray dataArray = gson.fromJson(gson.toJson(response.body().getData()), JsonArray.class);
                        if (dataArray.size() > 0) {
                            for (JsonElement element : dataArray) {
                                JsonObject jsonObject = element.getAsJsonObject();
                                String eventDate = jsonObject.has("eventDate") ? jsonObject.get("eventDate").getAsString() : "01/01/1970";
                                int count = jsonObject.get("count").getAsInt();
                                isEventModelData.add(new DateEventModel(eventDate, count));
                            }
                            populateCalendarWithEvents(isEventModelData);
                        } else {
                            populateCalendarWithEvents(isEventModelData);
                        }
                    } else {
                        Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(Call<AvailableSlotDate> call, Throwable t) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Log.e("Response Failure", t.getMessage());
            }
        });
    }

    private void getSubject() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireContext());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(requireActivity().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<PtmSubjectdata> call = apiService.getStudentSideSubject(childItem.getChildID(), childItem.getSchoolID());

        call.enqueue(new Callback<PtmSubjectdata>() {
            @Override
            public void onResponse(Call<PtmSubjectdata> call, retrofit2.Response<PtmSubjectdata> response) {
                try {
                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                    if (response.code() == 200 || response.code() == 201) {
                        try {
                            Log.d("daily:code-res___", response.code() + " - " + response.toString());

                            Gson gson = new Gson();
                            String json = gson.toJson(response.body());
                            Log.d("Lessons Response", json);
                            if (response.isSuccessful() && response.body() != null) {
                                PtmSubjectdata subjectResponse = response.body();

                                if (subjectResponse.getData() != null) {
                                    List<String> subjectNames = new ArrayList<>();
                                    for (PtmSubject subject : subjectResponse.getData()) {
                                        subjectNames.add(subject.getSubjectName());
                                    }
                                    isUpdateTheSpinner(subjectNames, subjectResponse);
                                }
                            }
                        } catch (Exception e) {
                            Log.e("JSON Parsing Error", "Error parsing JSON", e);
                        }

                    } else {
                        Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PtmSubjectdata> call, Throwable t) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isUpdateTheSpinner(List<String> subjectNames, PtmSubjectdata subjectResponse) {
        subjectNames.add(0, "All Subjects");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.spinner_drop_down, subjectNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjects.setAdapter(adapter);

        spinnerSubjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedSubjectName = parentView.getItemAtPosition(position).toString();
                Log.d("Selected Subject", "Name: " + selectedSubjectName);
                Util_Common.isDataLoadingOver = true;
                int selectedSubjectId = 0;
                if (position == 0) {
                    isSubjectID = String.valueOf(0);
                    isClassTeacherID = String.valueOf(0);
                } else if (selectedSubjectName.equals("Class Teacher")) {
                    isSubjectID = String.valueOf(0);
                    isClassTeacherID = String.valueOf(subjectResponse.getData().get(position - 1).getClassTeacherId());
                } else {
                    selectedSubjectId = subjectResponse.getData().get(position - 1).getSubjectId();
                    isSubjectID = String.valueOf(selectedSubjectId);
                    isClassTeacherID = String.valueOf(0);
                }
                Log.d("Selected Subject", "ID: " + selectedSubjectId);
                isSpecificMeeting = 1;
                Util_Common.overlappingSlots.clear();
                Util_Common.isSelectedSlotIds.clear();
                Util_Common.hasMyBooking = false;
                Util_Common.isHeaderSlotsIds.clear();
                Util_Common.isSelectedTime.clear();
                getSlotData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if no item is selected
            }
        });
    }


    private void getSlotData() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireContext());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(requireActivity().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<Slots> call = apiService.getSlotData(childItem.getChildID(), childItem.getSchoolID(), isEventDate, isSubjectID, isClassTeacherID);

        call.enqueue(new Callback<Slots>() {
            @Override
            public void onResponse(Call<Slots> call, retrofit2.Response<Slots> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response.toString());

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        int status = response.body().getStatus();
                        String message = response.body().getMessage();
                        Slots slotDetails = response.body();
                        if (status == 1) {
                            slotsData = slotDetails.getData();
                            if (slotsData.size() > 0) {
                                lblNoRecords.setVisibility(View.GONE);
                                recycleviewSlotsList.setVisibility(View.VISIBLE);

                                Map<String, GroupedSlot> groupedSlotsMap = new HashMap<>();
                                for (SlotsData slot : slotsData) {
                                    String key = slot.staff_id + "|" + slot.staff_name + "|" + slot.event_name + "|" + slot.subject_name;
                                    // If the grouped slot does not exist, create a new one
                                    if (!groupedSlotsMap.containsKey(key)) {
                                        groupedSlotsMap.put(key, new GroupedSlot(slot.is_booked, slot.staff_id, slot.staff_name, slot.subject_name, slot.event_name, slot.event_mode, slot.event_link, slot.my_booking, isSpecificMeeting));
                                        isSpecificMeeting++;
                                    }
                                    GroupedSlot groupedSlot = groupedSlotsMap.get(key);
                                    groupedSlot.addSlotDetail(new SlotDetail(slot.is_booked, slot.from_time, slot.to_time, slot.slot_id, slot.staff_id, isSpecificMeeting, slot.my_booking));
                                    List<GroupedSlot> groupedSlots = new ArrayList<>(groupedSlotsMap.values());
                                    loadAdapter(groupedSlots);
                                }
                                //  Print grouped slots
                                for (GroupedSlot groupedSlot : groupedSlotsMap.values()) {
                                    System.out.println(groupedSlot);
                                    for (SlotDetail slotDetail : groupedSlot.getSlots()) {
                                        System.out.println("SlotDetail: " + slotDetail);
                                    }
                                }
                            } else {
                                recycleviewSlotsList.setVisibility(View.GONE);
                            }
                        } else {
                            recycleviewSlotsList.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                        }
                    } else {
                        recycleviewSlotsList.setVisibility(View.GONE);
                        lblNoRecords.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Slots> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                BtnSlotBook.setVisibility(View.GONE);
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void loadAdapter(List<GroupedSlot> groupedSlot) {
        parentSlotBookingAdapter = new ParentSlotBookingAdapter(requireActivity(), groupedSlot, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        recycleviewSlotsList.setHasFixedSize(true);
        recycleviewSlotsList.setNestedScrollingEnabled(false);
        recycleviewSlotsList.getRecycledViewPool().setMaxRecycledViews(0, 20);
        recycleviewSlotsList.setLayoutManager(layoutManager);
        recycleviewSlotsList.setItemAnimator(new DefaultItemAnimator());
        recycleviewSlotsList.setAdapter(parentSlotBookingAdapter);
    }

    @Override
    public void onChildItemClick(boolean isAnySlotSelected) {
        if (isAnySlotSelected) {
            BtnSlotBook.setEnabled(true);
            BtnSlotBook.setBackgroundResource(R.drawable.bg_button_book_slots);
        } else {
            BtnSlotBook.setEnabled(false);
            BtnSlotBook.setBackgroundResource(R.drawable.bg_gray_button);
        }
    }

    @Override
    public void onItemClick(DateEventModel dateEvent) {
        BtnSlotBook.setEnabled(false);
        BtnSlotBook.setBackgroundResource(R.drawable.bg_gray_button);
        isEventDate = dateEvent.getEventDate();
        Util_Common.isDataLoadingOver = true;
        Util_Common.overlappingSlots.clear();
        Util_Common.isSelectedSlotIds.clear();
        Util_Common.hasMyBooking = false;
        Util_Common.isHeaderSlotsIds.clear();
        Util_Common.isSelectedTime.clear();
        Util_Common.isBookedIds.clear();
        isSpecificMeeting = 1;
        getSlotData();
        Toast.makeText(requireActivity(), "Selected date: " + dateEvent.getEventDate(), Toast.LENGTH_SHORT).show();
    }


    private void isBookingSlots() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(requireActivity().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonObject.addProperty("institute_id", childItem.getSchoolID());
        jsonObject.addProperty("student_id", childItem.getChildID());
        for (int a = 0; a <= Util_Common.isSelectedSlotIds.size() - 1; a++) {
            jsonArray.add(Util_Common.isSelectedSlotIds.get(a));
        }
        jsonObject.add("slot_id", jsonArray);

        Log.d("SlotBooking", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isBookingSlotForStudent(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String isMessage = jsonObject.getString("Message");
                        int isStatus = jsonObject.getInt("Status");
                        if (isStatus == 1) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                            builder.setTitle(requireActivity().getString(R.string.Success));
                            builder.setMessage(isMessage);

                            builder.setPositiveButton(requireActivity().getString(R.string.ok), (dialog, which) -> {
                                isUpComingFragment();
                                dialog.dismiss();
                            });

                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    } else {
                        Toast.makeText(requireActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(requireActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void isUpComingFragment() {
        Fragment newFragment = new UpComingEventFragement();
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.FL, newFragment);
        fragmentTransaction.addToBackStack(null); // Optional if you want to add to back stack
        fragmentTransaction.commit();

        Util_Common.overlappingSlots.clear();
        Util_Common.isSelectedSlotIds.clear();
        Util_Common.hasMyBooking = false;
        Util_Common.isHeaderSlotsIds.clear();
        Util_Common.isSelectedTime.clear();
        Util_Common.isBookedIds.clear();
        isSpecificMeeting = 1;

        if (getActivity() instanceof PTM) {
            ((PTM) getActivity()).updateFragmentLabel();
        }
    }
}