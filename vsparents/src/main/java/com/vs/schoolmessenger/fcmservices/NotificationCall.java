package com.vs.schoolmessenger.fcmservices;

import static com.vs.schoolmessenger.util.TimeConverter.convertToSeconds;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AudioUtils;
import com.vs.schoolmessenger.util.ScreenState;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;


public class NotificationCall extends AppCompatActivity {

    private TextView lblVoiceDuration, lblTotalDuration;
    private Handler durationUpdateHandler;
    RelativeLayout lneButtonHeight;
    ImageView imgDeclineNotificationCall;
    private AlertDialog exitDialog;
    String voiceUrl = "";
    String isReceiverId = "";
    String isUserResponse = "NO";
    String retrycount, circularId, ei1, ei2, ei3, ei4, ei5, role, menuId;
    int notificationId;
    ImageView imgDeclineCall, imgAcceptCall;
    TextView lblCutCall;
    String isStartTime, isEndTime;
    String isListeningDuration = "00:00";

    private MediaPlayer mediaPlayer;
    private int currentTrack = 0;
    private int preparedCount = 0;
    private long totalDurationMs = 0;
    private final List<Integer> trackDurations = new ArrayList<>();

    // 🔊 Your audio URLs
    private String[] audioUrls;
    List<String> audioList;
    String welcome_file = "";
    String school_name = "";
    String member_name = "";
    String call_title = "";

    Boolean isActivityClosing = false;
    Boolean isCallConnected = false;

    private Handler handler = new Handler();
    private Runnable updateRunnable;
    private int totalElapsed = 0;

