package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.QuestionDetails;
import com.vs.schoolmessenger.model.SubQustions;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
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

public class FeedBackDetails extends AppCompatActivity {

    public ArrayList<QuestionDetails> questionDetails = new ArrayList<>();
    public ArrayList<SubQustions> subQuestions = new ArrayList<>();
    EditText txtSchoolName, txtContactPerson, txtContactMobileNumber, txtEmailID;
    Button btnNext;
    String schoolname, contactperson, mobilenumber, emailID;
    Spinner SchoolList;
    TeacherSchoolsModel schoolmodel;
    String schoolID, StaffID;
    ArrayList<String> myArray = new ArrayList<>();
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    String selectedItemText;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        overridePendingTransition(R.anim.enter, R.anim.exit);

        setContentView(R.layout.feedback_details);

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


        txtContactPerson = (EditText) findViewById(R.id.txtContactPerson);
        txtContactMobileNumber = (EditText) findViewById(R.id.txtContactMobileNumber);
        txtEmailID = (EditText) findViewById(R.id.txtEmailID);


        SchoolList = (Spinner) findViewById(R.id.SchoolList);
        schoolmodel = (TeacherSchoolsModel) getIntent().getSerializableExtra("TeacherSchoolsModel");
        schoolname = schoolmodel.getStrSchoolName();

        myArray = (ArrayList<String>) getIntent().getSerializableExtra("schools");
        schools_list = (ArrayList<TeacherSchoolsModel>) getIntent().getSerializableExtra("list");


        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, myArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SchoolList.setAdapter(adapter);
        SchoolList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = (String) parent.getItemAtPosition(position);
                Log.d("selectedItemText", selectedItemText);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int i = 0; i < schools_list.size(); i++) {
                    final TeacherSchoolsModel nameslist = schools_list.get(i);
                    String name = nameslist.getStrSchoolName();
                    if (name.equals(selectedItemText)) {
                        schoolID = nameslist.getStrSchoolID();

                        StaffID = nameslist.getStrStaffID();
                        Log.d("schoolID", schoolID);
                    }
                }

                contactperson = txtContactPerson.getText().toString();
                mobilenumber = txtContactMobileNumber.getText().toString();
                emailID = txtEmailID.getText().toString();

                if (!contactperson.equals("") && !mobilenumber.equals("") && !emailID.equals("")) {

                    if (mobilenumber.length() < 10) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_valid_mobile), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent questionsActivity = new Intent(FeedBackDetails.this, FeedBackQuestionsScreen.class);
                        questionsActivity.putExtra("schoolID", schoolID);
                        questionsActivity.putExtra("schoolname", selectedItemText);
                        questionsActivity.putExtra("contactpersion", contactperson);
                        questionsActivity.putExtra("mobilenumber", mobilenumber);
                        questionsActivity.putExtra("email", emailID);
                        questionsActivity.putExtra("staffID", StaffID);
                        startActivity(questionsActivity);
                    }
                } else if (contactperson.equals("") || mobilenumber.equals("") || emailID.equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill_all_details), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(txtContactPerson.getWindowToken(), 0);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void feedBackDetails() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(FeedBackDetails.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

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
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(i);

                            String QuestionID = jsonObject.getString("QuestionID");
                            String Question = jsonObject.getString("Question");
                            QuestionDetails object = new QuestionDetails(QuestionID, Question);
                            questionDetails.add(object);

                            JSONArray answerlist = jsonObject.getJSONArray("AnswerList");
                            for (int j = 0; j < answerlist.length(); j++) {
                                JSONObject c = answerlist.getJSONObject(i);
                                String AnswerID = c.getString("AnswerID");
                                String subQuestion = c.getString("Answer");
                                SubQustions sub = new SubQustions(AnswerID, subQuestion, QuestionID, Question);
                                subQuestions.add(sub);

                            }
                        }

                        Intent questionsActivity = new Intent(FeedBackDetails.this, FeedBackQuestionsScreen.class);
                        questionsActivity.putExtra("Questions", questionDetails);
                        questionsActivity.putExtra("Sub_questions", subQuestions);
                        startActivity(questionsActivity);


                    } else {
                        Toast.makeText(getApplicationContext(), "Server Response Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }


}
