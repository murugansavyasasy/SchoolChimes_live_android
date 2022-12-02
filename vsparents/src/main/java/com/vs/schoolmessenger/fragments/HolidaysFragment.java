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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.HolidaysAdapter;
import com.vs.schoolmessenger.assignment.StudentSelectAssignment;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.HolidayModel;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;

public class HolidaysFragment extends Fragment {
    RecyclerView recycle_paidlist;
    String school_id, child_id,MemberId;
    Context context;
    private ArrayList<HolidayModel> msgModelList = new ArrayList<>();
    private ArrayList<HolidayModel> OfflinemsgModelList = new ArrayList<>();
    private ArrayList<HolidayModel> totalmsgModelList = new ArrayList<>();

    public HolidaysAdapter mAdapter;
    SqliteDB myDb;
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    RelativeLayout rytSearch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.feespending_recycle, container, false);
        recycle_paidlist = (RecyclerView) rootView.findViewById(R.id.fees_pending_recycler_view);

        child_id = Util_SharedPreference.getChildIdFromSP(getActivity());
        school_id = Util_SharedPreference.getSchoolIdFromSP(getActivity());
        MemberId = Util_SharedPreference.getChildIdFromSP(getActivity());

        c = Calendar.getInstance();
         LoadMore=(TextView) rootView.findViewById(R.id.btnSeeMore);
        lblNoMessages=(TextView) rootView.findViewById(R.id.lblNoMessages);

        rytSearch=(RelativeLayout) rootView.findViewById(R.id.rytSearch);
        rytSearch.setVisibility(View.GONE);

        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoregetEventsDetails();

            }
        });
         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }

        mAdapter = new HolidaysAdapter(msgModelList, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycle_paidlist.setLayoutManager(mLayoutManager);
        recycle_paidlist.setItemAnimator(new DefaultItemAnimator());
        recycle_paidlist.setAdapter(mAdapter);
        recycle_paidlist.getRecycledViewPool().setMaxRecycledViews(0, 80);

        getEventsDetails();
        return rootView;
    }

    private void LoadMoregetEventsDetails() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("schoolid", school_id);
        jsonObject.addProperty("memberid", MemberId);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.LoadMoreViewHolidays(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        LoadMore.setVisibility(View.GONE);
                        lblNoMessages.setVisibility(View.GONE);

                        try {
                            JSONArray js = new JSONArray(response.body().toString());
                            if (js.length() > 0) {
                                HolidayModel msgModel;
                                 OfflinemsgModelList.clear();
                                for (int i = 0; i < js.length(); i++) {
                                    JSONObject  jsonObject = js.getJSONObject(i);
                                    String date=jsonObject.getString("HolidayDate");
                                    String msg=jsonObject.getString("Reason");

                                    if(!date.equals("0")) {
                                        msgModel = new HolidayModel(jsonObject.getString("HolidayDate"), jsonObject.getString("Reason"));
                                        msgModelList.add(msgModel);
                                        OfflinemsgModelList.add(msgModel);
                                    }
                                    else {
                                        showAlert(msg);
                                    }

                                }
                                myDb = new SqliteDB(getActivity());
                                if(myDb.checkHolidays()){
                                    myDb.deleteHolidays();
                                }
                                myDb.addHolidays( (ArrayList<HolidayModel>) OfflinemsgModelList, getActivity());

                                mAdapter.notifyDataSetChanged();
                            } else {

                                showAlert(getResources().getString(R.string.no_records));
                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getEventsDetails() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("schoolid", school_id);
        jsonObject.addProperty("memberid", MemberId);

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.ViewHolidays(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        try {
                            JSONArray js = new JSONArray(response.body().toString());
                            if (js.length() > 0) {
                                mAdapter.clearAllData();
                                totalmsgModelList.clear();
                                HolidayModel msgModel;

                                    for (int i = 0; i < js.length(); i++) {
                                       JSONObject  jsonObject = js.getJSONObject(i);
                                       String date=jsonObject.getString("HolidayDate");
                                       String msg=jsonObject.getString("Reason");

                                       if(!date.equals("0")) {
                                           msgModel = new HolidayModel(jsonObject.getString("HolidayDate"), jsonObject.getString("Reason"));
                                           msgModelList.add(msgModel);
                                           totalmsgModelList.add(msgModel);
                                       }
                                       else {
                                           if(isNewVersion.equals("1")){
                                               lblNoMessages.setVisibility(View.VISIBLE);
                                               lblNoMessages.setText(msg);
                                           }
                                           else {
                                               lblNoMessages.setVisibility(View.GONE);
                                               showAlert(msg);
                                           }

                                       }

                                        }
                                mAdapter.notifyDataSetChanged();
                            } else {

                                showAlert(getResources().getString(R.string.no_records));
                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle( R.string.alert);
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton( R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
                getActivity().finish();

            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }



}
