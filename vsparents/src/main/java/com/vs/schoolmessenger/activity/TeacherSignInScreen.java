package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService.pubStArrChildList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_OFFICE_STAFF;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.AdView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.OTP.AutoReadOTPCallNumberScreen;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.SliderAdsImage.ShowAds;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherProfiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AddCouponPoints;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;
import com.vs.schoolmessenger.util.Util_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;


public class TeacherSignInScreen extends AppCompatActivity implements View.OnClickListener {
    public static String par, princi, staf, grouphead, admin;
    Button btnSignIn;
    EditText etMobile, etPassword;
    TextView tvForgotPassword;
    ImageView ivEye;
    boolean bEyeVisible = false;
    int PERMISSION_ALL = 1, mobnumberlength;
    String strMobile, strPassword, strmobilenumberlength, num;
    ArrayList<String> loginTypeList = new ArrayList<>();
    TeacherProfiles staffInfo;
    ArrayList<Profiles> arrayList;
    ArrayList<Profiles> arrChildList = new ArrayList<>();
    ArrayList<String> schoolNamelist = new ArrayList<>();
    LinearLayout mAdView;
    ImageView adImage;
    Slider slider;

    TextView textView2;

    public static boolean hasPermissions(Context context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

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
        setContentView(R.layout.teacher_activity_sign_in_screen);

        etMobile = (EditText) findViewById(R.id.login_etMobile);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
        textView2 = (TextView) findViewById(R.id.textView2);
        String data = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherSignInScreen.this);

        TeacherUtil_SharedPreference.putPassWordType(TeacherSignInScreen.this, "Signin_screen");
        strmobilenumberlength = TeacherUtil_SharedPreference.getMobileNumberLengthFromSP(TeacherSignInScreen.this);
        mobnumberlength = Integer.parseInt(strmobilenumberlength);


        String mobileHint = TeacherUtil_SharedPreference.getMobileHint(TeacherSignInScreen.this);

        if(mobileHint.equals("")) {
            etMobile.setHint(getResources().getString(R.string.Enter) + " " + strmobilenumberlength + " " + getResources().getString(R.string.Digit_Mobile_Number));
            textView2.setText(strmobilenumberlength + " " + getResources().getString(R.string.Digit_Mobile_Number));
        }
        else {
            etMobile.setHint(mobileHint);
            textView2.setText(strmobilenumberlength + " " + getResources().getString(R.string.Digit_Mobile_Number));
        }

        Log.d("textView2", textView2.getText().toString());
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(mobnumberlength);
        etMobile.setFilters(fArray);

        tvForgotPassword = (TextView) findViewById(R.id.login_tvForgotPassword);
        tvForgotPassword.setOnClickListener(this);

        btnSignIn = (Button) findViewById(R.id.login_btnSignIn);
        btnSignIn.setOnClickListener(this);

        ivEye = (ImageView) findViewById(R.id.login_ivEye);
        ivEye.setOnClickListener(this);

