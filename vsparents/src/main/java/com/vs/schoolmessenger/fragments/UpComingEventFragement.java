package com.vs.schoolmessenger.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.pluscubed.recyclerfastscroll.RecyclerFastScroller;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.Ptm.SlotHistoryParentAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.SlotsHistory;
import com.vs.schoolmessenger.model.SlotsHistoryData;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class UpComingEventFragement extends Fragment {

    RecyclerView rcySlotHistory;
    TextView lblNodata;
    public static Profiles childItem = new Profiles();
    ArrayList<SlotsHistoryData> isAvailableDateData = new ArrayList<SlotsHistoryData>();
    SlotHistoryParentAdapter isSlotHistoryParentAdapter;
    RecyclerFastScroller fastScroller;


    @SuppressLint("LongLogTag")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_upcoming_event, container, false);
        rcySlotHistory = view.findViewById(R.id.rcySlotHistory);
        lblNodata = view.findViewById(R.id.lblNodata);
        childItem = TeacherUtil_SharedPreference.getChildItems(requireActivity(), "childItem");

        isSlotHistoryParentAdapter = new SlotHistoryParentAdapter(requireActivity(), isAvailableDateData);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
        rcySlotHistory.setHasFixedSize(true);
        rcySlotHistory.setLayoutManager(layoutManager);
        rcySlotHistory.setItemAnimator(new DefaultItemAnimator());
        rcySlotHistory.setNestedScrollingEnabled(false);
        rcySlotHistory.getRecycledViewPool().setMaxRecycledViews(0, 20);
        rcySlotHistory.setAdapter(isSlotHistoryParentAdapter);
        fastScroller = view.findViewById(R.id.fasttrcv);
        fastScroller.attachRecyclerView(rcySlotHistory);
        getSlotsHistory();
        return view;
    }


    private void getSlotsHistory() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(requireActivity());
        Log.d("isNewVersion", String.valueOf(isNewVersion));
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(requireActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(requireContext());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<SlotsHistory> call = apiService.isParentSideSlotsHistory(childItem.getChildID(), childItem.getSchoolID(), childItem.getIsClassId(), childItem.getIsSectionId());

        call.enqueue(new Callback<SlotsHistory>() {
            @Override
            public void onResponse(Call<SlotsHistory> call, retrofit2.Response<SlotsHistory> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response);

                    Gson gson = new Gson();
                    String jsonString = gson.toJson(response.body());
                    JSONObject jsonObject = new JSONObject(jsonString);
                    Log.d("jsonString", String.valueOf(jsonString));
                    int status = jsonObject.getInt("Status");
                    String message = jsonObject.getString("Message");

                    if (status == 1) {
                        lblNodata.setVisibility(View.GONE);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray.length() > 0) {
                            isAvailableDateData.clear();
                            for (int j = 0; j < jsonArray.length(); j++) {
                                JSONObject jsonObjectGroups = jsonArray.getJSONObject(j);
                                String isSlotId = jsonObjectGroups.getString("slot_id");
                                String slotDate = jsonObjectGroups.getString("slot_date");
                                String slotTime = jsonObjectGroups.getString("slot_time");
                                String isStatus = jsonObjectGroups.getString("status");
                                String purpose = jsonObjectGroups.getString("purpose");
                                String mode = jsonObjectGroups.getString("mode");
                                String eventLink = jsonObjectGroups.getString("event_link");
                                String staffId = jsonObjectGroups.getString("staff_id");
                                String staffName = jsonObjectGroups.getString("staff_name");
                                String subject_name="";
                                if (jsonObjectGroups.has("staff_name")) {
                                     subject_name = jsonObjectGroups.getString("subject_name");
                                    Log.d("Staff Name", staffName);
                                } else {
                                    Log.d("Staff Name", "staff_name node is not present");
                                }

                                SlotsHistoryData isSlotsHistoryData = new SlotsHistoryData(isSlotId, slotDate, slotTime, isStatus, purpose, mode, eventLink, staffId, staffName,subject_name);
                                isAvailableDateData.add(isSlotsHistoryData);
                            }

                            isSlotHistoryParentAdapter = new SlotHistoryParentAdapter(requireActivity(), isAvailableDateData);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false);
                            rcySlotHistory.setHasFixedSize(true);
                            rcySlotHistory.setLayoutManager(layoutManager);
                            rcySlotHistory.setItemAnimator(new DefaultItemAnimator());
                            rcySlotHistory.setAdapter(isSlotHistoryParentAdapter);
                        }
                    } else {
                        lblNodata.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    lblNodata.setVisibility(View.GONE);
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<SlotsHistory> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                lblNodata.setVisibility(View.GONE);
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(requireContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }
}
