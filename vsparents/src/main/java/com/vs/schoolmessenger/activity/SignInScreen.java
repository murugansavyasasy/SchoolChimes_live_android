package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Util_JsonRequest;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService.pubStArrChildList;

public class SignInScreen extends AppCompatActivity implements View.OnClickListener {

    Button btnSignIn;
    EditText etMobile, etPassword;
    TextView tvForgotPassword;

    ImageView ivEye;
    boolean bEyeVisible = false;

    String strMobile, strPassword, strmobilenumberlength;
    int mobnumberlength;

    ArrayList<Profiles> arrChildList = new ArrayList<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_screen);



        etMobile = (EditText) findViewById(R.id.login_etMobile);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
        String strcountry = Util_SharedPreference.getMobileNumberLengthFromSP(SignInScreen.this);
        Log.d("County", strcountry);
        strmobilenumberlength = Util_SharedPreference.getMobileNumberLengthFromSP(SignInScreen.this);
        mobnumberlength = Integer.parseInt(strmobilenumberlength);

        Log.d("MobilenumberLength", strmobilenumberlength + "  ");
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(mobnumberlength);
        etMobile.setFilters(fArray);

        tvForgotPassword = (TextView) findViewById(R.id.login_tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);

        btnSignIn = (Button) findViewById(R.id.login_btnSignIn);
        btnSignIn.setOnClickListener(this);

        ivEye = (ImageView) findViewById(R.id.login_ivEye);
        ivEye.setOnClickListener(this);


    }

    private void passwordVisibilityOnOff() {
        if (bEyeVisible) {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            etPassword.setSelection(etPassword.length());
            bEyeVisible = false;
            ivEye.setImageResource(R.drawable.ic_visibility_off_24dp);
        } else {
            etPassword.setTransformationMethod(null);
            etPassword.setSelection(etPassword.length());
            bEyeVisible = true;
            ivEye.setImageResource(R.drawable.ic_visibility_on_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        int bID = v.getId();
        switch (bID) {
            case R.id.login_btnSignIn:
                if (validateLoginInputs())

                break;

            case R.id.login_ivEye:
                passwordVisibilityOnOff();
                break;

            case R.id.login_tvForgotPassword:
                forgotPasswordValidation();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private boolean validateLoginInputs() {
        strMobile = etMobile.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        if (strMobile.length() != mobnumberlength) {
            showToast("Enter Valid Mobile Number");
            return false;
        } else if (strPassword.length() < 1) {
            showToast("Enter Valid Password");
            return false;
        } else return true;
    }








    private void forgotPasswordValidation() {
        strMobile = Util_SharedPreference.getMobileNumberFromSP(SignInScreen.this);
        if (strMobile.equals("")) {
            strMobile = etMobile.getText().toString().trim();
            if (strMobile.length() != 10)
                showToast("Enter Registered Mobile number");
            else {
                forgotPasswordAPI(strMobile);
            }
        } else {
            forgotPasswordAPI(strMobile);
        }
    }

    private void forgotPasswordAPI(String mobileNumber) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

//        MessengerApiClient.BASE_URL = Util_SharedPreference.getBaseUrlFromSP(SignInScreen.this);
      //  MessengerApiClient.changeApiBaseUrl(Util_SharedPreference.getBaseUrlFromSP(SignInScreen.this));

        Log.d("ForgotPassword:Mob", mobileNumber);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_ForgetPassword(mobileNumber);
        Call<JsonArray> call = apiService.ForgetPasswordnew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("ForgotPassword:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("ForgotPassword:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        hideKeyBoard();
                        showForgotPasswordAlert("Forgot Password", strMessage);

//                        showToast(strMessage);
//                        if ((strStatus.toLowerCase()).equals("y")) {
//                                hideKeyBoard();
//                        }
                    } else {
                        showToast("Server Response Failed. Try again");
                    }

                } catch (Exception e) {
                    Log.e("ForgotPassword:Ex", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast("Check your Internet connectivity");
                Log.d("ForgotPassword:Failure", t.toString());
            }
        });
    }

    private void hideKeyBoard() {
//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//        );

        try {
            InputMethodManager inputManager = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }

    }

    private void showForgotPasswordAlert(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                SignInScreen.this);
        AlertDialog alertDialog;

        builder.setCancelable(false);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setIcon(R.drawable.ic_voice_snap);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));

    }
}
