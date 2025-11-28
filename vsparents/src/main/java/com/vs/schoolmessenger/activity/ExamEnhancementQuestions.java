package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_QUIZ;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.JsonObject;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.QuestionForQuizAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.QuestionClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamEnhancement;
import com.vs.schoolmessenger.model.QuestionForQuiz;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.MyWebViewClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class ExamEnhancementQuestions extends AppCompatActivity implements QuestionClickListener, View.OnClickListener {
    public QuestionForQuizAdapter mAdapter;
    TextView lblStarttime, lblendtime, lblduration, lblmin, lblqueno, lblque, lblPDF, lblprev, lblnext, lblqueduration;
    RadioGroup rgoptions;
    RecyclerView rcyquestions;
    FrameLayout videoview;
    ConstraintLayout constraint;
    LinearLayout containerOptions;
    LinearLayout lnrPDFtext;
    ImageView imgview, imgVideo, imgplay, imgShadow, imgprev, imgnext;
    Button btnnext, btnprevious, btnsubmit;
    long recTime, recquetime, curdifferenceque = 0;
    Handler recTimerHandler = new Handler();
    Handler recqueTimerHandler = new Handler();
    int iMaxRecDur;
    String child_id, ansid, answer;
    int adapterpos = 0, nextpos = 0, previouspos = 0;
    QuestionForQuiz msgModel;
    ExamEnhancement info;
    ArrayList<String> answerlist = new ArrayList<>();
    ArrayList<String> questionlist = new ArrayList<>();
    RelativeLayout ryttime;
    RadioButton radioButton;
    String currenttime24, pdfuri, videourl, imageurl, quetime;
    PopupWindow popupWindow, popupimage, popuppdfWindow;
    ImageView imgQuestion;
    WebView webview;
    private final ArrayList<QuestionForQuiz> msgModelList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }
    private final Runnable querunson = new Runnable() {
        @Override
        public void run() {
            lblqueduration.setText("Question Reading Time : " + milliSecondsToTimer(recquetime * 1000));
            String timing = lblqueduration.getText().toString();
            if (lblqueduration.getText().toString().equals("Question Reading Time : 00:00") || lblqueduration.getText().toString().equals("Question Reading Time : 0:00")) {
                lblqueduration.setVisibility(View.GONE);
                lblduration.setVisibility(View.VISIBLE);
            }
            recquetime = recquetime - 1;
            if (recquetime != iMaxRecDur)
                recqueTimerHandler.postDelayed(this, 1000);
            else
                lblqueduration.setText("Question Reading Time : " + milliSecondsToTimer(recquetime * 1000));
            if (lblqueduration.getText().toString().equals("Question Reading Time : 00:00") || lblqueduration.getText().toString().equals("Question Reading Time : 0:00")) {
                lblqueduration.setVisibility(View.GONE);
                lblduration.setVisibility(View.VISIBLE);
            }
        }
    };
    private final Runnable runson = new Runnable() {
        @Override
        public void run() {
            lblduration.setText("Exam Time:" + milliSecondsToTimer(recTime * 1000));
            if (lblduration.getText().toString().equals("Exam Time:00:00") || lblduration.getText().toString().equals("Exam Time:0:00")) {
                if (answerlist.size() != 0) {
                    if (answerlist.size() == msgModelList.size()) {
                        submitquiz();
                    } else if (questionlist.size() != msgModelList.size()) {

                        for (int i = 0; i < msgModelList.size(); i++) {
                            if (!questionlist.contains(String.valueOf(msgModelList.get(i).getQestionId()))) {
                                String emptyque = msgModelList.get(i).getQestionId() + "~" + 0;
                                answerlist.add(emptyque);
                            }
                        }
                        if (answerlist.size() == msgModelList.size()) {
                            submitquiz();
                        }
                    }
                } else {
                    showAlertfinish(getResources().getString(R.string.Can_able_submit));
                }
            }
            if (curdifferenceque <= 0) {
                Date currentTime = Calendar.getInstance().getTime();
                Log.d("currenttime", currentTime.toString());
                String date = currentTime.toString();
                String[] parts = date.split(" ");

                System.out.println("Time: " + parts[3]);
                String currenttime24add = parts[3];

                // ***********compare with current time and start time

                String _24HourTime11 = info.getTimeForQuestionReading() + ":00";
                Log.d("starttime", _24HourTime11);
                Date curStart = null;
                Date curEnd = null;
                SimpleDateFormat cursimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                try {
                    curStart = cursimpleDateFormat.parse(_24HourTime11);
                    curEnd = cursimpleDateFormat.parse(currenttime24add);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                curdifferenceque = curEnd.getTime() - curStart.getTime();
                Log.d("curdiffhandler", String.valueOf(curdifferenceque));
                if (curdifferenceque > 0) {
                    lblmin.setText("Students are now allowed to answer");
                    lblmin.setTextColor(getResources().getColor(R.color.clr_green));
                    lblduration.setVisibility(View.VISIBLE);
                    lblqueduration.setVisibility(View.GONE);
                    mAdapter = new QuestionForQuizAdapter(msgModelList, ExamEnhancementQuestions.this, adapterpos, ExamEnhancementQuestions.this);

                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());
                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }

            if (lblduration.getText().toString().equals("Exam Time:01:00") || lblduration.getText().toString().equals("Exam Time:1:00")) {
                showAlert(getResources().getString(R.string.Please_submit_your_exam));
            }

            recTime = recTime - 1;
            if (recTime != iMaxRecDur) {
                recTimerHandler.postDelayed(this, 1000);
            }


            lblduration.setText("Exam Time:" + milliSecondsToTimer(recTime * 1000));
            if (lblduration.getText().toString().equals("Exam Time:00:00") || lblduration.getText().toString().equals("Exam Time:0:00")) {
                if (answerlist.size() != 0) {
                    if (answerlist.size() == msgModelList.size()) {
                        submitquiz();
                    } else if (questionlist.size() != msgModelList.size()) {

                        for (int i = 0; i < msgModelList.size(); i++) {
                            if (!questionlist.contains(String.valueOf(msgModelList.get(i).getQestionId()))) {
                                String emptyque = msgModelList.get(i).getQestionId() + "~" + 0;
                                answerlist.add(emptyque);
                            }
                        }
                        if (answerlist.size() == msgModelList.size()) {
                            submitquiz();
                        }
                    }


                } else {
                    showAlertfinish(getResources().getString(R.string.Can_able_submit));
                }
            }

        }
    };

    public static String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        String minutesString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to Minutes if it is one digit
        if (minutes < 10) {
            minutesString = "0" + minutes;
        } else {
            minutesString = String.valueOf(minutes);
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = String.valueOf(seconds);
        }

        finalTimerString = finalTimerString + minutesString + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_exam_enhancement_questions);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Exam));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        btnnext = findViewById(R.id.btnnext);
        btnprevious = findViewById(R.id.btnprevious);
        btnsubmit = findViewById(R.id.btnsubmit);
        imgview = findViewById(R.id.imgview);
        imgVideo = findViewById(R.id.imgVideo);
        imgShadow = findViewById(R.id.imgShadow);
        imgplay = findViewById(R.id.imgplay);
        videoview = findViewById(R.id.videoview);
        rcyquestions = findViewById(R.id.rcyquestions);
        containerOptions = findViewById(R.id.containerOptions);
