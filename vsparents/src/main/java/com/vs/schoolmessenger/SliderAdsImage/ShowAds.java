package com.vs.schoolmessenger.SliderAdsImage;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vs.schoolmessenger.interfaces.TeacherMessengerApiInterface;
import com.vs.schoolmessenger.model.adsModel;
import com.vs.schoolmessenger.rest.TeacherSchoolsApiClient;
import com.vs.schoolmessenger.util.Constants;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_SharedPreference;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ss.com.bannerslider.Slider;

public class ShowAds {
    public static ArrayList<adsModel> adsList = new ArrayList<adsModel>();
    public static Handler handler = new Handler();
    public static Runnable myRunnable;
    public static int i = 0;
    public static String redirectURL = "";
    public static String advertisementName = "";
    public static void getAds(Activity activity, ImageView image, Slider slider, String Menu_Type) {
        stop();
        Log.d("Menu_ID", Constants.Menu_ID);
        String baseURL = TeacherUtil_SharedPreference.getReportURL(activity);
        TeacherSchoolsApiClient.changeApiBaseUrl(baseURL);
        String ChildId = Util_SharedPreference.getChildIdFromSP(activity);
        String schoolId = Util_SharedPreference.getSchoolIdFromSP(activity);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("MemberId", ChildId);
        jsonObject.addProperty("MemberType", "student");
        jsonObject.addProperty("MenuId", Constants.Menu_ID);
        jsonObject.addProperty("SchoolId", schoolId);
        final ProgressDialog mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        if (!activity.isFinishing())
            mProgressDialog.show();
        Log.d("Request", jsonObject.toString());
        TeacherMessengerApiInterface apiService = TeacherSchoolsApiClient.getClient().create(TeacherMessengerApiInterface.class);
        Call<JsonArray> call = apiService.getAds(jsonObject);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.d("GetMenuDetails:code", response.code() + " - " + response.toString());
                if (response.code() == 200 || response.code() == 201)
                    Log.d("GetMenuDetails:Res", response.body().toString());

                try {
                    JSONArray js = new JSONArray(response.body().toString());
                    if (js.length() > 0) {
                        JSONObject jsonObject = js.getJSONObject(0);
                        String status = jsonObject.getString("Status");
                        String message = jsonObject.getString("Message");

                        if (status.equals("1")) {
                            JSONArray data = jsonObject.getJSONArray("data");
                            adsModel ads;
                            adsList.clear();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject object = data.getJSONObject(i);
                                ads = new adsModel(object.getInt("id"), object.getString("advertisementName"), object.getString("contentUrl"), object.getString("redirectUrl"));
                                adsList.add(ads);
                            }
                            i = 0;
                            rotateAds(activity, image);

//                                Log.d("adsList", String.valueOf(adsList.size()));
//                                slider.setAdapter(new MainSliderAdapter(activity, adsList));
//                                slider.setInterval(3000);
//                                slider.setOnSlideClickListener(new OnSlideClickListener() {
//                                    @Override
//                                    public void onSlideClick(int position) {
//                                x        Log.d("redirectURl ",adsList.get(position).getRedirectURL());
//
//                                        if(!adsList.get(position).getRedirectURL().equals("")) {
//                                            Intent intent = new Intent(activity, SpecificAdDetails.class);
//                                            intent.putExtra("AdredirectURl",adsList.get(position).getRedirectURL());
//                                            activity.startActivity(intent);
//                                        }
//                                    }
//                                });

                        }
                    }

                } catch (Exception e) {
                    Log.e("VersionCheck:Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();
                Log.d("VersionCheck:Failure", t.toString());
            }
        });
    }

    public static void start() {
        handler.postDelayed(myRunnable, 0);
    }

    public static void stop() {
        handler.removeCallbacks(myRunnable);
    }

//    public static void restart(int j) {
//        handler.removeCallbacks(myRunnable);
//        handler.postDelayed(myRunnable, delay * j);
//    }

    private static void rotateAds(Activity activity, ImageView image) {
         String adTimeInterval = TeacherUtil_SharedPreference.getAdTimeInterval(activity);
         long delay = Long.parseLong(adTimeInterval);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!redirectURL.equals("")) {
                    Intent intent = new Intent(activity, SpecificAdDetails.class);
                    intent.putExtra("AdredirectURl", redirectURL);
                    intent.putExtra("advertisementName", advertisementName);
                    activity.startActivity(intent);
                }
            }
        });
        myRunnable = new Runnable() {
            public void run() {
                redirectURL = adsList.get(i).getRedirectURL();
                advertisementName = adsList.get(i).getAdName();
                Glide.with(activity.getApplicationContext()).load(adsList.get(i).getAdImage()).into(image);
                i++;
                if (i > adsList.size() - 1) {
                    i = 0;
                }
                handler.postDelayed(this, delay);
            }
        };
        handler.postDelayed(myRunnable, 0);
    }
}


