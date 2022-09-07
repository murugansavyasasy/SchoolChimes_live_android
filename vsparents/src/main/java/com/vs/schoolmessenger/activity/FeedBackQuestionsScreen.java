package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.QuestionDetails;
import com.vs.schoolmessenger.model.SubQustions;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by voicesnap on 5/10/2018.
 */

public class FeedBackQuestionsScreen extends AppCompatActivity {


    TextView lblQuestion1, lblQuestion2, lblQuestion3, lblQuestion4, lblQuestion5, lblQuestion6, lblQuestion7, lblQuestion8, lblQuestion9;

    RadioButton radioLessThanMonth, radioMonths,
            radioyears,
            radiogreatethan3years, radioEveryDay,
            radioOnceweek,
            radio2or3times,
            radioOneceMonth,
            radioOnNeedBasis,
            radioVoiceMostLiked,
            radioVoiceLiked,
            radioVoiceNuatral,
            radioVoiceNeedImprove,
            smsMostLiked,
            smsLiked,
            smsNuatral,
            smsNeedImprove,
            MobileMostLiked,
            MobileLiked,
            MobileNeutral,
            MobileImprove,
            ThroughAC,
            ThroughEmail,
            ThroughSupport,
            others,
            Exellent,
            VeryGood,
            Good,
            NotGood,
            Quality,
            Price,
            FirstUse,
            Usability,
            CustomerService,
            Quality9,
            Price9,
            FirstUse9,
            Usability9,
            CustomerService9,
            Nill;

    Button btnSubmit;
    EditText othercomments;


    private ArrayList<QuestionDetails> questions = new ArrayList<>();
    private ArrayList<SubQustions> subQuestions = new ArrayList<>();

    private ArrayList<SubQustions> Datas = new ArrayList<>();
    String values, othercomment;


