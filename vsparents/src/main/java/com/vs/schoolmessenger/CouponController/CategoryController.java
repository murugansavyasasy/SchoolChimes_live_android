package com.vs.schoolmessenger.CouponController;


import android.content.Context;
import android.util.Log;

import com.vs.schoolmessenger.CouponModel.CouponMenu.Category;
import com.vs.schoolmessenger.CouponModel.CouponMenu.CouponMenuResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.CouponSummaryResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.Summary;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryController {
    private CouponAPIServiceInterface apiService;
    private Context context;

    public CategoryController(Context context) {
        this.context = context;
        apiService = CoupoRetrofitNetworkCall.getClient().create(CouponAPIServiceInterface.class);
    }

    public void fetchCategories(final CategoryCallback callback) {
        Call<CouponMenuResponse> call = apiService.getCategories(
                "voicesnaps",
                "b9634e2c3aa9b6fdc392527645c43871"

        );

        call.enqueue(new Callback<CouponMenuResponse>() {
            @Override
            public void onResponse(Call<CouponMenuResponse> call, Response<CouponMenuResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> categories = response.body().getData().getCategories();
                    callback.onSuccess(categories);
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
        try {

            HashMap<String, String> requestBody = new HashMap<>();
            requestBody.put("Mobile_no", "8610786768");

            Call<CouponSummaryResponse> call = apiService.getCoupons(
                    "voicesnaps",
                    "b9634e2c3aa9b6fdc392527645c43871",
                    requestBody
            );

            call.enqueue(new Callback<CouponSummaryResponse>() {
                @Override
                public void onResponse(Call<CouponSummaryResponse> call, Response<CouponSummaryResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<Summary> campaigns = response.body().getData().getCampaigns().getData(); // Updated
                        callback.onSuccess(campaigns);
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
        } catch (Exception e) {
            Log.e("REQUEST_BODY_ERROR", e.getMessage());
            callback.onFailure("Error creating request body");
        }
    }




    public interface CouponSummaryCallback {
        void onSuccess(List<Summary> campaigns);
        void onFailure(String errorMessage);
    }



}

