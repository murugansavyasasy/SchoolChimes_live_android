package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.LessonPlan.Model.EditDataItem;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.EditDataList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;


public class TeacherNoticeBoard extends AppCompatActivity {

    Button btnNext;
    EditText etMessage, etTopic;
    TextView tvcount;
    String strmessage;
    RecyclerView rvSchoolsList;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();
    private int i_schools_count = 0;

    String loginType;
    private int iRequestCode;
    TextView lblFromDate, lblToDate;
    DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_notice_board);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        btnNext = (Button) findViewById(R.id.nb_btnmsg);
        etMessage = (EditText) findViewById(R.id.nb_txtmessage);
        tvcount = (TextView) findViewById(R.id.nb_msgcount);
        lblFromDate = (TextView) findViewById(R.id.lblFromDate);
        lblToDate = (TextView) findViewById(R.id.lblToDate);
        etMessage.addTextChangedListener(mTextEditorWatcher);

        etMessage.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View view, MotionEvent event) {

                if (view.getId() == R.id.nb_txtmessage) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            view.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });


        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        lblFromDate.setText(formattedDate);
        lblToDate.setText(formattedDate);

        lblFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(1);
            }
        });
        lblToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(2);

            }
        });


        etTopic = (EditText) findViewById(R.id.nb_txtTitle);
        etTopic.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableSubmitIfReady();
            }
        });

        rvSchoolsList = (RecyclerView) findViewById(R.id.nb_rvSchoolsList);
        rvSchoolsList.setNestedScrollingEnabled(false);

        ImageView ivBack = (ImageView) findViewById(R.id.nbPopup_ToolBarIvBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                Log.d("SelectedCount", "" + i_schools_count);
                if (i_schools_count > 0) {
                    SendEmergencyVoiceGroupheadAPI();
                } else {
                    btnNext.setEnabled(true);
                    showToast(getResources().getString(R.string.teacher_Select_school));
                }
            }
        });



        loginType = TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherNoticeBoard.this);
        if (loginType.equals(LOGIN_TYPE_PRINCIPAL)) {


            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        } else if (loginType.equals(LOGIN_TYPE_HEAD)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        } else if (loginType.equals(LOGIN_TYPE_ADMIN)) {
            rvSchoolsList.setVisibility(View.VISIBLE);
            listSchoolsAPI();
        }
    }

    private void openDatePicker(int type) {

        // calender class's instance and get current date , month and year from calender
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR); // current year
        int mMonth = c.get(Calendar.MONTH); // current month
        int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
        // date picker dialog
        datePickerDialog = new DatePickerDialog(TeacherNoticeBoard.this, R.style.DatePickerTheme,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // set day of month , month and year value in the edit text

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                        String dateString = format.format(calendar.getTime());

                        if(type == 1){
                            lblFromDate.setText(dateString);
                        }
                        else {
                           lblToDate.setText(dateString);
                        }
                    }
                }, mYear, mMonth, mDay);

       // datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etMessage.getWindowToken(), 0);

    }

    private void listSchoolsAPI() {
        i_schools_count = 0;
        for (int i = 0; i < listschooldetails.size(); i++) {
            TeacherSchoolsModel ss = listschooldetails.get(i);
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true,ss.getBookEnable(),ss.getOnlineLink(),ss.getIsPaymentPending());
            arrSchoolList.add(ss);
        }




        TeacherSchoolsListAdapter schoolsListAdapter =
                new TeacherSchoolsListAdapter(TeacherNoticeBoard.this, new TeacherOnCheckSchoolsListener() {
                    @Override
                    public void school_addSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (!seletedschoollist.contains(school))) {
                            seletedschoollist.add(school);
                            i_schools_count++;
                            enableSubmitIfReady();
                        }
                    }

                    @Override
                    public void school_removeSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (seletedschoollist.contains(school))) {
                            seletedschoollist.remove(school);
                            i_schools_count--;
                            enableSubmitIfReady();
                        }
                    }
                }, arrSchoolList);

        rvSchoolsList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherNoticeBoard.this);
        rvSchoolsList.setLayoutManager(layoutManager);
        rvSchoolsList.addItemDecoration(new DividerItemDecoration(TeacherNoticeBoard.this, LinearLayoutManager.VERTICAL));
        rvSchoolsList.setItemAnimator(new DefaultItemAnimator());
        rvSchoolsList.setAdapter(schoolsListAdapter);
    }


    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //tvcount.setText("" + (460 - (s.length())));
        }

        public void afterTextChanged(Editable s) {
            enableSubmitIfReady();
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public void enableSubmitIfReady() {
        boolean isTitleReady = etMessage.getText().toString().length() > 0;
        boolean isContentReady = etTopic.getText().toString().length() > 0;


            if (isContentReady && isTitleReady && (i_schools_count > 0)) {
                btnNext.setEnabled(true);
            } else if ((isContentReady && isTitleReady) && (i_schools_count == 0)) {
                btnNext.setEnabled(true);
            } else {
                btnNext.setEnabled(false);
            }



    }

    public void validation() {
        strmessage = etMessage.getText().toString().trim();
    }


    private void SendEmergencyVoiceGroupheadAPI() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(TeacherNoticeBoard.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherNoticeBoard.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        JsonObject jsonReqArray = constructJsonArrayMgtSchoolsGrouphead();
        Call<JsonArray> call = apiService.ManageNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");

                            if ((strStatus.toLowerCase()).equals("1")) {
                                showAlert(strMsg);
                            }
                            else {
                                showAlert(strMsg);
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                showToast(t.toString());
            }
        });
    }

    private void showAlert(String strMsg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherNoticeBoard.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(strMsg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                onBackPressed();
                dialog.cancel();
                finish();

            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private JsonObject constructJsonArrayMgtSchoolsGrouphead() {
        String strdescription = etTopic.getText().toString();
        JsonObject jsonObjectSchool = new JsonObject();
        try {
            jsonObjectSchool.addProperty("TopicHeading", strdescription);
            jsonObjectSchool.addProperty("TopicBody", strmessage);
            jsonObjectSchool.addProperty("FromDate", lblFromDate.getText().toString());
            jsonObjectSchool.addProperty("ToDate", lblToDate.getText().toString());

            JsonArray jsonArrayschool = new JsonArray();

            for (int i = 0; i < seletedschoollist.size(); i++) {
                JsonObject jsonObjectschoolId = new JsonObject();
                jsonObjectschoolId.addProperty("SchoolId", seletedschoollist.get(i).getStrSchoolID());
                jsonObjectschoolId.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                jsonArrayschool.add(jsonObjectschoolId);
            }

            jsonObjectSchool.add("Schools", jsonArrayschool);
            Log.d("Final_Array", jsonObjectSchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonObjectSchool;
    }
}
