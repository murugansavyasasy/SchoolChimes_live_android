package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.CircularsDateListAdapter;
import com.vs.schoolmessenger.app.AppController;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.CircularDates;
import com.vs.schoolmessenger.model.Languages;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.LanguageIDAndNames;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class MessageDatesScreen extends AppCompatActivity implements View.OnClickListener {

    String strChildName;
    Profiles childItem = new Profiles();

    int iTotMsgUnreadCount = 0;
    TextView tvSchoolName, tvSchoolAddress, tvMsgCount;
    NetworkImageView nivThumbNailSchoolImg;
    ImageLoader imageLoader;

    RecyclerView rvDatesList;
    private CircularsDateListAdapter dateListAdapter;
    private ArrayList<CircularDates> datesList = new ArrayList<>();
    private ArrayList<CircularDates> totaldatesList = new ArrayList<>();
    private ArrayList<CircularDates> OfflinedatesList = new ArrayList<>();


    SqliteDB myDb;
    ArrayList<CircularDates> arrayList;

    private PopupWindow pHelpWindow;
    RelativeLayout rytHome,rytLanguage, rytPassword,rytHelp,rytLogout;
    ArrayList<Languages> LanguageList = new ArrayList<Languages>();
    String IDs = "";
    ArrayList<TeacherSchoolsModel> schools_list = new ArrayList<TeacherSchoolsModel>();
    private ArrayList<Profiles> childList = new ArrayList<>();
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;

    ImageView imgSearch;
    EditText Searchable;

    Slider slider;
    ImageView adImage;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_dates_screen);
        c = Calendar.getInstance();
        childItem = (Profiles) getIntent().getSerializableExtra("Profiles");
        Util_SharedPreference.putSelecedChildInfoToSP(MessageDatesScreen.this, childItem.getChildID(), childItem.getChildName(), childItem.getSchoolID(),
                childItem.getSchoolName(), childItem.getSchoolAddress(), childItem.getSchoolThumbnailImgUrl(),childItem.getStandard(),childItem.getSection());

        strChildName = childItem.getChildName();

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_dates);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_acTitle)).setText("Homework");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Slider.init(new PicassoImageLoadingService(MessageDatesScreen.this));
        slider = findViewById(R.id.banner);
         adImage = findViewById(R.id.adImage);

        LoadMore=(TextView) findViewById(R.id.btnSeeMore);
        lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMorerecentCircularsDateWiseAPI();

            }
        });

         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);

        seeMoreButtonVisiblity();


        rytLanguage = (RelativeLayout) findViewById(R.id.rytLanguage);
        rytHome = (RelativeLayout) findViewById(R.id.rytHome);
        rytHelp = (RelativeLayout) findViewById(R.id.rytHelp);
        rytPassword = (RelativeLayout) findViewById(R.id.rytPassword);
        rytLogout = (RelativeLayout) findViewById(R.id.rytLogout);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);

        rytLogout.setOnClickListener(this);
        rytHelp.setOnClickListener(this);
        rytLanguage.setOnClickListener(this);
        rytPassword.setOnClickListener(this);
        rytHome.setOnClickListener(this);

        Searchable.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (dateListAdapter == null)
                    return;

                if (dateListAdapter.getItemCount() < 1) {
                    rvDatesList.setVisibility(View.GONE);
                    if (Searchable.getText().toString().isEmpty()) {
                        rvDatesList.setVisibility(View.VISIBLE);
                    }

                } else {
                    rvDatesList.setVisibility(View.VISIBLE);
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




        tvMsgCount = (TextView) findViewById(R.id.dates_tvMsgCount);
        tvSchoolAddress = (TextView) findViewById(R.id.dates_tvSchoolAddress);
        tvSchoolName = (TextView) findViewById(R.id.dates_tvSchoolName);
        tvMsgCount.setText(childItem.getMsgCount());
        tvSchoolName.setText(childItem.getSchoolName());
        tvSchoolAddress.setText(childItem.getSchoolAddress());


        nivThumbNailSchoolImg = (NetworkImageView) findViewById(R.id.dates_nivThumbnailSchoolImg);
        imageLoader = AppController.getInstance().getImageLoader();
        nivThumbNailSchoolImg.setImageUrl(childItem.getSchoolThumbnailImgUrl(), imageLoader);

        rvDatesList = (RecyclerView) findViewById(R.id.dates_rvChildList);
        dateListAdapter = new CircularsDateListAdapter(MessageDatesScreen.this, datesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvDatesList.setHasFixedSize(true);
        rvDatesList.setLayoutManager(mLayoutManager);
        rvDatesList.setItemAnimator(new DefaultItemAnimator());
        rvDatesList.setAdapter(dateListAdapter);
    }

    private void filterlist(String s) {
        List<CircularDates> temp = new ArrayList();
        for (CircularDates d : datesList) {

            if (d.getCircularDate().toLowerCase().contains(s.toLowerCase()) || d.getCircularDay().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        dateListAdapter.updateList(temp);
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

    private void LoadMorerecentCircularsDateWiseAPI() {
        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        final String strChildID = childItem.getChildID();
        final String strSchoolID = childItem.getSchoolID();
        Log.d("MsgDates:Child-SchoolID", strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetMessageCount(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.LoadMoreGetHomeWorkCount(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("MsgDates:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("MsgDates:Res", response.body().toString());
                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);


                        String strDate = jsonObject.getString("HomeworkDate");
                        String strTotalSMS = jsonObject.getString("TextCount");

                        if (!strDate.equals("")) {
                            CircularDates cirDates;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                cirDates = new CircularDates(jsonObject.getString("HomeworkDate"), jsonObject.getString("HomeworkDay"),
                                        jsonObject.getString("VoiceCount"),"0",
                                        jsonObject.getString("TextCount"),"0",jsonObject.getBoolean("is_Archive"));
                                datesList.add(cirDates);
                                OfflinedatesList.add(cirDates);

                                iTotMsgUnreadCount = iTotMsgUnreadCount + Integer.parseInt(jsonObject.getString("VoiceCount"))
                                        + Integer.parseInt(jsonObject.getString("TextCount")) ;

                            }

                            tvMsgCount.setText("" + iTotMsgUnreadCount);
                            arrayList = new ArrayList<>();
                            arrayList.addAll(datesList);
                            dateListAdapter.notifyDataSetChanged();

                        } else {
                            showrecordsFound(strTotalSMS);
                        }
                    } else {
                        showrecordsFound(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("MsgDates:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("MsgDates:Failure", t.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this,adImage,slider,"");

        if (isNetworkConnected()) {
            recentCircularsDateWiseAPI();
        }


    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageDatesScreen.this);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.alert);

        alertDialog.setMessage(R.string.connect_internet);
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

    private boolean isNetworkConnected() {

        ConnectivityManager connMgr = (ConnectivityManager) MessageDatesScreen.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private void showToast(String msg) {
        Toast.makeText(MessageDatesScreen.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void getAllCirculars() {
//        for (int i = 0; i < 2; i++)
        {
            CircularDates circular = new CircularDates();
            circular.setCircularDate("05 Jan 2017");
            circular.setCircularDay("Monday");
            circular.setVoiceTotCount("2");
            circular.setVoiceUnreadCount("40");
            circular.setTextTotCount("3");
            circular.setTextUnreadCount("55");
            circular.setImageTotCount("1");
            circular.setImageUnreadCount("10");
            circular.setPdfTotCount("10");
            circular.setPdfUnreadCount("10");
            datesList.add(circular);

            circular = new CircularDates();
            circular.setCircularDate("06 May 2017");
            circular.setCircularDay("Tuesday");
            circular.setVoiceTotCount("1");
            circular.setVoiceUnreadCount("1");
            circular.setTextTotCount("2");
            circular.setTextUnreadCount("2");
            circular.setImageTotCount("1");
            circular.setImageUnreadCount("1");
            circular.setPdfTotCount("1");
            circular.setPdfUnreadCount("0");
            datesList.add(circular);
        }
    }

    private void recentCircularsDateWiseAPI2() {
        try {
            JSONArray js = new JSONArray("[{\"Date\":\"17-01-2018\",\"Day\":\"Wednesday\",\"TotalSMS\":\"0\",\"UnreadSMS\":\"0\",\"TotalPDF\":\"0\",\"UnreadPDF\":\"0\",\"TotalIMG\":\"0\",\"UnreadIMG\":\"0\",\"TotalVOICE\":\"1\",\"UnreadVOICE\":\"0\"},{\"Date\":\"13-01-2018\",\"Day\":\"Saturday\",\"TotalSMS\":\"0\",\"UnreadSMS\":\"0\",\"TotalPDF\":\"0\",\"UnreadPDF\":\"0\",\"TotalIMG\":\"0\",\"UnreadIMG\":\"0\",\"TotalVOICE\":\"1\",\"UnreadVOICE\":\"0\"},{\"Date\":\"05-01-2018\",\"Day\":\"Friday\",\"TotalSMS\":\"1\",\"UnreadSMS\":\"0\",\"TotalPDF\":\"1\",\"UnreadPDF\":\"0\",\"TotalIMG\":\"2\",\"UnreadIMG\":\"2\",\"TotalVOICE\":\"2\",\"UnreadVOICE\":\"0\"}]");
            if (js.length() > 0) {
                JSONObject jsonObject = js.getJSONObject(0);
                String strDate = jsonObject.getString("Date");
                String strTotalSMS = jsonObject.getString("TotalSMS");

                if (!strDate.equals("")) {
                    CircularDates cirDates;
                    Log.d("json length", js.length() + "");

                    dateListAdapter.clearAllData();
                    iTotMsgUnreadCount = 0;

                    for (int i = 0; i < js.length(); i++) {
                        jsonObject = js.getJSONObject(i);
                        cirDates = new CircularDates(jsonObject.getString("Date"), jsonObject.getString("Day"),
                                jsonObject.getString("TotalVOICE"), jsonObject.getString("UnreadVOICE"),
                                jsonObject.getString("TotalSMS"), jsonObject.getString("UnreadSMS"),
                                jsonObject.getString("TotalIMG"), jsonObject.getString("UnreadIMG"),
                                jsonObject.getString("TotalPDF"), jsonObject.getString("UnreadPDF"),false);
                        datesList.add(cirDates);

                        iTotMsgUnreadCount = iTotMsgUnreadCount + Integer.parseInt(jsonObject.getString("UnreadVOICE"))
                                + Integer.parseInt(jsonObject.getString("UnreadSMS")) + Integer.parseInt(jsonObject.getString("UnreadIMG"))
                                + Integer.parseInt(jsonObject.getString("UnreadPDF"));
                    }

                    tvMsgCount.setText("" + iTotMsgUnreadCount);
                    dateListAdapter.notifyDataSetChanged();

                } else {
                    showToast(strTotalSMS);
                }
            } else {
                showToast("Server Response Failed. Try again");
            }

        } catch (Exception e) {
            Log.e("MsgDates:Exception", e.getMessage());
        }
    }

    private void recentCircularsDateWiseAPI() {
        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(MessageDatesScreen.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(MessageDatesScreen.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        final String strChildID = childItem.getChildID();
        final String strSchoolID = childItem.getSchoolID();
        Log.d("MsgDates:Child-SchoolID", strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetMessageCount(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetHomeWorkCount(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("MsgDates:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("MsgDates:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);


                        String strDate = jsonObject.getString("HomeworkDate");
                        String strTotalSMS = jsonObject.getString("TextCount");

                        dateListAdapter.clearAllData();
                        iTotMsgUnreadCount = 0;
                        totaldatesList.clear();
                        datesList.clear();

                        if(isNewVersion.equals("1")){
                            LoadMore.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        if (!strDate.equals("")) {
                            CircularDates cirDates;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                cirDates = new CircularDates(jsonObject.getString("HomeworkDate"), jsonObject.getString("HomeworkDay"),
                                        jsonObject.getString("VoiceCount"),"0",
                                        jsonObject.getString("TextCount"),"0",false);
                                datesList.add(cirDates);
                                totaldatesList.add(cirDates);

                                iTotMsgUnreadCount = iTotMsgUnreadCount + Integer.parseInt(jsonObject.getString("VoiceCount"))
                                        + Integer.parseInt(jsonObject.getString("TextCount")) ;

                            }

                            tvMsgCount.setText("" + iTotMsgUnreadCount);


                            arrayList = new ArrayList<>();
                            arrayList.addAll(datesList);
                            dateListAdapter.notifyDataSetChanged();

                        } else {

                            if(isNewVersion.equals("1")){
                                lblNoMessages.setVisibility(View.VISIBLE);
                                lblNoMessages.setText(strTotalSMS);

                                String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethodHWTEXT(MessageDatesScreen.this);
                                if(loadMoreCall.equals("1")){
                                    TeacherUtil_SharedPreference.putOnBackPressedHWTEXT(MessageDatesScreen.this,"");
                                    LoadMorerecentCircularsDateWiseAPI();
                                }
                            }
                            else {
                                lblNoMessages.setVisibility(View.GONE);
                                showrecordsFound(strTotalSMS);
                            }



                        }
                    } else {
                        showrecordsFound(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("MsgDates:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("MsgDates:Failure", t.toString());
            }
        });
    }

    private void showrecordsFound(String s) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MessageDatesScreen.this);

        alertDialog.setTitle(R.string.alert);

        //Setting Dialog Message
        alertDialog.setMessage(s);


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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rytHome:

                Intent homescreen=new Intent(MessageDatesScreen.this,HomeActivity.class);
                homescreen.putExtra("HomeScreen","1");
                homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homescreen);
                finish();

                break;
            case R.id.rytHelp:

                Intent faq=new Intent(MessageDatesScreen.this,FAQScreen.class);
                startActivity(faq);



                break;
            case R.id.rytLanguage:
                showLanguageListPopup();
                break;
            case R.id.rytPassword:
                Util_SharedPreference.putForget(MessageDatesScreen.this, "change");
                startActivity(new Intent(MessageDatesScreen.this, TeacherChangePassword.class));

                break;
            case R.id.rytLogout:

                Util_Common.popUpMenu(MessageDatesScreen.this,v,"1");


                break;


        }
    }

    private void setupHelpPopUp() {

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.popup_help_txt, null);

        pHelpWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pHelpWindow.setContentView(layout);

        ImageView ivClose = (ImageView) layout.findViewById(R.id.popupHelp_ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pHelpWindow.dismiss();
                hideKeyBoard();
            }
        });

        final EditText etmsg = (EditText) layout.findViewById(R.id.popupHelp_etMsg);



        final TextView tvTxtCount = (TextView) layout.findViewById(R.id.popupHelp_tvTxtCount);
        etmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTxtCount.setText("" + (460 - (s.length())));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView tvSend = (TextView) layout.findViewById(R.id.popupHelp_tvSend);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMsg = etmsg.getText().toString().trim();

                if (strMsg.length() > 0)
                    helpAPI(strMsg);
                else
                    showToast(getResources().getString(R.string.enter_message));
            }
        });

    }

    private void helpAPI(String strMsg) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(MessageDatesScreen.this);


        Log.d("Help:Mob-Query", mobNumber + " - " + strMsg);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetHelp(mobNumber, strMsg);
        Call<JsonArray> call = apiService.GetHelpnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Help:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Help:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if ((strStatus.toLowerCase()).equals("1")) {
                            showToast(strMessage);
                            if (pHelpWindow.isShowing()) {
                                pHelpWindow.dismiss();
                                hideKeyBoard();
                            }
                        } else {
                            showToast(strMessage);
                        }
                    } else {
                        showToast(String.valueOf(getResources().getText(R.string.else_error_message)));
                    }

                } catch (Exception e) {
                    showToast(String.valueOf(getResources().getText(R.string.catch_message)));
                    Log.e("Help:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Help:Failure", t.toString());
            }
        });
    }

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


    }

    private void showLanguageListPopup() {
        LanguageList = TeacherUtil_SharedPreference.getLanguages(MessageDatesScreen.this, "Language");
        String[] countriesArray = new String[LanguageList.size()];

        for (int i = 0; i < LanguageList.size(); i++) {
            for (int cnt = 0; cnt < countriesArray.length; cnt++) {
                countriesArray[cnt] = LanguageList.get(cnt).getStrLanguageName();
            }

        }

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MessageDatesScreen.this);
        android.app.AlertDialog alertDialog;
        builder.setTitle(R.string.choose_language);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(countriesArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                int selectedPosition = ((android.app.AlertDialog) dialog).getListView().getCheckedItemPosition();

                final Languages model = LanguageList.get(selectedPosition);
                String ID = model.getStrLanguageID();
                String code = model.getScriptCode();

                Log.d("code", code);
                Log.d("ID", ID);

                changeLanguage(code, ID);




                dialog.cancel();


            }
        });
        builder.setNegativeButton(R.string.pop_password_btnCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void changeLanguage(String code, String id) {
        TeacherUtil_SharedPreference.putLanguageType(MessageDatesScreen.this, code);
        languageChangeApi(id, code);

    }

    private void languageChangeApi(String id, final String lang) {

        schools_list = TeacherUtil_SharedPreference.getChildrenScreenSchools_List(MessageDatesScreen.this, "schools_list");
        childList = TeacherUtil_SharedPreference.getChildrenDetails(MessageDatesScreen.this, "child_list");

        IDs = "";
        String countryId = TeacherUtil_SharedPreference.getCountryID(MessageDatesScreen.this);


        JsonArray jsonArray = new JsonArray();
        JsonObject jsonObject = new JsonObject();



//        if (schools_list != null) {
//            for (int i = 0; i < schools_list.size(); i++) {
//                final TeacherSchoolsModel model = schools_list.get(i);
//                IDs = IDs + model.getStrStaffID() + "~";
//
//            }
//        }
//        if (childList != null) {
//            for (int i = 0; i < childList.size(); i++) {
//                final Profiles model = childList.get(i);
//                IDs = IDs + model.getChildID() + "~";
//            }
//        }
//
//        IDs = IDs.substring(0, IDs.length() - 1);
        //Log.d("IDS", IDs);

        if (schools_list != null) {
            for (int i = 0; i < schools_list.size(); i++) {
                final TeacherSchoolsModel model = schools_list.get(i);

                jsonObject.addProperty("type","staff");
                jsonObject.addProperty("id",model.getStrStaffID());
                jsonObject.addProperty("schoolid",model.getStrSchoolID());
                jsonArray.add(jsonObject);

            }
        }
        if (childList != null) {
            for (int i = 0; i < childList.size(); i++) {
                final Profiles model = childList.get(i);
                jsonObject.addProperty("type","parent");
                jsonObject.addProperty("id",model.getChildID());
                jsonObject.addProperty("schoolid",model.getSchoolID());
                jsonArray.add(jsonObject);
            }
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonObjectlanguage = new JsonObject();
        jsonObjectlanguage.add("MemberData", jsonArray);
        jsonObjectlanguage.addProperty("LanguageId", id);
        jsonObjectlanguage.addProperty("CountryID", countryId);

        Log.d("Request", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ChangeLanguage(jsonObjectlanguage);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("VersionCheck:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("VersionCheck:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {

                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");



                        LanguageIDAndNames. putPrincipalIdstoSharedPref(jsonObject.getString("isPrincipalID"),MessageDatesScreen.this);
                        LanguageIDAndNames.  putStaffIdstoSharedPref(jsonObject.getString("isStaffID"),MessageDatesScreen.this);
                        LanguageIDAndNames. putAdminIdstoSharedPref(jsonObject.getString("isAdminID"),MessageDatesScreen.this);
                        LanguageIDAndNames. putGroupHeadIdstosharedPref(jsonObject.getString("idGroupHeadID"),MessageDatesScreen.this);
                        LanguageIDAndNames. putParentIdstoSharedPref(jsonObject.getString("isParentID"),MessageDatesScreen.this);
                        LanguageIDAndNames. putPrincipalNametoSharedPref(jsonObject.getString("isPrincipal"),MessageDatesScreen.this);
                        LanguageIDAndNames. putStaffNamestoSharedPref(jsonObject.getString("isStaff"),MessageDatesScreen.this);
                        LanguageIDAndNames. putAdminNamestoSharedPref(jsonObject.getString("isAdmin"),MessageDatesScreen.this);
                        LanguageIDAndNames. putGroupHeadtoSharedPref(jsonObject.getString("idGroupHead"),MessageDatesScreen.this);
                        LanguageIDAndNames. putParentNamestoSharedPref(jsonObject.getString("isParent"),MessageDatesScreen.this);



                        if (Integer.parseInt(status) > 0) {
                            showToast(message);

                            Locale myLocale = new Locale(lang);
                            //saveLocale(lang);
                            Locale.setDefault(myLocale);
                            Configuration config = new Configuration();
                            config.locale = myLocale;
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                            recreate();

                        } else {
                            showToast(message);
                        }

                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));

                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }

    private void showLogoutAlert() {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MessageDatesScreen.this);
        alertDialog.setTitle(R.string.txt_menu_logout);
        alertDialog.setMessage(R.string.want_to_logut);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.cancel();

                TeacherUtil_SharedPreference.putInstall(MessageDatesScreen.this, "1");
                TeacherUtil_SharedPreference.putOTPNum(MessageDatesScreen.this, "");
                TeacherUtil_SharedPreference.putMobileNumberScreen(MessageDatesScreen.this, "");

                TeacherUtil_SharedPreference.clearStaffSharedPreference(MessageDatesScreen.this);
                startActivity(new Intent(MessageDatesScreen.this, TeacherSignInScreen.class));
                finish();


            }
        });
        alertDialog.setPositiveButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        android.app.AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
        Button negativebutton = dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
        negativebutton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }
}
