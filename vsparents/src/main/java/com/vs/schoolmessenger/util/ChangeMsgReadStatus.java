package com.vs.schoolmessenger.util;

import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_ASSIGNMENT;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_IMAGE;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_PDF;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VIDEO;
import static com.vs.schoolmessenger.util.Util_UrlMethods.MSG_TYPE_VOICE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.activity.SendToVoiceSpecificSection;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.interfaces.OnRefreshListener;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by voicesnap on 5/15/2017.
 */

public class ChangeMsgReadStatus {

    public static void changeReadStatus(Activity activity, String msgID, String msgType, String msgDate,String isNewVersion,Boolean is_Archive, final OnRefreshListener onRefreshListener) {
        final ProgressDialog mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(activity.getString(R.string.Loading));
        mProgressDialog.setCancelable(false);

        String baseURL=TeacherUtil_SharedPreference.getBaseUrl(activity);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);


        String strChildID = Util_SharedPreference.getChildIdFromSP(activity);
        String strSchoolID = Util_SharedPreference.getSchoolIdFromSP(activity);

        Log.d("Msg:ID-Child-Sch", msgID + " - " + strChildID + " - " + strSchoolID);

        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        JsonObject jsonReqArray = Util_JsonRequest.getJsonArray_ReadStatusUpdate(msgID, strChildID, strSchoolID, msgType, msgDate);


        Call<JsonArray> call;
        if(isNewVersion.equals("1")&&is_Archive){
            call = apiService.ReadStatusUpdatenew_Archive(jsonReqArray);
        }
        else {
            call = apiService.ReadStatusUpdatenew(jsonReqArray);
        }
        call.enqueue(new Callback<JsonArray>() {

            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("Msg:Code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("Msg:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String strStatus = jsonObject.getString("Status");

                        if ((strStatus.toLowerCase()).equals("1")) {
                            onRefreshListener.onRefreshItem();

                            String activity_name = Util_Common.READ_MESSAGE_POINTS;
                            if(msgType.equals(MSG_TYPE_IMAGE)){
                                activity_name = Util_Common.READ_MESSAGE_POINTS;
                            }
                            else if(msgType.equals(MSG_TYPE_VOICE)){
                                activity_name = Util_Common.LISTEN_VOICE_POINTS;
                            }
                            else if(msgType.equals(MSG_TYPE_ASSIGNMENT)){
                                activity_name = Util_Common.VIEW_ASSIGNMENT_POINTS;
                            }
                            else if(msgType.equals(MSG_TYPE_VIDEO)){
                                activity_name = Util_Common.VIEW_VIDEO_POINTS;
                            }
                            else if(msgType.equals(MSG_TYPE_PDF)){
                                activity_name = Util_Common.VIEW_IMAGE_PDF_POINTS;
                            }

                            AddCouponPoints.addPoints(activity, activity_name);
                        }
                    } else {
                    }

                } catch (Exception e) {
                    Log.e("Msg:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
//                showToast("Error! Try Again");
                Log.d("Msg:Failure", t.toString());
            }
        });
    }
}
