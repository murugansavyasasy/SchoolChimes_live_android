package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vs.schoolmessenger.R;

import java.io.File;
import java.io.IOException;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FILE_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.VOICE_FOLDER_NAME;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.milliSecondsToTimer;


public class TeacherStaffVoice extends AppCompatActivity implements View.OnClickListener {

    Button btnToSections, btnToStudents;
    RelativeLayout rlVoicePreview;
    ImageView ivRecord;
    ImageButton imgBtnPlayPause;
    SeekBar seekBar;
    TextView tvPlayDuration, tvRecordDuration, tvRecordTitle, tvEmergTitle;

    private MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();
    int recTime;
    MediaRecorder recorder;
    Handler recTimerHandler = new Handler();
    boolean bIsRecording = false;
    int iMediaDuration = 0;

    private int iRequestCode;
    int iMaxRecDur;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_staff_voice);



        iRequestCode = getIntent().getExtras().getInt("REQUEST_CODE", 0);

        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(this);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);
        tvPlayDuration = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);

        rlVoicePreview = (RelativeLayout) findViewById(R.id.staffVoice_rlPlayPreview);
        ivRecord = (ImageView) findViewById(R.id.staffVoice_ivRecord);
        ivRecord.setOnClickListener(this);
        tvRecordDuration = (TextView) findViewById(R.id.staffVoice_tvRecDuration);
        tvRecordTitle = (TextView) findViewById(R.id.staffVoice_tvRecTitle);
        tvEmergTitle = (TextView) findViewById(R.id.staffVoice_tvEmergenyTitle);

        btnToSections = (Button) findViewById(R.id.staffVoice_btnToSections);
        btnToSections.setOnClickListener(this);

        btnToStudents = (Button) findViewById(R.id.staffVoice_btnToStudents);
        btnToStudents.setOnClickListener(this);

        rlVoicePreview.setVisibility(View.GONE);
        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);

        ImageView ivBack = (ImageView) findViewById(R.id.staffVoice_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iMaxRecDur = 181;
        tvEmergTitle.setText(getText(R.string.teacher_txt_general_title));

        setupAudioPlayer();
    }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.staffVoice_btnToSections:
                Intent intoSec = new Intent(TeacherStaffVoice.this, TeacherStaffStandardSection.class);
                intoSec.putExtra("REQUEST_CODE", iRequestCode);
                startActivityForResult(intoSec, iRequestCode);
                break;

            case R.id.staffVoice_btnToStudents:
                Intent intoStu = new Intent(TeacherStaffVoice.this, TeacherStaffStandardSection.class);
                intoStu.putExtra("REQUEST_CODE", iRequestCode);
                startActivityForResult(intoStu, iRequestCode);
                break;

            case R.id.myAudioPlayer_imgBtnPlayPause:
                recVoicePlayPause();
                break;

            case R.id.staffVoice_ivRecord:
                if (bIsRecording)
                    stop_RECORD();
                else start_RECORD();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == iRequestCode) {
            String message = data.getStringExtra("MESSAGE");
            if (message.equals("SENT")) {
                finish();
            }
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    // Rec..

    private void start_RECORD() {
        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_stop);
        ivRecord.setImageResource(R.drawable.teacher_ic_stop);
        rlVoicePreview.setVisibility(View.GONE);
        tvRecordTitle.setText(getText(R.string.teacher_txt_stop_record));
        btnToSections.setEnabled(false);
        btnToStudents.setEnabled(false);

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
        btnToSections.setEnabled(true);
        btnToStudents.setEnabled(true);

        ivRecord.setBackgroundResource(R.drawable.teacher_bg_record_start);
        ivRecord.setImageResource(R.drawable.teacher_ic_mic);
        rlVoicePreview.setVisibility(View.VISIBLE);


        fetchSong();
    }

    private String getRecFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
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

    private Runnable runson = new Runnable() {
        @Override
        public void run() {
            tvRecordDuration.setText(milliSecondsToTimer(recTime * 1000));
            recTime = recTime + 1;


            if (recTime != iMaxRecDur)
                recTimerHandler.postDelayed(this, 1000);
            else
                stop_RECORD();
        }
    };

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

            File futureStudioIconFile = new File(dir, VOICE_FILE_NAME);
            System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(futureStudioIconFile.getPath());
            mediaPlayer.prepare();
            iMediaDuration = (int) (mediaPlayer.getDuration() / 1000.0);

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
}
