package com.vs.schoolmessenger.fcmservices;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AudioUtils;
import com.vs.schoolmessenger.util.ScreenState;
import com.vs.schoolmessenger.util.Util_Common;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;


public class NotificationCall extends AppCompatActivity {

    private TextView lblVoiceDuration, lblTotalDuration;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler durationUpdateHandler;
    //    String voiceUrl = "http://vs5.voicesnapforschools.com/nodejs/voice/VS_1718181818812.wav";
    RelativeLayout lneButtonHeight;
    ImageView imgDeclineNotificationCall;
    private AlertDialog exitDialog;
    Boolean isAcceptCall = true;
    String voiceUrl = "";
    String isReceiverId = "";
    String isUserResponse = "NO";
    int isDuration = 0;
    String retrycount, circularId, ei1, ei2, ei3, ei4, ei5, role, menuId;
    int notificationId;
    ImageView imgDeclineCall, imgAcceptCall;
    TextView lblCutCall;

    @SuppressLint("ClickableViewAccessibility")
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
        imgDeclineCall = findViewById(R.id.imgDeclineCall);
        imgAcceptCall = findViewById(R.id.imgAcceptCall);
        lneButtonHeight = findViewById(R.id.lneButtonHeight);
        imgDeclineNotificationCall = findViewById(R.id.imgDeclineNotificationCall);
        lblCutCall = findViewById(R.id.lblCutCall);

        handleIntent(getIntent());

        imgDeclineNotificationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                isUserResponse = "NO";
                //  updateNotificationCallLog();
                //   isWithOutApiCallPlaying();
                finish();
            }
        });

        imgDeclineCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserResponse = "NO";
                //  updateNotificationCallLog();
             //   isWithOutApiCallPlaying();
                finish();
            }
        });

        imgAcceptCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isUserResponse = "OC";
            //  updateNotificationCallLog();
            isWithOutApiCallPlaying();
            }
        });

        long durationMillis;
        try {
            durationMillis = AudioUtils.getWavFileDurationFromUrl(voiceUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String isTotalDuration = AudioUtils.formatDuration(durationMillis);
        Log.d("isTotalDuration", (isTotalDuration));
        if (!isTotalDuration.equals("-1")) {
            lblTotalDuration.setText(" / " + isTotalDuration);
        } else {
            System.out.println("Error getting duration");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
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

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ScreenState.getInstance().setIncomingCallScreen(false);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        if (durationUpdateHandler != null) {
            durationUpdateHandler.removeCallbacksAndMessages(null);
        }

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
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            exitDialog = builder.create();
            exitDialog.show();
        }
    }


    private void updateNotificationCallLog() {

        // Create the JSON object for the request
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", voiceUrl);
        jsonObject.addProperty("duration", isDuration);
        jsonObject.addProperty("ei1", ei1);
        jsonObject.addProperty("ei2", ei2);
        jsonObject.addProperty("ei3", ei3);
        jsonObject.addProperty("ei4", ei4);
        jsonObject.addProperty("ei5", ei5);
        jsonObject.addProperty("start_time", "abc");
        jsonObject.addProperty("end_time", "abc");
        jsonObject.addProperty("retry_count", 1);
        jsonObject.addProperty("phone", "Android");
        jsonObject.addProperty("receiver_id", isReceiverId);
        jsonObject.addProperty("circular_id", circularId);
        jsonObject.addProperty("diallist_id", 1);
        jsonObject.addProperty("call_status", isUserResponse);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isUpdateCallLog(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull retrofit2.Response<JsonObject> response) {
                try {
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        isAcceptCall = false;
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
                                        ScreenState.getInstance().setIncomingCallScreen(false);
                                        Toast.makeText(NotificationCall.this, "Call was ended.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }, 1000);
                            }
                        });
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
            }
        });
    }

    public  void isWithOutApiCallPlaying(){
        isAcceptCall = false;
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
                        ScreenState.getInstance().setIncomingCallScreen(false);
                        Toast.makeText(NotificationCall.this, "Call was ended.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }, 1000);
            }
        });
    }
}