package com.vs.schoolmessenger.activity;

import androidx.appcompat.app.ActionBar;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.LSRWAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.lsrwModelClass;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class LSRWListActivity extends AppCompatActivity {


    public ArrayList<lsrwModelClass> msgModelList = new ArrayList<>();
    RecyclerView recycleview;
    LSRWAdapter textAdapter;
    Boolean show=false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.feespending_recycle);


        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("LSRW");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        recycleview = (RecyclerView) findViewById(R.id.fees_pending_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleview.setLayoutManager(layoutManager);
        recycleview.setItemAnimator(new DefaultItemAnimator());
        textAdapter = new LSRWAdapter(this,msgModelList);

    }

    @Override
    public void onResume() {
        super.onResume();
        getLsrwListApi();

    }


    private void getLsrwListApi() {

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
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StudentID", strChildID);
        jsonObject.addProperty("SchoolID", strSchoolID);

        Log.d("LSRWRequest", jsonObject.toString());


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetLSRWlist(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("LSRW:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("LSRW:Res", response.body().toString());
                textAdapter.clearAllData();
                msgModelList.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    String status = jsonObject.getString("Status");
                    String message = jsonObject.getString("Message");

                    if (status.equals("1")) {
                        JSONArray data = jsonObject.getJSONArray("data");

                        lsrwModelClass msgModel;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject js = data.getJSONObject(i);
                            msgModel = new lsrwModelClass(js.getString("SkillId"),
                                    js.getString("Title"),
                                    js.getString("Description"),
                                    js.getString("Subject"),
                                    js.getString("SubmittedOn"),
                                    js.getString("Issubmitted"),
                                    js.getString("isAppRead"),
                                    js.getString("SentBy"),
                                    js.getString("ActivityType"),
                                    js.getString("detailId")
                            );
                            msgModelList.add(msgModel);

                        }
                        recycleview.setAdapter(textAdapter);
                        textAdapter.notifyDataSetChanged();

                    } else {
                        recycleview.setAdapter(textAdapter);
                        textAdapter.notifyDataSetChanged();
                        if(show==false) {
                            showAlertRecords(message);
                        }

                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                    showToast(e.getMessage());

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
        show=true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(msg);

        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                show=false;
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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }
}