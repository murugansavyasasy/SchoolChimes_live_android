package com.vs.schoolmessenger.assignment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.TeacherSchoolList;
import com.vs.schoolmessenger.adapter.TeacherSchoolListForPrincipalAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.assignment.AssignmentViewAdapter;
import com.vs.schoolmessenger.assignment.AssignmentViewClass;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherSchoolListPrincipalListener;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_ASSIGNMENT;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

public class ViewAssignment extends Fragment implements RefreshInterface{
    RecyclerView recyclerView;
    ImageView imgBack;

    AssignmentViewAdapter assignment_adapter;
    private ArrayList<AssignmentViewClass> assignlist = new ArrayList<>();

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    RecyclerView rvSchoolsList;
    private int i_schools_count = 0;
    private int iRequestCode;
    String SchoolId;
    TeacherSchoolListForPrincipalAdapter schoolsListAdapter;
    boolean singleschoollogin = false;
    TabLayout tabLayout;

    String isNewVersion;
    TextView LoadMore;
    TextView lblNoMessages;

    RelativeLayout rytSearch;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_assignment, container, false);
    }

    @SuppressLint("NewApi")

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        tabLayout= Objects.requireNonNull(getActivity()).findViewById(R.id.assignTablayout);
        recyclerView = view.findViewById(R.id.recyclerView);
        LoadMore = view.findViewById(R.id.btnSeeMore);
        imgBack = view.findViewById(R.id.imgBack);
        rytSearch = view.findViewById(R.id.rytSearch);
        rytSearch.setVisibility(View.GONE);

        LoadMore=(TextView) view.findViewById(R.id.btnSeeMore);
        lblNoMessages=(TextView) view.findViewById(R.id.lblNoMessages);
        LoadMore.setEnabled(true);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreViewAllAssignmentListByStaff();
            }
        });

        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        seeMoreButtonVisiblity();

        if (listschooldetails.size() == 1) {
            LoadMore.setVisibility(View.VISIBLE);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);
            assignment_adapter = new AssignmentViewAdapter(getContext(), assignlist, this, "0");
            recyclerView.setAdapter(assignment_adapter);
        } else {

            listSchoolsAPI();
            schoolsListAdapter =
                    new TeacherSchoolListForPrincipalAdapter(getContext(), arrSchoolList,false, new TeacherSchoolListPrincipalListener() {
                        @Override
                        public void onItemClick(TeacherSchoolsModel item) {
                            TeacherUtil_Common.Principal_SchoolId = item.getStrSchoolID();
                            TeacherUtil_Common.Principal_staffId = item.getStrStaffID();
                            Intent inPrincipal = new Intent(getContext(), PrincipalAssignmentList.class);
                            startActivity(inPrincipal);

                        }
                    });
            recyclerView.hasFixedSize();
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(schoolsListAdapter);
            schoolsListAdapter.notifyDataSetChanged();
        }
    }

    private void LoadMoreViewAllAssignmentListByStaff() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(getActivity());


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

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
        }
        if(listschooldetails.size()==1){
            LoadMore.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(listschooldetails.size()==1){
            ViewAllAssignmentListByStaff();
         }
    }
    private void listSchoolsAPI() {
        LoadMore.setVisibility(View.GONE);

        Log.d("test1", "test1");
        i_schools_count = 0;
        Log.d("test2", "test2" + listschooldetails.size());
        for (int i = 0; i < listschooldetails.size(); i++) {
            Log.d("test3", "test3" + listschooldetails.size());
            TeacherSchoolsModel ss = listschooldetails.get(i);
            Log.d("test4", "test4");
            ss = new TeacherSchoolsModel(ss.getStrSchoolName(),ss.getSchoolNameRegional(), ss.getStrSchoolID(),
                    ss.getStrCity(), ss.getStrSchoolAddress(), ss.getStrSchoolLogoUrl(),
                    ss.getStrStaffID(), ss.getStrStaffName(), true, ss.getBookEnable(), ss.getOnlineLink(),ss.getIsPaymentPending(),ss.getIsSchoolType(),ss.getIsBiometricEnable(),ss.getAllowDownload());
            Log.d("test", ss.getStrSchoolName());
            arrSchoolList.add(ss);
            Log.d("Testing", "8***********************");


        }
    }
    private void ViewAllAssignmentListByStaff() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(getActivity());
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(getActivity());
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(getActivity());


        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy", TeacherUtil_Common.Principal_staffId);
        object.addProperty("SchoolID", TeacherUtil_Common.Principal_SchoolId);
        object.addProperty("MobileNumber", MobileNumber);
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
                            assignlist.clear();

                            if(isNewVersion.equals("1")){
                                LoadMore.setVisibility(View.VISIBLE);
                            }
                            else {
                                LoadMore.setVisibility(View.GONE);
                            }

                            if(listschooldetails.size()==1){
                                LoadMore.setVisibility(View.VISIBLE);
                            }
                            else {
                                LoadMore.setVisibility(View.GONE);
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
                                    }
                                    else{
                                        if(isNewVersion.equals("1")){
                                            lblNoMessages.setVisibility(View.VISIBLE);
                                            lblNoMessages.setText(Message);

                                            String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodAssignmentStaff(getActivity());
                                            if(loadMoreCall.equals("1")){
                                                TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(getActivity(),"");
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
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {
        if(getContext()!=null) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());


            alertDialog.setTitle(R.string.alert);
            alertDialog.setMessage(strStudName);
            alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tabLayout.getTabAt(0).select();
                    CreateAssignment fragment = new CreateAssignment();

                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction().disallowAddToBackStack();
                    ft.replace(R.id.viewpager, fragment);
                    ft.detach(fragment);
                    ft.attach(fragment);
                    ft.commit();

                }
            });

            alertDialog.show();
        }
    }

    @Override
    public void delete() {
        TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(getActivity(),"1");

        ViewAllAssignmentListByStaff();
    }
}