    String schoolID, contactpersion, mobilenumber, emaiid, schoolname,StaffId;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.feedback_qstns);



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.feedback);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.to_voicesnap);
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        othercomments = (EditText) findViewById(R.id.othercomments);


        Bundle extras = getIntent().getExtras();


        schoolID = extras.getString("schoolID");
        Log.d("schoolID", schoolID);
        schoolname = extras.getString("schoolname","");
        Log.d("schoolname", schoolname);
        contactpersion = extras.getString("contactpersion","");
        Log.d("contactpersion", contactpersion);
        mobilenumber = extras.getString("mobilenumber","");
        Log.d("mobilenumber", mobilenumber);
        emaiid = extras.getString("email","");

        StaffId = extras.getString("staffID","");
        Log.d("emaiid", emaiid);



        lblQuestion1 = (TextView) findViewById(R.id.lblQuestion1);
        lblQuestion2 = (TextView) findViewById(R.id.lblQuestion2);
        lblQuestion3 = (TextView) findViewById(R.id.lblQuestion3);
        lblQuestion4 = (TextView) findViewById(R.id.lblQuestion4);
        lblQuestion5 = (TextView) findViewById(R.id.lblQuestion5);
        lblQuestion6 = (TextView) findViewById(R.id.lblQuestion6);
        lblQuestion7 = (TextView) findViewById(R.id.lblQuestion7);
        lblQuestion8 = (TextView) findViewById(R.id.lblQuestion8);
        lblQuestion9 = (TextView) findViewById(R.id.lblQuestion9);


        radioLessThanMonth = (RadioButton) findViewById(R.id.radioLessThanMonth);
        radioMonths = (RadioButton) findViewById(R.id.radioMonths);
        radioyears = (RadioButton) findViewById(R.id.radioyears);
        radiogreatethan3years = (RadioButton) findViewById(R.id.radiogreatethan3years);


        radioEveryDay = (RadioButton) findViewById(R.id.radioEveryDay);
        radioOnceweek = (RadioButton) findViewById(R.id.radioOnceweek);
        radio2or3times = (RadioButton) findViewById(R.id.radio2or3times);
        radioOnNeedBasis = (RadioButton) findViewById(R.id.radioOnNeedBasis);
        radioOneceMonth = (RadioButton) findViewById(R.id.radioOneceMonth);


        radioVoiceMostLiked = (RadioButton) findViewById(R.id.radioVoiceMostLiked);
        radioVoiceLiked = (RadioButton) findViewById(R.id.radioVoiceLiked);
        radioVoiceNuatral = (RadioButton) findViewById(R.id.radioVoiceNuatral);
        radioVoiceNeedImprove = (RadioButton) findViewById(R.id.radioVoiceNeedImprove);


        smsMostLiked = (RadioButton) findViewById(R.id.smsMostLiked);
        smsLiked = (RadioButton) findViewById(R.id.smsLiked);
        smsNuatral = (RadioButton) findViewById(R.id.smsNuatral);
        smsNeedImprove = (RadioButton) findViewById(R.id.smsNeedImprove);


        MobileMostLiked = (RadioButton) findViewById(R.id.MobileMostLiked);
        MobileLiked = (RadioButton) findViewById(R.id.MobileLiked);
        MobileNeutral = (RadioButton) findViewById(R.id.MobileNeutral);
        MobileImprove = (RadioButton) findViewById(R.id.MobileImprove);


        ThroughAC = (RadioButton) findViewById(R.id.ThroughAC);
        ThroughEmail = (RadioButton) findViewById(R.id.ThroughEmail);
        ThroughSupport = (RadioButton) findViewById(R.id.ThroughSupport);
        others = (RadioButton) findViewById(R.id.others);


        Exellent = (RadioButton) findViewById(R.id.Exellent);
        VeryGood = (RadioButton) findViewById(R.id.VeryGood);
        Good = (RadioButton) findViewById(R.id.Good);
        NotGood = (RadioButton) findViewById(R.id.NotGood);


        Quality = (RadioButton) findViewById(R.id.Quality);
        Price = (RadioButton) findViewById(R.id.Price);
        FirstUse = (RadioButton) findViewById(R.id.FirstUse);
        Usability = (RadioButton) findViewById(R.id.Usability);
        CustomerService = (RadioButton) findViewById(R.id.CustomerService);


        Quality9 = (RadioButton) findViewById(R.id.Quality9);
        Price9 = (RadioButton) findViewById(R.id.Price9);
        FirstUse9 = (RadioButton) findViewById(R.id.FirstUse9);
        Usability9 = (RadioButton) findViewById(R.id.Usability9);
        Nill = (RadioButton) findViewById(R.id.Nill);
        CustomerService9 = (RadioButton) findViewById(R.id.CustomerService9);

        feedBackDetails();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (radioLessThanMonth.isChecked()) {
                    SubQustions obj = subQuestions.get(0);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radioMonths.isChecked()) {
                    SubQustions obj = subQuestions.get(1);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radioyears.isChecked()) {
                    SubQustions obj = subQuestions.get(2);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radiogreatethan3years.isChecked()) {
                    SubQustions obj = subQuestions.get(3);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (radioEveryDay.isChecked()) {
                    SubQustions obj = subQuestions.get(4);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (radioOnceweek.isChecked()) {
                    SubQustions obj = subQuestions.get(5);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (radio2or3times.isChecked()) {
                    SubQustions obj = subQuestions.get(6);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (radioOneceMonth.isChecked()) {
                    SubQustions obj = subQuestions.get(7);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (radioOnNeedBasis.isChecked()) {
                    SubQustions obj = subQuestions.get(8);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (radioVoiceMostLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(9);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radioVoiceLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(10);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radioVoiceNuatral.isChecked()) {
                    SubQustions obj = subQuestions.get(11);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (radioVoiceNeedImprove.isChecked()) {
                    SubQustions obj = subQuestions.get(12);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (smsMostLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(13);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (smsLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(14);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (smsNuatral.isChecked()) {
                    SubQustions obj = subQuestions.get(15);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (smsNeedImprove.isChecked()) {
                    SubQustions obj = subQuestions.get(16);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (MobileMostLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(17);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (MobileLiked.isChecked()) {
                    SubQustions obj = subQuestions.get(18);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (MobileNeutral.isChecked()) {
                    SubQustions obj = subQuestions.get(19);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (MobileImprove.isChecked()) {
                    SubQustions obj = subQuestions.get(20);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (ThroughAC.isChecked()) {
                    SubQustions obj = subQuestions.get(21);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (ThroughEmail.isChecked()) {
                    SubQustions obj = subQuestions.get(22);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (ThroughSupport.isChecked()) {
                    SubQustions obj = subQuestions.get(23);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (others.isChecked()) {
                    SubQustions obj = subQuestions.get(24);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (Exellent.isChecked()) {
                    SubQustions obj = subQuestions.get(25);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (VeryGood.isChecked()) {
                    SubQustions obj = subQuestions.get(26);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Good.isChecked()) {
                    SubQustions obj = subQuestions.get(27);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (NotGood.isChecked()) {
                    SubQustions obj = subQuestions.get(28);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (Quality.isChecked()) {
                    SubQustions obj = subQuestions.get(29);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Price.isChecked()) {
                    SubQustions obj = subQuestions.get(30);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (FirstUse.isChecked()) {
                    SubQustions obj = subQuestions.get(31);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Usability.isChecked()) {
                    SubQustions obj = subQuestions.get(32);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (CustomerService.isChecked()) {
                    SubQustions obj = subQuestions.get(33);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }


                if (Quality9.isChecked()) {
                    SubQustions obj = subQuestions.get(34);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Price9.isChecked()) {
                    SubQustions obj = subQuestions.get(35);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (FirstUse9.isChecked()) {
                    SubQustions obj = subQuestions.get(36);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Usability9.isChecked()) {
                    SubQustions obj = subQuestions.get(37);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (CustomerService9.isChecked()) {
                    SubQustions obj = subQuestions.get(38);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }
                if (Nill.isChecked()) {
                    SubQustions obj = subQuestions.get(39);
                    String questionid = obj.getMainQuestionID();
                    String answerid = obj.getId();
                    SubQustions data = new SubQustions(answerid, "", questionid, "");
                    Datas.add(data);
                }

                if (Datas.size() > 0) {
                    String id = "";
                    for (int i = 0; i < Datas.size(); i++) {

                        SubQustions val = Datas.get(i);
                        String ansid = val.getId();
                        String qstnid = val.getMainQuestionID();
                        values = id + qstnid + "~" + ansid + ",";
                        id = values;
                        Log.d("values", values);
                    }
                    String answers = values.substring(0, values.length() - 1);
                    Log.d("answers", answers);
                    insertFeedBackDetails(answers);

                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.select_feedback), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void insertFeedBackDetails(String answers) {
        {

            String baseURL=TeacherUtil_SharedPreference.getBaseUrl(FeedBackQuestionsScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            othercomment = othercomments.getText().toString();
            TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("SchoolID", schoolID);
            Log.d("schoolID", schoolID);
            jsonObject.addProperty("SchoolName", schoolname);
            Log.d("schoolname", schoolname);
            jsonObject.addProperty("ContactPerson", contactpersion);
            Log.d("contactpersion", contactpersion);
            jsonObject.addProperty("ContactMobile", mobilenumber);
            Log.d("mobilenumber", mobilenumber);
            jsonObject.addProperty("EmailID", emaiid);
            Log.d("emaiid", emaiid);
            jsonObject.addProperty("AnswerDetails", answers);
            Log.d("values", values);
            jsonObject.addProperty("OtherInfo", othercomment);


            jsonObject.addProperty("MemberID", StaffId);
            Log.d("othercomment", othercomment);

            Call<JsonArray> call = apiService.InsertFeedbackDetails(jsonObject);
            Log.d("insert", jsonObject.toString());
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                    try {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.d("login:code-res", response.code() + " - " + response.toString());
                        if (response.code() == 200 || response.code() == 201) {
                            Log.d("Response", response.body().toString());

                            JSONArray js = new JSONArray(response.body().toString());
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");
                                String Question = jsonObject.getString("Message");

                                if (!result.equals("0")) {
                                    ALert(Question);
                                } else {
                                    Toast.makeText(getApplicationContext(), Question, Toast.LENGTH_SHORT).show();
                                }

                            }
                        } else {

                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Log.e("Response Exception", e.getMessage());
                    }
                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.e("Response Failure", t.getMessage());
                    // showToast("Server Connection Failed");
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void ALert(String question) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FeedBackQuestionsScreen.this);

        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(question);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void feedBackDetails() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(FeedBackQuestionsScreen.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(FeedBackQuestionsScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(FeedBackQuestionsScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.GetFeedbackQuestions();

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);

                            String QuestionID = jsonObject.getString("QuestionID");
                            String Question = jsonObject.getString("Question");

                            if (i == 0) {
                                lblQuestion1.setText("1) " + Question);
                            } else if (i == 1) {
                                lblQuestion2.setText("2) " + Question);
                            } else if (i == 2) {
                                lblQuestion3.setText("3) " + Question);
                            } else if (i == 3) {
                                lblQuestion4.setText("4) " + Question);
                            } else if (i == 4) {
                                lblQuestion5.setText("5) " + Question);
                            } else if (i == 5) {
                                lblQuestion6.setText("6) " + Question);
                            } else if (i == 6) {
                                lblQuestion7.setText("7) " + Question);
                            } else if (i == 7) {
                                lblQuestion8.setText("8) " + Question);
                            } else if (i == 8) {
                                lblQuestion9.setText("9) " + Question);
                            }

                            QuestionDetails object = new QuestionDetails(QuestionID, Question);
                            questions.add(object);

                            JSONArray answerlist = jsonObject.getJSONArray("AnswerList");
                            Log.d("answerlist", String.valueOf(answerlist.length()));

                            for (int j = 0; j < answerlist.length(); j++) {
                                JSONObject c = answerlist.getJSONObject(j);
                                String AnswerID = c.getString("AnswerID");
                                String subQuestion = c.getString("Answer");


                                if (i == 0 && j == 0) {
                                    radioLessThanMonth.setText(subQuestion);
                                } else if (i == 0 && j == 1) {
                                    radioMonths.setText(subQuestion);
                                } else if (i == 0 && j == 2) {
                                    radioyears.setText(subQuestion);

                                } else if (i == 0 && j == 3) {
                                    radiogreatethan3years.setText(subQuestion);
                                } else if (i == 1 && j == 0) {
                                    radioEveryDay.setText(subQuestion);
                                } else if (i == 1 && j == 1) {
                                    radioOnceweek.setText(subQuestion);
                                } else if (i == 1 && j == 2) {
                                    radio2or3times.setText(subQuestion);
                                } else if (i == 1 && j == 3) {
                                    radioOneceMonth.setText(subQuestion);
                                } else if (i == 1 && j == 4) {
                                    radioOnNeedBasis.setText(subQuestion);
                                } else if (i == 2 && j == 0) {
                                    radioVoiceMostLiked.setText(subQuestion);
                                } else if (i == 2 && j == 1) {
                                    radioVoiceLiked.setText(subQuestion);
                                } else if (i == 2 && j == 2) {
                                    radioVoiceNuatral.setText(subQuestion);
                                } else if (i == 2 && j == 3) {
                                    radioVoiceNeedImprove.setText(subQuestion);
                                } else if (i == 3 && j == 0) {
                                    smsMostLiked.setText(subQuestion);
                                } else if (i == 3 && j == 1) {
                                    smsLiked.setText(subQuestion);
                                } else if (i == 3 && j == 2) {
                                    smsNuatral.setText(subQuestion);
                                } else if (i == 3 && j == 3) {
                                    smsNeedImprove.setText(subQuestion);
                                } else if (i == 4 && j == 0) {
                                    MobileMostLiked.setText(subQuestion);
                                } else if (i == 4 && j == 1) {
                                    MobileLiked.setText(subQuestion);
                                } else if (i == 4 && j == 2) {
                                    MobileNeutral.setText(subQuestion);
                                } else if (i == 4 && j == 3) {
                                    MobileImprove.setText(subQuestion);
                                } else if (i == 5 && j == 0) {
                                    ThroughAC.setText(subQuestion);
                                } else if (i == 5 && j == 1) {
                                    ThroughEmail.setText(subQuestion);
                                } else if (i == 5 && j == 2) {
                                    ThroughSupport.setText(subQuestion);
                                } else if (i == 5 && j == 3) {
                                    others.setText(subQuestion);
                                } else if (i == 6 && j == 0) {
                                    Exellent.setText(subQuestion);
                                } else if (i == 6 && j == 1) {
                                    VeryGood.setText(subQuestion);
                                } else if (i == 6 && j == 2) {
                                    Good.setText(subQuestion);
                                } else if (i == 6 && j == 3) {
                                    NotGood.setText(subQuestion);
                                } else if (i == 7 && j == 0) {
                                    Quality.setText(subQuestion);
                                } else if (i == 7 && j == 1) {
                                    Price.setText(subQuestion);
                                } else if (i == 7 && j == 2) {
                                    FirstUse.setText(subQuestion);
                                } else if (i == 7 && j == 3) {
                                    Usability.setText(subQuestion);
                                } else if (i == 7 && j == 4) {
                                    CustomerService.setText(subQuestion);
                                } else if (i == 8 && j == 0) {
                                    Quality9.setText(subQuestion);
                                } else if (i == 8 && j == 1) {
                                    Price9.setText(subQuestion);
                                } else if (i == 8 && j == 2) {
                                    FirstUse9.setText(subQuestion);
                                } else if (i == 8 && j == 3) {
                                    Usability9.setText(subQuestion);
                                } else if (i == 8 && j == 4) {
                                    CustomerService9.setText(subQuestion);
                                } else if (i == 8 && j == 5) {
                                    Nill.setText(subQuestion);
                                }

                                SubQustions sub = new SubQustions(AnswerID, subQuestion, QuestionID, Question);
                                subQuestions.add(sub);
                            }
                        }


                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }


}

