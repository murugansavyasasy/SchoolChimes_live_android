package com.vs.schoolmessenger.LessonPlan.Activity;



import static com.vs.schoolmessenger.util.TeacherUtil_Common.EditDataList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.vs.schoolmessenger.LessonPlan.Adapter.ViewLessonPlanAdapter;
import com.vs.schoolmessenger.LessonPlan.Model.DataArrayItem;
import com.vs.schoolmessenger.LessonPlan.Model.EditDataItem;
import com.vs.schoolmessenger.LessonPlan.Model.ViewDataItem;
import com.vs.schoolmessenger.LessonPlan.Model.ViewLessonPlanModel;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherGeneralText;
import com.vs.schoolmessenger.activity.TeacherStandardsAndGroupsList;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewLessonPlanActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView rvViewLessonPlans;
    ViewLessonPlanAdapter mAdapter;

    RelativeLayout rytParent;
    int Section_ID = 0;
    String SchoolID, StaffID;

    int scrollToPosition = 0;
    boolean scroll = false;
    public  List<ViewDataItem> ViewlessonDataList = new ArrayList<ViewDataItem>();
    EditText Searchable;

    ImageView imgSearch;

    TextView lblYetToStart, lblInProgress, lblCompleted, lblAll;

    Boolean ifValueExist = false;
    int SearchIndex = 0;

    Spinner spinners;
    private int iRequestCode;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_lesson_plan_activity);
        rvViewLessonPlans = (RecyclerView) findViewById(R.id.rvViewLessonPlans);
        rytParent = (RelativeLayout) findViewById(R.id.rytParent);
        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        lblYetToStart = (TextView) findViewById(R.id.lblYetToStart);
        lblInProgress = (TextView) findViewById(R.id.lblInProgress);
        lblCompleted = (TextView) findViewById(R.id.lblCompleted);
        lblAll = (TextView) findViewById(R.id.lblAll);
        spinners = (Spinner) findViewById(R.id.spinners);

        lblYetToStart.setOnClickListener(this);
        lblInProgress.setOnClickListener(this);
        lblCompleted.setOnClickListener(this);
        lblAll.setOnClickListener(this);

        Section_ID = getIntent().getExtras().getInt("section_subject_id", 0);
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("View ");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        SchoolID = TeacherUtil_Common.Principal_SchoolId;
        StaffID = TeacherUtil_Common.Principal_staffId;

        TeacherUtil_Common.EditDataList.clear();

        String[] values = {"ALL","YET TO START","IN PROGRESS","COMPLETED"};
        ArrayAdapter adapter
                = new ArrayAdapter(
                ViewLessonPlanActivity.this,
                R.layout.text_spinner,R.id.text1,
                values);

        spinners.setAdapter(adapter);

        spinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getViewLessonPlans(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (mAdapter == null)
                    return;

                if (mAdapter.getItemCount() < 1) {
                    rvViewLessonPlans.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvViewLessonPlans.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvViewLessonPlans.setVisibility(View.VISIBLE);
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
    }

    private void filterlist(String s) {
        List<ViewDataItem> temp = new ArrayList();
        for (ViewDataItem d : ViewlessonDataList) {
            List<DataArrayItem> dataArray = d.getDataArray();
            for (int i = 0; i < dataArray.size(); i++) {

                if(dataArray.get(i).getValue() != null) {
                    if (dataArray.get(i).getValue().toLowerCase().contains(s.toLowerCase())) {
                        SearchIndex = ViewlessonDataList.indexOf(d);
                    }
                }
            }

            ViewDataItem data = ViewlessonDataList.get(SearchIndex);
            if(!temp.contains(data)) {
                temp.add(data);
            }

        }
        mAdapter.updateList(temp);
    }

    private void getViewLessonPlans(int type) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ViewLessonPlanActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<ViewLessonPlanModel> call = apiService.getViewLessonPlans(String.valueOf(Section_ID), SchoolID, StaffID, String.valueOf(type));

        call.enqueue(new Callback<ViewLessonPlanModel>() {
            @Override
            public void onResponse(Call<ViewLessonPlanModel> call, retrofit2.Response<ViewLessonPlanModel> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("daily:code-res", response.code() + " - " + response.toString());

                    if (response.code() == 200 || response.code() == 201) {
                        Gson gson = new Gson();
                        String json = gson.toJson(response.body());
                        Log.d("Lessons_Response", json);
                        int status = response.body().getStatus();
                        String message = response.body().getMessage();
                        ViewlessonDataList.clear();
                        if (status == 1) {
                            ViewlessonDataList = response.body().getData();

                            for (ViewDataItem data : ViewlessonDataList) {
                                int status_id = data.getStatus();
                                if (status_id == 1) {
                                    scroll = true;
                                    scrollToPosition = ViewlessonDataList.indexOf(data);
                                    break;
                                }
                            }

                            if(!scroll){
                                for (ViewDataItem data : ViewlessonDataList) {
                                    int status_id = data.getStatus();
                                    if (status_id == 2) {
                                        scrollToPosition = ViewlessonDataList.indexOf(data);
                                        break;
                                    }
                                }
                            }


                            Log.d("scrollToPosition", String.valueOf(scrollToPosition));
                            mAdapter = new ViewLessonPlanAdapter(ViewlessonDataList,iRequestCode, ViewLessonPlanActivity.this,rytParent);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvViewLessonPlans.setLayoutManager(mLayoutManager);
                            rvViewLessonPlans.setItemAnimator(new DefaultItemAnimator());
                            rvViewLessonPlans.scrollToPosition(scrollToPosition);
                            rvViewLessonPlans.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        } else {
                            if (mAdapter != null) {
                                mAdapter.clearAllData();
                                mAdapter.notifyDataSetChanged();

                            }
                            showAlert(message);
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ViewLessonPlanModel> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewLessonPlanActivity.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lblAll:
                getViewLessonPlans(0);

                break;

            case R.id.lblYetToStart:
                getViewLessonPlans(1);
                break;

            case R.id.lblInProgress:
                getViewLessonPlans(2);
                break;

            case R.id.lblCompleted:
                getViewLessonPlans(3);

                break;

        }
    }
}