//        rgoptions = findViewById(R.id.rgoptions);
        lblque = findViewById(R.id.lblque);
        lblqueno = findViewById(R.id.lblqueno);
        lblmin = findViewById(R.id.lblmin);
        lblduration = findViewById(R.id.lblduration);
        lblqueduration = findViewById(R.id.lblqueduration);
        lblendtime = findViewById(R.id.lblendtime);
        lblStarttime = findViewById(R.id.lblStarttime);
        ryttime = findViewById(R.id.ryttime);
        lnrPDFtext = findViewById(R.id.lnrPDFtext);
        lblPDF = findViewById(R.id.lblPDF);
        lblnext = findViewById(R.id.lblnext);
        lblprev = findViewById(R.id.lblprev);
        imgprev = findViewById(R.id.imgprev);
        imgnext = findViewById(R.id.imgnext);
        constraint = findViewById(R.id.constraint);

        constraint.setVisibility(View.GONE);
        ryttime.setVisibility(View.VISIBLE);
        lblmin.setVisibility(View.VISIBLE);

        webview = findViewById(R.id.webview);
        imgQuestion = findViewById(R.id.imgQuestion);


        child_id = Util_SharedPreference.getChildIdFromSP(this);
        info = (ExamEnhancement) getIntent().getSerializableExtra("Exam");
        quetime = getIntent().getStringExtra("quetime");


        if (quetime.equals("")) {
            lblmin.setVisibility(View.GONE);
            lblqueduration.setVisibility(View.GONE);
            lblduration.setVisibility(View.VISIBLE);
        } else {
            lblqueduration.setVisibility(View.VISIBLE);
            lblduration.setVisibility(View.GONE);
            lblmin.setText("Time for Question Reading " + quetime + " . Students are not allowed to answer upto " + quetime);
        }
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ExamEnhancementQuestions.this);
        if (info.getIsAppRead().equals("0")) {
            ChangeMsgReadStatus.changeReadStatus(ExamEnhancementQuestions.this, String.valueOf(info.getDetailId()), MSG_TYPE_QUIZ, "", isNewVersion, false, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {

                }
            });

        }


        if (adapterpos == msgModelList.size() && msgModelList.size() != 0) {
            imgnext.setImageResource(R.drawable.bg_next_disable);
            lblnext.setTextColor(getResources().getColor(R.color.clr_grey_school));
        } else {
            imgnext.setImageResource(R.drawable.bg_next_enable);
            lblnext.setTextColor(getResources().getColor(R.color.clr_black));
        }
        if (adapterpos == 0) {
            imgprev.setImageResource(R.drawable.bg_prev_disable);
            lblprev.setTextColor(getResources().getColor(R.color.clr_grey_school));
        } else {
            btnprevious.setEnabled(true);
            imgprev.setImageResource(R.drawable.bg_prev_enable);
            lblprev.setTextColor(getResources().getColor(R.color.clr_black));
        }
        btnsubmit.setEnabled(answerlist.size() != 0);
        imgnext.setOnClickListener(this);
        imgprev.setOnClickListener(this);
        btnnext.setOnClickListener(this);
        btnprevious.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);
        lnrPDFtext.setOnClickListener(this);
        videoview.setOnClickListener(this);
        imgview.setOnClickListener(this);
        imgVideo.setOnClickListener(this);
        imgShadow.setOnClickListener(this);
        imgplay.setOnClickListener(this);


        Log.d("adapterpos", String.valueOf(adapterpos));
        if (adapterpos == 0) {
            mAdapter = new QuestionForQuizAdapter(msgModelList, this, 0, this);

            rcyquestions.setHasFixedSize(true);
            rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
            rcyquestions.setItemAnimator(new DefaultItemAnimator());
            rcyquestions.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
        // *********** current system timing

        Date currentTime = Calendar.getInstance().getTime();
        Log.d("currenttime", currentTime.toString());
        String date = currentTime.toString();
        String[] parts = date.split(" ");

        System.out.println("Time: " + parts[3]);
        currenttime24 = parts[3];


        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = df.format(currentTime);
        Log.d("currentdate", formattedDate);
        // ***********compare with current time and start time

        String _24HourTime11 = info.getExamStartTime() + ":00";
        Log.d("starttime", _24HourTime11);
        Date curStart = null;
        Date curEnd = null;
        SimpleDateFormat cursimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        try {
            curStart = cursimpleDateFormat.parse(_24HourTime11);
            curEnd = cursimpleDateFormat.parse(currenttime24);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long curdifference = curEnd.getTime() - curStart.getTime();

        //End Time

        String _24HourTime11end = info.getExamEndTime() + ":00";
        Log.d("starttime", _24HourTime11end);
        Date curStartend = null;
        Date curEndend = null;
        SimpleDateFormat cursimpleDateFormatend = new SimpleDateFormat("HH:mm:ss");
        try {
            curStartend = cursimpleDateFormatend.parse(currenttime24);
            curEndend = cursimpleDateFormatend.parse(_24HourTime11end);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long curdifferenceend = curEndend.getTime() - curStartend.getTime();
        if (!formattedDate.equals(info.ExamDate)) {
            showAlertfinish(getResources().getString(R.string.This_Exam_Not_Allocated));
        } else if (curdifference < 0) {
            showAlertfinish(getResources().getString(R.string.Please_wait_Exam_Not_Started));
        } else if (curdifferenceend < 0) {
            showAlertfinish(getResources().getString(R.string.Exam_no_longer_exist));
        } else {
            constraint.setVisibility(View.VISIBLE);
            examEnhancement();
            // *********** 24 hrs to 12 hrs conversion for textview display
            try {
                String _24HourTime = info.getExamStartTime();
                String _24HourTime1 = info.getExamEndTime();
                SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                Date _24HourDt = _24HourSDF.parse(_24HourTime);
                Date _24HourDt1 = _24HourSDF.parse(_24HourTime1);
                System.out.println(_24HourDt);
                System.out.println(_12HourSDF.format(_24HourDt));
                System.out.println(_12HourSDF.format(_24HourDt1));

                lblStarttime.setText(_12HourSDF.format(_24HourDt));
                lblendtime.setText(_12HourSDF.format(_24HourDt1));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String _24HourTime12 = info.getExamEndTime() + ":00";
            String _24HourTimeque = info.getTimeForQuestionReading() + ":00";
            Date Start = null;
            Date End = null;
            Date Quetime = null;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                Start = simpleDateFormat.parse(currenttime24);
                End = simpleDateFormat.parse(_24HourTime12);
                Quetime = simpleDateFormat.parse(_24HourTimeque);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            long Endseconds = End.getTime() / 1000L;
            long startseconds = Start.getTime() / 1000L;
            long quereadseconds = Quetime.getTime() / 1000L;
            long totalsec = Endseconds - startseconds;
            long quesec = quereadseconds - startseconds;

            Log.d("totalsec", String.valueOf(totalsec));
            iMaxRecDur = 0;
            recTime = totalsec;
            recquetime = quesec;
            lblqueduration.setText("Question Reading Time : " + milliSecondsToTimer(recquetime * 1000));
            lblduration.setText("Exam Time:" + milliSecondsToTimer(recTime * 1000));
            if (lblduration.getText().toString().equals("Exam Time:00:00") || lblduration.getText().toString().equals("Exam Time:0:00")) {
                if (answerlist.size() != 0) {

                    if (answerlist.size() == msgModelList.size()) {
                        submitquiz();
                    } else if (questionlist.size() != msgModelList.size()) {

                        for (int i = 0; i < msgModelList.size(); i++) {
                            if (!questionlist.contains(String.valueOf(msgModelList.get(i).getQestionId()))) {
                                String emptyque = msgModelList.get(i).getQestionId() + "~" + 0;
                                answerlist.add(emptyque);
                            }
                        }
                        if (answerlist.size() == msgModelList.size()) {
                            submitquiz();
                        }
                    }

                } else {
                    showAlertfinish(getResources().getString(R.string.Can_able_submit));
                }
            }
            if (lblqueduration.getText().toString().equals("Question Reading Time : 00:00") || lblqueduration.getText().toString().equals("Question Reading Time : 0:00")) {
                lblqueduration.setVisibility(View.GONE);
                lblduration.setVisibility(View.VISIBLE);
            }
            recTimerHandler.postDelayed(runson, 1000);
            recqueTimerHandler.postDelayed(querunson, 1000);

        }

        Log.d("curdiff", String.valueOf(curdifference));


    }

    @Override
    protected void onResume() {
        super.onResume();
        Date currentTime = Calendar.getInstance().getTime();
        Log.d("currenttime0nres", currentTime.toString());
        String date = currentTime.toString();
        String[] parts = date.split(" ");

        System.out.println("Time: " + parts[3]);
        currenttime24 = parts[3];

    }

    @Override
    protected void onPause() {
        super.onPause();
        Date currentTime = Calendar.getInstance().getTime();
        Log.d("currenttime0nres", currentTime.toString());
        String date = currentTime.toString();
        String[] parts = date.split(" ");

        System.out.println("Time: " + parts[3]);
        currenttime24 = parts[3];
    }

    private void examEnhancement() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("QuizId", info.getQuizId());
        jsonObject.addProperty("StudentID", child_id);


        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.questionforquiz(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            int status = jsonObject.getInt("Status");
                            String message = jsonObject.getString("Message");

                            if (status == 1) {
                                msgModelList.clear();
                                JSONArray data = jsonObject.getJSONArray("data");


                                for (int i = 0; i < data.length(); i++) {
                                    jsonObject = data.getJSONObject(i);

                                    QuestionForQuiz msgModel = new QuestionForQuiz(
                                            jsonObject.getInt("QestionId"),
                                            jsonObject.getString("Question"),
                                            jsonObject.getString("VideoUrl"),
                                            jsonObject.getString("FileType"),
                                            jsonObject.getString("FileUrl"),
                                            jsonObject.getInt("mark"),
                                            String.valueOf(i + 1),
                                            jsonObject.getJSONArray("Answer"),
                                            jsonObject.optString("qImage", ""),
                                            jsonObject.optString("aImage", ""),
                                            jsonObject.optString("bImage", ""),
                                            jsonObject.optString("cImage", ""),
                                            jsonObject.optString("dImage", "")
                                    );


//                                    msgModel = new QuestionForQuiz(jsonObject.getInt("QestionId"),
//                                            jsonObject.getString("Question"),
//                                            jsonObject.getString("VideoUrl"),
//                                            jsonObject.getString("FileType"),
//                                            jsonObject.getString("FileUrl"),
//                                            jsonObject.getInt("mark"),
//                                            String.valueOf(i + 1),
//                                            jsonObject.getJSONArray("Answer")
//                                    );

                                    msgModelList.add(msgModel);

                                }

                                mAdapter.notifyDataSetChanged();

                            } else {

                                showAlertfinish(message);
                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(ExamEnhancementQuestions.this, "Server Response Failed", Toast.LENGTH_SHORT).show();
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
                // showToast("Server Connection Failed");
                Toast.makeText(ExamEnhancementQuestions.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void submitquiz() {

        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(this);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("QuizId", info.getQuizId());
        jsonObject.addProperty("StudentID", child_id);
        jsonObject.addProperty("Answer", String.valueOf(answerlist));

        Log.d("jsonObject", jsonObject.toString());

        Call<JsonObject> call = apiService.submitquiz(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            int strStatus = jsonObject.getInt("Status");
                            String strMsg = jsonObject.getString("Message");

                            if (strStatus == 1) {
                                showAlertfinish(strMsg);
                            } else {
                                showAlertfinish(strMsg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(ExamEnhancementQuestions.this, "Server Response Failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ExamEnhancementQuestions.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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

    private void showAlertfinish(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
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
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showAlertApi(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                submitquiz();
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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

    private void openPreview(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;

        Intent intent = new Intent(this, PreviewActivity.class);
        intent.putExtra("fileUrl", fileUrl);
        startActivity(intent);
    }

    @Override
    public void addclass(final QuestionForQuiz menu, int position) {
        adapterpos = position;

        containerOptions.removeAllViews();
        videoview.setVisibility(View.GONE);
        imgview.setVisibility(View.GONE);
        lnrPDFtext.setVisibility(View.GONE);

        if (menu.getqImage().isEmpty()) {
            imgQuestion.setVisibility(View.GONE);
            webview.setVisibility(View.GONE);
        } else {
            String imageUrl = menu.getqImage() != null ? menu.getqImage().toLowerCase() : "";

            if (imageUrl.endsWith(".jpg") ||
                    imageUrl.endsWith(".jpeg") ||
                    imageUrl.endsWith(".png") ||
                    imageUrl.endsWith(".gif") ||
                    imageUrl.endsWith(".bmp") ||
                    imageUrl.endsWith(".webp") ||
                    imageUrl.endsWith(".tiff")) {
                imgQuestion.setVisibility(View.VISIBLE);
                webview.setVisibility(View.GONE);
                Glide.with(this)
                        .load(menu.getqImage())
                        .into(imgQuestion);
            } else {
                webview.setVisibility(View.VISIBLE);
                imgQuestion.setVisibility(View.GONE);
                WebSettings webSettings = webview.getSettings();
                webSettings.setJavaScriptEnabled(true);
                webSettings.setDomStorageEnabled(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                webview.loadUrl(menu.getqImage());
            }
        }

        imgQuestion.setOnClickListener(v -> openPreview(menu.getqImage()));
        webview.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                openPreview(menu.getqImage());
            }
            return false;
        });



        for (int i = 0; i < menu.getAnswer().length(); i++) {
            String ansid = "";
            String answer = "";

            try {
                String[] id = menu.getAnswer().get(i).toString().split("~");
                ansid = id[0];
                if (id.length > 1) {
                    answer = id[1];
                } else {
                    answer = ""; // or some default value
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            View optionView = LayoutInflater.from(this).inflate(R.layout.item_quiz_option, containerOptions, false);

            LinearLayout itemContainer = optionView.findViewById(R.id.itemContainer);
            TextView txtOption = optionView.findViewById(R.id.txtOption);
            TextView txtContent = optionView.findViewById(R.id.txtContent);
            ImageView imgCheck = optionView.findViewById(R.id.imgCheck);
            ImageView imgAttachment = optionView.findViewById(R.id.imgAttachment);
            TextView txtAttachmentName = optionView.findViewById(R.id.txtAttachmentName);
            ConstraintLayout rlyCard = optionView.findViewById(R.id.rlyCard);
            LinearLayout lnrAttachment = optionView.findViewById(R.id.lnrAttachment);

            char optionChar = (char) ('A' + i);
            txtOption.setText(String.valueOf(optionChar));
            txtContent.setText(answer);

            String imageUrl = "";
            String caption = "";
            switch (i) {
                case 0:
                    imageUrl = menu.getaImage();
                    caption = "Graph 1 (A)";
                    break;
                case 1:
                    imageUrl = menu.getbImage();
                    caption = "Graph 2 (B)";
                    break;
                case 2:
                    imageUrl = menu.getcImage();
                    caption = "Graph 3 (C)";
                    break;
                case 3:
                    imageUrl = menu.getdImage();
                    caption = "Graph 4 (D)";
                    break;
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                lnrAttachment.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(imageUrl)
                        .into(imgAttachment);
                txtAttachmentName.setText(caption);
                String finalImageUrl = imageUrl;
                imgAttachment.setOnClickListener(v -> openPreview(finalImageUrl));

            } else {
                lnrAttachment.setVisibility(View.GONE);
            }

            boolean isSelected = false;
            if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
                int index = questionlist.indexOf(String.valueOf(menu.getQestionId()));
                String saved = answerlist.get(index);
                String[] split = saved.split("~");
                if (split.length > 1 && split[1].equals(ansid)) {
                    isSelected = true;
                }
            }

            if (isSelected) {
                rlyCard.setBackgroundResource(R.drawable.bg_shadow_violet_card);
                imgCheck.setVisibility(View.VISIBLE);
            } else {
                rlyCard.setBackgroundResource(R.drawable.bg_shadow_white_card);
                imgCheck.setVisibility(View.GONE);
            }

            final String finalAnsid = ansid;
            itemContainer.setOnClickListener(v -> {

                for (int j = 0; j < containerOptions.getChildCount(); j++) {
                    View child = containerOptions.getChildAt(j);

                    ConstraintLayout optionLayout = child.findViewById(R.id.rlyCard);
                    ImageView optionCheck = child.findViewById(R.id.imgCheck);

                    if (optionLayout != null) {
                        optionLayout.setBackgroundResource(R.drawable.bg_shadow_white_card);
                    }
                    if (optionCheck != null) {
                        optionCheck.setVisibility(View.GONE);
                    }
                }
                View clickedView = v;

                ConstraintLayout selectedLayout = clickedView.findViewById(R.id.rlyCard);
                ImageView selectedCheck = clickedView.findViewById(R.id.imgCheck);

                if (selectedLayout != null) {
                    selectedLayout.setBackgroundResource(R.drawable.bg_shadow_violet_card);
                }
                if (selectedCheck != null) {
                    selectedCheck.setVisibility(View.VISIBLE);
                }

                String idcom = menu.getQestionId() + "~" + finalAnsid;
                if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
                    int replacepos = questionlist.indexOf(String.valueOf(menu.getQestionId()));
                    answerlist.set(replacepos, idcom);
                } else {
                    questionlist.add(String.valueOf(menu.getQestionId()));
                    answerlist.add(idcom);
                }

                btnsubmit.setEnabled(!answerlist.isEmpty());
            });
            containerOptions.addView(optionView);
        }
        btnsubmit.setEnabled(answerlist.size() != 0);
        if (adapterpos == msgModelList.size() - 1 && msgModelList.size() != 0) {
            imgnext.setImageResource(R.drawable.bg_next_disable);
            lblnext.setTextColor(getResources().getColor(R.color.clr_grey_school));
        } else {
            imgnext.setImageResource(R.drawable.bg_next_enable);
            lblnext.setTextColor(getResources().getColor(R.color.clr_black));
        }

        if (adapterpos == 0) {
            imgprev.setImageResource(R.drawable.bg_prev_disable);
            lblprev.setTextColor(getResources().getColor(R.color.clr_grey_school));
        } else {
            imgprev.setImageResource(R.drawable.bg_prev_enable);
            lblprev.setTextColor(getResources().getColor(R.color.clr_black));
        }
        int pos = position + 1;
        String queno = "Q ." + pos;
        lblqueno.setText(queno);
        lblque.setText(menu.getQuestion());
        if (!menu.getVideoUrl().equals("")) {
            videoview.setVisibility(View.VISIBLE);
            videourl = menu.getVideoUrl();
        } else {
            videoview.setVisibility(View.GONE);
        }

        if (menu.getFileType().equals("IMAGE") && !menu.getFileUrl().equals("")) {
            imgview.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(menu.getFileUrl())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgview);
            imageurl = menu.getFileUrl();
        } else {
            imgview.setVisibility(View.GONE);
        }

        if (menu.getFileType().equals("PDF") && !menu.getFileUrl().equals("")) {
            lnrPDFtext.setVisibility(View.VISIBLE);
            lblPDF.setText(menu.getFileUrl());
            pdfuri = menu.getFileUrl();
        } else {
            lnrPDFtext.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // Go back inside webview history if possible
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }


//    @Override
//    public void addclass(final QuestionForQuiz menu, final int position) {
//        adapterpos = position;
//        rgoptions.removeAllViews();
//        rgoptions.clearCheck();
//        videoview.setVisibility(View.GONE);
//        imgview.setVisibility(View.GONE);
//        lnrPDFtext.setVisibility(View.GONE);
//        for (int i = 0; i < menu.getAnswer().length(); i++) {
//            String[] id = new String[0];
//            try {
//                id = menu.getAnswer().get(i).toString().split("~");
//                ansid = id[0];
//                Log.d("ansid", ansid);
//                answer = id[1];
//                Log.d("answer", answer);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            radioButton = new RadioButton(getApplicationContext());
//
//            radioButton.setText(answer);
//            radioButton.setId(Integer.parseInt(ansid));
//            radioButton.setTextSize(16);
//            radioButton.setTextColor(Color.BLACK);
//            radioButton.setPadding(1, 10, 1, 1);
//
//
//            Date currentTime = Calendar.getInstance().getTime();
//            Log.d("currenttime", currentTime.toString());
//            String date = currentTime.toString();
//            String[] parts = date.split(" ");
//
//            System.out.println("Time: " + parts[3]);
//            String currenttime24add = parts[3];
//
//
//            String _24HourTime11 = info.getTimeForQuestionReading() + ":00";
//            Log.d("starttime", _24HourTime11);
//            Date curStart = null;
//            Date curEnd = null;
//            SimpleDateFormat cursimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            try {
//                curStart = cursimpleDateFormat.parse(_24HourTime11);
//                curEnd = cursimpleDateFormat.parse(currenttime24add);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//            curdifferenceque = curEnd.getTime() - curStart.getTime();
//            Log.d("curdiffhandler", String.valueOf(curdifferenceque));
//            if (curdifferenceque <= 0) {
//                lblqueduration.setVisibility(View.VISIBLE);
//                lblduration.setVisibility(View.GONE);
//                rgoptions.setEnabled(false);
//                radioButton.setEnabled(false);
//                btnsubmit.setEnabled(false);
//            } else {
//                lblmin.setText("Students are now allowed to answer");
//                lblmin.setTextColor(getResources().getColor(R.color.clr_green));
//                lblduration.setVisibility(View.VISIBLE);
//                lblqueduration.setVisibility(View.GONE);
//                rgoptions.setEnabled(true);
//                radioButton.setEnabled(true);
//            }
//            rgoptions.addView(radioButton);
//            rgoptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    final int selectedPosition = rgoptions.getCheckedRadioButtonId();
//                    if (selectedPosition != -1) {
//                        String idcom = menu.getQestionId() + "~" + selectedPosition;
//                        String ansidref, answerref;
//                        if (questionlist.size() == 0) {
//                            questionlist.add(String.valueOf(menu.getQestionId()));
//                            if (answerlist.size() == 0) {
//                                answerlist.add(idcom);
//                                Log.d("answerlist", answerlist.get(0));
//                            }
//                        } else if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
//                            int replacequepos = questionlist.indexOf(String.valueOf(menu.getQestionId()));
//                            Log.d("replacepos", String.valueOf(replacequepos));
//                            questionlist.set(replacequepos, String.valueOf(menu.getQestionId()));
//                            for (int i = 0; i < menu.getAnswer().length(); i++) {
//                                String[] idref = new String[0];
//                                try {
//                                    idref = menu.getAnswer().get(i).toString().split("~");
//                                    ansidref = idref[0];
//                                    Log.d("ansid", ansid);
//                                    answerref = idref[1];
//                                    Log.d("answer", answer);
//
//
//                                    if (answerlist.contains(menu.getQestionId() + "~" + ansidref)) {
//                                        Log.d("changeexist", "changeexist");
//                                        int replacepos = answerlist.indexOf(menu.getQestionId() + "~" + ansidref);
//                                        Log.d("replacepos", String.valueOf(replacepos));
//                                        answerlist.set(replacepos, idcom);
//                                    }
//
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        } else {
//                            questionlist.add(String.valueOf(menu.getQestionId()));
//                            answerlist.add(idcom);
//                        }
//                        btnsubmit.setEnabled(answerlist.size() != 0);
//                        JsonObject jsonObject = new JsonObject();
//                        jsonObject.addProperty("QuizId", info.getQuizId());
//                        jsonObject.addProperty("StudentID", child_id);
//                        jsonObject.addProperty("Answer", String.valueOf(answerlist));
//
//                        Log.d("jsonObject", jsonObject.toString());
//                        Log.d("answersize", String.valueOf(answerlist.size()));
//                    }
//                }
//            });
//
//            if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
//                int replacequepos = questionlist.indexOf(String.valueOf(menu.getQestionId()));
//                if (questionlist.get(replacequepos).equals(String.valueOf(menu.getQestionId()))) {
//                    String answersetupl = answerlist.get(replacequepos);
//
//                    String[] idsetup = new String[0];
//                    idsetup = answersetupl.split("~");
//                    int ansidsetup = Integer.parseInt(idsetup[0]);
//                    Log.d("ansidsetup", String.valueOf(ansidsetup));
//                    int answersetup = Integer.parseInt(idsetup[1]);
//                    Log.d("answersetup", String.valueOf(answersetup));
//                    if (radioButton.getId() == answersetup) {
//                        radioButton.setChecked(true);
//                    }
//
//                }
//
//
//            }
//
//        }
//
//
//        btnsubmit.setEnabled(answerlist.size() != 0);
//        if (adapterpos == msgModelList.size() - 1 && msgModelList.size() != 0) {
//            imgnext.setImageResource(R.drawable.bg_next_disable);
//            lblnext.setTextColor(getResources().getColor(R.color.clr_grey_school));
//        } else {
//            imgnext.setImageResource(R.drawable.bg_next_enable);
//            lblnext.setTextColor(getResources().getColor(R.color.clr_black));
//        }
//        if (adapterpos == 0) {
//            imgprev.setImageResource(R.drawable.bg_prev_disable);
//            lblprev.setTextColor(getResources().getColor(R.color.clr_grey_school));
//        } else {
//            imgprev.setImageResource(R.drawable.bg_prev_enable);
//            lblprev.setTextColor(getResources().getColor(R.color.clr_black));
//        }
//
//
//        int pos = position + 1;
//        String queno = "Q ." + pos;
//        lblqueno.setText(queno);
//        lblque.setText(menu.getQuestion());
//        videoview.setVisibility(View.GONE);
//        imgview.setVisibility(View.GONE);
//        lnrPDFtext.setVisibility(View.GONE);
//        if (!menu.getVideoUrl().equals("")) {
//            videoview.setVisibility(View.VISIBLE);
//            videourl = menu.getVideoUrl();
//        } else {
//            videoview.setVisibility(View.GONE);
//        }
//        if (menu.getFileType().equals("IMAGE") && !menu.getFileUrl().equals("")) {
//
//            imgview.setVisibility(View.VISIBLE);
//            Glide.with(this)
//                    .load(menu.getFileUrl())
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .into(imgview);
//            imageurl = menu.getFileUrl();
//
//
//        } else {
//            imgview.setVisibility(View.GONE);
//        }
//        if (menu.getFileType().equals("PDF") && !menu.getFileUrl().equals("")) {
//            lnrPDFtext.setVisibility(View.VISIBLE);
//            lblPDF.setText(menu.getFileUrl());
//            pdfuri = menu.getFileUrl();
//        } else {
//
//            lnrPDFtext.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void removeclass(QuestionForQuiz menu) {
    }

    private void showvideopopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.vimeo_player, null);
        popupWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);

        RelativeLayout image_rlToolbar = (RelativeLayout) layout.findViewById(R.id.image_rlToolbar);
        ImageView imgBack = (ImageView) layout.findViewById(R.id.imgBack);
        WebView myWebView = (WebView) layout.findViewById(R.id.myWebView);

        image_rlToolbar.setBackgroundColor(getResources().getColor(R.color.clr_black));
        imgBack.setImageResource(R.drawable.ic_close);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        myWebView.setBackgroundColor(getResources().getColor(R.color.clr_black));
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.getSettings().setDomStorageEnabled(true);
        myWebView.getSettings().setSupportZoom(false);
        myWebView.getSettings().setBuiltInZoomControls(false);
        myWebView.getSettings().setDisplayZoomControls(false);
        myWebView.setScrollContainer(false);
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.setVerticalScrollBarEnabled(false);

        myWebView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {

                return true;
            }
        });
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                setProgress(progress * 100);
                if (progress == 100) {
                }
            }
        });
        myWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        String VIDEO_URL = "https://player.vimeo.com/video/" + "425233784";
        String data_html = "<!DOCTYPE html><html> " +
                "<head>" +
                " <meta charset=\"UTF-8\">" +

                "</head>" +
                " <body style=\"background:black;margin:0 0 0 0; padding:0 0 0 0;\"> " +
                "<div class=\"vimeo\">" +

                "<iframe  style=\"position:absolute;top:0;bottom:0;width:100%;height:100%\" src=\"" + videourl + "\" frameborder=\"0\">" +
                "</iframe>" +
                " </div>" +
                " </body>" +
                " </html> ";

        myWebView.loadData(data_html, "text/html", "UTF-8");
    }

    private void showpdfpopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.vimeo_player, null);
        popuppdfWindow = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        popuppdfWindow.setContentView(layout);
        popuppdfWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);

        RelativeLayout image_rlToolbar = (RelativeLayout) layout.findViewById(R.id.image_rlToolbar);
        ImageView imgBack = (ImageView) layout.findViewById(R.id.imgBack);
        WebView myWebView = (WebView) layout.findViewById(R.id.myWebView);

        image_rlToolbar.setBackgroundColor(getResources().getColor(R.color.clr_black));
        imgBack.setImageResource(R.drawable.ic_close);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popuppdfWindow.dismiss();
            }
        });
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.Loading));
        pDialog.setCancelable(false);
        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();
                }
            }
        });


        myWebView.setBackgroundColor(getResources().getColor(R.color.clr_black));
        myWebView.setWebViewClient(new MyWebViewClient(this));
        myWebView.getSettings().setLoadsImagesAutomatically(true);
        myWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.setVerticalScrollBarEnabled(true);
        WebSettings webSettings = myWebView.getSettings();
        myWebView.getSettings().setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);
        Log.d("content", pdfuri);
        myWebView.loadUrl("http://drive.google.com/viewerng/viewer?embedded=true&url=" + pdfuri);
    }


    private void showimagepopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.activity_image_circular_pop_up, null);
        popupimage = new PopupWindow(layout, android.app.ActionBar.LayoutParams.WRAP_CONTENT, android.app.ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupimage.setContentView(layout);

        ImageView imagePopup_ToolBarIvBack = (ImageView) layout.findViewById(R.id.imagePopup_ToolBarIvBack);
        RelativeLayout relativeLayoutHeader = (RelativeLayout) layout.findViewById(R.id.relativeLayoutHeader);
        RelativeLayout rlbackground = (RelativeLayout) layout.findViewById(R.id.rlbackground);
        ZoomageView demoView = (ZoomageView) layout.findViewById(R.id.demoView);

        relativeLayoutHeader.setVisibility(View.GONE);
        rlbackground.setBackgroundColor(getResources().getColor(R.color.clr_black));
        imagePopup_ToolBarIvBack.setImageResource(R.drawable.ic_close);

        imagePopup_ToolBarIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupimage.dismiss();
            }
        });