        slider = findViewById(R.id.banner);
        adImage = findViewById(R.id.adImage);
        mAdView = findViewById(R.id.adView);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onResume() {
        super.onResume();
        Constants.Menu_ID = "100";
        ShowAds.getAdsWithoutNative(TeacherSignInScreen.this, adImage, slider, "Signin", mAdView);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void passwordVisibilityOnOff() {
        if (bEyeVisible) {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            etPassword.setSelection(etPassword.length());
            bEyeVisible = false;
            ivEye.setImageResource(R.drawable.teacher_ic_visibility_off_24dp);
        } else {
            etPassword.setTransformationMethod(null);
            etPassword.setSelection(etPassword.length());
            bEyeVisible = true;
            ivEye.setImageResource(R.drawable.teacher_ic_visibility_on_24dp);
        }
    }

    @Override
    public void onClick(View v) {
        int bID = v.getId();
        switch (bID) {
            case R.id.login_btnSignIn:
                if (validateLoginInputs())
                    validateUser();

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

    private void validateUser() {
        TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, false);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSignInScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        JsonObject json_object = TeacherUtil_JsonRequest.getJsonArray_ValidateUser(strMobile, androidId, strPassword);
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
                                String click_here = jsonObject.getString("MoreInfo");
                                String redirect_to_otp;
                                if (jsonObject.has("redirect_to_otp")) {
                                    redirect_to_otp = jsonObject.getString("redirect_to_otp");
                                } else {
                                    redirect_to_otp = "";
                                }

                                TeacherUtil_SharedPreference.putNoteMessage(TeacherSignInScreen.this, click_here);
                                TeacherUtil_SharedPreference.putOTPNote(TeacherSignInScreen.this, Message);

                                if (numberExists.equals("1") && passwordUpdated.equals("1")) {

                                    if (redirect_to_otp.equals("1")) {
                                        Intent inChangePass = new Intent(TeacherSignInScreen.this, AutoReadOTPCallNumberScreen.class);
                                        startActivity(inChangePass);
                                        finish();
                                    } else {
                                        getUserDetails();
                                    }
                                } else if (numberExists.equals("1") && passwordUpdated.equals("0")) {
                                    Util_SharedPreference.putForget(TeacherSignInScreen.this, "New");
                                    Intent inChangePass = new Intent(TeacherSignInScreen.this, OTPCallNumberScreen.class);
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
            // if (strMobile.length() != 10) {
            showToast(getResources().getString(R.string.enter_valid_mobile));
            return false;
        } else if (strPassword.length() < 1) {
            showToast(getResources().getString(R.string.enter_correct_password));
            return false;
        } else return true;
    }


    private void forgotPasswordValidation() {

        strMobile = etMobile.getText().toString().trim();

        if (strMobile.length() != mobnumberlength)
            showToast(getResources().getString(R.string.registered_mobile));
        else {
            forgotPasswordAPI(strMobile);
        }

    }

    private void forgotPasswordAPI(String mobileNumber) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSignInScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String CountryID = TeacherUtil_SharedPreference.getCountryID(TeacherSignInScreen.this);
        Log.d("ForgotPassword:Mob", mobileNumber);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_ForgetPassword(mobileNumber, CountryID);
        Call<JsonArray> call = apiService.ForgetPassword(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("ForgotPassword:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("ForgotPassword:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");
                        String numbers = jsonObject.getString("DialNumbers");
                        String click_here = jsonObject.getString("MoreInfo");
                        String redirectToSignScreenMessage = jsonObject.getString("ForgetOTPMesage");

                        TeacherUtil_SharedPreference.putDialNumbers(TeacherSignInScreen.this, numbers);
                        TeacherUtil_SharedPreference.putOTPNote(TeacherSignInScreen.this, strMessage);


                        hideKeyBoard();

                        if (strStatus.equals("1")) {
                            TeacherUtil_SharedPreference.putForgetMessage(TeacherSignInScreen.this, redirectToSignScreenMessage);
                            Util_SharedPreference.putForget(TeacherSignInScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, "", false);


                            TeacherUtil_SharedPreference.putForgetPasswordOTP(TeacherSignInScreen.this, "1");

                            Intent inChangePass = new Intent(TeacherSignInScreen.this, OTPCallNumberScreen.class);
                            inChangePass.putExtra("Dial_numbers", numbers);
                            inChangePass.putExtra("Type", "forget");
                            inChangePass.putExtra("note_message", click_here);
                            startActivity(inChangePass);

                        } else {
                            showAlert(strMessage);
                        }


                    } else {
                        showToast(getResources().getString(R.string.no_records));
                    }

                } catch (Exception e) {
                    Log.e("ForgotPassword:Ex", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("ForgotPassword:Failure", t.toString());
            }
        });
    }

    private void hideKeyBoard() {

        try {
            InputMethodManager inputManager = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        }
    }

