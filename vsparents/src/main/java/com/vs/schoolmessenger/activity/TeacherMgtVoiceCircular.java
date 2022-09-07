package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSchoolsListAdapter;
import com.vs.schoolmessenger.adapter.TeacherSelectedschoolListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.TeacherOnCheckSchoolsListener;
import com.vs.schoolmessenger.interfaces.TeacherOnSelectedStdGroupListener;
import com.vs.schoolmessenger.model.TeacherClassGroupModel;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;


public class TeacherMgtVoiceCircular extends AppCompatActivity implements View.OnClickListener {

    Button btnSelectSchool, btnSend, btnSendToAllSchool;

    private ArrayList<TeacherSchoolsModel> arrSchoolList = new ArrayList<>();
    private ArrayList<TeacherSchoolsModel> seletedschoollist = new ArrayList<>();

    RecyclerView tvSelectedSchools;
    TeacherSelectedschoolListAdapter adapter;
    LinearLayout llRecipients;

    PopupWindow pWindowSchools;
    private int i_schools_count;

    static int SELECTED_SCHOOL_POSITION = 0;

    //Media
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration;

    private MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    File fileRecordedFilePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_send_audiotoentireschool);


//        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        i_schools_count = 0;

        ImageView ivBack = (ImageView) findViewById(R.id.audio_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
//        voiceclass=(Button)findViewById(R.id.voice_selectclass);

        btnSelectSchool = (Button) findViewById(R.id.mgtVoice_btnChooseSchools);
        btnSend = (Button) findViewById(R.id.mgtVoice_btnSend);
        btnSendToAllSchool = (Button) findViewById(R.id.mgtVoice_btnSendToAllSchools);

        btnSend.setOnClickListener(this);
        btnSendToAllSchool.setOnClickListener(this);
        btnSelectSchool.setOnClickListener(this);

        tvSelectedSchools = (RecyclerView) findViewById(R.id.rvselectedschoolList);
        adapter = new TeacherSelectedschoolListAdapter(TeacherMgtVoiceCircular.this, new TeacherOnCheckSchoolsListener() {
            @Override
            public void school_addSchool(TeacherSchoolsModel school) {
                if ((school != null) && (!seletedschoollist.contains(school))) {
                    seletedschoollist.add(school);
                    i_schools_count++;
                }
            }

            @Override
            public void school_removeSchool(TeacherSchoolsModel school) {
                if ((school != null) && (seletedschoollist.contains(school))) {
                    seletedschoollist.remove(school);
                    i_schools_count--;

                    int index = arrSchoolList.indexOf(school);
                    arrSchoolList.get(index).setSelectStatus(false);
                }

                enableSend();
            }
        }, new TeacherOnSelectedStdGroupListener() {
            @Override
            public void classGrp_selectedClassesAndGroups(TeacherSchoolsModel school) {

                SELECTED_SCHOOL_POSITION = seletedschoollist.indexOf(school);
                Intent inStdGroups = new Intent(TeacherMgtVoiceCircular.this, TeacherExtandablegroupclasslist.class);
                inStdGroups.putExtra("STD_GROUP", school);
                startActivityForResult(inStdGroups, SELECTED_SCHOOL_POSITION);
            }
        }, seletedschoollist);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        tvSelectedSchools.setHasFixedSize(true);
        tvSelectedSchools.setLayoutManager(mLayoutManager);
        tvSelectedSchools.setItemAnimator(new DefaultItemAnimator());
        tvSelectedSchools.setAdapter(adapter);

        llRecipients = (LinearLayout) findViewById(R.id.mgtVoice_llRecipient);
        llRecipients.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);

        setupAudioPlayer();
        fetchSong();
        schoolsListAPI();

    }

    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

        backToResultActvity("BACK");
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(1, returnIntent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_SCHOOL_POSITION) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {
//                showToast("Success");

                TeacherSchoolsModel schoolsModel = (TeacherSchoolsModel) data.getSerializableExtra("SCHOOL");
                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListClasses(schoolsModel.getListClasses());
                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListGroups(schoolsModel.getListGroups());

                TeacherSelectedschoolListAdapter.bEditClick = true;
            }
