package com.vs.schoolmessenger.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.CertificatesListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CertificateListDataItem;
import com.vs.schoolmessenger.model.CertificateListModelItem;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CertifiatesFragments extends Fragment {

    String childID = "";
    String SchoolID = "";
    RecyclerView rvCertificates;

    private List<CertificateListDataItem> certificateList = new ArrayList<CertificateListDataItem>();

    CertificatesListAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.certificates_fragment, container, false);
        rvCertificates = rootView.findViewById(R.id.rvCertificates);

        childID = Util_SharedPreference.getChildIdFromSP(getActivity());
        SchoolID = Util_SharedPreference.getSchoolIdFromSP(getActivity());
        getCertificatesList();

        return rootView;
    }

    private void getCertificatesList() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);


        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("student_id", childID);
        Log.d("request",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<List<CertificateListModelItem>> call = apiService.getParentCertificateList(jsonObject);

        call.enqueue(new Callback<List<CertificateListModelItem>>() {
            @Override
            public void onResponse(Call<List<CertificateListModelItem>> call, retrofit2.Response<List<CertificateListModelItem>> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response.toString());

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Lessons_Response", json);
                        List<CertificateListModelItem> data = response.body();

                        String status = data.get(0).getStatus();
                        String message = data.get(0).getMessage();

                        if (status.equals("1")) {
                            certificateList.clear();
                            certificateList = data.get(0).getData();

                            mAdapter = new CertificatesListAdapter(certificateList, getActivity());
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            rvCertificates.setLayoutManager(mLayoutManager);
                            rvCertificates.setItemAnimator(new DefaultItemAnimator());
                            rvCertificates.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                        else {
                            showAlert(message);
                        }

                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<CertificateListModelItem>> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);
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