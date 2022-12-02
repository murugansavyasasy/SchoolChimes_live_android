package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.PicassoImageLoadingService;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.adapter.ExamDateListAdapter;
import com.vs.schoolmessenger.adapter.TextCircularListAdapter;
import com.vs.schoolmessenger.adapter.TextCircularListAdapternew;
import com.vs.schoolmessenger.interfaces.OnMsgItemClickListener;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.ExamDateListClass;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.model.TeacherMessageModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

import static com.vs.schoolmessenger.util.Util_Common.MENU_EVENTS;
import static com.vs.schoolmessenger.util.Util_Common.MENU_EXAM_TEST;
import static com.vs.schoolmessenger.util.Util_Common.MENU_HW;
import static com.vs.schoolmessenger.util.Util_Common.MENU_NOTICE_BOARD;
import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT;
import static com.vs.schoolmessenger.util.Util_Common.MENU_TEXT_HW;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_SMS;

public class TextCircular extends AppCompatActivity {

    RecyclerView rvTextMsgList;
    TextCircularListAdapter textAdapter;
    public ArrayList<MessageModel> msgModelList = new ArrayList<>();
    public ArrayList<MessageModel> OfflinemsgModelList = new ArrayList<>();
    public ArrayList<MessageModel> totalmsgModelList = new ArrayList<>();
    TextCircularListAdapternew tvadapter;
    String selDate, strMsgType;
    private int iRequestCode;

    ExamDateListAdapter mAdapter;

    public ArrayList<ExamDateListClass> exams = new ArrayList<>();

    SqliteDB myDb;
    ArrayList<MessageModel> arrayList;

    ArrayList<ExamDateListClass> subjects;
    TextView lblNoMessages;
    String isNewVersion;
    TextView LoadMore;
    Calendar c;
    String previousDate;
    Boolean is_Archive;

    ImageView imgSearch;
    TextView Searchable;

    Slider slider;
    ImageView adImage;

