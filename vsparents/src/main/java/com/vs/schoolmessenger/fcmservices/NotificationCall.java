package com.vs.schoolmessenger.fcmservices;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
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
    String voiceUrl = "http://vs5.voicesnapforschools.com/nodejs/voice/VS_1718181818812.wav";
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

        Glide.with(this)
                .asGif()
                .load(R.drawable.call_accept_notification)
                .into(rlyAccept);


        Glide.with(this)
                .asGif()
                .load(R.drawable.call_decline)
                .into(rlyDecline);

        Intent intent = getIntent();
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", -1);
        voiceUrl = intent.getExtras().getString("VOICE_URL", "");

        Log.d("voiceUrl",voiceUrl);
        imgDeclineNotificationCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
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
        if (!isTotalDuration.equals("-1")) {
            lblTotalDuration.setText(" / " + isTotalDuration);
        } else {
            System.out.println("Error getting duration");
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
                // When the touch is lifted, reset the position if swiped up far enough
                if (isScrollingUp) {
                    // Snap back to original position if scrolling up has been detected
                    view.setY(0);
                    view.getLayoutParams().height = defaultViewHeight;
                    view.requestLayout();
                    isScrollingUp = false;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int currentYPosition = (int) view.getY(); // Get the current Y position of the view

                if (previousFingerPosition > Y) { // Detect swipe up (finger is moving upwards)
                    if (!isScrollingUp) {
                        isScrollingUp = true;
                    }

                    if (view.getHeight() < defaultViewHeight) {
                        // Adjust height as the view is being scrolled up
                        view.getLayoutParams().height = view.getHeight() - (Y - previousFingerPosition);
                        view.requestLayout();
                    } else {
                        // Trigger action if swiped far enough
                        if ((baseLayoutPosition - currentYPosition) > defaultViewHeight / 2) {
                            if (view.getId() == R.id.imgAccept) {
                                acceptAction(currentYPosition); // Handle accept action
                            } else if (view.getId() == R.id.imgDecline) {
                                declineAction(currentYPosition); // Handle decline action
                            }
                            return true; // Gesture handled
                        }
                    }

                    // Move the view's Y position upward as the finger moves
                    view.setY(view.getY() + (Y - previousFingerPosition));
                }
                previousFingerPosition = Y; // Update previous position for the next move
                break;
        }
        return true;
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
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                if (Util_Common.mediaPlayer.isPlaying()) {
                    Util_Common.mediaPlayer.stop();
                }
                if (!Util_Common.mediaPlayer.isPlaying()) {
                    if (isAcceptCall) {
                        isAcceptCall=false;
                        isAccept();
                    }
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        positionAnimator.start();
    }

    public void closeDownAndDismissDialog(int currentPosition) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = size.y;
        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(rlyDecline, "y", currentPosition, screenHeight + rlyDecline.getHeight());
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
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (isDeclineCall) {
                    isDeclineCall=false;
                    isDecline(currentPosition);
                    finish();
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
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


    private void isDecline(int notificationId) {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MemberId", 10071249);
        jsonObject.addProperty("SchoolId", 6540);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.GetMessageCount(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());
                        isDeclineCall = false;
                        if (Util_Common.mediaPlayer != null && Util_Common.mediaPlayer.isPlaying()) {
                            Util_Common.mediaPlayer.stop();
                        }
                        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        manager.cancel(notificationId);

                        Handler handler = new Handler();

                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (Util_Common.mediaPlayer.isPlaying()) {
                                    Util_Common.mediaPlayer.stop();
                                }
                                Toast.makeText(NotificationCall.this, "This call was decline", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        };
                        handler.postDelayed(runnable, 1000);
                    }
                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
            }
        });
    }


    private void isAccept() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MemberId", 10071249);
        jsonObject.addProperty("SchoolId", 6540);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.GetMessageCount(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
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
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
            }
        });
    }
}