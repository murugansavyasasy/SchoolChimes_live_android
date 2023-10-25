package com.vs.schoolmessenger.fcmservices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_JsonRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences pref = context.getSharedPreferences(Config.SHARED_PREF, 0);
        String existing_token = pref.getString("regId", "");
        Log.d("receiver_token_stored",existing_token);
        // Request a new token and update it on your server
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(token -> {
                        // Handle the new token, e.g., store it or send it to your server
                        Log.d("receiver_token",token);
                        SharedPreferences preference = context.getSharedPreferences(Config.SHARED_PREF, 0);
                        SharedPreferences.Editor editor = preference.edit();
                        editor.putString("regId", token);
                        editor.commit();

                        String mobNumber = TeacherUtil_SharedPreference.getMobileNumber(context);
                        if(!mobNumber.equals("") && !token.equals(existing_token)) {
                            updateServerWithNewToken(token, context);
                        }

                    });
    }

    private void updateServerWithNewToken(String token,Context context) {

        String baseURL= TeacherUtil_SharedPreference.getBaseUrl(context);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        String mobNumber = TeacherUtil_SharedPreference.getMobileNumber(context);
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_DeviceToken(mobNumber, token, "Android");
        Call<JsonArray> call = apiService.DeviceTokennew(jsonReqArray);
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Reciever_token:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");
                        String strMsg = jsonObject.getString("Message");


                    } else {
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
            }
        });
    }
}