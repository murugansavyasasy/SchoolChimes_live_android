package com.vs.schoolmessenger.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity {

    EditText etOldPass, etNewPass, etConfirmPass;
    ImageView ivOldPassEye, ivNewPassEye, ivConfirmPassEye;
    TextView btnUpdatePassword, btnCancel;

    String strOldPass;
    String strNewPass;
    String strConfirmPass;
    boolean bOldPassEyeVisible = false;
    boolean bNewPassEyeVisible = false;
    boolean bConfirmPassEyeVisible = false;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

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
    public void onBackPressed() {
        finish();
    }

    private void showToast(String msg) {
        Toast.makeText(ChangePassword.this, msg, Toast.LENGTH_SHORT).show();
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
                if (strOldPass.equals("") || strNewPass.equals("") || strConfirmPass.equals(""))
                    showToast("Enter Valid Passwords");
                else if (!strNewPass.equals(strConfirmPass))
                    showToast("New Passwords Mismatching");
                else
                    changePasswordAPI();
            }
        });

        btnCancel = (TextView) findViewById(R.id.updatePassword_tvCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void passwordVisibilityOnOff(ImageView ivEye, EditText etPassword, boolean bVisibility) {
        if (bVisibility) {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            etPassword.setSelection(etPassword.length());
            ivEye.setImageResource(R.drawable.ic_visibility_off_24dp);
        } else {
            etPassword.setTransformationMethod(null);
            etPassword.setSelection(etPassword.length());
            ivEye.setImageResource(R.drawable.ic_visibility_on_24dp);
        }
    }

    private void changePasswordAPI() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String mobNumber = Util_SharedPreference.getMobileNumberFromSP(ChangePassword.this);
        Log.d("ChangePass:mob-Old-New", mobNumber + " - " + strOldPass + " - " + strNewPass);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_ChangePassword(mobNumber, strOldPass, strNewPass);
        Call<JsonArray> call = apiService.ChangePasswordnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("ChangePass:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("ChangePass:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            showToast(strMsg);
                            Util_SharedPreference.clearParentSharedPreference(ChangePassword.this);
                            startActivity(new Intent(ChangePassword.this, SignInScreen.class));
                            finish();
                        } else {
                            showToast(strMsg + "error");
                        }

                    } else {
                        showToast("No Records Found..");
                    }

                } catch (Exception e) {
                    Log.e("ChangePass:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("ChangePass:Failure", t.toString());
            }
        });
    }
}
