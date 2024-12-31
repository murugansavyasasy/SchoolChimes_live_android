package com.vs.schoolmessenger.assignment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_ASSIGNMENT;

public class PdfListAcivity extends AppCompatActivity {
     RecyclerView recyclerView;
     ImageView imgBack;
     PdfListAdapter pdflistadapter;
     TextView lblPdf;
    ArrayList<String> imagelist = new ArrayList<>();
    ArrayList<String> descriptionlist = new ArrayList<>();
    String assignmentId, assignmentType, detailid, Content,type,userType,FileType,userID,isappread,date;

    String isNewVersion;
    Boolean is_Archive;


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
        setContentView(R.layout.activity_pdf_list_acivity);

        recyclerView = findViewById(R.id.recyclerView);
        imgBack =findViewById(R.id.imgBack);
        lblPdf =findViewById(R.id.lblPdf);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(PdfListAcivity.this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 80);

        assignmentId=getIntent().getExtras().getString("ID","");
        type=getIntent().getExtras().getString("TYPE","");
        userType = getIntent().getExtras().getString("USER_TYPE","");
        userID = getIntent().getExtras().getString("USER_ID","");
        FileType  = getIntent().getExtras().getString("FileType","");
        isappread  = getIntent().getExtras().getString("isappread","");
        date  = getIntent().getExtras().getString("date","");
        detailid  = getIntent().getExtras().getString("detailid","");

        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(PdfListAcivity.this);
        is_Archive  = getIntent().getExtras().getBoolean("is_Archive",false);

        lblPdf.setText("PDF");
        if(userType.equals("parent")){
            userID= Util_SharedPreference.getChildIdFromSP(PdfListAcivity.this);
        }
        else if(userType.equals("staff")){
            userID= TeacherUtil_Common.Principal_staffId;
        }
        if(userType.equals("parent")&& isappread.equals("0")){
            ChangeMsgReadStatus.changeReadStatus(PdfListAcivity.this, detailid, MSG_TYPE_ASSIGNMENT,date,isNewVersion,is_Archive, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
//                    pdfModel.setMsgReadStatus("1");
//                    if (pdfModel.getMsgReadStatus().equals("0"))
//                        tvStatus.setVisibility(View.VISIBLE);
//                    else tvStatus.setVisibility(View.GONE);
                }
            });

        }
        pdflistadapter = new PdfListAdapter(PdfListAcivity.this, imagelist,descriptionlist,assignmentId,userID);
        recyclerView.setAdapter(pdflistadapter);
        onBackControl();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackControl();
                onBackPressed();
            }
        });
    }

    private void onBackControl() {
        if(userType.equals("parent")){
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentParent(PdfListAcivity.this,"1");
        }
        else {
            TeacherUtil_SharedPreference.putOnBackPressedAssignmentStaff(PdfListAcivity.this,"1");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackControl();
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewAssignmentContent();
    }
    private void ViewAssignmentContent() {
        final ProgressDialog mProgressDialog = new ProgressDialog(PdfListAcivity.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(PdfListAcivity.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(PdfListAcivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(PdfListAcivity.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        JsonObject object = new JsonObject();
        object.addProperty("ProcessBy", userID);
        object.addProperty("AssignmentId", assignmentId);
        object.addProperty("Type", type);
        object.addProperty("FileType", FileType );
        Log.d("Upload:Req", "" + object.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call;
       // if(isNewVersion.equals("1") && is_Archive && userType.equals("parent")){
        if(isNewVersion.equals("1") && is_Archive){
            call = apiService.ViewAssignmentContent_Archive(object);
        }
        else {
            call = apiService.ViewAssignmentContent(object);
        }

        //Call<JsonArray> call = apiService.ViewAssignmentContent(object);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());
                    try {
                        Log.d("FileResponse:Res", response.toString());
                        int statusCode = response.code();
                        Log.d("Status Code - Response", statusCode + " - " + response.body());
                        String strResponse = response.body().toString();

                        JSONArray js = new JSONArray(response.body().toString());
                        imagelist.clear();
                        descriptionlist.clear();
                        if (js.length() > 0) {
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String result = jsonObject.getString("result");
                                String Message = jsonObject.getString("Message");

                                if (result.equals("1")) {
                                    Content = jsonObject.getString("Content");
                                    String description = jsonObject.getString("description");
                                    imagelist.add(Content);
                                    descriptionlist.add(description);
                                } else {
                                    alert(Message);
                                }
                            }
                            pdflistadapter.notifyDataSetChanged();
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
        Toast.makeText(PdfListAcivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    private void alert(String strStudName) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PdfListAcivity.this);


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
}
