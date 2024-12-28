package com.vs.schoolmessenger.LessonPlan.Activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Adapter.LessonPlanAdapter;
import com.vs.schoolmessenger.LessonPlan.Model.LessPlanData;
import com.vs.schoolmessenger.LessonPlan.Model.LessonPlanModel;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.DailyFeeCollectionActivity;
import com.vs.schoolmessenger.activity.TeacherGeneralText;
import com.vs.schoolmessenger.adapter.PaymentTypeAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.DailyFeeCollectionModelItem;
import com.vs.schoolmessenger.model.DatesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class LessonPlanActivity extends AppCompatActivity {

    RecyclerView rvLessonPlans;
    LessonPlanAdapter mAdapter;
    LinearLayout  lnrTab,lnrAllClasses,lnrYourHandled;
    TextView lblAllClasses,lblYouHandled;
    String SchoolID,StaffID;
    private List<LessPlanData> lessonDataList = new ArrayList<LessPlanData>();
    String type = "";

    EditText Searchable;

    ImageView imgSearch;

    private int iRequestCode;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lesson_plan_activity);

        rvLessonPlans = (RecyclerView) findViewById(R.id.rvLessonPlans);
        lnrTab = (LinearLayout) findViewById(R.id.lnrTab);
        lnrAllClasses = (LinearLayout) findViewById(R.id.lnrAllClasses);
        lnrYourHandled = (LinearLayout) findViewById(R.id.lnrYourHandled);
        lblAllClasses = (TextView) findViewById(R.id.lblAllClasses);
        lblYouHandled = (TextView) findViewById(R.id.lblYouHandled);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.Lesson_Plan);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);


        if(TeacherUtil_SharedPreference.getLoginTypeContextFromSP(LessonPlanActivity.this).equals(LOGIN_TYPE_TEACHER)){
           lnrTab.setVisibility(View.GONE);
           type = "myclass";
           TeacherUtil_Common.lesson_request_type = type;
        }
       else {
           lnrTab.setVisibility(View.VISIBLE);
           type ="allclass";
           TeacherUtil_Common.lesson_request_type = type;

       }

        if ((listschooldetails.size() == 1)) {
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }
        else if(TeacherUtil_SharedPreference.getLoginTypeFromSP(LessonPlanActivity.this).equals(LOGIN_TYPE_TEACHER)){
            SchoolID = TeacherUtil_Common.Principal_SchoolId;
            StaffID = TeacherUtil_Common.Principal_staffId;
        }
        else {
            SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
            StaffID = getIntent().getExtras().getString("STAFF_ID", "");
        }

        TeacherUtil_Common.Principal_SchoolId = SchoolID;
        TeacherUtil_Common.Principal_staffId = StaffID;

        lnrAllClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter != null) {
                    mAdapter.clearAllData();
                }
                getLessonPlans("allclass");
                TeacherUtil_Common.lesson_request_type = "allclass";

                lnrAllClasses.setBackground(getResources().getDrawable(R.drawable.bg_stroke_teacherprimary));
                lnrYourHandled.setBackground(getResources().getDrawable(R.drawable.bg_rect_white));
            }
        });
        lnrYourHandled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAdapter != null) {
                    mAdapter.clearAllData();
                }

                getLessonPlans("myclass");
                TeacherUtil_Common.lesson_request_type = "myclass";

                lnrAllClasses.setBackground(getResources().getDrawable(R.drawable.bg_rect_white));
                lnrYourHandled.setBackground(getResources().getDrawable(R.drawable.bg_stroke_teacherprimary));

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
                    rvLessonPlans.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvLessonPlans.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvLessonPlans.setVisibility(View.VISIBLE);
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
        List<LessPlanData> temp = new ArrayList();
        for (LessPlanData d : lessonDataList) {

            if (d.getClassName().toLowerCase().contains(s.toLowerCase()) || d.getStaffName().toLowerCase().contains(s.toLowerCase()) ||
                    d.getSectionName().toLowerCase().contains(s.toLowerCase()) || d.getSubjectName().toLowerCase().contains(s.toLowerCase()) ||
                    d.getTotalItems().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        mAdapter.updateList(temp);
    }

    @Override
    public void onResume(){
        super.onResume();
        getLessonPlans(type);
    }

    private void getLessonPlans(String type) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(LessonPlanActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<LessonPlanModel> call = apiService.getLessonPlans(type,SchoolID,StaffID);

        call.enqueue(new Callback<LessonPlanModel>() {
            @Override
            public void onResponse(Call<LessonPlanModel> call, retrofit2.Response<LessonPlanModel> response) {
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

                        lessonDataList.clear();
                        if(status == 1) {
                            lessonDataList = response.body().getData();
                            mAdapter = new LessonPlanAdapter(lessonDataList,iRequestCode, LessonPlanActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rvLessonPlans.setLayoutManager(mLayoutManager);
                            rvLessonPlans.setItemAnimator(new DefaultItemAnimator());
                            rvLessonPlans.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();

                        }
                        else {
                            showAlert(message);
                        }


                    }else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<LessonPlanModel> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LessonPlanActivity.this);
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
}