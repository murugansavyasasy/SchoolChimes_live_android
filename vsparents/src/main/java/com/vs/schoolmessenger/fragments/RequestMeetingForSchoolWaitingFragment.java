package com.vs.schoolmessenger.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.RequestMeetingWaitingAdapter;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.RequestMeetingForParentModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class RequestMeetingForSchoolWaitingFragment extends Fragment {
    RecyclerView recycle_paidlist;
    String school_id,Staff_ID;
    private ArrayList<RequestMeetingForParentModel> Meeting_list = new ArrayList<>();
    public RequestMeetingWaitingAdapter mAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.request_meeting_parent_recycle, container, false);
        recycle_paidlist = (RecyclerView) rootView.findViewById(R.id.request_meeting_recycler_view);

        Staff_ID= TeacherUtil_Common.Principal_staffId;
        school_id= TeacherUtil_Common.Principal_SchoolId;

        mAdapter = new RequestMeetingWaitingAdapter(Meeting_list, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycle_paidlist.setLayoutManager(mLayoutManager);
        recycle_paidlist.setItemAnimator(new DefaultItemAnimator());
        recycle_paidlist.setAdapter(mAdapter);
        recycle_paidlist.getRecycledViewPool().setMaxRecycledViews(0, 80);

        getDetails();

        return rootView;
    }

    private void getDetails() {
        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StaffID", Staff_ID);
        jsonObject.addProperty("SchoolID", school_id);
        jsonObject.addProperty("ApprovalStatus", "0");


        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.GetMeetingRequestsforStaff(jsonObject);

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


                        if (js.length() > 0) {
                            mAdapter.clearAllData();
                            RequestMeetingForParentModel datas;
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String ID=jsonObject.getString("RequestId");
                                String message=jsonObject.getString("Name");
                                if(!ID.equals("0")) {

                                    String id = jsonObject.getString("RequestId");
                                    String name = jsonObject.getString("Name");
                                    String parentcomment = jsonObject.getString("ParentComment");
                                    String status = jsonObject.getString("ApprovalStatus");
                                    String staffcomment = jsonObject.getString("StaffComment");
                                    String date = jsonObject.getString("ScheduleDate");
                                    String time = jsonObject.getString("ScheduleTime");
                                    String requestedON = jsonObject.getString("RequestedON");
                                    String approvedOn = jsonObject.getString("ApprovedOn");
                                    String cls = jsonObject.getString("Class");


                                    Log.d("Response1234", "");
                                    datas = new RequestMeetingForParentModel(id, name, parentcomment, status, staffcomment, date, time, requestedON, approvedOn, cls);
                                    Meeting_list.add(datas);

                                }
                                else {
                                    Toast.makeText(getActivity(),message, Toast.LENGTH_SHORT).show();
                                }

                            }
                            mAdapter.notifyDataSetChanged();

                        }
                        else {
                            Toast.makeText(getActivity(), R.string.no_records, Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

}
