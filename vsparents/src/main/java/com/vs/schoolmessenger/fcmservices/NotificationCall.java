package com.vs.schoolmessenger.fcmservices;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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


public class NotificationCall extends AppCompatActivity implements View.OnTouchListener {

    private TextView lblVoiceDuration, lblTotalDuration;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private Handler durationUpdateHandler;
    //    String voiceUrl = "http://vs5.voicesnapforschools.com/nodejs/voice/VS_1718181818812.wav";
    ImageView rlyDecline, rlyAccept;
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isScrollingUp = false;
    LinearLayout lneButtonHeight;
    ImageView imgDeclineNotificationCall;
    private AlertDialog exitDialog;
    Boolean isAcceptCall = true;
    Boolean isDeclineCall = true;
    String voiceUrl = "";
    String isReceiverId = "";
    String isUserResponse = "NO";
    int isDuration = 0;

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
        rlyDecline = findViewById(R.id.imgDecline);
        rlyAccept = findViewById(R.id.imgAccept);
        lneButtonHeight = findViewById(R.id.lneButtonHeight);
        imgDeclineNotificationCall = findViewById(R.id.imgDeclineNotificationCall);

        rlyAccept.setOnTouchListener(this);
        rlyDecline.setOnTouchListener(this);
        handleIntent(getIntent());

        Glide.with(this)
                .asGif()
                .load(R.drawable.call_accept_notification)
                .into(rlyAccept);


        Glide.with(this)
                .asGif()
                .load(R.drawable.call_decline)
                .into(rlyDecline);

        imgDeclineNotificationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                isUserResponse = "NO";
                isDuration = Integer.parseInt(lblVoiceDuration.getText().toString());
                updateNotificationCallLog();
                finish();
            }
        });

        long durationMillis = 0;
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
            int notificationId = intent.getIntExtra("isNotificationId", -1);
            voiceUrl = intent.getStringExtra("isVoiceUrl");
            isReceiverId = intent.getStringExtra("isReceiverId");
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        final int Y = (int) event.getRawY(); // Get the raw Y coordinate of the finger

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                defaultViewHeight = rlyAccept.getHeight();
                previousFingerPosition = Y;
                baseLayoutPosition = (int) view.getY();
                break;

            case MotionEvent.ACTION_UP:
                // Reset position if swiped up far enough
                if (isScrollingUp) {
                    resetPosition(view);
                    isScrollingUp = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int currentYPosition = (int) view.getY(); // Get the current Y position of the view

                if (previousFingerPosition > Y) { // Detect swipe up
                    if (!isScrollingUp) {
                        isScrollingUp = true;
                    }

                    // Adjust height with a smooth animator if it's swiping up
                    if (view.getHeight() < defaultViewHeight) {
                        adjustViewHeight(view, view.getHeight() - (Y - previousFingerPosition));
                    } else {
                        // Trigger accept/decline action if swiped far enough
                        if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                            if (view.getId() == R.id.imgAccept) {
                                acceptAction(currentYPosition);
                            } else if (view.getId() == R.id.imgDecline) {
                                declineAction(currentYPosition);
                            }
                            return true; // Gesture handled
                        }
                    }

                    // Move the view's Y position as the finger moves
                    view.setY(view.getY() + (Y - previousFingerPosition));
                }
                previousFingerPosition = Y; // Update previous position for the next move
                break;
        }
        return true;
    }

    private void resetPosition(View view) {
        view.setY(0);
        view.getLayoutParams().height = defaultViewHeight;
        view.requestLayout();
    }

    private void adjustViewHeight(View view, int targetHeight) {
        ValueAnimator heightAnimator = ValueAnimator.ofInt(view.getHeight(), targetHeight);
        heightAnimator.setDuration(50); // Smooth transition
        heightAnimator.addUpdateListener(animation -> {
            view.getLayoutParams().height = (int) animation.getAnimatedValue();
            view.requestLayout();
        });
        heightAnimator.start();
    }

    private void acceptAction(int currentYPosition) {
        closeUpAndDismissDialog(currentYPosition);
    }

    private void declineAction(int currentYPosition) {
        Toast.makeText(this, "Declined!", Toast.LENGTH_SHORT).show();
        closeDownAndDismissDialog(currentYPosition);
    }

    public void closeUpAndDismissDialog(int currentPosition) {
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(rlyAccept, "y", currentPosition, -rlyAccept.getHeight());
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                Animator.AnimatorListener.super.onAnimationStart(animation, isReverse);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                Animator.AnimatorListener.super.onAnimationEnd(animation, isReverse);
            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (Util_Common.mediaPlayer.isPlaying()) {
                    Util_Common.mediaPlayer.stop();
                }
                if (!Util_Common.mediaPlayer.isPlaying()) {
                    if (isAcceptCall) {
                        isAcceptCall = false;
                        isUserResponse = "OC";
                        updateNotificationCallLog();
                    }
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) { }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) { }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;

        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(rlyDecline, "y", currentPosition, screenHeight + rlyDecline.getHeight());
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.setDuration(300);
        positionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(@NonNull Animator animation, boolean isReverse) {
                Animator.AnimatorListener.super.onAnimationStart(animation, isReverse);
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                Animator.AnimatorListener.super.onAnimationEnd(animation, isReverse);
            }

            @Override
            public void onAnimationStart(@NonNull Animator animator) { }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isDeclineCall) {
                    isDeclineCall = false;
                    isUserResponse = "NO";
                    updateNotificationCallLog();
                    finish();
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) { }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) { }
        });
        positionAnimator.start();
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
        jsonObject.addProperty("ei1", "abc");
        jsonObject.addProperty("ei2", "abc");
        jsonObject.addProperty("ei3", "abc");
        jsonObject.addProperty("ei4", "abc");
        jsonObject.addProperty("ei5", "abc");
        jsonObject.addProperty("start_time", "abc");
        jsonObject.addProperty("end_time", "abc");
        jsonObject.addProperty("retry_count", 1);
        jsonObject.addProperty("phone", "abc");
        jsonObject.addProperty("receiver_id", 1);
        jsonObject.addProperty("circular_id", 1);
        jsonObject.addProperty("diallist_id", 1);
        jsonObject.addProperty("call_status", isUserResponse);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonObject> call = apiService.isUpdateCallLog(jsonObject);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
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
}