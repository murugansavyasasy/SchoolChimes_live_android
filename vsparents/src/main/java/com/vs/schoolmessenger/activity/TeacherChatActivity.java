package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherChatAdapter;
import com.vs.schoolmessenger.databinding.ActivityTeacherChatBinding;
import com.vs.schoolmessenger.interfaces.PaginationScrollListener;
import com.vs.schoolmessenger.interfaces.PopupListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Subject;
import com.vs.schoolmessenger.model.TeacherChat;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class TeacherChatActivity extends AppCompatActivity {

    ActivityTeacherChatBinding binding;
    String isChangeAnswer = "0";
    TeacherChat teacherChat;
    Subject subject;
    String staffId;
    String comeFrom;
    AlertDialog alertDialog;
    TeacherChatAdapter adapter;
    ArrayList<TeacherChat> teacherMessages = new ArrayList<>();

    int chatCount;
    int offset = 0;
    int limit;

    boolean isLoading = false;
    boolean isLastPage = false;
    int totalPages;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_teacher_chat);
        Intent intent = getIntent();
        subject = new Gson().fromJson(intent.getStringExtra(Constants.SUBJECT), Subject.class);
        staffId = intent.getStringExtra(Constants.STAFF_ID);
        comeFrom = intent.getStringExtra(Constants.COME_FROM);

        teacherChatApi(offset);
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        binding.repalyAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.chatMessage.getText().toString().isEmpty())
                    teacherAnswerApi(binding.chatMessage.getText().toString(), "1");
                closeKeyBoard();
            }
        });

        binding.repalyPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.chatMessage.getText().toString().isEmpty())
                    teacherAnswerApi(binding.chatMessage.getText().toString(), "2");
                closeKeyBoard();
            }
        });

        adapter = new TeacherChatAdapter(comeFrom, getApplicationContext(), new PopupListener() {
            @Override
            public void click(String selected, TeacherChat teacherChat) {
                TeacherChatActivity.this.teacherChat = teacherChat;
                binding.layoutGroup.setVisibility(View.VISIBLE);
                if (selected.equals("Answer")) {
                    isChangeAnswer = "0";
                } else {
                    isChangeAnswer = "1";
                }
            }
        });

        binding.staffList.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) binding.staffList.getLayoutManager()) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                Log.d("studentchat", "Load more items");
                teacherChatApi(offset);
            }

            @Override
            public int getTotalPageCount() {
                return totalPages;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
    }

    public void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void teacherChatApi(final int currentOffset) {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TeacherChatActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TeacherChatActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherChatActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Offset", currentOffset);
        jsonObject.addProperty("StaffID", staffId);
        jsonObject.addProperty("SectionID", subject.SectionId);
        jsonObject.addProperty("SubjectID", subject.SubjectID);
        jsonObject.addProperty("isClassTeacher", subject.isClassTeacher);
        Log.d("reqteachat",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.teacherChat(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        Gson gson = new Gson();
                        Type userListType = new TypeToken<ArrayList<TeacherChat>>() {
                        }.getType();
                        ArrayList<TeacherChat> messages = gson.fromJson(response.body(), userListType);

                        try {
                            if (currentOffset == 0) {
                                if (!messages.isEmpty()) {
                                    limit = messages.get(0).Limit;
                                    if (limit > 0) {
                                        chatCount = messages.get(0).ChatCount;
                                        offset = messages.get(0).Offset + 1;
                                        totalPages = (chatCount / limit) + ((chatCount % limit) == 0 ? 0 : 1);
                                        if (messages.size() < limit)
                                            isLoading = true;
                                    } else if (limit == 0) {
                                        isLoading = true;
                                    }
                                } else {
                                    alertDialog("No records found", "OK");
                                }
                                teacherMessages.clear();
                                teacherMessages.addAll(messages);
                                adapter.addTeacherChatList(teacherMessages);
                                binding.staffList.setAdapter(adapter);
                            } else {
                                if (!messages.isEmpty()) {
                                    teacherMessages.addAll(messages);
                                    adapter.notifyDataSetChanged();
                                    isLastPage = (currentOffset == totalPages - 1);
                                    offset = messages.get(0).Offset + 1;
                                    Log.d("item count", binding.staffList.getLayoutManager().getItemCount() + "");

                                    if (isLastPage)
                                        isLoading = true;
                                    else
                                        isLoading = false;
                                }


                                else {
                                    alertDialog("No records found", "OK");
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Response Data Exception", e.getMessage());
                            alertDialog(e.getMessage(), "Ok");
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

    public void teacherAnswerApi(String answer, String replayType) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TeacherChatActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("QuestionID", teacherChat.QuestionID);
        jsonObject.addProperty("Answer", answer);
        jsonObject.addProperty("isChangeAnswer", isChangeAnswer);
        jsonObject.addProperty("ReplyType", replayType);
        jsonObject.addProperty("StaffID", staffId);

        Log.d("reqansque", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.answerStudentQuestion(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        binding.layoutGroup.setVisibility(View.GONE);
                        binding.chatMessage.setText("");
                        Log.d("Response", response.body().toString());
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            TeacherChat teacher = new Gson().fromJson(jsonObject.getString("result"), TeacherChat.class);
                            if (!teacherMessages.isEmpty()) {
                                int index = teacherMessages.indexOf(teacherChat);
                                teacherMessages.get(index).AnsweredOn = teacher.AnsweredOn;
                                teacherMessages.get(index).ChangeAnswer = teacher.ChangeAnswer;
                                teacherMessages.get(index).ReplyType = teacher.ReplyType;
                                teacherMessages.get(index).QuestionID = teacher.QuestionID;
                                teacherMessages.get(index).Answer = teacher.Answer;
                                adapter.notifyDataSetChanged();
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

    public void alertDialog(String message, String positiveText) {
        if (alertDialog != null && alertDialog.isShowing())
            return;
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(R.string.alert);
        ad.setMessage(message);
        ad.setPositiveButton(positiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
                onBackPressed();
            }
        });
        ad.setCancelable(false);
        alertDialog = ad.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }
}
