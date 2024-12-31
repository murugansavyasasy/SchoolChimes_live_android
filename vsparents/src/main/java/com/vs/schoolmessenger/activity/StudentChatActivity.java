package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.StudentChatAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.databinding.ActivityStudentChatBinding;
import com.vs.schoolmessenger.interfaces.PaginationScrollListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StudentChat;
import com.vs.schoolmessenger.model.SubjectDetail;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

public class StudentChatActivity extends AppCompatActivity {
    ActivityStudentChatBinding binding;
    String classTeacherId;
    String sectionId;
    SubjectDetail subjectDetail;
    AlertDialog alertDialog;
    StudentChatAdapter adapter;
    ArrayList<StudentChat> studentMessages = new ArrayList<>();
    int chatCount;
    int offset = 0;
    int limit;
    boolean isLoading = false;
    boolean isLastPage = false;
    int totalPages;

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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_student_chat);
        Intent intent = getIntent();
        classTeacherId = intent.getStringExtra(Constants.CLASS_TEACHER_ID);
        sectionId = intent.getStringExtra(Constants.SECTION_ID);
        subjectDetail = new Gson().fromJson(intent.getStringExtra(Constants.SUBJECT_DETAIL), SubjectDetail.class);

        binding.staffName.setText(subjectDetail.staffname);
        binding.section.setText(String.format("( %s )", subjectDetail.subjectname));
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (subjectDetail.subjectname.equalsIgnoreCase(Constants.CLASS_TEACHER))
            studentChatApi(offset, "1");
        else
            studentChatApi(offset, "0");
        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!binding.chatMessage.getText().toString().isEmpty())
                    if (subjectDetail.subjectname.equalsIgnoreCase(Constants.CLASS_TEACHER))
                        studentQuestionApi(binding.chatMessage.getText().toString(), "1");
                    else
                        studentQuestionApi(binding.chatMessage.getText().toString(), "0");
                closeKeyBoard();
            }
        });
        adapter = new StudentChatAdapter(studentMessages, subjectDetail.staffname);
        binding.studentChatList.addOnScrollListener(new PaginationScrollListener((LinearLayoutManager) binding.studentChatList.getLayoutManager()) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                Log.d("studentchat", "Load more items");
                if (subjectDetail.subjectname.equalsIgnoreCase(Constants.CLASS_TEACHER))
                    studentChatApi(offset, "1");
                else
                    studentChatApi(offset, "0");
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

        adapter = new StudentChatAdapter(studentMessages, subjectDetail.staffname);
        binding.studentChatList.setAdapter(adapter);

    }

    public void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void studentChatApi(final int current_offset, String isclassteacher) {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(StudentChatActivity.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(StudentChatActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {

            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StudentChatActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String childID = Util_SharedPreference.getChildIdFromSP(StudentChatActivity.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Offset", current_offset);
        jsonObject.addProperty("StaffID", subjectDetail.StaffID);
        jsonObject.addProperty("StudentID", childID);
        jsonObject.addProperty("SectionID", sectionId);
        jsonObject.addProperty("SubjectID", subjectDetail.SubjectID);
        jsonObject.addProperty("isClassTeacher", isclassteacher);
        Log.d("reqstdchatscr", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.studentChat(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        Gson gson = new Gson();
                        Type userListType = new TypeToken<ArrayList<StudentChat>>() {
                        }.getType();
                        ArrayList<StudentChat> messages = gson.fromJson(response.body(), userListType);

                        try {
                            if (current_offset == 0) {
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
                                    binding.noMessages.setVisibility(View.VISIBLE);
                                    binding.studentChatList.setVisibility(View.GONE);
                                }
                                studentMessages.clear();
                                studentMessages.addAll(messages);
//                                adapter.addStudentChatList(studentMessages);
                                binding.studentChatList.setAdapter(adapter);
                            } else {
                                if (!messages.isEmpty()) {
                                    studentMessages.addAll(messages);
                                    adapter.notifyDataSetChanged();
                                    isLastPage = (current_offset == totalPages - 1);
                                    offset = messages.get(0).Offset + 1;
                                    Log.d("item count", String.valueOf(binding.studentChatList.getLayoutManager().getItemCount()));
                                    isLoading = isLastPage;
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "No records found", Toast.LENGTH_SHORT).show();
                            Log.e("Response Data Exception", e.getMessage());
                            binding.noMessages.setVisibility(View.VISIBLE);
                            binding.studentChatList.setVisibility(View.GONE);
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

    public void studentQuestionApi(String chatMessage, String isClassTeacher) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(StudentChatActivity.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String childID = Util_SharedPreference.getChildIdFromSP(StudentChatActivity.this);
        String schoolid = Util_SharedPreference.getSchoolIdFromSP(StudentChatActivity.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StudentID", childID);
        jsonObject.addProperty("StaffID", subjectDetail.StaffID);
        jsonObject.addProperty("SubjectID", subjectDetail.SubjectID);
        jsonObject.addProperty("SectionID", sectionId);
        jsonObject.addProperty("isClassTeacher", isClassTeacher);
        jsonObject.addProperty("SchoolID", schoolid);
        jsonObject.addProperty("Question", chatMessage);
        Log.d("reqstudentque", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.studentQuestion(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    if (response.code() == 200 || response.code() == 201) {
                        binding.chatMessage.setText("");
                        binding.studentChatList.setVisibility(View.VISIBLE);

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String message = jsonObject.optString("Message", "No Message");
                            Log.d("Message", message);

                            if (jsonObject.has("result")) {
                                String result = jsonObject.getString("result");
                                StudentChat studentChat = new Gson().fromJson(result, StudentChat.class);

                                if (studentChat != null) {
                                    if (studentMessages == null) {
                                        studentMessages = new ArrayList<>();
                                    }
                                    studentMessages.add(studentChat);

                                    if (adapter == null) {
                                        adapter = new StudentChatAdapter(studentMessages, subjectDetail.staffname);
                                        binding.studentChatList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                        binding.studentChatList.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                    // Scroll to the latest message
                                    binding.studentChatList.scrollToPosition(studentMessages.size() - 1);
                                }
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
            }
        });
        ad.setCancelable(false);
        alertDialog = ad.create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));
    }
}