//        private void loadImage() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing()) {
            mProgressDialog.show();
        }


        Glide.with(ExamEnhancementQuestions.this)
                .load(imageurl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        mProgressDialog.dismiss();
                        onBackPressed();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        mProgressDialog.dismiss();
                        return false;
                    }
                })
                .into(demoView);
        mProgressDialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnnext:

                nextpos = adapterpos + 1;
                if (nextpos != 0) {
                    Log.d("clickn", String.valueOf(nextpos));

                    mAdapter = new QuestionForQuizAdapter(msgModelList, this, nextpos, this);

                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());

                    rcyquestions.smoothScrollToPosition(nextpos);

                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.imgnext:

                nextpos = adapterpos + 1;
                if (nextpos != 0) {
                    Log.d("clickn", String.valueOf(nextpos));

                    mAdapter = new QuestionForQuizAdapter(msgModelList, this, nextpos, this);

                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());

                    rcyquestions.smoothScrollToPosition(nextpos);

                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.btnprevious:

                previouspos = adapterpos - 1;
                if (previouspos != -1) {
                    Log.d("clickp", String.valueOf(previouspos));

                    mAdapter = new QuestionForQuizAdapter(msgModelList, this, previouspos, this);
                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());

                    rcyquestions.smoothScrollToPosition(previouspos);

                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.imgprev:

                previouspos = adapterpos - 1;
                if (previouspos != -1) {
                    Log.d("clickp", String.valueOf(previouspos));

                    mAdapter = new QuestionForQuizAdapter(msgModelList, this, previouspos, this);
                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(ExamEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());


                    rcyquestions.smoothScrollToPosition(previouspos);


                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.btnsubmit:
                if (questionlist.size() != msgModelList.size()) {

                    for (int i = 0; i < msgModelList.size(); i++) {
                        if (!questionlist.contains(String.valueOf(msgModelList.get(i).getQestionId()))) {
                            String emptyque = msgModelList.get(i).getQestionId() + "~" + 0;
                            answerlist.add(emptyque);
                        }
                    }
                }

                if (answerlist.size() == msgModelList.size()) {
                    showAlertApi(getResources().getString(R.string.Are_want_submit_Exam));
                }

                break;

            case R.id.lnrPDFtext:


                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/gview?embedded=true&url=" + pdfuri));
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                browserIntent.setPackage("com.android.chrome");
                startActivity(browserIntent);

                break;

            case R.id.imgVideo:
                showvideopopup();
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.imgShadow:
                showvideopopup();
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.imgplay:
                showvideopopup();
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case R.id.videoview:
                showvideopopup();
                popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;

            case R.id.imgview:
                showimagepopup();
                popupimage.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
        }
    }
}