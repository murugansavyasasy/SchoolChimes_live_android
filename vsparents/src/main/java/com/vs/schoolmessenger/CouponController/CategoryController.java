package com.vs.schoolmessenger.CouponController;


import static com.vs.schoolmessenger.util.TeacherUtil_SharedPreference.getMobileNumberFromSPContext;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.vs.schoolmessenger.CouponModel.CouponCoin.PointsData;
import com.vs.schoolmessenger.CouponModel.CouponCoin.PointsResponse;
import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.CouponModel.CouponMenu.CouponMenuResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.CouponSummaryResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;
import com.vs.schoolmessenger.CouponModel.LogactiveapiResponse.LogActiveApiResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCoupon.ActivateCouponResponse;
import com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary.ActivateCouponSummary;
import com.vs.schoolmessenger.CouponModel.TicketActivateCouponSummary.ActivateCouponSummaryResponse;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummary;
import com.vs.schoolmessenger.CouponModel.TicketCouponSummary.TicketSummaryResponse;
import com.vs.schoolmessenger.util.TeacherUtil_SharedPreference;
import com.vs.schoolmessenger.util.Util_Common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryController {
    private Context context;
    private CouponAPIServiceInterface defaultApiService;
    private CouponAPIServiceInterface dynamicApiService;
    private CouponAPIServiceInterface reportingURLService;

    String MOBILE_NUMBER;
    private static final String PARTNER_NAME = "savyasasy";
    private static final String API_KEY = "33adab6a67a9eee6e72be49acfb6c100";
    private static final int USER_TYPE = 1;


    public CategoryController(Context context) {
        this.context = context;

        MOBILE_NUMBER = getMobileNumberFromSPContext(context);

        Log.d("MOBILE_NUMBER", MOBILE_NUMBER);
        defaultApiService = CouponRetrofitNetworkCall.getClient().create(CouponAPIServiceInterface.class);

        String baseURL = TeacherUtil_SharedPreference.getBaseUrl(context);
        String reportURL = TeacherUtil_SharedPreference.getReportURL(context);
        dynamicApiService = CouponRetrofitNetworkCall.getClientWithBaseUrl(baseURL).create(CouponAPIServiceInterface.class);
        reportingURLService = CouponRetrofitNetworkCall.getClientWithBaseUrl(reportURL).create(CouponAPIServiceInterface.class);
    }


    public void fetchCoinDetails(final PointsCouponCallback callback) {
        Call<PointsResponse> call = reportingURLService.getPointsCoupons(
                Util_Common.USER_TYPE,
                MOBILE_NUMBER
        );
        Log.d("CategoryController", "Request URL: " + call.request().url().toString());

        call.enqueue(new Callback<PointsResponse>() {
            @Override
            public void onResponse(Call<PointsResponse> call, Response<PointsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PointsData pointsData = response.body().getData();
                    Log.d("CategoryController", "Raw Response: " + new Gson().toJson(response.body()));

                    if(response.body().getStatus() == 1) {
                        if (pointsData != null) {
                            Log.d("CategoryController", "Points Earned: " + pointsData.getPointsEarned());
                            Log.d("CategoryController", "Points Spent: " + pointsData.getPointsSpent());
                            Log.d("CategoryController", "Points Remaining: " + pointsData.getPointsRemaining());
                            callback.onSuccess(pointsData);

                        } else {
                            Log.e("CategoryController", "PointsData is null");
                            callback.onFailure("No points data found.");
                        }
                    }
                    else {
                        callback.onFailure("No points data found.");
                    }
                } else {
                    Log.e("CategoryController", "API failed or body is null");
                    callback.onFailure("Failed to fetch coin details.");
                }
            }

            @Override
            public void onFailure(Call<PointsResponse> call, Throwable t) {
                Log.e("CategoryController", "API Error: " + t.getMessage(), t);
                callback.onFailure("Network Error");
            }
        });
    }


    public interface PointsCouponCallback {
        void onSuccess(PointsData pointsData);

        void onFailure(String errorMessage);
    }


    public void fetchCategories(final CategoryCallback callback) {
        Call<CouponMenuResponse> call = defaultApiService.getCategories(
                PARTNER_NAME,
                API_KEY
        );

        call.enqueue(new Callback<CouponMenuResponse>() {
            @Override
            public void onResponse(Call<CouponMenuResponse> call, Response<CouponMenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getCategories());
                } else {
                    callback.onFailure("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<CouponMenuResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                callback.onFailure("Network Error");
            }
        });
    }

    public interface CategoryCallback {
        void onSuccess(List<Category> categories);

        void onFailure(String errorMessage);
    }

    public void fetchCouponSummary(final CouponSummaryCallback callback) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("mobile_no", "91" + MOBILE_NUMBER);

        Call<CouponSummaryResponse> call = defaultApiService.getCoupons(
                PARTNER_NAME,
                API_KEY,
                requestBody
        );

        call.enqueue(new Callback<CouponSummaryResponse>() {
            @Override
            public void onResponse(Call<CouponSummaryResponse> call, Response<CouponSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getCampaigns().getData());
                } else {
                    callback.onFailure("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<CouponSummaryResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                callback.onFailure("Network Error");
            }
        });
    }

    public interface CouponSummaryCallback {
        void onSuccess(List<Summary> campaigns);

        void onFailure(String errorMessage);
    }


    public void fetchCategoryCouponSummary(String categoryId, final CategoryCouponSummaryCallback callback) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("mobile_no", "91" + MOBILE_NUMBER);
        requestBody.put("category_id", categoryId);

        Call<CouponSummaryResponse> call = defaultApiService.getCategoryCoupons(
                PARTNER_NAME,
                API_KEY,
                requestBody
        );

        call.enqueue(new Callback<CouponSummaryResponse>() {
            @Override
            public void onResponse(Call<CouponSummaryResponse> call, Response<CouponSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body().getData().getCampaigns().getData());
                } else {
                    callback.onFailure("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<CouponSummaryResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                callback.onFailure("Network Error");
            }
        });
    }

    public interface CategoryCouponSummaryCallback {
        void onSuccess(List<Summary> campaigns);

        void onFailure(String errorMessage);
    }


    public void fetchActivateCoupondata(String sourceLink, final ActivateCouponCallback callback) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("source_link", sourceLink);
        requestBody.put("mobile_no", MOBILE_NUMBER);

        Call<ActivateCouponSummaryResponse> call = defaultApiService.getActivateCouponsDetails(
                PARTNER_NAME,
                API_KEY,
                requestBody
        );

        call.enqueue(new Callback<ActivateCouponSummaryResponse>() {
            @Override
            public void onResponse(Call<ActivateCouponSummaryResponse> call, Response<ActivateCouponSummaryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ActivateCouponSummary coupon = response.body().getData().getCampaign_details();
                    if (coupon != null) {
                        callback.onSuccess(Collections.singletonList(coupon));
                    } else {
                        callback.onFailure("No campaign data found");
                    }
                } else {
                    callback.onFailure("Failed to load data");
                }
            }

            @Override
            public void onFailure(Call<ActivateCouponSummaryResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                callback.onFailure("Network Error");
            }
        });
    }

    public interface ActivateCouponCallback {
        void onSuccess(List<ActivateCouponSummary> campaign_details);

        void onFailure(String errorMessage);
    }


    public void activateCouponWithSource(String sourceLink, final CouponActivationCallback callback) {
        Log.d("CouponActivation", "Activating coupon with source link: " + sourceLink);

        HashMap<String, String> body = new HashMap<>();
        body.put("source_link", sourceLink);
        body.put("mobile_no", "91" + MOBILE_NUMBER);

        Log.d("CouponActivation", "Request body: " + body.toString());

        Call<ActivateCouponResponse> call = defaultApiService.getactivateCoupon(
                PARTNER_NAME,
                API_KEY,
                body
        );

        call.enqueue(new Callback<ActivateCouponResponse>() {
            @Override
            public void onResponse(Call<ActivateCouponResponse> call, Response<ActivateCouponResponse> response) {
                Log.d("CouponActivation", "onResponse called");
                Log.d("CouponActivation", "Response code: " + response.code());

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CouponActivation", "Activation successful: " + response.body().toString());
                    callback.onSuccess(response.body());
                } else {
                    Log.e("CouponActivation", "Activation failed: Response not successful or body is null");
                    callback.onFailure("Activation failed");
                }
            }

            @Override
            public void onFailure(Call<ActivateCouponResponse> call, Throwable t) {
                Log.e("CouponActivation", "Network error: " + t.getMessage(), t);
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }


    public interface CouponActivationCallback {
        void onSuccess(ActivateCouponResponse response);

        void onFailure(String errorMessage);
    }

    public void logCouponActivation(String sourceLink, final GenericCallback callback) {
        Log.d("CouponActivation", "Starting coupon activation process...");
        Log.d("CouponActivation", "Source Link: " + sourceLink);

        int user_type = Util_Common.USER_TYPE;
        String mobile_number = MOBILE_NUMBER;
        int coupon_id = 0;

        Log.d("CouponActivation", "User Type: " + user_type);
        Log.d("CouponActivation", "Mobile Number: " + mobile_number);
        Log.d("CouponActivation", "Coupon ID: " + coupon_id);

        Map<String, Object> body = new HashMap<>();
        body.put("user_type", user_type);
        body.put("mobile_number", mobile_number);
        body.put("coupon_id", coupon_id);
        body.put("coupon_link", sourceLink);

        String jsonBody = new Gson().toJson(body);

        Log.d("CouponActivationBodyResponse", "Request Body: " + jsonBody);


        Call<LogActiveApiResponse> call = dynamicApiService.getlogactiveresponse(body);

        Log.d("CouponActivation", "Retrofit call created. Executing...");

        call.enqueue(new Callback<LogActiveApiResponse>() {
            @Override
            public void onResponse(Call<LogActiveApiResponse> call, Response<LogActiveApiResponse> response) {
                Log.d("CouponActivation", "Received response from API");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("CouponActivation", "Activation successful. Response: " + new Gson().toJson(response.body()));
                    callback.onSuccess(response.body());
                } else {
                    Log.w("CouponActivation", "Activation failed. Response code: " + response.code());
                    callback.onFailure("Activation failed");
                }
            }

            @Override
            public void onFailure(Call<LogActiveApiResponse> call, Throwable t) {
                Log.e("CouponActivation", "Network error during activation: " + t.getMessage(), t);
                callback.onFailure("Network error: " + t.getMessage());
            }
        });
    }


    public interface GenericCallback {
        void onSuccess(LogActiveApiResponse response);

        void onFailure(String errorMessage);
    }


    public void fetchTicketCouponSummary(String couponStatus, final TicketCouponSummaryCallback callback) {
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("mobile_no", "91" + MOBILE_NUMBER);
        requestBody.put("coupon_status", couponStatus);
        Log.d("requestBody", String.valueOf(requestBody));
        Call<TicketSummaryResponse> call = defaultApiService.getTicketCoupons(
                PARTNER_NAME,
                API_KEY,
                requestBody
        );

        call.enqueue(new Callback<TicketSummaryResponse>() {
            @Override
            public void onResponse(Call<TicketSummaryResponse> call, Response<TicketSummaryResponse> response) {
                Log.d("TicketSummaryResponse", "Raw Response: " + new Gson().toJson(response.body()));


                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isStatus()) {
                        callback.onSuccess(response.body().getData().getCoupon_list().getData());
                    }
                } else {
                    callback.onFailure("Failed to load data");
                }

            }

            @Override
            public void onFailure(Call<TicketSummaryResponse> call, Throwable t) {
                Log.e("API_ERROR", t.getMessage());
                callback.onFailure("Network Error");
            }
        });
    }

    public interface TicketCouponSummaryCallback {
        void onSuccess(List<TicketSummary> coupon_list);

        void onFailure(String errorMessage);
    }
}
