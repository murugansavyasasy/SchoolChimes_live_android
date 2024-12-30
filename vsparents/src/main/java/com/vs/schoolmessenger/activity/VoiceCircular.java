package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.Util_Common.MENU_EMERGENCY;
import static com.vs.schoolmessenger.util.Util_Common.MENU_HW;
import static com.vs.schoolmessenger.util.Util_Common.MENU_VOICE;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.VoiceCircularListAdapterNEW;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class VoiceCircular extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;
    private final String[] android_image_urls = {
            "https://www.hrupin.com/wp-content/uploads/mp3/testsong_20_sec.mp3",
            "http://9xmusiq.com/songs/Tamil%20Songs/2016%20Tamil%20Mp3/Devi%20(2016)/Chalmaar%20%5bStarmusiq.cc%5d.mp3"
    };
    private final String[] android_image_status = {"1", "0"};
    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    public ArrayList<MessageModel> totalMsgList = new ArrayList<>();
    public ArrayList<MessageModel> OffLinemsgModelList = new ArrayList<>();
    RecyclerView rvVoiceList;
    VoiceCircularListAdapterNEW voiceAdapter;
    String selDate;
    ArrayList<MessageModel> arrayList;
    Boolean is_Archive;
    Calendar c;
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    ImageView imgSearch;
    EditText Searchable;
    Slider slider;
    ImageView adImage;
    AdView mAdView;
    RelativeLayout voice_rlToolbar;
    private int iRequestCode;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_circular);
        c = Calendar.getInstance();
        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);


        Log.d("iRequestCode", String.valueOf(iRequestCode));
        if (iRequestCode != MENU_EMERGENCY) {
            selDate = getIntent().getExtras().getString("SEL_DATE", "");
        } else {
            selDate = "Emergency Voice";
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(selDate);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");


        Slider.init(new PicassoImageLoadingService(VoiceCircular.this));
        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);


        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);
        voice_rlToolbar = (RelativeLayout) findViewById(R.id.voice_rlToolbar);
        voice_rlToolbar.setVisibility(View.GONE);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (voiceAdapter == null)
                    return;

                if (voiceAdapter.getItemCount() < 1) {
                    rvVoiceList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvVoiceList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvVoiceList.setVisibility(View.VISIBLE);
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


        LoadMore = (TextView) findViewById(R.id.btnSeeMore);
        LoadMore.setEnabled(true);
        lblNoMessages = (TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (iRequestCode == MENU_EMERGENCY) {
                    LoadMorecircularsEmergencyAPI();
                }

            }
        });

        is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VoiceCircular.this);

        seeMoreButtonVisiblity();

        ImageView ivBack = (ImageView) findViewById(R.id.voice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackController();
                onBackPressed();
            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.voice_ToolBarTvTitle);
        tvTitle.setText(selDate);
        rvVoiceList = (RecyclerView) findViewById(R.id.voice_rvCircularList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rvVoiceList.setLayoutManager(layoutManager);
        rvVoiceList.setItemAnimator(new DefaultItemAnimator());
        voiceAdapter = new VoiceCircularListAdapterNEW(VoiceCircular.this, msgModelList);
        rvVoiceList.setAdapter(voiceAdapter);
    }

    private void filterlist(String s) {
        ArrayList<MessageModel> temp = new ArrayList();
        for (MessageModel d : msgModelList) {

            if (d.getMsgdescription().toLowerCase().contains(s.toLowerCase()) || d.getMsgTitle().toLowerCase().contains(s.toLowerCase()) || d.getMsgDate().toLowerCase().contains(s.toLowerCase())) {
                temp.add(d);
            }

        }
        voiceAdapter.updateList(temp);
    }


    private void onBackController() {
        if (iRequestCode == MENU_VOICE) {
            TeacherUtil_SharedPreference.putOnBackPressedVoice(VoiceCircular.this, "1");
        } else if (iRequestCode == MENU_HW) {
            TeacherUtil_SharedPreference.putOnBackPressedHWTEXT(VoiceCircular.this, "1");
        }
    }

    private void seeMoreButtonVisiblity() {
        if (isNewVersion.equals("1") && iRequestCode == MENU_EMERGENCY) {
            LoadMore.setVisibility(View.VISIBLE);
        }

    }


    private void LoadMorecircularsEmergencyAPI() {
        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VoiceCircular.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(VoiceCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(VoiceCircular.this);
        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(VoiceCircular.this);


        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID, "VOICE", MobileNumber);
        Call<JsonArray> call = apiService.LoadMoreGetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());


                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    OffLinemsgModelList.clear();


                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", String.valueOf(js.length()));


                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"), jsonObject.getBoolean("is_Archive"));


                                msgModelList.add(msgModel);
                                OffLinemsgModelList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            voiceAdapter.notifyDataSetChanged();

                        } else {
                            showAlertRecords(strMessage);
                        }
                    } else {
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackController();
            onBackPressed();
            return (true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this, adImage, slider, "", mAdView);
        switch (iRequestCode) {
            case MENU_EMERGENCY:

                if (isNetworkConnected()) {
                    circularsEmergencyAPI();
                }

                break;

            case MENU_VOICE:
                if (isNetworkConnected()) {
                    circularsVoicebydateAPI();

                }

                break;

            case MENU_HW:

                if (isNetworkConnected()) {
                    circularsHomeworkbydateAPI();
                }


                break;
        }

    }

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) VoiceCircular.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        onBackController();
        finish();
    }


    private void circularsEmergencyAPI() {
        String isNewVersionn = TeacherUtil_SharedPreference.getNewVersion(VoiceCircular.this);
        if (isNewVersionn.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(VoiceCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(VoiceCircular.this);
        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(VoiceCircular.this);


        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetEmergencyvoice(strChildID, strSchoolID, "VOICE", MobileNumber);
        Call<JsonArray> call = apiService.GetEmergencyVoiceOrImageOrPDF(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (isNewVersion.equals("1") && iRequestCode == MENU_EMERGENCY) {
                            LoadMore.setVisibility(View.VISIBLE);
                        } else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        voiceAdapter.clearAllData();
                        totalMsgList.clear();
                        msgModelList.clear();

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", String.valueOf(js.length()));

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("MessageID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"), false);


                                msgModelList.add(msgModel);
                                totalMsgList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            voiceAdapter.notifyDataSetChanged();

                        } else {

                            if (isNewVersion.equals("1")) {
                                lblNoMessages.setVisibility(View.VISIBLE);
                                lblNoMessages.setText(strMessage);

                                String loadMoreCall = TeacherUtil_SharedPreference.getOnBackMethodEmeVoice(VoiceCircular.this);
                                if (loadMoreCall.equals("1")) {
                                    TeacherUtil_SharedPreference.putOnBackPressedEmeVoice(VoiceCircular.this, "");
                                    LoadMorecircularsEmergencyAPI();
                                }
                            } else {
                                lblNoMessages.setVisibility(View.GONE);
                                showAlertRecords(strMessage);
                            }


                        }
                    } else {
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }


                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void showAlertRecords(String no_records_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(VoiceCircular.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_records_found);
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


    private void circularsVoicebydateAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VoiceCircular.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(VoiceCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(VoiceCircular.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetGeneralvoiceortext(strChildID, strSchoolID, "VOICE", selDate);

        Call<JsonArray> call;
        if (isNewVersion.equals("1") && is_Archive) {
            call = apiService.GetFiles_Archive(jsonReqArray);
        } else {
            call = apiService.GetFiles(jsonReqArray);
        }

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        voiceAdapter.clearAllData();
                        msgModelList.clear();

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"), jsonObject.getString("Description"), is_Archive);
                                msgModelList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);
                            voiceAdapter.notifyDataSetChanged();

                        } else {
                            showAlertRecords(strMessage);
                        }
                    } else {
                        showAlertRecords(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }


    private void circularsHomeworkbydateAPI() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VoiceCircular.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(VoiceCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(VoiceCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(VoiceCircular.this);
        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(VoiceCircular.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Gethomework(strChildID, selDate, "VOICE", strSchoolID, MobileNumber);


        Call<JsonArray> call;
        if (isNewVersion.equals("1") && is_Archive) {
            call = apiService.GetHomeWorkFiles_Archive(jsonReqArray);
        } else {
            call = apiService.GetHomeWorkFiles(jsonReqArray);
        }
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        MessageModel msgModel;
                        voiceAdapter.clearAllData();

                        for (int i = 0; i < js.length(); i++) {
                            jsonObject = js.getJSONObject(i);
                            msgModel = new MessageModel(jsonObject.getString("HomeworkID"), jsonObject.getString("HomeworkSubject"),
                                    jsonObject.getString("HomeworkContent"), "",
                                    selDate, "", jsonObject.getString("HomeworkTitle"), false);

                            msgModelList.add(msgModel);
                        }


                        arrayList = new ArrayList<>();
                        arrayList.addAll(msgModelList);

                        voiceAdapter.notifyDataSetChanged();


                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void showToast(String msg) {
        Toast.makeText(VoiceCircular.this, msg, Toast.LENGTH_SHORT).show();
    }

    public boolean isWriteExternalPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
                return true;
            } else {

                Log.v("WRITE_EXTERNAL_STORAGE", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("WRITE_EXTERNAL_STORAGE", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }
}
