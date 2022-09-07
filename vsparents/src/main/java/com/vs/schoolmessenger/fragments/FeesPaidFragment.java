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

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.adapter.PaidDetailsAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.PaidDetailsList;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by voicesnap on 5/17/2018.
 */

public class FeesPaidFragment extends Fragment {
    RecyclerView recycle_paidlist;
    String school_id,child_id;
    Context context;
    private ArrayList<PaidDetailsList> paid_list = new ArrayList<>();

    public PaidDetailsAdapter mAdapter;

    public static String paid_size="";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.feespending_recycle, container, false);
        recycle_paidlist = (RecyclerView) rootView.findViewById(R.id.fees_pending_recycler_view);

        child_id= Util_SharedPreference.getChildIdFromSP(getActivity());
         school_id=Util_SharedPreference.getSchoolIdFromSP(getActivity());

         Log.e("sizee123", String.valueOf(paid_list.size()));

         mAdapter = new PaidDetailsAdapter(paid_list, getActivity());
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
        jsonObject.addProperty("ChildID", child_id);
        jsonObject.addProperty("SchoolID", school_id);


        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.GetStudentInvoice_App(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject data=new JSONObject(response.body().toString());
                        JSONArray js = data.getJSONArray("StudentInvoices");
                        if (js.length() > 0) {
                            mAdapter.clearAllData();
                            PaidDetailsList datas;
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");
                                String Message = jsonObject.getString("Message");

                                if(result.equals("-2")){
                                    showRecordsFound(Message);
                                }
                                else {
                                    String invoice = jsonObject.getString("InvoiceId");
                                    String totalpaid = jsonObject.getString("TotalPaid");
                                    Log.d("total", totalpaid);
                                    String type = jsonObject.getString("PaymentType");
                                    String created_on = jsonObject.getString("CreatedOn");
                                    String latefee = jsonObject.getString("LateFee");
                                    String isRejected = jsonObject.getString("IsRejected");

                                    Log.d("Response1234", "");
                                    datas = new PaidDetailsList(invoice, totalpaid, type, created_on, latefee, isRejected);
                                    paid_list.add(datas);
                                }

                                }
                            mAdapter.notifyDataSetChanged();


                            paid_size=String.valueOf(paid_list.size());


                        }
                        else {
                            paid_size="0";
                            showAlert();
                        }
                    }else {
                        Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle( R.string.alert);
        alertDialog.setMessage( R.string.no_paid_records);


        alertDialog.setNegativeButton( R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
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

    private void showRecordsFound(String name) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(name);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                }
        });

        alertDialog.show();
    }


    private void getPaidDetails() {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(getActivity());
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

        JsonObject jsonReqArray = Util_JsonRequest.getJson_feesDetails(child_id, school_id);

        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.GetStudentInvoice_App(jsonObject);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.d("Response123456", "");
                try {

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("Response1234567", "hi");
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONObject data=new JSONObject(response.body().toString());
                        JSONArray js = data.getJSONArray("StudentInvoices");

                        Log.d("Response1234567", String.valueOf(js.length()));
                            if(js.length()>0) {
                                Log.d("Response1234567", String.valueOf(js.length()));
                                mAdapter.clearAllData();
                            PaidDetailsList datas;
                            for (int i = 0; i < js.length(); i++) {

                                Log.d("Response123", "");
                                JSONObject jsonObject = js.getJSONObject(i);



                                String invoice = jsonObject.getString("InvoiceId");
                                String totalpaid = jsonObject.getString("TotalPaid");
                                String type = jsonObject.getString("PaymentType");
                                String created_on = jsonObject.getString("CreatedOn");
                                String latefee = jsonObject.getString("LateFee");
                                String isRejected = jsonObject.getString("IsRejected");

                                Log.d("Response1234", "");
                                datas = new PaidDetailsList(invoice, totalpaid, type, created_on, latefee, isRejected);
                                paid_list.add(datas);

                                }
                            Log.e("sizee", String.valueOf(paid_list.size()));
                                Log.d("Response12345", "");

                            mAdapter.notifyDataSetChanged();

                        }
                        else {
                            Toast.makeText(getActivity(), R.string.no_records, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(),  R.string.check_internet, Toast.LENGTH_SHORT).show();

            }
        });
    }
}