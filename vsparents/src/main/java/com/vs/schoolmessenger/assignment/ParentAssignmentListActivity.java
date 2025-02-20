package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class ParentAssignmentListActivity extends AppCompatActivity implements RefreshInterface {
    RecyclerView recyclerView;
    ImageView imgBack;
    TextView lbltitle;
    AssignmentViewAdapter assignment_adapter;
    private ArrayList<AssignmentViewClass> assignlist = new ArrayList<>();
    private ArrayList<AssignmentViewClass> Offlineassignlist = new ArrayList<>();
    private ArrayList<AssignmentViewClass> totalassignlist = new ArrayList<>();
    String errorLogs="";
    File file=null;
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;

    ImageView imgSearch;
    EditText Searchable;

    Slider slider;
    LinearLayout lnrAction;

    LinearLayout mAdView;


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
        c = Calendar.getInstance();
        recyclerView =findViewById(R.id.recyclerView);
        imgBack =findViewById(R.id.imgBack);
        lbltitle =findViewById(R.id.lbltitle);
        imgBack.setVisibility(View.VISIBLE);
        lbltitle.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ParentAssignmentListActivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);

        assignment_adapter = new AssignmentViewAdapter(ParentAssignmentListActivity.this, assignlist,this,"1");
        recyclerView.setAdapter(assignment_adapter);

        Slider.init(new PicassoImageLoadingService(ParentAssignmentListActivity.this));
        slider = findViewById(R.id.banner);
        ImageView adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        ShowAds.getAds(ParentAssignmentListActivity.this,adImage,slider,"",mAdView);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        lnrAction = (LinearLayout) findViewById(R.id.lnrAction);
        lnrAction.setVisibility(View.GONE);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.assignment);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");


        LoadMore=(TextView) findViewById(R.id.btnSeeMore);
         lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
         LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMoreViewAllAssignmentListByStaff();

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


        isNewVersion=TeacherUtil_SharedPreference.getNewVersion(ParentAssignmentListActivity.this);

         seeMoreButtonVisiblity();

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void filterlist(String s) {
        List<AssignmentViewClass> temp = new ArrayList();
        for (AssignmentViewClass d : assignlist) {

            if (d.getDate().toLowerCase().contains(s.toLowerCase()) || d.getTitle().toLowerCase().contains(s.toLowerCase()) ||  d.getContent().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }
        }
        assignment_adapter.updateList(temp);
    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1")){
            LoadMore.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    private void LoadMoreViewAllAssignmentListByStaff() {
        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(ParentAssignmentListActivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ParentAssignmentListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ParentAssignmentListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(ParentAssignmentListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String childID= Util_SharedPreference.getChildIdFromSP(ParentAssignmentListActivity.this);
        String schoolID=Util_SharedPreference.getSchoolIdFromSP(ParentAssignmentListActivity.this);
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(ParentAssignmentListActivity.this);
        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy",childID);
        object.addProperty("SchoolID", schoolID);
        object.addProperty("MobileNumber", MobileNumber);
        Log.d("view:req", object.toString());

        errorLogs="";
        errorLogs="Request       :" +object.toString();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.LoadMoreGetAssignmentForStudent(object);

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
                       // assignlist.clear();
                        Offlineassignlist.clear();
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                int result = jsonObject.getInt("result");

                                String Message = jsonObject.getString("Message");
                                if (result==1) {
                                    String AssignmentId = jsonObject.getString("AssignmentId");
                                    String Type = jsonObject.getString("Type");
                                    String Content = jsonObject.getString("Subject");
                                    String Title = jsonObject.getString("Title");
                                    String Date = jsonObject.getString("Date");
                                    String Time = jsonObject.getString("Time");
                                    String submittedCount = jsonObject.getString("SubmittedCount");
                                    String TotalCount = jsonObject.getString("SentBy");
                                    String isAppRead = jsonObject.getString("isAppRead");
                                    String EndDate = jsonObject.getString("EndDate");
                                    String DeTailId = jsonObject.getString("DeTailId");
                                    String category = jsonObject.getString("category");
                                    boolean is_Archive = jsonObject.getBoolean("is_Archive");
                                    AssignmentViewClass report = new AssignmentViewClass(AssignmentId, Type, Content, Title, Date, Time, submittedCount,TotalCount,EndDate,isAppRead,DeTailId,is_Archive,category);
                                    assignlist.add(report);
                                    Offlineassignlist.add(report);
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

    private void sendMail() {

        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP error log");
        String to[] = {"murugan@voicesnap.com","swathi.gururajan@voicesnap.com","madhavan@voicesnap.net","nats@voicesnap.net" };
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_TEXT, errorLogs);
       // intent.putExtra(Intent.EXTRA_STREAM, errorLogs);
        startActivityForResult(Intent.createChooser(intent, "Send mail..."),
                1222);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewAllAssignmentListByStaff();
    }

    private void ViewAllAssignmentListByStaff() {
        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(ParentAssignmentListActivity.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(ParentAssignmentListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(ParentAssignmentListActivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(ParentAssignmentListActivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String childID= Util_SharedPreference.getChildIdFromSP(ParentAssignmentListActivity.this);
        String schoolID=Util_SharedPreference.getSchoolIdFromSP(ParentAssignmentListActivity.this);
        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(ParentAssignmentListActivity.this);
        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy",childID);
        object.addProperty("SchoolID", schoolID);
        object.addProperty("MobileNumber", MobileNumber);
        Log.d("view:req", object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.GetAssignmentForStudent(object);
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
                        totalassignlist.clear();

                        if(isNewVersion.equals("1")){
                            LoadMore.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for(int i=0;i<js.length();i++) {
                                JSONObject jsonObject = js.getJSONObject(i);

                                int result = jsonObject.getInt("result");
                                String Message = jsonObject.getString("Message");
                                if (result==1) {
                                    String AssignmentId = jsonObject.getString("AssignmentId");
                                    String Type = jsonObject.getString("Type");
                                    String Content = jsonObject.getString("Subject");
                                    String Title = jsonObject.getString("Title");
                                    String Date = jsonObject.getString("Date");
                                    String Time = jsonObject.getString("Time");
                                    String submittedCount = jsonObject.getString("SubmittedCount");
                                    String TotalCount = jsonObject.getString("SentBy");
                                    String isAppRead = jsonObject.getString("isAppRead");
                                    String EndDate = jsonObject.getString("EndDate");
                                    String DeTailId = jsonObject.getString("DeTailId");
                                    String category = jsonObject.getString("category");

                                    AssignmentViewClass report = new AssignmentViewClass(AssignmentId, Type, Content, Title, Date, Time, submittedCount,TotalCount,EndDate,isAppRead,DeTailId,false,category);
                                    assignlist.add(report);
                                    totalassignlist.add(report);
                                }
                                else{
                                    if(isNewVersion.equals("1")){
                                        lblNoMessages.setVisibility(View.VISIBLE);
                                        lblNoMessages.setText(Message);

                                        String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodAssignmentParent(ParentAssignmentListActivity.this);
                                        if(loadMoreCall.equals("1")){
                                            TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(ParentAssignmentListActivity.this,"");
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

    private File writeErrorlogFile(String errorLogs) {

        File file = null;
        String newPath = Environment.getExternalStorageDirectory()+"/voicesnapErrorLog/";
        try {
            File f = new File(newPath);
            f.mkdirs();
            FileInputStream fin = openFileInput("logs");
            FileOutputStream fos = new FileOutputStream(newPath + "logs");
            //byte[] buffer = new byte[1024];
            byte[] buffer = errorLogs.getBytes();
            int len1 = 0;
            while ((len1 = fin.read(buffer)) != -1) {
                fos.write(buffer, 0, len1);
            }
            fin.close();
            fos.close();
            file = new File(newPath + "logs");
            if (file.exists())
                return file;
        } catch (Exception e) {

        }
        return null;
    }


    private void showToast(String msg) {
        Toast.makeText(ParentAssignmentListActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParentAssignmentListActivity.this);
            alertDialog.setTitle(R.string.alert);
            alertDialog.setMessage(strStudName);
            alertDialog.setCancelable(false);
            alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   finish();
                   // dialog.cancel();

                }
            });

            alertDialog.show();

    }

    @Override
    public void delete() {
        ViewAllAssignmentListByStaff();
    }
}
