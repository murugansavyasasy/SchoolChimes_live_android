package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.PRINCIPAL_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.STAFF_TEXT_HW;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.LsrwDocsAdapter;
import com.vs.schoolmessenger.assignment.ContentAdapter;
import com.vs.schoolmessenger.aws.S3Uploader;
import com.vs.schoolmessenger.aws.S3Utils;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.UploadDocListener;
import com.vs.schoolmessenger.model.UploadFilesModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AwsUploadingPreSigned;
import com.vs.schoolmessenger.util.CurrentDatePicking;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.UploadCallback;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import com.vs.schoolmessenger.util.VimeoUploader;
import com.vs.schoolmessenger.videoalbum.AlbumVideoSelectVideoActivity;

import net.ypresto.androidtranscoder.MediaTranscoder;
import net.ypresto.androidtranscoder.format.MediaFormatStrategyPresets;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ParentSubmitLSRW extends AppCompatActivity implements View.OnClickListener, UploadDocListener, VimeoUploader.UploadCompletionListener {

    public static PopupWindow popupWindowvideo;
    public static ArrayList<String> gallerylist = new ArrayList<>();
    public static ArrayList<String> contentlist = new ArrayList<>();
    Spinner spinitem;
    EditText edtextmsg;
    RecyclerView rcysubmissions, rcyselected;
    RelativeLayout rytvoice, voiceplay;
    LinearLayout lnrImages;
    VideoView img1;
    ConstraintLayout parentBrowseFile, parentImagefile, parentVideofile;
    ImageView ivRecord, lblImageCount1;
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration, tvRecordDuration, tvRecordTitle, lblAddAttachment, lblvideo, lblBrowse, lblImage;
    ArrayAdapter<String> spintypeitem;
    List<String> listtypes = new ArrayList<>();
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int recTime;
    MediaRecorder recorder;
    Handler recTimerHandler = new Handler();
    boolean bIsRecording = false;
    int iMediaDuration = 0;
    int iMaxRecDur;
    String imageFilePath;
    String DocFilePath;
    ProgressDialog progressDialog;
    S3Uploader s3uploaderObj;
    String fileNameDateTime;
    String urlFromS3 = null;
    String contentType = "", filecontent;
    LsrwDocsAdapter uploadAdapter;
    Button btnadd, btnsubmit;
    int pathIndex = 0;
    int SELECT_VIDEO = 1;
    int SELECT_IMAGE = 2;
    int SELECT_PDF = 3;
    int CAMERA = 4;
    File photoFile, file, futureStudioIconFile;
    ContentAdapter contentadapter;
    String upload_link, strsize, iframe, link, imagesize;
    String ticket_id;
    String video_file_id;
    String signature;
    String v6;
    String redirect_url, selected, skillid;
    Future<Void> mFuture;
    private MediaPlayer mediaPlayer;
    private int iRequestCode;
    private final List<UploadFilesModel> UploadedS3URlList = new ArrayList<>();
    private final List<UploadFilesModel> submitlist = new ArrayList<>();
    private final List<UploadFilesModel> imagePathList = new ArrayList<>();
    AwsUploadingPreSigned isAwsUploadingPreSigned;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.activity_parent_submit_l_s_r_w);

        getSupportActionBar().setDisplayOptions(androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText("Submit LSRW");
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");

        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        spinitem = findViewById(R.id.spinitem);
        edtextmsg = findViewById(R.id.edtextmsg);
        rytvoice = findViewById(R.id.rytvoice);
        voiceplay = findViewById(R.id.voiceplay);
        parentBrowseFile = findViewById(R.id.parentBrowseFile);
        parentImagefile = findViewById(R.id.parentImagefile);
        parentVideofile = findViewById(R.id.parentVideofile);
        rcysubmissions = findViewById(R.id.rcysubmissions);
        rcyselected = findViewById(R.id.rcyselected);
        btnadd = findViewById(R.id.btnadd);
        btnsubmit = findViewById(R.id.btnsubmit);
        lnrImages = findViewById(R.id.lnrImages);
        lblImageCount1 = findViewById(R.id.lblImageCount1);
        img1 = findViewById(R.id.img1);
        lblvideo = findViewById(R.id.lblvideo);
        lblBrowse = findViewById(R.id.lblBrowse);
        lblImage = findViewById(R.id.lblImage);
        lblAddAttachment = findViewById(R.id.lblAddAttachment);

        ivRecord = findViewById(R.id.staffVoice_ivRecord);
        tvRecordDuration = findViewById(R.id.staffVoice_tvRecDuration);
        tvRecordTitle = findViewById(R.id.staffVoice_tvRecTitle);
        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        ivRecord.setOnClickListener(this);
        parentImagefile.setOnClickListener(this);
        parentBrowseFile.setOnClickListener(this);
        parentVideofile.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        btnsubmit.setOnClickListener(this);
        lblImageCount1.setOnClickListener(this);

        isAwsUploadingPreSigned = new AwsUploadingPreSigned();
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);
        skillid = getIntent().getStringExtra("skillid");
        Log.d("skillID", skillid);
        s3uploaderObj = new S3Uploader(ParentSubmitLSRW.this);
        listtypes.clear();
        listtypes.add("Text");
        listtypes.add("Voice");
        listtypes.add("Image");
        listtypes.add("Pdf");
        listtypes.add("Video");

        submitlist.clear();
        spintypeitem = new ArrayAdapter<>(ParentSubmitLSRW.this, R.layout.lsrw_spin_title, listtypes);
        spintypeitem.setDropDownViewResource(R.layout.teacher_spin_dropdown);
        spinitem.setAdapter(spintypeitem);

        spinitem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                imagePathList.clear();
                UploadedS3URlList.clear();
                if (mediaPlayer.isPlaying())
                    mediaPlayer.stop();

                if (bIsRecording)
                    stop_RECORD();
                selected = parent.getItemAtPosition(position).toString();
                edtextmsg.setVisibility(View.GONE);
                rytvoice.setVisibility(View.GONE);
                voiceplay.setVisibility(View.GONE);
                parentImagefile.setVisibility(View.GONE);
                parentBrowseFile.setVisibility(View.GONE);
                parentVideofile.setVisibility(View.GONE);
                lnrImages.setVisibility(View.GONE);
                rcyselected.setVisibility(View.GONE);
                lblAddAttachment.setVisibility(View.GONE);
                btnadd.setText(" Add File Attachments ");

                switch (selected) {
                    case "Text":
                        edtextmsg.setVisibility(View.VISIBLE);
                        btnadd.setText("Add Content");
                        btnadd.setEnabled(true);
                        break;
                    case "Voice":
                        rytvoice.setVisibility(View.VISIBLE);
                        btnadd.setEnabled(false);
                        break;
                    case "Image":
                        parentImagefile.setVisibility(View.VISIBLE);
                        btnadd.setEnabled(false);
                        break;
                    case "Pdf":
                        parentBrowseFile.setVisibility(View.VISIBLE);
                        btnadd.setEnabled(false);
                        break;
                    case "Video":
                        parentVideofile.setVisibility(View.VISIBLE);
                        btnadd.setEnabled(false);
                        break;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        iMaxRecDur = 181;

        setupAudioPlayer();

        if (submitlist.size() != 0) {
            rcysubmissions.setVisibility(View.VISIBLE);
            lblAddAttachment.setVisibility(View.VISIBLE);
            setAdapter();
            btnsubmit.setEnabled(true);
        } else {
            rcysubmissions.setVisibility(View.GONE);
            lblAddAttachment.setVisibility(View.GONE);
            btnsubmit.setEnabled(false);
        }
        if (imagePathList.size() != 0) {
            rcyselected.setVisibility(View.VISIBLE);
            selectedsetAdapter();
            btnadd.setEnabled(true);
        } else {
            rcyselected.setVisibility(View.GONE);
            btnadd.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.staffVoice_ivRecord:
                if (bIsRecording)
                    stop_RECORD();
                else start_RECORD();
                break;

            case R.id.btnsubmit:
                submitquiz();
                break;

            case R.id.parentBrowseFile:

                String[] mimeTypes =
                        {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                                "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                                "text/plain",
                                "text/csv",
                                "application/csv",
                                "application/pdf",
                                "image/jpeg",
                                "image/jpg",
                                "image/png",
                                "image/gif",
                                "application/rar",
                                "application/zip",
                        };

                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), SELECT_PDF);

                break;

            case R.id.parentVideofile:
                videotermsapi();


                break;

            case R.id.parentImagefile:
                showFilePopup(v);

                break;

            case R.id.btnadd:

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getCurrentFocus();
                //If no view currently has focus, create a new one, just so we can grab a window token from it
                if (view == null) {
                    view = new View(this);
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (selected.equals("Text")) {
                    if (!edtextmsg.getText().toString().equals("")) {
                        submitlist.add(new UploadFilesModel(edtextmsg.getText().toString(), "TEXT"));
                        UploadedS3URlList.add(new UploadFilesModel(edtextmsg.getText().toString(), "text"));
                        edtextmsg.setText("");
                    } else {
                        alert("Please Enter Text");
                    }
                } else if (selected.equals("Video")) {
                    //   VimeoAPi();
                    uploadVimeoVideo();
                } else if (selected.equals("Voice")) {
                    DocFilePath = futureStudioIconFile.getPath();

                    imagePathList.add(new UploadFilesModel(DocFilePath, "VOICE"));
                    // UploadFileToAWS("voice_from_lsrw", 0);
                    isUploadAWS("audio", ".mp3", "", "voice_from_lsrw");
                } else if (selected.equals("Image")) {

                    //UploadFileToAWS("image_from_lsrw", 0);
                    isUploadAWS("image", "IMG", "", "image_from_lsrw");
                } else if (selected.equals("Pdf")) {
                    isUploadAWS("pdf", ".pdf", "", "pdf_from_lsrw");
                    //    UploadFileToAWS("pdf_from_lsrw", 0);
                }
                if (submitlist.size() != 0) {
                    rcysubmissions.setVisibility(View.VISIBLE);
                    lblAddAttachment.setVisibility(View.VISIBLE);
                    setAdapter();
                } else {
                    rcysubmissions.setVisibility(View.GONE);
                    lblAddAttachment.setVisibility(View.GONE);
                }
                break;


        }
    }

    private void isUploadAWS(String contentType, String isType, String value,String isLSRWtype) {

        Log.d("selectedImagePath", String.valueOf(imagePathList.size()));

        String currentDate = CurrentDatePicking.getCurrentDate();
        String instituteID = Util_SharedPreference.getSchoolId(ParentSubmitLSRW.this);

        for (int i = 0; i < imagePathList.size(); i++) {
            AwsUploadingFile(String.valueOf(imagePathList.get(i).getWsUploadedDoc()), currentDate + "/" + instituteID, contentType, isType, value,isLSRWtype);
        }
    }


    private void AwsUploadingFile(String isFilePath, String bucketPath, String isFileExtension, String filetype, String type,String isLSRWtype) {

        String countryID = TeacherUtil_SharedPreference.getCountryID(ParentSubmitLSRW.this);


        isAwsUploadingPreSigned.getPreSignedUrl(isFilePath, bucketPath, isFileExtension, this,countryID,false, new UploadCallback() {
            @Override
            public void onUploadSuccess(String response, String isAwsFile) {
                Log.d("Upload Success", response);

                UploadFilesModel files;
                files = new UploadFilesModel(isAwsFile, isLSRWtype);

                UploadedS3URlList.add(files);
                if (selected.equals("Voice")) {
                    submitlist.add(new UploadFilesModel(isAwsFile, "VOICE"));
                    setAdapter();
                    voiceplay.setVisibility(View.GONE);
                    btnadd.setEnabled(false);

                } else if (selected.equals("Image")) {
                    submitlist.add(new UploadFilesModel(isAwsFile, "IMAGE"));
                    setAdapter();
                    rcyselected.setVisibility(View.GONE);
                    btnadd.setEnabled(false);
                    lblImage.setText("Upload Image");
                } else if (selected.equals("Pdf")) {
                    submitlist.add(new UploadFilesModel(isAwsFile, "PDF"));
                    setAdapter();
                    rcyselected.setVisibility(View.GONE);
                    btnadd.setEnabled(false);
                    lblBrowse.setText("Browse File");
                }
                if (submitlist.size() == 0) {
                    rcysubmissions.setVisibility(View.GONE);
                    lblAddAttachment.setVisibility(View.GONE);

                } else {
                    rcysubmissions.setVisibility(View.VISIBLE);
                    lblAddAttachment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onUploadError(String error) {
                Log.e("Upload Error", error);
            }
        });
    }



    private void uploadVimeoVideo() {
        String authToken = TeacherUtil_SharedPreference.getVideotoken(ParentSubmitLSRW.this);
        String filepath = imagePathList.get(0).getWsUploadedDoc();
        VimeoUploader.uploadVideo(ParentSubmitLSRW.this, "quiz", "quiz", authToken, filepath, this);
    }

    //VOICE
    @Override
    public void onBackPressed() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();

        if (bIsRecording)
            stop_RECORD();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (imagePathList.size() != 0 && selected.equals("Video")) {
            lnrImages.setVisibility(View.VISIBLE);

            img1.setVideoPath(imagePathList.get(0).getWsUploadedDoc());
            img1.setVisibility(View.VISIBLE);
            img1.seekTo(1);
            lblImageCount1.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.teacher_ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.teacher_colorPrimary));
        }

        if (bIsRecording)
            stop_RECORD();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private void start_RECORD() {
        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_stop);
        ivRecord.setImageResource(R.drawable.teacher_ic_stop);
        voiceplay.setVisibility(View.GONE);
        btnadd.setEnabled(false);
        tvRecordTitle.setText(getText(R.string.teacher_txt_stop_record));

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioEncodingBitRate(256000);
            recorder.setAudioSamplingRate(44100);
            recorder.setOutputFile(getRecFilename());
            recorder.prepare();
            recorder.start();

            recTimeUpdation();
            bIsRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop_RECORD() {
        recorder.stop();
        recTimerHandler.removeCallbacks(runson);
        bIsRecording = false;
        tvRecordTitle.setText(getText(R.string.teacher_txt_start_record));
        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
        ivRecord.setImageResource(R.drawable.teacher_ic_mic);
        voiceplay.setVisibility(View.VISIBLE);

        btnadd.setEnabled(true);
        fetchSong();
    }

    private String getRecFilename() {
        String filepath;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            filepath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

        } else {
            filepath = Environment.getExternalStorageDirectory().getPath();
        }

        File fileDir = new File(filepath, VOICE_FOLDER_NAME);

        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        File fileNamePath = new File(fileDir, VOICE_FILE_NAME);
        Log.d("FILE_PATH", fileNamePath.getPath());
        return (fileNamePath.getPath()); //+ System.currentTimeMillis()
    }

    public void recTimeUpdation() {
        recTime = 1;
        recTimerHandler.postDelayed(runson, 1000);
    }

    public void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {
//            String filepath = Environment.getExternalStorageDirectory().getPath();

            String filepath;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                filepath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

            } else {
                filepath = Environment.getExternalStorageDirectory().getPath();
            }

            File file = new File(filepath, VOICE_FOLDER_NAME);
            File dir = new File(file.getAbsolutePath());

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }

            futureStudioIconFile = new File(dir, VOICE_FILE_NAME);
            System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(futureStudioIconFile.getPath());
            mediaPlayer.prepare();
            iMediaDuration = (int) (mediaPlayer.getDuration() / 1000.0);

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }    private final Runnable runson = new Runnable() {
        @Override
        public void run() {
            tvRecordDuration.setText(milliSecondsToTimer(recTime * 1000L));
            recTime = recTime + 1;


            if (recTime != iMaxRecDur) {
                recTimerHandler.postDelayed(this, 1000);
            } else {
                stop_RECORD();
                Log.d("stoprecord", "stoprecord");
            }
        }
    };

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

    private void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    //IMAGE

    private void showLoading() {
        {
            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Uploading..");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
        }
    }

    private void showFilePopup(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.profile_popup, null);
        final PopupWindow popupWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT, true);
        popupWindow.setContentView(layout);

        RelativeLayout rytGallery = (RelativeLayout) layout.findViewById(R.id.rytGallery);
        RelativeLayout rytCamera = (RelativeLayout) layout.findViewById(R.id.rytCamera);
        ImageView imgClose = (ImageView) layout.findViewById(R.id.imgClose);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);


        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        rytGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ParentSubmitLSRW.this, AlbumSelectActivity.class);
                i.putExtra("ProfileScreen", "Profile");
                startActivityForResult(i, SELECT_IMAGE);
                popupWindow.dismiss();

            }
        });
        rytCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                photoFile = createImageFile();
                Log.d("photoFile", photoFile.toString());
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(ParentSubmitLSRW.this, "com.vs.schoolmessenger.provider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(intent, CAMERA);
                    popupWindow.dismiss();
                }

            }
        });
    }

    private File createImageFile() {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "PROFILE_IMG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    private void UploadFileToAWS(final String FileDisplayname, int pathind) {
        pathIndex = pathind;

        progressDialog = new ProgressDialog(ParentSubmitLSRW.this);
        for (int index = pathIndex; index < imagePathList.size(); index++) {

            DocFilePath = imagePathList.get(index).getWsUploadedDoc();
            break;
        }
        Log.d("upload", String.valueOf(UploadedS3URlList.size()));
        Log.d("image", String.valueOf(imagePathList.size()));

        String instituteID = Util_SharedPreference.getSchoolId(ParentSubmitLSRW.this);
        String countryID = TeacherUtil_SharedPreference.getCountryID(ParentSubmitLSRW.this);

        if (UploadedS3URlList.size() < imagePathList.size()) {
            if (DocFilePath != null) {
                showLoading();
                fileNameDateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(Calendar.getInstance().getTime());
                fileNameDateTime = "File_" + fileNameDateTime;
                Log.d("S3contentType", contentType);
                s3uploaderObj.initUpload(DocFilePath, contentType, fileNameDateTime, instituteID, countryID, false);
                s3uploaderObj.setOns3UploadDone(new S3Uploader.S3UploadInterface() {
                    @Override
                    public void onUploadSuccess(String response) {
                        hideLoading();

                        if (response.equalsIgnoreCase("Success")) {
                            urlFromS3 = S3Utils.generates3ShareUrl(getApplicationContext(), DocFilePath, fileNameDateTime, instituteID, countryID, false);
                            Log.d("urlFromS3", urlFromS3);
                            if (!TextUtils.isEmpty(urlFromS3)) {

                                UploadFilesModel files;
                                files = new UploadFilesModel(urlFromS3, FileDisplayname);


                                UploadedS3URlList.add(files);
                                UploadFileToAWS(FileDisplayname, pathIndex + 1);
                                if (selected.equals("Voice")) {
                                    submitlist.add(new UploadFilesModel(urlFromS3, "VOICE"));
                                    setAdapter();
                                    voiceplay.setVisibility(View.GONE);
                                    btnadd.setEnabled(false);

                                } else if (selected.equals("Image")) {
                                    submitlist.add(new UploadFilesModel(urlFromS3, "IMAGE"));
                                    setAdapter();
                                    rcyselected.setVisibility(View.GONE);
                                    btnadd.setEnabled(false);
                                    lblImage.setText("Upload Image");
                                } else if (selected.equals("Pdf")) {
                                    submitlist.add(new UploadFilesModel(urlFromS3, "PDF"));
                                    setAdapter();
                                    rcyselected.setVisibility(View.GONE);
                                    btnadd.setEnabled(false);
                                    lblBrowse.setText("Browse File");
                                }
                                if (submitlist.size() == 0) {
                                    rcysubmissions.setVisibility(View.GONE);
                                    lblAddAttachment.setVisibility(View.GONE);

                                } else {
                                    rcysubmissions.setVisibility(View.VISIBLE);
                                    lblAddAttachment.setVisibility(View.VISIBLE);

                                }

                            }
                        }
                    }

                    @Override
                    public void onUploadError(String response) {
                        hideLoading();
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_VIDEO && null != data) {
            imagePathList.clear();
            long sizekb = 0;
            imagesize = TeacherUtil_SharedPreference.getVideosize(ParentSubmitLSRW.this);

            Log.d("length", String.valueOf(imagesize));
            sizekb = (1024 * 1024) * Integer.parseInt(imagesize);
            ArrayList<String> path;

            path = data.getStringArrayListExtra("images");
            for (int y = 0; y < path.size(); y++) {

                if (y == 0) {

                    File img = new File(path.get(0));
                    long pathlength = img.length();
                    String check = img.toString();
                    check = check.substring(check.lastIndexOf(".") + 1);
                    if (check.equalsIgnoreCase("mp4") || check.equalsIgnoreCase("mov") ||
                            check.equalsIgnoreCase("flv") || check.equalsIgnoreCase("avi") ||
                            check.equalsIgnoreCase("wmv")) {
                        if (pathlength <= sizekb) {
                            lnrImages.setVisibility(View.VISIBLE);

                            img1.setVideoPath(path.get(0));
                            img1.setVisibility(View.VISIBLE);
                            img1.seekTo(1);
                            lblImageCount1.setVisibility(View.VISIBLE);

                            imagePathList.add(new UploadFilesModel(path.get(0), "VIDEO"));

                            if (imagePathList.size() != 0) {

                                lblvideo.setText("Change Video");
                                btnadd.setEnabled(true);
                            } else {

                                lblvideo.setText("Upload Video");
                                btnadd.setEnabled(false);
                            }

                            try {
                                File outputDir = new File(getExternalFilesDir(null), "outputs");
                                outputDir.mkdir();
                                file = File.createTempFile("transcode_test", ".mp4", outputDir);
                            } catch (IOException e) {
                                return;
                            }
                            ContentResolver resolver = getContentResolver();
                            final ParcelFileDescriptor parcelFileDescriptor;
                            File file1 = new File(imagePathList.get(0).getWsUploadedDoc());
                            try {

                                parcelFileDescriptor = resolver.openFileDescriptor(Uri.fromFile(file1), "r");
                            } catch (FileNotFoundException e) {
                                Log.w("Could not open '" + Uri.fromFile(file1) + "'", e);
                                return;
                            }
                            assert parcelFileDescriptor != null;
                            final FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                            final ProgressDialog progressBar = new ProgressDialog(ParentSubmitLSRW.this);
                            progressBar.setCancelable(false);
                            progressBar.setMessage("loading");
                            progressBar.show();
                            final long startTime = SystemClock.uptimeMillis();
                            MediaTranscoder.Listener listener = new MediaTranscoder.Listener() {
                                @Override
                                public void onTranscodeProgress(double progress) {
                                    if (progress < 0) {
                                        progressBar.setIndeterminate(true);
                                    } else {
                                        progressBar.setIndeterminate(false);
                                        progressBar.setProgress((int) Math.round(progress * 100));
                                    }
                                }

                                @Override
                                public void onTranscodeCompleted() {
                                    progressBar.dismiss();
                                    Log.d("test", "transcoding took " + (SystemClock.uptimeMillis() - startTime) + "ms");
                                    onTranscodeFinished(true, parcelFileDescriptor);

                                }

                                @Override
                                public void onTranscodeCanceled() {
                                    progressBar.dismiss();
                                    onTranscodeFinished(false, parcelFileDescriptor);
                                    file = new File(imagePathList.get(0).getWsUploadedDoc());
                                }

                                @Override
                                public void onTranscodeFailed(Exception exception) {
                                    progressBar.dismiss();
                                    onTranscodeFinished(false, parcelFileDescriptor);
                                    file = new File(imagePathList.get(0).getWsUploadedDoc());

                                }
                            };
                            Log.d("TAG", "transcoding into " + file);
                            mFuture = MediaTranscoder.getInstance().transcodeVideo(fileDescriptor, file.getAbsolutePath(),
                                    MediaFormatStrategyPresets.createExportPreset960x540Strategy(), listener);
                        } else {
                            filecontent = TeacherUtil_SharedPreference.getVideoalert(ParentSubmitLSRW.this);
                            alert(filecontent);
                        }
                    } else {
                        alert("Please Choose Valid Video format to send");
                    }

                }
            }
        } else if (requestCode == SELECT_IMAGE && null != data) {
            try {
                if (resultCode == RESULT_OK) {
                    imagePathList.clear();
                    String imageSize = TeacherUtil_SharedPreference.getImagesize(ParentSubmitLSRW.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(imageSize));

                    gallerylist = data.getStringArrayListExtra("images");
                    for (int i = 0; i < gallerylist.size(); i++) {
                        imageFilePath = gallerylist.get(i);
                        Log.d("imglist", imageFilePath);

                        File file = new File(gallerylist.get(i));

                        long pathlength = file.length();
                        if (pathlength <= maxSize) {
                            imagePathList.add(new UploadFilesModel(imageFilePath, "IMAGE"));
                            if (imagePathList.size() != 0) {
                                rcyselected.setVisibility(View.VISIBLE);
                                selectedsetAdapter();
                                lblImage.setText("Change Image");
                            } else {
                                rcyselected.setVisibility(View.GONE);
                                lblImage.setText("Upload Image");
                            }

                        } else {
                            String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitLSRW.this);
                            alert(filecontent);
                        }
                    }


                }
            } catch (Exception e) {

            }
        } else if (requestCode == CAMERA && null != data) {
            try {
                if (resultCode == RESULT_OK) {
                    gallerylist.clear();
                    imagePathList.clear();
                    String imageSize = TeacherUtil_SharedPreference.getImagesize(ParentSubmitLSRW.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(imageSize));

                    gallerylist = data.getStringArrayListExtra("images");
                    for (int i = 0; i < gallerylist.size(); i++) {
                        imageFilePath = gallerylist.get(i);
                        File file = new File(gallerylist.get(i));

                        long pathlength = file.length();
                        if (pathlength <= maxSize) {
                            imagePathList.add(new UploadFilesModel(imageFilePath, "IMAGE"));
                            if (imagePathList.size() != 0) {
                                rcyselected.setVisibility(View.VISIBLE);
                                selectedsetAdapter();
                                lblImage.setText("Change Image");
                            } else {
                                rcyselected.setVisibility(View.GONE);
                                lblImage.setText("Upload Image");
                            }

                        } else {
                            String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitLSRW.this);
                            alert(filecontent);
                        }
                    }

                }
            } catch (Exception e) {

            }
        } else if (requestCode == SELECT_PDF) {
            try {
                if (data != null && resultCode == RESULT_OK) {
                    imagePathList.clear();
                    String pdfSize = TeacherUtil_SharedPreference.getPdfsize(ParentSubmitLSRW.this);
                    long maxSize = (1024 * 1024) * Integer.parseInt(String.valueOf(pdfSize));

                    Uri uri = data.getData();
                    String mimeType = getContentResolver().getType(uri);

                    final MimeTypeMap mime = MimeTypeMap.getSingleton();
                    String extension = mime.getExtensionFromMimeType(ParentSubmitLSRW.this.getContentResolver().getType(uri));
                    contentType = mimeType;
                    File outputDir = ParentSubmitLSRW.this.getCacheDir(); // context being the Activity pointer
                    File outputFile = File.createTempFile("School_Upload_document_", "." + extension, outputDir);
                    try (InputStream in = getContentResolver().openInputStream(uri)) {
                        if (in == null) return;
                        try (OutputStream out = getContentResolver().openOutputStream(Uri.fromFile(outputFile))) {
                            if (out == null) return;
                            // Transfer bytes from in to out
                            byte[] buf = new byte[1024];
                            int len;
                            while ((len = in.read(buf)) > 0) {
                                out.write(buf, 0, len);
                            }
                        }
                    }
                    contentType = "Pdf/.pdf";
                    DocFilePath = outputFile.getPath();

                    File file = new File(DocFilePath);
                    long pathlength = file.length();
                    if (pathlength <= maxSize) {
                        imagePathList.add(new UploadFilesModel(DocFilePath, "PDF"));
                        if (imagePathList.size() != 0) {
                            rcyselected.setVisibility(View.VISIBLE);
                            selectedsetAdapter();
                            lblBrowse.setText("Change File");
                        } else {
                            rcyselected.setVisibility(View.GONE);
                            lblBrowse.setText("Browse File");
                        }
                    } else {
                        String filecontent = TeacherUtil_SharedPreference.getFilecontent(ParentSubmitLSRW.this);
                        alert(filecontent);
                    }

                }
            } catch (Exception e) {
                Log.d("Exception", e.getMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //Video
    private void onTranscodeFinished(boolean isSuccess, ParcelFileDescriptor parcelFileDescriptor) {
        final ProgressDialog progressBar = new ProgressDialog(ParentSubmitLSRW.this);
        progressBar.setCancelable(false);
        progressBar.setMessage("loading");
        progressBar.setCancelable(false);
        progressBar.show();
        progressBar.setIndeterminate(false);
        try {
            progressBar.dismiss();
            parcelFileDescriptor.close();
        } catch (IOException e) {
            progressBar.dismiss();
            Log.w("Error while closing", e);
        }
    }

    private void showFilePickPopup() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.video_terms, null);
        popupWindowvideo = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        popupWindowvideo.setContentView(layout);
        popupWindowvideo.showAtLocation(layout, Gravity.CENTER, 0, 0);
        RecyclerView ryccontent = (RecyclerView) layout.findViewById(R.id.rycContent);
        Button btnAgree = (Button) layout.findViewById(R.id.btnAgree);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        ryccontent.setLayoutManager(layoutManager);
        ryccontent.setItemAnimator(new DefaultItemAnimator());
        contentadapter = new ContentAdapter(contentlist, ParentSubmitLSRW.this);
        ryccontent.setAdapter(contentadapter);
        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindowvideo.dismiss();
                Intent intent;
                intent = new Intent(ParentSubmitLSRW.this, AlbumVideoSelectVideoActivity.class);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(intent, SELECT_VIDEO);
            }
        });


    }

    private void videotermsapi() {

        String isNewVersion = TeacherUtil_SharedPreference.getNewVersion(ParentSubmitLSRW.this);
        if (isNewVersion.equals("1")) {
            String ReportURL = TeacherUtil_SharedPreference.getReportURL(ParentSubmitLSRW.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(ReportURL);
        } else {
            String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ParentSubmitLSRW.this);
            TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        }

        Log.d("BaseURL", TeacherSchoolsApiClient.BASE_URL);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        Call<JsonArray> call = apiService.GetVideoContentRestriction();
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call,
                                   Response<JsonArray> response) {

                Log.d("Upload-Code:Response", response.code() + "-" + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Upload:Body", response.body().toString());
                    contentlist.clear();
                    try {
                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            for (int i = 0; i < js.length(); i++) {
                                JSONObject jsonObject = js.getJSONObject(i);
                                String strStatus = jsonObject.getString("result");
                                String strMsg = jsonObject.getString("Message");

                                if ((strStatus).equalsIgnoreCase("1")) {
                                    String strcontent = jsonObject.getString("Content");
                                    contentlist.add(strcontent);
                                    Log.d("siz", String.valueOf(contentlist.size()));

                                }
                            }
                        }
                        showFilePickPopup();

                    } catch (Exception e) {
                        showToast(getResources().getString(R.string.check_internet));
                        Log.d("Ex", e.toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

                showToast(getResources().getString(R.string.check_internet));
                Log.d("Upload error:", t.getMessage() + "\n" + t);
                showToast(t.toString());
            }
        });
    }

    private void VimeoAPi() {


        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl("https://api.vimeo.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(ParentSubmitLSRW.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Connecting...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);

        strsize = String.valueOf(imagePathList.get(0).getWsUploadedDoc().length());


        JsonObject object = new JsonObject();

        JsonObject jsonObjectclasssec = new JsonObject();
        jsonObjectclasssec.addProperty("approach", "post");
        jsonObjectclasssec.addProperty("size", String.valueOf(strsize));


        JsonObject jsonprivacy = new JsonObject();
        jsonprivacy.addProperty("view", "unlisted");

        JsonObject jsonshare = new JsonObject();
        jsonshare.addProperty("share", "false");

        JsonObject jsonembed = new JsonObject();
        jsonembed.add("buttons", jsonshare);

        object.add("upload", jsonObjectclasssec);
        object.addProperty("name", "quiz");
        object.addProperty("description", "quiz");
        object.add("privacy", jsonprivacy);
        object.add("embed", jsonembed);

        String head = "Bearer " + TeacherUtil_SharedPreference.getVideotoken(ParentSubmitLSRW.this);
        Call<JsonObject> call = service.VideoUpload(object, head);
        Log.d("jsonOBJECT", object.toString());
        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                int res = response.code();
                Log.d("RESPONSE", String.valueOf(res));
                if (response.isSuccessful()) {
                    try {

                        Log.d("try", "testtry");
                        JSONObject object1 = new JSONObject(response.body().toString());
                        Log.d("Response sucess", object1.toString());


                        JSONObject obj = object1.getJSONObject("upload");
                        JSONObject obj1 = object1.getJSONObject("embed");
                        upload_link = obj.getString("upload_link");
                        link = object1.getString("link");
                        iframe = obj1.getString("html");
                        Log.d("c", upload_link);
                        Log.d("iframe", iframe);


                        String[] separated = upload_link.split("\\?(?!\\?)");

                        String name = separated[0];  //"/"
                        Log.d("name", name);

                        String FileName = separated[1];
                        Log.d("FileName", FileName);

                        String upload = name.replace("upload", "");
                        Log.d("replace", upload);

                        try {
                            VIDEOUPLOAD(upload_link);
                        } catch (Exception e) {
                            Log.e("VIMEO Exception", e.getMessage());
                            alert("Video sending failed.Retry");

                        }


                    } catch (Exception e) {
                        Log.e("VIMEO Exception", e.getMessage());
                        alert(e.getMessage());

                    }

                } else {

                    Log.d("Response fail", "fail");
                    alert("Video sending failed.Retry");

                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                alert("Video sending failed.Retry");

            }


        });
    }

    private void VIDEOUPLOAD(String upload_link) {

        String[] separated = upload_link.split("\\?(?!\\?)");
        String name = separated[0];  //"/"
        Log.d("name2", name);
        String FileName = separated[1];
        Log.d("FileName", FileName);
        String upload = name.replace("upload", "");
        Log.d("replace234", upload);

        String[] id = FileName.split("&");

        ticket_id = id[0];
        Log.d("ticket_id", ticket_id);
        video_file_id = id[1];
        Log.d("video_file_id", video_file_id);
        signature = id[2];
        Log.d("signature", signature);
        v6 = id[3];
        Log.d("v6", v6);
        redirect_url = id[4];
        Log.d("redirect_url", redirect_url);

        String[] seperate1 = ticket_id.split("=");

        String ticket = seperate1[0];
        Log.d("sp1", ticket);

        String ticket2 = seperate1[1];
        Log.d("ticket2", ticket2);


        String[] seperate2 = video_file_id.split("=");

        String ticket1 = seperate2[0];
        Log.d("sp2", ticket1);

        String ticket3 = seperate2[1];
        Log.d("tic", ticket3);

        String[] seper = signature.split("=");

        String ticke = seper[0];
        Log.d("sp3", ticke);

        String tick = seper[1];
        Log.d("signature_number", tick);

        String[] sepera = v6.split("=");

        String str = sepera[0];
        Log.d("str", str);

        String str1 = sepera[1];
        Log.d("v6123", str1);

        String[] sucess = redirect_url.split("=");

        String urlRIDERCT = sucess[0];
        Log.d("urlRIDERCT", urlRIDERCT);

        String redirect_url123 = sucess[1];
        Log.d("redirect_url123", redirect_url123);

        OkHttpClient client1;
        client1 = new OkHttpClient.Builder()

                .connectTimeout(600, TimeUnit.SECONDS)
                .readTimeout(40, TimeUnit.MINUTES)
                .writeTimeout(40, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client1)
                .baseUrl(upload)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        final ProgressDialog mProgressDialog = new ProgressDialog(ParentSubmitLSRW.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Uploading...");
        mProgressDialog.setCancelable(false);

        if (!this.isFinishing())
            mProgressDialog.show();
        TeacherMessengerApiInterface service = retrofit.create(TeacherMessengerApiInterface.class);
        RequestBody requestFile = null;
        Log.d("imgpthlist", imagePathList.get(0).getWsUploadedDoc());
        file = new File(imagePathList.get(0).getWsUploadedDoc());
        try {
            InputStream in = new FileInputStream(file);

            byte[] buf;
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
            requestFile = RequestBody.create(MediaType.parse("application/offset+octet-stream"), buf);
        } catch (IOException e) {
            e.printStackTrace();
            alert(e.getMessage());
        }

        Call<ResponseBody> call = service.patchVimeoVideoMetaData(ticket2, ticket3, tick, str1, redirect_url123 + "www.voicesnapforschools.com", requestFile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {

                        submitlist.add(new UploadFilesModel(iframe, "VIDEO"));
                        setAdapter();
                        imagePathList.clear();
                        lnrImages.setVisibility(View.GONE);
                        lblvideo.setText("Upload Video");
                        btnadd.setEnabled(false);

                        if (submitlist.size() == 0) {
                            rcysubmissions.setVisibility(View.GONE);
                            lblAddAttachment.setVisibility(View.GONE);

                        } else {
                            rcysubmissions.setVisibility(View.VISIBLE);
                            lblAddAttachment.setVisibility(View.VISIBLE);

                        }
                    } else {
                        alert("Video sending failed.Retry");
                    }
                } catch (Exception e) {
                    alert(e.getMessage());


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                alert("Video sending failed.Retry");
            }
        });


    }

    //PDF
    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void alert(String strStudName) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ParentSubmitLSRW.this);


        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(strStudName);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        alertDialog.show();
    }

    private void setAdapter() {

        runOnUiThread(() -> {
            uploadAdapter = new LsrwDocsAdapter(ParentSubmitLSRW.this, submitlist, "1", this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            rcysubmissions.setHasFixedSize(true);
            rcysubmissions.setLayoutManager(mLayoutManager);
            rcysubmissions.setItemAnimator(new DefaultItemAnimator());
            rcysubmissions.setAdapter(uploadAdapter);
            uploadAdapter.notifyDataSetChanged();
            btnsubmit.setEnabled(submitlist.size() != 0);
        });
    }

    private void selectedsetAdapter() {
        uploadAdapter = new LsrwDocsAdapter(ParentSubmitLSRW.this, imagePathList, "2", this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rcyselected.setHasFixedSize(true);
        rcyselected.setLayoutManager(mLayoutManager);
        rcyselected.setItemAnimator(new DefaultItemAnimator());
        rcyselected.setAdapter(uploadAdapter);
        uploadAdapter.notifyDataSetChanged();
        btnadd.setEnabled(imagePathList.size() != 0);
    }

    @Override
    public void Doc_addClass(UploadFilesModel student) {

    }

    @Override
    public void Doc_removeClass(UploadFilesModel student) {
        submitlist.remove(student);
        setAdapter();

        if (submitlist.size() == 0) {
            rcysubmissions.setVisibility(View.GONE);
            lblAddAttachment.setVisibility(View.GONE);

        } else {
            rcysubmissions.setVisibility(View.VISIBLE);
            lblAddAttachment.setVisibility(View.VISIBLE);

        }
        Log.d("UploadedS3URlList", String.valueOf(submitlist.size()));
    }

    private void submitquiz() {
        String child_id = Util_SharedPreference.getChildIdFromSP(this);
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("SkillId", skillid);
        jsonObject.addProperty("StudentID", child_id);


        JsonArray jsonarray = new JsonArray();

        for (int i = 0; i < submitlist.size(); i++) {

            JsonObject jsonObjectlist = new JsonObject();
            jsonObjectlist.addProperty("content", submitlist.get(i).getWsUploadedDoc());
            jsonObjectlist.addProperty("type", submitlist.get(i).getDisplayname());
            jsonarray.add(jsonObjectlist);
        }
        jsonObject.add("Attachment", jsonarray);
        Log.d("jsonObject", jsonObject.toString());

        Call<JsonArray> call = apiService.SubmitResponseForSkill(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        try {
                            JSONArray js = new JSONArray(response.body().toString());

                            if (js.length() > 0) {
                                JSONObject jsonObject = js.getJSONObject(0);
                                int strStatus = jsonObject.getInt("Status");
                                String strMsg = jsonObject.getString("Message");

                                if (strStatus == 1) {
                                    showAlert(strMsg, strStatus);
                                } else {
                                    showAlert(strMsg, strStatus);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Toast.makeText(ParentSubmitLSRW.this, "Server Response Failed", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(ParentSubmitLSRW.this, "Server Connection Failed", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String msg, final int strStatus) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert");

        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (strStatus == 0) {
                    dialog.cancel();
                    finish();
                } else {
                    Intent homescreen = new Intent(ParentSubmitLSRW.this, LSRWListActivity.class);
                    homescreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homescreen);
                    finish();
                }

            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void onUploadComplete(boolean success, String isIframe, String isLink) {

        runOnUiThread(() -> {
            Log.d("Vimeo_Video_upload", String.valueOf(success));
            Log.d("VimeoIframe", isIframe);
            Log.d("link", isLink);

            if (success) {
                iframe = isIframe;
                link = isLink;
                submitlist.add(new UploadFilesModel(iframe, "VIDEO"));
                setAdapter();
                imagePathList.clear();
                lnrImages.setVisibility(View.GONE);
                lblvideo.setText("Upload Video");
                btnadd.setEnabled(false);

                if (submitlist.size() == 0) {
                    rcysubmissions.setVisibility(View.GONE);
                    lblAddAttachment.setVisibility(View.GONE);

                } else {
                    rcysubmissions.setVisibility(View.VISIBLE);
                    lblAddAttachment.setVisibility(View.VISIBLE);
                }
            } else {
                alert("Video sending failed.");
            }
        });
    }
}