package com.vs.schoolmessenger.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.adapter.LocationsHistoryAdapter;
import com.vs.schoolmessenger.app.LocaleHelper;
import com.vs.schoolmessenger.interfaces.LocationClick;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.StaffBiometricLocationRes;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewExistingLocations extends AppCompatActivity {

    public LocationsHistoryAdapter mAdapter;
    public List<StaffBiometricLocationRes.BiometricLoationData> locationsList = new ArrayList<StaffBiometricLocationRes.BiometricLoationData>();
    String SchoolID = "", StaffID = "";
    RecyclerView recyleLocations;
    ConstraintLayout constParent;
    TextView lblNoRecords;
    private PopupWindow editPopup;

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
        setContentView(R.layout.locations_history_popup);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.teacher_actionbar_home);

        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acTitle)).setText(R.string.Locations);
        ((TextView) getSupportActionBar().getCustomView().findViewById(R.id.actBar_acSubTitle)).setText("");
        ((ImageView) getSupportActionBar().getCustomView().findViewById(R.id.actBarDate_ivBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        SchoolID = getIntent().getExtras().getString("SCHOOL_ID", "");
        StaffID = getIntent().getExtras().getString("STAFF_ID", "");

        ImageView gifImage = (ImageView) findViewById(R.id.gifImage);
        TextView lblTitle = (TextView) findViewById(R.id.lblTitle);
        recyleLocations = (RecyclerView) findViewById(R.id.recyleLocations);
        constParent = (ConstraintLayout) findViewById(R.id.constParent);
        lblNoRecords = (TextView) findViewById(R.id.lblNoRecords);

        Glide.with(this)
                .asGif()
                .load(R.drawable.map_location) // Replace with your GIF resource
                .into(gifImage);

        lblTitle.setTypeface(null, Typeface.BOLD);
        getExistingViewLocations();
    }

    private void getExistingViewLocations() {
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ViewExistingLocations.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<StaffBiometricLocationRes> call = apiService.getExistingViewLocations(SchoolID);
        call.enqueue(new Callback<StaffBiometricLocationRes>() {
            @Override
            public void onResponse(Call<StaffBiometricLocationRes> call, retrofit2.Response<StaffBiometricLocationRes> response) {
                try {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.d("locations:code-res", response.code() + " - " + response);
                    if (response.code() == 200 || response.code() == 201) {

                        Gson gson = new Gson();
                        String data = gson.toJson(response.body());
                        Log.d("locations_res", data);
                        Log.d("locations_res", response.body().toString());
                        locationsList.clear();
                        if (response.body().getStatus() == 1) {
                            recyleLocations.setVisibility(View.VISIBLE);
                            lblNoRecords.setVisibility(View.GONE);
                            locationsList = response.body().getData();
                            mAdapter = new LocationsHistoryAdapter(locationsList, ViewExistingLocations.this, new LocationClick() {
                                @Override
                                public void onItemClick(StaffBiometricLocationRes.BiometricLoationData item, Boolean isEdit) {
                                    if (isEdit) {
                                        editPopup(item);
                                    } else {
                                        deleteAlert(item);
                                    }
                                }
                            });
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ViewExistingLocations.this);
                            recyleLocations.setLayoutManager(mLayoutManager);
                            recyleLocations.setItemAnimator(new DefaultItemAnimator());
                            recyleLocations.setAdapter(mAdapter);
                            recyleLocations.getRecycledViewPool().setMaxRecycledViews(0, 80);
                            mAdapter.notifyDataSetChanged();
                        } else {
                            recyleLocations.setVisibility(View.GONE);
                            lblNoRecords.setVisibility(View.VISIBLE);
                            lblNoRecords.setText(response.body().getMessage());
                            lblNoRecords.setTypeface(null, Typeface.BOLD);

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    Log.e("Response Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<StaffBiometricLocationRes> call, Throwable t) {
                Log.e("Response Failure", t.getMessage());
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.check_internet), Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void editPopup(StaffBiometricLocationRes.BiometricLoationData item) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.edit_location_popup, null);
        editPopup = new PopupWindow(layout, android.app.ActionBar.LayoutParams.MATCH_PARENT, android.app.ActionBar.LayoutParams.MATCH_PARENT, true);
        editPopup.setContentView(layout);
        constParent.post(new Runnable() {
            public void run() {
                editPopup.showAtLocation(constParent, Gravity.CENTER, 0, 0);
            }
        });

        EditText txtLocationName = (EditText) layout.findViewById(R.id.txtLocationName);
        EditText txtDistance = (EditText) layout.findViewById(R.id.txtDistance);
        TextView btnUpdate = (TextView) layout.findViewById(R.id.btnUpdate);
        TextView btnCancel = (TextView) layout.findViewById(R.id.btnCancel);
        txtLocationName.setText(item.getLocation());
        txtDistance.setText(item.getDistance() + R.string.Meters);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPopup.dismiss();
                String distance = txtDistance.getText().toString().replaceAll("[^0-9]", ""); // Keep only numbers
                Log.d("en_distance", distance);
                int distanceCheck = Integer.parseInt(distance);
                if (distanceCheck >= 10) {
                    Log.d("en_distance", "true");
                    updateLocationAPI(String.valueOf(distanceCheck), txtLocationName.getText().toString(), item.getId());
                } else {
                    Toast.makeText(ViewExistingLocations.this, R.string.Distance_should_be_minimum_Meters, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPopup.dismiss();
            }
        });
    }

    private void updateLocationAPI(String distance, String locationName, int id) {
        final ProgressDialog mProgressDialog = new ProgressDialog(ViewExistingLocations.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ViewExistingLocations.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!ViewExistingLocations.this.isFinishing()) mProgressDialog.show();

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("id", id);
        jsonObjectSchool.addProperty("location", locationName);
        jsonObjectSchool.addProperty("distance", distance);
        jsonObjectSchool.addProperty("userId", StaffID);
        Log.d("location_update_request", jsonObjectSchool.toString());
        Call<JsonObject> call = apiService.updateLocation(jsonObjectSchool);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Biometric:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Location:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 1) {
                            getExistingViewLocations();
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.e("Excep", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }

    private void deleteAlert(StaffBiometricLocationRes.BiometricLoationData item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ViewExistingLocations.this);
        alertDialog.setTitle(R.string.Delete_location);
        alertDialog.setMessage(R.string.Are_you_sure_you_want_delete_location);
        alertDialog.setNegativeButton(R.string.rb_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                removeLocationApi(item);
            }
        });
        alertDialog.setPositiveButton(R.string.btn_sign_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = alertDialog.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

    }

    private void removeLocationApi(StaffBiometricLocationRes.BiometricLoationData item) {
        final ProgressDialog mProgressDialog = new ProgressDialog(ViewExistingLocations.this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(getResources().getString(R.string.Loading));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(ViewExistingLocations.this);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        if (!ViewExistingLocations.this.isFinishing()) mProgressDialog.show();

        JsonObject jsonObjectSchool = new JsonObject();
        jsonObjectSchool.addProperty("instituteId", SchoolID);
        jsonObjectSchool.addProperty("locationId", item.getId());
        Log.d("location_remove_request", jsonObjectSchool.toString());
        Call<JsonObject> call = apiService.removeLocation(jsonObjectSchool);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();

                Log.d("Biometric:Code", response.code() + " - " + response);
                if (response.code() == 200 || response.code() == 201) {
                    Log.d("Location:Res", response.body().toString());
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        int status = jsonObject.getInt("status");
                        String message = jsonObject.getString("message");
                        if (status == 1) {
                            getExistingViewLocations();
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ViewExistingLocations.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.dismiss();
                        Log.e("Excep", String.valueOf(e));
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                if (mProgressDialog.isShowing()) mProgressDialog.dismiss();
            }
        });
    }
}