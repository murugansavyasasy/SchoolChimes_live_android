package com.vs.schoolmessenger.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ExamEnhancementAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamEnhancement;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UpcomingExamEnhancement extends Fragment {
    RecyclerView recycle_paidlist;
    String school_id, child_id;
    TextView lblNoMessages;
    private ArrayList<ExamEnhancement> msgModelList = new ArrayList<>();
    public ExamEnhancementAdapter mAdapter;
    TextView LoadMore;
    Calendar c;
    Boolean show=false;
    ImageView imgSearch;
    TextView Searchable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.feespending_recycle, container, false);
        recycle_paidlist = (RecyclerView) rootView.findViewById(R.id.fees_pending_recycler_view);

        child_id = Util_SharedPreference.getChildIdFromSP(getActivity());
        school_id = Util_SharedPreference.getSchoolIdFromSP(getActivity());

        c = Calendar.getInstance();
         LoadMore=(TextView) rootView.findViewById(R.id.btnSeeMore);
         lblNoMessages=(TextView) rootView.findViewById(R.id.lblNoMessages);

            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);

        Searchable = (EditText) rootView.findViewById(R.id.Searchable);
        imgSearch = (ImageView) rootView.findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    recycle_paidlist.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        recycle_paidlist.setVisibility(View.VISIBLE);
                    }

                } else {
                    recycle_paidlist.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (editable.length() > 0) {
                    imgSearch.setVisibility(View.GONE);
                } else {
                    imgSearch.setVisibility(View.VISIBLE);
                }
                filterlist(editable.toString());
            }
        });
        mAdapter = new ExamEnhancementAdapter(msgModelList, getActivity(),"1");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycle_paidlist.setLayoutManager(mLayoutManager);
        recycle_paidlist.setItemAnimator(new DefaultItemAnimator());
        recycle_paidlist.setAdapter(mAdapter);
        recycle_paidlist.getRecycledViewPool().setMaxRecycledViews(0, 80);

        return rootView;
    }

    private void filterlist(String s) {
        ArrayList<ExamEnhancement> temp = new ArrayList();
        for (ExamEnhancement d : msgModelList) {

            if (d.getTitle().toLowerCase().contains(s.toLowerCase()) || d.getSubject().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    @Override
    public void onResume() {
        super.onResume();
        examEnhancement();
    }
    private void examEnhancement() {

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
        jsonObject.addProperty("StudentID", child_id);
        jsonObject.addProperty("StatusType", 1);
        jsonObject.addProperty("SchoolID", school_id);
        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.examenhancement(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        msgModelList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            int status = jsonObject.getInt("Status");
                            String message = jsonObject.getString("Message");

                            if (status==1) {

                                JSONArray data = jsonObject.getJSONArray("data");
                                if(data.length()!=0) {
                                    ExamEnhancement msgModel;
                                    Log.d("json length", data.length() + "");

                                    for (int i = 0; i < data.length(); i++) {
                                        jsonObject = data.getJSONObject(i);
                                        msgModel = new ExamEnhancement(jsonObject.getString("Title"),
                                                jsonObject.getString("Description"),
                                                jsonObject.getString("Subject"),
                                                jsonObject.getString("SubmittedOn"),
                                                jsonObject.getString("Issubmitted"),
                                                jsonObject.getString("isAppRead"),
                                                jsonObject.getString("SentBy"),
                                                jsonObject.getString("ExamStartTime"),
                                                jsonObject.getString("ExamEndTime"),
                                                jsonObject.getString("TimeForQuestionReading"),
                                                jsonObject.getString("TotalNumberOfQuestions"),
                                                jsonObject.getString("RightAnswer"),
                                                jsonObject.getString("ExamDate"),
                                                jsonObject.getString("WrongAnswer"),
                                                jsonObject.getString("MaxMark"),
                                                jsonObject.getString("createdOn"),
                                                jsonObject.getInt("QuizId"),
                                                jsonObject.getInt("detailId"),
                                                jsonObject.getString("isShow")
                                        );
                                        msgModelList.add(msgModel);

                                    }
                                    mAdapter.notifyDataSetChanged();

                                }
                                else {
                                    recycle_paidlist.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    if(show==false) {
                                        showAlert(getResources().getString(R.string.no_records));
                                    }
                                }
                            } else {
                                recycle_paidlist.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();
                                if(show==false) {
                                    showAlert(message);
                                }

                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(getActivity(), "Server Response Failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String msg) {
        show=true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Alert");

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                show=false;
            }
        });

        AlertDialog dialog = alertDialog.create();

         if(dialog.isShowing()){
             dialog.cancel();
             dialog.dismiss();
         }
         else{
             dialog.show();
         }
         dialog.setCanceledOnTouchOutside(false);

         Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
         positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

}
