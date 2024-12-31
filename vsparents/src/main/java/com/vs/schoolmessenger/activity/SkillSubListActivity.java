package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_LSRW;

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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.SkillSublistAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.SkillAttachmentModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SkillSubListActivity extends AppCompatActivity {
    public ArrayList<SkillAttachmentModel> msgModelList = new ArrayList<>();
    RecyclerView recycleview;
    SkillSublistAdapter textAdapter;
    String skillID;
    Button btnNext;


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
        setContentView(R.layout.activity_l_s_r_w_list);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.LSRW);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ImageView ivBack = (ImageView) findViewById(R.id.text_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recycleview = (RecyclerView) findViewById(R.id.recycleview);
        TextView tvTitle = (TextView) findViewById(R.id.text_ToolBarTvTitle);
        btnNext = findViewById(R.id.btnNext);
        tvTitle.setText("LSRW");

        skillID = getIntent().getStringExtra("skillID");
        String isappread = getIntent().getStringExtra("isappread");
        String detailid = getIntent().getStringExtra("detailid");
        Log.d("skillID", skillID);
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(SkillSubListActivity.this);

        if (isappread.equals("0")) {
            ChangeMsgReadStatus.changeReadStatus(SkillSubListActivity.this, detailid, MSG_TYPE_LSRW, "", isNewVersion, false, new OnRefreshListener() {
                @Override
                public void onRefreshItem() {
                }
            });
        }
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recycleview.setLayoutManager(layoutManager);
        recycleview.setItemAnimator(new DefaultItemAnimator());
        textAdapter = new SkillSublistAdapter(this, msgModelList);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SkillSubListActivity.this, ParentSubmitLSRW.class);
                i.putExtra("skillid", skillID);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getAttachment();

    }

    private void prepareAlbums() {

        SkillAttachmentModel a = new SkillAttachmentModel("1", "https://school-app-files.s3.amazonaws.com/File_20210531110627_File_20210525211810_20210324_182541.png", "IMAGE", "1", "21/05/2021");
        msgModelList.add(a);

        a = new SkillAttachmentModel("2", "https://school-master-documents.s3.ap-south-1.amazonaws.com/File_20210619074425_schoolVoice.mp3", "VOICE", "English", "21/05/2021");
        msgModelList.add(a);

        a = new SkillAttachmentModel("3", "Title test one two 3", "TEXT", "3", "21/05/2021");
        msgModelList.add(a);

        a = new SkillAttachmentModel("4", "https://school-app-files.s3.amazonaws.com/File_20210529104055_NEWSCHOOLAPP4119162045852217803.pdf", "PDF", "3", "21/05/2021");
        msgModelList.add(a);

        a = new SkillAttachmentModel("5", "https://player.vimeo.com/video/564458606?title\\u003d0\\u0026amp;byline\\u003d0\\u0026amp;portrait\\u003d0\\u0026amp;speed\\u003d0\\u0026amp;badge\\u003d0\\u0026amp;autopause\\u003d0\\u0026amp;player_id\\u003d0\\u0026amp;app_id\\u003d177030", "VIDEO", "3", "21/05/2021");
        msgModelList.add(a);

        recycleview.setAdapter(textAdapter);
        textAdapter.notifyDataSetChanged();
    }

    private void getAttachment() {

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
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("StudentID", strChildID);
        jsonObject.addProperty("SkillId", skillID);

        Log.d("GetAttachmentForSkill", jsonObject.toString());


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.GetAttachmentForSkill(jsonObject);
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("LSRW:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("LSRW:Res", response.body().toString());

                try {
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    String status = jsonObject.getString("Status");
                    String message = jsonObject.getString("Message");

                    if (status.equals("1")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        textAdapter.clearAllData();
                        msgModelList.clear();
                        SkillAttachmentModel msgModel;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject js = data.getJSONObject(i);
                            msgModel = new SkillAttachmentModel(js.getString("ContentId"), js.getString("Attachment"), js.getString("Type"), js.getString("Order"), js.getString("SkillId"));
                            msgModelList.add(msgModel);

                        }
                        recycleview.setAdapter(textAdapter);
                        textAdapter.notifyDataSetChanged();

                    } else {
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

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
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }


}