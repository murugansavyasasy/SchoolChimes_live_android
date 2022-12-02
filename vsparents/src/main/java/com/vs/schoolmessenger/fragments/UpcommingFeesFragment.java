package com.vs.schoolmessenger.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.MonthlyPendingFees;
import com.vs.schoolmessenger.activity.PaymentWebViewActivity;
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.adapter.PendingDetailsAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MonthlyPending;
import com.vs.schoolmessenger.model.PendingFeeDetails;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by voicesnap on 5/17/2018.
 */

public class UpcommingFeesFragment extends Fragment {
    RecyclerView recycle_upcomming_list;
    String child_id, school_id;
    Button btnMonthlyFees, btnMakePayment;
    PendingDetailsAdapter mAdapter;
    private ArrayList<PendingFeeDetails> pending_list = new ArrayList<>();
    private ArrayList<MonthlyPending> monthly_list = new ArrayList<>();
    public static String pending_size = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fees_upcomming_recycle, container, false);
        recycle_upcomming_list = (RecyclerView) rootView.findViewById(R.id.fees_upcomming_recycler_view);

        btnMonthlyFees = (Button) rootView.findViewById(R.id.btnMonthlyFees);
        btnMakePayment = (Button) rootView.findViewById(R.id.btnMakePayment);

        child_id = Util_SharedPreference.getChildIdFromSP(getActivity());
        school_id = Util_SharedPreference.getSchoolIdFromSP(getActivity());

        getPendingDetails();
        mAdapter = new PendingDetailsAdapter(pending_list, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycle_upcomming_list.setLayoutManager(mLayoutManager);
        recycle_upcomming_list.setItemAnimator(new DefaultItemAnimator());
        recycle_upcomming_list.setAdapter(mAdapter);
        recycle_upcomming_list.getRecycledViewPool().setMaxRecycledViews(0, 80);
        btnMonthlyFees.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent monthlyfees = new Intent(getActivity(), MonthlyPendingFees.class);
                startActivity(monthlyfees);
            }
        });

        btnMakePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent payment = new Intent(getActivity(), PaymentWebViewActivity.class);
                startActivity(payment);
            }
        });
        return rootView;

    }
    private void getPendingDetails() {
        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("SchoolID", school_id);
        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetStudentPendingFee(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject jresponse = new JSONObject(response.body().toString());
                        JSONArray values = jresponse.getJSONArray("StudentFeeDetails");

                        if (values.length() > 0) {
                            btnMakePayment.setVisibility(View.VISIBLE);

                            mAdapter.clearAllData();
                            PendingFeeDetails datas;
                            for (int i = 0; i < values.length(); i++) {
                                JSONObject jsonObject = values.getJSONObject(i);
                                String FeeName = jsonObject.getString("FeeName");
                                String Term_I = jsonObject.getString("Term_I");
                                String Term_II = jsonObject.getString("Term_II");
                                String Term_III = jsonObject.getString("Term_III");
                                String Term_IV = jsonObject.getString("Term_IV");
                                String Total = jsonObject.getString("Total");
                                String Term1_From = jsonObject.getString("Term1_From");
                                String Term1_To = jsonObject.getString("Term1_To");
                                String Term2_From = jsonObject.getString("Term2_From");
                                String Term2_To = jsonObject.getString("Term2_To");
                                String Term3_From = jsonObject.getString("Term3_From");
                                String Term3_To = jsonObject.getString("Term3_To");
                                String Term4_From = jsonObject.getString("Term4_From");
                                String Term4_To = jsonObject.getString("Term4_To");
                                String FeeTerms = jsonObject.getString("FeeTerms");

                                String Monthly = jsonObject.getString("Monthly");
                                String Yearly = jsonObject.getString("Yearly");
                                String FeeGroupId = jsonObject.getString("FeeGroupId");

                                datas = new PendingFeeDetails(FeeName, Term_I, Term_II, Term_III, Term_IV, Total, Term1_From, Term1_To, Term2_From, Term2_To, Term3_From, Term3_To,
                                        Term4_From, Term4_To, FeeTerms, Monthly, Yearly, FeeGroupId);

                                pending_list.add(datas);

                            }
                            mAdapter.notifyDataSetChanged();
                            pending_size = String.valueOf(pending_list.size());
                        } else {
                            pending_size = "0";
                            btnMakePayment.setVisibility(View.GONE);

                            showAlert();
                        }
                        JSONArray monthly = jresponse.getJSONArray("StudentMonthlyFees");
                        MonthlyPending data;

                        for (int j = 0; j < monthly.length(); j++) {
                            JSONObject jsonObject = monthly.getJSONObject(j);
                            String FeeName = jsonObject.getString("FeeName");
                            String Monthy = jsonObject.getString("Monthly");
                            String total = jsonObject.getString("TotalMonthly");
                            String StartMonthName = jsonObject.getString("StartMonthName");
                            String EndMonthName = jsonObject.getString("EndMonthName");
                            String PendingAmount = jsonObject.getString("PendingAmount");
                            data = new MonthlyPending(FeeName, Monthy, total, StartMonthName, EndMonthName, PendingAmount);
                            monthly_list.add(data);
                        }
                        if (monthly_list.size() > 0) {
                            btnMonthlyFees.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getActivity(), R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(R.string.no_pending_records);
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
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

    }
}