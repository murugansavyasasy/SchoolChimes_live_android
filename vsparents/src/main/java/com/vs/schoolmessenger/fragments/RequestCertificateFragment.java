package com.vs.schoolmessenger.fragments;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CertificateDataItem;
import com.vs.schoolmessenger.model.CertificateRequestModel;
import com.vs.schoolmessenger.model.CertificateRequestModelItem;
import com.vs.schoolmessenger.model.CertificateTypeModel;
import com.vs.schoolmessenger.model.CertificateTypeModelItem;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class RequestCertificateFragment extends Fragment {

    String childID = "";
    String SchoolID = "";

    Spinner spinnerCertificates;
    EditText txtReason;
    Button btnRequestCertificate;

    String selectedSpinnerValue = "";

    String[] values;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.request_certificate_fragment, container, false);

        spinnerCertificates = rootView.findViewById(R.id.spinnerCertificates);
        txtReason = rootView.findViewById(R.id.txtReason);
        btnRequestCertificate = rootView.findViewById(R.id.btnRequestCertificate);

        childID = Util_SharedPreference.getChildIdFromSP(getActivity());
        SchoolID = Util_SharedPreference.getSchoolIdFromSP(getActivity());

        getCertificateTypes();

        btnRequestCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCertificate();
            }
        });

        return rootView;
    }

    private void requestCertificate() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("instituteId", SchoolID);
        jsonObject.addProperty("student_id", childID);
        jsonObject.addProperty("requested_for", selectedSpinnerValue);
        jsonObject.addProperty("reason", txtReason.getText().toString());
        jsonObject.addProperty("urgency_level", "Urgent");
        Log.d("request",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<CertificateRequestModel> call = apiService.createCertificateRequest(jsonObject);

        call.enqueue(new Callback<CertificateRequestModel>() {
            @Override
            public void onResponse(Call<CertificateRequestModel> call, retrofit2.Response<CertificateRequestModel> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response.toString());

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Lessons_Response", json);
                        List<CertificateRequestModelItem> data = response.body().getCertificateRequestModel();

                        String status = data.get(0).getStatus();
                        String message = data.get(0).getMessage();

                        if (status.equals("1")) {
                            showAlert(message);
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
            public void onFailure(Call<CertificateRequestModel> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getActivity(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void getCertificateTypes() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(getActivity());
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<CertificateTypeModel> call = apiService.getCertificateTypes();

        call.enqueue(new Callback<CertificateTypeModel>() {
            @Override
            public void onResponse(Call<CertificateTypeModel> call, retrofit2.Response<CertificateTypeModel> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response.toString());

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Lessons_Response", json);
                        List<CertificateTypeModelItem> data = response.body().getCertificateTypeModel();

                        String status = data.get(0).getStatus();
                        String message = data.get(0).getMessage();

                        if (status.equals("1")) {
                            List<CertificateDataItem> certificates = data.get(0).getData();

                            values = new String[certificates.size()+1];
                            values[0] = "Select Certificate";
                            for (int i = 0; i < certificates.size(); i++) {
                                values[i+1] = certificates.get(i).getCertificateName();
                            }

                            ArrayAdapter adapter
                                    = new ArrayAdapter(
                                    getActivity(),
                                    R.layout.text_spinner, R.id.text1,
                                    values);

                            spinnerCertificates.setAdapter(adapter);

                            spinnerCertificates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                    if(position != 0) {
                                        selectedSpinnerValue = values[position-1];
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


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
            public void onFailure(Call<CertificateTypeModel> call, Throwable t) {
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