package com.vs.schoolmessenger.OTP;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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
import androidx.annotation.Nullable;
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
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.ChildrenScreen;
import com.vs.schoolmessenger.activity.TeacherSignInScreen;
import com.vs.schoolmessenger.activity.Teacher_AA_Test;
import com.vs.schoolmessenger.adapter.ForgetPaswordDialinNumbers;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.Profiles;
import com.vs.schoolmessenger.model.TeacherProfiles;
import com.vs.schoolmessenger.model.TeacherSchoolsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.SqliteDB;
import com.vs.schoolmessenger.util.TeacherUtil_Common;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.fcmservices.MyFirebaseMessagingService.pubStArrChildList;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_ADMIN;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_PRINCIPAL;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_TEACHER;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.listschooldetails;
import static com.vs.schoolmessenger.util.TeacherUtil_Common.loginJsonObject;
import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getMobileNum;
import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getMobileNumberFromSP;
import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getPasswordFromSP;

/**
 * Created by voicesnap on 4/26/2018.
 */

public class AutoReadOTPCallNumberScreen extends AppCompatActivity implements SmsBroadcastReceiver.OTPReceiveListener {
    Button btnSubmitOtp;
    EditText txtOtp;
    TextView btnResendOTP, lblNote;
    Boolean condition = false;
    String otp_Timer;
    public static Handler handler = new Handler();
    Runnable yourRunnable;

    String note_message = "";
    String DialNumbers = "";
    String Type = "";
    String forgot = "";

    RecyclerView recycleNumbers;
    Button btnBackToLogin;

    SmsBroadcastReceiver mSmsBroadcastReceiver;
    private int RESOLVE_HINT = 2;
    IntentFilter intentFilter;
    JSONObject jsonObject;

    String Grouphead,Principal,Staff,Admin,Parent;

    String strMobile, strPassword, strmobilenumberlength, num;
    ArrayList<String> loginTypeList = new ArrayList<>();
    TeacherProfiles staffInfo;

    public static String  princi, staf, grouphead, admin;

    SqliteDB myDb;
    ArrayList<Profiles> arrayList;

    ArrayList<Profiles> arrChildList = new ArrayList<>();
    ArrayList<String> schoolNamelist = new ArrayList<>();


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        overridePendingTransition(R.anim.enter, R.anim.exit);
        setContentView(R.layout.otp_call_number_screen);


//        AppSignatureHelper appSignatureHashHelper = new AppSignatureHelper(this);
//        // This code requires one time to get Hash keys do comment and share key
//        Log.d("HashKey: ", appSignatureHashHelper.getAppSignatures().get(0));



        startSMSListener();

        DialNumbers = TeacherUtil_SharedPreference.getDialNumbers(AutoReadOTPCallNumberScreen.this);
        recycleNumbers = (RecyclerView) findViewById(R.id.recycleNumbers);


        jsonObject = loginJsonObject;
        Log.d("Login",jsonObject.toString());

         Grouphead = TeacherUtil_SharedPreference.getGroupHead(AutoReadOTPCallNumberScreen.this);
         Principal = TeacherUtil_SharedPreference.getPrincipal(AutoReadOTPCallNumberScreen.this);
         Staff = TeacherUtil_SharedPreference.getStaff(AutoReadOTPCallNumberScreen.this);
         Admin = TeacherUtil_SharedPreference.getAdmin(AutoReadOTPCallNumberScreen.this);
         Parent = TeacherUtil_SharedPreference.getParent(AutoReadOTPCallNumberScreen.this);


        if (!DialNumbers.equals("")) {
            final Dialog dialog = new Dialog(AutoReadOTPCallNumberScreen.this);
            String[] separated = DialNumbers.split(",");
            ForgetPaswordDialinNumbers mAdapter = new ForgetPaswordDialinNumbers(Arrays.asList(separated), AutoReadOTPCallNumberScreen.this, dialog);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recycleNumbers.setLayoutManager(mLayoutManager);
            recycleNumbers.setItemAnimator(new DefaultItemAnimator());
            recycleNumbers.setAdapter(mAdapter);
            recycleNumbers.getRecycledViewPool().setMaxRecycledViews(0, 80);
        }

