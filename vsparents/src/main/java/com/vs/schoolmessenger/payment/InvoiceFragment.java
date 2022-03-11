package com.vs.schoolmessenger.payment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.InvoiceDetailsAdapter;
import com.vs.schoolmessenger.adapter.KnowledgeEnhancementAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.InVoiceDetailsModel;
import com.vs.schoolmessenger.model.KnowledgeEnhancementModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceFragment extends Fragment {
    RecyclerView recyclerView;
    InvoiceDetailsAdapter invoiceadapter;
    private ArrayList<InVoiceDetailsModel> invoiceList = new ArrayList<>();

    String isNewVersion;
    Boolean show=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.questions_recylerview, container, false);
    }

    @SuppressLint("NewApi")

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        recyclerView = view.findViewById(R.id.recycleview);
        isNewVersion= TeacherUtil_SharedPreference.getNewVersion(getActivity());

        getInvoiceDetails();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);
        invoiceadapter = new InvoiceDetailsAdapter(getContext(), invoiceList);
        recyclerView.setAdapter(invoiceadapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        getInvoiceDetails();

    }
    private void getInvoiceDetails() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if (isNewVersionn.equals("1")) {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


        String strChildID = Util_SharedPreference.getChildIdFromSP(getActivity());
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(getActivity());
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("SchoolID", strSchoolID);
        jsonObject.addProperty("ChildID", strChildID);
        jsonObject.addProperty("FeeCategory", "1");
        Log.d("InvoiceRequest", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetInvoiceData(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Invoice:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Invoice:Res", response.body().toString());
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    int status = jsonObject.getInt("status");
                    String message = jsonObject.getString("message");

                    if (status==1) {
                        invoiceList.clear();

                        JSONArray data = jsonObject.getJSONArray("data");
                        if(data.length()!=0) {
                            InVoiceDetailsModel msgModel;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject js = data.getJSONObject(i);
                                msgModel = new InVoiceDetailsModel(
                                        js.getString("id"),
                                        js.getString("invoiceNo"),
                                        js.getString("invoiceDate"),
                                        js.getString("invoiceAmount")

                                );
                                invoiceList.add(msgModel);
                            }
                            invoiceadapter.notifyDataSetChanged();
                        }
                        else{
                            invoiceadapter.notifyDataSetChanged();

                            alert(message);
                        }
                    } else {

                        alert(message);
                    }

                } catch (Exception e) {
                    Log.e("Invoice:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Invoice:Failure", t.toString());
            }
        });
    }


    private void alert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Objects.requireNonNull(getActivity()).finish();
                dialog.cancel();

            }
        });

        alertDialog.show();
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

}