//            else {
//                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListClasses(new ArrayList<TeacherClassGroupModel>());
//                seletedschoollist.get(SELECTED_SCHOOL_POSITION).setListGroups(new ArrayList<TeacherClassGroupModel>());
//                showToast(message);
//            }

            constructResultJsonArray();
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mgtVoice_btnChooseSchools:
//                i_schools_count = 0;
//                adapter.clearAllData();

                setupSchoolsListPopUp();
                pWindowSchools.showAtLocation(btnSelectSchool, Gravity.NO_GRAVITY, 0, 0);
                break;

            case R.id.mgtVoice_btnSend:
                if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtVoiceCircular.this).equals(LOGIN_TYPE_ADMIN))
                    sendAdminVoiceToListOfSchoolsAPI();
                else sendMgtVoiceToListOfSchoolsAPI();
                break;

            case R.id.mgtVoice_btnSendToAllSchools:
                sendVoiceToAllSchoolAPI();
                break;

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;
        }
    }

    public void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, VOICE_FOLDER_NAME);
            File dir = new File(file.getAbsolutePath());

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

            fileRecordedFilePath = new File(dir, VOICE_FILE_NAME);
            System.out.println("FILE_PATH:" + fileRecordedFilePath.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileRecordedFilePath.getPath());
            mediaPlayer.prepare();
//            seekBar.setProgress(0);
//            seekBar.setMax(99);

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }

    // Play Voice

    private void setupAudioPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
                imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
                mediaPlayer.seekTo(0);
            }
        });

        seekBar.setMax(99); // It means 100% .0-99
        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.myAudioPlayer_seekBar) {
//                    if (holder.mediaPlayer.isPlaying())
                    {
                        SeekBar sb = (SeekBar) v;
                        int playPositionInMillisecconds = (mediaFileLengthInMilliseconds / 100) * sb.getProgress();
//                        Log.d("Position: ", ""+playPositionInMillisecconds);
                        mediaPlayer.seekTo(playPositionInMillisecconds);
                    }
                }
                return false;
            }
        });
    }

    private void recVoicePlayPause() {

        mediaFileLengthInMilliseconds = mediaPlayer.getDuration(); // gets the song length in milliseconds from URL
//        tvPlayDuration.setText(milliSecondsToTimer(mediaFileLengthInMilliseconds));
//                tvTotDuration.setText(TeacherUtil_Common.milliSecondsToTimer(mediaFileLengthInMilliseconds));

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_pause);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_clr_red));
        } else {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }

        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds);
    }

    private void primarySeekBarProgressUpdater(final int fileLength) {
        int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
        seekBar.setProgress(iProgress); // This math construction give a percentage of "was playing"/"song length"

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    tvPlayDuration.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    primarySeekBarProgressUpdater(fileLength);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }

    private void schoolsListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

