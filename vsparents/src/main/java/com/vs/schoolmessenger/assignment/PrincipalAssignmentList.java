package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.DatesList;
import com.vs.schoolmessenger.activity.TextCircular;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PrincipalAssignmentList extends AppCompatActivity implements RefreshInterface {
    RecyclerView recyclerView;
    ImageView imgBack;

    String isNewVersion;
    TextView LoadMore;
    TextView lblNoMessages;

    ImageView imgSearch;
    EditText Searchable;

    AssignmentViewAdapter assignment_adapter;
    private ArrayList<AssignmentViewClass> assignlist = new ArrayList<>();

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
        setContentView(R.layout.view_assignment);
        recyclerView =findViewById(R.id.recyclerView);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setVisibility(View.VISIBLE);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (assignment_adapter == null)
                    return;

                if (assignment_adapter.getItemCount() < 1) {
                    recyclerView.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                } else {
                    recyclerView.setVisibility(View.VISIBLE);
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




        LoadMore=(TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setEnabled(true);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreViewAllAssignmentListByStaff();
            }
        });

        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(PrincipalAssignmentList.this);
        seeMoreButtonVisiblity();



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PrincipalAssignmentList.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);
        assignment_adapter = new AssignmentViewAdapter(PrincipalAssignmentList.this, assignlist,this,"0");
        recyclerView.setAdapter(assignment_adapter);
    }

    private void filterlist(String s) {

        List<AssignmentViewClass> temp = new ArrayList();
        for (AssignmentViewClass d : assignlist) {

            if (d.getDate().toLowerCase().contains(s.toLowerCase()) || d.getTitle().toLowerCase().contains(s.toLowerCase()) || d.getContent().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        assignment_adapter.updateList(temp);
    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.VISIBLE);
        }
    }

    private void LoadMoreViewAllAssignmentListByStaff() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(PrincipalAssignmentList.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(PrincipalAssignmentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PrincipalAssignmentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(PrincipalAssignmentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(PrincipalAssignmentList.this);

        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
        object.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        object.addProperty("MobileNumber", MobileNumber);
        Log.d("view:req", object.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.ViewAllAssignmentListByStaff_Archive(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    Log.d("Upload-Code:Response", response.code() + "-" + response);

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    LoadMore.setVisibility(View.GONE);
                    lblNoMessages.setVisibility(View.GONE);
                    mProgressDialog.dismiss();
                    try {
//                                String strResponse = response.body().toString();

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");

                                String Message = jsonObject.getString("Message");
                                if (result.equals("1")) {
                                    String AssignmentId = jsonObject.getString("AssignmentId");
                                    String Type = jsonObject.getString("Type");
                                    String Content = jsonObject.getString("Subject");
                                    String Title = jsonObject.getString("Title");
                                    String Date = jsonObject.getString("Date");
                                    String Time = jsonObject.getString("Time");
                                    String submittedCount = jsonObject.getString("submittedCount");
                                    String TotalCount = jsonObject.getString("TotalCount");
                                    String EndDate = jsonObject.getString("EndDate");
                                    String category = jsonObject.getString("category");

                                    boolean is_Archive = jsonObject.getBoolean("is_Archive");


                                    AssignmentViewClass report = new AssignmentViewClass(AssignmentId, Type, Content, Title, Date, Time, submittedCount,TotalCount,EndDate,"0","0",is_Archive,category);
                                    assignlist.add(report);
//                                        assignment_adapter.notifyDataSetChanged();
                                }
                                else{
                                    alert(Message);
                                }
                            }


                            assignment_adapter.notifyDataSetChanged();
                        }
                        else{

                            alert(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        Log.e("Response Exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Log.e("Response Failure", t.getMessage());
                showToast(getResources().getString(R.string.Server_Connection_Failed));
            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewAllAssignmentListByStaff();

    }

    private void ViewAllAssignmentListByStaff() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(PrincipalAssignmentList.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(PrincipalAssignmentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PrincipalAssignmentList.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(PrincipalAssignmentList.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(PrincipalAssignmentList.this);

        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
        object.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        object.addProperty("MobileNumber", MobileNumber);
//        object.addProperty("ProcessBy", "2174939");
        Log.d("view:req", object.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.ViewAllAssignmentListByStaff(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    Log.d("Upload-Code:Response", response.code() + "-" + response);

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    mProgressDialog.dismiss();
                    try {
//                                String strResponse = response.body().toString();
                        assignlist.clear();

                        if(isNewVersion.equals("1")){
                            LoadMore.setVisibility(View.VISIBLE);
                            lblNoMessages.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.VISIBLE);
                        }

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                String result = jsonObject.getString("result");
                                String Message = jsonObject.getString("Message");
                                if (result.equals("1")) {
                                    String AssignmentId = jsonObject.getString("AssignmentId");
                                    String Type = jsonObject.getString("Type");
                                    String Content = jsonObject.getString("Subject");
                                    String Title = jsonObject.getString("Title");
                                    String Date = jsonObject.getString("Date");
                                    String Time = jsonObject.getString("Time");
                                    String submittedCount = jsonObject.getString("submittedCount");
                                    String TotalCount = jsonObject.getString("TotalCount");
                                    String EndDate = jsonObject.getString("EndDate");
                                    String category = jsonObject.getString("category");

                                    AssignmentViewClass report = new AssignmentViewClass(AssignmentId, Type, Content, Title, Date, Time, submittedCount,TotalCount,EndDate,"0","0",false,category);
                                    assignlist.add(report);
//                                        assignment_adapter.notifyDataSetChanged();
                                }
                                else{

                                    if(isNewVersion.equals("1")){
                                        lblNoMessages.setVisibility(View.VISIBLE);
                                        lblNoMessages.setText(Message);

                                        String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodAssignmentStaff(PrincipalAssignmentList.this);
                                        if(loadMoreCall.equals("1")){
                                            TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(PrincipalAssignmentList.this,"");
                                            LoadMoreViewAllAssignmentListByStaff();
                                        }
                                    }
                                    else {
                                        lblNoMessages.setVisibility(View.GONE);
                                        alert(Message);
                                    }



                                }
                            }

                            assignment_adapter.notifyDataSetChanged();
                        }
                        else{

                            alert(getResources().getString(R.string.no_records));
                        }


                    } catch (Exception e) {
                        Log.e("Response Exception", e.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                Log.e("Response Failure", t.getMessage());
                showToast(getResources().getString(R.string.Server_Connection_Failed));
            }


        });
    }


    private void showToast(String msg) {
        Toast.makeText(PrincipalAssignmentList.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PrincipalAssignmentList.this);


            alertDialog.setTitle(R.string.alert);
            alertDialog.setMessage(strStudName);
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();

                }
            });

            alertDialog.show();
        }


    @Override
    public void delete() {
        TeacherUtil_SharedPreference.putOnBackPressed(PrincipalAssignmentList.this,"1");

        ViewAllAssignmentListByStaff();
    }
}
