package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.PasswordTransformationMethod;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.regex.Pattern;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TeacherChangePassword extends AppCompatActivity {

    EditText etOldPass, etNewPass, etConfirmPass;
    ImageView ivOldPassEye, ivNewPassEye, ivConfirmPassEye;
    TextView btnUpdatePassword, btnCancel;

    TextView updatePassword_tv1;


    String strOldPass;
    String strNewPass;
    String strConfirmPass;
    boolean bOldPassEyeVisible = false;
    boolean bNewPassEyeVisible = false;
    boolean bConfirmPassEyeVisible = false;

    String Mobile_number;
    EditText updatePassword_etOldPassword;

    String RedirectToSignInScreen, forget, forgetOTPMessage, click_here;
    TextView txtRedirectMessage, txtMessage, txtChangePassword;
    RelativeLayout rytRedirect;

    RelativeLayout rytExisting;
    String[] values;
    String OTP = "";
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {

                final String message = intent.getStringExtra("message");
                Log.d("OTPMessage", message);
                updatePassword_etOldPassword.setText(message);
            }
        }
    };

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
        setContentView(R.layout.teacher_activity_change_password);

        updatePassword_tv1 = (TextView) findViewById(R.id.updatePassword_tv1);
        txtRedirectMessage = (TextView) findViewById(R.id.txtRedirectMessage);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        txtChangePassword = (TextView) findViewById(R.id.txtChangePassword);
        updatePassword_etOldPassword = (EditText) findViewById(R.id.updatePassword_etOldPassword);
        rytRedirect = (RelativeLayout) findViewById(R.id.rytRedirect);
        rytExisting = (RelativeLayout) findViewById(R.id.rytExisting);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            RedirectToSignInScreen = extras.getString("RedirectToSignInScreen", "");
            OTP = extras.getString("OTP", "");
        }
        forget = Util_SharedPreference.getForget(TeacherChangePassword.this);
        forgetOTPMessage = TeacherUtil_SharedPreference.getForgetOtpMessage(TeacherChangePassword.this);


        if (forget.equals("forget")) {

            updatePassword_tv1.setVisibility(View.GONE);
            rytExisting.setVisibility(View.GONE);

            updatePassword_tv1.setText(R.string.enter_your_otp);
            txtChangePassword.setText(R.string.reset_password);

            if (!forgetOTPMessage.equals("")) {
                rytRedirect.setVisibility(View.VISIBLE);
                String currentString = forgetOTPMessage;
                values = currentString.split(Pattern.quote("."));

                SpannableString content = new SpannableString(values[0]);
                content.setSpan(new UnderlineSpan(), 0, values[0].length(), 0);
                txtRedirectMessage.setText(content);
                txtMessage.setText("(" + values[1] + ")");
            }
        }

        rytRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherUtil_SharedPreference.clearStaffSharedPreference(TeacherChangePassword.this);
                Intent sigin = new Intent(TeacherChangePassword.this, TeacherSignInScreen.class);
                startActivity(sigin);
                finish();
            }
        });

        if (forget.equals("change")) {
            updatePassword_tv1.setText(R.string.pop_password_txt_exist);

        }
        ImageView ivBack = (ImageView) findViewById(R.id.updatePassword_ToolBarIvBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setChangePasswordInputs();
    }

    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(TeacherChangePassword.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setChangePasswordInputs() {

        etOldPass = (EditText) findViewById(R.id.updatePassword_etOldPassword);
        ivOldPassEye = (ImageView) findViewById(R.id.updatePassword_ivOldPassEye);
        ivOldPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisibilityOnOff(ivOldPassEye, etOldPass, bOldPassEyeVisible);
                bOldPassEyeVisible = !bOldPassEyeVisible;
            }
        });

        etNewPass = (EditText) findViewById(R.id.updatePassword_etNewPassword);
        ivNewPassEye = (ImageView) findViewById(R.id.updatePassword_ivNewPassEye);
        ivNewPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisibilityOnOff(ivNewPassEye, etNewPass, bNewPassEyeVisible);
                bNewPassEyeVisible = !bNewPassEyeVisible;
            }
        });

        etConfirmPass = (EditText) findViewById(R.id.updatePassword_etRepeatPassword);
        ivConfirmPassEye = (ImageView) findViewById(R.id.updatePassword_ivRepeatPassEye);
        ivConfirmPassEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordVisibilityOnOff(ivConfirmPassEye, etConfirmPass, bConfirmPassEyeVisible);
                bConfirmPassEyeVisible = !bConfirmPassEyeVisible;
            }
        });

        btnUpdatePassword = (TextView) findViewById(R.id.updatePassword_tvUpdate);
        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOldPass = etOldPass.getText().toString().trim();
                strNewPass = etNewPass.getText().toString().trim();
                strConfirmPass = etConfirmPass.getText().toString().trim();

                if (forget.equals("forget")) {
                    if (strNewPass.equals("") || strConfirmPass.equals(""))
                        showToast(getResources().getString(R.string.enter_correct_password));
                    else if (!strNewPass.equals(strConfirmPass))
                        showToast(getResources().getString(R.string.password_missmatch));
                    else {
                        if (forget.equals("forget")) {
                            forgetPasswordOTP();
                        } else {
                            changePasswordAPI();
                        }

                    }
                } else {
                    if (strOldPass.equals("") || strNewPass.equals("") || strConfirmPass.equals(""))
                        showToast(getResources().getString(R.string.enter_correct_password));

                    else if (!strNewPass.equals(strConfirmPass))
                        showToast(getResources().getString(R.string.password_missmatch));
                    else {
                        if (forget.equals("forget")) {
                            forgetPasswordOTP();
                        } else {
                            changePasswordAPI();
                        }

                    }


                }
            }
        });

        btnCancel = (TextView) findViewById(R.id.updatePassword_tvCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (forget.equals("forget")) {
                    Intent inChangePass = new Intent(TeacherChangePassword.this, TeacherSignInScreen.class);
                    startActivity(inChangePass);
                } else {
                    onBackPressed();

                }
            }
        });
    }

    private void forgetPasswordOTP() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherChangePassword.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherChangePassword.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobNumber);
        jsonObject.addProperty("OTP", OTP);
        jsonObject.addProperty("NewPassword", strNewPass);

        Log.d("req", jsonObject.toString());

        Call<JsonArray> call = apiService.ResetPasswordAfterForget(jsonObject);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("ChangePass:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("ChangePass:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            String type = TeacherUtil_SharedPreference.getPassWordType(TeacherChangePassword.this);

                            if (type.equals("Signin_screen")) {
                                TeacherUtil_SharedPreference.cleaStaffSharedPreference(TeacherChangePassword.this);
                                startActivity(new Intent(TeacherChangePassword.this, TeacherSignInScreen.class));
                                finish();
                            }

                            if (type.equals("password_screen")) {
                                startActivity(new Intent(TeacherChangePassword.this, PasswordScreen.class));
                                finish();
                            }

                            showToast(strMsg);
                        } else {
                            showToast(strMsg);
                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("ChangePass:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }

    private void passwordVisibilityOnOff(ImageView ivEye, EditText etPassword, boolean bVisibility) {
        if (bVisibility) {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            etPassword.setSelection(etPassword.length());
            ivEye.setImageResource(R.drawable.teacher_ic_visibility_off_24dp);
        } else {
            etPassword.setTransformationMethod(null);
            etPassword.setSelection(etPassword.length());
            ivEye.setImageResource(R.drawable.teacher_ic_visibility_on_24dp);
        }
    }

    private void changePasswordAPI() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherChangePassword.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();


        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherChangePassword.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_ChangePassword(mobNumber, strOldPass, strNewPass);
        Call<JsonArray> call = apiService.ChangePasswordnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("ChangePass:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("ChangePass:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            String type = TeacherUtil_SharedPreference.getPassWordType(TeacherChangePassword.this);

                            if (type.equals("Signin_screen")) {
                                TeacherUtil_SharedPreference.cleaStaffSharedPreference(TeacherChangePassword.this);
                                startActivity(new Intent(TeacherChangePassword.this, TeacherSignInScreen.class));
                                finish();
                            }

                            if (type.equals("password_screen")) {
                                startActivity(new Intent(TeacherChangePassword.this, PasswordScreen.class));
                                finish();
                            }

                            showToast(strMsg);
                        } else {
                            showToast(strMsg);
                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("ChangePass:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
            }
        });
    }
}
