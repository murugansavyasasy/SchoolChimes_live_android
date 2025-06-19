package com.vs.schoolmessenger.activity;

import static com.vs.schoolmessenger.R.layout.create_password;
import static com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService.pubStArrChildList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_OFFICE_STAFF;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
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

public class ChangePasswordScreen extends AppCompatActivity {
    Button btnConfirmSubmit;
    EditText create_etPassword, confirm_etPassword1;

    ImageView login_ivEye, login_ivEye1;
    ArrayList<Profiles> arrChildList = new ArrayList<>();
    String number, imei, confirm_password;
    TelephonyManager telephonyManager;
    boolean bEyeVisible = false;
    boolean bEyeVisible1 = false;

    ArrayList<Profiles> arrayList;
    ArrayList<String> schoolNamelist = new ArrayList<>();
    String IMEINumber = "";

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
        setContentView(create_password);


        btnConfirmSubmit = (Button) findViewById(R.id.btnConfirmSubmit);
        create_etPassword = (EditText) findViewById(R.id.create_etPassword);
        confirm_etPassword1 = (EditText) findViewById(R.id.confirm_etPassword1);
        login_ivEye = (ImageView) findViewById(R.id.login_ivEye);
        login_ivEye1 = (ImageView) findViewById(R.id.login_ivEye1);

