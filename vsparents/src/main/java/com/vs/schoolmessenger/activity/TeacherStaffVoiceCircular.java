package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.TeacherSelectedsectionListAdapter;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.TeacherSectionModel;
import com.vs.schoolmessenger.model.TeacherStudentsModel;
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

import static com.vs.schoolmessenger.activity.TeacherListAllSection.SELECTED_STD_SEC_STUD_CODE;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.list_staffStdSecs;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.strNoClassWarning;


public class TeacherStaffVoiceCircular extends AppCompatActivity implements View.OnClickListener {
    Button btnChoose, btnSend;
    RecyclerView selectedsection;
    TeacherSelectedsectionListAdapter adapter;

    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration;

    private MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();

    LinearLayout llRecipients;
    ArrayList<TeacherSectionModel> stdSecList = new ArrayList<>();
    ArrayList<TeacherSectionModel> selectedStdSecStudList = new ArrayList<>();

    File fileRecordedFilePath;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_send_audio_staff);



        ImageView ivBack = (ImageView) findViewById(R.id.staffVoice_toolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);

        selectedsection = (RecyclerView) findViewById(R.id.rvselectedsectionList1);

        btnChoose = (Button) findViewById(R.id.staffVoice_btnchoose);
        btnChoose.setOnClickListener(this);

        btnSend = (Button) findViewById(R.id.staffVoice_btnSend);
        btnSend.setOnClickListener(this);

        adapter = new TeacherSelectedsectionListAdapter(TeacherStaffVoiceCircular.this, selectedStdSecStudList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 3);
        selectedsection.setHasFixedSize(true);
        selectedsection.setLayoutManager(mLayoutManager);
        selectedsection.setItemAnimator(new DefaultItemAnimator());
        selectedsection.setAdapter(adapter);

        llRecipients = (LinearLayout) findViewById(R.id.staffVoice_llRecipient);
        llRecipients.setVisibility(View.GONE);
        btnSend.setEnabled(false);

        setupAudioPlayer();
        fetchSong();
        stdSecListAPI();
    }


    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

        backToResultActvity("BACK");
    }

    private void backToResultActvity(String msg) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("MESSAGE", msg);
        setResult(1, returnIntent);
        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.staffVoice_btnchoose:
                Intent inStdSec = new Intent(TeacherStaffVoiceCircular.this, TeacherListAllSection.class);
                inStdSec.putExtra("STD_SEC_LIST", stdSecList);
                startActivityForResult(inStdSec, SELECTED_STD_SEC_STUD_CODE);
                break;

            case R.id.staffVoice_btnSend:
                sendVoiceCircularRetroFit();
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_STD_SEC_STUD_CODE) {

            String message = data.getStringExtra("MESSAGE");
            if (message.equals("OK")) {

                llRecipients.setVisibility(View.VISIBLE);
                btnSend.setEnabled(true);
                adapter.clearAllData();
                selectedStdSecStudList.addAll((ArrayList<TeacherSectionModel>) data.getSerializableExtra("STUDENTS"));


                adapter.notifyDataSetChanged();

            } else {
                llRecipients.setVisibility(View.GONE);
                btnSend.setEnabled(false);
            }
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

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }


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

    private void stdSecListAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffVoiceCircular.this);
        final String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffVoiceCircular.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonArray jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_SubjectHandling(schoolID, staffID);
        Call<JsonArray> call = apiService.staffSubjectHandling(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("SubjectHandling:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("SubjectHandling:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strSubName = jsonObject.getString("SubjectName");
                        String strSubCode = jsonObject.getString("SubjectCode");

                        if (strSubName.equals("")) {

                            list_staffStdSecs = new ArrayList<>();
                            strNoClassWarning = strSubCode;
                        } else {
                            TeacherSectionModel sectionModel;
                            Log.d("json length", js.length() + "");
                            for (int i = 0; i < js.length(); i++) {
                                jsonObject = js.getJSONObject(i);
                                sectionModel = new TeacherSectionModel(false, jsonObject.getString("Class"), jsonObject.getString("Section")
                                        , jsonObject.getString("ClassSecCode"), jsonObject.getString("SubjectName")
                                        , jsonObject.getString("SubjectCode"), jsonObject.getString("NoOfStudents"), jsonObject.getString("NoOfStudents"), true);
                                stdSecList.add(sectionModel);
                            }

                            list_staffStdSecs = new ArrayList<>();
                            list_staffStdSecs.addAll(stdSecList);
                        }

                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                        onBackPressed();
                    }

                } catch (Exception e) {
                    try {
                        JSONArray jsError = new JSONArray(response.body().toString());
                        if (jsError.length() > 0) {
                            JSONObject jsonObjectError = jsError.getJSONObject(0);

                            String msg = jsonObjectError.getString("Message");
                            showToast(msg);
                        }
                    } catch (Exception ex) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.e("SubjectHandling:Excep", ex.getMessage());
                    }
                    onBackPressed();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("SubjectHandling:Failure", t.toString());
                onBackPressed();
            }
        });
    }


    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void sendVoiceCircularRetroFit() {


        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        File file = new File(fileRecordedFilePath.getPath());
        Log.d("FILE_Path", fileRecordedFilePath.getPath());

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part bodyFile =
                MultipartBody.Part.createFormData("voice", file.getName(), requestFile);

        JsonArray jsonReqArray = constructResultJsonArray();
        RequestBody requestBody =
                RequestBody.create(
                        MultipartBody.FORM, jsonReqArray.toString());



        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherStaffVoiceCircular.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();

        Log.d("BaseURL2", TeacherSchoolsApiClient.BASE_URL);

        Call<JsonArray> call = apiService.StaffwiseVoice(requestBody, bodyFile);
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
                Log.d("Upload error:", t.getMessage()+"\n"+t.toString());
                showToast(t.toString());
            }
        });
    }

    private JsonArray constructResultJsonArray() {
        JsonArray jsonArraySchool = new JsonArray();

        try {
            String schoolID = TeacherUtil_SharedPreference.getSchoolIdFromSP(TeacherStaffVoiceCircular.this);
            String staffID = TeacherUtil_SharedPreference.getStaffIdFromSP(TeacherStaffVoiceCircular.this);

            JsonObject jsonObjectSchool = new JsonObject();
            jsonObjectSchool.addProperty("SchoolID", schoolID);
            jsonObjectSchool.addProperty("StaffID", staffID);
            jsonObjectSchool.addProperty("Duration", getIntent().getExtras().getString("MEDIA_DURATION", "0"));

            JsonArray jsonArrayStdSections = new JsonArray();
            for (int i = 0; i < selectedStdSecStudList.size(); i++) {

                {
                    JsonObject jsonObjectStdSections = new JsonObject();
                    jsonObjectStdSections.addProperty("TargetCode", selectedStdSecStudList.get(i).getStdSecCode());
                    jsonObjectStdSections.addProperty("SubCode", selectedStdSecStudList.get(i).getSubjectCode());
                    String strMsgToAll = "T";
                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected())
                        strMsgToAll = "F";
                    jsonObjectStdSections.addProperty("MessageToAll", strMsgToAll);

                    JsonArray jsonArrayStudents = new JsonArray();

                    if (!selectedStdSecStudList.get(i).isAllStudentsSelected()) {
                        ArrayList<TeacherStudentsModel> seleStudents = new ArrayList<>();
                        seleStudents.addAll(selectedStdSecStudList.get(i).getStudentsList());

                        for (int j = 0; j < seleStudents.size(); j++) {
                            if (seleStudents.get(j).isSelectStatus()) {
                                JsonObject jsonObjectStudents = new JsonObject();
                                jsonObjectStudents.addProperty("ID", seleStudents.get(j).getStudentID());
                                jsonArrayStudents.add(jsonObjectStudents);
                            }
                        }

                    }

                    jsonObjectStdSections.add("IDS", jsonArrayStudents);
                    jsonArrayStdSections.add(jsonObjectStdSections);
                }
            }
            jsonObjectSchool.add("Seccode", jsonArrayStdSections);
            jsonArraySchool.add(jsonObjectSchool);

            Log.d("Final_Array", jsonArraySchool.toString());

        } catch (Exception e) {
            Log.d("ASDF", e.toString());
        }

        return jsonArraySchool;
    }
}
