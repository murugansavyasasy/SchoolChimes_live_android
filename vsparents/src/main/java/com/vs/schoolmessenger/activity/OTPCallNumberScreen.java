package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getMobileNumberFromSP;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.OTP.SmsBroadcastReceiver;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.ForgetPaswordDialinNumbers;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.GenericTextWatcher;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by voicesnap on 4/26/2018.
 */

public class OTPCallNumberScreen extends AppCompatActivity implements SmsBroadcastReceiver.OTPReceiveListener {
    public static Handler handler = new Handler();
    Button btnSubmitOtp;
    EditText txtOtp;
    TextView btnResendOTP, lblNote;
    Boolean condition = false;
    String otp_Timer;
    Runnable yourRunnable;

    String note_message = "";
    String DialNumbers = "";
    String Type = "";
    String forgot = "";

    RecyclerView recycleNumbers;
    TextView btnBackToLogin;

    SmsBroadcastReceiver mSmsBroadcastReceiver;

    EditText otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four,
            otp_textbox_five, otp_textbox_six;

    String OTP = "";
    TextView lblNoteMessage;


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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.otp_call_number_screen);

        startSMSListener();

        otp_textbox_one = findViewById(R.id.otp_edit_box1);
        otp_textbox_two = findViewById(R.id.otp_edit_box2);
        otp_textbox_three = findViewById(R.id.otp_edit_box3);
        otp_textbox_four = findViewById(R.id.otp_edit_box4);
        otp_textbox_five = findViewById(R.id.otp_edit_box5);
        otp_textbox_six = findViewById(R.id.otp_edit_box6);
        lblNoteMessage = findViewById(R.id.lblNoteMessage);

        String otp_note = TeacherUtil_SharedPreference.getOTPNote(OTPCallNumberScreen.this);
        lblNoteMessage.setText(otp_note);

        EditText[] edit = {otp_textbox_one, otp_textbox_two, otp_textbox_three, otp_textbox_four, otp_textbox_five, otp_textbox_six};
        otp_textbox_one.addTextChangedListener(new GenericTextWatcher(otp_textbox_one, edit));
        otp_textbox_two.addTextChangedListener(new GenericTextWatcher(otp_textbox_two, edit));
        otp_textbox_three.addTextChangedListener(new GenericTextWatcher(otp_textbox_three, edit));
        otp_textbox_four.addTextChangedListener(new GenericTextWatcher(otp_textbox_four, edit));
        otp_textbox_five.addTextChangedListener(new GenericTextWatcher(otp_textbox_five, edit));
        otp_textbox_six.addTextChangedListener(new GenericTextWatcher(otp_textbox_six, edit));


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            note_message = extras.getString("note_message", "");
            Type = extras.getString("Type", "");
        }
        TeacherUtil_SharedPreference.putNoteMessage(OTPCallNumberScreen.this, note_message);

        DialNumbers = TeacherUtil_SharedPreference.getDialNumbers(OTPCallNumberScreen.this);
        forgot = Util_SharedPreference.getForget(OTPCallNumberScreen.this);
        recycleNumbers = (RecyclerView) findViewById(R.id.recycleNumbers);

        if (forgot.equals("forget")) {
            TeacherUtil_SharedPreference.putForgetPasswordOTP(OTPCallNumberScreen.this, "1");
        } else {
            TeacherUtil_SharedPreference.putForgetPasswordOTP(OTPCallNumberScreen.this, "0");
        }

        if (!DialNumbers.equals("")) {
            final Dialog dialog = new Dialog(OTPCallNumberScreen.this);
            String[] separated = DialNumbers.split(",");
            ForgetPaswordDialinNumbers mAdapter = new ForgetPaswordDialinNumbers(Arrays.asList(separated), OTPCallNumberScreen.this, dialog);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recycleNumbers.setLayoutManager(mLayoutManager);
            recycleNumbers.setItemAnimator(new DefaultItemAnimator());
            recycleNumbers.setAdapter(mAdapter);
            recycleNumbers.getRecycledViewPool().setMaxRecycledViews(0, 80);

        }

        String note = TeacherUtil_SharedPreference.getNoteMessage(OTPCallNumberScreen.this);

        TeacherUtil_SharedPreference.putOTPNum(OTPCallNumberScreen.this, "1");
        otp_Timer = TeacherUtil_SharedPreference.getOTpTimer(OTPCallNumberScreen.this);
        Log.d("OTp timer", otp_Timer);

        otpHandler(otp_Timer);

        btnBackToLogin = (TextView) findViewById(R.id.btnBackToLogin);

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stophandler();
                Intent intent = new Intent(OTPCallNumberScreen.this, TeacherSignInScreen.class);
                startActivity(intent);
            }
        });

        btnSubmitOtp = (Button) findViewById(R.id.btnSubmitOtp);
        btnResendOTP = (TextView) findViewById(R.id.btnResendOTP);
        lblNote = (TextView) findViewById(R.id.lblNote);
        lblNote.setText(note);
        btnResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobileNumberApi();
            }
        });
        txtOtp = (EditText) findViewById(R.id.txtOtp);
        btnSubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otp = otp_textbox_one.getText().toString() + otp_textbox_two.getText().toString() + otp_textbox_three.getText().toString() +
                        otp_textbox_four.getText().toString() + otp_textbox_five.getText().toString() + otp_textbox_six.getText().toString();

                TeacherUtil_SharedPreference.putOTPNum(OTPCallNumberScreen.this, "");
                if (!otp.equals("")) {
                    verifyOTP(otp);
                } else {
                    String msg = getResources().getString(R.string.enter_your_otp);
                    showAlert(msg);
                }
            }
        });
    }

    private void startSMSListener() {
        try {
            mSmsBroadcastReceiver = new SmsBroadcastReceiver();
            mSmsBroadcastReceiver.setOTPListener(this);

            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
            registerReceiver(mSmsBroadcastReceiver, intentFilter);

            SmsRetrieverClient client = SmsRetriever.getClient(this);

            Task<Void> task = client.startSmsRetriever();
            task.addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });

            task.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });

        } catch (Exception e) {
            Log.d("Exception", e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mSmsBroadcastReceiver != null) {
            unregisterReceiver(mSmsBroadcastReceiver);
        }
    }


    private void otpHandler(final String otp_timer) {
        handler = new Handler();
        yourRunnable = new Runnable() {
            @Override
            public void run() {
                btnResendOTP.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(yourRunnable, Long.parseLong(otp_timer));
    }

    private void MobileNumberApi() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(OTPCallNumberScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String mobilenumber = getMobileNumberFromSP(OTPCallNumberScreen.this);
        TeacherUtil_SharedPreference.putMobileNum(OTPCallNumberScreen.this, mobilenumber);

        String CountryID = TeacherUtil_SharedPreference.getCountryID(OTPCallNumberScreen.this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobilenumber);
        jsonObject.addProperty("CountryID", CountryID);
        Log.d("Req", jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePasswordByCountryID(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        btnResendOTP.setVisibility(View.GONE);
                        otpHandler(otp_Timer);

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String OTPSent = jsonObject.getString("OTPSent");
                            if (OTPSent.equals("1")) {
                                showAlert(getResources().getString(R.string.otp_sent));
                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void stophandler() {
        handler.removeCallbacks(yourRunnable);
    }

    private void starthandler() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!condition) {
                    alertShow();
                }
            }
        }, 30000);
    }

    private void verifyOTP(String otp) {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(OTPCallNumberScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String number = getMobileNumberFromSP(OTPCallNumberScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("OTP", otp);
        jsonObject.addProperty("MobileNumber", number);
        Log.d("Req", jsonObject.toString());

        Call<JsonArray> call = apiService.ValidateOTP(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");

                            if (Status.equals("1")) {

                                stophandler();
                                showToast(Message);

                                if (forgot.equals("New")) {
                                    Intent intent = new Intent(OTPCallNumberScreen.this, ChangePasswordScreen.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(OTPCallNumberScreen.this, TeacherChangePassword.class);
                                    intent.putExtra("OTP", txtOtp.getText().toString());
                                    startActivity(intent);
                                    finish();
                                }

                            } else {
                                showAlert(Message);

                            }
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlert(String strMsg) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(OTPCallNumberScreen.this);

        alertDialog.setTitle(R.string.alert);

        //Setting Dialog Message
        alertDialog.setMessage(strMsg);


        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        android.app.AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);

        dialog.show();
        Button positiveButton = dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));


    }

    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void alertShow() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(OTPCallNumberScreen.this);
        alertDialog.setMessage(getResources().getString(R.string.otp_time_expired));
        alertDialog.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                againOtpApi();


            }
        });
        alertDialog.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    private void againOtpApi() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(OTPCallNumberScreen.this);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String CountryID = TeacherUtil_SharedPreference.getCountryID(OTPCallNumberScreen.this);
        String num = TeacherUtil_SharedPreference.getMobileNumberFromSP(OTPCallNumberScreen.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", num);
        jsonObject.addProperty("CountryID", CountryID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePasswordByCountryID(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);

                            String numberExists = jsonObject.getString("isNumberExists");
                            String passwordUpdated = jsonObject.getString("isPasswordUpdated");
                            String OTPSent = jsonObject.getString("OTPSent");
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");

                            if (OTPSent.equals("1")) {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.otp_sent), Toast.LENGTH_SHORT).show();
                            }


                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    public void onOTPReceived(String otp) {
        Log.d("OTP Received", otp);

        OTP = otp;
        otp_textbox_one.setText(String.valueOf(otp.charAt(0)));
        otp_textbox_two.setText(String.valueOf(otp.charAt(1)));
        otp_textbox_three.setText(String.valueOf(otp.charAt(2)));
        otp_textbox_four.setText(String.valueOf(otp.charAt(3)));
        otp_textbox_five.setText(String.valueOf(otp.charAt(4)));
        otp_textbox_six.setText(String.valueOf(otp.charAt(5)));

        if (mSmsBroadcastReceiver != null) {
            unregisterReceiver(mSmsBroadcastReceiver);
            mSmsBroadcastReceiver = null;
        }
        verifyOTP(OTP);
    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }
}