//        TeacherSchoolsApiClient.BASE_URL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherMgtVoiceCircular.this);
        final String mobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherMgtVoiceCircular.this);
        final String callerType;
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtVoiceCircular.this).equals(LOGIN_TYPE_ADMIN))
            callerType = "A";
        else callerType = "M";

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetSchoolLists(mobileNumber, callerType);
        Call<JsonArray> call = apiService.GetSchoolLists(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("GetSchools:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetSchools:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strSubName = jsonObject.getString("SchoolName");
                        String strSubCode = jsonObject.getString("SchoolID");

                        if (strSubName.equals("")) {
                            showToast(strSubCode);
                            onBackPressed();
                        } else {
                            TeacherSchoolsModel schoolsModel;
                            Log.d("json length", js.length() + "");

                            for (int i = 0; i < js.length(); i++)
                            {
                                jsonObject = js.getJSONObject(i);
                                schoolsModel = new TeacherSchoolsModel(jsonObject.getString("SchoolName"), jsonObject.getString("SchoolID")
                                        , jsonObject.getString("SchoolLogo"), jsonObject.getString("SchoolAddress"), false);

                                schoolsModel.setStrStaffID(jsonObject.getString("StaffID"));

                                Log.d("Testing", "1***********************");
                                ArrayList<TeacherClassGroupModel> listStd = new ArrayList<>();
                                JSONArray jsAryStd = jsonObject.getJSONArray("StdCode");
                                Log.d("Testing", "2***********************");
                                if (jsAryStd.length() > 0) {
                                    JSONObject jObjStd;
                                    TeacherClassGroupModel classGroups;
                                    Log.d("Testing", "3***********************");
                                    for (int j = 0; j < jsAryStd.length(); j++) {
                                        jObjStd = jsAryStd.getJSONObject(j);
                                        classGroups = new TeacherClassGroupModel(jObjStd.getString("Stdname"), jObjStd.getString("StdId"), true);
                                        listStd.add(classGroups);

                                        Log.d("Testing", "4***********************");
                                    }
                                }

                                ArrayList<TeacherClassGroupModel> listGroups = new ArrayList<>();
                                JSONArray jsAryGrp = jsonObject.getJSONArray("GrpCode");

                                Log.d("Testing", "5***********************");
                                if (jsAryGrp.length() > 0) {
                                    JSONObject jObjGrp;
                                    TeacherClassGroupModel classGroups;

                                    Log.d("Testing", "6***********************");
                                    for (int j = 0; j < jsAryGrp.length(); j++) {
                                        jObjGrp = jsAryGrp.getJSONObject(j);
                                        classGroups = new TeacherClassGroupModel(jObjGrp.getString("GroupName"), jObjGrp.getString("GroupCode"), true);
                                        listGroups.add(classGroups);

                                        Log.d("Testing", "7***********************");
                                    }
                                }

                                schoolsModel.setListClasses(listStd);
                                schoolsModel.setListGroups(listGroups);
                                arrSchoolList.add(schoolsModel);

                                Log.d("Testing", "8***********************");
                            }

                            TeacherSelectedschoolListAdapter.bEditClick = false;
//                            seletedschoollist.addAll(arrSchoolList);
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    Log.d("Exception", e.toString());
                    showToast(getResources().getString(R.string.check_internet));
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("GetSchools:Failure", t.toString());
                onBackPressed();
            }
        });
    }

    private void setupSchoolsListPopUp() {

        final LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_schools, null);

//        final List<TeacherSchoolsModel> myContacts = new ArrayList<>();
        RecyclerView rvMembers = (RecyclerView) layout.findViewById(R.id.popupSchools_rvSchools);

        TextView tvCancel = (TextView) layout.findViewById(R.id.popupSchools_tvCancel);
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pWindowSchools.dismiss();
            }
        });

        final TextView tvOk = (TextView) layout.findViewById(R.id.popupSchools_tvOk);
        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pWindowSchools.dismiss();
                adapter.notifyDataSetChanged();
                enableSend();
            }
        });

        TeacherSchoolsListAdapter schoolsListAdapter =
                new TeacherSchoolsListAdapter(TeacherMgtVoiceCircular.this, new TeacherOnCheckSchoolsListener() {
                    @Override
                    public void school_addSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (!seletedschoollist.contains(school))) {
                            // Scroll Testing...
//                            for (int i = 0; i < 30; i++)
                            seletedschoollist.add(school);
                            i_schools_count++;
//                            enableDisableNext(tvOk);
                        }
                    }

                    @Override
                    public void school_removeSchool(TeacherSchoolsModel school) {
                        if ((school != null) && (seletedschoollist.contains(school))) {
                            seletedschoollist.remove(school);
                            i_schools_count--;
//                            enableDisableNext(tvOk);
                        }
                    }
                }, arrSchoolList);

        pWindowSchools = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pWindowSchools.setContentView(layout);

        rvMembers.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(TeacherMgtVoiceCircular.this);
        rvMembers.setLayoutManager(layoutManager);
        rvMembers.addItemDecoration(new DividerItemDecoration(TeacherMgtVoiceCircular.this, LinearLayoutManager.VERTICAL));
        rvMembers.setItemAnimator(new DefaultItemAnimator());
        rvMembers.setAdapter(schoolsListAdapter);
    }

    private void enableDisableNext(TextView tvOK) {
        Log.d("i_students_count", "count: " + i_schools_count);

//        tvSelectedCount.setText(i_schools_count + "");

        if (i_schools_count > 0) {
            tvOK.setEnabled(true);
        } else {
            tvOK.setEnabled(false);
        }
    }

    private void enableSend() {
        Log.d("i_students_count", "count: " + i_schools_count);

//        tvSelectedCount.setText(i_schools_count + "");

        if (i_schools_count > 0) {
            llRecipients.setVisibility(View.VISIBLE);
            btnSend.setEnabled(true);
        } else {
            llRecipients.setVisibility(View.GONE);
            btnSend.setEnabled(false);
        }
    }

    private JsonArray constructResultJsonArray() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
