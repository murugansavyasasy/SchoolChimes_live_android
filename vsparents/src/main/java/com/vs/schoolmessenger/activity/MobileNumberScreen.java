package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.activity.TeacherSignInScreen.hasPermissions;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.OTP.AutoReadOTPCallNumberScreen;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by voicesnap on 4/26/2018.
 */

public class MobileNumberScreen extends AppCompatActivity {
    Button btnNext;
    EditText enter_mobile;
    int PERMISSION_ALL = 1;
    String strmobilenumberlength, mobileNumber;
    int mobnumberlength;

    TextView textView2;

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
        setContentView(R.layout.enter_mobile_num);
        TeacherUtil_SharedPreference.putMobileNumberScreen(MobileNumberScreen.this, "1");

        String[] PERMISSIONS = {Manifest.permission.READ_PHONE_STATE};
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        btnNext = (Button) findViewById(R.id.btnNext);
        enter_mobile = (EditText) findViewById(R.id.enter_mobile);
        textView2 = (TextView) findViewById(R.id.textView2);

        strmobilenumberlength = TeacherUtil_SharedPreference.getMobileNumberLengthFromSP(MobileNumberScreen.this);
        mobnumberlength = Integer.parseInt(strmobilenumberlength);

        Log.d("MobilenumberLength", mobnumberlength + "  " + strmobilenumberlength);

        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(mobnumberlength);

        enter_mobile.setHint(getResources().getString(R.string.Enter) + strmobilenumberlength + getResources().getString(R.string.Digit_Mobile_Number));
        textView2.setText(strmobilenumberlength +getResources().getString(R.string.Digit_Mobile_Number));

        Log.d("textView2", textView2.getText().toString());
        enter_mobile.setFilters(fArray);
        TeacherUtil_SharedPreference.putInstall(MobileNumberScreen.this, "1");
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateLoginInputs()) {
                    ValidateUser();
                } else {
                    String msg = getResources().getString(R.string.enter_valid_mobile);
                    showToast(msg);
                }
            }
        });
    }

    private boolean validateLoginInputs() {
        mobileNumber = enter_mobile.getText().toString().trim();
        if (enter_mobile.length() != mobnumberlength) {
            showToast(getResources().getString(R.string.enter_valid_mobile));
            return false;
        } else return true;
    }

    public void ValidateUser() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(MobileNumberScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        final String mobilenumber = enter_mobile.getText().toString();
        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        JsonObject json_object = TeacherUtil_JsonRequest.getJsonArray_ValidateUser(mobilenumber, androidId, "");

        TeacherUtil_SharedPreference.putStaffLoginInfoToSP(MobileNumberScreen.this, mobileNumber, "", false);

        String number = TeacherUtil_SharedPreference.getMobileNumberFromSP(MobileNumberScreen.this);
        TeacherUtil_SharedPreference.putMobileNum(MobileNumberScreen.this, number);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.ValidateUser(json_object);
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
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");
                            if (Status.equals("1")) {
                                String numberExists = jsonObject.getString("isNumberExists");
                                String passwordUpdated = jsonObject.getString("isPasswordUpdated");

                                String redirect_to_otp;
                                if (jsonObject.has("redirect_to_otp")) {
                                    redirect_to_otp = jsonObject.getString("redirect_to_otp");
                                } else {
                                    redirect_to_otp = "";
                                }

                                String click_here = jsonObject.getString("MoreInfo");
                                TeacherUtil_SharedPreference.putNoteMessage(MobileNumberScreen.this, click_here);
                                TeacherUtil_SharedPreference.putOTPNote(MobileNumberScreen.this, Message);

                                if (numberExists.equals("1") && passwordUpdated.equals("1")) {

                                    if (redirect_to_otp.equals("1")) {
                                        Intent inChangePass = new Intent(MobileNumberScreen.this, AutoReadOTPCallNumberScreen.class);
                                        startActivity(inChangePass);
                                        finish();
                                    } else {
                                        Intent i = new Intent(MobileNumberScreen.this, PasswordScreen.class);
                                        startActivity(i);
                                        finish();
                                    }
                                } else if (numberExists.equals("1") && passwordUpdated.equals("0")) {
                                    Util_SharedPreference.putForget(MobileNumberScreen.this, "New");
                                    Intent inChangePass = new Intent(MobileNumberScreen.this, OTPCallNumberScreen.class);
                                    inChangePass.putExtra("Type", "New");
                                    inChangePass.putExtra("note_message", click_here);
                                    startActivity(inChangePass);
                                } else {
                                    showAlert(Message);
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
                Log.e("Response Failure", t.getMessage());
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showAlert(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MobileNumberScreen.this);
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(msg);
        alertDialog.setNegativeButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));


    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}