    private void getUserDetails() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(TeacherSignInScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(TeacherSignInScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("ID_android", androidId);

        Log.d("Mobileno", strMobile + strPassword);
        strMobile = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherSignInScreen.this);
        strPassword = TeacherUtil_SharedPreference.getPasswordFromSP(TeacherSignInScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        strMobile = etMobile.getText().toString().trim();
        strPassword = etPassword.getText().toString().trim();

        JsonObject jsonReqArray = TeacherUtil_JsonRequest.GetUserDetails(strMobile, strPassword, androidId);
        Log.d("Login Req", jsonReqArray.toString());
        Call<JsonArray> call = apiService.getStaffDetail(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();


                Log.d("Login:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Login:Res", response.body().toString());


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String Status = jsonObject.getString("Status");
                        String Message = jsonObject.getString("Message");


                        String strlogin = "";
                        TeacherSchoolsModel schoolmodel = null;
                        listschooldetails = new ArrayList<>();
                        if (Status.equals("1")) {
                            AddCouponPoints.addPoints(TeacherSignInScreen.this, Util_Common.LOGIN_POINTS);

                            String role = jsonObject.getString("staff_role");
                            String display_role = jsonObject.getString("staff_display_role");
                            TeacherUtil_SharedPreference.putRole(TeacherSignInScreen.this, role);
                            TeacherUtil_SharedPreference.putDisplayRoleMessage(TeacherSignInScreen.this, display_role);
                            TeacherUtil_SharedPreference.putLoginMessage(TeacherSignInScreen.this, Message);
                            String ImageCount = jsonObject.getString("ImageCount");
                            TeacherUtil_SharedPreference.putImageCount(TeacherSignInScreen.this, ImageCount);

                            Boolean is_parent = jsonObject.getBoolean("is_parent");
                            Boolean is_staff = jsonObject.getBoolean("is_staff");
                            TeacherUtil_SharedPreference.putIsStaff(TeacherSignInScreen.this, is_staff);
                            TeacherUtil_SharedPreference.putIsParent(TeacherSignInScreen.this, is_parent);


                            TeacherUtil_Common.maxEmergencyvoicecount = jsonObject.getInt("MaxEmergencyVoiceDuration");
                            TeacherUtil_Common.maxGeneralvoicecount = jsonObject.getInt("MaxGeneralVoiceDuartion");
                            TeacherUtil_Common.maxHWVoiceDuration = jsonObject.getInt("MaxHWVoiceDuration");
                            TeacherUtil_Common.maxGeneralSMSCount = jsonObject.getInt("MaxGeneralSMSCount");
                            TeacherUtil_Common.maxHomeWorkSMSCount = jsonObject.getInt("MaxHomeWorkSMSCount");


                            if (is_staff && is_parent) {

                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");

                                for (int i = 0; i < jSONArray1.length(); i++) {

                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true,
                                            jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                    schoolNamelist.add(jsonObjectdetailsStaff.getString("SchoolName"));
                                }

                                JSONObject object = jSONArray1.getJSONObject(0);
                                if (listschooldetails.size() == 1) {

                                    String strSchoolId = object.getString("SchoolID");
                                    String strStaffId = object.getString("StaffID");
                                    TeacherUtil_Common.Principal_staffId = strStaffId;
                                    TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                    Util_Common.isSchoolType = object.getInt("school_type");
                                    TeacherUtil_Common.isBioMetricEnable = object.getInt("biometricEnable");
                                    TeacherUtil_Common.isVideoDownload = object.getBoolean("allowVideoDownload");
                                    TeacherUtil_SharedPreference.putShoolID(TeacherSignInScreen.this, strSchoolId);
                                    TeacherUtil_SharedPreference.putStaffID(TeacherSignInScreen.this, strStaffId);
                                    String logo = object.getString("SchoolLogo");
                                    TeacherUtil_SharedPreference.putSchoolLogo(TeacherSignInScreen.this, logo);
                                }


                                String logo = object.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(TeacherSignInScreen.this, logo);
                                String strSchoolId = object.getString("SchoolID");
                                String strStaffId1 = object.getString("StaffID");
                                String schoolname = object.getString("SchoolName");
                                String schooladdress = object.getString("SchoolAddress");
                                TeacherUtil_SharedPreference.putSchoolName(TeacherSignInScreen.this, schoolname);
                                TeacherUtil_SharedPreference.putStaffAddress(TeacherSignInScreen.this, schooladdress);


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"),
                                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);

                                TeacherUtil_SharedPreference.listSchoolDetails(TeacherSignInScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(TeacherSignInScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(TeacherSignInScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(TeacherSignInScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                inHome.putExtra("SCHOOL_ID & Staff_ID", strSchoolId + " " + strStaffId1);
                                inHome.putExtra("schoolname", schoolname);
                                inHome.putExtra("Staff_ID1", strStaffId1);
                                inHome.putExtra("schoollist", schoolNamelist);
                                inHome.putExtra("schooladdress", schooladdress);
                                inHome.putExtra("TeacherSchoolsModel", schoolmodel);
                                inHome.putExtra("list", listschooldetails);


                                if (role.equals("p1")) {
                                    strlogin = LOGIN_TYPE_HEAD;
                                } else if (role.equals("p2")) {
                                    strlogin = LOGIN_TYPE_PRINCIPAL;

                                } else if (role.equals("p3")) {
                                    strlogin = LOGIN_TYPE_TEACHER;

                                } else if (role.equals("p4")) {
                                    strlogin = LOGIN_TYPE_ADMIN;

                                } else if (role.equals("p5")) {
                                    strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                }
                                startActivity(inHome);
                                finish();

                            } else if (!is_staff && is_parent) {
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"),
                                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                Intent inHome = new Intent(TeacherSignInScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                startActivity(inHome);
                                finish();
                            } else if (role.equals("p2")) {
                                JSONArray jSONArray = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jsonObjectdetails = jSONArray.getJSONObject(i);

                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolNameRegional"), jsonObjectdetails.getString("SchoolID"),
                                            jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo")
                                            , jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"),
                                            jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"), jsonObjectdetails.getInt("school_type"), jsonObjectdetails.getInt("biometricEnable"),jsonObjectdetails.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetails.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                    schoolNamelist.add(jsonObjectdetails.getString("SchoolName"));
                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetails.getString("SchoolID");
                                        String strStaffId = jsonObjectdetails.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                        Util_Common.isSchoolType = jsonObjectdetails.getInt("school_type");
                                        TeacherUtil_Common.isBioMetricEnable = jsonObjectdetails.getInt("biometricEnable");
                                        TeacherUtil_Common.isVideoDownload = jsonObjectdetails.getBoolean("allowVideoDownload");

                                        String logo = jsonObjectdetails.getString("SchoolLogo");
                                        TeacherUtil_SharedPreference.putSchoolLogo(TeacherSignInScreen.this, logo);
                                    }
                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                Intent i = new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                i.putExtra("schoollist", schoolNamelist);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                finish();

                            } else if (role.equals("p1")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"),
                                            jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo")
                                            , jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"),
                                            jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload")
                                    );
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);

                                Intent i = new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class);
                                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                                TeacherUtil_SharedPreference.isAdminFromSP(TeacherSignInScreen.this);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_HEAD;
                                finish();

                            } else if (role.equals("p3")) {

                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"),
                                            jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo")
                                            , jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"),
                                            jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload")
                                    );
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }

                                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"),
                                        jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                        , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                        jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload")
                                );
                                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));


                                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(TeacherSignInScreen.this, logo);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");

                                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                                TeacherUtil_Common.Principal_staffId = strStaffId1;
                                TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                Util_Common.isSchoolType = jsonObjectdetailsStaff.getInt("school_type");
                                TeacherUtil_Common.isBioMetricEnable = jsonObjectdetailsStaff.getInt("biometricEnable");
                                TeacherUtil_Common.isVideoDownload = jsonObjectdetailsStaff.getBoolean("allowVideoDownload");


