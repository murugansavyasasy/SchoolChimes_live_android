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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;
import com.jsibbold.zoomage.ZoomageView;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.QuestionForQuizAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.QuestionClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.KnowledgeEnhancementModel;
import com.vs.schoolmessenger.model.QuestionForQuiz;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class KnowledgeEnhancementQuestions extends AppCompatActivity implements QuestionClickListener, View.OnClickListener {
    public QuestionForQuizAdapter mAdapter;
    TextView lblStarttime, lblendtime, lblduration, lblmin, lblqueno, lblque, lblPDF, lblprev, lblnext;
    RadioGroup rgoptions;
    RecyclerView rcyquestions;
    FrameLayout videoview;
    ConstraintLayout constraint;
    LinearLayout lnrPDFtext;
    ImageView imgview, imgVideo, imgplay, imgShadow, imgprev, imgnext;
    Button btnnext, btnprevious, btnsubmit;
    String child_id, ansid, answer;
    int adapterpos = 0, nextpos = 0, previouspos = 0, QuizId;
    QuestionForQuiz msgModel;
    KnowledgeEnhancementModel info;
    ArrayList<String> answerlist = new ArrayList<>();
    ArrayList<String> questionlist = new ArrayList<>();
    RelativeLayout ryttime;
    String pdfuri, videourl, imageurl;
    PopupWindow popupWindow, popupimage;
    TabLayout tabLayout;
    private final ArrayList<QuestionForQuiz> msgModelList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_exam_enhancement_questions);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(getResources().getString(R.string.Level));
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabLayout = Objects.requireNonNull(this).findViewById(R.id.tabs);


        btnnext = findViewById(R.id.btnnext);
        btnprevious = findViewById(R.id.btnprevious);
        btnsubmit = findViewById(R.id.btnsubmit);
        imgview = findViewById(R.id.imgview);
        imgVideo = findViewById(R.id.imgVideo);
        imgShadow = findViewById(R.id.imgShadow);
        imgplay = findViewById(R.id.imgplay);
        videoview = findViewById(R.id.videoview);
        rcyquestions = findViewById(R.id.rcyquestions);
        rgoptions = findViewById(R.id.rgoptions);
        lblque = findViewById(R.id.lblque);
        lblqueno = findViewById(R.id.lblqueno);
        lblmin = findViewById(R.id.lblmin);
        lblduration = findViewById(R.id.lblduration);
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
        ryttime.setVisibility(View.GONE);

        child_id = Util_SharedPreference.getChildIdFromSP(this);
        info = (KnowledgeEnhancementModel) getIntent().getSerializableExtra("Knowledge");
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(KnowledgeEnhancementQuestions.this);
        if (info.getIsAppRead().equals("0")) {
            ChangeMsgReadStatus.changeReadStatus(KnowledgeEnhancementQuestions.this, String.valueOf(info.getDetailId()), MSG_TYPE_QUIZ, "", isNewVersion, false, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {

                }
            });

        }
        examEnhancement(info.getQuizId());


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

        if (adapterpos == 0) {
            mAdapter = new QuestionForQuizAdapter(msgModelList, this, 0, this);
            rcyquestions.setHasFixedSize(true);
            rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
            rcyquestions.setItemAnimator(new DefaultItemAnimator());
            rcyquestions.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void examEnhancement(int quizId) {
        QuizId = quizId;
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
        jsonObject.addProperty("QuizId", quizId);
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
                            msgModelList.clear();
                            answerlist.clear();
                            questionlist.clear();
                            adapterpos = 0;
                            nextpos = 0;
                            previouspos = 0;
                            if (status == 1) {

                                String level = jsonObject.getString("Level");
                                ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Level" + " " + level);

                                JSONArray data = jsonObject.getJSONArray("data");

                                if (data.length() != 0) {
                                    constraint.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < data.length(); i++) {
                                        jsonObject = data.getJSONObject(i);
                                        msgModel = new QuestionForQuiz(jsonObject.getInt("QestionId"),
                                                jsonObject.getString("Question"),
                                                jsonObject.getString("VideoUrl"),
                                                jsonObject.getString("FileType"),
                                                jsonObject.getString("FileUrl"),
                                                jsonObject.getInt("mark"),
                                                String.valueOf(i + 1),
                                                jsonObject.getJSONArray("Answer")
                                        );

                                        msgModelList.add(msgModel);

                                    }
                                    mAdapter = new QuestionForQuizAdapter(msgModelList, KnowledgeEnhancementQuestions.this, 0, KnowledgeEnhancementQuestions.this);

                                    rcyquestions.setHasFixedSize(true);
                                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                                    rcyquestions.setItemAnimator(new DefaultItemAnimator());
                                    rcyquestions.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
//                                    mAdapter.notifyDataSetChanged();
                                } else {
                                    constraint.setVisibility(View.GONE);
                                    mAdapter = new QuestionForQuizAdapter(msgModelList, KnowledgeEnhancementQuestions.this, 0, KnowledgeEnhancementQuestions.this);

                                    rcyquestions.setHasFixedSize(true);
                                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                                    rcyquestions.setItemAnimator(new DefaultItemAnimator());
                                    rcyquestions.setAdapter(mAdapter);
                                    mAdapter.notifyDataSetChanged();
                                    showAlert(getResources().getString(R.string.no_records), "0", "EXAM");
                                }

                            } else {
                                constraint.setVisibility(View.GONE);
                                showAlert(message, "0", "EXAM");
                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(KnowledgeEnhancementQuestions.this, "Server Response Failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(KnowledgeEnhancementQuestions.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void submitquiz(int id) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("QuizId", id);
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
                            QuizId = Integer.parseInt(jsonObject.getString("QuizId"));

                            if (strStatus == 1) {
                                if (QuizId == 0) {
                                    showAlert(strMsg, String.valueOf(strStatus), "SUBMIT");
                                } else {
                                    showAlertApi("Submitted Successfully..!,Do you want to attend the next level", "LEVEL");
                                }
                            } else {
                                showAlert(strMsg, "0", "SUBMIT");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(KnowledgeEnhancementQuestions.this, "Server Response Failed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(KnowledgeEnhancementQuestions.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAlert(String msg, final String s, final String submit) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (submit.equals("SUBMIT")) {
                    if (s.equals("1") && QuizId == 0) {
                        dialog.cancel();
                        Intent i = new Intent(KnowledgeEnhancementQuestions.this, ParentKnowledgeEnhancementScreen.class);
                        i.putExtra("complete", 1);
                        startActivity(i);
                        finish();
                    } else {
                        dialog.cancel();
                        finish();
                    }
                } else {
                    dialog.cancel();
                    finish();
                }

            }
        });


        AlertDialog dialog = alertDialog.create();

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    private void showAlertApi(String msg, final String submit) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(getResources().getString(R.string.alert));

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (submit.equals("SUBMIT")) {
                    submitquiz(QuizId);
                } else if (submit.equals("LEVEL")) {
                    examEnhancement(QuizId);
                }
                dialog.cancel();
            }
        });

        alertDialog.setPositiveButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (submit.equals("SUBMIT")) {
                    dialog.cancel();
//                    finish();
                } else if (submit.equals("LEVEL")) {
                    dialog.cancel();
                    Intent i = new Intent(KnowledgeEnhancementQuestions.this, ParentKnowledgeEnhancementScreen.class);
                    i.putExtra("complete", 1);
                    startActivity(i);
                    finish();
                }
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void addclass(final QuestionForQuiz menu, int position) {
        adapterpos = position;
        rgoptions.removeAllViews();
        rgoptions.clearCheck();
        videoview.setVisibility(View.GONE);
        imgview.setVisibility(View.GONE);
        lnrPDFtext.setVisibility(View.GONE);
        for (int i = 0; i < menu.getAnswer().length(); i++) {
            String[] id = new String[0];
            try {
                id = menu.getAnswer().get(i).toString().split("~");
                ansid = id[0];
                answer = id[1];
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RadioButton radioButton = new RadioButton(getApplicationContext());
            radioButton.setText(answer);
            radioButton.setId(Integer.parseInt(ansid));
            radioButton.setTextSize(16);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setPadding(1, 10, 1, 1);

            rgoptions.addView(radioButton);
            rgoptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    final int selectedPosition = rgoptions.getCheckedRadioButtonId();
                    if (selectedPosition != -1) {
                        String idcom = menu.getQestionId() + "~" + selectedPosition;
                        String ansidref, answerref;
                        if (questionlist.size() == 0) {
                            questionlist.add(String.valueOf(menu.getQestionId()));
                            if (answerlist.size() == 0) {
                                answerlist.add(idcom);
                                Log.d("answerlist", answerlist.get(0));
                            }
                        } else if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
                            int replacequepos = questionlist.indexOf(String.valueOf(menu.getQestionId()));
                            Log.d("replacepos", String.valueOf(replacequepos));
                            questionlist.set(replacequepos, String.valueOf(menu.getQestionId()));
                            for (int i = 0; i < menu.getAnswer().length(); i++) {
                                String[] idref = new String[0];
                                try {
                                    idref = menu.getAnswer().get(i).toString().split("~");
                                    ansidref = idref[0];
                                    answerref = idref[1];


                                    if (answerlist.contains(menu.getQestionId() + "~" + ansidref)) {
                                        Log.d("changeexist", "changeexist");
                                        int replacepos = answerlist.indexOf(menu.getQestionId() + "~" + ansidref);
                                        Log.d("replacepos", String.valueOf(replacepos));
                                        answerlist.set(replacepos, idcom);
                                    }


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            questionlist.add(String.valueOf(menu.getQestionId()));
                            answerlist.add(idcom);
                        }
                        btnsubmit.setEnabled(answerlist.size() != 0);

                    }
                }
            });

            if (questionlist.contains(String.valueOf(menu.getQestionId()))) {
                int replacequepos = questionlist.indexOf(String.valueOf(menu.getQestionId()));
                if (questionlist.get(replacequepos).equals(String.valueOf(menu.getQestionId()))) {
                    String answersetupl = answerlist.get(replacequepos);

                    String[] idsetup = new String[0];
                    idsetup = answersetupl.split("~");
                    int ansidsetup = Integer.parseInt(idsetup[0]);
                    Log.d("ansidsetup", String.valueOf(ansidsetup));
                    int answersetup = Integer.parseInt(idsetup[1]);
                    Log.d("answersetup", String.valueOf(answersetup));
                    if (radioButton.getId() == answersetup) {
                        radioButton.setChecked(true);
                    }

                }


            }

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
        videoview.setVisibility(View.GONE);
        imgview.setVisibility(View.GONE);
        lnrPDFtext.setVisibility(View.GONE);
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
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage(getResources().getString(R.string.Loading));
        pDialog.setCancelable(false);
        myWebView.setBackgroundColor(getResources().getColor(R.color.clr_black));

        myWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                pDialog.show();
                setProgress(progress * 100);
                if (progress == 100) {
                    pDialog.dismiss();
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
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        Glide.with(KnowledgeEnhancementQuestions.this)
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
                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
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
                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
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
                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());

                    rcyquestions.smoothScrollToPosition(previouspos);

                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.imgprev:

                previouspos = adapterpos - 1;
                if (previouspos != -1) {

                    mAdapter = new QuestionForQuizAdapter(msgModelList, this, previouspos, this);
                    rcyquestions.setHasFixedSize(true);
                    rcyquestions.setLayoutManager(new LinearLayoutManager(KnowledgeEnhancementQuestions.this, LinearLayoutManager.HORIZONTAL, false));
                    rcyquestions.setItemAnimator(new DefaultItemAnimator());


                    rcyquestions.smoothScrollToPosition(previouspos);


                    rcyquestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
                break;

            case R.id.btnsubmit:
                if (answerlist.size() == msgModelList.size()) {
                    showAlertApi(getResources().getString(R.string.want_submit_Quiz), "SUBMIT");
                } else if (questionlist.size() != msgModelList.size()) {

                    for (int i = 0; i < msgModelList.size(); i++) {
                        if (!questionlist.contains(String.valueOf(msgModelList.get(i).getQestionId()))) {
                            String emptyque = msgModelList.get(i).getQestionId() + "~" + 0;
                            answerlist.add(emptyque);
                        }
                    }
                    if (answerlist.size() == msgModelList.size()) {
                        showAlertApi(getResources().getString(R.string.want_submit_Quiz), "SUBMIT");
                    }
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