    RelativeLayout voice_rlToolbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_circular);
        c = Calendar.getInstance();
        voice_rlToolbar = (RelativeLayout) findViewById(R.id.text_rlToolbar);
        voice_rlToolbar.setVisibility(View.GONE);

        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);
        if (iRequestCode == MENU_HW || iRequestCode == MENU_TEXT) {
            selDate = getIntent().getExtras().getString("SEL_DATE", "");
            is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        }
        else
        {
            //selDate = getIntent().getExtras().getString("HEADER", "");
            selDate = "Notice Board";
        }

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_children);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(selDate);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        Slider.init(new PicassoImageLoadingService(TextCircular.this));
        slider = findViewById(R.id.banner);
         adImage = findViewById(R.id.adImage);

        ImageView ivBack = (ImageView) findViewById(R.id.text_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(iRequestCode==MENU_TEXT){
                    TeacherUtil_SharedPreference.putOnBackPressedText(TextCircular.this,"1");
                }
                else if(iRequestCode==MENU_HW){
                    TeacherUtil_SharedPreference.putOnBackPressedHWTEXT(TextCircular.this,"1");

                }
                onBackPressed();
            }
        });
        rvTextMsgList = (RecyclerView) findViewById(R.id.text_rvCircularList);
        TextView tvTitle = (TextView) findViewById(R.id.text_ToolBarTvTitle);
        tvTitle.setText(selDate);

        Searchable = (EditText) findViewById(R.id.Searchable);
        imgSearch = (ImageView) findViewById(R.id.imgSearch);


        if(iRequestCode==MENU_NOTICE_BOARD || iRequestCode == MENU_EVENTS) {

            Searchable.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (tvadapter == null)
                        return;

                    if (tvadapter.getItemCount() < 1) {
                        rvTextMsgList.setVisibility(View.GONE);
                        if (Searchable.getText().toString().isEmpty()) {
                            rvTextMsgList.setVisibility(View.VISIBLE);
                        }

                    } else {
                        rvTextMsgList.setVisibility(View.VISIBLE);
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

        }
        else {
            Searchable.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (textAdapter == null)
                        return;

                    if (textAdapter.getItemCount() < 1) {
                        rvTextMsgList.setVisibility(View.GONE);
                        if (Searchable.getText().toString().isEmpty()) {
                            rvTextMsgList.setVisibility(View.VISIBLE);
                        }

                    } else {
                        rvTextMsgList.setVisibility(View.VISIBLE);
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

        }

         LoadMore=(TextView) findViewById(R.id.btnSeeMore);
         lblNoMessages=(TextView) findViewById(R.id.lblNoMessages);
        LoadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadMorecircularsNoticeboardAPI();

            }
        });

         isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
         seeMoreButtonVisiblity();



        if(iRequestCode==MENU_NOTICE_BOARD || iRequestCode == MENU_EVENTS){
            tvadapter = new TextCircularListAdapternew(msgModelList, TextCircular.this);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            rvTextMsgList.setLayoutManager(layoutManager);
            rvTextMsgList.setItemAnimator(new DefaultItemAnimator());
            rvTextMsgList.setAdapter(tvadapter);
        }

        else {
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
            rvTextMsgList.setLayoutManager(layoutManager);
            rvTextMsgList.setItemAnimator(new DefaultItemAnimator());
            textAdapter = new TextCircularListAdapter(msgModelList, TextCircular.this, new OnMsgItemClickListener() {
                @Override
                public void onMsgItemClick(TeacherMessageModel item) {
                    Intent inTextPopup = new Intent(TextCircular.this, TextMessagePopup.class);
                    inTextPopup.putExtra("TEXT_ITEM", item);
                    inTextPopup.putExtra("is_Archive", is_Archive);
                    startActivity(inTextPopup);
                }
            });
            rvTextMsgList.setAdapter(textAdapter);
        }
    }

    private void filterlist(String s) {
        ArrayList<MessageModel> temp = new ArrayList();
        for (MessageModel d : msgModelList) {

            if (d.getMsgContent().toLowerCase().contains(s.toLowerCase()) || d.getMsgDate().toLowerCase().contains(s.toLowerCase()) ) {
                temp.add(d);
            }

        }
        if(iRequestCode==MENU_NOTICE_BOARD || iRequestCode == MENU_EVENTS) {
            tvadapter.updateList(temp);
        }
        else {
            textAdapter.updateList(temp);

        }
    }

    private void seeMoreButtonVisiblity() {
        if(isNewVersion.equals("1") && iRequestCode==MENU_NOTICE_BOARD){
            LoadMore.setVisibility(View.VISIBLE);
        }
        else {
            LoadMore.setVisibility(View.GONE);
            lblNoMessages.setVisibility(View.GONE);
        }
    }

    private void LoadMorecircularsNoticeboardAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Getnoticeboard(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.LoadMoreGetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());
                LoadMore.setVisibility(View.GONE);
                lblNoMessages.setVisibility(View.GONE);


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                           OfflinemsgModelList.clear();
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"),"",jsonObject.getBoolean("is_Archive"));
                                msgModelList.add(msgModel);
                                OfflinemsgModelList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);
                            tvadapter.notifyDataSetChanged();

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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //TeacherUtil_SharedPreference.putOnBackPressed(TextCircular.this,"1");
        if(iRequestCode==MENU_TEXT){
            TeacherUtil_SharedPreference.putOnBackPressedText(TextCircular.this,"1");
        }
        else if(iRequestCode==MENU_HW){
            TeacherUtil_SharedPreference.putOnBackPressedHWTEXT(TextCircular.this,"1");

        }
        finish();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(iRequestCode==MENU_TEXT){
                    TeacherUtil_SharedPreference.putOnBackPressedText(TextCircular.this,"1");
                }
                else if(iRequestCode==MENU_HW){
                    TeacherUtil_SharedPreference.putOnBackPressedHWTEXT(TextCircular.this,"1");

                }
                onBackPressed();
                return (true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ShowAds.getAds(this,adImage,slider,"");
        switch (iRequestCode) {
            case MENU_TEXT:
                if (isNetworkConnected()) {
                    circularsTextbydateAPI();
                }

                break;

            case MENU_NOTICE_BOARD:

                if (isNetworkConnected()) {
                    circularsNoticeboardAPI();
                }

                break;

            case MENU_EVENTS:

                if (isNetworkConnected()) {
                    circularsEventsAPI();
                }

                break;

            case MENU_EXAM_TEST:

              if (isNetworkConnected()) {
                    circularsExameAPI();
                }


                break;

            case MENU_HW:

                if (isNetworkConnected()) {
                    circularsHomeworkAPI();
                }

                break;
        }


    }

    private void showSettingsAlert1() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TextCircular.this);

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

        ConnectivityManager connMgr = (ConnectivityManager) TextCircular.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        return connMgr.getActiveNetworkInfo() != null;
    }

    private void voiceMsgType(String msgType) {
        strMsgType = msgType;
        circularsForGivenDateAPI2();
    }

    private void showToast(String msg) {
        Toast.makeText(TextCircular.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void prepareData() {
        textAdapter.clearAllData();
        for (int i = 0; i < 3; i++) {
            MessageModel model = new MessageModel();
            model.setMsgID(String.valueOf(i));
            long unixTime = System.currentTimeMillis() / 1000L;
            model.setMsgTitle(String.valueOf(unixTime));
            model.setMsgContent(i + " " + getResources().getString(R.string.sample_txt_msg));
            model.setMsgDate("10 May 2017");
            model.setMsgTime("11:30 AM");
            model.setMsgReadStatus("1");
            msgModelList.add(model);
        }

        textAdapter.notifyDataSetChanged();
    }

    private void circularsForGivenDateAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = Util_JsonRequest.getJsonArray_GetFiles(selDate, strChildID, strSchoolID, MSG_TYPE_SMS);
        Call<JsonArray> call = apiService.GetFiles(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strDate = jsonObject.getString("ID");
                        String strTotalSMS = jsonObject.getString("URL");

                        if (!strDate.equals("")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            textAdapter.clearAllData();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);
                                msgModelList.add(msgModel);
                            }

                            textAdapter.notifyDataSetChanged();

                        } else {
                            showToast(strTotalSMS);
                        }
                    } else {
                        showToast("No Records found. Try again");
                    }

                } catch (Exception e) {
                    Log.e("TextMsg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }


    private void circularsForGivenDateAPI2() {
        try {
            JSONArray js = new JSONArray("[{\"ID\":\"7344083\",\"URL\":\"Assalamu alaikum, MASHAA ALLAH aaj Vision Olympics for Parents bahot kamiyab raha, ham Vision International School ki taraf se sabhi parents ko aaj ke din her ek sports Mai Shamil hoker iss sports day ko kamiyab kerne ke liye shukriya Ada kerta hai\\nand Due to the same reason the school is announcing for holiday tomorrow that is on Monday 15 Jan 2018.\\n-Vision International School.\",\"Date\":\"14-01-2018\",\"Time\":\"19:14:45\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null},{\"ID\":\"7344083\",\"URL\":\"Assalamu alaikum, MASHAA ALLAH aaj Vision Olympics for Parents bahot kamiyab raha, ham Vision International School ki taraf se sabhi parents ko aaj ke din her ek sports Mai Shamil hoker iss sports day ko kamiyab kerne ke liye shukriya Ada kerta hai\\nand Due to the same reason the school is announcing for holiday tomorrow that is on Monday 15 Jan 2018.\\n-Vision International School.\",\"Date\":\"14-01-2018\",\"Time\":\"19:14:45\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null},{\"ID\":\"7332886\",\"URL\":\"Assalamu alaikum Vision Olympics Parents ke liye abhi Kuch hi pal mai shuru hone wala hai. Parents se darkhwast hai ke O is moukhe Mai Shamil hoker iska bharoose fayada uthaye.\\n-Vision International School.\",\"Date\":\"14-01-2018\",\"Time\":\"09:33:36\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null},{\"ID\":\"7332886\",\"URL\":\"Assalamu alaikum Vision Olympics Parents ke liye abhi Kuch hi pal mai shuru hone wala hai. Parents se darkhwast hai ke O is moukhe Mai Shamil hoker iska bharoose fayada uthaye.\\n-Vision International School.\",\"Date\":\"14-01-2018\",\"Time\":\"09:33:36\",\"Subject\":\"Management Circular\",\"AppReadStatus\":\"0\",\"Query\":null,\"Question\":null}]");
            if (js.length() > 0) {
                JSONObject jsonObject = js.getJSONObject(0);
                String strDate = jsonObject.getString("ID");
                String strTotalSMS = jsonObject.getString("URL");

                if (!strDate.equals("")) {
                    MessageModel msgModel;
                    Log.d("json length", js.length() + "");

                    textAdapter.clearAllData();

                    for (int i = 0; i < js.length(); i++) {
                        jsonObject = js.getJSONObject(i);
                        msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);
                        msgModelList.add(msgModel);
                    }

                    textAdapter.notifyDataSetChanged();

                } else {
                    showToast(strTotalSMS);
                }
            } else {
                showToast(getResources().getString(R.string.check_internet));
            }

        } catch (Exception e) {
            Log.e("TextMsg:Exception", e.getMessage());
        }
    }


    private void circularsTextbydateAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_GetGeneralvoiceortext(strChildID, strSchoolID,"SMS",selDate);

        Call<JsonArray> call;
        if(isNewVersion.equals("1")&&is_Archive){
            call= apiService.GetFiles_Archive(jsonReqArray);
        }
        else {
            call = apiService.GetFiles(jsonReqArray);
        }
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (!strStatus.equals("")) {
                            textAdapter.clearAllData();
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("ID"), jsonObject.getString("Subject"),
                                        jsonObject.getString("URL"), jsonObject.getString("AppReadStatus"),
                                        jsonObject.getString("Date"), jsonObject.getString("Time"),jsonObject.getString("Description"),false);
                                msgModelList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            textAdapter.notifyDataSetChanged();

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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }



    private void circularsExameAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Getnoticeboard(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetExamsOrTests(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());

                   if (js.length() > 0) {

                       for (int i = 0; i < js.length(); i++) {
                           JSONObject jsonObject = js.getJSONObject(i);
                           String strStatus = jsonObject.getString("ExamId");
                           String strMessage = jsonObject.getString("ExaminationName");
                           String Syllabus = jsonObject.getString("ExaminationSyllabus");

                           if (strStatus.equals("0")) {
                               showAlertRecords(strMessage);
                           } else {

                               JSONArray Details = new JSONArray(jsonObject.getString("SubjectDetails"));

                               ExamDateListClass model;

                               mAdapter.clearAllData();

                               for (int j = 0; j < Details.length(); j++) {

                                   JSONObject jsonObject1 = Details.getJSONObject(j);

                                   model = new ExamDateListClass(Syllabus, jsonObject1.getString("Subname"), jsonObject1.getString("ExamDate"),
                                           jsonObject1.getString("ExamSession"), jsonObject1.getString("maxMark"),"");

                                   exams.add(model);
                               }


                               subjects = new ArrayList<>();
                               subjects.addAll(exams);
                               mAdapter.notifyDataSetChanged();

                           }
                       }
                   }
                   else {
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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }



    private void circularsNoticeboardAPI() {
        String isNewVersionn=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersionn.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Getnoticeboard(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetNoticeBoard(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if(isNewVersion.equals("1")&&iRequestCode==MENU_NOTICE_BOARD){
                            LoadMore.setVisibility(View.VISIBLE);
                        }
                        else {
                            LoadMore.setVisibility(View.GONE);
                            lblNoMessages.setVisibility(View.GONE);
                        }

                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            tvadapter.clearAllData();
                            totalmsgModelList.clear();
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("NoticeBoardTitle"),
                                        jsonObject.getString("NoticeBoardContent"), "",
                                        jsonObject.getString("Date"), jsonObject.getString("Day"),"",false);
                                msgModelList.add(msgModel);
                                totalmsgModelList.add(msgModel);
                            }


                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);
                            tvadapter.notifyDataSetChanged();

                        } else {

                            if(isNewVersion.equals("1")){
                                lblNoMessages.setVisibility(View.VISIBLE);
                                lblNoMessages.setText(strMessage);

                                String loadMoreCall=TeacherUtil_SharedPreference.getOnBackMethod(TextCircular.this);
                                if(loadMoreCall.equals("1")){
                                    TeacherUtil_SharedPreference.putOnBackPressed(TextCircular.this,"");
                                    LoadMorecircularsNoticeboardAPI();
                                }

                            }
                            else {
                                lblNoMessages.setVisibility(View.GONE);
                                showAlertRecords(strMessage);
                            }

                        }
                    }

                    else {
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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void circularsEventsAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Getnoticeboard(strChildID, strSchoolID);
        Call<JsonArray> call = apiService.GetSchoolEvents(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");


                        if (strStatus.equals("1")) {
                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            tvadapter.clearAllData();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("Status"), jsonObject.getString("EventTitle"),
                                        jsonObject.getString("EventContent"), "",
                                        jsonObject.getString("EventDate"), jsonObject.getString("EventTime"),"",false);
                                msgModelList.add(msgModel);
                            }

                            arrayList = new ArrayList<>();
                            arrayList.addAll(msgModelList);

                            tvadapter.notifyDataSetChanged();

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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }

    private void showAlertRecords(String no_events_found) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TextCircular.this);

        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(no_events_found);

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

        //alertDialog.show();
    }

    private void circularsHomeworkAPI() {

        String isNewVersion=TeacherUtil_SharedPreference.getNewVersion(TextCircular.this);
        if(isNewVersion.equals("1")){
            String ReportURL=TeacherUtil_SharedPreference.getReportURL(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        }
        else {
            String baseURL= TeacherUtil_SharedPreference.getBaseUrl(TextCircular.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String strChildID = Util_SharedPreference.getChildIdFromSP(TextCircular.this);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(TextCircular.this);

        String MobileNumber= TeacherUtil_SharedPreference.getMobileNumberFromSP(TextCircular.this);

        Log.d("TextMsg:Date-Child-Sch", selDate + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_Gethomework(strChildID, selDate,"TEXT",strSchoolID,MobileNumber);


        Call<JsonArray> call;
        if(isNewVersion.equals("1")&&is_Archive){
            call = apiService.GetHomeWorkFiles_Archive(jsonReqArray);
        }
        else {
            call = apiService.GetHomeWorkFiles(jsonReqArray);
        }

        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("TextMsg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("TextMsg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                            MessageModel msgModel;
                            Log.d("json length", js.length() + "");

                            textAdapter.clearAllData();

                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                msgModel = new MessageModel(jsonObject.getString("HomeworkID"), jsonObject.getString("HomeworkSubject"),
                                        jsonObject.getString("HomeworkContent"), "",
                                        selDate, "",jsonObject.getString("HomeworkTitle"),false);


                                msgModelList.add(msgModel);
                            }


                        arrayList = new ArrayList<>();
                        arrayList.addAll(msgModelList);
                        textAdapter.notifyDataSetChanged();


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
                Log.d("TextMsg:Failure", t.toString());
            }
        });
    }
}