        login_ivEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordVisibilityOnOff();
            }
        });
        login_ivEye1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordVisibilityOnOff1();
            }
        });

        btnConfirmSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String create_password = create_etPassword.getText().toString();
                Log.d("craete_pass", create_password);

                confirm_password = confirm_etPassword1.getText().toString();
                Log.d("confirm_pass", confirm_password);

                if (create_password.equals(confirm_password)) {
                    verifyPassword();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.enter_correct_password), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void passwordVisibilityOnOff1() {
        if (bEyeVisible1) {
            confirm_etPassword1.setTransformationMethod(new PasswordTransformationMethod());
            confirm_etPassword1.setSelection(confirm_etPassword1.length());
            bEyeVisible1 = false;
            login_ivEye1.setImageResource(R.drawable.teacher_ic_visibility_off_24dp);
        } else {
            confirm_etPassword1.setTransformationMethod(null);
            confirm_etPassword1.setSelection(confirm_etPassword1.length());
            bEyeVisible1 = true;
            login_ivEye1.setImageResource(R.drawable.teacher_ic_visibility_on_24dp);
        }
    }

    private void passwordVisibilityOnOff() {
        if (bEyeVisible) {
            create_etPassword.setTransformationMethod(new PasswordTransformationMethod());
            create_etPassword.setSelection(create_etPassword.length());
            bEyeVisible = false;
            login_ivEye.setImageResource(R.drawable.teacher_ic_visibility_off_24dp);
        } else {
            create_etPassword.setTransformationMethod(null);
            create_etPassword.setSelection(create_etPassword.length());
            bEyeVisible = true;
            login_ivEye.setImageResource(R.drawable.teacher_ic_visibility_on_24dp);
        }
    }

    private void verifyPassword() {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ChangePasswordScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChangePasswordScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", number);
        jsonObject.addProperty("NewPassword", confirm_password);
        jsonObject.addProperty("IMEINumber", IMEINumber);
        Call<JsonArray> call = apiService.UpdateNewPasswordforNewUser(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");

                            if (Status.equals("1")) {
                                alertShow(Message);

                            } else {
                                showAlert(Message);
                            }
                        }
                    } else {
                        //showToast("Server Response Failed");
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                // showToast("Server Connection Failed");
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void alertShow(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordScreen.this);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton(getResources().getString(R.string.teacher_btn_ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                getUserDetails();
            }
        });

        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(getResources().getColor(R.color.teacher_colorPrimary));

    }

    private void getUserDetails() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ChangePasswordScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(ChangePasswordScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing()) mProgressDialog.show();

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("ID_android", androidId);

        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChangePasswordScreen.this);
        confirm_password = TeacherUtil_SharedPreference.getPasswordFromSP(ChangePasswordScreen.this);

        confirm_password = confirm_etPassword1.getText().toString();
        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChangePasswordScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        JsonObject jsonReqArray = TeacherUtil_JsonRequest.GetUserDetails(number, confirm_password, androidId);
        Call<JsonArray> call = apiService.getStaffDetail(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();


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
                            AddCouponPoints.addPoints(ChangePasswordScreen.this, Util_Common.LOGIN_POINTS);

                            String role = jsonObject.getString("staff_role");
                            String display_role = jsonObject.getString("staff_display_role");
                            TeacherUtil_SharedPreference.putRole(ChangePasswordScreen.this, role);
                            TeacherUtil_SharedPreference.putDisplayRoleMessage(ChangePasswordScreen.this, display_role);
                            TeacherUtil_SharedPreference.putLoginMessage(ChangePasswordScreen.this, Message);
                            String ImageCount = jsonObject.getString("ImageCount");
                            TeacherUtil_SharedPreference.putImageCount(ChangePasswordScreen.this, ImageCount);


                            Boolean is_parent = jsonObject.getBoolean("is_parent");
                            Boolean is_staff = jsonObject.getBoolean("is_staff");
                            TeacherUtil_SharedPreference.putIsStaff(ChangePasswordScreen.this, is_staff);
                            TeacherUtil_SharedPreference.putIsParent(ChangePasswordScreen.this, is_parent);


                            TeacherUtil_Common.maxEmergencyvoicecount = jsonObject.getInt("MaxEmergencyVoiceDuration");
                            TeacherUtil_Common.maxGeneralvoicecount = jsonObject.getInt("MaxGeneralVoiceDuartion");
                            TeacherUtil_Common.maxHWVoiceDuration = jsonObject.getInt("MaxHWVoiceDuration");
                            TeacherUtil_Common.maxGeneralSMSCount = jsonObject.getInt("MaxGeneralSMSCount");
                            TeacherUtil_Common.maxHomeWorkSMSCount = jsonObject.getInt("MaxHomeWorkSMSCount");


                            if (is_staff && is_parent) {

                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");

                                for (int i = 0; i < jSONArray1.length(); i++) {

                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
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
                                    TeacherUtil_SharedPreference.putShoolID(ChangePasswordScreen.this, strSchoolId);
                                    TeacherUtil_SharedPreference.putStaffID(ChangePasswordScreen.this, strStaffId);
                                    String logo = object.getString("SchoolLogo");
                                    TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                }


                                String logo = object.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                String strSchoolId = object.getString("SchoolID");
                                String strStaffId1 = object.getString("StaffID");
                                String schoolname = object.getString("SchoolName");
                                String schooladdress = object.getString("SchoolAddress");
                                TeacherUtil_SharedPreference.putSchoolName(ChangePasswordScreen.this, schoolname);
                                TeacherUtil_SharedPreference.putStaffAddress(ChangePasswordScreen.this, schooladdress);


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                TeacherUtil_SharedPreference.listSchoolDetails(ChangePasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(ChangePasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(ChangePasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                startActivity(inHome);
                                finish();


                            } else if (role.equals("p2")) {
                                JSONArray jSONArray = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jsonObjectdetails = jSONArray.getJSONObject(i);

                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolNameRegional"), jsonObjectdetails.getString("SchoolID"), jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo"), jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"), jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"), jsonObjectdetails.getInt("school_type"), jsonObjectdetails.getInt("biometricEnable"),jsonObjectdetails.getBoolean("allowVideoDownload"));
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
                                        TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                    }
                                }

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                i.putExtra("schoollist", schoolNamelist);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                finish();

                            } else if (role.equals("p1")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"), jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo"), jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"), jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                                TeacherUtil_SharedPreference.isAdminFromSP(ChangePasswordScreen.this);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_HEAD;
                                finish();

                            } else if (role.equals("p3")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");

                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"), jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo"), jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"), jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }
                                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
                                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));


                                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
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
                                    Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
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
                                    Intent i = new Intent(ChangePasswordScreen.this, SelectStaffSchools.class);
                                    startActivity(i);
                                    strlogin = LOGIN_TYPE_TEACHER;
                                    finish();
                                }
                            } else if (role.equals("p4")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
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


                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_ADMIN;
                                finish();

                            } else if (role.equals("p5")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("StaffDetails");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
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

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);

                                startActivity(i);
                                strlogin = LOGIN_TYPE_OFFICE_STAFF;
                                finish();

                            }
                            TeacherUtil_SharedPreference.putLoggedInAsToSP(ChangePasswordScreen.this, strlogin);


                        } else if ((Status).equalsIgnoreCase("RESET")) {
                            Util_SharedPreference.putForget(ChangePasswordScreen.this, "forget");
                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, false);
                            Intent inChangePass = new Intent(ChangePasswordScreen.this, TeacherChangePassword.class);
                            startActivity(inChangePass);
                        } else {
                            showAlert(Message);
                        }

                    } else {
                        showToast("No Records Found..");
                        finish();
                    }

                } catch (Exception e) {
                    Log.e("Login:Exception", e.getMessage());
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("Login:Failure", t.toString());
            }
        });
    }

    private void loginApi() {
        final ProgressDialog mProgressDialog = new ProgressDialog(ChangePasswordScreen.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing()) mProgressDialog.show();

//        TeacherSchoolsApiClient.BASE_URL = TeacherUtil_SharedPreference.getBaseUrlFromSP(TeacherSignInScreen.this);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);

        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChangePasswordScreen.this);
        confirm_password = TeacherUtil_SharedPreference.getPasswordFromSP(ChangePasswordScreen.this);

        confirm_password = confirm_etPassword1.getText().toString();
        number = TeacherUtil_SharedPreference.getMobileNumberFromSP(ChangePasswordScreen.this);

        String IMEINumber = TeacherUtil_SharedPreference.getIMEI(ChangePasswordScreen.this);


        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetStaffList(number, confirm_password, "", androidId);
        Call<JsonArray> call = apiService.getStaffDetail(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();


                Log.d("Login:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Login:Res", response.body().toString());


                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);

                        String Status = jsonObject.getString("Status");
                        String Message = jsonObject.getString("Message");

                        TeacherUtil_SharedPreference.putLoginMessage(ChangePasswordScreen.this, Message);

                        String strlogin = "";
                        TeacherSchoolsModel schoolmodel = null;
                        listschooldetails = new ArrayList<>();
                        if (Status.equals("1")) {
                            AddCouponPoints.addPoints(ChangePasswordScreen.this, Util_Common.LOGIN_POINTS);


                            String Grouphead = jsonObject.getString("idGroupHead");
                            String Principal = jsonObject.getString("isPrincipal");
                            String Staff = jsonObject.getString("isStaff");
                            String Admin = jsonObject.getString("isAdmin");
                            String Parent = jsonObject.getString("isParent");

                            TeacherUtil_SharedPreference.putPrincipal(ChangePasswordScreen.this, Principal);
                            TeacherUtil_SharedPreference.putStaff(ChangePasswordScreen.this, Staff);
                            TeacherUtil_SharedPreference.putGroupHead(ChangePasswordScreen.this, Grouphead);
                            TeacherUtil_SharedPreference.putParent(ChangePasswordScreen.this, Parent);
                            TeacherUtil_SharedPreference.putAdmin(ChangePasswordScreen.this, Admin);

                            TeacherSignInScreen.par = Parent;
                            TeacherSignInScreen.princi = Principal;
                            TeacherSignInScreen.staf = Staff;
                            TeacherSignInScreen.grouphead = Grouphead;
                            TeacherSignInScreen.admin = Admin;

                            TeacherUtil_Common.maxEmergencyvoicecount = jsonObject.getInt("MaxEmergencyVoiceDuration");
                            TeacherUtil_Common.maxGeneralvoicecount = jsonObject.getInt("MaxGeneralVoiceDuartion");
                            TeacherUtil_Common.maxHWVoiceDuration = jsonObject.getInt("MaxHWVoiceDuration");
                            TeacherUtil_Common.maxGeneralSMSCount = jsonObject.getInt("MaxGeneralSMSCount");
                            TeacherUtil_Common.maxHomeWorkSMSCount = jsonObject.getInt("MaxHomeWorkSMSCount");


                            if (Principal.equals("true") && Parent.equals("true")) {

                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetails = jSONArray1.getJSONObject(i);

                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolNameRegional"), jsonObjectdetails.getString("SchoolID"), jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo"), jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"), jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"), jsonObjectdetails.getInt("school_type"), jsonObjectdetails.getInt("biometricEnable"),jsonObjectdetails.getBoolean("allowVideoDownload"));
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
                                        TeacherUtil_SharedPreference.putShoolID(ChangePasswordScreen.this, strSchoolId);
                                        TeacherUtil_SharedPreference.putStaffID(ChangePasswordScreen.this, strStaffId);


                                        String logo = jsonObjectdetails.getString("SchoolLogo");
                                        TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                    }
                                }


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }

                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);


                                TeacherUtil_SharedPreference.listSchoolDetails(ChangePasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(ChangePasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(ChangePasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);


                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                inHome.putExtra("TeacherSchoolsModel", schoolmodel);
                                inHome.putExtra("schoollist", schoolNamelist);
                                inHome.putExtra("list", listschooldetails);


                                startActivity(inHome);
                                finish();


                            } else if (Staff.equals("true") && Parent.equals("true")) {

//                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
//                                childApi();


                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
                                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                listschooldetails.add(schoolmodel);


                                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");
                                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                                TeacherUtil_Common.Principal_staffId = strStaffId1;
                                TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                Util_Common.isSchoolType = jsonObjectdetailsStaff.getInt("school_type");
                                TeacherUtil_Common.isBioMetricEnable = jsonObjectdetailsStaff.getInt("biometricEnable");
                                TeacherUtil_Common.isVideoDownload = jsonObjectdetailsStaff.getBoolean("allowVideoDownload");
                                TeacherUtil_SharedPreference.putShoolID(ChangePasswordScreen.this, strSchoolId);
                                TeacherUtil_SharedPreference.putStaffID(ChangePasswordScreen.this, strStaffId1);

                                TeacherUtil_SharedPreference.putSchoolName(ChangePasswordScreen.this, schoolname);
                                TeacherUtil_SharedPreference.putStaffAddress(ChangePasswordScreen.this, schooladdress);


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);


                                TeacherUtil_SharedPreference.listSchoolDetails(ChangePasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(ChangePasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(ChangePasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);


                                inHome.putExtra("SCHOOL_ID & Staff_ID", strSchoolId + " " + strStaffId1);
                                inHome.putExtra("schoolname", schoolname);
                                inHome.putExtra("Staff_ID1", strStaffId1);
                                inHome.putExtra("schooladdress", schooladdress);
                                inHome.putExtra("TeacherSchoolsModel", schoolmodel);
                                inHome.putExtra("list", listschooldetails);
                                strlogin = LOGIN_TYPE_TEACHER;

                                startActivity(inHome);
                                finish();


                            } else if (Admin.equals("true") && Parent.equals("true")) {


                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
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
                                        TeacherUtil_SharedPreference.putShoolID(ChangePasswordScreen.this, strSchoolId);
                                        TeacherUtil_SharedPreference.putStaffID(ChangePasswordScreen.this, strStaffId);
                                    }

                                }


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);


                                TeacherUtil_SharedPreference.listSchoolDetails(ChangePasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(ChangePasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(ChangePasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);


                                inHome.putExtra("list", listschooldetails);
                                strlogin = LOGIN_TYPE_ADMIN;


                                startActivity(inHome);
                                finish();

                            } else if (Grouphead.equals("true") && Parent.equals("true")) {


                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"), jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo"), jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"), jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }

                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);

                                pubStArrChildList.addAll(arrChildList);

                                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                TeacherUtil_SharedPreference.listSchoolDetails(ChangePasswordScreen.this, listschooldetails, "listSchoolDetails");
                                TeacherUtil_SharedPreference.schoollist(ChangePasswordScreen.this, schoolNamelist, "schoollist");
                                TeacherUtil_SharedPreference.schoolmodel(ChangePasswordScreen.this, schoolmodel, "schoolmodel");

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);


                                inHome.putExtra("list", listschooldetails);
                                strlogin = LOGIN_TYPE_HEAD;


                                startActivity(inHome);
                                finish();


                            } else if (Principal.equals("true")) {
                                JSONArray jSONArray = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    JSONObject jsonObjectdetails = jSONArray.getJSONObject(i);

                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolNameRegional"), jsonObjectdetails.getString("SchoolID"), jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo"), jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"), jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"), jsonObjectdetails.getInt("school_type"), jsonObjectdetails.getInt("biometricEnable"),jsonObjectdetails.getBoolean("allowVideoDownload"));
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
                                        TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);
                                    }
                                }


                                Log.d("valuelist", String.valueOf(listschooldetails.size()));
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                strlogin = LOGIN_TYPE_PRINCIPAL;
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("TeacherSchoolsModel", schoolmodel);
                                i.putExtra("schoollist", schoolNamelist);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                finish();
