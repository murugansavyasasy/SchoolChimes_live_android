package com.vs.schoolmessenger.fragments;

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

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.KnowledgeEnhancementAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.KnowledgeEnhancementModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpcomingKnowledgeEnhancement extends Fragment {
    RecyclerView recyclerView;
    ImageView imgBack;
    KnowledgeEnhancementAdapter knowledgeadapter;
    private ArrayList<KnowledgeEnhancementModel> msgModelList = new ArrayList<>();

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
//        tabLayout= Objects.requireNonNull(getActivity()).findViewById(R.id.assignTablayout);
        recyclerView = view.findViewById(R.id.recycleview);
        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());


            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);
            knowledgeadapter = new KnowledgeEnhancementAdapter(getContext(), msgModelList,"1");
            recyclerView.setAdapter(knowledgeadapter);


    }

    @Override
    public void onResume() {
        super.onResume();
        getKnowledgeEnchancement();
    }

    private void getKnowledgeEnchancement() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
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
        jsonObject.addProperty("StudentID", strChildID);
        jsonObject.addProperty("StatusType", 1);
        jsonObject.addProperty("SchoolID", strSchoolID);

        Log.d("Request", jsonObject.toString());


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.knowledgeEnhanement(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("KnowlwgeEnhancment:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("KnowlwgeEnhancment:Res", response.body().toString());
                msgModelList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    int status = jsonObject.getInt("Status");
                    String message = jsonObject.getString("Message");

                    if (status==1) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        if(data.length()!=0) {


                            KnowledgeEnhancementModel msgModel;
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject js = data.getJSONObject(i);
                                msgModel = new KnowledgeEnhancementModel(js.getInt("QuizId"),
                                        js.getString("Title"),
                                        js.getString("Description"),
                                        js.getString("Subject"),
                                        js.getString("SubmittedOn"),
                                        js.getString("Issubmitted"),
                                        js.getString("isAppRead"),
                                        js.getString("SentBy"),
                                        js.getString("TotalNumberOfQuestions"),
                                        js.getString("RightAnswer"),
                                        js.getString("WrongAnswer"),
                                        js.getString("MaxMark"),
                                        js.getInt("Level"),
                                        js.getString("NoOfLevels"),
                                        js.getString("createdOn"),
                                        js.getInt("detailId")
                                );
                                msgModelList.add(msgModel);
                            }


                            knowledgeadapter.notifyDataSetChanged();

                        }
                        else{
                            recyclerView.setAdapter(knowledgeadapter);
                            knowledgeadapter.notifyDataSetChanged();
                            if(show==false) {
                                showAlertRecords(getResources().getString(R.string.no_records));
                            }


                        }


                    } else {
                        recyclerView.setAdapter(knowledgeadapter);
                        knowledgeadapter.notifyDataSetChanged();
                        if(show==false) {
                            showAlertRecords(message);
                        }

                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Knowledge:Failure", t.toString());
            }
        });
    }

    private void showAlertRecords(String msg) {
        show=true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
//                getActivity().finish();
                show=false;

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));

    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();

    }

}
