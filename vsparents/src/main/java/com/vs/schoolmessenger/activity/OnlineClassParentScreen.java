package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.OnlineClassAdapter;
import com.vs.schoolmessenger.interfaces.OnItemClickOnlineClass;
import com.vs.schoolmessenger.interfaces.OnMsgItemClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.OnlineClassModel;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public  class OnlineClassParentScreen extends AppCompatActivity implements OnItemClickOnlineClass {


    public ArrayList<OnlineClassModel> msgModelList = new ArrayList<>();
    RecyclerView rvTextMsgList;
    OnlineClassAdapter textAdapter;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.online_class_recycle);

        ImageView ivBack = (ImageView) findViewById(R.id.text_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        rvTextMsgList = (RecyclerView) findViewById(R.id.text_rvCircularList);
        TextView tvTitle = (TextView) findViewById(R.id.text_ToolBarTvTitle);
        tvTitle.setText("Online Meetings");



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvTextMsgList.setLayoutManager(layoutManager);
        rvTextMsgList.setItemAnimator(new DefaultItemAnimator());
        textAdapter = new OnlineClassAdapter(msgModelList, OnlineClassParentScreen.this, new OnItemClickOnlineClass() {
            @Override
            public void onMsgItemClick(OnlineClassModel item) {
                Intent inTextPopup = new Intent(OnlineClassParentScreen.this, OnlineClassPopup.class);
                inTextPopup.putExtra("TEXT_ITEM", item);
                startActivity(inTextPopup);
            }
        });
        rvTextMsgList.setAdapter(textAdapter);

    }

    @Override
    public void onResume(){
        super.onResume();
        getOnlineClasses();

    }

    private void getOnlineClasses() {

        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(OnlineClassParentScreen.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(OnlineClassParentScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(OnlineClassParentScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(OnlineClassParentScreen.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(OnlineClassParentScreen.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("school_id", strSchoolID);
        jsonObject.addProperty("member_id", strChildID);

        Log.d("Request",jsonObject.toString());


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.getOnlineClasses(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    String status=jsonObject.getString("status");
                    String message=jsonObject.getString("message");

                    if(status.equals("1")){
                        JSONArray data=jsonObject.getJSONArray("data");

                        textAdapter.clearAllData();
                        msgModelList.clear();

                        OnlineClassModel msgModel;
                        for (int i = 0; i < data.length(); i++) {
                          JSONObject  js = data.getJSONObject(i);
                            msgModel = new OnlineClassModel(js.getString("header_id"), js.getString("message_id"),
                                    js.getString("topic"),js.getString("description"), js.getString("url"),js.getString("meetingid"),js.getString("meetingtype"),
                                    js.getString("meetingdate"),js.getString("meetingtime"),js.getString("meetingdatetime"),js.getString("staff_name"),
                                    js.getString("subject_name"), js.getString("is_app_viewed"));
                            msgModelList.add(msgModel);
                        }


                        textAdapter.notifyDataSetChanged();


                    }
                    else {
                        showAlertRecords(message);

                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showAlertRecords(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OnlineClassParentScreen.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
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

    private void showToast(String msg) {
        Toast.makeText(OnlineClassParentScreen.this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMsgItemClick(OnlineClassModel item) {
        
    }
}