        String note = TeacherUtil_SharedPreference.getNoteMessage(AutoReadOTPCallNumberScreen.this);
        TeacherUtil_SharedPreference.putOTPNum(AutoReadOTPCallNumberScreen.this, "1");
        otp_Timer = TeacherUtil_SharedPreference.getOTpTimer(AutoReadOTPCallNumberScreen.this);
        otpHandler(otp_Timer);

        btnBackToLogin = (Button) findViewById(R.id.btnBackToLogin);

        btnBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stophandler();
                Intent intent = new Intent(AutoReadOTPCallNumberScreen.this, TeacherSignInScreen.class);
                startActivity(intent);
               // finish();
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
                TeacherUtil_SharedPreference.putOTPNum(AutoReadOTPCallNumberScreen.this, "");
                if (!txtOtp.getText().toString().equals("")) {
                    verifyOTP();

                } else {
                    String msg = getResources().getString(R.string.enter_your_otp);
                    showAlert(msg);
                }
            }
        });


    }

    private void MobileNumberApi() {
        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(AutoReadOTPCallNumberScreen.this);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        final String mobilenumber = TeacherUtil_SharedPreference.getMobileNum(AutoReadOTPCallNumberScreen.this);
        TeacherUtil_SharedPreference.putMobileNum(AutoReadOTPCallNumberScreen.this, mobilenumber);

        String CountryID=TeacherUtil_SharedPreference.getCountryID(AutoReadOTPCallNumberScreen.this);
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty("MobileNumber",mobilenumber);
        jsonObject.addProperty("CountryID",CountryID);
        Log.d("Req",jsonObject.toString());

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        // Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePassword(mobilenumber);
        Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePasswordByCountryID(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, retrofit2.Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        btnResendOTP.setVisibility(View.GONE);
                        otpHandler(otp_Timer);

                        JSONArray js = new JSONArray(response.body().toString());
                        if (js.length() > 0) {
                            JSONObject jsonObject = js.getJSONObject(0);

                            String numberExists = jsonObject.getString("isNumberExists");
                            String passwordUpdated = jsonObject.getString("isPasswordUpdated");
                            String OTPSent = jsonObject.getString("OTPSent");
                            String OTP = jsonObject.getString("OTP");
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");
                            if(OTPSent.equals("1")){
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

    private void startSMSListener() {
        try {
            mSmsBroadcastReceiver = new SmsBroadcastReceiver();
            mSmsBroadcastReceiver.setOTPListener(this);

             intentFilter = new IntentFilter();
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

//        if (mSmsBroadcastReceiver != null) {
//            unregisterReceiver(mSmsBroadcastReceiver);
//        }
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


    private void stophandler() {
        handler.removeCallbacks(yourRunnable);
    }

    private void verifyOTP() {

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(AutoReadOTPCallNumberScreen.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String otp = txtOtp.getText().toString();
        String number = getMobileNum(AutoReadOTPCallNumberScreen.this);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("OTP", otp);
        jsonObject.addProperty("MobileNumber", number);
        Log.d("Req",jsonObject.toString());

        Call<JsonArray> call = apiService.ValidateOTP(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
                    if (response.code() == 200 || response.code() == 201) {
                        Log.d("Response", response.body().toString());

                        JSONArray js = new JSONArray(response.body().toString());
                        for (int i = 0; i < js.length(); i++) {
                            JSONObject jsonObject = js.getJSONObject(0);
                            String Status = jsonObject.getString("Status");
                            String Message = jsonObject.getString("Message");

                            if (Status.equals("1")) {
                                 stophandler();
                                 goToDashBoard();

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

    private void goToDashBoard() {

        strMobile = getMobileNumberFromSP(AutoReadOTPCallNumberScreen.this);
        strPassword = getPasswordFromSP(AutoReadOTPCallNumberScreen.this);

        Log.d("mobile_pass",strMobile+strPassword);

        String strlogin = "";
        TeacherSchoolsModel schoolmodel = null;
        listschooldetails = new ArrayList<>();
        try {
            if (Principal.equals("true") && Parent.equals("true")) {

                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                for (int i = 0; i < jSONArray1.length(); i++) {
                    JSONObject jsonObjectdetails = jSONArray1.getJSONObject(i);

                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolID"),
                            jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo")
                            , jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"), jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"));
                    Log.d("value1", jsonObjectdetails.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);
                    schoolNamelist.add(jsonObjectdetails.getString("SchoolName"));
                    if (listschooldetails.size() == 1) {
                        String strSchoolId = jsonObjectdetails.getString("SchoolID");
                        String strStaffId = jsonObjectdetails.getString("StaffID");
                        TeacherUtil_Common.Principal_staffId = strStaffId;
                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                        TeacherUtil_SharedPreference.putShoolID(AutoReadOTPCallNumberScreen.this, strSchoolId);
                        TeacherUtil_SharedPreference.putStaffID(AutoReadOTPCallNumberScreen.this, strStaffId);


                        String logo = jsonObjectdetails.getString("SchoolLogo");
                        TeacherUtil_SharedPreference.putSchoolLogo(AutoReadOTPCallNumberScreen.this, logo);
                    }
                }

                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                Profiles childList;
                for (int i = 0; i < jSONArray.length(); i++) {
                    jsonObject = jSONArray.getJSONObject(i);
                    childList = new Profiles(jsonObject.getString("ChildName"),
                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                    arrChildList.add(childList);
                }

                arrayList = new ArrayList<>();
                arrayList.addAll(arrChildList);
                myDb = new SqliteDB(AutoReadOTPCallNumberScreen.this);
                if (myDb.checkChildDetails()) {
                    myDb.deleteChildDetails();
                }
                myDb.addChildDetails((ArrayList<Profiles>) arrChildList, AutoReadOTPCallNumberScreen.this);
                pubStArrChildList.addAll(arrChildList);

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);


                TeacherUtil_SharedPreference.listSchoolDetails(AutoReadOTPCallNumberScreen.this, listschooldetails, "listSchoolDetails");
                TeacherUtil_SharedPreference.schoollist(AutoReadOTPCallNumberScreen.this, schoolNamelist, "schoollist");
                TeacherUtil_SharedPreference.schoolmodel(AutoReadOTPCallNumberScreen.this, schoolmodel, "schoolmodel");

                Intent inHome = new Intent(AutoReadOTPCallNumberScreen.this, ChildrenScreen.class);
                inHome.putExtra("CHILD_LIST", arrChildList);


                strlogin = LOGIN_TYPE_PRINCIPAL;
                inHome.putExtra("TeacherSchoolsModel", schoolmodel);
                inHome.putExtra("schoollist", schoolNamelist);
                inHome.putExtra("list", listschooldetails);

                startActivity(inHome);
                finish();

            } else if (Staff.equals("true") && Parent.equals("true")) {

                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                        jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                        , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true,
                        jsonObjectdetailsStaff.getString("isBooksEnabled"),
                        jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"));
                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                listschooldetails.add(schoolmodel);


                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                TeacherUtil_SharedPreference.putSchoolLogo(AutoReadOTPCallNumberScreen.this, logo);
                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");
                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                TeacherUtil_Common.Principal_staffId = strStaffId1;
                TeacherUtil_Common.Principal_SchoolId = strSchoolId;


                TeacherUtil_SharedPreference.putShoolID(AutoReadOTPCallNumberScreen.this, strSchoolId);
                TeacherUtil_SharedPreference.putStaffID(AutoReadOTPCallNumberScreen.this, strStaffId1);
                TeacherUtil_SharedPreference.putSchoolName(AutoReadOTPCallNumberScreen.this, schoolname);
                TeacherUtil_SharedPreference.putStaffAddress(AutoReadOTPCallNumberScreen.this, schooladdress);


                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                Profiles childList;
                Log.d("json length", jSONArray.length() + "");
                for (int i = 0; i < jSONArray.length(); i++) {
                    jsonObject = jSONArray.getJSONObject(i);
                    childList = new Profiles(jsonObject.getString("ChildName"),
                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                    arrChildList.add(childList);
                }


                arrayList = new ArrayList<>();
                arrayList.addAll(arrChildList);
                myDb = new SqliteDB(AutoReadOTPCallNumberScreen.this);
                if (myDb.checkChildDetails()) {
                    myDb.deleteChildDetails();
                }
                myDb.addChildDetails((ArrayList<Profiles>) arrChildList, AutoReadOTPCallNumberScreen.this);
                pubStArrChildList.addAll(arrChildList);

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);

                TeacherUtil_SharedPreference.listSchoolDetails(AutoReadOTPCallNumberScreen.this, listschooldetails, "listSchoolDetails");
                TeacherUtil_SharedPreference.schoollist(AutoReadOTPCallNumberScreen.this, schoolNamelist, "schoollist");
                TeacherUtil_SharedPreference.schoolmodel(AutoReadOTPCallNumberScreen.this, schoolmodel, "schoolmodel");

                Intent inHome = new Intent(AutoReadOTPCallNumberScreen.this, ChildrenScreen.class);
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
                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending"));
                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);

                    if (listschooldetails.size() == 1) {
                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                        TeacherUtil_Common.Principal_staffId = strStaffId;
                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;


                        TeacherUtil_SharedPreference.putShoolID(AutoReadOTPCallNumberScreen.this, strSchoolId);
                        TeacherUtil_SharedPreference.putStaffID(AutoReadOTPCallNumberScreen.this, strStaffId);
                    }

                }

                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                Profiles childList;
                Log.d("json length", jSONArray.length() + "");
                for (int i = 0; i < jSONArray.length(); i++) {
                    jsonObject = jSONArray.getJSONObject(i);
                    childList = new Profiles(jsonObject.getString("ChildName"),
                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                    arrChildList.add(childList);
                }


                arrayList = new ArrayList<>();
                arrayList.addAll(arrChildList);
                myDb = new SqliteDB(AutoReadOTPCallNumberScreen.this);
                if (myDb.checkChildDetails()) {
                    myDb.deleteChildDetails();
                }
                myDb.addChildDetails((ArrayList<Profiles>) arrChildList, AutoReadOTPCallNumberScreen.this);
                pubStArrChildList.addAll(arrChildList);

                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);


                TeacherUtil_SharedPreference.listSchoolDetails(AutoReadOTPCallNumberScreen.this, listschooldetails, "listSchoolDetails");
                TeacherUtil_SharedPreference.schoollist(AutoReadOTPCallNumberScreen.this, schoolNamelist, "schoollist");
                TeacherUtil_SharedPreference.schoolmodel(AutoReadOTPCallNumberScreen.this, schoolmodel, "schoolmodel");

                Intent inHome = new Intent(AutoReadOTPCallNumberScreen.this, ChildrenScreen.class);
                inHome.putExtra("CHILD_LIST", arrChildList);
                inHome.putExtra("list", listschooldetails);
                strlogin = LOGIN_TYPE_ADMIN;
                startActivity(inHome);
                finish();

            } else if (Grouphead.equals("true") && Parent.equals("true")) {

                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                for (int i = 0; i < jSONArray1.length(); i++) {
                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolID"),
                            jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo")
                            , jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"),
                            jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending"));
                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);
                }

                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                Profiles childList;
                Log.d("json length", jSONArray.length() + "");
                for (int i = 0; i < jSONArray.length(); i++) {
                    jsonObject = jSONArray.getJSONObject(i);
                    childList = new Profiles(jsonObject.getString("ChildName"),
                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                    arrChildList.add(childList);
                }

                arrayList = new ArrayList<>();
                arrayList.addAll(arrChildList);
                myDb = new SqliteDB(AutoReadOTPCallNumberScreen.this);
                if (myDb.checkChildDetails()) {
                    myDb.deleteChildDetails();
                }
                myDb.addChildDetails((ArrayList<Profiles>) arrChildList, AutoReadOTPCallNumberScreen.this);
                pubStArrChildList.addAll(arrChildList);

                // Util_SharedPreference.putParentLoginInfoToSP(TeacherSignInScreen.this, strMobile, strPassword, true);
                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);


                TeacherUtil_SharedPreference.listSchoolDetails(AutoReadOTPCallNumberScreen.this, listschooldetails, "listSchoolDetails");
                TeacherUtil_SharedPreference.schoollist(AutoReadOTPCallNumberScreen.this, schoolNamelist, "schoollist");
                TeacherUtil_SharedPreference.schoolmodel(AutoReadOTPCallNumberScreen.this, schoolmodel, "schoolmodel");

                Intent inHome = new Intent(AutoReadOTPCallNumberScreen.this, ChildrenScreen.class);
                inHome.putExtra("CHILD_LIST", arrChildList);
                inHome.putExtra("list", listschooldetails);
                strlogin = LOGIN_TYPE_HEAD;
                startActivity(inHome);
                finish();


            } else if (Principal.equals("true")) {
                JSONArray jSONArray = jsonObject.getJSONArray("Details");
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jsonObjectdetails = jSONArray.getJSONObject(i);

                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetails.getString("SchoolName"), jsonObjectdetails.getString("SchoolID"),
                            jsonObjectdetails.getString("city"), jsonObjectdetails.getString("SchoolAddress"), jsonObjectdetails.getString("SchoolLogo")
                            , jsonObjectdetails.getString("StaffID"), jsonObjectdetails.getString("StaffName"), true, jsonObjectdetails.getString("isBooksEnabled"),
                            jsonObjectdetails.getString("OnlineBooksLink"), jsonObjectdetails.getString("is_payment_pending"));
                    Log.d("value1", jsonObjectdetails.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);
                    schoolNamelist.add(jsonObjectdetails.getString("SchoolName"));
                    if (listschooldetails.size() == 1) {
                        String strSchoolId = jsonObjectdetails.getString("SchoolID");
                        String strStaffId = jsonObjectdetails.getString("StaffID");
                        TeacherUtil_Common.Principal_staffId = strStaffId;
                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;


                        String logo = jsonObjectdetails.getString("SchoolLogo");
                        TeacherUtil_SharedPreference.putSchoolLogo(AutoReadOTPCallNumberScreen.this, logo);
                    }
                }

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);
                strlogin = LOGIN_TYPE_PRINCIPAL;
                Intent i = new Intent(AutoReadOTPCallNumberScreen.this, Teacher_AA_Test.class);
                i.putExtra("TeacherSchoolsModel", schoolmodel);
                i.putExtra("schoollist", schoolNamelist);
                i.putExtra("list", listschooldetails);
                startActivity(i);
                finish();

            } else if (Grouphead.equals("true")) {
                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                for (int i = 0; i < jSONArray1.length(); i++) {
                    JSONObject jsonObjectdetailsgrouphead = jSONArray1.getJSONObject(i);
                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsgrouphead.getString("SchoolName"), jsonObjectdetailsgrouphead.getString("SchoolID"),
                            jsonObjectdetailsgrouphead.getString("city"), jsonObjectdetailsgrouphead.getString("SchoolAddress"), jsonObjectdetailsgrouphead.getString("SchoolLogo")
                            , jsonObjectdetailsgrouphead.getString("StaffID"), jsonObjectdetailsgrouphead.getString("StaffName"), true, jsonObjectdetailsgrouphead.getString("isBooksEnabled"),
                            jsonObjectdetailsgrouphead.getString("OnlineBooksLink"), jsonObjectdetailsgrouphead.getString("is_payment_pending")
                    );
                    Log.d("value1", jsonObjectdetailsgrouphead.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);
                }
                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);

                Intent i = new Intent(AutoReadOTPCallNumberScreen.this, Teacher_AA_Test.class);
                //  startActivity(new Intent(TeacherSignInScreen.this, Teacher_AA_Test.class));
                TeacherUtil_SharedPreference.isAdminFromSP(AutoReadOTPCallNumberScreen.this);
                i.putExtra("list", listschooldetails);
                startActivity(i);
                strlogin = LOGIN_TYPE_HEAD;
                finish();

            } else if (Staff.equals("true")) {
                JSONArray jSONArray1 = jsonObject.getJSONArray("Details");
                JSONObject jsonObjectdetailsStaff = jSONArray1.getJSONObject(0);
                schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                        jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                        , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                        jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending")
                );
                Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                listschooldetails.add(schoolmodel);


                String logo = jsonObjectdetailsStaff.getString("SchoolLogo");
                TeacherUtil_SharedPreference.putSchoolLogo(AutoReadOTPCallNumberScreen.this, logo);

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);
                String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                String strStaffId1 = jsonObjectdetailsStaff.getString("StaffID");

                String schoolname = jsonObjectdetailsStaff.getString("SchoolName");
                String schooladdress = jsonObjectdetailsStaff.getString("SchoolAddress");

                TeacherUtil_Common.Principal_staffId = strStaffId1;
                TeacherUtil_Common.Principal_SchoolId = strSchoolId;


                Intent i = new Intent(AutoReadOTPCallNumberScreen.this, Teacher_AA_Test.class);
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
                    schoolmodel = new TeacherSchoolsModel(jsonObjectdetailsStaff.getString("SchoolName"), jsonObjectdetailsStaff.getString("SchoolID"),
                            jsonObjectdetailsStaff.getString("city"), jsonObjectdetailsStaff.getString("SchoolAddress"), jsonObjectdetailsStaff.getString("SchoolLogo")
                            , jsonObjectdetailsStaff.getString("StaffID"), jsonObjectdetailsStaff.getString("StaffName"), true, jsonObjectdetailsStaff.getString("isBooksEnabled"),
                            jsonObjectdetailsStaff.getString("OnlineBooksLink"), jsonObjectdetailsStaff.getString("is_payment_pending")
                    );
                    Log.d("value1", jsonObjectdetailsStaff.getString("SchoolName"));
                    listschooldetails.add(schoolmodel);

                    if (listschooldetails.size() == 1) {
                        String strSchoolId = jsonObjectdetailsStaff.getString("SchoolID");
                        String strStaffId = jsonObjectdetailsStaff.getString("StaffID");
                        TeacherUtil_Common.Principal_staffId = strStaffId;
                        TeacherUtil_Common.Principal_SchoolId = strSchoolId;

                    }

                }


                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);
                Intent i = new Intent(AutoReadOTPCallNumberScreen.this, Teacher_AA_Test.class);
                i.putExtra("list", listschooldetails);

                startActivity(i);
                strlogin = LOGIN_TYPE_ADMIN;
                finish();

            } else if (Parent.equals("true")) {

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);
                JSONArray jSONArray = jsonObject.getJSONArray("ChildDetails");
                Profiles childList;
                Log.d("json length", jSONArray.length() + "");
                for (int i = 0; i < jSONArray.length(); i++) {
                    jsonObject = jSONArray.getJSONObject(i);
                    childList = new Profiles(jsonObject.getString("ChildName"),
                            jsonObject.getString("ChildID"), jsonObject.getString("RollNumber")
                            , jsonObject.getString("StandardName"), jsonObject.getString("SectionName"), jsonObject.getString("SchoolID")
                            , jsonObject.getString("SchoolName"), jsonObject.getString("SchoolCity"), jsonObject.getString("SchoolLogoUrl"),
                            jsonObject.getString("isBooksEnabled"), jsonObject.getString("OnlineBooksLink"), jsonObject.getString("IsNotAllow"), jsonObject.getString("DisplayMessage"));
                    arrChildList.add(childList);
                }


                arrayList = new ArrayList<>();
                arrayList.addAll(arrChildList);
                myDb = new SqliteDB(AutoReadOTPCallNumberScreen.this);
                if (myDb.checkChildDetails()) {
                    myDb.deleteChildDetails();
                }
                myDb.addChildDetails((ArrayList<Profiles>) arrChildList, AutoReadOTPCallNumberScreen.this);
                pubStArrChildList.addAll(arrChildList);

                TeacherUtil_SharedPreference.putStaffLoginInfoToSP(AutoReadOTPCallNumberScreen.this, strMobile, strPassword, true);
                Intent inHome = new Intent(AutoReadOTPCallNumberScreen.this, ChildrenScreen.class);
                inHome.putExtra("CHILD_LIST", arrChildList);
                startActivity(inHome);
                finish();


            }

        }
        catch (Exception e){

        }

        TeacherUtil_SharedPreference.putLoggedInAsToSP(AutoReadOTPCallNumberScreen.this, strlogin);
    }

    private void showAlert(String strMsg) {
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(AutoReadOTPCallNumberScreen.this);

        alertDialog.setTitle(R.string.alert);

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



    private void alertShow() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AutoReadOTPCallNumberScreen.this);
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

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(AutoReadOTPCallNumberScreen.this);


        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String CountryID = TeacherUtil_SharedPreference.getCountryID(AutoReadOTPCallNumberScreen.this);
        String num = TeacherUtil_SharedPreference.getMobileNum(AutoReadOTPCallNumberScreen.this);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MobileNumber", num);
        jsonObject.addProperty("CountryID", CountryID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        //Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePassword(num);
        Call<JsonArray> call = apiService.CheckMobileNumberforUpdatePasswordByCountryID(jsonObject);

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("login:code-res", response.code() + " - " + response.toString());
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
        Log.d("OTP Received",otp);
        txtOtp.setText("");
        txtOtp.setText(otp);
        if (mSmsBroadcastReceiver != null) {
            unregisterReceiver(mSmsBroadcastReceiver);
            mSmsBroadcastReceiver = null;
        }
        verifyOTP();


    }

    @Override
    public void onOTPTimeOut() {

    }

    @Override
    public void onOTPReceivedError(String error) {

    }
}