    ImageButton acceptButton, declineButton, messageButton;
    View ring1, ring2, ring3;
    RelativeLayout actionContainer;
    FrameLayout ringContainer;
    TextView slideText, lblSchoolName, lblMemberName, lblVoiceTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_call);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        ScreenState.getInstance().setIncomingCallScreen(true);

        lblVoiceDuration = findViewById(R.id.lblVoiceDuration);
        lblTotalDuration = findViewById(R.id.lblTotalDuration);

        acceptButton = findViewById(R.id.acceptButton);
        declineButton = findViewById(R.id.declineButton);
        messageButton = findViewById(R.id.messageButton);
        ring1 = findViewById(R.id.ring1);
        ring2 = findViewById(R.id.ring2);
        ring3 = findViewById(R.id.ring3);
        actionContainer = findViewById(R.id.actionContainer);
        ringContainer = findViewById(R.id.ringContainer);
        slideText = findViewById(R.id.slideText);
        lblSchoolName = findViewById(R.id.lblSchoolName);
        lblMemberName = findViewById(R.id.lblMemberName);
        lblVoiceTitle = findViewById(R.id.lblVoiceTitle);

        startCallAnimation();


        imgDeclineNotificationCall = findViewById(R.id.imgDeclineNotificationCall);
        lblCutCall = findViewById(R.id.lblCutCall);

        handleIntent(getIntent());

        acceptButton.setOnTouchListener(new View.OnTouchListener() {
            float dX = 0f;
            float originalX = 0f;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (isActivityClosing)
                    return false;

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        originalX = view.getX();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float newX = event.getRawX() + dX;
                        if (newX >= declineButton.getX() && newX <= messageButton.getX()) {
                            view.setX(newX);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        float movedDistance = view.getX() - originalX;
                        if (movedDistance > 150) {
                            showConnectedState();
                        } else if (movedDistance < -150) {
                            //end call
                            isUserResponse = "NO";
                            if (Util_Common.mediaPlayer.isPlaying()) {
                                Util_Common.mediaPlayer.stop();
                            }
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                            isEndTime = sdf.format(new Date());
                            isStartTime = isEndTime;
                            updateNotificationCallLog(isStartTime, isEndTime);

                        } else {
                            view.animate().x(originalX).setDuration(200).start();
                        }
                        break;
                }

                return true;
            }
        });

        // Step 1: Calculate total duration before playing
        calculateTotalDuration(() -> {
            String formatted = formatDuration(totalDurationMs);
            Log.d("Total_Duration:", formatted);
            lblTotalDuration.setText(formatted);
            // Step 2: Start playback after calculating total
        });

        imgDeclineNotificationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                isEndTime = sdf.format(new Date());
                isListeningDuration = lblVoiceDuration.getText().toString();
                updateNotificationCallLog(isStartTime, isEndTime);
            }
        });

        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserResponse = "NO";
                if (Util_Common.mediaPlayer.isPlaying()) {
                    Util_Common.mediaPlayer.stop();
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                isEndTime = sdf.format(new Date());
                isStartTime = isEndTime;
                updateNotificationCallLog(isStartTime, isEndTime);
            }
        });
    }


    private void startCallAnimation() {
        Animation wave1 = AnimationUtils.loadAnimation(this, R.anim.call_wave1);
        Animation wave2 = AnimationUtils.loadAnimation(this, R.anim.call_wave2);
        Animation wave3 = AnimationUtils.loadAnimation(this, R.anim.call_wave3);

        ring1.startAnimation(wave1);
        ring2.startAnimation(wave2);
        ring3.startAnimation(wave3);
    }

    private void stopCallAnimation() {
        ring1.clearAnimation();
        ring2.clearAnimation();
        ring3.clearAnimation();

        ring1.setVisibility(View.GONE);
        ring2.setVisibility(View.GONE);
        ring3.setVisibility(View.GONE);
    }

    private void showConnectedState() {
        if (isCallConnected || isActivityClosing)
            return;

        isCallConnected = true;

        stopCallAnimation();

        slideText.setVisibility(View.GONE);
        declineButton.setVisibility(View.GONE);
        messageButton.setVisibility(View.GONE);


        acceptButton.animate()
                .x(actionContainer.getWidth() / 2f - acceptButton.getWidth() / 2f)
                .setDuration(300)
                .withEndAction(() -> {
                    ringContainer.setVisibility(View.GONE);
                    acceptButton.setVisibility(View.GONE);

                    imgDeclineNotificationCall.setVisibility(View.VISIBLE);

                    if (Util_Common.mediaPlayer.isPlaying()) {
                        Util_Common.mediaPlayer.stop();
                    }
                    isUserResponse = "OC";
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    isStartTime = sdf.format(new Date());
                    isEndTime = "00:00";
                    playAudio(currentTrack);

                })
                .start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    // 🔹 Calculate total duration of all files
    private void calculateTotalDuration(Runnable onComplete) {
        for (String url : audioUrls) {
            MediaPlayer tempPlayer = new MediaPlayer();
            try {
                tempPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build());
                tempPlayer.setDataSource(url);

                tempPlayer.setOnPreparedListener(mp -> {
                    int duration = mp.getDuration();
                    totalDurationMs += duration;
                    trackDurations.add(duration);
                    preparedCount++;
                    mp.release();

                    if (preparedCount == audioUrls.length) {
                        onComplete.run();
                    }
                });

                tempPlayer.setOnErrorListener((mp, what, extra) -> {
                    preparedCount++;
                    mp.release();
                    if (preparedCount == audioUrls.length) {
                        onComplete.run();
                    }
                    return true;
                });

                tempPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 🔹 Play each track one by one
    private void playAudio(int index) {
        imgDeclineNotificationCall.setVisibility(View.VISIBLE);
        lblCutCall.setVisibility(View.VISIBLE);
        if (index >= audioUrls.length) {
            Log.d("AUDIO_PLAYER", "All tracks completed");
            stopUpdatingProgress();

            return;
        }

        releasePlayer();
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build());
            mediaPlayer.setDataSource(audioUrls[index]);
            mediaPlayer.prepareAsync();

            mediaPlayer.setOnPreparedListener(mp -> {
                Log.d("AUDIO_PLAYER", "Playing: " + (index + 1) +
                        " | Duration: " + formatDuration(mp.getDuration()));

                mp.start();
                startUpdatingProgress(); // 🕒 start updating duration

            });

            mediaPlayer.setOnCompletionListener(mp -> {
                totalElapsed += mp.getDuration();

                currentTrack++;
                if (currentTrack < audioUrls.length) {
                    playAudio(currentTrack);
                } else {
                    stopUpdatingProgress();
                    Log.d("AUDIO_PLAYER", "Playback finished");
                    releasePlayer();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String currentTime = sdf.format(new Date());
                    isEndTime = currentTime;
                    isListeningDuration = lblVoiceDuration.getText().toString();
                    updateNotificationCallLog(isStartTime, isEndTime);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startUpdatingProgress() {
        stopUpdatingProgress(); // avoid duplicates

        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int totalDuration = mediaPlayer.getDuration();

                    int totalProgress = totalElapsed + currentPosition; // ✅ accumulated time

                    Log.d("AUDIO_PROGRESS", "Current: " + formatDuration(totalProgress)
                            + " / Total: " + formatDuration(totalProgress));

                    lblVoiceDuration.setText(formatDuration(totalProgress));

                }
                handler.postDelayed(this, 1000); // update every second
            }
        };
        handler.postDelayed(updateRunnable, 1000);
    }

    private void stopUpdatingProgress() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }


    private String formatDuration(long durationMs) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs) % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private void releasePlayer() {
        stopUpdatingProgress();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void handleIntent(Intent intent) {
        if (intent != null) {

            notificationId = intent.getIntExtra("isNotificationId", -1);
            voiceUrl = intent.getStringExtra("isVoiceUrl");
            isReceiverId = intent.getStringExtra("isReceiverId");
            retrycount = intent.getStringExtra("retrycount");
            circularId = intent.getStringExtra("circularId");
            ei1 = intent.getStringExtra("ei1");
            ei2 = intent.getStringExtra("ei2");
            ei3 = intent.getStringExtra("ei3");
            ei4 = intent.getStringExtra("ei4");
            ei5 = intent.getStringExtra("ei5");
            role = intent.getStringExtra("role");
            menuId = intent.getStringExtra("menuId");

            welcome_file = intent.getStringExtra("welcome");
            school_name = intent.getStringExtra("school_name");
            member_name = intent.getStringExtra("member_name");
            call_title = intent.getStringExtra("call_title");

            lblSchoolName.setText(school_name);
            lblMemberName.setText("Calling - " + member_name + " from");
            lblVoiceTitle.setText(call_title);


            audioList = new ArrayList<>();
            if (!welcome_file.equals("")) {
                audioList.add(welcome_file);
            }
            audioList.add(voiceUrl);
            audioUrls = audioList.toArray(new String[0]);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
        ScreenState.getInstance().setIncomingCallScreen(false);
        if (exitDialog != null && exitDialog.isShowing()) {
            exitDialog.dismiss(); // Avoid window leak
        }
    }

    @Override
    public void onBackPressed() {
        if (!isFinishing() && !isDestroyed()) {
            isExit();
        } else {
            super.onBackPressed();
        }
    }


    private void isExit() {

        if (exitDialog == null || !exitDialog.isShowing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit")
                    .setMessage("Are you sure you want to exit?")
                    .setPositiveButton(R.string.rb_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.rb_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            exitDialog = builder.create();
            exitDialog.show();
        }
    }


    private void updateNotificationCallLog(String isStartTime, String isEndTime) {

        int totalSeconds = convertToSeconds(isListeningDuration);
        isListeningDuration = "";
        isListeningDuration = String.valueOf(totalSeconds);

        String MobileNumber = TeacherUtil_SharedPreference.getMobileNumber(NotificationCall.this);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(NotificationCall.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        // Create the JSON object for the request
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", voiceUrl);
        jsonObject.addProperty("duration", isListeningDuration);
        jsonObject.addProperty("ei1", ei1);
        jsonObject.addProperty("ei2", ei2);
        jsonObject.addProperty("ei3", ei3);
        jsonObject.addProperty("ei4", ei4);
        jsonObject.addProperty("ei5", ei5);
        jsonObject.addProperty("start_time", isStartTime);
        jsonObject.addProperty("end_time", isEndTime);
        jsonObject.addProperty("retry_count", retrycount);
        jsonObject.addProperty("phone", MobileNumber);
        jsonObject.addProperty("receiver_id", isReceiverId);
        jsonObject.addProperty("circular_id", circularId);
        jsonObject.addProperty("diallist_id", ei5);
        jsonObject.addProperty("call_status", isUserResponse);

        Log.d("jsonArray", String.valueOf(jsonObject));

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.isUpdateCallLog(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull retrofit2.Response<JsonArray> response) {
                try {
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        ScreenState.getInstance().setIncomingCallScreen(false);
                        Toast.makeText(NotificationCall.this, "Call ended.", Toast.LENGTH_SHORT).show();
                        exitApp();

                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                exitApp();
            }
        });
    }

    public void exitApp() {
        // Clear app from recent tasks
        finishAndRemoveTask();
        System.exit(0);
    }


    private void isVoicePlaying() {
        try {
            mediaPlayer.setDataSource(voiceUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        durationUpdateHandler = new Handler();
        imgDeclineNotificationCall.setVisibility(View.VISIBLE);
        lblCutCall.setVisibility(View.VISIBLE);
        lneButtonHeight.setVisibility(View.GONE);
        if (Util_Common.mediaPlayer.isPlaying()) {
            Util_Common.mediaPlayer.stop();
            mediaPlayer.start();
        } else {
            mediaPlayer.start();
        }

        durationUpdateHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer.isPlaying()) {
                    int currentDuration = mediaPlayer.getCurrentPosition();
                    int minutes = currentDuration / 1000 / 60;
                    int seconds = currentDuration / 1000 % 60;
                    lblVoiceDuration.setText(String.format("%02d:%02d", minutes, seconds));
                    durationUpdateHandler.postDelayed(this, 1000);
                }
            }
        }, 0);


        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                final Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        String currentTime = sdf.format(new Date());
                        isEndTime = currentTime;
                        isListeningDuration = lblVoiceDuration.getText().toString();
                        updateNotificationCallLog(isStartTime, isEndTime);
                    }
                }, 1000);
            }
        });
    }
}