//            String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherMgtVoiceCircular.this);
//            String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherMgtVoiceCircular.this);

            JsonObject jsonObjectSchool = new JsonObject();

            JsonArray jsonArrayStdSections = new JsonArray();
            for (int i = 0; i < seletedschoollist.size(); i++) {

//                if (selectedStdSecStudList.get(i).isSelectStatus())
                {
                    JsonObject jsonObjectStdSections = new JsonObject();

                    jsonObjectStdSections.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectStdSections.addProperty("SchoolName", seletedschoollist.get(i).getStrSchoolName());
                    jsonObjectStdSections.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());

//                    jsonObjectStdSections.addProperty("MessageToAll", selectedStdSecStudList.get(i).isAllStudentsSelected());
//                    String strMsgToAll = "T";
//                    if (!seletedschoollist.get(i).isAllStudentsSelected())
//                        strMsgToAll = "F";
//                    jsonObjectStdSections.addProperty("MsgToAll", strMsgToAll);

                    JsonArray jsonArrayStandards = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleStds = new ArrayList<>();
                    seleStds.addAll(seletedschoollist.get(i).getListClasses());

                    if (seleStds.size() > 0) {
                        for (int j = 0; j < seleStds.size(); j++) {
                            if (seleStds.get(j).isbSelected()) {
                                JsonObject jsonObjectStandards = new JsonObject();
                                jsonObjectStandards.addProperty("GroupID", seleStds.get(j).getStrID());
                                jsonObjectStandards.addProperty("GroupName", seleStds.get(j).getStrName());
                                jsonArrayStandards.add(jsonObjectStandards);
                            }
                        }
                    }

                    JsonArray jsonArrayGroups = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleGroups = new ArrayList<>();
                    seleGroups.addAll(seletedschoollist.get(i).getListGroups());

                    if (seleGroups.size() > 0) {
                        for (int j = 0; j < seleGroups.size(); j++) {
                            if (seleGroups.get(j).isbSelected()) {
                                JsonObject jsonObjectGrops = new JsonObject();
                                jsonObjectGrops.addProperty("GroupID", seleGroups.get(j).getStrID());
                                jsonObjectGrops.addProperty("GroupName", seleGroups.get(j).getStrName());
                                jsonArrayGroups.add(jsonObjectGrops);
                            }
                        }
                    }

                    jsonObjectStdSections.add("Stds", jsonArrayStandards);
                    jsonObjectStdSections.add("Groups", jsonArrayGroups);
                    jsonArrayStdSections.add(jsonObjectStdSections);
                }
            }
            jsonObjectSchool.add("Schools", jsonArrayStdSections);
            jsonArraySchool.add(jsonObjectSchool);

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }

    // Upload API

    private void sendVoiceToAllSchoolAPI() {
//        String tempBaseURl = "http://106.51.127.215:8089/ERP/";
        String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherMgtVoiceCircular.this);
        String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherMgtVoiceCircular.this);
        String mobileNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherMgtVoiceCircular.this);
        final String callerType;
        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherMgtVoiceCircular.this).equals(LOGIN_TYPE_ADMIN))
            callerType = "A";
        else callerType = "M";
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(fileRecordedFilePath.getPath());
        Log.d("FILE_Path", fileRecordedFilePath.getPath());
//        File file = FileUtils.getFile(this, Uri.parse(recFilePath));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_SendVoiceMgtAdmin(schoolID, staffID, callerType, mobileNumber, getIntent().getExtras().getString("MEDIA_DURATION", "0"));
//        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        // Or
//        RequestBody requestContent =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtVoiceCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceMgtAdminToAllSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {

//                                Bundle b = new Bundle();
//                                b.putString("status", "done");
//                                Intent intent = new Intent();
//                                intent.putExtras(b);
//                                setResult(RESULT_OK, intent);

