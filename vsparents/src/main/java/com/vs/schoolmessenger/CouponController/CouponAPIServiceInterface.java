package com.vs.schoolmessenger.CouponController;

import com.vs.schoolmessenger.CouponModel.CouponMenu.CouponMenuResponse;
import com.vs.schoolmessenger.CouponModel.CouponSummary.CouponSummaryResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.QueryMap;

public interface CouponAPIServiceInterface {
        @GET("get_category_list")
        Call<CouponMenuResponse> getCategories(
                @Header("Partner-Name") String parentName,
                @Header("api-key") String apiKey
        );

    @GET("get_campaigns")
    Call<CouponSummaryResponse> getCoupons(
            @Header("Partner-Name") String parentName,
            @Header("api-key") String apiKey,
            @QueryMap Map<String, String> params
    );


}


