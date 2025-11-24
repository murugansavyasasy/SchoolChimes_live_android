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
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.OTP.AutoReadOTPCallNumberScreen;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.AddCouponPoints;
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

/**
 * Created by voicesnap on 4/26/2018.
 */

public class PasswordScreen extends AppCompatActivity {
    Button btnSubmit;
    EditText login_etPassword;
    TextView login_tvForgotPassword;
    ImageView login_ivEye;

    ArrayList<Profiles> arrChildList = new ArrayList<>();

    String mobilenumber;
    ArrayList<Profiles> arrayList;
    boolean bEyeVisible = false;
    String number, password;

    ArrayList<String> schoolNamelist = new ArrayList<>();
    TelephonyManager telephonyManager;
    String IMEINumber = "", strmobilenumberlength;
    int mobnumberlength;
    String UserPassword;
    JSONObject jsonObjectdetailsStaff;

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
        setContentView(R.layout.password_screen);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            UserPassword = extras.getString("UserPassword", "");
        }

        TeacherUtil_SharedPreference.putPermission(PasswordScreen.this, "1");
        TeacherUtil_SharedPreference.putPassWordType(PasswordScreen.this, "password_screen");

        strmobilenumberlength = TeacherUtil_SharedPreference.getMobileNumberLengthFromSP(PasswordScreen.this);
        mobnumberlength = Integer.parseInt(strmobilenumberlength);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        login_etPassword = (EditText) findViewById(R.id.login_etPassword);
        login_tvForgotPassword = (TextView) findViewById(R.id.login_tvForgotPassword);

        login_etPassword.setEnabled(true);
        login_ivEye = (ImageView) findViewById(R.id.login_ivEye);

        login_ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordVisibilityOnOff();
            }
        });
        login_tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPasswordValidation();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login_etPassword.getText().toString().equals("")) {
                    validatePassword();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_correct_password), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void validatePassword() {
        String mobilenumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(PasswordScreen.this);
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(PasswordScreen.this);
        Log.d("BASE URL", baseURL);

        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", mobilenumber);
        jsonObject.addProperty("Password", login_etPassword.getText().toString());
        Log.d("validate_password", jsonObject.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.validatePassword(jsonObject);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("validate pass", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("validate:pass res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        if (strStatus.equals("1")) {
                            getUserDetails();
                        } else {
                            showToast(strMessage);
                        }

                    } else {
                        showToast(getResources().getString(R.string.Invalid_password));
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

    private void passwordVisibilityOnOff() {
        if (bEyeVisible) {
            login_etPassword.setTransformationMethod(new PasswordTransformationMethod());
            login_etPassword.setSelection(login_etPassword.length());
            bEyeVisible = false;
            login_ivEye.setImageResource(R.drawable.teacher_ic_visibility_off_24dp);
        } else {
            login_etPassword.setTransformationMethod(null);
            login_etPassword.setSelection(login_etPassword.length());
            bEyeVisible = true;
            login_ivEye.setImageResource(R.drawable.teacher_ic_visibility_on_24dp);
        }
    }

    private void forgotPasswordValidation() {
        mobilenumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(PasswordScreen.this);

        if (mobilenumber.length() != mobnumberlength)
            showToast(getResources().getString(R.string.registered_mobile));
        else {
            forgotPasswordAPI(mobilenumber);
        }

    }

    private void forgotPasswordAPI(final String mobilenumber) {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(PasswordScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String CountryID = TeacherUtil_SharedPreference.getCountryID(PasswordScreen.this);
        Log.d("ForgotPassword:Mob", mobilenumber);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_ForgetPassword(mobilenumber, CountryID);
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

                        TeacherUtil_SharedPreference.putDialNumbers(PasswordScreen.this, numbers);
                        TeacherUtil_SharedPreference.putOTPNote(PasswordScreen.this, strMessage);


                        hideKeyBoard();

                        if (strStatus.equals("1")) {
                            TeacherUtil_SharedPreference.putForgetMessage(PasswordScreen.this, redirectToSignScreenMessage);
                            Util_SharedPreference.putForget(PasswordScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, mobilenumber, password, false);
                            TeacherUtil_SharedPreference.putForgetPasswordOTP(PasswordScreen.this, "1");
                            Intent inChangePass = new Intent(PasswordScreen.this, OTPCallNumberScreen.class);
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
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(PasswordScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(PasswordScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Log.d("ID_android", androidId);

        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(PasswordScreen.this);
        Log.d("number1", number);
        password = TeacherUtil_SharedPreference.getPasswordFromSP(PasswordScreen.this);
        Log.d("password1", password);
        password = login_etPassword.getText().toString();
        Log.d("password2", password);
        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(PasswordScreen.this);
        Log.d("number2", number);


        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = TeacherUtil_JsonRequest.GetUserDetails(number, password, androidId);
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

                            AddCouponPoints.addPoints(PasswordScreen.this, Util_Common.LOGIN_POINTS);

                            String role = jsonObject.getString("staff_role");
                            String display_role = jsonObject.getString("staff_display_role");
                            TeacherUtil_SharedPreference.putRole(PasswordScreen.this, role);
                            TeacherUtil_SharedPreference.putDisplayRoleMessage(PasswordScreen.this, display_role);
                            TeacherUtil_SharedPreference.putLoginMessage(PasswordScreen.this, Message);
                            String ImageCount = jsonObject.getString("ImageCount");
                            TeacherUtil_SharedPreference.putImageCount(PasswordScreen.this, ImageCount);


                            Boolean is_parent = jsonObject.getBoolean("is_parent");
                            Boolean is_staff = jsonObject.getBoolean("is_staff");
                            TeacherUtil_SharedPreference.putIsStaff(PasswordScreen.this, is_staff);
                            TeacherUtil_SharedPreference.putIsParent(PasswordScreen.this, is_parent);


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
                                    TeacherUtil_SharedPreference.putShoolID(PasswordScreen.this, strSchoolId);
                                    TeacherUtil_SharedPreference.putStaffID(PasswordScreen.this, strStaffId);
                                    String logo = object.getString("SchoolLogo");
                                    TeacherUtil_SharedPreference.putSchoolLogo(PasswordScreen.this, logo);
                                }


                                String logo = object.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(PasswordScreen.this, logo);
                                String strSchoolId = object.getString("SchoolID");
                                String strStaffId1 = object.getString("StaffID");
                                String schoolname = object.getString("SchoolName");
                                String schooladdress = object.getString("SchoolAddress");
                                TeacherUtil_SharedPreference.putSchoolName(PasswordScreen.this, schoolname);
                                TeacherUtil_SharedPreference.putStaffAddress(PasswordScreen.this, schooladdress);


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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);

                                TeacherUtil_SharedPreference.listSchoolDetails(PasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(PasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(PasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(PasswordScreen.this, ChildrenScreen.class);
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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
                                Intent inHome = new Intent(PasswordScreen.this, ChildrenScreen.class);
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
                                        TeacherUtil_SharedPreference.putSchoolLogo(PasswordScreen.this, logo);
                                    }
                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                Intent i = new Intent(PasswordScreen.this, Teacher_AA_Test.class);
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
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);

                                Intent i = new Intent(PasswordScreen.this, Teacher_AA_Test.class);
                                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                                TeacherUtil_SharedPreference.isAdminFromSP(PasswordScreen.this);
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
                                TeacherUtil_SharedPreference.putSchoolLogo(PasswordScreen.this, logo);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
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
                                    Intent i = new Intent(PasswordScreen.this, Teacher_AA_Test.class);
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
                                    Intent i = new Intent(PasswordScreen.this, SelectStaffSchools.class);
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


                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
                                Intent i = new Intent(PasswordScreen.this, Teacher_AA_Test.class);
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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, true);
                                Intent i = new Intent(PasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                finish();

                            }

                            TeacherUtil_SharedPreference.putLoggedInAsToSP(PasswordScreen.this, strlogin);


                        } else if ((Status).equalsIgnoreCase("RESET")) {
                            Util_SharedPreference.putForget(PasswordScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(PasswordScreen.this, number, password, false);
                            Intent inChangePass = new Intent(PasswordScreen.this, TeacherChangePassword.class);
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
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PasswordScreen.this);
        alertDialog.setTitle(R.string.alert);
        alertDialog.setMessage(message);
        alertDialog.setNegativeButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));


    }


    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
