package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.QuizSubmissionResultAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.QuizSubmissions;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class ViewQuizResult extends AppCompatActivity {
    String quiz_id, child_id;
    TextView lblrightanswer, lblwronganswer, lblnotanswer;
    RecyclerView rcyquestions;

    QuizSubmissionResultAdapter mAdapter;
    private final ArrayList<QuizSubmissions> msgModelList = new ArrayList<>();

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
        setContentView(R.layout.activity_view_quiz_result);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.View_Report);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lblrightanswer = findViewById(R.id.lblrightanswer);
        lblwronganswer = findViewById(R.id.lblwronganswer);
        lblnotanswer = findViewById(R.id.lblnotanswer);
        rcyquestions = findViewById(R.id.rcyquestions);

        mAdapter = new QuizSubmissionResultAdapter(msgModelList, this, "1");
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rcyquestions.setLayoutManager(mLayoutManager);
        rcyquestions.setItemAnimator(new DefaultItemAnimator());
        rcyquestions.setAdapter(mAdapter);
        rcyquestions.getRecycledViewPool().setMaxRecycledViews(0, 80);
        quiz_id = String.valueOf(getIntent().getIntExtra("id", 0));
        quizresultapi();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void quizresultapi() {
        child_id = Util_SharedPreference.getChildIdFromSP(this);

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
        jsonObject.addProperty("StudentID", child_id);
        jsonObject.addProperty("QuizId", quiz_id);


        Log.d("jsonObject", jsonObject.toString());
        Call<JsonObject> call = apiService.ViewSubmission(jsonObject);

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
                            int status = jsonObject.getInt("status");
                            String message = jsonObject.getString("message");

                            if (status == 1) {

                                JSONObject data = new JSONObject(jsonObject.getString("data"));

                                QuizSubmissions msgModel;

                                lblrightanswer.setText(data.getString("rightAnswer"));
                                lblwronganswer.setText(data.getString("wrongAnswer"));
                                lblnotanswer.setText(data.getString("unAnswer"));


                                JSONArray quiz = data.getJSONArray("quizArray");
                                for (int i = 0; i < quiz.length(); i++) {
                                    jsonObject = quiz.getJSONObject(i);
                                    msgModel = new QuizSubmissions(jsonObject.getInt("id"),
                                            jsonObject.getInt("quizId"),
                                            jsonObject.getString("question"),
                                            jsonObject.getString("aOption"),
                                            jsonObject.getString("bOption"),
                                            jsonObject.getString("cOption"),
                                            jsonObject.getString("dOption"),
                                            jsonObject.getString("mark"),
                                            jsonObject.getString("answer"),
                                            jsonObject.getString("studentAnswer"),
                                            jsonObject.getString("correctAnswer")
                                    );
                                    msgModelList.add(msgModel);
                                }

                                mAdapter.notifyDataSetChanged();


                            } else {
                                showAlert(message);

                            }

                        } catch (Exception e) {
                            Log.e("TextMsg:Exception", e.getMessage());
                        }
                    } else {
                        Toast.makeText(ViewQuizResult.this, R.string.Server_Response_Failed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(ViewQuizResult.this, R.string.Server_Response_Failed, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
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