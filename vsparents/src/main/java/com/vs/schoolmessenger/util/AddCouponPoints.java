package com.vs.schoolmessenger.util;

import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getMobileNumberFromSPContext;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCouponPoints {

    public static void addPoints(Activity activity, String activity_name) {

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(activity);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        String MOBILE_NUMBER = getMobileNumberFromSPContext(activity);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_addPoints(MOBILE_NUMBER, activity_name);

        Call<JsonObject> call;
        call = apiService.addPoints(jsonReqArray);

        call.enqueue(new Callback<JsonObject>() {

            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                Log.d("Msg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Msg:Res", response.body().toString());

                try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        String strStatus = jsonObject.getString("status");
                        String strMsg = jsonObject.getString("message");
                        Toast.makeText(activity, strMsg, Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Log.d("Ex", e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Msg:Failure", t.toString());
            }
        });
    }
}