                                if (listschooldetails.size() == 1) {
                                    Intent i = new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class);
                                    i.putExtra("SCHOOL_ID & Staff_ID", strSchoolId + " " + strStaffId1);
                                    i.putExtra("schoolname", schoolname);
                                    i.putExtra("Staff_ID1", strStaffId1);
                                    i.putExtra("schooladdress", schooladdress);
                                    i.putExtra("TeacherSchoolsModel", schoolmodel);
                                    i.putExtra("list", listschooldetails);
                                    Log.d("Schoolid", TeacherUtil_Common.Principal_SchoolId);
                                    startActivity(i);
                                    strlogin = LOGIN_TYPE_TEACHER;
                                    finish();
                                } else if (listschooldetails.size() > 1) {
                                    Intent i = new Intent(TeacherSignInScreen.this, SelectStaffSchools.class);
                                    startActivity(i);
                                    strlogin = LOGIN_TYPE_TEACHER;
                                    finish();
                                }


                            } else if (role.equals("p4")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload")
                                    );
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);

                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                        Util_Common.isSchoolType = jsonObjectdetailsStaff.getInt("school_type");
                                        TeacherUtil_Common.isBioMetricEnable = jsonObjectdetailsStaff.getInt("biometricEnable");
                                        TeacherUtil_Common.isVideoDownload = jsonObjectdetailsStaff.getBoolean("allowVideoDownload");
                                    }
                                }


                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                Intent i = new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_ADMIN;
                                finish();

                            } else if (role.equals("p5")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"),
                                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload")
                                    );
                                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                    if (listschooldetails.size() == 1) {
                                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                                        TeacherUtil_Common.Principal_staffId = strStaffId;
                                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                        Util_Common.isSchoolType = jsonObjectdetailsStaff.getInt("school_type");
                                        TeacherUtil_Common.isBioMetricEnable = jsonObjectdetailsStaff.getInt("biometricEnable");
                                        TeacherUtil_Common.isVideoDownload = jsonObjectdetailsStaff.getBoolean("allowVideoDownload");
                                    }
                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                Intent i = new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                finish();

                            }


                            TeacherUtil_SharedPreference.putLoggedInAsToSP(TeacherSignInScreen.this, strlogin);


                        } else if ((Status).equalsIgnoreCase("RESET")) {
                            Util_SharedPreference.putForget(TeacherSignInScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, false);

                            Intent inChangePass = new Intent(TeacherSignInScreen.this, TeacherChangePassword.class);
                            startActivity(inChangePass);
                        } else {
                            showAlert(Message);
                        }

                    } else {
                        showToast(getResources().getString(R.string.no_records));
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("Login:Exception", e.getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("Login:Failure", t.toString());
            }
        });
    }


    private void showAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TeacherSignInScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
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


}