//                                finish();
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private void sendAdminVoiceToListOfSchoolsAPI() {
//        String tempBaseURl = "http://106.51.127.215:8089/ERP/";
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(fileRecordedFilePath.getPath());
        Log.d("FILE_Path", fileRecordedFilePath.getPath());
//        File file = FileUtils.getFile(this, Uri.parse(recFilePath));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonArray jsonReqArray = constructJsonArrayAdminSchools();
//        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        // Or
//        RequestBody requestContent =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtVoiceCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceAdminToSchools(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {

//                                Bundle b = new Bundle();
//                                b.putString("status", "done");
//                                Intent intent = new Intent();
//                                intent.putExtras(b);
//                                setResult(RESULT_OK, intent);

//                                finish();
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonArray constructJsonArrayAdminSchools() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
            for (int i = 0; i < seletedschoollist.size(); i++) {
                JsonObject jsonObjectSchool = new JsonObject();

                jsonObjectSchool.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                jsonObjectSchool.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                jsonObjectSchool.addProperty("Duration", getIntent().getExtras().getString("MEDIA_DURATION", "0"));
                jsonArraySchool.add(jsonObjectSchool);
            }

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }

    private void sendMgtVoiceToListOfSchoolsAPI() {
//        String tempBaseURl = "http://106.51.127.215:8089/ERP/";
        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(fileRecordedFilePath.getPath());
        Log.d("FILE_Path", fileRecordedFilePath.getPath());
//        File file = FileUtils.getFile(this, Uri.parse(recFilePath));

        // create RequestBody instance from file
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

//        // add another part within the multipart request
        JsonArray jsonReqArray = constructJsonArrayMgtSchools();
//        String descriptionString = "hello, this is description speaking";
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());

        // Or
//        RequestBody requestContent =
//                RequestBody.create(
//                        MediaType.parse("multipart/form-data"), jsonReqArray.toString());

        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherMgtVoiceCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Call<JsonArray> call = apiService.SendVoiceMgtStdGrp(requestBody, bodyFile);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", "" + response.body().toString());

                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String strStatus = jsonObject.getString("Status");
                            String strMsg = jsonObject.getString("Message");
                            showToast(strMsg);


                            if ((strStatus.toLowerCase()).equals("y")) {

//                                Bundle b = new Bundle();
//                                b.putString("status", "done");
//                                Intent intent = new Intent();
//                                intent.putExtras(b);
//                                setResult(RESULT_OK, intent);

//                                finish();
                                backToResultActvity("SENT");
                            }
                        } else {
                            showToast(getResources().getString(R.string.check_internet));
                        }


                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonArray constructJsonArrayMgtSchools() {
        JsonArray jsonArraySchool = new JsonArray();

        try {

            for (int i = 0; i < seletedschoollist.size(); i++) {

//                if (selectedStdSecStudList.get(i).isSelectStatus())
                {
                    JsonObject jsonObjectSchool = new JsonObject();
                    jsonObjectSchool.addProperty("SchoolID", seletedschoollist.get(i).getStrSchoolID());
                    jsonObjectSchool.addProperty("StaffID", seletedschoollist.get(i).getStrStaffID());
                    jsonObjectSchool.addProperty("Duration", getIntent().getExtras().getString("MEDIA_DURATION", "0"));

                    JsonArray jsonArrayStandards = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleStds = new ArrayList<>();
                    seleStds.addAll(seletedschoollist.get(i).getListClasses());

                    if (seleStds.size() > 0) {
                        for (int j = 0; j < seleStds.size(); j++) {
                            if (seleStds.get(j).isbSelected()) {
                                JsonObject jsonObjectStandards = new JsonObject();
                                jsonObjectStandards.addProperty("TargetCode", seleStds.get(j).getStrID());
                                jsonArrayStandards.add(jsonObjectStandards);
                            }
                        }
                    }

                    JsonArray jsonArrayGroups = new JsonArray();
                    ArrayList<TeacherClassGroupModel> seleGroups = new ArrayList<>();
                    seleGroups.addAll(seletedschoollist.get(i).getListGroups());

                    if (seleGroups.size() > 0) {
                        for (int j = 0; j < seleGroups.size(); j++) {
                            if (seleGroups.get(j).isbSelected()) {
                                JsonObject jsonObjectGrops = new JsonObject();
                                jsonObjectGrops.addProperty("TargetCode", seleGroups.get(j).getStrID());
                                jsonArrayGroups.add(jsonObjectGrops);
                            }
                        }
                    }

                    jsonObjectSchool.add("Seccode", jsonArrayStandards);
                    jsonObjectSchool.add("GrpCode", jsonArrayGroups);
                    jsonArraySchool.add(jsonObjectSchool);
                }
            }

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }


}
