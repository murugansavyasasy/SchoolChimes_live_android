package com.vs.schoolmessenger.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.app.TeacherAppController;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_JsonRequest;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.vs.schoolmessenger.util.TeacherUtil_Common.LOGIN_TYPE_HEAD;


public class TeacherHomeScreen extends AppCompatActivity {
    NetworkImageView nivSchoolLogo;
    ImageLoader imageLoader;

    RelativeLayout rlText, rlVoice, rlImage, rlAttendance;
    TextView tvLoggedInAs, tvSchoolName, tvSchoolAddress;
    Button btnChange;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;
    public static final int MY_PERMISSIONS_REQUEST_RECORD_AUDIO = 2;

    private PopupWindow pHelpWindow;

    ArrayList<String> loginTypeList;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_activity_message_send_type_screen);



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.messages);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText(R.string.for_parents);

        nivSchoolLogo = (NetworkImageView) findViewById(R.id.home_nivSchoolLogo);
        imageLoader = TeacherAppController.getInstance().getImageLoader();

       nivSchoolLogo.setImageUrl(TeacherUtil_SharedPreference.getSchoolLogoUrlFromSP(TeacherHomeScreen.this), imageLoader);

        tvLoggedInAs = (TextView) findViewById(R.id.home_tvLoggedInAs);
        tvLoggedInAs.setText(TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherHomeScreen.this));

        tvSchoolName = (TextView) findViewById(R.id.home_tvSchoolName);
        tvSchoolName.setText(TeacherUtil_SharedPreference.getSchoolNameFromSP(TeacherHomeScreen.this));

        tvSchoolAddress = (TextView) findViewById(R.id.home_tvSchoolAddress);
        tvSchoolAddress.setText(TeacherUtil_SharedPreference.getShSchoolAddressFromSP(TeacherHomeScreen.this));

        btnChange = (Button) findViewById(R.id.home_btnChange);
        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLoginType();
            }
        });

        rlText = (RelativeLayout) findViewById(R.id.home_rlText);
        rlImage = (RelativeLayout) findViewById(R.id.home_rlImage);
        rlVoice = (RelativeLayout) findViewById(R.id.home_rlVoice);
        rlAttendance = (RelativeLayout) findViewById(R.id.home_rlAttendance);

        rlText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TeacherHomeScreen.this, TeacherTextMessage.class);
                startActivity(i);
            }
        });

        rlVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecordPermissionGranted())
                    if (isStoragePermissionGranted()) {
                        Intent i = new Intent(TeacherHomeScreen.this, TeacherRecordAudio.class);
                        startActivity(i);
                    }
            }
        });

        rlImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Intent i = new Intent(TeacherHomeScreen.this, TeacherTakePicture.class);
                    startActivity(i);
                }
            }
        });

        rlAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TeacherHomeScreen.this, TeacherAttendanceScreen.class);
                startActivity(i);
            }
        });



        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherHomeScreen.this).equals(LOGIN_TYPE_HEAD))
            rlAttendance.setVisibility(View.GONE);
        else rlAttendance.setVisibility(View.VISIBLE);

    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Write_Etnl_Permission", "Permission is granted");
                return true;
            } else {

                Log.v("Write_Etnl_Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Write_Etnl_Permission", "Permission is granted");
            return true;
        }
    }

    public boolean isRecordPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.RECORD_AUDIO)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Rec_Audio_Permission", "Permission is granted");
                return true;
            } else {

                Log.v("Rec_Audio_Permission", "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_RECORD_AUDIO);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Rec_Audio_Permission", "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Write_Etnl_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                }
                return;
            }

            case MY_PERMISSIONS_REQUEST_RECORD_AUDIO: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Rec_Audio_Permission", "Permission: " + permissions[0] + "was " + grantResults[0]);
                    //resume tasks needing this permission
                }
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_homeHelp:
                setupHelpPopUp();
                pHelpWindow.showAtLocation(rlText, Gravity.NO_GRAVITY, 0, 0);
                return true;

            case R.id.menu_homeChangePassword:
                startActivity(new Intent(TeacherHomeScreen.this, TeacherChangePassword.class));
                return true;

            case R.id.menu_homeLogout:
                TeacherUtil_SharedPreference.cleaStaffSharedPreference(TeacherHomeScreen.this);
                TeacherUtil_SharedPreference.clearStaffSharedPreference(TeacherHomeScreen.this);

                startActivity(new Intent(TeacherHomeScreen.this, TeacherSignInScreen.class));
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideKeyBoard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );


    }

    private void setupHelpPopUp() {

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.teacher_popup_help_txt, null);

        pHelpWindow = new PopupWindow(layout, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);
        pHelpWindow.setContentView(layout);

        ImageView ivClose = (ImageView) layout.findViewById(R.id.popupHelp_ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pHelpWindow.dismiss();
                hideKeyBoard();
            }
        });

        final EditText etmsg = (EditText) layout.findViewById(R.id.popupHelp_etMsg);



        final TextView tvTxtCount = (TextView) layout.findViewById(R.id.popupHelp_tvTxtCount);
        etmsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvTxtCount.setText("" + (460 - (s.length())));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TextView tvSend = (TextView) layout.findViewById(R.id.popupHelp_tvSend);
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMsg = etmsg.getText().toString().trim();

                if (strMsg.length() > 0)
                    helpAPI(strMsg);
                else
                    showToast(getResources().getString(R.string.enter_message));
            }
        });

        TextView tvClear = (TextView) layout.findViewById(R.id.popupHelp_tvClear);
        tvClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etmsg.setText("");
            }
        });


    }

    private void showToast(String msg) {
        Toast.makeText(TeacherHomeScreen.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void changeLoginType() {
        loginTypeList = new ArrayList<>();



        showLoginType();
    }

    private void showLoginType() {
        String[] LoginTypeArray = loginTypeList.toArray(new String[loginTypeList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherHomeScreen.this);
        AlertDialog alertDialog;
        builder.setTitle(R.string.alert);
        builder.setCancelable(false);
        builder.setSingleChoiceItems(LoginTypeArray, 0, null);
        builder.setPositiveButton(R.string.teacher_btn_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        String strLoginType = loginTypeList.get(selectedPosition);
                        Log.d("LOGIN_TYPE", strLoginType);

                        TeacherUtil_SharedPreference.putLoggedInAsToSP(TeacherHomeScreen.this, strLoginType);
                        tvLoggedInAs.setText(strLoginType);

                        if (TeacherUtil_SharedPreference.getLoginTypeFromSP(TeacherHomeScreen.this).equals(LOGIN_TYPE_HEAD))
                            rlAttendance.setVisibility(View.GONE);
                        else rlAttendance.setVisibility(View.VISIBLE);

                    }
                });
        builder.setNegativeButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.teacher_colorPrimaryDark));

    }

    private void helpAPI(String msg) {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!this.isFinishing())
            mProgressDialog.show();

        String mobNumber = TeacherUtil_SharedPreference.getMobileNumberFromSP(TeacherHomeScreen.this);
        Log.d("Help:Mob-Query", mobNumber + " - " + msg);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = TeacherUtil_JsonRequest.getJsonArray_GetHelp(mobNumber, msg);
        Call<JsonArray> call = apiService.GetHelp(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Help:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Help:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMessage = jsonObject.getString("Message");

                        showToast(strMessage);
                        if ((strStatus.toLowerCase()).equals("y")) {
                            if (pHelpWindow.isShowing()) {
                                pHelpWindow.dismiss();
                                hideKeyBoard();
                            }
                        }
                    } else {
                        showToast(getResources().getString(R.string.check_internet));
                    }

                } catch (Exception e) {
                    Log.e("Help:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                showToast(getResources().getString(R.string.check_internet));
                Log.d("Help:Failure", t.toString());
            }
        });
    }

}