//                                }
                            } else if (Grouphead.equals("true")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolNameRegional"), jsonObjectdetailsgrouphead.getString("SchoolID"), jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo"), jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"), jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"), jsonObjectdetailsgrouphead.getInt("school_type"), jsonObjectdetailsgrouphead.getInt("biometricEnable"),jsonObjectdetailsgrouphead.getBoolean("allowVideoDownload"));
                                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                                    listschooldetails.add(schoolmodel);
                                }
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                                TeacherUtil_SharedPreference.isAdminFromSP(ChangePasswordScreen.this);


                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_HEAD;
                                finish();
//
                            } else if (Staff.equals("true")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
                                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                                listschooldetails.add(schoolmodel);


                                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                                TeacherUtil_SharedPreference.putSchoolLogo(ChangePasswordScreen.this, logo);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");

                                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                                TeacherUtil_Common.Principal_staffId = strStaffId1;
                                TeacherUtil_Common.Principal_SchoolId = strSchoolId;
                                Util_Common.isSchoolType = jsonObjectdetailsStaff.getInt("school_type");
                                TeacherUtil_Common.isBioMetricEnable = jsonObjectdetailsStaff.getInt("biometricEnable");
                                TeacherUtil_Common.isVideoDownload = jsonObjectdetailsStaff.getBoolean("allowVideoDownload");
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
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
                            } else if (Admin.equals("true")) {
                                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                                for (int i = 0; i < jSONArray1.length(); i++) {
                                    JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(i);
                                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolNameRegional"), jsonObjectdetailsStaff.getString("SchoolID"), jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo"), jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"), jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"), jsonObjectdetailsStaff.getInt("school_type"), jsonObjectdetailsStaff.getInt("biometricEnable"),jsonObjectdetailsStaff.getBoolean("allowVideoDownload"));
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


                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);
                                Intent i = new Intent(ChangePasswordScreen.this, Teacher_AA_Test.class);
                                i.putExtra("list", listschooldetails);
                                startActivity(i);
                                strlogin = LOGIN_TYPE_ADMIN;
                                finish();

                            } else if (Parent.equals("true")) {
                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);


                                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");

                                Profiles childList;
                                Log.d("json length", String.valueOf(jSONArray.length()));
                                for (int i = 0; i < jSONArray.length(); i++) {
                                    jsonObject = jSONArray.getJSONObject(i);
                                    childList = new Profiles(jsonObject.getString("ChildName"), jsonObject.getString("ChildID"), jsonObject.getString("RollNumber"), jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID"), jsonObject.getString("SchoolName"), jsonObject.getString("SchoolNameRegional"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"), jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"), jsonObject.getString("classId"), jsonObject.getString("sectionId"));
                                    arrChildList.add(childList);
                                }


                                arrayList = new ArrayList<>();
                                arrayList.addAll(arrChildList);
                                pubStArrChildList.addAll(arrChildList);

                                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, true);

                                Intent inHome = new Intent(ChangePasswordScreen.this, ChildrenScreen.class);
                                inHome.putExtra("CHILD_LIST", arrChildList);
                                startActivity(inHome);
                                finish();

                            }


                            TeacherUtil_SharedPreference.putLoggedInAsToSP(ChangePasswordScreen.this, strlogin);
                        } else if ((Status).equalsIgnoreCase("RESET")) {
                            Util_SharedPreference.putForget(ChangePasswordScreen.this, "forget");

                            TeacherUtil_SharedPreference.putStaffLoginInfoToSP(ChangePasswordScreen.this, number, confirm_password, false);
                            Intent inChangePass = new Intent(ChangePasswordScreen.this, TeacherChangePassword.class);
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
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                finish();
                Log.d("Login:Failure", t.toString());
            }
        });
    }

    private void showAlert(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangePasswordScreen.this);
        alertDialog.setTitle(getResources().getString(R.string.alert));
        alertDialog.setMessage(message);
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


    private void showToast(String message) {

        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}
