package com.vs.schoolmessenger.activity;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.model.MessageModel;
import com.vs.schoolmessenger.util.ChangeMsgReadStatus;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import java.io.File;

import static com.vs.schoolmessenger.util.Util_Common.MENU_EMERGENCY;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

public class VoiceCircularPopup extends AppCompatActivity {

    TextView tvTitle, tvTime, tvStatus,tvdescription;
    TextView tvDuarion, tvTotDuration;
    public ImageButton imgBtnPlayPause;
    public SeekBar seekBar;

    ProgressBar pbLoading;

    private MediaPlayer mediaPlayer;
    int mediaFileLengthInMilliseconds = 0;
    Handler handler = new Handler();

    MessageModel voiceModel;


    private final String VOICE_FOLDER = "//SchoolChimesVoice";

    String voicetype="";
    String isNewVersion;
    Boolean is_Archive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_circular_popup);

        voiceModel = (MessageModel) getIntent().getSerializableExtra("VOICE_ITEM");
        voicetype = getIntent().getExtras().getString("VOICE_TYPE", "");

        ImageView ivBack = (ImageView) findViewById(R.id.voicePopup_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TextView tvBarTitle = (TextView) findViewById(R.id.voicePopup_ToolBarTvTitle);
        tvBarTitle.setText(voiceModel.getMsgDate());

        tvTitle = (TextView) findViewById(R.id.voicePopup_tvTitle);
        tvTime = (TextView) findViewById(R.id.voicePopup_tvTime);
        tvStatus = (TextView) findViewById(R.id.voicePopup_tvNew);
        tvdescription = (TextView) findViewById(R.id.voicePopup_tvdescrip);

        tvTitle.setText(voiceModel.getMsgTitle());
        tvdescription.setText(voiceModel.getMsgdescription());
        tvTime.setText(voiceModel.getMsgTime());
        if (voiceModel.getMsgReadStatus().equals("0"))
            tvStatus.setVisibility(View.VISIBLE);
        else tvStatus.setVisibility(View.GONE);

        imgBtnPlayPause = (ImageButton) findViewById(R.id.myAudioPlayer_imgBtnPlayPause);
        imgBtnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // TeacherUtil_SharedPreference.putOnBackPressed(VoiceCircularPopup.this,"1");
                TeacherUtil_SharedPreference.putOnBackPressedEmeVoice(VoiceCircularPopup.this,"1");
                recVoicePlayPause();
            }
        });


        isNewVersion = TeacherUtil_SharedPreference.getNewVersion(VoiceCircularPopup.this);
        //is_Archive = getIntent().getExtras().getBoolean("is_Archive", false);
        is_Archive = voiceModel.getIs_Archive();


        if(voicetype.equals("Voice_History")){
            tvTitle.setText(voiceModel.getMsgDate());
            tvTime.setText("");
        }

        tvDuarion = (TextView) findViewById(R.id.myAudioPlayer_tvDuration);
        tvTotDuration = (TextView) findViewById(R.id.myAudioPlayer_tvTotDuration);
        seekBar = (SeekBar) findViewById(R.id.myAudioPlayer_seekBar);

        pbLoading = (ProgressBar) findViewById(R.id.voicePopup_pbBuffering);
        pbLoading.setVisibility(View.GONE);

        setupAudioPlayer();


        if(voiceModel.getMsgdescription().equals("")){
            tvdescription.setVisibility(View.GONE);
        }
        else{
            tvdescription.setVisibility(View.VISIBLE);
        }

        if (voiceModel.getMsgReadStatus().equals("0")) {
            voiceModel.setMsgReadStatus("1");
            tvStatus.setVisibility(View.GONE);

            ChangeMsgReadStatus.changeReadStatus(VoiceCircularPopup.this, voiceModel.getMsgID(), MSG_TYPE_VOICE, voiceModel.getMsgDate(),isNewVersion,is_Archive, new OnRefreshListener()
            {
                @Override
                public void onRefreshItem() {
                    voiceModel.setMsgReadStatus("1");
                    if (voiceModel.getMsgReadStatus().equals("0"))
                        tvStatus.setVisibility(View.VISIBLE);
                    else tvStatus.setVisibility(View.GONE);
                }
            });
        }

        fetchSong();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onBackPressed() {
       // TeacherUtil_SharedPreference.putOnBackPressed(VoiceCircularPopup.this,"1");
        TeacherUtil_SharedPreference.putOnBackPressedEmeVoice(VoiceCircularPopup.this,"1");

        if (mediaPlayer.isPlaying())
            mediaPlayer.stop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void setupAudioPlayer() {
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                imgBtnPlayPause.setImageResource(R.drawable.ic_play);
                imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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
            imgBtnPlayPause.setImageResource(R.drawable.ic_pause);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.clr_red));
        } else {
            mediaPlayer.pause();
            imgBtnPlayPause.setImageResource(R.drawable.ic_play);
            imgBtnPlayPause.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }

        primarySeekBarProgressUpdater(mediaFileLengthInMilliseconds);
    }

    public void fetchSong() {
        Log.d("FetchSong", "Start***************************************");
        try {

            String filepath;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
            {
                filepath=getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getPath();

            }
            else{
                filepath = Environment.getExternalStorageDirectory().getPath();
            }

            File file = new File(filepath, VOICE_FOLDER);
            File dir = new File(file.getAbsolutePath());

//            final File dir;
//            if (Build.VERSION_CODES.R > Build.VERSION.SDK_INT) {
//                dir = new File(Environment.getExternalStorageDirectory().getPath()
//                        + VOICE_FOLDER);
//            } else {
//                dir = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath()
//                        + VOICE_FOLDER);
//            }

            if (!dir.exists()) {
                dir.mkdirs();
                System.out.println("Dir: " + dir);
            }


            Log.d("path",voiceModel.getMsgTitle());
            Log.d("path",voiceModel.getMsgTitle());
            String name=voiceModel.getMsgID()+"_"+voiceModel.getMsgTitle()+".mp3";
            Log.d("name",name);

            File futureStudioIconFile = new File(dir, voiceModel.getMsgID()+"_"+voiceModel.getMsgTitle()+".mp3");

            System.out.println("FILE_PATH:" + futureStudioIconFile.getPath());

            mediaPlayer.reset();
            mediaPlayer.setDataSource(futureStudioIconFile.getPath());
            mediaPlayer.prepare();

            mediaFileLengthInMilliseconds = mediaPlayer.getDuration();
            tvTotDuration.setText(Util_Common.milliSecondsToTimer(mediaFileLengthInMilliseconds));

        } catch (Exception e) {
            Log.d("in Fetch Song", e.toString());
        }

        Log.d("FetchSong", "END***************************************");
    }




    private void primarySeekBarProgressUpdater(final int fileLength) {
        int iProgress = (int) (((float) mediaPlayer.getCurrentPosition() / fileLength) * 100);
        seekBar.setProgress(iProgress); // This math construction give a percentage of "was playing"/"song length"

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    tvDuarion.setText(Util_Common.milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    primarySeekBarProgressUpdater(fileLength);
                }
            };
            handler.postDelayed(notification, 1000);
        }
    }
}
