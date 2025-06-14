package com.vs.schoolmessenger.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;


public class NewPopUpActivity extends AppCompatActivity {
    MediaPlayer mp;
    String notificationcheck;

    @Override
    protected void attachBaseContext(Context newBase) {
        String savedLanguage = LocaleHelper.getLanguage(newBase);
        Context localeUpdatedContext = LocaleHelper.setLocale(newBase, savedLanguage);
        Context wrappedContext = ViewPumpContextWrapper.wrap(localeUpdatedContext);
        super.attachBaseContext(wrappedContext);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);

        Log.d("PopUpScreen", "PopupScreen");

        notificationcheck = TeacherUtil_SharedPreference.getNotification(NewPopUpActivity.this);
        Log.d("check_activity_status", notificationcheck);
        if (notificationcheck.equals("true")) {
            finish();
        }

        alert();
        mp = MediaPlayer.create(NewPopUpActivity.this, R.raw.school_voice);
        playSong();

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d("Onstart called", "Onstart");

        TeacherUtil_SharedPreference.putNotification(NewPopUpActivity.this, "true");


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Onstop called", "Onstop");
        TeacherUtil_SharedPreference.putNotification(NewPopUpActivity.this, "false");

    }


    private void playSong() {
        mp.start();
    }

    private void alert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NewPopUpActivity.this);
        alertDialog.setIcon(R.drawable.ic_voice_snap);
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(getResources().getString(R.string.you_have_new_message));
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent splashscreen = new Intent(NewPopUpActivity.this, TeacherSplashScreen.class);
                startActivity(splashscreen);
                finish();
                mp.stop();
            }
        });


        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.colorPrimary));


    }
}
